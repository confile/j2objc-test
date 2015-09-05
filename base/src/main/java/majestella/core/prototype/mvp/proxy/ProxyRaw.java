package majestella.core.prototype.mvp.proxy;

import majestella.core.prototype.eventBus.BHasHandlers;
import majestella.core.prototype.mvp.BAbstractPresenter;

/**
 * This is the unparameterized base interface for proxy. It is provided as a
 * work around since GIN/Guice cannot inject parameterized types. For most
 * purposes you should use {@link Proxy}.
 */
public interface ProxyRaw extends BHasHandlers {

    /**
     * Get the associated {@link Presenter}. The presenter can only be obtained in
     * an asynchronous manner to support code splitting when needed. To access the
     * presenter, pass a callback.
     * <p/>
     * The difference between this method and
     * {@link Proxy#getPresenter}
     * is that the latter one gets the specific parameterised {@link Presenter}
     * type.
     *
     * @param callback The callback in which the {@link Presenter} will be passed
     *                 as a parameter.
     */
    // TODO unused
//    void getRawPresenter(BNotifyingAsyncCallback<BAbstractPresenter<?, ?>> callback);

}