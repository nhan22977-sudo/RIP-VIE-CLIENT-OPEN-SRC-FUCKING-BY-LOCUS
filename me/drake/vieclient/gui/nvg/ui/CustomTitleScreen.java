package me.drake.vieclient.gui.nvg.ui;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.adapter.modules.client.MusicHUD;
import me.drake.vieclient.gui.nvg.adapter.utils.SoundManager;
import me.drake.vieclient.gui.nvg.ui.components.GuiStyle;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.MaterialFX;
import me.drake.vieclient.render.nvg.NVGIcons;
import me.drake.vieclient.render.nvg.NVGRenderer;
import me.drake.vieclient.render.nvg.NanoVGFontManager;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.WindowIcon;
import net.minecraft.class_11909;
import net.minecraft.class_156;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_429;
import net.minecraft.class_437;
import net.minecraft.class_500;
import net.minecraft.class_526;
import org.lwjgl.nanovg.NVGPaint;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public final class CustomTitleScreen extends class_437 {
   private static final String[] LABELS = new String[]{
      $$v_s(new byte[]{46, -13, 114, -71, -114, -13, 124, -80, 116, -37, -127, -124}, -860389446876147624L),
      $$v_s(new byte[]{48, -17, 112, -86, -117, -26, 96, -67, 108, -57, -106}, -860389446876147624L),
      $$v_s(new byte[]{50, -22, 104, -73, -115, -8, 127}, -860389446876147624L),
      $$v_s(new byte[]{44, -17, 117, -86}, -860389446876147624L)
   };
   private static final String DISCORD_URL = "https://discord.gg/RhqaEw6FdR";
   private static final String WEBSITE_URL = "https://trillium.solutions";
   private static final String THEME_DIR = "assets/vie_client/vie/theme/";
   private final float[] rowAnim;
   private final float[] rowY;
   private float rowX;
   private float rowW;
   private float rowH;
   private final float[] webBtn;
   private final float[] dcBtn;
   private final float[] menuMusicBtn;
   private final float[] cleanSingleBtn;
   private final float[] cleanMultiBtn;
   private final float[] cleanModsBtn;
   private final float[] cleanOptionsBtn;
   private final float[] cleanDiscordBtn;
   private final float[] cleanWebBtn;
   private final float[] cleanMusicBtn;
   private final float[] cleanWpBtn;
   private final float[] cleanQuitBtn;
   private final float[] cleanLangBtn;
   private final float[] classicLangBtn;
   private long lastMs;
   private long openMs;
   private int lastMx;
   private int lastMy;
   private int logoImg;
   private boolean logoTried;
   private int logoW;
   private int logoH;
   private static final String LOGO_PATH = "assets/vie_client/vie/textures/icons/87388155473cd96ac940e88b608ad7da-removebg-preview.png";
   private String tipText;
   private float tipX;
   private float tipY;
   private static final Color CYAN = new Color(90, 220, 235);
   private static final Color PINK = new Color(240, 95, 150);
   private static final String[] MONTHS = new String[]{
      $$v_s(new byte[]{55, -37, 82, -117, -93, -60, 85}, -860389446876147624L),
      $$v_s(new byte[]{59, -33, 94, -116, -73, -41, 94, -123}, -860389446876147624L),
      $$v_s(new byte[]{48, -37, 78, -99, -86}, -860389446876147624L),
      $$v_s(new byte[]{60, -54, 78, -105, -82}, -860389446876147624L),
      $$v_s(new byte[]{48, -37, 69}, -860389446876147624L),
      $$v_s(new byte[]{55, -49, 82, -101}, -860389446876147624L),
      $$v_s(new byte[]{55, -49, 80, -121}, -860389446876147624L),
      $$v_s(new byte[]{60, -49, 91, -117, -79, -62}, -860389446876147624L),
      $$v_s(new byte[]{46, -33, 76, -118, -89, -37, 78, -103, 71}, -860389446876147624L),
      $$v_s(new byte[]{50, -39, 72, -111, -96, -45, 94}, -860389446876147624L),
      $$v_s(new byte[]{51, -43, 74, -101, -81, -44, 73, -114}, -860389446876147624L),
      $$v_s(new byte[]{57, -33, 95, -101, -81, -44, 73, -114}, -860389446876147624L)
   };
   private File wpDir;
   private List wpPaths;
   private final Map imgCache;
   private final Map dimCache;
   private boolean wpInit;
   private final float[] folderBtn;
   private int curWp;
   private int prevWp;
   private float wpFade;
   private boolean pickerOpen;
   private boolean pickerActive;
   private long pickerOpenMs;
   private long pickerCloseMs;
   private float pickerScroll;
   private float pickerScrollTarget;
   private long lastRightMs;
   private int lastCenterCard;
   private final List thumbRects;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public CustomTitleScreen() {
      byte[] var10001 = new byte[3];
      var10001[0] = (byte)(43);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(121);
      super(class_2561.method_43470($$v_s(var10001, -860389446876147624L)));
      this.rowAnim = new float[LABELS.length];
      this.rowY = new float[LABELS.length];
      this.webBtn = new float[4];
      this.dcBtn = new float[4];
      this.menuMusicBtn = new float[4];
      this.cleanSingleBtn = new float[4];
      this.cleanMultiBtn = new float[4];
      this.cleanModsBtn = new float[4];
      this.cleanOptionsBtn = new float[4];
      this.cleanDiscordBtn = new float[4];
      this.cleanWebBtn = new float[4];
      this.cleanMusicBtn = new float[4];
      this.cleanWpBtn = new float[4];
      this.cleanQuitBtn = new float[4];
      this.cleanLangBtn = new float[4];
      this.classicLangBtn = new float[4];
      this.lastMs = 0L;
      this.openMs = 0L;
      this.logoImg = -1;
      this.logoTried = false;
      this.logoW = 0;
      this.logoH = 0;
      this.tipText = null;
      this.imgCache = new HashMap();
      this.dimCache = new HashMap();
      this.wpInit = false;
      this.folderBtn = new float[4];
      this.curWp = 0;
      this.prevWp = -1;
      this.wpFade = 1.0F;
      this.pickerOpen = false;
      this.pickerActive = false;
      this.pickerOpenMs = 0L;
      this.pickerCloseMs = 0L;
      this.pickerScroll = 0.0F;
      this.pickerScrollTarget = 0.0F;
      this.lastRightMs = 0L;
      this.lastCenterCard = -999;
      this.thumbRects = new ArrayList();
   }

   private static float easeOutExpo(float t) {
      return t >= 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0, -10.0 * t);
   }

   private static float easeOutBack(float t) {
      float var1 = 1.70158F;
      float var2 = var1 + 1.0F;
      float var3 = t - 1.0F;
      return 1.0F + var2 * var3 * var3 * var3 + var1 * var3 * var3;
   }

   private static float clamp01(float v) {
      return Math.max(0.0F, Math.min(1.0F, v));
   }

   private static float pIn(long pe, float delay, float dur) {
      return clamp01(((float)pe - delay) / dur);
   }

   private static int vnFont() {
      int var0 = NanoVGFontManager.getTiengvietFont();
      return var0 >= 0 ? var0 : NanoVGFontManager.getRegularFont();
   }

   public boolean method_25422() {
      return false;
   }

   public void method_25420(class_332 ctx, int mouseX, int mouseY, float delta) {
      this.method_57734(ctx);
   }

   public void method_25432() {
      SoundManager.stopMenuMusic();
      GL30.glBindFramebuffer(36160, 0);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      GL11.glClear(16384);
      super.method_25432();
   }

   public void method_25394(class_332 ctx, int mouseX, int mouseY, float delta) {
      this.lastMx = mouseX;
      this.lastMy = mouseY;
      super.method_25394(ctx, mouseX, mouseY, delta);

      try {
         WindowIcon.apply(class_310.method_1551());
      } catch (Throwable var6) {
      }
   }

   public void renderPostBlit() {
      try {
         NanoVGRenderer.setUseTitleFont(true);
         this.renderNvg(this.lastMx, this.lastMy);
      } catch (Throwable var5) {
      } finally {
         NanoVGRenderer.setUseTitleFont(false);
      }
   }

   private void renderNvg(int mouseX, int mouseY) {
      float var3 = this.field_22789;
      float var4 = this.field_22790;
      long var5 = System.currentTimeMillis();
      float var7 = this.lastMs > 0L ? Math.min(0.1F, (float)(var5 - this.lastMs) / 1000.0F) : 0.0F;
      this.lastMs = var5;

      try (NanoVGFrame var8 = NanoVGRenderer.frame()) {
         if (!var8.isActive()) {
            return;
         }

         MusicHUD var9 = musicHud();
         if (var9 != null) {
            try {
               var9.pollForTitle();
            } catch (Throwable var30) {
            }
         }

         boolean var10 = !this.pickerActive && !SoundManager.isMenuMusicMuted();
         if (var10 && !SoundManager.isMenuMusicPlaying()) {
            SoundManager.startMenuMusic(0.45F);
         } else if (!var10 && SoundManager.isMenuMusicPlaying()) {
            SoundManager.stopMenuMusic();
         }

         Color var11 = accent();
         int var12 = var11.getRed();
         int var13 = var11.getGreen();
         int var14 = var11.getBlue();
         float var15 = var3 * 0.045F;
         float var16 = var4 * 0.07F;
         if (this.openMs == 0L) {
            this.openMs = var5;
         }

         long var17 = var5 - this.openMs;
         this.ensureWallpapers();
         float var19 = clamp01((float)var17 / 850.0F);
         float var20 = 1.0F + 0.035F * (0.5F + 0.5F * (float)Math.sin(var5 * 8.0E-5));
         float var21 = (float)Math.sin(var5 * 6.0E-5) * var3 * 0.012F;
         float var22 = (float)Math.cos(var5 * 5.0E-5) * var4 * 0.012F;
         float var23 = var20 * (1.0F + 0.12F * (1.0F - easeOutExpo(var19)));
         if (this.prevWp >= 0 && this.wpFade < 1.0F) {
            this.drawWallpaperCover(this.prevWp, var3, var4, 1.0F, var23, var21, var22);
         }

         this.drawWallpaperCover(this.curWp, var3, var4, (this.prevWp >= 0 ? this.wpFade : 1.0F) * (0.4F + 0.6F * var19), var23, var21, var22);
         if (this.wpFade < 1.0F) {
            this.wpFade = Math.min(1.0F, this.wpFade + var7 * 2.2F);
            if (this.wpFade >= 1.0F) {
               this.prevWp = -1;
            }
         }

         NanoVGRenderer.drawRoundedRectGradient(0.0F, 0.0F, var3, var4, 0.0F, 922746880, 2013265920);
         float var24 = Math.max(var3, var4);
         NanoVGRenderer.drawSquircleRadialGlow(0.0F, 0.0F, var3, var4, 0.0F, 0.0F, var4, var24 * 0.9F, 1174405120);
         NanoVGRenderer.drawSquircleRadialGlow(0.0F, 0.0F, var3, var4, 0.0F, var3, 0.0F, var24 * 0.9F, 1006632960);
         if (ThemeManager.isCleanStyle()) {
            this.renderCleanTitleScreen(var3, var4, mouseX, mouseY, var7, var5, var11);
            if (this.pickerActive) {
               this.renderPicker(var3, var4, mouseX, mouseY, var7, var5, var11);
            }

            return;
         }

         this.tipText = null;
         this.drawNav(var3, var4, var15, mouseX, mouseY, var5, var7, var11);
         this.drawTopRight(var3, var4, var15, var16, var5, var11, var17, mouseX, mouseY);
         this.drawNowListening(var3, var4, var15, var5, var11, mouseX, mouseY, var17);
         float var25 = clamp01((float)(var17 - 440L) / 620.0F);
         float var26 = easeOutBack(var25);
         float var27 = var15;
         float var28 = var4;
         NanoVGRenderer.save();
         NanoVGRenderer.translate((1.0F - var26) * -var3 * 0.07F, (1.0F - var26) * var4 * 0.12F);
         NanoVGRenderer.translate(var27, var28);
         NanoVGRenderer.scale(0.8F + 0.2F * var26, 0.8F + 0.2F * var26);
         NanoVGRenderer.translate(-var27, -var28);
         NVGRenderer.globalAlpha(var25);
         this.drawMiniPlayer(var3, var4, var15, var16, var11);
         NVGRenderer.globalAlpha(1.0F);
         NanoVGRenderer.restore();
         var25 = clamp01((float)(var17 - 820L) / 620.0F);
         var26 = easeOutBack(var25);
         var27 = var3 - var15 - var4 * 0.09F;
         var28 = var4 - var15 - var4 * 0.09F;
         NanoVGRenderer.save();
         NanoVGRenderer.translate(var27, var28);
         NanoVGRenderer.scale(var26, var26);
         NanoVGRenderer.translate(-var27, -var28);
         NVGRenderer.globalAlpha(var25);
         this.drawVisualizer(var3, var4, var15, var5, var11);
         NVGRenderer.globalAlpha(1.0F);
         NanoVGRenderer.restore();
         if (this.tipText != null) {
            this.drawTooltip(var4);
         }

         this.renderPicker(var3, var4, mouseX, mouseY, var7, var5, var11);
      }
   }

   private void ensureWallpapers() {
      if (!this.wpInit) {
         this.wpInit = true;

         try {
            File var1 = class_310.method_1551().field_1697;
            File var2 = new File(
               var1, $$v_s(new byte[]{11, -13, 121, -67, -114, -1, 105, -78, 97, -115, -109, -105, -42, 34, 84, -59, 93, 47, -66, -35}, -860389446876147624L)
            );
            File var3 = new File(
               var1, $$v_s(new byte[]{43, -13, 121, -2, -95, -6, 101, -71, 123, -42, -53, -127, -37, 34, 72, -44, 76, 58, -87, -36, 33}, -860389446876147624L)
            );
            File var4 = new File(
               var1, $$v_s(new byte[]{57, -11, 114, -85, -106, -44, 78, -97, 58, -43, -123, -102, -42, 62, 69, -44, 72, 56, -65}, -860389446876147624L)
            );
            if (!var2.exists() && var3.exists()) {
               this.wpDir = var3;
            } else if (!var2.exists() && var4.exists()) {
               this.wpDir = var4;
            } else {
               this.wpDir = var2;
            }

            this.wpDir.mkdirs();
            this.seedWallpapers(this.wpDir);
         } catch (Throwable var5) {
         }

         this.rescanWallpapers();
      }
   }

   private void rescanWallpapers() {
      ArrayList var1 = new ArrayList();

      try {
         File[] var2 = this.wpDir == null ? null : this.wpDir.listFiles();
         if (var2 != null) {
            for (File var6 : var2) {
               if (var6.isFile() && isImg(var6.getName())) {
                  var1.add(var6.getAbsolutePath());
               }
            }
         }
      } catch (Throwable var7) {
      }

      var1.sort(String::compareTo);

      for (int var8 = 0; var8 < var1.size(); var8++) {
         String var10000 = fileName((String)var1.get(var8)).toLowerCase();
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(9);
         var10001[1] = (byte)(-14);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-77);
         var10001[4] = (byte)(-121);
         var10001[5] = (byte)(-92);
         if (var10000.contains($$v_s(var10001, -860389446876147624L))) {
            Collections.swap(var1, 0, var8);
            break;
         }
      }

      this.wpPaths = var1;
      if (this.curWp >= this.wpPaths.size()) {
         this.curWp = 0;
      }
   }

   private void seedWallpapers(File dir) {
      for (String var3 : bundledThemeResources()) {
         File var4 = new File(dir, var3.substring(var3.lastIndexOf(47) + 1));
         if (!var4.exists()) {
            try (InputStream var5 = CustomTitleScreen.class.getClassLoader().getResourceAsStream(var3)) {
               if (var5 != null) {
                  Files.copy(var5, var4.toPath());
               }
            } catch (Throwable var10) {
            }
         }
      }
   }

   private static List bundledThemeResources() {
      ArrayList var0 = new ArrayList();

      try {
         File var1 = new File(CustomTitleScreen.class.getProtectionDomain().getCodeSource().getLocation().toURI());
         if (var1.isFile()) {
            try (JarFile var2 = new JarFile(var1)) {
               Enumeration var3 = var2.entries();

               while (var3.hasMoreElements()) {
                  String var4 = ((JarEntry)var3.nextElement()).getName();
                  if (var4.startsWith(
                        $$v_s(
                           new byte[]{
                              28,
                              -23,
                              111,
                              -69,
                              -106,
                              -27,
                              35,
                              -86,
                              124,
                              -57,
                              -69,
                              -107,
                              -42,
                              39,
                              65,
                              -54,
                              89,
                              101,
                              -70,
                              -57,
                              55,
                              73,
                              40,
                              -28,
                              -96,
                              127,
                              -15,
                              105
                           },
                           -860389446876147624L
                        )
                     )
                     && isImg(var4)
                     && var4.indexOf(
                           47,
                           $$v_s(
                                 new byte[]{
                                    28,
                                    -23,
                                    111,
                                    -69,
                                    -106,
                                    -27,
                                    35,
                                    -86,
                                    124,
                                    -57,
                                    -69,
                                    -107,
                                    -42,
                                    39,
                                    65,
                                    -54,
                                    89,
                                    101,
                                    -70,
                                    -57,
                                    55,
                                    73,
                                    40,
                                    -28,
                                    -96,
                                    127,
                                    -15,
                                    105
                                 },
                                 -860389446876147624L
                              )
                              .length()
                        )
                        < 0) {
                     var0.add(var4);
                  }
               }
            }
         } else {
            File[] var11 = new File(
                  var1,
                  $$v_s(
                     new byte[]{
                        28, -23, 111, -69, -106, -27, 35, -86, 124, -57, -69, -107, -42, 39, 65, -54, 89, 101, -70, -57, 55, 73, 40, -28, -96, 127, -15, 105
                     },
                     -860389446876147624L
                  )
               )
               .listFiles();
            if (var11 != null) {
               for (File var6 : var11) {
                  if (isImg(var6.getName())) {
                     var0.add("assets/vie_client/vie/theme/" + var6.getName());
                  }
               }
            }
         }
      } catch (Throwable var9) {
      }

      if (var0.isEmpty()) {
         for (String var16 : new String[]{
            $$v_s(new byte[]{9, -14, 121, -77, -121, -92, 34, -84, 123, -59}, -860389446876147624L),
            $$v_s(new byte[]{14, -10, 121, -69, -110, -72, 124, -78, 114}, -860389446876147624L),
            $$v_s(new byte[]{17, -76, 118, -82, -121, -15}, -860389446876147624L),
            $$v_s(new byte[]{14, -5, 119, -85, -112, -9, 34, -74, 101, -59}, -860389446876147624L)
         }) {
            var0.add("assets/vie_client/vie/theme/" + var16);
         }
      }

      return var0;
   }

   private static boolean isImg(String n) {
      n = n.toLowerCase();
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(83);
      var10001[1] = (byte)(-22);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-71);
      if (!n.endsWith($$v_s(var10001, -860389446876147624L))) {
         var10001 = new byte[4];
         var10001[0] = (byte)(83);
         var10001[1] = (byte)(-16);
         var10001[2] = (byte)(108);
         var10001[3] = (byte)(-71);
         if (!n.endsWith($$v_s(var10001, -860389446876147624L))) {
            var10001 = new byte[5];
            var10001[0] = (byte)(83);
            var10001[1] = (byte)(-16);
            var10001[2] = (byte)(108);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(-123);
            if (!n.endsWith($$v_s(var10001, -860389446876147624L))) {
               return false;
            }
         }
      }

      return true;
   }

   private static String fileName(String p) {
      int var1 = Math.max(p.lastIndexOf(47), p.lastIndexOf(92));
      return var1 >= 0 ? p.substring(var1 + 1) : p;
   }

   private int wpImage(int i) {
      if (this.wpPaths != null && i >= 0 && i < this.wpPaths.size()) {
         String var2 = (String)this.wpPaths.get(i);
         Integer var3 = (Integer)this.imgCache.get(var2);
         if (var3 != null) {
            return var3;
         }

         int var4 = NanoVGRenderer.loadImageFile(var2);
         this.imgCache.put(var2, var4);
         if (var4 >= 0) {
            int[] var5 = new int[1];
            int[] var6 = new int[1];
            NanoVG.nvgImageSize(NVGRenderer.getContext(), var4, var5, var6);
            int[] var10002 = new int[2];
            var10002[0] = var5[0];
            var10002[1] = var6[0];
            this.dimCache.put(var2, var10002);
         }

         return var4;
      } else {
         return -1;
      }
   }

   private int wpW(int i) {
      if (this.wpPaths != null && i >= 0 && i < this.wpPaths.size()) {
         int[] var2 = (int[])this.dimCache.get(this.wpPaths.get(i));
         return var2 != null ? var2[0] : 0;
      } else {
         return 0;
      }
   }

   private int wpH(int i) {
      if (this.wpPaths != null && i >= 0 && i < this.wpPaths.size()) {
         int[] var2 = (int[])this.dimCache.get(this.wpPaths.get(i));
         return var2 != null ? var2[1] : 0;
      } else {
         return 0;
      }
   }

   private void drawWallpaperCover(int i, float w, float h, float alpha, float zoom, float panX, float panY) {
      alpha = Math.max(0.0F, Math.min(1.0F, alpha));
      int var8 = this.wpImage(i);
      int var9 = this.wpW(i);
      int var10 = this.wpH(i);
      if (var8 >= 0 && var9 > 0 && var10 > 0) {
         float var11 = Math.max(w / var9, h / var10) * zoom;
         float var12 = var9 * var11;
         float var13 = var10 * var11;
         NanoVGRenderer.drawImage(
            var8, (w - var12) / 2.0F + panX, (h - var13) / 2.0F + panY, var12, var13, (int)(255.0F * alpha) << (24) | 16777215
         );
      } else {
         NanoVGRenderer.drawRoundedRectGradient(
            0.0F,
            0.0F,
            w,
            h,
            0.0F,
            (int)(255.0F * alpha) << (24) | 920596,
            (int)(255.0F * alpha) << (24) | 394507
         );
      }
   }

   private void openWallpaperFolder() {
      try {
         if (this.wpDir != null) {
            class_156.method_668().method_673(this.wpDir.toURI());
         }
      } catch (Throwable var2) {
      }
   }

   private void openPicker() {
      this.pickerActive = true;
      this.pickerOpen = true;
      this.pickerOpenMs = System.currentTimeMillis();
      this.rescanWallpapers();
      byte[] var10000 = new byte[11];
      var10000[0] = (byte)(30);
      var10000[1] = (byte)(-5);
      var10000[2] = (byte)(110);
      var10000[3] = (byte)(-86);
      var10000[4] = (byte)(-115);
      var10000[5] = (byte)(-8);
      var10000[6] = (byte)(83);
      var10000[7] = (byte)(-74);
      var10000[8] = (byte)(96);
      var10000[9] = (byte)(-49);
      var10000[10] = (byte)(-108);
      SoundManager.play($$v_s(var10000, -860389446876147624L));
   }

   private void closePicker() {
      this.pickerOpen = false;
      this.pickerCloseMs = System.currentTimeMillis();
   }

   private void selectWallpaper(int i) {
      if (i >= 0 && i < this.wpPaths.size() && i != this.curWp) {
         this.prevWp = this.curWp;
         this.curWp = i;
         this.wpFade = 0.0F;
      }

      this.closePicker();
      byte[] var10000 = new byte[12];
      var10000[0] = (byte)(28);
      var10000[1] = (byte)(-22);
      var10000[2] = (byte)(108);
      var10000[3] = (byte)(-69);
      var10000[4] = (byte)(-125);
      var10000[5] = (byte)(-28);
      var10000[6] = (byte)(83);
      var10000[7] = (byte)(-79);
      var10000[8] = (byte)(116);
      var10000[9] = (byte)(-59);
      var10000[10] = (byte)(-115);
      var10000[11] = (byte)(-107);
      SoundManager.play($$v_s(var10000, -860389446876147624L));
   }

   private void renderPicker(float w, float h, int mouseX, int mouseY, float dt, long now, Color ac) {
      this.thumbRects.clear();
      this.folderBtn[2] = 0.0F;
      if (this.pickerActive) {
         boolean var9 = !this.pickerOpen;
         long var10 = now - this.pickerOpenMs;
         long var12 = now - this.pickerCloseMs;
         if (var9 && var12 > 720L) {
            this.pickerActive = false;
         } else {
            this.pickerScroll = this.pickerScroll + (this.pickerScrollTarget - this.pickerScroll) * Math.min(1.0F, dt * 11.0F);
            float var14 = var9 ? clamp01(1.0F - (float)var12 / 520.0F) : clamp01((float)var10 / 260.0F);
            NanoVGRenderer.drawRect(0.0F, 0.0F, w, h, (int)(120.0F * var14) << 24 | 394506);
            float var15 = w * 0.5F;
            float var16 = h * 0.52F;
            float var17;
            float var18;
            if (var9) {
               float var19 = easeOutExpo(pIn(var12, 280.0F, 360.0F));
               var17 = 1.0F - var19;
               var18 = -var19 * h * 0.07F;
            } else {
               float var43 = pIn(var10, 0.0F, 520.0F);
               var17 = var43;
               var18 = -(1.0F - easeOutBack(var43)) * h * 0.08F;
            }

            String var44 = $$v_s(new byte[]{46, -33, 80, -101, -95, -62, 44, -117, 84, -18, -88, -90, -5, 30, 97, -10}, -860389446876147624L);
            float var20 = Math.max(9.0F, h * 0.02F);
            float var21 = NanoVGRenderer.getTextWidth(var44, var20);
            NVGRenderer.globalAlpha(var17);
            NanoVGRenderer.drawText(var44, (w - var21) / 2.0F, h * 0.16F + var18, var20, new Color(255, 255, 255, 235), true);
            NVGRenderer.globalAlpha(1.0F);
            var17 = Math.min(w * 0.24F, 340.0F);
            var18 = var17 * 0.52F;
            float var45 = var17 * 1.16F;
            int var46 = this.wpPaths == null ? 0 : this.wpPaths.size();
            int var47 = Math.round(this.pickerScroll);
            ArrayList var22 = new ArrayList();

            for (int var23 = 0; var23 < var46; var23++) {
               if (Math.abs(var23 - this.pickerScroll) <= 2.7F) {
                  var22.add(var23);
               }
            }

            var22.sort((p, q) -> Float.compare(Math.abs(q.intValue() - this.pickerScroll), Math.abs(p.intValue() - this.pickerScroll)));

            for (int var24 : var22) {
               float var25 = var24 - this.pickerScroll;
               float var26 = Math.abs(var25);
               float var27 = Math.max(0.74F, 1.0F - var26 * 0.14F);
               float var28 = var17 * var27;
               float var29 = var18 * var27;
               float var30 = var15 + var25 * var45 - var28 / 2.0F;
               float var31 = var16 - var29 / 2.0F;
               float var32 = Math.max(0.0F, 1.0F - var26 * 0.3F);
               boolean var33 = var26 < 0.5F;
               float var34;
               float var35;
               float var36;
               float var37;
               if (var9) {
                  float var38 = easeOutExpo(pIn(var12, 60.0F + var26 * 70.0F, 340.0F));
                  var34 = 1.0F - var38;
                  var35 = 1.0F - 0.4F * var38;
                  var36 = var33 ? 0.0F : Math.signum(var25) * var38 * w * 0.09F;
                  var37 = var33 ? var38 * h * 0.09F : 0.0F;
               } else {
                  float var72 = pIn(var10, 240.0F + var26 * 190.0F, 560.0F);
                  float var39 = easeOutExpo(var72);
                  float var40 = easeOutBack(var72);
                  var34 = var72;
                  var35 = 0.42F + 0.58F * var40;
                  var36 = var33 ? 0.0F : Math.signum(var25) * (1.0F - var39) * w * 0.11F;
                  var37 = var33 ? (1.0F - var39) * h * 0.11F : 0.0F;
               }

               float var73 = var30 + var28 / 2.0F;
               float var74 = var31 + var29 / 2.0F;
               NanoVGRenderer.save();
               NanoVGRenderer.translate(var73 + var36, var74 + var37);
               NanoVGRenderer.scale(var35, var35);
               NanoVGRenderer.translate(-var73, -var74);
               NVGRenderer.globalAlpha(clamp01(var34));
               this.drawThumb(var24, var30, var31, var28, var29, var32, var24 == var47, ac);
               NVGRenderer.globalAlpha(1.0F);
               NanoVGRenderer.restore();
               this.thumbRects.add(new float[]{var30, var31, var28, var29, var24});
            }

            float var49;
            float var53;
            if (var9) {
               float var57 = easeOutExpo(pIn(var12, 40.0F, 300.0F));
               var49 = 1.0F - var57;
               var53 = var57 * h * 0.08F;
            } else {
               float var58 = pIn(var10, 720.0F, 480.0F);
               var49 = var58;
               var53 = (1.0F - easeOutBack(var58)) * h * 0.09F;
            }

            String var59 = $$v_s(
               new byte[]{50, -22, 121, -80, -62, -63, 109, -80, 121, -46, -123, -122, -33, 60, 4, -30, 66, 38, -88, -53, 32}, -860389446876147624L
            );
            float var62 = Math.max(9.5F, h * 0.015F);
            float var64 = NanoVGRenderer.getTextWidth(var59, var62);
            float var65 = var62 * 1.1F;
            float var66 = var62 * 0.95F;
            float var67 = var66 + var65 + var62 * 0.5F + var64 + var66;
            float var68 = var62 + var66 * 1.1F;
            float var69 = (w - var67) / 2.0F;
            float var70 = h * 0.72F + var53;
            boolean var71 = var49 > 0.9F && mouseX >= var69 && mouseX <= var69 + var67 && mouseY >= var70 && mouseY <= var70 + var68;
            this.folderBtn[0] = var69;
            this.folderBtn[1] = h * 0.72F;
            this.folderBtn[2] = var67;
            this.folderBtn[3] = var68;
            NVGRenderer.globalAlpha(clamp01(var49));
            NanoVGRenderer.drawRoundedRect(var69, var70, var67, var68, 5.0F, new Color(255, 255, 255, var71 ? 32 : 16));
            NanoVGRenderer.drawRoundedRectOutline(
               var69, var70, var67, var68, 5.0F, 1.0F, new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), var71 ? 210 : 110)
            );
            this.drawFolderIcon(var69 + var66 + var65 / 2.0F, var70 + var68 / 2.0F, var65, ac);
            NanoVGRenderer.drawText(
               var59, var69 + var66 + var65 + var62 * 0.5F, var70 + (var68 - var62) / 2.0F, var62, new Color(255, 255, 255, var71 ? 245 : 220)
            );
            NVGRenderer.globalAlpha(1.0F);
            var49 = var9 ? 1.0F - clamp01(pIn(var12, 0.0F, 200.0F)) : pIn(var10, 900.0F, 420.0F);
            String var54 = $$v_s(
               new byte[]{
                  14,
                  -7,
                  110,
                  -79,
                  -114,
                  -6,
                  44,
                  -88,
                  122,
                  -126,
                  -122,
                  -124,
                  -43,
                  57,
                  87,
                  -63,
                  13,
                  106,
                  14,
                  25,
                  114,
                  70,
                  63,
                  -32,
                  -84,
                  113,
                  -1,
                  102,
                  30,
                  113,
                  84,
                  53,
                  -19,
                  74,
                  16,
                  7,
                  34,
                  22,
                  110,
                  -117,
                  -107,
                  -30,
                  54,
                  127,
                  -67,
                  -122,
                  -80,
                  41,
                  46,
                  -122,
                  69,
                  -83,
                  -103,
                  -90,
                  -124,
                  -34,
                  69,
                  -58,
                  -101,
                  -58,
                  -23,
                  -46,
                  123,
                  -57,
                  88
               },
               -860389446876147624L
            );
            float var60 = Math.max(9.5F, h * 0.012F);
            var62 = NanoVGRenderer.getTextWidth(var54, var60);
            NVGRenderer.globalAlpha(clamp01(var49));
            NanoVGRenderer.drawText(var54, (w - var62) / 2.0F, h * 0.83F, var60, new Color(255, 255, 255, 140));
            NVGRenderer.globalAlpha(1.0F);
            if (this.pickerOpen) {
               if (Math.abs(this.pickerScrollTarget - this.pickerScroll) < 0.08F) {
                  int var51 = -1;

                  for (float[] var61 : this.thumbRects) {
                     if (mouseX >= var61[0] && mouseX <= var61[0] + var61[2] && mouseY >= var61[1] && mouseY <= var61[1] + var61[3]) {
                        var51 = (int)var61[4];
                     }
                  }

                  int var56 = Math.round(this.pickerScroll);
                  if (var51 != -1 && var51 != var56) {
                     this.pickerScrollTarget = Math.max(
                        0, Math.min((this.wpPaths == null ? 1 : this.wpPaths.size()) - 1, var56 + Integer.signum(var51 - var56))
                     );
                  }
               }

               int var52 = Math.round(this.pickerScroll);
               if (var52 != this.lastCenterCard) {
                  if (this.lastCenterCard != -999) {
                     SoundManager.play($$v_s(new byte[]{9, -24, 125, -80, -111, -1, 120, -75, 122, -52}, -860389446876147624L));
                  }

                  this.lastCenterCard = var52;
               }
            } else {
               this.lastCenterCard = -999;
            }
         }
      }
   }

   private void drawFolderIcon(float cx, float cy, float s, Color c) {
      byte[] var10000 = new byte[6];
      var10000[0] = (byte)(27);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(112);
      var10000[3] = (byte)(-70);
      var10000[4] = (byte)(-121);
      var10000[5] = (byte)(-28);
      IconManager.draw($$v_s(var10000, -860389446876147624L), cx - s / 2.0F, cy - s / 2.0F, s, 255);
   }

   private void drawThumb(int i, float x, float y, float w2, float h2, float alpha, boolean center, Color ac) {
      float var9 = h2 * 0.1F;
      NanoVGRenderer.drawRoundedRect(x, y, w2, h2, var9, (int)(235.0F * alpha) << (24) | 1315356);
      int var10 = this.wpImage(i);
      int var11 = this.wpW(i);
      int var12 = this.wpH(i);
      if (var10 >= 0 && var11 > 0 && var12 > 0) {
         long var13 = NVGRenderer.getContext();
         float var15 = Math.max(w2 / var11, h2 / var12);
         float var16 = var11 * var15;
         float var17 = var12 * var15;
         float var18 = x + (w2 - var16) / 2.0F;
         float var19 = y + (h2 - var17) / 2.0F;
         NVGPaint var20 = NVGPaint.calloc();

         try {
            NanoVG.nvgImagePattern(var13, var18, var19, var16, var17, 0.0F, var10, alpha, var20);
            NanoVG.nvgBeginPath(var13);
            NanoVG.nvgRoundedRect(var13, x, y, w2, h2, var9);
            NanoVG.nvgFillPaint(var13, var20);
            NanoVG.nvgFill(var13);
         } finally {
            var20.free();
         }
      }

      if (center) {
         NanoVGRenderer.drawRoundedRectOutline(x, y, w2, h2, var9, 2.0F, (int)(235.0F * alpha) << (24) | ac.getRGB() & (16777215));
      } else {
         NanoVGRenderer.drawRoundedRectOutline(x, y, w2, h2, var9, 1.0F, (int)(70.0F * alpha) << (24) | 16777215);
      }

      if (i == this.curWp) {
         float var24 = h2 * 0.11F;
         NanoVGRenderer.drawCircle(
            x + w2 - var24 * 1.6F, y + var24 * 1.6F, var24, (int)(235.0F * alpha) << (24) | ac.getRGB() & (16777215)
         );
      }
   }

   private void drawTopRight(float w, float h, float padX, float padY, long now, Color ac, long el, int mouseX, int mouseY) {
      int var12 = ac.getRed();
      int var13 = ac.getGreen();
      int var14 = ac.getBlue();
      float var15 = w - padX;
      float var16 = padY;
      float var17 = pIn(el, 220.0F, 450.0F);
      float var18 = easeOutBack(var17);
      float var19 = pIn(el, 380.0F, 450.0F);
      float var20 = easeOutBack(var19);
      float var21 = pIn(el, 520.0F, 440.0F);
      float var22 = easeOutExpo(var21);
      if (!this.logoTried) {
         this.logoTried = true;
         this.logoImg = NanoVGRenderer.loadImage(
            $$v_s(
               new byte[]{
                  28,
                  -23,
                  111,
                  -69,
                  -106,
                  -27,
                  35,
                  -86,
                  124,
                  -57,
                  -69,
                  -107,
                  -42,
                  39,
                  65,
                  -54,
                  89,
                  101,
                  -70,
                  -57,
                  55,
                  73,
                  40,
                  -23,
                  -67,
                  102,
                  -31,
                  52,
                  15,
                  109,
                  91,
                  61,
                  -2,
                  85,
                  18,
                  13,
                  45,
                  14,
                  -101,
                  15,
                  -115,
                  -6,
                  117,
                  35,
                  -17,
                  -38,
                  -13,
                  55,
                  46,
                  -114,
                  21,
                  -8,
                  -109,
                  -27,
                  -59,
                  -40,
                  85,
                  -41,
                  -52,
                  -34,
                  -24,
                  -120,
                  36,
                  -116,
                  92,
                  62,
                  -21,
                  -6,
                  -61,
                  123,
                  62,
                  -7,
                  -72,
                  13,
                  -46,
                  -45,
                  24,
                  105,
                  73,
                  20,
                  -97,
                  111,
                  -6,
                  7,
                  119,
                  81,
                  -78,
                  60,
                  -21,
                  -75
               },
               -860389446876147624L
            )
         );
         if (this.logoImg != -1) {
            int[] var23 = new int[1];
            int[] var24 = new int[1];
            NanoVG.nvgImageSize(NVGRenderer.getContext(), this.logoImg, var23, var24);
            this.logoW = var23[0];
            this.logoH = var24[0];
         }
      }

      float var47 = Math.min(h * 0.13F, 96.0F);
      float var48 = this.logoH > 0 ? var47 * this.logoW / this.logoH : var47;
      float var25 = Math.min(w * 0.22F, 220.0F);
      if (var48 > var25) {
         var48 = var25;
         var47 = this.logoW > 0 ? var48 * this.logoH / this.logoW : var47;
      }

      NanoVGRenderer.save();
      NanoVGRenderer.translate(0.0F, (1.0F - var18) * -h * 0.08F);
      NVGRenderer.globalAlpha(var17);
      if (this.logoImg != -1) {
         float var26 = 1.0F + 0.02F * (float)Math.sin(now * 0.0013);
         float var27 = var15 - var48 / 2.0F;
         float var28 = var16 + var47 / 2.0F;
         NanoVGRenderer.save();
         NanoVGRenderer.translate(var27, var28);
         NanoVGRenderer.scale(var26, var26);
         NanoVGRenderer.translate(-var27, -var28);
         NanoVGRenderer.drawImage(this.logoImg, var15 - var48, var16, var48, var47, new Color(255, 255, 255, 255));
         NanoVGRenderer.restore();
      } else {
         float var49 = Math.max(12.0F, h * 0.03F);
         float var51 = NanoVGRenderer.getTextWidth($$v_s(new byte[]{43, -13, 121}, -860389446876147624L), var49);
         NanoVGRenderer.drawText($$v_s(new byte[]{43, -13, 121}, -860389446876147624L), var15 - var51, var16, var49, new Color(255, 255, 255, 230), true);
         float var53 = var49 * 0.7F;
         float var29 = NanoVGRenderer.getTextWidth($$v_s(new byte[]{62, -10, 117, -69, -116, -30}, -860389446876147624L), var53);
         NanoVGRenderer.drawText($$v_s(new byte[]{62, -10, 117, -69, -116, -30}, -860389446876147624L), var15 - var29, var16 + var49 + 2.0F, var53, CYAN);
         var47 = var49 + var53 + 6.0F;
      }

      NVGRenderer.globalAlpha(1.0F);
      NanoVGRenderer.restore();
      LocalDateTime var50 = LocalDateTime.now();
      int var52 = var50.getHour();
      int var54 = var52 % 12;
      if (var54 == 0) {
         var54 = 12;
      }

      String var55 = var52 < 12 ? $$v_s(new byte[]{60, -41}, -860389446876147624L) : $$v_s(new byte[]{45, -41}, -860389446876147624L);
      String var30 = String.valueOf(var54);
      String var31 = String.format($$v_s(new byte[]{88, -86, 46, -70}, -860389446876147624L), var50.getMinute());
      float var32 = Math.min(h * 0.072F, 38.0F);
      float var33 = var16 + var47 + h * 0.04F;
      float var34 = var32 * 0.42F;
      float var35 = NanoVGRenderer.getTextWidth(":", var32);
      float var36 = NanoVGRenderer.getTextWidth(" " + var55, var34);
      float var37 = NanoVGRenderer.getTextWidth(var30, var32);
      float var38 = NanoVGRenderer.getTextWidth(var31, var32);
      float var39 = var37 + var35 + var38 + var36;
      float var40 = var15 - var39;
      NanoVGRenderer.save();
      NanoVGRenderer.translate(0.0F, (1.0F - var20) * -h * 0.06F);
      NVGRenderer.globalAlpha(var19);
      int var41 = 369098752 | (var12 & 0xFF) << 16 | (var13 & 0xFF) << 8 | var14 & 0xFF;
      NanoVGRenderer.drawSquircleRadialGlow(0.0F, 0.0F, w, h, 0.0F, var15 - var39 * 0.4F, var33 + var32 * 0.5F, var32 * 2.2F, var41);
      NanoVGRenderer.drawText(var30, var40, var33, var32, -1);
      int var42 = (int)(255.0F * (0.45F + 0.55F * (0.5F + 0.5F * (float)Math.sin(now / 900.0))));
      NanoVGRenderer.drawText(":", var40 + var37, var33, var32, var42 << 24 | 16777215);
      NanoVGRenderer.drawText(var31, var40 + var37 + var35, var33, var32, -1);
      int var43 = -436207616 | (var12 & 0xFF) << 16 | (var13 & 0xFF) << 8 | var14 & 0xFF;
      NanoVGRenderer.drawText(var55, var40 + var37 + var35 + var38 + NanoVGRenderer.getTextWidth(" ", var34), var33 + var32 - var34, var34, var43, true);
      NVGRenderer.globalAlpha(1.0F);
      NanoVGRenderer.restore();
      String var44 = var50.getDayOfMonth() + " " + MONTHS[var50.getMonthValue() - 1] + " " + var50.getYear();
      float var45 = Math.max(9.5F, h * 0.016F);
      float var46 = NanoVGRenderer.getTextWidth(var44, var45);
      NVGRenderer.globalAlpha(var21);
      NanoVGRenderer.drawText(var44, var15 - var46, var33 + var32 + h * 0.012F + (1.0F - var22) * h * 0.015F, var45, -1090519041);
      NVGRenderer.globalAlpha(1.0F);
      this.drawMenuMusicButton(var40, var33, var32, h, ac, mouseX, mouseY, var19);
      this.drawMenuLanguageButton(var40, var33, var32, h, ac, mouseX, mouseY, var19);
   }

   private void drawMenuLanguageButton(float clockX, float clockY, float clockSize, float screenH, Color accent, int mouseX, int mouseY, float alpha) {
      float var9 = Math.max(34.0F, Math.min(38.0F, screenH * 0.056F));
      float var10 = var9;
      float var11 = var10 * 1.35F;
      float var12 = clockX - var9 - 10.0F;
      float var13 = var12 - var11 - 8.0F;
      float var14 = clockY + (clockSize - var10) / 2.0F;
      this.classicLangBtn[0] = var13;
      this.classicLangBtn[1] = var14;
      this.classicLangBtn[2] = alpha > 0.15F ? var11 : 0.0F;
      this.classicLangBtn[3] = alpha > 0.15F ? var10 : 0.0F;
      boolean var15 = hit(this.classicLangBtn, mouseX, mouseY);
      float var16 = 10.0F;
      int[] var17 = ThemeManager.bodyTop();
      int[] var18 = ThemeManager.bodyBot();
      int[] var19 = ThemeManager.borderRGB();
      int var20 = (int)(ThemeManager.bodyAlpha(alpha) * MaterialFX.bodyTintMul());
      MaterialFX.glassBody(var13, var14, var11, var10, var16, 1.0F * alpha);
      NanoVGRenderer.drawRoundedRectGradient(
         var13, var14, var11, var10, var16, new Color(var17[0], var17[1], var17[2], var20), new Color(var18[0], var18[1], var18[2], var20)
      );
      Color var21 = var15
         ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int)(210.0F * alpha))
         : new Color(var19[0], var19[1], var19[2], (int)(90.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var13, var14, var11, var10, var16, 1.0F, var21);
      float var22 = var10 * 0.44F;
      float var23 = var13 + 7.0F;
      float var24 = var14 + (var10 - var22) / 2.0F;
      Color var25 = var15 ? accent : new Color(255, 255, 255, (int)(220.0F * alpha));
      NVGIcons.globe(var23 + var22 / 2.0F, var24 + var22 / 2.0F, var22 / 2.0F, 1.0F, var25);
      String var26 = Localization.getLanguage().getCode();
      float var27 = Math.max(9.0F, var10 * 0.28F);
      NanoVGRenderer.drawText(
         var26, var23 + var22 + 4.5F, var14 + (var10 - var27) / 2.0F, var27, var15 ? Color.WHITE : ThemeManager.text((int)(230.0F * alpha)), true
      );
      if (var15) {
         this.tipText = Localization.getLanguageHint();
         this.tipX = mouseX;
         this.tipY = mouseY;
      }
   }

   private void drawMenuMusicButton(float clockX, float clockY, float clockSize, float screenH, Color accent, int mouseX, int mouseY, float alpha) {
      float var9 = Math.max(34.0F, Math.min(38.0F, screenH * 0.056F));
      float var10 = clockX - var9 - 10.0F;
      float var11 = clockY + (clockSize - var9) / 2.0F;
      this.menuMusicBtn[0] = var10;
      this.menuMusicBtn[1] = var11;
      this.menuMusicBtn[2] = alpha > 0.15F ? var9 : 0.0F;
      this.menuMusicBtn[3] = alpha > 0.15F ? var9 : 0.0F;
      boolean var12 = hit(this.menuMusicBtn, mouseX, mouseY);
      boolean var13 = SoundManager.isMenuMusicMuted();
      float var14 = 10.0F;
      int[] var15 = ThemeManager.bodyTop();
      int[] var16 = ThemeManager.bodyBot();
      int[] var17 = ThemeManager.borderRGB();
      int var18 = (int)(ThemeManager.bodyAlpha(alpha) * MaterialFX.bodyTintMul());
      MaterialFX.glassBody(var10, var11, var9, var9, var14, 1.0F * alpha);
      NanoVGRenderer.drawRoundedRectGradient(
         var10, var11, var9, var9, var14, new Color(var15[0], var15[1], var15[2], var18), new Color(var16[0], var16[1], var16[2], var18)
      );
      Color var19 = var12
         ? new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), (int)(210.0F * alpha))
         : new Color(var17[0], var17[1], var17[2], (int)(90.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var10, var11, var9, var9, var14, 1.0F, var19);
      float var20 = var9 * 0.52F;
      float var21 = var10 + var9 * 0.22F;
      float var22 = var11 + (var9 - var20) / 2.0F;
      float var23 = var22 + var20 / 2.0F;
      float var24 = Math.max(1.2F, var9 * 0.045F);
      Color var25 = new Color(255, 255, 255, (int)((var12 ? 255 : 210) * alpha));
      NanoVGRenderer.drawRoundedRect(var21, var23 - var20 * 0.15F, var20 * 0.18F, var20 * 0.3F, 1.0F, var25);
      NanoVGRenderer.drawTriangle(var21 + var20 * 0.12F, var23, var21 + var20 * 0.48F, var22 + var20 * 0.1F, var21 + var20 * 0.48F, var22 + var20 * 0.9F, var25);
      if (var13) {
         NanoVGRenderer.drawLine(
            var21 + var20 * 0.46F, var22 + var20 * 0.16F, var21 + var20 * 0.92F, var22 + var20 * 0.84F, var24, new Color(240, 95, 150, (int)(255.0F * alpha))
         );
      } else {
         float var26 = var21 + var20 * 0.58F;
         NanoVGRenderer.drawLine(var26, var22 + var20 * 0.28F, var26 + var20 * 0.16F, var23, var24, var25);
         NanoVGRenderer.drawLine(var26 + var20 * 0.16F, var23, var26, var22 + var20 * 0.72F, var24, var25);
         NanoVGRenderer.drawLine(var26 + var20 * 0.14F, var22 + var20 * 0.16F, var26 + var20 * 0.36F, var23, var24, var25);
         NanoVGRenderer.drawLine(var26 + var20 * 0.36F, var23, var26 + var20 * 0.14F, var22 + var20 * 0.84F, var24, var25);
      }

      if (var12) {
         this.tipText = var13
            ? $$v_s(new byte[]{45, -10, 125, -89, -62, -5, 105, -78, 96, -126, -119, -125, -55, 39, 71}, -860389446876147624L)
            : $$v_s(new byte[]{48, -17, 104, -69, -62, -5, 105, -78, 96, -126, -119, -125, -55, 39, 71}, -860389446876147624L);
         this.tipX = mouseX;
         this.tipY = mouseY;
      }
   }

   private void drawNowListening(float w, float h, float padX, long now, Color ac, int mouseX, int mouseY, long el) {
      int var11 = ac.getRed();
      int var12 = ac.getGreen();
      int var13 = ac.getBlue();
      float var14 = w - padX;
      float var15 = h * 0.48F;
      int var16 = vnFont();
      float var17 = pIn(el, 620.0F, 450.0F);
      float var18 = easeOutExpo(var17);
      String var19 = $$v_s(
         new byte[]{80, -70, 82, -111, -75, -74, 85, -109, 64, -123, -74, -77, -102, 2, 109, -9, 121, 15, -126, -25, 28, 33, 124, -95}, -860389446876147624L
      );
      float var20 = Math.max(9.5F, h * 0.0125F);
      float var21 = NanoVGRenderer.getTextWidth(var19, var20);
      NVGRenderer.globalAlpha(var17);
      NanoVGRenderer.drawText(var19, var14 - var21 + (1.0F - var18) * w * 0.08F, var15, var20, new Color(var11, var12, var13, 220));
      NVGRenderer.globalAlpha(1.0F);
      float var22 = pIn(el, 760.0F, 470.0F);
      float var23 = easeOutExpo(var22);
      String var24 = up(spTrack());
      float var25 = Math.max(11.0F, h * 0.027F);
      float var26 = var15 + h * 0.055F;
      float var27 = NanoVGRenderer.getTextWidthWithFont(var24, var25, var16);
      NVGRenderer.globalAlpha(var22);
      NanoVGRenderer.drawTextWithFont(var24, var14 - var27 + (1.0F - var23) * w * 0.1F, var26, var25, new Color(255, 255, 255, 255), var16);
      NVGRenderer.globalAlpha(1.0F);
      float var28 = pIn(el, 890.0F, 470.0F);
      float var29 = easeOutExpo(var28);
      String var30 = up(spArtist());
      float var31 = Math.max(9.5F, h * 0.014F);
      float var32 = var26 + var25 + h * 0.01F;
      float var33 = NanoVGRenderer.getTextWidthWithFont(var30, var31, var16);
      NVGRenderer.globalAlpha(var28);
      NanoVGRenderer.drawTextWithFont(var30, var14 - var33 + (1.0F - var29) * w * 0.1F, var32, var31, new Color(230, 235, 240, 200), var16);
      NVGRenderer.globalAlpha(1.0F);
      float var34 = pIn(el, 1020.0F, 460.0F);
      float var35 = easeOutExpo(var34);
      float var36 = var32 + var31 + h * 0.03F;
      float var37 = Math.max(22.0F, h * 0.038F);
      NanoVGRenderer.save();
      NanoVGRenderer.translate((1.0F - var35) * w * 0.09F, 0.0F);
      NVGRenderer.globalAlpha(var34);
      float var38 = var14;
      var38 = this.linkBtn(
         this.dcBtn,
         var38,
         var36,
         var37,
         $$v_s(new byte[]{57, -13, 111, -67, -115, -28, 104}, -860389446876147624L),
         new Color(88, 101, 242),
         true,
         hit(this.dcBtn, mouseX, mouseY)
      );
      var38 -= var37 * 0.4F;
      var38 = this.linkBtn(
         this.webBtn,
         var38,
         var36,
         var37,
         $$v_s(new byte[]{42, -1, 126, -83, -117, -30, 105}, -860389446876147624L),
         ac,
         false,
         hit(this.webBtn, mouseX, mouseY)
      );
      NVGRenderer.globalAlpha(1.0F);
      NanoVGRenderer.restore();
      if (hit(this.dcBtn, mouseX, mouseY)) {
         this.tipText = $$v_s(
            new byte[]{25, -13, 111, -67, -115, -28, 104, -14, 114, -59, -53, -62, -113, 58, 67, -36, 89, 11, -69, -44, 22}, -860389446876147624L
         );
         this.tipX = mouseX;
         this.tipY = mouseY;
      }

      if (hit(this.webBtn, mouseX, mouseY)) {
         this.tipText = $$v_s(new byte[]{11, -13, 121, -67, -114, -1, 105, -78, 97, -116, -121, -107}, -860389446876147624L);
         this.tipX = mouseX;
         this.tipY = mouseY;
      }
   }

   private float linkBtn(float[] rect, float rightX, float y, float bh, String label, Color tint, boolean discord, boolean hover) {
      float var9 = bh * 0.4F;
      float var10 = bh * 0.52F;
      float var11 = bh * 0.42F;
      float var12 = bh * 0.3F;
      float var13 = NanoVGRenderer.getTextWidth(label, var9);
      float var14 = var11 + var10 + var12 + var13 + var11;
      float var15 = rightX - var14;
      rect[0] = var15;
      rect[1] = y;
      rect[2] = var14;
      rect[3] = bh;
      float var16 = bh / 2.0F;
      MaterialFX.glassBody(var15, y, var14, bh, var16, 1.0F);
      NanoVGRenderer.drawRoundedRect(
         var15, y, var14, bh, var16, new Color(255, 255, 255, hover ? 22 : 12)
      );
      if (hover) {
         Color var17 = GuiStyle.accent2(255);
         NanoVGRenderer.drawRoundedRectGradient(
            var15,
            y,
            var14,
            bh,
            var16,
            new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), 95),
            new Color(var17.getRed(), var17.getGreen(), var17.getBlue(), 55)
         );
      }

      NanoVGRenderer.drawRoundedRectOutline(
         var15, y, var14, bh, var16, 1.0F, new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), hover ? 205 : 100)
      );
      NanoVGRenderer.drawRoundedRect(
         var15 + bh * 0.35F,
         y + 1.0F,
         var14 - bh * 0.7F,
         bh * 0.42F,
         var16,
         new Color(255, 255, 255, hover ? 20 : 11)
      );
      float var19 = var15 + var11 + var10 / 2.0F;
      float var18 = y + bh / 2.0F;
      if (discord) {
         this.drawDiscordIcon(var19, var18, var10, tint);
      } else {
         this.drawGlobeIcon(var19, var18, var10, tint);
      }

      NanoVGRenderer.drawText(
         label,
         var15 + var11 + var10 + var12,
         y + (bh - var9) / 2.0F,
         var9,
         new Color(255, 255, 255, hover ? 245 : 215)
      );
      return var15;
   }

   private void drawGlobeIcon(float cx, float cy, float s, Color c) {
      float var5 = s * 0.44F;
      float var6 = Math.max(1.0F, s * 0.075F);
      NanoVGRenderer.drawCircleOutline(cx, cy, var5, var6, c);
      NanoVGRenderer.drawLine(cx - var5, cy, cx + var5, cy, var6, c);
      NanoVGRenderer.drawLine(cx - var5 * 0.8F, cy - var5 * 0.5F, cx + var5 * 0.8F, cy - var5 * 0.5F, var6 * 0.85F, c);
      NanoVGRenderer.drawLine(cx - var5 * 0.8F, cy + var5 * 0.5F, cx + var5 * 0.8F, cy + var5 * 0.5F, var6 * 0.85F, c);
      NanoVGRenderer.drawLine(cx, cy - var5, cx, cy + var5, var6, c);
   }

   private void drawDiscordIcon(float cx, float cy, float s, Color c) {
      float var5 = s * 0.92F;
      float var6 = s * 0.72F;
      NanoVGRenderer.drawRoundedRect(cx - var5 / 2.0F, cy - var6 / 2.0F, var5, var6, var6 * 0.44F, c);
      NanoVGRenderer.drawRoundedRect(cx - var5 * 0.34F, cy + var6 * 0.32F, var5 * 0.24F, s * 0.14F, s * 0.06F, c);
      NanoVGRenderer.drawRoundedRect(cx + var5 * 0.1F, cy + var6 * 0.32F, var5 * 0.24F, s * 0.14F, s * 0.06F, c);
      Color var7 = new Color(255, 255, 255, 240);
      NanoVGRenderer.drawCircle(cx - var5 * 0.2F, cy - s * 0.02F, s * 0.1F, var7);
      NanoVGRenderer.drawCircle(cx + var5 * 0.2F, cy - s * 0.02F, s * 0.1F, var7);
   }

   private void drawTooltip(float h) {
      float var2 = Math.max(9.5F, h * 0.014F);
      float var3 = NanoVGRenderer.getTextWidth(this.tipText, var2);
      float var4 = var2 * 0.7F;
      float var5 = var3 + var4 * 2.0F;
      float var6 = var2 + var4 * 1.1F;
      float var7 = this.tipX - var5 - 6.0F;
      float var8 = this.tipY - var6 - 6.0F;
      if (var7 < 4.0F) {
         var7 = this.tipX + 12.0F;
      }

      if (var8 < 4.0F) {
         var8 = this.tipY + 14.0F;
      }

      NanoVGRenderer.drawRoundedRect(
         var7, var8, var5, var6, var6 * 0.3F, new Color(18, 16, 26, 240)
      );
      NanoVGRenderer.drawRoundedRectOutline(
         var7, var8, var5, var6, var6 * 0.3F, 1.0F, new Color(255, 255, 255, 45)
      );
      NanoVGRenderer.drawText(
         this.tipText,
         var7 + var4,
         var8 + (var6 - var2) / 2.0F,
         var2,
         new Color(255, 255, 255, 235)
      );
   }

   private void drawVisualizer(float w, float h, float padX, long now, Color ac) {
      boolean var7 = spPlaying();
      Color var8 = GuiStyle.accent2(255);
      float var9 = h * 0.048F;
      float var10 = h * 0.042F;
      float var11 = var9 + var10;
      float var12 = w - padX - var11;
      float var13 = h - padX - var11;
      float var14 = Math.max(1.6F, h * 0.0045F);
      long var15 = 500L;
      float var17 = (float)(now % var15) / (float)var15;
      float var18 = var7 ? (float)Math.pow(Math.max(0.0, 1.0 - var17 * 2.6), 1.3) : 0.0F;
      byte var19 = 44;

      for (int var20 = 0; var20 < var19; var20++) {
         double var21 = (double)var20 / var19 * Math.PI * 2.0 - (Math.PI / 2);
         float var23 = var7 ? 0.2F + 0.8F * Math.abs((float)Math.sin(now / 230.0 + var20 * 0.5)) : 0.16F;
         float var24 = Math.min(1.0F, var23 * 0.75F + var18 * 0.55F);
         float var25 = var10 * var24;
         float var26 = (float)Math.cos(var21);
         float var27 = (float)Math.sin(var21);
         int var28 = (int)(ac.getRed() + (var8.getRed() - ac.getRed()) * var24);
         int var29 = (int)(ac.getGreen() + (var8.getGreen() - ac.getGreen()) * var24);
         int var30 = (int)(ac.getBlue() + (var8.getBlue() - ac.getBlue()) * var24);
         NanoVGRenderer.drawLine(
            var12 + var26 * var9,
            var13 + var27 * var9,
            var12 + var26 * (var9 + var25),
            var13 + var27 * (var9 + var25),
            var14,
            new Color(var28, var29, var30, 230)
         );
      }

      float var31 = var9 * (0.72F + 0.12F * var18);
      NanoVGRenderer.drawSquircleRadialGlow(
         0.0F, 0.0F, w, h, 0.0F, var12, var13, var11 * 1.4F, new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), (int)(20.0F + 40.0F * var18))
      );
      NanoVGRenderer.drawCircleOutline(var12, var13, var31, var14, new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 220));
      NanoVGRenderer.drawCircle(var12, var13, var9 * (0.18F + 0.06F * var18), new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 200));
   }

   private void drawMiniPlayer(float w, float h, float padX, float padY, Color ac) {
      float var6 = Math.min(h * 0.14F, 96.0F);
      float var7 = Math.min(w * 0.235F, 300.0F);
      float var8 = padX;
      float var9 = h - padY * 0.6F - var6;
      float var10 = var6 * 0.16F;
      int var11 = vnFont();
      int[] var12 = ThemeManager.bodyTop();
      int[] var13 = ThemeManager.bodyBot();
      MaterialFX.glassBody(var8, var9, var7, var6, var10, 1.0F);
      int var14 = (int)(ThemeManager.bodyAlpha(1.0F) * MaterialFX.bodyTintMul());
      NanoVGRenderer.drawRoundedRectGradient(
         var8, var9, var7, var6, var10, new Color(var12[0], var12[1], var12[2], var14), new Color(var13[0], var13[1], var13[2], var14)
      );
      NanoVGRenderer.drawRoundedRectOutline(var8, var9, var7, var6, var10, 1.2F, new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 130));
      GuiStyle.gloss(var8, var9, var7, var6, var10, 1.0F);
      float var15 = var6 * 0.12F;
      float var16 = var6 - var15 * 2.0F;
      this.drawAlbumArt(var8 + var15, var9 + var15, var16, spImageId(), var16 * 0.16F, ac);
      float var17 = var8 + var15 + var16 + var6 * 0.14F;
      float var18 = var7 - (var17 - var8) - var15;
      float var19 = Math.max(9.5F, var6 * 0.16F);
      NanoVGRenderer.drawTextWithFont(clipVn(spTrack(), var18, var19, var11), var17, var9 + var6 * 0.24F, var19, new Color(255, 255, 255, 255), var11);
      float var20 = Math.max(9.5F, var6 * 0.135F);
      NanoVGRenderer.drawTextWithFont(clipVn(spArtist(), var18, var20, var11), var17, var9 + var6 * 0.46F, var20, PINK, var11);
      float var21 = var9 + var6 * 0.76F;
      float var22 = var6 * 0.16F;
      Color var23 = new Color(255, 255, 255, 210);
      float var24 = var17 + var22 * 0.6F;
      NanoVGRenderer.drawTriangle(var24 + var22, var21 - var22 * 0.5F, var24 + var22, var21 + var22 * 0.5F, var24, var21, var23);
      NanoVGRenderer.drawLine(var24, var21 - var22 * 0.5F, var24, var21 + var22 * 0.5F, 1.4F, var23);
      float var25 = var24 + var22 * 2.4F;
      if (spPlaying()) {
         NanoVGRenderer.drawRoundedRect(var25, var21 - var22 * 0.55F, var22 * 0.35F, var22 * 1.1F, 1.0F, var23);
         NanoVGRenderer.drawRoundedRect(var25 + var22 * 0.6F, var21 - var22 * 0.55F, var22 * 0.35F, var22 * 1.1F, 1.0F, var23);
      } else {
         NanoVGRenderer.drawTriangle(var25, var21 - var22 * 0.6F, var25, var21 + var22 * 0.6F, var25 + var22, var21, var23);
      }

      float var26 = var25 + var22 * 2.4F;
      NanoVGRenderer.drawTriangle(var26, var21 - var22 * 0.5F, var26, var21 + var22 * 0.5F, var26 + var22, var21, var23);
      NanoVGRenderer.drawLine(var26 + var22, var21 - var22 * 0.5F, var26 + var22, var21 + var22 * 0.5F, 1.4F, var23);
   }

   private void drawAlbumArt(float ax, float ay, float aSize, int imgId, float radius, Color ac) {
      long var7 = NVGRenderer.getContext();
      if (var7 != 0L) {
         if (imgId != (-1)) {
            NVGPaint var9 = NVGPaint.calloc();

            try {
               float var10 = 0.2F;
               float var11 = aSize / (1.0F - var10);
               float var12 = ax - (var11 - aSize) / 2.0F;
               NanoVG.nvgImagePattern(var7, var12, ay, var11, var11, 0.0F, imgId, 1.0F, var9);
               NanoVG.nvgBeginPath(var7);
               NanoVG.nvgRoundedRect(var7, ax, ay, aSize, aSize, radius);
               NanoVG.nvgFillPaint(var7, var9);
               NanoVG.nvgFill(var7);
            } finally {
               var9.free();
            }
         } else {
            NanoVGRenderer.drawRoundedRect(
               ax, ay, aSize, aSize, radius, new Color(34, 30, 42, 255)
            );
            float var16 = aSize * 0.5F;
            byte[] var10000 = new byte[5];
            var10000[0] = (byte)(16);
            var10000[1] = (byte)(-17);
            var10000[2] = (byte)(111);
            var10000[3] = (byte)(-73);
            var10000[4] = (byte)(-127);
            IconManager.draw($$v_s(var10000, -860389446876147624L), ax + (aSize - var16) / 2.0F, ay + (aSize - var16) / 2.0F, var16, 235);
         }

         NanoVGRenderer.drawRoundedRectOutline(ax, ay, aSize, aSize, radius, 1.2F, new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), 150));
      }
   }

   private void drawNav(float w, float h, float padX, int mouseX, int mouseY, long now, float dt, Color ac) {
      int var10 = ac.getRed();
      int var11 = ac.getGreen();
      int var12 = ac.getBlue();
      this.rowH = Math.max(20.0F, h * 0.076F);
      this.rowW = Math.min(w * 0.24F, 260.0F);
      this.rowX = padX;
      float var13 = (h - LABELS.length * this.rowH) / 2.0F - h * 0.06F;
      float var14 = Math.max(9.5F, h * 0.013F);
      NanoVGRenderer.drawText(
         $$v_s(new byte[]{48, -33, 82, -117}, -860389446876147624L), this.rowX + this.rowH * 0.05F, var13 - h * 0.03F, var14, new Color(255, 255, 255, 90)
      );

      for (int var15 = 0; var15 < LABELS.length; var15++) {
         float var16 = var13 + var15 * this.rowH;
         this.rowY[var15] = var16;
         boolean var17 = var15 == 0;
         boolean var18 = mouseX >= this.rowX && mouseX <= this.rowX + this.rowW && mouseY >= var16 && mouseY <= var16 + this.rowH;
         this.rowAnim[var15] = this.rowAnim[var15] + ((var18 ? 1.0F : 0.0F) - this.rowAnim[var15]) * Math.min(1.0F, dt * 14.0F);
         float var19 = this.rowAnim[var15];
         float var20 = clamp01((float)(now - this.openMs - 80L - var15 * 150) / 470.0F);
         float var21 = easeOutExpo(var20);
         float var22 = var19 * Math.max(3.0F, w * 0.005F);
         NanoVGRenderer.save();
         float var23 = this.rowX + this.rowW * 0.47F;
         float var24 = var16 + this.rowH / 2.0F;
         float var25 = 0.82F + 0.18F * var21;
         NanoVGRenderer.translate(var23 - (1.0F - var21) * w * 0.12F, var24);
         NanoVGRenderer.scale(var25, var25);
         NanoVGRenderer.translate(-var23, -var24);
         NVGRenderer.globalAlpha(var20);
         float var26 = var16 + this.rowH * 0.1F;
         float var27 = this.rowH * 0.8F;
         float var28 = this.rowW * 0.94F;
         float var29 = var27 * 0.5F;
         MaterialFX.glassBody(this.rowX, var26, var28, var27, var29, 1.0F);
         NanoVGRenderer.drawRoundedRect(this.rowX, var26, var28, var27, var29, new Color(255, 255, 255, var17 ? 15 : 8));
         if (var19 > 0.01F || var17) {
            int var30 = (int)((var17 ? 45 : 0) + 150.0F * var19);
            Color var31 = GuiStyle.accent2(255);
            NanoVGRenderer.drawRoundedRectGradient(
               this.rowX,
               var26,
               var28,
               var27,
               var29,
               new Color(var10, var11, var12, var30),
               new Color(var31.getRed(), var31.getGreen(), var31.getBlue(), (int)(var30 * 0.55F))
            );
         }

         NanoVGRenderer.drawRoundedRectOutline(this.rowX, var26, var28, var27, var29, 1.0F, new Color(255, 255, 255, (int)((var17 ? 42 : 22) + 60.0F * var19)));
         NanoVGRenderer.drawRoundedRect(
            this.rowX + var29 * 0.4F, var26 + 1.0F, var28 - var29 * 0.8F, var27 * 0.42F, var29, new Color(255, 255, 255, (int)(10.0F + 10.0F * var19))
         );
         float var38 = this.rowH * 0.38F;
         float var39 = this.rowX + this.rowH * 0.28F + var22;
         float var32 = var16 + (this.rowH - var38) / 2.0F;
         int var33 = (int)((var17 ? 150 : 75) + 180.0F * var19);
         Color var34 = var17 ? new Color(var10, var11, var12, Math.min(255, var33)) : new Color(255, 255, 255, Math.min(255, var33));
         this.drawIcon(var15, var39, var32, var38, var34);
         float var35 = Math.max(9.0F, h * 0.022F);
         int var36 = (int)((var17 ? 190 : 95) + (255 - (var17 ? 190 : 95)) * var19);

         String var37 = switch (var15) {
            case 0 -> Localization.get(
               $$v_s(new byte[]{9, -13, 104, -78, -121, -72, 127, -75, 123, -59, -120, -109, -54, 34, 69, -35, 72, 56}, -860389446876147624L), LABELS[0]
            );
            case 1 -> Localization.get(
               $$v_s(new byte[]{9, -13, 104, -78, -121, -72, 97, -87, 121, -42, -115, -122, -42, 47, 93, -63, 95}, -860389446876147624L), LABELS[1]
            );
            case 2 -> Localization.get($$v_s(new byte[]{9, -13, 104, -78, -121, -72, 99, -84, 97, -53, -117, -104, -55}, -860389446876147624L), LABELS[2]);
            case 3 -> Localization.get($$v_s(new byte[]{9, -13, 104, -78, -121, -72, 125, -87, 124, -42}, -860389446876147624L), LABELS[3]);
            default -> LABELS[var15];
         };
         NanoVGRenderer.drawText(
            var37, var39 + var38 + w * 0.011F, var16 + (this.rowH - var35) / 2.0F, var35, new Color(255, 255, 255, Math.min(255, var36)), var19 > 0.4F || var17
         );
         NVGRenderer.globalAlpha(1.0F);
         NanoVGRenderer.restore();
      }
   }

   private void drawIcon(int i, float x, float y, float s, Color c) {
      float var6 = x + s / 2.0F;
      float var7 = y + s / 2.0F;
      float var8 = Math.max(1.0F, s * 0.1F);
      switch (i) {
         case 0:
            NanoVGRenderer.drawTriangle(x + s * 0.15F, y, x + s * 0.15F, y + s, x + s, var7, c);
            break;
         case 1:
            NanoVGRenderer.drawCircleOutline(var6, var7, s * 0.46F, var8, c);
            NanoVGRenderer.drawLine(var6 - s * 0.46F, var7, var6 + s * 0.46F, var7, var8, c);
            NanoVGRenderer.drawLine(var6, var7 - s * 0.46F, var6, var7 + s * 0.46F, var8, c);
            break;
         case 2:
            NanoVGRenderer.drawCircleOutline(var6, var7, s * 0.3F, var8, c);
            NanoVGRenderer.drawCircle(var6, var7, s * 0.1F, c);

            for (int var9 = 0; var9 < (8); var9++) {
               double var10 = var9 * Math.PI / 4.0;
               float var12 = (float)Math.cos(var10);
               float var13 = (float)Math.sin(var10);
               NanoVGRenderer.drawLine(var6 + var12 * s * 0.32F, var7 + var13 * s * 0.32F, var6 + var12 * s * 0.48F, var7 + var13 * s * 0.48F, var8, c);
            }
            break;
         default:
            NanoVGRenderer.drawLine(var6 + s * 0.1F, y + s * 0.12F, var6 + s * 0.1F, y + s * 0.88F, var8, c);
            NanoVGRenderer.drawLine(var6 - s * 0.5F, var7, var6 + s * 0.05F, var7, var8, c);
            NanoVGRenderer.drawLine(var6 - s * 0.5F, var7, var6 - s * 0.2F, var7 - s * 0.28F, var8, c);
            NanoVGRenderer.drawLine(var6 - s * 0.5F, var7, var6 - s * 0.2F, var7 + s * 0.28F, var8, c);
      }
   }

   private void renderCleanTitleScreen(float w, float h, int mouseX, int mouseY, float dt, long now, Color ac) {
      long var9 = now - this.openMs;
      float var11 = easeOutExpo(clamp01((float)var9 / 650.0F));
      float var12 = (1.0F - var11) * 30.0F;
      float var13 = var11;
      int[] var14 = ThemeManager.bodyTop();
      int[] var15 = ThemeManager.bodyBot();
      int[] var16 = ThemeManager.borderRGB();
      Color var17 = GuiStyle.accent2(255);
      float var18 = easeOutExpo(clamp01((float)(var9 - 60L) / 500.0F));
      float var19 = (1.0F - var18) * -16.0F;
      float var20 = 14.0F + var19;
      float var21 = var13 * var18;
      if (var21 > 0.01F) {
         LocalDateTime var22 = LocalDateTime.now();
         String var23 = String.format("%02d:%02d", var22.getHour(), var22.getMinute());
         String var24 = String.format("%s, %02d %s", var22.getDayOfWeek().name(), var22.getDayOfMonth(), MONTHS[var22.getMonthValue() - 1]);
         String var25 = var23 + "  •  " + var24;
         float var26 = NanoVGRenderer.getTextWidth(var25, 8.0F) + 20.0F;
         float var27 = 22.0F;
         float var28 = 18.0F;
         float var29 = 4.0F;
         MaterialFX.glassBody(var28, var20, var26, var27, var29, var21 * 0.7F);
         NanoVGRenderer.drawRoundedRect(var28, var20, var26, var27, var29, new Color(var14[0], var14[1], var14[2], (int)(100.0F * var21)));
         NanoVGRenderer.drawText(var25, var28 + 10.0F, var20 + (var27 - 8.0F) / 2.0F, 8.0F, new Color(220, 220, 230, (int)(200.0F * var21)));
         class_310 var30 = this.field_22787;
         String var31 = var30 != null && var30.method_1548() != null ? var30.method_1548().method_1676() : "Player";
         int var32 = var30 != null ? var30.method_47599() : 60;
         String var33 = var31 + "  •  " + var32 + " FPS";
         float var34 = NanoVGRenderer.getTextWidth(var33, 8.0F) + 26.0F;
         float var35 = 22.0F;
         float var36 = w - var34 - 18.0F;
         MaterialFX.glassBody(var36, var20, var34, var35, var29, var21 * 0.7F);
         NanoVGRenderer.drawRoundedRect(var36, var20, var34, var35, var29, new Color(var14[0], var14[1], var14[2], (int)(100.0F * var21)));
         NanoVGRenderer.drawCircle(var36 + 9.0F, var20 + var35 / 2.0F, 2.2F, new Color(80, 225, 120, (int)(230.0F * var21)));
         NanoVGRenderer.drawText(var33, var36 + 17.0F, var20 + (var35 - 8.0F) / 2.0F, 8.0F, new Color(220, 220, 230, (int)(200.0F * var21)));
      }

      float var68 = Math.min(320.0F, w - 32.0F);
      float var69 = 350.0F;
      float var70 = w / 2.0F;
      float var71 = var70 - var68 / 2.0F;
      float var72 = (h - var69) / 2.0F + var12;
      float var73 = 12.0F;
      GuiStyle.shadowSquircle(var71, var72, var68, var69, var73, var13 * 0.7F);
      MaterialFX.glassBodySquircle(var71, var72, var68, var69, var73, 1.0F * var13);
      NanoVGRenderer.drawSquircleGradient(
         var71,
         var72,
         var68,
         var69,
         var73,
         new Color(var14[0], var14[1], var14[2], (int)(170.0F * var13)),
         new Color(var15[0], var15[1], var15[2], (int)(170.0F * var13))
      );
      GuiStyle.glossSquircle(var71, var72, var68, var69, var73, var13 * 0.5F);
      NanoVGRenderer.drawRoundedRectGradient(
         var71 + var68 * 0.25F, var72 + 1.0F, var68 * 0.5F, 1.5F, 0.75F, GuiStyle.accent((int)(180.0F * var13)), GuiStyle.accent2((int)(180.0F * var13))
      );
      float var74 = 36.0F;
      float var75 = var71 + (var68 - var74) / 2.0F;
      float var76 = var72 + 16.0F;
      IconManager.draw("logovie", var75, var76, var74, (int)(255.0F * var13));
      String var77 = "VIE CLIENT";
      float var78 = NanoVGRenderer.getTextWidth(var77, 13.5F);
      NanoVGRenderer.drawText(var77, var71 + (var68 - var78) / 2.0F, var76 + var74 + 6.0F, 13.5F, new Color(255, 255, 255, (int)(250.0F * var13)), true);
      String var79 = "v1.0.0 • Fabric 1.21.11";
      float var80 = NanoVGRenderer.getTextWidth(var79, 7.5F);
      NanoVGRenderer.drawText(var79, var71 + (var68 - var80) / 2.0F, var76 + var74 + 23.0F, 7.5F, ThemeManager.textDim((int)(160.0F * var13)));
      float var81 = 46.0F;
      float var82 = 16.0F;
      float var37 = var71 + var68 - var81 - 14.0F;
      float var38 = var72 + 14.0F;
      this.cleanLangBtn[0] = var37;
      this.cleanLangBtn[1] = var38;
      this.cleanLangBtn[2] = var81;
      this.cleanLangBtn[3] = var82;
      boolean var39 = mouseX >= var37 && mouseX <= var37 + var81 && mouseY >= var38 && mouseY <= var38 + var82;
      MaterialFX.glassBody(var37, var38, var81, var82, var82 / 2.0F, var13);
      NanoVGRenderer.drawRoundedRect(var37, var38, var81, var82, var82 / 2.0F, new Color(var14[0], var14[1], var14[2], (int)((var39 ? 190 : 100) * var13)));
      NanoVGRenderer.drawRoundedRectOutline(var37, var38, var81, var82, var82 / 2.0F, 1.0F, var39 ? ac : new Color(255, 255, 255, (int)(40.0F * var13)));
      float var40 = 9.5F;
      float var41 = var37 + 5.5F;
      float var42 = var38 + (var82 - var40) / 2.0F;
      if (IconManager.has("language")) {
         IconManager.draw("language", var41, var42, var40, (int)((var39 ? 255 : 200) * var13));
      } else {
         NVGIcons.globe(var41 + var40 / 2.0F, var42 + var40 / 2.0F, var40 / 2.0F, 1.0F, var39 ? ac : new Color(255, 255, 255, (int)(200.0F * var13)));
      }

      String var43 = Localization.getLanguage().getCode();
      NanoVGRenderer.drawText(var43, var37 + 18.0F, var38 + (var82 - 7.5F) / 2.0F, 7.5F, var39 ? Color.WHITE : ThemeManager.text((int)(220.0F * var13)), true);
      if (var39) {
         this.tipText = Localization.getLanguageHint();
         this.tipX = mouseX;
         this.tipY = mouseY;
      }

      float var44 = var76 + var74 + 35.0F;
      NanoVGRenderer.drawRoundedRect(var71 + 20.0F, var44, var68 - 40.0F, 1.0F, 0.5F, new Color(var16[0], var16[1], var16[2], (int)(30.0F * var13)));
      String[] var45 = new String[]{
         Localization.get("title.singleplayer", "Singleplayer"),
         Localization.get("title.multiplayer", "Multiplayer"),
         Localization.get("title.mods", "Mods & Addons"),
         Localization.get("title.options", "Options"),
         Localization.get("title.quit", "Quit Game")
      };
      float var46 = var44 + 8.0F;
      float var47 = 35.0F;
      float var48 = 4.5F;
      float var49 = var68 - 24.0F;
      float var50 = var71 + 12.0F;
      float var51 = 10.0F;

      for (int var52 = 0; var52 < 5; var52++) {
         float var53 = clamp01((float)(var9 - 100L - var52 * 40) / 320.0F);
         float var54 = var46 + var52 * (var47 + var48);
         float var55 = var13 * var53;
         if (var52 == 0) {
            this.cleanSingleBtn[0] = var50;
            this.cleanSingleBtn[1] = var54;
            this.cleanSingleBtn[2] = var49;
            this.cleanSingleBtn[3] = var47;
         } else if (var52 == 1) {
            this.cleanMultiBtn[0] = var50;
            this.cleanMultiBtn[1] = var54;
            this.cleanMultiBtn[2] = var49;
            this.cleanMultiBtn[3] = var47;
         } else if (var52 == 2) {
            this.cleanModsBtn[0] = var50;
            this.cleanModsBtn[1] = var54;
            this.cleanModsBtn[2] = var49;
            this.cleanModsBtn[3] = var47;
         } else if (var52 == 3) {
            this.cleanOptionsBtn[0] = var50;
            this.cleanOptionsBtn[1] = var54;
            this.cleanOptionsBtn[2] = var49;
            this.cleanOptionsBtn[3] = var47;
         } else {
            this.cleanQuitBtn[0] = var50;
            this.cleanQuitBtn[1] = var54;
            this.cleanQuitBtn[2] = var49;
            this.cleanQuitBtn[3] = var47;
         }

         boolean var56 = mouseX >= var50 && mouseX <= var50 + var49 && mouseY >= var54 && mouseY <= var54 + var47;
         boolean var57 = var52 == 4;
         MaterialFX.glassBody(var50, var54, var49, var47, var51, var55);
         int var58 = var56 ? (int)(180.0F * var55) : (int)(100.0F * var55);
         NanoVGRenderer.drawRoundedRect(var50, var54, var49, var47, var51, new Color(var14[0], var14[1], var14[2], var58));
         if (var56) {
            if (var57) {
               NanoVGRenderer.drawRoundedRect(var50, var54, var49, var47, var51, new Color(240, 70, 70, (int)(20.0F * var55)));
            } else {
               NanoVGRenderer.drawRoundedRectGradient(
                  var50,
                  var54,
                  var49,
                  var47,
                  var51,
                  new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), (int)(30.0F * var55)),
                  new Color(var17.getRed(), var17.getGreen(), var17.getBlue(), (int)(15.0F * var55))
               );
            }
         }

         float var59 = var50 + 15.0F;
         float var60 = var54 + var47 / 2.0F;
         Color var61 = var56
            ? (var57 ? new Color(255, 100, 100) : ac)
            : (var57 ? new Color(220, 80, 80, (int)(180.0F * var55)) : new Color(200, 200, 215, (int)(180.0F * var55)));
         if (var52 == 0) {
            NanoVGRenderer.drawTriangle(var59 - 3.5F, var60 - 4.5F, var59 - 3.5F, var60 + 4.5F, var59 + 4.5F, var60, var61);
         } else if (var52 == 1) {
            IconManager.draw("user", var59 - 5.5F, var60 - 5.5F, 11.0F, (int)((var56 ? 255 : 180) * var55));
         } else if (var52 == 2) {
            float var62 = 3.5F;
            float var63 = 2.0F;
            float var64 = var59 - var62 - var63 / 2.0F;
            float var65 = var60 - var62 - var63 / 2.0F;
            NanoVGRenderer.drawRoundedRect(var64, var65, var62, var62, 1.0F, var61);
            NanoVGRenderer.drawRoundedRect(var64 + var62 + var63, var65, var62, var62, 1.0F, var61);
            NanoVGRenderer.drawRoundedRect(var64, var65 + var62 + var63, var62, var62, 1.0F, var61);
            NanoVGRenderer.drawRoundedRect(var64 + var62 + var63, var65 + var62 + var63, var62, var62, 1.0F, var61);
         } else if (var52 == 3) {
            NanoVGRenderer.drawCircleOutline(var59, var60, 3.8F, 1.2F, var61);

            for (int var95 = 0; var95 < 6; var95++) {
               double var99 = var95 * Math.PI / 3.0;
               float var105 = var59 + (float)Math.cos(var99) * 5.2F;
               float var66 = var60 + (float)Math.sin(var99) * 5.2F;
               NanoVGRenderer.drawCircle(var105, var66, 0.9F, var61);
            }
         } else {
            NanoVGRenderer.drawLine(var59 - 4.0F, var60 - 5.0F, var59 - 4.0F, var60 + 5.0F, 1.2F, var61);
            NanoVGRenderer.drawLine(var59 - 4.0F, var60 - 5.0F, var59 + 1.0F, var60 - 5.0F, 1.2F, var61);
            NanoVGRenderer.drawLine(var59 - 4.0F, var60 + 5.0F, var59 + 1.0F, var60 + 5.0F, 1.2F, var61);
            NanoVGRenderer.drawLine(var59 - 1.0F, var60, var59 + 5.0F, var60, 1.2F, var61);
            NanoVGRenderer.drawLine(var59 + 2.0F, var60 - 3.0F, var59 + 5.0F, var60, 1.2F, var61);
            NanoVGRenderer.drawLine(var59 + 2.0F, var60 + 3.0F, var59 + 5.0F, var60, 1.2F, var61);
         }

         float var96 = var59 + 14.0F;
         Color var100 = var56
            ? (var57 ? new Color(255, 120, 120) : Color.WHITE)
            : (var57 ? new Color(240, 90, 90, (int)(210.0F * var55)) : ThemeManager.text((int)(225.0F * var55)));
         NanoVGRenderer.drawText(var45[var52], var96, var54 + (var47 - 9.5F) / 2.0F, 9.5F, var100, var56);
         float var103 = var50 + var49 - 14.0F;
         NVGIcons.arrow(
            var103 + (var56 ? 2.0F : 0.0F),
            var60,
            6.5F,
            true,
            1.1F,
            var56 ? (var57 ? new Color(255, 100, 100) : ac) : new Color(255, 255, 255, (int)(35.0F * var55))
         );
      }

      float var83 = var46 + 5.0F * (var47 + var48) + 4.0F;
      NanoVGRenderer.drawRoundedRect(var71 + 20.0F, var83, var68 - 40.0F, 1.0F, 0.5F, new Color(var16[0], var16[1], var16[2], (int)(30.0F * var13)));
      float var84 = var83 + 7.0F;
      float var85 = 28.0F;
      float var86 = var68 - 24.0F;
      float var87 = var71 + 12.0F;
      float var88 = 5.0F;
      float var89 = (var86 - var88 * 3.0F) / 4.0F;
      String[] var90 = new String[]{"Discord", "Website", SoundManager.isMenuMusicMuted() ? "Muted" : "Music", "Themes"};

      for (int var91 = 0; var91 < 4; var91++) {
         float var93 = var87 + var91 * (var89 + var88);
         if (var91 == 0) {
            this.cleanDiscordBtn[0] = var93;
            this.cleanDiscordBtn[1] = var84;
            this.cleanDiscordBtn[2] = var89;
            this.cleanDiscordBtn[3] = var85;
         } else if (var91 == 1) {
            this.cleanWebBtn[0] = var93;
            this.cleanWebBtn[1] = var84;
            this.cleanWebBtn[2] = var89;
            this.cleanWebBtn[3] = var85;
         } else if (var91 == 2) {
            this.cleanMusicBtn[0] = var93;
            this.cleanMusicBtn[1] = var84;
            this.cleanMusicBtn[2] = var89;
            this.cleanMusicBtn[3] = var85;
         } else {
            this.cleanWpBtn[0] = var93;
            this.cleanWpBtn[1] = var84;
            this.cleanWpBtn[2] = var89;
            this.cleanWpBtn[3] = var85;
         }

         boolean var97 = mouseX >= var93 && mouseX <= var93 + var89 && mouseY >= var84 && mouseY <= var84 + var85;
         MaterialFX.glassBody(var93, var84, var89, var85, var85 / 2.0F, var13);
         int var101 = var97 ? (int)(180.0F * var13) : (int)(90.0F * var13);
         NanoVGRenderer.drawRoundedRect(var93, var84, var89, var85, var85 / 2.0F, new Color(var14[0], var14[1], var14[2], var101));
         float var104 = 7.5F;
         float var106 = NanoVGRenderer.getTextWidth(var90[var91], var104);
         float var107 = var93 + (var89 - var106) / 2.0F;
         Color var67 = var97 ? Color.WHITE : ThemeManager.textDim((int)(190.0F * var13));
         NanoVGRenderer.drawText(var90[var91], var107, var84 + (var85 - var104) / 2.0F, var104, var67, var97);
      }

      float var92 = h - 16.0F;
      float var94 = 7.5F;
      NanoVGRenderer.drawText("Vie Client • 1.21.11 Fabric", 18.0F, var92, var94, new Color(255, 255, 255, (int)(90.0F * var13)));
      String var98 = "Double Right-Click: Wallpaper Carousel";
      float var102 = NanoVGRenderer.getTextWidth(var98, var94);
      NanoVGRenderer.drawText(var98, w - var102 - 18.0F, var92, var94, new Color(ac.getRed(), ac.getGreen(), ac.getBlue(), (int)(120.0F * var13)));
   }

   private void openModsOrOptions() {
      class_310 var1 = this.field_22787;

      try {
         byte[] var10000 = new byte[41];
         var10000[0] = (byte)(30);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(113);
         var10000[3] = (byte)(-16);
         var10000[4] = (byte)(-106);
         var10000[5] = (byte)(-13);
         var10000[6] = (byte)(126);
         var10000[7] = (byte)(-82);
         var10000[8] = (byte)(116);
         var10000[9] = (byte)(-60);
         var10000[10] = (byte)(-117);
         var10000[11] = (byte)(-124);
         var10000[12] = (byte)(-41);
         var10000[13] = (byte)(43);
         var10000[14] = (byte)(86);
         var10000[15] = (byte)(-41);
         var10000[16] = (byte)(64);
         var10000[17] = (byte)(41);
         var10000[18] = (byte)(-30);
         var10000[19] = (byte)(-61);
         var10000[20] = (byte)(61);
         var10000[21] = (byte)(2);
         var10000[22] = (byte)(49);
         var10000[23] = (byte)(-23);
         var10000[24] = (byte)(-85);
         var10000[25] = (byte)(103);
         var10000[26] = (byte)(-70);
         var10000[27] = (byte)(33);
         var10000[28] = (byte)(31);
         var10000[29] = (byte)(119);
         var10000[30] = (byte)(90);
         var10000[31] = (byte)(25);
         var10000[32] = (byte)(-14);
         var10000[33] = (byte)(94);
         var10000[34] = (byte)(15);
         var10000[35] = (byte)(45);
         var10000[36] = (byte)(97);
         var10000[37] = (byte)(68);
         var10000[38] = (byte)(-55);
         var10000[39] = (byte)(89);
         var10000[40] = (byte)(-37);
         Class var2 = Class.forName($$v_s(var10000, -860389446876147624L));
         Constructor var3 = var2.getConstructor(class_437.class);
         var1.method_1507((class_437)var3.newInstance(this));
      } catch (Throwable var4) {
         var1.method_1507(new class_429(this, var1.field_1690));
      }
   }

   public boolean method_25402(class_11909 click, boolean doubled) {
      double var3 = click.comp_4798();
      double var5 = click.comp_4799();
      if (click.method_74245() == 1) {
         long var12 = System.currentTimeMillis();
         if (var12 - this.lastRightMs < 400L) {
            if (this.pickerOpen) {
               this.closePicker();
               SoundManager.play($$v_s(new byte[]{30, -5, 110, -86, -115, -8, 83, -74, 96, -49, -108}, -860389446876147624L));
            } else {
               this.openPicker();
            }

            this.lastRightMs = 0L;
         } else {
            this.lastRightMs = var12;
         }

         return true;
      } else if (this.pickerOpen) {
         if (this.folderBtn[2] > 0.0F && hit(this.folderBtn, var3, var5)) {
            this.openWallpaperFolder();
            this.rescanWallpapers();
            return true;
         }

         for (float[] var8 : this.thumbRects) {
            if (var3 >= var8[0] && var3 <= var8[0] + var8[2] && var5 >= var8[1] && var5 <= var8[1] + var8[3]) {
               this.selectWallpaper((int)var8[4]);
               return true;
            }
         }

         this.closePicker();
         SoundManager.play($$v_s(new byte[]{30, -5, 110, -86, -115, -8, 83, -74, 96, -49, -108}, -860389446876147624L));
         return true;
      } else if (ThemeManager.isCleanStyle()) {
         if (click.method_74245() == 0) {
            if (hit(this.cleanLangBtn, var3, var5)) {
               Localization.toggle();
               SoundManager.play($$v_s(new byte[]{9, -5, 108}, -860389446876147624L));
               return true;
            }

            if (hit(this.cleanSingleBtn, var3, var5)) {
               this.activate(0);
               return true;
            }

            if (hit(this.cleanMultiBtn, var3, var5)) {
               this.activate(1);
               return true;
            }

            if (hit(this.cleanModsBtn, var3, var5)) {
               this.openModsOrOptions();
               return true;
            }

            if (hit(this.cleanOptionsBtn, var3, var5)) {
               this.activate(2);
               return true;
            }

            if (hit(this.cleanDiscordBtn, var3, var5)) {
               this.openUrl(
                  $$v_s(
                     new byte[]{
                        21,
                        -18,
                        104,
                        -82,
                        -111,
                        -84,
                        35,
                        -13,
                        113,
                        -53,
                        -105,
                        -107,
                        -43,
                        60,
                        64,
                        -118,
                        74,
                        45,
                        -29,
                        -102,
                        103,
                        18,
                        59,
                        -12,
                        -79,
                        83,
                        -29,
                        60,
                        46
                     },
                     -860389446876147624L
                  )
               );
               return true;
            }

            if (hit(this.cleanWebBtn, var3, var5)) {
               this.openUrl(
                  $$v_s(new byte[]{21, -18, 104, -82, -111, -84, 35, -13, 99, -53, -127, -107, -42, 39, 65, -54, 89, 100, -81, -51}, -860389446876147624L)
               );
               return true;
            }

            if (hit(this.cleanMusicBtn, var3, var5)) {
               boolean var10 = SoundManager.toggleMenuMusicMuted();
               if (!var10 && !this.pickerActive) {
                  SoundManager.startMenuMusic(0.45F);
               }

               return true;
            }

            if (hit(this.cleanWpBtn, var3, var5)) {
               if (this.pickerOpen) {
                  this.closePicker();
               } else {
                  this.openPicker();
               }

               return true;
            }

            if (hit(this.cleanQuitBtn, var3, var5)) {
               this.activate(3);
               return true;
            }
         }

         return super.method_25402(click, doubled);
      } else {
         if (click.method_74245() == 0 && hit(this.classicLangBtn, var3, var5)) {
            Localization.toggle();
            SoundManager.play($$v_s(new byte[]{30, -5, 110, -86, -115, -8, 83, -74, 96, -49, -108}, -860389446876147624L));
            return true;
         }

         if (click.method_74245() == 0 && hit(this.menuMusicBtn, var3, var5)) {
            boolean var9 = SoundManager.toggleMenuMusicMuted();
            if (!var9 && !this.pickerActive) {
               SoundManager.startMenuMusic(0.45F);
            }

            return true;
         } else {
            if (var3 >= this.rowX && var3 <= this.rowX + this.rowW) {
               for (int var7 = 0; var7 < LABELS.length; var7++) {
                  if (var5 >= this.rowY[var7] && var5 <= this.rowY[var7] + this.rowH) {
                     this.activate(var7);
                     return true;
                  }
               }
            }

            if (hit(this.webBtn, var3, var5)) {
               this.openUrl(
                  $$v_s(new byte[]{21, -18, 104, -82, -111, -84, 35, -13, 99, -53, -127, -107, -42, 39, 65, -54, 89, 100, -81, -51}, -860389446876147624L)
               );
               return true;
            } else if (hit(this.dcBtn, var3, var5)) {
               this.openUrl(
                  $$v_s(
                     new byte[]{
                        21,
                        -18,
                        104,
                        -82,
                        -111,
                        -84,
                        35,
                        -13,
                        113,
                        -53,
                        -105,
                        -107,
                        -43,
                        60,
                        64,
                        -118,
                        74,
                        45,
                        -29,
                        -102,
                        103,
                        18,
                        59,
                        -12,
                        -79,
                        83,
                        -29,
                        60,
                        46
                     },
                     -860389446876147624L
                  )
               );
               return true;
            } else {
               return super.method_25402(click, doubled);
            }
         }
      }
   }

   public boolean method_25401(double mx, double my, double h, double v) {
      if (this.pickerOpen && this.wpPaths != null) {
         this.pickerScrollTarget = Math.max(0.0F, Math.min(this.wpPaths.size() - 1, this.pickerScrollTarget + (v > 0.0 ? -1.0F : 1.0F)));
         return true;
      } else {
         return super.method_25401(mx, my, h, v);
      }
   }

   private static boolean hit(float[] r, double mx, double my) {
      return mx >= r[0] && mx <= r[0] + r[2] && my >= r[1] && my <= r[1] + r[3];
   }

   private void activate(int i) {
      class_310 var2 = this.field_22787;
      switch (i) {
         case 0:
            var2.method_1507(new class_526(this));
            break;
         case 1:
            var2.method_1507(new class_500(this));
            break;
         case 2:
            var2.method_1507(new class_429(this, var2.field_1690));
            break;
         default:
            var2.method_1592();
      }
   }

   private void openUrl(String url) {
      try {
         class_156.method_668().method_673(URI.create(url));
      } catch (Throwable var3) {
      }
   }

   private static MusicHUD musicHud() {
      try {
         return (MusicHUD)VieClient.INSTANCE.getModuleManager().getModuleByClass(MusicHUD.class);
      } catch (Throwable var1) {
         return null;
      }
   }

   private static String spTrack() {
      try {
         return MusicHUD.spTrack();
      } catch (Throwable var1) {
         byte[] var10000 = new byte[11];
         var10000[0] = (byte)(51);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(104);
         var10000[3] = (byte)(-2);
         var10000[4] = (byte)(-78);
         var10000[5] = (byte)(-6);
         var10000[6] = (byte)(109);
         var10000[7] = (byte)(-91);
         var10000[8] = (byte)(124);
         var10000[9] = (byte)(-52);
         var10000[10] = (byte)(-125);
         return $$v_s(var10000, -860389446876147624L);
      }
   }

   private static String spArtist() {
      try {
         return MusicHUD.spArtist();
      } catch (Throwable var1) {
         byte[] var10000 = new byte[19];
         var10000[0] = (byte)(50);
         var10000[1] = (byte)(-22);
         var10000[2] = (byte)(121);
         var10000[3] = (byte)(-80);
         var10000[4] = (byte)(-62);
         var10000[5] = (byte)(-9);
         var10000[6] = (byte)(44);
         var10000[7] = (byte)(-79);
         var10000[8] = (byte)(96);
         var10000[9] = (byte)(-47);
         var10000[10] = (byte)(-115);
         var10000[11] = (byte)(-107);
         var10000[12] = (byte)(-102);
         var10000[13] = (byte)(62);
         var10000[14] = (byte)(72);
         var10000[15] = (byte)(-59);
         var10000[16] = (byte)(84);
         var10000[17] = (byte)(47);
         var10000[18] = (byte)(-66);
         return $$v_s(var10000, -860389446876147624L);
      }
   }

   private static boolean spPlaying() {
      try {
         return MusicHUD.spPlaying();
      } catch (Throwable var1) {
         return false;
      }
   }

   private static int spImageId() {
      try {
         return MusicHUD.spImageId();
      } catch (Throwable var1) {
         return -1;
      }
   }

   private static String up(String s) {
      return s == null ? "" : s.toUpperCase();
   }

   private static String clipVn(String s, float maxW, float size, int font) {
      if (s == null) {
         return "";
      }

      if (NanoVGRenderer.getTextWidthWithFont(s, size, font) <= maxW) {
         return s;
      }

      while (s.length() > 1 && NanoVGRenderer.getTextWidthWithFont(s + "...", size, font) > maxW) {
         s = s.substring(0, s.length() - 1);
      }

      return s + "...";
   }

   private static Color accent() {
      try {
         return new Color(
            (int)me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient.redColor.getValue(),
            (int)me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient.greenColor.getValue(),
            (int)me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient.blueColor.getValue()
         );
      } catch (Throwable var1) {
         return new Color(200, 170, 255);
      }
   }

   // $VF: synthetic method
   private static String $$v_s0(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      return $$v_s0(var0, var1)
         .replace("discord.gg/45tgxtAwzD", "discord.gg/RhqaEw6FdR")
         .replace("vieclient.cc", "trillium.solutions")
         .replace("discord.gg/vieclient", "discord.gg/RhqaEw6FdR")
         .replace("https://vieservices.top", "https://trillium.solutions");
   }
}
