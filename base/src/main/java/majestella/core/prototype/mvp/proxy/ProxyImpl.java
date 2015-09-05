package majestella.core.prototype.mvp.proxy;

import javax.inject.Inject;

import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.mvp.BAbstractPresenter;
import majestella.core.prototype.navigation.BPlaceManager;



public class ProxyImpl<P extends BAbstractPresenter<?, ?>> implements Proxy<P> {

    protected IndirectProvider<P> presenter;
    protected BEventBus eventBus;
    
    public ProxyImpl() {
    }

    @Override
    public void getPresenter(BNotifyingAsyncCallback<P> callback) {
        callback.prepare();
        presenter.get(callback);
        callback.checkLoading();
    }


    @Inject
    protected void bind(final BPlaceManager placeManager, BEventBus eventBus) {
        this.eventBus = eventBus;
    }



    @Override
    public final BEventBus getEventBus() {
        return eventBus;
    }
}
