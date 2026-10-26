package de.labystudio.spotifyapi.open;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import de.labystudio.spotifyapi.model.Track;
import de.labystudio.spotifyapi.open.model.AccessTokenResponse;
import de.labystudio.spotifyapi.open.model.GraphQLOperation;
import de.labystudio.spotifyapi.open.model.track.Image;
import de.labystudio.spotifyapi.open.model.track.OpenTrack;
import de.labystudio.spotifyapi.open.model.track.TrackResponse;
import de.labystudio.spotifyapi.open.totp.TOTP;
import de.labystudio.spotifyapi.open.totp.gson.SecretDeserializer;
import de.labystudio.spotifyapi.open.totp.gson.SecretSerializer;
import de.labystudio.spotifyapi.open.totp.model.Secret;
import de.labystudio.spotifyapi.open.totp.provider.SecretProvider;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

public class OpenSpotifyAPI {
   public static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(Secret.class, new SecretDeserializer())
      .registerTypeAdapter(Secret.class, new SecretSerializer())
      .create();
   public static final String USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537."
      + (int)(Math.random() * 90.0);
   public static final String URL_API_GEN_ACCESS_TOKEN = "https://open.spotify.com/api/token?reason=%s&productType=web-player&totp=%s&totpServer=%s&totpVer=%s";
   public static final String URL_API_GRAPHQL = "https://api-partner.spotify.com/pathfinder/v1/query";
   public static final String URL_API_SERVER_TIME = "https://open.spotify.com/api/server-time";
   private final Executor executor = Executors.newSingleThreadExecutor(runnable -> {
      Thread thread = new Thread(runnable, "spotify-open-api");
      thread.setDaemon(true);
      return thread;
   });
   private final Cache<BufferedImage> imageCache = new Cache<>(10);
   private final Cache<OpenTrack> openTrackCache = new Cache<>(100);
   private final SecretProvider secretProvider;
   private AccessTokenResponse accessTokenResponse;

   public OpenSpotifyAPI(SecretProvider secretProvider) {
      this.secretProvider = secretProvider;
   }

   private void generateAccessTokenAsync(Consumer<AccessTokenResponse> callback) {
      this.executor.execute(() -> {
         try {
            callback.accept(this.generateAccessToken());
         } catch (Exception error) {
            error.printStackTrace();
         }
      });
   }

   public long requestServerTime() throws IOException {
      URL url = new URL("https://open.spotify.com/api/server-time");
      HttpURLConnection conn = (HttpURLConnection)url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("Host", "open.spotify.com");
      conn.setRequestProperty("User-Agent", USER_AGENT);
      conn.setRequestProperty("Accept", "application/json");
      BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String response = reader.readLine();
      reader.close();
      JsonObject obj = (JsonObject)GSON.fromJson(response, JsonObject.class);
      return obj.get("serverTime").getAsLong();
   }

   private AccessTokenResponse generateAccessToken() throws IOException {
      Secret secret = this.secretProvider.getSecret();
      if (secret == null) {
         throw new IOException("No TOTP secret provided");
      }

      long serverTime = this.requestServerTime();
      String totp = TOTP.generateOtp(secret.getSecretAsBytes(), serverTime, 30, 6);
      AccessTokenResponse response = this.getToken("transport", totp, secret.getVersion());
      if (!this.hasValidAccessToken(response)) {
         response = this.getToken("init", totp, secret.getVersion());
      }

      if (!this.hasValidAccessToken(response)) {
         throw new IOException("Could not generate access token");
      } else {
         return response;
      }
   }

