package me.drake.vieclient.modules.client.hud;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.gui.nvg.ui.components.GuiStyle;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.render.nvg.PlayerHeadCache;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_640;

public final class RadarWidget {
   public static final float SIZE = 116.0F;
   public static final float WIDGET_W = 116.0F;
   public static final float WIDGET_H = 116.0F;
   public static final float RADIUS = 58.0F;
   private final class_310 mc = class_310.method_1551();
   private boolean wasVisible = false;
   private long shownMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void draw(HUD hud, int range) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (!this.wasVisible) {
            this.shownMs = System.currentTimeMillis();
         }

         this.wasVisible = true;
         NanoVGRenderer.globalAlpha(hud.fadeIn(this.shownMs));
         float var3 = this.defaultX(hud);
         float var4 = this.defaultY(hud);
         float var5 = hud.wx("radar", var3);
         float var6 = hud.wy("radar", var4);
         List var7 = this.getNearbyPlayers(range);
         int var8 = var7.size();

         try (HUD.WidgetRenderScope var9 = hud.beginWidgetRender("radar", var5, var6)) {
            Color var10 = hud.accent();
            float var11 = var5 + 58.0F;
            float var12 = var6 + 58.0F;
            int[] var13 = ThemeManager.bodyTop();
            int var14 = ThemeManager.isCleanStyle() ? 178 : (int)(ThemeManager.bodyAlpha(1.0F) * 0.9F);
            NanoVGRenderer.drawCircle(var11, var12, 58.0F, new Color(var13[0], var13[1], var13[2], var14));
            NanoVGRenderer.drawCircle(var11, var12, 58.0F, new Color(10, 10, 15, 140));
            if (!ThemeManager.isCleanStyle()) {
               int[] var15 = ThemeManager.borderRGB();
               NanoVGRenderer.drawCircleOutline(var11, var12, 58.0F, 1.2F, new Color(var10.getRed(), var10.getGreen(), var10.getBlue(), 95));
               NanoVGRenderer.drawCircleOutline(var11, var12, 57.4F, 0.8F, new Color(var15[0], var15[1], var15[2], 50));
            }

            float var55 = 17.820002F;
            float var16 = 35.640003F;
            float var17 = 54.0F;
            NanoVGRenderer.drawCircleOutline(var11, var12, var55, 0.7F, new Color(255, 255, 255, 18));
            NanoVGRenderer.drawCircleOutline(var11, var12, var16, 0.7F, new Color(255, 255, 255, 18));
            NanoVGRenderer.drawCircleOutline(var11, var12, var17, 0.8F, new Color(255, 255, 255, 22));
            NanoVGRenderer.drawLine(var11 - 52.0F, var12, var11 + 52.0F, var12, 0.7F, new Color(255, 255, 255, 16));
            NanoVGRenderer.drawLine(var11, var12 - 52.0F, var11, var12 + 52.0F, 0.7F, new Color(255, 255, 255, 16));
            long var18 = System.currentTimeMillis();
            float var20 = 2600.0F;
            float var21 = (float)((float)(var18 % (long)var20) / var20 * Math.PI * 2.0);
            float var22 = 54.0F;
            float var23 = var11 + (float)Math.cos(var21) * var22;
            float var24 = var12 + (float)Math.sin(var21) * var22;
            NanoVGRenderer.drawLine(var11, var12, var23, var24, 1.2F, new Color(var10.getRed(), var10.getGreen(), var10.getBlue(), 140));

            for (int var25 = 1; var25 <= 8; var25++) {
               float var26 = var21 - (float)(var25 * 0.04);
               float var27 = var11 + (float)Math.cos(var26) * var22;
               float var28 = var12 + (float)Math.sin(var26) * var22;
               int var29 = (int)(45.0F * (1.0F - var25 / 8.0F));
               NanoVGRenderer.drawLine(var11, var12, var27, var28, 1.0F, new Color(var10.getRed(), var10.getGreen(), var10.getBlue(), var29));
            }

            double var56 = Math.toRadians(this.mc.field_1724.method_36454());
            double var57 = Math.sin(var56);
            double var58 = Math.cos(var56);
            float var31 = 49.0F;
            float var32 = var11 + (float)var57 * var31;
            float var33 = var12 + (float)var58 * var31;
            float var34 = NanoVGRenderer.getTextWidth("N", 6.5F);
            NanoVGRenderer.drawText("N", var32 - var34 / 2.0F, var33 - 3.25F, 6.5F, var10, true);
            float var35 = var11 - (float)var57 * var31;
            float var36 = var12 - (float)var58 * var31;
            float var37 = NanoVGRenderer.getTextWidth("S", 6.5F);
            NanoVGRenderer.drawText("S", var35 - var37 / 2.0F, var36 - 3.25F, 6.5F, HUD.sub());
            float var38 = var11 - (float)var58 * var31;
            float var39 = var12 + (float)var57 * var31;
            float var40 = NanoVGRenderer.getTextWidth("E", 6.5F);
            NanoVGRenderer.drawText("E", var38 - var40 / 2.0F, var39 - 3.25F, 6.5F, HUD.sub());
            float var41 = var11 + (float)var58 * var31;
            float var42 = var12 - (float)var57 * var31;
            float var43 = NanoVGRenderer.getTextWidth("W", 6.5F);
            NanoVGRenderer.drawText("W", var41 - var43 / 2.0F, var42 - 3.25F, 6.5F, HUD.sub());
            String var44 = range + "m";
            float var45 = NanoVGRenderer.getTextWidth(var44, 6.5F);
            float var46 = var12 - 58.0F + 4.5F;
            float var47 = 9.5F;
            if (ThemeManager.isCleanStyle()) {
               NanoVGRenderer.drawRoundedRect(var11 - var45 / 2.0F - 4.0F, var46, var45 + 8.0F, var47, 4.0F, new Color(255, 255, 255, 18));
            } else {
               NanoVGRenderer.drawRoundedRect(var11 - var45 / 2.0F - 4.0F, var46, var45 + 8.0F, var47, 4.0F, new Color(0, 0, 0, 130));
            }

            NanoVGRenderer.drawText(var44, var11 - var45 / 2.0F, var46 + (var47 - 6.5F) / 2.0F, 6.5F, HUD.sub());
            if (var8 > 0) {
               String var48 = var8
                  + (
                     var8 == 1
                        ? " Player"
                        : " Players"
                  );
               float var49 = NanoVGRenderer.getTextWidth(var48, 6.5F, true);
               float var50 = var12 + 58.0F - 14.0F;
               float var51 = 10.0F;
               if (ThemeManager.isCleanStyle()) {
                  NanoVGRenderer.drawRoundedRect(var11 - var49 / 2.0F - 5.0F, var50, var49 + 10.0F, var51, 4.0F, new Color(255, 255, 255, 22));
                  NanoVGRenderer.drawText(var48, var11 - var49 / 2.0F, var50 + (var51 - 6.5F) / 2.0F, 6.5F, Color.WHITE, true);
               } else {
                  NanoVGRenderer.drawRoundedRectGradient(
                     var11 - var49 / 2.0F - 5.0F, var50, var49 + 10.0F, var51, 4.0F, GuiStyle.accent(65), GuiStyle.accent2(65)
                  );
                  NanoVGRenderer.drawRoundedRectOutline(var11 - var49 / 2.0F - 5.0F, var50, var49 + 10.0F, var51, 4.0F, 0.8F, GuiStyle.accent(160));
                  NanoVGRenderer.drawText(var48, var11 - var49 / 2.0F, var50 + (var51 - 6.5F) / 2.0F, 6.5F, Color.WHITE, true);
               }
            }

            float var59 = (float)Math.sin(var18 * 0.004) * 0.5F + 0.5F;
            float var60 = 5.0F + var59 * 0.6F;
            float var61 = 3.2F + var59 * 0.3F;
            NanoVGRenderer.drawCircle(
               var11, var12, 6.0F + var59 * 2.0F, new Color(var10.getRed(), var10.getGreen(), var10.getBlue(), (int)(30.0F + var59 * 25.0F))
            );
            NanoVGRenderer.drawTriangle(var11, var12 - var60, var11 - var61, var12 + var61, var11 + var61, var12 + var61, var10);
            NanoVGRenderer.drawCircle(var11, var12 + 0.5F, 1.2F, Color.WHITE);

            for (class_1657 var52 : var7) {
               this.renderPlayerDot(var52, var11, var12, range, var57, var58);
            }
         }

