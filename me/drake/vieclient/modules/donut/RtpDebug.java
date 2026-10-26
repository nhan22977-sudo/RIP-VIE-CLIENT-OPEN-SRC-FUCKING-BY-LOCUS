package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_243;
import net.minecraft.class_634;

public class RtpDebug extends Module {
   public final EnumSetting region = (EnumSetting)this.addSetting(
      new EnumSetting(
            "Region",
            "Target DonutSMP region for /rtp",
            RtpDebug.Region.ASIA
         )
         .withDisplayName(RtpDebug.Region.ASIA, "Asia")
         .withDisplayName(RtpDebug.Region.EU_CENTRAL, "EU Central")
         .withDisplayName(RtpDebug.Region.EU_WEST, "EU West")
         .withDisplayName(RtpDebug.Region.EAST, "East")
         .withDisplayName(RtpDebug.Region.WEST, "West")
         .withDisplayName(RtpDebug.Region.OCEANIA, "Oceania")
   );
   public final IntSetting homeSlot = (IntSetting)this.addSetting(
      new IntSetting(
         "Home Slot",
         "Home slot number (1-10)",
         1,
         1,
         10
      )
   );
   public final IntSetting delay = (IntSetting)this.addSetting(
      new IntSetting(
         "Delay (ms)",
         "Delay between commands (ms)",
         250,
         0,
         5000
      )
   );
   private RtpDebug.State state = RtpDebug.State.IDLE;
   private long nextActionMs = 0L;
   private long rtpSentTime = 0L;
   private long homeSentTime = 0L;
   private int currentSlot = 1;
   private class_243 originPos = null;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public RtpDebug() {
      super(
         "/rtp debug",
         "Auto home setter + executes /rtp <region> and returns home",
         Category.SMP_UTILITIES
      );
   }

   @Override
   public String getDisplayName() {
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(27);
      var10000[1] = (byte)(-24);
      var10000[2] = (byte)(104);
      var10000[3] = (byte)(-82);
      var10000[4] = (byte)(-32);
      var10000[5] = (byte)(-34);
      var10000[6] = (byte)(-15);
      var10000[7] = (byte)(-3);
      var10000[8] = (byte)(41);
      var10000[9] = (byte)(-59);
      return $$v_s(var10000, -5217591171182505967L);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isInGame() && mc.field_1724 != null) {
         this.currentSlot = (Integer)this.homeSlot.get();
         this.originPos = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
         this.sendCommand("/delhome " + this.currentSlot);
         int var1 = Math.max(50, (Integer)this.delay.get());
         this.nextActionMs = System.currentTimeMillis() + var1;
         this.state = RtpDebug.State.WAITING_SETHOME;
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      this.state = RtpDebug.State.IDLE;
      this.nextActionMs = 0L;
      this.originPos = null;
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (!this.isInGame() || mc.field_1724 == null) {
         this.state = RtpDebug.State.IDLE;
         if (this.isEnabled()) {
            this.setEnabled(false);
         }
      } else if (this.state == RtpDebug.State.IDLE) {
         if (this.isEnabled()) {
            this.setEnabled(false);
         }
      } else {
         long var1 = System.currentTimeMillis();
         int var3 = Math.max(50, (Integer)this.delay.get());
         switch (this.state) {
            case WAITING_SETHOME:
               if (var1 < this.nextActionMs) {
                  return;
               }

               this.originPos = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
               this.sendCommand("/sethome " + this.currentSlot);
               this.nextActionMs = var1 + var3;
               this.state = RtpDebug.State.WAITING_RTP;
               break;
            case WAITING_RTP:
               if (var1 < this.nextActionMs) {
                  return;
               }

               RtpDebug.Region var7 = (RtpDebug.Region)this.region.get();
               this.sendCommand("/rtp " + var7.getArg());
               this.rtpSentTime = var1;
               this.nextActionMs = var1 + var3;
               this.state = RtpDebug.State.WAITING_TELEPORT;
               break;
            case WAITING_TELEPORT:
               boolean var6 = this.originPos != null
                  && this.originPos.method_1028(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321()) > 400.0;
               boolean var5 = var1 - this.rtpSentTime > 15000L;
               if (var6 || var5) {
                  if (var1 < this.nextActionMs) {
                     return;
                  }

                  this.sendCommand("/home " + this.currentSlot);
                  this.homeSentTime = var1;
                  this.nextActionMs = var1 + Math.max(1000, var3);
                  this.state = RtpDebug.State.WAITING_RETURN_HOME;
               }
               break;
            case WAITING_RETURN_HOME:
               boolean var4 = this.originPos != null
                  && this.originPos.method_1028(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321()) <= 100.0;
               if (var4) {
                  this.state = RtpDebug.State.IDLE;
                  this.setEnabled(false);
                  return;
               }

               if (var1 >= this.nextActionMs) {
                  if (var1 - this.homeSentTime < 15000L) {
                     this.sendCommand("/home " + this.currentSlot);
                     this.nextActionMs = var1 + 1500L;
                  } else {
                     this.state = RtpDebug.State.IDLE;
                     this.setEnabled(false);
                  }
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
      EU_WEST(
         "eu west", "EU West"
      ),
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

      public String getLabel() {
         return this.label;
      }

      @Override
      public String toString() {
         return this.label;
      }

      // $VF: synthetic method
      private static RtpDebug.Region[] $values() {
         RtpDebug.Region[] var10000 = new RtpDebug.Region[6];
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
      WAITING_SETHOME,
      WAITING_RTP,
      WAITING_TELEPORT,
      WAITING_RETURN_HOME;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static RtpDebug.State[] $values() {
         RtpDebug.State[] var10000 = new RtpDebug.State[5];
         var10000[0] = IDLE;
         var10000[1] = WAITING_SETHOME;
         var10000[2] = WAITING_RTP;
         var10000[3] = WAITING_TELEPORT;
         var10000[4] = WAITING_RETURN_HOME;
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
