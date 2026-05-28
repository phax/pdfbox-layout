package rst.pdfbox.layout.util;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;

import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.annotations.Annotations.HyperlinkAnnotation.ELinkStyle;

/**
 * Provide compatible methods for API changes from pdfbox 1x to 3x.
 */
public class CompatibilityHelper
{

  private final static String BULLET = "•";
  private final static String DOUBLE_ANGLE = "»";

  private static final String IMAGE_CACHE = "IMAGE_CACHE";
  private static Map <PDDocument, Map <String, Map <?, ?>>> s_aDocumentCaches = new WeakHashMap <> ();

  /**
   * Returns the bullet character for the given level. Actually only two bullets are used for odd
   * and even levels. For odd levels the {@link #BULLET bullet} character is used, for even it is
   * the {@link #DOUBLE_ANGLE double angle}. You may customize this by setting the system properties
   * <code>pdfbox.layout.bullet.odd</code> and/or <code>pdfbox.layout.bullet.even</code>.
   *
   * @param nLevel
   *        the level to return the bullet for.
   * @return the bullet character for the leve.
   */
  public static String getBulletCharacter (final int nLevel)
  {
    if (nLevel % 2 == 1)
    {
      return System.getProperty ("pdfbox.layout.bullet.odd", BULLET);
    }
    return System.getProperty ("pdfbox.layout.bullet.even", DOUBLE_ANGLE);
  }

  public static void clip (final PDPageContentStream aContentStream) throws IOException
  {
    aContentStream.clip ();
  }

  public static void transform (final PDPageContentStream aContentStream,
                                final float a,
                                final float b,
                                final float c,
                                final float d,
                                final float e,
                                final float f) throws IOException
  {
    aContentStream.transform (new Matrix (a, b, c, d, e, f));
  }

  public static void curveTo (final PDPageContentStream aContentStream,
                              final float x1,
                              final float y1,
                              final float x2,
                              final float y2,
                              final float x3,
                              final float y3) throws IOException
  {
    aContentStream.curveTo (x1, y1, x2, y2, x3, y3);
  }

  public static void curveTo1 (final PDPageContentStream aContentStream,
                               final float x1,
                               final float y1,
                               final float x3,
                               final float y3) throws IOException
  {
    aContentStream.curveTo1 (x1, y1, x3, y3);
  }

  public static void fillNonZero (final PDPageContentStream aContentStream) throws IOException
  {
    aContentStream.fill ();
  }

  public static void showText (final PDPageContentStream aContentStream, final String sText) throws IOException
  {
    aContentStream.showText (sText);
  }

  public static void setTextTranslation (final PDPageContentStream aContentStream, final float x, final float y)
                                                                                                                 throws IOException
  {
    aContentStream.setTextMatrix (Matrix.getTranslateInstance (x, y));
  }

  public static void moveTextPosition (final PDPageContentStream aContentStream, final float x, final float y)
                                                                                                               throws IOException
  {
    // PDFBox 3 forbids cm (concatenate-CTM) inside text objects. Use Tm
    // (setTextMatrix) with absolute coordinates instead. Callers (rewritten
    // by adaptToPdfBox3) pass absolute x/y rather than deltas.
    aContentStream.setTextMatrix (Matrix.getTranslateInstance (x, y));
  }

  public static PDPageContentStream createAppendablePDPageContentStream (final PDDocument aPdDocument,
                                                                         final PDPage aPage) throws IOException
  {
    return new PDPageContentStream (aPdDocument, aPage, PDPageContentStream.AppendMode.APPEND, true);
  }

  public static void drawImage (final BufferedImage aImage,
                                final PDDocument aDocument,
                                final PDPageContentStream aContentStream,
                                final Position aUpperLeft,
                                final float fWidth,
                                final float fHeight) throws IOException
  {
    final PDImageXObject aCachedImage = _getCachedImage (aDocument, aImage);
    final float x = aUpperLeft.getX ();
    final float y = aUpperLeft.getY () - fHeight;
    aContentStream.drawImage (aCachedImage, x, y, fWidth, fHeight);
  }

  public static int getPageRotation (final PDPage aPage)
  {
    return aPage.getRotation ();
  }

