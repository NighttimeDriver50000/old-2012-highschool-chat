package nighttimedriver.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nighttimedriver.chat.TerminalPane;
import nighttimedriver.chat.Util;
import nighttimedriver.chat.message.ExternalizableMessage;
import nighttimedriver.chat.message.Message;

public class Server implements Runnable {

	private final TerminalPane disp;
	private final ServerSocket serverSocket;
	private final List<Client> clients = new LinkedList<Client>();
	private Thread thread = null;
	private final ExecutorService messageSender = Executors
			.newSingleThreadExecutor();

	public Server(final TerminalPane pane) throws IOException {
		this(pane, Util.PORT);
	}

	public Server(final TerminalPane pane, final int port) throws IOException {
		disp = pane;
		serverSocket = new ServerSocket(port);
	}

	@Override
	public void run() {
		if (thread == null && thread.isAlive())
			thread = Thread.currentThread();
		while (!(Thread.interrupted() || serverSocket.isClosed())) {
			try {
				final Client c = new Client(this, serverSocket.accept());
				c.start();
				synchronized (clients) {
					clients.add(c);
				}
				c.setRegistered();
			} catch (final SocketException e) {
				if (Thread.currentThread().isInterrupted()
						|| serverSocket.isClosed())
					sendServerMsgToAll("Server is stopping...");
				else
					e.printStackTrace();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		try {
			if (!serverSocket.isClosed())
				serverSocket.close();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		disp.out.println("Server stopped.");
	}

	private void sendServerMsgToAll(final String msgBody) {
		sendServerMsgToAll(Message.create(msgBody, null));
	}

	private void sendServerMsgToAll(final Message msg) {
		messageSender.execute(new Runnable() {
			@Override
			public void run() {
				synchronized (clients) {
					for (final Client client : clients) {
						client.send(msg);
					}
				}
			}
		});
	}

	void sendToAll(final Message msg, final Client src) {
		if (!(clients.contains(src) && src.getUsername()
				.equals(msg.getSender())))
			return;
		messageSender.execute(new Runnable() {
			@Override
			public void run() {
				synchronized (clients) {
					for (final Client client : clients) {
						if (client != src)
							client.send(msg);
					}
				}
			}
		});
	}

	public static final int STOPPED = 0;
	public static final int STOPPING = 1;
	public static final int NOT_STOPPING = 2;

	public int stop() {
		messageSender.shutdown();
		if (thread != null)
			thread.interrupt();
		try {
			serverSocket.close();
		} catch (final IOException e) {
			final Socket s = new Socket();
			try {
				s.connect(new InetSocketAddress(serverSocket.getInetAddress(),
						serverSocket.getLocalPort()), 1000);
			} catch (final IOException e1) {
				e.printStackTrace();
				e1.printStackTrace();
			} finally {
				try {
					s.close();
				} catch (final IOException e1) {
					if (!serverSocket.isClosed())
						e1.printStackTrace();
				}
			}
		}
		if (!messageSender.isTerminated())
			messageSender.shutdownNow();
		else if (!thread.isAlive())
			return STOPPED;
		if (thread == null && !serverSocket.isClosed())
			return NOT_STOPPING;
		return STOPPING;
	}

	void remove(final Client client) {
		if (clients.contains(client))
			synchronized (clients) {
				clients.remove(client);
			}
	}

	void removeAsync(final Client client) {
		messageSender.execute(new Runnable() {
			@Override
			public void run() {
				remove(client);
			}
		});
	}

	boolean registerUsername(final String name) {
		synchronized (clients) {
			for (final Client client : clients) {
				if (client.getUsername().equals(name))
					return false;
			}
		}
		return true;
	}

	// the following is for chat history

	private long historyCounter = 0;
	private Charset charset; // TODO set
	private String dirname; // TODO

	{
		// TODO read historyCounter from file
	}

	void storeMsgInHist(Message msg) throws IOException {
		long index = ++historyCounter;
		try (ObjectOutputStream out = new ObjectOutputStream(
				Files.newOutputStream(Paths
						.get("chat-data", "servers", dirname, "chat-history",
								new String(new byte[] { (byte) (index >>> 48),
										(byte) (index >>> 32),
										(byte) (index >>> 16), (byte) index },
										charset))));) {
			out.writeObject(new ExternalizableMessage(msg));
		}
	}

	Message retrievePastMsg(long index) throws IOException,
			ClassNotFoundException {
		try (ObjectInputStream in = new ObjectInputStream(
				Files.newInputStream(Paths
						.get("chat-data", "servers", dirname, "chat-history",
								new String(new byte[] { (byte) (index >>> 48),
										(byte) (index >>> 32),
										(byte) (index >>> 16), (byte) index },
										charset))));) {
			return ((ExternalizableMessage) in.readObject()).getMessage();
		}
	}

	Message[] retrievePastMsgs(long startIndex, long endIndex) {
		ArrayList<Message> msgs = new ArrayList<Message>((int) (endIndex
				- startIndex + 1));
		for (long i = startIndex; i <= endIndex; i++)
			addPastMsg(msgs, i);
		return (Message[]) msgs.toArray();
	}

	Message[] retrievePastMsgs(long startIndex) {
		ArrayList<Message> msgs = new ArrayList<Message>(
				(int) (historyCounter - startIndex));
		for (long i = startIndex; i < historyCounter; i++)
			addPastMsg(msgs, i);
		return (Message[]) msgs.toArray();
	}

	private void addPastMsg(List<Message> msgList, long index) {
		try {
			msgList.add(retrievePastMsg(index));
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
			msgList.add(ExternalizableMessage.DEFAULT_MSG);
		}
	}

	// the following is for diagnostic history

	private ObjectOutputStream diagnosticHistOut = new ObjectOutputStream(
			Files.newOutputStream(Paths.get("chat-data", "servers", dirname,
					"diagnostic-history", "message-record.ser"),
					StandardOpenOption.APPEND, StandardOpenOption.CREATE));

	void storeMsgInDiagnostics(ExternalizableMessage msg) throws IOException {
		diagnosticHistOut.writeObject(msg);
	}

}
