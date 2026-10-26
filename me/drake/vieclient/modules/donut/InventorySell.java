package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.StringSetting;
import net.minecraft.class_124;
import net.minecraft.class_1304;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_465;
import net.minecraft.class_7923;

public class InventorySell extends Module {
   private final StringSetting sellCommand = (StringSetting)this.addSetting(
      new StringSetting(
         "Command",
         "Command to open sell GUI",
         "/sell"
      )
   );
   private final BoolSetting sellArmor = (BoolSetting)this.addSetting(
      new BoolSetting(
         "SellArmor",
         "Unequip and sell currently equipped armor",
         true
      )
   );
   private final BoolSetting sellOffhand = (BoolSetting)this.addSetting(
      new BoolSetting(
         "SellOffhand",
         "Unequip and sell held offhand item",
         true
      )
   );
   private final BoolSetting sellHotbar = (BoolSetting)this.addSetting(
      new BoolSetting(
         "SellHotbar",
         "Sell items in the hotbar",
         true
      )
   );
   private final BoolSetting autoConfirm = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AutoConfirm",
         "Automatically click sell/confirm button in GUI",
         true
      )
   );
   private final BoolSetting autoClose = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AutoClose",
         "Automatically close GUI when sold",
         true
      )
   );
   private final BoolSetting autoDisable = (BoolSetting)this.addSetting(
      new BoolSetting(
         "AutoDisable",
         "Automatically turn off module after selling",
         true
      )
   );
   private final IntSetting clickDelayTicks = (IntSetting)this.addSetting(
      new IntSetting(
         "ClickDelay",
         "Tick delay between moving item stacks (anti-kick)",
         1,
         0,
         10
      )
   );
   private static final String[] CONFIRM_KEYWORDS = new String[]{
      "sell",
      "confirm",
      "accept",
      "ok",
      "bán",
      "xác nhận",
      "dong y",
      "đồng ý"
   };
   private static final Set CONFIRM_ICON_ITEMS = Set.of(
      class_2960.method_60655(
         "minecraft",
         "lime_stained_glass_pane"
      ),
      class_2960.method_60655(
         "minecraft",
         "green_stained_glass_pane"
      ),
      class_2960.method_60655(
         "minecraft",
         "lime_dye"
      ),
      class_2960.method_60655(
         "minecraft",
         "green_dye"
      ),
      class_2960.method_60655(
         "minecraft",
         "lime_wool"
      ),
      class_2960.method_60655(
         "minecraft",
         "lime_concrete"
      ),
      class_2960.method_60655(
         "minecraft",
         "emerald"
      ),
      class_2960.method_60655(
         "minecraft",
         "emerald_block"
      ),
      class_2960.method_60655(
         "minecraft",
         "slime_ball"
      )
   );
   private InventorySell.State state = InventorySell.State.START;
   private int delay = 0;
   private int guiTimeout = 0;
   private int moveCursor = 0;
   private List pendingSlotsToMove = new ArrayList();
   private boolean hadLeftovers = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public InventorySell() {
      super(
         "InventorySell",
         "Auto /sell and dumps all inventory, armor & offhand to sell panel",
         Category.SMP_UTILITIES
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.reset();
   }

   private void reset() {
      this.state = InventorySell.State.START;
      this.delay = 0;
      this.guiTimeout = 0;
      this.moveCursor = 0;
      this.pendingSlotsToMove.clear();
      this.hadLeftovers = false;
   }

   @Override
   public void onTick() {
      if (this.isInGame() && mc.field_1724 != null && mc.field_1761 != null) {
         if (this.delay > 0) {
            this.delay--;
         } else {
            switch (this.state) {
               case START:
                  if (mc.field_1755 instanceof class_465) {
                     this.state = InventorySell.State.DUMPING_ITEMS;
                     return;
                  }

                  this.state = InventorySell.State.UNEQUIP_BEFORE_CMD;
                  break;
               case UNEQUIP_BEFORE_CMD:
                  this.unequipEquippedItems();
                  this.delay = 2;
                  this.state = InventorySell.State.SEND_COMMAND;
                  break;
               case SEND_COMMAND:
                  String var6 = ((String)this.sellCommand.get()).trim();
                  if (var6.startsWith("/")) {
                     var6 = var6.substring(1);
                  }

                  if (mc.field_1724.field_3944 != null) {
                     mc.field_1724.field_3944.method_45730(var6);
                  }

                  this.guiTimeout = 60;
                  this.state = InventorySell.State.WAIT_FOR_GUI;
                  break;
               case WAIT_FOR_GUI:
                  if (mc.field_1755 instanceof class_465) {
                     this.delay = 3;
                     this.state = InventorySell.State.DUMPING_ITEMS;
                     return;
                  }

                  this.guiTimeout--;
                  if (this.guiTimeout <= 0) {
                     this.message(class_124.field_1061 + "[InventorySell] " + class_124.field_1068 + "Timeout waiting for /sell GUI to open.");
                     this.state = InventorySell.State.FINISH;
                  }
                  break;
               case DUMPING_ITEMS:
                  if (!(mc.field_1755 instanceof class_465 var5)) {
                     this.state = InventorySell.State.FINISH;
                     return;
                  }

                  class_1703 var9 = var5.method_17577();
                  if (this.pendingSlotsToMove.isEmpty()) {
                     this.pendingSlotsToMove = this.scanPlayerSlotsToMove(var9);
                     this.moveCursor = 0;
                  }

                  if (this.moveCursor < this.pendingSlotsToMove.size()) {
                     int var10 = (Integer)this.pendingSlotsToMove.get(this.moveCursor++);
                     if (var10 >= 0 && var10 < var9.field_7761.size()) {
                        class_1735 var4 = var9.method_7611(var10);
                        if (!var4.method_7677().method_7960()) {
                           mc.field_1761.method_2906(var9.field_7763, var10, 0, class_1713.field_7794, mc.field_1724);
                           this.delay = (Integer)this.clickDelayTicks.get();
                        }
                     }

                     return;
                  }

                  this.pendingSlotsToMove.clear();
                  this.delay = 3;
                  this.state = InventorySell.State.CHECK_LEFTOVERS;
                  break;
               case CHECK_LEFTOVERS:
                  if (!this.hadLeftovers && this.hasEquippedItems()) {
                     this.hadLeftovers = true;
                     if (mc.field_1755 != null) {
                        mc.field_1724.method_7346();
                     }

                     this.delay = 4;
                     this.state = InventorySell.State.UNEQUIP_BEFORE_CMD;
                     return;
                  }

                  this.state = InventorySell.State.CONFIRM_SELL;
                  break;
               case CONFIRM_SELL:
                  if (!(mc.field_1755 instanceof class_465 var1)) {
                     this.state = InventorySell.State.FINISH;
                     return;
                  }

                  if ((Boolean)this.autoConfirm.get()) {
                     class_1703 var7 = var1.method_17577();
                     int var3 = this.findConfirmButtonSlot(var7);
                     if (var3 >= 0 && var3 < var7.field_7761.size()) {
                        mc.field_1761.method_2906(var7.field_7763, var3, 0, class_1713.field_7790, mc.field_1724);
                        this.delay = 3;
                     }
                  }

                  this.state = InventorySell.State.FINISH;
                  break;
               case FINISH:
                  if ((Boolean)this.autoClose.get() && mc.field_1755 != null) {
                     mc.field_1724.method_7346();
                  }

                  this.message(class_124.field_1060 + "[InventorySell] " + class_124.field_1068 + "Successfully dumped inventory & gear to sell panel!");
                  if ((Boolean)this.autoDisable.get()) {
                     this.setEnabled(false);
                  } else {
                     this.reset();
                  }
            }
         }
      }
   }

   private void unequipEquippedItems() {
      if (mc.field_1724 != null && mc.field_1761 != null) {
         class_1723 var1 = mc.field_1724.field_7498;
         if (var1 != null) {
            if ((Boolean)this.sellOffhand.get()) {
               class_1799 var2 = mc.field_1724.method_6079();
               if (!var2.method_7960()) {
                  mc.field_1761.method_2906(var1.field_7763, 45, 0, class_1713.field_7794, mc.field_1724);
               }
            }

            if ((Boolean)this.sellArmor.get()) {
               for (int var3 = 5; var3 <= (8); var3++) {
                  if (!var1.method_7611(var3).method_7677().method_7960()) {
                     mc.field_1761.method_2906(var1.field_7763, var3, 0, class_1713.field_7794, mc.field_1724);
                  }
               }
            }
         }
      }
   }

   private boolean hasEquippedItems() {
      if (mc.field_1724 == null) {
         return false;
      }

      if ((Boolean)this.sellOffhand.get() && !mc.field_1724.method_6079().method_7960()) {
         return true;
      }

      if ((Boolean)this.sellArmor.get()) {
         class_1304[] var10000 = new class_1304[4];
         var10000[0] = class_1304.field_6169;
         var10000[1] = class_1304.field_6174;
         var10000[2] = class_1304.field_6172;
         var10000[3] = class_1304.field_6166;
         class_1304[] var1 = var10000;

         for (class_1304 var5 : var1) {
            if (!mc.field_1724.method_6118(var5).method_7960()) {
               return true;
            }
         }
      }

      return false;
   }

   private List scanPlayerSlotsToMove(class_1703 handler) {
      ArrayList var2 = new ArrayList();
      if (mc.field_1724 == null) {
         return var2;
      }

      int var3 = handler.field_7761.size();

      for (int var4 = 0; var4 < var3; var4++) {
         class_1735 var5 = handler.method_7611(var4);
         if (var5.field_7871 == mc.field_1724.method_31548()) {
            class_1799 var6 = var5.method_7677();
            if (!var6.method_7960()) {
               int var7 = var5.method_34266();
               if ((Boolean)this.sellHotbar.get() || var7 >= (9)) {
                  var2.add(var4);
               }
            }
         }
      }

      return var2;
   }

   private int findConfirmButtonSlot(class_1703 handler) {
      if (mc.field_1724 == null) {
         return -1;
      }

      int var2 = handler.field_7761.size();

      for (int var3 = 0; var3 < var2; var3++) {
         class_1735 var4 = handler.method_7611(var3);
         if (var4.field_7871 != mc.field_1724.method_31548()) {
            class_1799 var5 = var4.method_7677();
            if (!var5.method_7960()) {
               String var6 = var5.method_7964().getString().toLowerCase(Locale.ROOT);

               for (String var10 : CONFIRM_KEYWORDS) {
                  if (var6.contains(var10)) {
                     return var3;
                  }
               }

               class_2960 var11 = class_7923.field_41178.method_10221(var5.method_7909());
               if (CONFIRM_ICON_ITEMS.contains(var11)) {
                  return var3;
               }
            }
         }
      }

      return -1;
   }

   private void message(String msg) {
      if (mc.field_1724 != null) {
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

   private enum State {
      START,
      UNEQUIP_BEFORE_CMD,
      SEND_COMMAND,
      WAIT_FOR_GUI,
      DUMPING_ITEMS,
      CHECK_LEFTOVERS,
      CONFIRM_SELL,
      FINISH;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static InventorySell.State[] $values() {
         InventorySell.State[] var10000 = new InventorySell.State[8];
         var10000[0] = START;
         var10000[1] = UNEQUIP_BEFORE_CMD;
         var10000[2] = SEND_COMMAND;
         var10000[3] = WAIT_FOR_GUI;
         var10000[4] = DUMPING_ITEMS;
         var10000[5] = CHECK_LEFTOVERS;
         var10000[6] = CONFIRM_SELL;
         var10000[7] = FINISH;
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
