package rst.pdfbox.layout.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.elements.IDividable.Divided;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.util.Pair;
import rst.pdfbox.layout.util.WordBreakerFactory;

/**
 * Utility methods for dealing with text sequences.
 */
public class TextSequenceUtil
{

  /**
   * Dissects the given sequence into {@link TextLine}s.
   *
   * @param aText
   *        the text to extract the lines from.
   * @return the list of text lines.
   * @throws IOException
   *         by pdfbox
   */
  public static List <TextLine> getLines (final ITextSequence aText) throws IOException
  {
    final List <TextLine> aResult = new ArrayList <> ();

    TextLine aLine = new TextLine ();
    for (final ITextFragment aFragment : aText)
    {
      if (aFragment instanceof NewLine)
      {
        aLine.setNewLine ((NewLine) aFragment);
        aResult.add (aLine);
        aLine = new TextLine ();
      }
      else
        if (aFragment instanceof ReplacedWhitespace)
        {
          // ignore replaced whitespace
        }
        else
        {
          aLine.add ((StyledText) aFragment);
        }
    }
    if (!aLine.isEmpty ())
    {
      aResult.add (aLine);
    }
    return aResult;
  }

  /**
   * Word-wraps and divides the given text sequence.
   *
   * @param aText
   *        the text to divide.
   * @param fMaxWidth
   *        the max width used for word-wrapping.
   * @param fMaxHeight
   *        the max height for divide.
   * @return the Divided element containing the parts.
   * @throws IOException
   *         by pdfbox
   */
  public static Divided divide (final ITextSequence aText, final float fMaxWidth, final float fMaxHeight)
                                                                                                          throws IOException
  {
    final TextFlow aWrapped = wordWrap (aText, fMaxWidth);
    final List <TextLine> aLines = getLines (aWrapped);

    final Paragraph aFirst = new Paragraph ();
    final Paragraph aTail = new Paragraph ();
    if (aText instanceof final TextFlow aFlow)
    {
      aFirst.setMaxWidth (aFlow.getMaxWidth ());
      aFirst.setLineSpacing (aFlow.getLineSpacing ());
      aTail.setMaxWidth (aFlow.getMaxWidth ());
      aTail.setLineSpacing (aFlow.getLineSpacing ());
    }
    if (aText instanceof final Paragraph aParagraph)
    {
      aFirst.setAlignment (aParagraph.getAlignment ());
      aFirst.setApplyLineSpacingToFirstLine (aParagraph.isApplyLineSpacingToFirstLine ());
      aTail.setAlignment (aParagraph.getAlignment ());
      aTail.setApplyLineSpacingToFirstLine (aParagraph.isApplyLineSpacingToFirstLine ());
    }

    int nIndex = 0;
    do
    {
      final TextLine aLine = aLines.get (nIndex);
      aFirst.add (aLine);
      ++nIndex;
    } while (nIndex < aLines.size () && aFirst.getHeight () < fMaxHeight);

    if (aFirst.getHeight () > fMaxHeight)
    {
      // remove last line
      --nIndex;
      final TextLine aLine = aLines.get (nIndex);
      for (@SuppressWarnings ("unused")
      final ITextFragment aTextFragment : aLine)
      {
        aFirst.removeLast ();
      }
    }

    for (int i = nIndex; i < aLines.size (); ++i)
    {
      aTail.add (aLines.get (i));
    }
    return new Divided (aFirst, aTail);
  }

