package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.text.DrawListener;
import rst.pdfbox.layout.text.Position;

/**
 * Common interface for drawable objects.
 */
public interface Drawable extends IDrawable {

    /**
     * Draws the object at the given position.
     *
     * @param pdDocument
     *            the underlying pdfbox document.
     * @param contentStream
     *            the stream to draw to.
     * @param upperLeft
     *            the upper left position to start drawing.
     * @param drawListener
     *            the listener to
     *            {@link DrawListener#drawn(Object, Position, float, float) notify} on
     *            drawn objects.
     * @throws IOException
     *             by pdfbox
     */
    void draw(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, DrawListener drawListener) throws IOException;

    @Override
    Drawable removeLeadingEmptyVerticalSpace() throws IOException;
}
