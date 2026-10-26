package de.javagl.jgltf.model;

public class Utils {
   public static float[] validate(float[] array, int length) {
      return array != null && array.length == length ? array : new float[length];
   }

   private Utils() {
   }
}
