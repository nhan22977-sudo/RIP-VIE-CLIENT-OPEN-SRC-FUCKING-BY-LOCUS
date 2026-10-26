package de.javagl.jgltf.model.v1;

import de.javagl.jgltf.impl.v1.Accessor;
import de.javagl.jgltf.impl.v1.Animation;
import de.javagl.jgltf.impl.v1.AnimationChannel;
import de.javagl.jgltf.impl.v1.AnimationChannelTarget;
import de.javagl.jgltf.impl.v1.AnimationSampler;
import de.javagl.jgltf.impl.v1.Asset;
import de.javagl.jgltf.impl.v1.Buffer;
import de.javagl.jgltf.impl.v1.BufferView;
import de.javagl.jgltf.impl.v1.Camera;
import de.javagl.jgltf.impl.v1.CameraOrthographic;
import de.javagl.jgltf.impl.v1.CameraPerspective;
import de.javagl.jgltf.impl.v1.GlTF;
import de.javagl.jgltf.impl.v1.GlTFChildOfRootProperty;
import de.javagl.jgltf.impl.v1.GlTFProperty;
import de.javagl.jgltf.impl.v1.Image;
import de.javagl.jgltf.impl.v1.Material;
import de.javagl.jgltf.impl.v1.Mesh;
import de.javagl.jgltf.impl.v1.MeshPrimitive;
import de.javagl.jgltf.impl.v1.Node;
import de.javagl.jgltf.impl.v1.Program;
import de.javagl.jgltf.impl.v1.Sampler;
import de.javagl.jgltf.impl.v1.Scene;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.impl.v1.Skin;
import de.javagl.jgltf.impl.v1.Technique;
import de.javagl.jgltf.impl.v1.TechniqueParameters;
import de.javagl.jgltf.impl.v1.TechniqueStates;
import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import de.javagl.jgltf.impl.v1.Texture;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.Accessors;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.ElementType;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.TextureModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.gl.TechniqueParametersModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;
import de.javagl.jgltf.model.gl.impl.DefaultProgramModel;
import de.javagl.jgltf.model.gl.impl.DefaultShaderModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueParametersModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueStatesModel;
import de.javagl.jgltf.model.impl.AbstractModelElement;
import de.javagl.jgltf.model.impl.AbstractNamedModelElement;
import de.javagl.jgltf.model.impl.DefaultAccessorModel;
import de.javagl.jgltf.model.impl.DefaultAnimationModel;
import de.javagl.jgltf.model.impl.DefaultAssetModel;
import de.javagl.jgltf.model.impl.DefaultBufferModel;
import de.javagl.jgltf.model.impl.DefaultBufferViewModel;
import de.javagl.jgltf.model.impl.DefaultCameraModel;
import de.javagl.jgltf.model.impl.DefaultCameraOrthographicModel;
import de.javagl.jgltf.model.impl.DefaultCameraPerspectiveModel;
import de.javagl.jgltf.model.impl.DefaultExtensionsModel;
import de.javagl.jgltf.model.impl.DefaultImageModel;
import de.javagl.jgltf.model.impl.DefaultMeshModel;
import de.javagl.jgltf.model.impl.DefaultMeshPrimitiveModel;
import de.javagl.jgltf.model.impl.DefaultNodeModel;
import de.javagl.jgltf.model.impl.DefaultSceneModel;
import de.javagl.jgltf.model.impl.DefaultSkinModel;
import de.javagl.jgltf.model.impl.DefaultTextureModel;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.GltfAsset;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.io.MimeTypes;
import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import de.javagl.jgltf.model.v1.gl.DefaultModels;
import de.javagl.jgltf.model.v1.gl.GltfDefaults;
import de.javagl.jgltf.model.v1.gl.TechniqueStatesFunctionsModels;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.logging.Logger;

public class GltfModelCreatorV1 {
   private static final Logger logger = Logger.getLogger(GltfModelCreatorV1.class.getName());
   private final IndexMappingSet indexMappingSet;
   private final GltfAsset gltfAsset;
   private final GlTF gltf;
   private final GltfModelV1 gltfModel;

   public static GltfModelV1 create(GltfAssetV1 gltfAsset) {
      GltfModelV1 gltfModel = new GltfModelV1();
      GltfModelCreatorV1 creator = new GltfModelCreatorV1(gltfAsset, gltfModel);
      creator.create();
      return gltfModel;
   }

   GltfModelCreatorV1(GltfAssetV1 gltfAsset, GltfModelV1 gltfModel) {
      this.gltfAsset = Objects.requireNonNull(gltfAsset, "The gltfAsset may not be null");
      this.gltf = gltfAsset.getGltf();
      this.gltfModel = Objects.requireNonNull(gltfModel, "The gltfModel may not be null");
      this.indexMappingSet = IndexMappingSets.create(gltfAsset.getGltf());
   }

