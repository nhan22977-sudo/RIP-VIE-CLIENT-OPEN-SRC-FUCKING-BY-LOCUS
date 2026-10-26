package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tdta extends ReferenceImpl {
   private static final Logger LOG = Logger.getLogger(Tdta.class.getName());
   private static final TypeClass[] CLASSES = new TypeClass[]{
      new TypeClass("tdta", new Chevron("class", "tdta")), new TypeClass("raw data", new Chevron("class", "tdta"))
   };
   private static final Tdta instance = new Tdta();
   private byte[] tdta = new byte[0];
   private static final int DIM = 55;
   private static final int[][] HEXMAP = new int[55][];

   private static int hexToInt(char[] array, int offset) {
      return HEXMAP[array[offset] - 48][array[offset + 1] - 48];
   }

   private Tdta() {
      super(null, null);
   }

   public Tdta(byte[] buf, String applicationReference) {
      super("«data tdta" + toHex(buf) + "»", applicationReference);
      this.tdta = buf;
   }

   public Tdta(Path file, String applicationReference) throws IOException {
      this("«data tdta" + toHex(file) + "»", applicationReference);
   }

   @Deprecated(since = "3.4.8", forRemoval = true)
   public Tdta(File file, String applicationReference) throws IOException {
      this(file.toPath(), applicationReference);
   }

   private static String toHex(byte[] buf) {
      StringBuilder sb = new StringBuilder(buf.length * 2);

      for (byte b : buf) {
         sb.append(String.format("%02X", b));
      }

      return sb.toString();
   }

   private static String toHex(Path file) throws IOException {
      BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class);
      StringBuilder sb = new StringBuilder((int)attributes.size() * 2);

      try (InputStream in = Files.newInputStream(file)) {
         byte[] buf = new byte[65536];

         int justRead;
         while ((justRead = in.read(buf)) > 0) {
            for (int i = 0; i < justRead; i++) {
               sb.append(String.format("%02X", buf[i]));
            }
         }

         return sb.toString();
      }
   }

   public Tdta(String objectReference, String applicationReference) {
      super(objectReference, applicationReference);
      if (objectReference.startsWith("data tdta", 1)) {
         String hexString = objectReference.substring("<data tdta".length(), objectReference.length() - 1);
         ByteArrayOutputStream out = new ByteArrayOutputStream(hexString.length() / 2);
         char[] chars = hexString.toCharArray();

         for (int i = 0; i < hexString.length(); i += 2) {
            try {
               out.write(hexToInt(chars, i));
            } catch (NumberFormatException e) {
               LOG.log(Level.SEVERE, e.toString(), e);
            }
         }

         this.tdta = out.toByteArray();
      }
   }

   public byte[] getTdta() {
      return this.tdta;
   }

   public void setTdta(byte[] tdta) {
      this.tdta = tdta;
   }

   public static Tdta getInstance() {
      return instance;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }

   static {
      for (int i = 0; i < 10; i++) {
         HEXMAP[i] = new int[55];

         for (int j = 0; j < 10; j++) {
            HEXMAP[i][j] = java.lang.Integer.parseInt("" + i + j, 16);
         }

         for (char j = 'A'; j < 'G'; j++) {
            HEXMAP[i][j - '0'] = java.lang.Integer.parseInt("" + i + j, 16);
         }

         for (char j = 'a'; j < 'g'; j++) {
            HEXMAP[i][j - '0'] = java.lang.Integer.parseInt("" + i + j, 16);
         }
      }

      for (char i = 'A'; i < 'G'; i++) {
         HEXMAP[i - '0'] = new int[55];

         for (int j = 0; j < 10; j++) {
            HEXMAP[i - '0'][j] = java.lang.Integer.parseInt(Character.toString(i) + j, 16);
         }

         for (char j = 'A'; j < 'G'; j++) {
            HEXMAP[i - '0'][j - '0'] = java.lang.Integer.parseInt(i + Character.toString(j), 16);
         }

         for (char j = 'a'; j < 'g'; j++) {
            HEXMAP[i - '0'][j - '0'] = java.lang.Integer.parseInt(i + Character.toString(j), 16);
         }
      }

      for (char i = 'a'; i < 'g'; i++) {
         HEXMAP[i - '0'] = new int[55];

         for (int j = 0; j < 10; j++) {
            HEXMAP[i - '0'][j] = java.lang.Integer.parseInt(Character.toString(i) + j, 16);
         }

         for (char j = 'A'; j < 'G'; j++) {
            HEXMAP[i - '0'][j - '0'] = java.lang.Integer.parseInt(i + Character.toString(j), 16);
         }

         for (char j = 'a'; j < 'g'; j++) {
            HEXMAP[i - '0'][j - '0'] = java.lang.Integer.parseInt(i + Character.toString(j), 16);
         }
      }
   }
}
