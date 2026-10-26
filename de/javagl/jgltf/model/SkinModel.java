package de.javagl.jgltf.model;

import java.util.List;

public interface SkinModel extends NamedModelElement {
   float[] getBindShapeMatrix(float[] var1);

   List<NodeModel> getJoints();

   NodeModel getSkeleton();

   AccessorModel getInverseBindMatrices();

   float[] getInverseBindMatrix(int var1, float[] var2);
}
