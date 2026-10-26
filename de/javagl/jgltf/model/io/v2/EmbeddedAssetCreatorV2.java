package de.javagl.jgltf.model.io.v2;

import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.v2.GltfCreatorV2;
import java.util.List;

final class EmbeddedAssetCreatorV2 {
   GltfAssetV2 create(GltfModel gltfModel) {
      GlTF outputGltf = GltfCreatorV2.create(gltfModel);
      List<Buffer> buffers = Optionals.of(outputGltf.getBuffers());
      List<BufferModel> bufferModels = gltfModel.getBufferModels();

      for (int i = 0; i < buffers.size(); i++) {
         Buffer buffer = buffers.get(i);
         BufferModel bufferModel = bufferModels.get(i);
         String dataUri = DataUris.createBufferDataUri(bufferModel);
         buffer.setUri(dataUri);
      }

      List<Image> images = Optionals.of(outputGltf.getImages());
      List<ImageModel> imageModels = gltfModel.getImageModels();

      for (int i = 0; i < images.size(); i++) {
         Image image = images.get(i);
         ImageModel imageModel = imageModels.get(i);
         if (imageModel.getBufferViewModel() == null) {
            String currentUri = image.getUri();
            String dataUri = DataUris.createImageDataUri(currentUri, imageModel);
            image.setUri(dataUri);
         } else {
            image.setUri(null);
         }
      }

      return new GltfAssetV2(outputGltf, null);
   }
}
