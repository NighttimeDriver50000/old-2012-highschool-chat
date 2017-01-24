package nighttimedriver.chat.testing;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import nighttimedriver.chat.LineInputEvent;
import nighttimedriver.chat.TerminalPane;

public class TerminalPaneTest extends TerminalPane {

	private static final long serialVersionUID = 1L;

	public static void main(final String[] args) {
		final JFrame frame = new JFrame("TerminalPane Test");
		frame.setSize(854, 480);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});
		final TerminalPane pane = new TerminalPaneTest();
		frame.getContentPane().add(pane, BorderLayout.CENTER);
		frame.setVisible(true);
		pane.out.println("Input text in the field below and press 'Enter' to submit!");
	}
	
	@Override
	public void lineInput(final LineInputEvent evt) {
		super.lineInput(evt);
		out.format("User Input: %s%n", evt.getLine());
	}

}
