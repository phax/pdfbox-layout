package rst.pdfbox.layout.elements;

/**
 * In order to avoid dependencies to AWT, we use our own Dimension class here.
 */
public class Dimension
{

  private final float m_fWidth;
  private final float m_fHeight;

  public Dimension (final float fWidth, final float fHeight)
  {
    super ();
    this.m_fWidth = fWidth;
    this.m_fHeight = fHeight;
  }

  public float getWidth ()
  {
    return m_fWidth;
  }

  public float getHeight ()
  {
    return m_fHeight;
  }

  @Override
  public String toString ()
  {
    return "Dimension [width=" + m_fWidth + ", height=" + m_fHeight + "]";
  }

  @Override
  public int hashCode ()
  {
    final int nPrime = 31;
    int nResult = 1;
    nResult = nPrime * nResult + Float.floatToIntBits (m_fHeight);
    nResult = nPrime * nResult + Float.floatToIntBits (m_fWidth);
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
    final Dimension aOther = (Dimension) aObj;
    if (Float.floatToIntBits (m_fHeight) != Float.floatToIntBits (aOther.m_fHeight))
      return false;
    if (Float.floatToIntBits (m_fWidth) != Float.floatToIntBits (aOther.m_fWidth))
      return false;
    return true;
  }

}
