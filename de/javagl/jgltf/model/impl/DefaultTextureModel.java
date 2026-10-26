package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.TextureModel;

public class DefaultTextureModel extends AbstractNamedModelElement implements TextureModel {
   private Integer magFilter;
   private Integer minFilter;
   private Integer wrapS;
   private Integer wrapT;
   private ImageModel imageModel;

   public void setImageModel(ImageModel imageModel) {
      this.imageModel = imageModel;
   }

   @Override
   public Integer getMagFilter() {
      return this.magFilter;
   }

   public void setMagFilter(Integer magFilter) {
      this.magFilter = magFilter;
   }

   @Override
   public Integer getMinFilter() {
      return this.minFilter;
   }

   public void setMinFilter(Integer minFilter) {
      this.minFilter = minFilter;
   }

   @Override
   public Integer getWrapS() {
      return this.wrapS;
   }

   public void setWrapS(Integer wrapS) {
      this.wrapS = wrapS;
   }

   @Override
   public Integer getWrapT() {
      return this.wrapT;
   }

   public void setWrapT(Integer wrapT) {
      this.wrapT = wrapT;
   }

   @Override
   public ImageModel getImageModel() {
      return this.imageModel;
   }
}
