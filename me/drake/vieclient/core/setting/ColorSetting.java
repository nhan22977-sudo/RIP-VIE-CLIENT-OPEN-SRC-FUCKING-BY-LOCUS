package me.drake.vieclient.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

public class ColorSetting extends Setting {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ColorSetting(String name, String description, int defaultColor) {
      super(name, description, defaultColor);
   }

   public int getAlpha() {
      return (Integer)this.get() >> (24) & (255);
   }

   public int getRed() {
      return (Integer)this.get() >> (16) & (255);
   }

   public int getGreen() {
      return (Integer)this.get() >> (8) & (255);
   }

   public int getBlue() {
      return (Integer)this.get() & (255);
   }

   public void setRGBA(int r, int g, int b, int a) {
      this.set(
         (a & (255)) << (24)
            | (r & (255)) << (16)
            | (g & (255)) << (8)
            | b & (255)
      );
   }

   public float[] asFloatArray() {
      float[] var10000 = new float[4];
      var10000[0] = this.getRed() / 255.0F;
      var10000[1] = this.getGreen() / 255.0F;
      var10000[2] = this.getBlue() / 255.0F;
      var10000[3] = this.getAlpha() / 255.0F;
      return var10000;
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive(Integer.toHexString((Integer)this.get()));
   }

   @Override
   public void fromJson(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         try {
            this.set((int)Long.parseLong(json.getAsString(), 16));
         } catch (NumberFormatException var3) {
         }
      }
   }
}
