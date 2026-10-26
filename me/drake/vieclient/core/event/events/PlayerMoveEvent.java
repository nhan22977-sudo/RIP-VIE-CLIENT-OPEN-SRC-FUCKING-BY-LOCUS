package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.CancellableEvent;

public class PlayerMoveEvent extends CancellableEvent {
   private double x;
   private double y;
   private double z;
   private float yaw;
   private float pitch;
   private boolean onGround;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public PlayerMoveEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
      this.x = xx;
      this.y = yx;
      this.z = zx;
      this.yaw = yawx;
      this.pitch = pitchx;
      this.onGround = onGroundx;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public void setX(double x) {
      this.x = xx;
   }

   public void setY(double y) {
      this.y = yx;
   }

   public void setZ(double z) {
      this.z = zx;
   }

   public void setYaw(float yaw) {
      this.yaw = yawx;
   }

   public void setPitch(float pitch) {
      this.pitch = pitchx;
   }

   public void setOnGround(boolean onGround) {
      this.onGround = onGroundx;
   }
}
