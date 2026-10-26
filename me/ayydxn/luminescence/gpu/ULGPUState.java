package me.ayydxn.luminescence.gpu;

import me.ayydxn.luminescence.geometry.ULIntRect;

public class ULGPUState {
   public int viewportWidth;
   public int viewportHeight;
   public float[] transform;
   public boolean enableTexturing;
   public boolean enableBlend;
   public ULShaderType shaderType;
   public int renderBufferId;
   public int texture1Id;
   public int texture2Id;
   public int texture3Id;
   public float[] uniformScalar;
   public float[] uniformVector;
   public int clipSize;
   public float[] clip;
   public boolean enableScissor;
   public ULIntRect scissorRect;
}
