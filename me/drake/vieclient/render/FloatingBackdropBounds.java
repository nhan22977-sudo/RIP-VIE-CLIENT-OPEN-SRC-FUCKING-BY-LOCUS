package me.drake.vieclient.render;

public final class FloatingBackdropBounds {
   private float x;
   private float y;
   private float width;
   private float height;
   private boolean visible;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void update(float x, float y, float width, float height) {
      if (Float.isFinite(xx) && Float.isFinite(yx) && Float.isFinite(widthx) && Float.isFinite(heightx) && !(widthx < 1.0F) && !(heightx < 1.0F)) {
         this.x = xx;
         this.y = yx;
         this.width = widthx;
         this.height = heightx;
         this.visible = true;
      } else {
         this.clear();
      }
   }

   public void clear() {
      this.x = 0.0F;
      this.y = 0.0F;
      this.width = 0.0F;
      this.height = 0.0F;
      this.visible = false;
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public float width() {
      return this.width;
   }

   public float height() {
      return this.height;
   }

   public boolean isVisible() {
      return this.visible;
   }
}
