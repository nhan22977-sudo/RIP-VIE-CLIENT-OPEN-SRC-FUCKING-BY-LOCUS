package me.drake.vieclient.modules.client;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import me.drake.vieclient.VieClientMod;

public class DiscordPresence {
   private static final String DEFAULT_CLIENT_ID = "1501618761810055364";
   private static final String CLIENT_ID = resolveClientId();
   private static final long UPDATE_INTERVAL_MS = 5000L;
   private static final int MAX_PIPE_INDEX = 9;
   private static final String ACTIVITY_NAME = "Vie Client";
   private static final String DISCORD_BUTTON_LABEL = "Discord";
   private static final String DISCORD_INVITE_URL = "https://discord.gg/RhqaEw6FdR";
   private static final String WEBSITE_BUTTON_LABEL = "Website";
   private static final String WEBSITE_URL = "https://trillium.solutions";
   private RandomAccessFile pipe;
   private volatile boolean connected = false;
   private long lastUpdate = 0L;
   private long startTimestamp;
   private String lastDetails;
   private String lastState;
   private static final ExecutorService RPC_EXECUTOR = Executors.newSingleThreadExecutor(r -> {
      byte[] var10003 = new byte[22];
      var10003[0] = (byte)(115);
      var10003[1] = (byte)(-13);
      var10003[2] = (byte)(121);
      var10003[3] = (byte)(-13);
      var10003[4] = (byte)(-105);
      var10003[5] = (byte)(47);
      var10003[6] = (byte)(42);
      var10003[7] = (byte)(33);
      var10003[8] = (byte)(2);
      var10003[9] = (byte)(-48);
      var10003[10] = (byte)(-128);
      var10003[11] = (byte)(-37);
      var10003[12] = (byte)(-39);
      var10003[13] = (byte)(-18);
      var10003[14] = (byte)(18);
      var10003[15] = (byte)(23);
      var10003[16] = (byte)(34);
      var10003[17] = (byte)(37);
      var10003[18] = (byte)(-66);
      var10003[19] = (byte)(-59);
      var10003[20] = (byte)(38);
      var10003[21] = (byte)(-60);
      Thread var1 = new Thread(r, $$v_s(var10003, 7663605160768426016L));
      var1.setDaemon(true);
      var1.setPriority(1);
      return var1;
   });
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public boolean connect() {
      if (this.connected) {
         return true;
      }

      this.closePipe();
      this.startTimestamp = System.currentTimeMillis() / 1000L;

      try {
         IOException var1 = null;

         for (int var2 = 0; var2 <= 9; var2++) {
            try {
               this.pipe = new RandomAccessFile("\\\\.\\pipe\\discord-ipc-" + var2, $$v_s(new byte[]{119, -19}, 7663605160768426016L));
               break;
            } catch (IOException var4) {
               var1 = var4;
            }
         }

         if (this.pipe == null) {
            if (var1 != null) {
               throw var1;
            } else {
               throw new IOException(
                  $$v_s(
                     new byte[]{
                        65, -13, 111, -67, -100, 52, 61, 98, 36, -14, -89, -42, -37, -9, 1, 95, 38, 106, -71, -64, 34, -64, 104, 123, -47, 115, -10, 42, 30
                     },
                     7663605160768426016L
                  )
               );
            }
         } else {
            String var6 = String.format(
               $$v_s(
                  new byte[]{126, -72, 106, -4, -55, 119, 117, 96, 14, -50, -115, -109, -59, -22, 46, 83, 49, 104, -10, -116, 102, -59, 43, 111},
                  7663605160768426016L
               ),
               CLIENT_ID
            );
            this.sendFrame(0, var6);
            String var3 = this.readFrame();
            if (var3 == null) {
               throw new IOException(
                  $$v_s(new byte[]{75, -11, 60, -74, -110, 40, 61, 49, 5, -61, -113, -109, -117, -20, 20, 73, 37, 37, -94, -35, 38}, 7663605160768426016L)
               );
            }

            if (!var3.contains($$v_s(new byte[]{39, -1, 106, -86, -47, 124, 123, 16, 40, -29, -96, -81, -119}, 7663605160768426016L))) {
               throw new IOException("Handshake rejected: " + this.trimForLog(var3));
            }

            this.connected = true;
            this.lastUpdate = 0L;
            VieClientMod.LOGGER
               .info(
                  $$v_s(
                     new byte[]{
                        94,
                        -34,
                        117,
                        -83,
                        -112,
                        41,
                        43,
                        38,
                        63,
                        -14,
                        -89,
                        -85,
                        -117,
                        -35,
                        30,
                        84,
                        59,
                        47,
                        -81,
                        -38,
                        38,
                        -46,
                        41,
                        102,
                        -46,
                        50,
                        -48,
                        47,
                        8,
                        -83,
                        78,
                        -72,
                        -127,
                        26,
                        84,
                        29,
                        127,
                        -113,
                        -100,
                        -52,
                        -71,
                        -99,
                        45,
                        114,
                        -10,
                        69,
                        -20,
                        -77
                     },
                     7663605160768426016L
                  ),
                  CLIENT_ID
               );
            if (this.lastDetails == null && this.lastState == null) {
               this.updatePresence(
                  $$v_s(new byte[]{85, -10, 125, -89, -102, 40, 62, 98, 59, -53, -127, -42, -24, -14, 24, 95, 59, 62}, 7663605160768426016L), null
               );
            } else {
               this.updatePresence(this.lastDetails, this.lastState);
            }

            return true;
         }
      } catch (Exception var5) {
         VieClientMod.LOGGER
            .warn(
               $$v_s(
                  new byte[]{
                     94,
                     -34,
                     117,
                     -83,
                     -112,
                     41,
                     43,
                     38,
                     63,
                     -14,
                     -89,
                     -85,
                     -117,
                     -40,
                     16,
                     83,
                     57,
                     47,
                     -88,
                     -114,
                     55,
                     -39,
                     41,
                     113,
                     -46,
                     124,
                     -6,
                     35,
                     24,
                     -70,
                     1,
                     -30,
                     -122,
                     86,
                     21,
                     27,
                     125,
                     -110,
                     -90,
                     -53,
                     -87,
                     -1,
                     63,
                     107,
                     -25,
                     30,
                     -31,
                     -13,
                     69,
                     -113,
                     95,
                     -18,
                     -45,
                     123,
                     -46,
                     15,
                     52,
                     -120,
                     -44,
                     -99,
                     -26
                  },
                  7663605160768426016L
               ),
               new Object[]{CLIENT_ID, 9, var5.getMessage()}
            );
         this.closePipe();
         this.connected = false;
         return false;
      }
   }

