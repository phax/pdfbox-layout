package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.Position;

/**
 * A cutter transforms any Drawable element into a {@link IDividable}. It simply <em>cuts</em> the
 * drawable vertically into pieces matching the target height.
 */
public class Cutter implements IDividable, IDrawable3
{

  private final IDrawable3 m_aUndividable;
  private final float m_fViewPortY;
  private final float m_fViewPortHeight;

  public Cutter (final IDrawable3 aUndividableElement) throws IOException
  {
    this (aUndividableElement, 0, aUndividableElement.getHeight ());
  }

  protected Cutter (final IDrawable3 aUndividable, final float fViewPortY, final float fViewPortHeight)
  {
    this.m_aUndividable = aUndividable;
    this.m_fViewPortY = fViewPortY;
    this.m_fViewPortHeight = fViewPortHeight;
  }

  @Override
  public Divided divide (final float fRemainingHeight, final float fPageHeight)
  {
    return new Divided (new Cutter (m_aUndividable, m_fViewPortY, fRemainingHeight),
                        new Cutter (m_aUndividable, m_fViewPortY - fRemainingHeight, m_fViewPortHeight - fRemainingHeight));
  }

  @Override
  public float getWidth () throws IOException
  {
    return m_aUndividable.getWidth ();
  }

  @Override
  public float getHeight () throws IOException
  {
    return m_fViewPortHeight;
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
    final Position aViewPortOrigin = aUpperLeft.add (0, -m_fViewPortY);
    m_aUndividable.draw (aPdDocument, aContentStream, aViewPortOrigin, aDrawListener);
  }

  @Override
  public IDrawable3 removeLeadingEmptyVerticalSpace () throws IOException
  {
    return new Cutter (m_aUndividable.removeLeadingEmptyVerticalSpace ());
  }

}
