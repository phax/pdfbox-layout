package rst.pdfbox.layout.elements.render;

import java.io.IOException;

import rst.pdfbox.layout.elements.ControlElement;
import rst.pdfbox.layout.elements.IDrawable3;
import rst.pdfbox.layout.elements.IElement;

/**
 * The column layout divides the page vertically into columns. You can specify the number of columns
 * and the inter-column spacing. The layouting inside a column is similar to the
 * {@link VerticalLayout}. See there for more details on the possiblities.
 */
public class ColumnLayout extends VerticalLayout
{

  /**
   * Triggers flip to the next column.
   */
  public final static ControlElement NEWCOLUMN = new ControlElement ("NEWCOLUMN");

  private final int m_nColumnCount;
  private final float m_fColumnSpacing;
  private int m_nColumnIndex = 0;
  private Float m_aOffsetY;

  public ColumnLayout (final int nColumnCount)
  {
    this (nColumnCount, 0);
  }

  public ColumnLayout (final int nColumnCount, final float fColumnSpacing)
  {
    this.m_nColumnCount = nColumnCount;
    this.m_fColumnSpacing = fColumnSpacing;
  }

  @Override
  protected float getTargetWidth (final RenderContext aRenderContext)
  {
    return (aRenderContext.getWidth () - ((m_nColumnCount - 1) * m_fColumnSpacing)) / m_nColumnCount;
  }

  /**
   * Flips to the next column
   */
  @Override
  protected void turnPage (final RenderContext aRenderContext) throws IOException
  {
    if (++m_nColumnIndex >= m_nColumnCount)
    {
      aRenderContext.newPage ();
      m_nColumnIndex = 0;
      m_aOffsetY = Float.valueOf (0f);
    }
    else
    {
      final float fNextColumnX = (getTargetWidth (aRenderContext) + m_fColumnSpacing) * m_nColumnIndex;
      aRenderContext.resetPositionToUpperLeft ();
      aRenderContext.movePositionBy (fNextColumnX, -m_aOffsetY.floatValue ());
    }
  }

  @Override
  public boolean render (final RenderContext aRenderContext, final IElement aElement, final ILayoutHint aLayoutHint)
                                                                                                                     throws IOException
  {
    if (aElement == ControlElement.NEWPAGE)
    {
      aRenderContext.newPage ();
      return true;
    }
    if (aElement == NEWCOLUMN)
    {
      turnPage (aRenderContext);
      return true;
    }
    return super.render (aRenderContext, aElement, aLayoutHint);
  }

  @Override
  public void render (final RenderContext aRenderContext, final IDrawable3 aDrawable, final ILayoutHint aLayoutHint)
                                                                                                                     throws IOException
  {
    if (m_aOffsetY == null)
    {
      m_aOffsetY = Float.valueOf (aRenderContext.getUpperLeft ().getY () -
                                  aRenderContext.getCurrentPosition ().getY ());
    }
    super.render (aRenderContext, aDrawable, aLayoutHint);
  }

  @Override
  protected boolean isPositionTopOfPage (final RenderContext aRenderContext)
  {
    float fTopPosition = aRenderContext.getUpperLeft ().getY ();
    if (m_aOffsetY != null)
    {
      fTopPosition -= m_aOffsetY.floatValue ();
    }
    return aRenderContext.getCurrentPosition ().getY () == fTopPosition;
  }

}
