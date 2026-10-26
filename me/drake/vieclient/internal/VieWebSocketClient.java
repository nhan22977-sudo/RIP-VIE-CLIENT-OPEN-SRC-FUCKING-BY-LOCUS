package me.drake.vieclient.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocketHandshakeException;
import java.net.http.HttpClient.Builder;
import java.net.http.HttpClient.Version;
import java.net.http.WebSocket.Listener;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import me.drake.vieclient.security.ChimeraEngine;
import net.minecraft.class_310;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VieWebSocketClient {
   private static final Logger LOGGER = LoggerFactory.getLogger(VieWebSocketClient.class);
   private static final int MAX_RECONNECT_ATTEMPTS = 8;
   private final String serverUrl;
   private final String keyCode;
   private final String hwid;
   private final ChimeraEngine chimeraEngine;
   private final WebSocketLogger wsLogger;
   private volatile WebSocket wsClient;
   private volatile boolean connected = false;
   private volatile boolean connecting = false;
   private volatile boolean reconnectEnabled = true;
   private volatile boolean closingByClient = false;
   private final ScheduledExecutorService executorService;
   private final ScheduledExecutorService pingExecutor;
   private volatile ScheduledFuture pingTask;
   private final AtomicInteger reconnectAttempts = new AtomicInteger(0);
   private volatile long connectionStartTime = 0L;
   private final ConcurrentHashMap moduleCheckCallbacks = new ConcurrentHashMap();
   private VieWebSocketClient.MessageHandler messageHandler;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void sendModuleCheck(String moduleName, Consumer callback) {
      String var3 = UUID.randomUUID().toString().substring(0, 8);
      this.moduleCheckCallbacks.put(var3, callback);

      try {
         byte[] var10002 = new byte[2];
         var10002[0] = (byte)(-5);
         var10002[1] = (byte)(-58);
         String var10000 = moduleName.replace("\\", $$v_s(var10002, -2988010967076188030L));
         var10002 = new byte[2];
         var10002[0] = (byte)(-5);
         var10002[1] = (byte)(-72);
         String var4 = var10000.replace("\"", $$v_s(var10002, -2988010967076188030L));
         String var5 = "{\"type\":\"module_check\",\"moduleName\":\""
            + var4
            + "\",\"key\":\""
            + (this.keyCode != null ? this.keyCode : "")
            + "\",\"hwid\":\""
            + (this.hwid != null ? this.hwid : "")
            + "\",\"nonce\":\""
            + var3
            + "\"}";
         this.sendEncrypted(var5);
         this.executorService.schedule(() -> {
            Consumer var2 = (Consumer)this.moduleCheckCallbacks.remove(var3);
            if (var2 != null) {
               var2.accept(false);
            }
         }, 10L, TimeUnit.SECONDS);
      } catch (Exception var6) {
         this.moduleCheckCallbacks.remove(var3);
         if (callback != null) {
            callback.accept(false);
         }
      }
   }

   public VieWebSocketClient(String serverUrl, String keyCode, String hwid, String chimeraKey) {
      this.serverUrl = serverUrlx;
      this.keyCode = keyCodex;
      this.hwid = hwidx;
      this.chimeraEngine = new ChimeraEngine(chimeraKey);
      this.executorService = Executors.newSingleThreadScheduledExecutor(r -> {
         byte[] var10003 = new byte[19];
         var10003[0] = (byte)(-16);
         var10003[1] = (byte)(-1);
         var10003[2] = (byte)(126);
         var10003[3] = (byte)(-115);
         var10003[4] = (byte)(13);
         var10003[5] = (byte)(-50);
         var10003[6] = (byte)(-32);
         var10003[7] = (byte)(-101);
         var10003[8] = (byte)(-69);
         var10003[9] = (byte)(-113);
         var10003[10] = (byte)(-74);
         var10003[11] = (byte)(-109);
         var10003[12] = (byte)(89);
         var10003[13] = (byte)(26);
         var10003[14] = (byte)(-51);
         var10003[15] = (byte)(-24);
         var10003[16] = (byte)(-110);
         var10003[17] = (byte)(41);
         var10003[18] = (byte)(-72);
         Thread var1 = new Thread(r, $$v_s(var10003, -2988010967076188030L));
         var1.setDaemon(true);
         return var1;
      });
      this.pingExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
         byte[] var10003 = new byte[14];
         var10003[0] = (byte)(-16);
         var10003[1] = (byte)(-1);
         var10003[2] = (byte)(126);
         var10003[3] = (byte)(-115);
         var10003[4] = (byte)(13);
         var10003[5] = (byte)(-50);
         var10003[6] = (byte)(-32);
         var10003[7] = (byte)(-101);
         var10003[8] = (byte)(-69);
         var10003[9] = (byte)(-113);
         var10003[10] = (byte)(-76);
         var10003[11] = (byte)(-97);
         var10003[12] = (byte)(84);
         var10003[13] = (byte)(18);
         Thread var1 = new Thread(r, $$v_s(var10003, -2988010967076188030L));
         var1.setDaemon(true);
         return var1;
      });
      Path var5 = class_310.method_1551().field_1697.toPath();
      this.wsLogger = new WebSocketLogger(var5);
      this.wsLogger
         .info(
            "INIT",
            "WebSocketClient created with external credentials"
         );
   }

   public void setMessageHandler(VieWebSocketClient.MessageHandler handler) {
      this.messageHandler = handler;
   }

   public synchronized void connect() {
      this.wsLogger
         .info(
            "CONNECT",
            "Connecting to server..."
         );
      if (!this.connected && !this.connecting) {
         try {
            URI var1 = new URI(this.serverUrl);
            String var2 = var1.getScheme() == null ? "" : var1.getScheme().toLowerCase();
            boolean var3 = "wss".equals(var2)
               || "https".equals(var2);
            boolean var4 = "ws".equals(var2)
               || "http".equals(var2);
            Builder var5 = HttpClient.newBuilder().version(Version.HTTP_1_1).connectTimeout(Duration.ofSeconds(10L));
            if (var3) {
               this.wsLogger
                  .info(
                     "PROTOCOL",
                     "Using WebSocket Secure (WSS) - TLS encrypted"
                  );
               this.wsLogger
                  .ssl(
                     "INIT",
                     "Using JVM default certificate and hostname validation"
                  );
            } else {
               if (!var4) {
                  this.wsLogger
                     .error(
                        "PROTOCOL",
                        "Unknown protocol in URL"
                     );
                  throw new IllegalArgumentException(
                     "URL must start with ws://, wss://, http://, or https://"
                  );
               }

               this.wsLogger
                  .warn(
                     "SECURITY",
                     "WARNING: Using non-secure WebSocket (WS). Data is plaintext."
                  );
               LOGGER.warn(
                  "[WebSocket] Using non-secure WebSocket (ws://)"
               );
            }

            this.wsLogger
               .info(
                  "CONNECT",
                  String.format(
                     "Protocol: %s, Starting connection...",
                     var3 ? "WSS" : "WS"
                  )
               );
            HttpClient var6 = var5.build();
            VieWebSocketClient.ClientListener var7 = new VieWebSocketClient.ClientListener();
            this.connecting = true;
            var6.newWebSocketBuilder()
               .connectTimeout(Duration.ofSeconds(10L))
               .buildAsync(var1, var7)
               .whenComplete(
                  (socket, throwable) -> {
                     if (throwable != null) {
                        this.connecting = false;
                        this.connected = false;
                        this.wsClient = null;
                        int var3x = extractHandshakeStatus(throwable);
                        boolean var4x = isFatalHandshakeStatus(var3x);
                        if (var3x > 0) {
                           this.wsLogger
                              .warn(
                                 "CONNECT",
                                 String.format(
                                    "Handshake rejected with HTTP %d",
                                    var3x
                                 )
                              );
                        }

                        if (var4x) {
                           String var5x = "WebSocket handshake rejected by server";
                           this.wsLogger
                              .warn(
                                 "CONNECT",
                                 String.format(
                                    "%s (HTTP %d)", var5x, var3x
                                 )
                              );
                           LOGGER.warn(
                              "[WebSocket] {} (HTTP {}).",
                              var5x,
                              var3x
                           );
                        } else {
                           this.wsLogger
                              .error(
                                 "CONNECT",
                                 String.format(
                                    "Connection failed: %s",
                                    throwable.getMessage()
                                 ),
                                 throwable
                              );
                           LOGGER.error(
                              "[WebSocket] Connection failed: {}",
                              throwable.getMessage(),
                              throwable
                           );
                        }

                        if (this.reconnectEnabled) {
                           if (var4x) {
                              this.reconnectEnabled = false;
                              this.wsLogger
                                 .warn(
                                    "RECONNECT",
                                    String.format(
                                       "Disabling reconnect for this session due to fatal handshake HTTP %d",
                                       var3x
                                    )
                                 );
                              LOGGER.warn(
                                 "[WebSocket] Fatal handshake HTTP {}. Reconnect disabled for this session.",
                                 var3x
                              );
                              return;
                           }

                           int var6x = this.reconnectAttempts.incrementAndGet();
                           if (var6x > 8) {
                              this.reconnectEnabled = false;
                              this.wsLogger
                                 .warn(
                                    "RECONNECT",
                                    String.format(
                                       "Max reconnect attempts reached (%d). Reconnect disabled for this session.",
                                       8
                                    )
                                 );
                              LOGGER.warn(
                                 "[WebSocket] Max reconnect attempts reached ({}). Reconnect disabled.",
                                 8
                              );
                              return;
                           }

                           this.wsLogger
                              .reconnectAttempt(
                                 var6x,
                                 String.format(
                                    "Connection failure: %s",
                                    throwable.getMessage()
                                 )
                              );
                           this.scheduleReconnect();
                        }
                     } else {
                        this.wsClient = socket;
                     }
                  }
               );
         } catch (URISyntaxException var8) {
            this.wsLogger
               .error(
                  "CONNECT",
                  "Invalid URI syntax",
                  var8
               );
            LOGGER.error(
               "[WebSocket] Invalid server URL"
            );
         } catch (Exception var9) {
            this.wsLogger
               .error(
                  "CONNECT",
                  String.format(
                     "Connection failed: %s",
                     var9.getMessage()
                  ),
                  var9
               );
            LOGGER.error(
               "[WebSocket] Connection failed"
            );
         }
      }
   }

   private void handleDisconnect(int code, String reason, boolean remote, AtomicBoolean disconnectHandled) {
      if (disconnectHandled.compareAndSet(false, true)) {
         long var5 = this.connectionStartTime > 0L ? System.currentTimeMillis() - this.connectionStartTime : 0L;
         this.connected = false;
         this.connecting = false;
         this.wsClient = null;
         this.stopPing();
         this.wsLogger
            .connection(
               "CLOSED",
               this.serverUrl,
               String.format(
                  "duration=%dms, remote=%s",
                  var5,
                  remote
               )
            );
         this.wsLogger
            .warn(
               "DISCONNECT",
               String.format(
                  "Connection closed: code=%d, reason=%s, remote=%s, duration=%dms",
                  code,
                  reason,
                  remote,
                  var5
               )
            );
         LOGGER.warn(
            "[WebSocket] Disconnected: {} (code: {})",
            reason,
            code
         );
         if (this.reconnectEnabled) {
            if (isFatalHandshakeStatus(code)) {
               this.reconnectEnabled = false;
               this.wsLogger
                  .warn(
                     "RECONNECT",
                     String.format(
                        "Disabling reconnect due to fatal status code %d",
                        code
                     )
                  );
               LOGGER.warn(
                  "[WebSocket] Fatal status code {}. Reconnect disabled.",
                  code
               );
               return;
            }

            int var7 = this.reconnectAttempts.incrementAndGet();
            if (var7 > 8) {
               this.reconnectEnabled = false;
               this.wsLogger
                  .warn(
                     "RECONNECT",
                     String.format(
                        "Max reconnect attempts reached (%d). Reconnect disabled for this session.",
                        8
                     )
                  );
               LOGGER.warn(
                  "[WebSocket] Max reconnect attempts reached ({}). Reconnect disabled.",
                  8
               );
               return;
            }

            this.wsLogger
               .reconnectAttempt(
                  var7,
                  String.format(
                     "Previous connection closed: %s",
                     reason
                  )
               );
            this.scheduleReconnect();
         }
      }
   }

   private void authenticate() {
      try {
         this.wsLogger
            .auth(
               "SENDING",
               this.keyCode,
               "Preparing authentication message"
            );
         String var1 = String.format(
            "{\"type\":\"authenticate\",\"key\":\"%s\",\"hwid\":\"%s\"}",
            this.keyCode,
            this.hwid
         );
         this.sendEncrypted(var1);
         this.wsLogger
            .auth(
               "SENT",
               this.keyCode,
               "Authentication message encrypted and sent"
            );
      } catch (Exception var2) {
         this.wsLogger
            .error(
               "AUTH",
               String.format(
                  "Failed to send auth: %s",
                  var2.getMessage()
               ),
               var2
            );
         LOGGER.error(
            "[WebSocket] Failed to send auth: {}",
            var2.getMessage()
         );
      }
   }

   private void handleMessage(String message) {
      try {
         if (!message.contains("\"sequence\"")
            || !message.contains("\"locus\"")) {
            this.wsLogger
               .error(
                  "SECURITY",
                  "Received unencrypted message - SECURITY VIOLATION"
               );
            LOGGER.error(
               "[WebSocket] SECURITY VIOLATION: Received unencrypted message"
            );
            this.disconnect();
            return;
         }

         String var2;
         try {
            String var3 = this.extractJsonValue(message, "sequence");
            String var4 = this.extractJsonValue(message, "locus");
            if (var3.isEmpty() || var4.isEmpty()) {
               this.wsLogger
                  .error(
                     "DECRYPT",
                     String.format(
                        "Empty sequence or locus: seq_len=%d, locus_len=%d",
                        var3.length(),
                        var4.length()
                     )
                  );
               this.disconnect();
               return;
            }

            ChimeraEngine.GenomePacket var5 = new ChimeraEngine.GenomePacket(var3, var4);
            var2 = this.chimeraEngine.decode(var5);
         } catch (Exception var21) {
            this.wsLogger
               .error(
                  "DECRYPT",
                  String.format(
                     "Failed to decrypt message: %s",
                     var21.getMessage()
                  ),
                  var21
               );
            LOGGER.error(
               "[WebSocket] SECURITY VIOLATION: Failed to decrypt message: {}",
               var21.getMessage()
            );
            this.disconnect();
            return;
         }

         this.wsLogger
            .message(
               "DECRYPTED",
               "success",
               var2
            );
         if (this.messageHandler != null) {
            this.messageHandler.onMessage(var2);
         }

         if (var2.contains("\"type\":")) {
            String var23 = this.extractJsonValue(var2, "type");
            switch (var23) {
               case "key_expired":
                  String var6 = this.extractJsonValue(var2, "keyCode");
                  this.wsLogger
                     .info(
                        "EVENT",
                        "Key expired notification received"
                     );
                  if (this.messageHandler != null) {
                     this.messageHandler.onKeyExpired(var6);
                  }

                  LOGGER.warn(
                     "[WebSocket] Key expired notification received."
                  );
                  break;
               case "key_expiring_soon":
                  String var7 = this.extractJsonValue(var2, "keyCode");
                  int var8 = this.extractJsonInt(var2, "hoursLeft");
                  if (this.messageHandler != null) {
                     this.messageHandler.onKeyExpiringSoon(var7, var8);
                  }
                  break;
               case "key_revoked":
                  String var9 = this.extractJsonValue(var2, "keyCode");
                  String var10 = this.extractJsonValue(var2, "reason");
                  this.wsLogger
                     .warn(
                        "EVENT",
                        String.format(
                           "Key revoked notification received, reason: %s",
                           var10
                        )
                     );
                  if (this.messageHandler != null) {
                     this.messageHandler.onKeyRevoked(var9, var10);
                  }

                  LOGGER.warn(
                     "[WebSocket] Key revoked notification received (reason: {}).",
                     var10
                  );
                  break;
               case "key_unrevoked":
                  String var11 = this.extractJsonValue(var2, "keyCode");
                  if (this.messageHandler != null) {
                     this.messageHandler.onKeyUnrevoked(var11);
                  }
                  break;
               case "key_extended":
                  String var12 = this.extractJsonValue(var2, "keyCode");
                  String var13 = this.extractJsonValue(
                     var2, "newExpiresAt"
                  );
                  if (this.messageHandler != null) {
                     this.messageHandler.onKeyExtended(var12, var13);
                  }
                  break;
               case "hwid_reset":
                  String var14 = this.extractJsonValue(var2, "keyCode");
                  if (this.messageHandler != null) {
                     this.messageHandler.onHwidReset(var14);
                  }
                  break;
               case "key_deleted":
                  String var15 = this.extractJsonValue(var2, "keyCode");
                  if (this.messageHandler != null) {
                     this.messageHandler.onKeyDeleted(var15);
                  }

                  LOGGER.warn(
                     "[WebSocket] Key deleted notification received."
                  );
                  break;
               case "client_update":
               case "force_update":
               case "update_required":
               case "outdated_client":
                  String var16 = this.extractJsonValue(var2, "version");
                  String var17 = this.extractJsonValue(var2, "message");
                  this.wsLogger.warn("EVENT", "Server update notification received: " + var17);
                  if (this.messageHandler != null) {
                     this.messageHandler.onForceUpdate(var16, var17);
                  }

                  LOGGER.warn(
                     "[WebSocket] Server update notification received (version: {}).",
                     var16
                  );
                  break;
               case "module_check_response":
                  String var18 = this.extractJsonValue(var2, "nonce");
                  boolean var19 = "true"
                     .equals(this.extractJsonValue(var2, "allowed"));
                  Consumer var20 = (Consumer)this.moduleCheckCallbacks.remove(var18);
                  if (var20 != null) {
                     var20.accept(var19);
                  }
            }
         }
      } catch (Exception var22) {
         this.wsLogger
            .error(
               "MSG",
               String.format(
                  "Failed to handle message: %s",
                  var22.getMessage()
               ),
               var22
            );
         LOGGER.error(
            "[WebSocket] Failed to handle message: {}",
            var22.getMessage()
         );
      }
   }

   private void sendEncrypted(String message) {
      try {
         ChimeraEngine.GenomePacket var2 = this.chimeraEngine.encode(message);
         String var3 = String.format(
            "{\"sequence\":\"%s\",\"locus\":\"%s\"}",
            var2.sequence(),
            var2.locus()
         );
         this.wsLogger
            .message(
               "SEND",
               "encrypted",
               String.format(
                  "seq_len=%d, locus_len=%d",
                  var2.sequence().length(),
                  var2.locus().length()
               )
            );
         WebSocket var4 = this.wsClient;
         if (var4 != null && this.connected) {
            var4.sendText(var3, true).exceptionally(error -> {
               WebSocketLogger var10000 = this.wsLogger;
               byte[] var10001 = new byte[4];
               var10001[0] = (byte)(-12);
               var10001[1] = (byte)(-33);
               var10001[2] = (byte)(82);
               var10001[3] = (byte)(-102);
               String var2x = $$v_s(var10001, -2988010967076188030L);
               byte[] var10002 = new byte[26];
               var10002[0] = (byte)(-31);
               var10002[1] = (byte)(-5);
               var10002[2] = (byte)(117);
               var10002[3] = (byte)(-78);
               var10002[4] = (byte)(7);
               var10002[5] = (byte)(-55);
               var10002[6] = (byte)(-85);
               var10002[7] = (byte)(-118);
               var10002[8] = (byte)(-96);
               var10002[9] = (byte)(-126);
               var10002[10] = (byte)(-105);
               var10002[11] = (byte)(-109);
               var10002[12] = (byte)(84);
               var10002[13] = (byte)(17);
               var10002[14] = (byte)(-125);
               var10002[15] = (byte)(-21);
               var10002[16] = (byte)(-110);
               var10002[17] = (byte)(57);
               var10002[18] = (byte)(-65);
               var10002[19] = (byte)(-49);
               var10002[20] = (byte)(-75);
               var10002[21] = (byte)(56);
               var10002[22] = (byte)(-31);
               var10002[23] = (byte)(-114);
               var10002[24] = (byte)(58);
               var10002[25] = (byte)(97);
               var10000.error(var2x, String.format($$v_s(var10002, -2988010967076188030L), error.getMessage()), error);
               return null;
            });
         } else {
            this.wsLogger
               .error(
                  "SEND",
                  "WebSocket client is not connected"
               );
         }
      } catch (Exception var5) {
         this.wsLogger
            .error(
               "SEND",
               String.format(
                  "Failed to encrypt and send: %s",
                  var5.getMessage()
               ),
               var5
            );
         LOGGER.error(
            "[WebSocket] Failed to encrypt and send: {}",
            var5.getMessage()
         );
      }
   }

   private String extractJsonValue(String json, String key) {
      String var3 = "\"" + key + "\":";
      int var4 = json.indexOf(var3);
      if (var4 == (-1)) {
         return "";
      }

      var4 += var3.length();

      while (var4 < json.length() && Character.isWhitespace(json.charAt(var4))) {
         var4++;
      }

      if (var4 < json.length() && json.charAt(var4) == (34)) {
         int var8 = json.indexOf(34, ++var4);
         return var8 == (-1) ? "" : json.substring(var4, var8);
      }

      int var5 = var4;

      while (var5 < json.length() && json.charAt(var5) != (44) && json.charAt(var5) != (125)) {
         var5++;
      }

      return json.substring(var4, var5).trim();
   }

   private int extractJsonInt(String json, String key) {
      String var3 = this.extractJsonValue(json, key);

      try {
         return Integer.parseInt(var3);
      } catch (NumberFormatException var5) {
         return 0;
      }
   }

   private synchronized void startPing() {
      this.stopPing();
      this.pingTask = this.pingExecutor.scheduleAtFixedRate(() -> {
         if (this.connected && this.wsClient != null) {
            try {
               byte[] var3 = new byte[15];
               var3[0] = (byte)(-36);
               var3[1] = (byte)(-72);
               var3[2] = (byte)(104);
               var3[3] = (byte)(-89);
               var3[4] = (byte)(18);
               var3[5] = (byte)(-56);
               var3[6] = (byte)(-87);
               var3[7] = (byte)(-60);
               var3[8] = (byte)(-19);
               var3[9] = (byte)(-46);
               var3[10] = (byte)(-115);
               var3[11] = (byte)(-104);
               var3[12] = (byte)(93);
               var3[13] = (byte)(87);
               var3[14] = (byte)(-34);
               this.sendEncrypted($$v_s(var3, -2988010967076188030L));
            } catch (Exception var2) {
               byte[] var10001 = new byte[35];
               var10001[0] = (byte)(-4);
               var10001[1] = (byte)(-51);
               var10001[2] = (byte)(121);
               var10001[3] = (byte)(-68);
               var10001[4] = (byte)(49);
               var10001[5] = (byte)(-62);
               var10001[6] = (byte)(-24);
               var10001[7] = (byte)(-107);
               var10001[8] = (byte)(-86);
               var10001[9] = (byte)(-42);
               var10001[10] = (byte)(-71);
               var10001[11] = (byte)(-42);
               var10001[12] = (byte)(124);
               var10001[13] = (byte)(20);
               var10001[14] = (byte)(-54);
               var10001[15] = (byte)(-22);
               var10001[16] = (byte)(-110);
               var10001[17] = (byte)(46);
               var10001[18] = (byte)(-20);
               var10001[19] = (byte)(-38);
               var10001[20] = (byte)(-67);
               var10001[21] = (byte)(125);
               var10001[22] = (byte)(-88);
               var10001[23] = (byte)(-53);
               var10001[24] = (byte)(113);
               var10001[25] = (byte)(118);
               var10001[26] = (byte)(-76);
               var10001[27] = (byte)(54);
               var10001[28] = (byte)(-125);
               var10001[29] = (byte)(75);
               var10001[30] = (byte)(-108);
               var10001[31] = (byte)(76);
               var10001[32] = (byte)(103);
               var10001[33] = (byte)(65);
               var10001[34] = 1;
               LOGGER.error($$v_s(var10001, -2988010967076188030L), var2.getMessage());
            }
         }
      }, 10L, 10L, TimeUnit.SECONDS);
   }

   private synchronized void stopPing() {
      ScheduledFuture var1 = this.pingTask;
      if (var1 != null) {
         var1.cancel(false);
         this.pingTask = null;
      }
   }

   private void scheduleReconnect() {
      int var1 = this.reconnectAttempts.get();
      int var2 = Math.min(5 + var1 * 2, 30);
      this.wsLogger
         .info(
            "RECONNECT",
            String.format(
               "Scheduling reconnect attempt #%d in %d seconds",
               var1,
               var2
            )
         );
      this.executorService.schedule(() -> {
         if (!this.connected && !this.connecting && this.reconnectEnabled) {
            WebSocketLogger var10000 = this.wsLogger;
            byte[] var10001 = new byte[9];
            var10001[0] = (byte)(-11);
            var10001[1] = (byte)(-33);
            var10001[2] = (byte)(95);
            var10001[3] = (byte)(-111);
            var10001[4] = (byte)(44);
            var10001[5] = (byte)(-29);
            var10001[6] = (byte)(-50);
            var10001[7] = (byte)(-67);
            var10001[8] = (byte)(-101);
            String var2x = $$v_s(var10001, -2988010967076188030L);
            byte[] var10002 = new byte[31];
            var10002[0] = (byte)(-30);
            var10002[1] = (byte)(-30);
            var10002[2] = (byte)(121);
            var10002[3] = (byte)(-67);
            var10002[4] = (byte)(23);
            var10002[5] = (byte)(-39);
            var10002[6] = (byte)(-30);
            var10002[7] = (byte)(-112);
            var10002[8] = (byte)(-88);
            var10002[9] = (byte)(-126);
            var10002[10] = (byte)(-106);
            var10002[11] = (byte)(-109);
            var10002[12] = (byte)(89);
            var10002[13] = (byte)(26);
            var10002[14] = (byte)(-51);
            var10002[15] = (byte)(-24);
            var10002[16] = (byte)(-110);
            var10002[17] = (byte)(41);
            var10002[18] = (byte)(-72);
            var10002[19] = (byte)(-114);
            var10002[20] = (byte)(-77);
            var10002[21] = (byte)(41);
            var10002[22] = (byte)(-81);
            var10002[23] = (byte)(-53);
            var10002[24] = (byte)(114);
            var10002[25] = (byte)(98);
            var10002[26] = (byte)(-32);
            var10002[27] = (byte)(102);
            var10002[28] = (byte)(-55);
            var10002[29] = 0;
            var10002[30] = (byte)(-105);
            var10000.info(var2x, String.format($$v_s(var10002, -2988010967076188030L), var1));
            this.connect();
         }
      }, var2, TimeUnit.SECONDS);
   }

   public synchronized void disconnect() {
      this.wsLogger
         .info(
            "DISCONNECT",
            "Disconnecting WebSocket..."
         );
      this.reconnectEnabled = false;
      this.connecting = false;
      this.stopPing();
      WebSocket var1 = this.wsClient;
      this.wsClient = null;
      if (var1 != null) {
         try {
            this.closingByClient = true;
            var1.sendClose(1000, "client disconnect")
               .orTimeout(3L, TimeUnit.SECONDS)
               .exceptionally(error -> {
                  WebSocketLogger var10000 = this.wsLogger;
                  byte[] var10001 = new byte[10];
                  var10001[0] = (byte)(-29);
                  var10001[1] = (byte)(-45);
                  var10001[2] = (byte)(79);
                  var10001[3] = (byte)(-99);
                  var10001[4] = (byte)(45);
                  var10001[5] = (byte)(-29);
                  var10001[6] = (byte)(-59);
                  var10001[7] = (byte)(-69);
                  var10001[8] = (byte)(-116);
                  var10001[9] = (byte)(-10);
                  String var2 = $$v_s(var10001, -2988010967076188030L);
                  byte[] var10002 = new byte[27];
                  var10002[0] = (byte)(-30);
                  var10002[1] = (byte)(-24);
                  var10002[2] = (byte)(110);
                  var10002[3] = (byte)(-79);
                  var10002[4] = (byte)(16);
                  var10002[5] = (byte)(-115);
                  var10002[6] = (byte)(-24);
                  var10002[7] = (byte)(-110);
                  var10002[8] = (byte)(-96);
                  var10002[9] = (byte)(-47);
                  var10002[10] = (byte)(-115);
                  var10002[11] = (byte)(-104);
                  var10002[12] = (byte)(93);
                  var10002[13] = (byte)(85);
                  var10002[14] = (byte)(-12);
                  var10002[15] = (byte)(-29);
                  var10002[16] = (byte)(-107);
                  var10002[17] = (byte)(25);
                  var10002[18] = (byte)(-93);
                  var10002[19] = (byte)(-51);
                  var10002[20] = (byte)(-71);
                  var10002[21] = (byte)(56);
                  var10002[22] = (byte)(-81);
                  var10002[23] = (byte)(-108);
                  var10002[24] = (byte)(63);
                  var10002[25] = (byte)(55);
                  var10002[26] = (byte)(-25);
                  var10000.error(var2, String.format($$v_s(var10002, -2988010967076188030L), error.getMessage()), error);
                  return null;
               });
         } catch (Exception var9) {
            this.wsLogger
               .error(
                  "DISCONNECT",
                  String.format(
                     "Error closing WebSocket: %s",
                     var9.getMessage()
                  )
               );

            try {
               var1.abort();
            } catch (Exception var8) {
            }
         } finally {
            this.closingByClient = false;
         }
      }

      this.shutdownExecutor(this.executorService, "reconnect");
      this.shutdownExecutor(this.pingExecutor, "ping");
      this.connected = false;
      this.wsLogger
         .info(
            "DISCONNECT",
            "WebSocket disconnect completed"
         );
   }

   private void shutdownExecutor(ScheduledExecutorService executor, String name) {
      executor.shutdownNow();

      try {
         if (!executor.awaitTermination(2L, TimeUnit.SECONDS)) {
            this.wsLogger
               .warn(
                  "DISCONNECT",
                  String.format(
                     "Executor (%s) did not terminate in time",
                     name
                  )
               );
         }
      } catch (InterruptedException var4) {
         Thread.currentThread().interrupt();
         this.wsLogger
            .error(
               "DISCONNECT",
               String.format(
                  "Interrupted while shutting down %s executor",
                  name
               ),
               var4
            );
      }
   }

   private String describeThrowable(Throwable throwable) {
      if (throwable == null) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(-46);
         var10000[1] = (byte)(-12);
         var10000[2] = (byte)(119);
         var10000[3] = (byte)(-80);
         var10000[4] = (byte)(13);
         var10000[5] = (byte)(-38);
         var10000[6] = (byte)(-27);
         return $$v_s(var10000, -2988010967076188030L);
      } else {
         String var2 = throwable.getMessage();
         return var2 != null && !var2.isBlank() ? var2 : throwable.getClass().getSimpleName();
      }
   }

   private static int extractHandshakeStatus(Throwable throwable) {
      for (Throwable var1 = throwable; var1 != null; var1 = var1.getCause()) {
         if (var1 instanceof WebSocketHandshakeException var2) {
            try {
               if (var2.getResponse() != null) {
                  return var2.getResponse().statusCode();
               }
            } catch (Exception var4) {
               return -1;
            }
         }
      }

      return -1;
   }

   private static boolean isFatalHandshakeStatus(int statusCode) {
      return statusCode == (401) || statusCode == (403) || statusCode == (404) || statusCode == (426);
   }

   public boolean isConnected() {
      return this.connected;
   }

   public boolean isConnecting() {
      return this.connecting;
   }

   public boolean isReconnectEnabled() {
      return this.reconnectEnabled;
   }

   public int getReconnectAttempts() {
      return this.reconnectAttempts.get();
   }

   public long getConnectionDuration() {
      return this.connected && this.connectionStartTime != 0L ? System.currentTimeMillis() - this.connectionStartTime : 0L;
   }

   public String getServerUrl() {
      return this.serverUrl;
   }

   public Path getLogFilePath() {
      return this.wsLogger.getCurrentLogFile();
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

   private final class ClientListener implements Listener {
      private final AtomicBoolean disconnectHandled = new AtomicBoolean(false);
      private final StringBuilder textBuffer = new StringBuilder();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      @Override
      public void onOpen(WebSocket webSocket) {
         VieWebSocketClient.this.wsClient = webSocket;
         VieWebSocketClient.this.connected = true;
         VieWebSocketClient.this.connecting = false;
         VieWebSocketClient.this.reconnectAttempts.set(0);
         VieWebSocketClient.this.connectionStartTime = System.currentTimeMillis();
         WebSocketLogger var10000 = VieWebSocketClient.this.wsLogger;
         byte[] var10001 = new byte[4];
         var10001[0] = (byte)(115);
         var10001[1] = (byte)(-54);
         var10001[2] = (byte)(89);
         var10001[3] = (byte)(-112);
         String var2 = $$v_s(var10001, -3072980929317973991L);
         byte[] var10003 = new byte[35];
         var10003[0] = (byte)(127);
         var10003[1] = (byte)(-11);
         var10003[2] = (byte)(114);
         var10003[3] = (byte)(-80);
         var10003[4] = (byte)(-4);
         var10003[5] = (byte)(41);
         var10003[6] = (byte)(45);
         var10003[7] = (byte)(-108);
         var10003[8] = (byte)(59);
         var10003[9] = (byte)(-52);
         var10003[10] = (byte)(-60);
         var10003[11] = (byte)(-109);
         var10003[12] = (byte)(-78);
         var10003[13] = (byte)(-26);
         var10003[14] = (byte)(16);
         var10003[15] = (byte)(-25);
         var10003[16] = 0;
         var10003[17] = (byte)(35);
         var10003[18] = (byte)(-65);
         var10003[19] = (byte)(-58);
         var10003[20] = (byte)(76);
         var10003[21] = (byte)(-34);
         var10003[22] = (byte)(41);
         var10003[23] = (byte)(-34);
         var10003[24] = (byte)(-15);
         var10003[25] = (byte)(113);
         var10003[26] = (byte)(-9);
         var10003[27] = (byte)(35);
         var10003[28] = (byte)(98);
         var10003[29] = (byte)(-79);
         var10003[30] = (byte)(71);
         var10003[31] = 0;
         var10003[32] = (byte)(-80);
         var10003[33] = (byte)(86);
         var10003[34] = (byte)(5);
         var10000.connection(var2, VieWebSocketClient.this.serverUrl, $$v_s(var10003, -3072980929317973991L));
         VieWebSocketClient.this.authenticate();
         VieWebSocketClient.this.startPing();
         webSocket.request(1L);
      }

      @Override
      public CompletionStage onText(WebSocket webSocket, CharSequence data, boolean last) {
         String var4 = null;
         synchronized (this.textBuffer) {
            this.textBuffer.append(data);
            if (last) {
               var4 = this.textBuffer.toString();
               this.textBuffer.setLength(0);
            }
         }

         if (var4 != null) {
            WebSocketLogger var10000 = VieWebSocketClient.this.wsLogger;
            byte[] var10001 = new byte[4];
            var10001[0] = (byte)(110);
            var10001[1] = (byte)(-33);
            var10001[2] = (byte)(95);
            var10001[3] = (byte)(-120);
            String var8 = $$v_s(var10001, -3072980929317973991L);
            byte[] var10002 = new byte[3];
            var10002[0] = (byte)(78);
            var10002[1] = (byte)(-5);
            var10002[2] = (byte)(107);
            var10000.message(var8, $$v_s(var10002, -3072980929317973991L), var4);
            VieWebSocketClient.this.handleMessage(var4);
         }

         webSocket.request(1L);
         return CompletableFuture.completedFuture(null);
      }

      @Override
      public CompletionStage onBinary(WebSocket webSocket, ByteBuffer data, boolean last) {
         webSocket.request(1L);
         return CompletableFuture.completedFuture(null);
      }

      @Override
      public CompletionStage onClose(WebSocket webSocket, int statusCode, String reason) {
         VieWebSocketClient.this.handleDisconnect(statusCode, reason, !VieWebSocketClient.this.closingByClient, this.disconnectHandled);
         return CompletableFuture.completedFuture(null);
      }

      @Override
      public void onError(WebSocket webSocket, Throwable error) {
         WebSocketLogger var10000 = VieWebSocketClient.this.wsLogger;
         byte[] var10001 = new byte[5];
         var10001[0] = (byte)(121);
         var10001[1] = (byte)(-56);
         var10001[2] = (byte)(78);
         var10001[3] = (byte)(-111);
         var10001[4] = (byte)(-53);
         String var3 = $$v_s(var10001, -3072980929317973991L);
         byte[] var10002 = new byte[19];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-1);
         var10002[2] = (byte)(126);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(-10);
         var10002[5] = (byte)(41);
         var10002[6] = (byte)(50);
         var10002[7] = (byte)(-104);
         var10002[8] = (byte)(32);
         var10002[9] = (byte)(-126);
         var10002[10] = (byte)(-127);
         var10002[11] = (byte)(-124);
         var10002[12] = (byte)(-77);
         var10002[13] = (byte)(-3);
         var10002[14] = (byte)(3);
         var10002[15] = (byte)(-65);
         var10002[16] = (byte)(76);
         var10002[17] = (byte)(111);
         var10002[18] = (byte)(-65);
         var10000.error(var3, String.format($$v_s(var10002, -3072980929317973991L), error.getMessage()), error);
         var10001 = new byte[21];
         var10001[0] = (byte)(103);
         var10001[1] = (byte)(-51);
         var10001[2] = (byte)(121);
         var10001[3] = (byte)(-68);
         var10001[4] = (byte)(-54);
         var10001[5] = (byte)(37);
         var10001[6] = (byte)(58);
         var10001[7] = (byte)(-106);
         var10001[8] = (byte)(49);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-71);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(-124);
         var10001[13] = (byte)(-32);
         var10001[14] = (byte)(3);
         var10001[15] = (byte)(-22);
         var10001[16] = (byte)(30);
         var10001[17] = (byte)(112);
         var10001[18] = (byte)(-20);
         var10001[19] = (byte)(-43);
         var10001[20] = (byte)(84);
         VieWebSocketClient.LOGGER.error($$v_s(var10001, -3072980929317973991L), error.getMessage(), error);
         VieWebSocketClient.this.handleDisconnect(
            -1,
            "error: " + VieWebSocketClient.this.describeThrowable(error),
            !VieWebSocketClient.this.closingByClient,
            this.disconnectHandled
         );
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

   public interface MessageHandler {
      void onMessage(String var1);

      void onKeyExpired(String var1);

      void onKeyExpiringSoon(String var1, int var2);

      void onKeyRevoked(String var1, String var2);

      void onKeyUnrevoked(String var1);

      void onKeyExtended(String var1, String var2);

      void onHwidReset(String var1);

      void onKeyDeleted(String var1);

      void onForceUpdate(String var1, String var2);
   }
}
