package de.labystudio.spotifyapi.platform.windows.api.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Kernel32 extends WinNT, StdCallLibrary {
   Kernel32 INSTANCE = (Kernel32)Native.loadLibrary("kernel32", Kernel32.class, W32APIOptions.UNICODE_OPTIONS);

   boolean ReadProcessMemory(HANDLE var1, Pointer var2, Pointer var3, int var4, IntByReference var5);

   boolean Module32NextW(HANDLE var1, Tlhelp32.MODULEENTRY32W var2);

   boolean Process32First(HANDLE var1, Tlhelp32.PROCESSENTRY32.ByReference var2);

   boolean Process32Next(HANDLE var1, Tlhelp32.PROCESSENTRY32.ByReference var2);

   HANDLE CreateToolhelp32Snapshot(DWORD var1, DWORD var2);

   boolean CloseHandle(HANDLE var1);

   HANDLE OpenProcess(int var1, boolean var2, int var3);

   int GetLastError();
}
