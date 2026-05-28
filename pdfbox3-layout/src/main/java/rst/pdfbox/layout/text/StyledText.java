package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Base class representing drawable text styled with font, size, color etc.
 */
public class StyledText implements ITextFragment
{

  private final String m_sText;
  private final FontDescriptor m_aFontDescriptor;
  private final Color m_aColor;
  private final float m_fLeftMargin;
  private final float m_fRightMargin;
  private final float m_fBaselineOffset;

  /**
   * The cached (calculated) width of the text.
   */
  private Float m_aWidth = null;

  /**
   * Creates a styled text.
   *
   * @param sText
   *        the text to draw. Must not contain line feeds ('\n').
   * @param fSize
   *        the size of the font.
   * @param aFont
   *        the font to use.
   */
  public StyledText (final String sText, final float fSize, final PDFont aFont)
  {
    this (sText, fSize, aFont, Color.black);
  }

  /**
   * Creates a styled text.
   *
   * @param sText
   *        the text to draw. Must not contain line feeds ('\n').
   * @param fSize
   *        the size of the font.
   * @param aFont
   *        the font to use.
   * @param aColor
   *        the color to use.
   */
  public StyledText (final String sText, final float fSize, final PDFont aFont, final Color aColor)
  {
    this (sText, new FontDescriptor (aFont, fSize), aColor);
  }

  /**
   * Creates a styled text.
   *
   * @param sText
   *        the text to draw. Must not contain line feeds ('\n').
   * @param fSize
   *        the size of the font.
   * @param aFont
   *        the font to use.
   * @param aColor
   *        the color to use.
   * @param fBaselineOffset
   *        the offset of the baseline.
   */
  public StyledText (final String sText,
                     final float fSize,
                     final PDFont aFont,
                     final Color aColor,
                     final float fBaselineOffset)
  {
    this (sText, new FontDescriptor (aFont, fSize), aColor, fBaselineOffset, 0, 0);
  }

  /**
   * Creates a styled text.
   *
   * @param sText
   *        the text to draw. Must not contain line feeds ('\n').
   * @param aFontDescriptor
   *        the font to use.
   */
  public StyledText (final String sText, final FontDescriptor aFontDescriptor)
  {
    this (sText, aFontDescriptor, Color.black);
  }

  /**
   * Creates a styled text.
   *
   * @param sText
   *        the text to draw. Must not contain line feeds ('\n').
   * @param aFontDescriptor
   *        the font to use.
   * @param aColor
   *        the color to use.
   */
  public StyledText (final String sText, final FontDescriptor aFontDescriptor, final Color aColor)
  {
    this (sText, aFontDescriptor, aColor, 0, 0, 0);
  }

  /**
   * Creates a styled text.
   *
   * @param sText
   *        the text to draw. Must not contain line feeds ('\n').
   * @param aFontDescriptor
   *        the font to use.
   * @param aColor
   *        the color to use.
   * @param fBaselineOffset
   *        the offset of the baseline.
   * @param fLeftMargin
   *        the margin left to the text.
   * @param fRightMargin
   *        the margin right to the text.
   */
  public StyledText (final String sText,
                     final FontDescriptor aFontDescriptor,
                     final Color aColor,
                     final float fBaselineOffset,
                     final float fLeftMargin,
                     final float fRightMargin)
  {
    if (sText.contains ("\n"))
    {
      throw new IllegalArgumentException ("StyledText must not contain line breaks, use TextFragment.LINEBREAK for that");
    }
    if (fLeftMargin < 0)
    {
      throw new IllegalArgumentException ("leftMargin must be >= 0");
    }
    if (fRightMargin < 0)
    {
      throw new IllegalArgumentException ("rightMargin must be >= 0");
    }
    this.m_sText = sText;
    this.m_aFontDescriptor = aFontDescriptor;
    this.m_aColor = aColor;
    this.m_fLeftMargin = fLeftMargin;
    this.m_fRightMargin = fRightMargin;
    this.m_fBaselineOffset = fBaselineOffset;
  }

  /**
   * @return the text to draw.
   */
  @Override
  public String getText ()
  {
    return m_sText;
  }

  /**
   * @return the font to use to draw the text.
   */
  @Override
  public FontDescriptor getFontDescriptor ()
  {
    return m_aFontDescriptor;
  }

  @Override
  public float getWidth () throws IOException
  {
    if (m_aWidth == null)
    {
      m_aWidth = Float.valueOf (getFontDescriptor ().getSize () *
                                getFontDescriptor ().getFont ().getStringWidth (getText ()) /
                                1000 +
                                m_fLeftMargin +
                                m_fRightMargin);
    }
    return m_aWidth.floatValue ();
  }

  public float getWidthWithoutMargin () throws IOException
  {
    return getWidth () - m_fLeftMargin - m_fRightMargin;
  }

  @Override
  public float getHeight () throws IOException
  {
    return getFontDescriptor ().getSize ();
  }

  /**
   * @return the ascent of the associated font.
   * @throws IOException
   *         by pdfbox.
   */
  public float getAsent () throws IOException
  {
    return getFontDescriptor ().getSize () * getFontDescriptor ().getFont ().getFontDescriptor ().getAscent () / 1000;
  }

  public float getBaselineOffset ()
  {
    return m_fBaselineOffset;
  }

  @Override
  public Color getColor ()
  {
    return m_aColor;
  }

  /**
   * @return the margin left to the text represented by this object.
   */
  public float getLeftMargin ()
  {
    return m_fLeftMargin;
  }

  /**
   * @return the margin right to the text represented by this object.
   */
  public float getRightMargin ()
  {
    return m_fRightMargin;
  }

  /**
   * @return indicates if this text has margin.
   */
  public boolean hasMargin ()
  {
    return getLeftMargin () != 0 || getRightMargin () != 0;
  }

  /**
   * @return converts this text to a sequence.
   */
  public ITextSequence asSequence ()
  {
    final TextLine aLine = new TextLine ();
    aLine.add (this);
    return aLine;
  }

  public StyledText inheritAttributes (final String sText)
  {
    return inheritAttributes (sText, getLeftMargin (), getRightMargin ());
  }

  public StyledText inheritAttributes (final String sText, final float fLeftMargin, final float fRightMargin)
  {
    return new StyledText (sText, getFontDescriptor (), getColor (), getBaselineOffset (), fLeftMargin, fRightMargin);
  }

  @Override
  public String toString ()
  {
    return "StyledText [text=" +
           m_sText +
           ", fontDescriptor=" +
           m_aFontDescriptor +
           ", width=" +
           m_aWidth +
           ", color=" +
           m_aColor +
           ", leftMargin=" +
           m_fLeftMargin +
           ", rightMargin=" +
           m_fRightMargin +
           ", baselineOffset=" +
           m_fBaselineOffset +
           "]";
  }

}
