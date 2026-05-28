package rst.pdfbox.layout.elements.render;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;

import rst.pdfbox.layout.elements.ControlElement;
import rst.pdfbox.layout.elements.Cutter;
import rst.pdfbox.layout.elements.IDividable;
import rst.pdfbox.layout.elements.IDividable.Divided;
import rst.pdfbox.layout.elements.IDrawable3;
import rst.pdfbox.layout.elements.IElement;
import rst.pdfbox.layout.elements.PageFormat;
import rst.pdfbox.layout.elements.VerticalSpacer;
import rst.pdfbox.layout.text.EAlignment;
import rst.pdfbox.layout.text.IWidthRespecting;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.util.CompatibilityHelper;

/**
 * Layout implementation that stacks drawables vertically onto the page. If the remaining height on
 * the page is not sufficient for the drawable, it will be {@link IDividable divided}. Any given
 * {@link VerticalLayoutHint} will be taken into account to calculate the position, width, alignment
 * etc.
 */
public class VerticalLayout implements ILayout
{

  protected boolean m_bRemoveLeadingEmptyVerticalSpace = true;

  /**
   * See {@link IDrawable3#removeLeadingEmptyVerticalSpace()}
   *
   * @return <code>true</code> if empty space (e.g. empty lines) should be removed at the begin of a
   *         page.
   */
  public boolean isRemoveLeadingEmptyVerticalSpace ()
  {
    return m_bRemoveLeadingEmptyVerticalSpace;
  }

  /**
   * Indicates if empty space (e.g. empty lines) should be removed at the begin of a page. See
   * {@link IDrawable3#removeLeadingEmptyVerticalSpace()}
   *
   * @param bRemoveLeadingEmptyLines
   *        <code>true</code> if space should be removed.
   */
  public void setRemoveLeadingEmptyVerticalSpace (final boolean bRemoveLeadingEmptyLines)
  {
    this.m_bRemoveLeadingEmptyVerticalSpace = bRemoveLeadingEmptyLines;
  }

  /**
   * Turns to the next area, usually a page.
   *
   * @param aRenderContext
   *        the render context.
   * @throws IOException
   *         by pdfbox.
   */
  protected void turnPage (final RenderContext aRenderContext) throws IOException
  {
    aRenderContext.newPage ();
  }

  /**
   * @param aRenderContext
   *        the render context.
   * @return the target width to draw to.
   */
  protected float getTargetWidth (final RenderContext aRenderContext)
  {
    final float fTargetWidth = aRenderContext.getWidth ();
    return fTargetWidth;
  }

  @Override
  public boolean render (final RenderContext aRenderContext,
                         final IElement aElement,
                         final ILayoutHint aLayoutHint) throws IOException
  {
    if (aElement instanceof IDrawable3)
    {
      render (aRenderContext, (IDrawable3) aElement, aLayoutHint);
      return true;
    }
    if (aElement == ControlElement.NEWPAGE)
    {
      turnPage (aRenderContext);
      return true;
    }

    return false;
  }

  public void render (final RenderContext aRenderContext,
                      final IDrawable3 aDrawable,
                      final ILayoutHint aLayoutHint) throws IOException
  {
    if (aDrawable.getAbsolutePosition () != null)
    {
      renderAbsolute (aRenderContext, aDrawable, aLayoutHint, aDrawable.getAbsolutePosition ());
    }
    else
    {
      renderReleative (aRenderContext, aDrawable, aLayoutHint);
    }
  }

  /**
   * Draws at the given position, ignoring all layouting rules.
   *
   * @param aRenderContext
   *        the context providing all rendering state.
   * @param aDrawable
   *        the drawable to draw.
   * @param aLayoutHint
   *        the layout hint used to layout.
   * @param aPosition
   *        the left upper position to start drawing at.
   * @throws IOException
   *         by pdfbox
   */
  protected void renderAbsolute (final RenderContext aRenderContext,
                                 final IDrawable3 aDrawable,
                                 final ILayoutHint aLayoutHint,
                                 final Position aPosition) throws IOException
  {
    aDrawable.draw (aRenderContext.getPdDocument (), aRenderContext.getContentStream (), aPosition, aRenderContext);
  }

