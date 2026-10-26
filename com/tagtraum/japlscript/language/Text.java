package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.JaplScript;

public class Text implements Codec<String> {
   private static final Text instance = new Text();
   private static final TypeClass[] CLASSES = new TypeClass[]{
      new TypeClass("text", new Chevron("class", "ctxt")),
      new TypeClass("unicode text", new Chevron("class", "utxt")),
      new TypeClass("styled unicode text", new Chevron("class", "sutx")),
      new TypeClass("styled text", new Chevron("class", "STXT").toString()),
      new TypeClass("international text", new Chevron("class", "itxt")),
      new TypeClass("number or string", new Chevron("class", "ns  ")),
      new TypeClass("list or string", new Chevron("class", "ls  ")),
      new TypeClass("alias or string", new Chevron("class", "sf  ")),
      new TypeClass("string", new Chevron("class", "TEXT")),
      new TypeClass("version", new Chevron("class", "vers")),
      new TypeClass("version", new Chevron("class", "vers"))
   };

   private Text() {
   }

   public static Text getInstance() {
      return instance;
   }

   public String _decode(String objectReference, String applicationReference) {
      String trimmed = objectReference == null ? null : objectReference.trim();
      if (trimmed != null && trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
         trimmed = trimmed.substring(1, trimmed.length() - 1);
      }

      return trimmed;
   }

   @Override
   public String _encode(Object s) {
      return s == null ? "null" : JaplScript.quote((String)s);
   }

   @Override
   public Class<String> _getJavaType() {
      return String.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
