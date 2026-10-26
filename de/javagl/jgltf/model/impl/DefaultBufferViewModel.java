package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.io.Buffers;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public final class DefaultBufferViewModel extends AbstractNamedModelElement implements BufferViewModel {
   private BufferModel bufferModel;
   private int byteOffset = 0;
   private int byteLength = 0;
   private Integer byteStride;
   private final Integer target;
   private Consumer<? super ByteBuffer> sparseSubstitutionCallback;
   private boolean sparseSubstitutionApplied;

   public DefaultBufferViewModel(Integer target) {
      this.target = target;
   }

   public void setSparseSubstitutionCallback(Consumer<? super ByteBuffer> sparseSubstitutionCallback) {
      this.sparseSubstitutionCallback = sparseSubstitutionCallback;
   }

   public void setBufferModel(BufferModel bufferModel) {
      this.bufferModel = bufferModel;
   }

   public void setByteOffset(int byteOffset) {
      this.byteOffset = byteOffset;
   }

   public void setByteLength(int byteLength) {
      this.byteLength = byteLength;
   }

   public void setByteStride(Integer byteStride) {
      this.byteStride = byteStride;
   }

   @Override
   public ByteBuffer getBufferViewData() {
      ByteBuffer bufferData = this.bufferModel.getBufferData();
      ByteBuffer bufferViewData = Buffers.createSlice(bufferData, this.getByteOffset(), this.getByteLength());
      if (this.sparseSubstitutionCallback != null && !this.sparseSubstitutionApplied) {
         this.sparseSubstitutionCallback.accept(bufferViewData);
         this.sparseSubstitutionApplied = true;
      }

      return bufferViewData;
   }

   @Override
   public BufferModel getBufferModel() {
      return this.bufferModel;
   }

   @Override
   public int getByteOffset() {
      return this.byteOffset;
   }

   @Override
   public int getByteLength() {
      return this.byteLength;
   }

   @Override
   public Integer getByteStride() {
      return this.byteStride;
   }

   @Override
   public Integer getTarget() {
      return this.target;
   }
}
