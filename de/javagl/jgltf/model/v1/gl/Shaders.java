package de.javagl.jgltf.model.v1.gl;

import de.javagl.jgltf.impl.v1.Shader;
import java.util.Base64;

class Shaders {
   private static final String DEFAULT_VERTEX_SHADER_CODE = "#ifdef GL_ES\n  precision highp float;\n#endif\n\nuniform mat4 u_modelViewMatrix;\nuniform mat4 u_projectionMatrix;\nattribute vec3 a_position;\nvoid main(void)\n{\n    gl_Position = u_projectionMatrix * u_modelViewMatrix *\n        vec4(a_position,1.0);\n}\n\n";
   private static final String DEFAULT_FRAGMENT_SHADER_CODE = "#ifdef GL_ES\n  precision highp float;\n#endif\n\nuniform vec4 u_emission;\nvoid main(void)\n{\n    gl_FragColor = u_emission;\n}\n\n";

   static Shader createDefaultVertexShader() {
      Shader shader = new Shader();
      shader.setType(35633);
      String encodedCode = Base64.getEncoder()
         .encodeToString(
            "#ifdef GL_ES\n  precision highp float;\n#endif\n\nuniform mat4 u_modelViewMatrix;\nuniform mat4 u_projectionMatrix;\nattribute vec3 a_position;\nvoid main(void)\n{\n    gl_Position = u_projectionMatrix * u_modelViewMatrix *\n        vec4(a_position,1.0);\n}\n\n"
               .getBytes()
         );
      String dataUriString = "data:text/plain;base64," + encodedCode;
      shader.setUri(dataUriString);
      return shader;
   }

   static Shader createDefaultFragmentShader() {
      Shader shader = new Shader();
      shader.setType(35632);
      String encodedCode = Base64.getEncoder()
         .encodeToString(
            "#ifdef GL_ES\n  precision highp float;\n#endif\n\nuniform vec4 u_emission;\nvoid main(void)\n{\n    gl_FragColor = u_emission;\n}\n\n".getBytes()
         );
      String dataUriString = "data:text/plain;base64," + encodedCode;
      shader.setUri(dataUriString);
      return shader;
   }

   private Shaders() {
   }
}
