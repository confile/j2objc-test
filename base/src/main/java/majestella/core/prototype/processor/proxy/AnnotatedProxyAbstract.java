package majestella.core.prototype.processor.proxy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public abstract class AnnotatedProxyAbstract {

  protected final static String SUFFIX = "Impl";
  
  protected Set<Element> contentSlots = new HashSet<>();
  protected final TypeElement annotatedClassElement;
  protected Set<ProxyEventWrapper> proxyEvents = new HashSet<>();
  
  
  public AnnotatedProxyAbstract(TypeElement classElement) throws IllegalArgumentException {
    this.annotatedClassElement = classElement;
  }
  
  public void addContentSlot(Element slot) {
    contentSlots.add(slot);
  }
  
  public void addProxyEventType(ProxyEventWrapper proxyType) {
    proxyEvents.add(proxyType);
  }
  
  public Set<Element> getContentSlots() {
    return contentSlots;
  }
  
  /**
   * The original element that was annotated.
   */
  public TypeElement getTypeElement() {
    return annotatedClassElement;
  }
  
  
  
  /**
   * Returns the package element of this type.
   * @return
   */
  public Element getPackageElement() {
    Element pkgElem = annotatedClassElement.getEnclosingElement();
    while (pkgElem.getKind() != ElementKind.PACKAGE) {
      pkgElem = pkgElem.getEnclosingElement();
    }
    return pkgElem;
  }
  
  
  
  /**
   * The simple name of the enclosing class.
   * @return
   */
  public String getEnclosingClassSimpleName() {
    TypeElement enclosingClass = (TypeElement) annotatedClassElement.getEnclosingElement();
    return enclosingClass.getSimpleName().toString();
  }
  
  public String getProxyImplClassSimpleName() {
    return getEnclosingClassSimpleName() + annotatedClassElement.getSimpleName() + SUFFIX;
  }
  
  
  /**
   * Returns a list of ProxyEvents used in the enclosing class.
   * @return List<ProxyEventWrapper>
   */
  public Set<ProxyEventWrapper> getProxyEvents() {
    return proxyEvents;
  }
  
  
  /**
   * For 
   * <code>
   * public interface MyProxy extends ProxyPlace&lt;StartPagePresenter&gt;{}
   * </code>
   * or
   * <code>
   * public interface MyProxy extends Proxy&lt;StartPagePresenter&gt;{}
   * </code>
   * it returns StartPagePresenter.
   * @return
   */
  public TypeElement getSuperProxyParameter() {
    List<? extends TypeMirror> proxySuperInterfaces = annotatedClassElement.getInterfaces();
    TypeMirror proxySuperInterfaceMirror = proxySuperInterfaces.get(0);
    List<? extends TypeMirror> proxySuperParameterTypes = 
        ((DeclaredType)proxySuperInterfaceMirror).getTypeArguments();
    TypeMirror proxySuperParameterMirror = proxySuperParameterTypes.get(0);
    TypeElement paramterTypeElement = (TypeElement)((DeclaredType)proxySuperParameterMirror).asElement();
    
    return paramterTypeElement;
  }
  
}
