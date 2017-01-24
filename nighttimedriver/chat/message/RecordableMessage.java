package nighttimedriver.chat.message;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import nighttimedriver.chat.Util;

public class RecordableMessage extends ExternalizableMessage {

	public enum TransceiveType {
		SENT("sent"), RECEIVED("received");

		private final String name;

		TransceiveType(String name) {
			this.name = name;
		}

		public String toString() {
			return name;
		}
	}

	private String username;
	private Timestamp time;
	private TransceiveType type;

	public RecordableMessage() {
		this(DEFAULT_MSG, null, DEFAULT_MSG.getSender());
	}

	public RecordableMessage(Message msg, TransceiveType type, String username,
			Timestamp time) {
		super(msg);
		this.type = type;
		this.username = username;
		this.time = time;
	}

	public RecordableMessage(Message msg, TransceiveType type, String username) {
		super(msg);
		this.type = type;
		this.username = username;
		time = Timestamp.create();
	}

	/* add to ExternalizableMessage empty and call at the end of writeObject() */
	@Override
	protected void writeImpl(ObjectOutput out) throws IOException {
		out.writeUTF(Util.format("<transfer>"
				+ "<ver>1</ver>"
				+ (type == null ? "" : "<type>%1$s</type>")
				+ "<user>"
				+ (username == null ? "" : "<name>%2$s</name>")
				+ "</user>"
				+ (time == null ? "" : ("<time>" + "<full>%3$tc</full>"
						+ "<ms>%4$s</ms>" + "</time>")) + "</transfer>", type,
				username, time.createCalendar(),
				Long.toString(time.getMillisTime(), 36)));
	}

	/* add to ExternalizableMessage empty and call at the end of readObject() */
	@Override
	protected void readImpl(ObjectInput in) throws IOException {
		final String transfer = getElementInner(in.readUTF(), "transfer");
		final int version = Integer.parseInt(getElementInner(transfer, "ver"));
		switch (version) {
		case 1:
			String typeName = getElementInner(transfer, "type");
			if (typeName != null) {
				if (typeName.equals("sent"))
					type = TransceiveType.SENT;
				else if (typeName.equals("received"))
					type = TransceiveType.RECEIVED;
			}
			String name = getElementInner(transfer, "user", "name");
			if (name != null)
				username = name;
			String timeStr = getElementInner(transfer, "time", "ms");
			try {
				time = Timestamp.create(Integer.parseInt(timeStr, 36));
			} catch (Exception e) {
				time = getMessage().getTimestamp();
			}
			break;
		}
	}

}
