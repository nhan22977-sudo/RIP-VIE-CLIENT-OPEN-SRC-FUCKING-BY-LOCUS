package me.drake.vieclient.gui.nvg.ui;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.List;
import me.drake.vieclient.integration.EmotecraftBridge;
import me.drake.vieclient.modules.client.Emote;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import net.minecraft.class_310;
import org.lwjgl.glfw.GLFW;

public final class EmoteWheel {
   private static final int SLOT_COUNT = 9;
   private static final float INNER_RADIUS = 32.0F;
   private static final float OUTER_RADIUS = 104.0F;
   private static final long OPEN_ANIMATION_NS = 220000000L;
   private static final String EMPTY_LABEL = "-";
   private static final String NEXT_LABEL = "More >";
   private static final float AIM_SCALE = 0.15F;
   private static final float AIM_DEADZONE = 5.0F;
   private static final float AIM_MAX = 35.0F;
   private static final long TAP_NS = 250000000L;
   private static boolean open;
   private static boolean closing;
   private static boolean emoteRunning;
   private static boolean emoteLooping;
   private static long animationStartedAt;
   private static long emoteLoopStartsAt;
   private static int hoveredSlot = -1;
   private static int page;
   private static int runningEmote = -1;
   private static boolean holdMode;
   private static int heldBind = -1;
   private static long openedAt;
   private static float aimX;
   private static float aimY;
   private static boolean cursorUnlocked;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private EmoteWheel() {
   }

   public static void open() {
      class_310 var0 = class_310.method_1551();
      if (var0 != null && var0.field_1724 != null && var0.field_1687 != null) {
         if (open && !closing) {
            close();
         } else {
            if (var0.field_1755 != null) {
               var0.method_1507(null);
            }

            open = true;
            closing = false;
            page = 0;
            aimX = 0.0F;
            aimY = 0.0F;
            animationStartedAt = System.nanoTime();
            openedAt = animationStartedAt;
            heldBind = Emote.bindCode();
            holdMode = Emote.holdToSelect() && isBindHeld(heldBind);
            hoveredSlot = -1;
            if (!holdMode) {
               unlockCursor();
            }
         }
      }
   }

   public static boolean consumesCameraInput() {
      return isOpen() && holdMode;
   }

   public static void applyMouseDelta(double deltaX, double deltaY) {
      if (isOpen() && holdMode) {
         aimX += (float)deltaX * 0.15F;
         aimY += (float)deltaY * 0.15F;
         float var4 = aimLength();
         if (var4 > 35.0F) {
            aimX = aimX / var4 * 35.0F;
            aimY = aimY / var4 * 35.0F;
         }
      }
   }

   private static float aimLength() {
      return (float)Math.sqrt(aimX * aimX + aimY * aimY);
   }

   private static int slotFromAim() {
      float var0 = aimLength();
      return var0 < 5.0F ? -1 : slotAtAngle((float)Math.atan2(aimY, aimX));
   }

   private static int currentSlot() {
      return holdMode ? slotFromAim() : slotAtMouse();
   }

   private static boolean isBindHeld(int vieKey) {
      if (vieKey == (-1)) {
         return false;
      }

      class_310 var1 = class_310.method_1551();
      if (var1 != null && var1.method_22683() != null) {
         long var2 = var1.method_22683().method_4490();
         if (vieKey >= (1000)) {
            int var4 = vieKey - (1000);
            return var4 >= 0 && var4 <= (7) && GLFW.glfwGetMouseButton(var2, var4) == 1;
         } else {
            return vieKey >= 0 && GLFW.glfwGetKey(var2, vieKey) == 1;
         }
      } else {
         return false;
      }
   }

   private static void becomeSticky() {
      holdMode = false;
      aimX = 0.0F;
      aimY = 0.0F;
      unlockCursor();
      hoveredSlot = slotAtMouse();
   }

   private static void unlockCursor() {
      class_310 var0 = class_310.method_1551();
      if (var0 != null && var0.field_1729 != null && !cursorUnlocked) {
         cursorUnlocked = true;
         var0.field_1729.method_1610();
      }
   }

   public static boolean isOpen() {
      return open || closing;
   }

   public static boolean isEmoteRunning() {
      return emoteRunning;
   }

   public static boolean handleKey(int keyCode) {
      if (!isOpen()) {
         return false;
      } else if (closing) {
         return true;
      } else if (keyCode == (256)) {
         close();
         return true;
      } else if (keyCode == (258)) {
         nextPage();
         return true;
      } else if (keyCode >= (49) && keyCode <= (57)) {
         select(keyCode - (49));
         return true;
      } else {
         return true;
      }
   }

