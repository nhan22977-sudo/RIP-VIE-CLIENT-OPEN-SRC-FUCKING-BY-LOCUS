package de.javagl.jgltf.model.v1.gl;

import de.javagl.jgltf.impl.v1.Technique;
import de.javagl.jgltf.impl.v1.TechniqueParameters;
import de.javagl.jgltf.impl.v1.TechniqueStates;
import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Techniques {
   static Technique createDefaultTechnique(String programId) {
      Technique technique = new Technique();
      technique.addAttributes("a_position", "position");
      technique.addParameters("modelViewMatrix", createDefaultTechniqueParameters("MODELVIEW", 35676, null));
      technique.addParameters("projectionMatrix", createDefaultTechniqueParameters("PROJECTION", 35676, null));
      technique.addParameters("emission", createDefaultTechniqueParameters(null, 35666, Arrays.asList(0.5F, 0.5F, 0.5F, 1.0F)));
      technique.addParameters("position", createDefaultTechniqueParameters("POSITION", 35665, null));
      technique.setStates(createDefaultTechniqueStates());
      technique.setProgram(programId);
      technique.addUniforms("u_modelViewMatrix", "modelViewMatrix");
      technique.addUniforms("u_projectionMatrix", "projectionMatrix");
      technique.addUniforms("u_emission", "emission");
      return technique;
   }

   private static TechniqueStates createDefaultTechniqueStates() {
      TechniqueStates techniqueStates = new TechniqueStates();
      techniqueStates.setEnable(new ArrayList(techniqueStates.defaultEnable()));
      techniqueStates.setFunctions(createDefaultTechniqueStatesFunctions());
      return techniqueStates;
   }

   public static TechniqueStatesFunctions createDefaultTechniqueStatesFunctions() {
      TechniqueStatesFunctions techniqueStatesFunctions = new TechniqueStatesFunctions();
      techniqueStatesFunctions.setBlendColor(techniqueStatesFunctions.defaultBlendColor());
      techniqueStatesFunctions.setBlendEquationSeparate(techniqueStatesFunctions.defaultBlendEquationSeparate());
      techniqueStatesFunctions.setBlendFuncSeparate(techniqueStatesFunctions.defaultBlendFuncSeparate());
      techniqueStatesFunctions.setColorMask(techniqueStatesFunctions.defaultColorMask());
      techniqueStatesFunctions.setCullFace(techniqueStatesFunctions.defaultCullFace());
      techniqueStatesFunctions.setDepthFunc(techniqueStatesFunctions.defaultDepthFunc());
      techniqueStatesFunctions.setDepthMask(techniqueStatesFunctions.defaultDepthMask());
      techniqueStatesFunctions.setDepthRange(techniqueStatesFunctions.defaultDepthRange());
      techniqueStatesFunctions.setFrontFace(techniqueStatesFunctions.defaultFrontFace());
      techniqueStatesFunctions.setLineWidth(techniqueStatesFunctions.defaultLineWidth());
      techniqueStatesFunctions.setPolygonOffset(techniqueStatesFunctions.defaultPolygonOffset());
      techniqueStatesFunctions.setScissor(techniqueStatesFunctions.defaultScissor());
      return techniqueStatesFunctions;
   }

   private static TechniqueParameters createDefaultTechniqueParameters(String semantic, Integer type, Object value) {
      TechniqueParameters techniqueParameters = new TechniqueParameters();
      techniqueParameters.setSemantic(semantic);
      techniqueParameters.setType(type);
      techniqueParameters.setValue(value);
      return techniqueParameters;
   }

   public static List<Integer> obtainEnabledStates(Technique technique) {
      TechniqueStates states = obtainTechniqueStates(technique);
      List<Integer> enable = states.getEnable();
      return enable == null ? states.defaultEnable() : enable;
   }

   private static TechniqueStates obtainTechniqueStates(Technique technique) {
      TechniqueStates states = technique.getStates();
      return states == null ? GltfDefaults.getDefaultTechnique().getStates() : states;
   }

   public static TechniqueStatesFunctions obtainTechniqueStatesFunctions(Technique technique) {
      TechniqueStates states = obtainTechniqueStates(technique);
      TechniqueStatesFunctions functions = states.getFunctions();
      if (functions == null) {
         TechniqueStates defaultStates = GltfDefaults.getDefaultTechnique().getStates();
         return defaultStates.getFunctions();
      } else {
         return functions;
      }
   }

   private Techniques() {
   }
}
