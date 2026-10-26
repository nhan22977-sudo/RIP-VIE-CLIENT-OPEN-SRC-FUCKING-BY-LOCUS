package de.javagl.jgltf.model.image;

import java.nio.ByteBuffer;

public interface PixelData {
   int getWidth();

   int getHeight();

   ByteBuffer getPixelsRGBA();
}
