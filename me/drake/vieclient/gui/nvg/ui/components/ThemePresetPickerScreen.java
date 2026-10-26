package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.ModeSetting;
import me.drake.vieclient.gui.nvg.adapter.setting.Setting;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RainbowUtil;
import net.minecraft.class_2561;
import net.minecraft.class_437;

public class ThemePresetPickerScreen extends VieOSScreen {
   private static final float CARD_W = 110.0F;
   private static final float CARD_H = 78.0F;
   private static final float CARD_GAP = 10.0F;
   private static final float SWATCH_H = 48.0F;
   private static final int COLS = 4;
   private final class_437 parentScreen;
   private final ModeSetting modeSetting;
   private final EnumSetting enumSetting;
   private final String titleText;
   private final List options;
   private int selectedIndex;
   private float scroll;
   private float targetScroll;
   private static final Map PRESET_CACHE = new ConcurrentHashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ThemePresetPickerScreen(ModeSetting setting, class_437 parent, String title) {
      String var10001;
      if (title != null) {
         var10001 = title;
      } else {
         byte[] var11 = new byte[12];
         var11[0] = (byte)(45);
         var11[1] = (byte)(-1);
         var11[2] = (byte)(112);
         var11[3] = (byte)(-69);
         var11[4] = (byte)(-77);
         var11[5] = (byte)(-67);
         var11[6] = (byte)(-104);
         var11[7] = (byte)(69);
         var11[8] = (byte)(126);
         var11[9] = (byte)(-57);
         var11[10] = (byte)(-119);
         var11[11] = (byte)(-109);
         var10001 = $$v_s(var11, 4159944421794893915L);
      }

      super(class_2561.method_43470(var10001));
      this.options = new ArrayList();
      this.selectedIndex = 0;
      this.scroll = 0.0F;
      this.targetScroll = 0.0F;
      this.parentScreen = parent;
      this.modeSetting = setting;
      this.enumSetting = null;
      this.titleText = title != null ? title : "Select " + setting.getName();
      List var4 = setting.getPossibleValues();
      int var5 = setting.getModeIndex();
      this.selectedIndex = Math.max(0, Math.min(var4.size() - 1, var5));

      for (int var6 = 0; var6 < var4.size(); var6++) {
         Object var7 = var4.get(var6);
         String var8 = var7 instanceof Enum ? ((Enum)var7).name() : var7.toString();
         String var9 = var7 instanceof Enum ? setting.getDisplayName((Enum)var7) : var8;
         if (var9 == null || var9.isEmpty()) {
            var9 = formatDisplayName(var8);
         }

         Color[] var10 = resolveColors(var8, setting.getName().toString());
         this.options.add(new ThemePresetPickerScreen.ThemeOption(var6, var8, var9, var10[0], var10[1]));
      }

      this.openScreen();
   }

   public ThemePresetPickerScreen(EnumSetting setting, class_437 parent, String title) {
      String var10001;
      if (title != null) {
         var10001 = title;
      } else {
         byte[] var12 = new byte[12];
         var12[0] = (byte)(45);
         var12[1] = (byte)(-1);
         var12[2] = (byte)(112);
         var12[3] = (byte)(-69);
         var12[4] = (byte)(-77);
         var12[5] = (byte)(-67);
         var12[6] = (byte)(-104);
         var12[7] = (byte)(69);
         var12[8] = (byte)(126);
         var12[9] = (byte)(-57);
         var12[10] = (byte)(-119);
         var12[11] = (byte)(-109);
         var10001 = $$v_s(var12, 4159944421794893915L);
      }

      super(class_2561.method_43470(var10001));
      this.options = new ArrayList();
      this.selectedIndex = 0;
      this.scroll = 0.0F;
      this.targetScroll = 0.0F;
      this.parentScreen = parent;
      this.modeSetting = null;
      this.enumSetting = setting;
      this.titleText = title != null ? title : "Select " + setting.getName();
      Enum[] var4 = setting.getValues();
      int var5 = 0;

      for (int var6 = 0; var6 < var4.length; var6++) {
         if (var4[var6] == setting.get()) {
            var5 = var6;
            break;
         }
      }

      this.selectedIndex = var5;

      for (int var11 = 0; var11 < var4.length; var11++) {
         Enum var7 = var4[var11];
         String var8 = var7.name();
         String var9 = getEnumDisplayName(setting, var7);
         if (var9 == null || var9.isEmpty()) {
            var9 = formatDisplayName(var8);
         }

         Color[] var10 = resolveColors(var8, setting.getName());
         this.options.add(new ThemePresetPickerScreen.ThemeOption(var11, var8, var9, var10[0], var10[1]));
      }

      this.openScreen();
   }

