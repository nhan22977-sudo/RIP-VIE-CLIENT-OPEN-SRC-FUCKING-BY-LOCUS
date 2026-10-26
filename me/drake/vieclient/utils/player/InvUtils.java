package me.drake.vieclient.utils.player;

import java.util.function.Predicate;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_310;

public class InvUtils {
   private static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private InvUtils() {
   }

   public static InvUtils.FindItemResult find(class_1792 item) {
      if (mc.field_1724 == null) {
         return InvUtils.FindItemResult.empty();
      }

      for (int var1 = 0; var1 < (9); var1++) {
         if (mc.field_1724.method_31548().method_5438(var1).method_7909() == item) {
            return new InvUtils.FindItemResult(var1, mc.field_1724.method_31548().method_5438(var1));
         }
      }

      int var2 = InventoryUtil.findInInventory(item);
      return var2 != (-1) ? new InvUtils.FindItemResult(var2, mc.field_1724.method_31548().method_5438(var2)) : InvUtils.FindItemResult.empty();
   }

   public static InvUtils.FindItemResult find(Predicate pred) {
      if (mc.field_1724 == null) {
         return InvUtils.FindItemResult.empty();
      }

      for (int var1 = 0; var1 < (9); var1++) {
         if (pred.test(mc.field_1724.method_31548().method_5438(var1))) {
            return new InvUtils.FindItemResult(var1, mc.field_1724.method_31548().method_5438(var1));
         }
      }

      return InvUtils.FindItemResult.empty();
   }

   public static InvUtils.FindItemResult findInHotbar(class_1792 item) {
      return find(item);
   }

   public static InvUtils.FindItemResult findInHotbar(Predicate p) {
      return find(p);
   }

   public static void swap(int slot) {
      if (mc.field_1724 != null && slot >= 0 && slot <= (8)) {
         mc.field_1724.method_31548().method_61496(slot);
      }
   }

   public static void move(int from, int to) {
      if (mc.field_1724 != null && mc.field_1724.field_7512 != null) {
         if (from != to && from >= 0 && to >= 0 && from <= (8) && to <= (8)) {
            InventoryUtil.clickSlot(mc.field_1724.field_7512.field_7763, from, 0, class_1713.field_7790);
            InventoryUtil.clickSlot(mc.field_1724.field_7512.field_7763, to, 0, class_1713.field_7790);
         }
      }
   }

   public static InvUtils.Click click() {
      return new InvUtils.Click();
   }

   public static class Click {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public InvUtils.Click slot(int slot) {
         if (InvUtils.mc.field_1724 != null && InvUtils.mc.field_1724.field_7512 != null) {
            InventoryUtil.clickSlot(InvUtils.mc.field_1724.field_7512.field_7763, slot, 0, class_1713.field_7790);
         }

         return this;
      }

      public void slotHotbar(int slot) {
         InvUtils.swap(slot);
      }

      public void slotOffhand() {
         if (InvUtils.mc.field_1724 != null && InvUtils.mc.field_1724.field_7512 != null) {
            InventoryUtil.clickSlot(InvUtils.mc.field_1724.field_7512.field_7763, 45, 0, class_1713.field_7790);
         }
      }
   }

   public static class FindItemResult {
      public final int slot;
      public final class_1799 stack;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private FindItemResult(int slot, class_1799 stack) {
         this.slot = slotx;
         this.stack = stackx;
      }

      public boolean found() {
         return this.slot != (-1);
      }

      public int slot() {
         return this.slot;
      }

      static InvUtils.FindItemResult empty() {
         return new InvUtils.FindItemResult(-1, class_1799.field_8037);
      }
   }
}
