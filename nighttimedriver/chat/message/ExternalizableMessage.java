package nighttimedriver.chat.message;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import nighttimedriver.chat.Util;

public class ExternalizableMessage implements Externalizable {

	/**
	 * Used for persistent object serialization. The version for which this
	 * documentation was generated is version {@value}
	 * 
	 * <p>
	 * This value should not be changed unless a new version of a serialization
	 * method is not backwards-compatible.
	 * </p>
	 */
	private static final long serialVersionUID = 1L;

	public static final Message DEFAULT_MSG = Message.create(
			"Error loading message.", "SERVER", Timestamp.EPOCH);
	/**
	 * The message to be written from and read into.
	 */
	private Message msg;

	public ExternalizableMessage() {
		msg = DEFAULT_MSG;
	}

	public ExternalizableMessage(final Message msg) {
		this.msg = msg;
	}

	public Message getMessage() {
		return msg;
	}
	
	

	@Override
	public void writeExternal(final ObjectOutput out) throws IOException {
		final String[] args = { Long.toString(serialVersionUID, 36),
				msg.getBody(), msg.getSender(),
				Long.toString(msg.getTimestamp().getMillisTime(), 36) };
		final String msg = Util.format("<extmsg>"
				+ (args[0] == null ? "" : "<ver>%1$s</ver>") + "<msg>"
				+ (args[1] == null ? "" : "<body>%2$s</body>") + "<src>"
				+ (args[2] == null ? "" : "<name>%3$s</name>") + "</src>"
				+ "<time>" + (args[3] == null ? "" : "<ms>%4$s</ms>")
				+ "</time>" + "</msg>" + "</extmsg>", (Object[]) args);
		out.writeUTF(msg);
		writeImpl(out);
	}

	protected void writeImpl(ObjectOutput out) throws IOException {
	}

	@Override
	public void readExternal(final ObjectInput in) throws IOException,
			ClassNotFoundException {
		final String extmsg = in.readUTF();
		final long version = Long.parseLong(
				getElementInner(extmsg, "extmsg", "ver"), 36);
		if (version == 1) {
			final String msgTag = getElementInner(extmsg, "extmsg", "msg");
			String body = getElementInner(msgTag, "body");
			if (body == null)
				body = DEFAULT_MSG.getBody();
			String name = getElementInner(msgTag, "src", "name");
			if (name == null)
				name = DEFAULT_MSG.getSender();
			final String ms = getElementInner(msgTag, "time", "ms");
			assert (ms != null);
			Timestamp time;
			try {
				time = Timestamp.create(Long.parseLong(ms, 36));
			} catch (NumberFormatException | NullPointerException e) {
				time = DEFAULT_MSG.getTimestamp();
			}
			msg = Message.create(body, name, time);
		}
		readImpl(in);
	}

	protected void readImpl(ObjectInput in) throws IOException, ClassNotFoundException {
	}

	public static String getElementInner(String src,
			final String... elementHeirarchy) {
		for (final String element : elementHeirarchy) {
			final int index = src.indexOf("<" + element + ">");
			if (index == -1)
				return null;
			src = src.substring(index + element.length() + 2,
					src.indexOf("</" + element + ">", index));
		}
		return src;
	}
}
