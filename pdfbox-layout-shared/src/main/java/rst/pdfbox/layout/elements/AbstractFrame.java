package rst.pdfbox.layout.elements;

import java.awt.Color;
import java.io.IOException;

import rst.pdfbox.layout.text.IWidthRespecting;
import rst.pdfbox.layout.text.Position;

/**
 * PDFBox-version-independent base for {@code Frame}. Holds the geometric and cosmetic state
 * (paddings, margins, colors, size hints, absolute position) plus the spacing/border-width
 * calculations that are identical between all pdfbox flavours. The PDFBox-coupled bits — actual
 * {@code Shape}/{@code Stroke} fields, drawing, and {@link IDividable#divide(float, float)} — stay
 * in the module-specific subclass.
 */
public abstract class AbstractFrame implements IElement, IDrawable, IWidthRespecting, IDividable
{
  private float m_fPaddingLeft;
  private float m_fPaddingRight;
  private float m_fPaddingTop;
  private float m_fPaddingBottom;

  private float m_fMarginLeft;
  private float m_fMarginRight;
  private float m_fMarginTop;
  private float m_fMarginBottom;

  private Color m_aBorderColor;
  private Color m_aBackgroundColor;

  private float m_fMaxWidth = -1;

  private Float m_aGivenWidth;
  private Float m_aGivenHeight;

  private Position m_aAbsolutePosition;

  protected AbstractFrame ()
  {}

  protected AbstractFrame (final Float aWidth, final Float aHeight)
  {
    this.m_aGivenWidth = aWidth;
    this.m_aGivenHeight = aHeight;
  }

  /**
   * @return the current width of the {@link #getBorderStroke() border stroke} on the active shape,
   *         or {@code 0} if no border is drawn.
   */
  protected abstract float getBorderWidth ();

  /**
   * @return the sum of left/right padding and border width.
   */
  protected float getHorizontalShapeSpacing ()
  {
    return 2 * getBorderWidth () + getPaddingLeft () + getPaddingRight ();
  }

  /**
   * @return the sum of top/bottom padding and border width.
   */
  protected float getVerticalShapeSpacing ()
  {
    return 2 * getBorderWidth () + getPaddingTop () + getPaddingBottom ();
  }

  /**
   * @return the sum of left/right margin, padding and border width.
   */
  protected float getHorizontalSpacing ()
  {
    return getMarginLeft () + getMarginRight () + getHorizontalShapeSpacing ();
  }

  /**
   * @return the sum of top/bottom margin, padding and border width.
   */
  protected float getVerticalSpacing ()
  {
    return getMarginTop () + getMarginBottom () + getVerticalShapeSpacing ();
  }

  /**
   * @return the height given to constrain the size of the shape.
   */
  protected Float getGivenHeight ()
  {
    return m_aGivenHeight;
  }

  /**
   * @return the width given to constrain the size of the shape.
   */
  protected Float getGivenWidth ()
  {
    return m_aGivenWidth;
  }

  /**
   * @return the color to use to draw the border.
   */
  public Color getBorderColor ()
  {
    return m_aBorderColor;
  }

  /**
   * Sets the color to use to draw the border.
   *
   * @param aBorderColor
   *        the border color.
   */
  public void setBorderColor (final Color aBorderColor)
  {
    this.m_aBorderColor = aBorderColor;
  }

  /**
   * @return the color to use to draw the background.
   */
  public Color getBackgroundColor ()
  {
    return m_aBackgroundColor;
  }

  /**
   * Sets the color to use to draw the background.
   *
   * @param aBackgroundColor
   *        the background color.
   */
  public void setBackgroundColor (final Color aBackgroundColor)
  {
    this.m_aBackgroundColor = aBackgroundColor;
  }

  /**
   * @return the left padding
   */
  public float getPaddingLeft ()
  {
    return m_fPaddingLeft;
  }

  /**
   * Sets the left padding.
   *
   * @param fPaddingLeft
   *        left padding.
   */
  public void setPaddingLeft (final float fPaddingLeft)
  {
    this.m_fPaddingLeft = fPaddingLeft;
  }

  /**
   * @return the right padding
   */
  public float getPaddingRight ()
  {
    return m_fPaddingRight;
  }

