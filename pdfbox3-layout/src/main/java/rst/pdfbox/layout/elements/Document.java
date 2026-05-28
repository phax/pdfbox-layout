package rst.pdfbox.layout.elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import rst.pdfbox.layout.elements.render.ILayout;
import rst.pdfbox.layout.elements.render.ILayoutHint;
import rst.pdfbox.layout.elements.render.IRenderListener;
import rst.pdfbox.layout.elements.render.IRenderer;
import rst.pdfbox.layout.elements.render.RenderContext;
import rst.pdfbox.layout.elements.render.VerticalLayout;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;

/**
 * The central class for creating a document.
 */
public class Document implements IRenderListener
{

  /**
   * A4 portrait without margins.
   */
  public final static PageFormat DEFAULT_PAGE_FORMAT = new PageFormat ();

  private final List <Entry <IElement, ILayoutHint>> m_aElements = new ArrayList <> ();
  private final List <IRenderer> m_aCustomRenderer = new CopyOnWriteArrayList <> ();
  private final List <IRenderListener> m_aRenderListener = new CopyOnWriteArrayList <> ();

  private PDDocument m_aPdDocument;
  private final PageFormat m_aPageFormat;

  /**
   * Creates a Document using the {@link #DEFAULT_PAGE_FORMAT}.
   */
  public Document ()
  {
    this (DEFAULT_PAGE_FORMAT);
  }

  /**
   * Creates a Document in A4 with orientation portrait and the given margins. By default, a
   * {@link VerticalLayout} is used.
   *
   * @param fMarginLeft
   *        the left margin
   * @param fMarginRight
   *        the right margin
   * @param fMarginTop
   *        the top margin
   * @param fMarginBottom
   *        the bottom margin
   */
  public Document (final float fMarginLeft, final float fMarginRight, final float fMarginTop, final float fMarginBottom)
  {
    this (PageFormat.with ().margins (fMarginLeft, fMarginRight, fMarginTop, fMarginBottom).build ());
  }

  /**
   * Creates a Document based on the given media box. By default, a {@link VerticalLayout} is used.
   *
   * @param aMediaBox
   *        the media box to use.
   * @deprecated use {@link #Document(PageFormat)} instead.
   */
  @Deprecated
  public Document (final PDRectangle aMediaBox)
  {
    this (aMediaBox, 0, 0, 0, 0);
  }

  /**
   * Creates a Document based on the given media box and margins. By default, a
   * {@link VerticalLayout} is used.
   *
   * @param aMediaBox
   *        the media box to use.
   * @param fMarginLeft
   *        the left margin
   * @param fMarginRight
   *        the right margin
   * @param fMarginTop
   *        the top margin
   * @param fMarginBottom
   *        the bottom margin
   * @deprecated use {@link #Document(PageFormat)} instead.
   */
  @Deprecated
  public Document (final PDRectangle aMediaBox,
                   final float fMarginLeft,
                   final float fMarginRight,
                   final float fMarginTop,
                   final float fMarginBottom)
  {
    this (new PageFormat (aMediaBox, EOrientation.Portrait, fMarginLeft, fMarginRight, fMarginTop, fMarginBottom));
  }

  /**
   * Creates a Document based on the given page format. By default, a {@link VerticalLayout} is
   * used.
   *
   * @param aPageFormat
   *        the page format box to use.
   */
  public Document (final PageFormat aPageFormat)
  {
    this.m_aPageFormat = aPageFormat;
  }

  /**
   * Adds an element to the document using a {@link VerticalLayoutHint}.
   *
   * @param aElement
   *        the element to add
   */
  public void add (final IElement aElement)
  {
    add (aElement, new VerticalLayoutHint ());
  }

  /**
   * Adds an element with the given layout hint.
   *
   * @param aElement
   *        the element to add
   * @param aLayoutHint
   *        the hint for the {@link ILayout}.
   */
  public void add (final IElement aElement, final ILayoutHint aLayoutHint)
  {
    m_aElements.add (_createEntry (aElement, aLayoutHint));
  }

  private Entry <IElement, ILayoutHint> _createEntry (final IElement aElement, final ILayoutHint aLayoutHint)
  {
    return new SimpleEntry <> (aElement, aLayoutHint);
  }

  /**
   * Removes the given element.
   *
   * @param aElement
   *        the element to remove.
   */
  public void remove (final IElement aElement)
  {
    m_aElements.removeIf (x -> x.getKey ().equals (aElement));
  }

  /**
   * @return the page format to use as default.
   */
  public PageFormat getPageFormat ()
  {
    return m_aPageFormat;
  }

  /**
   * @return the left document margin.
   * @deprecated use {@link #getPageFormat()} instead.
   */
  @Deprecated
  public float getMarginLeft ()
  {
    return getPageFormat ().getMarginLeft ();
  }

  /**
   * @return the right document margin.
   * @deprecated use {@link #getPageFormat()} instead.
   */
  @Deprecated
  public float getMarginRight ()
  {
    return getPageFormat ().getMarginRight ();
  }

  /**
   * @return the top document margin.
   * @deprecated use {@link #getPageFormat()} instead.
   */
  @Deprecated
  public float getMarginTop ()
  {
    return getPageFormat ().getMarginTop ();
  }

  /**
   * @return the bottom document margin.
   * @deprecated use {@link #getPageFormat()} instead.
   */
  @Deprecated
  public float getMarginBottom ()
  {
    return getPageFormat ().getMarginBottom ();
  }

