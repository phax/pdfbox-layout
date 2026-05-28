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
 * The frame is a container for a {@link IDrawable3}, that allows to add margin, padding, border and
 * background to the contained drawable. The size (width and height) is either given, or calculated
 * based on the dimensions of the contained item. The size available for the inner element is
 * reduced by the margin, padding and border width.
 */
public class Frame extends AbstractFrame implements IDrawable3
{

  private final List <IDrawable3> m_aInnerList = new CopyOnWriteArrayList <> ();

  private Shape m_aShape = new Rect ();
  private Stroke m_aBorderStroke = new Stroke ();

  /**
   * Creates an empty frame.
   */
  public Frame ()
  {
    this (null, null);
  }

  /**
   * Creates a frame containing the inner element.
   *
   * @param aInner
   *        the item to contain.
   */
  public Frame (final IDrawable3 aInner)
  {
    this (aInner, null, null);
  }

  /**
   * Creates a frame containing the inner element, optionally constraint by the given dimensions.
   * These contraints target the border-box of the frame, means: the inner element plus padding plus
   * border width, but not the margin.
   *
   * @param aInner
   *        the item to contain.
   * @param aWidth
   *        the width to constrain the border-box of the frame to, or <code>null</code>.
   * @param aHeight
   *        the height to constrain the border-box of the frame to, or <code>null</code>.
   */
  public Frame (final IDrawable3 aInner, final Float aWidth, final Float aHeight)
  {
    this (aWidth, aHeight);
    add (aInner);
  }

  /**
   * Creates a frame constraint by the given dimensions. These contraints target the border-box of
   * the frame, means: the inner element plus padding plus border width, but not the margin.
   *
   * @param aWidth
   *        the width to constrain the border-box of the frame to, or <code>null</code>.
   * @param aHeight
   *        the height to constrain the border-box of the frame to, or <code>null</code>.
   */
  public Frame (final Float aWidth, final Float aHeight)
  {
    super (aWidth, aHeight);
  }

  /**
   * Adds a drawable to the frame.
   *
   * @param aDrawable
   *        drawable to add
   */
  public void add (final IDrawable3 aDrawable)
  {
    m_aInnerList.add (aDrawable);
  }

  protected void addAll (final Collection <IDrawable3> aDrawable)
  {
    m_aInnerList.addAll (aDrawable);
  }

  /**
   * @return the shape to use as border and/or background.
   */
  public Shape getShape ()
  {
    return m_aShape;
  }

  /**
   * Sets the shape to use as border and/or background.
   *
   * @param aShape
   *        the shape to use.
   */
  public void setShape (final Shape aShape)
  {
    this.m_aShape = aShape;
  }

  /**
   * The stroke to use to draw the border.
   *
   * @return the stroke to use.
   */
  public Stroke getBorderStroke ()
  {
    return m_aBorderStroke;
  }

  /**
   * Sets the stroke to use to draw the border.
   *
   * @param aBorderStroke
   *        the stroke to use.
   */
  public void setBorderStroke (final Stroke aBorderStroke)
  {
    this.m_aBorderStroke = aBorderStroke;
  }

  /**
   * @return the widht of the {@link #getBorderStroke()} or <code>0</code>.
   */
  @Override
  protected float getBorderWidth ()
  {
    return hasBorder () ? getBorderStroke ().getLineWidth () : 0;
  }

  /**
   * @return if a {@link #getShape() shape}, a {@link #getBorderStroke() stroke} and
   *         {@link #getBorderColor() color} is set.
   */
  protected boolean hasBorder ()
  {
    return getShape () != null && getBorderStroke () != null && getBorderColor () != null;
  }

  /**
   * Convenience method for setting both border color and stroke.
   *
   * @param aBorderColor
   *        the border color.
   * @param aBorderStroke
   *        the stroke to use.
   */
  public void setBorder (final Color aBorderColor, final Stroke aBorderStroke)
  {
    setBorderColor (aBorderColor);
    setBorderStroke (aBorderStroke);
  }

  /**
   * Copies all attributes but the inner drawable and size to the given frame.
   *
   * @param aOther
   *        the frame to copy the attributes to.
   */
  protected void copyAllButInnerAndSizeTo (final Frame aOther)
  {
    aOther.setShape (this.getShape ());
    aOther.setBorderStroke (this.getBorderStroke ());
    aOther.setBorderColor (this.getBorderColor ());
    aOther.setBackgroundColor (this.getBackgroundColor ());

    aOther.setPaddingBottom (this.getPaddingBottom ());
    aOther.setPaddingLeft (this.getPaddingLeft ());
    aOther.setPaddingRight (this.getPaddingRight ());
    aOther.setPaddingTop (this.getPaddingTop ());

    aOther.setMarginBottom (this.getMarginBottom ());
    aOther.setMarginLeft (this.getMarginLeft ());
    aOther.setMarginRight (this.getMarginRight ());
    aOther.setMarginTop (this.getMarginTop ());
  }

