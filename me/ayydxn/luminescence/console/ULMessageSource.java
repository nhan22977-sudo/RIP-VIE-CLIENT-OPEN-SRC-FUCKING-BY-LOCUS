package me.ayydxn.luminescence.console;

import me.ayydxn.luminescence.util.NativeEnum;
import me.ayydxn.luminescence.util.UsedByNative;

public enum ULMessageSource implements NativeEnum {
   XML(0),
   JS(1),
   NETWORK(2),
   CONSOLE_API(3),
   STORAGE(4),
   APP_CACHE(5),
   RENDERING(6),
   CSS(7),
   SECURITY(8),
   CONTENT_BLOCKER(9),
   MEDIA(10),
   MEDIA_SOURCE(11),
   WEB_RTC(12),
   ITP_DEBUG(13),
   PRIVATE_CLICK_MEASUREMENT(14),
   PAYMENT_REQUEST(15),
   OTHER(16);

   private final int value;

   ULMessageSource(int value) {
      this.value = value;
   }

   @Override
   public int getValue() {
      return this.value;
   }

   @UsedByNative("ViewCallbackAdapter.cpp")
   public static ULMessageSource fromNativeValue(int value) {
      return NativeEnum.resolve(ULMessageSource.class, value, OTHER);
   }

   // $VF: synthetic method
   private static ULMessageSource[] $values() {
      return new ULMessageSource[]{
         XML,
         JS,
         NETWORK,
         CONSOLE_API,
         STORAGE,
         APP_CACHE,
         RENDERING,
         CSS,
         SECURITY,
         CONTENT_BLOCKER,
         MEDIA,
         MEDIA_SOURCE,
         WEB_RTC,
         ITP_DEBUG,
         PRIVATE_CLICK_MEASUREMENT,
         PAYMENT_REQUEST,
         OTHER
      };
   }
}
