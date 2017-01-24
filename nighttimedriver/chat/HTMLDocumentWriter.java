package nighttimedriver.chat;

import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLDocument;

public class HTMLDocumentWriter extends DocumentWriter {
	protected StringBuffer buffer = new StringBuffer();
	private Element elem;

	public HTMLDocumentWriter(final HTMLDocument doc) {
		this(doc, doc.getDefaultRootElement());
	}
	
	public HTMLDocumentWriter(final HTMLDocument doc, final Element elem) {
		super(doc);
		this.elem = elem;
	}

	@Override
	public void write(final String str) throws IOException {
		synchronized (lock) {
			if (closed)
				throw new IOException("closed stream");
			buffer.append(str);
		}
	}

	@Override
	public void flush() throws IOException {
		try {
			final HTMLDocument htmlDoc = (HTMLDocument) doc;
			htmlDoc.insertBeforeEnd(elem, buffer.toString());
			buffer = new StringBuffer();
		} catch (final BadLocationException | ClassCastException e) {
			throw new IOException(e);
		}
	}
	
	public Element getElement() {
		return elem;
	}

	public void setElement(Element elem) {
		this.elem = elem;
	}
}
