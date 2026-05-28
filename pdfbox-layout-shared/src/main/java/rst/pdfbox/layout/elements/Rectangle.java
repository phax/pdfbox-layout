package rst.pdfbox.layout.elements;

/**
 * In order to avoid dependencies to AWT, we use our own Rectangle class here.
 */
public class Rectangle extends Dimension
{
  private final float m_fX;
  private final float m_fY;

  public Rectangle (final float fX, final float fY, final float fWidth, final float fHeight)
  {
    super (fWidth, fHeight);
    this.m_fX = fX;
    this.m_fY = fY;
  }

  public float getX ()
  {
    return m_fX;
  }

  public float getY ()
  {
    return m_fY;
  }

  @Override
  public String toString ()
  {
    return "Rectangle [x=" + m_fX + ", y=" + m_fY + ", width=" + getWidth () + ", height=" + getHeight () + "]";
  }

  @Override
  public int hashCode ()
  {
    final int nPrime = 31;
    int nResult = super.hashCode ();
    nResult = nPrime * nResult + Float.floatToIntBits (m_fX);
    nResult = nPrime * nResult + Float.floatToIntBits (m_fY);
    return nResult;
  }

  @Override
  public boolean equals (final Object aObj)
  {
    if (this == aObj)
      return true;
    if (!super.equals (aObj))
      return false;
    if (getClass () != aObj.getClass ())
      return false;
    final Rectangle aOther = (Rectangle) aObj;
    if (Float.floatToIntBits (m_fX) != Float.floatToIntBits (aOther.m_fX))
      return false;
    if (Float.floatToIntBits (m_fY) != Float.floatToIntBits (aOther.m_fY))
      return false;
    return true;
  }

}
