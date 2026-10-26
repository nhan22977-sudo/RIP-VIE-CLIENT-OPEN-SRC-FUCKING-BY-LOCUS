package me.drake.vieclient.modules.combat;

public final class CombatAim {
   private final CombatAim.Axis yaw = new CombatAim.Axis();
   private final CombatAim.Axis pitch = new CombatAim.Axis();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public CombatAim.Angles step(
      float currentYaw, float currentPitch, float targetYaw, float targetPitch, double degreesPerTick, float sensitivity, float frameSeconds, boolean humanize
   ) {
      if (Float.isFinite(frameSeconds) && !(frameSeconds <= 0.0F)) {
         float var10 = Math.min(frameSeconds, 0.05F);
         float var11 = wrap(targetYaw - currentYaw);
         float var12 = clamp(targetPitch, -90.0F, 90.0F) - currentPitch;
         float var13 = clamp(sensitivity, 0.0F, 1.0F) * 0.6F + 0.2F;
         float var14 = var13 * var13 * var13 * 1.2F;
         float var15 = (float)Math.min(Math.max(1.0, degreesPerTick) * 20.0, 360.0);
         float var16 = currentPitch + this.pitch.step(var12, var15 * 0.9F, var14, var10, humanize);
         if (var16 > 90.0F) {
            float var17 = (float)Math.floor((90.0F - currentPitch) / var14) * var14;
            var16 = currentPitch + Math.max(0.0F, var17);
         } else if (var16 < -90.0F) {
            float var18 = (float)Math.floor((currentPitch - -90.0F) / var14) * var14;
            var16 = currentPitch - Math.max(0.0F, var18);
         }

         return new CombatAim.Angles(currentYaw + this.yaw.step(var11, var15, var14, var10, humanize), clamp(var16, -90.0F, 90.0F));
      } else {
         return new CombatAim.Angles(currentYaw, currentPitch);
      }
   }

   public void reset() {
      this.yaw.reset();
      this.pitch.reset();
   }

   public static float wrap(float angle) {
      float var1 = angle % 360.0F;
      if (var1 >= 180.0F) {
         var1 -= 360.0F;
      }

      if (var1 < -180.0F) {
         var1 += 360.0F;
      }

      return var1;
   }

   private static float clamp(float value, float min, float max) {
      return Math.max(min, Math.min(max, value));
   }

   public record Angles(float yaw, float pitch) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private static final class Axis {
      private float velocity;
      private float residual;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      float step(float error, float maxSpeed, float gcd, float dt, boolean humanize) {
         if (Math.abs(error) < gcd) {
            this.reset();
            return 0.0F;
         }

         if (this.velocity * error < 0.0F) {
            this.reset();
         }

         float var6 = Math.copySign(Math.min(maxSpeed, Math.abs(error) * (humanize ? 16 : 18)), error);
         float var7 = humanize ? 22.0F : 26.0F;
         this.velocity = this.velocity + (var6 - this.velocity) * (1.0F - (float)Math.exp(-var7 * dt));
         float var8 = Math.min(Math.abs(error), Math.max(gcd, maxSpeed * dt));
         this.residual = this.residual + this.velocity * dt;
         float var9 = Math.round(this.residual / gcd) * gcd;
         float var10 = (float)Math.floor(var8 / gcd) * gcd;
         var9 = Math.copySign(Math.min(Math.abs(var9), var10), error);
         this.residual = CombatAim.clamp(this.residual - var9, -gcd, gcd);
         return var9;
      }

      void reset() {
         this.velocity = 0.0F;
         this.residual = 0.0F;
      }
   }
}
