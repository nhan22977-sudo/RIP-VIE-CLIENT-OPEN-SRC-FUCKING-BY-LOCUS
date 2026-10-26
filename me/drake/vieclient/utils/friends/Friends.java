package me.drake.vieclient.utils.friends;

import net.minecraft.class_1657;

public class Friends {
   private static final Friends INSTANCE = new Friends();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static Friends get() {
      return INSTANCE;
   }

   public boolean shouldAttack(class_1657 player) {
      return true;
   }

   public boolean isFriend(String name) {
      return false;
   }
}
