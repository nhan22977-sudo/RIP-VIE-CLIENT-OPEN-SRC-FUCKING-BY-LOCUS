package me.drake.vieclient.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_4604;
import org.joml.Matrix4f;

public final class EntityCache {
   private static final double MAX_TRACK_RANGE = 192.0;
   private static final double MAX_TRACK_RANGE_SQ = 36864.0;
   private static volatile boolean active = false;
   private static final Int2ObjectOpenHashMap cache = new Int2ObjectOpenHashMap();
   private static volatile List snapshot = Collections.emptyList();
   private static int lastUpdateTick = -1;
   private static final IntOpenHashSet staleIdsScratch = new IntOpenHashSet();
   private static final Comparator DISTANCE_COMPARATOR = (a, b) -> Double.compare(a.distanceSq, b.distanceSq);
   private static final ArrayList sortScratch = new ArrayList();
   private static class_4604 frustum;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private EntityCache() {
   }

   public static void setActive(boolean enabled) {
      boolean var1 = active;
      active = enabled;
      if (var1 && !enabled) {
         clear();
         frustum = null;
      }
   }

   public static boolean isActive() {
      return active;
   }

   public static void updateFrustum(Matrix4f viewMatrix, Matrix4f projectionMatrix, class_4184 camera) {
      if (active && camera != null) {
         frustum = new class_4604(viewMatrix, projectionMatrix);
         class_243 var3 = camera.method_71156();
         frustum.method_23088(var3.field_1352, var3.field_1351, var3.field_1350);
      } else {
         frustum = null;
      }
   }

   public static boolean isVisible(class_238 box) {
      return frustum == null || frustum.method_23093(box);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static void update() {
      if (active) {
         class_310 var0 = class_310.method_1551();
         if (var0.field_1724 != null && var0.field_1687 != null) {
            int var1 = var0.field_1724.field_6012;

            byte var2 = switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$util$PerformanceHelper$Tier[PerformanceHelper.tier().ordinal()]) {
               case 1, 2 -> 1;
               case 3 -> 2;
               case 4 -> 3;
               case 5 -> 4;
               default -> throw new MatchException(null, null);
            };
            if (lastUpdateTick < 0 || var1 - lastUpdateTick >= var2) {
               lastUpdateTick = var1;
               double var3 = var0.field_1724.method_23317();
               double var5 = var0.field_1724.method_23318();
               double var7 = var0.field_1724.method_23321();
               staleIdsScratch.clear();
               staleIdsScratch.addAll(cache.keySet());

               for (class_1297 var10 : var0.field_1687.method_18112()) {
                  if (var10 != var0.field_1724 && var10 instanceof class_1309 var11 && var11.method_5805()) {
                     int var12 = var10.method_5628();
                     double var13 = var10.method_23317();
                     double var15 = var10.method_23318();
                     double var17 = var10.method_23321();
                     double var19 = var13 - var3;
                     double var21 = var15 - var5;
                     double var23 = var17 - var7;
                     double var25 = var19 * var19 + var21 * var21 + var23 * var23;
                     if (!(var25 > 36864.0)) {
                        staleIdsScratch.remove(var12);
                        EntityCache.CachedEntity var27 = (EntityCache.CachedEntity)cache.get(var12);
                        if (var27 == null || var27.lastAge != var10.field_6012) {
                           double var28 = var25;
                           cache.put(var12, new EntityCache.CachedEntity(var12, var10, var10.method_5829(), var28, var10.field_6012));
                        }
                     }
                  }
               }

               IntIterator var30 = staleIdsScratch.iterator();

               while (var30.hasNext()) {
                  int var31 = (Integer)var30.next();
                  cache.remove(var31);
               }

               if (cache.isEmpty()) {
                  snapshot = Collections.emptyList();
               } else if (cache.size() == 1) {
                  snapshot = Collections.singletonList((EntityCache.CachedEntity)cache.values().iterator().next());
               } else {
                  sortScratch.clear();
                  sortScratch.addAll(cache.values());
                  sortScratch.sort(DISTANCE_COMPARATOR);
                  snapshot = List.copyOf(sortScratch);
               }
            }
         } else {
            cache.clear();
            snapshot = Collections.emptyList();
         }
      }
   }

   public static List getEntities() {
      return !active ? Collections.emptyList() : snapshot;
   }

   public static List getEntitiesInRange(double range, boolean cullFrustum) {
      if (!active) {
         return Collections.emptyList();
      }

      double var3 = range * range;
      ArrayList var5 = new ArrayList();

      for (EntityCache.CachedEntity var7 : snapshot) {
         if (var7.distanceSq > var3) {
            break;
         }

         if (!cullFrustum || isVisible(var7.boundingBox)) {
            var5.add(var7);
         }
      }

      return var5;
   }

   public static EntityCache.CachedEntity getById(int entityId) {
      return (EntityCache.CachedEntity)cache.get(entityId);
   }

   public static void clear() {
      cache.clear();
      snapshot = Collections.emptyList();
      lastUpdateTick = -1;
   }

   public static float fastSin(float x) {
      return class_3532.method_15374(x);
   }

   public static float fastCos(float x) {
      return class_3532.method_15362(x);
   }

   public record CachedEntity(int id, class_1297 entity, class_238 boundingBox, double distanceSq, int lastAge) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
