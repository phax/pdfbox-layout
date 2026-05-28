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
  private float paddingLeft;
  private float paddingRight;
  private float paddingTop;
  private float paddingBottom;

  private float marginLeft;
  private float marginRight;
  private float marginTop;
  private float marginBottom;

  private Color borderColor;
  private Color backgroundColor;

  private float maxWidth = -1;

  private Float givenWidth;
  private Float givenHeight;

  private Position absolutePosition;

  protected AbstractFrame ()
  {}

  protected AbstractFrame (final Float width, final Float height)
  {
    this.givenWidth = width;
    this.givenHeight = height;
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
    return givenHeight;
  }

  /**
   * @return the width given to constrain the size of the shape.
   */
  protected Float getGivenWidth ()
  {
    return givenWidth;
  }

  /**
   * @return the color to use to draw the border.
   */
  public Color getBorderColor ()
  {
    return borderColor;
  }

  /**
   * Sets the color to use to draw the border.
   *
   * @param borderColor
   *        the border color.
   */
  public void setBorderColor (final Color borderColor)
  {
    this.borderColor = borderColor;
  }

  /**
   * @return the color to use to draw the background.
   */
  public Color getBackgroundColor ()
  {
    return backgroundColor;
  }

  /**
   * Sets the color to use to draw the background.
   *
   * @param backgroundColor
   *        the background color.
   */
  public void setBackgroundColor (final Color backgroundColor)
  {
    this.backgroundColor = backgroundColor;
  }

  /**
   * @return the left padding
   */
  public float getPaddingLeft ()
  {
    return paddingLeft;
  }

  /**
   * Sets the left padding.
   *
   * @param paddingLeft
   *        left padding.
   */
  public void setPaddingLeft (final float paddingLeft)
  {
    this.paddingLeft = paddingLeft;
  }

  /**
   * @return the right padding
   */
  public float getPaddingRight ()
  {
    return paddingRight;
  }

  /**
   * Sets the right padding.
   *
   * @param paddingRight
   *        right padding.
   */
  public void setPaddingRight (final float paddingRight)
  {
    this.paddingRight = paddingRight;
  }

  /**
   * @return the top padding
   */
  public float getPaddingTop ()
  {
    return paddingTop;
  }

  /**
   * Sets the top padding.
   *
   * @param paddingTop
   *        top padding.
   */
  public void setPaddingTop (final float paddingTop)
  {
    this.paddingTop = paddingTop;
  }

  /**
   * @return the bottom padding
   */
  public float getPaddingBottom ()
  {
    return paddingBottom;
  }

  /**
   * Sets the bottom padding.
   *
   * @param paddingBottom
   *        bottom padding.
   */
  public void setPaddingBottom (final float paddingBottom)
  {
    this.paddingBottom = paddingBottom;
  }

  /**
   * Sets the padding.
   *
   * @param left
   *        left padding.
   * @param right
   *        right padding.
   * @param top
   *        top padding.
   * @param bottom
   *        bottom padding.
   */
  public void setPadding (final float left, final float right, final float top, final float bottom)
  {
    setPaddingLeft (left);
    setPaddingRight (right);
    setPaddingTop (top);
    setPaddingBottom (bottom);
  }

  /**
   * @return the left margin
   */
  public float getMarginLeft ()
  {
    return marginLeft;
  }

  /**
   * Sets the left margin.
   *
   * @param marginLeft
   *        left margin.
   */
  public void setMarginLeft (final float marginLeft)
  {
    this.marginLeft = marginLeft;
  }

  /**
   * @return the right margin
   */
  public float getMarginRight ()
  {
    return marginRight;
  }

  /**
   * Sets the right margin.
   *
   * @param marginRight
   *        right margin.
   */
  public void setMarginRight (final float marginRight)
  {
    this.marginRight = marginRight;
  }

  /**
   * @return the top margin
   */
  public float getMarginTop ()
  {
    return marginTop;
  }

  /**
   * Sets the top margin.
   *
   * @param marginTop
   *        top margin.
   */
  public void setMarginTop (final float marginTop)
  {
    this.marginTop = marginTop;
  }

  /**
   * @return the bottom margin
   */
  public float getMarginBottom ()
  {
    return marginBottom;
  }

  /**
   * Sets the bottom margin.
   *
   * @param marginBottom
   *        bottom margin.
   */
  public void setMarginBottom (final float marginBottom)
  {
    this.marginBottom = marginBottom;
  }

  /**
   * Sets the margin.
   *
   * @param left
   *        left margin.
   * @param right
   *        right margin.
   * @param top
   *        top margin.
   * @param bottom
   *        bottom margin.
   */
  public void setMargin (final float left, final float right, final float top, final float bottom)
  {
    setMarginLeft (left);
    setMarginRight (right);
    setMarginTop (top);
    setMarginBottom (bottom);
  }

  @Override
  public Position getAbsolutePosition () throws IOException
  {
    return absolutePosition;
  }

  /**
   * Sets the absolute position.
   *
   * @param absolutePosition
   *        the absolute position to use, or <code>null</code>.
   */
  public void setAbsolutePosition (final Position absolutePosition)
  {
    this.absolutePosition = absolutePosition;
  }

  @Override
  public float getMaxWidth ()
  {
    return maxWidth;
  }

  /**
   * Records the maximum width assigned to this frame by enclosing layouts. Subclasses are expected
   * to propagate the constraint to their inner children — that part needs the per-module
   * {@code Drawable} type.
   */
  protected void setMaxWidthInternal (final float maxWidth)
  {
    this.maxWidth = maxWidth;
  }
}
