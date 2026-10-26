package com.tagtraum.japlscript.language;

import com.tagtraum.japlscript.Chevron;
import com.tagtraum.japlscript.Codec;
import com.tagtraum.japlscript.DateParser;
import com.tagtraum.japlscript.execution.JaplScriptException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class Date implements Codec<java.util.Date> {
   private static final Date instance = new Date();
   private static final TypeClass[] CLASSES = new TypeClass[]{new TypeClass("date", new Chevron("class", "ldt "))};

   private Date() {
   }

   public static Date getInstance() {
      return instance;
   }

   @Override
   public String _encode(Object date) {
      if (date == null) {
         return "null";
      }

      SimpleDateFormat dateHelperFormat = new SimpleDateFormat("'my createDate('yyyy, M, d, H, m, s')'");
      return dateHelperFormat.format(date);
   }

   public java.util.Date _decode(String objectReference, String applicationReference) {
      return objectReference == null ? null : parseDate(objectReference);
   }

   private static java.util.Date parseDate(String objectReference) {
      DateFormat rfc3339Format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
      rfc3339Format.setTimeZone(TimeZone.getTimeZone("UTC"));

      try {
         return rfc3339Format.parse(objectReference);
      } catch (ParseException var11) {
         int firstQuote = objectReference.indexOf(34);
         int lastQuote = objectReference.lastIndexOf(34);
         if (firstQuote >= 0 && lastQuote >= 0) {
            String d = objectReference.substring(firstQuote + 1, lastQuote);

            try {
               return new DateParser(Locale.getDefault()).parse(d);
            } catch (ParseException var10) {
               try {
                  return new DateParser(Locale.US).parse(d);
               } catch (ParseException var9) {
                  for (int format = 0; format <= 3; format++) {
                     try {
                        return DateFormat.getDateTimeInstance(format, format, Locale.US).parse(d);
                     } catch (Exception var8) {
                     }
                  }

                  for (int format = 0; format <= 3; format++) {
                     try {
                        return DateFormat.getDateTimeInstance(format, format, Locale.getDefault()).parse(d);
                     } catch (Exception var7) {
                     }
                  }

                  throw new JaplScriptException("Failed to parse date: " + objectReference);
               }
            }
         } else {
            throw new JaplScriptException("Failed to parse date: " + objectReference);
         }
      }
   }

   @Override
   public Class<java.util.Date> _getJavaType() {
      return java.util.Date.class;
   }

   @Override
   public TypeClass[] _getAppleScriptTypes() {
      return CLASSES;
   }
}
