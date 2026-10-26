package me.drake.vieclient.modules.client.hud;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class HudLayoutCodec {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private HudLayoutCodec() {
   }

   public static JsonObject encode(Map positions, Map scales) {
      JsonObject var2 = new JsonObject();
      JsonObject var3 = new JsonObject();
      if (positions != null) {
         positions.forEach((id, value) -> {
            if (id != null && value != null && value.length >= (2)) {
               if (Float.isFinite(value[0]) && Float.isFinite(value[1])) {
                  JsonArray var3x = new JsonArray();
                  var3x.add(value[0]);
                  var3x.add(value[1]);
                  var3.add(id, var3x);
               }
            }
         });
      }

      JsonObject var4 = new JsonObject();
      if (scales != null) {
         scales.forEach((id, value) -> {
            if (id != null && value != null) {
               var4.addProperty(id, HudEditorGeometry.clampScale(value));
            }
         });
      }

      byte[] var10001 = new byte[9];
      var10001[0] = (byte)(79);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(111);
      var10001[3] = (byte)(-73);
      var10001[4] = (byte)(-17);
      var10001[5] = (byte)(-118);
      var10001[6] = (byte)(-79);
      var10001[7] = (byte)(104);
      var10001[8] = (byte)(36);
      var2.add($$v_s(var10001, 3376922513085026330L), var3);
      var10001 = new byte[6];
      var10001[0] = (byte)(76);
      var10001[1] = (byte)(-7);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-78);
      var10001[4] = (byte)(-2);
      var10001[5] = (byte)(-112);
      var2.add($$v_s(var10001, 3376922513085026330L), var4);
      return var2;
   }

   public static HudLayoutCodec.Layout decode(JsonObject root) {
      HashMap var1 = new HashMap();
      HashMap var2 = new HashMap();
      if (root == null) {
         return new HudLayoutCodec.Layout(var1, var2);
      }

      byte[] var10001 = new byte[9];
      var10001[0] = (byte)(79);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(111);
      var10001[3] = (byte)(-73);
      var10001[4] = (byte)(-17);
      var10001[5] = (byte)(-118);
      var10001[6] = (byte)(-79);
      var10001[7] = (byte)(104);
      var10001[8] = (byte)(36);
      JsonObject var3 = object(root, $$v_s(var10001, 3376922513085026330L));
      if (var3 != null) {
         for (Entry var5 : var3.entrySet()) {
            try {
               JsonArray var6 = ((JsonElement)var5.getValue()).getAsJsonArray();
               if (var6.size() >= (2)) {
                  float var7 = var6.get(0).getAsFloat();
                  float var8 = var6.get(1).getAsFloat();
                  if (Float.isFinite(var7) && Float.isFinite(var8)) {
                     String var15 = (String)var5.getKey();
                     float[] var10002 = new float[2];
                     var10002[0] = var7;
                     var10002[1] = var8;
                     var1.put(var15, var10002);
                  }
               }
            } catch (RuntimeException var10) {
            }
         }
      }

      var10001 = new byte[6];
      var10001[0] = (byte)(76);
      var10001[1] = (byte)(-7);
      var10001[2] = (byte)(125);
      var10001[3] = (byte)(-78);
      var10001[4] = (byte)(-2);
      var10001[5] = (byte)(-112);
      JsonObject var11 = object(root, $$v_s(var10001, 3376922513085026330L));
      if (var11 != null) {
         for (Entry var13 : var11.entrySet()) {
            float var14 = 1.0F;

            try {
               var14 = ((JsonElement)var13.getValue()).getAsFloat();
            } catch (RuntimeException var9) {
            }

            var2.put((String)var13.getKey(), HudEditorGeometry.clampScale(var14));
         }
      }

      return new HudLayoutCodec.Layout(var1, var2);
   }

   private static JsonObject object(JsonObject root, String key) {
      try {
         JsonElement var2 = root.get(key);
         return var2 != null && var2.isJsonObject() ? var2.getAsJsonObject() : null;
      } catch (RuntimeException var3) {
         return null;
      }
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   public record Layout(Map<String, float[]> positions, Map<String, Float> scales) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
