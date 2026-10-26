package me.drake.vieclient.modules.donut.autobase.schematic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.class_1792;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_2470;
import net.minecraft.class_2680;

public record SchematicData(
   String name,
   String author,
   int version,
   int dataVersion,
   class_2382 dimensions,
   int totalVolume,
   int totalNonAirBlocks,
   Map<class_2338, SchematicBlockEntry> blocks,
   Map<String, Integer> materialCounts,
   List<String> unknownBlockNames
) {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SchematicData(
      String name,
      String author,
      int version,
      int dataVersion,
      class_2382 dimensions,
      int totalVolume,
      int totalNonAirBlocks,
      Map blocks,
      Map materialCounts,
      List unknownBlockNames
   ) {
      blocks = Collections.unmodifiableMap(new LinkedHashMap(blocks));
      materialCounts = Collections.unmodifiableMap(new LinkedHashMap(materialCounts));
      unknownBlockNames = Collections.unmodifiableList(new ArrayList(unknownBlockNames));
      this.name = name;
      this.author = author;
      this.version = version;
      this.dataVersion = dataVersion;
      this.dimensions = dimensions;
      this.totalVolume = totalVolume;
      this.totalNonAirBlocks = totalNonAirBlocks;
      this.blocks = blocks;
      this.materialCounts = materialCounts;
      this.unknownBlockNames = unknownBlockNames;
   }

   public SchematicData transform(SchematicTransform transform) {
      if (transform != null && !transform.equals(SchematicTransform.IDENTITY)) {
         LinkedHashMap var2 = new LinkedHashMap();
         LinkedHashMap var3 = new LinkedHashMap();
         class_2382 var4;
         if (transform.rotation() != class_2470.field_11463 && transform.rotation() != class_2470.field_11465) {
            var4 = this.dimensions;
         } else {
            var4 = new class_2382(this.dimensions.method_10260(), this.dimensions.method_10264(), this.dimensions.method_10263());
         }

         for (Entry var6 : this.blocks.entrySet()) {
            class_2338 var7 = (class_2338)var6.getKey();
            SchematicBlockEntry var8 = (SchematicBlockEntry)var6.getValue();
            class_2338 var9 = transform.transformPos(var7, this.dimensions);
            class_2680 var10 = transform.transformState(var8.state());
            Map var11 = transform.transformProperties(var8.properties());
            SchematicBlockEntry var12 = new SchematicBlockEntry(
               var9,
               var8.blockId(),
               var11,
               var10,
               var8.requiredItemId(),
               var8.requiredItem(),
               var8.itemQuantity(),
               var8.isAir(),
               var8.isDependentHalf(),
               var8.regionName()
            );
            var2.put(var9, var12);
            if (!var12.isAir() && var12.itemQuantity() > 0) {
               var3.merge(var12.requiredItemId(), var12.itemQuantity(), Integer::sum);
            }
         }

         return new SchematicData(
            this.name, this.author, this.version, this.dataVersion, var4, this.totalVolume, this.totalNonAirBlocks, var2, var3, this.unknownBlockNames
         );
      } else {
         return this;
      }
   }

   public Map getResolvedMaterialCounts() {
      LinkedHashMap var1 = new LinkedHashMap();

      for (Entry var3 : this.materialCounts.entrySet()) {
         class_1792 var4 = SchematicBlockEntry.resolveItem((String)var3.getKey());
         if (var4 != null) {
            var1.merge(var4, (Integer)var3.getValue(), Integer::sum);
         }
      }

      return var1;
   }
}
