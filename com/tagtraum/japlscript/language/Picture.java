package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Picture extends ReferenceImpl {
   private static final Picture instance = new Picture();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("picture", new Chevron("class", "PICT"))};
   private byte[] data;
   private String format;

   public Picture() {
      super(null, null);
   }

   public Picture(String objectReference, String applicationReference) {
      super(objectReference, applicationReference);
      if (objectReference.startsWith("data ", 1)) {
         this.format = objectReference.substring("<data ".length(), "<data ".length() + 4);
         String hexString = objectReference.substring("<data ".length() + 4, objectReference.length() - 1);
         ByteArrayOutputStream out = new ByteArrayOutputStream(hexString.length() / 2);

         for (int i = 0; i < hexString.length(); i += 2) {
            out.write(java.lang.Integer.parseInt(hexString.substring(i, i + 2), 16));
         }

         this.data = out.toByteArray();
      }
   }

   public static Picture getInstance() {
      return instance;
   }

   public byte[] getData() {
      return this.data;
   }

   public void setData(byte[] data) {
      this.data = data;
   }

   public Image getImage() throws IOException {
      return this.data == null ? null : ImageIO.read(new ByteArrayInputStream(this.data));
   }

   public String getFormat() {
      return this.format;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
