package rst.pdfbox.layout.elements.render;

import java.io.IOException;

import rst.pdfbox.layout.elements.IElement;

/**
 * A renderer is responsible for rendering certain, but not necessarily all elements. The boolean
 * return value indicates whether the element could be processed by this renderer.
 */
public interface IRenderer
{

  /**
   * Renders an element.
   *
   * @param aRenderContext
   *        the render context.
   * @param aElement
   *        the element to draw.
   * @param aLayoutHint
   *        the associated layout hint
   * @return <code>true</code> if the layout is able to render the element.
   * @throws IOException
   *         by pdfbox
   */
  boolean render (final RenderContext aRenderContext,
                  final IElement aElement,
                  final ILayoutHint aLayoutHint) throws IOException;

}
