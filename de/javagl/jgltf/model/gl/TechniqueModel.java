package de.javagl.jgltf.model.gl;

import de.javagl.jgltf.model.NamedModelElement;
import java.util.Map;

public interface TechniqueModel extends NamedModelElement {
   ProgramModel getProgramModel();

   Map<String, TechniqueParametersModel> getParameters();

   Map<String, String> getAttributes();

   TechniqueParametersModel getAttributeParameters(String var1);

   Map<String, String> getUniforms();

   TechniqueParametersModel getUniformParameters(String var1);

   TechniqueStatesModel getTechniqueStatesModel();
}
