package me.drake.vieclient;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.auth.AuthManager;
import me.drake.vieclient.core.command.CommandManager;
import me.drake.vieclient.core.config.ConfigManager;
import me.drake.vieclient.core.config.ModerationConfigManager;
import me.drake.vieclient.core.event.EventBus;
import me.drake.vieclient.core.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_310;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class VieClientMod implements ClientModInitializer {
   public static final String MOD_ID = "vie_client";
   public static final String MOD_NAME = "Vie Client";
   public static final String VERSION = "1.0.0";
   public static final String AUTHOR = "Drake";
   public static final Logger LOGGER;
   private static VieClientMod INSTANCE;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static VieClientMod getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new VieClientMod();
      }

      return INSTANCE;
   }

   public void onInitializeClient() {
   }

   public EventBus getEventBus() {
      CoreEntry var1 = CoreEntry.getInstance();
      return var1 != null ? var1.getEventBus() : null;
   }

   public ModuleManager getModuleManager() {
      CoreEntry var1 = CoreEntry.getInstance();
      return var1 != null ? var1.getModuleManager() : null;
   }

   public CommandManager getCommandManager() {
      CoreEntry var1 = CoreEntry.getInstance();
      return var1 != null ? var1.getCommandManager() : null;
   }

   public ConfigManager getConfigManager() {
      CoreEntry var1 = CoreEntry.getInstance();
      return var1 != null ? var1.getConfigManager() : null;
   }

   public CoreEntry.FriendManager getFriendManager() {
      CoreEntry var1 = CoreEntry.getInstance();
      return var1 != null ? var1.getFriendManager() : null;
   }

   public AuthManager getAuthManager() {
      CoreEntry var1 = CoreEntry.getInstance();
      return var1 != null ? var1.getAuthManager() : AuthManager.getInstance();
   }

   public ModerationConfigManager getModerationConfigManager() {
      CoreEntry var1 = CoreEntry.getInstance();
      return var1 != null ? var1.getModerationConfigManager() : null;
   }

   public void openClickGuiFromKeyPress(class_310 client) {
      CoreEntry var2 = CoreEntry.getInstance();
      if (var2 != null) {
         var2.openClickGuiFromKeyPress(client);
      }
   }

   static {
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(108);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(-10);
      var10000[5] = (byte)(43);
      var10000[6] = (byte)(76);
      var10000[7] = (byte)(41);
      var10000[8] = (byte)(60);
      var10000[9] = (byte)(-42);
      LOGGER = LoggerFactory.getLogger($$v_s(var10000, 7216623730975559711L));
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
