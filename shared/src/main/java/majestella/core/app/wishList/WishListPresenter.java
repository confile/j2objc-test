package majestella.core.app.wishList;

 
import majestella.core.prototype.annotation.NameToken;
import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.mvp.BAbstractPresenter;
import majestella.core.prototype.mvp.BBaseView;
import majestella.core.prototype.mvp.proxy.ProxyPlace;
 
public class WishListPresenter extends BAbstractPresenter<WishListPresenter.MyView, WishListPresenter.MyProxy> {

  public interface MyView extends BBaseView {
  }
  
  @NameToken("wishList")
  public interface MyProxy extends ProxyPlace<WishListPresenter> {
  }
  

  int t34;

  public WishListPresenter( BEventBus eventBus, BBaseView view,  MyProxy proxy) {
    super(eventBus, (MyView)view); 
    
     
  }
   
  

 
  
}
