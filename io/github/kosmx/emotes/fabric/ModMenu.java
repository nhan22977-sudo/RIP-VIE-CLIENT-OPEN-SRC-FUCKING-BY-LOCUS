package io.github.kosmx.emotes.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import io.github.kosmx.emotes.arch.screen.EmoteMenu;

public class ModMenu implements ModMenuApi {
   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      return EmoteMenu::new;
   }
}
