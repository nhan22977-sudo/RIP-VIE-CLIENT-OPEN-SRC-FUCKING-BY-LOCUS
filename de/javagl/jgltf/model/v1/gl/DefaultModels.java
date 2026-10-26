package de.javagl.jgltf.model.v1.gl;

import de.javagl.jgltf.impl.v1.Material;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.impl.v1.Technique;
import de.javagl.jgltf.impl.v1.TechniqueParameters;
import de.javagl.jgltf.impl.v1.TechniqueStates;
import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.gl.TechniqueParametersModel;
import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;
import de.javagl.jgltf.model.gl.impl.DefaultProgramModel;
import de.javagl.jgltf.model.gl.impl.DefaultShaderModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueParametersModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueStatesModel;
import de.javagl.jgltf.model.io.Buffers;
import de.javagl.jgltf.model.io.IO;
import de.javagl.jgltf.model.v1.MaterialModelV1;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class DefaultModels {
   private static final Logger logger = Logger.getLogger(DefaultModels.class.getName());
   private static final DefaultShaderModel DEFAULT_VERTEX_SHADER_MODEL;
   private static final DefaultShaderModel DEFAULT_FRAGMENT_SHADER_MODEL;
   private static final DefaultProgramModel DEFAULT_PROGRAM_MODEL = new DefaultProgramModel();
   private static final DefaultTechniqueModel DEFAULT_TECHNIQUE_MODEL = new DefaultTechniqueModel();
   private static final MaterialModelV1 DEFAULT_MATERIAL_MODEL = new MaterialModelV1();

   public static MaterialModel getDefaultMaterialModel() {
      return DEFAULT_MATERIAL_MODEL;
   }

   private static void initShaderData(DefaultShaderModel shaderModel) {
      try {
         URI uri = new URI(shaderModel.getUri());
         byte[] data = IO.read(uri);
         ByteBuffer shaderData = Buffers.create(data);
         shaderModel.setShaderData(shaderData);
      } catch (URISyntaxException | IOException e) {
         logger.severe("Failed to initialize shader data");
      }
   }

   public static TechniqueModel getDefaultTechniqueModel() {
      return DEFAULT_TECHNIQUE_MODEL;
   }

   private static void addParametersForDefaultTechnique(Technique technique, DefaultTechniqueModel techniqueModel) {
      Map<String, TechniqueParameters> parameters = Optionals.of(technique.getParameters());

      for (Entry<String, TechniqueParameters> entry : parameters.entrySet()) {
         String parameterName = entry.getKey();
         TechniqueParameters parameter = entry.getValue();
         int type = parameter.getType();
         int count = Optionals.of(parameter.getCount(), 1);
         String semantic = parameter.getSemantic();
         Object value = parameter.getValue();
         NodeModel nodeModel = null;
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

   private DefaultModels() {
   }

   static {
      Shader vertexShader = GltfDefaults.getDefaultVertexShader();
      DEFAULT_VERTEX_SHADER_MODEL = new DefaultShaderModel(vertexShader.getUri(), ShaderModel.ShaderType.VERTEX_SHADER);
      initShaderData(DEFAULT_VERTEX_SHADER_MODEL);
      Shader fragmentShader = GltfDefaults.getDefaultFragmentShader();
      DEFAULT_FRAGMENT_SHADER_MODEL = new DefaultShaderModel(fragmentShader.getUri(), ShaderModel.ShaderType.FRAGMENT_SHADER);
      initShaderData(DEFAULT_FRAGMENT_SHADER_MODEL);
      DEFAULT_PROGRAM_MODEL.setVertexShaderModel(DEFAULT_VERTEX_SHADER_MODEL);
      DEFAULT_PROGRAM_MODEL.setFragmentShaderModel(DEFAULT_FRAGMENT_SHADER_MODEL);
      Technique technique = GltfDefaults.getDefaultTechnique();
      DEFAULT_TECHNIQUE_MODEL.setProgramModel(DEFAULT_PROGRAM_MODEL);
      addParametersForDefaultTechnique(technique, DEFAULT_TECHNIQUE_MODEL);
      addAttributes(technique, DEFAULT_TECHNIQUE_MODEL);
      addUniforms(technique, DEFAULT_TECHNIQUE_MODEL);
      TechniqueStates states = technique.getStates();
      List<Integer> enable = Optionals.of(states.getEnable(), states.defaultEnable());
      TechniqueStatesFunctions functions = states.getFunctions();
      TechniqueStatesFunctionsModel techniqueStatesFunctionsModel = TechniqueStatesFunctionsModels.create(functions);
      TechniqueStatesModel techniqueStatesModel = new DefaultTechniqueStatesModel(enable, techniqueStatesFunctionsModel);
      DEFAULT_TECHNIQUE_MODEL.setTechniqueStatesModel(techniqueStatesModel);
      Material material = GltfDefaults.getDefaultMaterial();
      DEFAULT_MATERIAL_MODEL.setValues(material.getValues());
      DEFAULT_MATERIAL_MODEL.setTechniqueModel(DEFAULT_TECHNIQUE_MODEL);
   }
}
