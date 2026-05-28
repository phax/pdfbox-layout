package rst.pdfbox.layout.util;

import java.io.IOException;

import rst.pdfbox.layout.text.FontDescriptor;

/**
 * This interface may be used to implement different strategies on how to break a word, if it does
 * not fit into a line.
 */
public interface IWordBreaker
{

  /**
   * Breaks the word in order to fit the given maximum width.
   *
   * @param sWord
   *        the word to break.
   * @param aFontDescriptor
   *        describing the font's type and size.
   * @param fMaxWidth
   *        the maximum width to obey.
   * @param bBreakHardIfNecessary
   *        indicates if the word should be broken hard to fit the width, in case there is no
   *        suitable position for breaking it adequately.
   * @return the broken word, or <code>null</code> if it cannot be broken.
   * @throws IOException
   *         by pdfbox
   */
  Pair <String> breakWord (final String sWord,
                           final FontDescriptor aFontDescriptor,
                           final float fMaxWidth,
                           final boolean bBreakHardIfNecessary) throws IOException;

}
