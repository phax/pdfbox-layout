package rst.pdfbox.layout.elements;

import java.awt.Color;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.shape.Rect;
import rst.pdfbox.layout.shape.Shape;
import rst.pdfbox.layout.shape.Stroke;
import rst.pdfbox.layout.text.IDrawListener;
import rst.pdfbox.layout.text.IWidthRespecting;
import rst.pdfbox.layout.text.Position;

/**
 * The frame is a container for a {@link Drawable}, that allows to add margin,
 * padding, border and background to the contained drawable. The size (width and
 * height) is either given, or calculated based on the dimensions of the
 * contained item. The size available for the inner element is reduced by the
 * margin, padding and border width.
 */
public class Frame extends AbstractFrame implements Drawable {

    private List<Drawable> innerList = new CopyOnWriteArrayList<Drawable>();

    private Shape shape = new Rect();
    private Stroke borderStroke = new Stroke();

    /**
     * Creates an empty frame.
     */
    public Frame() {
	this(null, null);
    }

    /**
     * Creates a frame containing the inner element.
     *
     * @param inner
     *            the item to contain.
     */
    public Frame(final Drawable inner) {
	this(inner, null, null);
    }

    /**
     * Creates a frame containing the inner element, optionally constraint by
     * the given dimensions. These contraints target the border-box of the
     * frame, means: the inner element plus padding plus border width, but not
     * the margin.
     *
     * @param inner
     *            the item to contain.
     * @param width
     *            the width to constrain the border-box of the frame to, or
     *            <code>null</code>.
     * @param height
     *            the height to constrain the border-box of the frame to, or
     *            <code>null</code>.
     */
    public Frame(final Drawable inner, final Float width, final Float height) {
	this(width, height);
	add(inner);
    }

    /**
     * Creates a frame constraint by the given dimensions. These contraints
     * target the border-box of the frame, means: the inner element plus padding
     * plus border width, but not the margin.
     *
     * @param width
     *            the width to constrain the border-box of the frame to, or
     *            <code>null</code>.
     * @param height
     *            the height to constrain the border-box of the frame to, or
     *            <code>null</code>.
     */
    public Frame(final Float width, final Float height) {
	super(width, height);
    }

    /**
     * Adds a drawable to the frame.
     * @param drawable
     */
    public void add(final Drawable drawable) {
	innerList.add(drawable);
    }

    protected void addAll(final Collection<Drawable> drawable) {
	innerList.addAll(drawable);
    }

    /**
     * @return the shape to use as border and/or background.
     */
    public Shape getShape() {
	return shape;
    }

    /**
     * Sets the shape to use as border and/or background.
     *
     * @param shape
     *            the shape to use.
     */
    public void setShape(Shape shape) {
	this.shape = shape;
    }

    /**
     * The stroke to use to draw the border.
     *
     * @return the stroke to use.
     */
    public Stroke getBorderStroke() {
	return borderStroke;
    }

    /**
     * Sets the stroke to use to draw the border.
     *
     * @param borderStroke
     *            the stroke to use.
     */
    public void setBorderStroke(Stroke borderStroke) {
	this.borderStroke = borderStroke;
    }

    /**
     * @return the widht of the {@link #getBorderStroke()} or <code>0</code>.
     */
    @Override
    protected float getBorderWidth() {
	return hasBorder() ? getBorderStroke().getLineWidth() : 0;
    }

    /**
     * @return if a {@link #getShape() shape}, a {@link #getBorderStroke()
     *         stroke} and {@link #getBorderColor() color} is set.
     */
    protected boolean hasBorder() {
	return getShape() != null && getBorderStroke() != null
		&& getBorderColor() != null;
    }

    /**
     * Convenience method for setting both border color and stroke.
     *
     * @param borderColor
     *            the border color.
     * @param borderStroke
     *            the stroke to use.
     */
    public void setBorder(Color borderColor, Stroke borderStroke) {
	setBorderColor(borderColor);
	setBorderStroke(borderStroke);
    }

