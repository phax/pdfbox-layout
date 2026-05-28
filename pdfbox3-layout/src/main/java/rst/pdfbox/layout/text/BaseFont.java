package rst.pdfbox.layout.text;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

/**
 * In order to easy handling with fonts, this enum bundles the
 * plain/italic/bold/bold-italic variants of the three standard font types
 * Times, Courier and Helvetica.
 *
 * <p>pdfbox3 note: PDFBox 3 dropped {@code PDType1Font.HELVETICA} etc. as
 * static fields; the recommended replacement {@code new PDType1Font(...)} ties
 * the new {@code COSDictionary} to the first {@code PDDocument} that touches
 * it. Sharing one instance across documents (as ExampleTest does when it runs
 * many example main()s in a single JVM) leaks PDF object state and changes the
 * page resource numbering, which in turn changes line-wrap decisions.</p>
 *
 * <p>This version caches the 12 standard-font instances behind {@link #reset()}
 * — call that between examples that share a JVM (see ExampleTest.setUp).
 * Within a single test, you get the same instance every time, so layout
 * measurements are consistent.</p>
 */
public enum BaseFont {

    Times (Standard14Fonts.FontName.TIMES_ROMAN, Standard14Fonts.FontName.TIMES_BOLD,
	    Standard14Fonts.FontName.TIMES_ITALIC, Standard14Fonts.FontName.TIMES_BOLD_ITALIC),
    Courier (Standard14Fonts.FontName.COURIER, Standard14Fonts.FontName.COURIER_BOLD,
	    Standard14Fonts.FontName.COURIER_OBLIQUE, Standard14Fonts.FontName.COURIER_BOLD_OBLIQUE),
    Helvetica (Standard14Fonts.FontName.HELVETICA, Standard14Fonts.FontName.HELVETICA_BOLD,
	    Standard14Fonts.FontName.HELVETICA_OBLIQUE, Standard14Fonts.FontName.HELVETICA_BOLD_OBLIQUE);

    private final Standard14Fonts.FontName plainName;
    private final Standard14Fonts.FontName boldName;
    private final Standard14Fonts.FontName italicName;
    private final Standard14Fonts.FontName boldItalicName;

    private PDFont plainFont;
    private PDFont boldFont;
    private PDFont italicFont;
    private PDFont boldItalicFont;

    private BaseFont(Standard14Fonts.FontName plain, Standard14Fonts.FontName bold,
	    Standard14Fonts.FontName italic, Standard14Fonts.FontName boldItalic) {
	this.plainName = plain;
	this.boldName = bold;
	this.italicName = italic;
	this.boldItalicName = boldItalic;
	resetThis();
    }

    private void resetThis() {
	this.plainFont = new PDType1Font(plainName);
	this.boldFont = new PDType1Font(boldName);
	this.italicFont = new PDType1Font(italicName);
	this.boldItalicFont = new PDType1Font(boldItalicName);
    }

    /**
     * Re-creates the four cached {@link PDType1Font} instances for every enum
     * value. Intended for tests that run multiple example main()s in one JVM
     * and need to start each example with PDFont instances unattached to any
     * prior PDDocument.
     */
    public static void reset() {
	for (BaseFont bf : values()) {
	    bf.resetThis();
	}
    }

    public PDFont getPlainFont() {
	return plainFont;
    }

    public PDFont getBoldFont() {
	return boldFont;
    }

    public PDFont getItalicFont() {
	return italicFont;
    }

    public PDFont getBoldItalicFont() {
	return boldItalicFont;
    }

}
