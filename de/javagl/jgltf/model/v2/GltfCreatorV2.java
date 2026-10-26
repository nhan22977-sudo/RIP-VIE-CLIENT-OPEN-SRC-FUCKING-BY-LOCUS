package de.javagl.jgltf.model.v2;

import de.javagl.jgltf.impl.v2.Accessor;
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
import de.javagl.jgltf.model.AssetModel;
import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.CameraOrthographicModel;
import de.javagl.jgltf.model.CameraPerspectiveModel;
import de.javagl.jgltf.model.ExtensionsModel;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.ModelElement;
import de.javagl.jgltf.model.NamedModelElement;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.TextureModel;
import de.javagl.jgltf.model.impl.DefaultNodeModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GltfCreatorV2 {
   private static final Logger logger = Logger.getLogger(GltfCreatorV2.class.getName());
   private final GltfModel gltfModel;
   private final List<NodeModel> nodesWithSingleMeshes;
   private final Map<AccessorModel, Integer> accessorIndices;
   private final Map<BufferModel, Integer> bufferIndices;
   private final Map<BufferViewModel, Integer> bufferViewIndices;
   private final Map<CameraModel, Integer> cameraIndices;
   private final Map<ImageModel, Integer> imageIndices;
   private final Map<MaterialModel, Integer> materialIndices;
   private final Map<MeshModel, Integer> meshIndices;
   private final Map<NodeModel, Integer> nodeIndices;
   private final Map<SkinModel, Integer> skinIndices;
   private final Map<TextureModel, Integer> textureIndices;
   private final Map<GltfCreatorV2.SamplerInfo, Integer> samplerIndices;

   public static GlTF create(GltfModel gltfModel) {
      GltfCreatorV2 creator = new GltfCreatorV2(gltfModel);
      return creator.create();
   }

   private GltfCreatorV2(GltfModel gltfModel) {
      this.gltfModel = Objects.requireNonNull(gltfModel, "The gltfModel may not be null");
      this.accessorIndices = computeIndexMap(gltfModel.getAccessorModels());
      this.bufferIndices = computeIndexMap(gltfModel.getBufferModels());
      this.bufferViewIndices = computeIndexMap(gltfModel.getBufferViewModels());
      this.cameraIndices = computeIndexMap(gltfModel.getCameraModels());
      this.imageIndices = computeIndexMap(gltfModel.getImageModels());
      this.materialIndices = computeIndexMap(gltfModel.getMaterialModels());
      this.meshIndices = computeIndexMap(gltfModel.getMeshModels());
      this.nodesWithSingleMeshes = createNodesWithSingleMeshes(gltfModel.getNodeModels());
      this.nodeIndices = computeIndexMap(this.nodesWithSingleMeshes);
      this.skinIndices = computeIndexMap(gltfModel.getSkinModels());
      this.textureIndices = computeIndexMap(gltfModel.getTextureModels());
      this.samplerIndices = this.createSamplerIndices(gltfModel.getTextureModels());
   }

   public GlTF create() {
      GlTF gltf = new GlTF();
      transferGltfPropertyElements(this.gltfModel, gltf);
      gltf.setAccessors(map(this.gltfModel.getAccessorModels(), this::createAccessor));
      gltf.setAnimations(map(this.gltfModel.getAnimationModels(), this::createAnimation));
      gltf.setBuffers(map(this.gltfModel.getBufferModels(), GltfCreatorV2::createBuffer));
      gltf.setBufferViews(map(this.gltfModel.getBufferViewModels(), this::createBufferView));
      gltf.setCameras(map(this.gltfModel.getCameraModels(), this::createCamera));
      gltf.setImages(map(this.gltfModel.getImageModels(), this::createImage));
      gltf.setMaterials(map(this.gltfModel.getMaterialModels(), this::createMaterial));
      gltf.setMeshes(map(this.gltfModel.getMeshModels(), this::createMesh));
      gltf.setNodes(map(this.nodesWithSingleMeshes, this::createNode));
      gltf.setScenes(map(this.gltfModel.getSceneModels(), this::createScene));
      gltf.setSkins(map(this.gltfModel.getSkinModels(), this::createSkin));
      gltf.setSamplers(this.createSamplers());
      gltf.setTextures(map(this.gltfModel.getTextureModels(), this::createTexture));
      if (gltf.getScenes() != null && !gltf.getScenes().isEmpty()) {
         gltf.setScene(0);
      }

      ExtensionsModel extensionsModel = this.gltfModel.getExtensionsModel();
      List<String> extensionsUsed = extensionsModel.getExtensionsUsed();
      if (!extensionsUsed.isEmpty()) {
         gltf.setExtensionsUsed(extensionsUsed);
      }

      List<String> extensionsRequired = extensionsModel.getExtensionsRequired();
      if (!extensionsRequired.isEmpty()) {
         gltf.setExtensionsRequired(extensionsRequired);
      }

      Asset asset = this.createAsset(this.gltfModel.getAssetModel());
      gltf.setAsset(asset);
      return gltf;
   }

   private static List<NodeModel> createNodesWithSingleMeshes(List<NodeModel> nodeModels) {
      List<NodeModel> newNodes = new ArrayList<>();
      List<NodeModel> nodeModelsWithSingleMeshes = new ArrayList<>(nodeModels);

      for (int i = 0; i < nodeModelsWithSingleMeshes.size(); i++) {
         NodeModel nodeModel = nodeModelsWithSingleMeshes.get(i);
         List<MeshModel> meshModels = nodeModel.getMeshModels();
         if (meshModels.size() > 1) {
            DefaultNodeModel newParentNodeModel = new DefaultNodeModel(nodeModel);

            for (int j = 0; j < meshModels.size(); j++) {
               MeshModel meshModel = meshModels.get(j);
               DefaultNodeModel child = new DefaultNodeModel();
               child.addMeshModel(meshModel);
               newNodes.add(child);
               newParentNodeModel.addChild(child);
            }

            nodeModelsWithSingleMeshes.set(i, newParentNodeModel);
         }
      }

      nodeModelsWithSingleMeshes.addAll(newNodes);
      return nodeModelsWithSingleMeshes;
   }

   private Accessor createAccessor(AccessorModel accessorModel) {
      Integer bufferViewIndex = this.bufferViewIndices.get(accessorModel.getBufferViewModel());
      return createAccessor(accessorModel, bufferViewIndex);
   }

   public static Accessor createAccessor(AccessorModel accessorModel, Integer bufferViewIndex) {
      Accessor accessor = new Accessor();
      transferGltfChildOfRootPropertyElements(accessorModel, accessor);
      accessor.setBufferView(bufferViewIndex);
      accessor.setByteOffset(accessorModel.getByteOffset());
      accessor.setComponentType(accessorModel.getComponentType());
      accessor.setCount(accessorModel.getCount());
      accessor.setType(accessorModel.getElementType().toString());
      accessor.setNormalized(accessorModel.isNormalized() ? true : null);
      AccessorData accessorData = accessorModel.getAccessorData();
      accessor.setMax(AccessorDatas.computeMax(accessorData));
      accessor.setMin(AccessorDatas.computeMin(accessorData));
      return accessor;
   }

   private Animation createAnimation(AnimationModel animationModel) {
      Animation animation = new Animation();
      transferGltfChildOfRootPropertyElements(animationModel, animation);
      List<AnimationModel.Sampler> samplers = new ArrayList<>();
      List<AnimationModel.Channel> channels = animationModel.getChannels();

      for (AnimationModel.Channel channel : channels) {
         samplers.add(channel.getSampler());
      }

      List<AnimationChannel> animationChannels = new ArrayList<>();

      for (AnimationModel.Channel channel : channels) {
         AnimationChannel animationChannel = new AnimationChannel();
         AnimationChannelTarget target = new AnimationChannelTarget();
         NodeModel nodeModel = channel.getNodeModel();
         target.setNode(this.nodeIndices.get(nodeModel));
         target.setPath(channel.getPath());
         animationChannel.setTarget(target);
         AnimationModel.Sampler sampler = channel.getSampler();
         animationChannel.setSampler(samplers.indexOf(sampler));
         animationChannels.add(animationChannel);
      }

      animation.setChannels(animationChannels);
      List<AnimationSampler> animationSamplers = new ArrayList<>();

      for (AnimationModel.Sampler sampler : samplers) {
         AnimationSampler animationSampler = new AnimationSampler();
         animationSampler.setInput(this.accessorIndices.get(sampler.getInput()));
         animationSampler.setInterpolation(sampler.getInterpolation().name());
         animationSampler.setOutput(this.accessorIndices.get(sampler.getOutput()));
         animationSamplers.add(animationSampler);
      }

      animation.setSamplers(animationSamplers);
      return animation;
   }

   public static Buffer createBuffer(BufferModel bufferModel) {
      Buffer buffer = new Buffer();
      transferGltfChildOfRootPropertyElements(bufferModel, buffer);
      buffer.setUri(bufferModel.getUri());
      buffer.setByteLength(bufferModel.getByteLength());
      return buffer;
   }

   private BufferView createBufferView(BufferViewModel bufferViewModel) {
      Integer bufferIndex = this.bufferIndices.get(bufferViewModel.getBufferModel());
      return createBufferView(bufferViewModel, bufferIndex);
   }

   public static BufferView createBufferView(BufferViewModel bufferViewModel, Integer bufferIndex) {
      BufferView bufferView = new BufferView();
      transferGltfChildOfRootPropertyElements(bufferViewModel, bufferView);
      bufferView.setBuffer(bufferIndex);
      bufferView.setByteOffset(bufferViewModel.getByteOffset());
      bufferView.setByteLength(bufferViewModel.getByteLength());
      bufferView.setByteStride(bufferViewModel.getByteStride());
      bufferView.setTarget(bufferViewModel.getTarget());
      return bufferView;
   }

   private Camera createCamera(CameraModel cameraModel) {
      Camera camera = new Camera();
      transferGltfChildOfRootPropertyElements(cameraModel, camera);
      CameraPerspectiveModel cameraPerspectiveModel = cameraModel.getCameraPerspectiveModel();
      CameraOrthographicModel cameraOrthographicModel = cameraModel.getCameraOrthographicModel();
      if (cameraPerspectiveModel != null) {
         CameraPerspective cameraPerspective = new CameraPerspective();
         cameraPerspective.setAspectRatio(cameraPerspectiveModel.getAspectRatio());
         cameraPerspective.setYfov(cameraPerspectiveModel.getYfov());
         cameraPerspective.setZfar(cameraPerspectiveModel.getZfar());
         cameraPerspective.setZnear(cameraPerspectiveModel.getZnear());
         camera.setPerspective(cameraPerspective);
         camera.setType("perspective");
      } else if (cameraOrthographicModel != null) {
         CameraOrthographic cameraOrthographic = new CameraOrthographic();
         cameraOrthographic.setXmag(cameraOrthographicModel.getXmag());
         cameraOrthographic.setYmag(cameraOrthographicModel.getYmag());
         cameraOrthographic.setZfar(cameraOrthographicModel.getZfar());
         cameraOrthographic.setZnear(cameraOrthographicModel.getZnear());
         camera.setOrthographic(cameraOrthographic);
         camera.setType("orthographic");
      } else {
         logger.severe("Camera is neither perspective nor orthographic");
      }

      return camera;
   }

   private Image createImage(ImageModel imageModel) {
      Image image = new Image();
      transferGltfChildOfRootPropertyElements(imageModel, image);
      Integer bufferView = this.bufferViewIndices.get(imageModel.getBufferViewModel());
      image.setBufferView(bufferView);
      image.setMimeType(imageModel.getMimeType());
      image.setUri(imageModel.getUri());
      return image;
   }

   private Material createMaterial(MaterialModel materialModel) {
      if (materialModel instanceof MaterialModelV2) {
         MaterialModelV2 materialModelV2 = (MaterialModelV2)materialModel;
         return this.createMaterialV2(materialModelV2);
      } else {
         logger.severe("Cannot store glTF 1.0 material in glTF 2.0");
         return null;
      }
   }

   private Material createMaterialV2(MaterialModelV2 materialModel) {
      Material material = new Material();
      transferGltfChildOfRootPropertyElements(materialModel, material);
      MaterialModelV2.AlphaMode alphaMode = materialModel.getAlphaMode();
      if (alphaMode == null) {
         material.setAlphaMode(MaterialModelV2.AlphaMode.OPAQUE.name());
      } else {
         material.setAlphaMode(alphaMode.name());
      }

      if (MaterialModelV2.AlphaMode.MASK.equals(alphaMode)) {
         material.setAlphaCutoff(materialModel.getAlphaCutoff());
      }

      material.setDoubleSided(materialModel.isDoubleSided());
      MaterialPbrMetallicRoughness pbrMetallicRoughness = new MaterialPbrMetallicRoughness();
      material.setPbrMetallicRoughness(pbrMetallicRoughness);
      pbrMetallicRoughness.setBaseColorFactor(materialModel.getBaseColorFactor());
      TextureModel baseColorTexture = materialModel.getBaseColorTexture();
      if (baseColorTexture != null) {
         TextureInfo baseColorTextureInfo = new TextureInfo();
         baseColorTextureInfo.setIndex(this.textureIndices.get(baseColorTexture));
         baseColorTextureInfo.setTexCoord(materialModel.getBaseColorTexcoord());
         pbrMetallicRoughness.setBaseColorTexture(baseColorTextureInfo);
      }

      pbrMetallicRoughness.setMetallicFactor(materialModel.getMetallicFactor());
      pbrMetallicRoughness.setRoughnessFactor(materialModel.getRoughnessFactor());
      TextureModel metallicRoughnessTexture = materialModel.getMetallicRoughnessTexture();
      if (metallicRoughnessTexture != null) {
         TextureInfo metallicRoughnessTextureInfo = new TextureInfo();
         metallicRoughnessTextureInfo.setIndex(this.textureIndices.get(metallicRoughnessTexture));
         metallicRoughnessTextureInfo.setTexCoord(materialModel.getMetallicRoughnessTexcoord());
         pbrMetallicRoughness.setMetallicRoughnessTexture(metallicRoughnessTextureInfo);
      }

      TextureModel normalTexture = materialModel.getNormalTexture();
      if (normalTexture != null) {
         MaterialNormalTextureInfo normalTextureInfo = new MaterialNormalTextureInfo();
         normalTextureInfo.setIndex(this.textureIndices.get(normalTexture));
         normalTextureInfo.setTexCoord(materialModel.getNormalTexcoord());
         normalTextureInfo.setScale(materialModel.getNormalScale());
         material.setNormalTexture(normalTextureInfo);
      }

      TextureModel occlusionTexture = materialModel.getOcclusionTexture();
      if (occlusionTexture != null) {
         MaterialOcclusionTextureInfo occlusionTextureInfo = new MaterialOcclusionTextureInfo();
         occlusionTextureInfo.setIndex(this.textureIndices.get(occlusionTexture));
         occlusionTextureInfo.setTexCoord(materialModel.getOcclusionTexcoord());
         occlusionTextureInfo.setStrength(materialModel.getOcclusionStrength());
         material.setOcclusionTexture(occlusionTextureInfo);
      }

      TextureModel emissiveTexture = materialModel.getEmissiveTexture();
      if (emissiveTexture != null) {
         TextureInfo emissiveTextureInfo = new TextureInfo();
         emissiveTextureInfo.setIndex(this.textureIndices.get(emissiveTexture));
         emissiveTextureInfo.setTexCoord(materialModel.getEmissiveTexcoord());
         material.setEmissiveFactor(materialModel.getEmissiveFactor());
         material.setEmissiveTexture(emissiveTextureInfo);
      }

      return material;
   }

   private Mesh createMesh(MeshModel meshModel) {
      Mesh mesh = new Mesh();
      transferGltfChildOfRootPropertyElements(meshModel, mesh);
      List<MeshPrimitive> meshPrimitives = new ArrayList<>();

      for (MeshPrimitiveModel meshPrimitiveModel : meshModel.getMeshPrimitiveModels()) {
         MeshPrimitive meshPrimitive = this.createMeshPrimitive(meshPrimitiveModel);
         meshPrimitives.add(meshPrimitive);
      }

      mesh.setPrimitives(meshPrimitives);
      mesh.setWeights(toList(meshModel.getWeights()));
      return mesh;
   }

   private MeshPrimitive createMeshPrimitive(MeshPrimitiveModel meshPrimitiveModel) {
      MeshPrimitive meshPrimitive = new MeshPrimitive();
      transferGltfPropertyElements(meshPrimitiveModel, meshPrimitive);
      meshPrimitive.setMode(meshPrimitiveModel.getMode());
      Map<String, Integer> attributes = resolveIndices(meshPrimitiveModel.getAttributes(), this.accessorIndices::get);
      meshPrimitive.setAttributes(attributes);
      AccessorModel indices = meshPrimitiveModel.getIndices();
      meshPrimitive.setIndices(this.accessorIndices.get(indices));
      List<Map<String, AccessorModel>> modelTargetsList = meshPrimitiveModel.getTargets();
      if (!modelTargetsList.isEmpty()) {
         List<Map<String, Integer>> targetsList = new ArrayList<>();

         for (Map<String, AccessorModel> modelTargets : modelTargetsList) {
            Map<String, Integer> targets = resolveIndices(modelTargets, this.accessorIndices::get);
            targetsList.add(targets);
         }

         meshPrimitive.setTargets(targetsList);
      }

      Integer material = this.materialIndices.get(meshPrimitiveModel.getMaterialModel());
      meshPrimitive.setMaterial(material);
      return meshPrimitive;
   }

   private Node createNode(NodeModel nodeModel) {
      Node node = new Node();
      transferGltfChildOfRootPropertyElements(nodeModel, node);
      if (!nodeModel.getChildren().isEmpty()) {
         node.setChildren(map(nodeModel.getChildren(), this.nodeIndices::get));
      }

      node.setTranslation(Optionals.clone(nodeModel.getTranslation()));
      node.setRotation(Optionals.clone(nodeModel.getRotation()));
      node.setScale(Optionals.clone(nodeModel.getScale()));
      node.setMatrix(Optionals.clone(nodeModel.getMatrix()));
      Integer camera = this.cameraIndices.get(nodeModel.getCameraModel());
      node.setCamera(camera);
      Integer skin = this.skinIndices.get(nodeModel.getSkinModel());
      node.setSkin(skin);
      node.setWeights(toList(nodeModel.getWeights()));
      List<MeshModel> nodeMeshModels = nodeModel.getMeshModels();
      if (nodeMeshModels.size() > 1) {
         logger.severe("Warning: glTF 2.0 only supports one mesh per node");
      }

      if (!nodeMeshModels.isEmpty()) {
         MeshModel nodeMeshModel = nodeMeshModels.get(0);
         Integer mesh = this.meshIndices.get(nodeMeshModel);
         node.setMesh(mesh);
      }

      return node;
   }

   private Scene createScene(SceneModel sceneModel) {
      Scene scene = new Scene();
      transferGltfChildOfRootPropertyElements(sceneModel, scene);
      scene.setNodes(map(sceneModel.getNodeModels(), this.nodeIndices::get));
      return scene;
   }

   private Skin createSkin(SkinModel skinModel) {
      Skin skin = new Skin();
      transferGltfChildOfRootPropertyElements(skinModel, skin);
      Integer inverseBindMatrices = this.accessorIndices.get(skinModel.getInverseBindMatrices());
      skin.setInverseBindMatrices(inverseBindMatrices);
      skin.setJoints(map(skinModel.getJoints(), this.nodeIndices::get));
      Integer skeleton = this.nodeIndices.get(skinModel.getSkeleton());
      skin.setSkeleton(skeleton);
      return skin;
   }

   private Map<GltfCreatorV2.SamplerInfo, Integer> createSamplerIndices(List<TextureModel> textureModels) {
      Map<GltfCreatorV2.SamplerInfo, Integer> samplerIndices = new LinkedHashMap<>();

      for (TextureModel textureModel : textureModels) {
         GltfCreatorV2.SamplerInfo samplerInfo = new GltfCreatorV2.SamplerInfo(textureModel);
         if (!samplerIndices.containsKey(samplerInfo)) {
            samplerIndices.put(samplerInfo, samplerIndices.size());
         }
      }

      return samplerIndices;
   }

   private List<Sampler> createSamplers() {
      if (this.samplerIndices.isEmpty()) {
         return null;
      }

      List<Sampler> samplers = new ArrayList<>();

      for (GltfCreatorV2.SamplerInfo samplerInfo : this.samplerIndices.keySet()) {
         Sampler sampler = createSampler(samplerInfo);
         samplers.add(sampler);
      }

      return samplers;
   }

   private static Sampler createSampler(GltfCreatorV2.SamplerInfo samplerInfo) {
      Sampler sampler = new Sampler();
      sampler.setMagFilter(samplerInfo.magFilter);
      sampler.setMinFilter(samplerInfo.minFilter);
      sampler.setWrapS(samplerInfo.wrapS);
      sampler.setWrapT(samplerInfo.wrapT);
      return sampler;
   }

   private Texture createTexture(TextureModel textureModel) {
      Texture texture = new Texture();
      transferGltfChildOfRootPropertyElements(textureModel, texture);
      GltfCreatorV2.SamplerInfo samplerInfo = new GltfCreatorV2.SamplerInfo(textureModel);
      Integer index = this.samplerIndices.get(samplerInfo);
      texture.setSampler(index);
      texture.setSource(this.imageIndices.get(textureModel.getImageModel()));
      return texture;
   }

   private Asset createAsset(AssetModel assetModel) {
      Asset asset = new Asset();
      asset.setVersion("2.0");
      asset.setGenerator("JglTF from https://github.com/javagl/JglTF");
      transferGltfPropertyElements(assetModel, asset);
      if (assetModel.getCopyright() != null) {
         asset.setCopyright(assetModel.getCopyright());
      }

      if (assetModel.getGenerator() != null) {
         asset.setGenerator(assetModel.getGenerator());
      }

      return asset;
   }

   private static void transferGltfPropertyElements(ModelElement modelElement, GlTFProperty property) {
      property.setExtensions(modelElement.getExtensions());
      property.setExtras(modelElement.getExtras());
   }

   private static void transferGltfChildOfRootPropertyElements(NamedModelElement modelElement, GlTFChildOfRootProperty property) {
      property.setName(modelElement.getName());
      transferGltfPropertyElements(modelElement, property);
   }

   private static <T, U> List<U> map(Collection<? extends T> collection, Function<? super T, ? extends U> mapper) {
      return collection.isEmpty() ? null : collection.stream().map(mapper).collect(Collectors.toList());
   }

   private static <K, T> Map<K, Integer> resolveIndices(Map<K, ? extends T> map, Function<? super T, Integer> indexLookup) {
      Map<K, Integer> result = new LinkedHashMap<>();

      for (Entry<K, ? extends T> entry : map.entrySet()) {
         K key = entry.getKey();
         T value = (T)entry.getValue();
         Integer index = indexLookup.apply(value);
         result.put(key, index);
      }

      return result;
   }

   private static <T> Map<T, Integer> computeIndexMap(Collection<? extends T> elements) {
      Map<T, Integer> indices = new LinkedHashMap<>();
      int index = 0;

      for (T element : elements) {
         indices.put(element, index);
         index++;
      }

      return indices;
   }

   private static List<Float> toList(float[] array) {
      if (array == null) {
         return null;
      }

      List<Float> list = new ArrayList<>();

      for (float f : array) {
         list.add(f);
      }

      return list;
   }

   private static class SamplerInfo {
      final Integer magFilter;
      final Integer minFilter;
      final Integer wrapS;
      final Integer wrapT;

      SamplerInfo(TextureModel textureModel) {
         this.magFilter = textureModel.getMagFilter();
         this.minFilter = textureModel.getMinFilter();
         this.wrapS = textureModel.getWrapS();
         this.wrapT = textureModel.getWrapT();
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.magFilter, this.minFilter, this.wrapS, this.wrapT);
      }

      @Override
      public boolean equals(Object object) {
         if (this == object) {
            return true;
         } else if (object == null) {
            return false;
         } else if (this.getClass() != object.getClass()) {
            return false;
         } else {
            GltfCreatorV2.SamplerInfo other = (GltfCreatorV2.SamplerInfo)object;
            if (!Objects.equals(this.magFilter, other.magFilter)) {
               return false;
            } else if (!Objects.equals(this.minFilter, other.minFilter)) {
               return false;
            } else {
               return !Objects.equals(this.wrapS, other.wrapS) ? false : Objects.equals(this.wrapT, other.wrapT);
            }
         }
      }
   }
}
