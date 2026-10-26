package de.javagl.jgltf.model.structure;

import de.javagl.jgltf.model.AccessorModel;

class Alignment {
   static int computeAlignmentBytes(AccessorModel accessorModel) {
      return accessorModel.getComponentSizeInBytes();
   }

   static int computeAlignmentBytes(Iterable<? extends AccessorModel> accessorModels) {
      int alignmentBytes = 1;

      for (AccessorModel accessorModel : accessorModels) {
         alignmentBytes = computeLeastCommonMultiple(alignmentBytes, computeAlignmentBytes(accessorModel));
      }

      return alignmentBytes;
   }

   private static int computeCommonByteStride(Iterable<? extends AccessorModel> accessorModels) {
      int commonByteStride = 1;

      for (AccessorModel accessorModel : accessorModels) {
         int elementSize = accessorModel.getPaddedElementSizeInBytes();
         commonByteStride = Math.max(commonByteStride, elementSize);
         int byteStride = accessorModel.getByteStride();
         commonByteStride = Math.max(commonByteStride, byteStride);
      }

      return commonByteStride;
   }

   static int computeCommonVertexAttributeByteStride(Iterable<? extends AccessorModel> accessorModels) {
      int commonByteStride = computeCommonByteStride(accessorModels);
      return pad(commonByteStride, 4);
   }

   private static int computeGreatestCommonDivisor(int a, int b) {
      return b == 0 ? a : computeGreatestCommonDivisor(b, a % b);
   }

   private static int computeLeastCommonMultiple(int a, int b) {
      if (a == 0) {
         return b;
      } else {
         return b == 0 ? a : a * b / computeGreatestCommonDivisor(a, b);
      }
   }

   static int computePadding(int size, int alignment) {
      int remainder = size % alignment;
      return remainder > 0 ? alignment - remainder : 0;
   }

   static int pad(int size, int alignment) {
      return size + computePadding(size, alignment);
   }

   private Alignment() {
   }
}
