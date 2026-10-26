package de.javagl.jgltf.model.io.v2;

import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.impl.UriStrings;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.v2.GltfCreatorV2;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class DirectAssetCreatorV2 {
   private GltfAssetV2 gltfAsset;
   private Set<String> existingBufferUriStrings;
   private Set<String> existingImageUriStrings;

   public GltfAssetV2 createDefault(GltfModel gltfModel) {
      GlTF outputGltf = GltfCreatorV2.create(gltfModel);
      List<Buffer> buffers = Optionals.of(outputGltf.getBuffers());
      List<Image> images = Optionals.of(outputGltf.getImages());
      this.existingBufferUriStrings = collectUriStrings(buffers, Buffer::getUri);
      this.existingImageUriStrings = collectUriStrings(images, Image::getUri);
      this.gltfAsset = new GltfAssetV2(outputGltf, null);

      for (int i = 0; i < buffers.size(); i++) {
         Buffer buffer = buffers.get(i);
         this.storeBufferAsDefault(gltfModel, i, buffer);
      }

      for (int i = 0; i < images.size(); i++) {
         Image image = images.get(i);
         this.storeImageAsDefault(gltfModel, i, image);
      }

      return this.gltfAsset;
   }

   public GltfAssetV2 createBinary(GltfModel gltfModel) {
      GltfAssetV2 defaultAsset = this.createDefault(gltfModel);
      ByteBuffer resultBinaryBuffer = null;
      GlTF resultGltf = defaultAsset.getGltf();
      String binaryBufferUri = null;
      List<Buffer> resultBuffers = resultGltf.getBuffers();
      if (resultBuffers != null && !resultBuffers.isEmpty()) {
         Buffer resultBuffer = resultBuffers.get(0);
         binaryBufferUri = resultBuffer.getUri();
         resultBinaryBuffer = defaultAsset.getReferenceData(binaryBufferUri);
         resultBuffer.setUri(null);
      }

      GltfAssetV2 binaryAsset = new GltfAssetV2(resultGltf, resultBinaryBuffer);
      Map<String, ByteBuffer> referenceDatas = defaultAsset.getReferenceDatas();

      for (Entry<String, ByteBuffer> entry : referenceDatas.entrySet()) {
         String key = entry.getKey();
         if (!key.equals(binaryBufferUri)) {
            ByteBuffer value = entry.getValue();
            binaryAsset.putReferenceData(key, value);
         }
      }

      return binaryAsset;
   }

   private static <T> Set<String> collectUriStrings(Collection<T> elements, Function<? super T, ? extends String> uriFunction) {
      return elements.stream().map(uriFunction).filter(Objects::nonNull).filter(uriString -> !IO.isDataUriString(uriString)).collect(Collectors.toSet());
   }

   private void storeBufferAsDefault(GltfModel gltfModel, int index, Buffer buffer) {
      BufferModel bufferModel = gltfModel.getBufferModels().get(index);
      ByteBuffer bufferData = bufferModel.getBufferData();
      String oldUriString = buffer.getUri();
      String newUriString = oldUriString;
      if (oldUriString == null || IO.isDataUriString(oldUriString)) {
         newUriString = UriStrings.createBufferUriString(this.existingBufferUriStrings);
         buffer.setUri(newUriString);
         this.existingBufferUriStrings.add(newUriString);
      }

      this.gltfAsset.putReferenceData(newUriString, bufferData);
   }

   private void storeImageAsDefault(GltfModel gltfModel, int index, Image image) {
      ImageModel imageModel = gltfModel.getImageModels().get(index);
      ByteBuffer imageData = imageModel.getImageData();
      if (image.getBufferView() == null) {
         String oldUriString = image.getUri();
         String newUriString = oldUriString;
         if (oldUriString == null || IO.isDataUriString(oldUriString)) {
            newUriString = UriStrings.createImageUriString(imageModel, this.existingImageUriStrings);
            image.setUri(newUriString);
            this.existingImageUriStrings.add(newUriString);
         }

         this.gltfAsset.putReferenceData(newUriString, imageData);
      }
   }
}
