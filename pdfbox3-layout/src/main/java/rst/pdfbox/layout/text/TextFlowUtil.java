package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;

import org.apache.pdfbox.pdmodel.font.PDFont;

import rst.pdfbox.layout.text.ControlCharacters.BoldControlCharacter;
import rst.pdfbox.layout.text.ControlCharacters.ColorControlCharacter;
import rst.pdfbox.layout.text.ControlCharacters.IControlCharacterFactory;
import rst.pdfbox.layout.text.ControlCharacters.ItalicControlCharacter;
import rst.pdfbox.layout.text.ControlCharacters.MetricsControlCharacter;
import rst.pdfbox.layout.text.ControlCharacters.NewLineControlCharacter;
import rst.pdfbox.layout.text.IndentCharacters.IndentCharacter;
import rst.pdfbox.layout.text.annotations.AnnotatedStyledText;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters.AbstractAnnotationControlCharacter;
import rst.pdfbox.layout.text.annotations.AnnotationCharacters.IAnnotationControlCharacterFactory;
import rst.pdfbox.layout.text.annotations.IAnnotation;

public class TextFlowUtil
{

  /**
   * Creates a text flow from the given text. The text may contain line breaks.
   *
   * @param sText
   *        the text
   * @param fFontSize
   *        the font size to use.
   * @param aFont
   *        the font to use.
   * @return the created text flow.
   * @throws IOException
   *         by pdfbox
   */
  public static TextFlow createTextFlow (final String sText, final float fFontSize, final PDFont aFont)
                                                                                                        throws IOException
  {
    final Iterable <CharSequence> aParts = fromPlainText (sText);
    return createTextFlow (aParts, fFontSize, aFont, aFont, aFont, aFont);
  }

  /**
   * Convenience alternative to
   * {@link #createTextFlowFromMarkup(String, float, PDFont, PDFont, PDFont, PDFont)} which allows
   * to specifies the fonts to use by using the {@link EBaseFont} enum.
   *
   * @param sMarkup
   *        the markup text.
   * @param fFontSize
   *        the font size to use.
   * @param eBaseFont
   *        the base font describing the bundle of plain/blold/italic/bold-italic fonts.
   * @return the created text flow.
   * @throws IOException
   *         by pdfbox
   */
  public static TextFlow createTextFlowFromMarkup (final String sMarkup,
                                                   final float fFontSize,
                                                   final EBaseFont eBaseFont) throws IOException
  {
    return createTextFlowFromMarkup (sMarkup,
                                     fFontSize,
                                     eBaseFont.getPlainFont (),
                                     eBaseFont.getBoldFont (),
                                     eBaseFont.getItalicFont (),
                                     eBaseFont.getBoldItalicFont ());
  }

  /**
   * Creates a text flow from the given text. The text may contain line breaks, and also supports
   * some markup for creating bold and italic fonts. The following raw text
   *
   * <pre>
   * Markup supports *bold*, _italic_, and *even _mixed* markup_.
   * </pre>
   *
   * is rendered like this:
   *
   * <pre>
   * Markup supports <b>bold</b>, <em>italic</em>, and <b>even <em>mixed</em></b><em> markup</em>.
   * </pre>
   *
   * Use backslash to escape special characters '*', '_' and '\' itself:
   *
   * <pre>
   * Escape \* with \\\* and \_ with \\\_ in markup.
   * </pre>
   *
   * is rendered like this:
   *
   * <pre>
   * Escape * with \* and _ with \_ in markup.
   * </pre>
   *
   * @param sMarkup
   *        the markup text.
   * @param fFontSize
   *        the font size to use.
   * @param aPlainFont
   *        the plain font.
   * @param aBoldFont
   *        the bold font.
   * @param aItalicFont
   *        the italic font.
   * @param aBoldItalicFont
   *        the bold-italic font.
   * @return the created text flow.
   * @throws IOException
   *         by pdfbox
   */
  public static TextFlow createTextFlowFromMarkup (final String sMarkup,
                                                   final float fFontSize,
                                                   final PDFont aPlainFont,
                                                   final PDFont aBoldFont,
                                                   final PDFont aItalicFont,
                                                   final PDFont aBoldItalicFont) throws IOException
  {
    final Iterable <CharSequence> aParts = fromMarkup (sMarkup);
    return createTextFlow (aParts, fFontSize, aPlainFont, aBoldFont, aItalicFont, aBoldItalicFont);
  }

