package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1753;
import net.minecraft.class_1764;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_638;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class Trajectories extends Module {
   private final BoolSetting tracerEnabled = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tracers",
         "Show trajectory line.",
         true
      )
   );
   private final BoolSetting toolsEnabled = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tools",
         "Show trajectories for bow, crossbow and trident.",
         true
      )
   );
   private final BoolSetting pearlEnabled = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Pearl",
         "Show trajectory for ender pearl.",
         true
      )
   );
   private final ColorSetting lineColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "LineColor",
         "Trajectory line color.",
         -4924161
      )
   );
   private final ColorSetting warningColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "WarningColor",
         "Color when hitting entity.",
         -47546
      )
   );
   private static final double GRAVITY_PEARL = 0.03;
   private static final double DRAG_PEARL = 0.99;
   private static final double SPEED_PEARL = 1.5;
   private static final double GRAVITY_BOW = 0.05;
   private static final double SPEED_BOW_FULL = 3.0;
   private static final double GRAVITY_CROSSBOW = 0.05;
   private static final double SPEED_CROSSBOW = 2.5;
   private static final double GRAVITY_TRIDENT = 0.05;
   private static final double SPEED_TRIDENT = 2.5;
   private static final int MAX_TICKS = 300;
   private static final int ENTITY_CHECK_DELAY_TICKS = 4;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Trajectories() {
      super(
         "Trajectories",
         "Predicts the flight path of thrown projectiles.",
         Category.RENDER
      );
      this.lineColor.visibleWhen(this.tracerEnabled::get);
      this.warningColor.visibleWhen(this.tracerEnabled::get);
   }

   private Trajectories.ProjectileInfo getProjectile() {
      if (mc.field_1724 == null) {
         return null;
      }

      class_1799 var1 = mc.field_1724.method_6047();
      class_1799 var2 = mc.field_1724.method_6079();
      class_1792 var3 = var1.method_7909();
      class_1792 var4 = var2.method_7909();
      if (var3 != class_1802.field_8634 && var4 != class_1802.field_8634) {
         if (var3 != class_1802.field_8102 && var4 != class_1802.field_8102) {
            if (var3 != class_1802.field_8399 && var4 != class_1802.field_8399) {
               if (var3 != class_1802.field_8547 && var4 != class_1802.field_8547) {
                  return null;
               }

               if (!(Boolean)this.toolsEnabled.get()) {
                  return null;
               }

               float var10 = Math.min(mc.field_1724.method_6048(), 10);
               double var12 = 2.5 * Math.max(0.05, var10 / 10.0);
               return new Trajectories.ProjectileInfo(Trajectories.ItemType.TRIDENT, 0.05, var12, false);
            } else {
               if (!(Boolean)this.toolsEnabled.get()) {
                  return null;
               }

               class_1799 var9 = var3 == class_1802.field_8399 ? var1 : var2;
               float var11 = class_1764.method_7781(var9) ? 1.0F : 0.0F;
               double var7 = 2.5 * Math.max(0.05, var11);
               return new Trajectories.ProjectileInfo(Trajectories.ItemType.CROSSBOW, 0.05, var7, false);
            }
         } else {
            if (!(Boolean)this.toolsEnabled.get()) {
               return null;
            }

            float var5 = class_1753.method_7722(mc.field_1724.method_6048());
            double var6 = 3.0 * Math.max(0.05, var5);
            return new Trajectories.ProjectileInfo(Trajectories.ItemType.BOW, 0.05, var6, false);
         }
      } else {
         return !this.pearlEnabled.get() ? null : new Trajectories.ProjectileInfo(Trajectories.ItemType.PEARL, 0.03, 1.5, true);
      }
   }

   @EventHandler
   private void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && mc.field_1724 != null && mc.field_1687 != null) {
         Trajectories.ProjectileInfo var2 = this.getProjectile();
         if (var2 != null) {
            Trajectories.SimResult var3 = this.simulate(var2, event.getTickDelta());
            if (var3 != null && var3.points.size() >= (2)) {
               int var4 = var3.hitEntity ? (Integer)this.warningColor.get() : (Integer)this.lineColor.get();
               if ((Boolean)this.tracerEnabled.get()) {
                  for (int var5 = 0; var5 < var3.points.size() - 1; var5++) {
                     class_243 var6 = (class_243)var3.points.get(var5);
                     class_243 var7 = (class_243)var3.points.get(var5 + 1);
                     RenderUtil.drawLine(var6.field_1352, var6.field_1351, var6.field_1350, var7.field_1352, var7.field_1351, var7.field_1350, var4);
                  }
               }

               class_243 var8 = (class_243)var3.points.get(var3.points.size() - 1);
               class_2350 var9 = var3.hitSide != null ? var3.hitSide : class_2350.field_11036;
               if (var3.hitEntity) {
                  this.drawCrosshair(var8, var9, var4);
               } else if (var3.landingBlock != null) {
                  this.drawLandingBox(var3.landingBlock.x, var3.landingBlock.y, var3.landingBlock.z, var4);
                  this.drawCrosshair(var8, var9, var4);
               }
            }
         }
      }
   }

   private void drawLandingBox(int x, int y, int z, int color) {
      class_238 var5 = new class_238(x, y, z, x + 1, y + 1, z + 1);
      RenderUtil.drawFilledBox(var5, color);
      RenderUtil.drawBox(var5, color);
   }

   private void drawCrosshair(class_243 pos, class_2350 side, int color) {
      double var4 = 0.35;
      double var6 = 0.02;
      double var8 = pos.field_1352 + side.method_10148() * var6;
      double var10 = pos.field_1351 + side.method_10164() * var6;
      double var12 = pos.field_1350 + side.method_10165() * var6;
      RenderUtil.drawLine(var8 - var4, var10, var12 - var4, var8 + var4, var10, var12 + var4, color);
      RenderUtil.drawLine(var8 - var4, var10, var12 + var4, var8 + var4, var10, var12 - var4, color);
   }

   private Trajectories.SimResult simulate(Trajectories.ProjectileInfo info, float tickDelta) {
      class_638 var3 = mc.field_1687;
      class_243 var4 = mc.field_1724.method_5836(tickDelta).method_1023(0.0, 0.35, 0.0);
      class_243 var5 = mc.field_1724.method_5828(tickDelta).method_1021(info.speed);
      ArrayList var6 = new ArrayList();
      var6.add(var4);
      class_243 var7 = var4;
      List var8 = var3.method_8333(
         mc.field_1724, mc.field_1724.method_5829().method_1014(128.0), e -> e instanceof class_1309 && e.method_5805() && !(e instanceof class_1657)
      );

      for (int var9 = 0; var9 < (300); var9++) {
         class_243 var10 = var7.method_1019(var5);
         if (var9 >= (4)) {
            class_1297 var11 = this.findEntityHit(var7, var10, var8);
            if (var11 != null) {
               var6.add(var10);
               return new Trajectories.SimResult(var6, true, (Trajectories.BlockPosHolder)null, class_2350.field_11036);
            }
         }

         class_3959 var14 = new class_3959(var7, var10, class_3960.field_17558, class_242.field_1347, mc.field_1724);
         class_3965 var12 = var3.method_17742(var14);
         if (var12 != null && var12.method_17783() != class_240.field_1333) {
            var6.add(var12.method_17784());
            Trajectories.BlockPosHolder var13 = new Trajectories.BlockPosHolder(
               var12.method_17777().method_10263(), var12.method_17777().method_10264(), var12.method_17777().method_10260()
            );
            return new Trajectories.SimResult(var6, false, var13, var12.method_17780());
         }

         var6.add(var10);
         var7 = var10;
         if (info.useDrag) {
            var5 = var5.method_1021(0.99);
         }

         var5 = var5.method_1023(0.0, info.gravity, 0.0);
      }

      return new Trajectories.SimResult(var6, false, (Trajectories.BlockPosHolder)null, class_2350.field_11036);
   }

   private class_1297 findEntityHit(class_243 from, class_243 to, List candidates) {
      if (candidates != null && !candidates.isEmpty()) {
         class_238 var4 = new class_238(from, to);

         for (class_1297 var6 : candidates) {
            if (var6.method_5805() && var6.method_5829().method_1014(0.3).method_994(var4)) {
               return var6;
            }
         }

         return null;
      } else {
         return null;
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

   private static class BlockPosHolder {
      final int x;
      final int y;
      final int z;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      BlockPosHolder(int x, int y, int z) {
         this.x = xx;
         this.y = yx;
         this.z = zx;
      }
   }

   private enum ItemType {
      PEARL,
      BOW,
      CROSSBOW,
      TRIDENT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private static Trajectories.ItemType[] $values() {
         Trajectories.ItemType[] var10000 = new Trajectories.ItemType[4];
         var10000[0] = PEARL;
         var10000[1] = BOW;
         var10000[2] = CROSSBOW;
         var10000[3] = TRIDENT;
         return var10000;
      }

      // $VF: synthetic method
      private static Trajectories.ItemType[] $values$() {
         Trajectories.ItemType[] var10000 = new Trajectories.ItemType[4];
         var10000[0] = PEARL;
         var10000[1] = BOW;
         var10000[2] = CROSSBOW;
         var10000[3] = TRIDENT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(47);
         PEARL = new Trajectories.ItemType();
         var10002 = new byte[3];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(75);
         BOW = new Trajectories.ItemType();
         var10002 = new byte[8];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(48);
         var10002[5] = (byte)(-40);
         var10002[6] = (byte)(-101);
         var10002[7] = (byte)(49);
         CROSSBOW = new Trajectories.ItemType();
         var10002 = new byte[7];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(38);
         var10002[5] = (byte)(-44);
         var10002[6] = (byte)(-128);
         TRIDENT = new Trajectories.ItemType();
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

   private static class ProjectileInfo {
      final Trajectories.ItemType type;
      final double gravity;
      final double speed;
      final boolean useDrag;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ProjectileInfo(Trajectories.ItemType type, double gravity, double speed, boolean useDrag) {
         this.type = typex;
         this.gravity = gravityx;
         this.speed = speedx;
         this.useDrag = useDragx;
      }
   }

   private static class SimResult {
      final List points;
      final boolean hitEntity;
      final Trajectories.BlockPosHolder landingBlock;
      final class_2350 hitSide;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      SimResult(List points, boolean hitEntity, Trajectories.BlockPosHolder landingBlock, class_2350 hitSide) {
         this.points = pointsx;
         this.hitEntity = hitEntityx;
         this.landingBlock = landingBlockx;
         this.hitSide = hitSidex;
      }
   }
}
