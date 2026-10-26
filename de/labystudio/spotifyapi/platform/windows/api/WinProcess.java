package de.labystudio.spotifyapi.platform.windows.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import de.labystudio.spotifyapi.platform.windows.api.jna.Kernel32;
import de.labystudio.spotifyapi.platform.windows.api.jna.Psapi;
import java.util.Map;
import java.util.Map.Entry;

public class WinProcess implements WinApi {
   protected static final Gson GSON = new Gson();
   protected final int processId;
   protected final HANDLE handle;
   protected final HWND window;
   protected long scanTimeout = 10000L;

   public WinProcess(String executableName) {
      this.processId = this.getProcessIdByName(executableName);
      if (this.processId == -1) {
         throw new IllegalStateException("Process of executable " + executableName + " not found");
      }

      this.handle = this.openProcessHandle(this.processId);
      if (this.handle == null) {
         throw new IllegalStateException("Process handle of " + this.processId + " not found");
      }

      this.window = this.openWindow(this.processId, hWnd -> {
         String title = this.getWindowTitle(hWnd);
         return !title.equals("Spotify Debug Window") && !title.equals("DevTools");
      });
      if (this.getWindowTitle().isEmpty()) {
         throw new IllegalStateException("Window for process " + this.processId + " not found");
      }
   }

   public boolean readBoolean(long address) {
      return this.readByte(address) == 1;
   }

   public byte readByte(long address) {
      return this.readBytes(address, 1)[0];
   }

