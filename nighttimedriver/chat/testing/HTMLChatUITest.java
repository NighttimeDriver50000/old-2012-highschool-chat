package nighttimedriver.chat.testing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.text.Element;

import nighttimedriver.chat.HTMLTerminalPane;
import nighttimedriver.chat.LineInputEvent;
import nighttimedriver.chat.LineInputListener;
import nighttimedriver.chat.MapUtil;
import nighttimedriver.chat.TerminalPane;
import nighttimedriver.chat.message.ExternalizableMessage;
import nighttimedriver.chat.message.Message;

public class HTMLChatUITest extends JPanel {

	public final boolean doLoadStyle;

	private static final long serialVersionUID = 1L;

	public static final String MESSAGE = "<span class='user'>%1$s</span>"
			+ "<span class='not-user'>"
			+ "<span class='time'>[%2$tFT%2$tT]</span>"
			+ "<span class='body'>%3$s</span></span>";

	protected Socket socket;
	protected ObjectInputStream msgIn;
	protected ObjectOutputStream msgOut;
	protected Executor sendExecutor = Executors.newSingleThreadExecutor();

	protected final TerminalPane terminal;
	protected final JPanel controlPanel;
	protected final JTextField hostField;
	protected final JTextField nameField;

	protected final Object terminalOutLock = new Object();

	public HTMLChatUITest() {
		this(false);
	}

