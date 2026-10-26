package me.drake.vieclient.modules.donut.profiler.network;

import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;
import me.drake.vieclient.modules.donut.profiler.ProfilerSample;

public record RegionLatencySample(int blockX, int blockY, int blockZ, long roundTripMs, double connectionBaselineMs, long capturedNanos)
   implements ProfilerSample {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public long chunkKey() {
      return ChunkKey.pack(this.blockX >> (4), this.blockZ >> (4));
   }
}
