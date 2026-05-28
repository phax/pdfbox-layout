package rst.pdfbox.layout.text;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Container class for all control character factories.
 */
public class ControlCharacters
{
  /**
   * Unescapes the escape character backslash.
   *
   * @param sText
   *        the text to escape.
   * @return the unescaped text.
   */
  public static String unescapeBackslash (final String sText)
  {
    return sText.replace ("\\\\", "\\");
  }

  /**
   * A control character factory is used to create control characters on the fly from the control
   * pattern. This allows to parameterize the characters as needed for e.g. colors.
   */
  public interface IControlCharacterFactory
  {
    /**
     * Creates the control character from the given matched pattern.
     *
     * @param sText
     *        the parsed text.
     * @param aMatcher
     *        the matcher.
     * @param aCharactersSoFar
     *        the characters created so far.
     * @return the created character.
     */
    ControlCharacter createControlCharacter (final String sText,
                                             final Matcher aMatcher,
                                             final List <CharSequence> aCharactersSoFar);

    /**
     * @return the pattern used to match the control character.
     */
    Pattern getPattern ();

    /**
     * Indicates if the pattern should be applied to the begin of line only.
     *
     * @return <code>true</code> if the pattern is to be applied at the begin of a line.
     */
    boolean patternMatchesBeginOfLine ();

    /**
     * Unescapes the pattern.
     *
     * @param sText
     *        the text to unescape.
     * @return the unescaped text.
     */
    String unescape (final String sText);

  }

  /**
   * The factory for bold control characters.
   */
  public static IControlCharacterFactory BOLD_FACTORY = new StaticControlCharacterFactory (new BoldControlCharacter (),
                                                                                           BoldControlCharacter.PATTERN);
  /**
   * The factory for italic control characters.
   */
  public static IControlCharacterFactory ITALIC_FACTORY = new StaticControlCharacterFactory (new ItalicControlCharacter (),
                                                                                             ItalicControlCharacter.PATTERN);
  /**
   * The factory for new line control characters.
   */
  public static IControlCharacterFactory NEWLINE_FACTORY = new StaticControlCharacterFactory (new NewLineControlCharacter (),
                                                                                              NewLineControlCharacter.PATTERN);
  /**
   * The factory for color control characters.
   */
  public static IControlCharacterFactory COLOR_FACTORY = new ColorControlCharacterFactory ();

  /**
   * The factory for metrics control characters.
   */
  public static MetricsControlCharacterFactory METRICS_FACTORY = new MetricsControlCharacterFactory ();

  /**
   * An asterisk ('*') indicates switching of bold font mode in markup. It can be escaped with a
   * backslash ('\').
   */
  public static class BoldControlCharacter extends ControlCharacter
  {
    public static Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\*");

    protected BoldControlCharacter ()
    {
      super ("BOLD", "*");
    }
  }

  /**
   * An underscore ('_') indicates switching of italic font mode in markup. It can be escaped with a
   * backslash ('\').
   */
  public static class ItalicControlCharacter extends ControlCharacter
  {
    private static Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*(?<!_)_(?!_)");

    protected ItalicControlCharacter ()
    {
      super ("ITALIC", "_");
    }
  }

  /**
   * LF ('\n') and CRLF ('\r\n') indicates a new line.
   */
  public static class NewLineControlCharacter extends ControlCharacter
  {
    private static Pattern PATTERN = Pattern.compile ("(\r\n|\n)");

    protected NewLineControlCharacter ()
    {
      super ("NEWLINE", null);
    }
  }

  /**
   * An <code>{color:#ee22aa}</code> indicates switching the color in markup, where the color is
   * given as hex RGB code (ee22aa in this case). It can be escaped with a backslash ('\').
   */
  public static class ColorControlCharacter extends ControlCharacter
  {
    private final Color m_aColor;

    protected ColorControlCharacter (final String sHex)
    {
      super ("COLOR", ColorControlCharacterFactory.TO_ESCAPE);
      final int nR = Integer.parseUnsignedInt (sHex.substring (0, 2), 16);
      final int nG = Integer.parseUnsignedInt (sHex.substring (2, 4), 16);
      final int nB = Integer.parseUnsignedInt (sHex.substring (4, 6), 16);
      this.m_aColor = new Color (nR, nG, nB);
    }