   void create() {
      transferGltfPropertyElements(this.gltf, this.gltfModel);
      this.createAccessorModels();
      this.createAnimationModels();
      this.createBufferModels();
      this.createBufferViewModels();
      this.createCameraModels();
      this.createImageModels();
      this.createMaterialModels();
      this.createMeshModels();
      this.createNodeModels();
      this.createSceneModels();
      this.createSkinModels();
      this.createTextureModels();
      this.createShaderModels();
      this.createProgramModels();
      this.createTechniqueModels();
      this.initBufferModels();
      this.initBufferViewModels();
      this.initAccessorModels();
      this.assignBufferViewByteStrides();
      this.initAnimationModels();
      this.initImageModels();
      this.initTechniqueModels();
      this.initMaterialModels();
      this.initMeshModels();
      this.initNodeModels();
      this.initSceneModels();
      this.initSkinModels();
      this.initTextureModels();
      this.initShaderModels();
      this.initProgramModels();
      this.initExtensionsModel();
      this.initAssetModel();
   }

   private void createAccessorModels() {
      Map<String, Accessor> accessors = Optionals.of(this.gltf.getAccessors());

      for (Accessor accessor : accessors.values()) {
         DefaultAccessorModel accessorModel = createAccessorModel(accessor);
         this.gltfModel.addAccessorModel(accessorModel);
      }
   }

   private static DefaultAccessorModel createAccessorModel(Accessor accessor) {
      Integer componentType = accessor.getComponentType();
      Integer byteOffset = accessor.getByteOffset();
      Integer count = accessor.getCount();
      ElementType elementType = ElementType.forString(accessor.getType());
      Integer byteStride = accessor.getByteStride();
      if (byteStride == null) {
         byteStride = elementType.getNumComponents() * Accessors.getNumBytesForAccessorComponentType(componentType);
      }

      DefaultAccessorModel accessorModel = new DefaultAccessorModel(componentType, count, elementType);
      accessorModel.setByteOffset(byteOffset);
      accessorModel.setByteStride(byteStride);
      return accessorModel;
   }

   private void createAnimationModels() {
      Map<String, Animation> animations = Optionals.of(this.gltf.getAnimations());

      for (int i = 0; i < animations.size(); i++) {
         this.gltfModel.addAnimationModel(new DefaultAnimationModel());
      }
   }

   private void createBufferModels() {
      Map<String, Buffer> buffers = Optionals.of(this.gltf.getBuffers());

      for (Buffer buffer : buffers.values()) {
         DefaultBufferModel bufferModel = new DefaultBufferModel();
         bufferModel.setUri(buffer.getUri());
         this.gltfModel.addBufferModel(bufferModel);
      }
   }

   private void createBufferViewModels() {
      Map<String, BufferView> bufferViews = Optionals.of(this.gltf.getBufferViews());

      for (BufferView bufferView : bufferViews.values()) {
         DefaultBufferViewModel bufferViewModel = createBufferViewModel(bufferView);
         this.gltfModel.addBufferViewModel(bufferViewModel);
      }
   }

   private void createCameraModels() {
      Map<String, Camera> cameras = Optionals.of(this.gltf.getCameras());

      for (Camera camera : cameras.values()) {
         String type = camera.getType();
         if ("perspective".equals(type)) {
            CameraPerspective cameraPerspective = camera.getPerspective();
            DefaultCameraPerspectiveModel cameraPerspectiveModel = new DefaultCameraPerspectiveModel();
            cameraPerspectiveModel.setAspectRatio(cameraPerspective.getAspectRatio());
            cameraPerspectiveModel.setYfov(cameraPerspective.getYfov());
            cameraPerspectiveModel.setZfar(cameraPerspective.getZfar());
            cameraPerspectiveModel.setZnear(cameraPerspective.getZnear());
            DefaultCameraModel cameraModel = new DefaultCameraModel();
            cameraModel.setCameraPerspectiveModel(cameraPerspectiveModel);
            this.gltfModel.addCameraModel(cameraModel);
         } else if ("orthographic".equals(type)) {
            CameraOrthographic cameraOrthographic = camera.getOrthographic();
            DefaultCameraOrthographicModel cameraOrthographicModel = new DefaultCameraOrthographicModel();
            cameraOrthographicModel.setXmag(cameraOrthographic.getXmag());
            cameraOrthographicModel.setYmag(cameraOrthographic.getYmag());
            cameraOrthographicModel.setZfar(cameraOrthographic.getZfar());
            cameraOrthographicModel.setZnear(cameraOrthographic.getZnear());
            DefaultCameraModel cameraModel = new DefaultCameraModel();
            cameraModel.setCameraOrthographicModel(cameraOrthographicModel);
            this.gltfModel.addCameraModel(cameraModel);
         } else {
            logger.severe("Invalid camera type: " + type);
         }
      }
   }

