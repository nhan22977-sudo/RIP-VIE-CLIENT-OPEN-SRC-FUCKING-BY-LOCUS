package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.AttackEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.SendPacketEvent;
import me.drake.vieclient.core.event.events.ServerBlockUpdateEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1511;
import net.minecraft.class_1802;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2604;
import net.minecraft.class_2663;
import net.minecraft.class_2664;
import net.minecraft.class_2680;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_4184;
import net.minecraft.class_4969;

public class CrystalFX extends Module {
   private static final int[][] EDGES = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
   private static final int[][] FACES = new int[][]{{0, 1, 2, 3}, {4, 5, 6, 7}, {0, 1, 5, 4}, {2, 3, 7, 6}, {0, 3, 7, 4}, {1, 2, 6, 5}};
   private final EnumSetting preset = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Preset",
         "Preset configuration for CrystalPvP aesthetic",
         CrystalFX.Preset.CUSTOM
      )
   );
   private final EnumSetting popStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Pop Style",
         "Detonation aesthetic: clean, minimal, or rich",
         CrystalFX.PopStyle.CLEAN_HALO
      )
   );
   private final DoubleSetting maxShockwaveRadius = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Max Ring Radius",
         "Maximum expansion radius of crystal shockwaves",
         2.4,
         1.0,
         6.0,
         0.2
      )
   );
   private final BoolSetting crystalShockwave = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Shockwave Ring",
         "Expanding smooth shockwave ring when crystal explodes",
         true
      )
   );
   private final BoolSetting crystalBeam = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Crystal Beam",
         "Vertical ascending light flare at detonation center",
         false
      )
   );
   private final BoolSetting crystalShards = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Crystal Shards",
         "3D crystalline shards bursting outward with gravity trails",
         false
      )
   );
   private final BoolSetting crystalPlaceFX = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Crystal Place Resonance",
         "Expanding crystalline resonance ring when placing an end crystal",
         true
      )
   );
   private final BoolSetting obsidianHighlight = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Obsidian Highlight",
         "Highlight supporting obsidian block upon crystal pop",
         true
      )
   );
   private final BoolSetting obsidianBox = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Obsidian Box",
         "Render clean outer bounding box and face fill on obsidian",
         true
      )
   );
   private final BoolSetting obsidianCore = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Obsidian Core",
         "Render glowing inner core cube inside obsidian",
         true
      )
   );
   private final BoolSetting obsidianAura = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Obsidian Aura",
         "Render sleek expanding surface ring from obsidian top face",
         true
      )
   );
   private final DoubleSetting obsidianDuration = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Obsidian Duration",
         "Duration of obsidian highlight effect (ms)",
         550.0,
         200.0,
         2000.0,
         50.0
      )
   );
   private final BoolSetting crystalGlow = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Crystal Glow",
         "Stylish clean ambient glow and pedestal ring for active crystals",
         true
      )
   );
   private final EnumSetting glowStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Glow Style",
         "Aesthetic styling for crystal glow",
         CrystalFX.GlowStyle.CLEAN_AURA
      )
   );
   private final BoolSetting glowPedestal = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Glow Pedestal",
         "Render elegant placement ring on the obsidian base",
         true
      )
   );
   private final DoubleSetting glowRange = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Glow Range",
         "Maximum distance to render crystal glow",
         24.0,
         8.0,
         48.0,
         2.0
      )
   );
   private final DoubleSetting glowAlpha = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Glow Opacity",
         "Luminance of crystal glow effects",
         140.0,
         30.0,
         255.0,
         5.0
      )
   );
   private final BoolSetting totemHalo = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Totem Pop Halo",
         "Sacred mandala halo expanding horizontally on totem pop",
         true
      )
   );
   private final BoolSetting totemWings = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Totem Wings",
         "Divine celestial angel wings silhouette flaring out on totem pop",
         true
      )
   );
   private final BoolSetting totemSoulRise = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Totem Soul Ascension",
         "Rising ethereal golden soul motes ascending to the heavens",
         true
      )
   );
   private final BoolSetting totemScreenFlash = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Totem Screen Flash",
         "Subtle golden celestial vignette screen flash on pop",
         false
      )
   );
   private final BoolSetting popBadge = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Pop Counter Badge",
         "Floating glowing POP badge with player name and pop count",
         true
      )
   );
   private final BoolSetting totemSparks = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Totem Sparks",
         "Golden radiant sparkles bursting on totem consumption",
         true
      )
   );
   private final EnumSetting theme = (EnumSetting)this.addSetting(
      new EnumSetting(
         "FxTheme",
         "Color palette for crystal and combat effects",
         CrystalFX.Theme.CRYSTAL_PURPLE
      )
   );
   private final ColorSetting customColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Custom Color",
         "Base color when not using rainbow",
         -2601993
      )
   );
   private final DoubleSetting thickness = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Thickness",
         "Shockwave line thickness",
         1.4,
         0.6,
         4.0,
         0.2
      )
   );
   private final IntSetting particleCount = (IntSetting)this.addSetting(
      new IntSetting(
         "Particles",
         "Number of spark / shard particles per burst",
         16,
         6,
         48,
         2
      )
   );
   private final BoolSetting neonBloom = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Neon Glow",
         "Multi-pass glowing bloom outline on all rings and boxes",
         true
      )
   );
   private final BoolSetting anchorConvergence = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Anchor Convergence FX",
         "Inward converging particle vortex when placing and using respawn anchors",
         true
      )
   );
   private final BoolSetting anchorPlaceFX = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Anchor Place Vortex",
         "Particles converge inward upon placing an anchor",
         true
      )
   );
   private final BoolSetting anchorChargeFX = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Anchor Charge Vortex",
         "Radiant particles converge inward when charging anchor with glowstone",
         true
      )
   );
   private final BoolSetting anchorExplodeFX = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Anchor Detonate Vortex",
         "Violent gravitational implosion particles when anchor detonates",
         true
      )
   );
   private final BoolSetting anchorPortalAura = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Anchor Portal Aura",
         "Ambient void portal aura on charged respawn anchors in world",
         true
      )
   );
   private final EnumSetting anchorColorMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Anchor Palette",
         "Color palette for anchor convergence particles",
         CrystalFX.AnchorPalette.NETHER_ANCHOR
      )
   );
   private final IntSetting anchorParticleCount = (IntSetting)this.addSetting(
      new IntSetting(
         "Anchor Particles",
         "Density of converging particles",
         28,
         12,
         64,
         2
      )
   );
   private final DoubleSetting anchorRadius = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Convergence Radius",
         "Outer radius where particles spawn before converging inward",
         2.4,
         1.2,
         4.5,
         0.2
      )
   );
   private final DoubleSetting anchorVortexSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Vortex Spiral Speed",
         "Rotational swirl speed as particles converge into the anchor",
         1.8,
         0.5,
         4.0,
         0.1
      )
   );
   private final BoolSetting anchorContractingRing = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Contracting Rune Ring",
         "Collapsing dimensional rune ring shrinking into the anchor",
         true
      )
   );
   private final BoolSetting anchorTracerTrails = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Particle Motion Tails",
         "Render sleek directional motion tails behind converging particles",
         true
      )
   );
   private static final int MAX_EFFECTS = 48;
   private static final int MAX_ANCHOR_EFFECTS = 32;
   private static final Color[] ANCHOR_PLACE_COLORS = new Color[]{
      new Color(138, 43, 226), new Color(199, 125, 255), new Color(0, 240, 255), new Color(255, 255, 255)
   };
   private static final Color[] ANCHOR_CHARGE_COLORS = new Color[]{
      new Color(255, 200, 20), new Color(255, 140, 0), new Color(255, 245, 160), new Color(255, 255, 255)
   };
   private static final Color[] ANCHOR_EXPLODE_COLORS = new Color[]{
      new Color(255, 20, 147), new Color(148, 0, 211), new Color(0, 255, 255), new Color(255, 255, 255)
   };
   private final List shockwaves = new ArrayList();
   private final List beams = new ArrayList();
   private final List badges = new ArrayList();
   private final List bursts = new ArrayList();
   private final List obsidianEffects = new ArrayList();
   private final List anchorEffects = new ArrayList();
   private final List totemWingsEffects = new ArrayList();
   private final List totemSoulEffects = new ArrayList();
   private long screenFlashUntil = 0L;
   private final Map trackedAnchors = new HashMap();
   private final Map localDebounce = new HashMap();
   private final Map popCountMap = new HashMap();
   private CrystalFX.CamProj camProj;
   private int fbW;
   private int fbH;
   private double scale;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public CrystalFX() {
      super(
         "CrystalFX",
         "Spectacular CPVP visual effects: crystal auras, anchor convergence, obsidian core/box, glow & pop styles",
         Category.CUSTOMIZE
      );
      this.preset.onChanged(this::applyPreset);
      this.totemWings.visibleWhen(this.totemHalo::get);
      this.totemSoulRise.visibleWhen(this.totemHalo::get);
      this.totemScreenFlash.visibleWhen(this.totemHalo::get);
      this.maxShockwaveRadius.visibleWhen(this.crystalShockwave::get);
      this.obsidianBox.visibleWhen(this.obsidianHighlight::get);
      this.obsidianCore.visibleWhen(this.obsidianHighlight::get);
      this.obsidianAura.visibleWhen(this.obsidianHighlight::get);
      this.glowStyle.visibleWhen(this.crystalGlow::get);
      this.glowPedestal.visibleWhen(this.crystalGlow::get);
      this.customColor.visibleWhen(() -> this.theme.get() != CrystalFX.Theme.RAINBOW);
      this.obsidianDuration.setVisibleInGui(false);
      this.glowRange.setVisibleInGui(false);
      this.glowAlpha.setVisibleInGui(false);
      this.thickness.setVisibleInGui(false);
      this.particleCount.setVisibleInGui(false);
      this.anchorPlaceFX.visibleWhen(this.anchorConvergence::get);
      this.anchorChargeFX.visibleWhen(this.anchorConvergence::get);
      this.anchorExplodeFX.visibleWhen(this.anchorConvergence::get);
      this.anchorPortalAura.visibleWhen(this.anchorConvergence::get);
      this.anchorColorMode.visibleWhen(this.anchorConvergence::get);
      this.anchorParticleCount.visibleWhen(this.anchorConvergence::get);
      this.anchorContractingRing.visibleWhen(this.anchorConvergence::get);
      this.anchorRadius.setVisibleInGui(false);
      this.anchorVortexSpeed.setVisibleInGui(false);
      this.anchorTracerTrails.setVisibleInGui(false);
   }

   private void applyPreset(CrystalFX.Preset p) {
      if (p != null && p != CrystalFX.Preset.CUSTOM) {
         switch (p) {
            case COMPETITIVE_CLEAN:
               this.popStyle.set(CrystalFX.PopStyle.MINIMAL_PULSE);
               this.maxShockwaveRadius.set(1.8);
               this.crystalShockwave.set(true);
               this.crystalBeam.set(false);
               this.crystalShards.set(false);
               this.obsidianHighlight.set(true);
               this.obsidianBox.set(true);
               this.obsidianCore.set(true);
               this.obsidianAura.set(false);
               this.crystalGlow.set(true);
               this.glowStyle.set(CrystalFX.GlowStyle.MINIMAL_PULSE);
               this.theme.set(CrystalFX.Theme.CYAN_NEON);
               this.anchorConvergence.set(true);
               this.anchorParticleCount.set(18);
               this.anchorTracerTrails.set(false);
               this.totemHalo.set(true);
               this.totemWings.set(false);
               this.totemSoulRise.set(false);
               this.totemSparks.set(true);
               break;
            case CYBER_NEON:
               this.popStyle.set(CrystalFX.PopStyle.NEON_CYBER);
               this.maxShockwaveRadius.set(2.4);
               this.crystalShockwave.set(true);
               this.crystalBeam.set(true);
               this.crystalShards.set(false);
               this.obsidianHighlight.set(true);
               this.obsidianBox.set(true);
               this.obsidianCore.set(true);
               this.obsidianAura.set(true);
               this.crystalGlow.set(true);
               this.glowStyle.set(CrystalFX.GlowStyle.ORBITAL_RING);
               this.theme.set(CrystalFX.Theme.CYAN_NEON);
               this.neonBloom.set(true);
               this.anchorConvergence.set(true);
               this.anchorColorMode.set(CrystalFX.AnchorPalette.THEME);
               this.anchorTracerTrails.set(true);
               this.anchorContractingRing.set(true);
               this.totemHalo.set(true);
               this.totemWings.set(true);
               this.totemSoulRise.set(false);
               this.totemSparks.set(true);
               break;
            case CELESTIAL_VOID:
               this.popStyle.set(CrystalFX.PopStyle.COSMIC_SUPERNOVA);
               this.maxShockwaveRadius.set(2.8);
               this.crystalShockwave.set(true);
               this.crystalBeam.set(true);
               this.crystalShards.set(true);
               this.obsidianHighlight.set(true);
               this.obsidianBox.set(true);
               this.obsidianCore.set(true);
               this.obsidianAura.set(true);
               this.crystalGlow.set(true);
               this.glowStyle.set(CrystalFX.GlowStyle.FULL_STYLISH);
               this.theme.set(CrystalFX.Theme.CRYSTAL_PURPLE);
               this.neonBloom.set(true);
               this.anchorConvergence.set(true);
               this.anchorColorMode.set(CrystalFX.AnchorPalette.NETHER_ANCHOR);
               this.anchorParticleCount.set(36);
               this.anchorContractingRing.set(true);
               this.anchorTracerTrails.set(true);
               this.totemHalo.set(true);
               this.totemWings.set(true);
               this.totemSoulRise.set(true);
               this.totemSparks.set(true);
               break;
            case SOLAR_GOD:
               this.popStyle.set(CrystalFX.PopStyle.SMOOTH_SHOCKWAVE);
               this.crystalBeam.set(true);
               this.crystalGlow.set(true);
               this.glowStyle.set(CrystalFX.GlowStyle.CLEAN_AURA);
               this.obsidianHighlight.set(true);
               this.theme.set(CrystalFX.Theme.GOLDEN_SUN);
               this.neonBloom.set(true);
               this.anchorConvergence.set(true);
               this.anchorColorMode.set(CrystalFX.AnchorPalette.THEME);
               this.totemHalo.set(true);
               this.totemWings.set(true);
               this.totemSoulRise.set(true);
               this.totemSparks.set(true);
               break;
            case BLOOD_RAGE:
               this.popStyle.set(CrystalFX.PopStyle.FULL_FX);
               this.crystalShards.set(true);
               this.crystalGlow.set(true);
               this.glowStyle.set(CrystalFX.GlowStyle.MINIMAL_PULSE);
               this.obsidianHighlight.set(true);
               this.theme.set(CrystalFX.Theme.CRIMSON_VOID);
               this.anchorConvergence.set(true);
               this.anchorColorMode.set(CrystalFX.AnchorPalette.THEME);
               this.totemHalo.set(true);
               this.totemWings.set(true);
               this.popBadge.set(true);
               this.totemSparks.set(true);
               break;
            case RAINBOW_HYPER:
               this.popStyle.set(CrystalFX.PopStyle.PRISMATIC_VORTEX);
               this.crystalGlow.set(true);
               this.glowStyle.set(CrystalFX.GlowStyle.FULL_STYLISH);
               this.obsidianHighlight.set(true);
               this.obsidianCore.set(true);
               this.obsidianAura.set(true);
               this.theme.set(CrystalFX.Theme.RAINBOW);
               this.neonBloom.set(true);
               this.anchorConvergence.set(true);
               this.anchorColorMode.set(CrystalFX.AnchorPalette.THEME);
               this.anchorContractingRing.set(true);
               this.anchorTracerTrails.set(true);
               this.totemWings.set(true);
               this.totemSoulRise.set(true);
         }
      }
   }

   @Override
   public void onDisable() {
      this.shockwaves.clear();
      this.beams.clear();
      this.badges.clear();
      this.bursts.clear();
      this.obsidianEffects.clear();
      this.anchorEffects.clear();
      this.totemWingsEffects.clear();
      this.totemSoulEffects.clear();
      this.screenFlashUntil = 0L;
      this.trackedAnchors.clear();
      this.localDebounce.clear();
      this.popCountMap.clear();
      super.onDisable();
   }

   @EventHandler
   public void onPacketReceive(ReceivePacketEvent event) {
      if (this.isInGame()) {
         if (event.getPacket() instanceof class_2663 var2 && var2.method_11470() == (35) && mc.field_1687 != null) {
            class_1297 var9 = var2.method_11469(mc.field_1687);
            if (var9 != null) {
               class_243 var4 = new class_243(var9.method_23317(), var9.method_23318(), var9.method_23321());
               String var5 = var9.method_5477().getString();
               int var6 = this.popCountMap.merge(var5, 1, Integer::sum);
               this.triggerTotemPop(var4, var5, var6);
            }
         }

         if (event.getPacket() instanceof class_2664 var7) {
            class_243 var11 = var7.comp_2883();
            this.triggerCrystalDetonation(var11);
            this.checkAnchorExplosion(var11);
         }

         if (event.getPacket() instanceof class_2604 var8 && var8.method_11169() == class_1299.field_6110) {
            this.triggerCrystalPlacement(new class_243(var8.method_11175(), var8.method_11174(), var8.method_11176()));
         }
      }
   }

   @EventHandler
   public void onServerBlockUpdate(ServerBlockUpdateEvent event) {
      if (this.isInGame() && mc.field_1687 != null) {
         class_2338 var2 = event.getPos();
         class_2680 var3 = event.getState();
         if (var3.method_27852(class_2246.field_23152)) {
            int var4 = (Integer)var3.method_11654(class_4969.field_23153);
            Integer var5 = (Integer)this.trackedAnchors.get(var2);
            if (var5 == null) {
               this.trackedAnchors.put(var2, var4);
               if ((Boolean)this.anchorConvergence.get() && (Boolean)this.anchorPlaceFX.get()) {
                  this.triggerAnchorConvergence(var2, CrystalFX.AnchorAction.PLACE, var4);
               }
            } else if (var4 > var5) {
               this.trackedAnchors.put(var2, var4);
               if ((Boolean)this.anchorConvergence.get() && (Boolean)this.anchorChargeFX.get()) {
                  this.triggerAnchorConvergence(var2, CrystalFX.AnchorAction.CHARGE, var4);
               }
            } else {
               this.trackedAnchors.put(var2, var4);
            }
         } else {
            Integer var6 = (Integer)this.trackedAnchors.remove(var2);
            if (var6 != null && var6 > 0 && (Boolean)this.anchorConvergence.get() && (Boolean)this.anchorExplodeFX.get()) {
               this.triggerAnchorConvergence(var2, CrystalFX.AnchorAction.EXPLODE, var6);
            }
         }
      }
   }

   @EventHandler
   public void onSendPacket(SendPacketEvent event) {
      if (this.isInGame() && mc.field_1687 != null && mc.field_1724 != null) {
         if ((Boolean)this.anchorConvergence.get()) {
            if (event.getPacket() instanceof class_2885 var2) {
               class_3965 var11 = var2.method_12543();
               class_2338 var4 = var11.method_17777();
               class_2680 var5 = mc.field_1687.method_8320(var4);
               boolean var6 = mc.field_1724.method_6047().method_31574(class_1802.field_23141);
               boolean var7 = mc.field_1724.method_6079().method_31574(class_1802.field_23141);
               boolean var8 = mc.field_1724.method_6047().method_31574(class_1802.field_8801);
               boolean var9 = mc.field_1724.method_6079().method_31574(class_1802.field_8801);
               if (var6 || var7) {
                  class_2338 var12 = var4.method_10093(var11.method_17780());
                  if ((Boolean)this.anchorPlaceFX.get() && this.shouldTriggerLocal(var12)) {
                     this.triggerAnchorConvergence(var12, CrystalFX.AnchorAction.PLACE, 0);
                  }
               } else if (var5.method_27852(class_2246.field_23152)) {
                  int var10 = (Integer)var5.method_11654(class_4969.field_23153);
                  if ((var8 || var9) && var10 < (4)) {
                     if ((Boolean)this.anchorChargeFX.get() && this.shouldTriggerLocal(var4)) {
                        this.triggerAnchorConvergence(var4, CrystalFX.AnchorAction.CHARGE, var10 + 1);
                     }
                  } else if (!var8 && !var9 && var10 > 0 && !this.isNether() && (Boolean)this.anchorExplodeFX.get() && this.shouldTriggerLocal(var4)) {
                     this.triggerAnchorConvergence(var4, CrystalFX.AnchorAction.EXPLODE, var10);
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onAttack(AttackEvent event) {
      if (this.isInGame() && event.getTarget() != null) {
         if (event.getTarget() instanceof class_1511 var2) {
            this.triggerCrystalDetonation(new class_243(var2.method_23317(), var2.method_23318(), var2.method_23321()));
         }
      }
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      if (this.isInGame() && mc.field_1724 != null) {
         this.popCountMap.clear();
         this.anchorEffects.clear();
         this.totemWingsEffects.clear();
         this.totemSoulEffects.clear();
         this.screenFlashUntil = 0L;
         this.trackedAnchors.clear();
         this.localDebounce.clear();
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (!this.isInGame()) {
            this.shockwaves.clear();
            this.beams.clear();
            this.badges.clear();
            this.bursts.clear();
            this.obsidianEffects.clear();
            this.anchorEffects.clear();
            this.totemWingsEffects.clear();
            this.totemSoulEffects.clear();
            this.screenFlashUntil = 0L;
            this.trackedAnchors.clear();
            this.localDebounce.clear();
         } else {
            this.shockwaves.removeIf(CrystalFX.Shockwave::isExpired);
            this.beams.removeIf(CrystalFX.BeamFlare::isExpired);
            this.badges.removeIf(CrystalFX.PopBadge::isExpired);
            this.bursts.removeIf(CrystalFX.SparkBurst::isExpired);
            this.obsidianEffects.removeIf(CrystalFX.ObsidianPopEffect::isExpired);
            this.anchorEffects.removeIf(CrystalFX.AnchorConvergenceEffect::isExpired);
            this.totemWingsEffects.removeIf(CrystalFX.TotemWingsEffect::isExpired);
            this.totemSoulEffects.removeIf(CrystalFX.TotemSoulEffect::isExpired);
         }
      }
   }

   private void triggerCrystalDetonation(class_243 pos) {
      Color var2 = this.resolveColor(0.0F);
      float var3 = (float)((Double)this.maxShockwaveRadius.get()).doubleValue();
      CrystalFX.PopStyle var4 = (CrystalFX.PopStyle)this.popStyle.get();
      if (this.shockwaves.size() >= 48) {
         this.shockwaves.remove(0);
      }

      switch (var4) {
         case CLEAN_HALO:
            this.shockwaves.add(new CrystalFX.Shockwave(pos, 380L, var3 * 0.9F, var2, var4));
            break;
         case EXPANDING_DISC:
            this.shockwaves.add(new CrystalFX.Shockwave(pos, 420L, var3, var2, var4));
            break;
         case MINIMAL_PULSE:
            this.shockwaves.add(new CrystalFX.Shockwave(pos, 280L, var3 * 0.75F, var2, var4));
            break;
         case SMOOTH_SHOCKWAVE:
            this.shockwaves.add(new CrystalFX.Shockwave(pos, 460L, var3, var2, var4));
            break;
         case FULL_FX:
            this.shockwaves.add(new CrystalFX.Shockwave(pos, 480L, var3, var2, var4));
            if ((Boolean)this.crystalBeam.get()) {
               if (this.beams.size() >= 16) {
                  this.beams.remove(0);
               }

               this.beams.add(new CrystalFX.BeamFlare(pos, 450L, var2));
            }

            if ((Boolean)this.crystalShards.get()) {
               if (this.bursts.size() >= 24) {
                  this.bursts.remove(0);
               }

               this.bursts.add(new CrystalFX.SparkBurst(pos, 500L, (Integer)this.particleCount.get(), var2, 3.2F, true));
            }
            break;
         case COSMIC_SUPERNOVA:
            this.shockwaves.add(new CrystalFX.Shockwave(pos, 540L, var3 * 1.3F, var2, var4));
            if ((Boolean)this.crystalBeam.get()) {
               if (this.beams.size() >= 16) {
                  this.beams.remove(0);
               }

               this.beams.add(new CrystalFX.BeamFlare(pos, 520L, var2));
            }

            if ((Boolean)this.crystalShards.get()) {
               if (this.bursts.size() >= 24) {
                  this.bursts.remove(0);
               }

               this.bursts.add(new CrystalFX.SparkBurst(pos, 600L, (int)(((Integer)this.particleCount.get()).intValue() * 1.5), var2, 4.2F, false));
            }
            break;
         case PRISMATIC_VORTEX:
            this.shockwaves.add(new CrystalFX.Shockwave(pos, 480L, var3 * 1.15F, var2, var4));
            if ((Boolean)this.crystalShards.get()) {
               if (this.bursts.size() >= 24) {
                  this.bursts.remove(0);
               }

               this.bursts.add(new CrystalFX.SparkBurst(pos, 520L, (Integer)this.particleCount.get(), var2, 3.6F, true));
            }
            break;
         case NEON_CYBER:
            this.shockwaves.add(new CrystalFX.Shockwave(pos, 420L, var3, var2, var4));
            if ((Boolean)this.crystalBeam.get()) {
               if (this.beams.size() >= 16) {
                  this.beams.remove(0);
               }

               this.beams.add(new CrystalFX.BeamFlare(pos, 400L, var2));
            }

            if ((Boolean)this.crystalShards.get()) {
               if (this.bursts.size() >= 24) {
                  this.bursts.remove(0);
               }

               this.bursts.add(new CrystalFX.SparkBurst(pos, 480L, (Integer)this.particleCount.get(), var2, 3.8F, true));
            }
      }

      if ((Boolean)this.obsidianHighlight.get() && mc.field_1687 != null) {
         class_2338 var5 = class_2338.method_49637(pos.field_1352, pos.field_1351 - 0.4, pos.field_1350);
         class_2338 var6 = null;
         if (this.isObsidianOrBedrock(var5)) {
            var6 = var5;
         } else if (this.isObsidianOrBedrock(var5.method_10074())) {
            var6 = var5.method_10074();
         } else {
            for (int var7 = -1; var7 <= 1 && var6 == null; var7++) {
               for (int var8 = -1; var8 <= 1 && var6 == null; var8++) {
                  class_2338 var9 = var5.method_10069(var7, 0, var8);
                  if (this.isObsidianOrBedrock(var9)) {
                     var6 = var9;
                  }
               }
            }
         }

         if (var6 != null) {
            if (this.obsidianEffects.size() >= 32) {
               this.obsidianEffects.remove(0);
            }

            this.obsidianEffects.add(new CrystalFX.ObsidianPopEffect(var6, (long)((Double)this.obsidianDuration.get()).doubleValue(), var2));
         }
      }
   }

   private void triggerCrystalPlacement(class_243 pos) {
      if ((Boolean)this.crystalPlaceFX.get()) {
         Color var2 = this.resolveColor(0.0F);
         if (this.shockwaves.size() >= (48)) {
            this.shockwaves.remove(0);
         }

         this.shockwaves.add(new CrystalFX.Shockwave(pos, 340L, 1.35F, var2, CrystalFX.PopStyle.CLEAN_HALO));
      }
   }

   private boolean isObsidianOrBedrock(class_2338 p) {
      if (mc.field_1687 != null && p != null) {
         class_2680 var2 = mc.field_1687.method_8320(p);
         return var2.method_27852(class_2246.field_10540) || var2.method_27852(class_2246.field_23152) || var2.method_27852(class_2246.field_9987);
      } else {
         return false;
      }
   }

   private void triggerTotemPop(class_243 pos, String victimName, int popCount) {
      Color var4 = new Color(255, 215, 64);
      if ((Boolean)this.totemHalo.get()) {
         if (this.shockwaves.size() >= (48)) {
            this.shockwaves.remove(0);
         }

         this.shockwaves.add(new CrystalFX.Shockwave(pos, 750L, (float)((Double)this.maxShockwaveRadius.get() * 1.3), var4, true));
      }

      if ((Boolean)this.totemWings.get()) {
         if (this.totemWingsEffects.size() >= (16)) {
            this.totemWingsEffects.remove(0);
         }

         this.totemWingsEffects.add(new CrystalFX.TotemWingsEffect(pos.method_1031(0.0, 0.85, 0.0), 850L, var4));
      }

      if ((Boolean)this.totemSoulRise.get()) {
         if (this.totemSoulEffects.size() >= (16)) {
            this.totemSoulEffects.remove(0);
         }

         this.totemSoulEffects.add(new CrystalFX.TotemSoulEffect(pos.method_1031(0.0, 0.5, 0.0), 1000L, 16, var4));
      }

      if ((Boolean)this.totemScreenFlash.get()) {
         this.screenFlashUntil = System.currentTimeMillis() + 450L;
      }

      if ((Boolean)this.popBadge.get()) {
         if (this.badges.size() >= (12)) {
            this.badges.remove(0);
         }

         this.badges.add(new CrystalFX.PopBadge(pos.method_1031(0.0, 1.8, 0.0), 1200L, victimName, popCount, var4));
      }

      if ((Boolean)this.totemSparks.get()) {
         if (this.bursts.size() >= (24)) {
            this.bursts.remove(0);
         }

         this.bursts
            .add(new CrystalFX.SparkBurst(pos.method_1031(0.0, 1.0, 0.0), 800L, (int)(((Integer)this.particleCount.get()).intValue() * 1.5), var4, 4.5F, false));
      }
   }

   private void checkAnchorExplosion(class_243 center) {
      if ((Boolean)this.anchorConvergence.get() && (Boolean)this.anchorExplodeFX.get()) {
         class_2338 var2 = class_2338.method_49637(center.field_1352, center.field_1351, center.field_1350);
         class_2338 var3 = null;
         double var4 = 18.0;

         for (class_2338 var7 : this.trackedAnchors.keySet()) {
            double var8 = var7.method_10262(var2);
            if (var8 < var4) {
               var4 = var8;
               var3 = var7;
            }
         }

         if (var3 != null) {
            int var12 = this.trackedAnchors.getOrDefault(var3, 4);
            this.trackedAnchors.remove(var3);
            this.triggerAnchorConvergence(var3, CrystalFX.AnchorAction.EXPLODE, var12);
         } else {
            if (mc.field_1687 != null) {
               for (int var11 = -2; var11 <= (2); var11++) {
                  for (int var13 = -2; var13 <= (2); var13++) {
                     for (int var14 = -2; var14 <= (2); var14++) {
                        class_2338 var9 = var2.method_10069(var11, var13, var14);
                        if (mc.field_1687.method_8320(var9).method_27852(class_2246.field_23152)) {
                           int var10 = (Integer)mc.field_1687.method_8320(var9).method_11654(class_4969.field_23153);
                           this.triggerAnchorConvergence(var9, CrystalFX.AnchorAction.EXPLODE, Math.max(1, var10));
                           return;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean shouldTriggerLocal(class_2338 pos) {
      long var2 = System.currentTimeMillis();
      Long var4 = (Long)this.localDebounce.get(pos);
      return var4 == null || var2 - var4 >= 320L;
   }

   private boolean isNether() {
      return mc.field_1687 != null && mc.field_1687.method_27983() == class_1937.field_25180;
   }

   public void triggerAnchorConvergence(class_2338 pos, CrystalFX.AnchorAction action, int chargeLevel) {
      if ((Boolean)this.anchorConvergence.get()) {
         long var4 = System.currentTimeMillis();
         Long var6 = (Long)this.localDebounce.get(pos);
         if (var6 == null || var4 - var6 >= 320L) {
            this.localDebounce.put(pos, var4);
            if (this.anchorEffects.size() >= (32)) {
               this.anchorEffects.remove(0);
            }
            long var7 = switch (action) {
               case PLACE -> 680L;
               case CHARGE -> 520L;
               case EXPLODE -> 420L;
            };
            float var9 = (float)((Double)this.anchorRadius.get()).doubleValue();
            float var10 = (float)((Double)this.anchorVortexSpeed.get()).doubleValue();
            int var11 = (Integer)this.anchorParticleCount.get();

            int var12 = switch (action) {
               case PLACE -> var11;
               case CHARGE -> (int)(var11 * (1.0F + chargeLevel * 0.15F));
               case EXPLODE -> (int)(var11 * 1.35F);
            };
            Color[] var13 = this.resolveAnchorColors(action, chargeLevel);
            this.anchorEffects.add(new CrystalFX.AnchorConvergenceEffect(pos, action, chargeLevel, var7, var9, var10, var12, var13));
         }
      }
   }

   private Color[] resolveAnchorColors(CrystalFX.AnchorAction action, int charge) {
      CrystalFX.AnchorPalette var3 = (CrystalFX.AnchorPalette)this.anchorColorMode.get();
      if (var3 == CrystalFX.AnchorPalette.THEME) {
         Color var7 = this.resolveColor(0.0F);
         Color var8 = this.resolveColor(0.2F);
         Color[] var10 = new Color[3];
         var10[0] = var7;
         var10[1] = var8;
         var10[2] = Color.WHITE;
         return var10;
      }

      if (var3 == CrystalFX.AnchorPalette.CUSTOM) {
         int var4 = (Integer)this.customColor.get();
         Color var5 = new Color(var4, true);
         Color var6 = new Color(
            Math.min(255, var5.getRed() + (40)),
            Math.min(255, var5.getGreen() + (40)),
            Math.min(255, var5.getBlue() + (40))
         );
         Color[] var9 = new Color[3];
         var9[0] = var5;
         var9[1] = var6;
         var9[2] = Color.WHITE;
         return var9;
      }

      return switch (action) {
         case PLACE -> ANCHOR_PLACE_COLORS;
         case CHARGE -> ANCHOR_CHARGE_COLORS;
         case EXPLODE -> ANCHOR_EXPLODE_COLORS;
      };
   }

   public List getAnchorEffectsForTest() {
      return this.anchorEffects;
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (this.isInGame()) {
         boolean var2 = !this.shockwaves.isEmpty()
            || !this.beams.isEmpty()
            || !this.badges.isEmpty()
            || !this.bursts.isEmpty()
            || !this.obsidianEffects.isEmpty()
            || !this.anchorEffects.isEmpty()
            || !this.totemWingsEffects.isEmpty()
            || !this.totemSoulEffects.isEmpty()
            || (Boolean)this.anchorPortalAura.get() && !this.trackedAnchors.isEmpty()
            || System.currentTimeMillis() < this.screenFlashUntil
            || (Boolean)this.crystalGlow.get();
         if (var2) {
            this.fbW = mc.method_22683().method_4489();
            this.fbH = mc.method_22683().method_4506();
            this.scale = mc.method_22683().method_4495();

            try (NanoVGFrame var3 = NanoVGRenderer.frame()) {
               if (!var3.isActive()) {
                  return;
               }

               this.camProj = this.computeCamProj(this.fbW, this.fbH);
               boolean var4 = (Boolean)this.neonBloom.get();
               float var5 = (float)((Double)this.thickness.get()).doubleValue();
               double var6 = System.currentTimeMillis() / 1000.0;

               for (CrystalFX.ObsidianPopEffect var9 : this.obsidianEffects) {
                  this.renderObsidianEffect(var9, (float)this.scale, var6);
               }

               if ((Boolean)this.crystalGlow.get() && mc.field_1687 != null) {
                  this.renderActiveCrystalsGlow((float)this.scale, var6);
               }

               for (CrystalFX.BeamFlare var19 : this.beams) {
                  this.renderBeam(var19, var5, var4);
               }

               for (CrystalFX.Shockwave var20 : this.shockwaves) {
                  this.renderShockwave(var20, var5, var4);
               }

               for (CrystalFX.SparkBurst var21 : this.bursts) {
                  this.renderSparkBurst(var21, var4);
               }

               for (CrystalFX.PopBadge var22 : this.badges) {
                  this.renderPopBadge(var22);
               }

               for (CrystalFX.AnchorConvergenceEffect var23 : this.anchorEffects) {
                  this.renderAnchorConvergence(var23, (float)this.scale, var6, var4);
               }

               if ((Boolean)this.anchorPortalAura.get() && mc.field_1687 != null) {
                  this.renderAnchorPortalAuras((float)this.scale, var6, var4);
               }

               for (CrystalFX.TotemWingsEffect var24 : this.totemWingsEffects) {
                  this.renderTotemWings(var24, (float)this.scale, var4);
               }

               for (CrystalFX.TotemSoulEffect var25 : this.totemSoulEffects) {
                  this.renderTotemSoulRise(var25, (float)this.scale, var4);
               }

               if (System.currentTimeMillis() < this.screenFlashUntil) {
                  this.renderScreenFlash();
               }
            }
         }
      }
   }

   private void renderAnchorConvergence(CrystalFX.AnchorConvergenceEffect effect, float scale, double time, boolean neon) {
      float var6 = effect.progress();
      if (!(var6 >= 1.0F)) {
         if ((Boolean)this.anchorContractingRing.get()) {
            float var7 = var6;
            float var8 = effect.outerRadius * (1.0F - (float)Math.pow(var7, 1.35F)) + 0.12F;
            float var9 = (1.0F - var7) * (var7 < 0.15F ? var7 / 0.15F : 1.0F);
            if (var9 > 0.02F) {
               Color var10 = withAlpha(effect.colors[0], clamp((int)(210.0F * var9)));
               this.drawWorldRing(
                  effect.pos.method_10263() + 0.5,
                  effect.pos.method_10264() + 1.0,
                  effect.pos.method_10260() + 0.5,
                  var8,
                  32,
                  var10,
                  1.4F,
                  scalex,
                  time * 2.5 * effect.vortexSpeed
               );
               if (effect.action == CrystalFX.AnchorAction.EXPLODE || effect.action == CrystalFX.AnchorAction.CHARGE) {
                  Color var11 = withAlpha(effect.colors[1 % effect.colors.length], clamp((int)(160.0F * var9)));
                  this.drawWorldRing(
                     effect.pos.method_10263() + 0.5,
                     effect.pos.method_10264() + 1.0,
                     effect.pos.method_10260() + 0.5,
                     var8 * 0.65F,
                     24,
                     var11,
                     1.1F,
                     scalex,
                     -time * 3.2 * effect.vortexSpeed
                  );
               }
            }
         }

         for (int var42 = 0; var42 < effect.count; var42++) {
            float var44 = var42 % 8 * 0.02F;
            float var46 = Math.max(0.0F, Math.min(1.0F, (var6 - var44) / (1.0F - var44)));
            if (!(var46 <= 0.0F) && !(var46 >= 1.0F)) {
               float var48 = 1.0F - var46;
               float var50 = effect.initialRadius[var42] * (float)Math.pow(var48, 1.85);
               float var12 = effect.initialAngle[var42] + effect.spinDirection[var42] * effect.vortexSpeed * (float)Math.pow(var46, 1.25) * 3.8F;
               float var13 = (effect.initialYOffset[var42] + (float)Math.sin((Math.PI * 5.0 / 2.0) * var46 + effect.initialAngle[var42]) * effect.curl[var42])
                  * (float)Math.pow(var48, 1.7);
               double var14 = effect.center.field_1352 + Math.cos(var12) * var50;
               double var16 = effect.center.field_1351 + var13;
               double var18 = effect.center.field_1350 + Math.sin(var12) * var50;
               double[] var20 = this.worldToScreen(var14, var16, var18);
               if (var20 != null) {
                  float var21 = (float)(var20[0] / scalex);
                  float var22 = (float)(var20[1] / scalex);
                  float var23 = var46 < 0.12F ? var46 / 0.12F : (float)Math.pow(var48, 0.55);
                  int var24 = clamp((int)(240.0F * var23));
                  if (var24 > 4) {
                     Color var25 = withAlpha(effect.particleColor[var42], var24);
                     if ((Boolean)this.anchorTracerTrails.get()) {
                        float var26 = Math.max(0.0F, var46 - 0.045F);
                        float var27 = 1.0F - var26;
                        float var28 = effect.initialRadius[var42] * (float)Math.pow(var27, 1.85);
                        float var29 = effect.initialAngle[var42] + effect.spinDirection[var42] * effect.vortexSpeed * (float)Math.pow(var26, 1.25) * 3.8F;
                        float var30 = (
                              effect.initialYOffset[var42] + (float)Math.sin((Math.PI * 5.0 / 2.0) * var26 + effect.initialAngle[var42]) * effect.curl[var42]
                           )
                           * (float)Math.pow(var27, 1.7);
                        double var31 = effect.center.field_1352 + Math.cos(var29) * var28;
                        double var33 = effect.center.field_1351 + var30;
                        double var35 = effect.center.field_1350 + Math.sin(var29) * var28;
                        double[] var37 = this.worldToScreen(var31, var33, var35);
                        if (var37 != null) {
                           float var38 = (float)(var37[0] / scalex);
                           float var39 = (float)(var37[1] / scalex);
                           float var40 = this.getDistanceScale(var20[2], 4.0F);
                           Color var41 = withAlpha(effect.particleColor[var42], (int)(var24 * 0.7F));
                           if (neon) {
                              NanoVGRenderer.drawEspTracer(var38, var39, var21, var22, Math.max(0.5F, 1.2F * var40), var41);
                           } else {
                              NanoVGRenderer.drawLine(var38, var39, var21, var22, Math.max(0.4F, 1.0F * var40), var41);
                           }
                        }
                     }

                     float var55 = this.getDistanceScale(var20[2], 4.0F);
                     float var56 = Math.max(0.5F, effect.pSize[var42] * var55);
                     NanoVGRenderer.drawCircle(var21, var22, var56, var25);
                     NanoVGRenderer.drawCircle(var21, var22, var56 * 0.45F, withAlpha(Color.WHITE, var24));
                     if (neon) {
                        this.drawStretchedRadialGlow(var21, var22, var56 * 3.2F, withAlpha(effect.particleColor[var42], (int)(var24 * 0.45F)));
                     }
                  }
               }
            }
         }

         double[] var43 = this.worldToScreen(effect.center.field_1352, effect.center.field_1351, effect.center.field_1350);
         if (var43 != null) {
            float var45 = (float)(var43[0] / scalex);
            float var47 = (float)(var43[1] / scalex);
            float var49 = this.getDistanceScale(var43[2], 4.0F);
            float var51 = (float)Math.sin(var6 * Math.PI);
            float var52 = (18.0F + 26.0F * var51) * var49;
            Color var53 = effect.colors[0];
            this.drawStretchedRadialGlow(var45, var47, var52, withAlpha(var53, (int)(95.0F * var51)));
            if (effect.action == CrystalFX.AnchorAction.EXPLODE && var6 > 0.6F) {
               float var54 = (var6 - 0.6F) / 0.4F;
               float var15 = (2.0F + 5.5F * var54) * var49;
               NanoVGRenderer.drawCircle(var45, var47, Math.max(0.6F, var15), withAlpha(Color.WHITE, clamp((int)(255.0F * var54))));
               if (neon) {
                  this.drawStretchedRadialGlow(var45, var47, var15 * 4.0F, withAlpha(Color.WHITE, clamp((int)(140.0F * var54))));
               }
            } else {
               NanoVGRenderer.drawCircle(var45, var47, Math.max(0.6F, 2.2F * var49), withAlpha(Color.WHITE, (int)(220.0F * var51)));
            }
         }
      }
   }

   private void renderObsidianEffect(CrystalFX.ObsidianPopEffect effect, float scale, double time) {
      float var5 = effect.progress();
      float var6 = easeOutCubic(var5);
      float var7 = (float)Math.pow(1.0F - var5, 1.4);
      if (!(var7 <= 0.01F)) {
         class_2338 var8 = effect.pos;
         double var9 = var8.method_10263();
         double var11 = var8.method_10264();
         double var13 = var8.method_10260();
         double var15 = var9 + 1.0;
         double var17 = var11 + 1.0;
         double var19 = var13 + 1.0;
         Color var21 = effect.color;
         if ((Boolean)this.obsidianBox.get()) {
            double[][] var22 = this.projectBox(var9, var11, var13, var15, var17, var19);
            if (var22 != null) {
               double var23 = this.camProj != null
                  ? Math.sqrt(
                     Math.pow(var8.method_10263() + 0.5 - this.camProj.camX, 2.0)
                        + Math.pow(var8.method_10264() + 0.5 - this.camProj.camY, 2.0)
                        + Math.pow(var8.method_10260() + 0.5 - this.camProj.camZ, 2.0)
                  )
                  : 4.0;
               float var25 = this.getDistanceScale(var23, 4.0F);
               Color var26 = withAlpha(var21, (int)(36.0F * var7));
               NanoVGRenderer.drawEspFill(var22, FACES, scalex, var26);
               Color var27 = withAlpha(var21, (int)(220.0F * var7));
               if ((Boolean)this.neonBloom.get()) {
                  Color var28 = withAlpha(var21, (int)(65.0F * var7));
                  NanoVGRenderer.drawEspBox(var22, EDGES, scalex, Math.max(0.8F, 2.2F * var25), var28);
               }

               NanoVGRenderer.drawEspBox(var22, EDGES, scalex, Math.max(0.5F, 1.1F * var25), var27);
            }
         }

         if ((Boolean)this.obsidianCore.get()) {
            double var39 = var9 + 0.5;
            double var24 = var11 + 0.5;
            double var42 = var13 + 0.5;
            double var44 = 0.16 + 0.06 * (1.0 - var5);
            double[][] var30 = this.projectBox(var39 - var44, var24 - var44, var42 - var44, var39 + var44, var24 + var44, var42 + var44);
            if (var30 != null) {
               Color var31 = withAlpha(var21, (int)(70.0F * var7));
               NanoVGRenderer.drawEspFill(var30, FACES, scalex, var31);
               double var32 = this.camProj != null
                  ? Math.sqrt(Math.pow(var39 - this.camProj.camX, 2.0) + Math.pow(var24 - this.camProj.camY, 2.0) + Math.pow(var42 - this.camProj.camZ, 2.0))
                  : 4.0;
               float var34 = this.getDistanceScale(var32, 4.0F);
               Color var35 = withAlpha(var21, (int)(240.0F * var7));
               NanoVGRenderer.drawEspBox(var30, EDGES, scalex, Math.max(0.5F, 1.2F * var34), var35);
               double[] var36 = this.worldToScreen(var39, var24, var42);
               if (var36 != null) {
                  float var37 = (float)(var36[0] / scalex);
                  float var38 = (float)(var36[1] / scalex);
                  NanoVGRenderer.drawCircle(var37, var38, Math.max(0.6F, 2.2F * var34), withAlpha(Color.WHITE, (int)(230.0F * var7)));
                  if ((Boolean)this.neonBloom.get()) {
                     this.drawStretchedRadialGlow(var37, var38, 12.0F * var34, withAlpha(var21, (int)(80.0F * var7)));
                  }
               }
            }
         }

         if ((Boolean)this.obsidianAura.get()) {
            double var40 = var9 + 0.5;
            double var41 = var13 + 0.5;
            double var43 = var17 + 0.02;
            double[] var45 = this.worldToScreen(var40, var43, var41);
            if (var45 != null) {
               float var29 = (float)(var45[0] / scalex);
               float var47 = (float)(var45[1] / scalex);
               float var49 = this.getDistanceScale(var45[2], 4.0F);
               float var50 = (24.0F + 16.0F * var6) * var49;
               this.drawStretchedRadialGlow(var29, var47, var50, withAlpha(var21, (int)(75.0F * var7)));
            }

            float var46 = 0.2F + 0.38F * var6;
            this.drawWorldRing(var40, var43, var41, var46, 24, withAlpha(var21, (int)(190.0F * var7)), 1.3F, scalex, 0.0);
            double var48 = 0.55 * (1.0 - var6);
            double[][] var51 = this.projectBox(var9 + 0.12, var43, var13 + 0.12, var15 - 0.12, var43 + var48, var19 - 0.12);
            if (var51 != null) {
               Color var33 = withAlpha(var21, (int)(28.0F * var7));
               NanoVGRenderer.drawEspFill(var51, FACES, scalex, var33);
            }
         }
      }
   }

   private void renderActiveCrystalsGlow(float scale, double time) {
      double var4 = (Double)this.glowRange.get() * (Double)this.glowRange.get();
      float var6 = (float)((Double)this.glowAlpha.get()).doubleValue() / 255.0F;
      Color var7 = this.resolveColor((float)(time * 0.1F));
      CrystalFX.GlowStyle var8 = (CrystalFX.GlowStyle)this.glowStyle.get();

      for (class_1297 var10 : mc.field_1687.method_18112()) {
         if (var10 instanceof class_1511 var11) {
            double var12 = mc.field_1724.method_5858(var11);
            if (!(var12 > var4)) {
               double var14 = var11.method_23317();
               double var16 = var11.method_23318();
               double var18 = var11.method_23321();
               double var20 = Math.sin(time * 3.2 + var11.method_5628()) * 0.05;
               double var22 = var16 + 0.95 + var20;
               if ((Boolean)this.glowPedestal.get() || var8 == CrystalFX.GlowStyle.PEDESTAL_ONLY || var8 == CrystalFX.GlowStyle.FULL_STYLISH) {
                  this.drawWorldRing(var14, var16 + 0.02, var18, 0.52F, 24, withAlpha(var7, (int)(160.0F * var6)), 1.2F, scalex, 0.0);
               }

               if (var8 != CrystalFX.GlowStyle.PEDESTAL_ONLY) {
                  if (var8 == CrystalFX.GlowStyle.CLEAN_AURA || var8 == CrystalFX.GlowStyle.FULL_STYLISH) {
                     double[] var30 = this.worldToScreen(var14, var22, var18);
                     if (var30 != null) {
                        float var31 = (float)(var30[0] / scalex);
                        float var32 = (float)(var30[1] / scalex);
                        float var33 = this.getDistanceScale(var30[2], 4.5F);
                        float var34 = 40.0F * var33;
                        this.drawStretchedRadialGlow(var31, var32, var34, withAlpha(var7, (int)(55.0F * var6)));
                     }
                  } else if (var8 == CrystalFX.GlowStyle.MINIMAL_PULSE) {
                     double[] var24 = this.worldToScreen(var14, var22, var18);
                     if (var24 != null) {
                        float var25 = (float)(var24[0] / scalex);
                        float var26 = (float)(var24[1] / scalex);
                        float var27 = this.getDistanceScale(var24[2], 4.5F);
                        float var28 = 0.85F + 0.15F * (float)Math.sin(time * 2.8 + var11.method_5628());
                        float var29 = 34.0F * var28 * var27;
                        this.drawStretchedRadialGlow(var25, var26, var29, withAlpha(var7, (int)(48.0F * var6 * var28)));
                     }
                  }

                  if (var8 == CrystalFX.GlowStyle.ORBITAL_RING || var8 == CrystalFX.GlowStyle.FULL_STYLISH) {
                     this.drawTiltedWorldRing(var14, var22, var18, 0.44F, 24, withAlpha(var7, (int)(185.0F * var6)), 1.2F, scalex, time * 2.0, 0.18);
                  }
               }
            }
         }
      }
   }

   private void drawTiltedWorldRing(double cx, double cy, double cz, float r, int segs, Color color, float width, float scale, double rot, double tilt) {
      double var16 = this.camProj != null
         ? Math.sqrt(Math.pow(cx - this.camProj.camX, 2.0) + Math.pow(cy - this.camProj.camY, 2.0) + Math.pow(cz - this.camProj.camZ, 2.0))
         : 4.0;
      float var18 = this.getDistanceScale(var16, 4.5F);
      float var19 = Math.max(0.5F, width * var18);
      double var20 = (Math.PI * 2) / segs;
      double[] var22 = null;
      NanoVGRenderer.beginLineBatch(color, var19);

      for (int var23 = 0; var23 <= segs; var23++) {
         double var24 = rot + var23 * var20;
         double var26 = Math.cos(var24) * r;
         double var28 = Math.sin(var24) * r;
         double var30 = Math.sin(var24) * (r * tilt);
         double[] var32 = this.worldToScreen(cx + var26, cy + var30, cz + var28);
         if (var22 != null && var32 != null) {
            NanoVGRenderer.batchLine((float)(var22[0] / scalex), (float)(var22[1] / scalex), (float)(var32[0] / scalex), (float)(var32[1] / scalex));
         }

         var22 = var32;
      }

      NanoVGRenderer.endLineBatch();
   }

   private void drawWorldRing(double cx, double cy, double cz, float r, int segs, Color color, float width, float scale, double rot) {
      double var14 = this.camProj != null
         ? Math.sqrt(Math.pow(cx - this.camProj.camX, 2.0) + Math.pow(cy - this.camProj.camY, 2.0) + Math.pow(cz - this.camProj.camZ, 2.0))
         : 4.0;
      float var16 = this.getDistanceScale(var14, 4.5F);
      float var17 = Math.max(0.5F, width * var16);
      double var18 = (Math.PI * 2) / segs;
      double[] var20 = this.ringPoint(new class_243(cx, cy, cz), 0, var18, r, (float)rot);
      NanoVGRenderer.beginLineBatch(color, var17);

      for (int var21 = 1; var21 <= segs; var21++) {
         double[] var22 = this.ringPoint(new class_243(cx, cy, cz), var21, var18, r, (float)rot);
         if (var20 != null && var22 != null) {
            NanoVGRenderer.batchLine((float)(var20[0] / scalex), (float)(var20[1] / scalex), (float)(var22[0] / scalex), (float)(var22[1] / scalex));
         }

         var20 = var22;
      }

      NanoVGRenderer.endLineBatch();
   }

   private void renderShockwave(CrystalFX.Shockwave sw, float th, boolean neon) {
      float var4 = sw.progress();
      float var5 = easeOutCubic(var4);
      float var6 = sw.maxRadius * var5;
      float var7 = (float)Math.pow(1.0F - var4, 1.4);
      int var8 = clamp((int)(var7 * 255.0F));
      if (var8 > 2) {
         Color var9 = withAlpha(sw.color, var8);
         if (sw.isTotemMandala) {
            byte var23 = 48;
            double var30 = (Math.PI * 2) / var23;
            float var13 = (float)(sw.elapsedSec() * 2.5);
            this.drawWorldRingWithScreenLines(sw.pos, var23, var30, var6, var13, th, var9, neon);
            float var43 = var6 * 0.65F;
            Color var50 = withAlpha(var9, clamp((int)(var8 * 0.7F)));
            this.drawWorldRingWithScreenLines(sw.pos, var23, var30, var43, -var13 * 1.5F, th * 0.8F, var50, false);
         } else {
            CrystalFX.PopStyle var10 = sw.style != null ? sw.style : CrystalFX.PopStyle.CLEAN_HALO;
            switch (var10) {
               case CLEAN_HALO:
                  byte var29 = 32;
                  double var36 = (Math.PI * 2) / var29;
                  this.drawWorldRingWithScreenLines(sw.pos, var29, var36, var6, 0.0F, th, var9, neon);
                  if (neon) {
                     double[] var42 = this.worldToScreen(sw.pos.field_1352, sw.pos.field_1351 + 0.05, sw.pos.field_1350);
                     if (var42 != null) {
                        float var49 = (float)(var42[0] / this.scale);
                        float var56 = (float)(var42[1] / this.scale);
                        float var62 = this.getDistanceScale(var42[2], 4.5F);
                        this.drawStretchedRadialGlow(var49, var56, 22.0F * (1.0F - var5) * var62, withAlpha(var9, (int)(60.0F * var7)));
                     }
                  }
                  break;
               case EXPANDING_DISC:
                  byte var28 = 32;
                  double var35 = (Math.PI * 2) / var28;
                  this.drawWorldRingWithScreenLines(sw.pos, var28, var35, var6, 0.0F, th, var9, neon);
                  this.drawWorldRingWithScreenLines(sw.pos, var28, var35, var6 * 0.62F, 0.0F, th * 0.8F, withAlpha(var9, (int)(var8 * 0.5F)), false);
                  double[] var41 = this.worldToScreen(sw.pos.field_1352, sw.pos.field_1351 + 0.05, sw.pos.field_1350);
                  if (var41 != null) {
                     float var48 = (float)(var41[0] / this.scale);
                     float var55 = (float)(var41[1] / this.scale);
                     float var61 = this.getDistanceScale(var41[2], 4.5F);
                     this.drawStretchedRadialGlow(var48, var55, 32.0F * var5 * var61, withAlpha(var9, (int)(70.0F * var7)));
                  }
                  break;
               case MINIMAL_PULSE:
                  byte var27 = 24;
                  double var34 = (Math.PI * 2) / var27;
                  this.drawWorldRingWithScreenLines(sw.pos, var27, var34, var6, 0.0F, th * 0.85F, var9, false);
                  double[] var40 = this.worldToScreen(sw.pos.field_1352, sw.pos.field_1351 + 0.05, sw.pos.field_1350);
                  if (var40 != null) {
                     float var47 = (float)(var40[0] / this.scale);
                     float var54 = (float)(var40[1] / this.scale);
                     float var60 = this.getDistanceScale(var40[2], 4.5F);
                     NanoVGRenderer.drawCircle(var47, var54, Math.max(0.6F, 2.0F * var60), withAlpha(Color.WHITE, (int)(180.0F * var7)));
                  }
                  break;
               case SMOOTH_SHOCKWAVE:
               case FULL_FX:
                  byte var26 = 36;
                  double var33 = (Math.PI * 2) / var26;
                  float var39 = (float)(sw.elapsedSec() * 1.5);
                  this.drawWorldRingWithScreenLines(sw.pos, var26, var33, var6, var39, th, var9, neon);
                  float var46 = var6 * 0.55F;
                  Color var53 = withAlpha(var9, clamp((int)(var8 * 0.45F)));
                  this.drawWorldRingWithScreenLines(sw.pos, var26, var33, var46, -var39, th * 0.75F, var53, false);
                  if (neon) {
                     double[] var59 = this.worldToScreen(sw.pos.field_1352, sw.pos.field_1351 + 0.05, sw.pos.field_1350);
                     if (var59 != null) {
                        float var65 = (float)(var59[0] / this.scale);
                        float var68 = (float)(var59[1] / this.scale);
                        float var70 = this.getDistanceScale(var59[2], 4.5F);
                        this.drawStretchedRadialGlow(var65, var68, 28.0F * (1.0F - var5) * var70, withAlpha(var9, (int)(55.0F * var7)));
                     }
                  }
                  break;
               case COSMIC_SUPERNOVA:
                  byte var25 = 40;
                  double var32 = (Math.PI * 2) / var25;
                  float var38 = (float)(sw.elapsedSec() * 2.2);
                  this.drawWorldRingWithScreenLines(sw.pos, var25, var32, var6, var38, th * 1.2F, var9, neon);
                  float var45 = var6 * 0.72F;
                  Color var52 = withAlpha(var9, clamp((int)(var8 * 0.65F)));
                  this.drawWorldRingWithScreenLines(sw.pos, var25, var32, var45, -var38 * 1.4F, th * 0.9F, var52, false);
                  float var58 = var6 * 0.4F;
                  Color var64 = withAlpha(var9, clamp((int)(var8 * 0.85F)));
                  this.drawWorldRingWithScreenLines(sw.pos, var25, var32, var58, var38 * 2.0F, th * 0.8F, var64, false);
                  double[] var67 = this.worldToScreen(sw.pos.field_1352, sw.pos.field_1351 + 0.05, sw.pos.field_1350);
                  if (var67 != null) {
                     float var69 = (float)(var67[0] / this.scale);
                     float var21 = (float)(var67[1] / this.scale);
                     float var22 = this.getDistanceScale(var67[2], 4.5F);
                     this.drawStretchedRadialGlow(var69, var21, 38.0F * (1.0F - var5) * var22, withAlpha(var9, (int)(85.0F * var7)));
                     NanoVGRenderer.drawCircle(var69, var21, Math.max(0.7F, 3.2F * (1.0F - var5) * var22), withAlpha(Color.WHITE, (int)(240.0F * var7)));
                  }
                  break;
               case PRISMATIC_VORTEX:
                  byte var24 = 36;
                  double var31 = (Math.PI * 2) / var24;
                  float var37 = (float)(sw.elapsedSec() * 3.5);
                  this.drawWorldRingWithScreenLines(sw.pos, var24, var31, var6, var37, th, var9, neon);
                  float var44 = var6 * 0.58F;
                  Color var51 = withAlpha(var9, clamp((int)(var8 * 0.55F)));
                  this.drawWorldRingWithScreenLines(sw.pos, var24, var31, var44, -var37 * 2.2F, th * 0.8F, var51, false);
                  double[] var57 = this.worldToScreen(sw.pos.field_1352, sw.pos.field_1351 + 0.05, sw.pos.field_1350);
                  if (var57 != null) {
                     float var63 = (float)(var57[0] / this.scale);
                     float var66 = (float)(var57[1] / this.scale);
                     float var20 = this.getDistanceScale(var57[2], 4.5F);
                     this.drawStretchedRadialGlow(var63, var66, 26.0F * var5 * var20, withAlpha(var9, (int)(70.0F * var7)));
                  }
                  break;
               case NEON_CYBER:
                  byte var11 = 8;
                  double var12 = (Math.PI * 2) / var11;
                  float var14 = (float)(sw.elapsedSec() * 1.8);
                  this.drawWorldRingWithScreenLines(sw.pos, var11, var12, var6, var14, th * 1.4F, var9, neon);
                  float var15 = var6 * 0.65F;
                  this.drawWorldRingWithScreenLines(sw.pos, 16, Math.PI / 8, var15, -var14, th * 0.7F, withAlpha(var9, (int)(var8 * 0.5F)), false);
                  double[] var16 = this.worldToScreen(sw.pos.field_1352, sw.pos.field_1351 + 0.05, sw.pos.field_1350);
                  if (var16 != null) {
                     float var17 = (float)(var16[0] / this.scale);
                     float var18 = (float)(var16[1] / this.scale);
                     float var19 = this.getDistanceScale(var16[2], 4.5F);
                     NanoVGRenderer.drawCircle(var17, var18, Math.max(0.6F, 2.5F * var19), withAlpha(Color.WHITE, (int)(220.0F * var7)));
                     if (neon) {
                        this.drawStretchedRadialGlow(var17, var18, 20.0F * var19, withAlpha(var9, (int)(60.0F * var7)));
                     }
                  }
            }
         }
      }
   }

   private void drawWorldRingWithScreenLines(class_243 pos, int segs, double step, float r, float rot, float th, Color col, boolean neon) {
      double var10 = this.camProj != null
         ? Math.sqrt(
            Math.pow(pos.field_1352 - this.camProj.camX, 2.0)
               + Math.pow(pos.field_1351 - this.camProj.camY, 2.0)
               + Math.pow(pos.field_1350 - this.camProj.camZ, 2.0)
         )
         : 4.0;
      float var12 = this.getDistanceScale(var10, 4.5F);
      float var13 = Math.max(0.5F, th * var12);
      double[] var14 = this.ringPoint(pos, 0, step, r, rot);

      for (int var15 = 1; var15 <= segs; var15++) {
         double[] var16 = this.ringPoint(pos, var15, step, r, rot);
         if (var14 != null && var16 != null) {
            float var17 = (float)(var14[0] / this.scale);
            float var18 = (float)(var14[1] / this.scale);
            float var19 = (float)(var16[0] / this.scale);
            float var20 = (float)(var16[1] / this.scale);
            if (neon) {
               NanoVGRenderer.drawEspTracer(var17, var18, var19, var20, var13, col);
            } else {
               NanoVGRenderer.drawLine(var17, var18, var19, var20, var13, col);
            }
         }

         var14 = var16;
      }
   }

   private void renderBeam(CrystalFX.BeamFlare beam, float th, boolean neon) {
      float var4 = beam.progress();
      float var5 = 1.0F - var4;
      int var6 = clamp((int)(var5 * 255.0F));
      Color var7 = new Color(beam.color.getRed(), beam.color.getGreen(), beam.color.getBlue(), var6);
      double var8 = beam.pos.field_1352;
      double var10 = beam.pos.field_1351;
      double var12 = beam.pos.field_1350;
      double var14 = 14.0 * (1.0 - var4 * 0.3);
      double[] var16 = this.worldToScreen(var8, var10, var12);
      double[] var17 = this.worldToScreen(var8, var10 + var14, var12);
      if (var16 != null && var17 != null) {
         float var18 = this.getDistanceScale(var16[2], 5.0F);
         float var19 = (float)(var16[0] / this.scale);
         float var20 = (float)(var16[1] / this.scale);
         float var21 = (float)(var17[0] / this.scale);
         float var22 = (float)(var17[1] / this.scale);
         if (neon) {
            NanoVGRenderer.drawEspTracer(var19, var20, var21, var22, Math.max(0.6F, th * 2.2F * var18), var7);
         } else {
            NanoVGRenderer.drawLine(var19, var20, var21, var22, Math.max(0.5F, th * 1.5F * var18), var7);
         }
      }
   }

   private void renderSparkBurst(CrystalFX.SparkBurst sb, boolean neon) {
      float var3 = sb.progress();
      float var4 = 1.0F - var3;
      int var5 = clamp((int)(var4 * 255.0F));
      Color var6 = new Color(sb.color.getRed(), sb.color.getGreen(), sb.color.getBlue(), var5);
      float var7 = sb.elapsedSec();

      for (int var8 = 0; var8 < sb.count; var8++) {
         float var9 = sb.speed[var8] * var7;
         double var10 = sb.pos.field_1352 + Math.cos(sb.angle[var8]) * var9;
         double var12 = sb.pos.field_1350 + Math.sin(sb.angle[var8]) * var9;
         double var14 = sb.pos.field_1351 + sb.up[var8] * var7 - (sb.gravity ? 4.9 * var7 * var7 : 0.0);
         double[] var16 = this.worldToScreen(var10, var14, var12);
         if (var16 != null) {
            float var17 = this.getDistanceScale(var16[2], 4.0F);
            float var18 = (float)(var16[0] / this.scale);
            float var19 = (float)(var16[1] / this.scale);
            if (neon) {
               NanoVGRenderer.drawCircle(var18, var19, Math.max(0.5F, 2.5F * var17), var6);
               NanoVGRenderer.drawCircle(var18, var19, Math.max(0.3F, 1.0F * var17), new Color(255, 255, 255, var5));
            } else {
               NanoVGRenderer.drawCircle(var18, var19, Math.max(0.4F, 1.8F * var17), var6);
            }
         }
      }
   }

   private void renderPopBadge(CrystalFX.PopBadge badge) {
      float var2 = badge.progress();
      float var3 = 1.0F - var2;
      if (!(var3 <= 0.05F)) {
         double var4 = var2 * 0.6;
         double[] var6 = this.worldToScreen(badge.pos.field_1352, badge.pos.field_1351 + var4, badge.pos.field_1350);
         if (var6 != null) {
            float var7 = (float)(var6[0] / this.scale);
            float var8 = (float)(var6[1] / this.scale);
            int var9 = clamp((int)(var3 * 255.0F));
            float var10 = this.getDistanceScale(var6[2], 5.0F);
            String var11 = "§lPOP §r" + badge.playerName + " §e#" + badge.popCount;
            float var12 = Math.max(4.0F, 9.5F * var10);
            float var13 = NanoVGRenderer.getTextWidth(var11, var12);
            float var14 = 6.0F * var10;
            float var15 = 3.0F * var10;
            float var16 = var7 - var13 / 2.0F - var14;
            float var17 = var8 - var12 / 2.0F - var15;
            float var18 = var13 + var14 * 2.0F;
            float var19 = var12 + var15 * 2.0F;
            NanoVGRenderer.drawRoundedRect(
               var16,
               var17,
               var18,
               var19,
               Math.max(1.5F, 4.0F * var10),
               new Color(15, 15, 20, clamp((int)(var9 * 0.75F)))
            );
            NanoVGRenderer.drawRoundedRectOutline(
               var16,
               var17,
               var18,
               var19,
               Math.max(1.5F, 4.0F * var10),
               Math.max(0.6F, 1.2F * var10),
               new Color(badge.color.getRed(), badge.color.getGreen(), badge.color.getBlue(), var9)
            );
            NanoVGRenderer.drawText(
               var11,
               var7 - var13 / 2.0F,
               var8 - var12 / 2.0F + 1.0F * var10,
               var12,
               new Color(255, 255, 255, var9)
            );
         }
      }
   }

   private void renderAnchorPortalAuras(float scale, double time, boolean neon) {
      if (!this.trackedAnchors.isEmpty()) {
         for (Entry var6 : this.trackedAnchors.entrySet()) {
            int var7 = (Integer)var6.getValue();
            if (var7 > 0) {
               class_2338 var8 = (class_2338)var6.getKey();
               double var9 = var8.method_10263() + 0.5;
               double var11 = var8.method_10264() + 1.01;
               double var13 = var8.method_10260() + 0.5;
               if (mc.field_1724 == null || !(mc.field_1724.method_5649(var9, var11, var13) > 1024.0)) {
                  double[] var15 = this.worldToScreen(var9, var11, var13);
                  if (var15 != null) {
                     float var16 = (float)(var15[0] / scalex);
                     float var17 = (float)(var15[1] / scalex);
                     float var18 = this.getDistanceScale(var15[2], 4.0F);
                     float var19 = 0.85F + 0.15F * (float)Math.sin(time * 3.0 + var8.hashCode());
                     float var20 = (16.0F + var7 * 4.0F) * var19 * var18;

                     Color var21 = switch (var7) {
                        case 2 -> new Color(220, 110, 230, 55);
                        case 3 -> new Color(255, 175, 40, 65);
                        case 4 -> new Color(255, 230, 80, 75);
                        default -> new Color(160, 50, 220, 50);
                     };
                     this.drawStretchedRadialGlow(var16, var17, var20, var21);
                  }

                  float var22 = (float)((time * 0.8 + (var8.method_10263() + var8.method_10260()) * 0.1) % 1.0);
                  float var23 = 0.15F + 0.32F * var22;
                  int var24 = clamp((int)(140.0F * (1.0F - var22)));
                  Color var25 = new Color(255, 210, 80, var24);
                  this.drawWorldRing(var9, var11, var13, var23, 20, var25, 1.1F, scalex, time * 1.5);
               }
            }
         }
      }
   }

   private void renderTotemWings(CrystalFX.TotemWingsEffect wings, float scale, boolean neon) {
      float var4 = wings.progress();
      if (!(var4 >= 1.0F)) {
         float var5 = easeOutCubic(var4);
         float var6 = (float)Math.pow(1.0F - var4, 1.2);
         int var7 = clamp((int)(230.0F * var6));
         if (var7 > 3) {
            Color var8 = withAlpha(wings.color, var7);
            class_243 var9 = wings.pos;
            byte var10 = 5;
            float var11 = 0.35F + 0.75F * var5;
            float var12 = 0.2F + 0.85F * var5;
            class_4184 var13 = mc.field_1773.method_19418();
            double var14 = Math.toRadians(var13.method_19330() + 90.0);
            double var16 = Math.cos(var14);
            double var18 = Math.sin(var14);
            double[] var20 = this.worldToScreen(var9.field_1352, var9.field_1351, var9.field_1350);
            if (var20 != null) {
               float var21 = (float)(var20[0] / scalex);
               float var22 = (float)(var20[1] / scalex);
               float var23 = this.getDistanceScale(var20[2], 4.0F);

               for (int var27 : new int[]{-1, 1}) {
                  for (int var28 = 0; var28 < var10; var28++) {
                     float var29 = (float)var28 / (var10 - 1);
                     double var30 = (0.7 + 0.5 * (1.0 - var29 * 0.4)) * var11;
                     double var32 = (0.15 + var29 * 0.55) * var27;
                     double var34 = var12 * 0.8 + (1.0 - var29) * 0.45;
                     double var36 = var9.field_1352 + var16 * (var32 * var30);
                     double var38 = var9.field_1351 + var34;
                     double var40 = var9.field_1350 + var18 * (var32 * var30);
                     double[] var42 = this.worldToScreen(var36, var38, var40);
                     if (var42 != null) {
                        float var43 = (float)(var42[0] / scalex);
                        float var44 = (float)(var42[1] / scalex);
                        if (neon) {
                           NanoVGRenderer.drawEspTracer(var21, var22, var43, var44, Math.max(0.6F, 1.8F * var23), var8);
                        } else {
                           NanoVGRenderer.drawLine(var21, var22, var43, var44, Math.max(0.5F, 1.4F * var23), var8);
                        }

                        NanoVGRenderer.drawCircle(var43, var44, Math.max(0.5F, 1.5F * var23), withAlpha(Color.WHITE, var7));
                     }
                  }
               }

               if (neon) {
                  this.drawStretchedRadialGlow(var21, var22, 24.0F * (1.0F - var5 * 0.5F) * var23, withAlpha(wings.color, clamp((int)(90.0F * var6))));
               }
            }
         }
      }
   }

   private void renderTotemSoulRise(CrystalFX.TotemSoulEffect souls, float scale, boolean neon) {
      float var4 = souls.progress();
      if (!(var4 >= 1.0F)) {
         float var5 = (float)Math.pow(1.0F - var4, 0.85);
         int var6 = clamp((int)(225.0F * var5));
         if (var6 > (3)) {
            Color var7 = withAlpha(souls.color, var6);
            Color var8 = withAlpha(Color.WHITE, var6);
            float var9 = souls.elapsedSec();

            for (int var10 = 0; var10 < souls.count; var10++) {
               float var11 = Math.max(0.0F, Math.min(1.0F, var4 * (1.0F + var10 % (4) * 0.15F)));
               double var12 = Math.sin(var9 * souls.swaySpeed[var10] + var10) * 0.18;
               double var14 = souls.pos.field_1352 + souls.offsetX[var10] + var12;
               double var16 = souls.pos.field_1351 + souls.riseSpeed[var10] * var11 * 2.2;
               double var18 = souls.pos.field_1350 + souls.offsetZ[var10] + Math.cos(var9 * souls.swaySpeed[var10] + var10) * 0.18;
               double[] var20 = this.worldToScreen(var14, var16, var18);
               if (var20 != null) {
                  float var21 = this.getDistanceScale(var20[2], 4.0F);
                  float var22 = (float)(var20[0] / scalex);
                  float var23 = (float)(var20[1] / scalex);
                  float var24 = Math.max(0.5F, (1.8F + var10 % (3) * 0.7F) * var21);
                  NanoVGRenderer.drawCircle(var22, var23, var24, var7);
                  NanoVGRenderer.drawCircle(var22, var23, var24 * 0.45F, var8);
                  if (neon && var10 % (2) == 0) {
                     this.drawStretchedRadialGlow(var22, var23, var24 * 3.5F, withAlpha(souls.color, clamp((int)(65.0F * var5))));
                  }
               }
            }
         }
      }
   }

   private void renderScreenFlash() {
      long var1 = this.screenFlashUntil - System.currentTimeMillis();
      if (var1 > 0L) {
         float var3 = (float)var1 / 450.0F;
         int var4 = clamp((int)(55.0F * var3));
         if (var4 > (2)) {
            Color var5 = new Color(255, 215, 64, var4);
            float var6 = 36.0F;
            float var7 = (float)(this.fbW / this.scale);
            float var8 = (float)(this.fbH / this.scale);
            NanoVGRenderer.drawRect(0.0F, 0.0F, var7, var6, var5);
            NanoVGRenderer.drawRect(0.0F, var8 - var6, var7, var6, var5);
            NanoVGRenderer.drawRect(0.0F, var6, var6, var8 - var6 * 2.0F, var5);
            NanoVGRenderer.drawRect(var7 - var6, var6, var6, var8 - var6 * 2.0F, var5);
         }
      }
   }

   public List getTotemWingsEffectsForTest() {
      return this.totemWingsEffects;
   }

   public List getTotemSoulEffectsForTest() {
      return this.totemSoulEffects;
   }

   public EnumSetting getPresetSetting() {
      return this.preset;
   }

   private double[] ringPoint(class_243 center, int i, double step, float radius, float rot) {
      double var7 = i * step + rot;
      return this.worldToScreen(center.field_1352 + Math.cos(var7) * radius, center.field_1351 + 0.05, center.field_1350 + Math.sin(var7) * radius);
   }

   private double[][] projectBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      double[][] var13 = new double[8][];
      var13[0] = this.worldToScreen(minX, minY, minZ);
      var13[1] = this.worldToScreen(maxX, minY, minZ);
      var13[2] = this.worldToScreen(maxX, minY, maxZ);
      var13[3] = this.worldToScreen(minX, minY, maxZ);
      var13[4] = this.worldToScreen(minX, maxY, minZ);
      var13[5] = this.worldToScreen(maxX, maxY, minZ);
      var13[6] = this.worldToScreen(maxX, maxY, maxZ);
      var13[7] = this.worldToScreen(minX, maxY, maxZ);
      boolean var14 = false;

      for (double[] var18 : var13) {
         if (var18 != null) {
            var14 = true;
            break;
         }
      }

      return var14 ? var13 : null;
   }

   private CrystalFX.CamProj computeCamProj(int fw, int fh) {
      class_4184 var3 = mc.field_1773.method_19418();
      class_243 var4 = var3.method_71156();
      double var5 = Math.toRadians(var3.method_19330() + 180.0);
      double var7 = Math.toRadians(var3.method_19329());
      float var9 = (float)Math.tan(Math.toRadians(((Integer)mc.field_1690.method_41808().method_41753()).intValue()) / 2.0);
      float var10 = TrueStretched.getEffectiveAspectRatio(fw, fh);
      return new CrystalFX.CamProj(
         var4.field_1352, var4.field_1351, var4.field_1350, Math.cos(var5), Math.sin(var5), Math.cos(var7), Math.sin(var7), var9, var10
      );
   }

   private void drawStretchedRadialGlow(float cx, float cy, float r, Color color) {
      float var5 = TrueStretched.getStretchFactor();
      boolean var6 = Math.abs(var5 - 1.0F) > 0.001F;
      if (var6) {
         NanoVGRenderer.save();
         NanoVGRenderer.translate(cx, cy);
         NanoVGRenderer.scale(var5, 1.0F);
         NanoVGRenderer.translate(-cx, -cy);
      }

      try {
         NanoVGRenderer.drawRadialGlow(cx, cy, r, color);
      } finally {
         if (var6) {
            NanoVGRenderer.restore();
         }
      }
   }

   private double[] worldToScreen(double wx, double wy, double wz) {
      CrystalFX.CamProj var7 = this.camProj;
      if (var7 == null) {
         return null;
      }

      float var8 = (float)(wx - var7.camX);
      float var9 = (float)(wy - var7.camY);
      float var10 = (float)(wz - var7.camZ);
      float var11 = (float)(var8 * var7.cosYaw + var10 * var7.sinYaw);
      float var12 = var9;
      float var13 = (float)(-var8 * var7.sinYaw + var10 * var7.cosYaw);
      float var14 = (float)(var12 * var7.cosPitch - var13 * var7.sinPitch);
      float var15 = (float)(var12 * var7.sinPitch + var13 * var7.cosPitch);
      if (var15 >= -0.05F) {
         return null;
      }

      float var16 = (float)(var11 / -var15 / (var7.tanHalfFov * var7.aspect) * (this.fbW / 2.0) + this.fbW / 2.0);
      float var17 = (float)(-var14 / -var15 / var7.tanHalfFov * (this.fbH / 2.0) + this.fbH / 2.0);
      double[] var10000 = new double[3];
      var10000[0] = var16;
      var10000[1] = var17;
      var10000[2] = -var15;
      return var10000;
   }

   private float getDistanceScale(double depth, float refDist) {
      if (depth <= 0.001) {
         return 1.0F;
      }

      float var4 = (float)(refDist / Math.max(refDist * 0.35, depth));
      return Math.max(0.15F, Math.min(1.8F, var4));
   }

   private float getDistanceScale(double depth) {
      return this.getDistanceScale(depth, 4.0F);
   }

   private Color resolveColor(float offset) {
      return switch ((CrystalFX.Theme)this.theme.get()) {
         case CRYSTAL_PURPLE -> new Color(216, 75, 247);
         case CYAN_NEON -> new Color(0, 245, 255);
         case GOLDEN_SUN -> new Color(255, 200, 40);
         case CRIMSON_VOID -> new Color(255, 30, 80);
         case RAINBOW -> {
            float var2 = ((float)(System.currentTimeMillis() % 6000L) / 6000.0F + offset) % 1.0F;
            int var3 = Color.HSBtoRGB(var2, 0.85F, 1.0F);
            yield new Color(
               var3 >> (16) & (255), var3 >> (8) & (255), var3 & (255)
            );
         }
      };
   }

   private static float easeOutCubic(float t) {
      float var1 = 1.0F - t;
      return 1.0F - var1 * var1 * var1;
   }

   private static int clamp(int v) {
      return Math.max(0, Math.min(255, v));
   }

   private static Color withAlpha(Color c, int a) {
      return new Color(c.getRed(), c.getGreen(), c.getBlue(), clamp(a));
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

   public enum AnchorAction {
      PLACE,
      CHARGE,
      EXPLODE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CrystalFX.AnchorAction[] $values() {
         CrystalFX.AnchorAction[] var10000 = new CrystalFX.AnchorAction[3];
         var10000[0] = PLACE;
         var10000[1] = CHARGE;
         var10000[2] = EXPLODE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(13);
         PLACE = new CrystalFX.AnchorAction();
         var10002 = new byte[6];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-46);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(15);
         var10002[5] = (byte)(-91);
         CHARGE = new CrystalFX.AnchorAction();
         var10002 = new byte[7];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-62);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(7);
         var10002[5] = (byte)(-92);
         var10002[6] = (byte)(96);
         EXPLODE = new CrystalFX.AnchorAction();
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

   public static final class AnchorConvergenceEffect {
      final class_2338 pos;
      final class_243 center;
      final CrystalFX.AnchorAction action;
      final int chargeLevel;
      final long spawnMs;
      final long totalMs;
      final Color[] colors;
      final float outerRadius;
      final float vortexSpeed;
      final int count;
      final float[] initialAngle;
      final float[] initialRadius;
      final float[] initialYOffset;
      final float[] spinDirection;
      final float[] curl;
      final Color[] particleColor;
      final float[] pSize;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      AnchorConvergenceEffect(
         class_2338 pos, CrystalFX.AnchorAction action, int chargeLevel, long totalMs, float outerRadius, float vortexSpeed, int count, Color[] colors
      ) {
         this.pos = posx;
         this.center = new class_243(posx.method_10263() + 0.5, posx.method_10264() + 0.75, posx.method_10260() + 0.5);
         this.action = actionx;
         this.chargeLevel = chargeLevelx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = totalMsx;
         this.colors = colorsx;
         this.outerRadius = outerRadiusx;
         this.vortexSpeed = vortexSpeedx;
         this.count = countx;
         this.initialAngle = new float[countx];
         this.initialRadius = new float[countx];
         this.initialYOffset = new float[countx];
         this.spinDirection = new float[countx];
         this.curl = new float[countx];
         this.particleColor = new Color[countx];
         this.pSize = new float[countx];
         Random var10 = new Random();

         for (int var11 = 0; var11 < countx; var11++) {
            this.initialAngle[var11] = var10.nextFloat() * (float) (Math.PI * 2);
            this.initialRadius[var11] = outerRadiusx * (0.65F + var10.nextFloat() * 0.7F);
            this.initialYOffset[var11] = -0.3F + var10.nextFloat() * 1.5F;
            this.spinDirection[var11] = actionx == CrystalFX.AnchorAction.EXPLODE && var10.nextFloat() < 0.25F ? -1.0F : 1.0F;
            this.curl[var11] = (var10.nextFloat() - 0.5F) * 0.4F;
            this.particleColor[var11] = colorsx[var10.nextInt(colorsx.length)];
            this.pSize[var11] = 1.6F + var10.nextFloat() * 1.6F;
         }
      }

      public float progress() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.spawnMs) / (float)this.totalMs);
      }

      public boolean isExpired() {
         return System.currentTimeMillis() - this.spawnMs >= this.totalMs;
      }

      public class_2338 getPos() {
         return this.pos;
      }

      public CrystalFX.AnchorAction getAction() {
         return this.action;
      }

      public int getCount() {
         return this.count;
      }

      public long getTotalMs() {
         return this.totalMs;
      }
   }

   public enum AnchorPalette {
      NETHER_ANCHOR,
      THEME,
      CUSTOM;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CrystalFX.AnchorPalette[] $values() {
         CrystalFX.AnchorPalette[] var10000 = new CrystalFX.AnchorPalette[3];
         var10000[0] = NETHER_ANCHOR;
         var10000[1] = THEME;
         var10000[2] = CUSTOM;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[13];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-106);
         var10002[4] = (byte)(68);
         var10002[5] = (byte)(-112);
         var10002[6] = (byte)(-65);
         var10002[7] = (byte)(-116);
         var10002[8] = (byte)(30);
         var10002[9] = (byte)(-31);
         var10002[10] = (byte)(-84);
         var10002[11] = (byte)(-71);
         var10002[12] = (byte)(11);
         NETHER_ANCHOR = new CrystalFX.AnchorPalette();
         var10002 = new byte[5];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-46);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(68);
         THEME = new CrystalFX.AnchorPalette();
         var10002 = new byte[6];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(78);
         var10002[5] = (byte)(-113);
         CUSTOM = new CrystalFX.AnchorPalette();
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }

   private static final class BeamFlare {
      final class_243 pos;
      final long spawnMs;
      final long totalMs;
      final Color color;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      BeamFlare(class_243 pos, long totalMs, Color color) {
         this.pos = posx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = totalMsx;
         this.color = colorx;
      }

      float progress() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.spawnMs) / (float)this.totalMs);
      }

      boolean isExpired() {
         return System.currentTimeMillis() - this.spawnMs >= this.totalMs;
      }
   }

   private static final class CamProj {
      final double camX;
      final double camY;
      final double camZ;
      final double cosYaw;
      final double sinYaw;
      final double cosPitch;
      final double sinPitch;
      final float tanHalfFov;
      final float aspect;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      CamProj(double camX, double camY, double camZ, double cosYaw, double sinYaw, double cosPitch, double sinPitch, float tanHalfFov, float aspect) {
         this.camX = camXx;
         this.camY = camYx;
         this.camZ = camZx;
         this.cosYaw = cosYawx;
         this.sinYaw = sinYawx;
         this.cosPitch = cosPitchx;
         this.sinPitch = sinPitchx;
         this.tanHalfFov = tanHalfFovx;
         this.aspect = aspectx;
      }
   }

   public enum GlowStyle {
      CLEAN_AURA,
      ORBITAL_RING,
      MINIMAL_PULSE,
      PEDESTAL_ONLY,
      FULL_STYLISH;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CrystalFX.GlowStyle[] $values() {
         CrystalFX.GlowStyle[] var10000 = new CrystalFX.GlowStyle[5];
         var10000[0] = CLEAN_AURA;
         var10000[1] = ORBITAL_RING;
         var10000[2] = MINIMAL_PULSE;
         var10000[3] = PEDESTAL_ONLY;
         var10000[4] = FULL_STYLISH;
         return var10000;
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

   private static final class ObsidianPopEffect {
      final class_2338 pos;
      final long spawnMs;
      final long totalMs;
      final Color color;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ObsidianPopEffect(class_2338 pos, long totalMs, Color color) {
         this.pos = posx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = totalMsx;
         this.color = colorx;
      }

      float progress() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.spawnMs) / (float)this.totalMs);
      }

      boolean isExpired() {
         return System.currentTimeMillis() - this.spawnMs >= this.totalMs;
      }
   }

   private static final class PopBadge {
      final class_243 pos;
      final long spawnMs;
      final long totalMs;
      final String playerName;
      final int popCount;
      final Color color;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      PopBadge(class_243 pos, long totalMs, String playerName, int popCount, Color color) {
         this.pos = posx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = totalMsx;
         this.playerName = playerNamex;
         this.popCount = popCountx;
         this.color = colorx;
      }

      float progress() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.spawnMs) / (float)this.totalMs);
      }

      boolean isExpired() {
         return System.currentTimeMillis() - this.spawnMs >= this.totalMs;
      }
   }

   public enum PopStyle {
      CLEAN_HALO,
      EXPANDING_DISC,
      MINIMAL_PULSE,
      SMOOTH_SHOCKWAVE,
      FULL_FX,
      COSMIC_SUPERNOVA,
      PRISMATIC_VORTEX,
      NEON_CYBER;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CrystalFX.PopStyle[] $values() {
         CrystalFX.PopStyle[] var10000 = new CrystalFX.PopStyle[8];
         var10000[0] = CLEAN_HALO;
         var10000[1] = EXPANDING_DISC;
         var10000[2] = MINIMAL_PULSE;
         var10000[3] = SMOOTH_SHOCKWAVE;
         var10000[4] = FULL_FX;
         var10000[5] = COSMIC_SUPERNOVA;
         var10000[6] = PRISMATIC_VORTEX;
         var10000[7] = NEON_CYBER;
         return var10000;
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

   public enum Preset {
      CUSTOM,
      COMPETITIVE_CLEAN,
      CYBER_NEON,
      CELESTIAL_VOID,
      SOLAR_GOD,
      BLOOD_RAGE,
      RAINBOW_HYPER;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CrystalFX.Preset[] $values() {
         CrystalFX.Preset[] var10000 = new CrystalFX.Preset[7];
         var10000[0] = CUSTOM;
         var10000[1] = COMPETITIVE_CLEAN;
         var10000[2] = CYBER_NEON;
         var10000[3] = CELESTIAL_VOID;
         var10000[4] = SOLAR_GOD;
         var10000[5] = BLOOD_RAGE;
         var10000[6] = RAINBOW_HYPER;
         return var10000;
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

   private static final class Shockwave {
      final class_243 pos;
      final long spawnMs;
      final long totalMs;
      final float maxRadius;
      final Color color;
      final boolean isTotemMandala;
      final CrystalFX.PopStyle style;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Shockwave(class_243 pos, long totalMs, float maxRadius, Color color, CrystalFX.PopStyle style) {
         this.pos = posx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = totalMsx;
         this.maxRadius = maxRadiusx;
         this.color = colorx;
         this.isTotemMandala = false;
         this.style = stylex;
      }

      Shockwave(class_243 pos, long totalMs, float maxRadius, Color color, boolean isTotemMandala) {
         this.pos = posx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = totalMsx;
         this.maxRadius = maxRadiusx;
         this.color = colorx;
         this.isTotemMandala = isTotemMandalax;
         this.style = CrystalFX.PopStyle.CLEAN_HALO;
      }

      float progress() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.spawnMs) / (float)this.totalMs);
      }

      float elapsedSec() {
         return (float)(System.currentTimeMillis() - this.spawnMs) / 1000.0F;
      }

      boolean isExpired() {
         return System.currentTimeMillis() - this.spawnMs >= this.totalMs;
      }
   }

   private static final class SparkBurst {
      final class_243 pos;
      final long spawnMs;
      final long totalMs;
      final int count;
      final Color color;
      final boolean gravity;
      final float[] angle;
      final float[] speed;
      final float[] up;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      SparkBurst(class_243 pos, long totalMs, int count, Color color, float baseSpeed, boolean gravity) {
         this.pos = posx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = totalMsx;
         this.count = countx;
         this.color = colorx;
         this.gravity = gravityx;
         this.angle = new float[countx];
         this.speed = new float[countx];
         this.up = new float[countx];
         Random var8 = new Random();

         for (int var9 = 0; var9 < countx; var9++) {
            this.angle[var9] = var8.nextFloat() * (float) (Math.PI * 2);
            this.speed[var9] = baseSpeed * 0.6F + var8.nextFloat() * (baseSpeed * 0.8F);
            this.up[var9] = 1.5F + var8.nextFloat() * 4.0F;
         }
      }

      float progress() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.spawnMs) / (float)this.totalMs);
      }

      float elapsedSec() {
         return (float)(System.currentTimeMillis() - this.spawnMs) / 1000.0F;
      }

      boolean isExpired() {
         return System.currentTimeMillis() - this.spawnMs >= this.totalMs;
      }
   }

   public enum Theme {
      CRYSTAL_PURPLE,
      CYAN_NEON,
      GOLDEN_SUN,
      CRIMSON_VOID,
      RAINBOW;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CrystalFX.Theme[] $values() {
         CrystalFX.Theme[] var10000 = new CrystalFX.Theme[5];
         var10000[0] = CRYSTAL_PURPLE;
         var10000[1] = CYAN_NEON;
         var10000[2] = GOLDEN_SUN;
         var10000[3] = CRIMSON_VOID;
         var10000[4] = RAINBOW;
         return var10000;
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }

   public static final class TotemSoulEffect {
      final class_243 pos;
      final long spawnMs;
      final long totalMs;
      final int count;
      final Color color;
      final float[] offsetX;
      final float[] offsetZ;
      final float[] swaySpeed;
      final float[] riseSpeed;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      TotemSoulEffect(class_243 pos, long totalMs, int count, Color color) {
         this.pos = posx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = totalMsx;
         this.count = countx;
         this.color = colorx;
         this.offsetX = new float[countx];
         this.offsetZ = new float[countx];
         this.swaySpeed = new float[countx];
         this.riseSpeed = new float[countx];
         Random var6 = new Random();

         for (int var7 = 0; var7 < countx; var7++) {
            double var8 = var6.nextFloat() * Math.PI * 2.0;
            double var10 = 0.15 + var6.nextFloat() * 0.75;
            this.offsetX[var7] = (float)(Math.cos(var8) * var10);
            this.offsetZ[var7] = (float)(Math.sin(var8) * var10);
            this.swaySpeed[var7] = 2.0F + var6.nextFloat() * 3.0F;
            this.riseSpeed[var7] = 1.0F + var6.nextFloat() * 1.6F;
         }
      }

      public float progress() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.spawnMs) / (float)this.totalMs);
      }

      public float elapsedSec() {
         return (float)(System.currentTimeMillis() - this.spawnMs) / 1000.0F;
      }

      public boolean isExpired() {
         return System.currentTimeMillis() - this.spawnMs >= this.totalMs;
      }

      public class_243 getPos() {
         return this.pos;
      }

      public int getCount() {
         return this.count;
      }

      public Color getColor() {
         return this.color;
      }
   }

   public static final class TotemWingsEffect {
      final class_243 pos;
      final long spawnMs;
      final long totalMs;
      final Color color;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      TotemWingsEffect(class_243 pos, long totalMs, Color color) {
         this.pos = posx;
         this.spawnMs = System.currentTimeMillis();
         this.totalMs = totalMsx;
         this.color = colorx;
      }

      public float progress() {
         return Math.min(1.0F, (float)(System.currentTimeMillis() - this.spawnMs) / (float)this.totalMs);
      }

      public boolean isExpired() {
         return System.currentTimeMillis() - this.spawnMs >= this.totalMs;
      }

      public class_243 getPos() {
         return this.pos;
      }

      public Color getColor() {
         return this.color;
      }
   }
}
