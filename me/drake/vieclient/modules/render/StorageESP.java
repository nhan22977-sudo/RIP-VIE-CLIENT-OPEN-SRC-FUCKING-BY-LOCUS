package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import me.drake.vieclient.core.setting.ButtonSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.gui.StorageFilterScreen;
import me.drake.vieclient.util.ChunkScanCursor;
import me.drake.vieclient.util.PerformanceHelper;
import me.drake.vieclient.util.RainbowUtil;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.util.render.EspFade;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2586;
import net.minecraft.class_2589;
import net.minecraft.class_2595;
import net.minecraft.class_2601;
import net.minecraft.class_2608;
import net.minecraft.class_2611;
import net.minecraft.class_2614;
import net.minecraft.class_2627;
import net.minecraft.class_2636;
import net.minecraft.class_2818;
import net.minecraft.class_3719;
import net.minecraft.class_3720;
import net.minecraft.class_3723;
import net.minecraft.class_3866;

public class StorageESP extends Module {
   private static final int MAX_RANGE = 32;
   private static final StorageESP.StorageType[] STORAGE_TYPES = StorageESP.StorageType.values();
   private final ButtonSetting openFilter = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Open Filter",
         "Open block type filter",
         this::openFilterScreen
      )
   );
   private final EnumSetting colorTheme;
   private final BoolSetting tracers;
   private final IntSetting alpha;
   private final IntSetting maxRender;
   private final IntSetting rescanTicks;
   private final BoolSetting chestEnabled;
   private final BoolSetting barrelEnabled;
   private final BoolSetting shulkerEnabled;
   private final BoolSetting enderChestEnabled;
   private final BoolSetting spawnerEnabled;
   private final BoolSetting hopperEnabled;
   private final BoolSetting furnaceEnabled;
   private final BoolSetting blastFurnaceEnabled;
   private final BoolSetting smokerEnabled;
   private final BoolSetting dispenserEnabled;
   private final BoolSetting dropperEnabled;
   private final BoolSetting brewingEnabled;
   private final ColorSetting chestColor;
   private final ColorSetting barrelColor;
   private final ColorSetting shulkerColor;
   private final ColorSetting enderChestColor;
   private final ColorSetting spawnerColor;
   private final ColorSetting hopperColor;
   private final ColorSetting furnaceColor;
   private final ColorSetting blastFurnaceColor;
   private final ColorSetting smokerColor;
   private final ColorSetting dispenserColor;
   private final ColorSetting dropperColor;
   private final ColorSetting brewingColor;
   private final Map chunkCache;
   private int cachedStorageCount;
   private int rescanTimer;
   private final ChunkScanCursor scanCursor = new ChunkScanCursor();
   private final Set dirtyChunks = new LinkedHashSet();
   private class_1937 observedWorld;
   private volatile int cachedAlpha;
   private StorageESP.ColorTheme cachedTheme;
   private final int[] cachedFillColors = new int[STORAGE_TYPES.length];
   private final int[] cachedTracerColors = new int[STORAGE_TYPES.length];
   private boolean dirtyColors = true;
   private boolean isApplyingTheme = false;
   private int lastRenderAlpha = -1;
   private int lastTracerAlpha = -1;
   private boolean lastDrawTracers = false;
   private final EspFade espFade = new EspFade(this::finishDisable);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public StorageESP() {
      super(
         "StorageESP",
         "Highlights storage blocks",
         Category.RENDER
      );
      this.colorTheme = (EnumSetting)this.addSetting(
         new EnumSetting(
               "ColorTheme",
               "StorageESP color palette",
               StorageESP.ColorTheme.DEFAULT
            )
            .withDisplayName(StorageESP.ColorTheme.DEFAULT, "Default")
            .withDisplayName(StorageESP.ColorTheme.LAVENDER_MIST, "Lavender")
            .withDisplayName(StorageESP.ColorTheme.MATCHA_LATTE, "Matcha")
            .withDisplayName(StorageESP.ColorTheme.PEACH_SORBET, "Peach")
            .withDisplayName(StorageESP.ColorTheme.CLOUD_NINE, "Cloud")
            .withDisplayName(StorageESP.ColorTheme.DESERT_SAND, "Desert")
            .withDisplayName(StorageESP.ColorTheme.RAINBOW, "Rainbow")
            .withDisplayName(StorageESP.ColorTheme.CUSTOM, "Custom")
      );
      this.tracers = (BoolSetting)this.addSetting(
         new BoolSetting(
            "Tracers",
            "Draw tracer lines",
            false
         )
      );
      this.alpha = (IntSetting)this.addSetting(
         new IntSetting(
            "Alpha",
            "Fill opacity",
            125,
            5,
            255
         )
      );
      this.maxRender = (IntSetting)this.addSetting(
         (IntSetting)hidden(
            new IntSetting(
               "MaxRender",
               "Max rendered per frame",
               8192,
               50,
               65536
            )
         )
      );
      this.rescanTicks = (IntSetting)this.addSetting(
         (IntSetting)hidden(
            new IntSetting(
               "RescanTicks",
               "Ticks between rescans",
               8,
               1,
               40
            )
         )
      );
      this.chestEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "ChestEnabled",
               "Enable chest",
               true
            )
         )
      );
      this.barrelEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "BarrelEnabled",
               "Enable barrel",
               true
            )
         )
      );
      this.shulkerEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "ShulkerEnabled",
               "Enable shulker",
               true
            )
         )
      );
      this.enderChestEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "EnderChestEnabled",
               "Enable ender chest",
               true
            )
         )
      );
      this.spawnerEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "SpawnerEnabled",
               "Enable spawner",
               false
            )
         )
      );
      this.hopperEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "HopperEnabled",
               "Enable hopper",
               true
            )
         )
      );
      this.furnaceEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "FurnaceEnabled",
               "Enable furnace",
               true
            )
         )
      );
      this.blastFurnaceEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "BlastFurnaceEnabled",
               "Enable blast furnace",
               true
            )
         )
      );
      this.smokerEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "SmokerEnabled",
               "Enable smoker",
               true
            )
         )
      );
      this.dispenserEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "DispenserEnabled",
               "Enable dispenser",
               true
            )
         )
      );
      this.dropperEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "DropperEnabled",
               "Enable dropper",
               true
            )
         )
      );
      this.brewingEnabled = (BoolSetting)this.addSetting(
         (BoolSetting)hidden(
            new BoolSetting(
               "BrewingEnabled",
               "Enable brewing stand",
               true
            )
         )
      );
      this.chestColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "ChestColor",
               "Chest color",
               -33876
            )
         )
      );
      this.barrelColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "BarrelColor",
               "Barrel color",
               -23623
            )
         )
      );
      this.shulkerColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "ShulkerColor",
               "Shulker color",
               -2080517
            )
         )
      );
      this.enderChestColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "EnderChestColor",
               "Ender chest color",
               -5745161
            )
         )
      );
      this.spawnerColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "SpawnerColor",
               "Spawner color",
               -8635667
            )
         )
      );
      this.hopperColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "HopperColor",
               "Hopper color",
               -5793025
            )
         )
      );
      this.furnaceColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "FurnaceColor",
               "Furnace color",
               -4555871
            )
         )
      );
      this.blastFurnaceColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "BlastFurnaceColor",
               "Blast furnace color",
               -3885571
            )
         )
      );
      this.smokerColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "SmokerColor",
               "Smoker color",
               -298619
            )
         )
      );
      this.dispenserColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "DispenserColor",
               "Dispenser color",
               -3703297
            )
         )
      );
      this.dropperColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "DropperColor",
               "Dropper color",
               -2575106
            )
         )
      );
      this.brewingColor = (ColorSetting)this.addSetting(
         (ColorSetting)hidden(
            new ColorSetting(
               "BrewingColor",
               "Brewing stand color",
               -45676
            )
         )
      );

      for (StorageESP.StorageType var4 : STORAGE_TYPES) {
         this.colorSetting(var4).onChanged(val -> this.checkAndApplyCustomThemeIfNeeded(var4, val));
      }

      this.chunkCache = new ConcurrentHashMap();
      this.cachedStorageCount = 0;
      this.rescanTimer = 0;
      this.cachedAlpha = -1;
      this.cachedTheme = null;
   }

   private static Setting hidden(Setting setting) {
      setting.setVisibleInGui(false);
      return setting;
   }

   public boolean isTypeEnabled(StorageESP.StorageType type) {
      return (Boolean)this.enabledSetting(type).get();
   }

   public void setTypeEnabled(StorageESP.StorageType type, boolean enabled) {
      if ((Boolean)this.enabledSetting(type).get() != enabled) {
         this.enabledSetting(type).set(enabled);
         this.fullRescan();
      }
   }

   public int getTypeColor(StorageESP.StorageType type) {
      return (Integer)this.colorSetting(type).get();
   }

   public void setTypeColor(StorageESP.StorageType type, int rgb) {
      int var3 = -16777216 | rgb & (16777215);
      if ((Integer)this.colorSetting(type).get() != var3) {
         this.colorSetting(type).set(var3);
         this.checkAndApplyCustomThemeIfNeeded(type, var3);
      }
   }

   public void applyFilterBatch(boolean[] enabledStates, int[] colors) {
      StorageESP.StorageType[] var3 = StorageESP.StorageType.values();
      boolean var4 = false;

      for (int var5 = 0; var5 < var3.length && var5 < enabledStates.length; var5++) {
         if ((Boolean)this.enabledSetting(var3[var5]).get() != enabledStates[var5]) {
            this.enabledSetting(var3[var5]).set(enabledStates[var5]);
            var4 = true;
         }

         if (colors != null && var5 < colors.length) {
            int var6 = -16777216 | colors[var5] & (16777215);
            if ((Integer)this.colorSetting(var3[var5]).get() != var6) {
               this.colorSetting(var3[var5]).set(var6);
               this.checkAndApplyCustomThemeIfNeeded(var3[var5], var6);
            }
         }
      }

      if (var4) {
         this.fullRescan();
      }
   }

   public void openFilterScreen() {
      mc.method_1507(new StorageFilterScreen(this));
   }

   public boolean isRendering(class_2338 pos) {
      if (!this.isEnabled() || !this.isInGame()) {
         return false;
      }

      if (this.cachedAlpha <= 0) {
         return false;
      }

      class_1923 var2 = new class_1923(pos);
      List var3 = (List)this.chunkCache.get(var2);
      if (var3 != null && !var3.isEmpty()) {
         long var4 = pos.method_10063();

         for (StorageESP.CachedStorage var7 : var3) {
            if (var7.seed == var4) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void finishDisable() {
      super.onDisable();
      this.chunkCache.clear();
      this.cachedStorageCount = 0;
      this.dirtyChunks.clear();
      this.observedWorld = null;
      this.lastRenderAlpha = -1;
      this.lastTracerAlpha = -1;
      this.lastDrawTracers = false;
      this.dirtyColors = true;
      Arrays.fill(this.cachedFillColors, 0);
      Arrays.fill(this.cachedTracerColors, 0);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.lastRenderAlpha = -1;
      this.lastTracerAlpha = -1;
      this.lastDrawTracers = false;
      this.dirtyColors = true;
      Arrays.fill(this.cachedFillColors, 0);
      Arrays.fill(this.cachedTracerColors, 0);
      this.observedWorld = mc.field_1687;
      this.cachedAlpha = (Integer)this.alpha.get();
      StorageESP.ColorTheme var1 = (StorageESP.ColorTheme)this.colorTheme.get();
      if (var1 != StorageESP.ColorTheme.CUSTOM && this.hasNonThemeColors(var1)) {
         this.colorTheme.set(StorageESP.ColorTheme.CUSTOM);
         var1 = StorageESP.ColorTheme.CUSTOM;
      }

      this.applyColorTheme(var1);
      this.cachedTheme = var1;
      this.fullRescan();
      this.espFade.onEnable();
   }

   @Override
   public void onDisable() {
      this.espFade.onDisable();
   }

   @Override
   public String getInfo() {
      return this.cachedStorageCount + " | 32ch";
   }

   @Override
   public boolean usesEntityCache() {
      return false;
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent e) {
      if (this.isInGame()) {
         if (e.isLoaded()) {
            this.scanChunk(e.getChunkPos());
         } else {
            this.removeChunkCache(e.getChunkPos());
            this.dirtyChunks.remove(e.getChunkPos());
         }
      }
   }

   @EventHandler
   public void onBlockUpdate(ServerBlockUpdateEvent event) {
      if (this.isInGame()) {
         this.dirtyChunks.add(new class_1923(event.getPos()));
      }
   }

   @EventHandler
   public void onTick(TickEvent e) {
      if (e.getPhase() == TickEvent.Phase.PRE) {
         this.espFade.update();
         if (this.isEnabled()) {
            if (mc.field_1687 != this.observedWorld) {
               this.observedWorld = mc.field_1687;
               this.fullRescan();
            }

            if (e.getPhase() == TickEvent.Phase.PRE && this.isInGame()) {
               int var2 = (Integer)this.alpha.get();
               if (this.cachedAlpha != var2) {
                  this.cachedAlpha = var2;
               }

               StorageESP.ColorTheme var3 = (StorageESP.ColorTheme)this.colorTheme.get();
               if (var3 != this.cachedTheme) {
                  this.cachedTheme = var3;
                  this.applyColorTheme(var3);
               }

               Iterator var4 = this.dirtyChunks.iterator();

               for (int var5 = 12; var5 > 0 && var4.hasNext(); var5--) {
                  class_1923 var6 = (class_1923)var4.next();
                  var4.remove();
                  this.scanChunk(var6);
               }

               int var7 = PerformanceHelper.scaleInterval((Integer)this.rescanTicks.get());
               if (++this.rescanTimer >= var7) {
                  this.rescanTimer = 0;
                  this.rescanNearby();
                  this.cleanupOldChunks();
               }
            }
         }
      }
   }

   private void cleanupOldChunks() {
      if (mc.field_1724 != null) {
         int var1 = 34;
         int var2 = mc.field_1724.method_31476().field_9181;
         int var3 = mc.field_1724.method_31476().field_9180;
         this.cleanupOutOfRangeChunks(var2, var3, var1);
      }
   }

   @EventHandler(priority = 100)
   public void onRender(RenderEvent e) {
      if (e.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame()) {
         float var2 = this.espFade.getFade();
         if (var2 <= 0.001F) {
            return;
         }

         int var3 = (int)(this.cachedAlpha * var2);
         boolean var4 = (Boolean)this.tracers.get();
         int var5 = (int)(255.0F * var2);
         if (var3 <= 0 && (!var4 || var5 <= 0)) {
            return;
         }

         if (var3 > 0 || var4 && var5 > 0) {
            float var6 = e.getTickDelta();
            double var7 = mc.field_1724.method_23317();
            double var9 = mc.field_1724.method_23318();
            double var11 = mc.field_1724.method_23321();
            int var13 = PerformanceHelper.scaleRenderCap((Integer)this.maxRender.get());
            int var14 = 0;
            boolean var15 = this.colorTheme.get() == StorageESP.ColorTheme.RAINBOW;
            if (!var15 && (this.dirtyColors || var3 != this.lastRenderAlpha || var4 != this.lastDrawTracers || var5 != this.lastTracerAlpha)) {
               this.lastRenderAlpha = var3;
               this.lastTracerAlpha = var5;
               this.lastDrawTracers = var4;
               this.dirtyColors = false;

               for (StorageESP.StorageType var19 : STORAGE_TYPES) {
                  int var20 = (Integer)this.colorSetting(var19).get();
                  this.cachedFillColors[var19.ordinal()] = RainbowUtil.withAlpha(var20, var3);
                  if (var4) {
                     this.cachedTracerColors[var19.ordinal()] = RainbowUtil.withAlpha(var20, var5);
                  }
               }
            }

            for (List var30 : this.chunkCache.values()) {
               for (StorageESP.CachedStorage var32 : var30) {
                  if (var14 >= var13) {
                     return;
                  }

                  double var33 = var32.centerX - var7;
                  double var22 = var32.centerZ - var11;
                  double var24 = var33 * var33 + var22 * var22;
                  if (!(var24 > var32.maxDistSq)) {
                     int var26 = var32.type.ordinal();
                     int var28 = 0;
                     int var27;
                     if (var15) {
                        var27 = RainbowUtil.rainbow(1.2, 0.85, (long)(var32.centerX * 4.0 + var32.centerY * 2.0 + var32.centerZ * 4.0), var3);
                        if (var4 && var5 > 0) {
                           var28 = RainbowUtil.withAlpha(var27, var5);
                        }
                     } else {
                        var27 = this.cachedFillColors[var26];
                        if (var4 && var5 > 0) {
                           var28 = this.cachedTracerColors[var26];
                        }
                     }

                     if (var3 > 0) {
                        RenderUtil.drawFilledBox(var32.coreBox, var27);
                     }

                     if (var4 && var5 > 0) {
                        RenderUtil.drawTracerToPos(var32.centerX, var32.centerY, var32.centerZ, var28, var6);
                     }

                     var14++;
                  }
               }
            }
         }
      }
   }

   private BoolSetting enabledSetting(StorageESP.StorageType type) {
      return switch (type) {
         case CHEST -> this.chestEnabled;
         case BARREL -> this.barrelEnabled;
         case SHULKER -> this.shulkerEnabled;
         case ENDER_CHEST -> this.enderChestEnabled;
         case SPAWNER -> this.spawnerEnabled;
         case HOPPER -> this.hopperEnabled;
         case FURNACE -> this.furnaceEnabled;
         case BLAST_FURNACE -> this.blastFurnaceEnabled;
         case SMOKER -> this.smokerEnabled;
         case DISPENSER -> this.dispenserEnabled;
         case DROPPER -> this.dropperEnabled;
         case BREWING_STAND -> this.brewingEnabled;
      };
   }

   private ColorSetting colorSetting(StorageESP.StorageType type) {
      return switch (type) {
         case CHEST -> this.chestColor;
         case BARREL -> this.barrelColor;
         case SHULKER -> this.shulkerColor;
         case ENDER_CHEST -> this.enderChestColor;
         case SPAWNER -> this.spawnerColor;
         case HOPPER -> this.hopperColor;
         case FURNACE -> this.furnaceColor;
         case BLAST_FURNACE -> this.blastFurnaceColor;
         case SMOKER -> this.smokerColor;
         case DISPENSER -> this.dispenserColor;
         case DROPPER -> this.dropperColor;
         case BREWING_STAND -> this.brewingColor;
      };
   }

   public static int getThemeColor(StorageESP.ColorTheme theme, StorageESP.StorageType type) {
      if (theme != null && theme != StorageESP.ColorTheme.CUSTOM) {
         return switch (theme) {
            case DEFAULT -> {
               switch (type) {
                  case CHEST:
                     yield -33876;
                  case BARREL:
                     yield -23623;
                  case SHULKER:
                     yield -2080517;
                  case ENDER_CHEST:
                     yield -5745161;
                  case SPAWNER:
                     yield -8635667;
                  case HOPPER:
                     yield -5793025;
                  case FURNACE:
                     yield -4555871;
                  case BLAST_FURNACE:
                     yield -3885571;
                  case SMOKER:
                     yield -298619;
                  case DISPENSER:
                     yield -3703297;
                  case DROPPER:
                     yield -2575106;
                  case BREWING_STAND:
                     yield -45676;
                  default:
                     throw new MatchException(null, null);
               }
            }
            case LAVENDER_MIST -> {
               switch (type) {
                  case CHEST:
                     yield -3558170;
                  case BARREL:
                     yield -2109456;
                  case SHULKER:
                     yield -5204528;
                  case ENDER_CHEST:
                     yield -1516043;
                  case SPAWNER:
                     yield -5928252;
                  case HOPPER:
                     yield -7496267;
                  case FURNACE:
                     yield -8687985;
                  case BLAST_FURNACE:
                     yield -8687985;
                  case SMOKER:
                     yield -2831389;
                  case DISPENSER:
                     yield -6387784;
                  case DROPPER:
                     yield -6387784;
                  case BREWING_STAND:
                     yield -1648398;
                  default:
                     throw new MatchException(null, null);
               }
            }
            case MATCHA_LATTE -> {
               switch (type) {
                  case CHEST:
                     yield -5712454;
                  case BARREL:
                     yield -3610928;
                  case SHULKER:
                     yield -7618658;
                  case ENDER_CHEST:
                     yield -2428704;
                  case SPAWNER:
                     yield -8737140;
                  case HOPPER:
                     yield -1515856;
                  case FURNACE:
                     yield -9463454;
                  case BLAST_FURNACE:
                     yield -9463454;
                  case SMOKER:
                     yield -4267836;
                  case DISPENSER:
                     yield -7356518;
                  case DROPPER:
                     yield -7356518;
                  case BREWING_STAND:
                     yield -2035744;
                  default:
                     throw new MatchException(null, null);
               }
            }
            case PEACH_SORBET -> {
               switch (type) {
                  case CHEST:
                     yield -670544;
                  case BARREL:
                     yield -336696;
                  case SHULKER:
                     yield -1529712;
                  case ENDER_CHEST:
                     yield -135968;
                  case SPAWNER:
                     yield -2714758;
                  case HOPPER:
                     yield -1187627;
                  case FURNACE:
                     yield -4030870;
                  case BLAST_FURNACE:
                     yield -4030870;
                  case SMOKER:
                     yield -993592;
                  case DISPENSER:
                     yield -1527910;
                  case DROPPER:
                     yield -1527910;
                  case BREWING_STAND:
                     yield -466216;
                  default:
                     throw new MatchException(null, null);
               }
            }
            case CLOUD_NINE -> {
               switch (type) {
                  case CHEST:
                     yield -1906448;
                  case BARREL:
                     yield -1117449;
                  case SHULKER:
                     yield -3286808;
                  case ENDER_CHEST:
                     yield -985862;
                  case SPAWNER:
                     yield -4536102;
                  case HOPPER:
                     yield -4664088;
                  case FURNACE:
                     yield -6244916;
                  case BLAST_FURNACE:
                     yield -6244916;
                  case SMOKER:
                     yield -2431760;
                  case DISPENSER:
                     yield -3812123;
                  case DROPPER:
                     yield -3812123;
                  case BREWING_STAND:
                     yield -1511691;
                  default:
                     throw new MatchException(null, null);
               }
            }
            case DESERT_SAND -> {
               switch (type) {
                  case CHEST:
                     yield -2569816;
                  case BARREL:
                     yield -1779521;
                  case SHULKER:
                     yield -4017524;
                  case ENDER_CHEST:
                     yield -1055019;
                  case SPAWNER:
                     yield -5399432;
                  case HOPPER:
                     yield -4412786;
                  case FURNACE:
                     yield -6648976;
                  case BLAST_FURNACE:
                     yield -6648976;
                  case SMOKER:
                     yield -3226966;
                  case DISPENSER:
                     yield -4280690;
                  case DROPPER:
                     yield -4280690;
                  case BREWING_STAND:
                     yield -1712952;
                  default:
                     throw new MatchException(null, null);
               }
            }
            case RAINBOW -> RainbowUtil.rainbow(1.2, 0.85, type.ordinal() * 300L, 255);
            case CUSTOM -> -33876;
         };
      } else {
         return -33876;
      }
   }

   public boolean hasNonThemeColors(StorageESP.ColorTheme theme) {
      if (theme != null && theme != StorageESP.ColorTheme.CUSTOM && theme != StorageESP.ColorTheme.RAINBOW) {
         for (StorageESP.StorageType var5 : STORAGE_TYPES) {
            if ((Integer)this.colorSetting(var5).get() != getThemeColor(theme, var5)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private void checkAndApplyCustomThemeIfNeeded(StorageESP.StorageType type, int newColor) {
      if (!this.isApplyingTheme) {
         StorageESP.ColorTheme var3 = (StorageESP.ColorTheme)this.colorTheme.get();
         if (var3 != StorageESP.ColorTheme.CUSTOM && var3 != StorageESP.ColorTheme.RAINBOW) {
            int var4 = getThemeColor(var3, type);
            if (newColor != var4) {
               this.colorTheme.set(StorageESP.ColorTheme.CUSTOM);
               this.cachedTheme = StorageESP.ColorTheme.CUSTOM;
            }
         } else if (var3 == StorageESP.ColorTheme.RAINBOW) {
            this.colorTheme.set(StorageESP.ColorTheme.CUSTOM);
            this.cachedTheme = StorageESP.ColorTheme.CUSTOM;
         }
      }
   }

   public EnumSetting getColorThemeSetting() {
      return this.colorTheme;
   }

   public StorageESP.ColorTheme getColorTheme() {
      return (StorageESP.ColorTheme)this.colorTheme.get();
   }

   private void applyColorTheme(StorageESP.ColorTheme theme) {
      if (theme != StorageESP.ColorTheme.CUSTOM && theme != StorageESP.ColorTheme.RAINBOW) {
         this.isApplyingTheme = true;

         try {
            for (StorageESP.StorageType var5 : STORAGE_TYPES) {
               this.colorSetting(var5).set(getThemeColor(theme, var5));
            }
         } finally {
            this.isApplyingTheme = false;
         }

         this.dirtyColors = true;
      } else {
         this.dirtyColors = true;
      }
   }

   private void rescanNearby() {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         int var1 = mc.field_1724.method_31476().field_9181;
         int var2 = mc.field_1724.method_31476().field_9180;
         int var3 = PerformanceHelper.scaleBudget(12);
         this.scanChunk(mc.field_1724.method_31476());

         for (int var4 = 0; var4 < Math.max(1, var3 - 1); var4++) {
            this.scanChunk(new class_1923(this.scanCursor.next(var1, var2, 32)));
         }
      }
   }

   private void fullRescan() {
      this.chunkCache.clear();
      this.cachedStorageCount = 0;
      this.scanCursor.reset();
      this.dirtyChunks.clear();
      if (mc.field_1687 != null && mc.field_1724 != null) {
         int var1 = 32;
         int var2 = mc.field_1724.method_31476().field_9181;
         int var3 = mc.field_1724.method_31476().field_9180;

         for (int var4 = var2 - var1; var4 <= var2 + var1; var4++) {
            for (int var5 = var3 - var1; var5 <= var3 + var1; var5++) {
               this.scanChunk(new class_1923(var4, var5));
            }
         }
      }
   }

   private void scanChunk(class_1923 pos) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         int var2 = 33;
         int var3 = mc.field_1724.method_31476().field_9181;
         int var4 = mc.field_1724.method_31476().field_9180;
         if (Math.abs(pos.field_9181 - var3) <= var2 && Math.abs(pos.field_9180 - var4) <= var2) {
            class_2818 var5 = mc.field_1687.method_2935().method_12126(pos.field_9181, pos.field_9180, false);
            if (var5 == null) {
               this.removeChunkCache(pos);
            } else {
               ArrayList var6 = new ArrayList();

               for (Entry var8 : var5.method_12214().entrySet()) {
                  StorageESP.StorageType var9 = this.getType((class_2586)var8.getValue());
                  if (var9 != null && this.isTypeEnabled(var9)) {
                     class_2338 var10 = (class_2338)var8.getKey();
                     class_238 var11 = new class_238(
                        var10.method_10263() + 0.1,
                        var10.method_10264() + 0.05,
                        var10.method_10260() + 0.1,
                        var10.method_10263() + 0.9,
                        var10.method_10264() + 0.85,
                        var10.method_10260() + 0.9
                     );
                     double var12 = var10.method_10264() + 0.5;
                     int var14 = (Integer)this.colorSetting(var9).get();
                     double var15 = 528.0;
                     double var17 = var15 * var15;
                     var6.add(
                        new StorageESP.CachedStorage(var11, var9, var10.method_10263() + 0.5, var12, var10.method_10260() + 0.5, var17, var10.method_10063())
                     );
                  }
               }

               List var19 = (List)this.chunkCache.get(pos);
               int var20 = var19 == null ? 0 : var19.size();
               int var21 = var6.size();
               this.cachedStorageCount += var21 - var20;
               if (var21 == 0) {
                  this.chunkCache.remove(pos);
               } else {
                  this.chunkCache.put(pos, var6);
               }
            }
         } else {
            this.removeChunkCache(pos);
         }
      }
   }

   private void cleanupOutOfRangeChunks(int pcx, int pcz, int radius) {
      this.chunkCache.entrySet().removeIf(entry -> {
         class_1923 var5 = (class_1923)entry.getKey();
         if (Math.abs(var5.field_9181 - pcx) <= radius && Math.abs(var5.field_9180 - pcz) <= radius) {
            return false;
         }

         List var6 = (List)entry.getValue();
         if (var6 != null) {
            this.cachedStorageCount = this.cachedStorageCount - var6.size();
         }

         return true;
      });
   }

   private void removeChunkCache(class_1923 pos) {
      List var2 = (List)this.chunkCache.remove(pos);
      if (var2 != null) {
         this.cachedStorageCount = this.cachedStorageCount - var2.size();
      }
   }

   private StorageESP.StorageType getType(class_2586 be) {
      Objects.requireNonNull(be);
      if (be instanceof class_2595) {
         return StorageESP.StorageType.CHEST;
      } else if (be instanceof class_3719) {
         return StorageESP.StorageType.BARREL;
      } else if (be instanceof class_2627) {
         return StorageESP.StorageType.SHULKER;
      } else if (be instanceof class_2611) {
         return StorageESP.StorageType.ENDER_CHEST;
      } else if (be instanceof class_2636) {
         return StorageESP.StorageType.SPAWNER;
      } else if (be instanceof class_2614) {
         return StorageESP.StorageType.HOPPER;
      } else if (be instanceof class_2608) {
         return StorageESP.StorageType.DROPPER;
      } else if (be instanceof class_2601) {
         return StorageESP.StorageType.DISPENSER;
      } else if (be instanceof class_3720) {
         return StorageESP.StorageType.BLAST_FURNACE;
      } else if (be instanceof class_3723) {
         return StorageESP.StorageType.SMOKER;
      } else if (be instanceof class_3866) {
         return StorageESP.StorageType.FURNACE;
      } else {
         return be instanceof class_2589 ? StorageESP.StorageType.BREWING_STAND : null;
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

   private record CachedStorage(class_238 coreBox, StorageESP.StorageType type, double centerX, double centerY, double centerZ, double maxDistSq, long seed) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum ColorTheme {
      DEFAULT,
      LAVENDER_MIST,
      MATCHA_LATTE,
      PEACH_SORBET,
      CLOUD_NINE,
      DESERT_SAND,
      RAINBOW,
      CUSTOM;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private static StorageESP.ColorTheme[] $values() {
         StorageESP.ColorTheme[] var10000 = new StorageESP.ColorTheme[8];
         var10000[0] = DEFAULT;
         var10000[1] = LAVENDER_MIST;
         var10000[2] = MATCHA_LATTE;
         var10000[3] = PEACH_SORBET;
         var10000[4] = CLOUD_NINE;
         var10000[5] = DESERT_SAND;
         var10000[6] = RAINBOW;
         var10000[7] = CUSTOM;
         return var10000;
      }

      // $VF: synthetic method
      private static StorageESP.ColorTheme[] $values$() {
         StorageESP.ColorTheme[] var10000 = new StorageESP.ColorTheme[8];
         var10000[0] = DEFAULT;
         var10000[1] = LAVENDER_MIST;
         var10000[2] = MATCHA_LATTE;
         var10000[3] = PEACH_SORBET;
         var10000[4] = CLOUD_NINE;
         var10000[5] = DESERT_SAND;
         var10000[6] = RAINBOW;
         var10000[7] = CUSTOM;
         return var10000;
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

   public enum StorageType {
      CHEST,
      BARREL,
      SHULKER,
      ENDER_CHEST,
      SPAWNER,
      HOPPER,
      FURNACE,
      BLAST_FURNACE,
      SMOKER,
      DISPENSER,
      DROPPER,
      BREWING_STAND;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      private static StorageESP.StorageType[] $values() {
         StorageESP.StorageType[] var10000 = new StorageESP.StorageType[12];
         var10000[0] = CHEST;
         var10000[1] = BARREL;
         var10000[2] = SHULKER;
         var10000[3] = ENDER_CHEST;
         var10000[4] = SPAWNER;
         var10000[5] = HOPPER;
         var10000[6] = FURNACE;
         var10000[7] = BLAST_FURNACE;
         var10000[8] = SMOKER;
         var10000[9] = DISPENSER;
         var10000[10] = DROPPER;
         var10000[11] = BREWING_STAND;
         return var10000;
      }

      // $VF: synthetic method
      private static StorageESP.StorageType[] $values$() {
         StorageESP.StorageType[] var10000 = new StorageESP.StorageType[12];
         var10000[0] = CHEST;
         var10000[1] = BARREL;
         var10000[2] = SHULKER;
         var10000[3] = ENDER_CHEST;
         var10000[4] = SPAWNER;
         var10000[5] = HOPPER;
         var10000[6] = FURNACE;
         var10000[7] = BLAST_FURNACE;
         var10000[8] = SMOKER;
         var10000[9] = DISPENSER;
         var10000[10] = DROPPER;
         var10000[11] = BREWING_STAND;
         return var10000;
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
}
