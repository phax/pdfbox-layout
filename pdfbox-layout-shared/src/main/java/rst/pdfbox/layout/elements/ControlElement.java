package rst.pdfbox.layout.elements;

/**
 * ControlElements do not have a drawable representation, but control page flow, rendering etc.
 */
public class ControlElement implements IElement
{
  /**
   * Triggers a new page in a document.
   */
  public final static ControlElement NEWPAGE = new ControlElement ("NEWPAGE");

  private final String m_sName;

  public ControlElement (final String sName)
  {
    this.m_sName = sName;
  }

  @Override
  public String toString ()
  {
    return "ControlElement [NEWPAGE=" + NEWPAGE + ", name=" + m_sName + "]";
  }
}
