package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.ServerBlockUpdateEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.ChunkScanMemo;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.util.SpawnerToast;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2818;
import net.minecraft.class_2826;

public class SpawnersNotifier extends Module {
   private final BoolSetting notifySetting = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Notify",
         "Show achievement toast when spawners are found",
         true
      )
   );
   private final BoolSetting showBox = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Box",
         "Render box around spawners",
         true
      )
   );
   private final BoolSetting showTracers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tracers",
         "Draw tracer lines to spawners",
         false
      )
   );
   private final BoolSetting showColumn = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Column",
         "Draw dual beacon columns from spawner to sky",
         true
      )
   );
   private final ColorSetting boxColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "BoxColor",
         "Spawner box color",
         -6946816
      )
   );
   private final ColorSetting columnColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "ColumnColor",
         "Column color (top half)",
         -16711936
      )
   );
   private final IntSetting columnAlpha = (IntSetting)this.addSetting(
      new IntSetting(
         "ColumnAlpha",
         "Column opacity",
         80,
         5,
         255
      )
   );
   private final DoubleSetting columnWidth = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "CoreWidth",
         "Primary core column half-width",
         0.25,
         0.05,
         1.0,
         0.05
      )
   );
   private final DoubleSetting beamWidth = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "BeamWidth",
         "Secondary beam column half-width",
         0.75,
         0.2,
         2.5,
         0.05
      )
   );
   private final IntSetting beamAlpha = (IntSetting)this.addSetting(
      new IntSetting(
         "BeamAlpha",
         "Secondary beam opacity",
         25,
         1,
         100
      )
   );
   private final IntSetting renderDist = (IntSetting)this.addSetting(
      new IntSetting(
         "RenderDist",
         "Render distance (chunks)",
         32,
         2,
         32
      )
   );
   private final IntSetting maxRender = (IntSetting)this.addSetting(
      new IntSetting(
         "MaxRender",
         "Max rendered per frame",
         2048,
         16,
         2048
      )
   );
   private final Map spawnersByChunk = new ConcurrentHashMap();
   private final Set notifiedChunks = ConcurrentHashMap.newKeySet();
   private volatile boolean initialScanComplete = false;
   private volatile int totalSpawners = 0;
   private int incrementalScanCursor = 0;
   private final ChunkScanMemo scanMemo = new ChunkScanMemo();
   private final Set dirtyChunks = new LinkedHashSet();
   private class_1937 observedWorld;
   private int cleanupX = Integer.MIN_VALUE;
   private int cleanupZ = Integer.MIN_VALUE;
   private int cleanupRadius = -1;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SpawnersNotifier() {
      super(
         "SpawnersNotifier",
         "Detect and render mob spawners",
         Category.SMP_UTILITIES
      );
      this.renderDist.setVisibleInGui(false);
      this.maxRender.setVisibleInGui(false);
      this.boxColor.visibleWhen(this.showBox::get);
      this.columnColor.visibleWhen(this.showColumn::get);
      this.columnAlpha.visibleWhen(this.showColumn::get);
      this.columnWidth.visibleWhen(this.showColumn::get);
      this.beamWidth.visibleWhen(this.showColumn::get);
      this.beamAlpha.visibleWhen(this.showColumn::get);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.spawnersByChunk.clear();
      this.totalSpawners = 0;
      this.incrementalScanCursor = 0;
      this.notifiedChunks.clear();
      this.initialScanComplete = false;
      this.scanMemo.clear();
      this.dirtyChunks.clear();
      this.observedWorld = mc.field_1687;
      this.cleanupRadius = -1;
      if (this.isInGame()) {
         this.scanNearbyChunksFull();
      }

      this.initialScanComplete = true;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.spawnersByChunk.clear();
      this.totalSpawners = 0;
      this.incrementalScanCursor = 0;
      this.scanMemo.clear();
      this.dirtyChunks.clear();
      this.notifiedChunks.clear();
      this.observedWorld = null;
   }

   @Override
   public String getInfo() {
      return this.totalSpawners + " spawners";
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent e) {
      if (this.isInGame()) {
         if (e.isLoaded()) {
            this.scanChunk(e.getChunkPos(), true);
         } else {
            long var2 = pack(e.getChunkPos());
            this.removeChunk(var2);
            this.scanMemo.invalidate(var2);
            this.dirtyChunks.remove(var2);
         }
      }
   }

   @EventHandler
   public void onBlockUpdate(ServerBlockUpdateEvent event) {
      if (this.isInGame()) {
         long var2 = class_1923.method_8331(event.getPos().method_10263() >> (4), event.getPos().method_10260() >> (4));
         if (event.getState().method_27852(class_2246.field_10260) || this.spawnersByChunk.containsKey(var2)) {
            this.scanMemo.invalidate(var2);
            this.dirtyChunks.add(var2);
         }
      }
   }

   @EventHandler
   public void onTick(TickEvent e) {
      if (e.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1687 != this.observedWorld) {
            this.observedWorld = mc.field_1687;
            this.spawnersByChunk.clear();
            this.notifiedChunks.clear();
            this.scanMemo.clear();
            this.dirtyChunks.clear();
            this.totalSpawners = 0;
            this.incrementalScanCursor = 0;
            this.cleanupRadius = -1;
            this.initialScanComplete = false;
            if (this.isInGame()) {
               this.scanNearbyChunksFull();
            }

            this.initialScanComplete = true;
         }

         if (e.getPhase() == TickEvent.Phase.PRE && this.isInGame()) {
            Iterator var2 = this.dirtyChunks.iterator();

            for (int var3 = '頋' ^ '𢡊'; var3 > 0 && var2.hasNext(); var3--) {
               long var4 = (Long)var2.next();
               var2.remove();
               this.scanChunk(unpack(var4), true);
            }

            this.scanNearbyIncremental();
            this.cleanupOutOfRangeChunks();
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent e) {
      if (e.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame() && !this.spawnersByChunk.isEmpty()) {
         int var2 = (Integer)this.boxColor.get();
         int var3 = (Integer)this.columnColor.get();
         int var4 = (Integer)this.columnAlpha.get();
         float var5 = e.getTickDelta();
         int var6 = (Integer)this.maxRender.get();
         int var7 = (Integer)this.renderDist.get();
         int var8 = mc.field_1724.method_31476().field_9181;
         int var9 = mc.field_1724.method_31476().field_9180;
         double var10 = mc.field_1724.method_23317();
         double var12 = mc.field_1724.method_23318();
         double var14 = mc.field_1724.method_23321();
         double var16 = (var7 + 1) * 16.0;
         var16 *= var16;
         int var18 = 0;

         for (Entry var20 : this.spawnersByChunk.entrySet()) {
            class_1923 var21 = unpack((Long)var20.getKey());
            if (Math.abs(var21.field_9181 - var8) <= var7 + 1 && Math.abs(var21.field_9180 - var9) <= var7 + 1) {
               List var22 = (List)var20.getValue();
               if (var22 != null && !var22.isEmpty()) {
                  for (class_2338 var24 : var22) {
                     if (var18 >= var6) {
                        return;
                     }

                     var18++;
                     double var25 = var24.method_10263() + 0.5;
                     double var27 = var24.method_10264() + 0.5;
                     double var29 = var24.method_10260() + 0.5;
                     double var31 = (var25 - var10) * (var25 - var10) + (var27 - var12) * (var27 - var12) + (var29 - var14) * (var29 - var14);
                     if (!(var31 > var16)) {
                        if ((Boolean)this.showBox.get()) {
                           class_238 var33 = new class_238(var24);
                           RenderUtil.drawBox(var33, var2);
                           RenderUtil.drawFilledBox(var33, var2 & 16777215 | 1342177280);
                        }

                        if ((Boolean)this.showTracers.get()) {
                           RenderUtil.drawTracerToPos(var25, var27, var29, var2, var5);
                        }

                        if ((Boolean)this.showColumn.get()) {
                           int var47 = mc.field_1687.method_31600();
                           double var34 = var24.method_10263() + 0.5;
                           double var36 = var24.method_10260() + 0.5;
                           double var38 = var24.method_10264() + 1.0;
                           double var40 = (Double)this.columnWidth.get();
                           double var42 = (Double)this.beamWidth.get();
                           int var44 = (Integer)this.beamAlpha.get();
                           if (var42 > 0.0 && var44 > 0) {
                              class_238 var45 = new class_238(var34 - var42, var38, var36 - var42, var34 + var42, var47, var36 + var42);
                              RenderUtil.drawFilledBoxAdditive(var45, var3 & 16777215 | var44 << 24);
                           }

                           if (var40 > 0.0) {
                              class_238 var48 = new class_238(var34 - var40, var38, var36 - var40, var34 + var40, var47, var36 + var40);
                              RenderUtil.drawFilledBox(var48, var3 & 16777215 | var4 << 24);
                              RenderUtil.drawBox(var48, var3);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void scanNearbyIncremental() {
      int var1 = (Integer)this.renderDist.get();
      if (var1 > 1) {
         int var2 = 4;
         int var3 = mc.field_1724.method_31476().field_9181;
         int var4 = mc.field_1724.method_31476().field_9180;
         int var5 = var1 * (2) + 1;
         int var6 = var5 * var5;
         if (var6 > 0) {
            if (this.incrementalScanCursor >= var6) {
               this.incrementalScanCursor = 0;
            }

            int var7 = 0;

            for (int var8 = 0; var8 < var6 && var7 < var2; var7++) {
               int var9 = (this.incrementalScanCursor + var8) % var6;
               int var10 = var9 % var5 - var1;
               int var11 = var9 / var5 - var1;
               var8++;
               int var12 = var3 + var10;
               int var13 = var4 + var11;
               this.scanChunk(new class_1923(var12, var13), false);
            }

            this.incrementalScanCursor = (this.incrementalScanCursor + var7) % var6;
         }
      }
   }

   private void scanNearbyChunksFull() {
      int var1 = (Integer)this.renderDist.get();
      int var2 = mc.field_1724.method_31476().field_9181;
      int var3 = mc.field_1724.method_31476().field_9180;

      for (int var4 = -var1; var4 <= var1; var4++) {
         for (int var5 = -var1; var5 <= var1; var5++) {
            this.scanChunk(new class_1923(var2 + var4, var3 + var5), true);
         }
      }
   }

   private boolean scanChunk(class_1923 cpos, boolean force) {
      long var3 = pack(cpos);
      class_2818 var5 = mc.field_1687.method_2935().method_12126(cpos.field_9181, cpos.field_9180, false);
      if (var5 == null) {
         this.scanMemo.invalidate(var3);
         this.removeChunk(var3);
         return false;
      }

      if (!force && !this.scanMemo.needsScan(var3, var5)) {
         return false;
      }

      List var6 = this.findSpawners(var5);
      this.scanMemo.markScanned(var3, var5);
      List var7 = (List)this.spawnersByChunk.get(var3);
      boolean var8 = var7 == null || !var7.equals(var6);
      if (!var6.isEmpty()) {
         this.spawnersByChunk.put(var3, var6);
      } else if (var8) {
         this.spawnersByChunk.remove(var3);
      }

      if (var8) {
         this.totalSpawners = this.totalSpawners + (var6.size() - (var7 == null ? 0 : var7.size()));
      }

      if (var8 && this.initialScanComplete && !var6.isEmpty() && (Boolean)this.notifySetting.get() && this.notifiedChunks.add(var3)) {
         class_2338 var9 = (class_2338)var6.get(0);
         mc.method_1566().method_1999(new SpawnerToast(var9));
      }

      return var8;
   }

   private List findSpawners(class_2818 chunk) {
      ArrayList var2 = new ArrayList();
      class_1923 var3 = chunk.method_12004();
      class_2826[] var4 = chunk.method_12006();
      int var5 = mc.field_1687.method_31607();

      for (int var6 = 0; var6 < var4.length; var6++) {
         class_2826 var7 = var4[var6];
         if (var7 != null && !var7.method_38292() && var7.method_19523(state -> state.method_27852(class_2246.field_10260))) {
            int var8 = var5 + (var6 << (4));

            for (int var9 = 0; var9 < (16); var9++) {
               for (int var10 = 0; var10 < (16); var10++) {
                  for (int var11 = 0; var11 < (16); var11++) {
                     if (var7.method_12254(var9, var10, var11).method_27852(class_2246.field_10260)) {
                        var2.add(new class_2338(var3.method_8326() + var9, var8 + var10, var3.method_8328() + var11));
                     }
                  }
               }
            }
         }
      }

      return var2;
   }

   private void cleanupOutOfRangeChunks() {
      int var1 = (Integer)this.renderDist.get() + 1;
      int var2 = mc.field_1724.method_31476().field_9181;
      int var3 = mc.field_1724.method_31476().field_9180;
      if (var2 != this.cleanupX || var3 != this.cleanupZ || var1 != this.cleanupRadius) {
         this.cleanupX = var2;
         this.cleanupZ = var3;
         this.cleanupRadius = var1;
         this.scanMemo.retainWithin(var2, var3, var1);
         boolean[] var4 = new boolean[]{false};
         this.spawnersByChunk.keySet().removeIf(key -> {
            class_1923 var5 = unpack(key);
            boolean var6 = Math.abs(var5.field_9181 - var2) > var1 || Math.abs(var5.field_9180 - var3) > var1;
            if (var6) {
               var4[0] = true;
            }

            return var6;
         });
         if (var4[0]) {
            this.updateTotalCount();
         }
      }
   }

   private void removeChunk(long key) {
      List var3 = (List)this.spawnersByChunk.remove(key);
      if (var3 != null) {
         this.totalSpawners = this.totalSpawners - var3.size();
      }
   }

   private void updateTotalCount() {
      int var1 = 0;

      for (List var3 : this.spawnersByChunk.values()) {
         if (var3 != null) {
            var1 += var3.size();
         }
      }

      this.totalSpawners = var1;
   }

   private static long pack(class_1923 pos) {
      return class_1923.method_8331(pos.field_9181, pos.field_9180);
   }

   private static class_1923 unpack(long key) {
      return new class_1923(class_1923.method_8325(key), class_1923.method_8332(key));
   }

   public double getColumnWidth() {
      return (Double)this.columnWidth.get();
   }

   public double getBeamWidth() {
      return (Double)this.beamWidth.get();
   }

   public int getBeamAlpha() {
      return (Integer)this.beamAlpha.get();
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
}