  /**
   * Renders the given page as an RGB image.
   *
   * @param aDocument
   *        the document containing the page.
   * @param nPageIndex
   *        the index of the page to render.
   * @param nResolution
   *        the image resolution.
   * @return the rendered image
   * @throws IOException
   *         by pdfbox
   */
  public static BufferedImage createImageFromPage (final PDDocument aDocument,
                                                   final int nPageIndex,
                                                   final int nResolution) throws IOException
  {
    final PDFRenderer aPdfRenderer = new PDFRenderer (aDocument);
    return aPdfRenderer.renderImageWithDPI (nPageIndex, nResolution, ImageType.RGB);
  }

  public static PDAnnotationLink createLink (final PDPage aPage,
                                             final PDRectangle aRect,
                                             final Color aColor,
                                             final ELinkStyle eLinkStyle,
                                             final String sUri)
  {
    final PDAnnotationLink aPdLink = _createLink (aPage, aRect, aColor, eLinkStyle);

    final PDActionURI aActionUri = new PDActionURI ();
    aActionUri.setURI (sUri);
    aPdLink.setAction (aActionUri);
    return aPdLink;
  }

  public static PDAnnotationLink createLink (final PDPage aPage,
                                             final PDRectangle aRect,
                                             final Color aColor,
                                             final ELinkStyle eLinkStyle,
                                             final PDDestination aDestination)
  {
    final PDAnnotationLink aPdLink = _createLink (aPage, aRect, aColor, eLinkStyle);

    final PDActionGoTo aGotoAction = new PDActionGoTo ();
    aGotoAction.setDestination (aDestination);
    aPdLink.setAction (aGotoAction);
    return aPdLink;
  }

  /**
   * Sets the color in the annotation.
   *
   * @param aAnnotation
   *        the annotation.
   * @param aColor
   *        the color to set.
   */
  public static void setAnnotationColor (final PDAnnotation aAnnotation, final Color aColor)
  {
    aAnnotation.setColor (_toPDColor (aColor));
  }

  private static PDAnnotationLink _createLink (final PDPage aPage,
                                               final PDRectangle aRect,
                                               final Color aColor,
                                               final ELinkStyle eLinkStyle)
  {
    final PDAnnotationLink aPdLink = new PDAnnotationLink ();
    aPdLink.setBorderStyle (_toBorderStyle (eLinkStyle));
    final PDRectangle aRotatedRect = transformToPageRotation (aRect, aPage);
    aPdLink.setRectangle (aRotatedRect);
    setAnnotationColor (aPdLink, aColor);
    return aPdLink;
  }

  private static PDBorderStyleDictionary _toBorderStyle (final ELinkStyle eLinkStyle)
  {
    if (eLinkStyle == ELinkStyle.none)
    {
      return _getNoBorder ();
    }
    final PDBorderStyleDictionary aBorderStyle = new PDBorderStyleDictionary ();
    aBorderStyle.setStyle (PDBorderStyleDictionary.STYLE_UNDERLINE);
    return aBorderStyle;
  }

  private static PDColor _toPDColor (final Color aColor)
  {
    final float [] aComponents = { aColor.getRed () / 255f, aColor.getGreen () / 255f, aColor.getBlue () / 255f };
    return new PDColor (aComponents, PDDeviceRGB.INSTANCE);
  }

  /**
   * Return the quad points representation of the given rect.
   *
   * @param aRect
   *        the rectangle.
   * @return the quad points.
   */
  public static float [] toQuadPoints (final PDRectangle aRect)
  {
    return toQuadPoints (aRect, 0, 0);
  }

  /**
   * Return the quad points representation of the given rect.
   *
   * @param aRect
   *        the rectangle.
   * @param fXOffset
   *        the offset in x-direction to add.
   * @param fYOffset
   *        the offset in y-direction to add.
   * @return the quad points.
   */
  public static float [] toQuadPoints (final PDRectangle aRect, final float fXOffset, final float fYOffset)
  {
    final float [] aQuads = new float [8];
    aQuads[0] = aRect.getLowerLeftX () + fXOffset; // x1
    aQuads[1] = aRect.getUpperRightY () + fYOffset; // y1
    aQuads[2] = aRect.getUpperRightX () + fXOffset; // x2
    aQuads[3] = aQuads[1]; // y2
    aQuads[4] = aQuads[0]; // x3
    aQuads[5] = aRect.getLowerLeftY () + fYOffset; // y3
    aQuads[6] = aQuads[2]; // x4
    aQuads[7] = aQuads[5]; // y5
    return aQuads;
  }

