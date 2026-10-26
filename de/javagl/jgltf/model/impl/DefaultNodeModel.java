package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.MathUtils;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.Suppliers;
import de.javagl.jgltf.model.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class DefaultNodeModel extends AbstractNamedModelElement implements NodeModel {
   private static final ThreadLocal<float[]> TEMP_MATRIX_4x4_IN_LOCAL = ThreadLocal.withInitial(() -> new float[16]);
   private static final ThreadLocal<float[]> TEMP_MATRIX_4x4_IN_GLOBAL = ThreadLocal.withInitial(() -> new float[16]);
   private NodeModel parent;
   private final List<NodeModel> children;
   private final List<MeshModel> meshModels;
   private SkinModel skinModel;
   private CameraModel cameraModel;
   private float[] matrix;
   private float[] translation;
   private float[] rotation;
   private float[] scale;
   private float[] weights;

   public DefaultNodeModel() {
      this.children = new ArrayList<>();
      this.meshModels = new ArrayList<>();
   }

   public DefaultNodeModel(NodeModel other) {
      this.cameraModel = other.getCameraModel();
      this.children = new ArrayList<>();
      this.matrix = other.getMatrix();
      this.meshModels = new ArrayList<>();
      this.parent = other.getParent();
      this.rotation = other.getRotation();
      this.scale = other.getScale();
      this.skinModel = other.getSkinModel();
      this.translation = other.getTranslation();
      this.weights = other.getWeights();
   }

   public void setParent(DefaultNodeModel parent) {
      this.parent = parent;
   }

   public void addChild(DefaultNodeModel child) {
      Objects.requireNonNull(child, "The child may not be null");
      this.children.add(child);
      child.setParent(this);
   }

   public void addMeshModel(MeshModel meshModel) {
      Objects.requireNonNull(meshModel, "The meshModel may not be null");
      this.meshModels.add(meshModel);
   }

   public void setSkinModel(SkinModel skinModel) {
      this.skinModel = skinModel;
   }

   public void setCameraModel(CameraModel cameraModel) {
      this.cameraModel = cameraModel;
   }

   @Override
   public NodeModel getParent() {
      return this.parent;
   }

   @Override
   public List<NodeModel> getChildren() {
      return Collections.unmodifiableList(this.children);
   }

   @Override
   public List<MeshModel> getMeshModels() {
      return Collections.unmodifiableList(this.meshModels);
   }

   @Override
   public SkinModel getSkinModel() {
      return this.skinModel;
   }

   @Override
   public CameraModel getCameraModel() {
      return this.cameraModel;
   }

   @Override
   public void setMatrix(float[] matrix) {
      this.matrix = check(matrix, 16);
   }

   @Override
   public float[] getMatrix() {
      return this.matrix;
   }

   @Override
   public void setTranslation(float[] translation) {
      this.translation = check(translation, 3);
   }

   @Override
   public float[] getTranslation() {
      return this.translation;
   }

   @Override
   public void setRotation(float[] rotation) {
      this.rotation = check(rotation, 4);
   }

   @Override
   public float[] getRotation() {
      return this.rotation;
   }

   @Override
   public void setScale(float[] scale) {
      this.scale = check(scale, 3);
   }

   @Override
   public float[] getScale() {
      return this.scale;
   }

   @Override
   public void setWeights(float[] weights) {
      this.weights = weights;
   }

   @Override
   public float[] getWeights() {
      return this.weights;
   }

   @Override
   public float[] computeLocalTransform(float[] result) {
      return computeLocalTransform(this, result);
   }

   @Override
   public float[] computeGlobalTransform(float[] result) {
      return computeGlobalTransform(this, result);
   }

   @Override
   public Supplier<float[]> createGlobalTransformSupplier() {
      return Suppliers.createTransformSupplier(this, NodeModel::computeGlobalTransform);
   }

   @Override
   public Supplier<float[]> createLocalTransformSupplier() {
      return Suppliers.createTransformSupplier(this, NodeModel::computeLocalTransform);
   }

   public static float[] computeLocalTransform(NodeModel nodeModel, float[] result) {
      float[] localResult = Utils.validate(result, 16);
      if (nodeModel.getMatrix() != null) {
         float[] m = nodeModel.getMatrix();
         System.arraycopy(m, 0, localResult, 0, m.length);
         return localResult;
      }

      MathUtils.setIdentity4x4(localResult);
      if (nodeModel.getTranslation() != null) {
         float[] t = nodeModel.getTranslation();
         localResult[12] = t[0];
         localResult[13] = t[1];
         localResult[14] = t[2];
      }

      if (nodeModel.getRotation() != null) {
         float[] q = nodeModel.getRotation();
         float[] m = TEMP_MATRIX_4x4_IN_LOCAL.get();
         MathUtils.quaternionToMatrix4x4(q, m);
         MathUtils.mul4x4(localResult, m, localResult);
      }

      if (nodeModel.getScale() != null) {
         float[] s = nodeModel.getScale();
         float[] m = TEMP_MATRIX_4x4_IN_LOCAL.get();
         MathUtils.setIdentity4x4(m);
         m[0] = s[0];
         m[5] = s[1];
         m[10] = s[2];
         m[15] = 1.0F;
         MathUtils.mul4x4(localResult, m, localResult);
      }

      return localResult;
   }

   private static float[] computeGlobalTransform(NodeModel nodeModel, float[] result) {
      float[] localResult = Utils.validate(result, 16);
      float[] tempLocalTransform = TEMP_MATRIX_4x4_IN_GLOBAL.get();
      NodeModel currentNode = nodeModel;
      MathUtils.setIdentity4x4(localResult);

      while (currentNode != null) {
         currentNode.computeLocalTransform(tempLocalTransform);
         MathUtils.mul4x4(tempLocalTransform, localResult, localResult);
         currentNode = currentNode.getParent();
      }

      return localResult;
   }

   private static float[] check(float[] array, int expectedLength) {
      if (array == null) {
         return null;
      } else if (array.length != expectedLength) {
         throw new IllegalArgumentException("Expected " + expectedLength + " array elements, but found " + array.length);
      } else {
         return array;
      }
   }
}
