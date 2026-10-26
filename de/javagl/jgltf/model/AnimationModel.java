package de.javagl.jgltf.model;

import java.util.List;

public interface AnimationModel extends NamedModelElement {
   List<AnimationModel.Channel> getChannels();

   interface Channel {
      AnimationModel.Sampler getSampler();

      NodeModel getNodeModel();

      String getPath();
   }

   enum Interpolation {
      STEP,
      LINEAR,
      CUBICSPLINE;
   }

   interface Sampler {
      AccessorModel getInput();

      AnimationModel.Interpolation getInterpolation();

      AccessorModel getOutput();
   }
}
