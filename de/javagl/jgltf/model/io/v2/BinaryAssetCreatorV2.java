package de.javagl.jgltf.model.io.v2;

import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.structure.GltfModelStructures;
import java.util.List;
import java.util.logging.Logger;

final class BinaryAssetCreatorV2 {
   private static final Logger logger = Logger.getLogger(BinaryAssetCreatorV2.class.getName());

   GltfAssetV2 create(GltfModel gltfModel) {
      boolean hasBinaryStructure = hasBinaryStructure(gltfModel);
      if (hasBinaryStructure) {
         logger.fine("The model has a binary structure - creating asset");
         DirectAssetCreatorV2 delegate = new DirectAssetCreatorV2();
         return delegate.createBinary(gltfModel);
      } else {
         logger.fine("Converting model into binary structure");
         GltfModelStructures g = new GltfModelStructures();
         g.prepare(gltfModel);
         DefaultGltfModel binaryGltfModel = g.createBinary();
         DirectAssetCreatorV2 delegate = new DirectAssetCreatorV2();
         return delegate.createBinary(binaryGltfModel);
      }
   }

   private static boolean hasBinaryStructure(GltfModel gltfModel) {
      List<BufferModel> bufferModels = gltfModel.getBufferModels();
      if (bufferModels.size() >= 2) {
         return false;
      }

      for (ImageModel imageModel : gltfModel.getImageModels()) {
         BufferViewModel imageBufferViewModel = imageModel.getBufferViewModel();
         if (imageBufferViewModel == null) {
            return false;
         }
      }

      return true;
   }
}
