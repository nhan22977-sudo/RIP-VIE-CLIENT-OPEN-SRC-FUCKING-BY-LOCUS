package de.javagl.jgltf.model.io;

import java.util.function.Consumer;

public class JsonErrorConsumers {
   public static Consumer<JsonError> createLogging() {
      return JacksonUtils.loggingJsonErrorConsumer();
   }

   private JsonErrorConsumers() {
   }
}
