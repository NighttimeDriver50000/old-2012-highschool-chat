package nighttimedriver.chat.testing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import nighttimedriver.chat.LineInputEvent;
import nighttimedriver.chat.LineInputListener;
import nighttimedriver.chat.TerminalPane;
import nighttimedriver.chat.message.ExternalizableMessage;
import nighttimedriver.chat.message.Message;

public class ChatUITest extends JPanel {

	private static final long serialVersionUID = 1L;

	protected Socket socket;
	protected ObjectInputStream msgIn;
	protected ObjectOutputStream msgOut;
	protected Executor sendExecutor = Executors.newSingleThreadExecutor();

	protected final TerminalPane terminal;
	protected final JPanel controlPanel;
	protected final JTextField hostField;
	protected final JTextField nameField;

	public ChatUITest() {
		setLayout(new BorderLayout());

		terminal = new TerminalPane();
		terminal.addLineInputListener(new LineInputListener() {
			@Override
			public void lineInput(final LineInputEvent evt) {
				final Message msg = Message.create(evt.getLine(),
						nameField.getText());
				terminal.out.format("%s (@ %tc): %s%n", msg.getSender(), msg
						.getTimestamp().createCalendar(), msg.getBody());
				sendExecutor.execute(new SingleSender(msg));
			}
		});
		add(terminal, BorderLayout.CENTER);

		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

		final JCheckBox hostCheckBox = new JCheckBox("Serve as host");
		hostCheckBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				hostField.setText("");
				hostField.setEnabled(e.getStateChange() == ItemEvent.DESELECTED);
			}
		});
		controlPanel.add(hostCheckBox);

		final Box hostBox = Box.createHorizontalBox();
		hostBox.add(new JLabel("Host"));
		hostField = new JTextField();
		hostBox.add(hostField);
		hostBox.setMaximumSize(new Dimension(hostBox.getMaximumSize().width,
				hostField.getPreferredSize().height));
		controlPanel.add(hostBox);

		final Box portBox = Box.createHorizontalBox();
		portBox.add(new JLabel("Port"));
		final JSpinner portSpinner = new JSpinner(new SpinnerNumberModel(6447,
				0, 65535, 1));
		portBox.add(portSpinner);
		portBox.setMaximumSize(new Dimension(portBox.getMaximumSize().width,
				portSpinner.getPreferredSize().height));
		controlPanel.add(portBox);

		final Box nameBox = Box.createHorizontalBox();
		nameBox.add(new JLabel("Username"));
		nameField = new JTextField();
		nameBox.add(nameField);
		nameBox.setMaximumSize(new Dimension(nameBox.getMaximumSize().width,
				nameField.getPreferredSize().height));
		controlPanel.add(nameBox);

		final JButton startBtn = new JButton("Start");
		startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controlPanel.setVisible(false);
				revalidate();
				repaint();
				if (hostCheckBox.isSelected()) {
					new Thread(new Server((Integer) portSpinner.getValue()))
							.start();
				} else {
					new Thread(new Client(hostField.getText(),
							(Integer) portSpinner.getValue())).start();
				}
			}
		});
		controlPanel.add(startBtn);

		controlPanel.add(Box.createVerticalGlue());

		add(controlPanel, BorderLayout.WEST);
	}

	public class SingleSender implements Runnable {
		public final Message msg;

		public SingleSender(final Message msg) {
			this.msg = msg;
		}

		@Override
		public void run() {
			if (msgOut == null)
				return;
			try {
				msgOut.writeObject(new ExternalizableMessage(msg));
			} catch (IOException e) {
				e.printStackTrace();
				terminal.out.println("ERROR while sending message");
			}
		}
	}

	public class Client implements Runnable {
		public final String host;
		public final int port;

		public Client(final String host, final int port) {
			this.host = host;
			this.port = port;
		}

		public void run() {
			try {
				socket = new Socket(host, port);
			} catch (IOException e) {
				e.printStackTrace();
				terminal.out.println("ERROR while connecting");
				controlPanel.setVisible(true);
				revalidate();
				repaint();
				return;
			}
			receive();
		}
	}

	public class Server implements Runnable {
		public final int port;

		public Server(final int port) {
			this.port = port;
		}

		@Override
		public void run() {
			try {
				final ServerSocket serverSocket = new ServerSocket(port);
				terminal.out.format("LISTENING on port %d%n",
						serverSocket.getLocalPort());
				socket = serverSocket.accept();
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			} catch (IOException e) {
				e.printStackTrace();
				terminal.out.println("ERROR while initializing server");
				controlPanel.setVisible(true);
				revalidate();
				repaint();
				return;
			}
			receive();
		}
	}

	public void receive() {
		terminal.out.format("CONNECTED to %s:%d%n", socket.getInetAddress()
				.getHostName(), socket.getPort());
		try {
			msgOut = new ObjectOutputStream(socket.getOutputStream());
			msgIn = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			terminal.out.println("ERROR while initializing");
			controlPanel.setVisible(true);
			revalidate();
			repaint();
			return;
		}
		while (!Thread.interrupted()) {
			Message msg = null;
			try {
				msg = ((ExternalizableMessage) msgIn.readObject()).getMessage();
			} catch (final EOFException e) {
				break;
			} catch (IOException | ClassNotFoundException e) {
				msg = ExternalizableMessage.DEFAULT_MSG;
			}
			terminal.out.format("%s (@ %tc): %s%n", msg.getSender(), msg
					.getTimestamp().createCalendar(), msg.getBody());
		}
		try {
			msgIn.close();
			msgOut.close();
			terminal.out.println("DISCONNECTED");
		} catch (IOException e) {
			e.printStackTrace();
			terminal.out.println("ERROR while disconnecting");
		}
		socket = null;
		msgIn = null;
		msgOut = null;
		
		controlPanel.setVisible(true);
		revalidate();
		repaint();
	}

	public static void main(final String[] args) {
		final JFrame frame = new JFrame("Chat UI Test");
		frame.setSize(854, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		final ChatUITest cuit = new ChatUITest();
		frame.add(cuit);
		frame.setVisible(true);
	}

}
