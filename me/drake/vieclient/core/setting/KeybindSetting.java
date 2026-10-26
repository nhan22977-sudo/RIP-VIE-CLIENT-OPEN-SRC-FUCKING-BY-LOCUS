package me.drake.vieclient.core.setting;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;

public class KeybindSetting extends Setting {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public KeybindSetting(String name, String description, int defaultKey) {
      super(name, description, defaultKey);
   }

   public KeybindSetting(String name, String description) {
      this(name, description, -1);
   }

   public boolean isBound() {
      return (Integer)this.get() != (-1);
   }

   public String getKeyName() {
      if (!this.isBound()) {
         byte[] var4 = new byte[4];
         var4[0] = (byte)(-2);
         var4[1] = (byte)(-43);
         var4[2] = (byte)(82);
         var4[3] = (byte)(-101);
         return $$v_s(var4, 6223737896138166421L);
      }

      if ((Integer)this.get() >= (1000)) {
         int var1 = (Integer)this.get() - (1000);
         if (var1 == 0) {
            byte[] var3 = new byte[10];
            var3[0] = (byte)(-3);
            var3[1] = (byte)(-40);
            var3[2] = (byte)(45);
            var3[3] = (byte)(-2);
            var3[4] = (byte)(53);
            var3[5] = (byte)(-70);
            var3[6] = (byte)(57);
            var3[7] = (byte)(24);
            var3[8] = (byte)(-84);
            var3[9] = (byte)(-117);
            return $$v_s(var3, 6223737896138166421L);
         } else if (var1 == 1) {
            byte[] var2 = new byte[11];
            var2[0] = (byte)(-3);
            var2[1] = (byte)(-40);
            var2[2] = (byte)(46);
            var2[3] = (byte)(-2);
            var2[4] = (byte)(53);
            var2[5] = (byte)(-92);
            var2[6] = (byte)(53);
            var2[7] = (byte)(25);
            var2[8] = (byte)(-80);
            var2[9] = (byte)(-42);
            var2[10] = (byte)(-51);
            return $$v_s(var2, 6223737896138166421L);
         } else if (var1 == (2)) {
            byte[] var10000 = new byte[9];
            var10000[0] = (byte)(-3);
            var10000[1] = (byte)(-40);
            var10000[2] = (byte)(47);
            var10000[3] = (byte)(-2);
            var10000[4] = (byte)(53);
            var10000[5] = (byte)(-69);
            var10000[6] = (byte)(53);
            var10000[7] = (byte)(26);
            var10000[8] = (byte)(-15);
            return $$v_s(var10000, 6223737896138166421L);
         } else {
            return "MB" + (var1 + 1);
         }
      } else {
         return keyCodeToName((Integer)this.get());
      }
   }

   public static String keyCodeToName(int k) {
      if (k == 0 || k == -1) {
         return "NONE";
      }

      if (k >= 1000) {
         int var1 = k - 1000;
         return "MB" + (var1 + 1);
      }

      if (k < 0) {
         return "MB" + -k;
      }

      return switch (k) {
         case 32 -> "SPACE";
         default -> "KEY" + k;
         case 39 -> "'";
         case 44 -> ",";
         case 45 -> "-";
         case 46 -> ".";
         case 47 -> "/";
         case 48 -> "0";
         case 49 -> "1";
         case 50 -> "2";
         case 51 -> "3";
         case 52 -> "4";
         case 53 -> "5";
         case 54 -> "6";
         case 55 -> "7";
         case 56 -> "8";
         case 57 -> "9";
         case 59 -> ";";
         case 61 -> "=";
         case 65 -> "A";
         case 66 -> "B";
         case 67 -> "C";
         case 68 -> "D";
         case 69 -> "E";
         case 70 -> "F";
         case 71 -> "G";
         case 72 -> "H";
         case 73 -> "I";
         case 74 -> "J";
         case 75 -> "K";
         case 76 -> "L";
         case 77 -> "M";
         case 78 -> "N";
         case 79 -> "O";
         case 80 -> "P";
         case 81 -> "Q";
         case 82 -> "R";
         case 83 -> "S";
         case 84 -> "T";
         case 85 -> "U";
         case 86 -> "V";
         case 87 -> "W";
         case 88 -> "X";
         case 89 -> "Y";
         case 90 -> "Z";
         case 91 -> "[";
         case 92 -> "\\";
         case 93 -> "]";
         case 96 -> "`";
         case 256 -> "ESC";
         case 257 -> "ENTER";
         case 258 -> "TAB";
         case 259 -> "BKSP";
         case 260 -> "INS";
         case 261 -> "DEL";
         case 262 -> "RIGHT";
         case 263 -> "LEFT";
         case 264 -> "DOWN";
         case 265 -> "UP";
         case 266 -> "PGUP";
         case 267 -> "PGDN";
         case 268 -> "HOME";
         case 269 -> "END";
         case 280 -> "CAPS";
         case 281 -> "PAUSE";
         case 282 -> "NUMLK";
         case 283 -> "PRINT";
         case 290 -> "F1";
         case 291 -> "F2";
         case 292 -> "F3";
         case 293 -> "F4";
         case 294 -> "F5";
         case 295 -> "F6";
         case 296 -> "F7";
         case 297 -> "F8";
         case 298 -> "F9";
         case 299 -> "F10";
         case 300 -> "F11";
         case 301 -> "F12";
         case 320 -> "NUM0";
         case 321 -> "NUM1";
         case 322 -> "NUM2";
         case 323 -> "NUM3";
         case 324 -> "NUM4";
         case 325 -> "NUM5";
         case 326 -> "NUM6";
         case 327 -> "NUM7";
         case 328 -> "NUM8";
         case 329 -> "NUM9";
         case 330 -> "NUM.";
         case 331 -> "NUM/";
         case 332 -> "NUM*";
         case 333 -> "NUM-";
         case 334 -> "NUM+";
         case 335 -> "NUMENT";
         case 340 -> "LSHIFT";
         case 341 -> "LCTRL";
         case 342 -> "LALT";
         case 343 -> "LSUPER";
         case 344 -> "RSHIFT";
         case 345 -> "RCTRL";
         case 346 -> "RALT";
         case 347 -> "RSUPER";
         case 348 -> "MENU";
      };
   }

