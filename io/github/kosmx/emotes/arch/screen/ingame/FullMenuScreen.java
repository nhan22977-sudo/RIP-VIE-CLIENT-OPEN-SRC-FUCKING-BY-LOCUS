package io.github.kosmx.emotes.arch.screen.ingame;

import io.github.kosmx.emotes.arch.gui.widgets.EmoteListWidget;
import io.github.kosmx.emotes.arch.screen.EmoteMenu;
import io.github.kosmx.emotes.arch.screen.components.EmoteSubScreen;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.emotePlay.EmotePlayer;
import io.github.kosmx.emotes.main.mixinFunctions.IPlayerEntity;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_8667;

public class FullMenuScreen extends EmoteSubScreen {
   protected static final class_2561 TITLE = class_2561.method_43471("emotecraft.emotelist");
   protected static final class_2561 CONFIG = class_2561.method_43471("emotecraft.config");

   public FullMenuScreen(class_437 parent) {
      super(TITLE, false, parent);
   }

   @Override
   protected void addOptions() {
      this.list.setEmotes(EmoteHolder.list, false);
   }

   @Override
   protected void addFooter() {
      class_8667 linearLayout = (class_8667)this.layout.method_48996(class_8667.method_52742().method_52735(8));
      if (this.list != null) {
         linearLayout.method_52736(this.list.createBackButton());
      }

      linearLayout.method_52736(class_4185.method_46430(class_5244.field_24335, button -> this.method_25419()).method_46431());
      linearLayout.method_52736(class_4185.method_46430(CONFIG, button -> this.field_22787.method_1507(new EmoteMenu(this))).method_46431());
   }

   @Override
   protected void onPressed(EmoteListWidget.ListEntry selected) {
      if (selected instanceof EmoteListWidget.EmoteEntry entry && entry.getEmote().playEmote() && this.lastScreen instanceof FastMenuScreen fast) {
         this.lastScreen = fast.parent;
      }
   }

   protected void method_57734(class_332 guiGraphics) {
      if (!(this.field_22787.field_1724 instanceof IPlayerEntity entity && EmotePlayer.isRunningEmote(entity.emotecraft$getEmote()))) {
         super.method_57734(guiGraphics);
      }
   }

   @Override
   protected void method_48640() {
      this.addOptions();
      super.method_48640();
      this.layout.method_48222();
   }

   @Override
   public void method_25393() {
      if (this.preview != null && this.list.method_25334() == this.list.getHovered()) {
         this.preview.getMannequin().stopEmote();
      }

      super.method_25393();
   }

   public boolean method_25421() {
      return false;
   }
}
