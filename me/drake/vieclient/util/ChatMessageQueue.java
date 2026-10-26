package me.drake.vieclient.util;

import java.util.concurrent.ConcurrentLinkedDeque;

public final class ChatMessageQueue {
   private static final int MAX_QUEUE_SIZE = 64;
   private static final ConcurrentLinkedDeque QUEUE = new ConcurrentLinkedDeque();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private ChatMessageQueue() {
   }

   public static void push(String message) {
      if (message != null && !message.isEmpty()) {
         if (QUEUE.size() >= (64)) {
            QUEUE.pollFirst();
         }

         QUEUE.offerLast(message);
      }
   }

   public static String poll() {
      return (String)QUEUE.pollFirst();
   }

   public static boolean isEmpty() {
      return QUEUE.isEmpty();
   }

   public static int size() {
      return QUEUE.size();
   }
}
