package me.drake.vieclient.modules.combat;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.PlayerDeathEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.KeybindSetting;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1268;
import net.minecraft.class_1304;
import net.minecraft.class_1713;
import net.minecraft.class_1792;
import net.minecraft.class_1802;
import org.lwjgl.glfw.GLFW;

public class ElytraSwap extends Module {
   private final IntSetting swapDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "SwapDelay",
         "Ticks to wait between major operations",
         1,
         0,
         20
      )
   );
   private final BoolSetting switchBack = (BoolSetting)this.addSetting(
      new BoolSetting(
         "SwitchBack",
         "Return to original hotbar slot after swap",
         true
      )
   );
   private final BoolSetting randomDelay = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Random Delay",
         "Randomly vary delay",
         true
      )
   );
   private final IntSetting switchDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "SwitchDelay",
         "Ticks before swapping back",
         0,
         0,
         20
      )
   );
   private final BoolSetting moveToHotbar = (BoolSetting)this.addSetting(
      new BoolSetting(
         "MoveToHotbar",
         "Move elytra from inventory to hotbar if not present",
         true
      )
   );
   private final IntSetting elytraSlot = (IntSetting)this.addSetting(
      new IntSetting(
         "ElytraSlot",
         "Hotbar slot for elytra (1-9) — used for inventory pulls",
         9,
         1,
         9
      )
   );
   private ElytraSwap.Stage stage = ElytraSwap.Stage.IDLE;
   private int stageClock = 0;
   private int originalSlot = -1;
   private int targetHotbar = -1;
   private int equipClock = 0;
   private boolean wasBindDown = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ElytraSwap() {
      super(
         "ElytraSwap",
         "Instantly swap Elytra <-> Chestplate with the bound key",
         Category.COMBAT,
         -1
      );
      this.randomDelay.setVisibleInGui(false);
      this.switchDelay.visibleWhen(this.switchBack::get);
      this.elytraSlot.visibleWhen(this.moveToHotbar::get);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.reset();
      if (mc.field_1724 != null && mc.field_1687 != null) {
         this.stage = ElytraSwap.Stage.CAPTURE;
         this.stageClock = 0;
         this.originalSlot = mc.field_1724.method_31548().method_67532();
         this.targetHotbar = -1;
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.reset();
   }

   @EventHandler
   public void onPlayerDeath(PlayerDeathEvent event) {
      this.reset();
   }

   @EventHandler
   public void onGameJoin(GameJoinEvent event) {
      this.reset();
   }

   private void reset() {
      this.stage = ElytraSwap.Stage.IDLE;
      this.stageClock = 0;
      this.originalSlot = -1;
      this.targetHotbar = -1;
      this.equipClock = 0;
      this.wasBindDown = false;
   }

   @EventHandler
   public void onTickPre(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 == null || mc.field_1687 == null) {
            this.reset();
         } else if (mc.field_1755 != null) {
            this.reset();
         } else if (this.stage == ElytraSwap.Stage.IDLE) {
            if (this.isBindPressed()) {
               this.stage = ElytraSwap.Stage.CAPTURE;
               this.stageClock = 0;
               this.originalSlot = mc.field_1724.method_31548().method_67532();
               this.targetHotbar = -1;
            }
         } else if (this.stageClock > 0) {
            this.stageClock--;
         } else {
            switch (this.stage) {
               case CAPTURE:
                  boolean var2 = mc.field_1724.method_6118(class_1304.field_6174).method_31574(class_1802.field_8833);
                  if (var2) {
                     this.targetHotbar = this.findInHotbar(class_1802.field_8058);
                     if (this.targetHotbar < 0) {
                        this.targetHotbar = this.findInHotbar(class_1802.field_22028);
                     }

                     if (this.targetHotbar < 0 && (Boolean)this.moveToHotbar.get()) {
                        int var4 = this.findInInventory(class_1802.field_8058);
                        if (var4 < 0) {
                           var4 = this.findInInventory(class_1802.field_22028);
                        }

                        if (var4 >= 0) {
                           this.moveFromInventoryToHotbar(var4, (Integer)this.elytraSlot.get() - 1);
                           this.targetHotbar = (Integer)this.elytraSlot.get() - 1;
                        }
                     }

                     if (this.targetHotbar < 0) {
                        this.reset();
                        return;
                     }
                  } else {
                     this.targetHotbar = this.findInHotbar(class_1802.field_8833);
                     if (this.targetHotbar < 0 && (Boolean)this.moveToHotbar.get()) {
                        int var5 = this.findInInventory(class_1802.field_8833);
                        if (var5 >= 0) {
                           this.moveFromInventoryToHotbar(var5, (Integer)this.elytraSlot.get() - 1);
                           this.targetHotbar = (Integer)this.elytraSlot.get() - 1;
                        }
                     }

                     if (this.targetHotbar < 0) {
                        if ((Boolean)this.switchBack.get()) {
                           InventoryUtil.swap(this.originalSlot);
                        }

                        this.reset();
                        return;
                     }
                  }

                  this.advance((Integer)this.swapDelay.get());
                  break;
               case SWAP_TO_HOTBAR:
                  if (this.targetHotbar >= 0) {
                     InventoryUtil.swap(this.targetHotbar);
                  }

                  this.advance((Integer)this.swapDelay.get());
                  break;
               case EQUIP:
                  if (this.equipClock < Math.max(0, (Integer)this.swapDelay.get())) {
                     this.equipClock++;
                     return;
                  }

                  if (this.equipClock == Math.max(0, (Integer)this.swapDelay.get())) {
                     if (mc.field_1761 != null) {
                        mc.field_1761.method_2919(mc.field_1724, class_1268.field_5808);
                     }

                     mc.field_1724.method_6104(class_1268.field_5808);
                     this.equipClock++;
                     this.advance(this.switchBack.get() ? (Integer)this.switchDelay.get() : 1);
                     return;
                  }

                  this.equipClock = 0;
                  boolean var3 = mc.field_1724.method_6118(class_1304.field_6174).method_31574(class_1802.field_8833);
                  if (var3 && !(Boolean)this.switchBack.get()) {
                     this.reset();
                     return;
                  }

                  this.advance(this.switchBack.get() ? (Integer)this.switchDelay.get() : 0);
                  break;
               case SWAP_BACK:
                  if ((Boolean)this.switchBack.get() && this.originalSlot != -1) {
                     InventoryUtil.swap(this.originalSlot);
                  }

                  this.reset();
                  break;
               default:
                  this.reset();
            }
         }
      }
   }

   private void advance(int ticks) {
      int var2 = this.randomDelay.get() ? ThreadLocalRandom.current().nextInt(0, 2) : 0;
      this.stageClock = Math.max(0, ticks + var2);
      this.stage = this.nextStage(this.stage);
   }

   private ElytraSwap.Stage nextStage(ElytraSwap.Stage current) {
      return switch (current) {
         case CAPTURE -> ElytraSwap.Stage.SWAP_TO_HOTBAR;
         case SWAP_TO_HOTBAR -> ElytraSwap.Stage.EQUIP;
         case EQUIP -> this.switchBack.get() ? ElytraSwap.Stage.SWAP_BACK : ElytraSwap.Stage.IDLE;
         case SWAP_BACK -> ElytraSwap.Stage.IDLE;
         default -> ElytraSwap.Stage.IDLE;
      };
   }

   private boolean isBindPressed() {
      KeybindSetting var1 = this.getKeybind();
      if (var1 != null && var1.isBound()) {
         int var2 = (Integer)var1.get();
         if (mc.method_22683() == null) {
            return false;
         }

         long var3 = mc.method_22683().method_4490();
         boolean var5;
         if (var2 >= (1000)) {
            int var6 = var2 - (1000);
            var5 = GLFW.glfwGetMouseButton(var3, var6) == 1;
         } else {
            if (var2 < 0) {
               return false;
            }

            var5 = GLFW.glfwGetKey(var3, var2) == 1;
         }

         if (var5 && !this.wasBindDown) {
            this.wasBindDown = true;
            return true;
         }

         if (!var5) {
            this.wasBindDown = false;
         }

         return false;
      } else {
         return false;
      }
   }

   private int findInHotbar(class_1792 item) {
      if (mc.field_1724 != null && item != null) {
         for (int var2 = 0; var2 < (9); var2++) {
            if (mc.field_1724.method_31548().method_5438(var2).method_31574(item)) {
               return var2;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private int findInInventory(class_1792 item) {
      if (mc.field_1724 != null && item != null) {
         for (int var2 = 9; var2 < (36); var2++) {
            if (mc.field_1724.method_31548().method_5438(var2).method_31574(item)) {
               return var2;
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private void moveFromInventoryToHotbar(int invSlot, int hotbarSelected) {
      if (mc.field_1724 != null && mc.field_1761 != null && hotbarSelected >= 0 && hotbarSelected <= (8)) {
         int var3 = mc.field_1724.field_7512.field_7763;
         int var4 = (36) + hotbarSelected;
         mc.field_1761.method_2906(var3, invSlot, 0, class_1713.field_7790, mc.field_1724);
         mc.field_1761.method_2906(var3, var4, 0, class_1713.field_7790, mc.field_1724);
         mc.field_1761.method_2906(var3, invSlot, 0, class_1713.field_7790, mc.field_1724);
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

   private enum Stage {
      IDLE,
      CAPTURE,
      SWAP_TO_HOTBAR,
      EQUIP,
      SWAP_BACK;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ElytraSwap.Stage[] $values() {
         ElytraSwap.Stage[] var10000 = new ElytraSwap.Stage[5];
         var10000[0] = IDLE;
         var10000[1] = CAPTURE;
         var10000[2] = SWAP_TO_HOTBAR;
         var10000[3] = EQUIP;
         var10000[4] = SWAP_BACK;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-34);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-101);
         IDLE = new ElytraSwap.Stage();
         var10002 = new byte[7];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(22);
         var10002[5] = (byte)(2);
         var10002[6] = (byte)(95);
         CAPTURE = new ElytraSwap.Stage();
         var10002 = new byte[14];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-51);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-114);
         var10002[4] = (byte)(28);
         var10002[5] = (byte)(4);
         var10002[6] = (byte)(85);
         var10002[7] = (byte)(57);
         var10002[8] = (byte)(30);
         var10002[9] = (byte)(-19);
         var10002[10] = (byte)(-80);
         var10002[11] = (byte)(-76);
         var10002[12] = (byte)(90);
         var10002[13] = (byte)(-38);
         SWAP_TO_HOTBAR = new ElytraSwap.Stage();
         var10002 = new byte[5];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-53);
         var10002[2] = (byte)(73);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(19);
         EQUIP = new ElytraSwap.Stage();
         var10002 = new byte[9];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-51);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-114);
         var10002[4] = (byte)(28);
         var10002[5] = (byte)(18);
         var10002[6] = (byte)(91);
         var10002[7] = (byte)(37);
         var10002[8] = (byte)(29);
         SWAP_BACK = new ElytraSwap.Stage();
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