   public static boolean handleMouseButton(int button) {
      if (!isOpen()) {
         return false;
      }

      if (closing) {
         return true;
      }

      if (button == 0) {
         int var1 = currentSlot();
         if (var1 >= 0) {
            select(var1);
         }
      } else if (button == 1) {
         close();
      }

      return true;
   }

   public static boolean handleScroll(double amount) {
      if (isOpen() && !closing && pageCount() > 1) {
         if (amount > 0.0) {
            previousPage();
         } else {
            nextPage();
         }

         return true;
      } else {
         return false;
      }
   }

   public static void updateMouse() {
      if (open && !closing && !holdMode) {
         hoveredSlot = slotAtMouse();
      }
   }

   public static void tick() {
      class_310 var0 = class_310.method_1551();
      if (var0 != null && var0.field_1724 != null && var0.field_1687 != null) {
         if (isOpen() && var0.field_1755 != null) {
            forceClose();
         }

         if (open && !closing && holdMode && !isBindHeld(heldBind)) {
            int var1 = slotFromAim();
            if (var1 >= 0 && isPagerSlot(var1)) {
               nextPage();
               becomeSticky();
            } else if (var1 >= 0) {
               select(var1);
            } else if (System.nanoTime() - openedAt < 250000000L) {
               becomeSticky();
            } else {
               close();
            }
         }

         if (emoteRunning) {
            if (Emote.shouldStopOnMove() && isMovementPressed()) {
               stopEmote();
            } else {
               if (!emoteLooping && Emote.shouldLoop() && System.nanoTime() >= emoteLoopStartsAt) {
                  emoteLooping = EmotecraftBridge.loop(runningEmote, 0);
                  if (!emoteLooping) {
                     resetEmoteState();
                  }
               }
            }
         }
      } else {
         if (isOpen()) {
            forceClose();
         }

         if (emoteRunning) {
            resetEmoteState();
         }
      }
   }

   public static void render() {
      if (isOpen()) {
         class_310 var0 = class_310.method_1551();
         if (var0 != null && var0.method_22683() != null && var0.field_1724 != null) {
            float var1 = animationProgress();
            if (closing && var1 <= 0.0F) {
               finishClose();
            } else {
               int var2 = var0.method_22683().method_4486();
               int var3 = var0.method_22683().method_4502();
               float var4 = var2 * 0.5F;
               float var5 = var3 * 0.5F;
               hoveredSlot = currentSlot();
               int var6 = EmotecraftBridge.count();

               try (NanoVGFrame var7 = NanoVGRenderer.frame()) {
                  if (!var7.isActive()) {
                     return;
                  }

                  NanoVGRenderer.save();
                  NanoVGRenderer.translate(var4, var5);
                  NanoVGRenderer.scale(var1, var1);
                  NanoVGRenderer.translate(-var4, -var5);
                  NanoVGRenderer.globalAlpha(var1);
                  NanoVGRenderer.drawCircle(var4, var5, 109.0F, new Color(8, 10, 16, 105));

                  for (int var8 = 0; var8 < 9; var8++) {
                     float var9 = startAngle(var8);
                     float var10 = var9 + sectorAngle();
                     String var11 = labelFor(var8);
                     boolean var12 = isSlotUsable(var8);
                     boolean var13 = var8 == hoveredSlot && var12;
                     Color var14 = var13 ? new Color(101, 192, 255, 175) : (var12 ? new Color(28, 33, 46, 205) : new Color(20, 23, 32, 150));
                     drawSector(var4, var5, 32.0F, 104.0F, var9, var10, var14);
                     NanoVGRenderer.drawLine(
                        var4 + (float)Math.cos(var9) * 32.0F,
                        var5 + (float)Math.sin(var9) * 32.0F,
                        var4 + (float)Math.cos(var9) * 104.0F,
                        var5 + (float)Math.sin(var9) * 104.0F,
                        1.0F,
                        new Color(180, 205, 230, 105)
                     );
                     float var15 = var9 + sectorAngle() * 0.5F;
                     float var16 = 68.0F;
                     float var17 = var11.length() > 8 ? 9.0F : 10.5F;
                     String var18 = trim(var11, 66.0F, var17);
                     float var19 = var4 + (float)Math.cos(var15) * var16;
                     float var20 = var5 + (float)Math.sin(var15) * var16;
                     float var21 = NanoVGRenderer.getTextWidth(var18, var17);
                     NanoVGRenderer.drawText(
                        var18, var19 - var21 * 0.5F, var20 - var17 * 0.5F, var17, new Color(255, 255, 255, var13 ? 255 : (var12 ? 180 : 90)), true
                     );
                  }

                  NanoVGRenderer.drawCircleOutline(var4, var5, 32.0F, 1.25F, new Color(205, 225, 255, 135));
                  NanoVGRenderer.drawCircleOutline(var4, var5, 104.0F, 1.25F, new Color(205, 225, 255, 155));
                  NanoVGRenderer.drawCircle(var4, var5, 29.0F, new Color(15, 18, 27, 230));
                  if (holdMode) {
                     float var24 = aimLength();
                     float var26 = Math.min(1.0F, var24 / 35.0F) * 23.0F;
                     float var28 = var4;
                     float var30 = var5;
                     if (var24 > 1.0E-4F) {
                        var28 = var4 + aimX / var24 * var26;
                        var30 = var5 + aimY / var24 * var26;
                     }

                     boolean var31 = var24 >= 5.0F;
                     NanoVGRenderer.drawCircle(var28, var30, var31 ? 3.5F : 2.5F, var31 ? new Color(101, 192, 255, 235) : new Color(150, 165, 190, 170));
                  }

                  String var25;
                  if (var6 == 0) {
                     var25 = "No emotes";
                  } else if (hoveredSlot >= 0 && isSlotUsable(hoveredSlot)) {
                     var25 = labelFor(hoveredSlot);
                  } else {
                     var25 = pageCount() > 1 ? page + 1 + "/" + pageCount() : "Emote";
                  }

                  String var27 = trim(var25, 56.0F, 10.0F);
                  float var29 = NanoVGRenderer.getTextWidth(var27, 10.0F);
                  NanoVGRenderer.drawText(var27, var4 - var29 * 0.5F, var5 - 5.0F, 10.0F, new Color(240, 245, 255, 230));
                  NanoVGRenderer.globalAlpha(1.0F);
                  NanoVGRenderer.restore();
               }
            }
         } else {
            forceClose();
         }
      }
   }

