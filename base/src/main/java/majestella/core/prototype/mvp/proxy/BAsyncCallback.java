package majestella.core.prototype.mvp.proxy;

public interface BAsyncCallback<T> {

  void onSuccess(T result);
  
  void onFailure(Exception caught);
  
}
