package net.ostore.ultralight;

import java.util.function.Consumer;

public final class UltralightJsBridge {
   private volatile Consumer handler;

   UltralightJsBridge(Consumer handler) {
      this.handler = handlerx;
   }

   Consumer getHandler() {
      return this.handler;
   }

   void updateHandler(Consumer newHandler) {
      this.handler = newHandler;
   }

   void dispose() {
      this.handler = null;
   }
}
