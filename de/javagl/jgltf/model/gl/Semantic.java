package de.javagl.jgltf.model.gl;

public enum Semantic {
   LOCAL,
   MODEL,
   VIEW,
   PROJECTION,
   MODELVIEW,
   MODELVIEWPROJECTION,
   MODELINVERSE,
   VIEWINVERSE,
   MODELVIEWINVERSE,
   PROJECTIONINVERSE,
   MODELVIEWPROJECTIONINVERSE,
   MODELINVERSETRANSPOSE,
   MODELVIEWINVERSETRANSPOSE,
   VIEWPORT,
   JOINTMATRIX;

   public static boolean contains(String s) {
      for (Semantic semantic : values()) {
         if (semantic.name().equals(s)) {
            return true;
         }
      }

      return false;
   }

   public static Semantic forString(String string) {
      if (string == null) {
         return null;
      } else {
         return !contains(string) ? null : valueOf(string);
      }
   }
}
