package de.javagl.jgltf.model;

import java.nio.ByteBuffer;

public interface ImageModel extends NamedModelElement {
   String getUri();

   String getMimeType();

   BufferViewModel getBufferViewModel();

   ByteBuffer getImageData();
}
