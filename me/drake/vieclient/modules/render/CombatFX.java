package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.AttackEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.render.fx.CombatFxEngine;
import me.drake.vieclient.render.fx.KillBurst;

public class CombatFX extends Module {
   private final CombatFxEngine engine = new CombatFxEngine();
   private final CombatFxEngine.Config cfg = new CombatFxEngine.Config();
   private final EnumSetting preset = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Preset",
         "Preset aesthetic configuration for combat effects",
         CombatFX.CombatPreset.CUSTOM
      )
   );
   private final BoolSetting numbers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Numbers",
         "Floating damage numbers over entities",
         true
      )
   );
   private final EnumSetting numberStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "NumberStyle",
         "Damage number animation style",
         CombatFxEngine.NumberStyle.CLASSIC
      )
   );
   private final BoolSetting damageIcons = (BoolSetting)this.addSetting(
      new BoolSetting(
         "DamageIcons",
         "Show combat glyph icons (❤, ⚡, 🗡) on damage numbers",
         true
      )
   );
   private final DoubleSetting numberSize = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "NumberSize",
         "Damage number size multiplier",
         1.0,
         0.3,
         3.0,
         0.05
      )
   );
   private final DoubleSetting numberLife = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "NumberLife",
         "How long a number stays on screen (seconds)",
         1.2,
         0.4,
         4.0,
         0.1
      )
   );
   private final IntSetting mergeWindow = (IntSetting)this.addSetting(
      new IntSetting(
         "MergeWindow",
         "Hits on one entity within this many ms stack into one number",
         250,
         0,
         1200,
         25
      )
   );
   private final BoolSetting selfDamage = (BoolSetting)this.addSetting(
      new BoolSetting(
         "SelfDamage",
         "Show damage taken by you",
         true
      )
   );
   private final BoolSetting showHeal = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ShowHeal",
         "Show healing as green numbers",
         false
      )
   );
   private final BoolSetting occlusion = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Occlusion",
         "Dim numbers that are behind blocks",
         true
      )
   );
   private final ColorSetting damageColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "DamageColor",
         "Normal hit colour",
         -1
      )
   );
   private final ColorSetting critColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "CritColor",
         "Critical hit colour",
         -20448
      )
   );
   private final ColorSetting healColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "HealColor",
         "Healing colour",
         -10690438
      )
   );
   private final ColorSetting selfColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "SelfColor",
         "Damage-taken colour",
         -45747
      )
   );
   private final BoolSetting sparks = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Sparks",
         "Particle burst on every hit you land",
         true
      )
   );
   private final EnumSetting sparkStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "SparkStyle",
         "Hit particle look",
         CombatFxEngine.SparkStyle.SPARK
      )
   );
   private final IntSetting sparkCount = (IntSetting)this.addSetting(
      new IntSetting(
         "SparkCount",
         "Particles per hit",
         12,
         2,
         48
      )
   );
   private final ColorSetting sparkColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "SparkColor",
         "Hit particle colour",
         -3904
      )
   );
   private final BoolSetting killEffect = (BoolSetting)this.addSetting(
      new BoolSetting(
         "KillEffect",
         "Burst where an entity dies",
         true
      )
   );
   private final EnumSetting killStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "KillStyle",
         "Kill burst look",
         KillBurst.Style.SHOCKWAVE
      )
   );
   private final ColorSetting killColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "KillColor",
         "Kill burst colour",
         -4160260
      )
   );
   private final BoolSetting killLabel = (BoolSetting)this.addSetting(
      new BoolSetting(
         "KillLabel",
         "Show the victim's name on the burst",
         true
      )
   );
   private final BoolSetting killSound = (BoolSetting)this.addSetting(
      new BoolSetting(
         "KillSound",
         "Play a sound on kill",
         true
      )
   );
   private final BoolSetting combo = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Combo",
         "Count kills within 5s and show a multiplier",
         true
      )
   );
   private final BoolSetting targetMarker = (BoolSetting)this.addSetting(
      new BoolSetting(
         "TargetMarker",
         "Glowing target marker at entity center",
         true
      )
   );
   private final EnumSetting markerStyle = (EnumSetting)this.addSetting(
      new EnumSetting(
         "MarkerStyle",
         "Target marker look",
         CombatFxEngine.TargetMarkerStyle.ORBITAL
      )
   );
   private final ColorSetting markerColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "MarkerColor",
         "Target marker glow colour",
         -52378
      )
   );
   private final DoubleSetting markerSize = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "MarkerSize",
         "Target marker size multiplier",
         1.0,
         0.4,
         2.5,
         0.05
      )
   );
   private final EnumSetting targets = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Targets",
         "Which entities produce effects",
         CombatFxEngine.Targets.ALL
      )
   );
   private final EnumSetting source = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Source",
         "ANY shows all damage, MINE_ONLY only what you deal",
         CombatFxEngine.Source.ANY
      )
   );
   private final DoubleSetting range = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Range",
         "Max distance to track entities",
         64.0,
         8.0,
         128.0,
         4.0
      )
   );
   private final IntSetting maxFx = (IntSetting)this.addSetting(
      new IntSetting(
         "MaxFX",
         "Hard ceiling on live particles",
         220,
         40,
         600,
         20
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public CombatFX() {
      super(
         "CombatFX",
         "Damage numbers, hit particles and kill effects",
         Category.CUSTOMIZE
      );
      this.preset.onChanged(this::applyPreset);
      this.numberStyle.visibleWhen(this.numbers::get);
      this.damageIcons.visibleWhen(this.numbers::get);
      this.numberSize.visibleWhen(this.numbers::get);
      this.critColor.visibleWhen(this.numbers::get);
      this.selfDamage.visibleWhen(this.numbers::get);
      this.sparkStyle.visibleWhen(this.sparks::get);
      this.sparkColor.visibleWhen(this.sparks::get);
      this.sparkCount.visibleWhen(this.sparks::get);
      this.killStyle.visibleWhen(this.killEffect::get);
      this.killColor.visibleWhen(this.killEffect::get);
      this.killLabel.visibleWhen(this.killEffect::get);
      this.killSound.visibleWhen(this.killEffect::get);
      this.combo.visibleWhen(this.killEffect::get);
      this.markerStyle.visibleWhen(this.targetMarker::get);
      this.markerColor.visibleWhen(this.targetMarker::get);
      this.markerSize.visibleWhen(this.targetMarker::get);
      this.numberLife.setVisibleInGui(false);
      this.mergeWindow.setVisibleInGui(false);
      this.occlusion.setVisibleInGui(false);
      this.damageColor.setVisibleInGui(false);
      this.selfColor.setVisibleInGui(false);
      this.showHeal.setVisibleInGui(false);
      this.healColor.setVisibleInGui(false);
      this.range.setVisibleInGui(false);
      this.maxFx.setVisibleInGui(false);
      this.targets.setVisibleInGui(false);
      this.source.setVisibleInGui(false);
   }

   private void applyPreset(CombatFX.CombatPreset p) {
      if (p != null && p != CombatFX.CombatPreset.CUSTOM) {
         switch (p) {
            case COMPETITIVE:
               this.numbers.set(true);
               this.numberSize.set(0.9);
               this.numberStyle.set(CombatFxEngine.NumberStyle.CLASSIC);
               this.damageIcons.set(false);
               this.sparks.set(true);
               this.sparkStyle.set(CombatFxEngine.SparkStyle.SPARK);
               this.sparkCount.set(8);
               this.killEffect.set(true);
               this.killStyle.set(KillBurst.Style.SHOCKWAVE);
               this.targetMarker.set(true);
               this.markerStyle.set(CombatFxEngine.TargetMarkerStyle.CYBER_RETICLE);
               this.markerColor.set(-16718337);
               break;
            case ANIME_SLASH:
               this.numbers.set(true);
               this.numberSize.set(1.1);
               this.numberStyle.set(CombatFxEngine.NumberStyle.BOUNCE);
               this.damageIcons.set(true);
               this.sparks.set(true);
               this.sparkStyle.set(CombatFxEngine.SparkStyle.SLASH);
               this.sparkCount.set(18);
               this.killEffect.set(true);
               this.killStyle.set(KillBurst.Style.SAKURA);
               this.targetMarker.set(true);
               this.markerStyle.set(CombatFxEngine.TargetMarkerStyle.ORBITAL);
               this.markerColor.set(-45703);
               break;
            case BLOOD_CARNAGE:
               this.numbers.set(true);
               this.numberSize.set(1.05);
               this.numberStyle.set(CombatFxEngine.NumberStyle.BOUNCE);
               this.damageIcons.set(true);
               this.sparks.set(true);
               this.sparkStyle.set(CombatFxEngine.SparkStyle.BLOOD);
               this.sparkCount.set(24);
               this.killEffect.set(true);
               this.killStyle.set(KillBurst.Style.BLOOD_GEYSER);
               this.killColor.set(-58834);
               this.targetMarker.set(true);
               this.markerStyle.set(CombatFxEngine.TargetMarkerStyle.PULSE_CIRCLE);
               this.markerColor.set(-58834);
               break;
            case CYBERPUNK:
               this.numbers.set(true);
               this.numberSize.set(1.15);
               this.numberStyle.set(CombatFxEngine.NumberStyle.EXPAND_FADE);
               this.damageIcons.set(true);
               this.sparks.set(true);
               this.sparkStyle.set(CombatFxEngine.SparkStyle.PLASMA);
               this.sparkColor.set(-16715521);
               this.sparkCount.set(20);
               this.killEffect.set(true);
               this.killStyle.set(KillBurst.Style.CYBER_VOXEL);
               this.killColor.set(-65409);
               this.targetMarker.set(true);
               this.markerStyle.set(CombatFxEngine.TargetMarkerStyle.CYBER_RETICLE);
               this.markerColor.set(-16715521);
               break;
            case HOLY_PALADIN:
               this.numbers.set(true);
               this.numberSize.set(1.1);
               this.numberStyle.set(CombatFxEngine.NumberStyle.BOUNCE);
               this.damageIcons.set(true);
               this.sparks.set(true);
               this.sparkStyle.set(CombatFxEngine.SparkStyle.STAR);
               this.sparkColor.set(-10496);
               this.sparkCount.set(16);
               this.killEffect.set(true);
               this.killStyle.set(KillBurst.Style.ANGELIC_ASCENSION);
               this.killColor.set(-8090);
               this.targetMarker.set(true);
               this.markerStyle.set(CombatFxEngine.TargetMarkerStyle.HEXAGON);
               this.markerColor.set(-10496);
               break;
            case VOID_DEMON:
               this.numbers.set(true);
               this.numberSize.set(1.1);
               this.numberStyle.set(CombatFxEngine.NumberStyle.EXPAND_FADE);
               this.damageIcons.set(true);
               this.sparks.set(true);
               this.sparkStyle.set(CombatFxEngine.SparkStyle.VOID_TEAR);
               this.sparkColor.set(-7722014);
               this.sparkCount.set(22);
               this.killEffect.set(true);
               this.killStyle.set(KillBurst.Style.BLACK_HOLE);
               this.killColor.set(-6467875);
               this.targetMarker.set(true);
               this.markerStyle.set(CombatFxEngine.TargetMarkerStyle.ORBITAL);
               this.markerColor.set(-6467875);
         }
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.engine.reset();
   }

   @Override
   public void onDisable() {
      this.engine.reset();
      super.onDisable();
   }

   @Override
   public String getInfo() {
      int var1 = this.engine.comboCount();
      if (var1 > 1) {
         return "x" + var1;
      }

      int var2 = this.engine.activeCount();
      return var2 == 0 ? null : String.valueOf(var2);
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.POST) {
         if (!this.isInGame()) {
            this.engine.reset();
         } else {
            this.engine.tick(this.syncConfig());
         }
      }
   }

   @EventHandler
   public void onAttack(AttackEvent event) {
      if (this.isInGame() && event.getTarget() != null) {
         this.engine.onAttack(event.getTarget(), this.syncConfig());
      }
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (this.isInGame()) {
         this.engine.render(this.syncConfig(), event.tickDelta);
      }
   }

   private CombatFxEngine.Config syncConfig() {
      this.cfg.numbers = (Boolean)this.numbers.get();
      this.cfg.numberStyle = (CombatFxEngine.NumberStyle)this.numberStyle.get();
      this.cfg.damageIcons = (Boolean)this.damageIcons.get();
      this.cfg.numberSize = (Double)this.numberSize.get();
      this.cfg.numberLife = (Double)this.numberLife.get();
      this.cfg.mergeWindowMs = (Integer)this.mergeWindow.get();
      this.cfg.selfDamage = (Boolean)this.selfDamage.get();
      this.cfg.showHeal = (Boolean)this.showHeal.get();
      this.cfg.occlusion = (Boolean)this.occlusion.get();
      this.cfg.damageColor = (Integer)this.damageColor.get();
      this.cfg.critColor = (Integer)this.critColor.get();
      this.cfg.healColor = (Integer)this.healColor.get();
      this.cfg.selfColor = (Integer)this.selfColor.get();
      this.cfg.sparks = (Boolean)this.sparks.get();
      this.cfg.sparkStyle = (CombatFxEngine.SparkStyle)this.sparkStyle.get();
      this.cfg.sparkCount = (Integer)this.sparkCount.get();
      this.cfg.sparkColor = (Integer)this.sparkColor.get();
      this.cfg.killEffect = (Boolean)this.killEffect.get();
      this.cfg.killStyle = (KillBurst.Style)this.killStyle.get();
      this.cfg.killColor = (Integer)this.killColor.get();
      this.cfg.killLabel = (Boolean)this.killLabel.get();
      this.cfg.killSound = (Boolean)this.killSound.get();
      this.cfg.combo = (Boolean)this.combo.get();
      this.cfg.targetMarker = (Boolean)this.targetMarker.get();
      this.cfg.markerStyle = (CombatFxEngine.TargetMarkerStyle)this.markerStyle.get();
      this.cfg.markerColor = (Integer)this.markerColor.get();
      this.cfg.markerSize = (Double)this.markerSize.get();
      this.cfg.targets = (CombatFxEngine.Targets)this.targets.get();
      this.cfg.source = (CombatFxEngine.Source)this.source.get();
      this.cfg.range = (Double)this.range.get();
      this.cfg.maxFx = (Integer)this.maxFx.get();
      return this.cfg;
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

   public enum CombatPreset {
      CUSTOM,
      COMPETITIVE,
      ANIME_SLASH,
      BLOOD_CARNAGE,
      CYBERPUNK,
      HOLY_PALADIN,
      VOID_DEMON;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CombatFX.CombatPreset[] $values() {
         CombatFX.CombatPreset[] var10000 = new CombatFX.CombatPreset[7];
         var10000[0] = CUSTOM;
         var10000[1] = COMPETITIVE;
         var10000[2] = ANIME_SLASH;
         var10000[3] = BLOOD_CARNAGE;
         var10000[4] = CYBERPUNK;
         var10000[5] = HOLY_PALADIN;
         var10000[6] = VOID_DEMON;
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
}
