package rst.pdfbox.layout.elements;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.shape.Stroke;
import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.IWidthRespecting;
import rst.pdfbox.layout.text.Position;

/**
 * A horizontal ruler that adjust its width to the given {@link IWidthRespecting#getMaxWidth() max
 * width}.
 */
public class HorizontalRuler implements IDrawable3, IElement, IWidthRespecting
{

  private final Stroke m_aStroke;
  private final Color m_aColor;
  private float m_fMaxWidth = -1f;

  public HorizontalRuler (final Stroke aStroke, final Color aColor)
  {
    super ();
    this.m_aStroke = aStroke;
    this.m_aColor = aColor;
  }

  /**
   * @return the stroke to draw the ruler with.
   */
  public Stroke getStroke ()
  {
    return m_aStroke;
  }

  /**
   * @return the color to draw the ruler with.
   */
  public Color getColor ()
  {
    return m_aColor;
  }

  @Override
  public float getMaxWidth ()
  {
    return m_fMaxWidth;
  }

  @Override
  public void setMaxWidth (final float fMaxWidth)
  {
    this.m_fMaxWidth = fMaxWidth;
  }

  @Override
  public float getWidth () throws IOException
  {
    return getMaxWidth ();
  }

  @Override
  public float getHeight () throws IOException
  {
    if (getStroke () == null)
    {
      return 0f;
    }
    return getStroke ().getLineWidth ();
  }

  @Override
  public Position getAbsolutePosition ()
  {
    return null;
  }

  @Override
  public void draw (final PDDocument aPdDocument,
                    final PDPageContentStream aContentStream,
                    final Position aUpperLeft,
                    final IDrawListener aDrawListener) throws IOException
  {
    if (getColor () != null)
    {
      aContentStream.setStrokingColor (getColor ());
    }
    if (getStroke () != null)
    {
      getStroke ().applyTo (aContentStream);
      final float x = aUpperLeft.getX ();
      final float y = aUpperLeft.getY () - getStroke ().getLineWidth () / 2;
      aContentStream.moveTo (x, y);
      aContentStream.lineTo (x + getWidth (), y);
      aContentStream.stroke ();
    }
    if (aDrawListener != null)
    {
      aDrawListener.drawn (this, aUpperLeft, getWidth (), getHeight ());
    }
  }

  @Override
  public IDrawable3 removeLeadingEmptyVerticalSpace ()
  {
    return this;
  }

}
