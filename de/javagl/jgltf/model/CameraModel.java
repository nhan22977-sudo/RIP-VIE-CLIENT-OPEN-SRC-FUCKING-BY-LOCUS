package de.javagl.jgltf.model;

import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public interface CameraModel extends NamedModelElement {
   CameraOrthographicModel getCameraOrthographicModel();

   CameraPerspectiveModel getCameraPerspectiveModel();

   float[] computeProjectionMatrix(float[] var1, Float var2);

   Supplier<float[]> createProjectionMatrixSupplier(DoubleSupplier var1);
}
