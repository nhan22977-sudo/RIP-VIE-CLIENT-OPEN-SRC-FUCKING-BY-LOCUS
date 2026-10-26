package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.ButtonSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.StringSetting;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_7923;
import net.minecraft.class_9304;
import net.minecraft.class_9334;

public class ItemReplacement extends Module {
   private static ItemReplacement INSTANCE;
   public static final int MAX_PAIRS = 8;
   public final IntSetting pairCount = (IntSetting)this.addSetting(
      new IntSetting(
         "PairCount",
         "Number of replacement pairs",
         1,
         1,
         8
      )
   );
   public final List pairs = new ArrayList();
   public final ButtonSetting addPairBtn = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "+ Add Item Replace",
         "Add another item to fake",
         () -> {
            if ((Integer)this.pairCount.get() < (8)) {
               this.pairCount.set((Integer)this.pairCount.get() + 1);
            }
         }
      )
   );
   public final ButtonSetting removePairBtn = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "- Remove Last",
         "Remove the last item replacement",
         () -> {
            if ((Integer)this.pairCount.get() > 1) {
               this.pairCount.set((Integer)this.pairCount.get() - 1);
            }
         }
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ItemReplacement() {
      super(
         "Item Replacement",
         "Client-side cosmetic item model replacement",
         Category.CUSTOMIZE
      );
      INSTANCE = this;
      this.pairCount.setVisibleInGui(false);
      String[][] var1 = new String[][]{
         {
               "wooden_sword",
               "diamond_sword"
         },
         {
               "stone_sword",
               "netherite_sword"
         },
         {"iron_sword", "mace"},
         {
               "stick",
               "netherite_sword"
         },
         {
               "leather_helmet",
               "diamond_helmet"
         },
         {
               "leather_chestplate",
               "diamond_chestplate"
         },
         {
               "leather_leggings",
               "diamond_leggings"
         },
         {
               "leather_boots",
               "diamond_boots"
         }
      };

      for (int var2 = 0; var2 < 8; var2++) {
         this.pairs.add(new ItemReplacement.ReplacePair(this, var2 + 1, var1[var2][0], var1[var2][1]));
      }

      this.removePairBtn.visibleWhen(() -> (Integer)this.pairCount.get() > 1);
   }

   public static ItemReplacement getInstance() {
      return INSTANCE;
   }

   public static boolean isActive() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   public static class_1799 getReplaced(class_1799 original) {
      if (isActive() && original != null && !original.method_7960()) {
         if (INSTANCE.pairs.isEmpty()) {
            return original;
         }

         int var1 = Math.min((Integer)INSTANCE.pairCount.get(), INSTANCE.pairs.size());

         for (int var2 = 0; var2 < var1; var2++) {
            ItemReplacement.ReplacePair var3 = (ItemReplacement.ReplacePair)INSTANCE.pairs.get(var2);
            class_1792 var4 = var3.getTarget();
            class_1792 var5 = var3.getReplacement();
            if (var4 != null && var5 != null && var4 != class_1802.field_8162 && var5 != class_1802.field_8162 && original.method_7909() == var4) {
               class_1799 var6 = new class_1799(var5, original.method_7947());
               if (original.method_57826(class_9334.field_49631)) {
                  var6.method_57379(class_9334.field_49631, (class_2561)original.method_58694(class_9334.field_49631));
               }

               if (original.method_57826(class_9334.field_49633)) {
                  var6.method_57379(class_9334.field_49633, (class_9304)original.method_58694(class_9334.field_49633));
               }

               if (original.method_57826(class_9334.field_49641)) {
                  var6.method_57379(class_9334.field_49641, (Boolean)original.method_58694(class_9334.field_49641));
               }

               return var6;
            }
         }

         return original;
      } else {
         return original;
      }
   }

   public static class_1792 resolveItem(String name) {
      if (name != null && !name.isBlank()) {
         String var1 = name.trim().toLowerCase(Locale.ROOT).replace(" ", "_");
         if (!var1.contains(":")) {
            byte[] var10000 = new byte[9];
            var10000[0] = (byte)(79);
            var10000[1] = (byte)(-13);
            var10000[2] = (byte)(114);
            var10000[3] = (byte)(-69);
            var10000[4] = (byte)(31);
            var10000[5] = (byte)(-46);
            var10000[6] = (byte)(55);
            var10000[7] = (byte)(54);
            var10000[8] = (byte)(62);
            class_2960 var2 = class_2960.method_60655($$v_s(var10000, 8670976043394519047L), var1);
            if (class_7923.field_41178.method_10250(var2)) {
               return (class_1792)class_7923.field_41178.method_63535(var2);
            }
         } else {
            class_2960 var5 = class_2960.method_12829(var1);
            if (var5 != null && class_7923.field_41178.method_10250(var5)) {
               return (class_1792)class_7923.field_41178.method_63535(var5);
            }
         }

         for (class_1792 var3 : class_7923.field_41178) {
            class_2960 var4 = class_7923.field_41178.method_10221(var3);
            if (var4.method_12832().equalsIgnoreCase(var1)) {
               return var3;
            }
         }

         return null;
      } else {
         return null;
      }
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

   public static class ReplacePair {
      public final StringSetting target;
      public final StringSetting replacement;
      private class_1792 cachedTarget = null;
      private String lastTargetStr = "";
      private class_1792 cachedReplacement = null;
      private String lastReplacementStr = "";
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public ReplacePair(ItemReplacement parent, int index, String defaultTarget, String defaultReplacement) {
         String var5 = index == 1 ? "" : " #" + index;
         this.target = (StringSetting)parent.addSetting(
            new StringSetting(
               "Target" + var5,
               "Item to replace (e.g. wooden_sword)",
               defaultTarget
            )
         );
         this.replacement = (StringSetting)parent.addSetting(
            new StringSetting(
               "Replacement" + var5,
               "Item to replace with (e.g. diamond_sword)",
               defaultReplacement
            )
         );
         if (index > 1) {
            this.target.visibleWhen(() -> (Integer)parent.pairCount.get() >= index);
            this.replacement.visibleWhen(() -> (Integer)parent.pairCount.get() >= index);
         }
      }

      public class_1792 getTarget() {
         String var1 = (String)this.target.get();
         if (this.cachedTarget == null || !var1.equals(this.lastTargetStr)) {
            this.lastTargetStr = var1;
            this.cachedTarget = ItemReplacement.resolveItem(var1);
         }

         return this.cachedTarget;
      }

      public class_1792 getReplacement() {
         String var1 = (String)this.replacement.get();
         if (this.cachedReplacement == null || !var1.equals(this.lastReplacementStr)) {
            this.lastReplacementStr = var1;
            this.cachedReplacement = ItemReplacement.resolveItem(var1);
         }

         return this.cachedReplacement;
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
}
