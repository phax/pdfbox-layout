package rst.pdfbox.layout.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import rst.pdfbox.layout.util.Enumerators.AlphabeticEnumerator;
import rst.pdfbox.layout.util.Enumerators.ArabicEnumerator;
import rst.pdfbox.layout.util.Enumerators.LowerCaseAlphabeticEnumerator;
import rst.pdfbox.layout.util.Enumerators.LowerCaseRomanEnumerator;
import rst.pdfbox.layout.util.Enumerators.RomanEnumerator;

/**
 * Enumerators are created using this factory. It allows you to register and use your own
 * enumerations, if the built ins does not satisfy your needs.<br>
 * Currently supported are:
 * <table summary="">
 * <tr>
 * <th>Name</th>
 * <th>Key</th>
 * <th>Seperator</th>
 * </tr>
 * <tr>
 * <td>Arabic</td>
 * <td align="center">1</td>
 * <td align="center">.</td>
 * </tr>
 * <tr>
 * <td>Roman</td>
 * <td align="center">I</td>
 * <td align="center">.</td>
 * </tr>
 * <tr>
 * <td>Roman Lower Case</td>
 * <td align="center">i</td>
 * <td align="center">.</td>
 * </tr>
 * <tr>
 * <td>Alphabetic</td>
 * <td align="center">A</td>
 * <td align="center">)</td>
 * </tr>
 * <tr>
 * <td>Alphabetic Lower Case</td>
 * <td align="center">a</td>
 * <td align="center">)</td>
 * </tr>
 * </table>
 */
public class EnumeratorFactory
{

  private static final Map <String, Class <? extends Enumerator>> ENUMERATORS = new ConcurrentHashMap <> ();

  static
  {
    register ("1", ArabicEnumerator.class);
    register ("I", RomanEnumerator.class);
    register ("i", LowerCaseRomanEnumerator.class);
    register ("A", AlphabeticEnumerator.class);
    register ("a", LowerCaseAlphabeticEnumerator.class);
  }

  /**
   * Registers an Enumerator class for a given key.
   *
   * @param sKey
   *        the key (character) used in markup.
   * @param aEnumeratorClass
   *        the enumerator class.
   */
  public static void register (final String sKey, final Class <? extends Enumerator> aEnumeratorClass)
  {
    ENUMERATORS.put (sKey, aEnumeratorClass);
  }

  /**
   * Creates an Enumerator for the given key.
   *
   * @param sKey
   *        the key of the enumerator.
   * @return the created enumerator.
   */
  public static Enumerator createEnumerator (final String sKey)
  {
    final Class <? extends Enumerator> aEnumeratorClass = ENUMERATORS.get (sKey);
    if (aEnumeratorClass == null)
    {
      throw new IllegalArgumentException ("no enumerator found for '" + sKey + "'");
    }
    try
    {
      return aEnumeratorClass.getDeclaredConstructor ().newInstance ();
    }
    catch (final Exception ex)
    {
      throw new RuntimeException ("failed to create enumerator", ex);
    }
  }
}
