package me.drake.vieclient.core.event.events;

import java.util.List;
import me.drake.vieclient.core.event.Event;
import net.minecraft.class_1923;
import net.minecraft.class_2818;

public class ChunkLoadEvent extends Event {
   private final class_1923 chunkPos;
   private final boolean loaded;
   private final List blockEntities;
   private final class_2818 chunk;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ChunkLoadEvent(class_1923 chunkPos, boolean loaded, List blockEntities, class_2818 chunk) {
      this.chunkPos = chunkPosx;
      this.loaded = loadedx;
      this.blockEntities = blockEntitiesx;
      this.chunk = chunkx;
   }

   public ChunkLoadEvent(class_1923 chunkPos, boolean loaded, List blockEntities) {
      this(chunkPosx, loadedx, blockEntitiesx, null);
   }

   public ChunkLoadEvent(class_1923 chunkPos, boolean loaded) {
      this(chunkPosx, loadedx, List.of(), null);
   }

   public class_1923 getChunkPos() {
      return this.chunkPos;
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public boolean isUnloaded() {
      return !this.loaded;
   }

   public List getBlockEntities() {
      return this.blockEntities;
   }

   public class_2818 getChunk() {
      return this.chunk;
   }
}
