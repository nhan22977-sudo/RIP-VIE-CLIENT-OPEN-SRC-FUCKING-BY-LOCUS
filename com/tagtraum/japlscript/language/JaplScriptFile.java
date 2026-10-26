package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.JaplScript;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JaplScriptFile extends ReferenceImpl {
   private static final JaplScriptFile instance = new JaplScriptFile();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("file", new Chevron("class", "file"))};
   private final Path file;

   private JaplScriptFile() {
      super(null, null);
      this.file = null;
   }

   public JaplScriptFile(String objectReference, String applicationReference) {
      super(objectReference, applicationReference);
      int startQuote = objectReference.indexOf(34);
      int endQuote = objectReference.lastIndexOf(34);
      if (startQuote != -1 && endQuote != -1 && startQuote < endQuote && objectReference.contains(":")) {
         String alias = objectReference.substring(startQuote + 1, endQuote);
         this.file = Paths.get("/Volumes/" + alias.replace(':', '/'));
      } else if (startQuote != -1 && endQuote != -1 && startQuote < endQuote && objectReference.contains("POSIX file \"")) {
         this.file = Paths.get(objectReference.substring(startQuote + 1, endQuote));
      } else {
         this.file = Paths.get(objectReference);
      }
   }

   @Deprecated(since = "3.4.8", forRemoval = true)
   public JaplScriptFile(File file) throws IOException {
      this(file.toPath());
   }

   public JaplScriptFile(Path file) throws IOException {
      super(toAppleScriptFile(file), null);
      this.file = file;
   }

   public static JaplScriptFile getInstance() {
      return instance;
   }

   @Deprecated(since = "3.4.8", forRemoval = true)
   public static String toAppleScriptFile(File javaFile) throws IOException {
      return "(POSIX file " + JaplScript.quote(javaFile.getCanonicalFile().toString()) + ")";
   }

   public static String toAppleScriptFile(Path javaPath) throws IOException {
      return toAppleScriptFile(javaPath.toFile());
   }

   @Deprecated(since = "3.4.8", forRemoval = true)
   public File getFile() {
      return this.file.toFile();
   }

   public Path getPath() {
      return this.file;
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
