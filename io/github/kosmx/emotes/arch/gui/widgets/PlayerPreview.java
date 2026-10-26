package io.github.kosmx.emotes.arch.gui.widgets;

import com.mojang.authlib.GameProfile;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.easing.EasingType;
import io.github.kosmx.emotes.arch.screen.utils.UnsafeMannequin;
import io.github.kosmx.emotes.arch.screen.utils.WidgetOutliner;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.main.emotePlay.EmotePlayer;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.class_1144;
import net.minecraft.class_11875;
import net.minecraft.class_11876;
import net.minecraft.class_11903;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3532;
import net.minecraft.class_490;
import net.minecraft.class_5244;
import net.minecraft.class_6382;
import net.minecraft.class_8021;
import net.minecraft.class_9848;
import org.jetbrains.annotations.Nullable;

public class PlayerPreview extends class_339 implements class_8021 {
   private static final Float2FloatFunction EASING_TRANSFORMER = EasingType.EASE_OUT_QUART.buildTransformer(null);
   protected final boolean renderBackround;
   protected class_11903 mannequin;
   protected float animTime = 1.0F;

   public PlayerPreview(GameProfile profile, int x, int y, int width, int height, boolean renderBackround) {
      super(x, y, width, height, class_5244.field_39003);
      this.mannequin = new UnsafeMannequin(null, profile);
      this.renderBackround = renderBackround;
      this.method_25350(0.0F);
   }

   public boolean playAnimation(@Nullable Animation animation, Animation.LoopType loopType, boolean check) {
      return this.playAnimation(animation, loopType, check, 0.0F);
   }

   public boolean playAnimation(@Nullable Animation animation, Animation.LoopType loopType, boolean check, float tick) {
      if (check && animation != null) {
         EmotePlayer emotePlayer = this.mannequin.emotecraft$getEmote();
         if (animation.equals(emotePlayer.getCurrentAnimationInstance())) {
            return false;
         }
      }

      this.mannequin.emotecraft$playEmote(animation, loopType, tick, check);
      return true;
   }

   public void pause(boolean paused) {
      EmotePlayer emotePlayer = this.mannequin.emotecraft$getEmote();
      if (paused) {
         emotePlayer.pause();
      } else {
         emotePlayer.unpause();
      }
   }

   protected void method_48579(class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
      guiGraphics.method_51448().pushMatrix();
      if (this.renderBackround) {
         float alpha = class_3532.method_16439(this.field_22765, 0.0F, 0.5F);
         guiGraphics.method_25294(
            this.method_46426() + 1,
            this.method_46427() + 1,
            this.method_55442() - 1,
            this.method_55443() - 1,
            class_9848.method_61318(alpha, 0.0F, 0.0F, 0.0F)
         );
         WidgetOutliner.renderOutline(guiGraphics, this, class_9848.method_61317(alpha));
      }

      guiGraphics.method_44379(this.method_46426(), this.method_46427(), this.method_55442(), this.method_55443());

      try {
         int scale = this.method_25364() / (this.renderBackround ? 3 : 2);
         class_490.method_2486(
            guiGraphics,
            this.method_46426(),
            this.method_46427(),
            this.method_55442(),
            this.method_55443(),
            class_3532.method_48781(this.field_22765, 0, scale),
            0.0625F,
            mouseX,
            mouseY,
            this.mannequin
         );
      } catch (Throwable th) {
         CommonData.LOGGER.warn("Failed to render entity preview!", th);
      }

      guiGraphics.method_44380();
      guiGraphics.method_51448().popMatrix();
      if (this.animTime > 0.0F) {
         this.method_25350(1.0F - EASING_TRANSFORMER.get(this.animTime));
      }

      if (this.method_49606()) {
         guiGraphics.method_74037(this.method_37303() && this.mannequin.isPlayingEmote() ? class_11876.field_62458 : class_11875.field_62449);
      }
   }

   protected void method_47399(class_6382 narrationElementOutput) {
      this.method_37021(narrationElementOutput);
   }

   public void tick() {
      if (this.field_22764 && this.mannequin != null && this.mannequin.isPlayingEmote()) {
         this.animTime = 0.0F;
         this.method_25350(1.0F);

         try {
            this.mannequin.method_5773();
         } catch (Throwable th) {
            CommonData.LOGGER.warn("Failed to tick entity preview!", th);
         }
      } else {
         this.animTime = Math.min(1.0F, this.animTime + 0.1F);
      }
   }

   public class_11903 getMannequin() {
      return this.mannequin;
   }

   public void method_25354(class_1144 handler) {
   }
}
