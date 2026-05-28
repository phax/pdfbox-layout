package rst.pdfbox.layout.shape;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.Position;

/**
 * A simple rectangular shape.
 */
public class Rect extends AbstractShape
{

  @Override
  public void add (final PDDocument aPdDocument,
                   final PDPageContentStream aContentStream,
                   final Position aUpperLeft,
                   final float fWidth,
                   final float fHeight) throws IOException
  {
    aContentStream.addRect (aUpperLeft.getX (), aUpperLeft.getY () - fHeight, fWidth, fHeight);
  }

}
