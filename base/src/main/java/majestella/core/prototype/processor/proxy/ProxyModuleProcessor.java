package majestella.core.prototype.processor.proxy;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import majestella.core.prototype.annotation.ContentSlot;
import majestella.core.prototype.annotation.NameToken;
import majestella.core.prototype.annotation.ProxyEvent;
import majestella.core.prototype.annotation.ProxyStandard;
import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.mvp.BAbstractPresenter;
import majestella.core.prototype.mvp.PresenterHolder;
import majestella.core.prototype.mvp.proxy.BAsyncCallback;
import majestella.core.prototype.mvp.proxy.BNotifyingAsyncCallback;
import majestella.core.prototype.mvp.proxy.BRevealContentHandler;
import majestella.core.prototype.mvp.proxy.IndirectProvider;
import majestella.core.prototype.mvp.proxy.ProxyImpl;
import majestella.core.prototype.mvp.proxy.ProxyPlace;
import majestella.core.prototype.mvp.proxy.ProxyPlaceImpl;
import majestella.core.prototype.navigation.BPlaceImpl;
import majestella.core.prototype.navigation.BPlaceManager;
import majestella.core.prototype.navigation.BPlaceTokenRegistry;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ArrayTypeName;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import dagger.Module;
import dagger.Provides;

@AutoService(Processor.class)
public class ProxyModuleProcessor extends AbstractProcessor {

  private static final String GENERATED_CLASS_PACKAGE = "generated";
  private static final String GENERATED_PLACE_TOKEN_REGISTRY_SIMPLE_CLASS_NAME = BPlaceTokenRegistry.class.getSimpleName() + "Impl";
  private static final String GENERATED_MODULE_SIMPLE_CLASS_NAME = "ProxyModule";
  
  
  private final Class<NameToken> nameTokenClass = NameToken.class;
  private final Class<ProxyStandard> proxyStandardClass = ProxyStandard.class;
  private final Class<ProxyEvent> proxyEventClass = ProxyEvent.class;
  private final Class<ContentSlot> contentSlotClass = ContentSlot.class;
  private final Class<BAbstractPresenter> abstractPresenterClass = BAbstractPresenter.class;
  private final Class<ProxyPlace> proxyPlaceClass = ProxyPlace.class;
  private final Class<BPlaceTokenRegistry> placeTokenRegistryClass = BPlaceTokenRegistry.class;
  
