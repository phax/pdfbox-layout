package rst.pdfbox.layout.shape;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.Position;

/**
 * Shapes an ellipse, or a circle if width==height.
 */
public class Ellipse extends RoundRect
{

  /**
   * Default constructor.
   */
  public Ellipse ()
  {
    super (0);
  }

  @Override
  protected void addRoundRect (final PDPageContentStream aContentStream,
                               final Position aUpperLeft,
                               final float fWidth,
                               final float fHeight,
                               final float fCornerRadiusX,
                               final float fCornerRadiusY) throws IOException
  {
    super.addRoundRect (aContentStream, aUpperLeft, fWidth, fHeight, fWidth / 2f, fHeight / 2);
  }
}
