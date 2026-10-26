package io.github.kosmx.emotes.arch.gui.widgets.search;

import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.gui.widgets.EmoteListWidget;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_342;
import net.minecraft.class_4068;

public interface ISearchEngine extends class_4068 {
   class_342 createEditBox(class_327 var1, class_2561 var2, Supplier<List<EmoteListWidget.ListEntry>> var3);

   boolean mouseScrolled(double var1, double var3, double var5, double var7);

   Stream<EmoteListWidget.ListEntry> filter(Stream<EmoteListWidget.ListEntry> var1, String var2);

   static ISearchEngine getInstance() {
      return PlatformTools.HAS_SEARCHABLES ? new SearchablesSearch() : VanillaSearch.INSTANCE;
   }
}
