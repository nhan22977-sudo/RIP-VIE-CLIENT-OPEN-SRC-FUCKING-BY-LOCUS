package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.StringSetting;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_465;
import net.minecraft.class_7923;

public class AutoSell extends Module {
   private final StringSetting targetItems = (StringSetting)this.addSetting(
      new StringSetting(
         "TargetItems",
         "Comma-separated item ids to sell",
         "minecraft:cobblestone_wall"
      )
   );
   private final StringSetting sellCommand = (StringSetting)this.addSetting(
      new StringSetting(
         "SellCommand",
         "Command to open sell GUI",
         "/sell"
      )
   );
   private final IntSetting sellButtonSlot = (IntSetting)this.addSetting(
      new IntSetting(
         "SellButtonSlot",
         "Fallback slot index for sell button",
         49,
         0,
         200
      )
   );
   private final IntSetting requiredFilledSlots = (IntSetting)this.addSetting(
      new IntSetting(
         "RequiredFilledSlots",
         "Sell when this many container slots are filled (0 = auto)",
         45,
         0,
         200
      )
   );
   private final IntSetting cycleDelayMs = (IntSetting)this.addSetting(
      new IntSetting(
         "CycleDelayMs",
         "Delay between sell cycles in milliseconds",
         4000,
         200,
         60000,
         50
      )
   );
   private static final long GUI_WAIT_TIMEOUT_MS = 5000L;
   private static final long DELAY_MINIMAL_MS = 50L;
   private static final long DELAY_AFTER_CMD_MS = 300L;
   private static final long DELAY_AFTER_DEPOSIT_MS = 250L;
   private static final long DELAY_AFTER_SELL_CLICK_MS = 350L;
   private static final long DELAY_TOPUP_RETRY_MS = 500L;
   private static final int MAX_OPEN_GUI_RETRIES = 5;
   private static final int EMPTY_CYCLE_WARN_EVERY = 5;
   private static final String[] CONFIRM_KEYWORDS = new String[]{
      "sell",
      "confirm",
      "accept",
      "ok",
      "ban",
      "bán",
      "xac nhan",
      "xác nhận",
      "dong y",
      "đồng ý"
   };
   private static final Set COMMON_CONFIRM_ITEMS = Set.of(
      class_2960.method_60655(
         "minecraft",
         "lime_stained_glass_pane"
      ),
      class_2960.method_60655(
         "minecraft",
         "lime_dye"
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
   private final Set parsedTargets = new LinkedHashSet();
   private String parsedTargetsSource = "";
   private AutoSell.State state = AutoSell.State.IDLE;
   private int openGuiRetries = 0;
   private int emptyCycleStreak = 0;
   private long delayUntilMs = 0L;
   private long guiWaitDeadlineMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoSell() {
      super(
         "AutoSell",
         "GUI-based autosell state machine (DonutSMP)",
         Category.SMP_UTILITIES
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetRuntime();
      this.refreshParsedTargets();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.resetRuntime();
   }

   @Override
   public void onTick() {
      if (this.isInGame()) {
         this.refreshParsedTargets();
         if (System.currentTimeMillis() >= this.delayUntilMs) {
            switch (this.state) {
               case IDLE:
                  this.state = AutoSell.State.OPEN_SELL_GUI;
                  break;
               case OPEN_SELL_GUI:
                  this.handleOpenSellGui();
                  break;
               case WAIT_SELL_GUI:
                  this.handleWaitSellGui();
                  break;
               case DEPOSIT_ITEMS:
                  this.handleDepositItems();
                  break;
               case CLICK_SELL_BUTTON:
                  this.handleClickSellButton();
                  break;
               case AFTER_SELL_CHECK:
                  this.handleAfterSellCheck();
                  break;
               case CYCLE_COOLDOWN:
                  this.setDelay(((Integer)this.cycleDelayMs.get()).intValue());
                  this.state = AutoSell.State.IDLE;
            }
         }
      }
   }

   private void handleOpenSellGui() {
      if (mc.field_1755 instanceof class_465) {
         this.openGuiRetries = 0;
         this.setDelay(50L);
         this.state = AutoSell.State.DEPOSIT_ITEMS;
      } else if (this.parsedTargets.isEmpty()) {
         this.warn(
            "No valid target items. Set TargetItems first."
         );
         this.setEnabled(false);
      } else if (this.openGuiRetries >= 5) {
         this.warn(
            "Cannot open sell GUI after 5 retries. Stopping."
         );
         this.setEnabled(false);
      } else {
         this.sendCommand((String)this.sellCommand.get());
         this.openGuiRetries++;
         this.setDelay(300L);
         this.guiWaitDeadlineMs = System.currentTimeMillis() + 5000L;
         this.state = AutoSell.State.WAIT_SELL_GUI;
      }
   }

   private void handleWaitSellGui() {
      if (mc.field_1755 instanceof class_465) {
         this.openGuiRetries = 0;
         this.setDelay(50L);
         this.state = AutoSell.State.DEPOSIT_ITEMS;
      } else {
         if (System.currentTimeMillis() >= this.guiWaitDeadlineMs) {
            this.state = AutoSell.State.OPEN_SELL_GUI;
         }
      }
   }

   private void handleDepositItems() {
      if (!(mc.field_1755 instanceof class_465 var1)) {
         this.state = AutoSell.State.OPEN_SELL_GUI;
      } else if (mc.field_1761 != null && mc.field_1724 != null) {
         class_1703 var10 = var1.method_17577();
         int var3 = var10.field_7761.size();
         int var4 = Math.max(0, var3 - 36);
         int var5 = 0;

         for (int var6 = var4; var6 < var3; var6++) {
            class_1799 var7 = var10.method_7611(var6).method_7677();
            if (this.matchesTarget(var7)) {
               mc.field_1761.method_2906(var10.field_7763, var6, 0, class_1713.field_7794, mc.field_1724);
               var5++;
            }
         }

         int var11 = this.findConfirmSlot(var10, var4);
         int var12 = this.countFilledItemSlots(var10, var4, var11);
         int var8 = this.effectiveCapacity(var11, var4);
         boolean var9 = var8 > 0 && var12 >= var8;
         if (var9) {
            this.emptyCycleStreak = 0;
            this.setDelay(250L);
            this.state = AutoSell.State.CLICK_SELL_BUTTON;
         } else {
            if (var5 == 0) {
               if (var12 > 0) {
                  this.emptyCycleStreak = 0;
                  this.setDelay(250L);
                  this.state = AutoSell.State.CLICK_SELL_BUTTON;
                  return;
               }

               this.emptyCycleStreak++;
               if (this.emptyCycleStreak == 1 || this.emptyCycleStreak % 5 == 0) {
                  this.warn(
                     "No matching items found, waiting..."
                  );
               }
            } else {
               this.emptyCycleStreak = 0;
            }

            this.setDelay(500L);
            this.state = AutoSell.State.DEPOSIT_ITEMS;
         }
      }
   }

   private void handleClickSellButton() {
      if (mc.field_1755 instanceof class_465 var1) {
         if (mc.field_1761 != null && mc.field_1724 != null) {
            class_1703 var6 = var1.method_17577();
            int var3 = var6.field_7761.size();
            int var4 = Math.max(0, var3 - 36);
            int var5 = this.findConfirmSlot(var6, var4);
            if (this.isValidSlot(var5, var3)) {
               mc.field_1761.method_2906(var6.field_7763, var5, 0, class_1713.field_7790, mc.field_1724);
            } else {
               this.warn(
                  "Sell button not found. Set SellButtonSlot manually."
               );
            }

            this.setDelay(350L);
            this.state = AutoSell.State.AFTER_SELL_CHECK;
         }
      } else {
         this.state = AutoSell.State.OPEN_SELL_GUI;
      }
   }

   private void handleAfterSellCheck() {
      this.state = mc.field_1755 instanceof class_465 ? AutoSell.State.DEPOSIT_ITEMS : AutoSell.State.CYCLE_COOLDOWN;
   }

   private int findConfirmSlot(class_1703 handler, int containerSlotCount) {
      int var3 = -1;

      for (int var4 = 0; var4 < containerSlotCount; var4++) {
         class_1799 var5 = handler.method_7611(var4).method_7677();
         if (!var5.method_7960()) {
            String var6 = this.stripFormatting(var5.method_7964().getString()).toLowerCase(Locale.ROOT);

            for (String var10 : CONFIRM_KEYWORDS) {
               if (var6.contains(var10)) {
                  return var4;
               }
            }

            if (var3 == (-1)) {
               class_2960 var11 = class_7923.field_41178.method_10221(var5.method_7909());
               if (COMMON_CONFIRM_ITEMS.contains(var11)) {
                  var3 = var4;
               }
            }
         }
      }

      return var3 != (-1) ? var3 : (Integer)this.sellButtonSlot.get();
   }

   private int effectiveCapacity(int confirmSlot, int containerSlotCount) {
      return this.requiredFilledSlots.get() > 0
         ? Math.min((Integer)this.requiredFilledSlots.get(), containerSlotCount)
         : containerSlotCount - (this.isValidSlot(confirmSlot, containerSlotCount) ? 1 : 0);
   }

   private int countFilledItemSlots(class_1703 handler, int containerSlotCount, int confirmSlot) {
      int var4 = 0;

      for (int var5 = 0; var5 < containerSlotCount; var5++) {
         if (var5 != confirmSlot && !handler.method_7611(var5).method_7677().method_7960()) {
            var4++;
         }
      }

      return var4;
   }

   private boolean matchesTarget(class_1799 stack) {
      if (stack.method_7960()) {
         return false;
      }

      class_2960 var2 = class_7923.field_41178.method_10221(stack.method_7909());
      return this.parsedTargets.contains(var2);
   }

   private void refreshParsedTargets() {
      String var1 = (String)this.targetItems.get();
      if (var1 == null) {
         var1 = "";
      }

      if (!var1.equals(this.parsedTargetsSource)) {
         this.parsedTargetsSource = var1;
         this.parsedTargets.clear();
         String[] var2 = var1.split(",");

         for (String var6 : var2) {
            class_2960 var7 = this.parseItemId(var6);
            if (var7 != null && class_7923.field_41178.method_10235().contains(var7)) {
               this.parsedTargets.add(var7);
            }
         }
      }
   }

   private class_2960 parseItemId(String token) {
      if (token == null) {
         return null;
      }

      String var2 = token.trim().toLowerCase(Locale.ROOT);
      if (var2.isEmpty()) {
         return null;
      }

      String var3 = var2.contains(":") ? var2 : "minecraft:" + var2;
      int var4 = var3.indexOf(58);
      if (var4 > 0 && var4 < var3.length() - 1) {
         String var5 = var3.substring(0, var4);
         String var6 = var3.substring(var4 + 1);

         try {
            return class_2960.method_60655(var5, var6);
         } catch (Exception var8) {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean isValidSlot(int slot, int bound) {
      return slot >= 0 && slot < bound;
   }

   private String stripFormatting(String text) {
      if (text == null) {
         return "";
      }

      byte[] var10001 = new byte[18];
      var10001[0] = (byte)(84);
      var10001[1] = (byte)(-17);
      var10001[2] = (byte)(44);
      var10001[3] = (byte)(-18);
      var10001[4] = (byte)(101);
      var10001[5] = (byte)(28);
      var10001[6] = (byte)(-93);
      var10001[7] = (byte)(7);
      var10001[8] = (byte)(77);
      var10001[9] = (byte)(-101);
      var10001[10] = (byte)(-123);
      var10001[11] = (byte)(-37);
      var10001[12] = (byte)(26);
      var10001[13] = (byte)(-104);
      var10001[14] = (byte)(-3);
      var10001[15] = (byte)(32);
      var10001[16] = (byte)(42);
      var10001[17] = (byte)(23);
      return text.replaceAll($$v_s(var10001, 2304705690237915181L), "");
   }

   private void setDelay(long ms) {
      this.delayUntilMs = System.currentTimeMillis() + ms;
   }

   private void sendCommand(String raw) {
      if (mc.field_1724 != null && mc.field_1724.field_3944 != null) {
         String var2 = raw == null ? "" : raw.trim();
         if (var2.isEmpty()) {
            byte[] var10000 = new byte[5];
            var10000[0] = (byte)(39);
            var10000[1] = (byte)(-23);
            var10000[2] = (byte)(121);
            var10000[3] = (byte)(-78);
            var10000[4] = (byte)(72);
            var2 = $$v_s(var10000, 2304705690237915181L);
         }

         if (var2.startsWith("/")) {
            mc.field_1724.field_3944.method_45730(var2.substring(1));
         } else {
            mc.field_1724.field_3944.method_45729(var2);
         }
      }
   }

   private void warn(String msg) {
      if (mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470("§d[VC]§7 [AutoSell] " + msg), false);
      }
   }

   private void resetRuntime() {
      this.state = AutoSell.State.IDLE;
      this.openGuiRetries = 0;
      this.emptyCycleStreak = 0;
      this.delayUntilMs = 0L;
      this.guiWaitDeadlineMs = 0L;
   }

   @Override
   public String getInfo() {
      return switch (this.state) {
         case IDLE -> "Idle";
         case OPEN_SELL_GUI -> "OpenGUI";
         case WAIT_SELL_GUI -> "WaitGUI";
         case DEPOSIT_ITEMS -> "Deposit";
         case CLICK_SELL_BUTTON -> "ClickSell";
         case AFTER_SELL_CHECK -> "PostSell";
         case CYCLE_COOLDOWN -> "Cooldown";
      };
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
      IDLE,
      OPEN_SELL_GUI,
      WAIT_SELL_GUI,
      DEPOSIT_ITEMS,
      CLICK_SELL_BUTTON,
      AFTER_SELL_CHECK,
      CYCLE_COOLDOWN;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoSell.State[] $values() {
         AutoSell.State[] var10000 = new AutoSell.State[7];
         var10000[0] = IDLE;
         var10000[1] = OPEN_SELL_GUI;
         var10000[2] = WAIT_SELL_GUI;
         var10000[3] = DEPOSIT_ITEMS;
         var10000[4] = CLICK_SELL_BUTTON;
         var10000[5] = AFTER_SELL_CHECK;
         var10000[6] = CYCLE_COOLDOWN;
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
