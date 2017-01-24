package nighttimedriver.chat;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

/**
 * A component similar to a command-line environment, except that input is
 * entered into a text field at the bottom of the component.
 * 
 * @author NighttimeDriver50000
 * 
 */
public class TerminalPane extends JPanel implements LineInputListener {

	/**
	 * Used for persistent object serialization. The version for which this
	 * documentation was generated is version {@value}
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This PrintWriter acts like standard output, appending to the display text
	 * area.
	 */
	public final PrintWriter out;
	/**
	 * The text field used by the user for input into the TerminalPane.
	 * <p>
	 * Input should be handled in the filterLineInput() and lineInput() methods,
	 * not using a TextListener or DocumentListener, unless it is for live
	 * filtering purposes.
	 * </p>
	 */
	protected final JTextField inputField;

	/**
	 * Constructs a new TerminalPane.
	 */
	public TerminalPane() {
		setLayout(new BorderLayout(0, 0));

		final JScrollPane scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);

		final JTextComponent outputTextComponent = createOutputTextComponent();
		scrollPane.setViewportView(outputTextComponent);

		out = createOut(outputTextComponent);

		inputField = new JTextField();
		inputField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent evt) {
				fireLineInputEvent(LineInputEvent.create(TerminalPane.this,
						inputField.getText()));
			}
		});
		add(inputField, BorderLayout.SOUTH);

		addLineInputListener(this);
	}
	
	protected JTextComponent createOutputTextComponent() {
		final JTextArea outputTextComponent = new JTextArea();
		outputTextComponent.setWrapStyleWord(true);
		outputTextComponent.setLineWrap(true);
		outputTextComponent.setEditable(false);
		return outputTextComponent;
	}
	
	protected PrintWriter createOut(final JTextComponent outputTextComponent) {
		return new PrintWriter(new DocumentWriter(outputTextComponent.getDocument()) {
			@Override
			public void write(final String str) throws IOException {
				super.write(str);
				outputTextComponent.setCaretPosition(doc.getLength());
			}
		}, true);
	}

	/**
	 * Adds the given line input listener to receive line input events from this
	 * terminal pane.
	 * 
	 * @param listener
	 *            the line input listener to be added
	 */
	public final void addLineInputListener(final LineInputListener listener) {
		listenerList.add(LineInputListener.class, listener);
	}

	/**
	 * Removes the given line input listener so that it no longer receives line
	 * input events from this terminal pane.
	 * 
	 * @param listener
	 *            the line input listener to be removed
	 */
	public final void removeLineInputListener(final LineInputListener listener) {
		listenerList.remove(LineInputListener.class, listener);
	}

	public final void fireLineInputEvent(LineInputEvent evt) {
		evt = filterLineInput(evt);
		if (evt == null)
			return;
		final Object[] listeners = listenerList.getListenerList();
		for (int i = 0; i < listeners.length; i += 2) {
			if (listeners[i] == LineInputListener.class) {
				((LineInputListener) listeners[i + 1]).lineInput(evt);
			}
		}
	}

	/**
	 * Filters a line input event before it is sent to the line input listeners.
	 * 
	 * <p>
	 * If this method returns <code>null</code>, the line input event will be
	 * canceled and the listeners will not be notified.
	 * </p>
	 * 
	 * <p>
	 * This implementation immediately returns the event, doing no filtering.
	 * </p>
	 * 
	 * @param evt
	 * @return
	 */
	protected LineInputEvent filterLineInput(final LineInputEvent evt) {
		return evt;
	}
	
	/**
	 * Called when a line is input into the input text field.
	 * 
	 * <p>
	 * This implementation clears the input text field.
	 * </p>
	 * 
	 * @param evt
	 *            holds the line of input, as well as a reference to
	 *            <code>this</code>
	 */
	@Override
	public void lineInput(final LineInputEvent evt) {
		inputField.setText("");
	}

}
