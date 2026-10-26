package de.javagl.jgltf.model;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class Suppliers {
   public static <T> Supplier<float[]> createTransformSupplier(T object, BiConsumer<T, float[]> computer) {
      float[] transform = new float[16];
      return object == null ? () -> {
         MathUtils.setIdentity4x4(transform);
         return transform;
      } : () -> {
         computer.accept(object, transform);
         return transform;
      };
   }

   private Suppliers() {
   }
}
