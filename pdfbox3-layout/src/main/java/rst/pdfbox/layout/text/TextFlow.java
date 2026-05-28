package rst.pdfbox.layout.text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;

/**
 * A text flow is a text sequence that {@link IWidthRespecting respects a given width} by word
 * wrapping the text. The text may contain line breaks ('\n').<br>
 * In order to ease creation of styled text, this class supports a kind of
 * {@link #addMarkup(String, float, EBaseFont) markup}. The following raw text
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
 */
public class TextFlow implements ITextSequence, IWidthRespecting
{
  public static final float DEFAULT_LINE_SPACING = 1.2f;
  private static final String HEIGHT = "height";
  private static final String WIDTH = "width";

  private final Map <String, Object> m_aCache = new HashMap <> ();

  private final List <ITextFragment> m_aText = new ArrayList <> ();
  private float m_fLineSpacing = DEFAULT_LINE_SPACING;
  private float m_fMaxWidth = -1;
  private boolean m_bApplyLineSpacingToFirstLine = true;

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
   * Adds some text associated with the font to draw. The text may contain line breaks ('\n').
   *
   * @param sText
   *        the text to add.
   * @param fFontSize
   *        the size of the font.
   * @param aFont
   *        the font to use to draw the text.
   * @throws IOException
   *         by PDFBox
   */
  public void addText (final String sText, final float fFontSize, final PDFont aFont) throws IOException
  {
    add (TextFlowUtil.createTextFlow (sText, fFontSize, aFont));
  }

  /**
   * Adds some markup to the text flow.
   *
   * @param sMarkup
   *        the markup to add.
   * @param fFontSize
   *        the font size to use.
   * @param eBaseFont
   *        the base font describing the bundle of plain/blold/italic/bold-italic fonts.
   * @throws IOException
   *         by PDFBox
   */
  public void addMarkup (final String sMarkup, final float fFontSize, final EBaseFont eBaseFont) throws IOException
  {
    add (TextFlowUtil.createTextFlowFromMarkup (sMarkup, fFontSize, eBaseFont));
  }

  /**
   * Adds some markup to the text flow.
   *
   * @param sMarkup
   *        the markup to add.
   * @param fFontSize
   *        the font size to use.
   * @param aPlainFont
   *        the plain font to use.
   * @param aBoldFont
   *        the bold font to use.
   * @param aItalicFont
   *        the italic font to use.
   * @param aBoldItalicFont
   *        the bold-italic font to use.
   * @throws IOException
   *         by PDFBox
   */
  public void addMarkup (final String sMarkup,
                         final float fFontSize,
                         final PDFont aPlainFont,
                         final PDFont aBoldFont,
                         final PDFont aItalicFont,
                         final PDFont aBoldItalicFont) throws IOException
  {
    add (TextFlowUtil.createTextFlowFromMarkup (sMarkup,
                                                fFontSize,
                                                aPlainFont,
                                                aBoldFont,
                                                aItalicFont,
                                                aBoldItalicFont));
  }

  /**
   * Adds a text sequence to this flow.
   *
   * @param aSequence
   *        the sequence to add.
   */
  public void add (final ITextSequence aSequence)
  {
    for (final ITextFragment aFragment : aSequence)
    {
      add (aFragment);
    }
  }

  /**
   * Adds a text fragment to this flow.
   *
   * @param aFragment
   *        the fragment to add.
   */
  public void add (final ITextFragment aFragment)
  {
    m_aText.add (aFragment);
    _clearCache ();
  }

  /**
   * Removes the last added fragment.
   *
   * @return the removed fragment (if any).
   */
  public ITextFragment removeLast ()
  {
    if (m_aText.size () > 0)
    {
      _clearCache ();
      return m_aText.remove (m_aText.size () - 1);
    }
    return null;
  }

  /**
   * @return the last added fragment (if any).
   */
  public ITextFragment getLast ()
  {
    if (m_aText.size () > 0)
    {
      _clearCache ();
      return m_aText.get (m_aText.size () - 1);
    }
    return null;
  }

  /**
   * @return <code>true</code> if this flow does not contain any fragments.
   */
  public boolean isEmpty ()
  {
    return m_aText.isEmpty ();
  }

  @Override
  public Iterator <ITextFragment> iterator ()
  {
    return m_aText.iterator ();
  }

