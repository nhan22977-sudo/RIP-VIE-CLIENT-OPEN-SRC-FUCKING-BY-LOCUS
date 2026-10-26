package de.javagl.jgltf.model.gl.impl;

import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import de.javagl.jgltf.model.gl.TechniqueStatesFunctionsModel;
import de.javagl.jgltf.model.gl.TechniqueStatesModel;
import de.javagl.jgltf.model.v1.gl.TechniqueStatesFunctionsModels;
import de.javagl.jgltf.model.v1.gl.Techniques;
import java.util.Arrays;
import java.util.List;

public class TechniqueStatesModels {
   public static TechniqueStatesModel createDefault() {
      return new DefaultTechniqueStatesModel(createDefaultTechniqueStatesEnable(), createDefaultTechniqueStatesFunctions());
   }

   public static TechniqueStatesFunctionsModel createDefaultTechniqueStatesFunctions() {
      TechniqueStatesFunctions functions = Techniques.createDefaultTechniqueStatesFunctions();
      return TechniqueStatesFunctionsModels.create(functions);
   }

   public static List<Integer> createDefaultTechniqueStatesEnable() {
      return Arrays.asList(2929, 2884);
   }

   private TechniqueStatesModels() {
   }
}
