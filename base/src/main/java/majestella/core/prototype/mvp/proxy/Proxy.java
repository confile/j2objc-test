package majestella.core.prototype.mvp.proxy;

import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.mvp.BAbstractPresenter;


/**
 * The interface for light-weight singleton classes that listens for events
 * before the full {@link BPresenter} is instantiated. This include, among
 * others, the presenter's specific {@link RevealContentEvent} that needs the
 * presenter to reveal itself.
 * <p/>
 * The relationship between a presenter and its proxy is two-way.
 * <p/>
 * {@link BPresenter} subclasses will usually define their own interface called
 * MyProxy and be derived from this one.
 *
 * @param <P> The type of the {@link Presenter} associated with this proxy.
 */
public interface Proxy<P extends BAbstractPresenter<?, ?>> extends ProxyRaw {

    /**
     * Makes it possible to access the {@link BEventBus} object associated with
     * that proxy.
     *
     * @return The {@link BEventBus} associated with that proxy.
     */
    BEventBus getEventBus();

    /**
     * Get the associated {@link BPresenter}. The presenter can only be obtained in
     * an asynchronous manner to support code splitting when needed. To access the
     * presenter, pass a callback.
     *
     * @param callback The callback in which the {@link BPresenter} will be passed
     *                 as a parameter.
     */
    void getPresenter(BNotifyingAsyncCallback<P> callback);
}
