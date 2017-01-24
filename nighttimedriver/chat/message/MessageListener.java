package nighttimedriver.chat.message;

import java.util.EventListener;

/**
 * An interface for objects to receive messages
 * 
 * @author NighttimeDriver50000
 * 
 */
public interface MessageListener extends EventListener {

	/**
	 * Called when a message is broadcast by the source.
	 * 
	 * @param evt
	 *            holds the message, as well as a reference to the source
	 */
	void messageReceived(MessageEvent evt);

}
