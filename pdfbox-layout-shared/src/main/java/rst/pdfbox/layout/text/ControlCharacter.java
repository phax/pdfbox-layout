package rst.pdfbox.layout.text;

import java.util.regex.Pattern;

/**
 * A control character represents the pattern to match and escape sequence for character sequences
 * with a special meaning. Currently there is newline for all kinds of text, and bold and italic for
 * markup.
 */
public class ControlCharacter implements CharSequence
{
  private final String m_sDescription;
  private final String m_sCharaterToEscape;

  protected ControlCharacter (final String sDescription, final String sCharaterToEscape)
  {
    this.m_sDescription = sDescription;
    this.m_sCharaterToEscape = sCharaterToEscape;
  }

  /**
   * @return the character to escape, e.g. '*' for bold.
   */
  public String getCharacterToEscape ()
  {
    return m_sCharaterToEscape;
  }

  /**
   * @return <code>true</code> if this control character must be escaped in text.
   */
  public boolean mustEscape ()
  {
    return getCharacterToEscape () != null;
  }

  /**
   * Escapes the control character in the given text if necessary.
   *
   * @param sText
   *        the text to escape.
   * @return the escaped text.
   */
  public String escape (final String sText)
  {
    if (!mustEscape ())
      return sText;
    return sText.replaceAll (Pattern.quote (getCharacterToEscape ()), "\\" + getCharacterToEscape ());
  }

  /**
   * Un-escapes the control character in the given text if necessary.
   *
   * @param sText
   *        the text to un-escape.
   * @return the un-escaped text.
   */
  public String unescape (final String sText)
  {
    if (!mustEscape ())
    {
      return sText;
    }
    return sText.replaceAll ("\\\\" + Pattern.quote (getCharacterToEscape ()), getCharacterToEscape ());
  }

  @Override
  public int length ()
  {
    return 0;
  }

  @Override
  public char charAt (final int nIndex)
  {
    throw new ArrayIndexOutOfBoundsException (nIndex);
  }

  @Override
  public CharSequence subSequence (final int nStart, final int nEnd)
  {
    return null;
  }

  @Override
  public String toString ()
  {
    return m_sDescription;
  }
}
