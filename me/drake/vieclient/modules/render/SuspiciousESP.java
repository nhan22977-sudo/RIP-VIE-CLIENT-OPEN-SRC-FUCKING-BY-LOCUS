package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.EntityCache;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.util.render.EspFade;
import net.minecraft.class_1297;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2666;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_3989;
import net.minecraft.class_2338.class_2339;

public class SuspiciousESP extends Module {
   private static final int COLOR_ACTIVE_PISTON = -16711936;
   private static final int COLOR_WANDERING = -16711681;
   private static final int COLOR_ACTIVE_BLOCK = -256;
   private static final long SCAN_INTERVAL_MS = 1000L;
   private static final int MIN_RENDER_CHUNK_RADIUS = 2;
   private static final ExecutorService SCAN_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
      byte[] var10003 = new byte[25];
      var10003[0] = (byte)(67);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-67);
      var10003[4] = (byte)(-10);
      var10003[5] = (byte)(2);
      var10003[6] = (byte)(-80);
      var10003[7] = (byte)(-3);
      var10003[8] = (byte)(41);
      var10003[9] = (byte)(-113);
      var10003[10] = (byte)(-105);
      var10003[11] = (byte)(-125);
      var10003[12] = (byte)(-79);
      var10003[13] = (byte)(-61);
      var10003[14] = (byte)(-108);
      var10003[15] = (byte)(-120);
      var10003[16] = (byte)(12);
      var10003[17] = (byte)(37);
      var10003[18] = (byte)(-71);
      var10003[19] = (byte)(-35);
      var10003[20] = (byte)(7);
      var10003[21] = (byte)(-24);
      var10003[22] = (byte)(-26);
      var10003[23] = (byte)(-94);
      var10003[24] = (byte)(-29);
      Thread var1 = new Thread(r, $$v_s(var10003, -4911539180423360496L));
      var1.setDaemon(true);
      return var1;
   });
   private final BoolSetting tracers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tracers",
         "Draw tracers to suspicious targets",
         true
      )
   );
   private final BoolSetting flagWandering = (BoolSetting)this.addSetting(
      new BoolSetting(
         "FlagWandering",
         "Flag wandering trader",
         true
      )
   );
   private final BoolSetting flagPistons = (BoolSetting)this.addSetting(
      new BoolSetting(
         "FlagPistons",
         "Flag active piston/sticky piston",
         true
      )
   );
   private final BoolSetting flagActiveBlocks = (BoolSetting)this.addSetting(
      new BoolSetting(
         "FlagActiveBlocks",
         "Flag active observer/dispenser/dropper/lamp",
         true
      )
   );
   private final IntSetting alpha = (IntSetting)this.addSetting(
      new IntSetting(
         "Alpha",
         "Overlay opacity",
         100,
         0,
         255
      )
   );
   private final ConcurrentHashMap markerMap = new ConcurrentHashMap();
   private volatile List blockMarkers = Collections.emptyList();
   private final AtomicBoolean scanRunning = new AtomicBoolean(false);
   private long lastScanMs;
   private final EspFade espFade = new EspFade(this::finishDisable);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SuspiciousESP() {
      super(
         "SuspiciousESP",
         "Flags suspicious base activity",
         Category.RENDER
      );
   }

   @Override
   public boolean usesEntityCache() {
      return (Boolean)this.flagWandering.get();
   }

   private void finishDisable() {
      this.markerMap.clear();
      this.blockMarkers = Collections.emptyList();
      this.scanRunning.set(false);
      super.onDisable();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.markerMap.clear();
      this.blockMarkers = Collections.emptyList();
      this.scanRunning.set(false);
      this.lastScanMs = 0L;
      this.espFade.onEnable();
   }

   @Override
   public void onDisable() {
      this.espFade.onDisable();
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE && this.isInGame()) {
         this.espFade.update();
         if (this.isEnabled()) {
            if (!(Boolean)this.flagPistons.get() && !(Boolean)this.flagActiveBlocks.get()) {
               if (!this.markerMap.isEmpty()) {
                  this.markerMap.clear();
                  this.blockMarkers = Collections.emptyList();
               }
            } else {
               long var2 = System.currentTimeMillis();
               if (var2 - this.lastScanMs >= 1000L) {
                  this.lastScanMs = var2;
                  this.scheduleBlockScan();
               }
            }
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent e) {
      if (e.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame()) {
         float var2 = this.espFade.getFade();
         if (!(var2 <= 0.001F)) {
            int var3 = (int)(clamp((Integer)this.alpha.get(), 0, 255) * var2);
            int var4 = (int)(255.0F * var2);
            boolean var5 = (Boolean)this.tracers.get() && var4 > 0;
            if (var3 > 0 || var5) {
               float var6 = e.getTickDelta();
               if ((Boolean)this.flagWandering.get()) {
                  int var7 = withAlpha(-16711681, var4);
                  int var8 = withAlpha(-16711681, var3);
                  if (EntityCache.isActive()) {
                     for (EntityCache.CachedEntity var10 : EntityCache.getEntities()) {
                        if (isWanderingTrader(var10.entity())) {
                           class_238 var11 = RenderUtil.getInterpolatedBox(var10.entity(), var6);
                           RenderUtil.drawFilledBox(var11, var8);
                           if (var5) {
                              RenderUtil.drawTracer(var10.entity(), var7, var6);
                           }
                        }
                     }
                  } else {
                     for (class_1297 var16 : mc.field_1687.method_18112()) {
                        if (isWanderingTrader(var16)) {
                           class_238 var18 = RenderUtil.getInterpolatedBox(var16, var6);
                           RenderUtil.drawFilledBox(var18, var8);
                           if (var5) {
                              RenderUtil.drawTracer(var16, var7, var6);
                           }
                        }
                     }
                  }
               }

               List var12 = this.blockMarkers;
               if (!var12.isEmpty()) {
                  for (SuspiciousESP.BlockMarker var15 : var12) {
                     RenderUtil.drawFilledBox(var15.coreBox, withAlpha(var15.color, var3));
                     if (var5) {
                        class_2338 var17 = var15.pos;
                        int var19 = withAlpha(var15.color, var4);
                        RenderUtil.drawTracerToPos(var17.method_10263() + 0.5, var17.method_10264() + 0.5, var17.method_10260() + 0.5, var19, var6);
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onReceivePacket(ReceivePacketEvent event) {
      if (this.isEnabled() && mc != null) {
         if (event.getPacket() instanceof class_2626 var5) {
            mc.execute(() -> {
               if (this.isEnabled() && this.isInGame()) {
                  boolean var2x = this.applyStateUpdate(var5.method_11309(), var5.method_11308());
                  if (var2x) {
                     this.publishSnapshot();
                  }
               }
            });
         } else if (event.getPacket() instanceof class_2637 var4) {
            mc.execute(() -> {
               if (this.isEnabled() && this.isInGame()) {
                  boolean[] var2x = new boolean[]{false};
                  var4.method_30621((pos, state) -> {
                     if (this.applyStateUpdate(pos, state)) {
                        var2x[0] = true;
                     }
                  });
                  if (var2x[0]) {
                     this.publishSnapshot();
                  }
               }
            });
         } else {
            if (event.getPacket() instanceof class_2666 var2) {
               mc.execute(() -> {
                  if (this.isEnabled()) {
                     boolean var2x = this.removeChunkMarkers(var2.comp_1726());
                     if (var2x) {
                        this.publishSnapshot();
                     }
                  }
               });
            }
         }
      }
   }

   private void scheduleBlockScan() {
      if (this.scanRunning.compareAndSet(false, true)) {
         SCAN_EXECUTOR.execute(() -> {
            try {
               if (mc.field_1687 != null && mc.field_1724 != null) {
                  Map var1 = this.scanSuspiciousBlocksSnapshot();
                  this.markerMap.clear();
                  this.markerMap.putAll(var1);
                  this.publishSnapshot();
                  return;
               }

               this.markerMap.clear();
               this.blockMarkers = Collections.emptyList();
            } finally {
               this.scanRunning.set(false);
            }
         });
      }
   }

   private Map scanSuspiciousBlocksSnapshot() {
      HashMap var1 = new HashMap();
      int var2 = this.resolveAutoRangeBlocks();
      int var3 = var2 * var2;
      class_2338 var4 = mc.field_1724.method_24515();
      class_1923 var5 = mc.field_1724.method_31476();
      int var6 = Math.max(1, (var2 + 15) / 16);
      int var7 = mc.field_1687.method_31607();
      int var8 = mc.field_1687.method_31600();
      boolean var9 = (Boolean)this.flagPistons.get();
      boolean var10 = (Boolean)this.flagActiveBlocks.get();
      class_2339 var11 = new class_2339();

      for (int var12 = var5.field_9181 - var6; var12 <= var5.field_9181 + var6; var12++) {
         for (int var13 = var5.field_9180 - var6; var13 <= var5.field_9180 + var6; var13++) {
            class_2818 var14 = mc.field_1687.method_2935().method_21730(var12, var13);
            if (var14 != null) {
               class_2826[] var15 = var14.method_12006();
               int var16 = var14.method_12004().method_8326();
               int var17 = var14.method_12004().method_8328();
               int var18 = var14.method_31607();

               for (int var19 = 0; var19 < var15.length; var19++) {
                  class_2826 var20 = var15[var19];
                  if (var20 != null && !var20.method_38292()) {
                     int var21 = var18 + var19 * 16;
                     int var22 = var21 + 15;
                     if (var22 >= var7 && var21 <= var8) {
                        for (int var23 = 0; var23 < 16; var23++) {
                           int var24 = var16 + var23;
                           int var25 = var24 - var4.method_10263();
                           int var26 = var25 * var25;
                           if (var26 <= var3) {
                              for (int var27 = 0; var27 < 16; var27++) {
                                 int var28 = var17 + var27;
                                 int var29 = var28 - var4.method_10260();
                                 if (var26 + var29 * var29 <= var3) {
                                    for (int var30 = 0; var30 < 16; var30++) {
                                       int var31 = var21 + var30;
                                       if (var31 >= var7 && var31 <= var8) {
                                          class_2680 var32 = var20.method_12254(var23, var30, var27);
                                          if (!var32.method_26215()) {
                                             int var33 = resolveMarkerColor(var32, var9, var10);
                                             if (var33 != 0) {
                                                var11.method_10103(var24, var31, var28);
                                                class_2338 var34 = var11.method_10062();
                                                var1.put(var34.method_10063(), new SuspiciousESP.BlockMarker(var34, var33, createCoreBox(var34)));
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return var1;
   }

   private boolean applyStateUpdate(class_2338 pos, class_2680 state) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         int var3 = this.resolveAutoRangeBlocks();
         int var4 = pos.method_10263() - mc.field_1724.method_31477();
         int var5 = pos.method_10260() - mc.field_1724.method_31479();
         if (var4 * var4 + var5 * var5 > var3 * var3) {
            return this.markerMap.remove(pos.method_10063()) != null;
         }

         int var6 = resolveMarkerColor(state, (Boolean)this.flagPistons.get(), (Boolean)this.flagActiveBlocks.get());
         long var7 = pos.method_10063();
         if (var6 == 0) {
            return this.markerMap.remove(var7) != null;
         }

         SuspiciousESP.BlockMarker var9 = new SuspiciousESP.BlockMarker(pos.method_10062(), var6, createCoreBox(pos));
         SuspiciousESP.BlockMarker var10 = this.markerMap.put(var7, var9);
         return var10 == null || var10.color != var9.color;
      } else {
         return false;
      }
   }

   private boolean removeChunkMarkers(class_1923 chunkPos) {
      int var2 = chunkPos.field_9181;
      int var3 = chunkPos.field_9180;
      return this.markerMap
         .keySet()
         .removeIf(key -> class_2338.method_10061(key) >> (4) == var2 && class_2338.method_10083(key) >> (4) == var3);
   }

   private void publishSnapshot() {
      this.blockMarkers = this.markerMap.isEmpty() ? Collections.emptyList() : List.copyOf(this.markerMap.values());
   }

   private int resolveAutoRangeBlocks() {
      if (mc != null && mc.field_1690 != null) {
         int var1 = Math.max(2, mc.field_1690.method_38521());
         return var1 * (16);
      } else {
         return 64;
      }
   }

   private static int resolveMarkerColor(class_2680 state, boolean checkPiston, boolean checkActiveBlocks) {
      if (checkPiston && isActivePiston(state)) {
         return -16711936;
      } else {
         return checkActiveBlocks && isActiveAutomationBlock(state) ? -256 : 0;
      }
   }

   private static boolean isActivePiston(class_2680 state) {
      return !state.method_27852(class_2246.field_10560) && !state.method_27852(class_2246.field_10615)
         ? false
         : state.method_28498(class_2741.field_12552) && (Boolean)state.method_11654(class_2741.field_12552);
   }

   private static boolean isActiveAutomationBlock(class_2680 state) {
      if (state.method_27852(class_2246.field_10282)) {
         return state.method_28498(class_2741.field_12484) && (Boolean)state.method_11654(class_2741.field_12484);
      } else if (!state.method_27852(class_2246.field_10200) && !state.method_27852(class_2246.field_10228)) {
         return !state.method_27852(class_2246.field_10524)
            ? false
            : state.method_28498(class_2741.field_12548) && (Boolean)state.method_11654(class_2741.field_12548);
      } else {
         return state.method_28498(class_2741.field_12522) && (Boolean)state.method_11654(class_2741.field_12522);
      }
   }

   private static boolean isWanderingTrader(class_1297 entity) {
      if (entity instanceof class_3989) {
         return true;
      }

      String var10000 = entity.getClass().getSimpleName();
      byte[] var10001 = new byte[15];
      var10001[0] = (byte)(98);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-70);
      var10001[4] = (byte)(-1);
      var10001[5] = (byte)(25);
      var10001[6] = (byte)(-68);
      var10001[7] = (byte)(-3);
      var10001[8] = (byte)(58);
      var10001[9] = (byte)(-10);
      var10001[10] = (byte)(-106);
      var10001[11] = (byte)(-105);
      var10001[12] = (byte)(-90);
      var10001[13] = (byte)(-42);
      var10001[14] = (byte)(-113);
      return var10000.contains($$v_s(var10001, -4911539180423360496L));
   }

   private static int withAlpha(int rgbColor, int alpha) {
      return clamp(alphax, 0, 255) << (24) | rgbColor & (16777215);
   }

   private static int clamp(int value, int min, int max) {
      return Math.max(min, Math.min(max, value));
   }

   public static class_238 createCoreBox(class_2338 bp) {
      return new class_238(
         bp.method_10263() + 0.1, bp.method_10264() + 0.05, bp.method_10260() + 0.1, bp.method_10263() + 0.9, bp.method_10264() + 0.85, bp.method_10260() + 0.9
      );
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

   record BlockMarker(class_2338 pos, int color, class_238 coreBox) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
