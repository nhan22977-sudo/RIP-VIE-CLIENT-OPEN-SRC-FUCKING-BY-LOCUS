package io.github.kosmx.emotes.arch.screen.components;

import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.gui.widgets.EmoteListWidget;
import io.github.kosmx.emotes.arch.gui.widgets.PlayerPreview;
import io.github.kosmx.emotes.arch.gui.widgets.search.ISearchEngine;
import io.github.kosmx.emotes.arch.screen.utils.EmoteListener;
import io.github.kosmx.emotes.common.CommonData;
import io.github.kosmx.emotes.server.serializer.EmoteSerializer;
import io.github.kosmx.emotes.server.serializer.EmoteWriter;
import io.github.kosmx.emotes.server.services.InstanceService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_507;
import net.minecraft.class_5244;
import net.minecraft.class_8132;
import net.minecraft.class_8667;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

public abstract class EmoteSubScreen extends class_437 {
   protected final boolean reloadOnOpen;
   protected final ISearchEngine searchEngine;
   protected class_437 lastScreen;
   @Nullable
   public EmoteListener watcher;
   @Nullable
   protected PlayerPreview preview;
   @Nullable
   protected EmoteListWidget list;
   protected class_8132 layout = new class_8132(this);
   @Nullable
   protected class_342 searchBox;

   protected EmoteSubScreen(class_2561 title, boolean reloadOnOpen, class_437 lastScreen) {
      this(title, reloadOnOpen, ISearchEngine.getInstance(), lastScreen);
   }

   protected EmoteSubScreen(class_2561 title, boolean reloadOnOpen, ISearchEngine searchEngine, class_437 lastScreen) {
      super(title);
      this.reloadOnOpen = reloadOnOpen;
      this.searchEngine = searchEngine;
      this.lastScreen = lastScreen;
   }

   public void method_49589() {
      if (this.watcher == null) {
         this.watcher = EmoteListener.create(InstanceService.INSTANCE.getExternalEmoteDir());
         if (this.reloadOnOpen && this.watcher != null) {
            this.watcher.load(this::addOptions, this.field_22787);
         }
      }

      super.method_49589();
   }

   protected void method_25426() {
      this.addTitle();
      this.addPlayerPreview();
      this.addContents();
      this.addFooter();
      this.layout.method_48206(x$0 -> {
         class_339 var10000 = (class_339)this.method_37063(x$0);
      });
      this.method_48640();
   }

   protected void addTitle() {
      this.searchBox = (class_342)this.layout
         .method_48992(
            this.searchEngine.createEditBox(this.field_22793, class_507.field_25711, () -> Objects.requireNonNull(this.list).getEmotes(this.isSearchActive()))
         );
      this.searchBox.method_47404(class_507.field_25711);
      this.searchBox.method_1863(string -> Objects.requireNonNull(this.list).filter(this.searchEngine, this.isSearchActive(), string));
   }

   protected void addPlayerPreview() {
      this.preview = (PlayerPreview)this.layout
         .method_49000(new PlayerPreview(this.field_22787.method_53462(), 0, 0, 0, 0, true), layoutSettings -> layoutSettings.method_46461().method_46469(8));
   }

   protected EmoteListWidget newEmoteListWidget() {
      return new EmoteListWidget(this.field_22787, this.field_22789, this.layout.method_57727(), this.layout.method_48998(), 36) {
         @Override
         public void setSelected(@Nullable EmoteListWidget.ListEntry entry) {
            super.setSelected(entry);
            EmoteSubScreen.this.onPressed(entry);
         }

         @Override
         public boolean setLastFolder(EmoteListWidget.FolderEntry folder) {
            if (super.setLastFolder(folder)) {
               if (EmoteSubScreen.this.searchBox != null) {
                  EmoteSubScreen.this.searchBox.method_1852("");
               }

               if (EmoteSubScreen.this.preview != null) {
                  EmoteSubScreen.this.preview.getMannequin().stopEmote();
               }

               return true;
            } else {
               return false;
            }
         }
      };
   }

