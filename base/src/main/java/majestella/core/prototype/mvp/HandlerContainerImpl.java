package majestella.core.prototype.mvp;

import java.util.List;

import javax.inject.Inject;

import majestella.core.prototype.eventBus.BHandlerRegistration;

 
/**
 * From: package com.gwtplatform.mvp.client.HandlerContainerImpl;
 * @author Dr. Michael Gorski
 *
 */
public class HandlerContainerImpl implements HandlerContainer {

  /**
   * We use this static class instead of a boolean to make the {@code bound}
   * field final. This is done in order for it to not be persisted by objectify,
   * since objectify persists field maked as {@code transient}.
   */
  private static class BindMonitor {
      public boolean value;
  }

  private final transient boolean autoBind;
  private final transient BindMonitor bound = new BindMonitor();

  private final transient List<BHandlerRegistration> handlerRegistrations = new java.util
          .ArrayList<BHandlerRegistration>();

 
  @Inject
  public HandlerContainerImpl() {
      this(true);
  }

 
  public HandlerContainerImpl(boolean autoBind) {
      super();
      this.autoBind = autoBind;
  }

  @Override
  public void bind() {
      if (!bound.value) {
          onBind();
          bound.value = true;
      }
  }

  @Override
  public final boolean isBound() {
      return bound.value;
  }

  @Override
  public void unbind() {
      if (bound.value) {
          bound.value = false;

          for (BHandlerRegistration reg : handlerRegistrations) {
              reg.removeHandler();
          }
          handlerRegistrations.clear();

          onUnbind();
      }
  }

 
  protected void onBind() {
  }

 
  protected void onUnbind() {
  }

  /**
   * Registers a handler so that it is automatically removed when
   * {@link #unbind()} is called. This provides an easy way to track event
   * handler registrations.
   *
   * @param handlerRegistration The registration of handler to track.
   */
  protected void registerHandler(BHandlerRegistration handlerRegistration) {
      handlerRegistrations.add(handlerRegistration);
  }

 

}