package io.github.kosmx.emotes.arch.screen.widget.preview;

import com.mojang.authlib.GameProfile;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.gui.widgets.PlayerPreview;
import io.github.kosmx.emotes.arch.screen.widget.AbstractFastChooseWidget;
import io.github.kosmx.emotes.arch.screen.widget.FastChooseController;
import io.github.kosmx.emotes.arch.screen.widget.preview.elemets.PlayerChooseCircleElement;
import io.github.kosmx.emotes.arch.screen.widget.preview.elemets.PlayerChooseSquareElement;
import net.minecraft.class_310;
import net.minecraft.class_339;
import net.minecraft.class_5244;
import net.minecraft.class_6382;
import org.jspecify.annotations.NonNull;

public class PreviewFastChooseWidget extends AbstractFastChooseWidget {
   private final boolean animated;
   private float animTime = 1.0F;

   public PreviewFastChooseWidget(FastChooseController controller, boolean animated, int x, int y, int size) {
      super(controller, x, y, size, class_5244.field_39003);
      this.animated = animated;
      GameProfile profile = class_310.method_1551().method_53462();
      if (PlatformTools.getConfig().oldChooseWheel.get()) {
         this.addChild(new PlayerChooseCircleElement(this, profile, 0, 0.0F));
         this.addChild(new PlayerChooseCircleElement(this, profile, 1, 45.0F));
         this.addChild(new PlayerChooseCircleElement(this, profile, 2, 90.0F));
         this.addChild(new PlayerChooseCircleElement(this, profile, 3, 135.0F));
         this.addChild(new PlayerChooseCircleElement(this, profile, 4, 180.0F));
         this.addChild(new PlayerChooseCircleElement(this, profile, 5, 225.0F));
         this.addChild(new PlayerChooseCircleElement(this, profile, 6, 270.0F));
         this.addChild(new PlayerChooseCircleElement(this, profile, 7, 315.0F));
      } else {
         this.addChild(new PlayerChooseSquareElement(this, profile, 0, -1, -1));
         this.addChild(new PlayerChooseSquareElement(this, profile, 1, 0, -1));
         this.addChild(new PlayerChooseSquareElement(this, profile, 2, 1, -1));
         this.addChild(new PlayerChooseSquareElement(this, profile, 3, -1, 0));
         this.addChild(new PlayerChooseSquareElement(this, profile, 4, 1, 0));
         this.addChild(new PlayerChooseSquareElement(this, profile, 5, -1, 1));
         this.addChild(new PlayerChooseSquareElement(this, profile, 6, 0, 1));
         this.addChild(new PlayerChooseSquareElement(this, profile, 7, 1, 1));
      }
   }

   protected void method_47399(@NonNull class_6382 narrationElementOutput) {
      this.method_37021(narrationElementOutput);
   }

   @Override
   public void tick() {
      for (class_339 widget : this.method_25396()) {
         if (widget instanceof PlayerPreview preview) {
            preview.tick();
         }
      }

      if (this.animated) {
         this.animTime = Math.max(0.0F, this.animTime - 0.15F);
      }
   }

   public float getAnimTime() {
      return this.animated ? this.animTime : 0.0F;
   }
}
