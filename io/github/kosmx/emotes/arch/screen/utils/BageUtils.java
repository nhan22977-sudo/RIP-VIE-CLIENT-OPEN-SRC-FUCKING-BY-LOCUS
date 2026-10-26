package io.github.kosmx.emotes.arch.screen.utils;

import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_5251;
import net.minecraft.class_9848;
import org.jetbrains.annotations.Nullable;

public class BageUtils {
   private static final int SPACE = 3;

   public static int drawBadges(class_332 guiGraphics, class_327 font, List<class_2561> text, int x, int y, int width, boolean right) {
      int bageX = right ? 3 : x;

      for (class_2561 bage : text) {
         int tagWidth = font.method_27525(bage) + 6;
         if (bageX + tagWidth + 3 < width) {
            drawBadge(guiGraphics, font, bage, right ? x - tagWidth - bageX : bageX, y, tagWidth, -1);
            bageX += tagWidth + 3;
         }
      }

      return bageX;
   }

   public static void drawBadge(class_332 guiGraphics, class_327 font, class_2561 text, int x, int y, int color) {
      drawBadge(guiGraphics, font, text, x, y, font.method_27525(text) + 6, color);
   }

   public static void drawBadge(class_332 guiGraphics, class_327 font, class_2561 text, int x, int y, int tagWidth, int color) {
      class_5251 textColor = text.method_10866().method_10973();
      int outlineColor = getTextColor(textColor, color);
      int fillColor = getShadowColor(text.method_10866(), outlineColor);
      guiGraphics.method_25294(x + 1, y - 1, x + tagWidth, y, outlineColor);
      guiGraphics.method_25294(x, y, x + 1, y + 9, outlineColor);
      guiGraphics.method_25294(x + 1, y + 1 + 9 - 1, x + tagWidth, y + 9 + 1, outlineColor);
      guiGraphics.method_25294(x + tagWidth, y, x + tagWidth + 1, y + 9, outlineColor);
      guiGraphics.method_25294(x + 1, y, x + tagWidth, y + 9, fillColor);
      guiGraphics.method_51439(font, text, (int)(x + 1 + (tagWidth - font.method_27525(text)) / 2.0F), y + 1, color, false);
   }

   private static int getTextColor(@Nullable class_5251 textColor, int color) {
      if (textColor != null) {
         int i = class_9848.method_61320(color);
         int j = textColor.method_27716();
         return class_9848.method_61330(i, j);
      } else {
         return color;
      }
   }

   private static int getShadowColor(class_2583 style, int textColor) {
      Integer integer = style.method_65301();
      if (integer != null) {
         float f = class_9848.method_65100(textColor);
         float g = class_9848.method_65100(integer);
         return f != 1.0F ? class_9848.method_61330(class_9848.method_61326(f * g), integer) : integer;
      } else {
         return class_9848.method_61321(textColor, 0.25F);
      }
   }
}