  @Override
  public float getWidth () throws IOException
  {
    if (getGivenWidth () != null)
    {
      return getGivenWidth ().floatValue () + getMarginLeft () + getMarginRight ();
    }
    return getMaxWidth (m_aInnerList) + getHorizontalSpacing ();
  }

  protected float getMaxWidth (final List <IDrawable3> aDrawableList) throws IOException
  {
    float fMax = 0;
    if (aDrawableList != null)
    {
      for (final IDrawable3 aInner : aDrawableList)
      {
        fMax = Math.max (fMax, aInner.getWidth ());
      }
    }
    return fMax;
  }

  @Override
  public float getHeight () throws IOException
  {
    if (getGivenHeight () != null)
    {
      return getGivenHeight ().floatValue () + getMarginTop () + getMarginBottom ();
    }
    return getHeight (m_aInnerList) + getVerticalSpacing ();
  }

  protected float getHeight (final List <IDrawable3> aDrawableList) throws IOException
  {
    float fHeight = 0;
    if (aDrawableList != null)
    {
      for (final IDrawable3 aInner : aDrawableList)
      {
        fHeight += aInner.getHeight ();
      }
    }
    return fHeight;
  }

  @Override
  public void setMaxWidth (final float fMaxWidth)
  {
    setMaxWidthInternal (fMaxWidth);

    for (final IDrawable3 aInner : m_aInnerList)
    {
      _setMaxWidth (aInner, fMaxWidth);
    }
  }

  private void _setMaxWidth (final IDrawable3 aInner, final float fMaxWidth)
  {
    if (aInner instanceof final IWidthRespecting aInnerWR)
    {
      if (getGivenWidth () != null)
      {
        aInnerWR.setMaxWidth (getGivenWidth ().floatValue () - getHorizontalShapeSpacing ());
      }
      else
        if (fMaxWidth >= 0)
        {
          aInnerWR.setMaxWidth (fMaxWidth - getHorizontalSpacing ());
        }
    }
  }

  /**
   * Propagates the max width to the inner item if there is a given size, but no absolute position.
   *
   * @throws IOException
   *         by pdfbox.
   */
  protected void setInnerMaxWidthIfNecessary () throws IOException
  {
    if (getAbsolutePosition () == null && getGivenWidth () != null)
    {
      setMaxWidth (getGivenWidth ().floatValue () - getHorizontalShapeSpacing ());
    }
  }

  @Override
  public void draw (final PDDocument aPdDocument,
                    final PDPageContentStream aContentStream,
                    final Position aUpperLeft,
                    final IDrawListener aDrawListener) throws IOException
  {
    setInnerMaxWidthIfNecessary ();

    float fHalfBorderWidth = 0;
    if (getBorderWidth () > 0)
    {
      fHalfBorderWidth = getBorderWidth () / 2f;
    }
    final Position aActualUpperLeft = aUpperLeft.add (getMarginLeft () + fHalfBorderWidth,
                                                      -getMarginTop () - fHalfBorderWidth);

    if (getShape () != null)
    {
      final float fShapeWidth = getWidth () - getMarginLeft () - getMarginRight () - getBorderWidth ();
      final float fShapeHeight = getHeight () - getMarginTop () - getMarginBottom () - getBorderWidth ();

      if (getBackgroundColor () != null)
      {
        getShape ().fill (aPdDocument,
                          aContentStream,
                          aActualUpperLeft,
                          fShapeWidth,
                          fShapeHeight,
                          getBackgroundColor (),
                          aDrawListener);
      }
      if (hasBorder ())
      {
        getShape ().draw (aPdDocument,
                          aContentStream,
                          aActualUpperLeft,
                          fShapeWidth,
                          fShapeHeight,
                          getBorderColor (),
                          getBorderStroke (),
                          aDrawListener);
      }
    }

    Position aInnerUpperLeft = aActualUpperLeft.add (getPaddingLeft () + fHalfBorderWidth,
                                                     -getPaddingTop () - fHalfBorderWidth);

    for (final IDrawable3 aInner : m_aInnerList)
    {
      aInner.draw (aPdDocument, aContentStream, aInnerUpperLeft, aDrawListener);
      aInnerUpperLeft = aInnerUpperLeft.add (0, -aInner.getHeight ());
    }
  }

