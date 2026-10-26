package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.MaterialModel;
import de.javagl.jgltf.model.MeshPrimitiveModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DefaultMeshPrimitiveModel extends AbstractModelElement implements MeshPrimitiveModel {
   private final Map<String, AccessorModel> attributes;
   private AccessorModel indices;
   private MaterialModel materialModel;
   private final int mode;
   private final List<Map<String, AccessorModel>> targets;

   public DefaultMeshPrimitiveModel(int mode) {
      this.mode = mode;
      this.attributes = new LinkedHashMap<>();
      this.targets = new ArrayList<>();
   }

   public AccessorModel putAttribute(String name, AccessorModel accessorModel) {
      Objects.requireNonNull(accessorModel, "The accessorModel may not be null");
      return this.attributes.put(name, accessorModel);
   }

   public void setIndices(AccessorModel indices) {
      this.indices = indices;
   }

   public void setMaterialModel(MaterialModel materialModel) {
      this.materialModel = materialModel;
   }

   public void addTarget(Map<String, AccessorModel> target) {
      Objects.requireNonNull(target, "The target may not be null");
      this.targets.add(target);
   }

   public AccessorModel putTarget(int index, String name, AccessorModel accessorModel) {
      Objects.requireNonNull(accessorModel, "The accessorModel may not be null");
      Map<String, AccessorModel> target = this.targets.get(index);
      return target.put(name, accessorModel);
   }

   @Override
   public Map<String, AccessorModel> getAttributes() {
      return Collections.unmodifiableMap(this.attributes);
   }

   @Override
   public AccessorModel getIndices() {
      return this.indices;
   }

   @Override
   public int getMode() {
      return this.mode;
   }

   @Override
   public MaterialModel getMaterialModel() {
      return this.materialModel;
   }

   @Override
   public List<Map<String, AccessorModel>> getTargets() {
      return Collections.unmodifiableList(this.targets);
   }
}
