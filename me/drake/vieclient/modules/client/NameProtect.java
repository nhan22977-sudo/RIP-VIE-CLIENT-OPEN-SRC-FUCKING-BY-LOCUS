package me.drake.vieclient.modules.client;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.StringSetting;
import net.minecraft.class_2561;
import net.minecraft.class_640;

public class NameProtect extends Module {
   private final EnumSetting mode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Mode",
         "How to hide names",
         NameProtect.Mode.BLANK,
         NameProtect.Mode.values()
      )
   );
   private final StringSetting customReplacement = (StringSetting)this.addSetting(
      new StringSetting(
         "Replacement",
         "Text shown instead of hidden names",
         "???"
      )
   );
   private final BoolSetting hideOthers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "HideOthers",
         "Replace every other player's name in chat, death messages, and HUDs.",
         true
      )
   );
   private final BoolSetting hideNametags = (BoolSetting)this.addSetting(
      new BoolSetting(
         "HideNametags",
         "Apply HideOthers to in-world player nametags.",
         true
      )
   );
   private String[] protectedNames = new String[0];
   private static NameProtect INSTANCE;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public NameProtect() {
      super(
         "NameProtect",
         "Hides customer names in chat and nametags",
         Category.PROTECT
      );
      INSTANCE = this;
      this.mode.onChanged(m -> FakeStats.requestRebuild());
      this.customReplacement.onChanged(s -> FakeStats.requestRebuild());
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

   public static NameProtect get() {
      return INSTANCE;
   }

   public void setProtectedNames(String[] names) {
      if (names != null && names.length != 0) {
         String[] var2 = new String[names.length];
         int var3 = 0;

         for (String var7 : names) {
            String var8 = normalizeName(var7);
            if (var8 != null && !var8.isBlank()) {
               boolean var9 = false;

               for (int var10 = 0; var10 < var3; var10++) {
                  if (var8.equals(var2[var10])) {
                     var9 = true;
                     break;
                  }
               }

               if (!var9) {
                  var2[var3++] = var8;
               }
            }
         }

         this.protectedNames = Arrays.copyOf(var2, var3);
      } else {
         this.protectedNames = new String[0];
      }
   }

   public String[] getProtectedNames() {
      return this.protectedNames;
   }

   static String resolvePlayerName(boolean enabled, NameProtect.Mode mode, String configuredReplacement, String originalName) {
      if (!enabled) {
         return originalName;
      }

      if (modex == NameProtect.Mode.BLANK) {
         return "";
      }

      String var4;
      if (configuredReplacement != null && !configuredReplacement.isBlank()) {
         var4 = configuredReplacement;
      } else {
         byte[] var10000 = new byte[3];
         var10000[0] = (byte)(8);
         var10000[1] = (byte)(-91);
         var10000[2] = (byte)(35);
         var4 = $$v_s(var10000, -2809534442904498158L);
      }

      return var4;
   }

   static boolean shouldMaskPlayerName(
      boolean enabled, boolean self, boolean explicitlyProtected, boolean hideOthers, boolean hideNametags, boolean nametagSurface
   ) {
      if (!enabled) {
         return false;
      } else {
         return !self && !explicitlyProtected ? hideOthersx && (!nametagSurface || hideNametagsx) : true;
      }
   }

   static String scrubKnownNames(String text, String replacement, Set knownNames) {
      if (text != null && !text.isBlank() && knownNames != null && !knownNames.isEmpty()) {
         StringBuilder var3 = new StringBuilder(text.length());
         int var4 = 0;

         while (var4 < text.length()) {
            int var5 = text.codePointAt(var4);
            int var6 = Character.charCount(var5);
            if (!isUsernameCharacter(var5)) {
               var3.appendCodePoint(var5);
               var4 += var6;
            } else {
               int var7 = var4;
               var4 += var6;

               while (true) {
                  if (var4 < text.length()) {
                     int var8 = text.codePointAt(var4);
                     if (isUsernameCharacter(var8)) {
                        var4 += Character.charCount(var8);
                        continue;
                     }
                  }

                  String var9 = text.substring(var7, var4);
                  if (knownNames.contains(var9.toLowerCase(Locale.ROOT))) {
                     var3.append(replacement);
                  } else {
                     var3.append(var9);
                  }
                  break;
               }
            }
         }

         return var3.toString();
      } else {
         return text;
      }
   }

   private static boolean isUsernameCharacter(int codePoint) {
      return Character.isLetterOrDigit(codePoint) || codePoint == (95);
   }

   private String getReplacement() {
      return resolvePlayerName(true, (NameProtect.Mode)this.mode.get(), (String)this.customReplacement.get(), null);
   }

   private static String normalizeName(String value) {
      return value == null ? null : value.trim().toLowerCase(Locale.ROOT);
   }

   private String[] getActiveProtectedNames() {
      if (mc != null && mc.method_1548() != null) {
         String var1 = normalizeName(mc.method_1548().method_1676());
         if (var1 != null && !var1.isBlank()) {
            for (String var5 : this.protectedNames) {
               if (var1.equals(normalizeName(var5))) {
                  return this.protectedNames;
               }
            }

            String[] var6 = Arrays.copyOf(this.protectedNames, this.protectedNames.length + 1);
            var6[this.protectedNames.length] = var1;
            return var6;
         } else {
            return this.protectedNames;
         }
      } else {
         return this.protectedNames;
      }
   }

   private Set getKnownNames(boolean includeOnlinePlayers) {
      HashSet var2 = new HashSet();

      for (String var6 : this.getActiveProtectedNames()) {
         String var7 = normalizeName(var6);
         if (var7 != null && !var7.isBlank()) {
            var2.add(var7);
         }
      }

      if (includeOnlinePlayers && mc != null && mc.method_1562() != null) {
         for (class_640 var9 : mc.method_1562().method_2880()) {
            String var10 = var9.method_2966() == null ? null : normalizeName(var9.method_2966().name());
            if (var10 != null && !var10.isBlank()) {
               var2.add(var10);
            }
         }
      }

      return var2;
   }

   private String replaceImpl(String text) {
      return text != null && !text.isBlank() ? scrubKnownNames(text, this.getReplacement(), this.getKnownNames((Boolean)this.hideOthers.get())) : text;
   }

   public String replace(String text) {
      return !this.isEnabled() ? text : this.replaceImpl(text);
   }

   private boolean isExplicitlyProtected(String name) {
      String var2 = normalizeName(name);
      if (var2 != null && !var2.isBlank()) {
         for (String var6 : this.protectedNames) {
            if (var2.equals(normalizeName(var6))) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean isSelfName(String name) {
      if (mc != null && mc.method_1548() != null) {
         String var2 = normalizeName(name);
         String var3 = normalizeName(mc.method_1548().method_1676());
         return var2 != null && var2.equals(var3);
      } else {
         return false;
      }
   }

   public String maskPlayerName(String originalName) {
      boolean var2 = originalName != null && this.isSelfName(originalName);
      boolean var3 = shouldMaskPlayerName(
         this.isEnabled(), var2, this.isExplicitlyProtected(originalName), (Boolean)this.hideOthers.get(), (Boolean)this.hideNametags.get(), false
      );
      return var3 ? this.getReplacement() : originalName;
   }

   public String maskPlayerNametag(String originalName, boolean self) {
      boolean var3 = shouldMaskPlayerName(
         this.isEnabled(), self, this.isExplicitlyProtected(originalName), (Boolean)this.hideOthers.get(), (Boolean)this.hideNametags.get(), true
      );
      return var3 ? this.getReplacement() : originalName;
   }

   public String maskSelfName(String originalName) {
      boolean var2 = shouldMaskPlayerName(
         this.isEnabled(), true, this.isExplicitlyProtected(originalName), (Boolean)this.hideOthers.get(), (Boolean)this.hideNametags.get(), false
      );
      return var2 ? this.getReplacement() : originalName;
   }

   public class_2561 replaceText(class_2561 original) {
      if (this.isEnabled() && original != null) {
         String var2 = original.getString();
         String var3 = this.replaceImpl(var2);
         return (class_2561)(var3.equals(var2) ? original : class_2561.method_43470(var3).method_10862(original.method_10866()));
      } else {
         return original;
      }
   }

   public class_2561 replaceNametagText(class_2561 original) {
      if (this.isEnabled() && original != null) {
         String var2 = original.getString();
         Set var3 = this.getKnownNames((Boolean)this.hideOthers.get() && (Boolean)this.hideNametags.get());
         String var4 = scrubKnownNames(var2, this.getReplacement(), var3);
         return (class_2561)(var4.equals(var2) ? original : class_2561.method_43470(var4).method_10862(original.method_10866()));
      } else {
         return original;
      }
   }

   public static String staticReplace(String text) {
      return INSTANCE != null ? INSTANCE.replace(text) : text;
   }

   public static class_2561 staticReplaceText(class_2561 original) {
      return INSTANCE != null ? INSTANCE.replaceText(original) : original;
   }

   public static String staticMaskPlayerName(String originalName) {
      return INSTANCE != null ? INSTANCE.maskPlayerName(originalName) : originalName;
   }

   public static String staticMaskPlayerNametag(String originalName, boolean self) {
      return INSTANCE != null ? INSTANCE.maskPlayerNametag(originalName, self) : originalName;
   }

   public static class_2561 staticReplaceNametagText(class_2561 original) {
      return INSTANCE != null ? INSTANCE.replaceNametagText(original) : original;
   }

   public static String staticMaskSelfName(String originalName) {
      return INSTANCE != null ? INSTANCE.maskSelfName(originalName) : originalName;
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

   public enum Mode {
      BLANK,
      CUSTOM;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static NameProtect.Mode[] $values() {
         NameProtect.Mode[] var10000 = new NameProtect.Mode[2];
         var10000[0] = BLANK;
         var10000[1] = CUSTOM;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(-102);
         BLANK = new NameProtect.Mode();
         var10002 = new byte[6];
         var10002[0] = (byte)(122);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-98);
         var10002[5] = (byte)(115);
         CUSTOM = new NameProtect.Mode();
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
}
