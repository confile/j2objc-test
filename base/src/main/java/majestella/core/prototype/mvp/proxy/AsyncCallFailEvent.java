package majestella.core.prototype.mvp.proxy;

 
import majestella.core.prototype.eventBus.AbstractBEvent;
import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.eventBus.BEventHandler;
import majestella.core.prototype.eventBus.BHasHandlers;

 
public class AsyncCallFailEvent extends AbstractBEvent<AsyncCallFailEvent.AsyncCallFailHandler> {

  public static Type<AsyncCallFailHandler> TYPE = new Type<AsyncCallFailHandler>();
  
  public interface AsyncCallFailHandler extends BEventHandler {
    void onAsyncCallFail(AsyncCallFailEvent event);
  }
  
  private final Throwable caught;
  
  AsyncCallFailEvent(Throwable caught) {
    this.caught = caught;
  }
  
  @Override
  public AbstractBEvent.Type<AsyncCallFailHandler> getAssociatedType() {
    return TYPE;
  }
  
  public static Type<AsyncCallFailHandler> getType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AsyncCallFailHandler handler) {
    handler.onAsyncCallFail(this);
  }
  
  public static void fire(BHasHandlers source, Throwable caught) {
  }
  
  
  public static void fire(BEventBus source, Throwable caught) {
  }
 
  
}
