package io.github.kosmx.emotes.arch.screen;

import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.gui.screen.ConfigScreen;
import io.github.kosmx.emotes.arch.gui.widgets.EmoteListWidget;
import io.github.kosmx.emotes.arch.screen.components.EmoteSubScreen;
import io.github.kosmx.emotes.arch.screen.widget.AbstractFastChooseWidget;
import io.github.kosmx.emotes.arch.screen.widget.FastChooseController;
import io.github.kosmx.emotes.arch.screen.widget.IChooseElement;
import io.github.kosmx.emotes.arch.screen.widget.preview.PreviewFastChooseWidget;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.server.config.Serializer;
import java.util.UUID;
import net.minecraft.class_11907;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_2561;
import net.minecraft.class_3675;
import net.minecraft.class_410;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_7845;
import net.minecraft.class_7847;
import net.minecraft.class_7940;
import net.minecraft.class_8667;
import net.minecraft.class_3675.class_306;
import net.minecraft.class_3675.class_307;
import net.minecraft.class_7845.class_7939;
import org.jetbrains.annotations.NotNull;

public class EmoteMenu extends EmoteSubScreen implements FastChooseController {
   private static final class_2561 TITLE = class_2561.method_43471("emotecraft.menu");
   public static final class_2561 OPEN_FOLDER = class_2561.method_43471("emotecraft.openFolder");
   private static final class_2561 OPTIONS = class_2561.method_43471("emotecraft.options.options");
   public static final class_2561 RESET = class_2561.method_43471("controls.reset");
   private static final class_2561 KEYBIND = class_2561.method_43471("emotecraft.options.keybind");
   private static final class_2561 FASTMENU = class_2561.method_43471("emotecraft.options.fastmenu")
      .method_10852(class_5244.field_41874)
      .method_10852(class_2561.method_43471("emotecraft.options.fastmenu2"))
      .method_10852(class_5244.field_41874)
      .method_10852(class_2561.method_43471("emotecraft.options.fastmenu3"));
   private static final class_2561 SURE = class_2561.method_43471("emotecraft.sure");
   private static final class_2561 SURE2 = class_2561.method_43471("emotecraft.sure2");
   private static final class_2561 RESET_ONE = class_2561.method_43471("controls.reset");
   private static final class_2561 RESET_ALL = class_2561.method_43471("controls.resetAll");
   private static final class_2561 RESET_ALL_TITLE = class_2561.method_43471("emotecraft.resetAllKeys.title");
   private static final class_2561 RESET_ALL_MSG = class_2561.method_43471("emotecraft.resetAllKeys.message");
   public long activeKeyTime;
   private class_4185 setKeyButton;
   private class_4185 resetButton;
   private boolean resetOnlySelected;
   protected AbstractFastChooseWidget fastChoose;

   public EmoteMenu(class_437 parent) {
      super(TITLE, true, parent);
   }

   @Override
   protected void addContents() {
      class_8667 linearLayout = (class_8667)this.layout.method_48999(class_8667.method_52742().method_52735(8));
      this.list = (EmoteListWidget)linearLayout.method_52738(this.newEmoteListWidget(), class_7847::method_46476);
      this.list.setCompactMode(true);
      this.addOptions();
      class_7845 gridLayout = (class_7845)linearLayout.method_52736(new class_7845());
      gridLayout.method_46458().method_46466(4, 2, 4, 0);
      class_7939 rowHelper = gridLayout.method_47610(2);
      rowHelper.method_47613(new class_7940(KEYBIND, this.field_22793).method_48984(240), 2);
      this.setKeyButton = (class_4185)rowHelper.method_47612(class_4185.method_46430(class_3675.field_16237.method_27445(), button -> {
         if (this.list != null && this.list.method_25334() != null) {
            this.activeKeyTime = 200L;
         }
      }).method_46432(120).method_46431());
      this.setKeyButton.field_22763 = false;
      this.resetButton = (class_4185)rowHelper.method_47612(class_4185.method_46430(RESET, this::resetKeyAction).method_46432(120).method_46431());
      this.resetButton.field_22763 = false;
      rowHelper.method_47614(new class_7940(FASTMENU, this.field_22793).method_48984(240), 2, gridLayout.method_46457().method_46471(8));
      this.fastChoose = (AbstractFastChooseWidget)rowHelper.method_47614(
         new PreviewFastChooseWidget(this, false, 0, 0, 256), 2, rowHelper.method_47611().method_46467().method_46471(4)
      );
   }

   @Override
   protected void addOptions() {
      if (this.list != null) {
         this.list.setEmotes(EmoteHolder.list, true);
      }
   }

   @Override
   protected void addFooter() {
      class_8667 linearLayout = (class_8667)this.layout.method_48996(class_8667.method_52742().method_52735(8));
      if (this.list != null) {
         linearLayout.method_52736(this.list.createBackButton());
      }

      linearLayout.method_52736(class_4185.method_46430(OPEN_FOLDER, button -> PlatformTools.openExternalEmotesDir()).method_46432(120).method_46431());
      linearLayout.method_52736(class_4185.method_46430(class_5244.field_24334, button -> this.method_25419()).method_46432(120).method_46431());
      linearLayout.method_52736(
         class_4185.method_46430(OPTIONS, button -> this.field_22787.method_1507(new ConfigScreen(this))).method_46432(120).method_46431()
      );
   }

