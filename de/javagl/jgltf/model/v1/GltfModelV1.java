package de.javagl.jgltf.model.v1;

import de.javagl.jgltf.model.GltfModel;
import de.javagl.jgltf.model.gl.ProgramModel;
import de.javagl.jgltf.model.gl.ShaderModel;
import de.javagl.jgltf.model.gl.TechniqueModel;
import de.javagl.jgltf.model.gl.impl.DefaultProgramModel;
import de.javagl.jgltf.model.gl.impl.DefaultShaderModel;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueModel;
import de.javagl.jgltf.model.impl.DefaultGltfModel;
import de.javagl.jgltf.model.io.v1.GltfAssetV1;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class GltfModelV1 extends DefaultGltfModel implements GltfModel {
   private final List<DefaultShaderModel> shaderModels;
   private final List<DefaultProgramModel> programModels;
   private final List<DefaultTechniqueModel> techniqueModels;

   public GltfModelV1(GltfAssetV1 gltfAsset) {
      Objects.requireNonNull(gltfAsset, "The gltfAsset may not be null");
      this.shaderModels = new ArrayList<>();
      this.programModels = new ArrayList<>();
      this.techniqueModels = new ArrayList<>();
      GltfModelCreatorV1 gltfModelCreatorV1 = new GltfModelCreatorV1(gltfAsset, this);
      gltfModelCreatorV1.create();
   }

   public GltfModelV1() {
      this.shaderModels = new ArrayList<>();
      this.programModels = new ArrayList<>();
      this.techniqueModels = new ArrayList<>();
   }

   public void addShaderModel(DefaultShaderModel shaderModel) {
      this.shaderModels.add(shaderModel);
   }

   public void removeShaderModel(DefaultShaderModel shaderModel) {
      this.shaderModels.remove(shaderModel);
   }

   public void addShaderModels(Collection<? extends DefaultShaderModel> shaderModels) {
      for (DefaultShaderModel shaderModel : shaderModels) {
         this.addShaderModel(shaderModel);
      }
   }

   public DefaultShaderModel getShaderModel(int index) {
      return this.shaderModels.get(index);
   }

   public void clearShaderModels() {
      this.shaderModels.clear();
   }

   public List<ShaderModel> getShaderModels() {
      return Collections.unmodifiableList(this.shaderModels);
   }

   public void addProgramModel(DefaultProgramModel programModel) {
      this.programModels.add(programModel);
   }

   public void removeProgramModel(DefaultProgramModel programModel) {
      this.programModels.remove(programModel);
   }

   public void addProgramModels(Collection<? extends DefaultProgramModel> programModels) {
      for (DefaultProgramModel programModel : programModels) {
         this.addProgramModel(programModel);
      }
   }

   public DefaultProgramModel getProgramModel(int index) {
      return this.programModels.get(index);
   }

   public void clearProgramModels() {
      this.programModels.clear();
   }

   public List<ProgramModel> getProgramModels() {
      return Collections.unmodifiableList(this.programModels);
   }

   public void addTechniqueModel(DefaultTechniqueModel techniqueModel) {
      this.techniqueModels.add(techniqueModel);
   }

   public void removeTechniqueModel(DefaultTechniqueModel techniqueModel) {
      this.techniqueModels.remove(techniqueModel);
   }

   public void addTechniqueModels(Collection<? extends DefaultTechniqueModel> techniqueModels) {
      for (DefaultTechniqueModel techniqueModel : techniqueModels) {
         this.addTechniqueModel(techniqueModel);
      }
   }

   public DefaultTechniqueModel getTechniqueModel(int index) {
      return this.techniqueModels.get(index);
   }

   public void clearTechniqueModels() {
      this.techniqueModels.clear();
   }

   public List<TechniqueModel> getTechniqueModels() {
      return Collections.unmodifiableList(this.techniqueModels);
   }
}
