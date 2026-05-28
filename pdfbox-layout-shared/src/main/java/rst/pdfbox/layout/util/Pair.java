package rst.pdfbox.layout.util;

/**
 * Generic container for a pair of objects.
 *
 * @param <T>
 *        the generic parameter.
 */
public class Pair <T>
{

  private T m_aFirst;
  private T m_aSecond;

  public Pair (final T aFirst, final T aSecond)
  {
    super ();
    this.m_aFirst = aFirst;
    this.m_aSecond = aSecond;
  }

  public T getFirst ()
  {
    return m_aFirst;
  }

  public T getSecond ()
  {
    return m_aSecond;
  }

  @Override
  public String toString ()
  {
    return "Tuple [first=" + m_aFirst + ", second=" + m_aSecond + "]";
  }

  @Override
  public int hashCode ()
  {
    final int nPrime = 31;
    int nResult = 1;
    nResult = nPrime * nResult + ((m_aFirst == null) ? 0 : m_aFirst.hashCode ());
    nResult = nPrime * nResult + ((m_aSecond == null) ? 0 : m_aSecond.hashCode ());
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
    @SuppressWarnings ("rawtypes")
    final Pair aOther = (Pair) aObj;
    if (m_aFirst == null)
    {
      if (aOther.m_aFirst != null)
        return false;
    }
    else
      if (!m_aFirst.equals (aOther.m_aFirst))
        return false;
    if (m_aSecond == null)
    {
      if (aOther.m_aSecond != null)
        return false;
    }
    else
      if (!m_aSecond.equals (aOther.m_aSecond))
        return false;
    return true;
  }

}
