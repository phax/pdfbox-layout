package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Control fragment that represents a indent in text.
 */
public class Indent extends ControlFragment
{

  /**
   * Constant for the indentation of 0.
   */
  public final static Indent UNINDENT = new Indent (0);

  protected float m_fIndentWidth = 4;
  protected ESpaceUnit m_eIndentUnit = ESpaceUnit.em;
  protected EAlignment m_eAlignment = EAlignment.Left;
  protected StyledText m_aStyledText;

  /**
   * Creates a new line with the given font descriptor.
   *
   * @param fIndentWidth
   *        the indentation.
   * @param eIndentUnit
   *        the indentation unit.
   * @throws IOException
   *         by pdfbox
   */
  public Indent (final float fIndentWidth, final ESpaceUnit eIndentUnit) throws IOException
  {
    this ("", fIndentWidth, eIndentUnit, DEFAULT_FONT_DESCRIPTOR, EAlignment.Left, Color.black);
  }

  /**
   * Creates a new line with the {@link ControlFragment#DEFAULT_FONT_DESCRIPTOR}'s font and the
   * given height.
   *
   * @param sLabel
   *        the label of the indentation.
   * @param fIndentWidth
   *        the indentation.
   * @param eIndentUnit
   *        the indentation unit.
   * @param fFontSize
   *        the font size, resp. the height of the new line.
   * @param aFont
   *        the font to use.
   * @throws IOException
   *         by pdfbox
   */
  public Indent (final String sLabel,
                 final float fIndentWidth,
                 final ESpaceUnit eIndentUnit,
                 final float fFontSize,
                 final PDFont aFont) throws IOException
  {
    this (sLabel, fIndentWidth, eIndentUnit, fFontSize, aFont, EAlignment.Left, Color.black);
  }

  /**
   * Creates a new line with the {@link ControlFragment#DEFAULT_FONT_DESCRIPTOR}'s font and the
   * given height.
   *
   * @param sLabel
   *        the label of the indentation.
   * @param fIndentWidth
   *        the indentation.
   * @param eIndentUnit
   *        the indentation unit.
   * @param fFontSize
   *        the font size, resp. the height of the new line.
   * @param aFont
   *        the font to use.
   * @param eAlignment
   *        the alignment of the label.
   * @throws IOException
   *         by pdfbox
   */
  public Indent (final String sLabel,
                 final float fIndentWidth,
                 final ESpaceUnit eIndentUnit,
                 final float fFontSize,
                 final PDFont aFont,
                 final EAlignment eAlignment) throws IOException
  {
    this (sLabel, fIndentWidth, eIndentUnit, fFontSize, aFont, eAlignment, Color.black);
  }

  /**
   * Creates a new line with the {@link ControlFragment#DEFAULT_FONT_DESCRIPTOR}'s font and the
   * given height.
   *
   * @param sLabel
   *        the label of the indentation.
   * @param fIndentWidth
   *        the indentation.
   * @param eIndentUnit
   *        the indentation unit.
   * @param fFontSize
   *        the font size, resp. the height of the new line.
   * @param aFont
   *        the font to use.
   * @param eAlignment
   *        the alignment of the label.
   * @param aColor
   *        the color to use.
   * @throws IOException
   *         by pdfbox
   */
  public Indent (final String sLabel,
                 final float fIndentWidth,
                 final ESpaceUnit eIndentUnit,
                 final float fFontSize,
                 final PDFont aFont,
                 final EAlignment eAlignment,
                 final Color aColor) throws IOException
  {
    this (sLabel, fIndentWidth, eIndentUnit, new FontDescriptor (aFont, fFontSize), eAlignment, aColor);
  }

  /**
   * Creates a new line with the given font descriptor.
   *
   * @param sLabel
   *        the label of the indentation.
   * @param fIndentWidth
   *        the indentation width.
   * @param eIndentUnit
   *        the indentation unit.
   * @param aFontDescriptor
   *        the font and size associated with this new line.
   * @param eAlignment
   *        the alignment of the label.
   * @param aColor
   *        the color to use.
   * @throws IOException
   *         by pdfbox
   */
  public Indent (final String sLabel,
                 final float fIndentWidth,
                 final ESpaceUnit eIndentUnit,
                 final FontDescriptor aFontDescriptor,
                 final EAlignment eAlignment,
                 final Color aColor) throws IOException
  {
    super ("INDENT", sLabel, aFontDescriptor, aColor);

    final float fIndent = _calculateIndent (fIndentWidth, eIndentUnit, aFontDescriptor);
    float fTextWidth = 0;
    if (sLabel != null && !sLabel.isEmpty ())
    {
      fTextWidth = aFontDescriptor.getSize () * aFontDescriptor.getFont ().getStringWidth (sLabel) / 1000f;
    }
    float fMarginLeft = 0;
    float fMarginRight = 0;
    if (fTextWidth < fIndent)
    {
      switch (eAlignment)
      {
        case Left:
          fMarginRight = fIndent - fTextWidth;
          break;
        case Right:
          fMarginLeft = fIndent - fTextWidth;
          break;
        default:
          fMarginLeft = (fIndent - fTextWidth) / 2f;
          fMarginRight = fMarginLeft;
          break;
      }
    }
    m_aStyledText = new StyledText (sLabel, getFontDescriptor (), getColor (), 0, fMarginLeft, fMarginRight);
  }

  /**
   * Directly creates an indent of the given width in pt.
   *
   * @param fIndentPt
   *        the indentation in pt.
   */
  public Indent (final float fIndentPt)
  {
    super ("", DEFAULT_FONT_DESCRIPTOR);
    m_aStyledText = new StyledText ("", getFontDescriptor (), getColor (), 0, fIndentPt, 0);
  }

  private float _calculateIndent (final float fIndentWidth,
                                  final ESpaceUnit eIndentUnit,
                                  final FontDescriptor aFontDescriptor) throws IOException
  {
    if (fIndentWidth < 0)
    {
      return 0;
    }
    return eIndentUnit.toPt (fIndentWidth, aFontDescriptor);
  }

  @Override
  public float getWidth () throws IOException
  {
    return m_aStyledText.getWidth ();
  }

  /**
   * @return a styled text representation of the indent.
   */
  public StyledText toStyledText ()
  {
    return m_aStyledText;
  }

  @Override
  public String toString ()
  {
    return "ControlFragment [" + getName () + ", " + m_aStyledText + "]";
  }

}
