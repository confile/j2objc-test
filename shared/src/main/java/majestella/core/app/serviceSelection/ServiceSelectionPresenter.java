package majestella.core.app.serviceSelection;

 
import javax.inject.Inject;

import majestella.core.prototype.annotation.NameToken;
import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.mvp.BAbstractPresenter;
import majestella.core.prototype.mvp.BBaseView;
import majestella.core.prototype.mvp.proxy.ProxyPlace;

import com.google.auto.factory.AutoFactory;
import com.google.auto.factory.Provided;
 
@AutoFactory(className="ServiceSelectionPresenterFactory")
public class ServiceSelectionPresenter extends BAbstractPresenter<ServiceSelectionPresenter.MyView, ServiceSelectionPresenter.MyProxy> {

  public interface MyView extends BBaseView {

  }
  
  @NameToken("serviceSelection")
  public interface MyProxy extends ProxyPlace<ServiceSelectionPresenter> {
  }
  
 
  
  
  @Inject
  public ServiceSelectionPresenter(@Provided BEventBus eventBus, BBaseView view, @Provided MyProxy proxy) {
    super(eventBus, (MyView)view); 
   
  }
  
  
  
}
