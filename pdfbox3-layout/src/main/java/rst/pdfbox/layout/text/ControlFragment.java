package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * A control fragment has no drawable representation but is meant to control the text rendering.
 * <p>
 * pdfbox3 note: {@link #DEFAULT_FONT_DESCRIPTOR} wraps a {@link PDType1Font} whose
 * {@code COSDictionary} would otherwise be tied to whichever {@code PDDocument} renders first
 * (every {@code NewLine} and {@link Indent} uses this descriptor). Across a run with multiple
 * documents in one JVM that leaks PDF object state. {@link #reset()} replaces the descriptor with a
 * brand-new instance; ExampleTest.setUp calls it between tests.
 * </p>
 */
public class ControlFragment implements ITextFragment
{
  protected static FontDescriptor DEFAULT_FONT_DESCRIPTOR = _newDefaultFontDescriptor ();

  private static FontDescriptor _newDefaultFontDescriptor ()
  {
    return new FontDescriptor (new PDType1Font (Standard14Fonts.FontName.HELVETICA), 11);
  }

  /**
   * Replaces {@link #DEFAULT_FONT_DESCRIPTOR} with a fresh instance backed by a new
   * {@link PDType1Font}. Call between tests that share a JVM to avoid leaking PDF object state
   * across documents.
   */
  public static void reset ()
  {
    DEFAULT_FONT_DESCRIPTOR = _newDefaultFontDescriptor ();
  }

  private String m_sName;
  private final String m_sText;
  private final FontDescriptor m_aFontDescriptor;
  private final Color m_aColor;

  protected ControlFragment (final String sText, final FontDescriptor aFontDescriptor)
  {
    this (null, sText, aFontDescriptor, Color.black);
  }

  protected ControlFragment (final String sName,
                             final String sText,
                             final FontDescriptor aFontDescriptor,
                             final Color aColor)
  {
    this.m_sName = sName;
    if (this.m_sName == null)
    {
      this.m_sName = getClass ().getSimpleName ();
    }
    this.m_sText = sText;
    this.m_aFontDescriptor = aFontDescriptor;
    this.m_aColor = aColor;
  }

  @Override
  public float getWidth () throws IOException
  {
    return 0;
  }

  @Override
  public float getHeight () throws IOException
  {
    return getFontDescriptor () == null ? 0 : getFontDescriptor ().getSize ();
  }

  @Override
  public FontDescriptor getFontDescriptor ()
  {
    return m_aFontDescriptor;
  }

  protected String getName ()
  {
    return m_sName;
  }

  @Override
  public String getText ()
  {
    return m_sText;
  }

  @Override
  public Color getColor ()
  {
    return m_aColor;
  }

  @Override
  public String toString ()
  {
    return "ControlFragment [" + m_sName + "]";
  }

}
