package me.drake.vieclient.modules.client.hud;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.gui.nvg.ui.components.GuiStyle;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.modules.client.NameProtect;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.render.nvg.PlayerHeadCache;
import net.minecraft.class_1304;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_640;

public final class PlayerHudWidget {
   public static final float CARD_BASE_W = 216.0F;
   public static final float CARD_BASE_H = 46.0F;
   public static final float CARD_GAP = 5.0F;
   private final class_310 mc = class_310.method_1551();
   private final Map healthTrackers = new ConcurrentHashMap();
   private boolean wasVisible = false;
   private long shownMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static float calculateHeight(int count) {
      return count <= 0 ? 46.0F : 46.0F * count + 5.0F * Math.max(0, count - 1);
   }

   public int getDisplayCount(int maxPlayers, boolean editorMode) {
      List var3 = this.getNearbyPlayers();
      return var3.isEmpty() && editorMode ? 1 : Math.min(var3.size(), Math.max(1, maxPlayers));
   }

   public void draw(HUD hud, int maxPlayers) {
      if (this.mc.field_1724 != null && this.mc.field_1687 != null) {
         long var3 = System.currentTimeMillis();
         this.healthTrackers.entrySet().removeIf(e -> var3 - ((PlayerHudWidget.HealthTracker)e.getValue()).lastSeenMs > 10000L);
         List var5 = this.getNearbyPlayers();
         boolean var6 = hud.isEditorMode()
            || hud.isDragging()
            || "playerhud".equals(hud.getSelectedWidget())
            || "playerhud".equals(hud.getDraggingWidget());
         if (var5.isEmpty() && !var6) {
            this.onHidden();
            hud.removeExternalBounds("playerhud");
         } else {
            if (!this.wasVisible) {
               this.shownMs = System.currentTimeMillis();
            }

            this.wasVisible = true;
            NanoVGRenderer.globalAlpha(hud.fadeIn(this.shownMs));
            int var7 = var5.isEmpty() ? 1 : Math.min(var5.size(), Math.max(1, maxPlayers));
            float var8 = calculateHeight(var7);
            int var9 = this.mc.method_22683().method_4486();
            int var10 = this.mc.method_22683().method_4502();
            float var11 = Math.max(1.0F, Math.min(var9 - 216.0F - 1.0F, 10.0F));
            float var12 = Math.max(1.0F, Math.min(var10 - var8 - 4.0F, 160.0F));
            float var13 = hud.wx("playerhud", var11);
            float var14 = hud.wy("playerhud", var12);
            hud.cacheBounds("playerhud", var13, var14, 216.0F, var8);

            try (HUD.WidgetRenderScope var15 = hud.beginWidgetRender(
                  "playerhud", var13, var14
               )) {
               boolean var16 = ThemeManager.isCleanStyle();
               Color var17 = hud.accent();

               for (int var18 = 0; var18 < var7; var18++) {
                  float var19 = var14 + var18 * 51.0F;
                  Object var20 = var18 < var5.size() ? (class_1657)var5.get(var18) : this.mc.field_1724;
                  boolean var21 = var5.isEmpty() && var6;
                  this.renderPlayerCard(hud, (class_1657)var20, var13, var19, 216.0F, 46.0F, var16, var17, var21, var18);
               }
            }

            hud.drawEditorOutlineForWidget("playerhud");
            NanoVGRenderer.globalAlpha(1.0F);
         }
      } else {
         this.wasVisible = false;
      }
   }

