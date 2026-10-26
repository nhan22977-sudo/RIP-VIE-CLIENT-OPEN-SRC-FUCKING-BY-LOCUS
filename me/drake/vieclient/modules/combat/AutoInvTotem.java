package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.ScreenChangeEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.FakeInvScreen;
import net.minecraft.class_1297;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2663;
import net.minecraft.class_2848;
import net.minecraft.class_490;
import net.minecraft.class_2848.class_2849;

public class AutoInvTotem extends Module {
   private final IntSetting delay = (IntSetting)this.addSetting(
      new IntSetting(
         "Delay",
         "Base ticks before moving totem (humanized +0-5)",
         3,
         1,
         20
      )
   );
   private final EnumSetting targetSlot = (EnumSetting)this.addSetting(
      new EnumSetting(
            "TargetSlot",
            "Where to put the totem",
            AutoInvTotem.TargetSlot.OFFHAND,
            AutoInvTotem.TargetSlot.values()
         )
         .withDisplayName(AutoInvTotem.TargetSlot.OFFHAND, "Offhand")
         .withDisplayName(
            AutoInvTotem.TargetSlot.MAINHAND, "Mainhand Slot"
         )
         .withDisplayName(AutoInvTotem.TargetSlot.BOTH_HANDS, "Both Hands")
   );
   private final IntSetting mainhandSlot = (IntSetting)this.addSetting(
      new IntSetting(
         "MainhandSlot",
         "Hotbar slot 1-9 to use when TargetSlot=Mainhand",
         1,
         1,
         9
      )
   );
   private final BoolSetting autoOpen = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AutoOpen",
         "Automatically open inventory when totem is lost",
         false
      )
   );
   private final IntSetting openDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "OpenDelay",
         "Ticks before auto-opening inventory",
         8,
         1,
         30
      )
   );
   private final BoolSetting autoClose = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AutoClose",
         "Close inventory after refill",
         true
      )
   );
   private final IntSetting closeDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "CloseDelay",
         "Ticks before auto-closing inventory",
         12,
         1,
         30
      )
   );
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         true
      )
   );
   private final BoolSetting debug = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Debug",
         "Log totem refill events to chat",
         false
      )
   );
   private final Random random = new Random();
   private boolean hadTotemInOffhand = false;
   private boolean hadTotemInMainhand = false;
   private boolean needsTotem = false;
   private int delayClock = 0;
   private int giveUpTicks = 0;
   private int openClock = 0;
   private int closeClock = 0;
   private static final int GIVE_UP_TICKS = 600;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoInvTotem() {
      super(
         "AutoInvTotem",
         "Auto-refill totem to offhand or hotbar slots after pop",
         Category.COMBAT
      );
      this.debug.setVisibleInGui(false);
      this.randomDelay.setVisibleInGui(false);
      this.mainhandSlot.visibleWhen(() -> this.targetSlot.get() != AutoInvTotem.TargetSlot.OFFHAND);
      this.openDelay.visibleWhen(this.autoOpen::get);
      this.closeDelay.visibleWhen(this.autoClose::get);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetState();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.resetState();
      if ((Boolean)this.autoClose.get()) {
         this.closeInventory();
      }
   }

   private void resetState() {
      if (mc.field_1724 != null) {
         this.hadTotemInOffhand = this.hasTotemInOffhand();
         this.hadTotemInMainhand = this.hasTotemInMainhand();
      }

      this.needsTotem = false;
      this.delayClock = 0;
      this.giveUpTicks = 0;
      this.openClock = 0;
      this.closeClock = 0;
   }

   @EventHandler
   public void onPacketReceive(ReceivePacketEvent event) {
      if (event.getPacket() instanceof class_2663 var2) {
         if (var2.method_11470() == (35)) {
            if (mc.field_1724 != null && mc.field_1687 != null) {
               class_1297 var5 = var2.method_11469(mc.field_1687);
               if (var5 == mc.field_1724) {
                  this.giveUpTicks = 600;
                  AutoInvTotem.TargetSlot var4 = (AutoInvTotem.TargetSlot)this.targetSlot.get();
                  if (tracksOffhand(var4)) {
                     this.hadTotemInOffhand = true;
                  }

                  if (var4 == AutoInvTotem.TargetSlot.MAINHAND) {
                     this.hadTotemInMainhand = true;
                  }

                  byte[] var10001 = new byte[28];
                  var10001[0] = (byte)(124);
                  var10001[1] = (byte)(-37);
                  var10001[2] = (byte)(105);
                  var10001[3] = (byte)(-86);
                  var10001[4] = (byte)(45);
                  var10001[5] = (byte)(-121);
                  var10001[6] = (byte)(-79);
                  var10001[7] = (byte)(49);
                  var10001[8] = (byte)(27);
                  var10001[9] = (byte)(-51);
                  var10001[10] = (byte)(-112);
                  var10001[11] = (byte)(-109);
                  var10001[12] = (byte)(119);
                  var10001[13] = (byte)(75);
                  var10001[14] = (byte)(-41);
                  var10001[15] = (byte)(107);
                  var10001[16] = (byte)(24);
                  var10001[17] = (byte)(62);
                  var10001[18] = (byte)(-87);
                  var10001[19] = (byte)(-61);
                  var10001[20] = (byte)(-46);
                  var10001[21] = (byte)(78);
                  var10001[22] = (byte)(-32);
                  var10001[23] = (byte)(103);
                  var10001[24] = (byte)(-17);
                  var10001[25] = (byte)(119);
                  var10001[26] = (byte)(-16);
                  var10001[27] = (byte)(103);
                  this.debugLog($$v_s(var10001, 8060336104558284802L));
               }
            }
         }
      }
   }

   @EventHandler
   public void onScreenChange(ScreenChangeEvent event) {
      if (event.getScreen() instanceof class_490) {
         if (this.needsTotem && mc.field_1724 != null) {
            if (this.delayClock == 0 && this.openClock == 0) {
               this.delayClock = this.humanizedDelay();
            }
         }
      }
   }

   @EventHandler
   public void onTickPre(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 != null && mc.field_1761 != null) {
            boolean var2 = this.hasTotemInOffhand();
            boolean var3 = this.hasTotemInMainhand();
            AutoInvTotem.TargetSlot var4 = (AutoInvTotem.TargetSlot)this.targetSlot.get();
            boolean var5 = tracksOffhand(var4);
            boolean var6 = tracksMainhand(var4);
            boolean var7 = var5 && this.hadTotemInOffhand && !var2;
            boolean var8 = var6 && this.hadTotemInMainhand && !var3;
            if (var7 || var8) {
               this.needsTotem = true;
               this.giveUpTicks = 600;
               this.openClock = this.autoOpen.get() ? (Integer)this.openDelay.get() : Integer.MAX_VALUE;
               this.debugLog("[AutoInvTotem] Totem missing (offhand=" + !var2 + ", mainhand=" + !var3 + "), needs refill");
            }

            this.hadTotemInOffhand = var2;
            this.hadTotemInMainhand = var3;
            boolean var9 = (!var5 || var2) && (!var6 || var3);
            if (var9 && this.needsTotem) {
               if (!(Boolean)this.autoClose.get()) {
                  this.finishRefill();
               } else if (!(mc.field_1755 instanceof class_490)) {
                  this.finishRefill();
               } else {
                  if (this.closeClock == 0) {
                     this.closeClock = (Integer)this.closeDelay.get();
                  }

                  if (mc.field_1755 instanceof class_490 && this.closeClock > 0) {
                     this.closeClock--;
                     if (this.closeClock == 0) {
                        this.closeInventory();
                        this.finishRefill();
                     }
                  }
               }
            } else {
               if (this.needsTotem && this.giveUpTicks > 0) {
                  this.giveUpTicks--;
                  if (this.giveUpTicks == 0) {
                     this.needsTotem = false;
                     this.delayClock = 0;
                     this.openClock = 0;
                     this.closeClock = 0;
                     this.debugLog(
                        "[AutoInvTotem] Give-up — no inventory opened"
                     );
                     return;
                  }
               }

               if (this.needsTotem && this.openClock > 0 && !(mc.field_1755 instanceof class_490)) {
                  this.openClock--;
                  if (this.openClock == 0) {
                     this.openInventory();
                     this.delayClock = this.humanizedDelay();
                  }
               }

               if (this.needsTotem && mc.field_1755 instanceof class_490) {
                  if (this.delayClock > 0) {
                     this.delayClock--;
                     return;
                  }

                  boolean var10 = false;
                  if (var5 && !var2) {
                     var10 = this.moveTotemToOffhand();
                  } else if (var6 && !var3) {
                     var10 = this.moveTotemToMainhand();
                  }

                  if (!var10) {
                     return;
                  }

                  this.delayClock = this.humanizedDelay();
               }
            }
         }
      }
   }

   private int humanizedDelay() {
      return (Integer)this.delay.get() + (this.randomDelay.get() ? this.random.nextInt(6) : 0);
   }

   private void openInventory() {
      if (mc.field_1724 != null && mc.field_1724.field_3944 != null) {
         if (mc.field_1755 == null) {
            mc.field_1724.field_3944.method_52787(new class_2848(mc.field_1724, class_2849.field_12988));
            mc.method_1507(new FakeInvScreen(mc.field_1724));
            byte[] var10001 = new byte[40];
            var10001[0] = (byte)(124);
            var10001[1] = (byte)(-37);
            var10001[2] = (byte)(105);
            var10001[3] = (byte)(-86);
            var10001[4] = (byte)(45);
            var10001[5] = (byte)(-121);
            var10001[6] = (byte)(-79);
            var10001[7] = (byte)(49);
            var10001[8] = (byte)(27);
            var10001[9] = (byte)(-51);
            var10001[10] = (byte)(-112);
            var10001[11] = (byte)(-109);
            var10001[12] = (byte)(119);
            var10001[13] = (byte)(75);
            var10001[14] = (byte)(-41);
            var10001[15] = (byte)(112);
            var10001[16] = (byte)(7);
            var10001[17] = (byte)(47);
            var10001[18] = (byte)(-94);
            var10001[19] = (byte)(-53);
            var10001[20] = (byte)(-106);
            var10001[21] = (byte)(30);
            var10001[22] = (byte)(-26);
            var10001[23] = (byte)(121);
            var10001[24] = (byte)(-23);
            var10001[25] = (byte)(119);
            var10001[26] = (byte)(-6);
            var10001[27] = (byte)(50);
            var10001[28] = (byte)(-91);
            var10001[29] = (byte)(52);
            var10001[30] = (byte)(-34);
            var10001[31] = (byte)(-17);
            var10001[32] = (byte)(-17);
            var10001[33] = (byte)(73);
            var10001[34] = (byte)(25);
            var10001[35] = (byte)(12);
            var10001[36] = (byte)(-44);
            var10001[37] = (byte)(11);
            var10001[38] = (byte)(13);
            var10001[39] = (byte)(-114);
            this.debugLog($$v_s(var10001, 8060336104558284802L));
         }
      }
   }

   private void closeInventory() {
      if (mc.field_1755 instanceof class_490) {
         mc.method_1507(null);
      }
   }

   private int currentSyncId() {
      if (mc.field_1724 == null) {
         return -1;
      } else if (mc.field_1724.field_7512 instanceof class_1723 var1) {
         return mc.field_1755 instanceof class_490 ? var1.field_7763 : -1;
      } else {
         return -1;
      }
   }

   private void safeClick(int slot, int button, class_1713 action) {
      if (mc.field_1724 != null && mc.field_1761 != null) {
         int var4 = this.currentSyncId();
         if (var4 >= 0) {
            try {
               mc.field_1761.method_2906(var4, slot, button, action, mc.field_1724);
            } catch (Throwable var6) {
            }
         }
      }
   }

   private boolean moveTotemToOffhand() {
      int var1 = this.findTotemSlot();
      if (var1 < 0) {
         byte[] var10001 = new byte[43];
         var10001[0] = (byte)(124);
         var10001[1] = (byte)(-37);
         var10001[2] = (byte)(105);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(45);
         var10001[5] = (byte)(-121);
         var10001[6] = (byte)(-79);
         var10001[7] = (byte)(49);
         var10001[8] = (byte)(27);
         var10001[9] = (byte)(-51);
         var10001[10] = (byte)(-112);
         var10001[11] = (byte)(-109);
         var10001[12] = (byte)(119);
         var10001[13] = (byte)(75);
         var10001[14] = (byte)(-41);
         var10001[15] = (byte)(113);
         var10001[16] = (byte)(24);
         var10001[17] = (byte)(106);
         var10001[18] = (byte)(-72);
         var10001[19] = (byte)(-63);
         var10001[20] = (byte)(-122);
         var10001[21] = (byte)(91);
         var10001[22] = (byte)(-30);
         var10001[23] = (byte)(55);
         var10001[24] = (byte)(-7);
         var10001[25] = (byte)(125);
         var10001[26] = (byte)(-31);
         var10001[27] = (byte)(40);
         var10001[28] = (byte)(-82);
         var10001[29] = (byte)(102);
         var10001[30] = (byte)(-50);
         var10001[31] = (byte)(-95);
         var10001[32] = (byte)(-25);
         var10001[33] = (byte)(83);
         var10001[34] = (byte)(18);
         var10001[35] = (byte)(8);
         var10001[36] = (byte)(-57);
         var10001[37] = 0;
         var10001[38] = (byte)(11);
         var10001[39] = (byte)(-56);
         var10001[40] = (byte)(-99);
         var10001[41] = (byte)(-69);
         var10001[42] = (byte)(101);
         this.debugLog($$v_s(var10001, 8060336104558284802L));
         return false;
      }

      int var2 = var1 < (9) ? var1 + (36) : var1;
      class_1799 var3 = mc.field_1724.method_6079();
      if (var3.method_7960()) {
         this.safeClick(var2, 40, class_1713.field_7791);
      } else {
         this.safeClick(var2, 0, class_1713.field_7790);
         this.safeClick(45, 0, class_1713.field_7790);
         this.safeClick(var2, 0, class_1713.field_7790);
      }

      return true;
   }

   private boolean moveTotemToMainhand() {
      int var1 = this.findTotemSlot();
      if (var1 < 0) {
         return false;
      }

      int var2 = (Integer)this.mainhandSlot.get() - 1;
      if (var2 >= 0 && var2 <= (8)) {
         int var3 = var1 < (9) ? var1 + (36) : var1;
         int var4 = var2 < (9) ? var2 + (36) : var2;
         class_1799 var5 = mc.field_1724.method_31548().method_5438(var2);
         if (var5.method_7960()) {
            this.safeClick(var3, 0, class_1713.field_7790);
            this.safeClick(var4, 0, class_1713.field_7790);
            if (var1 < (9)) {
               this.safeClick(var3, 0, class_1713.field_7790);
            }
         } else {
            this.safeClick(var3, 0, class_1713.field_7790);
            this.safeClick(var4, 0, class_1713.field_7790);
            this.safeClick(var3, 0, class_1713.field_7790);
         }

         return true;
      } else {
         return false;
      }
   }

   private int findTotemSlot() {
      int var1 = this.targetSlot.get() == AutoInvTotem.TargetSlot.BOTH_HANDS && this.hasTotemInMainhand()
         ? (Integer)this.mainhandSlot.get() - 1
         : -1;
      return this.findNextTotemSlot(0, var1);
   }

   private int findNextTotemSlot(int fromSlot, int excludedHotbarSlot) {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var3 = fromSlot; var3 < (36); var3++) {
         if (var3 != excludedHotbarSlot && mc.field_1724.method_31548().method_5438(var3).method_31574(class_1802.field_8288)) {
            return var3;
         }
      }

      if (fromSlot <= (8)) {
         for (int var4 = 0; var4 < (9); var4++) {
            if (var4 != excludedHotbarSlot && mc.field_1724.method_31548().method_5438(var4).method_31574(class_1802.field_8288)) {
               return var4;
            }
         }
      }

      return -1;
   }

   private static boolean tracksOffhand(AutoInvTotem.TargetSlot target) {
      return target == AutoInvTotem.TargetSlot.OFFHAND || target == AutoInvTotem.TargetSlot.BOTH_HANDS;
   }

   private static boolean tracksMainhand(AutoInvTotem.TargetSlot target) {
      return target == AutoInvTotem.TargetSlot.MAINHAND || target == AutoInvTotem.TargetSlot.BOTH_HANDS;
   }

   private void finishRefill() {
      this.needsTotem = false;
      this.delayClock = 0;
      this.giveUpTicks = 0;
      this.openClock = 0;
      this.closeClock = 0;
      byte[] var10001 = new byte[30];
      var10001[0] = (byte)(124);
      var10001[1] = (byte)(-37);
      var10001[2] = (byte)(105);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(45);
      var10001[5] = (byte)(-121);
      var10001[6] = (byte)(-79);
      var10001[7] = (byte)(49);
      var10001[8] = (byte)(27);
      var10001[9] = (byte)(-51);
      var10001[10] = (byte)(-112);
      var10001[11] = (byte)(-109);
      var10001[12] = (byte)(119);
      var10001[13] = (byte)(75);
      var10001[14] = (byte)(-41);
      var10001[15] = (byte)(107);
      var10001[16] = (byte)(24);
      var10001[17] = (byte)(62);
      var10001[18] = (byte)(-87);
      var10001[19] = (byte)(-61);
      var10001[20] = (byte)(-46);
      var10001[21] = (byte)(76);
      var10001[22] = (byte)(-22);
      var10001[23] = (byte)(113);
      var10001[24] = (byte)(-10);
      var10001[25] = (byte)(126);
      var10001[26] = (byte)(-8);
      var10001[27] = (byte)(35);
      var10001[28] = (byte)(-82);
      var10001[29] = (byte)(103);
      this.debugLog($$v_s(var10001, 8060336104558284802L));
   }

   private boolean hasTotemInOffhand() {
      if (mc.field_1724 == null) {
         return false;
      }

      class_1799 var1 = mc.field_1724.method_6079();
      return !var1.method_7960() && var1.method_31574(class_1802.field_8288);
   }

   private boolean hasTotemInMainhand() {
      if (mc.field_1724 == null) {
         return false;
      } else {
         int var1 = (Integer)this.mainhandSlot.get() - 1;
         if (var1 >= 0 && var1 <= (8)) {
            class_1799 var2 = mc.field_1724.method_31548().method_5438(var1);
            return !var2.method_7960() && var2.method_31574(class_1802.field_8288);
         } else {
            return false;
         }
      }
   }

   private void debugLog(String msg) {
      if ((Boolean)this.debug.get() && mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470(msg), false);
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

   public enum TargetSlot {
      OFFHAND,
      MAINHAND,
      BOTH_HANDS;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoInvTotem.TargetSlot[] $values() {
         AutoInvTotem.TargetSlot[] var10000 = new AutoInvTotem.TargetSlot[3];
         var10000[0] = OFFHAND;
         var10000[1] = MAINHAND;
         var10000[2] = BOTH_HANDS;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-36);
         var10002[2] = (byte)(90);
         var10002[3] = (byte)(-106);
         var10002[4] = (byte)(-56);
         var10002[5] = (byte)(71);
         var10002[6] = (byte)(39);
         OFFHAND = new AutoInvTotem.TargetSlot();
         var10002 = new byte[8];
         var10002[0] = (byte)(117);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-63);
         var10002[5] = (byte)(72);
         var10002[6] = (byte)(45);
         var10002[7] = (byte)(45);
         MAINHAND = new AutoInvTotem.TargetSlot();
         var10002 = new byte[10];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-106);
         var10002[4] = (byte)(-42);
         var10002[5] = (byte)(65);
         var10002[6] = (byte)(34);
         var10002[7] = (byte)(39);
         var10002[8] = (byte)(20);
         var10002[9] = (byte)(-15);
         BOTH_HANDS = new AutoInvTotem.TargetSlot();
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
