package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.CameraOrthographicModel;

public class DefaultCameraOrthographicModel implements CameraOrthographicModel {
   private Float xmag;
   private Float ymag;
   private Float zfar;
   private Float znear;

   public void setXmag(Float xmag) {
      this.xmag = xmag;
   }

   public void setYmag(Float ymag) {
      this.ymag = ymag;
   }

   public void setZfar(Float zfar) {
      this.zfar = zfar;
   }

   public void setZnear(Float znear) {
      this.znear = znear;
   }

   @Override
   public Float getXmag() {
      return this.xmag;
   }

   @Override
   public Float getYmag() {
      return this.ymag;
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
