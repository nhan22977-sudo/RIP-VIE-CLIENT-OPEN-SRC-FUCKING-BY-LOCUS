package me.drake.vieclient.render;

import org.joml.Matrix4f;

final class FrameMatrixCache {
   private final Matrix4f projection = new Matrix4f();
   private final Matrix4f modelView = new Matrix4f();
   private Object value;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   Object get(Matrix4f proj, Matrix4f view) {
      return this.value != null && this.projection.equals(proj) && this.modelView.equals(view) ? this.value : null;
   }

   Object put(Matrix4f proj, Matrix4f view, Object value) {
      this.projection.set(proj);
      this.modelView.set(view);
      this.value = valuex;
      return valuex;
   }

   void clear() {
      this.value = null;
   }
}
