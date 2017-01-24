package nighttimedriver.chat.message;

/**
 * An immutable object that represents a string with other associated
 * information, such as the time it was sent, the sender, etc.
 * 
 * @author NighttimeDriver50000
 */
public class Message {
	/**
	 * The body of the message.
	 */
	private final String body;
	/**
	 * A timestamp for the message.
	 */
	private final Timestamp timestamp;
	/**
	 * The name of the sender of the message, or <code>null</code> if the server
	 * generated this message.
	 */
	private final String sender;

	/**
	 * Constructs a new Message with the given body, sender, and timestamp.
	 * 
	 * @param body
	 *            the body of the new message
	 * @param sender
	 *            the name of the sender of the message, or <code>null</code> if
	 *            the server generated this message
	 * @param time
	 *            the timestamp for the message
	 */
	private Message(final String body, final String sender, final Timestamp time) {
		this.body = body;
		this.sender = sender;
		timestamp = time;
	}

	/**
	 * Creates a new Message with the given body and sender and the current
	 * time.
	 * 
	 * @param body
	 *            the body of the new message
	 * @param sender
	 *            the name of the sender of the message, or <code>null</code> if
	 *            the server generated this message
	 * @return a new Message with the given body and sender and the current time
	 */
	public static Message create(final String body, final String sender) {
		return new Message(body, sender, Timestamp.create());
	}

	/**
	 * Constructs a new Message with the given body, sender, and timestamp.
	 * 
	 * @param body
	 *            the body of the new message
	 * @param sender
	 *            the name of the sender of the message, or <code>null</code> if
	 *            the server generated this message
	 * @param time
	 *            the timestamp for the message
	 * @return a new Message with the given body and sender and the current time
	 */
	public static Message create(final String body, final String sender,
			final Timestamp time) {
		return new Message(body, sender, time);
	}

	/**
	 * Returns the body of this message.
	 * 
	 * @return the body of this message
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Returns the timestamp for this message.
	 * 
	 * @return the timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}

	/**
	 * Returns the name of the sender of this message, or <code>null</code> if
	 * the server generated this message.
	 * 
	 * @return the name of the sender
	 */
	public String getSender() {
		return sender;
	}
}
