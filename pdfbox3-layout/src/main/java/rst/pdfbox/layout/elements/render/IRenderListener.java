package rst.pdfbox.layout.elements.render;

import java.io.IOException;

/**
 * A render listener is called before and after a page has been rendered. It may be used, to perform
 * some custom operations (drawings) to the page.
 */
public interface IRenderListener
{

  /**
   * Called before any rendering is performed to the page.
   *
   * @param aRenderContext
   *        the context providing all rendering state.
   * @throws IOException
   *         by pdfbox.
   */
  void beforePage (final RenderContext aRenderContext) throws IOException;

  /**
   * Called after any rendering is performed to the page.
   *
   * @param aRenderContext
   *        the context providing all rendering state.
   * @throws IOException
   *         by pdfbox.
   */
  void afterPage (final RenderContext aRenderContext) throws IOException;
}
