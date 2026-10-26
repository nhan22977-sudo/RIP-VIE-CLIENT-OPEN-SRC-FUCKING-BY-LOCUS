package io.github.kosmx.emotes.arch.screen.ingame;

import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.EmotecraftClientMod;
import io.github.kosmx.emotes.arch.network.client.ClientNetwork;
import io.github.kosmx.emotes.arch.screen.widget.AbstractFastChooseWidget;
import io.github.kosmx.emotes.arch.screen.widget.FastChooseController;
import io.github.kosmx.emotes.arch.screen.widget.IChooseElement;
import io.github.kosmx.emotes.arch.screen.widget.preview.PreviewFastChooseWidget;
import io.github.kosmx.emotes.main.config.CloseWheel;
import io.github.kosmx.emotes.main.network.ClientPacketManager;
import java.util.List;
import net.minecraft.class_11907;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_3532;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5244;
import net.minecraft.class_7847;
import net.minecraft.class_7940;
import net.minecraft.class_8132;
import net.minecraft.class_8667;
import org.jspecify.annotations.NonNull;

public class FastMenuScreen extends class_437 implements FastChooseController {
   protected static final class_2561 TITLE = class_2561.method_43471("emotecraft.fastmenu");
   private static final class_2561 WARN_NO_SERVER = class_2561.method_43471("emotecraft.no_server").method_54663(-2142128);
   public static final class_2561 WARN_DIFFERENT_SERVER = class_2561.method_43471("emotecraft.different_server").method_54663(-2142128);
   private static final class_2561 WARN_ONLY_PROXY = class_2561.method_43471("emotecraft.only_proxy");
   private final class_8132 layout = new class_8132(this, 0, 33);
   protected final class_437 parent;
   protected AbstractFastChooseWidget fastMenu;

   public FastMenuScreen(class_437 parent) {
      super(TITLE);
      this.parent = parent;
   }

   public void method_25426() {
      this.layout.method_48995(9 * 2);
      if (ClientNetwork.INSTANCE.isActive()) {
         if (ClientPacketManager.isInstanceOutdatedForStreaming(ClientNetwork.INSTANCE)) {
            class_7940 widget = (class_7940)this.layout
               .method_48992(
                  new class_7940(WARN_DIFFERENT_SERVER, this.field_22793)
                     .method_48981(true)
                     .method_48985(3)
                     .method_48984(class_3532.method_15384(this.field_22789 / 1.2))
               );
            this.layout.method_48995(widget.method_25364());
         } else {
            this.layout.method_48995(0);
         }
      } else if (ClientPacketManager.isAvailableProxy()) {
         this.layout.method_57726(WARN_ONLY_PROXY, this.field_22793);
      } else {
         this.layout.method_57726(WARN_NO_SERVER, this.field_22793);
      }

      this.fastMenu = (AbstractFastChooseWidget)this.layout.method_49000(new PreviewFastChooseWidget(this, true, 0, 0, 512), class_7847::method_46474);
      class_8667 linearLayout = (class_8667)this.layout.method_48996(class_8667.method_52742().method_52735(8));
      linearLayout.method_52736(class_4185.method_46430(class_5244.field_24335, button -> this.method_25419()).method_46432(120).method_46431());
      linearLayout.method_52736(
         class_4185.method_46430(FullMenuScreen.TITLE, button -> this.field_22787.method_1507(new FullMenuScreen(this))).method_46432(120).method_46431()
      );
      this.layout.method_48206(x$0 -> {
         class_339 var10000 = (class_339)this.method_37063(x$0);
      });
      this.method_48640();
   }

   protected void method_48640() {
      if (this.fastMenu != null) {
         this.fastMenu.setSize(Math.min(Math.round(Math.min(this.field_22789 * 0.8F, (this.field_22790 - this.layout.method_48998()) * 0.8F)), 512));
      }

      this.layout.method_48222();
   }

   public void method_25432() {
      super.method_25432();
      if (this.fastMenu != null) {
         this.fastMenu.removed();
      }
   }

   protected void method_57734(@NonNull class_332 guiGraphics) {
   }

   public boolean method_25404(@NonNull class_11908 keyEvent) {
      if (this.supportsKeyboardNavigation()) {
         List<IChooseElement> chooseElements = this.fastMenu.getChooseElements();
         int digit = keyEvent.method_74237() - 1;
         if (digit >= 0 && digit < chooseElements.size() && this.onClick(chooseElements.get(digit), keyEvent, false)) {
            return true;
         }
      }

      if (PlatformTools.getConfig().closeWheelType.get() == CloseWheel.PRESS && EmotecraftClientMod.OPEN_MENU_KEY.method_1417(keyEvent)) {
         return this.onToggleKey(keyEvent);
      }

      if (!super.method_25404(keyEvent)) {
         return false;
      }

      if (this.supportsKeyboardNavigation() && (keyEvent.method_74233() || keyEvent.method_74232())) {
         this.fastMenu.method_25404(keyEvent);
      }

      return true;
   }

   public boolean method_16803(@NonNull class_11908 keyEvent) {
      return PlatformTools.getConfig().closeWheelType.get() == CloseWheel.HOLD && EmotecraftClientMod.OPEN_MENU_KEY.method_1417(keyEvent)
         ? this.onToggleKey(keyEvent)
         : super.method_16803(keyEvent);
   }

   public boolean method_25402(@NonNull class_11909 event, boolean bl) {
      return PlatformTools.getConfig().closeWheelType.get() == CloseWheel.PRESS && EmotecraftClientMod.OPEN_MENU_KEY.method_1433(event)
         ? this.onToggleKey(event)
         : super.method_25402(event, bl);
   }

   public boolean method_25406(@NonNull class_11909 event) {
      return PlatformTools.getConfig().closeWheelType.get() == CloseWheel.HOLD && EmotecraftClientMod.OPEN_MENU_KEY.method_1433(event)
         ? this.onToggleKey(event)
         : super.method_25406(event);
   }

   protected boolean onToggleKey(class_11907 event) {
      this.method_25419();
      return true;
   }

   public void method_25393() {
      super.method_25393();
      if (this.fastMenu != null) {
         this.fastMenu.tick();
      }
   }

   public boolean method_25421() {
      return false;
   }

   public void method_25419() {
      this.field_22787.method_1507(this.parent);
   }

   @Override
   public boolean doHoverPart(IChooseElement part) {
      return part.hasEmote();
   }

   @Override
   public boolean isValidClickButton(class_11910 info) {
      return info.comp_4801() == 0;
   }

   @Override
   public boolean onClick(IChooseElement element, class_11907 event, boolean unused) {
      if (element.getEmote() != null) {
         boolean bl = element.getEmote().playEmote();
         if (bl) {
            class_310.method_1551().method_1507(null);
         }

         return bl;
      } else {
         return false;
      }
   }

   @Override
   public boolean doesShowInvalid() {
      return false;
   }

   @Override
   public boolean supportsKeyboardNavigation() {
      return PlatformTools.getConfig().enableWheelKeyboardNav.get();
   }
}
