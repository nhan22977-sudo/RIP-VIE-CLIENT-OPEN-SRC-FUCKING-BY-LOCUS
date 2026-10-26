package de.javagl.jgltf.model.io;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Objects;

public final class RawGltfData {
   private final ByteBuffer jsonData;
   private final ByteBuffer binaryData;

   public RawGltfData(ByteBuffer jsonData, ByteBuffer binaryData) {
      this.jsonData = Objects.requireNonNull(jsonData, "The jsonData may not be null");
      this.binaryData = binaryData;
   }

   public String getJsonString() {
      byte[] jsonDataArray = new byte[this.jsonData.capacity()];
      this.jsonData.slice().get(jsonDataArray);
      return new String(jsonDataArray, Charset.forName("UTF-8"));
   }

   public ByteBuffer getJsonData() {
      return Buffers.createSlice(this.jsonData);
   }

   public ByteBuffer getBinaryData() {
      return Buffers.createSlice(this.binaryData);
   }
}