   private void resetKeyAction(class_4185 button) {
      if (this.resetOnlySelected) {
         if (this.list == null || this.list.method_25336() == null) {
            return;
         }

         PlatformTools.getConfig().emoteKeyMap.removeL(this.list.getFocusedEmote().getUuid());
         this.onPressed((EmoteListWidget.ListEntry)this.list.method_25334());
      } else {
         this.field_22787.method_1507(new class_410(aBoolean -> {
            if (aBoolean) {
               PlatformTools.getConfig().emoteKeyMap.clear();
               this.onPressed((EmoteListWidget.ListEntry)this.list.method_25334());
            }

            this.field_22787.method_1507(this);
         }, RESET_ALL_TITLE, RESET_ALL_MSG.method_27661().method_27693(" (" + PlatformTools.getConfig().emoteKeyMap.size() + ")")));
      }
   }

   @Override
   protected void method_48640() {
      if (this.fastChoose != null) {
         this.fastChoose.setSize(Math.min(Math.round(Math.min(this.field_22789 / 2.5F, this.field_22790 / 2.3F)), 256));
      }

      super.method_48640();
   }

   @Override
   protected void onPressed(EmoteListWidget.ListEntry selected) {
      if (this.resetButton != null) {
         this.setKeyButton.field_22763 = this.resetButton.field_22763 = selected instanceof EmoteListWidget.EmoteEntry;
         if (selected instanceof EmoteListWidget.EmoteEntry entry) {
            this.setKeyButton.method_25355(getKey(entry.getEmote().getUuid()).method_27445());
            this.resetOnlySelected = PlatformTools.getConfig().emoteKeyMap.containsL(entry.getEmote().getUuid());
         } else {
            this.resetOnlySelected = false;
         }

         if (this.resetOnlySelected) {
            this.resetButton.field_22763 = true;
            this.resetButton.method_25355(RESET_ONE);
         } else if (!PlatformTools.getConfig().emoteKeyMap.isEmpty()) {
            this.resetButton.field_22763 = true;
            this.resetButton.method_25355(RESET_ALL.method_27661().method_27693(" (" + PlatformTools.getConfig().emoteKeyMap.size() + ")"));
         } else {
            this.resetButton.field_22763 = false;
            this.resetButton.method_25355(RESET_ONE);
         }
      }
   }

   @Override
   public void method_25393() {
      if (this.activeKeyTime == 1L) {
         this.method_25395(null);
      }

      if (this.activeKeyTime != 0L) {
         this.activeKeyTime--;
      }

      super.method_25393();
      if (this.fastChoose != null) {
         this.fastChoose.tick();
      }
   }

   public boolean method_25402(class_11909 event, boolean bl) {
      return this.activeKeyTime != 0L && this.list != null && this.list.method_25336() != null
         ? this.setKey(class_307.field_1672.method_1447(event.method_74245()))
         : super.method_25402(event, bl);
   }

   private boolean setKey(class_306 key) {
      boolean bl = false;
      if (this.list != null && this.list.method_25336() != null) {
         bl = true;
         if (!this.applyKey(false, this.list.getFocusedEmote(), key)) {
            this.field_22787.method_1507(new class_410(choice -> {
               if (choice) {
                  this.applyKey(true, this.list.getFocusedEmote(), key);
               }

               this.field_22787.method_1507(this);
            }, SURE, SURE2));
         }
      }

      return bl;
   }

   private boolean applyKey(boolean force, EmoteHolder emote, class_306 key) {
      boolean bl = true;

      for (EmoteHolder emoteHolder : EmoteHolder.list) {
         if (!key.equals(class_3675.field_16237) && getKey(emoteHolder.getUuid()).equals(key)) {
            bl = false;
            if (force) {
               PlatformTools.getConfig().emoteKeyMap.removeL(emoteHolder.getUuid());
            }
         }
      }

      if (bl || force) {
         PlatformTools.getConfig().emoteKeyMap.put(emote.getUuid(), key);
         this.onPressed((EmoteListWidget.ListEntry)this.list.method_25334());
      }

      this.activeKeyTime = 0L;
      return bl;
   }

   @NotNull
   public static class_306 getKey(UUID emoteID) {
      class_306 key;
      return (key = PlatformTools.getConfig().emoteKeyMap.getR(emoteID)) == null ? class_3675.field_16237 : key;
   }

   @Override
   public void method_25432() {
      super.method_25432();
      Serializer.INSTANCE.saveConfig();
      if (this.fastChoose != null) {
         this.fastChoose.removed();
      }
   }

   public boolean method_25404(class_11908 keyEvent) {
      if (this.list == null || this.list.method_25336() == null || this.activeKeyTime == 0L) {
         return super.method_25404(keyEvent);
      } else {
         return keyEvent.method_74231() ? this.setKey(class_3675.field_16237) : this.setKey(class_3675.method_15985(keyEvent));
      }
   }

   @Override
   public boolean isValidClickButton(class_11910 info) {
      return (info.comp_4801() == 0 || info.comp_4801() == 1) && this.activeKeyTime == 0L;
   }

   @Override
   public boolean onClick(IChooseElement element, class_11907 event, boolean bl) {
      if (this.activeKeyTime != 0L) {
         return false;
      } else if (event.method_74228() == 1) {
         element.clearEmote();
         return true;
      } else if (this.list != null && this.list.method_25336() != null) {
         element.setEmote(this.list.getFocusedEmote());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean doHoverPart(IChooseElement part) {
      return this.activeKeyTime == 0L;
   }

   @Override
   public boolean doesShowInvalid() {
      return true;
   }

   @Override
   public boolean supportsKeyboardNavigation() {
      return false;
   }
}
