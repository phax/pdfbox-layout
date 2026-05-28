package rst.pdfbox.layout.text.annotations;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rst.pdfbox.layout.text.ControlCharacter;
import rst.pdfbox.layout.text.ControlCharacters.IControlCharacterFactory;
import rst.pdfbox.layout.text.annotations.Annotations.AnchorAnnotation;
import rst.pdfbox.layout.text.annotations.Annotations.HyperlinkAnnotation;
import rst.pdfbox.layout.text.annotations.Annotations.HyperlinkAnnotation.ELinkStyle;
import rst.pdfbox.layout.text.annotations.Annotations.UnderlineAnnotation;

/**
 * Container for annotation control characters.
 */
public class AnnotationCharacters
{
  private static final List <IAnnotationControlCharacterFactory <?>> FACTORIES = new CopyOnWriteArrayList <> ();

  static
  {
    register (new HyperlinkControlCharacterFactory ());
    register (new AnchorControlCharacterFactory ());
    register (new UnderlineControlCharacterFactory ());
  }

  /**
   * Use this method to register your (custom) annotation control character factory.
   *
   * @param aFactory
   *        the factory to register.
   */
  public static void register (final IAnnotationControlCharacterFactory <?> aFactory)
  {
    FACTORIES.add (aFactory);
  }

  /**
   * Drops every registered factory and re-registers only the built-in trio (hyperlink, anchor,
   * underline). Intended for tests that run several example main() methods in a single JVM and need
   * to undo registrations made by previous tests.
   */
  public static void reset ()
  {
    FACTORIES.clear ();
    register (new HyperlinkControlCharacterFactory ());
    register (new AnchorControlCharacterFactory ());
    register (new UnderlineControlCharacterFactory ());
  }

  /**
   * @return all the default and custom annotation control character factories.
   */
  public static Iterable <IAnnotationControlCharacterFactory <?>> getFactories ()
  {
    return FACTORIES;
  }

  private static class HyperlinkControlCharacterFactory implements
                                                        IAnnotationControlCharacterFactory <HyperlinkControlCharacter>
  {

    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{link(:(ul|none))?(\\[(([^}]+))\\])?\\}");

    private static final String TO_ESCAPE = "{";

