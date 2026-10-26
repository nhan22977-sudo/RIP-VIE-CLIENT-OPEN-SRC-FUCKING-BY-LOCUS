package de.javagl.jgltf.model.v1.gl;

import de.javagl.jgltf.impl.v1.Program;

class Programs {
   static Program createDefaultProgram(String vertexShaderId, String fragmentShaderId) {
      Program program = new Program();
      program.setVertexShader(vertexShaderId);
      program.setFragmentShader(fragmentShaderId);
      program.addAttributes("a_position");
      return program;
   }

   private Programs() {
   }
}
