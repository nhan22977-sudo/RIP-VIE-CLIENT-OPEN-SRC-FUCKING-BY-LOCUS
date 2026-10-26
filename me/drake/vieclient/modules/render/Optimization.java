package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_2338;
import net.minecraft.class_2394;
import net.minecraft.class_2396;
import net.minecraft.class_2398;
import net.minecraft.class_2591;
import net.minecraft.class_310;

public class Optimization extends Module {
   public final BoolSetting weather = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Weather",
         "Disable weather precipitation rendering and audio",
         true
      )
   );
   public final BoolSetting shadows = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Entity Shadows",
         "Disable circular entity drop shadows",
         true
      )
   );
   public final BoolSetting heavyParticles = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Heavy Particles",
         "Cull explosion, smoke and decorative particles while keeping PVP hit indicators",
         true
      )
   );
   public final BoolSetting blockEntities = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Block Entity Culling",
         "Distance-cull distant chests, banners and decorative block entities",
         true
      )
   );
   public final IntSetting cullDistance = (IntSetting)this.addSetting(
      new IntSetting(
         "Cull Distance",
         "Max render distance for block entities (blocks)",
         48,
         16,
         96,
         4
      )
   );
   public final BoolSetting animatedTextures = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Animated Textures",
         "Freeze animated water, lava and portal texture VRAM re-uploads",
         false
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Optimization() {
      super(
         "Optimization",
         "Scientific client performance optimizations and visual overhead culling",
         Category.RENDER
      );
      this.cullDistance.visibleWhen(this.blockEntities::get);
   }

   private static Optimization get() {
      VieClientMod var0 = VieClientMod.getInstance();
      if (var0 != null && var0.getModuleManager() != null) {
         Optimization var1 = (Optimization)var0.getModuleManager().getModule(Optimization.class);
         return var1 != null && var1.isEnabled() ? var1 : null;
      } else {
         return null;
      }
   }

   public static boolean shouldDisableWeather() {
      Optimization var0 = get();
      return var0 != null && (Boolean)var0.weather.get();
   }

   public static boolean shouldDisableShadows() {
      Optimization var0 = get();
      return var0 != null && (Boolean)var0.shadows.get();
   }

   public static boolean shouldFreezeAnimatedTextures() {
      Optimization var0 = get();
      return var0 != null && (Boolean)var0.animatedTextures.get();
   }

   public static boolean shouldCullParticle(class_2394 effect, double x, double y, double z) {
      Optimization var7 = get();
      if (var7 == null) {
         return false;
      }

      class_310 var8 = class_310.method_1551();
      if (var8.field_1724 != null) {
         double var9 = var8.field_1724.method_5649(x, y, z);
         if (var9 > 784.0) {
            if (isCriticalCombatParticle(effect)) {
               return false;
            }

            return true;
         }
      }

      return var7.heavyParticles.get() ? isHeavyParticle(effect) : false;
   }

   private static boolean isCriticalCombatParticle(class_2394 effect) {
      if (effect == null) {
         return false;
      }

      class_2396 var1 = effect.method_10295();
      return var1 == class_2398.field_11205
         || var1 == class_2398.field_11208
         || var1 == class_2398.field_11227
         || var1 == class_2398.field_11220
         || var1 == class_2398.field_11209;
   }

   private static boolean isHeavyParticle(class_2394 effect) {
      if (effect == null) {
         return false;
      }

      class_2396 var1 = effect.method_10295();
      return var1 == class_2398.field_17430
         || var1 == class_2398.field_17431
         || var1 == class_2398.field_11251
         || var1 == class_2398.field_11237
         || var1 == class_2398.field_11236
         || var1 == class_2398.field_11221
         || var1 == class_2398.field_11248
         || var1 == class_2398.field_11233
         || var1 == class_2398.field_28478
         || var1 == class_2398.field_11216
         || var1 == class_2398.field_11214
         || var1 == class_2398.field_11250
         || var1 == class_2398.field_11247
         || var1 == class_2398.field_11241
         || var1 == class_2398.field_11238
         || var1 == class_2398.field_11202
         || var1 == class_2398.field_11242
         || var1 == class_2398.field_18306
         || var1 == class_2398.field_18304
         || var1 == class_2398.field_46911
         || var1 == class_2398.field_49140
         || var1 == class_2398.field_49139;
   }

   public static boolean shouldCullBlockEntity(class_2338 pos, class_2591 type) {
      Optimization var2 = get();
      if (var2 == null || !(Boolean)var2.blockEntities.get() || pos == null) {
         return false;
      }

      if (type != class_2591.field_11889 && type != class_2591.field_47352) {
         class_310 var3 = class_310.method_1551();
         if (var3.field_1724 == null) {
            return false;
         }

         double var4 = ((Integer)var2.cullDistance.get()).intValue();
         double var6 = var3.field_1724.method_5649(pos.method_10263() + 0.5, pos.method_10264() + 0.5, pos.method_10260() + 0.5);
         return var6 > var4 * var4;
      } else {
         return false;
      }
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }
}
