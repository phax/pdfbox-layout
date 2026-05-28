package rst.pdfbox.layout.text;

/**
 * Acts as a replacement for whitespace that has been removed by word wrapping.
 */
public class ReplacedWhitespace extends ControlFragment
{

  private final String m_sReplacedSpace;

  public ReplacedWhitespace (final String sReplacedSpace, final FontDescriptor aFontDescriptor)
  {
    super ("", aFontDescriptor);

    this.m_sReplacedSpace = sReplacedSpace;
  }

  /**
   * @return the replaced space.
   */
  public String getReplacedSpace ()
  {
    return m_sReplacedSpace;
  }

  /**
   * @return the replaced fragment.
   */
  public ITextFragment toReplacedFragment ()
  {
    return new StyledText (getReplacedSpace (), getFontDescriptor ());
  }
}
