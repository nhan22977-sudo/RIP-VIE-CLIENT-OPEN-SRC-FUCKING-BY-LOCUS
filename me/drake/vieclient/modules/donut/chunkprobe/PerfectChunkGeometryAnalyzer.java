package me.drake.vieclient.modules.donut.chunkprobe;

import java.util.Optional;

public final class PerfectChunkGeometryAnalyzer {
   private static final int SIZE = 16;
   private static final int PLANE_COUNT = 15;
   private static final int MIN_ALIGNED_BOUNDARY = 32;
   private static final int MIN_LONG_RUN = 8;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Optional analyze(RawChunkProbe probe) {
      PerfectChunkGeometryAnalyzer.GeometryEvidence var2 = null;

      for (RawChunkProbe.Section var4 : probe.sections()) {
         PerfectChunkGeometryAnalyzer.GeometryEvidence var5 = this.analyzeSection(var4);
         if (var5.confidence() != Confidence.NONE
            && (
               var2 == null
                  || var5.confidence().ordinal() > var2.confidence().ordinal()
                  || var5.confidence() == var2.confidence() && var5.score() > var2.score()
            )) {
            var2 = var5;
         }
      }

      return Optional.ofNullable(var2);
   }

   private PerfectChunkGeometryAnalyzer.GeometryEvidence analyzeSection(RawChunkProbe.Section section) {
      if (section.allMaskedDeepslate() && !section.canonicalUniformMask() && section.bitsPerEntry() != 0 && section.distinctLabels() >= (2)) {
         int[] var2 = section.labels();
         int[][] var3 = new int[3][15];
         int var4 = countTransitions(var2, var3);
         if (var4 == 0) {
            return PerfectChunkGeometryAnalyzer.GeometryEvidence.none(section.sectionY());
         }

         int var5 = maxPlane(var3);
         int var6 = countParallelAxes(var3);
         int var7 = countLongRuns(var2);
         boolean var8 = var5 >= (32) && var5 * 8L >= var4;
         boolean var9 = var8 && var6 >= (2);
         boolean var10 = var8 && var6 >= 1 && var7 >= (8);
         Confidence var11 = !var9 && !var10 ? (var8 && var7 >= (4) ? Confidence.POSSIBLE : Confidence.NONE) : Confidence.HIGH;
         double var12 = var5 + var6 * 24.0 + Math.min(var7, 64) * 1.5 + var5 * 64.0 / var4;
         return new PerfectChunkGeometryAnalyzer.GeometryEvidence(var11, var12, section.sectionY(), var5, var6, var7);
      } else {
         return PerfectChunkGeometryAnalyzer.GeometryEvidence.none(section.sectionY());
      }
   }

   private static int countTransitions(int[] labels, int[][] planes) {
      int var2 = 0;

      for (int var3 = 0; var3 < (16); var3++) {
         for (int var4 = 0; var4 < (16); var4++) {
            for (int var5 = 0; var5 < (15); var5++) {
               if (labels[index(var5, var3, var4)] != labels[index(var5 + 1, var3, var4)]) {
                  planes[0][var5]++;
                  var2++;
               }
            }
         }
      }

      for (int var6 = 0; var6 < (15); var6++) {
         for (int var8 = 0; var8 < (16); var8++) {
            for (int var10 = 0; var10 < (16); var10++) {
               if (labels[index(var10, var6, var8)] != labels[index(var10, var6 + 1, var8)]) {
                  planes[1][var6]++;
                  var2++;
               }
            }
         }
      }

      for (int var7 = 0; var7 < (15); var7++) {
         for (int var9 = 0; var9 < (16); var9++) {
            for (int var11 = 0; var11 < (16); var11++) {
               if (labels[index(var11, var9, var7)] != labels[index(var11, var9, var7 + 1)]) {
                  planes[2][var7]++;
                  var2++;
               }
            }
         }
      }

      return var2;
   }

   private static int maxPlane(int[][] planes) {
      int var1 = 0;

      for (int[] var5 : planes) {
         for (int var9 : var5) {
            var1 = Math.max(var1, var9);
         }
      }

      return var1;
   }

   private static int countParallelAxes(int[][] planes) {
      int var1 = 0;

      for (int[] var5 : planes) {
         boolean var6 = false;

         for (int var7 = 0; var7 < var5.length && !var6; var7++) {
            if (var5[var7] >= (32)) {
               for (int var8 = var7 + 1; var8 < var5.length && var8 - var7 <= (12); var8++) {
                  if (var5[var8] >= (32)) {
                     var6 = true;
                     break;
                  }
               }
            }
         }

         if (var6) {
            var1++;
         }
      }

      return var1;
   }

   private static int countLongRuns(int[] labels) {
      int var1 = 0;

      for (int var2 = 0; var2 < (16); var2++) {
         for (int var3 = 0; var3 < (16); var3++) {
            var1 += countLineRuns(labels, index(0, var2, var3), 1);
         }
      }

      for (int var4 = 0; var4 < (16); var4++) {
         for (int var6 = 0; var6 < (16); var6++) {
            var1 += countLineRuns(labels, index(var6, 0, var4), 256);
         }
      }

      for (int var5 = 0; var5 < (16); var5++) {
         for (int var7 = 0; var7 < (16); var7++) {
            var1 += countLineRuns(labels, index(var7, var5, 0), 16);
         }
      }

      return var1;
   }

   private static int countLineRuns(int[] labels, int start, int stride) {
      int var3 = 0;
      int var4 = 1;

      for (int var5 = 1; var5 < (16); var5++) {
         int var6 = labels[start + (var5 - 1) * stride];
         int var7 = labels[start + var5 * stride];
         if (var7 == var6) {
            var4++;
         } else {
            if (var4 >= (8) && var4 < (16)) {
               var3++;
            }

            var4 = 1;
         }
      }

      if (var4 >= (8) && var4 < (16)) {
         var3++;
      }

      return var3;
   }

   private static int index(int x, int y, int z) {
      return y << (8) | z << (4) | x;
   }

   public record GeometryEvidence(Confidence confidence, double score, int strongestSectionY, int alignedBoundary, int parallelAxes, int longRuns) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private static PerfectChunkGeometryAnalyzer.GeometryEvidence none(int sectionY) {
         return new PerfectChunkGeometryAnalyzer.GeometryEvidence(Confidence.NONE, 0.0, sectionY, 0, 0, 0);
      }
   }
}
