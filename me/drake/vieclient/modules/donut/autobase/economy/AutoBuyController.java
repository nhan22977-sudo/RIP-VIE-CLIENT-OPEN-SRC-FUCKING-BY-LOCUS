package me.drake.vieclient.modules.donut.autobase.economy;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import me.drake.vieclient.VieClientMod;
import net.minecraft.class_1707;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_476;
import net.minecraft.class_7923;

public class AutoBuyController {
   private static final Pattern PRICE_PATTERN;
   private boolean enabled = true;
   private int maxOverbuy = 0;
   private int reserveInventorySlots = 2;
   private double maxUnitPrice = 50000.0;
   private long maxSpendPerJob = 10000000L;
   private long totalSpent = 0L;
   private AutoBuyController.State currentState = AutoBuyController.State.IDLE;
   private String currentMaterialNeeded = null;
   private int currentQuantityNeeded = 0;
   private int initialInventoryCount = 0;
   private AHListing selectedListing = null;
   private long stateTimestamp = 0L;
   private int searchAttempts = 0;
   private static final int MAX_SEARCH_ATTEMPTS = 3;
   private static final long STATE_TIMEOUT_MS = 5000L;
   private String blockingReason = "";
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void configure(boolean enabled, int maxOverbuy, int reserveSlots, double maxUnitPrice, long maxSpend) {
      this.enabled = enabledx;
      this.maxOverbuy = Math.max(0, maxOverbuyx);
      this.reserveInventorySlots = Math.max(0, reserveSlots);
      this.maxUnitPrice = maxUnitPricex > 0.0 ? maxUnitPricex : 50000.0;
      this.maxSpendPerJob = maxSpend > 0L ? maxSpend : 10000000L;
   }

   public boolean requestPurchase(String cleanItemId, int quantityNeeded) {
      if (!this.enabled) {
         byte[] var10001 = new byte[19];
         var10001[0] = (byte)(112);
         var10001[1] = (byte)(-17);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-79);
         var10001[4] = (byte)(-12);
         var10001[5] = (byte)(-104);
         var10001[6] = (byte)(46);
         var10001[7] = (byte)(78);
         var10001[8] = (byte)(48);
         var10001[9] = (byte)(-47);
         var10001[10] = (byte)(-60);
         var10001[11] = (byte)(-110);
         var10001[12] = (byte)(-121);
         var10001[13] = (byte)(70);
         var10001[14] = (byte)(30);
         var10001[15] = (byte)(116);
         var10001[16] = (byte)(13);
         var10001[17] = (byte)(47);
         var10001[18] = (byte)(-88);
         this.setBlocked($$v_s(var10001, 5067731621465280532L));
         return false;
      }

      if (this.currentState != AutoBuyController.State.IDLE) {
         return false;
      }