   private static DefaultBufferViewModel createBufferViewModel(BufferView bufferView) {
      int byteOffset = bufferView.getByteOffset();
      Integer byteLength = bufferView.getByteLength();
      if (byteLength == null) {
         logger.warning("No byteLength found in BufferView");
         byteLength = 0;
      }

      Integer target = bufferView.getTarget();
      DefaultBufferViewModel bufferViewModel = new DefaultBufferViewModel(target);
      bufferViewModel.setByteOffset(byteOffset);
      bufferViewModel.setByteLength(byteLength);
      return bufferViewModel;
   }

   private void createImageModels() {
      Map<String, Image> images = Optionals.of(this.gltf.getImages());

      for (Image image : images.values()) {
         DefaultImageModel imageModel = new DefaultImageModel();
         String uri = image.getUri();
         imageModel.setUri(uri);
         this.gltfModel.addImageModel(imageModel);
      }
   }

   private void createMaterialModels() {
      Map<String, Material> materials = Optionals.of(this.gltf.getMaterials());

      for (int i = 0; i < materials.size(); i++) {
         this.gltfModel.addMaterialModel(new MaterialModelV1());
      }
   }

   private void createMeshModels() {
      Map<String, Mesh> meshes = Optionals.of(this.gltf.getMeshes());

      for (int i = 0; i < meshes.size(); i++) {
         this.gltfModel.addMeshModel(new DefaultMeshModel());
      }
   }

   private void createNodeModels() {
      Map<String, Node> nodes = Optionals.of(this.gltf.getNodes());

      for (int i = 0; i < nodes.size(); i++) {
         this.gltfModel.addNodeModel(new DefaultNodeModel());
      }
   }

   private void createSceneModels() {
      Map<String, Scene> scenes = Optionals.of(this.gltf.getScenes());

      for (int i = 0; i < scenes.size(); i++) {
         this.gltfModel.addSceneModel(new DefaultSceneModel());
      }
   }

   private void createSkinModels() {
      Map<String, Skin> skins = Optionals.of(this.gltf.getSkins());

      for (Entry<String, Skin> entry : skins.entrySet()) {
         Skin skin = entry.getValue();
         float[] bindShapeMatrix = skin.getBindShapeMatrix();
         DefaultSkinModel skinModel = new DefaultSkinModel();
         skinModel.setBindShapeMatrix(bindShapeMatrix);
         this.gltfModel.addSkinModel(skinModel);
      }
   }

   private void createTextureModels() {
      Map<String, Texture> textures = Optionals.of(this.gltf.getTextures());
      Map<String, Sampler> samplers = Optionals.of(this.gltf.getSamplers());

      for (Entry<String, Texture> entry : textures.entrySet()) {
         Texture texture = entry.getValue();
         String samplerId = texture.getSampler();
         Sampler sampler = samplers.get(samplerId);
         int magFilter = Optionals.of(sampler.getMagFilter(), sampler.defaultMagFilter());
         int minFilter = Optionals.of(sampler.getMinFilter(), sampler.defaultMinFilter());
         int wrapS = Optionals.of(sampler.getWrapS(), sampler.defaultWrapS());
         int wrapT = Optionals.of(sampler.getWrapT(), sampler.defaultWrapT());
         DefaultTextureModel textureModel = new DefaultTextureModel();
         textureModel.setMagFilter(magFilter);
         textureModel.setMinFilter(minFilter);
         textureModel.setWrapS(wrapS);
         textureModel.setWrapT(wrapT);
         this.gltfModel.addTextureModel(textureModel);
      }
   }

   private void createShaderModels() {
      Map<String, Shader> shaders = Optionals.of(this.gltf.getShaders());

      for (Entry<String, Shader> entry : shaders.entrySet()) {
         Shader shader = entry.getValue();
         Integer type = shader.getType();
         ShaderModel.ShaderType shaderType = null;
         if (type == 35633) {
            shaderType = ShaderModel.ShaderType.VERTEX_SHADER;
         } else {
            shaderType = ShaderModel.ShaderType.FRAGMENT_SHADER;
         }

         DefaultShaderModel shaderModel = new DefaultShaderModel(shader.getUri(), shaderType);
         this.gltfModel.addShaderModel(shaderModel);
      }
   }

   private void createProgramModels() {
      Map<String, Program> programs = Optionals.of(this.gltf.getPrograms());

      for (int i = 0; i < programs.size(); i++) {
         this.gltfModel.addProgramModel(new DefaultProgramModel());
      }
   }

   private void createTechniqueModels() {
      Map<String, Technique> techniques = Optionals.of(this.gltf.getTechniques());

      for (int i = 0; i < techniques.size(); i++) {
         this.gltfModel.addTechniqueModel(new DefaultTechniqueModel());
      }
   }