   private AccessTokenResponse getToken(String mode, String totp, int version) throws IOException {
      String url = String.format(
         "https://open.spotify.com/api/token?reason=%s&productType=web-player&totp=%s&totpServer=%s&totpVer=%s", mode, totp, totp, version
      );
      HttpsURLConnection connection = (HttpsURLConnection)new URL(url).openConnection();
      connection.addRequestProperty("User-Agent", USER_AGENT);
      connection.addRequestProperty("referer", "https://open.spotify.com/");
      connection.addRequestProperty("app-platform", "WebPlayer");
      connection.setRequestProperty("Accept", "application/json");
      int code = connection.getResponseCode();
      if (code != 200) {
         InputStream errorStream = connection.getErrorStream();
         if (errorStream != null) {
            JsonReader reader = new JsonReader(new InputStreamReader(errorStream));
            JsonObject response = (JsonObject)GSON.fromJson(reader, JsonObject.class);
            throw new IOException("Could not retrieve access token: " + response.toString());
         }
      }

      JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream()));
      return (AccessTokenResponse)GSON.fromJson(reader, AccessTokenResponse.class);
   }

   public void requestImageAsync(Track track, Consumer<BufferedImage> callback) {
      this.requestImageAsync(track.getId(), callback);
   }

   public void requestImageAsync(String trackId, Consumer<BufferedImage> callback) {
      if (!Track.isTrackIdValid(trackId)) {
         throw new IllegalArgumentException("Invalid track ID: " + trackId);
      }

      this.executor.execute(() -> {
         try {
            BufferedImage image = this.requestImage(trackId);
            if (image != null) {
               callback.accept(image);
            }
         } catch (Exception error) {
            error.printStackTrace();
         }
      });
   }

   public void requestImageUrlAsync(String trackId, Consumer<String> callback) {
      this.executor.execute(() -> {
         try {
            String imageUrl = this.requestImageUrl(trackId);
            if (imageUrl != null) {
               callback.accept(imageUrl);
            }
         } catch (Exception error) {
            error.printStackTrace();
         }
      });
   }

   public void requestOpenTrackAsync(Track track, Consumer<OpenTrack> callback) {
      this.requestOpenTrackAsync(track.getId(), callback);
   }

   public void requestOpenTrackAsync(String trackId, Consumer<OpenTrack> callback) {
      this.executor.execute(() -> {
         try {
            OpenTrack openTrack = this.requestOpenTrack(trackId);
            if (openTrack != null) {
               callback.accept(openTrack);
            }
         } catch (Exception error) {
            error.printStackTrace();
         }
      });
   }

   public BufferedImage requestImage(Track track) throws IOException {
      return this.requestImage(track.getId());
   }

   public BufferedImage requestImage(String trackId) throws IOException {
      if (!Track.isTrackIdValid(trackId)) {
         throw new IllegalArgumentException("Invalid track ID: " + trackId);
      }

      BufferedImage cachedImage = this.imageCache.get(trackId);
      if (cachedImage != null) {
         return cachedImage;
      }

      String url = this.requestImageUrl(trackId);
      if (url == null) {
         return null;
      }

      BufferedImage image = ImageIO.read(new URL(url));
      if (image == null) {
         throw new IOException("Could not load image: " + url);
      }

      this.imageCache.push(trackId, image);
      return image;
   }

   private String requestImageUrl(String trackId) throws IOException {
      OpenTrack openTrack = this.requestOpenTrack(trackId);
      if (openTrack != null && openTrack.album != null) {
         List<Image> images = openTrack.album.getImages();
         return images != null && !images.isEmpty() ? images.get(0).url : null;
      } else {
         return null;
      }
   }

   public OpenTrack requestOpenTrack(Track track) throws IOException {
      return this.requestOpenTrack(track.getId());
   }

   public OpenTrack requestOpenTrack(String trackId) throws IOException {
      OpenTrack cachedOpenTrack = this.openTrackCache.get(trackId);
      if (cachedOpenTrack != null) {
         return cachedOpenTrack;
      }

      TrackResponse graphQLResponse = this.requestTrack(trackId);
      OpenTrack openTrack = graphQLResponse != null && graphQLResponse.data != null ? graphQLResponse.data.trackUnion : null;
      if (openTrack == null) {
         return null;
      }

      this.openTrackCache.push(trackId, openTrack);
      return openTrack;
   }

   private TrackResponse requestTrack(String trackId) throws IOException {
      JsonObject variables = new JsonObject();
      variables.addProperty("uri", "spotify:track:" + trackId);
      return this.requestGraphQL(GraphQLOperation.GET_TRACK, variables, TrackResponse.class);
   }

   public <T> T requestGraphQL(GraphQLOperation operation, JsonObject variables, Class<T> clazz) throws IOException {
      return this.requestGraphQL(operation, variables, clazz, true);
   }

   private <T> T requestGraphQL(GraphQLOperation operation, JsonObject variables, Class<T> clazz, boolean canGenerateNewAccessToken) throws IOException {
      if (this.accessTokenResponse == null) {
         this.accessTokenResponse = this.generateAccessToken();
      }

      JsonObject query = new JsonObject();
      query.addProperty("operationName", operation.getOperationName());
      query.add("variables", variables);
      JsonObject extensions = new JsonObject();
      JsonObject persistedQuery = new JsonObject();
      persistedQuery.addProperty("version", 1);
      persistedQuery.addProperty("sha256Hash", operation.getSha256Hash());
      extensions.add("persistedQuery", persistedQuery);
      query.add("extensions", extensions);
      HttpsURLConnection connection = (HttpsURLConnection)new URL("https://api-partner.spotify.com/pathfinder/v1/query").openConnection();
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.addRequestProperty("User-Agent", USER_AGENT);
      connection.addRequestProperty("referer", "https://open.spotify.com/");
      connection.addRequestProperty("app-platform", "WebPlayer");
      connection.addRequestProperty("origin", "https://open.spotify.com");
      connection.addRequestProperty("content-type", "application/json");
      if (this.accessTokenResponse != null) {
         connection.addRequestProperty("authorization", "Bearer " + this.accessTokenResponse.accessToken);
      }

      try (OutputStream os = connection.getOutputStream()) {
         byte[] input = GSON.toJson(query).getBytes(StandardCharsets.UTF_8);
         os.write(input, 0, input.length);
      }

      if (connection.getResponseCode() / 100 != 2) {
         if (canGenerateNewAccessToken) {
            this.accessTokenResponse = this.generateAccessToken();
            return this.requestGraphQL(operation, variables, clazz, false);
         } else {
            return null;
         }
      } else {
         JsonReader reader = new JsonReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
         return (T)GSON.fromJson(reader, clazz);
      }
   }

   private boolean hasValidAccessToken(AccessTokenResponse response) {
      return response != null && response.accessToken != null && !response.accessToken.isEmpty();
   }

   public Cache<BufferedImage> getImageCache() {
      return this.imageCache;
   }

   public Cache<OpenTrack> getOpenTrackCache() {
      return this.openTrackCache;
   }
}
