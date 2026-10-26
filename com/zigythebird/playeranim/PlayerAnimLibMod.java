package com.zigythebird.playeranim;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.animation.keyframe.event.builtin.AutoPlayingSoundKeyframeHandler;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranim.molang.MolangQueries;
import com.zigythebird.playeranimcore.PlayerAnimLib;
import com.zigythebird.playeranimcore.animation.keyframe.event.CustomKeyFrameEvents;
import com.zigythebird.playeranimcore.enums.PlayState;
import com.zigythebird.playeranimcore.event.MolangEvent;
import net.minecraft.class_2960;

public abstract class PlayerAnimLibMod extends PlayerAnimLib {
   public static final class_2960 ANIMATION_LAYER_ID = id("factory");

   public static class_2960 id(String name) {
      return class_2960.method_60655("player_animation_library", name);
   }

   protected void init() {
      PlayerAnimationFactory.ANIMATION_DATA_FACTORY
         .registerFactory(ANIMATION_LAYER_ID, 1000, player -> new PlayerAnimationController(player, (controller, state, animSetter) -> PlayState.STOP));
      MolangEvent.MOLANG_EVENT.register((controller, engine, queryBinding) -> MolangQueries.setDefaultQueryValues(queryBinding));
      CustomKeyFrameEvents.SOUND_KEYFRAME_EVENT.register(new AutoPlayingSoundKeyframeHandler());
   }
}
