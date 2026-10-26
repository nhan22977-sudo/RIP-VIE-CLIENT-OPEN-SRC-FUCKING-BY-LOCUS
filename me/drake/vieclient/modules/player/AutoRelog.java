package me.drake.vieclient.modules.player;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.DoubleSetting;
import net.minecraft.class_2561;
import net.minecraft.class_412;
import net.minecraft.class_442;
import net.minecraft.class_500;
import net.minecraft.class_639;
import net.minecraft.class_642;
import net.minecraft.class_642.class_8678;

public class AutoRelog extends Module {
   private final DoubleSetting delay = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Delay",
         "Delay before reconnecting in seconds",
         3.0,
         0.5,
         30.0,
         0.5
      )
   );
   private AutoRelog.State state = AutoRelog.State.IDLE;
   private class_642 lastServer = null;
   private long reconnectAtTimeMs = 0L;
   private long connectingStartTimeMs = 0L;
   private int cooldownTicks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoRelog() {
      super(
         "Auto Relog",
         "Automatically disconnects and reconnects when falling below Y -5",
         Category.SMP_UTILITIES
      );
   }

   public DoubleSetting getDelaySetting() {
      return this.delay;
   }

   public AutoRelog.State getState() {
      return this.state;
   }

   public void setState(AutoRelog.State state) {
      this.state = statex;
   }

   public class_642 getLastServer() {
      return this.lastServer;
   }

   public void setLastServer(class_642 lastServer) {
      this.lastServer = lastServerx;
   }

   public int getCooldownTicks() {
      return this.cooldownTicks;
   }

   public void setCooldownTicks(int cooldownTicks) {
      this.cooldownTicks = cooldownTicksx;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetRoutine();
      this.cooldownTicks = 40;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.resetRoutine();
   }

   @Override
   public boolean canTickOutOfGame() {
      return this.state == AutoRelog.State.WAITING_RECONNECT || this.state == AutoRelog.State.CONNECTING;
   }

   @Override
   public void onTick() {
      boolean var1 = mc != null && mc.field_1724 != null && mc.field_1687 != null;
      if (!var1) {
         this.handleOutOfGame();
      } else {
         this.handleInGame();
      }
   }

   private void handleOutOfGame() {
      long var1 = System.currentTimeMillis();
      if (this.state == AutoRelog.State.WAITING_RECONNECT) {
         if (mc.field_1755 instanceof class_500 || mc.field_1755 instanceof class_442) {
            VieClientMod.LOGGER
               .info(
                  "[Auto Relog] User returned to menu screen. Cancelling auto reconnect."
               );
            this.state = AutoRelog.State.IDLE;
            return;
         }

         if (var1 >= this.reconnectAtTimeMs) {
            this.state = AutoRelog.State.CONNECTING;
            this.connectingStartTimeMs = var1;
            this.reconnect();
         }
      } else if (this.state == AutoRelog.State.CONNECTING) {
         if (var1 - this.connectingStartTimeMs > 30000L) {
            VieClientMod.LOGGER
               .warn(
                  "[Auto Relog] Reconnect attempt timed out. Resetting to IDLE."
               );
            this.state = AutoRelog.State.IDLE;
         }
      } else if (this.state == AutoRelog.State.RECONNECTED_COOLDOWN) {
         this.state = AutoRelog.State.IDLE;
      }
   }

   private void handleInGame() {
      class_642 var1 = mc.method_1558();
      if (var1 != null) {
         this.lastServer = var1;
      }

      if (this.state == AutoRelog.State.CONNECTING || this.state == AutoRelog.State.WAITING_RECONNECT) {
         this.state = AutoRelog.State.RECONNECTED_COOLDOWN;
         this.cooldownTicks = Math.max(100, (int)((Double)this.delay.get() * 20.0));
         VieClientMod.LOGGER
            .info(
               "[Auto Relog] Successfully reconnected. Grace period active ({} ticks).",
               this.cooldownTicks
            );
      } else if (this.state != AutoRelog.State.RECONNECTED_COOLDOWN && this.cooldownTicks <= 0) {
         if (!mc.field_1724.method_29504() && !(mc.field_1724.method_6032() <= 0.0F)) {
            double var2 = mc.field_1724.method_5668() != null ? mc.field_1724.method_5668().method_23318() : mc.field_1724.method_23318();
            if (var2 <= -5.0) {
               this.triggerRelog(var2);
            }
         }
      } else {
         if (this.cooldownTicks > 0) {
            this.cooldownTicks--;
         }

         if (this.cooldownTicks <= 0) {
            this.state = AutoRelog.State.IDLE;
         }
      }
   }

   public void triggerRelog(double y) {
      if (!mc.method_1542()) {
         class_642 var3 = mc.method_1558();
         if (var3 == null && this.lastServer != null) {
            var3 = this.lastServer;
         }

         if (var3 == null
            && mc.method_1562() != null
            && mc.method_1562().method_48296() != null
            && mc.method_1562().method_48296().method_10755() instanceof InetSocketAddress var5) {
            var3 = new class_642(
               "Server", var5.getHostString() + ":" + var5.getPort(), class_8678.field_45611
            );
         }

         if (var3 == null) {
            VieClientMod.LOGGER
               .warn(
                  "[Auto Relog] Cannot determine server address to reconnect to."
               );
         } else {
            this.lastServer = var3;
            this.state = AutoRelog.State.WAITING_RECONNECT;
            double var6 = Math.max(0.1, (Double)this.delay.get());
            this.reconnectAtTimeMs = System.currentTimeMillis() + (long)(var6 * 1000.0);
            VieClientMod.LOGGER
               .info(
                  "[Auto Relog] Player fell to Y = {} (<= -5.0). Disconnecting and scheduling reconnect in {}s.",
                  String.format(Locale.US, "%.2f", y),
                  var6
               );
            if (mc.method_1562() != null && mc.method_1562().method_48296() != null) {
               mc.method_1562()
                  .method_48296()
                  .method_10747(
                     class_2561.method_43470(
                        String.format(
                           Locale.US,
                           "§c[Auto Relog] §fY <= -5 (%.1f). Reconnecting in %.1fs...",
                           y,
                           var6
                        )
                     )
                  );
            }
         }
      }
   }

   public void reconnect() {
      if (this.lastServer != null && mc != null) {
         try {
            VieClientMod.LOGGER
               .info(
                  "[Auto Relog] Reconnecting to {}...",
                  this.lastServer.field_3761
               );
            class_639 var1 = class_639.method_2950(this.lastServer.field_3761);
            class_500 var2 = new class_500(new class_442());
            class_412.method_36877(var2, mc, var1, this.lastServer, false, null);
         } catch (Throwable var3) {
            VieClientMod.LOGGER
               .error(
                  "[Auto Relog] Failed to reconnect to {}",
                  this.lastServer.field_3761,
                  var3
               );
            this.state = AutoRelog.State.IDLE;
         }
      } else {
         this.state = AutoRelog.State.IDLE;
      }
   }

   private void resetRoutine() {
      this.state = AutoRelog.State.IDLE;
      this.reconnectAtTimeMs = 0L;
      this.connectingStartTimeMs = 0L;
      this.cooldownTicks = 0;
   }

   @Override
   public String getInfo() {
      double var1 = (Double)this.delay.get();
      if (var1 == (long)var1) {
         byte[] var3 = new byte[3];
         var3[0] = (byte)(14);
         var3[1] = (byte)(-2);
         var3[2] = (byte)(111);
         return String.format(Locale.US, $$v_s(var3, 6618405494689353742L), (long)var1);
      } else {
         byte[] var10001 = new byte[5];
         var10001[0] = (byte)(14);
         var10001[1] = (byte)(-76);
         var10001[2] = (byte)(45);
         var10001[3] = (byte)(-72);
         var10001[4] = (byte)(113);
         return String.format(Locale.US, $$v_s(var10001, 6618405494689353742L), var1);
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

   public enum State {
      IDLE,
      WAITING_RECONNECT,
      CONNECTING,
      RECONNECTED_COOLDOWN;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoRelog.State[] $values() {
         AutoRelog.State[] var10000 = new AutoRelog.State[4];
         var10000[0] = IDLE;
         var10000[1] = WAITING_RECONNECT;
         var10000[2] = CONNECTING;
         var10000[3] = RECONNECTED_COOLDOWN;
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
         IDLE = new AutoRelog.State();
         var10002 = new byte[17];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-40);
         var10002[5] = (byte)(-50);
         var10002[6] = (byte)(-123);
         var10002[7] = (byte)(113);
         var10002[8] = (byte)(5);
         var10002[9] = (byte)(-25);
         var10002[10] = (byte)(-89);
         var10002[11] = (byte)(-71);
         var10002[12] = (byte)(-121);
         var10002[13] = (byte)(22);
         var10002[14] = (byte)(-81);
         var10002[15] = (byte)(21);
         var10002[16] = (byte)(59);
         WAITING_RECONNECT = new AutoRelog.State();
         var10002 = new byte[10];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-44);
         var10002[5] = (byte)(-61);
         var10002[6] = (byte)(-106);
         var10002[7] = (byte)(103);
         var10002[8] = (byte)(25);
         var10002[9] = (byte)(-27);
         CONNECTING = new AutoRelog.State();
         var10002 = new byte[20];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(95);
         var10002[3] = (byte)(-111);
         var10002[4] = (byte)(-33);
         var10002[5] = (byte)(-50);
         var10002[6] = (byte)(-121);
         var10002[7] = (byte)(109);
         var10002[8] = (byte)(3);
         var10002[9] = (byte)(-25);
         var10002[10] = (byte)(-96);
         var10002[11] = (byte)(-87);
         var10002[12] = (byte)(-118);
         var10002[13] = (byte)(23);
         var10002[14] = (byte)(-91);
         var10002[15] = (byte)(26);
         var10002[16] = (byte)(43);
         var10002[17] = (byte)(5);
         var10002[18] = (byte)(-101);
         var10002[19] = (byte)(-32);
         RECONNECTED_COOLDOWN = new AutoRelog.State();
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
