package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.render.EspFade;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2902;
import net.minecraft.class_4184;
import net.minecraft.class_638;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_2902.class_2903;

public class HoleESP extends Module {
   private final BoolSetting detect1x1 = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Detect-1x1",
         "Detect and render 1x1 holes",
         true
      )
   );
   private final BoolSetting detect3x3 = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Detect-3x3",
         "Detect and render 3x3 holes",
         true
      )
   );
   private final IntSetting minHoleDepth = (IntSetting)this.addSetting(
      new IntSetting(
         "Min-hole-depth",
         "Minimum hole depth before a column counts as a hole",
         4,
         1,
         15
      )
   );
   private final ColorSetting hole1x1Color = (ColorSetting)this.addSetting(
      new ColorSetting(
         "1x1-Color",
         "Side color for 1x1 holes",
         1073681555
      )
   );
   private final ColorSetting hole3x3Color = (ColorSetting)this.addSetting(
      new ColorSetting(
         "3x3-Color",
         "Side color for 3x3 holes",
         1073676288
      )
   );
   private static final int MAX_GAP_LENGTH = 3;
   private static final int MAX_GAPS_ALLOWED = 2;
   private static final int SCAN_DEPTH_LIMIT = 70;
   private static final int OPEN_FACE_LIMIT = 5;
   private static final double DOMINANT_RATIO = 0.85;
   private static final double SOLID_MASS_RATIO = 0.8;
   private static final int CHUNK_SCAN_BUDGET = 5;
   private static final int[][] EDGES = new int[][]{{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
   private static final int[][] FACES = new int[][]{{0, 1, 2, 3}, {4, 5, 6, 7}, {0, 1, 5, 4}, {2, 3, 7, 6}, {0, 3, 7, 4}, {1, 2, 6, 5}};
   private final Map rawByChunk = new ConcurrentHashMap();
   private final ArrayDeque chunkQueue = new ArrayDeque();
   private final Set queuedChunkKeys = ConcurrentHashMap.newKeySet();
   private volatile boolean rawDataDirty = false;
   private final Set hole1x1 = ConcurrentHashMap.newKeySet();
   private final Set hole3x3 = ConcurrentHashMap.newKeySet();
   private final AtomicBoolean rebuildRunning = new AtomicBoolean(false);
   private ExecutorService worker;
   private final EspFade espFade = new EspFade(this::finishDisable);
   private int fbW;
   private int fbH;
   private final HoleESP.CamProj camProj = new HoleESP.CamProj();
   private final double[][] scratchPts = new double[8][];
   private final double[][] scratchCoords = new double[8][2];
   private int lastHole1x1Rgba = 0;
   private Color cachedHole1x1Color;
   private int lastHole3x3Rgba = 0;
   private Color cachedHole3x3Color;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public HoleESP() {
      super(
         "HoleESP",
         "Highlights crystal-safe 1x1 and 3x3 holes",
         Category.RENDER
      );
      this.hole1x1Color.visibleWhen(this.detect1x1::get);
      this.hole3x3Color.visibleWhen(this.detect3x3::get);
   }

   private void finishDisable() {
      this.shutdownWorker();
      this.clearAllData();
      super.onDisable();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.ensureWorker();
      this.clearAllData();
      if (this.isInGame()) {
         this.refreshChunkQueue();
      }

      this.espFade.onEnable();
   }

   @Override
   public void onDisable() {
      this.espFade.onDisable();
   }

   private void shutdownWorker() {
      if (this.worker != null) {
         this.worker.shutdownNow();
         this.worker = null;
      }
   }

   private void ensureWorker() {
      if (this.worker == null) {
         this.worker = Executors.newSingleThreadExecutor(r -> {
            byte[] var10003 = new byte[12];
            var10003[0] = (byte)(67);
            var10003[1] = (byte)(-13);
            var10003[2] = (byte)('ꃂ' ^ 'ꂻ');
            var10003[3] = (byte)(-13);
            var10003[4] = (byte)(-79);
            var10003[5] = (byte)(73);
            var10003[6] = (byte)(10);
            var10003[7] = (byte)(83);
            var10003[8] = (byte)(112);
            var10003[9] = (byte)(-57);
            var10003[10] = (byte)(-105);
            var10003[11] = (byte)(-122);
            Thread var1 = new Thread(r, $$v_s(var10003, 2190429886235267088L));
            var1.setDaemon(true);
            return var1;
         });
      }
   }

   private void clearAllData() {
      this.rawByChunk.clear();
      this.chunkQueue.clear();
      this.queuedChunkKeys.clear();
      this.hole1x1.clear();
      this.hole3x3.clear();
      this.rawDataDirty = false;
   }

   @Override
   public String getInfo() {
      return String.valueOf(this.hole1x1.size() + this.hole3x3.size());
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent event) {
      if (this.isEnabled() && this.isInGame() && event.isLoaded()) {
         this.queueChunk(event.getChunkPos());
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         this.espFade.update();
         if (this.isEnabled() && this.isInGame()) {
            this.processChunkQueue();
            this.refreshChunkQueue();
            this.removeStaleChunks();
            if (this.rawDataDirty && this.rebuildRunning.compareAndSet(false, true)) {
               this.rawDataDirty = false;
               ExecutorService var2 = this.worker;
               if (var2 != null && !var2.isShutdown()) {
                  var2.execute(this::rebuildAndPublish);
               } else {
                  this.rebuildRunning.set(false);
               }
            }
         }
      }
   }

   private Color getHole1x1Color(int rgba) {
      if (this.cachedHole1x1Color == null || this.lastHole1x1Rgba != rgba) {
         this.lastHole1x1Rgba = rgba;
         this.cachedHole1x1Color = new Color(rgba, true);
      }

      return this.cachedHole1x1Color;
   }

   private Color getHole3x3Color(int rgba) {
      if (this.cachedHole3x3Color == null || this.lastHole3x3Rgba != rgba) {
         this.lastHole3x3Rgba = rgba;
         this.cachedHole3x3Color = new Color(rgba, true);
      }

      return this.cachedHole3x3Color;
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (mc != null && mc.field_1724 != null && mc.field_1687 != null) {
         if (!this.hole1x1.isEmpty() || !this.hole3x3.isEmpty()) {
            float var2 = this.espFade.getFade();
            if (!(var2 <= 0.001F)) {
               this.fbW = mc.method_22683().method_4489();
               this.fbH = mc.method_22683().method_4506();
               float var3 = mc.method_22683().method_4495();

               try (NanoVGFrame var4 = NanoVGRenderer.frame()) {
                  if (!var4.isActive()) {
                     return;
                  }

                  NanoVGRenderer.globalAlpha(var2);

                  try {
                     this.computeCamProj(this.fbW, this.fbH);
                     if ((Boolean)this.detect1x1.get() && !this.hole1x1.isEmpty()) {
                        int var5 = (Integer)this.hole1x1Color.get();

                        for (class_238 var7 : this.hole1x1) {
                           this.drawHoleBox(var7, var5, var3);
                        }
                     }

                     if ((Boolean)this.detect3x3.get() && !this.hole3x3.isEmpty()) {
                        int var15 = (Integer)this.hole3x3Color.get();

                        for (class_238 var17 : this.hole3x3) {
                           this.drawHoleBox(var17, var15, var3);
                        }
                     }
                  } finally {
                     NanoVGRenderer.globalAlpha(1.0F);
                  }
               }
            }
         }
      }
   }

   private void drawHoleBox(class_238 box, int color, float scale) {
      double[][] var4 = this.projectBox(box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1324);
      if (var4 != null) {
         NanoVGRenderer.drawEspFill(var4, FACES, scale, color);
         NanoVGRenderer.drawEspBox(var4, EDGES, scale, 1.2F, color);
      }
   }

   private double[][] projectBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
      boolean var13 = false;
      if (this.worldToScreen(minX, minY, minZ, this.scratchCoords[0])) {
         this.scratchPts[0] = this.scratchCoords[0];
         var13 = true;
      } else {
         this.scratchPts[0] = null;
      }

      if (this.worldToScreen(maxX, minY, minZ, this.scratchCoords[1])) {
         this.scratchPts[1] = this.scratchCoords[1];
         var13 = true;
      } else {
         this.scratchPts[1] = null;
      }

      if (this.worldToScreen(maxX, minY, maxZ, this.scratchCoords[2])) {
         this.scratchPts[2] = this.scratchCoords[2];
         var13 = true;
      } else {
         this.scratchPts[2] = null;
      }

      if (this.worldToScreen(minX, minY, maxZ, this.scratchCoords[3])) {
         this.scratchPts[3] = this.scratchCoords[3];
         var13 = true;
      } else {
         this.scratchPts[3] = null;
      }

      if (this.worldToScreen(minX, maxY, minZ, this.scratchCoords[4])) {
         this.scratchPts[4] = this.scratchCoords[4];
         var13 = true;
      } else {
         this.scratchPts[4] = null;
      }

      if (this.worldToScreen(maxX, maxY, minZ, this.scratchCoords[5])) {
         this.scratchPts[5] = this.scratchCoords[5];
         var13 = true;
      } else {
         this.scratchPts[5] = null;
      }

      if (this.worldToScreen(maxX, maxY, maxZ, this.scratchCoords[6])) {
         this.scratchPts[6] = this.scratchCoords[6];
         var13 = true;
      } else {
         this.scratchPts[6] = null;
      }

      if (this.worldToScreen(minX, maxY, maxZ, this.scratchCoords[7])) {
         this.scratchPts[7] = this.scratchCoords[7];
         var13 = true;
      } else {
         this.scratchPts[7] = null;
      }

      return var13 ? this.scratchPts : null;
   }

   private void computeCamProj(int fw, int fh) {
      class_4184 var3 = mc.field_1773.method_19418();
      class_243 var4 = var3.method_71156();
      double var5 = Math.toRadians(var3.method_19330() + 180.0);
      double var7 = Math.toRadians(var3.method_19329());
      float var9 = (float)Math.tan(Math.toRadians(((Integer)mc.field_1690.method_41808().method_41753()).intValue()) / 2.0);
      float var10 = TrueStretched.getEffectiveAspectRatio(fw, fh);
      this.camProj.update(var4.field_1352, var4.field_1351, var4.field_1350, Math.cos(var5), Math.sin(var5), Math.cos(var7), Math.sin(var7), var9, var10);
   }

   private boolean worldToScreen(double wx, double wy, double wz, double[] out) {
      HoleESP.CamProj var8 = this.camProj;
      float var9 = (float)(wx - var8.camX);
      float var10 = (float)(wy - var8.camY);
      float var11 = (float)(wz - var8.camZ);
      float var12 = (float)(var9 * var8.cosYaw + var11 * var8.sinYaw);
      float var13 = var10;
      float var14 = (float)(-var9 * var8.sinYaw + var11 * var8.cosYaw);
      float var15 = (float)(var13 * var8.cosPitch - var14 * var8.sinPitch);
      float var16 = (float)(var13 * var8.sinPitch + var14 * var8.cosPitch);
      if (var16 >= 0.0F) {
         return false;
      }

      out[0] = var12 / -var16 / (var8.tanHalfFov * var8.aspect) * (this.fbW / 2.0) + this.fbW / 2.0;
      out[1] = -var15 / -var16 / var8.tanHalfFov * (this.fbH / 2.0) + this.fbH / 2.0;
      return true;
   }

   private void queueChunk(int cx, int cz) {
      long var3 = class_1923.method_8331(cx, cz);
      if (!this.queuedChunkKeys.contains(var3) && this.queuedChunkKeys.size() < (96)) {
         class_2818 var5 = mc.field_1687.method_2935().method_12126(cx, cz, false);
         if (var5 != null) {
            this.queuedChunkKeys.add(var3);
            this.chunkQueue.add(var5);
         }
      }
   }

   private void queueChunk(class_1923 pos) {
      if (pos != null) {
         this.queueChunk(pos.field_9181, pos.field_9180);
      }
   }

   private void refreshChunkQueue() {
      if (this.isInGame()) {
         class_1923 var1 = mc.field_1724.method_31476();
         int var2 = Math.max(1, mc.field_1690.method_38521());

         for (int var3 = var1.field_9181 - var2; var3 <= var1.field_9181 + var2; var3++) {
            for (int var4 = var1.field_9180 - var2; var4 <= var1.field_9180 + var2; var4++) {
               if (this.queuedChunkKeys.size() >= (96)) {
                  return;
               }

               this.queueChunk(var3, var4);
            }
         }
      }
   }

   private void removeStaleChunks() {
      if (this.isInGame()) {
         class_1923 var1 = mc.field_1724.method_31476();
         int var2 = Math.max(1, mc.field_1690.method_38521());
         int var3 = var1.field_9181 - var2;
         int var4 = var1.field_9181 + var2;
         int var5 = var1.field_9180 - var2;
         int var6 = var1.field_9180 + var2;
         boolean var7 = this.rawByChunk.keySet().removeIf(key -> {
            int var5x = class_1923.method_8325(key);
            int var6x = class_1923.method_8332(key);
            return var5x < var3 || var5x > var4 || var6x < var5 || var6x > var6;
         });
         if (var7) {
            this.rawDataDirty = true;
         }

         if (!this.hole1x1.isEmpty()) {
            this.hole1x1.removeIf(box -> {
               int var5x = (int)Math.floor(box.field_1323) >> (4);
               int var6x = (int)Math.floor(box.field_1321) >> (4);
               return var5x < var3 || var5x > var4 || var6x < var5 || var6x > var6;
            });
         }

         if (!this.hole3x3.isEmpty()) {
            this.hole3x3.removeIf(box -> {
               int var5x = (int)Math.floor(box.field_1323) >> (4);
               int var6x = (int)Math.floor(box.field_1321) >> (4);
               return var5x < var3 || var5x > var4 || var6x < var5 || var6x > var6;
            });
         }
      }
   }

   private void processChunkQueue() {
      int var1 = 5;

      while (var1 > 0 && !this.chunkQueue.isEmpty()) {
         class_2818 var2 = (class_2818)this.chunkQueue.pollFirst();
         if (var2 != null) {
            long var3 = class_1923.method_8331(var2.method_12004().field_9181, var2.method_12004().field_9180);
            this.queuedChunkKeys.remove(var3);
            var1--;
            if (mc.field_1687.method_2935().method_12123(var2.method_12004().field_9181, var2.method_12004().field_9180)) {
               this.scanChunkAsync(var2, var3);
            }
         }
      }
   }

   private void scanChunkAsync(class_2818 chunk, long key) {
      ExecutorService var4 = this.worker;
      if (var4 != null) {
         var4.execute(() -> {
            try {
               Map var4x = this.scanChunk(chunk);
               mc.execute(() -> {
                  if (this.isEnabled() && this.isInGame()) {
                     if (var4x.isEmpty()) {
                        this.rawByChunk.remove(key);
                     } else {
                        this.rawByChunk.put(key, var4x);
                     }

                     this.rawDataDirty = true;
                  }
               });
            } catch (Exception var5) {
            }
         });
      }
   }

   private Map scanChunk(class_2818 chunk) {
      class_638 var2 = mc.field_1687;
      if (var2 == null) {
         return Collections.emptyMap();
      }

      class_2826[] var3 = chunk.method_12006();
      boolean var4 = false;

      for (class_2826 var8 : var3) {
         if (var8 != null && !var8.method_38292()) {
            var4 = true;
            break;
         }
      }

      if (!var4) {
         return Collections.emptyMap();
      }

      int var18 = var2.method_31607();
      int var19 = var2.method_31600();
      int var20 = var18;
      int var21 = var19;
      int var9 = chunk.method_12004().method_8326();
      int var10 = chunk.method_12004().method_8328();
      class_2902 var11 = chunk.method_12032(class_2903.field_13202);
      int var12 = var20;

      for (int var13 = 0; var13 < (16); var13++) {
         for (int var14 = 0; var14 < (16); var14++) {
            var12 = Math.max(var12, var11.method_12603(var13, var14));
         }
      }

      int var22 = Math.min(var21, var12 + (20));
      HashMap var23 = new HashMap();
      class_2339 var15 = new class_2339();

      for (int var16 = 0; var16 < (16); var16++) {
         for (int var17 = 0; var17 < (16); var17++) {
            this.scanColumn(chunk, var16, var17, var9 + var16, var10 + var17, var22, var20, var21, var23, var15);
         }
      }

      return var23;
   }

   private void scanColumn(class_2818 chunk, int lx, int lz, int x, int z, int topYForChunk, int scanMinY, int scanMaxY, Map out, class_2339 scratch) {
      class_638 var11 = mc.field_1687;
      if (var11 != null) {
         int var12 = (Integer)this.minHoleDepth.get() * 14 / 4;
         byte var13 = 70;
         byte var14 = 3;
         byte var15 = 2;

         for (int var16 = topYForChunk; var16 >= scanMinY + var12; var16--) {
            if (this.isHoleInteriorBlock(var11, scratch, x, var16, z)) {
               scratch.method_10103(x, var16, z);
               boolean var17 = !var11.method_8320(scratch).method_26215();
               scratch.method_10103(x, var16 + 1, z);
               class_2680 var18 = var11.method_8320(scratch);
               boolean var19 = var18.method_26215();
               boolean var20 = !var18.method_26227().method_15769();
               boolean var21;
               if (var17) {
                  if (var19 || var20) {
                     continue;
                  }

                  var21 = true;
               } else {
                  var21 = false;
                  if (var19 && (!this.isHoleSection(var11, scratch, x, var16, z) || this.isHoleSection(var11, scratch, x, var16 + 1, z))) {
                     continue;
                  }
               }

               int var22 = var16;
               int var23 = var16;
               int var24 = 0;
               int var25 = 0;
               int var26 = 0;
               int[] var27 = new int[16];
               int var28 = 0;
               int var29 = 0;
               int var30 = 0;
               int var31 = var16;
               boolean var32 = false;

               while (var23 >= scanMinY && var16 - var23 + 1 <= var13) {
                  if (this.isHoleInteriorBlock(var11, scratch, x, var23, z)) {
                     if (var24 > 0) {
                        if (++var25 > var15) {
                           break;
                        }

                        var24 = 0;
                     }

                     var22 = var23;
                     var26++;
                     if (!var32 && this.isNaturalVegetationBlock(var11, scratch, x, var23, z)) {
                        var32 = true;
                     }

                     byte var33 = 0;
                     if (this.isSolidWall(var11, scratch, x, var23, z - 1)) {
                        var33 |= 1;
                     }

                     if (this.isSolidWall(var11, scratch, x, var23, z + 1)) {
                        var33 |= 2;
                     }

                     if (this.isSolidWall(var11, scratch, x - 1, var23, z)) {
                        var33 |= 4;
                     }

                     if (this.isSolidWall(var11, scratch, x + 1, var23, z)) {
                        var33 |= 8;
                     }

                     var27[var33]++;
                     if (Integer.bitCount(var33) >= 3) {
                        var30++;
                        var31 = var23;
                     }

                     if (var33 == 15) {
                        var28++;
                     }

                     int var34 = 0;
                     if (this.isSolidWall(var11, scratch, x, var23, z - 2)) {
                        var34++;
                     }

                     if (this.isSolidWall(var11, scratch, x, var23, z + 2)) {
                        var34++;
                     }

                     if (this.isSolidWall(var11, scratch, x - 2, var23, z)) {
                        var34++;
                     }

                     if (this.isSolidWall(var11, scratch, x + 2, var23, z)) {
                        var34++;
                     }

                     if (var34 >= 3) {
                        var29++;
                     }
                  } else if (++var24 > var14) {
                     break;
                  }

                  var23--;
               }

               int var38 = var22;
               int var39 = var16 - var38 + 1;
               if (var39 >= var12) {
                  int var35 = -1;
                  int var36 = 0;

                  for (int var37 = 0; var37 < 16; var37++) {
                     if (var27[var37] > var36) {
                        var36 = var27[var37];
                        var35 = var37;
                     }
                  }

                  out.put(new class_2338(x, var16, z), new HoleESP.RawColumn(var16, var39, var26, var30, var31, var35, var36, var28, var29, var32, var21));
               }

               var16 = var38;
            }
         }
      }
   }

   private boolean isHoleInteriorBlock(class_1937 world, class_2339 scratch, int x, int y, int z) {
      scratch.method_10103(x, y, z);
      class_2680 var6 = world.method_8320(scratch);
      return var6.method_26215() || !var6.method_26227().method_15769();
   }

   private boolean isHoleSection(class_1937 world, class_2339 scratch, int x, int y, int z) {
      scratch.method_10103(x, y, z);
      if (!this.isHoleInteriorBlock(world, scratch, x, y, z)) {
         return false;
      }

      int var6 = 0;
      if (this.isSolidWall(world, scratch, x, y, z - 1)) {
         var6++;
      }

      if (this.isSolidWall(world, scratch, x, y, z + 1)) {
         var6++;
      }

      if (this.isSolidWall(world, scratch, x - 1, y, z)) {
         var6++;
      }

      if (this.isSolidWall(world, scratch, x + 1, y, z)) {
         var6++;
      }

      byte var7 = 1;
      return (4) - var6 <= var7;
   }

   private void rebuildAndPublish() {
      try {
         class_638 var1 = mc.field_1687;
         if (var1 == null) {
            return;
         }

         HashMap var2 = new HashMap();

         for (Map var4 : this.rawByChunk.values()) {
            var2.putAll(var4);
         }

         HashSet var7 = new HashSet();
         Set var8 = this.detect3x3.get() ? this.rebuild3x3(var1, var2, var7) : Collections.emptySet();
         Set var5 = this.detect1x1.get() ? this.rebuild1x1(var1, var2, var7) : Collections.emptySet();
         mc.execute(() -> {
            this.hole1x1.clear();
            this.hole1x1.addAll(var5);
            this.hole3x3.clear();
            this.hole3x3.addAll(var8);
            this.rebuildRunning.set(false);
         });
      } catch (Exception var6) {
         this.rebuildRunning.set(false);
      }
   }

   private Set rebuild3x3(class_1937 world, Map allRaw, Set consumed) {
      Set var4 = Collections.newSetFromMap(new ConcurrentHashMap());
      class_2339 var5 = new class_2339();
      int[][] var6 = new int[][]{{3, 3}, {3, 2}, {2, 3}, {3, 1}, {1, 3}, {2, 2}, {2, 1}, {1, 2}};

      for (Entry var8 : allRaw.entrySet()) {
         class_2338 var9 = (class_2338)var8.getKey();
         if (!consumed.contains(var9)) {
            HoleESP.RawColumn var10 = (HoleESP.RawColumn)var8.getValue();
            int var11 = var10.topY();
            boolean var12 = false;

            for (int[] var16 : var6) {
               if (var12) {
                  break;
               }

               int var17 = var16[0];
               int var18 = var16[1];

               for (int var19 = 0; var19 < var17 && !var12; var19++) {
                  for (int var20 = 0; var20 < var18 && !var12; var20++) {
                     int var21 = var9.method_10263() - var19;
                     int var22 = var9.method_10260() - var20;
                     boolean var23 = true;
                     ArrayList var24 = new ArrayList(var17 * var18);
                     int var25 = var10.shaftBottomY();
                     int var26 = var10.topY();

                     for (int var27 = 0; var27 < var17 && var23; var27++) {
                        for (int var28 = 0; var28 < var18 && var23; var28++) {
                           int var29 = var21 + var27;
                           int var30 = var22 + var28;
                           class_2338 var31 = new class_2338(var29, var11, var30);
                           HoleESP.RawColumn var32 = (HoleESP.RawColumn)allRaw.get(var31);
                           if (var32 == null) {
                              var31 = new class_2338(var29, var11 + 1, var30);
                              var32 = (HoleESP.RawColumn)allRaw.get(var31);
                              if (var32 == null) {
                                 var31 = new class_2338(var29, var11 - 1, var30);
                                 var32 = (HoleESP.RawColumn)allRaw.get(var31);
                              }
                           }

                           if (var32 != null) {
                              if (consumed.contains(var31)) {
                                 var23 = false;
                                 break;
                              }

                              var24.add(var31);
                              var25 = Math.max(var25, var32.shaftBottomY());
                              var26 = Math.max(var26, var32.topY());
                           } else {
                              for (int var33 = var26; var33 >= var25; var33--) {
                                 if (!this.isHoleInteriorBlock(world, var5, var29, var33, var30)) {
                                    var23 = false;
                                    break;
                                 }
                              }
                           }
                        }
                     }

                     if (var23 && !var24.isEmpty()) {
                        int var34 = var26 - var25 + 1;
                        int var35 = (Integer)this.minHoleDepth.get() * 14 / 4;
                        if (var34 >= var35) {
                           int var36 = (2 * var17 + 2 * var18) * var34;
                           int var37 = 0;

                           for (int var38 = var26; var38 >= var25; var38--) {
                              for (int var40 = 0; var40 < var17; var40++) {
                                 if (!this.isSolidWall(world, var5, var21 + var40, var38, var22 - 1)) {
                                    var37++;
                                 }

                                 if (!this.isSolidWall(world, var5, var21 + var40, var38, var22 + var18)) {
                                    var37++;
                                 }
                              }

                              for (int var41 = 0; var41 < var18; var41++) {
                                 if (!this.isSolidWall(world, var5, var21 - 1, var38, var22 + var41)) {
                                    var37++;
                                 }

                                 if (!this.isSolidWall(world, var5, var21 + var17, var38, var22 + var41)) {
                                    var37++;
                                 }
                              }
                           }

                           double var39 = (double)var37 / Math.max(1, var36);
                           if (var39 <= 0.15) {
                              var4.add(new class_238(var21, var25, var22, var21 + var17, var26 + 1, var22 + var18));
                              consumed.addAll(var24);
                              var12 = true;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var4;
   }

   private Set rebuild1x1(class_1937 world, Map allRaw, Set consumed) {
      Set var4 = Collections.newSetFromMap(new ConcurrentHashMap());
      class_2339 var5 = new class_2339();

      for (Entry var7 : allRaw.entrySet()) {
         class_2338 var8 = (class_2338)var7.getKey();
         if (!consumed.contains(var8)) {
            HoleESP.RawColumn var9 = (HoleESP.RawColumn)var7.getValue();
            if (!var9.hasVegetation() || var9.isUnderwaterContext()) {
               int var10 = var9.depth();
               int var11 = Math.max(1, var9.shaftLayers());
               int var12 = var9.topY();
               int var13 = var9.shaftBottomY();
               int var14 = (Integer)this.minHoleDepth.get() * 14 / 4;
               if (var9.shaftLayers() >= var14) {
                  double var15 = (double)var9.dominantCount() / var11;
                  double var17 = (double)var9.outerSolidLayers() / var11;
                  boolean var19 = var15 >= 0.85;
                  boolean var20 = Integer.bitCount(var9.dominantPattern()) >= 3;
                  boolean var21 = var17 >= 0.8;
                  boolean var22 = var13 <= world.method_31607()
                     || this.isSolidWall(world, var5, var8.method_10263(), var13 - 1, var8.method_10260())
                     || var9.fullySolidLayers() >= var11 * 2 / 3;
                  boolean var23 = false;
                  if (!var9.isUnderwaterContext()) {
                     int[] var24 = new int[]{0, 0, -1, 1};
                     int[] var25 = new int[]{-1, 1, 0, 0};
                     int[] var26 = new int[]{var12, var13};

                     label107:
                     for (int var30 : var26) {
                        for (int var31 = 0; var31 < 4; var31++) {
                           if ((var9.dominantPattern() & 1 << var31) == 0) {
                              int var32 = var24[var31];
                              int var33 = var25[var31];
                              int var34 = 0;

                              for (int var35 = 1; var35 <= 5; var35++) {
                                 var5.method_10103(var8.method_10263() + var32 * var35, var30, var8.method_10260() + var33 * var35);
                                 if (!world.method_8320(var5).method_26215()) {
                                    break;
                                 }

                                 var34++;
                              }

                              if (var34 >= 5) {
                                 var23 = true;
                                 break label107;
                              }
                           }
                        }
                     }
                  }

                  if (var19 && var20 && var21 && var22 && !var23) {
                     var4.add(new class_238(var8.method_10263(), var13, var8.method_10260(), var8.method_10263() + 1, var12 + 1, var8.method_10260() + 1));
                  }
               }
            }
         }
      }

      return var4;
   }

   private boolean isNaturalVegetationBlock(class_1937 world, class_2339 scratch, int x, int y, int z) {
      scratch.method_10103(x, y, z);
      class_2248 var6 = world.method_8320(scratch).method_26204();
      if (var6 != class_2246.field_9993
         && var6 != class_2246.field_10463
         && var6 != class_2246.field_10376
         && var6 != class_2246.field_10238
         && var6 != class_2246.field_10597
         && var6 != class_2246.field_28675
         && var6 != class_2246.field_28676
         && var6 != class_2246.field_28411
         && var6 != class_2246.field_10588) {
         String var10000 = var6.method_63499();
         byte[] var10001 = new byte[5];
         var10001[0] = (byte)(86);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(110);
         var10001[3] = (byte)(-65);
         var10001[4] = (byte)(-75);
         if (!var10000.contains($$v_s(var10001, 2190429886235267088L))) {
            return false;
         }
      }

      return true;
   }

   private boolean isSolidWall(class_1937 world, class_2339 scratch, int x, int y, int z) {
      scratch.method_10103(x, y, z);
      class_2680 var6 = world.method_8320(scratch);
      if (!var6.method_26215() && var6.method_26227().method_15769()) {
         class_2248 var7 = var6.method_26204();
         return var7 != class_2246.field_10033
            && var7 != class_2246.field_10285
            && var7 != class_2246.field_10597
            && var7 != class_2246.field_28675
            && var7 != class_2246.field_28676
            && var7 != class_2246.field_9993
            && var7 != class_2246.field_10463
            && var7 != class_2246.field_10376
            && var7 != class_2246.field_10238;
      } else {
         return false;
      }
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   private static final class CamProj {
      double camX;
      double camY;
      double camZ;
      double cosYaw;
      double sinYaw;
      double cosPitch;
      double sinPitch;
      float tanHalfFov;
      float aspect;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      void update(double camX, double camY, double camZ, double cosYaw, double sinYaw, double cosPitch, double sinPitch, float tanHalfFov, float aspect) {
         this.camX = camXx;
         this.camY = camYx;
         this.camZ = camZx;
         this.cosYaw = cosYawx;
         this.sinYaw = sinYawx;
         this.cosPitch = cosPitchx;
         this.sinPitch = sinPitchx;
         this.tanHalfFov = tanHalfFovx;
         this.aspect = aspectx;
      }
   }

   private record RawColumn(
      int topY,
      int depth,
      int interiorLayers,
      int shaftLayers,
      int shaftBottomY,
      int dominantPattern,
      int dominantCount,
      int fullySolidLayers,
      int outerSolidLayers,
      boolean hasVegetation,
      boolean isUnderwaterContext
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
