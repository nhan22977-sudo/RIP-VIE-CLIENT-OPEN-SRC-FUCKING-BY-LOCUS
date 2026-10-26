package me.drake.vieclient.modules.client;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ButtonSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.gui.nvg.ui.components.GuiStyle;
import me.drake.vieclient.gui.nvg.ui.components.SoftPalette;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;
import me.drake.vieclient.modules.client.hud.ArmorStatusWidget;
import me.drake.vieclient.modules.client.hud.HudEditorGeometry;
import me.drake.vieclient.modules.client.hud.HudResizeCursor;
import me.drake.vieclient.modules.client.hud.HudSnapping;
import me.drake.vieclient.modules.client.hud.PlayerHudWidget;
import me.drake.vieclient.modules.client.hud.RadarWidget;
import me.drake.vieclient.modules.client.hud.RegionMapWidget;
import me.drake.vieclient.modules.donut.StaffLists;
import me.drake.vieclient.render.FrostedBackground;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.MaterialFX;
import me.drake.vieclient.render.nvg.NVGIcons;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.BlockIcon;
import me.drake.vieclient.util.MeasuredOrderCache;
import me.drake.vieclient.util.RainbowUtil;
import me.drake.vieclient.util.Sfx;
import me.drake.vieclient.util.render.CordNotification;
import me.drake.vieclient.util.render.ToastManager;
import net.minecraft.class_124;
import net.minecraft.class_1291;
import net.minecraft.class_1293;
import net.minecraft.class_1297;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import net.minecraft.class_408;
import net.minecraft.class_640;
import net.minecraft.class_7923;

public final class HUD extends Module {
   private static final SimpleDateFormat TIME_FMT;
   private static volatile long lastTimeSec = -1L;
   private static volatile String cachedTimeStr = "";
   private final Map moduleAnimations = new HashMap();
   private final MeasuredOrderCache moduleOrder = new MeasuredOrderCache();
   private final Map keyPressAnim = new HashMap();
   private long lastFrameTime = System.currentTimeMillis();
   private long hudIntroStart = 0L;
   private boolean hudWasInWorld = false;
   private int introIdx = 0;
   private final BoolSetting showWatermark = new BoolSetting(
      "Watermark",
      "Shows the watermark chip",
      true
   );
   private final BoolSetting showFPS = new BoolSetting(
      "FPS",
      "Shows the FPS chip",
      true
   );
   private final BoolSetting showPing = new BoolSetting(
      "Ping",
      "Shows the ping chip",
      true
   );
   private final BoolSetting showTotems = new BoolSetting(
      "Totems",
      "Shows the totem count chip",
      true
   );
   private final BoolSetting showTime = new BoolSetting(
      "Time",
      "Shows the clock chip",
      true
   );
   private final BoolSetting showKeybinds = new BoolSetting(
      "Keybinds",
      "Shows the keybinds list",
      true
   );
   private final BoolSetting showPotions = new BoolSetting(
      "Potions",
      "Shows active potion effects",
      true
   );
   private final BoolSetting showModules = new BoolSetting(
      "Modules",
      "Shows enabled modules",
      true
   );
   private final EnumSetting moduleMode = new EnumSetting(
      "Module Style",
      "Module list style (GUI tab)",
      HUD.ModuleMode.PILL,
      HUD.ModuleMode.values()
   );
   private final BoolSetting showCoords = new BoolSetting(
      "Coords",
      "Shows coordinates",
      true
   );
   private final DoubleSetting hudScale = new DoubleSetting(
      "HUD Scale",
      "Master size multiplier for every HUD widget (chip + coords + compass). 0.8 = 80%.",
      0.8,
      0.5,
      1.5,
      0.05
   );
   private final BoolSetting showCompass = new BoolSetting(
      "Compass",
      "Shows the compass strip",
      true
   );
   private final BoolSetting showRegionMap = new BoolSetting(
      "RegionMap",
      "Shows the region map",
      true
   );
   private final EnumSetting regionMapMode = new EnumSetting(
         "RegionMap Mode",
         "Choose between Old and New region map layout",
         HUD.RegionMapMode.OLD,
         HUD.RegionMapMode.values()
      )
      .withDisplayName(HUD.RegionMapMode.OLD, "Old")
      .withDisplayName(HUD.RegionMapMode.NEW, "New");
   private final BoolSetting showKeystrokes = new BoolSetting(
      "Keystrokes",
      "Shows the keystrokes widget",
      true
   );
   private final BoolSetting showRadar = new BoolSetting(
      "Radar",
      "Shows the nearby player radar",
      true
   );
   private final IntSetting radarRange = new IntSetting(
      "Radar Range",
      "Radar detection distance in blocks",
      64,
      16,
      256,
      16
   );
   private final BoolSetting showPlayerHUD = new BoolSetting(
      "PlayerHUD",
      "Shows nearby player HUD cards",
      true
   );
   private final IntSetting playerHudMax = new IntSetting(
      "PlayerHUD Max",
      "Maximum number of players to display in PlayerHUD",
      4,
      1,
      10,
      1
   );
   private final BoolSetting showArmor = new BoolSetting(
      "Armor",
      "Shows equipped armor & held items",
      true
   );
   private final EnumSetting armorLayout = new EnumSetting(
      "Armor Layout",
      "Orientation of armor widget",
      HUD.ArmorLayout.VERTICAL,
      HUD.ArmorLayout.values()
   );
   private final BoolSetting armorShowHands = new BoolSetting(
      "Armor Hands",
      "Show mainhand and offhand items",
      true
   );
   private final EnumSetting armorDuraMode = new EnumSetting(
      "Armor Durability",
      "Durability display mode",
      HUD.DuraMode.PERCENT,
      HUD.DuraMode.values()
   );
   private final BoolSetting animations = new BoolSetting(
      "Animations",
      "Animates the HUD widgets",
      true
   );
   private final ButtonSetting openEditor = new ButtonSetting(
      "HUD Editor",
      "Open interactive HUD layout editor",
      () -> {
         if (mc != null) {
            this.enterEditorMode();
            mc.method_1507(new class_408("", false));
         }
      }
   );
   private final ButtonSetting resetLayout = new ButtonSetting(
      "Reset Positions & Sizes",
      "Reset all HUD widget positions and sizes to default",
      this::resetLayout
   );
   public static final String W_KEYSTROKES = "keystrokes";
   public static final String W_RADAR = "radar";
   private final List leftClicks = new ArrayList();
   private final List rightClicks = new ArrayList();
   private boolean lastAttackPressed = false;
   private boolean lastUsePressed = false;
   private static final float FS = 8.5F;
   private static final float PX = 5.0F;
   private static final float PY = 3.0F;
   private static final float GAP = 6.0F;
   private static final float RAD = 7.0F;
   private static final int BODY_A = 205;
   public static final String W_WATERMARK = "watermark";
   public static final String W_FPS = "fps";
   public static final String W_PING = "ping";
   public static final String W_TOTEMS = "totems";
   public static final String W_TIME = "time";
   public static final String W_KEYBINDS = "keybinds";
   public static final String W_POTIONS = "potions";
   public static final String W_COORDS = "coords";
   public static final String W_COORDS_ALT = "coords_alt";
   public static final String W_COORDS_OW = "coords_ow";
   public static final String W_COORDS_NETHER = "coords_nether";
   public static final String W_COMPASS = "compass";
   public static final String W_MODULES = "modules";
   public static final String W_SPOTIFY = "spotify";
   public static final String W_MUSIC = "spotify";
   public static final String W_STAFFLIST = "stafflist";
   public static final String W_WEATHER = "weather";
   public static final String W_PLAYERHUD = "playerhud";
   public static final String W_REGIONMAP = "regionmap";
   public static final String W_ARMOR = "armor";
   private final RegionMapWidget regionMapWidget = new RegionMapWidget();
   private final RadarWidget radarWidget = new RadarWidget();
   private final PlayerHudWidget playerHudWidget = new PlayerHudWidget();
   private final ArmorStatusWidget armorStatusWidget = new ArmorStatusWidget();
   private final Map widgetPos = new HashMap();
   private final Map widgetBounds = new HashMap();
   private final Set userMovedWidgets = new HashSet();
   private final Map savedDefaultPositions = new HashMap();
   private final Map widgetRightOffset = new HashMap();
   private boolean editorMode = false;
   private String selectedWidget = null;
   private boolean armorMenuOpen = false;
   private float armorMenuX = 0.0F;
   private float armorMenuY = 0.0F;
   public static final float ARMOR_MENU_W = 152.0F;
   public static final float ARMOR_MENU_H = 88.0F;
   private float[] editorResetBtnRect = null;
   private String draggingWidget = null;
   private float dragOffX;
   private float dragOffY;
   private static final float HANDLE_HIT_RADIUS = 6.0F;
   private static final long DOUBLE_RIGHT_CLICK_MS = 350L;
   private final HudResizeCursor resizeCursor = new HudResizeCursor();
   private HUD.EditorAction editorAction = HUD.EditorAction.IDLE;
   private HudEditorGeometry.Corner activeCorner;
   private HudEditorGeometry.Rect resizeStartRect;
   private float resizeStartScale;
   private String lastRightWidget;
   private long lastRightClickMs;
   private boolean potionsWasVisible = false;
   private long potionsShownMs = 0L;
   private final Map potionIconCache = new HashMap();
   private final BoolSetting editorLayers = new BoolSetting(
      "HUD Editor layers",
      "Allow HUD editor widgets to overlap",
      false
   );
   private final BoolSetting snapping = new BoolSetting(
      "Snapping",
      "Snap widgets to alignment guides and screen center",
      true
   );
   private final List activeSnapGuides = new ArrayList();
   private final Map widgetBaseBounds = new HashMap();
   private final Map widgetScale = new HashMap();
   private List cachedSortedMods;
   private long lastSortedFrameNanos = -1L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private static String getCachedTimeString() {
      long var0 = System.currentTimeMillis() / 1000L;
      if (var0 != lastTimeSec) {
         lastTimeSec = var0;
         cachedTimeStr = TIME_FMT.format(new Date(var0 * 1000L));
      }

      return cachedTimeStr;
   }

   private float[] widgetFitInfo(String id, int sw, int sh) {
      float[] var10000 = new float[3];
      var10000[0] = 1.0F;
      var10000[1] = sw * 0.5F;
      var10000[2] = sh * 0.5F;
      return var10000;
   }

   private double[] screenToDesign(String id, double mx, double my) {
      int var6 = mc.method_22683().method_4486();
      int var7 = mc.method_22683().method_4502();
      float[] var8 = this.widgetFitInfo(id, var6, var7);
      float var9 = var8[0];
      if (Math.abs(var9 - 1.0F) < 1.0E-4F) {
         double[] var12 = new double[2];
         var12[0] = mx;
         var12[1] = my;
         return var12;
      } else {
         float var10 = var8[1];
         float var11 = var8[2];
         double[] var10000 = new double[2];
         var10000[0] = (mx - var10) / var9 + var10;
         var10000[1] = (my - var11) / var9 + var11;
         return var10000;
      }
   }

   private float[] designClamp(String id, int sw, int sh, float w, float h) {
      float[] var6 = this.widgetFitInfo(id, sw, sh);
      float var7 = var6[0];
      if (Math.abs(var7 - 1.0F) < 1.0E-4F) {
         float[] var10 = new float[4];
         var10[0] = 1.0F;
         var10[1] = 1.0F;
         var10[2] = Math.max(1.0F, sw - w - 1.0F);
         var10[3] = Math.max(1.0F, sh - h - 1.0F);
         return var10;
      } else {
         float var8 = var6[1];
         float var9 = var6[2];
         float[] var10000 = new float[4];
         var10000[0] = (1.0F - var8) / var7 + var8;
         var10000[1] = (1.0F - var9) / var7 + var9;
         var10000[2] = (sw - 1.0F - var8) / var7 + var8 - w;
         var10000[3] = (sh - 1.0F - var9) / var7 + var9 - h;
         return var10000;
      }
   }

   private static Color bgTop(float e) {
      int[] var1 = ThemeManager.bodyTop();
      int var2 = ThemeManager.isCleanStyle() ? (int)(178.0F * e) : (int)(ThemeManager.bodyAlpha(e) * MaterialFX.bodyTintMul());
      return new Color(var1[0], var1[1], var1[2], var2);
   }

   private static Color bgBot(float e) {
      int[] var1 = ThemeManager.bodyBot();
      int var2 = ThemeManager.isCleanStyle() ? (int)(178.0F * e) : (int)(ThemeManager.bodyAlpha(e) * MaterialFX.bodyTintMul());
      return new Color(var1[0], var1[1], var1[2], var2);
   }

   private static Color ol(float e) {
      if (ThemeManager.isCleanStyle()) {
         return new Color(0, 0, 0, 0);
      }

      int[] var1 = ThemeManager.borderRGB();
      return new Color(var1[0], var1[1], var1[2], (int)(72.0F * e));
   }

   public static Color bgTop() {
      return bgTop(1.0F);
   }

   public static Color bgBot() {
      return bgBot(1.0F);
   }

   public static Color ol() {
      return ol(1.0F);
   }

   public static Color txt() {
      return ThemeManager.text(255);
   }

   public static Color sub() {
      return ThemeManager.textDim(220);
   }

   private static ClientSettings clientSettings() {
      try {
         VieClientMod var0 = VieClientMod.getInstance();
         if (var0 != null && var0.getModuleManager() != null) {
            return (ClientSettings)var0.getModuleManager().getModule(ClientSettings.class);
         }
      } catch (Throwable var1) {
      }

      return null;
   }

   public static HUD instance() {
      try {
         VieClientMod var0 = VieClientMod.getInstance();
         if (var0 != null && var0.getModuleManager() != null) {
            return (HUD)var0.getModuleManager().getModule(HUD.class);
         }
      } catch (Throwable var1) {
      }

      return null;
   }

   public Color accent() {
      ClientSettings var1 = clientSettings();
      if (var1 != null) {
         int var2 = var1.getPrimaryColor();
         return new Color(var2);
      } else {
         return new Color(-10050049);
      }
   }

   public HUD() {
      super(
         "HUD",
         "Minimal HUD",
         Category.RENDER,
         -1
      );
      this.hudIntroStart = System.currentTimeMillis() - 1000L;
      this.hudWasInWorld = true;
      this.addSetting(this.showWatermark);
      this.addSetting(this.showFPS);
      this.addSetting(this.showPing);
      this.addSetting(this.showTotems);
      this.addSetting(this.showTime);
      this.addSetting(this.showKeybinds);
      this.addSetting(this.showPotions);
      this.addSetting(this.showModules);
      this.addSetting(this.moduleMode);
      this.addSetting(this.showCoords);
      this.addSetting(this.hudScale);
      this.addSetting(this.showCompass);
      this.addSetting(this.showRegionMap);
      this.addSetting(this.regionMapMode);
      this.regionMapMode.visibleWhen(this.showRegionMap::get);
      this.addSetting(this.showKeystrokes);
      this.addSetting(this.showRadar);
      this.addSetting(this.radarRange);
      this.addSetting(this.showPlayerHUD);
      this.addSetting(this.playerHudMax);
      this.addSetting(this.showArmor);
      this.addSetting(this.armorLayout);
      this.addSetting(this.armorShowHands);
      this.addSetting(this.armorDuraMode);
      this.addSetting(this.animations);
      this.addSetting(this.editorLayers);
      this.addSetting(this.snapping);
      this.addSetting(this.openEditor);
      this.addSetting(this.resetLayout);
      this.setEnabled(true);
   }

   @Override
   protected boolean bypassAuthLock() {
      return true;
   }

   public Map getWidgetPositions() {
      HashMap var1 = new HashMap();

      for (Entry var3 : this.widgetPos.entrySet()) {
         if (this.userMovedWidgets.contains(var3.getKey())) {
            var1.put((String)var3.getKey(), (float[])var3.getValue());
         }
      }

      return var1;
   }

   public Map getWidgetScales() {
      return new HashMap(this.widgetScale);
   }

   public void setWidgetScales(Map scales) {
      this.widgetScale.clear();
      if (scales != null) {
         scales.forEach((id, value) -> this.widgetScale.put(id, HudEditorGeometry.clampScale(value == null ? 1.0F : value)));
      }

      for (String var3 : this.widgetScale.keySet()) {
         this.recalculateBounds(var3);
      }
   }

   public float getWidgetScale(String id) {
      float var2 = this.widgetScale.getOrDefault(id, 1.0F);
      return HudEditorGeometry.clampScale(var2) * this.masterHudScale();
   }

   private float masterHudScale() {
      if (this.hudScale == null) {
         return 1.0F;
      }

      double var1 = (Double)this.hudScale.get();
      return Double.isFinite(var1) && !(var1 <= 0.0) ? HudEditorGeometry.clampScale((float)var1) : 1.0F;
   }

   public HUD.WidgetRenderScope beginWidgetRender(String id, float x, float y) {
      return new HUD.WidgetRenderScope(id, x, y);
   }

   public void setWidgetPositions(Map positions) {
      this.widgetPos.clear();
      this.userMovedWidgets.clear();
      if (positions != null) {
         this.widgetPos.putAll(positions);
         this.userMovedWidgets.addAll(positions.keySet());
      }

      for (String var3 : this.widgetPos.keySet()) {
         this.recalculateBounds(var3);
      }
   }

   public void onMousePressed(double mx, double my) {
      if (this.editorMode) {
         if (this.armorMenuOpen) {
            if (this.handleArmorMenuClick(mx, my)) {
               return;
            }

            if (!this.isInsideArmorMenu(mx, my)) {
               this.armorMenuOpen = false;
            }
         }

         if (this.editorResetBtnRect != null
            && mx >= this.editorResetBtnRect[0]
            && mx <= this.editorResetBtnRect[0] + this.editorResetBtnRect[2]
            && my >= this.editorResetBtnRect[1]
            && my <= this.editorResetBtnRect[1] + this.editorResetBtnRect[3]) {
            this.resetLayout();
            this.selectedWidget = null;
         } else {
            HUD.ResizeTarget var5 = this.resizeTargetAt(mx, my);
            if (var5 != null) {
               this.selectedWidget = var5.id();
               this.draggingWidget = var5.id();
               this.editorAction = HUD.EditorAction.RESIZE;
               this.activeCorner = var5.corner();
               this.resizeStartRect = var5.rect();
               this.resizeStartScale = this.getWidgetScale(var5.id());
            } else {
               String var6 = this.widgetAt(mx, my);
               if (var6 == null) {
                  this.selectedWidget = null;
               } else {
                  this.selectedWidget = var6;
                  this.activeSnapGuides.clear();
                  float[] var7 = (float[])this.widgetBounds.get(var6);
                  this.draggingWidget = var6;
                  this.editorAction = HUD.EditorAction.MOVE;
                  double[] var8 = this.screenToDesign(var6, mx, my);
                  this.dragOffX = (float)var8[0] - var7[0];
                  this.dragOffY = (float)var8[1] - var7[1];
               }
            }
         }
      }
   }

