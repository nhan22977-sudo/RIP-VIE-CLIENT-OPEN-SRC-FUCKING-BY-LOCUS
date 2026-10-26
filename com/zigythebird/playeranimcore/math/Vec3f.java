package com.zigythebird.playeranimcore.math;

import java.util.Objects;

public record Vec3f(float x, float y, float z) {
   public static final Vec3f ZERO = new Vec3f(0.0F, 0.0F, 0.0F);
   public static final Vec3f ONE = new Vec3f(1.0F, 1.0F, 1.0F);

   public Vec3f mul(float scalar) {
      return new Vec3f(this.x * scalar, this.y * scalar, this.z * scalar);
   }

   public Vec3f add(Vec3f other) {
      return new Vec3f(this.x + other.x, this.y + other.y, this.z + other.z);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof Vec3f vec) ? false : Objects.equals(this.x, vec.x) && Objects.equals(this.y, vec.y) && Objects.equals(this.z, vec.z);
      }
   }

   @Override
   public String toString() {
      return "Vec3f[" + this.x + "; " + this.y + "; " + this.z + "]";
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.x, this.y, this.z);
   }
}