   private static String getEnumDisplayName(EnumSetting s, Enum e) {
      return s.getDisplayName(e);
   }

   public static ThemePresetPickerScreen forSetting(ModeSetting setting, class_437 parent) {
      return new ThemePresetPickerScreen(setting, parent, "Select " + setting.getName());
   }

   public static ThemePresetPickerScreen forSetting(EnumSetting setting, class_437 parent) {
      return new ThemePresetPickerScreen(setting, parent, "Select " + setting.getName());
   }

   public static boolean isThemeOrPreset(Setting s) {
      if (s == null) {
         return false;
      }

      String var1 = s.getName().toString().toLowerCase().trim();
      String var2 = s.getDescription() != null ? s.getDescription().toString().toLowerCase().trim() : "";
      return var1.contains("theme")
         || var1.contains("preset")
         || var1.contains("palette")
         || var2.contains("theme")
         || var2.contains("preset")
         || var2.contains("palette")
         || var1.equals("color mode")
         || var1.equals("colormode");
   }

   public static boolean isThemeOrPreset(me.drake.vieclient.core.setting.Setting s) {
      if (s == null) {
         return false;
      }

      String var1 = s.getName().toLowerCase().trim();
      String var2 = s.getDescription() != null ? s.getDescription().toLowerCase().trim() : "";
      return var1.contains("theme")
         || var1.contains("preset")
         || var1.contains("palette")
         || var2.contains("theme")
         || var2.contains("preset")
         || var2.contains("palette")
         || var1.equals("color mode")
         || var1.equals("colormode");
   }

   @Override
   protected float contentW(int sw) {
      int var2 = Math.max(1, Math.min(this.options.size(), 4));
      return Math.min(sw - 32.0F, 36.0F + var2 * 110.0F + (var2 - 1) * 10.0F);
   }

   @Override
   protected float contentH(int sw, int sh) {
      int var3 = Math.max(1, Math.min(this.options.size(), 4));
      int var4 = Math.max(1, (this.options.size() + var3 - 1) / var3);
      float var5 = 50.0F + var4 * 88.0F - 10.0F + 14.0F + 34.0F;
      return Math.min(sh - 32.0F, Math.min(var5, 380.0F));
   }

   @Override
   protected String title() {
      return this.titleText;
   }

   @Override
   protected String footerText() {
      byte[] var10000 = new byte[57];
      var10000[0] = (byte)(63);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(110);
      var10000[3] = (byte)(-79);
      var10000[4] = (byte)(-89);
      var10000[5] = (byte)(-70);
      var10000[6] = (byte)(-104);
      var10000[7] = (byte)(101);
      var10000[8] = (byte)(121);
      var10000[9] = (byte)(-126);
      var10000[10] = (byte)(-118);
      var10000[11] = (byte)(-105);
      var10000[12] = (byte)(-2);
      var10000[13] = (byte)(120);
      var10000[14] = (byte)(-9);
      var10000[15] = (byte)(8);
      var10000[16] = (byte)(90);
      var10000[17] = (byte)(47);
      var10000[18] = (byte)(-20);
      var10000[19] = (byte)(-114);
      var10000[20] = (byte)(-94);
      var10000[21] = (byte)(-114);
      var10000[22] = (byte)(-56);
      var10000[23] = (byte)(97);
      var10000[24] = (byte)(-125);
      var10000[25] = (byte)(124);
      var10000[26] = (byte)(-32);
      var10000[27] = (byte)(35);
      var10000[28] = (byte)(42);
      var10000[29] = (byte)(97);
      var10000[30] = (byte)(-76);
      var10000[31] = (byte)(-10);
      var10000[32] = (byte)(-66);
      var10000[33] = (byte)(91);
      var10000[34] = (byte)(12);
      var10000[35] = (byte)(14);
      var10000[36] = (byte)(92);
      var10000[37] = (byte)(16);
      var10000[38] = (byte)(56);
      var10000[39] = (byte)(-47);
      var10000[40] = (byte)(116);
      var10000[41] = (byte)(117);
      var10000[42] = (byte)(100);
      var10000[43] = (byte)(54);
      var10000[44] = (byte)(-83);
      var10000[45] = (byte)(-30);
      var10000[46] = (byte)(51);
      var10000[47] = (byte)(-23);
      var10000[48] = (byte)(58);
      var10000[49] = (byte)(-123);
      var10000[50] = (byte)(12);
      var10000[51] = (byte)(-83);
      var10000[52] = (byte)(-95);
      var10000[53] = (byte)(-73);
      var10000[54] = (byte)(43);
      var10000[55] = (byte)(68);
      var10000[56] = (byte)(10);
      return $$v_s(var10000, 4159944421794893915L);
   }

