package majestella.core.prototype.mvp;

 
import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.mvp.proxy.Proxy;


public abstract class BAbstractPresenter<V extends BBaseView, Proxy_ extends Proxy<?>> extends BAbstractPresenterWidget<V> {

  public BAbstractPresenter(BEventBus eventBus, V view) {
//    super(eventBus, view);
  }

  
  
  
}
