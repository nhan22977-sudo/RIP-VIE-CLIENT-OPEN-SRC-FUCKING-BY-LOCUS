package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.gui.nvg.adapter.module.Module;
import me.drake.vieclient.gui.nvg.adapter.modules.client.MusicHUD;
import me.drake.vieclient.gui.nvg.adapter.modules.client.Sounds;
import me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient;
import me.drake.vieclient.gui.nvg.adapter.modules.render.HUD;
import me.drake.vieclient.gui.nvg.adapter.setting.BindSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.BlocksSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.BooleanSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.ButtonSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.ColorSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.FriendsSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.ItemSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.MacroSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.ModeSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.NumberSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.Setting;
import me.drake.vieclient.gui.nvg.adapter.setting.StringSetting;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.FrostedBackground;
import me.drake.vieclient.render.nvg.NVGIcons;
import me.drake.vieclient.render.nvg.NanoVGFontManager;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.Sfx;
import net.minecraft.class_310;

public class VieOSPage {
   private static final String[] SECTIONS;
   private static float PW = 320.0F;
   private static final float MAX_H = 300.0F;
   private static final float HEADER_H = 34.0F;
   private static final float FOOTER_H = 20.0F;
   private static final float PAD = 6.0F;
   private static final float ROW_H = 24.0F;
   private static final float SLIDER_ROW_H = 34.0F;
   private static final float RADIUS = 10.0F;
   private int selected = 0;
   private Module popupModule;
   private String popupTitle;
   private boolean pageOpen = false;
   private float pageAnim = 0.0F;
   private float scroll = 0.0F;
   private float targetScroll = 0.0F;
   private float maxScroll = 0.0F;
   private NumberSetting draggingNum = null;
   private BindSetting listeningBind = null;
   private ColorPicker colorPicker = null;
   private final Map togAnim = new HashMap();
   private ModeSetting openDropdown;
   private float openDropdownTarget = 0.0F;
   private float openDropdownAnim = 0.0F;
   private float ddScroll = 0.0F;
   private float ddTargetScroll = 0.0F;
   private float ddMaxScroll = 0.0F;
   private boolean draggingDdScroll = false;
   private final float[] ddBoxRect = new float[4];
   private final float[] ddListRect = new float[4];
   private int ddDir = 1;
   private static final float DD_BOX_H = 18.0F;
   private static final float DD_OPT_H = 18.0F;
   private Module hudMod;
   private Module musicMod;
   private float px;
   private float py;
   private float ph;
   private final float[] backRect = new float[4];
   private final float[] closeRect = new float[4];
   private final float[] headerToggle = new float[4];
   private final List rows = new ArrayList();
   private int screenW;
   private int screenH;
   private Object lastHoveredRowKey = null;
   private long lastRowHoverSoundAt = 0L;
   private double lastMouseXForSound = -1.0;
   private double lastMouseYForSound = -1.0;
   private long openedAt = 0L;
   private List discordRows;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public boolean isPageOpen() {
      return this.pageOpen || this.pageAnim > 0.01F;
   }

   public boolean isOverlayOpen() {
      return this.isPageOpen();
   }

   public void openPage(int section) {
      this.openPage(section, null);
   }

   public void openPage(int section, String customTitle) {
      this.stopListening();
      this.selected = section;
      this.popupModule = null;
      if (customTitle != null && !customTitle.isBlank()) {
         this.popupTitle = customTitle;
      } else if (section == 1) {
         byte[] var10001 = new byte[7];
         var10001[0] = (byte)(12);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(127);
         var10001[3] = (byte)(-16);
         var10001[4] = (byte)(-43);
         var10001[5] = (byte)(34);
         var10001[6] = (byte)(38);
         String var3 = $$v_s(var10001, 6431572394383757402L);
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(12);
         var10002[1] = (byte)(-1);
         var10002[2] = (byte)(127);
         var10002[3] = (byte)(-16);
         var10002[4] = (byte)(-37);
         var10002[5] = (byte)(39);
         var10002[6] = (byte)(49);
         String var4 = $$v_s(var10002, 6431572394383757402L);
         byte[] var10003 = new byte[12];
         var10003[0] = (byte)(55);
         var10003[1] = (byte)(-49);
         var10003[2] = (byte)(88);
         var10003[3] = (byte)(-2);
         var10003[4] = (byte)(-18);
         var10003[5] = (byte)(50);
         var10003[6] = (byte)(54);
         var10003[7] = (byte)(5);
         var10003[8] = (byte)(126);
         var10003[9] = (byte)(-52);
         var10003[10] = (byte)(-125);
         var10003[11] = (byte)(-123);
         this.popupTitle = Localization.get(var3, Localization.get(var4, $$v_s(var10003, 6431572394383757402L)));
      } else {
         this.popupTitle = null;
      }

      this.open();
   }

   public void openModuleOverlay(Module module) {
      this.openModuleOverlay(module, null);
   }

   public void openModuleOverlay(Module module, String customTitle) {
      this.stopListening();
      this.popupModule = module;
      if (customTitle != null && !customTitle.isBlank()) {
         this.popupTitle = customTitle;
      } else {
         label37: {
            label39: {
               if (module != null) {
                  byte[] var10000 = new byte[3];
                  var10000[0] = (byte)(55);
                  var10000[1] = (byte)(-49);
                  var10000[2] = (byte)(88);
                  if ($$v_s(var10000, 6431572394383757402L).equalsIgnoreCase(module.getName().toString())) {
                     break label39;
                  }

                  var10000 = new byte[3];
                  var10000[0] = (byte)(55);
                  var10000[1] = (byte)(-49);
                  var10000[2] = (byte)(88);
                  if ($$v_s(var10000, 6431572394383757402L).equalsIgnoreCase(module.getDisplayName().toString())) {
                     break label39;
                  }
               }

               String var4;
               if (module != null && module.getDisplayName() != null) {
                  var4 = module.getDisplayName().toString();
               } else {
                  byte[] var10001 = new byte[8];
                  var10001[0] = (byte)(44);
                  var10001[1] = (byte)(-1);
                  var10001[2] = (byte)(104);
                  var10001[3] = (byte)(-86);
                  var10001[4] = (byte)(-44);
                  var10001[5] = (byte)(57);
                  var10001[6] = (byte)(37);
                  var10001[7] = (byte)(2);
                  var4 = $$v_s(var10001, 6431572394383757402L);
               }

               this.popupTitle = var4;
               break label37;
            }

            byte[] var5 = new byte[7];
            var5[0] = (byte)(12);
            var5[1] = (byte)(-1);
            var5[2] = (byte)(127);
            var5[3] = (byte)(-16);
            var5[4] = (byte)(-43);
            var5[5] = (byte)(34);
            var5[6] = (byte)(38);
            String var6 = $$v_s(var5, 6431572394383757402L);
            byte[] var10002 = new byte[7];
            var10002[0] = (byte)(12);
            var10002[1] = (byte)(-1);
            var10002[2] = (byte)(127);
            var10002[3] = (byte)(-16);
            var10002[4] = (byte)(-37);
            var10002[5] = (byte)(39);
            var10002[6] = (byte)(49);
            String var7 = $$v_s(var10002, 6431572394383757402L);
            byte[] var10003 = new byte[12];
            var10003[0] = (byte)(55);
            var10003[1] = (byte)(-49);
            var10003[2] = (byte)(88);
            var10003[3] = (byte)(-2);
            var10003[4] = (byte)(-18);
            var10003[5] = (byte)(50);
            var10003[6] = (byte)(54);
            var10003[7] = (byte)(5);
            var10003[8] = (byte)(126);
            var10003[9] = (byte)(-52);
            var10003[10] = (byte)(-125);
            var10003[11] = (byte)(-123);
            this.popupTitle = Localization.get(var6, Localization.get(var7, $$v_s(var10003, 6431572394383757402L)));
         }
      }

      this.open();
   }

