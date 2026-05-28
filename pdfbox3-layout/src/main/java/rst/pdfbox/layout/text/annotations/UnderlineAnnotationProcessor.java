package rst.pdfbox.layout.text.annotations;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.shape.Stroke;
import rst.pdfbox.layout.text.IDrawContext;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.StyledText;
import rst.pdfbox.layout.text.annotations.Annotations.UnderlineAnnotation;

/**
 * This annotation processor handles the {@link UnderlineAnnotation}s, and adds the needed hyperlink
 * metadata to the PDF document.
 */
public class UnderlineAnnotationProcessor implements AnnotationProcessor
{

  private final List <Line> m_aLinesOnPage = new ArrayList <> ();

  @Override
  public void annotatedObjectDrawn (final IAnnotated aDrawnObject,
                                    final IDrawContext aDrawContext,
                                    final Position aUpperLeft,
                                    final float fWidth,
                                    final float fHeight) throws IOException
  {
    if (!(aDrawnObject instanceof final StyledText aDrawnText))
    {
      return;
    }

    for (final UnderlineAnnotation aUnderlineAnnotation : aDrawnObject.getAnnotationsOfType (UnderlineAnnotation.class))
    {
      final float fFontSize = aDrawnText.getFontDescriptor ().getSize ();
      final float fAscent = fFontSize *
                            aDrawnText.getFontDescriptor ().getFont ().getFontDescriptor ().getAscent () /
                            1000;

      final float fBaselineOffset = fFontSize * aUnderlineAnnotation.getBaselineOffsetScale ();
      final float fThickness = (0.01f + fFontSize * 0.05f) * aUnderlineAnnotation.getLineWeight ();

      final Position aStart = new Position (aUpperLeft.getX (), aUpperLeft.getY () - fAscent + fBaselineOffset);
      final Position aEnd = new Position (aStart.getX () + fWidth, aStart.getY ());
      final Stroke aStroke = Stroke.builder ().lineWidth (fThickness).build ();
      final Line aLine = new Line (aStart, aEnd, aStroke, aDrawnText.getColor ());
      m_aLinesOnPage.add (aLine);
    }
  }

  @Override
  public void beforePage (final IDrawContext aDrawContext) throws IOException
  {
    m_aLinesOnPage.clear ();
  }

  @Override
  public void afterPage (final IDrawContext aDrawContext) throws IOException
  {
    for (final Line aLine : m_aLinesOnPage)
    {
      aLine.draw (aDrawContext.getCurrentPageContentStream ());
    }
    m_aLinesOnPage.clear ();
  }

  @Override
  public void afterRender (final PDDocument aDocument) throws IOException
  {
    m_aLinesOnPage.clear ();
  }

  private static class Line
  {

    private final Position m_aStart;
    private final Position m_aEnd;
    private final Stroke m_aStroke;
    private final Color m_aColor;

    public Line (final Position aStart, final Position aEnd, final Stroke aStroke, final Color aColor)
    {
      this.m_aStart = aStart;
      this.m_aEnd = aEnd;
      this.m_aStroke = aStroke;
      this.m_aColor = aColor;
    }

    public void draw (final PDPageContentStream aContentStream) throws IOException
    {
      if (m_aColor != null)
      {
        aContentStream.setStrokingColor (m_aColor);
      }
      if (m_aStroke != null)
      {
        m_aStroke.applyTo (aContentStream);
      }
      aContentStream.moveTo (m_aStart.getX (), m_aStart.getY ());
      aContentStream.lineTo (m_aEnd.getX (), m_aEnd.getY ());
      aContentStream.stroke ();
    }

  }
}
