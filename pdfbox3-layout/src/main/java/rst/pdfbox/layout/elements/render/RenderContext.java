package rst.pdfbox.layout.elements.render;

import java.io.Closeable;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import rst.pdfbox.layout.elements.ControlElement;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.EOrientation;
import rst.pdfbox.layout.elements.IElement;
import rst.pdfbox.layout.elements.PageFormat;
import rst.pdfbox.layout.elements.PositionControl;
import rst.pdfbox.layout.elements.PositionControl.MarkPosition;
import rst.pdfbox.layout.elements.PositionControl.MovePosition;
import rst.pdfbox.layout.elements.PositionControl.SetPosition;
import rst.pdfbox.layout.text.IDrawContext;
import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.annotations.AnnotationDrawListener;
import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * The render context is a container providing all state of the current rendering process.
 */
public class RenderContext implements IRenderer, Closeable, IDrawContext, IDrawListener
{

  private final Document m_aDocument;
  private final PDDocument m_aPdDocument;
  private PDPage m_aPage;
  private int m_nPageIndex = 0;
  private PDPageContentStream m_aContentStream;
  private Position m_aCurrentPosition;
  private Position m_aMarkedPosition;
  private Position m_aMaxPositionOnPage;
  private ILayout m_aLayout = new VerticalLayout ();

  private PageFormat m_aNextPageFormat;
  private PageFormat m_aPageFormat;

  private final AnnotationDrawListener m_aAnnotationDrawListener;

  /**
   * Creates a render context.
   *
   * @param aDocument
   *        the document to render.
   * @param aPdDocument
   *        the underlying pdfbox document.
   * @throws IOException
   *         by pdfbox.
   */
  public RenderContext (final Document aDocument, final PDDocument aPdDocument) throws IOException
  {
    this.m_aDocument = aDocument;
    this.m_aPdDocument = aPdDocument;
    this.m_aPageFormat = aDocument.getPageFormat ();
    this.m_aAnnotationDrawListener = new AnnotationDrawListener (this);
    newPage ();
  }

  /**
   * @return the current {@link ILayout} used for rendering.
   */
  public ILayout getLayout ()
  {
    return m_aLayout;
  }

  /**
   * Sets the current {@link ILayout} used for rendering.
   *
   * @param aLayout
   *        the new layout.
   */
  public void setLayout (final ILayout aLayout)
  {
    this.m_aLayout = aLayout;
    resetPositionToLeftEndOfPage ();
  }

  /**
   * @return the orientation to use for the page. If no special {@link #setPageFormat(PageFormat)
   *         page format} is set, the {@link Document#getOrientation() document orientation} is
   *         used.
   * @deprecated use {@link #getPageFormat()} instead.
   */
  @Deprecated
  public EOrientation getOrientation ()
  {
    return getPageFormat ().getOrientation ();
  }

  /**
   * @return the media box to use for the page. If no special {@link #setPageFormat(PageFormat) page
   *         format} is set, the {@link Document#getMediaBox() document media box} is used.
   * @deprecated use {@link #getPageFormat()} instead.
   */
  @Deprecated
  public PDRectangle getMediaBox ()
  {
    return getPageFormat ().getMediaBox ();
  }

  public void setPageFormat (final PageFormat aPageFormat)
  {
    if (aPageFormat == null)
    {
      this.m_aPageFormat = m_aDocument.getPageFormat ();
    }
    else
    {
      this.m_aPageFormat = aPageFormat;
    }
  }

  public PageFormat getPageFormat ()
  {
    return m_aPageFormat;
  }

  /**
   * @return the upper left position in the document respecting the {@link Document document}
   *         margins.
   */
  public Position getUpperLeft ()
  {
    return new Position (getPageFormat ().getMarginLeft (), getPageHeight () - getPageFormat ().getMarginTop ());
  }

  /**
   * @return the lower right position in the document respecting the {@link Document document}
   *         margins.
   */
  public Position getLowerRight ()
  {
    return new Position (getPageWidth () - getPageFormat ().getMarginRight (), getPageFormat ().getMarginBottom ());
  }

  /**
   * @return the current rendering position in pdf coord space (origin in lower left corner).
   */
  public Position getCurrentPosition ()
  {
    return m_aCurrentPosition;
  }

  /**
   * @return the {@link PositionControl#MARKED_POSITION}.
   */
  public Position getMarkedPosition ()
  {
    return m_aMarkedPosition;
  }

  protected void setMarkedPosition (final Position aMarkedPosition)
  {
    this.m_aMarkedPosition = aMarkedPosition;
  }

