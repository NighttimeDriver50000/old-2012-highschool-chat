package nighttimedriver.chat;

import java.util.EventListener;

/**
 * An interface for objects to receive line input events
 * 
 * @author NighttimeDriver50000
 * 
 */
public interface LineInputListener extends EventListener {

	/**
	 * Called when a line is input in the source.
	 * 
	 * @param evt
	 *            holds the line of input, as well as a reference to the source
	 */
	void lineInput(LineInputEvent evt);

}