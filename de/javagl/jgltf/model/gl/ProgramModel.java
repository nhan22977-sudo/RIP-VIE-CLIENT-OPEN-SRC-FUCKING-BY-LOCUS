package de.javagl.jgltf.model.gl;

import de.javagl.jgltf.model.NamedModelElement;
import java.util.List;

public interface ProgramModel extends NamedModelElement {
   ShaderModel getVertexShaderModel();

   ShaderModel getFragmentShaderModel();

   List<String> getAttributes();
}