    public Color getColor ()
    {
      return m_aColor;
    }
  }

  private static class StaticControlCharacterFactory implements IControlCharacterFactory
  {

    private final ControlCharacter m_aControlCharacter;
    private final Pattern m_aPattern;

    public StaticControlCharacterFactory (final ControlCharacter aControlCharacter, final Pattern aPattern)
    {
      this.m_aControlCharacter = aControlCharacter;
      this.m_aPattern = aPattern;
    }

    @Override
    public ControlCharacter createControlCharacter (final String sText,
                                                    final Matcher aMatcher,
                                                    final List <CharSequence> aCharactersSoFar)
    {
      return m_aControlCharacter;
    }

    @Override
    public Pattern getPattern ()
    {
      return m_aPattern;
    }

    @Override
    public String unescape (final String sText)
    {
      return m_aControlCharacter.unescape (sText);
    }

    @Override
    public boolean patternMatchesBeginOfLine ()
    {
      return false;
    }

  }

  private static class ColorControlCharacterFactory implements IControlCharacterFactory
  {

    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{color:#(\\p{XDigit}{6})\\}");

    private static final String TO_ESCAPE = "{";

    @Override
    public ControlCharacter createControlCharacter (final String sText,
                                                    final Matcher aMatcher,
                                                    final List <CharSequence> aCharactersSoFar)
    {
      return new ColorControlCharacter (aMatcher.group (2));
    }

    @Override
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    public String unescape (final String sText)
    {
      return sText.replaceAll ("\\\\" + Pattern.quote (TO_ESCAPE), TO_ESCAPE);
    }

    @Override
    public boolean patternMatchesBeginOfLine ()
    {
      return false;
    }

  }

  public static class MetricsControlCharacter extends ControlCharacter
  {
    private final float m_fFontScale;
    private final float m_fBaselineOffsetScale;

    protected MetricsControlCharacter (final String sName, final String sFontScale, final String sBaselineOffset)
    {
      super (sName, MetricsControlCharacterFactory.TO_ESCAPE);
      this.m_fFontScale = _parse (sFontScale, 1);
      this.m_fBaselineOffsetScale = _parse (sBaselineOffset, 0);
    }

    private static float _parse (final String sText, final float fDefaultValue)
    {
      if (sText == null || sText.trim ().isEmpty ())
      {
        return fDefaultValue;
      }
      return Float.parseFloat (sText);
    }

    public float getFontScale ()
    {
      return m_fFontScale;
    }

    public float getBaselineOffsetScale ()
    {
      return m_fBaselineOffsetScale;
    }

  }

  private static class MetricsControlCharacterFactory implements IControlCharacterFactory
  {

    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{(_|\\^)(:(-?\\d+(\\.\\d*)?)\\|(-?\\d+(\\.\\d*)?))?}");

    private static final String TO_ESCAPE = "{";

    @Override
    public ControlCharacter createControlCharacter (final String sText,
                                                    final Matcher aMatcher,
                                                    final List <CharSequence> aCharactersSoFar)
    {
      final boolean bIsSuperscript = "^".equals (aMatcher.group (2));
      final String sName = bIsSuperscript ? "SUPERSCRIPT" : "SUBSCRIPT";
      String sBaselineOffsetScale = bIsSuperscript ? "-0.4" : "0.15";
      if (aMatcher.groupCount () > 6 && aMatcher.group (6) != null)
      {
        sBaselineOffsetScale = aMatcher.group (6);
      }
      String sFontScale = "0.61";
      if (aMatcher.groupCount () > 4 && aMatcher.group (4) != null)
      {
        sFontScale = aMatcher.group (4);
      }
      return new MetricsControlCharacter (sName, sFontScale, sBaselineOffsetScale);
    }

    @Override
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    public String unescape (final String sText)
    {
      return sText.replaceAll ("\\\\" + Pattern.quote (TO_ESCAPE), TO_ESCAPE);
    }

    @Override
    public boolean patternMatchesBeginOfLine ()
    {
      return false;
    }

  }

}
