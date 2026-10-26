package de.labystudio.spotifyapi.platform.linux.api.model;

public class Parameter {
   private final String key;
   private final String value;

   public Parameter(String key, String value) {
      this.key = key;
      this.value = value;
   }

   public Parameter(String key) {
      this(key, null);
   }

   @Override
   public String toString() {
      return String.format("--%s%s", this.key, this.value == null ? "" : "=" + this.value);
   }
}
