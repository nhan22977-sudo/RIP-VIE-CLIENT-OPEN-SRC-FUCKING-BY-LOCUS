package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ButtonSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.gui.BlockNotifierFilterScreen;
import me.drake.vieclient.gui.nvg.adapter.setting.BlocksSetting;
import me.drake.vieclient.gui.nvg.ui.components.ItemBlockPickerScreen;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.util.render.ToastManager;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2960;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_638;
import net.minecraft.class_7923;

public class BlockNotifier extends Module {
   private static final int MAX_CAPTURE_PER_TICK = 16;
   private static final int RESCAN_TICKS = 10;
   private final ButtonSetting openFilter = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Filter",
         "Open block type filter",
         this::openFilterScreen
      )
   );
   private final EnumSetting theme = (EnumSetting)this.addSetting(
      (EnumSetting)hidden(
         new EnumSetting(
               "Palette",
               "Color palette theme for blocks (Warm / Cold)",
               BlockNotifier.ColorTheme.WARM
            )
            .withDisplayName(BlockNotifier.ColorTheme.WARM, "Warm")
            .withDisplayName(BlockNotifier.ColorTheme.COLD, "Cold")
      )
   );
   private final BoolSetting toastNotify = (BoolSetting)this.addSetting(
      (BoolSetting)hidden(
         new BoolSetting(
            "Toast",
            "Show achievement toast notification",
            true
         )
      )
   );
   private final BoolSetting soundNotify = (BoolSetting)this.addSetting(
      (BoolSetting)hidden(
         new BoolSetting(
            "Sound",
            "Play alert sound when blocks are found",
            true
         )
      )
   );
   private final BoolSetting cordNotify = (BoolSetting)this.addSetting(
      (BoolSetting)hidden(
         new BoolSetting(
            "Cord Notification",
            "Show notification on compass cord",
            true
         )
      )
   );
   private final DoubleSetting range = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "Range",
            "Detection range (blocks)",
            512.0,
            16.0,
            512.0
         )
      )
   );
   private final IntSetting repeatCooldown = (IntSetting)this.addSetting(
      new IntSetting(
         "Cooldown",
         "Seconds before notifying same block position again (0 = never repeat)",
         10,
         0,
         300
      )
   );
   private final BoolSetting showTracers = (BoolSetting)this.addSetting(
      (BoolSetting)hidden(
         new BoolSetting(
            "Tracers",
            "Draw temporary tracers to notified blocks",
            false
         )
      )
   );
   private final BoolSetting showBox = (BoolSetting)this.addSetting(
      (BoolSetting)hidden(
         new BoolSetting(
            "Box",
            "Draw temporary bounding box around notified blocks",
            false
         )
      )
   );
   private final IntSetting tracerDuration = (IntSetting)this.addSetting(
      (IntSetting)hidden(
         new IntSetting(
            "Duration",
            "Seconds temporary visuals remain visible",
            10,
            1,
            60
         )
      )
   );
   private BlockNotifier.ColorTheme cachedTheme = null;
   private final List blockOptions = new ArrayList();
   private final List blockOptionsView = Collections.unmodifiableList(this.blockOptions);
   private final Map configById = new HashMap();
   private final Map configByBlock = new HashMap();
   private final Map candidateBlocksByChunk = new ConcurrentHashMap();
   private final Map notifiedBlocks = new ConcurrentHashMap();
   private final Map activeVisuals = new ConcurrentHashMap();
   private final ConcurrentLinkedQueue scanQueue = new ConcurrentLinkedQueue();
   private final Set queuedChunks = ConcurrentHashMap.newKeySet();
   private final ExecutorService scanner = Executors.newSingleThreadExecutor(r -> {
      byte[] var10003 = new byte[29];
      var10003[0] = (byte)(112);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-67);
      var10003[4] = (byte)(100);
      var10003[5] = (byte)(-106);
      var10003[6] = (byte)(65);
      var10003[7] = (byte)(-17);
      var10003[8] = (byte)(26);
      var10003[9] = (byte)(-113);
      var10003[10] = (byte)(-122);
      var10003[11] = (byte)(-102);
      var10003[12] = (byte)(63);
      var10003[13] = (byte)(68);
      var10003[14] = (byte)(103);
      var10003[15] = (byte)(-44);
      var10003[16] = (byte)(56);
      var10003[17] = (byte)(37);
      var10003[18] = (byte)(-72);
      var10003[19] = (byte)(-57);
      var10003[20] = (byte)(-34);
      var10003[21] = (byte)(102);
      var10003[22] = (byte)(17);
      var10003[23] = (byte)(-93);
      var10003[24] = (byte)(-109);
      var10003[25] = (byte)(97);
      var10003[26] = (byte)(-9);
      var10003[27] = (byte)(39);
      var10003[28] = (byte)(-18);
      Thread var1 = new Thread(r, $$v_s(var10003, -6257996111865982941L));
      var1.setDaemon(true);
      return var1;
   });
   private boolean scanRunning;
   private long scanGeneration;
   private class_1937 observedWorld;
   private int enabledBlockCount = 0;
   private int totalNotifiedCount = 0;
   private int rescanTimer;
   private int lastPlayerChunkX = Integer.MIN_VALUE;
   private int lastPlayerChunkZ = Integer.MIN_VALUE;
   private boolean dirtyFilter = true;
   private double lastRange = -1.0;
   private final BlocksSetting blocksFilter = new BlocksSetting("Blocks");
   private boolean pendingPickerSync;
   public static final Set DEFAULT_NOTIFIED_PATHS = Set.of(
      "spawner",
      "trial_spawner",
      "vault",
      "diamond_ore",
      "deepslate_diamond_ore",
      "ancient_debris",
      "emerald_ore",
      "deepslate_emerald_ore",
      "ender_chest",
      "nether_portal"
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BlockNotifier() {
      super(
         "Block Notifier",
         "Notifies when selected block types are found nearby",
         Category.RENDER
      );
      this.tracerDuration.visibleWhen(() -> (Boolean)this.showTracers.get() || (Boolean)this.showBox.get());
      this.buildBlockFilters();
   }

   private static Setting hidden(Setting setting) {
      setting.setVisibleInGui(false);
      return setting;
   }

   public List getBlockOptions() {
      return this.blockOptionsView;
   }

   public int getTotalBlockTypeCount() {
      return this.blockOptions.size();
   }

   public int getEnabledBlockTypeCount() {
      return this.enabledBlockCount;
   }

   public int getTotalNotifiedCount() {
      return this.totalNotifiedCount;
   }

   public boolean isBlockEnabled(class_2960 id) {
      BlockNotifier.BlockConfig var2 = (BlockNotifier.BlockConfig)this.configById.get(id);
      return var2 != null && (Boolean)var2.enabled.get();
   }

   public void setBlockEnabled(class_2960 id, boolean enabled) {
      BlockNotifier.BlockConfig var3 = (BlockNotifier.BlockConfig)this.configById.get(id);
      if (var3 != null && (Boolean)var3.enabled.get() != enabled) {
         var3.enabled.set(enabled);
         this.recountEnabledBlocks();
         this.dirtyFilter = true;
      }
   }

   public int getBlockColor(class_2960 id) {
      BlockNotifier.BlockConfig var2 = (BlockNotifier.BlockConfig)this.configById.get(id);
      return var2 == null ? -1 : (Integer)var2.color.get();
   }

   public void setBlockColor(class_2960 id, int rgb) {
      BlockNotifier.BlockConfig var3 = (BlockNotifier.BlockConfig)this.configById.get(id);
      if (var3 != null) {
         var3.color.set(-16777216 | rgb & (16777215));
      }
   }

   public void setBlocksEnabled(Collection ids, boolean enabled) {
      boolean var3 = false;

      for (class_2960 var5 : ids) {
         BlockNotifier.BlockConfig var6 = (BlockNotifier.BlockConfig)this.configById.get(var5);
         if (var6 != null && (Boolean)var6.enabled.get() != enabled) {
            var6.enabled.set(enabled);
            var3 = true;
         }
      }

      if (var3) {
         this.recountEnabledBlocks();
         this.dirtyFilter = true;
      }
   }

   public EnumSetting getThemeSetting() {
      return this.theme;
   }

   public BlockNotifier.ColorTheme getTheme() {
      return (BlockNotifier.ColorTheme)this.theme.get();
   }

   public void setTheme(BlockNotifier.ColorTheme t) {
      this.theme.set(t);
      this.cachedTheme = t;
      this.applyColorTheme(t);
   }

   public BoolSetting getToastNotifySetting() {
      return this.toastNotify;
   }

   public BoolSetting getSoundNotifySetting() {
      return this.soundNotify;
   }

   public BoolSetting getCordNotifySetting() {
      return this.cordNotify;
   }

   public DoubleSetting getRangeSetting() {
      return this.range;
   }

   public IntSetting getCooldownSetting() {
      return this.repeatCooldown;
   }

   public BoolSetting getTracersSetting() {
      return this.showTracers;
   }

   public BoolSetting getBoxSetting() {
      return this.showBox;
   }

   public IntSetting getTracerDurationSetting() {
      return this.tracerDuration;
   }

   public void resetToDefaults() {
      for (BlockNotifier.BlockConfig var2 : this.configById.values()) {
         boolean var3 = DEFAULT_NOTIFIED_PATHS.contains(var2.id.method_12832());
         var2.enabled.set(var3);
      }

      this.toastNotify.set(true);
      this.soundNotify.set(true);
      this.cordNotify.set(true);
      this.range.set(512.0);
      this.repeatCooldown.set(10);
      this.showTracers.set(false);
      this.showBox.set(false);
      this.tracerDuration.set(10);
      this.recountEnabledBlocks();
      this.dirtyFilter = true;
      if (this.isEnabled() && this.isInGame()) {
         this.fullRescan();
      }
   }

   public void syncFromBlocks(Set picked) {
      if (picked != null) {
         boolean var2 = false;

         for (BlockNotifier.BlockOption var4 : this.blockOptions) {
            BlockNotifier.BlockConfig var5 = (BlockNotifier.BlockConfig)this.configById.get(var4.id());
            if (var5 != null) {
               boolean var6 = picked.contains(var5.block);
               if ((Boolean)var5.enabled.get() != var6) {
                  var5.enabled.set(var6);
                  var2 = true;
               }
            }
         }

         if (var2) {
            this.recountEnabledBlocks();
            this.dirtyFilter = true;
            if (this.isEnabled() && this.isInGame()) {
               this.fullRescan();
            }
         }
      }
   }

   private void syncBlocksFromPicker() {
      this.syncFromBlocks(this.blocksFilter.getBlocks());
   }

   public void openFilterScreen() {
      mc.method_1507(new BlockNotifierFilterScreen(this));
   }

   public ItemBlockPickerScreen createLegacyPickerScreen() {
      this.blocksFilter.clearBlocks();

      for (BlockNotifier.BlockOption var2 : this.blockOptions) {
         BlockNotifier.BlockConfig var3 = (BlockNotifier.BlockConfig)this.configById.get(var2.id());
         if (var3 != null && (Boolean)var3.enabled.get()) {
            this.blocksFilter.addBlock(var3.block);
         }
      }

      this.pendingPickerSync = true;
      return ItemBlockPickerScreen.forBlocks(
         this.blocksFilter,
         (block, rgb) -> {
            int var3x = -16777216 | rgb & (16777215);
            BlockNotifier.BlockConfig var4 = (BlockNotifier.BlockConfig)this.configByBlock.get(block);
            if (var4 != null) {
               var4.color.set(var3x);
            }
         },
         () -> {
            String var1;
            if (this.theme.get() == BlockNotifier.ColorTheme.WARM) {
               byte[] var10000 = new byte[4];
               var10000[0] = (byte)(81);
               var10000[1] = (byte)(-5);
               var10000[2] = (byte)(110);
               var10000[3] = (byte)(-77);
               var1 = $$v_s(var10000, -6257996111865982941L);
            } else {
               byte[] var2x = new byte[4];
               var2x[0] = (byte)(69);
               var2x[1] = (byte)(-11);
               var2x[2] = (byte)(112);
               var2x[3] = (byte)(-70);
               var1 = $$v_s(var2x, -6257996111865982941L);
            }

            return var1;
         },
         chosenTheme -> {
            byte[] var10000 = new byte[4];
            var10000[0] = (byte)(81);
            var10000[1] = (byte)(-5);
            var10000[2] = (byte)(110);
            var10000[3] = (byte)(-77);
            BlockNotifier.ColorTheme var2x = $$v_s(var10000, -6257996111865982941L).equalsIgnoreCase(chosenTheme)
               ? BlockNotifier.ColorTheme.WARM
               : BlockNotifier.ColorTheme.COLD;
            this.setTheme(var2x);
         },
         this::syncFromBlocks
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.observedWorld = mc.field_1687;
      this.dirtyFilter = true;
      this.totalNotifiedCount = 0;
      this.notifiedBlocks.clear();
      this.activeVisuals.clear();
      this.fullRescan();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.resetForWorldTransition();
      this.notifiedBlocks.clear();
      this.activeVisuals.clear();
      this.totalNotifiedCount = 0;
      this.observedWorld = null;
      this.lastPlayerChunkX = -2147483648;
      this.lastPlayerChunkZ = -2147483648;
   }

   @Override
   public String getInfo() {
      if (this.enabledBlockCount == 0) {
         byte[] var10000 = new byte[18];
         var10000[0] = (byte)(72);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(60);
         var10000[3] = (byte)(-68);
         var10000[4] = (byte)(100);
         var10000[5] = (byte)(-112);
         var10000[6] = (byte)(71);
         var10000[7] = (byte)(-22);
         var10000[8] = (byte)(29);
         var10000[9] = (byte)(-126);
         var10000[10] = (byte)(-105);
         var10000[11] = (byte)(-109);
         var10000[12] = (byte)(60);
         var10000[13] = (byte)(66);
         var10000[14] = (byte)(111);
         var10000[15] = (byte)(-115);
         var10000[16] = (byte)(51);
         var10000[17] = (byte)(46);
         return $$v_s(var10000, -6257996111865982941L);
      } else {
         return this.totalNotifiedCount + " found | " + this.enabledBlockCount + " types";
      }
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent e) {
      if (this.isInGame()) {
         class_1923 var2 = e.getChunkPos();
         if (!e.isLoaded()) {
            this.removeChunk(var2);
         } else if (this.hasAnyEnabledBlocks()) {
            this.scanChunk(var2);
         }
      }
   }

   @EventHandler
   public void onReceivePacket(ReceivePacketEvent event) {
      if (this.isEnabled() && this.isInGame()) {
         if (event.getPacket() instanceof class_2626 var4) {
            mc.execute(() -> this.scanChunkAndNeighbors(var4.method_11309()));
         } else {
            if (event.getPacket() instanceof class_2637 var2) {
               mc.execute(() -> var2.method_30621((pos, state) -> this.scanChunkAndNeighbors(pos)));
            }
         }
      }
   }

   @EventHandler
   public void onTick(TickEvent e) {
      if (e.getPhase() == TickEvent.Phase.PRE) {
         if (this.pendingPickerSync && !(mc.field_1755 instanceof ItemBlockPickerScreen)) {
            this.pendingPickerSync = false;
            this.syncBlocksFromPicker();
         }

         if (this.isEnabled()) {
            BlockNotifier.ColorTheme var2 = (BlockNotifier.ColorTheme)this.theme.get();
            if (var2 != this.cachedTheme) {
               this.cachedTheme = var2;
               this.applyColorTheme(var2);
            }

            if (mc.field_1687 != this.observedWorld) {
               this.resetForWorldTransition();
               this.observedWorld = mc.field_1687;
               this.dirtyFilter = true;
            }

            if (this.isInGame()) {
               if (Math.abs(this.lastRange - (Double)this.range.get()) > 1.0E-6) {
                  this.lastRange = (Double)this.range.get();
               }

               class_1923 var3 = mc.field_1724.method_31476();
               if (var3.field_9181 != this.lastPlayerChunkX || var3.field_9180 != this.lastPlayerChunkZ) {
                  this.lastPlayerChunkX = var3.field_9181;
                  this.lastPlayerChunkZ = var3.field_9180;
                  this.enqueueNearbyLoadedChunks();
               }

               if (!this.hasAnyEnabledBlocks()) {
                  if (!this.candidateBlocksByChunk.isEmpty()) {
                     this.candidateBlocksByChunk.clear();
                  }
               } else if (this.dirtyFilter) {
                  this.dirtyFilter = false;
                  this.fullRescan();
               } else {
                  if (++this.rescanTimer >= (10)) {
                     this.rescanTimer = 0;
                     if (this.scanQueue.isEmpty()) {
                        this.enqueueNearbyLoadedChunks();
                     }
                  }

                  this.cleanupOutOfRangeChunks(var3.field_9181, var3.field_9180, this.getScanRadius());
                  this.drainScanQueue();
                  this.checkCandidateBlocksForNotification();
               }
            }
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame() && this.hasAnyEnabledBlocks()) {
         if ((Boolean)this.showTracers.get() || (Boolean)this.showBox.get()) {
            if (!this.activeVisuals.isEmpty()) {
               long var2 = System.currentTimeMillis();
               float var4 = event.getTickDelta();
               double var5 = mc.field_1724.method_23317();
               double var7 = mc.field_1724.method_23318();
               double var9 = mc.field_1724.method_23321();
               double var11 = (Double)this.range.get() * (Double)this.range.get();
               boolean var13 = (Boolean)this.showTracers.get();
               boolean var14 = (Boolean)this.showBox.get();
               Iterator var15 = this.activeVisuals.entrySet().iterator();

               while (var15.hasNext()) {
                  Entry var16 = (Entry)var15.next();
                  BlockNotifier.NotifiedVisual var17 = (BlockNotifier.NotifiedVisual)var16.getValue();
                  if (var2 > var17.expireAt) {
                     var15.remove();
                  } else {
                     class_2338 var18 = var17.pos;
                     double var19 = var18.method_10263() + 0.5;
                     double var21 = var18.method_10264() + 0.5;
                     double var23 = var18.method_10260() + 0.5;
                     double var25 = var19 - var5;
                     double var27 = var21 - var7;
                     double var29 = var23 - var9;
                     if (!(var25 * var25 + var27 * var27 + var29 * var29 > var11)) {
                        long var31 = var17.expireAt - var2;
                        float var33 = Math.min(1.0F, (float)var31 / 1000.0F);
                        int var34 = var17.color;
                        int var35 = (int)((var34 >>> 24 & 0xFF) * var33);
                        if (var35 > 0) {
                           int var36 = var34 & 16777215 | var35 << 24;
                           if (var14) {
                              int var37 = var18.method_10263();
                              int var38 = var18.method_10264();
                              int var39 = var18.method_10260();
                              RenderUtil.drawBox(var37, var38, var39, var37 + 1.0, var38 + 1.0, var39 + 1.0, var36);
                              RenderUtil.drawFilledBox(var37, var38, var39, var37 + 1.0, var38 + 1.0, var39 + 1.0, var36 & 16777215 | var35 / 3 << 24);
                           }

                           if (var13) {
                              RenderUtil.drawTracerToPos(var19, var21, var23, var36, var4);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void checkCandidateBlocksForNotification() {
      if (mc.field_1724 != null && !this.candidateBlocksByChunk.isEmpty()) {
         double var1 = mc.field_1724.method_23317();
         double var3 = mc.field_1724.method_23318();
         double var5 = mc.field_1724.method_23321();
         double var7 = (Double)this.range.get() * (Double)this.range.get();
         long var9 = System.currentTimeMillis();
         long var11 = ((Integer)this.repeatCooldown.get()).intValue() * 1000L;
         HashMap var13 = new HashMap();

         for (List var15 : this.candidateBlocksByChunk.values()) {
            for (BlockNotifier.CandidateBlock var17 : var15) {
               if ((Boolean)var17.config.enabled.get()) {
                  long var18 = var17.pos.method_10063();
                  Long var20 = (Long)this.notifiedBlocks.get(var18);
                  if (var20 == null || var11 != 0L && var9 - var20 >= var11) {
                     double var21 = var17.pos.method_40081(var1, var3, var5);
                     if (!(var21 > var7)) {
                        var13.computeIfAbsent(var17.config, k -> new ArrayList<>()).add(var17.pos);
                     }
                  }
               }
            }
         }

         if (!var13.isEmpty()) {
            this.processDetectedBlocks(var13);
         }
      }
   }

   private void processDetectedBlocks(Map detected) {
      if (mc.field_1724 != null) {
         class_243 var2 = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
         long var3 = System.currentTimeMillis();
         boolean var5 = false;

         for (Entry var7 : detected.entrySet()) {
            BlockNotifier.BlockConfig var8 = (BlockNotifier.BlockConfig)var7.getKey();
            List var9 = (List)var7.getValue();
            if (!var9.isEmpty()) {
               var9.sort(Comparator.comparingDouble(p -> p.method_19770(var2)));
               class_2338 var10 = (class_2338)var9.get(0);
               int var11 = var9.size();
               int var12 = (int)Math.round(Math.sqrt(var10.method_19770(var2)));

               for (class_2338 var14 : var9) {
                  this.notifiedBlocks.put(var14.method_10063(), var3);
                  if ((Boolean)this.showTracers.get() || (Boolean)this.showBox.get()) {
                     long var15 = var3 + ((Integer)this.tracerDuration.get()).intValue() * 1000L;
                     this.activeVisuals.put(var14.method_10063(), new BlockNotifier.NotifiedVisual(var14, (Integer)var8.color.get(), var15));
                  }
               }

               this.totalNotifiedCount += var11;
               String var19 = var8.displayName;
               String var20 = var11 > 1 ? var11 + "x " : "";
               if ((Boolean)this.toastNotify.get()) {
                  String var21 = var20 + var19;
                  String var16 = String.format(
                     "%d, %d, %d (%dm away)",
                     var10.method_10263(),
                     var10.method_10264(),
                     var10.method_10260(),
                     var12
                  );
                  class_1792 var17 = var8.block.method_8389();
                  if (var17 == class_1802.field_8162) {
                     var17 = ItemBlockPickerScreen.resolveBlockItemFallback(var8.block);
                  }

                  class_1799 var18 = var17 != class_1802.field_8162 ? new class_1799(var17) : class_1799.field_8037;
                  ToastManager.pushBlock(var21, var16, var18, true);
               }

               var5 = true;
            }
         }

         if (var5 && (Boolean)this.soundNotify.get()) {
            mc.field_1724.method_5783((class_3414)class_3417.field_14793.comp_349(), 1.0F, 1.0F);
         }
      }
   }

   private void buildBlockFilters() {
      ArrayList var1 = new ArrayList();
      HashSet var2 = new HashSet();

      for (class_2960 var4 : class_7923.field_41175.method_10235()) {
         byte[] var10000 = new byte[9];
         var10000[0] = (byte)(107);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-69);
         var10000[4] = (byte)(107);
         var10000[5] = (byte)(-115);
         var10000[6] = (byte)(69);
         var10000[7] = (byte)(-25);
         var10000[8] = (byte)(26);
         if ($$v_s(var10000, -6257996111865982941L).equals(var4.method_12836())) {
            class_2248 var5 = (class_2248)class_7923.field_41175.method_63535(var4);
            if (var5 != class_2246.field_10124
               && var5 != class_2246.field_10543
               && var5 != class_2246.field_10243
               && var5 != class_2246.field_10499
               && var2.add(var5)) {
               var1.add(var4);
            }
         }
      }

      var1.sort(Comparator.comparing(class_2960::toString));
      BlockNotifier.ColorTheme var14 = this.theme != null ? (BlockNotifier.ColorTheme)this.theme.get() : BlockNotifier.ColorTheme.WARM;

      for (class_2960 var16 : var1) {
         class_2248 var6 = (class_2248)class_7923.field_41175.method_63535(var16);
         if (var6 != null) {
            class_1799 var7 = resolveBlockIcon(var6, var16);
            if (var7 != null && !var7.method_7960()) {
               String var8 = sanitizeIdentifier(var16);
               boolean var9 = DEFAULT_NOTIFIED_PATHS.contains(var16.method_12832());
               BoolSetting var10 = (BoolSetting)this.addSetting((BoolSetting)hidden(new BoolSetting("BlockEnabled_" + var8, "Enable " + var16, var9)));
               var10.onChanged(val -> {
                  this.recountEnabledBlocks();
                  this.dirtyFilter = true;
               });
               ColorSetting var11 = (ColorSetting)this.addSetting(
                  (ColorSetting)hidden(new ColorSetting("BlockColor_" + var8, "Color for " + var16, colorForTheme(var16, var14)))
               );
               String var12 = resolveDisplayName(var7, var16);
               this.blockOptions.add(new BlockNotifier.BlockOption(var16, var12, var7, (var12 + " " + var16).toLowerCase(Locale.ROOT)));
               BlockNotifier.BlockConfig var13 = new BlockNotifier.BlockConfig(var6, var16, var12, var7, var10, var11);
               this.configById.put(var16, var13);
               this.configByBlock.put(var6, var13);
            }
         }
      }

      this.recountEnabledBlocks();
   }

   private void recountEnabledBlocks() {
      int var1 = 0;

      for (BlockNotifier.BlockConfig var3 : this.configById.values()) {
         if ((Boolean)var3.enabled.get()) {
            var1++;
         }
      }

      this.enabledBlockCount = var1;
   }

   private static class_1799 resolveBlockIcon(class_2248 block, class_2960 id) {
      class_1792 var2 = block.method_8389();
      if (var2 != class_1802.field_8162) {
         return new class_1799(var2);
      } else {
         String var3 = id.method_12832();
         if (var3.contains("water")) {
            return new class_1799(class_1802.field_8705);
         } else if (var3.contains("lava")) {
            return new class_1799(class_1802.field_8187);
         } else if (var3.contains("portal")) {
            return new class_1799(class_1802.field_8281);
         } else if (var3.contains("fire")) {
            return new class_1799(class_1802.field_8884);
         } else if (var3.contains("piston")) {
            return new class_1799(class_1802.field_8249);
         } else if (var3.contains("head")
            || var3.contains("skull")) {
            return new class_1799(class_1802.field_8398);
         } else if (var3.contains("melon")) {
            return new class_1799(class_1802.field_46250);
         } else if (var3.contains("pumpkin")) {
            return new class_1799(class_1802.field_46249);
         } else if (var3.contains("kelp")) {
            return new class_1799(class_1802.field_17532);
         } else if (var3.contains("bamboo")) {
            return new class_1799(class_1802.field_8648);
         } else if (var3.contains("vines")) {
            return new class_1799(class_1802.field_28659);
         } else if (var3.contains("ice")) {
            return new class_1799(class_1802.field_8426);
         } else if (var3.contains("potted")) {
            return new class_1799(class_1802.field_8074);
         } else if (var3.contains("tripwire")) {
            return new class_1799(class_1802.field_8276);
         } else if (var3.contains("redstone")) {
            return new class_1799(class_1802.field_8725);
         } else if (var3.contains("repeater")) {
            return new class_1799(class_1802.field_8619);
         } else {
            return var3.contains("comparator")
               ? new class_1799(class_1802.field_8857)
               : new class_1799(class_1802.field_20391);
         }
      }
   }

   private static String resolveDisplayName(class_1799 icon, class_2960 id) {
      String var2 = icon != null && !icon.method_7960() ? icon.method_7964().getString() : null;
      return var2 != null && !var2.isBlank() ? var2 : prettifyPath(id.method_12832());
   }

   private boolean hasAnyEnabledBlocks() {
      return this.enabledBlockCount > 0;
   }

   private int getScanRadius() {
      return Math.max(1, (Integer)mc.field_1690.method_42503().method_41753());
   }

   private static int chessDistance(class_1923 first, class_1923 second) {
      return Math.max(Math.abs(first.field_9181 - second.field_9181), Math.abs(first.field_9180 - second.field_9180));
   }

   private void resetForWorldTransition() {
      this.scanGeneration++;
      this.scanRunning = false;
      this.scanQueue.clear();
      this.queuedChunks.clear();
      this.candidateBlocksByChunk.clear();
      this.rescanTimer = 0;
      this.lastPlayerChunkX = -2147483648;
      this.lastPlayerChunkZ = -2147483648;
   }

   private void fullRescan() {
      this.scanGeneration++;
      this.scanRunning = false;
      this.scanQueue.clear();
      this.queuedChunks.clear();
      this.candidateBlocksByChunk.clear();
      if (this.isInGame() && this.hasAnyEnabledBlocks()) {
         this.enqueueNearbyLoadedChunks();
      }
   }

   private void enqueueNearbyLoadedChunks() {
      if (this.isInGame()) {
         class_1923 var1 = mc.field_1724.method_31476();
         int var2 = this.getScanRadius();
         ArrayList var3 = new ArrayList((var2 * (2) + 1) * (var2 * (2) + 1));

         for (int var4 = var1.field_9181 - var2; var4 <= var1.field_9181 + var2; var4++) {
            for (int var5 = var1.field_9180 - var2; var5 <= var1.field_9180 + var2; var5++) {
               if (mc.field_1687.method_2935().method_12126(var4, var5, false) != null) {
                  var3.add(new class_1923(var4, var5));
               }
            }
         }

         var3.sort(Comparator.comparingInt(pos -> chessDistance(pos, var1)));

         for (class_1923 var7 : var3) {
            this.scanChunk(var7);
         }
      }
   }

   private void scanChunk(class_1923 pos) {
      if (this.queuedChunks.add(pos)) {
         this.scanQueue.offer(pos);
      }
   }

   private void scanChunkAndNeighbors(class_2338 pos) {
      int var2 = pos.method_10263() >> (4);
      int var3 = pos.method_10260() >> (4);
      this.scanChunk(new class_1923(var2, var3));
      if ((pos.method_10263() & (15)) == 0) {
         this.scanChunk(new class_1923(var2 - 1, var3));
      }

      if ((pos.method_10263() & (15)) == (15)) {
         this.scanChunk(new class_1923(var2 + 1, var3));
      }

      if ((pos.method_10260() & (15)) == 0) {
         this.scanChunk(new class_1923(var2, var3 - 1));
      }

      if ((pos.method_10260() & (15)) == (15)) {
         this.scanChunk(new class_1923(var2 + 1, var3));
      }
   }

   private void removeChunk(class_1923 pos) {
      this.queuedChunks.remove(pos);
      this.candidateBlocksByChunk.remove(pos);
   }

   private void cleanupOutOfRangeChunks(int pcx, int pcz, int radius) {
      int var4 = radius + (2);
      this.candidateBlocksByChunk.keySet().removeIf(pos -> Math.abs(pos.field_9181 - pcx) > var4 || Math.abs(pos.field_9180 - pcz) > var4);
   }

   private void drainScanQueue() {
      if (!this.scanRunning && this.hasAnyEnabledBlocks()) {
         ArrayList var1 = new ArrayList(16);

         class_1923 var2;
         while (var1.size() < (16) && (var2 = (class_1923)this.scanQueue.poll()) != null) {
            this.queuedChunks.remove(var2);
            class_2818 var3 = mc.field_1687.method_2935().method_12126(var2.field_9181, var2.field_9180, false);
            if (var3 == null) {
               this.removeChunk(var2);
            } else {
               var1.add(this.capture(var3));
            }
         }

         if (!var1.isEmpty()) {
            long var6 = this.scanGeneration;
            class_638 var5 = mc.field_1687;
            this.scanRunning = true;
            this.scanner.execute(() -> {
               ArrayList var5x = new ArrayList(var1.size());

               try {
                  for (BlockNotifier.ChunkSnapshot var7 : var1) {
                     var5x.add(this.analyze(var7));
                  }
               } catch (Throwable var8) {
                  byte[] var10001 = new byte[49];
                  var10001[0] = (byte)(93);
                  var10001[1] = (byte)(-40);
                  var10001[2] = (byte)(112);
                  var10001[3] = (byte)(-79);
                  var10001[4] = (byte)(107);
                  var10001[5] = (byte)(-108);
                  var10001[6] = (byte)(106);
                  var10001[7] = (byte)(-18);
                  var10001[8] = (byte)(26);
                  var10001[9] = (byte)(-53);
                  var10001[10] = (byte)(-126);
                  var10001[11] = (byte)(-97);
                  var10001[12] = (byte)(53);
                  var10001[13] = (byte)(85);
                  var10001[14] = (byte)(81);
                  var10001[15] = (byte)(-39);
                  var10001[16] = (byte)(16);
                  var10001[17] = (byte)(43);
                  var10001[18] = (byte)(-91);
                  var10001[19] = (byte)(-62);
                  var10001[20] = (byte)(-35);
                  var10001[21] = (byte)(107);
                  var10001[22] = (byte)(84);
                  var10001[23] = (byte)(-91);
                  var10001[24] = (byte)(-47);
                  var10001[25] = (byte)(50);
                  var10001[26] = (byte)(-11);
                  var10001[27] = (byte)(40);
                  var10001[28] = (byte)(-31);
                  var10001[29] = (byte)(27);
                  var10001[30] = (byte)(37);
                  var10001[31] = (byte)(115);
                  var10001[32] = (byte)(-125);
                  var10001[33] = (byte)(26);
                  var10001[34] = (byte)(31);
                  var10001[35] = (byte)(31);
                  var10001[36] = (byte)(-104);
                  var10001[37] = (byte)(43);
                  var10001[38] = (byte)(-15);
                  var10001[39] = (byte)(19);
                  var10001[40] = (byte)(-85);
                  var10001[41] = (byte)(-90);
                  var10001[42] = (byte)(100);
                  var10001[43] = (byte)(117);
                  var10001[44] = (byte)(88);
                  var10001[45] = (byte)(-14);
                  var10001[46] = (byte)(-126);
                  var10001[47] = (byte)(50);
                  var10001[48] = (byte)(69);
                  VieClientMod.LOGGER.error($$v_s(var10001, -6257996111865982941L), var8);
               }

               mc.execute(() -> {
                  try {
                     if (this.isEnabled() && mc.field_1687 == var5 && var6 == this.scanGeneration && this.hasAnyEnabledBlocks()) {
                        for (BlockNotifier.ChunkScanned var6x : var5x) {
                           if (var6x.candidates.isEmpty()) {
                              this.candidateBlocksByChunk.remove(var6x.position);
                           } else {
                              this.candidateBlocksByChunk.put(var6x.position, var6x.candidates);
                           }
                        }
                     }
                  } finally {
                     if (var6 == this.scanGeneration) {
                        this.scanRunning = false;
                     }
                  }
               });
            });
         }
      }
   }

   private BlockNotifier.ChunkSnapshot capture(class_2818 chunk) {
      ArrayList var2 = new ArrayList();
      class_1923 var3 = chunk.method_12004();
      class_2826[] var4 = chunk.method_12006();
      int var5 = chunk.method_31607();

      for (int var6 = 0; var6 < var4.length; var6++) {
         class_2826 var7 = var4[var6];
         if (var7 != null && !var7.method_38292() && this.sectionMayContainEnabledBlock(var7)) {
            int var8 = var5 + (var6 << (4));

            for (int var9 = 0; var9 < (16); var9++) {
               for (int var10 = 0; var10 < (16); var10++) {
                  for (int var11 = 0; var11 < (16); var11++) {
                     class_2680 var12 = var7.method_12254(var10, var9, var11);
                     BlockNotifier.BlockConfig var13 = (BlockNotifier.BlockConfig)this.configByBlock.get(var12.method_26204());
                     if (var13 != null && (Boolean)var13.enabled.get()) {
                        int var14 = var3.method_8326() + var10;
                        int var15 = var8 + var9;
                        int var16 = var3.method_8328() + var11;
                        var2.add(new BlockNotifier.CandidateBlock(new class_2338(var14, var15, var16), var13));
                     }
                  }
               }
            }
         }
      }

      return new BlockNotifier.ChunkSnapshot(var3, List.copyOf(var2));
   }

   private BlockNotifier.ChunkScanned analyze(BlockNotifier.ChunkSnapshot snapshot) {
      return new BlockNotifier.ChunkScanned(snapshot.position, snapshot.candidates);
   }

   private boolean sectionMayContainEnabledBlock(class_2826 section) {
      return section.method_19523(state -> {
         BlockNotifier.BlockConfig var2 = (BlockNotifier.BlockConfig)this.configByBlock.get(state.method_26204());
         return var2 != null && (Boolean)var2.enabled.get();
      });
   }

   public void applyColorTheme(BlockNotifier.ColorTheme t) {
      for (BlockNotifier.BlockConfig var3 : this.configById.values()) {
         var3.color.set(colorForTheme(var3.id, t));
      }
   }

   public static int colorForTheme(class_2960 id, BlockNotifier.ColorTheme theme) {
      int var2 = Math.abs(id.hashCode());
      float var3 = (var2 & (65535)) / 65535.0F;
      float var4 = 0.75F + (var2 >>> (8) & (63)) / 63.0F * 0.22F;
      float var5 = 0.92F + (var2 >>> (16) & (15)) / 15.0F * 0.08F;
      float var6;
      if (themex == BlockNotifier.ColorTheme.WARM) {
         float var7 = -15.0F + var3 * 70.0F;
         if (var7 < 0.0F) {
            var7 += 360.0F;
         }

         var6 = var7 / 360.0F;
      } else {
         float var8 = 165.0F + var3 * 110.0F;
         var6 = var8 / 360.0F;
      }

      return -16777216 | hsvToRgb(var6, var4, var5);
   }

   private static String prettifyPath(String path) {
      String var1 = path.replace((char)(95), (char)(32)).replace((char)(47), (char)(32));
      return var1.isEmpty() ? path : Character.toUpperCase(var1.charAt(0)) + var1.substring(1);
   }

   private static String sanitizeIdentifier(class_2960 id) {
      String var1 = id.toString();
      StringBuilder var2 = new StringBuilder();

      for (char var6 : var1.toCharArray()) {
         var2.append((char)(Character.isLetterOrDigit(var6) ? var6 : 95));
      }

      return var2.toString();
   }

   private static int hsvToRgb(float h, float s, float v) {
      float var3 = h * 6.0F;
      float var4 = v * (1.0F - s);
      float var5 = v * (1.0F - s * (var3 - (float)Math.floor(var3)));
      float var6 = v * (1.0F - s * (1.0F - (var3 - (float)Math.floor(var3))));

      return switch ((int)var3) {
         case 0 -> rgb(v, var6, var4);
         case 1 -> rgb(var5, v, var4);
         case 2 -> rgb(var4, v, var6);
         case 3 -> rgb(var4, var5, v);
         case 4 -> rgb(var6, var4, v);
         default -> rgb(v, var4, var5);
      };
   }

   private static int rgb(float r, float g, float b) {
      return (int)(r * 255.0F + 0.5F) << (16) | (int)(g * 255.0F + 0.5F) << (8) | (int)(b * 255.0F + 0.5F);
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

   public static final class BlockConfig {
      public final class_2248 block;
      public final class_2960 id;
      public final String displayName;
      public final class_1799 icon;
      public final BoolSetting enabled;
      public final ColorSetting color;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public BlockConfig(class_2248 block, class_2960 id, String displayName, class_1799 icon, BoolSetting enabled, ColorSetting color) {
         this.block = blockx;
         this.id = idx;
         this.displayName = displayNamex;
         this.icon = iconx;
         this.enabled = enabledx;
         this.color = colorx;
      }
   }

   public record BlockOption(class_2960 id, String name, class_1799 icon, String searchKey) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record CandidateBlock(class_2338 pos, BlockNotifier.BlockConfig config) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record ChunkScanned(class_1923 position, List<BlockNotifier.CandidateBlock> candidates) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record ChunkSnapshot(class_1923 position, List<BlockNotifier.CandidateBlock> candidates) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum ColorTheme {
      WARM,
      COLD;

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ColorTheme(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static BlockNotifier.ColorTheme[] $values() {
         BlockNotifier.ColorTheme[] var10000 = new BlockNotifier.ColorTheme[2];
         var10000[0] = WARM;
         var10000[1] = COLD;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-109);
         String var0 = $$v_s(var10002, 7246427595669229594L);
         byte[] var10004 = new byte[4];
         var10004[0] = (byte)(104);
         var10004[1] = (byte)(-5);
         var10004[2] = (byte)(110);
         var10004[3] = (byte)(-77);
         WARM = new BlockNotifier.ColorTheme($$v_s(var10004, 7246427595669229594L));
         var10002 = new byte[4];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-102);
         String var2 = $$v_s(var10002, 7246427595669229594L);
         var10004 = new byte[4];
         var10004[0] = (byte)(124);
         var10004[1] = (byte)(-11);
         var10004[2] = (byte)(112);
         var10004[3] = (byte)(-70);
         COLD = new BlockNotifier.ColorTheme($$v_s(var10004, 7246427595669229594L));
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

   private record NotifiedVisual(class_2338 pos, int color, long expireAt) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
