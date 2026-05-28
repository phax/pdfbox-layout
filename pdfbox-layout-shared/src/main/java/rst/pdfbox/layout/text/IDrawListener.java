package rst.pdfbox.layout.text;

/**
 * Called if an object has been drawn.
 */
public interface IDrawListener
{
  /**
   * Indicates that an object has been drawn.
   *
   * @param aDrawnObject
   *        the drawn object.
   * @param aUpperLeft
   *        the upper left position.
   * @param fWidth
   *        the width of the drawn object.
   * @param fHeight
   *        the height of the drawn object.
   */
  void drawn (final Object aDrawnObject, final Position aUpperLeft, final float fWidth, final float fHeight);
}
