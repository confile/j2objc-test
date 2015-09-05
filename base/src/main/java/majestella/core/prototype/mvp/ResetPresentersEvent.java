package majestella.core.prototype.mvp;

 
import majestella.core.prototype.eventBus.AbstractBEvent;
import majestella.core.prototype.eventBus.BEventHandler;
import majestella.core.prototype.eventBus.BHasHandlers;



public class ResetPresentersEvent extends AbstractBEvent<ResetPresentersEvent.ResetPresentersHandler> {

  public static Type<ResetPresentersHandler> TYPE = new Type<ResetPresentersHandler>();
  
  public interface ResetPresentersHandler extends BEventHandler {
    void onPresenterReset(ResetPresentersEvent event);
  }
  
  @Override
  public AbstractBEvent.Type<ResetPresentersHandler> getAssociatedType() {
    return TYPE;
  }
  
  public static Type<ResetPresentersHandler> getType() {
    return TYPE;
  }

  @Override
  protected void dispatch(ResetPresentersHandler handler) {
    handler.onPresenterReset(this);
  }
  
 
  public static void fire(BHasHandlers source) {
  }
  
 
  
}
