package org.lwjgl.nanovg;

import org.lwjgl.system.APIUtil;
import org.lwjgl.system.CallbackI;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.NativeType;
import org.lwjgl.system.libffi.FFICIF;
import org.lwjgl.system.libffi.FFIType;
import org.lwjgl.system.libffi.LibFFI;

@FunctionalInterface
@NativeType("UIhandler")
public interface UIHandlerI extends CallbackI {
   FFICIF CIF = APIUtil.apiCreateCIF(LibFFI.FFI_DEFAULT_ABI, LibFFI.ffi_type_void, new FFIType[]{LibFFI.ffi_type_sint32, LibFFI.ffi_type_uint32});

   default FFICIF getCallInterface() {
      return CIF;
   }

   default void callback(long ret, long args) {
      this.invoke(MemoryUtil.memGetInt(MemoryUtil.memGetAddress(args)), MemoryUtil.memGetInt(MemoryUtil.memGetAddress(args + POINTER_SIZE)));
   }

   void invoke(int var1, @NativeType("UIevent") int var2);
}
