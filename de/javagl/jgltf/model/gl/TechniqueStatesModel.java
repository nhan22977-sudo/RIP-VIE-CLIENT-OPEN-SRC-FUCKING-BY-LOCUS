package de.javagl.jgltf.model.gl;

import java.util.Arrays;
import java.util.List;

public interface TechniqueStatesModel {
   List<Integer> getEnable();

   TechniqueStatesFunctionsModel getTechniqueStatesFunctionsModel();

   static List<Integer> getAllStates() {
      return Arrays.asList(3042, 2884, 2929, 32823, 32926, 3089);
   }
}
