package io.github.kosmx.emotes.arch.mixin;

import com.zigythebird.playeranim.accessors.IAnimatedAvatar;
import com.zigythebird.playeranim.util.ClientUtil;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.RawAnimation;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.api.events.client.ClientEmoteEvents;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.emotePlay.EmotePlayer;
import io.github.kosmx.emotes.main.mixinFunctions.IPlayerEntity;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.class_11890;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_742;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_11890.class)
public abstract class EmoteAvatarMixin extends class_1309 implements IPlayerEntity {
   @Unique
   private int emotecraft$age = 0;
   @Unique
   private final EmotePlayer emotecraft$container = new EmotePlayer((class_11890)this);
   @Unique
   private boolean emotecraft$isForced = false;

   protected EmoteAvatarMixin(class_1299<? extends class_1309> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Inject(method = "<init>(Lnet/minecraft/class_1299;Lnet/minecraft/class_1937;)V", at = @At("TAIL"))
   private void init(class_1299<? extends class_1309> entityType, class_1937 level, CallbackInfo ci) {
      ((IAnimatedAvatar)this).playerAnimLib$getAnimManager().addAnimLayer(1000, this.emotecraft$container);
   }

   @Override
   public void emotecraft$playEmote(@Nullable Animation emote, Animation.LoopType loopType, float tick, boolean isForced) {
      this.stopEmote();
      if (emote != null) {
         this.emotecraft$container.triggerAnimation(RawAnimation.begin().then(emote, loopType), tick);
         this.initEmotePerspective();
         if (this.isMainAvatar()) {
            this.emotecraft$isForced = isForced;
         }
      }
   }

   @NotNull
   @Override
   public EmotePlayer emotecraft$getEmote() {
      return this.emotecraft$container;
   }

   @Inject(method = {"tick", "method_5773"}, at = @At("TAIL"), remap = false)
   public void tick(CallbackInfo ci) {
      if (this.emotecraft$age <= 1 && this.emotecraft$age++ == 1) {
         Pair<Animation, Float> p = ClientEmotePlay.getEmoteForUUID(this.method_5667());
         if (p != null) {
            ClientEmoteEvents.EMOTE_PLAY.invoker().onEmotePlay((Animation)p.left(), (Float)p.right(), this.method_5667());
            this.emotecraft$playEmote((Animation)p.left(), (Float)p.right(), false);
         }

         if (!this.isMainAvatar() && ClientUtil.getClientPlayer() != null && ClientUtil.getClientPlayer().isPlayingEmote()) {
            IPlayerEntity playerEntity = ClientUtil.getClientPlayer();
            ClientEmotePlay.clientRepeatLocalEmote(
               playerEntity.emotecraft$getEmote().getCurrentAnimationInstance(), playerEntity.emotecraft$getEmote().getAnimationTicks(), this.method_5667()
            );
         }
      }

      if (this.isPlayingEmote() && this.isMainAvatar()) {
         if (this.emotecraft$getEmote().perspective && PlatformTools.getCameraType() != PlatformTools.getConfig().cameraType.get()) {
            this.emotecraft$getEmote().perspective = false;
         }

         if (this instanceof class_742 player && !EmoteHolder.canRunEmote(player)) {
            ClientEmotePlay.clientStopLocalEmote(player.emotecraft$getEmote().getCurrentAnimationInstance());
         }
      }
   }

   @Override
   public boolean emotecraft$isForcedEmote() {
      return this.isPlayingEmote() && this.emotecraft$isForced;
   }
}
