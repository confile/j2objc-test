package majestella.core.prototype.eventBus;


/**
 * Base Event object.
 * 
 * @param <H> interface implemented by handlers of this kind of event
 */
public abstract class BEvent<H> {
  /**
   * Type class used to register events with an {@link BEventBus}.
   * 
   * @param <H> handler type
   */
  public static class Type<H> {
    private static int nextHashCode;
    private final int index;

    /**
     * Constructor.
     */
    public Type() {
      index = ++nextHashCode;
    }

    @Override
    public final int hashCode() {
      return index;
    }

    @Override
    public String toString() {
      return "Event type";
    }
  }

  private Object source;

  /**
   * Constructor.
   */
  protected BEvent() {
  }

  /**
   * Returns the {@link Type} used to register this event, allowing an
   * {@link BEventBus} to find handlers of the appropriate class.
   * 
   * @return the type
   */
  public abstract Type<H> getAssociatedType();

  /**
   * Returns the source for this event. The type and meaning of the source is
   * arbitrary, and is most useful as a secondary key for handler registration.
   * (See {@link BEventBus#addHandlerToSource}, which allows a handler to
   * register for events of a particular type, tied to a particular source.)
   * <p>
   * Note that the source is actually set at dispatch time, e.g. via
   * {@link BEventBus#fireEventFromSource(BEvent, Object)}.
   * 
   * @return object representing the source of this event
   */
  public Object getSource() {
    return source;
  }

  /**
   * This is a method used primarily for debugging. It gives a string
   * representation of the event details. This does not override the toString
   * method because the compiler cannot always optimize toString out correctly.
   * Event types should override as desired.
   * 
   * @return a string representing the event's specifics.
   */
  public String toDebugString() {
    String name = this.getClass().getName();
    name = name.substring(name.lastIndexOf(".") + 1);
    return "event: " + name + ":";
  }

  /**
   * The toString() for abstract event is overridden to avoid accidently
   * including class literals in the compiled output. Use {@link BEvent}
   * #toDebugString to get more information about the event.
   */
  @Override
  public String toString() {
    return "An event type";
  }

  /**
   * Implemented by subclasses to to invoke their handlers in a type safe
   * manner. Intended to be called by {@link BEventBus#fireEvent(BEvent)} or
   * {@link BEventBus#fireEventFromSource(BEvent, Object)}. 
   * 
   * @param handler handler
   * @see BEventBus#dispatchEvent(BEvent, Object)
   */
  protected abstract void dispatch(H handler);

  /**
   * Set the source that triggered this event. Intended to be called by the
   * {@link BEventBus} during dispatch.
   * 
   * @param source the source of this event.
   * @see BEventBus#fireEventFromSource(BEvent, Object)
   * @see BEventBus#setSourceOfEvent(BEvent, Object)
   */
  protected void setSource(Object source) {
    this.source = source;
  }
}
