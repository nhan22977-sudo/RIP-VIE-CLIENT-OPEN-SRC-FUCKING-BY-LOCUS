package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.CameraPerspectiveModel;

public class DefaultCameraPerspectiveModel implements CameraPerspectiveModel {
   private Float aspectRatio;
   private Float yfov;
   private Float zfar;
   private Float znear;

   public void setAspectRatio(Float aspectRatio) {
      this.aspectRatio = aspectRatio;
   }

   public void setYfov(Float yfov) {
      this.yfov = yfov;
   }

   public void setZfar(Float zfar) {
      this.zfar = zfar;
   }

   public void setZnear(Float znear) {
      this.znear = znear;
   }

   @Override
   public Float getAspectRatio() {
      return this.aspectRatio;
   }

   @Override
   public Float getYfov() {
      return this.yfov;
   }

   @Override
   public Float getZfar() {
      return this.zfar;
   }

   @Override
   public Float getZnear() {
      return this.znear;
   }
}
