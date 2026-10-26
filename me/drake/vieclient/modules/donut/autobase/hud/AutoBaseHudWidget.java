package me.drake.vieclient.modules.donut.autobase.hud;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.render.nvg.NanoVGRenderer;

public final class AutoBaseHudWidget {
   public static final String W_AUTOBASE_BUILD = "autobase_build";
   public static final String W_AUTOBASE_MINE = "autobase_mine";
   public static final float BASE_W = 230.0F;
   public static final float BASE_H = 84.0F;
   private static volatile AutoBaseHudWidget.HudSnapshot currentSnapshot = AutoBaseHudWidget.HudSnapshot.EMPTY;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static void updateSnapshot(AutoBaseHudWidget.HudSnapshot snapshot) {
      currentSnapshot = snapshot != null ? snapshot : AutoBaseHudWidget.HudSnapshot.EMPTY;
   }

   public static AutoBaseHudWidget.HudSnapshot getSnapshot() {
      return currentSnapshot;
   }

   public void draw(HUD hud, float x, float y, float scale) {
      AutoBaseHudWidget.HudSnapshot var5 = currentSnapshot;
      if (var5 != null && (!"IDLE".equalsIgnoreCase(var5.state()) || var5.total() > 0)) {
         float var6 = 230.0F;
         float var7 = 84.0F;
         float var8 = 5.5F;
         new Color(20, 24, 30, 220);
         new Color(255, 255, 255, 25);
         Color var11 = hud != null ? hud.accent() : new Color(50, 168, 82);
         Color var12 = Color.WHITE;
         Color var13 = new Color(180, 185, 195);
         Color var14 = new Color(245, 80, 80);
         Color var15 = new Color(245, 190, 60);
         HUD.drawCardBox(x, y, var6, var7, var8);
         NanoVGRenderer.drawRoundedRect(x + 2.0F, y + 6.0F, 3.0F, var7 - 12.0F, 1.5F, var11);
         float var16 = x + 10.0F;
         float var17 = y + 7.0F;
         String var18 = var5.isMining()
            ? "Auto Base Miner"
            : "Auto Base Builder";
         NanoVGRenderer.drawText(var18, var16, var17, 9.0F, var12, true);
         String var19 = var5.state().toUpperCase();
         Color var20 = "BLOCKED".equals(var19)
            ? var14
            : (
               "PAUSED".equals(var19)
                  ? var15
                  : (
                     !"DONE".equals(var19)
                           && !"COMPLETED".equals(var19)
                        ? new Color(100, 200, 255)
                        : var11
                  )
            );
         NanoVGRenderer.drawText(var19, x + var6 - 10.0F - var19.length() * 5.2F, var17, 8.0F, var20, true);
         float var21 = y + 20.0F;
         String var22 = var5.jobName() != null && !var5.jobName().isEmpty()
            ? var5.jobName()
            : "Active Job";
         NanoVGRenderer.drawText(var22, var16, var21, 7.5F, var13);
         float var23 = var16;
         float var24 = y + 33.0F;
         float var25 = var6 - 20.0F;
         float var26 = 5.0F;
         float var27 = 2.5F;
         NanoVGRenderer.drawRoundedRect(var23, var24, var25, var26, var27, new Color(40, 45, 55, 255));
         int var28 = Math.max(1, var5.total());
         float var29 = Math.min(1.0F, (float)var5.completed() / var28);
         float var30 = Math.min(1.0F - var29, (float)var5.pending() / var28);
         float var31 = Math.min(1.0F - var29 - var30, (float)var5.blocked() / var28);
         float var32 = var25 * var29;
         float var33 = var25 * var30;
         float var34 = var25 * var31;
         if (var32 > 1.0F) {
            NanoVGRenderer.drawRoundedRect(var23, var24, var32, var26, var27, var11);
         }

         if (var33 > 1.0F) {
            NanoVGRenderer.drawRoundedRect(var23 + var32, var24, var33, var26, var27, var15);
         }

         if (var34 > 1.0F) {
            NanoVGRenderer.drawRoundedRect(var23 + var32 + var33, var24, var34, var26, var27, var14);
         }

         float var35 = y + 43.0F;
         int var36 = (int)(var29 * 100.0F);
         String var37 = var5.completed() + "/" + var5.total() + " (" + var36 + "%)";
         if (var5.pending() > 0) {
            var37 = var37 + " +" + var5.pending() + "p";
         }

         if (var5.blocked() > 0) {
            var37 = var37 + " !" + var5.blocked() + "b";
         }

         NanoVGRenderer.drawText(var37, var16, var35, 7.5F, var12);
         String var38 = String.format(
            "%.0f blk/m | %s", var5.blocksPerMin(), var5.etaText()
         );
         NanoVGRenderer.drawText(var38, x + var6 - 10.0F - var38.length() * 4.6F, var35, 7.5F, var13);
         float var39 = y + 55.0F;
         String var40 = formatTime(var5.elapsedMs());
         String var41 = formatTime(var5.remainingBudgetMs());
         String var42 = "Time: " + var40 + " (Budget: " + var41 + ")";
         NanoVGRenderer.drawText(var42, var16, var39, 7.0F, var13);
         float var43 = y + 68.0F;
         if (var5.blockingReason() != null && !var5.blockingReason().isEmpty()) {
            String var44 = "Blocked: " + var5.blockingReason();
            if (var44.length() > 38) {
               var44 = var44.substring(0, 35) + "...";
            }

            NanoVGRenderer.drawText(var44, var16, var43, 7.0F, var14, true);
         } else if (var5.materialSummary() != null && !var5.materialSummary().isEmpty()) {
            NanoVGRenderer.drawText(var5.materialSummary(), var16, var43, 7.0F, var13);
         }
      }
   }