   public void onMouseDragged(double mx, double my) {
      this.onMouseMoved(mx, my);
   }

   public void onMouseMoved(double mx, double my) {
      if (this.editorMode) {
         int var5 = mc.method_22683().method_4486();
         int var6 = mc.method_22683().method_4502();
         if (this.editorAction == HUD.EditorAction.RESIZE && this.draggingWidget != null && this.resizeStartRect != null && this.activeCorner != null) {
            this.activeSnapGuides.clear();
            HudEditorGeometry.ResizeResult var23 = HudEditorGeometry.resize(this.resizeStartRect, this.resizeStartScale, this.activeCorner, mx, my, var5, var6);
            this.applyResize(this.draggingWidget, var23);
         } else if (this.editorAction == HUD.EditorAction.MOVE && this.draggingWidget != null) {
            float[] var7 = (float[])this.widgetBounds.get(this.draggingWidget);
            if (var7 != null) {
               double[] var8 = this.screenToDesign(this.draggingWidget, mx, my);
               float[] var9 = this.designClamp(this.draggingWidget, var5, var6, var7[2], var7[3]);
               float var10 = var9[0];
               float var11 = var9[2];
               float var12 = var9[1];
               float var13 = var9[3];
               float var14 = (float)var8[0] - this.dragOffX;
               float var15 = (float)var8[1] - this.dragOffY;
               float var16 = var14;
               float var17 = var15;
               this.activeSnapGuides.clear();
               if ((Boolean)this.snapping.get()) {
                  HashMap var18 = new HashMap(this.widgetBounds);
                  var18.remove(this.draggingWidget);
                  HudSnapping.Result var19 = HudSnapping.snap(var14, var15, var7[2], var7[3], var5, var6, var18, 6.0F);
                  var16 = var19.x();
                  var17 = var19.y();

                  for (HudSnapping.Guide var21 : var19.guides()) {
                     if (var21.vertical()) {
                        float var22 = Math.max(var10, Math.min(var11, var16));
                        if (Math.abs(var22 - var16) <= 0.01F) {
                           this.activeSnapGuides.add(var21);
                        }
                     } else {
                        float var27 = Math.max(var12, Math.min(var13, var17));
                        if (Math.abs(var27 - var17) <= 0.01F) {
                           this.activeSnapGuides.add(var21);
                        }
                     }
                  }
               }

               float var25 = Math.max(var10, Math.min(var11, var16));
               float var26 = Math.max(var12, Math.min(var13, var17));
               if ("modules".equals(this.draggingWidget)) {
                  var25 += var7[2];
               }

               this.userMovedWidgets.add(this.draggingWidget);
               this.storeWidgetPosition(this.draggingWidget, var25, var26);
               var7[0] = var25 - ("modules".equals(this.draggingWidget) ? var7[2] : 0.0F);
               var7[1] = var26;
            } else {
               this.activeSnapGuides.clear();
            }
         } else {
            this.activeSnapGuides.clear();
         }

         HudEditorGeometry.Corner var24 = this.editorAction == HUD.EditorAction.RESIZE ? this.activeCorner : this.hoveredCorner(mx, my);
         this.resizeCursor.apply(mc.method_22683().method_4490(), var24);
      }
   }

   public void onMouseReleased() {
      boolean var1 = this.editorAction != HUD.EditorAction.IDLE;
      this.activeSnapGuides.clear();
      this.draggingWidget = null;
      this.editorAction = HUD.EditorAction.IDLE;
      this.activeCorner = null;
      this.resizeStartRect = null;
      this.resizeCursor.reset(mc.method_22683().method_4490());
      if (var1) {
         this.requestEditorConfigSave();
      }

      if (!(mc.field_1755 instanceof class_408)) {
         this.exitEditorMode();
      }
   }

   public void onRightMousePressed(double mx, double my, long nowMs) {
      if (this.editorMode) {
         if (this.armorMenuOpen && this.isInsideArmorMenu(mx, my)) {
            this.handleArmorMenuClick(mx, my);
         } else {
            String var7 = this.widgetAt(mx, my);
            if (var7 == null) {
               this.lastRightWidget = null;
               if (this.armorMenuOpen) {
                  this.armorMenuOpen = false;
               }
            } else if ("armor".equals(var7)) {
               this.armorMenuOpen = !this.armorMenuOpen;
               this.selectedWidget = "armor";
               if (this.armorMenuOpen) {
                  this.initArmorMenu(mx, my);

                  try {
                     Sfx.play(Sfx.CLICK_ON);
                  } catch (Throwable var9) {
                  }
               }
            } else {
               if (this.armorMenuOpen) {
                  this.armorMenuOpen = false;
               }

               if (var7.equals(this.lastRightWidget) && nowMs - this.lastRightClickMs <= 350L) {
                  this.widgetScale.put(var7, 1.0F);
                  if ("spotify".equals(var7) && MusicHUD.scale != null) {
                     MusicHUD.scale.set(1.0);
                  }

                  if ("stafflist".equals(var7)) {
                     try {
                        StaffLists var8 = (StaffLists)VieClientMod.getInstance().getModuleManager().getModule(StaffLists.class);
                        if (var8 != null && var8.scale != null) {
                           var8.scale.set(1);
                        }
                     } catch (Throwable var10) {
                     }
                  }

                  this.recalculateBounds(var7);
                  this.requestEditorConfigSave();
                  this.lastRightWidget = null;
               } else {
                  this.lastRightWidget = var7;
                  this.lastRightClickMs = nowMs;
               }
            }
         }
      }
   }

   public void enterEditorMode() {
      this.editorMode = true;
   }

   public void exitEditorMode() {
      this.editorMode = false;
      this.selectedWidget = null;
      this.armorMenuOpen = false;
      this.activeSnapGuides.clear();
      this.draggingWidget = null;
      this.editorAction = HUD.EditorAction.IDLE;
      this.activeCorner = null;
      this.resizeStartRect = null;
      this.editorResetBtnRect = null;
      this.resizeCursor.close();
      this.requestEditorConfigSave();
   }

   public void resetLayout() {
      if (mc != null && mc.method_22683() != null) {
         int var7 = mc.method_22683().method_4486();

         for (Entry var10 : this.widgetPos.entrySet()) {
            if (this.userMovedWidgets.contains(var10.getKey())) {
               float[] var4 = (float[])var10.getValue();
               this.savedDefaultPositions.put((String)var10.getKey(), new float[]{var4[0], var4[1]});
               if ("modules".equals(var10.getKey())) {
                  this.widgetRightOffset.put((String)var10.getKey(), var7 - var4[0]);
               }
            }
         }
      } else {
         for (Entry var2 : this.widgetPos.entrySet()) {
            if (this.userMovedWidgets.contains(var2.getKey())) {
               float[] var3 = (float[])var2.getValue();
               this.savedDefaultPositions.put((String)var2.getKey(), new float[]{var3[0], var3[1]});
            }
         }
      }

      this.widgetPos.clear();
      this.userMovedWidgets.clear();
      this.widgetScale.clear();
      this.widgetBounds.clear();
      this.widgetBaseBounds.clear();
      if (this.hudScale != null) {
         this.hudScale.set(0.8);
      }

      try {
         if (MusicHUD.scale != null) {
            MusicHUD.scale.set(1.0);
         }
      } catch (Throwable var6) {
      }

      try {
         StaffLists var8 = (StaffLists)VieClientMod.getInstance().getModuleManager().getModule(StaffLists.class);
         if (var8 != null && var8.scale != null) {
            var8.scale.set(1);
         }
      } catch (Throwable var5) {
      }

      this.draggingWidget = null;
      this.editorAction = HUD.EditorAction.IDLE;
      this.activeCorner = null;
      this.resizeStartRect = null;
      this.requestEditorConfigSave();
      ToastManager.push(
         "HUD",
         true,
         "Positions & sizes reset to default",
         null
      );
   }

   public boolean isEditorMode() {
      return this.editorMode;
   }

   public boolean editorLayersEnabled() {
      return (Boolean)this.editorLayers.get();
   }

   public void toggleEditorLayers() {
      this.editorLayers.toggle();
      this.requestEditorConfigSave();
   }

   public boolean isSnappingEnabled() {
      return (Boolean)this.snapping.get();
   }

   public boolean snappingEnabled() {
      return (Boolean)this.snapping.get();
   }

   public void toggleSnapping() {
      this.snapping.toggle();
      this.requestEditorConfigSave();
   }

   public BoolSetting getSnappingSetting() {
      return this.snapping;
   }

   public String getSelectedWidget() {
      return this.selectedWidget;
   }

   public String getDraggingWidget() {
      return this.draggingWidget;
   }

   public boolean isDragging() {
      return this.draggingWidget != null || this.editorAction != HUD.EditorAction.IDLE;
   }

   public boolean hasSelectedWidget() {
      return this.selectedWidget != null;
   }

   public void clearSelectedWidget() {
      this.selectedWidget = null;
   }

   public boolean isArmorMenuOpen() {
      return this.armorMenuOpen;
   }

   public void closeArmorMenu() {
      this.armorMenuOpen = false;
   }

   public HUD.ArmorLayout getArmorLayout() {
      return (HUD.ArmorLayout)this.armorLayout.get();
   }

   public void setArmorLayout(HUD.ArmorLayout layout) {
      this.armorLayout.set(layout);
      this.updateArmorBounds();
      this.requestEditorConfigSave();
   }

   public boolean getArmorShowHands() {
      return (Boolean)this.armorShowHands.get();
   }

   public void setArmorShowHands(boolean show) {
      this.armorShowHands.set(show);
      this.updateArmorBounds();
      this.requestEditorConfigSave();
   }

   public HUD.DuraMode getArmorDuraMode() {
      return (HUD.DuraMode)this.armorDuraMode.get();
   }

   public void setArmorDuraMode(HUD.DuraMode mode) {
      this.armorDuraMode.set(mode);
      this.updateArmorBounds();
      this.requestEditorConfigSave();
   }

   public HUD.RegionMapMode getRegionMapMode() {
      return (HUD.RegionMapMode)this.regionMapMode.get();
   }

   public void setRegionMapMode(HUD.RegionMapMode mode) {
      this.regionMapMode.set(mode);
      this.requestEditorConfigSave();
   }

   public void updateArmorBounds() {
      float var1 = ArmorStatusWidget.calculateWidth(
         (HUD.ArmorLayout)this.armorLayout.get(), (Boolean)this.armorShowHands.get(), (HUD.DuraMode)this.armorDuraMode.get()
      );
      float var2 = ArmorStatusWidget.calculateHeight(
         (HUD.ArmorLayout)this.armorLayout.get(), (Boolean)this.armorShowHands.get(), (HUD.DuraMode)this.armorDuraMode.get()
      );
      byte[] var10001 = new byte[5];
      var10001[0] = (byte)(-119);
      var10001[1] = (byte)(-24);
      var10001[2] = (byte)(113);
      var10001[3] = (byte)(-79);
      var10001[4] = (byte)(81);
      float[] var3 = (float[])this.widgetBounds.get($$v_s(var10001, -8444524616064184115L));
      if (var3 != null) {
         var10001 = new byte[5];
         var10001[0] = (byte)(-119);
         var10001[1] = (byte)(-24);
         var10001[2] = (byte)(113);
         var10001[3] = (byte)(-79);
         var10001[4] = (byte)(81);
         this.cacheBounds($$v_s(var10001, -8444524616064184115L), var3[0], var3[1], var1, var2);
      }
   }

   public boolean deleteSelectedWidget() {
      if (this.selectedWidget == null) {
         return false;
      }

      String var1 = this.selectedWidget;
      boolean var2 = this.disableWidget(var1);
      this.selectedWidget = null;
      if (var1.equals(this.draggingWidget)) {
         this.draggingWidget = null;
         this.editorAction = HUD.EditorAction.IDLE;
      }

      byte[] var10000 = new byte[5];
      var10000[0] = (byte)(-119);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(113);
      var10000[3] = (byte)(-79);
      var10000[4] = (byte)(81);
      if ($$v_s(var10000, -8444524616064184115L).equals(var1)) {
         this.armorMenuOpen = false;
      }

      if (var2) {
         try {
            Sfx.play(Sfx.BOOLEAN_DEACTIVATE);
         } catch (Throwable var4) {
         }

         this.requestEditorConfigSave();
         return true;
      } else {
         return false;
      }
   }

   public boolean disableWidget(String id) {
      if (id == null) {
         return false;
      }

      switch (id) {
         case "watermark":
            this.showWatermark.set(false);
            return true;
         case "fps":
            this.showFPS.set(false);
            return true;
         case "ping":
            this.showPing.set(false);
            return true;
         case "totems":
            this.showTotems.set(false);
            return true;
         case "time":
            this.showTime.set(false);
            return true;
         case "keybinds":
            this.showKeybinds.set(false);
            return true;
         case "potions":
            this.showPotions.set(false);
            return true;
         case "coords":
         case "coords_alt":
         case "coords_ow":
         case "coords_nether":
            this.showCoords.set(false);
            return true;
         case "compass":
            this.showCompass.set(false);
            return true;
         case "regionmap":
            this.showRegionMap.set(false);
            return true;
         case "radar":
            this.showRadar.set(false);
            return true;
         case "keystrokes":
            this.showKeystrokes.set(false);
            return true;
         case "playerhud":
            this.showPlayerHUD.set(false);
            return true;
         case "armor":
            this.showArmor.set(false);
            return true;
         case "modules":
            this.showModules.set(false);
            return true;
         case "spotify":
            try {
               MusicHUD var7 = (MusicHUD)VieClientMod.getInstance().getModuleManager().getModule(MusicHUD.class);
               if (var7 != null) {
                  var7.setEnabled(false);
                  return true;
               }
            } catch (Throwable var6) {
            }

            return false;
         case "stafflist":
            try {
               StaffLists var4 = (StaffLists)VieClientMod.getInstance().getModuleManager().getModule(StaffLists.class);
               if (var4 != null) {
                  var4.setEnabled(false);
                  return true;
               }
            } catch (Throwable var5) {
            }

            return false;
         default:
            return false;
      }
   }

   public boolean isInsideArmorMenu(double mx, double my) {
      return !this.armorMenuOpen ? false : mx >= this.armorMenuX && mx <= this.armorMenuX + 152.0F && my >= this.armorMenuY && my <= this.armorMenuY + 88.0F;
   }

   private void initArmorMenu(double mx, double my) {
      int var5 = mc != null && mc.method_22683() != null ? mc.method_22683().method_4486() : 800;
      int var6 = mc != null && mc.method_22683() != null ? mc.method_22683().method_4502() : 600;
      byte[] var10001 = new byte[5];
      var10001[0] = (byte)(-119);
      var10001[1] = (byte)(-24);
      var10001[2] = (byte)(113);
      var10001[3] = (byte)(-79);
      var10001[4] = (byte)(81);
      float[] var7 = (float[])this.widgetBounds.get($$v_s(var10001, -8444524616064184115L));
      float var8 = 152.0F;
      float var9 = 88.0F;
      if (var7 != null) {
         if (var7[0] + var7[2] + var8 + 8.0F <= var5) {
            this.armorMenuX = var7[0] + var7[2] + 8.0F;
            this.armorMenuY = var7[1];
         } else if (var7[0] - var8 - 8.0F >= 0.0F) {
            this.armorMenuX = var7[0] - var8 - 8.0F;
            this.armorMenuY = var7[1];
         } else {
            this.armorMenuX = Math.max(4.0F, Math.min(var5 - var8 - 4.0F, var7[0]));
            this.armorMenuY = var7[1] + var7[3] + 8.0F;
         }
      } else {
         this.armorMenuX = (float)mx + 4.0F;
         this.armorMenuY = (float)my + 4.0F;
      }

      this.armorMenuX = Math.max(4.0F, Math.min(var5 - var8 - 4.0F, this.armorMenuX));
      this.armorMenuY = Math.max(4.0F, Math.min(var6 - var9 - 4.0F, this.armorMenuY));
   }

   public boolean handleArmorMenuClick(double mx, double my) {
      if (!this.armorMenuOpen) {
         return false;
      }

      if (!this.isInsideArmorMenu(mx, my)) {
         return false;
      }

      float var5 = this.armorMenuX;
      float var6 = this.armorMenuY;
      if (mx >= var5 + 152.0F - 20.0F && mx <= var5 + 152.0F - 2.0F && my >= var6 + 2.0F && my <= var6 + 20.0F) {
         this.armorMenuOpen = false;

         try {
            Sfx.play(Sfx.CLICK_OFF);
         } catch (Throwable var10) {
         }

         return true;
      } else if (my >= var6 + 22.0F && my < var6 + 43.0F) {
         HUD.ArmorLayout var15 = (HUD.ArmorLayout)this.armorLayout.get();
         this.armorLayout.set(var15 == HUD.ArmorLayout.VERTICAL ? HUD.ArmorLayout.HORIZONTAL : HUD.ArmorLayout.VERTICAL);
         this.updateArmorBounds();
         this.requestEditorConfigSave();

         try {
            Sfx.play(Sfx.CLICK_ON);
         } catch (Throwable var11) {
         }

         return true;
      } else if (my >= var6 + 43.0F && my < var6 + 64.0F) {
         boolean var14 = (Boolean)this.armorShowHands.get();
         this.armorShowHands.set(!var14);
         this.updateArmorBounds();
         this.requestEditorConfigSave();

         try {
            if (!var14) {
               Sfx.play(Sfx.BOOLEAN_ACTIVATE);
            } else {
               Sfx.play(Sfx.BOOLEAN_DEACTIVATE);
            }
         } catch (Throwable var12) {
         }

         return true;
      } else if (my >= var6 + 64.0F && my <= var6 + 86.0F) {
         HUD.DuraMode var7 = (HUD.DuraMode)this.armorDuraMode.get();

         HUD.DuraMode var8 = switch (var7) {
            case PERCENT -> HUD.DuraMode.DAMAGE;
            case DAMAGE -> HUD.DuraMode.REMAINING;
            case REMAINING -> HUD.DuraMode.NONE;
            case NONE -> HUD.DuraMode.PERCENT;
         };
         this.armorDuraMode.set(var8);
         this.updateArmorBounds();
         this.requestEditorConfigSave();

         try {
            Sfx.play(Sfx.CLICK_ON);
         } catch (Throwable var13) {
         }

         return true;
      } else {
         return true;
      }
   }

