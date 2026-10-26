package de.labystudio.spotifyapi.platform.windows.api.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Psapi extends WinNT, StdCallLibrary {
   Psapi INSTANCE = (Psapi)Native.loadLibrary("psapi", Psapi.class, W32APIOptions.UNICODE_OPTIONS);

   boolean EnumProcessModulesEx(HANDLE var1, Pointer[] var2, int var3, IntByReference var4, int var5);

   int GetModuleBaseName(HANDLE var1, Pointer var2, char[] var3, int var4);

   boolean GetModuleInformation(HANDLE var1, Pointer var2, Psapi.ModuleInfo var3, int var4);

   class ModuleFilter {
      public static final int NONE = 0;
      public static final int X32BIT = 1;
      public static final int X64BIT = 2;
      public static final int ALL = 3;
   }

   class ModuleInfo extends Structure {
      public Pointer BaseOfDll;
      public int SizeOfImage;
      public Pointer EntryPoint;

      public long getBaseOfDll() {
         return Pointer.nativeValue(this.BaseOfDll);
      }

      public long getEntryPoint() {
         return Pointer.nativeValue(this.EntryPoint);
      }

      public int getSizeOfImage() {
         return this.SizeOfImage;
      }

      protected List<String> getFieldOrder() {
         return new ArrayList<>(Arrays.asList("BaseOfDll", "SizeOfImage", "EntryPoint"));
      }
   }
}
