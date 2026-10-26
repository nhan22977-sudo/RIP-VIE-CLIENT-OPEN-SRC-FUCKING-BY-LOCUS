package io.github.kosmx.emotes.arch.screen.utils;

import com.google.gson.internal.UnsafeAllocator;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.class_11897;
import net.minecraft.class_1267;
import net.minecraft.class_1297;
import net.minecraft.class_1923;
import net.minecraft.class_1937;
import net.minecraft.class_1944;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2361;
import net.minecraft.class_238;
import net.minecraft.class_2680;
import net.minecraft.class_269;
import net.minecraft.class_2791;
import net.minecraft.class_2806;
import net.minecraft.class_2812;
import net.minecraft.class_2874;
import net.minecraft.class_5321;
import net.minecraft.class_5575;
import net.minecraft.class_5577;
import net.minecraft.class_634;
import net.minecraft.class_638;
import net.minecraft.class_6563;
import net.minecraft.class_6880;
import net.minecraft.class_761;
import net.minecraft.class_7927;
import net.minecraft.class_638.class_5271;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnsafeClientLevel extends class_638 implements class_5577<class_1297> {
   public static final UnsafeClientLevel INSTANCE;
   private static final class_5271 CLIENT_LEVEL_DATA;
   private static final class_11897 PALETTED_CONTAINER_FACTORY;

   private UnsafeClientLevel(
      class_634 connection,
      class_5271 levelData,
      class_5321<class_1937> dimension,
      class_6880<class_2874> dimensionTypeRegistration,
      int viewDistance,
      int serverSimulationDistance,
      class_761 levelRenderer,
      boolean isDebug,
      long biomeZoomSeed,
      int seaLevel
   ) {
      super(
         connection, levelData, dimension, dimensionTypeRegistration, viewDistance, serverSimulationDistance, levelRenderer, isDebug, biomeZoomSeed, seaLevel
      );
   }

   public int method_8314(class_1944 lightType, class_2338 blockPos) {
      return 0;
   }

   @NotNull
   public class_269 method_8428() {
      return new class_269();
   }

   public boolean method_31601(int y) {
      return true;
   }

   public int method_31607() {
      return 1;
   }

   public int method_31605() {
      return 1;
   }

   public boolean method_8419() {
      return false;
   }

   @NotNull
   protected class_5577<class_1297> method_31592() {
      return this;
   }

   @Nullable
   public class_1297 get(int id) {
      return null;
   }

   @Nullable
   public class_1297 get(UUID uuid) {
      return null;
   }

   @NotNull
   public Iterable<class_1297> method_31803() {
      return Collections.emptyList();
   }

   public <U extends class_1297> void method_31806(class_5575<class_1297, U> test, class_7927<U> consumer) {
   }

   public void method_31807(class_238 boundingBox, Consumer<class_1297> consumer) {
   }

   public <U extends class_1297> void method_31805(class_5575<class_1297, U> test, class_238 bounds, class_7927<U> consumer) {
   }

   public class_2791 method_8402(int x, int z, class_2806 chunkStatus, boolean requireChunk) {
      return new class_2812(this, new class_1923(x, z), class_6880.method_40223(null));
   }

   @NotNull
   public class_2680 method_8320(class_2338 pos) {
      return class_2246.field_10243.method_9564();
   }

   public long method_75260() {
      return 0L;
   }

   public long method_8532() {
      return 0L;
   }

   @NotNull
   public class_5271 method_28104() {
      return CLIENT_LEVEL_DATA;
   }

   @NotNull
   public List<class_1297> method_66349(class_1297 entity, class_238 boundingBox) {
      return Collections.emptyList();
   }

   @NotNull
   public class_11897 method_74142() {
      return PALETTED_CONTAINER_FACTORY;
   }

   public boolean method_8608() {
      return true;
   }

   static {
      try {
         INSTANCE = (UnsafeClientLevel)UnsafeAllocator.INSTANCE.newInstance(UnsafeClientLevel.class);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

      CLIENT_LEVEL_DATA = new class_5271(class_1267.field_5801, false, true);
      PALETTED_CONTAINER_FACTORY = new class_11897(
         class_6563.method_74162(new class_2361()),
         class_2246.field_10124.method_9564(),
         null,
         class_6563.method_74165(new class_2361()),
         class_6880.method_40223(null),
         null
      );
   }
}
