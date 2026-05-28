package rst.pdfbox.layout.text;

/**
 * A NewLine introduced by wrapping. This interface is useful for detecting new-lines not contained
 * in the original text.
 */
public class WrappingNewLine extends NewLine
{

  /**
   * See {@link NewLine#NewLine()}.
   */
  public WrappingNewLine ()
  {
    super ();
  }

  /**
   * See {@link NewLine#NewLine(FontDescriptor)}.
   *
   * @param aFontDescriptor
   *        the font and size associated with this new line.
   */
  public WrappingNewLine (final FontDescriptor aFontDescriptor)
  {
    super (aFontDescriptor);
  }

  /**
   * See {@link NewLine#NewLine(float)}.
   *
   * @param fFontSize
   *        the font size, resp. the height of the new line.
   */
  public WrappingNewLine (final float fFontSize)
  {
    super (fFontSize);
  }

}
