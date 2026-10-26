package me.drake.vieclient.modules.render;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;
import net.minecraft.class_243;

public final class SkyEffectGeometry {
   private static final int MIN_STARS = 200;
   private static final int MAX_STARS = 3000;
   private static final Map STAR_CACHE = new ConcurrentHashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private SkyEffectGeometry() {
   }

   public static class_243 relative(class_243 camera, class_243 unit, double radius) {
      return unit.method_1027() == 0.0 ? camera : camera.method_1019(unit.method_1029().method_1021(radius));
   }

   public static int clampStarCount(int count) {
      return Math.max(200, Math.min(3000, count));
   }

   public static List stars(long seed, int count) {
      SkyEffectGeometry.StarKey var3 = new SkyEffectGeometry.StarKey(seed, clampStarCount(count));
      return STAR_CACHE.computeIfAbsent(var3, SkyEffectGeometry::createStars);
   }

   private static List createStars(SkyEffectGeometry.StarKey key) {
      Random var1 = new Random(key.seed());
      return IntStream.range(0, key.count())
         .mapToObj(
            ignored -> {
               double var2 = var1.nextDouble() * (Math.PI * 2);
               double var4 = 0.04 + var1.nextDouble() * 0.96;
               double var6 = Math.sqrt(1.0 - var4 * var4);
               return new SkyEffectGeometry.Star(
                  new class_243(Math.cos(var2) * var6, var4, Math.sin(var2) * var6), 0.35F + var1.nextFloat() * 0.9F, var1.nextDouble() * (Math.PI * 2)
               );
            }
         )
         .toList();
   }

   public static SkyEffectGeometry.Meteor newMeteor(long seed, long startMillis) {
      Random var4 = new Random(seed);
      double var5 = var4.nextDouble() * (Math.PI * 2);
      double var7 = 0.25 + var4.nextDouble() * 0.55;
      class_243 var9 = direction(var5, var7);
      class_243 var10 = direction(var5 - 0.18 - var4.nextDouble() * 0.15, Math.max(0.08, var7 - 0.12 - var4.nextDouble() * 0.14));
      return new SkyEffectGeometry.Meteor(startMillis, 1000L, var9, var10);
   }

   public static class_243 direction(double azimuth, double elevation) {
      double var4 = Math.cos(elevation);
      return new class_243(Math.cos(azimuth) * var4, Math.sin(elevation), Math.sin(azimuth) * var4).method_1029();
   }

   public record Meteor(long startMillis, long durationMillis, class_243 headDirection, class_243 tailDirection) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public boolean isActiveAt(long nowMillis) {
         return nowMillis >= this.startMillis && nowMillis < this.startMillis + this.durationMillis;
      }
   }

   public record Star(class_243 direction, float size, double phase) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record StarKey(long seed, int count) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
