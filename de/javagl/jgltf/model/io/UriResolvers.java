package de.javagl.jgltf.model.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Logger;

public class UriResolvers {
   private static final Logger logger = Logger.getLogger(UriResolvers.class.getName());

   public static Function<String, ByteBuffer> createBaseUriResolver(final URI baseUri) {
      Objects.requireNonNull(baseUri, "The baseUri may not be null");
      Function<String, InputStream> inputStreamFunction = new Function<String, InputStream>() {
         public InputStream apply(String uriString) {
            try {
               URI absoluteUri = IO.makeAbsolute(baseUri, uriString);
               return IO.createInputStream(absoluteUri);
            } catch (IOException e) {
               UriResolvers.logger.warning("Could not open input stream for URI " + uriString + ":  " + e.getMessage());
               return null;
            }
         }
      };
      return reading(inputStreamFunction);
   }

   public static Function<String, ByteBuffer> createBasePathResolver(final Path basePath) {
      Objects.requireNonNull(basePath, "The basePath may not be null");
      Function<String, InputStream> inputStreamFunction = new Function<String, InputStream>() {
         public InputStream apply(String uriString) {
            try {
               if (IO.isDataUriString(uriString)) {
                  return IO.createInputStream(URI.create(uriString));
               }

               Path absolutePath = IO.makeAbsolute(basePath, uriString);
               return IO.createInputStream(absolutePath);
            } catch (IOException e) {
               UriResolvers.logger.warning("Could not open input stream for URI " + uriString + ":  " + e.getMessage());
               return null;
            }
         }
      };
      return reading(inputStreamFunction);
   }

   public static Function<String, ByteBuffer> createResourceUriResolver(final Class<?> c) {
      Objects.requireNonNull(c, "The class may not be null");
      Function<String, InputStream> inputStreamFunction = new Function<String, InputStream>() {
         public InputStream apply(String uriString) {
            InputStream inputStream = c.getResourceAsStream("/" + uriString);
            if (inputStream == null) {
               UriResolvers.logger.warning("Could not obtain input stream for resource with URI " + uriString);
            }

            return inputStream;
         }
      };
      return reading(inputStreamFunction);
   }

   private static <T> Function<T, ByteBuffer> reading(final Function<? super T, ? extends InputStream> inputStreamFunction) {
      return new Function<T, ByteBuffer>() {
         public ByteBuffer apply(T t) {
            try (InputStream inputStream = inputStreamFunction.apply(t)) {
               if (inputStream == null) {
                  UriResolvers.logger.warning("The input stream was null");
                  return null;
               } else {
                  byte[] data = IO.readStream(inputStream);
                  return Buffers.create(data);
               }
            } catch (IOException e) {
               UriResolvers.logger.warning("Could not read from input stream: " + e.getMessage());
               return null;
            }
         }
      };
   }

   private UriResolvers() {
   }
}
