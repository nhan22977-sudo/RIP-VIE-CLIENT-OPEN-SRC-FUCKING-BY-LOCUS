package me.drake.vieclient.util;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.function.Predicate;
import me.drake.vieclient.mixin.MixinClientPlayerInteractionManagerAccessor;
import net.minecraft.class_10192;
import net.minecraft.class_1304;
import net.minecraft.class_1661;
import net.minecraft.class_1713;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4174;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_746;
import net.minecraft.class_9285;
import net.minecraft.class_9334;

public class InventoryUtil {
   private static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static void switchTo(int slot) {
      if (mc.field_1724 != null && slot >= 0 && slot <= (8)) {
         mc.field_1724.method_31548().method_61496(slot);
      }
   }

   private static void syncSlot() {
      if (mc.field_1724 != null && mc.field_1761 != null) {
         ((MixinClientPlayerInteractionManagerAccessor)mc.field_1761).invokeSyncSlot();
      }
   }

   public static void swap(int selectedSlot) {
      if (mc.field_1724 != null && selectedSlot >= 0 && selectedSlot <= (8)) {
         mc.field_1724.method_31548().method_61496(selectedSlot);
         syncSlot();
      }
   }

   public static boolean swapStack(Predicate predicate) {
      if (mc.field_1724 == null) {
         return false;
      }

      class_1661 var1 = mc.field_1724.method_31548();

      for (int var2 = 0; var2 < (9); var2++) {
         if (predicate.test(var1.method_5438(var2))) {
            var1.method_61496(var2);
            syncSlot();
            return true;
         }
      }

      return false;
   }

   public static boolean swapItem(Predicate predicate) {
      return swapStack(stack -> !stack.method_7960() && predicate.test(stack.method_7909()));
   }

   public static boolean swap(class_1792 targetItem) {
      return swapItem(item -> item == targetItem);
   }

   public static boolean selectItemFromHotbar(class_1792 targetItem) {
      if (mc.field_1724 == null) {
         return false;
      }

      class_1661 var1 = mc.field_1724.method_31548();

      for (int var2 = 0; var2 < (9); var2++) {
         if (var1.method_5438(var2).method_7909() == targetItem) {
            var1.method_61496(var2);
            syncSlot();
            return true;
         }
      }

      return false;
   }

   public static int getSelectedSlot() {
      return mc.field_1724 == null ? 0 : mc.field_1724.method_31548().method_67532();
   }

   public static void clickSlot(int syncId, int slotIndex, int button, class_1713 actionType) {
      if (mc.field_1761 != null) {
         mc.field_1761.method_2906(syncId, slotIndex, button, actionType, mc.field_1724);
      }
   }

   public static int findInHotbar(class_1792 item) {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 0; var1 < (9); var1++) {
         if (mc.field_1724.method_31548().method_5438(var1).method_7909() == item) {
            return var1;
         }
      }

