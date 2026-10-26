package io.github.kosmx.emotes.fabric;

import io.github.kosmx.emotes.fabric.network.ServerNetworkStuff;
import io.github.kosmx.emotes.main.EmotecraftMod;
import io.github.kosmx.emotes.mc.ServerCommands;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.ServerStarting;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents.StartTracking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

public class EmotecraftFabricMod extends EmotecraftMod implements ModInitializer {
   public static MinecraftServer SERVER_INSTANCE;

   public void onInitialize() {
      super.onInitialize(FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT);
      ServerNetworkStuff.init();
      CommandRegistrationCallback.EVENT.register(ServerCommands::register);
      EntityTrackingEvents.START_TRACKING.register((StartTracking)(x$0, x$1) -> this.onStartTracking(x$0, x$1));
      ServerLifecycleEvents.SERVER_STARTING.register((ServerStarting)server -> SERVER_INSTANCE = server);
   }
}
