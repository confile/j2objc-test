package majestella.core.prototype.processor.proxy;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.TypeElement;

import majestella.core.prototype.annotation.NameToken;

/**
 * Holds the information about a proxy interface annotated with @NameToken
 * 
 * @author Dr. Michael Gorski
 *
 */
public class PlaceAnnotatedProxy extends AnnotatedProxyAbstract {

 
	private final Set<String> nameTokens = new HashSet<String>();

	public PlaceAnnotatedProxy(TypeElement classElement)
			throws IllegalArgumentException {
	  super(classElement);
		NameToken annotation = classElement.getAnnotation(NameToken.class);
		String[] values = annotation.value();

		if (values.length == 0) {
			throw new IllegalArgumentException(
					String.format(
							"value in @%s for class %s is null or empty! that's not allowed",
							NameToken.class.getSimpleName(), classElement
									.getQualifiedName().toString()));
		}

		for (int i = 0; i < values.length; i++) {
			boolean changed = nameTokens.add(values[i]);
			if (!changed) {
				throw new IllegalArgumentException(
						String.format(
								"value %s in @%s for class %s is a duplicate! that's not allowed",
								values[i], NameToken.class.getSimpleName(),
								classElement.getQualifiedName().toString()));
			}
		}
	}

	public Set<String> getNameTokens() {
		return nameTokens;
	}

 
 

}
