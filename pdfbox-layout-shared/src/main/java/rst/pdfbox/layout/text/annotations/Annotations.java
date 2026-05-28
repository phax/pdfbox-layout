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
    private float m_fBaselineOffsetScale = 0f;
    private float m_fLineWeight = 1f;

    public UnderlineAnnotation (final float fBaselineOffsetScale, final float fLineWeight)
    {
      this.m_fBaselineOffsetScale = fBaselineOffsetScale;
      this.m_fLineWeight = fLineWeight;
    }

    public float getBaselineOffsetScale ()
    {
      return m_fBaselineOffsetScale;
    }

    public float getLineWeight ()
    {
      return m_fLineWeight;
    }

    @Override
    public String toString ()
    {
      return "UnderlineAnnotation [baselineOffsetScale=" + m_fBaselineOffsetScale + ", lineWeight=" + m_fLineWeight + "]";
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

    private final String m_sHyperlinkUri;
    private final ELinkStyle m_eLinkStyle;

    /**
     * Creates a hyperlink annotation.
     *
     * @param sHyperlinkUri
     *        the hyperlinkUri.
     * @param eLinkStyle
     *        the link style.
     */
    public HyperlinkAnnotation (final String sHyperlinkUri, final ELinkStyle eLinkStyle)
    {
      this.m_sHyperlinkUri = sHyperlinkUri;
      this.m_eLinkStyle = eLinkStyle;
    }

    /**
     * @return the hyperlink URI.
     */
    public String getHyperlinkURI ()
    {
      return m_sHyperlinkUri;
    }

    public ELinkStyle getLinkStyle ()
    {
      return m_eLinkStyle;
    }

    @Override
    public String toString ()
    {
      return "HyperlinkAnnotation [hyperlinkUri=" + m_sHyperlinkUri + ", linkStyle=" + m_eLinkStyle + "]";
    }

  }

  /**
   * Represents a anchor annotation
   */
  public static class AnchorAnnotation implements IAnnotation
  {
    private final String m_sAnchor;

    /**
     * Creates a anchor annotation.
     *
     * @param sAnchor
     *        the anchor name.
     */
    public AnchorAnnotation (final String sAnchor)
    {
      this.m_sAnchor = sAnchor;
    }

    /**
     * @return the anchor name.
     */
    public String getAnchor ()
    {
      return m_sAnchor;
    }

    @Override
    public String toString ()
    {
      return "AnchorAnnotation [anchor=" + m_sAnchor + "]";
    }

  }
}
