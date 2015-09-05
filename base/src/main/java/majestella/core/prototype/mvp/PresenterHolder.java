package majestella.core.prototype.mvp;

import java.util.HashMap;
import java.util.Map;

public class PresenterHolder {

  private static PresenterHolder holder;
  
  @SuppressWarnings("rawtypes")
  private Map<Class, BAbstractPresenterWidget> presenteryMap = new HashMap<>();
  
  private PresenterHolder() {
  }
  
  public static PresenterHolder getInstance() {
    if (holder == null) {
      holder = new PresenterHolder();
    }
    return holder;
  }
  
  @SuppressWarnings("rawtypes")
  public BAbstractPresenterWidget get(Class clazz) {
    BAbstractPresenterWidget presenter = presenteryMap.get(clazz);
    if (presenter == null) {
      throw new NullPointerException("PresenterHolder - BAbstractPresenter with Class "+clazz+" is not available!");
    }
    return presenter;
  }
  
  @SuppressWarnings("rawtypes")
  public void add(Class clazz, BAbstractPresenterWidget presenter) {
    presenteryMap.put(clazz, presenter);
  }
  
}
