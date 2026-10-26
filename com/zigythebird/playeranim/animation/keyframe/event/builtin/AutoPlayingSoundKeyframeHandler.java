package com.zigythebird.playeranim.animation.keyframe.event.builtin;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranim.util.ClientUtil;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.animation.AnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.event.CustomKeyFrameEvents;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.event.EventResult;
import java.util.Optional;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_6880.class_6883;

public class AutoPlayingSoundKeyframeHandler implements CustomKeyFrameEvents.CustomKeyFrameHandler<SoundKeyframeData> {
   public EventResult handle(float animationTick, AnimationController controller, SoundKeyframeData keyFrameData, AnimationData animationData) {
      class_243 position = controller instanceof PlayerAnimationController player ? player.getAvatar().method_73189() : null;
      if (position == null) {
         return EventResult.PASS;
      }

      String[] segments = keyFrameData.getSound().split("\\|");
      class_2960 rl = class_2960.method_12829(segments[0]);
      if (rl == null) {
         return EventResult.PASS;
      }

      Optional<class_6883<class_3414>> soundEvent = class_7923.field_41172.method_10223(rl);
      if (soundEvent.isEmpty()) {
         return EventResult.PASS;
      }

      float volume = segments.length > 1 ? Float.parseFloat(segments[1]) : 1.0F;
      float pitch = segments.length > 2 ? Float.parseFloat(segments[2]) : 1.0F;
      ClientUtil.getLevel()
         .method_60511(null, position.field_1352, position.field_1351, position.field_1350, (class_6880)soundEvent.get(), class_3419.field_15248, volume, pitch);
      return EventResult.SUCCESS;
   }
}
