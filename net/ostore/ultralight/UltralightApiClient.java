package net.ostore.ultralight;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class UltralightApiClient implements ClientModInitializer {
   public void onInitializeClient() {
      UltralightEngine.init();
   }
}