    /**
     * Copies all attributes but the inner drawable and size to the given frame.
     *
     * @param other
     *            the frame to copy the attributes to.
     */
    protected void copyAllButInnerAndSizeTo(final Frame other) {
	other.setShape(this.getShape());
	other.setBorderStroke(this.getBorderStroke());
	other.setBorderColor(this.getBorderColor());
	other.setBackgroundColor(this.getBackgroundColor());

	other.setPaddingBottom(this.getPaddingBottom());
	other.setPaddingLeft(this.getPaddingLeft());
	other.setPaddingRight(this.getPaddingRight());
	other.setPaddingTop(this.getPaddingTop());

	other.setMarginBottom(this.getMarginBottom());
	other.setMarginLeft(this.getMarginLeft());
	other.setMarginRight(this.getMarginRight());
	other.setMarginTop(this.getMarginTop());
    }

    @Override
    public float getWidth() throws IOException {
	if (getGivenWidth() != null) {
	    return getGivenWidth() + getMarginLeft() + getMarginRight();
	}
	return getMaxWidth(innerList) + getHorizontalSpacing();
    }

    protected float getMaxWidth(List<Drawable> drawableList) throws IOException {
	float max = 0;
	if (drawableList != null) {
	    for (Drawable inner : drawableList) {
		max = Math.max(max, inner.getWidth());
	    }
	}
	return max;
    }

    @Override
    public float getHeight() throws IOException {
	if (getGivenHeight() != null) {
	    return getGivenHeight() + getMarginTop() + getMarginBottom();
	}
	return getHeight(innerList) + getVerticalSpacing();
    }

    protected float getHeight(List<Drawable> drawableList) throws IOException {
	float height = 0;
	if (drawableList != null) {
	    for (Drawable inner : drawableList) {
		height += inner.getHeight();
	    }
	}
	return height;
    }

    @Override
    public void setMaxWidth(float maxWidth) {
	setMaxWidthInternal(maxWidth);

	for (Drawable inner : innerList) {
	    setMaxWidth(inner, maxWidth);
	}
    }

    private void setMaxWidth(final Drawable inner, float maxWidth) {
	if (inner instanceof IWidthRespecting) {
	    if (getGivenWidth() != null) {
		((IWidthRespecting) inner).setMaxWidth(getGivenWidth()
			- getHorizontalShapeSpacing());
	    } else if (maxWidth >= 0) {
		((IWidthRespecting) inner).setMaxWidth(maxWidth
			- getHorizontalSpacing());
	    }
	}
    }

    /**
     * Propagates the max width to the inner item if there is a given size, but
     * no absolute position.
     *
     * @throws IOException
     *             by pdfbox.
     */
    protected void setInnerMaxWidthIfNecessary() throws IOException {
	if (getAbsolutePosition() == null && getGivenWidth() != null) {
	    setMaxWidth(getGivenWidth() - getHorizontalShapeSpacing());
	}
    }

    @Override
    public void draw(PDDocument pdDocument, PDPageContentStream contentStream,
	    Position upperLeft, IDrawListener drawListener) throws IOException {

	setInnerMaxWidthIfNecessary();

	float halfBorderWidth = 0;
	if (getBorderWidth() > 0) {
	    halfBorderWidth = getBorderWidth() / 2f;
	}
	upperLeft = upperLeft.add(getMarginLeft() + halfBorderWidth,
		-getMarginTop() - halfBorderWidth);

	if (getShape() != null) {
	    float shapeWidth = getWidth() - getMarginLeft() - getMarginRight()
		    - getBorderWidth();
	    float shapeHeight = getHeight() - getMarginTop()
		    - getMarginBottom() - getBorderWidth();

	    if (getBackgroundColor() != null) {
		getShape().fill(pdDocument, contentStream, upperLeft,
			shapeWidth, shapeHeight, getBackgroundColor(),
			drawListener);
	    }
	    if (hasBorder()) {
		getShape().draw(pdDocument, contentStream, upperLeft,
			shapeWidth, shapeHeight, getBorderColor(),
			getBorderStroke(), drawListener);
	    }
	}

	Position innerUpperLeft = upperLeft.add(getPaddingLeft()
		+ halfBorderWidth, -getPaddingTop() - halfBorderWidth);

	for (Drawable inner : innerList) {
	    inner.draw(pdDocument, contentStream, innerUpperLeft, drawListener);
	    innerUpperLeft = innerUpperLeft.add(0, -inner.getHeight());
	}
    }

