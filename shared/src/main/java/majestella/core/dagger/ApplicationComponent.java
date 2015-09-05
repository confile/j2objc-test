package majestella.core.dagger;

import javax.inject.Singleton;

//import majestella.core.network.Connection;
import majestella.core.prototype.eventBus.BEventBus;
import dagger.Component;

@Component(modules = {ApplicationModule.class})
@Singleton
public interface ApplicationComponent {

  BEventBus getEventBus();
  
}
