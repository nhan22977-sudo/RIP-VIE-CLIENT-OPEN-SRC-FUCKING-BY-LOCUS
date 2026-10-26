package de.javagl.jgltf.model;

import java.util.List;
import java.util.Map;

public interface MeshPrimitiveModel extends ModelElement {
   Map<String, AccessorModel> getAttributes();

   AccessorModel getIndices();

   int getMode();

   MaterialModel getMaterialModel();

   List<Map<String, AccessorModel>> getTargets();
}
