package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.config.ConfigSyncManager;
import me.drake.vieclient.core.i18n.Language;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.gui.ChatMacroScreen;
import me.drake.vieclient.gui.nvg.adapter.ConfigManager;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.adapter.module.Category;
import me.drake.vieclient.gui.nvg.adapter.module.Module;
import me.drake.vieclient.gui.nvg.adapter.modules.client.Sounds;
import me.drake.vieclient.gui.nvg.adapter.modules.render.HUD;
import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import me.drake.vieclient.gui.nvg.animation.Spring;
import me.drake.vieclient.render.FrostedBackground;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.NVGIcons;
import me.drake.vieclient.render.nvg.NanoVGFontManager;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RainbowUtil;
import me.drake.vieclient.util.Sfx;
import me.drake.vieclient.util.render.ToastManager;
import net.minecraft.class_156;
import net.minecraft.class_310;
import net.minecraft.class_437;
import org.lwjgl.glfw.GLFW;

public class DashboardView {
   public static final float PANEL_GAP = 3.0F;
   public static float LEFT_PANEL_W = 135.0F;
   public static float RIGHT_PANEL_W = 385.0F;
   public static float SIDEBAR_W = 135.0F;
   public static float DW = 523.0F;
   public static float DH = 340.0F;
   private float animLeftW = targetLeftPanelW();
   private float animRightW = targetRightPanelW();
   private float animDH = targetDH();
   private static final float RADIUS = 12.0F;
   private static final float TOOLBAR_Y = 0.0F;
   private static final float CONTENT_Y = 44.0F;
   private static final float FOOTER_H = 10.0F;
   private static final float ROW_H = 26.0F;
   private static final float COL_GAP = 8.0F;
   private static final float CAT_Y = 55.0F;
   private static final float CAT_H = 24.0F;
   private static final float CAT_STEP = 26.0F;
   private static final float CAT_PAD = 8.0F;
   private static final String[] SECTION_ICONS = new String[]{
      $$v_s(new byte[]{-118, -12, 117, -77, 8, -1, 37, 124, -19}, 4273728924488552654L),
      $$v_s(new byte[]{-115, -22, 111}, 4273728924488552654L),
      $$v_s(new byte[]{-120, -11, 113, -82, 28, -1, 41, 97}, 4273728924488552654L),
      $$v_s(new byte[]{-122, -17, 111, -73, 10}, 4273728924488552654L),
      $$v_s(new byte[]{-120, -11, 114, -86, 8, -24, 56}, 4273728924488552654L)
   };
   private static final String[] SECTION_TIPS = new String[]{
      $$v_s(new byte[]{-86, -12, 117, -77, 8, -1, 37, 124, -19, -47}, 4273728924488552654L),
      $$v_s(new byte[]{-93, -49, 88, -2, 58, -18, 56, 103, -22, -52, -125, -123}, 4273728924488552654L),
      $$v_s(new byte[]{-84, -49, 85, -2, 79, -85, 31, 103, -6, -50, -127}, 4273728924488552654L),
      $$v_s(new byte[]{-90, -17, 111, -73, 10, -85, 4, 70, -57}, 4273728924488552654L),
      $$v_s(new byte[]{-88, -14, 125, -86, 73, -58, 45, 112, -15, -51, -105}, 4273728924488552654L)
   };
   private static int rememberedCategory = 0;
   private int selectedCategory = rememberedCategory;
   private final Spring selSpring = new Spring(rememberedCategory);
   private float selAnim = rememberedCategory;
   private float contentFade = 1.0F;
   private float scroll = 0.0F;
   private float targetScroll = 0.0F;
   private float maxScroll = 0.0F;
   private String searchQuery = "";
   private boolean searchFocused = false;
   private boolean selectedAll = false;
   private boolean newConfigSelectedAll = false;
   private boolean onlyEnabled = false;
   private final float[] onRect = new float[4];
   private long listShownAt = System.currentTimeMillis();
   private final Map toggleAnim = new HashMap();
   private final Map hoverAnim = new HashMap();
   private final Set prevHoveredModules = new HashSet();
   private final Set prevHoveredConfigs = new HashSet();
   private long lastHoverSoundAt = 0L;
   private final List visibleRows = new ArrayList();
   private VieOSPage osPage;
   private Module listeningKeybindModule = null;
   private static final long TIP_DELAY_MS = 220L;
   private String tipKey;
   private String frameTipKey;
   private String frameTipText;
   private float frameTipX;
   private float frameTipY;
   private long tipSince;
   private final float[] searchRect = new float[4];
   private final float[] profileRect = new float[4];
   private final float[] langRect = new float[4];
   private final float[] themeRect = new float[4];
   private final float[] gearRect = new float[4];
   private final float[][] sectionRects = new float[SECTION_ICONS.length][4];
   private final float[] contentRect = new float[4];
   private final List configList = new ArrayList();
   private String defaultConfig = null;
   private String newConfigName = "";
   private boolean namingNewConfig = false;
   private DashboardView.ConfigTab configTab = DashboardView.ConfigTab.PROFILES;
   private float tabProfilesX;
   private float tabProfilesY;
   private float tabProfilesW;
   private float tabProfilesH;
   private float tabCommunityX;
   private float tabCommunityY;
   private float tabCommunityW;
   private float tabCommunityH;
   private final List communityConfigs = new ArrayList();
   private boolean loadingCommunity = false;
   private long lastCommunityFetchTime = 0L;
   private String communitySort = $$v_s(new byte[]{-97, -11, 108}, 4273728924488552654L);
   private String communitySearch = "";
   private boolean communitySearchFocused = false;
   private boolean communitySearchSelectedAll = false;
   private float sortTopX;
   private float sortTopY;
   private float sortTopW;
   private float sortTopH;
   private float sortNewX;
   private float sortNewY;
   private float sortNewW;
   private float sortNewH;
   private float sortDownloadsX;
   private float sortDownloadsY;
   private float sortDownloadsW;
   private float sortDownloadsH;
   private float commSearchX;
   private float commSearchY;
   private float commSearchW;
   private float commSearchH;
   private float commRefreshX;
   private float commRefreshY;
   private float commRefreshW;
   private float commRefreshH;
   private float commPublishBtnX;
   private float commPublishBtnY;
   private float commPublishBtnW;
   private float commPublishBtnH;
   private boolean publishingToCommunity = false;
   private String publishProfileName = "";
   private String publishTitle = "";
   private String publishDesc = "";
   private String publishTags = "";
   private int publishFocusedField = 0;
   private float pubCardX;
   private float pubCardY;
   private float pubCardW;
   private float pubCardH;
   private float pubTitleX;
   private float pubTitleY;
   private float pubTitleW;
   private float pubTitleH;
   private float pubDescX;
   private float pubDescY;
   private float pubDescW;
   private float pubDescH;
   private float pubTagsX;
   private float pubTagsY;
   private float pubTagsW;
   private float pubTagsH;
   private float pubConfirmX;
   private float pubConfirmY;
   private float pubConfirmW;
   private float pubConfirmH;
   private float pubCancelX;
   private float pubCancelY;
   private float pubCancelW;
   private float pubCancelH;
   private final List visibleCommunityRows = new ArrayList();
   private String configStatus = "";
   private long configStatusAt = 0L;
   private String deletingConfig = null;
   private final List visibleConfigRows = new ArrayList();
   private String importCodeText = "";
   private boolean importingCloudConfig = false;
   private boolean importCodeSelectedAll = false;
   private String lastSharedCode = null;
   private boolean showSharePopup = false;
   private boolean copiedCodeNotice = false;
   private float newCfgBtnX;
   private float newCfgBtnY;
   private float newCfgBtnW;
   private float newCfgBtnH;
   private float shareTopBtnX;
   private float shareTopBtnY;
   private float shareTopBtnW;
   private float shareTopBtnH;
   private float importBtnX;
   private float importBtnY;
   private float importBtnW;
   private float importBtnH;
   private float folderBtnX;
   private float folderBtnY;
   private float folderBtnW;
   private float folderBtnH;
   private float refreshBtnX;
   private float refreshBtnY;
   private float refreshBtnW;
   private float refreshBtnH;
   private float shareCopyBtnX;
   private float shareCopyBtnY;
   private float shareCopyBtnW;
   private float shareCopyBtnH;
   private float shareCloseBtnX;
   private float shareCloseBtnY;
   private float shareCloseBtnW;
   private float shareCloseBtnH;
   private float pasteImportX;
   private float pasteImportY;
   private float pasteImportW;
   private float pasteImportH;
   private float doImportX;
   private float doImportY;
   private float doImportW;
   private float doImportH;
   private float cancelImportX;
   private float cancelImportY;
   private float cancelImportW;
   private float cancelImportH;
   private float saveNewX;
   private float saveNewY;
   private float saveNewW;
   private float saveNewH;
   private float cancelNewX;
   private float cancelNewY;
   private float cancelNewW;
   private float cancelNewH;
   private final Set pinnedModules = new HashSet();
   private String lastSearchQuery = null;
   private boolean lastOnlyEnabled = false;
   private Category lastCategory = null;
   private int lastPinnedHash = 0;
   private List cachedFilteredList = null;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static float targetLeftPanelW() {
      return NanoVGFontManager.isUseMinecraftFont() ? 165.0F : 135.0F;
   }

   public static float targetRightPanelW() {
      return NanoVGFontManager.isUseMinecraftFont() ? 550.0F : 385.0F;
   }

   public static float targetDH() {
      return NanoVGFontManager.isUseMinecraftFont() ? 365.0F : 340.0F;
   }

   public static float targetDW() {
      return targetLeftPanelW() + 3.0F + targetRightPanelW();
   }

   public void setOsPage(VieOSPage page) {
      this.osPage = page;
   }

   public void reset() {
      this.searchQuery = "";
      this.searchFocused = false;
      this.selectedAll = false;
      this.newConfigSelectedAll = false;
      this.scroll = 0.0F;
      this.targetScroll = 0.0F;
      this.contentFade = 1.0F;
      this.onlyEnabled = false;
      this.listShownAt = System.currentTimeMillis();
      this.namingNewConfig = false;
      this.newConfigName = "";
      this.deletingConfig = null;
      this.listeningKeybindModule = null;
      this.importingCloudConfig = false;
      this.importCodeText = "";
      this.importCodeSelectedAll = false;
      this.showSharePopup = false;
      this.lastSharedCode = null;
      this.copiedCodeNotice = false;
      this.cachedFilteredList = null;
      this.prevHoveredConfigs.clear();
      this.publishingToCommunity = false;
      this.communitySearchFocused = false;
      this.visibleCommunityRows.clear();
      this.refreshConfigs();
   }

   private Category[] categories() {
      ArrayList var1 = new ArrayList();

      for (Category var5 : Category.values()) {
         if (var5 != Category.CLIENT && (var5 == Category.CONFIG || !this.getModules(var5).isEmpty())) {
            var1.add(var5);
         }
      }

      return var1.toArray(new Category[0]);
   }

   private Category currentCategory() {
      Category[] var1 = this.categories();
      if (var1.length == 0) {
         return Category.CONFIG;
      }

      if (this.selectedCategory >= var1.length) {
         this.selectedCategory = var1.length - 1;
      }

      return var1[Math.max(0, this.selectedCategory)];
   }

   private void selectCategory(int i) {
      if (this.selectedCategory != i) {
         this.selectedCategory = i;
         rememberedCategory = i;
         this.onlyEnabled = false;
         this.listShownAt = System.currentTimeMillis();
         this.scroll = this.targetScroll = 0.0F;
         this.contentFade = 0.0F;
         this.listeningKeybindModule = null;
      }
   }

   public void update(float dt) {
      this.selSpring.set(this.selectedCategory);
      this.selSpring.update(dt);
      this.selAnim = this.selSpring.value;
      this.contentFade = AnimationManager.isOff() ? 1.0F : AnimationManager.damp(this.contentFade, 1.0F, 16.0F, dt);
      float var2 = targetLeftPanelW();
      float var3 = targetRightPanelW();
      float var4 = targetDH();
      if (AnimationManager.isOff()) {
         this.animLeftW = var2;
         this.animRightW = var3;
         this.animDH = var4;
      } else {
         this.animLeftW = AnimationManager.damp(this.animLeftW, var2, 16.0F, dt);
         this.animRightW = AnimationManager.damp(this.animRightW, var3, 16.0F, dt);
         this.animDH = AnimationManager.damp(this.animDH, var4, 16.0F, dt);
      }

      LEFT_PANEL_W = this.animLeftW;
      RIGHT_PANEL_W = this.animRightW;
      SIDEBAR_W = this.animLeftW;
      DH = this.animDH;
      DW = this.animLeftW + 3.0F + this.animRightW;
      float var5 = Math.max(0.0F, Math.min(this.maxScroll, this.targetScroll));
      this.targetScroll = var5;
      this.scroll = AnimationManager.damp(this.scroll, this.targetScroll, 20.0F, dt);
      Category var6 = this.currentCategory();
      if (var6 == Category.CONFIG) {
         if (this.configList.isEmpty()) {
            this.refreshConfigs();
         }
      } else {
         for (Module var8 : this.getModules(var6)) {
            float var9 = var8.isEnabled() ? 1.0F : 0.0F;
            this.toggleAnim.merge(var8, var9, (cur, tgt) -> AnimationManager.damp(cur, tgt, 20.0F, dt));
         }

         for (Entry var13 : this.hoverAnim.entrySet()) {
            boolean var14 = false;

            for (DashboardView.ModuleRow var11 : this.visibleRows) {
               if (var11.module == var13.getKey() && var11.hovered) {
                  var14 = true;
                  break;
               }
            }

            var13.setValue(AnimationManager.damp((Float)var13.getValue(), var14 ? 1.0F : 0.0F, 24.0F, dt));
         }
      }
   }

   public void blitKawase(float s, float cx, float cy, int sw, int sh) {
      float var6 = this.animLeftW;
      float var7 = this.animRightW;
      float var8 = this.animDH;
      float var9 = var6 + 3.0F + var7;
      float var10 = (sw - var9) / 2.0F;
      float var11 = (sh - var8) / 2.0F;
      float var12 = var10 + var6 + 3.0F;
      FrostedBackground.blitGuiRect(var10, var11, var6, var8, 12.0F, s, cx, cy, true);
      FrostedBackground.blitGuiRect(var12, var11, var7, var8, 12.0F, s, cx, cy, true);
   }

   public void render(int mx, int my, float alpha, int sw, int sh) {
      if (!(alpha <= 0.01F)) {
         float var6 = this.animLeftW;
         float var7 = this.animRightW;
         float var8 = this.animDH;
         float var9 = var6 + 3.0F + var7;
         float var10 = (sw - var9) / 2.0F;
         float var11 = (sh - var8) / 2.0F;
         float var12 = var10 + var6 + 3.0F;
         SoftPalette.softShadow(var10, var11, var6, var8, 12.0F, 18.0F, 6.0F, 60.0F * alpha);
         SoftPalette.softShadow(var12, var11, var7, var8, 12.0F, 18.0F, 6.0F, 60.0F * alpha);
         this.frameTipKey = null;
         this.renderSidebar(var10, var11, var6, var8, alpha, mx, my);
         NanoVGRenderer.drawSquircleGradient(
            var12, var11, var7, var8, 8.0F, SoftPalette.rgba(SoftPalette.BODY_TOP, 255.0F * alpha), SoftPalette.rgba(SoftPalette.BODY_BOT, 255.0F * alpha)
         );
         float var13 = var12 + 10.0F;
         float var14 = var7 - 20.0F;
         this.renderToolbar(var13, var11 + 0.0F, var14, alpha, mx, my);
         float var15 = var11 + 44.0F;
         float var16 = var8 - 44.0F - 10.0F;
         this.contentRect[0] = var13;
         this.contentRect[1] = var15;
         this.contentRect[2] = var14;
         this.contentRect[3] = var16;
         float var17 = alpha * this.contentFade;
         float var18 = (1.0F - this.contentFade) * 5.0F;
         if (this.currentCategory() == Category.CONFIG) {
            this.visibleRows.clear();
            this.renderConfigGrid(var13, var15 + var18, var14, var16, var17, mx, my);
         } else {
            this.visibleConfigRows.clear();
            this.renderModuleGrid(var13, var15, var14, var16, var17, var18, mx, my);
         }

         this.tickHoverSounds();
         this.renderTooltip(var10, var11, alpha);
      }
   }

   private void tickHoverSounds() {
      long var1 = System.currentTimeMillis();
      if (this.currentCategory() == Category.CONFIG) {
         HashSet var6 = new HashSet();

         for (DashboardView.ConfigRow var12 : this.visibleConfigRows) {
            if (var12.hovered) {
               var6.add(var12.name);
            }
         }

         for (DashboardView.CommunityRow var13 : this.visibleCommunityRows) {
            if (var13.hovered) {
               var6.add(var13.config.getCode());
            }
         }

         for (String var14 : var6) {
            if (!this.prevHoveredConfigs.contains(var14) && var1 - this.lastHoverSoundAt >= 70L) {
               Sounds.instance.playHovering();
               this.lastHoverSoundAt = var1;
               break;
            }
         }

         this.prevHoveredConfigs.clear();
         this.prevHoveredConfigs.addAll(var6);
      } else {
         HashSet var3 = new HashSet();

         for (DashboardView.ModuleRow var5 : this.visibleRows) {
            if (var5.hovered) {
               var3.add(var5.module);
            }
         }

         for (Module var11 : var3) {
            if (!this.prevHoveredModules.contains(var11) && var1 - this.lastHoverSoundAt >= 70L) {
               Sounds.instance.playHovering();
               this.lastHoverSoundAt = var1;
               break;
            }
         }

         this.prevHoveredModules.clear();
         this.prevHoveredModules.addAll(var3);
      }
   }

   private void hint(String key, String text, float ax, float ay) {
      if (text != null && !text.isBlank()) {
         this.frameTipKey = key;
         this.frameTipText = text;
         this.frameTipX = ax;
         this.frameTipY = ay;
      }
   }

   private void renderTooltip(float dx, float dy, float alpha) {
      long var4 = System.currentTimeMillis();
      if (this.frameTipKey != null && this.frameTipKey.equals(this.tipKey)) {
         long var6 = var4 - this.tipSince - 220L;
         if (var6 > 0L) {
            float var8 = alpha * Math.min(1.0F, (float)var6 / 100.0F);
            float var9 = 8.5F;
            float var10 = 6.0F;
            String var11 = SoftPalette.fit(this.frameTipText, 240.0F, var9);
            float var12 = SoftPalette.width(var11, var9) + var10 * 2.0F;
            float var13 = var9 + var10 * 1.5F;
            float var14 = this.animLeftW + 3.0F + this.animRightW;
            float var15 = this.animDH;
            float var16 = Math.max(dx + 4.0F, Math.min(dx + var14 - var12 - 4.0F, this.frameTipX));
            float var17 = Math.max(dy + 4.0F, Math.min(dy + var15 - var13 - 4.0F, this.frameTipY));
            NanoVGRenderer.drawRoundedRect(var16, var17 + 2.0F, var12, var13, 6.0F, new Color(0, 0, 0, SoftPalette.clamp(70.0F * var8)));
            NanoVGRenderer.drawRoundedRect(var16, var17, var12, var13, 6.0F, SoftPalette.rgba(SoftPalette.BODY_TOP, 255.0F * var8));
            NanoVGRenderer.drawRoundedRectOutline(var16, var17, var12, var13, 6.0F, 1.0F, SoftPalette.white(28.0F * var8));
            SoftPalette.label(var11, var16 + var10, var17 + (var13 - var9) / 2.0F, var9, SoftPalette.text(245.0F * var8));
         }
      } else {
         this.tipKey = this.frameTipKey;
         this.tipSince = var4;
      }
   }

   private void renderSidebar(float dx, float dy, float pw, float dh, float alpha, int mx, int my) {
      float var8 = dx;
      NanoVGRenderer.drawSquircleGradient(
         var8, dy, pw, dh, 8.0F, SoftPalette.rgba(SoftPalette.BODY_TOP, 255.0F * alpha), SoftPalette.rgba(SoftPalette.BODY_BOT, 255.0F * alpha)
      );
      this.gearRect[0] = this.gearRect[1] = this.gearRect[2] = this.gearRect[3] = 0.0F;
      float var9 = dx + 8.0F;
      float var10 = dy + 28.0F;
      float var11 = pw - 16.0F;
      float var12 = 20.0F;
      this.searchRect[0] = var9;
      this.searchRect[1] = var10;
      this.searchRect[2] = var11;
      this.searchRect[3] = var12;
      NanoVGRenderer.drawRoundedRect(var9, var10, var11, var12, var12 / 2.0F, SoftPalette.white((this.searchFocused ? 20 : 10) * alpha));
      if (this.searchFocused) {
         NanoVGRenderer.drawRoundedRectOutline(var9, var10, var11, var12, var12 / 2.0F, 1.0F, SoftPalette.mint(180.0F * alpha));
      } else {
         NanoVGRenderer.drawRoundedRectOutline(var9, var10, var11, var12, var12 / 2.0F, 1.0F, SoftPalette.white(16.0F * alpha));
      }

      IconManager.draw(
         $$v_s(new byte[]{-104, -1, 125, -84, 10, -29}, 4273728924488552654L),
         var9 + 6.0F,
         var10 + (var12 - 10.0F) / 2.0F,
         10.0F,
         this.searchFocused ? SoftPalette.mint(230.0F * alpha) : SoftPalette.textDim(200.0F * alpha)
      );
      String var13 = this.searchQuery.isEmpty()
         ? (
            this.searchFocused
               ? ""
               : (
                  Localization.isVietnamese()
                     ? $$v_s(new byte[]{-65, 89, -80, -77, 73, -32, 37, -14, 57, 29, -119, -40, 31, 125}, 4273728924488552654L)
                     : $$v_s(new byte[]{-72, -1, 125, -84, 10, -29, 98, 61, -83}, 4273728924488552654L)
               )
         )
         : this.searchQuery;
      if (!this.searchFocused && hit(mx, my, this.searchRect)) {
         this.hint(
            $$v_s(new byte[]{-104, -1, 125, -84, 10, -29}, 4273728924488552654L),
            Localization.isVietnamese()
               ? $$v_s(
                  new byte[]{
                     -65,
                     89,
                     -80,
                     -77,
                     73,
                     -32,
                     37,
                     -14,
                     57,
                     29,
                     -119,
                     -42,
                     69,
                     60,
                     -89,
                     -53,
                     -43,
                     106,
                     -82,
                     79,
                     98,
                     -30,
                     60,
                     46,
                     60,
                     118,
                     -31,
                     42,
                     -124,
                     35,
                     28,
                     -40,
                     127,
                     72,
                     16,
                     94,
                     -94,
                     11,
                     -86,
                     -38
                  },
                  4273728924488552654L
               )
               : $$v_s(
                  new byte[]{
                     -72, -1, 125, -84, 10, -29, 108, 114, -17, -50, -60, -101, 94, 55, 17, 7, -34, 57, -20, -122, -102, 15, 110, 47, 115, 57, -76, 0, -56
                  },
                  4273728924488552654L
               ),
            var9,
            var10 + var12 + 4.0F
         );
      }

      String var14 = SoftPalette.fit(var13, var11 - 36.0F, 9.0F);
      float var15 = var9 + 20.0F;
      float var16 = var10 + (var12 - 9.0F) / 2.0F;
      if (this.searchFocused && this.selectedAll && !this.searchQuery.isEmpty()) {
         float var38 = SoftPalette.width(var14, 9.0F);
         NanoVGRenderer.drawRoundedRect(var15 - 2.0F, var10 + 3.0F, var38 + 4.0F, var12 - 6.0F, 2.0F, SoftPalette.mint(120.0F * alpha));
         SoftPalette.label(var14, var15, var16, 9.0F, SoftPalette.white(255.0F * alpha));
      } else {
         SoftPalette.label(var14, var15, var16, 9.0F, this.searchQuery.isEmpty() ? SoftPalette.textDim(170.0F * alpha) : SoftPalette.text(255.0F * alpha));
         if (this.searchFocused && System.currentTimeMillis() / 450L % 2L == 0L) {
            float var17 = this.searchQuery.isEmpty() ? 0.0F : SoftPalette.width(var14, 9.0F);
            NanoVGRenderer.drawRect(var15 + var17 + 1.0F, var10 + 3.5F, 1.2F, var12 - 7.0F, SoftPalette.mint(240.0F * alpha));
         }
      }

      if (!this.searchQuery.isEmpty()) {
         float var39 = var9 + var11 - 10.0F;
         float var18 = var10 + var12 / 2.0F;
         NanoVGRenderer.drawCircle(var39, var18, 5.0F, SoftPalette.white(36.0F * alpha));
         NVGIcons.cross(var39, var18, 2.8F, 1.1F, SoftPalette.text(240.0F * alpha));
      }

      Category[] var40 = this.categories();
      float var41 = dx + 8.0F;
      float var19 = pw - 16.0F;
      float var20 = dy + 55.0F;
      boolean var21 = !this.searchQuery.trim().isEmpty();
      if (!var21) {
         NanoVGRenderer.drawRoundedRect(var41, var20 + this.selAnim * 26.0F, var19, 24.0F, 6.0F, SoftPalette.white(15.0F * alpha));
         NanoVGRenderer.drawRoundedRectOutline(var41, var20 + this.selAnim * 26.0F, var19, 24.0F, 6.0F, 1.0F, SoftPalette.white(22.0F * alpha));
      }

      for (int var22 = 0; var22 < var40.length; var22++) {
         Category var23 = var40[var22];
         float var24 = var20 + var22 * 26.0F;
         boolean var25 = var22 == this.selectedCategory;
         boolean var26 = !var25 && mx >= var41 && mx <= var41 + var19 && my >= var24 && my <= var24 + 24.0F;
         if (var26) {
            NanoVGRenderer.drawRoundedRect(var41, var24, var19, 24.0F, 6.0F, SoftPalette.white(9.0F * alpha));
         }

         float var27 = 11.0F;
         float var28 = var41 + 8.0F;
         Color var29 = var25 ? SoftPalette.mint(255.0F * alpha) : (var26 ? SoftPalette.text(240.0F * alpha) : SoftPalette.textDim(200.0F * alpha));
         IconManager.draw(categoryIcon(var23), var28, var24 + (24.0F - var27) / 2.0F, var27, var29);
         String var30 = Localization.getCategoryName(var23.name.toString());
         float var31 = var19 - var27 - 36.0F;
         SoftPalette.label(
            SoftPalette.fit(var30, var31, 9.0F),
            var28 + var27 + 8.0F,
            var24 + 7.5F,
            9.0F,
            var25 ? SoftPalette.text(255.0F * alpha) : SoftPalette.text((var26 ? 245 : 205) * alpha)
         );
         if (var23 != Category.CONFIG) {
            int var32 = 0;

            for (Module var34 : this.getModules(var23)) {
               if (var34.isEnabled()) {
                  var32++;
               }
            }

            if (var32 > 0) {
               String var54 = String.valueOf(var32);
               float var56 = SoftPalette.widthBold(var54, 7.0F) + 8.0F;
               float var35 = 12.0F;
               float var36 = var41 + var19 - var56 - 5.0F;
               float var37 = var24 + (24.0F - var35) / 2.0F;
               NanoVGRenderer.drawRoundedRect(
                  var36, var37, var56, var35, var35 / 2.0F, var25 ? SoftPalette.mint(50.0F * alpha) : SoftPalette.white(20.0F * alpha)
               );
               SoftPalette.labelBold(
                  var54,
                  var36 + (var56 - SoftPalette.widthBold(var54, 7.0F)) / 2.0F,
                  var37 + (var35 - 7.0F) / 2.0F,
                  7.0F,
                  var25 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(225.0F * alpha)
               );
            }
         } else if (!this.configList.isEmpty()) {
            String var52 = String.valueOf(this.configList.size());
            float var55 = SoftPalette.widthBold(var52, 7.0F) + 8.0F;
            float var57 = 12.0F;
            float var58 = var41 + var19 - var55 - 5.0F;
            float var59 = var24 + (24.0F - var57) / 2.0F;
            NanoVGRenderer.drawRoundedRect(var58, var59, var55, var57, var57 / 2.0F, var25 ? SoftPalette.mint(50.0F * alpha) : SoftPalette.white(20.0F * alpha));
            SoftPalette.labelBold(
               var52,
               var58 + (var55 - SoftPalette.widthBold(var52, 7.0F)) / 2.0F,
               var59 + (var57 - 7.0F) / 2.0F,
               7.0F,
               var25 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(225.0F * alpha)
            );
         }
      }

      int var42 = SECTION_ICONS.length;
      float var43 = 19.0F;
      float var44 = var43 + 5.0F;
      float var45 = dy + dh - 10.0F - var44;
      NanoVGRenderer.drawRoundedRect(var41, var45, var19, var44, var44 / 2.0F, SoftPalette.white(8.0F * alpha));
      NanoVGRenderer.drawRoundedRectOutline(var41, var45, var19, var44, var44 / 2.0F, 1.0F, SoftPalette.white(10.0F * alpha));
      float var46 = (var19 - 6.0F - var43 * var42) / (var42 - 1);
      float var47 = var45 + 2.5F;

      for (int var48 = 0; var48 < var42; var48++) {
         float var49 = var41 + 3.0F + var48 * (var43 + var46);
         float[] var50 = this.sectionRects[var48];
         var50[0] = var49;
         var50[1] = var47;
         var50[2] = var43;
         var50[3] = var43;
         boolean var51 = mx >= var49 && mx <= var49 + var43 && my >= var47 && my <= var47 + var43;
         if (var51) {
            NanoVGRenderer.drawRoundedRect(var49, var47, var43, var43, var43 / 2.0F, SoftPalette.white(18.0F * alpha));

            String var53 = switch (var48) {
               case 0 -> Localization.get(
                  $$v_s(new byte[]{-104, -1, 127, -16, 8, -27, 37, 126, -30, -42, -115, -103, 95, 32}, 4273728924488552654L), SECTION_TIPS[0]
               );
               case 1 -> Localization.get(
                  $$v_s(new byte[]{-104, -1, 127, -16, 1, -2, 40}, 4273728924488552654L),
                  Localization.get($$v_s(new byte[]{-104, -1, 127, -16, 15, -5, 63}, 4273728924488552654L), SECTION_TIPS[1])
               );
               case 2 -> Localization.get($$v_s(new byte[]{-104, -1, 127, -16, 14, -2, 37}, 4273728924488552654L), SECTION_TIPS[2]);
               case 3 -> Localization.get($$v_s(new byte[]{-104, -1, 127, -16, 4, -2, 63, 122, -32}, 4273728924488552654L), SECTION_TIPS[3]);
               case 4 -> Localization.get($$v_s(new byte[]{-104, -1, 127, -16, 4, -22, 47, 97, -20, -47}, 4273728924488552654L), SECTION_TIPS[4]);
               default -> SECTION_TIPS[var48];
            };
            this.hint("section" + var48, var53, var49, var47 - 24.0F);
         }

         IconManager.draw(
            SECTION_ICONS[var48], var49 + 4.5F, var47 + 4.5F, 10.0F, var51 ? SoftPalette.text(255.0F * alpha) : SoftPalette.textDim(180.0F * alpha)
         );
      }
   }

