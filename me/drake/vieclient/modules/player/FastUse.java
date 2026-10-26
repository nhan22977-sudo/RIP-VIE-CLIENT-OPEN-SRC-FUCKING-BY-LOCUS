package me.drake.vieclient.modules.player;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;

public class FastUse extends Module {
   private final EnumSetting mode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Mode",
         "Which items to fast use",
         FastUse.Mode.All
      )
   );
   private final IntSetting cooldown = (IntSetting)this.addSetting(
      new IntSetting(
         "Cooldown",
         "Fast-use cooldown in ticks (0 = instant)",
         0,
         0,
         4
      )
   );
   private final BoolSetting blocks = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Blocks",
         "Fast-places blocks if mode is Some",
         true
      )
   );
   private final BoolSetting exp = (BoolSetting)this.addSetting(
      new BoolSetting(
         "EXP Bottles",
         "Fast use experience bottles if mode is Some",
         true
      )
   );
   private final BoolSetting crystals = (BoolSetting)this.addSetting(
      new BoolSetting(
         "End Crystals",
         "Fast place end crystals if mode is Some",
         true
      )
   );
   private final BoolSetting fireworks = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Fireworks",
         "Fast use firework rockets if mode is Some",
         true
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FastUse() {
      super(
         "FastUse",
         "Allows you to use items at very high speeds.",
         Category.PLAYER
      );
      this.blocks.visibleWhen(() -> this.mode.get() == FastUse.Mode.Some);
      this.exp.visibleWhen(() -> this.mode.get() == FastUse.Mode.Some);
      this.crystals.visibleWhen(() -> this.mode.get() == FastUse.Mode.Some);
      this.fireworks.visibleWhen(() -> this.mode.get() == FastUse.Mode.Some);
   }

   public int getItemUseCooldown(class_1799 stack) {
      if (!this.isEnabled()) {
         return 4;
      } else {
         return this.mode.get() != FastUse.Mode.All && !this.shouldWorkSome(stack) ? 4 : (Integer)this.cooldown.get();
      }
   }

   private boolean shouldWorkSome(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         class_1792 var2 = stack.method_7909();
         if ((Boolean)this.blocks.get() && var2 instanceof class_1747) {
            return true;
         } else if ((Boolean)this.exp.get() && var2 == class_1802.field_8287) {
            return true;
         } else {
            return this.crystals.get() && var2 == class_1802.field_8301 ? true : (Boolean)this.fireworks.get() && var2 == class_1802.field_8639;
         }
      } else {
         return false;
      }
   }

   public int getCooldown() {
      return (Integer)this.cooldown.get();
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

   public enum Mode {
      All,
      Some;

      private final String label;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Mode(String label) {
         this.label = labelx;
      }

      @Override
      public String toString() {
         return this.label;
      }

      // $VF: synthetic method
      private static FastUse.Mode[] $values() {
         FastUse.Mode[] var10000 = new FastUse.Mode[2];
         var10000[0] = All;
         var10000[1] = Some;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-10);
         var10002[2] = (byte)(112);
         String var0 = $$v_s(var10002, -1254538183313207270L);
         byte[] var10004 = new byte[3];
         var10004[0] = (byte)(126);
         var10004[1] = (byte)(-10);
         var10004[2] = (byte)(112);
         All = new FastUse.Mode($$v_s(var10004, -1254538183313207270L));
         var10002 = new byte[4];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-11);
         var10002[2] = (byte)(113);
         var10002[3] = (byte)(-69);
         String var2 = $$v_s(var10002, -1254538183313207270L);
         var10004 = new byte[4];
         var10004[0] = (byte)(108);
         var10004[1] = (byte)(-11);
         var10004[2] = (byte)(113);
         var10004[3] = (byte)(-69);
         Some = new FastUse.Mode($$v_s(var10004, -1254538183313207270L));
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
