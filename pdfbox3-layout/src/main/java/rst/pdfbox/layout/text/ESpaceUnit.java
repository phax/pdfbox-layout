package rst.pdfbox.layout.text;

import java.io.IOException;

/**
 * Unit to specify space, currently only em and pt.
 */
public enum ESpaceUnit
{
  /**
   * The average character width of the associated font.
   */
  em,
  /**
   * Measuring in points.
   */
  pt;

  /**
   * Converts the given unit to pt.
   *
   * @param fSize
   *        the size with respect to the unit.
   * @param aFontDescriptor
   *        the font/size to use.
   * @return the size in pt.
   * @throws IOException
   *         by pdfbox
   */
  public float toPt (final float fSize, final FontDescriptor aFontDescriptor) throws IOException
  {
    if (this == em)
      return aFontDescriptor.getSize () * aFontDescriptor.getFont ().getAverageFontWidth () / 1000 * fSize;

    return fSize;
  }
}
