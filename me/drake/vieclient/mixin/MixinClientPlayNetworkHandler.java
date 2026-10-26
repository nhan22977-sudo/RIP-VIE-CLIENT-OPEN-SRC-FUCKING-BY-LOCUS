package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.ChunkDataEvent;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.LightUpdateEvent;
import me.drake.vieclient.core.event.events.ServerBlockUpdateEvent;
import me.drake.vieclient.render.ImmediateGuiRenderer;
import me.drake.vieclient.render.ViePipelines;
import me.drake.vieclient.util.SpawnerEntityCache;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2591;
import net.minecraft.class_2622;
import net.minecraft.class_2626;
import net.minecraft.class_2637;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_2678;
import net.minecraft.class_310;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_634.class)
public class MixinClientPlayNetworkHandler {
   @Inject(method = "method_11136", at = @At("RETURN"))
   private void afterServerBlockUpdate(class_2626 packet, CallbackInfo ci) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getEventBus().hasSubscribers(ServerBlockUpdateEvent.class)) {
         var3.getEventBus().post(new ServerBlockUpdateEvent(packet.method_11309(), packet.method_11308()));
      }
   }

   @Inject(method = "method_11100", at = @At("RETURN"))
   private void afterServerChunkDelta(class_2637 packet, CallbackInfo ci) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getEventBus().hasSubscribers(ServerBlockUpdateEvent.class)) {
         packet.method_30621((pos, state) -> var3.getEventBus().post(new ServerBlockUpdateEvent(pos, state)));
      }
   }

   @Inject(method = "method_11120", at = @At("RETURN"))
   private void onGameJoin(class_2678 packet, CallbackInfo ci) {
      ViePipelines.isPrecompiled = false;
      ImmediateGuiRenderer.reset();
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getEventBus() != null) {
         var3.getEventBus().post(GameJoinEvent.INSTANCE);
      }
   }

   @Inject(method = "method_11094", at = @At("HEAD"))
   private void onBlockEntityUpdate(class_2622 packet, CallbackInfo ci) {
      if (packet.method_11291() == class_2591.field_11889) {
         class_2487 var3 = packet.method_11290();
         if (var3 != null) {
            String var4 = readSpawnEntityId(var3);
            SpawnerEntityCache.put(packet.method_11293(), var4 != null && !var4.isEmpty() ? var4 : "");
         }
      }
   }

   @Inject(method = "method_11143", at = @At("HEAD"))
   private void onLightUpdate(class_2676 packet, CallbackInfo ci) {
      if (class_310.method_1551().method_18854()) {
         VieClientMod var3 = VieClientMod.getInstance();
         if (var3 != null && var3.getEventBus() != null) {
            var3.getEventBus().post(new LightUpdateEvent(packet));
         }
      }
   }

   @Inject(method = "method_11128", at = @At("HEAD"))
   private void onChunkData(class_2672 packet, CallbackInfo ci) {
      if (class_310.method_1551().method_18854()) {
         VieClientMod var3 = VieClientMod.getInstance();
         if (var3 != null && var3.getEventBus() != null) {
            var3.getEventBus().post(new ChunkDataEvent(packet));
         }

         packet.method_38598().method_38587(packet.method_11523(), packet.method_11524()).accept((pos, type, nbt) -> {
            if (type == class_2591.field_11889 && nbt != null) {
               String var3x = readSpawnEntityId(nbt);
               SpawnerEntityCache.put(pos, var3x != null && !var3x.isEmpty() ? var3x : "");
            }
         });
      }
   }

   private static String readSpawnEntityId(class_2487 nbt) {
      if (nbt != null && !nbt.method_33133()) {
         String var1 = readSpawnEntityId(nbt, "spawn_data");
         if (var1 != null) {
            return var1;
         }

         var1 = readSpawnEntityId(nbt, "SpawnData");
         if (var1 != null) {
            return var1;
         }

         for (String var5 : new String[]{"spawn_potentials", "SpawnPotentials"}) {
            if (nbt.method_10545(var5)) {
               class_2499 var6 = nbt.method_68569(var5);

               for (int var7 = 0; var7 < var6.size(); var7++) {
                  class_2487 var8 = var6.method_68582(var7);
                  if (var8.method_10545("data")) {
                     String var9 = readSpawnEntityId(var8, "data");
                     if (var9 != null) {
                        return var9;
                     }
                  }

                  if (var8.method_10545("entity")) {
                     class_2487 var15 = var8.method_68568("entity");
                     String var10 = var15.method_68564("id", "");
                     if (!var10.isEmpty()) {
                        return normalize(var10);
                     }
                  }

                  String var16 = var8.method_68564("id", "");
                  if (!var16.isEmpty()) {
                     return normalize(var16);
                  }
               }
            }
         }

         if (nbt.method_10545("EntityId")) {
            String var12 = nbt.method_68564("EntityId", "");
            if (!var12.isEmpty()) {
               return normalize(var12);
            }
         }

         if (nbt.method_10545("entity")) {
            class_2487 var13 = nbt.method_68568("entity");
            String var14 = var13.method_68564("id", "");
            if (!var14.isEmpty()) {
               return normalize(var14);
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static String readSpawnEntityId(class_2487 nbt, String key) {
      if (!nbt.method_10545(key)) {
         return null;
      }

      class_2487 var2 = nbt.method_68568(key);
      if (var2 != null && !var2.method_33133()) {
         if (var2.method_10545("entity")) {
            class_2487 var3 = var2.method_68568("entity");
            if (var3 != null && !var3.method_33133()) {
               String var4 = var3.method_68564("id", "");
               if (!var4.isEmpty()) {
                  return normalize(var4);
               }
            }
         }

         String var5 = var2.method_68564("id", "");
         return !var5.isEmpty() ? normalize(var5) : null;
      } else {
         return null;
      }
   }

   private static String normalize(String id) {
      if (id != null && !id.isEmpty()) {
         int var1 = id.lastIndexOf(58);
         return var1 >= 0 ? id.substring(var1 + 1) : id;
      } else {
         return null;
      }
   }
}