  /**
   * Renders the drawable at the {@link RenderContext#getCurrentPosition() current position}. This
   * method is responsible taking any top or bottom margin described by the (Vertical-)LayoutHint
   * into account. The actual rendering of the drawable is performed by
   * {@link #layoutAndDrawReleative(RenderContext, IDrawable3, ILayoutHint)}.
   *
   * @param aRenderContext
   *        the context providing all rendering state.
   * @param aDrawable
   *        the drawable to draw.
   * @param aLayoutHint
   *        the layout hint used to layout.
   * @throws IOException
   *         by pdfbox
   */
  protected void renderReleative (final RenderContext aRenderContext,
                                  final IDrawable3 aDrawable,
                                  final ILayoutHint aLayoutHint) throws IOException
  {
    VerticalLayoutHint aVerticalLayoutHint = null;
    if (aLayoutHint instanceof VerticalLayoutHint)
    {
      aVerticalLayoutHint = (VerticalLayoutHint) aLayoutHint;
      if (aVerticalLayoutHint.getMarginTop () > 0)
      {
        layoutAndDrawReleative (aRenderContext,
                                new VerticalSpacer (aVerticalLayoutHint.getMarginTop ()),
                                aVerticalLayoutHint);
      }
    }

    layoutAndDrawReleative (aRenderContext, aDrawable, aVerticalLayoutHint);

    if (aVerticalLayoutHint != null)
    {
      if (aVerticalLayoutHint.getMarginBottom () > 0)
      {
        layoutAndDrawReleative (aRenderContext,
                                new VerticalSpacer (aVerticalLayoutHint.getMarginBottom ()),
                                aVerticalLayoutHint);
      }
    }
  }

  /**
   * Adjusts the width of the drawable (if it is {@link IWidthRespecting}), and divides it onto
   * multiple pages if necessary. Actual drawing is delegated to
   * {@link #drawReletivePartAndMovePosition(RenderContext, IDrawable3, ILayoutHint, boolean)} .
   *
   * @param aRenderContext
   *        the context providing all rendering state.
   * @param aDrawable
   *        the drawable to draw.
   * @param aLayoutHint
   *        the layout hint used to layout.
   * @throws IOException
   *         by pdfbox
   */
  protected void layoutAndDrawReleative (final RenderContext aRenderContext,
                                         final IDrawable3 aDrawable,
                                         final ILayoutHint aLayoutHint) throws IOException
  {
    float fTargetWidth = getTargetWidth (aRenderContext);
    boolean bMovePosition = true;
    VerticalLayoutHint aVerticalLayoutHint = null;
    if (aLayoutHint instanceof VerticalLayoutHint)
    {
      aVerticalLayoutHint = (VerticalLayoutHint) aLayoutHint;
      fTargetWidth -= aVerticalLayoutHint.getMarginLeft ();
      fTargetWidth -= aVerticalLayoutHint.getMarginRight ();
      bMovePosition = !aVerticalLayoutHint.isResetY ();
    }

    float fOldMaxWidth = -1;
    if (aDrawable instanceof IWidthRespecting)
    {
      final IWidthRespecting aFlowing = (IWidthRespecting) aDrawable;
      fOldMaxWidth = aFlowing.getMaxWidth ();
      if (fOldMaxWidth < 0)
      {
        aFlowing.setMaxWidth (fTargetWidth);
      }
    }

    IDrawable3 aDrawablePart = removeLeadingEmptyVerticalSpace (aDrawable, aRenderContext);
    while (aRenderContext.getRemainingHeight () < aDrawablePart.getHeight ())
    {
      IDividable aDividable = null;
      if (aDrawablePart instanceof IDividable)
      {
        aDividable = (IDividable) aDrawablePart;
      }
      else
      {
        aDividable = new Cutter (aDrawablePart);
      }
      final Divided aDivided = aDividable.divide (aRenderContext.getRemainingHeight (), aRenderContext.getHeight ());
      drawReletivePartAndMovePosition (aRenderContext, (IDrawable3) aDivided.getFirst (), aLayoutHint, true);

      // new page
      turnPage (aRenderContext);

      aDrawablePart = (IDrawable3) aDivided.getTail ();
      aDrawablePart = removeLeadingEmptyVerticalSpace (aDrawablePart, aRenderContext);
    }

    drawReletivePartAndMovePosition (aRenderContext, aDrawablePart, aLayoutHint, bMovePosition);

    if (aDrawable instanceof IWidthRespecting)
    {
      if (fOldMaxWidth < 0)
      {
        ((IWidthRespecting) aDrawable).setMaxWidth (fOldMaxWidth);
      }
    }
  }

