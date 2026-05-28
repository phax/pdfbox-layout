package rst.pdfbox.layout.elements;

/**
 * Utility class to create elements that allow the manipulation of the current layout position. Use
 * carefully.
 */
public class PositionControl extends ControlElement
{

  /**
   * Use this value in {@link #createSetPosition(Float, Float)} to reset either one or both
   * coordinates to the marked position.
   */
  public static final Float MARKED_POSITION = Float.valueOf (Float.NEGATIVE_INFINITY);

  /**
   * Add this element to a document to mark the current position.
   *
   * @return the created element
   */
  public static MarkPosition createMarkPosition ()
  {
    return new MarkPosition ();
  }

  /**
   * Add this element to a document to manipulate the current layout position. If <code>null</code>,
   * the position won't be changed (useful if you want to change only X or Y). If the value is
   * {@link #MARKED_POSITION}, it wil be (re-)set to the marked position.
   *
   * @param aNewX
   *        the new X position.
   * @param aNewY
   *        new new Y position.
   * @return the created element
   */
  public static SetPosition createSetPosition (final Float aNewX, final Float aNewY)
  {
    return new SetPosition (aNewX, aNewY);
  }

  /**
   * Add this element to a document to manipulate the current layout position by a relative amount.
   * If <code>null</code>, the position won't be changed (useful if you want to change only X or Y).
   *
   * @param fRelativeX
   *        the value to change position in X direction.
   * @param fRelativeY
   *        the value to change position in Y direction.
   * @return the created element
   */
  public static MovePosition createMovePosition (final float fRelativeX, final float fRelativeY)
  {
    return new MovePosition (fRelativeX, fRelativeY);
  }

  public static class MarkPosition extends PositionControl
  {
    private MarkPosition ()
    {
      super ("MARK_POSITION");
    }
  }

  public static class SetPosition extends PositionControl
  {
    private final Float m_aNewX;
    private final Float m_aNewY;

    private SetPosition (final Float aNewX, final Float aNewY)
    {
      super ("SET_POSITION x:" + aNewX + ", y" + aNewY);
      this.m_aNewX = aNewX;
      this.m_aNewY = aNewY;
    }

    public Float getX ()
    {
      return m_aNewX;
    }

    public Float getY ()
    {
      return m_aNewY;
    }

  }

  public static class MovePosition extends PositionControl
  {
    private final float m_fRelativeX;
    private final float m_fRelativeY;

    private MovePosition (final float fRelativeX, final float fRelativeY)
    {
      super ("SET_POSITION x:" + fRelativeX + ", y" + fRelativeY);
      this.m_fRelativeX = fRelativeX;
      this.m_fRelativeY = fRelativeY;
    }

    public float getX ()
    {
      return m_fRelativeX;
    }

    public float getY ()
    {
      return m_fRelativeY;
    }

  }

  private PositionControl (final String sName)
  {
    super (sName);
  }

}
