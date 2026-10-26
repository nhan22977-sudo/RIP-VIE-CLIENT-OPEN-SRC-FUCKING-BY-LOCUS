package de.javagl.jgltf.model.structure;

public class BufferBuilderStrategies {
   public static BufferBuilderStrategy createDefault() {
      DefaultBufferBuilderStrategy.Config config = new DefaultBufferBuilderStrategy.Config();
      return new DefaultBufferBuilderStrategy(config);
   }

   static BufferBuilderStrategy create(DefaultBufferBuilderStrategy.Config config) {
      return new DefaultBufferBuilderStrategy(config);
   }

   private BufferBuilderStrategies() {
   }
}
