package me.drake.vieclient.modules.donut.autobase.schematic;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.TreeSet;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_2487;
import net.minecraft.class_2499;
import net.minecraft.class_2505;
import net.minecraft.class_2507;

public final class LitematicaParser {
   private static final int MAX_REGIONS = 256;
   private static final long MAX_VOLUME = 10000000L;
   private static final long MAX_NBT_BYTES = 268435456L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static SchematicData parse(File file) throws Exception {
      if (file.exists() && file.canRead()) {
         try (BufferedInputStream var1 = new BufferedInputStream(new FileInputStream(file))) {
            return parse(var1, file.getName());
         }
      } else {
         throw new IllegalArgumentException("Schematic file cannot be read: " + file.getAbsolutePath());
      }
   }

   public static SchematicData parse(InputStream inputStream, String schematicName) throws Exception {
      byte[] var3 = inputStream.readAllBytes();

      class_2487 var2;
      try {
         var2 = class_2507.method_10629(new ByteArrayInputStream(var3), class_2505.method_53899(268435456L));
      } catch (Exception var11) {
         try (DataInputStream var5 = new DataInputStream(new ByteArrayInputStream(var3))) {
            var2 = class_2507.method_10625(var5, class_2505.method_53899(268435456L));
         } catch (Exception var10) {
            throw new IllegalArgumentException("Failed to decode NBT: " + var11.getMessage(), var11);
         }
      }

      return parseFromCompound(var2, schematicName);
   }

   public static SchematicData parseFromCompound(class_2487 root, String schematicName) {
      int var2 = root.method_68083("Version", 0);
      int var3 = root.method_68083("SubVersion", 0);
      int var4 = root.method_68083(
         "MinecraftDataVersion", 0
      );
      String var5 = schematicName;
      String var6 = "Unknown";
      class_2487 var7 = root.method_68568("Metadata");
      if (!var7.method_33133()) {
         var5 = var7.method_68564("Name", var5);
         var6 = var7.method_68564("Author", var6);
      }

      if (!root.method_10545("Regions")) {
         throw new IllegalArgumentException(
            "Invalid .litematic: missing 'Regions' compound"
         );
      }

      class_2487 var8 = root.method_68568("Regions");
      TreeSet var9 = new TreeSet(var8.method_10541());
      if (var9.size() > 256) {
         throw new IllegalStateException("Schematic exceeds maximum region limit: " + var9.size() + " > 256");
      }

      LinkedHashMap var10 = new LinkedHashMap();
      LinkedHashMap var11 = new LinkedHashMap();
      LinkedHashSet var12 = new LinkedHashSet();
      int var13 = Integer.MAX_VALUE;
      int var14 = Integer.MAX_VALUE;
      int var15 = Integer.MAX_VALUE;
      int var16 = Integer.MIN_VALUE;
      int var17 = Integer.MIN_VALUE;
      int var18 = Integer.MIN_VALUE;

      for (String var20 : var9) {
         class_2487 var21 = var8.method_68568(var20);
         class_2487 var22 = var21.method_68568("Position");
         int var23 = var22.method_68083("x", 0);
         int var24 = var22.method_68083("y", 0);
         int var25 = var22.method_68083("z", 0);
         class_2487 var26 = var21.method_68568("Size");
         int var27 = var26.method_68083("x", 0);
         int var28 = var26.method_68083("y", 0);
         int var29 = var26.method_68083("z", 0);
         int var30 = Math.abs(var27);
         int var31 = Math.abs(var28);
         int var32 = Math.abs(var29);
         if (var30 != 0 && var31 != 0 && var32 != 0) {
            long var33 = (long)var30 * var31 * var32;
            if (var33 > 10000000L) {
               throw new IllegalStateException("Region '" + var20 + "' exceeds volume limit: " + var33 + " > 10000000");
            }

            int var35 = var27 > 0 ? var27 - 1 : var27 + 1;
            int var36 = var28 > 0 ? var28 - 1 : var28 + 1;
            int var37 = var29 > 0 ? var29 - 1 : var29 + 1;
            int var38 = var23 + var35;
            int var39 = var24 + var36;
            int var40 = var25 + var37;
            int var41 = Math.min(var23, var38);
            int var42 = Math.min(var24, var39);
            int var43 = Math.min(var25, var40);
            class_2499 var44 = var21.method_68569(
               "BlockStatePalette"
            );
            ArrayList var45 = new ArrayList(var44.size());

            for (int var46 = 0; var46 < var44.size(); var46++) {
               class_2487 var47 = var44.method_68582(var46);
               String var48 = var47.method_68564(
                  "Name",
                  "minecraft:air"
               );
               HashMap var49 = new HashMap();
               class_2487 var50 = var47.method_68568("Properties");
               if (!var50.method_33133()) {
                  for (String var52 : var50.method_10541()) {
                     var49.put(var52, var50.method_68564(var52, ""));
                  }
               }

               var45.add(new LitematicaParser.RawPaletteEntry(var48, var49));
            }

            long[] var67 = var21.method_10565("BlockStates").orElse(new long[0]);
            int var68 = LitematicaBitArray.calculateBitsPerEntry(var45.size());
            LitematicaBitArray var69 = new LitematicaBitArray(var68, var33, var67);

            for (int var70 = 0; var70 < var31; var70++) {
               for (int var71 = 0; var71 < var32; var71++) {
                  for (int var72 = 0; var72 < var30; var72++) {
                     long var73 = (long)var70 * var30 * var32 + (long)var71 * var30 + var72;
                     int var54 = var69.getAt(var73);
                     LitematicaParser.RawPaletteEntry var55 = var54 >= 0 && var54 < var45.size()
                        ? (LitematicaParser.RawPaletteEntry)var45.get(var54)
                        : new LitematicaParser.RawPaletteEntry(
                           "minecraft:air", Collections.emptyMap()
                        );
                     int var56 = var41 + var72;
                     int var57 = var42 + var70;
                     int var58 = var43 + var71;
                     class_2338 var59 = new class_2338(var56, var57, var58);
                     SchematicBlockEntry var60 = SchematicBlockEntry.of(var59, var55.name(), var55.properties(), var20);
                     SchematicBlockEntry var61 = (SchematicBlockEntry)var10.get(var59);
                     if (var61 == null || var61.isAir() && !var60.isAir()) {
                        var10.put(var59, var60);
                     }

                     var13 = Math.min(var13, var56);
                     var14 = Math.min(var14, var57);
                     var15 = Math.min(var15, var58);
                     var16 = Math.max(var16, var56);
                     var17 = Math.max(var17, var57);
                     var18 = Math.max(var18, var58);
                  }
               }
            }
         }
      }

      int var62 = 0;

      for (SchematicBlockEntry var65 : var10.values()) {
         if (!var65.isAir()) {
            var62++;
            if (var65.itemQuantity() > 0 && var65.requiredItemId() != null) {
               var11.merge(var65.requiredItemId(), var65.itemQuantity(), Integer::sum);
            }
         }
      }

      class_2382 var64 = var13 <= var16 ? new class_2382(var16 - var13 + 1, var17 - var14 + 1, var18 - var15 + 1) : class_2382.field_11176;
      int var66 = var64.method_10263() * var64.method_10264() * var64.method_10260();
      return new SchematicData(var5, var6, var2, var4, var64, var66, var62, var10, var11, new ArrayList(var12));
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

   private record RawPaletteEntry(String name, Map<String, String> properties) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
