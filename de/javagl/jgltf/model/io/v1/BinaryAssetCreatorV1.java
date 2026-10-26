package de.javagl.jgltf.model.io.v1;

import de.javagl.jgltf.impl.v1.Buffer;
import de.javagl.jgltf.impl.v1.BufferView;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.v1.BinaryGltfV1;
import de.javagl.jgltf.model.v1.GltfCreatorV1;
import de.javagl.jgltf.model.v1.GltfExtensionsV1;
import de.javagl.jgltf.model.v1.GltfIds;
import de.javagl.jgltf.model.v1.GltfModelV1;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

final class BinaryAssetCreatorV1 {
   GltfAssetV1 create(GltfModelV1 gltfModel) {
      GlTF outputGltf = GltfCreatorV1.create(gltfModel);
      int binaryGltfBufferSize = computeBinaryGltfBufferSize(gltfModel);
      ByteBuffer binaryGltfByteBuffer = Buffers.create(binaryGltfBufferSize);
      GltfExtensionsV1.addExtensionUsed(outputGltf, BinaryGltfV1.getBinaryGltfExtensionName());
      Buffer binaryGltfBuffer = new Buffer();
      binaryGltfBuffer.setType("arraybuffer");
      binaryGltfBuffer.setUri(BinaryGltfV1.getBinaryGltfBufferId() + ".bin");
      binaryGltfBuffer.setByteLength(binaryGltfBufferSize);
      Map<String, Buffer> newBuffers = Collections.singletonMap(BinaryGltfV1.getBinaryGltfBufferId(), binaryGltfBuffer);
      Map<String, Buffer> oldBuffers = copy(outputGltf.getBuffers());
      Map<String, Image> oldImages = copy(outputGltf.getImages());
      Map<String, Shader> oldShaders = copy(outputGltf.getShaders());
      Map<String, BufferModel> bufferIdToBuffer = GltfUtilsV1.createMap(oldBuffers, gltfModel.getBufferModels());
      Map<String, ImageModel> imageIdToImage = GltfUtilsV1.createMap(oldImages, gltfModel.getImageModels());
      Map<String, ShaderModel> shaderIdToShader = GltfUtilsV1.createMap(oldShaders, gltfModel.getShaderModels());
      Map<String, Integer> bufferOffsets = concatBuffers(oldBuffers.keySet(), idx -> bufferIdToBuffer.get(idx).getBufferData(), binaryGltfByteBuffer);
      Map<String, Integer> imageOffsets = concatBuffers(oldImages.keySet(), idx -> {
         ImageModel image = imageIdToImage.get(idx);
         return image.getBufferViewModel() != null ? ByteBuffer.allocate(0) : image.getImageData();
      }, binaryGltfByteBuffer);
      Map<String, Integer> shaderOffsets = concatBuffers(oldShaders.keySet(), idx -> {
         ShaderModel shader = shaderIdToShader.get(idx);
         return shader.getBufferViewModel() != null ? ByteBuffer.allocate(0) : shader.getShaderData();
      }, binaryGltfByteBuffer);
      ((java.nio.Buffer)binaryGltfByteBuffer).position(0);
      Map<String, BufferView> oldBufferViews = copy(outputGltf.getBufferViews());
      Map<String, BufferView> newBufferViews = new LinkedHashMap<>();

      for (Entry<String, BufferView> oldEntry : oldBufferViews.entrySet()) {
         String id = oldEntry.getKey();
         BufferView oldBufferView = oldEntry.getValue();
         BufferView newBufferView = GltfUtilsV1.copy(oldBufferView);
         newBufferView.setBuffer(BinaryGltfV1.getBinaryGltfBufferId());
         String oldBufferId = oldBufferView.getBuffer();
         int oldByteOffset = oldBufferView.getByteOffset();
         int bufferOffset = bufferOffsets.get(oldBufferId);
         int newByteOffset = oldByteOffset + bufferOffset;
         newBufferView.setByteOffset(newByteOffset);
         newBufferViews.put(id, newBufferView);
      }

      Map<String, Image> newImages = new LinkedHashMap<>();

      for (Entry<String, Image> oldEntry : oldImages.entrySet()) {
         String id = oldEntry.getKey();
         Image oldImage = oldEntry.getValue();
         Image newImage = GltfUtilsV1.copy(oldImage);
         if (!BinaryGltfV1.hasBinaryGltfExtension(oldImage)) {
            ByteBuffer imageData = imageIdToImage.get(id).getImageData();
            int byteLength = imageData.capacity();
            int byteOffset = imageOffsets.get(id);
            BufferView imageBufferView = new BufferView();
            imageBufferView.setBuffer(BinaryGltfV1.getBinaryGltfBufferId());
            imageBufferView.setByteOffset(byteOffset);
            imageBufferView.setByteLength(byteLength);
            String generatedBufferViewId = GltfIds.generateId("bufferView_for_image_" + id, oldBufferViews.keySet());
            newBufferViews.put(generatedBufferViewId, imageBufferView);
            BinaryGltfV1.setBinaryGltfBufferViewId(newImage, generatedBufferViewId);
            newImage.setUri("image_" + id);
            BinaryGltfV1.setBinaryGltfImageProperties(newImage, imageData);
         } else {
            List<BufferViewModel> bufferViewModels = gltfModel.getBufferViewModels();
            ImageModel imageModel = imageIdToImage.get(id);
            BufferViewModel imageBufferViewModel = imageModel.getBufferViewModel();
            int index = bufferViewModels.indexOf(imageBufferViewModel);
            String newId = "bufferView_" + index;
            BinaryGltfV1.setBinaryGltfBufferViewId(newImage, newId);
         }

         newImages.put(id, newImage);
      }

      Map<String, Shader> newShaders = new LinkedHashMap<>();

      for (Entry<String, Shader> oldEntry : oldShaders.entrySet()) {
         String id = oldEntry.getKey();
         Shader oldShader = oldEntry.getValue();
         Shader newShader = GltfUtilsV1.copy(oldShader);
         if (!BinaryGltfV1.hasBinaryGltfExtension(oldShader)) {
            ByteBuffer shaderData = shaderIdToShader.get(id).getShaderData();
            int byteLength = shaderData.capacity();
            int byteOffset = shaderOffsets.get(id);
            BufferView shaderBufferView = new BufferView();
            shaderBufferView.setBuffer(BinaryGltfV1.getBinaryGltfBufferId());
            shaderBufferView.setByteOffset(byteOffset);
            shaderBufferView.setByteLength(byteLength);
            String generatedBufferViewId = GltfIds.generateId("bufferView_for_shader_" + id, oldBufferViews.keySet());
            newBufferViews.put(generatedBufferViewId, shaderBufferView);
            BinaryGltfV1.setBinaryGltfBufferViewId(newShader, generatedBufferViewId);
         } else {
            List<BufferViewModel> bufferViewModels = gltfModel.getBufferViewModels();
            ShaderModel shaderModel = shaderIdToShader.get(id);
            BufferViewModel shaderBufferViewModel = shaderModel.getBufferViewModel();
            int index = bufferViewModels.indexOf(shaderBufferViewModel);
            String newId = "bufferView_" + index;
            BinaryGltfV1.setBinaryGltfBufferViewId(newShader, newId);
         }

         newShaders.put(id, newShader);
      }

      if (!newBuffers.isEmpty()) {
         outputGltf.setBuffers(newBuffers);
      }

      if (!newImages.isEmpty()) {
         outputGltf.setImages(newImages);
      }

      if (!newShaders.isEmpty()) {
         outputGltf.setShaders(newShaders);
      }

      if (!newBufferViews.isEmpty()) {
         outputGltf.setBufferViews(newBufferViews);
      }

      return new GltfAssetV1(outputGltf, binaryGltfByteBuffer);
   }

