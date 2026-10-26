package de.javagl.jgltf.model.io.v1;

import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.structure.GltfModelStructures;
import de.javagl.jgltf.model.v1.GltfModelV1;
import java.util.logging.Logger;

final class DefaultAssetCreatorV1 {
   private static final Logger logger = Logger.getLogger(DefaultAssetCreatorV1.class.getName());

   GltfAssetV1 create(GltfModelV1 gltfModel) {
      boolean hasDefaultStructure = hasDefaultStructure(gltfModel);
      if (hasDefaultStructure) {
         logger.fine("The model has a default structure - creating asset");
         DirectAssetCreatorV1 delegate = new DirectAssetCreatorV1();
         return delegate.create(gltfModel);
      } else {
         logger.fine("Converting model into default structure");
         GltfModelStructures g = new GltfModelStructures();
         g.prepare(gltfModel);
         DefaultGltfModel defaultGltfModel = g.createDefault();
         DirectAssetCreatorV1 delegate = new DirectAssetCreatorV1();
         return delegate.create((GltfModelV1)defaultGltfModel);
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
