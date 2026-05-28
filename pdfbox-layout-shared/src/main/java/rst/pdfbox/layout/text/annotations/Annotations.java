package rst.pdfbox.layout.text.annotations;

/**
 * Container for all annotations.
 */
public class Annotations
{

  /**
   * Represents a underline annotation
   */
  public static class UnderlineAnnotation implements IAnnotation
  {
    private float baselineOffsetScale = 0f;
    private float lineWeight = 1f;

    public UnderlineAnnotation (final float baselineOffsetScale, final float lineWeight)
    {
      this.baselineOffsetScale = baselineOffsetScale;
      this.lineWeight = lineWeight;
    }

    public float getBaselineOffsetScale ()
    {
      return baselineOffsetScale;
    }

    public float getLineWeight ()
    {
      return lineWeight;
    }

    @Override
    public String toString ()
    {
      return "UnderlineAnnotation [baselineOffsetScale=" + baselineOffsetScale + ", lineWeight=" + lineWeight + "]";
    }

  }

  /**
   * Represents a hyperlink annotation
   */
  public static class HyperlinkAnnotation implements IAnnotation
  {

    public enum ELinkStyle
    {
      /**
       * Underline.
       */
      ul,
      /**
       * None.
       */
      none;
    }

    private final String hyperlinkUri;
    private final ELinkStyle linkStyle;

    /**
     * Creates a hyperlink annotation.
     *
     * @param hyperlinkUri
     *        the hyperlinkUri.
     * @param linkStyle
     *        the link style.
     */
    public HyperlinkAnnotation (final String hyperlinkUri, final ELinkStyle linkStyle)
    {
      this.hyperlinkUri = hyperlinkUri;
      this.linkStyle = linkStyle;
    }

    /**
     * @return the hyperlink URI.
     */
    public String getHyperlinkURI ()
    {
      return hyperlinkUri;
    }

    public ELinkStyle getLinkStyle ()
    {
      return linkStyle;
    }

    @Override
    public String toString ()
    {
      return "HyperlinkAnnotation [hyperlinkUri=" + hyperlinkUri + ", linkStyle=" + linkStyle + "]";
    }

  }

  /**
   * Represents a anchor annotation
   */
  public static class AnchorAnnotation implements IAnnotation
  {
    private final String anchor;

    /**
     * Creates a anchor annotation.
     *
     * @param anchor
     *        the anchor name.
     */
    public AnchorAnnotation (final String anchor)
    {
      this.anchor = anchor;
    }

    /**
     * @return the anchor name.
     */
    public String getAnchor ()
    {
      return anchor;
    }

    @Override
    public String toString ()
    {
      return "AnchorAnnotation [anchor=" + anchor + "]";
    }

  }
}
