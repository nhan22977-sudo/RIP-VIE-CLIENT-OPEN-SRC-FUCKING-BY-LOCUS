package me.drake.vieclient.modules.donut.autobase.economy;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_7923;

public final class InventoryCapacitySimulator {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static InventoryCapacitySimulator.SimulationResult simulateInsertion(
      List slots, String incomingItemId, int incomingCount, int maxStackSize, int reserveEmptySlots
   ) {
      if (incomingCount <= 0) {
         return new InventoryCapacitySimulator.SimulationResult(true, 0, countEmptySlots(slots), reserveEmptySlots, "");
      }

      int var5 = incomingCount;
      int var6 = countEmptySlots(slots);

      for (InventoryCapacitySimulator.SlotInfo var8 : slots) {
         if (!var8.isProtected() && !var8.isEmpty() && var8.itemId().equalsIgnoreCase(incomingItemId)) {
            int var9 = var8.maxCount() - var8.count();
            if (var9 > 0) {
               int var10 = Math.min(var9, var5);
               var5 -= var10;
               if (var5 <= 0) {
                  break;
               }
            }
         }
      }

      if (var5 <= 0) {
         return new InventoryCapacitySimulator.SimulationResult(true, incomingCount, var6, reserveEmptySlots, "");
      } else {
         int var11 = (int)Math.ceil((double)var5 / maxStackSize);
         int var12 = var6 - var11;
         if (var11 > var6) {
            return new InventoryCapacitySimulator.SimulationResult(
               false, incomingCount - var5, var6, reserveEmptySlots, "Not enough empty slots in inventory (needed " + var11 + ", available " + var6 + ")"
            );
         } else {
            return var12 < reserveEmptySlots
               ? new InventoryCapacitySimulator.SimulationResult(
                  false,
                  incomingCount - var5,
                  var12,
                  reserveEmptySlots,
                  "Violates reserve empty slot rule: requires " + var11 + " slots, leaving " + var12 + " (minimum reserve: " + reserveEmptySlots + ")"
               )
               : new InventoryCapacitySimulator.SimulationResult(true, incomingCount, var12, reserveEmptySlots, "");
         }
      }
   }

   private static int countEmptySlots(List slots) {
      int var1 = 0;

      for (InventoryCapacitySimulator.SlotInfo var3 : slots) {
         if (!var3.isProtected() && var3.isEmpty()) {
            var1++;
         }
      }

      return var1;
   }

   public static List extractSlots(class_1661 inv) {
      ArrayList var1 = new ArrayList();
      if (inv == null) {
         return var1;
      }

      for (int var2 = 0; var2 < (36); var2++) {
         class_1799 var3 = inv.method_5438(var2);
         if (var3.method_7960()) {
            byte[] var10004 = new byte[13];
            var10004[0] = (byte)(87);
            var10004[1] = (byte)(-13);
            var10004[2] = (byte)(114);
            var10004[3] = (byte)(-69);
            var10004[4] = (byte)(104);
            var10004[5] = (byte)(88);
            var10004[6] = (byte)(16);
            var10004[7] = (byte)(-120);
            var10004[8] = (byte)(38);
            var10004[9] = (byte)(-104);
            var10004[10] = (byte)(-123);
            var10004[11] = (byte)(-97);
            var10004[12] = (byte)(33);
            var1.add(new InventoryCapacitySimulator.SlotInfo(var2, $$v_s(var10004, -4146983260264345569L), 0, 64, false));
         } else {
            String var4 = class_7923.field_41178.method_10221(var3.method_7909()).toString();
            var1.add(new InventoryCapacitySimulator.SlotInfo(var2, var4, var3.method_7947(), var3.method_7914(), false));
         }
      }

      return var1;
   }

   public static int countAvailableItems(class_1661 inv, String cleanItemId) {
      if (inv != null && cleanItemId != null) {
         int var2 = 0;

         for (int var3 = 0; var3 < (36); var3++) {
            class_1799 var4 = inv.method_5438(var3);
            if (!var4.method_7960()) {
               String var5 = class_7923.field_41178.method_10221(var4.method_7909()).toString();
               if (var5.equalsIgnoreCase(cleanItemId)) {
                  var2 += var4.method_7947();
               }
            }
         }

         return var2;
      } else {
         return 0;
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

   public record SimulationResult(boolean canFit, int itemsFitted, int emptySlotsRemaining, int reserveSlotsRequired, String rejectionReason) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public record SlotInfo(int slotId, String itemId, int count, int maxCount, boolean isProtected) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public boolean isEmpty() {
         if (this.count > 0 && this.itemId != null && !this.itemId.isEmpty()) {
            byte[] var10001 = new byte[13];
            var10001[0] = (byte)(87);
            var10001[1] = (byte)(-13);
            var10001[2] = (byte)(114);
            var10001[3] = (byte)(-69);
            var10001[4] = (byte)(-28);
            var10001[5] = (byte)(-65);
            var10001[6] = (byte)(-43);
            var10001[7] = (byte)(-88);
            var10001[8] = (byte)(38);
            var10001[9] = (byte)(-104);
            var10001[10] = (byte)(-123);
            var10001[11] = (byte)(-97);
            var10001[12] = (byte)(-83);
            if (!this.itemId.equals($$v_s(var10001, -1821966365990072289L))) {
               return false;
            }
         }

         return true;
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
