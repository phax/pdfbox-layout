package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.font.PDFont;

import rst.pdfbox.layout.text.ControlCharacters.IControlCharacterFactory;
import rst.pdfbox.layout.util.CompatibilityHelper;
import rst.pdfbox.layout.util.Enumerator;
import rst.pdfbox.layout.util.EnumeratorFactory;

/**
 * Container class for current supported indentation control characters.
 */
public class IndentCharacters
{

  /**
   * The factory for indent control characters.
   */
  public static IControlCharacterFactory INDENT_FACTORY = new IndentCharacterFactory ();

  /**
   * Represent un-indentation, means effectively indent of 0.
   */
  public static IndentCharacter UNINDENT_CHARACTER = new IndentCharacter ("0", "0", "pt");

  /**
   * An <code>--{7em}</code> indicates an indentation of 7 characters in markup, where the number,
   * the unit, and the brackets are optional. Default indentation is 4 characters, default unit is
   * <code>7em</code> It can be escaped with a backslash ('\').
   */
  public static class IndentCharacter extends ControlCharacter
  {

    protected int m_nLevel = 1;
    protected float m_fIndentWidth = 4;
    protected ESpaceUnit m_eIndentUnit = ESpaceUnit.em;

    public IndentCharacter (final String sLevel, final String sIndentWidth, final String sIndentUnit)
    {
      super ("INDENT", IndentCharacterFactory.TO_ESCAPE);
      try
      {
        this.m_nLevel = sLevel == null ? 0 : sLevel.length () + 1;
      }
      catch (final NumberFormatException ex)
      {}
      try
      {
        this.m_eIndentUnit = sIndentUnit == null ? ESpaceUnit.em : ESpaceUnit.valueOf (sIndentUnit);
      }
      catch (final NumberFormatException ex)
      {}
      final float fDefaultIndent = this.m_eIndentUnit == ESpaceUnit.em ? 4 : 10;
      try
      {
        this.m_fIndentWidth = sIndentWidth == null ? fDefaultIndent : Integer.parseInt (sIndentWidth);
      }
      catch (final NumberFormatException ex)
      {}
    }

    /**
     * @return the level of indentation, where 0 means no indent.
     */
    public int getLevel ()
    {
      return m_nLevel;
    }

    /**
     * @return the next label to use on a subsequent indent. Makes only sense for enumerating
     *         indents.
     */
    protected String nextLabel ()
    {
      return "";
    }

    /**
     * Creates the actual {@link Indent} fragment from this control character.
     *
     * @param fFontSize
     *        the current font size.
     * @param aFont
     *        the current font.
     * @param aColor
     *        the color to use.
     * @return the new Indent.
     * @throws IOException
     *         by pdfbox
     */
    public Indent createNewIndent (final float fFontSize, final PDFont aFont, final Color aColor) throws IOException
    {
      return new Indent (nextLabel (), m_nLevel * m_fIndentWidth, m_eIndentUnit, fFontSize, aFont, EAlignment.Right, aColor);
    }

    @Override
    public int hashCode ()
    {
      final int nPrime = 31;
      int nResult = 1;
      nResult = nPrime * nResult + ((m_eIndentUnit == null) ? 0 : m_eIndentUnit.hashCode ());
      nResult = nPrime * nResult + Float.floatToIntBits (m_fIndentWidth);
      nResult = nPrime * nResult + m_nLevel;
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
      final IndentCharacter aOther = (IndentCharacter) aObj;
      if (m_eIndentUnit != aOther.m_eIndentUnit)
        return false;
      if (Float.floatToIntBits (m_fIndentWidth) != Float.floatToIntBits (aOther.m_fIndentWidth))
        return false;
      if (m_nLevel != aOther.m_nLevel)
        return false;
      return true;
    }

  }

  /**
   * An <code>-+{--:7em}</code> indicates a list indentation of 7 characters in markup, using
   * <code>--</code> as the bullet. The number, the unit, bullet character and the brackets are
   * optional. Default indentation is 4 characters, default unit is <code>em</code> and the default
   * bullet depends on {@link CompatibilityHelper#getBulletCharacter(int)}. It can be escaped with a
   * backslash ('\').
   */
  public static class ListCharacter extends IndentCharacter
  {

    protected String m_sLabel;

    protected ListCharacter (final String sLevel,
                             final String sIndentWidth,
                             final String sIndentUnit,
                             final String sBulletCharacter)
    {
      super (sLevel, sIndentWidth, sIndentUnit);
      if (sBulletCharacter != null)
      {
        m_sLabel = sBulletCharacter;
        if (!m_sLabel.endsWith (" "))
        {
          m_sLabel += " ";
        }
      }
      else
      {
        m_sLabel = CompatibilityHelper.getBulletCharacter (getLevel ()) + " ";
      }
    }

    @Override
    protected String nextLabel ()
    {
      return m_sLabel;
    }