  /**
   * @return the media box to use.
   * @deprecated use {@link #getPageFormat()} instead.
   */
  @Deprecated
  public PDRectangle getMediaBox ()
  {
    return getPageFormat ().getMediaBox ();
  }

  /**
   * @return the orientation to use.
   * @deprecated use {@link #getPageFormat()} instead.
   */
  @Deprecated
  public EOrientation getOrientation ()
  {
    return getPageFormat ().getOrientation ();
  }

  /**
   * @return the media box width minus margins.
   */
  public float getPageWidth ()
  {
    return getMediaBox ().getWidth () - getMarginLeft () - getMarginRight ();
  }

  /**
   * @return the media box height minus margins.
   */
  public float getPageHeight ()
  {
    return getMediaBox ().getHeight () - getMarginTop () - getMarginBottom ();
  }

  /**
   * Returns the {@link PDDocument} to be created by method {@link #render()}. Beware that this
   * PDDocument is released after rendering. This means each rendering process creates a new
   * PDDocument.
   *
   * @return the PDDocument to be used on the next call to {@link #render()}.
   */
  public PDDocument getPDDocument ()
  {
    if (m_aPdDocument == null)
    {
      m_aPdDocument = new PDDocument ();
    }
    return m_aPdDocument;
  }

  /**
   * Called after {@link #render()} in order to release the current document.
   */
  protected void resetPDDocument ()
  {
    this.m_aPdDocument = null;
  }

  /**
   * Adds a (custom) {@link IRenderer} that may handle the rendering of an element. All renderers
   * will be asked to render the current element in the order they have been added. If no renderer
   * is capable, the default renderer will be asked.
   *
   * @param aRenderer
   *        the renderer to add.
   */
  public void addRenderer (final IRenderer aRenderer)
  {
    if (aRenderer != null)
    {
      m_aCustomRenderer.add (aRenderer);
    }
  }

  /**
   * Removes a {@link IRenderer} .
   *
   * @param aRenderer
   *        the renderer to remove.
   */
  public void removeRenderer (final IRenderer aRenderer)
  {
    m_aCustomRenderer.remove (aRenderer);
  }

  /**
   * Renders all elements and returns the resulting {@link PDDocument}.
   *
   * @return the resulting {@link PDDocument}
   * @throws IOException
   *         by pdfbox
   */
  public PDDocument render () throws IOException
  {
    final PDDocument aDocument = getPDDocument ();
    final RenderContext aRenderContext = new RenderContext (this, aDocument);
    for (final Entry <IElement, ILayoutHint> aEntry : m_aElements)
    {
      final IElement aElement = aEntry.getKey ();
      final ILayoutHint aLayoutHint = aEntry.getValue ();
      boolean bSuccess = false;

      // first ask custom renderer to render the element
      final Iterator <IRenderer> aCustomRendererIterator = m_aCustomRenderer.iterator ();
      while (!bSuccess && aCustomRendererIterator.hasNext ())
      {
        bSuccess = aCustomRendererIterator.next ().render (aRenderContext, aElement, aLayoutHint);
      }

      // if none of them felt responsible, let the default renderer do the job.
      if (!bSuccess)
      {
        bSuccess = aRenderContext.render (aRenderContext, aElement, aLayoutHint);
      }

      if (!bSuccess)
      {
        throw new IllegalArgumentException ("neither layout " +
                                            aRenderContext.getLayout () +
                                            " nor the render context knows what to do with " +
                                            aElement);

      }
    }
    aRenderContext.close ();

    resetPDDocument ();
    return aDocument;
  }

  /**
   * {@link #render() Renders} the document and saves it to the given file.
   *
   * @param aFile
   *        the file to save to.
   * @throws IOException
   *         by pdfbox
   */
  public void save (final File aFile) throws IOException
  {
    try (final OutputStream aOut = new FileOutputStream (aFile))
    {
      save (aOut);
    }
  }

  /**
   * {@link #render() Renders} the document and saves it to the given output stream.
   *
   * @param aOutput
   *        the stream to save to.
   * @throws IOException
   *         by pdfbox
   */
  public void save (final OutputStream aOutput) throws IOException
  {
    try (final PDDocument aDocument = render ())
    {
      try
      {
        aDocument.save (aOutput);
      }
      catch (final IOException ioe)
      {
        throw ioe;
      }
      catch (final Exception ex)
      {
        throw new IOException (ex);
      }
    }
  }

  /**
   * Adds a {@link IRenderListener} that will be notified during {@link #render() rendering}.
   *
   * @param aListener
   *        the listener to add.
   */
  public void addRenderListener (final IRenderListener aListener)
  {
    if (aListener != null)
    {
      m_aRenderListener.add (aListener);
    }
  }

  /**
   * Removes a {@link IRenderListener} .
   *
   * @param aListener
   *        the listener to remove.
   */
  public void removeRenderListener (final IRenderListener aListener)
  {
    m_aRenderListener.remove (aListener);
  }

  @Override
  public void beforePage (final RenderContext aRenderContext) throws IOException
  {
    for (final IRenderListener aListener : m_aRenderListener)
    {
      aListener.beforePage (aRenderContext);
    }
  }

  @Override
  public void afterPage (final RenderContext aRenderContext) throws IOException
  {
    for (final IRenderListener aListener : m_aRenderListener)
    {
      aListener.afterPage (aRenderContext);
    }
  }

}
