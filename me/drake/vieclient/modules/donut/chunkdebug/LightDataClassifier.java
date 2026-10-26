package me.drake.vieclient.modules.donut.chunkdebug;

import java.util.BitSet;
import java.util.List;

public final class LightDataClassifier {
   public static final int NIBBLES_PER_SECTION = 2048;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private LightDataClassifier() {
   }

   public static SectionStatus classifyPayload(byte[] payload) {
      if (payload != null && payload.length == (2048)) {
         boolean var1 = true;
         boolean var2 = true;

         for (byte var6 : payload) {
            if (var6 != 0) {
               var1 = false;
            }

            if (var6 != (-1)) {
               var2 = false;
            }

            if (!var1 && !var2) {
               return SectionStatus.NORMAL;
            }
         }

         if (var1) {
            return SectionStatus.ZEROED_PRESENT;
         } else {
            return var2 ? SectionStatus.UNIFORM_MAX : SectionStatus.NORMAL;
         }
      } else {
         return SectionStatus.UNKNOWN;
      }
   }

   public static void applyTrack(BitSet initialized, BitSet uninitialized, List nibbles, SectionStatus[] output, long[] updatedNanos, long stamp) {
      if (output != null && updatedNanos != null) {
         BitSet var7 = initialized == null ? new BitSet() : initialized;
         BitSet var8 = uninitialized == null ? new BitSet() : uninitialized;
         List var9 = nibbles == null ? List.of() : nibbles;
         int var10 = 0;

         for (int var11 = 0; var11 < output.length; var11++) {
            if (var7.get(var11)) {
               byte[] var12 = var10 < var9.size() ? (byte[])var9.get(var10) : null;
               var10++;
               output[var11] = classifyPayload(var12);
               if (var11 < updatedNanos.length) {
                  updatedNanos[var11] = stamp;
               }
            } else if (var8.get(var11)) {
               output[var11] = SectionStatus.EMPTY_MASK;
               if (var11 < updatedNanos.length) {
                  updatedNanos[var11] = stamp;
               }
            }
         }
      }
   }
}
