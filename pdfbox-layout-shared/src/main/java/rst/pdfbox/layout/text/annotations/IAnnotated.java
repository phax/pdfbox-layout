package rst.pdfbox.layout.text.annotations;

/**
 * Marker interface for annotated objects.
 */
public interface IAnnotated extends Iterable <IAnnotation>
{

  /**
   * Gets the annotations of a specific type.
   *
   * @param aType
   *        the type of interest.
   * @return the annotations of that type, or an empty collection.
   * @param <T>
   *        the annotation type.
   */
  <T extends IAnnotation> Iterable <T> getAnnotationsOfType (final Class <T> aType);
}