    @Override
    public HyperlinkControlCharacter createControlCharacter (final String sText,
                                                             final Matcher aMatcher,
                                                             final List <CharSequence> aCharactersSoFar)
    {
      return new HyperlinkControlCharacter (aMatcher.group (5), aMatcher.group (3));
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

  private static class AnchorControlCharacterFactory implements
                                                     IAnnotationControlCharacterFactory <AnchorControlCharacter>
  {

    private static final Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{anchor(:((\\w+)))?\\}");

    private static final String TO_ESCAPE = "{";

    @Override
    public AnchorControlCharacter createControlCharacter (final String sText,
                                                          final Matcher aMatcher,
                                                          final List <CharSequence> aCharactersSoFar)
    {
      return new AnchorControlCharacter (aMatcher.group (3));
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

  private static class UnderlineControlCharacterFactory implements
                                                        IAnnotationControlCharacterFactory <UnderlineControlCharacter>
  {

    private static Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*(__(\\{(-?\\d+(\\.\\d*)?)?\\:(-?\\d+(\\.\\d*)?)?\\})?)");

    private static final String TO_ESCAPE = "__";

    @Override
    public UnderlineControlCharacter createControlCharacter (final String sText,
                                                             final Matcher aMatcher,
                                                             final List <CharSequence> aCharactersSoFar)
    {
      return new UnderlineControlCharacter (aMatcher.group (4), aMatcher.group (6));
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

  /**
   * A <code>{link:#title1}</code> indicates an internal link to the {@link AnchorControlCharacter
   * anchor} <code>title1</code>. Any other link (not starting with <code>#</code> will be treated
   * as an external link. It can be escaped with a backslash ('\').
   */
  public static class HyperlinkControlCharacter extends AbstractAnnotationControlCharacter <HyperlinkAnnotation>
  {
    private HyperlinkAnnotation m_aHyperlink;

    protected HyperlinkControlCharacter (final String sHyperlink, final String sLinkStyle)
    {
      super ("HYPERLINK", HyperlinkControlCharacterFactory.TO_ESCAPE);
      if (sHyperlink != null)
      {
        ELinkStyle eStyle = ELinkStyle.ul;
        if (sLinkStyle != null)
        {
          eStyle = ELinkStyle.valueOf (sLinkStyle);
        }
        this.m_aHyperlink = new HyperlinkAnnotation (sHyperlink, eStyle);
      }
    }

    @Override
    public HyperlinkAnnotation getAnnotation ()
    {
      return m_aHyperlink;
    }

    @Override
    public Class <HyperlinkAnnotation> getAnnotationType ()
    {
      return HyperlinkAnnotation.class;
    }
  }

  /**
   * An <code>{color:#ee22aa}</code> indicates switching the color in markup, where the color is
   * given as hex RGB code (ee22aa in this case). It can be escaped with a backslash ('\').
   */
  public static class AnchorControlCharacter extends AbstractAnnotationControlCharacter <AnchorAnnotation>
  {
    private AnchorAnnotation m_aAnchor;

    protected AnchorControlCharacter (final String sAnchor)
    {
      super ("ANCHOR", AnchorControlCharacterFactory.TO_ESCAPE);
      if (sAnchor != null)
      {
        this.m_aAnchor = new AnchorAnnotation (sAnchor);
      }
    }

    @Override
    public AnchorAnnotation getAnnotation ()
    {
      return m_aAnchor;
    }

    @Override
    public Class <AnchorAnnotation> getAnnotationType ()
    {
      return AnchorAnnotation.class;
    }

  }

  /**
   * Control character for underline. It can be escaped with a backslash ('\').
   */
  public static class UnderlineControlCharacter extends AbstractAnnotationControlCharacter <UnderlineAnnotation>
  {

    /**
     * constant for the system property
     * <code>pdfbox.layout.underline.baseline.offset.scale.default</code>.
     */
    public static final String UNDERLINE_DEFAULT_BASELINE_OFFSET_SCALE_PROPERTY = "pdfbox.layout.underline.baseline.offset.scale.default";

    private static Float s_aDefaultBaselineOffsetScale;
    private final UnderlineAnnotation m_aLine;

    protected UnderlineControlCharacter ()
    {
      this (null, null);
    }

    protected UnderlineControlCharacter (final String sBaselineOffsetScaleValue, final String sLineWeightValue)
    {
      super ("UNDERLINE", UnderlineControlCharacterFactory.TO_ESCAPE);

      final float fBaselineOffsetScale = _parseFloat (sBaselineOffsetScaleValue, _getDefaultBaselineOffsetScale ());
      final float fLineWeight = _parseFloat (sLineWeightValue, 1f);
      m_aLine = new UnderlineAnnotation (fBaselineOffsetScale, fLineWeight);
    }

    @Override
    public UnderlineAnnotation getAnnotation ()
    {
      return m_aLine;
    }

    @Override
    public Class <UnderlineAnnotation> getAnnotationType ()
    {
      return UnderlineAnnotation.class;
    }

    private static float _parseFloat (final String sText, final float fDefaultValue)
    {
      if (sText == null)
      {
        return fDefaultValue;
      }
      try
      {
        return Float.parseFloat (sText);
      }
      catch (final NumberFormatException ex)
      {
        return fDefaultValue;
      }
    }

    private static float _getDefaultBaselineOffsetScale ()
    {
      if (s_aDefaultBaselineOffsetScale == null)
      {
        s_aDefaultBaselineOffsetScale = Float.valueOf (Float.parseFloat (System.getProperty (UNDERLINE_DEFAULT_BASELINE_OFFSET_SCALE_PROPERTY,
                                                                                             "-0.1")));
      }
      return s_aDefaultBaselineOffsetScale.floatValue ();
    }
  }

  /**
   * Specialized interface for control character factories for annotations.
   *
   * @param <T>
   *        the type of the annotation control character.
   */
  public interface IAnnotationControlCharacterFactory <T extends AbstractAnnotationControlCharacter <? extends IAnnotation>>
                                                      extends
                                                      IControlCharacterFactory
  {
    T createControlCharacter (final String sText, final Matcher aMatcher, final List <CharSequence> aCharactersSoFar);

  }

  /**
   * Common base class for annotation control characters.
   */
  public static abstract class AbstractAnnotationControlCharacter <T extends IAnnotation> extends ControlCharacter
  {

    protected AbstractAnnotationControlCharacter (final String sDescription, final String sCharaterToEscape)
    {
      super (sDescription, sCharaterToEscape);
    }

    /**
     * @return the associated annotation.
     */
    public abstract T getAnnotation ();

    /**
     * @return the type of the annotation.
     */
    public abstract Class <T> getAnnotationType ();

  }

}