      this.currentMaterialNeeded = cleanItemId;
      this.currentQuantityNeeded = quantityNeeded;
      this.searchAttempts = 0;
      this.selectedListing = null;
      this.blockingReason = "";
      this.transitionTo(AutoBuyController.State.NEED_MATERIAL);
      return true;
   }

   public void tick(class_310 mc) {
      if (this.currentState != AutoBuyController.State.IDLE && this.currentState != AutoBuyController.State.BLOCKED) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.stateTimestamp <= 5000L
            || this.currentState != AutoBuyController.State.WAIT_RESULTS
               && this.currentState != AutoBuyController.State.CONFIRM_PURCHASE
               && this.currentState != AutoBuyController.State.WAIT_INVENTORY_SYNC) {
            switch (this.currentState) {
               case NEED_MATERIAL:
                  this.transitionTo(AutoBuyController.State.PAUSE_WORK);
                  break;
               case PAUSE_WORK:
                  this.transitionTo(AutoBuyController.State.CHECK_CAPACITY_AND_BUDGET);
                  break;
               case CHECK_CAPACITY_AND_BUDGET:
                  if (mc.field_1724 == null) {
                     this.setBlocked("Player is null");
                     return;
                  }

                  if (this.totalSpent >= this.maxSpendPerJob) {
                     this.setBlocked("Exceeded maximum spend budget of $" + this.maxSpendPerJob);
                     return;
                  }

                  List var21 = InventoryCapacitySimulator.extractSlots(mc.field_1724.method_31548());
                  InventoryCapacitySimulator.SimulationResult var31 = InventoryCapacitySimulator.simulateInsertion(
                     var21, this.currentMaterialNeeded, 1, 64, this.reserveInventorySlots
                  );
                  if (!var31.canFit()) {
                     this.setBlocked("Inventory full: " + var31.rejectionReason());
                     return;
                  }

                  this.initialInventoryCount = InventoryCapacitySimulator.countAvailableItems(mc.field_1724.method_31548(), this.currentMaterialNeeded);
                  this.transitionTo(AutoBuyController.State.OPEN_AH_SEARCH);
                  break;
               case OPEN_AH_SEARCH:
                  if (mc.field_1724 == null || mc.field_1724.field_3944 == null) {
                     this.setBlocked(
                        "Network handler unavailable"
                     );
                     return;
                  }

                  this.searchAttempts++;
                  if (this.searchAttempts > 3) {
                     this.setBlocked(
                        "Exceeded search attempts (3) without viable listing"
                     );
                     this.closeScreen(mc);
                     return;
                  }

                  String var20 = this.mapItemToAhKeyword(this.currentMaterialNeeded);
                  String var30 = "ah " + var20;
                  mc.field_1724.field_3944.method_45730(var30);
                  this.transitionTo(AutoBuyController.State.WAIT_RESULTS);
                  break;
               case WAIT_RESULTS:
                  if (mc.field_1755 instanceof class_476 var19) {
                     this.transitionTo(AutoBuyController.State.SELECT_LISTING);
                  }
                  break;
               case SELECT_LISTING:
                  if (!(mc.field_1755 instanceof class_476 var18)) {
                     this.transitionTo(AutoBuyController.State.OPEN_AH_SEARCH);
                     return;
                  }

                  List var28 = this.parseAhScreenListings(var18, this.currentMaterialNeeded);
                  if (var28.isEmpty()) {
                     this.setBlocked("No matching listings found for " + this.currentMaterialNeeded);
                     this.closeScreen(mc);
                     return;
                  }

                  AHListing var32 = null;
                  double var7 = Double.MAX_VALUE;

                  for (AHListing var10 : var28) {
                     int var11 = var10.amount() - this.currentQuantityNeeded;
                     if (var11 <= this.maxOverbuy && !(var10.unitPrice() > this.maxUnitPrice) && this.totalSpent + var10.totalPrice() <= this.maxSpendPerJob) {
                        List var12 = InventoryCapacitySimulator.extractSlots(mc.field_1724.method_31548());
                        InventoryCapacitySimulator.SimulationResult var13 = InventoryCapacitySimulator.simulateInsertion(
                           var12, this.currentMaterialNeeded, var10.amount(), 64, this.reserveInventorySlots
                        );
                        if (var13.canFit() && var10.unitPrice() < var7) {
                           var7 = var10.unitPrice();
                           var32 = var10;
                        }
                     }
                  }

                  if (var32 == null) {
                     this.setBlocked("Listings found but rejected by overbuy/budget/capacity filters (Max Overbuy: " + this.maxOverbuy + ")");
                     this.closeScreen(mc);
                     return;
                  }

                  this.selectedListing = var32;
                  this.transitionTo(AutoBuyController.State.REVALIDATE);
                  break;
               case REVALIDATE:
                  if (mc.field_1755 instanceof class_476 var17 && mc.field_1761 != null && mc.field_1724 != null) {
                     int var26 = ((class_1707)var17.method_17577()).field_7763;
                     mc.field_1761.method_2906(var26, this.selectedListing.slotId(), 0, class_1713.field_7790, mc.field_1724);
                     this.transitionTo(AutoBuyController.State.CONFIRM_PURCHASE);
                     break;
                  }

                  this.transitionTo(AutoBuyController.State.SELECT_LISTING);
                  break;
               case CONFIRM_PURCHASE:
                  if (mc.field_1724 != null) {
                     int var15 = InventoryCapacitySimulator.countAvailableItems(mc.field_1724.method_31548(), this.currentMaterialNeeded);
                     if (var15 > this.initialInventoryCount) {
                        this.transitionTo(AutoBuyController.State.WAIT_INVENTORY_SYNC);
                        return;
                     }
                  }

                  if (mc.field_1755 instanceof class_476 var16 && mc.field_1761 != null && mc.field_1724 != null) {
                     int var24 = this.findConfirmSlot(var16);
                     if (var24 != -1) {
                        int var6 = ((class_1707)var16.method_17577()).field_7763;
                        mc.field_1761.method_2906(var6, var24, 0, class_1713.field_7790, mc.field_1724);
                        this.transitionTo(AutoBuyController.State.WAIT_INVENTORY_SYNC);
                        return;
                     }
                  }

                  if (System.currentTimeMillis() - this.stateTimestamp > 1200L) {
                     this.transitionTo(AutoBuyController.State.WAIT_INVENTORY_SYNC);
                  }
                  break;
               case WAIT_INVENTORY_SYNC:
                  if (mc.field_1724 != null) {
                     int var14 = InventoryCapacitySimulator.countAvailableItems(mc.field_1724.method_31548(), this.currentMaterialNeeded);
                     if (var14 > this.initialInventoryCount) {
                        int var22 = var14 - this.initialInventoryCount;
                        if (this.selectedListing != null) {
                           this.totalSpent = this.totalSpent + this.selectedListing.totalPrice();
                        }

                        this.closeScreen(mc);
                        this.transitionTo(AutoBuyController.State.RECOUNT);
                     }
                  }
                  break;
               case RECOUNT:
                  if (mc.field_1724 != null) {
                     int var4 = InventoryCapacitySimulator.countAvailableItems(mc.field_1724.method_31548(), this.currentMaterialNeeded);
                     int var5 = Math.max(0, this.currentQuantityNeeded - (var4 - this.initialInventoryCount));
                     if (var5 > 0) {
                        this.currentQuantityNeeded = var5;
                        this.initialInventoryCount = var4;
                        this.transitionTo(AutoBuyController.State.CHECK_CAPACITY_AND_BUDGET);
                     } else {
                        this.transitionTo(AutoBuyController.State.RESUME_OR_NEXT_MATERIAL);
                     }
                  }
                  break;
               case RESUME_OR_NEXT_MATERIAL:
                  this.closeScreen(mc);
                  this.resetTransaction();
                  this.currentState = AutoBuyController.State.IDLE;
            }
         } else {
            this.setBlocked("Operation timed out in state " + this.currentState);
            this.closeScreen(mc);
         }
      }
   }

   private void transitionTo(AutoBuyController.State newState) {
      this.currentState = newState;
      this.stateTimestamp = System.currentTimeMillis();
   }

   private void setBlocked(String reason) {
      this.currentState = AutoBuyController.State.BLOCKED;
      String var10001;
      if (reason != null) {
         var10001 = reason;
      } else {
         byte[] var2 = new byte[21];
         var2[0] = (byte)(100);
         var2[1] = (byte)(-12);
         var2[2] = (byte)(119);
         var2[3] = (byte)(-80);
         var2[4] = (byte)(-39);
         var2[5] = (byte)(-102);
         var2[6] = (byte)(57);
         var2[7] = (byte)(78);
         var2[8] = (byte)(24);
         var2[9] = (byte)(-41);
         var2[10] = (byte)(-112);
         var2[11] = (byte)(-103);
         var2[12] = (byte)(-84);
         var2[13] = (byte)(64);
         var2[14] = (byte)(6);
         var2[15] = (byte)(54);
         var2[16] = (byte)(4);
         var2[17] = (byte)(56);
         var2[18] = (byte)(-66);
         var2[19] = (byte)(-63);
         var2[20] = (byte)(116);
         var10001 = $$v_s(var2, 5067731621465280532L);
      }

      this.blockingReason = var10001;
      this.stateTimestamp = System.currentTimeMillis();
      byte[] var3 = new byte[30];
      var3[0] = (byte)(106);
      var3[1] = (byte)(-37);
      var3[2] = (byte)(105);
      var3[3] = (byte)(-86);
      var3[4] = (byte)(-39);
      var3[5] = (byte)(-81);
      var3[6] = (byte)(34);
      var3[7] = (byte)(23);
      var3[8] = (byte)(4);
      var3[9] = (byte)(-126);
      var3[10] = (byte)(-76);
      var3[11] = (byte)(-125);
      var3[12] = (byte)(-100);
      var3[13] = (byte)(86);
      var3[14] = (byte)(23);
      var3[15] = (byte)(119);
      var3[16] = (byte)(18);
      var3[17] = (byte)(47);
      var3[18] = (byte)(-20);
      var3[19] = (byte)(-52);
      var3[20] = (byte)(106);
      var3[21] = (byte)(114);
      var3[22] = (byte)(100);
      var3[23] = (byte)(85);
      var3[24] = (byte)(-20);
      var3[25] = (byte)(118);
      var3[26] = (byte)(-82);
      var3[27] = (byte)(102);
      var3[28] = (byte)(69);
      var3[29] = (byte)(24);
      VieClientMod.LOGGER.warn($$v_s(var3, 5067731621465280532L), this.blockingReason);
   }

   private void closeScreen(class_310 mc) {
      if (mc.field_1724 != null) {
         mc.field_1724.method_7346();
      }
   }

   public void resetTransaction() {
      this.currentMaterialNeeded = null;
      this.currentQuantityNeeded = 0;
      this.selectedListing = null;
      this.searchAttempts = 0;
      this.blockingReason = "";
   }

   public void cancelAndReset(class_310 mc) {
      this.closeScreen(mc);
      this.resetTransaction();
      this.currentState = AutoBuyController.State.IDLE;
   }

   public String mapItemToAhKeyword(String cleanItemId) {
      if (cleanItemId == null) {
         return "";
      }

      String var2 = cleanItemId.contains(":") ? cleanItemId.substring(cleanItemId.indexOf(58) + 1) : cleanItemId;
      return var2.replace((char)(95), (char)(32));
   }

   private List parseAhScreenListings(class_476 screen, String targetItemId) {
      ArrayList var3 = new ArrayList();
      if (screen != null && screen.method_17577() != null) {
         int var4 = ((class_1707)screen.method_17577()).field_7761.size();
         int var5 = var4 - (36);

         for (int var6 = 0; var6 < var5; var6++) {
            class_1799 var7 = ((class_1707)screen.method_17577()).method_7611(var6).method_7677();
            if (!var7.method_7960()) {
               String var8 = class_7923.field_41178.method_10221(var7.method_7909()).toString();
               if (var8.equalsIgnoreCase(targetItemId)) {
                  int var9 = var7.method_7947();
                  long var10 = 0L;
                  if (var10 <= 0L) {
                     var10 = 1000L;
                  }

                  double var12 = (double)var10 / Math.max(1, var9);
                  var3.add(new AHListing(var6, var8, var8, var9, var10, var12, "", Collections.emptyList()));
               }
            }
         }

         return var3;
      } else {
         return var3;
      }
   }

   private int findConfirmSlot(class_476 screen) {
      if (screen != null && screen.method_17577() != null) {
         int var2 = ((class_1707)screen.method_17577()).field_7761.size() - (36);
         int var3 = 0;

         while (true) {
            if (var3 >= var2) {
               return -1;
            }

            class_1799 var4 = ((class_1707)screen.method_17577()).method_7611(var3).method_7677();
            if (!var4.method_7960()) {
               String var5 = class_7923.field_41178.method_10221(var4.method_7909()).toString();
               byte[] var10001 = new byte[10];
               var10001[0] = (byte)(86);
               var10001[1] = (byte)(-24);
               var10001[2] = (byte)(121);
               var10001[3] = (byte)(-69);
               var10001[4] = (byte)(-40);
               var10001[5] = (byte)(-78);
               var10001[6] = (byte)(32);
               var10001[7] = 1;
               var10001[8] = (byte)(54);
               var10001[9] = (byte)(-50);
               if (var5.contains($$v_s(var10001, 5067731621465280532L))) {
                  break;
               }

               var10001 = new byte[7];
               var10001[0] = (byte)(84);
               var10001[1] = (byte)(-9);
               var10001[2] = (byte)(121);
               var10001[3] = (byte)(-84);
               var10001[4] = (byte)(-41);
               var10001[5] = (byte)(-127);
               var10001[6] = (byte)(51);
               if (var5.contains($$v_s(var10001, 5067731621465280532L))) {
                  break;
               }

               var10001 = new byte[15];
               var10001[0] = (byte)(93);
               var10001[1] = (byte)(-13);
               var10001[2] = (byte)(113);
               var10001[3] = (byte)(-69);
               var10001[4] = (byte)(-23);
               var10001[5] = (byte)(-103);
               var10001[6] = (byte)(50);
               var10001[7] = (byte)(28);
               var10001[8] = (byte)(43);
               var10001[9] = (byte)(-61);
               var10001[10] = (byte)(-121);
               var10001[11] = (byte)(-103);
               var10001[12] = (byte)(-102);
               var10001[13] = (byte)(65);
               var10001[14] = (byte)(30);
               if (var5.contains($$v_s(var10001, 5067731621465280532L))) {
                  break;
               }
            }

            var3++;
         }

         return var3;
      } else {
         return -1;
      }
   }

   public AutoBuyController.State getCurrentState() {
      return this.currentState;
   }

   public String getBlockingReason() {
      return this.blockingReason;
   }

   public String getCurrentMaterialNeeded() {
      return this.currentMaterialNeeded;
   }

   public int getCurrentQuantityNeeded() {
      return this.currentQuantityNeeded;
   }

   public long getTotalSpent() {
      return this.totalSpent;
   }

   public int getMaxOverbuy() {
      return this.maxOverbuy;
   }

   public int getReserveInventorySlots() {
      return this.reserveInventorySlots;
   }

   public boolean isPendingTransaction() {
      return this.currentState != AutoBuyController.State.IDLE && this.currentState != AutoBuyController.State.BLOCKED;
   }

   static {
      byte[] var10000 = new byte[18];
      var10000[0] = (byte)(109);
      var10000[1] = (byte)(-66);
      var10000[2] = (byte)(52);
      var10000[3] = (byte)(-126);
      var10000[4] = (byte)(-46);
      var10000[5] = (byte)(-58);
      var10000[6] = (byte)(127);
      var10000[7] = (byte)(81);
      var10000[8] = (byte)(99);
      var10000[9] = (byte)(-114);
      var10000[10] = (byte)(-72);
      var10000[11] = (byte)(-110);
      var10000[12] = (byte)(-107);
      var10000[13] = (byte)(6);
      var10000[14] = (byte)(2);
      var10000[15] = (byte)(63);
      var10000[16] = (byte)(75);
      var10000[17] = (byte)(99);
      PRICE_PATTERN = Pattern.compile($$v_s(var10000, 5067731621465280532L));
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

   public enum State {
      IDLE,
      NEED_MATERIAL,
      PAUSE_WORK,
      CHECK_CAPACITY_AND_BUDGET,
      OPEN_AH_SEARCH,
      WAIT_RESULTS,
      SELECT_LISTING,
      REVALIDATE,
      CONFIRM_PURCHASE,
      WAIT_INVENTORY_SYNC,
      RECOUNT,
      RESUME_OR_NEXT_MATERIAL,
      BLOCKED;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoBuyController.State[] $values() {
         AutoBuyController.State[] var10000 = new AutoBuyController.State[13];
         var10000[0] = IDLE;
         var10000[1] = NEED_MATERIAL;
         var10000[2] = PAUSE_WORK;
         var10000[3] = CHECK_CAPACITY_AND_BUDGET;
         var10000[4] = OPEN_AH_SEARCH;
         var10000[5] = WAIT_RESULTS;
         var10000[6] = SELECT_LISTING;
         var10000[7] = REVALIDATE;
         var10000[8] = CONFIRM_PURCHASE;
         var10000[9] = WAIT_INVENTORY_SYNC;
         var10000[10] = RECOUNT;
         var10000[11] = RESUME_OR_NEXT_MATERIAL;
         var10000[12] = BLOCKED;
         return var10000;
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
