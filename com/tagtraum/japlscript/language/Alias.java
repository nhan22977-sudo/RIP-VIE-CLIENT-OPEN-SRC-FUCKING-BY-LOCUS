package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Alias extends ReferenceImpl {
   private static final Logger LOG = Logger.getLogger(Alias.class.getName());
   private static final Alias instance = new Alias();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("alias", new Chevron("class", "alis"))};
   private final Path file;
   private String alias;

   private Alias() {
      super(null, null);
      this.file = null;
   }

   public Alias(String objectReference, String applicationReference) {
      super(toObjectReference(objectReference), applicationReference);
      int startQuote = objectReference.indexOf(34);
      if (startQuote != -1) {
         int endQuote = objectReference.lastIndexOf(34);
         this.alias = objectReference.substring(startQuote + 1, endQuote);
         this.file = Paths.get("/Volumes/" + this.alias.replace(':', '/'));
      } else {
         this.alias = objectReference;
         this.file = Paths.get(objectReference);
      }
   }

   public static Alias getInstance() {
      return instance;
   }

   public Alias(Path file) throws IOException {
      super(JaplScriptFile.toAppleScriptFile(file), null);
      this.file = file;
   }

   @Deprecated(since = "3.4.8", forRemoval = true)
   public Alias(File file) throws IOException {
      this(file.toPath());
   }

   private static String toObjectReference(String objectReference) {
      if (objectReference.startsWith("/")) {
         try {
            return JaplScriptFile.toAppleScriptFile(new File(objectReference));
         } catch (IOException e) {
            LOG.log(Level.SEVERE, e.toString(), e);
            return objectReference;
         }
      } else {
         return objectReference;
      }
   }

   public File getFile() {
      return this.file.toFile();
   }

   public Path getPath() {
      return this.file;
   }

   public URL getURL() throws MalformedURLException {
      return this.file.toString().startsWith("/") ? new URL("file://localhost" + this.file) : new URL("file://localhost/" + this.file);
   }

   public String getAlias() {
      return this.alias;
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
