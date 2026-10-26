package io.github.kosmx.emotes.arch.network;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface EmotesMixinConnection {
   @NotNull
   Map<Byte, Byte> emotecraft$getRemoteVersions();

   void emotecraft$setVersions(@Nullable Map<Byte, Byte> var1);
}
