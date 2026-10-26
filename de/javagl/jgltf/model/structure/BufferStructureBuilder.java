package de.javagl.jgltf.model.structure;

import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.Accessors;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.ElementType;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferViewModel;
import de.javagl.jgltf.model.io.Buffers;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public final class BufferStructureBuilder {
   private static final Logger logger = Logger.getLogger(BufferStructureBuilder.class.getName());
   private final BufferStructure bufferStructure = new BufferStructure();
   private final List<DefaultAccessorModel> currentAccessorModels = new ArrayList<>();
   private final List<DefaultBufferViewModel> currentBufferViewModels = new ArrayList<>();
   private final Map<DefaultBufferViewModel, ByteBuffer> imageBufferViewDataMap = new LinkedHashMap<>();

   public int getNumAccessorModels() {
      return this.bufferStructure.getAccessorModels().size();
   }

   public int getNumBufferViewModels() {
      return this.bufferStructure.getBufferViewModels().size();
   }

   public int getNumBufferModels() {
      return this.bufferStructure.getBufferModels().size();
   }

   public int getNumCurrentAccessorModels() {
      return this.currentAccessorModels.size();
   }

   public int getNumCurrentBufferViewModels() {
      return this.currentBufferViewModels.size();
   }

   public AccessorModel createAccessorModel(String idPrefix, float[] data, String type) {
      ElementType elementType = ElementType.valueOf(type);
      int numComponents = elementType.getNumComponents();
      if (data.length % numComponents != 0) {
         throw new IllegalArgumentException("Invalid data for type " + type + ". The data.length is not divisble by " + numComponents);
      }

      int componentType = 5126;
      ByteBuffer byteBuffer = Buffers.createByteBufferFrom(FloatBuffer.wrap(data));
      return this.createAccessorModel(idPrefix, componentType, type, byteBuffer);
   }

   public AccessorModel createAccessorModel(String idPrefix, int[] data, String type) {
      ElementType elementType = ElementType.valueOf(type);
      int numComponents = elementType.getNumComponents();
      if (data.length % numComponents != 0) {
         throw new IllegalArgumentException("Invalid data for type " + type + ". The data.length is not divisble by " + numComponents);
      }

      int componentType = 5125;
      ByteBuffer byteBuffer = Buffers.createByteBufferFrom(IntBuffer.wrap(data));
      return this.createAccessorModel(idPrefix, componentType, type, byteBuffer);
   }

   public AccessorModel createAccessorModel(String idPrefix, short[] data, String type) {
      ElementType elementType = ElementType.valueOf(type);
      int numComponents = elementType.getNumComponents();
      if (data.length % numComponents != 0) {
         throw new IllegalArgumentException("Invalid data for type " + type + ". The data.length is not divisble by " + numComponents);
      }

      int componentType = 5123;
      ByteBuffer byteBuffer = Buffers.createByteBufferFrom(ShortBuffer.wrap(data));
      return this.createAccessorModel(idPrefix, componentType, type, byteBuffer);
   }

   public AccessorModel createAccessorModel(String idPrefix, byte[] data, String type) {
      ElementType elementType = ElementType.valueOf(type);
      int numComponents = elementType.getNumComponents();
      if (data.length % numComponents != 0) {
         throw new IllegalArgumentException("Invalid data for type " + type + ". The data.length is not divisble by " + numComponents);
      }

      int componentType = 5121;
      ByteBuffer byteBuffer = ByteBuffer.wrap(data);
      return this.createAccessorModel(idPrefix, componentType, type, byteBuffer);
   }

   public AccessorModel createAccessorModel(String idPrefix, int componentType, String type, ByteBuffer byteBuffer) {
      boolean normalized = false;
      ElementType elementType = ElementType.valueOf(type);
      int numBytesPerElement = elementType.getByteStride(componentType);
      if (byteBuffer.capacity() % numBytesPerElement != 0) {
         throw new IllegalArgumentException(
            "Invalid data for type "
               + type
               + " accessor with "
               + Accessors.getDataTypeForAccessorComponentType(componentType)
               + " components: The data length is "
               + byteBuffer.capacity()
               + " which is not divisble by "
               + numBytesPerElement
         );
      }

      int count = byteBuffer.capacity() / numBytesPerElement;
      DefaultAccessorModel accessorModel = new DefaultAccessorModel(componentType, count, elementType);
      accessorModel.setNormalized(normalized);
      accessorModel.setAccessorData(AccessorDatas.create(accessorModel, byteBuffer));
      this.addAccessorModel(idPrefix, accessorModel);
      return accessorModel;
   }

   public void addAccessorModel(String idPrefix, DefaultAccessorModel accessorModel) {
      BufferViewModel bufferViewModel = accessorModel.getBufferViewModel();
      if (bufferViewModel != null) {
         throw new GltfException("The accessor already contains a buffer view");
      }

      this.bufferStructure.addAccessorModel(accessorModel, idPrefix);
      this.currentAccessorModels.add(accessorModel);
   }

   public BufferViewModel createArrayBufferViewModel(String idPrefix) {
      return this.createBufferViewModel(idPrefix, 34962);
   }

   public BufferViewModel createArrayElementBufferViewModel(String idPrefix) {
      return this.createBufferViewModel(idPrefix, 34963);
   }

   public BufferViewModel createBufferViewModel(String idPrefix, Integer target) {
      DefaultBufferViewModel bufferViewModel = new DefaultBufferViewModel(target);
      this.addBufferViewModel(idPrefix, bufferViewModel);
      return bufferViewModel;
   }

   public void addBufferViewModel(String idPrefix, DefaultBufferViewModel bufferViewModel) {
      for (DefaultAccessorModel accessorModel : this.currentAccessorModels) {
         accessorModel.setBufferViewModel(bufferViewModel);
      }

      this.bufferStructure.addBufferViewModel(bufferViewModel, idPrefix, this.currentAccessorModels);
      this.currentBufferViewModels.add(bufferViewModel);
      this.currentAccessorModels.clear();
   }

   public BufferViewModel createImageBufferViewModel(String idPrefix, ByteBuffer imageData) {
      Objects.requireNonNull(imageData, "The imageData may not be null");
      if (!this.currentAccessorModels.isEmpty()) {
         throw new IllegalStateException("Cannot create an image buffer view model with " + this.currentAccessorModels.size() + " pending accessors");
      }

      DefaultBufferViewModel bufferViewModel = new DefaultBufferViewModel(null);
      bufferViewModel.setByteLength(imageData.capacity());
      this.imageBufferViewDataMap.put(bufferViewModel, imageData);
      this.bufferStructure.addBufferViewModel(bufferViewModel, idPrefix, Collections.emptyList());
      this.currentBufferViewModels.add(bufferViewModel);
      return bufferViewModel;
   }

   public BufferModel createBufferModel(String idPrefix, String uri) {
      if (this.currentBufferViewModels.isEmpty()) {
         return null;
      }

      DefaultBufferModel bufferModel = new DefaultBufferModel();
      bufferModel.setUri(uri);
      this.addBufferModel(idPrefix, bufferModel);
      return bufferModel;
   }

   public void addBufferModel(String idPrefix, DefaultBufferModel bufferModel) {
      for (DefaultBufferViewModel bufferViewModel : this.currentBufferViewModels) {
         bufferViewModel.setBufferModel(bufferModel);
      }

      this.bufferStructure.addBufferModel(bufferModel, idPrefix, this.currentBufferViewModels);
      this.currentBufferViewModels.clear();
   }

   public BufferStructure build() {
      if (!this.currentBufferViewModels.isEmpty()) {
         throw new IllegalStateException(
            "There are "
               + this.currentBufferViewModels.size()
               + " buffer views for which no buffer has been created yet. The 'createBufferModel' method must be called before building the buffer structure"
         );
      }

      this.buildDefault();
      return this.bufferStructure;
   }

   private void buildDefault() {
      for (DefaultBufferModel bufferModel : this.bufferStructure.getBufferModels()) {
         this.processBufferModel(bufferModel);
      }
   }

   private void processBufferModel(DefaultBufferModel bufferModel) {
      Map<DefaultAccessorModel, ByteBuffer> rawAccessorModelByteBuffers = new LinkedHashMap<>();

      for (DefaultAccessorModel accessorModel : this.bufferStructure.getAccessorModels()) {
         AccessorData accessorData = accessorModel.getAccessorData();
         ByteBuffer byteBuffer = accessorData.createByteBuffer();
         rawAccessorModelByteBuffers.put(accessorModel, byteBuffer);
      }

      List<ByteBuffer> bufferElements = new ArrayList<>();
      List<DefaultBufferViewModel> bufferViewModels = this.bufferStructure.getBufferViewModels(bufferModel);
      int accumulatedBufferBytes = 0;

      for (DefaultBufferViewModel bufferViewModel : bufferViewModels) {
         List<DefaultAccessorModel> accessorModels = this.bufferStructure.getAccessorModels(bufferViewModel);
         int bufferViewAlignmnentBytes = Alignment.computeAlignmentBytes(accessorModels);
         int paddingBytesForBuffer = Alignment.computePadding(accumulatedBufferBytes, bufferViewAlignmnentBytes);
         this.bufferStructure.addPaddingByteIndices(bufferModel, accumulatedBufferBytes, paddingBytesForBuffer);
         accumulatedBufferBytes += paddingBytesForBuffer;
         bufferElements.add(ByteBuffer.allocate(paddingBytesForBuffer));
         bufferViewModel.setByteOffset(accumulatedBufferBytes);
         Integer commonByteStride = null;
         Integer target = bufferViewModel.getTarget();
         boolean targetIsVertexAttribute = Objects.equals(34962, target);
         if (targetIsVertexAttribute) {
            commonByteStride = Alignment.computeCommonVertexAttributeByteStride(accessorModels);

            for (DefaultAccessorModel accessorModel : accessorModels) {
               int oldByteStride = accessorModel.getByteStride();
               if (oldByteStride != commonByteStride) {
                  accessorModel.setByteStride(commonByteStride);
                  bufferViewModel.setByteStride(commonByteStride);
               }
            }

            if (accessorModels.size() > 1) {
               bufferViewModel.setByteStride(commonByteStride);
            }
         }

         if (accessorModels.isEmpty()) {
            ByteBuffer bufferViewData = this.imageBufferViewDataMap.get(bufferViewModel);
            bufferViewModel.setByteLength(bufferViewData.capacity());
            accumulatedBufferBytes += bufferViewData.capacity();
            bufferElements.add(bufferViewData);
         } else {
            int accumulatedBufferViewBytes = 0;

            for (DefaultAccessorModel accessorModel : accessorModels) {
               int accessorAlignmentBytes = Alignment.computeAlignmentBytes(accessorModel);
               int paddingBytesForBufferView = Alignment.computePadding(accumulatedBufferViewBytes, accessorAlignmentBytes);
               if (paddingBytesForBufferView != 0) {
                  logger.warning("Inserting " + paddingBytesForBufferView + " padding bytes for buffer view, due to accessor " + accessorModel);
               }

               this.bufferStructure.addPaddingByteIndices(bufferModel, accumulatedBufferBytes, paddingBytesForBufferView);
               accumulatedBufferViewBytes += paddingBytesForBufferView;
               accessorModel.setByteOffset(accumulatedBufferViewBytes);
               int targetByteStride = accessorModel.getPaddedElementSizeInBytes();
               if (commonByteStride == null) {
                  accessorModel.setByteStride(targetByteStride);
               } else {
                  targetByteStride = commonByteStride;
               }

               ByteBuffer rawAccessorByteBuffer = rawAccessorModelByteBuffers.get(accessorModel);
               int count = accessorModel.getCount();
               ElementType elementType = accessorModel.getElementType();
               int componentType = accessorModel.getComponentType();
               ByteBuffer accessorByteBuffer = applyPadding(rawAccessorByteBuffer, count, elementType, componentType, targetByteStride);
               accumulatedBufferViewBytes += accessorByteBuffer.capacity();
               accumulatedBufferBytes += paddingBytesForBufferView;
               bufferElements.add(ByteBuffer.allocate(paddingBytesForBufferView));
               accumulatedBufferBytes += accessorByteBuffer.capacity();
               bufferElements.add(accessorByteBuffer);
            }

            bufferViewModel.setByteLength(accumulatedBufferViewBytes);
         }
      }

      this.validatePadding(bufferModel);
      ByteBuffer bufferData = Buffers.concat(bufferElements);
      bufferModel.setBufferData(bufferData);
   }

   private static ByteBuffer applyPadding(ByteBuffer packedByteBuffer, int count, ElementType elementType, int componentType, int byteStride) {
      ByteBuffer newByteBuffer = ByteBuffer.allocate(count * byteStride);
      int numComponents = elementType.getNumComponents();
      int numBytesPerComponent = Accessors.getNumBytesForAccessorComponentType(componentType);
      int sourceIndex = 0;
      int targetIndex = 0;
      int padddingForByteStride = byteStride - elementType.getByteStride(componentType);

      for (int i = 0; i < count; i++) {
         for (int c = 0; c < numComponents; c++) {
            for (int b = 0; b < numBytesPerComponent; b++) {
               byte value = packedByteBuffer.get(sourceIndex);
               newByteBuffer.put(targetIndex, value);
               sourceIndex++;
               targetIndex++;
            }

            int padding = computePaddingBytesAfterComponent(elementType, componentType, c);
            targetIndex += padding;
         }

         targetIndex += padddingForByteStride;
      }

      return newByteBuffer;
   }

   private static int computePaddingBytesAfterComponent(ElementType elementType, int componentType, int componentIndex) {
      int n = Accessors.getNumBytesForAccessorComponentType(componentType);
      if (n == 1) {
         if (elementType == ElementType.MAT2) {
            if (componentIndex == 1) {
               return 2;
            }

            if (componentIndex == 3) {
               return 2;
            }
         }

         if (elementType == ElementType.MAT3) {
            if (componentIndex == 2) {
               return 1;
            }

            if (componentIndex == 5) {
               return 1;
            }

            if (componentIndex == 9) {
               return 1;
            }
         }
      }

      if (n == 2 && elementType == ElementType.MAT3) {
         if (componentIndex == 2) {
            return 2;
         }

         if (componentIndex == 5) {
            return 2;
         }

         if (componentIndex == 9) {
            return 2;
         }
      }

      return 0;
   }

   private void validatePadding(BufferModel bufferModel) {
      for (BufferViewModel bufferViewModel : this.bufferStructure.getBufferViewModels()) {
         for (AccessorModel accessorModel : this.bufferStructure.getAccessorModels(bufferViewModel)) {
            this.validatePadding(bufferViewModel, accessorModel);
         }
      }
   }

   private void validatePadding(BufferViewModel bufferViewModel, AccessorModel accessorModel) {
      int alignmentBytes = Alignment.computeAlignmentBytes(accessorModel);
      int bufferViewByteOffset = bufferViewModel.getByteOffset();
      int accessorByteOffset = accessorModel.getByteOffset();
      int totalByteOffset = bufferViewByteOffset + accessorByteOffset;
      if (accessorByteOffset % alignmentBytes != 0) {
         logger.severe("Error: accessor.byteOffset is " + accessorByteOffset + " for alignment " + alignmentBytes + " in accessor " + accessorModel);
      }

      if (totalByteOffset % alignmentBytes != 0) {
         logger.severe(
            "Error: bufferView.byteOffset+accessor.byteOffset is " + totalByteOffset + " for alignment " + alignmentBytes + " in accessor " + accessorModel
         );
      }
   }
}
