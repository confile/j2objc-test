package majestella.core.prototype.eventBus;


 
/**
 * Root of all events.
 * @author Dr. Michael Gorski
 *
 * @param <H>
 */
public abstract class AbstractBEvent<H extends BEventHandler> extends BEvent<H> {
  /**
   * Type class used to register events with the TODO change  HandlerManager.
   * <p>
   * Type is parameterized by the handler type in order to make the addHandler
   * method type safe.
   * </p>
   * 
   * @param <H> handler type
   */
  public static class Type<H> extends BEvent.Type<H> {
  }

  private boolean dead;

  /**
   * Constructor.
   */
  protected AbstractBEvent() {
  }

  @Override
  public abstract AbstractBEvent.Type<H> getAssociatedType();

  @Override
  public Object getSource() {
    assertLive();
    return super.getSource();
  }

  /**
   * Asserts that the event still should be accessed. All events are considered
   * to be "dead" after their original handler manager finishes firing them. An
   * event can be revived by calling {@link AbstractBEvent#revive()}.
   */
  protected void assertLive() {
    assert (!dead) : "This event has already finished being processed by its original handler manager, so you can no longer access it";
  }

  /**
   * Should only be called by HandlerManager. In other words, do not use
   * or call.
   * 
   * @param handler handler
   */
  protected abstract void dispatch(H handler);

  /**
   * Is the event current live?
   * 
   * @return whether the event is live
   */
  protected final boolean isLive() {
    return !dead;
  }

  /**
   * Kill the event. After the event has been killed, users cannot really on its
   * values or functions being available.
   */
  protected void kill() {
    dead = true;
    setSource(null);
  }

  /**
   * Revives the event. Used when recycling event instances.
   */
  protected void revive() {
    dead = false;
    setSource(null);
  }

  void overrideSource(Object source) {
    super.setSource(source);
  }
}

