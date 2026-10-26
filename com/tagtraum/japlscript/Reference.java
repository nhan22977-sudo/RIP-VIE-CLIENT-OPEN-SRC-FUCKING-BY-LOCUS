package com.tagtraum.japlscript;

import com.tagtraum.japlscript.language.TypeClass;

public interface Reference {
   String getObjectReference();

   String getApplicationReference();

   <T> T cast(Class<T> var1);

   @Type("type")
   @Name("class")
   @Code("pcls")
   @Kind("property")
   TypeClass getTypeClass();

   boolean isInstanceOf(TypeClass var1);
}
