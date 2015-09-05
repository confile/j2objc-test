package majestella.core.app.locationMain;

 
import majestella.core.prototype.annotation.NameToken;
import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.mvp.BAbstractPresenter;
import majestella.core.prototype.mvp.BBaseView;
import majestella.core.prototype.mvp.proxy.ProxyPlace;
import majestella.core.prototype.navigation.BPlaceManager;
 

public class LocationMainPresenter extends BAbstractPresenter<LocationMainPresenter.MyView, LocationMainPresenter.MyProxy> {

  public interface MyView extends BBaseView {   
    
  }
  
  @NameToken("locationMain")
  public interface MyProxy extends ProxyPlace<LocationMainPresenter> {
  }
  
  int testsdfdgfh;
  
  
  
  public LocationMainPresenter(BEventBus eventBus, BBaseView view, MyProxy proxy) {
    super(eventBus, (MyView)view); 
      
  }

  
 
  
}
