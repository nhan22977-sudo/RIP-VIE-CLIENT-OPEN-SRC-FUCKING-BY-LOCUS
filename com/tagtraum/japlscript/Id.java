package com.tagtraum.japlscript;

public class Id {
   private final int value;

   public Id(int value) {
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }

   @Override
   public int hashCode() {
      return Integer.hashCode(this.value);
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Id) {
         return this.value == ((Id)obj).value;
      } else {
         return obj instanceof Integer ? Integer.valueOf(this.value).equals(obj) : false;
      }
   }

   @Override
   public String toString() {
      return "id " + this.value;
   }
}