  /**
   * Transform the quad points in order to match the page rotation
   *
   * @param aQuadPoints
   *        the quad points.
   * @param aPage
   *        the page.
   * @return the transformed quad points.
   */
  public static float [] transformToPageRotation (final float [] aQuadPoints, final PDPage aPage)
  {
    final AffineTransform aTransform = _transformToPageRotation (aPage);
    if (aTransform == null)
    {
      return aQuadPoints;
    }
    final float [] aRotatedPoints = new float [aQuadPoints.length];
    aTransform.transform (aQuadPoints, 0, aRotatedPoints, 0, 4);
    return aRotatedPoints;
  }

  /**
   * Transform the rectangle in order to match the page rotation
   *
   * @param aRect
   *        the rectangle.
   * @param aPage
   *        the page.
   * @return the transformed rectangle.
   */
  public static PDRectangle transformToPageRotation (final PDRectangle aRect, final PDPage aPage)
  {
    final AffineTransform aTransform = _transformToPageRotation (aPage);
    if (aTransform == null)
    {
      return aRect;
    }
    final float [] aPoints = { aRect.getLowerLeftX (),
                               aRect.getLowerLeftY (),
                               aRect.getUpperRightX (),
                               aRect.getUpperRightY () };
    final float [] aRotatedPoints = new float [4];
    aTransform.transform (aPoints, 0, aRotatedPoints, 0, 2);
    final PDRectangle aRotated = new PDRectangle ();
    aRotated.setLowerLeftX (aRotatedPoints[0]);
    aRotated.setLowerLeftY (aRotatedPoints[1]);
    aRotated.setUpperRightX (aRotatedPoints[2]);
    aRotated.setUpperRightY (aRotatedPoints[3]);
    return aRotated;
  }

  private static AffineTransform _transformToPageRotation (final PDPage aPage)
  {
    final int nPageRotation = getPageRotation (aPage);
    if (nPageRotation == 0)
    {
      return null;
    }
    final float fPageWidth = aPage.getMediaBox ().getHeight ();
    final float fPageHeight = aPage.getMediaBox ().getWidth ();
    final AffineTransform aTransform = new AffineTransform ();
    aTransform.rotate (nPageRotation * Math.PI / 180, fPageHeight / 2, fPageWidth / 2);
    final double dOffset = Math.abs (fPageHeight - fPageWidth) / 2;
    aTransform.translate (-dOffset, dOffset);
    return aTransform;
  }

  private static PDBorderStyleDictionary _getNoBorder ()
  {
    // Each call returns a fresh instance: a single PDBorderStyleDictionary
    // wraps a COSDictionary that becomes tied to the first PDDocument it is
    // attached to. Sharing it across documents (e.g. when ExampleTest runs
    // many example main() methods in one JVM) leaks PDF object state and
    // makes rendering non-deterministic across tests.
    final PDBorderStyleDictionary aNoBorder = new PDBorderStyleDictionary ();
    aNoBorder.setWidth (0);
    return aNoBorder;
  }

  private static synchronized Map <String, Map <?, ?>> _getDocumentCache (final PDDocument aDocument)
  {
    Map <String, Map <?, ?>> aCache = s_aDocumentCaches.get (aDocument);
    if (aCache == null)
    {
      aCache = new HashMap <> ();
      s_aDocumentCaches.put (aDocument, aCache);
    }
    return aCache;
  }

  private static synchronized Map <BufferedImage, PDImageXObject> _getImageCache (final PDDocument aDocument)
  {
    final Map <String, Map <?, ?>> aDocumentCache = _getDocumentCache (aDocument);
    @SuppressWarnings ("unchecked")
    Map <BufferedImage, PDImageXObject> aImageCache = (Map <BufferedImage, PDImageXObject>) aDocumentCache.get (IMAGE_CACHE);
    if (aImageCache == null)
    {
      aImageCache = new HashMap <> ();
      aDocumentCache.put (IMAGE_CACHE, aImageCache);
    }
    return aImageCache;
  }

  private static synchronized PDImageXObject _getCachedImage (final PDDocument aDocument, final BufferedImage aImage)
                                                                                                                      throws IOException
  {
    final Map <BufferedImage, PDImageXObject> aImageCache = _getImageCache (aDocument);
    PDImageXObject aPdxObjectImage = aImageCache.get (aImage);
    if (aPdxObjectImage == null)
    {
      aPdxObjectImage = LosslessFactory.createFromImage (aDocument, aImage);
      aImageCache.put (aImage, aPdxObjectImage);
    }
    return aPdxObjectImage;
  }

}
