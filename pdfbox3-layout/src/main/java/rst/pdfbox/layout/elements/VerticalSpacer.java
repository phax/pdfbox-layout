package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.Position;

/**
 * A drawable element that occupies some vertical space without any graphical representation.
 */
public class VerticalSpacer implements IDrawable3, IElement, IDividable
{

  private final float m_fHeight;

  /**
   * Creates a vertical space with the given height.
   *
   * @param fHeight
   *        the height of the space.
   */
  public VerticalSpacer (final float fHeight)
  {
    this.m_fHeight = fHeight;
  }

  @Override
  public float getWidth () throws IOException
  {
    return 0;
  }

  @Override
  public float getHeight () throws IOException
  {
    return m_fHeight;
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
    if (aDrawListener != null)
    {
      aDrawListener.drawn (this, aUpperLeft, getWidth (), getHeight ());
    }
  }

  @Override
  public Divided divide (final float fRemainingHeight, final float fPageHeight) throws IOException
  {
    return new Divided (new VerticalSpacer (fRemainingHeight), new VerticalSpacer (getHeight () - fRemainingHeight));
  }

  @Override
  public IDrawable3 removeLeadingEmptyVerticalSpace ()
  {
    return this;
  }

}
