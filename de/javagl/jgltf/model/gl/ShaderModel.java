package de.javagl.jgltf.model.gl;

import de.javagl.jgltf.model.BufferViewModel;
import de.javagl.jgltf.model.NamedModelElement;
import java.nio.ByteBuffer;

public interface ShaderModel extends NamedModelElement {
   String getUri();

   BufferViewModel getBufferViewModel();

   ByteBuffer getShaderData();

   String getShaderSource();

   ShaderModel.ShaderType getShaderType();

   enum ShaderType {
      VERTEX_SHADER,
      FRAGMENT_SHADER;
   }
}