   private void renderArmorMenu(int sw, int sh) {
      if (this.armorMenuOpen) {
         float var3 = this.armorMenuX;
         float var4 = this.armorMenuY;
         float var5 = 152.0F;
         float var6 = 88.0F;
         double var7 = mc.method_22683().method_4495();
         double var9 = mc.field_1729.method_1603() / var7;
         double var11 = mc.field_1729.method_1604() / var7;
         Color var13 = this.accent();
         NanoVGRenderer.drawRoundedRect(var3, var4, var5, var6, 6.0F, new Color(14, 18, 26, 245));
         NanoVGRenderer.drawRoundedRectOutline(var3, var4, var5, var6, 6.0F, 1.0F, new Color(255, 255, 255, 40));
         float var14 = 22.0F;
         NanoVGRenderer.drawRoundedRectVarying(var3, var4, var5, var14, 6.0F, 6.0F, 0.0F, 0.0F, new Color(22, 28, 38, 245));
         NanoVGRenderer.drawLine(var3, var4 + var14, var3 + var5, var4 + var14, 1.0F, new Color(255, 255, 255, 25));
         NanoVGRenderer.drawText(
            "Armor Status",
            var3 + 8.0F,
            var4 + 6.0F,
            8.0F,
            Color.WHITE,
            true
         );
         float var15 = var3 + var5 - 18.0F;
         float var16 = var4 + 4.0F;
         boolean var17 = var9 >= var15 - 2.0F && var9 <= var15 + 16.0F && var11 >= var16 - 2.0F && var11 <= var16 + 16.0F;
         if (var17) {
            NanoVGRenderer.drawRoundedRect(var15 - 2.0F, var16 - 2.0F, 16.0F, 16.0F, 3.0F, new Color(255, 60, 60, 80));
         }

         NanoVGRenderer.drawText("✕", var15 + 2.0F, var16 + 2.0F, 8.0F, var17 ? new Color(255, 100, 100) : new Color(160, 170, 190));
         float var18 = var4 + var14 + 2.0F;
         float var19 = 20.0F;
         boolean var20 = var9 >= var3 + 4.0F && var9 <= var3 + var5 - 4.0F && var11 >= var18 && var11 < var18 + var19;
         if (var20) {
            NanoVGRenderer.drawRoundedRect(var3 + 4.0F, var18, var5 - 8.0F, var19, 3.5F, new Color(255, 255, 255, 12));
         }

         NanoVGRenderer.drawText(
            "Layout", var3 + 8.0F, var18 + 5.5F, 7.5F, new Color(210, 215, 230)
         );
         String var21 = ((HUD.ArmorLayout)this.armorLayout.get()).name();
         float var22 = NanoVGRenderer.getTextWidth(var21, 6.5F) + 10.0F;
         float var23 = var3 + var5 - var22 - 8.0F;
         float var24 = var18 + 3.0F;
         NanoVGRenderer.drawRoundedRect(
            var23, var24, var22, 14.0F, 3.0F, var20 ? new Color(var13.getRed(), var13.getGreen(), var13.getBlue(), 60) : new Color(255, 255, 255, 20)
         );
         NanoVGRenderer.drawRoundedRectOutline(var23, var24, var22, 14.0F, 3.0F, 0.8F, var20 ? var13 : new Color(255, 255, 255, 40));
         NanoVGRenderer.drawText(var21, var23 + 5.0F, var24 + 3.5F, 6.5F, var20 ? Color.WHITE : var13, true);
         float var25 = var18 + var19;
         boolean var26 = var9 >= var3 + 4.0F && var9 <= var3 + var5 - 4.0F && var11 >= var25 && var11 < var25 + var19;
         if (var26) {
            NanoVGRenderer.drawRoundedRect(var3 + 4.0F, var25, var5 - 8.0F, var19, 3.5F, new Color(255, 255, 255, 12));
         }

         NanoVGRenderer.drawText("Hands", var3 + 8.0F, var25 + 5.5F, 7.5F, new Color(210, 215, 230));
         boolean var27 = (Boolean)this.armorShowHands.get();
         String var28 = var27 ? "ON" : "OFF";
         float var29 = 28.0F;
         float var30 = var3 + var5 - var29 - 8.0F;
         float var31 = var25 + 3.0F;
         Color var32 = var27 ? new Color(var13.getRed(), var13.getGreen(), var13.getBlue(), var26 ? 220 : 180) : new Color(60, 65, 75, 200);
         NanoVGRenderer.drawRoundedRect(var30, var31, var29, 14.0F, 3.0F, var32);
         NanoVGRenderer.drawRoundedRectOutline(var30, var31, var29, 14.0F, 3.0F, 0.8F, var27 ? var13 : new Color(255, 255, 255, 30));
         float var33 = NanoVGRenderer.getTextWidth(var28, 6.5F);
         NanoVGRenderer.drawText(var28, var30 + (var29 - var33) / 2.0F, var31 + 3.5F, 6.5F, Color.WHITE, true);
         float var34 = var25 + var19;
         boolean var35 = var9 >= var3 + 4.0F && var9 <= var3 + var5 - 4.0F && var11 >= var34 && var11 <= var34 + var19;
         if (var35) {
            NanoVGRenderer.drawRoundedRect(var3 + 4.0F, var34, var5 - 8.0F, var19, 3.5F, new Color(255, 255, 255, 12));
         }

         NanoVGRenderer.drawText(
            "Durability",
            var3 + 8.0F,
            var34 + 5.5F,
            7.5F,
            new Color(210, 215, 230)
         );
         String var36 = ((HUD.DuraMode)this.armorDuraMode.get()).name();
         float var37 = NanoVGRenderer.getTextWidth(var36, 6.5F) + 10.0F;
         float var38 = var3 + var5 - var37 - 8.0F;
         float var39 = var34 + 3.0F;
         NanoVGRenderer.drawRoundedRect(
            var38, var39, var37, 14.0F, 3.0F, var35 ? new Color(var13.getRed(), var13.getGreen(), var13.getBlue(), 60) : new Color(255, 255, 255, 20)
         );
         NanoVGRenderer.drawRoundedRectOutline(var38, var39, var37, 14.0F, 3.0F, 0.8F, var35 ? var13 : new Color(255, 255, 255, 40));
         NanoVGRenderer.drawText(var36, var38 + 5.0F, var39 + 3.5F, 6.5F, var35 ? Color.WHITE : var13, true);
      }
   }

   public void cacheExternalBounds(String id, float x, float y, float w, float h) {
      this.cacheBounds(id, x, y, w, h);
   }

   public void removeExternalBounds(String id) {
      this.widgetBaseBounds.remove(id);
      this.widgetBounds.remove(id);
      if (id != null && id.equals(this.draggingWidget)) {
         this.draggingWidget = null;
         this.editorAction = HUD.EditorAction.IDLE;
         this.activeCorner = null;
         this.resizeStartRect = null;
         this.resizeCursor.reset(mc.method_22683().method_4490());
      }
   }

   public void drawExternalEditorOutline(String id, float x, float y, float w, float h) {
      if (this.editorMode) {
         float[] var6 = (float[])this.widgetBounds.get(id);
         if (var6 == null) {
            float var7 = this.getWidgetScale(id);
            float[] var10000 = new float[4];
            var10000[0] = x;
            var10000[1] = y;
            var10000[2] = w * var7;
            var10000[3] = h * var7;
            var6 = var10000;
         }

         boolean var8 = id != null && (id.equals(this.draggingWidget) || id.equals(this.selectedWidget));
         this.drawEditorOutline(id, var6[0], var6[1], var6[2], var6[3], var8);
      }
   }

   public float wx(String id, float defaultX) {
      float[] var3 = (float[])this.widgetPos.get(id);
      float var4;
      if (var3 != null && this.userMovedWidgets.contains(id)) {
         var4 = var3[0];
      } else if (this.savedDefaultPositions.containsKey(id)) {
         var4 = ((float[])this.savedDefaultPositions.get(id))[0];
      } else {
         var4 = defaultX;
      }

      if (mc != null && mc.method_22683() != null) {
         int var5 = mc.method_22683().method_4486();
         float var6 = 1.0F;
         float[] var7 = (float[])this.widgetBounds.get(id);
         float var8 = var7 != null && var7[2] > 0.0F ? var7[2] : 0.0F;
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(-123);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(120);
         var10000[3] = (byte)(-85);
         var10000[4] = (byte)(79);
         var10000[5] = (byte)(-66);
         var10000[6] = (byte)(-65);
         if ($$v_s(var10000, -8444524616064184115L).equals(id)) {
            Float var10 = (Float)this.widgetRightOffset.get(id);
            if (var10 != null) {
               var4 = var5 - var10;
            }

            return Math.max(var6 + var8, Math.min(var5 - 1.0F, var4));
         } else {
            float var9 = Math.max(var6, var5 - var8 - 1.0F);
            return Math.max(var6, Math.min(var9, var4));
         }
      } else {
         return Math.max(1.0F, var4);
      }
   }

   public float wy(String id, float defaultY) {
      float[] var3 = (float[])this.widgetPos.get(id);
      float var4;
      if (var3 != null && this.userMovedWidgets.contains(id)) {
         var4 = var3[1];
      } else if (this.savedDefaultPositions.containsKey(id)) {
         var4 = ((float[])this.savedDefaultPositions.get(id))[1];
      } else {
         var4 = defaultY;
      }

      if (mc != null && mc.method_22683() != null) {
         int var5 = mc.method_22683().method_4502();
         float var6 = 1.0F;
         float[] var7 = (float[])this.widgetBounds.get(id);
         float var8 = var7 != null && var7[3] > 0.0F ? var7[3] : 0.0F;
         float var9 = Math.max(var6, var5 - var8 - 1.0F);
         return Math.max(var6, Math.min(var9, var4));
      } else {
         return Math.max(1.0F, var4);
      }
   }

   public void cacheBounds(String id, float x, float y, float w, float h) {
      float[] var10002 = new float[4];
      var10002[0] = x;
      var10002[1] = y;
      var10002[2] = w;
      var10002[3] = h;
      this.widgetBaseBounds.put(id, var10002);
      float var6 = this.getWidgetScale(id);
      var10002 = new float[4];
      var10002[0] = x;
      var10002[1] = y;
      var10002[2] = w * var6;
      var10002[3] = h * var6;
      this.widgetBounds.put(id, var10002);
   }

   public float[] getCachedBounds(String id) {
      float[] var2 = (float[])this.widgetBounds.get(id);
      return var2 == null ? null : (float[])var2.clone();
   }

   private HUD.ResizeTarget resizeTargetAt(double mx, double my) {
      if (this.draggingWidget != null && this.editorAction == HUD.EditorAction.RESIZE) {
         float[] var5 = (float[])this.widgetBounds.get(this.draggingWidget);
         if (var5 != null) {
            HudEditorGeometry.Rect var6 = rect(var5);
            HudEditorGeometry.Corner var7 = HudEditorGeometry.hitCorner(var6, mx, my, 6.0F);
            if (var7 != null) {
               return new HUD.ResizeTarget(this.draggingWidget, var6, var7);
            }
         }
      }

      ArrayList var11 = new ArrayList(this.widgetBounds.entrySet());
      HUD.ResizeTarget var12 = null;

      for (int var13 = var11.size() - 1; var13 >= 0; var13--) {
         Entry var8 = (Entry)var11.get(var13);
         HudEditorGeometry.Rect var9 = rect((float[])var8.getValue());
         HudEditorGeometry.Corner var10 = HudEditorGeometry.hitCorner(var9, mx, my, 6.0F);
         if (var10 != null) {
            byte[] var10000 = new byte[7];
            var10000[0] = (byte)(-123);
            var10000[1] = (byte)(-11);
            var10000[2] = (byte)(120);
            var10000[3] = (byte)(-85);
            var10000[4] = (byte)(79);
            var10000[5] = (byte)(-66);
            var10000[6] = (byte)(-65);
            if (!$$v_s(var10000, -8444524616064184115L).equals(var8.getKey())) {
               return new HUD.ResizeTarget((String)var8.getKey(), var9, var10);
            }

            if (var12 == null) {
               var12 = new HUD.ResizeTarget((String)var8.getKey(), var9, var10);
            }
         }
      }

      return var12;
   }

   private HudEditorGeometry.Corner hoveredCorner(double mx, double my) {
      HUD.ResizeTarget var5 = this.resizeTargetAt(mx, my);
      return var5 == null ? null : var5.corner();
   }

   private String widgetAt(double mx, double my) {
      if (this.draggingWidget != null) {
         float[] var5 = (float[])this.widgetBounds.get(this.draggingWidget);
         if (var5 != null && rect(var5).contains(mx, my)) {
            return this.draggingWidget;
         }
      }

      String var10 = null;
      ArrayList var6 = new ArrayList(this.widgetBounds.entrySet());

      for (int var7 = var6.size() - 1; var7 >= 0; var7--) {
         Entry var8 = (Entry)var6.get(var7);
         if (rect((float[])var8.getValue()).contains(mx, my)) {
            String var9 = (String)var8.getKey();
            byte[] var10000 = new byte[7];
            var10000[0] = (byte)(-123);
            var10000[1] = (byte)(-11);
            var10000[2] = (byte)(120);
            var10000[3] = (byte)(-85);
            var10000[4] = (byte)(79);
            var10000[5] = (byte)(-66);
            var10000[6] = (byte)(-65);
            if (!$$v_s(var10000, -8444524616064184115L).equals(var9)) {
               return var9;
            }

            if (var10 == null) {
               var10 = var9;
            }
         }
      }

      return var10;
   }

   private void applyResize(String id, HudEditorGeometry.ResizeResult result) {
      HudEditorGeometry.Rect var3 = result.rect();
      float var4 = var3.width() / result.scale();
      float var5 = var3.height() / result.scale();
      float var6 = Math.max(0.1F, this.masterHudScale());
      float var7 = result.scale() / var6;
      this.widgetScale.put(id, HudEditorGeometry.clampScale(var7));
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(-123);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(120);
      var10000[3] = (byte)(-85);
      var10000[4] = (byte)(79);
      var10000[5] = (byte)(-66);
      var10000[6] = (byte)(-65);
      float var8 = $$v_s(var10000, -8444524616064184115L).equals(id) ? var3.right() : var3.x();
      this.userMovedWidgets.add(id);
      this.storeWidgetPosition(id, var8, var3.y());
      Map var9 = this.widgetBaseBounds;
      float[] var10002 = new float[4];
      var10002[0] = var3.x();
      var10002[1] = var3.y();
      var10002[2] = var4;
      var10002[3] = var5;
      var9.put(id, var10002);
      Map var10 = this.widgetBounds;
      var10002 = new float[4];
      var10002[0] = var3.x();
      var10002[1] = var3.y();
      var10002[2] = var3.width();
      var10002[3] = var3.height();
      var10.put(id, var10002);
   }

   private void storeWidgetPosition(String id, float x, float y) {
      float[] var10002 = new float[2];
      var10002[0] = x;
      var10002[1] = y;
      this.widgetPos.put(id, var10002);
      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(-123);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(120);
      var10000[3] = (byte)(-85);
      var10000[4] = (byte)(79);
      var10000[5] = (byte)(-66);
      var10000[6] = (byte)(-65);
      if ($$v_s(var10000, -8444524616064184115L).equals(id) && mc != null && mc.method_22683() != null) {
         int var4 = mc.method_22683().method_4486();
         this.widgetRightOffset.put(id, var4 - x);
      }
   }

   private void recalculateBounds(String id) {
      float[] var2 = (float[])this.widgetBaseBounds.get(id);
      if (var2 != null) {
         float[] var3 = (float[])this.widgetPos.get(id);
         float var4 = this.getWidgetScale(id);
         float var5 = var2[2] * var4;
         float var6 = var2[3] * var4;
         float var7 = var3 == null ? var2[0] : var3[0];
         float var8 = var3 == null ? var2[1] : var3[1];
         float[] var10002 = new float[4];
         var10002[0] = var7;
         var10002[1] = var8;
         var10002[2] = var5;
         var10002[3] = var6;
         this.widgetBounds.put(id, var10002);
      }
   }

   private static HudEditorGeometry.Rect rect(float[] bounds) {
      return new HudEditorGeometry.Rect(bounds[0], bounds[1], bounds[2], bounds[3]);
   }

   private void requestEditorConfigSave() {
      try {
         VieClientMod.getInstance().getConfigManager().requestSave();
      } catch (Exception var2) {
      }
   }

   private float calcListBoxWidth(String header, List lines) {
      float var3 = 11.5F + NanoVGRenderer.getTextWidth(header, 8.5F);
      float var4 = 0.0F;

      for (String[] var6 : lines) {
         var3 = Math.max(var3, NanoVGRenderer.getTextWidth(var6[0], 8.5F));
         if (var6[1] != null && !var6[1].isEmpty()) {
            var4 = Math.max(var4, NanoVGRenderer.getTextWidth(var6[1], 8.5F));
         }
      }

      return var3 + (var4 > 0.0F ? var4 + 12.0F : 0.0F) + 14.0F;
   }

   private float calcListBoxHeight(List lines) {
      return (lines.size() + 1) * 10.5F + 8.0F + 2.0F;
   }

