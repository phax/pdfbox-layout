package rst.pdfbox.layout.shape;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * This is a container for all information needed to perform a stroke.
 */
public class Stroke
{

  /**
   * Enum for the PDF cap styles.
   */
  public static enum CapStyle
  {

    Cap (0),
    RoundCap (1),
    Square (2);

    private final int m_nValue;

    private CapStyle (final int nValue)
    {
      this.m_nValue = nValue;
    }

    public int value ()
    {
      return m_nValue;
    }
  }

  /**
   * Enum for the PDF join styles.
   */
  public static enum JoinStyle
  {

    Miter (0),
    Round (1),
    Bevel (2);

    private final int m_nValue;

    private JoinStyle (final int nValue)
    {
      this.m_nValue = nValue;
    }

    public int value ()
    {
      return m_nValue;
    }
  }

  /**
   * Describes a PDF dash pattern. See the PDF documentation for more information on that.
   */
  public static class DashPattern
  {

    private final float [] m_aPattern;
    private final float m_fPhase;

    /**
     * Creates a pattern with equal on and off length, starting with phase 0.
     *
     * @param fOnOff
     *        the length of the on/off part.
     */
    public DashPattern (final float fOnOff)
    {
      this (fOnOff, fOnOff, 0f);
    }

    /**
     * Creates a pattern with different on and off length, starting with phase 0.
     *
     * @param fOn
     *        the length of the off part.
     * @param fOff
     *        the length of the off part.
     */
    public DashPattern (final float fOn, final float fOff)
    {
      this (fOn, fOff, 0f);
    }

    /**
     * Creates a pattern with different on and off length, starting with the given phase .
     *
     * @param fOn
     *        the length of the off part.
     * @param fOff
     *        the length of the off part.
     * @param fPhase
     *        the phase to start the pattern with.
     */
    public DashPattern (final float fOn, final float fOff, final float fPhase)
    {
      this.m_aPattern = new float [] { fOn, fOff };
      this.m_fPhase = fPhase;
    }

    public float getOn ()
    {
      return m_aPattern[0];
    }

    public float getOff ()
    {
      return m_aPattern[1];
    }

    public float [] getPattern ()
    {
      return m_aPattern;
    }

    public float getPhase ()
    {
      return m_fPhase;
    }

  }

  private final CapStyle m_eCapStyle;
  private final JoinStyle m_eJoinStyle;
  private final DashPattern m_aDashPattern;
  private final float m_fLineWidth;

  /**
   * Creates a Stroke with line width 1, cap style {@link CapStyle#Cap}, join style
   * {@link JoinStyle#Miter}, and no dash pattern.
   */
  public Stroke ()
  {
    this (1f);
  }

  /**
   * Creates a Stroke with the given line width, cap style {@link CapStyle#Cap}, join style
   * {@link JoinStyle#Miter}, and no dash pattern.
   *
   * @param fLineWidth
   *        the line width.
   */
  public Stroke (final float fLineWidth)
  {
    this (CapStyle.Cap, JoinStyle.Miter, null, fLineWidth);
  }

  /**
   * Creates a stroke with the given attributes.
   *
   * @param eCapStyle
   *        the cap style.
   * @param eJoinStyle
   *        the join style.
   * @param aDashPattern
   *        the dash pattern.
   * @param fLineWidth
   *        the line width.
   */
  public Stroke (final CapStyle eCapStyle,
                 final JoinStyle eJoinStyle,
                 final DashPattern aDashPattern,
                 final float fLineWidth)
  {
    this.m_eCapStyle = eCapStyle;
    this.m_eJoinStyle = eJoinStyle;
    this.m_aDashPattern = aDashPattern;
    this.m_fLineWidth = fLineWidth;
  }

  public CapStyle getCapStyle ()
  {
    return m_eCapStyle;
  }

  public JoinStyle getJoinStyle ()
  {
    return m_eJoinStyle;
  }

  public DashPattern getDashPattern ()
  {
    return m_aDashPattern;
  }

  public float getLineWidth ()
  {
    return m_fLineWidth;
  }

  /**
   * Applies this stroke to the given content stream.
   *
   * @param aContentStream
   *        the content stream to apply this stroke to.
   * @throws IOException
   *         by PDFBox.
   */
  public void applyTo (final PDPageContentStream aContentStream) throws IOException
  {
    if (getCapStyle () != null)
    {
      aContentStream.setLineCapStyle (getCapStyle ().value ());
    }
    if (getJoinStyle () != null)
    {
      aContentStream.setLineJoinStyle (getJoinStyle ().value ());
    }
    if (getDashPattern () != null)
    {
      aContentStream.setLineDashPattern (getDashPattern ().getPattern (), getDashPattern ().getPhase ());
    }
    aContentStream.setLineWidth (getLineWidth ());
  }

  /**
   * Creates a stroke builder providing a fluent interface for creating a stroke.
   *
   * @return a stroke builder.
   */
  public static StrokeBuilder builder ()
  {
    return new StrokeBuilder ();
  }

  /**
   * A builder providing a fluent interface for creating a stroke.
   */
  public static class StrokeBuilder
  {
    private CapStyle m_eCapStyle = CapStyle.Cap;
    private JoinStyle m_eJoinStyle = JoinStyle.Miter;
    private DashPattern m_aDashPattern;
    private float m_fLineWidth = 1f;

    public StrokeBuilder capStyle (final CapStyle eCapStyle)
    {
      this.m_eCapStyle = eCapStyle;
      return this;
    }

    public StrokeBuilder joinStyle (final JoinStyle eJoinStyle)
    {
      this.m_eJoinStyle = eJoinStyle;
      return this;
    }

    public StrokeBuilder dashPattern (final DashPattern aDashPattern)
    {
      this.m_aDashPattern = aDashPattern;
      return this;
    }

    public StrokeBuilder lineWidth (final float fLineWidth)
    {
      this.m_fLineWidth = fLineWidth;
      return this;
    }

    public Stroke build ()
    {
      return new Stroke (m_eCapStyle, m_eJoinStyle, m_aDashPattern, m_fLineWidth);
    }
  }
}
