package io.github.kosmx.emotes.arch.mixin;

import io.github.kosmx.emotes.arch.network.EmotesMixinConnection;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_2535;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(class_2535.class)
public class ConnectionHandlerMixin implements EmotesMixinConnection {
   @Unique
   @NotNull
   private final HashMap<Byte, Byte> emotecraft$versions = new HashMap<>();

   @NotNull
   @Override
   public Map<Byte, Byte> emotecraft$getRemoteVersions() {
      return this.emotecraft$versions;
   }

   @Override
   public void emotecraft$setVersions(@Nullable Map<Byte, Byte> map) {
      this.emotecraft$versions.clear();
      if (map != null) {
         this.emotecraft$versions.putAll(map);
      }
   }
}