   private void open() {
      this.pageOpen = true;
      this.openedAt = System.currentTimeMillis();
      this.scroll = this.targetScroll = this.maxScroll = 0.0F;
      this.colorPicker = null;
      this.closeDropdown();
      this.lastHoveredRowKey = null;
      Sounds.instance.playModuleExpand();
   }

   public void closeOverlay() {
      if (this.pageOpen) {
         Sounds.instance.playModuleCollapse();
      }

      this.stopListening();
      this.draggingNum = null;
      this.draggingDdScroll = false;
      if (this.colorPicker != null) {
         this.colorPicker.hide();
         this.colorPicker = null;
      }

      this.closeDropdown();
      this.scroll = this.targetScroll = this.maxScroll = 0.0F;
      this.popupModule = null;
      this.popupTitle = null;
      this.pageOpen = false;
      this.pageAnim = 0.0F;
      this.openDropdownAnim = 0.0F;
      this.lastHoveredRowKey = null;
   }

   public void closeImmediate() {
      this.closeOverlay();
      this.pageAnim = 0.0F;
      this.openDropdownAnim = 0.0F;
   }

   private void stopListening() {
      if (this.listeningBind != null) {
         this.listeningBind.setListening(false);
         this.listeningBind = null;
      }
   }

   public int dropdownSection() {
      return this.selected;
   }

   private Module headerModule() {
      if (this.popupModule != null) {
         return this.popupModule;
      } else if (this.selected == 1) {
         return this.hud();
      } else {
         return this.selected == (3) ? this.music() : null;
      }
   }

   String title() {
      if (this.popupTitle != null) {
         return this.popupTitle;
      }

      return switch (this.selected) {
         case 0 -> Localization.get(
            "sec.animations",
            "Animations"
         );
         case 1 -> Localization.get(
            "sec.hud",
            Localization.get(
               "sec.fps",
               "HUD Settings"
            )
         );
         case 2 -> Localization.get(
            "sec.gui",
            "GUI & Style"
         );
         case 3 -> Localization.get(
            "sec.music",
            "Music HUD"
         );
         default -> SECTIONS[Math.max(0, Math.min(SECTIONS.length - 1, this.selected))];
      };
   }

   private List items() {
      ArrayList var1 = new ArrayList();
      Module var2 = this.headerModule();
      if (var2 != null) {
         try {
            for (Setting var4 : var2.getSettings()) {
               if (isSupported(var4)) {
                  var1.add(var4);
               }
            }
         } catch (Throwable var9) {
         }

         if (!var1.isEmpty()) {
            return var1;
         }
      }

      if (this.selected == 0) {
         var1.add(VieClient.animationMode);
         var1.add(VieClient.animationSpeed);
      } else if (this.selected == 1) {
         Module var10 = this.hud();
         if (var10 != null) {
            try {
               for (Setting var5 : var10.getSettings()) {
                  if (isSupported(var5)) {
                     var1.add(var5);
                  }
               }
            } catch (Throwable var8) {
            }
         }
      } else if (this.selected == (2)) {
         if (VieClient.instance != null) {
            try {
               for (Setting var14 : VieClient.instance.getSettings()) {
                  if (isSupported(var14)) {
                     var1.add(var14);
                  }
               }
            } catch (Throwable var7) {
            }
         }

         var1.addAll(this.discordSettings());
      } else if (this.selected == (3)) {
         Module var12 = this.music();
         if (var12 != null) {
            try {
               for (Setting var16 : var12.getSettings()) {
                  if (isSupported(var16)) {
                     var1.add(var16);
                  }
               }
            } catch (Throwable var6) {
            }
         }
      }

      return var1;
   }

   private static boolean isSupported(Setting s) {
      return s != null && s.isVisible()
         ? s instanceof BooleanSetting
            || s instanceof NumberSetting
            || s instanceof ModeSetting
            || s instanceof ColorSetting
            || s instanceof StringSetting
            || s instanceof BindSetting
            || s instanceof ButtonSetting
            || s instanceof FriendsSetting
            || s instanceof MacroSetting
            || s instanceof BlocksSetting
            || s instanceof ItemSetting
         : false;
   }

   private List discordSettings() {
      if (this.discordRows == null) {
         ArrayList var1 = new ArrayList();
         ClientSettings var2 = ClientSettings.get();
         if (var2 == null) {
            return var1;
         }

         Setting var3 = Module.mapSetting(var2.getDiscordRpcSetting());
         if (var3 != null) {
            var1.add(var3);
         }

         this.discordRows = var1;
      }

      return this.discordRows;
   }

   private Module hud() {
      if (this.hudMod == null && me.drake.vieclient.gui.nvg.adapter.VieClient.INSTANCE != null) {
         this.hudMod = me.drake.vieclient.gui.nvg.adapter.VieClient.INSTANCE.getModuleManager().getModuleByClass(HUD.class);
      }

      return this.hudMod != null ? this.hudMod : HUD.instance;
   }

   private Module music() {
      if (this.musicMod == null && me.drake.vieclient.gui.nvg.adapter.VieClient.INSTANCE != null) {
         this.musicMod = me.drake.vieclient.gui.nvg.adapter.VieClient.INSTANCE.getModuleManager().getModuleByClass(MusicHUD.class);
      }

      return this.musicMod != null ? this.musicMod : MusicHUD.instance;
   }

   private static float rowHeight(Object o) {
      return o instanceof NumberSetting ? 34.0F : 24.0F;
   }

   public void update(float dt) {
      if (this.colorPicker != null) {
         this.colorPicker.update(dt);
      }

      this.pageAnim = AnimationManager.isOff() ? (this.pageOpen ? 1.0F : 0.0F) : AnimationManager.damp(this.pageAnim, this.pageOpen ? 1.0F : 0.0F, 22.0F, dt);
      if (AnimationManager.isOff()) {
         this.openDropdownAnim = this.openDropdownTarget;
         this.ddScroll = this.ddTargetScroll;
      } else {
         this.openDropdownAnim = AnimationManager.damp(this.openDropdownAnim, this.openDropdownTarget, 22.0F, dt);
         this.ddScroll = AnimationManager.damp(this.ddScroll, this.ddTargetScroll, 22.0F, dt);
      }

      if (this.openDropdown != null && this.openDropdownAnim < 0.005F && this.openDropdownTarget == 0.0F) {
         this.openDropdown = null;
         this.ddScroll = this.ddTargetScroll = this.ddMaxScroll = 0.0F;
         this.draggingDdScroll = false;
      }

      if (this.openDropdown == null && this.openDropdownAnim < 0.005F) {
      }

      if (Float.isNaN(this.maxScroll) || Float.isInfinite(this.maxScroll)) {
         this.maxScroll = 0.0F;
      }

      this.targetScroll = Math.max(0.0F, Math.min(this.maxScroll, this.targetScroll));
      this.scroll = AnimationManager.damp(this.scroll, this.targetScroll, 20.0F, dt);
      if (Float.isNaN(this.scroll) || Float.isInfinite(this.scroll)) {
         this.scroll = 0.0F;
      }

      this.scroll = Math.max(0.0F, Math.min(this.maxScroll, this.scroll));
      Module var2 = this.headerModule();
      if (var2 != null) {
         this.togAnim.merge(var2, var2.isEnabled() ? 1.0F : 0.0F, (a, t) -> AnimationManager.damp(a, t, 22.0F, dt));
      }

      for (Object var4 : this.items()) {
         if (var4 instanceof BooleanSetting var5) {
            this.togAnim.merge(var5, var5.getValue() ? 1.0F : 0.0F, (a, t) -> AnimationManager.damp(a, t, 22.0F, dt));
         }
      }
   }

