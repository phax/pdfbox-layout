package rst.pdfbox.layout.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rst.pdfbox.layout.util.WordBreakers.DefaultWordBreaker;
import rst.pdfbox.layout.util.WordBreakers.NonBreakingWordBreaker;

/**
 * Factory for creating a {@link IWordBreaker}. This may be used to define a custom strategy for
 * breaking words. By default the {@link DefaultWordBreaker} is used. Another predefined word
 * breaker is the {@link NonBreakingWordBreaker} which may be used to get the legacy behavior. To
 * switch to a different word breaker, just set the system property
 * {@link #WORD_BREAKER_CLASS_PROPERTY pdfbox.layout.word.breaker} to the class name of the breaker
 * to use.
 */
public class WordBreakerFactory
{

  /**
   * constant for the system property <code>pdfbox.layout.word.breaker</code>.
   */
  public final static String WORD_BREAKER_CLASS_PROPERTY = "pdfbox.layout.word.breaker";

  /**
   * class name of the default word breaker.
   */
  public final static String DEFAULT_WORD_BREAKER_CLASS_NAME = DefaultWordBreaker.class.getName ();

  /**
   * class name of the (legacy) non-breaking word breaker.
   */
  public final static String LEGACY_WORD_BREAKER_CLASS_NAME = NonBreakingWordBreaker.class.getName ();

  private final static IWordBreaker DEFAULT_WORD_BREAKER = new DefaultWordBreaker ();
  private final static Map <String, IWordBreaker> WORD_BREAKERS = new ConcurrentHashMap <> ();

  /**
   * @return the word breaker instance to use.
   */
  public static IWordBreaker getWorkBreaker ()
  {
    return getWorkBreaker (System.getProperty (WORD_BREAKER_CLASS_PROPERTY));
  }

  private static IWordBreaker getWorkBreaker (final String sClassName)
  {
    if (sClassName == null)
    {
      return DEFAULT_WORD_BREAKER;
    }
    IWordBreaker aWordBreaker = WORD_BREAKERS.get (sClassName);
    if (aWordBreaker == null)
    {
      aWordBreaker = _createWordBreakerInstance (sClassName);
      WORD_BREAKERS.put (sClassName, aWordBreaker);
    }
    return aWordBreaker;
  }

  private static IWordBreaker _createWordBreakerInstance (final String sClassName)
  {
    try
    {
      return (IWordBreaker) Class.forName (sClassName).getDeclaredConstructor ().newInstance ();
    }
    catch (final Exception ex)
    {
      throw new RuntimeException ("failed to create word breaker '" + sClassName + "'", ex);
    }
  }

}
