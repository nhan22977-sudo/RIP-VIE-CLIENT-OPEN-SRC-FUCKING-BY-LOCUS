package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;

public class Record extends ReferenceImpl {
   private static final Record instance = new Record();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("record", new Chevron("class", "reco"))};

   private Record() {
      super(null, null);
   }

   public static Record getInstance() {
      return instance;
   }

   public Record(String objectReference, String applicationReference) {
      super(objectReference, applicationReference);
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }

   @Override
   public String toString() {
      return this.getObjectReference();
   }
}
