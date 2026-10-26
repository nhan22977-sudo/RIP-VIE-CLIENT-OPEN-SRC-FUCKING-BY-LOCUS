package de.labystudio.spotifyapi.platform.windows.api.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LONG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Tlhelp32 {
   DWORD TH32CS_SNAPMODULE = new DWORD(8L);
   DWORD TH32CS_SNAPPROCESS = new DWORD(2L);

   class MODULEENTRY32W extends Structure {
      public DWORD dwSize;
      public DWORD th32ModuleID;
      public DWORD th32ProcessID;
      public DWORD GlblcntUsage;
      public DWORD ProccntUsage;
      public Pointer modBaseAddr;
      public DWORD modBaseSize;
      public HMODULE hModule;
      public char[] szModule = new char[256];
      public char[] szExePath = new char[260];

      public MODULEENTRY32W() {
         this.dwSize = new DWORD(this.size());
      }

      public MODULEENTRY32W(Pointer memory) {
         super(memory);
         this.read();
      }

      protected List<String> getFieldOrder() {
         return new ArrayList<>(
            Arrays.asList(
               "dwSize", "th32ModuleID", "th32ProcessID", "GlblcntUsage", "ProccntUsage", "modBaseAddr", "modBaseSize", "hModule", "szModule", "szExePath"
            )
         );
      }

      public static class ByReference extends Tlhelp32.MODULEENTRY32W implements com.sun.jna.Structure.ByReference {
      }
   }

   class PROCESSENTRY32 extends Structure {
      public DWORD dwSize;
      public DWORD cntUsage;
      public DWORD th32ProcessID;
      public ULONG_PTR th32DefaultHeapID;
      public DWORD th32ModuleID;
      public DWORD cntThreads;
      public DWORD th32ParentProcessID;
      public LONG pcPriClassBase;
      public DWORD dwFlags;
      public char[] szExeFile = new char[260];

      public PROCESSENTRY32() {
         this.dwSize = new DWORD(this.size());
      }

      public PROCESSENTRY32(Pointer memory) {
         super(memory);
         this.read();
      }

      protected List<String> getFieldOrder() {
         return new ArrayList<>(
            Arrays.asList(
               "dwSize",
               "cntUsage",
               "th32ProcessID",
               "th32DefaultHeapID",
               "th32ModuleID",
               "cntThreads",
               "th32ParentProcessID",
               "pcPriClassBase",
               "dwFlags",
               "szExeFile"
            )
         );
      }

      public static class ByReference extends com.sun.jna.platform.win32.Tlhelp32.PROCESSENTRY32 implements com.sun.jna.Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }

         protected List<String> getFieldOrder() {
            return new ArrayList<>(
               Arrays.asList(
                  "dwSize",
                  "cntUsage",
                  "th32ProcessID",
                  "th32DefaultHeapID",
                  "th32ModuleID",
                  "cntThreads",
                  "th32ParentProcessID",
                  "pcPriClassBase",
                  "dwFlags",
                  "szExeFile"
               )
            );
         }
      }
   }
}
