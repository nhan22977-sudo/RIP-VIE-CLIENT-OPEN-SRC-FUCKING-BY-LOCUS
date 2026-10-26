package com.zigythebird.playeranim.util;

import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranimcore.api.firstPerson.FirstPersonMode;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_746;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class ClientUtil {
   public static class_746 getClientPlayer() {
      return class_310.method_1551().field_1724;
   }

   public static class_1937 getLevel() {
      return class_310.method_1551().field_1687;
   }

   @Internal
   public static boolean shouldBeFirstPersonPass() {
      return shouldBeFirstPersonPass(class_310.method_1551().field_1773.method_19418());
   }

   @Internal
   public static boolean shouldBeFirstPersonPass(class_4184 camera) {
      return !camera.method_19333()
         && camera.method_19331() instanceof IAnimatedAvatar player
         && player.playerAnimLib$getAnimManager().isActive()
         && player.playerAnimLib$getAnimManager().getFirstPersonMode() == FirstPersonMode.THIRD_PERSON_MODEL
         && player.playerAnimLib$getAnimManager().getFirstPersonTransitionProgress() == 1.0F
         && (!(camera.method_19331() instanceof class_1309) || !((class_1309)camera.method_19331()).method_6113());
   }
}