   private boolean overlaps(float[] a, float[] b, float pad) {
      return a[0] - pad < b[0] + b[2]
         && a[0] + a[2] + pad > b[0]
         && a[1] - pad < b[1] + b[3]
         && a[1] + a[3] + pad > b[1];
   }

   private void resolveOverlaps() {
      if (!this.editorLayersEnabled()) {
         if (this.draggingWidget != null) {
            if (!"spotify".equals(this.draggingWidget)
               && !"stafflist".equals(this.draggingWidget)
               && !"modules".equals(this.draggingWidget)
               && !"playerhud".equals(this.draggingWidget)) {
               float[] var1 = (float[])this.widgetBounds.get(this.draggingWidget);
               if (var1 != null) {
                  float var2 = 6.0F;
                  int var3 = mc.method_22683().method_4486();
                  int var4 = mc.method_22683().method_4502();

                  for (Entry var6 : this.widgetBounds.entrySet()) {
                     String var7 = (String)var6.getKey();
                     if (!var7.equals(this.draggingWidget)
                        && !"modules".equals(var7)
                        && !"spotify".equals(var7)
                        && !"stafflist".equals(var7)
                        && !"playerhud".equals(var7)) {
                        float[] var8 = (float[])var6.getValue();
                        if (this.overlaps(var1, var8, var2)) {
                           float var9 = var1[0] + var1[2] / 2.0F - (var8[0] + var8[2] / 2.0F);
                           float var10 = var1[1] + var1[3] / 2.0F - (var8[1] + var8[3] / 2.0F);
                           float var11 = var1[2] / 2.0F + var8[2] / 2.0F + var2 - Math.abs(var9);
                           float var12 = var1[3] / 2.0F + var8[3] / 2.0F + var2 - Math.abs(var10);
                           float[] var13 = this.widgetPos.getOrDefault(var7, new float[]{var8[0], var8[1]});
                           if (var11 < var12) {
                              float var14 = var13[0] + (var9 < 0.0F ? var11 : -var11);
                              float var15 = 1.0F;
                              float var16 = Math.max(var15, var3 - var8[2] - 1.0F);
                              var14 = Math.max(var15, Math.min(var16, var14));
                              var13 = new float[]{var14, var13[1]};
                           } else {
                              float var19 = var13[1] + (var10 < 0.0F ? var12 : -var12);
                              float var21 = 1.0F;
                              float var22 = Math.max(var21, var4 - var8[3] - 1.0F);
                              var19 = Math.max(var21, Math.min(var22, var19));
                              var13 = new float[]{var13[0], var19};
                           }

                           this.userMovedWidgets.add(var7);
                           this.widgetPos.put(var7, var13);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public float fadeIn(long shownMs) {
      if ((Boolean)this.animations.get() && !AnimationManager.isOff()) {
         float var3 = Math.min(1.0F, (float)(System.currentTimeMillis() - shownMs) / 240.0F);
         return hudEaseOutExpo(var3);
      } else {
         return 1.0F;
      }
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (VieClient.INSTANCE == null || VieClient.INSTANCE.GUI == null || mc.field_1755 != VieClient.INSTANCE.GUI) {
         if (mc.field_1724 != null && mc.field_1687 != null) {
            boolean var2 = mc.field_1755 instanceof class_408;
            if (var2 && !this.editorMode) {
               this.enterEditorMode();
            }

            if (!var2 && this.editorMode && !this.isDragging()) {
               this.exitEditorMode();
            }

            this.updateAnimations();
            CordNotification.tick();
            int var3 = mc.method_22683().method_4486();
            int var4 = mc.method_22683().method_4502();
            float var5 = mc.method_22683().method_4495();

            try (NanoVGFrame var6 = NanoVGRenderer.frame()) {
               if (!var6.isActive()) {
                  return;
               }

               this.blitHudFrost(var3, var4);
               this.ensureIcons();
               if (!this.hudWasInWorld) {
                  this.hudIntroStart = System.currentTimeMillis();
                  this.hudWasInWorld = true;
               }

               this.introIdx = 0;
               float var7 = 17.0F;
               float var8 = 17.0F;
               if ((Boolean)this.showWatermark.get()) {
                  float var9 = 4.0F;
                  float var10 = 4.0F;
                  float var11 = this.wx("watermark", var9);
                  float var12 = this.wy("watermark", var10);

                  float var13;
                  try (HUD.WidgetRenderScope var14 = this.beginWidgetRender("watermark", var11, var12)) {
                     var13 = this.box(var11, var12, "Vie Client", this.accent(), var5);
                  }

                  float var115 = var13 - var11 - 6.0F;
                  this.cacheBounds("watermark", var11, var12, var115, var7);
                  this.drawEditorOutlineForWidget("watermark");
               } else {
                  this.widgetBounds.remove("watermark");
                  this.widgetBaseBounds.remove("watermark");
               }

               if ((Boolean)this.showFPS.get()) {
                  float var59 = 4.0F + (this.showWatermark.get() ? NanoVGRenderer.getTextWidth("Vie Client", 8.5F) + 14.0F + 6.0F : 0.0F);
                  float var70 = 4.0F;
                  float var81 = this.wx("fps", var59);
                  float var92 = this.wy("fps", var70);

                  float var103;
                  try (HUD.WidgetRenderScope var116 = this.beginWidgetRender("fps", var81, var92)) {
                     String var15 = IconManager.has("monitor") ? "monitor" : "computer";
                     var103 = this.boxIMono(var81, var92, var15, mc.method_47599() + " FPS", txt(), var5);
                  }

                  float var117 = var103 - var81 - 6.0F;
                  this.cacheBounds("fps", var81, var92, var117, var8);
                  this.drawEditorOutlineForWidget("fps");
               } else {
                  this.widgetBounds.remove("fps");
                  this.widgetBaseBounds.remove("fps");
               }

               if ((Boolean)this.showPing.get() && mc.field_1724 != null) {
                  float var60 = 4.0F
                     + (this.showWatermark.get() ? NanoVGRenderer.getTextWidth("Vie Client", 8.5F) + 14.0F + 6.0F : 0.0F)
                     + (this.showFPS.get() ? 12.5F + NanoVGRenderer.getTextWidth("000 FPS", 8.5F) + 14.0F + 6.0F : 0.0F);
                  float var71 = 4.0F;
                  float var82 = this.wx("ping", var60);
                  float var93 = this.wy("ping", var71);

                  float var104;
                  try (HUD.WidgetRenderScope var118 = this.beginWidgetRender("ping", var82, var93)) {
                     var104 = this.boxIMono(var82, var93, "ping", this.getPing() + " MS", txt(), var5);
                  }

                  float var119 = var104 - var82 - 6.0F;
                  this.cacheBounds("ping", var82, var93, var119, var8);
                  this.drawEditorOutlineForWidget("ping");
               } else {
                  this.widgetBounds.remove("ping");
                  this.widgetBaseBounds.remove("ping");
               }

               if ((Boolean)this.showTotems.get() && mc.field_1724 != null) {
                  float var61 = 4.0F
                     + (this.showWatermark.get() ? NanoVGRenderer.getTextWidth("Vie Client", 8.5F) + 14.0F + 6.0F : 0.0F)
                     + (this.showFPS.get() ? 12.5F + NanoVGRenderer.getTextWidth("000 FPS", 8.5F) + 14.0F + 6.0F : 0.0F)
                     + (this.showPing.get() ? 12.5F + NanoVGRenderer.getTextWidth("000 MS", 8.5F) + 14.0F + 6.0F : 0.0F);
                  float var72 = 4.0F;
                  float var83 = this.wx("totems", var61);
                  float var94 = this.wy("totems", var72);

                  float var105;
                  try (HUD.WidgetRenderScope var120 = this.beginWidgetRender("totems", var83, var94)) {
                     var105 = this.box(var83, var94, this.countTotems() + " Totems", txt(), var5);
                  }

                  float var121 = var105 - var83 - 6.0F;
                  this.cacheBounds("totems", var83, var94, var121, var8);
                  this.drawEditorOutlineForWidget("totems");
               } else {
                  this.widgetBounds.remove("totems");
                  this.widgetBaseBounds.remove("totems");
               }

               if ((Boolean)this.showTime.get()) {
                  float var62 = 4.0F;
                  float var73 = 4.0F + var8 + 6.0F;
                  float var84 = this.wx("time", var62);
                  float var95 = this.wy("time", var73);
                  String var106 = getCachedTimeString();
                  float var122 = 12.5F + NanoVGRenderer.getTextWidth(var106, 8.5F) + 14.0F;

                  try (HUD.WidgetRenderScope var131 = this.beginWidgetRender("time", var84, var95)) {
                     this.boxIMono(var84, var95, "clock", var106, sub(), var5);
                  }

                  this.cacheBounds("time", var84, var95, var122, var8);
                  this.drawEditorOutlineForWidget("time");
               } else {
                  this.widgetBounds.remove("time");
               }

               if ((Boolean)this.showKeybinds.get()) {
                  List var63 = this.getKeybindLines();
                  float var74 = 4.0F;
                  float var85 = Math.max(1.0F, var4 / 2.0F + 58.0F + 6.0F);
                  float var96 = this.wx("keybinds", var74);
                  float var107 = this.wy("keybinds", var85);
                  float var123 = this.calcListBoxWidth("Keybinds", var63);
                  float var132 = this.calcListBoxHeight(var63);

                  try (HUD.WidgetRenderScope var16 = this.beginWidgetRender("keybinds", var96, var107)) {
                     this.drawListBox(var96, var107, "Keybinds", var63, var5);
                  }

                  this.cacheBounds("keybinds", var96, var107, var123, var132);
                  this.drawEditorOutlineForWidget("keybinds");
               } else {
                  this.widgetBounds.remove("keybinds");
               }

               if ((Boolean)this.showPotions.get() && mc.field_1724 != null) {
                  ArrayList var64 = new ArrayList(mc.field_1724.method_6026());
                  if (var64.isEmpty()) {
                     this.potionsWasVisible = false;
                     this.widgetBounds.remove("potions");
                  } else {
                     if (!this.potionsWasVisible) {
                        this.potionsShownMs = System.currentTimeMillis();
                     }

                     this.potionsWasVisible = true;
                     NanoVGRenderer.globalAlpha(this.fadeIn(this.potionsShownMs));
                     float var75 = 10.5F;
                     float var86 = 11.5F + NanoVGRenderer.getTextWidth("Potions", 8.5F);
                     float var97 = 0.0F;

                     for (class_1293 var124 : var64) {
                        String var133 = class_124.method_539(((class_1291)var124.method_5579().comp_349()).method_5560().getString());
                        int var141 = var124.method_5578();
                        if (var141 > 0) {
                           var133 = var133 + " " + this.toRoman(var141 + 1);
                        }

                        int var17 = var124.method_5584();
                        float var18 = var17 == 32767 ? 10.0F : NanoVGRenderer.getTextWidth(formatDuration(var17), 8.5F);
                        var86 = Math.max(var86, 11.5F + NanoVGRenderer.getTextWidth(var133, 8.5F));
                        var97 = Math.max(var97, var18);
                     }

                     float var109 = var86 + (var97 > 0.0F ? var97 + 12.0F : 0.0F) + 14.0F;
                     float var125 = (var64.size() + 1) * var75 + 8.0F + 2.0F;
                     float var134 = Math.max(1.0F, var3 - var109 - 4.0F);
                     float var142 = Math.max(1.0F, var4 - var125 - 4.0F);
                     float var148 = this.wx("potions", var134);
                     float var153 = this.wy("potions", var142);

                     try (HUD.WidgetRenderScope var19 = this.beginWidgetRender("potions", var148, var153)) {
                        float var20 = 5.5F;
                        drawCardBox(var148, var153, var109, var125, var20);
                        Color var21 = this.accent();
                        this.hudIcon(var148 + 7.0F, var153 + 4.0F, 8.5F, "potion", 4, var21);
                        NanoVGRenderer.drawText("Potions", var148 + 7.0F + 8.5F + 4.0F, var153 + 4.0F, 8.5F, var21);

                        for (int var22 = 0; var22 < var64.size(); var22++) {
                           class_1293 var23 = (class_1293)var64.get(var22);
                           float var24 = var153 + 4.0F + (var22 + 1) * var75;
                           class_2960 var25 = class_7923.field_41174.method_10221((class_1291)var23.method_5579().comp_349());
                           if (var25 != null) {
                              int var26 = this.potionIconCache
                                 .computeIfAbsent(var25.method_12832(), p -> NVGImage.loadImage("assets/minecraft/textures/mob_effect/" + p + ".png"));
                              if (var26 != -1) {
                                 NVGImage.drawImage(var26, var148 + 7.0F, var24 - 1.0F, 8.5F, 8.5F, Color.WHITE);
                              }
                           }

                           String var173 = class_124.method_539(((class_1291)var23.method_5579().comp_349()).method_5560().getString());
                           int var27 = var23.method_5578();
                           if (var27 > 0) {
                              var173 = var173 + " " + this.toRoman(var27 + 1);
                           }

                           NanoVGRenderer.drawText(var173, var148 + 7.0F + 8.5F + 4.0F, var24, 8.5F, txt());
                           int var28 = var23.method_5584();
                           if (var28 == 32767) {
                              NVGIcons.infinity(var148 + var109 - 7.0F - 5.0F, var24 + 4.25F, 9.0F, 1.3F, sub());
                           } else {
                              String var29 = formatDuration(var28);
                              float var30 = NanoVGRenderer.getTextWidth(var29, 8.5F);
                              NanoVGRenderer.drawText(var29, var148 + var109 - 7.0F - var30, var24, 8.5F, sub());
                           }
                        }
                     }

                     this.cacheBounds("potions", var148, var153, var109, var125);
                     this.drawEditorOutlineForWidget("potions");
                     NanoVGRenderer.globalAlpha(1.0F);
                  }
               } else {
                  this.potionsWasVisible = false;
                  this.widgetBounds.remove("potions");
               }

               if ((Boolean)this.showCoords.get() && mc.field_1724 != null) {
                  String var65 = mc.field_1687 == null ? "" : mc.field_1687.method_27983().method_29177().method_12832();
                  String var76 = owCoordText(mc.field_1724, var65);
                  String var87 = netherCoordText(mc.field_1724, var65);
                  float var98 = this.boxBlockW(var76);
                  float var110 = this.boxBlockW(var87);
                  float var126 = 4.0F;
                  float var135 = Math.max(1.0F, var4 - 16.0F - 6.0F);
                  float var143 = Math.max(1.0F, var135 - 16.0F - 6.0F);
                  float var149 = this.wx("coords_nether", var126);
                  float var154 = this.wy("coords_nether", var135);
                  float var158 = this.wx("coords_ow", var126);
                  float var162 = this.wy("coords_ow", var143);

                  try (HUD.WidgetRenderScope var166 = this.beginWidgetRender("coords_ow", var158, var162)) {
                     this.boxBlock(var158, var162, BlockIcon.grassTop(), var76, txt(), var5, true);
                  }

                  this.cacheBounds("coords_ow", var158, var162, var98, 16.0F);
                  this.drawEditorOutlineForWidget("coords_ow");

                  try (HUD.WidgetRenderScope var167 = this.beginWidgetRender("coords_nether", var149, var154)) {
                     this.boxBlock(var149, var154, BlockIcon.netherrack(), var87, sub(), var5, false);
                  }

                  this.cacheBounds("coords_nether", var149, var154, var110, 16.0F);
                  this.drawEditorOutlineForWidget("coords_nether");
                  this.widgetBounds.remove("coords");
                  this.widgetBounds.remove("coords_alt");
                  this.widgetBaseBounds.remove("coords");
                  this.widgetBaseBounds.remove("coords_alt");
               } else {
                  this.widgetBounds.remove("coords_ow");
                  this.widgetBounds.remove("coords_nether");
                  this.widgetBounds.remove("coords");
                  this.widgetBounds.remove("coords_alt");
                  this.widgetBaseBounds.remove("coords_ow");
                  this.widgetBaseBounds.remove("coords_nether");
                  this.widgetBaseBounds.remove("coords");
                  this.widgetBaseBounds.remove("coords_alt");
               }

               if ((Boolean)this.showKeystrokes.get() && mc.field_1724 != null) {
                  float var66 = 60.0F;
                  float var77 = 58.0F;
                  float var88 = this.showCoords.get() ? 38.0F : 0.0F;
                  float var99 = 4.0F;
                  float var111 = Math.max(1.0F, var4 - var88 - 6.0F - var77 - 6.0F);
                  float var127 = this.wx("keystrokes", var99);
                  float var136 = this.wy("keystrokes", var111);

                  try (HUD.WidgetRenderScope var144 = this.beginWidgetRender("keystrokes", var127, var136)) {
                     this.renderKeystrokes(var127, var136, var5);
                  }

                  this.cacheBounds("keystrokes", var127, var136, var66, var77);
                  this.drawEditorOutlineForWidget("keystrokes");
               } else {
                  this.widgetBounds.remove("keystrokes");
                  this.widgetBaseBounds.remove("keystrokes");
               }

               if ((Boolean)this.showCompass.get() && mc.field_1724 != null) {
                  float var67 = 180.0F;
                  float var78 = Math.max(1.0F, (var3 - var67) / 2.0F);
                  float var89 = 4.0F;
                  float var100 = this.wx("compass", var78);
                  float var112 = this.wy("compass", var89);
                  float var128 = CordNotification.expandFactor();
                  float var137 = CordNotification.active() ? NanoVGRenderer.getTextWidth(CordNotification.title(), 9.0F) : 0.0F;
                  float var145 = Math.max(216.0F, 32.0F + var137 + 24.0F);
                  float var150 = var67 + (var145 - var67) * var128;
                  float var155 = var7 + (26.0F - var7) * var128;
                  float var159 = var100 + var67 / 2.0F;

                  try (HUD.WidgetRenderScope var163 = this.beginWidgetRender("compass", var159 - var150 / 2.0F, var112)) {
                     this.renderCompass(var159 - var150 / 2.0F, var112, var150, var155, var128);
                  }

                  this.cacheBounds("compass", var159 - var150 / 2.0F, var112, var150, var155);
                  this.drawEditorOutlineForWidget("compass");
               } else {
                  this.widgetBounds.remove("compass");
                  this.widgetBaseBounds.remove("compass");
               }

               if ((Boolean)this.showModules.get()) {
                  float var68 = var3 - 4;
                  float var79 = 4.0F;
                  float var90 = this.wx("modules", var68);
                  float var101 = this.wy("modules", var79);
                  float[] var113 = this.renderModulesAndGetBounds(var90, var101, var5);
                  float var129 = this.getWidgetScale("modules");

                  try (HUD.WidgetRenderScope var138 = this.beginWidgetRender("modules", var90, var101)) {
                     this.renderModules(var90, var101, var5);
                  }

                  float var139 = var90 - var113[0];
                  float var146 = var101;
                  float var151 = var113[0];
                  float var156 = var113[1];
                  this.widgetBaseBounds.put("modules", new float[]{var139, var146, var151, var156});
                  float var160 = var90 - var113[0] * var129;
                  float var164 = var101;
                  float var168 = var113[0] * var129;
                  float var170 = var113[1] * var129;
                  this.widgetBounds.put("modules", new float[]{var160, var164, var168, var170});
                  this.widgetPos.put("modules", new float[]{var90, var101});
                  this.drawEditorOutlineForWidget("modules");
               } else {
                  this.widgetBounds.remove("modules");
                  this.widgetBaseBounds.remove("modules");
               }

               if ((Boolean)this.showRegionMap.get() && mc.field_1724 != null && mc.field_1687 != null) {
                  this.regionMapWidget.draw(this, (HUD.RegionMapMode)this.regionMapMode.get());
               } else {
                  this.regionMapWidget.onHidden();
                  this.widgetBounds.remove("regionmap");
                  this.widgetBaseBounds.remove("regionmap");
               }

               if ((Boolean)this.showRadar.get() && mc.field_1724 != null && mc.field_1687 != null) {
                  this.radarWidget.draw(this, (Integer)this.radarRange.get());
               } else {
                  this.radarWidget.onHidden();
                  this.widgetBounds.remove("radar");
                  this.widgetBaseBounds.remove("radar");
               }

               if ((Boolean)this.showPlayerHUD.get() && mc.field_1724 != null && mc.field_1687 != null) {
                  this.playerHudWidget.draw(this, (Integer)this.playerHudMax.get());
               } else {
                  this.playerHudWidget.onHidden();
                  this.widgetBounds.remove("playerhud");
                  this.widgetBaseBounds.remove("playerhud");
               }

               if ((Boolean)this.showArmor.get() && mc.field_1724 != null && mc.field_1687 != null) {
                  this.armorStatusWidget
                     .draw(this, (HUD.ArmorLayout)this.armorLayout.get(), (Boolean)this.armorShowHands.get(), (HUD.DuraMode)this.armorDuraMode.get());
               } else {
                  this.armorStatusWidget.onHidden();
                  this.widgetBounds.remove("armor");
                  this.widgetBaseBounds.remove("armor");
               }

               if (this.editorMode) {
                  this.resolveOverlaps();
                  this.renderSnapGuides(var3, var4);
                  String var69 = "HUD Editor — Drag to move • Drag corners to resize • Double right-click to reset scale • DEL to hide widget";
                  float var80 = NanoVGRenderer.getTextWidth(var69, 8.5F);
                  float var91 = var80 + 20.0F;
                  float var102 = 22.0F;
                  float var114 = var3 / 2.0F - var91 / 2.0F;
                  float var130 = var4 - 32.0F;
                  NanoVGRenderer.drawRoundedRect(var114, var130, var91, var102, var102 / 2.0F, new Color(14, 14, 20, 220));
                  if (!ThemeManager.isCleanStyle()) {
                     NanoVGRenderer.drawRoundedRectOutline(var114, var130, var91, var102, var102 / 2.0F, 1.0F, new Color(255, 255, 255, 30));
                  }

                  NanoVGRenderer.drawText(var69, var114 + 10.0F, var130 + 6.5F, 8.5F, new Color(210, 215, 235, 230));
                  float var140 = 12.0F;
                  String var147 = "Reset Positions & Sizes";
                  float var152 = NanoVGRenderer.getTextWidth(var147, 8.5F);
                  float var157 = var152 + var140 + 24.0F;
                  float var161 = 22.0F;
                  float var165 = var3 / 2.0F - var157 / 2.0F;
                  float var169 = var130 - var161 - 6.0F;
                  this.editorResetBtnRect = new float[]{var165, var169, var157, var161};
                  double var171 = mc.method_22683().method_4495();
                  double var172 = mc.field_1729.method_1603() / var171;
                  double var174 = mc.field_1729.method_1604() / var171;
                  boolean var175 = var172 >= var165 && var172 <= var165 + var157 && var174 >= var169 && var174 <= var169 + var161;
                  Color var176 = var175 ? new Color(225, 65, 65, 235) : new Color(18, 18, 26, 225);
                  NanoVGRenderer.drawRoundedRect(var165, var169, var157, var161, var161 / 2.0F, var176);
                  if (!ThemeManager.isCleanStyle()) {
                     Color var177 = var175 ? new Color(255, 130, 130, 240) : new Color(255, 255, 255, 35);
                     NanoVGRenderer.drawRoundedRectOutline(var165, var169, var157, var161, var161 / 2.0F, 1.0F, var177);
                  }

                  float var178 = var165 + 10.0F;
                  float var31 = var169 + (var161 - var140) / 2.0F;
                  int var32 = IconManager.id("reset");
                  if (var32 != -1) {
                     NanoVGRenderer.drawImage(var32, var178, var31, var140, var140, Color.WHITE);
                     NanoVGRenderer.drawText(var147, var178 + var140 + 4.0F, var169 + 6.5F, 8.5F, Color.WHITE, true);
                  } else {
                     NanoVGRenderer.drawText("↺  " + var147, var165 + 10.0F, var169 + 6.5F, 8.5F, Color.WHITE, true);
                  }

                  if (this.armorMenuOpen) {
                     this.renderArmorMenu(var3, var4);
                  }
               } else {
                  this.editorResetBtnRect = null;
               }

               ToastManager.render(var3, var4);
            }
         } else {
            this.hudWasInWorld = false;
         }
      }
   }

   private void renderSnapGuides(int sw, int sh) {
      if ((Boolean)this.snapping.get() && !this.activeSnapGuides.isEmpty()) {
         int var3 = -587212992;
         int var4 = -935279361;
         int var5 = 771741504;
         int var6 = 759219455;

         for (HudSnapping.Guide var8 : this.activeSnapGuides) {
            int var9 = var8.center() ? var3 : var4;
            int var10 = var8.center() ? var5 : var6;
            if (var8.vertical()) {
               NanoVGRenderer.drawLine(var8.position(), 0.0F, var8.position(), sh, 2.5F, var10);
               NanoVGRenderer.drawLine(var8.position(), 0.0F, var8.position(), sh, 1.0F, var9);
            } else {
               NanoVGRenderer.drawLine(0.0F, var8.position(), sw, var8.position(), 2.5F, var10);
               NanoVGRenderer.drawLine(0.0F, var8.position(), sw, var8.position(), 1.0F, var9);
            }
         }
      }
   }

   private void drawEditorOutline(float x, float y, float w, float h, boolean active) {
      this.drawEditorOutline(null, x, y, w, h, active);
   }

   private void drawEditorOutline(String id, float x, float y, float w, float h, boolean active) {
      boolean var7 = id != null && id.equals(this.selectedWidget);
      boolean var8 = active || var7;
      Color var9 = this.accent();
      Color var10 = var8 ? var9 : new Color(130, 150, 220, 160);
      NanoVGRenderer.drawRoundedRectOutline(x - 2.0F, y - 2.0F, w + 4.0F, h + 4.0F, 3.5F, var8 ? 1.5F : 1.0F, var10);
      if (var8) {
         NanoVGRenderer.drawRoundedRect(x - 2.0F, y - 2.0F, w + 4.0F, h + 4.0F, 3.5F, new Color(var9.getRed(), var9.getGreen(), var9.getBlue(), 22));
      }

      float var11 = 4.5F;
      float var12 = 1.2F;
      Color var13 = var8 ? var9 : new Color(245, 245, 255, 220);
      Color var14 = new Color(20, 20, 30, 230);
      float[][] var15 = new float[][]{{x - 2.0F, y - 2.0F}, {x + w + 2.0F, y - 2.0F}, {x - 2.0F, y + h + 2.0F}, {x + w + 2.0F, y + h + 2.0F}};

      for (float[] var19 : var15) {
         NanoVGRenderer.drawRoundedRect(var19[0] - var11 / 2.0F, var19[1] - var11 / 2.0F, var11, var11, var12, var13);
         NanoVGRenderer.drawRoundedRectOutline(var19[0] - var11 / 2.0F, var19[1] - var11 / 2.0F, var11, var11, var12, 0.75F, var14);
      }

      float var22 = id != null ? this.getWidgetScale(id) : 1.0F;
      if (var8 || Math.abs(var22 - 1.0F) > 0.02F) {
         String var23 = (id != null ? id.toUpperCase() : "WIDGET")
            + " • "
            + Math.round(var22 * 100.0F)
            + "%";
         float var24 = NanoVGRenderer.getTextWidth(var23, 7.5F) + 8.0F;
         float var25 = 13.0F;
         float var20 = x - 2.0F;
         float var21 = y - var25 - 4.0F;
         if (var21 < 4.0F) {
            var21 = y + h + 6.0F;
         }

         NanoVGRenderer.drawRoundedRect(var20, var21, var24, var25, 6.5F, new Color(18, 18, 24, 230));
         NanoVGRenderer.drawRoundedRectOutline(var20, var21, var24, var25, 6.5F, 0.8F, var8 ? var9 : new Color(255, 255, 255, 45));
         NanoVGRenderer.drawText(var23, var20 + 4.0F, var21 + 2.5F, 7.5F, var8 ? var9 : new Color(225, 225, 245, 240));
      }
   }

   public void drawEditorOutlineForWidget(String id) {
      if (this.editorMode || this.isDragging() || id != null && id.equals(this.selectedWidget)) {
         float[] var2 = (float[])this.widgetBounds.get(id);
         if (var2 != null) {
            boolean var3 = id != null && (id.equals(this.draggingWidget) || id.equals(this.selectedWidget));
            this.drawEditorOutline(id, var2[0], var2[1], var2[2], var2[3], var3);
         }
      }
   }

   private void ensureIcons() {
   }

   private float hudIntroP(int idx) {
      if ((Boolean)this.animations.get() && !AnimationManager.isOff()) {
         long var2 = System.currentTimeMillis() - this.hudIntroStart;
         long var4 = idx * 28L;
         if (var2 < var4) {
            return 0.0F;
         }

         float var6 = Math.min(1.0F, (float)(var2 - var4) / 280.0F);
         return hudEaseOutExpo(var6);
      } else {
         return 1.0F;
      }
   }

   private void introBegin(float p) {
      NanoVGRenderer.save();
      NanoVGRenderer.translate(0.0F, (1.0F - p) * -7.0F);
      NanoVGRenderer.globalAlpha(p);
   }

   private void introEnd() {
      NanoVGRenderer.globalAlpha(1.0F);
      NanoVGRenderer.restore();
   }

   public static void material(float x, float y, float w, float h, float e) {
      MaterialFX.innerTopRim(x, y, w, h, 7.0F, 1.0F, new Color(255, 255, 255, (int)(10.0F * e)));
   }

   public static void drawCardBox(float x, float y, float w, float h, float radius, float e) {
      if (ThemeManager.isCleanStyle()) {
         float var6 = Math.max(3.5F, radius * 1.5F);
         NanoVGRenderer.drawSquircleGradient(x, y, w, h, var6, SoftPalette.bodyTopInt(235.0F * e), SoftPalette.bodyBotInt(245.0F * e));
         NanoVGRenderer.drawSquircleOutline(x, y, w, h, var6, 1.0F, SoftPalette.whiteInt(22.0F * e));
      } else {
         MaterialFX.glassBody(x, y, w, h, radius, e);
         NanoVGRenderer.drawRoundedRectGradient(x, y, w, h, radius, bgTop(e), bgBot(e));
         NanoVGRenderer.drawRoundedRectOutline(x, y, w, h, radius, 1.0F, ol(e));
         GuiStyle.gloss(x, y, w, h, radius, e);
      }
   }

   public static void drawCardBox(float x, float y, float w, float h, float radius) {
      drawCardBox(x, y, w, h, radius, 1.0F);
   }

   private float box(float x, float y, String text, Color col, float sc) {
      float var6 = this.hudIntroP(this.introIdx++);
      this.introBegin(var6);
      float var7 = NanoVGRenderer.getTextWidth(text, 8.5F);
      float var8 = var7 + 14.0F;
      float var9 = 17.0F;
      float var10 = 5.5F;
      drawCardBox(x, y, var8, var9, var10);
      NanoVGRenderer.drawText(text, x + 7.0F, y + (var9 - 8.5F) / 2.0F, 8.5F, col);
      this.introEnd();
      return x + var8 + 6.0F;
   }

   private float boxI(float x, float y, String png, int vtype, String text, Color col, float sc) {
      return this.boxI(x, y, png, vtype, text, col, sc, 8.5F, false);
   }

   private float boxI(float x, float y, String png, int vtype, String text, Color col, float sc, float iconSz) {
      return this.boxI(x, y, png, vtype, text, col, sc, iconSz, false);
   }

   private float boxIMono(float x, float y, String png, String text, Color col, float sc) {
      return this.boxI(x, y, png, -1, text, col, sc, 8.5F, false);
   }

   private float boxI(float x, float y, String png, int vtype, String text, Color col, float sc, float iconSz, boolean mono) {
      float var10 = this.hudIntroP(this.introIdx++);
      this.introBegin(var10);
      float var11 = NanoVGRenderer.getTextWidth(text, 8.5F);
      float var12 = iconSz + 4.0F + var11 + 14.0F;
      float var13 = 17.0F;
      float var14 = 5.5F;
      drawCardBox(x, y, var12, var13, var14);
      this.hudIcon(x + 7.0F, y + (var13 - iconSz) / 2.0F, iconSz, png, vtype, col);
      NanoVGRenderer.drawText(text, x + 7.0F + iconSz + 4.0F, y + (var13 - 8.5F) / 2.0F, 8.5F, col);
      this.introEnd();
      return x + var12 + 6.0F;
   }

   private float boxBlock(float x, float y, int blockImageId, String text, Color col, float sc) {
      return this.boxBlock(x, y, blockImageId, text, col, sc, true);
   }

   private float boxBlock(float x, float y, int blockImageId, String text, Color col, float sc, boolean isOverworld) {
      float var8 = this.hudIntroP(this.introIdx++);
      this.introBegin(var8);
      float var9 = NanoVGRenderer.getTextWidth(text, 8.5F);
      float var10 = 12.0F;
      float var11 = 3.0F;
      float var12 = 4.0F;
      float var13 = 4.0F;
      float var14 = 5.0F;
      float var15 = var12 + var10 + var13 + var9 + var14;
      float var16 = 16.0F;
      float var17 = 5.0F;
      drawCardBox(x, y, var15, var16, var17);
      float var18 = y + (var16 - var10) / 2.0F;
      float var19 = x + var12;
      if (blockImageId != (-1)) {
         BlockIcon.drawRounded(blockImageId, var19, var18, var10, var11);
      } else {
         int var20 = isOverworld ? -11564230 : -9756640;
         NanoVGRenderer.drawRoundedRect(var19, var18, var10, var10, var11, var20);
      }

      NanoVGRenderer.drawText(text, x + var12 + var10 + var13, y + (var16 - 8.5F) / 2.0F, 8.5F, col);
      this.introEnd();
      return x + var15 + 6.0F;
   }

   private float boxBlockW(String text) {
      float var2 = NanoVGRenderer.getTextWidth(text, 8.5F);
      return 20.0F + var2 + 5.0F;
   }

   private static String owCoordText(class_1297 p, String dimPath) {
      if (p == null) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(-40);
         var10000[1] = (byte)(-74);
         var10000[2] = (byte)(60);
         var10000[3] = (byte)(-18);
         var10000[4] = (byte)(15);
         var10000[5] = (byte)(-5);
         var10000[6] = (byte)(-4);
         return $$v_s(var10000, -8444524616064184115L);
      }

      if (dimPath != null) {
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(-122);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-74);
         var10001[4] = (byte)(70);
         var10001[5] = (byte)(-87);
         if (dimPath.contains($$v_s(var10001, -8444524616064184115L))) {
            return Math.round(p.method_23317() * 8.0) + ", " + Math.round(p.method_23318()) + ", " + Math.round(p.method_23321() * 8.0);
         }
      }

      return Math.round(p.method_23317()) + ", " + Math.round(p.method_23318()) + ", " + Math.round(p.method_23321());
   }

   private static String netherCoordText(class_1297 p, String dimPath) {
      if (p == null) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(-40);
         var10000[1] = (byte)(-74);
         var10000[2] = (byte)(60);
         var10000[3] = (byte)(-18);
         var10000[4] = (byte)(15);
         var10000[5] = (byte)(-5);
         var10000[6] = (byte)(-4);
         return $$v_s(var10000, -8444524616064184115L);
      }

      if (dimPath != null) {
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(-122);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-74);
         var10001[4] = (byte)(70);
         var10001[5] = (byte)(-87);
         if (dimPath.contains($$v_s(var10001, -8444524616064184115L))) {
            return Math.round(p.method_23317()) + ", " + Math.round(p.method_23318()) + ", " + Math.round(p.method_23321());
         }
      }

      return Math.round(p.method_23317() / 8.0) + ", " + Math.round(p.method_23318()) + ", " + Math.round(p.method_23321() / 8.0);
   }

   private void hudIcon(float x, float y, float s, String png, int vtype, Color c) {
      if (png != null && IconManager.has(png)) {
         IconManager.draw(png, x, y, s, c.getAlpha());
      } else {
         float var7 = x + s / 2.0F;
         float var8 = y + s / 2.0F;
         float var9 = Math.max(1.0F, s * 0.11F);
         switch (vtype) {
            case 1:
               NanoVGRenderer.drawTriangle(var7, y, x + s * 0.12F, var8, x + s * 0.88F, var8, c);
               NanoVGRenderer.drawTriangle(x + s * 0.12F, var8, x + s * 0.88F, var8, var7, y + s, c);
               break;
            case 2:
               float var10 = s * 0.3F;
               float var11 = var7;
               float var12 = y + s * 0.36F;
               NanoVGRenderer.drawCircle(var11, var12, var10, c);
               NanoVGRenderer.drawTriangle(var11 - var10 * 0.9F, var12 + var10 * 0.38F, var11 + var10 * 0.9F, var12 + var10 * 0.38F, var11, y + s * 0.98F, c);
               NanoVGRenderer.drawCircle(var11, var12, var10 * 0.42F, new Color(0, 0, 0, 150));
               break;
            case 3:
               float var13 = x + s * 0.02F;
               float var14 = y + s * 0.24F;
               float var15 = s * 0.96F;
               float var16 = s * 0.52F;
               NanoVGRenderer.drawRoundedRect(var13, var14, var15, var16, s * 0.12F, c);
               Color var17 = new Color(0, 0, 0, 150);
               float var18 = s * 0.075F;
               float var19 = var14 + var16 * 0.34F;
               float var20 = var14 + var16 * 0.7F;
               NanoVGRenderer.drawCircle(var13 + var15 * 0.24F, var19, var18, var17);
               NanoVGRenderer.drawCircle(var13 + var15 * 0.5F, var19, var18, var17);
               NanoVGRenderer.drawCircle(var13 + var15 * 0.76F, var19, var18, var17);
               NanoVGRenderer.drawRoundedRect(var13 + var15 * 0.29F, var20 - var18 * 0.75F, var15 * 0.42F, var18 * 1.5F, var18 * 0.7F, var17);
               break;
            case 4:
               float var21 = s * 0.3F;
               float var22 = y + s * 0.63F;
               NanoVGRenderer.drawCircle(var7, var22, var21, c);
               NanoVGRenderer.drawRoundedRect(var7 - s * 0.1F, y + s * 0.12F, s * 0.2F, s * 0.34F, s * 0.05F, c);
               NanoVGRenderer.drawRect(var7 - s * 0.17F, y + s * 0.08F, s * 0.34F, s * 0.07F, c);
               NanoVGRenderer.drawCircle(var7, var22 + var21 * 0.12F, var21 * 0.5F, new Color(0, 0, 0, 120));
               break;
            case 5:
               NanoVGRenderer.drawRoundedRect(x + s * 0.12F, y + s * 0.18F, s * 0.76F, s * 0.18F, 1.0F, c);
               NanoVGRenderer.drawRoundedRect(x + s * 0.12F, y + s * 0.44F, s * 0.76F, s * 0.18F, 1.0F, c);
               NanoVGRenderer.drawRoundedRect(x + s * 0.12F, y + s * 0.7F, s * 0.5F, s * 0.18F, 1.0F, c);
               break;
            case 6:
               NanoVGRenderer.drawTriangle(var7, y, var7 - s * 0.26F, var8, var7 + s * 0.26F, var8, c);
               NanoVGRenderer.drawTriangle(var7 - s * 0.26F, var8, var7 + s * 0.26F, var8, var7, y + s, c);
               break;
            case 7:
               float var23 = y + s * 0.6F;
               float var24 = s * 0.37F;
               float var25 = (float) (-Math.PI / 2);
               float var26 = (float)Math.toRadians(102.0);
               NanoVGRenderer.drawArc(var7, var23, var24, var25 - var26, var25 + var26, var9, c);
               float var27 = var25 + (float)Math.toRadians(50.0);
               NanoVGRenderer.drawLine(var7, var23, var7 + (float)Math.cos(var27) * var24 * 0.78F, var23 + (float)Math.sin(var27) * var24 * 0.78F, var9, c);
               NanoVGRenderer.drawCircle(var7, var23, var9 * 0.9F, c);
               break;
            case 8:
               float var28 = s * 0.14F;
               float var29 = s * 0.09F;
               float var30 = x + s * 0.13F;
               float var31 = y + s * 0.84F;

               for (int var32 = 0; var32 < 4; var32++) {
                  float var33 = s * (0.24F + var32 * 0.16F);
                  NanoVGRenderer.drawRoundedRect(var30 + var32 * (var28 + var29), var31 - var33, var28, var33, var28 * 0.4F, c);
               }
         }
      }
   }

   private float drawListBox(float x, float y, String header, List lines, float sc) {
      float var6 = this.hudIntroP(this.introIdx++);
      this.introBegin(var6);
      float var7 = 10.5F;
      float var8 = 11.5F + NanoVGRenderer.getTextWidth(header, 8.5F);
      float var9 = 0.0F;

      for (String[] var11 : lines) {
         var8 = Math.max(var8, NanoVGRenderer.getTextWidth(var11[0], 8.5F));
         if (var11[1] != null && !var11[1].isEmpty()) {
            var9 = Math.max(var9, NanoVGRenderer.getTextWidth(var11[1], 8.5F));
         }
      }

      float var18 = var8 + (var9 > 0.0F ? var9 + 12.0F : 0.0F);
      float var19 = var18 + 14.0F;
      float var12 = (lines.size() + 1) * var7 + 8.0F + 2.0F;
      float var13 = 5.5F;
      drawCardBox(x, y, var19, var12, var13);
      Color var14 = this.accent();
      this.hudIcon(x + 7.0F, y + 4.0F, 8.5F, "keybind", 3, var14);
      NanoVGRenderer.drawText(header, x + 7.0F + 8.5F + 4.0F, y + 4.0F, 8.5F, txt());

      for (int var15 = 0; var15 < lines.size(); var15++) {
         float var16 = y + 4.0F + (var15 + 1) * var7;
         NanoVGRenderer.drawText(((String[])lines.get(var15))[0], x + 7.0F, var16, 8.5F, txt());
         if (((String[])lines.get(var15))[1] != null && !((String[])lines.get(var15))[1].isEmpty()) {
            float var17 = NanoVGRenderer.getTextWidth(((String[])lines.get(var15))[1], 8.5F);
            NanoVGRenderer.drawText(((String[])lines.get(var15))[1], x + var19 - 7.0F - var17, var16, 8.5F, sub());
         }
      }

      this.introEnd();
      return y + var12 + 6.0F;
   }

   private void updateClicks() {
      if (mc.field_1690 != null) {
         long var1 = System.currentTimeMillis();
         boolean var3 = mc.field_1690.field_1886.method_1434();
         if (var3 && !this.lastAttackPressed) {
            this.leftClicks.add(var1);
         }

         this.lastAttackPressed = var3;
         boolean var4 = mc.field_1690.field_1904.method_1434();
         if (var4 && !this.lastUsePressed) {
            this.rightClicks.add(var1);
         }

         this.lastUsePressed = var4;
         this.leftClicks.removeIf(t -> var1 - t > 1000L);
         this.rightClicks.removeIf(t -> var1 - t > 1000L);
      }
   }

   private void renderKeystrokes(float x, float y, float sc) {
      if (mc.field_1690 != null) {
         this.updateClicks();
         float var4 = 18.0F;
         float var5 = 3.0F;
         float var6 = (var4 * 3.0F + var5 * 2.0F - var5) / 2.0F;
         float var7 = 16.0F;
         Color var8 = this.accent();
         boolean var9 = mc.field_1690.field_1894.method_1434();
         boolean var10 = mc.field_1690.field_1913.method_1434();
         boolean var11 = mc.field_1690.field_1881.method_1434();
         boolean var12 = mc.field_1690.field_1849.method_1434();
         boolean var13 = mc.field_1690.field_1886.method_1434();
         boolean var14 = mc.field_1690.field_1904.method_1434();
         this.drawKey(x + var4 + var5, y, var4, var4, "W", null, var9, var8);
         float var15 = y + var4 + var5;
         this.drawKey(x, var15, var4, var4, "A", null, var10, var8);
         this.drawKey(x + var4 + var5, var15, var4, var4, "S", null, var11, var8);
         this.drawKey(x + (var4 + var5) * 2.0F, var15, var4, var4, "D", null, var12, var8);
         float var16 = var15 + var4 + var5;
         byte[] var10005 = new byte[3];
         var10005[0] = (byte)(-92);
         var10005[1] = (byte)(-41);
         var10005[2] = (byte)(94);
         this.drawKey(x, var16, var6, var7, $$v_s(var10005, -8444524616064184115L), this.leftClicks.size() + " CPS", var13, var8);
         float var10001 = x + var6 + var5;
         var10005 = new byte[3];
         var10005[0] = (byte)(-70);
         var10005[1] = (byte)(-41);
         var10005[2] = (byte)(94);
         this.drawKey(var10001, var16, var6, var7, $$v_s(var10005, -8444524616064184115L), this.rightClicks.size() + " CPS", var14, var8);
      }
   }

   private void drawKey(float kx, float ky, float kw, float kh, String key, String subText, boolean pressed, Color ac) {
      float var9 = pressed ? 1.0F : 0.0F;
      float var10 = this.animations.get() ? this.keyPressAnim.getOrDefault(key, 0.0F) : var9;
      if (var10 < var9) {
         var10 = Math.min(1.0F, var10 + 0.25F);
      } else if (var10 > var9) {
         var10 = Math.max(0.0F, var10 - 0.08F);
      }

      this.keyPressAnim.put(key, var10);
      float var11 = 5.5F;
      if (var10 <= 0.01F) {
         drawCardBox(kx, ky, kw, kh, var11);
      } else if (ThemeManager.isCleanStyle()) {
         float var12 = Math.max(3.5F, var11 * 1.5F);
         NanoVGRenderer.drawSquircle(kx, ky, kw, kh, var12, SoftPalette.mint((int)(200.0F * var10)));
         NanoVGRenderer.drawSquircleOutline(kx, ky, kw, kh, var12, 1.0F, SoftPalette.mint((int)(160.0F * var10)));
      } else {
         MaterialFX.glassBody(kx, ky, kw, kh, var11, 1.0F);
         int var16 = (int)(175.0F * MaterialFX.bodyTintMul() * var10);
         NanoVGRenderer.drawRoundedRectGradient(kx, ky, kw, kh, var11, GuiStyle.accent(var16), GuiStyle.accent2(var16));
         Color var13 = PlayerHudWidget.mixColors(ol(), ac, var10);
         NanoVGRenderer.drawRoundedRectOutline(kx, ky, kw, kh, var11, 1.0F, var13);
         GuiStyle.gloss(kx, ky, kw, kh, var11, 1.0F);
      }

      Color var17 = PlayerHudWidget.mixColors(txt(), Color.WHITE, var10);
      if (subText == null) {
         float var18 = NanoVGRenderer.getTextWidth(key, 8.5F);
         NanoVGRenderer.drawText(key, kx + (kw - var18) / 2.0F, ky + (kh - 8.5F) / 2.0F, 8.5F, var17, true);
      } else {
         float var19 = NanoVGRenderer.getTextWidth(key, 7.5F);
         NanoVGRenderer.drawText(key, kx + (kw - var19) / 2.0F, ky + 2.0F, 7.5F, var17, true);
         float var14 = NanoVGRenderer.getTextWidth(subText, 6.5F);
         Color var15 = PlayerHudWidget.mixColors(sub(), new Color(255, 255, 255, 200), var10);
         NanoVGRenderer.drawText(subText, kx + (kw - var14) / 2.0F, ky + 9.0F, 6.5F, var15);
      }
   }

   private boolean isWidgetActive(String id) {
      if (id == null) {
         return false;
      }

      return switch (id) {
         case "watermark" -> this.showWatermark.get();
         case "fps" -> this.showFPS.get();
         case "ping" -> this.showPing.get() && mc.field_1724 != null;
         case "totems" -> this.showTotems.get() && mc.field_1724 != null;
         case "time" -> this.showTime.get();
         case "keybinds" -> this.showKeybinds.get();
         case "potions" -> this.showPotions.get()
            && mc.field_1724 != null
            && !mc.field_1724.method_6026().isEmpty();
         case "coords" -> this.showCoords.get() && mc.field_1724 != null;
         case "coords_alt" -> this.showCoords.get()
            && mc.field_1724 != null
            && !ThemeManager.isCleanStyle()
            && !this.dimAlt().isEmpty();
         case "coords_ow" -> this.showCoords.get() && mc.field_1724 != null;
         case "coords_nether" -> this.showCoords.get()
            && mc.field_1724 != null;
         case "compass" -> this.showCompass.get() && mc.field_1724 != null;
         case "regionmap" -> this.showRegionMap.get()
            && mc.field_1724 != null
            && mc.field_1687 != null;
         case "radar" -> this.showRadar.get() && mc.field_1724 != null && mc.field_1687 != null;
         case "keystrokes" -> this.showKeystrokes.get() && mc.field_1724 != null;
         case "playerhud" -> this.showPlayerHUD.get()
            && mc.field_1724 != null
            && mc.field_1687 != null;
         case "armor" -> this.showArmor.get()
            && mc.field_1724 != null
            && mc.field_1687 != null
            && (
               this.editorMode
                  || this.isDragging()
                  || "armor".equals(this.selectedWidget)
                  || "armor".equals(this.draggingWidget)
                  || this.armorStatusWidget.hasAnyItems((Boolean)this.armorShowHands.get())
            );
         default -> true;
      };
   }

   private void blitHudFrost(int sw, int sh) {
      if (FrostedBackground.isFrostActive()) {
         float var3 = 17.0F;
         float var4 = 17.0F;
         float var5 = 5.5F;
         if ((Boolean)this.showWatermark.get()) {
            float var6 = this.wx("watermark", 4.0F);
            float var7 = this.wy("watermark", 4.0F);
            float var8 = this.getWidgetScale("watermark");
            float var9 = NanoVGRenderer.getTextWidth("Vie Client", 8.5F);
            float var10 = var9 + 14.0F;
            this.cacheBounds("watermark", var6, var7, var10, var3);
            FrostedBackground.queueLogicalRect(var6, var7, var10 * var8, var3 * var8, var5 * var8, false);
         } else {
            this.widgetBounds.remove("watermark");
            this.widgetBaseBounds.remove("watermark");
         }

         if ((Boolean)this.showFPS.get()) {
            float var20 = 4.0F + (this.showWatermark.get() ? NanoVGRenderer.getTextWidth("Vie Client", 8.5F) + 14.0F + 6.0F : 0.0F);
            float var31 = this.wx("fps", var20);
            float var42 = this.wy("fps", 4.0F);
            float var53 = this.getWidgetScale("fps");
            float var65 = NanoVGRenderer.getTextWidth(mc.method_47599() + " FPS", 8.5F);
            float var11 = 12.5F + var65 + 14.0F;
            this.cacheBounds("fps", var31, var42, var11, var4);
            FrostedBackground.queueLogicalRect(var31, var42, var11 * var53, var4 * var53, var5 * var53, false);
         } else {
            this.widgetBounds.remove("fps");
            this.widgetBaseBounds.remove("fps");
         }

         if ((Boolean)this.showPing.get() && mc.field_1724 != null) {
            float var21 = 4.0F
               + (this.showWatermark.get() ? NanoVGRenderer.getTextWidth("Vie Client", 8.5F) + 14.0F + 6.0F : 0.0F)
               + (this.showFPS.get() ? 12.5F + NanoVGRenderer.getTextWidth("000 FPS", 8.5F) + 14.0F + 6.0F : 0.0F);
            float var32 = this.wx("ping", var21);
            float var43 = this.wy("ping", 4.0F);
            float var54 = this.getWidgetScale("ping");
            float var66 = NanoVGRenderer.getTextWidth(this.getPing() + " MS", 8.5F);
            float var78 = 12.5F + var66 + 14.0F;
            this.cacheBounds("ping", var32, var43, var78, var4);
            FrostedBackground.queueLogicalRect(var32, var43, var78 * var54, var4 * var54, var5 * var54, false);
         } else {
            this.widgetBounds.remove("ping");
            this.widgetBaseBounds.remove("ping");
         }

         if ((Boolean)this.showTotems.get() && mc.field_1724 != null) {
            float var22 = 4.0F
               + (this.showWatermark.get() ? NanoVGRenderer.getTextWidth("Vie Client", 8.5F) + 14.0F + 6.0F : 0.0F)
               + (this.showFPS.get() ? 12.5F + NanoVGRenderer.getTextWidth("000 FPS", 8.5F) + 14.0F + 6.0F : 0.0F)
               + (this.showPing.get() ? 12.5F + NanoVGRenderer.getTextWidth("000 MS", 8.5F) + 14.0F + 6.0F : 0.0F);
            float var33 = this.wx("totems", var22);
            float var44 = this.wy("totems", 4.0F);
            float var55 = this.getWidgetScale("totems");
            float var67 = NanoVGRenderer.getTextWidth(this.countTotems() + " Totems", 8.5F);
            float var79 = var67 + 14.0F;
            this.cacheBounds("totems", var33, var44, var79, var4);
            FrostedBackground.queueLogicalRect(var33, var44, var79 * var55, var4 * var55, var5 * var55, false);
         } else {
            this.widgetBounds.remove("totems");
            this.widgetBaseBounds.remove("totems");
         }

         if ((Boolean)this.showTime.get()) {
            float var23 = this.wx("time", 4.0F);
            float var34 = this.wy("time", 4.0F + var4 + 6.0F);
            float var45 = this.getWidgetScale("time");
            String var56 = getCachedTimeString();
            float var68 = 12.5F + NanoVGRenderer.getTextWidth(var56, 8.5F) + 14.0F;
            this.cacheBounds("time", var23, var34, var68, var4);
            FrostedBackground.queueLogicalRect(var23, var34, var68 * var45, var4 * var45, var5 * var45, false);
         } else {
            this.widgetBounds.remove("time");
            this.widgetBaseBounds.remove("time");
         }

         if ((Boolean)this.showKeybinds.get()) {
            List var24 = this.getKeybindLines();
            float var35 = 4.0F + var4 + 6.0F + (this.showTime.get() ? var4 + 6.0F : 0.0F);
            float var46 = this.wx("keybinds", 4.0F);
            float var57 = this.wy("keybinds", var35);
            float var69 = this.getWidgetScale("keybinds");
            float var80 = this.calcListBoxWidth("Keybinds", var24);
            float var12 = this.calcListBoxHeight(var24);
            this.cacheBounds("keybinds", var46, var57, var80, var12);
            FrostedBackground.queueLogicalRect(var46, var57, var80 * var69, var12 * var69, var5 * var69, false);
         } else {
            this.widgetBounds.remove("keybinds");
            this.widgetBaseBounds.remove("keybinds");
         }

         if ((Boolean)this.showPotions.get() && mc.field_1724 != null) {
            ArrayList var25 = new ArrayList(mc.field_1724.method_6026());
            if (!var25.isEmpty()) {
               float var36 = 10.5F;
               float var47 = 11.5F + NanoVGRenderer.getTextWidth("Potions", 8.5F);
               float var58 = 0.0F;

               for (class_1293 var81 : var25) {
                  String var89 = class_124.method_539(((class_1291)var81.method_5579().comp_349()).method_5560().getString());
                  int var13 = var81.method_5578();
                  if (var13 > 0) {
                     var89 = var89 + " " + this.toRoman(var13 + 1);
                  }

                  int var14 = var81.method_5584();
                  float var15 = var14 == 32767 ? 10.0F : NanoVGRenderer.getTextWidth(formatDuration(var14), 8.5F);
                  var47 = Math.max(var47, 11.5F + NanoVGRenderer.getTextWidth(var89, 8.5F));
                  var58 = Math.max(var58, var15);
               }

               float var71 = var47 + (var58 > 0.0F ? var58 + 12.0F : 0.0F) + 14.0F;
               float var82 = (var25.size() + 1) * var36 + 8.0F + 2.0F;
               float var90 = 4.0F
                  + var4
                  + 6.0F
                  + (this.showTime.get() ? var4 + 6.0F : 0.0F)
                  + (this.showKeybinds.get() ? this.calcListBoxHeight(this.getKeybindLines()) + 6.0F : 0.0F);
               float var96 = this.wx("potions", 4.0F);
               float var103 = this.wy("potions", var90);
               float var109 = this.getWidgetScale("potions");
               this.cacheBounds("potions", var96, var103, var71, var82);
               FrostedBackground.queueLogicalRect(var96, var103, var71 * var109, var82 * var109, var5 * var109, false);
            } else {
               this.widgetBounds.remove("potions");
               this.widgetBaseBounds.remove("potions");
            }
         } else {
            this.widgetBounds.remove("potions");
            this.widgetBaseBounds.remove("potions");
         }

         if ((Boolean)this.showCoords.get() && mc.field_1724 != null) {
            String var26 = mc.field_1687 == null ? "" : mc.field_1687.method_27983().method_29177().method_12832();
            String var37 = owCoordText(mc.field_1724, var26);
            String var48 = netherCoordText(mc.field_1724, var26);
            float var59 = this.boxBlockW(var37);
            float var72 = this.boxBlockW(var48);
            float var83 = 5.0F;
            float var91 = Math.max(1.0F, sh - 16.0F - 6.0F);
            float var97 = Math.max(1.0F, var91 - 16.0F - 6.0F);
            float var104 = this.wx("coords_ow", 4.0F);
            float var110 = this.wy("coords_ow", var97);
            float var16 = this.getWidgetScale("coords_ow");
            this.cacheBounds("coords_ow", var104, var110, var59, 16.0F);
            FrostedBackground.queueLogicalRect(var104, var110, var59 * var16, 16.0F * var16, var83 * var16, false);
            float var17 = this.wx("coords_nether", 4.0F);
            float var18 = this.wy("coords_nether", var91);
            float var19 = this.getWidgetScale("coords_nether");
            this.cacheBounds("coords_nether", var17, var18, var72, 16.0F);
            FrostedBackground.queueLogicalRect(var17, var18, var72 * var19, 16.0F * var19, var83 * var19, false);
            this.widgetBounds.remove("coords");
            this.widgetBounds.remove("coords_alt");
            this.widgetBaseBounds.remove("coords");
            this.widgetBaseBounds.remove("coords_alt");
         } else {
            this.widgetBounds.remove("coords_ow");
            this.widgetBounds.remove("coords_nether");
            this.widgetBounds.remove("coords");
            this.widgetBounds.remove("coords_alt");
            this.widgetBaseBounds.remove("coords_ow");
            this.widgetBaseBounds.remove("coords_nether");
            this.widgetBaseBounds.remove("coords");
            this.widgetBaseBounds.remove("coords_alt");
         }

         if ((Boolean)this.showCompass.get() && mc.field_1724 != null) {
            float var27 = 180.0F;
            float var38 = Math.max(1.0F, (sw - var27) / 2.0F);
            float var49 = this.wx("compass", var38);
            float var60 = this.wy("compass", 4.0F);
            float var73 = CordNotification.expandFactor();
            float var84 = CordNotification.active() ? NanoVGRenderer.getTextWidth(CordNotification.title(), 9.0F) : 0.0F;
            float var92 = Math.max(216.0F, 32.0F + var84 + 24.0F);
            float var98 = var27 + (var92 - var27) * var73;
            float var105 = var3 + (26.0F - var3) * var73;
            float var111 = var49 + var27 / 2.0F;
            float var115 = var111 - var98 / 2.0F;
            float var119 = this.getWidgetScale("compass");
            this.cacheBounds("compass", var115, var60, var98, var105);
            float var122 = var5 * var119;
            FrostedBackground.queueLogicalRect(var115, var60, var98 * var119, var105 * var119, var122, false);
         } else {
            this.widgetBounds.remove("compass");
            this.widgetBaseBounds.remove("compass");
         }

         if ((Boolean)this.showRadar.get() && mc.field_1724 != null && mc.field_1687 != null) {
            float var28 = 4.0F;
            float var39 = Math.max(1.0F, sh / 2.0F - 58.0F);
            float var50 = this.wx("radar", var28);
            float var61 = this.wy("radar", var39);
            float var74 = this.getWidgetScale("radar");
            this.cacheBounds("radar", var50, var61, 116.0F, 116.0F);
            float var85 = 116.0F * var74;
            FrostedBackground.queueLogicalRect(var50, var61, var85, var85, var85 * 0.5F, false);
         } else {
            this.radarWidget.onHidden();
            this.widgetBounds.remove("radar");
            this.widgetBaseBounds.remove("radar");
         }

         if ((Boolean)this.showRegionMap.get() && mc.field_1724 != null && mc.field_1687 != null) {
            boolean var29 = mc.field_1687.method_27983() == class_1937.field_25180;
            boolean var40 = mc.field_1687.method_27983() == class_1937.field_25181;
            boolean var51 = !var29 && !var40;
            HUD.RegionMapMode var62 = (HUD.RegionMapMode)this.regionMapMode.get();
            float var75 = RegionMapWidget.calculateWidth(var62);
            float var86 = RegionMapWidget.calculateHeight(var51, var62);
            float var93 = Math.max(1.0F, sh - var86 - 4.0F);
            float[] var99;
            if ((var99 = this.getCachedBounds("coords_nether")) != null) {
               var93 = Math.max(1.0F, var99[1] - var86 - 6.0F);
            }

            if ((var99 = this.getCachedBounds("coords_ow")) != null) {
               var93 = Math.min(var93, Math.max(1.0F, var99[1] - var86 - 6.0F));
            }

            float var106 = this.wx("regionmap", 4.0F);
            float var112 = this.wy("regionmap", var93);
            float var116 = this.getWidgetScale("regionmap");
            this.cacheBounds("regionmap", var106, var112, var75, var86);
            FrostedBackground.queueLogicalRect(var106, var112, var75 * var116, var86 * var116, var5 * var116, false);
         } else {
            this.regionMapWidget.onHidden();
            this.widgetBounds.remove("regionmap");
            this.widgetBaseBounds.remove("regionmap");
         }

         boolean var30 = this.editorMode || this.isDragging() || "playerhud".equals(this.selectedWidget);
         int var41 = this.playerHudWidget.getDisplayCount((Integer)this.playerHudMax.get(), var30);
         boolean var52 = (Boolean)this.showPlayerHUD.get() && mc.field_1724 != null && mc.field_1687 != null && (var30 || var41 > 0);
         if (var52) {
            int var63 = Math.max(1, var41);
            float var76 = PlayerHudWidget.calculateHeight(var63);
            float var87 = Math.max(1.0F, Math.min(sw - 216.0F - 1.0F, 10.0F));
            float var94 = Math.max(1.0F, Math.min(sh - var76 - 4.0F, 160.0F));
            float var101 = this.wx("playerhud", var87);
            float var107 = this.wy("playerhud", var94);
            float var113 = this.getWidgetScale("playerhud");
            this.cacheBounds("playerhud", var101, var107, 216.0F, var76);
            float var117 = 5.5F * var113;

            for (int var120 = 0; var120 < var63; var120++) {
               float var123 = var107 + var120 * 51.0F * var113;
               FrostedBackground.queueLogicalRect(var101, var123, 216.0F * var113, 46.0F * var113, var117, false);
            }
         } else {
            this.playerHudWidget.onHidden();
            this.widgetBounds.remove("playerhud");
            this.widgetBaseBounds.remove("playerhud");
         }

         boolean var64 = this.editorMode || this.isDragging() || "armor".equals(this.selectedWidget) || "armor".equals(this.draggingWidget);
         boolean var77 = (Boolean)this.showArmor.get()
            && mc.field_1724 != null
            && mc.field_1687 != null
            && (var64 || this.armorStatusWidget.hasAnyItems((Boolean)this.armorShowHands.get()));
         if (var77) {
            float var88 = ArmorStatusWidget.calculateWidth(
               (HUD.ArmorLayout)this.armorLayout.get(), (Boolean)this.armorShowHands.get(), (HUD.DuraMode)this.armorDuraMode.get()
            );
            float var95 = ArmorStatusWidget.calculateHeight(
               (HUD.ArmorLayout)this.armorLayout.get(), (Boolean)this.armorShowHands.get(), (HUD.DuraMode)this.armorDuraMode.get()
            );
            float var102 = Math.max(1.0F, Math.min(sw - var88 - 1.0F, sw - var88 - 6.0F));
            float var108 = Math.max(1.0F, Math.min(sh - var95 - 1.0F, sh / 2.0F - var95 / 2.0F));
            float var114 = this.wx("armor", var102);
            float var118 = this.wy("armor", var108);
            float var121 = this.getWidgetScale("armor");
            this.cacheBounds("armor", var114, var118, var88, var95);
            float var124 = (ThemeManager.isCleanStyle() ? 8.0F : 5.5F) * var121;
            FrostedBackground.queueLogicalRect(var114, var118, var88 * var121, var95 * var121, var124, false);
         } else {
            this.armorStatusWidget.onHidden();
            this.widgetBounds.remove("armor");
            this.widgetBaseBounds.remove("armor");
         }

         this.blitKeystrokesKawase(sh);
         this.blitModulesKawase(sw);
         ToastManager.blitKawase(sw, sh);
         FrostedBackground.drawWidgetBackdrops();
      }
   }

   private void blitKeystrokesKawase(int sh) {
      if ((Boolean)this.showKeystrokes.get() && mc.field_1724 != null && mc.field_1690 != null) {
         float var2 = 17.0F;
         float var3 = 18.0F;
         float var4 = 3.0F;
         float var5 = var3 * 2.0F + var4 * 2.0F + 16.0F;
         float var6 = this.showCoords.get() ? 38.0F : 0.0F;
         float var7 = 4.0F;
         float var8 = Math.max(1.0F, sh - var6 - 4.0F - var5 - 6.0F);
         float var9 = this.wx("keystrokes", var7);
         float var10 = this.wy("keystrokes", var8);
         float var11 = this.getWidgetScale("keystrokes");
         float var12 = (var3 * 3.0F + var4 * 2.0F - var4) / 2.0F;
         float var13 = 16.0F;
         float var14 = 5.5F * var11;
         this.queueScaledKey(var9, var10, var9 + var3 + var4, var10, var3, var3, var11, var14);
         float var15 = var10 + var3 + var4;
         this.queueScaledKey(var9, var10, var9, var15, var3, var3, var11, var14);
         this.queueScaledKey(var9, var10, var9 + var3 + var4, var15, var3, var3, var11, var14);
         this.queueScaledKey(var9, var10, var9 + (var3 + var4) * 2.0F, var15, var3, var3, var11, var14);
         float var16 = var15 + var3 + var4;
         this.queueScaledKey(var9, var10, var9, var16, var12, var13, var11, var14);
         this.queueScaledKey(var9, var10, var9 + var12 + var4, var16, var12, var13, var11, var14);
      }
   }

   private void queueScaledKey(float anchorX, float anchorY, float kx, float ky, float kw, float kh, float scale, float rad) {
      float var9 = anchorX + (kx - anchorX) * scale;
      float var10 = anchorY + (ky - anchorY) * scale;
      float var11 = kw * scale;
      float var12 = kh * scale;
      FrostedBackground.queueLogicalRect(var9, var10, var11, var12, rad, false);
   }

   private void blitModulesKawase(int sw) {
      if ((Boolean)this.showModules.get()) {
         float var2 = sw - 4;
         float var3 = 4.0F;
         float var4 = this.getWidgetScale("modules");
         float var5 = this.wx("modules", var2);
         float var6 = this.wy("modules", var3);
         List var7 = this.getSorted();
         float var8 = 17.0F;
         float var9 = var8;
         float var10 = var6;
         float var11 = 5.5F * var4;

         for (Module var13 : var7) {
            float var14 = this.animations.get() ? this.moduleAnimations.getOrDefault(var13, 0.0F) : 1.0F;
            if (!(var14 < 0.01F)) {
               float var15 = hudEaseOutExpo(var14);
               String var16 = var13.getDisplayName();
               float var17 = NanoVGRenderer.getTextWidth(var16, 8.5F);
               float var18 = var17 + 16.5F;
               float var19 = var18 * var15;
               float var20 = (1.0F - easeOutCubic(var15)) * 12.0F;
               float var21 = var5 - var19 + var20;
               if (var19 >= 1.0F) {
                  float var22 = var5 + (var21 - var5) * var4;
                  float var23 = var6 + (var10 - var6) * var4;
                  float var24 = var19 * var4;
                  float var25 = var8 * var4;
                  FrostedBackground.queueLogicalRect(var22, var23, var24, var25, var11, false);
               }

               var10 += var9;
            }
         }
      }
   }

   private float[] renderModulesAndGetBounds(float anchorX, float anchorY, float sc) {
      float var4 = this.calculateModulesWidth();
      List var5 = this.getSorted();
      float var6 = 17.0F;
      float var7 = var6;
      float var8 = anchorY;

      for (Module var10 : var5) {
         float var11 = this.animations.get() ? this.moduleAnimations.getOrDefault(var10, 0.0F) : 1.0F;
         if (!(var11 < 0.01F)) {
            var8 += var7;
         }
      }

      float var12 = var8 - anchorY;
      if (var4 == 60.0F) {
         var12 = var6;
      }

      float[] var10000 = new float[2];
      var10000[0] = var4;
      var10000[1] = var12;
      return var10000;
   }

   private float calculateModulesWidth() {
      List var1 = this.getSorted();
      HUD.ModuleMode var2 = (HUD.ModuleMode)this.moduleMode.get();
      boolean var3 = var2 == HUD.ModuleMode.PILL;
      float var4 = 0.0F;

      for (Module var6 : var1) {
         float var7 = this.animations.get() ? this.moduleAnimations.getOrDefault(var6, 0.0F) : 1.0F;
         if (!(var7 < 0.01F)) {
            String var8 = var6.getDisplayName();
            float var9 = NanoVGRenderer.getTextWidth(var8, 8.5F);
            float var10 = var9 + 16.5F;
            var4 = Math.max(var4, var10);
         }
      }

      if (var4 == 0.0F) {
         var4 = 60.0F;
      }

      return var4;
   }

   private void renderModules(float anchorX, float anchorY, float sc) {
      List var4 = this.getSorted();
      float var5 = 17.0F;
      float var6 = var5;
      float var7 = anchorY;
      Color var8 = this.accent();
      float var9 = 5.5F;
      int var10 = 0;
      boolean var11 = ThemeManager.isRainbowTheme();

      for (Module var13 : var4) {
         float var14 = this.animations.get() ? this.moduleAnimations.getOrDefault(var13, 0.0F) : 1.0F;
         if (!(var14 < 0.01F)) {
            float var15 = hudEaseOutExpo(var14);
            String var16 = var13.getDisplayName();
            float var17 = NanoVGRenderer.getTextWidth(var16, 8.5F);
            float var18 = 2.5F;
            float var19 = var17 + 14.0F + var18;
            float var20 = var19 * var15;
            float var21 = (1.0F - easeOutCubic(var15)) * 12.0F;
            float var22 = anchorX - var20 + var21;
            int var23 = (int)(255.0F * var15);
            int var24 = var23 << (24) | txt().getRGB() & (16777215);
            drawCardBox(var22, var7, var20, var5, var9, var15);
            float var25 = var22 + var20 - var18;
            float var26 = var7;
            float var27 = var5;
            float var28 = ThemeManager.isCleanStyle() ? Math.max(3.5F, var9 * 1.5F) : var9;
            int var29;
            if (var11) {
               float var30 = (float)((System.currentTimeMillis() * 6.0E-4 + var10 * 0.045F) % 1.0);
               if (var30 < 0.0F) {
                  var30++;
               }

               int var31 = RainbowUtil.hsbToRgb(var30, 0.85F, 1.0F);
               var29 = (int)(255.0F * var15) << (24) | var31 & (16777215);
            } else {
               var29 = (int)(255.0F * var15) << (24) | var8.getRGB() & (16777215);
            }

            NanoVGRenderer.drawRoundedRectVarying(var25, var26, var18, var27, 0.0F, var28, var28, 0.0F, var29);
            NanoVGRenderer.drawText(var16, var22 + 7.0F * var15, var7 + (var5 - 8.5F) / 2.0F, 8.5F, var24);
            var7 += var6;
            var10++;
         }
      }
   }

   private static float easeOutCubic(float t) {
      float var1 = t - 1.0F;
      return 1.0F + var1 * var1 * var1;
   }

   private void renderCompass(float cx, float cy, float boxW, float boxH, float notifyE) {
      float var6 = (mc.field_1724.method_36454() % 360.0F + 360.0F) % 360.0F;
      String[] var7 = new String[]{
         "S",
         "SW",
         "W",
         "NW",
         "N",
         "NE",
         "E",
         "SE",
         "S"
      };
      float[] var8 = new float[]{0.0F, 45.0F, 90.0F, 135.0F, 180.0F, 225.0F, 270.0F, 315.0F, 360.0F};
      Color var9 = this.accent();
      float var10 = 17.0F;
      float var11 = 5.5F;
      drawCardBox(cx, cy, boxW, boxH, var11);
      float var12 = 2.5F;
      float var13 = cx + boxW / 2.0F;
      float var14 = 1.0F - clampF((notifyE - 0.45F) / 0.4F);

      for (int var15 = 0; var15 < var7.length; var15++) {
         float var16 = var8[var15] - var6;

         while (var16 > 180.0F) {
            var16 -= 360.0F;
         }

         while (var16 < -180.0F) {
            var16 += 360.0F;
         }

         if (!(Math.abs(var16) > 80.0F)) {
            float var17 = Math.max(0.0F, 1.0F - Math.abs(var16) / 80.0F) * var14;
            if (!(var17 <= 0.01F)) {
               float var18 = var13 - var16 * var12;
               if (!(var18 < cx) && !(var18 > cx + boxW)) {
                  float var19 = NanoVGRenderer.getTextWidth(var7[var15], 8.5F);
                  Color var20 = Math.abs(var16) < 4.0F
                     ? new Color(var9.getRed(), var9.getGreen(), var9.getBlue(), (int)(255.0F * var17))
                     : new Color(txt().getRed(), txt().getGreen(), txt().getBlue(), (int)(255.0F * var17));
                  NanoVGRenderer.drawText(var7[var15], var18 - var19 / 2.0F, cy + (var10 - 8.5F) / 2.0F, 8.5F, var20);
               }
            }
         }
      }

      if (notifyE > 0.02F && CordNotification.active()) {
         float var21 = clampF((notifyE - 0.3F) / 0.7F);
         if (var21 > 0.01F) {
            String var22 = CordNotification.title();
            int var23 = CordNotification.icon();
            float var24 = 14.0F;
            float var25 = cy + (boxH - var24) / 2.0F;
            if (var23 == 1) {
               this.drawWeatherIcon(cx + 12.0F, var25, var24, var9, true);
            } else if (var23 == 2) {
               this.drawWeatherIcon(cx + 12.0F, var25, var24, var9, false);
            } else {
               NanoVGRenderer.drawCircle(cx + 19.0F, cy + boxH / 2.0F, 3.4F, new Color(var9.getRed(), var9.getGreen(), var9.getBlue(), (int)(255.0F * var21)));
            }

            float var26 = NanoVGRenderer.getTextHeight(9.0F);
            NanoVGRenderer.drawText(
               var22, cx + 32.0F, cy + (boxH - var26) / 2.0F, 9.0F, new Color(txt().getRed(), txt().getGreen(), txt().getBlue(), (int)(255.0F * var21))
            );
         }
      }
   }

   private void drawWeatherIcon(float x, float y, float s, Color c, boolean rain) {
      Color var6 = new Color(c.getRed(), c.getGreen(), c.getBlue(), 255);
      if (rain) {
         float var7 = x + s * 0.42F;
         float var8 = y + s * 0.52F;
         NanoVGRenderer.drawCircle(var7 - s * 0.18F, var8 + s * 0.1F, s * 0.14F, var6);
         NanoVGRenderer.drawCircle(var7 + s * 0.04F, var8, s * 0.19F, var6);
         NanoVGRenderer.drawCircle(var7 + s * 0.26F, var8 + s * 0.12F, s * 0.13F, var6);
         NanoVGRenderer.drawRoundedRect(var7 - s * 0.24F, var8 + s * 0.02F, s * 0.55F, s * 0.16F, s * 0.08F, var6);
         float var9 = (float)(System.currentTimeMillis() % 500L) / 500.0F;

         for (int var10 = 0; var10 < (3); var10++) {
            float var11 = x + s * (0.22F + var10 * 0.16F);
            float var12 = y + s * (0.72F + (var9 + var10 * 0.22F) % 1.0F * 0.25F);
            NanoVGRenderer.drawLine(var11, var12, var11, var12 + s * 0.14F, 1.2F, var6);
         }
      } else {
         float var14 = x + s / 2.0F;
         float var15 = y + s / 2.0F;
         NanoVGRenderer.drawCircle(var14, var15, s * 0.24F, var6);

         for (int var16 = 0; var16 < (8); var16++) {
            double var17 = Math.toRadians(var16 * 45.0);
            float var18 = s * 0.36F;
            float var13 = s * 0.46F;
            NanoVGRenderer.drawLine(
               var14 + (float)Math.cos(var17) * var18,
               var15 + (float)Math.sin(var17) * var18,
               var14 + (float)Math.cos(var17) * var13,
               var15 + (float)Math.sin(var17) * var13,
               1.4F,
               var6
            );
         }
      }
   }

   private static float clampF(float v) {
      return Math.max(0.0F, Math.min(1.0F, v));
   }

   private List getKeybindLines() {
      ArrayList var1 = new ArrayList();

      for (Module var3 : VieClientMod.getInstance().getModuleManager().getModules()) {
         int var4 = var3.getKeybind() != null && var3.getKeybind().isBound() ? (Integer)var3.getKeybind().get() : -1;
         if (var4 > 0) {
            String[] var10001 = new String[2];
            var10001[0] = var3.getDisplayName();
            var10001[1] = this.keyName(var4);
            var1.add(var10001);
         }
      }

      return var1;
   }

   private int countTotems() {
      if (mc.field_1724 == null) {
         return 0;
      }

      int var1 = 0;

      for (int var2 = 0; var2 < mc.field_1724.method_31548().method_5439(); var2++) {
         class_1799 var3 = mc.field_1724.method_31548().method_5438(var2);
         if (var3.method_31574(class_1802.field_8288)) {
            var1 += var3.method_7947();
         }
      }

      return var1;
   }

   private int getPing() {
      if (mc.field_1724 != null && mc.method_1562() != null) {
         class_640 var1 = mc.method_1562().method_2871(mc.field_1724.method_5667());
         return var1 != null ? var1.method_2959() : 0;
      } else {
         return 0;
      }
   }

   private float coordChipW(String text) {
      return NanoVGRenderer.getTextWidth(text, 8.5F) + 14.0F;
   }

   private String dimAlt() {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         String var1 = mc.field_1687.method_27983().method_29177().method_12832();
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(-122);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-74);
         var10001[4] = (byte)(70);
         var10001[5] = (byte)(-87);
         if (var1.contains($$v_s(var10001, -8444524616064184115L))) {
            return "OW: " + Math.round(mc.field_1724.method_23317() * 8.0) + ", " + Math.round(mc.field_1724.method_23321() * 8.0);
         }

         var10001 = new byte[9];
         var10001[0] = (byte)(-121);
         var10001[1] = (byte)(-20);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-84);
         var10001[4] = (byte)(84);
         var10001[5] = (byte)(-76);
         var10001[6] = (byte)(-66);
         var10001[7] = (byte)(-50);
         var10001[8] = (byte)(-28);
         return var1.contains($$v_s(var10001, -8444524616064184115L))
            ? "Nether: " + Math.round(mc.field_1724.method_23317() / 8.0) + ", " + Math.round(mc.field_1724.method_23321() / 8.0)
            : "";
      } else {
         return "";
      }
   }

   private static String formatDuration(int durationTicks) {
      int var1 = durationTicks / (20);
      int var2 = var1 / (60);
      int var3 = var1 % (60);
      String var4;
      if (var3 < (10)) {
         byte[] var10001 = new byte[2];
         var10001[0] = (byte)(-46);
         var10001[1] = (byte)(-86);
         var4 = $$v_s(var10001, -8444524616064184115L);
      } else {
         var4 = ":";
      }

      return var2 + var4 + var3;
   }

   private static String formatCoordNums(double x, double y, double z) {
      return Math.round(x) + ", " + Math.round(y) + ", " + Math.round(z);
   }

   private String keyName(int k) {
      if (k <= 0) {
         return "NONE";
      }

      if (k < 0) {
         return "MB" + -k;
      }

      if (k >= 1000) {
         return "MB" + (k - 999);
      }

      return switch (k) {
         case 32 -> "SPACE";
         default -> "KEY" + k;
         case 39 -> "'";
         case 44 -> ",";
         case 45 -> "-";
         case 46 -> ".";
         case 47 -> "/";
         case 48 -> "0";
         case 49 -> "1";
         case 50 -> "2";
         case 51 -> "3";
         case 52 -> "4";
         case 53 -> "5";
         case 54 -> "6";
         case 55 -> "7";
         case 56 -> "8";
         case 57 -> "9";
         case 59 -> ";";
         case 61 -> "=";
         case 65 -> "A";
         case 66 -> "B";
         case 67 -> "C";
         case 68 -> "D";
         case 69 -> "E";
         case 70 -> "F";
         case 71 -> "G";
         case 72 -> "H";
         case 73 -> "I";
         case 74 -> "J";
         case 75 -> "K";
         case 76 -> "L";
         case 77 -> "M";
         case 78 -> "N";
         case 79 -> "O";
         case 80 -> "P";
         case 81 -> "Q";
         case 82 -> "R";
         case 83 -> "S";
         case 84 -> "T";
         case 85 -> "U";
         case 86 -> "V";
         case 87 -> "W";
         case 88 -> "X";
         case 89 -> "Y";
         case 90 -> "Z";
         case 91 -> "[";
         case 92 -> "\\";
         case 93 -> "]";
         case 96 -> "`";
         case 256 -> "ESC";
         case 257 -> "ENTER";
         case 258 -> "TAB";
         case 259 -> "BKSP";
         case 260 -> "INS";
         case 261 -> "DEL";
         case 262 -> "RIGHT";
         case 263 -> "LEFT";
         case 264 -> "DOWN";
         case 265 -> "UP";
         case 266 -> "PGUP";
         case 267 -> "PGDN";
         case 268 -> "HOME";
         case 269 -> "END";
         case 280 -> "CAPS";
         case 282 -> "NUMLK";
         case 290 -> "F1";
         case 291 -> "F2";
         case 292 -> "F3";
         case 293 -> "F4";
         case 294 -> "F5";
         case 295 -> "F6";
         case 296 -> "F7";
         case 297 -> "F8";
         case 298 -> "F9";
         case 299 -> "F10";
         case 300 -> "F11";
         case 301 -> "F12";
         case 320 -> "NUM0";
         case 321 -> "NUM1";
         case 322 -> "NUM2";
         case 323 -> "NUM3";
         case 324 -> "NUM4";
         case 325 -> "NUM5";
         case 326 -> "NUM6";
         case 327 -> "NUM7";
         case 328 -> "NUM8";
         case 329 -> "NUM9";
         case 330 -> "NUM.";
         case 331 -> "NUM/";
         case 332 -> "NUM*";
         case 333 -> "NUM-";
         case 334 -> "NUM+";
         case 335 -> "NUMENT";
         case 340 -> "LSHIFT";
         case 341 -> "LCTRL";
         case 342 -> "LALT";
         case 343 -> "LSUPER";
         case 344 -> "RSHIFT";
         case 345 -> "RCTRL";
         case 346 -> "RALT";
         case 347 -> "RSUPER";
         case 348 -> "MENU";
      };
   }

   private String toRoman(int n) {
      return switch (n) {
         case 1 -> "I";
         case 2 -> {
            byte[] var4 = new byte[2];
            var4[0] = (byte)(-95);
            var4[1] = (byte)(-45);
            yield $$v_s(var4, -8444524616064184115L);
         }
         case 3 -> {
            byte[] var3 = new byte[3];
            var3[0] = (byte)(-95);
            var3[1] = (byte)(-45);
            var3[2] = (byte)(85);
            yield $$v_s(var3, -8444524616064184115L);
         }
         case 4 -> {
            byte[] var2 = new byte[2];
            var2[0] = (byte)(-95);
            var2[1] = (byte)(-52);
            yield $$v_s(var2, -8444524616064184115L);
         }
         case 5 -> "V";
         default -> String.valueOf(n);
      };
   }

   private static float hudEaseOutExpo(float t) {
      return t >= 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0, -10.0F * t);
   }

