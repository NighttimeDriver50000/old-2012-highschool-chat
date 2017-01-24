package nighttimedriver.chat.testing;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import nighttimedriver.chat.message.ExternalizableMessage;
import nighttimedriver.chat.message.Message;

public class ReceiveMessage {

	public static void main(final String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("<port>");
			System.exit(-1);
		}
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(Integer.parseInt(args[0]));
		} catch (final IllegalArgumentException e) {
			System.err.format("\"%s\" is not a valid port number."
					+ " The range of valid port numbers is"
					+ " between 0 and 65535, inclusive.%n", args[0]);
			System.exit(1);
		}
		final Socket socket = serverSocket.accept();
		final ObjectInputStream in = new ObjectInputStream(
				socket.getInputStream());
		for (;;) {
			Message msg = null;
			try {
				msg = ((ExternalizableMessage) in.readObject()).getMessage();
			} catch (final EOFException e) {
				break;
			} catch (IOException | ClassNotFoundException e) {
				msg = ExternalizableMessage.DEFAULT_MSG;
			}
			System.out.format("%s (@ %tc): %s%n", msg.getSender(), msg
					.getTimestamp().createCalendar(), msg.getBody());
		}
		in.close();
	}

}