         hud.cacheBounds("radar", var5, var6, 116.0F, 116.0F);
         hud.drawEditorOutlineForWidget("radar");
         NanoVGRenderer.globalAlpha(1.0F);
      } else {
         this.wasVisible = false;
      }
   }

   private void renderPlayerDot(class_1657 p, float cx, float cy, int range, double sin, double cos) {
      double var9 = p.method_23317() - this.mc.field_1724.method_23317();
      double var11 = p.method_23318() - this.mc.field_1724.method_23318();
      double var13 = p.method_23321() - this.mc.field_1724.method_23321();
      double var15 = Math.sqrt(var9 * var9 + var13 * var13);
      double var17 = Math.min(var15, range);
      double var19 = -var9 * sin + var13 * cos;
      double var21 = -var9 * cos - var13 * sin;
      double var23 = var15 > 0.001 ? var15 : 1.0;
      float var25 = (float)(var17 / range) * 46.0F;
      float var26 = cx + (float)(var21 / var23) * var25;
      float var27 = cy - (float)(var19 / var23) * var25;
      boolean var28 = false;

      try {
         if (VieClientMod.getInstance() != null && VieClientMod.getInstance().getFriendManager() != null) {
            var28 = VieClientMod.getInstance().getFriendManager().isFriend(p.method_5477().getString());
         }
      } catch (Throwable var37) {
      }

      Color var29 = var28 ? new Color(80, 220, 110, 255) : new Color(240, 70, 70, 255);
      float var30 = 6.0F;
      UUID var31 = p.method_5667();
      class_640 var32 = this.mc.method_1562() != null ? this.mc.method_1562().method_2871(var31) : null;
      int var33 = -1;
      if (var32 != null && var32.method_52810() != null) {
         var33 = PlayerHeadCache.getOrRequest(var31, var32.method_52810().comp_1626());
      }

      NanoVGRenderer.drawCircle(var26, var27, var30 + 1.2F, var29);
      if (var33 > 0) {
         NVGImage.drawRoundedImage(var33, var26 - var30, var27 - var30, var30 * 2.0F, var30 * 2.0F, var30);
      } else {
         NanoVGRenderer.drawCircle(var26, var27, var30, new Color(42, 42, 54, 255));
         String var34 = p.method_5477().getString();
         String var35 = var34.isEmpty() ? "?" : var34.substring(0, 1).toUpperCase();
         float var36 = NanoVGRenderer.getTextWidth(var35, 6.5F);
         NanoVGRenderer.drawText(var35, var26 - var36 / 2.0F, var27 - 3.25F, 6.5F, Color.WHITE);
      }

      float var38 = NanoVGRenderer.getTextWidth("▲", 6.0F);
      if (var11 > 2.0) {
         NanoVGRenderer.drawText("▲", var26 - var38 / 2.0F, var27 - var30 - 5.0F, 6.0F, new Color(90, 230, 120, 230));
      } else if (var11 < -2.0) {
         NanoVGRenderer.drawText("▼", var26 - var38 / 2.0F, var27 + var30 + 1.0F, 6.0F, new Color(245, 80, 80, 230));
      }
   }

   private List getNearbyPlayers(int range) {
      ArrayList var2 = new ArrayList();
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         double var3 = (double)range * range;

         for (class_1657 var6 : this.mc.field_1687.method_18456()) {
            if (var6 != null && var6 != this.mc.field_1724 && !var6.method_7325() && var6.method_5805()) {
               double var7 = var6.method_23317() - this.mc.field_1724.method_23317();
               double var9 = var6.method_23321() - this.mc.field_1724.method_23321();
               if (var7 * var7 + var9 * var9 <= var3) {
                  var2.add(var6);
               }
            }
         }

         return var2;
      } else {
         return var2;
      }
   }

   public void onHidden() {
      this.wasVisible = false;
   }

   private float defaultX(HUD hud) {
      int var2 = this.mc.method_22683().method_4486();
      return Math.max(1.0F, var2 - 116.0F - 4.0F);
   }

   private float defaultY(HUD hud) {
      return 24.5F;
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
