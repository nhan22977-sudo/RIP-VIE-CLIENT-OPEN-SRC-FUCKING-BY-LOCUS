package me.drake.vieclient.gui.nvg.adapter.setting;

import java.awt.Color;

public class ColorSetting extends Setting {
   private final me.drake.vieclient.core.setting.ColorSetting wrapped;
   private Color value;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ColorSetting(CharSequence name, Color defaultValue) {
      super(name);
      this.wrapped = null;
      this.value = defaultValue;
   }

   public ColorSetting(me.drake.vieclient.core.setting.ColorSetting wrapped) {
      super(wrappedx.getName());
      this.wrapped = wrappedx;
      this.value = fromArgb((Integer)wrappedx.get());
   }

   private static int argb(Color c) {
      return (c.getAlpha() & (255)) << (24)
         | (c.getRed() & (255)) << (16)
         | (c.getGreen() & (255)) << (8)
         | c.getBlue() & (255);
   }

   private static Color fromArgb(int a) {
      return new Color(
         a >> (16) & (255),
         a >> (8) & (255),
         a & (255),
         a >> (24) & (255)
      );
   }

   public Color getValue() {
      return this.wrapped != null ? fromArgb((Integer)this.wrapped.get()) : this.value;
   }

   public void setValue(Color v) {
      if (this.wrapped != null) {
         this.wrapped.set(argb(v));
      } else {
         this.value = v;
      }
   }

   public Color getDefaultValue() {
      return this.wrapped != null ? fromArgb((Integer)this.wrapped.getDefaultValue()) : this.value;
   }

   @Override
   public void resetValue() {
      if (this.wrapped != null) {
         Color var1 = this.getDefaultValue();
         this.wrapped.set(argb(var1));
         this.value = var1;
      }
   }

   public void setValue(int red, int green, int blue, int alpha) {
      this.setValue(new Color(red, green, blue, alpha));
   }

   public ColorSetting setDescription(CharSequence s) {
      super.setDescription(s);
      return this;
   }

   public me.drake.vieclient.core.setting.ColorSetting raw() {
      return this.wrapped;
   }
}