   @Override
   protected void tickAnimation(float delta) {
      int var2 = Math.max(1, Math.min(this.options.size(), 4));
      int var3 = Math.max(1, (this.options.size() + var2 - 1) / var2);
      float var4 = var3 * 88.0F - 10.0F;
      float var5 = Math.max(0.0F, var4 - this.contentListH());
      this.targetScroll = Math.max(0.0F, Math.min(var5, this.targetScroll));
      this.scroll = AnimationManager.isOff() ? this.targetScroll : AnimationManager.damp(this.scroll, this.targetScroll, 20.0F, delta);
   }

   public boolean method_25401(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
      this.targetScroll -= (float)verticalAmount * 28.0F;
      return true;
   }

   @Override
   protected void renderContent(int mx, int my, float alpha) {
      if (this.options.isEmpty()) {
         byte[] var10000 = new byte[10];
         var10000[0] = (byte)(48);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(60);
         var10000[3] = (byte)(-79);
         var10000[4] = (byte)(-96);
         var10000[5] = (byte)(-67);
         var10000[6] = (byte)(-47);
         var10000[7] = (byte)(126);
         var10000[8] = (byte)(120);
         var10000[9] = (byte)(-47);
         String var14 = $$v_s(var10000, 4159944421794893915L);
         float var15 = SoftPalette.width(var14, 10.0F);
         SoftPalette.label(var14, this.px + (this.pw - var15) / 2.0F, this.py + this.ph / 2.0F, 10.0F, SoftPalette.textDim(200.0F * alpha));
      } else {
         int var4 = Math.max(1, Math.min(this.options.size(), 4));
         float var5 = var4 * 110.0F + (var4 - 1) * 10.0F;
         float var6 = this.px + (this.pw - var5) / 2.0F;
         float var7 = this.contentY() - this.scroll;
         NanoVGRenderer.save();
         NanoVGRenderer.scissor(this.bodyX(), this.contentY(), this.bodyW(), this.contentListH());

         for (int var8 = 0; var8 < this.options.size(); var8++) {
            int var9 = var8 / var4;
            int var10 = var8 % var4;
            float var11 = var6 + var10 * 120.0F;
            float var12 = var7 + var9 * 88.0F;
            if (!(var12 + 78.0F < this.contentY()) && !(var12 > this.contentY() + this.contentListH())) {
               float var13 = this.rowEntrance(var8);
               if (var13 < 0.999F) {
                  NanoVGRenderer.globalAlpha(alpha * var13);
                  this.drawCard(var11, var12, (ThemePresetPickerScreen.ThemeOption)this.options.get(var8), var8 == this.selectedIndex, mx, my, 1.0F);
                  NanoVGRenderer.globalAlpha(1.0F);
               } else {
                  this.drawCard(var11, var12, (ThemePresetPickerScreen.ThemeOption)this.options.get(var8), var8 == this.selectedIndex, mx, my, alpha);
               }
            }
         }

         NanoVGRenderer.resetScissor();
         NanoVGRenderer.restore();
      }
   }

