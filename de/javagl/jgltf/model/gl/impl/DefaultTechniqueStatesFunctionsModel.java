package de.javagl.jgltf.model.gl.impl;

import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;

public class DefaultTechniqueStatesFunctionsModel implements TechniqueStatesFunctionsModel {
   private float[] blendColor;
   private int[] blendEquationSeparate;
   private int[] blendFuncSeparate;
   private boolean[] colorMask;
   private int[] cullFace;
   private int[] depthFunc;
   private boolean[] depthMask;
   private float[] depthRange;
   private int[] frontFace;
   private float[] lineWidth;
   private float[] polygonOffset;

   @Override
   public float[] getBlendColor() {
      return this.blendColor;
   }

   public void setBlendColor(float[] blendColor) {
      this.blendColor = blendColor;
   }

   @Override
   public int[] getBlendEquationSeparate() {
      return this.blendEquationSeparate;
   }

   public void setBlendEquationSeparate(int[] blendEquationSeparate) {
      this.blendEquationSeparate = blendEquationSeparate;
   }

   @Override
   public int[] getBlendFuncSeparate() {
      return this.blendFuncSeparate;
   }

   public void setBlendFuncSeparate(int[] blendFuncSeparate) {
      this.blendFuncSeparate = blendFuncSeparate;
   }

   @Override
   public boolean[] getColorMask() {
      return this.colorMask;
   }

   public void setColorMask(boolean[] colorMask) {
      this.colorMask = colorMask;
   }

   @Override
   public int[] getCullFace() {
      return this.cullFace;
   }

   public void setCullFace(int[] cullFace) {
      this.cullFace = cullFace;
   }

   @Override
   public int[] getDepthFunc() {
      return this.depthFunc;
   }

   public void setDepthFunc(int[] depthFunc) {
      this.depthFunc = depthFunc;
   }

   @Override
   public boolean[] getDepthMask() {
      return this.depthMask;
   }

   public void setDepthMask(boolean[] depthMask) {
      this.depthMask = depthMask;
   }

   @Override
   public float[] getDepthRange() {
      return this.depthRange;
   }

   public void setDepthRange(float[] depthRange) {
      this.depthRange = depthRange;
   }

   @Override
   public int[] getFrontFace() {
      return this.frontFace;
   }

   public void setFrontFace(int[] frontFace) {
      this.frontFace = frontFace;
   }

   @Override
   public float[] getLineWidth() {
      return this.lineWidth;
   }

   public void setLineWidth(float[] lineWidth) {
      this.lineWidth = lineWidth;
   }

   @Override
   public float[] getPolygonOffset() {
      return this.polygonOffset;
   }

   public void setPolygonOffset(float[] polygonOffset) {
      this.polygonOffset = polygonOffset;
   }
}