   public static int parseKey(String name) {
      if (name != null && !name.isBlank()) {
         String var1 = name.trim().toUpperCase();
         if (!var1.equals("NONE")
            && !var1.equals("CLEAR")
            && !var1.equals("NULL")
            && !var1.equals("0")) {
            if (var1.length() == 1) {
               char var2 = var1.charAt(0);
               if (var2 >= 'A' && var2 <= 'Z') {
                  return var2;
               }

               if (var2 >= '0' && var2 <= '9') {
                  return var2;
               }

               if (var2 == '`' || var2 == '~') {
                  return 96;
               }

               if (var2 == '-' || var2 == '_') {
                  return 45;
               }

               if (var2 == '=' || var2 == '+') {
                  return 61;
               }

               if (var2 == '[' || var2 == '{') {
                  return 91;
               }

               if (var2 == ']' || var2 == '}') {
                  return 93;
               }

               if (var2 == '\\' || var2 == '|') {
                  return 92;
               }

               if (var2 == ';' || var2 == ':') {
                  return 59;
               }

               if (var2 == '\'' || var2 == '"') {
                  return 39;
               }

               if (var2 == ',' || var2 == '<') {
                  return 44;
               }

               if (var2 == '.' || var2 == '>') {
                  return 46;
               }

               if (var2 == '/' || var2 == '?') {
                  return 47;
               }
            }

            try {
               return Integer.parseInt(var1);
            } catch (NumberFormatException var5) {
               if (var1.startsWith("MB")
                  || var1.startsWith("MOUSE")) {
                  String var6 = var1.replace("MOUSE", "")
                     .replace("MB", "");

                  try {
                     int var3 = Integer.parseInt(var6) - 1;
                     if (var3 >= 0 && var3 < 8) {
                        return 1000 + var3;
                     }
                  } catch (NumberFormatException var4) {
                  }
               }

               if (var1.equals("LMB")
                  || var1.equals("LEFT_CLICK")) {
                  return 1000;
               }

               if (var1.equals("RMB")
                  || var1.equals("RIGHT_CLICK")) {
                  return 1001;
               }

               if (!var1.equals("MMB")
                  && !var1.equals("MIDDLE_CLICK")) {
                  return switch (var1) {
                     case "ESC", "ESCAPE" -> 256;
                     case "ENTER", "RETURN" -> 257;
                     case "TAB" -> 258;
                     case "BKSP", "BACKSPACE" -> 259;
                     case "INS", "INSERT" -> 260;
                     case "DEL", "DELETE" -> 261;
                     case "RIGHT" -> 262;
                     case "LEFT" -> 263;
                     case "DOWN" -> 264;
                     case "UP" -> 265;
                     case "PGUP", "PAGE_UP", "PAGEUP" -> 266;
                     case "PGDN", "PAGE_DOWN", "PAGEDOWN" -> 267;
                     case "HOME" -> 268;
                     case "END" -> 269;
                     case "CAPS", "CAPSLOCK", "CAPS_LOCK" -> 280;
                     case "SPACE", "SPC" -> 32;
                     case "LSHIFT", "LEFT_SHIFT", "LEFTSHIFT" -> 340;
                     case "RSHIFT", "RIGHT_SHIFT", "RIGHTSHIFT", "SHIFT" -> 344;
                     case "LCTRL", "LEFT_CONTROL", "LEFT_CTRL", "LEFTCTRL" -> 341;
                     case "RCTRL", "RIGHT_CONTROL", "RIGHT_CTRL", "RIGHTCTRL", "CTRL" -> 345;
                     case "LALT", "LEFT_ALT", "LEFTALT" -> 342;
                     case "RALT", "RIGHT_ALT", "RIGHTALT", "ALT" -> 346;
                     case "GRAVE", "TILDE" -> 96;
                     case "F1" -> 290;
                     case "F2" -> 291;
                     case "F3" -> 292;
                     case "F4" -> 293;
                     case "F5" -> 294;
                     case "F6" -> 295;
                     case "F7" -> 296;
                     case "F8" -> 297;
                     case "F9" -> 298;
                     case "F10" -> 299;
                     case "F11" -> 300;
                     case "F12" -> 301;
                     default -> Integer.MIN_VALUE;
                  };
               } else {
                  return 1002;
               }
            }
         } else {
            return -1;
         }
      } else {
         return Integer.MIN_VALUE;
      }
   }

   @Override
   public JsonElement toJson() {
      return new JsonPrimitive((Number)this.get());
   }

   @Override
   public void fromJson(JsonElement json) {
      if (json != null && json.isJsonPrimitive()) {
         this.set(json.getAsInt());
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
}
