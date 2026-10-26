package me.drake.vieclient.modules.client;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class InventoryProtect extends Module {
   private static InventoryProtect INSTANCE;
   private final EnumSetting style = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Style",
         "Camouflage style for inventory slots",
         InventoryProtect.MaskStyle.TOTEM,
         InventoryProtect.MaskStyle.values()
      )
   );
   private final BoolSetting fillEmpty = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Fill Empty",
         "Camouflage empty slots to conceal item stack counts",
         true
      )
   );
   private final BoolSetting hideTooltips = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Hide Tooltips",
         "Hide hovered item tooltips (enchants, lore, durability)",
         true
      )
   );
   private final BoolSetting hideItemName = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Hide Item Name",
         "Hide item name overlay when cycling hotbar slots",
         true
      )
   );
   private final BoolSetting maskHotbar = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Mask Hotbar",
         "Apply camouflage to on-screen hotbar HUD",
         false
      )
   );
   private final BoolSetting maskContainers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Containers",
         "Apply camouflage in chests, shulker boxes, and ender chests",
         true
      )
   );
   private final class_1799 totemStack = new class_1799(class_1802.field_8288);
   private final class_1799[] dummyItems = new class_1799[]{
      new class_1799(class_1802.field_8831),
      new class_1799(class_1802.field_20412),
      new class_1799(class_1802.field_8118),
      new class_1799(class_1802.field_8279),
      new class_1799(class_1802.field_8107)
   };
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public InventoryProtect() {
      super(
         "InventoryProtect",
         "Conceal sensitive inventory and container items from stream/video leaks",
         Category.PROTECT
      );
      INSTANCE = this;
   }

   public static InventoryProtect get() {
      return INSTANCE;
   }

   public static boolean isActive() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   public static boolean shouldHideTooltips() {
      return isActive() && (Boolean)INSTANCE.hideTooltips.get();
   }

   public static boolean shouldHideItemName() {
      return isActive() && (Boolean)INSTANCE.hideItemName.get();
   }

   public static boolean shouldMaskHotbar() {
      return isActive() && (Boolean)INSTANCE.maskHotbar.get();
   }

   public static boolean shouldMaskContainers() {
      return isActive() && (Boolean)INSTANCE.maskContainers.get();
   }

   public class_1799 getMaskedStack(class_1735 slot, class_1799 original) {
      if (!this.isEnabled()) {
         return original;
      }

      if ((original == null || original.method_7960()) && !(Boolean)this.fillEmpty.get()) {
         return original;
      }

      return switch ((InventoryProtect.MaskStyle)this.style.get()) {
         case TOTEM -> this.totemStack;
         case EMPTY -> class_1799.field_8037;
         case OBFUSCATED -> {
            int var3 = slot != null ? Math.abs(slot.field_7874 % this.dummyItems.length) : 0;
            yield this.dummyItems[var3];
         }
      };
   }

   @Override
   public String getInfo() {
      return ((InventoryProtect.MaskStyle)this.style.get()).name();
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

   public enum MaskStyle {
      TOTEM,
      EMPTY,
      OBFUSCATED;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static InventoryProtect.MaskStyle[] $values() {
         InventoryProtect.MaskStyle[] var10000 = new InventoryProtect.MaskStyle[3];
         var10000[0] = TOTEM;
         var10000[1] = EMPTY;
         var10000[2] = OBFUSCATED;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(-19);
         TOTEM = new InventoryProtect.MaskStyle();
         var10002 = new byte[5];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-41);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-7);
         EMPTY = new InventoryProtect.MaskStyle();
         var10002 = new byte[10];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-40);
         var10002[2] = (byte)(90);
         var10002[3] = (byte)(-117);
         var10002[4] = (byte)(-13);
         var10002[5] = (byte)(74);
         var10002[6] = (byte)(16);
         var10002[7] = (byte)(63);
         var10002[8] = (byte)(21);
         var10002[9] = (byte)(-26);
         OBFUSCATED = new InventoryProtect.MaskStyle();
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
