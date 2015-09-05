package majestella.core.prototype.mvp.proxy;

 
import majestella.core.prototype.eventBus.AbstractBEvent;
import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.eventBus.BEventHandler;
import majestella.core.prototype.eventBus.BHasHandlers;

 
public class AsyncCallStartEvent extends AbstractBEvent<AsyncCallStartEvent.AsyncCallStartHandler> {

  public static Type<AsyncCallStartHandler> TYPE = new Type<AsyncCallStartHandler>();
  
  public interface AsyncCallStartHandler extends BEventHandler {
    void onAsyncCallStart(AsyncCallStartEvent event);
  }
  
  
  @Override
  public AbstractBEvent.Type<AsyncCallStartHandler> getAssociatedType() {
    return TYPE;
  }
  
  public static Type<AsyncCallStartHandler> getType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AsyncCallStartHandler handler) {
    handler.onAsyncCallStart(this);
  }
  
  public static void fire(BHasHandlers source) {
  }
  
  
  public static void fire(BEventBus source) {
  }
 
  
}
