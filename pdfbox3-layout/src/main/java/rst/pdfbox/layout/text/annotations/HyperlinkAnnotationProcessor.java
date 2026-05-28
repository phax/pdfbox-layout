package rst.pdfbox.layout.text.annotations;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

import rst.pdfbox.layout.text.IDrawContext;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.annotations.Annotations.AnchorAnnotation;
import rst.pdfbox.layout.text.annotations.Annotations.HyperlinkAnnotation;
import rst.pdfbox.layout.text.annotations.Annotations.HyperlinkAnnotation.ELinkStyle;
import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * This annotation processor handles both {@link HyperlinkAnnotation}s and
 * {@link AnchorAnnotation}s, and adds the needed hyperlink metadata to the PDF document.
 */
public class HyperlinkAnnotationProcessor implements AnnotationProcessor
{

  private final Map <String, PageAnchor> m_aAnchorMap = new HashMap <> ();
  private final Map <PDPage, List <Hyperlink>> m_aLinkMap = new HashMap <> ();

  @Override
  public void annotatedObjectDrawn (final IAnnotated aDrawnObject,
                                    final IDrawContext aDrawContext,
                                    final Position aUpperLeft,
                                    final float fWidth,
                                    final float fHeight) throws IOException
  {
    if (!(aDrawnObject instanceof final AnnotatedStyledText aAnnotatedText))
    {
      return;
    }
    handleHyperlinkAnnotations (aAnnotatedText, aDrawContext, aUpperLeft, fWidth, fHeight);
    handleAnchorAnnotations (aAnnotatedText, aDrawContext, aUpperLeft);
  }

  protected void handleAnchorAnnotations (final AnnotatedStyledText aAnnotatedText,
                                          final IDrawContext aDrawContext,
                                          final Position aUpperLeft)
  {
    final Iterable <AnchorAnnotation> aAnchorAnnotations = aAnnotatedText.getAnnotationsOfType (AnchorAnnotation.class);
    for (final AnchorAnnotation aAnchorAnnotation : aAnchorAnnotations)
    {
      m_aAnchorMap.put (aAnchorAnnotation.getAnchor (),
                        new PageAnchor (aDrawContext.getCurrentPage (), aUpperLeft.getX (), aUpperLeft.getY ()));
    }
  }

  protected void handleHyperlinkAnnotations (final AnnotatedStyledText aAnnotatedText,
                                             final IDrawContext aDrawContext,
                                             final Position aUpperLeft,
                                             final float fWidth,
                                             final float fHeight)
  {
    final Iterable <HyperlinkAnnotation> aHyperlinkAnnotations = aAnnotatedText.getAnnotationsOfType (HyperlinkAnnotation.class);
    for (final HyperlinkAnnotation aHyperlinkAnnotation : aHyperlinkAnnotations)
    {
      List <Hyperlink> aLinks = m_aLinkMap.get (aDrawContext.getCurrentPage ());
      if (aLinks == null)
      {
        aLinks = new ArrayList <> ();
        m_aLinkMap.put (aDrawContext.getCurrentPage (), aLinks);
      }
      final PDRectangle aBounds = new PDRectangle ();
      aBounds.setLowerLeftX (aUpperLeft.getX ());
      aBounds.setLowerLeftY (aUpperLeft.getY () - fHeight);
      aBounds.setUpperRightX (aUpperLeft.getX () + fWidth);
      aBounds.setUpperRightY (aUpperLeft.getY ());

      aLinks.add (new Hyperlink (aBounds,
                                 aAnnotatedText.getColor (),
                                 aHyperlinkAnnotation.getLinkStyle (),
                                 aHyperlinkAnnotation.getHyperlinkURI ()));
    }
  }

  @Override
  public void beforePage (final IDrawContext aDrawContext)
  {
    // nothing to do here
  }

  @Override
  public void afterPage (final IDrawContext aDrawContext)
  {
    // nothing to do here
  }

  @Override
  public void afterRender (final PDDocument aDocument) throws IOException
  {
    for (final Entry <PDPage, List <Hyperlink>> aEntry : m_aLinkMap.entrySet ())
    {
      final PDPage aPage = aEntry.getKey ();
      final List <Hyperlink> aLinks = aEntry.getValue ();
      for (final Hyperlink aHyperlink : aLinks)
      {
        PDAnnotationLink aPdLink = null;
        if (aHyperlink.getHyperlinkURI ().startsWith ("#"))
        {
          aPdLink = _createGotoLink (aHyperlink);
        }
        else
        {
          aPdLink = CompatibilityHelper.createLink (aPage,
                                                    aHyperlink.getRect (),
                                                    aHyperlink.getColor (),
                                                    aHyperlink.getLinkStyle (),
                                                    aHyperlink.getHyperlinkURI ());
        }
        aPage.getAnnotations ().add (aPdLink);
      }

    }
  }

  private PDAnnotationLink _createGotoLink (final Hyperlink aHyperlink)
  {
    final String sAnchor = aHyperlink.getHyperlinkURI ().substring (1);
    final PageAnchor aPageAnchor = m_aAnchorMap.get (sAnchor);
    if (aPageAnchor == null)
    {
      throw new IllegalArgumentException ("anchor named '" + sAnchor + "' not found");
    }
    final PDPageXYZDestination aXyzDestination = new PDPageXYZDestination ();
    aXyzDestination.setPage (aPageAnchor.getPage ());
    aXyzDestination.setLeft ((int) aPageAnchor.getX ());
    aXyzDestination.setTop ((int) aPageAnchor.getY ());
    return CompatibilityHelper.createLink (aPageAnchor.getPage (),
                                           aHyperlink.getRect (),
                                           aHyperlink.getColor (),
                                           aHyperlink.getLinkStyle (),
                                           aXyzDestination);
  }

  private static class PageAnchor
  {
    private final PDPage m_aPage;
    private final float m_fX;
    private final float m_fY;

    public PageAnchor (final PDPage aPage, final float fX, final float fY)
    {
      this.m_aPage = aPage;
      this.m_fX = fX;
      this.m_fY = fY;
    }

    public PDPage getPage ()
    {
      return m_aPage;
    }

    public float getX ()
    {
      return m_fX;
    }

    public float getY ()
    {
      return m_fY;
    }

    @Override
    public String toString ()
    {
      return "PageAnchor [page=" + m_aPage + ", x=" + m_fX + ", y=" + m_fY + "]";
    }

  }

  private static class Hyperlink
  {
    private final PDRectangle m_aRect;
    private final Color m_aColor;
    private final String m_sHyperlinkUri;
    private final ELinkStyle m_eLinkStyle;

    public Hyperlink (final PDRectangle aRect,
                      final Color aColor,
                      final ELinkStyle eLinkStyle,
                      final String sHyperlinkUri)
    {
      this.m_aRect = aRect;
      this.m_aColor = aColor;
      this.m_sHyperlinkUri = sHyperlinkUri;
      this.m_eLinkStyle = eLinkStyle;
    }

    public PDRectangle getRect ()
    {
      return m_aRect;
    }

    public Color getColor ()
    {
      return m_aColor;
    }

    public String getHyperlinkURI ()
    {
      return m_sHyperlinkUri;
    }

    public ELinkStyle getLinkStyle ()
    {
      return m_eLinkStyle;
    }

    @Override
    public String toString ()
    {
      return "Hyperlink [rect=" +
             m_aRect +
             ", color=" +
             m_aColor +
             ", hyperlinkUri=" +
             m_sHyperlinkUri +
             ", linkStyle=" +
             m_eLinkStyle +
             "]";
    }

  }

}
