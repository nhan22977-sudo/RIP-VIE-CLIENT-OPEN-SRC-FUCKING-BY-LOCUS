package de.javagl.jgltf.model.impl;

import de.javagl.jgltf.model.BufferModel;
import de.javagl.jgltf.model.io.Buffers;
import java.nio.ByteBuffer;

public final class DefaultBufferModel extends AbstractNamedModelElement implements BufferModel {
   private String uri;
   private ByteBuffer bufferData;

   public void setUri(String uri) {
      this.uri = uri;
   }

   public void setBufferData(ByteBuffer bufferData) {
      this.bufferData = bufferData;
   }

   @Override
   public String getUri() {
      return this.uri;
   }

   @Override
   public int getByteLength() {
      return this.bufferData.capacity();
   }

   @Override
   public ByteBuffer getBufferData() {
      return Buffers.createSlice(this.bufferData);
   }
}
