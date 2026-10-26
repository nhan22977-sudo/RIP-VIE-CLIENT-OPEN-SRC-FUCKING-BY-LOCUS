package me.ayydxn.luminescence.gpu;

import java.nio.ByteBuffer;
import me.ayydxn.luminescence.util.NativeEnum;
import me.ayydxn.luminescence.util.UsedByNative;

public class ULVertexBuffer {
   private final ULVertexBuffer.Format format;
   private final ByteBuffer data;

   public ULVertexBuffer(ULVertexBuffer.Format format, ByteBuffer data) {
      this.format = format;
      this.data = data;
   }

   public ULVertexBuffer.Format getFormat() {
      return this.format;
   }

   public ByteBuffer getData() {
      return this.data;
   }

   public enum Format implements NativeEnum {
      PATH(0),
      QUAD(1);

      private final int value;

      Format(int value) {
         this.value = value;
      }

      @Override
      public int getValue() {
         return this.value;
      }

      @UsedByNative("GPUDriverCallbackAdapter.cpp")
      public static ULVertexBuffer.Format fromNativeValue(int value) {
         return NativeEnum.resolve(ULVertexBuffer.Format.class, value, PATH);
      }

      // $VF: synthetic method
      private static ULVertexBuffer.Format[] $values() {
         return new ULVertexBuffer.Format[]{PATH, QUAD};
      }
   }
}