   public int readInteger(long address) {
      byte[] bytes = this.readBytes(address, 4);
      return bytes[0] & 0xFF | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF) << 16 | (bytes[3] & 0xFF) << 24;
   }

   public String readString(long address, int length) {
      return new String(this.readBytes(address, length));
   }

   public byte[] readBytes(long address, int length) {
      Kernel32 kernel = Kernel32.INSTANCE;
      Memory memory = new Memory(length);
      kernel.ReadProcessMemory(this.handle, new Pointer(address), memory, length, new IntByReference(length));
      return memory.getByteArray(0L, length);
   }

   public long findInMemory(long minAddress, long maxAddress, byte[] searchBytes) {
      long timeStart = System.currentTimeMillis();
      int chunkSize = 65536;

      for (long cursor = minAddress; cursor < maxAddress; cursor += chunkSize) {
         byte[] chunk = this.readBytes(cursor, chunkSize + searchBytes.length);

         for (int i = 0; i < chunk.length - searchBytes.length; i++) {
            boolean found = true;

            for (int k = 0; k < searchBytes.length; k++) {
               if (chunk[i + k] != searchBytes[k]) {
                  found = false;
                  break;
               }
            }

            if (found) {
               return cursor + i;
            }
         }

         long timePassed = System.currentTimeMillis() - timeStart;
         if (timePassed > this.scanTimeout) {
            throw new IllegalStateException("Scan timeout of " + this.scanTimeout + "ms reached at address " + cursor);
         }
      }

      return -1L;
   }

   public long findInMemory(long minAddress, long maxAddress, byte[] searchBytes, WinProcess.SearchCondition condition) {
      long cursor = minAddress;

      for (int index = 0; cursor < maxAddress; index++) {
         long target = this.findInMemory(cursor, maxAddress, searchBytes);
         if (target == -1L || condition.matches(target, index)) {
            return target;
         }

         cursor = target + 1L;
      }

      return -1L;
   }

   public boolean hasBytes(long address, int... bytes) {
      byte[] chunk = this.readBytes(address, bytes.length);

      for (int i = 0; i < chunk.length; i++) {
         if (chunk[i] != (byte)bytes[i]) {
            return false;
         }
      }

      return true;
   }

   public boolean hasBytes(long address, byte[] bytes) {
      byte[] chunk = this.readBytes(address, bytes.length);

      for (int i = 0; i < chunk.length; i++) {
         if (chunk[i] != bytes[i]) {
            return false;
         }
      }

      return true;
   }

   public boolean hasBytes(long address, byte[]... chunksOfBytes) {
      for (byte[] bytes : chunksOfBytes) {
         if (this.hasBytes(address, bytes)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasText(long address, String text) {
      return this.hasBytes(address, text.getBytes());
   }

   public boolean hasText(long address, String... texts) {
      for (String text : texts) {
         if (this.hasText(address, text)) {
            return true;
         }
      }

      return false;
   }

   public long findAddressOfTextInModule(String moduleName, String text) {
      Psapi.ModuleInfo moduleInfo = this.getModuleInfo(moduleName);
      return moduleInfo == null ? -1L : this.findAddressOfText(moduleInfo.getBaseOfDll(), text, 0);
   }

   public long findAddressOfText(long start, String text, int index) {
      return this.findAddressOfText(start, text, (address, matchIndex) -> matchIndex == index);
   }

   public long findAddressOfText(long start, String text, WinProcess.SearchCondition condition) {
      return this.findAddressOfText(start, Long.MAX_VALUE, text, condition);
   }

   public long findAddressOfText(long start, long end, String text, WinProcess.SearchCondition condition) {
      return this.findInMemory(start, end, text.getBytes(), condition);
   }

   public long findAddressOfTexts(long start, long end, WinProcess.SearchCondition condition, String... texts) {
      for (String text : texts) {
         long address = this.findAddressOfText(start, end, text, condition);
         if (address != -1L) {
            return address;
         }
      }

      return -1L;
   }

   public long findAddressUsingPath(String... path) {
      long cursor = -1L;

      for (String part : path) {
         cursor = this.findAddressOfText(cursor + 1L, part, 0);
         if (cursor == -1L) {
            return -1L;
         }
      }

      return cursor;
   }

   public long findAddressUsingRules(WinProcess.SearchRule... rules) {
      long cursor = -1L;

      for (WinProcess.SearchRule rule : rules) {
         cursor = this.findAddressOfText(cursor + 1L, rule.getText(), rule.getCondition());
         if (cursor == -1L) {
            return -1L;
         }
      }

      return cursor;
   }

   public JsonObject readJsonObject(long address) {
      int depth = 0;
      boolean inString = false;
      int chunkSize = 1024;
      long offset = 0L;
      StringBuilder json = new StringBuilder();

      do {
         String chunk = this.readString(address + offset, chunkSize);

         for (int i = 0; i < chunk.length(); i++) {
            char c = chunk.charAt(i);
            if (c == '"') {
               inString = !inString;
            }

            if (!inString) {
               if (c == '{' || c == '[') {
                  depth++;
               } else if (c == '}' || c == ']') {
                  depth--;
               }
            }

            json.append(c);
            if (depth == 0) {
               break;
            }
         }

         offset += chunkSize;
      } while (depth > 0);

      return (JsonObject)GSON.fromJson(json.toString(), JsonObject.class);
   }

   public Psapi.ModuleInfo getModuleInfo(String moduleName) {
      return this.getModuleInfo(this.handle, moduleName);
   }

   public Map<String, Psapi.ModuleInfo> getModules() {
      return this.getModules(this.handle);
   }

   public long getFirstModuleAddress() {
      long minAddress = Long.MAX_VALUE;

      for (Entry<String, Psapi.ModuleInfo> module : this.getModules().entrySet()) {
         long baseOfDll = module.getValue().getBaseOfDll();
         if (baseOfDll > 0L) {
            minAddress = Math.min(minAddress, baseOfDll);
         }
      }

      return minAddress;
   }

   public long getMaxProcessAddress() {
      long maxAddress = 0L;

      for (Entry<String, Psapi.ModuleInfo> module : this.getModules().entrySet()) {
         maxAddress = Math.max(maxAddress, module.getValue().getBaseOfDll() + module.getValue().getSizeOfImage());
      }

      return maxAddress;
   }

   public String getWindowTitle() {
      return this.getWindowTitle(this.window);
   }

   public int getProcessId() {
      return this.processId;
   }

   public HANDLE getHandle() {
      return this.handle;
   }

   public void setScanTimeout(long scanTimeout) {
      this.scanTimeout = scanTimeout;
   }

   public boolean isOpen() {
      return this.handle != null;
   }

   public void close() {
      Kernel32.INSTANCE.CloseHandle(this.handle);
   }

   public interface SearchCondition {
      boolean matches(long var1, int var3);
   }

   public static class SearchRule {
      private final String text;
      private final WinProcess.SearchCondition condition;

      public SearchRule(String text, WinProcess.SearchCondition condition) {
         this.text = text;
         this.condition = condition;
      }

      public String getText() {
         return this.text;
      }

      public WinProcess.SearchCondition getCondition() {
         return this.condition;
      }
   }
}
