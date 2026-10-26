package me.drake.vieclient.util.render;

import me.drake.vieclient.gui.nvg.animation.AnimationManager;
import net.minecraft.class_310;

public class EspFade {
   private static final float DEFAULT_DURATION_MS = 220.0F;
   private float currentFade = 0.0F;
   private float fromFade = 0.0F;
   private float targetFade = 0.0F;
   private long animStartTimeNanos = 0L;
   private float animDurationMs = 220.0F;
   private boolean active = false;
   private final Runnable onCompleteDisable;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public EspFade(Runnable onCompleteDisable) {
      this.onCompleteDisable = onCompleteDisablex;
   }

   public synchronized void onEnable() {
      if (this.active && this.targetFade == 1.0F) {
         if (this.currentFade >= 0.999F) {
            this.currentFade = 1.0F;
            return;
         }

         if (this.animDurationMs > 0.0F) {
            return;
         }
      }

      if (!this.active) {
         this.currentFade = 0.0F;
         this.fromFade = 0.0F;
      } else {
         this.fromFade = Math.max(0.0F, Math.min(1.0F, this.currentFade));
      }

      this.active = true;
      this.targetFade = 1.0F;
      this.animStartTimeNanos = System.nanoTime();
      if (!AnimationManager.isOff() && isInGame()) {
         float var1 = Math.abs(this.targetFade - this.fromFade);
         float var2 = Math.max(0.1F, AnimationManager.speedMul());
         this.animDurationMs = Math.max(20.0F, 220.0F * var1 / var2);
      } else {
         this.currentFade = 1.0F;
         this.fromFade = 1.0F;
         this.animDurationMs = 0.0F;
      }
   }

   public synchronized void onDisable() {
      if (this.active || !(this.currentFade <= 0.001F)) {
         if (!this.active || this.targetFade != 0.0F) {
            if (!AnimationManager.isOff() && isInGame() && !(this.currentFade <= 0.001F)) {
               this.active = true;
               this.targetFade = 0.0F;
               this.fromFade = Math.max(0.0F, Math.min(1.0F, this.currentFade));
               this.animStartTimeNanos = System.nanoTime();
               float var1 = Math.abs(this.targetFade - this.fromFade);
               float var2 = Math.max(0.1F, AnimationManager.speedMul());
               this.animDurationMs = Math.max(20.0F, 220.0F * var1 / var2);
            } else {
               this.finish();
            }
         }
      }
   }

   public synchronized float getFade() {
      this.update();
      return this.currentFade;
   }

   public synchronized boolean isActive() {
      this.update();
      return this.active && (this.targetFade > 0.0F || this.currentFade > 0.001F);
   }

   public synchronized boolean isFadingOut() {
      return this.active && this.targetFade == 0.0F;
   }

   public synchronized void update() {
      if (!this.active) {
         this.currentFade = 0.0F;
      } else if (!isInGame()) {
         this.currentFade = this.targetFade;
         if (this.targetFade <= 0.001F) {
            this.finish();
         }
      } else if (!(this.animDurationMs <= 0.0F) && !AnimationManager.isOff()) {
         long var1 = System.nanoTime() - this.animStartTimeNanos;
         if (var1 < 0L) {
            var1 = 0L;
         }

         long var3 = (long)(this.animDurationMs * 1000000.0F);
         if (var3 > 0L && var1 < var3) {
            float var5 = Math.min(1.0F, Math.max(0.0F, (float)((double)var1 / var3)));
            float var6 = AnimationManager.easeInOutCubic(var5);
            float var7 = this.fromFade + (this.targetFade - this.fromFade) * var6;
            if (this.targetFade >= this.fromFade) {
               this.currentFade = Math.max(this.currentFade, Math.max(0.0F, Math.min(1.0F, var7)));
            } else {
               this.currentFade = Math.min(this.currentFade, Math.max(0.0F, Math.min(1.0F, var7)));
            }
         } else {
            this.currentFade = this.targetFade;
            if (this.targetFade <= 0.001F) {
               this.finish();
            }
         }
      } else {
         this.currentFade = this.targetFade;
         if (this.targetFade <= 0.001F) {
            this.finish();
         }
      }
   }

   public synchronized void forceFinish() {
      this.finish();
   }

   private void finish() {
      this.currentFade = 0.0F;
      this.fromFade = 0.0F;
      this.targetFade = 0.0F;
      this.active = false;
      if (this.onCompleteDisable != null) {
         try {
            this.onCompleteDisable.run();
         } catch (Throwable var2) {
         }
      }
   }

   private static boolean isInGame() {
      class_310 var0 = class_310.method_1551();
      return var0 != null && var0.field_1724 != null && var0.field_1687 != null;
   }
}
