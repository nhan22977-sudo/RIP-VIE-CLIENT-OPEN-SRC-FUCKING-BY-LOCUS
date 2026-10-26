package me.drake.vieclient.render.fx;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_243;

public final class KillBurst {
   public double x;
   public double y;
   public double z;
   public float age;
   public float life;
   public KillBurst.Style style;
   public int color;
   public String label = "";
   public int combo;
   public int seed;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public KillBurst init(class_243 origin, KillBurst.Style style, int color, String label, int combo, float life, int seed) {
      this.x = origin.field_1352;
      this.y = origin.field_1351;
      this.z = origin.field_1350;
      this.age = 0.0F;
      this.life = Math.max(0.3F, lifex);
      this.style = stylex;
      this.color = colorx;
      this.label = labelx == null ? "" : labelx;
      this.combo = combox;
      this.seed = seedx;
      return this;
   }

   public void update(float dt) {
      this.age += dt;
   }

   public boolean alive() {
      return this.age < this.life;
   }

   public float progress() {
      return this.life <= 0.0F ? 1.0F : this.age / this.life;
   }

   public float rand(int i) {
      int var2 = this.seed * (31) + i;
      var2 ^= var2 >>> (16);
      var2 *= -2048144789;
      var2 ^= var2 >>> (13);
      var2 *= -1028477387;
      var2 ^= var2 >>> (16);
      return (var2 >>> (8)) / 1.6777216E7F;
   }

   public float randSigned(int i) {
      return this.rand(i) * 2.0F - 1.0F;
   }

   public enum Style {
      SHOCKWAVE,
      SHATTER,
      SOUL,
      SAKURA,
      LIGHTNING,
      SUPERNOVA,
      FIREWORK,
      BLACK_HOLE,
      BLOOD_GEYSER,
      ANGELIC_ASCENSION,
      CYBER_VOXEL;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static KillBurst.Style[] $values() {
         KillBurst.Style[] var10000 = new KillBurst.Style[11];
         var10000[0] = SHOCKWAVE;
         var10000[1] = SHATTER;
         var10000[2] = SOUL;
         var10000[3] = SAKURA;
         var10000[4] = LIGHTNING;
         var10000[5] = SUPERNOVA;
         var10000[6] = FIREWORK;
         var10000[7] = BLACK_HOLE;
         var10000[8] = BLOOD_GEYSER;
         var10000[9] = ANGELIC_ASCENSION;
         var10000[10] = CYBER_VOXEL;
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
