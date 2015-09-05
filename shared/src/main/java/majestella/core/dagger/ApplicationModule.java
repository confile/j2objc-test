package majestella.core.dagger;

import javax.inject.Singleton;

import majestella.core.prototype.eventBus.BEventBus;
import majestella.core.prototype.eventBus.BSimpleEventBus;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

  @Provides @Singleton
  BEventBus provideBEventBus() {
    return new BSimpleEventBus();
  }
  
  
}
