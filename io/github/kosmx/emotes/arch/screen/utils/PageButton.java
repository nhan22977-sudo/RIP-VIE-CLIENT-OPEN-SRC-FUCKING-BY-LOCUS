package io.github.kosmx.emotes.arch.screen.utils;

import java.util.function.Consumer;
import net.minecraft.class_10799;
import net.minecraft.class_11907;
import net.minecraft.class_332;
import net.minecraft.class_4264;
import net.minecraft.class_5244;
import net.minecraft.class_6382;
import net.minecraft.class_8666;
import net.minecraft.class_9848;

public class PageButton extends class_4264 {
   public static final int PAGE_BUTTON_WIDTH = 12;
   public static final int PAGE_BUTTON_HEIGHT = 17;
   protected final class_8666 sprites;
   protected final boolean drawBackground;
   protected final Consumer<PageButton> onPress;

   public PageButton(class_8666 sprites, boolean background, Consumer<PageButton> onPress) {
      this(12, 17, sprites, background, onPress);
   }

   public PageButton(int width, int height, class_8666 sprites, boolean background, Consumer<PageButton> onPress) {
      this(0, 0, width, height, sprites, background, onPress);
   }

   public PageButton(int x, int y, int width, int height, class_8666 sprites, boolean background, Consumer<PageButton> onPress) {
      super(x, y, width, height, class_5244.field_39003);
      this.sprites = sprites;
      this.drawBackground = background;
      this.onPress = onPress;
   }

   protected void method_75752(class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (this.drawBackground) {
         super.method_75794(guiGraphics);
      }

      int width = this.drawBackground ? 12 : this.method_25368();
      int height = this.drawBackground ? 17 : this.method_25364();
      int x = this.method_46426();
      int y = this.method_46427();
      if (this.drawBackground) {
         x += (this.method_25368() - width) / 2;
         y += (this.method_25364() - height) / 2;
      }

      guiGraphics.method_52707(
         class_10799.field_56883,
         this.sprites.method_52729(this.field_22763, this.field_22763 && this.method_25367()),
         x,
         y,
         width,
         height,
         class_9848.method_61317(this.field_22765)
      );
   }

   public void method_25306(class_11907 inputWithModifiers) {
      this.onPress.accept(this);
   }

   protected void method_47399(class_6382 narrationElementOutput) {
      this.method_37021(narrationElementOutput);
   }
}
