package de.labystudio.spotifyapi.open.model.track;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class OpenTrack {
   public String id;
   public String name;
   public String uri;
   @SerializedName("duration")
   public OpenTrack.Duration duration;
   @SerializedName("albumOfTrack")
   public OpenTrack.AlbumOfTrack album;
   @SerializedName("firstArtist")
   public OpenTrack.FirstArtist artistsData;
   @SerializedName("contentRating")
   public OpenTrack.ContentRating contentRating;
   @SerializedName("playcount")
   public String playcount;
   private transient String joinedArtists;
   private transient List<Artist> artistsList;

   public Integer getDurationMs() {
      return this.duration != null ? this.duration.durationMs : null;
   }

   public boolean isExplicit() {
      return this.contentRating != null && this.contentRating.isExplicit();
   }

   public List<Artist> getArtists() {
      if (this.artistsList == null && this.artistsData != null && this.artistsData.items != null) {
         this.artistsList = new ArrayList<>();

         for (OpenTrack.FirstArtist.ArtistItem item : this.artistsData.items) {
            Artist artist = new Artist();
            artist.id = item.id;
            artist.uri = item.uri;
            if (item.profile != null) {
               artist.name = item.profile.name;
            }

            this.artistsList.add(artist);
         }
      }

      return this.artistsList;
   }

   public String getArtistsString() {
      if (this.joinedArtists == null) {
         this.joinedArtists = this.getArtistsString(", ");
      }

      return this.joinedArtists;
   }

   public String getArtistsString(String delimiter) {
      List<Artist> artists = this.getArtists();
      if (artists != null && !artists.isEmpty()) {
         StringBuilder builder = new StringBuilder();

         for (Artist artist : artists) {
            builder.append(delimiter);
            builder.append(artist.name);
         }

         return builder.substring(delimiter.length());
      } else {
         return null;
      }
   }

   public static class AlbumOfTrack {
      public String id;
      public String name;
      public String uri;
      @SerializedName("coverArt")
      public OpenTrack.AlbumOfTrack.CoverArt coverArt;

      public List<Image> getImages() {
         return this.coverArt != null ? this.coverArt.sources : null;
      }

      public static class CoverArt {
         public List<Image> sources;
      }
   }

   public static class ContentRating {
      public String label;

      public boolean isExplicit() {
         return "EXPLICIT".equals(this.label);
      }
   }

   public static class Duration {
      @SerializedName("totalMilliseconds")
      public Integer durationMs;
   }

   public static class FirstArtist {
      public List<OpenTrack.FirstArtist.ArtistItem> items;

      public static class ArtistItem {
         public String id;
         public String uri;
         public OpenTrack.FirstArtist.ArtistItem.Profile profile;

         public static class Profile {
            public String name;
         }
      }
   }
}
