package majestella.core.prototype.mvp.proxy;

import majestella.core.prototype.mvp.BAbstractPresenter;

/**
 * A useful mixing class to define a {@link Proxy} that is also a {@link Place}.
 * See {@link ProxyPlaceAbstract} for more details.
 *
 * @param <P> Type of the associated {@link Presenter}.
 */
public class ProxyPlaceImpl<P extends BAbstractPresenter<?, ?>> extends ProxyPlaceAbstract<P, Proxy<P>> {
}

