package io.github.kosmx.emotes.arch.gui.widgets.search;

import io.github.kosmx.emotes.arch.gui.widgets.EmoteListWidget;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_342;

public class VanillaSearch implements ISearchEngine {
   public static final VanillaSearch INSTANCE = new VanillaSearch();

   protected VanillaSearch() {
   }

   @Override
   public class_342 createEditBox(class_327 font, class_2561 message, Supplier<List<EmoteListWidget.ListEntry>> entries) {
      return new class_342(font, 0, 0, 200, 20, message);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      return false;
   }

   public void method_25394(class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
   }

   @Override
   public Stream<EmoteListWidget.ListEntry> filter(Stream<EmoteListWidget.ListEntry> entries, String search) {
      return entries.filter(entry -> entry.matches(search.toLowerCase()));
   }
}
