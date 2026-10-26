package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.util.render.EspFade;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2561;
import net.minecraft.class_2666;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2338.class_2339;

public final class BedrockHoleESP extends Module {
   private static final int[] OVERWORLD_Y_LEVELS;
   private static final int[] NETHER_FLOOR_Y_LEVELS;
   private static final int[] NETHER_ROOF_Y_LEVELS;
   private static final int CELL_MASK = 255;
   private static final int MAX_GROUP_SIZE = 200;
   private final IntSetting minVoidSize = (IntSetting)this.addSetting(
      new IntSetting(
         "MinVoidSize",
         "Minimum connected non-bedrock blocks to flag",
         2,
         1,
         50
      )
   );
   private final BoolSetting showEsp = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ShowESP",
         "Render bedrock-hole ESP boxes",
         true
      )
   );
   private final BoolSetting showTracers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ShowTracers",
         "Render tracers to bedrock holes",
         false
      )
   );
   private final IntSetting alpha = (IntSetting)this.addSetting(
      new IntSetting(
         "Alpha",
         "Global ESP opacity",
         255,
         0,
         255
      )
   );
   private final BoolSetting chatFeedback = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ChatFeedback",
         "Announce newly found holes in chat",
         true
      )
   );
   private final IntSetting maxMessagesPerMinute = (IntSetting)this.addSetting(
      new IntSetting(
         "MaxMsgPerMin",
         "Maximum chat messages per minute (0 = unlimited)",
         10,
         0,
         60
      )
   );
   private final ColorSetting espColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "ESPColor",
         "Color for bedrock-hole ESP and tracer",
         -2131733168
      )
   );
   private final BoolSetting useThreading = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Threading",
         "Use worker threads for chunk scan",
         true
      )
   );
   private final IntSetting threadPoolSize = (IntSetting)this.addSetting(
      new IntSetting(
         "ThreadPoolSize",
         "Number of scanner threads",
         4,
         1,
         8
      )
   );
   private final IntSetting rescanTicks = (IntSetting)this.addSetting(
      new IntSetting(
         "RescanTicks",
         "Ticks between automatic rescans",
         20,
         5,
         200
      )
   );
   private final IntSetting scanBudget = (IntSetting)this.addSetting(
      new IntSetting(
         "ScanBudget",
         "Chunks scanned each rescan tick",
         14,
         1,
         128
      )
   );
   private final IntSetting maxRender = (IntSetting)this.addSetting(
      new IntSetting(
         "MaxRender",
         "Maximum rendered hole blocks per frame",
         5000,
         100,
         20000
      )
   );
   private final Map chunkVoidBlocks = new ConcurrentHashMap();
   private final Map chunkAnchors = new ConcurrentHashMap();
   private final Map chunkScanSeq = new ConcurrentHashMap();
   private final Set knownAnchors = ConcurrentHashMap.newKeySet();
   private final AtomicInteger totalVoidBlocks = new AtomicInteger(0);
   private volatile ExecutorService threadPool;
   private String currentDimension = "";
   private int scanRingIndex = 0;
   private int tickCounter = 0;
   private long minuteWindow = -1L;
   private int messagesInWindow = 0;
   private final EspFade espFade = new EspFade(this::finishDisable);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BedrockHoleESP() {
      super(
         "BedrockHoleESP",
         "Finds connected non-bedrock holes in bedrock layers",
         Category.RENDER
      );
      this.minVoidSize.setVisibleInGui(false);
      this.maxMessagesPerMinute.setVisibleInGui(false);
      this.threadPoolSize.setVisibleInGui(false);
      this.rescanTicks.setVisibleInGui(false);
      this.scanBudget.setVisibleInGui(false);
      this.maxRender.setVisibleInGui(false);
      this.useThreading.setVisibleInGui(false);
      this.minVoidSize.onChanged(v -> this.triggerFullRescan());
      this.useThreading.onChanged(v -> this.rebuildThreadPoolIfNeeded());
      this.threadPoolSize.onChanged(v -> this.rebuildThreadPoolIfNeeded());
   }

   private void finishDisable() {
      this.shutdownThreadPool();
      this.clearAllDetections();
      super.onDisable();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (this.isInGame()) {
         this.currentDimension = this.resolveDimension();
         this.tickCounter = 0;
         this.scanRingIndex = 0;
         this.minuteWindow = -1L;
         this.messagesInWindow = 0;
         this.clearAllDetections();
         this.rebuildThreadPoolIfNeeded();
         this.fullRescan();
         this.espFade.onEnable();
      }
   }

   @Override
   public void onDisable() {
      this.espFade.onDisable();
   }

   @Override
   public String getInfo() {
      return Integer.toString(this.totalVoidBlocks.get());
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent event) {
      if (this.isEnabled() && this.isInGame()) {
         if (event.isLoaded()) {
            this.scheduleChunkScan(event.getChunkPos());
         } else {
            this.removeChunkData(event.getChunkPos());
         }
      }
   }

   @EventHandler
   public void onReceivePacket(ReceivePacketEvent event) {
      if (this.isEnabled()) {
         if (event.getPacket() instanceof class_2666 var2) {
            this.removeChunkData(var2.comp_1726());
         }
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         this.espFade.update();
         if (this.isEnabled() && this.isInGame()) {
            String var2 = this.resolveDimension();
            if (!var2.equals(this.currentDimension)) {
               this.currentDimension = var2;
               this.clearAllDetections();
               this.fullRescan();
            } else {
               this.pruneOutsideRenderDistance();
               if (++this.tickCounter >= (Integer)this.rescanTicks.get()) {
                  this.tickCounter = 0;
                  this.rescanNearby();
               }
            }
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame()) {
         if ((Boolean)this.showEsp.get() || (Boolean)this.showTracers.get()) {
            if (!this.chunkVoidBlocks.isEmpty()) {
               float var2 = this.espFade.getFade();
               if (!(var2 <= 0.001F)) {
                  boolean var3 = (Boolean)this.showEsp.get();
                  boolean var4 = (Boolean)this.showTracers.get();
                  int var5 = (int)(((Integer)this.alpha.get()).intValue() * var2);
                  int var6 = (int)(255.0F * var2);
                  if (var3 && var5 > 0 || var4 && var6 > 0) {
                     int var7 = applyOpacity((Integer)this.espColor.get(), var5);
                     int var8 = applyOpacity((Integer)this.espColor.get(), var6);
                     int var9 = Math.max(1, (Integer)this.maxRender.get());
                     float var10 = event.getTickDelta();
                     int var11 = 0;

                     for (Set var13 : this.chunkVoidBlocks.values()) {
                        for (class_2338 var15 : var13) {
                           if (var11 >= var9) {
                              return;
                           }

                           int var16 = var15.method_10263();
                           int var17 = var15.method_10264();
                           int var18 = var15.method_10260();
                           if (var3 && var5 > 0) {
                              RenderUtil.drawFilledBox(var16, var17, var18, var16 + 1, var17 + 1, var18 + 1, var7);
                           }

                           if (var4 && var6 > 0) {
                              RenderUtil.drawTracerToPos(var16 + 0.5, var17 + 0.5, var18 + 0.5, var8, var10);
                           }

                           var11++;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void triggerFullRescan() {
      if (this.isEnabled() && this.isInGame()) {
         this.clearAllDetections();
         this.fullRescan();
      }
   }

   private void rebuildThreadPoolIfNeeded() {
      if (this.isEnabled()) {
         this.shutdownThreadPool();
         if ((Boolean)this.useThreading.get()) {
            int var1 = Math.max(1, (Integer)this.threadPoolSize.get());
            this.threadPool = Executors.newFixedThreadPool(var1, r -> {
               byte[] var10003 = new byte[21];
               var10003[0] = (byte)(83);
               var10003[1] = (byte)(-13);
               var10003[2] = (byte)(121);
               var10003[3] = (byte)(-13);
               var10003[4] = (byte)(-123);
               var10003[5] = (byte)(-34);
               var10003[6] = (byte)(48);
               var10003[7] = (byte)(125);
               var10003[8] = (byte)(34);
               var10003[9] = (byte)(-63);
               var10003[10] = (byte)(-113);
               var10003[11] = (byte)(-37);
               var10003[12] = (byte)(-41);
               var10003[13] = (byte)(12);
               var10003[14] = (byte)(16);
               var10003[15] = (byte)(18);
               var10003[16] = (byte)(88);
               var10003[17] = (byte)(57);
               var10003[18] = (byte)(-81);
               var10003[19] = (byte)(-49);
               var10003[20] = (byte)(57);
               Thread var1x = new Thread(r, $$v_s(var10003, 2834845946103451648L));
               var1x.setDaemon(true);
               return var1x;
            });
         }
      }
   }

   private void shutdownThreadPool() {
      ExecutorService var1 = this.threadPool;
      this.threadPool = null;
      if (var1 != null && !var1.isShutdown()) {
         var1.shutdownNow();
      }
   }

   private void clearAllDetections() {
      this.chunkVoidBlocks.clear();
      this.chunkAnchors.clear();
      this.chunkScanSeq.clear();
      this.knownAnchors.clear();
      this.totalVoidBlocks.set(0);
   }

   private void fullRescan() {
      if (this.isInGame()) {
         class_1923 var1 = mc.field_1724.method_31476();
         int var2 = Math.max(1, mc.field_1690.method_38521());

         for (int var3 = var1.field_9181 - var2; var3 <= var1.field_9181 + var2; var3++) {
            for (int var4 = var1.field_9180 - var2; var4 <= var1.field_9180 + var2; var4++) {
               this.scheduleChunkScan(var3, var4);
            }
         }
      }
   }

   private void rescanNearby() {
      if (this.isInGame()) {
         class_1923 var1 = mc.field_1724.method_31476();
         int var2 = Math.max(1, (Integer)this.scanBudget.get());
         int var3 = Math.max(1, mc.field_1690.method_38521());

         label66:
         for (int var4 = -1; var4 <= 1; var4++) {
            for (int var5 = -1; var5 <= 1; var5++) {
               if (var2 <= 0) {
                  break label66;
               }

               this.scheduleChunkScan(var1.field_9181 + var4, var1.field_9180 + var5);
               var2--;
            }
         }

         if (var2 > 0 && var3 > 1) {
            int var9 = Math.max(1, var3 - 1);
            int var10 = Math.min(var3, this.scanRingIndex % var9 + (2));
            this.scanRingIndex++;

            for (int var6 = -var10; var6 <= var10; var6++) {
               if (Math.abs(var6) == var10) {
                  for (int var7 = -var10; var7 <= var10; var7++) {
                     if (var2 <= 0) {
                        return;
                     }

                     this.scheduleChunkScan(var1.field_9181 + var6, var1.field_9180 + var7);
                     var2--;
                  }
               } else {
                  if (var2 <= 0) {
                     break;
                  }

                  this.scheduleChunkScan(var1.field_9181 + var6, var1.field_9180 - var10);
                  if (--var2 <= 0) {
                     break;
                  }

                  this.scheduleChunkScan(var1.field_9181 + var6, var1.field_9180 + var10);
                  var2--;
               }
            }
         }
      }
   }

   private void pruneOutsideRenderDistance() {
      if (this.isInGame()) {
         class_1923 var1 = mc.field_1724.method_31476();
         int var2 = Math.max(1, mc.field_1690.method_38521());

         for (long var4 : this.chunkScanSeq.keySet()) {
            int var6 = class_1923.method_8325(var4);
            int var7 = class_1923.method_8332(var4);
            if (!isWithinRenderDistance(var6, var7, var1.field_9181, var1.field_9180, var2)) {
               this.removeChunkData(var4);
            }
         }
      }
   }

   private void scheduleChunkScan(int cx, int cz) {
      if (this.isInGame()) {
         class_2818 var3 = mc.field_1687.method_2935().method_12126(cx, cz, false);
         if (var3 == null) {
            this.removeChunkData(cx, cz);
         } else {
            this.scheduleChunkScan(var3);
         }
      }
   }

   private void scheduleChunkScan(class_1923 pos) {
      if (pos != null) {
         this.scheduleChunkScan(pos.field_9181, pos.field_9180);
      }
   }

   private void scheduleChunkScan(class_2818 chunk) {
      if (this.isInGame()) {
         int[] var2 = getYLevelsForDimension(this.currentDimension);
         if (var2.length != 0) {
            long var3 = class_1923.method_8331(chunk.method_12004().field_9181, chunk.method_12004().field_9180);
            int var5 = this.chunkScanSeq.merge(var3, 1, Integer::sum);
            BedrockHoleESP.ChunkSnapshot var6 = this.captureSnapshot(chunk, var2);
            if (var6 != null) {
               Runnable var7 = () -> {
                  BedrockHoleESP.ScanResult var5x = this.scanSnapshot(var6);
                  Runnable var6x = () -> this.applyScanResult(var3, var5, var5x);
                  if (mc != null) {
                     mc.execute(var6x);
                  } else {
                     var6x.run();
                  }
               };
               ExecutorService var8 = this.threadPool;
               if ((Boolean)this.useThreading.get() && var8 != null && !var8.isShutdown()) {
                  var8.execute(var7);
               } else {
                  var7.run();
               }
            }
         }
      }
   }

   private BedrockHoleESP.ChunkSnapshot captureSnapshot(class_2818 chunk, int[] yLevels) {
      if (mc != null && mc.field_1687 != null && chunk != null) {
         int var3 = chunk.method_12004().method_8326();
         int var4 = chunk.method_12004().method_8328();
         boolean[][] var5 = new boolean[yLevels.length][256];
         class_2339 var6 = new class_2339();

         for (int var7 = 0; var7 < yLevels.length; var7++) {
            int var8 = yLevels[var7];

            for (int var9 = 0; var9 < (16); var9++) {
               int var10 = var3 + var9;

               for (int var11 = 0; var11 < (16); var11++) {
                  int var12 = var4 + var11;
                  var6.method_10103(var10, var8, var12);
                  class_2680 var13 = mc.field_1687.method_8320(var6);
                  var5[var7][packCell(var9, var11)] = isBedrock(var13);
               }
            }
         }

         return new BedrockHoleESP.ChunkSnapshot(var3, var4, yLevels, var5);
      } else {
         return null;
      }
   }

   private BedrockHoleESP.ScanResult scanSnapshot(BedrockHoleESP.ChunkSnapshot snapshot) {
      int[] var2 = snapshot.yLevels;
      boolean[][] var3 = snapshot.bedrock;
      boolean[][] var4 = new boolean[var2.length][256];
      HashMap var5 = new HashMap(var2.length * (2));

      for (int var6 = 0; var6 < var2.length; var6++) {
         var5.put(var2[var6], var6);
      }

      HashSet var24 = new HashSet();
      ArrayList var7 = new ArrayList();
      int var8 = Math.max(1, (Integer)this.minVoidSize.get());

      for (int var9 = 0; var9 < var2.length; var9++) {
         for (int var10 = 0; var10 < (256); var10++) {
            if (!var4[var9][var10] && !var3[var9][var10]) {
               Set var11 = this.floodFill(snapshot, var9, var10, var5, var4);
               if (var11.size() >= var8 && this.isVoidEnclosed(snapshot, var11, var5)) {
                  long var12 = Long.MAX_VALUE;
                  class_2338 var14 = null;

                  for (int var16 : var11) {
                     int var17 = unpackLayer(var16);
                     int var18 = unpackCell(var16);
                     int var19 = unpackLocalX(var18);
                     int var20 = unpackLocalZ(var18);
                     class_2338 var21 = new class_2338(snapshot.startX + var19, var2[var17], snapshot.startZ + var20);
                     var24.add(var21);
                     long var22 = var21.method_10063();
                     if (var22 < var12) {
                        var12 = var22;
                        var14 = var21;
                     }
                  }

                  if (var14 != null && var12 != Long.MAX_VALUE) {
                     var7.add(new BedrockHoleESP.VoidGroup(var12, var14, var11.size()));
                  }
               }
            }
         }
      }

      return new BedrockHoleESP.ScanResult(var24, var7);
   }

   private Set floodFill(BedrockHoleESP.ChunkSnapshot snapshot, int startLayer, int startCell, Map yToIndex, boolean[][] processed) {
      ArrayDeque var6 = new ArrayDeque();
      HashSet var7 = new HashSet();
      var6.add(packCode(startLayer, startCell));

      while (!var6.isEmpty() && var7.size() < (200)) {
         int var8 = (Integer)var6.pollFirst();
         int var9 = unpackLayer(var8);
         int var10 = unpackCell(var8);
         if (!processed[var9][var10] && !snapshot.bedrock[var9][var10]) {
            processed[var9][var10] = true;
            var7.add(var8);
            int var11 = unpackLocalX(var10);
            int var12 = unpackLocalZ(var10);
            int var13 = snapshot.yLevels[var9];
            this.enqueueHorizontal(var6, var9, var11 + 1, var12);
            this.enqueueHorizontal(var6, var9, var11 - 1, var12);
            this.enqueueHorizontal(var6, var9, var11, var12 + 1);
            this.enqueueHorizontal(var6, var9, var11, var12 - 1);
            Integer var14 = (Integer)yToIndex.get(var13 + 1);
            if (var14 != null) {
               var6.addLast(packCode(var14, var10));
            }

            Integer var15 = (Integer)yToIndex.get(var13 - 1);
            if (var15 != null) {
               var6.addLast(packCode(var15, var10));
            }
         }
      }

      return var7;
   }

   private boolean isVoidEnclosed(BedrockHoleESP.ChunkSnapshot snapshot, Set group, Map yToIndex) {
      for (int var5 : group) {
         int var6 = unpackLayer(var5);
         int var7 = unpackCell(var5);
         int var8 = unpackLocalX(var7);
         int var9 = unpackLocalZ(var7);
         int var10 = snapshot.yLevels[var6];
         if (!this.checkHorizontalEnclosure(snapshot, group, var6, var8 + 1, var9)) {
            return false;
         }

         if (!this.checkHorizontalEnclosure(snapshot, group, var6, var8 - 1, var9)) {
            return false;
         }

         if (!this.checkHorizontalEnclosure(snapshot, group, var6, var8, var9 + 1)) {
            return false;
         }

         if (!this.checkHorizontalEnclosure(snapshot, group, var6, var8, var9 - 1)) {
            return false;
         }

         Integer var11 = (Integer)yToIndex.get(var10 + 1);
         if (var11 != null) {
            int var12 = packCode(var11, var7);
            if (!group.contains(var12) && !snapshot.bedrock[var11][var7]) {
               return false;
            }
         }

         Integer var14 = (Integer)yToIndex.get(var10 - 1);
         if (var14 != null) {
            int var13 = packCode(var14, var7);
            if (!group.contains(var13) && !snapshot.bedrock[var14][var7]) {
               return false;
            }
         }
      }

      return true;
   }

   private boolean checkHorizontalEnclosure(BedrockHoleESP.ChunkSnapshot snapshot, Set group, int layer, int lx, int lz) {
      if (lx >= 0 && lx <= (15) && lz >= 0 && lz <= (15)) {
         int var6 = packCell(lx, lz);
         int var7 = packCode(layer, var6);
         return group.contains(var7) ? true : snapshot.bedrock[layer][var6];
      } else {
         return true;
      }
   }

   private void enqueueHorizontal(ArrayDeque queue, int layer, int lx, int lz) {
      if (lx >= 0 && lx <= (15) && lz >= 0 && lz <= (15)) {
         queue.addLast(packCode(layer, packCell(lx, lz)));
      }
   }

   private void applyScanResult(long chunkKey, int seq, BedrockHoleESP.ScanResult result) {
      Integer var5 = (Integer)this.chunkScanSeq.get(chunkKey);
      if (var5 != null && var5 == seq) {
         Set var6 = result.blocks;
         Set var7 = (Set)this.chunkVoidBlocks.get(chunkKey);
         int var8 = var7 == null ? 0 : var7.size();
         int var9 = var6.size();
         this.totalVoidBlocks.addAndGet(var9 - var8);
         if (var6.isEmpty()) {
            this.chunkVoidBlocks.remove(chunkKey);
         } else {
            this.chunkVoidBlocks.put(chunkKey, var6);
         }

         Set var10 = this.chunkAnchors.getOrDefault(chunkKey, Collections.emptySet());
         HashSet var11 = new HashSet(result.groups.size());

         for (BedrockHoleESP.VoidGroup var13 : result.groups) {
            var11.add(var13.anchor);
         }

         if ((Boolean)this.chatFeedback.get()) {
            for (BedrockHoleESP.VoidGroup var15 : result.groups) {
               if (!var10.contains(var15.anchor) && !this.knownAnchors.contains(var15.anchor)) {
                  this.sendVoidMessage(var15);
               }
            }
         }

         if (!var10.isEmpty()) {
            this.knownAnchors.removeAll(var10);
         }

         if (var11.isEmpty()) {
            this.chunkAnchors.remove(chunkKey);
         } else {
            this.chunkAnchors.put(chunkKey, var11);
            this.knownAnchors.addAll(var11);
         }
      }
   }

   private void removeChunkData(int cx, int cz) {
      this.removeChunkData(class_1923.method_8331(cx, cz));
   }

   private void removeChunkData(class_1923 pos) {
      if (pos != null) {
         this.removeChunkData(pos.field_9181, pos.field_9180);
      }
   }

   private void removeChunkData(long key) {
      this.chunkScanSeq.remove(key);
      Set var3 = (Set)this.chunkVoidBlocks.remove(key);
      if (var3 != null && !var3.isEmpty()) {
         this.totalVoidBlocks.addAndGet(-var3.size());
      }

      Set var4 = (Set)this.chunkAnchors.remove(key);
      if (var4 != null && !var4.isEmpty()) {
         this.knownAnchors.removeAll(var4);
      }
   }

   private void sendVoidMessage(BedrockHoleESP.VoidGroup group) {
      if (this.isInGame() && mc.field_1724 != null) {
         if ((Boolean)this.chatFeedback.get()) {
            long var2 = System.currentTimeMillis() / 60000L;
            if (var2 != this.minuteWindow) {
               this.minuteWindow = var2;
               this.messagesInWindow = 0;
            }

            int var4 = (Integer)this.maxMessagesPerMinute.get();
            if (var4 <= 0 || this.messagesInWindow < var4) {
               String var5 = String.format(
                  "§d[BedrockHoleESP]§b Void found: §f%d blocks §7at §f%s",
                  group.size,
                  group.firstPos.method_23854()
               );
               mc.field_1724.method_7353(class_2561.method_43470(var5), false);
               this.messagesInWindow++;
            }
         }
      }
   }

   private String resolveDimension() {
      return mc != null && mc.field_1687 != null ? mc.field_1687.method_27983().method_29177().toString() : "";
   }

   private static int[] getYLevelsForDimension(String dimension) {
      byte[] var10000 = new byte[19];
      var10000[0] = (byte)(72);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(114);
      var10000[3] = (byte)(-69);
      var10000[4] = (byte)(-124);
      var10000[5] = (byte)(-55);
      var10000[6] = (byte)(53);
      var10000[7] = (byte)(105);
      var10000[8] = (byte)(57);
      var10000[9] = (byte)(-104);
      var10000[10] = (byte)(-117);
      var10000[11] = (byte)(-128);
      var10000[12] = (byte)(-38);
      var10000[13] = (byte)(17);
      var10000[14] = (byte)(11);
      var10000[15] = (byte)(24);
      var10000[16] = (byte)(7);
      var10000[17] = (byte)(38);
      var10000[18] = (byte)(-88);
      if ($$v_s(var10000, 2834845946103451648L).equals(dimension)) {
         return OVERWORLD_Y_LEVELS;
      } else {
         var10000 = new byte[20];
         var10000[0] = (byte)(72);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-69);
         var10000[4] = (byte)(-124);
         var10000[5] = (byte)(-55);
         var10000[6] = (byte)(53);
         var10000[7] = (byte)(105);
         var10000[8] = (byte)(57);
         var10000[9] = (byte)(-104);
         var10000[10] = (byte)(-112);
         var10000[11] = (byte)(-98);
         var10000[12] = (byte)(-38);
         var10000[13] = (byte)(60);
         var10000[14] = (byte)(18);
         var10000[15] = (byte)(18);
         var10000[16] = 1;
         var10000[17] = (byte)(34);
         var10000[18] = (byte)(-87);
         var10000[19] = (byte)(-36);
         if ($$v_s(var10000, 2834845946103451648L).equals(dimension)) {
            int[] var1 = new int[NETHER_FLOOR_Y_LEVELS.length + NETHER_ROOF_Y_LEVELS.length];
            System.arraycopy(NETHER_FLOOR_Y_LEVELS, 0, var1, 0, NETHER_FLOOR_Y_LEVELS.length);
            System.arraycopy(NETHER_ROOF_Y_LEVELS, 0, var1, NETHER_FLOOR_Y_LEVELS.length, NETHER_ROOF_Y_LEVELS.length);
            return var1;
         } else {
            return new int[0];
         }
      }
   }

   private static boolean isBedrock(class_2680 state) {
      return state != null && state.method_27852(class_2246.field_9987);
   }

   static int applyOpacity(int argb, int opacity) {
      int var2 = Math.max(0, Math.min(255, opacity));
      int var3 = argb >>> (24) & (255);
      int var4 = (var3 * var2 + (127)) / (255);
      return var4 << (24) | argb & (16777215);
   }

   static boolean isWithinRenderDistance(int chunkX, int chunkZ, int centerX, int centerZ, int distance) {
      int var5 = Math.max(1, distance);
      long var6 = (long)chunkX - centerX;
      long var8 = (long)chunkZ - centerZ;
      return Math.abs(var6) <= var5 && Math.abs(var8) <= var5;
   }

   private static int packCell(int lx, int lz) {
      return lx << (4) | lz & (15);
   }

   private static int unpackLocalX(int cell) {
      return cell >> (4) & (15);
   }

   private static int unpackLocalZ(int cell) {
      return cell & (15);
   }

   private static int packCode(int layer, int cell) {
      return layer << (8) | cell & (255);
   }

   private static int unpackLayer(int code) {
      return code >>> (8);
   }

   private static int unpackCell(int code) {
      return code & (255);
   }

   static {
      int[] var10000 = new int[5];
      var10000[0] = -64;
      var10000[1] = -63;
      var10000[2] = -62;
      var10000[3] = -61;
      var10000[4] = -60;
      OVERWORLD_Y_LEVELS = var10000;
      var10000 = new int[5];
      var10000[0] = 0;
      var10000[1] = 1;
      var10000[2] = 2;
      var10000[3] = 3;
      var10000[4] = 4;
      NETHER_FLOOR_Y_LEVELS = var10000;
      var10000 = new int[5];
      var10000[0] = 123;
      var10000[1] = 124;
      var10000[2] = 125;
      var10000[3] = 126;
      var10000[4] = 127;
      NETHER_ROOF_Y_LEVELS = var10000;
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

   private record ChunkSnapshot(int startX, int startZ, int[] yLevels, boolean[][] bedrock) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record ScanResult(Set<class_2338> blocks, List<BedrockHoleESP.VoidGroup> groups) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record VoidGroup(long anchor, class_2338 firstPos, int size) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
