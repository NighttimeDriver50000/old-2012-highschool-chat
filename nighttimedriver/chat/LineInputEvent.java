package nighttimedriver.chat;

import java.util.EventObject;

/**
 * An event that holds a single line of input.
 * 
 * @author NighttimeDriver50000
 * 
 */
public class LineInputEvent extends EventObject {

	/**
	 * Used for persistent object serialization. The version for which this
	 * documentation was generated is version {@value}
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * The input line.
	 */
	protected String line;

	/**
	 * Constructs a new LineInputEvent holding the given line from the given
	 * source.
	 * 
	 * @param source
	 *            the object on which the Event initially occurred
	 * @param line
	 *            the input line
	 */
	private LineInputEvent(final Object source, final String line) {
		super(source);
		this.line = line;
	}

	/**
	 * Creates a new LineInputEvent holding the given line from the given
	 * source. Any line breaks and carriage returns in the line will be removed.
	 * 
	 * @param source
	 *            the object on which the Event initially occurred
	 * @param line
	 *            the input line
	 */
	public static LineInputEvent create(final Object source, final String line) {
		return create(source, line, true);
	}

	/**
	 * Creates a new LineInputEvent holding the given line from the given
	 * source. The line should not contain any line breaks, but if they should
	 * not be automatically removed, the <code>removeBreaks</code> flag should
	 * be false.
	 * 
	 * @param source
	 *            the object on which the Event initially occurred
	 * @param line
	 *            the input line
	 * @param removeTrailingBreaks
	 *            if true, any line breaks and carriage returns in the line will
	 *            be removed
	 */
	public static LineInputEvent create(final Object source, String line,
			final boolean removeBreaks) {
		if (removeBreaks)
			line = Util.removeChars(line, '\n', '\r');
		return new LineInputEvent(source, line);
	}

	/**
	 * Returns the input line.
	 * 
	 * @return the line
	 */
	public String getLine() {
		return line;
	}

}
