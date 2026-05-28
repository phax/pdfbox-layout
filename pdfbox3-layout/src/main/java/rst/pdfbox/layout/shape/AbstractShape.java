package rst.pdfbox.layout.shape;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * Abstract base class for shapes which performs the
 * {@link #fill(PDDocument, PDPageContentStream, Position, float, float, Color, IDrawListener)} and
 * (@link
 * {@link #draw(PDDocument, PDPageContentStream, Position, float, float, Color, Stroke, IDrawListener)}
 * .
 */
public abstract class AbstractShape implements Shape
{

  @Override
  public void draw (final PDDocument aPdDocument,
                    final PDPageContentStream aContentStream,
                    final Position aUpperLeft,
                    final float fWidth,
                    final float fHeight,
                    final Color aColor,
                    final Stroke aStroke,
                    final IDrawListener aDrawListener) throws IOException
  {
    add (aPdDocument, aContentStream, aUpperLeft, fWidth, fHeight);

    if (aStroke != null)
    {
      aStroke.applyTo (aContentStream);
    }
    if (aColor != null)
    {
      aContentStream.setStrokingColor (aColor);
    }
    aContentStream.stroke ();

    if (aDrawListener != null)
    {
      aDrawListener.drawn (this, aUpperLeft, fWidth, fHeight);
    }
  }

  @Override
  public void fill (final PDDocument aPdDocument,
                    final PDPageContentStream aContentStream,
                    final Position aUpperLeft,
                    final float fWidth,
                    final float fHeight,
                    final Color aColor,
                    final IDrawListener aDrawListener) throws IOException
  {
    add (aPdDocument, aContentStream, aUpperLeft, fWidth, fHeight);

    if (aColor != null)
    {
      aContentStream.setNonStrokingColor (aColor);
    }
    CompatibilityHelper.fillNonZero (aContentStream);

    if (aDrawListener != null)
    {
      aDrawListener.drawn (this, aUpperLeft, fWidth, fHeight);
    }
  }

}
