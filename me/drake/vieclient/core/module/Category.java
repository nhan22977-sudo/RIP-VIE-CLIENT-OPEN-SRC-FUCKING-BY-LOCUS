package me.drake.vieclient.core.module;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.CoreEntry;

public enum Category {
   COMBAT("Combat", "⚔"),
   MOVEMENT("Movement", "????"),
   PLAYER("Player", "????"),
   RENDER("Render", "????"),
   WORLD("World", "????"),
   EXPLOIT("Exploit", "???"),
   DONUT_SMP("DonutSMP", "????"),
   SMP_UTILITIES(
      "SMP Utilities",
      "???"
   ),
   BASE_FINDING(
      "Base Finding",
      "????"
   ),
   CUSTOMIZE("Customize", "🎨"),
   PROTECT("Protect", "🛡"),
   CONFIG("Config", "⚙"),
   CLIENT("Client", "💻");

   private final String displayName;
   private final String icon;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   Category(String displayName, String icon) {
      this.displayName = displayNamex;
      this.icon = iconx;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getIcon() {
      return this.icon;
   }

   public boolean isProtected() {
      return !CoreEntry.isAuthEnabled() ? false : this != PROTECT && this != CONFIG && this != CLIENT && this != CUSTOMIZE;
   }

   public String getCapabilityName() {
      return switch (this) {
         case COMBAT -> "combat";
         case MOVEMENT -> "movement";
         case PLAYER -> "player";
         case RENDER -> "render";
         case WORLD -> "world";
         case EXPLOIT -> "exploit";
         case DONUT_SMP, SMP_UTILITIES, BASE_FINDING -> "donut";
         default -> "client";
      };
   }

   // $VF: synthetic method
   private static Category[] $values() {
      Category[] var10000 = new Category[13];
      var10000[0] = COMBAT;
      var10000[1] = MOVEMENT;
      var10000[2] = PLAYER;
      var10000[3] = RENDER;
      var10000[4] = WORLD;
      var10000[5] = EXPLOIT;
      var10000[6] = DONUT_SMP;
      var10000[7] = SMP_UTILITIES;
      var10000[8] = BASE_FINDING;
      var10000[9] = CUSTOMIZE;
      var10000[10] = PROTECT;
      var10000[11] = CONFIG;
      var10000[12] = CLIENT;
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
