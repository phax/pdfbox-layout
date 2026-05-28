package rst.pdfbox.layout.text;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * In order to easy handling with fonts, this enum bundles the plain/italic/bold/bold-italic
 * variants of the three standard font types Times, Courier and Helvetica.
 * <p>
 * pdfbox3 note: PDFBox 3 dropped {@code PDType1Font.HELVETICA} etc. as static fields; the
 * recommended replacement {@code new PDType1Font(...)} ties the new {@code COSDictionary} to the
 * first {@code PDDocument} that touches it. Sharing one instance across documents (as ExampleTest
 * does when it runs many example main()s in a single JVM) leaks PDF object state and changes the
 * page resource numbering, which in turn changes line-wrap decisions.
 * </p>
 * <p>
 * This version caches the 12 standard-font instances behind {@link #reset()} — call that between
 * examples that share a JVM (see ExampleTest.setUp). Within a single test, you get the same
 * instance every time, so layout measurements are consistent.
 * </p>
 */
public enum EBaseFont
{

  Times (Standard14Fonts.FontName.TIMES_ROMAN,
         Standard14Fonts.FontName.TIMES_BOLD,
         Standard14Fonts.FontName.TIMES_ITALIC,
         Standard14Fonts.FontName.TIMES_BOLD_ITALIC),
  Courier (Standard14Fonts.FontName.COURIER,
           Standard14Fonts.FontName.COURIER_BOLD,
           Standard14Fonts.FontName.COURIER_OBLIQUE,
           Standard14Fonts.FontName.COURIER_BOLD_OBLIQUE),
  Helvetica (Standard14Fonts.FontName.HELVETICA,
             Standard14Fonts.FontName.HELVETICA_BOLD,
             Standard14Fonts.FontName.HELVETICA_OBLIQUE,
             Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE);

  private final Standard14Fonts.FontName m_ePlainName;
  private final Standard14Fonts.FontName m_eBoldName;
  private final Standard14Fonts.FontName m_eItalicName;
  private final Standard14Fonts.FontName m_eBoldItalicName;

  private PDFont m_aPlainFont;
  private PDFont m_aBoldFont;
  private PDFont m_aItalicFont;
  private PDFont m_aBoldItalicFont;

  private EBaseFont (final Standard14Fonts.FontName ePlain,
                     final Standard14Fonts.FontName eBold,
                     final Standard14Fonts.FontName eItalic,
                     final Standard14Fonts.FontName eBoldItalic)
  {
    this.m_ePlainName = ePlain;
    this.m_eBoldName = eBold;
    this.m_eItalicName = eItalic;
    this.m_eBoldItalicName = eBoldItalic;
    _resetThis ();
  }

  private void _resetThis ()
  {
    this.m_aPlainFont = new PDType1Font (m_ePlainName);
    this.m_aBoldFont = new PDType1Font (m_eBoldName);
    this.m_aItalicFont = new PDType1Font (m_eItalicName);
    this.m_aBoldItalicFont = new PDType1Font (m_eBoldItalicName);
  }

  /**
   * Re-creates the four cached {@link PDType1Font} instances for every enum value. Intended for
   * tests that run multiple example main()s in one JVM and need to start each example with PDFont
   * instances unattached to any prior PDDocument.
   */
  public static void reset ()
  {
    for (final EBaseFont eBf : values ())
    {
      eBf._resetThis ();
    }
  }

  public PDFont getPlainFont ()
  {
    return m_aPlainFont;
  }

  public PDFont getBoldFont ()
  {
    return m_aBoldFont;
  }

  public PDFont getItalicFont ()
  {
    return m_aItalicFont;
  }

  public PDFont getBoldItalicFont ()
  {
    return m_aBoldItalicFont;
  }

}
