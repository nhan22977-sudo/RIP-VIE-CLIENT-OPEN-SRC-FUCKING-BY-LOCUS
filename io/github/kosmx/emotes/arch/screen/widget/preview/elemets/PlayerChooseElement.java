package io.github.kosmx.emotes.arch.screen.widget.preview.elemets;

import com.mojang.authlib.GameProfile;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.easing.EasingType;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.gui.widgets.PlayerPreview;
import io.github.kosmx.emotes.arch.screen.utils.EmotecraftTexture;
import io.github.kosmx.emotes.arch.screen.utils.WidgetOutliner;
import io.github.kosmx.emotes.arch.screen.widget.AbstractFastChooseWidget;
import io.github.kosmx.emotes.arch.screen.widget.IChooseElement;
import io.github.kosmx.emotes.arch.screen.widget.preview.PreviewFastChooseWidget;
import io.github.kosmx.emotes.main.EmoteHolder;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import java.time.Duration;
import java.util.UUID;
import net.minecraft.class_10799;
import net.minecraft.class_1144;
import net.minecraft.class_11876;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_3532;
import net.minecraft.class_7919;
import net.minecraft.class_9848;
import org.joml.Vector2f;

public abstract class PlayerChooseElement extends PlayerPreview implements IChooseElement {
   private static final Float2FloatFunction EASING_TRANSFORMER = EasingType.EASE_IN_CIRC.buildTransformer(null);
   protected final AbstractFastChooseWidget parent;
   protected final int id;
   private boolean isAnimFinishing;

   public PlayerChooseElement(AbstractFastChooseWidget parent, GameProfile profile, int id) {
      super(profile, 0, 0, 0, 0, false);
      this.mannequin.emotecraft$getEmote().muteNbs = true;
      this.parent = parent;
      this.id = id;
      this.tick();
   }

   public void method_25394(class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.method_25394(guiGraphics, this.isAnimFinishing ? mouseX : 0, this.isAnimFinishing ? mouseY : 0, partialTick);
   }

   protected abstract void updateRectangle(float var1);

   protected abstract void getDirectionVector(Vector2f var1);

   @Override
   public void method_48579(class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
      boolean doHoverPart = this.parent.controller.doHoverPart(this);
      float progress = this.parent instanceof PreviewFastChooseWidget widget ? widget.getAnimTime() : 0.0F;
      float easedProgress = 1.0F - EASING_TRANSFORMER.get(progress);
      this.isAnimFinishing = easedProgress > 0.9F;
      this.updateRectangle(easedProgress);
      this.renderBackground(guiGraphics);
      if (this.method_25367() && doHoverPart) {
         this.renderHover(guiGraphics);
      }

      EmoteHolder emoteHolder = this.getEmote();
      if (this.parent.controller.supportsKeyboardNavigation() && emoteHolder != null) {
         this.renderTileId(guiGraphics, easedProgress);
      }

      if (emoteHolder != null && PlatformTools.getConfig().showIconsIfPossible.get() && emoteHolder.getIconIdentifier() != null) {
         guiGraphics.method_25302(
            class_10799.field_56883,
            emoteHolder.getIconIdentifier(),
            this.method_46426(),
            this.method_46427(),
            0.0F,
            0.0F,
            this.method_25368(),
            this.method_25364(),
            256,
            256,
            256,
            256
         );
      } else if (emoteHolder != null || !PlatformTools.getConfig().showIconsIfPossible.get()) {
         super.method_48579(guiGraphics, this.method_46426() + this.method_25368() / 2, this.method_46427() + this.method_25364() / 2, partialTick);
      }

      if (this.method_25367() && emoteHolder != null) {
         this.method_47400(class_7919.method_47407(emoteHolder.name));
         this.method_47402(Duration.ZERO);
      } else {
         this.method_47400(null);
      }

      if (this.method_49606()) {
         guiGraphics.method_74037(
            this.method_37303() && doHoverPart ? (emoteHolder != null ? class_11876.field_62455 : class_11876.field_62458) : class_11876.field_62459
         );
      }
   }

   protected void renderBackground(class_332 guiGraphics) {
      class_2960 texture = EmotecraftTexture.MENU_LIST_BACKGROUND.identifier(class_310.method_1551().field_1687 != null);
      guiGraphics.method_25290(
         class_10799.field_56883,
         texture,
         this.method_46426() + 1,
         this.method_46427() + 1,
         this.method_55442(),
         this.method_55443(),
         this.method_25368() - 2,
         this.method_25364() - 2,
         32,
         32
      );
      WidgetOutliner.renderOutline(guiGraphics, this, -1);
   }

