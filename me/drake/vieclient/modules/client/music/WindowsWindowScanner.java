package me.drake.vieclient.modules.client.music;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class WindowsWindowScanner implements WindowScanner {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   @Override
   public List scan() {
      ArrayList var1 = new ArrayList();
      User32.INSTANCE.EnumWindows((hwnd, ignored) -> {
         try {
            collect(var1, hwnd);
         } catch (Throwable var4) {
         }

         return true;
      }, null);
      return List.copyOf(var1);
   }

   private static void collect(List windows, HWND hwnd) {
      if (User32.INSTANCE.IsWindowVisible(hwnd)) {
         int var2 = User32.INSTANCE.GetWindowTextLength(hwnd);
         if (var2 > 0 && var2 <= (4096)) {
            char[] var3 = new char[var2 + 1];
            User32.INSTANCE.GetWindowText(hwnd, var3, var3.length);
            String var4 = Native.toString(var3).trim();
            if (!var4.isBlank()) {
               IntByReference var5 = new IntByReference();
               User32.INSTANCE.GetWindowThreadProcessId(hwnd, var5);
               long var6 = Integer.toUnsignedLong(var5.getValue());
               String var8 = ProcessHandle.of(var6)
                  .flatMap(handle -> handle.info().command())
                  .map(x$0 -> Path.of(x$0))
                  .map(Path::getFileName)
                  .map(Path::toString)
                  .orElse("");
               if (var8.isBlank()) {
                  var8 = queryProcessName(var5.getValue());
               }

               windows.add(new WindowInfo(var6, var8, var4));
            }
         }
      }
   }

   private static String queryProcessName(int processId) {
      HANDLE var1 = Kernel32.INSTANCE.OpenProcess(4096, false, processId);
      if (var1 == null) {
         return "";
      }

      try {
         char[] var2 = new char[2048];
         IntByReference var3 = new IntByReference(var2.length);
         if (!Kernel32.INSTANCE.QueryFullProcessImageName(var1, 0, var2, var3)) {
            return "";
         }

         String var4 = new String(var2, 0, Math.max(0, var3.getValue())).trim();
         return var4.isBlank() ? "" : Path.of(var4).getFileName().toString();
      } catch (RuntimeException var9) {
         return "";
      } finally {
         Kernel32.INSTANCE.CloseHandle(var1);
      }
   }
}
