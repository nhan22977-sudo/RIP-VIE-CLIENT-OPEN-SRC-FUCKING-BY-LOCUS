package me.drake.vieclient.modules.donut.autobase.executor;

import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2769;

public final class BlockStateMatcher {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static boolean matches(class_2680 worldState, class_2680 expectedState) {
      if (worldState != null && expectedState != null) {
         if (worldState.method_26204() != expectedState.method_26204()) {
            return false;
         } else if (!propertyMatches(worldState, expectedState, class_2741.field_12525)) {
            return false;
         } else if (!propertyMatches(worldState, expectedState, class_2741.field_12481)) {
            return false;
         } else if (!propertyMatches(worldState, expectedState, class_2741.field_12545)) {
            return false;
         } else if (!propertyMatches(worldState, expectedState, class_2741.field_12496)) {
            return false;
         } else if (!propertyMatches(worldState, expectedState, class_2741.field_12529)) {
            return false;
         } else if (!propertyMatches(worldState, expectedState, class_2741.field_12485)) {
            return false;
         } else if (!propertyMatches(worldState, expectedState, class_2741.field_12518)) {
            return false;
         } else if (!propertyMatches(worldState, expectedState, class_2741.field_12533)) {
            return false;
         } else {
            return !propertyMatches(worldState, expectedState, class_2741.field_12483)
               ? false
               : propertyMatches(worldState, expectedState, class_2741.field_12520);
         }
      } else {
         return false;
      }
   }

   private static boolean propertyMatches(class_2680 a, class_2680 b, class_2769 prop) {
      if (b.method_28498(prop)) {
         return !a.method_28498(prop) ? false : a.method_11654(prop).equals(b.method_11654(prop));
      } else {
         return true;
      }
   }
}
