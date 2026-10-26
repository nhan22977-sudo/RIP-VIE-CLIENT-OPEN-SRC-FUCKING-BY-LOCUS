package de.javagl.jgltf.model;

import java.nio.ByteBuffer;

public interface BufferModel extends NamedModelElement {
   String getUri();

   int getByteLength();

   ByteBuffer getBufferData();
}
