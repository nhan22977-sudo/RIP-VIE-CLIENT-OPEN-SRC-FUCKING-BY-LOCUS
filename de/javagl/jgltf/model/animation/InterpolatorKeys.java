package de.javagl.jgltf.model.animation;

import java.util.Arrays;

class InterpolatorKeys {
   static int computeIndex(float key, float[] keys) {
      int index = Arrays.binarySearch(keys, key);
      return index >= 0 ? index : Math.max(0, -index - 2);
   }

   static float computeAlpha(float key, float[] keys, int index) {
      if (key <= keys[0]) {
         return 0.0F;
      }

      if (key >= keys[keys.length - 1]) {
         return 1.0F;
      }

      float local = key - keys[index];
      float delta = keys[index + 1] - keys[index];
      return local / delta;
   }

   public static void main(String[] args) {
      float[] keys = new float[]{1.0F, 8.0F, 11.0F};

      for (float d = -1.0F; d <= 12.0F; d = (float)(d + 0.1)) {
         int index = computeIndex(d, keys);
         float alpha = computeAlpha(d, keys, index);
         System.out.println("For " + d);
         System.out.println("    index " + index);
         System.out.println("    alpha " + alpha);
      }
   }
}
