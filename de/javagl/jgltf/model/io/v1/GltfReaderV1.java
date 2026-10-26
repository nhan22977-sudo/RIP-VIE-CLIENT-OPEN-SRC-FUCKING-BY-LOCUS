package de.javagl.jgltf.model.io.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.model.io.JacksonUtils;
import de.javagl.jgltf.model.io.JsonError;
import de.javagl.jgltf.model.io.JsonErrorConsumers;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

public final class GltfReaderV1 {
   private Consumer<? super JsonError> jsonErrorConsumer = JsonErrorConsumers.createLogging();

   public void setJsonErrorConsumer(Consumer<? super JsonError> jsonErrorConsumer) {
      this.jsonErrorConsumer = jsonErrorConsumer;
   }

   public GlTF read(InputStream inputStream) throws IOException {
      ObjectMapper objectMapper = JacksonUtils.createObjectMapper(this.jsonErrorConsumer);
      return (GlTF)objectMapper.readValue(inputStream, GlTF.class);
   }
}
