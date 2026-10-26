package me.drake.vieclient.gui.nvg.ui.components;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.render.nvg.NanoVGRenderer;

public final class ModuleIcon {
   private static final int CROSSHAIR = 0;
   private static final int EYE = 1;
   private static final int SUN = 2;
   private static final int CUBE = 3;
   private static final int BOLT = 4;
   private static final int WING = 5;
   private static final int BUBBLE = 6;
   private static final int GEAR = 7;
   private static final int DOT = 8;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ModuleIcon() {
   }

   private static int pick(String n) {
      if (has(
         n,
         "esp",
         "finder",
         "detect",
         "debug",
         "suspicious",
         "scoreboard",
         "kelp",
         "scanner",
         "scan"
      )) {
         return 1;
      } else if (has(n, "bright", "light")
         )
       {
         return 2;
      } else if (has(
         n,
         "block",
         "netherite",
         "spawner",
         "shulker",
         "bone",
         "storage",
         "web",
         "trap",
         "chunk",
         "bedrock",
         "hole",
         "ore"
      )) {
         return 3;
      } else if (has(
         n,
         "aim",
         "target",
         "crystal",
         "trigger",
         "aura",
         "mace",
         "anchor",
         "crit",
         "hitbox",
         "shield"
      )) {
         return 0;
      } else if (has(
         n,
         "elytra",
         "fly",
         "glide",
         "boost",
         "firework",
         "pearl",
         "trident",
         "jump"
      )) {
         return 5;
      } else if (has(
         n,
         "auto",
         "fast",
         "sprint",
         "speed",
         "swing",
         "sell",
         "mine",
         "eat",
         "tool",
         "place",
         "totem",
         "cart"
      )) {
         return 4;
      } else if (has(
         n,
         "spam",
         "name",
         "message",
         "tpa",
         "communication",
         "translator",
         "notif",
         "chat",
         "weather",
         "order"
      )) {
         return 6;
      } else {
         return has(
               n,
               $$v_s(new byte[]{(byte)4, (byte)-11, (byte)105, (byte)-80, (byte)-77}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)26, (byte)-17, (byte)111, (byte)-73, (byte)-76}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)17, (byte)-24, (byte)117, (byte)-69, (byte)-71, (byte)77}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)20, (byte)-11, (byte)114, (byte)-72, (byte)-66, (byte)78}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)4, (byte)-1, (byte)112, (byte)-72}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)5, (byte)-22, (byte)127}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)26, (byte)-5, (byte)127, (byte)-84, (byte)-72}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)6, (byte)-17, (byte)117, (byte)-67, (byte)-68}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)20, (byte)-10, (byte)117, (byte)-69, (byte)-71, (byte)93}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)4, (byte)-22, (byte)115, (byte)-86, (byte)-66, (byte)79, (byte)62}, -7546635022498541486L),
               $$v_s(
                  new byte[]{(byte)21, (byte)-5, (byte)127, (byte)-75, (byte)-80, (byte)91, (byte)40, (byte)-54, (byte)113, (byte)-58}, -7546635022498541486L
               ),
               $$v_s(new byte[]{(byte)19, (byte)-11, (byte)114, (byte)-85, (byte)-93}, -7546635022498541486L),
               $$v_s(new byte[]{(byte)5, (byte)-5, (byte)114, (byte)-75}, -7546635022498541486L)
            )
            ? 7
            : 8;
      }
   }

   private static boolean has(String n, String... keys) {
      for (String var5 : keys) {
         if (n.contains(var5)) {
            return true;
         }
      }

      return false;
   }

   public static void draw(String name, float x, float y, float s, Color c) {
      float var5 = x + s / 2.0F;
      float var6 = y + s / 2.0F;
      float var7 = 1.1F;
      switch (pick(name == null ? "" : name.toLowerCase())) {
         case 0:
            NanoVGRenderer.drawCircleOutline(var5, var6, s * 0.4F, var7, c);
            NanoVGRenderer.drawLine(var5, var6 - s * 0.16F, var5, var6 + s * 0.16F, var7, c);
            NanoVGRenderer.drawLine(var5 - s * 0.16F, var6, var5 + s * 0.16F, var6, var7, c);
            break;
         case 1:
            NanoVGRenderer.drawCircleOutline(var5, var6, s * 0.4F, var7, c);
            NanoVGRenderer.drawCircle(var5, var6, s * 0.15F, c);
            break;
         case 2:
            NanoVGRenderer.drawCircle(var5, var6, s * 0.17F, c);

            for (int var13 = 0; var13 < 8; var13++) {
               double var14 = var13 * Math.PI / 4.0;
               float var15 = (float)Math.cos(var14);
               float var16 = (float)Math.sin(var14);
               NanoVGRenderer.drawLine(var5 + var15 * s * 0.28F, var6 + var16 * s * 0.28F, var5 + var15 * s * 0.44F, var6 + var16 * s * 0.44F, var7, c);
            }
            break;
         case 3:
            float var8 = s * 0.5F;
            float var9 = s * 0.5F;
            float var10 = var5 - var8 / 2.0F;
            float var11 = var6 - var9 / 2.0F + s * 0.06F;
            float var12 = s * 0.16F;
            NanoVGRenderer.drawRoundedRectOutline(var10, var11, var8, var9, 1.5F, var7, c);
            NanoVGRenderer.drawLine(var10, var11, var10 + var12, var11 - var12, var7, c);
            NanoVGRenderer.drawLine(var10 + var8, var11, var10 + var8 + var12, var11 - var12, var7, c);
            NanoVGRenderer.drawLine(var10 + var12, var11 - var12, var10 + var8 + var12, var11 - var12, var7, c);
            NanoVGRenderer.drawLine(var10 + var8 + var12, var11 - var12, var10 + var8 + var12, var11 + var9 - var12, var7, c);
            break;
         case 4:
            NanoVGRenderer.drawLine(var5 + s * 0.1F, y + s * 0.12F, var5 - s * 0.16F, var6 + s * 0.02F, var7, c);
            NanoVGRenderer.drawLine(var5 - s * 0.16F, var6 + s * 0.02F, var5 + s * 0.04F, var6 + s * 0.02F, var7, c);
            NanoVGRenderer.drawLine(var5 + s * 0.04F, var6 + s * 0.02F, var5 - s * 0.1F, y + s * 0.88F, var7, c);
            break;
         case 5:
            NanoVGRenderer.drawLine(var5 - s * 0.26F, var6 + s * 0.14F, var5, var6 - s * 0.2F, var7, c);
            NanoVGRenderer.drawLine(var5, var6 - s * 0.2F, var5 + s * 0.26F, var6 + s * 0.14F, var7, c);
            NanoVGRenderer.drawLine(var5, var6 - s * 0.1F, var5, var6 + s * 0.24F, var7, c);
            break;
         case 6:
            NanoVGRenderer.drawRoundedRectOutline(x + s * 0.12F, y + s * 0.16F, s * 0.76F, s * 0.5F, 3.0F, var7, c);
            NanoVGRenderer.drawLine(x + s * 0.3F, y + s * 0.66F, x + s * 0.42F, y + s * 0.82F, var7, c);
            NanoVGRenderer.drawLine(x + s * 0.42F, y + s * 0.82F, x + s * 0.46F, y + s * 0.66F, var7, c);
            break;
         case 7:
            NanoVGRenderer.drawCircleOutline(var5, var6, s * 0.34F, var7, c);
            NanoVGRenderer.drawCircle(var5, var6, s * 0.12F, c);
            break;
         default:
            NanoVGRenderer.drawRoundedRect(var5 - s * 0.18F, var6 - s * 0.18F, s * 0.36F, s * 0.36F, 2.0F, c);
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