  private Elements elementUtils;
  private Types typeUtils;
  private Filer filer;
  private Messager messager;
  private Set<PlaceAnnotatedProxy> placeProxies = new HashSet<PlaceAnnotatedProxy>();
  private Set<StandardAnnotatedProxy> standardProxies = new HashSet<StandardAnnotatedProxy>(); 
  
  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    elementUtils = processingEnv.getElementUtils();
    typeUtils = processingEnv.getTypeUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }
  
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotataions = new LinkedHashSet<String>();
    annotataions.add(nameTokenClass.getCanonicalName());
    annotataions.add(proxyStandardClass.getCanonicalName());
    annotataions.add(contentSlotClass.getCanonicalName());
    annotataions.add(proxyEventClass.getCanonicalName());
    return annotataions;
  }
  
  
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
  
  
  /**
   * Checks if the annotated element observes our rules
   */
  private boolean isValidProxyInterface(PlaceAnnotatedProxy item) {
    
    // Cast to TypeElement, has more type specific methods
    TypeElement interfaceElement = item.getTypeElement();
    
    // check if the enclosing class extends BAbstractPresenter
    if (interfaceElement.getEnclosingElement().getKind() != ElementKind.CLASS) {
      error(interfaceElement, "The interface %s annotated with @%s must be declared in an enclosing class", 
          interfaceElement.getQualifiedName(), nameTokenClass.getSimpleName());
      return false;
    }
    
    // we know its a class so we can cast
    TypeElement enclosingClass = (TypeElement)interfaceElement.getEnclosingElement();
    TypeMirror superClassTypeMirror = enclosingClass.getSuperclass();
    if (superClassTypeMirror.getKind() != TypeKind.DECLARED) {
      error(interfaceElement, "The interface %s annotated with @%s must be declared in an enclosing class extending a class %s", 
          interfaceElement.getQualifiedName(), nameTokenClass.getSimpleName(), abstractPresenterClass.getCanonicalName());
      return false;
    }
    
    // we know the super type is a class
    TypeElement superClassTypeElement = (TypeElement)((DeclaredType)superClassTypeMirror).asElement();
    
    
    // check if the enclosing class has the correct superclass
    if (!superClassTypeElement.getQualifiedName().toString().equals(abstractPresenterClass.getCanonicalName())) {
      error(interfaceElement, "The interface %s annotated with @%s must be declared in an enclosing class extending %s", 
          interfaceElement.getQualifiedName(), nameTokenClass.getSimpleName(), abstractPresenterClass.getCanonicalName());
      return false;
    }
    
    
    List<? extends TypeMirror> superInterfaces = interfaceElement.getInterfaces();
    
    // check if the interface has a super type interface
    if (superInterfaces.size() == 0) {
      error(interfaceElement, "The interface %s annotated with @%s must inherit from %s", 
          interfaceElement.getQualifiedName().toString(), nameTokenClass.getSimpleName(),
          proxyPlaceClass.getName());
      return false; 
    }
    else {
      // check if one of the super interfaces is ProxyPlace
      boolean foundProxyPlace = false;
      for (TypeMirror superInterface : superInterfaces) {
        if (superInterface.getKind() == TypeKind.DECLARED) {
          TypeElement superInterfaceElement = (TypeElement)((DeclaredType)superInterface).asElement();
          if (superInterfaceElement.getQualifiedName().toString().equals(proxyPlaceClass.getName().toString())) {
            foundProxyPlace = true;
            break;
          }
        }
      }     
      if (!foundProxyPlace) {
        error(interfaceElement, "The interface %s annotated with @%s does not inherit from %s", 
            interfaceElement.getQualifiedName().toString(), nameTokenClass.getSimpleName(),
            proxyPlaceClass.getName());
        return false; 
      }
    }
    
    return true;
  }
  
  
  @Override
  public boolean process(Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv) {
    
    // ---------------
    // get ProxyPlaces
    // ---------------
    
    for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(nameTokenClass)) {
    
      // Check if an interface has been annotated with @NameToken
      if (annotatedElement.getKind() != ElementKind.INTERFACE) {
        error(annotatedElement, "Only interfaces can be annotated with " + nameTokenClass.getSimpleName());
        return true;
      }
      
      // We can cast it, because we know that it of ElementKind.INTERFACE
      TypeElement typeElement = (TypeElement) annotatedElement;
      
      try {
        // throws IllegalArgumentException
        PlaceAnnotatedProxy annotatedProxy = new PlaceAnnotatedProxy(typeElement);
        
        if (!isValidProxyInterface(annotatedProxy)) {
          return true; // Error message printed, exit processing
        }
        
        placeProxies.add(annotatedProxy);
        
      } catch (IllegalArgumentException e) {
        error(typeElement, e.getMessage());
        return true;
      } catch (Exception e) {
        error(typeElement, e.getMessage());
        return true;
      }
    }
    
    // --------------------
    // get standard Proxies
    // --------------------
        
    for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(proxyStandardClass)) {
      
      // Check if an interface has been annotated with @NProxyStandard
      if (annotatedElement.getKind() != ElementKind.INTERFACE) {
        error(annotatedElement, "Only interfaces can be annotated with " + proxyStandardClass.getSimpleName());
        return true;
      }
      
      // We can cast it, because we know that it of ElementKind.INTERFACE
      TypeElement typeElement = (TypeElement) annotatedElement;
      
      try {
        // throws IllegalArgumentException
        StandardAnnotatedProxy annotatedProxy = new StandardAnnotatedProxy(typeElement);
        
        // TODO perform checks
//        if (!isValidProxyInterface(annotatedProxy)) {
//          return true; // Error message printed, exit processing
//        }
        
        standardProxies.add(annotatedProxy);
        
      } catch (IllegalArgumentException e) {
        error(typeElement, e.getMessage());
        return true;
      } catch (Exception e) {
        error(typeElement, e.getMessage());
        return true;
      }
    }   
    
    
    // ---------------
    // get ContentSlots and assign them to the corresponding Proxies
    // ---------------
        
    for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(contentSlotClass)) {
      
      // TODO check the correct syntax
      TypeElement enclosingClass = (TypeElement) annotatedElement.getEnclosingElement();
      String enclosingClassName = enclosingClass.getSimpleName().toString();
      
      // assign the contentSlot to the proxy if it matches the enclosing class
      for (PlaceAnnotatedProxy proxy : placeProxies) {
        if (proxy.getEnclosingClassSimpleName().equals(enclosingClassName)) {
          proxy.addContentSlot(annotatedElement);
          break;
        }
      }
      for (StandardAnnotatedProxy proxy : standardProxies) {
        if (proxy.getEnclosingClassSimpleName().equals(enclosingClassName)) {
          proxy.addContentSlot(annotatedElement);
          break;
        }
      }
    }
    
    
    // ---------------
    // get ProxyEvents and assign them to the corresponding Proxies 
    // ---------------
    
    for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(proxyEventClass)) {
      
      // TODO check the correct syntax
      TypeElement enclosingClass = (TypeElement) annotatedElement.getEnclosingElement();
      String enclosingClassName = enclosingClass.getSimpleName().toString();
      
      // get the ProxyEvent TypeElement
      // check if annotated element is a method
      if (annotatedElement.getKind() != ElementKind.METHOD) {
        error(annotatedElement, "The element %s annotated with @%s is not a methods", 
            annotatedElement.getSimpleName(), proxyEventClass.getSimpleName());
        return false;
      }
      // TODO check if the methods has an Event as argument
      
      // we know that it is a methods so cast to ExecutableType
      ExecutableType executableType = (ExecutableType)annotatedElement.asType();
      List<? extends TypeMirror> parameters = executableType.getParameterTypes();
      
      if (parameters.size() == 0) {
        break;
      }
      else if (parameters.size() > 1) {
        error(annotatedElement, "The element %s has more that one paramete. Only one parameter is allowed.", 
            annotatedElement.getSimpleName());
      }
       
      // get the first method parameter
      TypeMirror paramTypeMirror = parameters.get(0);
      TypeElement paramTypeElement = (TypeElement)((DeclaredType)paramTypeMirror).asElement();
      ProxyEventWrapper proxyEventWrapper = new ProxyEventWrapper(paramTypeElement);
      
      // assign the ProxyEvent to the proxy if it matches the enclosing class
      for (PlaceAnnotatedProxy proxy : placeProxies) {
        if (proxy.getEnclosingClassSimpleName().equals(enclosingClassName)) {
          proxy.addProxyEventType(proxyEventWrapper);
          break;
        }
      }      
    }    
    
    
    // ==============
    // generate files
    // ==============
    
    try {
      if ( (placeProxies.size() > 0) || (standardProxies.size() > 0) ) {
        generatePlaceTokenRegistry();
        generateProxyPlaceImpl();
        generateProxyStandardImpl();
        generateModuleCode();
      }
      placeProxies.clear(); // to make sure that files are only generated once
      standardProxies.clear(); // to make sure that files are only generated once
    } catch (IOException e) {
      error(null, e.getMessage());
    }
    
    return true;
  }
  

  /**
   * Prints an error message
   *
   * @param e
   *            The element which has caused the error. Can be null
   * @param msg
   *            The error message
   * @param args
   *            if the error message contains %s, %d etc. placeholders this
   *            arguments will be used to replace them
   */
  public void error(Element e, String msg, Object... args) {
    messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args),
        e);
  }
  
  
  /**
   * Generate an implementation of the ProxyPlace interface.
   * @throws IOException
   */
  private void generatePlaceTokenRegistry() throws IOException {
    // collect all nameTokens
    Set<String> nameTokens = new HashSet<String>();
    for (PlaceAnnotatedProxy placeAnnotatedProxy : placeProxies) {
      Set<String> tmpNameTokens = placeAnnotatedProxy.getNameTokens();
      for(String token: tmpNameTokens) {
        boolean changed = nameTokens.add(token);
        if (!changed) {
          error(placeAnnotatedProxy.getTypeElement(), "NameToken %s is used at least twice", token);
          return;
        }
      }
    }
    
    ClassName stringClass = ClassName.get(String.class);
    ClassName setClass = ClassName.get("java.util", "Set");
    TypeName setOfStrings = ParameterizedTypeName.get(setClass, stringClass);
    ClassName hashSetClass = ClassName.get(HashSet.class);
    TypeName hashSetOfStrings = ParameterizedTypeName.get(hashSetClass, stringClass);

    MethodSpec.Builder getAllPlaceTokensBuilder = MethodSpec
        .methodBuilder("getAllPlaceTokens")
        .addAnnotation(Override.class)
        .addModifiers(Modifier.PUBLIC)
        .returns(setOfStrings)
        .addStatement("$T places = new $T()", setOfStrings, hashSetOfStrings);
    
    for (String token : nameTokens) {
      getAllPlaceTokensBuilder = getAllPlaceTokensBuilder
          .addStatement("places.add($S)", token);
    }
    nameTokens.clear();

    getAllPlaceTokensBuilder = getAllPlaceTokensBuilder.addStatement("return places");
    
    TypeSpec myClassDef = TypeSpec.classBuilder(GENERATED_PLACE_TOKEN_REGISTRY_SIMPLE_CLASS_NAME)
        .addJavadoc("Generated implementation of {@link ($T)}.", placeTokenRegistryClass)
        .addModifiers(Modifier.PUBLIC)
        .addSuperinterface(placeTokenRegistryClass)
        .addMethod(getAllPlaceTokensBuilder.build())
        .build();
    
    JavaFile javaFile = JavaFile.builder(GENERATED_CLASS_PACKAGE,myClassDef).build();
//    javaFile.writeTo(System.out);
    javaFile.writeTo(filer);
  }
  
  
  /**
   * Generate the code for the implementation of the ProxyPlace interfaces.
   * @throws IOException
   */
  private void generateProxyPlaceImpl() throws IOException {
    
    for (PlaceAnnotatedProxy placeAnnotatedProxy : placeProxies) {

      ClassName proxyPlaceInterfaceName = ClassName.get(placeAnnotatedProxy.getTypeElement());
      
      ArrayTypeName stringArray = ArrayTypeName.of(String.class);
      
      String placeManagerName = "placeManager";
      String eventBusName = "eventBus";
      String callbackName = "callback";
      
      // create WrappedProxy class
      ClassName proxyImplClass = ClassName.get(ProxyImpl.class);
      ClassName proxyImplParamClass = ClassName.get(placeAnnotatedProxy.getSuperProxyParameter());
      TypeName proxyImplType = ParameterizedTypeName.get(proxyImplClass, proxyImplParamClass);
      ClassName indirectProviderClassName = ClassName.get(IndirectProvider.class);
      ClassName asyncCallbackClassName = ClassName.get(BAsyncCallback.class);
     
      TypeSpec indirectProviderType = TypeSpec.anonymousClassBuilder("")
          .addSuperinterface(ParameterizedTypeName.get(indirectProviderClassName, proxyImplParamClass))
          .addMethod(MethodSpec.methodBuilder("get")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC)
              .addParameter(ParameterizedTypeName.get(asyncCallbackClassName, proxyImplParamClass), callbackName)
              .addStatement("$T presenter = ($T)$T.getInstance().get($T.class)", 
                  proxyImplParamClass, proxyImplParamClass, PresenterHolder.class, proxyImplParamClass)
              .addStatement("presenter.bind()")
              .addStatement("$N.onSuccess(presenter)", callbackName)
              .build())         
          .build();
      
      MethodSpec.Builder wrappedClassConstructorBuilder = MethodSpec.constructorBuilder()
          .addModifiers(Modifier.PUBLIC)
          .addParameter(BPlaceManager.class, placeManagerName)
          .addParameter(BEventBus.class, eventBusName)
          .addStatement("bind($N, $N)", placeManagerName, eventBusName)
          .addStatement("presenter = $L", indirectProviderType);
      
    // append contentSlots
      wrappedClassConstructorBuilder = appendContentSlots(wrappedClassConstructorBuilder, placeAnnotatedProxy, eventBusName);
      
      MethodSpec wrappedClassConstructor = wrappedClassConstructorBuilder.build();
      
      String wrappedroxyClassName = "WrappedProxy";
      TypeSpec wrapperClassDef = TypeSpec.classBuilder(wrappedroxyClassName)
          .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
          .superclass(proxyImplType)
          .addMethod(wrappedClassConstructor)
          .build();
          
      // Constructor
      MethodSpec.Builder constructMethodBuilder = MethodSpec.constructorBuilder()
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(Inject.class)
          .addParameter(BPlaceManager.class, placeManagerName)
          .addParameter(BEventBus.class, eventBusName)
          .addStatement("$T nameTokens = new $T["+placeAnnotatedProxy.getNameTokens().size()+"]", stringArray, String.class);
      
      // add nameTokens
      int i = 0;
      for (String token : placeAnnotatedProxy.getNameTokens()) {
        constructMethodBuilder.addStatement("nameTokens["+i+"] = $S", token);
        i++;
      }
      constructMethodBuilder.addStatement("setPlace(new $T($N))", BPlaceImpl.class, "nameTokens");
      constructMethodBuilder.addStatement("bind($N, $N)", placeManagerName, eventBusName);
      constructMethodBuilder.addStatement("$N wrappedProxy = new $N($N, $N)", 
          wrappedroxyClassName, wrappedroxyClassName, placeManagerName, eventBusName);
      constructMethodBuilder.addStatement("setProxy(wrappedProxy)");

      // register event handlers for proxy events in constructor
      for (ProxyEventWrapper proxyEventWrapper : placeAnnotatedProxy.getProxyEvents()) {
        ClassName eventTypeClassName = ClassName.get(proxyEventWrapper.getTypeElement());        
        constructMethodBuilder.addStatement("getEventBus().addHandler($T.getType(), this)", eventTypeClassName);
      }

      MethodSpec constructMethod = constructMethodBuilder.build();
          
      
      ClassName proxyPlaceImplClass = ClassName.get(ProxyPlaceImpl.class);
      ClassName proxyPlaceParameterTypeClass = 
            ClassName.get(placeAnnotatedProxy.getSuperProxyParameter());
      TypeName proxyPlaceImplParamType = 
            ParameterizedTypeName.get(proxyPlaceImplClass, proxyPlaceParameterTypeClass);
      
      // create outer class
      
      TypeSpec.Builder myClassDefBuilder = TypeSpec.classBuilder(placeAnnotatedProxy.getProxyImplClassSimpleName())
          .addJavadoc("Generated implementation")
          .addModifiers(Modifier.PUBLIC)
          .superclass(proxyPlaceImplParamType)
          .addSuperinterface(proxyPlaceInterfaceName)
          .addType(wrapperClassDef)
          .addMethod(constructMethod); 
          
      // add proxy event implementations
      myClassDefBuilder = createProxyEventImpls(myClassDefBuilder, proxyImplParamClass, placeAnnotatedProxy.getProxyEvents());
      
      TypeSpec myClassDef = myClassDefBuilder.build();

      JavaFile javaFile = JavaFile.builder(placeAnnotatedProxy.getPackageElement().toString(), myClassDef).build();
//      javaFile.writeTo(System.out);
      javaFile.writeTo(filer);
    }   
  }
  
  
  private TypeSpec.Builder createProxyEventImpls(TypeSpec.Builder myClassDefBuilder, ClassName proxyImplParamClass, 
      Set<ProxyEventWrapper> proxyEventWrappers) {
    
    for (ProxyEventWrapper proxyEventWrapper : proxyEventWrappers) {
      
      ClassName eventTypeClassName = ClassName.get(proxyEventWrapper.getTypeElement());
      ClassName interfaceClassName = ClassName.get(proxyEventWrapper.getEnclosingInterface());
      
      
      // implement the event handler interface
      myClassDefBuilder.addSuperinterface(interfaceClassName);
          
      // create implementation method for proxy event handler
      
      ClassName callbackClassName = ClassName.get(BNotifyingAsyncCallback.class);      
      String presenterVarName = "presenter";
      String eventVarName = "event";
   
      TypeSpec callbackType = TypeSpec.anonymousClassBuilder("getEventBus()")
          .addSuperinterface(ParameterizedTypeName.get(callbackClassName, proxyImplParamClass))
          .addMethod(MethodSpec.methodBuilder("success")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PROTECTED)
              .addParameter(proxyImplParamClass, presenterVarName, Modifier.FINAL)
              .addStatement("$N.$N($N)", presenterVarName, 
                  proxyEventWrapper.getInterfaceMethodElement().getSimpleName().toString(),
                  eventVarName)
              .build())         
          .build();
      
      MethodSpec.Builder handlerImplMethodBuilder = MethodSpec.methodBuilder(
          proxyEventWrapper.getInterfaceMethodElement().getSimpleName().toString())
           .addAnnotation(Override.class)           
           .addModifiers(Modifier.PUBLIC) 
           .addParameter(eventTypeClassName, eventVarName, Modifier.FINAL)
           .addStatement("getPresenter($L)", callbackType);
      
      MethodSpec handlerImplMethod = handlerImplMethodBuilder.build();
      myClassDefBuilder.addMethod(handlerImplMethod);
    }
    return myClassDefBuilder;
  }
  
  
  private void generateProxyStandardImpl() throws IOException {
    for (StandardAnnotatedProxy annotatedProxy : standardProxies) {
      
      ClassName proxyPlaceInterfaceName = ClassName.get(annotatedProxy.getTypeElement());
      
      ClassName proxyImplClass = ClassName.get(ProxyImpl.class);
      ClassName proxyParameterTypeClass = 
            ClassName.get(annotatedProxy.getSuperProxyParameter());
      TypeName proxyImplParamType = 
            ParameterizedTypeName.get(proxyImplClass, proxyParameterTypeClass);
      
      String placeManagerName = "placeManager";
      String eventBusName = "eventBus";
      String callbackName = "callback";      
      
      // create WrappedProxy class
      TypeName proxyImplType = ParameterizedTypeName.get(proxyImplClass, proxyParameterTypeClass);
      ClassName indirectProviderClassName = ClassName.get(IndirectProvider.class);
      ClassName asyncCallbackClassName = ClassName.get(BAsyncCallback.class);
      
      TypeSpec indirectProviderType = TypeSpec.anonymousClassBuilder("")
          .addSuperinterface(ParameterizedTypeName.get(indirectProviderClassName, proxyParameterTypeClass))
          .addMethod(MethodSpec.methodBuilder("get")
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC)
              .addParameter(ParameterizedTypeName.get(asyncCallbackClassName, proxyParameterTypeClass), callbackName)
              .addStatement("$T presenter = ($T)$T.getInstance().get($T.class)", 
                  proxyParameterTypeClass, proxyParameterTypeClass, PresenterHolder.class, proxyParameterTypeClass)
              .addStatement("presenter.bind()")
              .addStatement("$N.onSuccess(presenter)", callbackName)
              .build())         
          .build();
      
   // Constructor
      MethodSpec.Builder constructMethodBuilder = MethodSpec.constructorBuilder()
          .addModifiers(Modifier.PUBLIC)
          .addAnnotation(Inject.class)
          .addParameter(BPlaceManager.class, placeManagerName)
          .addParameter(BEventBus.class, eventBusName)
          .addStatement("bind($N, $N)", placeManagerName, eventBusName)
          .addStatement("presenter = $L", indirectProviderType);
      
      // append contentSlots
      constructMethodBuilder = appendContentSlots(constructMethodBuilder, annotatedProxy, eventBusName);
          
      // register event handlers for proxy events in constructor
      for (ProxyEventWrapper proxyEventWrapper : annotatedProxy.getProxyEvents()) {
        ClassName eventTypeClassName = ClassName.get(proxyEventWrapper.getTypeElement());        
        constructMethodBuilder.addStatement("getEventBus().addHandler($T.getType(), this)", eventTypeClassName);
      }
      
      MethodSpec constructMethod = constructMethodBuilder.build();
      
      TypeSpec.Builder myClassDefBuilder = TypeSpec.classBuilder(annotatedProxy.getProxyImplClassSimpleName())
          .addJavadoc("Generated implementation")
          .addModifiers(Modifier.PUBLIC)
          .superclass(proxyImplParamType)
          .addSuperinterface(proxyPlaceInterfaceName)
          .addMethod(constructMethod);
          
      // add proxy event implementations
      myClassDefBuilder = createProxyEventImpls(myClassDefBuilder, proxyParameterTypeClass, annotatedProxy.getProxyEvents());
      
      TypeSpec myClassDef = myClassDefBuilder.build();
      
      JavaFile javaFile = JavaFile.builder(annotatedProxy.getPackageElement().toString(), myClassDef).build();
//      javaFile.writeTo(System.out);
      javaFile.writeTo(filer);
    }
  }
  
  
  /**
   * Append the contentSlot to the method.
   * Example: <br>
   * BRevealContentHandler<BasePresenter> revealContentHandler = new BRevealContentHandler<BasePresenter>( eventBus, this );
   * getEventBus().addHandler( BasePresenter.SLOT_CONTENT, revealContentHandler );
   * @param builder
   * @param proxy
   * @return
   */
  private MethodSpec.Builder appendContentSlots(MethodSpec.Builder builder, AnnotatedProxyAbstract proxy, String eventBusName) {
    Set<Element> contentSlots = proxy.getContentSlots();
    if (contentSlots.size() > 0) {
      ClassName revealHandlerClass = ClassName.get(BRevealContentHandler.class);
      ClassName proxyParameterTypeClass = ClassName.get(proxy.getSuperProxyParameter());
      TypeName revealHandlerType = ParameterizedTypeName.get(revealHandlerClass, proxyParameterTypeClass);
      String revealContentHandlerStringName = "revealContentHandler";
      
      builder.addStatement("$T $N = new $T($N, this)", revealHandlerType, revealContentHandlerStringName, 
          revealHandlerType, eventBusName);

      for (Element slot : contentSlots) {
        builder.addStatement("getEventBus().addHandler($T.$L, $N)", proxyParameterTypeClass, slot.getSimpleName().toString(), 
            revealContentHandlerStringName);
      }     
    }   
    return builder;
  }

 
  /**
   * Generate the ProxyModule for Dagger2.
   * @throws IOException
   */
  private void generateModuleCode() throws IOException {
    
    String methodPrefix = "provide";
    
    TypeSpec.Builder myClassBuilder = TypeSpec.classBuilder(GENERATED_MODULE_SIMPLE_CLASS_NAME)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Module.class)
        .addJavadoc("Generated ProxyModule.");
    
    for (PlaceAnnotatedProxy placeAnnotatedProxy : placeProxies) {
    
      ClassName proxyPlaceInterfaceName = ClassName.get(placeAnnotatedProxy.getTypeElement());
      ClassName proxyPlaceImplName = ClassName.get(placeAnnotatedProxy.getPackageElement().toString(), 
            placeAnnotatedProxy.getProxyImplClassSimpleName());
      
      String placeManagerName = "placeManager";
      String eventBusName = "eventBus";
      
      MethodSpec providesMethod = MethodSpec.methodBuilder(methodPrefix + placeAnnotatedProxy.getProxyImplClassSimpleName())
        .addAnnotation(Provides.class)
        .addAnnotation(Singleton.class)
        .addParameter(BPlaceManager.class, placeManagerName)
        .addParameter(BEventBus.class, eventBusName)
        .returns(proxyPlaceInterfaceName)
        .addStatement("return new $T($N, $N)", proxyPlaceImplName, placeManagerName, eventBusName)
        .build();
      
      myClassBuilder.addMethod(providesMethod);
    }
    
    
    for (StandardAnnotatedProxy annotatedProxy : standardProxies) {
      ClassName proxyPlaceInterfaceName = ClassName.get(annotatedProxy.getTypeElement());
      ClassName proxyPlaceImplName = ClassName.get(annotatedProxy.getPackageElement().toString(), 
          annotatedProxy.getProxyImplClassSimpleName());
      
      String placeManagerName = "placeManager";
      String eventBusName = "eventBus";
      
      MethodSpec providesMethod = MethodSpec.methodBuilder(methodPrefix + annotatedProxy.getProxyImplClassSimpleName())
        .addAnnotation(Provides.class)
        .addAnnotation(Singleton.class)
        .addParameter(BPlaceManager.class, placeManagerName)
        .addParameter(BEventBus.class, eventBusName)
        .returns(proxyPlaceInterfaceName)
        .addStatement("return new $T($N, $N)", proxyPlaceImplName, placeManagerName, eventBusName)
        .build();
      
      myClassBuilder.addMethod(providesMethod);
    }

    
    // add PlaceTokenRegistry to module
    
    ClassName registryClassImplName = ClassName.get(GENERATED_CLASS_PACKAGE, GENERATED_PLACE_TOKEN_REGISTRY_SIMPLE_CLASS_NAME);
    
    MethodSpec providesRegistryMethod = MethodSpec.methodBuilder(methodPrefix + GENERATED_PLACE_TOKEN_REGISTRY_SIMPLE_CLASS_NAME)
        .addAnnotation(Provides.class)
        .addAnnotation(Singleton.class)
        .returns(placeTokenRegistryClass)
        .addStatement("return new $T()", registryClassImplName)
        .build();
    myClassBuilder.addMethod(providesRegistryMethod);
        
    JavaFile javaFile = JavaFile.builder(GENERATED_CLASS_PACKAGE, myClassBuilder.build()).build();
        
    
//    javaFile.writeTo(System.out);
    javaFile.writeTo(filer);
  }

}
