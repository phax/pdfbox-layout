package rst.pdfbox.layout.text;

import java.io.IOException;

/**
 * Defines an area with a width and height.
 */
public interface IArea
{

  /**
   * @return the width of the area.
   * @throws IOException
   *         by pdfbox
   */
  float getWidth () throws IOException;

  /**
   * @return the height of the area.
   * @throws IOException
   *         by pdfbox
   */
  float getHeight () throws IOException;
}