  /**
   * Moves the {@link #getCurrentPosition() current position} relatively by the given offset.
   *
   * @param x
   *        to move horizontally.
   * @param y
   *        to move vertically.
   */
  public void movePositionBy (final float x, final float y)
  {
    m_aCurrentPosition = m_aCurrentPosition.add (x, y);
  }

  /**
   * Resets the position to {@link #getUpperLeft()}.
   */
  public void resetPositionToUpperLeft ()
  {
    m_aCurrentPosition = getUpperLeft ();
  }

  /**
   * Resets the position to the x of {@link #getUpperLeft()} while keeping the current y.
   */
  public void resetPositionToLeft ()
  {
    m_aCurrentPosition = new Position (getUpperLeft ().getX (), m_aCurrentPosition.getY ());
  }

  /**
   * Resets the position to the x of {@link #getUpperLeft()} and the y of
   * {@link #getMaxPositionOnPage()}.
   */
  protected void resetPositionToLeftEndOfPage ()
  {
    m_aCurrentPosition = new Position (getUpperLeft ().getX (), getMaxPositionOnPage ().getY ());
  }

  /**
   * @return the orientation of the current page
   */
  protected EOrientation getPageOrientation ()
  {
    if (getPageWidth () > getPageHeight ())
    {
      return EOrientation.Landscape;
    }
    return EOrientation.Portrait;
  }

  /**
   * @return <code>true</code> if the page is rotated by 90/270 degrees.
   */
  public boolean isPageTilted ()
  {
    return CompatibilityHelper.getPageRotation (m_aPage) == 90 || CompatibilityHelper.getPageRotation (m_aPage) == 270;
  }

  /**
   * @return the page' width, or - if {@link #isPageTilted() rotated} - the height.
   */
  public float getPageWidth ()
  {
    if (isPageTilted ())
    {
      return m_aPage.getMediaBox ().getHeight ();
    }
    return m_aPage.getMediaBox ().getWidth ();
  }

  /**
   * @return the page' height, or - if {@link #isPageTilted() rotated} - the width.
   */
  public float getPageHeight ()
  {
    if (isPageTilted ())
    {
      return m_aPage.getMediaBox ().getWidth ();
    }
    return m_aPage.getMediaBox ().getHeight ();
  }

  /**
   * @return the {@link #getPageWidth() width of the page} respecting the margins.
   */
  public float getWidth ()
  {
    return getPageWidth () - getPageFormat ().getMarginLeft () - getPageFormat ().getMarginRight ();
  }

  /**
   * @return the {@link #getPageHeight() height of the page} respecting the margins.
   */
  public float getHeight ()
  {
    return getPageHeight () - getPageFormat ().getMarginTop () - getPageFormat ().getMarginBottom ();
  }

  /**
   * @return the remaining height on the page.
   */
  public float getRemainingHeight ()
  {
    return getCurrentPosition ().getY () - getPageFormat ().getMarginBottom ();
  }

  /**
   * @return the document.
   */
  public Document getDocument ()
  {
    return m_aDocument;
  }

  /**
   * @return the PDDocument.
   */
  @Override
  public PDDocument getPdDocument ()
  {
    return m_aPdDocument;
  }

  @Override
  public PDPage getCurrentPage ()
  {
    return m_aPage;
  }

  @Override
  public PDPageContentStream getCurrentPageContentStream ()
  {
    return getContentStream ();
  }

  /**
   * @return the current PDPage.
   */
  @Deprecated
  public PDPage getPage ()
  {
    return getCurrentPage ();
  }

  /**
   * @return the current PDPageContentStream.
   */
  public PDPageContentStream getContentStream ()
  {
    return m_aContentStream;
  }

  /**
   * @return the current page index (starting from 0).
   */
  public int getPageIndex ()
  {
    return m_nPageIndex;
  }

  @Override
  public boolean render (final RenderContext aRenderContext, final IElement aElement, final ILayoutHint aLayoutHint)
                                                                                                                     throws IOException
  {
    final boolean bSuccess = getLayout ().render (aRenderContext, aElement, aLayoutHint);
    if (bSuccess)
    {
      return true;
    }
    if (aElement == ControlElement.NEWPAGE)
    {
      newPage ();
      return true;
    }
    if (aElement instanceof PositionControl)
    {
      return render ((PositionControl) aElement);
    }
    if (aElement instanceof PageFormat)
    {
      m_aNextPageFormat = (PageFormat) aElement;
      return true;
    }
    if (aElement instanceof ILayout)
    {
      setLayout ((ILayout) aElement);
      return true;
    }
    return false;
  }

