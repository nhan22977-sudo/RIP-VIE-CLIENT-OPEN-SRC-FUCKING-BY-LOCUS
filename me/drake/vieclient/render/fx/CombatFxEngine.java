package me.drake.vieclient.render.fx;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import me.drake.vieclient.modules.render.TrueStretched;
import me.drake.vieclient.render.nvg.NVGIcons;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.EntityUtil;
import me.drake.vieclient.util.PerformanceHelper;
import me.drake.vieclient.util.Sfx;
import net.minecraft.class_1296;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1588;
import net.minecraft.class_1657;
import net.minecraft.class_243;
import net.minecraft.class_310;

public final class CombatFxEngine {
   private static final int FREECAM_DUMMY_ID = -6969;
   private static final float HEALTH_EPSILON = 0.05F;
   private static final long ATTACK_CREDIT_MS = 700L;
   private static final long COMBO_WINDOW_MS = 5000L;
   private static final long VIS_RECHECK_MS = 220L;
   private static final int SPARK_ALPHA_BUCKETS = 4;
   private static final int MAX_SPARK_GROUPS = 12;
   private final Map tracked = new HashMap();
   private final Map mergeTargets = new HashMap();
   private final List numbers = new ArrayList();
   private final List sparks = new ArrayList();
   private final List bursts = new ArrayList();
   private final FxPool numberPool = new FxPool(FloatingNumber::new, 128);
   private final FxPool sparkPool = new FxPool(Spark::new, 512);
   private final FxPool burstPool = new FxPool(KillBurst::new, 16);
   private final WorldProjector projector = new WorldProjector();
   private final float[] proj = new float[3];
   private final float[] proj2 = new float[3];
   private final int[] groupKey = new int[12];
   private int groupCount;
   private float[] sparkCache = new float[2560];
   private int sparkCacheCount;
   private int scanMark;
   private long lastFrameNanos;
   private int lastKillSeed;
   private static final long TARGET_LINGER_MS = 2500L;
   private int currentTargetEntityId = -2147483648;
   private double targetSmoothX;
   private double targetSmoothY;
   private double targetSmoothZ;
   private boolean hasTargetPos;
   private float targetAlpha;
   private float targetAnimRot;
   private float targetHitKick;
   private long lastTargetVisCheckMs;
   private boolean targetVisible = true;
   private long lastAttackAtMs;
   private int lastAttackTargetId = -2147483648;
   private boolean lastAttackWasCrit;
   private long lastKillAtMs;
   private int comboCount;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void reset() {
      for (FloatingNumber var2 : this.numbers) {
         this.numberPool.release(var2);
      }

      for (Spark var5 : this.sparks) {
         this.sparkPool.release(var5);
      }

      for (KillBurst var6 : this.bursts) {
         this.burstPool.release(var6);
      }

      this.numbers.clear();
      this.sparks.clear();
      this.bursts.clear();
      this.tracked.clear();
      this.mergeTargets.clear();
      this.comboCount = 0;
      this.lastKillAtMs = 0L;
      this.lastAttackTargetId = -2147483648;
      this.currentTargetEntityId = -2147483648;
      this.targetAlpha = 0.0F;
      this.hasTargetPos = false;
      this.targetHitKick = 0.0F;
      this.lastTargetVisCheckMs = 0L;
      this.targetVisible = true;
      this.lastFrameNanos = 0L;
   }

   public int activeCount() {
      return this.numbers.size() + this.sparks.size() + this.bursts.size();
   }

   public int comboCount() {
      return this.comboCount;
   }

   public void onAttack(class_1297 target, CombatFxEngine.Config cfg) {
      if (target != null) {
         class_310 var3 = class_310.method_1551();
         if (var3 != null && var3.field_1724 != null) {
            this.lastAttackAtMs = System.currentTimeMillis();
            this.lastAttackTargetId = target.method_5628();
            this.currentTargetEntityId = target.method_5628();
            this.lastAttackWasCrit = isCritCondition(var3.field_1724);
            this.targetHitKick = 1.0F;
            if (cfg.sparks) {
               this.spawnSparks(WorldProjector.hitboxCentre(target, 1.0F), cfg, this.lastAttackWasCrit);
            }
         }
      }
   }

   private static boolean isCritCondition(class_1657 p) {
      return p.field_6017 > 0.0 && !p.method_24828() && !p.method_6101() && !p.method_5799() && !p.method_5765() && !p.method_5624();
   }

   public void tick(CombatFxEngine.Config cfg) {
      class_310 var2 = class_310.method_1551();
      if (var2 != null && var2.field_1687 != null && var2.field_1724 != null) {
         long var3 = System.currentTimeMillis();
         if (cfg.combo && this.comboCount > 0 && var3 - this.lastKillAtMs > 5000L) {
            this.comboCount = 0;
         }

         this.mergeTargets.entrySet().removeIf(e -> ((FloatingNumber)e.getValue()).mergeUntil < var3 || !((FloatingNumber)e.getValue()).alive());
         this.scanMark++;
         double var5 = cfg.range * cfg.range;
         class_243 var7 = var2.field_1724.method_33571();

         for (class_1297 var9 : var2.field_1687.method_18112()) {
            if (var9 instanceof class_1309 var10 && var10.method_5628() != -6969) {
               boolean var11 = var10 == var2.field_1724;
               if ((var11 || !(var10.method_5707(var7) > var5)) && (var11 || matchesTargetFilter(var10, cfg.targets))) {
                  int var12 = var10.method_5628();
                  float var13 = var10.method_6032();
                  boolean var14 = var10.method_5805() && var13 > 0.0F;
                  CombatFxEngine.Tracked var15 = (CombatFxEngine.Tracked)this.tracked.get(var12);
                  if (var15 == null) {
                     CombatFxEngine.Tracked var20 = new CombatFxEngine.Tracked();
                     var20.health = var13;
                     var20.alive = var14;
                     var20.mark = this.scanMark;
                     this.tracked.put(var12, var20);
                  } else {
                     var15.mark = this.scanMark;
                     float var16 = var15.health - var13;
                     if (var14 && var16 > 0.05F) {
                        boolean var17 = this.isCreditedToPlayer(var12, var3);
                        if (cfg.source != CombatFxEngine.Source.MINE_ONLY || var17 || var11) {
                           boolean var18 = var17 && this.lastAttackWasCrit;
                           if (var11) {
                              if (cfg.selfDamage) {
                                 this.spawnNumber(var10, var16, FloatingNumber.Kind.SELF, cfg);
                              }
                           } else if (cfg.numbers) {
                              this.spawnNumber(var10, var16, var18 ? FloatingNumber.Kind.CRIT : FloatingNumber.Kind.DAMAGE, cfg);
                           }
                        }
                     } else if (var14 && cfg.showHeal && cfg.numbers && var16 < -0.05F) {
                        this.spawnNumber(var10, -var16, FloatingNumber.Kind.HEAL, cfg);
                     }

                     if (var15.alive && !var14) {
                        this.onKill(var10, cfg, this.isCreditedToPlayer(var12, var3));
                     }

                     var15.health = var13;
                     var15.alive = var14;
                  }
               }
            }
         }

         if (this.tracked.size() > 256) {
            this.tracked.values().removeIf(t -> t.mark != this.scanMark);
         } else {
            Iterator var19 = this.tracked.entrySet().iterator();

            while (var19.hasNext()) {
               if (((CombatFxEngine.Tracked)((Entry)var19.next()).getValue()).mark < this.scanMark - 60) {
                  var19.remove();
               }
            }
         }
      } else {
         if (!this.tracked.isEmpty()) {
            this.reset();
         }
      }
   }

   private boolean isCreditedToPlayer(int entityId, long now) {
      return entityId == this.lastAttackTargetId && now - this.lastAttackAtMs <= 700L;
   }

   private static boolean matchesTargetFilter(class_1309 e, CombatFxEngine.Targets filter) {
      return switch (filter) {
         case ALL -> true;
         case PLAYERS -> e instanceof class_1657;
         case MOBS -> e instanceof class_1588 || e instanceof class_1296;
      };
   }

   private void spawnNumber(class_1309 entity, float amount, FloatingNumber.Kind kind, CombatFxEngine.Config cfg) {
      long var5 = System.currentTimeMillis();
      int var7 = entity.method_5628();
      FloatingNumber var8 = (FloatingNumber)this.mergeTargets.get(var7);
      if (var8 != null && var8.alive() && var8.mergeUntil >= var5 && var8.kind != FloatingNumber.Kind.HEAL && kind != FloatingNumber.Kind.HEAL) {
         var8.merge(amount, kind == FloatingNumber.Kind.CRIT);
         var8.mergeUntil = var5 + cfg.mergeWindowMs;
      } else if (this.numbers.size() < cfg.maxFx / (3)) {
         class_243 var9 = kind == FloatingNumber.Kind.SELF
            ? selfNumberOrigin(entity)
            : WorldProjector.hitboxCentre(entity, 1.0F).method_1031((Math.random() - 0.5) * 0.35, entity.method_17682() * 0.25, (Math.random() - 0.5) * 0.35);
         FloatingNumber var10 = ((FloatingNumber)this.numberPool.obtain())
            .init(var7, var9, amount, kind, (float)cfg.numberLife, (Math.random() - 0.5) * 0.55, 1.55 + Math.random() * 0.35, (Math.random() - 0.5) * 0.55);
         var10.mergeUntil = var5 + cfg.mergeWindowMs;
         this.numbers.add(var10);
         this.mergeTargets.put(var7, var10);
      }
   }

