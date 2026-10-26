package me.drake.vieclient.modules.donut.chunkdebug;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public final class MachineEvidenceTickBudget {
   private final int maxSweeps;
   private final Map resolved = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MachineEvidenceTickBudget(int maxSweeps) {
      this.maxSweeps = Math.max(0, maxSweepsx);
   }

   public MachineEvidence resolve(int chunkX, int chunkZ, BiFunction scanner) {
      long var4 = packChunk(chunkX, chunkZ);
      MachineEvidence var6 = (MachineEvidence)this.resolved.get(var4);
      if (var6 != null) {
         return var6;
      }

      if (this.resolved.size() >= this.maxSweeps) {
         return MachineEvidence.NONE;
      }

      MachineEvidence var7 = scanner == null ? null : (MachineEvidence)scanner.apply(chunkX, chunkZ);
      var7 = var7 == null ? MachineEvidence.NONE : var7;
      this.resolved.put(var4, var7);
      return var7;
   }

   private static long packChunk(int chunkX, int chunkZ) {
      return chunkX & 4294967295L | (long)chunkZ << (32);
   }
}
