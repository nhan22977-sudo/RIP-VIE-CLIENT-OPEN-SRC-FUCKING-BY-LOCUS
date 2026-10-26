package de.javagl.jgltf.model;

import java.util.List;

public interface GltfModel extends ModelElement {
   List<AccessorModel> getAccessorModels();

   List<AnimationModel> getAnimationModels();

   List<BufferModel> getBufferModels();

   List<BufferViewModel> getBufferViewModels();

   List<CameraModel> getCameraModels();

   List<ImageModel> getImageModels();

   List<MaterialModel> getMaterialModels();

   List<MeshModel> getMeshModels();

   List<NodeModel> getNodeModels();

   List<SceneModel> getSceneModels();

   List<SkinModel> getSkinModels();

   List<TextureModel> getTextureModels();

   ExtensionsModel getExtensionsModel();

   AssetModel getAssetModel();
}
