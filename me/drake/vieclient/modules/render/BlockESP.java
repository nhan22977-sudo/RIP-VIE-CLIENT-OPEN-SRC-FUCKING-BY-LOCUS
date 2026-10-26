package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
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
import me.drake.vieclient.gui.nvg.adapter.setting.BlocksSetting;
import me.drake.vieclient.gui.nvg.ui.components.ItemBlockPickerScreen;
import me.drake.vieclient.util.RainbowUtil;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.util.render.EspFade;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_2960;
import net.minecraft.class_638;
import net.minecraft.class_7923;

public class BlockESP extends Module {
   private static final int MAX_CAPTURE_PER_TICK = 16;
   private static final int RESCAN_TICKS = 8;
   private final ButtonSetting openFilter = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Open Filter",
         "Open block type filter",
         this::openFilterScreen
      )
   );
   private final EnumSetting theme = (EnumSetting)this.addSetting(
      new EnumSetting(
            "Palette",
            "Color palette theme for blocks (Warm / Cold / Rainbow / Custom)",
            BlockESP.ColorTheme.WARM
         )
         .withDisplayName(BlockESP.ColorTheme.WARM, "Warm")
         .withDisplayName(BlockESP.ColorTheme.COLD, "Cold")
         .withDisplayName(BlockESP.ColorTheme.RAINBOW, "Rainbow")
         .withDisplayName(BlockESP.ColorTheme.CUSTOM, "Custom")
   );
   private final BoolSetting tracers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tracers",
         "Draw tracer lines",
         false
      )
   );
   private final DoubleSetting range = (DoubleSetting)this.addSetting(
      (DoubleSetting)hidden(
         new DoubleSetting(
            "Range",
            "Render range",
            512.0,
            16.0,
            512.0
         )
      )
   );
   private final IntSetting fillAlpha = (IntSetting)this.addSetting(
      new IntSetting(
         "Alpha",
         "Fill opacity",
         30,
         0,
         255
      )
   );
   private BlockESP.ColorTheme cachedTheme = null;
   private boolean isApplyingTheme = false;
   private final List blockOptions = new ArrayList();
   private final List blockOptionsView = Collections.unmodifiableList(this.blockOptions);
   private final Map configById = new HashMap();
   private final Map configByBlock = new HashMap();
   private final Map chunkCache = new ConcurrentHashMap();
   private final ConcurrentLinkedQueue scanQueue = new ConcurrentLinkedQueue();
   private final Set queuedChunks = ConcurrentHashMap.newKeySet();
   private final ExecutorService scanner = Executors.newSingleThreadExecutor(r -> {
      byte[] var10003 = new byte[24];
      var10003[0] = (byte)(96);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-67);
      var10003[4] = (byte)(-46);
      var10003[5] = (byte)(114);
      var10003[6] = (byte)(82);
      var10003[7] = (byte)(70);
      var10003[8] = (byte)(10);
      var10003[9] = (byte)(-113);
      var10003[10] = (byte)(-122);
      var10003[11] = (byte)(-102);
      var10003[12] = (byte)(-119);
      var10003[13] = (byte)(-96);
      var10003[14] = (byte)(116);
      var10003[15] = (byte)(125);
      var10003[16] = (byte)(35);
      var10003[17] = (byte)(57);
      var10003[18] = (byte)(-68);
      var10003[19] = (byte)(-125);
      var10003[20] = (byte)(125);
      var10003[21] = (byte)(-120);
      var10003[22] = (byte)(6);
      var10003[23] = (byte)(22);
      Thread var1 = new Thread(r, $$v_s(var10003, 14853333893500979L));
      var1.setDaemon(true);
      return var1;
   });
   private boolean scanRunning;
   private long scanGeneration;
   private class_1937 observedWorld;
   private int cachedBlockCount;
   private int rescanTimer;
   private int lastPlayerChunkX = Integer.MIN_VALUE;
   private int lastPlayerChunkZ = Integer.MIN_VALUE;
   private boolean dirtyFilter = true;
   private double lastRange = -1.0;
   private final BlocksSetting blocksFilter = new BlocksSetting("Blocks");
   private boolean pendingPickerSync;
   private final EspFade espFade = new EspFade(this::finishDisable);
   private int enabledBlockCount = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BlockESP() {
      super(
         "BlockESP",
         "Highlights selected block types",
         Category.RENDER
      );
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

   public boolean isBlockEnabled(class_2960 id) {
      BlockESP.BlockConfig var2 = (BlockESP.BlockConfig)this.configById.get(id);
      return var2 != null && (Boolean)var2.enabled.get();
   }

   public void setBlockEnabled(class_2960 id, boolean enabled) {
      BlockESP.BlockConfig var3 = (BlockESP.BlockConfig)this.configById.get(id);
      if (var3 != null && (Boolean)var3.enabled.get() != enabled) {
         var3.enabled.set(enabled);
         this.dirtyFilter = true;
      }
   }

   public int getBlockColor(class_2960 id) {
      BlockESP.BlockConfig var2 = (BlockESP.BlockConfig)this.configById.get(id);
      return var2 == null ? -1 : (Integer)var2.color.get();
   }

   public void setBlockColor(class_2960 id, int rgb) {
      BlockESP.BlockConfig var3 = (BlockESP.BlockConfig)this.configById.get(id);
      if (var3 != null) {
         int var4 = -16777216 | rgb & (16777215);
         var3.color.set(var4);
         this.checkAndApplyCustomTheme(id, var4);
      }
   }

   public void setBlocksEnabled(Collection ids, boolean enabled) {
      boolean var3 = false;

      for (class_2960 var5 : ids) {
         BlockESP.BlockConfig var6 = (BlockESP.BlockConfig)this.configById.get(var5);
         if (var6 != null && (Boolean)var6.enabled.get() != enabled) {
            var6.enabled.set(enabled);
            var3 = true;
         }
      }

      if (var3) {
         this.dirtyFilter = true;
      }
   }

   public EnumSetting getThemeSetting() {
      return this.theme;
   }

   public BlockESP.ColorTheme getTheme() {
      return (BlockESP.ColorTheme)this.theme.get();
   }

   public void setTheme(BlockESP.ColorTheme t) {
      this.theme.set(t);
      this.cachedTheme = t;
      this.applyColorTheme(t);
   }

   public void syncFromBlocks(Set picked) {
      if (picked != null) {
         boolean var2 = false;

         for (BlockESP.BlockOption var4 : this.blockOptions) {
            BlockESP.BlockConfig var5 = (BlockESP.BlockConfig)this.configById.get(var4.id());
            if (var5 != null) {
               boolean var6 = picked.contains(var5.block);
               if ((Boolean)var5.enabled.get() != var6) {
                  var5.enabled.set(var6);
                  var2 = true;
               }
            }
         }

         if (var2) {
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
      this.blocksFilter.clearBlocks();
      HashMap var1 = new HashMap();

      for (BlockESP.BlockOption var3 : this.blockOptions) {
         BlockESP.BlockConfig var4 = (BlockESP.BlockConfig)this.configById.get(var3.id());
         if (var4 != null) {
            if ((Boolean)var4.enabled.get()) {
               this.blocksFilter.addBlock(var4.block);
            }

            var1.put(var4.block, (Integer)var4.color.get() & (16777215));
         }
      }

      ItemBlockPickerScreen[] var5 = new ItemBlockPickerScreen[1];
      ItemBlockPickerScreen var6 = ItemBlockPickerScreen.forBlocks(this.blocksFilter, (block, rgb) -> {
         int var3x = -16777216 | rgb & (16777215);
         BlockESP.BlockConfig var4x = (BlockESP.BlockConfig)this.configByBlock.get(block);
         if (var4x != null) {
            var4x.color.set(var3x);
         }

         class_1792 var5x = block.method_8389();
         if (var5x != null && var5x != class_1802.field_8162) {
            for (BlockESP.BlockConfig var7 : this.configById.values()) {
               if (var7.block != block && var7.block.method_8389() == var5x) {
                  var7.color.set(var3x);
               }
            }
         }
      }, () -> {
         return switch ((BlockESP.ColorTheme)this.theme.get()) {
            case WARM -> {
               byte[] var4x = new byte[4];
               var4x[0] = (byte)(65);
               var4x[1] = (byte)(-5);
               var4x[2] = (byte)(110);
               var4x[3] = (byte)(-77);
               yield $$v_s(var4x, 14853333893500979L);
            }
            case COLD -> {
               byte[] var3x = new byte[4];
               var3x[0] = (byte)(85);
               var3x[1] = (byte)(-11);
               var3x[2] = (byte)(112);
               var3x[3] = (byte)(-70);
               yield $$v_s(var3x, 14853333893500979L);
            }
            case RAINBOW -> {
               byte[] var2 = new byte[7];
               var2[0] = (byte)(68);
               var2[1] = (byte)(-5);
               var2[2] = (byte)(117);
               var2[3] = (byte)(-80);
               var2[4] = (byte)(-36);
               var2[5] = (byte)(116);
               var2[6] = (byte)(64);
               yield $$v_s(var2, 14853333893500979L);
            }
            case CUSTOM -> {
               byte[] var10000 = new byte[6];
               var10000[0] = (byte)(85);
               var10000[1] = (byte)(-17);
               var10000[2] = (byte)(111);
               var10000[3] = (byte)(-86);
               var10000[4] = (byte)(-47);
               var10000[5] = (byte)(118);
               yield $$v_s(var10000, 14853333893500979L);
            }
         };
      }, chosenTheme -> {
         byte[] var10000 = new byte[4];
         var10000[0] = (byte)(65);
         var10000[1] = (byte)(-5);
         var10000[2] = (byte)(110);
         var10000[3] = (byte)(-77);
         BlockESP.ColorTheme var7;
         if ($$v_s(var10000, 14853333893500979L).equalsIgnoreCase(chosenTheme)) {
            var7 = BlockESP.ColorTheme.WARM;
         } else {
            var10000 = new byte[4];
            var10000[0] = (byte)(85);
            var10000[1] = (byte)(-11);
            var10000[2] = (byte)(112);
            var10000[3] = (byte)(-70);
            if ($$v_s(var10000, 14853333893500979L).equalsIgnoreCase(chosenTheme)) {
               var7 = BlockESP.ColorTheme.COLD;
            } else {
               var10000 = new byte[7];
               var10000[0] = (byte)(68);
               var10000[1] = (byte)(-5);
               var10000[2] = (byte)(117);
               var10000[3] = (byte)(-80);
               var10000[4] = (byte)(-36);
               var10000[5] = (byte)(116);
               var10000[6] = (byte)(64);
               var7 = $$v_s(var10000, 14853333893500979L).equalsIgnoreCase(chosenTheme) ? BlockESP.ColorTheme.RAINBOW : BlockESP.ColorTheme.CUSTOM;
            }
         }

         BlockESP.ColorTheme var3x = var7;
         this.theme.set(var3x);
         this.cachedTheme = var3x;
         if (var3x != BlockESP.ColorTheme.CUSTOM && var3x != BlockESP.ColorTheme.RAINBOW) {
            this.applyColorTheme(var3x);
         }

         if (var5[0] != null) {
            for (BlockESP.BlockOption var5x : this.blockOptions) {
               BlockESP.BlockConfig var6x = (BlockESP.BlockConfig)this.configById.get(var5x.id());
               if (var6x != null) {
                  var5[0].getBlockColors().put(var6x.block, (Integer)var6x.color.get() & (16777215));
               }
            }
         }
      }, this::syncFromBlocks);
      var5[0] = var6;
      var6.getBlockColors().putAll(var1);
      mc.method_1507(var6);
      this.pendingPickerSync = true;
   }

   private void finishDisable() {
      super.onDisable();
      this.resetForWorldTransition();
      this.observedWorld = null;
      this.lastPlayerChunkX = -2147483648;
      this.lastPlayerChunkZ = -2147483648;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.observedWorld = mc.field_1687;
      BlockESP.ColorTheme var1 = (BlockESP.ColorTheme)this.theme.get();
      if (var1 != BlockESP.ColorTheme.CUSTOM && this.hasNonThemeColors(var1)) {
         this.theme.set(BlockESP.ColorTheme.CUSTOM);
         this.cachedTheme = BlockESP.ColorTheme.CUSTOM;
      } else {
         this.cachedTheme = var1;
      }

      this.syncBlocksFromPicker();
      this.dirtyFilter = true;
      this.fullRescan();
      this.espFade.onEnable();
   }

   @Override
   public void onDisable() {
      this.espFade.onDisable();
   }

   @Override
   public String getInfo() {
      return this.cachedBlockCount + " | " + this.getEnabledBlockTypeCount() + " | " + (int)((Double)this.range.get()).doubleValue() + "m";
   }

   @Override
   public boolean usesEntityCache() {
      return false;
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent e) {
      if (this.isInGame()) {
         if (!this.hasAnyEnabledBlocks()) {
            if (!e.isLoaded()) {
               this.removeChunkCache(e.getChunkPos());
            }
         } else if (e.isLoaded()) {
            this.scanChunk(e.getChunkPos());
         } else {
            this.removeChunkCache(e.getChunkPos());
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
         this.espFade.update();
         if (mc.field_1755 instanceof ItemBlockPickerScreen) {
            this.syncBlocksFromPicker();
         } else if (this.pendingPickerSync) {
            this.pendingPickerSync = false;
            this.syncBlocksFromPicker();
         }

         if (this.isEnabled()) {
            BlockESP.ColorTheme var2 = (BlockESP.ColorTheme)this.theme.get();
            if (var2 != this.cachedTheme) {
               this.cachedTheme = var2;
               if (var2 != BlockESP.ColorTheme.CUSTOM) {
                  this.applyColorTheme(var2);
               }
            }

            if (mc.field_1687 != this.observedWorld) {
               this.resetForWorldTransition();
               this.observedWorld = mc.field_1687;
               this.dirtyFilter = true;
            }

            if (this.isInGame()) {
               if (Math.abs(this.lastRange - (Double)this.range.get()) > 1.0E-6) {
                  this.lastRange = (Double)this.range.get();
                  this.dirtyFilter = true;
               }

               class_1923 var3 = mc.field_1724.method_31476();
               if (var3.field_9181 != this.lastPlayerChunkX || var3.field_9180 != this.lastPlayerChunkZ) {
                  this.lastPlayerChunkX = var3.field_9181;
                  this.lastPlayerChunkZ = var3.field_9180;
                  this.enqueueNearbyLoadedChunks();
               }

               if (!this.hasAnyEnabledBlocks()) {
                  if (this.cachedBlockCount != 0) {
                     this.chunkCache.clear();
                     this.cachedBlockCount = 0;
                  }
               } else if (this.dirtyFilter) {
                  this.dirtyFilter = false;
                  this.fullRescan();
               } else {
                  if (++this.rescanTimer >= (8)) {
                     this.rescanTimer = 0;
                     if (this.scanQueue.isEmpty()) {
                        this.enqueueNearbyLoadedChunks();
                     }
                  }

                  this.cleanupOutOfRangeChunks(mc.field_1724.method_31476().field_9181, mc.field_1724.method_31476().field_9180, this.getScanRadius());
                  this.drainScanQueue();
               }
            }
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent e) {
      if (e.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame() && this.hasAnyEnabledBlocks()) {
         float var2 = this.espFade.getFade();
         if (!(var2 <= 0.001F)) {
            float var3 = e.getTickDelta();
            double var4 = (Double)this.range.get() * (Double)this.range.get();
            double var6 = this.getEffectiveRenderRange();
            double var8 = Math.max(var4, var6 * var6);
            double var10 = mc.field_1724.method_23317();
            double var12 = mc.field_1724.method_23318();
            double var14 = mc.field_1724.method_23321();
            int var16 = (int)(((Integer)this.fillAlpha.get()).intValue() * var2);
            boolean var17 = (Boolean)this.tracers.get();
            int var18 = (int)(255.0F * var2);
            if (var16 > 0 || var17 && var18 > 0) {
               boolean var19 = this.theme.get() == BlockESP.ColorTheme.RAINBOW;

               for (List var21 : this.chunkCache.values()) {
                  for (BlockESP.CachedBlock var23 : var21) {
                     if (var17 || var23.visibleFaces != 0) {
                        double var24 = var23.centerX - var10;
                        double var26 = var23.centerY - var12;
                        double var28 = var23.centerZ - var14;
                        double var30 = var24 * var24 + var26 * var26 + var28 * var28;
                        if (!(var30 > var8)) {
                           int var32 = var19
                              ? RainbowUtil.rainbow(1.2, 0.85, (long)(var23.centerX * 4.0 + var23.centerY * 2.0 + var23.centerZ * 4.0), 255)
                              : this.getRenderColor(var23.config, var30, var23.seed);
                           RenderUtil.drawFilledBoxFaces(var23.box, RainbowUtil.withAlpha(var32, var16), var23.visibleFaces);
                           if (var17 && var18 > 0) {
                              RenderUtil.drawTracerToPos(var23.centerX, var23.centerY, var23.centerZ, RainbowUtil.withAlpha(var32, var18), var3);
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void buildBlockFilters() {
      ArrayList var1 = new ArrayList();
      HashSet var2 = new HashSet();

      for (class_2960 var4 : class_7923.field_41175.method_10235()) {
         byte[] var10000 = new byte[9];
         var10000[0] = (byte)(123);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(114);
         var10000[3] = (byte)(-69);
         var10000[4] = (byte)(-35);
         var10000[5] = (byte)(105);
         var10000[6] = (byte)(86);
         var10000[7] = (byte)(78);
         var10000[8] = (byte)(10);
         if ($$v_s(var10000, 14853333893500979L).equals(var4.method_12836())) {
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
      BlockESP.ColorTheme var13 = this.theme != null ? (BlockESP.ColorTheme)this.theme.get() : BlockESP.ColorTheme.WARM;

      for (class_2960 var15 : var1) {
         class_2248 var6 = (class_2248)class_7923.field_41175.method_63535(var15);
         if (var6 != null) {
            class_1799 var7 = resolveBlockIcon(var6, var15);
            if (var7 != null && !var7.method_7960()) {
               String var8 = sanitizeIdentifier(var15);
               BoolSetting var9 = (BoolSetting)this.addSetting((BoolSetting)hidden(new BoolSetting("BlockEnabled_" + var8, "Enable " + var15, false)));
               var9.onChanged(val -> {
                  this.recountEnabledBlocks();
                  this.dirtyFilter = true;
               });
               ColorSetting var10 = (ColorSetting)this.addSetting(
                  (ColorSetting)hidden(new ColorSetting("BlockColor_" + var8, "Color for " + var15, colorForTheme(var15, var13)))
               );
               var10.onChanged(val -> this.checkAndApplyCustomTheme(var15, val));
               String var11 = resolveDisplayName(var7, var15);
               this.blockOptions.add(new BlockESP.BlockOption(var15, var11, var7, (var11 + " " + var15).toLowerCase(Locale.ROOT)));
               BlockESP.BlockConfig var12 = new BlockESP.BlockConfig(var6, var15, var9, var10);
               this.configById.put(var15, var12);
               this.configByBlock.put(var6, var12);
            }
         }
      }

      this.recountEnabledBlocks();
   }

   private void recountEnabledBlocks() {
      int var1 = 0;

      for (BlockESP.BlockConfig var3 : this.configById.values()) {
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

   private int getRenderColor(BlockESP.BlockConfig config, double distSq, long offset) {
      return (Integer)config.color.get();
   }

   private boolean hasAnyEnabledBlocks() {
      return this.enabledBlockCount > 0;
   }

   private int getScanRadius() {
      return Math.max(1, (Integer)mc.field_1690.method_42503().method_41753());
   }

   private double getEffectiveRenderRange() {
      return this.getScanRadius() * 16.0;
   }

   private static int chessDistance(class_1923 first, class_1923 second) {
      return Math.max(Math.abs(first.field_9181 - second.field_9181), Math.abs(first.field_9180 - second.field_9180));
   }

   private void resetForWorldTransition() {
      this.scanGeneration++;
      this.scanRunning = false;
      this.scanQueue.clear();
      this.queuedChunks.clear();
      this.chunkCache.clear();
      this.cachedBlockCount = 0;
      this.rescanTimer = 0;
      this.lastPlayerChunkX = -2147483648;
      this.lastPlayerChunkZ = -2147483648;
   }

   private void fullRescan() {
      this.scanGeneration++;
      this.scanRunning = false;
      this.scanQueue.clear();
      this.queuedChunks.clear();
      this.chunkCache.clear();
      this.cachedBlockCount = 0;
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
         this.scanChunk(new class_1923(var2, var3 + 1));
      }
   }

   private void drainScanQueue() {
      if (!this.scanRunning && this.hasAnyEnabledBlocks()) {
         ArrayList var1 = new ArrayList(16);

         class_1923 var2;
         while (var1.size() < (16) && (var2 = (class_1923)this.scanQueue.poll()) != null) {
            this.queuedChunks.remove(var2);
            class_2818 var3 = mc.field_1687.method_2935().method_12126(var2.field_9181, var2.field_9180, false);
            if (var3 == null) {
               this.removeChunkCache(var2);
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
                  for (BlockESP.Snapshot var7 : var1) {
                     var5x.add(this.analyze(var7));
                  }
               } catch (Throwable var8) {
                  byte[] var10001 = new byte[44];
                  var10001[0] = (byte)(77);
                  var10001[1] = (byte)(-40);
                  var10001[2] = (byte)(112);
                  var10001[3] = (byte)(-79);
                  var10001[4] = (byte)(-35);
                  var10001[5] = (byte)(112);
                  var10001[6] = (byte)(114);
                  var10001[7] = (byte)(123);
                  var10001[8] = (byte)(46);
                  var10001[9] = (byte)(-1);
                  var10001[10] = (byte)(-60);
                  var10001[11] = (byte)(-80);
                  var10001[12] = (byte)(-121);
                  var10001[13] = (byte)(-86);
                  var10001[14] = (byte)(115);
                  var10001[15] = (byte)(53);
                  var10001[16] = (byte)(34);
                  var10001[17] = (byte)(106);
                  var10001[18] = (byte)(-72);
                  var10001[19] = (byte)(-63);
                  var10001[20] = (byte)(46);
                  var10001[21] = (byte)(-118);
                  var10001[22] = (byte)(9);
                  var10001[23] = (byte)(25);
                  var10001[24] = (byte)(-62);
                  var10001[25] = (byte)(107);
                  var10001[26] = (byte)(-18);
                  var10001[27] = (byte)(35);
                  var10001[28] = (byte)(22);
                  var10001[29] = (byte)(-16);
                  var10001[30] = (byte)(46);
                  var10001[31] = (byte)(-48);
                  var10001[32] = (byte)(-126);
                  var10001[33] = (byte)(79);
                  var10001[34] = (byte)(14);
                  var10001[35] = (byte)(27);
                  var10001[36] = (byte)(58);
                  var10001[37] = (byte)(-101);
                  var10001[38] = (byte)(-12);
                  var10001[39] = (byte)(-96);
                  var10001[40] = (byte)(-85);
                  var10001[41] = (byte)(-84);
                  var10001[42] = (byte)(47);
                  var10001[43] = (byte)(101);
                  VieClientMod.LOGGER.error($$v_s(var10001, 14853333893500979L), var8);
               }

               mc.execute(() -> {
                  try {
                     if (this.isEnabled() && mc.field_1687 == var5 && var6 == this.scanGeneration && this.hasAnyEnabledBlocks()) {
                        for (BlockESP.Scanned var6x : var5x) {
                           this.apply(var6x);
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

   private BlockESP.Snapshot capture(class_2818 chunk) {
      ArrayList var2 = new ArrayList();
      class_1923 var3 = chunk.method_12004();
      class_2826[] var4 = chunk.method_12006();
      int var5 = chunk.method_31607();
      int var6 = mc.field_1724.method_24515().method_10263();
      int var7 = mc.field_1724.method_24515().method_10264();
      int var8 = mc.field_1724.method_24515().method_10260();

      for (int var9 = 0; var9 < var4.length; var9++) {
         class_2826 var10 = var4[var9];
         if (var10 != null && !var10.method_38292() && this.sectionMayContainEnabledBlock(var10)) {
            int var11 = var5 + (var9 << (4));

            for (int var12 = 0; var12 < (16); var12++) {
               for (int var13 = 0; var13 < (16); var13++) {
                  for (int var14 = 0; var14 < (16); var14++) {
                     class_2680 var15 = var10.method_12254(var13, var12, var14);
                     BlockESP.BlockConfig var16 = (BlockESP.BlockConfig)this.configByBlock.get(var15.method_26204());
                     if (var16 != null && (Boolean)var16.enabled.get()) {
                        int var17 = var3.method_8326() + var13;
                        int var18 = var11 + var12;
                        int var19 = var3.method_8328() + var14;
                        int var20 = var17 - var6;
                        int var21 = var18 - var7;
                        int var22 = var19 - var8;
                        int var23 = this.visibleFaces(chunk, var17, var18, var19, var15.method_26204());
                        var2.add(new BlockESP.Hit(var20 * var20 + var21 * var21 + var22 * var22, var17, var18, var19, var16, var23));
                     }
                  }
               }
            }
         }
      }

      return new BlockESP.Snapshot(var3, List.copyOf(var2));
   }

   private BlockESP.Scanned analyze(BlockESP.Snapshot snapshot) {
      ArrayList var2 = new ArrayList(snapshot.hits());
      var2.sort(Comparator.comparingInt(BlockESP.Hit::distance));
      ArrayList var3 = new ArrayList(var2.size());

      for (BlockESP.Hit var5 : var2) {
         var3.add(
            new BlockESP.CachedBlock(
               new class_238(var5.x, var5.y, var5.z, var5.x + 1, var5.y + 1, var5.z + 1),
               var5.config,
               var5.x + 0.5,
               var5.y + 0.5,
               var5.z + 0.5,
               class_2338.method_10064(var5.x, var5.y, var5.z),
               var5.visibleFaces
            )
         );
      }

      return new BlockESP.Scanned(snapshot.position(), List.copyOf(var3));
   }

   private void apply(BlockESP.Scanned result) {
      List var2 = this.chunkCache.put(result.position(), result.blocks());
      this.cachedBlockCount = this.cachedBlockCount + (result.blocks().size() - (var2 == null ? 0 : var2.size()));
      if (result.blocks().isEmpty()) {
         this.chunkCache.remove(result.position());
      }
   }

   private int visibleFaces(class_2818 chunk, int x, int y, int z, class_2248 block) {
      int var6 = 63;
      if (this.sameBlock(chunk, x - 1, y, z, block)) {
         var6 &= -2;
      }

      if (this.sameBlock(chunk, x + 1, y, z, block)) {
         var6 &= -3;
      }

      if (this.sameBlock(chunk, x, y, z - 1, block)) {
         var6 &= -5;
      }

      if (this.sameBlock(chunk, x, y, z + 1, block)) {
         var6 &= -9;
      }

      if (this.sameBlock(chunk, x, y - 1, z, block)) {
         var6 &= -17;
      }

      if (this.sameBlock(chunk, x, y + 1, z, block)) {
         var6 &= -33;
      }

      return var6;
   }

   private boolean sameBlock(class_2818 chunk, int x, int y, int z, class_2248 block) {
      class_2818 var6 = chunk;
      if (x >> (4) != chunk.method_12004().field_9181 || z >> (4) != chunk.method_12004().field_9180) {
         var6 = mc.field_1687.method_2935().method_12126(x >> (4), z >> (4), false);
      }

      if (var6 == null) {
         return false;
      } else {
         int var7 = y - var6.method_31607() >> (4);
         class_2826[] var8 = var6.method_12006();
         if (var7 >= 0 && var7 < var8.length) {
            class_2826 var9 = var8[var7];
            return var9 != null && var9.method_12254(x & (15), y & (15), z & (15)).method_26204() == block;
         } else {
            return false;
         }
      }
   }

   private boolean sectionMayContainEnabledBlock(class_2826 section) {
      return section.method_19523(state -> {
         BlockESP.BlockConfig var2 = (BlockESP.BlockConfig)this.configByBlock.get(state.method_26204());
         return var2 != null && (Boolean)var2.enabled.get();
      });
   }

   private void cleanupOutOfRangeChunks(int pcx, int pcz, int radius) {
      ArrayList var4 = new ArrayList();

      for (class_1923 var6 : this.chunkCache.keySet()) {
         if (Math.abs(var6.field_9181 - pcx) > radius || Math.abs(var6.field_9180 - pcz) > radius) {
            var4.add(var6);
         }
      }

      for (class_1923 var8 : var4) {
         this.removeChunkCache(var8);
      }
   }

   private void removeChunkCache(class_1923 pos) {
      List var2 = (List)this.chunkCache.remove(pos);
      if (var2 != null) {
         this.cachedBlockCount = this.cachedBlockCount - var2.size();
      }
   }

   private static String resolveDisplayName(class_1799 icon, class_2960 id) {
      String var2 = icon != null && !icon.method_7960() ? icon.method_7964().getString() : null;
      return var2 != null && !var2.isBlank() ? var2 : prettifyPath(id.method_12832());
   }

   public void applyColorTheme(BlockESP.ColorTheme t) {
      if (t != BlockESP.ColorTheme.CUSTOM && t != BlockESP.ColorTheme.RAINBOW) {
         this.isApplyingTheme = true;

         try {
            for (BlockESP.BlockConfig var3 : this.configById.values()) {
               var3.color.set(colorForTheme(var3.id, t));
            }
         } finally {
            this.isApplyingTheme = false;
         }
      }
   }

   public boolean hasNonThemeColors(BlockESP.ColorTheme t) {
      if (t != null && t != BlockESP.ColorTheme.CUSTOM && t != BlockESP.ColorTheme.RAINBOW) {
         for (BlockESP.BlockConfig var3 : this.configById.values()) {
            if ((Integer)var3.color.get() != colorForTheme(var3.id, t)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void checkAndApplyCustomTheme(class_2960 id, int newColor) {
      if (!this.isApplyingTheme) {
         BlockESP.ColorTheme var3 = (BlockESP.ColorTheme)this.theme.get();
         if (var3 != BlockESP.ColorTheme.CUSTOM && var3 != BlockESP.ColorTheme.RAINBOW) {
            int var4 = colorForTheme(id, var3);
            if (newColor != var4) {
               this.theme.set(BlockESP.ColorTheme.CUSTOM);
               this.cachedTheme = BlockESP.ColorTheme.CUSTOM;
            }
         } else if (var3 == BlockESP.ColorTheme.RAINBOW) {
            this.theme.set(BlockESP.ColorTheme.CUSTOM);
            this.cachedTheme = BlockESP.ColorTheme.CUSTOM;
         }
      }
   }

   public static int colorForTheme(class_2960 id, BlockESP.ColorTheme theme) {
      if (themex == BlockESP.ColorTheme.CUSTOM) {
         return defaultColorFor(id);
      }

      if (themex == BlockESP.ColorTheme.RAINBOW) {
         int var8 = Math.abs(id.hashCode());
         return RainbowUtil.rainbow(1.2, 0.85, var8, 255);
      }

      int var2 = Math.abs(id.hashCode());
      float var3 = (var2 & (65535)) / 65535.0F;
      float var4 = 0.22F + (var2 >>> (8) & (63)) / 63.0F * 0.12F;
      float var5 = 0.96F + (var2 >>> (16) & (15)) / 15.0F * 0.04F;
      float var6;
      if (themex == BlockESP.ColorTheme.WARM) {
         float var7 = -15.0F + var3 * 70.0F;
         if (var7 < 0.0F) {
            var7 += 360.0F;
         }

         var6 = var7 / 360.0F;
      } else {
         float var9 = 165.0F + var3 * 110.0F;
         var6 = var9 / 360.0F;
      }

      return -16777216 | hsvToRgb(var6, var4, var5);
   }

   private static int defaultColorFor(class_2960 id) {
      return colorForTheme(id, BlockESP.ColorTheme.WARM);
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

   private static final class BlockConfig {
      final class_2248 block;
      final class_2960 id;
      final BoolSetting enabled;
      final ColorSetting color;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      BlockConfig(class_2248 block, class_2960 id, BoolSetting enabled, ColorSetting color) {
         this.block = blockx;
         this.id = idx;
         this.enabled = enabledx;
         this.color = colorx;
      }
   }

   public record BlockOption(class_2960 id, String name, class_1799 icon, String searchKey) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record CachedBlock(class_238 box, BlockESP.BlockConfig config, double centerX, double centerY, double centerZ, long seed, int visibleFaces) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum ColorMode {
      FILTER_COLOR,
      DISTANCE,
      RAINBOW;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static BlockESP.ColorMode[] $values() {
         BlockESP.ColorMode[] var10000 = new BlockESP.ColorMode[3];
         var10000[0] = FILTER_COLOR;
         var10000[1] = DISTANCE;
         var10000[2] = RAINBOW;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[12];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-35);
         var10002[5] = (byte)(103);
         var10002[6] = (byte)(76);
         var10002[7] = (byte)(-74);
         var10002[8] = (byte)(31);
         var10002[9] = (byte)(-18);
         var10002[10] = (byte)(-85);
         var10002[11] = (byte)(-92);
         FILTER_COLOR = new BlockESP.ColorMode();
         var10002 = new byte[8];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-39);
         var10002[5] = (byte)(123);
         var10002[6] = (byte)(80);
         var10002[7] = (byte)(-80);
         DISTANCE = new BlockESP.ColorMode();
         var10002 = new byte[7];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-38);
         var10002[5] = (byte)(122);
         var10002[6] = (byte)(68);
         RAINBOW = new BlockESP.ColorMode();
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

   public enum ColorTheme {
      WARM,
      COLD,
      RAINBOW,
      CUSTOM;

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
      private static BlockESP.ColorTheme[] $values() {
         BlockESP.ColorTheme[] var10000 = new BlockESP.ColorTheme[4];
         var10000[0] = WARM;
         var10000[1] = COLD;
         var10000[2] = RAINBOW;
         var10000[3] = CUSTOM;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(100);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-109);
         String var0 = $$v_s(var10002, 1811000599722053654L);
         byte[] var10004 = new byte[4];
         var10004[0] = (byte)(100);
         var10004[1] = (byte)(-5);
         var10004[2] = (byte)(110);
         var10004[3] = (byte)(-77);
         WARM = new BlockESP.ColorTheme($$v_s(var10004, 1811000599722053654L));
         var10002 = new byte[4];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-102);
         String var2 = $$v_s(var10002, 1811000599722053654L);
         var10004 = new byte[4];
         var10004[0] = (byte)(112);
         var10004[1] = (byte)(-11);
         var10004[2] = (byte)(112);
         var10004[3] = (byte)(-70);
         COLD = new BlockESP.ColorTheme($$v_s(var10004, 1811000599722053654L));
         var10002 = new byte[7];
         var10002[0] = (byte)(97);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-120);
         var10002[5] = (byte)(102);
         var10002[6] = (byte)(117);
         String var4 = $$v_s(var10002, 1811000599722053654L);
         int var10003 = 2;
         var10004 = new byte[7];
         var10004[0] = (byte)(97);
         var10004[1] = (byte)(-5);
         var10004[2] = (byte)(117);
         var10004[3] = (byte)(-80);
         var10004[4] = (byte)(-88);
         var10004[5] = (byte)(70);
         var10004[6] = (byte)(85);
         RAINBOW = new BlockESP.ColorTheme($$v_s(var10004, 1811000599722053654L));
         var10002 = new byte[6];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-123);
         var10002[5] = (byte)(100);
         String var6 = $$v_s(var10002, 1811000599722053654L);
         var10003 = 3;
         var10004 = new byte[6];
         var10004[0] = (byte)(112);
         var10004[1] = (byte)(-17);
         var10004[2] = (byte)(111);
         var10004[3] = (byte)(-86);
         var10004[4] = (byte)(-91);
         var10004[5] = (byte)(68);
         CUSTOM = new BlockESP.ColorTheme($$v_s(var10004, 1811000599722053654L));
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

   private record Hit(int distance, int x, int y, int z, BlockESP.BlockConfig config, int visibleFaces) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum RenderStyle {
      SOLID,
      OUTLINE,
      FILL_ONLY;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static BlockESP.RenderStyle[] $values() {
         BlockESP.RenderStyle[] var10000 = new BlockESP.RenderStyle[3];
         var10000[0] = SOLID;
         var10000[1] = OUTLINE;
         var10000[2] = FILL_ONLY;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(115);
         SOLID = new BlockESP.RenderStyle();
         var10002 = new byte[7];
         var10002[0] = (byte)(119);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(126);
         var10002[5] = (byte)(-25);
         var10002[6] = (byte)(-21);
         OUTLINE = new BlockESP.RenderStyle();
         var10002 = new byte[9];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(104);
         var10002[5] = (byte)(-26);
         var10002[6] = (byte)(-32);
         var10002[7] = (byte)(93);
         var10002[8] = (byte)(9);
         FILL_ONLY = new BlockESP.RenderStyle();
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

   private record Scanned(class_1923 position, List<BlockESP.CachedBlock> blocks) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record Snapshot(class_1923 position, List<BlockESP.Hit> hits) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
