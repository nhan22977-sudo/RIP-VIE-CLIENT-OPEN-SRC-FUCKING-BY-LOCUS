package de.javagl.jgltf.model;

import java.util.Map;
import java.util.logging.Logger;

class BoundingBoxComputer {
   private static final Logger logger = Logger.getLogger(BoundingBoxComputer.class.getName());
   private final GltfModel gltfModel;

   BoundingBoxComputer(GltfModel gltfModel) {
      this.gltfModel = gltfModel;
   }

   BoundingBox compute() {
      BoundingBox boundingBox = new BoundingBox();

      for (SceneModel sceneModel : this.gltfModel.getSceneModels()) {
         float[] rootTransform = MathUtils.createIdentity4x4();
         this.computeSceneBoundingBox(sceneModel, rootTransform, boundingBox);
      }

      return boundingBox;
   }

   private BoundingBox computeSceneBoundingBox(SceneModel sceneModel, float[] transform, BoundingBox boundingBox) {
      BoundingBox localResult = boundingBox;
      if (localResult == null) {
         localResult = new BoundingBox();
      }

      for (NodeModel nodeModel : sceneModel.getNodeModels()) {
         this.computeNodeBoundingBox(nodeModel, transform, localResult);
      }

      return localResult;
   }

   private BoundingBox computeNodeBoundingBox(NodeModel nodeModel, float[] parentTransform, BoundingBox boundingBox) {
      BoundingBox result = boundingBox;
      if (result == null) {
         result = new BoundingBox();
      }

      float[] localTransform = nodeModel.computeLocalTransform(null);
      float[] transform = new float[16];
      MathUtils.mul4x4(parentTransform, localTransform, transform);

      for (MeshModel meshModel : nodeModel.getMeshModels()) {
         BoundingBox meshBoundingBox = this.computeMeshBoundingBox(meshModel, transform, result);
         result.combine(meshBoundingBox);
      }

      for (NodeModel child : nodeModel.getChildren()) {
         this.computeNodeBoundingBox(child, transform, result);
      }

      return result;
   }

   private BoundingBox computeMeshBoundingBox(MeshModel meshModel, float[] transform, BoundingBox boundingBox) {
      BoundingBox result = boundingBox;
      if (result == null) {
         result = new BoundingBox();
      }

      for (MeshPrimitiveModel meshPrimitiveModel : meshModel.getMeshPrimitiveModels()) {
         BoundingBox meshPrimitiveBoundingBox = this.computeBoundingBox(meshPrimitiveModel, transform);
         if (meshPrimitiveBoundingBox != null) {
            result.combine(meshPrimitiveBoundingBox);
         }
      }

      return result;
   }

   private BoundingBox computeBoundingBox(MeshPrimitiveModel meshPrimitiveModel, float[] transform) {
      Map<String, AccessorModel> attributes = meshPrimitiveModel.getAttributes();
      String positionsAttributeName = "POSITION";
      AccessorModel accessorModel = attributes.get(positionsAttributeName);
      if (accessorModel == null) {
         return null;
      }

      ElementType accessorType = accessorModel.getElementType();
      int numComponents = accessorType.getNumComponents();
      if (numComponents < 3) {
         logger.warning(
            "Mesh primitive " + positionsAttributeName + " attribute refers to an accessor with type " + accessorType + " - expected \"VEC3\" or \"VEC4\""
         );
         return null;
      }

      Class<?> componentDataType = accessorModel.getComponentDataType();
      if (!componentDataType.equals(float.class)) {
         logger.warning(
            "Mesh primitive "
               + positionsAttributeName
               + " attribute refers to an accessor with component type "
               + GltfConstants.stringFor(accessorModel.getComponentType())
               + " - expected GL_FLOAT"
         );
      }

      AccessorData accessorData = accessorModel.getAccessorData();
      AccessorFloatData accessorFloatData = (AccessorFloatData)accessorData;
      float[] point = new float[3];
      float[] transformedPoint;
      if (transform != null) {
         transformedPoint = new float[3];
      } else {
         transformedPoint = point;
      }

      BoundingBox boundingBox = new BoundingBox();

      for (int e = 0; e < accessorData.getNumElements(); e++) {
         for (int c = 0; c < 3; c++) {
            point[c] = accessorFloatData.get(e, c);
         }

         if (transform != null) {
            MathUtils.transformPoint3D(transform, point, transformedPoint);
         }

         boundingBox.combine(transformedPoint[0], transformedPoint[1], transformedPoint[2]);
      }

      return boundingBox;
   }
}
