package de.labystudio.spotifyapi.open.model.track;

public class TrackResponse {
   public TrackResponse.Data data;

   public static class Data {
      public OpenTrack trackUnion;
   }
}
