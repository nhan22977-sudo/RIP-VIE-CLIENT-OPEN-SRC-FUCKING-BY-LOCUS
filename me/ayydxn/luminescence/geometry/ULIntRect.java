package me.ayydxn.luminescence.geometry;

public class ULIntRect {
   public int left;
   public int top;
   public int right;
   public int bottom;

   public ULIntRect(int left, int top, int right, int bottom) {
      this.left = left;
      this.top = top;
      this.right = right;
      this.bottom = bottom;
   }

   public boolean isEmpty() {
      return this.left == 0 && this.right == 0 && this.top == 0 && this.bottom == 0;
   }
}
