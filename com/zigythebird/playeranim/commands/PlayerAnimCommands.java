package com.zigythebird.playeranim.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.zigythebird.playeranim.PlayerAnimLibMod;
import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.RawAnimation;
import com.zigythebird.playeranimcore.network.AnimationBinary;
import com.zigythebird.playeranimcore.network.LegacyAnimationBinary;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Objects;
import net.minecraft.class_11890;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2232;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5242;
import net.minecraft.class_7157;

public class PlayerAnimCommands {
   public static <T> void register(CommandDispatcher<T> dispatcher, class_7157 registryAccess) {
      dispatcher.register(
         (LiteralArgumentBuilder)class_2170.method_9247("testPlayerAnimation")
            .then(
               class_2170.method_9244("animationID", class_2232.method_9441()).suggests(new AnimationArgumentProvider()).executes(PlayerAnimCommands::execute)
            )
      );
      dispatcher.register(
         (LiteralArgumentBuilder)class_2170.method_9247("testLegacyAnimationBinary")
            .then(
               class_2170.method_9244("animationID", class_2232.method_9441())
                  .suggests(new AnimationArgumentProvider())
                  .then(
                     class_2170.method_9244("version", IntegerArgumentType.integer(1, LegacyAnimationBinary.getCurrentVersion()))
                        .executes(PlayerAnimCommands::executeLegacy)
                  )
            )
      );
      dispatcher.register(
         (LiteralArgumentBuilder)class_2170.method_9247("testAnimationBinary")
            .then(
               class_2170.method_9244("animationID", class_2232.method_9441())
                  .suggests(new AnimationArgumentProvider())
                  .then(class_2170.method_9244("version", IntegerArgumentType.integer(1, 5)).executes(PlayerAnimCommands::executeBinary))
            )
      );
      dispatcher.register(
         (LiteralArgumentBuilder)class_2170.method_9247("testMannequin")
            .then(
               class_2170.method_9244("animationID", class_2232.method_9441())
                  .suggests(new AnimationArgumentProvider())
                  .then(class_2170.method_9244("mannequin", class_5242.method_27643()).executes(PlayerAnimCommands::executeMannequin))
            )
      );
   }

   private static int execute(CommandContext<class_2168> context) {
      class_2960 animation = class_2232.method_9443(context, "animationID");
      return playAnimation(PlayerAnimResources.getAnimation(animation));
   }

   private static int executeLegacy(CommandContext<class_2168> context) {
      Animation animation = Objects.requireNonNull(PlayerAnimResources.getAnimation(class_2232.method_9443(context, "animationID")));
      int version = IntegerArgumentType.getInteger(context, "version");
      ByteBuf byteBuffer = Unpooled.buffer(LegacyAnimationBinary.calculateSize(animation, version));
      LegacyAnimationBinary.write(animation, byteBuffer, version);

      try {
         return playAnimation(LegacyAnimationBinary.read(byteBuffer, version));
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         byteBuffer.release();
      }
   }

   private static int executeBinary(CommandContext<class_2168> context) {
      Animation animation = Objects.requireNonNull(PlayerAnimResources.getAnimation(class_2232.method_9443(context, "animationID")));
      int version = IntegerArgumentType.getInteger(context, "version");
      ByteBuf byteBuf = Unpooled.buffer();
      AnimationBinary.write(byteBuf, version, animation);
      return playAnimation(AnimationBinary.read(byteBuf, version));
   }

   private static int playAnimation(Animation animation) {
      AnimationController controller = (AnimationController)PlayerAnimationAccess.getPlayerAnimationLayer(
         Objects.requireNonNull(class_310.method_1551().field_1724), PlayerAnimLibMod.ANIMATION_LAYER_ID
      );
      if (controller == null) {
         return 0;
      }

      controller.triggerAnimation(RawAnimation.begin().thenPlay(animation));
      return 1;
   }

   private static int executeMannequin(CommandContext<class_2168> context) {
      Animation animation = Objects.requireNonNull(PlayerAnimResources.getAnimation(class_2232.method_9443(context, "animationID")));
      class_11890 avatar = Objects.requireNonNull(class_310.method_1551().field_1687.method_66347(class_5242.method_27645(context, "mannequin")));
      AnimationController controller = (AnimationController)PlayerAnimationAccess.getPlayerAnimationLayer(avatar, PlayerAnimLibMod.ANIMATION_LAYER_ID);
      if (controller == null) {
         return 0;
      }

      controller.triggerAnimation(RawAnimation.begin().thenPlay(animation));
      return 1;
   }
}
