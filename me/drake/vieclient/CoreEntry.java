package me.drake.vieclient;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.auth.AuthManager;
import me.drake.vieclient.auth.AutoUpdate;
import me.drake.vieclient.core.command.CommandManager;
import me.drake.vieclient.core.config.ConfigManager;
import me.drake.vieclient.core.config.ModerationConfigManager;
import me.drake.vieclient.core.event.EventBus;
import me.drake.vieclient.core.module.ModuleManager;
import me.drake.vieclient.gui.nvg.adapter.VieClient;
import me.drake.vieclient.gui.nvg.ui.NewClickGUI;
import me.drake.vieclient.gui.nvg.ui.components.VieOSScreen;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.FrostedBackground;
import me.drake.vieclient.render.VieMeshRenderer;
import me.drake.vieclient.render.ViePipelines;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.NVGImage;
import me.drake.vieclient.render.nvg.NVGRenderer;
import me.drake.vieclient.render.nvg.NanoVGResourceGeneration;
import me.drake.vieclient.render.nvg.PlayerHeadCache;
import me.drake.vieclient.security.SecurityGuard;
import me.drake.vieclient.util.Sfx;
import me.drake.vieclient.util.WindowIcon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.class_2960;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3264;
import net.minecraft.class_3300;
import net.minecraft.class_3675;
import net.minecraft.class_304.class_11900;
import net.minecraft.class_3675.class_307;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class CoreEntry implements ClientModInitializer {
   public static final String MOD_ID = "vie_client";
   public static final String MOD_NAME = "Vie Client";
   public static final String VERSION = "1.0.1";
   public static final String AUTHOR = "Drake";
   public static final Logger LOGGER;
   private static CoreEntry INSTANCE;
   private EventBus eventBus;
   private ModuleManager moduleManager;
   private CommandManager commandManager;
   private ConfigManager configManager;
   private CoreEntry.FriendManager friendManager;
   private AuthManager authManager;
   private ModerationConfigManager moderationConfigManager;
   public static final boolean DEV_MODE = true;
   private class_304 guiKeyBinding;
   private boolean guiHotkeyHeld = false;
   private long lastGuiOpenAtMs = 0L;
   private static final long GUI_OPEN_COOLDOWN_MS = 250L;
   private long securityTickCounter = 0L;
   private boolean hadPlayerLastTick = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static boolean isAuthEnabled() {
      return false;
   }

   public void onInitializeClient() {
      INSTANCE = this;
      this.printBanner();
      LOGGER.info(
         "[VieClient] Initializing SecurityGuard..."
      );

      try {
         SecurityGuard.initialize();
      } catch (Throwable var9) {
         LOGGER.error(
            "[VieClient] SecurityGuard initialization error: {}",
            var9.getMessage(),
            var9
         );
      }

      LOGGER.info(
         "[VieClient] Initializing AuthManager..."
      );

      try {
         this.authManager = AuthManager.getInstance();
         this.authManager.initialize();
         boolean var1 = this.authManager.awaitInitialValidation(2500L);
         LOGGER.info(
            "[VieClient] AuthManager initial validation: {} (status: {})",
            var1
               ? "ready"
               : "deferred",
            this.authManager.getStatusSnapshot().status()
         );
      } catch (Throwable var8) {
         LOGGER.error(
            "[VieClient] AuthManager initialization error: {}",
            var8.getMessage(),
            var8
         );
      }

      LOGGER.info(
         "[VieClient] Initializing AutoUpdate..."
      );
      AutoUpdate var10 = new AutoUpdate();
      var10.setEnabled(false);
      var10.checkAndApply();
      LOGGER.info(
         "[VieClient] Initializing EventBus..."
      );
      this.eventBus = new EventBus();
      LOGGER.info(
         "[VieClient] Initializing Ultralight (HTML title screen engine)..."
      );

      try {
         Class var2 = Class.forName(
            "net.ostore.ultralight.UltralightEngine"
         );
         var2.getMethod("init").invoke(null);
         LOGGER.info(
            "[VieClient] Ultralight engine registered successfully."
         );
      } catch (Throwable var7) {
         LOGGER.error(
            "[VieClient] UltralightEngine.init() failed — HTML title screen will be unavailable.",
            var7
         );
      }

      LOGGER.info(
         "[VieClient] Registering sound effects..."
      );
      Sfx.register();
      LOGGER.info(
         "[VieClient] Initializing ConfigManager..."
      );
      this.configManager = new ConfigManager();
      LOGGER.info(
         "[VieClient] Initializing FriendManager..."
      );
      this.friendManager = new CoreEntry.FriendManager();
      LOGGER.info(
         "[VieClient] Initializing ModuleManager..."
      );
      this.moduleManager = new ModuleManager();
      LOGGER.info(
         "[VieClient] Initializing CommandManager..."
      );
      this.commandManager = new CommandManager();
      LOGGER.info(
         "[VieClient] Initializing ModerationConfigManager..."
      );
      this.moderationConfigManager = new ModerationConfigManager();
      this.configManager.load();
      this.configManager.save();
      ClientSettings var11 = ClientSettings.get();
      int var3 = var11 != null ? var11.getClickGuiKey() : 344;
      this.guiKeyBinding = KeyBindingHelper.registerKeyBinding(
         new class_304(
            "key.vie_client.open_gui",
            var3,
            class_11900.field_62556
         )
      );
      ClientLifecycleEvents.CLIENT_STARTED.register(this::onClientStarted);
      ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
      ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStopping);
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         try {
            if (this.configManager != null) {
               this.configManager.save();
            }
         } catch (Throwable var2x) {
         }
      }, "vie-config-shutdown"));
      ResourceManagerHelper.get(class_3264.field_14188)
         .registerReloadListener(
            new SimpleSynchronousResourceReloadListener() {
               // $VF: synthetic field
               private static final boolean $$vie_p = true;

               public class_2960 getFabricId() {
                  byte[] var10000 = new byte[10];
                  var10000[0] = (byte)(78);
                  var10000[1] = (byte)(-13);
                  var10000[2] = (byte)(121);
                  var10000[3] = (byte)(-127);
                  var10000[4] = (byte)(115);
                  var10000[5] = (byte)(-126);
                  var10000[6] = (byte)(108);
                  var10000[7] = (byte)(-59);
                  var10000[8] = (byte)(62);
                  var10000[9] = (byte)(-42);
                  String var1 = $$v_s(var10000, -8645168931034378211L);
                  byte[] var10001 = new byte[17];
                  var10001[0] = (byte)(72);
                  var10001[1] = (byte)(-13);
                  var10001[2] = (byte)(108);
                  var10001[3] = (byte)(-69);
                  var10001[4] = (byte)(124);
                  var10001[5] = (byte)(-121);
                  var10001[6] = (byte)(107);
                  var10001[7] = (byte)(-59);
                  var10001[8] = (byte)(15);
                  var10001[9] = (byte)(-48);
                  var10001[10] = (byte)(-127);
                  var10001[11] = (byte)(-102);
                  var10001[12] = (byte)(39);
                  var10001[13] = (byte)(87);
                  var10001[14] = (byte)(73);
                  var10001[15] = (byte)(-67);
                  var10001[16] = (byte)(26);
                  return class_2960.method_60655(var1, $$v_s(var10001, -8645168931034378211L));
               }

               public void method_14491(class_3300 manager) {
                  CoreEntry.LOGGER
                     .info(
                        "[VieClient] Resources reloaded, invalidating pipelines and render caches..."
                     );
                  ViePipelines.isPrecompiled = false;
                  ViePipelines.precompile(manager);
                  NanoVGResourceGeneration.invalidateGlobal();
                  NVGImage.invalidateResourceImages();
                  IconManager.invalidate();
                  PlayerHeadCache.invalidate();
                  NVGRenderer.invalidate();
                  VieMeshRenderer.invalidate();
                  FrostedBackground.invalidate();
               }

               // $VF: synthetic method
               private static String $$v_s(byte[] var0, long var1) {
                  byte[] var3 = new byte[var0.length];

                  for (int var4 = 0; var4 < var0.length; var4++) {
                     var3[var4] = (byte)(
                        var0[var4]
                           ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                           ^ (var4 + 1) * (37)
                     );
                  }

                  return new String(var3, StandardCharsets.UTF_8);
               }
            }
         );
      LOGGER.info(
         "[VieClient] Vie Client v{} by {} — Loaded successfully!",
         "1.0.1",
         "Drake"
      );

      try {
         Class var4 = Class.forName(
            "me.drake.vieclient.loader.CrossCheckManager"
         );
         Method var5 = var4.getMethod("markGatePassed", int.class);
         var5.invoke(null, 4);
      } catch (Throwable var6) {
      }
   }

   private void onClientTick(class_310 client) {
      this.handleGuiHotkey(client);
      WindowIcon.tick(client);
      if (client != null && !ViePipelines.isPrecompiled) {
         try {
            ViePipelines.precompile();
         } catch (Throwable var4) {
            LOGGER.error(
               "[VieClient] Pipeline precompile tick attempt failed",
               var4
            );
         }
      }

      if (this.configManager != null) {
         this.configManager.tickAutosave();
         if (client != null && client.field_1724 == null && this.hadPlayerLastTick) {
            this.configManager.save();
         }

         this.hadPlayerLastTick = client != null && client.field_1724 != null;
      }

      if (this.authManager != null) {
         this.authManager.tick();
      }

      this.securityTickCounter++;
      if (this.securityTickCounter % 100L == 0L) {
         try {
            SecurityGuard.verifyIntegrity();
         } catch (Throwable var3) {
            LOGGER.error(
               "[VieClient] SecurityGuard periodic check error",
               var3
            );
         }
      }

      if (this.moduleManager != null) {
         this.moduleManager.onTick();
      }

      if (client == null || client.field_1724 == null || client.field_1687 == null) {
         ;
      }
   }

   private void onClientStarted(class_310 client) {
      if (client != null) {
         if (!ViePipelines.isPrecompiled) {
            try {
               ViePipelines.precompile();
            } catch (Throwable var4) {
            }

            try {
               WindowIcon.apply(client);
            } catch (Throwable var3) {
               byte[] var10001 = new byte[41];
               var10001[0] = (byte)(64);
               var10001[1] = (byte)(-52);
               var10001[2] = (byte)(117);
               var10001[3] = (byte)(-69);
               var10001[4] = (byte)(-50);
               var10001[5] = (byte)(-92);
               var10001[6] = (byte)(81);
               var10001[7] = (byte)(16);
               var10001[8] = (byte)(29);
               var10001[9] = (byte)(-42);
               var10001[10] = (byte)(-71);
               var10001[11] = (byte)(-42);
               var10001[12] = (byte)(-109);
               var10001[13] = (byte)(113);
               var10001[14] = (byte)(121);
               var10001[15] = (byte)(97);
               var10001[16] = (byte)(46);
               var10001[17] = (byte)(46);
               var10001[18] = (byte)(-20);
               var10001[19] = (byte)(-38);
               var10001[20] = (byte)(82);
               var10001[21] = (byte)(24);
               var10001[22] = 1;
               var10001[23] = (byte)(75);
               var10001[24] = (byte)(-48);
               var10001[25] = (byte)(102);
               var10001[26] = (byte)(-11);
               var10001[27] = (byte)(42);
               var10001[28] = (byte)(105);
               var10001[29] = (byte)(96);
               var10001[30] = (byte)(55);
               var10001[31] = (byte)(-108);
               var10001[32] = (byte)(-107);
               var10001[33] = (byte)(94);
               var10001[34] = (byte)(19);
               var10001[35] = (byte)(9);
               var10001[36] = (byte)(77);
               var10001[37] = 1;
               var10001[38] = (byte)(-5);
               var10001[39] = (byte)(-6);
               var10001[40] = (byte)(-67);
               LOGGER.error($$v_s(var10001, 6717987682996441150L), var3);
            }
         }
      }
   }

   public void updateGuiKeyBinding(int newKey) {
      if (this.guiKeyBinding != null) {
         try {
            if (newKey > 0 && newKey < (1000)) {
               this.guiKeyBinding.method_1422(class_307.field_1668.method_1447(newKey));
               class_304.method_1426();
            } else if (newKey <= 0) {
               this.guiKeyBinding.method_1422(class_3675.field_16237);
               class_304.method_1426();
            }
         } catch (Throwable var3) {
         }
      }
   }

   private void handleGuiHotkey(class_310 client) {
      if (client != null && client.method_22683() != null) {
         ClientSettings var2 = ClientSettings.get();
         if (var2 != null) {
            if (this.guiKeyBinding != null) {
               try {
                  int var3 = class_3675.method_15981(this.guiKeyBinding.method_1428()).method_1444();
                  if (var3 != var2.getClickGuiKey() && var3 != 0) {
                     var2.setClickGuiKey(var3);
                  }
               } catch (Throwable var10) {
               }
            }

            int var11 = var2.getClickGuiKey();
            long var4 = client.method_22683().method_4490();
            boolean var6 = false;
            if (var11 >= (1000)) {
               var6 = GLFW.glfwGetMouseButton(var4, var11 - (1000)) == 1;
            } else if (var11 > 0) {
               var6 = GLFW.glfwGetKey(var4, var11) == 1;
            }

            boolean var7 = GLFW.glfwGetKey(var4, 260) == 1;
            boolean var8 = var6 || var7;
            boolean var9 = var8 && !this.guiHotkeyHeld;
            this.guiHotkeyHeld = var8;
            if (this.guiKeyBinding != null && this.guiKeyBinding.method_1436() || var9) {
               this.openClickGui(client);
            }
         }
      }
   }

   public void openClickGuiFromKeyPress(class_310 client) {
      this.openClickGui(client);
   }

   private void openClickGui(class_310 client) {
      if (client != null) {
         long var2 = System.currentTimeMillis();
         if (var2 - this.lastGuiOpenAtMs >= 250L) {
            if (!(client.field_1755 instanceof NewClickGUI) && !(client.field_1755 instanceof VieOSScreen)) {
               this.lastGuiOpenAtMs = var2;
               VieClient.INSTANCE.GUI = new NewClickGUI();
               VieClient.INSTANCE.GUI.reset();
               client.method_1507(VieClient.INSTANCE.GUI);
               me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient.rememberOpenedFrom();
               Sfx.play(Sfx.TAP);
            } else {
               this.lastGuiOpenAtMs = var2;
               client.method_1507(null);
               Sfx.play(Sfx.TAP);
            }
         }
      }
   }

   private void onClientStopping(class_310 client) {
      if (this.configManager != null) {
         this.configManager.save();
      }
   }

   private void printBanner() {
      String var1 = "
██╗   ██╗██╗███████╗      ██████╗██╗     ██╗███████╗███╗   ██╗████████╗
██║   ██║██║██╔════╝     ██╔════╝██║     ██║██╔════╝████╗  ██║╚══██╔══╝
██║   ██║██║█████╗       ██║     ██║     ██║█████╗  ██╔██╗ ██║   ██║
╚██╗ ██╔╝██║██╔══╝       ██║     ██║     ██║██╔══╝  ██║╚██╗██║   ██║
 ╚████╔╝ ██║███████╗     ╚██████╗███████╗██║███████╗██║ ╚████║   ██║
  ╚═══╝  ╚═╝╚══════╝      ╚═════╝╚══════╝╚═╝╚══════╝╚═╝  ╚═══╝   ╚═╝
                           by Drake | v1.0.0
";
      LOGGER.info(var1);
   }

   public static CoreEntry getInstance() {
      return INSTANCE;
   }

   public EventBus getEventBus() {
      return this.eventBus;
   }

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public CommandManager getCommandManager() {
      return this.commandManager;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   public CoreEntry.FriendManager getFriendManager() {
      return this.friendManager;
   }

   public AuthManager getAuthManager() {
      if (this.authManager == null) {
         this.authManager = AuthManager.getInstance();
      }

      return this.authManager;
   }

   public ModerationConfigManager getModerationConfigManager() {
      return this.moderationConfigManager;
   }

   static {
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(77);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(121);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(-50);
      var10000[5] = (byte)(-92);
      var10000[6] = (byte)(81);
      var10000[7] = (byte)(16);
      var10000[8] = (byte)(29);
      var10000[9] = (byte)(-42);
      LOGGER = LoggerFactory.getLogger($$v_s(var10000, 6717987682996441150L));
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

   public static class FriendManager {
      private final List friends = new ArrayList();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public void add(String name) {
         if (!this.friends.contains(name.toLowerCase())) {
            this.friends.add(name.toLowerCase());
         }
      }

      public void remove(String name) {
         this.friends.remove(name.toLowerCase());
      }

      public boolean isFriend(String name) {
         return this.friends.contains(name.toLowerCase());
      }

      public List getFriends() {
         return new ArrayList(this.friends);
      }
   }
}
