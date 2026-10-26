package de.javagl.jgltf.model.io.v1;

import de.javagl.jgltf.impl.v1.Buffer;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.impl.UriStrings;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.v1.BinaryGltfV1;
import de.javagl.jgltf.model.v1.GltfCreatorV1;
import de.javagl.jgltf.model.v1.GltfExtensionsV1;
import de.javagl.jgltf.model.v1.GltfModelV1;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

final class DirectAssetCreatorV1 {
   private GltfAssetV1 gltfAsset;
   private Set<String> existingBufferUriStrings;
   private Set<String> existingImageUriStrings;
   private Set<String> existingShaderUriStrings;

   GltfAssetV1 create(GltfModelV1 gltfModel) {
      GlTF outputGltf = GltfCreatorV1.create(gltfModel);
      GltfExtensionsV1.removeExtensionUsed(outputGltf, BinaryGltfV1.getBinaryGltfExtensionName());
      this.existingBufferUriStrings = collectUriStrings(Optionals.of(outputGltf.getBuffers()).values(), Buffer::getUri);
      this.existingImageUriStrings = collectUriStrings(Optionals.of(outputGltf.getImages()).values(), Image::getUri);
      this.existingShaderUriStrings = collectUriStrings(Optionals.of(outputGltf.getShaders()).values(), Shader::getUri);
      this.gltfAsset = new GltfAssetV1(outputGltf, null);
      Map<String, BufferModel> bufferIdToBuffer = GltfUtilsV1.createMap(outputGltf.getBuffers(), gltfModel.getBufferModels());
      Map<String, ImageModel> imageIdToImage = GltfUtilsV1.createMap(outputGltf.getImages(), gltfModel.getImageModels());
      Map<String, ShaderModel> shaderIdToShader = GltfUtilsV1.createMap(outputGltf.getShaders(), gltfModel.getShaderModels());
      Optionals.<String, Buffer>of(outputGltf.getBuffers()).forEach((id, value) -> this.storeBufferAsDefault(gltfModel, id, value, bufferIdToBuffer::get));
      Optionals.<String, Image>of(outputGltf.getImages()).forEach((id, value) -> this.storeImageAsDefault(gltfModel, id, value, imageIdToImage::get));
      Optionals.<String, Shader>of(outputGltf.getShaders()).forEach((id, value) -> this.storeShaderAsDefault(gltfModel, id, value, shaderIdToShader::get));
      return this.gltfAsset;
   }

   private static <T> Set<String> collectUriStrings(Collection<T> elements, Function<? super T, ? extends String> uriFunction) {
      return elements.stream().map(uriFunction).filter(Objects::nonNull).filter(uriString -> !IO.isDataUriString(uriString)).collect(Collectors.toSet());
   }

   private void storeBufferAsDefault(GltfModelV1 gltfModel, String id, Buffer buffer, Function<? super String, ? extends BufferModel> lookup) {
      BufferModel bufferModel = lookup.apply(id);
      ByteBuffer bufferData = bufferModel.getBufferData();
      String oldUriString = buffer.getUri();
      String newUriString = oldUriString;
      if (oldUriString == null || IO.isDataUriString(oldUriString) || BinaryGltfV1.isBinaryGltfBufferId(id)) {
         newUriString = UriStrings.createBufferUriString(this.existingBufferUriStrings);
         buffer.setUri(newUriString);
         this.existingBufferUriStrings.add(newUriString);
      }

      this.gltfAsset.putReferenceData(newUriString, bufferData);
   }

   private void storeImageAsDefault(GltfModelV1 gltfModel, String id, Image image, Function<? super String, ? extends ImageModel> lookup) {
      ImageModel imageModel = lookup.apply(id);
      ByteBuffer imageData = imageModel.getImageData();
      String oldUriString = image.getUri();
      String newUriString = oldUriString;
      if (oldUriString == null || IO.isDataUriString(oldUriString) || BinaryGltfV1.hasBinaryGltfExtension(image)) {
         newUriString = UriStrings.createImageUriString(imageModel, this.existingImageUriStrings);
         image.setUri(newUriString);
         this.existingImageUriStrings.add(newUriString);
         image.removeExtensions(BinaryGltfV1.getBinaryGltfExtensionName());
      }

      this.gltfAsset.putReferenceData(newUriString, imageData);
   }

   private void storeShaderAsDefault(GltfModelV1 gltfModel, String id, Shader shader, Function<? super String, ? extends ShaderModel> lookup) {
      ShaderModel shaderModel = lookup.apply(id);
      ByteBuffer shaderData = shaderModel.getShaderData();
      String oldUriString = shader.getUri();
      String newUriString = oldUriString;
      if (oldUriString == null || IO.isDataUriString(oldUriString) || BinaryGltfV1.hasBinaryGltfExtension(shader)) {
         newUriString = UriStrings.createShaderUriString(shaderModel, this.existingShaderUriStrings);
         shader.setUri(newUriString);
         this.existingShaderUriStrings.add(newUriString);
         shader.removeExtensions(BinaryGltfV1.getBinaryGltfExtensionName());
      }

      this.gltfAsset.putReferenceData(newUriString, shaderData);
   }
}
