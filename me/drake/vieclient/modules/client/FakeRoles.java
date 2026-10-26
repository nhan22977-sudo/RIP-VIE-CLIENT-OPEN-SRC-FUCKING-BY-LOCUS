package me.drake.vieclient.modules.client;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.StringSetting;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_5250;
import net.minecraft.class_5481;

public class FakeRoles extends Module {
   private static FakeRoles INSTANCE;
   private final BoolSetting enabled_setting = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Enabled",
         "Enable role replacement",
         true
      )
   );
   private final EnumSetting role = (EnumSetting)this.addSetting(
      new EnumSetting(
            "Role",
            "Fake role to display",
            FakeRoles.Role.OWNER,
            FakeRoles.Role.values()
         )
         .withDisplayName(FakeRoles.Role.OWNER, "Owner")
         .withDisplayName(FakeRoles.Role.ADMIN, "Admin (★)")
         .withDisplayName(FakeRoles.Role.DEV, "Dev (★)")
         .withDisplayName(FakeRoles.Role.MOD, "Mod (★)")
         .withDisplayName(FakeRoles.Role.MEDIA, "Media")
         .withDisplayName(FakeRoles.Role.HELPER, "Helper")
         .withDisplayName(FakeRoles.Role.VIP, "VIP")
         .withDisplayName(FakeRoles.Role.YOUTUBER, "YouTuber")
         .withDisplayName(FakeRoles.Role.BUILDER, "Builder")
         .withDisplayName(FakeRoles.Role.FRIEND, "Friend")
         .withDisplayName(FakeRoles.Role.CUSTOM, "Custom")
   );
   private final StringSetting customPrefix = (StringSetting)this.addSetting(
      new StringSetting(
         "CustomPrefix",
         "Custom role prefix text (overrides role)",
         ""
      )
   );
   private final ColorSetting roleColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "RoleColor",
         "Color of the role prefix",
         -43691
      )
   );
   private final ColorSetting nameColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "NameColor",
         "Color of your name after role",
         -1
      )
   );
   private final BoolSetting chatReplace = (BoolSetting)this.addSetting(
      new BoolSetting(
         "ChatReplace",
         "Replace name in chat messages",
         true
      )
   );
   private final BoolSetting hudReplace = (BoolSetting)this.addSetting(
      new BoolSetting(
         "HUDReplace",
         "Replace name in HUD elements",
         true
      )
   );
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public FakeRoles() {
      super(
         "FakeRoles",
         "Replace your name with a fake role prefix in chat",
         Category.PROTECT
      );
      INSTANCE = this;
      this.role.onChanged(newRole -> {
         if (newRole != null) {
            this.roleColor.set(newRole.defaultColor());
         }

         FakeStats.requestRebuild();
      });
      this.roleColor.onChanged(c -> FakeStats.requestRebuild());
      this.nameColor.onChanged(c -> FakeStats.requestRebuild());
      this.customPrefix.onChanged(s -> FakeStats.requestRebuild());
      this.enabled_setting.onChanged(b -> FakeStats.requestRebuild());
   }

   @Override
   public void onEnable() {
      super.onEnable();
      FakeStats.requestRebuild();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      FakeStats.requestRebuild();
   }

   @Override
   public String getInfo() {
      if (isMediaRole()) {
         byte[] var3 = new byte[5];
         var3[0] = (byte)(105);
         var3[1] = (byte)(-1);
         var3[2] = (byte)(120);
         var3[3] = (byte)(-73);
         var3[4] = (byte)(115);
         return $$v_s(var3, 1859038503257755649L);
      }

      String var1 = getEffectivePrefix();
      String var2;
      if (var1.isEmpty()) {
         byte[] var10000 = new byte[3];
         var10000[0] = (byte)(75);
         var10000[1] = (byte)(-4);
         var10000[2] = (byte)(122);
         var2 = $$v_s(var10000, 1859038503257755649L);
      } else {
         var2 = var1;
      }

      return var2;
   }

   public static FakeRoles getInstance() {
      return INSTANCE;
   }

   public static void setFakeRole(FakeRoles.Role newRole) {
      FakeRoles var1 = INSTANCE;
      if (var1 != null && newRole != null) {
         var1.role.set(newRole);
         var1.roleColor.set(newRole.defaultColor());
         var1.customPrefix.set("");
         var1.enabled_setting.set(true);
         if (!var1.isEnabled()) {
            var1.setEnabled(true);
         }

         FakeStats.requestRebuild();
      }
   }

   public static void disableFakeRole() {
      FakeRoles var0 = INSTANCE;
      if (var0 != null) {
         var0.enabled_setting.set(false);
         if (var0.isEnabled()) {
            var0.setEnabled(false);
         }

         FakeStats.requestRebuild();
      }
   }

   public static boolean isActive() {
      FakeRoles var0 = INSTANCE;
      return var0 != null && var0.isEnabled() && (Boolean)var0.enabled_setting.get();
   }

   public static String getVisiblePlayerName() {
      class_310 var0 = class_310.method_1551();
      return var0 != null && var0.field_1724 != null ? var0.field_1724.method_5477().getString() : null;
   }

   public static String getRealPlayerName() {
      return getVisiblePlayerName();
   }

   public static FakeRoles.Role getSelectedRole() {
      FakeRoles var0 = INSTANCE;
      return !isActive() ? null : (FakeRoles.Role)var0.role.get();
   }

   public static boolean isMediaRole() {
      FakeRoles var0 = INSTANCE;
      if (!isActive()) {
         return false;
      }

      String var1 = (String)var0.customPrefix.get();
      return var1 != null && !var1.isBlank() ? false : var0.role.get() == FakeRoles.Role.MEDIA;
   }

   public static String getEffectivePrefix() {
      FakeRoles var0 = INSTANCE;
      if (!isActive()) {
         return "";
      }

      if (isMediaRole()) {
         return "";
      }

      String var1 = (String)var0.customPrefix.get();
      if (var1 != null && !var1.isBlank()) {
         String var2 = var1.trim();
         if (!var2.startsWith("[") && !var2.endsWith("]") && !var2.startsWith("★")) {
            var2 = "[" + var2 + "]";
         }

         return var2;
      } else {
         return ((FakeRoles.Role)var0.role.get()).prefix();
      }
   }

   public static boolean hasMediaPrefix(String input) {
      if (input != null && !input.isBlank()) {
         String var1 = "^(\\s*§[0-9a-fk-orA-FK-OR])*\\s*(\\[MEDIA\\+?\\]|MEDIA\\s*\\+|MEDIA\\b)";
         Pattern var2 = Pattern.compile("(?i)" + var1);
         return var2.matcher(input).find();
      } else {
         return false;
      }
   }

   public static String stripMediaPrefix(String input) {
      if (input != null && !input.isBlank()) {
         String var1 = "^(\\s*§[0-9a-fk-orA-FK-OR])*\\s*(\\[MEDIA\\+?\\]|MEDIA\\s*\\+|MEDIA\\b)\\s*(§[0-9a-fk-orA-FK-OR])*\\s*";
         return input.replaceFirst("(?i)" + var1, "").trim();
      } else {
         return "";
      }
   }

   public static class_2583 getRolePrefixStyle() {
      FakeRoles var0 = INSTANCE;
      if (!isActive()) {
         return class_2583.field_24360;
      }

      int var1 = (Integer)var0.roleColor.get();
      return class_2583.field_24360.method_36139(var1);
   }

   public static class_2583 getRoleNameStyle() {
      FakeRoles var0 = INSTANCE;
      if (!isActive()) {
         return class_2583.field_24360;
      }

      int var1 = (Integer)var0.nameColor.get();
      return class_2583.field_24360.method_36139(var1);
   }

   public static String getPrefixedNameString() {
      String var0 = getEffectivePrefix();
      String var1 = getVisiblePlayerName();
      if (!var0.isEmpty() && var1 != null && !var1.isBlank()) {
         return var0 + " " + var1;
      } else {
         return var1 != null ? var1 : "";
      }
   }

   public static String replaceInString(String input) {
      FakeRoles var1 = INSTANCE;
      if (!isActive() || !(Boolean)var1.chatReplace.get()) {
         return input;
      }

      if (input == null || input.isBlank()) {
         return input;
      }

      if (!isMediaRole()) {
         String var2 = getVisiblePlayerName();
         if (var2 != null && !var2.isBlank()) {
            String var3 = getPrefixedNameString();
            return var3.equals(var2) ? input : input.replace(var2, var3);
         } else {
            return input;
         }
      } else {
         return hasMediaPrefix(input) ? stripMediaPrefix(input) : input;
      }
   }

   public static class_2561 replaceInText(class_2561 original) {
      FakeRoles var1 = INSTANCE;
      if (!isActive() || !(Boolean)var1.chatReplace.get()) {
         return original;
      }

      if (original == null) {
         return null;
      }

      if (isMediaRole()) {
         String var16 = original.getString();
         return (class_2561)(hasMediaPrefix(var16) ? class_2561.method_43470(stripMediaPrefix(var16)) : original);
      }

      String var2 = getVisiblePlayerName();
      if (var2 != null && !var2.isBlank()) {
         String var3 = getPrefixedNameString();
         if (var3.equals(var2)) {
            return original;
         }

         String var4 = original.getString();
         if (!var4.contains(var2)) {
            return original;
         }

         List var5 = collectChunks(original);
         if (var5.isEmpty()) {
            return original;
         }

         String var6 = rebuild(var5);
         if (!var6.contains(var2)) {
            return original;
         }

         int var7 = var6.indexOf(var2);
         if (var7 < 0) {
            return original;
         }

         class_5250 var8 = class_2561.method_43473();
         int var9 = 0;

         for (FakeRoles.StyledChunk var11 : var5) {
            int var12 = var11.start;
            int var13 = var11.end;
            if (var9 < var12) {
               var8.method_10852(class_2561.method_43470(var6.substring(var9, var12)).method_10862(var11.style));
               var9 = var12;
            }

            if (var9 == var7) {
               class_2583 var14 = getRolePrefixStyle();
               var8.method_10852(class_2561.method_43470(getEffectivePrefix()).method_10862(var14));
               var8.method_10852(class_2561.method_43470(" ").method_10862(var14));
               class_2583 var15 = getRoleNameStyle();
               var8.method_10852(class_2561.method_43470(var2).method_10862(var15));
               var9 = var7 + var2.length();
            }
         }

         if (var9 < var6.length()) {
            class_2583 var17 = class_2583.field_24360;

            for (FakeRoles.StyledChunk var19 : var5) {
               if (var9 >= var19.start && var9 < var19.end) {
                  var17 = var19.style;
                  break;
               }
            }

            var8.method_10852(class_2561.method_43470(var6.substring(var9)).method_10862(var17));
         }

         return var8;
      } else {
         return original;
      }
   }

   public static class_5481 replaceOrderedText(class_5481 original) {
      FakeRoles var1 = INSTANCE;
      if (!isActive() || !(Boolean)var1.chatReplace.get()) {
         return original;
      }

      if (original == null) {
         return original;
      }

      if (isMediaRole()) {
         return original;
      }

      String var2 = getVisiblePlayerName();
      if (var2 != null && !var2.isBlank()) {
         List var3 = collectOrderedChars(original);
         StringBuilder var4 = new StringBuilder();

         for (FakeRoles.OrderedChar var6 : var3) {
            var4.append(var6.chars);
         }

         String var18 = var4.toString();
         if (!var18.contains(var2)) {
            return original;
         }

         int var19 = var18.indexOf(var2);
         if (var19 < 0) {
            return original;
         }

         ArrayList var7 = new ArrayList();

         for (int var8 = 0; var8 < var3.size(); var8++) {
            var7.add(((FakeRoles.OrderedChar)var3.get(var8)).globalPos);
         }

         new ArrayList();
         class_2583 var9 = getRolePrefixStyle();
         class_2583 var10 = getRoleNameStyle();
         ArrayList var11 = new ArrayList();

         for (int var12 = 0; var12 < getEffectivePrefix().length(); var12++) {
            var11.add(String.valueOf(getEffectivePrefix().charAt(var12)));
         }

         ArrayList var20 = new ArrayList();

         for (String var14 : var11) {
            var20.add(new FakeRoles.ReplacementPart(var14, var9));
         }

         var20.add(new FakeRoles.ReplacementPart(" ", var9));

         for (int var21 = var19; var21 < var19 + var2.length(); var21++) {
            var20.add(new FakeRoles.ReplacementPart(String.valueOf(var2.charAt(var21 - var19)), var10));
         }

         int var22 = -1;
         int var23 = -1;

         for (int var15 = 0; var15 < var7.size(); var15++) {
            if ((Integer)var7.get(var15) == var19) {
               var22 = var15;
            }

            if ((Integer)var7.get(var15) == var19 + var2.length()) {
               var23 = var15;
            }
         }

         ArrayList var24 = new ArrayList();
         if (var22 > 0) {
            List var16 = var3.subList(0, var22);
            var24.add(concatOrdered(var16));
         }

         for (FakeRoles.ReplacementPart var17 : var20) {
            var24.add(class_5481.method_30747(var17.text, var17.style));
         }

         if (var23 >= 0 && var23 < var3.size()) {
            List var27 = var3.subList(var23, var3.size());
            var24.add(concatOrdered(var27));
         } else if (var23 < 0 && var22 >= 0) {
            List var26 = var3.subList(var22 + var2.length(), var3.size());
            var24.add(concatOrdered(var26));
         }

         return var24.isEmpty() ? original : class_5481.method_30749(var24);
      } else {
         return original;
      }
   }

   private static class_5481 concatOrdered(List chars) {
      if (chars.isEmpty()) {
         return class_5481.method_34905();
      }

      ArrayList var1 = new ArrayList();

      for (FakeRoles.OrderedChar var3 : chars) {
         var1.add(class_5481.method_30747(var3.chars, var3.style));
      }

      return class_5481.method_30749(var1);
   }

   private static List collectChunks(class_2561 text) {
      ArrayList var1 = new ArrayList();
      int[] var2 = new int[]{0};
      text.method_27658((style, part) -> {
         if (part != null && !part.isEmpty()) {
            int var4 = var2[0];
            int var5 = var4 + part.length();
            var1.add(new FakeRoles.StyledChunk(part, style, var4, var5));
            var2[0] = var5;
         }

         return Optional.empty();
      }, class_2583.field_24360);
      return var1;
   }

   private static String rebuild(List chunks) {
      StringBuilder var1 = new StringBuilder();

      for (FakeRoles.StyledChunk var3 : chunks) {
         var1.append(var3.text);
      }

      return var1.toString();
   }

   private static List collectOrderedChars(class_5481 ot) {
      ArrayList var1 = new ArrayList();
      int[] var2 = new int[]{0};
      ot.accept((index, style, codePoint) -> {
         var1.add(new FakeRoles.OrderedChar(new String(Character.toChars(codePoint)), style, (int)(var2[0]++)));
         return true;
      });
      return var1;
   }

   private static int codePointCount(String s) {
      return (int)s.codePoints().count();
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

   private record OrderedChar(String chars, class_2583 style, int globalPos) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record ReplacementPart(String text, class_2583 style) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum Role {
      OWNER("[OWNER]", -43691),
      ADMIN("★", -22016),
      DEV("★", -5745161),
      MOD("★", -11141291),
      MEDIA("", -65366),
      HELPER("[HELPER]", -11141291),
      VIP("[VIP]", -43521),
      YOUTUBER("[YT]", -21846),
      BUILDER("[BUILDER]", -11141121),
      FRIEND("[FRIEND]", -5592406),
      CUSTOM("[CUSTOM]", -1);

      private final String prefix;
      private final int defaultColor;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Role(String prefix, int defaultColor) {
         this.prefix = prefixx;
         this.defaultColor = defaultColorx;
      }

      public String prefix() {
         return this.prefix;
      }

      public int defaultColor() {
         return this.defaultColor;
      }

      // $VF: synthetic method
      private static FakeRoles.Role[] $values() {
         FakeRoles.Role[] var10000 = new FakeRoles.Role[11];
         var10000[0] = OWNER;
         var10000[1] = ADMIN;
         var10000[2] = DEV;
         var10000[3] = MOD;
         var10000[4] = MEDIA;
         var10000[5] = HELPER;
         var10000[6] = VIP;
         var10000[7] = YOUTUBER;
         var10000[8] = BUILDER;
         var10000[9] = FRIEND;
         var10000[10] = CUSTOM;
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

   private record StyledChunk(String text, class_2583 style, int start, int end) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
