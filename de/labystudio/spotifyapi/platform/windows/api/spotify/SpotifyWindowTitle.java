package de.labystudio.spotifyapi.platform.windows.api.spotify;

public class SpotifyWindowTitle {
   public static final String DELIMITER = " - ";
   public static final SpotifyWindowTitle UNKNOWN = new SpotifyWindowTitle("Unknown", "No song playing");
   private final String name;
   private final String artist;

   public SpotifyWindowTitle(String name, String artist) {
      this.name = name;
      this.artist = artist;
   }

   public String getTrackName() {
      return this.name;
   }

   public String getTrackArtist() {
      return this.artist;
   }

   @Override
   public String toString() {
      return this.name + " - " + this.artist;
   }

   public static SpotifyWindowTitle of(String title) {
      if (!title.contains(" - ")) {
         return null;
      }

      String[] split = title.split(" - ");
      return new SpotifyWindowTitle(split[1], split[0]);
   }
}
