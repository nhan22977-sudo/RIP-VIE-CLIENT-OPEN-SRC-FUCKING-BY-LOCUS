package io.github.kosmx.emotes.arch.screen.widget;

import io.github.kosmx.emotes.PlatformTools;
import io.github.kosmx.emotes.arch.screen.utils.PageButton;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1144;
import net.minecraft.class_11908;
import net.minecraft.class_11909;
import net.minecraft.class_2561;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_332;
import net.minecraft.class_339;
import net.minecraft.class_364;
import net.minecraft.class_4068;
import net.minecraft.class_4069;
import net.minecraft.class_513;
import net.minecraft.class_8016;
import net.minecraft.class_8023;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public abstract class AbstractFastChooseWidget extends class_339 implements class_4069 {
   private final List<class_339> elements = new ArrayList<>();
   private final List<IChooseElement> chooseElements = new ArrayList<>();
   public final FastChooseController controller;
   @Nullable
   private class_364 focused;
   private boolean isDragging;
   protected final PageButton forwardButton = new PageButton(class_513.field_45552, false, this::onForwardButton);
   protected final PageButton backButton = new PageButton(class_513.field_45553, false, this::onBackButton);
   private static int currentPage;
   private double scrollAccumulator = 0.0;

   protected AbstractFastChooseWidget(FastChooseController controller, int x, int y, int size, class_2561 message) {
      super(x, y, size, size, message);
      this.controller = controller;
      this.elements.add(this.forwardButton);
      this.elements.add(this.backButton);
   }

   protected void method_48579(class_332 guiGraphics, int mouseX, int mouseY, float partialTick) {
      int centerX = this.method_46426() + this.method_25368() / 2;
      int centerY = this.method_46427() + this.method_25364() / 2;
      class_2561 text = class_2561.method_43470(String.valueOf(this.getCurrentPage() + 1));
      class_327 font = class_310.method_1551().field_1772;
      int textWidth = font.method_27525(text);
      int buttonPadding = Math.max((12 + textWidth) / 2 + 2, this.globalPadding());
      this.forwardButton.method_48229(centerX - this.forwardButton.method_25368() / 2 + buttonPadding, centerY - this.forwardButton.method_25364() / 2);
      this.backButton.method_48229(centerX - this.backButton.method_25368() / 2 - buttonPadding, centerY - this.backButton.method_25364() / 2);
      guiGraphics.method_27535(font, text, centerX - textWidth / 2, centerY - 9 / 2, -1);

      for (class_4068 renderable : this.elements) {
         renderable.method_25394(guiGraphics, mouseX, mouseY, partialTick);
      }
   }

   public int globalPadding() {
      return Math.min(this.method_25368() / 8, this.method_25364() / 8);
   }

   public abstract void tick();

   public void removed() {
      for (class_339 widget : this.elements) {
         if (widget instanceof IChooseElement element) {
            element.removed();
         }
      }
   }

   @NotNull
   public List<class_339> method_25396() {
      return this.elements;
   }

   protected void addChild(class_339 widget) {
      this.elements.add(widget);
      if (widget instanceof IChooseElement element) {
         this.chooseElements.add(element);
      }
   }

   public List<IChooseElement> getChooseElements() {
      return this.chooseElements;
   }

   public boolean method_25397() {
      return this.isDragging;
   }

   public void method_25398(boolean isDragging) {
      this.isDragging = isDragging;
   }

   @Nullable
   public class_364 method_25399() {
      return this.focused;
   }

   public void method_25395(@Nullable class_364 focused) {
      if (this.focused != null) {
         this.focused.method_25365(false);
      }

      if (focused != null) {
         focused.method_25365(true);
      }

      this.focused = focused;
   }

   public int getCurrentPage() {
      return currentPage;
   }

   public void setSize(int size) {
      this.method_55445(size, size);
   }

   public void method_25354(class_1144 handler) {
   }

   public boolean method_25402(class_11909 event, boolean bl) {
      super.method_25402(event, bl);
      return super.method_25402(event, bl);
   }

   protected void onForwardButton(PageButton button) {
      if (currentPage < PlatformTools.getConfig().fastMenuEmotes.length - 1) {
         currentPage++;
      } else {
         currentPage = 0;
      }
   }

   protected void onBackButton(PageButton button) {
      if (currentPage > 0) {
         currentPage--;
      } else {
         currentPage = PlatformTools.getConfig().fastMenuEmotes.length - 1;
      }
   }

   public boolean method_25401(double mouseX, double mouseY, double scrollX, double scrollY) {
      super.method_25401(mouseX, mouseY, scrollX, scrollY);
      boolean ret = super.method_25401(mouseX, mouseY, scrollX, scrollY);
      this.scrollAccumulator += scrollX;
      int maxPages = PlatformTools.getConfig().fastMenuEmotes.length;

      while (this.scrollAccumulator >= 2.0) {
         this.scrollAccumulator -= 2.0;
         currentPage = (currentPage + 1) % maxPages;
         ret = true;
      }

      while (this.scrollAccumulator <= -2.0) {
         this.scrollAccumulator += 2.0;
         currentPage = (currentPage - 1 + maxPages) % maxPages;
         ret = true;
      }

      return ret;
   }

   @Nullable
   public class_8016 method_48205(class_8023 event) {
      return super.method_48205(event);
   }

   public boolean method_25406(class_11909 event) {
      super.method_25406(event);
      return super.method_25406(event);
   }

   public boolean method_25403(class_11909 event, double d, double e) {
      super.method_25403(event, d, e);
      return super.method_25403(event, d, e);
   }

   public boolean method_25370() {
      return super.method_25370();
   }

   public void method_25365(boolean focused) {
      super.method_25365(focused);
   }

   public boolean method_25404(@NonNull class_11908 event) {
      if (this.controller.supportsKeyboardNavigation()) {
         if (event.method_74232()) {
            this.onBackButton(this.backButton);
         }

         if (event.method_74233()) {
            this.onForwardButton(this.forwardButton);
         }
      }

      return super.method_25404(event);
   }
}
