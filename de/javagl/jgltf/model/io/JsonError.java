package de.javagl.jgltf.model.io;

import com.fasterxml.jackson.core.JsonStreamContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public final class JsonError {
   private final String message;
   private final List<String> jsonPath;
   private final Throwable throwable;

   JsonError(String message, JsonStreamContext jsonStreamContext, Throwable throwable) {
      this.message = message;
      this.jsonPath = Collections.unmodifiableList(createJsonPath(jsonStreamContext));
      this.throwable = throwable;
   }

   public String getMessage() {
      return this.message;
   }

   public List<String> getJsonPath() {
      return this.jsonPath;
   }

   public String getJsonPathString() {
      return this.jsonPath.stream().collect(Collectors.joining("."));
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   private static List<String> createJsonPath(JsonStreamContext streamContext) {
      Collection<JsonStreamContext> list = expand(streamContext);
      return list.stream().map(c -> c.getCurrentName() == null ? "" : c.getCurrentName()).collect(Collectors.toList());
   }

   private static Collection<JsonStreamContext> expand(JsonStreamContext streamContext) {
      Deque<JsonStreamContext> collection = new LinkedList<>();

      for (JsonStreamContext current = streamContext; current != null; current = current.getParent()) {
         collection.addFirst(current);
      }

      return collection;
   }
}