  /**
   * Actually creates the text flow from the given (markup) text.
   *
   * @param aParts
   *        the parts to create the text flow from.
   * @param fFontSize
   *        the font size to use.
   * @param aPlainFont
   *        the plain font.
   * @param aBoldFont
   *        the bold font.
   * @param aItalicFont
   *        the italic font.
   * @param aBoldItalicFont
   *        the bold-italic font.
   * @return the created text flow.
   * @throws IOException
   *         by pdfbox
   */
  protected static TextFlow createTextFlow (final Iterable <CharSequence> aParts,
                                            final float fFontSize,
                                            final PDFont aPlainFont,
                                            final PDFont aBoldFont,
                                            final PDFont aItalicFont,
                                            final PDFont aBoldItalicFont) throws IOException
  {
    final TextFlow aResult = new TextFlow ();
    boolean bBold = false;
    boolean bItalic = false;
    Color aColor = Color.black;
    MetricsControlCharacter aMetricsControl = null;
    final Map <Class <? extends IAnnotation>, IAnnotation> aAnnotationMap = new HashMap <> ();
    final Stack <IndentCharacter> aIndentStack = new Stack <> ();
    for (final CharSequence aFragment : aParts)
    {

      if (aFragment instanceof ControlCharacter)
      {
        if (aFragment instanceof NewLineControlCharacter)
        {
          aResult.add (new NewLine (fFontSize));
        }
        if (aFragment instanceof BoldControlCharacter)
        {
          bBold = !bBold;
        }
        if (aFragment instanceof ItalicControlCharacter)
        {
          bItalic = !bItalic;
        }
        if (aFragment instanceof ColorControlCharacter)
        {
          aColor = ((ColorControlCharacter) aFragment).getColor ();
        }
        if (aFragment instanceof AbstractAnnotationControlCharacter)
        {
          final AbstractAnnotationControlCharacter <?> aAnnotationControlCharacter = (AbstractAnnotationControlCharacter <?>) aFragment;
          if (aAnnotationMap.containsKey (aAnnotationControlCharacter.getAnnotationType ()))
          {
            aAnnotationMap.remove (aAnnotationControlCharacter.getAnnotationType ());
          }
          else
          {
            aAnnotationMap.put (aAnnotationControlCharacter.getAnnotationType (),
                                aAnnotationControlCharacter.getAnnotation ());
          }
        }
        if (aFragment instanceof MetricsControlCharacter)
        {
          if (aMetricsControl != null && aMetricsControl.toString ().equals (aFragment.toString ()))
          {
            // end marker
            aMetricsControl = null;
          }
          else
          {
            aMetricsControl = (MetricsControlCharacter) aFragment;
          }
        }
        if (aFragment instanceof IndentCharacter aCurrentIndent)
        {
          if (aCurrentIndent.getLevel () == 0)
          {
            // indentation of 0 resets indent
            aIndentStack.clear ();
            aResult.add (Indent.UNINDENT);
            continue;
          }
          IndentCharacter aLast = null;
          while (!aIndentStack.isEmpty () &&
                 aIndentStack.peek () != null &&
                 aCurrentIndent.getLevel () <= aIndentStack.peek ().getLevel ())
          {
            aLast = aIndentStack.pop ();
          }
          if (aLast != null && aLast.equals (aCurrentIndent))
          {
            aCurrentIndent = aLast;
          }
          aIndentStack.push (aCurrentIndent);
          aResult.add (aCurrentIndent.createNewIndent (fFontSize, aPlainFont, aColor));
        }
      }
      else
      {
        final PDFont aFont = getFont (bBold, bItalic, aPlainFont, aBoldFont, aItalicFont, aBoldItalicFont);
        float fBaselineOffset = 0;
        float fCurrentFontSize = fFontSize;
        if (aMetricsControl != null)
        {
          fBaselineOffset = aMetricsControl.getBaselineOffsetScale () * fFontSize;
          fCurrentFontSize *= aMetricsControl.getFontScale ();
        }
        if (aAnnotationMap.isEmpty ())
        {
          final StyledText aStyledText = new StyledText (aFragment.toString (),
                                                         fCurrentFontSize,
                                                         aFont,
                                                         aColor,
                                                         fBaselineOffset);
          aResult.add (aStyledText);
        }
        else
        {
          final AnnotatedStyledText aStyledText = new AnnotatedStyledText (aFragment.toString (),
                                                                           fCurrentFontSize,
                                                                           aFont,
                                                                           aColor,
                                                                           fBaselineOffset,
                                                                           aAnnotationMap.values ());
          aResult.add (aStyledText);
        }
      }
    }
    return aResult;
  }

  protected static PDFont getFont (final boolean bBold,
                                   final boolean bItalic,
                                   final PDFont aPlainFont,
                                   final PDFont aBoldFont,
                                   final PDFont aItalicFont,
                                   final PDFont aBoldItalicFont)
  {
    PDFont aFont = aPlainFont;
    if (bBold && !bItalic)
    {
      aFont = aBoldFont;
    }
    else
      if (!bBold && bItalic)
      {
        aFont = aItalicFont;
      }
      else
        if (bBold && bItalic)
        {
          aFont = aBoldItalicFont;
        }
    return aFont;
  }

