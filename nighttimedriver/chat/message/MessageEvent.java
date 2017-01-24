package nighttimedriver.chat.message;

import java.util.EventObject;

/**
 * An event that holds a Message.
 * 
 * @author NighttimeDriver50000
 * 
 */
public class MessageEvent extends EventObject {

	/**
	 * Used for persistent object serialization. The version for which this
	 * documentation was generated is version {@value}
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The Message that holds the body, as well as other associated information,
	 * such as the time it was sent, the sender, etc.
	 */
	private final Message msg;

	/**
	 * Constructs a new MessageEvent with the given source and message.
	 * 
	 * @param source
	 *            The object on which the Event initially occurred
	 * @param msg
	 *            the message to be sent to all of the source's MessageListeners
	 */
	public MessageEvent(final Object source, final Message msg) {
		super(source);
		this.msg = msg;
	}

	/**
	 * Creates a new MessageEvent with the given source and message.
	 * 
	 * @param source
	 *            The object on which the Event initially occurred
	 * @param msg
	 *            the message to be sent to all of the source's MessageListeners
	 */
	public static MessageEvent create(final Object source, final Message msg) {
		return new MessageEvent(source, msg);
	}

	/**
	 * Returns the message held by this MessageEvent.
	 * 
	 * @return the message
	 */
	public Message getMessage() {
		return msg;
	}

}
