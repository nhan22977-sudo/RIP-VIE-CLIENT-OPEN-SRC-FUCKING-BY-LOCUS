package de.javagl.jgltf.model.v1.gl;

import de.javagl.jgltf.impl.v1.Material;
import de.javagl.jgltf.impl.v1.Program;
import de.javagl.jgltf.impl.v1.Shader;
import de.javagl.jgltf.impl.v1.Technique;

public class GltfDefaults {
   private static final String DEFAULT_VERTEX_SHADER_ID = GltfDefaults.class.getName() + ".DEFAULT_VERTEX_SHADER_ID";
   private static final String DEFAULT_FRAGMENT_SHADER_ID = GltfDefaults.class.getName() + ".DEFAULT_FRAGMENT_SHADER_ID";
   private static final String DEFAULT_PROGRAM_ID = GltfDefaults.class.getName() + ".DEFAULT_PROGRAM_ID";
   private static final String DEFAULT_TECHNIQUE_ID = GltfDefaults.class.getName() + ".DEFAULT_TECHNIQUE_ID";
   private static final String DEFAULT_MATERIAL_ID = GltfDefaults.class.getName() + ".DEFAULT_MATERIAL_ID";
   private static final Shader DEFAULT_VERTEX_SHADER = Shaders.createDefaultVertexShader();
   private static final Shader DEFAULT_FRAGMENT_SHADER = Shaders.createDefaultFragmentShader();
   private static final Program DEFAULT_PROGRAM = Programs.createDefaultProgram(DEFAULT_VERTEX_SHADER_ID, DEFAULT_FRAGMENT_SHADER_ID);
   private static final Technique DEFAULT_TECHNIQUE = Techniques.createDefaultTechnique(DEFAULT_PROGRAM_ID);
   private static final Material DEFAULT_MATERIAL = Materials.createDefaultMaterial(DEFAULT_TECHNIQUE_ID);

   public static boolean isDefaultTechniqueId(String id) {
      return DEFAULT_TECHNIQUE_ID.equals(id);
   }

   public static boolean isDefaultMaterialId(String id) {
      return DEFAULT_MATERIAL_ID.equals(id);
   }

   static Shader getDefaultVertexShader() {
      return DEFAULT_VERTEX_SHADER;
   }

   static Shader getDefaultFragmentShader() {
      return DEFAULT_FRAGMENT_SHADER;
   }

   static Technique getDefaultTechnique() {
      return DEFAULT_TECHNIQUE;
   }

   static Material getDefaultMaterial() {
      return DEFAULT_MATERIAL;
   }

   private GltfDefaults() {
   }
}
