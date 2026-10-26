package me.drake.vieclient.modules.client;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.i18n.Language;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.KeybindSetting;
import me.drake.vieclient.core.setting.StringSetting;
import me.drake.vieclient.gui.nvg.adapter.modules.client.Sounds;
import me.drake.vieclient.gui.nvg.adapter.modules.client.VieClient;
import me.drake.vieclient.render.nvg.NanoVGFontManager;
import me.drake.vieclient.util.RainbowUtil;
import net.minecraft.class_1937;

public class ClientSettings extends Module {
   private static final double LOCKED_GUI_SCALE = 1.72;
   private static final long RPC_RECONNECT_INTERVAL_MS = 10000L;
   private static final double TEXT_RGB_SPEED = 1.0;
   private static final double TEXT_RGB_SATURATION = 1.0;
   private static final int OBSIDIAN_PRIMARY = -7643914;
   private static final int OBSIDIAN_SECONDARY = -9623335;
   private static final int OBSIDIAN_TEXT = -460036;
   private final EnumSetting moduleListPos = (EnumSetting)this.addSetting(
      new EnumSetting(
         "ModuleList",
         "Module list position",
         ClientSettings.HudCorner.TOP_RIGHT
      )
   );
   private final EnumSetting watermarkPos = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Watermark",
         "Watermark position",
         ClientSettings.WatermarkPos.TOP_LEFT
      )
   );
   private final BoolSetting showInfoBar = (BoolSetting)this.addSetting(
      new BoolSetting(
         "InfoBar",
         "Show FPS/Ping bar",
         true
      )
   );
   private final BoolSetting showCoords = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Coordinates",
         "Show coordinate bar",
         true
      )
   );
   private final BoolSetting showNotifs = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Notifications",
         "Show module toggle notifications",
         true
      )
   );
   private final BoolSetting guiFade = (BoolSetting)this.addSetting(
      new BoolSetting(
         "GuiFade",
         "Enable opening/closing fade animation",
         true
      )
   );
   private final IntSetting topInfoOffsetX = (IntSetting)this.addSetting(
      new IntSetting(
         "TopInfoOffsetX",
         "Top info horizontal offset",
         0,
         -4000,
         4000,
         1
      )
   );
   private final IntSetting topInfoOffsetY = (IntSetting)this.addSetting(
      new IntSetting(
         "TopInfoOffsetY",
         "Top info vertical offset",
         0,
         -4000,
         4000,
         1
      )
   );
   private final IntSetting coordsOffsetX = (IntSetting)this.addSetting(
      new IntSetting(
         "CoordsOffsetX",
         "Coordinates HUD horizontal offset",
         0,
         -4000,
         4000,
         1
      )
   );
   private final IntSetting coordsOffsetY = (IntSetting)this.addSetting(
      new IntSetting(
         "CoordsOffsetY",
         "Coordinates HUD vertical offset",
         0,
         -4000,
         4000,
         1
      )
   );
   private final IntSetting moduleListOffsetX = (IntSetting)this.addSetting(
      new IntSetting(
         "ModuleListOffsetX",
         "Module list horizontal offset",
         0,
         -4000,
         4000,
         1
      )
   );
   private final IntSetting moduleListOffsetY = (IntSetting)this.addSetting(
      new IntSetting(
         "ModuleListOffsetY",
         "Module list vertical offset",
         0,
         -4000,
         4000,
         1
      )
   );
   private final IntSetting hotkeysOffsetX = (IntSetting)this.addSetting(
      new IntSetting(
         "HotkeysOffsetX",
         "Hotkeys panel horizontal offset",
         0,
         -4000,
         4000,
         1
      )
   );
   private final IntSetting hotkeysOffsetY = (IntSetting)this.addSetting(
      new IntSetting(
         "HotkeysOffsetY",
         "Hotkeys panel vertical offset",
         0,
         -4000,
         4000,
         1
      )
   );
   private final IntSetting hotkeysScale = (IntSetting)this.addSetting(
      new IntSetting(
         "HotkeysScale",
         "Hotkeys panel scale",
         10,
         6,
         20,
         1
      )
   );
   private final EnumSetting language = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Language",
         "Client display language (ENG / VIE)",
         ClientSettings.LanguageMode.ENG
      )
   );
   private final EnumSetting fontMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Font",
         "Client UI font (Default / Minecraft)",
         ClientSettings.FontMode.DEFAULT
      )
   );
   private final BoolSetting interactionSfx = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Interaction SFX",
         "Play sound effects on UI clicks and interactions",
         true
      )
   );
   private final KeybindSetting clickGuiKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "ClickGuiKey",
         "Key to open ClickGUI",
         344
      )
   );
   private final EnumSetting guiStyle = new EnumSetting(
      "GuiStyle",
      "ClickGUI visual style",
      VieClient.GuiStyleMode.CLEAN
   );
   private final EnumSetting clickGuiMode = new EnumSetting(
      "ClickGuiMode",
      "ClickGUI display mode",
      VieClient.ClickGuiMode.DASHBOARD
   );
   private final EnumSetting themePreset = (EnumSetting)this.addSetting(
      new EnumSetting(
         "ThemePreset",
         "Glass theme preset",
         ClientSettings.ThemePreset.OBSIDIAN
      )
   );
   private final EnumSetting textColorMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "TextColorMode",
         "Text color mode",
         ClientSettings.TextColorMode.STATIC
      )
   );
   private final BoolSetting discordRPC;
   private final StringSetting rpcDetail;
   private DiscordPresence discord;
   private boolean wasRpcEnabled;
   private long lastRpcConnectAttemptAtMs;
   private static ClientSettings instance;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ClientSettings() {
      super(
         "ClientSettings",
         "Personalize your client",
         Category.CLIENT
      );
      this.moduleListPos.setVisibleInGui(false);
      this.watermarkPos.setVisibleInGui(false);
      this.topInfoOffsetX.setVisibleInGui(false);
      this.topInfoOffsetY.setVisibleInGui(false);
      this.coordsOffsetX.setVisibleInGui(false);
      this.coordsOffsetY.setVisibleInGui(false);
      this.moduleListOffsetX.setVisibleInGui(false);
      this.moduleListOffsetY.setVisibleInGui(false);
      this.hotkeysOffsetX.setVisibleInGui(false);
      this.hotkeysOffsetY.setVisibleInGui(false);
      this.hotkeysScale.setVisibleInGui(false);
      this.themePreset.setVisibleInGui(false);
      this.discordRPC = (BoolSetting)this.addSetting(
         new BoolSetting(
            "DiscordRPC",
            "Show Discord Rich Presence",
            false
         )
      );
      this.rpcDetail = (StringSetting)this.addSetting(
         new StringSetting(
            "RPCDetail",
            "Discord status detail",
            "Playing Vie Client"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            public void set(String value) {
               if (value != null) {
                  if (value.contains("Vie Cheats Client")) {
                     value = value.replace(
                        "Vie Cheats Client",
                        "Vie Client"
                     );
                  }

                  if (value.contains("Vie Cheats")) {
                     value = value.replace(
                        "Vie Cheats",
                        "Vie Client"
                     );
                  }
               }

               super.set(value);
            }

            public String get() {
               String var1 = (String)super.get();
               if (var1 != null) {
                  boolean var2 = false;
                  if (var1.contains("Vie Cheats Client")) {
                     var1 = var1.replace(
                        "Vie Cheats Client",
                        "Vie Client"
                     );
                     var2 = true;
                  }

                  if (var1.contains("Vie Cheats")) {
                     var1 = var1.replace(
                        "Vie Cheats",
                        "Vie Client"
                     );
                     var2 = true;
                  }

                  if (var2) {
                     super.set(var1);
                  }
               }

               return var1;
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
      this.wasRpcEnabled = false;
      this.lastRpcConnectAttemptAtMs = 0L;
      instance = this;
      this.rpcDetail.setVisibleInGui(false);
      this.clickGuiKey.onChanged(key -> {
         try {
            CoreEntry var1 = CoreEntry.getInstance();
            if (var1 != null) {
               var1.updateGuiKeyBinding(key);
            }
         } catch (Throwable var2) {
         }
      });
      this.language.onChanged(mode -> {
         Localization.setLanguage(mode == ClientSettings.LanguageMode.ENG ? Language.ENG : Language.VIE);
         this.requestConfigSave();
      });
      this.fontMode.onChanged(mode -> {
         NanoVGFontManager.setUseMinecraftFont(mode == ClientSettings.FontMode.MINECRAFT);
         this.requestConfigSave();
      });
      this.interactionSfx.onChanged(on -> {
         Sounds.instance.soundEnabled = on;
         this.requestConfigSave();
      });
      Localization.addListener(lang -> {
         ClientSettings.LanguageMode var2 = lang == Language.ENG ? ClientSettings.LanguageMode.ENG : ClientSettings.LanguageMode.VIE;
         if (this.language.get() != var2) {
            this.language.set(var2);
            this.requestConfigSave();
         }
      });
      this.discordRPC.onChanged(on -> {
         if (on) {
            this.startDiscord();
         } else {
            this.stopDiscord();
         }

         this.requestConfigSave();
      });
   }

   public EnumSetting getLanguageSetting() {
      return this.language;
   }

   public void syncLanguage(Language lang) {
      ClientSettings.LanguageMode var2 = lang == Language.ENG ? ClientSettings.LanguageMode.ENG : ClientSettings.LanguageMode.VIE;
      if (this.language.get() != var2) {
         this.language.set(var2);
         this.requestConfigSave();
      }
   }

   public ClientSettings.LanguageMode getLanguageMode() {
      return (ClientSettings.LanguageMode)this.language.get();
   }

   @Override
   public boolean canTickOutOfGame() {
      return true;
   }

   @Override
   protected boolean bypassAuthLock() {
      return true;
   }

   @Override
   public boolean isVisibleInHud() {
      return false;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      Localization.setLanguage(this.language.get() == ClientSettings.LanguageMode.ENG ? Language.ENG : Language.VIE);
      NanoVGFontManager.setUseMinecraftFont(this.fontMode.get() == ClientSettings.FontMode.MINECRAFT);
      Sounds.instance.soundEnabled = (Boolean)this.interactionSfx.get();
      this.wasRpcEnabled = (Boolean)this.discordRPC.get();
      this.lastRpcConnectAttemptAtMs = 0L;
      if ((Boolean)this.discordRPC.get()) {
         this.startDiscord();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.stopDiscord();
      this.wasRpcEnabled = false;
      this.lastRpcConnectAttemptAtMs = 0L;
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         boolean var2 = (Boolean)this.discordRPC.get();
         if (var2 && !this.wasRpcEnabled) {
            this.startDiscord();
         } else if (!var2 && this.wasRpcEnabled) {
            this.stopDiscord();
         }

         this.wasRpcEnabled = var2;
         if (var2) {
            if (this.discord != null && this.discord.isConnected()) {
               this.updateRpcPresence(false);
            } else {
               long var3 = System.currentTimeMillis();
               if (var3 - this.lastRpcConnectAttemptAtMs >= 10000L) {
                  this.startDiscord();
               }
            }
         }
      }
   }

   private void startDiscord() {
      if (this.discord == null) {
         this.discord = new DiscordPresence();
      }

      this.lastRpcConnectAttemptAtMs = System.currentTimeMillis();
      CompletableFuture.runAsync(() -> {
         if (this.discord != null && this.discord.connect()) {
            this.updateRpcPresence(true);
         }
      });
   }

   private void stopDiscord() {
      if (this.discord != null) {
         this.discord.disconnect();
      }
   }

   private void updateRpcPresence(boolean force) {
      if (this.discord != null && this.discord.isConnected()) {
         ClientSettings.RpcPresenceData var2 = this.resolveRpcPresence();
         if (force) {
            this.discord.updatePresence(var2.details(), var2.state());
         } else {
            this.discord.tick(var2.details(), var2.state());
         }
      }
   }

   public ClientSettings.RpcPresenceData resolveRpcPresence() {
      String var1;
      String var2;
      if (mc != null && mc.field_1687 != null && mc.field_1724 != null) {
         if (mc.method_1542()) {
            var1 = "Singleplayer";
         } else if (mc.method_1558() != null && mc.method_1558().field_3761 != null && !mc.method_1558().field_3761.isBlank()) {
            var1 = "Playing on " + mc.method_1558().field_3761.trim();
         } else {
            var1 = "Playing Multiplayer";
         }

         var2 = resolveDimensionName(mc.field_1687);
      } else {
         var1 = "In Main Menu";
         var2 = "Vie Client";
      }

      return new ClientSettings.RpcPresenceData(var1, var2);
   }

   public static boolean isLegacyDefaultDetail(String text) {
      if (text != null && !text.isBlank()) {
         String var1 = text.trim();
         return var1.equalsIgnoreCase(
               "Playing Vie Cheats Client"
            )
            || var1.equalsIgnoreCase(
               "Playing Vie Client"
            )
            || var1.equalsIgnoreCase(
               "Playing Vie Cheats"
            )
            || var1.equalsIgnoreCase(
               "Join discord for more info"
            )
            || var1.equalsIgnoreCase("Vie Client")
            || var1.equalsIgnoreCase("Vie Cheats")
            || var1.equalsIgnoreCase("1")
            || var1.equalsIgnoreCase("1.0")
            || var1.equalsIgnoreCase("1.0.0")
            || var1.equalsIgnoreCase("v1.0")
            || var1.equalsIgnoreCase("v1.0.0");
      } else {
         return true;
      }
   }

   public static String resolveDimensionName(class_1937 world) {
      if (world == null) {
         byte[] var5 = new byte[7];
         var5[0] = (byte)(58);
         var5[1] = (byte)(-12);
         var5[2] = (byte)(60);
         var5[3] = (byte)(-103);
         var5[4] = (byte)(-37);
         var5[5] = (byte)(-58);
         var5[6] = (byte)(-124);
         return $$v_s(var5, -4475886422658396074L);
      }

      if (world.method_27983() == class_1937.field_25179) {
         byte[] var4 = new byte[9];
         var4[0] = (byte)(60);
         var4[1] = (byte)(-20);
         var4[2] = (byte)(121);
         var4[3] = (byte)(-84);
         var4[4] = (byte)(-51);
         var4[5] = (byte)(-60);
         var4[6] = (byte)(-109);
         var4[7] = (byte)(-123);
         var4[8] = (byte)(127);
         return $$v_s(var4, -4475886422658396074L);
      }

      if (world.method_27983() == class_1937.field_25180) {
         byte[] var3 = new byte[6];
         var3[0] = (byte)(61);
         var3[1] = (byte)(-1);
         var3[2] = (byte)(104);
         var3[3] = (byte)(-74);
         var3[4] = (byte)(-33);
         var3[5] = (byte)(-39);
         return $$v_s(var3, -4475886422658396074L);
      }

      if (world.method_27983() == class_1937.field_25181) {
         byte[] var2 = new byte[7];
         var2[0] = (byte)(39);
         var2[1] = (byte)(-14);
         var2[2] = (byte)(121);
         var2[3] = (byte)(-2);
         var2[4] = (byte)(-1);
         var2[5] = (byte)(-59);
         var2[6] = (byte)(-123);
         return $$v_s(var2, -4475886422658396074L);
      }

      String var1 = world.method_27983().method_29177().method_12832();
      if (var1 != null && !var1.isBlank()) {
         return Character.toUpperCase(var1.charAt(0)) + var1.substring(1).replace((char)(95), (char)(32));
      }

      byte[] var10000 = new byte[7];
      var10000[0] = (byte)(58);
      var10000[1] = (byte)(-12);
      var10000[2] = (byte)(60);
      var10000[3] = (byte)(-103);
      var10000[4] = (byte)(-37);
      var10000[5] = (byte)(-58);
      var10000[6] = (byte)(-124);
      return $$v_s(var10000, -4475886422658396074L);
   }

   public int resolveTextColor(long rgbOffset) {
      if (this.textColorMode.get() == ClientSettings.TextColorMode.RGB) {
         int var3 = 255;
         return RainbowUtil.rainbow(1.0, 1.0, rgbOffset, var3);
      } else {
         return -460036;
      }
   }

   public boolean isRgbTextMode() {
      return this.textColorMode.get() == ClientSettings.TextColorMode.RGB;
   }

   public ClientSettings.TextColorMode getTextColorMode() {
      return (ClientSettings.TextColorMode)this.textColorMode.get();
   }

   public ClientSettings.HudCorner getModuleListPos() {
      return (ClientSettings.HudCorner)this.moduleListPos.get();
   }

   public ClientSettings.WatermarkPos getWatermarkPos() {
      return (ClientSettings.WatermarkPos)this.watermarkPos.get();
   }

   public double getGuiScale() {
      return 1.72;
   }

   public boolean showInfoBar() {
      return (Boolean)this.showInfoBar.get();
   }

   public boolean showCoords() {
      return (Boolean)this.showCoords.get();
   }

   public boolean showNotifs() {
      return (Boolean)this.showNotifs.get();
   }

   public boolean isGuiFadeEnabled() {
      return (Boolean)this.guiFade.get();
   }

   public int getTopInfoOffsetX() {
      return (Integer)this.topInfoOffsetX.get();
   }

   public int getTopInfoOffsetY() {
      return (Integer)this.topInfoOffsetY.get();
   }

   public int getCoordsOffsetX() {
      return (Integer)this.coordsOffsetX.get();
   }

   public int getCoordsOffsetY() {
      return (Integer)this.coordsOffsetY.get();
   }

   public int getModuleListOffsetX() {
      return (Integer)this.moduleListOffsetX.get();
   }

   public int getModuleListOffsetY() {
      return (Integer)this.moduleListOffsetY.get();
   }

   public int getHotkeysOffsetX() {
      return (Integer)this.hotkeysOffsetX.get();
   }

   public int getHotkeysOffsetY() {
      return (Integer)this.hotkeysOffsetY.get();
   }

   public float getHotkeysScale() {
      return ((Integer)this.hotkeysScale.get()).intValue() / 10.0F;
   }

   public void setHotkeysScale(float value) {
      this.hotkeysScale.set(Math.max(6, Math.min(20, Math.round(value * 10.0F))));
   }

   public void setTopInfoOffsetX(int value) {
      this.topInfoOffsetX.set(value);
   }

   public void setTopInfoOffsetY(int value) {
      this.topInfoOffsetY.set(value);
   }

   public void setCoordsOffsetX(int value) {
      this.coordsOffsetX.set(value);
   }

   public void setCoordsOffsetY(int value) {
      this.coordsOffsetY.set(value);
   }

   public void setModuleListOffsetX(int value) {
      this.moduleListOffsetX.set(value);
   }

   public void setModuleListOffsetY(int value) {
      this.moduleListOffsetY.set(value);
   }

   public void setHotkeysOffsetX(int value) {
      this.hotkeysOffsetX.set(value);
   }

   public void setHotkeysOffsetY(int value) {
      this.hotkeysOffsetY.set(value);
   }

   public int getClickGuiKey() {
      return (Integer)this.clickGuiKey.get();
   }

   public KeybindSetting getClickGuiKeySetting() {
      return this.clickGuiKey;
   }

   public void setClickGuiKey(int key) {
      if ((Integer)this.clickGuiKey.get() != key) {
         this.clickGuiKey.set(key);
         this.requestConfigSave();

         try {
            CoreEntry var2 = CoreEntry.getInstance();
            if (var2 != null) {
               var2.updateGuiKeyBinding(key);
            }
         } catch (Throwable var3) {
         }
      }
   }

   public VieClient.GuiStyleMode getGuiStyle() {
      return (VieClient.GuiStyleMode)this.guiStyle.get();
   }

   public void setGuiStyle(VieClient.GuiStyleMode mode) {
      this.guiStyle.set(mode);
   }

   public EnumSetting getGuiStyleSetting() {
      return this.guiStyle;
   }

   public VieClient.ClickGuiMode getClickGuiMode() {
      return (VieClient.ClickGuiMode)this.clickGuiMode.get();
   }

   public void setClickGuiMode(VieClient.ClickGuiMode mode) {
      this.clickGuiMode.set(mode);
   }

   public EnumSetting getClickGuiModeSetting() {
      return this.clickGuiMode;
   }

   public ClientSettings.ThemePreset getThemePreset() {
      return (ClientSettings.ThemePreset)this.themePreset.get();
   }

   public void setThemePreset(ClientSettings.ThemePreset preset) {
      this.themePreset.set(preset);
   }

   public EnumSetting getThemePresetSetting() {
      return this.themePreset;
   }

   public EnumSetting getFontModeSetting() {
      return this.fontMode;
   }

   public ClientSettings.FontMode getFontMode() {
      return (ClientSettings.FontMode)this.fontMode.get();
   }

   public void setFontMode(ClientSettings.FontMode mode) {
      this.fontMode.set(mode);
   }

   public BoolSetting getInteractionSfxSetting() {
      return this.interactionSfx;
   }

   public boolean isInteractionSfxEnabled() {
      return (Boolean)this.interactionSfx.get();
   }

   public void setInteractionSfxEnabled(boolean on) {
      this.interactionSfx.set(on);
      Sounds.instance.soundEnabled = on;
   }

   public StringSetting getUiFontSetting() {
      return this.rpcDetail;
   }

   public BoolSetting getDiscordRpcSetting() {
      return this.discordRPC;
   }

   public StringSetting getRpcDetailSetting() {
      return this.rpcDetail;
   }

   public int getAccentColor() {
      return this.getPrimaryColor();
   }

   public int getPrimaryColor() {
      return this.isRainbowActive() ? getRainbowColor(0L) : this.resolveThemePrimaryColor();
   }

   public int getSecondaryColor() {
      return this.isRainbowActive() ? getRainbowColor(600L) : this.resolveThemeSecondaryColor();
   }

   public int getTextColor() {
      return this.resolveThemeTextColor();
   }

   public boolean isRainbowActive() {
      if (this.themePreset.get() == ClientSettings.ThemePreset.RAINBOW) {
         return true;
      }

      try {
         return VieClient.enableRainbowEffect.getValue();
      } catch (Throwable var2) {
         return false;
      }
   }

   public static int getRainbowColor(long offset) {
      return RainbowUtil.rainbow(1.0, 0.88, offset, 255);
   }

   public static ClientSettings get() {
      if (instance != null) {
         return instance;
      }

      try {
         VieClientMod var0 = VieClientMod.getInstance();
         if (var0 != null && var0.getModuleManager() != null) {
            return (ClientSettings)var0.getModuleManager().getModule(ClientSettings.class);
         }
      } catch (Throwable var1) {
      }

      return null;
   }

   private int resolveThemePrimaryColor() {
      ClientSettings.ThemePreset var1 = (ClientSettings.ThemePreset)this.themePreset.get();
      if (var1 == null) {
         return -7643914;
      }

      return switch (var1) {
         case OBSIDIAN -> -7643914;
         case TITANIUM -> -10852744;
         case GRAPHITE -> -12960187;
         case SAPPHIRE -> -12877066;
         case AURORA -> -15419226;
         case EMERALD -> -15681151;
         case ICE -> -13058568;
         case ROSE -> -298619;
         case AMBER -> -278748;
         case CRIMSON -> -1096636;
         case VIOLET -> -5745161;
         case GOLD -> -340971;
         case CYBERPUNK_NEON -> -65409;
         case VAPORWAVE -> -54651;
         case AURORA_NIGHT -> -13244752;
         case PURPLE_VOID -> -8836918;
         case SUNSET_DREAM -> -37772;
         case RAINBOW -> getRainbowColor(0L);
      };
   }

   private int resolveThemeSecondaryColor() {
      ClientSettings.ThemePreset var1 = (ClientSettings.ThemePreset)this.themePreset.get();
      if (var1 == null) {
         return -9623335;
      }

      return switch (var1) {
         case OBSIDIAN -> -9623335;
         case TITANIUM -> -8219477;
         case GRAPHITE -> -10525328;
         case SAPPHIRE -> -14794577;
         case AURORA -> -15887224;
         case EMERALD -> -16484265;
         case ICE -> -16612153;
         case ROSE -> -2024120;
         case AMBER -> -2525434;
         case CRIMSON -> -2349530;
         case VIOLET -> -8635667;
         case GOLD -> -3503612;
         case CYBERPUNK_NEON -> -16715521;
         case VAPORWAVE -> -16715521;
         case AURORA_NIGHT -> -16040658;
         case PURPLE_VOID -> -11788907;
         case SUNSET_DREAM -> -14200;
         case RAINBOW -> getRainbowColor(600L);
      };
   }

   private int resolveThemeTextColor() {
      return -460036;
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

   public enum FontMode {
      DEFAULT,
      MINECRAFT;

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      FontMode(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static ClientSettings.FontMode[] $values() {
         ClientSettings.FontMode[] var10000 = new ClientSettings.FontMode[2];
         var10000[0] = DEFAULT;
         var10000[1] = MINECRAFT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(90);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(103);
         var10002[5] = (byte)(106);
         var10002[6] = (byte)(-53);
         String var0 = $$v_s(var10002, -7305691218368540646L);
         byte[] var10004 = new byte[16];
         var10004[0] = (byte)(123);
         var10004[1] = (byte)(-1);
         var10004[2] = (byte)(122);
         var10004[3] = (byte)(-65);
         var10004[4] = (byte)(71);
         var10004[5] = (byte)(74);
         var10004[6] = (byte)(-21);
         var10004[7] = (byte)(-110);
         var10004[8] = (byte)(127);
         var10004[9] = (byte)(-15);
         var10004[10] = (byte)(-94);
         var10004[11] = (byte)(-42);
         var10004[12] = (byte)(58);
         var10004[13] = (byte)(-116);
         var10004[14] = (byte)(-40);
         var10004[15] = (byte)(-29);
         DEFAULT = new ClientSettings.FontMode($$v_s(var10004, -7305691218368540646L));
         var10002 = new byte[9];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(113);
         var10002[5] = (byte)(116);
         var10002[6] = (byte)(-34);
         var10002[7] = (byte)(-12);
         var10002[8] = (byte)(3);
         String var2 = $$v_s(var10002, -7305691218368540646L);
         var10004 = new byte[9];
         var10004[0] = (byte)(114);
         var10004[1] = (byte)(-13);
         var10004[2] = (byte)(114);
         var10004[3] = (byte)(-69);
         var10004[4] = (byte)(81);
         var10004[5] = (byte)(84);
         var10004[6] = (byte)(-2);
         var10004[7] = (byte)(-44);
         var10004[8] = (byte)(35);
         MINECRAFT = new ClientSettings.FontMode($$v_s(var10004, -7305691218368540646L));
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

   public enum HudCorner {
      TOP_RIGHT,
      TOP_LEFT,
      BOTTOM_RIGHT,
      BOTTOM_LEFT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ClientSettings.HudCorner[] $values() {
         ClientSettings.HudCorner[] var10000 = new ClientSettings.HudCorner[4];
         var10000[0] = TOP_RIGHT;
         var10000[1] = TOP_LEFT;
         var10000[2] = BOTTOM_RIGHT;
         var10000[3] = BOTTOM_LEFT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[9];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(48);
         var10002[5] = (byte)(119);
         var10002[6] = (byte)(-90);
         var10002[7] = (byte)(-110);
         var10002[8] = (byte)(3);
         TOP_RIGHT = new ClientSettings.HudCorner();
         var10002 = new byte[8];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(46);
         var10002[5] = (byte)(123);
         var10002[6] = (byte)(-89);
         var10002[7] = (byte)(-114);
         TOP_LEFT = new ClientSettings.HudCorner();
         var10002 = new byte[12];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(45);
         var10002[5] = (byte)(115);
         var10002[6] = (byte)(-66);
         var10002[7] = (byte)(-120);
         var10002[8] = (byte)(30);
         var10002[9] = (byte)(-27);
         var10002[10] = (byte)(-84);
         var10002[11] = (byte)(-94);
         BOTTOM_RIGHT = new ClientSettings.HudCorner();
         var10002 = new byte[11];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(45);
         var10002[5] = (byte)(115);
         var10002[6] = (byte)(-66);
         var10002[7] = (byte)(-106);
         var10002[8] = (byte)(18);
         var10002[9] = (byte)(-28);
         var10002[10] = (byte)(-80);
         BOTTOM_LEFT = new ClientSettings.HudCorner();
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

   public enum LanguageMode {
      VIE,
      ENG;

      private final String code;
      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      LanguageMode(String code, String displayName) {
         this.code = codex;
         this.displayName = displayNamex;
      }

      public String getCode() {
         return this.code;
      }

      public String getDisplayName() {
         return this.displayName;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static ClientSettings.LanguageMode[] $values() {
         ClientSettings.LanguageMode[] var10000 = new ClientSettings.LanguageMode[2];
         var10000[0] = VIE;
         var10000[1] = ENG;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(89);
         String var0 = $$v_s(var10002, -8902727249213272040L);
         byte[] var10004 = new byte[3];
         var10004[0] = (byte)(107);
         var10004[1] = (byte)(-45);
         var10004[2] = (byte)(89);
         String var3 = $$v_s(var10004, -8902727249213272040L);
         byte[] var10005 = new byte[14];
         var10005[0] = (byte)(105);
         var10005[1] = (byte)(-13);
         var10005[2] = (byte)(-3);
         var10005[3] = (byte)(100);
         var10005[4] = (byte)(-70);
         var10005[5] = (byte)(-104);
         var10005[6] = (byte)(23);
         var10005[7] = (byte)(-116);
         var10005[8] = (byte)(3);
         var10005[9] = (byte)(-53);
         var10005[10] = (byte)(5);
         var10005[11] = (byte)(77);
         var10005[12] = (byte)(-38);
         var10005[13] = (byte)(90);
         VIE = new ClientSettings.LanguageMode(var3, $$v_s(var10005, -8902727249213272040L));
         var10002 = new byte[3];
         var10002[0] = (byte)(120);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(91);
         String var2 = $$v_s(var10002, -8902727249213272040L);
         var10004 = new byte[3];
         var10004[0] = (byte)(120);
         var10004[1] = (byte)(-44);
         var10004[2] = (byte)(91);
         String var5 = $$v_s(var10004, -8902727249213272040L);
         var10005 = new byte[7];
         var10005[0] = (byte)(120);
         var10005[1] = (byte)(-12);
         var10005[2] = (byte)(123);
         var10005[3] = (byte)(-78);
         var10005[4] = (byte)(108);
         var10005[5] = (byte)(-123);
         var10005[6] = (byte)(24);
         ENG = new ClientSettings.LanguageMode(var5, $$v_s(var10005, -8902727249213272040L));
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

   public record RpcPresenceData(String details, String state) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum TextColorMode {
      STATIC,
      RGB;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ClientSettings.TextColorMode[] $values() {
         ClientSettings.TextColorMode[] var10000 = new ClientSettings.TextColorMode[2];
         var10000[0] = STATIC;
         var10000[1] = RGB;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-50);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-8);
         var10002[5] = (byte)(99);
         STATIC = new ClientSettings.TextColorMode();
         var10002 = new byte[3];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-35);
         var10002[2] = (byte)(94);
         RGB = new ClientSettings.TextColorMode();
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

   public enum ThemePreset {
      OBSIDIAN("Obsidian"),
      TITANIUM("Titanium"),
      GRAPHITE("Graphite"),
      SAPPHIRE("Sapphire"),
      AURORA("Aurora"),
      EMERALD("Emerald"),
      ICE("Ice"),
      ROSE("Rose"),
      AMBER("Amber"),
      CRIMSON("Crimson"),
      VIOLET("Violet"),
      GOLD("Gold"),
      CYBERPUNK_NEON("Cyberpunk Neon"),
      VAPORWAVE("Vaporwave"),
      AURORA_NIGHT("Aurora Night"),
      PURPLE_VOID("Purple Void"),
      SUNSET_DREAM("Sunset Dream"),
      RAINBOW("Rainbow");

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ThemePreset(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static ClientSettings.ThemePreset[] $values() {
         ClientSettings.ThemePreset[] var10000 = new ClientSettings.ThemePreset[18];
         var10000[0] = OBSIDIAN;
         var10000[1] = TITANIUM;
         var10000[2] = GRAPHITE;
         var10000[3] = SAPPHIRE;
         var10000[4] = AURORA;
         var10000[5] = EMERALD;
         var10000[6] = ICE;
         var10000[7] = ROSE;
         var10000[8] = AMBER;
         var10000[9] = CRIMSON;
         var10000[10] = VIOLET;
         var10000[11] = GOLD;
         var10000[12] = CYBERPUNK_NEON;
         var10000[13] = VAPORWAVE;
         var10000[14] = AURORA_NIGHT;
         var10000[15] = PURPLE_VOID;
         var10000[16] = SUNSET_DREAM;
         var10000[17] = RAINBOW;
         return var10000;
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

   public enum WatermarkPos {
      TOP_LEFT,
      TOP_RIGHT,
      HIDDEN;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ClientSettings.WatermarkPos[] $values() {
         ClientSettings.WatermarkPos[] var10000 = new ClientSettings.WatermarkPos[3];
         var10000[0] = TOP_LEFT;
         var10000[1] = TOP_RIGHT;
         var10000[2] = HIDDEN;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[8];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(60);
         var10002[5] = (byte)(33);
         var10002[6] = (byte)(-73);
         var10002[7] = (byte)(-39);
         TOP_LEFT = new ClientSettings.WatermarkPos();
         var10002 = new byte[9];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(34);
         var10002[5] = (byte)(45);
         var10002[6] = (byte)(-74);
         var10002[7] = (byte)(-59);
         var10002[8] = (byte)(4);
         TOP_RIGHT = new ClientSettings.WatermarkPos();
         var10002 = new byte[6];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(88);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(53);
         var10002[5] = (byte)(42);
         HIDDEN = new ClientSettings.WatermarkPos();
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
