package io.github.kosmx.emotes.arch.screen.widget;

import net.minecraft.class_11907;
import net.minecraft.class_11910;

public interface FastChooseController {
   boolean doHoverPart(IChooseElement var1);

   boolean isValidClickButton(class_11910 var1);

   boolean onClick(IChooseElement var1, class_11907 var2, boolean var3);

   boolean doesShowInvalid();

   boolean supportsKeyboardNavigation();
}
