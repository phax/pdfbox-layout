package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.Position;

/**
 * Common interface for drawable objects.
 */
public interface IDrawable3 extends IDrawable
{

  /**
   * Draws the object at the given position.
   *
   * @param aPdDocument
   *        the underlying pdfbox document.
   * @param aContentStream
   *        the stream to draw to.
   * @param aUpperLeft
   *        the upper left position to start drawing.
   * @param aDrawListener
   *        the listener to {@link IDrawListener#drawn(Object, Position, float, float) notify} on
   *        drawn objects.
   * @throws IOException
   *         by pdfbox
   */
  void draw (PDDocument aPdDocument,
             PDPageContentStream aContentStream,
             Position aUpperLeft,
             IDrawListener aDrawListener) throws IOException;

  @Override
  IDrawable3 removeLeadingEmptyVerticalSpace () throws IOException;
}