   private static float animationProgress() {
      float var0 = Math.min(1.0F, (float)(System.nanoTime() - animationStartedAt) / 2.2E8F);
      var0 = var0 * var0 * (3.0F - 2.0F * var0);
      return closing ? 1.0F - var0 : var0;
   }

   private static String trim(String text, float maxWidth, float size) {
      if (NanoVGRenderer.getTextWidth(text, size) <= maxWidth) {
         return text;
      }

      String var3 = text;

      while (var3.length() > 1 && NanoVGRenderer.getTextWidth(var3 + "..", size) > maxWidth) {
         var3 = var3.substring(0, var3.length() - 1);
      }

      return var3 + "..";
   }

   private static int perPage() {
      return EmotecraftBridge.count() > (9) ? 8 : 9;
   }

   private static int pageCount() {
      int var0 = EmotecraftBridge.count();
      return var0 <= (9) ? 1 : (int)Math.ceil((double)var0 / perPage());
   }

   private static boolean isPagerSlot(int slot) {
      return pageCount() > 1 && slot == (8);
   }

   private static int emoteIndex(int slot) {
      if (slot >= 0 && slot < (9) && !isPagerSlot(slot)) {
         int var1 = page * perPage() + slot;
         return var1 < EmotecraftBridge.count() ? var1 : -1;
      } else {
         return -1;
      }
   }

   private static boolean isSlotUsable(int slot) {
      return isPagerSlot(slot) || emoteIndex(slot) >= 0;
   }

   private static String labelFor(int slot) {
      if (isPagerSlot(slot)) {
         byte[] var10000 = new byte[6];
         var10000[0] = (byte)(117);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(110);
         var10000[3] = (byte)(-69);
         var10000[4] = (byte)(42);
         var10000[5] = (byte)(83);
         return $$v_s(var10000, -1413088275434581987L);
      }

      int var1 = emoteIndex(slot);
      if (var1 < 0) {
         return "-";
      }

      String var2 = EmotecraftBridge.nameAt(var1);
      return var2 == null ? "-" : var2;
   }

   private static void nextPage() {
      int var0 = pageCount();
      if (var0 > 1) {
         page = (page + 1) % var0;
      }
   }

   private static void previousPage() {
      int var0 = pageCount();
      if (var0 > 1) {
         page = (page - 1 + var0) % var0;
      }
   }

   private static void select(int slot) {
      if (isPagerSlot(slot)) {
         nextPage();
      } else {
         int var1 = emoteIndex(slot);
         if (var1 >= 0) {
            startEmote(var1);
            close();
         }
      }
   }

