package me.drake.vieclient.modules.client;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.gui.nvg.ui.EmoteWheel;

public final class Emote extends Module {
   private static Emote instance;
   private final BoolSetting loop = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Loop",
         "Keep the animation playing after it finishes instead of stopping once",
         true
      )
   );
   private final BoolSetting stopOnMove = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Stop On Move",
         "Cancel the animation as soon as you move, jump or sneak",
         true
      )
   );
   private final BoolSetting holdToSelect = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Hold To Select",
         "Hold the key and aim with the mouse; releasing plays what you aimed at",
         true
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Emote() {
      super(
         "Emote",
         "Open the emote wheel",
         Category.CUSTOMIZE,
         -1
      );
      instance = this;
   }

   @Override
   public void toggle() {
      EmoteWheel.open();
   }

   @Override
   public boolean isTriggerOnBind() {
      return true;
   }

   @Override
   public void trigger() {
      EmoteWheel.open();
   }

   @Override
   public boolean isVisibleInHud() {
      return false;
   }

   public static boolean shouldLoop() {
      return instance == null || (Boolean)instance.loop.get();
   }

   public static boolean shouldStopOnMove() {
      return instance == null || (Boolean)instance.stopOnMove.get();
   }

   public static boolean holdToSelect() {
      return instance != null && (Boolean)instance.holdToSelect.get();
   }

   public static int bindCode() {
      return instance == null ? -1 : (Integer)instance.getKeybind().get();
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
