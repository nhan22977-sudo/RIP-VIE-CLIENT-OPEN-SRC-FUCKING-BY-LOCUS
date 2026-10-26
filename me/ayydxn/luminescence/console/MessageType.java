package me.ayydxn.luminescence.console;

public enum MessageType {
   LOG,
   DIR,
   DIR_XML,
   TABLE,
   TRACE,
   START_GROUP,
   START_GROUP_COLLAPSED,
   END_GROUP,
   CLEAR,
   ASSERT,
   TIMING,
   PROFILE,
   PROFILE_END,
   IMAGE;

   // $VF: synthetic method
   private static MessageType[] $values() {
      return new MessageType[]{
         LOG, DIR, DIR_XML, TABLE, TRACE, START_GROUP, START_GROUP_COLLAPSED, END_GROUP, CLEAR, ASSERT, TIMING, PROFILE, PROFILE_END, IMAGE
      };
   }
}
