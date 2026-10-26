package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.AssetModel;

public class DefaultAssetModel extends AbstractNamedModelElement implements AssetModel {
   private String copyright;
   private String generator;

   public void setCopyright(String copyright) {
      this.copyright = copyright;
   }

   @Override
   public String getCopyright() {
      return this.copyright;
   }

   public void setGenerator(String generator) {
      this.generator = generator;
   }

   @Override
   public String getGenerator() {
      return this.generator;
   }
}
