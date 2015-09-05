package majestella.core.prototype.mvp.proxy;

import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.eventBus.BEventHandler;
import majestella.core.prototype.mvp.BAbstractPresenter;

 

/**
 * This is the handler class for BRevealContentEvent. It should be used
 * by any  Proxy class of a BAbstractPresenter that accepts child
 * presenters. When this handler is triggered, the proxy should <b>first</b> set
 * the content appropriately in the presenter, and then reveal the presenter.
 *
 * @param <T> The Presenter's type.
 */
public class BRevealContentHandler<T extends BAbstractPresenter<?, ?>> implements BEventHandler {

  private final BEventBus eventBus;
  private final ProxyImpl<T> proxy;

  public BRevealContentHandler(final BEventBus eventBus,
          final ProxyImpl<T> proxy) {
      this.eventBus = eventBus;
      this.proxy = proxy;
  }

  /**
   * This is the dispatched method. Reveals
   *
   * @param revealContentEvent The event containing the presenter that wants to
   *                           bet set as content.
   */
  public final void onRevealContent(final BRevealContentEvent revealContentEvent) {
      proxy.getPresenter(new BNotifyingAsyncCallback<T>(eventBus) {

          @Override
          public void success(final T presenter) {

          }
      });
  }
  
  
}
