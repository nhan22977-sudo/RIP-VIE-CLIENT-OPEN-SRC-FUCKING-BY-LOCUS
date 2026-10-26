package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.NodeModel;
import de.javagl.jgltf.model.SceneModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultSceneModel extends AbstractNamedModelElement implements SceneModel {
   private final List<NodeModel> nodeModels = new ArrayList<>();

   public void addNode(NodeModel node) {
      this.nodeModels.add(node);
   }

   @Override
   public List<NodeModel> getNodeModels() {
      return Collections.unmodifiableList(this.nodeModels);
   }
}
