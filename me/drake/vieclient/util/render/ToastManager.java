package me.drake.vieclient.util.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.FrostedBackground;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.MaterialFX;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RainbowUtil;
import net.minecraft.class_1799;

public final class ToastManager {
   static final List toasts = new ArrayList();
   private static final Object LOCK = new Object();
   private static final int MAX_TOASTS = 8;
   private static long lastRenderMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private static float easeOutCubic(float t) {
      if (t >= 1.0F) {
         return 1.0F;
      }

      float var1 = t - 1.0F;
      return 1.0F + var1 * var1 * var1;
   }

   private static float easeInCubic(float t) {
      if (t <= 0.0F) {
         return 0.0F;
      } else {
         return t >= 1.0F ? 1.0F : t * t * t;
      }
   }

   private static float easeOutBack(float t, float overshoot) {
      if (t >= 1.0F) {
         return 1.0F;
      }

      float var2 = t - 1.0F;
      return 1.0F + (overshoot + 1.0F) * var2 * var2 * var2 + overshoot * var2 * var2;
   }

   static boolean matchesTitle(String a, String b) {
      if (a == null || b == null) {
         return a == b;
      }

      if (a.equalsIgnoreCase(b)) {
         return true;
      }

      String var2 = a.replace(" ", "").toLowerCase(Locale.ROOT);
      String var3 = b.replace(" ", "").toLowerCase(Locale.ROOT);
      return var2.equals(var3);
   }

   static int getToastCount() {
      synchronized (LOCK) {
         return toasts.size();
      }
   }

   static void clearForTest() {
      synchronized (LOCK) {
         toasts.clear();
      }
   }

   static ToastManager.Toast getToast(int index) {
      synchronized (LOCK) {
         return index >= 0 && index < toasts.size() ? (ToastManager.Toast)toasts.get(index) : null;
      }
   }

   private ToastManager() {
   }

   public static void push(String moduleName, boolean enabled) {
      byte[] var10003 = new byte[3];
      var10003[0] = (byte)(127);
      var10003[1] = (byte)(-29);
      var10003[2] = (byte)(121);
      push(moduleName, enabled, null, $$v_s(var10003, -7260256692079439809L));
   }

   public static void push(String moduleName, boolean enabled, String desc, String icon) {
      byte[] var10000 = new byte[6];
      var10000[0] = (byte)(87);
      var10000[1] = (byte)(-43);
      var10000[2] = (byte)(88);
      var10000[3] = (byte)(-117);
      var10000[4] = (byte)(-12);
      var10000[5] = (byte)(-8);
      String var4 = $$v_s(var10000, -7260256692079439809L);
      String var5;
      if (enabled) {
         byte[] var10004 = new byte[7];
         var10004[0] = (byte)(95);
         var10004[1] = (byte)(-12);
         var10004[2] = (byte)(125);
         var10004[3] = (byte)(-68);
         var10004[4] = (byte)(-44);
         var10004[5] = (byte)(-40);
         var10004[6] = (byte)(89);
         var5 = $$v_s(var10004, -7260256692079439809L);
      } else {
         byte[] var6 = new byte[8];
         var6[0] = (byte)(94);
         var6[1] = (byte)(-13);
         var6[2] = (byte)(111);
         var6[3] = (byte)(-65);
         var6[4] = (byte)(-38);
         var6[5] = (byte)(-47);
         var6[6] = (byte)(88);
         var6[7] = (byte)(-41);
         var5 = $$v_s(var6, -7260256692079439809L);
      }

      pushNotificationInternal(var4, moduleName, desc, icon, var5, enabled, null, ToastManager.WeatherType.NONE);
   }

   public static void pushTop(String message, boolean enabled) {
      byte[] var10002 = new byte[6];
      var10002[0] = (byte)(105);
      var10002[1] = (byte)(-1);
      var10002[2] = (byte)(125);
      var10002[3] = (byte)(-84);
      var10002[4] = (byte)(-37);
      var10002[5] = (byte)(-43);
      pushInternal(message, null, $$v_s(var10002, -7260256692079439809L), enabled, true);
   }

