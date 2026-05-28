package rst.pdfbox.layout.elements;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.text.EAlignment;
import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.IWidthRespecting;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.TextFlow;
import rst.pdfbox.layout.text.TextSequenceUtil;

/**
 * A paragraph is used as a container for {@link TextFlow text} that is drawn as one element. A
 * paragraph has a {@link #setAlignment(EAlignment) (text-) alignment}, and {@link IWidthRespecting
 * respects a given width} by applying word-wrap.
 */
public class Paragraph extends TextFlow implements IDrawable3, IElement, IDividable
{

  private Position m_aAbsolutePosition;
  private EAlignment m_eAlignment = EAlignment.Left;

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

  /**
   * @return the text alignment to apply. Default is left.
   */
  public EAlignment getAlignment ()
  {
    return m_eAlignment;
  }

  /**
   * Sets the alignment to apply.
   *
   * @param eAlignment
   *        the text alignment.
   */
  public void setAlignment (final EAlignment eAlignment)
  {
    this.m_eAlignment = eAlignment;
  }

  @Override
  public void draw (final PDDocument aPdDocument,
                    final PDPageContentStream aContentStream,
                    final Position aUpperLeft,
                    final IDrawListener aDrawListener) throws IOException
  {
    drawText (aContentStream, aUpperLeft, getAlignment (), aDrawListener);
  }

  @Override
  public Divided divide (final float fRemainingHeight, final float fPageHeight) throws IOException
  {
    return TextSequenceUtil.divide (this, getMaxWidth (), fRemainingHeight);
  }

  @Override
  public Paragraph removeLeadingEmptyVerticalSpace () throws IOException
  {
    return removeLeadingEmptyLines ();
  }

  @Override
  public Paragraph removeLeadingEmptyLines () throws IOException
  {
    final Paragraph aResult = (Paragraph) super.removeLeadingEmptyLines ();
    aResult.setAbsolutePosition (this.getAbsolutePosition ());
    aResult.setAlignment (this.getAlignment ());
    return aResult;
  }

  @Override
  protected Paragraph createInstance ()
  {
    return new Paragraph ();
  }

}
