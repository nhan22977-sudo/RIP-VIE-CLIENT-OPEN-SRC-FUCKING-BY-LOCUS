package io.github.kosmx.emotes.arch.mixin;

import io.github.kosmx.emotes.arch.network.EmotesMixinNetwork;
import io.github.kosmx.emotes.arch.network.ModdedServerPlayNetwork;
import net.minecraft.class_2535;
import net.minecraft.class_3244;
import net.minecraft.class_8609;
import net.minecraft.class_8792;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(class_3244.class)
public abstract class ServerPlayNetworkMixin extends class_8609 implements EmotesMixinNetwork {
   @Unique
   private final ModdedServerPlayNetwork emotecraft$instance = new ModdedServerPlayNetwork((class_3244)this);

   public ServerPlayNetworkMixin(MinecraftServer minecraftServer, class_2535 connection, class_8792 commonListenerCookie) {
      super(minecraftServer, connection, commonListenerCookie);
   }

   @NotNull
   @Override
   public ModdedServerPlayNetwork emotecraft$getServerNetworkInstance() {
      return this.emotecraft$instance;
   }
}
