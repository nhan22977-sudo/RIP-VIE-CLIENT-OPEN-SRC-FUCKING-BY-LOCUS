package de.javagl.jgltf.model.v2;

import de.javagl.jgltf.impl.v2.Accessor;
import de.javagl.jgltf.impl.v2.AccessorSparse;
import de.javagl.jgltf.impl.v2.AccessorSparseIndices;
import de.javagl.jgltf.impl.v2.AccessorSparseValues;
import de.javagl.jgltf.impl.v2.Animation;
import de.javagl.jgltf.impl.v2.AnimationChannel;
import de.javagl.jgltf.impl.v2.AnimationChannelTarget;
import de.javagl.jgltf.impl.v2.AnimationSampler;
import de.javagl.jgltf.impl.v2.Asset;
import de.javagl.jgltf.impl.v2.Buffer;
import de.javagl.jgltf.impl.v2.BufferView;
import de.javagl.jgltf.impl.v2.Camera;
import de.javagl.jgltf.impl.v2.CameraOrthographic;
import de.javagl.jgltf.impl.v2.CameraPerspective;
import de.javagl.jgltf.impl.v2.GlTF;
import de.javagl.jgltf.impl.v2.GlTFChildOfRootProperty;
import de.javagl.jgltf.impl.v2.GlTFProperty;
import de.javagl.jgltf.impl.v2.Image;
import de.javagl.jgltf.impl.v2.Material;
import de.javagl.jgltf.impl.v2.MaterialNormalTextureInfo;
import de.javagl.jgltf.impl.v2.MaterialOcclusionTextureInfo;
import de.javagl.jgltf.impl.v2.MaterialPbrMetallicRoughness;
import de.javagl.jgltf.impl.v2.Mesh;
import de.javagl.jgltf.impl.v2.MeshPrimitive;
import de.javagl.jgltf.impl.v2.Node;
import de.javagl.jgltf.impl.v2.Sampler;
import de.javagl.jgltf.impl.v2.Scene;
import de.javagl.jgltf.impl.v2.Skin;
import de.javagl.jgltf.impl.v2.Texture;
import de.javagl.jgltf.impl.v2.TextureInfo;
import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.ElementType;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.TextureModel;
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
import de.javagl.jgltf.model.impl.DefaultGltfModel;
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
import de.javagl.jgltf.model.io.v2.GltfAssetV2;
import de.javagl.jgltf.model.v2.gl.Materials;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class GltfModelCreatorV2 {
   private static final Logger logger = Logger.getLogger(GltfModelCreatorV2.class.getName());
   private final GltfAsset gltfAsset;
   private final GlTF gltf;
   private final DefaultGltfModel gltfModel;

   public static DefaultGltfModel create(GltfAssetV2 gltfAsset) {
      DefaultGltfModel gltfModel = new DefaultGltfModel();
      GltfModelCreatorV2 creator = new GltfModelCreatorV2(gltfAsset, gltfModel);
      creator.create();
      return gltfModel;
   }

   GltfModelCreatorV2(GltfAssetV2 gltfAsset, DefaultGltfModel gltfModel) {
      this.gltfAsset = Objects.requireNonNull(gltfAsset, "The gltfAsset may not be null");
      this.gltf = gltfAsset.getGltf();
      this.gltfModel = Objects.requireNonNull(gltfModel, "The gltfModel may not be null");
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
      this.initBufferModels();
      this.initBufferViewModels();
      this.initAccessorModels();
      this.initAnimationModels();
      this.initImageModels();
      this.initMeshModels();
      this.initNodeModels();
      this.initSceneModels();
      this.initSkinModels();
      this.initTextureModels();
      this.initMaterialModels();
      this.initExtensionsModel();
      this.initAssetModel();
   }

   private void createAccessorModels() {
      List<Accessor> accessors = Optionals.of(this.gltf.getAccessors());

      for (int i = 0; i < accessors.size(); i++) {
         Accessor accessor = accessors.get(i);
         Integer componentType = accessor.getComponentType();
         Integer count = accessor.getCount();
         ElementType elementType = ElementType.forString(accessor.getType());
         DefaultAccessorModel accessorModel = new DefaultAccessorModel(componentType, count, elementType);
         this.gltfModel.addAccessorModel(accessorModel);
      }
   }

   private void createAnimationModels() {
      List<Animation> animations = Optionals.of(this.gltf.getAnimations());

      for (int i = 0; i < animations.size(); i++) {
         this.gltfModel.addAnimationModel(new DefaultAnimationModel());
      }
   }

   private void createBufferModels() {
      List<Buffer> buffers = Optionals.of(this.gltf.getBuffers());

      for (int i = 0; i < buffers.size(); i++) {
         Buffer buffer = buffers.get(i);
         DefaultBufferModel bufferModel = new DefaultBufferModel();
         bufferModel.setUri(buffer.getUri());
         this.gltfModel.addBufferModel(bufferModel);
      }
   }

   private void createCameraModels() {
      List<Camera> cameras = Optionals.of(this.gltf.getCameras());

      for (int i = 0; i < cameras.size(); i++) {
         Camera camera = cameras.get(i);
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

   private void createBufferViewModels() {
      List<BufferView> bufferViews = Optionals.of(this.gltf.getBufferViews());

      for (int i = 0; i < bufferViews.size(); i++) {
         BufferView bufferView = bufferViews.get(i);
         DefaultBufferViewModel bufferViewModel = createBufferViewModel(bufferView);
         this.gltfModel.addBufferViewModel(bufferViewModel);
      }
   }

   private static DefaultBufferViewModel createBufferViewModel(BufferView bufferView) {
      int byteOffset = Optionals.of(bufferView.getByteOffset(), 0);
      int byteLength = bufferView.getByteLength();
      Integer byteStride = bufferView.getByteStride();
      Integer target = bufferView.getTarget();
      DefaultBufferViewModel bufferViewModel = new DefaultBufferViewModel(target);
      bufferViewModel.setByteOffset(byteOffset);
      bufferViewModel.setByteLength(byteLength);
      bufferViewModel.setByteStride(byteStride);
      return bufferViewModel;
   }

   private void createImageModels() {
      List<Image> images = Optionals.of(this.gltf.getImages());

      for (int i = 0; i < images.size(); i++) {
         Image image = images.get(i);
         String mimeType = image.getMimeType();
         DefaultImageModel imageModel = new DefaultImageModel();
         imageModel.setMimeType(mimeType);
         String uri = image.getUri();
         imageModel.setUri(uri);
         this.gltfModel.addImageModel(imageModel);
      }
   }

   private void createMaterialModels() {
      List<Material> materials = Optionals.of(this.gltf.getMaterials());

      for (int i = 0; i < materials.size(); i++) {
         MaterialModelV2 materialModel = new MaterialModelV2();
         this.gltfModel.addMaterialModel(materialModel);
      }
   }

   private void createMeshModels() {
      List<Mesh> meshes = Optionals.of(this.gltf.getMeshes());

      for (int i = 0; i < meshes.size(); i++) {
         this.gltfModel.addMeshModel(new DefaultMeshModel());
      }
   }

   private void createNodeModels() {
      List<Node> nodes = Optionals.of(this.gltf.getNodes());

      for (int i = 0; i < nodes.size(); i++) {
         this.gltfModel.addNodeModel(new DefaultNodeModel());
      }
   }

   private void createSceneModels() {
      List<Scene> scenes = Optionals.of(this.gltf.getScenes());

      for (int i = 0; i < scenes.size(); i++) {
         this.gltfModel.addSceneModel(new DefaultSceneModel());
      }
   }

   private void createSkinModels() {
      List<Skin> skins = Optionals.of(this.gltf.getSkins());

      for (int i = 0; i < skins.size(); i++) {
         this.gltfModel.addSkinModel(new DefaultSkinModel());
      }
   }

   private void createTextureModels() {
      List<Texture> textures = Optionals.of(this.gltf.getTextures());
      List<Sampler> samplers = Optionals.of(this.gltf.getSamplers());

      for (int i = 0; i < textures.size(); i++) {
         Texture texture = textures.get(i);
         Integer samplerIndex = texture.getSampler();
         Integer magFilter = 9729;
         Integer minFilter = 9729;
         Integer wrapS = 10497;
         Integer wrapT = 10497;
         if (samplerIndex != null) {
            Sampler sampler = samplers.get(samplerIndex);
            magFilter = sampler.getMagFilter();
            minFilter = sampler.getMinFilter();
            wrapS = Optionals.of(sampler.getWrapS(), sampler.defaultWrapS());
            wrapT = Optionals.of(sampler.getWrapT(), sampler.defaultWrapT());
         }

         DefaultTextureModel textureModel = new DefaultTextureModel();
         textureModel.setMagFilter(magFilter);
         textureModel.setMinFilter(minFilter);
         textureModel.setWrapS(wrapS);
         textureModel.setWrapT(wrapT);
         this.gltfModel.addTextureModel(textureModel);
      }
   }

   private void initAccessorModels() {
      List<Accessor> accessors = Optionals.of(this.gltf.getAccessors());

      for (int i = 0; i < accessors.size(); i++) {
         Accessor accessor = accessors.get(i);
         DefaultAccessorModel accessorModel = this.gltfModel.getAccessorModel(i);
         transferGltfChildOfRootPropertyElements(accessor, accessorModel);
         int byteOffset = Optionals.of(accessor.getByteOffset(), 0);
         accessorModel.setByteOffset(byteOffset);
         Boolean normalized = accessor.isNormalized();
         accessorModel.setNormalized(Boolean.TRUE.equals(normalized));
         AccessorSparse accessorSparse = accessor.getSparse();
         if (accessorSparse == null) {
            this.initDenseAccessorModel(i, accessor, accessorModel);
         } else {
            this.initSparseAccessorModel(i, accessor, accessorModel);
         }
      }
   }

   private void initDenseAccessorModel(int accessorIndex, Accessor accessor, DefaultAccessorModel accessorModel) {
      Integer bufferViewIndex = accessor.getBufferView();
      if (bufferViewIndex != null) {
         BufferViewModel bufferViewModel = this.gltfModel.getBufferViewModel(bufferViewIndex);
         accessorModel.setBufferViewModel(bufferViewModel);
         Integer byteStride = bufferViewModel.getByteStride();
         if (byteStride != null) {
            accessorModel.setByteStride(byteStride);
         }

         accessorModel.setAccessorData(AccessorDatas.create(accessorModel));
      } else {
         int count = accessorModel.getCount();
         int elementSizeInBytes = accessorModel.getPaddedElementSizeInBytes();
         int byteLength = elementSizeInBytes * count;
         ByteBuffer bufferData = Buffers.create(byteLength);
         String uriString = "buffer_for_accessor" + accessorIndex + ".bin";
         BufferViewModel bufferViewModel = createBufferViewModel(uriString, bufferData);
         accessorModel.setBufferViewModel(bufferViewModel);
         accessorModel.setAccessorData(AccessorDatas.create(accessorModel));
      }
   }

   private void initSparseAccessorModel(int accessorIndex, Accessor accessor, DefaultAccessorModel accessorModel) {
      int count = accessorModel.getCount();
      int elementSizeInBytes = accessorModel.getPaddedElementSizeInBytes();
      int byteLength = elementSizeInBytes * count;
      ByteBuffer bufferData = Buffers.create(byteLength);
      String uriString = "buffer_for_accessor" + accessorIndex + ".bin";
      DefaultBufferViewModel denseBufferViewModel = createBufferViewModel(uriString, bufferData);
      accessorModel.setBufferViewModel(denseBufferViewModel);
      accessorModel.setByteOffset(0);
      Integer bufferViewIndex = accessor.getBufferView();
      if (bufferViewIndex != null) {
         Consumer<ByteBuffer> sparseSubstitutionCallback = denseByteBuffer -> {
            logger.fine("Substituting sparse accessor data, based on existing buffer view");
            DefaultBufferViewModel baseBufferViewModel = this.gltfModel.getBufferViewModel(bufferViewIndex);
            ByteBuffer baseBufferViewData = baseBufferViewModel.getBufferViewData();
            AccessorData baseAccessorData = AccessorDatas.create(accessorModel, baseBufferViewData);
            AccessorData denseAccessorData = AccessorDatas.create(accessorModel, bufferData);
            this.substituteSparseAccessorData(accessor, accessorModel, denseAccessorData, baseAccessorData);
         };
         denseBufferViewModel.setSparseSubstitutionCallback(sparseSubstitutionCallback);
      } else {
         Consumer<ByteBuffer> sparseSubstitutionCallback = denseByteBuffer -> {
            logger.fine("Substituting sparse accessor data, without an existing buffer view");
            AccessorData denseAccessorData = AccessorDatas.create(accessorModel, bufferData);
            this.substituteSparseAccessorData(accessor, accessorModel, denseAccessorData, null);
         };
         denseBufferViewModel.setSparseSubstitutionCallback(sparseSubstitutionCallback);
      }
   }

   private static DefaultBufferViewModel createBufferViewModel(String uriString, ByteBuffer bufferData) {
      DefaultBufferModel bufferModel = new DefaultBufferModel();
      bufferModel.setUri(uriString);
      bufferModel.setBufferData(bufferData);
      DefaultBufferViewModel bufferViewModel = new DefaultBufferViewModel(null);
      bufferViewModel.setByteOffset(0);
      bufferViewModel.setByteLength(bufferData.capacity());
      bufferViewModel.setBufferModel(bufferModel);
      return bufferViewModel;
   }

   private void substituteSparseAccessorData(Accessor accessor, AccessorModel accessorModel, AccessorData denseAccessorData, AccessorData baseAccessorData) {
      AccessorSparse accessorSparse = accessor.getSparse();
      int count = accessorSparse.getCount();
      AccessorSparseIndices accessorSparseIndices = accessorSparse.getIndices();
      AccessorData sparseIndicesAccessorData = this.createSparseIndicesAccessorData(accessorSparseIndices, count);
      AccessorSparseValues accessorSparseValues = accessorSparse.getValues();
      ElementType elementType = accessorModel.getElementType();
      AccessorData sparseValuesAccessorData = this.createSparseValuesAccessorData(accessorSparseValues, accessorModel.getComponentType(), elementType, count);
      AccessorSparseUtils.substituteAccessorData(denseAccessorData, baseAccessorData, sparseIndicesAccessorData, sparseValuesAccessorData);
   }

   private AccessorData createSparseIndicesAccessorData(AccessorSparseIndices accessorSparseIndices, int count) {
      Integer componentType = accessorSparseIndices.getComponentType();
      Integer bufferViewIndex = accessorSparseIndices.getBufferView();
      BufferViewModel bufferViewModel = this.gltfModel.getBufferViewModel(bufferViewIndex);
      ByteBuffer bufferViewData = bufferViewModel.getBufferViewData();
      int byteOffset = Optionals.of(accessorSparseIndices.getByteOffset(), 0);
      return AccessorDatas.create(componentType, bufferViewData, byteOffset, count, ElementType.SCALAR, null);
   }

   private AccessorData createSparseValuesAccessorData(AccessorSparseValues accessorSparseValues, int componentType, ElementType elementType, int count) {
      Integer bufferViewIndex = accessorSparseValues.getBufferView();
      BufferViewModel bufferViewModel = this.gltfModel.getBufferViewModel(bufferViewIndex);
      ByteBuffer bufferViewData = bufferViewModel.getBufferViewData();
      int byteOffset = Optionals.of(accessorSparseValues.getByteOffset(), 0);
      return AccessorDatas.create(componentType, bufferViewData, byteOffset, count, elementType, null);
   }

   private void initAnimationModels() {
      List<Animation> animations = Optionals.of(this.gltf.getAnimations());

      for (int i = 0; i < animations.size(); i++) {
         Animation animation = animations.get(i);
         DefaultAnimationModel animationModel = this.gltfModel.getAnimationModel(i);
         transferGltfChildOfRootPropertyElements(animation, animationModel);

         for (AnimationChannel animationChannel : Optionals.of(animation.getChannels())) {
            AnimationModel.Channel channel = this.createChannel(animation, animationChannel);
            animationModel.addChannel(channel);
         }
      }
   }

   private AnimationModel.Channel createChannel(Animation animation, AnimationChannel animationChannel) {
      List<AnimationSampler> samplers = Optionals.of(animation.getSamplers());
      int samplerIndex = animationChannel.getSampler();
      AnimationSampler animationSampler = samplers.get(samplerIndex);
      int inputAccessorIndex = animationSampler.getInput();
      AccessorModel inputAccessorModel = this.gltfModel.getAccessorModel(inputAccessorIndex);
      int outputAccessorIndex = animationSampler.getOutput();
      AccessorModel outputAccessorModel = this.gltfModel.getAccessorModel(outputAccessorIndex);
      String interpolationString = animationSampler.getInterpolation();
      AnimationModel.Interpolation interpolation = interpolationString == null
         ? AnimationModel.Interpolation.LINEAR
         : AnimationModel.Interpolation.valueOf(interpolationString);
      AnimationModel.Sampler sampler = new DefaultAnimationModel.DefaultSampler(inputAccessorModel, interpolation, outputAccessorModel);
      AnimationChannelTarget animationChannelTarget = animationChannel.getTarget();
      Integer nodeIndex = animationChannelTarget.getNode();
      NodeModel nodeModel = null;
      if (nodeIndex == null) {
         logger.warning("No node index given for animation channel target");
      } else {
         nodeModel = this.gltfModel.getNodeModel(nodeIndex);
      }

      String path = animationChannelTarget.getPath();
      return new DefaultAnimationModel.DefaultChannel(sampler, nodeModel, path);
   }

   private void initBufferModels() {
      List<Buffer> buffers = Optionals.of(this.gltf.getBuffers());
      ByteBuffer binaryData = null;
      ByteBuffer b = this.gltfAsset.getBinaryData();
      if (b != null && b.capacity() > 0) {
         binaryData = b;
      }

      if (buffers.isEmpty() && binaryData != null) {
         logger.warning("Binary data was given, but no buffers");
      } else {
         for (int i = 0; i < buffers.size(); i++) {
            Buffer buffer = buffers.get(i);
            DefaultBufferModel bufferModel = this.gltfModel.getBufferModel(i);
            transferGltfChildOfRootPropertyElements(buffer, bufferModel);
            if (i == 0 && binaryData != null) {
               bufferModel.setBufferData(binaryData);
            } else {
               String uri = buffer.getUri();
               if (IO.isDataUriString(uri)) {
                  byte[] data = IO.readDataUri(uri);
                  ByteBuffer bufferData = Buffers.create(data);
                  bufferModel.setBufferData(bufferData);
               } else if (uri == null) {
                  logger.warning("Buffer " + i + " does not have a uri. Binary chunks that are not the main GLB buffer are not supported.");
               } else {
                  ByteBuffer bufferData = this.gltfAsset.getReferenceData(uri);
                  bufferModel.setBufferData(bufferData);
               }
            }
         }
      }
   }

   private void initBufferViewModels() {
      List<BufferView> bufferViews = Optionals.of(this.gltf.getBufferViews());

      for (int i = 0; i < bufferViews.size(); i++) {
         BufferView bufferView = bufferViews.get(i);
         DefaultBufferViewModel bufferViewModel = this.gltfModel.getBufferViewModel(i);
         transferGltfChildOfRootPropertyElements(bufferView, bufferViewModel);
         int bufferIndex = bufferView.getBuffer();
         BufferModel bufferModel = this.gltfModel.getBufferModel(bufferIndex);
         bufferViewModel.setBufferModel(bufferModel);
      }
   }

   private void initMeshModels() {
      List<Mesh> meshes = Optionals.of(this.gltf.getMeshes());

      for (int i = 0; i < meshes.size(); i++) {
         Mesh mesh = meshes.get(i);
         DefaultMeshModel meshModel = this.gltfModel.getMeshModel(i);
         transferGltfChildOfRootPropertyElements(mesh, meshModel);

         for (MeshPrimitive meshPrimitive : Optionals.of(mesh.getPrimitives())) {
            MeshPrimitiveModel meshPrimitiveModel = this.createMeshPrimitiveModel(meshPrimitive);
            meshModel.addMeshPrimitiveModel(meshPrimitiveModel);
         }

         meshModel.setWeights(toArray(mesh.getWeights()));
      }
   }

   private DefaultMeshPrimitiveModel createMeshPrimitiveModel(MeshPrimitive meshPrimitive) {
      Integer mode = Optionals.of(meshPrimitive.getMode(), meshPrimitive.defaultMode());
      DefaultMeshPrimitiveModel meshPrimitiveModel = new DefaultMeshPrimitiveModel(mode);
      transferGltfPropertyElements(meshPrimitive, meshPrimitiveModel);
      Integer indicesIndex = meshPrimitive.getIndices();
      if (indicesIndex != null) {
         AccessorModel indices = this.gltfModel.getAccessorModel(indicesIndex);
         meshPrimitiveModel.setIndices(indices);
      }

      Map<String, Integer> attributes = Optionals.of(meshPrimitive.getAttributes());

      for (Entry<String, Integer> entry : attributes.entrySet()) {
         String attributeName = entry.getKey();
         int attributeIndex = entry.getValue();
         AccessorModel attribute = this.gltfModel.getAccessorModel(attributeIndex);
         meshPrimitiveModel.putAttribute(attributeName, attribute);
      }

      for (Map<String, Integer> morphTarget : Optionals.of(meshPrimitive.getTargets())) {
         Map<String, AccessorModel> morphTargetModel = new LinkedHashMap<>();

         for (Entry<String, Integer> entry : morphTarget.entrySet()) {
            String attribute = entry.getKey();
            Integer accessorIndex = entry.getValue();
            AccessorModel accessorModel = this.gltfModel.getAccessorModel(accessorIndex);
            morphTargetModel.put(attribute, accessorModel);
         }

         meshPrimitiveModel.addTarget(morphTargetModel);
      }

      Integer materialIndex = meshPrimitive.getMaterial();
      if (materialIndex != null) {
         MaterialModelV2 materialModel = (MaterialModelV2)this.gltfModel.getMaterialModel(materialIndex);
         meshPrimitiveModel.setMaterialModel(materialModel);
      }

      return meshPrimitiveModel;
   }

   private void initNodeModels() {
      List<Node> nodes = Optionals.of(this.gltf.getNodes());

      for (int i = 0; i < nodes.size(); i++) {
         Node node = nodes.get(i);
         DefaultNodeModel nodeModel = this.gltfModel.getNodeModel(i);
         transferGltfChildOfRootPropertyElements(node, nodeModel);

         for (Integer childIndex : Optionals.of(node.getChildren())) {
            DefaultNodeModel child = this.gltfModel.getNodeModel(childIndex);
            nodeModel.addChild(child);
         }

         Integer meshIndex = node.getMesh();
         if (meshIndex != null) {
            MeshModel meshModel = this.gltfModel.getMeshModel(meshIndex);
            nodeModel.addMeshModel(meshModel);
         }

         Integer skinIndex = node.getSkin();
         if (skinIndex != null) {
            SkinModel skinModel = this.gltfModel.getSkinModel(skinIndex);
            nodeModel.setSkinModel(skinModel);
         }

         Integer cameraIndex = node.getCamera();
         if (cameraIndex != null) {
            CameraModel cameraModel = this.gltfModel.getCameraModel(cameraIndex);
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
         nodeModel.setWeights(toArray(node.getWeights()));
      }
   }

   private void initSceneModels() {
      List<Scene> scenes = Optionals.of(this.gltf.getScenes());

      for (int i = 0; i < scenes.size(); i++) {
         Scene scene = scenes.get(i);
         DefaultSceneModel sceneModel = this.gltfModel.getSceneModel(i);
         transferGltfChildOfRootPropertyElements(scene, sceneModel);

         for (Integer nodeIndex : Optionals.of(scene.getNodes())) {
            NodeModel nodeModel = this.gltfModel.getNodeModel(nodeIndex);
            sceneModel.addNode(nodeModel);
         }
      }
   }

   private void initSkinModels() {
      List<Skin> skins = Optionals.of(this.gltf.getSkins());

      for (int i = 0; i < skins.size(); i++) {
         Skin skin = skins.get(i);
         DefaultSkinModel skinModel = this.gltfModel.getSkinModel(i);
         transferGltfChildOfRootPropertyElements(skin, skinModel);

         for (Integer jointIndex : skin.getJoints()) {
            NodeModel jointNodeModel = this.gltfModel.getNodeModel(jointIndex);
            skinModel.addJoint(jointNodeModel);
         }

         Integer inverseBindMatricesIndex = skin.getInverseBindMatrices();
         AccessorModel inverseBindMatrices = this.gltfModel.getAccessorModel(inverseBindMatricesIndex);
         skinModel.setInverseBindMatrices(inverseBindMatrices);
      }
   }

   private void initTextureModels() {
      List<Texture> textures = Optionals.of(this.gltf.getTextures());

      for (int i = 0; i < textures.size(); i++) {
         Texture texture = textures.get(i);
         DefaultTextureModel textureModel = this.gltfModel.getTextureModel(i);
         transferGltfChildOfRootPropertyElements(texture, textureModel);
         Integer imageIndex = texture.getSource();
         if (imageIndex != null) {
            DefaultImageModel imageModel = this.gltfModel.getImageModel(imageIndex);
            textureModel.setImageModel(imageModel);
         }
      }
   }

   private void initImageModels() {
      List<Image> images = Optionals.of(this.gltf.getImages());

      for (int i = 0; i < images.size(); i++) {
         Image image = images.get(i);
         DefaultImageModel imageModel = this.gltfModel.getImageModel(i);
         transferGltfChildOfRootPropertyElements(image, imageModel);
         Integer bufferViewIndex = image.getBufferView();
         if (bufferViewIndex != null) {
            BufferViewModel bufferViewModel = this.gltfModel.getBufferViewModel(bufferViewIndex);
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

   private void initMaterialModels() {
      List<Material> materials = Optionals.of(this.gltf.getMaterials());

      for (int i = 0; i < materials.size(); i++) {
         Material material = materials.get(i);
         MaterialModelV2 materialModel = (MaterialModelV2)this.gltfModel.getMaterialModel(i);
         transferGltfChildOfRootPropertyElements(material, materialModel);
         this.initMaterialModel(materialModel, material);
      }
   }

   private void initMaterialModel(MaterialModelV2 materialModel, Material material) {
      MaterialPbrMetallicRoughness pbrMetallicRoughness = material.getPbrMetallicRoughness();
      if (pbrMetallicRoughness == null) {
         pbrMetallicRoughness = Materials.createDefaultMaterialPbrMetallicRoughness();
      }

      String alphaModeString = material.getAlphaMode();
      if (alphaModeString != null) {
         materialModel.setAlphaMode(MaterialModelV2.AlphaMode.valueOf(alphaModeString));
      }

      materialModel.setAlphaCutoff(Optionals.of(material.getAlphaCutoff(), 0.5F));
      materialModel.setDoubleSided(Boolean.TRUE.equals(material.isDoubleSided()));
      TextureInfo baseColorTextureInfo = pbrMetallicRoughness.getBaseColorTexture();
      if (baseColorTextureInfo != null) {
         int index = baseColorTextureInfo.getIndex();
         TextureModel textureModel = this.gltfModel.getTextureModel(index);
         materialModel.setBaseColorTexture(textureModel);
         materialModel.setBaseColorTexcoord(baseColorTextureInfo.getTexCoord());
      }

      float[] baseColorFactor = Optionals.of(pbrMetallicRoughness.getBaseColorFactor(), pbrMetallicRoughness.defaultBaseColorFactor());
      materialModel.setBaseColorFactor(baseColorFactor);
      TextureInfo metallicRoughnessTextureInfo = pbrMetallicRoughness.getMetallicRoughnessTexture();
      if (metallicRoughnessTextureInfo != null) {
         int index = metallicRoughnessTextureInfo.getIndex();
         TextureModel textureModel = this.gltfModel.getTextureModel(index);
         materialModel.setMetallicRoughnessTexture(textureModel);
         materialModel.setMetallicRoughnessTexcoord(metallicRoughnessTextureInfo.getTexCoord());
      }

      float metallicFactor = Optionals.of(pbrMetallicRoughness.getMetallicFactor(), pbrMetallicRoughness.defaultMetallicFactor());
      materialModel.setMetallicFactor(metallicFactor);
      float roughnessFactor = Optionals.of(pbrMetallicRoughness.getRoughnessFactor(), pbrMetallicRoughness.defaultRoughnessFactor());
      materialModel.setRoughnessFactor(roughnessFactor);
      MaterialNormalTextureInfo normalTextureInfo = material.getNormalTexture();
      if (normalTextureInfo != null) {
         int index = normalTextureInfo.getIndex();
         TextureModel textureModel = this.gltfModel.getTextureModel(index);
         materialModel.setNormalTexture(textureModel);
         materialModel.setNormalTexcoord(normalTextureInfo.getTexCoord());
         float normalScale = Optionals.of(normalTextureInfo.getScale(), normalTextureInfo.defaultScale());
         materialModel.setNormalScale(normalScale);
      }

      MaterialOcclusionTextureInfo occlusionTextureInfo = material.getOcclusionTexture();
      if (occlusionTextureInfo != null) {
         int index = occlusionTextureInfo.getIndex();
         TextureModel textureModel = this.gltfModel.getTextureModel(index);
         materialModel.setOcclusionTexture(textureModel);
         materialModel.setOcclusionTexcoord(occlusionTextureInfo.getTexCoord());
         float occlusionStrength = Optionals.of(occlusionTextureInfo.getStrength(), occlusionTextureInfo.defaultStrength());
         materialModel.setOcclusionStrength(occlusionStrength);
      }

      TextureInfo emissiveTextureInfo = material.getEmissiveTexture();
      if (emissiveTextureInfo != null) {
         int index = emissiveTextureInfo.getIndex();
         TextureModel textureModel = this.gltfModel.getTextureModel(index);
         materialModel.setEmissiveTexture(textureModel);
         materialModel.setEmissiveTexcoord(emissiveTextureInfo.getTexCoord());
      }

      float[] emissiveFactor = Optionals.of(material.getEmissiveFactor(), material.defaultEmissiveFactor());
      materialModel.setEmissiveFactor(emissiveFactor);
   }

   private void initExtensionsModel() {
      List<String> extensionsUsed = this.gltf.getExtensionsUsed();
      List<String> extensionsRequired = this.gltf.getExtensionsRequired();
      DefaultExtensionsModel extensionsModel = this.gltfModel.getExtensionsModel();
      extensionsModel.addExtensionsUsed(extensionsUsed);
      extensionsModel.addExtensionsRequired(extensionsRequired);
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

   private static float[] toArray(List<? extends Number> numbers) {
      if (numbers == null) {
         return null;
      }

      float[] array = new float[numbers.size()];

      for (int j = 0; j < numbers.size(); j++) {
         array[j] = numbers.get(j).floatValue();
      }

      return array;
   }
}
