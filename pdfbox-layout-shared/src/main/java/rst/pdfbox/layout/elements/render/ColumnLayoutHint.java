package rst.pdfbox.layout.elements.render;

import rst.pdfbox.layout.text.EAlignment;

/**
 * The column layout hint provides currently the same possibilities as the
 * {@link VerticalLayoutHint}. See there for more details.
 */
public class ColumnLayoutHint extends VerticalLayoutHint
{
  @SuppressWarnings ("hiding")
  public static final ColumnLayoutHint LEFT = new ColumnLayoutHint (EAlignment.Left);
  @SuppressWarnings ("hiding")
  public static final ColumnLayoutHint CENTER = new ColumnLayoutHint (EAlignment.Center);
  @SuppressWarnings ("hiding")
  public static final ColumnLayoutHint RIGHT = new ColumnLayoutHint (EAlignment.Right);

  /**
   * Creates a layout hint with {@link EAlignment#Left left alignment}.
   */
  public ColumnLayoutHint ()
  {}

  /**
   * Creates a layout hint with the given alignment.
   *
   * @param eAlignment
   *        the element alignment.
   */
  public ColumnLayoutHint (final EAlignment eAlignment)
  {
    super (eAlignment);
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
  public ColumnLayoutHint (final EAlignment eAlignment,
                           final float fMarginLeft,
                           final float fMarginRight,
                           final float fMarginTop,
                           final float fMarginBottom)
  {
    super (eAlignment, fMarginLeft, fMarginRight, fMarginTop, fMarginBottom);
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
  public ColumnLayoutHint (final EAlignment eAlignment,
                           final float fMarginLeft,
                           final float fMarginRight,
                           final float fMarginTop,
                           final float fMarginBottom,
                           final boolean bResetY)
  {
    super (eAlignment, fMarginLeft, fMarginRight, fMarginTop, fMarginBottom, bResetY);
  }

  /**
   * @return a {@link VerticalLayoutHintBuilder} for creating a {@link VerticalLayoutHint} using a
   *         fluent API.
   */
  public static ColumnLayoutHintBuilder builder ()
  {
    return new ColumnLayoutHintBuilder ();
  }

  /**
   * A builder for creating a {@link VerticalLayoutHint} using a fluent API.
   */
  public static class ColumnLayoutHintBuilder extends VerticalLayoutHintBuilder
  {

    @Override
    public ColumnLayoutHint build ()
    {
      return new ColumnLayoutHint (m_eAlignment,
                                   m_fMarginLeft,
                                   m_fMarginRight,
                                   m_fMarginTop,
                                   m_fMarginBottom,
                                   m_bResetY);
    }

  }
}
