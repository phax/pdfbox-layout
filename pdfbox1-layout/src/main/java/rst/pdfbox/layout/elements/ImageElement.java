package rst.pdfbox.layout.elements;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;

import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.util.CompatibilityHelper;

public class ImageElement extends AbstractImageElement implements Drawable, IDividable {

    public ImageElement(final BufferedImage image) {
	super(image);
    }

    public ImageElement(final InputStream inputStream) throws IOException {
	super(inputStream);
    }

    public ImageElement(final String filePath) throws IOException {
	super(filePath);
    }

    @Override
    public Divided divide(float remainingHeight, float nextPageHeight)
	    throws IOException {
	if (getHeight() <= nextPageHeight) {
	    return new Divided(new VerticalSpacer(remainingHeight), this);
	}
	return new Cutter(this).divide(remainingHeight, nextPageHeight);
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, IDrawListener drawListener) throws IOException {
	CompatibilityHelper.drawImage(image, pdDocument, contentStream,
		upperLeft, getWidth(), getHeight());
	if (drawListener != null) {
	    drawListener.drawn(this, upperLeft, getWidth(), getHeight());
	}
    }

    @Override
    public ImageElement removeLeadingEmptyVerticalSpace() {
	return this;
    }
}
