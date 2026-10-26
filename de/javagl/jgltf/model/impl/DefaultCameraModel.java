package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.CameraOrthographicModel;
import de.javagl.jgltf.model.CameraPerspectiveModel;
import de.javagl.jgltf.model.Suppliers;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

public final class DefaultCameraModel extends AbstractNamedModelElement implements CameraModel {
   private CameraOrthographicModel cameraOrthographicModel;
   private CameraPerspectiveModel cameraPerspectiveModel;

   public void setCameraOrthographicModel(CameraOrthographicModel cameraOrthographicModel) {
      this.cameraOrthographicModel = cameraOrthographicModel;
   }

   @Override
   public CameraOrthographicModel getCameraOrthographicModel() {
      return this.cameraOrthographicModel;
   }

   public void setCameraPerspectiveModel(CameraPerspectiveModel cameraPerspectiveModel) {
      this.cameraPerspectiveModel = cameraPerspectiveModel;
   }

   @Override
   public CameraPerspectiveModel getCameraPerspectiveModel() {
      return this.cameraPerspectiveModel;
   }

   @Override
   public float[] computeProjectionMatrix(float[] result, Float aspectRatio) {
      return Cameras.computeProjectionMatrix(this, aspectRatio, result);
   }

   @Override
   public Supplier<float[]> createProjectionMatrixSupplier(DoubleSupplier aspectRatioSupplier) {
      return Suppliers.createTransformSupplier(this, (c, t) -> {
         Float aspectRatio = null;
         if (aspectRatioSupplier != null) {
            aspectRatio = (float)aspectRatioSupplier.getAsDouble();
         }

         this.computeProjectionMatrix(t, aspectRatio);
      });
   }
}
