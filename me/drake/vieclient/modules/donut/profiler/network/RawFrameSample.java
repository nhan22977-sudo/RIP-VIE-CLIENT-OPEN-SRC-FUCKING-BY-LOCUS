package me.drake.vieclient.modules.donut.profiler.network;

import me.drake.vieclient.modules.donut.profiler.ProfilerSample;

public record RawFrameSample(
   int chunkX, int chunkZ, String profileBucket, int compressedBytes, int decodedBytes, String packetClass, long frameNanos, long decodedNanos
) implements ProfilerSample {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   @Override
   public long capturedNanos() {
      return this.decodedNanos;
   }

   public double compressionRatio() {
      return this.decodedBytes <= 0 ? 1.0 : (double)this.compressedBytes / this.decodedBytes;
   }
}
