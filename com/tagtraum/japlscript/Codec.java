package com.tagtraum.japlscript;

import com.tagtraum.japlscript.language.TypeClass;

public interface Codec<T> {
   T _decode(String var1, String var2);

   default T _decode(Reference reference) {
      return reference == null ? null : this._decode(reference.getObjectReference(), reference.getApplicationReference());
   }

   String _encode(Object var1);

   Class<? extends T> _getJavaType();

   TypeClass[] _getAppleScriptTypes();
}
