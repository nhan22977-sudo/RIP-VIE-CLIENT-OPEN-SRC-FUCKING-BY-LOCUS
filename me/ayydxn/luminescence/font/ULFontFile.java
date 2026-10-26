package me.ayydxn.luminescence.font;

public class ULFontFile {
   private final ULFontFile.Source source;
   private final String filePath;
   private final byte[] buffer;

   private ULFontFile(String filePath) {
      this.source = ULFontFile.Source.FILE_PATH;
      this.filePath = filePath;
      this.buffer = null;
   }

   private ULFontFile(byte[] buffer) {
      this.source = ULFontFile.Source.BUFFER;
      this.filePath = null;
      this.buffer = buffer;
   }

   public static ULFontFile createFromFilePath(String path) {
      if (path != null && !path.isBlank()) {
         return new ULFontFile(path);
      } else {
         throw new IllegalArgumentException("Font file path must not be null or blank.");
      }
   }

   public static ULFontFile createFromBuffer(byte[] data) {
      if (data != null && data.length != 0) {
         return new ULFontFile(data);
      } else {
         throw new IllegalArgumentException("Font buffer must not be null or empty.");
      }
   }

   public ULFontFile.Source getSource() {
      return this.source;
   }

   public String getFilePath() {
      return this.filePath;
   }

   public byte[] getBuffer() {
      return this.buffer;
   }

   public enum Source {
      FILE_PATH,
      BUFFER;

      // $VF: synthetic method
      private static ULFontFile.Source[] $values() {
         return new ULFontFile.Source[]{FILE_PATH, BUFFER};
      }
   }
}
