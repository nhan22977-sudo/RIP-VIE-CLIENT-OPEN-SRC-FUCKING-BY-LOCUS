package me.lucko.fabric.api.permissions.v0;

import com.mojang.authlib.GameProfile;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import net.fabricmc.fabric.api.util.TriState;
import net.minecraft.class_11560;
import net.minecraft.class_12094;
import net.minecraft.class_1297;
import net.minecraft.class_2168;
import net.minecraft.class_2172;
import net.minecraft.class_12087.class_12089;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public interface Permissions {
   @NotNull
   static TriState getPermissionValue(@NotNull class_2172 source, @NotNull String permission) {
      Objects.requireNonNull(source, "source");
      Objects.requireNonNull(permission, "permission");
      return ((PermissionCheckEvent)PermissionCheckEvent.EVENT.invoker()).onPermissionCheck(source, permission);
   }

   static boolean check(@NotNull class_2172 source, @NotNull String permission, boolean defaultValue) {
      return getPermissionValue(source, permission).orElse(defaultValue);
   }

   @Deprecated
   static boolean check(@NotNull class_2172 source, @NotNull String permission, int defaultRequiredLevel) {
      return check(source, permission, Util.permissionLevelFromInt(defaultRequiredLevel));
   }

   static boolean check(@NotNull class_2172 source, @NotNull String permission, @NotNull class_12094 defaultRequiredLevel) {
      return getPermissionValue(source, permission).orElseGet(() -> source.method_75037().hasPermission(new class_12089(defaultRequiredLevel)));
   }

   static boolean check(@NotNull class_2172 source, @NotNull String permission) {
      return getPermissionValue(source, permission).orElse(false);
   }

   @NotNull
   static Predicate<class_2168> require(@NotNull String permission, boolean defaultValue) {
      Objects.requireNonNull(permission, "permission");
      return player -> check(player, permission, defaultValue);
   }

   @Deprecated
   @NotNull
   static Predicate<class_2168> require(@NotNull String permission, int defaultRequiredLevel) {
      Objects.requireNonNull(permission, "permission");
      class_12094 level = Util.permissionLevelFromInt(defaultRequiredLevel);
      return player -> check(player, permission, level);
   }

   @NotNull
   static Predicate<class_2168> require(@NotNull String permission, class_12094 defaultRequiredLevel) {
      Objects.requireNonNull(permission, "permission");
      Objects.requireNonNull(defaultRequiredLevel, "defaultRequiredLevel");
      return player -> check(player, permission, defaultRequiredLevel);
   }

   @NotNull
   static Predicate<class_2168> require(@NotNull String permission) {
      Objects.requireNonNull(permission, "permission");
      return player -> check(player, permission);
   }

   @NotNull
   static TriState getPermissionValue(@NotNull class_1297 entity, @NotNull String permission) {
      Objects.requireNonNull(entity, "entity");
      return getPermissionValue(Util.commandSourceFromEntity(entity), permission);
   }

   static boolean check(@NotNull class_1297 entity, @NotNull String permission, boolean defaultValue) {
      Objects.requireNonNull(entity, "entity");
      return check(Util.commandSourceFromEntity(entity), permission, defaultValue);
   }

   @Deprecated
   static boolean check(@NotNull class_1297 entity, @NotNull String permission, int defaultRequiredLevel) {
      Objects.requireNonNull(entity, "entity");
      return check(Util.commandSourceFromEntity(entity), permission, defaultRequiredLevel);
   }

   static boolean check(@NotNull class_1297 entity, @NotNull String permission, class_12094 defaultRequiredLevel) {
      Objects.requireNonNull(entity, "entity");
      Objects.requireNonNull(defaultRequiredLevel, "permissionLevel");
      return check(Util.commandSourceFromEntity(entity), permission, defaultRequiredLevel);
   }

   static boolean check(@NotNull class_1297 entity, @NotNull String permission) {
      Objects.requireNonNull(entity, "entity");
      return check(Util.commandSourceFromEntity(entity), permission);
   }

   @NotNull
   static CompletableFuture<TriState> getPermissionValue(@NotNull UUID uuid, @NotNull String permission) {
      Objects.requireNonNull(uuid, "uuid");
      Objects.requireNonNull(permission, "permission");
      return ((OfflinePermissionCheckEvent)OfflinePermissionCheckEvent.EVENT.invoker()).onPermissionCheck(uuid, permission);
   }

   static CompletableFuture<Boolean> check(@NotNull UUID uuid, @NotNull String permission, boolean defaultValue) {
      return getPermissionValue(uuid, permission).thenApplyAsync(state -> state.orElse(defaultValue));
   }

   static CompletableFuture<Boolean> check(@NotNull UUID uuid, @NotNull String permission) {
      return getPermissionValue(uuid, permission).thenApplyAsync(state -> state.orElse(false));
   }

   static CompletableFuture<Boolean> check(@NotNull GameProfile profile, @NotNull String permission, boolean defaultValue) {
      Objects.requireNonNull(profile, "profile");
      return check(profile.id(), permission, defaultValue);
   }

   static CompletableFuture<Boolean> check(@NotNull GameProfile profile, @NotNull String permission) {
      Objects.requireNonNull(profile, "profile");
      return check(profile.id(), permission);
   }

   @Deprecated
   static CompletableFuture<Boolean> check(@NotNull GameProfile profile, @NotNull String permission, int defaultRequiredLevel, @NotNull MinecraftServer server) {
      return check(profile, permission, Util.permissionLevelFromInt(defaultRequiredLevel), server);
   }

   static CompletableFuture<Boolean> check(
      @NotNull GameProfile profile, @NotNull String permission, @NotNull class_12094 defaultRequiredLevel, @NotNull MinecraftServer server
   ) {
      Objects.requireNonNull(profile, "profile");
      Objects.requireNonNull(defaultRequiredLevel, "defaultRequiredLevel");
      Objects.requireNonNull(server, "server");
      BooleanSupplier permissionLevelCheck = () -> server.method_3835(new class_11560(profile)).method_75009().method_75028(defaultRequiredLevel);
      return getPermissionValue(profile.id(), permission).thenApplyAsync(state -> state.orElseGet(permissionLevelCheck));
   }

   static CompletableFuture<Boolean> check(@NotNull class_11560 entry, @NotNull String permission, boolean defaultValue) {
      Objects.requireNonNull(entry, "entry");
      return check(entry.comp_4422(), permission, defaultValue);
   }

   static CompletableFuture<Boolean> check(@NotNull class_11560 entry, @NotNull String permission) {
      Objects.requireNonNull(entry, "entry");
      return check(entry.comp_4422(), permission);
   }

   @Deprecated
   static CompletableFuture<Boolean> check(@NotNull class_11560 entry, @NotNull String permission, int defaultRequiredLevel, @NotNull MinecraftServer server) {
      return check(entry, permission, Util.permissionLevelFromInt(defaultRequiredLevel), server);
   }

   static CompletableFuture<Boolean> check(
      @NotNull class_11560 entry, @NotNull String permission, class_12094 defaultRequiredLevel, @NotNull MinecraftServer server
   ) {
      Objects.requireNonNull(entry, "entry");
      Objects.requireNonNull(defaultRequiredLevel, "defaultRequiredLevel");
      Objects.requireNonNull(server, "server");
      BooleanSupplier permissionLevelCheck = () -> server.method_3835(entry).method_75009().method_75028(defaultRequiredLevel);
      return getPermissionValue(entry.comp_4422(), permission).thenApplyAsync(state -> state.orElseGet(permissionLevelCheck));
   }
}