  /**
   * Word-wraps the given text sequence in order to fit the max width.
   *
   * @param aText
   *        the text to word-wrap.
   * @param fMaxWidth
   *        the max width to fit.
   * @return the word-wrapped text.
   * @throws IOException
   *         by pdfbox
   */
  public static TextFlow wordWrap (final ITextSequence aText, final float fMaxWidth) throws IOException
  {
    float fIndentation = 0;
    final TextFlow aResult = new TextFlow ();
    float fLineLength = fIndentation;
    boolean bIsWrappedLine = false;
    for (final ITextFragment aFragment : aText)
    {
      if (aFragment instanceof NewLine)
      {
        bIsWrappedLine = aFragment instanceof WrappingNewLine;
        aResult.add (aFragment);
        fLineLength = fIndentation;
        if (fIndentation > 0)
        {
          aResult.add (new Indent (fIndentation).toStyledText ());
        }
      }
      else
        if (aFragment instanceof Indent)
        {
          if (fIndentation > 0)
          {
            // reset indentation
            aResult.removeLast ();
            fIndentation = 0;
          }
          fIndentation = aFragment.getWidth ();
          fLineLength = aFragment.getWidth ();
          aResult.add (((Indent) aFragment).toStyledText ());
        }
        else
        {
          final TextFlow aWords = splitWords (aFragment);
          for (final ITextFragment aWord : aWords)
          {
            WordWrapContext aContext = new WordWrapContext (aWord, fLineLength, fIndentation, bIsWrappedLine);
            do
            {
              aContext = _wordWrap (aContext, fMaxWidth, aResult);
            } while (aContext.isMoreToWrap ());

            fIndentation = aContext.getIndentation ();
            fLineLength = aContext.getLineLength ();
            bIsWrappedLine = aContext.isWrappedLine ();
          }
        }
    }
    return aResult;
  }

  private static WordWrapContext _wordWrap (final WordWrapContext aContext,
                                            final float fMaxWidth,
                                            final TextFlow aResult) throws IOException
  {
    ITextFragment aWord = aContext.getWord ();
    ITextFragment aMoreToWrap = null;
    final float fIndentation = aContext.getIndentation ();
    float fLineLength = aContext.getLineLength ();
    boolean bIsWrappedLine = aContext.isWrappedLine ();

    if (bIsWrappedLine && fLineLength == fIndentation)
    {
      // start of line, replace leading blanks if
      final ITextFragment [] aReplaceLeadingBlanks = _replaceLeadingBlanks (aWord);
      aWord = aReplaceLeadingBlanks[0];
      if (aReplaceLeadingBlanks.length > 1)
      {
        aResult.add (aReplaceLeadingBlanks[1]);
      }
    }

    FontDescriptor aFontDescriptor = aWord.getFontDescriptor ();
    float fLength = aWord.getWidth ();

    if (fMaxWidth > 0 && fLineLength + fLength > fMaxWidth)
    {
      // word exceeds max width, so create new line

      // break hard, if the text does not fit in a full (next) line
      final boolean bBreakHard = fIndentation + fLength > fMaxWidth;

      final Pair <ITextFragment> aBrokenWord = _breakWord (aWord,
                                                           fMaxWidth - fLineLength,
                                                           bBreakHard);
      if (aBrokenWord != null)
      {
        // word is broken
        aWord = aBrokenWord.getFirst ();
        fLength = aWord.getWidth ();
        aMoreToWrap = aBrokenWord.getSecond ();

        aResult.add (aWord);
        if (fLength > 0)
        {
          fLineLength += fLength;
        }

      }
      else
      {
        if (fLineLength == fIndentation)
        {
          // Begin of line and word could now be broke...
          // Well, so we have to use it as it is,
          // it won't get any better in the next line
          aResult.add (aWord);
          if (fLength > 0)
          {
            fLineLength += fLength;
          }

        }
        else
        {
          // give it another try in a new line, there
          // will be more space.
          aMoreToWrap = aWord;
          if (aResult.getLast () != null)
          {
            // since the current word is not used, take
            // font descriptor of last line. Otherwise
            // the line break might be to high
            aFontDescriptor = aResult.getLast ().getFontDescriptor ();
          }
        }
      }

      // wrap line only if not empty
      if (fLineLength > fIndentation)
      {
        // and terminate it with a new line
        aResult.add (new WrappingNewLine (aFontDescriptor));
        bIsWrappedLine = true;
        if (fIndentation > 0)
        {
          aResult.add (new Indent (fIndentation).toStyledText ());
        }
        fLineLength = fIndentation;
      }

    }
    else
    {
      // word fits, so just add it
      aResult.add (aWord);
      if (fLength > 0)
      {
        fLineLength += fLength;
      }
    }

    return new WordWrapContext (aMoreToWrap, fLineLength, fIndentation, bIsWrappedLine);
  }

