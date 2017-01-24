package nighttimedriver.chat.testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import nighttimedriver.chat.message.ExternalizableMessage;
import nighttimedriver.chat.message.Message;

public class SendMessage {

	public static void main(final String[] args) throws IOException {
		if (args.length < 2) {
			System.out.println("<host> <port>");
			System.exit(-1);
		}
		Socket socket = null;
		try {
			socket = new Socket(args[0], Integer.parseInt(args[1]));
		} catch (final IllegalArgumentException e) {
			System.err.format("\"%s\" is not a valid port number."
					+ " The range of valid port numbers is"
					+ " between 0 and 65535, inclusive.%n", args[1]);
			System.exit(1);
		} catch (final UnknownHostException e) {
			System.err.format("The ip address of host \"%s\""
					+ " could not be determined.%n", args[0]);
			System.exit(1);
		}
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("Enter a username:");
		final String name = in.readLine();
		final ObjectOutputStream out = new ObjectOutputStream(
				socket.getOutputStream());
		for (;;) {
			try {
				final String line = in.readLine();
				if (line.equals("exit"))
					break;
				out.writeObject(new ExternalizableMessage(Message.create(line,
						name)));
			} catch (final IOException e) {
				System.err.println("An error occured while"
						+ " sending the message.");
			}
		}
		out.close();
	}
}