   private static int computeBinaryGltfBufferSize(GltfModelV1 gltfModel) {
      int binaryGltfBufferSize = 0;

      for (BufferModel bufferModel : gltfModel.getBufferModels()) {
         ByteBuffer bufferData = bufferModel.getBufferData();
         binaryGltfBufferSize += bufferData.capacity();
      }

      for (ImageModel imageModel : gltfModel.getImageModels()) {
         if (imageModel.getBufferViewModel() == null) {
            ByteBuffer imageData = imageModel.getImageData();
            binaryGltfBufferSize += imageData.capacity();
         }
      }

      for (ShaderModel shaderModel : gltfModel.getShaderModels()) {
         if (shaderModel.getBufferViewModel() == null) {
            ByteBuffer shaderData = shaderModel.getShaderData();
            binaryGltfBufferSize += shaderData.capacity();
         }
      }

      return binaryGltfBufferSize;
   }

   private static <K> Map<K, Integer> concatBuffers(Iterable<K> keys, Function<? super K, ? extends ByteBuffer> keyToByteBuffer, ByteBuffer targetBuffer) {
      Map<K, Integer> offsets = new LinkedHashMap<>();

      for (K key : keys) {
         ByteBuffer oldByteBuffer = keyToByteBuffer.apply(key);
         int offset = targetBuffer.position();
         offsets.put(key, offset);
         targetBuffer.put(oldByteBuffer.slice());
      }

      return offsets;
   }

   private static <K, V> Map<K, V> copy(Map<K, V> map) {
      return map == null ? Collections.emptyMap() : new LinkedHashMap<>(map);
   }
}