   public static void pushStaff(String name, String role, boolean joined) {
      byte[] var10000 = new byte[5];
      var10000[0] = (byte)(73);
      var10000[1] = (byte)(-50);
      var10000[2] = (byte)(93);
      var10000[3] = (byte)(-104);
      var10000[4] = (byte)(-2);
      String var3 = $$v_s(var10000, -7260256692079439809L);
      byte[] var10003 = new byte[4];
      var10003[0] = (byte)(111);
      var10003[1] = (byte)(-23);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-84);
      String var4 = $$v_s(var10003, -7260256692079439809L);
      String var5;
      if (joined) {
         byte[] var10004 = new byte[6];
         var10004[0] = (byte)(80);
         var10004[1] = (byte)(-11);
         var10004[2] = (byte)(117);
         var10004[3] = (byte)(-80);
         var10004[4] = (byte)(-35);
         var10004[5] = (byte)(-39);
         var5 = $$v_s(var10004, -7260256692079439809L);
      } else {
         byte[] var6 = new byte[4];
         var6[0] = (byte)(86);
         var6[1] = (byte)(-1);
         var6[2] = (byte)(122);
         var6[3] = (byte)(-86);
         var5 = $$v_s(var6, -7260256692079439809L);
      }

      pushNotificationInternal(var3, name, role, var4, var5, joined, null, ToastManager.WeatherType.NONE);
   }

   public static void pushWeather(boolean raining) {
      String var1 = raining
         ? "Rain Starting"
         : "Clear Skies";
      String var2 = raining
         ? "World weather transitioned to rain"
         : "Skies have cleared up";
      String var3 = raining
         ? "cloud-rain"
         : "sun";
      String var4 = raining ? "Rain" : "Clear";
      synchronized (LOCK) {
         for (ToastManager.Toast var7 : toasts) {
            if (var7.weatherType != ToastManager.WeatherType.NONE
               || "WEATHER".equalsIgnoreCase(var7.prefix)) {
               var7.title = var1;
               var7.desc = var2;
               var7.icon = var3;
               var7.statusText = var4;
               var7.prefix = "WEATHER";
               var7.enabled = raining;
               var7.weatherType = raining ? ToastManager.WeatherType.RAIN : ToastManager.WeatherType.CLEAR;
               var7.dims = null;
               var7.startTime = System.currentTimeMillis();
               return;
            }
         }
      }

      pushNotificationInternal(
         "WEATHER",
         var1,
         var2,
         var3,
         var4,
         raining,
         null,
         raining ? ToastManager.WeatherType.RAIN : ToastManager.WeatherType.CLEAR
      );
   }

   public static void pushBlock(String title, String desc, class_1799 blockStack, boolean positive) {
      byte[] var10000 = new byte[5];
      var10000[0] = (byte)(88);
      var10000[1] = (byte)(-42);
      var10000[2] = (byte)(83);
      var10000[3] = (byte)(-99);
      var10000[4] = (byte)(-13);
      String var4 = $$v_s(var10000, -7260256692079439809L);
      byte[] var10003 = new byte[3];
      var10003[0] = (byte)(120);
      var10003[1] = (byte)(-11);
      var10003[2] = (byte)(100);
      String var5 = $$v_s(var10003, -7260256692079439809L);
      byte[] var10004 = new byte[5];
      var10004[0] = (byte)(92);
      var10004[1] = (byte)(-11);
      var10004[2] = (byte)(105);
      var10004[3] = (byte)(-80);
      var10004[4] = (byte)(-36);
      pushNotificationInternal(var4, title, desc, var5, $$v_s(var10004, -7260256692079439809L), positive, blockStack, ToastManager.WeatherType.NONE);
   }

   public static void pushNotification(String prefix, String title, String desc, String icon, String statusText, boolean positive) {
      ToastManager.WeatherType var6 = ToastManager.WeatherType.NONE;
      if (icon != null) {
         byte[] var10001 = new byte[4];
         var10001[0] = (byte)(104);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-80);
         if (icon.contains($$v_s(var10001, -7260256692079439809L))) {
            var6 = ToastManager.WeatherType.RAIN;
         } else {
            var10001 = new byte[3];
            var10001[0] = (byte)(105);
            var10001[1] = (byte)(-17);
            var10001[2] = (byte)(114);
            if (icon.contains($$v_s(var10001, -7260256692079439809L))) {
               var6 = ToastManager.WeatherType.CLEAR;
            }
         }
      }

      pushNotificationInternal(prefix, title, desc, icon, statusText, positive, null, var6);
   }

   public static void pushNotification(String prefix, String title, String desc, String icon, String statusText, boolean positive, class_1799 itemStack) {
      ToastManager.WeatherType var7 = ToastManager.WeatherType.NONE;
      if (icon != null) {
         byte[] var10001 = new byte[4];
         var10001[0] = (byte)(104);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-80);
         if (icon.contains($$v_s(var10001, -7260256692079439809L))) {
            var7 = ToastManager.WeatherType.RAIN;
         } else {
            var10001 = new byte[3];
            var10001[0] = (byte)(105);
            var10001[1] = (byte)(-17);
            var10001[2] = (byte)(114);
            if (icon.contains($$v_s(var10001, -7260256692079439809L))) {
               var7 = ToastManager.WeatherType.CLEAR;
            }
         }
      }

      pushNotificationInternal(prefix, title, desc, icon, statusText, positive, itemStack, var7);
   }

   private static void pushNotificationInternal(
      String prefix, String title, String desc, String icon, String statusText, boolean positive, class_1799 itemStack, ToastManager.WeatherType weatherType
   ) {
      synchronized (LOCK) {
         for (ToastManager.Toast var10 : toasts) {
            if (matchesTitle(var10.title, title)) {
               if (title != null && !title.isEmpty()) {
                  var10.title = title;
               }

               var10.enabled = positive;
               if (desc != null && !desc.trim().isEmpty()) {
                  var10.desc = desc;
               }

               if (icon != null && !icon.isEmpty()) {
                  var10.icon = icon;
               }

               if (prefix != null && !prefix.isEmpty()) {
                  var10.prefix = prefix;
               }

               if (statusText != null && !statusText.isEmpty()) {
                  var10.statusText = statusText;
               }

               if (itemStack != null) {
                  var10.itemIcon = itemStack;
               }

               if (weatherType != ToastManager.WeatherType.NONE) {
                  var10.weatherType = weatherType;
               }

               var10.startTime = System.currentTimeMillis();
               return;
            }
         }

         ToastManager.Toast var13 = new ToastManager.Toast(title, desc, icon, positive, false);
         var13.prefix = prefix;
         var13.statusText = statusText;
         var13.itemIcon = itemStack;
         var13.weatherType = weatherType;
         toasts.add(var13);

         while (toasts.size() > (8)) {
            toasts.remove(0);
         }
      }
   }

   private static void pushInternal(String title, String desc, String icon, boolean enabled, boolean isTop) {
      synchronized (LOCK) {
         for (ToastManager.Toast var7 : toasts) {
            if (matchesTitle(var7.title, title)) {
               if (title != null && !title.isEmpty()) {
                  var7.title = title;
               }

               var7.enabled = enabled;
               if (desc != null && !desc.trim().isEmpty()) {
                  var7.desc = desc;
               }

               if (icon != null && !icon.isEmpty()) {
                  var7.icon = icon;
               }

               var7.dims = null;
               var7.startTime = System.currentTimeMillis();
               return;
            }
         }

         toasts.add(new ToastManager.Toast(title, desc, icon, enabled, isTop));

         while (toasts.size() > (8)) {
            toasts.remove(0);
         }
      }
   }

   private static boolean isModernStyle() {
      return true;
   }

   public static boolean isNotifsEnabled() {
      ClientSettings var0 = ClientSettings.get();
      return var0 == null || var0.showNotifs();
   }

   private static ToastManager.ToastDims calcDims(ToastManager.Toast toast) {
      if (toast.dims != null) {
         return toast.dims;
      }

      ToastManager.ToastDims var1 = new ToastManager.ToastDims();
      if (isModernStyle() && !toast.isTop) {
         var1.hasDesc = toast.desc != null && !toast.desc.trim().isEmpty();
         float var2 = 7.2F;
         float var3 = 6.0F;
         float var4 = 5.6F;
         float var5 = 6.0F;
         String var6 = toast.prefix != null ? toast.prefix.trim() : "";
         String var7 = toast.statusText != null
            ? toast.statusText
            : (
               toast.enabled
                  ? "Enabled"
                  : "Disabled"
            );
         var1.tagW = !var6.isEmpty() ? NanoVGRenderer.getTextWidth(var6, var4, true) + 5.5F : 0.0F;
         var1.titleW = NanoVGRenderer.getTextWidth(toast.title, var2, true);
         var1.statusW = NanoVGRenderer.getTextWidth(var7, var5, true) + 7.0F;
         var1.descW = var1.hasDesc ? NanoVGRenderer.getTextWidth(toast.desc, var3) : 0.0F;
         float var8 = var1.tagW + var1.titleW + 5.0F + var1.statusW;
         float var9 = Math.max(var8, var1.descW);
         float var10 = 16.0F;
         float var11 = 5.0F;
         float var12 = var11 + var10 + 5.5F;
         var1.w = Math.max(130.0F, var12 + var9 + 7.5F);
         var1.h = var1.hasDesc ? 28.0F : 20.0F;
         var1.rad = 4.0F;
      } else if (toast.isTop) {
         var1.w = 140.0F;
         var1.h = 24.0F;
         var1.rad = 3.5F;
      } else {
         var1.w = 125.0F;
         var1.h = 19.0F;
         var1.rad = 3.5F;
      }

      toast.dims = var1;
      return var1;
   }

   private static void updatePositions(int screenW, int screenH, float dt) {
      float var3 = 5.5F;
      float var4 = 0.0F;
      float var5 = 0.0F;
      float var6 = 0.0F;
      boolean var7 = isModernStyle();

      for (ToastManager.Toast var9 : toasts) {
         ToastManager.ToastDims var10 = calcDims(var9);
         float var11;
         if (var7 && !var9.isTop) {
            var11 = 18.0F + var4;
            var4 += var10.h + var3;
         } else if (var9.isTop) {
            var11 = 8.0F + var5;
            var5 += var10.h + var3;
         } else {
            var6 += var10.h;
            var11 = screenH - 8.0F - var6;
            var6 += var3;
         }

         if (Float.isNaN(var9.curY) || AnimationManager.isOff()) {
            var9.curY = var11;
         } else if (dt > 0.0F) {
            var9.curY = AnimationManager.damp(var9.curY, var11, 18.0F, dt);
         }
      }
   }

   public static void blitKawase(int screenW, int screenH) {
      synchronized (LOCK) {
         if (!toasts.isEmpty() && isNotifsEnabled()) {
            updatePositions(screenW, screenH, 0.0F);
            boolean var3 = isModernStyle();

            for (ToastManager.Toast var5 : toasts) {
               float var6 = var5.getSlide();
               ToastManager.ToastDims var7 = calcDims(var5);
               float var8 = var7.w;
               float var9 = var7.h;
               float var10 = var7.rad;
               float var11;
               if (var3 && !var5.isTop) {
                  var11 = (screenW - var8) / 2.0F;
               } else {
                  var11 = screenW - var8 - 8.0F;
               }

               float var12 = var5.curY + var6;
               float var13 = var5.getScale();
               float var14 = var11 + var8 / 2.0F;
               float var15 = var12 + var9 / 2.0F;
               float var16 = var8 * var13;
               float var17 = var9 * var13;
               float var18 = var14 - var16 / 2.0F;
               float var19 = var15 - var17 / 2.0F;
               FrostedBackground.queueLogicalRect(var18, var19, var16, var17, var10 * var13, false);
            }
         }
      }
   }

   public static void render(int screenW, int screenH) {
      synchronized (LOCK) {
         toasts.removeIf(ToastManager.Toast::isDead);
         if (toasts.isEmpty() || !isNotifsEnabled()) {
            lastRenderMs = 0L;
            return;
         }
      }

      long var59 = System.currentTimeMillis();
      float var4 = lastRenderMs > 0L ? Math.min(0.1F, (float)(var59 - lastRenderMs) / 1000.0F) : 0.0F;
      lastRenderMs = var59;
      boolean var5 = isModernStyle();
      synchronized (LOCK) {
         updatePositions(screenW, screenH, var4);

         for (ToastManager.Toast var9 : new ArrayList(toasts)) {
            float var10 = var9.getAlpha();
            float var11 = var9.getSlide();
            ToastManager.ToastDims var12 = calcDims(var9);
            float var13 = var12.w;
            float var14 = var12.h;
            float var15 = var12.rad;
            float var16;
            if (var5 && !var9.isTop) {
               var16 = (screenW - var13) / 2.0F;
            } else {
               var16 = screenW - var13 - 8.0F;
            }

            float var17 = var9.curY + var11;
            int var18 = (int)(255.0F * var10);
            float var19 = var9.getScale();
            float var20 = var16 + var13 / 2.0F;
            float var21 = var17 + var14 / 2.0F;
            NanoVGRenderer.save();
            NanoVGRenderer.translate(var20, var21);
            NanoVGRenderer.scale(var19, var19);
            NanoVGRenderer.translate(-var20, -var21);
            int[] var22 = ThemeManager.bodyTop();
            int[] var23 = ThemeManager.bodyBot();
            int var24 = ThemeManager.bodyAlpha(var10);
            MaterialFX.glassBody(var16, var17, var13, var14, var15, var10);
            int var25 = var24 << 24 | var22[0] << 16 | var22[1] << 8 | var22[2];
            int var26 = var24 << 24 | var23[0] << 16 | var23[1] << 8 | var23[2];
            NanoVGRenderer.drawRoundedRectGradient(var16, var17, var13, var14, var15, var25, var26);
            int var27 = (int)VieClient.redColor.getValue();
            int var28 = (int)VieClient.greenColor.getValue();
            int var29 = (int)VieClient.blueColor.getValue();
            int var30 = var18 << 24 | var27 << 16 | var28 << 8 | var29;
            int var31;
            if (var9.weatherType == ToastManager.WeatherType.RAIN) {
               var31 = var18 << 24 | 3718648;
            } else if (var9.weatherType == ToastManager.WeatherType.CLEAR) {
               var31 = var18 << 24 | 16498468;
            } else if (var9.itemIcon != null && !var9.itemIcon.method_7960()) {
               var31 = var18 << 24 | 4906624;
            } else if (var9.enabled) {
               if (ThemeManager.isRainbowTheme()) {
                  var31 = RainbowUtil.rainbow(1.0, 0.88, 0L, var18);
               } else {
                  var31 = var18 << 24 | 4906624;
               }
            } else {
               var31 = var18 << 24 | 16281969;
            }

            int var32 = (int)(45.0F * var10) << 24 | var31 & 16777215;
            NanoVGRenderer.drawRoundedRectOutline(var16, var17, var13, var14, var15, 0.8F, var32);
            MaterialFX.innerTopRim(var16, var17, var13, var14, var15, 0.8F, (int)(10.0F * var10) << 24 | 16777215);
            if (var5 && !var9.isTop) {
               float var61 = 16.0F;
               float var62 = var16 + 5.0F;
               float var63 = var17 + (var14 - var61) / 2.0F;
               float var64 = 3.2F;
               int var37 = (int)(65.0F * var10) << 24 | 0;
               int var38 = (int)(40.0F * var10) << 24 | 16777215;
               NanoVGRenderer.drawRoundedRect(var62, var63, var61, var61, var64, var37);
               NanoVGRenderer.drawRoundedRectOutline(var62, var63, var61, var61, var64, 0.7F, var38);
               float var39 = var62 + var61 / 2.0F;
               float var40 = var63 + var61 / 2.0F;
               if (var9.weatherType == ToastManager.WeatherType.RAIN) {
                  drawRainAnimation(var39, var40, var10);
               } else if (var9.weatherType == ToastManager.WeatherType.CLEAR) {
                  drawSunAnimation(var39, var40, var10);
               } else if (var9.itemIcon != null && !var9.itemIcon.method_7960()) {
                  NVGImage.drawMCItem(var9.itemIcon, var62 + 2.0F, var63 + 2.0F, 12.0F, var10);
               } else {
                  float var41 = 9.0F;
                  float var42 = var62 + (var61 - var41) / 2.0F;
                  float var43 = var63 + (var61 - var41) / 2.0F;
                  IconManager.draw(var9.icon, var42, var43, var41, var18);
               }

               float var65 = var62 + var61 + 5.5F;
               float var66 = var12.hasDesc ? var17 + 4.0F : var17 + (var14 - 7.2F) / 2.0F;
               float var67 = var17 + 15.5F;
               float var44 = var65;
               String var45 = var9.prefix != null ? var9.prefix.trim().toUpperCase() : "";
               if (!var45.isEmpty()) {
                  float var46 = 9.0F;
                  float var47 = var66 - 0.8F;
                  int var48 = (int)(40.0F * var10) << 24 | 16777215;
                  int var49 = (int)(60.0F * var10) << 24 | 16777215;
                  NanoVGRenderer.drawRoundedRect(var44, var47, var12.tagW, var46, 2.2F, var48);
                  NanoVGRenderer.drawRoundedRectOutline(var44, var47, var12.tagW, var46, 2.2F, 0.6F, var49);
                  int var50 = ThemeManager.textDimArgb((int)(220.0F * var10));
                  NanoVGRenderer.drawText(var45, var44 + 2.8F, var47 + 1.5F, 5.6F, var50, true);
                  var44 += var12.tagW + 4.0F;
               }

               NanoVGRenderer.drawText(var9.title, var44, var66, 7.2F, var18 << 24 | 16777215, true);
               var44 += var12.titleW + 5.0F;
               String var69 = var9.statusText != null
                  ? var9.statusText
                  : (
                     var9.enabled
                        ? "Enabled"
                        : "Disabled"
                  );
               float var70 = 9.5F;
               float var71 = var66 - 1.0F;
               int var72 = (int)(35.0F * var10) << 24 | var31 & 16777215;
               int var73 = (int)(90.0F * var10) << 24 | var31 & 16777215;
               NanoVGRenderer.drawRoundedRect(var44, var71, var12.statusW, var70, 2.5F, var72);
               NanoVGRenderer.drawRoundedRectOutline(var44, var71, var12.statusW, var70, 2.5F, 0.6F, var73);
               NanoVGRenderer.drawText(var69, var44 + 3.5F, var71 + 1.8F, 6.0F, var31, true);
               if (var12.hasDesc) {
                  int var51 = (int)(210.0F * var10) << 24 | 13424354;
                  NanoVGRenderer.drawText(var9.desc, var65, var67, 6.0F, var51);
               }

               long var74 = System.currentTimeMillis() - var9.startTime;
               float var53 = Math.max(0.0F, Math.min(1.0F, 1.0F - (float)var74 / 3200.0F));
               if (var53 > 0.0F) {
                  float var54 = (var13 - 10.0F) * var53;
                  int var55 = (int)(110.0F * var10) << 24 | var31 & 16777215;
                  NanoVGRenderer.drawRoundedRect(var16 + 5.0F, var17 + var14 - 1.2F, var54, 0.9F, 0.45F, var55);
               }
            } else {
               float var33 = var16 + 8.0F;
               float var34 = var17 + var14 / 2.0F - 3.8F;
               String var35 = var9.statusText != null
                  ? var9.statusText
                  : (var9.enabled ? "ON" : "OFF");
               NanoVGRenderer.drawText(var35, var33, var34, 7.5F, var31, true);
               var33 += NanoVGRenderer.getTextWidth(var35, 7.5F, true) + 4.0F;
               String var36 = (var9.prefix != null ? var9.prefix : "") + var9.title + (var9.desc != null && !var9.desc.isEmpty() ? " (" + var9.desc + ")" : "");
               NanoVGRenderer.drawText(var36, var33, var34, 7.5F, ThemeManager.textArgb(var18), false);
            }

            NanoVGRenderer.restore();
         }
      }
   }

   private static void drawRainAnimation(float cx, float cy, float alpha) {
      long var3 = System.currentTimeMillis();
      float var5 = cy - 2.2F;
      int var6 = (int)(240.0F * alpha) << (24) | 15594231;
      int var7 = (int)(220.0F * alpha) << (24) | 10530496;
      NanoVGRenderer.drawRoundedRectGradient(cx - 5.5F, var5 + 0.3F, 11.0F, 4.2F, 2.0F, var6, var7);
      NanoVGRenderer.drawCircle(cx, var5 - 1.2F, 3.8F, var6);
      NanoVGRenderer.drawCircle(cx - 3.5F, var5 + 0.3F, 2.9F, var6);
      NanoVGRenderer.drawCircle(cx + 3.3F, var5, 3.1F, var6);
      int var8 = (int)(140.0F * alpha) << (24) | 16777215;
      NanoVGRenderer.drawCircle(cx, var5 - 1.2F, 3.8F, var8);
      float[] var10000 = new float[4];
      var10000[0] = -3.5F;
      var10000[1] = -1.2F;
      var10000[2] = 1.3F;
      var10000[3] = 3.7F;
      float[] var9 = var10000;
      long[] var24 = new long[4];
      var24[0] = 0L;
      var24[1] = 160L;
      var24[2] = 320L;
      var24[3] = 480L;
      long[] var10 = var24;
      float var11 = 6.0F;
      float var12 = var5 + 3.6F;

      for (int var13 = 0; var13 < (4); var13++) {
         long var14 = (var3 + var10[var13]) % 600L;
         float var16 = (float)var14 / 600.0F;
         float var17 = var12 + var16 * var11;
         float var18 = cx + var9[var13] - var16 * 1.5F;
         float var19 = -0.7F;
         float var20 = 2.2F;
         float var21 = 1.0F;
         if (var16 < 0.25F) {
            var21 = var16 / 0.25F;
         } else if (var16 > 0.7F) {
            var21 = (1.0F - var16) / 0.3F;
         }

         int var22 = (int)(240.0F * var21 * alpha);
         if (var22 > (4)) {
            int var23 = var22 << (24) | 3718648;
            NanoVGRenderer.drawLine(var18, var17, var18 + var19, var17 + var20, 1.0F, var23);
         }
      }
   }

   private static void drawSunAnimation(float cx, float cy, float alpha) {
      long var3 = System.currentTimeMillis();
      int var5 = (int)(55.0F * alpha) << (24) | 16752640;
      NanoVGRenderer.drawCircle(cx, cy, 6.0F, var5);
      float var6 = (float)(var3 % 7000L / 7000.0 * (Math.PI * 2));
      int var7 = (int)(230.0F * alpha) << (24) | 16498468;

      for (int var8 = 0; var8 < (8); var8++) {
         double var9 = var6 + var8 * (Math.PI / 4);
         float var11 = (float)Math.sin(var3 / 250.0 + var8) * 0.4F;
         float var12 = 4.4F;
         float var13 = 6.2F + var11;
         float var14 = cx + (float)Math.cos(var9) * var12;
         float var15 = cy + (float)Math.sin(var9) * var12;
         float var16 = cx + (float)Math.cos(var9) * var13;
         float var17 = cy + (float)Math.sin(var9) * var13;
         NanoVGRenderer.drawLine(var14, var15, var16, var17, 1.0F, var7);
      }

      int var18 = (int)(255.0F * alpha) << (24) | 16096779;
      int var19 = (int)(255.0F * alpha) << (24) | 16638023;
      NanoVGRenderer.drawCircle(cx, cy, 3.8F, var18);
      NanoVGRenderer.drawCircle(cx, cy - 0.4F, 3.0F, var19);
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

   static class Toast {
      String title;
      String desc;
      String icon;
      String prefix;
      String statusText;
      boolean enabled;
      long startTime;
      boolean isTop;
      float curY = Float.NaN;
      class_1799 itemIcon;
      ToastManager.WeatherType weatherType = ToastManager.WeatherType.NONE;
      ToastManager.ToastDims dims;
      static final long DURATION = 3200L;
      static final long FADE_IN = 340L;
      static final long FADE_OUT = 360L;
      static final long HOLD = 2500L;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Toast(String title, String desc, String icon, boolean enabled, boolean isTop) {
         this.title = titlex != null ? titlex : "";
         this.desc = descx;
         String var10001;
         if (iconx != null) {
            var10001 = iconx;
         } else {
            byte[] var6 = new byte[3];
            var6[0] = (byte)(95);
            var6[1] = (byte)(-29);
            var6[2] = (byte)(121);
            var10001 = $$v_s(var6, -1085624949286907873L);
         }

         this.icon = var10001;
         this.enabled = enabledx;
         this.startTime = System.currentTimeMillis();
         this.isTop = isTopx;
      }

      float getAlpha() {
         long var1 = System.currentTimeMillis() - this.startTime;
         if (var1 < 340L) {
            float var5 = (float)var1 / 340.0F;
            return ToastManager.easeOutCubic(var5);
         }

         if (var1 < 2840L) {
            return 1.0F;
         }

         long var3 = var1 - 340L - 2500L;
         return Math.max(0.0F, 1.0F - ToastManager.easeInCubic((float)var3 / 360.0F));
      }

      float getSlide() {
         if (AnimationManager.isOff()) {
            return 0.0F;
         } else {
            long var1 = System.currentTimeMillis() - this.startTime;
            if (var1 < 340L) {
               float var6 = (float)var1 / 340.0F;
               return (1.0F - ToastManager.easeOutCubic(var6)) * -10.0F;
            } else {
               long var3 = 2840L;
               if (var1 >= var3) {
                  float var5 = (float)(var1 - var3) / 360.0F;
                  return -ToastManager.easeInCubic(var5) * 8.0F;
               } else {
                  return 0.0F;
               }
            }
         }
      }

      float getScale() {
         if (AnimationManager.isOff()) {
            return 1.0F;
         } else {
            long var1 = System.currentTimeMillis() - this.startTime;
            if (var1 < 340L) {
               float var6 = (float)var1 / 340.0F;
               return 0.8F + 0.2F * ToastManager.easeOutBack(var6, 1.35F);
            } else {
               long var3 = 2840L;
               if (var1 >= var3) {
                  float var5 = (float)(var1 - var3) / 360.0F;
                  return 1.0F - 0.12F * ToastManager.easeInCubic(var5);
               } else {
                  return 1.0F;
               }
            }
         }
      }

      boolean isDead() {
         return System.currentTimeMillis() - this.startTime >= 3200L;
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << ('酪' ^ '駱'))) & (255) ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }

   static class ToastDims {
      float w;
      float h;
      float rad;
      float tagW;
      float titleW;
      float statusW;
      float descW;
      boolean hasDesc;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum WeatherType {
      NONE,
      RAIN,
      CLEAR;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ToastManager.WeatherType[] $values() {
         ToastManager.WeatherType[] var10000 = new ToastManager.WeatherType[3];
         var10000[0] = NONE;
         var10000[1] = RAIN;
         var10000[2] = CLEAR;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new ToastManager.WeatherType();
         var10002 = new byte[4];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         RAIN = new ToastManager.WeatherType();
         var10002 = new byte[5];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(-48);
         CLEAR = new ToastManager.WeatherType();
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
