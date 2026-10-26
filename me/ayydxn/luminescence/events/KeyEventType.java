package me.ayydxn.luminescence.events;

public enum KeyEventType {
   KEY_DOWN,
   KEY_UP,
   RAW_KEY_DOWN,
   CHAR;

   // $VF: synthetic method
   private static KeyEventType[] $values() {
      return new KeyEventType[]{KEY_DOWN, KEY_UP, RAW_KEY_DOWN, CHAR};
   }
}
