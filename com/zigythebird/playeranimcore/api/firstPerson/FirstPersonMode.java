package com.zigythebird.playeranimcore.api.firstPerson;

public enum FirstPersonMode {
   NONE(false),
   VANILLA(true),
   THIRD_PERSON_MODEL(true),
   DISABLED(false);

   private final boolean enabled;

   public boolean isEnabled() {
      return this.enabled;
   }

   FirstPersonMode(boolean enabled) {
      this.enabled = enabled;
   }

   // $VF: synthetic method
   private static FirstPersonMode[] $values() {
      return new FirstPersonMode[]{NONE, VANILLA, THIRD_PERSON_MODEL, DISABLED};
   }
}
