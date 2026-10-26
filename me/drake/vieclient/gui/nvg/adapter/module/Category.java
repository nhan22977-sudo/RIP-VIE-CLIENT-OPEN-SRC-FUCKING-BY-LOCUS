package me.drake.vieclient.gui.nvg.adapter.module;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public enum Category {
   COMBAT("Combat", false),
   PLAYER("Player", true),
   RENDER("Render", true),
   MISC("Misc", true),
   DONUT("Donut", true),
   BASE_FINDING("Base Finding", true),
   CUSTOMIZE("Customize", true),
   PROTECT("Protect", true),
   CONFIG("Config", true),
   CLIENT("Client", false);

   public final CharSequence name;
   private final boolean visible;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   Category(final CharSequence name, boolean visible) {
      this.name = namex;
      this.visible = visiblex;
   }

   public static Category[] visibleValues() {
      return Arrays.stream(values()).filter(category -> category.visible).toArray(Category[]::new);
   }

   // $VF: synthetic method
   private static Category[] $values() {
      Category[] var10000 = new Category[10];
      var10000[0] = COMBAT;
      var10000[1] = PLAYER;
      var10000[2] = RENDER;
      var10000[3] = MISC;
      var10000[4] = DONUT;
      var10000[5] = BASE_FINDING;
      var10000[6] = CUSTOMIZE;
      var10000[7] = PROTECT;
      var10000[8] = CONFIG;
      var10000[9] = CLIENT;
      return var10000;
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
}
