package rst.pdfbox.layout.elements;

import java.io.IOException;

/**
 * If a drawable is marked as {@link IDividable}, it can be (vertically) divided in case it does not
 * fit on the (remaining) page.
 */
public interface IDividable
{

  /**
   * Divides the drawable vetically into pieces where the first part is to respect the given
   * remaining height. The page height allows to make better decisions on how to divide best.
   *
   * @param fRemainingHeight
   *        the remaining height on the page dictating the height of the first part.
   * @param fNextPageHeight
   *        the height of the next page allows to make better decisions on how to divide best, e.g.
   *        maybe the element fits completely on the next page.
   * @return the Divided containing the first part and the tail.
   * @throws IOException
   *         by pdfbox.
   */
  Divided divide (final float fRemainingHeight, final float fNextPageHeight) throws IOException;

  /**
   * A container for the result of a {@link IDividable#divide(float, float)} operation.
   */
  public static class Divided
  {
    private final IDrawable m_aFirst;
    private final IDrawable m_aTail;

    public Divided (final IDrawable aFirst, final IDrawable aTail)
    {
      this.m_aFirst = aFirst;
      this.m_aTail = aTail;
    }

    public IDrawable getFirst ()
    {
      return m_aFirst;
    }

    public IDrawable getTail ()
    {
      return m_aTail;
    }
  }
}
