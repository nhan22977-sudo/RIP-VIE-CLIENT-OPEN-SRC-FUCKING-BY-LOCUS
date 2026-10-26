package me.drake.vieclient.modules.client.music;

import java.util.Optional;

@FunctionalInterface
public interface MusicMetadataResolver {
   Optional resolve(String var1);
}
