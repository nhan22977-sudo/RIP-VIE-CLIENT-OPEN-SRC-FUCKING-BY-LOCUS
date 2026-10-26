package de.labystudio.spotifyapi.platform.linux.api.model;

public class InterfaceMember {
   private final String path;

   public InterfaceMember(String path) {
      this.path = path;
   }

   @Override
   public String toString() {
      return this.path;
   }
}
