package rst.pdfbox.layout.text.annotations;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import rst.pdfbox.layout.text.IDrawContext;
import rst.pdfbox.layout.text.Position;

/**
 * Processes an annotation.
 */
public interface AnnotationProcessor
{

  /**
   * Called if an annotated object has been drawn.
   *
   * @param aDrawnObject
   *        the drawn object.
   * @param aDrawContext
   *        the drawing context.
   * @param aUpperLeft
   *        the upper left position the object has been drawn to.
   * @param fWidth
   *        the width of the drawn object.
   * @param fHeight
   *        the height of the drawn object.
   * @throws IOException
   *         by pdfbox.
   */
  void annotatedObjectDrawn (final IAnnotated aDrawnObject,
                             final IDrawContext aDrawContext,
                             Position aUpperLeft,
                             float fWidth,
                             float fHeight) throws IOException;

  /**
   * Called before a page is drawn.
   *
   * @param aDrawContext
   *        the drawing context.
   * @throws IOException
   *         by pdfbox.
   */
  void beforePage (final IDrawContext aDrawContext) throws IOException;

  /**
   * Called after a page is drawn.
   *
   * @param aDrawContext
   *        the drawing context.
   * @throws IOException
   *         by pdfbox.
   */
  void afterPage (final IDrawContext aDrawContext) throws IOException;

  /**
   * Called after all rendering has been performed.
   *
   * @param aDocument
   *        the document.
   * @throws IOException
   *         by pdfbox.
   */
  void afterRender (final PDDocument aDocument) throws IOException;

}