   private List getSorted() {
      long var1 = System.nanoTime();
      if (this.cachedSortedMods != null && var1 - this.lastSortedFrameNanos < 2000000L) {
         return this.cachedSortedMods;
      }

      this.cachedSortedMods = this.moduleOrder
         .update(VieClientMod.getInstance().getModuleManager().getModules(), Module::isEnabled, m -> NanoVGRenderer.getTextWidth(m.getName(), 8.5F));
      this.lastSortedFrameNanos = var1;
      return this.cachedSortedMods;
   }

   private void updateAnimations() {
      long var1 = System.currentTimeMillis();
      float var3 = Math.min((float)(var1 - this.lastFrameTime) / 16.67F, 3.0F);
      this.lastFrameTime = var1;

      for (Module var5 : VieClientMod.getInstance().getModuleManager().getModules()) {
         if (var5.isEnabled()) {
            float var6 = this.moduleAnimations.getOrDefault(var5, 0.0F);
            if (var6 < 1.0F) {
               this.moduleAnimations.put(var5, Math.min(var6 + 0.08F * var3, 1.0F));
            }
         }
      }

      Iterator var7 = this.moduleAnimations.entrySet().iterator();

      while (var7.hasNext()) {
         Entry var8 = (Entry)var7.next();
         if (!((Module)var8.getKey()).isEnabled()) {
            float var9 = (Float)var8.getValue() - 0.08F * var3;
            if (var9 <= 0.0F) {
               var7.remove();
            } else {
               var8.setValue(var9);
            }
         }
      }
   }

