package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Data extends ReferenceImpl {
   private static final Logger LOG = Logger.getLogger(Data.class.getName());
   private static final Data instance = new Data();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("data", new Chevron("class", "rdat"))};
   private byte[] data;

   private Data() {
      super(null, null);
   }

   public Data(String objectReference, String applicationReference) {
      super(objectReference, applicationReference);
      if (objectReference.startsWith("data ", 1)) {
         String hexString = objectReference.substring("<data ".length(), objectReference.length() - 1);
         ByteArrayOutputStream out = new ByteArrayOutputStream(hexString.length() / 2);

         for (int i = 0; i < hexString.length(); i += 2) {
            try {
               out.write(java.lang.Integer.parseInt(hexString.substring(i, i + 2), 16));
            } catch (NumberFormatException e) {
               LOG.log(Level.SEVERE, e.toString(), e);
            }
         }

         this.data = out.toByteArray();
      }
   }

   public static Data getInstance() {
      return instance;
   }

   public byte[] getData() {
      return this.data;
   }

   public void setData(byte[] data) {
      this.data = data;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
