package com.zigythebird.playeranim.mixin;

import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranim.api.PlayerAnimationAccess;
import com.zigythebird.playeranim.api.PlayerAnimationFactory;
import com.zigythebird.playeranim.util.ClientUtil;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_11890;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_11890.class)
public abstract class AvatarMixin extends class_1309 implements IAnimatedAvatar {
   @Unique
   private final Map<class_2960, IAnimation> playerAnimLib$modAnimationData = new HashMap<>();
   @Unique
   private final AvatarAnimManager playerAnimLib$animationManager = this.playerAnimLib$createAnimationStack();

   protected AvatarMixin(class_1299<? extends class_1309> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Unique
   private AvatarAnimManager playerAnimLib$createAnimationStack() {
      AvatarAnimManager manager = new AvatarAnimManager((class_11890)this);
      PlayerAnimationFactory.ANIMATION_DATA_FACTORY.prepareAnimations((class_11890)this, manager, this.playerAnimLib$modAnimationData);
      PlayerAnimationAccess.REGISTER_ANIMATION_EVENT.invoker().registerAnimation((class_11890)this, manager);
      return manager;
   }

   @Override
   public AvatarAnimManager playerAnimLib$getAnimManager() {
      return this.playerAnimLib$animationManager;
   }

   @Override
   public IAnimation playerAnimLib$getAnimation(class_2960 id) {
      return this.playerAnimLib$modAnimationData.containsKey(id) ? this.playerAnimLib$modAnimationData.get(id) : null;
   }

   @Intrinsic
   public void method_5773() {
      super.method_5773();
   }

   @Inject(method = {"tick", "method_5773"}, at = @At("TAIL"), remap = false)
   private void tick(CallbackInfo ci) {
      if (this.method_73183().method_8608()) {
         this.playerAnimLib$animationManager.handleAnimations(0.0F, true, ClientUtil.shouldBeFirstPersonPass());
      }
   }
}