   static {
      byte[] var10002 = new byte[8];
      var10002[0] = (byte)(-96);
      var10002[1] = (byte)(-46);
      var10002[2] = (byte)(38);
      var10002[3] = (byte)(-77);
      var10002[4] = (byte)(78);
      var10002[5] = (byte)(-31);
      var10002[6] = (byte)(-65);
      var10002[7] = (byte)(-47);
      TIME_FMT = new SimpleDateFormat($$v_s(var10002, -8444524616064184115L));
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

   public enum ArmorLayout {
      VERTICAL,
      HORIZONTAL;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static HUD.ArmorLayout[] $values() {
         HUD.ArmorLayout[] var10000 = new HUD.ArmorLayout[2];
         var10000[0] = VERTICAL;
         var10000[1] = HORIZONTAL;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[8];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-25);
         var10002[5] = (byte)(-114);
         var10002[6] = (byte)(-46);
         var10002[7] = (byte)(29);
         VERTICAL = new HUD.ArmorLayout();
         var10002 = new byte[10];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-12);
         var10002[5] = (byte)(-126);
         var10002[6] = (byte)(-35);
         var10002[7] = (byte)(5);
         var10002[8] = (byte)(16);
         var10002[9] = (byte)(-18);
         HORIZONTAL = new HUD.ArmorLayout();
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

   public enum DuraMode {
      PERCENT,
      DAMAGE,
      REMAINING,
      NONE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static HUD.DuraMode[] $values() {
         HUD.DuraMode[] var10000 = new HUD.DuraMode[4];
         var10000[0] = PERCENT;
         var10000[1] = DAMAGE;
         var10000[2] = REMAINING;
         var10000[3] = NONE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(33);
         var10002[5] = (byte)(-23);
         var10002[6] = (byte)(-27);
         PERCENT = new HUD.DuraMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(35);
         var10002[5] = (byte)(-30);
         DAMAGE = new HUD.DuraMode();
         var10002 = new byte[9];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(45);
         var10002[5] = (byte)(-23);
         var10002[6] = (byte)(-8);
         var10002[7] = (byte)(-92);
         var10002[8] = (byte)(16);
         REMAINING = new HUD.DuraMode();
         var10002 = new byte[4];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new HUD.DuraMode();
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

   private enum EditorAction {
      IDLE,
      MOVE,
      RESIZE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static HUD.EditorAction[] $values() {
         HUD.EditorAction[] var10000 = new HUD.EditorAction[3];
         var10000[0] = IDLE;
         var10000[1] = MOVE;
         var10000[2] = RESIZE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-34);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-101);
         IDLE = new HUD.EditorAction();
         var10002 = new byte[4];
         var10002[0] = (byte)(117);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(74);
         var10002[3] = (byte)(-101);
         MOVE = new HUD.EditorAction();
         var10002 = new byte[6];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(126);
         var10002[5] = (byte)(83);
         RESIZE = new HUD.EditorAction();
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

   public enum ModuleMode {
      CARD,
      PILL;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static HUD.ModuleMode[] $values() {
         HUD.ModuleMode[] var10000 = new HUD.ModuleMode[2];
         var10000[0] = CARD;
         var10000[1] = PILL;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-102);
         CARD = new HUD.ModuleMode();
         var10002 = new byte[4];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-110);
         PILL = new HUD.ModuleMode();
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

   public enum RegionMapMode {
      OLD,
      NEW;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static HUD.RegionMapMode[] $values() {
         HUD.RegionMapMode[] var10000 = new HUD.RegionMapMode[2];
         var10000[0] = OLD;
         var10000[1] = NEW;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(88);
         OLD = new HUD.RegionMapMode();
         var10002 = new byte[3];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(75);
         NEW = new HUD.RegionMapMode();
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

   private record ResizeTarget(String id, HudEditorGeometry.Rect rect, HudEditorGeometry.Corner corner) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public final class WidgetRenderScope implements AutoCloseable {
      private final boolean transformed;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private WidgetRenderScope(String id, float x, float y) {
         float var5 = HUD.this.getWidgetScale(id);
         this.transformed = Math.abs(var5 - 1.0F) > 1.0E-4F;
         if (this.transformed) {
            NanoVGRenderer.save();
            NanoVGRenderer.translate(x, y);
            NanoVGRenderer.scale(var5, var5);
            NanoVGRenderer.translate(-x, -y);
         }
      }

      @Override
      public void close() {
         if (this.transformed) {
            NanoVGRenderer.restore();
         }
      }
   }
}
