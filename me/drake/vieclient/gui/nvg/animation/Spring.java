package me.drake.vieclient.gui.nvg.animation;

public final class Spring {
   public float value;
   public float target;
   public float vel;
   private final float stiffness;
   private final float damping;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Spring(float initial) {
      this(initial, 200.0F, 22.0F);
   }

   public Spring(float initial, float stiffness, float damping) {
      this.value = this.target = initial;
      this.stiffness = stiffnessx;
      this.damping = dampingx;
   }

   public void set(float target) {
      this.target = targetx;
   }

   public void snap(float v) {
      this.value = this.target = v;
      this.vel = 0.0F;
   }

   public void update(float dt) {
      if (!(dt <= 0.0F)) {
         dt = Math.min(dt, 0.05F);
         float var2 = -this.stiffness * (this.value - this.target) - this.damping * this.vel;
         this.vel += var2 * dt;
         this.value = this.value + this.vel * dt;
      }
   }
}
