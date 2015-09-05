package majestella.core.prototype.mvp.proxy;

import majestella.core.prototype.mvp.BAbstractPresenter;
import majestella.core.prototype.navigation.BPlace;


/**
 * The interface of a {@link Proxy} that is also a {@link Place}.
 *
 * @param <P> The type of the {@link Presenter} associated with this proxy.
 */
public interface ProxyPlace<P extends BAbstractPresenter<?, ?>> extends Proxy<P>, BPlace {

    /**
     * Manually reveals a presenter. Only use this method if your presenter is configured
     * to use manual reveal via {@link Presenter#useManualReveal()}. This method should be
     * called following one or more asynchronous server calls in
     * {@link Presenter#prepareFromRequest(com.gwtplatform.mvp.shared.proxy.PlaceRequest)}.
     * You should manually reveal your presenter exactly once, when all the data needed to use it is available.
     * <p/>
     * If you failed to fetch the data or cannot reveal the presenter you must call
     * {@link #manualRevealFailed()} otherwise navigation will be blocked and your application
     * will appear to be frozen.
     * <p/>
     * Also consider using {@link ManualRevealCallback}, which will automatically call
     * {@link #manualReveal(Presenter)} upon success and {@link #manualRevealFailed()} upon
     * failure.
     *
     * @param presenter The presenter that will be delayed revealed.
     * @see Presenter#useManualReveal()
     * @see #manualRevealFailed()
     */
    void manualReveal(BAbstractPresenter<?, ?> presenter);

    /**
     * Cancels manually revealing a presenter. Only use this method if your presenter is configured
     * to use manual reveal via {@link Presenter#useManualReveal()}. For more details see
     * {@link #manualReveal(Presenter)}.
     *
     * @see #manualReveal(Presenter)
     */
    void manualRevealFailed();
}