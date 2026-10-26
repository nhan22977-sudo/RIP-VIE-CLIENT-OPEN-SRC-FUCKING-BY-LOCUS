package de.javagl.jgltf.model.structure;

import de.javagl.jgltf.model.AccessorData;
import de.javagl.jgltf.model.AccessorDatas;
import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.CameraModel;
import de.javagl.jgltf.model.CameraOrthographicModel;
import de.javagl.jgltf.model.CameraPerspectiveModel;
import de.javagl.jgltf.model.ElementType;
import de.javagl.jgltf.model.GltfException;
import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.ImageModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MeshModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.SceneModel;
import de.javagl.jgltf.model.SkinModel;
import de.javagl.jgltf.model.TextureModel;
import de.javagl.jgltf.model.gl.ProgramModel;
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
import de.javagl.jgltf.model.v1.GltfModelV1;
import de.javagl.jgltf.model.v1.MaterialModelV1;
import de.javagl.jgltf.model.v2.MaterialModelV2;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GltfModelStructures {
   private static final Logger logger = Logger.getLogger(GltfModelStructures.class.getName());
   private DefaultGltfModel source;
   private DefaultGltfModel target;
   private Map<AccessorModel, DefaultAccessorModel> accessorModelsMap;
   private Map<AnimationModel, DefaultAnimationModel> animationModelsMap;
   private Map<CameraModel, DefaultCameraModel> cameraModelsMap;
   private Map<ImageModel, DefaultImageModel> imageModelsMap;
   private Map<MaterialModel, MaterialModel> materialModelsMap;
   private Map<MeshModel, DefaultMeshModel> meshModelsMap;
   private Map<NodeModel, DefaultNodeModel> nodeModelsMap;
   private Map<SceneModel, DefaultSceneModel> sceneModelsMap;
   private Map<SkinModel, DefaultSkinModel> skinModelsMap;
   private Map<TextureModel, DefaultTextureModel> textureModelsMap;
   private Map<ShaderModel, DefaultShaderModel> shaderModelsMap;
   private Map<ProgramModel, DefaultProgramModel> programModelsMap;
   private Map<TechniqueModel, DefaultTechniqueModel> techniqueModelsMap;

   public void prepare(GltfModel sourceGltfModel) {
      this.source = (DefaultGltfModel)sourceGltfModel;
      GltfModelV1 sourceV1 = null;
      GltfModelV1 targetV1 = null;
      if (sourceGltfModel instanceof GltfModelV1) {
         sourceV1 = (GltfModelV1)sourceGltfModel;
         targetV1 = new GltfModelV1();
         this.target = targetV1;
      } else {
         this.target = new DefaultGltfModel();
      }

      copyGltfPropertyElements(this.source, this.target);
      this.accessorModelsMap = new LinkedHashMap<>();
      this.copyAccessorModels(this.source, this.target);
      this.animationModelsMap = computeMapping(this.source.getAnimationModels(), DefaultAnimationModel::new, this.target::addAnimationModel);
      this.cameraModelsMap = computeMapping(this.source.getCameraModels(), DefaultCameraModel::new, this.target::addCameraModel);
      this.imageModelsMap = computeMapping(this.source.getImageModels(), DefaultImageModel::new, this.target::addImageModel);
      if (sourceV1 != null && targetV1 != null) {
         this.materialModelsMap = computeMapping(this.source.getMaterialModels(), MaterialModelV1::new, this.target::addMaterialModel);
      } else {
         this.materialModelsMap = computeMapping(this.source.getMaterialModels(), MaterialModelV2::new, this.target::addMaterialModel);
      }

      this.meshModelsMap = computeMapping(this.source.getMeshModels(), DefaultMeshModel::new, this.target::addMeshModel);
      this.nodeModelsMap = computeMapping(this.source.getNodeModels(), DefaultNodeModel::new, this.target::addNodeModel);
      this.sceneModelsMap = computeMapping(this.source.getSceneModels(), DefaultSceneModel::new, this.target::addSceneModel);
      this.skinModelsMap = computeMapping(this.source.getSkinModels(), DefaultSkinModel::new, this.target::addSkinModel);
      this.textureModelsMap = computeMapping(this.source.getTextureModels(), DefaultTextureModel::new, this.target::addTextureModel);
      if (sourceV1 != null && targetV1 != null) {
         this.shaderModelsMap = computeMapping(sourceV1.getShaderModels(), DefaultShaderModel::new, targetV1::addShaderModel);
         this.programModelsMap = computeMapping(sourceV1.getProgramModels(), DefaultProgramModel::new, targetV1::addProgramModel);
         this.techniqueModelsMap = computeMapping(sourceV1.getTechniqueModels(), DefaultTechniqueModel::new, targetV1::addTechniqueModel);
      }

      this.initAnimationModels();
      this.initImageModels();
      if (sourceV1 != null && targetV1 != null) {
         this.initTechniqueModels();
      }

      this.initMeshModels();
      this.initNodeModels();
      this.initSceneModels();
      this.initSkinModels();
      this.initTextureModels();
      this.initMaterialModels();
      this.initCameraModels();
      if (sourceV1 != null && targetV1 != null) {
         this.initShaderModels();
         this.initProgramModels();
      }

      this.initExtensionsModel();
      this.initAssetModel();
   }

   public DefaultGltfModel createDefault() {
      DefaultBufferBuilderStrategy.Config config = new DefaultBufferBuilderStrategy.Config();
      return this.create(config);
   }

   public DefaultGltfModel createBinary() {
      DefaultBufferBuilderStrategy.Config config = new DefaultBufferBuilderStrategy.Config();
      config.imagesInBufferViews = true;
      return this.create(config);
   }

   public DefaultGltfModel createCustom() {
      DefaultBufferBuilderStrategy.Config config = new DefaultBufferBuilderStrategy.Config();
      config.bufferForAnimations = true;
      config.bufferForMeshes = true;
      config.bufferPerMesh = true;
      config.bufferPerMeshPrimitive = true;
      config.bufferForAnimations = true;
      return this.create(config);
   }

   private DefaultGltfModel create(DefaultBufferBuilderStrategy.Config config) {
      if (this.target == null) {
         throw new GltfException("The 'prepare' method has not bee called");
      }

      Level level = Level.FINE;
      if (logger.isLoggable(level)) {
         StringBuilder sb = new StringBuilder();
         sb.append("Creating model with configuration:\n");
         sb.append("  bufferPerMeshPrimitive : " + config.bufferPerMeshPrimitive + "\n");
         sb.append("  bufferPerMesh : " + config.bufferPerMesh + "\n");
         sb.append("  bufferForMeshes : " + config.bufferForMeshes + "\n");
         sb.append("  bufferPerAnimation : " + config.bufferPerAnimation + "\n");
         sb.append("  bufferForAnimations : " + config.bufferForAnimations + "\n");
         sb.append("  bufferPerSkin : " + config.bufferPerSkin + "\n");
         sb.append("  bufferForSkins : " + config.bufferForSkins + "\n");
         sb.append("  bufferPerImage : " + config.bufferPerImage + "\n");
         sb.append("  bufferForImages : " + config.bufferForImages + "\n");
         sb.append("  bufferForAdditionalAccessors : " + config.bufferForAdditionalAccessors + "\n");
         sb.append("  imagesInBufferViews : " + config.imagesInBufferViews + "\n");
         logger.log(level, sb.toString());
      }

      BufferBuilderStrategy bbs = BufferBuilderStrategies.create(config);
      bbs.process(this.target);

      for (DefaultImageModel imageModel : this.imageModelsMap.values()) {
         bbs.validateImageModel(imageModel);
      }

      this.target.addBufferViewModels(bbs.getBufferViewModels());
      this.target.addBufferModels(bbs.getBufferModels());
      DefaultGltfModel result = this.target;
      this.source = null;
      this.target = null;
      this.accessorModelsMap = null;
      this.animationModelsMap = null;
      this.cameraModelsMap = null;
      this.imageModelsMap = null;
      this.materialModelsMap = null;
      this.meshModelsMap = null;
      this.nodeModelsMap = null;
      this.sceneModelsMap = null;
      this.skinModelsMap = null;
      this.textureModelsMap = null;
      this.shaderModelsMap = null;
      this.programModelsMap = null;
      this.techniqueModelsMap = null;
      return result;
   }

   private static void copyGltfPropertyElements(AbstractModelElement source, AbstractModelElement target) {
      target.setExtensions(source.getExtensions());
      target.setExtras(source.getExtras());
   }

   private static void copyGltfChildOfRootPropertyElements(AbstractNamedModelElement source, AbstractNamedModelElement target) {
      target.setName(source.getName());
      copyGltfPropertyElements(source, target);
   }

   private void copyAccessorModels(DefaultGltfModel source, DefaultGltfModel target) {
      for (AccessorModel input : source.getAccessorModels()) {
         DefaultAccessorModel output = copy((DefaultAccessorModel)input);
         target.addAccessorModel(output);
         this.accessorModelsMap.put(input, output);
      }
   }

   private static DefaultAccessorModel copy(DefaultAccessorModel input) {
      AccessorData inputAccessorData = input.getAccessorData();
      ByteBuffer byteBuffer = inputAccessorData.createByteBuffer();
      int componentType = input.getComponentType();
      ElementType elementType = input.getElementType();
      int count = input.getCount();
      boolean normalized = input.isNormalized();
      DefaultAccessorModel output = new DefaultAccessorModel(componentType, count, elementType);
      output.setNormalized(normalized);
      output.setAccessorData(AccessorDatas.create(output, byteBuffer));
      copyGltfChildOfRootPropertyElements(input, output);
      return output;
   }

   private void initAnimationModels() {
      List<AnimationModel> sourceAnimationModels = this.source.getAnimationModels();

      for (int i = 0; i < sourceAnimationModels.size(); i++) {
         DefaultAnimationModel sourceAnimationModel = (DefaultAnimationModel)sourceAnimationModels.get(i);
         DefaultAnimationModel targetAnimationModel = this.animationModelsMap.get(sourceAnimationModel);
         copyGltfChildOfRootPropertyElements(sourceAnimationModel, targetAnimationModel);

         for (AnimationModel.Channel sourceChannel : sourceAnimationModel.getChannels()) {
            AnimationModel.Channel targetChannel = this.copyChannel(sourceChannel);
            targetAnimationModel.addChannel(targetChannel);
         }
      }
   }

   private AnimationModel.Channel copyChannel(AnimationModel.Channel sourceChannel) {
      AnimationModel.Sampler sourceSampler = sourceChannel.getSampler();
      AccessorModel sourceInput = sourceSampler.getInput();
      AccessorModel sourceOutput = sourceSampler.getOutput();
      DefaultAccessorModel targetInput = this.accessorModelsMap.get(sourceInput);
      DefaultAccessorModel targetOutput = this.accessorModelsMap.get(sourceOutput);
      AnimationModel.Interpolation interpolation = sourceSampler.getInterpolation();
      AnimationModel.Sampler targetSampler = new DefaultAnimationModel.DefaultSampler(targetInput, interpolation, targetOutput);
      NodeModel sourceNodeModel = sourceChannel.getNodeModel();
      DefaultNodeModel targetNodeModel = this.nodeModelsMap.get(sourceNodeModel);
      String path = sourceChannel.getPath();
      return new DefaultAnimationModel.DefaultChannel(targetSampler, targetNodeModel, path);
   }

   private void initMeshModels() {
      List<MeshModel> sourceMeshModels = this.source.getMeshModels();

      for (int i = 0; i < sourceMeshModels.size(); i++) {
         DefaultMeshModel sourceMeshModel = (DefaultMeshModel)sourceMeshModels.get(i);
         DefaultMeshModel targetMeshModel = this.meshModelsMap.get(sourceMeshModel);
         copyGltfChildOfRootPropertyElements(sourceMeshModel, targetMeshModel);

         for (MeshPrimitiveModel sourceMeshPrimitiveModel : sourceMeshModel.getMeshPrimitiveModels()) {
            DefaultMeshPrimitiveModel targetMeshPrimitiveModel = this.copyMeshPrimitiveModel((DefaultMeshPrimitiveModel)sourceMeshPrimitiveModel);
            targetMeshModel.addMeshPrimitiveModel(targetMeshPrimitiveModel);
         }

         float[] weights = sourceMeshModel.getWeights();
         targetMeshModel.setWeights(Optionals.clone(weights));
      }
   }

   private DefaultMeshPrimitiveModel copyMeshPrimitiveModel(DefaultMeshPrimitiveModel sourceMeshPrimitiveModel) {
      int mode = sourceMeshPrimitiveModel.getMode();
      DefaultMeshPrimitiveModel targetMeshPrimitiveModel = new DefaultMeshPrimitiveModel(mode);
      copyGltfPropertyElements(sourceMeshPrimitiveModel, targetMeshPrimitiveModel);
      AccessorModel sourceIndices = sourceMeshPrimitiveModel.getIndices();
      DefaultAccessorModel targetIndices = this.accessorModelsMap.get(sourceIndices);
      targetMeshPrimitiveModel.setIndices(targetIndices);
      Map<String, AccessorModel> sourceAttributes = sourceMeshPrimitiveModel.getAttributes();

      for (Entry<String, AccessorModel> sourceEntry : sourceAttributes.entrySet()) {
         String name = sourceEntry.getKey();
         AccessorModel sourceAttribute = sourceEntry.getValue();
         DefaultAccessorModel targetAttribute = this.accessorModelsMap.get(sourceAttribute);
         targetMeshPrimitiveModel.putAttribute(name, targetAttribute);
      }

      for (Map<String, AccessorModel> sourceTarget : sourceMeshPrimitiveModel.getTargets()) {
         Map<String, AccessorModel> targetTarget = new LinkedHashMap<>();

         for (Entry<String, AccessorModel> sourceEntry : sourceTarget.entrySet()) {
            String name = sourceEntry.getKey();
            AccessorModel sourceTargetValue = sourceEntry.getValue();
            DefaultAccessorModel targetTargetValue = this.accessorModelsMap.get(sourceTargetValue);
            targetTarget.put(name, targetTargetValue);
         }

         targetMeshPrimitiveModel.addTarget(targetTarget);
      }

      MaterialModel sourceMaterial = sourceMeshPrimitiveModel.getMaterialModel();
      MaterialModel targetMaterial = this.materialModelsMap.get(sourceMaterial);
      targetMeshPrimitiveModel.setMaterialModel(targetMaterial);
      return targetMeshPrimitiveModel;
   }

   private void initNodeModels() {
      List<NodeModel> sourceNodeModels = this.source.getNodeModels();

      for (int i = 0; i < sourceNodeModels.size(); i++) {
         DefaultNodeModel sourceNodeModel = (DefaultNodeModel)sourceNodeModels.get(i);
         DefaultNodeModel targetNodeModel = this.nodeModelsMap.get(sourceNodeModel);
         copyGltfChildOfRootPropertyElements(sourceNodeModel, targetNodeModel);

         for (NodeModel sourceChild : sourceNodeModel.getChildren()) {
            DefaultNodeModel targetChild = this.nodeModelsMap.get(sourceChild);
            targetNodeModel.addChild(targetChild);
         }

         for (MeshModel sourceMesh : sourceNodeModel.getMeshModels()) {
            DefaultMeshModel targetMesh = this.meshModelsMap.get(sourceMesh);
            targetNodeModel.addMeshModel(targetMesh);
         }

         SkinModel sourceSkin = sourceNodeModel.getSkinModel();
         DefaultSkinModel targetSkin = this.skinModelsMap.get(sourceSkin);
         targetNodeModel.setSkinModel(targetSkin);
         CameraModel sourceCamera = sourceNodeModel.getCameraModel();
         DefaultCameraModel targetCamera = this.cameraModelsMap.get(sourceCamera);
         targetNodeModel.setCameraModel(targetCamera);
         float[] matrix = sourceNodeModel.getMatrix();
         float[] translation = sourceNodeModel.getTranslation();
         float[] rotation = sourceNodeModel.getRotation();
         float[] scale = sourceNodeModel.getScale();
         float[] weights = sourceNodeModel.getWeights();
         targetNodeModel.setMatrix(Optionals.clone(matrix));
         targetNodeModel.setTranslation(Optionals.clone(translation));
         targetNodeModel.setRotation(Optionals.clone(rotation));
         targetNodeModel.setScale(Optionals.clone(scale));
         targetNodeModel.setWeights(Optionals.clone(weights));
      }
   }

   private void initSceneModels() {
      List<SceneModel> sourceSceneModels = this.source.getSceneModels();

      for (int i = 0; i < sourceSceneModels.size(); i++) {
         DefaultSceneModel sourceSceneModel = (DefaultSceneModel)sourceSceneModels.get(i);
         DefaultSceneModel targetSceneModel = this.sceneModelsMap.get(sourceSceneModel);
         copyGltfChildOfRootPropertyElements(sourceSceneModel, targetSceneModel);

         for (NodeModel sourceNodeModel : sourceSceneModel.getNodeModels()) {
            DefaultNodeModel targetNodeModel = this.nodeModelsMap.get(sourceNodeModel);
            targetSceneModel.addNode(targetNodeModel);
         }
      }
   }

   private void initSkinModels() {
      List<SkinModel> sourceSkinModels = this.source.getSkinModels();

      for (int i = 0; i < sourceSkinModels.size(); i++) {
         DefaultSkinModel sourceSkinModel = (DefaultSkinModel)sourceSkinModels.get(i);
         DefaultSkinModel targetSkinModel = this.skinModelsMap.get(sourceSkinModel);
         copyGltfChildOfRootPropertyElements(sourceSkinModel, targetSkinModel);

         for (NodeModel sourceJoint : sourceSkinModel.getJoints()) {
            DefaultNodeModel targetJoint = this.nodeModelsMap.get(sourceJoint);
            targetSkinModel.addJoint(targetJoint);
         }

         AccessorModel sourceInverseBindMatrices = sourceSkinModel.getInverseBindMatrices();
         DefaultAccessorModel targetInverseBindMatrices = this.accessorModelsMap.get(sourceInverseBindMatrices);
         targetSkinModel.setInverseBindMatrices(targetInverseBindMatrices);
         NodeModel sourceSkeleton = sourceSkinModel.getSkeleton();
         DefaultNodeModel targetSkeleton = this.nodeModelsMap.get(sourceSkeleton);
         targetSkinModel.setSkeleton(targetSkeleton);
      }
   }

   private void initTextureModels() {
      List<TextureModel> sourceTextureModels = this.source.getTextureModels();

      for (int i = 0; i < sourceTextureModels.size(); i++) {
         DefaultTextureModel sourceTextureModel = (DefaultTextureModel)sourceTextureModels.get(i);
         DefaultTextureModel targetTextureModel = this.textureModelsMap.get(sourceTextureModel);
         copyGltfChildOfRootPropertyElements(sourceTextureModel, targetTextureModel);
         targetTextureModel.setMagFilter(sourceTextureModel.getMagFilter());
         targetTextureModel.setMinFilter(sourceTextureModel.getMinFilter());
         targetTextureModel.setWrapS(sourceTextureModel.getWrapS());
         targetTextureModel.setWrapT(sourceTextureModel.getWrapT());
         ImageModel sourceImageModel = sourceTextureModel.getImageModel();
         DefaultImageModel targetImageModel = this.imageModelsMap.get(sourceImageModel);
         targetTextureModel.setImageModel(targetImageModel);
      }
   }

   private void initImageModels() {
      List<ImageModel> sourceImageModels = this.source.getImageModels();

      for (int i = 0; i < sourceImageModels.size(); i++) {
         DefaultImageModel sourceImageModel = (DefaultImageModel)sourceImageModels.get(i);
         DefaultImageModel targetImageModel = this.imageModelsMap.get(sourceImageModel);
         copyGltfChildOfRootPropertyElements(sourceImageModel, targetImageModel);
         targetImageModel.setUri(sourceImageModel.getUri());
         targetImageModel.setMimeType(sourceImageModel.getMimeType());
         targetImageModel.setImageData(sourceImageModel.getImageData());
      }
   }

   private void initTechniqueModels() {
      if (this.source instanceof GltfModelV1) {
         GltfModelV1 sourceV1 = (GltfModelV1)this.source;
         List<TechniqueModel> sourceTechniqueModels = sourceV1.getTechniqueModels();

         for (int i = 0; i < sourceTechniqueModels.size(); i++) {
            DefaultTechniqueModel sourceTechniqueModel = (DefaultTechniqueModel)sourceTechniqueModels.get(i);
            DefaultTechniqueModel targetTechniqueModel = this.techniqueModelsMap.get(sourceTechniqueModel);
            copyGltfChildOfRootPropertyElements(sourceTechniqueModel, targetTechniqueModel);
            this.initTechniqueModel(sourceTechniqueModel, targetTechniqueModel);
         }
      }
   }

   private void initTechniqueModel(DefaultTechniqueModel sourceTechniqueModel, DefaultTechniqueModel targetTechniqueModel) {
      ProgramModel sourceProgramModel = sourceTechniqueModel.getProgramModel();
      DefaultProgramModel targetProgramModel = this.programModelsMap.get(sourceProgramModel);
      targetTechniqueModel.setProgramModel(targetProgramModel);
      Map<String, TechniqueParametersModel> sourceParameters = sourceTechniqueModel.getParameters();

      for (Entry<String, TechniqueParametersModel> entry : sourceParameters.entrySet()) {
         String parameterName = entry.getKey();
         TechniqueParametersModel sourceValue = entry.getValue();
         int type = sourceValue.getType();
         int count = sourceValue.getCount();
         String semantic = sourceValue.getSemantic();
         Object value = sourceValue.getValue();
         NodeModel sourceNodeModel = sourceValue.getNodeModel();
         NodeModel targetNodeModel = this.nodeModelsMap.get(sourceNodeModel);
         DefaultTechniqueParametersModel targetValue = new DefaultTechniqueParametersModel(type, count, semantic, value, targetNodeModel);
         targetTechniqueModel.addParameter(parameterName, targetValue);
      }

      Map<String, String> sourceAttributes = sourceTechniqueModel.getAttributes();

      for (Entry<String, String> entry : sourceAttributes.entrySet()) {
         String attributeName = entry.getKey();
         String parameterName = entry.getValue();
         targetTechniqueModel.addAttribute(attributeName, parameterName);
      }

      Map<String, String> sourceUniforms = sourceTechniqueModel.getUniforms();

      for (Entry<String, String> entry : sourceUniforms.entrySet()) {
         String uniformName = entry.getKey();
         String parameterName = entry.getValue();
         targetTechniqueModel.addUniform(uniformName, parameterName);
      }

      TechniqueStatesModel sourceTechniqueStatesModel = sourceTechniqueModel.getTechniqueStatesModel();
      if (sourceTechniqueStatesModel != null) {
         List<Integer> targetEnable = null;
         List<Integer> sourceEnable = sourceTechniqueStatesModel.getEnable();
         if (sourceEnable != null) {
            targetEnable = new ArrayList<>(sourceEnable);
         }

         DefaultTechniqueStatesFunctionsModel targetTechniqueStatesFunctionsModel = new DefaultTechniqueStatesFunctionsModel();
         DefaultTechniqueStatesModel targetTechniqueStatesModel = new DefaultTechniqueStatesModel(targetEnable, targetTechniqueStatesFunctionsModel);
         targetTechniqueModel.setTechniqueStatesModel(targetTechniqueStatesModel);
      }
   }

   private void initMaterialModels() {
      if (this.target instanceof GltfModelV1) {
         this.initMaterialModelsV1();
      } else {
         this.initMaterialModelsV2();
      }
   }

   private void initMaterialModelsV1() {
      List<MaterialModel> sourceMaterialModels = this.source.getMaterialModels();

      for (int i = 0; i < sourceMaterialModels.size(); i++) {
         MaterialModelV1 sourceMaterialModel = (MaterialModelV1)sourceMaterialModels.get(i);
         MaterialModelV1 targetMaterialModel = (MaterialModelV1)this.materialModelsMap.get(sourceMaterialModel);
         copyGltfChildOfRootPropertyElements(sourceMaterialModel, targetMaterialModel);
         this.initMaterialModel(sourceMaterialModel, targetMaterialModel);
      }
   }

   private void initMaterialModel(MaterialModelV1 sourceMaterialModel, MaterialModelV1 targetMaterialModel) {
      TechniqueModel sourceTechniqueModel = sourceMaterialModel.getTechniqueModel();
      TechniqueModel targetTechniqueModel = this.techniqueModelsMap.get(sourceTechniqueModel);
      targetMaterialModel.setTechniqueModel(targetTechniqueModel);
      Map<String, Object> sourceValues = sourceMaterialModel.getValues();
      Map<String, Object> targetValues = new LinkedHashMap<>();
      Map<String, TechniqueParametersModel> sourceParameters = sourceTechniqueModel.getParameters();

      for (Entry<String, Object> entry : sourceValues.entrySet()) {
         String parameterName = entry.getKey();
         Object sourceValue = entry.getValue();
         TechniqueParametersModel sourceTechniqueParametersModel = sourceParameters.get(parameterName);
         if (sourceTechniqueParametersModel == null || sourceTechniqueParametersModel.getType() != 35678) {
            targetValues.put(parameterName, entry.getValue());
         } else if (sourceValue instanceof TextureModel) {
            TextureModel targetValue = this.textureModelsMap.get(sourceValue);
            targetValues.put(parameterName, targetValue);
         }
      }

      targetMaterialModel.setValues(targetValues);
   }

   private void initMaterialModelsV2() {
      List<MaterialModel> sourceMaterialModels = this.source.getMaterialModels();

      for (int i = 0; i < sourceMaterialModels.size(); i++) {
         MaterialModelV2 sourceMaterialModel = (MaterialModelV2)sourceMaterialModels.get(i);
         MaterialModelV2 targetMaterialModel = (MaterialModelV2)this.materialModelsMap.get(sourceMaterialModel);
         copyGltfChildOfRootPropertyElements(sourceMaterialModel, targetMaterialModel);
         this.initMaterialModel(sourceMaterialModel, targetMaterialModel);
      }
   }

   private void initMaterialModel(MaterialModelV2 sourceMaterialModel, MaterialModelV2 targetMaterialModel) {
      targetMaterialModel.setAlphaMode(sourceMaterialModel.getAlphaMode());
      targetMaterialModel.setAlphaCutoff(sourceMaterialModel.getAlphaCutoff());
      targetMaterialModel.setDoubleSided(sourceMaterialModel.isDoubleSided());
      TextureModel sourceBaseColorTexture = sourceMaterialModel.getBaseColorTexture();
      DefaultTextureModel targetBaseColorTexture = this.textureModelsMap.get(sourceBaseColorTexture);
      targetMaterialModel.setBaseColorTexture(targetBaseColorTexture);
      targetMaterialModel.setBaseColorTexcoord(sourceMaterialModel.getBaseColorTexcoord());
      float[] baseColorFactor = sourceMaterialModel.getBaseColorFactor();
      targetMaterialModel.setBaseColorFactor(Optionals.clone(baseColorFactor));
      TextureModel sourceMetallicRoughnessTexture = sourceMaterialModel.getMetallicRoughnessTexture();
      DefaultTextureModel targetMetallicRoughnessTexture = this.textureModelsMap.get(sourceMetallicRoughnessTexture);
      targetMaterialModel.setMetallicRoughnessTexture(targetMetallicRoughnessTexture);
      targetMaterialModel.setMetallicRoughnessTexcoord(sourceMaterialModel.getMetallicRoughnessTexcoord());
      targetMaterialModel.setMetallicFactor(sourceMaterialModel.getMetallicFactor());
      targetMaterialModel.setRoughnessFactor(sourceMaterialModel.getRoughnessFactor());
      TextureModel sourceNormalTexture = sourceMaterialModel.getNormalTexture();
      DefaultTextureModel targetNormalTexture = this.textureModelsMap.get(sourceNormalTexture);
      targetMaterialModel.setNormalTexture(targetNormalTexture);
      targetMaterialModel.setNormalTexcoord(sourceMaterialModel.getNormalTexcoord());
      targetMaterialModel.setNormalScale(sourceMaterialModel.getNormalScale());
      TextureModel sourceOcclusionTexture = sourceMaterialModel.getOcclusionTexture();
      DefaultTextureModel targetOcclusionTexture = this.textureModelsMap.get(sourceOcclusionTexture);
      targetMaterialModel.setOcclusionTexture(targetOcclusionTexture);
      targetMaterialModel.setOcclusionTexcoord(sourceMaterialModel.getOcclusionTexcoord());
      targetMaterialModel.setOcclusionStrength(sourceMaterialModel.getOcclusionStrength());
      TextureModel sourceEmissiveTexture = sourceMaterialModel.getEmissiveTexture();
      DefaultTextureModel targetEmissiveTexture = this.textureModelsMap.get(sourceEmissiveTexture);
      targetMaterialModel.setEmissiveTexture(targetEmissiveTexture);
      targetMaterialModel.setEmissiveTexcoord(sourceMaterialModel.getEmissiveTexcoord());
      float[] emissiveFactor = sourceMaterialModel.getEmissiveFactor();
      targetMaterialModel.setEmissiveFactor(Optionals.clone(emissiveFactor));
   }

   private void initCameraModels() {
      List<CameraModel> sourceCameraModels = this.source.getCameraModels();

      for (int i = 0; i < sourceCameraModels.size(); i++) {
         DefaultCameraModel sourceCameraModel = (DefaultCameraModel)sourceCameraModels.get(i);
         DefaultCameraModel targetCameraModel = this.cameraModelsMap.get(sourceCameraModel);
         copyGltfChildOfRootPropertyElements(sourceCameraModel, targetCameraModel);
         CameraPerspectiveModel sourceCameraPerspectiveModel = sourceCameraModel.getCameraPerspectiveModel();
         if (sourceCameraPerspectiveModel != null) {
            DefaultCameraPerspectiveModel targetCameraPerspectiveModel = new DefaultCameraPerspectiveModel();
            targetCameraPerspectiveModel.setAspectRatio(sourceCameraPerspectiveModel.getAspectRatio());
            targetCameraPerspectiveModel.setYfov(sourceCameraPerspectiveModel.getYfov());
            targetCameraPerspectiveModel.setZfar(sourceCameraPerspectiveModel.getZfar());
            targetCameraPerspectiveModel.setZnear(sourceCameraPerspectiveModel.getZnear());
            targetCameraModel.setCameraPerspectiveModel(targetCameraPerspectiveModel);
         }

         CameraOrthographicModel sourceCameraOrthographicModel = sourceCameraModel.getCameraOrthographicModel();
         if (sourceCameraOrthographicModel != null) {
            DefaultCameraOrthographicModel targetCameraOrthographicModel = new DefaultCameraOrthographicModel();
            targetCameraOrthographicModel.setXmag(sourceCameraOrthographicModel.getXmag());
            targetCameraOrthographicModel.setYmag(sourceCameraOrthographicModel.getYmag());
            targetCameraOrthographicModel.setZfar(sourceCameraOrthographicModel.getZfar());
            targetCameraOrthographicModel.setZnear(sourceCameraOrthographicModel.getZnear());
            targetCameraModel.setCameraOrthographicModel(targetCameraOrthographicModel);
         }
      }
   }

   private void initShaderModels() {
      if (this.source instanceof GltfModelV1) {
         GltfModelV1 sourceV1 = (GltfModelV1)this.source;
         List<ShaderModel> sourceShaderModels = sourceV1.getShaderModels();

         for (int i = 0; i < sourceShaderModels.size(); i++) {
            DefaultShaderModel sourceShaderModel = (DefaultShaderModel)sourceShaderModels.get(i);
            DefaultShaderModel targetShaderModel = this.shaderModelsMap.get(sourceShaderModel);
            copyGltfChildOfRootPropertyElements(sourceShaderModel, targetShaderModel);
            targetShaderModel.setUri(sourceShaderModel.getUri());
            targetShaderModel.setShaderType(sourceShaderModel.getShaderType());
            targetShaderModel.setShaderData(sourceShaderModel.getShaderData());
         }
      }
   }

   private void initProgramModels() {
      if (this.source instanceof GltfModelV1) {
         GltfModelV1 sourceV1 = (GltfModelV1)this.source;
         List<ProgramModel> sourceProgramModels = sourceV1.getProgramModels();

         for (int i = 0; i < sourceProgramModels.size(); i++) {
            DefaultProgramModel sourceProgramModel = (DefaultProgramModel)sourceProgramModels.get(i);
            DefaultProgramModel targetProgramModel = this.programModelsMap.get(sourceProgramModel);
            copyGltfChildOfRootPropertyElements(sourceProgramModel, targetProgramModel);
            ShaderModel sourceVertexShaderModel = sourceProgramModel.getVertexShaderModel();
            ShaderModel targetVertexShaderModel = this.shaderModelsMap.get(sourceVertexShaderModel);
            targetProgramModel.setVertexShaderModel(targetVertexShaderModel);
            ShaderModel sourceFragmentShaderModel = sourceProgramModel.getFragmentShaderModel();
            ShaderModel targetFragmentShaderModel = this.shaderModelsMap.get(sourceFragmentShaderModel);
            targetProgramModel.setFragmentShaderModel(targetFragmentShaderModel);

            for (String attribute : sourceProgramModel.getAttributes()) {
               targetProgramModel.addAttribute(attribute);
            }
         }
      }
   }

   private void initExtensionsModel() {
      DefaultExtensionsModel sourceExtensionsModel = this.source.getExtensionsModel();
      DefaultExtensionsModel targetExtensionsModel = this.target.getExtensionsModel();
      List<String> extensionsUsed = sourceExtensionsModel.getExtensionsUsed();
      targetExtensionsModel.addExtensionsUsed(extensionsUsed);
      List<String> extensionsRequired = sourceExtensionsModel.getExtensionsRequired();
      targetExtensionsModel.addExtensionsRequired(extensionsRequired);
   }

   private void initAssetModel() {
      DefaultAssetModel sourceAssetModel = this.source.getAssetModel();
      DefaultAssetModel targetAssetModel = this.target.getAssetModel();
      copyGltfChildOfRootPropertyElements(sourceAssetModel, targetAssetModel);
      targetAssetModel.setCopyright(sourceAssetModel.getCopyright());
      targetAssetModel.setGenerator(sourceAssetModel.getGenerator());
   }

   private static <S, T> Map<S, T> computeMapping(Iterable<? extends S> sources, Supplier<? extends T> supplier, Consumer<? super T> consumer) {
      Map<S, T> map = new LinkedHashMap<>();

      for (S s : sources) {
         T t = (T)supplier.get();
         consumer.accept(t);
         map.put(s, t);
      }

      return map;
   }
}
