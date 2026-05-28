package rst.pdfbox.layout.elements;

import java.io.IOException;

/**
 * If a drawable is marked as {@link Dividable}, it can be (vertically) divided
 * in case it does not fit on the (remaining) page.
 */
public interface Dividable {

    /**
     * Divides the drawable vetically into pieces where the first part is to
     * respect the given remaining height. The page height allows to make better
     * decisions on how to divide best.
     * 
     * @param remainingHeight
     *            the remaining height on the page dictating the height of the
     *            first part.
     * @param nextPageHeight
     *            the height of the next page allows to make better decisions on
     *            how to divide best, e.g. maybe the element fits completely on
     *            the next page.
     * @return the Divided containing the first part and the tail.
     * @throws IOException by pdfbox.
     */
    Divided divide(final float remainingHeight, final float nextPageHeight)
	    throws IOException;

    /**
     * A container for the result of a {@link Dividable#divide(float, float)}
     * operation.
     */
    public static class Divided {

	private final IDrawable first;
	private final IDrawable tail;

	public Divided(IDrawable first, IDrawable tail) {
	    this.first = first;
	    this.tail = tail;
	}

	public IDrawable getFirst() {
	    return first;
	}

	public IDrawable getTail() {
	    return tail;
	}

    }

}
