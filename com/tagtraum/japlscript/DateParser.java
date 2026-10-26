package com.tagtraum.japlscript;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateParser {
   private final Locale locale;
   private final DateFormatSymbols dateFormatSymbols;
   private final SimpleDateFormat[] universalFormats;

   public DateParser(Locale locale) {
      this.locale = locale;
      this.dateFormatSymbols = DateFormatSymbols.getInstance(locale);
      if (Locale.CANADA.getCountry().equals(locale.getCountry()) || Locale.US.getCountry().equals(locale.getCountry())) {
         this.universalFormats = new SimpleDateFormat[]{
            new SimpleDateFormat("M d yyyy h.mm.ss aa", Locale.US), new SimpleDateFormat("M d yyyy H.mm.ss", Locale.US)
         };
      } else if (Locale.JAPAN.getLanguage().equals(locale.getLanguage())) {
         this.universalFormats = new SimpleDateFormat[]{
            new SimpleDateFormat("yyyy M d h.mm.ss aa", Locale.US), new SimpleDateFormat("yyyy M d H.mm.ss", Locale.US)
         };
      } else {
         this.universalFormats = new SimpleDateFormat[]{
            new SimpleDateFormat("d M yyyy h.mm.ss aa", Locale.US),
            new SimpleDateFormat("d M yyyy H.mm.ss", Locale.US),
            new SimpleDateFormat("d. M yyyy h.mm.ss aa", Locale.US),
            new SimpleDateFormat("d. M yyyy H.mm.ss", Locale.US)
         };
      }

      if (Locale.GERMAN.getLanguage().equals(locale.getLanguage())) {
         this.dateFormatSymbols.setAmPmStrings(new String[]{"vorm.", "nachm."});
      }

      if ("nl".equals(locale.getLanguage())) {
         this.dateFormatSymbols.setAmPmStrings(new String[]{"a.m.", "p.m."});
      }
   }

   public Date parse(String s) throws ParseException {
      String universalDate = s;
      int month = this.getSymbolIndex(s, this.dateFormatSymbols.getMonths(), this.dateFormatSymbols.getShortMonths());
      if (month != -1) {
         if (universalDate.contains(this.dateFormatSymbols.getMonths()[month])) {
            universalDate = universalDate.replace(this.dateFormatSymbols.getMonths()[month], month + 1 + " ");
         } else if (universalDate.contains(this.dateFormatSymbols.getShortMonths()[month])) {
            universalDate = universalDate.replace(this.dateFormatSymbols.getShortMonths()[month], month + 1 + " ");
         }
      } else {
         String[] lowerMonths = this.toLowerCase(this.dateFormatSymbols.getMonths());
         String[] lowerShortMonths = this.toLowerCase(this.dateFormatSymbols.getShortMonths());
         month = this.getSymbolIndex(s, lowerMonths, lowerShortMonths);
         if (month != -1) {
            if (universalDate.contains(lowerMonths[month])) {
               universalDate = universalDate.replace(lowerMonths[month], month + 1 + " ");
            } else if (universalDate.contains(lowerShortMonths[month])) {
               universalDate = universalDate.replace(lowerShortMonths[month], month + 1 + " ");
            }
         }
      }

      String am = this.dateFormatSymbols.getAmPmStrings()[0];
      String pm = this.dateFormatSymbols.getAmPmStrings()[1];
      boolean containsAM = universalDate.contains(am);
      boolean containsPM = universalDate.contains(pm);
      if (containsAM) {
         universalDate = universalDate.replace(am, "");
      }

      if (containsPM) {
         universalDate = universalDate.replace(pm, "");
      }

      StringBuilder sb = new StringBuilder();
      boolean lastIsWhiteSpace = false;

      for (char c : universalDate.toCharArray()) {
         if (!Character.isLetter(c) && c != ',' && c != '-' && c != ' ') {
            sb.append(c);
            lastIsWhiteSpace = false;
         } else if (!lastIsWhiteSpace) {
            sb.append(' ');
            lastIsWhiteSpace = true;
         }
      }

      universalDate = sb.toString();
      if (universalDate.endsWith(".")) {
         universalDate = universalDate.substring(0, universalDate.length() - 1);
      }

      universalDate = universalDate.replace(':', '.').replace('/', '.').replace(" . ", " ");
      universalDate = universalDate.trim();
      if (containsAM) {
         universalDate = universalDate + " AM";
      }

      if (containsPM) {
         universalDate = universalDate + " PM";
      }

      universalDate = universalDate.trim();

      for (SimpleDateFormat format : this.universalFormats) {
         try {
            return format.parse(universalDate);
         } catch (ParseException var15) {
         }
      }

      throw new ParseException("Failed to parse " + s + " with locale " + this.locale, -1);
   }

   private String[] toLowerCase(String[] symbols) {
      String[] lower = new String[symbols.length];

      for (int i = 0; i < lower.length; i++) {
         lower[i] = symbols[i].toLowerCase(this.locale);
      }

      return lower;
   }

   private int getSymbolIndex(String dateString, String[] symbolArray, String[] shortSymbolArray) {
      int index = this.getSymbolIndex(dateString, symbolArray);
      if (index == -1) {
         index = this.getSymbolIndex(dateString, shortSymbolArray);
      }

      return index;
   }

   private int getSymbolIndex(String dateString, String[] symbolArray) {
      int index = -1;

      for (int i = 0; i < symbolArray.length; i++) {
         if (dateString.contains(symbolArray[i]) && !symbolArray[i].isEmpty()) {
            index = i;
            break;
         }
      }

      return index;
   }
}
