package com.zigythebird.playeranim.fabric;

import com.zigythebird.playeranim.PlayerAnimLibMod;
import com.zigythebird.playeranim.animation.PlayerAnimResources;
import com.zigythebird.playeranim.commands.PlayerAnimCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.class_3264;

public final class PlayerAnimLibModFabric extends PlayerAnimLibMod implements ClientModInitializer {
   public void onInitializeClient() {
      ResourceLoader.get(class_3264.field_14188).registerReloader(PlayerAnimResources.KEY, new PlayerAnimResources());
      if (FabricLoader.getInstance().isDevelopmentEnvironment()
         || ((ModContainer)FabricLoader.getInstance().getModContainer("player_animation_library").get())
            .getMetadata()
            .getVersion()
            .getFriendlyString()
            .contains("dev")) {
         ClientCommandRegistrationCallback.EVENT.register(PlayerAnimCommands::register);
      }

      super.init();
   }
}
