package rst.pdfbox.layout.elements;

import org.apache.pdfbox.pdmodel.common.PDRectangle;

import rst.pdfbox.layout.elements.render.VerticalLayout;
import rst.pdfbox.layout.text.Constants;

/**
 * Defines the size and orientation of a page. The default is A4 portrait without margins.
 */
public class PageFormat implements IElement
{

  private final float m_fMarginLeft;
  private final float m_fMarginRight;
  private final float m_fMarginTop;
  private final float m_fMarginBottom;
  private final PDRectangle m_aMediaBox;
  private final EOrientation m_eOrientation;
  private final int m_nRotation;

  /**
   * Creates a PageFormat with A4 portrait without margins.
   */
  public PageFormat ()
  {
    this (Constants.A4);
  }

  /**
   * Creates a PageFormat with a given size and orientation portrait.
   *
   * @param aMediaBox
   *        the size.
   */
  public PageFormat (final PDRectangle aMediaBox)
  {
    this (aMediaBox, EOrientation.Portrait);
  }

  /**
   * Creates a PageFormat with a given size and orientation.
   *
   * @param aMediaBox
   *        the size.
   * @param eOrientation
   *        the orientation.
   */
  public PageFormat (final PDRectangle aMediaBox, final EOrientation eOrientation)
  {
    this (aMediaBox, eOrientation, 0, 0, 0, 0);
  }

  /**
   * Creates a Document based on the given media box and margins. By default, a
   * {@link VerticalLayout} is used.
   *
   * @param aMediaBox
   *        the media box to use.
   * @param eOrientation
   *        the orientation to use.
   * @param fMarginLeft
   *        the left margin
   * @param fMarginRight
   *        the right margin
   * @param fMarginTop
   *        the top margin
   * @param fMarginBottom
   *        the bottom margin
   */
  public PageFormat (final PDRectangle aMediaBox,
                     final EOrientation eOrientation,
                     final float fMarginLeft,
                     final float fMarginRight,
                     final float fMarginTop,
                     final float fMarginBottom)
  {
    this (aMediaBox, eOrientation, 0, fMarginLeft, fMarginRight, fMarginTop, fMarginBottom);
  }

  /**
   * Creates a Document based on the given media box and margins. By default, a
   * {@link VerticalLayout} is used.
   *
   * @param aMediaBox
   *        the media box to use.
   * @param eOrientation
   *        the orientation to use.
   * @param nRotation
   *        the rotation to apply to the page after rendering.
   * @param fMarginLeft
   *        the left margin
   * @param fMarginRight
   *        the right margin
   * @param fMarginTop
   *        the top margin
   * @param fMarginBottom
   *        the bottom margin
   */
  public PageFormat (final PDRectangle aMediaBox,
                     final EOrientation eOrientation,
                     final int nRotation,
                     final float fMarginLeft,
                     final float fMarginRight,
                     final float fMarginTop,
                     final float fMarginBottom)
  {
    this.m_aMediaBox = aMediaBox;
    this.m_eOrientation = eOrientation;
    this.m_nRotation = nRotation;
    this.m_fMarginLeft = fMarginLeft;
    this.m_fMarginRight = fMarginRight;
    this.m_fMarginTop = fMarginTop;
    this.m_fMarginBottom = fMarginBottom;
  }

  /**
   * @return the orientation to use.
   */
  public EOrientation getOrientation ()
  {
    if (m_eOrientation != null)
    {
      return m_eOrientation;
    }
    if (getMediaBox ().getWidth () > getMediaBox ().getHeight ())
    {
      return EOrientation.Landscape;
    }
    return EOrientation.Portrait;
  }

  /**
   * @return the rotation to apply to the page after rendering.
   */
  public int getRotation ()
  {
    return m_nRotation;
  }

  /**
   * @return the left document margin.
   */
  public float getMarginLeft ()
  {
    return m_fMarginLeft;
  }

  /**
   * @return the right document margin.
   */
  public float getMarginRight ()
  {
    return m_fMarginRight;
  }

  /**
   * @return the top document margin.
   */
  public float getMarginTop ()
  {
    return m_fMarginTop;
  }

  /**
   * @return the bottom document margin.
   */
  public float getMarginBottom ()
  {
    return m_fMarginBottom;
  }

  /**
   * @return the media box to use.
   */
  public PDRectangle getMediaBox ()
  {
    return m_aMediaBox;
  }

  /**
   * @return a page format builder. The default of the builder is A4 portrait without margins.
   */
  public static PageFormatBuilder with ()
  {
    return new PageFormatBuilder ();
  }

  public static class PageFormatBuilder
  {
    private float m_fMarginLeft;
    private float m_fMarginRight;
    private float m_fMarginTop;
    private float m_fMarginBottom;
    private PDRectangle m_aMediaBox = Constants.A4;
    private EOrientation m_eOrientation;
    private int m_nRotation;

    protected PageFormatBuilder ()
    {}

    /**
     * Actually builds the PageFormat.
     *
     * @return the resulting PageFormat.
     */
    public PageFormat build ()
    {
      return new PageFormat (m_aMediaBox, m_eOrientation, m_nRotation, m_fMarginLeft, m_fMarginRight, m_fMarginTop, m_fMarginBottom);
    }

