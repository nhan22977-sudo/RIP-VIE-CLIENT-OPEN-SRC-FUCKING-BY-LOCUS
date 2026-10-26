package me.ayydxn.luminescence.gpu;

import me.ayydxn.luminescence.bitmap.BitmapFormat;
import me.ayydxn.luminescence.geometry.ULRect;

public class ULRenderTarget {
   public boolean isEmpty;
   public int width;
   public int height;
   public int textureID;
   public int textureWidth;
   public int textureHeight;
   public BitmapFormat bitmapFormat;
   public ULRect uvCoords;
   public int renderBufferID;
}
