package de.javagl.jgltf.model.animation;

class Interpolators {
   static Interpolator create(InterpolatorType interpolatorType) {
      if (interpolatorType == null) {
         return new LinearInterpolator();
      }

      switch (interpolatorType) {
         case SLERP:
            return new SlerpQuaternionInterpolator();
         case LINEAR:
            return new LinearInterpolator();
         case STEP:
            return new StepInterpolator();
         default:
            throw new IllegalArgumentException("Invalid interpolator type: " + interpolatorType);
      }
   }

   private Interpolators() {
   }
}
