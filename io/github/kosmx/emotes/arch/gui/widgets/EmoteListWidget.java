package io.github.kosmx.emotes.arch.gui.widgets;

import com.mojang.blaze3d.opengl.GlStateManager;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.gui.widgets.search.ISearchEngine;
import io.github.kosmx.emotes.arch.gui.widgets.search.VanillaSearch;
import io.github.kosmx.emotes.arch.screen.utils.BageUtils;
import io.github.kosmx.emotes.arch.screen.utils.PageButton;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.mc.McUtils;
import it.unimi.dsi.fastutil.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.class_10799;
import net.minecraft.class_11875;
import net.minecraft.class_11876;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4280;
import net.minecraft.class_513;
import net.minecraft.class_5244;
import net.minecraft.class_5250;
import net.minecraft.class_8132;
import net.minecraft.class_9848;
import net.minecraft.class_332.class_12228;
import net.minecraft.class_3675.class_306;
import net.minecraft.class_4280.class_4281;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EmoteListWidget extends class_4280<EmoteListWidget.ListEntry> {
   private static final List<class_2561> LAST_OPENED_PATH = new CopyOnWriteArrayList<>();
   private final EmoteListWidget.FolderEntry mainFolder = new EmoteListWidget.FolderEntry(class_2561.method_43471("emotecraft.folder.main"));
   private EmoteListWidget.FolderEntry lastClickedFolder;
   private boolean compactMode;
   private final PageButton backButton = new PageButton(20, 20, class_513.field_45553, true, button -> {
      this.setLastFolder(null);
      if (this.lastClickedFolder == null) {
         this.method_44382(0.0);
      } else {
         this.method_25324(this.lastClickedFolder);
      }
   });

   public EmoteListWidget(class_310 minecraft, int width, int height, int y, int itemHeight) {
      super(minecraft, width, height, y, itemHeight);
      this.field_22744 = false;
      this.backButton.field_22763 = false;
   }

   public int method_25322() {
      return this.compactMode ? this.field_22758 : this.field_22758 / 2;
   }

   protected int method_65507() {
      return !this.compactMode ? super.method_65507() : this.method_46426() + this.method_25322() - 6;
   }

   protected void renderSelection(class_332 guiGraphics, EmoteListWidget.ListEntry entry, int outerColor) {
      if (this.compactMode && this.method_44392()) {
         int j = entry.method_46426();
         int k = entry.method_46427();
         int l = j + entry.method_25368() - 8;
         int m = k + entry.method_25364();
         guiGraphics.method_25294(j, k, l, m, outerColor);
         guiGraphics.method_25294(j + 1, k + 1, l - 1, m - 1, -16777216);
      } else {
         super.method_44398(guiGraphics, entry, outerColor);
      }
   }

   protected void renderItem(class_332 guiGraphics, int i, int j, float f, EmoteListWidget.ListEntry entry) {
      try {
         super.method_44397(guiGraphics, i, j, f, entry);
      } catch (Throwable var7) {
      }
   }

   public void setEmotes(Iterable<EmoteHolder> list, boolean showInvalid) {
      this.mainFolder.entries.clear();

      for (EmoteHolder emoteHolder : list) {
         if (emoteHolder.folder.isEmpty()) {
            this.mainFolder.entries.put(emoteHolder.get(), new EmoteListWidget.EmoteEntry(emoteHolder));
         } else {
            this.createFoldersTree(emoteHolder.folder).entries.put(emoteHolder.get(), new EmoteListWidget.EmoteEntry(emoteHolder));
         }
      }

      if (showInvalid) {
         for (EmoteHolder emoteHolder : this.getEmptyEmotes()) {
            this.mainFolder.entries.put(emoteHolder.get(), new EmoteListWidget.EmoteEntry(emoteHolder));
         }
      }

      this.filter(VanillaSearch.INSTANCE, false, "");

      for (class_2561 folderName : LAST_OPENED_PATH) {
         EmoteListWidget.ListEntry child = Objects.requireNonNullElse(this.lastClickedFolder, this.mainFolder).entries.get(folderName);
         if (!(child instanceof EmoteListWidget.FolderEntry folder)) {
            break;
         }

         this.lastClickedFolder = folder;
         this.setLastFolder(folder);
      }
   }

   public void filter(ISearchEngine engine, boolean isSearchActive, String search) {
      this.method_25339();
      this.method_73370(new EmoteListWidget.HeaderEntry(), 13);
      this.setSelected(null);
      engine.filter(this.getEmotes(isSearchActive).stream(), search).forEach(x$0 -> this.method_25321(x$0));
      this.method_65506();
   }

   public EmoteListWidget.FolderEntry createFoldersTree(List<class_2561> folders) {
      EmoteListWidget.FolderEntry last = this.mainFolder;

      for (class_2561 folder : folders) {
         last = last.getOrCreateFolder(folder);
      }

      return last;
   }

   public Iterable<EmoteHolder> getEmptyEmotes() {
      Collection<EmoteHolder> empties = new LinkedList<>();

      for (Pair<UUID, class_306> pair : PlatformTools.getConfig().emoteKeyMap) {
         if (!EmoteHolder.list.containsKey(pair.left())) {
            empties.add(new EmoteHolder.Empty((UUID)pair.left()));
         }
      }

      return empties;
   }

   public List<EmoteListWidget.ListEntry> getEmotes(boolean isSearchActive) {
      List<EmoteListWidget.ListEntry> emotes = new ArrayList<>();
      this.mainFolder.collectEmotes(isSearchActive, emotes);
      emotes.sort(EmoteListWidget.ListEntry::compareTo);
      return Collections.unmodifiableList(emotes);
   }

   public void method_57712(int width, class_8132 layout) {
      super.method_57712(width, layout);
      if (this.compactMode) {
         layout.method_48222();
         this.method_65506();
      }
   }

   public void method_73369(int width, int height, int x, int y) {
      super.method_73369(this.compactMode ? width / 3 : width, height, x, y);
   }

   @Nullable
   public EmoteHolder getFocusedEmote() {
      return this.method_25336() instanceof EmoteListWidget.EmoteEntry emote ? emote.getEmote() : null;
   }

   @Nullable
   public EmoteListWidget.ListEntry getHovered() {
      return (EmoteListWidget.ListEntry)super.method_37019();
   }

   public void setSelected(@Nullable EmoteListWidget.ListEntry selected) {
      super.method_25313(selected);
      if (selected instanceof EmoteListWidget.FolderEntry folder) {
         this.lastClickedFolder = folder;
         this.setLastFolder(folder);
      }
   }

   public boolean setLastFolder(EmoteListWidget.FolderEntry folder) {
      if (this.mainFolder.setLastFolder(folder)) {
         this.backButton.field_22763 = this.mainFolder.next != null;
         LAST_OPENED_PATH.clear();
         updateLastOpenedPath(this.mainFolder.next);
         return true;
      } else {
         return false;
      }
   }

   public void setCompactMode(boolean compactMode) {
      this.compactMode = compactMode;
   }

   public static class_5250 appendScreenPath(EmoteListWidget.FolderEntry folder, class_5250 component) {
      component = component.method_10852(McUtils.SLASH).method_10852(class_5244.field_41874);
      return folder.next != null
         ? appendScreenPath(folder.next, component.method_10852(folder.name).method_10852(class_5244.field_41874))
         : component.method_10852(folder.name.method_27661().method_27694(style -> style.method_10982(true)));
   }

   private static void updateLastOpenedPath(@Nullable EmoteListWidget.FolderEntry folder) {
      if (folder != null) {
         LAST_OPENED_PATH.add(folder.name);
         if (folder.next != null) {
            updateLastOpenedPath(folder.next);
         }
      }
   }

   public PageButton createBackButton() {
      return this.backButton;
   }

   // $VF: synthetic method
   static class_310 access$300(EmoteListWidget x0) {
      return x0.field_22740;
   }

   // $VF: synthetic method
   static class_310 access$500(EmoteListWidget x0) {
      return x0.field_22740;
   }

   // $VF: synthetic method
   static class_310 access$600(EmoteListWidget x0) {
      return x0.field_22740;
   }

   public class EmoteEntry extends EmoteListWidget.ListEntry {
      public final EmoteHolder emote;

      public EmoteEntry(EmoteHolder emote) {
         super(emote.name, emote.description, emote.bages);
         this.emote = emote;
      }

      @Override
      public void renderAdditional(class_332 matrices, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         if (!this.emote.author.getString().isEmpty()) {
            class_2561 text = class_2561.method_43471("emotecraft.emote.author").method_27692(class_124.field_1065).method_10852(this.emote.author);
            matrices.method_27535(EmoteListWidget.this.field_22740.field_1772, text, this.method_73380() + 31, this.method_73382() + 23, -8355712);
         }

         class_2960 texture = this.emote.getIconIdentifier();
         if (texture != null) {
            GlStateManager._enableBlend();
            matrices.method_25302(class_10799.field_56883, texture, this.method_73380(), this.method_73382(), 0.0F, 0.0F, 32, 32, 256, 256, 256, 256);
            GlStateManager._disableBlend();
         }
      }

      public EmoteHolder getEmote() {
         return this.emote;
      }

      @NotNull
      @Override
      public class_2561 method_37006() {
         return this.emote.name;
      }

      @Override
      public boolean matches(String string) {
         return super.matches(string)
            || this.description.getString().toLowerCase().contains(string.toLowerCase())
            || this.emote.fileName != null && this.emote.fileName.getString().toLowerCase().contains(string.toLowerCase())
            || this.emote.author.getString().equalsIgnoreCase(string);
      }

      @Override
      protected void collectEmotes(boolean excludeFolders, List<EmoteListWidget.ListEntry> collection) {
         collection.add(this);
      }

      @Override
      public boolean equals(Object obj) {
         return obj instanceof EmoteListWidget.EmoteEntry entry && this.emote.equals(entry.emote);
      }

      @Override
      public int hashCode() {
         return this.emote.hashCode();
      }

      @Override
      public int compareTo(@NotNull EmoteListWidget.ListEntry o) {
         return o instanceof EmoteListWidget.EmoteEntry ? super.compareTo(o) : 1;
      }
   }

   public class FolderEntry extends EmoteListWidget.ListEntry {
      public static final class_2960 FOLDER = McUtils.newIdentifier("textures/folder.png");
      public static final class_2960 FOLDER_OPEN = McUtils.newIdentifier("textures/folder_open.png");
      public static final class_2561 FOLDER_DESC = class_2561.method_43471("emotecraft.folder");
      private final Map<Object, EmoteListWidget.ListEntry> entries = new HashMap<>();
      private EmoteListWidget.FolderEntry next;

      private FolderEntry(Object obj) {
         this((class_2561)obj);
      }

      public FolderEntry(@NotNull class_2561 name) {
         super(name, FOLDER_DESC, Collections.emptyList());
      }

      @Override
      public void renderAdditional(class_332 guiGraphics, int mouseX, int mouseY, boolean hovering, float tickDelta) {
         guiGraphics.method_25290(
            class_10799.field_56883, hovering ? FOLDER_OPEN : FOLDER, this.method_46426(), this.method_73382(), 0.0F, 0.0F, 32, 32, 32, 32
         );
      }

      @Override
      protected void collectEmotes(boolean isSearchActive, List<EmoteListWidget.ListEntry> collection) {
         if (this.next != null && this.entries.containsValue(this.next)) {
            this.next.collectEmotes(isSearchActive, collection);
         } else {
            for (EmoteListWidget.ListEntry entry : this.entries.values()) {
               if (entry instanceof EmoteListWidget.FolderEntry folder) {
                  boolean isInvalid = StringUtils.isBlank(this.name.getString());
                  if (!isInvalid) {
                     collection.add(folder);
                  }

                  if (isSearchActive || isInvalid) {
                     for (EmoteListWidget.ListEntry folderEntry : folder.entries.values()) {
                        folderEntry.collectEmotes(isSearchActive, collection);
                     }
                  }
               } else {
                  entry.collectEmotes(isSearchActive, collection);
               }
            }
         }
      }

      public boolean setLastFolder(EmoteListWidget.FolderEntry folder) {
         if (this.next != null) {
            if (folder == null && this.next.next == null) {
               this.setSelectedFolder(null);
               return true;
            } else {
               return this.next.setLastFolder(folder);
            }
         } else {
            return this.setSelectedFolder(folder);
         }
      }

      public boolean setSelectedFolder(EmoteListWidget.FolderEntry folder) {
         if (folder != null && !this.entries.containsValue(folder)) {
            return false;
         }

         this.next = folder;
         return true;
      }

      public EmoteListWidget.FolderEntry getOrCreateFolder(class_2561 name) {
         return (EmoteListWidget.FolderEntry)this.entries.computeIfAbsent(name, x$0 -> EmoteListWidget.this.new FolderEntry(x$0));
      }

      @Override
      public boolean equals(Object obj) {
         return obj instanceof EmoteListWidget.FolderEntry entry && this.name.equals(entry.name);
      }

      @Override
      public int hashCode() {
         return this.name.hashCode();
      }

      @Override
      public int compareTo(@NotNull EmoteListWidget.ListEntry o) {
         return o instanceof EmoteListWidget.FolderEntry ? super.compareTo(o) : -1;
      }
   }

   public class HeaderEntry extends EmoteListWidget.ListEntry {
      public HeaderEntry() {
         super(class_5244.field_39003, class_5244.field_39003, Collections.emptyList());
      }

      @Override
      public void method_25343(class_332 guiGraphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         class_2561 path = EmoteListWidget.appendScreenPath(EmoteListWidget.this.mainFolder, class_2561.method_43473());
         if (EmoteListWidget.this.compactMode) {
            guiGraphics.method_75785(class_12228.field_63850)
               .method_75771(path, this.method_73380(), this.method_73380(), this.method_73389(), this.method_73382(), this.method_73382() + 9);
         } else {
            guiGraphics.method_75785(class_12228.field_63850)
               .method_75770(path, this.method_73380(), this.method_73389(), this.method_73382(), this.method_73382() + 9);
         }
      }

      @Override
      public void renderAdditional(class_332 matrices, int mouseX, int mouseY, boolean hovered, float tickDelta) {
      }

      @Override
      protected void collectEmotes(boolean isSearchActive, List<EmoteListWidget.ListEntry> collection) {
      }

      @Override
      public boolean equals(Object obj) {
         return obj instanceof EmoteListWidget.HeaderEntry;
      }

      @Override
      public int hashCode() {
         return 984359084;
      }

      @Override
      public int compareTo(@NotNull EmoteListWidget.ListEntry o) {
         return 1;
      }

      @Override
      public boolean matches(String string) {
         return false;
      }
   }

   public abstract class ListEntry extends class_4281<EmoteListWidget.ListEntry> implements Comparable<EmoteListWidget.ListEntry> {
      public final class_2561 name;
      public final class_2561 description;
      public final List<class_2561> bages;

      public ListEntry(@NotNull class_2561 name, class_2561 description, List<class_2561> bages) {
         this.name = name;
         this.description = description;
         this.bages = bages;
      }

      public void method_25343(class_332 matrices, int mouseX, int mouseY, boolean hovered, float tickDelta) {
         int maxX = this.method_73389() - (EmoteListWidget.this.compactMode && EmoteListWidget.this.method_44392() ? 7 : 0);
         matrices.method_44379(this.method_46426() - 1, this.method_46427() - 1, maxX, this.method_46427() + this.method_25364() + 1);
         if (hovered) {
            matrices.method_74037(this.method_25370() ? class_11875.field_62449 : class_11876.field_62455);
            matrices.method_25294(this.method_73380() - 1, this.method_73382() - 1, maxX, this.method_73386() + 1, class_9848.method_61324(128, 66, 66, 66));
         }

         int maxBadgesWidth = Math.max(maxX - EmoteListWidget.this.field_22740.field_1772.method_27525(this.name), maxX / 3) - (this.method_73380() + 31);
         int badgeWidth = BageUtils.drawBadges(
            matrices, EmoteListWidget.this.field_22740.field_1772, this.bages, maxX, this.method_73382(), maxBadgesWidth, true
         );
         matrices.method_75785(class_12228.field_63850)
            .method_75771(this.name, this.method_73380() + 31, this.method_73380() + 31, maxX - badgeWidth, this.method_73382(), this.method_73382() + 9);
         matrices.method_27535(EmoteListWidget.this.field_22740.field_1772, this.description, this.method_73380() + 31, this.method_73382() + 12, -8355712);
         this.renderAdditional(matrices, mouseX, mouseY, hovered, tickDelta);
         matrices.method_44380();
      }

      public abstract void renderAdditional(class_332 var1, int var2, int var3, boolean var4, float var5);

      @NotNull
      public class_2561 method_37006() {
         return this.name;
      }

      public boolean matches(String string) {
         return this.name.getString().toLowerCase().contains(string.toLowerCase());
      }

      protected abstract void collectEmotes(boolean var1, List<EmoteListWidget.ListEntry> var2);

      @Override
      public abstract boolean equals(Object var1);

      @Override
      public abstract int hashCode();

      public int compareTo(@NotNull EmoteListWidget.ListEntry o) {
         return this.name.getString().compareTo(o.name.getString());
      }
   }
}
