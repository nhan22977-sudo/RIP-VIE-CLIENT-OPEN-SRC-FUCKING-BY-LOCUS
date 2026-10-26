package me.drake.vieclient.modules.donut.profiler;

public record FinderEvidence(long chunkKey, FinderConfidence confidence, double score, int sampleCount, long firstSeenMs, long updatedMs, String reason) {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;
}
