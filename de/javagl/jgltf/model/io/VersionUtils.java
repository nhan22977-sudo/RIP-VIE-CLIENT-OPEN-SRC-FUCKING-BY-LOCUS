package de.javagl.jgltf.model.io;

public class VersionUtils {
   public static int compareVersions(String v0, String v1) {
      int[] sv0 = computeMajorMinorPatch(v0);
      int[] sv1 = computeMajorMinorPatch(v1);

      for (int i = 0; i < 3; i++) {
         int c = Integer.compare(sv0[i], sv1[i]);
         if (c != 0) {
            return c;
         }
      }

      return 0;
   }

   static int[] computeMajorMinorPatch(String v) {
      int[] result = new int[3];
      String[] tokens = v.split("\\.");
      int n = Math.min(tokens.length, 3);

      for (int i = 0; i < n; i++) {
         String token = tokens[i];
         result[i] = parseIntPrefix(token);
      }

      return result;
   }

   private static int parseIntPrefix(String s) {
      String number = "";

      for (int j = 0; j < s.length(); j++) {
         char c = s.charAt(j);
         if (!Character.isDigit(c)) {
            break;
         }

         number = number + c;
      }

      try {
         return Integer.parseInt(number);
      } catch (NumberFormatException e) {
         return 0;
      }
   }
}
