package de.javagl.jgltf.model.structure;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferViewModel;
import de.javagl.jgltf.model.impl.DefaultImageModel;
import de.javagl.jgltf.model.impl.UriStrings;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DefaultBufferBuilderStrategy implements BufferBuilderStrategy {
   private final BufferStructureBuilder bufferStructureBuilder;
   private BufferStructure bufferStructure;
   private final Set<ImageModel> processedImageModels;
   private final Set<AccessorModel> processedAccessorModels;
   private final Map<ImageModel, BufferViewModel> imageBufferViews;
   private final Map<ImageModel, String> imageUriStrings;
   private final Set<String> existingImageUriStrings;
   private final DefaultBufferBuilderStrategy.Config config;

   DefaultBufferBuilderStrategy(DefaultBufferBuilderStrategy.Config config) {
      this.config = config;
      this.bufferStructureBuilder = new BufferStructureBuilder();
      this.processedImageModels = new LinkedHashSet<>();
      this.processedAccessorModels = new LinkedHashSet<>();
      this.imageBufferViews = new LinkedHashMap<>();
      this.imageUriStrings = new LinkedHashMap<>();
      this.existingImageUriStrings = new LinkedHashSet<>();
   }

   @Override
   public void process(GltfModel gltfModel) {
      this.processMeshModels(gltfModel.getMeshModels());
      this.processAnimationModels(gltfModel.getAnimationModels());
      this.processSkinModels(gltfModel.getSkinModels());
      this.processImageModels(gltfModel.getImageModels());
      this.processAccessorModels(gltfModel.getAccessorModels());
      this.commitBuffer();
      this.bufferStructure = this.bufferStructureBuilder.build();
   }

   private void processMeshModels(Collection<? extends MeshModel> meshModels) {
      if (this.config.bufferForMeshes) {
         this.commitBuffer();
      }

      for (MeshModel meshModel : meshModels) {
         this.processMeshModel(meshModel);
      }

      if (this.config.bufferForMeshes) {
         this.commitBuffer();
      }
   }

   private void processMeshModel(MeshModel meshModel) {
      if (this.config.bufferPerMesh) {
         this.commitBuffer();
      }

      for (MeshPrimitiveModel meshPrimitiveModel : meshModel.getMeshPrimitiveModels()) {
         this.processMeshPrimitiveModel(meshPrimitiveModel);
      }

      if (this.config.bufferPerMesh) {
         this.commitBuffer();
      }
   }

   private void processMeshPrimitiveModel(MeshPrimitiveModel meshPrimitiveModel) {
      if (this.config.bufferPerMeshPrimitive) {
         this.commitBuffer();
      }

      AccessorModel indices = meshPrimitiveModel.getIndices();
      if (indices != null && !this.processedAccessorModels.contains(indices)) {
         this.bufferStructureBuilder.addAccessorModel("indices", (DefaultAccessorModel)indices);
         this.processedAccessorModels.add(indices);
         this.bufferStructureBuilder.createArrayElementBufferViewModel("indices");
      }

      for (AccessorModel attribute : meshPrimitiveModel.getAttributes().values()) {
         if (!this.processedAccessorModels.contains(attribute)) {
            this.bufferStructureBuilder.addAccessorModel("attribute", (DefaultAccessorModel)attribute);
            this.processedAccessorModels.add(attribute);
         }
      }

      if (this.bufferStructureBuilder.getNumCurrentAccessorModels() > 0) {
         this.bufferStructureBuilder.createArrayBufferViewModel("attributes");
      }

      List<Map<String, AccessorModel>> targets = meshPrimitiveModel.getTargets();
      if (!targets.isEmpty()) {
         for (Map<String, AccessorModel> target : targets) {
            for (AccessorModel targetValue : target.values()) {
               if (!this.processedAccessorModels.contains(targetValue)) {
                  this.bufferStructureBuilder.addAccessorModel("target", (DefaultAccessorModel)targetValue);
                  this.processedAccessorModels.add(targetValue);
               }
            }
         }

         if (this.bufferStructureBuilder.getNumCurrentAccessorModels() > 0) {
            this.bufferStructureBuilder.createArrayBufferViewModel("targets");
         }
      }

      if (this.config.bufferPerMeshPrimitive) {
         this.commitBuffer();
      }
   }

   private void processAnimationModels(Collection<? extends AnimationModel> animationModels) {
      if (this.config.bufferForAnimations) {
         this.commitBuffer();
      }

      for (AnimationModel animationModel : animationModels) {
         this.processAnimationModel(animationModel);
      }

      if (this.config.bufferForAnimations) {
         this.commitBuffer();
      }
   }

   private void processAnimationModel(AnimationModel animationModel) {
      if (this.config.bufferPerAnimation) {
         this.commitBuffer();
      }

      for (AnimationModel.Channel channel : animationModel.getChannels()) {
         AnimationModel.Sampler sampler = channel.getSampler();
         AccessorModel input = sampler.getInput();
         AccessorModel output = sampler.getOutput();
         if (!this.processedAccessorModels.contains(input)) {
            this.bufferStructureBuilder.addAccessorModel("animation input", (DefaultAccessorModel)input);
            this.processedAccessorModels.add(input);
         }

         if (!this.processedAccessorModels.contains(output)) {
            this.bufferStructureBuilder.addAccessorModel("animation output", (DefaultAccessorModel)output);
            this.processedAccessorModels.add(output);
         }
      }

      if (!animationModel.getChannels().isEmpty()) {
         this.bufferStructureBuilder.createBufferViewModel("animation", null);
      }

      if (this.config.bufferPerAnimation) {
         this.commitBuffer();
      }
   }

   private void processSkinModels(Collection<? extends SkinModel> skinModels) {
      if (this.config.bufferForSkins) {
         this.commitBuffer();
      }

      for (SkinModel skinModel : skinModels) {
         this.processSkinModel(skinModel);
      }

      if (this.config.bufferForSkins) {
         this.commitBuffer();
      }
   }

   private void processSkinModel(SkinModel skinModel) {
      if (this.config.bufferPerSkin) {
         this.commitBuffer();
      }

      AccessorModel ibm = skinModel.getInverseBindMatrices();
      if (ibm != null && !this.processedAccessorModels.contains(ibm)) {
         this.bufferStructureBuilder.addAccessorModel("inverse bind matrices", (DefaultAccessorModel)ibm);
         this.processedAccessorModels.add(ibm);
         this.bufferStructureBuilder.createBufferViewModel("skin", null);
      }

      if (this.config.bufferPerSkin) {
         this.commitBuffer();
      }
   }

   private void processImageModels(Collection<? extends ImageModel> imageModels) {
      if (this.config.bufferForImages) {
         this.commitBuffer();
      }

      for (ImageModel imageModel : imageModels) {
         this.processImageModel(imageModel);
      }

      if (this.config.bufferForImages) {
         this.commitBuffer();
      }
   }

   private void processImageModel(ImageModel imageModel) {
      if (!this.processedImageModels.contains(imageModel)) {
         this.processedImageModels.add(imageModel);
         String uri = imageModel.getUri();
         if (uri != null) {
            this.imageUriStrings.put(imageModel, uri);
            this.existingImageUriStrings.add(uri);
         }

         if (this.config.bufferPerImage) {
            this.commitBuffer();
         }

         if (this.config.imagesInBufferViews) {
            ByteBuffer imageData = imageModel.getImageData();
            BufferViewModel bufferViewModel = this.bufferStructureBuilder.createImageBufferViewModel("image", imageData);
            this.imageBufferViews.put(imageModel, bufferViewModel);
         }

         if (this.config.bufferPerImage) {
            this.commitBuffer();
         }
      }
   }

   private void processAccessorModels(Collection<? extends AccessorModel> accessorModels) {
      if (this.config.bufferForAdditionalAccessors) {
         this.commitBuffer();
      }

      for (AccessorModel accessorModel : accessorModels) {
         this.processAccessorModel(accessorModel);
      }

      if (this.config.bufferForAdditionalAccessors) {
         this.commitBuffer();
      }
   }

   private void processAccessorModel(AccessorModel accessorModel) {
      if (!this.processedAccessorModels.contains(accessorModel)) {
         this.processedAccessorModels.add(accessorModel);
         this.bufferStructureBuilder.addAccessorModel("additional", (DefaultAccessorModel)accessorModel);
         this.bufferStructureBuilder.createBufferViewModel("additional", null);
      }
   }

   private void commitBuffer() {
      if (this.bufferStructureBuilder.getNumCurrentBufferViewModels() > 0) {
         int index = this.bufferStructureBuilder.getNumBufferModels();
         String uri = "buffer" + index + ".bin";
         this.bufferStructureBuilder.createBufferModel("buffer", uri);
      }
   }

   @Override
   public List<DefaultAccessorModel> getAccessorModels() {
      if (this.bufferStructure == null) {
         throw new IllegalStateException("No input model has been processed");
      } else {
         return this.bufferStructure.getAccessorModels();
      }
   }

   @Override
   public List<DefaultBufferViewModel> getBufferViewModels() {
      if (this.bufferStructure == null) {
         throw new IllegalStateException("No input model has been processed");
      } else {
         return this.bufferStructure.getBufferViewModels();
      }
   }

   @Override
   public List<DefaultBufferModel> getBufferModels() {
      if (this.bufferStructure == null) {
         throw new IllegalStateException("No input model has been processed");
      } else {
         return this.bufferStructure.getBufferModels();
      }
   }

   @Override
   public void validateImageModel(DefaultImageModel imageModel) {
      BufferViewModel imageBufferViewModel = this.imageBufferViews.get(imageModel);
      if (imageBufferViewModel == null) {
         String oldUriString = this.imageUriStrings.get(imageModel);
         String newUriString = oldUriString;
         if (oldUriString == null) {
            newUriString = UriStrings.createImageUriString(imageModel, this.existingImageUriStrings);
            this.existingImageUriStrings.add(newUriString);
            this.imageUriStrings.put(imageModel, newUriString);
         }

         imageModel.setUri(newUriString);
         imageModel.setBufferViewModel(null);
      } else {
         imageModel.setBufferViewModel(imageBufferViewModel);
         imageModel.setUri(null);
      }
   }

   static class Config {
      boolean bufferPerMeshPrimitive = false;
      boolean bufferPerMesh = false;
      boolean bufferForMeshes = false;
      boolean bufferPerAnimation = false;
      boolean bufferForAnimations = false;
      boolean bufferPerSkin = false;
      boolean bufferForSkins = false;
      boolean imagesInBufferViews = false;
      boolean bufferPerImage = false;
      boolean bufferForImages = false;
      boolean bufferForAdditionalAccessors = false;
   }
}
