package rst.pdfbox.layout.util;

import static rst.pdfbox.layout.text.TextSequenceUtil.getEmWidth;
import static rst.pdfbox.layout.text.TextSequenceUtil.getStringWidth;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rst.pdfbox.layout.text.FontDescriptor;

/**
 * Container class for the default word breakers.
 */
public class WordBreakers
{

  /**
   * May by used for legacy compatibility, does not break at all.
   */
  public static class NonBreakingWordBreaker implements IWordBreaker
  {

    @Override
    public Pair <String> breakWord (final String sWord,
                                    final FontDescriptor aFontDescriptor,
                                    final float fMaxWidth,
                                    final boolean bBreakHardIfNecessary) throws IOException
    {
      return null;
    }

  }

  /**
   * Abstract base class for implementing (custom) word breakers. Tries to break the word
   * {@link #breakWordSoft(String, FontDescriptor, float) softly}, or - if this is not possible -
   * {@link #breakWordHard(String, FontDescriptor, float) hard}.
   */
  public static abstract class AbstractWordBreaker implements IWordBreaker
  {

    @Override
    public Pair <String> breakWord (final String sWord,
                                    final FontDescriptor aFontDescriptor,
                                    final float fMaxWidth,
                                    final boolean bBreakHardIfNecessary) throws IOException
    {
      Pair <String> aBrokenWord = breakWordSoft (sWord, aFontDescriptor, fMaxWidth);
      if (aBrokenWord == null && bBreakHardIfNecessary)
      {
        aBrokenWord = breakWordHard (sWord, aFontDescriptor, fMaxWidth);
      }
      return aBrokenWord;
    }

    /**
     * To be implemented by subclasses. Give your best to break the word softly using your strategy,
     * otherwise return <code>null</code>.
     *
     * @param sWord
     *        the word to break.
     * @param aFontDescriptor
     *        describing the font's type and size.
     * @param fMaxWidth
     *        the maximum width to obey.
     * @return the broken word, or <code>null</code> if it cannot be broken.
     * @throws IOException
     *         by pdfbox
     */
    abstract protected Pair <String> breakWordSoft (final String sWord,
                                                    final FontDescriptor aFontDescriptor,
                                                    final float fMaxWidth) throws IOException;

    /**
     * Breaks the word hard at the outermost position that fits the given max width.
     *
     * @param sWord
     *        the word to break.
     * @param aFontDescriptor
     *        describing the font's type and size.
     * @param fMaxWidth
     *        the maximum width to obey.
     * @return the broken word, or <code>null</code> if it cannot be broken.
     * @throws IOException
     *         by pdfbox
     */
    protected Pair <String> breakWordHard (final String sWord,
                                           final FontDescriptor aFontDescriptor,
                                           final float fMaxWidth) throws IOException
    {
      int nCutIndex = (int) (fMaxWidth / getEmWidth (aFontDescriptor));
      float fCurrentWidth = getStringWidth (sWord.substring (0, nCutIndex), aFontDescriptor);
      if (fCurrentWidth > fMaxWidth)
      {
        while (fCurrentWidth > fMaxWidth)
        {
          --nCutIndex;
          fCurrentWidth = getStringWidth (sWord.substring (0, nCutIndex), aFontDescriptor);
        }
        ++nCutIndex;
      }
      else
        if (fCurrentWidth < fMaxWidth)
        {
          while (fCurrentWidth < fMaxWidth)
          {
            ++nCutIndex;
            fCurrentWidth = getStringWidth (sWord.substring (0, nCutIndex), aFontDescriptor);
          }
          --nCutIndex;
        }

      return new Pair <> (sWord.substring (0, nCutIndex), sWord.substring (nCutIndex));
    }

  }

  /**
   * Breaks a word if one of the following characters is found after a non-digit letter:
   * <ul>
   * <li>.</li>
   * <li>,</li>
   * <li>-</li>
   * <li>/</li>
   * </ul>
   */
  public static class DefaultWordBreaker extends AbstractWordBreaker
  {

    /**
     * A letter followed by either <code>-</code>, <code>.</code>, <code>,</code> or <code>/</code>.
     */
    private final Pattern m_aBreakPattern = Pattern.compile ("[A-Za-zÀ-ÖØ-öø-ÿ]([\\-\\.\\,/])");

    @Override
    protected Pair <String> breakWordSoft (final String sWord,
                                           final FontDescriptor aFontDescriptor,
                                           final float fMaxWidth) throws IOException
    {
      final Matcher aMatcher = m_aBreakPattern.matcher (sWord);
      int nBreakIndex = -1;
      boolean bMaxWidthExceeded = false;
      while (!bMaxWidthExceeded && aMatcher.find ())
      {
        final int nCurrentIndex = aMatcher.end ();
        if (nCurrentIndex < sWord.length () - 1)
        {
          if (getStringWidth (sWord.substring (0, nCurrentIndex), aFontDescriptor) < fMaxWidth)
          {
            nBreakIndex = nCurrentIndex;
          }
          else
          {
            bMaxWidthExceeded = true;
          }
        }
      }

      if (nBreakIndex < 0)
      {
        return null;
      }
      return new Pair <> (sWord.substring (0, nBreakIndex), sWord.substring (nBreakIndex));
    }

  }

}
