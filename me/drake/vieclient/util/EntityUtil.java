package me.drake.vieclient.util;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.VieClientMod;
import net.minecraft.class_1296;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1588;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_3966;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public final class EntityUtil {
   private static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private EntityUtil() {
   }

   public static boolean passesFilter(class_1309 entity, EntityUtil.Filter filter, boolean friendCheck) {
      if (entity == mc.field_1724) {
         return false;
      } else if (!entity.method_5805()) {
         return false;
      } else if (friendCheck && entity instanceof class_1657 var3 && VieClientMod.getInstance().getFriendManager().isFriend(var3.method_5477().getString())) {
         return false;
      } else {
         return switch (filter) {
            case PLAYERS -> entity instanceof class_1657;
            case MOBS -> entity instanceof class_1588 || entity instanceof class_1296;
            case ALL -> true;
         };
      }
   }

   public static List getTargets(double range, boolean players, boolean mobs) {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         ArrayList var4 = new ArrayList();
         double var5 = range * range;

         for (EntityCache.CachedEntity var8 : EntityCache.getEntities()) {
            if (var8.distanceSq() > var5) {
               break;
            }

            if (var8.entity() instanceof class_1309 var9 && (!(var9 instanceof class_1657) || players) && (var9 instanceof class_1657 || mobs)) {
               var4.add(var9);
            }
         }

         return var4;
      } else {
         return List.of();
      }
   }

   public static class_1657 getClosestPlayer(double range) {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         double var2 = range * range;

         for (EntityCache.CachedEntity var5 : EntityCache.getEntities()) {
            if (var5.distanceSq() > var2) {
               break;
            }

            if (var5.entity() instanceof class_1657 var6 && var6 != mc.field_1724 && var6.method_5805()) {
               return var6;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static boolean canSeeEntity(class_1297 target) {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         class_243 var1 = mc.field_1724.method_33571();
         class_243 var2 = target.method_33571();
         class_3965 var3 = mc.field_1687.method_17742(new class_3959(var1, var2, class_3960.field_17558, class_242.field_1348, mc.field_1724));
         return var3.method_17784().method_1025(var2) < 1.0;
      } else {
         return false;
      }
   }

   public static class_1297 getCrosshairEntity() {
      return mc.field_1765 instanceof class_3966 var0 ? var0.method_17782() : null;
   }

   public enum Filter {
      PLAYERS,
      MOBS,
      ALL;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static EntityUtil.Filter[] $values() {
         EntityUtil.Filter[] var10000 = new EntityUtil.Filter[3];
         var10000[0] = PLAYERS;
         var10000[1] = MOBS;
         var10000[2] = ALL;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-121);
         var10002[4] = (byte)(117);
         var10002[5] = (byte)(77);
         var10002[6] = (byte)(102);
         PLAYERS = new EntityUtil.Filter();
         var10002 = new byte[4];
         var10002[0] = (byte)(117);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-115);
         MOBS = new EntityUtil.Filter();
         var10002 = new byte[3];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(80);
         ALL = new EntityUtil.Filter();
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }
}