   protected void addContents() {
      this.list = (EmoteListWidget)this.layout.method_48999(this.newEmoteListWidget());
      this.addOptions();
   }

   protected abstract void addOptions();

   protected void addFooter() {
      class_8667 linearLayout = (class_8667)this.layout.method_48996(class_8667.method_52742().method_52735(8));
      if (this.list != null) {
         linearLayout.method_52736(this.list.createBackButton());
      }

      linearLayout.method_52736(class_4185.method_46430(class_5244.field_24334, button -> this.method_25419()).method_46432(200).method_46431());
   }

   protected abstract void onPressed(@Nullable EmoteListWidget.ListEntry var1);

   protected void method_48640() {
      this.layout.method_48222();
      if (this.preview != null) {
         this.preview.method_55445(this.field_22789 / 6, this.field_22790 / 2);
      }

      if (this.list != null) {
         this.list.method_57712(this.field_22789, this.layout);
         if (this.preview != null) {
            this.preview.field_22764 = this.preview.method_55442() <= this.list.method_25342();
         }
      }
   }

   public void method_25393() {
      if (this.watcher != null) {
         try {
            if (this.watcher.method_30292()) {
               this.watcher.load(this::addOptions, this.field_22787);
            }
         } catch (IOException ex) {
            CommonData.LOGGER.warn("Failed to poll for directory changes, stopping", ex);
            this.closeWatcher();
         }
      }

      super.method_25393();
      if (this.preview != null) {
         EmoteListWidget.ListEntry hovered = this.list.getHovered();
         if (this.list.method_25334() == hovered) {
            hovered = null;
         }

         if (hovered instanceof EmoteListWidget.EmoteEntry emote) {
            this.preview.playAnimation(emote.emote.emote, Animation.LoopType.DEFAULT, true);
         } else if (hovered instanceof EmoteListWidget.FolderEntry) {
            this.preview.getMannequin().stopEmote();
         }

         this.preview.tick();
      }
   }

   public void method_25432() {
      if (this.watcher != null) {
         this.watcher.blockWhileLoading();
      }

      super.method_25432();
      if (this.preview != null) {
         this.preview.getMannequin().stopEmote();
      }
   }

   private void closeWatcher() {
      if (this.watcher != null) {
         try {
            this.watcher.close();
            this.watcher = null;
         } catch (Throwable th) {
            CommonData.LOGGER.warn("Failed to close watcher!", th);
         }
      }
   }

   public void method_25419() {
      if (this.watcher != null && this.watcher.isLoading()) {
         PlatformTools.addToast(EmoteListener.RELOADING_WAIT);
      } else {
         this.field_22787.method_1507(this.lastScreen);
      }
   }

   public boolean method_25422() {
      return this.watcher == null || !this.watcher.isLoading();
   }

   public void method_25394(class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
      super.method_25394(guiGraphics, mouseX, mouseY, partialTick);
      this.searchEngine.method_25394(guiGraphics, mouseX, mouseY, partialTick);
   }

   public boolean method_25401(double mouseX, double mouseY, double scrollX, double scrollY) {
      return this.searchEngine.mouseScrolled(mouseX, mouseY, scrollX, scrollY) ? true : super.method_25401(mouseX, mouseY, scrollX, scrollY);
   }

   public void method_29638(List<Path> paths) {
      for (Path path : paths) {
         try (Stream<Path> stream = Files.walk(path)) {
            stream.forEach(emote -> {
               Map<String, Animation> animations = EmoteSerializer.serializeExternalEmote(emote);
               if (!animations.isEmpty()) {
                  for (Animation animation : animations.values()) {
                     try {
                        EmoteWriter.writeAnimationInBestFormat(animation, InstanceService.INSTANCE.getExternalEmoteDir());
                     } catch (Throwable th) {
                        CommonData.LOGGER.warn("Failed to move animation!", thx);
                     }
                  }
               }
            });
         } catch (Throwable th) {
            CommonData.LOGGER.warn("Failed to walk!", th);
         }
      }
   }

   public boolean isSearchActive() {
      return this.searchBox != null && !StringUtils.isBlank(this.searchBox.method_1882());
   }
}
