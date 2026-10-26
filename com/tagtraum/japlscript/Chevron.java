package com.tagtraum.japlscript;

import java.util.Objects;

public class Chevron {
   private final String kind;
   private final String code;

   public Chevron(String kind, String code) {
      if (kind.indexOf(32) != -1) {
         throw new IllegalArgumentException("Chevron kind must not contain spaces: " + kind);
      }

      if (code.length() != 4) {
         throw new IllegalArgumentException("Chevron code must be four characters long: " + code);
      }

      this.kind = kind;
      this.code = code;
   }

   public static Chevron parse(String chevronEncodedCode) {
      String trimmedChevronEncodedCode = chevronEncodedCode.trim();
      if (!trimmedChevronEncodedCode.startsWith("«")) {
         throw new IllegalArgumentException("Chevron encoded code must start with \"«\", but does not: " + chevronEncodedCode);
      }

      if (!trimmedChevronEncodedCode.endsWith("»")) {
         throw new IllegalArgumentException("Chevron encoded code must end with \"»\", but does not: " + chevronEncodedCode);
      }

      int space = trimmedChevronEncodedCode.indexOf(32);
      String kind = trimmedChevronEncodedCode.substring(1, space);
      String code = trimmedChevronEncodedCode.substring(space + 1, trimmedChevronEncodedCode.length() - 1);
      return new Chevron(kind, code);
   }

   public String getKind() {
      return this.kind;
   }

   public String getCode() {
      return this.code;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Chevron chevron = (Chevron)o;
         return this.kind.equals(chevron.kind) && this.code.equals(chevron.code);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.kind, this.code);
   }

   @Override
   public String toString() {
      return "«" + this.kind + " " + this.code + "»";
   }
}
