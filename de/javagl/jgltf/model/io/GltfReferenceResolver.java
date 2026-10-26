package de.javagl.jgltf.model.io;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class GltfReferenceResolver {
   private static final Logger logger = Logger.getLogger(GltfReferenceResolver.class.getName());

   public static void resolveAll(Iterable<? extends GltfReference> references, URI baseUri) {
      Objects.requireNonNull(references, "The references may not be null");
      Objects.requireNonNull(baseUri, "The baseUri may not be null");
      Function<String, ByteBuffer> uriResolver = UriResolvers.createBaseUriResolver(baseUri);
      resolveAll(references, uriResolver);
   }

   public static void resolveAll(Iterable<? extends GltfReference> references, Path basePath) {
      Objects.requireNonNull(references, "The references may not be null");
      Objects.requireNonNull(basePath, "The basePath may not be null");
      Function<String, ByteBuffer> uriResolver = UriResolvers.createBasePathResolver(basePath);
      resolveAll(references, uriResolver);
   }

   public static void resolveAll(Iterable<? extends GltfReference> references, Function<? super String, ? extends ByteBuffer> uriResolver) {
      Objects.requireNonNull(references, "The references may not be null");
      Objects.requireNonNull(uriResolver, "The uriResolver may not be null");

      for (GltfReference reference : references) {
         resolve(reference, uriResolver);
      }
   }

   public static void resolve(GltfReference reference, Function<? super String, ? extends ByteBuffer> uriResolver) {
      Objects.requireNonNull(reference, "The reference may not be null");
      Objects.requireNonNull(uriResolver, "The uriResolver may not be null");
      String uri = reference.getUri();
      ByteBuffer byteBuffer = uriResolver.apply(uri);
      if (byteBuffer == null) {
         logger.warning("Could not resolve URI " + uri);
      }

      Consumer<ByteBuffer> target = reference.getTarget();
      target.accept(byteBuffer);
   }

   private GltfReferenceResolver() {
   }
}