   protected void renderHover(class_332 guiGraphics) {
      guiGraphics.method_25294(this.method_46426(), this.method_46427(), this.method_55442(), this.method_55443(), class_9848.method_61324(128, 66, 66, 66));
   }

   protected void renderTileId(class_332 gg, float easedProgress) {
      Vector2f dir = new Vector2f();
      this.getDirectionVector(dir);
      if (dir.x != 0.0F || dir.y != 0.0F) {
         float inv = class_3532.method_48119(dir.x * dir.x + dir.y * dir.y);
         float nx = dir.x * inv;
         float ny = dir.y * inv;
         float cx0 = this.method_46426() + this.method_25368() * 0.5F;
         float cy0 = this.method_46427() + this.method_25364() * 0.5F;
         float cx1 = this.parent.method_46426() + this.parent.method_25368() * 0.5F;
         float cy1 = this.parent.method_46427() + this.parent.method_25364() * 0.5F;
         float t = 1.0F - easedProgress;
         float cx = class_3532.method_16439(t, cx0, cx1);
         float cy = class_3532.method_16439(t, cy0, cy1);
         float halfW = this.method_25368() * 0.5F;
         float halfH = this.method_25364() * 0.5F;
         float edge = Math.min(halfW / Math.abs(nx), halfH / Math.abs(ny));
         float offset = edge + 14.0F + Math.min(this.method_25368(), this.method_25364()) * 0.25F * t;
         float px = cx + nx * offset;
         float py = cy + ny * offset;
         class_327 font = class_310.method_1551().field_1772;
         String s = Integer.toString(this.id + 1);
         gg.method_25303(font, s, Math.round(px - font.method_1727(s) / 2.0F), Math.round(py - 9.0F / 2.0F), -1);
      }
   }

   @Override
   public void removed() {
      this.mannequin.stopEmote();
   }

   @Override
   public boolean hasEmote() {
      return PlatformTools.getConfig().fastMenuEmotes[this.parent.getCurrentPage()][this.id] != null;
   }

   @Override
   public EmoteHolder getEmote() {
      UUID uuid = PlatformTools.getConfig().fastMenuEmotes[this.parent.getCurrentPage()][this.id];
      if (uuid != null) {
         EmoteHolder emote = EmoteHolder.list.get(uuid);
         if (emote == null && this.parent.controller.doesShowInvalid()) {
            emote = new EmoteHolder.Empty(uuid);
         }

         return emote;
      } else {
         return null;
      }
   }

   @Override
   public void clearEmote() {
      this.setEmote(null);
   }

   @Override
   public void setEmote(EmoteHolder emote) {
      PlatformTools.getConfig().fastMenuEmotes[this.parent.getCurrentPage()][this.id] = emote == null ? null : emote.getUuid();
   }

   @Override
   public void tick() {
      EmoteHolder holder = this.getEmote();
      float previewTick = holder == null ? 1.0F : calculatePreviewTick(holder.getEmote());
      boolean shouldPlayAgain = holder == null || holder.getEmote().loopType().shouldPlayAgain(null, holder.getEmote());
      boolean updated = this.playAnimation(
         holder == null ? null : holder.getEmote(), shouldPlayAgain ? Animation.LoopType.DEFAULT : Animation.LoopType.LOOP, true, previewTick
      );
      super.pause(!updated && !this.method_25367());
      if (updated || this.method_25367()) {
         super.tick();
      }
   }

   protected static float calculatePreviewTick(Animation animation) {
      ExtraAnimationData data = animation.data();
      if (data.has("previewTick")) {
         return (Float)data.getRaw("previewTick");
      }

      if (animation.loopType().shouldPlayAgain(null, animation)) {
         float returnToTick = animation.loopType().restartFromTick(null, animation);
         if (returnToTick > 0.0F) {
            return (animation.length() - returnToTick) / 2.0F;
         }
      }

      return animation.length() / 2.0F;
   }

   protected boolean method_25351(class_11910 button) {
      return this.parent.controller.isValidClickButton(button);
   }

   public boolean method_25402(class_11909 event, boolean bl) {
      return super.method_25402(event, bl) ? this.parent.controller.onClick(this, event, bl) : false;
   }

   @Override
   public void method_25354(class_1144 handler) {
      if (this.parent.controller.doHoverPart(this)) {
         method_62888(handler);
      }
   }
}