    /**
     * Sets the left margin.
     *
     * @param fMarginLeft
     *        the left margin to use.
     * @return the builder.
     */
    public PageFormatBuilder marginLeft (final float fMarginLeft)
    {
      this.m_fMarginLeft = fMarginLeft;
      return this;
    }

    /**
     * Sets the right margin.
     *
     * @param fMarginRight
     *        the right margin to use.
     * @return the builder.
     */
    public PageFormatBuilder marginRight (final float fMarginRight)
    {
      this.m_fMarginRight = fMarginRight;
      return this;
    }

    /**
     * Sets the top margin.
     *
     * @param fMarginTop
     *        the top margin to use.
     * @return the builder.
     */
    public PageFormatBuilder marginTop (final float fMarginTop)
    {
      this.m_fMarginTop = fMarginTop;
      return this;
    }

    /**
     * Sets the bottom margin.
     *
     * @param fMarginBottom
     *        the bottom margin to use.
     * @return the builder.
     */
    public PageFormatBuilder marginBottom (final float fMarginBottom)
    {
      this.m_fMarginBottom = fMarginBottom;
      return this;
    }

    /**
     * Sets the margins.
     *
     * @param fMarginLeft
     *        the left margin to use.
     * @param fMarginRight
     *        the right margin to use.
     * @param fMarginTop
     *        the top margin to use.
     * @param fMarginBottom
     *        the bottom margin to use.
     * @return the builder.
     */
    public PageFormatBuilder margins (final float fMarginLeft,
                                      final float fMarginRight,
                                      final float fMarginTop,
                                      final float fMarginBottom)
    {
      this.m_fMarginLeft = fMarginLeft;
      this.m_fMarginRight = fMarginRight;
      this.m_fMarginTop = fMarginTop;
      this.m_fMarginBottom = fMarginBottom;
      return this;
    }

    /**
     * Sets the media box to the given size.
     *
     * @param aMediaBox
     *        the media box to use.
     * @return the builder.
     */
    public PageFormatBuilder mediaBox (final PDRectangle aMediaBox)
    {
      this.m_aMediaBox = aMediaBox;
      return this;
    }

    /**
     * Sets the media box to size {@link Constants#A0}.
     *
     * @return the builder.
     */
    public PageFormatBuilder A0 ()
    {
      this.m_aMediaBox = Constants.A0;
      return this;
    }

    /**
     * Sets the media box to size {@link Constants#A1}.
     *
     * @return the builder.
     */
    public PageFormatBuilder A1 ()
    {
      this.m_aMediaBox = Constants.A1;
      return this;
    }

    /**
     * Sets the media box to size {@link Constants#A2}.
     *
     * @return the builder.
     */
    public PageFormatBuilder A2 ()
    {
      this.m_aMediaBox = Constants.A2;
      return this;
    }

    /**
     * Sets the media box to size {@link Constants#A3}.
     *
     * @return the builder.
     */
    public PageFormatBuilder A3 ()
    {
      this.m_aMediaBox = Constants.A3;
      return this;
    }

    /**
     * Sets the media box to size {@link Constants#A4}.
     *
     * @return the builder.
     */
    public PageFormatBuilder A4 ()
    {
      this.m_aMediaBox = Constants.A4;
      return this;
    }

    /**
     * Sets the media box to size {@link Constants#A5}.
     *
     * @return the builder.
     */
    public PageFormatBuilder A5 ()
    {
      this.m_aMediaBox = Constants.A5;
      return this;
    }

    /**
     * Sets the media box to size {@link Constants#A6}.
     *
     * @return the builder.
     */
    public PageFormatBuilder A6 ()
    {
      this.m_aMediaBox = Constants.A6;
      return this;
    }

    /**
     * Sets the media box to size {@link Constants#Letter}.
     *
     * @return the builder.
     */
    public PageFormatBuilder letter ()
    {
      this.m_aMediaBox = Constants.Letter;
      return this;
    }

    /**
     * Sets the orientation to the given one.
     *
     * @param eOrientation
     *        the orientation to use.
     * @return the builder.
     */
    public PageFormatBuilder orientation (final EOrientation eOrientation)
    {
      this.m_eOrientation = eOrientation;
      return this;
    }

    /**
     * Sets the orientation to {@link EOrientation#Portrait}.
     *
     * @return the builder.
     */
    public PageFormatBuilder portrait ()
    {
      this.m_eOrientation = EOrientation.Portrait;
      return this;
    }

    /**
     * Sets the orientation to {@link EOrientation#Landscape}.
     *
     * @return the builder.
     */
    public PageFormatBuilder landscape ()
    {
      this.m_eOrientation = EOrientation.Landscape;
      return this;
    }

    /**
     * Sets the rotation to apply to the page after rendering.
     *
     * @param nAngle
     *        the angle to rotate.
     * @return the builder.
     */
    public PageFormatBuilder rotation (final int nAngle)
    {
      this.m_nRotation = nAngle;
      return this;
    }
  }

}
