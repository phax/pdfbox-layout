import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.IElement;
import rst.pdfbox.layout.elements.HorizontalRuler;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.render.ILayoutHint;
import rst.pdfbox.layout.elements.render.RenderContext;
import rst.pdfbox.layout.elements.render.RenderListener;
import rst.pdfbox.layout.elements.render.Renderer;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.shape.Stroke;
import rst.pdfbox.layout.shape.Stroke.CapStyle;
import rst.pdfbox.layout.text.EAlignment;
import rst.pdfbox.layout.text.EBaseFont;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.TextFlow;
import rst.pdfbox.layout.text.TextFlowUtil;
import rst.pdfbox.layout.text.TextSequenceUtil;
import examples.AbstractExampleTest;
import org.junit.Test;

public class CustomRendererTest extends AbstractExampleTest {

    @Test
    public void test() throws Exception {
	String text1 = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, "
		+ "sed diam nonumy eirmod tempor invidunt ut labore et dolore magna "
		+ "aliquyam erat, _sed diam_ voluptua. At vero eos et *accusam et justo* "
		+ "duo dolores et ea rebum.\n\nStet clita kasd gubergren, no sea takimata "
		+ "sanctus est *Lorem ipsum _dolor* sit_ amet. Lorem ipsum dolor sit amet, "
		+ "consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, *sed diam voluptua.\n\n"
		+ "At vero eos et accusam* et justo duo dolores et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n\n";

	String text2 = "At *vero eos et accusam* et justo duo dolores et ea rebum."
		+ "Stet clita kasd gubergren, no sea takimata\n\n"
		+ "sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, "
		+ "_consetetur sadipscing elitr_, sed diam nonumy eirmod tempor invidunt "
		+ "ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero "
		+ "eos et _accusam et *justo* duo dolores_ et ea rebum. Stet clita kasd "
		+ "gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet.\n";

	Document document = new Document(40, 60, 40, 60);
	SectionRenderer sectionRenderer = new SectionRenderer();
	document.addRenderer(sectionRenderer);
	document.addRenderListener(sectionRenderer);

	Paragraph paragraph = new Paragraph();
	paragraph.addMarkup(text1, 11, EBaseFont.Times);
	paragraph.addMarkup(text2, 12, EBaseFont.Helvetica);
	paragraph.addMarkup(text1, 8, EBaseFont.Courier);

	document.add(new Section(1));
	document.add(paragraph);
	document.add(paragraph);
	document.add(paragraph);
	document.add(new Section(2));
	document.add(paragraph);
	document.add(paragraph);
	document.add(paragraph);
	document.add(new Section(3));
	document.add(paragraph);
	document.add(paragraph);

	final OutputStream outputStream = new FileOutputStream("customrenderer.pdf");
	document.save(outputStream);

        verifyPdf();

    }

    public static class SectionRenderer implements Renderer, RenderListener {

	private int sectionNumber;
	
	@Override
	public boolean render(RenderContext renderContext, IElement element,
		ILayoutHint layoutHint) throws IOException {
	    if (element instanceof Section) {

		if (renderContext.getPageIndex() > 0) {
		    // no new page on first page ;-)
		    renderContext.newPage();
		}
		sectionNumber = ((Section)element).getNumber();
		
		renderContext.render(renderContext, element, layoutHint);

		IElement ruler = new HorizontalRuler(Stroke.builder().lineWidth(2)
			.capStyle(CapStyle.RoundCap).build(), Color.black);
		renderContext.render(renderContext, ruler, VerticalLayoutHint.builder().marginBottom(10).build());
		
		return true;
	    }
	    return false;
	}

	@Override
	public void beforePage(RenderContext renderContext) {

	}

	@Override
	public void afterPage(RenderContext renderContext) throws IOException {
	    String content = "Section " + sectionNumber + ", Page " + (renderContext.getPageIndex() + 1);
	    TextFlow text = TextFlowUtil.createTextFlow(content, 11,
		    new PDType1Font(Standard14Fonts.FontName.TIMES_ROMAN));
	    float offset = renderContext.getPageFormat().getMarginLeft()
		    + TextSequenceUtil.getOffset(text,
			    renderContext.getWidth(), EAlignment.Right);
	    text.drawText(renderContext.getContentStream(), new Position(
		    offset, 30), EAlignment.Right, null);
	}

    }

    public static class Section extends Paragraph {
	private int number;

	public Section(int number) throws IOException {
	    super();
	    this.number = number;
	    addMarkup("*Section " + number, 16, EBaseFont.Times);
	}

	public int getNumber() {
	    return number;
	}

    }
}
