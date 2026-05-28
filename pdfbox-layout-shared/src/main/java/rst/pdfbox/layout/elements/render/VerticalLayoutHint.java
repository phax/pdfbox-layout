package rst.pdfbox.layout.elements.render;

import rst.pdfbox.layout.text.EAlignment;

/**
 * Layout hint for the {@link VerticalLayout}. You may specify margins to define some extra space
 * around the drawable. If there is still some extra space available vertically, the alignment
 * decides where to position the drawable. The {@link #isResetY() reset Y} indicates if the Y
 * postion should be reset to the value before drawing. Be aware that this only applies to the
 * current page where the remainder of the element has been drawn to. Means, if the elemenent spawns
 * multiple pages, the position is reset to the begin of the last page.
 */
public class VerticalLayoutHint implements ILayoutHint
{
  public static final VerticalLayoutHint LEFT = new VerticalLayoutHint (EAlignment.Left);
  public static final VerticalLayoutHint CENTER = new VerticalLayoutHint (EAlignment.Center);
  public static final VerticalLayoutHint RIGHT = new VerticalLayoutHint (EAlignment.Right);

  private final EAlignment m_eAlignment;
  private final float m_fMarginLeft;
  private final float m_fMarginRight;
  private final float m_fMarginTop;
  private final float m_fMarginBottom;
  private final boolean m_bResetY;

  /**
   * Creates a layout hint with {@link EAlignment#Left left alignment}.
   */
  public VerticalLayoutHint ()
  {
    this (EAlignment.Left);
  }

  /**
   * Creates a layout hint with the given alignment.
   *
   * @param eAlignment
   *        the element alignment.
   */
  public VerticalLayoutHint (final EAlignment eAlignment)
  {
    this (eAlignment, 0, 0, 0, 0);
  }

  /**
   * Creates a layout hint with the given alignment and margins.
   *
   * @param eAlignment
   *        the element alignment.
   * @param fMarginLeft
   *        the left alignment.
   * @param fMarginRight
   *        the right alignment.
   * @param fMarginTop
   *        the top alignment.
   * @param fMarginBottom
   *        the bottom alignment.
   */
  public VerticalLayoutHint (final EAlignment eAlignment,
                             final float fMarginLeft,
                             final float fMarginRight,
                             final float fMarginTop,
                             final float fMarginBottom)
  {
    this (eAlignment, fMarginLeft, fMarginRight, fMarginTop, fMarginBottom, false);
  }

  /**
   * Creates a layout hint with the given alignment and margins.
   *
   * @param eAlignment
   *        the element alignment.
   * @param fMarginLeft
   *        the left alignment.
   * @param fMarginRight
   *        the right alignment.
   * @param fMarginTop
   *        the top alignment.
   * @param fMarginBottom
   *        the bottom alignment.
   * @param bResetY
   *        if <code>true</code>, the y coordinate will be reset to the point before layouting the
   *        element.
   */
  public VerticalLayoutHint (final EAlignment eAlignment,
                             final float fMarginLeft,
                             final float fMarginRight,
                             final float fMarginTop,
                             final float fMarginBottom,
                             final boolean bResetY)
  {
    this.m_eAlignment = eAlignment;
    this.m_fMarginLeft = fMarginLeft;
    this.m_fMarginRight = fMarginRight;
    this.m_fMarginTop = fMarginTop;
    this.m_fMarginBottom = fMarginBottom;
    this.m_bResetY = bResetY;
  }

  public EAlignment getAlignment ()
  {
    return m_eAlignment;
  }

  public float getMarginLeft ()
  {
    return m_fMarginLeft;
  }

  public float getMarginRight ()
  {
    return m_fMarginRight;
  }

  public float getMarginTop ()
  {
    return m_fMarginTop;
  }

  public float getMarginBottom ()
  {
    return m_fMarginBottom;
  }

  public boolean isResetY ()
  {
    return m_bResetY;
  }

  @Override
  public String toString ()
  {
    return "VerticalLayoutHint [alignment=" +
           m_eAlignment +
           ", marginLeft=" +
           m_fMarginLeft +
           ", marginRight=" +
           m_fMarginRight +
           ", marginTop=" +
           m_fMarginTop +
           ", marginBottom=" +
           m_fMarginBottom +
           ", resetY=" +
           m_bResetY +
           "]";
  }

  /**
   * @return a {@link VerticalLayoutHintBuilder} for creating a {@link VerticalLayoutHint} using a
   *         fluent API.
   */
  public static VerticalLayoutHintBuilder builder ()
  {
    return new VerticalLayoutHintBuilder ();
  }

  /**
   * A builder for creating a {@link VerticalLayoutHint} using a fluent API.
   */
  public static class VerticalLayoutHintBuilder
  {
    protected EAlignment m_eAlignment = EAlignment.Left;
    protected float m_fMarginLeft = 0;
    protected float m_fMarginRight = 0;
    protected float m_fMarginTop = 0;
    protected float m_fMarginBottom = 0;
    protected boolean m_bResetY = false;

    public VerticalLayoutHintBuilder alignment (final EAlignment eAlignment)
    {
      this.m_eAlignment = eAlignment;
      return this;
    }

    public VerticalLayoutHintBuilder marginLeft (final float fMarginLeft)
    {
      this.m_fMarginLeft = fMarginLeft;
      return this;
    }

    public VerticalLayoutHintBuilder marginRight (final float fMarginRight)
    {
      this.m_fMarginRight = fMarginRight;
      return this;
    }

    public VerticalLayoutHintBuilder marginTop (final float fMarginTop)
    {
      this.m_fMarginTop = fMarginTop;
      return this;
    }

    public VerticalLayoutHintBuilder marginBottom (final float fMarginBottom)
    {
      this.m_fMarginBottom = fMarginBottom;
      return this;
    }

    public VerticalLayoutHintBuilder margins (final float fMarginLeft, final float fMarginRight, final float fMarginTop, final float fMarginBottom)
    {
      this.m_fMarginLeft = fMarginLeft;
      this.m_fMarginRight = fMarginRight;
      this.m_fMarginTop = fMarginTop;
      this.m_fMarginBottom = fMarginBottom;
      return this;
    }

    public VerticalLayoutHintBuilder resetY (final boolean bResetY)
    {
      this.m_bResetY = bResetY;
      return this;
    }

    public VerticalLayoutHint build ()
    {
      return new VerticalLayoutHint (m_eAlignment, m_fMarginLeft, m_fMarginRight, m_fMarginTop, m_fMarginBottom, m_bResetY);
    }

  }

}
