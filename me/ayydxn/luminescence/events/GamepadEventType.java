package me.ayydxn.luminescence.events;

public enum GamepadEventType {
   CONNECTED,
   DISCONNECTED;

   // $VF: synthetic method
   private static GamepadEventType[] $values() {
      return new GamepadEventType[]{CONNECTED, DISCONNECTED};
   }
}