      return -1;
   }

   public static int findInHotbar(Predicate predicate) {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 0; var1 < (9); var1++) {
         if (predicate.test(mc.field_1724.method_31548().method_5438(var1))) {
            return var1;
         }
      }

      return -1;
   }

   public static int findInInventory(class_1792 item) {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 0; var1 < (36); var1++) {
         if (mc.field_1724.method_31548().method_5438(var1).method_7909() == item) {
            return var1;
         }
      }

      return -1;
   }

   public static int findBestFood(class_746 player) {
      if (player == null) {
         return -1;
      }

      int var1 = -1;
      float var2 = -1.0F;

      for (int var3 = 0; var3 < (36); var3++) {
         class_1799 var4 = player.method_31548().method_5438(var3);
         if (!var4.method_7960()) {
            class_4174 var5 = (class_4174)var4.method_58694(class_9334.field_50075);
            if (var5 != null) {
               float var6 = var5.comp_2491() + var5.comp_2492() * 2.0F;
               if (var6 > var2) {
                  var2 = var6;
                  var1 = var3;
               }
            }
         }
      }

      return var1;
   }

   public static int findBestFoodInHotbar(class_746 player) {
      if (player == null) {
         return -1;
      }

      int var1 = -1;
      float var2 = -1.0F;

      for (int var3 = 0; var3 < (9); var3++) {
         class_1799 var4 = player.method_31548().method_5438(var3);
         if (!var4.method_7960()) {
            class_4174 var5 = (class_4174)var4.method_58694(class_9334.field_50075);
            if (var5 != null) {
               float var6 = var5.comp_2491() + var5.comp_2492() * 2.0F;
               if (var6 > var2) {
                  var2 = var6;
                  var1 = var3;
               }
            }
         }
      }

      return var1;
   }

   public static int findBestTool(class_2680 state) {
      if (mc.field_1724 == null) {
         return -1;
      }

      int var1 = -1;
      float var2 = 1.0F;

      for (int var3 = 0; var3 < (9); var3++) {
         class_1799 var4 = mc.field_1724.method_31548().method_5438(var3);
         if (!var4.method_7960()) {
            float var5 = var4.method_7924(state);
            if (var5 > var2) {
               var2 = var5;
               var1 = var3;
            }
         }
      }

      return var1;
   }

   public static int findBestArmor(class_1304 slot) {
      if (mc.field_1724 == null) {
         return -1;
      }

      int var1 = -1;
      int var2 = -1;

      for (int var3 = 0; var3 < (36); var3++) {
         class_1799 var4 = mc.field_1724.method_31548().method_5438(var3);
         if (!var4.method_7960()) {
            class_10192 var5 = (class_10192)var4.method_58694(class_9334.field_54196);
            if (var5 != null && var5.comp_3174() == slot) {
               class_9285 var6 = (class_9285)var4.method_58694(class_9334.field_49636);
               int var7 = var6 != null ? var6.hashCode() : 0;
               if (var7 > var2) {
                  var2 = var7;
                  var1 = var3;
               }
            }
         }
      }

      return var1;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static int getArmorValue(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         class_10192 var1 = (class_10192)stack.method_58694(class_9334.field_54196);
         if (var1 == null) {
            return -1;
         }

         class_1304 var2 = var1.comp_3174();
         if (var2 == null) {
            return -1;
         }

         return switch (<unrepresentable>.$SwitchMap$net$minecraft$entity$EquipmentSlot[var2.ordinal()]) {
            case 1, 2 -> 1;
            case 3 -> 2;
            case 4 -> 1;
            default -> 0;
         };
      } else {
         return -1;
      }
   }

   public static int getProtectionValue(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         int var1 = 0;
         var1 += getEnchantmentLevel(
            stack,
            "minecraft",
            "protection"
         );
         var1 += getEnchantmentLevel(
            stack,
            "minecraft",
            "projectile_protection"
         );
         var1 += getEnchantmentLevel(
            stack,
            "minecraft",
            "fire_protection"
         );
         var1 += getEnchantmentLevel(
            stack,
            "minecraft",
            "blast_protection"
         );
         return var1
            + getEnchantmentLevel(
               stack,
               $$v_s(new byte[]{(byte)92, (byte)-13, (byte)114, (byte)-69, (byte)-70, (byte)-51, (byte)-48, (byte)-80, (byte)45}, -93905576027500524L),
               $$v_s(
                  new byte[]{
                     (byte)87,
                     (byte)-1,
                     (byte)125,
                     (byte)-86,
                     (byte)-79,
                     (byte)-38,
                     (byte)-61,
                     (byte)-119,
                     (byte)63,
                     (byte)-61,
                     (byte)-120,
                     (byte)-102,
                     (byte)-24,
                     (byte)9,
                     (byte)-2
                  },
                  -93905576027500524L
               )
            );
      } else {
         return 0;
      }
   }

   private static int getEnchantmentLevel(class_1799 stack, String namespace, String path) {
      if (stack != null && !stack.method_7960()) {
         LinkedHashMap var3 = new LinkedHashMap();
         EnchantmentUtil.populateEnchantmentMap(stack, var3);

         for (Entry var5 : var3.entrySet()) {
            if (((class_6880)var5.getKey()).method_40230().isPresent()) {
               class_2960 var6 = ((class_5321)((class_6880)var5.getKey()).method_40230().get()).method_29177();
               if (var6.method_12836().equals(namespace) && var6.method_12832().equals(path)) {
                  return (Integer)var5.getValue();
               }
            }
         }

         return 0;
      } else {
         return 0;
      }
   }

   public static int countItem(class_1792 item) {
      if (mc.field_1724 == null) {
         return 0;
      }

      int var1 = 0;

      for (int var2 = 0; var2 < (36); var2++) {
         class_1799 var3 = mc.field_1724.method_31548().method_5438(var2);
         if (var3.method_7909() == item) {
            var1 += var3.method_7947();
         }
      }

      class_1799 var4 = mc.field_1724.method_6079();
      if (var4.method_7909() == item) {
         var1 += var4.method_7947();
      }

      return var1;
   }

   public static int findBlockInHotbar() {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var0 = 0; var0 < (9); var0++) {
         class_1799 var1 = mc.field_1724.method_31548().method_5438(var0);
         if (!var1.method_7960() && var1.method_7909() instanceof class_1747) {
            return var0;
         }
      }

      return -1;
   }

   public static int findBestArmorPiece(class_746 player, int slotIndex) {
      if (player == null) {
         return -1;
      }

      class_1304 var2;
      if (slotIndex >= (36)) {
         var2 = switch (slotIndex) {
            case 36 -> class_1304.field_6166;
            case 37 -> class_1304.field_6172;
            case 38 -> class_1304.field_6174;
            case 39 -> class_1304.field_6169;
            default -> null;
         };
      } else {
         var2 = switch (slotIndex) {
            case 0 -> class_1304.field_6166;
            case 1 -> class_1304.field_6172;
            case 2 -> class_1304.field_6174;
            case 3 -> class_1304.field_6169;
            default -> null;
         };
      }

      return var2 == null ? -1 : findBestArmor(var2);
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }
}