    @Override
    public Drawable removeLeadingEmptyVerticalSpace() throws IOException {
	if (innerList.size() > 0) {
	    Drawable drawableWithoutLeadingVerticalSpace = innerList.get(0)
		    .removeLeadingEmptyVerticalSpace();
	    innerList.set(0, drawableWithoutLeadingVerticalSpace);
	}
	return this;
    }

    @Override
    public Divided divide(float remainingHeight, float nextPageHeight)
	    throws IOException {
	setInnerMaxWidthIfNecessary();

	if (remainingHeight - getVerticalSpacing() <= 0) {
	    return new Divided(new VerticalSpacer(remainingHeight), this);
	}

	// find first inner that does not fit on page
	float spaceLeft = remainingHeight - getVerticalSpacing();

	DividedList dividedList = divideList(innerList, spaceLeft);

	float spaceLeftForDivided = spaceLeft
		- getHeight(dividedList.getHead());
	Divided divided = null;

	if (dividedList.getDrawableToDivide() != null) {
	    IDividable innerDividable = null;
	    if (dividedList.getDrawableToDivide() instanceof IDividable) {
		innerDividable = (IDividable) dividedList.getDrawableToDivide();
	    } else {
		innerDividable = new Cutter(dividedList.getDrawableToDivide());
	    }
	    // some space left on this page for the inner element
	    divided = innerDividable.divide(spaceLeftForDivided, nextPageHeight
		    - getVerticalSpacing());
	}

	Float firstHeight = getGivenHeight() == null ? null : remainingHeight;
	Float tailHeight = getGivenHeight() == null ? null : getGivenHeight()
		- spaceLeft;

	// create head sub frame
	Frame first = new Frame(getGivenWidth(), firstHeight);
	copyAllButInnerAndSizeTo(first);
	if (dividedList.getHead() != null) {
	    first.addAll(dividedList.getHead());
	}
	if (divided != null) {
	    first.add((Drawable) divided.getFirst());
	}

	// create tail sub frame
	Frame tail = new Frame(getGivenWidth(), tailHeight);
	copyAllButInnerAndSizeTo(tail);
	if (divided != null) {
	    tail.add((Drawable) divided.getTail());
	}
	if (dividedList.getTail() != null) {
	    tail.addAll(dividedList.getTail());
	}

	return new Divided(first, tail);
    }

    private DividedList divideList(List<Drawable> items, float spaceLeft)
	    throws IOException {
	List<Drawable> head = null;
	List<Drawable> tail = null;
	Drawable toDivide = null;

	float tmpHeight = 0;
	int index = 0;
	while (tmpHeight < spaceLeft) {
	    tmpHeight += items.get(index).getHeight();

	    if (tmpHeight == spaceLeft) {
		// we can split between two drawables
		head = items.subList(0, index + 1);
		if (index + 1 < items.size()) {
		    tail = items.subList(index + 1, items.size());
		}
	    }

	    if (tmpHeight > spaceLeft) {
		head = items.subList(0, index);
		toDivide = items.get(index);
		if (index + 1 < items.size()) {
		    tail = items.subList(index + 1, items.size());
		}
	    }

	    ++index;
	}

	return new DividedList(head, toDivide, tail);
    }

    public static class DividedList {
	private List<Drawable> head;
	private Drawable drawableToDivide;
	private List<Drawable> tail;

	public DividedList(List<Drawable> head, Drawable drawableToDivide,
		List<Drawable> tail) {
	    this.head = head;
	    this.drawableToDivide = drawableToDivide;
	    this.tail = tail;
	}

	public List<Drawable> getHead() {
	    return head;
	}

	public Drawable getDrawableToDivide() {
	    return drawableToDivide;
	}

	public List<Drawable> getTail() {
	    return tail;
	}

    }

}
