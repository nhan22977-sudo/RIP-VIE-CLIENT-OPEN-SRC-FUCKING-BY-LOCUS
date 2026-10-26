package me.drake.vieclient.gui.nvg.animation;

import java.util.HashMap;
import java.util.Map;
import me.drake.vieclient.gui.nvg.adapter.module.Category;
import me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient;

public class AnimationManager {
   private final Map categoryAnimations = new HashMap();
   private final Map moduleAnimations = new HashMap();
   private final Map dropdownAnimations = new HashMap();
   private float sidebarAnimation = 0.0F;
   private float contentAnimation = 0.0F;
   private float categorySwitch = 1.0F;
   private float guiAnimation = 0.0F;
   private float scaleAnimation = 0.0F;
   private boolean isClosing = false;
   private boolean animationComplete = false;
   private long openTime = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AnimationManager() {
      for (Category var4 : Category.values()) {
         this.categoryAnimations.put(var4, 0.0F);
      }
   }

   private static float lerp(float delta, float start, float end) {
      return start + (end - start) * delta;
   }

   public void update(float deltaTime) {
      float var2 = 8.0F;
      if (!this.isClosing) {
         this.guiAnimation = Math.min(1.0F, this.guiAnimation + deltaTime * var2);
         this.scaleAnimation = Math.min(1.0F, this.scaleAnimation + deltaTime * var2);

         for (Category var4 : this.categoryAnimations.keySet()) {
            float var5 = (Float)this.categoryAnimations.get(var4);
            this.categoryAnimations.put(var4, Math.min(1.0F, var5 + deltaTime * var2));
         }
      } else {
         this.guiAnimation = Math.max(0.0F, this.guiAnimation - deltaTime * var2);
         this.scaleAnimation = Math.max(0.0F, this.scaleAnimation - deltaTime * var2);
         if (this.guiAnimation <= 0.0F) {
            this.animationComplete = true;
         }
      }
   }

   public void startClosing() {
      this.isClosing = true;
   }

   public boolean isAnimationComplete() {
      return this.animationComplete;
   }

   public float getGuiAnimation() {
      return this.guiAnimation;
   }

   public float getScaleAnimation() {
      return easeOutCubic(this.scaleAnimation);
   }

   public float getCategoryAnimation(Category cat) {
      return this.categoryAnimations.getOrDefault(cat, 0.0F);
   }

   public void setOpen(long time) {
      this.openTime = time;
      this.isClosing = false;
      this.animationComplete = false;
   }

   public static float easeOutCubic(float t) {
      return 1.0F - (float)Math.pow(1.0F - t, 3.0);
   }

   public static float easeInOutCubic(float t) {
      return t < 0.5F ? 4.0F * t * t * t : 1.0F - (float)Math.pow(-2.0F * t + 2.0F, 3.0) / 2.0F;
   }

   public static float easeOutExpo(float t) {
      return t >= 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0, -10.0 * t);
   }

   public static float easeOutBack(float t) {
      float var1 = 1.70158F;
      float var2 = var1 + 1.0F;
      float var3 = t - 1.0F;
      return 1.0F + var2 * var3 * var3 * var3 + var1 * var3 * var3;
   }

   public static float easeOutQuint(float t) {
      return 1.0F - (float)Math.pow(1.0F - t, 5.0);
   }

   public static float easeOutQuart(float t) {
      return 1.0F - (float)Math.pow(1.0F - t, 4.0);
   }

   public static float damp(float current, float target, float rate, float dt) {
      if (dt <= 0.0F) {
         return current;
      }

      if (dt > 0.1F) {
         dt = 0.1F;
      }

      return current + (target - current) * (1.0F - (float)Math.exp(-rate * dt));
   }

   public static boolean isOff() {
      try {
         return VieClient.animationMode.isMode(VieClient.AnimationMode.OFF);
      } catch (Throwable var1) {
         return false;
      }
   }

   public static boolean isPositive() {
      try {
         return VieClient.animationMode.isMode(VieClient.AnimationMode.POSITIVE);
      } catch (Throwable var1) {
         return false;
      }
   }

   public static float popAmp() {
      return isPositive() ? 1.7F : 1.0F;
   }

   public static float speedMul() {
      try {
         return switch ((VieClient.AnimSpeed)VieClient.animationSpeed.getValue()) {
            case SUPER_SLOW -> 0.45F;
            case SLOW -> 0.7F;
            default -> 1.0F;
            case FAST -> 1.7F;
         };
      } catch (Throwable var1) {
         return 1.0F;
      }
   }
}
