package de.javagl.jgltf.model.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class IO {
   public static URI makeAbsolute(URI baseUri, String uriString) throws IOException {
      try {
         String escapedUriString = uriString.replaceAll(" ", "%20");
         URI uri = new URI(escapedUriString);
         return uri.isAbsolute() ? uri : baseUri.resolve(escapedUriString);
      } catch (URISyntaxException e) {
         throw new IOException("Invalid URI string: " + uriString, e);
      }
   }

   public static Path makeAbsolute(Path basePath, String uriString) throws IOException {
      try {
         String escapedUriString = uriString.replaceAll(" ", "%20");
         URI uri = new URI(escapedUriString);
         return uri.isAbsolute() ? Paths.get(uri).toAbsolutePath() : basePath.resolve(escapedUriString).toAbsolutePath();
      } catch (URISyntaxException e) {
         throw new IOException("Invalid URI string: " + uriString, e);
      }
   }

   public static URI getParent(URI uri) {
      return uri.getPath().endsWith("/") ? uri.resolve("..") : uri.resolve(".");
   }

   public static Path getParent(Path path) {
      return path.getParent();
   }

   public static boolean isDataUri(URI uri) {
      return "data".equalsIgnoreCase(uri.getScheme());
   }

   public static boolean isDataUriString(String uriString) {
      if (uriString == null) {
         return false;
      }

      try {
         URI uri = new URI(uriString);
         return isDataUri(uri);
      } catch (URISyntaxException e) {
         return false;
      }
   }

   public static String extractFileName(URI uri) {
      String s = uri.toString();
      int lastSlashIndex = s.lastIndexOf(47);
      return lastSlashIndex != -1 ? s.substring(lastSlashIndex + 1) : s;
   }

   public static boolean existsUnchecked(URI uri) {
      try {
         return exists(uri);
      } catch (IOException e) {
         return false;
      }
   }

   private static boolean exists(URI uri) throws IOException {
      URL url = uri.toURL();
      URLConnection connection = url.openConnection();
      if (connection instanceof HttpURLConnection) {
         HttpURLConnection httpConnection = (HttpURLConnection)connection;
         httpConnection.setRequestMethod("HEAD");
         int responseCode = httpConnection.getResponseCode();
         return responseCode == 200;
      } else {
         String path = uri.getPath();
         return new File(path).exists();
      }
   }

   public static long getContentLength(URI uri) {
      try {
         URLConnection connection = uri.toURL().openConnection();
         return connection.getContentLengthLong();
      } catch (IOException e) {
         return -1L;
      }
   }

   public static InputStream createInputStream(URI uri) throws IOException {
      if ("data".equalsIgnoreCase(uri.getScheme())) {
         byte[] data = readDataUri(uri.toString());
         return new ByteArrayInputStream(data);
      }

      try {
         return uri.toURL().openStream();
      } catch (MalformedURLException e) {
         throw new IOException(e);
      }
   }

   public static InputStream createInputStream(Path path) throws IOException {
      if ("data".equalsIgnoreCase(path.toUri().getScheme())) {
         byte[] data = readDataUri(path.toUri().toString());
         return new ByteArrayInputStream(data);
      }

      try {
         return path.toUri().toURL().openStream();
      } catch (MalformedURLException e) {
         throw new IOException(e);
      }
   }

   public static byte[] read(URI uri) throws IOException {
      try (InputStream inputStream = createInputStream(uri)) {
         return readStream(inputStream);
      }
   }

   public static byte[] readDataUri(String uriString) {
      String encoding = "base64,";
      int encodingIndex = uriString.indexOf(encoding);
      if (encodingIndex < 0) {
         throw new IllegalArgumentException("The given URI string is not a base64 encoded data URI string: " + uriString);
      }

      int contentStartIndex = encodingIndex + encoding.length();
      return Base64.getDecoder().decode(uriString.substring(contentStartIndex));
   }

   public static byte[] readStream(InputStream inputStream) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[16384];

      do {
         int read = inputStream.read(buffer);
         if (read == -1) {
            baos.flush();
            return baos.toByteArray();
         }

         baos.write(buffer, 0, read);
      } while (!Thread.currentThread().isInterrupted());

      throw new IOException("Interrupted while reading stream", new InterruptedException());
   }

   static void read(InputStream inputStream, byte[] data, int offset, int numBytesToRead) throws IOException {
      if (offset < 0) {
         throw new IllegalArgumentException("Array offset is negative: " + offset);
      }

      if (offset + numBytesToRead > data.length) {
         throw new IllegalArgumentException("Cannot write " + numBytesToRead + " bytes into an array of length " + data.length + " with an offset of " + offset);
      }

      int totalNumBytesRead = 0;

      do {
         int read = inputStream.read(data, offset + totalNumBytesRead, numBytesToRead - totalNumBytesRead);
         if (read == -1) {
            throw new IOException("Could not read " + numBytesToRead + " bytes");
         }

         totalNumBytesRead += read;
      } while (totalNumBytesRead != numBytesToRead);
   }

   public static void read(InputStream inputStream, byte[] data) throws IOException {
      read(inputStream, data, 0, data.length);
   }

   private IO() {
   }
}
