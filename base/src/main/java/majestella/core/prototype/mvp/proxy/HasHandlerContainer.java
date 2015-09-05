package majestella.core.prototype.mvp.proxy;

import majestella.core.prototype.mvp.HandlerContainer;

 
/**
 * This class is used on class that can register handlers. Currently used by
 * ProxyImpl to remove any registered handler when we unbind a proxy.
 */
public interface HasHandlerContainer {
    /**
     * @return the handler container of the implementer.
     */
    HandlerContainer getHandlerContainer();
}
