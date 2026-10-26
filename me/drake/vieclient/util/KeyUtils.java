package me.drake.vieclient.util;

import java.nio.charset.StandardCharsets;
import net.minecraft.class_310;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.glfw.GLFW;

public final class KeyUtils {
   private static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private KeyUtils() {
   }

   public static CharSequence getKey(int key) {
      if (key >= 1000) {
         int var3 = key - 1000;
         switch (var3) {
            case 0:
               return "LMB";
            case 1:
               return "RMB";
            case 2:
               return "MMB";
            case 3:
               return "Button 4";
            case 4:
               return "Button 5";
            case 5:
               return "Button 6";
            case 6:
               return "Button 7";
            case 7:
               return "Button 8";
            default:
               return "Button " + (var3 + 1);
         }
      } else if (key <= -100) {
         int var2 = -(key + 100);
         switch (var2) {
            case 0:
               return "LMB";
            case 1:
               return "RMB";
            case 2:
               return "MMB";
            case 3:
               return "Button 4";
            case 4:
               return "Button 5";
            case 5:
               return "Button 6";
            case 6:
               return "Button 7";
            case 7:
               return "Button 8";
            default:
               return "Button " + (var2 + 1);
         }
      } else {
         if (key >= 65 && key <= 90) {
            return Character.toString((char)key);
         }

         if (key >= 48 && key <= 57) {
            return Character.toString((char)key);
         }

         switch (key) {
            case -1:
               return "Unknown";
            case 0:
               return "LMB";
            case 1:
               return "RMB";
            case 2:
               return "MMB";
            case 32:
               return "Space";
            case 39:
               return "Apostrophe";
            case 44:
               return "Comma";
            case 59:
               return "Semicolon";
            case 61:
               return "Equals";
            case 91:
               return "Left Bracket";
            case 92:
               return "Backslash";
            case 93:
               return "Right Bracket";
            case 96:
               return "Grave Accent";
            case 161:
               return "World 1";
            case 162:
               return "World 2";
            case 256:
               return "Esc";
            case 257:
               return "Enter";
            case 258:
               return "Tab";
            case 259:
               return "Backspace";
            case 260:
               return "Insert";
            case 261:
               return "Delete";
            case 262:
               return "Arrow Right";
            case 263:
               return "Arrow Left";
            case 264:
               return "Arrow Down";
            case 265:
               return "Arrow Up";
            case 266:
               return "Page Up";
            case 267:
               return "Page Down";
            case 268:
               return "Home";
            case 269:
               return "End";
            case 280:
               return "Caps Lock";
            case 281:
               return "Scroll Lock";
            case 282:
               return "Num Lock";
            case 283:
               return "Print Screen";
            case 284:
               return "Pause";
            case 290:
               return "F1";
            case 291:
               return "F2";
            case 292:
               return "F3";
            case 293:
               return "F4";
            case 294:
               return "F5";
            case 295:
               return "F6";
            case 296:
               return "F7";
            case 297:
               return "F8";
            case 298:
               return "F9";
            case 299:
               return "F10";
            case 300:
               return "F11";
            case 301:
               return "F12";
            case 302:
               return "F13";
            case 303:
               return "F14";
            case 304:
               return "F15";
            case 305:
               return "F16";
            case 306:
               return "F17";
            case 307:
               return "F18";
            case 308:
               return "F19";
            case 309:
               return "F20";
            case 310:
               return "F21";
            case 311:
               return "F22";
            case 312:
               return "F23";
            case 313:
               return "F24";
            case 314:
               return "F25";
            case 335:
               return "Numpad Enter";
            case 340:
               return "Left Shift";
            case 341:
               return "Left Control";
            case 342:
               return "Left Alt";
            case 343:
               return "Left Super";
            case 344:
               return "Right Shift";
            case 345:
               return "Right Control";
            case 346:
               return "Right Alt";
            case 347:
               return "Right Super";
            case 348:
               return "Menu";
            default:
               String var1 = GLFW.glfwGetKeyName(key, 0);
               return var1 == null ? "None" : StringUtils.capitalize(var1);
         }
      }
   }

   public static boolean isKeyPressed(int n) {
      if (n == (-1) || n == 0) {
         return true;
      } else if (mc.method_22683() == null) {
         return false;
      } else {
         long var1 = mc.method_22683().method_4490();
         if (n >= (1000) && n <= (1007)) {
            int var3 = n - (1000);
            return GLFW.glfwGetMouseButton(var1, var3) == 1;
         } else if (n <= (-100) && n >= (-107)) {
            return GLFW.glfwGetMouseButton(var1, (-100) - n) == 1;
         } else if (n >= 1 && n <= (7)) {
            return GLFW.glfwGetMouseButton(var1, n) == 1;
         } else {
            return n > (7) ? GLFW.glfwGetKey(var1, n) == 1 : false;
         }
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
