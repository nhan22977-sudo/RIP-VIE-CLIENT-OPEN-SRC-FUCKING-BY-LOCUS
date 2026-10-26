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
import de.javagl.jgltf.model.gl.ProgramModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.gl.TechniqueParametersModel;
import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class GltfCreatorV1 {
   private static final Logger logger = Logger.getLogger(GltfCreatorV1.class.getName());
   private final GltfModel gltfModel;
   private final Map<AccessorModel, String> accessorIds;
   private final Map<BufferModel, String> bufferIds;
   private final Map<BufferViewModel, String> bufferViewIds;
   private final Map<CameraModel, String> cameraIds;
   private final Map<ImageModel, String> imageIds;
   private final Map<MaterialModel, String> materialIds;
   private final Map<MeshModel, String> meshIds;
   private final Map<NodeModel, String> nodeIds;
   private final Map<ProgramModel, String> programIds;
   private final Map<ShaderModel, String> shaderIds;
   private final Map<SkinModel, String> skinIds;
   private final Map<TechniqueModel, String> techniqueIds;
   private final Map<TextureModel, String> textureIds;
   private final Map<GltfCreatorV1.SamplerInfo, String> samplerIds;

   public static GlTF create(GltfModel gltfModel) {
      GltfCreatorV1 creator = new GltfCreatorV1(gltfModel);
      return creator.create();
   }

   private GltfCreatorV1(GltfModel gltfModel) {
      this.gltfModel = Objects.requireNonNull(gltfModel, "The gltfModel may not be null");
      this.accessorIds = computeIdMap("accessor", gltfModel.getAccessorModels());
      this.bufferIds = computeIdMap("buffer", gltfModel.getBufferModels());
      this.bufferViewIds = computeIdMap("bufferView", gltfModel.getBufferViewModels());
      this.cameraIds = computeIdMap("camera", gltfModel.getCameraModels());
      this.imageIds = computeIdMap("image", gltfModel.getImageModels());
      this.materialIds = computeIdMap("material", gltfModel.getMaterialModels());
      this.meshIds = computeIdMap("mesh", gltfModel.getMeshModels());
      this.nodeIds = computeIdMap("node", gltfModel.getNodeModels());
      this.skinIds = computeIdMap("skin", gltfModel.getSkinModels());
      this.textureIds = computeIdMap("texture", gltfModel.getTextureModels());
      if (gltfModel instanceof GltfModelV1) {
         GltfModelV1 gltfModelV1 = (GltfModelV1)gltfModel;
         this.programIds = computeIdMap("program", gltfModelV1.getProgramModels());
         this.shaderIds = computeIdMap("shader", gltfModelV1.getShaderModels());
         this.techniqueIds = computeIdMap("technique", gltfModelV1.getTechniqueModels());
      } else {
         this.programIds = Collections.emptyMap();
         this.shaderIds = Collections.emptyMap();
         this.techniqueIds = Collections.emptyMap();
      }

      this.samplerIds = this.createSamplerIds(gltfModel.getTextureModels());
   }

   public GlTF create() {
      GlTF gltf = new GlTF();
      transferGltfPropertyElements(this.gltfModel, gltf);
      gltf.setAccessors(map("accessor", this.gltfModel.getAccessorModels(), this::createAccessor));
      gltf.setAnimations(map("animation", this.gltfModel.getAnimationModels(), this::createAnimation));
      gltf.setBuffers(map("buffer", this.gltfModel.getBufferModels(), GltfCreatorV1::createBuffer));
      gltf.setBufferViews(map("bufferView", this.gltfModel.getBufferViewModels(), this::createBufferView));
      gltf.setCameras(map("camera", this.gltfModel.getCameraModels(), this::createCamera));
      gltf.setImages(map("image", this.gltfModel.getImageModels(), this::createImage));
      gltf.setMaterials(map("material", this.gltfModel.getMaterialModels(), this::createMaterial));
      gltf.setMeshes(map("mesh", this.gltfModel.getMeshModels(), this::createMesh));
      gltf.setNodes(map("node", this.gltfModel.getNodeModels(), this::createNode));
      gltf.setScenes(map("scene", this.gltfModel.getSceneModels(), this::createScene));
      gltf.setSkins(map("skin", this.gltfModel.getSkinModels(), this::createSkin));
      gltf.setSamplers(this.createSamplers());
      gltf.setTextures(map("texture", this.gltfModel.getTextureModels(), this::createTexture));
      if (this.gltfModel instanceof GltfModelV1) {
         GltfModelV1 gltfModelV1 = (GltfModelV1)this.gltfModel;
         gltf.setPrograms(map("program", gltfModelV1.getProgramModels(), this::createProgram));
         gltf.setShaders(map("shader", gltfModelV1.getShaderModels(), this::createShader));
         gltf.setTechniques(map("technique", gltfModelV1.getTechniqueModels(), this::createTechnique));
      }

      if (gltf.getScenes() != null && !gltf.getScenes().isEmpty()) {
         gltf.setScene((String)gltf.getScenes().keySet().iterator().next());
      }

      ExtensionsModel extensionsModel = this.gltfModel.getExtensionsModel();
      List<String> extensionsUsed = extensionsModel.getExtensionsUsed();
      if (!extensionsUsed.isEmpty()) {
         gltf.setExtensionsUsed(extensionsUsed);
      }

      Asset asset = this.createAsset(this.gltfModel.getAssetModel());
      gltf.setAsset(asset);
      return gltf;
   }

   private Accessor createAccessor(AccessorModel accessorModel) {
      String bufferViewId = this.bufferViewIds.get(accessorModel.getBufferViewModel());
      return createAccessor(accessorModel, bufferViewId);
   }

   public static Accessor createAccessor(AccessorModel accessorModel, String bufferViewId) {
      Accessor accessor = new Accessor();
      transferGltfChildOfRootPropertyElements(accessorModel, accessor);
      accessor.setBufferView(bufferViewId);
      accessor.setByteOffset(accessorModel.getByteOffset());
      accessor.setComponentType(accessorModel.getComponentType());
      accessor.setCount(accessorModel.getCount());
      accessor.setType(accessorModel.getElementType().toString());
      accessor.setByteStride(accessorModel.getByteStride());
      AccessorData accessorData = accessorModel.getAccessorData();
      accessor.setMax(AccessorDatas.computeMax(accessorData));
      accessor.setMin(AccessorDatas.computeMin(accessorData));
      return accessor;
   }

   private Animation createAnimation(AnimationModel animationModel) {
      Animation animation = new Animation();
      transferGltfChildOfRootPropertyElements(animationModel, animation);
      Map<AnimationModel.Sampler, String> samplers = new LinkedHashMap<>();
      List<AnimationModel.Channel> channels = animationModel.getChannels();
      int counter = 0;

      for (AnimationModel.Channel channel : channels) {
         String id = "sampler_" + counter;
         samplers.put(channel.getSampler(), id);
         counter++;
      }

      List<AnimationChannel> animationChannels = new ArrayList<>();

      for (AnimationModel.Channel channel : channels) {
         AnimationChannel animationChannel = new AnimationChannel();
         AnimationChannelTarget target = new AnimationChannelTarget();
         NodeModel nodeModel = channel.getNodeModel();
         target.setId(this.nodeIds.get(nodeModel));
         target.setPath(channel.getPath());
         animationChannel.setTarget(target);
         AnimationModel.Sampler sampler = channel.getSampler();
         animationChannel.setSampler(samplers.get(sampler));
         animationChannels.add(animationChannel);
      }

      animation.setChannels(animationChannels);
      Map<String, AnimationSampler> animationSamplers = new LinkedHashMap<>();

      for (AnimationModel.Sampler sampler : samplers.keySet()) {
         AnimationSampler animationSampler = new AnimationSampler();
         animationSampler.setInput(this.accessorIds.get(sampler.getInput()));
         animationSampler.setInterpolation(sampler.getInterpolation().name());
         animationSampler.setOutput(this.accessorIds.get(sampler.getOutput()));
         String key = samplers.get(sampler);
         animationSamplers.put(key, animationSampler);
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
      String bufferId = this.bufferIds.get(bufferViewModel.getBufferModel());
      return createBufferView(bufferViewModel, bufferId);
   }

   public static BufferView createBufferView(BufferViewModel bufferViewModel, String bufferId) {
      BufferView bufferView = new BufferView();
      transferGltfChildOfRootPropertyElements(bufferViewModel, bufferView);
      bufferView.setBuffer(bufferId);
      bufferView.setByteOffset(bufferViewModel.getByteOffset());
      bufferView.setByteLength(bufferViewModel.getByteLength());
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
      String bufferView = this.bufferViewIds.get(imageModel.getBufferViewModel());
      if (bufferView != null) {
         BinaryGltfV1.setBinaryGltfBufferViewId(image, bufferView);
      } else {
         image.setUri(imageModel.getUri());
      }

      return image;
   }

   private Material createMaterial(MaterialModel materialModel) {
      if (materialModel instanceof MaterialModelV1) {
         MaterialModelV1 materialModelV1 = (MaterialModelV1)materialModel;
         return this.createMaterialV1(materialModelV1);
      } else {
         logger.severe("Cannot store glTF 2.0 material in glTF 1.0");
         return null;
      }
   }

   private Material createMaterialV1(MaterialModelV1 materialModel) {
      Material material = new Material();
      transferGltfChildOfRootPropertyElements(materialModel, material);
      TechniqueModel techniqueModel = materialModel.getTechniqueModel();
      material.setTechnique(this.techniqueIds.get(techniqueModel));
      Map<String, Object> modelValues = materialModel.getValues();
      Map<String, Object> values = new LinkedHashMap<>();

      for (Entry<String, Object> valueEntry : modelValues.entrySet()) {
         String parameterName = valueEntry.getKey();
         Object value = valueEntry.getValue();
         if (value instanceof TextureModel) {
            TextureModel textureModel = (TextureModel)value;
            String textureId = this.textureIds.get(textureModel);
            values.put(parameterName, textureId);
         } else {
            values.put(parameterName, value);
         }
      }

      material.setValues(values);
      return material;
   }

   private Program createProgram(ProgramModel programModel) {
      Program program = new Program();
      transferGltfChildOfRootPropertyElements(programModel, program);
      ShaderModel vertexShaderModel = programModel.getVertexShaderModel();
      program.setVertexShader(this.shaderIds.get(vertexShaderModel));
      ShaderModel fragmentShaderModel = programModel.getFragmentShaderModel();
      program.setFragmentShader(this.shaderIds.get(fragmentShaderModel));
      List<String> modelAttributes = programModel.getAttributes();
      if (!modelAttributes.isEmpty()) {
         List<String> attributes = new ArrayList<>(modelAttributes);
         program.setAttributes(attributes);
      }

      return program;
   }

   private Shader createShader(ShaderModel shaderModel) {
      Shader shader = new Shader();
      transferGltfChildOfRootPropertyElements(shaderModel, shader);
      ShaderModel.ShaderType shaderType = shaderModel.getShaderType();
      if (shaderType == ShaderModel.ShaderType.VERTEX_SHADER) {
         shader.setType(35633);
      } else if (shaderType == ShaderModel.ShaderType.FRAGMENT_SHADER) {
         shader.setType(35632);
      } else {
         logger.severe("Invalid shader type: " + shaderType);
      }

      shader.setUri(shaderModel.getUri());
      return shader;
   }

   private Technique createTechnique(TechniqueModel techniqueModel) {
      Technique technique = new Technique();
      transferGltfChildOfRootPropertyElements(techniqueModel, technique);
      ProgramModel programModel = techniqueModel.getProgramModel();
      technique.setProgram(this.programIds.get(programModel));
      Map<String, String> uniforms = techniqueModel.getUniforms();
      technique.setUniforms(new LinkedHashMap<>(uniforms));
      Map<String, String> attributes = techniqueModel.getAttributes();
      technique.setAttributes(new LinkedHashMap<>(attributes));
      Map<String, TechniqueParametersModel> parametersModel = techniqueModel.getParameters();
      if (!parametersModel.isEmpty()) {
         Map<String, TechniqueParameters> parameters = new LinkedHashMap<>();

         for (Entry<String, TechniqueParametersModel> entry : parametersModel.entrySet()) {
            String key = entry.getKey();
            TechniqueParametersModel techniqueParametersModel = entry.getValue();
            TechniqueParameters techniqueParameters = this.createTechniqueParameters(techniqueParametersModel);
            parameters.put(key, techniqueParameters);
         }

         technique.setParameters(parameters);
      }

      technique.setStates(this.createTechniqueStates(techniqueModel.getTechniqueStatesModel()));
      return technique;
   }

   private TechniqueParameters createTechniqueParameters(TechniqueParametersModel techniqueParametersModel) {
      TechniqueParameters techniqueParameters = new TechniqueParameters();
      techniqueParameters.setSemantic(techniqueParametersModel.getSemantic());
      techniqueParameters.setType(techniqueParametersModel.getType());
      techniqueParameters.setCount(techniqueParametersModel.getCount());
      techniqueParameters.setValue(techniqueParametersModel.getValue());
      NodeModel nodeModel = techniqueParametersModel.getNodeModel();
      techniqueParameters.setNode(this.nodeIds.get(nodeModel));
      return techniqueParameters;
   }

   private TechniqueStates createTechniqueStates(TechniqueStatesModel techniqueStatesModel) {
      if (techniqueStatesModel == null) {
         return null;
      }

      TechniqueStates techniqueStates = new TechniqueStates();
      List<Integer> enable = techniqueStatesModel.getEnable();
      if (enable != null) {
         techniqueStates.setEnable(new ArrayList<>(enable));
      }

      techniqueStates.setFunctions(this.createTechniqueStatesFunctions(techniqueStatesModel.getTechniqueStatesFunctionsModel()));
      return techniqueStates;
   }

   private TechniqueStatesFunctions createTechniqueStatesFunctions(TechniqueStatesFunctionsModel techniqueStatesFunctionsModel) {
      if (techniqueStatesFunctionsModel == null) {
         return null;
      }

      TechniqueStatesFunctions techniqueStatesFunctions = new TechniqueStatesFunctions();
      techniqueStatesFunctions.setBlendColor(Optionals.clone(techniqueStatesFunctionsModel.getBlendColor()));
      techniqueStatesFunctions.setBlendEquationSeparate(Optionals.clone(techniqueStatesFunctionsModel.getBlendEquationSeparate()));
      techniqueStatesFunctions.setBlendFuncSeparate(Optionals.clone(techniqueStatesFunctionsModel.getBlendFuncSeparate()));
      techniqueStatesFunctions.setColorMask(Optionals.clone(techniqueStatesFunctionsModel.getColorMask()));
      techniqueStatesFunctions.setCullFace(Optionals.clone(techniqueStatesFunctionsModel.getCullFace()));
      techniqueStatesFunctions.setDepthFunc(Optionals.clone(techniqueStatesFunctionsModel.getDepthFunc()));
      techniqueStatesFunctions.setDepthMask(Optionals.clone(techniqueStatesFunctionsModel.getDepthMask()));
      techniqueStatesFunctions.setDepthRange(Optionals.clone(techniqueStatesFunctionsModel.getDepthRange()));
      techniqueStatesFunctions.setFrontFace(Optionals.clone(techniqueStatesFunctionsModel.getFrontFace()));
      techniqueStatesFunctions.setLineWidth(Optionals.clone(techniqueStatesFunctionsModel.getLineWidth()));
      techniqueStatesFunctions.setPolygonOffset(Optionals.clone(techniqueStatesFunctionsModel.getPolygonOffset()));
      return techniqueStatesFunctions;
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
      if (meshModel.getWeights() != null) {
         logger.severe("Morph target weights are not supported in glTF 1.0");
      }

      return mesh;
   }

   private MeshPrimitive createMeshPrimitive(MeshPrimitiveModel meshPrimitiveModel) {
      MeshPrimitive meshPrimitive = new MeshPrimitive();
      transferGltfPropertyElements(meshPrimitiveModel, meshPrimitive);
      meshPrimitive.setMode(meshPrimitiveModel.getMode());
      Map<String, String> attributes = resolveIds(meshPrimitiveModel.getAttributes(), this.accessorIds::get);
      meshPrimitive.setAttributes(attributes);
      AccessorModel Ids = meshPrimitiveModel.getIndices();
      meshPrimitive.setIndices(this.accessorIds.get(Ids));
      List<Map<String, AccessorModel>> modelTargetsList = meshPrimitiveModel.getTargets();
      if (!modelTargetsList.isEmpty()) {
         logger.severe("Morph targets are not supported in glTF 1.0");
      }

      String material = this.materialIds.get(meshPrimitiveModel.getMaterialModel());
      meshPrimitive.setMaterial(material);
      return meshPrimitive;
   }

   private Node createNode(NodeModel nodeModel) {
      Node node = new Node();
      transferGltfChildOfRootPropertyElements(nodeModel, node);
      if (!nodeModel.getChildren().isEmpty()) {
         node.setChildren(map(nodeModel.getChildren(), this.nodeIds::get));
      }

      node.setTranslation(Optionals.clone(nodeModel.getTranslation()));
      node.setRotation(Optionals.clone(nodeModel.getRotation()));
      node.setScale(Optionals.clone(nodeModel.getScale()));
      node.setMatrix(Optionals.clone(nodeModel.getMatrix()));
      String camera = this.cameraIds.get(nodeModel.getCameraModel());
      node.setCamera(camera);
      String skin = this.skinIds.get(nodeModel.getSkinModel());
      node.setSkin(skin);
      if (nodeModel.getWeights() != null) {
         logger.severe("Morph target weights are not supported in glTF 1.0");
      }

      List<MeshModel> nodeMeshModels = nodeModel.getMeshModels();
      if (!nodeMeshModels.isEmpty()) {
         List<String> meshes = new ArrayList<>();

         for (MeshModel meshModel : nodeMeshModels) {
            String id = this.meshIds.get(meshModel);
            meshes.add(id);
         }

         node.setMeshes(meshes);
      }

      return node;
   }

   private Scene createScene(SceneModel sceneModel) {
      Scene scene = new Scene();
      transferGltfChildOfRootPropertyElements(sceneModel, scene);
      scene.setNodes(map(sceneModel.getNodeModels(), this.nodeIds::get));
      return scene;
   }

   private Skin createSkin(SkinModel skinModel) {
      Skin skin = new Skin();
      transferGltfChildOfRootPropertyElements(skinModel, skin);
      String inverseBindMatrices = this.accessorIds.get(skinModel.getInverseBindMatrices());
      skin.setInverseBindMatrices(inverseBindMatrices);
      logger.severe("Skins are not yet fully supported");
      return skin;
   }

   private Map<GltfCreatorV1.SamplerInfo, String> createSamplerIds(List<TextureModel> textureModels) {
      Map<GltfCreatorV1.SamplerInfo, String> samplerIndices = new LinkedHashMap<>();

      for (TextureModel textureModel : textureModels) {
         GltfCreatorV1.SamplerInfo samplerInfo = new GltfCreatorV1.SamplerInfo(textureModel);
         if (!samplerIndices.containsKey(samplerInfo)) {
            samplerIndices.put(samplerInfo, "sampler_" + samplerIndices.size());
         }
      }

      return samplerIndices;
   }

   private Map<String, Sampler> createSamplers() {
      if (this.samplerIds.isEmpty()) {
         return null;
      }

      Map<String, Sampler> samplers = new LinkedHashMap<>();

      for (GltfCreatorV1.SamplerInfo samplerInfo : this.samplerIds.keySet()) {
         Sampler sampler = createSampler(samplerInfo);
         String key = this.samplerIds.get(samplerInfo);
         samplers.put(key, sampler);
      }

      return samplers;
   }

   private static Sampler createSampler(GltfCreatorV1.SamplerInfo samplerInfo) {
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
      GltfCreatorV1.SamplerInfo samplerInfo = new GltfCreatorV1.SamplerInfo(textureModel);
      String id = this.samplerIds.get(samplerInfo);
      texture.setSampler(id);
      texture.setSource(this.imageIds.get(textureModel.getImageModel()));
      return texture;
   }

   private Asset createAsset(AssetModel assetModel) {
      Asset asset = new Asset();
      asset.setVersion("1.0");
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

   private static <T, U> Map<String, U> map(String prefix, Collection<? extends T> elements, Function<? super T, ? extends U> mapper) {
      return map(prefix, map(elements, mapper));
   }

   private static <T> Map<String, T> map(String prefix, Collection<? extends T> elements) {
      if (elements != null && !elements.isEmpty()) {
         Map<String, T> map = new LinkedHashMap<>();
         int index = 0;

         for (T element : elements) {
            map.put(prefix + "_" + index, element);
            index++;
         }

         return map;
      } else {
         return null;
      }
   }

   private static <T, U> List<U> map(Collection<? extends T> collection, Function<? super T, ? extends U> mapper) {
      return collection.isEmpty() ? null : collection.stream().map(mapper).collect(Collectors.toList());
   }

   private static <K, T> Map<K, String> resolveIds(Map<K, ? extends T> map, Function<? super T, String> idLookup) {
      Map<K, String> result = new LinkedHashMap<>();

      for (Entry<K, ? extends T> entry : map.entrySet()) {
         K key = entry.getKey();
         T value = (T)entry.getValue();
         String id = idLookup.apply(value);
         result.put(key, id);
      }

      return result;
   }

   private static <T> Map<T, String> computeIdMap(String prefix, Collection<? extends T> elements) {
      Map<T, String> ids = new LinkedHashMap<>();
      int index = 0;

      for (T element : elements) {
         ids.put(element, prefix + "_" + index);
         index++;
      }

      return ids;
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
            GltfCreatorV1.SamplerInfo other = (GltfCreatorV1.SamplerInfo)object;
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
