package com.zigythebird.playeranim;

import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Stream;

public interface PlayerAnimLibService {
   PlayerAnimLibService INSTANCE = loadServices(PlayerAnimLibService.class).findAny().orElseThrow();

   boolean isModLoaded(String var1);

   static <T> Stream<T> loadServices(Class<T> serviceClass) {
      ModuleLayer layer = serviceClass.getModule().getLayer();
      ServiceLoader<T> loader = layer == null ? ServiceLoader.load(serviceClass, serviceClass.getClassLoader()) : ServiceLoader.load(layer, serviceClass);
      return loader.stream().map(Provider::get);
   }
}
