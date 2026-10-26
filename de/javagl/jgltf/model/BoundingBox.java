package de.javagl.jgltf.model;

import java.util.Objects;

class BoundingBox {
   private float minX = Float.MAX_VALUE;
   private float minY = Float.MAX_VALUE;
   private float minZ = Float.MAX_VALUE;
   private float maxX = -Float.MAX_VALUE;
   private float maxY = -Float.MAX_VALUE;
   private float maxZ = -Float.MAX_VALUE;

   void combine(float x, float y, float z) {
      this.minX = Math.min(this.minX, x);
      this.minY = Math.min(this.minY, y);
      this.minZ = Math.min(this.minZ, z);
      this.maxX = Math.max(this.maxX, x);
      this.maxY = Math.max(this.maxY, y);
      this.maxZ = Math.max(this.maxZ, z);
   }

   void combine(BoundingBox other) {
      Objects.requireNonNull(other, "The other bounding box may not be null");
      this.minX = Math.min(this.minX, other.getMinX());
      this.minY = Math.min(this.minY, other.getMinY());
      this.minZ = Math.min(this.minZ, other.getMinZ());
      this.maxX = Math.max(this.maxX, other.getMaxX());
      this.maxY = Math.max(this.maxY, other.getMaxY());
      this.maxZ = Math.max(this.maxZ, other.getMaxZ());
   }

   float getCenterX() {
      return this.getMinX() + this.getSizeX() * 0.5F;
   }

   float getCenterY() {
      return this.getMinY() + this.getSizeY() * 0.5F;
   }

   float getCenterZ() {
      return this.getMinZ() + this.getSizeZ() * 0.5F;
   }

   float getSizeX() {
      return this.getMaxX() - this.getMinX();
   }

   float getSizeY() {
      return this.getMaxY() - this.getMinY();
   }

   float getSizeZ() {
      return this.getMaxZ() - this.getMinZ();
   }

   float getMinX() {
      return this.minX;
   }

   float getMinY() {
      return this.minY;
   }

   float getMinZ() {
      return this.minZ;
   }

   float getMaxX() {
      return this.maxX;
   }

   float getMaxY() {
      return this.maxY;
   }

   float getMaxZ() {
      return this.maxZ;
   }

   @Override
   public String toString() {
      return "[(" + this.getMinX() + "," + this.getMinY() + "," + this.getMinZ() + ")-(" + this.getMaxX() + "," + this.getMaxY() + "," + this.getMaxZ() + ")]";
   }
}
