package com.tagtraum.japlscript;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class Identifiers {
   private static final Set<String> JAVA_KEYWORDS = new HashSet<>(
      Arrays.asList(
         "_",
         "abstract",
         "assert",
         "boolean",
         "break",
         "byte",
         "case",
         "catch",
         "char",
         "class",
         "const",
         "continue",
         "default",
         "double",
         "do",
         "else",
         "enum",
         "extends",
         "false",
         "final",
         "finally",
         "float",
         "for",
         "goto",
         "if",
         "implements",
         "import",
         "instanceof",
         "int",
         "interface",
         "long",
         "native",
         "new",
         "non-sealed",
         "null",
         "package",
         "private",
         "protected",
         "public",
         "return",
         "short",
         "static",
         "strictfp",
         "super",
         "switch",
         "synchronized",
         "this",
         "throw",
         "throws",
         "transient",
         "true",
         "try",
         "void",
         "volatile",
         "while"
      )
   );

   private Identifiers() {
   }

   public static String toJavaConstant(String name) {
      return toJavaIdentifier(name.toUpperCase());
   }

   public static String toCamelCaseClassName(String name) {
      return toJavaIdentifier(toCamelCase(name, true));
   }

   public static String toCamelCaseMethodName(String name) {
      return toJavaIdentifier(toCamelCase(name, false));
   }

   public static String toCamelCase(String identifier, boolean uppercaseFirstLetter) {
      StringBuilder sb = new StringBuilder();
      boolean upperCase = uppercaseFirstLetter;

      for (int i = 0; i < identifier.length(); i++) {
         char c = identifier.charAt(i);
         if (!Character.isLetterOrDigit(c)) {
            upperCase = true;
         } else {
            if (upperCase) {
               sb.append(Character.toUpperCase(c));
            } else {
               sb.append(Character.toLowerCase(c));
            }

            upperCase = false;
         }
      }

      return sb.toString();
   }

   public static String toJavaIdentifier(String string) {
      StringBuilder sb = new StringBuilder(string.length());

      for (int i = 0; i < string.length(); i++) {
         char c = string.charAt(i);
         if (Character.isJavaIdentifierPart(c)) {
            sb.append(c);
         } else {
            sb.append('_');
         }
      }

      String s = sb.toString();
      return JAVA_KEYWORDS.contains(s) ? s + "_" : s;
   }
}