   private void initAccessorModels() {
      Map<String, Accessor> accessors = Optionals.of(this.gltf.getAccessors());

      for (Entry<String, Accessor> entry : accessors.entrySet()) {
         String accessorId = entry.getKey();
         Accessor accessor = entry.getValue();
         String bufferViewId = accessor.getBufferView();
         BufferViewModel bufferViewModel = this.get("bufferViews", bufferViewId, this.gltfModel::getBufferViewModel);
         DefaultAccessorModel accessorModel = this.get("accessors", accessorId, this.gltfModel::getAccessorModel);
         transferGltfChildOfRootPropertyElements(accessor, accessorModel);
         accessorModel.setBufferViewModel(bufferViewModel);
         accessorModel.setAccessorData(AccessorDatas.create(accessorModel));
      }
   }

   private void initAnimationModels() {
      Map<String, Animation> animations = Optionals.of(this.gltf.getAnimations());

      for (Entry<String, Animation> entry : animations.entrySet()) {
         String animationId = entry.getKey();
         Animation animation = entry.getValue();
         DefaultAnimationModel animationModel = this.get("animations", animationId, this.gltfModel::getAnimationModel);
         transferGltfChildOfRootPropertyElements(animation, animationModel);

         for (AnimationChannel animationChannel : Optionals.of(animation.getChannels())) {
            AnimationModel.Channel channel = this.createChannel(animation, animationChannel);
            animationModel.addChannel(channel);
         }
      }
   }

   private void initImageModels() {
      Map<String, Image> images = Optionals.of(this.gltf.getImages());

      for (Entry<String, Image> entry : images.entrySet()) {
         String imageId = entry.getKey();
         Image image = entry.getValue();
         DefaultImageModel imageModel = this.get("images", imageId, this.gltfModel::getImageModel);
         transferGltfChildOfRootPropertyElements(image, imageModel);
         if (BinaryGltfV1.hasBinaryGltfExtension(image)) {
            String bufferViewId = BinaryGltfV1.getBinaryGltfBufferViewId(image);
            BufferViewModel bufferViewModel = this.get("bufferViews", bufferViewId, this.gltfModel::getBufferViewModel);
            imageModel.setBufferViewModel(bufferViewModel);
         } else {
            String uri = image.getUri();
            if (IO.isDataUriString(uri)) {
               byte[] data = IO.readDataUri(uri);
               ByteBuffer imageData = Buffers.create(data);
               imageModel.setImageData(imageData);
            } else {
               ByteBuffer imageData = this.gltfAsset.getReferenceData(uri);
               imageModel.setImageData(imageData);
            }
         }

         String mimeType = imageModel.getMimeType();
         if (mimeType == null) {
            ByteBuffer imageData = imageModel.getImageData();
            mimeType = MimeTypes.guessImageMimeTypeStringUnchecked(imageData);
            imageModel.setMimeType(mimeType);
         }
      }
   }

   private AnimationModel.Channel createChannel(Animation animation, AnimationChannel animationChannel) {
      Map<String, String> parameters = Optionals.of(animation.getParameters());
      Map<String, AnimationSampler> samplers = Optionals.of(animation.getSamplers());
      String samplerId = animationChannel.getSampler();
      AnimationSampler animationSampler = samplers.get(samplerId);
      String inputParameterId = animationSampler.getInput();
      String inputAccessorId = parameters.get(inputParameterId);
      if (inputAccessorId == null) {
         logger.warning("Assuming " + inputParameterId + " to be an accessor ID");
         inputAccessorId = inputParameterId;
      }

      AccessorModel inputAccessorModel = this.get("accessors", inputAccessorId, this.gltfModel::getAccessorModel);
      String outputParameterId = animationSampler.getOutput();
      String outputAccessorId = parameters.get(outputParameterId);
      if (outputAccessorId == null) {
         logger.warning("Assuming " + outputParameterId + " to be an accessor ID");
         outputAccessorId = outputParameterId;
      }

      AccessorModel outputAccessorModel = this.get("accessors", outputAccessorId, this.gltfModel::getAccessorModel);
      String interpolationString = animationSampler.getInterpolation();
      AnimationModel.Interpolation interpolation = interpolationString == null
         ? AnimationModel.Interpolation.LINEAR
         : AnimationModel.Interpolation.valueOf(interpolationString);
      AnimationModel.Sampler sampler = new DefaultAnimationModel.DefaultSampler(inputAccessorModel, interpolation, outputAccessorModel);
      AnimationChannelTarget animationChannelTarget = animationChannel.getTarget();
      String nodeId = animationChannelTarget.getId();
      String path = animationChannelTarget.getPath();
      NodeModel nodeModel = this.get("nodes", nodeId, this.gltfModel::getNodeModel);
      return new DefaultAnimationModel.DefaultChannel(sampler, nodeModel, path);
   }

