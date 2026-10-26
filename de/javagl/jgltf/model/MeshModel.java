package de.javagl.jgltf.model;

import java.util.List;

public interface MeshModel extends NamedModelElement {
   List<MeshPrimitiveModel> getMeshPrimitiveModels();

   float[] getWeights();
}
