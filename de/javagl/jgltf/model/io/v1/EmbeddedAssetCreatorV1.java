package de.javagl.jgltf.model.io.v1;

import de.javagl.jgltf.impl.v1.Buffer;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.io.MimeTypes;
import de.javagl.jgltf.model.structure.GltfModelStructures;
import de.javagl.jgltf.model.v1.BinaryGltfV1;
import de.javagl.jgltf.model.v1.GltfCreatorV1;
import de.javagl.jgltf.model.v1.GltfModelV1;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

final class EmbeddedAssetCreatorV1 {
   private static final Logger logger = Logger.getLogger(EmbeddedAssetCreatorV1.class.getName());

   GltfAssetV1 create(GltfModelV1 gltfModel) {
      boolean hasDefaultStructure = hasDefaultStructure(gltfModel);
      if (hasDefaultStructure) {
         logger.fine("The model has a default structure - creating asset");
         return this.createInternal(gltfModel);
      } else {
         logger.fine("Converting model into default structure");
         GltfModelStructures g = new GltfModelStructures();
         g.prepare(gltfModel);
         DefaultGltfModel defaultGltfModel = g.createDefault();
         return this.createInternal((GltfModelV1)defaultGltfModel);
      }
   }

   private GltfAssetV1 createInternal(GltfModelV1 gltfModel) {
      GlTF outputGltf = GltfCreatorV1.create(gltfModel);
      Map<String, BufferModel> bufferIdToBuffer = GltfUtilsV1.createMap(outputGltf.getBuffers(), gltfModel.getBufferModels());
      Map<String, ImageModel> imageIdToImage = GltfUtilsV1.createMap(outputGltf.getImages(), gltfModel.getImageModels());
      Map<String, ShaderModel> shaderIdToShader = GltfUtilsV1.createMap(outputGltf.getShaders(), gltfModel.getShaderModels());
      Optionals.<String, Buffer>of(outputGltf.getBuffers()).forEach((id, value) -> convertBufferToEmbedded(gltfModel, id, value, bufferIdToBuffer::get));
      Optionals.<String, Image>of(outputGltf.getImages()).forEach((id, value) -> convertImageToEmbedded(gltfModel, id, value, imageIdToImage::get));
      Optionals.<String, Shader>of(outputGltf.getShaders()).forEach((id, value) -> convertShaderToEmbedded(gltfModel, id, value, shaderIdToShader::get));
      return new GltfAssetV1(outputGltf, null);
   }

   private static boolean hasDefaultStructure(GltfModel gltfModel) {
      for (ImageModel imageModel : gltfModel.getImageModels()) {
         BufferViewModel imageBufferViewModel = imageModel.getBufferViewModel();
         if (imageBufferViewModel != null) {
            return false;
         }
      }

      return true;
   }

   private static void convertBufferToEmbedded(GltfModelV1 gltfModel, String id, Buffer buffer, Function<? super String, ? extends BufferModel> lookup) {
      BufferModel bufferModel = lookup.apply(id);
      ByteBuffer bufferData = bufferModel.getBufferData();
      byte[] data = new byte[bufferData.capacity()];
      bufferData.slice().get(data);
      String encodedData = Base64.getEncoder().encodeToString(data);
      String dataUriString = "data:application/gltf-buffer;base64," + encodedData;
      buffer.setUri(dataUriString);
   }

   private static void convertImageToEmbedded(GltfModelV1 gltfModel, String id, Image image, Function<? super String, ? extends ImageModel> lookup) {
      ImageModel imageModel = lookup.apply(id);
      ByteBuffer imageData = imageModel.getImageData();
      String uri = image.getUri();
      String imageMimeTypeString = MimeTypes.guessImageMimeTypeString(uri, imageData);
      if (imageMimeTypeString == null) {
         throw new GltfException("Could not detect MIME type of image " + id);
      }

      byte[] data = new byte[imageData.capacity()];
      imageData.slice().get(data);
      String encodedData = Base64.getEncoder().encodeToString(data);
      String dataUriString = "data:" + imageMimeTypeString + ";base64," + encodedData;
      image.removeExtensions(BinaryGltfV1.getBinaryGltfExtensionName());
      image.setUri(dataUriString);
   }

   private static void convertShaderToEmbedded(GltfModelV1 gltfModel, String id, Shader shader, Function<? super String, ? extends ShaderModel> lookup) {
      ShaderModel shaderModel = lookup.apply(id);
      ByteBuffer shaderData = shaderModel.getShaderData();
      byte[] data = new byte[shaderData.capacity()];
      shaderData.slice().get(data);
      String encodedData = Base64.getEncoder().encodeToString(data);
      String dataUriString = "data:text/plain;base64," + encodedData;
      shader.removeExtensions(BinaryGltfV1.getBinaryGltfExtensionName());
      shader.setUri(dataUriString);
   }
}
