package majestella.core.prototype.mvp.proxy;

public interface IndirectProvider<T> {
  /**
   * Asynchronously get the provided object.
   *
   * @param callback The {@link BAsyncCallback} to invoke once the object is
   *                 available. The parameter to the callback will be the provided
   *                 object.
   */
  void get(BAsyncCallback<T> callback);
}
