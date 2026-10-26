package me.drake.vieclient.modules.donut;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.ServerBlockUpdateEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.render.fx.WorldProjector;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.SpawnerEntityCache;
import net.minecraft.class_1297;
import net.minecraft.class_1308;
import net.minecraft.class_1917;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_1952;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2586;
import net.minecraft.class_2636;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_2826;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_6010;
import net.minecraft.class_6012;
import net.minecraft.class_631;
import net.minecraft.class_7923;

public final class SpawnerTags extends Module {
   private final BoolSetting persistent = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Persistent",
         "Keep tracked spawners in memory even when chunks unload",
         false
      )
   );
   private final BoolSetting offscreen = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Offscreen",
         "Show directional indicators for off-screen spawners",
         true
      )
   );
   private final DoubleSetting arrowDistance = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Arrow Distance",
         "Khoảng cách của mũi tên chỉ hướng so với tâm ngắm (crosshair)",
         95.0,
         20.0,
         350.0,
         5.0
      )
   );
   private final DoubleSetting maxDistance = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "MaxDistance",
         "Maximum spawner tracking distance in blocks",
         2048.0,
         64.0,
         2048.0,
         32.0
      )
   );
   private final IntSetting scanRadius = (IntSetting)this.addSetting(
      new IntSetting(
         "ScanRadius",
         "Chunk scan radius around player (chunks)",
         32,
         8,
         32
      )
   );
   private final ColorSetting textColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "TextColor",
         "Label text color",
         -1
      )
   );
   private final ColorSetting backgroundColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "BackgroundColor",
         "Label background color",
         2013265920
      )
   );
   private final DoubleSetting scale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Scale",
         "Text size multiplier",
         4.0,
         0.35,
         4.0
      )
   );
   private final DoubleSetting yOffset = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "YOffset",
         "Vertical offset above the spawner (blocks)",
         1.0,
         0.0,
         2.0
      )
   );
   private final IntSetting chunksPerTick = (IntSetting)this.addSetting(
      new IntSetting(
         "ChunksPerTick",
         "Chunks scanned per tick (higher = faster scan, more lag)",
         48,
         8,
         256
      )
   );
   private final BoolSetting showEmpty = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ShowEmpty",
         "Show \"?\" tag when the entity type is still unknown",
         true
      )
   );
   private final Map resolvedIds = new ConcurrentHashMap();
   private final LinkedHashMap pendingChunks = new LinkedHashMap();
   private final Map scannedChunks = new ConcurrentHashMap();
   private final WorldProjector projector = new WorldProjector();
   private final float[] scratchScreen = new float[3];
   private final BiConsumer cacheListener = this::onCacheUpdated;
   private class_1937 observedWorld;
   private final List tagPool = new ArrayList();
   private final List activeTags = new ArrayList();
   private final List offscreenPool = new ArrayList();
   private final List activeOffscreen = new ArrayList();
   private static final Map MOB_INFO_CACHE = new ConcurrentHashMap();
   private static final Field SPAWN_ENTRY_FIELD;
   private static final Field SPAWN_POTENTIALS_FIELD;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static SpawnerTags.MobDisplayInfo getMobInfo(String rawId) {
      return rawId != null && !rawId.isEmpty() && !rawId.equals("?")
         ? MOB_INFO_CACHE.computeIfAbsent(rawId, SpawnerTags::createMobInfo)
         : MOB_INFO_CACHE.computeIfAbsent("?", k -> {
            byte[] var10002 = new byte[7];
            var10002[0] = (byte)(15);
            var10002[1] = (byte)(-22);
            var10002[2] = (byte)(125);
            var10002[3] = (byte)(-87);
            var10002[4] = (byte)(10);
            var10002[5] = (byte)(-38);
            var10002[6] = (byte)(-2);
            return new SpawnerTags.MobDisplayInfo($$v_s(var10002, 760934465113673849L), -680437, false);
         });
   }

   private static SpawnerTags.MobDisplayInfo createMobInfo(String rawId) {
      String var1 = rawId.toLowerCase(Locale.ROOT);
      String var2 = formatTitleCase(rawId);

      int var3 = switch (var1) {
         case "blaze" -> -22016;
         case "skeleton" -> -3418655;
         case "wither_skeleton" -> -10193781;
         case "stray" -> -7092739;
         case "bogged" -> -8074218;
         case "zombie" -> -15681151;
         case "drowned" -> -16337196;
         case "husk" -> -680437;
         case "zombie_villager" -> -16411031;
         case "spider" -> -770210;
         case "cave_spider" -> -2024120;
         case "creeper" -> -14498466;
         case "enderman" -> -5745161;
         case "slime" -> -8074218;
         case "magma_cube" -> -429290;
         case "silverfish" -> -9967367;
         case "witch" -> -4184365;
         case "phantom" -> -10443270;
         case "guardian" -> -15419226;
         case "elder_guardian" -> -6687004;
         case "pig" -> -757066;
         case "cow" -> -2525434;
         case "sheep" -> -920071;
         case "chicken" -> -69494;
         case "iron_golem" -> -1906448;
         case "villager" -> -15681151;
         default -> -13058568;
      };
      return new SpawnerTags.MobDisplayInfo(var2, var3, true);
   }

   public static String formatTitleCase(String raw) {
      if (raw != null && !raw.isEmpty()) {
         String[] var1 = raw.replace((char)(45), (char)(95)).split("_");
         StringBuilder var2 = new StringBuilder();

         for (String var6 : var1) {
            if (!var6.isEmpty()) {
               if (var2.length() > 0) {
                  var2.append((char)(32));
               }

               var2.append(Character.toUpperCase(var6.charAt(0)));
               if (var6.length() > 1) {
                  var2.append(var6.substring(1).toLowerCase(Locale.ROOT));
               }
            }
         }

         return var2.length() > 0 ? var2.toString() : raw;
      } else {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(15);
         var10000[1] = (byte)(-22);
         var10000[2] = (byte)(125);
         var10000[3] = (byte)(-87);
         var10000[4] = (byte)(10);
         var10000[5] = (byte)(-38);
         var10000[6] = (byte)(-2);
         return $$v_s(var10000, 760934465113673849L);
      }
   }

   public SpawnerTags() {
      super(
         "SpawnerTags",
         "Label every nearby mob spawner with its spawn entry",
         Category.BASE_FINDING
      );
      this.arrowDistance.visibleWhen(this.offscreen::get);
      this.maxDistance.setVisibleInGui(false);
      this.scanRadius.setVisibleInGui(false);
      this.textColor.setVisibleInGui(false);
      this.backgroundColor.setVisibleInGui(false);
      this.scale.setVisibleInGui(false);
      this.yOffset.setVisibleInGui(false);
      this.chunksPerTick.setVisibleInGui(false);
      this.showEmpty.setVisibleInGui(false);
   }

   public DoubleSetting getArrowDistanceSetting() {
      return this.arrowDistance;
   }

   @Override
   public String getInfo() {
      int var1 = this.resolvedIds.size();
      return var1 > 0 ? String.valueOf(var1) : null;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      VieClientMod.getInstance().getEventBus().subscribe(this);
      SpawnerEntityCache.addListener(this.cacheListener);
      this.resolvedIds.clear();
      this.pendingChunks.clear();
      this.scannedChunks.clear();
      this.observedWorld = mc.field_1687;
      if (this.isInGame() && mc.field_1724 != null && mc.field_1687 != null) {
         this.enqueueNearbyChunks();
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      VieClientMod.getInstance().getEventBus().unsubscribe(this);
      SpawnerEntityCache.removeListener(this.cacheListener);
      this.resolvedIds.clear();
      this.pendingChunks.clear();
      this.scannedChunks.clear();
      this.activeTags.clear();
      this.activeOffscreen.clear();
      this.observedWorld = null;
   }

   private void onCacheUpdated(class_2338 pos, String entityId) {
      if (pos != null && entityId != null) {
         long var3 = pos.method_10063();
         String var5 = (String)this.resolvedIds.get(var3);
         if (!entityId.isEmpty()) {
            if (var5 == null || var5.isEmpty() || var5.equals("?")) {
               this.resolvedIds.put(var3, stripNamespace(entityId));
            }
         } else {
            this.resolvedIds.putIfAbsent(var3, "");
         }
      }
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent event) {
      if (this.isInGame() && this.isEnabled() && mc.field_1687 != null) {
         class_1923 var2 = event.getChunkPos();
         if (event.isUnloaded()) {
            this.scannedChunks.remove(var2.method_8324());
            if (!(Boolean)this.persistent.get()) {
               long var7 = var2.field_9181;
               long var5 = var2.field_9180;
               this.resolvedIds
                  .keySet()
                  .removeIf(k -> class_2338.method_10061(k) >> (4) == var7 && class_2338.method_10083(k) >> (4) == var5);
            }
         } else {
            class_2818 var3 = event.getChunk();
            if (var3 == null) {
               var3 = mc.field_1687.method_2935().method_12126(var2.field_9181, var2.field_9180, false);
            }

            if (var3 != null) {
               this.scanChunk(var3);
               this.scannedChunks.put(var2.method_8324(), Boolean.TRUE);
            }
         }
      }
   }

   @EventHandler
   public void onBlockUpdate(ServerBlockUpdateEvent event) {
      if (this.isInGame() && this.isEnabled() && mc.field_1687 != null) {
         class_2338 var2 = event.getPos();
         class_2680 var3 = event.getState();
         if (var3.method_27852(class_2246.field_10260)) {
            class_2586 var4 = mc.field_1687.method_8321(var2);
            this.trackSpawner(var2, var4);
         } else {
            this.resolvedIds.remove(var2.method_10063());
            SpawnerEntityCache.remove(var2);
         }
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.POST) {
         if (this.isInGame() && this.isEnabled() && mc.field_1724 != null && mc.field_1687 != null) {
            if (mc.field_1687 != this.observedWorld) {
               this.observedWorld = mc.field_1687;
               this.resolvedIds.clear();
               this.pendingChunks.clear();
               this.scannedChunks.clear();
               this.enqueueNearbyChunks();
            } else {
               for (Entry var3 : this.resolvedIds.entrySet()) {
                  if (var3.getValue() == null || ((String)var3.getValue()).isEmpty() || ((String)var3.getValue()).equals("?")) {
                     long var4 = (Long)var3.getKey();
                     class_2338 var6 = class_2338.method_10092(var4);
                     String var7 = this.resolveMobId(var6, null);
                     if (var7 != null && !var7.isEmpty() && !var7.equals("?")) {
                        var3.setValue(var7);
                        SpawnerEntityCache.put(var6, var7);
                     }
                  }
               }

               int var11 = (Integer)this.chunksPerTick.get();
               if (this.pendingChunks.isEmpty()) {
                  this.enqueueNearbyChunks();
               }

               int var12 = 0;

               for (Iterator var13 = this.pendingChunks.entrySet().iterator(); var13.hasNext() && var12 < var11; var12++) {
                  Entry var5 = (Entry)var13.next();
                  long var14 = (Long)var5.getKey();
                  int var8 = class_1923.method_8325(var14);
                  int var9 = class_1923.method_8332(var14);
                  class_2818 var10 = mc.field_1687.method_2935().method_12126(var8, var9, false);
                  if (var10 != null) {
                     this.scanChunk(var10);
                  }

                  this.scannedChunks.put(var14, Boolean.TRUE);
                  var13.remove();
               }

               this.pruneUnloadedSpawners();
            }
         }
      }
   }

   private void enqueueNearbyChunks() {
      this.scannedChunks.clear();
      if (mc.field_1724 != null && mc.field_1687 != null) {
         class_1923 var1 = mc.field_1724.method_31476();
         int var2 = Math.max(2, mc.field_1690.method_38521());
         int var3 = Math.min((Integer)this.scanRadius.get(), var2);
         ArrayList var4 = new ArrayList();

         for (int var5 = -var3; var5 <= var3; var5++) {
            for (int var6 = -var3; var6 <= var3; var6++) {
               var4.add(new class_1923(var1.field_9181 + var5, var1.field_9180 + var6));
            }
         }

         var4.sort(
            Comparator.comparingInt(
               cp -> (cp.field_9181 - var1.field_9181) * (cp.field_9181 - var1.field_9181)
                  + (cp.field_9180 - var1.field_9180) * (cp.field_9180 - var1.field_9180)
            )
         );

         for (class_1923 var8 : var4) {
            this.pendingChunks.put(var8.method_8324(), Boolean.TRUE);
         }
      }
   }

   private void scanChunk(class_2818 chunk) {
      if (chunk != null) {
         for (Entry var3 : chunk.method_12214().entrySet()) {
            class_2586 var4 = (class_2586)var3.getValue();
            if (var4 instanceof class_2636) {
               this.trackSpawner((class_2338)var3.getKey(), var4);
            }
         }

         class_2826[] var15 = chunk.method_12006();
         int var16 = mc.field_1687 != null ? mc.field_1687.method_31607() : -64;
         class_1923 var17 = chunk.method_12004();

         for (int var5 = 0; var5 < var15.length; var5++) {
            class_2826 var6 = var15[var5];
            if (var6 != null && !var6.method_38292() && var6.method_19523(state -> state.method_27852(class_2246.field_10260))) {
               int var7 = var16 + (var5 << (4));

               for (int var8 = 0; var8 < (16); var8++) {
                  for (int var9 = 0; var9 < (16); var9++) {
                     for (int var10 = 0; var10 < (16); var10++) {
                        class_2680 var11 = var6.method_12254(var8, var9, var10);
                        if (var11.method_27852(class_2246.field_10260)) {
                           class_2338 var12 = new class_2338(var17.method_8326() + var8, var7 + var9, var17.method_8328() + var10);
                           class_2586 var13 = chunk.method_8321(var12);
                           this.trackSpawner(var12, var13);
                        }
                     }
                  }
               }
            }
         }

         long var18 = var17.field_9181;
         long var19 = var17.field_9180;

         for (Entry var21 : this.resolvedIds.entrySet()) {
            long var22 = (Long)var21.getKey();
            if (class_2338.method_10061(var22) >> (4) == var18 && class_2338.method_10083(var22) >> (4) == var19) {
               class_2338 var23 = class_2338.method_10092(var22);
               class_2680 var14 = chunk.method_8320(var23);
               if (!var14.method_27852(class_2246.field_10260)) {
                  this.resolvedIds.remove(var22);
                  SpawnerEntityCache.remove(var23);
               }
            }
         }
      }
   }

   private void trackSpawner(class_2338 pos, class_2586 be) {
      long var3 = pos.method_10063();
      String var5 = (String)this.resolvedIds.get(var3);
      if (var5 == null || var5.isEmpty() || var5.equals("?")) {
         String var6 = this.resolveMobId(pos, be);
         if (var6 != null && !var6.isEmpty()) {
            this.resolvedIds.put(var3, var6);
            SpawnerEntityCache.put(pos, var6);
         } else {
            this.resolvedIds.putIfAbsent(var3, "");
         }
      }
   }

   private void pruneUnloadedSpawners() {
      if (!(Boolean)this.persistent.get()) {
         if (mc.field_1687 != null && mc.field_1724 != null) {
            class_631 var1 = mc.field_1687.method_2935();
            int var2 = Math.max(2, mc.field_1690.method_38521());
            class_1923 var3 = mc.field_1724.method_31476();
            this.resolvedIds
               .keySet()
               .removeIf(
                  key -> {
                     int var4 = class_2338.method_10061(key) >> (4);
                     int var5 = class_2338.method_10083(key) >> (4);
                     return Math.abs(var4 - var3.field_9181) <= var2 && Math.abs(var5 - var3.field_9180) <= var2
                        ? var1.method_12126(var4, var5, false) == null
                        : true;
                  }
               );
         }
      }
   }

   private String resolveMobId(class_2338 pos, class_2586 providedBe) {
      String var3 = SpawnerEntityCache.get(pos);
      if (var3 != null && !var3.isEmpty()) {
         return stripNamespace(var3);
      }

      class_2586 var4 = providedBe;
      if (var4 == null && mc.field_1687 != null) {
         var4 = mc.field_1687.method_8321(pos);
      }

      if (var4 instanceof class_2636 var5) {
         try {
            class_1917 var6 = var5.method_11390();
            if (var6 != null) {
               class_1297 var7 = var6.method_8283(mc.field_1687, pos);
               if (var7 != null) {
                  return stripNamespace(class_7923.field_41177.method_10221(var7.method_5864()).toString());
               }

               if (SPAWN_ENTRY_FIELD != null) {
                  class_1952 var8 = (class_1952)SPAWN_ENTRY_FIELD.get(var6);
                  if (var8 != null) {
                     String var9 = readEntityId(var8.method_38093());
                     if (var9 != null && !var9.isEmpty()) {
                        return stripNamespace(var9);
                     }
                  }
               }

               if (SPAWN_POTENTIALS_FIELD != null) {
                  class_6012 var18 = (class_6012)SPAWN_POTENTIALS_FIELD.get(var6);
                  if (var18 != null && !var18.method_34993()) {
                     List var20 = var18.method_34994();
                     if (!var20.isEmpty() && ((class_6010)var20.get(0)).comp_2542() != null) {
                        String var10 = readEntityId(((class_1952)((class_6010)var20.get(0)).comp_2542()).method_38093());
                        if (var10 != null && !var10.isEmpty()) {
                           return stripNamespace(var10);
                        }
                     }
                  }
               }
            }
         } catch (Throwable var12) {
         }

         try {
            if (mc.field_1687 != null) {
               class_2487 var14 = var5.method_16887(mc.field_1687.method_30349());
               String var16 = readEntityId(var14);
               if (var16 != null && !var16.isEmpty()) {
                  return stripNamespace(var16);
               }
            }
         } catch (Throwable var11) {
         }
      }

      if (mc.field_1687 != null) {
         class_238 var13 = new class_238(pos).method_1014(4.5);
         List var15 = mc.field_1687.method_8333(null, var13, e -> e instanceof class_1308);
         if (!var15.isEmpty()) {
            for (class_1297 var19 : var15) {
               String var21 = stripNamespace(class_7923.field_41177.method_10221(var19.method_5864()).toString());
               if (isCommonSpawnerMob(var21)) {
                  return var21;
               }
            }
         }
      }

      return null;
   }

   private static boolean isCommonSpawnerMob(String typeId) {
      if (typeId == null) {
         return false;
      }

      return switch (typeId.toLowerCase(Locale.ROOT)) {
         case "blaze", "skeleton", "wither_skeleton", "stray", "bogged", "zombie", "drowned", "husk", "zombie_villager", "spider", "cave_spider", "creeper", "enderman", "slime", "magma_cube", "silverfish", "witch", "phantom", "guardian", "elder_guardian", "pig", "cow", "sheep", "chicken", "iron_golem" -> true;
         default -> false;
      };
   }

   private static String readEntityId(class_2487 nbt) {
      if (nbt != null && !nbt.method_33133()) {
         for (String var4 : new String[]{
            "spawn_data",
            "SpawnData"
         }) {
            if (nbt.method_10545(var4)) {
               class_2487 var5 = nbt.method_68568(var4);
               if (var5 != null && !var5.method_33133()) {
                  if (var5.method_10545("entity")) {
                     class_2487 var6 = var5.method_68568("entity");
                     if (var6 != null && !var6.method_33133()) {
                        String var7 = var6.method_68564("id", "");
                        if (!var7.isEmpty()) {
                           return var7;
                        }
                     }
                  }

                  String var20 = var5.method_68564("id", "");
                  if (!var20.isEmpty()) {
                     return var20;
                  }
               }
            }
         }

         for (String var18 : new String[]{
            "spawn_potentials",
            "SpawnPotentials"
         }) {
            if (nbt.method_10545(var18)) {
               class_2499 var19 = nbt.method_68569(var18);

               for (int var21 = 0; var21 < var19.size(); var21++) {
                  class_2487 var22 = var19.method_68582(var21);
                  if (var22.method_10545("data")) {
                     class_2487 var8 = var22.method_68568("data");
                     if (var8.method_10545("entity")) {
                        class_2487 var9 = var8.method_68568("entity");
                        String var10 = var9.method_68564("id", "");
                        if (!var10.isEmpty()) {
                           return var10;
                        }
                     }

                     String var25 = var8.method_68564("id", "");
                     if (!var25.isEmpty()) {
                        return var25;
                     }
                  }

                  if (var22.method_10545("entity")) {
                     class_2487 var23 = var22.method_68568("entity");
                     String var26 = var23.method_68564("id", "");
                     if (!var26.isEmpty()) {
                        return var26;
                     }
                  }

                  String var24 = var22.method_68564("id", "");
                  if (!var24.isEmpty()) {
                     return var24;
                  }
               }
            }
         }

         if (nbt.method_10545("EntityId")) {
            String var12 = nbt.method_68564("EntityId", "");
            if (!var12.isEmpty()) {
               return var12;
            }
         }

         if (nbt.method_10545("entity")) {
            class_2487 var13 = nbt.method_68568("entity");
            String var16 = var13.method_68564("id", "");
            if (!var16.isEmpty()) {
               return var16;
            }
         }

         if (nbt.method_10545("id")) {
            String var14 = nbt.method_68564("id", "");
            if (!var14.isEmpty()
               && !var14.equals(
                  "minecraft:mob_spawner"
               )) {
               return var14;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static String stripNamespace(String id) {
      if (id == null) {
         return null;
      }

      int var1 = id.lastIndexOf(58);
      return var1 >= 0 ? id.substring(var1 + 1) : id;
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (this.isInGame() && this.isEnabled() && mc.field_1724 != null && mc.field_1687 != null) {
         if (!this.resolvedIds.isEmpty()) {
            if (this.projector.snapshot()) {
               float var2 = ((Double)this.scale.get()).floatValue();
               int var3 = Math.max(2, mc.field_1690.method_38521());
               double var4 = var3 * 16.0;
               double var6 = Math.min((Double)this.maxDistance.get(), var4);
               double var8 = var6 * var6;
               double var10 = (Double)this.yOffset.get();
               int var12 = (Integer)this.backgroundColor.get();
               int var13 = (Integer)this.textColor.get();
               class_4184 var14 = mc.field_1773 != null ? mc.field_1773.method_19418() : null;
               class_243 var15 = var14 != null ? var14.method_71156() : mc.field_1724.method_33571();
               double var16 = var6 * 0.85;
               this.activeTags.clear();
               this.activeOffscreen.clear();

               for (Entry var19 : this.resolvedIds.entrySet()) {
                  long var20 = (Long)var19.getKey();
                  int var22 = class_2338.method_10061(var20);
                  int var23 = class_2338.method_10071(var20);
                  int var24 = class_2338.method_10083(var20);
                  double var25 = var22 + 0.5;
                  double var27 = var23 + var10 + 0.25;
                  double var29 = var24 + 0.5;
                  double var31 = var25 - var15.field_1352;
                  double var33 = var27 - var15.field_1351;
                  double var35 = var29 - var15.field_1350;
                  double var37 = var31 * var31 + var33 * var33 + var35 * var35;
                  if (!(var37 > var8)) {
                     String var39 = (String)var19.getValue();
                     if (var39 == null || var39.isEmpty()) {
                        if (!(Boolean)this.showEmpty.get()) {
                           continue;
                        }

                        var39 = "?";
                     }

                     double var40 = Math.sqrt(var37);
                     float var42 = 1.0F;
                     if (var40 > var16) {
                        var42 = (float)Math.max(0.0, Math.min(1.0, (var6 - var40) / (var6 - var16)));
                     }

                     boolean var43 = this.projector.project(var25, var27, var29, this.scratchScreen);
                     boolean var44 = var43 && this.projector.onScreen(this.scratchScreen, 32.0F);
                     if (var44) {
                        SpawnerTags.ProjectedTag var45;
                        if (this.activeTags.size() < this.tagPool.size()) {
                           var45 = (SpawnerTags.ProjectedTag)this.tagPool.get(this.activeTags.size());
                        } else {
                           var45 = new SpawnerTags.ProjectedTag();
                           this.tagPool.add(var45);
                        }

                        var45.set(this.scratchScreen[0], this.scratchScreen[1], var40, var42, var39);
                        this.activeTags.add(var45);
                     } else if ((Boolean)this.offscreen.get() && var40 <= var6) {
                        SpawnerTags.ProjectedOffscreen var75;
                        if (this.activeOffscreen.size() < this.offscreenPool.size()) {
                           var75 = (SpawnerTags.ProjectedOffscreen)this.offscreenPool.get(this.activeOffscreen.size());
                        } else {
                           var75 = new SpawnerTags.ProjectedOffscreen();
                           this.offscreenPool.add(var75);
                        }

                        double var46 = var25 - var15.field_1352;
                        double var48 = var29 - var15.field_1350;
                        float var50 = var14 != null ? var14.method_19330() : mc.field_1724.method_36454();
                        float var51 = (float)Math.toDegrees(Math.atan2(var48, var46)) - 90.0F;
                        float var52 = class_3532.method_15393(var51 - var50);
                        var75.set(var52, var40, var42, var39);
                        this.activeOffscreen.add(var75);
                     }
                  }
               }

               if (!this.activeTags.isEmpty() || !this.activeOffscreen.isEmpty()) {
                  if (!this.activeTags.isEmpty()) {
                     this.activeTags.sort((a, b) -> Double.compare(b.dist, a.dist));
                  }

                  try (NanoVGFrame var56 = NanoVGRenderer.frame()) {
                     if (!var56.isActive()) {
                        return;
                     }

                     for (SpawnerTags.ProjectedTag var59 : this.activeTags) {
                        this.drawModernSpawnerTag(var59.cx, var59.cy, var59.rawId, var59.dist, var2, var12, var13, var59.distFade);
                     }

                     if (!this.activeOffscreen.isEmpty()) {
                        float var58 = mc.method_22683().method_4486() / 2.0F;
                        float var60 = mc.method_22683().method_4502() / 2.0F;
                        float var21 = ((Double)this.arrowDistance.get()).floatValue();
                        float var61 = Math.max(20.0F, Math.min(var58, var60) - 16.0F);
                        float var62 = Math.min(var21, var61);

                        for (SpawnerTags.ProjectedOffscreen var64 : this.activeOffscreen) {
                           double var26 = Math.toRadians(var64.relativeYaw);
                           float var28 = (float)Math.sin(var26);
                           float var65 = (float)(-Math.cos(var26));
                           float var30 = var58 + var28 * var62;
                           float var66 = var60 + var65 * var62;
                           SpawnerTags.MobDisplayInfo var32 = getMobInfo(var64.rawId);
                           String var67 = var32.formattedName();
                           int var34 = var32.accentColor();
                           float var68 = 6.0F;
                           float var36 = -var65;
                           float var69 = var28;
                           float var38 = var30 + var28 * var68;
                           float var70 = var66 + var65 * var68;
                           float var71 = var30 - var28 * (var68 * 0.4F) + var36 * (var68 * 0.5F);
                           float var41 = var66 - var65 * (var68 * 0.4F) + var69 * (var68 * 0.5F);
                           float var72 = var30 - var28 * (var68 * 0.4F) - var36 * (var68 * 0.5F);
                           float var73 = var66 - var65 * (var68 * 0.4F) - var69 * (var68 * 0.5F);
                           int var74 = (int)(220.0F * var64.distFade);
                           int var76 = var74 << 24 | var34 & 16777215;
                           NanoVGRenderer.drawTriangle(var38, var70, var71, var41, var72, var73, var76);
                           String var77 = Math.round(var64.dist) + "m";
                           float var47 = 5.2F;
                           float var78 = NanoVGRenderer.getTextWidth(var67, var47);
                           float var49 = NanoVGRenderer.getTextWidth(var77, var47);
                           float var79 = var78 + 2.0F + var49;
                           float var80 = 7.0F;
                           float var81 = var30 - var79 / 2.0F;
                           float var53 = var66 + (var65 >= 0.0F ? var68 + 2.0F : -(var68 + var80 + 2.0F));
                           NanoVGRenderer.drawRoundedRect(var81 - 2.5F, var53 - 1.0F, var79 + 5.0F, var80, 2.0F, (int)(200.0F * var64.distFade) << 24 | 922138);
                           NanoVGRenderer.drawRoundedRectOutline(
                              var81 - 2.5F, var53 - 1.0F, var79 + 5.0F, var80, 2.0F, 0.6F, (int)(40.0F * var64.distFade) << 24 | 16777215
                           );
                           NanoVGRenderer.drawText(var67, var81, var53, var47, var76, true);
                           NanoVGRenderer.drawText(var77, var81 + var78 + 2.0F, var53, var47, (int)(160.0F * var64.distFade) << 24 | 9741240);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void drawModernSpawnerTag(float cx, float cy, String rawId, double dist, float userScaleSetting, int bgSetting, int fgSetting, float distFade) {
      SpawnerTags.MobDisplayInfo var10 = getMobInfo(rawId);
      String var11 = var10.formattedName();
      int var12 = var10.accentColor();
      float var13 = (float)Math.max(0.65F, Math.min(1.0, 1.0 - (dist - 8.0) * 0.0035F));
      float var14 = Math.max(0.22F, userScaleSetting / 4.0F * 0.42F) * var13;
      float var15 = var14 / 0.42F;
      float var16 = Math.max(3.8F, 7.2F * var14);
      float var17 = Math.max(3.2F, 5.8F * var14);
      String var18 = (int)Math.round(dist) + "m";
      float var19 = NanoVGRenderer.getTextWidth(var11, var16);
      float var20 = NanoVGRenderer.getTextWidth(var18, var17);
      float var21 = 1.0F * var15;
      float var22 = 2.0F * var15;
      float var23 = 2.6F * var15;
      float var24 = 1.2F * var15;
      float var25 = 2.2F * var15;
      float var26 = var23 + (var21 * 2.0F + var22) + var19 + var25 + var20 + var23;
      float var27 = Math.max(7.0F * var15, var16 + var24 * 2.0F);
      float var28 = var27 / 2.0F;
      float var29 = Math.round(cx - var26 / 2.0F);
      float var30 = Math.round(cy - var27 / 2.0F);
      float var31 = Math.max(0.0F, Math.min(1.0F, distFade));
      int var32 = (int)(255.0F * var31);
      if (var32 > 4) {
         int var33 = (int)(215.0F * var31) << 24 | 922138;
         NanoVGRenderer.drawRoundedRect(var29, var30, var26, var27, var28, var33);
         int var34 = (int)(38.0F * var31) << 24 | 16777215;
         NanoVGRenderer.drawRoundedRectOutline(var29, var30, var26, var27, var28, 0.7F, var34);
         float var35 = var29 + var23 + var21;
         float var36 = var30 + var27 / 2.0F;
         int var37 = (int)(255.0F * var31) << 24 | var12 & 16777215;
         NanoVGRenderer.drawCircle(var35, var36, var21, var37);
         float var38 = var35 + var21 + var22;
         float var39 = NanoVGRenderer.getTextHeight(var16);
         float var40 = var30 + (var27 - var39) / 2.0F;
         int var41 = (int)(245.0F * var31) << 24 | fgSetting & 16777215;
         NanoVGRenderer.drawText(var11, var38, var40, var16, var41, true);
         float var42 = var38 + var19 + var25;
         float var43 = NanoVGRenderer.getTextHeight(var17);
         float var44 = var30 + (var27 - var43) / 2.0F;
         int var45 = (int)(165.0F * var31) << 24 | 9741240;
         NanoVGRenderer.drawText(var18, var42, var44, var17, var45);
      }
   }

   static {
      Field var0 = null;
      Field var1 = null;

      try {
         for (Field var5 : class_1917.class.getDeclaredFields()) {
            if (class_1952.class.isAssignableFrom(var5.getType())) {
               var5.setAccessible(true);
               var0 = var5;
            } else if (class_6012.class.isAssignableFrom(var5.getType())) {
               var5.setAccessible(true);
               var1 = var5;
            }
         }
      } catch (Throwable var6) {
      }

      SPAWN_ENTRY_FIELD = var0;
      SPAWN_POTENTIALS_FIELD = var1;
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

   public static final class MobDisplayInfo {
      private final String formattedName;
      private final int accentColor;
      private final boolean isKnown;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public MobDisplayInfo(String formattedName, int accentColor, boolean isKnown) {
         this.formattedName = formattedNamex;
         this.accentColor = accentColorx;
         this.isKnown = isKnownx;
      }

      public String formattedName() {
         return this.formattedName;
      }

      public int accentColor() {
         return this.accentColor;
      }

      public boolean isKnown() {
         return this.isKnown;
      }
   }

   private static final class ProjectedOffscreen {
      float relativeYaw;
      double dist;
      float distFade;
      String rawId;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      void set(float relativeYaw, double dist, float distFade, String rawId) {
         this.relativeYaw = relativeYawx;
         this.dist = distx;
         this.distFade = distFadex;
         this.rawId = rawIdx;
      }
   }

   private static final class ProjectedTag {
      float cx;
      float cy;
      double dist;
      float distFade;
      String rawId;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      void set(float cx, float cy, double dist, float distFade, String rawId) {
         this.cx = cxx;
         this.cy = cyx;
         this.dist = distx;
         this.distFade = distFadex;
         this.rawId = rawIdx;
      }
   }
}
