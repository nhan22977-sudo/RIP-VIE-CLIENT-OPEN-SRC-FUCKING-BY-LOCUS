package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.util.PerformanceHelper;
import me.drake.vieclient.util.RainbowUtil;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.util.render.EspFade;
import net.minecraft.class_1296;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1588;
import net.minecraft.class_1657;
import net.minecraft.class_243;

public class Tracers extends Module {
   private final EnumSetting filter = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Filter",
         "What to draw tracers to",
         Tracers.Filter.ALL
      )
   );
   private final EnumSetting target = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Target",
         "What part of the entity to target",
         Tracers.Target.Body
      )
   );
   private final BoolSetting stem = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Stem",
         "Draw a vertical stem line through the center of the tracer target",
         true
      )
   );
   private final DoubleSetting width = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Width",
         "Tracer line thickness in pixels",
         1.0,
         0.1,
         5.0,
         0.1
      )
   );
   private final IntSetting range = (IntSetting)this.addSetting(
      (IntSetting)hidden(
         new IntSetting(
            "Range",
            "Maximum tracer range",
            256,
            16,
            256
         )
      )
   );
   private final IntSetting maxTracers = (IntSetting)this.addSetting(
      (IntSetting)hidden(
         new IntSetting(
            "MaxTracers",
            "Maximum tracers per frame",
            120,
            10,
            800
         )
      )
   );
   private static final int PLAYER_COLOR = -3289651;
   private static final int HOSTILE_COLOR = -28271;
   private static final int PASSIVE_COLOR = -7209071;
   private static final int OPACITY = 255;
   private static final Comparator CANDIDATE_COMPARATOR = (a, b) -> Double.compare(a.distSq, b.distSq);
   private final List candidatePool = new ArrayList(128);
   private final List candidates = new ArrayList(128);
   private double[] drawnRayTargets = new double[360];
   private int drawnRayCount = 0;
   private final EspFade espFade = new EspFade(this::finishDisable);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Tracers() {
      super(
         "Tracers",
         "Draws lines to nearby entities",
         Category.RENDER
      );
   }

   private void finishDisable() {
      this.candidatePool.clear();
      this.candidates.clear();
      this.drawnRayCount = 0;
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

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         this.espFade.update();
      }
   }

   private static Setting hidden(Setting setting) {
      setting.setVisibleInGui(false);
      return setting;
   }

   public double getWidth() {
      return (Double)this.width.get();
   }

   @Override
   public boolean isVisibleInClickGui() {
      return false;
   }

   @Override
   public boolean isVisibleInHud() {
      return false;
   }

   @Override
   public String getInfo() {
      return ((Tracers.Filter)this.filter.get()).name() + " " + this.range.get() + "m";
   }

   @Override
   public boolean usesEntityCache() {
      return false;
   }

   private Tracers.EntityCandidate obtainCandidate(class_1297 entity, double distSq, boolean isPlayer, boolean isHostile) {
      int var7 = this.candidates.size();
      Tracers.EntityCandidate var6;
      if (var7 < this.candidatePool.size()) {
         var6 = (Tracers.EntityCandidate)this.candidatePool.get(var7);
      } else {
         var6 = new Tracers.EntityCandidate();
         this.candidatePool.add(var6);
      }

      var6.entity = entity;
      var6.distSq = distSq;
      var6.isPlayer = isPlayer;
      var6.isHostile = isHostile;
      return var6;
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (this.isInGame() && mc.field_1687 != null && mc.field_1724 != null) {
            float var2 = this.espFade.getFade();
            if (!(var2 <= 0.001F)) {
               int var3 = (int)(255.0F * var2);
               if (var3 > 0) {
                  float var4 = event.getTickDelta();
                  double var5 = ((Integer)this.range.get()).intValue();
                  double var7 = var5 * var5;
                  int var9 = PerformanceHelper.scaleRenderCap((Integer)this.maxTracers.get());
                  this.drawnRayCount = 0;
                  this.candidates.clear();

                  for (class_1297 var11 : mc.field_1687.method_18112()) {
                     if (var11 != mc.field_1724
                        && var11.method_5628() != (-6969)
                        && var11 instanceof class_1309 var12
                        && var12.method_5805()
                        && !var11.method_31481()) {
                        double var13 = mc.field_1724.method_5858(var11);
                        if (!(var13 > var7)) {
                           boolean var15 = var11 instanceof class_1657;
                           boolean var16 = var11 instanceof class_1588;
                           boolean var17 = var11 instanceof class_1296;
                           if ((this.filter.get() != Tracers.Filter.PLAYERS || var15) && (this.filter.get() != Tracers.Filter.MOBS || var16 || var17)) {
                              this.candidates.add(this.obtainCandidate(var11, var13, var15, var16));
                           }
                        }
                     }
                  }

                  if (!this.candidates.isEmpty()) {
                     this.candidates.sort(CANDIDATE_COMPARATOR);
                     int var18 = 0;

                     for (Tracers.EntityCandidate var20 : this.candidates) {
                        class_1297 var21 = var20.entity;
                        if (RenderUtil.recordTracerEntity(var21.method_5628())) {
                           int var14;
                           if (var20.isPlayer) {
                              var14 = -3289651;
                           } else if (var20.isHostile) {
                              var14 = -28271;
                           } else {
                              var14 = -7209071;
                           }

                           if (this.renderEntityTracer(var21, var14, var4, var2)) {
                              if (++var18 >= var9) {
                                 break;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean renderEntityTracer(class_1297 entity, int col, float tickDelta, float fade) {
      int var5 = (int)(255.0F * fade);
      if (var5 <= 0) {
         return false;
      }

      int var6 = RainbowUtil.withAlpha(col, var5);
      class_243 var7 = entity.method_30950(tickDelta);
      double var8 = var7.field_1352;
      double var10 = var7.field_1351;
      double var12 = var7.field_1350;
      double var14 = entity.method_5829().field_1325 - entity.method_5829().field_1322;
      double var16 = var10;
      if (this.target.get() == Tracers.Target.Head) {
         var16 += var14;
      } else if (this.target.get() == Tracers.Target.Body) {
         var16 += var14 / 2.0;
      }

      boolean var18 = false;

      for (int var19 = 0; var19 < this.drawnRayCount; var19++) {
         double var20 = this.drawnRayTargets[var19 * (3)];
         double var22 = this.drawnRayTargets[var19 * (3) + 1];
         double var24 = this.drawnRayTargets[var19 * (3) + (2)];
         double var26 = var20 - var8;
         double var28 = var22 - var16;
         double var30 = var24 - var12;
         if (var26 * var26 + var28 * var28 + var30 * var30 < 0.1225) {
            var18 = true;
            break;
         }
      }

      if (!var18) {
         int var32 = this.drawnRayCount * (3);
         if (var32 + (3) > this.drawnRayTargets.length) {
            this.drawnRayTargets = Arrays.copyOf(this.drawnRayTargets, this.drawnRayTargets.length * (2));
         }

         this.drawnRayTargets[var32] = var8;
         this.drawnRayTargets[var32 + 1] = var16;
         this.drawnRayTargets[var32 + (2)] = var12;
         this.drawnRayCount++;
         RenderUtil.drawTracerToPos(var8, var16, var12, var6, tickDelta, ((Double)this.width.get()).floatValue());
      }

      if ((Boolean)this.stem.get()) {
         RenderUtil.drawLine(var8, var10, var12, var8, var10 + var14, var12, var6);
      }

      return true;
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

   private static final class EntityCandidate {
      class_1297 entity;
      double distSq;
      boolean isPlayer;
      boolean isHostile;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum Filter {
      ALL,
      PLAYERS,
      MOBS;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static Tracers.Filter[] $values() {
         Tracers.Filter[] var10000 = new Tracers.Filter[3];
         var10000[0] = ALL;
         var10000[1] = PLAYERS;
         var10000[2] = MOBS;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(80);
         ALL = new Tracers.Filter();
         var10002 = new byte[7];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-121);
         var10002[4] = (byte)(124);
         var10002[5] = (byte)(50);
         var10002[6] = (byte)(-110);
         PLAYERS = new Tracers.Filter();
         var10002 = new byte[4];
         var10002[0] = (byte)(117);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-115);
         MOBS = new Tracers.Filter();
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

   public enum Target {
      Head,
      Body,
      Feet;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static Tracers.Target[] $values() {
         Tracers.Target[] var10000 = new Tracers.Target[3];
         var10000[0] = Head;
         var10000[1] = Body;
         var10000[2] = Feet;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-1);
         var10002[2] = (byte)(125);
         var10002[3] = (byte)(-70);
         Head = new Tracers.Target();
         var10002 = new byte[4];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-11);
         var10002[2] = (byte)(120);
         var10002[3] = (byte)(-89);
         Body = new Tracers.Target();
         var10002 = new byte[4];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-1);
         var10002[2] = (byte)(121);
         var10002[3] = (byte)(-86);
         Feet = new Tracers.Target();
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
}