   private static String formatTime(long ms) {
      if (ms <= 0L) {
         byte[] var9 = new byte[5];
         var9[0] = 0;
         var9[1] = (byte)(-86);
         var9[2] = (byte)(38);
         var9[3] = (byte)(-18);
         var9[4] = 1;
         return $$v_s(var9, 4983991136561647637L);
      } else {
         long var2 = ms / 1000L;
         long var4 = var2 / 60L;
         long var6 = var2 % 60L;
         byte[] var10000 = new byte[9];
         var10000[0] = (byte)(21);
         var10000[1] = (byte)(-86);
         var10000[2] = (byte)(46);
         var10000[3] = (byte)(-70);
         var10000[4] = (byte)(11);
         var10000[5] = (byte)(74);
         var10000[6] = (byte)(25);
         var10000[7] = (byte)(95);
         var10000[8] = (byte)(60);
         String var8 = $$v_s(var10000, 4983991136561647637L);
         Object[] var10001 = new Object[2];
         var10001[0] = var4;
         var10001[1] = var6;
         return String.format(var8, var10001);
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

   public record HudSnapshot(
      boolean isMining,
      String jobName,
      String state,
      int completed,
      int total,
      int pending,
      int blocked,
      long elapsedMs,
      long remainingBudgetMs,
      double blocksPerMin,
      String etaText,
      String materialSummary,
      String blockingReason
   ) {
      public static final AutoBaseHudWidget.HudSnapshot EMPTY;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      static {
         byte[] var10003 = new byte[4];
         var10003[0] = (byte)(118);
         var10003[1] = (byte)(-11);
         var10003[2] = (byte)(114);
         var10003[3] = (byte)(-69);
         String var0 = $$v_s(var10003, -2048675248341790691L);
         byte[] var10004 = new byte[4];
         var10004[0] = (byte)(113);
         var10004[1] = (byte)(-34);
         var10004[2] = (byte)(80);
         var10004[3] = (byte)(-101);
         String var1 = $$v_s(var10004, -2048675248341790691L);
         byte[] var10012 = new byte[5];
         var10012[0] = (byte)(21);
         var10012[1] = (byte)(-73);
         var10012[2] = (byte)(38);
         var10012[3] = (byte)(-13);
         var10012[4] = (byte)(35);
         EMPTY = new AutoBaseHudWidget.HudSnapshot(false, var0, var1, 0, 0, 0, 0, 0L, 0L, 0.0, $$v_s(var10012, -2048675248341790691L), "", "");
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
