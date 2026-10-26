package com.zigythebird.playeranim.animation;

import com.zigythebird.playeranim.PlayerAnimLibMod;
import com.zigythebird.playeranimcore.PlayerAnimLib;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.class_2960;
import net.minecraft.class_3298;
import net.minecraft.class_3300;
import net.minecraft.class_4013;
import net.minecraft.class_3302.class_11558;
import net.minecraft.class_3302.class_4045;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerAnimResources implements class_4013 {
   public static final class_2960 KEY = PlayerAnimLibMod.id("animation");
   private static final Map<class_2960, Animation> ANIMATIONS = new Object2ObjectOpenHashMap();

   @Nullable
   public static Animation getAnimation(class_2960 id) {
      return ANIMATIONS.get(id);
   }

   @NotNull
   public static Optional<Animation> getAnimationOptional(@NotNull class_2960 identifier) {
      return Optional.ofNullable(getAnimation(identifier));
   }

   public static Map<class_2960, Animation> getAnimations() {
      return Collections.unmodifiableMap(ANIMATIONS);
   }

   @NotNull
   public static Map<String, Animation> getModAnimations(@NotNull String modid) {
      HashMap<String, Animation> map = new HashMap<>();

      for (Entry<class_2960, Animation> entry : ANIMATIONS.entrySet()) {
         if (entry.getKey().method_12836().equals(modid)) {
            map.put(entry.getKey().method_12832(), entry.getValue());
         }
      }

      return map;
   }

   public static boolean hasAnimation(class_2960 id) {
      return ANIMATIONS.containsKey(id);
   }

   public void method_14491(class_3300 manager) {
      ANIMATIONS.clear();

      for (Entry<class_2960, class_3298> resource : manager.method_14488(
            "player_animations", resourceLocation -> resourceLocation.method_12832().endsWith(".json")
         )
         .entrySet()) {
         String namespace = resource.getKey().method_12836();

         try (InputStream is = resource.getValue().method_14482()) {
            for (Entry<String, Animation> entry : UniversalAnimLoader.loadAnimations(is).entrySet()) {
               ANIMATIONS.put(class_2960.method_60655(namespace, entry.getKey()), entry.getValue());
            }
         } catch (Exception e) {
            PlayerAnimLib.LOGGER.error("Player Animation Library failed to load animation {} because:", resource.getKey(), e);
         }
      }
   }

   @NotNull
   public CompletableFuture<Void> method_25931(class_11558 sharedState, Executor backgroundExecutor, class_4045 barrier, Executor gameExecutor) {
      class_3300 manager = sharedState.method_72361();
      return CompletableFuture.runAsync(() -> this.method_14491(manager), backgroundExecutor).thenCompose(barrier::method_18352);
   }
}
