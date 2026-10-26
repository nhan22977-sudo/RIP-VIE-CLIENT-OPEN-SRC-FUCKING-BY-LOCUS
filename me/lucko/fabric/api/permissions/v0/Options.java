package me.lucko.fabric.api.permissions.v0;

import com.mojang.authlib.GameProfile;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.class_11560;
import net.minecraft.class_1297;
import net.minecraft.class_2172;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface Options {
   @NotNull
   static Optional<String> get(@NotNull class_2172 source, @NotNull String key) {
      Objects.requireNonNull(source, "source");
      Objects.requireNonNull(key, "key");
      return ((OptionRequestEvent)OptionRequestEvent.EVENT.invoker()).onOptionRequest(source, key);
   }

   @Contract("_, _, !null -> !null")
   static String get(@NotNull class_2172 source, @NotNull String key, String defaultValue) {
      return get(source, key).orElse(defaultValue);
   }

   @NotNull
   static <T> Optional<T> get(@NotNull class_2172 source, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
      return get(source, key).flatMap(value -> {
         try {
            return Optional.ofNullable(valueTransformer.apply(value));
         } catch (IllegalArgumentException e) {
            return Optional.empty();
         }
      });
   }

   @Contract("_, _, !null, _ -> !null")
   static <T> T get(@NotNull class_2172 source, @NotNull String key, T defaultValue, @NotNull Function<String, ? extends T> valueTransformer) {
      return get(source, key, valueTransformer).orElse(defaultValue);
   }

   @NotNull
   static Optional<String> get(@NotNull class_1297 entity, @NotNull String key) {
      Objects.requireNonNull(entity, "entity");
      return get(Util.commandSourceFromEntity(entity), key);
   }

   @Contract("_, _, !null -> !null")
   static String get(@NotNull class_1297 entity, @NotNull String key, String defaultValue) {
      Objects.requireNonNull(entity, "entity");
      return get(Util.commandSourceFromEntity(entity), key, defaultValue);
   }

   @NotNull
   static <T> Optional<T> get(@NotNull class_1297 entity, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
      Objects.requireNonNull(entity, "entity");
      return get(Util.commandSourceFromEntity(entity), key, valueTransformer);
   }

   @Contract("_, _, !null, _ -> !null")
   static <T> T get(@NotNull class_1297 entity, @NotNull String key, T defaultValue, @NotNull Function<String, ? extends T> valueTransformer) {
      Objects.requireNonNull(entity, "entity");
      return get(Util.commandSourceFromEntity(entity), key, defaultValue, valueTransformer);
   }

   @NotNull
   static CompletableFuture<Optional<String>> get(@NotNull UUID uuid, @NotNull String key) {
      Objects.requireNonNull(uuid, "uuid");
      Objects.requireNonNull(key, "key");
      return ((OfflineOptionRequestEvent)OfflineOptionRequestEvent.EVENT.invoker()).onOptionRequest(uuid, key);
   }

   @Contract("_, _, !null -> !null")
   static CompletableFuture<String> get(@NotNull UUID uuid, @NotNull String key, String defaultValue) {
      return get(uuid, key).thenApply(opt -> opt.orElse(defaultValue));
   }

   @NotNull
   static <T> CompletableFuture<Optional<T>> get(@NotNull UUID uuid, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
      return get(uuid, key).thenApply(opt -> opt.flatMap(value -> {
         try {
            return Optional.ofNullable(valueTransformer.apply(value));
         } catch (IllegalArgumentException e) {
            return Optional.empty();
         }
      }));
   }

   @Contract("_, _, !null, _ -> !null")
   static <T> CompletableFuture<T> get(@NotNull UUID uuid, @NotNull String key, T defaultValue, @NotNull Function<String, ? extends T> valueTransformer) {
      return get(uuid, key, valueTransformer).thenApply(opt -> opt.orElse(defaultValue));
   }

   @NotNull
   static CompletableFuture<Optional<String>> get(@NotNull GameProfile profile, @NotNull String key) {
      Objects.requireNonNull(profile, "profile");
      return get(profile.id(), key);
   }

   @Contract("_, _, !null -> !null")
   static CompletableFuture<String> get(@NotNull GameProfile profile, @NotNull String key, String defaultValue) {
      Objects.requireNonNull(profile, "profile");
      return get(profile.id(), key, defaultValue);
   }

   @NotNull
   static <T> CompletableFuture<Optional<T>> get(@NotNull GameProfile profile, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
      Objects.requireNonNull(profile, "profile");
      return get(profile.id(), key, valueTransformer);
   }

   @Contract("_, _, !null, _ -> !null")
   static <T> CompletableFuture<T> get(
      @NotNull GameProfile profile, @NotNull String key, T defaultValue, @NotNull Function<String, ? extends T> valueTransformer
   ) {
      Objects.requireNonNull(profile, "profile");
      return get(profile.id(), key, defaultValue, valueTransformer);
   }

   @NotNull
   static CompletableFuture<Optional<String>> get(@NotNull class_11560 entry, @NotNull String key) {
      Objects.requireNonNull(entry, "entry");
      return get(entry.comp_4422(), key);
   }

   @Contract("_, _, !null -> !null")
   static CompletableFuture<String> get(@NotNull class_11560 entry, @NotNull String key, String defaultValue) {
      Objects.requireNonNull(entry, "entry");
      return get(entry.comp_4422(), key, defaultValue);
   }

   @NotNull
   static <T> CompletableFuture<Optional<T>> get(@NotNull class_11560 entry, @NotNull String key, @NotNull Function<String, ? extends T> valueTransformer) {
      Objects.requireNonNull(entry, "entry");
      return get(entry.comp_4422(), key, valueTransformer);
   }

   @Contract("_, _, !null, _ -> !null")
   static <T> CompletableFuture<T> get(@NotNull class_11560 entry, @NotNull String key, T defaultValue, @NotNull Function<String, ? extends T> valueTransformer) {
      Objects.requireNonNull(entry, "entry");
      return get(entry.comp_4422(), key, defaultValue, valueTransformer);
   }
}
