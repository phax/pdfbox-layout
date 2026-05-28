package rst.pdfbox.layout.text.annotations;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.font.PDFont;

import rst.pdfbox.layout.text.FontDescriptor;
import rst.pdfbox.layout.text.StyledText;

/**
 * Extension of styled text that supports annotations.
 */
public class AnnotatedStyledText extends StyledText implements IAnnotated
{
  private final List <IAnnotation> annotations = new ArrayList <> ();

  /**
   * Creates a styled text.
   *
   * @param text
   *        the text to draw. Must not contain line feeds ('\n').
   * @param fontDescriptor
   *        the font to use.
   * @param color
   *        the color to use.
   * @param baselineOffset
   *        the offset of the baseline.
   * @param leftMargin
   *        the margin left to the text.
   * @param rightMargin
   *        the margin right to the text.
   * @param annotations
   *        the annotations associated with the text.
   */
  public AnnotatedStyledText (final String text,
                              final FontDescriptor fontDescriptor,
                              final Color color,
                              final float leftMargin,
                              final float rightMargin,
                              final float baselineOffset,
                              final Collection <? extends IAnnotation> annotations)
  {
    super (text, fontDescriptor, color, baselineOffset, leftMargin, rightMargin);
    if (annotations != null)
    {
      this.annotations.addAll (annotations);
    }
  }

  /**
   * Creates a styled text.
   *
   * @param text
   *        the text to draw. Must not contain line feeds ('\n').
   * @param size
   *        the size of the font.
   * @param font
   *        the font to use..
   * @param color
   *        the color to use.
   * @param baselineOffset
   *        the offset of the baseline.
   * @param annotations
   *        the annotations associated with the text.
   */
  public AnnotatedStyledText (final String text,
                              final float size,
                              final PDFont font,
                              final Color color,
                              final float baselineOffset,
                              final Collection <? extends IAnnotation> annotations)
  {
    this (text, new FontDescriptor (font, size), color, baselineOffset, 0, 0, annotations);
  }

  @Override
  public Iterator <IAnnotation> iterator ()
  {
    return annotations.iterator ();
  }

  @SuppressWarnings ("unchecked")
  @Override
  public <T extends IAnnotation> Iterable <T> getAnnotationsOfType (final Class <T> type)
  {
    List <T> result = null;
    for (final IAnnotation annotation : annotations)
    {
      if (type.isAssignableFrom (annotation.getClass ()))
      {
        if (result == null)
        {
          result = new ArrayList <> ();
        }
        result.add ((T) annotation);
      }
    }

    if (result == null)
    {
      return Collections.emptyList ();
    }
    return result;
  }

  /**
   * Adds an annotation.
   *
   * @param annotation
   *        the annotation to add.
   */
  public void addAnnotation (final IAnnotation annotation)
  {
    annotations.add (annotation);
  }

  /**
   * Adds all annotations.
   *
   * @param annos
   *        the annotations to add.
   */
  public void addAllAnnotation (final Collection <IAnnotation> annos)
  {
    annotations.addAll (annos);
  }

  @Override
  public AnnotatedStyledText inheritAttributes (final String text, final float leftMargin, final float rightMargin)
  {
    return new AnnotatedStyledText (text,
                                    getFontDescriptor (),
                                    getColor (),
                                    getBaselineOffset (),
                                    leftMargin,
                                    rightMargin,
                                    annotations);
  }
}
