package me.drake.vieclient.render.fx;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_243;

public final class FloatingNumber {
   public int entityId;
   public double x;
   public double y;
   public double z;
   public double vx;
   public double vy;
   public double vz;
   public float age;
   public float life;
   public float amount;
   public FloatingNumber.Kind kind;
   public long mergeUntil;
   public float mergeKick;
   public boolean visible = true;
   public long visCheckedAtMs;
   public String text = "";
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FloatingNumber init(int entityId, class_243 origin, float amount, FloatingNumber.Kind kind, float life, double driftX, double driftY, double driftZ) {
      this.entityId = entityIdx;
      this.x = origin.field_1352;
      this.y = origin.field_1351;
      this.z = origin.field_1350;
      this.vx = driftX;
      this.vy = driftY;
      this.vz = driftZ;
      this.age = 0.0F;
      this.life = Math.max(0.2F, lifex);
      this.amount = amountx;
      this.kind = kindx;
      this.mergeUntil = 0L;
      this.mergeKick = 0.0F;
      this.visible = true;
      this.visCheckedAtMs = 0L;
      this.text = "";
      return this;
   }

   public void merge(float extra, boolean crit) {
      this.amount += extra;
      this.text = "";
      this.mergeKick = 0.35F;
      if (crit && this.kind == FloatingNumber.Kind.DAMAGE) {
         this.kind = FloatingNumber.Kind.CRIT;
      }

      this.age = Math.min(this.age, this.life * 0.25F);
   }

   public void update(float dt) {
      this.age += dt;
      this.vy -= 1.15 * dt;
      double var2 = Math.pow(0.06, dt);
      this.vx *= var2;
      this.vz *= var2;
      this.x = this.x + this.vx * dt;
      this.y = this.y + this.vy * dt;
      this.z = this.z + this.vz * dt;
      if (this.mergeKick > 0.0F) {
         this.mergeKick -= dt * 2.2F;
         if (this.mergeKick < 0.0F) {
            this.mergeKick = 0.0F;
         }
      }
   }

   public boolean alive() {
      return this.age < this.life;
   }

   public float progress() {
      return this.life <= 0.0F ? 1.0F : this.age / this.life;
   }

   public class_243 pos() {
      return new class_243(this.x, this.y, this.z);
   }

   public enum Kind {
      DAMAGE,
      CRIT,
      HEAL,
      SELF;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static FloatingNumber.Kind[] $values() {
         FloatingNumber.Kind[] var10000 = new FloatingNumber.Kind[4];
         var10000[0] = DAMAGE;
         var10000[1] = CRIT;
         var10000[2] = HEAL;
         var10000[3] = SELF;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(88);
         var10002[5] = (byte)(66);
         DAMAGE = new FloatingNumber.Kind();
         var10002 = new byte[4];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-118);
         CRIT = new FloatingNumber.Kind();
         var10002 = new byte[4];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-110);
         HEAL = new FloatingNumber.Kind();
         var10002 = new byte[4];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-104);
         SELF = new FloatingNumber.Kind();
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
