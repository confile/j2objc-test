package majestella.core.prototype.eventBus;

import majestella.core.prototype.eventBus.BEvent.Type;


public abstract class BEventBus {

  /**
   * Invokes {@code event.dispatch} with {@code handler}.
   * <p>
   * Protected to allow EventBus implementations in different packages to
   * dispatch events even though the {@code event.dispatch} method is protected.
   */
  protected static <H> void dispatchEvent(BEvent<H> event, H handler) {
    event.dispatch(handler);
  }

  /**
   * Sets {@code source} as the source of {@code event}.
   * <p>
   * Protected to allow EventBus implementations in different packages to set an
   * event source even though the {@code event.setSource} method is protected.
   */
  protected static void setSourceOfEvent(BEvent<?> event, Object source) {
    event.setSource(source);
  }

  /**
   * Adds an unfiltered handler to receive events of this type from all sources.
   * <p>
   * It is rare to call this method directly. More typically an {@link BEvent}
   * subclass will provide a static <code>register</code> method, or a widget
   * will accept handlers directly.
   * 
   * @param <H> The type of handler
   * @param type the event type associated with this handler
   * @param handler the handler
   * @return the handler registration, can be stored in order to remove the
   *         handler later
   */
  public abstract <H> BHandlerRegistration addHandler(Type<H> type, H handler);

  /**
   * Adds a handler to receive events of this type from the given source.
   * <p>
   * It is rare to call this method directly. More typically a {@link BEvent}
   * subclass will provide a static <code>register</code> method, or a widget
   * will accept handlers directly.
   * 
   * @param <H> The type of handler
   * @param type the event type associated with this handler
   * @param source the source associated with this handler
   * @param handler the handler
   * @return the handler registration, can be stored in order to remove the
   *         handler later
   */
  public abstract <H> BHandlerRegistration addHandlerToSource(Type<H> type, Object source, H handler);

  /**
   * Fires the event from no source. Only unfiltered handlers will receive it.
   * <p>
   * Any exceptions thrown by handlers will be bundled into a
   * {@link BUmbrellaException} and then re-thrown after all handlers have
   * completed. An exception thrown by a handler will not prevent other handlers
   * from executing.
   * 
   * @throws BUmbrellaException wrapping exceptions thrown by handlers
   * 
   * @param event the event to fire
   */
  public abstract void fireEvent(BEvent<?> event);

  /**
   * Fires the given event to the handlers listening to the event's type.
   * <p>
   * Any exceptions thrown by handlers will be bundled into a
   * {@link BUmbrellaException} and then re-thrown after all handlers have
   * completed. An exception thrown by a handler will not prevent other handlers
   * from executing.
   * 
   * @throws BUmbrellaException wrapping exceptions thrown by handlers
   * 
   * @param event the event to fire
   */
  public abstract void fireEventFromSource(BEvent<?> event, Object source);
  
  
}