  /**
   * Replaces leading whitespace by {@link ReplacedWhitespace}.
   *
   * @param aWord
   *        the fragment to replace
   * @return
   */
  private static ITextFragment [] _replaceLeadingBlanks (final ITextFragment aWord)
  {
    final String sText = aWord.getText ();
    int nSplitIndex = 0;
    while (nSplitIndex < sText.length () && Character.isWhitespace (sText.charAt (nSplitIndex)))
    {
      ++nSplitIndex;
    }

    if (nSplitIndex == 0)
    {
      return new ITextFragment [] { aWord };
    }
    final ReplacedWhitespace aWhitespace = new ReplacedWhitespace (sText.substring (0, nSplitIndex),
                                                                   aWord.getFontDescriptor ());
    StyledText aNewWord = null;
    if (aWord instanceof StyledText)
    {
      aNewWord = ((StyledText) aWord).inheritAttributes (sText.substring (nSplitIndex));
    }
    else
    {
      aNewWord = new StyledText (sText.substring (nSplitIndex), aWord.getFontDescriptor (), aWord.getColor ());
    }
    return new ITextFragment [] { aNewWord, aWhitespace };
  }

  /**
   * De-wraps the given text, means any new lines introduced by wrapping will be removed. Also all
   * whitespace removed by wrapping are re-introduced.
   *
   * @param aText
   *        the text to de-wrap.
   * @return the de-wrapped text.
   * @throws IOException
   *         by PDFBox
   */
  public static TextFlow deWrap (final ITextSequence aText) throws IOException
  {
    final TextFlow aResult = new TextFlow ();
    for (final ITextFragment aFragment : aText)
    {
      if (aFragment instanceof WrappingNewLine)
      {
        // skip
      }
      else
        if (aFragment instanceof ReplacedWhitespace)
        {
          aResult.add (((ReplacedWhitespace) aFragment).toReplacedFragment ());
        }
        else
        {
          aResult.add (aFragment);
        }
    }

    if (aText instanceof TextFlow)
    {
      aResult.setLineSpacing (((TextFlow) aText).getLineSpacing ());
    }
    return aResult;
  }

  /**
   * Convencience function that {@link #wordWrap(ITextSequence, float) word-wraps} into
   * {@link #getLines(ITextSequence)}.
   *
   * @param aText
   *        the text to word-wrap.
   * @param fMaxWidth
   *        the max width to fit.
   * @return the word-wrapped text lines.
   * @throws IOException
   *         by pdfbox
   */
  public static List <TextLine> wordWrapToLines (final ITextSequence aText, final float fMaxWidth) throws IOException
  {
    final TextFlow aWrapped = wordWrap (aText, fMaxWidth);
    final List <TextLine> aLines = getLines (aWrapped);
    return aLines;
  }

  /**
   * Splits the fragment into words.
   *
   * @param aText
   *        the text to split.
   * @return the words as a text flow.
   */
  public static TextFlow splitWords (final ITextFragment aText)
  {
    final TextFlow aResult = new TextFlow ();
    if (aText instanceof NewLine)
    {
      aResult.add (aText);
    }
    else
    {
      float fLeftMargin = 0;
      float fRightMargin = 0;
      if (aText instanceof StyledText && ((StyledText) aText).hasMargin ())
      {
        fLeftMargin = ((StyledText) aText).getLeftMargin ();
        fRightMargin = ((StyledText) aText).getRightMargin ();
      }

      final String [] aWords = aText.getText ().split (" ", -1);
      for (int nIndex = 0; nIndex < aWords.length; ++nIndex)
      {
        final String sNewWord = nIndex == 0 ? aWords[nIndex] : " " + aWords[nIndex];

        float fCurrentLeftMargin = 0;
        float fCurrentRightMargin = 0;
        if (nIndex == 0)
        {
          fCurrentLeftMargin = fLeftMargin;
        }
        if (nIndex == aWords.length - 1)
        {
          fCurrentRightMargin = fRightMargin;
        }
        final ITextFragment aDerived = deriveFromExisting (aText, sNewWord, fCurrentLeftMargin, fCurrentRightMargin);
        aResult.add (aDerived);
      }
    }
    return aResult;
  }

