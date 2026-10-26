package me.drake.vieclient.modules.donut.autobase.schematic;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.class_1792;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2689;
import net.minecraft.class_2769;
import net.minecraft.class_2960;
import net.minecraft.class_7923;

public record SchematicBlockEntry(
   class_2338 pos,
   String blockId,
   Map<String, String> properties,
   class_2680 state,
   String requiredItemId,
   class_1792 requiredItem,
   int itemQuantity,
   boolean isAir,
   boolean isDependentHalf,
   String regionName
) {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SchematicBlockEntry(
      class_2338 pos,
      String blockId,
      Map properties,
      class_2680 state,
      String requiredItemId,
      class_1792 requiredItem,
      int itemQuantity,
      boolean isAir,
      boolean isDependentHalf,
      String regionName
   ) {
      properties = Collections.unmodifiableMap(properties != null ? properties : Collections.emptyMap());
      this.pos = pos;
      this.blockId = blockId;
      this.properties = properties;
      this.state = state;
      this.requiredItemId = requiredItemId;
      this.requiredItem = requiredItem;
      this.itemQuantity = itemQuantity;
      this.isAir = isAir;
      this.isDependentHalf = isDependentHalf;
      this.regionName = regionName;
   }

   public static SchematicBlockEntry of(class_2338 pos, String blockId, Map properties, String regionName) {
      String var4 = blockIdx != null
         ? blockIdx.trim().toLowerCase()
         : "minecraft:air";
      if (!var4.contains(":")) {
         var4 = "minecraft:" + var4;
      }

      boolean var5 = isAirId(var4);
      byte var6 = 0;
      boolean var7 = false;
      String var8 = var4;
      if (!var5) {
         var6 = 1;
         String var9 = propertiesx != null ? (String)propertiesx.get("type") : null;
         if ("double".equalsIgnoreCase(var9)) {
            var6 = 2;
         }

         String var10 = propertiesx != null ? (String)propertiesx.get("half") : null;
         if ("upper".equalsIgnoreCase(var10)
            && var4.contains("door")) {
            var6 = 0;
            var7 = true;
         }

         String var11 = propertiesx != null ? (String)propertiesx.get("part") : null;
         if ("head".equalsIgnoreCase(var11)
            && var4.contains("bed")) {
            var6 = 0;
            var7 = true;
         }
      }

      class_2680 var13 = null;
      class_1792 var14 = null;

      try {
         var13 = resolveState(var4, propertiesx);
         if (!var5) {
            var14 = resolveItem(var8);
         }
      } catch (Throwable var12) {
      }

      return new SchematicBlockEntry(
         posx,
         var4,
         propertiesx,
         var13,
         var5 ? "minecraft:air" : var8,
         var14,
         var6,
         var5,
         var7,
         regionNamex
      );
   }

   private static boolean isAirId(String id) {
      return "minecraft:air".equals(id)
         || "minecraft:cave_air".equals(id)
         || "minecraft:void_air".equals(id)
         || "minecraft:structure_void"
            .equals(id);
   }

   public static class_2680 resolveState(String blockId, Map props) {
      class_2960 var2 = class_2960.method_12829(blockIdx);
      if (var2 == null) {
         return null;
      }

      class_2248 var3 = (class_2248)class_7923.field_41175.method_63535(var2);
      if (var3 == null) {
         return null;
      }

      class_2680 var4 = var3.method_9564();
      if (props != null && !props.isEmpty()) {
         class_2689 var5 = var3.method_9595();

         for (Entry var7 : props.entrySet()) {
            class_2769 var8 = var5.method_11663((String)var7.getKey());
            if (var8 != null) {
               var4 = applyProperty(var4, var8, (String)var7.getValue());
            }
         }
      }

      return var4;
   }

   public static class_1792 resolveItem(String itemId) {
      class_2960 var1 = class_2960.method_12829(itemId);
      return var1 == null ? null : (class_1792)class_7923.field_41178.method_63535(var1);
   }

   private static class_2680 applyProperty(class_2680 state, class_2769 property, String valueStr) {
      Optional var3 = property.method_11900(valueStr);
      return var3.isPresent() ? (class_2680)statex.method_11657(property, (Comparable)var3.get()) : statex;
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
