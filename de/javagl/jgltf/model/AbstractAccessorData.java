package de.javagl.jgltf.model;

import java.nio.ByteBuffer;
import java.util.Objects;

abstract class AbstractAccessorData implements AccessorData {
   private final Class<?> componentType;
   private final ByteBuffer bufferViewByteBuffer;
   private final int byteOffset;
   private final int numElements;
   private final ElementType elementType;
   private final int numBytesPerComponent;
   private final int byteStridePerElement;

   AbstractAccessorData(
      int accessorComponentType,
      Class<?> componentType,
      ByteBuffer bufferViewByteBuffer,
      int byteOffset,
      int numElements,
      ElementType elementType,
      int numBytesPerComponent,
      Integer byteStride
   ) {
      Objects.requireNonNull(bufferViewByteBuffer, "The bufferViewByteBuffer is null");
      this.componentType = componentType;
      this.bufferViewByteBuffer = bufferViewByteBuffer;
      this.byteOffset = byteOffset;
      this.numElements = numElements;
      this.elementType = elementType;
      this.numBytesPerComponent = numBytesPerComponent;
      if (byteStride != null && byteStride != 0) {
         this.byteStridePerElement = byteStride;
      } else {
         this.byteStridePerElement = elementType.getByteStride(accessorComponentType);
      }
   }

   @Override
   public final Class<?> getComponentType() {
      return this.componentType;
   }

   @Override
   public final int getNumElements() {
      return this.numElements;
   }

   @Override
   public final int getNumComponentsPerElement() {
      return this.elementType.getNumComponents();
   }

   @Override
   public final int getTotalNumComponents() {
      return this.numElements * this.getNumComponentsPerElement();
   }

   protected final int getByteIndex(int elementIndex, int componentIndex) {
      int byteIndex = this.byteOffset + elementIndex * this.byteStridePerElement;
      if (this.componentType == byte.class) {
         if (this.elementType == ElementType.MAT2) {
            int columnIndex = componentIndex / 2;
            int rowIndex = componentIndex % 2;
            byteIndex += columnIndex * 4 + rowIndex;
         } else if (this.elementType == ElementType.MAT3) {
            int columnIndex = componentIndex / 3;
            int rowIndex = componentIndex % 3;
            byteIndex += columnIndex * 4 + rowIndex;
         } else {
            byteIndex += componentIndex * this.numBytesPerComponent;
         }
      } else if (this.componentType == short.class) {
         if (this.elementType == ElementType.MAT3) {
            int columnIndex = componentIndex / 3;
            int rowIndex = componentIndex % 3;
            byteIndex += columnIndex * 8 + rowIndex * 2;
         } else {
            byteIndex += componentIndex * this.numBytesPerComponent;
         }
      } else {
         byteIndex += componentIndex * this.numBytesPerComponent;
      }

      return byteIndex;
   }

   protected final ByteBuffer getBufferViewByteBuffer() {
      return this.bufferViewByteBuffer;
   }

   protected final int getByteStridePerElement() {
      return this.byteStridePerElement;
   }

   protected final int getNumBytesPerComponent() {
      return this.numBytesPerComponent;
   }
}
