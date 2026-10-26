package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorFloatData;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.MathUtils;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class DefaultSkinModel extends AbstractNamedModelElement implements SkinModel {
   private float[] bindShapeMatrix = MathUtils.createIdentity4x4();
   private final List<NodeModel> joints = new ArrayList<>();
   private NodeModel skeleton;
   private AccessorModel inverseBindMatrices;

   public void setBindShapeMatrix(float[] bindShapeMatrix) {
      if (bindShapeMatrix == null) {
         this.bindShapeMatrix = MathUtils.createIdentity4x4();
      } else {
         this.bindShapeMatrix = (float[])bindShapeMatrix.clone();
      }
   }

   public void addJoint(NodeModel joint) {
      Objects.requireNonNull(joint, "The joint may not be null");
      this.joints.add(joint);
   }

   public void setSkeleton(NodeModel skeleton) {
      this.skeleton = skeleton;
   }

   public void setInverseBindMatrices(AccessorModel inverseBindMatrices) {
      this.inverseBindMatrices = Objects.requireNonNull(inverseBindMatrices, "The inverseBindMatrices may not be null");
   }

   @Override
   public float[] getBindShapeMatrix(float[] result) {
      float[] localResult = Utils.validate(result, 16);
      System.arraycopy(this.bindShapeMatrix, 0, localResult, 0, 16);
      return localResult;
   }

   @Override
   public List<NodeModel> getJoints() {
      return Collections.unmodifiableList(this.joints);
   }

   @Override
   public NodeModel getSkeleton() {
      return this.skeleton;
   }

   @Override
   public AccessorModel getInverseBindMatrices() {
      return this.inverseBindMatrices;
   }

   @Override
   public float[] getInverseBindMatrix(int index, float[] result) {
      float[] localResult = Utils.validate(result, 16);
      AccessorFloatData inverseBindMatricesData = AccessorDatas.createFloat(this.inverseBindMatrices);

      for (int j = 0; j < 16; j++) {
         localResult[j] = inverseBindMatricesData.get(index, j);
      }

      return localResult;
   }
}
