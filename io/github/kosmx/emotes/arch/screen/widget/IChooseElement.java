package io.github.kosmx.emotes.arch.screen.widget;

import io.github.kosmx.emotes.main.EmoteHolder;
import net.minecraft.class_364;
import net.minecraft.class_4068;
import org.jetbrains.annotations.Nullable;

public interface IChooseElement extends class_364, class_4068 {
   boolean hasEmote();

   @Nullable
   EmoteHolder getEmote();

   void clearEmote();

   void setEmote(EmoteHolder var1);

   void removed();
}