   private void renderPlayerCard(HUD hud, class_1657 player, float x, float y, float w, float h, boolean clean, Color accent, boolean isPreview, int index) {
      float var11 = 5.5F;
      int[] var12 = ThemeManager.borderRGB();
      HUD.drawCardBox(x, y, w, h, var11);
      float var13 = 32.0F;
      float var14 = x + 7.0F;
      float var15 = y + (h - var13) / 2.0F;
      float var16 = 4.0F;
      boolean var17 = this.isPlayerFriend(player);
      int var18 = var17 ? -263136146 : (clean ? 1023410175 : -1275068416 | GuiStyle.accent(180).getRGB() & 16777215);
      NanoVGRenderer.drawRoundedRect(var14, var15, var13, var13, var16, -602532324);
      if (!clean) {
         NanoVGRenderer.drawRoundedRectOutline(var14, var15, var13, var13, var16, 1.0F, var18);
      }

      UUID var19 = player.method_5667();
      class_640 var20 = this.mc.method_1562() != null ? this.mc.method_1562().method_2871(var19) : null;
      int var21 = -1;
      if (var20 != null && var20.method_52810() != null) {
         var21 = PlayerHeadCache.getOrRequest(var19, var20.method_52810().comp_1626());
      }

      if (var21 > 0) {
         NVGImage.drawRoundedImage(var21, var14, var15, var13, var13, var16);
      } else {
         String var22 = isPreview ? "P" : player.method_5477().getString();
         var22 = var22.isEmpty() ? "?" : var22.substring(0, 1).toUpperCase();
         float var23 = NanoVGRenderer.getTextWidth(var22, 12.0F, true);
         NanoVGRenderer.drawText(var22, var14 + (var13 - var23) / 2.0F, var15 + (var13 - 12.0F) / 2.0F, 12.0F, -1, true);
      }

      float var70 = var14 + var13 + 8.0F;
      String var71 = isPreview
         ? "Player [Preview]"
         : player.method_5477().getString();
      String var24 = isPreview ? var71 : NameProtect.staticMaskPlayerName(var71);
      float var25 = w - (var70 - x) - 52.0F;
      String var26 = var24;
      if (NanoVGRenderer.getTextWidth(var26, 8.5F, true) > var25) {
         while (var26.length() > 1 && NanoVGRenderer.getTextWidth(var26 + "..", 8.5F, true) > var25) {
            var26 = var26.substring(0, var26.length() - 1);
         }

         var26 = var26 + "..";
      }

      int var27 = var17 ? -9506676 : ThemeManager.textArgb(255);
      NanoVGRenderer.drawText(var26, var70, y + 6.5F, 8.5F, var27, true);
      float var28 = NanoVGRenderer.getTextWidth(var26, 8.5F, true);
      double var29 = isPreview ? 12.5 : (this.mc.field_1724 != null ? this.mc.field_1724.method_5739(player) : 0.0);
      String var31 = String.format("%.1fm", var29);
      float var32 = NanoVGRenderer.getTextWidth(var31, 6.8F);
      float var33 = var70 + var28 + 6.0F;
      float var34 = y + 7.5F;
      if (var33 + var32 < x + w - 46.0F) {
         float var35 = 9.0F;
         float var36 = var32 + 6.0F;
         float var37 = var33 - 3.0F;
         float var38 = y + 6.5F;
         if (clean) {
            NanoVGRenderer.drawRoundedRect(var37, var38, var36, var35, 2.0F, 385875967);
            NanoVGRenderer.drawText(var31, var37 + (var36 - var32) / 2.0F, var38 + (var35 - 6.8F) / 2.0F, 6.8F, ThemeManager.textDimArgb(180));
         } else {
            NanoVGRenderer.drawRoundedRect(var37, var38, var36, var35, 2.0F, 2013265920);
            NanoVGRenderer.drawRoundedRectOutline(var37, var38, var36, var35, 2.0F, 0.7F, 2013265920 | var12[0] << 16 | var12[1] << 8 | var12[2]);
            NanoVGRenderer.drawText(
               var31, var37 + (var36 - var32) / 2.0F, var38 + (var35 - 6.8F) / 2.0F, 6.8F, -436207616 | GuiStyle.accent(230).getRGB() & 16777215
            );
         }
      }

      float var72 = isPreview ? 20.0F : player.method_6032();
      float var73 = isPreview ? 20.0F : player.method_6063();
      float var74 = isPreview ? 0.0F : player.method_6067();
      float var75 = var72 + var74;
      float var39 = Math.max(1.0F, var73 + var74);
      float var40 = var75;
      float var41 = var75;
      if (!isPreview) {
         PlayerHudWidget.HealthTracker var42 = this.healthTrackers.computeIfAbsent(player.method_5667(), u -> new PlayerHudWidget.HealthTracker());
         var42.update(var75);
         var40 = var42.getCurrentHp();
         var41 = var42.getTrailHp();
      }

      float var76 = Math.max(0.0F, Math.min(1.0F, var40 / var39));
      float var43 = Math.max(0.0F, Math.min(1.0F, var41 / var39));
      String var44 = String.format("%.1f HP", var40);
      float var45 = NanoVGRenderer.getTextWidth(var44, 7.5F, true);
      float var46 = x + w - var45 - 7.0F;
      float var47 = y + 7.0F;
      int var48 = healthColorInt(var76);
      NanoVGRenderer.drawText(var44, var46, var47, 7.5F, var48, true);
      float var49 = var70;
      float var50 = y + 17.5F;
      float var51 = x + w - var49 - 7.0F;
      float var52 = 3.5F;
      float var53 = clean ? 1.0F : 1.5F;
      NanoVGRenderer.drawRoundedRect(var49, var50, var51, var52, var53, (clean ? 60 : 110) << 24);
      if (var43 > var76 + 0.005F) {
         float var54 = Math.max(2.0F, var51 * var43);
         int var55 = clean ? -2097191836 : -1761629626;
         NanoVGRenderer.drawRoundedRect(var49, var50, var54, var52, var53, var55);
      }

      float var77 = Math.max(2.0F, var51 * var76);
      if (clean) {
         NanoVGRenderer.drawRoundedRectGradient(var49, var50, var77, var52, var53, var48, mixColorInts(var48, -16777216, 0.2F));
      } else {
         int var78 = -268435456 | GuiStyle.accent2(240).getRGB() & 16777215;
         NanoVGRenderer.drawRoundedRectGradient(var49, var50, var77, var52, var53, var48, var78);
         NanoVGRenderer.drawCircle(var49 + var77, var50 + var52 / 2.0F, 1.6F, -1);
      }

      float var79 = y + 24.5F;
      float var56 = 16.0F;
      float var57 = 3.0F;
      ArrayList var58 = new ArrayList();
      var58.add(player.method_6118(class_1304.field_6169));
      var58.add(player.method_6118(class_1304.field_6174));
      var58.add(player.method_6118(class_1304.field_6172));
      var58.add(player.method_6118(class_1304.field_6166));
      var58.add(player.method_6047());
      var58.add(player.method_6079());
      float var59 = var70;

      for (int var60 = 0; var60 < var58.size(); var60++) {
         class_1799 var61 = (class_1799)var58.get(var60);
         float var62 = var59;
         int var63 = getItemTierColorInt(var61);
         if (clean) {
            NanoVGRenderer.drawRoundedRect(var62, var79, var56, var56, 4.0F, 251658239);
         } else {
            NanoVGRenderer.drawRoundedRect(var62, var79, var56, var56, 4.0F, 1677721600);
            int var64 = var61.method_7960() ? 520093695 : var63;
            NanoVGRenderer.drawRoundedRectOutline(var62, var79, var56, var56, 4.0F, 0.8F, var64);
         }

         if (var61 != null && !var61.method_7960()) {
            NVGImage.drawMCItem(var61, var62 + 1.0F, var79 + 1.0F, var56 - 2.0F);
            if (var61.method_7963() && var61.method_7986()) {
               float var80 = var61.method_7936();
               float var65 = var61.method_7919();
               float var66 = Math.max(0.0F, Math.min(1.0F, 1.0F - var65 / var80));
               int var67 = healthColorInt(var66);
               float var68 = (var56 - 3.0F) * var66;
               NanoVGRenderer.drawRoundedRect(var62 + 1.5F, var79 + var56 - 2.2F, var68, 1.2F, 0.5F, var67);
            }

            if (var61.method_7947() > 1) {
               String var81 = String.valueOf(var61.method_7947());
               float var82 = NanoVGRenderer.getTextWidth(var81, 6.0F, true);
               NanoVGRenderer.drawText(var81, var62 + var56 - var82 - 1.0F, var79 + var56 - 5.5F, 6.0F, -1, true);
            }
         }

         var59 += var56 + var57;
      }
   }

