package me.drake.vieclient.gui.nvg.adapter;

import me.drake.vieclient.gui.nvg.adapter.module.ModuleManager;
import me.drake.vieclient.gui.nvg.ui.NewClickGUI;

public final class VieClient {
   public static VieClient INSTANCE = new VieClient();
   public NewClickGUI GUI;
   private final ModuleManager moduleManager = new ModuleManager();
   private final ConfigManager configManager = new ConfigManager();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ModuleManager getModuleManager() {
      return this.moduleManager;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }
}