   private void initBufferModels() {
      ByteBuffer binaryData = null;
      ByteBuffer b = this.gltfAsset.getBinaryData();
      if (b != null && b.capacity() > 0) {
         binaryData = b;
      }

      Map<String, Buffer> buffers = Optionals.of(this.gltf.getBuffers());

      for (Entry<String, Buffer> entry : buffers.entrySet()) {
         String bufferId = entry.getKey();
         Buffer buffer = entry.getValue();
         DefaultBufferModel bufferModel = this.get("buffers", bufferId, this.gltfModel::getBufferModel);
         transferGltfChildOfRootPropertyElements(buffer, bufferModel);
         if (BinaryGltfV1.isBinaryGltfBufferId(bufferId)) {
            if (binaryData == null) {
               logger.severe("The glTF contains a buffer with the binary buffer ID, but no binary data has been given");
            } else {
               bufferModel.setBufferData(binaryData);
            }
         } else {
            String uri = buffer.getUri();
            if (IO.isDataUriString(uri)) {
               byte[] data = IO.readDataUri(uri);
               ByteBuffer bufferData = Buffers.create(data);
               bufferModel.setBufferData(bufferData);
            } else {
               ByteBuffer bufferData = this.gltfAsset.getReferenceData(uri);
               bufferModel.setBufferData(bufferData);
            }
         }
      }
   }

   private void initBufferViewModels() {
      Map<String, BufferView> bufferViews = Optionals.of(this.gltf.getBufferViews());

      for (Entry<String, BufferView> entry : bufferViews.entrySet()) {
         String bufferViewId = entry.getKey();
         BufferView bufferView = entry.getValue();
         String bufferId = bufferView.getBuffer();
         BufferModel bufferModel = this.get("buffers", bufferId, this.gltfModel::getBufferModel);
         DefaultBufferViewModel bufferViewModel = this.get("bufferViews", bufferViewId, this.gltfModel::getBufferViewModel);
         transferGltfChildOfRootPropertyElements(bufferView, bufferViewModel);
         bufferViewModel.setBufferModel(bufferModel);
      }
   }

   private List<DefaultAccessorModel> computeAccessorModelsOf(BufferViewModel bufferViewModel) {
      List<DefaultAccessorModel> result = new ArrayList<>();
      int n = this.gltfModel.getAccessorModels().size();

      for (int i = 0; i < n; i++) {
         DefaultAccessorModel accessorModel = this.gltfModel.getAccessorModel(i);
         BufferViewModel b = accessorModel.getBufferViewModel();
         if (bufferViewModel.equals(b)) {
            result.add(accessorModel);
         }
      }

      return result;
   }

   private static int computeCommonByteStride(Iterable<? extends AccessorModel> accessorModels) {
      int commonByteStride = -1;

      for (AccessorModel accessorModel : accessorModels) {
         int byteStride = accessorModel.getByteStride();
         if (commonByteStride == -1) {
            commonByteStride = byteStride;
         } else if (commonByteStride != byteStride) {
            logger.warning("The accessor models do not have the same byte stride: " + commonByteStride + " and " + byteStride);
         }
      }

      return commonByteStride;
   }

   private void assignBufferViewByteStrides() {
      int n = this.gltfModel.getBufferModels().size();

      for (int i = 0; i < n; i++) {
         DefaultBufferViewModel bufferViewModel = this.gltfModel.getBufferViewModel(i);
         List<DefaultAccessorModel> accessorModelsOfBufferView = this.computeAccessorModelsOf(bufferViewModel);
         if (accessorModelsOfBufferView.size() > 1) {
            int byteStride = computeCommonByteStride(accessorModelsOfBufferView);
            bufferViewModel.setByteStride(byteStride);
         }
      }
   }

   private void initMeshModels() {
      Map<String, Mesh> meshes = Optionals.of(this.gltf.getMeshes());

      for (Entry<String, Mesh> entry : meshes.entrySet()) {
         String meshId = entry.getKey();
         Mesh mesh = entry.getValue();
         List<MeshPrimitive> primitives = Optionals.of(mesh.getPrimitives());
         DefaultMeshModel meshModel = this.get("meshes", meshId, this.gltfModel::getMeshModel);
         transferGltfChildOfRootPropertyElements(mesh, meshModel);

         for (MeshPrimitive meshPrimitive : primitives) {
            MeshPrimitiveModel meshPrimitiveModel = this.createMeshPrimitiveModel(meshPrimitive);
            meshModel.addMeshPrimitiveModel(meshPrimitiveModel);
         }
      }
   }

   private DefaultMeshPrimitiveModel createMeshPrimitiveModel(MeshPrimitive meshPrimitive) {
      Integer mode = Optionals.of(meshPrimitive.getMode(), meshPrimitive.defaultMode());
      DefaultMeshPrimitiveModel meshPrimitiveModel = new DefaultMeshPrimitiveModel(mode);
      transferGltfPropertyElements(meshPrimitive, meshPrimitiveModel);
      String indicesId = meshPrimitive.getIndices();
      if (indicesId != null) {
         AccessorModel indices = this.get("accessors", indicesId, this.gltfModel::getAccessorModel);
         meshPrimitiveModel.setIndices(indices);
      }

      Map<String, String> attributes = Optionals.of(meshPrimitive.getAttributes());

      for (Entry<String, String> entry : attributes.entrySet()) {
         String attributeName = entry.getKey();
         String attributeId = entry.getValue();
         AccessorModel attribute = this.get("accessors", attributeId, this.gltfModel::getAccessorModel);
         meshPrimitiveModel.putAttribute(attributeName, attribute);
      }

      String materialId = meshPrimitive.getMaterial();
      if (materialId != null && !GltfDefaults.isDefaultMaterialId(materialId)) {
         MaterialModel materialModel = this.get("materials", materialId, this.gltfModel::getMaterialModel);
         meshPrimitiveModel.setMaterialModel(materialModel);
      } else {
         meshPrimitiveModel.setMaterialModel(DefaultModels.getDefaultMaterialModel());
      }

      return meshPrimitiveModel;
   }

