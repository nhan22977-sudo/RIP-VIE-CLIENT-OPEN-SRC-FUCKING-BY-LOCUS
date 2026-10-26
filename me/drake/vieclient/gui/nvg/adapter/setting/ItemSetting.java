package me.drake.vieclient.gui.nvg.adapter.setting;

import net.minecraft.class_1792;

public class ItemSetting extends Setting {
   private class_1792 value;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ItemSetting(CharSequence name, class_1792 value) {
      super(name);
      this.value = valuex;
   }

   public class_1792 getItem() {
      return this.value;
   }

   public void setItem(class_1792 a) {
      this.value = a;
   }

   public class_1792 getDefaultValue() {
      return null;
   }

   @Override
   public void resetValue() {
      this.value = null;
   }
}
