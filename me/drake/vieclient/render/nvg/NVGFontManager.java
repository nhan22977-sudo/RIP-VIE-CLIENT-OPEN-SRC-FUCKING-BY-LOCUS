package me.drake.vieclient.render.nvg;

public final class NVGFontManager {
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private NVGFontManager() {
   }

   public static void loadFonts() {
      NanoVGFontManager.loadFonts();
   }

   public static int getRegularFont() {
      return NanoVGFontManager.getRegularFont();
   }

   public static int getBoldFont() {
      return NanoVGFontManager.getBoldFont();
   }

   public static int getIconFont() {
      return NanoVGFontManager.getIconFont();
   }

   public static int getFontId(boolean bold) {
      return NanoVGFontManager.getFontId(bold);
   }

   public static String getFontName(int fontId) {
      return NanoVGFontManager.getFontName(fontId);
   }

   public static int getTitleFont() {
      return NanoVGFontManager.getTitleFont();
   }

   public static int getTitleBoldFont() {
      return NanoVGFontManager.getTitleBoldFont();
   }
}
