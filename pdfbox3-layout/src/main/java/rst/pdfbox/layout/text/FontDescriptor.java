package rst.pdfbox.layout.text;

import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * Container for a Font and size.
 */
public class FontDescriptor
{

  /**
   * the associated font.
   */
  private final PDFont m_aFont;

  /**
   * the font size.
   */
  private final float m_fSize;

  /**
   * Creates the descriptor the the given font and size.
   *
   * @param aFont
   *        the font.
   * @param fSize
   *        the size.
   */
  public FontDescriptor (final PDFont aFont, final float fSize)
  {
    this.m_aFont = aFont;
    this.m_fSize = fSize;
  }

  /**
   * @return the font.
   */
  public PDFont getFont ()
  {
    return m_aFont;
  }

  /**
   * @return the size.
   */
  public float getSize ()
  {
    return m_fSize;
  }

  @Override
  public String toString ()
  {
    return "FontDescriptor [font=" + m_aFont + ", size=" + m_fSize + "]";
  }

  @Override
  public int hashCode ()
  {
    final int nPrime = 31;
    int nResult = 1;
    nResult = nPrime * nResult + ((m_aFont == null) ? 0 : m_aFont.hashCode ());
    nResult = nPrime * nResult + Float.floatToIntBits (m_fSize);
    return nResult;
  }

  @Override
  public boolean equals (final Object aObj)
  {
    if (this == aObj)
    {
      return true;
    }
    if (aObj == null)
    {
      return false;
    }
    if (getClass () != aObj.getClass ())
    {
      return false;
    }
    final FontDescriptor aOther = (FontDescriptor) aObj;
    if (m_aFont == null)
    {
      if (aOther.m_aFont != null)
      {
        return false;
      }
    }
    else
      if (!m_aFont.equals (aOther.m_aFont))
      {
        return false;
      }
    if (Float.floatToIntBits (m_fSize) != Float.floatToIntBits (aOther.m_fSize))
    {
      return false;
    }
    return true;
  }

}