  @Override
  public float getMaxWidth ()
  {
    return m_fMaxWidth;
  }

  @Override
  public void setMaxWidth (final float fMaxWidth)
  {
    this.m_fMaxWidth = fMaxWidth;
    _clearCache ();
  }

  /**
   * @return the factor multiplied with the height to calculate the line spacing.
   */
  public float getLineSpacing ()
  {
    return m_fLineSpacing;
  }

  /**
   * Sets the factor multiplied with the height to calculate the line spacing.
   *
   * @param fLineSpacing
   *        the line spacing factor.
   */
  public void setLineSpacing (final float fLineSpacing)
  {
    this.m_fLineSpacing = fLineSpacing;
    _clearCache ();
  }

  /**
   * Indicates if the line spacing should be applied to the first line. Makes sense if there is text
   * above to achieve an equal spacing. In case you want to position the text precisely on top, you
   * may set this value to <code>false</code>. Default is <code>true</code>.
   *
   * @return <code>true</code> if the line spacing should be applied to the first line.
   */
  public boolean isApplyLineSpacingToFirstLine ()
  {
    return m_bApplyLineSpacingToFirstLine;
  }

  /**
   * Sets the indicator whether to apply line spacing to the first line.
   *
   * @param bApplyLineSpacingToFirstLine
   *        <code>true</code> if the line spacing should be applied to the first line.
   * @see TextFlow#isApplyLineSpacingToFirstLine()
   */
  public void setApplyLineSpacingToFirstLine (final boolean bApplyLineSpacingToFirstLine)
  {
    this.m_bApplyLineSpacingToFirstLine = bApplyLineSpacingToFirstLine;
  }

  @Override
  public float getWidth () throws IOException
  {
    Float aWidth = _getCachedValue (WIDTH, Float.class);
    if (aWidth == null)
    {
      aWidth = Float.valueOf (TextSequenceUtil.getWidth (this, getMaxWidth ()));
      _setCachedValue (WIDTH, aWidth);
    }
    return aWidth.floatValue ();
  }

  @Override
  public float getHeight () throws IOException
  {
    Float aHeight = _getCachedValue (HEIGHT, Float.class);
    if (aHeight == null)
    {
      aHeight = Float.valueOf (TextSequenceUtil.getHeight (this,
                                                           getMaxWidth (),
                                                           getLineSpacing (),
                                                           isApplyLineSpacingToFirstLine ()));
      _setCachedValue (HEIGHT, aHeight);
    }
    return aHeight.floatValue ();
  }

  @Override
  public void drawText (final PDPageContentStream aContentStream,
                        final Position aUpperLeft,
                        final EAlignment eAlignment,
                        final IDrawListener aDrawListener) throws IOException
  {
    TextSequenceUtil.drawText (this,
                               aContentStream,
                               aUpperLeft,
                               aDrawListener,
                               eAlignment,
                               getMaxWidth (),
                               getLineSpacing (),
                               isApplyLineSpacingToFirstLine ());
  }

  public void drawTextRightAligned (final PDPageContentStream aContentStream,
                                    final Position aEndOfFirstLine,
                                    final IDrawListener aDrawListener) throws IOException
  {
    drawText (aContentStream, aEndOfFirstLine.add (-getWidth (), 0), EAlignment.Right, aDrawListener);
  }

  /**
   * @return a copy of this text flow where all leading {@link NewLine}s are removed.
   * @throws IOException
   *         by pdfbox.
   */
  public TextFlow removeLeadingEmptyLines () throws IOException
  {
    if (m_aText.size () == 0 || !(m_aText.get (0) instanceof NewLine))
    {
      return this;
    }
    final TextFlow aResult = createInstance ();
    aResult.setApplyLineSpacingToFirstLine (this.isApplyLineSpacingToFirstLine ());
    aResult.setLineSpacing (this.getLineSpacing ());
    aResult.setMaxWidth (this.getMaxWidth ());
    for (final ITextFragment aFragment : this)
    {
      if (!aResult.isEmpty () || !(aFragment instanceof NewLine))
      {
        aResult.add (aFragment);
      }
    }
    return aResult;
  }

  protected TextFlow createInstance ()
  {
    return new TextFlow ();
  }

  @Override
  public String toString ()
  {
    return "TextFlow [text=" + m_aText + "]";
  }

}
