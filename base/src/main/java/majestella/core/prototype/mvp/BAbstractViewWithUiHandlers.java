package majestella.core.prototype.mvp;




public abstract class BAbstractViewWithUiHandlers<T extends BUiHandlers> extends AbstractBViewImpl implements BHasUiHandlers<T> {

  
  private T uiHandlers;
  

  protected T getUiHandlers() {
    return uiHandlers;
  }
  
  @Override
  public void setUiHandlers(T uiHandlers) {
    this.uiHandlers = uiHandlers;
  }

}