   private static class_243 selfNumberOrigin(class_1309 player) {
      double var1 = Math.toRadians(player.method_36454());
      double var3 = Math.toRadians(player.method_36455());
      double var5 = Math.cos(var3);
      double var7 = -Math.sin(var1) * var5;
      double var9 = -Math.sin(var3);
      double var11 = Math.cos(var1) * var5;
      double var13 = 2.1;
      return player.method_33571().method_1031(var7 * 2.1 + (Math.random() - 0.5) * 0.5, var9 * 2.1 - 0.35, var11 * 2.1 + (Math.random() - 0.5) * 0.5);
   }

   private void spawnSparks(class_243 origin, CombatFxEngine.Config cfg, boolean crit) {
      int var4 = PerformanceHelper.scaleRenderCap(cfg.sparkCount);
      int var5 = cfg.maxFx - this.sparks.size();
      int var6 = Math.max(0, Math.min(var4, var5));
      if (var6 != 0) {
         int var7 = cfg.sparkStyle == CombatFxEngine.SparkStyle.BLOOD ? -2936260 : cfg.sparkColor;
         if (crit) {
            var7 = brighten(var7, 0.25F);
         }
         float var8 = switch (cfg.sparkStyle) {
            case SPARK -> 0.35F;
            case BLOOD -> 1.0F;
            case STAR -> 0.12F;
            case SHOCKWAVE, SLASH, VORTEX, LIGHTNING, PLASMA, VOID_TEAR -> 0.0F;
            case EMBER -> -0.35F;
            case HEARTS -> -0.45F;
         };

         float var9 = switch (cfg.sparkStyle) {
            case SHOCKWAVE -> 0.012F;
            case EMBER -> 0.02F;
            case SLASH -> 0.08F;
            case VORTEX -> 0.05F;
            case LIGHTNING -> 0.09F;
            case HEARTS -> 0.02F;
            case PLASMA -> 0.06F;
            case VOID_TEAR -> 0.07F;
            default -> 0.045F;
         };

         float var10 = switch (cfg.sparkStyle) {
            case SPARK -> 6.0F;
            case BLOOD -> 3.2F;
            case STAR -> 4.0F;
            case SHOCKWAVE -> 7.5F;
            case EMBER -> 2.4F;
            case SLASH -> 9.5F;
            case VORTEX -> 6.0F;
            case LIGHTNING -> 8.2F;
            case HEARTS -> 2.2F;
            case PLASMA -> 7.5F;
            case VOID_TEAR -> 5.8F;
         };

         for (int var11 = 0; var11 < var6; var11++) {
            int var18 = var7;
            double var12;
            double var14;
            double var16;
            if (cfg.sparkStyle == CombatFxEngine.SparkStyle.SHOCKWAVE) {
               double var19 = (double)var11 / var6 * Math.PI * 2.0;
               var12 = Math.cos(var19);
               var14 = 0.0;
               var16 = Math.sin(var19);
            } else if (cfg.sparkStyle == CombatFxEngine.SparkStyle.SLASH) {
               double var23 = ((double)var11 / var6 - 0.5) * 1.8;
               var12 = Math.cos(var23 + 0.3);
               var14 = Math.sin(var23) * 0.45;
               var16 = Math.sin(var23 + 0.3);
               if (crit) {
                  var18 = -20448;
               }
            } else if (cfg.sparkStyle == CombatFxEngine.SparkStyle.VORTEX) {
               double var24 = (double)var11 / var6 * Math.PI * 2.0;
               var12 = Math.cos(var24) * 0.8;
               var14 = Math.sin(var24 * 2.0) * 0.35;
               var16 = Math.sin(var24) * 0.8;
            } else if (cfg.sparkStyle == CombatFxEngine.SparkStyle.EMBER) {
               double var25 = Math.random() * Math.PI * 2.0;
               var12 = Math.cos(var25) * 0.4;
               var14 = 0.6 + Math.random() * 0.8;
               var16 = Math.sin(var25) * 0.4;
               var18 = Math.random() > 0.5 ? -35038 : -17613;
            } else if (cfg.sparkStyle == CombatFxEngine.SparkStyle.LIGHTNING) {
               double var26 = (double)var11 / var6 * Math.PI * 2.0;
               var12 = Math.cos(var26) * 0.9 + (Math.random() - 0.5) * 0.4;
               var14 = (Math.random() - 0.5) * 0.8;
               var16 = Math.sin(var26) * 0.9 + (Math.random() - 0.5) * 0.4;
               var18 = Math.random() > 0.4 ? -16718337 : -1;
            } else if (cfg.sparkStyle == CombatFxEngine.SparkStyle.HEARTS) {
               double var27 = Math.random() * Math.PI * 2.0;
               var12 = Math.cos(var27) * 0.45;
               var14 = 0.5 + Math.random() * 0.6;
               var16 = Math.sin(var27) * 0.45;
               var18 = Math.random() > 0.3 ? -52378 : -31327;
            } else if (cfg.sparkStyle == CombatFxEngine.SparkStyle.PLASMA) {
               double var28 = (double)var11 / var6 * Math.PI * 2.0;
               double var21 = (Math.random() - 0.5) * 1.2;
               var12 = Math.cos(var28) * Math.cos(var21);
               var14 = Math.sin(var21) * 0.7;
               var16 = Math.sin(var28) * Math.cos(var21);
               var18 = Math.random() > 0.4 ? -16715521 : -2601993;
            } else if (cfg.sparkStyle == CombatFxEngine.SparkStyle.VOID_TEAR) {
               double var29 = ((double)var11 / var6 - 0.5) * 2.4;
               var12 = Math.cos(var29) * 0.95;
               var14 = (Math.random() - 0.5) * 0.5;
               var16 = Math.sin(var29) * 0.95;
               var18 = Math.random() > 0.35 ? -7722014 : -2074883;
            } else {
               double var30 = Math.random() * Math.PI * 2.0;
               double var32 = Math.acos(2.0 * Math.random() - 1.0);
               var12 = Math.sin(var32) * Math.cos(var30);
               var14 = Math.cos(var32) * 0.8 + 0.35;
               var16 = Math.sin(var32) * Math.sin(var30);
            }

            double var31 = var10 * (0.6 + Math.random() * 0.6);
            this.sparks
               .add(
                  ((Spark)this.sparkPool.obtain())
                     .init(
                        origin.field_1352,
                        origin.field_1351,
                        origin.field_1350,
                        var12 * var31,
                        var14 * var31,
                        var16 * var31,
                        0.28F + (float)Math.random() * 0.26F,
                        var8,
                        var9,
                        var18
                     )
               );
         }
      }
   }

   private void onKill(class_1309 victim, CombatFxEngine.Config cfg, boolean mine) {
      long var4 = System.currentTimeMillis();
      if (mine && cfg.combo) {
         this.comboCount = var4 - this.lastKillAtMs <= 5000L ? this.comboCount + 1 : 1;
         this.lastKillAtMs = var4;
      }

      if (cfg.killEffect) {
         if (this.bursts.size() < (8)) {
            String var6 = cfg.killLabel ? victim.method_5477().getString() : "";
            KillBurst var7 = ((KillBurst)this.burstPool.obtain())
               .init(WorldProjector.hitboxCentre(victim, 1.0F), cfg.killStyle, cfg.killColor, var6, cfg.combo ? this.comboCount : 1, 1.15F, ++this.lastKillSeed);
            this.bursts.add(var7);
            if (cfg.killSound) {
               Sfx.play(Sfx.MODULE_ACTIVATE, 0.7F);
            }
         }
      }
   }

   public void render(CombatFxEngine.Config cfg) {
      this.render(cfg, 1.0F);
   }

   public void render(CombatFxEngine.Config cfg, float tickDelta) {
      float var3 = this.frameDelta();
      this.advance(var3);
      boolean var4 = this.updateTarget(cfg, var3, tickDelta);
      if (!this.numbers.isEmpty() || !this.sparks.isEmpty() || !this.bursts.isEmpty() || var4) {
         if (this.projector.snapshot()) {
            try (NanoVGFrame var5 = NanoVGRenderer.frame()) {
               if (!var5.isActive()) {
                  return;
               }

               this.drawTargetMarker(cfg);
               this.drawSparks(cfg);
               this.drawBursts(cfg);
               this.drawNumbers(cfg);
            }
         }
      }
   }