   private void initNodeModels() {
      Map<String, Node> nodes = Optionals.of(this.gltf.getNodes());

      for (Entry<String, Node> entry : nodes.entrySet()) {
         String nodeId = entry.getKey();
         Node node = entry.getValue();
         DefaultNodeModel nodeModel = this.get("nodes", nodeId, this.gltfModel::getNodeModel);
         transferGltfChildOfRootPropertyElements(node, nodeModel);

         for (String childId : Optionals.of(node.getChildren())) {
            DefaultNodeModel child = this.get("nodes", childId, this.gltfModel::getNodeModel);
            nodeModel.addChild(child);
         }

         for (String meshId : Optionals.of(node.getMeshes())) {
            MeshModel meshModel = this.get("meshes", meshId, this.gltfModel::getMeshModel);
            nodeModel.addMeshModel(meshModel);
         }

         String skinId = node.getSkin();
         if (skinId != null) {
            SkinModel skinModel = this.get("skins", skinId, this.gltfModel::getSkinModel);
            nodeModel.setSkinModel(skinModel);
         }

         String cameraId = node.getCamera();
         if (cameraId != null) {
            CameraModel cameraModel = this.get("cameras", cameraId, this.gltfModel::getCameraModel);
            nodeModel.setCameraModel(cameraModel);
         }

         float[] matrix = node.getMatrix();
         float[] translation = node.getTranslation();
         float[] rotation = node.getRotation();
         float[] scale = node.getScale();
         nodeModel.setMatrix(Optionals.clone(matrix));
         nodeModel.setTranslation(Optionals.clone(translation));
         nodeModel.setRotation(Optionals.clone(rotation));
         nodeModel.setScale(Optionals.clone(scale));
      }
   }

   private void initSceneModels() {
      Map<String, Scene> scenes = Optionals.of(this.gltf.getScenes());

      for (Entry<String, Scene> entry : scenes.entrySet()) {
         String sceneId = entry.getKey();
         Scene scene = entry.getValue();
         DefaultSceneModel sceneModel = this.get("scenes", sceneId, this.gltfModel::getSceneModel);
         transferGltfChildOfRootPropertyElements(scene, sceneModel);

         for (String nodeId : Optionals.of(scene.getNodes())) {
            NodeModel nodeModel = this.get("nodes", nodeId, this.gltfModel::getNodeModel);
            sceneModel.addNode(nodeModel);
         }
      }
   }

   private static Map<String, String> computeJointNameToNodeIdMap(GlTF gltf) {
      Map<String, String> map = new LinkedHashMap<>();
      Map<String, Node> nodes = Optionals.of(gltf.getNodes());

      for (Entry<String, Node> entry : nodes.entrySet()) {
         String nodeId = entry.getKey();
         Node node = entry.getValue();
         if (node.getJointName() != null) {
            String oldNodeId = map.put(node.getJointName(), nodeId);
            if (oldNodeId != null) {
               logger.warning("Joint name " + node.getJointName() + " is mapped to nodes with IDs " + nodeId + " and " + oldNodeId);
            }
         }
      }

      return map;
   }

   private void initSkinModels() {
      Map<String, String> jointNameToNodeIdMap = computeJointNameToNodeIdMap(this.gltf);
      Map<String, Skin> skins = Optionals.of(this.gltf.getSkins());

      for (Entry<String, Skin> entry : skins.entrySet()) {
         String skinId = entry.getKey();
         Skin skin = entry.getValue();
         DefaultSkinModel skinModel = this.get("skins", skinId, this.gltfModel::getSkinModel);
         transferGltfChildOfRootPropertyElements(skin, skinModel);

         for (String jointName : skin.getJointNames()) {
            String nodeId = jointNameToNodeIdMap.get(jointName);
            NodeModel nodeModel = this.get("nodes", nodeId, this.gltfModel::getNodeModel);
            skinModel.addJoint(nodeModel);
         }

         String inverseBindMatricesId = skin.getInverseBindMatrices();
         AccessorModel inverseBindMatrices = this.get("accessors", inverseBindMatricesId, this.gltfModel::getAccessorModel);
         skinModel.setInverseBindMatrices(inverseBindMatrices);
      }
   }

