package me.drake.vieclient.modules.client.hud;

import org.lwjgl.glfw.GLFW;

public final class HudResizeCursor implements AutoCloseable {
   private long nwse;
   private long nesw;
   private long active;
   private long window;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public static int glfwShape(HudEditorGeometry.Corner corner) {
      return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$client$hud$HudEditorGeometry$Corner[corner.ordinal()]) {
         case 1, 2 -> 221191;
         case 3, 4 -> 221192;
         default -> throw new MatchException(null, null);
      };
   }

   public void apply(long window, HudEditorGeometry.Corner corner) {
      this.window = windowx;
      long var4 = 0L;
      if (corner != null) {
         if (glfwShape(corner) == (221191)) {
            if (this.nwse == 0L) {
               this.nwse = GLFW.glfwCreateStandardCursor(221191);
            }

            var4 = this.nwse;
         } else {
            if (this.nesw == 0L) {
               this.nesw = GLFW.glfwCreateStandardCursor(221192);
            }

            var4 = this.nesw;
         }
      }

      if (var4 != this.active) {
         GLFW.glfwSetCursor(windowx, var4);
         this.active = var4;
      }
   }

   public void reset(long window) {
      if (windowx != 0L) {
         GLFW.glfwSetCursor(windowx, 0L);
      }

      this.active = 0L;
   }

   @Override
   public void close() {
      this.reset(this.window);
      if (this.nwse != 0L) {
         GLFW.glfwDestroyCursor(this.nwse);
      }

      if (this.nesw != 0L) {
         GLFW.glfwDestroyCursor(this.nesw);
      }

      this.nwse = 0L;
      this.nesw = 0L;
      this.window = 0L;
   }
}
