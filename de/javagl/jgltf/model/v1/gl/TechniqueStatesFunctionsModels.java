package de.javagl.jgltf.model.v1.gl;

import de.javagl.jgltf.impl.v1.TechniqueStatesFunctions;
import de.javagl.jgltf.model.Optionals;
import de.javagl.jgltf.model.gl.impl.DefaultTechniqueStatesFunctionsModel;

public class TechniqueStatesFunctionsModels {
   public static DefaultTechniqueStatesFunctionsModel create(TechniqueStatesFunctions techniqueStatesFunctions) {
      DefaultTechniqueStatesFunctionsModel techniqueStatesFunctionsModel = new DefaultTechniqueStatesFunctionsModel();
      techniqueStatesFunctionsModel.setBlendColor(Optionals.clone(techniqueStatesFunctions.getBlendColor()));
      techniqueStatesFunctionsModel.setBlendEquationSeparate(Optionals.clone(techniqueStatesFunctions.getBlendEquationSeparate()));
      techniqueStatesFunctionsModel.setBlendFuncSeparate(Optionals.clone(techniqueStatesFunctions.getBlendFuncSeparate()));
      techniqueStatesFunctionsModel.setColorMask(Optionals.clone(techniqueStatesFunctions.getColorMask()));
      techniqueStatesFunctionsModel.setCullFace(Optionals.clone(techniqueStatesFunctions.getCullFace()));
      techniqueStatesFunctionsModel.setDepthFunc(Optionals.clone(techniqueStatesFunctions.getDepthFunc()));
      techniqueStatesFunctionsModel.setDepthMask(Optionals.clone(techniqueStatesFunctions.getDepthMask()));
      techniqueStatesFunctionsModel.setDepthRange(Optionals.clone(techniqueStatesFunctions.getDepthRange()));
      techniqueStatesFunctionsModel.setFrontFace(Optionals.clone(techniqueStatesFunctions.getFrontFace()));
      techniqueStatesFunctionsModel.setLineWidth(Optionals.clone(techniqueStatesFunctions.getLineWidth()));
      techniqueStatesFunctionsModel.setPolygonOffset(Optionals.clone(techniqueStatesFunctions.getPolygonOffset()));
      return techniqueStatesFunctionsModel;
   }

   private TechniqueStatesFunctionsModels() {
   }
}
