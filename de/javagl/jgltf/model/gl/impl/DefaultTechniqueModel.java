package de.javagl.jgltf.model.gl.impl;

import de.javagl.jgltf.model.gl.ProgramModel;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.gl.TechniqueParametersModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;
import de.javagl.jgltf.model.impl.AbstractNamedModelElement;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class DefaultTechniqueModel extends AbstractNamedModelElement implements TechniqueModel {
   private ProgramModel programModel;
   private final Map<String, TechniqueParametersModel> parameters = new LinkedHashMap<>();
   private final Map<String, String> attributes = new LinkedHashMap<>();
   private final Map<String, String> uniforms = new LinkedHashMap<>();
   private TechniqueStatesModel techniqueStatesModel;

   public void setProgramModel(ProgramModel programModel) {
      this.programModel = programModel;
   }

   @Override
   public ProgramModel getProgramModel() {
      return this.programModel;
   }

   @Override
   public Map<String, TechniqueParametersModel> getParameters() {
      return Collections.unmodifiableMap(this.parameters);
   }

   public void addAttribute(String attributeName, String parameterName) {
      Objects.requireNonNull(attributeName, "The attributeName may not be null");
      Objects.requireNonNull(parameterName, "The parameterName may not be null");
      this.attributes.put(attributeName, parameterName);
   }

   public void addParameter(String parameterName, TechniqueParametersModel techniqueParametersModel) {
      Objects.requireNonNull(parameterName, "The parameterName may not be null");
      Objects.requireNonNull(techniqueParametersModel, "The techniqueParametersModel may not be null");
      this.parameters.put(parameterName, techniqueParametersModel);
   }

   @Override
   public Map<String, String> getAttributes() {
      return Collections.unmodifiableMap(this.attributes);
   }

   @Override
   public TechniqueParametersModel getAttributeParameters(String attributeName) {
      String parameterName = this.attributes.get(attributeName);
      return this.parameters.get(parameterName);
   }

   public void addUniform(String uniformName, String parameterName) {
      Objects.requireNonNull(uniformName, "The uniformName may not be null");
      Objects.requireNonNull(parameterName, "The parameterName may not be null");
      this.uniforms.put(uniformName, parameterName);
   }

   @Override
   public Map<String, String> getUniforms() {
      return Collections.unmodifiableMap(this.uniforms);
   }

   @Override
   public TechniqueParametersModel getUniformParameters(String uniformName) {
      String parameterName = this.uniforms.get(uniformName);
      return this.parameters.get(parameterName);
   }

   public void setTechniqueStatesModel(TechniqueStatesModel techniqueStatesModel) {
      this.techniqueStatesModel = techniqueStatesModel;
   }

   @Override
   public TechniqueStatesModel getTechniqueStatesModel() {
      return this.techniqueStatesModel;
   }
}
