package de.javagl.jgltf.model;

public class GltfException extends RuntimeException {
   private static final long serialVersionUID = -1052127064537015753L;

   public GltfException(String message) {
      super(message);
   }

   public GltfException(String message, Throwable cause) {
      super(message, cause);
   }
}
