package me.drake.vieclient.modules.donut.chunkdebug;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_6606;

public record LightPayload(BitSet initedSky, BitSet uninitedSky, List<byte[]> skyNibbles, BitSet initedBlock, BitSet uninitedBlock, List<byte[]> blockNibbles) {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static LightPayload copyOf(class_6606 data) {
      return data == null
         ? null
         : new LightPayload(
            copy(data.method_38601()),
            copy(data.method_38604()),
            copyNibbles(data.method_38606()),
            copy(data.method_38608()),
            copy(data.method_38609()),
            copyNibbles(data.method_38610())
         );
   }

   private static BitSet copy(BitSet source) {
      return source == null ? new BitSet() : (BitSet)source.clone();
   }

   static List copyNibbles(List source) {
      if (source != null && !source.isEmpty()) {
         ArrayList var1 = new ArrayList(source.size());

         for (byte[] var3 : source) {
            var1.add(var3 == null ? null : (byte[])var3.clone());
         }

         return Collections.unmodifiableList(var1);
      } else {
         return List.of();
      }
   }
}
