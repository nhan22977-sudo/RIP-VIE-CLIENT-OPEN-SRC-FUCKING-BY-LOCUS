package de.javagl.jgltf.model.gl.impl;

import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultTechniqueStatesModel implements TechniqueStatesModel {
   private final List<Integer> enable;
   private final TechniqueStatesFunctionsModel techniqueStatesFunctionsModel;

   public DefaultTechniqueStatesModel(List<Integer> enable, TechniqueStatesFunctionsModel techniqueStatesFunctionsModel) {
      this.enable = Collections.unmodifiableList(new ArrayList<>(enable));
      this.techniqueStatesFunctionsModel = techniqueStatesFunctionsModel;
   }

   @Override
   public List<Integer> getEnable() {
      return this.enable;
   }

   @Override
   public TechniqueStatesFunctionsModel getTechniqueStatesFunctionsModel() {
      return this.techniqueStatesFunctionsModel;
   }
}
