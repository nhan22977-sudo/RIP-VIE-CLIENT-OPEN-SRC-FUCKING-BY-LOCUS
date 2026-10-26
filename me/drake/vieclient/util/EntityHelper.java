package me.drake.vieclient.util;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;

public class EntityHelper {
   private static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private EntityHelper() {
   }

   public static List getPlayers() {
      ArrayList var0 = new ArrayList();
      if (mc.field_1687 == null) {
         return var0;
      }

      for (class_1297 var2 : mc.field_1687.method_18112()) {
         if (var2 instanceof class_1657 var3) {
            var0.add(var3);
         }
      }

      return var0;
   }
}