   private void layout(int sw, int sh) {
      PW = NanoVGFontManager.isUseMinecraftFont() ? 370.0F : 320.0F;
      float var3 = 0.0F;

      for (Object var5 : this.items()) {
         var3 += rowHeight(var5);
      }

      float var6 = 40.0F + var3 + 6.0F + 20.0F;
      this.ph = Math.min(300.0F, Math.max(104.0F, var6));
      this.maxScroll = Math.max(0.0F, var6 - this.ph);
      this.px = (sw - PW) / 2.0F;
      this.py = (sh - this.ph) / 2.0F;
      if (Float.isNaN(this.targetScroll) || Float.isInfinite(this.targetScroll)) {
         this.targetScroll = 0.0F;
      }

      if (Float.isNaN(this.scroll) || Float.isInfinite(this.scroll)) {
         this.scroll = 0.0F;
      }

      this.targetScroll = Math.max(0.0F, Math.min(this.maxScroll, this.targetScroll));
      this.scroll = Math.max(0.0F, Math.min(this.maxScroll, this.scroll));
   }

   public void blitKawaseBackdrop(float guiScale, float cx, float cy, int sw, int sh) {
      if (!(this.pageAnim < 0.02F)) {
         this.layout(sw, sh);
         FrostedBackground.blitGuiRect(this.px, this.py, PW, this.ph, 10.0F, guiScale, cx, cy, true);
      }
   }

   public void render(int mx, int my, float alpha, int sw, int sh) {
      alpha *= this.pageAnim;
      if (!(alpha <= 0.01F)) {
         SoftPalette.syncTheme();
         this.screenW = sw;
         this.screenH = sh;
         this.layout(sw, sh);
         float var6 = 0.97F + 0.03F * this.pageAnim;
         float var7 = this.px + PW / 2.0F;
         float var8 = this.py + this.ph / 2.0F;
         NanoVGRenderer.save();
         NanoVGRenderer.translate(var7, var8);
         NanoVGRenderer.scale(var6, var6);
         NanoVGRenderer.translate(-var7, -var8);
         NanoVGRenderer.drawRect(-sw, -sh, sw * 3.0F, sh * 3.0F, (int)(90.0F * alpha) << 24);
         SoftPalette.softShadow(this.px, this.py, PW, this.ph, 10.0F, 26.0F, 10.0F, 110.0F * alpha);
         NanoVGRenderer.drawSquircleGradient(
            this.px,
            this.py,
            PW,
            this.ph,
            10.0F,
            SoftPalette.rgba(SoftPalette.BODY_TOP, 255.0F * alpha),
            SoftPalette.rgba(SoftPalette.BODY_BOT, 255.0F * alpha)
         );
         NanoVGRenderer.drawSquircleOutline(this.px, this.py, PW, this.ph, 10.0F, 1.0F, SoftPalette.white(26.0F * alpha));
         this.renderHeader(mx, my, alpha);
         float var9 = this.py + 34.0F;
         float var10 = this.ph - 34.0F - 20.0F;
         NanoVGRenderer.drawRect(this.px + 10.0F, var9, PW - 20.0F, 1.0F, SoftPalette.white(12.0F * alpha));
         this.rows.clear();
         this.lastMouseXForSound = mx;
         this.lastMouseYForSound = my;
         String var11 = null;
         NanoVGRenderer.scissor(this.px, var9 + 1.0F, PW, var10 - 1.0F);
         if (Float.isNaN(this.scroll) || Float.isInfinite(this.scroll)) {
            this.scroll = 0.0F;
         }

         this.scroll = Math.max(0.0F, Math.min(this.maxScroll, this.scroll));
         float var12 = var9 + 6.0F - this.scroll;
         float var13 = this.px + 8.0F;
         float var14 = PW - 16.0F;
         boolean var15 = my >= var9 && my <= var9 + var10;
         boolean var16 = this.openDropdown != null
            && my >= this.ddListRect[1]
            && my <= this.ddListRect[1] + this.ddListRect[3]
            && mx >= this.ddListRect[0]
            && mx <= this.ddListRect[0] + this.ddListRect[2];
         if (var16) {
            var15 = false;
         }

         int var17 = 0;
         List var18 = this.items();

         for (Object var20 : var18) {
            float var21 = rowHeight(var20);
            if (var12 + var21 >= var9 && var12 <= var9 + var10) {
               boolean var22 = var15 && mx >= var13 && mx <= var13 + var14 && my >= var12 && my <= var12 + var21;
               float var23 = this.rowEntrance(var17);
               if (var23 < 0.999F) {
                  NanoVGRenderer.save();
                  NanoVGRenderer.translate(0.0F, (1.0F - var23) * 4.0F);
               }

               if (var22) {
                  NanoVGRenderer.drawRoundedRect(var13, var12 + 1.0F, var14, var21 - 2.0F, 6.0F, SoftPalette.white(9.0F * alpha * var23));
               }

               this.drawRow(var20, var13 + 8.0F, var12, var14 - 16.0F, var21, mx, my, alpha * var23, var22);
               if (var23 < 0.999F) {
                  NanoVGRenderer.restore();
               }

               VieOSPage.Row var24 = new VieOSPage.Row();
               var24.item = var20;
               var24.x = var13;
               var24.y = var12;
               var24.w = var14;
               var24.h = var21;
               this.rows.add(var24);
               if (var22 && var20 instanceof Setting var25 && var25.getDescription() != null) {
                  var11 = var25.getDescription().toString();
               }
            }

            var12 += var21;
            var17++;
         }

         NanoVGRenderer.resetScissor();
         if (this.rows.isEmpty()) {
            if (var18.isEmpty()) {
               SoftPalette.label(
                  "No configurable settings",
                  this.px + 14.0F,
                  var9 + 6.0F + 6.0F,
                  9.0F,
                  SoftPalette.textDim(180.0F * alpha)
               );
            } else {
               this.scroll = this.targetScroll = 0.0F;
            }
         }

         this.tickRowHoverSound();
         if (this.maxScroll > 0.0F) {
            float var27 = Math.max(16.0F, var10 * (var10 / (var10 + this.maxScroll)));
            float var29 = var9 + this.scroll / this.maxScroll * (var10 - var27);
            NanoVGRenderer.drawRoundedRect(this.px + PW - 5.0F, var29, 2.5F, var27, 1.25F, SoftPalette.white(45.0F * alpha));
         }

         float var28 = this.py + this.ph - 20.0F + 5.0F;
         if (var11 != null && !var11.isEmpty()) {
            SoftPalette.label(truncate(var11, PW - 24.0F, 7.5F), this.px + 12.0F, var28, 7.5F, SoftPalette.mint(220.0F * alpha));
         }

         if (this.colorPicker != null && this.colorPicker.isAnimating()) {
            this.colorPicker.render(mx, my, alpha);
         }

         if (this.openDropdownAnim > 0.01F) {
            this.drawDropdownList(alpha, mx, my);
         }

         NanoVGRenderer.restore();
      }
   }

   private float rowEntrance(int index) {
      if (AnimationManager.isOff()) {
         return 1.0F;
      } else {
         long var2 = System.currentTimeMillis();
         if (this.openedAt > 0L && var2 >= this.openedAt && var2 - this.openedAt <= 1500L) {
            float var4 = (float)(var2 - this.openedAt) / 1000.0F - Math.min(index, 12) * 0.018F;
            float var5 = Math.max(0.0F, Math.min(1.0F, var4 / 0.18F));
            return 1.0F - (1.0F - var5) * (1.0F - var5) * (1.0F - var5);
         } else {
            return 1.0F;
         }
      }
   }

