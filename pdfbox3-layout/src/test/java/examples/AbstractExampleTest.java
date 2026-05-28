package examples;

import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.After;
import org.junit.Before;

import rst.pdfbox.layout.text.EBaseFont;
import rst.pdfbox.layout.text.ControlFragment;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters;
import rst.pdfbox.layout.text.annotations.AnnotationProcessorFactory;
import rst.pdfbox.layout.util.CompatibilityHelper;
import rst.pdfbox.layout.util.WordBreakerFactory;

/**
 * Shared infrastructure for the example test classes. Each example creates a
 * PDF in the working directory and then calls {@link #verifyPdf()} which
 * compares it page-by-page against the reference under
 * {@code /examples/pdf/<lowercaseclassname>.pdf}.
 */
public abstract class AbstractExampleTest
{

  protected File newPdf;

  @Before
  public void setUp () throws Exception
  {
    // reset test situation: examples like CustomAnnotation register
    // processors/factories into static lists that otherwise leak into
    // subsequent tests run in the same JVM and produce non-deterministic
    // rendering.
    System.clearProperty (WordBreakerFactory.WORD_BREAKER_CLASS_PROPERTY);
    AnnotationProcessorFactory.reset ();
    AnnotationCharacters.reset ();
    EBaseFont.reset ();
    ControlFragment.reset ();
  }

  @After
  public void tearDown () throws Exception
  {
    if (newPdf != null && newPdf.exists ())
    {
      if (Boolean.getBoolean ("recordReferences"))
      {
        try
        {
          final Path dst = Path.of (System.getProperty ("recordReferences.dir", "test-references/examples/pdf"),
                                    newPdf.getName ());
          Files.createDirectories (dst.getParent ());
          Files.copy (newPdf.toPath (), dst, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (final Exception ignore)
        {}
      }
      newPdf.deleteOnExit ();
    }
  }

  /**
   * Compares the generated {@code <lowercase-classname>.pdf} against the
   * checked-in reference.
   */
  protected void verifyPdf () throws Exception
  {
    String simpleName = getClass ().getSimpleName ();
    if (simpleName.endsWith ("Test"))
      simpleName = simpleName.substring (0, simpleName.length () - "Test".length ());
    final String pdfName = simpleName.toLowerCase () + ".pdf";
    newPdf = new File ("./" + pdfName);

    final InputStream oldPdf = this.getClass ().getResourceAsStream ("/examples/pdf/" + pdfName);
    assertNotNull (oldPdf);

    comparePdfs (newPdf, oldPdf);
  }

  private static BufferedImage toImage (final PDDocument document, final int pageIndex) throws IOException
  {
    return CompatibilityHelper.createImageFromPage (document, pageIndex, 175);
  }

  private static void comparePdfs (final File newPdf, final InputStream toCompareTo) throws IOException, AssertionError
  {
    try (PDDocument currentDoc = Loader.loadPDF (newPdf);
         PDDocument oldDoc = Loader.loadPDF (new RandomAccessReadBuffer (toCompareTo)))
    {

      if (currentDoc.getNumberOfPages () != oldDoc.getNumberOfPages ())
      {
        throw new AssertionError ("expected " + oldDoc.getNumberOfPages () +
                                  " pages, but is " + currentDoc.getNumberOfPages ());
      }

      for (int i = 0; i < oldDoc.getNumberOfPages (); i++)
      {
        final File diffFile = new File (newPdf.getAbsoluteFile () + ".diff.png");
        // Okay if not present
        diffFile.delete ();

        final BufferedImage currentPageImg = toImage (currentDoc, i);
        final BufferedImage oldPageImg = toImage (oldDoc, i);
        final BufferedImage diff = compareImage (currentPageImg, oldPageImg);
        if (diff != null)
        {
          ImageIO.write (diff, "png", diffFile);
          System.out.println ("Write diff to " + diffFile.getAbsolutePath ());
          throw new AssertionError ("page " + (i + 1) + " different, wrote diff image " + diffFile);
        }

      }
    }
  }

  public static BufferedImage compareImage (final BufferedImage img1, final BufferedImage img2) throws IOException
  {
    final double colorDistanceTolerance = 0.08;
    final int w = img1.getWidth ();
    final int h = img1.getHeight ();
    final int [] p1 = img1.getRGB (0, 0, w, h, null, 0, w);
    final int [] p2 = img2.getRGB (0, 0, w, h, null, 0, w);
    final BufferedImage out = new BufferedImage (w, h, BufferedImage.TYPE_INT_ARGB);
    boolean foundDiff = false;

    if (!(java.util.Arrays.equals (p1, p2)))
    {
      for (int i = 0; i < p1.length; i++)
      {
        if (normalizedRgbDistance (p1[i], p2[i]) > colorDistanceTolerance)
        {
          foundDiff = true;
          p1[i] = Color.red.getRGB ();
        }
      }
      out.setRGB (0, 0, w, h, p1, 0, w);
    }

    if (foundDiff)
      return out;

    return null;
  }

  private static double normalizedRgbDistance (final int one, final int other)
  {
    return normalizedDistance (new Color (one), new Color (other));
  }

  private static final double MAX_VECTOR_LENGTH = Math.sqrt (3.0 * 255.0 * 255.0);

  private static double normalizedDistance (final Color one, final Color other)
  {
    final int distanceR = one.getRed () - other.getRed ();
    final int distanceG = one.getGreen () - other.getGreen ();
    final int distanceB = one.getBlue () - other.getBlue ();

    final double distance = Math.sqrt (distanceR * distanceR + distanceG * distanceG + distanceB * distanceB);

    return distance / MAX_VECTOR_LENGTH;
  }
}
