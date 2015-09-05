package majestella.core.prototype.mvp;



/**
 * A class that can contain handlers. Handlers can be registered when
 * the object is being bound, or at any time while it is bound. They
 * will be automatically unregistered when the class is unbound.
 * <p/>
 */
public interface HandlerContainer {

  /**
   * Call this method after the object is constructed in order to bind all its
   * handlers. You should never call {@link #bind()} from the constructor
   * of a non-leaf class since it is meant to be called after the object has
   * been entirely constructed.
   */
  void bind();

  boolean isBound();

  
  /**
   * Call this method when you want to release the object and its handlers are
   * not needed anymore. You will have to call {@link #bind} again manually
   * if you ever want to reuse the object.
   */
  void unbind();
  
}
