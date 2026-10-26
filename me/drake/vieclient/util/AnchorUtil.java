package me.drake.vieclient.util;

import net.minecraft.class_1937;
import net.minecraft.class_2248;
import net.minecraft.class_2338;

public final class AnchorUtil {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private AnchorUtil() {
   }

   public static boolean isBlockAtPosition(class_1937 world, class_2338 pos, class_2248 block) {
      return world != null && pos != null && block != null ? world.method_8320(pos).method_26204() == block : false;
   }
}