   private boolean updateTarget(CombatFxEngine.Config cfg, float dt, float tickDelta) {
      this.targetAnimRot += dt * 2.0F;
      this.targetHitKick = Math.max(0.0F, this.targetHitKick - dt * 3.5F);
      if (!cfg.targetMarker) {
         this.targetAlpha = this.targetAlpha + (0.0F - this.targetAlpha) * (float)(1.0 - Math.exp(-dt * 8.0));
         if (this.targetAlpha < 0.01F) {
            this.targetAlpha = 0.0F;
            this.hasTargetPos = false;
         }

         return this.targetAlpha > 0.005F;
      } else {
         class_310 var4 = class_310.method_1551();
         class_1297 var5 = null;
         long var6 = System.currentTimeMillis();
         if (var4 != null && var4.field_1687 != null && var4.field_1724 != null) {
            if (this.currentTargetEntityId != Integer.MIN_VALUE && var6 - this.lastAttackAtMs < 2500L) {
               class_1297 var8 = var4.field_1687.method_8469(this.currentTargetEntityId);
               if (var8 instanceof class_1309 var9 && var9.method_5805() && var4.field_1724.method_5739(var8) <= cfg.range) {
                  var5 = var8;
               } else {
                  this.currentTargetEntityId = Integer.MIN_VALUE;
               }
            }

            if (var5 == null) {
               class_1297 var11 = EntityUtil.getCrosshairEntity();
               if (var11 == null && var4.field_1692 != null) {
                  var11 = var4.field_1692;
               }

               if (var11 instanceof class_1309 var13
                  && var13.method_5805()
                  && var13 != var4.field_1724
                  && EntityUtil.passesFilter(var13, toFilter(cfg.targets), false)
                  && var4.field_1724.method_5739(var13) <= cfg.range) {
                  var5 = var11;
                  this.currentTargetEntityId = var13.method_5628();
               }
            }
         }

         if (var5 != null) {
            class_243 var12 = WorldProjector.hitboxCentre(var5, tickDelta);
            if (this.hasTargetPos && !(this.targetAlpha <= 0.01F)) {
               double var14 = 1.0 - Math.exp(-dt * 26.0);
               this.targetSmoothX = this.targetSmoothX + (var12.field_1352 - this.targetSmoothX) * var14;
               this.targetSmoothY = this.targetSmoothY + (var12.field_1351 - this.targetSmoothY) * var14;
               this.targetSmoothZ = this.targetSmoothZ + (var12.field_1350 - this.targetSmoothZ) * var14;
            } else {
               this.targetSmoothX = var12.field_1352;
               this.targetSmoothY = var12.field_1351;
               this.targetSmoothZ = var12.field_1350;
               this.hasTargetPos = true;
            }

            this.targetAlpha = this.targetAlpha + (1.0F - this.targetAlpha) * (float)(1.0 - Math.exp(-dt * 12.0));
         } else {
            this.targetAlpha = this.targetAlpha + (0.0F - this.targetAlpha) * (float)(1.0 - Math.exp(-dt * 7.0));
            if (this.targetAlpha < 0.01F) {
               this.targetAlpha = 0.0F;
               this.hasTargetPos = false;
               this.currentTargetEntityId = Integer.MIN_VALUE;
            }
         }

         return this.targetAlpha > 0.005F;
      }
   }

   private static EntityUtil.Filter toFilter(CombatFxEngine.Targets targets) {
      return switch (targets) {
         case ALL -> EntityUtil.Filter.ALL;
         case PLAYERS -> EntityUtil.Filter.PLAYERS;
         case MOBS -> EntityUtil.Filter.MOBS;
      };
   }

   private void drawTargetMarker(CombatFxEngine.Config cfg) {
      if (cfg.targetMarker && !(this.targetAlpha <= 0.01F) && this.hasTargetPos) {
         if (this.projector.project(this.targetSmoothX, this.targetSmoothY, this.targetSmoothZ, this.proj)) {
            if (this.projector.onScreen(this.proj, 48.0F)) {
               float var2 = this.proj[0];
               float var3 = this.proj[1];
               float var4 = this.proj[2];
               float var5 = this.targetAlpha;
               if (cfg.occlusion) {
                  long var6 = System.currentTimeMillis();
                  if (var6 - this.lastTargetVisCheckMs > 220L) {
                     this.lastTargetVisCheckMs = var6;
                     this.targetVisible = WorldProjector.hasLineOfSight(new class_243(this.targetSmoothX, this.targetSmoothY, this.targetSmoothZ));
                  }

                  if (!this.targetVisible) {
                     var5 *= 0.35F;
                  }
               }

               if (!(var5 <= 0.01F)) {
                  float var28 = (float)(cfg.markerSize * 65.0 / Math.max(2.5, var4));
                  var28 = clamp(var28, 2.5F, 45.0F);
                  float var7 = (float)Math.sin(this.targetHitKick * Math.PI * 0.5) * 0.35F;
                  float var8 = var28 * (1.0F + var7);
                  int var9 = cfg.markerColor;
                  if (this.lastAttackWasCrit && this.targetHitKick > 0.08F) {
                     var9 = brighten(cfg.critColor, 0.25F);
                  }

                  float var10 = TrueStretched.getStretchFactor();
                  boolean var11 = Math.abs(var10 - 1.0F) > 0.001F;
                  if (var11) {
                     NanoVGRenderer.save();
                     NanoVGRenderer.translate(var2, var3);
                     NanoVGRenderer.scale(var10, 1.0F);
                     NanoVGRenderer.translate(-var2, -var3);
                  }

                  try {
                     NanoVGRenderer.drawRadialGlow(var2, var3, var8 * 2.8F, applyAlpha(var9, var5 * 0.3F));
                     NanoVGRenderer.drawRadialGlow(var2, var3, var8 * 1.35F, applyAlpha(var9, var5 * 0.55F));
                     CombatFxEngine.TargetMarkerStyle var12 = cfg.markerStyle == null ? CombatFxEngine.TargetMarkerStyle.ORBITAL : cfg.markerStyle;
                     switch (var12) {
                        case ORBITAL:
                           float var33 = this.targetAnimRot;
                           float var36 = Math.min(1.5F, var8 / 20.0F);

                           for (int var38 = 0; var38 < 3; var38++) {
                              float var41 = var33 + var38 * (float) (Math.PI * 2.0 / 3.0);
                              float var44 = var41 + 1.3823007F;
                              NanoVGRenderer.drawArc(var2, var3, var8 * 1.35F, var41, var44, Math.max(0.6F, 1.8F * var36), applyAlpha(var9, var5 * 0.85F));
                           }

                           float var39 = -var33 * 0.7F;

                           for (int var42 = 0; var42 < 6; var42++) {
                              float var45 = var39 + var42 * (float) (Math.PI / 3);
                              float var47 = var2 + (float)Math.cos(var45) * (var8 * 0.95F);
                              float var49 = var3 + (float)Math.sin(var45) * (var8 * 0.95F);
                              float var51 = var2 + (float)Math.cos(var45) * (var8 * 1.15F);
                              float var53 = var3 + (float)Math.sin(var45) * (var8 * 1.15F);
                              NanoVGRenderer.drawLine(var47, var49, var51, var53, Math.max(0.5F, 1.2F * var36), applyAlpha(var9, var5 * 0.6F));
                           }

                           NanoVGRenderer.drawCircleOutline(var2, var3, var8 * 0.85F, Math.max(0.6F, 1.6F * var36), applyAlpha(var9, var5 * 0.95F));
                           NanoVGRenderer.drawCircle(var2, var3, Math.max(0.7F, var8 * 0.22F), applyAlpha(-1, var5));
                           NanoVGRenderer.drawCircleOutline(var2, var3, Math.max(0.7F, var8 * 0.22F), Math.max(0.5F, 1.0F * var36), applyAlpha(var9, var5));
                           break;
                        case CYBER_RETICLE:
                           float var32 = this.targetAnimRot * 0.5F;
                           float var35 = Math.min(1.5F, var8 / 20.0F);

                           for (int var37 = 0; var37 < 4; var37++) {
                              float var40 = var32 + var37 * (float) (Math.PI / 2);
                              float var43 = (float)Math.cos(var40);
                              float var46 = (float)Math.sin(var40);
                              float var48 = var2 + var43 * (var8 * 0.75F);
                              float var50 = var3 + var46 * (var8 * 0.75F);
                              float var52 = var2 + var43 * (var8 * 1.4F);
                              float var54 = var3 + var46 * (var8 * 1.4F);
                              NanoVGRenderer.drawLine(var48, var50, var52, var54, Math.max(0.6F, 1.8F * var35), applyAlpha(var9, var5 * 0.9F));
                              float var23 = -var46 * (var8 * 0.22F);
                              float var24 = var43 * (var8 * 0.22F);
                              NanoVGRenderer.drawLine(
                                 var52 - var23, var54 - var24, var52 + var23, var54 + var24, Math.max(0.5F, 1.5F * var35), applyAlpha(var9, var5 * 0.8F)
                              );
                           }

                           NanoVGRenderer.drawCircleOutline(var2, var3, var8 * 0.9F, Math.max(0.6F, 1.5F * var35), applyAlpha(var9, var5 * 0.75F));
                           NanoVGRenderer.drawCircle(var2, var3, Math.max(0.7F, var8 * 0.2F), applyAlpha(-1, var5));
                           break;
                        case PULSE_CIRCLE:
                           float var31 = Math.min(1.5F, var8 / 20.0F);
                           float var34 = var8 * (1.25F + 0.12F * (float)Math.sin(this.targetAnimRot * 3.5F));
                           NanoVGRenderer.drawCircleOutline(var2, var3, var34, Math.max(0.5F, 1.2F * var31), applyAlpha(var9, var5 * 0.55F));
                           NanoVGRenderer.drawCircleOutline(var2, var3, var8, Math.max(0.6F, 2.0F * var31), applyAlpha(var9, var5 * 0.95F));
                           NanoVGRenderer.drawCircle(var2, var3, var8 * 0.45F, applyAlpha(var9, var5 * 0.25F));
                           NanoVGRenderer.drawCircle(var2, var3, Math.max(0.7F, var8 * 0.22F), applyAlpha(-1, var5));
                           break;
                        case HEXAGON:
                           float var30 = this.targetAnimRot * 0.8F;
                           float var14 = var8 * 1.2F;
                           float var15 = Math.min(1.5F, var8 / 20.0F);

                           for (int var16 = 0; var16 < 6; var16++) {
                              float var17 = var30 + var16 * (float) (Math.PI / 3);
                              float var18 = var30 + (var16 + 1) * (float) (Math.PI / 3);
                              float var19 = var2 + (float)Math.cos(var17) * var14;
                              float var20 = var3 + (float)Math.sin(var17) * var14;
                              float var21 = var2 + (float)Math.cos(var18) * var14;
                              float var22 = var3 + (float)Math.sin(var18) * var14;
                              NanoVGRenderer.drawLine(var19, var20, var21, var22, Math.max(0.6F, 1.8F * var15), applyAlpha(var9, var5 * 0.85F));
                              NanoVGRenderer.drawCircle(var19, var20, Math.max(0.5F, 1.8F * var15), applyAlpha(-1, var5 * 0.9F));
                           }

                           NanoVGRenderer.drawCircleOutline(var2, var3, var8 * 0.75F, Math.max(0.5F, 1.4F * var15), applyAlpha(var9, var5 * 0.7F));
                           NanoVGRenderer.drawCircle(var2, var3, Math.max(0.7F, var8 * 0.2F), applyAlpha(-1, var5));
                           break;
                        case DOT_GLOW:
                           float var13 = Math.min(1.5F, var8 / 20.0F);
                           NanoVGRenderer.drawCircleOutline(var2, var3, var8 * 0.85F, Math.max(0.5F, 1.5F * var13), applyAlpha(var9, var5 * 0.85F));
                           NanoVGRenderer.drawCircle(var2, var3, var8 * 0.35F, applyAlpha(var9, var5 * 0.75F));
                           NanoVGRenderer.drawCircle(var2, var3, Math.max(0.7F, var8 * 0.16F), applyAlpha(-1, var5));
                     }
                  } finally {
                     if (var11) {
                        NanoVGRenderer.restore();
                     }
                  }
               }
            }
         }
      }
   }

