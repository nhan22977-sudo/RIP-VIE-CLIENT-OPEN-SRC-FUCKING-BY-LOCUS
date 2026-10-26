package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_634;

public class AutoPearlMethod extends Module {
   public final EnumSetting region = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Region",
         "Target DonutSMP region for /rtp",
         AutoPearlMethod.Region.ASIA,
         AutoPearlMethod.Region.values()
      )
   );
   public final IntSetting delay = (IntSetting)this.addSetting(
      new IntSetting(
         "Delay (ms)",
         "Delay between throw and /rtp (ms)",
         150,
         50,
         1000
      )
   );
   private AutoPearlMethod.State state = AutoPearlMethod.State.IDLE;
   private long nextActionMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoPearlMethod() {
      super(
         "Auto Pearl Method",
         "Aim up -> throw pearl -> execute /rtp <region>",
         Category.SMP_UTILITIES
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isInGame() && mc.field_1724 != null) {
         int var1 = -1;

         for (int var2 = 0; var2 < (9); var2++) {
            if (mc.field_1724.method_31548().method_5438(var2).method_31574(class_1802.field_8634)) {
               var1 = var2;
               break;
            }
         }

         if (var1 == (-1) && !mc.field_1724.method_6079().method_31574(class_1802.field_8634)) {
            mc.field_1724
               .method_7353(class_2561.method_43470(class_124.field_1061 + "[AutoPearl] " + class_124.field_1068 + "No Ender Pearl found in hotbar!"), false);
            this.setEnabled(false);
         } else {
            if (var1 != (-1)) {
               mc.field_1724.method_31548().method_61496(var1);
            }

            mc.field_1724.method_36457(-90.0F);
            int var3 = Math.max(50, (Integer)this.delay.get());
            this.nextActionMs = System.currentTimeMillis() + 50L;
            this.state = AutoPearlMethod.State.AIM_UP;
         }
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      this.state = AutoPearlMethod.State.IDLE;
      this.nextActionMs = 0L;
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (!this.isInGame() || mc.field_1724 == null) {
         this.state = AutoPearlMethod.State.IDLE;
         if (this.isEnabled()) {
            this.setEnabled(false);
         }
      } else if (this.state == AutoPearlMethod.State.IDLE) {
         if (this.isEnabled()) {
            this.setEnabled(false);
         }
      } else {
         long var1 = System.currentTimeMillis();
         if (var1 >= this.nextActionMs) {
            int var3 = Math.max(50, (Integer)this.delay.get());
            switch (this.state) {
               case AIM_UP:
                  mc.field_1724.method_36457(-90.0F);
                  class_1268 var6 = class_1268.field_5808;
                  if (!mc.field_1724.method_6047().method_31574(class_1802.field_8634)) {
                     if (mc.field_1724.method_6079().method_31574(class_1802.field_8634)) {
                        var6 = class_1268.field_5810;
                     } else {
                        for (int var7 = 0; var7 < (9); var7++) {
                           if (mc.field_1724.method_31548().method_5438(var7).method_31574(class_1802.field_8634)) {
                              mc.field_1724.method_31548().method_61496(var7);
                              break;
                           }
                        }
                     }
                  }

                  if (mc.field_1761 != null) {
                     mc.field_1761.method_2919(mc.field_1724, var6);
                  }

                  mc.field_1724.method_6104(var6);
                  this.nextActionMs = var1 + var3;
                  this.state = AutoPearlMethod.State.EXECUTE_RTP;
                  break;
               case THROW_PEARL:
                  this.state = AutoPearlMethod.State.IDLE;
                  this.setEnabled(false);
                  break;
               case EXECUTE_RTP:
                  AutoPearlMethod.Region var4 = (AutoPearlMethod.Region)this.region.get();
                  String var5 = "rtp " + var4.getArg();
                  this.sendCommand(var5);
                  this.state = AutoPearlMethod.State.IDLE;
                  this.setEnabled(false);
            }
         }
      }
   }

   private void sendCommand(String command) {
      if (mc != null) {
         class_634 var2 = null;
         if (mc.field_1724 != null) {
            var2 = mc.field_1724.field_3944;
         }

         if (var2 == null) {
            var2 = mc.method_1562();
         }

         if (var2 != null) {
            String var3 = command.startsWith("/") ? command.substring(1) : command;

            try {
               var2.method_45730(var3);
            } catch (Throwable var7) {
               try {
                  var2.method_45729(command);
               } catch (Throwable var6) {
               }
            }
         }
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

   public enum Region {
      ASIA("asia", "Asia"),
      EU_CENTRAL(
         "eu central",
         "EU Central"
      ),
      EU_WEST("eu west", "EU West"),
      EAST("east", "East"),
      WEST("west", "West"),
      OCEANIA("oceania", "Oceania");

      private final String arg;
      private final String label;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Region(String arg, String label) {
         this.arg = argx;
         this.label = labelx;
      }

      public String getArg() {
         return this.arg;
      }

      @Override
      public String toString() {
         return this.label;
      }

      // $VF: synthetic method
      private static AutoPearlMethod.Region[] $values() {
         AutoPearlMethod.Region[] var10000 = new AutoPearlMethod.Region[6];
         var10000[0] = ASIA;
         var10000[1] = EU_CENTRAL;
         var10000[2] = EU_WEST;
         var10000[3] = EAST;
         var10000[4] = WEST;
         var10000[5] = OCEANIA;
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

   private enum State {
      IDLE,
      AIM_UP,
      THROW_PEARL,
      EXECUTE_RTP;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoPearlMethod.State[] $values() {
         AutoPearlMethod.State[] var10000 = new AutoPearlMethod.State[4];
         var10000[0] = IDLE;
         var10000[1] = AIM_UP;
         var10000[2] = THROW_PEARL;
         var10000[3] = EXECUTE_RTP;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-34);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-101);
         IDLE = new AutoPearlMethod.State();
         var10002 = new byte[6];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(117);
         var10002[5] = (byte)(10);
         AIM_UP = new AutoPearlMethod.State();
         var10002 = new byte[11];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-46);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-111);
         var10002[4] = (byte)(119);
         var10002[5] = (byte)(5);
         var10002[6] = (byte)(-89);
         var10002[7] = (byte)(-73);
         var10002[8] = (byte)(22);
         var10002[9] = (byte)(-16);
         var10002[10] = (byte)(-88);
         THROW_PEARL = new AutoPearlMethod.State();
         var10002 = new byte[11];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-62);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(117);
         var10002[5] = (byte)(14);
         var10002[6] = (byte)(-78);
         var10002[7] = (byte)(-83);
         var10002[8] = (byte)(5);
         var10002[9] = (byte)(-10);
         var10002[10] = (byte)(-76);
         EXECUTE_RTP = new AutoPearlMethod.State();
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