   public List getNearbyPlayers() {
      ArrayList var1 = new ArrayList();
      if (this.mc.field_1687 != null && this.mc.field_1724 != null) {
         for (class_1657 var3 : this.mc.field_1687.method_18456()) {
            if (var3 != null && var3 != this.mc.field_1724 && !var3.method_7325() && var3.method_5805()) {
               var1.add(var3);
            }
         }

         var1.sort(Comparator.comparingDouble(p -> this.mc.field_1724.method_5858(p)));
         return var1;
      } else {
         return var1;
      }
   }

   public boolean isPlayerFriend(class_1657 p) {
      try {
         if (VieClientMod.getInstance() != null && VieClientMod.getInstance().getFriendManager() != null) {
            return VieClientMod.getInstance().getFriendManager().isFriend(p.method_5477().getString());
         }
      } catch (Throwable var3) {
      }

      return false;
   }

   public static int getItemTierColorInt(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         String var1 = stack.method_7909().toString().toLowerCase();
         byte[] var10001 = new byte[9];
         var10001[0] = (byte)(68);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-74);
         var10001[4] = (byte)(113);
         var10001[5] = (byte)(-115);
         var10001[6] = (byte)(-21);
         var10001[7] = (byte)(-112);
         var10001[8] = (byte)(39);
         if (var1.contains($$v_s(var10001, -3710647589814415345L))) {
            return -593465136;
         }

