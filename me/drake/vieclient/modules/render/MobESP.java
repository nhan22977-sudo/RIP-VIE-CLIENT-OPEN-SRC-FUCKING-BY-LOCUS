package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.util.EntityCache;
import me.drake.vieclient.util.PerformanceHelper;
import me.drake.vieclient.util.RainbowUtil;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.util.render.EspFade;
import net.minecraft.class_1296;
import net.minecraft.class_1308;
import net.minecraft.class_1588;
import net.minecraft.class_238;
import net.minecraft.class_3532;

public class MobESP extends Module {
   private final EnumSetting filter = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Filter",
         "Which mobs to show",
         MobESP.Filter.ALL
      )
   );
   private static final int HOSTILE_COLOR = -48060;
   private static final int PASSIVE_COLOR = -12255352;
   private static final int NEUTRAL_COLOR = -188;
   private static final int OUTLINE_ALPHA = 180;
   private static final int FILL_ALPHA = 30;
   private final EspFade espFade = new EspFade(this::finishDisable);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MobESP() {
      super(
         "MobESP",
         "Highlights mobs through walls",
         Category.RENDER
      );
   }

   private void finishDisable() {
      super.onDisable();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.espFade.onEnable();
   }

   @Override
   public void onDisable() {
      this.espFade.onDisable();
   }

   @Override
   public String getInfo() {
      return ((MobESP.Filter)this.filter.get()).name();
   }

   @Override
   public boolean usesEntityCache() {
      return true;
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         this.espFade.update();
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (this.isInGame()) {
            float var2 = this.espFade.getFade();
            if (!(var2 <= 0.001F)) {
               float var3 = event.getTickDelta();
               int var4 = PerformanceHelper.scaleRenderCap(96);
               int var5 = 0;

               for (EntityCache.CachedEntity var7 : EntityCache.getEntities()) {
                  if (var5 >= var4) {
                     break;
                  }

                  if (var7.entity() != mc.field_1724) {
                     boolean var8 = var7.entity() instanceof class_1588;
                     boolean var9 = var7.entity() instanceof class_1296;
                     boolean var10 = var7.entity() instanceof class_1308;
                     if ((var10 || var8 || var9)
                        && (this.filter.get() != MobESP.Filter.HOSTILE || var8)
                        && (this.filter.get() != MobESP.Filter.PASSIVE || var9)
                        && EntityCache.isVisible(var7.boundingBox())) {
                        double var11 = class_3532.method_16436(var3, var7.entity().field_6038, var7.entity().method_23317());
                        double var13 = class_3532.method_16436(var3, var7.entity().field_5971, var7.entity().method_23318());
                        double var15 = class_3532.method_16436(var3, var7.entity().field_5989, var7.entity().method_23321());
                        class_238 var17 = var7.boundingBox();
                        double var18 = (var17.field_1320 - var17.field_1323) * 0.5;
                        double var20 = (var17.field_1324 - var17.field_1321) * 0.5;
                        double var22 = var17.field_1325 - var17.field_1322;
                        double var24 = var11 - var18;
                        double var26 = var13;
                        double var28 = var15 - var20;
                        double var30 = var11 + var18;
                        double var32 = var13 + var22;
                        double var34 = var15 + var20;
                        int var36 = var8 ? -48060 : (var9 ? -12255352 : -188);
                        int var37 = RainbowUtil.withAlpha(var36, Math.max(1, (int)(180.0F * var2)));
                        int var38 = RainbowUtil.withAlpha(var36, Math.max(1, (int)(30.0F * var2)));
                        RenderUtil.drawBox(var24, var26, var28, var30, var32, var34, var37);
                        RenderUtil.drawFilledBox(var24, var26, var28, var30, var32, var34, var38);
                        var5++;
                     }
                  }
               }
            }
         }
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

   public enum Filter {
      ALL,
      HOSTILE,
      PASSIVE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static MobESP.Filter[] $values() {
         MobESP.Filter[] var10000 = new MobESP.Filter[3];
         var10000[0] = ALL;
         var10000[1] = HOSTILE;
         var10000[2] = PASSIVE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(80);
         ALL = new MobESP.Filter();
         var10002 = new byte[7];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-104);
         var10002[5] = (byte)(34);
         var10002[6] = (byte)(-122);
         HOSTILE = new MobESP.Filter();
         var10002 = new byte[7];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(-104);
         var10002[5] = (byte)(56);
         var10002[6] = (byte)(-122);
         PASSIVE = new MobESP.Filter();
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
