package nighttimedriver.chat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import nighttimedriver.chat.Util;
import nighttimedriver.chat.message.ExternalizableMessage;
import nighttimedriver.chat.message.Message;
import nighttimedriver.chat.message.Timestamp;

public class Client extends Thread {

	private Server server;
	private final Socket socket;
	private final ObjectOutputStream out;
	private final ObjectInputStream in;
	private final ExecutorService messageSender = Executors
			.newSingleThreadExecutor();
	private String username = null;
	private Timestamp timeRegistered = null;
	private boolean disconnect = false;

	public Client(final Server server, final Socket socket) throws IOException {
		super(Util.format("Client-%s:%d", socket.getInetAddress()
				.getHostAddress(), socket.getPort()));
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		try {
			username = waitForUsername();
		} catch (final InterruptedException e) {
			disconnect();
			return;
		}
		while (!Thread.interrupted()) {
			// TODO
		}
		disconnect();
	}

	void setRegistered() {
		if (timeRegistered == null)
			timeRegistered = Timestamp.create();
	}

	private void disconnect() {
		if (!disconnect)
			try {
				disconnect(20, TimeUnit.SECONDS);
			} catch (final IOException e) {
				e.printStackTrace();
			}
	}

	void disconnect(final long timeout, final TimeUnit unit) throws IOException {
		disconnect = true;
		server.removeAsync(this);
		send(Message.create("You are being disconnected.", null));
		if (Thread.currentThread() != this)
			interrupt();
		messageSender.shutdown();
		try {
			messageSender.awaitTermination(timeout, unit);
		} catch (final InterruptedException e) {
		}
		socket.close();
	}

	void disconnectQuickly(final long timeout, final TimeUnit unit)
			throws IOException {
		disconnect = true;
		server.removeAsync(this);
		if (Thread.currentThread() != this)
			interrupt();
		messageSender.shutdownNow();
		try {
			messageSender.awaitTermination(timeout, unit);
		} catch (final InterruptedException e) {
		}
		socket.close();
	}

	void forceDisconnect() throws IOException {
		disconnect = true;
		server.removeAsync(this);
		interrupt();
		messageSender.shutdownNow();
		socket.close();
	}

	Timestamp getTimeRegistered() {
		return timeRegistered;
	}

	private String waitForUsername() throws InterruptedException {
		while (!Thread.interrupted()) {
			String strIn = "";
			try {
				strIn = in.readUTF();
			} catch (final IOException e) {
				e.printStackTrace();
			}
			final String name = ExternalizableMessage.getElementInner(strIn,
					"namereq");
			if (name != null && server.registerUsername(name)) {
			}
		}
		throw new InterruptedException();
	}

	void send(final Message msg) {
		try {
			messageSender.execute(new Runnable() {
				@Override
				public void run() {
					try {
						out.writeObject(msg);
					} catch (final IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (final RejectedExecutionException e) {
			e.printStackTrace();
		}
	}

	ExecutorService getMsgSender() {
		return messageSender;
	}

	public String getUsername() {
		return username;
	}

}