  /**
   * Derive a new TextFragment from an existing one, means use attributes like font, color etc.
   *
   * @param aToDeriveFrom
   *        the fragment to derive from.
   * @param sText
   *        the new text.
   * @param fLeftMargin
   *        the new left margin.
   * @param fRightMargin
   *        the new right margin.
   * @return the derived text fragment.
   */
  protected static ITextFragment deriveFromExisting (final ITextFragment aToDeriveFrom,
                                                     final String sText,
                                                     final float fLeftMargin,
                                                     final float fRightMargin)
  {
    if (aToDeriveFrom instanceof StyledText)
    {
      return ((StyledText) aToDeriveFrom).inheritAttributes (sText, fLeftMargin, fRightMargin);
    }
    return new StyledText (sText,
                           aToDeriveFrom.getFontDescriptor (),
                           aToDeriveFrom.getColor (),
                           0,
                           fLeftMargin,
                           fRightMargin);
  }

  private static Pair <ITextFragment> _breakWord (final ITextFragment aWord,
                                                  final float fRemainingLineWidth,
                                                  final boolean bBreakHard) throws IOException
  {
    float fLeftMargin = 0;
    float fRightMargin = 0;
    if (aWord instanceof final StyledText aStyledText)
    {
      fLeftMargin = aStyledText.getLeftMargin ();
      fRightMargin = aStyledText.getRightMargin ();
    }

    final Pair <String> aBrokenWord = WordBreakerFactory.getWorkBreaker ()
                                                        .breakWord (aWord.getText (),
                                                                    aWord.getFontDescriptor (),
                                                                    fRemainingLineWidth - fLeftMargin,
                                                                    bBreakHard);
    if (aBrokenWord == null)
    {
      return null;
    }

    // break at calculated index
    final ITextFragment aHead = deriveFromExisting (aWord, aBrokenWord.getFirst (), fLeftMargin, 0);
    final ITextFragment aTail = deriveFromExisting (aWord, aBrokenWord.getSecond (), 0, fRightMargin);

    return new Pair <> (aHead, aTail);
  }

  /**
   * Returns the width of the character <code>M</code> in the given font.
   *
   * @param aFontDescriptor
   *        font and size.
   * @return the width of <code>M</code>.
   * @throws IOException
   *         by pdfbox
   */
  public static float getEmWidth (final FontDescriptor aFontDescriptor) throws IOException
  {
    return getStringWidth ("M", aFontDescriptor);
  }

  /**
   * Returns the width of the given text in the given font.
   *
   * @param sText
   *        the text to measure.
   * @param aFontDescriptor
   *        font and size.
   * @return the width of given text.
   * @throws IOException
   *         by pdfbox
   */
  public static float getStringWidth (final String sText, final FontDescriptor aFontDescriptor) throws IOException
  {
    return aFontDescriptor.getSize () * aFontDescriptor.getFont ().getStringWidth (sText) / 1000;
  }

  /**
   * Draws the given text sequence to the PDPageContentStream at the given position.
   *
   * @param aText
   *        the text to draw.
   * @param aContentStream
   *        the stream to draw to
   * @param aUpperLeft
   *        the position of the start of the first line.
   * @param aDrawListener
   *        the listener to {@link IDrawListener#drawn(Object, Position, float, float) notify} on
   *        drawn objects.
   * @param eAlignment
   *        how to align the text lines.
   * @param fMaxWidth
   *        if &gt; 0, the text may be word-wrapped to match the width.
   * @param fLineSpacing
   *        the line spacing factor.
   * @param bApplyLineSpacingToFirstLine
   *        indicates if the line spacing should be applied to the first line also. Makes sense in
   *        most cases to do so.
   * @throws IOException
   *         by pdfbox
   */
  public static void drawText (final ITextSequence aText,
                               final PDPageContentStream aContentStream,
                               final Position aUpperLeft,
                               final IDrawListener aDrawListener,
                               final EAlignment eAlignment,
                               final float fMaxWidth,
                               final float fLineSpacing,
                               final boolean bApplyLineSpacingToFirstLine) throws IOException
  {
    final List <TextLine> aLines = wordWrapToLines (aText, fMaxWidth);
    final float fMaxLineWidth = Math.max (fMaxWidth, getMaxWidth (aLines));
    Position aPosition = aUpperLeft;
    float fLastLineHeight = 0;
    for (int i = 0; i < aLines.size (); i++)
    {
      final boolean bApplyLineSpacing = i > 0 || bApplyLineSpacingToFirstLine;
      final TextLine aTextLine = aLines.get (i);
      final float fCurrentLineHeight = aTextLine.getHeight ();
      float fLead = fLastLineHeight;
      if (bApplyLineSpacing)
      {
        fLead += (fCurrentLineHeight * (fLineSpacing - 1));
      }
      fLastLineHeight = fCurrentLineHeight;
      aPosition = aPosition.add (0, -fLead);
      aTextLine.drawAligned (aContentStream, aPosition, eAlignment, fMaxLineWidth, aDrawListener);
    }
  }

