package majestella.core.prototype.mvp.proxy;

import javax.inject.Inject;

import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.mvp.BAbstractPresenter;
import majestella.core.prototype.mvp.HandlerContainer;
import majestella.core.prototype.navigation.BPlace;
import majestella.core.prototype.navigation.BPlaceManager;



public class ProxyPlaceAbstract<P extends BAbstractPresenter<?, ?>, Proxy_ extends Proxy<P>> implements ProxyPlace<P>,
        HasHandlerContainer {
   
    
//    private final ProxyHandlerContainer handlerContainer = new ProxyHandlerContainer();

    private BPlace place;
    private BPlaceManager placeManager;
    private Proxy_ proxy; 
    private BEventBus eventBus;

    public ProxyPlaceAbstract() {
    }

  
    @Override
    public final boolean equals(Object o) {
        return place.equals(o);
    }

 

    @Override
    public final BEventBus getEventBus() {
        return eventBus;
    }

  

    @Override
    public void getPresenter(BNotifyingAsyncCallback<P> callback) {
        proxy.getPresenter(callback);
    }



    @Override
    public final int hashCode() {
        return place.hashCode();
    }

   

    @Override
    public HandlerContainer getHandlerContainer() {
        return null;
    }

   
    @Override
    public final String toString() {
        return place.toString();
    }


    protected BPlace getPlace() {
        return place;
    }

    protected void setPlace(BPlace place) {
        this.place = place;
    }

    protected BPlaceManager getPlaceManager() {
        return placeManager;
    }

    protected void setPlaceManager(BPlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    protected Proxy_ getProxy() {
        return proxy;
    }

    protected void setProxy(Proxy_ proxy) {
        this.proxy = proxy;
    }


    @Inject
    protected void bind(final BPlaceManager placeManager, BEventBus eventBus) {
        this.placeManager = placeManager;
        this.eventBus = eventBus;


    }

 
 
    @Override
    public void manualReveal(BAbstractPresenter<?, ?> presenter) {

    }

    @Override
    public void manualRevealFailed() {
    
    }


}
