package nighttimedriver.chat;

import java.io.IOException;
import java.io.Writer;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Position;

/**
 * Adapts the Java Writer API to insert text into to text components that use
 * the <code>Document</code> interface, such as Swing's JTextComponents.
 * <p>
 * The stream is unbuffered; every write() operation writes directly to the
 * document. Consequently, flush() does nothing.
 * </p>
 * 
 * @author NighttimeDriver50000
 */
public class DocumentWriter extends Writer {

	protected boolean closed = false;
	protected Document doc;
	protected Position pos;

	/**
	 * Creates a new <code>DocumentWriter</code> that appends to the given
	 * document.
	 * 
	 * @param doc
	 *            the document to append to
	 */
	public DocumentWriter(final Document doc) {
		this(doc, doc.getEndPosition());
	}

	/**
	 * Creates a new <code>DocumentWriter</code> that inserts text into the
	 * given document starting at the given position.
	 * 
	 * @param doc
	 *            the document to write to
	 * @param pos
	 *            the position from which to start
	 */
	public DocumentWriter(final Document doc, final Position pos) {
		this.doc = doc;
		this.pos = pos;
	}

	/**
	 * Creates a new <code>DocumentWriter</code> that inserts text into the
	 * given document starting from the given offset.
	 * 
	 * @param doc
	 *            the document to write to
	 * @param offs
	 *            the offset from which to start
	 * @throws BadLocationException
	 *             if the given offset does not represent a valid location in
	 *             the associated document
	 */
	public DocumentWriter(final Document doc, final int offs)
			throws BadLocationException {
		this(doc, doc.createPosition(offs));
	}

	/**
	 * Inserts a single character into the document. The character to be written
	 * is contained in the 16 low-order bits of the given integer value; the 16
	 * high-order bits are ignored.
	 */
	@Override
	public void write(final int c) throws IOException {
		write(String.valueOf((char) c));
	}

	/**
	 * Inserts an array of characters into the document.
	 */
	@Override
	public void write(final char[] cbuf) throws IOException {
		write(new String(cbuf));
	}

	/**
	 * Inserts a portion of an array of characters into the document.
	 */
	@Override
	public void write(final char[] cbuf, final int off, final int len)
			throws IOException {
		write(new String(cbuf, off, len));
	}

	/**
	 * Inserts a string into the document. All of the other write() methods call
	 * this one.
	 */
	@Override
	public void write(final String str) throws IOException {
		synchronized (lock) {
			if (closed)
				throw new IOException("closed stream");
			try {
				doc.insertString(pos.getOffset(), str, null);
			} catch (final BadLocationException e) {
				throw new IOException(e);
			}
		}
	}

	/**
	 * Inserts a portion of a string into the document.
	 */
	@Override
	public void write(final String str, final int off, final int len)
			throws IOException {
		write(str.substring(off, off + len));
	}

	/**
	 * Flushes the stream. In this implementation, this method does nothing; the
	 * stream is unbuffered.
	 */
	@Override
	public void flush() throws IOException {
	}

	@Override
	public void close() throws IOException {
		flush();
		closed = true;
		doc = null;
	}
}
