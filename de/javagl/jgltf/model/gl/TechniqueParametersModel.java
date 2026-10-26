package de.javagl.jgltf.model.gl;

import de.javagl.jgltf.model.NodeModel;

public interface TechniqueParametersModel {
   int getType();

   int getCount();

   String getSemantic();

   Object getValue();

   NodeModel getNodeModel();
}