   private static void startEmote(int index) {
      class_310 var1 = class_310.method_1551();
      if (var1 != null && var1.field_1724 != null) {
         if (!EmotecraftBridge.play(index)) {
            resetEmoteState();
         } else {
            emoteRunning = true;
            emoteLooping = false;
            runningEmote = index;
            int var2 = Math.max(1, EmotecraftBridge.lengthTicksAt(index));
            emoteLoopStartsAt = System.nanoTime() + var2 * 50000000L;
         }
      }
   }

   private static boolean isMovementPressed() {
      class_310 var0 = class_310.method_1551();
      return var0 != null && var0.field_1690 != null
         ? var0.field_1690.field_1894.method_1434()
            || var0.field_1690.field_1881.method_1434()
            || var0.field_1690.field_1913.method_1434()
            || var0.field_1690.field_1849.method_1434()
            || var0.field_1690.field_1903.method_1434()
            || var0.field_1690.field_1832.method_1434()
         : false;
   }

   private static void stopEmote() {
      resetEmoteState();
      EmotecraftBridge.stop();
   }

   private static void resetEmoteState() {
      emoteRunning = false;
      emoteLooping = false;
      runningEmote = -1;
   }

   private static void close() {
      if (open && !closing) {
         holdMode = false;
         closing = true;
         animationStartedAt = System.nanoTime();
      }
   }

   private static void forceClose() {
      if (isOpen()) {
         closing = false;
         finishClose();
      }
   }

   private static void finishClose() {
      open = false;
      closing = false;
      holdMode = false;
      heldBind = -1;
      hoveredSlot = -1;
      aimX = 0.0F;
      aimY = 0.0F;
      class_310 var0 = class_310.method_1551();
      if (cursorUnlocked) {
         cursorUnlocked = false;
         if (var0 != null && var0.field_1729 != null && var0.field_1755 == null && var0.field_1724 != null) {
            var0.field_1729.method_1612();
         }
      }
   }

   private static int slotAtMouse() {
      class_310 var0 = class_310.method_1551();
      if (var0 != null && var0.method_22683() != null && var0.field_1729 != null) {
         double var1 = var0.method_22683().method_4495();
         if (var1 <= 0.0) {
            return -1;
         }

         float var3 = var0.method_22683().method_4486() * 0.5F;
         float var4 = var0.method_22683().method_4502() * 0.5F;
         float var5 = (float)(var0.field_1729.method_1603() / var1) - var3;
         float var6 = (float)(var0.field_1729.method_1604() / var1) - var4;
         float var7 = (float)Math.sqrt(var5 * var5 + var6 * var6);
         return !(var7 < 32.0F) && !(var7 > 104.0F) ? slotAtAngle((float)Math.atan2(var6, var5)) : -1;
      } else {
         return -1;
      }
   }

   private static int slotAtAngle(float angle) {
      float var1 = angle - startAngle(0);

      while (var1 < 0.0F) {
         var1 += (float) (Math.PI * 2);
      }

      while (var1 >= (float) (Math.PI * 2)) {
         var1 -= (float) (Math.PI * 2);
      }

      return Math.min(8, (int)(var1 / sectorAngle()));
   }

   private static float sectorAngle() {
      return (float) (Math.PI * 2.0 / 9.0);
   }

   private static float startAngle(int slot) {
      return (float)((-Math.PI / 2) - sectorAngle() * 0.5F + slot * sectorAngle());
   }

   private static void drawSector(float cx, float cy, float inner, float outer, float start, float end, Color color) {
      int var7 = 12;

      for (int var8 = 0; var8 < var7; var8++) {
         float var9 = start + (end - start) * var8 / var7;
         float var10 = start + (end - start) * (var8 + 1) / var7;
         float var11 = cx + (float)Math.cos(var9) * inner;
         float var12 = cy + (float)Math.sin(var9) * inner;
         float var13 = cx + (float)Math.cos(var9) * outer;
         float var14 = cy + (float)Math.sin(var9) * outer;
         float var15 = cx + (float)Math.cos(var10) * inner;
         float var16 = cy + (float)Math.sin(var10) * inner;
         float var17 = cx + (float)Math.cos(var10) * outer;
         float var18 = cy + (float)Math.sin(var10) * outer;
         NanoVGRenderer.drawTriangle(var11, var12, var13, var14, var17, var18, color);
         NanoVGRenderer.drawTriangle(var11, var12, var17, var18, var15, var16, color);
      }
   }

   public static List emotes() {
      return EmotecraftBridge.emotes();
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