   private void initTextureModels() {
      Map<String, Texture> textures = Optionals.of(this.gltf.getTextures());

      for (Entry<String, Texture> entry : textures.entrySet()) {
         String textureId = entry.getKey();
         Texture texture = entry.getValue();
         DefaultTextureModel textureModel = this.get("textures", textureId, this.gltfModel::getTextureModel);
         transferGltfChildOfRootPropertyElements(texture, textureModel);
         String imageId = texture.getSource();
         DefaultImageModel imageModel = this.get("images", imageId, this.gltfModel::getImageModel);
         textureModel.setImageModel(imageModel);
      }
   }

   private void initShaderModels() {
      Map<String, Shader> shaders = Optionals.of(this.gltf.getShaders());

      for (Entry<String, Shader> entry : shaders.entrySet()) {
         String shaderId = entry.getKey();
         Shader shader = entry.getValue();
         DefaultShaderModel shaderModel = this.get("shaders", shaderId, this.gltfModel::getShaderModel);
         transferGltfChildOfRootPropertyElements(shader, shaderModel);
         if (BinaryGltfV1.hasBinaryGltfExtension(shader)) {
            String bufferViewId = BinaryGltfV1.getBinaryGltfBufferViewId(shader);
            BufferViewModel bufferViewModel = this.get("bufferViews", bufferViewId, this.gltfModel::getBufferViewModel);
            shaderModel.setBufferViewModel(bufferViewModel);
         } else {
            String uri = shader.getUri();
            if (IO.isDataUriString(uri)) {
               byte[] data = IO.readDataUri(uri);
               ByteBuffer shaderData = Buffers.create(data);
               shaderModel.setShaderData(shaderData);
            } else {
               ByteBuffer shaderData = this.gltfAsset.getReferenceData(uri);
               shaderModel.setShaderData(shaderData);
            }
         }
      }
   }

   void initProgramModels() {
      Map<String, Program> programs = Optionals.of(this.gltf.getPrograms());

      for (Entry<String, Program> entry : programs.entrySet()) {
         String programId = entry.getKey();
         Program program = entry.getValue();
         DefaultProgramModel programModel = this.get("programs", programId, this.gltfModel::getProgramModel);
         transferGltfChildOfRootPropertyElements(program, programModel);
         String vertexShaderId = program.getVertexShader();
         DefaultShaderModel vertexShaderModel = this.get("shaders", vertexShaderId, this.gltfModel::getShaderModel);
         programModel.setVertexShaderModel(vertexShaderModel);
         String fragmentShaderId = program.getFragmentShader();
         DefaultShaderModel fragmentShaderModel = this.get("shaders", fragmentShaderId, this.gltfModel::getShaderModel);
         programModel.setFragmentShaderModel(fragmentShaderModel);

         for (String attribute : Optionals.of(program.getAttributes())) {
            programModel.addAttribute(attribute);
         }
      }
   }

   private static void addParameters(Technique technique, DefaultTechniqueModel techniqueModel, Function<? super String, ? extends NodeModel> nodeLookup) {
      Map<String, TechniqueParameters> parameters = Optionals.of(technique.getParameters());

      for (Entry<String, TechniqueParameters> entry : parameters.entrySet()) {
         String parameterName = entry.getKey();
         TechniqueParameters parameter = entry.getValue();
         int type = parameter.getType();
         int count = Optionals.of(parameter.getCount(), 1);
         String semantic = parameter.getSemantic();
         Object value = parameter.getValue();
         String nodeId = parameter.getNode();
         NodeModel nodeModel = null;
         if (nodeId != null) {
            if (nodeLookup == null) {
               logger.severe("No lookup function found for the nodes");
            } else {
               nodeModel = nodeLookup.apply(nodeId);
            }
         }

         TechniqueParametersModel techniqueParametersModel = new DefaultTechniqueParametersModel(type, count, semantic, value, nodeModel);
         techniqueModel.addParameter(parameterName, techniqueParametersModel);
      }
   }

   private static void addAttributes(Technique technique, DefaultTechniqueModel techniqueModel) {
      Map<String, String> attributes = Optionals.of(technique.getAttributes());

      for (Entry<String, String> entry : attributes.entrySet()) {
         String attributeName = entry.getKey();
         String parameterName = entry.getValue();
         techniqueModel.addAttribute(attributeName, parameterName);
      }
   }

   private static void addUniforms(Technique technique, DefaultTechniqueModel techniqueModel) {
      Map<String, String> uniforms = Optionals.of(technique.getUniforms());

      for (Entry<String, String> entry : uniforms.entrySet()) {
         String uniformName = entry.getKey();
         String parameterName = entry.getValue();
         techniqueModel.addUniform(uniformName, parameterName);
      }
   }

