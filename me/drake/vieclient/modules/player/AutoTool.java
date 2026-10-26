package me.drake.vieclient.modules.player;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class AutoTool extends Module {
   private final BoolSetting switchBack = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Switch Back",
         "Automatically return to original slot after mining",
         true
      )
   );
   private final IntSetting switchDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "Switch Delay",
         "Tick delay before switching tool for legit humanization",
         1,
         0,
         5
      )
   );
   private final IntSetting returnDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "Return Delay",
         "Tick delay before returning to original slot",
         2,
         0,
         10
      )
   );
   private final BoolSetting duraSafe = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Dura Safe",
         "Prevent using tools close to breaking",
         true
      )
   );
   private final IntSetting duraThreshold = (IntSetting)this.addSetting(
      new IntSetting(
         "Min Dura",
         "Minimum durability threshold for tool protection",
         10,
         1,
         50
      )
   );
   private final BoolSetting preferAoe = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Prefer 3x3",
         "Prioritize 3x3 Shard Pickaxes / Drills on custom servers",
         true
      )
   );
   private final BoolSetting onlyWhileMining = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Only While Mining",
         "Only switch tools while actively mining blocks",
         true
      )
   );
   private class_2338 currentTarget = null;
   private int pendingSlot = -1;
   private int delayTimer = 0;
   private int originSlot = -1;
   private int ourAssignedSlot = -1;
   private int returnTimer = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoTool() {
      super(
         "AutoTool",
         "Automatically switches to the optimal tool when mining and returns to original slot",
         Category.PLAYER
      );
      this.onlyWhileMining.setVisibleInGui(false);
   }

   @Override
   public void onEnable() {
      this.reset();
   }

   @Override
   public void onDisable() {
      if (this.originSlot != (-1) && mc.field_1724 != null) {
         this.selectSlot(this.originSlot);
      }

      this.reset();
   }

   private void reset() {
      this.currentTarget = null;
      this.pendingSlot = -1;
      this.delayTimer = 0;
      this.originSlot = -1;
      this.ourAssignedSlot = -1;
      this.returnTimer = 0;
   }

   @Override
   public void onTick() {
      if (!this.isInGame()) {
         this.reset();
      } else {
         int var1 = mc.field_1724.method_31548().method_67532();
         if (this.originSlot != (-1) && var1 != this.ourAssignedSlot) {
            this.originSlot = -1;
            this.ourAssignedSlot = -1;
         }

         class_2338 var2 = null;
         if (mc.field_1755 == null
            && mc.field_1690.field_1886.method_1434()
            && mc.field_1765 instanceof class_3965 var3
            && var3.method_17783() == class_240.field_1332) {
            var2 = var3.method_17777();
         }

         class_2680 var6 = var2 != null && mc.field_1687 != null ? mc.field_1687.method_8320(var2) : null;
         boolean var7 = var2 != null && var6 != null && !var6.method_26215() && var6.method_26214(mc.field_1687, var2) >= 0.0F;
         if (var7) {
            this.returnTimer = 0;
            if (!var2.equals(this.currentTarget)) {
               this.currentTarget = var2;
               this.pendingSlot = -1;
               this.delayTimer = 0;
            }

            int var5 = this.findBestToolSlot(var6, var2, var1);
            if (var5 == var1) {
               this.pendingSlot = -1;
               this.delayTimer = 0;
               return;
            }

            if (var5 != (-1)) {
               if (this.pendingSlot != var5) {
                  this.pendingSlot = var5;
                  this.delayTimer = (Integer)this.switchDelay.get();
               }

               if (this.delayTimer > 0) {
                  this.delayTimer--;
               } else {
                  this.selectSlot(var5);
                  this.pendingSlot = -1;
               }
            }
         } else {
            this.currentTarget = null;
            this.pendingSlot = -1;
            this.delayTimer = 0;
            this.handleReturnSlot();
         }
      }
   }

   private void selectSlot(int slot) {
      if (ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL)) {
         if (this.originSlot == (-1)) {
            this.originSlot = mc.field_1724.method_31548().method_67532();
         }

         mc.field_1724.method_31548().method_61496(slot);
         this.ourAssignedSlot = slot;
         this.returnTimer = (Integer)this.returnDelay.get();
      }
   }

   private void handleReturnSlot() {
      if ((Boolean)this.switchBack.get() && this.originSlot != (-1)) {
         if (this.returnTimer > 0) {
            this.returnTimer--;
         } else {
            if (ActionArbiter.get().tryAcquire(ActionArbiter.Resource.HOTBAR_SLOT, ActionArbiter.Owner.AUTOTOOL)) {
               mc.field_1724.method_31548().method_61496(this.originSlot);
            }

            this.originSlot = -1;
            this.ourAssignedSlot = -1;
         }
      } else {
         this.originSlot = -1;
         this.ourAssignedSlot = -1;
      }
   }

   private int findBestToolSlot(class_2680 state, class_2338 pos, int selectedSlot) {
      class_1799 var4 = mc.field_1724.method_31548().method_5438(selectedSlot);
      if ((Boolean)this.preferAoe.get()) {
         int var5 = this.findAoeToolSlot(state, pos);
         if (var5 != (-1)) {
            return var5;
         }
      }

      int var10 = selectedSlot;
      float var6 = this.isUsableTool(var4) ? this.calculateBreakingSpeed(selectedSlot, state, pos) : -1.0F;

      for (int var7 = 0; var7 < (9); var7++) {
         if (var7 != selectedSlot) {
            class_1799 var8 = mc.field_1724.method_31548().method_5438(var7);
            if (this.isUsableTool(var8)) {
               float var9 = this.calculateBreakingSpeed(var7, state, pos);
               if (var9 > var6) {
                  var6 = var9;
                  var10 = var7;
               }
            }
         }
      }

      return var10 == selectedSlot && !this.isUsableTool(var4) ? -1 : var10;
   }

   private int findAoeToolSlot(class_2680 state, class_2338 pos) {
      for (int var3 = 0; var3 < (9); var3++) {
         class_1799 var4 = mc.field_1724.method_31548().method_5438(var3);
         if (this.isUsableTool(var4)) {
            String var5 = var4.method_7964().getString().toLowerCase();
            byte[] var10001 = new byte[5];
            var10001[0] = (byte)(92);
            var10001[1] = (byte)(-14);
            var10001[2] = (byte)(125);
            var10001[3] = (byte)(-84);
            var10001[4] = (byte)(-93);
            if (!var5.contains($$v_s(var10001, -8245762720835317750L))) {
               var10001 = new byte[5];
               var10001[0] = (byte)(75);
               var10001[1] = (byte)(-24);
               var10001[2] = (byte)(117);
               var10001[3] = (byte)(-78);
               var10001[4] = (byte)(-85);
               if (!var5.contains($$v_s(var10001, -8245762720835317750L))) {
                  var10001 = new byte[3];
                  var10001[0] = (byte)(28);
                  var10001[1] = (byte)(-30);
                  var10001[2] = (byte)(47);
                  if (!var5.contains($$v_s(var10001, -8245762720835317750L))) {
                     continue;
                  }
               }
            }

            if (var4.method_7951(state) || var4.method_7924(state) > 1.0F) {
               return var3;
            }
         }
      }

      return -1;
   }

   private boolean isUsableTool(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         if ((Boolean)this.duraSafe.get() && stack.method_7963()) {
            int var2 = stack.method_7936() - stack.method_7919();
            if (var2 <= (Integer)this.duraThreshold.get()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private float calculateBreakingSpeed(int slot, class_2680 state, class_2338 pos) {
      int var4 = mc.field_1724.method_31548().method_67532();
      mc.field_1724.method_31548().method_61496(slot);

      float var5;
      try {
         var5 = state.method_26165(mc.field_1724, mc.field_1687, pos);
      } finally {
         mc.field_1724.method_31548().method_61496(var4);
      }

      return var5;
   }

   @Override
   public String getInfo() {
      String var10000;
      if ((Integer)this.switchDelay.get() > 0) {
         var10000 = "Legit (" + this.switchDelay.get() + "t)";
      } else {
         byte[] var1 = new byte[7];
         var1[0] = (byte)(102);
         var1[1] = (byte)(-12);
         var1[2] = (byte)(111);
         var1[3] = (byte)(-86);
         var1[4] = (byte)(-90);
         var1[5] = (byte)(-102);
         var1[6] = (byte)(-26);
         var10000 = $$v_s(var1, -8245762720835317750L);
      }

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