   private void drawCard(float sx, float sy, ThemePresetPickerScreen.ThemeOption opt, boolean sel, int mx, int my, float alpha) {
      boolean var8 = hit(mx, my, sx, sy, 110.0F, 78.0F);
      float var9 = !var8 && !sel ? 0.0F : -2.0F;
      float var10 = sx;
      float var11 = sy + var9;
      if (var8 || sel) {
         SoftPalette.softShadow(var10, var11, 110.0F, 78.0F, 6.0F, 12.0F, 5.0F, 60.0F * alpha);
      }

      NanoVGRenderer.drawRoundedRect(var10, var11, 110.0F, 78.0F, 5.0F, (int)(55.0F * alpha) << 24);
      float var12 = 55.0F;
      float var13 = 4.0F;
      Color var14 = opt.id != null && opt.id.equalsIgnoreCase("RAINBOW")
         ? new Color(RainbowUtil.rainbow(1.0, 0.88, 0L, 255))
         : opt.primaryColor;
      Color var15 = opt.id != null && opt.id.equalsIgnoreCase("RAINBOW")
         ? new Color(RainbowUtil.rainbow(1.0, 0.88, 600L, 255))
         : opt.secondaryColor;
      NanoVGRenderer.drawRoundedRectVarying(var10, var11, var12, 48.0F, var13, 0.0F, 0.0F, 0.0F, applyAlpha(var14, 235.0F * alpha));
      NanoVGRenderer.drawRoundedRectVarying(var10 + var12, var11, var12, 48.0F, 0.0F, var13, 0.0F, 0.0F, applyAlpha(var15, 235.0F * alpha));
      NanoVGRenderer.drawRect(var10 + 2.0F, var11 + 1.2F, 106.0F, 1.0F, (int)(60.0F * alpha) << 24 | 16777215);
      float var16 = 30.0F;
      float var17 = var11 + 48.0F;
      NanoVGRenderer.drawRoundedRectVarying(var10, var17, 110.0F, var16, 0.0F, 0.0F, 5.0F, 5.0F, (int)(240.0F * alpha) << 24 | 1316380);
      if (sel) {
         NanoVGRenderer.drawRoundedRectOutline(var10 - 2.0F, var11 - 2.0F, 114.0F, 82.0F, 6.0F, 2.0F, SoftPalette.mint(255.0F * alpha));
         NanoVGRenderer.drawCircle(var10 + 110.0F - 8.0F, var11 + 8.0F, 3.2F, SoftPalette.white(255.0F * alpha));
         NanoVGRenderer.drawCircle(var10 + 110.0F - 8.0F, var11 + 8.0F, 1.9F, SoftPalette.mint(255.0F * alpha));
      } else if (var8) {
         NanoVGRenderer.drawRoundedRectOutline(var10 - 1.5F, var11 - 1.5F, 113.0F, 81.0F, 6.0F, 1.5F, SoftPalette.white(80.0F * alpha));
      } else {
         NanoVGRenderer.drawRoundedRectOutline(var10, var11, 110.0F, 78.0F, 5.0F, 1.0F, SoftPalette.white(14.0F * alpha));
      }

      String var18 = opt.displayName;
      String var19 = var18;

      while (var19.length() > 3 && SoftPalette.width(var19, 8.5F) > 100.0F) {
         var19 = var19.substring(0, var19.length() - 2) + "…";
      }

      float var20 = SoftPalette.width(var19, 8.5F);
      float var21 = var10 + (110.0F - var20) / 2.0F;
      float var22 = var17 + (var16 - 8.5F) / 2.0F;
      int var23 = sel ? (int)(255.0F * alpha) << 24 | 16777215 : (var8 ? (int)(255.0F * alpha) << 24 | 15790330 : SoftPalette.textDimInt(220.0F * alpha));
      SoftPalette.label(var19, var21, var22, 8.5F, var23);
   }

