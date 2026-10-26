package me.drake.vieclient.modules.player;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_2561;
import net.minecraft.class_634;

public class HomeSetter extends Module {
   private final IntSetting homeSlot = (IntSetting)this.addSetting(
      new IntSetting(
         "Home Slot",
         "Slot number to set",
         1,
         1,
         10
      )
   );
   private final IntSetting delayMs = (IntSetting)this.addSetting(
      new IntSetting(
         "Delay ms",
         "Delay before sethome",
         200,
         0,
         5000
      )
   );
   private final BoolSetting chatFeedback = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Chat Feedback",
         "Show confirmation message",
         true
      )
   );
   private static boolean running;
   private static long executeSetHomeAtMs;
   private static int slot;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public HomeSetter() {
      super(
         "HomeSetter",
         "Auto delete and set home at current position",
         Category.PLAYER
      );
      this.chatFeedback.setVisibleInGui(false);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (!running && this.isInGame()) {
         slot = (Integer)this.homeSlot.get();
         int var1 = Math.max(0, (Integer)this.delayMs.get());
         executeSetHomeAtMs = System.currentTimeMillis() + var1;
         running = true;
         this.sendCommand("/delhome " + slot);
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      running = false;
      executeSetHomeAtMs = 0L;
      slot = 0;
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (running && this.isInGame()) {
         if (System.currentTimeMillis() >= executeSetHomeAtMs) {
            this.sendCommand("/sethome " + slot);
            if ((Boolean)this.chatFeedback.get() && mc.field_1724 != null) {
               mc.field_1724.method_7353(class_2561.method_43470("§aHome " + slot + " deleted and set successfully!"), false);
            }

            running = false;
            this.setEnabled(false);
         }
      } else {
         if (running) {
            running = false;
            this.setEnabled(false);
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
}
