package me.drake.vieclient.gui.nvg.adapter.setting;

import java.util.function.Supplier;

public abstract class Setting {
   protected CharSequence name;
   public CharSequence description;
   private boolean visible = true;
   private Supplier descriptionSupplier;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public Setting(CharSequence name) {
      this.name = namex;
   }

   public void getDescription(CharSequence a) {
      this.name = a;
   }

   public CharSequence getName() {
      return this.name;
   }

   public CharSequence getDescription() {
      if (this.descriptionSupplier != null) {
         try {
            CharSequence var1 = (CharSequence)this.descriptionSupplier.get();
            if (var1 != null && var1.length() > 0) {
               return var1;
            }
         } catch (Throwable var2) {
         }
      }

      return this.description;
   }

   public Setting setDescription(CharSequence description) {
      this.description = descriptionx;
      return this;
   }

   public Setting setDescriptionSupplier(Supplier supplier) {
      this.descriptionSupplier = supplier;
      return this;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public Setting setVisible(boolean visible) {
      this.visible = visiblex;
      return this;
   }

   public void resetValue() {
   }
}
