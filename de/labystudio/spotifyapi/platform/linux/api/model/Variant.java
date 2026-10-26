package de.labystudio.spotifyapi.platform.linux.api.model;

import java.util.ArrayList;
import java.util.List;

public class Variant {
   private final String sig;
   private final Object value;

   public Variant(String sig, Object value) {
      this.sig = sig;
      this.value = value;
   }

   public String getSig() {
      return this.sig;
   }

   public <T> T getValue() {
      return (T)this.value;
   }

   @Override
   public String toString() {
      return this.sig + ":" + this.value;
   }

   public static Variant parse(String raw) {
      raw = raw.trim().replace("\n", "");

      while (raw.contains("  ")) {
         raw = raw.replace("  ", " ");
      }

      return new Variant("variant", parse0(raw));
   }

   private static Object parse0(String raw) {
      String[] segments = raw.split(" ", 2);
      if (segments.length != 2) {
         throw new IllegalArgumentException("Invalid variant: " + raw);
      } else {
         String signature = segments[0];
         String payload = segments[1];
         if (signature.startsWith("variant")) {
            String[] variantSegments = payload.split(" ", 2);
            return parseVariant(variantSegments[0], variantSegments[1]);
         } else if (signature.startsWith("dict")) {
            return parseDict(payload);
         } else {
            throw new IllegalArgumentException("Invalid variant signature: " + signature);
         }
      }
   }

   private static Object parseVariant(String type, String value) {
      switch (type) {
         case "array":
            String collection = value.substring(1, value.length() - 1).trim();
            List<Object> list = new ArrayList<>();
            StringBuilder buffer = new StringBuilder();
            boolean nested = false;
            boolean escaped = false;
            boolean primitive = false;
            String tempType = null;
            int i = 0;

            for (; i < collection.length(); i++) {
               char c = collection.charAt(i);
               if (c == '"') {
                  escaped = !escaped;
               }

               if (!escaped) {
                  if (c == '(') {
                     nested = true;
                  }

                  if (c == ')') {
                     nested = false;
                     list.add(parseDict(buffer + ")"));
                     buffer = new StringBuilder();
                     continue;
                  }

                  if (!nested && c == ' ' && buffer.length() > 0) {
                     String keyword = buffer.toString().trim();
                     buffer = new StringBuilder();
                     if (tempType == null) {
                        if (!keyword.equals("dict")) {
                           tempType = keyword;
                        }
                     } else {
                        Object variant = parseVariant(tempType, keyword);
                        if (!(variant instanceof Variant)) {
                           primitive = true;
                        }

                        list.add(variant);
                        tempType = null;
                     }
                  }
               }

               buffer.append(c);
            }

            if (tempType != null) {
               String keyword = buffer.toString().trim();
               Object variant = parseVariant(tempType, keyword);
               if (!(variant instanceof Variant)) {
                  primitive = true;
               }

               list.add(variant);
            }

            if (!primitive) {
               return list.toArray(new Variant[0]);
            } else {
               return list.get(0) instanceof String ? list.toArray(new String[0]) : list.toArray();
            }
         case "string":
            return value.substring(1, value.length() - 1);
         case "int32":
            return Integer.parseInt(value);
         case "uint32":
            return Integer.parseUnsignedInt(value);
         case "int64":
            return Long.parseLong(value);
         case "uint64":
            return Long.parseUnsignedLong(value);
         case "double":
            return Double.parseDouble(value);
         default:
            return value;
      }
   }

   private static Variant parseDict(String payload) {
      String sigType = null;
      String sig = null;
      StringBuilder buffer = new StringBuilder();
      boolean nested = false;
      boolean escaped = false;

      for (int i = 0; i < payload.length(); i++) {
         char c = payload.charAt(i);
         if (c == '"') {
            escaped = !escaped;
         }

         if (!escaped) {
            if (c == '(') {
               nested = true;
               continue;
            }

            if (c == ')') {
               nested = false;
               continue;
            }

            if (nested && c == ' ') {
               if (buffer.length() == 0) {
                  continue;
               }

               if (sigType == null) {
                  sigType = buffer.toString();
                  buffer = new StringBuilder();
                  if (!sigType.equals("string")) {
                     throw new IllegalArgumentException("Invalid dict sig type: " + sigType);
                  }
               } else if (sig == null) {
                  sig = (String)parseVariant(sigType, buffer.toString().trim());
                  buffer = new StringBuilder();
               }
            }
         }

         if (nested) {
            buffer.append(c);
         }
      }

      return new Variant(sig, parse0(buffer.toString().trim()));
   }
}