  /**
   * Creates a char sequence where new-line is replaced by the corresponding
   * {@link ControlCharacter}.
   *
   * @param aText
   *        the original text.
   * @return the create char sequence.
   */
  public static Iterable <CharSequence> fromPlainText (final CharSequence aText)
  {
    return fromPlainText (Collections.singleton (aText));
  }

  /**
   * Creates a char sequence where new-line is replaced by the corresponding
   * {@link ControlCharacter}.
   *
   * @param aText
   *        the original text.
   * @return the create char sequence.
   */
  public static Iterable <CharSequence> fromPlainText (final Iterable <CharSequence> aText)
  {
    Iterable <CharSequence> aResult = splitByControlCharacter (ControlCharacters.NEWLINE_FACTORY, aText);
    aResult = _unescapeBackslash (aResult);
    return aResult;
  }

  /**
   * Creates a char sequence where new-line, asterisk and underscore are replaced by their
   * corresponding {@link ControlCharacter}.
   *
   * @param aMarkup
   *        the markup.
   * @return the create char sequence.
   */
  public static Iterable <CharSequence> fromMarkup (final CharSequence aMarkup)
  {
    return fromMarkup (Collections.singleton (aMarkup));
  }

  /**
   * Creates a char sequence where new-line, asterisk and underscore are replaced by their
   * corresponding {@link ControlCharacter}.
   *
   * @param aMarkup
   *        the markup.
   * @return the create char sequence.
   */
  public static Iterable <CharSequence> fromMarkup (final Iterable <CharSequence> aMarkup)
  {
    Iterable <CharSequence> aText = aMarkup;
    aText = splitByControlCharacter (ControlCharacters.NEWLINE_FACTORY, aText);
    aText = splitByControlCharacter (ControlCharacters.METRICS_FACTORY, aText);
    aText = splitByControlCharacter (ControlCharacters.BOLD_FACTORY, aText);
    aText = splitByControlCharacter (ControlCharacters.ITALIC_FACTORY, aText);
    aText = splitByControlCharacter (ControlCharacters.COLOR_FACTORY, aText);

    for (final IAnnotationControlCharacterFactory <?> aAnnotationControlCharacterFactory : AnnotationCharacters.getFactories ())
    {
      aText = splitByControlCharacter (aAnnotationControlCharacterFactory, aText);
    }

    aText = splitByControlCharacter (IndentCharacters.INDENT_FACTORY, aText);

    aText = _unescapeBackslash (aText);

    return aText;
  }

  /**
   * Splits the sequence by the given control character and replaces its markup representation by
   * the {@link ControlCharacter}.
   *
   * @param aControlCharacterFactory
   *        the control character to split by.
   * @param aMarkup
   *        the markup to split.
   * @return the splitted and replaced sequence.
   */
  protected static Iterable <CharSequence> splitByControlCharacter (final IControlCharacterFactory aControlCharacterFactory,
                                                                    final Iterable <CharSequence> aMarkup)
  {
    final List <CharSequence> aResult = new ArrayList <> ();
    boolean bBeginOfLine = true;
    for (final CharSequence aCurrent : aMarkup)
    {
      if (aCurrent instanceof final String sString)
      {
        int nBegin = 0;

        if (!aControlCharacterFactory.patternMatchesBeginOfLine () || bBeginOfLine)
        {
          final Matcher aMatcher = aControlCharacterFactory.getPattern ().matcher (sString);
          while (aMatcher.find ())
          {
            final String sPart = sString.substring (nBegin, aMatcher.start ());
            nBegin = aMatcher.end ();

            if (!sPart.isEmpty ())
            {
              final String sUnescaped = aControlCharacterFactory.unescape (sPart);
              aResult.add (sUnescaped);
            }

            aResult.add (aControlCharacterFactory.createControlCharacter (sString, aMatcher, aResult));
          }
        }

        if (nBegin < sString.length ())
        {
          final String sPart = sString.substring (nBegin);
          final String sUnescaped = aControlCharacterFactory.unescape (sPart);
          aResult.add (sUnescaped);
        }

        bBeginOfLine = false;
      }
      else
      {
        if (aCurrent instanceof NewLineControlCharacter)
        {
          bBeginOfLine = true;
        }
        aResult.add (aCurrent);
      }

    }
    return aResult;
  }

  private static Iterable <CharSequence> _unescapeBackslash (final Iterable <CharSequence> aChars)
  {
    final List <CharSequence> aResult = new ArrayList <> ();
    for (final CharSequence aCurrent : aChars)
    {
      if (aCurrent instanceof String)
      {
        aResult.add (ControlCharacters.unescapeBackslash ((String) aCurrent));
      }
      else
      {
        aResult.add (aCurrent);
      }
    }
    return aResult;
  }

}
