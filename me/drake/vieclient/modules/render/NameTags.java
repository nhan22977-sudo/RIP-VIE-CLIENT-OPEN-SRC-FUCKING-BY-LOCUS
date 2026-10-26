package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.modules.client.FakeRoles;
import me.drake.vieclient.modules.client.NameProtect;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.render.EspFade;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_243;
import net.minecraft.class_2583;
import net.minecraft.class_3532;
import net.minecraft.class_4050;
import net.minecraft.class_4184;
import net.minecraft.class_640;
import net.minecraft.class_7923;
import net.minecraft.class_9064;

public class NameTags extends Module {
   private final BoolSetting selfNametag = (BoolSetting)this.addSetting(
      new BoolSetting(
         "SelfNametag",
         "Render your own nametag in third-person.",
         false
      )
   );
   private final BoolSetting ignoreSelf = (BoolSetting)this.addSetting(
      new BoolSetting(
         "IgnoreSelf",
         "Hide self nametag in first-person when freecam is off.",
         true
      )
   );
   private final BoolSetting ignoreFriends = (BoolSetting)this.addSetting(
      new BoolSetting(
         "IgnoreFriends",
         "Ignore rendering nametags for friends.",
         false
      )
   );
   private final BoolSetting ignoreBots = (BoolSetting)this.addSetting(
      new BoolSetting(
         "IgnoreBots",
         "Only render non-bot player nametags.",
         true
      )
   );
   private final DoubleSetting scale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Scale",
         "The scale of the nametag.",
         1.1,
         0.25,
         4.0,
         0.05
      )
   );
   private final BoolSetting displayItems = (BoolSetting)this.addSetting(
      new BoolSetting(
         "PlayerItems",
         "Displays armor and hand items above player nametags.",
         true
      )
   );
   private final DoubleSetting itemScale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "ItemScale",
         "The scale of player equipment icons.",
         0.7,
         0.3,
         1.5,
         0.05
      )
   );
   private final BoolSetting itemNametags = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ItemNametags",
         "Displays dropped-item nametags.",
         true
      )
   );
   private final BoolSetting itemNametagItems = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ItemIcons",
         "Displays item icon alongside dropped-item nametags.",
         true
      )
   );
   private final BoolSetting ignoreEmpty = (BoolSetting)this.addSetting(
      new BoolSetting(
         "IgnoreEmptySlots",
         "Don't reserve spacing for empty player slots.",
         true
      )
   );
   private final DoubleSetting itemSpacing = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "ItemSpacing",
         "Spacing between rendered player items.",
         2.0,
         0.0,
         12.0,
         0.25
      )
   );
   private final ColorSetting background = (ColorSetting)this.addSetting(
      new ColorSetting(
         "BackgroundColor",
         "Nametag background color.",
         -791029287
      )
   );
   private final ColorSetting nameColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "NameColor",
         "Name color.",
         -15658735
      )
   );
   private final ColorSetting distanceColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "DistanceColor",
         "Distance color.",
         -9803158
      )
   );
   private static final int RED = -59111;
   private static final int AMBER = -38631;
   private static final int GREEN = -15074279;
   private static final float ITEM_ICON_BASE_SCALE = 1.0F;
   private static final float DISTANCE_SCALE_NEAR = 6.0F;
   private static final float DISTANCE_SCALE_FAR = 56.0F;
   private static final float DISTANCE_SCALE_MIN = 0.55F;
   private static final int ITEM_TAG_MAX_RENDERED = 96;
   private static final float ITEM_TAG_BASE_SCALE = 0.7F;
   private static final float ITEM_TAG_NEAR_DISTANCE = 5.0F;
   private static final float ITEM_TAG_FAR_DISTANCE = 40.0F;
   private static final float ITEM_TAG_FAR_MULTIPLIER = 0.35F;
   private static final float ITEM_TAG_HIDE_SCALE = 0.4F;
   private static final float ITEM_TAG_MAX_WIDTH = 112.0F;
   private static final float PLAYER_NAME_SIZE = 7.0F;
   private static final float BIG_SIZE = 8.0F;
   private final List playerList = new ArrayList();
   private final List cachedItems = new ArrayList();
   private int itemCacheTick;
   private final NameTags.CameraDistanceComparator distanceComparator = new NameTags.CameraDistanceComparator();
   private final List panels = new ArrayList();
   private final List texts = new ArrayList();
   private final List bars = new ArrayList();
   private final List icons = new ArrayList();
   private final List droppedIcons = new ArrayList();
   private final List roleBadges = new ArrayList();
   private final EspFade espFade = new EspFade(this::finishDisable);
   private final WorldProjector projector = new WorldProjector();
   private final float[] scratchFeet = new float[3];
   private final float[] scratchScreen = new float[3];
   private final float[] scratchHead = new float[3];
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public NameTags() {
      super(
         "NameTags",
         "Player nametags with health, distance, and equipment.",
         Category.RENDER
      );
      this.ignoreBots.setVisibleInGui(false);
      this.ignoreEmpty.setVisibleInGui(false);
      this.itemScale.visibleWhen(this.displayItems::get);
      this.itemSpacing.visibleWhen(this.displayItems::get);
      this.itemNametagItems.visibleWhen(this.itemNametags::get);
   }

   public boolean isItemIconsEnabled() {
      return (Boolean)this.itemNametagItems.get();
   }

   public double getItemScale() {
      return (Double)this.itemScale.get();
   }

   @Override
   public String getInfo() {
      return Integer.toString(this.playerList.size());
   }

   private void finishDisable() {
      this.playerList.clear();
      this.cachedItems.clear();
      this.panels.clear();
      this.texts.clear();
      this.bars.clear();
      this.icons.clear();
      this.droppedIcons.clear();
      this.roleBadges.clear();
      super.onDisable();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.espFade.onEnable();
   }

   @Override
   public void onDisable() {
      this.espFade.onDisable();
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.POST) {
         this.espFade.update();
         if (!this.isInGame()) {
            this.playerList.clear();
            this.cachedItems.clear();
         } else if (this.espFade.isActive()) {
            this.playerList.clear();
            boolean var2 = this.isFreecamActive();
            boolean var3 = mc.field_1690.method_31044().method_31034();
            class_243 var4 = mc.field_1773.method_19418().method_71156();

            for (class_1657 var6 : mc.field_1687.method_18456()) {
               if (var6 != null
                  && var6.method_5805()
                  && (
                     var6 == mc.field_1724
                        ? (Boolean)this.selfNametag.get() && !this.shouldIgnoreSelfNametag(var2, var3)
                        : (!(Boolean)this.ignoreBots.get() || !this.isBot(var6))
                           && (!(Boolean)this.ignoreFriends.get() || !this.isFriend(var6.method_5477().getString()))
                  )) {
                  this.playerList.add(var6);
               }
            }

            this.appendSelfIfNeeded(var2, var3);
            this.distanceComparator.camPos = var4;
            this.playerList.sort(this.distanceComparator);
            if ((Boolean)this.itemNametags.get() && this.itemCacheTick++ % (10) == 0) {
               this.cachedItems.clear();

               for (class_1297 var9 : mc.field_1687.method_18112()) {
                  if (var9 instanceof class_1542 var7 && var7.method_5805() && !var7.method_6983().method_7960()) {
                     this.cachedItems.add(var7);
                  }
               }
            }
         }
      }
   }

   private void buildPlayerTag(class_1657 player, float sx, float sy, float feetY, float baseScale, double cameraDistance) {
      float var8 = this.distanceScaleMultiplier(cameraDistance);
      float var9 = baseScale * var8;
      float var10 = snap(7.0F * Math.max(0.01F, var9));
      NameTags.NameRenderParts var11 = this.resolveNameParts(player);
      String var12 = var11.prefixText();
      String var13 = var11.nameText();
      double var14 = mc.field_1724 != null ? mc.field_1724.method_5739(player) : 0.0;
      var14 = Math.round(var14 * 10.0) / 10.0;
      String var16 = " " + var14 + "m";
      double var17 = getTextHeight(var10);
      double var19 = getTextWidth(var13, var10);
      double var23 = 0.0;
      double var25 = 0.0;
      double var27 = 0.0;
      double var21;
      if (var11.isMediaBadge()) {
         var25 = Math.max(4.0, var17 * 0.42);
         var23 = var25 * 2.364102564102564;
         var27 = Math.max(1.5, 2.0 * var9);
         var21 = var23 + var27 + var19;
      } else {
         var21 = getTextWidth(var12, var10) + var19;
      }

      double var29 = sx - var21 / 2.0;
      double var31 = sy - var17;
      int var33 = this.resolvedBackdropColor();
      this.addPanel(var29 - 1.0, var31 - 1.0, var21 + 2.0, var17 + 2.0, var33, this.resolvedStrokeColor(var33));
      double var34 = var29;
      if (var11.isMediaBadge()) {
         double var36 = var31 + (var17 - var25) * 0.5;
         this.roleBadges
            .add(
               new NameTags.RoleBadgeDraw(
                  "role_media",
                  (float)var34,
                  (float)var36,
                  (float)var23,
                  (float)var25
               )
            );
         var34 += var23 + var27;
      } else if (!var12.isEmpty()) {
         var34 = this.addText(var12, var34, var31, var10, var11.prefixColor());
      }

      this.addText(var13, var34, var31, var10, var11.nameColor());
      float var55 = snap(8.0F * (float)Math.max(0.01, 0.74 * var9));
      double var37 = getTextWidth(var16, var55);
      double var39 = getTextHeight(var55);
      double var41 = 3.0;
      double var43 = 1.2;
      double var45 = var37 + var41 * 2.0;
      double var47 = var39 + var43 * 2.0;
      double var49 = sx - var45 * 0.5;
      double var51 = feetY + 3.0;
      int var53 = this.itemTagBackdropColor();
      this.addPanel(var49, var51, var45, var47, var53, this.resolvedStrokeColor(var53));
      this.addText(var16, var49 + var41, var51 + var43, var55, (Integer)this.distanceColor.get());
      if ((Boolean)this.displayItems.get()) {
         this.buildPlayerItems(player, sx, var31, var9, var8);
      }
   }

   private void buildPlayerItems(class_1657 player, float sx, double textY, float tagScale, float distanceScaleMul) {
      class_1799[] var7 = this.getPlayerDisplayItems(player);
      ArrayList var8 = new ArrayList(var7.length);
      ArrayList var9 = new ArrayList(var7.length);

      for (int var10 = 0; var10 < var7.length; var10++) {
         class_1799 var11 = var7[var10];
         if (!(Boolean)this.ignoreEmpty.get() || !var11.method_7960()) {
            var8.add(var11);
            var9.add(var10);
         }
      }

      if (!var8.isEmpty()) {
         float var26 = ((Double)this.itemScale.get()).floatValue();
         float var27 = Math.max(0.35F, var26 * distanceScaleMul);
         int var12 = Math.max(8, Math.round(15.0F * var27));
         double var13 = var12 + (Double)this.itemSpacing.get();
         double var15 = var13 * var8.size() - (Double)this.itemSpacing.get();
         double var17 = sx - var15 / 2.0;
         double var19 = textY - 4.0 - var12;
         float var21 = Math.max(1.5F, Math.min(2.5F, var12 * 0.2F));
         ArrayList var22 = new ArrayList(var8.size());

         for (int var23 = 0; var23 < var8.size(); var23++) {
            double var24 = var17 + var23 * var13;
            var22.add(var24);
            this.icons.add(new NameTags.ItemIconDraw((class_1799)var8.get(var23), (float)var24, (float)var19, var12, var21));
         }
      }
   }

   private void buildHealthBar(class_1657 player, class_243 playerPos) {
      if (this.projector.project(playerPos.field_1352, playerPos.field_1351, playerPos.field_1350, this.scratchFeet)) {
         float var3 = player.method_18377(class_4050.field_18076).comp_2186();
         if (this.projector.project(playerPos.field_1352, playerPos.field_1351 + var3, playerPos.field_1350, this.scratchHead)) {
            float var4 = Math.min(this.scratchFeet[1], this.scratchHead[1]);
            float var5 = Math.max(this.scratchFeet[1], this.scratchHead[1]);
            float var6 = var5 - var4;
            if (!(var6 < 4.0F)) {
               float var7 = player.method_6067();
               float var8 = player.method_6032() + var7;
               float var9 = Math.max(1.0F, player.method_6063() + var7);
               double var10 = Math.max(0.0, Math.min(1.0, var8 / var9));
               int var12 = Math.max(2, Math.round(var6 * 0.045F));
               int var13 = Math.max(2, Math.round(var6 * 0.05F));
               float var14 = this.scratchFeet[0] + var6 * 0.18F + var13;
               float var15 = (float)Math.floor(var4);
               float var16 = (float)Math.ceil(var5);
               float var17 = Math.max(1.0F, var16 - var15);
               float var18 = var12 * 0.5F;
               this.bars.add(new NameTags.BarDraw(var14, var15, var12, var17, var18, -2146430960, healthColorForPct(var10), (float)var10));
            }
         }
      }
   }

   private void buildItemNametags(class_243 camPos, float tickDelta, float baseScale) {
      if ((Boolean)this.itemNametags.get() && mc.field_1687 != null) {
         ArrayList var4 = new ArrayList();

         for (class_1297 var6 : mc.field_1687.method_18112()) {
            if (var6 instanceof class_1542 var7 && var7.method_5805() && !var7.method_6983().method_7960()) {
               var4.add(var7);
            }
         }

         if (!var4.isEmpty()) {
            var4.sort(Comparator.comparingDouble(item -> item.method_5707(camPos)));
            int var17 = 0;

            for (class_1542 var19 : var4) {
               if (var17 >= (96)) {
                  break;
               }

               if (var19.method_5805()) {
                  class_243 var8 = getEntityRenderPos(var19, tickDelta);
                  double var9 = camPos.method_1022(var8);
                  float var11 = this.itemTagScale(var9, baseScale);
                  if (!(var11 <= 0.4F)) {
                     class_243 var12 = var8.method_1031(0.0, Math.max(0.3, var19.method_17682() + 0.25), 0.0);
                     if (this.projector.project(var12.field_1352, var12.field_1351, var12.field_1350, this.scratchScreen)) {
                        class_1799 var13 = var19.method_6983();
                        String var14 = var13.method_7964().getString();
                        if (var14 != null && !var14.isBlank()) {
                           float var15 = this.scratchScreen[0];
                           float var16 = this.scratchScreen[1];
                           this.buildItemNametag(var15, var16, var13, var14, var13.method_7947(), var11);
                           var17++;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void buildItemNametag(float sx, float sy, class_1799 stack, String rawText, int count, float tagScale) {
      float var7 = snap(8.0F * Math.max(0.01F, tagScale));
      String var8 = " x" + Math.max(1, count);
      double var9 = getTextWidth(var8, var7);
      float var11 = 2.0F;
      boolean var12 = (Boolean)this.itemNametagItems.get() && stack != null && !stack.method_7960();
      float var13 = var12 ? snap(Math.max(9.0F, 13.0F * tagScale)) : 0.0F;
      float var14 = var12 ? 2.5F : 0.0F;
      double var15 = var12 ? var13 + var14 : 0.0;
      float var17 = (float)Math.max(0.0, 112.0 - var15 - var9 - var11);
      String var18 = this.fitTextByWidth(rawText, var7, var17);
      double var19 = getTextWidth(var18, var7);
      double var21 = var18.isEmpty() ? 0.0 : var11;
      double var23 = getTextHeight(var7);
      double var25 = 3.0;
      double var27 = 1.5;
      double var29 = Math.max(var23, var13);
      double var31 = var15 + var19 + var9 + var21 + var25 * 2.0;
      double var33 = var29 + var27 * 2.0;
      double var35 = sx - var31 * 0.5;
      double var37 = sy - var33;
      int var39 = this.itemTagBackdropColor();
      this.addPanel(var35, var37, var31, var33, var39, this.resolvedStrokeColor(var39));
      double var40 = var35 + var25;
      if (var12) {
         float var42 = (float)(var37 + (var33 - var13) * 0.5);
         float var43 = Math.max(2.0F, Math.min(3.5F, var13 * 0.22F));
         this.droppedIcons.add(new NameTags.DroppedItemIconDraw(stack, (float)var40, var42, var13, var43));
         var40 += var15;
      }

      double var44 = var37 + (var33 - var23) * 0.5;
      if (!var18.isEmpty()) {
         var40 = this.addText(var18, var40, var44, var7, this.resolvedNameTextColor()) + var21;
      }

      this.addText(var8, var40, var44, var7, (Integer)this.distanceColor.get());
   }

   private void addPanel(double x, double y, double width, double height, int color, int strokeColor) {
      float var11 = (float)x;
      float var12 = (float)y;
      float var13 = (float)width;
      float var14 = (float)height;
      float var15 = Math.max(1.0F, Math.min(Math.min(var13, var14) * 0.28F, 4.0F));
      this.panels.add(new NameTags.PanelDraw(var11 + 0.5F, var12 + 0.5F, var13 - 1.0F, var14 - 1.0F, var15, color, strokeColor));
   }

   private double addText(String text, double x, double y, float size, int color) {
      if (text != null && !text.isEmpty()) {
         this.texts.add(new NameTags.TextDraw(text, (float)x, (float)y, size, color));
         return x + getTextWidth(text, size);
      } else {
         return x;
      }
   }

   private static float getTextWidth(String text, float size) {
      if (text != null && !text.isEmpty()) {
         float var2 = NanoVGRenderer.getTextWidth(text, size);
         return var2 > 0.0F ? var2 : text.length() * Math.max(4.0F, size * 0.5F);
      } else {
         return 0.0F;
      }
   }

   private static float getTextHeight(float size) {
      float var1 = NanoVGRenderer.getTextHeight(size);
      return var1 > 0.0F ? var1 : Math.max(6.0F, size);
   }

   private static float snap(float size) {
      return Math.max(1.0F, Math.round(size));
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (this.isInGame() && this.espFade.isActive() && mc.field_1724 != null && mc.field_1687 != null) {
         float var2 = this.espFade.getFade();
         if (!(var2 <= 0.001F)) {
            this.panels.clear();
            this.texts.clear();
            this.bars.clear();
            this.icons.clear();
            this.droppedIcons.clear();
            this.roleBadges.clear();
            if (this.projector.snapshot()) {
               float var3 = event.tickDelta;
               if (var3 <= 0.0F && mc.method_61966() != null) {
                  var3 = mc.method_61966().method_60637(true);
               }

               class_4184 var4 = mc.field_1773.method_19418();
               class_243 var5 = var4 != null ? var4.method_71156() : class_243.field_1353;
               float var6 = ((Double)this.scale.get()).floatValue();
               ArrayList var7 = new ArrayList();
               boolean var8 = this.isFreecamActive();
               boolean var9 = mc.field_1690.method_31044().method_31034();

               for (class_1657 var11 : mc.field_1687.method_18456()) {
                  if (var11 != null
                     && var11.method_5805()
                     && (
                        var11 == mc.field_1724
                           ? (Boolean)this.selfNametag.get() && !this.shouldIgnoreSelfNametag(var8, var9)
                           : (!(Boolean)this.ignoreBots.get() || !this.isBot(var11))
                              && (!(Boolean)this.ignoreFriends.get() || !this.isFriend(var11.method_5477().getString()))
                     )) {
                     var7.add(var11);
                  }
               }

               if ((Boolean)this.selfNametag.get() && !this.shouldIgnoreSelfNametag(var8, var9) && !var7.contains(mc.field_1724) && mc.field_1724.method_5805()
                  )
                {
                  var7.add(mc.field_1724);
               }

               this.distanceComparator.camPos = var5;
               var7.sort(this.distanceComparator);
               if (!var7.isEmpty()) {
                  for (int var28 = var7.size() - 1; var28 >= 0; var28--) {
                     class_1657 var30 = (class_1657)var7.get(var28);
                     if (var30 != null && var30.method_5805()) {
                        class_243 var12 = getEntityRenderPos(var30, var3);
                        if (this.projector.project(var12.field_1352, var12.field_1351, var12.field_1350, this.scratchFeet)) {
                           class_243 var13 = this.getEntityTagPos(var30, var12, var3);
                           if (this.projector.project(var13.field_1352, var13.field_1351, var13.field_1350, this.scratchScreen)) {
                              float var14 = this.scratchScreen[0];
                              float var15 = this.scratchScreen[1];
                              float var16 = this.scratchFeet[1];
                              double var17 = var5.method_1022(var12);
                              this.buildPlayerTag(var30, var14, var15, var16, var6, var17);
                              this.buildHealthBar(var30, var12);
                           }
                        }
                     }
                  }
               }

               this.buildItemNametags(var5, var3, var6);
               if (!this.panels.isEmpty()
                  || !this.bars.isEmpty()
                  || !this.texts.isEmpty()
                  || !this.icons.isEmpty()
                  || !this.droppedIcons.isEmpty()
                  || !this.roleBadges.isEmpty()) {
                  try (NanoVGFrame var29 = NanoVGRenderer.frame()) {
                     if (!var29.isActive()) {
                        return;
                     }

                     NanoVGRenderer.globalAlpha(var2);

                     try {
                        for (NameTags.BarDraw var37 : this.bars) {
                           NanoVGRenderer.drawRoundedRect(var37.x, var37.y, var37.w, var37.h, var37.radius, var37.track);
                           int var43 = Math.max(1, Math.round(var37.h * var37.pct));
                           NanoVGRenderer.drawRoundedRect(var37.x, var37.y + (var37.h - var43), var37.w, var43, var37.radius, var37.fill);
                        }

                        for (NameTags.PanelDraw var38 : this.panels) {
                           NanoVGRenderer.drawRoundedRect(var38.x, var38.y, var38.w, var38.h, var38.radius, var38.color);
                           int var44 = var38.stroke >>> 24 & 0xFF;
                           if (var44 > 0) {
                              NanoVGRenderer.drawRoundedRectOutline(
                                 var38.x + 0.5F, var38.y + 0.5F, var38.w - 1.0F, var38.h - 1.0F, Math.max(0.5F, var38.radius - 0.5F), 1.0F, var38.stroke
                              );
                           }
                        }

                        for (NameTags.RoleBadgeDraw var39 : this.roleBadges) {
                           if ("role_media".equals(var39.iconName)) {
                              this.drawMediaBadge(var39.x, var39.y, var39.w, var39.h, var2);
                           } else {
                              IconManager.drawColored(var39.iconName, var39.x, var39.y, var39.w, var39.h, Math.round(255.0F * var2));
                           }
                        }

                        for (NameTags.DroppedItemIconDraw var40 : this.droppedIcons) {
                           float var45 = var40.radius > 0.05F ? var40.radius : Math.max(2.0F, Math.min(3.5F, var40.size * 0.22F));
                           if (var40.stack != null && var40.stack.method_7958()) {
                              NanoVGRenderer.drawRadialGlow(
                                 var40.x + var40.size * 0.5F, var40.y + var40.size * 0.5F, var40.size * 0.85F, new Color(175, 75, 255, (int)(70.0F * var2))
                              );
                           }

                           int var47 = withAlpha(0, (int)(40.0F * var2));
                           int var49 = withAlpha(16777215, (int)(25.0F * var2));
                           NanoVGRenderer.drawRoundedRect(var40.x, var40.y, var40.size, var40.size, var45, var47);
                           NanoVGRenderer.drawRoundedRectOutline(var40.x, var40.y, var40.size, var40.size, var45, 0.65F, var49);
                           NVGImage.drawMCItem(var40.stack, var40.x + 0.5F, var40.y + 0.5F, var40.size - 1.0F, Math.max(1.5F, var45 - 0.5F), var2);
                        }

                        for (NameTags.ItemIconDraw var41 : this.icons) {
                           if (var41.stack != null && !var41.stack.method_7960()) {
                              float var46 = var41.radius > 0.05F ? var41.radius : Math.max(2.0F, Math.min(3.5F, var41.size * 0.22F));
                              if (var41.stack.method_7958()) {
                                 NanoVGRenderer.drawRadialGlow(
                                    var41.x + var41.size * 0.5F, var41.y + var41.size * 0.5F, var41.size * 0.85F, new Color(175, 75, 255, (int)(70.0F * var2))
                                 );
                              }

                              int var48 = withAlpha(0, (int)(45.0F * var2));
                              int var50 = withAlpha(16777215, (int)(30.0F * var2));
                              NanoVGRenderer.drawRoundedRect(var41.x, var41.y, var41.size, var41.size, var46, var48);
                              NanoVGRenderer.drawRoundedRectOutline(var41.x, var41.y, var41.size, var41.size, var46, 0.65F, var50);
                              NVGImage.drawMCItem(var41.stack, var41.x + 0.5F, var41.y + 0.5F, var41.size - 1.0F, Math.max(1.5F, var46 - 0.5F), var2);
                              int var51 = Math.max(1, var41.stack.method_7936());
                              int var52 = var41.stack.method_7919();
                              if (var52 > 0 && var51 > 1) {
                                 float var18 = Math.max(0.0F, Math.min(1.0F, 1.0F - (float)var52 / var51));
                                 int var19 = withAlpha(var41.stack.method_31580(), (int)(220.0F * var2));
                                 float var20 = (var41.size - 3.0F) * var18;
                                 NanoVGRenderer.drawRoundedRect(var41.x + 1.5F, var41.y + var41.size - 2.2F, var20, 1.2F, 0.5F, var19);
                              }

                              if (var41.stack.method_7947() > 1) {
                                 String var53 = String.valueOf(var41.stack.method_7947());
                                 float var54 = Math.max(5.5F, var41.size * 0.42F);
                                 float var55 = NanoVGRenderer.getTextWidth(var53, var54, true);
                                 NanoVGRenderer.drawText(
                                    var53,
                                    var41.x + var41.size - var55 - 1.0F,
                                    var41.y + var41.size - var54 - 0.5F,
                                    var54,
                                    withAlpha(16777215, (int)(255.0F * var2)),
                                    true
                                 );
                              }
                           }
                        }

                        for (NameTags.TextDraw var42 : this.texts) {
                           NanoVGRenderer.drawText(var42.text, var42.x, var42.y, var42.size, var42.color);
                        }
                     } finally {
                        NanoVGRenderer.globalAlpha(1.0F);
                     }
                  }
               }
            }
         }
      }
   }

   private float distanceScaleMultiplier(double cameraDistance) {
      if (!Double.isFinite(cameraDistance)) {
         return 1.0F;
      }

      if (cameraDistance <= 6.0) {
         return 1.0F;
      }

      if (cameraDistance >= 56.0) {
         return 0.55F;
      }

      float var3 = (float)((cameraDistance - 6.0) / 50.0);
      var3 = clamp01(var3);
      var3 = var3 * var3 * (3.0F - 2.0F * var3);
      return 1.0F + -0.45F * var3;
   }

   private float itemTagScale(double distance, float baseScale) {
      float var4 = Math.max(0.12F, baseScale * 0.7F);
      if (!Double.isFinite(distance) || distance <= 5.0) {
         return var4;
      }

      if (distance >= 40.0) {
         return var4 * 0.35F;
      }

      float var5 = (float)((distance - 5.0) / 35.0);
      var5 = clamp01(var5);
      var5 = var5 * var5 * (3.0F - 2.0F * var5);
      float var6 = 1.0F + -0.65F * var5;
      return var4 * var6;
   }

   private String fitTextByWidth(String text, float sizePx, float maxWidth) {
      if (text != null && !text.isEmpty()) {
         if (getTextWidth(text, sizePx) <= maxWidth) {
            return text;
         }

         byte[] var10000 = new byte[3];
         var10000[0] = (byte)(36);
         var10000[1] = (byte)(-76);
         var10000[2] = (byte)(50);
         String var4 = $$v_s(var10000, -6106044682944131025L);
         var10000 = new byte[3];
         var10000[0] = (byte)(36);
         var10000[1] = (byte)(-76);
         var10000[2] = (byte)(50);
         double var5 = getTextWidth($$v_s(var10000, -6106044682944131025L), sizePx);
         if (var5 >= maxWidth) {
            return "";
         }

         String var7 = text.trim();

         for (int var8 = var7.length() - 1; var8 > 0; var8--) {
            String var9 = var7.substring(0, var8).trim() + "...";
            if (getTextWidth(var9, sizePx) <= maxWidth) {
               return var9;
            }
         }

         return "";
      } else {
         return "";
      }
   }

   private int itemTagBackdropColor() {
      int var1 = this.resolvedBackdropColor();
      int var2 = Math.max(96, Math.min(176, var1 >>> (24) & (255)));
      return withAlpha(var1, var2);
   }

   private int resolvedBackdropColor() {
      int var1 = (Integer)this.nameColor.get();
      int var2 = var1 >>> (24) & (255);
      return var2 <= (176) ? var1 : withAlpha(var1, 176);
   }

   private int resolvedStrokeColor(int backdrop) {
      int var2 = Math.max(90, backdrop >>> (24) & (255));
      return withAlpha(-13750722, var2);
   }

   private static float clamp01(float value) {
      return Math.max(0.0F, Math.min(1.0F, value));
   }

   private class_1799[] getPlayerDisplayItems(class_1657 player) {
      class_1799[] var10000 = new class_1799[6];
      var10000[0] = player.method_6047();
      var10000[1] = player.method_6118(class_1304.field_6169);
      var10000[2] = player.method_6118(class_1304.field_6174);
      var10000[3] = player.method_6118(class_1304.field_6172);
      var10000[4] = player.method_6118(class_1304.field_6166);
      var10000[5] = player.method_6079();
      return var10000;
   }

   private boolean shouldRenderDurabilityPercent(int slotIndex, class_1799 stack) {
      if (stack == null || stack.method_7960()) {
         return false;
      } else if (slotIndex >= 1 && slotIndex <= (4)) {
         return true;
      } else {
         return stack.method_7963() ? true : (slotIndex == 0 || slotIndex == (5)) && this.isToolLike(stack);
      }
   }

   private boolean isToolLike(class_1799 stack) {
      String var2 = class_7923.field_41178.method_10221(stack.method_7909()).method_12832();
      return var2.endsWith("_pickaxe")
         || var2.endsWith("_axe")
         || var2.endsWith("_shovel")
         || var2.endsWith("_hoe")
         || var2.endsWith("_sword")
         || var2.endsWith("_trident")
         || var2.endsWith("_mace")
         || var2.equals("shears")
         || var2.equals("fishing_rod")
         || var2.equals("flint_and_steel")
         || var2.equals("brush")
         || var2.equals("bow")
         || var2.equals("crossbow");
   }

   private String durabilityPercentText(class_1799 stack) {
      int var2 = Math.max(1, stack.method_7936());
      int var3 = Math.max(0, var2 - stack.method_7919());
      int var4 = Math.round(var3 * 100.0F / var2);
      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(47);
      var10001[1] = (byte)(-2);
      var10001[2] = (byte)(57);
      var10001[3] = (byte)(-5);
      return String.format(Locale.ROOT, $$v_s(var10001, -6106044682944131025L), var4);
   }

   private boolean isBot(class_1657 player) {
      if (player == mc.field_1724) {
         return false;
      }

      if (mc.method_1562() == null) {
         return true;
      }

      class_640 var2 = mc.method_1562().method_2871(player.method_5667());
      return var2 == null;
   }

   private boolean isFriend(String name) {
      VieClientMod var2 = VieClientMod.getInstance();
      return var2 != null && var2.getFriendManager() != null && var2.getFriendManager().isFriend(name);
   }

   private boolean shouldIgnoreSelfNametag(boolean freecamActive, boolean firstPerson) {
      return (Boolean)this.ignoreSelf.get() && !freecamActive && firstPerson;
   }

   private void appendSelfIfNeeded(boolean freecamActive, boolean firstPerson) {
      if (mc.field_1724 != null && mc.field_1724.method_5805()) {
         if ((Boolean)this.selfNametag.get()) {
            if (!this.shouldIgnoreSelfNametag(freecamActive, firstPerson)) {
               if (!this.playerList.contains(mc.field_1724)) {
                  this.playerList.add(mc.field_1724);
               }
            }
         }
      }
   }

   private boolean isFreecamActive() {
      Freecam var1 = VieClientMod.getInstance() == null ? null : (Freecam)VieClientMod.getInstance().getModuleManager().getModule(Freecam.class);
      return var1 != null && var1.isEnabled();
   }

   public static class_243 getEntityRenderPos(class_1297 entity, float tickDelta) {
      double var2 = class_3532.method_16436(tickDelta, entity.field_6038, entity.method_23317());
      double var4 = class_3532.method_16436(tickDelta, entity.field_5971, entity.method_23318());
      double var6 = class_3532.method_16436(tickDelta, entity.field_5989, entity.method_23321());
      return new class_243(var2, var4, var6);
   }

   private class_243 getEntityTagPos(class_1297 entity, class_243 basePos, float tickDelta) {
      if (entity.method_56072() != null) {
         class_243 var4 = entity.method_56072().method_55675(class_9064.field_47745, 0, entity.method_61415(tickDelta));
         if (var4 != null) {
            return basePos.method_1019(var4);
         }
      }

      return basePos.method_1031(0.0, entity.method_18381(entity.method_18376()) + 0.5, 0.0);
   }

   private void drawMediaBadge(float x, float y, float w, float h, float alpha) {
      float var6 = h / 195.0F;
      int var7 = Math.max(0, Math.min(255, (int)(255.0F * alpha)));
      int var8 = withAlpha(-1376091, var7);
      int var9 = withAlpha(-11927501, (int)(var7 * 0.9F));
      int var10 = withAlpha(-13975591, var7);
      int var11 = withAlpha(-16105655, (int)(var7 * 0.9F));
      drawMediaBadgeVector(x + 7.0F * var6, y + 7.0F * var6, var6, var9, var11);
      drawMediaBadgeVector(x, y, var6, var8, var10);
   }

   private static void drawMediaBadgeVector(float x, float y, float s, int colM, int colC) {
      NanoVGRenderer.drawRect(x + 0.0F * s, y + 65.0F * s, 16.0F * s, 109.0F * s, colM);
      NanoVGRenderer.drawRect(x + 16.0F * s, y + 81.0F * s, 37.0F * s, 75.0F * s, colM);
      NanoVGRenderer.drawRect(x + 196.0F * s, y + 20.5F * s, 34.0F * s, 17.5F * s, colM);
      NanoVGRenderer.drawRect(x + 212.0F * s, y + 38.0F * s, 18.0F * s, 20.0F * s, colM);
      NanoVGRenderer.drawRect(x + 53.0F * s, y + 38.0F * s, 159.0F * s, 20.0F * s, colM);
      NanoVGRenderer.drawRect(x + 53.0F * s, y + 99.0F * s, 159.0F * s, 88.0F * s, colM);
      NanoVGRenderer.drawRect(x + 53.0F * s, y + 58.0F * s, 53.0F * s, 41.0F * s, colM);
      NanoVGRenderer.drawRect(x + 177.5F * s, y + 58.0F * s, 34.5F * s, 41.0F * s, colM);
      NanoVGRenderer.drawRect(x + 357.5F * s, y + 0.0F * s, 32.0F * s, 187.0F * s, colC);
      NanoVGRenderer.drawRect(x + 286.0F * s, y + 79.0F * s, 71.5F * s, 36.0F * s, colC);
      NanoVGRenderer.drawRect(x + 389.5F * s, y + 79.0F * s, 63.5F * s, 36.0F * s, colC);
   }

   private int resolvedNameTextColor() {
      return (Integer)this.background.get();
   }

   private NameTags.NameRenderParts resolveNameParts(class_1657 player) {
      String var2 = this.resolveProtectedName(player);
      if (player == mc.field_1724 && FakeRoles.isActive()) {
         int var3 = this.resolvedNameTextColor();
         int var4 = colorFromStyle(FakeRoles.getRoleNameStyle(), var3);
         if (FakeRoles.isMediaRole()) {
            String var11 = FakeRoles.stripMediaPrefix(var2);
            return new NameTags.NameRenderParts("", var11, var3, var4, true);
         }

         String var5 = FakeRoles.getEffectivePrefix();
         if (var5 != null) {
            var5 = var5.trim();
         }

         var5 = sanitizeRolePrefixForNametag(var5);
         if (var5 != null && !var5.isEmpty()) {
            int var6 = colorFromStyle(FakeRoles.getRolePrefixStyle(), var3);
            return new NameTags.NameRenderParts(var5, var2, var6, var4, false);
         }
      }

      if (this.hasMediaRank(player)) {
         int var8 = this.resolvedNameTextColor();
         String var9 = FakeRoles.stripMediaPrefix(var2);
         return new NameTags.NameRenderParts("", var9, var8, var8, true);
      } else {
         int var7 = this.resolvedNameTextColor();
         return new NameTags.NameRenderParts("", var2, var7, var7, false);
      }
   }

   private boolean hasMediaRank(class_1657 player) {
      if (player == null) {
         return false;
      }

      if (player.method_5476() != null) {
         String var2 = player.method_5476().getString();
         if (FakeRoles.hasMediaPrefix(var2)) {
            return true;
         }
      }

      if (player.method_5781() != null) {
         String var3 = player.method_5781().method_1144().getString();
         if (FakeRoles.hasMediaPrefix(var3)) {
            return true;
         }
      }

      return false;
   }

   private String resolveProtectedName(class_1657 player) {
      String var2 = null;
      if (player != null && player.method_7334() != null) {
         var2 = player.method_7334().name();
      }

      if (var2 == null || var2.isBlank()) {
         var2 = player == null ? "" : player.method_5477().getString();
      }

      var2 = FakeRoles.stripMediaPrefix(var2);
      var2 = NameProtect.staticMaskPlayerNametag(var2, player == mc.field_1724);
      return var2 == null ? "" : var2;
   }

   private static String sanitizeRolePrefixForNametag(String prefix) {
      if (prefix != null && !prefix.isBlank()) {
         String var1 = prefix.replace("[", "").replace("]", "").trim();
         byte[] var10001 = new byte[3];
         var10001[0] = (byte)(86);
         var10001[1] = (byte)(-23);
         var10001[2] = (byte)(55);
         return var1.replaceAll($$v_s(var10001, -6106044682944131025L), " ");
      } else {
         return "";
      }
   }

   private static int withAlpha(int rgb, int alpha) {
      return (alpha & (255)) << (24) | rgb & (16777215);
   }

   private static int colorFromStyle(class_2583 style, int fallbackArgb) {
      return style != null && style.method_10973() != null ? withAlpha(style.method_10973().method_27716(), 255) : fallbackArgb;
   }

   private static int healthColorForPct(double pct) {
      pct = Math.max(0.0, Math.min(1.0, pct));
      return pct >= 0.5
         ? lerpColor(-38631, -15074279, (float)((pct - 0.5) / 0.5))
         : lerpColor(-59111, -38631, (float)(pct / 0.5));
   }

   private static int lerpColor(int a, int b, float t) {
      t = Math.max(0.0F, Math.min(1.0F, t));
      int var3 = a >>> (24) & (255);
      int var4 = a >>> (16) & (255);
      int var5 = a >>> (8) & (255);
      int var6 = a & (255);
      int var7 = b >>> (24) & (255);
      int var8 = b >>> (16) & (255);
      int var9 = b >>> (8) & (255);
      int var10 = b & (255);
      int var11 = Math.round(var3 + (var7 - var3) * t);
      int var12 = Math.round(var4 + (var8 - var4) * t);
      int var13 = Math.round(var5 + (var9 - var5) * t);
      int var14 = Math.round(var6 + (var10 - var6) * t);
      return var11 << (24) | var12 << (16) | var13 << (8) | var14;
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

   private static final class BarDraw {
      final float x;
      final float y;
      final float w;
      final float h;
      final float radius;
      final int track;
      final int fill;
      final float pct;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      BarDraw(float x, float y, float w, float h, float radius, int track, int fill, float pct) {
         this.x = xx;
         this.y = yx;
         this.w = wx;
         this.h = hx;
         this.radius = radiusx;
         this.track = trackx;
         this.fill = fillx;
         this.pct = pctx;
      }
   }

   private static final class CameraDistanceComparator implements Comparator {
      class_243 camPos = class_243.field_1353;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public int compare(class_1657 a, class_1657 b) {
         return Double.compare(a.method_5707(this.camPos), b.method_5707(this.camPos));
      }
   }

   private static final class DroppedItemIconDraw {
      final class_1799 stack;
      final float x;
      final float y;
      final float size;
      final float radius;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      DroppedItemIconDraw(class_1799 stack, float x, float y, float size, float radius) {
         this.stack = stackx;
         this.x = xx;
         this.y = yx;
         this.size = sizex;
         this.radius = radiusx;
      }
   }

   private static final class ItemIconDraw {
      final class_1799 stack;
      final float x;
      final float y;
      final float size;
      final float radius;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ItemIconDraw(class_1799 stack, float x, float y, float size, float radius) {
         this.stack = stackx;
         this.x = xx;
         this.y = yx;
         this.size = sizex;
         this.radius = radiusx;
      }
   }

   private record NameRenderParts(String prefix, String name, int prefixColor, int nameColor, boolean isMediaBadge) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      NameRenderParts(String prefix, String name, int prefixColor, int nameColor) {
         this(prefixx, namex, prefixColorx, nameColorx, false);
      }

      String prefixText() {
         if (this.isMediaBadge) {
            return "";
         } else if (this.prefix == null || this.prefix.isBlank()) {
            return "";
         } else {
            return this.name != null && !this.name.isBlank() ? this.prefix + " " : this.prefix;
         }
      }

      String nameText() {
         String var1 = this.name == null ? "" : this.name;
         return this.isMediaBadge ? FakeRoles.stripMediaPrefix(var1) : var1;
      }
   }

   private static final class PanelDraw {
      final float x;
      final float y;
      final float w;
      final float h;
      final float radius;
      final int color;
      final int stroke;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      PanelDraw(float x, float y, float w, float h, float radius, int color, int stroke) {
         this.x = xx;
         this.y = yx;
         this.w = wx;
         this.h = hx;
         this.radius = radiusx;
         this.color = colorx;
         this.stroke = strokex;
      }
   }

   private record RoleBadgeDraw(String iconName, float x, float y, float w, float h) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private static final class TextDraw {
      final String text;
      final float x;
      final float y;
      final float size;
      final int color;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      TextDraw(String text, float x, float y, float size, int color) {
         this.text = textx;
         this.x = xx;
         this.y = yx;
         this.size = sizex;
         this.color = colorx;
      }
   }
}