   private void renderHeader(int mx, int my, float alpha) {
      float var4 = this.py + 17.0F;
      SoftPalette.labelBold(truncate(this.title(), PW - 90.0F, 11.5F), this.px + 14.0F, var4 - 5.5F, 11.5F, SoftPalette.text(255.0F * alpha));
      float var5 = 18.0F;
      this.closeRect[0] = this.px + PW - var5 - 8.0F;
      this.closeRect[1] = var4 - var5 / 2.0F;
      this.closeRect[2] = var5;
      this.closeRect[3] = var5;
      boolean var6 = hit(this.closeRect, mx, my);
      if (var6) {
         NanoVGRenderer.drawRoundedRect(this.closeRect[0], this.closeRect[1], var5, var5, 4.0F, SoftPalette.white(14.0F * alpha));
      }

      NVGIcons.cross(
         this.closeRect[0] + var5 / 2.0F,
         var4,
         3.2F,
         1.0F,
         var6 ? new Color(245, 120, 120, SoftPalette.clamp(255.0F * alpha)) : SoftPalette.textDim(200.0F * alpha)
      );
      Module var7 = this.headerModule();
      this.headerToggle[2] = 0.0F;
      if (var7 != null) {
         float var8 = 22.0F;
         float var9 = 12.0F;
         this.headerToggle[0] = this.closeRect[0] - var8 - 8.0F;
         this.headerToggle[1] = var4 - var9 / 2.0F;
         this.headerToggle[2] = var8;
         this.headerToggle[3] = var9;
         SoftPalette.toggle(this.headerToggle[0], this.headerToggle[1], var8, var9, this.togAnim.getOrDefault(var7, var7.isEnabled() ? 1.0F : 0.0F), alpha);
      }
   }

   private void drawRow(Object o, float x, float y, float w, float h, int mx, int my, float alpha, boolean hov) {
      String var10 = o instanceof Setting var11 ? var11.getName().toString() : "";
      String var33 = "InteractionSFX".equalsIgnoreCase(var10)
         ? "Interaction SFX"
         : var10;
      float var12 = o instanceof NumberSetting ? y + 4.0F : y + (h - 9.0F) / 2.0F;
      float var13 = o instanceof BooleanSetting
         ? w - 32.0F
         : (o instanceof NumberSetting ? w - 45.0F : (o instanceof ButtonSetting ? w - 60.0F : (o instanceof BindSetting ? w - 65.0F : w - 110.0F)));
      SoftPalette.label(truncate(var33, var13, 9.0F), x, var12, 9.0F, hov ? SoftPalette.text(255.0F * alpha) : SoftPalette.text(225.0F * alpha));
      float var14 = x + w;
      float var15 = y + h / 2.0F;
      if (o instanceof BooleanSetting var16) {
         SoftPalette.toggle(var14 - 20.0F, var15 - 5.5F, 20.0F, 11.0F, this.togAnim.getOrDefault(var16, var16.getValue() ? 1.0F : 0.0F), alpha);
      } else if (o instanceof NumberSetting var17) {
         String var23 = fmt(var17);
         float var24 = SoftPalette.widthBold(var23, 8.5F);
         SoftPalette.labelBold(var23, var14 - var24, y + 4.0F, 8.5F, SoftPalette.mint(240.0F * alpha));
         float var25 = y + 20.0F;
         float var26 = 3.5F;
         float var27 = (float)Math.max(0.0, Math.min(1.0, var17.getSliderFraction()));
         NanoVGRenderer.drawRoundedRect(x, var25, w, var26, var26 / 2.0F, SoftPalette.white(20.0F * alpha));
         if (var27 > 0.001F) {
            NanoVGRenderer.drawRoundedRectGradient(
               x, var25, w * var27, var26, var26 / 2.0F, SoftPalette.mint(230.0F * alpha), SoftPalette.mintDeep(230.0F * alpha)
            );
         }

         float var28 = this.draggingNum == var17 ? 5.0F : 4.0F;
         NanoVGRenderer.drawCircle(x + w * var27, var25 + var26 / 2.0F, var28, new Color(245, 255, 250, SoftPalette.clamp(255.0F * alpha)));
      } else if (o instanceof ModeSetting var18) {
         List var34 = var18.getPossibleValues();
         if (var34.size() >= 3) {
            String var42 = optionName(var18.getValue());
            float var47 = Math.max(105.0F, SoftPalette.widthBold(var42, 8.0F) + 30.0F);
            float var49 = var14 - var47;
            float var52 = var15 - 9.0F;
            boolean var55 = hov && mx >= var49 && mx <= var49 + var47 && my >= var52 && my <= var52 + 18.0F;
            boolean var29 = this.openDropdown == var18;
            NanoVGRenderer.drawRoundedRect(
               var49, var52, var47, 18.0F, 5.0F, !var55 && !var29 ? SoftPalette.white(12.0F * alpha) : SoftPalette.white(18.0F * alpha)
            );
            NanoVGRenderer.drawRoundedRectOutline(var49, var52, var47, 18.0F, 5.0F, 1.0F, SoftPalette.white(16.0F * alpha));
            float var30 = SoftPalette.widthBold(var42, 8.0F);
            SoftPalette.labelBold(var42, var49 + 8.0F, var52 + 5.0F, 8.0F, SoftPalette.text(245.0F * alpha));
            float var31 = var49 + var47 - 9.0F;
            float var32 = var15;
            if (var29) {
               NVGIcons.chevron(var31, var32, 4.0F, false, 1.3F, SoftPalette.mint(220.0F * alpha));
            } else {
               NVGIcons.chevron(var31, var32, 4.0F, true, 1.3F, SoftPalette.textDim(220.0F * alpha));
            }
         } else {
            float[] var43 = segGeo(x, y, w, h, var34);
            int var48 = Math.max(0, var34.indexOf(var18.getValue()));
            if (var43 != null) {
               float var50 = var43[2] / var34.size();
               NanoVGRenderer.drawRoundedRect(var43[0], var43[1], var43[2], var43[3], 5.0F, SoftPalette.white(12.0F * alpha));
               NanoVGRenderer.drawRoundedRect(
                  var43[0] + var48 * var50 + 1.0F, var43[1] + 1.0F, var50 - 2.0F, var43[3] - 2.0F, 4.0F, SoftPalette.white(22.0F * alpha)
               );

               for (int var53 = 0; var53 < var34.size(); var53++) {
                  String var56 = optionName(var34.get(var53));
                  float var58 = SoftPalette.width(var56, 7.5F);
                  SoftPalette.label(
                     var56,
                     var43[0] + var53 * var50 + (var50 - var58) / 2.0F,
                     var43[1] + (var43[3] - 7.5F) / 2.0F,
                     7.5F,
                     var53 == var48 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.textDim(200.0F * alpha)
                  );
               }
            } else {
               String var51 = optionName(var18.getValue());
               float var54 = Math.max(85.0F, SoftPalette.widthBold(var51, 8.0F) + 30.0F);
               float var57 = 18.0F;
               float var59 = var14 - var54;
               float var60 = var15 - var57 / 2.0F;
               NanoVGRenderer.drawRoundedRect(var59, var60, var54, var57, 5.0F, SoftPalette.white(12.0F * alpha));
               NanoVGRenderer.drawRoundedRectOutline(var59, var60, var54, var57, 5.0F, 1.0F, SoftPalette.white(16.0F * alpha));
               chevron(var59 + 8.0F, var15, 3.0F, false, SoftPalette.textDim(220.0F * alpha));
               chevron(var59 + var54 - 8.0F, var15, 3.0F, true, SoftPalette.textDim(220.0F * alpha));
               float var61 = SoftPalette.widthBold(var51, 8.0F);
               SoftPalette.labelBold(var51, var59 + (var54 - var61) / 2.0F, var60 + (var57 - 8.0F) / 2.0F, 8.0F, SoftPalette.text(245.0F * alpha));
            }
         }
      } else if (o instanceof ColorSetting var19) {
         Color var35 = var19.getValue();
         NanoVGRenderer.drawRoundedRect(
            var14 - 28.0F, var15 - 7.0F, 28.0F, 14.0F, 4.0F, new Color(var35.getRed(), var35.getGreen(), var35.getBlue(), SoftPalette.clamp(255.0F * alpha))
         );
         NanoVGRenderer.drawRoundedRectOutline(var14 - 28.0F, var15 - 7.0F, 28.0F, 14.0F, 4.0F, 1.0F, SoftPalette.white(70.0F * alpha));
      } else if (o instanceof BindSetting var20) {
         boolean var36 = this.listeningBind == var20;
         boolean var44 = Localization.isVietnamese();
         chip(
            var36
               ? (
                  var44
                     ? "Bấm phím..."
                     : "Press key..."
               )
               : keyName(var20.getValue()),
            var14,
            var15,
            var36,
            alpha
         );
      } else if (o instanceof StringSetting var21) {
         boolean var37 = Localization.isVietnamese();
         String var45 = var21.getValue() != null && !var21.getValue().isEmpty()
            ? var21.getValue()
            : (
               var37
                  ? "Nhập chữ"
                  : "Set text"
            );
         chip(truncate(var45, 100.0F, 8.0F), var14, var15, false, alpha);
      } else if (o instanceof ButtonSetting var22) {
         boolean var38 = Localization.isVietnamese();
         String var46 = var22.getName() != null ? var22.getName().toString() : "Action";
         if (var46.startsWith("+")) {
            var46 = var38
               ? "+ Thêm"
               : "+ Add";
         } else if (var46.startsWith("-")) {
            var46 = var38
               ? "- Xóa"
               : "- Remove";
         } else if (var46.toLowerCase().contains("reset")) {
            var46 = var38
               ? "Đặt lại"
               : "Reset";
         } else if (var46.toLowerCase().contains("filter")) {
            var46 = var38
               ? "Bộ lọc"
               : "Filter";
         } else if (var46.toLowerCase().contains("editor")) {
            var46 = var38
               ? "Giao diện"
               : "Editor";
         } else if (var46.toLowerCase().contains("start")) {
            var46 = var38
               ? "Bắt đầu"
               : "Start";
         } else if (var46.toLowerCase().contains("stop")) {
            var46 = var38 ? "Dừng" : "Stop";
         } else if (var46.toLowerCase().contains("pause")) {
            var46 = var38
               ? "Tạm dừng"
               : "Pause";
         } else if (var46.toLowerCase().contains("load")) {
            var46 = var38 ? "Tải" : "Load";
         } else if (var46.length() > 12) {
            var46 = var38
               ? "Thực hiện"
               : "Action";
         }

         chip(var46, var14, var15, false, alpha);
      } else if (o instanceof FriendsSetting) {
         boolean var39 = Localization.isVietnamese();
         chip(
            var39 ? "Sửa" : "Edit",
            var14,
            var15,
            false,
            alpha
         );
      } else if (o instanceof MacroSetting) {
         boolean var40 = Localization.isVietnamese();
         chip(
            var40 ? "Sửa" : "Edit",
            var14,
            var15,
            false,
            alpha
         );
      } else if (o instanceof BlocksSetting || o instanceof ItemSetting) {
         boolean var41 = Localization.isVietnamese();
         chip(
            var41 ? "Chọn" : "Select",
            var14,
            var15,
            false,
            alpha
         );
      }
   }