  /**
   * Gets the (left) offset of the line with respect to the target width and alignment.
   *
   * @param aTextLine
   *        the text
   * @param fTargetWidth
   *        the target width
   * @param eAlignment
   *        the alignment of the line.
   * @return the left offset.
   * @throws IOException
   *         by pdfbox
   */
  public static float getOffset (final ITextSequence aTextLine, final float fTargetWidth, final EAlignment eAlignment)
                                                                                                                       throws IOException
  {
    return switch (eAlignment)
    {
      case Right -> fTargetWidth - aTextLine.getWidth ();
      case Center -> (fTargetWidth - aTextLine.getWidth ()) / 2f;
      default -> 0;
    };
  }

  /**
   * Calculates the max width of all text lines.
   *
   * @param aLines
   *        the lines for which to calculate the max width.
   * @return the max width of the lines.
   * @throws IOException
   *         by pdfbox.
   */
  public static float getMaxWidth (final Iterable <TextLine> aLines) throws IOException
  {
    float fMax = 0;
    for (final TextLine aLine : aLines)
    {
      fMax = Math.max (fMax, aLine.getWidth ());
    }
    return fMax;
  }

  /**
   * Calculates the width of the text
   *
   * @param aTextSequence
   *        the text.
   * @param fMaxWidth
   *        if &gt; 0, the text may be word-wrapped to match the width.
   * @return the width of the text.
   * @throws IOException
   *         by pdfbox.
   */
  public static float getWidth (final ITextSequence aTextSequence, final float fMaxWidth) throws IOException
  {
    final List <TextLine> aLines = wordWrapToLines (aTextSequence, fMaxWidth);
    float fMax = 0;
    for (final TextLine aLine : aLines)
    {
      fMax = Math.max (fMax, aLine.getWidth ());
    }
    return fMax;
  }

  /**
   * Calculates the height of the text
   *
   * @param aTextSequence
   *        the text.
   * @param fMaxWidth
   *        if &gt; 0, the text may be word-wrapped to match the width.
   * @param fLineSpacing
   *        the line spacing factor.
   * @param bApplyLineSpacingToFirstLine
   *        indicates if the line spacing should be applied to the first line also. Makes sense in
   *        most cases to do so.
   * @return the height of the text.
   * @throws IOException
   *         by pdfbox
   */
  public static float getHeight (final ITextSequence aTextSequence,
                                 final float fMaxWidth,
                                 final float fLineSpacing,
                                 final boolean bApplyLineSpacingToFirstLine) throws IOException
  {
    final List <TextLine> aLines = wordWrapToLines (aTextSequence, fMaxWidth);
    float fSum = 0;
    for (int i = 0; i < aLines.size (); i++)
    {
      final boolean bApplyLineSpacing = i > 0 || bApplyLineSpacingToFirstLine;
      final TextLine aLine = aLines.get (i);
      float fLineHeight = aLine.getHeight ();
      if (bApplyLineSpacing)
      {
        fLineHeight *= fLineSpacing;
      }
      fSum += fLineHeight;
    }
    return fSum;
  }

  private static class WordWrapContext
  {
    private final ITextFragment m_aWord;
    private final float m_fLineLength;
    private final float m_fIndentation;
    private final boolean m_bIsWrappedLine;

    public WordWrapContext (final ITextFragment aWord,
                            final float fLineLength,
                            final float fIndentation,
                            final boolean bIsWrappedLine)
    {
      this.m_aWord = aWord;
      this.m_fLineLength = fLineLength;
      this.m_fIndentation = fIndentation;
      this.m_bIsWrappedLine = bIsWrappedLine;
    }

    public ITextFragment getWord ()
    {
      return m_aWord;
    }

    public float getLineLength ()
    {
      return m_fLineLength;
    }

    public float getIndentation ()
    {
      return m_fIndentation;
    }

    public boolean isWrappedLine ()
    {
      return m_bIsWrappedLine;
    }

    public boolean isMoreToWrap ()
    {
      return getWord () != null;
    }

  }
}
