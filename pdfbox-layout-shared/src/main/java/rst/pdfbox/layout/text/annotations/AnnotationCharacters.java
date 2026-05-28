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
  private final static List <IAnnotationControlCharacterFactory <?>> FACTORIES = new CopyOnWriteArrayList <> ();

  static
  {
    register (new HyperlinkControlCharacterFactory ());
    register (new AnchorControlCharacterFactory ());
    register (new UnderlineControlCharacterFactory ());
  }

  /**
   * Use this method to register your (custom) annotation control character factory.
   *
   * @param factory
   *        the factory to register.
   */
  public static void register (final IAnnotationControlCharacterFactory <?> factory)
  {
    FACTORIES.add (factory);
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

    private final static Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{link(:(ul|none))?(\\[(([^}]+))\\])?\\}");

    private final static String TO_ESCAPE = "{";

    @Override
    public HyperlinkControlCharacter createControlCharacter (final String text,
                                                             final Matcher matcher,
                                                             final List <CharSequence> charactersSoFar)
    {
      return new HyperlinkControlCharacter (matcher.group (5), matcher.group (3));
    }

    @Override
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    public String unescape (final String text)
    {
      return text.replaceAll ("\\\\" + Pattern.quote (TO_ESCAPE), TO_ESCAPE);
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

    private final static Pattern PATTERN = Pattern.compile ("(?<!\\\\)(\\\\\\\\)*\\{anchor(:((\\w+)))?\\}");

    private final static String TO_ESCAPE = "{";

    @Override
    public AnchorControlCharacter createControlCharacter (final String text,
                                                          final Matcher matcher,
                                                          final List <CharSequence> charactersSoFar)
    {
      return new AnchorControlCharacter (matcher.group (3));
    }

    @Override
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    public String unescape (final String text)
    {
      return text.replaceAll ("\\\\" + Pattern.quote (TO_ESCAPE), TO_ESCAPE);
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

    private final static String TO_ESCAPE = "__";

    @Override
    public UnderlineControlCharacter createControlCharacter (final String text,
                                                             final Matcher matcher,
                                                             final List <CharSequence> charactersSoFar)
    {
      return new UnderlineControlCharacter (matcher.group (4), matcher.group (6));
    }

    @Override
    public Pattern getPattern ()
    {
      return PATTERN;
    }

    @Override
    public String unescape (final String text)
    {
      return text.replaceAll ("\\\\" + Pattern.quote (TO_ESCAPE), TO_ESCAPE);
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
    private HyperlinkAnnotation hyperlink;

    protected HyperlinkControlCharacter (final String hyperlink, final String linkStyle)
    {
      super ("HYPERLINK", HyperlinkControlCharacterFactory.TO_ESCAPE);
      if (hyperlink != null)
      {
        ELinkStyle style = ELinkStyle.ul;
        if (linkStyle != null)
        {
          style = ELinkStyle.valueOf (linkStyle);
        }
        this.hyperlink = new HyperlinkAnnotation (hyperlink, style);
      }
    }

    @Override
    public HyperlinkAnnotation getAnnotation ()
    {
      return hyperlink;
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
    private AnchorAnnotation anchor;

    protected AnchorControlCharacter (final String anchor)
    {
      super ("ANCHOR", AnchorControlCharacterFactory.TO_ESCAPE);
      if (anchor != null)
      {
        this.anchor = new AnchorAnnotation (anchor);
      }
    }

    @Override
    public AnchorAnnotation getAnnotation ()
    {
      return anchor;
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
    public final static String UNDERLINE_DEFAULT_BASELINE_OFFSET_SCALE_PROPERTY = "pdfbox.layout.underline.baseline.offset.scale.default";

    private static Float defaultBaselineOffsetScale;
    private final UnderlineAnnotation line;

    protected UnderlineControlCharacter ()
    {
      this (null, null);
    }

    protected UnderlineControlCharacter (final String baselineOffsetScaleValue, final String lineWeightValue)
    {
      super ("UNDERLINE", UnderlineControlCharacterFactory.TO_ESCAPE);

      final float baselineOffsetScale = parseFloat (baselineOffsetScaleValue, getdefaultBaselineOffsetScale ());
      final float lineWeight = parseFloat (lineWeightValue, 1f);
      line = new UnderlineAnnotation (baselineOffsetScale, lineWeight);
    }

    @Override
    public UnderlineAnnotation getAnnotation ()
    {
      return line;
    }

    @Override
    public Class <UnderlineAnnotation> getAnnotationType ()
    {
      return UnderlineAnnotation.class;
    }

    private static float parseFloat (final String text, final float defaultValue)
    {
      if (text == null)
      {
        return defaultValue;
      }
      try
      {
        return Float.parseFloat (text);
      }
      catch (final NumberFormatException e)
      {
        return defaultValue;
      }
    }

    private static float getdefaultBaselineOffsetScale ()
    {
      if (defaultBaselineOffsetScale == null)
      {
        defaultBaselineOffsetScale = Float.parseFloat (System.getProperty (UNDERLINE_DEFAULT_BASELINE_OFFSET_SCALE_PROPERTY,
                                                                           "-0.1"));
      }
      return defaultBaselineOffsetScale;
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
    T createControlCharacter (String text, Matcher matcher, final List <CharSequence> charactersSoFar);

  }

  /**
   * Common base class for annotation control characters.
   */
  public static abstract class AbstractAnnotationControlCharacter <T extends IAnnotation> extends ControlCharacter
  {

    protected AbstractAnnotationControlCharacter (final String description, final String charaterToEscape)
    {
      super (description, charaterToEscape);
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
