package io.github.kosmx.emotes.common.network;

import com.zigythebird.playeranimcore.network.LegacyAnimationBinary;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CommonNetwork {
   public static String readString(ByteBuf buf) {
      return LegacyAnimationBinary.getString(buf);
   }

   public static void writeString(ByteBuf buf, String str) {
      if (str != null && !str.isBlank()) {
         LegacyAnimationBinary.putString(buf, str);
      } else {
         buf.writeInt(0);
      }
   }

   public static <T> List<T> readList(ByteBuf buf, Function<ByteBuf, T> reader) {
      int count = buf.readInt();
      List<T> list = new ArrayList<>(count);

      for (int i = 0; i < count; i++) {
         list.add(reader.apply(buf));
      }

      return list;
   }

   public static <T> void writeList(ByteBuf buf, List<T> elements, BiConsumer<ByteBuf, T> writter) {
      if (elements == null) {
         buf.writeInt(0);
      } else {
         buf.writeInt(elements.size());

         for (T entry : elements) {
            writter.accept(buf, entry);
         }
      }
   }

   public static boolean readBoolean(ByteBuf buf) {
      return buf.readByte() != 0;
   }

   public static void writeBoolean(ByteBuf buf, boolean bool) {
      buf.writeByte((byte)(bool ? 1 : 0));
   }
}
