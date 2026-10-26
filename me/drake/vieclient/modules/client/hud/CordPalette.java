package me.drake.vieclient.modules.client.hud;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class CordPalette {
   private static final float NETHER_BODY_DARKEN = 0.62F;
   private static final float NETHER_BORDER_TINT = 0.55F;
   private static final float NETHER_BORDER_ALPHA = 108.0F;
   private final int bodyR;
   private final int bodyG;
   private final int bodyB;
   private final int borderR;
   private final int borderG;
   private final int borderB;
   private final float borderAlpha;
   private final boolean variant;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private CordPalette(int bodyR, int bodyG, int bodyB, int borderR, int borderG, int borderB, float borderAlpha, boolean variant) {
      this.bodyR = bodyRx;
      this.bodyG = bodyGx;
      this.bodyB = bodyBx;
      this.borderR = borderRx;
      this.borderG = borderGx;
      this.borderB = borderBx;
      this.borderAlpha = borderAlphax;
      this.variant = variantx;
   }

   public boolean isVariant() {
      return this.variant;
   }

   public static CordPalette forDimension(String registryPath, int[] themeBody, int[] themeBorder, int[] accent) {
      CordPalette.Dimension var4 = CordPalette.Dimension.resolve(registryPath);
      if (var4 != CordPalette.Dimension.NETHER) {
         return new CordPalette(0, 0, 0, 0, 0, 0, 0.0F, false);
      }

      int var5 = scale(themeBody[0], 0.62F);
      int var6 = scale(themeBody[1], 0.62F);
      int var7 = scale(themeBody[2], 0.62F);
      return new CordPalette(
         var5,
         var6,
         var7,
         mix(themeBorder[0], accent[0], 0.55F),
         mix(themeBorder[1], accent[1], 0.55F),
         mix(themeBorder[2], accent[2], 0.55F),
         108.0F,
         true
      );
   }

   public int bodyR() {
      return this.bodyR;
   }

   public int bodyG() {
      return this.bodyG;
   }

   public int bodyB() {
      return this.bodyB;
   }

   public int borderR() {
      return this.borderR;
   }

   public int borderG() {
      return this.borderG;
   }

   public int borderB() {
      return this.borderB;
   }

   public float borderAlpha() {
      return this.borderAlpha;
   }

   private static int scale(int c, float f) {
      return Math.max(0, Math.min(255, Math.round(c * f)));
   }

   private static int mix(int a, int b, float f) {
      return Math.max(0, Math.min(255, Math.round(a + (b - a) * f)));
   }

   public enum Dimension {
      OVERWORLD,
      NETHER,
      END,
      OTHER;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public static CordPalette.Dimension resolve(String registryPath) {
         if (registryPath == null) {
            return OVERWORLD;
         }

         String var1 = registryPath.toLowerCase(Locale.ROOT);
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(93);
         var10001[1] = (byte)(-1);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-74);
         var10001[4] = (byte)(86);
         var10001[5] = (byte)(-114);
         if (var1.contains($$v_s(var10001, 5749727326618505238L))) {
            return NETHER;
         }

         var10001 = new byte[7];
         var10001[0] = (byte)(71);
         var10001[1] = (byte)(-14);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-127);
         var10001[4] = (byte)(86);
         var10001[5] = (byte)(-110);
         var10001[6] = (byte)(-84);
         if (!var1.contains($$v_s(var10001, 5749727326618505238L))) {
            var10001 = new byte[3];
            var10001[0] = (byte)(86);
            var10001[1] = (byte)(-12);
            var10001[2] = (byte)(120);
            if (!var1.equals($$v_s(var10001, 5749727326618505238L))) {
               var10001 = new byte[4];
               var10001[0] = (byte)(9);
               var10001[1] = (byte)(-1);
               var10001[2] = (byte)(114);
               var10001[3] = (byte)(-70);
               if (!var1.endsWith($$v_s(var10001, 5749727326618505238L))) {
                  return OVERWORLD;
               }
            }
         }

         return END;
      }

      // $VF: synthetic method
      private static CordPalette.Dimension[] $values() {
         CordPalette.Dimension[] var10000 = new CordPalette.Dimension[4];
         var10000[0] = OVERWORLD;
         var10000[1] = NETHER;
         var10000[2] = END;
         var10000[3] = OTHER;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[9];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-52);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-116);
         var10002[4] = (byte)(100);
         var10002[5] = (byte)(-77);
         var10002[6] = (byte)(-102);
         var10002[7] = (byte)(43);
         var10002[8] = (byte)(31);
         OVERWORLD = new CordPalette.Dimension();
         var10002 = new byte[6];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-106);
         var10002[4] = (byte)(118);
         var10002[5] = (byte)(-82);
         NETHER = new CordPalette.Dimension();
         var10002 = new byte[3];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(88);
         END = new CordPalette.Dimension();
         var10002 = new byte[5];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-50);
         var10002[2] = (byte)(84);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(97);
         OTHER = new CordPalette.Dimension();
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
