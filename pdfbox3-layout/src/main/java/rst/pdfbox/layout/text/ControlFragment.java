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
public class ControlFragment implements TextFragment
{

  protected static FontDescriptor DEFAULT_FONT_DESCRIPTOR = newDefaultFontDescriptor ();

  private static FontDescriptor newDefaultFontDescriptor ()
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
    DEFAULT_FONT_DESCRIPTOR = newDefaultFontDescriptor ();
  }

  private String name;
  private String text;
  private FontDescriptor fontDescriptor;
  private Color color;

  protected ControlFragment (final String text, final FontDescriptor fontDescriptor)
  {
    this (null, text, fontDescriptor, Color.black);
  }

  protected ControlFragment (final String name,
                             final String text,
                             final FontDescriptor fontDescriptor,
                             final Color color)
  {
    this.name = name;
    if (this.name == null)
    {
      this.name = getClass ().getSimpleName ();
    }
    this.text = text;
    this.fontDescriptor = fontDescriptor;
    this.color = color;
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
    return fontDescriptor;
  }

  protected String getName ()
  {
    return name;
  }

  @Override
  public String getText ()
  {
    return text;
  }

  @Override
  public Color getColor ()
  {
    return color;
  }

  @Override
  public String toString ()
  {
    return "ControlFragment [" + name + "]";
  }

}
