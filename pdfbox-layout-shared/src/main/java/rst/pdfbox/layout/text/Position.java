package rst.pdfbox.layout.text;

/**
 * In order to avoid dependencies to AWT classes (e.g. Point), we have our own silly implemenation
 * of a position.
 */
public class Position
{
  private final float m_fX;
  private final float m_fY;

  /**
   * Creates a position at the given coordinates.
   *
   * @param fX
   *        the x coordinate.
   * @param fY
   *        the y coordinate.
   */
  public Position (final float fX, final float fY)
  {
    this.m_fX = fX;
    this.m_fY = fY;
  }

  /**
   * @return the x coordinate of the position.
   */
  public float getX ()
  {
    return m_fX;
  }

  /**
   * @return the y coordinate of the position.
   */
  public float getY ()
  {
    return m_fY;
  }

  /**
   * Adds an offset to the current position and returns it as a new position.
   *
   * @param fX
   *        the x offset to add.
   * @param fY
   *        the y offset to add.
   * @return the new position.
   */
  public Position add (final float fX, final float fY)
  {
    return new Position (this.m_fX + fX, this.m_fY + fY);
  }

  @Override
  public String toString ()
  {
    return "Position [x=" + m_fX + ", y=" + m_fY + "]";
  }

  @Override
  public int hashCode ()
  {
    final int nPrime = 31;
    int nResult = 1;
    nResult = nPrime * nResult + Float.floatToIntBits (m_fX);
    nResult = nPrime * nResult + Float.floatToIntBits (m_fY);
    return nResult;
  }

  @Override
  public boolean equals (final Object aObj)
  {
    if (this == aObj)
      return true;
    if (aObj == null)
      return false;
    if (getClass () != aObj.getClass ())
      return false;
    final Position aOther = (Position) aObj;
    if (Float.floatToIntBits (m_fX) != Float.floatToIntBits (aOther.m_fX))
      return false;
    if (Float.floatToIntBits (m_fY) != Float.floatToIntBits (aOther.m_fY))
      return false;
    return true;
  }

}
