package me.drake.vieclient.internal;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebSocketLogger {
   private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketLogger.class);
   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd"
   );
   private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss.SSS"
   );
   private static final boolean WS_VERBOSE_LOGS = Boolean.parseBoolean(
      String.valueOf(
         System.getenv()
            .getOrDefault(
               "VIE_WS_VERBOSE_LOGS",
               "false"
            )
      )
   );
   private static final boolean WS_FILE_LOGS = Boolean.parseBoolean(
      String.valueOf(
         System.getenv()
            .getOrDefault(
               "VIE_WS_FILE_LOGS",
               "false"
            )
      )
   );
   private final Path logDir;
   private Path currentLogFile;
   private String currentDate;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public WebSocketLogger(Path minecraftDir) {
      byte[] var10002 = new byte[9];
      var10002[0] = (byte)(106);
      var10002[1] = (byte)(-13);
      var10002[2] = (byte)(121);
      var10002[3] = (byte)(-67);
      var10002[4] = (byte)(-39);
      var10002[5] = (byte)(-7);
      var10002[6] = (byte)(-77);
      var10002[7] = (byte)(-60);
      var10002[8] = 0;
      Path var10001 = minecraftDir.resolve($$v_s(var10002, -9019216864043872199L));
      var10002 = new byte[4];
      var10002[0] = (byte)(112);
      var10002[1] = (byte)(-11);
      var10002[2] = (byte)(123);
      var10002[3] = (byte)(-83);
      var10001 = var10001.resolve($$v_s(var10002, -9019216864043872199L));
      var10002 = new byte[9];
      var10002[0] = (byte)(107);
      var10002[1] = (byte)(-1);
      var10002[2] = (byte)(126);
      var10002[3] = (byte)(-83);
      var10002[4] = (byte)(-38);
      var10002[5] = (byte)(-13);
      var10002[6] = (byte)(-67);
      var10002[7] = (byte)(-49);
      var10002[8] = 0;
      this.logDir = var10001.resolve($$v_s(var10002, -9019216864043872199L));
      if (WS_FILE_LOGS) {
         this.ensureLogDirectory();
         this.rotateLogFile();
      }
   }

   private void ensureLogDirectory() {
      try {
         Files.createDirectories(this.logDir);
      } catch (IOException var2) {
         byte[] var10001 = new byte[52];
         var10001[0] = (byte)(71);
         var10001[1] = (byte)(-51);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-68);
         var10001[4] = (byte)(-26);
         var10001[5] = (byte)(-1);
         var10001[6] = (byte)(-75);
         var10001[7] = (byte)(-63);
         var10001[8] = (byte)(17);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-88);
         var10001[11] = (byte)(-103);
         var10001[12] = (byte)(-118);
         var10001[13] = (byte)(47);
         var10001[14] = (byte)(-101);
         var10001[15] = (byte)(-96);
         var10001[16] = (byte)(17);
         var10001[17] = (byte)(106);
         var10001[18] = (byte)(-118);
         var10001[19] = (byte)(-49);
         var10001[20] = (byte)(108);
         var10001[21] = (byte)(12);
         var10001[22] = (byte)(-29);
         var10001[23] = (byte)(-98);
         var10001[24] = (byte)(-124);
         var10001[25] = (byte)(102);
         var10001[26] = (byte)(-5);
         var10001[27] = (byte)(102);
         var10001[28] = (byte)(94);
         var10001[29] = (byte)(106);
         var10001[30] = (byte)(-53);
         var10001[31] = (byte)(67);
         var10001[32] = (byte)(-120);
         var10001[33] = (byte)(95);
         var10001[34] = (byte)(92);
         var10001[35] = (byte)(18);
         var10001[36] = (byte)(58);
         var10001[37] = (byte)(87);
         var10001[38] = (byte)(86);
         var10001[39] = (byte)(46);
         var10001[40] = (byte)(-67);
         var10001[41] = (byte)(-80);
         var10001[42] = (byte)(33);
         var10001[43] = (byte)(117);
         var10001[44] = (byte)(-7);
         var10001[45] = (byte)(-121);
         var10001[46] = (byte)(108);
         var10001[47] = (byte)(11);
         var10001[48] = (byte)(22);
         var10001[49] = (byte)(-54);
         var10001[50] = (byte)(87);
         var10001[51] = (byte)(-77);
         LOGGER.error($$v_s(var10001, -9019216864043872199L), var2.getMessage());
      }
   }

   private void rotateLogFile() {
      if (WS_FILE_LOGS) {
         String var1 = LocalDateTime.now().format(DATE_FORMAT);
         if (!var1.equals(this.currentDate)) {
            this.currentDate = var1;
            this.currentLogFile = this.logDir.resolve("websocket-" + var1 + ".log");
            if (WS_VERBOSE_LOGS) {
               byte[] var10001 = new byte[39];
               var10001[0] = (byte)(71);
               var10001[1] = (byte)(-51);
               var10001[2] = (byte)(121);
               var10001[3] = (byte)(-68);
               var10001[4] = (byte)(-26);
               var10001[5] = (byte)(-1);
               var10001[6] = (byte)(-75);
               var10001[7] = (byte)(-63);
               var10001[8] = (byte)(17);
               var10001[9] = (byte)(-42);
               var10001[10] = (byte)(-34);
               var10001[11] = (byte)(-92);
               var10001[12] = (byte)(-94);
               var10001[13] = (byte)(28);
               var10001[14] = (byte)(-65);
               var10001[15] = (byte)(-122);
               var10001[16] = (byte)(9);
               var10001[17] = (byte)(23);
               var10001[18] = (byte)(-20);
               var10001[19] = (byte)(-30);
               var10001[20] = (byte)(106);
               var10001[21] = (byte)(7);
               var10001[22] = (byte)(-90);
               var10001[23] = (byte)(-100);
               var10001[24] = (byte)(-51);
               var10001[25] = (byte)(126);
               var10001[26] = (byte)(-15);
               var10001[27] = (byte)(102);
               var10001[28] = (byte)(79);
               var10001[29] = (byte)(119);
               var10001[30] = (byte)(-38);
               var10001[31] = (byte)(67);
               var10001[32] = (byte)(-120);
               var10001[33] = (byte)(95);
               var10001[34] = (byte)(24);
               var10001[35] = (byte)(68);
               var10001[36] = (byte)(117);
               var10001[37] = (byte)(75);
               var10001[38] = (byte)(11);
               LOGGER.info($$v_s(var10001, -9019216864043872199L), this.currentLogFile.getFileName());
            }
         }
      }
   }

   private void writeToFile(String level, String tag, String message, Throwable throwable) {
      if (WS_FILE_LOGS) {
         this.rotateLogFile();
         String var5 = LocalDateTime.now().format(TIMESTAMP_FORMAT);
         String var6 = Thread.currentThread().getName();
         String var7 = String.format(
            "[%s] [%s] [%s] [%s] %s",
            var5,
            var6,
            level,
            tag,
            message
         );
         if (throwable != null) {
            var7 = var7 + "\n" + this.getStackTrace(throwable);
         }

         var7 = var7 + "\n";

         try {
            Files.writeString(this.currentLogFile, var7, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         } catch (IOException var9) {
            LOGGER.error(
               "[WebSocketLogger] Failed to write to log file: {}",
               var9.getMessage()
            );
         }
      }
   }

   private String getStackTrace(Throwable throwable) {
      StringBuilder var2 = new StringBuilder();
      var2.append(throwable.toString()).append("\n");

      for (StackTraceElement var6 : throwable.getStackTrace()) {
         byte[] var10001 = new byte[4];
         var10001[0] = (byte)(21);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(104);
         var10001[3] = (byte)(-2);
         var2.append($$v_s(var10001, -9019216864043872199L)).append(var6.toString()).append("\n");
      }

      return var2.toString();
   }

   public void debug(String tag, String message) {
      if (WS_VERBOSE_LOGS) {
         byte[] var10001 = new byte[17];
         var10001[0] = (byte)(71);
         var10001[1] = (byte)(-51);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-68);
         var10001[4] = (byte)(-26);
         var10001[5] = (byte)(-1);
         var10001[6] = (byte)(-75);
         var10001[7] = (byte)(-63);
         var10001[8] = (byte)(17);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-34);
         var10001[11] = (byte)(-115);
         var10001[12] = (byte)(-112);
         var10001[13] = (byte)(21);
         var10001[14] = (byte)(-34);
         var10001[15] = (byte)(-87);
         var10001[16] = (byte)(49);
         LOGGER.debug($$v_s(var10001, -9019216864043872199L), tag, message);
         var10001 = new byte[5];
         var10001[0] = (byte)(88);
         var10001[1] = (byte)(-33);
         var10001[2] = (byte)(94);
         var10001[3] = (byte)(-117);
         var10001[4] = (byte)(-14);
         this.writeToFile($$v_s(var10001, -9019216864043872199L), tag, message, null);
      }
   }

   public void debug(String tag, String message, Object... args) {
      String var4 = String.format(message, args);
      this.debug(tag, var4);
   }

   public void info(String tag, String message) {
      if (WS_VERBOSE_LOGS) {
         byte[] var10001 = new byte[17];
         var10001[0] = (byte)(71);
         var10001[1] = (byte)(-51);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-68);
         var10001[4] = (byte)(-26);
         var10001[5] = (byte)(-1);
         var10001[6] = (byte)(-75);
         var10001[7] = (byte)(-63);
         var10001[8] = (byte)(17);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-34);
         var10001[11] = (byte)(-115);
         var10001[12] = (byte)(-112);
         var10001[13] = (byte)(21);
         var10001[14] = (byte)(-34);
         var10001[15] = (byte)(-87);
         var10001[16] = (byte)(49);
         LOGGER.info($$v_s(var10001, -9019216864043872199L), tag, message);
      }

      byte[] var3 = new byte[4];
      var3[0] = (byte)(85);
      var3[1] = (byte)(-44);
      var3[2] = (byte)(90);
      var3[3] = (byte)(-111);
      this.writeToFile($$v_s(var3, -9019216864043872199L), tag, message, null);
   }

   public void info(String tag, String message, Object... args) {
      String var4 = String.format(message, args);
      this.info(tag, var4);
   }

   public void warn(String tag, String message) {
      byte[] var10001 = new byte[17];
      var10001[0] = (byte)(71);
      var10001[1] = (byte)(-51);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(-26);
      var10001[5] = (byte)(-1);
      var10001[6] = (byte)(-75);
      var10001[7] = (byte)(-63);
      var10001[8] = (byte)(17);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-34);
      var10001[11] = (byte)(-115);
      var10001[12] = (byte)(-112);
      var10001[13] = (byte)(21);
      var10001[14] = (byte)(-34);
      var10001[15] = (byte)(-87);
      var10001[16] = (byte)(49);
      LOGGER.warn($$v_s(var10001, -9019216864043872199L), tag, message);
      var10001 = new byte[4];
      var10001[0] = (byte)(75);
      var10001[1] = (byte)(-37);
      var10001[2] = (byte)(78);
      var10001[3] = (byte)(-112);
      this.writeToFile($$v_s(var10001, -9019216864043872199L), tag, message, null);
   }

   public void warn(String tag, String message, Object... args) {
      String var4 = String.format(message, args);
      this.warn(tag, var4);
   }

   public void error(String tag, String message) {
      byte[] var10001 = new byte[17];
      var10001[0] = (byte)(71);
      var10001[1] = (byte)(-51);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(-26);
      var10001[5] = (byte)(-1);
      var10001[6] = (byte)(-75);
      var10001[7] = (byte)(-63);
      var10001[8] = (byte)(17);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-34);
      var10001[11] = (byte)(-115);
      var10001[12] = (byte)(-112);
      var10001[13] = (byte)(21);
      var10001[14] = (byte)(-34);
      var10001[15] = (byte)(-87);
      var10001[16] = (byte)(49);
      LOGGER.error($$v_s(var10001, -9019216864043872199L), tag, message);
      var10001 = new byte[5];
      var10001[0] = (byte)(89);
      var10001[1] = (byte)(-56);
      var10001[2] = (byte)(78);
      var10001[3] = (byte)(-111);
      var10001[4] = (byte)(-25);
      this.writeToFile($$v_s(var10001, -9019216864043872199L), tag, message, null);
   }

   public void error(String tag, String message, Throwable throwable) {
      Logger var10000 = LOGGER;
      byte[] var10001 = new byte[17];
      var10001[0] = (byte)(71);
      var10001[1] = (byte)(-51);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-68);
      var10001[4] = (byte)(-26);
      var10001[5] = (byte)(-1);
      var10001[6] = (byte)(-75);
      var10001[7] = (byte)(-63);
      var10001[8] = (byte)(17);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-34);
      var10001[11] = (byte)(-115);
      var10001[12] = (byte)(-112);
      var10001[13] = (byte)(21);
      var10001[14] = (byte)(-34);
      var10001[15] = (byte)(-87);
      var10001[16] = (byte)(49);
      String var4 = $$v_s(var10001, -9019216864043872199L);
      Object[] var10002 = new Object[3];
      var10002[0] = tag;
      var10002[1] = message;
      var10002[2] = throwable;
      var10000.error(var4, var10002);
      var10001 = new byte[5];
      var10001[0] = (byte)(89);
      var10001[1] = (byte)(-56);
      var10001[2] = (byte)(78);
      var10001[3] = (byte)(-111);
      var10001[4] = (byte)(-25);
      this.writeToFile($$v_s(var10001, -9019216864043872199L), tag, message, throwable);
   }

   public void error(String tag, String message, Object... args) {
      String var4 = String.format(message, args);
      this.error(tag, var4);
   }

   public void connection(String state, String url, String details) {
      String var4 = this.maskUrl(url);
      byte[] var10000 = new byte[22];
      var10000[0] = (byte)(95);
      var10000[1] = (byte)(-11);
      var10000[2] = (byte)(114);
      var10000[3] = (byte)(-80);
      var10000[4] = (byte)(-48);
      var10000[5] = (byte)(-13);
      var10000[6] = (byte)(-94);
      var10000[7] = (byte)(-61);
      var10000[8] = (byte)(27);
      var10000[9] = (byte)(-52);
      var10000[10] = (byte)(-60);
      var10000[11] = (byte)(-45);
      var10000[12] = (byte)(-98);
      var10000[13] = (byte)(114);
      var10000[14] = (byte)(-34);
      var10000[15] = (byte)(-9);
      var10000[16] = (byte)(63);
      var10000[17] = (byte)(106);
      var10000[18] = (byte)(-28);
      var10000[19] = (byte)(-117);
      var10000[20] = (byte)(118);
      var10000[21] = (byte)(73);
      String var6 = $$v_s(var10000, -9019216864043872199L);
      byte[] var10001 = (byte[])(new Object[3]);
      var10001[0] = (byte)state;
      var10001[1] = (byte)var4;
      var10001[2] = (byte)details;
      String var5 = String.format(var6, var10001);
      var10001 = new byte[4];
      var10001[0] = (byte)(95);
      var10001[1] = (byte)(-43);
      var10001[2] = (byte)(82);
      var10001[3] = (byte)(-112);
      this.info($$v_s(var10001, -9019216864043872199L), var5);
   }

   private String maskUrl(String url) {
      if (url == null) {
         byte[] var9 = new byte[4];
         var9[0] = (byte)(114);
         var9[1] = (byte)(-17);
         var9[2] = (byte)(112);
         var9[3] = (byte)(-78);
         return $$v_s(var9, -9019216864043872199L);
      }

      try {
         URI var2 = new URI(url);
         String var3 = var2.getScheme();
         int var4 = var2.getPort();
         String var5 = var2.getPath();
         byte[] var7 = new byte[13];
         var7[0] = (byte)(57);
         var7[1] = (byte)(-23);
         var7[2] = (byte)(38);
         var7[3] = (byte)(-15);
         var7[4] = (byte)(-102);
         var7[5] = (byte)(-70);
         var7[6] = (byte)(-4);
         var7[7] = (byte)(-128);
         var7[8] = (byte)(78);
         var7[9] = (byte)(-121);
         var7[10] = (byte)(-128);
         var7[11] = (byte)(-45);
         var7[12] = (byte)(-98);
         String var8 = $$v_s(var7, -9019216864043872199L);
         Object[] var10001 = new Object[3];
         var10001[0] = var3;
         var10001[1] = var4;
         var10001[2] = var5;
         return String.format(var8, var10001);
      } catch (Exception var6) {
         byte[] var10000 = new byte[3];
         var10000[0] = (byte)(54);
         var10000[1] = (byte)(-80);
         var10000[2] = (byte)(54);
         return $$v_s(var10000, -9019216864043872199L);
      }
   }

   public void auth(String state, String keyCode, String details) {
      String var6;
      if (keyCode != null && !keyCode.isBlank()) {
         byte[] var7 = new byte[7];
         var7[0] = (byte)(108);
         var7[1] = (byte)(-24);
         var7[2] = (byte)(121);
         var7[3] = (byte)(-83);
         var7[4] = (byte)(-48);
         var7[5] = (byte)(-2);
         var7[6] = (byte)(-94);
         var6 = $$v_s(var7, -9019216864043872199L);
      } else {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(113);
         var10000[1] = (byte)(-13);
         var10000[2] = (byte)(111);
         var10000[3] = (byte)(-83);
         var10000[4] = (byte)(-36);
         var10000[5] = (byte)(-2);
         var10000[6] = (byte)(-79);
         var6 = $$v_s(var10000, -9019216864043872199L);
      }

      String var4 = var6;
      byte[] var8 = new byte[19];
      var8[0] = (byte)(93);
      var8[1] = (byte)(-17);
      var8[2] = (byte)(104);
      var8[3] = (byte)(-74);
      var8[4] = (byte)(-107);
      var8[5] = (byte)(-75);
      var8[6] = (byte)(-91);
      var8[7] = (byte)(-112);
      var8[8] = (byte)(84);
      var8[9] = (byte)(-55);
      var8[10] = (byte)(-127);
      var8[11] = (byte)(-113);
      var8[12] = (byte)(-48);
      var8[13] = (byte)(109);
      var8[14] = (byte)(-115);
      var8[15] = (byte)(-2);
      var8[16] = (byte)(108);
      var8[17] = (byte)(111);
      var8[18] = (byte)(-65);
      var6 = $$v_s(var8, -9019216864043872199L);
      byte[] var10001 = (byte[])(new Object[3]);
      var10001[0] = (byte)state;
      var10001[1] = (byte)var4;
      var10001[2] = (byte)details;
      String var5 = String.format(var6, var10001);
      var10001 = new byte[4];
      var10001[0] = (byte)(93);
      var10001[1] = (byte)(-49);
      var10001[2] = (byte)(72);
      var10001[3] = (byte)(-106);
      this.info($$v_s(var10001, -9019216864043872199L), var5);
   }

   public void message(String direction, String type, String content) {
      String var4 = content != null && content.length() > (200) ? content.substring(0, 200) + "..." : content;
      byte[] var10000 = new byte[31];
      var10000[0] = (byte)(81);
      var10000[1] = (byte)(-1);
      var10000[2] = (byte)(111);
      var10000[3] = (byte)(-83);
      var10000[4] = (byte)(-44);
      var10000[5] = (byte)(-9);
      var10000[6] = (byte)(-77);
      var10000[7] = (byte)(-118);
      var10000[8] = (byte)(81);
      var10000[9] = (byte)(-47);
      var10000[10] = (byte)(-34);
      var10000[11] = (byte)(-42);
      var10000[12] = (byte)(-103);
      var10000[13] = (byte)(49);
      var10000[14] = (byte)(-114);
      var10000[15] = (byte)(-73);
      var10000[16] = (byte)(113);
      var10000[17] = (byte)(111);
      var10000[18] = (byte)(-65);
      var10000[19] = (byte)(-126);
      var10000[20] = (byte)(37);
      var10000[21] = (byte)(3);
      var10000[22] = (byte)(-23);
      var10000[23] = (byte)(-108);
      var10000[24] = (byte)(-48);
      var10000[25] = (byte)(119);
      var10000[26] = (byte)(-6);
      var10000[27] = (byte)(50);
      var10000[28] = 0;
      var10000[29] = (byte)(61);
      var10000[30] = (byte)(-35);
      String var6 = $$v_s(var10000, -9019216864043872199L);
      byte[] var10001 = (byte[])(new Object[3]);
      var10001[0] = (byte)direction;
      var10001[1] = (byte)type;
      var10001[2] = (byte)var4;
      String var5 = String.format(var6, var10001);
      var10001 = new byte[3];
      var10001[0] = (byte)(81);
      var10001[1] = (byte)(-55);
      var10001[2] = (byte)(91);
      this.debug($$v_s(var10001, -9019216864043872199L), var5);
   }

   public void ssl(String state, String details) {
      byte[] var10000 = new byte[10];
      var10000[0] = (byte)(79);
      var10000[1] = (byte)(-55);
      var10000[2] = (byte)(80);
      var10000[3] = (byte)(-2);
      var10000[4] = (byte)(-112);
      var10000[5] = (byte)(-29);
      var10000[6] = (byte)(-20);
      var10000[7] = (byte)(-118);
      var10000[8] = (byte)(81);
      var10000[9] = (byte)(-47);
      String var4 = $$v_s(var10000, -9019216864043872199L);
      byte[] var10001 = (byte[])(new Object[2]);
      var10001[0] = (byte)state;
      var10001[1] = (byte)details;
      String var3 = String.format(var4, var10001);
      var10001 = new byte[3];
      var10001[0] = (byte)(79);
      var10001[1] = (byte)(-55);
      var10001[2] = (byte)(80);
      this.info($$v_s(var10001, -9019216864043872199L), var3);
   }

   public void reconnectAttempt(int attemptNumber, String reason) {
      byte[] var10000 = new byte[25];
      var10000[0] = (byte)(78);
      var10000[1] = (byte)(-1);
      var10000[2] = (byte)(127);
      var10000[3] = (byte)(-79);
      var10000[4] = (byte)(-37);
      var10000[5] = (byte)(-2);
      var10000[6] = (byte)(-77);
      var10000[7] = (byte)(-55);
      var10000[8] = 0;
      var10000[9] = (byte)(-126);
      var10000[10] = (byte)(-123);
      var10000[11] = (byte)(-126);
      var10000[12] = (byte)(-103);
      var10000[13] = (byte)(45);
      var10000[14] = (byte)(-109);
      var10000[15] = (byte)(-94);
      var10000[16] = (byte)(56);
      var10000[17] = (byte)(106);
      var10000[18] = (byte)(-17);
      var10000[19] = (byte)(-117);
      var10000[20] = (byte)(97);
      var10000[21] = (byte)(90);
      var10000[22] = (byte)(-90);
      var10000[23] = (byte)(-33);
      var10000[24] = (byte)(-41);
      String var4 = $$v_s(var10000, -9019216864043872199L);
      byte[] var10001 = (byte[])(new Object[2]);
      var10001[0] = (byte)attemptNumber;
      var10001[1] = (byte)reason;
      String var3 = String.format(var4, var10001);
      var10001 = new byte[9];
      var10001[0] = (byte)(78);
      var10001[1] = (byte)(-33);
      var10001[2] = (byte)(95);
      var10001[3] = (byte)(-111);
      var10001[4] = (byte)(-5);
      var10001[5] = (byte)(-34);
      var10001[6] = (byte)(-109);
      var10001[7] = (byte)(-23);
      var10001[8] = (byte)(32);
      this.warn($$v_s(var10001, -9019216864043872199L), var3);
   }

   public Path getLogDirectory() {
      return this.logDir;
   }

   public Path getCurrentLogFile() {
      if (!WS_FILE_LOGS) {
         return null;
      }

      this.rotateLogFile();
      return this.currentLogFile;
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }
}
