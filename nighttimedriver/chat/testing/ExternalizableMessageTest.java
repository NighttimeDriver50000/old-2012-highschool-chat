package nighttimedriver.chat.testing;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import nighttimedriver.chat.Util;
import nighttimedriver.chat.message.ExternalizableMessage;
import nighttimedriver.chat.message.Message;

public class ExternalizableMessageTest {

	public static void main(final String[] args) throws IOException,
			ClassNotFoundException {
		final Path serPath = Paths.get("extmsgtst.ser");
		final ObjectOutputStream out = new ObjectOutputStream(
				Files.newOutputStream(serPath));
		out.writeObject(new ExternalizableMessage(Message.create(
				"This is a test.", "Test")));
		out.close();
		final ObjectInputStream in = new ObjectInputStream(
				Files.newInputStream(serPath));
		final Message msg = ((ExternalizableMessage) in.readObject())
				.getMessage();
		in.close();
		System.out.println(Util.format("%s (@ %tc): %s", msg.getSender(), msg
				.getTimestamp().createCalendar(), msg.getBody()));
	}

}
