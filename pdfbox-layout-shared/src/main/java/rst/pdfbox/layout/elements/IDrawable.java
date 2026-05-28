package rst.pdfbox.layout.elements;

import java.io.IOException;

import rst.pdfbox.layout.text.Position;

/**
 * PDFBox-version-independent base for {@link Drawable}. Holds every drawable
 * operation that does not depend on a concrete PDFBox version. The actual
 * {@code draw(...)} method (which takes PDFBox-typed parameters) lives in the
 * module-specific {@code Drawable} sub-interface.
 */
public interface IDrawable {

    /**
     * @return the width of the drawable.
     * @throws IOException
     *             by pdfbox
     */
    float getWidth() throws IOException;

    /**
     * @return the height of the drawable.
     * @throws IOException
     *             by pdfbox
     */
    float getHeight() throws IOException;

    /**
     * If an absolute position is given, the drawable will be drawn at this
     * position ignoring any layout.
     *
     * @return the absolute position.
     * @throws IOException
     *             by pdfbox
     */
    Position getAbsolutePosition() throws IOException;

    /**
     * @return a copy of this drawable where any leading empty vertical space is
     *         removed, if possible. This is useful for avoiding leading empty
     *         space on a new page.
     * @throws IOException
     *             by pdfbox
     */
    IDrawable removeLeadingEmptyVerticalSpace() throws IOException;
}
