package rst.pdfbox.layout.shape;

import java.awt.Color;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.Position;

/**
 * Shapes can be used to either
 * {@link #draw(PDDocument, PDPageContentStream, Position, float, float, Color, Stroke, IDrawListener)
 * stroke} or
 * {@link #fill(PDDocument, PDPageContentStream, Position, float, float, Color, IDrawListener) fill}
 * the path of the shape, or simply
 * {@link #add(PDDocument, PDPageContentStream, Position, float, float) add the path} of the shape
 * to the drawing context.
 */
public interface Shape
{

  /**
   * Draws (strokes) the shape.
   *
   * @param aPdDocument
   *        the underlying pdfbox document.
   * @param aContentStream
   *        the stream to draw to.
   * @param aUpperLeft
   *        the upper left position to start drawing.
   * @param fWidth
   *        the width of the bounding box.
   * @param fHeight
   *        the height of the bounding box.
   * @param aColor
   *        the color to use.
   * @param aStroke
   *        the stroke to use.
   * @param aDrawListener
   *        the listener to {@link IDrawListener#drawn(Object, Position, float, float) notify} on
   *        drawn objects.
   * @throws IOException
   *         by pdfbox
   */
  void draw (PDDocument aPdDocument,
             PDPageContentStream aContentStream,
             Position aUpperLeft,
             float fWidth,
             float fHeight,
             Color aColor,
             Stroke aStroke,
             IDrawListener aDrawListener) throws IOException;

  /**
   * Fills the shape.
   *
   * @param aPdDocument
   *        the underlying pdfbox document.
   * @param aContentStream
   *        the stream to draw to.
   * @param aUpperLeft
   *        the upper left position to start drawing.
   * @param fWidth
   *        the width of the bounding box.
   * @param fHeight
   *        the height of the bounding box.
   * @param aColor
   *        the color to use.
   * @param aDrawListener
   *        the listener to {@link IDrawListener#drawn(Object, Position, float, float) notify} on
   *        drawn objects.
   * @throws IOException
   *         by pdfbox
   */
  void fill (PDDocument aPdDocument,
             PDPageContentStream aContentStream,
             Position aUpperLeft,
             float fWidth,
             float fHeight,
             Color aColor,
             IDrawListener aDrawListener) throws IOException;

  /**
   * Adds (the path of) the shape without drawing anything.
   *
   * @param aPdDocument
   *        the underlying pdfbox document.
   * @param aContentStream
   *        the stream to draw to.
   * @param aUpperLeft
   *        the upper left position to start drawing.
   * @param fWidth
   *        the width of the bounding box.
   * @param fHeight
   *        the height of the bounding box.
   * @throws IOException
   *         by pdfbox
   */
  void add (PDDocument aPdDocument,
            PDPageContentStream aContentStream,
            Position aUpperLeft,
            float fWidth,
            float fHeight) throws IOException;

}
