package rst.pdfbox.layout.util;

/**
 * Container class for the default enumerators.
 */
public class Enumerators
{

  /**
   * Uses arabic numbers for the enumeration, and dot as the default separator. <br>
   *
   * <pre>
   * 1. At vero eos et accusam.
   * 2. Et justo duo dolores ea rebum.
   * 3. Stet clita ...
   * </pre>
   */
  public static class ArabicEnumerator implements Enumerator
  {

    private int m_nCount;

    public ArabicEnumerator ()
    {
      this (1);
    }

    public ArabicEnumerator (final int nStartCount)
    {
      this.m_nCount = nStartCount;
    }

    @Override
    public String next ()
    {
      return String.valueOf (m_nCount++);
    }

    @Override
    public String getDefaultSeperator ()
    {
      return ".";
    }
  }

  /**
   * Uses lower case letters for the enumeration, and braces as the default separator. <br>
   *
   * <pre>
   * a) At vero eos et accusam.
   * b) Et justo duo dolores ea rebum.
   * c) Stet clita ...
   * </pre>
   */
  public static class LowerCaseAlphabeticEnumerator extends AlphabeticEnumerator
  {

    public LowerCaseAlphabeticEnumerator ()
    {}

    public LowerCaseAlphabeticEnumerator (final int nStartCount)
    {
      super (nStartCount);
    }

    @Override
    public String next ()
    {
      return super.next ().toLowerCase ();
    }
  }

  /**
   * Uses upper case letters for the enumeration, and braces as the default separator. <br>
   *
   * <pre>
   * A) At vero eos et accusam.
   * B) Et justo duo dolores ea rebum.
   * C) Stet clita ...
   * </pre>
   */
  public static class AlphabeticEnumerator implements Enumerator
  {

    static final char [] DIGITS = { 'A',
                                    'B',
                                    'C',
                                    'D',
                                    'E',
                                    'F',
                                    'G',
                                    'H',
                                    'I',
                                    'J',
                                    'K',
                                    'L',
                                    'M',
                                    'N',
                                    'O',
                                    'P',
                                    'Q',
                                    'R',
                                    'S',
                                    'T',
                                    'U',
                                    'V',
                                    'W',
                                    'X',
                                    'Y',
                                    'Z' };

    private int m_nCount;

    public AlphabeticEnumerator ()
    {
      this (1);
    }

    public AlphabeticEnumerator (final int nStartCount)
    {
      this.m_nCount = nStartCount;
    }

    @Override
    public String next ()
    {
      return _toString (m_nCount++ - 1);
    }

    @Override
    public String getDefaultSeperator ()
    {
      return ")";
    }

    private static String _toString (final int nSrcValue)
    {
      final char [] aBuf = new char [33];
      int nCharPos = 32;

      int nValue = -nSrcValue;

      while (nValue <= -DIGITS.length)
      {
        aBuf[nCharPos--] = DIGITS[-(nValue % DIGITS.length)];
        nValue = nValue / DIGITS.length;
      }
      aBuf[nCharPos] = DIGITS[-nValue];

      return new String (aBuf, nCharPos, (33 - nCharPos));
    }

  }

  /**
   * Uses lower case roman numbers for the enumeration, and dot as the default separator. <br>
   *
   * <pre>
   *   i. At vero eos et accusam.
   *  ii. Et justo duo dolores ea rebum.
   * iii. Stet clita ...
   * </pre>
   */
  public static class LowerCaseRomanEnumerator extends RomanEnumerator
  {

    public LowerCaseRomanEnumerator ()
    {}

    public LowerCaseRomanEnumerator (final int nStartCount)
    {
      super (nStartCount);
    }

    @Override
    public String next ()
    {
      return super.next ().toLowerCase ();
    }
  }

  /**
   * Uses upper case roman numbers for the enumeration, and dot as the default separator. <br>
   *
   * <pre>
   *   I. At vero eos et accusam.
   *  II. Et justo duo dolores ea rebum.
   * III. Stet clita ...
   * </pre>
   */
  public static class RomanEnumerator implements Enumerator
  {

    private int m_nCount;

    public RomanEnumerator ()
    {
      this (1);
    }

    public RomanEnumerator (final int nStartCount)
    {
      this.m_nCount = nStartCount;
    }

    @Override
    public String next ()
    {
      return _toRoman (m_nCount++);
    }

    @Override
    public String getDefaultSeperator ()
    {
      return ".";
    }

    private String _toRoman (final int nSrcValue)
    {
      int nValue = nSrcValue;
      if (nValue < 1 || nValue > 3999)
        return "Invalid Roman Number Value";
      final StringBuilder sResult = new StringBuilder ();
      while (nValue >= 1000)
      {
        sResult.append ("M");
        nValue -= 1000;
      }
      while (nValue >= 900)
      {
        sResult.append ("CM");
        nValue -= 900;
      }
      while (nValue >= 500)
      {
        sResult.append ("D");
        nValue -= 500;
      }
      while (nValue >= 400)
      {
        sResult.append ("CD");
        nValue -= 400;
      }
      while (nValue >= 100)
      {
        sResult.append ("C");
        nValue -= 100;
      }
      while (nValue >= 90)
      {
        sResult.append ("XC");
        nValue -= 90;
      }
      while (nValue >= 50)
      {
        sResult.append ("L");
        nValue -= 50;
      }
      while (nValue >= 40)
      {
        sResult.append ("XL");
        nValue -= 40;
      }
      while (nValue >= 10)
      {
        sResult.append ("X");
        nValue -= 10;
      }
      while (nValue >= 9)
      {
        sResult.append ("IX");
        nValue -= 9;
      }
      while (nValue >= 5)
      {
        sResult.append ("V");
        nValue -= 5;
      }
      while (nValue >= 4)
      {
        sResult.append ("IV");
        nValue -= 4;
      }
      while (nValue >= 1)
      {
        sResult.append ("I");
        nValue -= 1;
      }
      return sResult.toString ();
    }
  }

}
