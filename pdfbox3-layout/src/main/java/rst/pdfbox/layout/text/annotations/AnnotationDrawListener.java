package rst.pdfbox.layout.text.annotations;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import rst.pdfbox.layout.elements.render.IRenderListener;
import rst.pdfbox.layout.elements.render.RenderContext;
import rst.pdfbox.layout.text.EAlignment;
import rst.pdfbox.layout.text.IDrawContext;
import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.IDrawableText;
import rst.pdfbox.layout.text.Position;

/**
 * This listener has to be passed to all
 * {@link IDrawableText#drawText(org.apache.pdfbox.pdmodel.PDPageContentStream, Position, EAlignment, IDrawListener)
 * draw()} methods, in order collect all annotation metadata. After all drawing is done, you have to
 * call {@link #finalizeAnnotations()} which creates all necessary annotations and sets them to the
 * corresponding pages. This listener is used by the the rendering API, but you may also use it with
 * the low-level text API.
 */
public class AnnotationDrawListener implements IDrawListener, IRenderListener
{

  private final IDrawContext m_aDrawContext;
  private final Iterable <AnnotationProcessor> m_aAnnotationProcessors;

  /**
   * Creates an AnnotationDrawListener with the given {@link IDrawContext}.
   *
   * @param aDrawContext
   *        the context which provides the {@link PDDocument} and the {@link PDPage} currently drawn
   *        to.
   */
  public AnnotationDrawListener (final IDrawContext aDrawContext)
  {
    this.m_aDrawContext = aDrawContext;
    m_aAnnotationProcessors = AnnotationProcessorFactory.createAnnotationProcessors ();
  }

  @Override
  public void drawn (final Object aDrawnObject, final Position aUpperLeft, final float fWidth, final float fHeight)
  {
    if (!(aDrawnObject instanceof IAnnotated))
    {
      return;
    }
    for (final AnnotationProcessor aAnnotationProcessor : m_aAnnotationProcessors)
    {
      try
      {
        aAnnotationProcessor.annotatedObjectDrawn ((IAnnotated) aDrawnObject,
                                                   m_aDrawContext,
                                                   aUpperLeft,
                                                   fWidth,
                                                   fHeight);
      }
      catch (final IOException ex)
      {
        throw new RuntimeException ("exception on annotation processing", ex);
      }
    }
  }

  /**
   * @deprecated user {@link #afterRender()} instead.
   * @throws IOException
   *         by pdfbox.
   */
  @Deprecated
  public void finalizeAnnotations () throws IOException
  {
    afterRender ();
  }

  @Override
  public void beforePage (final RenderContext aRenderContext) throws IOException
  {
    for (final AnnotationProcessor aAnnotationProcessor : m_aAnnotationProcessors)
    {
      try
      {
        aAnnotationProcessor.beforePage (m_aDrawContext);
      }
      catch (final IOException ex)
      {
        throw new RuntimeException ("exception on annotation processing", ex);
      }
    }
  }

  @Override
  public void afterPage (final RenderContext aRenderContext) throws IOException
  {
    for (final AnnotationProcessor aAnnotationProcessor : m_aAnnotationProcessors)
    {
      try
      {
        aAnnotationProcessor.afterPage (m_aDrawContext);
      }
      catch (final IOException ex)
      {
        throw new RuntimeException ("exception on annotation processing", ex);
      }
    }
  }

  public void afterRender ()
  {
    for (final AnnotationProcessor aAnnotationProcessor : m_aAnnotationProcessors)
    {
      try
      {
        aAnnotationProcessor.afterRender (m_aDrawContext.getPdDocument ());
      }
      catch (final IOException ex)
      {
        throw new RuntimeException ("exception on annotation processing", ex);
      }
    }
  }

}
