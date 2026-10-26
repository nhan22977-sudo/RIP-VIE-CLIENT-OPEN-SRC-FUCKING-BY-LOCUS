package me.drake.vieclient.render.fx;

public final class Spark {
   public double x;
   public double y;
   public double z;
   public double vx;
   public double vy;
   public double vz;
   public float age;
   public float life;
   public float gravity;
   public float trail;
   public int color;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Spark init(double x, double y, double z, double vx, double vy, double vz, float life, float gravity, float trail, int color) {
      this.x = xx;
      this.y = yx;
      this.z = zx;
      this.vx = vxx;
      this.vy = vyx;
      this.vz = vzx;
      this.age = 0.0F;
      this.life = Math.max(0.05F, lifex);
      this.gravity = gravityx;
      this.trail = trailx;
      this.color = colorx;
      return this;
   }

   public void update(float dt) {
      this.age += dt;
      this.vy = this.vy - 14.0 * this.gravity * dt;
      double var2 = Math.pow(0.12, dt);
      this.vx *= var2;
      this.vy *= var2;
      this.vz *= var2;
      this.x = this.x + this.vx * dt;
      this.y = this.y + this.vy * dt;
      this.z = this.z + this.vz * dt;
   }

   public boolean alive() {
      return this.age < this.life;
   }

   public float progress() {
      return this.life <= 0.0F ? 1.0F : this.age / this.life;
   }

   public double tailX() {
      return this.x - this.vx * this.trail;
   }

   public double tailY() {
      return this.y - this.vy * this.trail;
   }

   public double tailZ() {
      return this.z - this.vz * this.trail;
   }
}
