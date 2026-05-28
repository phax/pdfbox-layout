package rst.pdfbox.layout.elements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import rst.pdfbox.layout.text.IWidthRespecting;
import rst.pdfbox.layout.text.Position;

/**
 * PDFBox-version-independent base for {@code ImageElement}. Holds the image data plus its
 * width/height/maxWidth/absolute-position state — all of which are identical between the pdfbox
 * flavours. The PDFBox-coupled {@code draw(...)} and the {@link IDividable#divide(float, float)}
 * (which builds module-specific {@code VerticalSpacer}/{@code Cutter} instances) stay in the
 * subclass.
 */
public abstract class AbstractImageElement implements IElement, IDrawable, IWidthRespecting
{

  /**
   * Set this to {@link #setWidth(float)} resp. {@link #setHeight(float)} (usually both) in order to
   * respect the {@link IWidthRespecting width}.
   */
  public static final float SCALE_TO_RESPECT_WIDTH = -1f;

  protected final BufferedImage m_aImage;
  private float m_fWidth;
  private float m_fHeight;
  private float m_fMaxWidth = -1;
  private Position m_aAbsolutePosition;

  protected AbstractImageElement (final BufferedImage aImage)
  {
    this.m_aImage = aImage;
    this.m_fWidth = aImage.getWidth ();
    this.m_fHeight = aImage.getHeight ();
  }

  protected AbstractImageElement (final InputStream aInputStream) throws IOException
  {
    this (ImageIO.read (aInputStream));
  }

  protected AbstractImageElement (final String sFilePath) throws IOException
  {
    this (ImageIO.read (new File (sFilePath)));
  }

  @Override
  public float getWidth () throws IOException
  {
    if (m_fWidth == SCALE_TO_RESPECT_WIDTH)
    {
      if (getMaxWidth () > 0 && m_aImage.getWidth () > getMaxWidth ())
      {
        return getMaxWidth ();
      }
      return m_aImage.getWidth ();
    }
    return m_fWidth;
  }

  /**
   * Sets the width. Default is the image width. Set to {@link #SCALE_TO_RESPECT_WIDTH} in order to
   * let the image {@link IWidthRespecting respect any given width}.
   *
   * @param fWidth
   *        the width to use.
   */
  public void setWidth (final float fWidth)
  {
    this.m_fWidth = fWidth;
  }

  @Override
  public float getHeight () throws IOException
  {
    if (m_fHeight == SCALE_TO_RESPECT_WIDTH)
    {
      if (getMaxWidth () > 0 && m_aImage.getWidth () > getMaxWidth ())
      {
        return getMaxWidth () / m_aImage.getWidth () * m_aImage.getHeight ();
      }
      return m_aImage.getHeight ();
    }
    return m_fHeight;
  }

  /**
   * Sets the height. Default is the image height. Set to {@link #SCALE_TO_RESPECT_WIDTH} in order
   * to let the image {@link IWidthRespecting respect any given width}. Usually this makes only
   * sense if you also set the width to {@link #SCALE_TO_RESPECT_WIDTH}.
   *
   * @param fHeight
   *        the height to use.
   */
  public void setHeight (final float fHeight)
  {
    this.m_fHeight = fHeight;
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
  }

  @Override
  public Position getAbsolutePosition ()
  {
    return m_aAbsolutePosition;
  }

  /**
   * Sets the absolute position to render at.
   *
   * @param aAbsolutePosition
   *        the absolute position.
   */
  public void setAbsolutePosition (final Position aAbsolutePosition)
  {
    this.m_aAbsolutePosition = aAbsolutePosition;
  }
}
