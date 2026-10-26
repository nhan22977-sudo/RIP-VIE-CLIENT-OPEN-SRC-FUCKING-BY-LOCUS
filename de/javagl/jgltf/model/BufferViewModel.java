package de.javagl.jgltf.model;

import java.nio.ByteBuffer;

public interface BufferViewModel extends NamedModelElement {
   ByteBuffer getBufferViewData();

   BufferModel getBufferModel();

   int getByteOffset();

   int getByteLength();

   Integer getByteStride();

   Integer getTarget();
}
