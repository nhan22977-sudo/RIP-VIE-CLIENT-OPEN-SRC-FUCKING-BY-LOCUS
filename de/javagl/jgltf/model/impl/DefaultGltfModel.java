package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.TextureModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultGltfModel extends AbstractModelElement implements GltfModel {
   private final List<DefaultAccessorModel> accessorModels = new ArrayList<>();
   private final List<DefaultAnimationModel> animationModels = new ArrayList<>();
   private final List<DefaultBufferModel> bufferModels = new ArrayList<>();
   private final List<DefaultBufferViewModel> bufferViewModels = new ArrayList<>();
   private final List<DefaultCameraModel> cameraModels = new ArrayList<>();
   private final List<DefaultImageModel> imageModels = new ArrayList<>();
   private final List<MaterialModel> materialModels = new ArrayList<>();
   private final List<DefaultMeshModel> meshModels = new ArrayList<>();
   private final List<DefaultNodeModel> nodeModels = new ArrayList<>();
   private final List<DefaultSceneModel> sceneModels = new ArrayList<>();
   private final List<DefaultSkinModel> skinModels = new ArrayList<>();
   private final List<DefaultTextureModel> textureModels = new ArrayList<>();
   private final DefaultExtensionsModel extensionsModel = new DefaultExtensionsModel();
   private final DefaultAssetModel assetModel = new DefaultAssetModel();

   public void addAccessorModel(DefaultAccessorModel accessorModel) {
      this.accessorModels.add(accessorModel);
   }

   public void removeAccessorModel(DefaultAccessorModel accessorModel) {
      this.accessorModels.remove(accessorModel);
   }

   public void addAccessorModels(Collection<? extends DefaultAccessorModel> accessorModels) {
      for (DefaultAccessorModel accessorModel : accessorModels) {
         this.addAccessorModel(accessorModel);
      }
   }

   public DefaultAccessorModel getAccessorModel(int index) {
      return this.accessorModels.get(index);
   }

   public void clearAccessorModels() {
      this.accessorModels.clear();
   }

   @Override
   public List<AccessorModel> getAccessorModels() {
      return Collections.unmodifiableList(this.accessorModels);
   }

   public void addAnimationModel(DefaultAnimationModel animationModel) {
      this.animationModels.add(animationModel);
   }

   public void removeAnimationModel(DefaultAnimationModel animationModel) {
      this.animationModels.remove(animationModel);
   }

   public void addAnimationModels(Collection<? extends DefaultAnimationModel> animationModels) {
      for (DefaultAnimationModel animationModel : animationModels) {
         this.addAnimationModel(animationModel);
      }
   }

   public DefaultAnimationModel getAnimationModel(int index) {
      return this.animationModels.get(index);
   }

   public void clearAnimationModels() {
      this.animationModels.clear();
   }

   @Override
   public List<AnimationModel> getAnimationModels() {
      return Collections.unmodifiableList(this.animationModels);
   }

   public void addBufferModel(DefaultBufferModel bufferModel) {
      this.bufferModels.add(bufferModel);
   }

   public void removeBufferModel(DefaultBufferModel bufferModel) {
      this.bufferModels.remove(bufferModel);
   }

   public void addBufferModels(Collection<? extends DefaultBufferModel> bufferModels) {
      for (DefaultBufferModel bufferModel : bufferModels) {
         this.addBufferModel(bufferModel);
      }
   }

   public DefaultBufferModel getBufferModel(int index) {
      return this.bufferModels.get(index);
   }

   public void clearBufferModels() {
      this.bufferModels.clear();
   }

   @Override
   public List<BufferModel> getBufferModels() {
      return Collections.unmodifiableList(this.bufferModels);
   }

   public void addBufferViewModel(DefaultBufferViewModel bufferViewModel) {
      this.bufferViewModels.add(bufferViewModel);
   }

   public void removeBufferViewModel(DefaultBufferViewModel bufferViewModel) {
      this.bufferViewModels.remove(bufferViewModel);
   }

   public void addBufferViewModels(Collection<? extends DefaultBufferViewModel> bufferViewModels) {
      for (DefaultBufferViewModel bufferViewModel : bufferViewModels) {
         this.addBufferViewModel(bufferViewModel);
      }
   }

   public DefaultBufferViewModel getBufferViewModel(int index) {
      return this.bufferViewModels.get(index);
   }

   public void clearBufferViewModels() {
      this.bufferViewModels.clear();
   }

   @Override
   public List<BufferViewModel> getBufferViewModels() {
      return Collections.unmodifiableList(this.bufferViewModels);
   }

   public void addCameraModel(DefaultCameraModel cameraModel) {
      this.cameraModels.add(cameraModel);
   }

   public void removeCameraModel(DefaultCameraModel cameraModel) {
      this.cameraModels.remove(cameraModel);
   }

   public void addCameraModels(Collection<? extends DefaultCameraModel> cameraModels) {
      for (DefaultCameraModel cameraModel : cameraModels) {
         this.addCameraModel(cameraModel);
      }
   }

   public DefaultCameraModel getCameraModel(int index) {
      return this.cameraModels.get(index);
   }

   public void clearCameraModels() {
      this.cameraModels.clear();
   }

   @Override
   public List<CameraModel> getCameraModels() {
      return Collections.unmodifiableList(this.cameraModels);
   }

   public void addImageModel(DefaultImageModel imageModel) {
      this.imageModels.add(imageModel);
   }

   public void removeImageModel(DefaultImageModel imageModel) {
      this.imageModels.remove(imageModel);
   }

   public void addImageModels(Collection<? extends DefaultImageModel> imageModels) {
      for (DefaultImageModel imageModel : imageModels) {
         this.addImageModel(imageModel);
      }
   }

   public DefaultImageModel getImageModel(int index) {
      return this.imageModels.get(index);
   }

   public void clearImageModels() {
      this.imageModels.clear();
   }

   @Override
   public List<ImageModel> getImageModels() {
      return Collections.unmodifiableList(this.imageModels);
   }

   public void addMaterialModel(MaterialModel materialModel) {
      this.materialModels.add(materialModel);
   }

   public void removeMaterialModel(MaterialModel materialModel) {
      this.materialModels.remove(materialModel);
   }

   public void addMaterialModels(Collection<? extends MaterialModel> materialModels) {
      for (MaterialModel materialModel : materialModels) {
         this.addMaterialModel(materialModel);
      }
   }

   public MaterialModel getMaterialModel(int index) {
      return this.materialModels.get(index);
   }

   public void clearMaterialModels() {
      this.materialModels.clear();
   }

   @Override
   public List<MaterialModel> getMaterialModels() {
      return Collections.unmodifiableList(this.materialModels);
   }

   public void addMeshModel(DefaultMeshModel meshModel) {
      this.meshModels.add(meshModel);
   }

   public void removeMeshModel(DefaultMeshModel meshModel) {
      this.meshModels.remove(meshModel);
   }

   public void addMeshModels(Collection<? extends DefaultMeshModel> meshModels) {
      for (DefaultMeshModel meshModel : meshModels) {
         this.addMeshModel(meshModel);
      }
   }

   public DefaultMeshModel getMeshModel(int index) {
      return this.meshModels.get(index);
   }

   public void clearMeshModels() {
      this.meshModels.clear();
   }

   @Override
   public List<MeshModel> getMeshModels() {
      return Collections.unmodifiableList(this.meshModels);
   }

   public void addNodeModel(DefaultNodeModel nodeModel) {
      this.nodeModels.add(nodeModel);
   }

   public void removeNodeModel(DefaultNodeModel nodeModel) {
      this.nodeModels.remove(nodeModel);
   }

   public void addNodeModels(Collection<? extends DefaultNodeModel> nodeModels) {
      for (DefaultNodeModel nodeModel : nodeModels) {
         this.addNodeModel(nodeModel);
      }
   }

   public DefaultNodeModel getNodeModel(int index) {
      return this.nodeModels.get(index);
   }

   public void clearNodeModels() {
      this.nodeModels.clear();
   }

   @Override
   public List<NodeModel> getNodeModels() {
      return Collections.unmodifiableList(this.nodeModels);
   }

   public void addSceneModel(DefaultSceneModel sceneModel) {
      this.sceneModels.add(sceneModel);
   }

   public void removeSceneModel(DefaultSceneModel sceneModel) {
      this.sceneModels.remove(sceneModel);
   }

   public void addSceneModels(Collection<? extends DefaultSceneModel> sceneModels) {
      for (DefaultSceneModel sceneModel : sceneModels) {
         this.addSceneModel(sceneModel);
      }
   }

   public DefaultSceneModel getSceneModel(int index) {
      return this.sceneModels.get(index);
   }

   public void clearSceneModels() {
      this.sceneModels.clear();
   }

   @Override
   public List<SceneModel> getSceneModels() {
      return Collections.unmodifiableList(this.sceneModels);
   }

   public void addSkinModel(DefaultSkinModel skinModel) {
      this.skinModels.add(skinModel);
   }

   public void removeSkinModel(DefaultSkinModel skinModel) {
      this.skinModels.remove(skinModel);
   }

   public void addSkinModels(Collection<? extends DefaultSkinModel> skinModels) {
      for (DefaultSkinModel skinModel : skinModels) {
         this.addSkinModel(skinModel);
      }
   }

   public DefaultSkinModel getSkinModel(int index) {
      return this.skinModels.get(index);
   }

   public void clearSkinModels() {
      this.skinModels.clear();
   }

   @Override
   public List<SkinModel> getSkinModels() {
      return Collections.unmodifiableList(this.skinModels);
   }

   public void addTextureModel(DefaultTextureModel textureModel) {
      this.textureModels.add(textureModel);
   }

   public void removeTextureModel(DefaultTextureModel textureModel) {
      this.textureModels.remove(textureModel);
   }

   public void addTextureModels(Collection<? extends DefaultTextureModel> textureModels) {
      for (DefaultTextureModel textureModel : textureModels) {
         this.addTextureModel(textureModel);
      }
   }

   public DefaultTextureModel getTextureModel(int index) {
      return this.textureModels.get(index);
   }

   public void clearTextureModels() {
      this.textureModels.clear();
   }

   @Override
   public List<TextureModel> getTextureModels() {
      return Collections.unmodifiableList(this.textureModels);
   }

   public DefaultExtensionsModel getExtensionsModel() {
      return this.extensionsModel;
   }

   public DefaultAssetModel getAssetModel() {
      return this.assetModel;
   }
}