  /**
   * Actually draws the (drawble) part at the {@link RenderContext#getCurrentPosition()} and -
   * depending on flag <code>movePosition</code> - moves to the new Y position. Any left or right
   * margin is taken into account to calculate the position and alignment.
   *
   * @param aRenderContext
   *        the context providing all rendering state.
   * @param aDrawable
   *        the drawable to draw.
   * @param aLayoutHint
   *        the layout hint used to layout.
   * @param bMovePosition
   *        indicates if the position should be moved (vertically) after drawing.
   * @throws IOException
   *         by pdfbox
   */
  protected void drawReletivePartAndMovePosition (final RenderContext aRenderContext,
                                                  final IDrawable3 aDrawable,
                                                  final ILayoutHint aLayoutHint,
                                                  final boolean bMovePosition) throws IOException
  {
    final PDPageContentStream aContentStream = aRenderContext.getContentStream ();
    final PageFormat aPageFormat = aRenderContext.getPageFormat ();
    float fOffsetX = 0;
    if (aLayoutHint instanceof VerticalLayoutHint)
    {
      final VerticalLayoutHint aVerticalLayoutHint = (VerticalLayoutHint) aLayoutHint;
      final EAlignment eAlignment = aVerticalLayoutHint.getAlignment ();
      final float fHorizontalExtraSpace = getTargetWidth (aRenderContext) - aDrawable.getWidth ();
      switch (eAlignment)
      {
        case Right:
          fOffsetX = fHorizontalExtraSpace - aVerticalLayoutHint.getMarginRight ();
          break;
        case Center:
          fOffsetX = fHorizontalExtraSpace / 2f;
          break;
        default:
          fOffsetX = aVerticalLayoutHint.getMarginLeft ();
          break;
      }
    }

    aContentStream.saveGraphicsState ();
    aContentStream.addRect (0, aPageFormat.getMarginBottom (), aRenderContext.getPageWidth (), aRenderContext.getHeight ());
    CompatibilityHelper.clip (aContentStream);

    aDrawable.draw (aRenderContext.getPdDocument (),
                    aContentStream,
                    aRenderContext.getCurrentPosition ().add (fOffsetX, 0),
                    aRenderContext);

    aContentStream.restoreGraphicsState ();

    if (bMovePosition)
    {
      aRenderContext.movePositionBy (0, -aDrawable.getHeight ());
    }
  }

  /**
   * Indicates if the current position is the top of page.
   *
   * @param aRenderContext
   *        the render context.
   * @return <code>true</code> if the current position is top of page.
   */
  protected boolean isPositionTopOfPage (final RenderContext aRenderContext)
  {
    return aRenderContext.getCurrentPosition ().getY () == aRenderContext.getUpperLeft ().getY ();
  }

  /**
   * Removes empty space (e.g. empty lines) at the begin of a page. See
   * {@link IDrawable3#removeLeadingEmptyVerticalSpace()}
   *
   * @param aDrawable
   *        the drawable to process.
   * @param aRenderContext
   *        the render context.
   * @return the processed drawable
   * @throws IOException
   *         by pdfbox
   */
  protected IDrawable3 removeLeadingEmptyVerticalSpace (final IDrawable3 aDrawable,
                                                      final RenderContext aRenderContext) throws IOException
  {
    if (isRemoveLeadingEmptyVerticalSpace () && isPositionTopOfPage (aRenderContext))
    {
      return aDrawable.removeLeadingEmptyVerticalSpace ();
    }
    return aDrawable;
  }

}