   private static void chevron(float cx, float cy, float s, boolean right, Color c) {
      float var5 = right ? s * 0.5F : -s * 0.5F;
      NanoVGRenderer.drawLine(cx - var5, cy - s, cx + var5, cy, 1.2F, c);
      NanoVGRenderer.drawLine(cx + var5, cy, cx - var5, cy + s, 1.2F, c);
   }

   private static void chip(String label, float right, float cy, boolean active, float alpha) {
      float var5 = Math.max(42.0F, SoftPalette.widthBold(label, 7.5F) + 16.0F);
      float var6 = 17.0F;
      float var7 = right - var5;
      float var8 = cy - var6 / 2.0F;
      NanoVGRenderer.drawRoundedRect(var7, var8, var5, var6, 5.0F, active ? SoftPalette.mint(35.0F * alpha) : SoftPalette.white(12.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var7, var8, var5, var6, 5.0F, 1.0F, active ? SoftPalette.mint(200.0F * alpha) : SoftPalette.white(18.0F * alpha));
      float var9 = SoftPalette.widthBold(label, 7.5F);
      SoftPalette.labelBold(
         label, var7 + (var5 - var9) / 2.0F, var8 + (var6 - 7.5F) / 2.0F, 7.5F, active ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(240.0F * alpha)
      );
   }

   private static float[] segGeo(float x, float y, float w, float h, List opts) {
      if (opts.size() >= (2) && opts.size() <= (3)) {
         float var5 = 0.0F;

         for (Object var7 : opts) {
            var5 = Math.max(var5, SoftPalette.width(optionName(var7), 7.5F) + 12.0F);
         }

         float var8 = var5 * opts.size();
         if (var8 > w * 0.58F) {
            return null;
         }

         float var9 = 18.0F;
         float[] var10000 = new float[4];
         var10000[0] = x + w - var8;
         var10000[1] = y + (h - var9) / 2.0F;
         var10000[2] = var8;
         var10000[3] = var9;
         return var10000;
      } else {
         return null;
      }
   }

   private static String optionName(Object v) {
      String var1 = v instanceof Enum var2 ? var2.name() : String.valueOf(v);
      var1 = var1.replace((char)(95), (char)(32)).toLowerCase();
      return var1.isEmpty() ? var1 : Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
   }

   private void openDropdownAt(ModeSetting m, float boxX, float boxY, float boxW) {
      this.openDropdown = m;
      this.ddBoxRect[0] = boxX;
      this.ddBoxRect[1] = boxY;
      this.ddBoxRect[2] = boxW;
      this.ddBoxRect[3] = 18.0F;
      int var5 = m.getPossibleValues().size();
      float var6 = var5 * 18.0F + 4.0F;
      float var7 = boxW;

      for (Object var9 : m.getPossibleValues()) {
         float var10 = SoftPalette.widthBold(optionName(var9), 8.0F) + 36.0F;
         if (var10 > var7) {
            var7 = var10;
         }
      }

      float var16 = var7;
      float var17 = boxX + boxW - var16;
      if (var17 < 4.0F) {
         var17 = 4.0F;
      }

      if (var17 + var16 > this.screenW - 4.0F) {
         var16 = this.screenW - var17 - 4.0F;
      }

      this.ddDir = 1;
      float var18 = boxY + 18.0F + 3.0F;
      float var11 = Math.max(58.0F, this.screenH - var18 - 8.0F);
      float var12 = Math.min(121.0F, var11);
      float var13 = Math.min(var6, var12);
      this.ddListRect[0] = var17;
      this.ddListRect[1] = var18;
      this.ddListRect[2] = var16;
      this.ddListRect[3] = var13;
      this.ddMaxScroll = Math.max(0.0F, var6 - var13);
      this.ddScroll = this.ddTargetScroll = 0.0F;
      this.draggingDdScroll = false;
      int var14 = Math.max(0, m.getPossibleValues().indexOf(m.getValue()));
      float var15 = 2.0F + var14 * 18.0F;
      if (var15 + 18.0F > var13 && this.ddMaxScroll > 0.0F) {
         this.ddTargetScroll = Math.min(this.ddMaxScroll, var15 - var13 / 2.0F + 9.0F);
         this.ddScroll = this.ddTargetScroll;
      }

      this.openDropdownTarget = 1.0F;
   }

   private void closeDropdown() {
      this.openDropdownTarget = 0.0F;
      this.draggingDdScroll = false;
   }

   private int hitDropdownList(float mx, float my) {
      if (this.openDropdown == null) {
         return -1;
      }

      if (mx < this.ddListRect[0] || mx > this.ddListRect[0] + this.ddListRect[2]) {
         return -1;
      }

      if (my < this.ddListRect[1] || my > this.ddListRect[1] + this.ddListRect[3]) {
         return -1;
      }

      if (this.ddMaxScroll > 0.0F && mx > this.ddListRect[0] + this.ddListRect[2] - 8.0F) {
         return -1;
      }

      float var3 = my - this.ddListRect[1] - 2.0F + this.ddScroll;
      int var4 = (int)(var3 / 18.0F);
      int var5 = this.openDropdown.getPossibleValues().size();
      return var4 >= 0 && var4 < var5 ? var4 : -1;
   }

   private void drawDropdownList(float alpha, int mx, int my) {
      if (this.openDropdown != null) {
         ModeSetting var4 = this.openDropdown;
         List var5 = var4.getPossibleValues();
         int var6 = Math.max(0, var5.indexOf(var4.getValue()));
         float var7 = this.ddListRect[1];
         float var8 = this.ddListRect[0];
         float var9 = this.ddListRect[2];
         float var10 = this.ddListRect[3];
         float var11 = var7 - 4.0F;
         float var12 = var7 + (var11 - var7) * (1.0F - this.openDropdownAnim);
         float var13 = this.openDropdownAnim * alpha;
         NanoVGRenderer.drawRoundedRect(var8, var12, var9, var10, 6.0F, SoftPalette.rgba(SoftPalette.BODY_BOT, 255.0F * var13));
         NanoVGRenderer.drawRoundedRectOutline(var8, var12, var9, var10, 6.0F, 1.0F, SoftPalette.white(45.0F * var13));
         int var14 = -1;
         float var15 = this.ddMaxScroll > 0.0F ? 8.0F : 0.0F;
         if (mx >= var8 && mx <= var8 + var9 - var15 && my >= var12 + 2.0F && my <= var12 + var10 - 2.0F) {
            float var16 = my - var12 - 2.0F + this.ddScroll;
            int var17 = (int)(var16 / 18.0F);
            if (var17 >= 0 && var17 < var5.size()) {
               var14 = var17;
            }
         }

         NanoVGRenderer.save();
         NanoVGRenderer.scissor(var8, var12 + 1.0F, var9, var10 - 2.0F);

         for (int var23 = 0; var23 < var5.size(); var23++) {
            float var25 = var12 + 2.0F + var23 * 18.0F - this.ddScroll;
            if (!(var25 + 18.0F < var12) && !(var25 > var12 + var10)) {
               boolean var18 = var23 == var14;
               boolean var19 = var23 == var6;
               float var20 = this.ddMaxScroll > 0.0F ? var9 - 10.0F : var9 - 6.0F;
               if (var18 && var19) {
                  NanoVGRenderer.drawRoundedRect(var8 + 3.0F, var25 + 1.0F, var20, 16.0F, 4.0F, SoftPalette.white(22.0F * var13));
                  NanoVGRenderer.drawRoundedRect(var8 + 3.0F, var25 + 1.0F, var20, 16.0F, 4.0F, SoftPalette.mint(40.0F * var13));
               } else if (var19) {
                  NanoVGRenderer.drawRoundedRect(var8 + 3.0F, var25 + 1.0F, var20, 16.0F, 4.0F, SoftPalette.mint(28.0F * var13));
               } else if (var18) {
                  NanoVGRenderer.drawRoundedRect(var8 + 3.0F, var25 + 1.0F, var20, 16.0F, 4.0F, SoftPalette.white(16.0F * var13));
               }

               if (var19) {
                  NVGIcons.check(var8 + 11.0F, var25 + 9.0F, 4.5F, 1.4F, SoftPalette.mint(245.0F * var13));
               }

               String var21 = optionName(var5.get(var23));
               Color var22 = var19 ? SoftPalette.mint(250.0F * var13) : (var18 ? SoftPalette.text(245.0F * var13) : SoftPalette.text(220.0F * var13));
               SoftPalette.label(var21, var8 + 22.0F, var25 + 5.25F, 7.5F, var22);
            }
         }

         NanoVGRenderer.resetScissor();
         NanoVGRenderer.restore();
         if (this.ddMaxScroll > 0.0F) {
            float var24 = 3.0F;
            float var26 = var8 + var9 - var24 - 2.5F;
            float var27 = var10 - 6.0F;
            float var28 = var5.size() * 18.0F + 4.0F;
            float var29 = Math.max(12.0F, var27 * (var10 / var28));
            float var30 = var12 + 3.0F + (var27 - var29) * (this.ddScroll / this.ddMaxScroll);
            NanoVGRenderer.drawRoundedRect(var26, var30, var24, var29, 1.5F, SoftPalette.white(55.0F * var13));
         }
      }
   }

   public boolean mouseClicked(double mx, double my, int btn) {
      if (this.listeningBind != null) {
         this.listeningBind.setValue(-100 - btn);
         this.stopListening();
         return true;
      }

      if (this.colorPicker != null && this.colorPicker.isAnimating()) {
         if (this.colorPicker.mouseClicked(mx, my, btn)) {
            return true;
         }

         this.colorPicker.hide();
         this.colorPicker = null;
         return true;
      } else {
         if (this.openDropdown != null) {
            if (mx >= this.ddListRect[0]
               && mx <= this.ddListRect[0] + this.ddListRect[2]
               && my >= this.ddListRect[1]
               && my <= this.ddListRect[1] + this.ddListRect[3]) {
               if (btn == 0) {
                  if (this.ddMaxScroll > 0.0F && mx >= this.ddListRect[0] + this.ddListRect[2] - 10.0F) {
                     this.draggingDdScroll = true;
                     float var12 = Math.max(1.0F, this.ddListRect[3] - 6.0F);
                     float var13 = Math.max(0.0F, Math.min(1.0F, (float)(my - this.ddListRect[1] - 3.0) / var12));
                     this.ddTargetScroll = this.ddScroll = var13 * this.ddMaxScroll;
                     return true;
                  }

                  int var11 = this.hitDropdownList((float)mx, (float)my);
                  if (var11 >= 0) {
                     this.openDropdown.setModeIndex(var11);

                     try {
                        if (this.popupModule == null && this.openDropdown == VieClient.themePreset) {
                           ThemeManager.setTheme(var11);
                           SoftPalette.syncTheme();
                        }
                     } catch (Throwable var9) {
                     }

                     Sounds.instance.playTap();
                     this.closeDropdown();
                     return true;
                  }
               }

               return true;
            }

            if (hit(this.ddBoxRect, mx, my) && btn == 0) {
               this.closeDropdown();
               return true;
            }

            boolean var6 = mx >= this.px && mx <= this.px + PW && my >= this.py && my <= this.py + this.ph;
            this.closeDropdown();
            if (!var6) {
               return true;
            }
         }

         if (mx >= this.px && mx <= this.px + PW && my >= this.py && my <= this.py + this.ph) {
            if (!hit(this.backRect, mx, my) && !hit(this.closeRect, mx, my)) {
               Module var10 = this.headerModule();
               if (var10 != null && this.headerToggle[2] > 0.0F && my <= this.py + 34.0F && mx >= this.headerToggle[0] - 8.0F) {
                  var10.toggle();
                  clickIfIdle(var10.isEnabled());
                  return true;
               }

               for (VieOSPage.Row var8 : this.rows) {
                  if (!(mx < var8.x) && !(mx > var8.x + var8.w) && !(my < var8.y) && !(my > var8.y + var8.h)) {
                     if (!(my < this.py + 34.0F) && !(my > this.py + this.ph - 20.0F)) {
                        this.clickRow(var8, mx, my, btn);
                        return true;
                     }

                     return true;
                  }
               }

               return true;
            } else {
               this.closeOverlay();
               return true;
            }
         } else {
            return false;
         }
      }
   }

   private void clickRow(VieOSPage.Row r, double mx, double my, int btn) {
      Object var7 = r.item;
      class_310 var8 = class_310.method_1551();
      if (var7 instanceof BooleanSetting var9) {
         if (btn == 1) {
            var9.setValue(var9.getDefaultValue());
         } else {
            var9.toggle();
         }

         if (var9.getValue()) {
            Sounds.instance.playBooleanActivate();
         } else {
            Sounds.instance.playBooleanDeactivate();
         }
      } else if (var7 instanceof NumberSetting var10) {
         if (btn == 1) {
            var10.setValue(var10.getDefaultValue());
            Sounds.instance.playSlider();
         } else if (my >= r.y + 16.0F) {
            this.draggingNum = var10;
            this.setNumFromMouse(mx);
         }
      } else if (var7 instanceof ModeSetting var11) {
         int var27 = var11.getPossibleValues().size();
         int var28 = Math.max(0, var11.getPossibleValues().indexOf(var11.getValue()));
         if (var27 >= 3) {
            String var22 = optionName(var11.getValue());
            float var23 = Math.max(105.0F, SoftPalette.widthBold(var22, 8.0F) + 30.0F);
            float var24 = r.x + r.w - var23;
            float var25 = r.y + r.h / 2.0F - 9.0F;
            if (this.openDropdown == var11) {
               this.closeDropdown();
            } else {
               this.openDropdownAt(var11, var24, var25, var23);
            }

            Sounds.instance.playTap();
         } else {
            float[] var29 = segGeo(r.x + 10.0F, r.y, r.w - 20.0F, r.h, var11.getPossibleValues());
            if (var29 != null) {
               if (mx >= var29[0]) {
                  var11.setModeIndex(Math.max(0, Math.min(var27 - 1, (int)((mx - var29[0]) / (var29[2] / var27)))));
               } else {
                  var11.setModeIndex((var28 + (btn == 1 ? var27 - 1 : 1)) % var27);
               }
            } else {
               float var30 = Math.max(105.0F, SoftPalette.widthBold(optionName(var11.getValue()), 9.0F) + 40.0F);
               boolean var31 = btn == 1 || mx < r.x + r.w - 10.0F - var30 / 2.0F;
               var11.setModeIndex((var28 + (var31 ? var27 - 1 : 1)) % var27);
            }

            try {
               if (this.popupModule == null && var11 == VieClient.themePreset) {
                  ThemeManager.setTheme(var11.getModeIndex());
                  SoftPalette.syncTheme();
               }
            } catch (Throwable var26) {
            }

            Sounds.instance.playTap();
         }
      } else if (var7 instanceof ColorSetting var12 && btn == 0) {
         float var20 = Math.max(6.0F, this.px - 190.0F);
         float var21 = Math.max(6.0F, this.py);
         this.colorPicker = new ColorPicker(var12, var20, var21);
         this.colorPicker.show(var20, var21, this.screenW, this.screenH);
         Sounds.instance.playTap();
      } else if (var7 instanceof BindSetting var13) {
         if (btn == 1) {
            var13.setValue(-1);
            Sounds.instance.playBindRemove();
            return;
         }

         var13.setListening(true);
         this.listeningBind = var13;
         Sounds.instance.playTap();
      } else {
         if (btn != 0 || var8 == null) {
            return;
         }

         if (var7 instanceof StringSetting var14) {
            var8.method_1507(new StringInputScreen(var14));
            Sounds.instance.playTap();
         } else if (var7 instanceof ButtonSetting var15) {
            var15.press();
            Sounds.instance.playTap();
         } else if (var7 instanceof FriendsSetting var16) {
            var8.method_1507(new FriendsFilterScreen(var16));
            Sounds.instance.playTap();
         } else if (var7 instanceof MacroSetting var17) {
            var8.method_1507(new MacroFilterScreen(var17));
            Sounds.instance.playTap();
         } else if (var7 instanceof BlocksSetting var18) {
            var8.method_1507(ItemBlockPickerScreen.forBlocks(var18));
            Sounds.instance.playTap();
         } else if (var7 instanceof ItemSetting var19) {
            var8.method_1507(ItemBlockPickerScreen.forItem(var19));
            Sounds.instance.playTap();
         }
      }
   }

   public boolean keyPressed(int keyCode) {
      if (this.openDropdown != null) {
         if (keyCode == (256)) {
            this.closeDropdown();
            return true;
         } else {
            return true;
         }
      } else {
         if (this.listeningBind == null) {
            return false;
         }

         int var2 = keyCode == (256) ? -1 : keyCode;
         this.listeningBind.setValue(var2);
         if (var2 == (-1)) {
            Sounds.instance.playBindRemove();
         } else {
            Sounds.instance.playBindSuccess();
         }

         this.stopListening();
         return true;
      }
   }

   public boolean isListeningForKey() {
      return this.listeningBind != null;
   }

   public boolean mouseDragged(double mx, double my, int btn) {
      if (this.colorPicker != null && this.colorPicker.mouseDragged(mx, my, btn)) {
         return true;
      } else if (this.draggingNum != null) {
         this.setNumFromMouse(mx);
         return true;
      } else if (this.draggingDdScroll && this.openDropdown != null) {
         float var6 = Math.max(1.0F, this.ddListRect[3] - 6.0F);
         float var7 = Math.max(0.0F, Math.min(1.0F, (float)(my - this.ddListRect[1] - 3.0) / var6));
         this.ddTargetScroll = this.ddScroll = var7 * this.ddMaxScroll;
         return true;
      } else {
         return false;
      }
   }

   public void mouseReleased(double mx, double my, int btn) {
      if (this.colorPicker != null) {
         this.colorPicker.mouseReleased(mx, my, btn);
      }

      if (btn == 0) {
         this.draggingNum = null;
         this.draggingDdScroll = false;
      }
   }

   public boolean mouseScrolled(double mx, double my, double amount) {
      if (this.openDropdown != null) {
         if (mx >= this.ddListRect[0]
            && mx <= this.ddListRect[0] + this.ddListRect[2]
            && my >= this.ddListRect[1]
            && my <= this.ddListRect[1] + this.ddListRect[3]) {
            if (this.ddMaxScroll > 0.0F) {
               this.ddTargetScroll = Math.max(0.0F, Math.min(this.ddMaxScroll, this.ddTargetScroll - (float)amount * 18.0F * 2.5F));
            }

            return true;
         } else {
            this.closeDropdown();
            return true;
         }
      } else if (!(mx < this.px) && !(mx > this.px + PW) && !(my < this.py) && !(my > this.py + this.ph)) {
         for (VieOSPage.Row var8 : this.rows) {
            if (var8.item instanceof NumberSetting var9 && mx >= var8.x && mx <= var8.x + var8.w && my >= var8.y && my <= var8.y + var8.h) {
               double var14 = var9.getFormat() > 0.0 ? var9.getFormat() : (var9.getMax() - var9.getMin()) / 50.0;
               double var12 = Math.max(var9.getMin(), Math.min(var9.getMax(), var9.getValue() + (amount > 0.0 ? var14 : -var14)));
               if (var9.getFormat() > 0.0) {
                  var12 = Math.round(var12 / var9.getFormat()) * var9.getFormat();
               }

               if (var12 != var9.getValue()) {
                  var9.setValue(var12);
                  Sounds.instance.playSlider();
               }

               return true;
            }
         }

         this.targetScroll -= (float)amount * 28.0F;
         return true;
      } else {
         return false;
      }
   }

   private void setNumFromMouse(double mx) {
      for (VieOSPage.Row var4 : this.rows) {
         if (var4.item == this.draggingNum) {
            float var5 = var4.x + 10.0F;
            float var6 = var4.w - 20.0F;
            double var7 = Math.max(0.0, Math.min(1.0, (mx - var5) / var6));
            double var9 = this.draggingNum.getMin() + (this.draggingNum.getMax() - this.draggingNum.getMin()) * var7;
            double var11 = this.draggingNum.getFormat();
            if (var11 > 0.0) {
               var9 = Math.round(var9 / var11) * var11;
            }

            if (var9 != this.draggingNum.getValue()) {
               this.draggingNum.setValue(var9);
               Sounds.instance.playSlider();
            }

            return;
         }
      }
   }

   private static Object rowItemKey(Object item) {
      if (item == null) {
         return null;
      } else if (item instanceof Setting var1) {
         CharSequence var2 = var1.getName();
         return var1.getClass().getName() + ":" + (var2 != null ? var2.toString() : "");
      } else {
         return item;
      }
   }

   private void tickRowHoverSound() {
      Object var1 = null;

      for (VieOSPage.Row var3 : this.rows) {
         if (var3.x <= this.lastMouseXForSound
            && this.lastMouseXForSound <= var3.x + var3.w
            && var3.y <= this.lastMouseYForSound
            && this.lastMouseYForSound <= var3.y + var3.h) {
            var1 = var3.item;
            break;
         }
      }

      Object var5 = rowItemKey(var1);
      if (var5 != null && !Objects.equals(var5, this.lastHoveredRowKey)) {
         long var6 = System.currentTimeMillis();
         if (var6 - this.lastRowHoverSoundAt >= 70L) {
            Sounds.instance.playHovering();
            this.lastRowHoverSoundAt = var6;
         }
      }

      this.lastHoveredRowKey = var5;
   }

   private static void clickIfIdle(boolean on) {
      class_310 var1 = class_310.method_1551();
      if (var1 == null || var1.field_1687 == null) {
         Sfx.click(on);
      }
   }

   private static boolean hit(float[] r, double mx, double my) {
      return r[2] > 0.0F && mx >= r[0] && mx <= r[0] + r[2] && my >= r[1] && my <= r[1] + r[3];
   }

   private static String fmt(NumberSetting n) {
      double var1 = n.getValue();
      String var10000;
      if (var1 == (long)var1) {
         var10000 = String.valueOf((long)var1);
      } else {
         byte[] var3 = new byte[4];
         var3[0] = (byte)(90);
         var3[1] = (byte)(-76);
         var3[2] = (byte)(46);
         var3[3] = (byte)(-72);
         var10000 = String.format($$v_s(var3, 6431572394383757402L), var1);
      }

      return var10000;
   }

   private static String truncate(String s, float maxW, float size) {
      if (s == null) {
         return "";
      }

      if (SoftPalette.width(s, size) <= maxW) {
         return s;
      }

      int var3 = s.length();

      while (var3 > 1 && SoftPalette.width(s.substring(0, var3) + "..", size) > maxW) {
         var3--;
      }

      return s.substring(0, var3) + "..";
   }

   private static String keyName(int k) {
      if (k == (-1) || k == 0) {
         byte[] var6 = new byte[4];
         var6[0] = (byte)(49);
         var6[1] = (byte)(-11);
         var6[2] = (byte)(114);
         var6[3] = (byte)(-69);
         return $$v_s(var6, 6431572394383757402L);
      }

      if (k <= (-100)) {
         int var2 = -k - (100);

         return switch (var2) {
            case 0 -> {
               byte[] var5 = new byte[3];
               var5[0] = (byte)(51);
               var5[1] = (byte)(-41);
               var5[2] = (byte)(94);
               yield $$v_s(var5, 6431572394383757402L);
            }
            case 1 -> {
               byte[] var4 = new byte[3];
               var4[0] = (byte)(45);
               var4[1] = (byte)(-41);
               var4[2] = (byte)(94);
               yield $$v_s(var4, 6431572394383757402L);
            }
            case 2 -> {
               byte[] var3 = new byte[3];
               var3[0] = (byte)(50);
               var3[1] = (byte)(-41);
               var3[2] = (byte)(94);
               yield $$v_s(var3, 6431572394383757402L);
            }
            default -> "MB" + (var2 + 1);
         };
      } else {
         String var1 = DashboardView.keyLabel(k);
         return var1 != null ? var1 : "KEY" + k;
      }
   }

   static {
      String[] var10000 = new String[4];
      byte[] var10003 = new byte[10];
      var10003[0] = (byte)(62);
      var10003[1] = (byte)(-12);
      var10003[2] = (byte)(117);
      var10003[3] = (byte)(-77);
      var10003[4] = (byte)(-36);
      var10003[5] = (byte)(35);
      var10003[6] = (byte)(43);
      var10003[7] = (byte)(30);
      var10003[8] = (byte)(121);
      var10003[9] = (byte)(-47);
      var10000[0] = $$v_s(var10003, 6431572394383757402L);
      var10003 = new byte[12];
      var10003[0] = (byte)(55);
      var10003[1] = (byte)(-49);
      var10003[2] = (byte)(88);
      var10003[3] = (byte)(-2);
      var10003[4] = (byte)(-18);
      var10003[5] = (byte)(50);
      var10003[6] = (byte)(54);
      var10003[7] = (byte)(5);
      var10003[8] = (byte)(126);
      var10003[9] = (byte)(-52);
      var10003[10] = (byte)(-125);
      var10003[11] = (byte)(-123);
      var10000[1] = $$v_s(var10003, 6431572394383757402L);
      int var10002 = 2;
      var10003 = new byte[11];
      var10003[0] = (byte)(56);
      var10003[1] = (byte)(-49);
      var10003[2] = (byte)(85);
      var10003[3] = (byte)(-2);
      var10003[4] = (byte)(-101);
      var10003[5] = (byte)(119);
      var10003[6] = (byte)(17);
      var10003[7] = (byte)(5);
      var10003[8] = (byte)(110);
      var10003[9] = (byte)(-50);
      var10003[10] = (byte)(-127);
      var10000[var10002] = $$v_s(var10003, 6431572394383757402L);
      var10002 = 3;
      var10003 = new byte[9];
      var10003[0] = (byte)(50);
      var10003[1] = (byte)(-17);
      var10003[2] = (byte)(111);
      var10003[3] = (byte)(-73);
      var10003[4] = (byte)(-34);
      var10003[5] = (byte)(119);
      var10003[6] = (byte)(10);
      var10003[7] = (byte)(36);
      var10003[8] = (byte)(83);
      var10000[var10002] = $$v_s(var10003, 6431572394383757402L);
      SECTIONS = var10000;
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

   private static final class Row {
      Object item;
      float x;
      float y;
      float w;
      float h;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
