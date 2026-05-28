package rst.pdfbox.layout.shape;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * A rectangular shape with rounded corners.
 */
public class RoundRect extends AbstractShape
{

  private final static float BEZ = 0.551915024494f;

  private final float m_fCornerRadiusX;
  private final float m_fCornerRadiusY;

  /**
   * Creates a rounded rect with equal radiuss for both x-axis and y-axis (quarter of a circle).
   *
   * @param fCornerRadius
   *        the radius of the corner circle.
   */
  public RoundRect (final float fCornerRadius)
  {
    this (fCornerRadius, fCornerRadius);
  }

  /**
   * Creates a rounded rect with potentially different radiuss for both x-axis and y-axis (quarter
   * of an ellipse).
   *
   * @param fCornerRadiusX
   *        the radius in x-direction of the corner ellipse.
   * @param fCornerRadiusY
   *        the radius in y-direction of the corner ellipse.
   */
  public RoundRect (final float fCornerRadiusX, final float fCornerRadiusY)
  {
    this.m_fCornerRadiusX = fCornerRadiusX;
    this.m_fCornerRadiusY = fCornerRadiusY;
  }

  @Override
  public void add (final PDDocument aPdDocument,
                   final PDPageContentStream aContentStream,
                   final Position aUpperLeft,
                   final float fWidth,
                   final float fHeight) throws IOException
  {
    addRoundRect (aContentStream, aUpperLeft, fWidth, fHeight, m_fCornerRadiusX, m_fCornerRadiusY);
  }

  /**
   * create points clockwise starting in upper left corner
   *
   * <pre>
   *     a          b
   *      ----------
   *     /          \
   *  h |            | c
   *    |            |
   *    |            |
   *   g \          / d
   *      ----------
   *     f          e
   * </pre>
   *
   * @param aContentStream
   *        the content stream.
   * @param aUpperLeft
   *        the upper left point
   * @param fWidth
   *        the width
   * @param fHeight
   *        the height
   * @param fCornerRadiusX
   *        the corner radius in x direction
   * @param fCornerRadiusY
   *        the corner radius in y direction
   * @throws IOException
   *         by pdfbox
   */
  protected void addRoundRect (final PDPageContentStream aContentStream,
                               final Position aUpperLeft,
                               final float fWidth,
                               final float fHeight,
                               final float fCornerRadiusX,
                               final float fCornerRadiusY) throws IOException
  {
    final float fNettoWidth = fWidth - 2 * fCornerRadiusX;
    final float fNettoHeight = fHeight - 2 * fCornerRadiusY;

    // top line
    final Position a = new Position (aUpperLeft.getX () + fCornerRadiusX, aUpperLeft.getY ());
    final Position b = new Position (a.getX () + fNettoWidth, a.getY ());
    // right line
    final Position c = new Position (aUpperLeft.getX () + fWidth, aUpperLeft.getY () - fCornerRadiusY);
    final Position d = new Position (c.getX (), c.getY () - fNettoHeight);
    // bottom line
    final Position e = new Position (aUpperLeft.getX () + fWidth - fCornerRadiusX, aUpperLeft.getY () - fHeight);
    final Position f = new Position (e.getX () - fNettoWidth, e.getY ());
    // left line
    final Position g = new Position (aUpperLeft.getX (), aUpperLeft.getY () - fHeight + fCornerRadiusY);
    final Position h = new Position (g.getX (), aUpperLeft.getY () - fCornerRadiusY);

    final float fBezX = fCornerRadiusX * BEZ;
    final float fBezY = fCornerRadiusY * BEZ;

    aContentStream.moveTo (a.getX (), a.getY ());
    _addLine (aContentStream, a.getX (), a.getY (), b.getX (), b.getY ());
    CompatibilityHelper.curveTo (aContentStream,
                                 b.getX () + fBezX,
                                 b.getY (),
                                 c.getX (),
                                 c.getY () + fBezY,
                                 c.getX (),
                                 c.getY ());
    // contentStream.addLine(c.getX(), c.getY(), d.getX(), d.getY());
    _addLine (aContentStream, c.getX (), c.getY (), d.getX (), d.getY ());
    CompatibilityHelper.curveTo (aContentStream,
                                 d.getX (),
                                 d.getY () - fBezY,
                                 e.getX () + fBezX,
                                 e.getY (),
                                 e.getX (),
                                 e.getY ());
    // contentStream.addLine(e.getX(), e.getY(), f.getX(), f.getY());
    _addLine (aContentStream, e.getX (), e.getY (), f.getX (), f.getY ());
    CompatibilityHelper.curveTo (aContentStream,
                                 f.getX () - fBezX,
                                 f.getY (),
                                 g.getX (),
                                 g.getY () - fBezY,
                                 g.getX (),
                                 g.getY ());
    _addLine (aContentStream, g.getX (), g.getY (), h.getX (), h.getY ());
    CompatibilityHelper.curveTo (aContentStream,
                                 h.getX (),
                                 h.getY () + fBezY,
                                 a.getX () - fBezX,
                                 a.getY (),
                                 a.getX (),
                                 a.getY ());
  }

  /**
   * Using lines won't give us a continuing path, which looks silly on fill. So we are approximating
   * lines with bezier curves... is there no better way?
   */
  private void _addLine (final PDPageContentStream aContentStream,
                         final float x1,
                         final float y1,
                         final float x2,
                         final float y2) throws IOException
  {
    final float fXMid = (x1 + x2) / 2f;
    final float fYMid = (y1 + y2) / 2f;
    CompatibilityHelper.curveTo1 (aContentStream, fXMid, fYMid, x2, y2);
  }

}
