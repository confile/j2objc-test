package majestella.core.prototype.mvp.proxy;

 
import majestella.core.prototype.eventBus.AbstractBEvent;
import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.eventBus.BEventHandler;
import majestella.core.prototype.eventBus.BHasHandlers;

 
public class AsyncCallSucceedEvent extends AbstractBEvent<AsyncCallSucceedEvent.AsyncCallSucceedHandler> {

  public static Type<AsyncCallSucceedHandler> TYPE = new Type<AsyncCallSucceedHandler>();
  
  public interface AsyncCallSucceedHandler extends BEventHandler {
    void onAsyncCallSucceed(AsyncCallSucceedEvent event);
  }
  
  
  @Override
  public AbstractBEvent.Type<AsyncCallSucceedHandler> getAssociatedType() {
    return TYPE;
  }
  
  public static Type<AsyncCallSucceedHandler> getType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AsyncCallSucceedHandler handler) {
    handler.onAsyncCallSucceed(this);
  }
  
  public static void fire(BHasHandlers source) {
  }
  
  
  public static void fire(BEventBus source) {
  }
 
  
}
