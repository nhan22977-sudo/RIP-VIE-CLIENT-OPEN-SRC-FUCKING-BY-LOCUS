package de.javagl.jgltf.model.v1;

import de.javagl.jgltf.impl.v1.GlTF;

class IndexMappingSets {
   static IndexMappingSet create(GlTF gltf) {
      IndexMappingSet indexMappingSet = new IndexMappingSet();
      indexMappingSet.generate("accessors", gltf.getAccessors());
      indexMappingSet.generate("animations", gltf.getAnimations());
      indexMappingSet.generate("buffers", gltf.getBuffers());
      indexMappingSet.generate("bufferViews", gltf.getBufferViews());
      indexMappingSet.generate("cameras", gltf.getCameras());
      indexMappingSet.generate("images", gltf.getImages());
      indexMappingSet.generate("materials", gltf.getMaterials());
      indexMappingSet.generate("meshes", gltf.getMeshes());
      indexMappingSet.generate("nodes", gltf.getNodes());
      indexMappingSet.generate("programs", gltf.getPrograms());
      indexMappingSet.generate("samplers", gltf.getSamplers());
      indexMappingSet.generate("scenes", gltf.getScenes());
      indexMappingSet.generate("shaders", gltf.getShaders());
      indexMappingSet.generate("skins", gltf.getSkins());
      indexMappingSet.generate("techniques", gltf.getTechniques());
      indexMappingSet.generate("textures", gltf.getTextures());
      return indexMappingSet;
   }

   private IndexMappingSets() {
   }
}
