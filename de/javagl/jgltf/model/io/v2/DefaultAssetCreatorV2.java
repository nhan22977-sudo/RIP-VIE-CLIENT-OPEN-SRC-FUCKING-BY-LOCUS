package de.javagl.jgltf.model.io.v2;

import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.structure.GltfModelStructures;
import java.util.logging.Logger;

final class DefaultAssetCreatorV2 {
   private static final Logger logger = Logger.getLogger(DefaultAssetCreatorV2.class.getName());

   GltfAssetV2 create(GltfModel gltfModel) {
      boolean hasDefaultStructure = hasDefaultStructure(gltfModel);
      if (hasDefaultStructure) {
         logger.fine("The model has a default structure - creating asset");
         DirectAssetCreatorV2 delegate = new DirectAssetCreatorV2();
         return delegate.createDefault(gltfModel);
      } else {
         logger.fine("Converting model into default structure");
         GltfModelStructures g = new GltfModelStructures();
         g.prepare(gltfModel);
         DefaultGltfModel defaultGltfModel = g.createDefault();
         DirectAssetCreatorV2 delegate = new DirectAssetCreatorV2();
         return delegate.createDefault(defaultGltfModel);
      }
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
}