   private void renderToolbar(float x, float y, float w, float alpha, int mx, int my) {
      Category var7 = this.currentCategory();
      boolean var8 = !this.searchQuery.trim().isEmpty();
      List var9 = this.getFilteredModules(var7);
      int var10 = var7 == Category.CONFIG ? this.configList.size() : var9.size();
      String var11 = this.onlyEnabled
         ? (Localization.isVietnamese() ? "Module đang bật (" + var10 + ")" : "Enabled Modules (" + var10 + ")")
         : (
            var8 && var7 != Category.CONFIG
               ? (Localization.isVietnamese() ? "Kết quả tìm kiếm (" + var10 + ")" : "Search Results (" + var10 + ")")
               : Localization.getCategoryName(var7.name.toString())
         );
      float var12 = y + 8.0F;
      float var13 = 13.0F;
      if (this.onlyEnabled || var8 && var7 != Category.CONFIG) {
         SoftPalette.labelBold(var11, x, var12 + 1.0F, 12.5F, SoftPalette.text(255.0F * alpha));
      } else {
         IconManager.draw(categoryIcon(var7), x, var12, var13, SoftPalette.mint(240.0F * alpha));
         SoftPalette.labelBold(var11, x + var13 + 6.0F, var12 + 1.0F, 12.5F, SoftPalette.text(255.0F * alpha));
      }

      float var14 = 14.0F;
      this.profileRect[0] = x + w - var14;
      this.profileRect[1] = var12;
      this.profileRect[2] = var14;
      this.profileRect[3] = var14;
      boolean var15 = mx >= this.profileRect[0] && mx <= this.profileRect[0] + var14 && my >= this.profileRect[1] && my <= this.profileRect[1] + var14;
      if (var15) {
         NanoVGRenderer.drawRoundedRect(this.profileRect[0], this.profileRect[1], var14, var14, 4.0F, SoftPalette.white(18.0F * alpha));
         this.hint(
            $$v_s(new byte[]{-104, -1, 104, -86, 0, -27, 43, 96}, 4273728924488552654L),
            Localization.isVietnamese()
               ? $$v_s(new byte[]{-88, 89, -68, -73, 73, 79, -35, -14, 57, 21, -112, -42, 118, 58, 5, 4, -101, 46, -91, 79, 98, -4, 114}, 4273728924488552654L)
               : $$v_s(
                  new byte[]{
                     -88, -10, 117, -69, 7, -1, 108, 64, -26, -42, -112, -97, 95, 52, 23, 75, -109, 13, -103, -25, -7, 93, 60, 16, 39, 107, -8, 35, -56
                  },
                  4273728924488552654L
               ),
            this.profileRect[0] - 60.0F,
            this.profileRect[1] + var14 + 4.0F
         );
      }

      IconManager.draw(
         $$v_s(new byte[]{-104, -1, 104, -86, 0, -27, 43, 96}, 4273728924488552654L),
         this.profileRect[0] + 2.5F,
         this.profileRect[1] + 2.5F,
         var14 - 5.0F,
         var15 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.textDim(220.0F * alpha)
      );
      float var16 = 38.0F;
      float var17 = 14.0F;
      float var18 = this.profileRect[0] - 6.0F - var16;
      this.langRect[0] = var18;
      this.langRect[1] = var12;
      this.langRect[2] = var16;
      this.langRect[3] = var17;
      boolean var19 = mx >= var18 && mx <= var18 + var16 && my >= var12 && my <= var12 + var17;
      if (var19) {
         this.hint(
            $$v_s(new byte[]{-121, -5, 114, -71, 58, -4, 37, 103, -32, -54}, 4273728924488552654L),
            Localization.getLanguageHint(),
            var18 - 80.0F,
            var12 + var17 + 4.0F
         );
      }

      NanoVGRenderer.drawRoundedRect(var18, var12, var16, var17, var17 / 2.0F, SoftPalette.white((var19 ? 22 : 10) * alpha));
      NanoVGRenderer.drawRoundedRectOutline(
         var18, var12, var16, var17, var17 / 2.0F, 1.0F, var19 ? SoftPalette.mint(160.0F * alpha) : SoftPalette.white(20.0F * alpha)
      );
      float var20 = 8.5F;
      float var21 = var18 + 4.5F;
      float var22 = var12 + (var17 - var20) / 2.0F;
      if (IconManager.has($$v_s(new byte[]{-121, -5, 114, -71, 28, -22, 43, 118}, 4273728924488552654L))) {
         IconManager.draw(
            $$v_s(new byte[]{-121, -5, 114, -71, 28, -22, 43, 118}, 4273728924488552654L), var21, var22, var20, (int)((var19 ? 255 : 200) * alpha)
         );
      } else {
         NVGIcons.globe(
            var21 + var20 / 2.0F, var22 + var20 / 2.0F, var20 / 2.0F, 1.0F, var19 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.textDim(200.0F * alpha)
         );
      }

      String var23 = Localization.getLanguage().getCode();
      float var24 = var12 + (var17 - 7.5F) / 2.0F;
      SoftPalette.labelBold(var23, var21 + var20 + 3.5F, var24, 7.5F, var19 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(225.0F * alpha));
      float var25 = 14.0F;
      float var26 = var18 - 6.0F - var25;
      this.themeRect[0] = var26;
      this.themeRect[1] = var12;
      this.themeRect[2] = var25;
      this.themeRect[3] = var25;
      boolean var27 = mx >= var26 && mx <= var26 + var25 && my >= var12 && my <= var12 + var25;
      if (var27) {
         NanoVGRenderer.drawRoundedRect(var26, var12, var25, var25, 4.0F, SoftPalette.white(18.0F * alpha));
         this.hint(
            $$v_s(new byte[]{-97, -14, 121, -77, 12, -37, 37, 112, -24, -57, -106}, 4273728924488552654L),
            Localization.isVietnamese()
               ? $$v_s(
                  new byte[]{-88, -14, -3, 101, -50, -85, -120, -126, 98, 25, 101, -42, 30, 115, 35, 2, -38, 37, -20, -54, -80, -102, -89, -60, 61},
                  4273728924488552654L
               )
               : $$v_s(new byte[]{-65, -14, 121, -77, 12, -8, 108, 53, -93, -31, -117, -102, 94, 33, 23}, 4273728924488552654L),
            var26 - 60.0F,
            var12 + var25 + 4.0F
         );
      }

      IconManager.draw(
         $$v_s(new byte[]{-120, -17, 111, -86, 6, -26}, 4273728924488552654L),
         var26 + 2.5F,
         var12 + 2.5F,
         var25 - 5.0F,
         var27 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.textDim(220.0F * alpha)
      );
      this.onRect[2] = 0.0F;
      if (var7 != Category.CONFIG) {
         int var28 = 0;

         for (Category var32 : this.categories()) {
            if (var32 != Category.CONFIG) {
               for (Module var34 : this.getModules(var32)) {
                  if (var34.isEnabled()) {
                     var28++;
                  }
               }
            }
         }

         String var40 = var28
            + (
               Localization.isVietnamese()
                  ? $$v_s(new byte[]{-53, 94, -115, -65, 7, -20, 108, 113, 98, 24, 73, -126}, 4273728924488552654L)
                  : $$v_s(new byte[]{-53, -5, 127, -86, 0, -3, 41}, 4273728924488552654L)
            );
         float var43 = SoftPalette.widthBold(var40, 7.5F) + 20.0F;
         float var46 = 14.0F;
         float var49 = var26 - 6.0F - var43;
         float var52 = var12;
         this.onRect[0] = var49;
         this.onRect[1] = var52;
         this.onRect[2] = var43;
         this.onRect[3] = var46;
         boolean var55 = hit(mx, my, this.onRect);
         NanoVGRenderer.drawRoundedRect(
            var49, var52, var43, var46, var46 / 2.0F, this.onlyEnabled ? SoftPalette.mint(45.0F * alpha) : SoftPalette.white((var55 ? 18 : 10) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            var49, var52, var43, var46, var46 / 2.0F, 1.0F, this.onlyEnabled ? SoftPalette.mint(160.0F * alpha) : SoftPalette.white(20.0F * alpha)
         );
         NanoVGRenderer.drawCircle(var49 + 7.0F, var52 + var46 / 2.0F, 2.2F, var28 > 0 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.white(70.0F * alpha));
         SoftPalette.labelBold(
            var40,
            var49 + 12.0F,
            var52 + (var46 - 7.5F) / 2.0F,
            7.5F,
            this.onlyEnabled ? SoftPalette.mint(255.0F * alpha) : (var55 ? SoftPalette.text(255.0F * alpha) : SoftPalette.text(220.0F * alpha))
         );
         if (var55) {
            this.hint(
               $$v_s(new byte[]{-124, -12, 122, -73, 5, -1, 41, 97}, 4273728924488552654L),
               this.onlyEnabled
                  ? (
                     Localization.isVietnamese()
                        ? $$v_s(
                           new byte[]{-93, -13, -3, 101, -18, -27, 108, 103, 98, 24, 65, -126, 17, 48, -123, -47, 24, 106, -95, -63, -67, 14, 112, 38, 32},
                           4273728924488552654L
                        )
                        : $$v_s(new byte[]{-72, -14, 115, -87, 73, -22, 32, 127, -93, -49, -117, -110, 68, 63, 1, 24}, 4273728924488552654L)
                  )
                  : (
                     Localization.isVietnamese()
                        ? $$v_s(
                           new byte[]{
                              -89,
                              123,
                              -89,
                              83,
                              10,
                              -79,
                              108,
                              112,
                              -21,
                              67,
                              95,
                              127,
                              17,
                              59,
                              13,
                              -118,
                              0,
                              -51,
                              -94,
                              -114,
                              -76,
                              20,
                              120,
                              54,
                              63,
                              119,
                              -76,
                              -126,
                              112,
                              98,
                              90,
                              -4,
                              43,
                              88,
                              -99,
                              -60,
                              36,
                              95
                           },
                           4273728924488552654L
                        )
                        : $$v_s(
                           new byte[]{
                              -83,
                              -13,
                              112,
                              -86,
                              12,
                              -7,
                              118,
                              51,
                              -16,
                              -54,
                              -117,
                              -127,
                              17,
                              60,
                              10,
                              7,
                              -62,
                              106,
                              -87,
                              -64,
                              -72,
                              25,
                              112,
                              38,
                              55,
                              50,
                              -7,
                              41,
                              -123,
                              118,
                              88,
                              -2,
                              120
                           },
                           4273728924488552654L
                        )
                  ),
               var49 - 30.0F,
               var52 + var46 + 4.0F
            );
         }
      } else {
         int var38 = this.configList.size();
         String var41 = var38
            + (
               Localization.isVietnamese()
                  ? $$v_s(new byte[]{-53, -7, -3, 100, -52, -2, 108, 123, 64, 14, -118, -98}, 4273728924488552654L)
                  : (
                     var38 == 1
                        ? $$v_s(new byte[]{-53, -22, 110, -79, 15, -30, 32, 118}, 4273728924488552654L)
                        : $$v_s(new byte[]{-53, -22, 110, -79, 15, -30, 32, 118, -16}, 4273728924488552654L)
                  )
            );
         float var44 = SoftPalette.widthBold(var41, 7.5F) + 20.0F;
         float var47 = 14.0F;
         float var50 = var18 - 6.0F - var44;
         float var53 = var12;
         this.onRect[0] = var50;
         this.onRect[1] = var53;
         this.onRect[2] = var44;
         this.onRect[3] = var47;
         boolean var56 = hit(mx, my, this.onRect);
         NanoVGRenderer.drawRoundedRect(var50, var53, var44, var47, var47 / 2.0F, SoftPalette.white((var56 ? 20 : 10) * alpha));
         NanoVGRenderer.drawRoundedRectOutline(var50, var53, var44, var47, var47 / 2.0F, 1.0F, SoftPalette.white((var56 ? 32 : 18) * alpha));
         NanoVGRenderer.drawCircle(var50 + 7.0F, var53 + var47 / 2.0F, 2.2F, var38 > 0 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.white(70.0F * alpha));
         SoftPalette.labelBold(
            var41, var50 + 12.0F, var53 + (var47 - 7.5F) / 2.0F, 7.5F, var56 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(220.0F * alpha)
         );
         if (var56) {
            this.hint(
               $$v_s(new byte[]{-120, -4, 123, -82, 0, -25, 32}, 4273728924488552654L),
               Localization.isVietnamese()
                  ? $$v_s(
                     new byte[]{
                        -91,
                        -14,
                        -3,
                        100,
                        -52,
                        -5,
                        108,
                        -41,
                        18,
                        67,
                        95,
                        117,
                        17,
                        39,
                        -123,
                        -47,
                        24,
                        35,
                        -20,
                        -62,
                        56,
                        -63,
                        -67,
                        42,
                        115,
                        113,
                        117,
                        -4,
                        68,
                        118,
                        20,
                        -13,
                        -56,
                        -106,
                        18,
                        22,
                        -87,
                        95,
                        13,
                        72,
                        -120,
                        -30,
                        -128,
                        -121,
                        -107,
                        90,
                        -27
                     },
                     4273728924488552654L
                  )
                  : $$v_s(
                     new byte[]{
                        -88,
                        -10,
                        117,
                        -67,
                        2,
                        -85,
                        56,
                        124,
                        -93,
                        -48,
                        -127,
                        -102,
                        94,
                        50,
                        0,
                        75,
                        -53,
                        56,
                        -93,
                        -56,
                        -80,
                        23,
                        121,
                        48,
                        115,
                        116,
                        -26,
                        41,
                        -116,
                        35,
                        80,
                        -14,
                        120,
                        81
                     },
                     4273728924488552654L
                  ),
               var50 - 30.0F,
               var53 + var47 + 4.0F
            );
         }
      }

      if (!this.configStatus.isEmpty() && System.currentTimeMillis() - this.configStatusAt < 4000L) {
         float var39 = Math.min(1.0F, (float)(4000L - (System.currentTimeMillis() - this.configStatusAt)) / 500.0F);
         float var42 = 14.0F;
         float var45 = var12;
         float var48 = x + (this.onlyEnabled || var8 && var7 != Category.CONFIG ? 0.0F : var13 + 6.0F) + SoftPalette.widthBold(var11, 12.5F) + 10.0F;
         float var51 = this.onRect[0] > 0.0F ? this.onRect[0] - 8.0F : (this.themeRect[0] > 0.0F ? this.themeRect[0] - 8.0F : this.profileRect[0] - 8.0F);
         float var54 = Math.max(30.0F, var51 - var48);
         String var57 = SoftPalette.fit(this.configStatus, var54 - 22.0F, 7.5F);
         float var35 = Math.min(var54, SoftPalette.widthBold(var57, 7.5F) + 20.0F);
         float var36 = var48;
         boolean var37 = hit(mx, my, var36, var45, var35, var42);
         if (var37) {
            this.hint($$v_s(new byte[]{-120, -4, 123, -83, 29, -22, 56, 102, -16}, 4273728924488552654L), this.configStatus, var36, var45 + var42 + 4.0F);
         }

         NanoVGRenderer.drawRoundedRect(var36, var45, var35, var42, var42 / 2.0F, SoftPalette.white(14.0F * alpha * var39));
         NanoVGRenderer.drawRoundedRectOutline(var36, var45, var35, var42, var42 / 2.0F, 1.0F, SoftPalette.mint(130.0F * alpha * var39));
         NanoVGRenderer.drawCircle(var36 + 7.0F, var45 + var42 / 2.0F, 2.2F, SoftPalette.mint(255.0F * alpha * var39));
         SoftPalette.labelBold(var57, var36 + 13.0F, var45 + (var42 - 7.5F) / 2.0F, 7.5F, SoftPalette.mint(255.0F * alpha * var39));
      }
   }

   private void renderModuleGrid(float x, float y, float w, float h, float alpha, float slide, int mx, int my) {
      List var9 = this.getFilteredModules(this.currentCategory());
      int var10 = var9.size();
      float var11 = (w - 8.0F) / 2.0F;
      int var12 = (var10 + 1) / 2;
      float var13 = var12 * 26.0F;
      this.maxScroll = Math.max(0.0F, var13 + 4.0F - h);
      this.visibleRows.clear();
      if (var10 == 0) {
         String var25 = this.onlyEnabled
            ? (
               Localization.isVietnamese()
                  ? $$v_s(
                     new byte[]{-88, -14, -38, 110, 8, -85, 46, -14, 57, 15, -112, -42, 92, 60, 0, 30, -41, 47, -20, -64, 26, -37, 115}, 4273728924488552654L
                  )
                  : $$v_s(new byte[]{-91, -11, 60, -77, 6, -17, 57, 127, -26, -47, -60, -105, 67, 54, 68, 14, -43, 43, -82, -62, -68, 31}, 4273728924488552654L)
            )
            : (
               this.searchQuery.isEmpty()
                  ? (
                     Localization.isVietnamese()
                        ? $$v_s(
                           new byte[]{
                              -96,
                              -14,
                              -33,
                              106,
                              7,
                              -20,
                              108,
                              112,
                              64,
                              17,
                              -60,
                              -101,
                              94,
                              55,
                              17,
                              7,
                              -34,
                              106,
                              -72,
                              -36,
                              -74,
                              21,
                              123,
                              99,
                              62,
                              -13,
                              47,
                              -29,
                              -126,
                              35,
                              90,
                              88,
                              -85,
                              67
                           },
                           4273728924488552654L
                        )
                        : $$v_s(
                           new byte[]{-91, -11, 60, -77, 6, -17, 57, 127, -26, -47, -60, -97, 95, 115, 16, 3, -46, 57, -20, -51, -72, 15, 121, 36, 60, 96, -19},
                           4273728924488552654L
                        )
                  )
                  : (Localization.isVietnamese() ? "Không tìm thấy kết quả cho \"" + this.searchQuery + "\"" : "No results for \"" + this.searchQuery + "\"")
            );
         SoftPalette.label(var25, x + 4.0F, y + 10.0F + slide, 9.5F, SoftPalette.textDim(180.0F * alpha));
      } else {
         NanoVGRenderer.scissor(x - 2.0F, y, w + 4.0F, h);
         boolean var14 = mx >= x && mx <= x + w && my >= y && my <= y + h;

         for (int var15 = 0; var15 < 2; var15++) {
            int var16 = var15 == 0 ? 0 : var12;
            int var17 = var15 == 0 ? var12 : var10;
            if (var17 > var16) {
               float var18 = x + var15 * (var11 + 8.0F);
               float var19 = y + slide - this.scroll;
               float var20 = (var17 - var16) * 26.0F;
               NanoVGRenderer.drawRoundedRect(var18, var19, var11, var20, 8.0F, SoftPalette.white(6.0F * alpha));
               NanoVGRenderer.drawRoundedRectOutline(var18, var19, var11, var20, 8.0F, 1.0F, SoftPalette.white(10.0F * alpha));

               for (int var21 = var16; var21 < var17; var21++) {
                  Module var22 = (Module)var9.get(var21);
                  float var23 = var19 + (var21 - var16) * 26.0F;
                  if (!(var23 + 26.0F < y) && !(var23 > y + h)) {
                     if (var21 > var16) {
                        NanoVGRenderer.drawRect(var18 + 8.0F, var23, var11 - 16.0F, 1.0F, SoftPalette.white(6.0F * alpha));
                     }

                     float var24 = this.rowEntrance(var21 - var16 + var15);
                     if (var24 < 0.999F) {
                        NanoVGRenderer.save();
                        NanoVGRenderer.translate(0.0F, (1.0F - var24) * 4.0F);
                     }

                     this.drawModuleRow(var22, var18, var23, var11, alpha * var24, mx, my, var14, var21 == var16, var21 == var17 - 1);
                     if (var24 < 0.999F) {
                        NanoVGRenderer.restore();
                     }
                  }
               }
            }
         }

         if (this.scroll > 1.0F) {
            NanoVGRenderer.drawVerticalGradientRect(
               x - 2.0F, y, w + 4.0F, 12.0F, SoftPalette.rgba(SoftPalette.BODY_TOP, 200.0F * alpha), SoftPalette.rgba(SoftPalette.BODY_TOP, 0.0F)
            );
         }

         NanoVGRenderer.resetScissor();
         if (this.maxScroll > 0.0F) {
            float var26 = Math.max(16.0F, h * (h / (h + this.maxScroll)));
            float var27 = y + this.scroll / this.maxScroll * (h - var26);
            NanoVGRenderer.drawRoundedRect(x + w + 3.0F, var27, 2.5F, var26, 1.25F, SoftPalette.white(45.0F * alpha));
         }
      }
   }

   private float rowEntrance(int index) {
      if (AnimationManager.isOff()) {
         return 1.0F;
      }

      float var2 = (float)(System.currentTimeMillis() - this.listShownAt) / 1000.0F - Math.min(index, 14) * 0.018F;
      float var3 = Math.max(0.0F, Math.min(1.0F, var2 / 0.18F));
      return 1.0F - (1.0F - var3) * (1.0F - var3) * (1.0F - var3);
   }

   private boolean isModulePinned(Module m) {
      return this.pinnedModules.contains(m);
   }

   private void drawModuleRow(Module mod, float rx, float ry, float colW, float alpha, int mx, int my, boolean inContent, boolean first, boolean last) {
      boolean var11 = inContent && mx >= rx && mx <= rx + colW && my >= ry && my <= ry + 26.0F;
      float var12 = this.hoverAnim.computeIfAbsent(mod, k -> 0.0F);
      float var13 = first ? 8.0F : 0.0F;
      float var14 = last ? 8.0F : 0.0F;
      if (var12 > 0.01F) {
         NanoVGRenderer.drawRoundedRectVarying(rx, ry, colW, 26.0F, var13, var13, var14, var14, SoftPalette.white(9.0F * var12 * alpha));
      }

      if (mod.isEnabled()) {
         Color var15;
         if (ThemeManager.isRainbowTheme()) {
            float var16 = (float)((System.currentTimeMillis() * 6.0E-4 + ry * 0.004F) % 1.0);
            if (var16 < 0.0F) {
               var16++;
            }

            int var17 = RainbowUtil.hsbToRgb(var16, 0.85F, 1.0F);
            var15 = new Color(var17 >> 16 & 0xFF, var17 >> 8 & 0xFF, var17 & 0xFF, SoftPalette.clamp(240.0F * alpha));
         } else {
            var15 = SoftPalette.mint(240.0F * alpha);
         }

         NanoVGRenderer.drawRoundedRect(rx + 2.0F, ry + 5.0F, 2.0F, 16.0F, 1.0F, var15);
      }

      float var47 = this.toggleAnim.getOrDefault(mod, mod.isEnabled() ? 1.0F : 0.0F);
      float var48 = 20.0F;
      float var49 = 11.0F;
      float var18 = rx + colW - var48 - 8.0F;
      float var19 = ry + (26.0F - var49) / 2.0F;
      SoftPalette.toggle(var18, var19, var48, var49, var47, alpha);
      float var20 = var18 - 6.0F;
      float var21 = 12.0F;
      float var22 = var18 - var21 - 5.0F;
      float var23 = ry + (26.0F - var21) / 2.0F;
      boolean var24 = true;
      boolean var25 = var24 && this.isModulePinned(mod);
      if (var24) {
         boolean var26 = var11 && mx >= var22 - 2.0F && mx <= var22 + var21 + 2.0F && my >= var23 - 2.0F && my <= var23 + var21 + 2.0F;
         if (var26) {
            NanoVGRenderer.drawRoundedRect(var22 - 2.0F, var23 - 2.0F, var21 + 4.0F, var21 + 4.0F, 3.0F, SoftPalette.white(14.0F * alpha));
         }

         String var27 = var25
            ? $$v_s(new byte[]{-98, -12, 112, -73, 2, -18, 40}, 4273728924488552654L)
            : $$v_s(new byte[]{-121, -13, 119, -69, 13}, 4273728924488552654L);
         IconManager.draw(var27, var22, var23, var21, var26 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.textDim(180.0F * alpha));
         var20 = var22 - 5.0F;
      }

      float var50 = rx + 10.0F;
      boolean var51 = hasConfigurableSettings(mod);
      float var28 = 14.0F;
      float var29 = var20 - var28;
      float var30 = ry + (26.0F - var28) / 2.0F;
      if (var51) {
         boolean var31 = var11 && mx >= var29 - 2.0F && mx <= var29 + var28 + 2.0F && my >= var30 - 2.0F && my <= var30 + var28 + 2.0F;
         if (var31) {
            NanoVGRenderer.drawRoundedRect(var29 - 1.5F, var30 - 1.5F, var28 + 3.0F, var28 + 3.0F, 4.0F, SoftPalette.white(20.0F * alpha));
         }

         IconManager.draw(
            $$v_s(new byte[]{-104, -1, 104, -86, 0, -27, 43, 96}, 4273728924488552654L),
            var29 + 2.5F,
            var30 + 2.5F,
            var28 - 5.0F,
            var31 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.textDim((170.0F + 70.0F * var12) * alpha)
         );
         var20 = var29 - 5.0F;
      }

      boolean var52 = this.listeningKeybindModule == mod;
      int var32 = mod.getKeybind();
      String var33 = var52 ? $$v_s(new byte[]{-59, -76, 50}, 4273728924488552654L) : (var32 != 0 && var32 != -1 ? keyLabel(var32) : null);
      float var34 = 0.0F;
      float var35 = 0.0F;
      float var36 = 0.0F;
      float var37 = 0.0F;
      if (var52) {
         String var38 = $$v_s(new byte[]{-69, -24, 121, -83, 26, -85, 39, 118, -6}, 4273728924488552654L);
         var36 = SoftPalette.widthBold(var38, 7.0F) + 10.0F;
         var37 = 14.0F;
         var34 = var20 - var36;
         var35 = ry + (26.0F - var37) / 2.0F;
         NanoVGRenderer.drawRoundedRect(var34, var35, var36, var37, 3.5F, SoftPalette.mint(35.0F * alpha));
         NanoVGRenderer.drawRoundedRectOutline(var34, var35, var36, var37, 3.5F, 1.0F, SoftPalette.mint(220.0F * alpha));
         SoftPalette.labelBold(var38, var34 + 5.0F, var35 + (var37 - 7.0F) / 2.0F, 7.0F, SoftPalette.mint(255.0F * alpha));
         var20 = var34 - 5.0F;
      } else if (var33 != null) {
         var36 = SoftPalette.width(var33, 7.0F) + 8.0F;
         var37 = 14.0F;
         var34 = var20 - var36;
         var35 = ry + (26.0F - var37) / 2.0F;
         boolean var53 = var11 && mx >= var34 && mx <= var34 + var36 && my >= var35 && my <= var35 + var37;
         NanoVGRenderer.drawRoundedRect(var34, var35, var36, var37, 3.5F, SoftPalette.white((var53 ? 20 : 12) * alpha));
         NanoVGRenderer.drawRoundedRectOutline(var34, var35, var36, var37, 3.5F, 1.0F, SoftPalette.white((var53 ? 35 : 18) * alpha));
         SoftPalette.label(
            var33, var34 + 4.0F, var35 + (var37 - 7.0F) / 2.0F, 7.0F, var53 ? SoftPalette.text(255.0F * alpha) : SoftPalette.text(225.0F * alpha)
         );
         var20 = var34 - 5.0F;
      } else if (var11) {
         String var54 = $$v_s(new byte[]{-118, -2, 120}, 4273728924488552654L);
         var36 = 14.0F;
         var37 = 14.0F;
         var34 = var20 - var36;
         var35 = ry + (26.0F - var37) / 2.0F;
         boolean var39 = mx >= var34 && mx <= var34 + var36 && my >= var35 && my <= var35 + var37;
         NanoVGRenderer.drawRoundedRect(var34, var35, var36, var37, 3.5F, SoftPalette.white((var39 ? 20 : 10) * alpha));
         NanoVGRenderer.drawRoundedRectOutline(var34, var35, var36, var37, 3.5F, 1.0F, SoftPalette.white((var39 ? 30 : 14) * alpha));
         IconManager.draw(var54, var34 + 2.5F, var35 + 2.5F, 9.0F, var39 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.textDim(200.0F * alpha));
         var20 = var34 - 5.0F;
      }

      if (!this.searchQuery.trim().isEmpty() || this.onlyEnabled) {
         IconManager.draw(categoryIcon(mod.getCategory()), var50, ry + 8.0F, 10.0F, SoftPalette.textDim(220.0F * alpha));
         var50 += 13.0F;
      }

      float var55 = ry + 8.25F;
      String var56 = SoftPalette.fit(mod.getDisplayName().toString(), Math.max(20.0F, var20 - var50), 9.5F);
      Color var40 = var25
         ? new Color(255, 230, 184, SoftPalette.clamp(255.0F * alpha))
         : (mod.isEnabled() ? SoftPalette.text(255.0F * alpha) : SoftPalette.text(190.0F * alpha));
      int var41 = this.searchQuery.isEmpty() ? -1 : var56.toLowerCase().indexOf(this.searchQuery.toLowerCase());
      if (var41 >= 0) {
         String var42 = var56.substring(0, var41);
         String var43 = var56.substring(var41, var41 + this.searchQuery.length());
         float var44 = var50;
         SoftPalette.label(var42, var44, var55, 9.5F, var40);
         var44 += SoftPalette.width(var42, 9.5F);
         SoftPalette.labelBold(var43, var44, var55, 9.5F, SoftPalette.mint(255.0F * alpha));
         var44 += SoftPalette.width(var43, 9.5F);
         SoftPalette.label(var56.substring(var41 + this.searchQuery.length()), var44, var55, 9.5F, var40);
      } else {
         SoftPalette.label(var56, var50, var55, 9.5F, var40);
      }

      DashboardView.ModuleRow var57 = new DashboardView.ModuleRow();
      var57.module = mod;
      var57.x = rx;
      var57.y = ry;
      var57.w = colW;
      var57.h = 26.0F;
      var57.gearX = var29;
      var57.gearY = var30;
      var57.gearS = var28;
      var57.kbX = var34;
      var57.kbY = var35;
      var57.kbW = var36;
      var57.kbH = var37;
      var57.starX = var22;
      var57.starY = var23;
      var57.starS = var21;
      var57.hasSettings = var51;
      var57.hovered = var11;
      this.visibleRows.add(var57);
      if (var11) {
         boolean var58 = var51 && mx >= var29 - 3.0F && mx <= var29 + var28 + 3.0F && my >= var30 - 3.0F && my <= var30 + var28 + 3.0F;
         boolean var61 = var36 > 0.0F && mx >= var34 && mx <= var34 + var36 && my >= var35 && my <= var35 + var37;
         boolean var45 = mx >= var22 - 2.0F && mx <= var22 + var21 + 2.0F && my >= var23 - 2.0F && my <= var23 + var21 + 2.0F;
         String var46 = mod.getDisplayName().toString();
         if (var45) {
            this.hint(
               "star:" + var46,
               var25
                  ? (
                     Localization.isVietnamese()
                        ? $$v_s(
                           new byte[]{-87, 123, -89, 81, 73, -20, 36, 122, -18, -126, -120, 53, -101, 61, 68, -81, 42, -85, 118, 9, -84}, 4273728924488552654L
                        )
                        : $$v_s(new byte[]{-66, -12, 108, -73, 7, -85, 42, 97, -20, -49, -60, -126, 94, 35}, 4273728924488552654L)
                  )
                  : (
                     Localization.isVietnamese()
                        ? $$v_s(new byte[]{-84, -14, 117, -77, 73, -25, -113, -71, -19, -126, 32, 103, -48, -23, -61, 30}, 4273728924488552654L)
                        : $$v_s(new byte[]{-69, -13, 114, -2, 29, -28, 108, 103, -20, -46}, 4273728924488552654L)
                  ),
               var22 - 20.0F,
               ry + 26.0F + 2.0F
            );
         } else if (var61) {
            this.hint(
               "kb:" + var46,
               var33 != null
                  ? (
                     Localization.isVietnamese()
                        ? "Phím tắt: " + var33 + " (Click để đổi, Chuột phải để xóa)"
                        : "Keybind: " + var33 + " (Click to bind, R-Click to clear)"
                  )
                  : (
                     Localization.isVietnamese()
                        ? $$v_s(
                           new byte[]{
                              -88, -10, 117, -67, 2, -85, -120, -126, 98, 25, 103, -42, 86, -112, -59, 5, -101, 58, -92, 109, 116, 22, 60, 55, -78, -88, 59, 50
                           },
                           4273728924488552654L
                        )
                        : $$v_s(
                           new byte[]{-88, -10, 117, -67, 2, -85, 56, 124, -93, -61, -105, -123, 88, 52, 10, 75, -48, 47, -75, -52, -80, 21, 120},
                           4273728924488552654L
                        )
                  ),
               var34 - 30.0F,
               ry + 26.0F + 2.0F
            );
         } else if (var58) {
            this.hint(
               "gear:" + var46,
               Localization.isVietnamese()
                  ? $$v_s(
                     new byte[]{-88, 89, -68, -73, 73, 79, -35, -14, 57, 21, -112, -42, 25, 16, 12, 30, 90, -15, 85, -38, -7, 11, 116, -94, -23, -79, -3, 111},
                     4273728924488552654L
                  )
                  : $$v_s(new byte[]{-72, -1, 104, -86, 0, -27, 43, 96, -93, -118, -74, -97, 86, 59, 16, 70, -40, 38, -91, -51, -78, 82}, 4273728924488552654L),
               var29 - 25.0F,
               ry + 26.0F + 2.0F
            );
         } else if (mod.getDescription() != null) {
            this.hint("mod:" + var46, mod.getDescription().toString(), rx + 10.0F, ry + 26.0F + 2.0F);
         }
      }
   }

   private void renderScrollbar(float sbX, float y, float h, float alpha) {
      if (!(this.maxScroll <= 0.0F)) {
         float var5 = Math.max(20.0F, h * (h / (h + this.maxScroll)));
         float var6 = y + this.scroll / this.maxScroll * (h - var5);
         NanoVGRenderer.drawRoundedRect(sbX, var6, 3.0F, var5, 1.5F, SoftPalette.white(45.0F * alpha));
      }
   }

   private void renderConfigGrid(float x, float y, float w, float h, float alpha, int mx, int my) {
      if (this.configList.isEmpty()) {
         this.refreshConfigs();
      }

      float var8 = 20.0F;
      this.tabProfilesW = 86.0F;
      this.tabProfilesH = var8;
      this.tabProfilesX = x;
      this.tabProfilesY = y;
      boolean var9 = this.configTab == DashboardView.ConfigTab.PROFILES;
      boolean var10 = hit(mx, my, this.tabProfilesX, this.tabProfilesY, this.tabProfilesW, this.tabProfilesH);
      if (var10) {
         this.hint("tabprofiles", "View local saved profiles", this.tabProfilesX, this.tabProfilesY + this.tabProfilesH + 3.0F);
      }

      NanoVGRenderer.drawRoundedRect(
         this.tabProfilesX,
         this.tabProfilesY,
         this.tabProfilesW,
         this.tabProfilesH,
         6.0F,
         var9 ? SoftPalette.mint(38.0F * alpha) : SoftPalette.white((var10 ? 18 : 8) * alpha)
      );
      NanoVGRenderer.drawRoundedRectOutline(
         this.tabProfilesX,
         this.tabProfilesY,
         this.tabProfilesW,
         this.tabProfilesH,
         6.0F,
         1.0F,
         var9 ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white((var10 ? 28 : 12) * alpha)
      );
      IconManager.draw(
         "config",
         this.tabProfilesX + 6.0F,
         this.tabProfilesY + 4.5F,
         11.0F,
         var9 ? SoftPalette.mint(255.0F * alpha) : (var10 ? SoftPalette.text(255.0F * alpha) : SoftPalette.textDim(200.0F * alpha))
      );
      SoftPalette.labelBold(
         "My Profiles",
         this.tabProfilesX + 21.0F,
         this.tabProfilesY + 5.0F,
         8.5F,
         var9 ? SoftPalette.mint(255.0F * alpha) : (var10 ? SoftPalette.text(255.0F * alpha) : SoftPalette.text(225.0F * alpha))
      );
      this.tabCommunityW = 100.0F;
      this.tabCommunityH = var8;
      this.tabCommunityX = this.tabProfilesX + this.tabProfilesW + 4.0F;
      this.tabCommunityY = y;
      boolean var11 = this.configTab == DashboardView.ConfigTab.COMMUNITY;
      boolean var12 = hit(mx, my, this.tabCommunityX, this.tabCommunityY, this.tabCommunityW, this.tabCommunityH);
      if (var12) {
         this.hint("tabcommunity", "Browse, like & download community setups", this.tabCommunityX, this.tabCommunityY + this.tabCommunityH + 3.0F);
      }

      NanoVGRenderer.drawRoundedRect(
         this.tabCommunityX,
         this.tabCommunityY,
         this.tabCommunityW,
         this.tabCommunityH,
         6.0F,
         var11 ? SoftPalette.mint(38.0F * alpha) : SoftPalette.white((var12 ? 18 : 8) * alpha)
      );
      NanoVGRenderer.drawRoundedRectOutline(
         this.tabCommunityX,
         this.tabCommunityY,
         this.tabCommunityW,
         this.tabCommunityH,
         6.0F,
         1.0F,
         var11 ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white((var12 ? 28 : 12) * alpha)
      );
      IconManager.draw(
         "download",
         this.tabCommunityX + 6.0F,
         this.tabCommunityY + 4.5F,
         11.0F,
         var11 ? SoftPalette.mint(255.0F * alpha) : (var12 ? SoftPalette.text(255.0F * alpha) : SoftPalette.textDim(200.0F * alpha))
      );
      SoftPalette.labelBold(
         "Community Hub",
         this.tabCommunityX + 21.0F,
         this.tabCommunityY + 5.0F,
         8.5F,
         var11 ? SoftPalette.mint(255.0F * alpha) : (var12 ? SoftPalette.text(255.0F * alpha) : SoftPalette.text(225.0F * alpha))
      );
      if (this.configTab == DashboardView.ConfigTab.PROFILES) {
         this.refreshBtnW = 20.0F;
         this.refreshBtnH = var8;
         this.refreshBtnX = x + w - this.refreshBtnW;
         this.refreshBtnY = y;
         boolean var13 = hit(mx, my, this.refreshBtnX, this.refreshBtnY, this.refreshBtnW, this.refreshBtnH);
         if (var13) {
            this.hint("cfgreload", "Reload profile list from disk", this.refreshBtnX - 40.0F, this.refreshBtnY + this.refreshBtnH + 3.0F);
         }

         NanoVGRenderer.drawRoundedRect(
            this.refreshBtnX, this.refreshBtnY, this.refreshBtnW, this.refreshBtnH, 5.0F, SoftPalette.white((var13 ? 20 : 10) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.refreshBtnX, this.refreshBtnY, this.refreshBtnW, this.refreshBtnH, 5.0F, 1.0F, SoftPalette.white((var13 ? 30 : 16) * alpha)
         );
         IconManager.draw(
            "reset", this.refreshBtnX + 4.5F, this.refreshBtnY + 4.5F, 11.0F, var13 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.textDim(200.0F * alpha)
         );
         this.folderBtnW = 50.0F;
         this.folderBtnH = var8;
         this.folderBtnX = this.refreshBtnX - this.folderBtnW - 4.0F;
         this.folderBtnY = y;
         boolean var14 = hit(mx, my, this.folderBtnX, this.folderBtnY, this.folderBtnW, this.folderBtnH);
         if (var14) {
            this.hint("cfgfolder", "Open configurations directory", this.folderBtnX, this.folderBtnY + this.folderBtnH + 3.0F);
         }

         NanoVGRenderer.drawRoundedRect(this.folderBtnX, this.folderBtnY, this.folderBtnW, this.folderBtnH, 5.0F, SoftPalette.white((var14 ? 20 : 10) * alpha));
         NanoVGRenderer.drawRoundedRectOutline(
            this.folderBtnX, this.folderBtnY, this.folderBtnW, this.folderBtnH, 5.0F, 1.0F, SoftPalette.white((var14 ? 30 : 16) * alpha)
         );
         IconManager.draw(
            "folder", this.folderBtnX + 5.5F, this.folderBtnY + 4.5F, 11.0F, var14 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(220.0F * alpha)
         );
         SoftPalette.label(
            "Folder", this.folderBtnX + 19.0F, this.folderBtnY + 4.5F, 8.0F, var14 ? SoftPalette.text(255.0F * alpha) : SoftPalette.text(225.0F * alpha)
         );
         boolean var15 = Localization.isVietnamese();
         float var16 = y + var8 + 4.0F;
         this.newCfgBtnX = x;
         this.newCfgBtnY = var16;
         this.newCfgBtnW = 76.0F;
         this.newCfgBtnH = var8;
         boolean var17 = hit(mx, my, this.newCfgBtnX, this.newCfgBtnY, this.newCfgBtnW, this.newCfgBtnH);
         if (var17) {
            this.hint(
               "newcfg", var15 ? "Tạo và lưu cấu hình mới" : "Create and save a new config profile", this.newCfgBtnX, this.newCfgBtnY + this.newCfgBtnH + 3.0F
            );
         }

         boolean var18 = this.namingNewConfig;
         NanoVGRenderer.drawRoundedRect(
            this.newCfgBtnX,
            this.newCfgBtnY,
            this.newCfgBtnW,
            this.newCfgBtnH,
            5.0F,
            var18 ? SoftPalette.mint(40.0F * alpha) : SoftPalette.white((var17 ? 20 : 10) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.newCfgBtnX,
            this.newCfgBtnY,
            this.newCfgBtnW,
            this.newCfgBtnH,
            5.0F,
            1.0F,
            var18 ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white((var17 ? 30 : 16) * alpha)
         );
         IconManager.draw(
            "add",
            this.newCfgBtnX + 6.0F,
            this.newCfgBtnY + 4.5F,
            11.0F,
            !var18 && !var17 ? SoftPalette.text(220.0F * alpha) : SoftPalette.mint(255.0F * alpha)
         );
         SoftPalette.labelBold(
            var15 ? "Tạo mới" : "New Profile",
            this.newCfgBtnX + 20.0F,
            this.newCfgBtnY + 4.5F,
            8.0F,
            !var18 && !var17 ? SoftPalette.text(235.0F * alpha) : SoftPalette.mint(255.0F * alpha)
         );
         this.shareTopBtnX = this.newCfgBtnX + this.newCfgBtnW + 4.0F;
         this.shareTopBtnY = var16;
         this.shareTopBtnW = 74.0F;
         this.shareTopBtnH = var8;
         boolean var19 = hit(mx, my, this.shareTopBtnX, this.shareTopBtnY, this.shareTopBtnW, this.shareTopBtnH);
         if (var19) {
            this.hint(
               "sharetop",
               var15 ? "Tải cài đặt lên cloud & lấy mã 6 ký tự" : "Upload current settings to cloud & generate 6-char code",
               this.shareTopBtnX - 10.0F,
               this.shareTopBtnY + this.shareTopBtnH + 3.0F
            );
         }

         NanoVGRenderer.drawRoundedRect(
            this.shareTopBtnX, this.shareTopBtnY, this.shareTopBtnW, this.shareTopBtnH, 5.0F, SoftPalette.white((var19 ? 20 : 10) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.shareTopBtnX, this.shareTopBtnY, this.shareTopBtnW, this.shareTopBtnH, 5.0F, 1.0F, SoftPalette.white((var19 ? 30 : 16) * alpha)
         );
         IconManager.draw(
            "config", this.shareTopBtnX + 6.0F, this.shareTopBtnY + 4.5F, 11.0F, var19 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(220.0F * alpha)
         );
         SoftPalette.labelBold(
            var15 ? "Chia sẻ" : "Share Cloud",
            this.shareTopBtnX + 20.0F,
            this.shareTopBtnY + 4.5F,
            8.0F,
            var19 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(235.0F * alpha)
         );
         this.importBtnX = this.shareTopBtnX + this.shareTopBtnW + 4.0F;
         this.importBtnY = var16;
         this.importBtnW = 74.0F;
         this.importBtnH = var8;
         boolean var20 = hit(mx, my, this.importBtnX, this.importBtnY, this.importBtnW, this.importBtnH);
         if (var20) {
            this.hint(
               "importbtn",
               var15 ? "Tải và áp dụng cấu hình từ mã cloud" : "Download and apply config from a 6-character code",
               this.importBtnX - 10.0F,
               this.importBtnY + this.importBtnH + 3.0F
            );
         }

         boolean var21 = this.importingCloudConfig;
         NanoVGRenderer.drawRoundedRect(
            this.importBtnX,
            this.importBtnY,
            this.importBtnW,
            this.importBtnH,
            5.0F,
            var21 ? SoftPalette.mint(40.0F * alpha) : SoftPalette.white((var20 ? 20 : 10) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.importBtnX,
            this.importBtnY,
            this.importBtnW,
            this.importBtnH,
            5.0F,
            1.0F,
            var21 ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white((var20 ? 30 : 16) * alpha)
         );
         IconManager.draw(
            "download",
            this.importBtnX + 6.0F,
            this.importBtnY + 4.5F,
            11.0F,
            !var21 && !var20 ? SoftPalette.text(220.0F * alpha) : SoftPalette.mint(255.0F * alpha)
         );
         SoftPalette.labelBold(
            var15 ? "Nhập mã" : "Import Code",
            this.importBtnX + 20.0F,
            this.importBtnY + 4.5F,
            8.0F,
            !var21 && !var20 ? SoftPalette.text(235.0F * alpha) : SoftPalette.mint(255.0F * alpha)
         );
         this.commPublishBtnX = this.importBtnX + this.importBtnW + 4.0F;
         this.commPublishBtnY = var16;
         this.commPublishBtnW = x + w - this.commPublishBtnX;
         this.commPublishBtnH = var8;
         boolean var22 = hit(mx, my, this.commPublishBtnX, this.commPublishBtnY, this.commPublishBtnW, this.commPublishBtnH);
         if (var22) {
            this.hint(
               "commpub",
               var15 ? "Đăng cấu hình lên Community Hub" : "Publish profile to community hub",
               this.commPublishBtnX - 20.0F,
               this.commPublishBtnY + this.commPublishBtnH + 3.0F
            );
         }

         NanoVGRenderer.drawRoundedRect(
            this.commPublishBtnX,
            this.commPublishBtnY,
            this.commPublishBtnW,
            this.commPublishBtnH,
            5.0F,
            this.publishingToCommunity ? SoftPalette.mint(40.0F * alpha) : SoftPalette.white((var22 ? 22 : 12) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.commPublishBtnX,
            this.commPublishBtnY,
            this.commPublishBtnW,
            this.commPublishBtnH,
            5.0F,
            1.0F,
            this.publishingToCommunity ? SoftPalette.mint(190.0F * alpha) : SoftPalette.mint((var22 ? 160 : 90) * alpha)
         );
         NVGIcons.cloud(this.commPublishBtnX + 9.0F, this.commPublishBtnY + 10.0F, 9.0F, SoftPalette.mint(240.0F * alpha));
         SoftPalette.labelBold(
            var15 ? "Đăng Hub" : "Publish Hub", this.commPublishBtnX + 18.0F, this.commPublishBtnY + 4.5F, 8.0F, SoftPalette.mint(240.0F * alpha)
         );
      } else {
         boolean var52 = Localization.isVietnamese();
         this.commRefreshW = 20.0F;
         this.commRefreshH = var8;
         this.commRefreshX = x + w - this.commRefreshW;
         this.commRefreshY = y;
         boolean var54 = hit(mx, my, this.commRefreshX, this.commRefreshY, this.commRefreshW, this.commRefreshH);
         if (var54) {
            this.hint(
               "commrefresh",
               var52 ? "Tải lại danh sách cộng đồng" : "Refresh community configs",
               this.commRefreshX - 40.0F,
               this.commRefreshY + this.commRefreshH + 3.0F
            );
         }

         NanoVGRenderer.drawRoundedRect(
            this.commRefreshX, this.commRefreshY, this.commRefreshW, this.commRefreshH, 5.0F, SoftPalette.white((var54 ? 20 : 10) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.commRefreshX, this.commRefreshY, this.commRefreshW, this.commRefreshH, 5.0F, 1.0F, SoftPalette.white((var54 ? 30 : 16) * alpha)
         );
         IconManager.draw(
            "reset",
            this.commRefreshX + 4.5F,
            this.commRefreshY + 4.5F,
            11.0F,
            !this.loadingCommunity && !var54 ? SoftPalette.textDim(200.0F * alpha) : SoftPalette.mint(255.0F * alpha)
         );
         this.sortDownloadsW = 34.0F;
         this.sortDownloadsH = var8;
         this.sortDownloadsX = this.commRefreshX - this.sortDownloadsW - 3.0F;
         this.sortDownloadsY = y;
         boolean var56 = "downloads".equals(this.communitySort);
         boolean var63 = hit(mx, my, this.sortDownloadsX, this.sortDownloadsY, this.sortDownloadsW, this.sortDownloadsH);
         if (var63) {
            this.hint(
               "sortdls",
               var52 ? "Sắp xếp theo lượt tải nhiều nhất" : "Sort by most downloads",
               this.sortDownloadsX,
               this.sortDownloadsY + this.sortDownloadsH + 3.0F
            );
         }

         NanoVGRenderer.drawRoundedRect(
            this.sortDownloadsX,
            this.sortDownloadsY,
            this.sortDownloadsW,
            this.sortDownloadsH,
            5.0F,
            var56 ? SoftPalette.mint(40.0F * alpha) : SoftPalette.white((var63 ? 20 : 8) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.sortDownloadsX,
            this.sortDownloadsY,
            this.sortDownloadsW,
            this.sortDownloadsH,
            5.0F,
            1.0F,
            var56 ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white((var63 ? 30 : 12) * alpha)
         );
         SoftPalette.label(
            var52 ? "Tải" : "DLs",
            this.sortDownloadsX + (var52 ? 11.0F : 9.0F),
            this.sortDownloadsY + 4.5F,
            7.5F,
            var56 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(220.0F * alpha)
         );
         this.sortNewW = 34.0F;
         this.sortNewH = var8;
         this.sortNewX = this.sortDownloadsX - this.sortNewW - 3.0F;
         this.sortNewY = y;
         boolean var70 = "newest".equals(this.communitySort);
         boolean var78 = hit(mx, my, this.sortNewX, this.sortNewY, this.sortNewW, this.sortNewH);
         if (var78) {
            this.hint("sortnew", var52 ? "Sắp xếp theo mới nhất" : "Sort by newest uploads", this.sortNewX, this.sortNewY + this.sortNewH + 3.0F);
         }

         NanoVGRenderer.drawRoundedRect(
            this.sortNewX,
            this.sortNewY,
            this.sortNewW,
            this.sortNewH,
            5.0F,
            var70 ? SoftPalette.mint(40.0F * alpha) : SoftPalette.white((var78 ? 20 : 8) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.sortNewX,
            this.sortNewY,
            this.sortNewW,
            this.sortNewH,
            5.0F,
            1.0F,
            var70 ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white((var78 ? 30 : 12) * alpha)
         );
         SoftPalette.label(
            var52 ? "Mới" : "New",
            this.sortNewX + (var52 ? 9.0F : 8.0F),
            this.sortNewY + 4.5F,
            7.5F,
            var70 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(220.0F * alpha)
         );
         this.sortTopW = 34.0F;
         this.sortTopH = var8;
         this.sortTopX = this.sortNewX - this.sortTopW - 3.0F;
         this.sortTopY = y;
         boolean var88 = "top".equals(this.communitySort);
         boolean var97 = hit(mx, my, this.sortTopX, this.sortTopY, this.sortTopW, this.sortTopH);
         if (var97) {
            this.hint("sorttop", var52 ? "Sắp xếp theo yêu thích nhất" : "Sort by top liked", this.sortTopX, this.sortTopY + this.sortTopH + 3.0F);
         }

         NanoVGRenderer.drawRoundedRect(
            this.sortTopX,
            this.sortTopY,
            this.sortTopW,
            this.sortTopH,
            5.0F,
            var88 ? SoftPalette.mint(40.0F * alpha) : SoftPalette.white((var97 ? 20 : 8) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.sortTopX,
            this.sortTopY,
            this.sortTopW,
            this.sortTopH,
            5.0F,
            1.0F,
            var88 ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white((var97 ? 30 : 12) * alpha)
         );
         SoftPalette.label("Top", this.sortTopX + 8.0F, this.sortTopY + 4.5F, 7.5F, var88 ? SoftPalette.mint(255.0F * alpha) : SoftPalette.text(220.0F * alpha));
         float var106 = y + var8 + 4.0F;
         this.commPublishBtnW = 76.0F;
         this.commPublishBtnH = var8;
         this.commPublishBtnX = x + w - this.commPublishBtnW;
         this.commPublishBtnY = var106;
         boolean var115 = hit(mx, my, this.commPublishBtnX, this.commPublishBtnY, this.commPublishBtnW, this.commPublishBtnH);
         if (var115) {
            this.hint("commpub", "Publish local profile to community hub", this.commPublishBtnX - 20.0F, this.commPublishBtnY + this.commPublishBtnH + 3.0F);
         }

         NanoVGRenderer.drawRoundedRect(
            this.commPublishBtnX,
            this.commPublishBtnY,
            this.commPublishBtnW,
            this.commPublishBtnH,
            5.0F,
            this.publishingToCommunity ? SoftPalette.mint(40.0F * alpha) : SoftPalette.white((var115 ? 22 : 12) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.commPublishBtnX,
            this.commPublishBtnY,
            this.commPublishBtnW,
            this.commPublishBtnH,
            5.0F,
            1.0F,
            this.publishingToCommunity ? SoftPalette.mint(190.0F * alpha) : SoftPalette.mint((var115 ? 160 : 90) * alpha)
         );
         IconManager.draw("add", this.commPublishBtnX + 6.0F, this.commPublishBtnY + 4.5F, 11.0F, SoftPalette.mint(240.0F * alpha));
         SoftPalette.labelBold("Share Setup", this.commPublishBtnX + 20.0F, this.commPublishBtnY + 4.5F, 8.0F, SoftPalette.mint(240.0F * alpha));
         this.commSearchX = x;
         this.commSearchY = var106;
         this.commSearchW = this.commPublishBtnX - x - 4.0F;
         this.commSearchH = var8;
         boolean var23 = hit(mx, my, this.commSearchX, this.commSearchY, this.commSearchW, this.commSearchH);
         NanoVGRenderer.drawRoundedRect(
            this.commSearchX,
            this.commSearchY,
            this.commSearchW,
            this.commSearchH,
            5.0F,
            SoftPalette.white((!var23 && !this.communitySearchFocused ? 9 : 18) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.commSearchX,
            this.commSearchY,
            this.commSearchW,
            this.commSearchH,
            5.0F,
            1.0F,
            this.communitySearchFocused ? SoftPalette.mint(180.0F * alpha) : SoftPalette.white((var23 ? 28 : 12) * alpha)
         );
         IconManager.draw(
            "search",
            this.commSearchX + 6.0F,
            this.commSearchY + 4.5F,
            10.0F,
            this.communitySearchFocused ? SoftPalette.mint(240.0F * alpha) : SoftPalette.textDim(200.0F * alpha)
         );
         float var24 = this.commSearchX + 21.0F;
         String var25 = this.communitySearch.isEmpty()
            ? (var52 ? "Tìm kiếm config cộng đồng (tên, tác giả, tag)..." : "Search community configs (title, author, tags)...")
            : this.communitySearch;
         if (this.communitySearchSelectedAll && !this.communitySearch.isEmpty()) {
            float var132 = SoftPalette.width(var25, 8.0F);
            NanoVGRenderer.drawRoundedRect(
               var24 - 1.0F, this.commSearchY + 3.5F, var132 + 2.0F, this.commSearchH - 7.0F, 2.0F, SoftPalette.mint(120.0F * alpha)
            );
            SoftPalette.label(var25, var24, this.commSearchY + 5.0F, 8.0F, SoftPalette.white(255.0F * alpha));
         } else {
            SoftPalette.label(
               var25,
               var24,
               this.commSearchY + 5.0F,
               8.0F,
               this.communitySearch.isEmpty() ? SoftPalette.textDim(160.0F * alpha) : SoftPalette.text(255.0F * alpha)
            );
            if (this.communitySearchFocused && System.currentTimeMillis() / 450L % 2L == 0L) {
               float var26 = this.communitySearch.isEmpty() ? 0.0F : SoftPalette.width(this.communitySearch, 8.0F);
               NanoVGRenderer.drawRect(var24 + var26 + 1.0F, this.commSearchY + 4.5F, 1.2F, 11.0F, SoftPalette.mint(240.0F * alpha));
            }
         }
      }

      float var53 = y + var8 + 26.0F;
      float var55 = h - var8 - 26.0F;
      if (this.publishingToCommunity) {
         boolean var57 = Localization.isVietnamese();
         float var64 = 76.0F;
         this.pubCardX = x;
         this.pubCardY = var53;
         this.pubCardW = w;
         this.pubCardH = var64;
         NanoVGRenderer.drawRoundedRect(this.pubCardX, this.pubCardY, this.pubCardW, this.pubCardH, 7.0F, SoftPalette.white(14.0F * alpha));
         NanoVGRenderer.drawRoundedRectOutline(this.pubCardX, this.pubCardY, this.pubCardW, this.pubCardH, 7.0F, 1.0F, SoftPalette.mint(180.0F * alpha));
         NVGIcons.cloud(x + 12.0F, var53 + 11.0F, 10.0F, SoftPalette.mint(255.0F * alpha));
         SoftPalette.labelBold(
            var57 ? "Đăng cấu hình lên Community Hub" : "Publish Profile to Community Hub", x + 22.0F, var53 + 6.0F, 8.5F, SoftPalette.mint(255.0F * alpha)
         );
         String var71 = (var57 ? "Cấu hình: [" : "Profile: [") + (this.publishProfileName.isEmpty() ? "Current" : this.publishProfileName) + "]";
         SoftPalette.label(var71, x + 185.0F, var53 + 6.5F, 7.5F, SoftPalette.textDim(200.0F * alpha));
         this.pubTitleX = x + 8.0F;
         this.pubTitleY = var53 + 20.0F;
         this.pubTitleW = (w - 20.0F) * 0.45F;
         this.pubTitleH = 18.0F;
         NanoVGRenderer.drawRoundedRect(
            this.pubTitleX, this.pubTitleY, this.pubTitleW, this.pubTitleH, 4.0F, SoftPalette.white((this.publishFocusedField == 0 ? 20 : 10) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.pubTitleX,
            this.pubTitleY,
            this.pubTitleW,
            this.pubTitleH,
            4.0F,
            1.0F,
            this.publishFocusedField == 0 ? SoftPalette.mint(190.0F * alpha) : SoftPalette.white(16.0F * alpha)
         );
         String var79 = this.publishTitle.isEmpty() ? (var57 ? "Nhập tiêu đề..." : "Enter title...") : this.publishTitle;
         SoftPalette.label(
            var79,
            this.pubTitleX + 6.0F,
            this.pubTitleY + 4.0F,
            7.5F,
            this.publishTitle.isEmpty() ? SoftPalette.textDim(160.0F * alpha) : SoftPalette.text(245.0F * alpha)
         );
         this.pubDescX = this.pubTitleX + this.pubTitleW + 4.0F;
         this.pubDescY = var53 + 20.0F;
         this.pubDescW = (w - 20.0F) * 0.55F;
         this.pubDescH = 18.0F;
         NanoVGRenderer.drawRoundedRect(
            this.pubDescX, this.pubDescY, this.pubDescW, this.pubDescH, 4.0F, SoftPalette.white((this.publishFocusedField == 1 ? 20 : 10) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.pubDescX,
            this.pubDescY,
            this.pubDescW,
            this.pubDescH,
            4.0F,
            1.0F,
            this.publishFocusedField == 1 ? SoftPalette.mint(190.0F * alpha) : SoftPalette.white(16.0F * alpha)
         );
         String var89 = this.publishDesc.isEmpty() ? (var57 ? "Mô tả ngắn (vd: crystal, cự ly...)" : "Description (crystal, reach...)") : this.publishDesc;
         SoftPalette.label(
            var89,
            this.pubDescX + 6.0F,
            this.pubDescY + 4.0F,
            7.5F,
            this.publishDesc.isEmpty() ? SoftPalette.textDim(160.0F * alpha) : SoftPalette.text(245.0F * alpha)
         );
         this.pubTagsX = x + 8.0F;
         this.pubTagsY = var53 + 42.0F;
         this.pubTagsW = w - 124.0F;
         this.pubTagsH = 18.0F;
         NanoVGRenderer.drawRoundedRect(
            this.pubTagsX, this.pubTagsY, this.pubTagsW, this.pubTagsH, 4.0F, SoftPalette.white((this.publishFocusedField == 2 ? 20 : 10) * alpha)
         );
         NanoVGRenderer.drawRoundedRectOutline(
            this.pubTagsX,
            this.pubTagsY,
            this.pubTagsW,
            this.pubTagsH,
            4.0F,
            1.0F,
            this.publishFocusedField == 2 ? SoftPalette.mint(190.0F * alpha) : SoftPalette.white(16.0F * alpha)
         );
         String var98 = this.publishTags.isEmpty() ? (var57 ? "Thẻ: cpvp, crystal, smp..." : "Tags: cpvp, crystal, ranked...") : this.publishTags;
         SoftPalette.label(
            var98,
            this.pubTagsX + 6.0F,
            this.pubTagsY + 4.0F,
            7.5F,
            this.publishTags.isEmpty() ? SoftPalette.textDim(160.0F * alpha) : SoftPalette.text(245.0F * alpha)
         );
         this.pubCancelW = 48.0F;
         this.pubCancelH = 18.0F;
         this.pubCancelX = x + w - this.pubCancelW - 8.0F;
         this.pubCancelY = var53 + 42.0F;
         boolean var107 = hit(mx, my, this.pubCancelX, this.pubCancelY, this.pubCancelW, this.pubCancelH);
         NanoVGRenderer.drawRoundedRect(this.pubCancelX, this.pubCancelY, this.pubCancelW, this.pubCancelH, 4.0F, SoftPalette.white((var107 ? 24 : 12) * alpha));
         SoftPalette.label(var57 ? "Hủy" : "Cancel", this.pubCancelX + 11.0F, this.pubCancelY + 4.5F, 7.5F, SoftPalette.text(230.0F * alpha));
         this.pubConfirmW = 54.0F;
         this.pubConfirmH = 18.0F;
         this.pubConfirmX = this.pubCancelX - this.pubConfirmW - 4.0F;
         this.pubConfirmY = var53 + 42.0F;
         boolean var116 = hit(mx, my, this.pubConfirmX, this.pubConfirmY, this.pubConfirmW, this.pubConfirmH);
         NanoVGRenderer.drawRoundedRect(
            this.pubConfirmX, this.pubConfirmY, this.pubConfirmW, this.pubConfirmH, 4.0F, SoftPalette.mint((var116 ? 210 : 160) * alpha)
         );
         SoftPalette.labelBold(
            var57 ? "Đăng" : "Publish", this.pubConfirmX + 10.0F, this.pubConfirmY + 4.5F, 7.5F, new Color(14, 30, 24, SoftPalette.clamp(255.0F * alpha))
         );
         var53 += var64 + 6.0F;
         var55 -= var64 + 6.0F;
      }

      if (this.configTab == DashboardView.ConfigTab.COMMUNITY) {
         boolean var62 = Localization.isVietnamese();
         if (this.communityConfigs.isEmpty() && !this.loadingCommunity && System.currentTimeMillis() - this.lastCommunityFetchTime > 3000L) {
            this.refreshCommunityConfigs();
         }

         int var69 = this.communityConfigs.size();
         float var77 = w;
         float var87 = 46.0F;
         float var96 = 6.0F;
         this.maxScroll = Math.max(0.0F, var69 * (var87 + var96) - var55);
         this.visibleCommunityRows.clear();
         if (this.loadingCommunity && var69 == 0) {
            float var105 = 76.0F;
            float var114 = var53 + 16.0F;
            NanoVGRenderer.drawRoundedRect(x, var114, w, var105, 8.0F, SoftPalette.white(6.0F * alpha));
            NanoVGRenderer.drawRoundedRectOutline(x, var114, w, var105, 8.0F, 1.0F, SoftPalette.white(10.0F * alpha));
            IconManager.draw("download", x + (w - 18.0F) / 2.0F, var114 + 14.0F, 18.0F, SoftPalette.mint(220.0F * alpha));
            String var121 = var62 ? "Đang tải cấu hình cộng đồng..." : "Loading community configurations...";
            SoftPalette.labelBold(var121, x + (w - SoftPalette.widthBold(var121, 9.0F)) / 2.0F, var114 + 38.0F, 9.0F, SoftPalette.text(225.0F * alpha));
            String var126 = var62 ? "Đang kết nối tới Vie Services Community Hub" : "Connecting to Vie Services Community Hub";
            SoftPalette.label(var126, x + (w - SoftPalette.width(var126, 7.5F)) / 2.0F, var114 + 52.0F, 7.5F, SoftPalette.textDim(160.0F * alpha));
         } else if (var69 == 0) {
            float var104 = 76.0F;
            float var113 = var53 + 16.0F;
            NanoVGRenderer.drawRoundedRect(x, var113, w, var104, 8.0F, SoftPalette.white(6.0F * alpha));
            NanoVGRenderer.drawRoundedRectOutline(x, var113, w, var104, 8.0F, 1.0F, SoftPalette.white(10.0F * alpha));
            IconManager.draw("config", x + (w - 18.0F) / 2.0F, var113 + 14.0F, 18.0F, SoftPalette.textDim(160.0F * alpha));
            String var120 = this.communitySearch.isEmpty()
               ? (var62 ? "Chưa có cấu hình cộng đồng nào" : "No community configs found")
               : (var62 ? "Không có kết quả cho \"" + this.communitySearch + "\"" : "No results for \"" + this.communitySearch + "\"");
            SoftPalette.labelBold(var120, x + (w - SoftPalette.widthBold(var120, 9.0F)) / 2.0F, var113 + 38.0F, 9.0F, SoftPalette.text(225.0F * alpha));
            String var125 = var62
               ? "Bấm 'Đăng Hub' ở trên để chia sẻ cấu hình tốt nhất của bạn!"
               : "Click 'Share Setup' above to share your best config with the community!";
            SoftPalette.label(var125, x + (w - SoftPalette.width(var125, 7.5F)) / 2.0F, var113 + 52.0F, 7.5F, SoftPalette.textDim(160.0F * alpha));
         } else {
            NanoVGRenderer.scissor(x - 2.0F, var53, w + 4.0F, var55);
            boolean var103 = mx >= x && mx <= x + w && my >= var53 && my <= var53 + var55;

            for (int var112 = 0; var112 < var69; var112++) {
               ConfigSyncManager.CommunityConfig var119 = (ConfigSyncManager.CommunityConfig)this.communityConfigs.get(var112);
               float var124 = x;
               float var129 = var53 + var112 * (var87 + var96) - this.scroll;
               if (!(var129 + var87 < var53) && !(var129 > var53 + var55)) {
                  float var131 = this.rowEntrance(var112);
                  if (var131 < 0.999F) {
                     NanoVGRenderer.save();
                     NanoVGRenderer.translate(0.0F, (1.0F - var131) * 4.0F);
                  }

                  boolean var134 = var103 && hit(mx, my, var124, var129, var77, var87);
                  NanoVGRenderer.drawRoundedRect(var124, var129, var77, var87, 8.0F, SoftPalette.white((var134 ? 14 : 7) * alpha * var131));
                  NanoVGRenderer.drawRoundedRectOutline(
                     var124,
                     var129,
                     var77,
                     var87,
                     8.0F,
                     1.0F,
                     var119.isOwner() ? SoftPalette.mint((var134 ? 180 : 100) * alpha * var131) : SoftPalette.white((var134 ? 26 : 11) * alpha * var131)
                  );
                  float var135 = 24.0F;
                  float var136 = var124 + 8.0F;
                  float var137 = var129 + (var87 - var135) / 2.0F;
                  NanoVGRenderer.drawRoundedRect(var136, var137, var135, var135, 6.0F, SoftPalette.mint((var119.isOwner() ? 40 : 20) * alpha * var131));
                  NanoVGRenderer.drawRoundedRectOutline(
                     var136, var137, var135, var135, 6.0F, 1.0F, SoftPalette.mint((var119.isOwner() ? 150 : 80) * alpha * var131)
                  );
                  IconManager.draw("config", var136 + 6.5F, var137 + 6.5F, 11.0F, SoftPalette.mint(240.0F * alpha * var131));
                  DashboardView.CommunityRow var138 = new DashboardView.CommunityRow();
                  var138.config = var119;
                  var138.hovered = var134;
                  var138.x = var124;
                  var138.y = var129;
                  var138.w = var77;
                  var138.h = var87;
                  float var139 = var124 + var77 - 8.0F;
                  float var146 = 20.0F;
                  float var147 = var129 + (var87 - var146) / 2.0F;
                  if (var119.isOwner()) {
                     var138.deleteW = 32.0F;
                     var138.deleteH = var146;
                     var139 -= var138.deleteW;
                     var138.deleteX = var139;
                     var138.deleteY = var147;
                     boolean var148 = hit(mx, my, var138.deleteX, var138.deleteY, var138.deleteW, var138.deleteH);
                     NanoVGRenderer.drawRoundedRect(
                        var138.deleteX,
                        var138.deleteY,
                        var138.deleteW,
                        var138.deleteH,
                        5.0F,
                        new Color(235, 69, 58, SoftPalette.clamp((var148 ? 220 : 140) * alpha * var131))
                     );
                     SoftPalette.labelBold(var62 ? "Xóa" : "Del", var138.deleteX + (var62 ? 8.0F : 8.0F), var138.deleteY + 5.5F, 7.5F, Color.WHITE);
                     if (var148) {
                        this.hint(
                           "commdel_" + var112,
                           var62 ? "Gỡ bài đăng khỏi Community Hub" : "Remove post from Community Hub",
                           var138.deleteX - 50.0F,
                           var147 + var146 + 4.0F
                        );
                     }

                     var139 -= 6.0F;
                  }

                  var138.downloadW = 42.0F;
                  var138.downloadH = var146;
                  var139 -= var138.downloadW;
                  var138.downloadX = var139;
                  var138.downloadY = var147;
                  boolean var149 = hit(mx, my, var138.downloadX, var138.downloadY, var138.downloadW, var138.downloadH);
                  NanoVGRenderer.drawRoundedRect(
                     var138.downloadX, var138.downloadY, var138.downloadW, var138.downloadH, 5.0F, SoftPalette.mint((var149 ? 210 : 160) * alpha * var131)
                  );
                  SoftPalette.labelBold(
                     var62 ? "Tải" : "Get",
                     var138.downloadX + (var62 ? 13.0F : 13.0F),
                     var138.downloadY + 5.5F,
                     8.0F,
                     new Color(14, 30, 24, SoftPalette.clamp(255.0F * alpha * var131))
                  );
                  if (var149) {
                     this.hint(
                        "commdl_" + var112,
                        var62 ? "Tải & áp dụng: " + var119.getTitle() : "Download & apply setup '" + var119.getTitle() + "'",
                        var138.downloadX - 60.0F,
                        var147 + var146 + 4.0F
                     );
                  }

                  var139 -= 6.0F;
                  var138.likeW = 38.0F;
                  var138.likeH = var146;
                  var139 -= var138.likeW;
                  var138.likeX = var139;
                  var138.likeY = var147;
                  boolean var150 = hit(mx, my, var138.likeX, var138.likeY, var138.likeW, var138.likeH);
                  boolean var151 = var119.hasLiked();
                  if (var151) {
                     NanoVGRenderer.drawRoundedRect(
                        var138.likeX,
                        var138.likeY,
                        var138.likeW,
                        var138.likeH,
                        5.0F,
                        new Color(245, 60, 100, SoftPalette.clamp((var150 ? 60 : 40) * alpha * var131))
                     );
                     NanoVGRenderer.drawRoundedRectOutline(
                        var138.likeX,
                        var138.likeY,
                        var138.likeW,
                        var138.likeH,
                        5.0F,
                        1.0F,
                        new Color(245, 80, 110, SoftPalette.clamp((var150 ? 200 : 140) * alpha * var131))
                     );
                     NVGIcons.heart(var138.likeX + 9.0F, var138.likeY + 10.0F, 4.2F, new Color(245, 75, 105, SoftPalette.clamp(255.0F * alpha * var131)));
                     SoftPalette.labelBold(
                        String.valueOf(var119.getLikesCount()),
                        var138.likeX + 17.0F,
                        var138.likeY + 5.5F,
                        7.5F,
                        new Color(255, 130, 150, SoftPalette.clamp(255.0F * alpha * var131))
                     );
                  } else {
                     NanoVGRenderer.drawRoundedRect(
                        var138.likeX, var138.likeY, var138.likeW, var138.likeH, 5.0F, SoftPalette.white((var150 ? 22 : 10) * alpha * var131)
                     );
                     NanoVGRenderer.drawRoundedRectOutline(
                        var138.likeX, var138.likeY, var138.likeW, var138.likeH, 5.0F, 1.0F, SoftPalette.white((var150 ? 32 : 14) * alpha * var131)
                     );
                     NVGIcons.heart(
                        var138.likeX + 9.0F,
                        var138.likeY + 10.0F,
                        4.2F,
                        var150 ? new Color(245, 100, 130, SoftPalette.clamp(220.0F * alpha * var131)) : SoftPalette.textDim(180.0F * alpha * var131)
                     );
                     SoftPalette.label(
                        String.valueOf(var119.getLikesCount()),
                        var138.likeX + 17.0F,
                        var138.likeY + 5.5F,
                        7.5F,
                        var150 ? SoftPalette.text(255.0F * alpha * var131) : SoftPalette.text(220.0F * alpha * var131)
                     );
                  }

                  if (var150) {
                     this.hint(
                        "commlk_" + var112,
                        var151 ? (var62 ? "Bỏ thích" : "Unlike setup") : (var62 ? "Thích cấu hình" : "Like setup"),
                        var138.likeX - 15.0F,
                        var147 + var146 + 4.0F
                     );
                  }

                  var139 -= 6.0F;
                  float var165 = 34.0F;
                  var139 -= var165;
                  float var166 = var139;
                  IconManager.draw("download", var166 + 2.0F, var147 + 4.5F, 10.0F, SoftPalette.textDim(180.0F * alpha * var131));
                  SoftPalette.label(
                     String.valueOf(var119.getDownloadsCount()), var166 + 14.0F, var147 + 5.5F, 7.5F, SoftPalette.textDim(180.0F * alpha * var131)
                  );
                  float var167 = var136 + var135 + 8.0F;
                  float var170 = Math.max(10.0F, var139 - var167 - 8.0F);
                  String var173 = SoftPalette.fit(var119.getTitle(), var170 - 55.0F, 9.5F);
                  SoftPalette.labelBold(var173, var167, var129 + 6.0F, 9.5F, SoftPalette.mint(255.0F * alpha * var131));
                  float var176 = SoftPalette.widthBold(var173, 9.5F);
                  float var180 = var167 + var176 + 5.0F;
                  NanoVGRenderer.drawRoundedRect(var180, var129 + 6.0F, 34.0F, 12.0F, 3.0F, SoftPalette.white(14.0F * alpha * var131));
                  NanoVGRenderer.drawRoundedRectOutline(var180, var129 + 6.0F, 34.0F, 12.0F, 3.0F, 1.0F, SoftPalette.mint(120.0F * alpha * var131));
                  SoftPalette.labelBold(var119.getCode(), var180 + 4.0F, var129 + 7.5F, 6.5F, SoftPalette.mint(240.0F * alpha * var131));
                  if (var119.isOwner()) {
                     float var186 = var180 + 38.0F;
                     NanoVGRenderer.drawRoundedRect(var186, var129 + 6.0F, 26.0F, 12.0F, 3.0F, SoftPalette.mint(50.0F * alpha * var131));
                     SoftPalette.labelBold("YOU", var186 + 4.5F, var129 + 7.5F, 6.5F, SoftPalette.mint(255.0F * alpha * var131));
                  }

                  String var187 = "@" + var119.getAuthorDiscordName();
                  SoftPalette.labelBold(var187, var167, var129 + 20.0F, 7.5F, new Color(130, 210, 255, SoftPalette.clamp(245.0F * alpha * var131)));
                  float var190 = SoftPalette.widthBold(var187, 7.5F);
                  SoftPalette.label("•", var167 + var190 + 4.0F, var129 + 20.0F, 7.5F, SoftPalette.textDim(140.0F * alpha * var131));
                  String var191 = var119.getDescription();
                  if (var191.isEmpty() && !var119.getTags().isEmpty()) {
                     var191 = String.join(" ", var119.getTags().stream().map(t -> "#" + t).toList());
                  }

                  if (var191.isEmpty()) {
                     var191 = var119.getName() + ".json";
                  }

                  float var192 = var167 + var190 + 11.0F;
                  float var193 = Math.max(10.0F, var139 - var192 - 6.0F);
                  SoftPalette.label(SoftPalette.fit(var191, var193, 7.0F), var192, var129 + 20.0F, 7.0F, SoftPalette.textDim(180.0F * alpha * var131));
                  String var194 = var119.getTags().isEmpty() ? "" : String.join(" ", var119.getTags().stream().map(t -> "#" + t).toList());
                  String var50 = var119.getCreatedAt().length() >= 10 ? var119.getCreatedAt().substring(0, 10) : "";
                  String var51 = !var194.isEmpty() ? var194 + (!var50.isEmpty() ? " • " + var50 : "") : (!var50.isEmpty() ? var50 : var119.getName() + ".json");
                  SoftPalette.label(SoftPalette.fit(var51, var170, 6.5F), var167, var129 + 32.0F, 6.5F, SoftPalette.textDim(150.0F * alpha * var131));
                  this.visibleCommunityRows.add(var138);
                  if (var131 < 0.999F) {
                     NanoVGRenderer.restore();
                  }
               }
            }

            if (this.scroll > 1.0F) {
               NanoVGRenderer.drawVerticalGradientRect(
                  x - 2.0F, var53, w + 4.0F, 12.0F, SoftPalette.rgba(SoftPalette.BODY_TOP, 200.0F * alpha), SoftPalette.rgba(SoftPalette.BODY_TOP, 0.0F)
               );
            }

            NanoVGRenderer.resetScissor();
            this.renderScrollbar(x + w + 3.0F, var53, var55, alpha);
         }
      } else {
         if (this.showSharePopup && this.lastSharedCode != null && !this.lastSharedCode.isEmpty()) {
            boolean var58 = Localization.isVietnamese();
            float var65 = 26.0F;
            NanoVGRenderer.drawRoundedRect(x, var53, w, var65, 6.0F, SoftPalette.white(14.0F * alpha));
            NanoVGRenderer.drawRoundedRectOutline(x, var53, w, var65, 6.0F, 1.0F, SoftPalette.mint(170.0F * alpha));
            IconManager.draw("config", x + 8.0F, var53 + 7.0F, 12.0F, SoftPalette.mint(255.0F * alpha));
            SoftPalette.label(var58 ? "Mã chia sẻ:" : "Share Code:", x + 24.0F, var53 + 8.0F, 8.5F, SoftPalette.textDim(220.0F * alpha));
            float var72 = x + (var58 ? 80.0F : 86.0F);
            SoftPalette.labelBold(this.lastSharedCode, var72, var53 + 7.0F, 11.0F, SoftPalette.mint(255.0F * alpha));
            float var80 = var72 + SoftPalette.widthBold(this.lastSharedCode, 11.0F) + 12.0F;
            String var90 = this.copiedCodeNotice
               ? (var58 ? "Đã sao chép vào bộ nhớ tạm!" : "Copied to clipboard!")
               : (var58 ? "Chia sẻ mã này cho người chơi khác" : "Share code with other players");
            SoftPalette.label(var90, var80, var53 + 8.0F, 7.5F, this.copiedCodeNotice ? SoftPalette.mint(230.0F * alpha) : SoftPalette.textDim(180.0F * alpha));
            this.shareCloseBtnW = 18.0F;
            this.shareCloseBtnH = 18.0F;
            this.shareCloseBtnX = x + w - this.shareCloseBtnW - 5.0F;
            this.shareCloseBtnY = var53 + 4.0F;
            this.shareCopyBtnW = 48.0F;
            this.shareCopyBtnH = 18.0F;
            this.shareCopyBtnX = this.shareCloseBtnX - this.shareCopyBtnW - 5.0F;
            this.shareCopyBtnY = var53 + 4.0F;
            boolean var99 = hit(mx, my, this.shareCopyBtnX, this.shareCopyBtnY, this.shareCopyBtnW, this.shareCopyBtnH);
            NanoVGRenderer.drawRoundedRect(
               this.shareCopyBtnX, this.shareCopyBtnY, this.shareCopyBtnW, this.shareCopyBtnH, 4.0F, SoftPalette.white((var99 ? 25 : 14) * alpha)
            );
            NanoVGRenderer.drawRoundedRectOutline(
               this.shareCopyBtnX, this.shareCopyBtnY, this.shareCopyBtnW, this.shareCopyBtnH, 4.0F, 1.0F, SoftPalette.white(24.0F * alpha)
            );
            SoftPalette.label(
               this.copiedCodeNotice ? (var58 ? "Đã chép!" : "Copied!") : (var58 ? "Sao chép" : "Copy"),
               this.shareCopyBtnX + (this.copiedCodeNotice ? 6.0F : 8.0F),
               this.shareCopyBtnY + 4.5F,
               7.5F,
               SoftPalette.text(245.0F * alpha)
            );
            boolean var108 = hit(mx, my, this.shareCloseBtnX, this.shareCloseBtnY, this.shareCloseBtnW, this.shareCloseBtnH);
            if (var108) {
               NanoVGRenderer.drawRoundedRect(
                  this.shareCloseBtnX, this.shareCloseBtnY, this.shareCloseBtnW, this.shareCloseBtnH, 4.0F, SoftPalette.white(20.0F * alpha)
               );
            }

            NVGIcons.cross(
               this.shareCloseBtnX + 9.0F,
               this.shareCloseBtnY + 9.0F,
               3.5F,
               1.1F,
               var108 ? SoftPalette.whiteInt(255.0F * alpha) : SoftPalette.textDimInt(200.0F * alpha)
            );
            var53 += var65 + 6.0F;
            var55 -= var65 + 6.0F;
         }

         if (this.importingCloudConfig) {
            float var59 = 26.0F;
            NanoVGRenderer.drawRoundedRect(x, var53, w, var59, 6.0F, SoftPalette.white(12.0F * alpha));
            NanoVGRenderer.drawRoundedRectOutline(x, var53, w, var59, 6.0F, 1.0F, SoftPalette.mint(180.0F * alpha));
            IconManager.draw("download", x + 8.0F, var53 + 7.0F, 12.0F, SoftPalette.mint(240.0F * alpha));
            float var66 = x + 26.0F;
            String var73 = this.importCodeText.isEmpty() ? "Enter 6-char share code..." : this.importCodeText;
            if (this.importCodeSelectedAll && !this.importCodeText.isEmpty()) {
               float var82 = SoftPalette.width(var73, 9.0F);
               NanoVGRenderer.drawRoundedRect(var66 - 2.0F, var53 + 5.0F, var82 + 4.0F, var59 - 10.0F, 2.0F, SoftPalette.mint(120.0F * alpha));
               SoftPalette.label(var73, var66, var53 + 7.5F, 9.0F, SoftPalette.white(255.0F * alpha));
            } else {
               SoftPalette.label(
                  var73, var66, var53 + 7.5F, 9.0F, this.importCodeText.isEmpty() ? SoftPalette.textDim(170.0F * alpha) : SoftPalette.text(255.0F * alpha)
               );
               if (System.currentTimeMillis() / 450L % 2L == 0L) {
                  float var81 = this.importCodeText.isEmpty() ? 0.0F : SoftPalette.width(this.importCodeText, 9.0F);
                  NanoVGRenderer.drawRect(var66 + var81 + 1.0F, var53 + 6.0F, 1.2F, 14.0F, SoftPalette.mint(240.0F * alpha));
               }
            }

            boolean var83 = Localization.isVietnamese();
            this.cancelImportW = 44.0F;
            this.cancelImportH = 18.0F;
            this.doImportW = 50.0F;
            this.doImportH = 18.0F;
            this.pasteImportW = 44.0F;
            this.pasteImportH = 18.0F;
            this.cancelImportX = x + w - this.cancelImportW - 5.0F;
            this.cancelImportY = var53 + 4.0F;
            this.doImportX = this.cancelImportX - this.doImportW - 5.0F;
            this.doImportY = var53 + 4.0F;
            this.pasteImportX = this.doImportX - this.pasteImportW - 5.0F;
            this.pasteImportY = var53 + 4.0F;
            boolean var91 = hit(mx, my, this.pasteImportX, this.pasteImportY, this.pasteImportW, this.pasteImportH);
            NanoVGRenderer.drawRoundedRect(
               this.pasteImportX, this.pasteImportY, this.pasteImportW, this.pasteImportH, 5.0F, SoftPalette.white((var91 ? 22 : 12) * alpha)
            );
            NanoVGRenderer.drawRoundedRectOutline(
               this.pasteImportX, this.pasteImportY, this.pasteImportW, this.pasteImportH, 5.0F, 1.0F, SoftPalette.white(20.0F * alpha)
            );
            SoftPalette.label(
               var83 ? "Dán" : "Paste", this.pasteImportX + (var83 ? 12.0F : 9.0F), this.pasteImportY + 4.5F, 8.0F, SoftPalette.text(235.0F * alpha)
            );
            if (var91) {
               this.hint(
                  "pasteimport",
                  var83 ? "Dán mã chia sẻ từ bộ nhớ tạm" : "Paste share code from clipboard",
                  this.pasteImportX,
                  this.pasteImportY + this.pasteImportH + 3.0F
               );
            }

            boolean var100 = hit(mx, my, this.doImportX, this.doImportY, this.doImportW, this.doImportH);
            NanoVGRenderer.drawRoundedRect(this.doImportX, this.doImportY, this.doImportW, this.doImportH, 5.0F, SoftPalette.mint((var100 ? 190 : 150) * alpha));
            SoftPalette.labelBold(
               var83 ? "Nhập" : "Import",
               this.doImportX + (var83 ? 10.0F : 9.0F),
               this.doImportY + 4.5F,
               8.0F,
               SoftPalette.clamp(255.0F * alpha) << 24 | 925208
            );
            if (var100) {
               this.hint(
                  "doimport", var83 ? "Tải cấu hình từ mã cloud" : "Import profile from cloud code", this.doImportX, this.doImportY + this.doImportH + 3.0F
               );
            }

            boolean var109 = hit(mx, my, this.cancelImportX, this.cancelImportY, this.cancelImportW, this.cancelImportH);
            NanoVGRenderer.drawRoundedRect(
               this.cancelImportX, this.cancelImportY, this.cancelImportW, this.cancelImportH, 5.0F, SoftPalette.white((var109 ? 22 : 12) * alpha)
            );
            SoftPalette.label(
               var83 ? "Hủy" : "Cancel", this.cancelImportX + (var83 ? 11.0F : 7.0F), this.cancelImportY + 4.5F, 8.0F, SoftPalette.text(235.0F * alpha)
            );
            var53 += var59 + 6.0F;
            var55 -= var59 + 6.0F;
         }

         if (this.namingNewConfig) {
            boolean var60 = Localization.isVietnamese();
            float var67 = 28.0F;
            NanoVGRenderer.drawRoundedRect(x, var53, w, var67, 7.0F, SoftPalette.white(10.0F * alpha));
            NanoVGRenderer.drawRoundedRectOutline(x, var53, w, var67, 7.0F, 1.0F, SoftPalette.mint(160.0F * alpha));
            IconManager.draw("config", x + 8.0F, var53 + 8.0F, 12.0F, SoftPalette.mint(220.0F * alpha));
            float var74 = x + 25.0F;
            String var84 = this.newConfigName.isEmpty() ? (var60 ? "Nhập tên cấu hình mới..." : "Enter profile name...") : this.newConfigName;
            if (this.newConfigSelectedAll && !this.newConfigName.isEmpty()) {
               float var93 = SoftPalette.width(var84, 8.5F);
               NanoVGRenderer.drawRoundedRect(var74 - 2.0F, var53 + 5.0F, var93 + 4.0F, var67 - 10.0F, 3.0F, SoftPalette.mint(120.0F * alpha));
               SoftPalette.label(var84, var74, var53 + 8.0F, 8.5F, SoftPalette.white(255.0F * alpha));
            } else {
               SoftPalette.label(
                  var84, var74, var53 + 8.0F, 8.5F, this.newConfigName.isEmpty() ? SoftPalette.textDim(160.0F * alpha) : SoftPalette.text(255.0F * alpha)
               );
               if (!this.newConfigName.isEmpty() && System.currentTimeMillis() / 450L % 2L == 0L) {
                  float var92 = SoftPalette.width(this.newConfigName, 8.5F);
                  NanoVGRenderer.drawRect(var74 + var92 + 1.5F, var53 + 6.5F, 1.2F, 15.0F, SoftPalette.mint(240.0F * alpha));
               }
            }

            this.cancelNewW = 46.0F;
            this.cancelNewH = 20.0F;
            this.cancelNewX = x + w - this.cancelNewW - 6.0F;
            this.cancelNewY = var53 + 4.0F;
            boolean var94 = hit(mx, my, this.cancelNewX, this.cancelNewY, this.cancelNewW, this.cancelNewH);
            NanoVGRenderer.drawRoundedRect(
               this.cancelNewX, this.cancelNewY, this.cancelNewW, this.cancelNewH, 5.0F, SoftPalette.white((var94 ? 22 : 12) * alpha)
            );
            NanoVGRenderer.drawRoundedRectOutline(
               this.cancelNewX, this.cancelNewY, this.cancelNewW, this.cancelNewH, 5.0F, 1.0F, SoftPalette.white((var94 ? 35 : 18) * alpha)
            );
            SoftPalette.label(
               var60 ? "Hủy" : "Cancel", this.cancelNewX + (var60 ? 11.0F : 9.0F), this.cancelNewY + 5.5F, 8.0F, SoftPalette.text(235.0F * alpha)
            );
            if (var94) {
               this.hint("cancelnew", var60 ? "Hủy (Esc)" : "Cancel (Esc)", this.cancelNewX, this.cancelNewY + this.cancelNewH + 4.0F);
            }

            this.saveNewW = 46.0F;
            this.saveNewH = 20.0F;
            this.saveNewX = this.cancelNewX - this.saveNewW - 6.0F;
            this.saveNewY = var53 + 4.0F;
            boolean var101 = hit(mx, my, this.saveNewX, this.saveNewY, this.saveNewW, this.saveNewH);
            NanoVGRenderer.drawRoundedRect(this.saveNewX, this.saveNewY, this.saveNewW, this.saveNewH, 5.0F, SoftPalette.mint((var101 ? 210 : 160) * alpha));
            SoftPalette.labelBold(
               var60 ? "Lưu" : "Save",
               this.saveNewX + (var60 ? 13.0F : 13.0F),
               this.saveNewY + 5.5F,
               8.0F,
               new Color(14, 30, 24, SoftPalette.clamp(255.0F * alpha))
            );
            if (var101) {
               this.hint(
                  "savenew",
                  var60 ? "Lưu cấu hình hiện tại (Enter)" : "Save current settings to profile (Enter)",
                  this.saveNewX,
                  this.saveNewY + this.saveNewH + 4.0F
               );
            }

            var53 += var67 + 8.0F;
            var55 -= var67 + 8.0F;
         }

         ArrayList var61 = new ArrayList();
         String var68 = this.searchQuery.trim().toLowerCase();

         for (String var85 : this.configList) {
            if (var68.isEmpty() || var85.toLowerCase().contains(var68)) {
               var61.add(var85);
            }
         }

         int var76 = var61.size();
         float var86 = w;
         float var95 = 34.0F;
         float var102 = 6.0F;
         this.maxScroll = Math.max(0.0F, var76 * (var95 + var102) - var55);
         this.visibleConfigRows.clear();
         if (var76 == 0) {
            float var111 = 76.0F;
            float var118 = var53 + 16.0F;
            NanoVGRenderer.drawRoundedRect(x, var118, w, var111, 8.0F, SoftPalette.white(6.0F * alpha));
            NanoVGRenderer.drawRoundedRectOutline(x, var118, w, var111, 8.0F, 1.0F, SoftPalette.white(10.0F * alpha));
            IconManager.draw("config", x + (w - 18.0F) / 2.0F, var118 + 14.0F, 18.0F, SoftPalette.textDim(160.0F * alpha));
            String var123 = this.searchQuery.isEmpty() ? "No configs saved yet" : "No configs matching \"" + this.searchQuery + "\"";
            SoftPalette.labelBold(var123, x + (w - SoftPalette.widthBold(var123, 9.0F)) / 2.0F, var118 + 38.0F, 9.0F, SoftPalette.text(225.0F * alpha));
            String var128 = this.searchQuery.isEmpty()
               ? "Click 'New Profile' above to create and save your setup"
               : "Try searching for a different profile name";
            SoftPalette.label(var128, x + (w - SoftPalette.width(var128, 7.5F)) / 2.0F, var118 + 52.0F, 7.5F, SoftPalette.textDim(160.0F * alpha));
         } else {
            NanoVGRenderer.scissor(x - 2.0F, var53, w + 4.0F, var55);
            boolean var110 = mx >= x && mx <= x + w && my >= var53 && my <= var53 + var55;

            for (int var117 = 0; var117 < var76; var117++) {
               String var122 = (String)var61.get(var117);
               float var127 = x;
               float var130 = var53 + var117 * (var95 + var102) - this.scroll;
               if (!(var130 + var95 < var53) && !(var130 > var53 + var55)) {
                  float var133 = this.rowEntrance(var117);
                  if (var133 < 0.999F) {
                     NanoVGRenderer.save();
                     NanoVGRenderer.translate(0.0F, (1.0F - var133) * 4.0F);
                  }

                  boolean var27 = var110 && hit(mx, my, var127, var130, var86, var95);
                  boolean var28 = var122.equals(this.defaultConfig);
                  ConfigSyncManager.CloudRecord var29 = ConfigSyncManager.getInstance().getCloudRecord(var122);
                  String var30 = var29 != null ? var29.getCode() : ConfigSyncManager.getInstance().getCloudCode(var122);
                  boolean var31 = var30 != null && !var30.isBlank();
                  boolean var32 = var29 == null || var29.isOwner();
                  if (var28) {
                     NanoVGRenderer.drawRoundedRect(var127, var130, var86, var95, 8.0F, SoftPalette.white((var27 ? 14 : 9) * alpha * var133));
                     NanoVGRenderer.drawRoundedRectOutline(var127, var130, var86, var95, 8.0F, 1.0F, SoftPalette.mint((var27 ? 210 : 140) * alpha * var133));
                     NanoVGRenderer.drawRoundedRect(var127 + 2.5F, var130 + 6.0F, 2.0F, var95 - 12.0F, 1.0F, SoftPalette.mint(240.0F * alpha * var133));
                  } else {
                     NanoVGRenderer.drawRoundedRect(var127, var130, var86, var95, 8.0F, SoftPalette.white((var27 ? 12 : 6) * alpha * var133));
                     NanoVGRenderer.drawRoundedRectOutline(var127, var130, var86, var95, 8.0F, 1.0F, SoftPalette.white((var27 ? 24 : 10) * alpha * var133));
                  }

                  float var33 = 20.0F;
                  float var34 = var127 + (var28 ? 9.0F : 7.0F);
                  float var35 = var130 + (var95 - var33) / 2.0F;
                  NanoVGRenderer.drawRoundedRect(
                     var34, var35, var33, var33, 5.0F, var28 ? SoftPalette.mint(32.0F * alpha * var133) : SoftPalette.white((var27 ? 14 : 8) * alpha * var133)
                  );
                  NanoVGRenderer.drawRoundedRectOutline(
                     var34,
                     var35,
                     var33,
                     var33,
                     5.0F,
                     1.0F,
                     var28 ? SoftPalette.mint(120.0F * alpha * var133) : SoftPalette.white((var27 ? 20 : 12) * alpha * var133)
                  );
                  IconManager.draw(
                     "file",
                     var34 + 4.5F,
                     var35 + 4.5F,
                     11.0F,
                     var28
                        ? SoftPalette.mint(255.0F * alpha * var133)
                        : (var27 ? SoftPalette.text(255.0F * alpha * var133) : SoftPalette.textDim(200.0F * alpha * var133))
                  );
                  DashboardView.ConfigRow var36 = new DashboardView.ConfigRow();
                  var36.name = var122;
                  var36.hovered = var27;
                  var36.x = var127;
                  var36.y = var130;
                  var36.w = var86;
                  var36.h = var95;
                  float var37 = var127 + var86 - 8.0F;
                  float var38 = 18.0F;
                  float var39 = var130 + (var95 - var38) / 2.0F;
                  if (var122.equals(this.deletingConfig)) {
                     boolean var168 = Localization.isVietnamese();
                     var36.noW = 28.0F;
                     var36.noH = var38;
                     var37 -= var36.noW;
                     var36.noX = var37;
                     var36.noY = var39;
                     boolean var171 = hit(mx, my, var36.noX, var36.noY, var36.noW, var36.noH);
                     NanoVGRenderer.drawRoundedRect(var36.noX, var36.noY, var36.noW, var36.noH, 4.0F, SoftPalette.white((var171 ? 22 : 12) * alpha * var133));
                     NanoVGRenderer.drawRoundedRectOutline(
                        var36.noX, var36.noY, var36.noW, var36.noH, 4.0F, 1.0F, SoftPalette.white((var171 ? 32 : 16) * alpha * var133)
                     );
                     SoftPalette.label(
                        var168 ? "Không" : "No", var36.noX + (var168 ? 4.0F : 8.0F), var36.noY + 4.5F, 7.5F, SoftPalette.text(245.0F * alpha * var133)
                     );
                     if (var171) {
                        this.hint("delno", var168 ? "Hủy xóa" : "Cancel deletion", var36.noX, var36.noY + var36.noH + 3.0F);
                     }

                     var37 -= 6.0F;
                     var36.yesW = 32.0F;
                     var36.yesH = var38;
                     var37 -= var36.yesW;
                     var36.yesX = var37;
                     var36.yesY = var39;
                     boolean var174 = hit(mx, my, var36.yesX, var36.yesY, var36.yesW, var36.yesH);
                     NanoVGRenderer.drawRoundedRect(
                        var36.yesX, var36.yesY, var36.yesW, var36.yesH, 4.0F, new Color(235, 75, 75, SoftPalette.clamp((var174 ? 230 : 180) * alpha * var133))
                     );
                     SoftPalette.labelBold(
                        var168 ? "Có" : "Yes", var36.yesX + (var168 ? 10.0F : 7.0F), var36.yesY + 4.5F, 7.5F, SoftPalette.white(255.0F * alpha * var133)
                     );
                     if (var174) {
                        this.hint("delyes", var168 ? "Xác nhận xóa" : "Confirm deletion", var36.yesX, var36.yesY + var36.yesH + 3.0F);
                     }

                     float var164 = var37 - 8.0F;
                     String var177 = var168 ? "Xóa cấu hình?" : "Delete profile?";
                     float var181 = SoftPalette.width(var177, 8.0F);
                     var37 = var164 - var181;
                     SoftPalette.label(var177, var37, var39 + 4.0F, 8.0F, new Color(245, 130, 130, SoftPalette.clamp(245.0F * alpha * var133)));
                  } else {
                     boolean var40 = Localization.isVietnamese();
                     var36.loadW = 36.0F;
                     var36.loadH = var38;
                     var37 -= var36.loadW;
                     var36.loadX = var37;
                     var36.loadY = var39;
                     boolean var41 = hit(mx, my, var36.loadX, var36.loadY, var36.loadW, var36.loadH);
                     NanoVGRenderer.drawRoundedRect(
                        var36.loadX, var36.loadY, var36.loadW, var36.loadH, 4.0F, SoftPalette.mint((var41 ? 60 : 30) * alpha * var133)
                     );
                     NanoVGRenderer.drawRoundedRectOutline(
                        var36.loadX, var36.loadY, var36.loadW, var36.loadH, 4.0F, 1.0F, SoftPalette.mint((var41 ? 200 : 100) * alpha * var133)
                     );
                     SoftPalette.labelBold(
                        var40 ? "Tải" : "Load",
                        var36.loadX + (var40 ? 10.0F : 8.0F),
                        var36.loadY + 4.5F,
                        7.5F,
                        var41 ? SoftPalette.mint(255.0F * alpha * var133) : SoftPalette.text(245.0F * alpha * var133)
                     );
                     if (var41) {
                        this.hint(
                           "cfgload_" + var117,
                           var40 ? "Tải cấu hình '" + var122 + "'" : "Load profile '" + var122 + "'",
                           var36.loadX,
                           var36.loadY + var36.loadH + 3.0F
                        );
                     }

                     var37 -= 5.0F;
                     var36.saveW = 18.0F;
                     var36.saveH = var38;
                     var37 -= var36.saveW;
                     var36.saveX = var37;
                     var36.saveY = var39;
                     boolean var42 = hit(mx, my, var36.saveX, var36.saveY, var36.saveW, var36.saveH);
                     NanoVGRenderer.drawRoundedRect(
                        var36.saveX, var36.saveY, var36.saveW, var36.saveH, 4.0F, SoftPalette.white((var42 ? 22 : 10) * alpha * var133)
                     );
                     NanoVGRenderer.drawRoundedRectOutline(
                        var36.saveX, var36.saveY, var36.saveW, var36.saveH, 4.0F, 1.0F, SoftPalette.white((var42 ? 32 : 14) * alpha * var133)
                     );
                     IconManager.draw("save", var36.saveX + 4.0F, var36.saveY + 4.0F, 10.0F, SoftPalette.clamp((var42 ? 255 : 180) * alpha * var133));
                     var37 -= 4.0F;
                     var36.starW = 18.0F;
                     var36.starH = var38;
                     var37 -= var36.starW;
                     var36.starX = var37;
                     var36.starY = var39;
                     boolean var43 = hit(mx, my, var36.starX, var36.starY, var36.starW, var36.starH);
                     NanoVGRenderer.drawRoundedRect(
                        var36.starX,
                        var36.starY,
                        var36.starW,
                        var36.starH,
                        4.0F,
                        var28 ? SoftPalette.mint((var43 ? 50 : 25) * alpha * var133) : SoftPalette.white((var43 ? 20 : 8) * alpha * var133)
                     );
                     if (var28 || var43) {
                        NanoVGRenderer.drawRoundedRectOutline(
                           var36.starX,
                           var36.starY,
                           var36.starW,
                           var36.starH,
                           4.0F,
                           1.0F,
                           var28 ? SoftPalette.mint(140.0F * alpha * var133) : SoftPalette.white(25.0F * alpha * var133)
                        );
                     }

                     NVGIcons.star(
                        var36.starX + 9.0F,
                        var36.starY + 9.0F,
                        3.8F,
                        var28
                           ? SoftPalette.mint(255.0F * alpha * var133)
                           : (var43 ? SoftPalette.text(245.0F * alpha * var133) : SoftPalette.textDim(160.0F * alpha * var133))
                     );
                     if (var43) {
                        this.hint(
                           "cfgstar_" + var117,
                           var28
                              ? (var40 ? "Cấu hình khởi động mặc định (bấm để hủy)" : "Default startup profile (click to unset)")
                              : (var40 ? "Đặt làm cấu hình mặc định" : "Set as default startup profile"),
                           var36.starX - 40.0F,
                           var36.starY + var36.starH + 3.0F
                        );
                     }

                     var37 -= 4.0F;
                     if (var42) {
                        if (var31 && !var32) {
                           this.hint(
                              "cfgsave_" + var117,
                              var40
                                 ? "Ghi đè file cục bộ (Lưu ý: Cloud khóa cho @" + var29.getAuthorDiscordName() + ")"
                                 : "Overwrite local file (Note: Cloud overwrite locked to @" + var29.getAuthorDiscordName() + ")",
                              var36.saveX - 110.0F,
                              var36.saveY + var36.saveH + 3.0F
                           );
                        } else {
                           this.hint(
                              "cfgsave_" + var117,
                              var40 ? "Ghi đè cấu hình với cài đặt hiện tại" : "Overwrite profile with current settings",
                              var36.saveX - 20.0F,
                              var36.saveY + var36.saveH + 3.0F
                           );
                        }
                     }

                     var36.shareW = 20.0F;
                     var36.shareH = var38;
                     var37 -= var36.shareW;
                     var36.shareX = var37;
                     var36.shareY = var39;
                     boolean var44 = hit(mx, my, var36.shareX, var36.shareY, var36.shareW, var36.shareH);
                     if (var31) {
                        if (var32) {
                           NanoVGRenderer.drawRoundedRect(
                              var36.shareX, var36.shareY, var36.shareW, var36.shareH, 4.0F, SoftPalette.mint((var44 ? 50 : 25) * alpha * var133)
                           );
                           NanoVGRenderer.drawRoundedRectOutline(
                              var36.shareX, var36.shareY, var36.shareW, var36.shareH, 4.0F, 1.0F, SoftPalette.mint((var44 ? 160 : 90) * alpha * var133)
                           );
                           NVGIcons.cloud(
                              var36.shareX + var36.shareW / 2.0F, var36.shareY + var36.shareH / 2.0F, 10.0F, SoftPalette.mint(255.0F * alpha * var133)
                           );
                        } else {
                           NanoVGRenderer.drawRoundedRect(
                              var36.shareX,
                              var36.shareY,
                              var36.shareW,
                              var36.shareH,
                              4.0F,
                              new Color(70, 140, 240, SoftPalette.clamp((var44 ? 55 : 28) * alpha * var133))
                           );
                           NanoVGRenderer.drawRoundedRectOutline(
                              var36.shareX,
                              var36.shareY,
                              var36.shareW,
                              var36.shareH,
                              4.0F,
                              1.0F,
                              new Color(100, 180, 255, SoftPalette.clamp((var44 ? 170 : 100) * alpha * var133))
                           );
                           NVGIcons.cloud(
                              var36.shareX + var36.shareW / 2.0F,
                              var36.shareY + var36.shareH / 2.0F,
                              10.0F,
                              new Color(130, 210, 255, SoftPalette.clamp(240.0F * alpha * var133))
                           );
                        }
                     } else {
                        NanoVGRenderer.drawRoundedRect(
                           var36.shareX, var36.shareY, var36.shareW, var36.shareH, 4.0F, SoftPalette.white((var44 ? 22 : 10) * alpha * var133)
                        );
                        NanoVGRenderer.drawRoundedRectOutline(
                           var36.shareX, var36.shareY, var36.shareW, var36.shareH, 4.0F, 1.0F, SoftPalette.white((var44 ? 32 : 14) * alpha * var133)
                        );
                        IconManager.draw("config", var36.shareX + 4.5F, var36.shareY + 4.0F, 10.0F, SoftPalette.clamp((var44 ? 255 : 180) * alpha * var133));
                     }

                     if (var44) {
                        if (var31) {
                           if (var32) {
                              this.hint(
                                 "share:" + var122,
                                 var40
                                    ? "Mã: " + var30 + " (Click để chép | Shift+Click để đăng lại)"
                                    : "Code: " + var30 + " (Click to copy | Shift+Click re-upload)",
                                 var36.shareX - 90.0F,
                                 var39 + var38 + 4.0F
                              );
                           } else {
                              this.hint(
                                 "share:" + var122,
                                 var40
                                    ? "Mã: " + var30 + " (Tác giả @" + var29.getAuthorDiscordName() + " • Khóa chính chủ)"
                                    : "Code: " + var30 + " (By @" + var29.getAuthorDiscordName() + " • Locked: author only)",
                                 var36.shareX - 110.0F,
                                 var39 + var38 + 4.0F
                              );
                           }
                        } else {
                           this.hint(
                              "share:" + var122,
                              var40 ? "Chia sẻ lên cloud (Nhận mã 6 ký tự)" : "Share to cloud (Get 6-char code)",
                              var36.shareX - 55.0F,
                              var39 + var38 + 4.0F
                           );
                        }
                     }

                     float var159 = var37 - 4.0F;
                     var36.delW = 18.0F;
                     var36.delH = var38;
                     var37 = var159 - var36.delW;
                     var36.delX = var37;
                     var36.delY = var39;
                     boolean var45 = hit(mx, my, var36.delX, var36.delY, var36.delW, var36.delH);
                     if (var45) {
                        this.hint(
                           "cfgdel_" + var117,
                           var40 ? "Xóa cấu hình '" + var122 + "'" : "Delete profile '" + var122 + "'",
                           var36.delX - 25.0F,
                           var39 + var38 + 4.0F
                        );
                        NanoVGRenderer.drawRoundedRect(
                           var36.delX, var36.delY, var36.delW, var36.delH, 4.0F, new Color(235, 80, 80, SoftPalette.clamp(60.0F * alpha * var133))
                        );
                        NanoVGRenderer.drawRoundedRectOutline(
                           var36.delX, var36.delY, var36.delW, var36.delH, 4.0F, 1.0F, new Color(235, 80, 80, SoftPalette.clamp(140.0F * alpha * var133))
                        );
                     } else {
                        NanoVGRenderer.drawRoundedRect(var36.delX, var36.delY, var36.delW, var36.delH, 4.0F, SoftPalette.white(8.0F * alpha * var133));
                     }

                     NVGIcons.cross(
                        var36.delX + 9.0F,
                        var36.delY + 9.0F,
                        3.6F,
                        1.1F,
                        var45 ? SoftPalette.white(255.0F * alpha * var133) : new Color(235, 120, 120, SoftPalette.clamp(190.0F * alpha * var133))
                     );
                     if (var45) {
                        this.hint("cfgdel_" + var117, "Delete profile", var36.delX - 10.0F, var36.delY + var36.delH + 3.0F);
                     }
                  }

                  float var169 = var34 + var33 + 8.0F;
                  float var172 = Math.max(10.0F, var37 - var169 - 8.0F);
                  String var175 = SoftPalette.fit(var122, var172 - (var31 ? 45.0F : 0.0F), 9.0F);
                  SoftPalette.labelBold(
                     var175, var169, var130 + 6.0F, 9.0F, var28 ? SoftPalette.mint(255.0F * alpha * var133) : SoftPalette.text(240.0F * alpha * var133)
                  );
                  if (var31) {
                     float var178 = var169 + SoftPalette.widthBold(var175, 9.0F) + 5.0F;
                     if (var32) {
                        NanoVGRenderer.drawRoundedRect(var178, var130 + 6.0F, 24.0F, 11.0F, 3.0F, SoftPalette.mint(40.0F * alpha * var133));
                        SoftPalette.labelBold("YOU", var178 + 4.0F, var130 + 7.5F, 6.5F, SoftPalette.mint(255.0F * alpha * var133));
                     } else {
                        String var182 = "@" + (var29 != null && !var29.getAuthorDiscordName().isBlank() ? var29.getAuthorDiscordName() : "Community");
                        float var188 = SoftPalette.widthBold(var182, 6.5F) + 8.0F;
                        NanoVGRenderer.drawRoundedRect(
                           var178, var130 + 6.0F, var188, 11.0F, 3.0F, new Color(70, 140, 240, SoftPalette.clamp(40.0F * alpha * var133))
                        );
                        NanoVGRenderer.drawRoundedRectOutline(
                           var178, var130 + 6.0F, var188, 11.0F, 3.0F, 1.0F, new Color(100, 180, 255, SoftPalette.clamp(110.0F * alpha * var133))
                        );
                        SoftPalette.labelBold(var182, var178 + 4.0F, var130 + 7.5F, 6.5F, new Color(130, 210, 255, SoftPalette.clamp(255.0F * alpha * var133)));
                     }
                  }

                  String var179 = var28 ? "Default profile • " + var122 + ".json" : var122 + ".json";
                  if (!var31) {
                     String var183 = SoftPalette.fit(var179, var172, 6.5F);
                     SoftPalette.label(
                        var183, var169, var130 + 19.0F, 6.5F, var28 ? SoftPalette.mint(200.0F * alpha * var133) : SoftPalette.textDim(160.0F * alpha * var133)
                     );
                  } else {
                     String var184 = var179 + " • ";
                     float var189 = SoftPalette.width(var184, 6.5F);
                     SoftPalette.label(
                        var184, var169, var130 + 19.0F, 6.5F, var28 ? SoftPalette.mint(200.0F * alpha * var133) : SoftPalette.textDim(160.0F * alpha * var133)
                     );
                     float var46 = var169 + var189 + 4.0F;
                     float var47 = var130 + 22.5F;
                     NVGIcons.cloud(
                        var46,
                        var47,
                        6.5F,
                        var32 ? SoftPalette.mint(220.0F * alpha * var133) : new Color(120, 190, 255, SoftPalette.clamp(220.0F * alpha * var133))
                     );
                     float var48 = var46 + 5.5F;
                     SoftPalette.labelBold(
                        var30,
                        var48,
                        var130 + 19.0F,
                        6.5F,
                        var32 ? SoftPalette.mint(220.0F * alpha * var133) : new Color(120, 190, 255, SoftPalette.clamp(220.0F * alpha * var133))
                     );
                     if (!var32 && var29 != null && !var29.getAuthorDiscordName().isBlank()) {
                        float var49 = SoftPalette.widthBold(var30, 6.5F);
                        SoftPalette.label(
                           " • Uploaded by @" + var29.getAuthorDiscordName(),
                           var48 + var49 + 3.0F,
                           var130 + 19.0F,
                           6.5F,
                           new Color(140, 200, 255, SoftPalette.clamp(200.0F * alpha * var133))
                        );
                     }
                  }

                  if (var27 && !var122.equals(this.deletingConfig)) {
                     boolean var185 = hit(mx, my, var36.delX, var36.delY, var36.delW, var36.delH)
                        || hit(mx, my, var36.shareX, var36.shareY, var36.shareW, var36.shareH)
                        || hit(mx, my, var36.starX, var36.starY, var36.starW, var36.starH)
                        || hit(mx, my, var36.saveX, var36.saveY, var36.saveW, var36.saveH)
                        || hit(mx, my, var36.loadX, var36.loadY, var36.loadW, var36.loadH);
                     if (!var185) {
                        this.hint("cfg:" + var122, "Left click: Load | Right click: Share & copy code", var127 + 10.0F, var130 + var95 + 4.0F);
                     }
                  }

                  this.visibleConfigRows.add(var36);
                  if (var133 < 0.999F) {
                     NanoVGRenderer.restore();
                  }
               }
            }

            if (this.scroll > 1.0F) {
               NanoVGRenderer.drawVerticalGradientRect(
                  x - 2.0F, var53, w + 4.0F, 12.0F, SoftPalette.rgba(SoftPalette.BODY_TOP, 200.0F * alpha), SoftPalette.rgba(SoftPalette.BODY_TOP, 0.0F)
               );
            }

            NanoVGRenderer.resetScissor();
            this.renderScrollbar(x + w + 3.0F, var53, var55, alpha);
         }
      }
   }

   private static boolean hit(double mx, double my, float x, float y, float w, float h) {
      return mx >= x && mx <= x + w && my >= y && my <= y + h;
   }

   private static boolean hit(double mx, double my, float[] r) {
      return hit(mx, my, r[0], r[1], r[2], r[3]);
   }

   private void refreshConfigs() {
      this.configList.clear();

      try {
         File var1 = ConfigManager.getConfigsDir().toFile();
         if (var1.exists()) {
            File[] var2 = var1.listFiles((d, n) -> {
               byte[] var10001x = new byte[5];
               var10001x[0] = (byte)(-59);
               var10001x[1] = (byte)(-16);
               var10001x[2] = (byte)(111);
               var10001x[3] = (byte)(-79);
               var10001x[4] = (byte)(7);
               return n.endsWith($$v_s(var10001x, 4273728924488552654L));
            });
            if (var2 != null) {
               for (File var6 : var2) {
                  List var10000 = this.configList;
                  String var10001 = var6.getName();
                  byte[] var10002 = new byte[5];
                  var10002[0] = (byte)(-59);
                  var10002[1] = (byte)(-16);
                  var10002[2] = (byte)(111);
                  var10002[3] = (byte)(-79);
                  var10002[4] = (byte)(7);
                  var10000.add(var10001.replace($$v_s(var10002, 4273728924488552654L), ""));
               }
            }
         }
      } catch (Throwable var8) {
      }

      try {
         File var9 = ConfigManager.getConfigsDir().toFile();
         byte[] var10003 = new byte[8];
         var10003[0] = (byte)(-59);
         var10003[1] = (byte)(-2);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-72);
         var10003[4] = (byte)(8);
         var10003[5] = (byte)(-2);
         var10003[6] = (byte)(32);
         var10003[7] = (byte)(103);
         File var10 = new File(var9, $$v_s(var10003, 4273728924488552654L));
         this.defaultConfig = var10.exists() ? new String(Files.readAllBytes(var10.toPath()), StandardCharsets.UTF_8).trim() : null;
      } catch (Throwable var7) {
         this.defaultConfig = null;
      }
   }

   private void loadConfig(String name) {
      try {
         VieClient.INSTANCE.getConfigManager().loadProfile(name);
         this.setStatus("Loaded: " + name);
         byte[] var10000 = new byte[6];
         var10000[0] = (byte)(-88);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-72);
         var10000[4] = 0;
         var10000[5] = (byte)(-20);
         String var4 = $$v_s(var10000, 4273728924488552654L);
         String var10002 = "Loaded profile: " + name;
         byte[] var10003 = new byte[4];
         var10003[0] = (byte)(-115);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(112);
         var10003[3] = (byte)(-69);
         ToastManager.push(var4, true, var10002, $$v_s(var10003, 4273728924488552654L));
         Sounds.instance.playCfgLoad();
      } catch (Throwable var3) {
         this.setStatus("Failed to load " + name);
      }
   }

   private void saveConfig(String name) {
      try {
         boolean var2 = Localization.isVietnamese();
         VieClient.INSTANCE.getConfigManager().saveProfile(name);
         this.refreshConfigs();
         this.setStatus(var2 ? "Đã lưu: " + name : "Saved: " + name);
         byte[] var10000 = new byte[6];
         var10000[0] = (byte)(-88);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-72);
         var10000[4] = 0;
         var10000[5] = (byte)(-20);
         String var4 = $$v_s(var10000, 4273728924488552654L);
         String var10002 = var2 ? "Đã lưu cấu hình: " + name : "Saved profile: " + name;
         byte[] var10003 = new byte[4];
         var10003[0] = (byte)(-115);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(112);
         var10003[3] = (byte)(-69);
         ToastManager.push(var4, true, var10002, $$v_s(var10003, 4273728924488552654L));
         Sounds.instance.playCfgLoad();
      } catch (Throwable var3) {
         this.setStatus(Localization.isVietnamese() ? "Lưu thất bại " + name : "Failed to save " + name);
      }
   }

   private void deleteConfig(String name) {
      try {
         boolean var2 = Localization.isVietnamese();
         VieClient.INSTANCE.getConfigManager().deleteProfile(name);
         ConfigSyncManager.getInstance().removeCloudCode(name);
         this.refreshConfigs();
         this.setStatus(var2 ? "Đã xóa: " + name : "Deleted: " + name);
         byte[] var10000 = new byte[6];
         var10000[0] = (byte)(-88);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-72);
         var10000[4] = 0;
         var10000[5] = (byte)(-20);
         String var4 = $$v_s(var10000, 4273728924488552654L);
         String var10002 = var2 ? "Đã xóa cấu hình: " + name : "Deleted profile: " + name;
         byte[] var10003 = new byte[4];
         var10003[0] = (byte)(-115);
         var10003[1] = (byte)(-13);
         var10003[2] = (byte)(112);
         var10003[3] = (byte)(-69);
         ToastManager.push(var4, false, var10002, $$v_s(var10003, 4273728924488552654L));
         Sounds.instance.playCfgLoad();
      } catch (Throwable var3) {
         this.setStatus(Localization.isVietnamese() ? "Xóa thất bại " + name : "Failed to delete " + name);
      }
   }

   private void shareConfig(String name) {
      this.shareConfig(name, false);
   }

   private void shareConfig(String name, boolean forceReupload) {
      String var3 = name != null && !name.isBlank() ? name : $$v_s(new byte[]{-88, -17, 110, -84, 12, -27, 56}, 4273728924488552654L);

      try {
         if (VieClient.INSTANCE != null && VieClient.INSTANCE.getConfigManager() != null) {
            VieClient.INSTANCE.getConfigManager().saveProfile(var3);
         }
      } catch (Throwable var10) {
      }

      ConfigSyncManager var4 = ConfigSyncManager.getInstance();
      ConfigSyncManager.CloudRecord var5 = var4.getCloudRecord(var3);
      if (forceReupload && var5 != null && !var5.getCode().isBlank() && !var5.isOwner()) {
         var4.removeCloudCode(var3);
      }

      String var6 = var4.getCloudCode(var3);
      boolean var7 = Localization.isVietnamese();
      if (!forceReupload && var6 != null && !var6.isBlank()) {
         this.lastSharedCode = var6;
         this.showSharePopup = true;
         this.copiedCodeNotice = true;

         try {
            GLFW.glfwSetClipboardString(class_310.method_1551().method_22683().method_4490(), var6);
            class_310 var8 = class_310.method_1551();
            if (var8 != null && var8.field_1774 != null) {
               var8.field_1774.method_1455(var6);
            }
         } catch (Throwable var9) {
         }

         this.setStatus(var7 ? "Mã: " + var6 + " (Đã chép!)" : "Code: " + var6 + " (Copied!)");
         ToastManager.push(
            $$v_s(new byte[]{-88, -11, 114, -72, 0, -20, 108, 64, -21, -61, -106, -109}, 4273728924488552654L),
            true,
            var7 ? "Mã: " + var6 + " (Đã chép vào clipboard!)" : "Code: " + var6 + " (Copied to clipboard!)",
            $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
         );
         Sounds.instance.playCfgLoad();
      } else {
         this.setStatus(
            var7
               ? $$v_s(
                  new byte[]{47, 10, 125, -80, 14, -85, 56, -14, 57, 1, -115, -42, 93, -112, -50, 5, -101, 41, -96, -63, -84, 31, 50, 109, 125},
                  4273728924488552654L
               )
               : $$v_s(new byte[]{-66, -22, 112, -79, 8, -17, 37, 125, -28, -126, -112, -103, 17, 48, 8, 4, -50, 46, -30, -128, -9}, 4273728924488552654L)
         );
         var4.saveAndShareAsync(
            var3,
            code -> {
               if (code != null) {
                  this.lastSharedCode = code;
                  this.showSharePopup = true;
                  this.copiedCodeNotice = true;
                  this.refreshConfigs();

                  try {
                     GLFW.glfwSetClipboardString(class_310.method_1551().method_22683().method_4490(), code);
                     class_310 var3x = class_310.method_1551();
                     if (var3x != null && var3x.field_1774 != null) {
                        var3x.field_1774.method_1455(code);
                     }
                  } catch (Throwable var4x) {
                  }

                  this.setStatus("Code: " + code + " (Copied!)");
                  ToastManager.push(
                     $$v_s(new byte[]{-88, -11, 114, -72, 0, -20, 108, 64, -21, -61, -106, -109}, 4273728924488552654L),
                     true,
                     "Code: " + code + " (Copied to clipboard!)",
                     $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                  );
                  Sounds.instance.playCfgLoad();
               } else {
                  String var5x = var4.getLastSaveError();
                  this.setStatus(
                     "Share failed: "
                        + (var5x.isEmpty() ? $$v_s(new byte[]{-91, -1, 104, -87, 6, -7, 39, 51, -26, -48, -106, -103, 67}, 4273728924488552654L) : var5x)
                  );
                  ToastManager.push(
                     $$v_s(new byte[]{-88, -11, 114, -72, 0, -20, 108, 64, -21, -61, -106, -109}, 4273728924488552654L),
                     false,
                     "Share failed: "
                        + (var5x.isEmpty() ? $$v_s(new byte[]{-91, -1, 104, -87, 6, -7, 39, 51, -26, -48, -106, -103, 67}, 4273728924488552654L) : var5x),
                     $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                  );
               }
            }
         );
      }
   }

   private void commitImportCloudConfig() {
      if (!this.importCodeText.trim().isEmpty()) {
         String var1 = this.importCodeText.trim().toUpperCase();
         this.setStatus("Downloading: " + var1 + "...");
         ConfigSyncManager.getInstance()
            .importByCodeAsync(
               var1,
               importedName -> {
                  if (importedName != null) {
                     this.refreshConfigs();
                     this.loadConfig(importedName);
                     this.setStatus("Imported: " + importedName);
                     ToastManager.push(
                        $$v_s(new byte[]{-88, -11, 114, -72, 0, -20, 108, 90, -18, -46, -117, -124, 69}, 4273728924488552654L),
                        true,
                        "Imported profile: " + importedName,
                        $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                     );
                     Sounds.instance.playCfgLoad();
                     this.importingCloudConfig = false;
                     this.importCodeText = "";
                  } else {
                     String var3 = ConfigSyncManager.getInstance().getLastSaveError();
                     this.setStatus(
                        "Import failed: "
                           + (
                              var3.isEmpty()
                                 ? $$v_s(new byte[]{-88, -11, 120, -69, 73, -27, 35, 103, -93, -60, -117, -125, 95, 55}, 4273728924488552654L)
                                 : var3
                           )
                     );
                     ToastManager.push(
                        $$v_s(new byte[]{-88, -11, 114, -72, 0, -20, 108, 90, -18, -46, -117, -124, 69}, 4273728924488552654L),
                        false,
                        "Failed to load code: " + var1,
                        $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                     );
                  }
               }
            );
      }
   }

   private void refreshCommunityConfigs() {
      this.loadingCommunity = true;
      this.lastCommunityFetchTime = System.currentTimeMillis();
      String var1 = this.communitySearch != null ? this.communitySearch.trim() : "";
      String var10000;
      if (this.communitySort != null) {
         var10000 = this.communitySort;
      } else {
         byte[] var3 = new byte[3];
         var3[0] = (byte)(-97);
         var3[1] = (byte)(-11);
         var3[2] = (byte)(108);
         var10000 = $$v_s(var3, 4273728924488552654L);
      }

      String var2 = var10000;
      ConfigSyncManager.getInstance().fetchCommunityConfigsAsync(var2, var1, 1, 50, list -> {
         this.loadingCommunity = false;
         this.communityConfigs.clear();
         if (list != null) {
            this.communityConfigs.addAll(list);
         }
      });
   }

   private void toggleLikeCommunity(ConfigSyncManager.CommunityConfig cfg) {
      if (cfg != null) {
         boolean var2 = !cfg.hasLiked();
         cfg.setLiked(var2);
         Sounds.instance.playTap();
         ConfigSyncManager.getInstance().toggleLikeCommunityConfigAsync(cfg.getCode(), (liked, likesCount) -> {
            if (liked != var2) {
               cfg.setLiked(liked);
            }
         });
      }
   }

   private void downloadCommunityConfig(ConfigSyncManager.CommunityConfig cfg) {
      if (cfg != null && !cfg.getCode().isBlank()) {
         this.setStatus("Downloading @" + cfg.getAuthorDiscordName() + "'s setup: " + cfg.getTitle() + "...");
         byte[] var10000 = new byte[13];
         var10000[0] = (byte)(-88);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(113);
         var10000[3] = (byte)(-77);
         var10000[4] = (byte)(28);
         var10000[5] = (byte)(-27);
         var10000[6] = (byte)(37);
         var10000[7] = (byte)(103);
         var10000[8] = (byte)(-6);
         var10000[9] = (byte)(-126);
         var10000[10] = (byte)(-84);
         var10000[11] = (byte)(-125);
         var10000[12] = (byte)(83);
         String var2 = $$v_s(var10000, 4273728924488552654L);
         String var10002 = "Importing setup: " + cfg.getTitle();
         byte[] var10003 = new byte[8];
         var10003[0] = (byte)(-113);
         var10003[1] = (byte)(-11);
         var10003[2] = (byte)(107);
         var10003[3] = (byte)(-80);
         var10003[4] = (byte)(5);
         var10003[5] = (byte)(-28);
         var10003[6] = (byte)(45);
         var10003[7] = (byte)(119);
         ToastManager.push(var2, true, var10002, $$v_s(var10003, 4273728924488552654L));
         ConfigSyncManager.getInstance()
            .downloadCommunityConfigAsync(
               cfg.getCode(),
               importedName -> {
                  if (importedName != null) {
                     this.refreshConfigs();
                     this.loadConfig(importedName);
                     this.setStatus("Loaded setup: " + importedName);
                     ToastManager.push(
                        $$v_s(new byte[]{-88, -11, 113, -77, 28, -27, 37, 103, -6, -126, -84, -125, 83}, 4273728924488552654L),
                        true,
                        "Applied setup: " + importedName,
                        $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                     );
                     Sounds.instance.playCfgLoad();
                  } else {
                     this.setStatus("Failed to download setup " + cfg.getCode());
                     ToastManager.push(
                        $$v_s(new byte[]{-88, -11, 113, -77, 28, -27, 37, 103, -6, -126, -84, -125, 83}, 4273728924488552654L),
                        false,
                        $$v_s(
                           new byte[]{-83, -5, 117, -78, 12, -17, 108, 103, -20, -126, -128, -103, 70, 61, 8, 4, -38, 46, -20, -35, -68, 15, 105, 51},
                           4273728924488552654L
                        ),
                        $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                     );
                  }
               }
            );
      }
   }

   private void deleteCommunityConfig(ConfigSyncManager.CommunityConfig cfg) {
      if (cfg != null && !cfg.getCode().isBlank()) {
         this.setStatus("Removing community post: " + cfg.getTitle() + "...");
         ConfigSyncManager.getInstance()
            .deleteCommunityConfigAsync(
               cfg.getCode(),
               ok -> {
                  if (ok) {
                     this.setStatus("Removed community post: " + cfg.getTitle());
                     ToastManager.push(
                        $$v_s(new byte[]{-88, -11, 113, -77, 28, -27, 37, 103, -6, -126, -84, -125, 83}, 4273728924488552654L),
                        true,
                        $$v_s(
                           new byte[]{-69, -11, 111, -86, 73, -7, 41, 126, -20, -44, -127, -110, 17, 32, 17, 8, -40, 47, -65, -35, -65, 14, 112, 47, 42},
                           4273728924488552654L
                        ),
                        $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                     );
                     this.refreshCommunityConfigs();
                  } else {
                     this.setStatus("Failed to remove post " + cfg.getCode());
                     ToastManager.push(
                        $$v_s(new byte[]{-88, -11, 113, -77, 28, -27, 37, 103, -6, -126, -84, -125, 83}, 4273728924488552654L),
                        false,
                        $$v_s(
                           new byte[]{-83, -5, 117, -78, 12, -17, 108, 103, -20, -126, -106, -109, 92, 60, 18, 14, -101, 58, -93, -35, -83},
                           4273728924488552654L
                        ),
                        $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                     );
                  }
               }
            );
      }
   }

   private void openPublishModal(String profileName) {
      String var10001;
      if (profileName != null && !profileName.isBlank()) {
         var10001 = profileName;
      } else if (this.defaultConfig != null) {
         var10001 = this.defaultConfig;
      } else {
         byte[] var2 = new byte[7];
         var2[0] = (byte)(-88);
         var2[1] = (byte)(-17);
         var2[2] = (byte)(110);
         var2[3] = (byte)(-84);
         var2[4] = (byte)(12);
         var2[5] = (byte)(-27);
         var2[6] = (byte)(56);
         var10001 = $$v_s(var2, 4273728924488552654L);
      }

      this.publishProfileName = var10001;
      this.publishTitle = this.publishProfileName;
      this.publishDesc = "";
      byte[] var3 = new byte[13];
      var3[0] = (byte)(-120);
      var3[1] = (byte)(-24);
      var3[2] = (byte)(101);
      var3[3] = (byte)(-83);
      var3[4] = (byte)(29);
      var3[5] = (byte)(-22);
      var3[6] = (byte)(32);
      var3[7] = (byte)(63);
      var3[8] = (byte)(-93);
      var3[9] = (byte)(-63);
      var3[10] = (byte)(-108);
      var3[11] = (byte)(-128);
      var3[12] = (byte)(65);
      this.publishTags = $$v_s(var3, 4273728924488552654L);
      this.publishFocusedField = 0;
      this.publishingToCommunity = true;
   }

   private void commitPublishCommunity() {
      String var1 = this.publishTitle.trim();
      if (var1.isEmpty()) {
         ToastManager.push(
            $$v_s(new byte[]{-69, -17, 126, -78, 0, -8, 36, 51, -53, -41, -122}, 4273728924488552654L),
            false,
            $$v_s(new byte[]{-65, -13, 104, -78, 12, -85, 47, 114, -19, -52, -117, -126, 17, 49, 1, 75, -34, 39, -68, -38, -96}, 4273728924488552654L),
            $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
         );
      } else {
         this.publishingToCommunity = false;
         String var2 = this.publishDesc.trim();
         ArrayList var3 = new ArrayList();
         if (!this.publishTags.trim().isEmpty()) {
            for (String var7 : this.publishTags.split($$v_s(new byte[]{-80, -74, 60, -125, 66}, 4273728924488552654L))) {
               String var8 = var7.trim().replace("#", "");
               if (!var8.isEmpty()) {
                  var3.add(var8);
               }
            }
         }

         this.setStatus(
            $$v_s(
               new byte[]{
                  -69, -17, 126, -78, 0, -8, 36, 122, -19, -59, -60, -126, 94, 115, 39, 4, -42, 39, -71, -64, -80, 15, 101, 99, 27, 103, -10, 104, -49, 45
               },
               4273728924488552654L
            )
         );
         ToastManager.push(
            $$v_s(new byte[]{-69, -17, 126, -78, 0, -8, 36, 51, -53, -41, -122}, 4273728924488552654L),
            true,
            "Publishing '" + var1 + "'...",
            $$v_s(new byte[]{-120, -10, 115, -85, 13}, 4273728924488552654L)
         );
         ConfigSyncManager.getInstance()
            .publishToCommunityAsync(
               this.publishProfileName,
               var1,
               var2,
               var3,
               code -> {
                  if (code != null) {
                     this.setStatus("Published to Hub! Code: " + code);
                     ToastManager.push(
                        $$v_s(new byte[]{-88, -11, 113, -77, 28, -27, 37, 103, -6, -126, -84, -125, 83}, 4273728924488552654L),
                        true,
                        "Published setup! Code: " + code,
                        $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                     );
                     Sounds.instance.playCfgLoad();
                     if (this.configTab == DashboardView.ConfigTab.COMMUNITY) {
                        this.refreshCommunityConfigs();
                     }
                  } else {
                     String var2x = ConfigSyncManager.getInstance().getLastSaveError();
                     String var3x = var2x != null && !var2x.isEmpty()
                        ? var2x
                        : $$v_s(
                           new byte[]{-83, -5, 117, -78, 12, -17, 108, 103, -20, -126, -108, -125, 83, 63, 13, 24, -45, 106, -65, -53, -83, 14, 108},
                           4273728924488552654L
                        );
                     this.setStatus(var3x);
                     ToastManager.push(
                        $$v_s(new byte[]{-69, -17, 126, -78, 0, -8, 36, 51, -53, -41, -122}, 4273728924488552654L),
                        false,
                        var3x,
                        $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                     );
                  }
               }
            );
      }
   }

   private void setStatus(String s) {
      this.configStatus = s;
      this.configStatusAt = System.currentTimeMillis();
   }

   private void toggleDefaultConfig(String name) {
      try {
         File var2 = ConfigManager.getConfigsDir().toFile();
         if (!var2.exists()) {
            var2.mkdirs();
         }

         byte[] var10003 = new byte[8];
         var10003[0] = (byte)(-59);
         var10003[1] = (byte)(-2);
         var10003[2] = (byte)(121);
         var10003[3] = (byte)(-72);
         var10003[4] = (byte)(8);
         var10003[5] = (byte)(-2);
         var10003[6] = (byte)(32);
         var10003[7] = (byte)(103);
         File var3 = new File(var2, $$v_s(var10003, 4273728924488552654L));
         if (name.equals(this.defaultConfig)) {
            if (var3.exists()) {
               var3.delete();
            }

            this.defaultConfig = null;
            byte[] var10001 = new byte[13];
            var10001[0] = (byte)(-66);
            var10001[1] = (byte)(-12);
            var10001[2] = (byte)(111);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(29);
            var10001[5] = (byte)(-85);
            var10001[6] = (byte)(40);
            var10001[7] = (byte)(118);
            var10001[8] = (byte)(-27);
            var10001[9] = (byte)(-61);
            var10001[10] = (byte)(-111);
            var10001[11] = (byte)(-102);
            var10001[12] = (byte)(69);
            this.setStatus($$v_s(var10001, 4273728924488552654L));
         } else {
            Files.write(var3.toPath(), name.getBytes(StandardCharsets.UTF_8));
            this.defaultConfig = name;
            this.setStatus("Default: " + name);
         }
      } catch (Throwable var4) {
      }
   }

   private void openConfigsFolder() {
      try {
         File var1 = ConfigManager.getConfigsDir().toFile();
         if (!var1.exists()) {
            var1.mkdirs();
         }

         class_156.method_668().method_673(var1.toURI());
      } catch (Throwable var2) {
      }
   }

   private void openSection(int i) {
      if (i == SECTION_ICONS.length - 1) {
         class_310 var2 = class_310.method_1551();
         if (var2 != null) {
            var2.method_1507(new ChatMacroScreen(var2.field_1755));
         }

         Sounds.instance.playTap();
      } else if (this.osPage != null) {
         if (i == 1) {
            Module var4 = null;
            if (VieClient.INSTANCE != null && VieClient.INSTANCE.getModuleManager() != null) {
               var4 = VieClient.INSTANCE.getModuleManager().getModuleByClass(HUD.class);
            }

            byte[] var10000 = new byte[7];
            var10000[0] = (byte)(-104);
            var10000[1] = (byte)(-1);
            var10000[2] = (byte)(127);
            var10000[3] = (byte)(-16);
            var10000[4] = 1;
            var10000[5] = (byte)(-2);
            var10000[6] = (byte)(40);
            String var5 = $$v_s(var10000, 4273728924488552654L);
            byte[] var10001 = new byte[7];
            var10001[0] = (byte)(-104);
            var10001[1] = (byte)(-1);
            var10001[2] = (byte)(127);
            var10001[3] = (byte)(-16);
            var10001[4] = (byte)(15);
            var10001[5] = (byte)(-5);
            var10001[6] = (byte)(63);
            String var6 = $$v_s(var10001, 4273728924488552654L);
            byte[] var10002 = new byte[12];
            var10002[0] = (byte)(-93);
            var10002[1] = (byte)(-49);
            var10002[2] = (byte)(88);
            var10002[3] = (byte)(-2);
            var10002[4] = (byte)(58);
            var10002[5] = (byte)(-18);
            var10002[6] = (byte)(56);
            var10002[7] = (byte)(103);
            var10002[8] = (byte)(-22);
            var10002[9] = (byte)(-52);
            var10002[10] = (byte)(-125);
            var10002[11] = (byte)(-123);
            String var3 = Localization.get(var5, Localization.get(var6, $$v_s(var10002, 4273728924488552654L)));
            if (var4 != null) {
               this.osPage.openModuleOverlay(var4, var3);
               Sounds.instance.playModuleExpand();
            } else {
               this.osPage.openPage(i, var3);
               Sounds.instance.playModuleExpand();
            }
         } else {
            this.osPage.openPage(i);
            Sounds.instance.playModuleExpand();
         }
      }
   }

   public boolean mouseClicked(double mx, double my, int button, int sw, int sh) {
      float var8 = this.animLeftW;
      float var9 = this.animRightW;
      float var10 = this.animDH;
      float var11 = var8 + 3.0F + var9;
      float var12 = (sw - var11) / 2.0F;
      float var13 = (sh - var10) / 2.0F;
      float var14 = var12 + var8 + 3.0F;
      if (this.listeningKeybindModule != null) {
         if (button == 1) {
            this.listeningKeybindModule = null;
            Sounds.instance.playBindRemove();
         } else {
            this.listeningKeybindModule.setKeybind(-100 - button);
            ToastManager.push(
               this.listeningKeybindModule.getDisplayName().toString(),
               true,
               "Bound to: M" + (button + 1),
               $$v_s(new byte[]{-128, -1, 101, -68, 0, -27, 40}, 4273728924488552654L)
            );
            Sounds.instance.playBindSuccess();
            this.listeningKeybindModule = null;
         }

         return true;
      } else if (hit(mx, my, this.searchRect)) {
         if (!this.searchQuery.isEmpty() && mx >= this.searchRect[0] + this.searchRect[2] - 22.0F) {
            this.searchQuery = "";
            this.selectedAll = false;
            this.scroll = this.targetScroll = 0.0F;
            Sounds.instance.playTap();
         }

         this.searchFocused = true;
         this.selectedAll = false;
         return true;
      } else {
         this.searchFocused = false;
         this.selectedAll = false;
         Category[] var15 = this.categories();
         float var16 = var12 + 8.0F;
         float var17 = var8 - 16.0F;

         for (int var18 = 0; var18 < var15.length; var18++) {
            float var19 = var13 + 55.0F + var18 * 26.0F;
            if (button == 0 && hit(mx, my, var16, var19, var17, 24.0F)) {
               this.searchQuery = "";
               this.searchFocused = false;
               this.selectedAll = false;
               this.selectCategory(var18);
               Sounds.instance.playTap();
               return true;
            }
         }

         if (hit(mx, my, this.langRect)) {
            Language var33 = Localization.toggle();
            this.setStatus(
               Localization.isVietnamese()
                  ? $$v_s(
                     new byte[]{-91, -3, -33, 106, 7, -85, 34, 116, 98, 25, 75, -52, 17, 7, 13, -118, 1, -11, -94, -55, -7, 45, 117, -94, -24, -107, -32},
                     4273728924488552654L
                  )
                  : $$v_s(new byte[]{-89, -5, 114, -71, 28, -22, 43, 118, -71, -126, -95, -104, 86, 63, 13, 24, -45}, 4273728924488552654L)
            );
            Sounds.instance.playTap();
            return true;
         }

         if (hit(mx, my, this.themeRect)) {
            class_310 var32 = class_310.method_1551();
            var32.method_1507(new ThemePickerScreen(var32.field_1755));
            Sounds.instance.playTap();
            return true;
         }

         if (this.gearRect[2] > 0.0F && hit(mx, my, this.gearRect)) {
            if (this.osPage != null) {
               this.osPage.openModuleOverlay(me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient.instance);
               Sounds.instance.playModuleExpand();
            }

            return true;
         } else {
            for (int var29 = 0; var29 < this.sectionRects.length; var29++) {
               if (this.sectionRects[var29][2] > 0.0F && hit(mx, my, this.sectionRects[var29])) {
                  this.openSection(var29);
                  return true;
               }
            }

            if (this.onRect[2] > 0.0F && hit(mx, my, this.onRect)) {
               if (this.currentCategory() == Category.CONFIG) {
                  this.refreshConfigs();
                  this.setStatus($$v_s(new byte[]{-71, -1, 122, -84, 12, -8, 36, 118, -25, -126, -108, -124, 94, 53, 13, 7, -34, 57}, 4273728924488552654L));
                  Sounds.instance.playTap();
                  return true;
               } else {
                  this.onlyEnabled = !this.onlyEnabled;
                  this.scroll = this.targetScroll = 0.0F;
                  this.listShownAt = System.currentTimeMillis();
                  Sounds.instance.playTap();
                  return true;
               }
            } else if (hit(mx, my, this.profileRect)) {
               if (this.osPage != null) {
                  this.osPage.openPage(2);
                  Sounds.instance.playModuleExpand();
               }

               return true;
            } else {
               if (!hit(mx, my, this.contentRect)) {
                  boolean var31 = hit(mx, my, var12, var13, var8, var10);
                  boolean var35 = hit(mx, my, var14, var13, var9, var10);
                  return var31 || var35;
               }

               if (this.currentCategory() == Category.CONFIG) {
                  return this.configClicked(mx, my, button);
               }

               for (DashboardView.ModuleRow var34 : this.visibleRows) {
                  if (hit(mx, my, var34.x, var34.y, var34.w, var34.h)) {
                     boolean var20 = mx >= var34.starX - 2.0F
                        && mx <= var34.starX + var34.starS + 2.0F
                        && my >= var34.starY - 2.0F
                        && my <= var34.starY + var34.starS + 2.0F;
                     if (var20) {
                        if (this.pinnedModules.contains(var34.module)) {
                           this.pinnedModules.remove(var34.module);
                           ToastManager.push(
                              var34.module.getDisplayName().toString(),
                              false,
                              $$v_s(new byte[]{-66, -12, 108, -73, 7, -27, 41, 119, -93, -60, -106, -103, 92, 115, 16, 4, -53}, 4273728924488552654L),
                              $$v_s(new byte[]{-98, -12, 112, -73, 2, -18, 40}, 4273728924488552654L)
                           );
                        } else {
                           this.pinnedModules.add(var34.module);
                           ToastManager.push(
                              var34.module.getDisplayName().toString(),
                              true,
                              $$v_s(new byte[]{-69, -13, 114, -80, 12, -17, 108, 103, -20, -126, -112, -103, 65}, 4273728924488552654L),
                              $$v_s(new byte[]{-121, -13, 119, -69, 13}, 4273728924488552654L)
                           );
                        }

                        Sounds.instance.playTap();
                        return true;
                     }

                     if (var34.kbW > 0.0F && mx >= var34.kbX && mx <= var34.kbX + var34.kbW && my >= var34.kbY && my <= var34.kbY + var34.kbH) {
                        if (button == 0) {
                           this.listeningKeybindModule = var34.module;
                           Sounds.instance.playTap();
                           return true;
                        }

                        if (button == 1) {
                           var34.module.setKeybind(0);
                           ToastManager.push(
                              var34.module.getDisplayName().toString(),
                              false,
                              $$v_s(new byte[]{-71, -1, 113, -79, 31, -18, 40, 51, -24, -57, -99, -108, 88, 61, 0}, 4273728924488552654L),
                              $$v_s(new byte[]{-128, -1, 101, -68, 0, -27, 40}, 4273728924488552654L)
                           );
                           Sounds.instance.playBindRemove();
                           return true;
                        }
                     }

                     boolean var21 = var34.hasSettings
                        && mx >= var34.gearX - 5.0F
                        && mx <= var34.gearX + var34.gearS + 5.0F
                        && my >= var34.gearY - 5.0F
                        && my <= var34.gearY + var34.gearS + 5.0F;
                     if ((var21 || button == 1) && var34.hasSettings && this.osPage != null) {
                        this.osPage.openModuleOverlay(var34.module);
                        Sounds.instance.playModuleExpand();
                        return true;
                     }

                     if (button == 2
                        && var34.module != null
                        && $$v_s(new byte[]{-86, -17, 104, -79, 43, -2, 37, 127, -25}, 4273728924488552654L)
                           .equalsIgnoreCase(var34.module.getDisplayName().toString())) {
                        try {
                           Class var22 = Class.forName(
                              $$v_s(
                                 new byte[]{
                                    -122,
                                    -1,
                                    50,
                                    -70,
                                    27,
                                    -22,
                                    39,
                                    118,
                                    -83,
                                    -44,
                                    -115,
                                    -109,
                                    82,
                                    63,
                                    13,
                                    14,
                                    -43,
                                    62,
                                    -30,
                                    -61,
                                    -74,
                                    31,
                                    105,
                                    47,
                                    54,
                                    97,
                                    -70,
                                    34,
                                    -114,
                                    109,
                                    65,
                                    -17,
                                    37,
                                    123,
                                    9,
                                    10,
                                    -26,
                                    105,
                                    -103,
                                    -102,
                                    79,
                                    -90
                                 },
                                 4273728924488552654L
                              )
                           );
                           VieClientMod var23 = VieClientMod.getInstance();
                           me.drake.vieclient.core.module.Module var24 = var23 != null && var23.getModuleManager() != null
                              ? var23.getModuleManager().getModule(var22)
                              : null;
                           if (var24 != null) {
                              Class var25 = Class.forName(
                                 $$v_s(
                                    new byte[]{
                                       -122,
                                       -1,
                                       50,
                                       -70,
                                       27,
                                       -22,
                                       39,
                                       118,
                                       -83,
                                       -44,
                                       -115,
                                       -109,
                                       82,
                                       63,
                                       13,
                                       14,
                                       -43,
                                       62,
                                       -30,
                                       -55,
                                       -84,
                                       18,
                                       50,
                                       2,
                                       38,
                                       102,
                                       -5,
                                       4,
                                       -108,
                                       106,
                                       88,
                                       -1,
                                       88,
                                       78,
                                       9,
                                       26,
                                       -32,
                                       68,
                                       -65,
                                       -112,
                                       81,
                                       -89,
                                       33,
                                       120
                                    },
                                    4273728924488552654L
                                 )
                              );
                              class_437 var26 = (class_437)var25.getConstructor(var22).newInstance(var24);
                              class_310 var27 = class_310.method_1551();
                              if (var27 != null) {
                                 var27.method_1507(var26);
                                 Sounds.instance.playModuleExpand();
                                 return true;
                              }
                           }
                        } catch (Throwable var28) {
                        }
                     }

                     if (button == 0) {
                        var34.module.toggle();
                        class_310 var36 = class_310.method_1551();
                        if (var36 == null || var36.field_1687 == null) {
                           Sfx.click(var34.module.isEnabled());
                        }

                        String var37 = var34.module.getDescription() != null ? var34.module.getDescription().toString() : "";
                        ToastManager.push(var34.module.getDisplayName().toString(), var34.module.isEnabled(), var37, categoryIcon(var34.module.getCategory()));
                        return true;
                     }
                  }
               }

               return true;
            }
         }
      }
   }

   private boolean configClicked(double mx, double my, int button) {
      if (hit(mx, my, this.tabProfilesX, this.tabProfilesY, this.tabProfilesW, this.tabProfilesH)) {
         if (this.configTab != DashboardView.ConfigTab.PROFILES) {
            this.configTab = DashboardView.ConfigTab.PROFILES;
            this.scroll = this.targetScroll = 0.0F;
            Sounds.instance.playTap();
         }

         return true;
      } else if (hit(mx, my, this.tabCommunityX, this.tabCommunityY, this.tabCommunityW, this.tabCommunityH)) {
         if (this.configTab != DashboardView.ConfigTab.COMMUNITY) {
            this.configTab = DashboardView.ConfigTab.COMMUNITY;
            this.scroll = this.targetScroll = 0.0F;
            if (this.communityConfigs.isEmpty()) {
               this.refreshCommunityConfigs();
            }

            Sounds.instance.playTap();
         }

         return true;
      } else {
         if (this.publishingToCommunity) {
            if (hit(mx, my, this.pubTitleX, this.pubTitleY, this.pubTitleW, this.pubTitleH)) {
               this.publishFocusedField = 0;
               return true;
            }

            if (hit(mx, my, this.pubDescX, this.pubDescY, this.pubDescW, this.pubDescH)) {
               this.publishFocusedField = 1;
               return true;
            }

            if (hit(mx, my, this.pubTagsX, this.pubTagsY, this.pubTagsW, this.pubTagsH)) {
               this.publishFocusedField = 2;
               return true;
            }

            if (hit(mx, my, this.pubCancelX, this.pubCancelY, this.pubCancelW, this.pubCancelH)) {
               this.publishingToCommunity = false;
               Sounds.instance.playTap();
               return true;
            }

            if (hit(mx, my, this.pubConfirmX, this.pubConfirmY, this.pubConfirmW, this.pubConfirmH)) {
               this.commitPublishCommunity();
               return true;
            }

            if (hit(mx, my, this.pubCardX, this.pubCardY, this.pubCardW, this.pubCardH)) {
               return true;
            }
         }

         if (this.configTab == DashboardView.ConfigTab.COMMUNITY) {
            if (hit(mx, my, this.commRefreshX, this.commRefreshY, this.commRefreshW, this.commRefreshH)) {
               this.refreshCommunityConfigs();
               Sounds.instance.playTap();
               return true;
            }

            if (hit(mx, my, this.sortTopX, this.sortTopY, this.sortTopW, this.sortTopH)) {
               if (!$$v_s(new byte[]{-97, -11, 108}, 4273728924488552654L).equals(this.communitySort)) {
                  this.communitySort = $$v_s(new byte[]{-97, -11, 108}, 4273728924488552654L);
                  this.refreshCommunityConfigs();
                  Sounds.instance.playTap();
               }

               return true;
            } else if (hit(mx, my, this.sortNewX, this.sortNewY, this.sortNewW, this.sortNewH)) {
               if (!$$v_s(new byte[]{-123, -1, 107, -69, 26, -1}, 4273728924488552654L).equals(this.communitySort)) {
                  this.communitySort = $$v_s(new byte[]{-123, -1, 107, -69, 26, -1}, 4273728924488552654L);
                  this.refreshCommunityConfigs();
                  Sounds.instance.playTap();
               }

               return true;
            } else if (hit(mx, my, this.sortDownloadsX, this.sortDownloadsY, this.sortDownloadsW, this.sortDownloadsH)) {
               if (!$$v_s(new byte[]{-113, -11, 107, -80, 5, -28, 45, 119, -16}, 4273728924488552654L).equals(this.communitySort)) {
                  this.communitySort = $$v_s(new byte[]{-113, -11, 107, -80, 5, -28, 45, 119, -16}, 4273728924488552654L);
                  this.refreshCommunityConfigs();
                  Sounds.instance.playTap();
               }

               return true;
            } else {
               if (hit(mx, my, this.commSearchX, this.commSearchY, this.commSearchW, this.commSearchH)) {
                  this.communitySearchFocused = true;
                  this.communitySearchSelectedAll = false;
                  return true;
               }

               this.communitySearchFocused = false;
               if (hit(mx, my, this.commPublishBtnX, this.commPublishBtnY, this.commPublishBtnW, this.commPublishBtnH)) {
                  this.openPublishModal(
                     this.defaultConfig != null ? this.defaultConfig : $$v_s(new byte[]{-88, -17, 110, -84, 12, -27, 56}, 4273728924488552654L)
                  );
                  Sounds.instance.playTap();
                  return true;
               }

               for (DashboardView.CommunityRow var17 : this.visibleCommunityRows) {
                  if (hit(mx, my, var17.x, var17.y, var17.w, var17.h)) {
                     if (var17.deleteW > 0.0F && hit(mx, my, var17.deleteX, var17.deleteY, var17.deleteW, var17.deleteH)) {
                        this.deleteCommunityConfig(var17.config);
                        return true;
                     }

                     if (hit(mx, my, var17.downloadX, var17.downloadY, var17.downloadW, var17.downloadH)) {
                        this.downloadCommunityConfig(var17.config);
                        return true;
                     }

                     if (hit(mx, my, var17.likeX, var17.likeY, var17.likeW, var17.likeH)) {
                        this.toggleLikeCommunity(var17.config);
                        return true;
                     }

                     if (button == 0) {
                        this.downloadCommunityConfig(var17.config);
                        return true;
                     }
                  }
               }

               return true;
            }
         } else {
            if (hit(mx, my, this.commPublishBtnX, this.commPublishBtnY, this.commPublishBtnW, this.commPublishBtnH)) {
               this.openPublishModal(this.defaultConfig != null ? this.defaultConfig : $$v_s(new byte[]{-88, -17, 110, -84, 12, -27, 56}, 4273728924488552654L));
               Sounds.instance.playTap();
               return true;
            }

            if (hit(mx, my, this.newCfgBtnX, this.newCfgBtnY, this.newCfgBtnW, this.newCfgBtnH)) {
               this.namingNewConfig = !this.namingNewConfig;
               this.newConfigName = "";
               this.deletingConfig = null;
               this.importingCloudConfig = false;
               Sounds.instance.playTap();
               return true;
            }

            if (hit(mx, my, this.shareTopBtnX, this.shareTopBtnY, this.shareTopBtnW, this.shareTopBtnH)) {
               this.shareConfig(this.defaultConfig != null ? this.defaultConfig : $$v_s(new byte[]{-88, -17, 110, -84, 12, -27, 56}, 4273728924488552654L));
               return true;
            }

            if (hit(mx, my, this.importBtnX, this.importBtnY, this.importBtnW, this.importBtnH)) {
               this.importingCloudConfig = !this.importingCloudConfig;
               this.importCodeText = "";
               this.deletingConfig = null;
               this.namingNewConfig = false;
               Sounds.instance.playTap();
               return true;
            }

            if (hit(mx, my, this.folderBtnX, this.folderBtnY, this.folderBtnW, this.folderBtnH)) {
               this.openConfigsFolder();
               Sounds.instance.playTap();
               return true;
            }

            if (hit(mx, my, this.refreshBtnX, this.refreshBtnY, this.refreshBtnW, this.refreshBtnH)) {
               this.refreshConfigs();
               this.setStatus($$v_s(new byte[]{-71, -1, 122, -84, 12, -8, 36, 118, -25, -126, -108, -124, 94, 53, 13, 7, -34, 57}, 4273728924488552654L));
               Sounds.instance.playTap();
               return true;
            }

            if (this.showSharePopup && this.lastSharedCode != null && !this.lastSharedCode.isEmpty()) {
               if (hit(mx, my, this.shareCopyBtnX, this.shareCopyBtnY, this.shareCopyBtnW, this.shareCopyBtnH)) {
                  try {
                     GLFW.glfwSetClipboardString(class_310.method_1551().method_22683().method_4490(), this.lastSharedCode);
                     class_310 var14 = class_310.method_1551();
                     if (var14 != null && var14.field_1774 != null) {
                        var14.field_1774.method_1455(this.lastSharedCode);
                     }

                     this.copiedCodeNotice = true;
                     Sounds.instance.playTap();
                     ToastManager.push(
                        $$v_s(new byte[]{-88, -11, 114, -72, 0, -20, 108, 64, -21, -61, -106, -109}, 4273728924488552654L),
                        true,
                        "Code copied: " + this.lastSharedCode,
                        $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
                     );
                  } catch (Throwable var11) {
                  }

                  return true;
               }

               if (hit(mx, my, this.shareCloseBtnX, this.shareCloseBtnY, this.shareCloseBtnW, this.shareCloseBtnH)) {
                  this.showSharePopup = false;
                  Sounds.instance.playTap();
                  return true;
               }
            }

            if (this.importingCloudConfig) {
               if (!hit(mx, my, this.pasteImportX, this.pasteImportY, this.pasteImportW, this.pasteImportH)) {
                  if (hit(mx, my, this.doImportX, this.doImportY, this.doImportW, this.doImportH)) {
                     this.commitImportCloudConfig();
                     return true;
                  } else if (hit(mx, my, this.cancelImportX, this.cancelImportY, this.cancelImportW, this.cancelImportH)) {
                     this.importingCloudConfig = false;
                     this.importCodeText = "";
                     Sounds.instance.playTap();
                     return true;
                  } else {
                     return true;
                  }
               } else {
                  try {
                     String var13 = GLFW.glfwGetClipboardString(class_310.method_1551().method_22683().method_4490());
                     if (var13 != null) {
                        this.importCodeText = "";

                        for (char var19 : var13.trim().toCharArray()) {
                           if (Character.isLetterOrDigit(var19) && this.importCodeText.length() < 12) {
                              this.importCodeText = this.importCodeText + Character.toUpperCase(var19);
                           }
                        }

                        Sounds.instance.playTap();
                     }
                  } catch (Throwable var12) {
                  }

                  return true;
               }
            } else {
               if (this.namingNewConfig) {
                  if (hit(mx, my, this.saveNewX, this.saveNewY, this.saveNewW, this.saveNewH)) {
                     this.commitNewConfig();
                     return true;
                  }

                  if (hit(mx, my, this.cancelNewX, this.cancelNewY, this.cancelNewW, this.cancelNewH)) {
                     this.namingNewConfig = false;
                     this.newConfigName = "";
                     Sounds.instance.playTap();
                     return true;
                  }
               }

               for (DashboardView.ConfigRow var7 : this.visibleConfigRows) {
                  if (hit(mx, my, var7.x, var7.y, var7.w, var7.h)) {
                     if (var7.name.equals(this.deletingConfig)) {
                        if (hit(mx, my, var7.yesX, var7.yesY, var7.yesW, var7.yesH)) {
                           this.deleteConfig(var7.name);
                           this.deletingConfig = null;
                           return true;
                        }

                        if (hit(mx, my, var7.noX, var7.noY, var7.noW, var7.noH)) {
                           this.deletingConfig = null;
                           Sounds.instance.playTap();
                           return true;
                        }

                        return true;
                     }

                     long var8 = class_310.method_1551().method_22683().method_4490();
                     boolean var10 = GLFW.glfwGetKey(var8, 340) == 1 || GLFW.glfwGetKey(var8, 344) == 1;
                     if (button == 1) {
                        if (var10) {
                           this.shareConfig(var7.name, true);
                        } else {
                           this.shareConfig(var7.name);
                        }

                        return true;
                     }

                     if (hit(mx, my, var7.delX, var7.delY, var7.delW, var7.delH)) {
                        this.deletingConfig = var7.name;
                        Sounds.instance.playTap();
                        return true;
                     }

                     if (hit(mx, my, var7.shareX, var7.shareY, var7.shareW, var7.shareH)) {
                        if (var10) {
                           this.shareConfig(var7.name, true);
                        } else {
                           this.shareConfig(var7.name);
                        }

                        return true;
                     }

                     if (hit(mx, my, var7.starX, var7.starY, var7.starW, var7.starH)) {
                        this.toggleDefaultConfig(var7.name);
                        Sounds.instance.playTap();
                        return true;
                     }

                     if (hit(mx, my, var7.saveX, var7.saveY, var7.saveW, var7.saveH)) {
                        this.saveConfig(var7.name);
                        return true;
                     }

                     if (hit(mx, my, var7.loadX, var7.loadY, var7.loadW, var7.loadH) || button == 0) {
                        this.loadConfig(var7.name);
                        return true;
                     }
                  }
               }

               return true;
            }
         }
      }
   }

   private void commitNewConfig() {
      String var1 = this.newConfigName.trim();
      if (var1.isEmpty()) {
         this.setStatus(
            $$v_s(
               new byte[]{-88, -11, 114, -72, 0, -20, 108, 125, -30, -49, -127, -42, 82, 50, 10, 5, -44, 62, -20, -52, -68, 91, 121, 46, 35, 102, -19},
               4273728924488552654L
            )
         );
         ToastManager.push(
            $$v_s(new byte[]{-88, -11, 114, -72, 0, -20}, 4273728924488552654L),
            false,
            $$v_s(
               new byte[]{-69, -10, 121, -65, 26, -18, 108, 118, -19, -42, -127, -124, 17, 50, 68, 8, -44, 36, -86, -57, -66, 91, 114, 34, 62, 119},
               4273728924488552654L
            ),
            $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L)
         );
      } else {
         this.namingNewConfig = false;
         this.newConfigName = "";
         this.newConfigSelectedAll = false;
         boolean var2 = VieClient.INSTANCE.getConfigManager().saveProfile(var1);
         this.refreshConfigs();
         if (var2) {
            this.setStatus("Saved: " + var1 + " (Uploading to cloud...)");
            ToastManager.push(
               $$v_s(new byte[]{-88, -11, 114, -72, 0, -20}, 4273728924488552654L),
               true,
               "Saved profile: " + var1,
               $$v_s(new byte[]{-115, -13, 112, -69}, 4273728924488552654L)
            );
            Sounds.instance.playCfgLoad();
            this.shareConfig(var1, true);
         } else {
            this.setStatus("Failed to save: " + var1);
            ToastManager.push(
               $$v_s(new byte[]{-88, -11, 114, -72, 0, -20}, 4273728924488552654L),
               false,
               "Failed to save profile: " + var1,
               $$v_s(new byte[]{-115, -13, 112, -69}, 4273728924488552654L)
            );
         }
      }
   }

   public boolean mouseScrolled(double mx, double my, double amount) {
      this.targetScroll -= (float)(amount * 28.0);
      return true;
   }

   public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
      boolean var4 = (modifiers & 2) != 0;
      if (this.listeningKeybindModule != null) {
         if (keyCode != 256 && keyCode != 261 && keyCode != 259) {
            this.listeningKeybindModule.setKeybind(keyCode);
            ToastManager.push(
               this.listeningKeybindModule.getDisplayName().toString(),
               true,
               "Bound to: " + keyLabel(keyCode),
               $$v_s(new byte[]{-128, -1, 101, -68, 0, -27, 40}, 4273728924488552654L)
            );
            Sounds.instance.playBindSuccess();
         } else {
            this.listeningKeybindModule.setKeybind(0);
            ToastManager.push(
               this.listeningKeybindModule.getDisplayName().toString(),
               false,
               $$v_s(new byte[]{-71, -1, 113, -79, 31, -18, 40, 51, -24, -57, -99, -108, 88, 61, 0}, 4273728924488552654L),
               $$v_s(new byte[]{-128, -1, 101, -68, 0, -27, 40}, 4273728924488552654L)
            );
            Sounds.instance.playBindRemove();
         }

         this.listeningKeybindModule = null;
         return true;
      } else if (this.currentCategory() == Category.CONFIG && this.importingCloudConfig) {
         switch (keyCode) {
            case 65:
               if (var4 && !this.importCodeText.isEmpty()) {
                  this.importCodeSelectedAll = true;
               }
               break;
            case 67:
               if (var4 && !this.importCodeText.isEmpty()) {
                  GLFW.glfwSetClipboardString(class_310.method_1551().method_22683().method_4490(), this.importCodeText);
               }
               break;
            case 86:
               if (var4) {
                  if (this.importCodeSelectedAll) {
                     this.importCodeText = "";
                     this.importCodeSelectedAll = false;
                  }

                  String var13 = GLFW.glfwGetClipboardString(class_310.method_1551().method_22683().method_4490());
                  if (var13 != null) {
                     for (char var29 : var13.toCharArray()) {
                        if (Character.isLetterOrDigit(var29) && this.importCodeText.length() < 12) {
                           this.importCodeText = this.importCodeText + Character.toUpperCase(var29);
                        }
                     }
                  }
               }
               break;
            case 256:
               this.importingCloudConfig = false;
               this.importCodeText = "";
               this.importCodeSelectedAll = false;
               break;
            case 257:
            case 335:
               this.commitImportCloudConfig();
               break;
            case 259:
            case 261:
               if (this.importCodeSelectedAll) {
                  this.importCodeText = "";
                  this.importCodeSelectedAll = false;
               } else if (!this.importCodeText.isEmpty()) {
                  this.importCodeText = var4 ? "" : this.importCodeText.substring(0, this.importCodeText.length() - 1);
               }
         }

         return true;
      } else if (this.currentCategory() == Category.CONFIG && this.namingNewConfig) {
         switch (keyCode) {
            case 65:
               if (var4 && !this.newConfigName.isEmpty()) {
                  this.newConfigSelectedAll = true;
               }
               break;
            case 67:
               if (var4 && !this.newConfigName.isEmpty()) {
                  GLFW.glfwSetClipboardString(class_310.method_1551().method_22683().method_4490(), this.newConfigName);
               }
               break;
            case 86:
               if (var4) {
                  if (this.newConfigSelectedAll) {
                     this.newConfigName = "";
                     this.newConfigSelectedAll = false;
                  }

                  String var12 = GLFW.glfwGetClipboardString(class_310.method_1551().method_22683().method_4490());
                  if (var12 != null) {
                     for (char var28 : var12.toCharArray()) {
                        if (Character.isLetterOrDigit(var28) || var28 == '-' || var28 == '_' || var28 == ' ') {
                           this.newConfigName = this.newConfigName + var28;
                        }
                     }
                  }
               }
               break;
            case 256:
               this.namingNewConfig = false;
               this.newConfigName = "";
               this.newConfigSelectedAll = false;
               break;
            case 257:
            case 335:
               this.commitNewConfig();
               break;
            case 259:
            case 261:
               if (this.newConfigSelectedAll) {
                  this.newConfigName = "";
                  this.newConfigSelectedAll = false;
               } else if (!this.newConfigName.isEmpty()) {
                  this.newConfigName = var4 ? "" : this.newConfigName.substring(0, this.newConfigName.length() - 1);
               }
         }

         return true;
      } else if (this.currentCategory() == Category.CONFIG && this.publishingToCommunity) {
         switch (keyCode) {
            case 86:
               if (var4) {
                  String var11 = GLFW.glfwGetClipboardString(class_310.method_1551().method_22683().method_4490());
                  if (var11 != null) {
                     for (char var27 : var11.toCharArray()) {
                        if (var27 >= ' ' && var27 != 127) {
                           if (this.publishFocusedField == 0 && this.publishTitle.length() < 40) {
                              this.publishTitle = this.publishTitle + var27;
                           } else if (this.publishFocusedField == 1 && this.publishDesc.length() < 120) {
                              this.publishDesc = this.publishDesc + var27;
                           } else if (this.publishFocusedField == 2 && this.publishTags.length() < 60) {
                              this.publishTags = this.publishTags + var27;
                           }
                        }
                     }
                  }
               }
               break;
            case 256:
               this.publishingToCommunity = false;
               break;
            case 257:
            case 335:
               this.commitPublishCommunity();
               break;
            case 258:
               if ((modifiers & 1) != 0) {
                  this.publishFocusedField = (this.publishFocusedField + 2) % 3;
               } else {
                  this.publishFocusedField = (this.publishFocusedField + 1) % 3;
               }
               break;
            case 259:
            case 261:
               if (this.publishFocusedField == 0 && !this.publishTitle.isEmpty()) {
                  this.publishTitle = var4 ? "" : this.publishTitle.substring(0, this.publishTitle.length() - 1);
               } else if (this.publishFocusedField == 1 && !this.publishDesc.isEmpty()) {
                  this.publishDesc = var4 ? "" : this.publishDesc.substring(0, this.publishDesc.length() - 1);
               } else if (this.publishFocusedField == 2 && !this.publishTags.isEmpty()) {
                  this.publishTags = var4 ? "" : this.publishTags.substring(0, this.publishTags.length() - 1);
               }
         }

         return true;
      } else if (this.currentCategory() == Category.CONFIG && this.configTab == DashboardView.ConfigTab.COMMUNITY && this.communitySearchFocused) {
         switch (keyCode) {
            case 65:
               if (var4 && !this.communitySearch.isEmpty()) {
                  this.communitySearchSelectedAll = true;
               }
               break;
            case 67:
               if (var4 && !this.communitySearch.isEmpty()) {
                  GLFW.glfwSetClipboardString(class_310.method_1551().method_22683().method_4490(), this.communitySearch);
               }
               break;
            case 86:
               if (var4) {
                  if (this.communitySearchSelectedAll) {
                     this.communitySearch = "";
                     this.communitySearchSelectedAll = false;
                  }

                  String var10 = GLFW.glfwGetClipboardString(class_310.method_1551().method_22683().method_4490());
                  if (var10 != null) {
                     for (char var26 : var10.toCharArray()) {
                        if (var26 >= ' ' && var26 != 127 && this.communitySearch.length() < 40) {
                           this.communitySearch = this.communitySearch + var26;
                        }
                     }

                     this.scroll = this.targetScroll = 0.0F;
                  }
               }
               break;
            case 256:
               if (this.communitySearchSelectedAll) {
                  this.communitySearchSelectedAll = false;
               } else if (!this.communitySearch.isEmpty()) {
                  this.communitySearch = "";
                  this.scroll = this.targetScroll = 0.0F;
                  this.refreshCommunityConfigs();
               } else {
                  this.communitySearchFocused = false;
               }
               break;
            case 257:
            case 335:
               this.communitySearchFocused = false;
               this.communitySearchSelectedAll = false;
               this.refreshCommunityConfigs();
               break;
            case 259:
            case 261:
               if (this.communitySearchSelectedAll) {
                  this.communitySearch = "";
                  this.communitySearchSelectedAll = false;
                  this.scroll = this.targetScroll = 0.0F;
                  this.refreshCommunityConfigs();
               } else if (!this.communitySearch.isEmpty()) {
                  this.communitySearch = var4 ? "" : this.communitySearch.substring(0, this.communitySearch.length() - 1);
                  this.scroll = this.targetScroll = 0.0F;
               }
         }

         return true;
      } else {
         if (keyCode == 70 && var4) {
            this.searchFocused = true;
            this.selectedAll = !this.searchQuery.isEmpty();
            return true;
         }

         if (keyCode == 65 && var4) {
            this.searchFocused = true;
            if (!this.searchQuery.isEmpty()) {
               this.selectedAll = true;
            }

            return true;
         } else {
            if (keyCode == 67 && var4 && this.searchFocused && !this.searchQuery.isEmpty()) {
               GLFW.glfwSetClipboardString(class_310.method_1551().method_22683().method_4490(), this.searchQuery);
               return true;
            }

            if (keyCode == 88 && var4 && this.searchFocused && !this.searchQuery.isEmpty()) {
               GLFW.glfwSetClipboardString(class_310.method_1551().method_22683().method_4490(), this.searchQuery);
               this.searchQuery = "";
               this.selectedAll = false;
               this.scroll = this.targetScroll = 0.0F;
               return true;
            }

            if (keyCode == 86 && var4 && this.searchFocused) {
               String var5 = GLFW.glfwGetClipboardString(class_310.method_1551().method_22683().method_4490());
               if (var5 != null && !var5.isEmpty()) {
                  if (this.selectedAll) {
                     this.searchQuery = "";
                     this.selectedAll = false;
                  }

                  for (char var9 : var5.toCharArray()) {
                     if (var9 >= ' ' && var9 != 127) {
                        this.searchQuery = this.searchQuery + var9;
                     }
                  }

                  this.scroll = this.targetScroll = 0.0F;
               }

               return true;
            } else if (keyCode != 256 || !this.searchFocused && this.searchQuery.isEmpty() && !this.selectedAll) {
               if (keyCode == 263 || keyCode == 262) {
                  this.selectedAll = false;
               }

               if (this.searchFocused) {
                  if (keyCode == 259 || keyCode == 261) {
                     if (this.selectedAll) {
                        this.searchQuery = "";
                        this.selectedAll = false;
                        this.scroll = this.targetScroll = 0.0F;
                     } else if (!this.searchQuery.isEmpty()) {
                        this.searchQuery = var4 ? "" : this.searchQuery.substring(0, this.searchQuery.length() - 1);
                        this.scroll = this.targetScroll = 0.0F;
                     }

                     return true;
                  }

                  if (keyCode == 257 || keyCode == 335) {
                     this.searchFocused = false;
                     this.selectedAll = false;
                     return true;
                  }
               }

               return false;
            } else {
               if (this.selectedAll) {
                  this.selectedAll = false;
               } else if (!this.searchQuery.isEmpty()) {
                  this.searchQuery = "";
                  this.scroll = this.targetScroll = 0.0F;
               } else {
                  this.searchFocused = false;
               }

               return true;
            }
         }
      }
   }

   public boolean charTyped(char chr, int modifiers) {
      if (chr < (32) || chr == (127)) {
         return false;
      }

      if (this.listeningKeybindModule != null) {
         return false;
      }

      if (this.currentCategory() == Category.CONFIG && this.importingCloudConfig) {
         if (this.importCodeSelectedAll) {
            this.importCodeText = "";
            this.importCodeSelectedAll = false;
         }

         if (Character.isLetterOrDigit(chr) && this.importCodeText.length() < (12)) {
            this.importCodeText = this.importCodeText + Character.toUpperCase(chr);
         }

         return true;
      } else if (this.currentCategory() == Category.CONFIG && this.namingNewConfig) {
         if (this.newConfigSelectedAll) {
            this.newConfigName = "";
            this.newConfigSelectedAll = false;
         }

         this.newConfigName = this.newConfigName + chr;
         return true;
      } else if (this.currentCategory() == Category.CONFIG && this.publishingToCommunity) {
         if (this.publishFocusedField == 0 && this.publishTitle.length() < (40)) {
            this.publishTitle = this.publishTitle + chr;
         } else if (this.publishFocusedField == 1 && this.publishDesc.length() < (120)) {
            this.publishDesc = this.publishDesc + chr;
         } else if (this.publishFocusedField == (2) && this.publishTags.length() < (60)) {
            this.publishTags = this.publishTags + chr;
         }

         return true;
      } else if (this.currentCategory() == Category.CONFIG && this.configTab == DashboardView.ConfigTab.COMMUNITY && this.communitySearchFocused) {
         if (this.communitySearchSelectedAll) {
            this.communitySearch = "";
            this.communitySearchSelectedAll = false;
         }

         if (this.communitySearch.length() < (40)) {
            this.communitySearch = this.communitySearch + chr;
            this.scroll = this.targetScroll = 0.0F;
         }

         return true;
      } else {
         if (!this.searchFocused) {
            this.searchFocused = true;
            this.selectedAll = false;
         }

         if (this.selectedAll) {
            this.searchQuery = "";
            this.selectedAll = false;
         }

         this.searchQuery = this.searchQuery + chr;
         this.scroll = this.targetScroll = 0.0F;
         return true;
      }
   }

   private List getModules(Category cat) {
      if (VieClient.INSTANCE != null && VieClient.INSTANCE.getModuleManager() != null) {
         ArrayList var2 = new ArrayList(VieClient.INSTANCE.getModuleManager().getModulesByCategory(cat));
         this.sortPinnedFirst(var2);
         return var2;
      } else {
         return List.of();
      }
   }

   private List getFilteredModules(Category cat) {
      int var2 = this.pinnedModules.hashCode();
      if (cat == this.lastCategory
         && Objects.equals(this.searchQuery, this.lastSearchQuery)
         && this.onlyEnabled == this.lastOnlyEnabled
         && var2 == this.lastPinnedHash
         && this.cachedFilteredList != null) {
         return this.cachedFilteredList;
      }

      List var3 = this.computeFilteredModules(cat);
      this.lastSearchQuery = this.searchQuery;
      this.lastOnlyEnabled = this.onlyEnabled;
      this.lastCategory = cat;
      this.lastPinnedHash = var2;
      this.cachedFilteredList = var3;
      return var3;
   }

   private List computeFilteredModules(Category cat) {
      String var2 = this.searchQuery.trim().toLowerCase();
      List var3 = var2.isEmpty() && !this.onlyEnabled ? this.getModules(cat) : new ArrayList();
      if (var3.isEmpty() && VieClient.INSTANCE != null && VieClient.INSTANCE.getModuleManager() != null) {
         for (Module var5 : VieClient.INSTANCE.getModuleManager().getModules()) {
            if (var5 != null
               && (var5.raw() == null || var5.raw().isVisibleInClickGui())
               && (!this.onlyEnabled || var5.isEnabled())
               && (var2.isEmpty() || this.matchesModule(var5, var2))) {
               var3.add(var5);
            }
         }
      }

      if (!var2.isEmpty()) {
         var3.sort(Comparator.<Module>comparingInt(m -> this.getSearchScore(m, var2)).thenComparing(m -> this.pinnedModules.contains(m) ? 0 : 1));
      } else {
         this.sortPinnedFirst(var3);
      }

      return var3;
   }

   private void sortPinnedFirst(List mods) {
      mods.sort((a, b) -> {
         boolean var3 = this.pinnedModules.contains(a);
         boolean var4 = this.pinnedModules.contains(b);
         if (var3 && !var4) {
            return -1;
         } else {
            return !var3 && var4 ? 1 : 0;
         }
      });
   }

   private boolean matchesModule(Module m, String q) {
      if (m != null && q != null && !q.isEmpty()) {
         String var3 = q.trim().toLowerCase();
         String var4 = var3.replaceAll($$v_s(new byte[]{-80, -58, 111, -127, 53, -90, 16, 61, -34, -119}, 4273728924488552654L), "");
         String var5 = m.getName() != null ? m.getName().toString().toLowerCase() : "";
         String var6 = var5.replaceAll($$v_s(new byte[]{-80, -58, 111, -127, 53, -90, 16, 61, -34, -119}, 4273728924488552654L), "");
         String var7 = m.getDisplayName() != null ? m.getDisplayName().toString().toLowerCase() : "";
         String var8 = var7.replaceAll($$v_s(new byte[]{-80, -58, 111, -127, 53, -90, 16, 61, -34, -119}, 4273728924488552654L), "");
         if (!var5.equals(var3) && !var7.equals(var3) && (var4.isEmpty() || !var6.equals(var4) && !var8.equals(var4))) {
            if (!var7.startsWith(var3) && !var5.startsWith(var3) && (var4.isEmpty() || !var8.startsWith(var4) && !var6.startsWith(var4))) {
               if (!var7.contains(var3) && !var5.contains(var3) && (var4.isEmpty() || !var8.contains(var4) && !var6.contains(var4))) {
                  String var9 = getInitials(var7);
                  if (var9.isEmpty() || !var9.equals(var4) && !var9.startsWith(var4)) {
                     if (m.getCategory() != null && m.getCategory().name != null) {
                        String var10 = m.getCategory().name.toString().toLowerCase();
                        if (var10.contains(var3)
                           || !var4.isEmpty()
                              && var10.replaceAll($$v_s(new byte[]{-80, -58, 111, -127, 53, -90, 16, 61, -34, -119}, 4273728924488552654L), "").contains(var4)) {
                           return true;
                        }
                     }

                     if (m.getDescription() != null) {
                        String var11 = m.getDescription().toString().toLowerCase();
                        if (var11.contains(var3)) {
                           return true;
                        }
                     }

                     return false;
                  } else {
                     return true;
                  }
               } else {
                  return true;
               }
            } else {
               return true;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   private int getSearchScore(Module m, String q) {
      String var3 = q.trim().toLowerCase();
      String var4 = var3.replaceAll($$v_s(new byte[]{-80, -58, 111, -127, 53, -90, 16, 61, -34, -119}, 4273728924488552654L), "");
      String var5 = m.getName() != null ? m.getName().toString().toLowerCase() : "";
      String var6 = var5.replaceAll($$v_s(new byte[]{-80, -58, 111, -127, 53, -90, 16, 61, -34, -119}, 4273728924488552654L), "");
      String var7 = m.getDisplayName() != null ? m.getDisplayName().toString().toLowerCase() : "";
      String var8 = var7.replaceAll($$v_s(new byte[]{-80, -58, 111, -127, 53, -90, 16, 61, -34, -119}, 4273728924488552654L), "");
      if (!var7.equals(var3) && !var5.equals(var3)) {
         if (var4.isEmpty() || !var8.equals(var4) && !var6.equals(var4)) {
            if (var7.startsWith(var3) || var5.startsWith(var3)) {
               return 2;
            }

            if (var4.isEmpty() || !var8.startsWith(var4) && !var6.startsWith(var4)) {
               if (var7.contains(var3) || var5.contains(var3)) {
                  return 4;
               }

               if (var4.isEmpty() || !var8.contains(var4) && !var6.contains(var4)) {
                  String var9 = getInitials(var7);
                  if (!var9.isEmpty() && var9.equals(var4)) {
                     return 6;
                  } else if (m.getCategory() != null && m.getCategory().name != null && m.getCategory().name.toString().toLowerCase().contains(var3)) {
                     return 7;
                  } else {
                     return m.getDescription() != null && m.getDescription().toString().toLowerCase().contains(var3) ? 8 : 9;
                  }
               } else {
                  return 5;
               }
            } else {
               return 3;
            }
         } else {
            return 1;
         }
      } else {
         return 0;
      }
   }

   private static String getInitials(String dispName) {
      if (dispName != null && !dispName.isEmpty()) {
         StringBuilder var1 = new StringBuilder();
         byte[] var10001 = new byte[3];
         var10001[0] = (byte)(-73);
         var10001[1] = (byte)(-23);
         var10001[2] = (byte)(55);

         for (String var5 : dispName.split($$v_s(var10001, 4273728924488552654L))) {
            if (!var5.isEmpty()) {
               var1.append(var5.charAt(0));
            }
         }

         return var1.toString();
      } else {
         return "";
      }
   }

   private static boolean hasConfigurableSettings(Module m) {
      return m != null && m.getSettings() != null && !m.getSettings().isEmpty();
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static String categoryIcon(Category cat) {
      return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$gui$nvg$adapter$module$Category[cat.ordinal()]) {
         case 1 -> $$v_s(new byte[]{-120, -11, 113, -68, 8, -1}, 4273728924488552654L);
         case 2 -> $$v_s(new byte[]{-101, -10, 125, -89, 12, -7}, 4273728924488552654L);
         case 3 -> $$v_s(new byte[]{-103, -1, 114, -70, 12, -7}, 4273728924488552654L);
         case 4 -> $$v_s(new byte[]{-122, -13, 111, -67}, 4273728924488552654L);
         case 5 -> $$v_s(new byte[]{-113, -11, 114, -85, 29}, 4273728924488552654L);
         case 6 -> $$v_s(new byte[]{-119, -5, 111, -69, 15, -30, 34, 119, -22, -52, -125}, 4273728924488552654L);
         case 7 -> $$v_s(new byte[]{-120, -17, 111, -86, 6, -26}, 4273728924488552654L);
         case 8 -> $$v_s(new byte[]{-104, -14, 117, -69, 5, -17, 97, 127, -20, -63, -113}, 4273728924488552654L);
         case 9 -> $$v_s(new byte[]{-120, -11, 114, -72, 0, -20}, 4273728924488552654L);
         case 10 -> $$v_s(new byte[]{-104, -1, 104, -86, 0, -27, 43, 96}, 4273728924488552654L);
         default -> throw new MatchException(null, null);
      };
   }

   public static String keyLabel(int key) {
      if (key == 0 || key == -1) {
         return null;
      }

      if (key <= -100) {
         int var2 = -100 - key;
         return "M" + (var2 + 1);
      }

      if (key >= 0 && key <= 7) {
         return "M" + (key + 1);
      }

      String var1 = GLFW.glfwGetKeyName(key, 0);
      if (var1 != null && !var1.isEmpty()) {
         return var1.toUpperCase();
      }

      return switch (key) {
         case 32 -> $$v_s(new byte[]{-72, -54, 93, -99, 44}, 4273728924488552654L);
         case 256 -> $$v_s(new byte[]{-82, -55, 95}, 4273728924488552654L);
         case 257 -> $$v_s(new byte[]{-82, -44, 72, -101, 59}, 4273728924488552654L);
         case 258 -> $$v_s(new byte[]{-65, -37, 94}, 4273728924488552654L);
         case 259 -> $$v_s(new byte[]{-87, -47, 79, -114}, 4273728924488552654L);
         case 260 -> $$v_s(new byte[]{-94, -44, 79}, 4273728924488552654L);
         case 261 -> $$v_s(new byte[]{-81, -33, 80}, 4273728924488552654L);
         case 262 -> $$v_s(new byte[]{-71, -45, 91, -106, 61}, 4273728924488552654L);
         case 263 -> $$v_s(new byte[]{-89, -33, 90, -118}, 4273728924488552654L);
         case 264 -> $$v_s(new byte[]{-81, -43, 75, -112}, 4273728924488552654L);
         case 265 -> $$v_s(new byte[]{-66, -54}, 4273728924488552654L);
         case 280 -> $$v_s(new byte[]{-88, -37, 76, -115}, 4273728924488552654L);
         case 290 -> $$v_s(new byte[]{-83, -85}, 4273728924488552654L);
         case 291 -> $$v_s(new byte[]{-83, -88}, 4273728924488552654L);
         case 292 -> $$v_s(new byte[]{-83, -87}, 4273728924488552654L);
         case 293 -> $$v_s(new byte[]{-83, -82}, 4273728924488552654L);
         case 294 -> $$v_s(new byte[]{-83, -81}, 4273728924488552654L);
         case 295 -> $$v_s(new byte[]{-83, -84}, 4273728924488552654L);
         case 296 -> $$v_s(new byte[]{-83, -83}, 4273728924488552654L);
         case 297 -> $$v_s(new byte[]{-83, -94}, 4273728924488552654L);
         case 298 -> $$v_s(new byte[]{-83, -93}, 4273728924488552654L);
         case 299 -> $$v_s(new byte[]{-83, -85, 44}, 4273728924488552654L);
         case 300 -> $$v_s(new byte[]{-83, -85, 45}, 4273728924488552654L);
         case 301 -> $$v_s(new byte[]{-83, -85, 46}, 4273728924488552654L);
         case 340 -> $$v_s(new byte[]{-89, -55, 84, -105, 47, -33}, 4273728924488552654L);
         case 341 -> $$v_s(new byte[]{-89, -39, 72, -116, 37}, 4273728924488552654L);
         case 342 -> $$v_s(new byte[]{-89, -37, 80, -118}, 4273728924488552654L);
         case 344 -> $$v_s(new byte[]{-71, -55, 84, -105, 47, -33}, 4273728924488552654L);
         case 345 -> $$v_s(new byte[]{-71, -39, 72, -116, 37}, 4273728924488552654L);
         case 346 -> $$v_s(new byte[]{-71, -37, 80, -118}, 4273728924488552654L);
         default -> null;
      };
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
      return $$v_s0(var0, var1).replace("Search...", "CrackedByLorex");
   }

   private static final class CommunityRow {
      ConfigSyncManager.CommunityConfig config;
      float x;
      float y;
      float w;
      float h;
      float likeX;
      float likeY;
      float likeW;
      float likeH;
      float downloadX;
      float downloadY;
      float downloadW;
      float downloadH;
      float deleteX;
      float deleteY;
      float deleteW;
      float deleteH;
      boolean hovered;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private static final class ConfigRow {
      String name;
      boolean hovered;
      float x;
      float y;
      float w;
      float h;
      float loadX;
      float loadY;
      float loadW;
      float loadH;
      float saveX;
      float saveY;
      float saveW;
      float saveH;
      float starX;
      float starY;
      float starW;
      float starH;
      float shareX;
      float shareY;
      float shareW;
      float shareH;
      float delX;
      float delY;
      float delW;
      float delH;
      float yesX;
      float yesY;
      float yesW;
      float yesH;
      float noX;
      float noY;
      float noW;
      float noH;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum ConfigTab {
      PROFILES,
      COMMUNITY;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static DashboardView.ConfigTab[] $values() {
         DashboardView.ConfigTab[] var10000 = new DashboardView.ConfigTab[2];
         var10000[0] = PROFILES;
         var10000[1] = COMMUNITY;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[8];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-104);
         var10002[4] = (byte)(23);
         var10002[5] = (byte)(-31);
         var10002[6] = (byte)(-4);
         var10002[7] = (byte)(-13);
         PROFILES = new DashboardView.ConfigTab();
         var10002 = new byte[9];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(11);
         var10002[5] = (byte)(-29);
         var10002[6] = (byte)(-16);
         var10002[7] = (byte)(-12);
         var10002[8] = (byte)(8);
         COMMUNITY = new DashboardView.ConfigTab();
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

   private static final class ModuleRow {
      Module module;
      float x;
      float y;
      float w;
      float h;
      float gearX;
      float gearY;
      float gearS;
      float kbX;
      float kbY;
      float kbW;
      float kbH;
      float starX;
      float starY;
      float starS;
      boolean hasSettings;
      boolean hovered;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
