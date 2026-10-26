package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.Accessors;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.ElementType;

public final class DefaultAccessorModel extends AbstractNamedModelElement implements AccessorModel {
   private final int componentType;
   private boolean normalized;
   private int byteOffset;
   private BufferViewModel bufferViewModel;
   private final ElementType elementType;
   private final int count;
   private int byteStride;
   private AccessorData accessorData;
   private Number[] max;
   private Number[] min;

   public DefaultAccessorModel(int componentType, int count, ElementType elementType) {
      this.componentType = componentType;
      this.count = count;
      this.elementType = elementType;
      this.byteStride = elementType.getByteStride(componentType);
   }

   public void setBufferViewModel(BufferViewModel bufferViewModel) {
      this.bufferViewModel = bufferViewModel;
   }

   public void setByteOffset(int byteOffset) {
      this.byteOffset = byteOffset;
   }

   public void setByteStride(int byteStride) {
      this.byteStride = byteStride;
   }

   @Override
   public BufferViewModel getBufferViewModel() {
      return this.bufferViewModel;
   }

   @Override
   public int getComponentType() {
      return this.componentType;
   }

   @Override
   public Class<?> getComponentDataType() {
      return Accessors.getDataTypeForAccessorComponentType(this.getComponentType());
   }

   @Override
   public boolean isNormalized() {
      return this.normalized;
   }

   public void setNormalized(boolean normalized) {
      this.normalized = normalized;
   }

   @Override
   public int getComponentSizeInBytes() {
      return Accessors.getNumBytesForAccessorComponentType(this.componentType);
   }

   @Override
   public int getElementSizeInBytes() {
      return this.elementType.getNumComponents() * this.getComponentSizeInBytes();
   }

   @Override
   public int getPaddedElementSizeInBytes() {
      return this.elementType.getByteStride(this.componentType);
   }

   @Override
   public int getByteOffset() {
      return this.byteOffset;
   }

   @Override
   public int getCount() {
      return this.count;
   }

   @Override
   public ElementType getElementType() {
      return this.elementType;
   }

   @Override
   public int getByteStride() {
      return this.byteStride;
   }

   public void setAccessorData(AccessorData accessorData) {
      this.accessorData = accessorData;
   }

   @Override
   public AccessorData getAccessorData() {
      return this.accessorData;
   }

   @Override
   public Number[] getMin() {
      if (this.min == null) {
         this.min = AccessorDatas.computeMin(this.getAccessorData());
      }

      return (Number[])this.min.clone();
   }

   @Override
   public Number[] getMax() {
      if (this.max == null) {
         this.max = AccessorDatas.computeMax(this.getAccessorData());
      }

      return (Number[])this.max.clone();
   }
}