  @Override
  public IDrawable3 removeLeadingEmptyVerticalSpace () throws IOException
  {
    if (m_aInnerList.size () > 0)
    {
      final IDrawable3 aDrawableWithoutLeadingVerticalSpace = m_aInnerList.get (0).removeLeadingEmptyVerticalSpace ();
      m_aInnerList.set (0, aDrawableWithoutLeadingVerticalSpace);
    }
    return this;
  }

  @Override
  public Divided divide (final float fRemainingHeight, final float fNextPageHeight) throws IOException
  {
    setInnerMaxWidthIfNecessary ();

    if (fRemainingHeight - getVerticalSpacing () <= 0)
    {
      return new Divided (new VerticalSpacer (fRemainingHeight), this);
    }

    // find first inner that does not fit on page
    final float fSpaceLeft = fRemainingHeight - getVerticalSpacing ();

    final DividedList aDividedList = _divideList (m_aInnerList, fSpaceLeft);

    final float fSpaceLeftForDivided = fSpaceLeft - getHeight (aDividedList.getHead ());
    Divided aDivided = null;

    if (aDividedList.getDrawableToDivide () != null)
    {
      IDividable aInnerDividable = null;
      if (aDividedList.getDrawableToDivide () instanceof IDividable)
      {
        aInnerDividable = (IDividable) aDividedList.getDrawableToDivide ();
      }
      else
      {
        aInnerDividable = new Cutter (aDividedList.getDrawableToDivide ());
      }
      // some space left on this page for the inner element
      aDivided = aInnerDividable.divide (fSpaceLeftForDivided, fNextPageHeight - getVerticalSpacing ());
    }

    final Float aFirstHeight = getGivenHeight () == null ? null : Float.valueOf (fRemainingHeight);
    final Float aTailHeight = getGivenHeight () == null ? null : Float.valueOf (getGivenHeight ().floatValue () -
                                                                                fSpaceLeft);

    // create head sub frame
    final Frame aFirst = new Frame (getGivenWidth (), aFirstHeight);
    copyAllButInnerAndSizeTo (aFirst);
    if (aDividedList.getHead () != null)
    {
      aFirst.addAll (aDividedList.getHead ());
    }
    if (aDivided != null)
    {
      aFirst.add ((IDrawable3) aDivided.getFirst ());
    }

    // create tail sub frame
    final Frame aTail = new Frame (getGivenWidth (), aTailHeight);
    copyAllButInnerAndSizeTo (aTail);
    if (aDivided != null)
    {
      aTail.add ((IDrawable3) aDivided.getTail ());
    }
    if (aDividedList.getTail () != null)
    {
      aTail.addAll (aDividedList.getTail ());
    }

    return new Divided (aFirst, aTail);
  }

  private DividedList _divideList (final List <IDrawable3> aItems, final float fSpaceLeft) throws IOException
  {
    List <IDrawable3> aHead = null;
    List <IDrawable3> aTail = null;
    IDrawable3 aToDivide = null;

    float fTmpHeight = 0;
    int nIndex = 0;
    while (fTmpHeight < fSpaceLeft)
    {
      fTmpHeight += aItems.get (nIndex).getHeight ();

      if (fTmpHeight == fSpaceLeft)
      {
        // we can split between two drawables
        aHead = aItems.subList (0, nIndex + 1);
        if (nIndex + 1 < aItems.size ())
        {
          aTail = aItems.subList (nIndex + 1, aItems.size ());
        }
      }

      if (fTmpHeight > fSpaceLeft)
      {
        aHead = aItems.subList (0, nIndex);
        aToDivide = aItems.get (nIndex);
        if (nIndex + 1 < aItems.size ())
        {
          aTail = aItems.subList (nIndex + 1, aItems.size ());
        }
      }

      ++nIndex;
    }

    return new DividedList (aHead, aToDivide, aTail);
  }

  public static class DividedList
  {
    private final List <IDrawable3> m_aHead;
    private final IDrawable3 m_aDrawableToDivide;
    private final List <IDrawable3> m_aTail;

    public DividedList (final List <IDrawable3> aHead,
                        final IDrawable3 aDrawableToDivide,
                        final List <IDrawable3> aTail)
    {
      this.m_aHead = aHead;
      this.m_aDrawableToDivide = aDrawableToDivide;
      this.m_aTail = aTail;
    }

    public List <IDrawable3> getHead ()
    {
      return m_aHead;
    }

    public IDrawable3 getDrawableToDivide ()
    {
      return m_aDrawableToDivide;
    }

    public List <IDrawable3> getTail ()
    {
      return m_aTail;
    }

  }

}
