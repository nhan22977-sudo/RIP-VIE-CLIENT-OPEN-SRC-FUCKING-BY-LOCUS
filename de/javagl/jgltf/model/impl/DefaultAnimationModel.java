package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.AccessorModel;
import de.javagl.jgltf.model.AnimationModel;
import de.javagl.jgltf.model.NodeModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class DefaultAnimationModel extends AbstractNamedModelElement implements AnimationModel {
   private final List<AnimationModel.Channel> channels = new ArrayList<>();

   public void addChannel(AnimationModel.Channel channel) {
      Objects.requireNonNull(channel, "The channel may not be null");
      this.channels.add(channel);
   }

   @Override
   public List<AnimationModel.Channel> getChannels() {
      return Collections.unmodifiableList(this.channels);
   }

   public static class DefaultChannel implements AnimationModel.Channel {
      private final AnimationModel.Sampler sampler;
      private final NodeModel nodeModel;
      private final String path;

      public DefaultChannel(AnimationModel.Sampler sampler, NodeModel nodeModel, String path) {
         this.sampler = Objects.requireNonNull(sampler, "The sampler may not be null");
         this.nodeModel = nodeModel;
         this.path = path;
      }

      @Override
      public AnimationModel.Sampler getSampler() {
         return this.sampler;
      }

      @Override
      public NodeModel getNodeModel() {
         return this.nodeModel;
      }

      @Override
      public String getPath() {
         return this.path;
      }
   }

   public static class DefaultSampler implements AnimationModel.Sampler {
      private final AccessorModel input;
      private final AnimationModel.Interpolation interpolation;
      private final AccessorModel output;

      public DefaultSampler(AccessorModel input, AnimationModel.Interpolation interpolation, AccessorModel output) {
         this.input = Objects.requireNonNull(input, "The input may not be null");
         this.interpolation = Objects.requireNonNull(interpolation, "The interpolation may not be null");
         this.output = Objects.requireNonNull(output, "The output may not be null");
      }

      @Override
      public AccessorModel getInput() {
         return this.input;
      }

      @Override
      public AnimationModel.Interpolation getInterpolation() {
         return this.interpolation;
      }

      @Override
      public AccessorModel getOutput() {
         return this.output;
      }
   }
}