   private static Color applyAlpha(Color c, float a) {
      int var2 = Math.max(0, Math.min(255, (int)a));
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), var2);
   }

   @Override
   protected boolean mouseClickedContent(double mx, double my, int btn) {
      if (btn == 1) {
         this.closeScreen();
         if (this.field_22787 != null) {
            this.field_22787.method_1507(this.parentScreen);
         }

         return true;
      } else if (btn == 0 && !this.options.isEmpty()) {
         int var6 = Math.max(1, Math.min(this.options.size(), 4));
         float var7 = var6 * 110.0F + (var6 - 1) * 10.0F;
         float var8 = this.px + (this.pw - var7) / 2.0F;
         float var9 = this.contentY() - this.scroll;

         for (int var10 = 0; var10 < this.options.size(); var10++) {
            int var11 = var10 / var6;
            int var12 = var10 % var6;
            float var13 = var8 + var12 * 120.0F;
            float var14 = var9 + var11 * 88.0F;
            if (!(var14 + 78.0F < this.contentY()) && !(var14 > this.contentY() + this.contentListH()) && hit(mx, my, var13, var14, 110.0F, 78.0F)) {
               this.applySelection(var10);
               if (this.field_22787 != null) {
                  this.field_22787.method_1507(this.parentScreen);
               }

               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   protected boolean keyPressedContent(int kc, int sc, int mods) {
      if (this.options.isEmpty()) {
         return false;
      }

      if (kc != (257) && kc != (335)) {
         int var4 = Math.max(1, Math.min(this.options.size(), 4));
         if (kc == (262)) {
            this.selectedIndex = Math.min(this.options.size() - 1, this.selectedIndex + 1);
            return true;
         } else if (kc == (263)) {
            this.selectedIndex = Math.max(0, this.selectedIndex - 1);
            return true;
         } else if (kc == (264)) {
            this.selectedIndex = Math.min(this.options.size() - 1, this.selectedIndex + var4);
            return true;
         } else if (kc == (265)) {
            this.selectedIndex = Math.max(0, this.selectedIndex - var4);
            return true;
         } else {
            return false;
         }
      } else {
         this.applySelection(this.selectedIndex);
         if (this.field_22787 != null) {
            this.field_22787.method_1507(this.parentScreen);
         }

         return true;
      }
   }

   private void applySelection(int index) {
      this.selectedIndex = index;
      if (this.modeSetting != null) {
         this.modeSetting.setModeIndex(index);
      } else if (this.enumSetting != null) {
         Enum[] var2 = this.enumSetting.getValues();
         if (index >= 0 && index < var2.length) {
            this.enumSetting.set(var2[index]);
         }
      }

      try {
         if (isThemeOrPreset(this.modeSetting) || isThemeOrPreset(this.enumSetting)) {
            ThemeManager.setTheme(index);
            SoftPalette.syncTheme();
         }
      } catch (Throwable var3) {
      }
   }

   public static Color[] resolveColors(String optionId, String settingContext) {
      String var2 = (optionId != null ? optionId.toUpperCase() : "").replace((char)(32), (char)(95));
      return PRESET_CACHE.computeIfAbsent(var2, ThemePresetPickerScreen::computeColors);
   }

   private static Color[] computeColors(String key) {
      return switch (key) {
         case "CHAMELEON" -> new Color[]{new Color(65484), new Color(46296)};
         case "RPG_TIER" -> new Color[]{new Color(11868062), new Color(16765286)};
         case "CYBERPUNK_NEON" -> new Color[]{
            new Color(16711807), new Color(61695)
         };
         case "PURPLE_VOID" -> new Color[]{
            new Color(7940298), new Color(4988309)
         };
         case "GOLDEN_GOD" -> new Color[]{new Color(-10496), new Color(16753920)};
         case "CRIMSON_BLOOD" -> new Color[]{
            new Color(-57788), new Color(8323097)
         };
         case "ICE_DIAMOND" -> new Color[]{
            new Color(58879), new Color(12186367)
         };
         case "EMERALD_NATURE" -> new Color[]{
            new Color(1096065), new Color(292951)
         };
         case "RAINBOW" -> new Color[]{new Color(-65409), new Color(61695)};
         case "RAINBOW_WAVE" -> new Color[]{
            new Color(-65434), new Color(58879)
         };
         case "CLEAN_WHITE" -> new Color[]{
            new Color(16777215), new Color(9741240)
         };
         case "CLEAN" -> new Color[]{new Color(3829728), new Color(12113151)};
         case "CLEAN_DAY" -> new Color[]{new Color(2001125), new Color(8508666)};
         case "AURORA_NIGHT" -> new Color[]{
            new Color(736558), new Color(3532464)
         };
         case "BOREALIS" -> new Color[]{new Color(202528), new Color(58787)};
         case "GALAXY" -> new Color[]{new Color(592428), new Color(5324433)};
         case "VIE_CYBER" -> new Color[]{new Color(527390), new Color(10233776)};
         case "MIDNIGHT" -> new Color[]{new Color(329745), new Color(1976390)};
         case "SAKURA_DREAM" -> new Color[]{
            new Color(1970224), new Color(15753874)
         };
         case "SUNSET_DREAM" -> new Color[]{
            new Color(-37772), new Color(-14200)
         };
         case "VOID" -> new Color[]{new Color(855832), new Color(2370104)};
         case "VAPORWAVE" -> new Color[]{new Color(-54651), new Color(61695)};
         case "DEFAULT" -> new Color[]{new Color(16743340), new Color(11032055)};
         case "LAVENDER_MIST" -> new Color[]{
            new Color(12891645), new Color(10980346)
         };
         case "MATCHA_LATTE" -> new Color[]{
            new Color(8843180), new Color(4906624)
         };
         case "PEACH_SORBET" -> new Color[]{
            new Color(16557477), new Color(16478597)
         };
         case "CLOUD_NINE" -> new Color[]{new Color(8246268), new Color(3718648)};
         case "DESERT_SAND" -> new Color[]{
            new Color(16569165), new Color(16096779)
         };
         case "WARM" -> new Color[]{new Color(-34304), new Color(-65451)};
         case "COLD" -> new Color[]{new Color(54527), new Color(26367)};
         case "RARITY" -> new Color[]{new Color(16766720), new Color(10040319)};
         case "STATIC" -> new Color[]{new Color(3900150), new Color(6333946)};
         case "RGB" -> new Color[]{new Color(-65451), new Color(65484)};
         case "TITANIUM" -> new Color[]{new Color(5924472), new Color(8557739)};
         case "GRAPHITE" -> new Color[]{new Color(3817029), new Color(6251888)};
         case "OBSIDIAN" -> new Color[]{new Color(1710622), new Color(2894900)};
         case "SAPPHIRE" -> new Color[]{new Color(1982639), new Color(3900150)};
         case "AURORA" -> new Color[]{new Color(889992), new Color(1357990)};
         case "EMERALD" -> new Color[]{new Color(366185), new Color(1096065)};
         case "ICE" -> new Color[]{new Color(165063), new Color(3718648)};
         case "ROSE" -> new Color[]{new Color(14753096), new Color(16478597)};
         case "AMBER" -> new Color[]{new Color(14251782), new Color(16498468)};
         case "CRIMSON" -> new Color[]{new Color(14427686), new Color(15680580)};
         case "VIOLET" -> new Color[]{new Color(8141549), new Color(11032055)};
         case "GOLD" -> new Color[]{new Color(13273604), new Color(16436245)};
         case "PINK" -> new Color[]{new Color(16743340), new Color(16020150)};
         case "CUSTOM" -> new Color[]{new Color(8490232), new Color(6514417)};
         default -> {
            int var3 = Math.abs(key.hashCode());
            float var4 = var3 % 360 / 360.0F;
            float var5 = (var3 + 45) % 360 / 360.0F;
            yield new Color[]{Color.getHSBColor(var4, 0.75F, 0.95F), Color.getHSBColor(var5, 0.85F, 0.9F)};
         }
      };
   }

   public static String formatDisplayName(String raw) {
      if (raw != null && !raw.isEmpty()) {
         StringBuilder var1 = new StringBuilder();
         boolean var2 = true;

         for (char var6 : raw.replace((char)(95), (char)(32)).toCharArray()) {
            if (Character.isWhitespace(var6)) {
               var1.append((char)(32));
               var2 = true;
            } else if (var2) {
               var1.append(Character.toUpperCase(var6));
               var2 = false;
            } else {
               var1.append(Character.toLowerCase(var6));
            }
         }

         return var1.toString();
      } else {
         return "";
      }
   }

   @Override
   public void method_25419() {
      this.pageOpen = false;
      if (this.field_22787 != null) {
         this.field_22787.method_1507(this.parentScreen);
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

   public static class ThemeOption {
      public final int index;
      public final String id;
      public final String displayName;
      public final Color primaryColor;
      public final Color secondaryColor;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public ThemeOption(int index, String id, String displayName, Color primaryColor, Color secondaryColor) {
         this.index = indexx;
         this.id = idx;
         this.displayName = displayNamex;
         this.primaryColor = primaryColorx;
         this.secondaryColor = secondaryColorx;
      }
   }
}
