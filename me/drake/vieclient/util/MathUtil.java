package me.drake.vieclient.util;

import java.util.concurrent.ThreadLocalRandom;

public final class MathUtil {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private MathUtil() {
   }

   public static int randomInt(int min, int max) {
      return max < min ? min : ThreadLocalRandom.current().nextInt(min, max + 1);
   }

   public static double randomDouble(double min, double max) {
      return max < min ? min : min + ThreadLocalRandom.current().nextDouble(max - min);
   }

   public static long randomLong(long min, long max) {
      return max < min ? min : min + (long)(ThreadLocalRandom.current().nextDouble() * (max - min + 1L));
   }
}