	public HTMLChatUITest(boolean dls) {
		doLoadStyle = dls;

		setLayout(new BorderLayout());

		terminal = new HTMLTerminalPane();
		terminal.addLineInputListener(new LineInputListener() {
			@Override
			public void lineInput(final LineInputEvent evt) {
				final Message msg = Message.create(evt.getLine(),
						nameField.getText());
				terminalFormat(
						"<div class='message out'>" + MESSAGE + "</div>",
						msg.getSender(), msg.getTimestamp().createCalendar(),
						msg.getBody());
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

		controlPanel.addAncestorListener(new AncestorListener() {
			@Override
			public void ancestorRemoved(AncestorEvent event) {
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}

			@Override
			public void ancestorAdded(AncestorEvent event) {
				final Dimension size = new Dimension(
						controlPanel.getSize().width, controlPanel
								.getMaximumSize().height);
				controlPanel.setPreferredSize(size);
				controlPanel.setMaximumSize(size);
			}
		});

		add(controlPanel, BorderLayout.WEST);

		addAncestorListener(new AncestorListener() {
			boolean wasAdded = false;

			@Override
			public void ancestorRemoved(AncestorEvent event) {
			}

			@Override
			public void ancestorMoved(AncestorEvent event) {
			}

			@Override
			public void ancestorAdded(AncestorEvent event) {
				if (wasAdded)
					return;
				wasAdded = true;
				if (doLoadStyle)
					sendExecutor.execute(new StyleLoader());
			}
		});
	}

	public void terminalPrintln(String x) {
		if (!doLoadStyle)
			x = preapplyStyle(x);
		synchronized (terminalOutLock) {
			terminal.out.println(x);
			terminal.out.flush();
		}
	}

	public void terminalFormat(String format, Object... args) {
		if (!doLoadStyle)
			format = preapplyStyle(format);
		synchronized (terminalOutLock) {
			terminal.out.format(format, args);
			terminal.out.flush();
		}
	}

	public static final String CSS_FONT_FAMILY = "font-family: 'Avenir Next', "
			+ "Avenir, Futura, Arial, sans-serif;";
	public static final Pattern cssClassPattern = Pattern
			.compile("class=(['\"])([^'\"]*)\\1");
	protected static Map<String, String> cssClassStyleMap;
	{
		String[] keys = { "message out", "message in", "not-user", "time",
				"error", "listen", "connect", "disconnect", };
		String[] values = {
		/* message out */"color:#114422; font-style:italic;" + CSS_FONT_FAMILY,
		/* message in */"color:#113322; font-weight:bold;" + CSS_FONT_FAMILY,
		/* not-user */"color:#000000; font-style:normal; font-weight:normal;",
		/* time */"color:#aaccaa;",
		/* error */"color:#ff0000;" + CSS_FONT_FAMILY,
		/* listen */"color:#8888ff;" + CSS_FONT_FAMILY,
		/* connect */"color:#00ff00;" + CSS_FONT_FAMILY,
		/* disconnect */"color:#882211;" + CSS_FONT_FAMILY, };

		cssClassStyleMap = MapUtil.fromArrays(keys, values);
	}

	public String preapplyStyle(String x) {
		// System.out.format("PREAPPLY STYLE:%n\tx: %s%n", x);
		final StringBuilder sb = new StringBuilder(x);
		final Matcher m = cssClassPattern.matcher(sb);
		while (m.find()) {
			final String style = cssClassStyleMap.get(m.group(2));
			// System.out.format("%s:%n\tmatch: %s%n\tstyle: %s%n", m.group(2),
			// m.group(), style);
			if (style != null) {
				final String s = "style=\"" + style + "\"";
				sb.replace(m.start(), m.end(), s);
				m.reset();
			}
		}
		final String r = sb.toString();
		// System.out.format("RETURN:%n\t%s%n", r);
		return r;
	}

	class StyleLoader implements Runnable {
		@Override
		public void run() {
			System.out.println("StyleLoader::nighttimedriver.chat.testing."
					+ "HTMLChatUITest.StyleLoader.run()");
			// System.out.println("StyleLoader: running");
			// System.out.println("StyleLoader: acquiring lock");
			synchronized (terminalOutLock) {
				// System.out.println("StyleLoader: buffering style start tag");
				terminal.out.println("<head><style type='text/css'>");
				// System.out.println("StyleLoader: starting style loading");
				Reader cssReader = null;
				try {
					// System.out
					// .println("StyleLoader: acquiring resource reader");
					cssReader = new InputStreamReader(getClass()
							.getResourceAsStream("HTMLChatUITest.css"));
					// System.out.println("StyleLoader: allocating read buffer");
					char[] buffer = new char[1024];
					// System.out.println("StyleLoader: starting style buffering");
					int charsRead;
					// System.out.println("StyleLoader: starting buffer loop");
					while ((charsRead = cssReader.read(buffer)) != -1) {
						// System.out.format(
						// "StyleLoader: buffering %d characters%n",
						// charsRead);
						terminal.out.write(buffer, 0, charsRead);
					}
					// System.out
					// .println("StyleLoader: finalizing style buffering");
				} catch (IOException | NullPointerException e) {
					// System.out.println("StyleLoader: style loading failed");
					e.printStackTrace();
				} finally {
					// System.out
					// .println("StyleLoader: starting resource closing");
					try {
						if (cssReader != null)
							cssReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					// System.out
					// .println("StyleLoader: stopping resource closing");
				}
				// System.out.println("StyleLoader: buffering style end tag");
				terminal.out.println();
				terminal.out.println("</style></head><body id='body'></body>");
				// System.out.println("StyleLoader: flushing buffer");
				terminal.out.flush();
				// System.out.println("StyleLoader: casting terminal");
				final HTMLTerminalPane htmlTerminal = ((HTMLTerminalPane) terminal);
				// System.out.println("StyleLoader: getting body element");
				final Element bodyElem = htmlTerminal.getOutputDocument()
						.getElement("body");
				// System.out.println("StyleLoader: setting writer element");
				htmlTerminal.getDocumentWriter().setElement(bodyElem);
				// System.out.println("StyleLoader: releasing lock");
			}
			System.out.println("StyleLoader: stopping");
		}
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
				terminalPrintln("<div class='error'>ERROR while sending message</div>");
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
				terminalPrintln("<div class='error'>ERROR while connecting</div>");
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
				terminalFormat(
						"<div class='listen'>LISTENING on port %d</div>%n",
						serverSocket.getLocalPort());
				socket = serverSocket.accept();
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			} catch (IOException e) {
				e.printStackTrace();
				terminalPrintln("<div class='error'>ERROR while initializing server</div>");
				controlPanel.setVisible(true);
				revalidate();
				repaint();
				return;
			}
			receive();
		}
	}

	public void receive() {
		terminalFormat("<div class='connect'>CONNECTED to %s:%d</div>%n",
				socket.getInetAddress().getHostName(), socket.getPort());
		try {
			msgOut = new ObjectOutputStream(socket.getOutputStream());
			msgIn = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			terminalPrintln("<div class='error'>ERROR while initializing</div>");
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
			terminalFormat("<div class='message in'>" + MESSAGE + "</div>%n",
					msg.getSender(), msg.getTimestamp().createCalendar(),
					msg.getBody());
		}
		try {
			msgIn.close();
			msgOut.close();
			terminalPrintln("<div class='disconnect'>DISCONNECTED</div>");
		} catch (IOException e) {
			e.printStackTrace();
			terminalPrintln("<div class='error'>ERROR while disconnecting</div>");
		}
		socket = null;
		msgIn = null;
		msgOut = null;

		controlPanel.setVisible(true);
		revalidate();
		repaint();
	}

	public static void main(final String[] args) {
		// System.out.println("main::nighttimedriver.chat.testing."
		// + "HTMLChatUITest.main(String[])");
		// System.out.println("main: starting");
		// System.out.println("main: initializing frame object");
		final JFrame frame = new JFrame("HTML Chat UI Test");
		frame.setSize(854, 480);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// System.out.println("main: initializing html chat ui test object");
		final HTMLChatUITest cuit = new HTMLChatUITest();
		cuit.sendExecutor.execute(new Runnable() {
			@Override
			public void run() {
				// System.out.println("main:run::new Runnable(){...}.run()");
				// System.out.println("main:run: adding component to frame");
				frame.add(cuit);
				// System.out.println("main:run: making frame visible");
				frame.setVisible(true);
				// System.out.println("main:run: stopping");
			}
		});
		// System.out.println("main: stopping");
	}

}
