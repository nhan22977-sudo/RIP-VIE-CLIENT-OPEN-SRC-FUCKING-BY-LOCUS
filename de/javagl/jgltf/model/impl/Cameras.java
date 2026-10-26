package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.CameraOrthographicModel;
import de.javagl.jgltf.model.CameraPerspectiveModel;
import de.javagl.jgltf.model.MathUtils;
import de.javagl.jgltf.model.Utils;
import java.util.logging.Logger;

class Cameras {
   private static final Logger logger = Logger.getLogger(Cameras.class.getName());

   static float[] computeProjectionMatrix(CameraModel cameraModel, Float aspectRatio, float[] result) {
      float[] localResult = Utils.validate(result, 16);
      CameraPerspectiveModel cameraPerspective = cameraModel.getCameraPerspectiveModel();
      CameraOrthographicModel cameraOrthographic = cameraModel.getCameraOrthographicModel();
      if (cameraPerspective != null) {
         float fovRad = cameraPerspective.getYfov();
         float fovDeg = (float)Math.toDegrees(fovRad);
         float localAspectRatio = 1.0F;
         if (aspectRatio != null) {
            localAspectRatio = aspectRatio;
         } else if (cameraPerspective.getAspectRatio() != null) {
            localAspectRatio = cameraPerspective.getAspectRatio();
         }

         float zNear = cameraPerspective.getZnear();
         Float zFar = cameraPerspective.getZfar();
         if (zFar == null) {
            MathUtils.infinitePerspective4x4(fovDeg, localAspectRatio, zNear, localResult);
         } else {
            MathUtils.perspective4x4(fovDeg, localAspectRatio, zNear, zFar, localResult);
         }
      } else if (cameraOrthographic != null) {
         float xMag = cameraOrthographic.getXmag();
         float yMag = cameraOrthographic.getYmag();
         float zNear = cameraOrthographic.getZnear();
         float zFar = cameraOrthographic.getZfar();
         MathUtils.setIdentity4x4(localResult);
         localResult[0] = 1.0F / xMag;
         localResult[5] = 1.0F / yMag;
         localResult[10] = 2.0F / (zNear - zFar);
         localResult[14] = (zFar + zNear) / (zNear - zFar);
      } else {
         logger.severe("Invalid camera type: " + cameraModel);
         MathUtils.setIdentity4x4(localResult);
      }

      return localResult;
   }

   private Cameras() {
   }
}
