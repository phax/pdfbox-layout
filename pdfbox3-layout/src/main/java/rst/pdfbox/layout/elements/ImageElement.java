package rst.pdfbox.layout.elements;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.util.CompatibilityHelper;

public class ImageElement extends AbstractImageElement implements IDrawable3, IDividable
{

  public ImageElement (final BufferedImage aImage)
  {
    super (aImage);
  }

  public ImageElement (final InputStream aInputStream) throws IOException
  {
    super (aInputStream);
  }

  public ImageElement (final String sFilePath) throws IOException
  {
    super (sFilePath);
  }

  @Override
  public Divided divide (final float fRemainingHeight, final float fNextPageHeight) throws IOException
  {
    if (getHeight () <= fNextPageHeight)
    {
      return new Divided (new VerticalSpacer (fRemainingHeight), this);
    }
    return new Cutter (this).divide (fRemainingHeight, fNextPageHeight);
  }

  @Override
  public void draw (final PDDocument aPdDocument,
                    final PDPageContentStream aContentStream,
                    final Position aUpperLeft,
                    final IDrawListener aDrawListener) throws IOException
  {
    CompatibilityHelper.drawImage (m_aImage, aPdDocument, aContentStream, aUpperLeft, getWidth (), getHeight ());
    if (aDrawListener != null)
    {
      aDrawListener.drawn (this, aUpperLeft, getWidth (), getHeight ());
    }
  }

  @Override
  public ImageElement removeLeadingEmptyVerticalSpace ()
  {
    return this;
  }
}
