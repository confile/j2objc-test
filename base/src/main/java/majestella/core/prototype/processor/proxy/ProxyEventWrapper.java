package majestella.core.prototype.processor.proxy;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public class ProxyEventWrapper {

  private TypeElement paramTypeElement;
  
  public ProxyEventWrapper(TypeElement paramTypeElement) {
    this.paramTypeElement = paramTypeElement;
  }
  
  
  public TypeElement getTypeElement() {
    return paramTypeElement;
  }
  
  /**
   * Returns the enclosing interface of this Event class.
   * @return
   */
  public TypeElement getEnclosingInterface() {
    TypeElement interfaceTypeElement = null;
     
    for (Element element : paramTypeElement.getEnclosedElements()) {    
      if (element.getKind() == ElementKind.INTERFACE) {
        interfaceTypeElement = (TypeElement)element;
      }
    }
    return interfaceTypeElement;
  }
  
  
  public Element getInterfaceMethodElement() {
    Element methodElement = null;
    TypeElement interfaceTypeElement = getEnclosingInterface();
    
    // TODO check if there is exactly one method in this interface
    // TODO check if the method has paramTypeElement as only parameter
    
    methodElement = interfaceTypeElement.getEnclosedElements().get(0);
    
    return methodElement;
  }
  
  
}
