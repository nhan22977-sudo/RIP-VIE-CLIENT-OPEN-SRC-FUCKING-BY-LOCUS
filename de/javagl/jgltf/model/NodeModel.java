package de.javagl.jgltf.model;

import java.util.List;
import java.util.function.Supplier;

public interface NodeModel extends NamedModelElement {
   NodeModel getParent();

   List<NodeModel> getChildren();

   List<MeshModel> getMeshModels();

   SkinModel getSkinModel();

   CameraModel getCameraModel();

   void setMatrix(float[] var1);

   float[] getMatrix();

   void setTranslation(float[] var1);

   float[] getTranslation();

   void setRotation(float[] var1);

   float[] getRotation();

   void setScale(float[] var1);

   float[] getScale();

   void setWeights(float[] var1);

   float[] getWeights();

   float[] computeLocalTransform(float[] var1);

   float[] computeGlobalTransform(float[] var1);

   Supplier<float[]> createGlobalTransformSupplier();

   Supplier<float[]> createLocalTransformSupplier();
}
