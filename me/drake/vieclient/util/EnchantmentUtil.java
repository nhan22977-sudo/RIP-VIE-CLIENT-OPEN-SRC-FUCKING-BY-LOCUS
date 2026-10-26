package me.drake.vieclient.util;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.LinkedHashMap;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_9304;
import net.minecraft.class_9334;

public final class EnchantmentUtil {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private EnchantmentUtil() {
   }

   public static boolean hasEnchantment(class_1799 itemStack, class_5321 enchantmentKey) {
      if (!itemStack.method_7960() && enchantmentKey != null) {
         try {
            LinkedHashMap var2 = new LinkedHashMap();
            populateEnchantmentMap(itemStack, var2);

            for (class_6880 var4 : var2.keySet()) {
               if (var4.method_40225(enchantmentKey)) {
                  return true;
               }
            }
         } catch (Exception var5) {
         }

         return false;
      } else {
         return false;
      }
   }

   public static void populateEnchantmentMap(class_1799 itemStack, LinkedHashMap enchantmentMap) {
      if (enchantmentMap != null && !itemStack.method_7960()) {
         enchantmentMap.clear();

         try {
            class_9304 var2;
            if (itemStack.method_7909() == class_1802.field_8598) {
               var2 = (class_9304)itemStack.method_58694(class_9334.field_49643);
            } else {
               var2 = itemStack.method_58657();
            }

            if (var2 != null) {
               for (Entry var4 : var2.method_57539()) {
                  enchantmentMap.put((class_6880)var4.getKey(), var4.getIntValue());
               }
            }
         } catch (Exception var5) {
         }
      }
   }
}
