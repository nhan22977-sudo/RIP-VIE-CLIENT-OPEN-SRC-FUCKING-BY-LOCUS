package de.javagl.jgltf.model.gl.impl;

import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.gl.TechniqueParametersModel;

public class DefaultTechniqueParametersModel implements TechniqueParametersModel {
   private final int type;
   private final int count;
   private final String semantic;
   private final Object value;
   private final NodeModel nodeModel;

   public DefaultTechniqueParametersModel(int type, int count, String semantic, Object value, NodeModel nodeModel) {
      this.type = type;
      this.count = count;
      this.semantic = semantic;
      this.value = value;
      this.nodeModel = nodeModel;
   }

   @Override
   public int getType() {
      return this.type;
   }

   @Override
   public int getCount() {
      return this.count;
   }

   @Override
   public String getSemantic() {
      return this.semantic;
   }

   @Override
   public Object getValue() {
      return this.value;
   }

   @Override
   public NodeModel getNodeModel() {
      return this.nodeModel;
   }
}