  protected boolean render (final PositionControl aPositionControl)
  {
    if (aPositionControl instanceof MarkPosition)
    {
      setMarkedPosition (getCurrentPosition ());
      return true;
    }
    if (aPositionControl instanceof final SetPosition aSetPosition)
    {
      Float aX = aSetPosition.getX ();
      if (aX == PositionControl.MARKED_POSITION)
      {
        aX = Float.valueOf (getMarkedPosition ().getX ());
      }
      if (aX == null)
      {
        aX = Float.valueOf (getCurrentPosition ().getX ());
      }
      Float aY = aSetPosition.getY ();
      if (aY == PositionControl.MARKED_POSITION)
      {
        aY = Float.valueOf (getMarkedPosition ().getY ());
      }
      if (aY == null)
      {
        aY = Float.valueOf (getCurrentPosition ().getY ());
      }
      final Position aNewPosition = new Position (aX.floatValue (), aY.floatValue ());
      m_aCurrentPosition = aNewPosition;
      return true;
    }
    if (aPositionControl instanceof final MovePosition aMovePosition)
    {
      movePositionBy (aMovePosition.getX (), aMovePosition.getY ());
      return true;
    }
    return false;
  }

  /**
   * Triggers a new page.
   *
   * @throws IOException
   *         by pdfbox
   */
  public void newPage () throws IOException
  {
    if (closePage ())
    {
      ++m_nPageIndex;
    }
    if (m_aNextPageFormat != null)
    {
      setPageFormat (m_aNextPageFormat);
    }

    this.m_aPage = new PDPage (getPageFormat ().getMediaBox ());
    this.m_aPdDocument.addPage (m_aPage);
    this.m_aContentStream = CompatibilityHelper.createAppendablePDPageContentStream (m_aPdDocument, m_aPage);

    // fix orientation
    if (getPageOrientation () != getPageFormat ().getOrientation ())
    {
      if (isPageTilted ())
      {
        m_aPage.setRotation (0);
      }
      else
      {
        m_aPage.setRotation (90);
      }
    }

    if (isPageTilted ())
    {
      CompatibilityHelper.transform (m_aContentStream, 0, 1, -1, 0, getPageHeight (), 0);
    }

    resetPositionToUpperLeft ();
    resetMaxPositionOnPage ();
    m_aDocument.beforePage (this);
    m_aAnnotationDrawListener.beforePage (this);
  }

  /**
   * Closes the current page.
   *
   * @return <code>true</code> if the current page has not been closed before.
   * @throws IOException
   *         by pdfbox
   */
  public boolean closePage () throws IOException
  {
    if (m_aContentStream != null)
    {

      m_aAnnotationDrawListener.afterPage (this);
      m_aDocument.afterPage (this);

      if (getPageFormat ().getRotation () != 0)
      {
        final int nCurrentRotation = CompatibilityHelper.getPageRotation (getCurrentPage ());
        getCurrentPage ().setRotation (nCurrentRotation + getPageFormat ().getRotation ());
      }

      m_aContentStream.close ();
      m_aContentStream = null;
      return true;
    }
    return false;
  }

  @Override
  public void close () throws IOException
  {
    closePage ();
    m_aAnnotationDrawListener.afterRender ();
  }

  @Override
  public void drawn (final Object aDrawnObject, final Position aUpperLeft, final float fWidth, final float fHeight)
  {
    updateMaxPositionOnPage (aUpperLeft, fWidth, fHeight);
    m_aAnnotationDrawListener.drawn (aDrawnObject, aUpperLeft, fWidth, fHeight);
  }

  /**
   * Updates the maximum right resp. bottom position on the page.
   *
   * @param aUpperLeft
   * @param fWidth
   * @param fHeight
   */
  protected void updateMaxPositionOnPage (final Position aUpperLeft, final float fWidth, final float fHeight)
  {
    m_aMaxPositionOnPage = new Position (Math.max (m_aMaxPositionOnPage.getX (), aUpperLeft.getX () + fWidth),
                                         Math.min (m_aMaxPositionOnPage.getY (), aUpperLeft.getY () - fHeight));
  }

  /**
   * Resets the maximumn position to upper left.
   */
  protected void resetMaxPositionOnPage ()
  {
    m_aMaxPositionOnPage = getUpperLeft ();
  }

  /**
   * @return the maximum right and bottom position of all objects rendered on this page so far.
   */
  protected Position getMaxPositionOnPage ()
  {
    return m_aMaxPositionOnPage;
  }

}
