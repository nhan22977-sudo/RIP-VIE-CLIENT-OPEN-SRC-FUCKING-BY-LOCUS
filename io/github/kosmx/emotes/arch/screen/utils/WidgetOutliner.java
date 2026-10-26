package io.github.kosmx.emotes.arch.screen.utils;

import net.minecraft.class_10799;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_8021;

public class WidgetOutliner {
   public static void renderOutline(class_332 guiGraphics, class_8021 element, int color) {
      class_2960 headerSeparator = EmotecraftTexture.HEADER_SEPARATOR.identifier(class_310.method_1551().field_1687 != null);
      guiGraphics.method_25291(
         class_10799.field_56883, headerSeparator, element.method_46426(), element.method_46427() - 1, 0.0F, 0.0F, element.method_25368(), 2, 32, 2, color
      );
      drawSeparatorRotated(guiGraphics, headerSeparator, element.method_46426() - 1, element.method_46427(), element.method_25364(), -90.0F, color);
      drawSeparatorRotated(
         guiGraphics, headerSeparator, element.method_46426() + element.method_25368() + 1, element.method_46427(), element.method_25364(), 90.0F, color
      );
      class_2960 footerSeparator = EmotecraftTexture.FOOTER_SEPARATOR.identifier(class_310.method_1551().field_1687 != null);
      guiGraphics.method_25291(
         class_10799.field_56883,
         footerSeparator,
         element.method_46426(),
         element.method_46427() + element.method_25364() - 1,
         0.0F,
         0.0F,
         element.method_25368(),
         2,
         32,
         2,
         color
      );
   }

   protected static void drawSeparatorRotated(class_332 guiGraphics, class_2960 separator, int x, int y, int size, float angle, int color) {
      guiGraphics.method_51448().pushMatrix();
      guiGraphics.method_51448().translate(x, y + size / 2.0F);
      guiGraphics.method_51448().rotate((float)Math.toRadians(angle));
      guiGraphics.method_51448().translate(-size / 2.0F, 0.0F);
      guiGraphics.method_25291(class_10799.field_56883, separator, 0, 0, 0.0F, 0.0F, size, 2, 32, 2, color);
      guiGraphics.method_51448().popMatrix();
   }
}
