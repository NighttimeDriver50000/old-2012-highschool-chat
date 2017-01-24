package nighttimedriver.chat;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JTextPane;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTMLDocument;

// Note: does NOT auto-flush
public class HTMLTerminalPane extends TerminalPane {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private HTMLDocument outDoc;
	private HTMLDocumentWriter docWriter;

	@Override
	protected JTextComponent createOutputTextComponent() {
		final JTextPane outputTextComponent = new JTextPane();
		outputTextComponent.setContentType("text/html");
		outputTextComponent.setText("<html></html>");
		outputTextComponent.setEditable(false);
		return outputTextComponent;
	}
	
	@Override
	protected PrintWriter createOut(final JTextComponent outputTextComponent) {
		outDoc = (HTMLDocument) outputTextComponent.getDocument();
		try {
			outDoc.setBase(new URL("file:."));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		final Element elem = outDoc.getDefaultRootElement();
		docWriter = new HTMLDocumentWriter(outDoc, elem) {
			@Override
			public void write(final String str) throws IOException {
				super.write(str);
				outputTextComponent.setCaretPosition(doc.getLength());
			}
		};
		return new PrintWriter(docWriter, false);
	}

	public HTMLDocument getOutputDocument() {
		return outDoc;
	}
	
	public HTMLDocumentWriter getDocumentWriter() {
		return docWriter;
	}
	
}
