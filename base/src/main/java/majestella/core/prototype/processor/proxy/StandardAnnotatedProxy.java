package majestella.core.prototype.processor.proxy;

import javax.lang.model.element.TypeElement;

/**
 * Holds the information about a proxy interface annotated with @NameToken
 * 
 * @author Dr. Michael Gorski
 *
 */
public class StandardAnnotatedProxy extends AnnotatedProxyAbstract {

 
	public StandardAnnotatedProxy(TypeElement classElement)
			throws IllegalArgumentException {
		super(classElement);
	}
 

}
