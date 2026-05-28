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
  private final List <IAnnotation> m_aAnnotations = new ArrayList <> ();

  /**
   * Creates a styled text.
   *
   * @param sText
   *        the text to draw. Must not contain line feeds ('\n').
   * @param aFontDescriptor
   *        the font to use.
   * @param aColor
   *        the color to use.
   * @param fBaselineOffset
   *        the offset of the baseline.
   * @param fLeftMargin
   *        the margin left to the text.
   * @param fRightMargin
   *        the margin right to the text.
   * @param aAnnotations
   *        the annotations associated with the text.
   */
  public AnnotatedStyledText (final String sText,
                              final FontDescriptor aFontDescriptor,
                              final Color aColor,
                              final float fLeftMargin,
                              final float fRightMargin,
                              final float fBaselineOffset,
                              final Collection <? extends IAnnotation> aAnnotations)
  {
    super (sText, aFontDescriptor, aColor, fBaselineOffset, fLeftMargin, fRightMargin);
    if (aAnnotations != null)
    {
      this.m_aAnnotations.addAll (aAnnotations);
    }
  }

  /**
   * Creates a styled text.
   *
   * @param sText
   *        the text to draw. Must not contain line feeds ('\n').
   * @param fSize
   *        the size of the font.
   * @param aFont
   *        the font to use..
   * @param aColor
   *        the color to use.
   * @param fBaselineOffset
   *        the offset of the baseline.
   * @param aAnnotations
   *        the annotations associated with the text.
   */
  public AnnotatedStyledText (final String sText,
                              final float fSize,
                              final PDFont aFont,
                              final Color aColor,
                              final float fBaselineOffset,
                              final Collection <? extends IAnnotation> aAnnotations)
  {
    this (sText, new FontDescriptor (aFont, fSize), aColor, fBaselineOffset, 0, 0, aAnnotations);
  }

  @Override
  public Iterator <IAnnotation> iterator ()
  {
    return m_aAnnotations.iterator ();
  }

  @SuppressWarnings ("unchecked")
  @Override
  public <T extends IAnnotation> Iterable <T> getAnnotationsOfType (final Class <T> aType)
  {
    List <T> aResult = null;
    for (final IAnnotation aAnnotation : m_aAnnotations)
    {
      if (aType.isAssignableFrom (aAnnotation.getClass ()))
      {
        if (aResult == null)
        {
          aResult = new ArrayList <> ();
        }
        aResult.add ((T) aAnnotation);
      }
    }

    if (aResult == null)
    {
      return Collections.emptyList ();
    }
    return aResult;
  }

  /**
   * Adds an annotation.
   *
   * @param aAnnotation
   *        the annotation to add.
   */
  public void addAnnotation (final IAnnotation aAnnotation)
  {
    m_aAnnotations.add (aAnnotation);
  }

  /**
   * Adds all annotations.
   *
   * @param aAnnos
   *        the annotations to add.
   */
  public void addAllAnnotation (final Collection <IAnnotation> aAnnos)
  {
    m_aAnnotations.addAll (aAnnos);
  }

  @Override
  public AnnotatedStyledText inheritAttributes (final String sText, final float fLeftMargin, final float fRightMargin)
  {
    return new AnnotatedStyledText (sText,
                                    getFontDescriptor (),
                                    getColor (),
                                    getBaselineOffset (),
                                    fLeftMargin,
                                    fRightMargin,
                                    m_aAnnotations);
  }
}