   public void disconnect() {
      if (this.connected || this.pipe != null) {
         if (this.connected) {
            this.clearActivity();
         }

         this.connected = false;
         this.lastDetails = null;
         this.lastState = null;
         this.closePipe();
         byte[] var10001 = new byte[25];
         var10001[0] = (byte)(94);
         var10001[1] = (byte)(-34);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-83);
         var10001[4] = (byte)(-112);
         var10001[5] = (byte)(41);
         var10001[6] = (byte)(43);
         var10001[7] = (byte)(38);
         var10001[8] = (byte)(63);
         var10001[9] = (byte)(-14);
         var10001[10] = (byte)(-89);
         var10001[11] = (byte)(-85);
         var10001[12] = (byte)(-117);
         var10001[13] = (byte)(-38);
         var10001[14] = (byte)(24);
         var10001[15] = (byte)(73);
         var10001[16] = (byte)(54);
         var10001[17] = (byte)(37);
         var10001[18] = (byte)(-94);
         var10001[19] = (byte)(-64);
         var10001[20] = (byte)(38);
         var10001[21] = (byte)(-43);
         var10001[22] = (byte)(125);
         var10001[23] = (byte)(119);
         var10001[24] = (byte)(-39);
         VieClientMod.LOGGER.info($$v_s(var10001, 7663605160768426016L));
      }
   }

   public boolean isConnected() {
      return this.connected;
   }

   public void tick(String details, String state) {
      if (this.connected) {
         long var3 = System.currentTimeMillis();
         boolean var5 = !Objects.equals(details, this.lastDetails) || !Objects.equals(state, this.lastState);
         if (var5 || var3 - this.lastUpdate >= 5000L) {
            this.updatePresence(details, state);
         }
      }
   }

   public void tick(String detail) {
      this.tick(detail, null);
   }

   public void updatePresence(String detail) {
      this.updatePresence(detail, null);
   }

   public void updatePresence(String details, String state) {
      if (this.pipe != null && this.connected) {
         if (details != null) {
            if (details.contains($$v_s(new byte[]{83, -13, 121, -2, -80, 46, 60, 35, 25, -47, -60, -75, -57, -9, 20, 84, 33}, 7663605160768426016L))) {
               details = details.replace(
                  $$v_s(new byte[]{83, -13, 121, -2, -80, 46, 60, 35, 25, -47, -60, -75, -57, -9, 20, 84, 33}, 7663605160768426016L),
                  $$v_s(new byte[]{83, -13, 121, -2, -80, 42, 48, 39, 3, -42}, 7663605160768426016L)
               );
            }

            if (details.contains($$v_s(new byte[]{83, -13, 121, -2, -80, 46, 60, 35, 25, -47}, 7663605160768426016L))) {
               details = details.replace(
                  $$v_s(new byte[]{83, -13, 121, -2, -80, 46, 60, 35, 25, -47}, 7663605160768426016L),
                  $$v_s(new byte[]{83, -13, 121, -2, -80, 42, 48, 39, 3, -42}, 7663605160768426016L)
               );
            }
         }

         if (state != null) {
            if (state.contains($$v_s(new byte[]{83, -13, 121, -2, -80, 46, 60, 35, 25, -47, -60, -75, -57, -9, 20, 84, 33}, 7663605160768426016L))) {
               state = state.replace(
                  $$v_s(new byte[]{83, -13, 121, -2, -80, 46, 60, 35, 25, -47, -60, -75, -57, -9, 20, 84, 33}, 7663605160768426016L),
                  $$v_s(new byte[]{83, -13, 121, -2, -80, 42, 48, 39, 3, -42}, 7663605160768426016L)
               );
            }

            if (state.contains($$v_s(new byte[]{83, -13, 121, -2, -80, 46, 60, 35, 25, -47}, 7663605160768426016L))) {
               state = state.replace(
                  $$v_s(new byte[]{83, -13, 121, -2, -80, 46, 60, 35, 25, -47}, 7663605160768426016L),
                  $$v_s(new byte[]{83, -13, 121, -2, -80, 42, 48, 39, 3, -42}, 7663605160768426016L)
               );
            }
         }

         this.lastDetails = details;
         this.lastState = state;
         this.lastUpdate = System.currentTimeMillis();
         String var3 = details;
         String var4 = state;
         long var5 = this.startTimestamp;
         RPC_EXECUTOR.execute(
            () -> {
               try {
                  if (this.pipe == null || !this.connected) {
                     return;
                  }

                  StringBuilder var5x = new StringBuilder();
                  var5x.append(
                        $$v_s(
                           new byte[]{
                              126,
                              -72,
                              127,
                              -77,
                              -105,
                              100,
                              99,
                              96,
                              62,
                              -25,
                              -80,
                              -87,
                              -22,
                              -35,
                              37,
                              115,
                              3,
                              3,
                              -104,
                              -9,
                              97,
                              -102,
                              43,
                              115,
                              -49,
                              117,
                              -25,
                              100,
                              65,
                              -75,
                              3,
                              -70,
                              -116,
                              94,
                              94,
                              68
                           },
                           7663605160768426016L
                        )
                     )
                     .append(ProcessHandle.current().pid());
                  var5x.append($$v_s(new byte[]{41, -72, 125, -67, -121, 47, 47, 43, 25, -37, -58, -52, -48}, 7663605160768426016L));
                  var5x.append($$v_s(new byte[]{39, -12, 125, -77, -106, 100, 99, 96}, 7663605160768426016L))
                     .append(this.escapeJson($$v_s(new byte[]{83, -13, 121, -2, -80, 42, 48, 39, 3, -42}, 7663605160768426016L)))
                     .append($$v_s(new byte[]{39, -74}, 7663605160768426016L));
                  var5x.append($$v_s(new byte[]{39, -18, 101, -82, -106, 100, 99, 114}, 7663605160768426016L));
                  String var6 = var3 != null && !var3.isBlank()
                     ? var3.trim()
                     : $$v_s(new byte[]{85, -10, 125, -89, -102, 40, 62, 98, 59, -53, -127, -42, -24, -14, 24, 95, 59, 62}, 7663605160768426016L);
                  var5x.append($$v_s(new byte[]{41, -72, 120, -69, -121, 39, 48, 46, 30, -128, -34, -44}, 7663605160768426016L))
                     .append(this.escapeJson(var6))
                     .append("\"");
                  if (var4 != null && !var4.isBlank()) {
                     var5x.append($$v_s(new byte[]{41, -72, 111, -86, -110, 50, 60, 96, 87, -128}, 7663605160768426016L))
                        .append(this.escapeJson(var4.trim()))
                        .append("\"");
                  }

                  var5x.append(
                        $$v_s(
                           new byte[]{41, -72, 104, -73, -98, 35, 42, 54, 12, -49, -108, -123, -119, -92, 10, 24, 38, 62, -83, -36, 55, -108, 51},
                           7663605160768426016L
                        )
                     )
                     .append(var5)
                     .append("}");
                  var5x.append(
                     $$v_s(
                        new byte[]{
                           41,
                           -72,
                           125,
                           -83,
                           -128,
                           35,
                           45,
                           49,
                           79,
                           -104,
                           -97,
                           -44,
                           -57,
                           -1,
                           3,
                           93,
                           48,
                           21,
                           -91,
                           -61,
                           34,
                           -47,
                           108,
                           48,
                           -121,
                           48,
                           -30,
                           47,
                           30,
                           -111,
                           66,
                           -94,
                           -128,
                           91,
                           8,
                           13,
                           49,
                           -54,
                           -37,
                           -50,
                           -84,
                           -80,
                           35,
                           115,
                           -108,
                           74,
                           -12,
                           -30,
                           65,
                           -56,
                           22,
                           -20,
                           -75,
                           63,
                           -52,
                           82,
                           94,
                           -34,
                           -99,
                           -125,
                           -11,
                           26,
                           99,
                           87
                        },
                        7663605160768426016L
                     )
                  );
                  var5x.append($$v_s(new byte[]{41, -72, 126, -85, -121, 50, 54, 44, 30, -128, -34, -83}, 7663605160768426016L));
                  var5x.append($$v_s(new byte[]{126, -72, 112, -65, -111, 35, 53, 96, 87, -128}, 7663605160768426016L))
                     .append(this.escapeJson($$v_s(new byte[]{65, -13, 111, -67, -100, 52, 61}, 7663605160768426016L)))
                     .append($$v_s(new byte[]{39, -74, 62, -85, -127, 42, 123, 120, 79}, 7663605160768426016L))
                     .append(
                        this.escapeJson(
                           $$v_s(
                              new byte[]{
                                 109,
                                 -18,
                                 104,
                                 -82,
                                 -128,
                                 124,
                                 118,
                                 109,
                                 9,
                                 -53,
                                 -105,
                                 -107,
                                 -60,
                                 -20,
                                 21,
                                 20,
                                 50,
                                 45,
                                 -29,
                                 -40,
                                 42,
                                 -45,
                                 106,
                                 126,
                                 -44,
                                 119,
                                 -6,
                                 50
                              },
                              7663605160768426016L
                           )
                        )
                     )
                     .append($$v_s(new byte[]{39, -25, 48}, 7663605160768426016L));
                  var5x.append($$v_s(new byte[]{126, -72, 112, -65, -111, 35, 53, 96, 87, -128}, 7663605160768426016L))
                     .append(this.escapeJson($$v_s(new byte[]{82, -1, 126, -83, -102, 50, 60}, 7663605160768426016L)))
                     .append($$v_s(new byte[]{39, -74, 62, -85, -127, 42, 123, 120, 79}, 7663605160768426016L))
                     .append(
                        this.escapeJson(
                           $$v_s(
                              new byte[]{109, -18, 104, -82, -128, 124, 118, 109, 27, -53, -127, -123, -50, -20, 7, 83, 54, 47, -65, -128, 55, -39, 121},
                              7663605160768426016L
                           )
                        )
                     )
                     .append($$v_s(new byte[]{39, -25}, 7663605160768426016L));
                  var5x.append("]");
                  var5x.append($$v_s(new byte[]{41, -72, 117, -80, -128, 50, 56, 44, 14, -57, -58, -52, -33, -20, 4, 95}, 7663605160768426016L));
                  var5x.append($$v_s(new byte[]{120, -25, 48, -4, -99, 41, 55, 33, 8, -128, -34, -44}, 7663605160768426016L))
                     .append(System.nanoTime())
                     .append($$v_s(new byte[]{39, -25}, 7663605160768426016L));
                  this.sendFrame(1, var5x.toString());
               } catch (Exception var7) {
                  VieClientMod.LOGGER.warn("[DiscordRPC] Update failed: " + var7.getMessage());
                  this.connected = false;
                  this.closePipe();
               }
            }
         );
      }
   }

   private void clearActivity() {
      RPC_EXECUTOR.execute(
         () -> {
            if (this.pipe != null) {
               try {
                  String var1 = String.format(
                     $$v_s(
                        new byte[]{
                           126,
                           -72,
                           127,
                           -77,
                           -105,
                           100,
                           99,
                           96,
                           62,
                           -25,
                           -80,
                           -87,
                           -22,
                           -35,
                           37,
                           115,
                           3,
                           3,
                           -104,
                           -9,
                           97,
                           -102,
                           43,
                           115,
                           -49,
                           117,
                           -25,
                           100,
                           65,
                           -75,
                           3,
                           -70,
                           -116,
                           94,
                           94,
                           68,
                           54,
                           -126,
                           -43,
                           -128,
                           -84,
                           -95,
                           48,
                           127,
                           -67,
                           87,
                           -27,
                           -29,
                           23,
                           -48,
                           66,
                           -69,
                           -113,
                           58,
                           -44,
                           94,
                           63,
                           -36,
                           -101,
                           -120,
                           -8,
                           11,
                           99,
                           16,
                           103,
                           127,
                           -81,
                           -68,
                           -50
                        },
                        7663605160768426016L
                     ),
                     ProcessHandle.current().pid(),
                     UUID.randomUUID()
                  );
                  this.sendFrame(1, var1);
               } catch (Exception var2) {
               }
            }
         }
      );
   }

   private void sendFrame(int opcode, String payload) throws IOException {
      if (this.pipe == null) {
         byte[] var10002 = new byte[31];
         var10002[0] = (byte)(65);
         var10002[1] = (byte)(-13);
         var10002[2] = (byte)(111);
         var10002[3] = (byte)(-67);
         var10002[4] = (byte)(-100);
         var10002[5] = (byte)(52);
         var10002[6] = (byte)(61);
         var10002[7] = (byte)(98);
         var10002[8] = (byte)(36);
         var10002[9] = (byte)(-14);
         var10002[10] = (byte)(-89);
         var10002[11] = (byte)(-42);
         var10002[12] = (byte)(-37);
         var10002[13] = (byte)(-9);
         var10002[14] = 1;
         var10002[15] = (byte)(95);
         var10002[16] = (byte)(117);
         var10002[17] = (byte)(35);
         var10002[18] = (byte)(-65);
         var10002[19] = (byte)(-114);
         var10002[20] = (byte)('讼' ^ '讑');
         var10002[21] = (byte)(-39);
         var10002[22] = (byte)(125);
         var10002[23] = (byte)(50);
         var10002[24] = (byte)(-46);
         var10002[25] = (byte)(98);
         var10002[26] = (byte)(-15);
         var10002[27] = (byte)(40);
         var10002[28] = (byte)(30);
         var10002[29] = (byte)(-86);
         var10002[30] = (byte)(15);
         throw new IOException($$v_s(var10002, 7663605160768426016L));
      }

      byte[] var3 = payload.getBytes(StandardCharsets.UTF_8);
      ByteBuffer var4 = ByteBuffer.allocate((8) + var3.length);
      var4.order(ByteOrder.LITTLE_ENDIAN);
      var4.putInt(opcode);
      var4.putInt(var3.length);
      var4.put(var3);
      this.pipe.write(var4.array());
   }

   private String readFrame() {
      try {
         byte[] var1 = new byte[8];
         this.pipe.readFully(var1);
         ByteBuffer var2 = ByteBuffer.wrap(var1).order(ByteOrder.LITTLE_ENDIAN);
         var2.getInt();
         int var3 = var2.getInt();
         if (var3 >= 0 && var3 <= (1048576)) {
            byte[] var4 = new byte[var3];
            this.pipe.readFully(var4);
            return new String(var4, StandardCharsets.UTF_8);
         } else {
            throw new IOException("Invalid Discord IPC frame length: " + var3);
         }
      } catch (Exception var5) {
         return null;
      }
   }

   private void closePipe() {
      try {
         if (this.pipe != null) {
            this.pipe.close();
         }
      } catch (IOException var2) {
      }

      this.pipe = null;
   }

   private static String resolveClientId() {
      String var0 = System.getProperty(
            $$v_s(new byte[]{115, -13, 121, -16, -105, 47, 42, 33, 2, -48, -128, -40, -56, -14, 24, 95, 59, 62, -123, -54}, 7663605160768426016L), ""
         )
         .trim();
      if (!var0.isEmpty()) {
         return var0;
      }

      String var1 = System.getenv(
         $$v_s(new byte[]{83, -45, 89, -127, -73, 15, 10, 1, 34, -16, -96, -87, -24, -46, 56, 127, 27, 30, -109, -25, 7}, 7663605160768426016L)
      );
      return var1 != null && !var1.isBlank()
         ? var1.trim()
         : $$v_s(new byte[]{52, -81, 44, -17, -59, 119, 97, 117, 91, -109, -36, -57, -101, -82, 68, 15, 102, 124, -8}, 7663605160768426016L);
   }

   private static String formatDisplayVersion(String rawVersion) {
      if (rawVersion != null && !rawVersion.isBlank()) {
         String var2 = rawVersion.trim();
         byte[] var10001 = new byte[2];
         var10001[0] = (byte)(89);
         var10001[1] = (byte)(-76);
         String[] var1 = var2.split($$v_s(var10001, 7663605160768426016L));
         return var1.length >= (2) ? var1[0] + "." + var1[1] : rawVersion.trim();
      } else {
         byte[] var10000 = new byte[3];
         var10000[0] = (byte)(52);
         var10000[1] = (byte)(-76);
         var10000[2] = (byte)(44);
         return $$v_s(var10000, 7663605160768426016L);
      }
   }

   private String escapeJson(String s) {
      if (s != null && !s.isEmpty()) {
         StringBuilder var2 = new StringBuilder(s.length() + (16));

         for (int var3 = 0; var3 < s.length(); var3++) {
            char var4 = s.charAt(var3);
            switch (var4) {
               case '\b':
                  byte[] var11 = new byte[2];
                  var11[0] = (byte)(89);
                  var11[1] = (byte)(-8);
                  var2.append($$v_s(var11, 7663605160768426016L));
                  break;
               case '\t':
                  byte[] var10 = new byte[2];
                  var10[0] = (byte)(89);
                  var10[1] = (byte)(-18);
                  var2.append($$v_s(var10, 7663605160768426016L));
                  break;
               case '\n':
                  byte[] var9 = new byte[2];
                  var9[0] = (byte)(89);
                  var9[1] = (byte)(-12);
                  var2.append($$v_s(var9, 7663605160768426016L));
                  break;
               case '\f':
                  byte[] var8 = new byte[2];
                  var8[0] = (byte)(89);
                  var8[1] = (byte)(-4);
                  var2.append($$v_s(var8, 7663605160768426016L));
                  break;
               case '\r':
                  byte[] var7 = new byte[2];
                  var7[0] = (byte)(89);
                  var7[1] = (byte)(-24);
                  var2.append($$v_s(var7, 7663605160768426016L));
                  break;
               case '"':
                  byte[] var6 = new byte[2];
                  var6[0] = (byte)(89);
                  var6[1] = (byte)(-72);
                  var2.append($$v_s(var6, 7663605160768426016L));
                  break;
               case '\\':
                  byte[] var5 = new byte[2];
                  var5[0] = (byte)(89);
                  var5[1] = (byte)(-58);
                  var2.append($$v_s(var5, 7663605160768426016L));
                  break;
               default:
                  if (var4 < (32)) {
                     byte[] var10001 = new byte[6];
                     var10001[0] = (byte)(89);
                     var10001[1] = (byte)(-17);
                     var10001[2] = (byte)(57);
                     var10001[3] = (byte)(-18);
                     var10001[4] = (byte)(-57);
                     var10001[5] = (byte)(62);
                     var2.append(String.format($$v_s(var10001, 7663605160768426016L), Integer.valueOf(var4)));
                  } else {
                     var2.append(var4);
                  }
            }
         }

         return var2.toString();
      } else {
         return "";
      }
   }

   private String trimForLog(String raw) {
      if (raw != null && !raw.isEmpty()) {
         return raw.length() <= (180) ? raw : raw.substring(0, 180) + "...";
      } else {
         return "";
      }
   }

   // $VF: synthetic method
   private static String $$v_s0(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      return $$v_s0(var0, var1)
         .replace("discord.gg/45tgxtAwzD", "discord.gg/RhqaEw6FdR")
         .replace("vieclient.cc", "trillium.solutions")
         .replace("discord.gg/vieclient", "discord.gg/RhqaEw6FdR")
         .replace("https://vieservices.top", "https://trillium.solutions");
   }
}