         var10001 = new byte[7];
         var10001[0] = (byte)(78);
         var10001[1] = (byte)(-13);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-77);
         var10001[4] = (byte)(123);
         var10001[5] = (byte)(-111);
         var10001[6] = (byte)(-26);
         if (var1.contains($$v_s(var10001, -3710647589814415345L))) {
            return -599729952;
         }

         var10001 = new byte[4];
         var10001[0] = (byte)(77);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(112);
         var10001[3] = (byte)(-70);
         if (var1.contains($$v_s(var10001, -3710647589814415345L))) {
            return -587213056;
         }

         var10001 = new byte[4];
         var10001[0] = (byte)(67);
         var10001[1] = (byte)(-24);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-80);
         if (var1.contains($$v_s(var10001, -3710647589814415345L))) {
            return -924786464;
         }

         var10001 = new byte[9];
         var10001[0] = (byte)(73);
         var10001[1] = (byte)(-14);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-73);
         var10001[4] = (byte)(122);
         var10001[5] = (byte)(-110);
         var10001[6] = (byte)(-29);
         var10001[7] = (byte)(-115);
         var10001[8] = (byte)(46);
         if (var1.contains($$v_s(var10001, -3710647589814415345L))) {
            return -1264541536;
         }

         var10001 = new byte[7];
         var10001[0] = (byte)(70);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(124);
         var10001[5] = (byte)(-102);
         var10001[6] = (byte)(-16);
         return var1.contains($$v_s(var10001, -3710647589814415345L))
            ? -1264556747
            : -1610612736 | GuiStyle.accent(160).getRGB() & (16777215);
      } else {
         return 520093695;
      }
   }

   public static Color getItemTierColor(class_1799 stack) {
      return new Color(getItemTierColorInt(stack), true);
   }

   public static int healthColorInt(float pct) {
      if (pct >= 0.5F) {
         float var2 = (pct - 0.5F) / 0.5F;
         return mixColorInts(-672216, -11801746, var2);
      } else {
         float var1 = pct / 0.5F;
         return mixColorInts(-705476, -672216, var1);
      }
   }

   public static Color healthColor(float pct) {
      return new Color(healthColorInt(pct), true);
   }

   public static int mixColorInts(int c1, int c2, float factor) {
      float var3 = Math.max(0.0F, Math.min(1.0F, factor));
      int var4 = c1 >>> (24) & (255);
      int var5 = c1 >> (16) & (255);
      int var6 = c1 >> (8) & (255);
      int var7 = c1 & (255);
      int var8 = c2 >>> (24) & (255);
      int var9 = c2 >> (16) & (255);
      int var10 = c2 >> (8) & (255);
      int var11 = c2 & (255);
      int var12 = (int)(var4 + (var8 - var4) * var3);
      int var13 = (int)(var5 + (var9 - var5) * var3);
      int var14 = (int)(var6 + (var10 - var6) * var3);
      int var15 = (int)(var7 + (var11 - var7) * var3);
      return var12 << (24) | var13 << (16) | var14 << (8) | var15;
   }

   public static Color mixColors(Color c1, Color c2, float factor) {
      return new Color(mixColorInts(c1.getRGB(), c2.getRGB(), factor), true);
   }

   public void onHidden() {
      this.wasVisible = false;
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

   public static class HealthTracker {
      private float currentHp = -1.0F;
      private float startHp = -1.0F;
      private float targetHp = -1.0F;
      private long animStartTime = 0L;
      private long animDuration = 320L;
      private float trailHp = -1.0F;
      private float trailStartHp = -1.0F;
      private long trailStartTime = 0L;
      private long trailDelay = 100L;
      private long trailDuration = 320L;
      private long lastSeenMs = 0L;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public void update(float newTotalHp) {
         this.update(newTotalHp, System.currentTimeMillis());
      }

      public void update(float newTotalHp, long now) {
         this.lastSeenMs = now;
         if (this.currentHp < 0.0F) {
            this.currentHp = newTotalHp;
            this.startHp = newTotalHp;
            this.targetHp = newTotalHp;
            this.trailHp = newTotalHp;
            this.trailStartHp = newTotalHp;
         } else {
            if (Math.abs(newTotalHp - this.targetHp) > 0.05F) {
               if (newTotalHp < this.targetHp) {
                  this.trailStartHp = Math.max(this.currentHp, this.trailHp);
                  this.trailStartTime = now + this.trailDelay;
               } else {
                  this.trailHp = newTotalHp;
                  this.trailStartHp = newTotalHp;
                  this.trailStartTime = 0L;
               }

               this.startHp = this.currentHp;
               this.targetHp = newTotalHp;
               this.animStartTime = now;
            }

            if (this.animStartTime > 0L) {
               float var4 = (float)(now - this.animStartTime);
               float var5 = Math.max(0.0F, Math.min(1.0F, var4 / (float)this.animDuration));
               float var6 = easeIn(var5);
               this.currentHp = this.startHp + (this.targetHp - this.startHp) * var6;
               if (var5 >= 1.0F) {
                  this.currentHp = this.targetHp;
                  this.animStartTime = 0L;
               }
            } else {
               this.currentHp = this.targetHp;
            }

            if (this.trailStartTime > 0L) {
               if (now >= this.trailStartTime) {
                  float var7 = (float)(now - this.trailStartTime);
                  float var8 = Math.max(0.0F, Math.min(1.0F, var7 / (float)this.trailDuration));
                  float var9 = easeIn(var8);
                  this.trailHp = this.trailStartHp + (this.targetHp - this.trailStartHp) * var9;
                  if (var8 >= 1.0F) {
                     this.trailHp = this.targetHp;
                     this.trailStartTime = 0L;
                  }
               }
            } else {
               this.trailHp = Math.max(this.trailHp, this.currentHp);
            }
         }
      }

      public float getCurrentHp() {
         return this.currentHp;
      }

      public float getTrailHp() {
         return this.trailHp;
      }

      public static float easeIn(float t) {
         t = Math.max(0.0F, Math.min(1.0F, t));
         return t * t * t;
      }
   }
}
