package me.ayydxn.luminescence.platform;

import me.ayydxn.luminescence.font.ULFontFile;
import org.jetbrains.annotations.Nullable;

public interface ULFontLoader {
   String getFallbackFont();

   String getFallbackFontForCharacters(String var1, int var2, boolean var3);

   @Nullable
   ULFontFile load(String var1, int var2, boolean var3);
}