   private void initTechniqueModels() {
      Map<String, Technique> techniques = Optionals.of(this.gltf.getTechniques());

      for (Entry<String, Technique> entry : techniques.entrySet()) {
         String techniqueId = entry.getKey();
         Technique technique = entry.getValue();
         DefaultTechniqueModel techniqueModel = this.get("techniques", techniqueId, this.gltfModel::getTechniqueModel);
         String programId = technique.getProgram();
         DefaultProgramModel programModel = this.get("programs", programId, this.gltfModel::getProgramModel);
         techniqueModel.setProgramModel(programModel);
         Function<String, NodeModel> nodeLookup = nodeId -> (DefaultNodeModel)this.get("nodes", nodeId, this.gltfModel::getNodeModel);
         initTechniqueModel(techniqueModel, technique, nodeLookup);
      }
   }

   public static void initTechniqueModel(DefaultTechniqueModel techniqueModel, Technique technique, Function<? super String, ? extends NodeModel> nodeLookup) {
      transferGltfChildOfRootPropertyElements(technique, techniqueModel);
      addParameters(technique, techniqueModel, nodeLookup);
      addAttributes(technique, techniqueModel);
      addUniforms(technique, techniqueModel);
      List<Integer> enableModel = null;
      DefaultTechniqueStatesFunctionsModel techniqueStatesFunctionsModel = null;
      TechniqueStates states = technique.getStates();
      if (states != null) {
         List<Integer> enable = states.getEnable();
         if (enable != null) {
            enableModel = new ArrayList<>(enable);
         }

         TechniqueStatesFunctions functions = states.getFunctions();
         if (functions != null) {
            techniqueStatesFunctionsModel = TechniqueStatesFunctionsModels.create(functions);
         }

         TechniqueStatesModel techniqueStatesModel = new DefaultTechniqueStatesModel(enableModel, techniqueStatesFunctionsModel);
         techniqueModel.setTechniqueStatesModel(techniqueStatesModel);
      }
   }

   private void initMaterialModels() {
      Map<String, Material> materials = Optionals.of(this.gltf.getMaterials());

      for (Entry<String, Material> entry : materials.entrySet()) {
         String materialId = entry.getKey();
         Material material = entry.getValue();
         MaterialModelV1 materialModel = (MaterialModelV1)this.get("materials", materialId, this.gltfModel::getMaterialModel);
         transferGltfChildOfRootPropertyElements(material, materialModel);
         String techniqueId = material.getTechnique();
         TechniqueModel techniqueModel;
         if (techniqueId != null && !GltfDefaults.isDefaultTechniqueId(techniqueId)) {
            techniqueModel = this.get("techniques", techniqueId, this.gltfModel::getTechniqueModel);
         } else {
            techniqueModel = DefaultModels.getDefaultTechniqueModel();
         }

         materialModel.setTechniqueModel(techniqueModel);
         Map<String, Object> modelValues = new LinkedHashMap<>();
         Map<String, Object> values = Optionals.of(material.getValues());

         for (Entry<String, Object> valueEntry : values.entrySet()) {
            String parameterName = valueEntry.getKey();
            TechniqueParametersModel techniqueParametersModel = techniqueModel.getParameters().get(parameterName);
            if (techniqueParametersModel != null && techniqueParametersModel.getType() == 35678) {
               TextureModel textureModel = null;
               Object value = valueEntry.getValue();
               if (value != null) {
                  String textureId = String.valueOf(value);
                  textureModel = this.get("textures", textureId, this.gltfModel::getTextureModel);
               }

               modelValues.put(parameterName, textureModel);
            } else {
               modelValues.put(parameterName, valueEntry.getValue());
            }
         }

         materialModel.setValues(modelValues);
      }
   }

   private void initExtensionsModel() {
      List<String> extensionsUsed = this.gltf.getExtensionsUsed();
      DefaultExtensionsModel extensionsModel = this.gltfModel.getExtensionsModel();
      extensionsModel.addExtensionsUsed(extensionsUsed);
   }

   private void initAssetModel() {
      Asset asset = this.gltf.getAsset();
      if (asset != null) {
         DefaultAssetModel assetModel = this.gltfModel.getAssetModel();
         transferGltfPropertyElements(asset, assetModel);
         assetModel.setCopyright(asset.getCopyright());
         assetModel.setGenerator(asset.getGenerator());
      }
   }

   private static void transferGltfPropertyElements(GlTFProperty property, AbstractModelElement modelElement) {
      modelElement.setExtensions(property.getExtensions());
      modelElement.setExtras(property.getExtras());
   }

   private static void transferGltfChildOfRootPropertyElements(GlTFChildOfRootProperty property, AbstractNamedModelElement modelElement) {
      modelElement.setName(property.getName());
      transferGltfPropertyElements(property, modelElement);
   }

   private <T> T get(String name, String id, IntFunction<? extends T> getter) {
      Integer index = this.indexMappingSet.getIndex(name, id);
      if (index == null) {
         logger.severe("No index found for " + name + " ID " + id);
         return null;
      } else {
         return (T)getter.apply(index);
      }
   }
}
