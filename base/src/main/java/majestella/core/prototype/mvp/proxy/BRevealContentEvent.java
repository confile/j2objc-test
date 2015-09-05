package majestella.core.prototype.mvp.proxy;

import majestella.core.prototype.eventBus.AbstractBEvent;
import majestella.core.prototype.eventBus.BHasHandlers;
import majestella.core.prototype.mvp.BAbstractPresenter;

public class BRevealContentEvent extends AbstractBEvent<BRevealContentHandler<?>> {


  public static void fire(final BHasHandlers source,
          final Type<BRevealContentHandler<?>> type, final BAbstractPresenter<?, ?> content) {
     
  }

  private final BAbstractPresenter<?, ?> content;

  private final Type<BRevealContentHandler<?>> type;

  public BRevealContentEvent(Type<BRevealContentHandler<?>> type,
      BAbstractPresenter<?, ?> content) {
      this.type = type;
      this.content = content;
  }
  
  
  @Override
  public AbstractBEvent.Type<BRevealContentHandler<?>> getAssociatedType() {
    return type;
  }
  
  public BAbstractPresenter<?, ?> getContent() {
    return content;
  }
  
  @Override
  protected void dispatch(BRevealContentHandler<?> handler) {
    handler.onRevealContent(this);
  }

}
