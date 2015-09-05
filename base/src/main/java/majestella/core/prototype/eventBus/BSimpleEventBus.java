package majestella.core.prototype.eventBus;

import java.util.List;

import majestella.core.prototype.eventBus.BEvent.Type;


/**
 * Basic implementation of {@link BEventBus}.
 */
public class BSimpleEventBus extends BEventBus {
 
  
  
  public BSimpleEventBus() {
    
  }


  @Override
  public <H> BHandlerRegistration addHandler(Type<H> type, H handler) {
    return null;
  }

  @Override
  public <H> BHandlerRegistration addHandlerToSource(final BEvent.Type<H> type, final Object source,
      final H handler) {
    if (source == null) {
      throw new NullPointerException("Cannot add a handler with a null source");
    }

    return null;
  }

  @Override
  public void fireEvent(BEvent<?> event) {
    
  }

  @Override
  public void fireEventFromSource(BEvent<?> event, Object source) {
   
  }

 
  @Deprecated
  protected <H> void doRemove(BEvent.Type<H> type, Object source, H handler) {
  
  }

 
  @Deprecated
  protected <H> H getHandler(BEvent.Type<H> type, int index) {
 
    List<H> l = null;
    return l.get(index);
  }

  /**
   * @deprecated required by legacy features in GWT's old HandlerManager
   */
  @Deprecated
  protected int getHandlerCount(BEvent.Type<?> eventKey) {
    return 0;
  }

  /**
   * @deprecated required by legacy features in GWT's old HandlerManager
   */
  @Deprecated
  protected boolean isEventHandled(BEvent.Type<?> eventKey) {
    return false;
  }
 
   

  
}