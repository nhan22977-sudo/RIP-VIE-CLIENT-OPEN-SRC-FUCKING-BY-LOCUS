package me.drake.vieclient.modules.client.hud;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.gui.nvg.ui.components.GuiStyle;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.MaterialFX;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_1937;
import net.minecraft.class_310;

public final class RegionMapWidget {
   private static final float ICON_ROW_H = 14.5F;
   private static final float CELL = 13.0F;
   private static final float CELL_PITCH = 14.5F;
   private static final float GRID_PX = 129.0F;
   public static final float WIDGET_W = 139.0F;
   private static final float NEW_CELL = 4.0F;
   private static final float NEW_GRID_PX = 144.0F;
   public static final float NEW_WIDGET_W = 154.0F;
   private static final float HDR_H = 17.0F;
   private static final float NUM_SIZE = 6.5F;
   private static final float NUM_SIZE_SMALL = 5.5F;
   private final class_310 mc = class_310.method_1551();
   private boolean wasVisible = false;
   private long shownMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static float calculateWidth(HUD.RegionMapMode mode) {
      return mode == HUD.RegionMapMode.NEW ? 154.0F : 139.0F;
   }

   public static float calculateHeight(boolean supported) {
      return calculateHeight(supported, HUD.RegionMapMode.OLD);
   }

   public static float calculateHeight(boolean supported, HUD.RegionMapMode mode) {
      float var2 = mode == HUD.RegionMapMode.NEW ? 144.0F : 129.0F;
      return supported ? 18.0F + var2 + 5.0F : 36.0F;
   }

   public void draw(HUD hud) {
      this.draw(hud, hud.getRegionMapMode());
   }

   public void draw(HUD hud, boolean showLegend) {
      this.draw(hud, hud.getRegionMapMode());
   }

   public void draw(HUD hud, HUD.RegionMapMode mode) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         if (!this.wasVisible) {
            this.shownMs = System.currentTimeMillis();
         }

         this.wasVisible = true;
         NanoVGRenderer.globalAlpha(hud.fadeIn(this.shownMs));
         boolean var3 = this.mc.field_1687.method_27983() == class_1937.field_25180;
         boolean var4 = this.mc.field_1687.method_27983() == class_1937.field_25181;
         boolean var5 = !var3 && !var4;
         float var6 = calculateWidth(mode);
         float var7 = calculateHeight(var5, mode);
         float var8 = 4.0F;
         float var9 = this.defaultY(hud, var7);
         float var10 = hud.wx("regionmap", var8);
         float var11 = hud.wy("regionmap", var9);

         try (HUD.WidgetRenderScope var12 = hud.beginWidgetRender(
               "regionmap", var10, var11
            )) {
            Color var13 = hud.accent();
            float var14 = 4.0F;
            MaterialFX.glassBody(var10, var11, var6, var7, var14, 1.0F);
            NanoVGRenderer.drawRoundedRectGradient(var10, var11, var6, var7, var14, HUD.bgTop(), HUD.bgBot());
            if (!ThemeManager.isCleanStyle()) {
               NanoVGRenderer.drawRoundedRectOutline(var10, var11, var6, var7, var14, 1.0F, new Color(var13.getRed(), var13.getGreen(), var13.getBlue(), 50));
            }

            GuiStyle.gloss(var10, var11, var6, var7, var14, 1.0F);
            HUD.material(var10, var11, var6, var7, 1.0F);
            float var15 = 9.0F;
            float var16 = var10 + 6.0F;
            float var17 = var11 + (17.0F - var15) / 2.0F;
            if (IconManager.has("regionmap")) {
               IconManager.draw("regionmap", var16, var17, var15, Color.WHITE);
            }

            float var18 = var10 + 6.0F + var15 + 4.0F;
            if (!ThemeManager.isCleanStyle()) {
               NanoVGRenderer.drawText(
                  "REGION MAP", var18, var11 + 4.5F, 8.0F, Color.WHITE
               );
            } else {
               NanoVGRenderer.drawText(
                  "REGION MAP", var18, var11 + 4.5F, 8.0F, Color.WHITE
               );
            }

            float var19 = var10 + 5.0F;
            float var20 = var11 + 17.0F + 1.0F;
            if (mode == HUD.RegionMapMode.NEW) {
               this.drawNew(hud, var13, var10, var11, var6, var19, var20, var5, var3);
            } else {
               this.drawOld(hud, var13, var10, var11, var6, var19, var20, var5, var3);
            }
         }

