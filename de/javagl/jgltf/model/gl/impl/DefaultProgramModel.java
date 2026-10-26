package de.javagl.jgltf.model.gl.impl;

import de.javagl.jgltf.model.gl.ProgramModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.impl.AbstractNamedModelElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DefaultProgramModel extends AbstractNamedModelElement implements ProgramModel {
   private ShaderModel vertexShaderModel;
   private ShaderModel fragmentShaderModel;
   private final List<String> attributes = new ArrayList<>();

   public void addAttribute(String attribute) {
      this.attributes.add(attribute);
   }

   public void setVertexShaderModel(ShaderModel vertexShaderModel) {
      this.vertexShaderModel = Objects.requireNonNull(vertexShaderModel, "The vertexShaderModel may not be null");
   }

   @Override
   public ShaderModel getVertexShaderModel() {
      return this.vertexShaderModel;
   }

   public void setFragmentShaderModel(ShaderModel fragmentShaderModel) {
      this.fragmentShaderModel = Objects.requireNonNull(fragmentShaderModel, "The fragmentShaderModel may not be null");
   }

   @Override
   public ShaderModel getFragmentShaderModel() {
      return this.fragmentShaderModel;
   }

   @Override
   public List<String> getAttributes() {
      return Collections.unmodifiableList(this.attributes);
   }
}