   private float frameDelta() {
      long var1 = System.nanoTime();
      if (this.lastFrameNanos == 0L) {
         this.lastFrameNanos = var1;
         return 0.016666668F;
      } else {
         float var3 = (float)(var1 - this.lastFrameNanos) / 1.0E9F;
         this.lastFrameNanos = var1;
         return Math.max(5.0E-4F, Math.min(0.1F, var3));
      }
   }

   private void advance(float dt) {
      Iterator var2 = this.numbers.iterator();

      while (var2.hasNext()) {
         FloatingNumber var3 = (FloatingNumber)var2.next();
         var3.update(dt);
         if (!var3.alive()) {
            var2.remove();
            this.mergeTargets.remove(var3.entityId, var3);
            this.numberPool.release(var3);
         }
      }

      var2 = this.sparks.iterator();

      while (var2.hasNext()) {
         Spark var6 = (Spark)var2.next();
         var6.update(dt);
         if (!var6.alive()) {
            var2.remove();
            this.sparkPool.release(var6);
         }
      }

      var2 = this.bursts.iterator();

      while (var2.hasNext()) {
         KillBurst var7 = (KillBurst)var2.next();
         var7.update(dt);
         if (!var7.alive()) {
            var2.remove();
            this.burstPool.release(var7);
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void drawNumbers(CombatFxEngine.Config cfg) {
      if (cfg.numbers || cfg.selfDamage) {
         long var2 = System.currentTimeMillis();

         for (FloatingNumber var5 : this.numbers) {
            if (this.projector.project(var5.x, var5.y, var5.z, this.proj) && this.projector.onScreen(this.proj, 48.0F)) {
               float var6 = var5.progress();
               float var8 = 0.0F;
               float var7;
               if (cfg.numberStyle == CombatFxEngine.NumberStyle.BOUNCE) {
                  float var9 = var6 < 0.22F ? Easing.outBack(var6 / 0.22F) * 1.15F : 1.0F;
                  var7 = var9 * (1.0F + var5.mergeKick);
                  var8 = (float)Math.sin(var6 * Math.PI) * -10.0F;
               } else if (cfg.numberStyle == CombatFxEngine.NumberStyle.EXPAND_FADE) {
                  float var26 = var6 < 0.22F ? 1.0F + (0.22F - var6) * 1.8F : 1.0F;
                  var7 = var26 * (1.0F + var5.mergeKick);
               } else {
                  float var27 = var6 < 0.18F ? Easing.outBack(var6 / 0.18F) : 1.0F;
                  var7 = var27 * (1.0F + var5.mergeKick);
               }

               float var28 = var6 < 0.72F ? 1.0F : 1.0F - Easing.inQuad((var6 - 0.72F) / 0.28F);
               if (!(var28 <= 0.01F)) {
                  if (cfg.occlusion) {
                     if (var2 - var5.visCheckedAtMs > 220L) {
                        var5.visCheckedAtMs = var2;
                        var5.visible = WorldProjector.hasLineOfSight(new class_243(var5.x, var5.y, var5.z));
                     }

                     if (!var5.visible) {
                        var28 *= 0.3F;
                     }
                  }

                  float var10 = this.proj[2];
                  float var11 = (float)(cfg.numberSize * 60.0 / Math.max(2.5, var10));
                  if (var5.kind == FloatingNumber.Kind.CRIT) {
                     var11 *= 1.28F;
                  }

                  var11 = clamp(var11 * var7, 3.5F, 32.0F);
                  if (var5.text.isEmpty()) {
                     var5.text = formatAmount(var5.amount, var5.kind, false);
                  }
                  int var12 = switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$render$fx$FloatingNumber$Kind[var5.kind.ordinal()]) {
                     case 1 -> cfg.critColor;
                     case 2 -> cfg.healColor;
                     case 3 -> cfg.selfColor;
                     case 4 -> cfg.damageColor;
                     default -> throw new MatchException(null, null);
                  };
                  boolean var13 = cfg.damageIcons;
                  float var14 = NanoVGRenderer.getTextWidth(var5.text, var11);
                  float var15 = var11 * 0.74F;
                  float var16 = var11 * 0.22F;
                  float var17 = var13 ? var15 + var16 + var14 : var14;
                  float var18 = this.proj[0] - var17 * 0.5F;
                  float var19 = this.proj[1] + var8;
                  if (var5.kind == FloatingNumber.Kind.CRIT) {
                     float var20 = var11 / 14.0F;
                     float var21 = Math.max(1.5F, 5.0F * var20);
                     float var22 = Math.max(0.8F, 2.0F * var20);
                     float var23 = Math.max(1.0F, 4.0F * var20);
                     NanoVGRenderer.drawRoundedRect(var18 - var21, var19 - var22, var17 + var21 * 2.0F, var11 + var22 * 2.0F, var23, argb(0, var28 * 0.4F));
                     NanoVGRenderer.drawRoundedRectOutline(
                        var18 - var21,
                        var19 - var22,
                        var17 + var21 * 2.0F,
                        var11 + var22 * 2.0F,
                        var23,
                        Math.max(0.5F, 1.0F * var20),
                        argb(var12, var28 * 0.45F)
                     );
                     Color var24 = argb(var12, var28 * 0.35F);
                     float var25 = var13 ? var18 + var15 + var16 : var18;
                     NanoVGRenderer.drawText(var5.text, var25 - 1.0F, var19, var11, var24);
                     NanoVGRenderer.drawText(var5.text, var25 + 1.0F, var19, var11, var24);
                  }

                  float var30 = var18;
                  if (var13) {
                     float var31 = var18 + var15 * 0.5F;
                     float var32 = var19 + var11 * 0.46F;
                     this.drawKindIcon(var5.kind, var31, var32, var15, var12, var28);
                     var30 = var18 + var15 + var16;
                  }

                  NanoVGRenderer.drawTextWithShadow(var5.text, var30, var19, var11, argb(var12, var28));
               }
            }
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void drawKindIcon(FloatingNumber.Kind kind, float cx, float cy, float size, int baseRgb, float alpha) {
      Color var7 = argb(baseRgb, alpha);
      switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$render$fx$FloatingNumber$Kind[kind.ordinal()]) {
         case 1:
            NVGIcons.sparkle(cx, cy, size * 0.52F, var7);
            break;
         case 2:
         case 3:
            NVGIcons.heart(cx, cy, size, var7);
            break;
         case 4:
            NVGIcons.sword(cx, cy, size, var7);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static String formatAmount(float amount, FloatingNumber.Kind kind, boolean icons) {
      String var3 = switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$render$fx$FloatingNumber$Kind[kind.ordinal()]) {
         case 1 -> {
            if (icons) {
               byte[] var9 = new byte[4];
               var9[0] = (byte)(-45);
               var9[1] = 0;
               var9[2] = (byte)(-67);
               var9[3] = (byte)(-2);
               yield $$v_s(var9, 7750546160811757588L);
            } else {
               byte[] var10 = new byte[4];
               var10[0] = (byte)(-45);
               var10[1] = (byte)(6);
               var10[2] = (byte)(-70);
               var10[3] = (byte)(-2);
               yield $$v_s(var10, 7750546160811757588L);
            }
         }
         case 2 -> {
            if (icons) {
               byte[] var8 = new byte[5];
               var8[0] = (byte)(-45);
               var8[1] = (byte)(7);
               var8[2] = (byte)(-72);
               var8[3] = (byte)(-2);
               var8[4] = (byte)(57);
               yield $$v_s(var8, 7750546160811757588L);
            } else {
               yield "+";
            }
         }
         case 3 -> {
            if (icons) {
               byte[] var7 = new byte[5];
               var7[0] = (byte)(-45);
               var7[1] = (byte)(7);
               var7[2] = (byte)(-72);
               var7[3] = (byte)(-2);
               var7[4] = (byte)(63);
               yield $$v_s(var7, 7750546160811757588L);
            } else {
               yield "-";
            }
         }
         case 4 -> {
            if (icons) {
               byte[] var10000 = new byte[5];
               var10000[0] = (byte)(-63);
               var10000[1] = (byte)(5);
               var10000[2] = (byte)(-117);
               var10000[3] = (byte)(127);
               var10000[4] = (byte)(50);
               yield $$v_s(var10000, 7750546160811757588L);
            } else {
               yield "";
            }
         }
         default -> throw new MatchException(null, null);
      };
      String var4 = kind == FloatingNumber.Kind.CRIT ? "!" : "";
      float var5 = Math.round(amount * 10.0F) / 10.0F;
      if (!(var5 >= 10.0F) && var5 != Math.floor(var5)) {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(20);
         var10002[1] = (byte)(-76);
         var10002[2] = (byte)(45);
         var10002[3] = (byte)(-72);
         return var3 + String.format(Locale.ROOT, $$v_s(var10002, 7750546160811757588L), var5) + var4;
      } else {
         return var3 + (int)var5 + var4;
      }
   }

   private static String formatAmount(float amount, FloatingNumber.Kind kind) {
      return formatAmount(amount, kind, true);
   }

   private void drawSparks(CombatFxEngine.Config cfg) {
      if (cfg.sparks && !this.sparks.isEmpty()) {
         this.ensureSparkCache(this.sparks.size());
         this.sparkCacheCount = 0;
         this.groupCount = 0;
         float var2 = 0.0F;

         for (Spark var4 : this.sparks) {
            if (this.projector.project(var4.x, var4.y, var4.z, this.proj)
               && this.projector.onScreen(this.proj, 24.0F)
               && this.projector.project(var4.tailX(), var4.tailY(), var4.tailZ(), this.proj2)) {
               float var5 = 1.0F - Easing.inQuad(var4.progress());
               int var6 = (int)(var5 * 4.0F);
               if (var6 >= 4) {
                  var6 = 3;
               }

               if (var6 >= 0) {
                  int var7 = (var4.color & 16777215) << 3 | var6;
                  if (this.registerGroup(var7)) {
                     int var8 = this.sparkCacheCount * 5;
                     this.sparkCache[var8] = this.proj[0];
                     this.sparkCache[var8 + 1] = this.proj[1];
                     this.sparkCache[var8 + 2] = this.proj2[0];
                     this.sparkCache[var8 + 3] = this.proj2[1];
                     this.sparkCache[var8 + 4] = Float.intBitsToFloat(var7);
                     this.sparkCacheCount++;
                     var2 += this.proj[2];
                  }
               }
            }
         }

         if (this.sparkCacheCount != 0) {
            float var10 = var2 / this.sparkCacheCount;
            float var11 = clamp(4.0F / Math.max(2.0F, var10), 0.35F, 1.4F);

            for (int var12 = 0; var12 < this.groupCount; var12++) {
               int var13 = this.groupKey[var12];
               int var14 = var13 >>> 3 & 16777215;
               int var15 = var13 & 7;
               float var9 = (var15 + 1) / 4.0F;
               this.drawSparkPass(var13, var14, var9 * 0.28F, Math.max(0.8F, 3.4F * var11));
               this.drawSparkPass(var13, var14, var9, Math.max(0.5F, 1.35F * var11));
            }
         }
      }
   }

   private void drawSparkPass(int key, int rgb, float alpha, float width) {
      NanoVGRenderer.beginLineBatch(
         new Color(
            rgb >> (16) & (255),
            rgb >> (8) & (255),
            rgb & (255),
            (int)clamp(alpha * 255.0F, 0.0F, 255.0F)
         ),
         width
      );

      for (int var5 = 0; var5 < this.sparkCacheCount; var5++) {
         int var6 = var5 * (5);
         if (Float.floatToRawIntBits(this.sparkCache[var6 + (4)]) == key) {
            NanoVGRenderer.batchLine(
               this.sparkCache[var6], this.sparkCache[var6 + 1], this.sparkCache[var6 + (2)], this.sparkCache[var6 + (3)]
            );
         }
      }

      NanoVGRenderer.endLineBatch();
   }

   private boolean registerGroup(int key) {
      for (int var2 = 0; var2 < this.groupCount; var2++) {
         if (this.groupKey[var2] == key) {
            return true;
         }
      }

      if (this.groupCount >= (12)) {
         return false;
      }

      this.groupKey[this.groupCount++] = key;
      return true;
   }

   private void ensureSparkCache(int count) {
      int var2 = count * (5);
      if (this.sparkCache.length < var2) {
         this.sparkCache = new float[Integer.highestOneBit(var2) * (2)];
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void drawBursts(CombatFxEngine.Config cfg) {
      if (cfg.killEffect && !this.bursts.isEmpty()) {
         for (KillBurst var3 : this.bursts) {
            if (this.projector.project(var3.x, var3.y, var3.z, this.proj) && this.projector.onScreen(this.proj, 220.0F)) {
               float var4 = this.proj[0];
               float var5 = this.proj[1];
               float var6 = this.pixelsPerBlock(var3.x, var3.y, var3.z, var5);
               float var7 = var3.progress();
               float var8 = TrueStretched.getStretchFactor();
               boolean var9 = Math.abs(var8 - 1.0F) > 0.001F;
               if (var9) {
                  NanoVGRenderer.save();
                  NanoVGRenderer.translate(var4, var5);
                  NanoVGRenderer.scale(var8, 1.0F);
                  NanoVGRenderer.translate(-var4, -var5);
               }

               try {
                  switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$render$fx$KillBurst$Style[var3.style.ordinal()]) {
                     case 1:
                        this.drawShockwave(var3, var4, var5, var6, var7);
                        break;
                     case 2:
                        this.drawShatter(var3, var4, var5, var6, var7);
                        break;
                     case 3:
                        this.drawSoul(var3, var4, var5, var6, var7);
                        break;
                     case 4:
                        this.drawSakura(var3, var4, var5, var6, var7);
                        break;
                     case 5:
                        this.drawLightning(var3, var4, var5, var6, var7);
                        break;
                     case 6:
                        this.drawSupernova(var3, var4, var5, var6, var7);
                        break;
                     case 7:
                        this.drawFirework(var3, var4, var5, var6, var7);
                        break;
                     case 8:
                        this.drawBlackHole(var3, var4, var5, var6, var7);
                        break;
                     case 9:
                        this.drawBloodGeyser(var3, var4, var5, var6, var7);
                        break;
                     case 10:
                        this.drawAngelicAscension(var3, var4, var5, var6, var7);
                        break;
                     case 11:
                        this.drawCyberVoxel(var3, var4, var5, var6, var7);
                  }

                  this.drawKillLabel(var3, var4, var5, var6, var7);
               } finally {
                  if (var9) {
                     NanoVGRenderer.restore();
                  }
               }
            }
         }
      }
   }

   private float pixelsPerBlock(double x, double y, double z, float screenY) {
      if (!this.projector.project(x, y + 1.0, z, this.proj2)) {
         return 20.0F;
      }

      float var8 = Math.abs(this.proj2[1] - screenY);
      return clamp(var8, 1.5F, 400.0F);
   }

   private void drawShockwave(KillBurst b, float cx, float cy, float ppb, float t) {
      for (int var6 = 0; var6 < (3); var6++) {
         float var7 = (t - var6 * 0.13F) / (1.0F - var6 * 0.13F);
         if (!(var7 <= 0.0F) && !(var7 >= 1.0F)) {
            float var8 = Easing.outCubic(var7) * ppb * (1.6F + var6 * 0.55F);
            float var9 = (1.0F - var7) * (1.0F - var6 * 0.22F);
            float var10 = clamp((1.0F - var7) * 3.2F, 0.6F, 3.2F);
            NanoVGRenderer.drawCircleOutline(cx, cy, var8, var10 * 2.4F, argb(b.color, var9 * 0.22F));
            NanoVGRenderer.drawCircleOutline(cx, cy, var8, var10, argb(b.color, var9));
         }
      }
   }

   private void drawShatter(KillBurst b, float cx, float cy, float ppb, float t) {
      int var6 = 11;
      float var7 = 1.0F - Easing.inQuad(t);
      if (!(var7 <= 0.01F)) {
         NanoVGRenderer.beginFillBatch(argb(b.color, var7 * 0.9F));

         for (int var8 = 0; var8 < (11); var8++) {
            float var9 = var8 / 11.0F * (float) Math.PI * 2.0F + b.rand(var8) * 0.5F;
            float var10 = Easing.outCubic(t) * ppb * (0.9F + b.rand(var8 + (40)) * 1.5F);
            float var11 = t * (2.2F + b.randSigned(var8 + (80)) * 3.4F);
            float var12 = ppb * (0.1F + b.rand(var8 + (120)) * 0.14F) * (1.0F - t * 0.45F);
            float var13 = cx + (float)Math.cos(var9) * var10;
            float var14 = cy + (float)Math.sin(var9) * var10 - Easing.inQuad(t) * ppb * 0.5F;
            float var15 = var11;
            float var16 = var11 + 2.1F;
            float var17 = var11 + 4.2F;
            NanoVGRenderer.batchTri(
               var13 + (float)Math.cos(var15) * var12,
               var14 + (float)Math.sin(var15) * var12,
               var13 + (float)Math.cos(var16) * var12,
               var14 + (float)Math.sin(var16) * var12,
               var13 + (float)Math.cos(var17) * var12,
               var14 + (float)Math.sin(var17) * var12
            );
         }

         NanoVGRenderer.endFillBatch();
      }
   }

   private void drawSoul(KillBurst b, float cx, float cy, float ppb, float t) {
      int var6 = 14;

      for (int var7 = 0; var7 < (14); var7++) {
         float var8 = b.rand(var7);
         float var9 = t + var8 * 0.35F;
         if (!(var9 >= 1.0F)) {
            float var10 = Easing.outCubic(var9) * ppb * 2.1F;
            float var11 = (float)Math.sin(var9 * 7.0F + var8 * 6.283F) * ppb * 0.34F;
            float var12 = (1.0F - var9) * (0.55F + var8 * 0.45F);
            float var13 = clamp(ppb * 0.055F * (1.0F - var9 * 0.5F), 0.7F, 6.0F);
            float var14 = cx + var11 + b.randSigned(var7 + (30)) * ppb * 0.25F;
            float var15 = cy - var10;
            NanoVGRenderer.drawCircle(var14, var15, var13 * 2.6F, argb(b.color, var12 * 0.18F));
            NanoVGRenderer.drawCircle(var14, var15, var13, argb(b.color, var12));
         }
      }
   }

   private void drawSakura(KillBurst b, float cx, float cy, float ppb, float t) {
      int var6 = 12;
      float var7 = 1.0F - Easing.inQuad(t);
      if (!(var7 <= 0.01F)) {
         NanoVGRenderer.beginFillBatch(argb(b.color, var7 * 0.85F));

         for (int var8 = 0; var8 < (12); var8++) {
            float var9 = b.rand(var8);
            float var10 = Easing.outCubic(t) * ppb * (0.8F + var9 * 1.1F);
            float var11 = var8 / 12.0F * (float) Math.PI * 2.0F;
            float var12 = (float)Math.sin(t * 5.0F + var9 * 6.283F) * ppb * 0.22F;
            float var13 = cx + (float)Math.cos(var11) * var10 + var12;
            float var14 = cy + (float)Math.sin(var11) * var10 * 0.35F + Easing.inQuad(t) * ppb * 1.4F;
            float var15 = ppb * (0.07F + var9 * 0.06F);
            float var16 = t * 3.0F + var9 * 6.283F;
            float var17 = var13 + (float)Math.cos(var16) * var15 * 1.7F;
            float var18 = var14 + (float)Math.sin(var16) * var15 * 1.7F;
            float var19 = var13 - (float)Math.cos(var16) * var15 * 1.7F;
            float var20 = var14 - (float)Math.sin(var16) * var15 * 1.7F;
            float var21 = (float)Math.cos(var16 + 1.5708F) * var15 * 0.65F;
            float var22 = (float)Math.sin(var16 + 1.5708F) * var15 * 0.65F;
            NanoVGRenderer.batchTri(var17, var18, var19, var20, var13 + var21, var14 + var22);
            NanoVGRenderer.batchTri(var17, var18, var19, var20, var13 - var21, var14 - var22);
         }

         NanoVGRenderer.endFillBatch();
      }
   }

   private void drawLightning(KillBurst b, float cx, float cy, float ppb, float t) {
      int var6 = 9;
      float var7 = 1.0F - Easing.inQuad(t);
      if (!(var7 <= 0.01F)) {
         float var8 = Math.min(1.0F, t / 0.33F);
         float var9 = cy - ppb * 4.2F;
         float var10 = cx + b.randSigned(0) * ppb * 0.35F;
         float var11 = var9;

         for (int var12 = 1; var12 <= (9); var12++) {
            float var13 = var12 / 9.0F;
            if (var13 > var8) {
               break;
            }

            float var14 = cx + b.randSigned(var12) * ppb * 0.42F * (1.0F - var13);
            float var15 = Easing.lerp(var9, cy, var13);
            NanoVGRenderer.drawLine(var10, var11, var14, var15, 5.2F, argb(b.color, var7 * 0.18F));
            NanoVGRenderer.drawLine(var10, var11, var14, var15, 1.6F, argb(-1, var7 * 0.9F));
            var10 = var14;
            var11 = var15;
         }

         if (var8 >= 1.0F) {
            float var16 = Easing.pulse(Math.min(1.0F, (t - 0.33F) / 0.4F));
            NanoVGRenderer.drawCircle(cx, cy, ppb * 0.6F * var16, argb(b.color, var7 * 0.35F * var16));
         }
      }
   }

   private void drawSupernova(KillBurst b, float cx, float cy, float ppb, float t) {
      float var6 = 1.0F - Easing.inQuad(t);
      if (!(var6 <= 0.01F)) {
         float var7 = t < 0.2F ? Easing.outBack(t / 0.2F) * ppb * 0.9F : (1.0F - (t - 0.2F) / 0.8F) * ppb * 0.9F;
         if (var7 > 0.5F) {
            NanoVGRenderer.drawCircle(cx, cy, var7 * 1.8F, argb(b.color, var6 * 0.35F));
            NanoVGRenderer.drawCircle(cx, cy, var7, argb(-1, var6 * 0.85F));
         }

         for (int var8 = 0; var8 < 2; var8++) {
            float var9 = (t - var8 * 0.12F) / (1.0F - var8 * 0.12F);
            if (!(var9 <= 0.0F) && !(var9 >= 1.0F)) {
               float var10 = Easing.outCubic(var9) * ppb * (2.2F + var8 * 0.8F);
               float var11 = (1.0F - var9) * var6;
               NanoVGRenderer.drawCircleOutline(cx, cy, var10, (1.0F - var9) * 3.0F + 0.5F, argb(b.color, var11));
            }
         }

         float var16 = Easing.outCubic(Math.min(1.0F, t * 2.5F)) * (1.0F - t) * ppb * 2.4F;
         if (var16 > 1.0F) {
            float var17 = var6 * 0.75F;
            NanoVGRenderer.drawLine(cx - var16, cy, cx + var16, cy, 2.4F, argb(-1, var17));
            NanoVGRenderer.drawLine(cx, cy - var16, cx, cy + var16, 2.4F, argb(-1, var17));
            NanoVGRenderer.drawLine(cx - var16 * 0.7F, cy - var16 * 0.7F, cx + var16 * 0.7F, cy + var16 * 0.7F, 1.4F, argb(b.color, var17 * 0.5F));
            NanoVGRenderer.drawLine(cx - var16 * 0.7F, cy + var16 * 0.7F, cx + var16 * 0.7F, cy - var16 * 0.7F, 1.4F, argb(b.color, var17 * 0.5F));
         }

         byte var18 = 16;

         for (int var19 = 0; var19 < 16; var19++) {
            float var20 = var19 / 16.0F * 6.283F + t * (1.5F + b.randSigned(var19) * 0.8F);
            float var12 = Easing.outCubic(t) * ppb * (1.2F + b.rand(var19 + 20) * 1.6F);
            float var13 = cx + (float)Math.cos(var20) * var12;
            float var14 = cy + (float)Math.sin(var20) * var12;
            float var15 = clamp(ppb * 0.05F * (1.0F - t), 0.8F, 5.0F);
            NanoVGRenderer.drawCircle(var13, var14, var15, argb(-1, var6 * 0.9F));
         }
      }
   }

   private void drawFirework(KillBurst b, float cx, float cy, float ppb, float t) {
      int var6 = 18;
      float var7 = 1.0F - Easing.inQuad(t);
      if (!(var7 <= 0.01F)) {
         int[] var10000 = new int[7];
         var10000[0] = -43691;
         var10000[1] = -22016;
         var10000[2] = -171;
         var10000[3] = -11141291;
         var10000[4] = -11141121;
         var10000[5] = -43521;
         var10000[6] = -1;
         int[] var8 = var10000;

         for (int var9 = 0; var9 < (18); var9++) {
            float var10 = var9 / 18.0F * 6.283F + b.rand(var9) * 0.4F;
            float var11 = 1.0F + b.rand(var9 + (10)) * 1.8F;
            float var12 = Easing.outCubic(t) * ppb * var11;
            float var13 = Easing.inQuad(t) * ppb * 1.2F;
            float var14 = cx + (float)Math.cos(var10) * var12;
            float var15 = cy + (float)Math.sin(var10) * var12 + var13;
            int var16 = var8[Math.abs(b.seed + var9) % var8.length];
            float var17 = clamp(ppb * 0.07F * (1.0F - t * 0.6F), 1.0F, 6.0F);
            NanoVGRenderer.drawCircle(var14, var15, var17 * 1.8F, argb(var16, var7 * 0.25F));
            NanoVGRenderer.drawCircle(var14, var15, var17, argb(var16, var7 * 0.95F));
         }
      }
   }

   private void drawBlackHole(KillBurst b, float cx, float cy, float ppb, float t) {
      float var6 = 1.0F - Easing.inQuad(t);
      if (!(var6 <= 0.01F)) {
         float var7 = (1.0F - (float)Math.pow(t, 1.6F)) * ppb * 1.8F + ppb * 0.2F;
         NanoVGRenderer.drawCircleOutline(cx, cy, var7 * 1.35F, 3.2F, argb(b.color, var6 * 0.45F));
         NanoVGRenderer.drawCircleOutline(cx, cy, var7, 1.8F, argb(-1, var6 * 0.85F));
         NanoVGRenderer.drawCircle(cx, cy, var7 * 0.75F, argb(0, var6 * 0.95F));
         int var8 = 12;

         for (int var9 = 0; var9 < (12); var9++) {
            float var10 = b.rand(var9);
            float var11 = (t + var10 * 0.2F) % 1.0F;
            float var12 = var9 / 12.0F * 6.283F + var11 * 5.0F;
            float var13 = (1.0F - var11) * ppb * 2.2F;
            float var14 = cx + (float)Math.cos(var12) * var13;
            float var15 = cy + (float)Math.sin(var12) * var13;
            NanoVGRenderer.drawCircle(var14, var15, clamp(ppb * 0.045F, 1.0F, 4.0F), argb(b.color, var6 * 0.9F));
         }

         if (t > 0.65F) {
            float var16 = (t - 0.65F) / 0.35F;
            float var17 = Easing.outCubic(var16) * ppb * 2.5F;
            float var18 = (1.0F - var16) * var6;
            NanoVGRenderer.drawCircle(cx, cy, var17, argb(-1, var18 * 0.6F));
         }
      }
   }

   private void drawBloodGeyser(KillBurst b, float cx, float cy, float ppb, float t) {
      float var6 = 1.0F - Easing.inQuad(t);
      if (!(var6 <= 0.01F)) {
         float var7 = Easing.outCubic(t) * ppb * 1.6F;
         NanoVGRenderer.drawCircleOutline(cx, cy, var7, 2.5F, argb(-58834, var6 * 0.6F));
         float var8 = Easing.outCubic(Math.min(1.0F, t * 2.0F)) * ppb * 3.5F;
         float var9 = cy - var8;
         NanoVGRenderer.drawLine(cx, cy, cx, var9, 4.5F, argb(-6750208, var6 * 0.85F));
         NanoVGRenderer.drawLine(cx, cy, cx, var9, 1.8F, argb(-52412, var6 * 0.95F));
         int var10 = 16;

         for (int var11 = 0; var11 < (16); var11++) {
            float var12 = var11 / 16.0F * 6.283F + b.randSigned(var11) * 0.3F;
            float var13 = 1.2F + b.rand(var11 + (30)) * 1.8F;
            float var14 = (float)Math.cos(var12) * var13 * t * ppb * 1.1F;
            float var15 = -Easing.outCubic(t) * ppb * (2.0F + b.rand(var11 + (60)) * 2.2F) + Easing.inQuad(t) * ppb * 2.8F;
            float var16 = cx + var14;
            float var17 = cy + var15;
            float var18 = clamp(ppb * 0.06F * (1.0F - t * 0.5F), 1.2F, 5.0F);
            NanoVGRenderer.drawCircle(var16, var17, var18, argb(-58834, var6 * 0.9F));
         }
      }
   }

   private void drawAngelicAscension(KillBurst b, float cx, float cy, float ppb, float t) {
      float var6 = 1.0F - Easing.inQuad(t);
      if (!(var6 <= 0.01F)) {
         float var7 = ppb * 4.8F;
         float var8 = cy - var7;
         NanoVGRenderer.drawLine(cx, cy + ppb * 0.5F, cx, var8, 8.0F, argb(-10496, var6 * 0.25F));
         NanoVGRenderer.drawLine(cx, cy + ppb * 0.5F, cx, var8, 2.5F, argb(-1, var6 * 0.9F));
         float var9 = cy - var7 * 0.85F;
         float var10 = ppb * 0.65F * (0.85F + 0.15F * (float)Math.sin(t * 6.0F));
         NanoVGRenderer.drawCircleOutline(cx, var9, var10, 2.2F, argb(-8090, var6 * 0.85F));
         int var11 = 12;

         for (int var12 = 0; var12 < (12); var12++) {
            float var13 = b.rand(var12);
            float var14 = (t + var13 * 0.3F) % 1.0F;
            float var15 = var14 * var7;
            float var16 = (float)Math.sin(var14 * 6.283F * 2.0F + var13 * 4.0F) * ppb * 0.45F;
            float var17 = cx + var16 + b.randSigned(var12 + (20)) * ppb * 0.3F;
            float var18 = cy - var15;
            float var19 = clamp(ppb * 0.055F * (1.0F - var14 * 0.4F), 1.0F, 4.5F);
            NanoVGRenderer.drawCircle(var17, var18, var19 * 2.0F, argb(-10496, var6 * 0.3F));
            NanoVGRenderer.drawCircle(var17, var18, var19, argb(-1, var6 * 0.95F));
         }
      }
   }

   private void drawCyberVoxel(KillBurst b, float cx, float cy, float ppb, float t) {
      float var6 = 1.0F - Easing.inQuad(t);
      if (!(var6 <= 0.01F)) {
         int var7 = 8;
         float var8 = Easing.outCubic(t) * ppb * 1.8F;

         for (int var9 = 0; var9 < (8); var9++) {
            float var10 = ((var9 & 1) != 0 ? 1.0F : -1.0F) * var8;
            float var11 = ((var9 & (2)) != 0 ? 1.0F : -1.0F) * var8 * 0.6F;
            float var12 = ((var9 & (4)) != 0 ? 1.0F : -1.0F) * var8;
            float var13 = cx + var10 + var12 * 0.3F;
            float var14 = cy + var11 - var12 * 0.2F;
            float var15 = clamp(ppb * 0.14F * (1.0F - t * 0.5F), 2.0F, 10.0F);
            int var16 = var9 % (2) == 0 ? -16714241 : -65409;
            NanoVGRenderer.drawRoundedRect(var13 - var15 * 0.5F, var14 - var15 * 0.5F, var15, var15, 2.0F, argb(var16, var6 * 0.6F));
            NanoVGRenderer.drawRoundedRectOutline(var13 - var15 * 0.5F, var14 - var15 * 0.5F, var15, var15, 2.0F, 1.2F, argb(-1, var6 * 0.9F));
         }

         float var17 = Easing.outCubic(t) * ppb * 2.8F;
         NanoVGRenderer.drawLine(cx - var17, cy, cx + var17, cy, 2.0F, argb(-16714241, var6 * 0.7F));
         NanoVGRenderer.drawLine(cx - var17 * 0.6F, cy - ppb * 0.25F, cx + var17 * 0.6F, cy - ppb * 0.25F, 1.2F, argb(-65409, var6 * 0.5F));
      }
   }

   private void drawKillLabel(KillBurst b, float cx, float cy, float ppb, float t) {
      if (!b.label.isEmpty() || b.combo > 1) {
         float var6 = t < 0.6F ? 1.0F : 1.0F - Easing.inQuad((t - 0.6F) / 0.4F);
         if (!(var6 <= 0.01F)) {
            float var7 = Easing.outCubic(Math.min(1.0F, t / 0.35F)) * ppb * 0.7F;
            float var8 = clamp(ppb * 0.42F, 3.5F, 22.0F);
            float var9 = cy - ppb * 1.1F - var7;
            float var10 = var8 / 14.0F;
            if (!b.label.isEmpty()) {
               String var11 = "☠ " + b.label;
               float var12 = NanoVGRenderer.getTextWidth(var11, var8);
               float var13 = Math.max(1.5F, 6.0F * var10);
               float var14 = Math.max(0.8F, 2.0F * var10);
               float var15 = Math.max(1.0F, 4.0F * var10);
               NanoVGRenderer.drawRoundedRect(cx - var12 * 0.5F - var13, var9 - var14, var12 + var13 * 2.0F, var8 + var14 * 2.0F, var15, argb(0, var6 * 0.5F));
               NanoVGRenderer.drawRoundedRectOutline(
                  cx - var12 * 0.5F - var13,
                  var9 - var14,
                  var12 + var13 * 2.0F,
                  var8 + var14 * 2.0F,
                  var15,
                  Math.max(0.5F, 1.0F * var10),
                  argb(b.color, var6 * 0.4F)
               );
               NanoVGRenderer.drawTextWithShadow(var11, cx - var12 * 0.5F, var9, var8, argb(-1, var6));
            }

            if (b.combo > 1) {
               String var22 = switch (b.combo) {
                  case 2 -> "DOUBLE KILL!";
                  case 3 -> "TRIPLE KILL!";
                  case 4 -> "QUADRA KILL!";
                  case 5 -> "RAMPAGE!";
                  default -> "\ud83d\udc51 GODLIKE x" + b.combo + " \ud83d\udc51";
               };

               int var23 = switch (b.combo) {
                  case 2 -> -16715521;
                  case 3 -> -10496;
                  case 4 -> -29696;
                  case 5 -> -59580;
                  default -> -2080517;
               };
               float var24 = var8 * 1.25F;
               float var25 = NanoVGRenderer.getTextWidth(var22, var24);
               float var26 = Math.max(2.0F, 8.0F * var10);
               float var16 = Math.max(1.0F, 2.5F * var10);
               float var17 = cx - var25 * 0.5F - var26;
               float var18 = var9 - var8 * 1.45F - var16;
               float var19 = var25 + var26 * 2.0F;
               float var20 = var24 + var16 * 2.0F;
               float var21 = Math.max(1.0F, 4.0F * var10);
               NanoVGRenderer.drawRoundedRect(var17, var18, var19, var20, var21, argb(0, var6 * 0.65F));
               NanoVGRenderer.drawRoundedRectOutline(var17, var18, var19, var20, var21, Math.max(0.5F, 1.2F * var10), argb(var23, var6 * 0.85F));
               NanoVGRenderer.drawTextWithShadow(var22, cx - var25 * 0.5F, var18 + var16, var24, argb(var23, var6));
            }
         }
      }
   }

   private static Color argb(int argb, float alphaMul) {
      int var2 = (int)clamp((argb >>> (24) & (255)) * clamp(alphaMul, 0.0F, 1.0F), 0.0F, 255.0F);
      return new Color(argb >> (16) & (255), argb >> (8) & (255), argb & (255), var2);
   }

   private static int brighten(int argb, float amount) {
      int var2 = argb >>> (24) & (255);
      int var3 = (int)clamp((argb >> (16) & (255)) * (1.0F + amount), 0.0F, 255.0F);
      int var4 = (int)clamp((argb >> (8) & (255)) * (1.0F + amount), 0.0F, 255.0F);
      int var5 = (int)clamp((argb & (255)) * (1.0F + amount), 0.0F, 255.0F);
      return var2 << (24) | var3 << (16) | var4 << (8) | var5;
   }

   private static int applyAlpha(int argb, float alphaMul) {
      int var2 = argb >>> (24) & (255);
      if (var2 == 0) {
         var2 = 255;
      }

      int var3 = (int)clamp(var2 * clamp(alphaMul, 0.0F, 1.0F), 0.0F, 255.0F);
      return var3 << (24) | argb & (16777215);
   }

   private static float clamp(float v, float min, float max) {
      return v < min ? min : (v > max ? max : v);
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

   public static final class Config {
      public boolean numbers = true;
      public double numberSize = 1.0;
      public double numberLife = 1.2;
      public CombatFxEngine.NumberStyle numberStyle = CombatFxEngine.NumberStyle.CLASSIC;
      public boolean damageIcons = true;
      public boolean selfDamage = true;
      public boolean showHeal = false;
      public boolean occlusion = true;
      public int mergeWindowMs = 250;
      public int damageColor = -1;
      public int critColor = -20448;
      public int healColor = -10690438;
      public int selfColor = -45747;
      public boolean sparks = true;
      public int sparkCount = 12;
      public int sparkColor = -3904;
      public CombatFxEngine.SparkStyle sparkStyle = CombatFxEngine.SparkStyle.SPARK;
      public boolean killEffect = true;
      public KillBurst.Style killStyle = KillBurst.Style.SHOCKWAVE;
      public int killColor = -4160260;
      public boolean killLabel = true;
      public boolean killSound = true;
      public boolean combo = true;
      public boolean targetMarker = true;
      public CombatFxEngine.TargetMarkerStyle markerStyle = CombatFxEngine.TargetMarkerStyle.ORBITAL;
      public int markerColor = -52378;
      public double markerSize = 1.0;
      public double range = 64.0;
      public int maxFx = 220;
      public CombatFxEngine.Targets targets = CombatFxEngine.Targets.ALL;
      public CombatFxEngine.Source source = CombatFxEngine.Source.ANY;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum NumberStyle {
      CLASSIC,
      BOUNCE,
      EXPAND_FADE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CombatFxEngine.NumberStyle[] $values() {
         CombatFxEngine.NumberStyle[] var10000 = new CombatFxEngine.NumberStyle[3];
         var10000[0] = CLASSIC;
         var10000[1] = BOUNCE;
         var10000[2] = EXPAND_FADE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(-83);
         var10002[5] = (byte)(104);
         var10002[6] = (byte)(-34);
         CLASSIC = new CombatFxEngine.NumberStyle();
         var10002 = new byte[6];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(73);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-67);
         var10002[5] = (byte)(100);
         BOUNCE = new CombatFxEngine.NumberStyle();
         var10002 = new byte[11];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-62);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(-80);
         var10002[5] = (byte)(101);
         var10002[6] = (byte)(-62);
         var10002[7] = (byte)(60);
         var10002[8] = (byte)(17);
         var10002[9] = (byte)(-26);
         var10002[10] = (byte)(-95);
         EXPAND_FADE = new CombatFxEngine.NumberStyle();
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

   public enum Source {
      ANY,
      MINE_ONLY;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CombatFxEngine.Source[] $values() {
         CombatFxEngine.Source[] var10000 = new CombatFxEngine.Source[2];
         var10000[0] = ANY;
         var10000[1] = MINE_ONLY;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(120);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(69);
         ANY = new CombatFxEngine.Source();
         var10002 = new byte[9];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(50);
         var10002[5] = (byte)(-43);
         var10002[6] = (byte)(-115);
         var10002[7] = (byte)(23);
         var10002[8] = (byte)(8);
         MINE_ONLY = new CombatFxEngine.Source();
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

   public enum SparkStyle {
      SPARK,
      BLOOD,
      STAR,
      SHOCKWAVE,
      EMBER,
      SLASH,
      VORTEX,
      LIGHTNING,
      HEARTS,
      PLASMA,
      VOID_TEAR;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CombatFxEngine.SparkStyle[] $values() {
         CombatFxEngine.SparkStyle[] var10000 = new CombatFxEngine.SparkStyle[11];
         var10000[0] = SPARK;
         var10000[1] = BLOOD;
         var10000[2] = STAR;
         var10000[3] = SHOCKWAVE;
         var10000[4] = EMBER;
         var10000[5] = SLASH;
         var10000[6] = VORTEX;
         var10000[7] = LIGHTNING;
         var10000[8] = HEARTS;
         var10000[9] = PLASMA;
         var10000[10] = VOID_TEAR;
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

   public enum TargetMarkerStyle {
      ORBITAL,
      CYBER_RETICLE,
      PULSE_CIRCLE,
      HEXAGON,
      DOT_GLOW;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CombatFxEngine.TargetMarkerStyle[] $values() {
         CombatFxEngine.TargetMarkerStyle[] var10000 = new CombatFxEngine.TargetMarkerStyle[5];
         var10000[0] = ORBITAL;
         var10000[1] = CYBER_RETICLE;
         var10000[2] = PULSE_CIRCLE;
         var10000[3] = HEXAGON;
         var10000[4] = DOT_GLOW;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-120);
         var10002[5] = (byte)(59);
         var10002[6] = (byte)(-105);
         ORBITAL = new CombatFxEngine.TargetMarkerStyle();
         var10002 = new byte[13];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-61);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-114);
         var10002[5] = (byte)(37);
         var10002[6] = (byte)(-119);
         var10002[7] = (byte)(-41);
         var10002[8] = (byte)(2);
         var10002[9] = (byte)(-21);
         var10002[10] = (byte)(-89);
         var10002[11] = (byte)(-70);
         var10002[12] = (byte)(-63);
         CYBER_RETICLE = new CombatFxEngine.TargetMarkerStyle();
         var10002 = new byte[12];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(-103);
         var10002[5] = (byte)(37);
         var10002[6] = (byte)(-104);
         var10002[7] = (byte)(-37);
         var10002[8] = (byte)(4);
         var10002[9] = (byte)(-31);
         var10002[10] = (byte)(-88);
         var10002[11] = (byte)(-77);
         PULSE_CIRCLE = new CombatFxEngine.TargetMarkerStyle();
         var10002 = new byte[7];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(68);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(-101);
         var10002[5] = (byte)(53);
         var10002[6] = (byte)(-107);
         HEXAGON = new CombatFxEngine.TargetMarkerStyle();
         var10002 = new byte[8];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(-101);
         var10002[5] = (byte)(54);
         var10002[6] = (byte)(-108);
         var10002[7] = (byte)(-59);
         DOT_GLOW = new CombatFxEngine.TargetMarkerStyle();
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

   public enum Targets {
      ALL,
      PLAYERS,
      MOBS;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CombatFxEngine.Targets[] $values() {
         CombatFxEngine.Targets[] var10000 = new CombatFxEngine.Targets[3];
         var10000[0] = ALL;
         var10000[1] = PLAYERS;
         var10000[2] = MOBS;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(80);
         ALL = new CombatFxEngine.Targets();
         var10002 = new byte[7];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-121);
         var10002[4] = (byte)(21);
         var10002[5] = (byte)(-11);
         var10002[6] = (byte)(-95);
         PLAYERS = new CombatFxEngine.Targets();
         var10002 = new byte[4];
         var10002[0] = (byte)(117);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(94);
         var10002[3] = (byte)(-115);
         MOBS = new CombatFxEngine.Targets();
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

   private static final class Tracked {
      float health;
      boolean alive;
      int mark;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
