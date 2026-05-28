package rst.pdfbox.layout.text;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * Represents a drawable text.
 */
public interface IDrawableText extends IArea
{
  /**
   * Draws the text of the (PdfBox-) cursor position.
   *
   * @param aContentStream
   *        the content stream used to render.
   * @param aUpperLeft
   *        the upper left position to draw to.
   * @param eAlignment
   *        the text alignment.
   * @param aDrawListener
   *        the listener to {@link IDrawListener#drawn(Object, Position, float, float) notify} on
   *        drawn objects.
   * @throws IOException
   *         by pdfbox.
   */
  void drawText (PDPageContentStream aContentStream,
                 Position aUpperLeft,
                 EAlignment eAlignment,
                 IDrawListener aDrawListener) throws IOException;
}
