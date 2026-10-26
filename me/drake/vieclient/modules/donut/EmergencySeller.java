package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.StringSetting;

public class EmergencySeller extends Module {
   private final StringSetting cmd1 = (StringSetting)this.addSetting(
      new StringSetting(
         "PanicCmd1",
         "Emergency sell command 1",
         "/sell all"
      )
   );
   private final StringSetting cmd2 = (StringSetting)this.addSetting(
      new StringSetting(
         "PanicCmd2",
         "Emergency sell command 2",
         ""
      )
   );
   private final StringSetting cmd3 = (StringSetting)this.addSetting(
      new StringSetting(
         "PanicCmd3",
         "Emergency sell command 3",
         ""
      )
   );
   private final IntSetting cmdDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "CmdDelay",
         "Ticks between commands",
         5,
         1,
         20
      )
   );
   private boolean triggered = false;
   private int cmdIndex = 0;
   private int delayTicks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public EmergencySeller() {
      super(
         "EmergencySeller",
         "Panic sell - runs cmds instantly (DonutSMP)",
         Category.SMP_UTILITIES
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.triggered = true;
      this.cmdIndex = 0;
      this.delayTicks = 0;
   }

   @Override
   public void onTick() {
      if (this.isInGame() && this.triggered) {
         if (this.delayTicks > 0) {
            this.delayTicks--;
         } else {
            String[] var10000 = new String[3];
            var10000[0] = (String)this.cmd1.get();
            var10000[1] = (String)this.cmd2.get();
            var10000[2] = (String)this.cmd3.get();
            String[] var1 = var10000;

            while (this.cmdIndex < var1.length) {
               String var2 = var1[this.cmdIndex++];
               if (var2 != null && !var2.isBlank()) {
                  this.sendCmd(var2);
                  this.delayTicks = (Integer)this.cmdDelay.get();
                  return;
               }
            }

            this.triggered = false;
            this.setEnabled(false);
         }
      }
   }

   private void sendCmd(String cmd) {
      if (mc.field_1724 != null && mc.field_1724.field_3944 != null) {
         if (cmd.startsWith("/")) {
            mc.field_1724.field_3944.method_45730(cmd.substring(1));
         } else {
            mc.field_1724.field_3944.method_45729(cmd);
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
