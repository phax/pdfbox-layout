package rst.pdfbox.layout.text;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * A text of line containing only {@link StyledText}s. It may be terminated by a
 * {@link #getNewLine() new line}.
 */
public class TextLine implements ITextSequence
{

  /**
   * The font ascent.
   */
  private static final String ASCENT = "ascent";
  /**
   * The font height.
   */
  private static final String HEIGHT = "height";
  /**
   * The text width.
   */
  private static final String WIDTH = "width";

  private final List <StyledText> m_aStyledTextList = new ArrayList <> ();
  private NewLine m_aNewLine;
  private final Map <String, Object> m_aCache = new HashMap <> ();

  private void _clearCache ()
  {
    m_aCache.clear ();
  }

  private void _setCachedValue (final String sKey, final Object aValue)
  {
    m_aCache.put (sKey, aValue);
  }

  private <T> T _getCachedValue (final String sKey, final Class <T> aType)
  {
    return aType.cast (m_aCache.get (sKey));
  }

  /**
   * Adds a styled text.
   *
   * @param aFragment
   *        the fagment to add.
   */
  public void add (final StyledText aFragment)
  {
    m_aStyledTextList.add (aFragment);
    _clearCache ();
  }

  /**
   * Adds all styled texts of the given text line.
   *
   * @param aTextLine
   *        the text line to add.
   */
  public void add (final TextLine aTextLine)
  {
    for (final StyledText aFragment : aTextLine.getStyledTexts ())
    {
      add (aFragment);
    }
  }

  /**
   * @return the terminating new line, may be <code>null</code>.
   */
  public NewLine getNewLine ()
  {
    return m_aNewLine;
  }

  /**
   * Sets the new line.
   *
   * @param aNewLine
   *        the new line.
   */
  public void setNewLine (final NewLine aNewLine)
  {
    this.m_aNewLine = aNewLine;
    _clearCache ();
  }

  /**
   * @return the styled texts building up this line.
   */
  public List <StyledText> getStyledTexts ()
  {
    return Collections.unmodifiableList (m_aStyledTextList);
  }

  @Override
  public Iterator <ITextFragment> iterator ()
  {
    return new TextLineIterator (m_aStyledTextList.iterator (), m_aNewLine);
  }

  /**
   * @return <code>true</code> if the line contains neither styled text nor a new line.
   */
  public boolean isEmpty ()
  {
    return m_aStyledTextList.isEmpty () && m_aNewLine == null;
  }

  @Override
  public float getWidth () throws IOException
  {
    Float aWidth = _getCachedValue (WIDTH, Float.class);
    if (aWidth == null)
    {
      aWidth = 0f;
      for (final ITextFragment aFragment : this)
      {
        aWidth += aFragment.getWidth ();
      }
      _setCachedValue (WIDTH, aWidth);
    }
    return aWidth;
  }

  @Override
  public float getHeight () throws IOException
  {
    Float aHeight = _getCachedValue (HEIGHT, Float.class);
    if (aHeight == null)
    {
      aHeight = 0f;
      for (final ITextFragment aFragment : this)
      {
        aHeight = Math.max (aHeight, aFragment.getHeight ());
      }
      _setCachedValue (HEIGHT, aHeight);
    }
    return aHeight;
  }

  /**
   * @return the (max) ascent of this line.
   * @throws IOException
   *         by pdfbox.
   */
  protected float getAscent () throws IOException
  {
    Float aAscent = _getCachedValue (ASCENT, Float.class);
    if (aAscent == null)
    {
      aAscent = 0f;
      for (final ITextFragment aFragment : this)
      {
        final float fCurrentAscent = aFragment.getFontDescriptor ().getSize () *
                                     aFragment.getFontDescriptor ().getFont ().getFontDescriptor ().getAscent () /
                                     1000;
        aAscent = Math.max (aAscent, fCurrentAscent);
      }
      _setCachedValue (ASCENT, aAscent);
    }
    return aAscent;
  }

  @Override
  public void drawText (final PDPageContentStream aContentStream,
                        final Position aUpperLeft,
                        final EAlignment eAlignment,
                        final IDrawListener aDrawListener) throws IOException
  {
    drawAligned (aContentStream, aUpperLeft, eAlignment, getWidth (), aDrawListener);
  }

  public void drawAligned (final PDPageContentStream aContentStream,
                           final Position aUpperLeft,
                           final EAlignment eAlignment,
                           final float fAvailableLineWidth,
                           final IDrawListener aDrawListener) throws IOException
  {
    aContentStream.saveGraphicsState ();
    aContentStream.beginText ();

    float x = aUpperLeft.getX ();
    final float y = aUpperLeft.getY () - getAscent (); // the baseline
    final float fOffset = TextSequenceUtil.getOffset (this, fAvailableLineWidth, eAlignment);
    x += fOffset;
    CompatibilityHelper.setTextTranslation (aContentStream, x, y);
    float fExtraWordSpacing = 0;
    if (eAlignment == EAlignment.Justify && (getNewLine () instanceof WrappingNewLine))
    {
      fExtraWordSpacing = (fAvailableLineWidth - getWidth ()) / (m_aStyledTextList.size () - 1);
    }

    FontDescriptor aLastFontDesc = null;
    float fLastBaselineOffset = 0;
    Color aLastColor = null;
    float fGap = 0;
    for (final StyledText aStyledText : m_aStyledTextList)
    {
      if (!aStyledText.getFontDescriptor ().equals (aLastFontDesc))
      {
        aLastFontDesc = aStyledText.getFontDescriptor ();
        aContentStream.setFont (aLastFontDesc.getFont (), aLastFontDesc.getSize ());
      }
      if (!aStyledText.getColor ().equals (aLastColor))
      {
        aLastColor = aStyledText.getColor ();
        aContentStream.setNonStrokingColor (aLastColor);
      }
      if (aStyledText.getLeftMargin () > 0)
      {
        fGap += aStyledText.getLeftMargin ();
      }

      final boolean bMoveBaseline = aStyledText.getBaselineOffset () != fLastBaselineOffset;
      if (bMoveBaseline || fGap > 0)
      {
        // final float fBaselineDelta = fLastBaselineOffset - aStyledText.getBaselineOffset ();
        fLastBaselineOffset = aStyledText.getBaselineOffset ();
        CompatibilityHelper.moveTextPosition (aContentStream, x + fGap, y - aStyledText.getBaselineOffset ());
        x += fGap;
      }
      if (aStyledText.getText ().length () > 0)
      {
        CompatibilityHelper.showText (aContentStream, aStyledText.getText ());
      }

      if (aDrawListener != null)
      {
        final float fCurrentUpperLeft = y + aStyledText.getAsent ();
        aDrawListener.drawn (aStyledText,
                             new Position (x, fCurrentUpperLeft),
                             aStyledText.getWidthWithoutMargin (),
                             aStyledText.getHeight ());
      }
      x += aStyledText.getWidthWithoutMargin ();

      fGap = fExtraWordSpacing;
      if (aStyledText.getRightMargin () > 0)
      {
        fGap += aStyledText.getRightMargin ();
      }
    }
    aContentStream.endText ();
    aContentStream.restoreGraphicsState ();
  }

  @Override
  public String toString ()
  {
    return "TextLine [styledText=" + m_aStyledTextList + ", newLine=" + m_aNewLine + "]";
  }

  /**
   * An iterator for the text line. See {@link TextLine#iterator()}.
   */
  private static class TextLineIterator implements Iterator <ITextFragment>
  {

    private final Iterator <StyledText> m_aStyledText;
    private NewLine m_aNewLine;

    /**
     * Creates an iterator of the given styled texts with an optional trailing new line.
     *
     * @param aStyledText
     *        the text fragments to iterate.
     * @param aNewLine
     *        the optional trailing new line.
     */
    public TextLineIterator (final Iterator <StyledText> aStyledText, final NewLine aNewLine)
    {
      this.m_aStyledText = aStyledText;
      this.m_aNewLine = aNewLine;
    }

    @Override
    public boolean hasNext ()
    {
      return m_aStyledText.hasNext () || m_aNewLine != null;
    }

    @Override
    public ITextFragment next ()
    {
      ITextFragment aNext = null;
      if (m_aStyledText.hasNext ())
      {
        aNext = m_aStyledText.next ();
      }
      else
        if (m_aNewLine != null)
        {
          aNext = m_aNewLine;
          m_aNewLine = null;
        }
      return aNext;
    }

    @Override
    public void remove ()
    {
      throw new UnsupportedOperationException ();
    }

  }

}