         hud.cacheBounds("regionmap", var10, var11, var6, var7);
         hud.drawEditorOutlineForWidget("regionmap");
         NanoVGRenderer.globalAlpha(1.0F);
      } else {
         this.wasVisible = false;
      }
   }

   private void drawOld(HUD hud, Color ac, float ex, float ey, float widgetW, float gx0, float gy0, boolean supported, boolean inNether) {
      int var10 = -1;
      if (supported && this.mc.field_1724 != null) {
         var10 = RegionGrid.regionAt(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23321());
      }

      String var10000;
      if (var10 >= 0) {
         var10000 = "#" + var10;
      } else {
         byte[] var12 = new byte[3];
         var12[0] = (byte)(8);
         var12[1] = (byte)(-73);
         var12[2] = (byte)(49);
         var10000 = $$v_s(var12, 2859816271782072334L);
      }

      String var11 = var10000;
      this.drawPill(ex, ey, widgetW, var11);
      if (!supported) {
         this.drawUnsupported(ex, gy0, widgetW, inNether);
      } else {
         this.drawSupportedOld(hud, ac, ex, ey, gx0, gy0, var10);
      }
   }

   private void drawNew(HUD hud, Color ac, float ex, float ey, float widgetW, float gx0, float gy0, boolean supported, boolean inNether) {
      NewRegionGrid.Shard var10 = null;
      NewRegionGrid.Shard var11 = null;
      if (supported && this.mc.field_1724 != null) {
         var10 = NewRegionGrid.shardAt(this.mc.field_1724.method_23317(), this.mc.field_1724.method_23321());
         float var12 = this.mc.method_22683().method_4495();
         double var13 = this.mc.field_1729.method_1603() / var12;
         double var15 = this.mc.field_1729.method_1604() / var12;
         if (var13 >= gx0 && var13 < gx0 + 144.0F && var15 >= gy0 && var15 < gy0 + 144.0F) {
            int var17 = (int)((var13 - gx0) / 4.0);
            int var18 = (int)((var15 - gy0) / 4.0);
            var11 = NewRegionGrid.getShard(var17, var18);
         }
      }

      NewRegionGrid.Shard var19 = var11 != null ? var11 : var10;
      String var10000;
      if (var19 != null) {
         var10000 = "#" + var19.number();
      } else {
         byte[] var21 = new byte[3];
         var21[0] = (byte)(8);
         var21[1] = (byte)(-73);
         var21[2] = (byte)(49);
         var10000 = $$v_s(var21, 2859816271782072334L);
      }

      String var20 = var10000;
      this.drawPill(ex, ey, widgetW, var20);
      if (!supported) {
         this.drawUnsupported(ex, gy0, widgetW, inNether);
      } else {
         this.drawSupportedNew(hud, ac, ex, ey, gx0, gy0, var10, var11);
      }
   }

   private void drawPill(float ex, float ey, float widgetW, String pillText) {
      float var5 = NanoVGRenderer.getTextWidth(pillText, 7.5F, true);
      float var6 = var5 + 10.0F;
      float var7 = 12.0F;
      float var8 = ex + widgetW - var6 - 6.0F;
      float var9 = ey + (17.0F - var7) / 2.0F;
      float var10 = 4.0F;
      NanoVGRenderer.drawRoundedRect(var8, var9, var6, var7, var10, Color.WHITE);
      NanoVGRenderer.drawText(pillText, var8 + (var6 - var5) / 2.0F, var9 + (var7 - 7.5F) / 2.0F, 7.5F, Color.BLACK, true);
   }

   private void drawUnsupported(float ex, float gy0, float widgetW, boolean inNether) {
      String var8;
      if (inNether) {
         byte[] var10000 = new byte[6];
         var10000[0] = (byte)(101);
         var10000[1] = (byte)(-1);
         var10000[2] = (byte)(104);
         var10000[3] = (byte)(-74);
         var10000[4] = (byte)(99);
         var10000[5] = (byte)(-73);
         var8 = $$v_s(var10000, 2859816271782072334L);
      } else {
         byte[] var9 = new byte[7];
         var9[0] = (byte)(127);
         var9[1] = (byte)(-14);
         var9[2] = (byte)(121);
         var9[3] = (byte)(-2);
         var9[4] = (byte)(67);
         var9[5] = (byte)(-85);
         var9[6] = (byte)(-41);
         var8 = $$v_s(var9, 2859816271782072334L);
      }

      String var5 = var8;
      String var6 = var5 + " - Not Supported";
      float var7 = NanoVGRenderer.getTextWidth(var6, 9.5F);
      NanoVGRenderer.drawText(var6, ex + (widgetW - var7) / 2.0F, gy0 + 4.0F, 9.5F, HUD.sub());
   }

   public void onHidden() {
      this.wasVisible = false;
   }

   private float defaultY(HUD hud, float rmH) {
      float var3 = 24.5F;
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(95);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(113);
      var10001[3] = (byte)(-69);
      float[] var4;
      if ((var4 = hud.getCachedBounds($$v_s(var10001, 2859816271782072334L))) != null) {
         var3 = var4[1] + var4[3] + 6.0F;
      }

      var10001 = new byte[8];
      var10001[0] = (byte)(64);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(101);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(111);
      var10001[5] = (byte)(-85);
      var10001[6] = (byte)(-41);
      var10001[7] = (byte)(124);
      if ((var4 = hud.getCachedBounds($$v_s(var10001, 2859816271782072334L))) != null) {
         var3 = var4[1] + var4[3] + 6.0F;
      }

      var10001 = new byte[7];
      var10001[0] = (byte)(91);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(104);
      var10001[3] = (byte)(-73);
      var10001[4] = (byte)(105);
      var10001[5] = (byte)(-85);
      var10001[6] = (byte)(-64);
      if ((var4 = hud.getCachedBounds($$v_s(var10001, 2859816271782072334L))) != null) {
         var3 = var4[1] + var4[3] + 6.0F;
      }

      int var5 = this.mc.method_22683().method_4502();
      return Math.max(1.0F, Math.min(var5 - rmH - 1.0F, var3));
   }

   private void drawSupportedOld(HUD hud, Color ac, float ex, float ey, float gx0, float gy0, int playerCell) {
      double var8 = this.mc.field_1724.method_23317();
      double var10 = this.mc.field_1724.method_23321();
      int var12 = RegionGrid.gridX(var8);
      int var13 = RegionGrid.gridZ(var10);
      boolean var14 = RegionGrid.inGrid(var12, var13);
      int var15 = -1;
      int var16 = -1;
      float var17 = this.mc.method_22683().method_4495();
      double var18 = this.mc.field_1729.method_1603() / var17;
      double var20 = this.mc.field_1729.method_1604() / var17;
      if (var18 >= gx0 && var20 >= gy0) {
         int var22 = (int)((var18 - gx0) / 14.5);
         int var23 = (int)((var20 - gy0) / 14.5);
         if (RegionGrid.inGrid(var22, var23)) {
            var15 = var22;
            var16 = var23;
         }
      }

      for (int var35 = 0; var35 < 9; var35++) {
         for (int var37 = 0; var37 < 9; var37++) {
            float var24 = gx0 + var37 * 14.5F;
            float var25 = gy0 + var35 * 14.5F;
            boolean var26 = var14 && var37 == var12 && var35 == var13;
            boolean var27 = var37 == var15 && var35 == var16;
            Color var28 = RegionGrid.colorAt(var37, var35);
            NanoVGRenderer.drawRoundedRect(var24, var25, 13.0F, 13.0F, 2.5F, var28);
            String var29 = String.valueOf(RegionGrid.GRID[var35][var37]);
            float var30 = var29.length() >= 3 ? 5.5F : 6.5F;
            float var31 = NanoVGRenderer.getTextWidth(var29, var30);
            NanoVGRenderer.drawText(var29, var24 + (13.0F - var31) / 2.0F, var25 + (13.0F - var30) / 2.0F, var30, Color.WHITE);
            if (var26) {
               if (ThemeManager.isCleanStyle()) {
                  NanoVGRenderer.drawRoundedRect(var24, var25, 13.0F, 13.0F, 2.5F, new Color(255, 255, 255, 60));
               } else {
                  NanoVGRenderer.drawRoundedRectOutline(var24, var25, 13.0F, 13.0F, 2.5F, 1.6F, Color.WHITE);
               }
            } else if (var27) {
               if (ThemeManager.isCleanStyle()) {
                  NanoVGRenderer.drawRoundedRect(var24, var25, 13.0F, 13.0F, 2.5F, new Color(255, 255, 255, 35));
               } else {
                  NanoVGRenderer.drawRoundedRectOutline(var24, var25, 13.0F, 13.0F, 2.5F, 1.2F, new Color(255, 255, 255, 180));
               }
            }
         }
      }

      if (var14) {
         float var36 = (float)(-225000.0 + var12 * 50000.0);
         float var38 = (float)(-225000.0 + var13 * 50000.0);
         float var39 = Math.max(0.07F, Math.min(0.93F, (float)((var8 - var36) / 50000.0)));
         float var40 = Math.max(0.07F, Math.min(0.93F, (float)((var10 - var38) / 50000.0)));
         float var41 = gx0 + var12 * 14.5F + var39 * 13.0F;
         float var42 = gy0 + var13 * 14.5F + var40 * 13.0F;
         float var43 = (float)Math.toRadians(this.mc.field_1724.method_36454());
         float var44 = -((float)Math.sin(var43));
         float var45 = (float)Math.cos(var43);
         float var46 = -var45;
         float var32 = var44;
         float var33 = 5.5F;
         float var34 = 3.2F;
         NanoVGRenderer.drawTriangle(
            var41 + var44 * var33,
            var42 + var45 * var33,
            var41 - var44 * var34 + var46 * var34,
            var42 - var45 * var34 + var32 * var34,
            var41 - var44 * var34 - var46 * var34,
            var42 - var45 * var34 - var32 * var34,
            Color.WHITE
         );
      }
   }

   private void drawSupportedNew(HUD hud, Color ac, float ex, float ey, float gx0, float gy0, NewRegionGrid.Shard currentShard, NewRegionGrid.Shard hoverShard) {
      for (NewRegionGrid.Shard var10 : NewRegionGrid.SHARDS) {
         float var11 = gx0 + var10.col() * 4.0F;
         float var12 = gy0 + var10.row() * 4.0F;
         float var13 = var10.width() * 4.0F;
         float var14 = var10.height() * 4.0F;
         Color var15 = NewRegionGrid.colorOf(var10.region());
         NanoVGRenderer.drawRect(var11, var12, var13, var14, var15);
      }

      Color var30 = new Color(0, 0, 0, 90);

      for (int var31 = 0; var31 < 36; var31++) {
         for (int var33 = 0; var33 < 36; var33++) {
            if (var33 < 35 && NewRegionGrid.differs(var31, var33, var31, var33 + 1, false)) {
               NanoVGRenderer.drawRect(gx0 + (var33 + 1) * 4.0F - 0.5F, gy0 + var31 * 4.0F, 1.0F, 4.0F, var30);
            }

            if (var31 < 35 && NewRegionGrid.differs(var31, var33, var31 + 1, var33, false)) {
               NanoVGRenderer.drawRect(gx0 + var33 * 4.0F, gy0 + (var31 + 1) * 4.0F - 0.5F, 4.0F, 1.0F, var30);
            }
         }
      }

      Color var32 = new Color(0, 0, 0, 200);

      for (int var34 = 0; var34 < 36; var34++) {
         for (int var39 = 0; var39 < 36; var39++) {
            if (var39 < 35 && NewRegionGrid.differs(var34, var39, var34, var39 + 1, true)) {
               NanoVGRenderer.drawRect(gx0 + (var39 + 1) * 4.0F - 0.5F, gy0 + var34 * 4.0F, 1.0F, 4.0F, var32);
            }

            if (var34 < 35 && NewRegionGrid.differs(var34, var39, var34 + 1, var39, true)) {
               NanoVGRenderer.drawRect(gx0 + var39 * 4.0F, gy0 + (var34 + 1) * 4.0F - 0.5F, 4.0F, 1.0F, var32);
            }
         }
      }

      NanoVGRenderer.drawRoundedRectOutline(gx0, gy0, 144.0F, 144.0F, 1.0F, 1.0F, new Color(0, 0, 0, 180));

      for (NewRegionGrid.Shard var40 : NewRegionGrid.SHARDS) {
         String var43 = Integer.toString(var40.number());
         float var47 = var40.width() * 4.0F;
         float var50 = var40.height() * 4.0F;
         float var16 = gx0 + var40.col() * 4.0F;
         float var17 = gy0 + var40.row() * 4.0F;
         float var18 = var47 - 1.0F;
         float var19 = var50 - 1.0F;
         float var20 = Math.min(6.5F, Math.min(var19, var18 / Math.max(1.0F, var43.length() * 0.62F)));
         if (var20 >= 3.5F) {
            float var21 = NanoVGRenderer.getTextWidth(var43, var20);
            NanoVGRenderer.drawText(var43, var16 + (var47 - var21) / 2.0F, var17 + (var50 - var20) / 2.0F, var20, Color.WHITE);
         }
      }

      if (currentShard != null) {
         float var36 = gx0 + currentShard.col() * 4.0F;
         float var41 = gy0 + currentShard.row() * 4.0F;
         float var44 = currentShard.width() * 4.0F;
         float var48 = currentShard.height() * 4.0F;
         if (ThemeManager.isCleanStyle()) {
            NanoVGRenderer.drawRect(var36, var41, var44, var48, new Color(255, 255, 255, 50));
         } else {
            NanoVGRenderer.drawRoundedRectOutline(var36, var41, var44, var48, 1.0F, 1.4F, Color.WHITE);
         }
      }

      if (hoverShard != null) {
         float var37 = gx0 + hoverShard.col() * 4.0F;
         float var42 = gy0 + hoverShard.row() * 4.0F;
         float var45 = hoverShard.width() * 4.0F;
         float var49 = hoverShard.height() * 4.0F;
         if (ThemeManager.isCleanStyle()) {
            NanoVGRenderer.drawRect(var37, var42, var45, var49, new Color(255, 255, 255, 60));
         } else {
            NanoVGRenderer.drawRect(var37, var42, var45, var49, new Color(255, 255, 255, 45));
            NanoVGRenderer.drawRoundedRectOutline(var37, var42, var45, var49, 1.0F, 1.2F, new Color(255, 255, 255, 200));
         }
      }

      double var38 = this.mc.field_1724.method_23317();
      double var46 = this.mc.field_1724.method_23321();
      double var51 = -225000.0;
      double var52 = 225000.0;
      if (var38 >= var51 && var38 <= var52 && var46 >= var51 && var46 <= var52) {
         float var53 = (float)((var38 + 225000.0) / 450000.0);
         float var54 = (float)((var46 + 225000.0) / 450000.0);
         float var55 = gx0 + var53 * 144.0F;
         float var22 = gy0 + var54 * 144.0F;
         float var23 = (float)Math.toRadians(this.mc.field_1724.method_36454());
         float var24 = -((float)Math.sin(var23));
         float var25 = (float)Math.cos(var23);
         float var26 = -var25;
         float var27 = var24;
         float var28 = 5.0F;
         float var29 = 2.8F;
         NanoVGRenderer.drawTriangle(
            var55 + var24 * (var28 + 0.5F),
            var22 + var25 * (var28 + 0.5F),
            var55 - var24 * (var29 + 0.5F) + var26 * (var29 + 0.5F),
            var22 - var25 * (var29 + 0.5F) + var27 * (var29 + 0.5F),
            var55 - var24 * (var29 + 0.5F) - var26 * (var29 + 0.5F),
            var22 - var25 * (var29 + 0.5F) - var27 * (var29 + 0.5F),
            new Color(0, 0, 0, 180)
         );
         NanoVGRenderer.drawTriangle(
            var55 + var24 * var28,
            var22 + var25 * var28,
            var55 - var24 * var29 + var26 * var29,
            var22 - var25 * var29 + var27 * var29,
            var55 - var24 * var29 - var26 * var29,
            var22 - var25 * var29 - var27 * var29,
            Color.WHITE
         );
         NanoVGRenderer.drawCircle(var55, var22, 1.2F, new Color(255, 70, 70));
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