    @Override
    public int hashCode ()
    {
      final int nPrime = 31;
      int nResult = super.hashCode ();
      nResult = nPrime * nResult + ((m_sLabel == null) ? 0 : m_sLabel.hashCode ());
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
      final ListCharacter aOther = (ListCharacter) aObj;
      if (m_sLabel == null)
      {
        if (aOther.m_sLabel != null)
          return false;
      }
      else
        if (!m_sLabel.equals (aOther.m_sLabel))
          return false;
      return true;
    }

  }

  /**
   * An <code>-#{a):7em}</code> indicates an enumeration indentation of 7 characters in markup,
   * using <code>a)...b)...etc</code> as the enumeration. The number, the unit, enumeration
   * type/separator, and the brackets are optional. Default indentation is 4 characters, default
   * unit is <code>em</code>. Default enumerators are arabic numbers, the separator depends on the
   * enumerator by default ('.' for arabic). For available enumerators see
   * {@link EnumeratorFactory}.It can be escaped with a backslash ('\').
   */
  public static class EnumerationCharacter extends IndentCharacter
  {

    protected Enumerator m_aEnumerator;
    protected String m_sSeparator;

    protected EnumerationCharacter (final String sLevel,
                                    final String sIndentWidth,
                                    final String sIndentUnit,
                                    final String sEnumerationType,
                                    final String sSeparator)
    {
      super (sLevel, sIndentWidth, sIndentUnit);

      String sActualEnumerationType = sEnumerationType;
      if (sActualEnumerationType == null)
      {
        sActualEnumerationType = "1";
      }
      m_aEnumerator = EnumeratorFactory.createEnumerator (sActualEnumerationType);
      this.m_sSeparator = sSeparator != null ? sSeparator : m_aEnumerator.getDefaultSeperator ();
    }

    @Override
    protected String nextLabel ()
    {
      final String sNext = m_aEnumerator.next ();
      final StringBuilder aBob = new StringBuilder (sNext.length () + m_sSeparator.length () + 1);
      aBob.append (sNext);
      aBob.append (m_sSeparator);
      if (!m_sSeparator.endsWith (" "))
      {
        aBob.append (" ");
      }
      return aBob.toString ();
    }

    @Override
    public int hashCode ()
    {
      final int nPrime = 31;
      int nResult = super.hashCode ();
      nResult = nPrime * nResult + ((m_aEnumerator == null) ? 0 : m_aEnumerator.hashCode ());
      nResult = nPrime * nResult + ((m_sSeparator == null) ? 0 : m_sSeparator.hashCode ());
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
      final EnumerationCharacter aOther = (EnumerationCharacter) aObj;
      if (m_aEnumerator == null)
      {
        if (aOther.m_aEnumerator != null)
          return false;
      }
      else
        if (aOther.m_aEnumerator == null)
        {
          return false;
        }
        else
          if (!m_aEnumerator.getClass ().equals (aOther.m_aEnumerator.getClass ()))
            return false;
      if (m_sSeparator == null)
      {
        if (aOther.m_sSeparator != null)
          return false;
      }
      else
        if (!m_sSeparator.equals (aOther.m_sSeparator))
          return false;
      return true;
    }

  }

  private static class IndentCharacterFactory implements IControlCharacterFactory
  {

    private final static Pattern PATTERN = Pattern.compile ("^-(!)|^([ ]*)-(-)(\\{(\\d*)(em|pt)?\\})?|^([ ]*)-(\\+)(\\{(.+)?:(\\d*)(em|pt)?\\})?|^([ ]*)-(#)(\\{((?!:).)?(.+)?:((\\d*))((em|pt))?\\})?");
    private final static Pattern UNESCAPE_PATTERN = Pattern.compile ("^\\\\([ ]*-[-|+|#])");

    private final static String TO_ESCAPE = "--";

    @Override
    public ControlCharacter createControlCharacter (final String sText,
                                                    final Matcher aMatcher,
                                                    final List <CharSequence> aCharactersSoFar)
    {
      if ("!".equals (aMatcher.group (1)))
      {
        return UNINDENT_CHARACTER;
      }

      if ("-".equals (aMatcher.group (3)))
      {
        return new IndentCharacter (aMatcher.group (2), aMatcher.group (5), aMatcher.group (6));
      }

      if ("+".equals (aMatcher.group (8)))
      {
        return new ListCharacter (aMatcher.group (7), aMatcher.group (11), aMatcher.group (12), aMatcher.group (10));
      }

      if ("#".equals (aMatcher.group (14)))
      {
        return new EnumerationCharacter (aMatcher.group (13),
                                         aMatcher.group (18),
                                         aMatcher.group (20),
                                         aMatcher.group (16),
                                         aMatcher.group (17));
      }

      throw new IllegalArgumentException ("unkown indentation " + sText);
    }

    @Override
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    public String unescape (final String sText)
    {
      final Matcher aMatcher = UNESCAPE_PATTERN.matcher (sText);
      if (!aMatcher.find ())
      {
        return sText;
      }
      return aMatcher.group (1) + sText.substring (aMatcher.end ());
    }

    @Override
    public boolean patternMatchesBeginOfLine ()
    {
      return true;
    }

  }
}
