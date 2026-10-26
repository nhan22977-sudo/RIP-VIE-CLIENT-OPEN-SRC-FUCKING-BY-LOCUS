package io.github.kosmx.emotes.arch.gui.widgets.search;

import com.blamejared.searchables.api.SearchableComponent;
import com.blamejared.searchables.api.SearchableType;
import com.blamejared.searchables.api.SearchableType.Builder;
import com.blamejared.searchables.api.autcomplete.AutoCompletingEditBox;
import com.blamejared.searchables.api.context.ContextVisitor;
import com.blamejared.searchables.api.context.SearchContext;
import com.blamejared.searchables.lang.StringSearcher;
import io.github.kosmx.emotes.arch.gui.widgets.EmoteListWidget;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_342;

public class SearchablesSearch implements ISearchEngine {
   public static final SearchableType<EmoteListWidget.ListEntry> SEARCHABLE = new Builder()
      .defaultComponent(
         SearchableComponent.create(
            "default",
            holder -> Optional.ofNullable(holder.name).<String>map(class_2561::getString).filter(str -> !str.isEmpty()),
            EmoteListWidget.ListEntry::matches
         )
      )
      .component(
         SearchableComponent.create("name", holder -> Optional.ofNullable(holder.name).<String>map(class_2561::getString).filter(str -> !str.isEmpty()))
      )
      .component(
         SearchableComponent.create(
            "description", holder -> Optional.ofNullable(holder.description).<String>map(class_2561::getString).filter(str -> !str.isEmpty())
         )
      )
      .component(
         SearchableComponent.create(
            "author",
            entry -> entry instanceof EmoteListWidget.EmoteEntry holder
               ? Optional.ofNullable(holder.emote.author).<String>map(class_2561::getString).filter(str -> !str.isEmpty())
               : Optional.empty()
         )
      )
      .component(
         SearchableComponent.create(
            "fileName",
            entry -> entry instanceof EmoteListWidget.EmoteEntry holder
               ? Optional.ofNullable(holder.emote.fileName).<String>map(class_2561::getString).filter(str -> !str.isEmpty())
               : Optional.empty()
         )
      )
      .build();
   protected AutoCompletingEditBox<EmoteListWidget.ListEntry> search;

   @Override
   public class_342 createEditBox(class_327 font, class_2561 message, Supplier<List<EmoteListWidget.ListEntry>> entries) {
      return this.search = new SearchablesSearch.FixedAutoCompletingEditBox<>(font, 0, 0, 200, 20, message, SEARCHABLE, entries);
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
      return this.search == null ? false : this.search.autoComplete().method_25401(mouseX, mouseY, scrollX, scrollY);
   }

   public void method_25394(class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
      if (this.search != null) {
         this.search.autoComplete().method_25394(guiGraphics, mouseX, mouseY, partialTick);
      }
   }

   @Override
   public Stream<EmoteListWidget.ListEntry> filter(Stream<EmoteListWidget.ListEntry> entries, String search) {
      Optional<SearchContext<EmoteListWidget.ListEntry>> context = StringSearcher.search(search, new ContextVisitor());
      return entries.filter(
         context.<Predicate<? super EmoteListWidget.ListEntry>>map(tSearchContext -> tSearchContext.createPredicate(SEARCHABLE)).orElse(t -> true)
      );
   }

   public static class FixedAutoCompletingEditBox<T> extends AutoCompletingEditBox<T> {
      public FixedAutoCompletingEditBox(
         class_327 font, int x, int y, int width, int height, class_2561 message, SearchableType<T> type, Supplier<List<T>> entries
      ) {
         super(font, x, y, width, height, message, type, entries);
      }

      public boolean method_25402(class_11909 event, boolean doubleClick) {
         return !this.method_25367() ? false : super.method_25402(event, doubleClick);
      }

      public void method_46421(int x) {
         this.autoComplete().method_46421(x);
         super.method_46421(x);
      }

      public void method_46419(int y) {
         super.method_46419(y);
         this.autoComplete().method_46419(this.method_46427() + 2 + this.method_25364());
      }

      public void method_25358(int width) {
         this.autoComplete().method_25358(width);
         super.method_25358(width);
      }

      public void method_53533(int height) {
         this.autoComplete().method_53533(height);
         super.method_53533(height);
         this.autoComplete().method_46419(this.method_46427() + 2 + this.method_25364());
      }

      public void method_55445(int width, int height) {
         this.autoComplete().method_55445(width, height);
         super.method_55445(width, height);
         this.autoComplete().method_46419(this.method_46427() + 2 + this.method_25364());
      }
   }
}
