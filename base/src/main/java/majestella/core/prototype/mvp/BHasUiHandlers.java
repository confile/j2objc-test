package majestella.core.prototype.mvp;


public interface BHasUiHandlers<T extends BUiHandlers> {
  
  void setUiHandlers(T uiHandlers);
  
}