  /**
   * Sets the right padding.
   *
   * @param fPaddingRight
   *        right padding.
   */
  public void setPaddingRight (final float fPaddingRight)
  {
    this.m_fPaddingRight = fPaddingRight;
  }

  /**
   * @return the top padding
   */
  public float getPaddingTop ()
  {
    return m_fPaddingTop;
  }

  /**
   * Sets the top padding.
   *
   * @param fPaddingTop
   *        top padding.
   */
  public void setPaddingTop (final float fPaddingTop)
  {
    this.m_fPaddingTop = fPaddingTop;
  }

  /**
   * @return the bottom padding
   */
  public float getPaddingBottom ()
  {
    return m_fPaddingBottom;
  }

  /**
   * Sets the bottom padding.
   *
   * @param fPaddingBottom
   *        bottom padding.
   */
  public void setPaddingBottom (final float fPaddingBottom)
  {
    this.m_fPaddingBottom = fPaddingBottom;
  }

  /**
   * Sets the padding.
   *
   * @param fLeft
   *        left padding.
   * @param fRight
   *        right padding.
   * @param fTop
   *        top padding.
   * @param fBottom
   *        bottom padding.
   */
  public void setPadding (final float fLeft, final float fRight, final float fTop, final float fBottom)
  {
    setPaddingLeft (fLeft);
    setPaddingRight (fRight);
    setPaddingTop (fTop);
    setPaddingBottom (fBottom);
  }

  /**
   * @return the left margin
   */
  public float getMarginLeft ()
  {
    return m_fMarginLeft;
  }

  /**
   * Sets the left margin.
   *
   * @param fMarginLeft
   *        left margin.
   */
  public void setMarginLeft (final float fMarginLeft)
  {
    this.m_fMarginLeft = fMarginLeft;
  }

  /**
   * @return the right margin
   */
  public float getMarginRight ()
  {
    return m_fMarginRight;
  }

  /**
   * Sets the right margin.
   *
   * @param fMarginRight
   *        right margin.
   */
  public void setMarginRight (final float fMarginRight)
  {
    this.m_fMarginRight = fMarginRight;
  }

  /**
   * @return the top margin
   */
  public float getMarginTop ()
  {
    return m_fMarginTop;
  }

  /**
   * Sets the top margin.
   *
   * @param fMarginTop
   *        top margin.
   */
  public void setMarginTop (final float fMarginTop)
  {
    this.m_fMarginTop = fMarginTop;
  }

  /**
   * @return the bottom margin
   */
  public float getMarginBottom ()
  {
    return m_fMarginBottom;
  }

  /**
   * Sets the bottom margin.
   *
   * @param fMarginBottom
   *        bottom margin.
   */
  public void setMarginBottom (final float fMarginBottom)
  {
    this.m_fMarginBottom = fMarginBottom;
  }

  /**
   * Sets the margin.
   *
   * @param fLeft
   *        left margin.
   * @param fRight
   *        right margin.
   * @param fTop
   *        top margin.
   * @param fBottom
   *        bottom margin.
   */
  public void setMargin (final float fLeft, final float fRight, final float fTop, final float fBottom)
  {
    setMarginLeft (fLeft);
    setMarginRight (fRight);
    setMarginTop (fTop);
    setMarginBottom (fBottom);
  }

  @Override
  public Position getAbsolutePosition () throws IOException
  {
    return m_aAbsolutePosition;
  }

  /**
   * Sets the absolute position.
   *
   * @param aAbsolutePosition
   *        the absolute position to use, or <code>null</code>.
   */
  public void setAbsolutePosition (final Position aAbsolutePosition)
  {
    this.m_aAbsolutePosition = aAbsolutePosition;
  }

  @Override
  public float getMaxWidth ()
  {
    return m_fMaxWidth;
  }

  /**
   * Records the maximum width assigned to this frame by enclosing layouts. Subclasses are expected
   * to propagate the constraint to their inner children — that part needs the per-module
   * {@code Drawable} type.
   */
  protected void setMaxWidthInternal (final float fMaxWidth)
  {
    this.m_fMaxWidth = fMaxWidth;
  }
}
