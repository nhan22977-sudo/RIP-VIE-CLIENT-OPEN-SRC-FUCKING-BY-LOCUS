package de.javagl.jgltf.model;

import de.javagl.jgltf.model.animation.Animation;
import de.javagl.jgltf.model.animation.AnimationListener;
import de.javagl.jgltf.model.animation.AnimationManager;
import de.javagl.jgltf.model.animation.InterpolatorType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class GltfAnimations {
   private static final Logger logger = Logger.getLogger(GltfAnimations.class.getName());

   public static AnimationManager createAnimationManager(AnimationManager.AnimationPolicy animationPolicy) {
      return new AnimationManager(animationPolicy);
   }

   public static List<Animation> createModelAnimations(Iterable<? extends AnimationModel> animationModels) {
      Objects.requireNonNull(animationModels, "The animationModels may not be null");
      List<Animation> allModelAnimations = new ArrayList<>();

      for (AnimationModel animationModel : animationModels) {
         List<AnimationModel.Channel> channels = animationModel.getChannels();
         List<Animation> modelAnimations = createModelAnimationsForChannels(channels);
         allModelAnimations.addAll(modelAnimations);
      }

      return allModelAnimations;
   }

   private static List<Animation> createModelAnimationsForChannels(Iterable<? extends AnimationModel.Channel> channels) {
      List<Animation> modelAnimations = new ArrayList<>();

      for (AnimationModel.Channel channel : channels) {
         Animation modelAnimation = createModelAnimation(channel);
         if (modelAnimation != null) {
            modelAnimations.add(modelAnimation);
         }
      }

      return modelAnimations;
   }

   private static Animation createModelAnimation(AnimationModel.Channel channel) {
      AnimationModel.Sampler sampler = channel.getSampler();
      AnimationModel.Interpolation interpolation = sampler.getInterpolation();
      NodeModel nodeModel = channel.getNodeModel();
      String path = channel.getPath();
      AnimationListener animationListener = createAnimationListener(nodeModel, path);
      if (animationListener == null) {
         return null;
      } else {
         InterpolatorType interpolatorType = typeForInterpolation(interpolation, path);
         AccessorModel input = sampler.getInput();
         AccessorData inputData = input.getAccessorData();
         if (!(inputData instanceof AccessorFloatData)) {
            logger.warning("Input data is not an AccessorFloatData, but " + inputData.getClass());
            return null;
         } else {
            AccessorFloatData inputFloatData = (AccessorFloatData)inputData;
            AccessorModel output = sampler.getOutput();
            AccessorData outputData = output.getAccessorData();
            if (!(outputData instanceof AccessorFloatData)) {
               logger.warning("Output data is not an AccessorFloatData, but " + outputData.getClass());
               return null;
            } else {
               AccessorFloatData outputFloatData = (AccessorFloatData)outputData;
               Animation modelAnimation = createAnimation(inputFloatData, outputFloatData, interpolatorType);
               modelAnimation.addAnimationListener(animationListener);
               return modelAnimation;
            }
         }
      }
   }

   private static InterpolatorType typeForInterpolation(AnimationModel.Interpolation interpolation, String path) {
      switch (interpolation) {
         case LINEAR:
            if (path.equals("rotation")) {
               return InterpolatorType.SLERP;
            }

            return InterpolatorType.LINEAR;
         case STEP:
            return InterpolatorType.STEP;
         case CUBICSPLINE:
         default:
            logger.warning("This interpolation type is not supported yet");
            logger.warning("Interpolation type not supported: " + interpolation);
            return InterpolatorType.LINEAR;
      }
   }

   static Animation createAnimation(AccessorFloatData timeData, AccessorFloatData outputData, InterpolatorType interpolatorType) {
      int numKeyElements = timeData.getNumElements();
      float[] keys = new float[numKeyElements];

      for (int e = 0; e < numKeyElements; e++) {
         keys[e] = timeData.get(e);
      }

      int totalNumValueComponents = outputData.getTotalNumComponents();
      int numComponentsPerElement = totalNumValueComponents / numKeyElements;
      float[][] values = new float[numKeyElements][numComponentsPerElement];

      for (int c = 0; c < numComponentsPerElement; c++) {
         for (int e = 0; e < numKeyElements; e++) {
            int globalIndex = e * numComponentsPerElement + c;
            values[e][c] = outputData.get(globalIndex);
         }
      }

      return new Animation(keys, values, interpolatorType);
   }

   private static AnimationListener createAnimationListener(NodeModel nodeModel, String path) {
      switch (path) {
         case "translation":
            return createTranslationAnimationListener(nodeModel);
         case "rotation":
            return createRotationAnimationListener(nodeModel);
         case "scale":
            return createScaleAnimationListener(nodeModel);
         case "weights":
            return createWeightsAnimationListener(nodeModel);
         default:
            logger.warning("Animation channel target path must be \"translation\", \"rotation\", \"scale\" or  \"weights\", but is " + path);
            return null;
      }
   }

   private static AnimationListener createTranslationAnimationListener(NodeModel nodeModel) {
      return (animation, timeS, values) -> {
         float[] translation = nodeModel.getTranslation();
         if (translation == null) {
            translation = (float[])values.clone();
            nodeModel.setTranslation(translation);
         } else {
            System.arraycopy(values, 0, translation, 0, values.length);
         }
      };
   }

   private static AnimationListener createRotationAnimationListener(NodeModel nodeModel) {
      return (animation, timeS, values) -> {
         float[] rotation = nodeModel.getRotation();
         if (rotation == null) {
            rotation = (float[])values.clone();
            nodeModel.setRotation(rotation);
         } else {
            System.arraycopy(values, 0, rotation, 0, values.length);
         }
      };
   }

   private static AnimationListener createScaleAnimationListener(NodeModel nodeModel) {
      return (animation, timeS, values) -> {
         float[] scale = nodeModel.getScale();
         if (scale == null) {
            scale = (float[])values.clone();
            nodeModel.setScale(scale);
         } else {
            System.arraycopy(values, 0, scale, 0, values.length);
         }
      };
   }

   private static AnimationListener createWeightsAnimationListener(NodeModel nodeModel) {
      return (animation, timeS, values) -> {
         float[] weights = nodeModel.getWeights();
         if (weights == null) {
            weights = (float[])values.clone();
            nodeModel.setWeights(weights);
         } else {
            System.arraycopy(values, 0, weights, 0, values.length);
         }
      };
   }

   private GltfAnimations() {
   }
}
