package me.ayydxn.luminescence.platform;

public interface ULFileSystem {
   boolean fileExists(String var1);

   String getFileMimeType(String var1);

   String getFileCharset(String var1);

   byte[] openFile(String var1);
}
