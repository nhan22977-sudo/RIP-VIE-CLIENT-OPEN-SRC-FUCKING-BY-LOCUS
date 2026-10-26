package de.javagl.jgltf.model.v2;

import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.TextureModel;
import de.javagl.jgltf.model.impl.AbstractNamedModelElement;

public final class MaterialModelV2 extends AbstractNamedModelElement implements MaterialModel {
   private float[] baseColorFactor = new float[]{1.0F, 1.0F, 1.0F, 1.0F};
   private TextureModel baseColorTexture = null;
   private Integer baseColorTexcoord = null;
   private float metallicFactor = 1.0F;
   private float roughnessFactor = 1.0F;
   private TextureModel metallicRoughnessTexture = null;
   private Integer metallicRoughnessTexcoord = null;
   private TextureModel normalTexture;
   private Integer normalTexcoord;
   private float normalScale = 1.0F;
   private TextureModel occlusionTexture;
   private Integer occlusionTexcoord;
   private float occlusionStrength;
   private TextureModel emissiveTexture;
   private Integer emissiveTexcoord;
   private float[] emissiveFactor;
   private MaterialModelV2.AlphaMode alphaMode;
   private float alphaCutoff;
   private boolean doubleSided;

   public MaterialModelV2() {
      this.normalTexture = null;
      this.normalTexcoord = null;
      this.occlusionTexture = null;
      this.occlusionTexcoord = null;
      this.occlusionStrength = 1.0F;
      this.emissiveTexture = null;
      this.emissiveTexcoord = null;
      this.emissiveFactor = new float[]{0.0F, 0.0F, 0.0F};
      this.alphaMode = MaterialModelV2.AlphaMode.OPAQUE;
      this.alphaCutoff = 0.5F;
      this.doubleSided = false;
   }

   public float[] getBaseColorFactor() {
      return this.baseColorFactor;
   }

   public void setBaseColorFactor(float[] baseColorFactor) {
      this.baseColorFactor = baseColorFactor;
   }

   public TextureModel getBaseColorTexture() {
      return this.baseColorTexture;
   }

   public void setBaseColorTexture(TextureModel baseColorTexture) {
      this.baseColorTexture = baseColorTexture;
   }

   public Integer getBaseColorTexcoord() {
      return this.baseColorTexcoord;
   }

   public void setBaseColorTexcoord(Integer baseColorTexcoord) {
      this.baseColorTexcoord = baseColorTexcoord;
   }

   public float getMetallicFactor() {
      return this.metallicFactor;
   }

   public void setMetallicFactor(float metallicFactor) {
      this.metallicFactor = metallicFactor;
   }

   public float getRoughnessFactor() {
      return this.roughnessFactor;
   }

   public void setRoughnessFactor(float roughnessFactor) {
      this.roughnessFactor = roughnessFactor;
   }

   public TextureModel getMetallicRoughnessTexture() {
      return this.metallicRoughnessTexture;
   }

   public void setMetallicRoughnessTexture(TextureModel metallicRoughnessTexture) {
      this.metallicRoughnessTexture = metallicRoughnessTexture;
   }

   public Integer getMetallicRoughnessTexcoord() {
      return this.metallicRoughnessTexcoord;
   }

   public void setMetallicRoughnessTexcoord(Integer metallicRoughnessTexcoord) {
      this.metallicRoughnessTexcoord = metallicRoughnessTexcoord;
   }

   public TextureModel getNormalTexture() {
      return this.normalTexture;
   }

   public void setNormalTexture(TextureModel normalTexture) {
      this.normalTexture = normalTexture;
   }

   public Integer getNormalTexcoord() {
      return this.normalTexcoord;
   }

   public void setNormalTexcoord(Integer normalTexcoord) {
      this.normalTexcoord = normalTexcoord;
   }

   public float getNormalScale() {
      return this.normalScale;
   }

   public void setNormalScale(float normalScale) {
      this.normalScale = normalScale;
   }

   public TextureModel getOcclusionTexture() {
      return this.occlusionTexture;
   }

   public void setOcclusionTexture(TextureModel occlusionTexture) {
      this.occlusionTexture = occlusionTexture;
   }

   public Integer getOcclusionTexcoord() {
      return this.occlusionTexcoord;
   }

   public void setOcclusionTexcoord(Integer occlusionTexcoord) {
      this.occlusionTexcoord = occlusionTexcoord;
   }

   public float getOcclusionStrength() {
      return this.occlusionStrength;
   }

   public void setOcclusionStrength(float occlusionStrength) {
      this.occlusionStrength = occlusionStrength;
   }

   public TextureModel getEmissiveTexture() {
      return this.emissiveTexture;
   }

   public void setEmissiveTexture(TextureModel emissiveTexture) {
      this.emissiveTexture = emissiveTexture;
   }

   public Integer getEmissiveTexcoord() {
      return this.emissiveTexcoord;
   }

   public void setEmissiveTexcoord(Integer emissiveTexcoord) {
      this.emissiveTexcoord = emissiveTexcoord;
   }

   public float[] getEmissiveFactor() {
      return this.emissiveFactor;
   }

   public void setEmissiveFactor(float[] emissiveFactor) {
      this.emissiveFactor = emissiveFactor;
   }

   public MaterialModelV2.AlphaMode getAlphaMode() {
      return this.alphaMode;
   }

   public void setAlphaMode(MaterialModelV2.AlphaMode alphaMode) {
      this.alphaMode = alphaMode;
   }

   public float getAlphaCutoff() {
      return this.alphaCutoff;
   }

   public void setAlphaCutoff(float alphaCutoff) {
      this.alphaCutoff = alphaCutoff;
   }

   public boolean isDoubleSided() {
      return this.doubleSided;
   }

   public void setDoubleSided(boolean doubleSided) {
      this.doubleSided = doubleSided;
   }

   public enum AlphaMode {
      OPAQUE,
      MASK,
      BLEND;
   }
}
