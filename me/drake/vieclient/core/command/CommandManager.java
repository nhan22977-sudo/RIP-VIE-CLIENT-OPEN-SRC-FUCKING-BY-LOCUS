package me.drake.vieclient.core.command;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.auth.AuthManager;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.KeybindSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.gui.nvg.ui.NewClickGUI;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.modules.client.FakeRoles;
import me.drake.vieclient.modules.client.NameProtect;
import me.drake.vieclient.modules.donut.StaffLists;
import net.minecraft.class_2561;
import net.minecraft.class_310;

public class CommandManager {
   private static final class_310 mc = class_310.method_1551();
   private final List commands = new ArrayList();
   public static final String PREFIX = ".";
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public CommandManager() {
      this.registerCommands();
      byte[] var10001 = new byte[35];
      var10001[0] = (byte)(84);
      var10001[1] = (byte)(-52);
      var10001[2] = (byte)(117);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(77);
      var10001[5] = (byte)(-18);
      var10001[6] = (byte)(-57);
      var10001[7] = (byte)(-33);
      var10001[8] = (byte)(9);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-71);
      var10001[11] = (byte)(-42);
      var10001[12] = (byte)(4);
      var10001[13] = (byte)(63);
      var10001[14] = (byte)(-31);
      var10001[15] = (byte)(-85);
      var10001[16] = (byte)(44);
      var10001[17] = (byte)(62);
      var10001[18] = (byte)(-87);
      var10001[19] = (byte)(-36);
      var10001[20] = (byte)(-37);
      var10001[21] = (byte)(22);
      var10001[22] = (byte)(-34);
      var10001[23] = (byte)(-111);
      var10001[24] = (byte)(-54);
      var10001[25] = (byte)(50);
      var10001[26] = (byte)(-9);
      var10001[27] = (byte)(41);
      var10001[28] = (byte)(-21);
      var10001[29] = (byte)(103);
      var10001[30] = (byte)(-73);
      var10001[31] = (byte)(92);
      var10001[32] = (byte)(-117);
      var10001[33] = (byte)(73);
      var10001[34] = (byte)(82);
      VieClientMod.LOGGER.info($$v_s(var10001, -7877538230903254998L), this.commands.size());
   }

   private void registerCommands() {
      this.register(
         new Command(
            "vc",
            "Show client info",
            ".vc"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               this.info(
                  "Vie Client v1.0.0 by Drake"
               );
               this.info("Modules: " + VieClientMod.getInstance().getModuleManager().getModules().size());
               this.info("Enabled: " + VieClientMod.getInstance().getModuleManager().getEnabled().size());
               AuthManager var2 = VieClientMod.getInstance().getAuthManager();
               if (var2 != null) {
                  AuthManager.StatusSnapshot var3 = var2.getStatusSnapshot();
                  this.info(
                     "Auth: "
                        + (
                           var3.authorized()
                              ? "§aVALID"
                              : "§c" + var3.status().toUpperCase()
                        )
                  );
               } else {
                  this.info("Auth: §7DISABLED");
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "auth",
            "Check license auth status",
            ".auth <check/status>"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               AuthManager var2 = VieClientMod.getInstance().getAuthManager();
               if (var2 == null) {
                  this.info(
                     "Auth is disabled in this build."
                  );
               } else if (args.length < 2) {
                  this.error(
                     "Usage: .auth check | .auth status"
                  );
               } else {
                  switch (args[1].toLowerCase()) {
                     case "check":
                        AuthManager.StatusSnapshot var5 = var2.validateStoredKey();
                        this.printAuthSnapshot(var5);
                        break;
                     case "status":
                        this.printAuthSnapshot(var2.getStatusSnapshot());
                        break;
                     case "set":
                     case "clear":
                        this.error(
                           "Manual key command is disabled. Manage access via Discord redeem/download + HWID reset."
                        );
                        break;
                     default:
                        this.error("Unknown sub-command: " + args[1]);
                  }
               }
            }

            private void printAuthSnapshot(AuthManager.StatusSnapshot snapshot) {
               String var2 = snapshot.authorized()
                  ? "§a"
                  : "§c";
               this.info("Auth: " + var2 + snapshot.status().toUpperCase());
               this.info("Server: " + snapshot.apiBaseUrl());
               this.info("Key: " + this.maskKey(snapshot.keyCode()));
               this.info("HWID: " + this.shortHwid(snapshot.hwid()));
               if (snapshot.expiresAt() != null) {
                  this.info("Expires: " + snapshot.expiresAt());
               } else {
                  this.info(
                     "Expires: Lifetime/Unknown"
                  );
               }

               this.info("Message: " + snapshot.message());
               if (!snapshot.authorized()
                  && "server_error".equalsIgnoreCase(snapshot.status())) {
                  this.info(
                     "Hint: check auth API URL and /health endpoint."
                  );
               }
            }

            private String maskKey(String key) {
               if (key != null && !key.isBlank()) {
                  return key.length() <= (10)
                     ? key
                     : key.substring(0, 6) + "..." + key.substring(key.length() - (4));
               }

               byte[] var10000 = new byte[6];
               var10000[0] = (byte)(29);
               var10000[1] = (byte)(-12);
               var10000[2] = (byte)(115);
               var10000[3] = (byte)(-80);
               var10000[4] = (byte)(-71);
               var10000[5] = (byte)(37);
               return $$v_s(var10000, 2939674514272014352L);
            }

            private String shortHwid(String hwid) {
               if (hwid != null && !hwid.isBlank()) {
                  return hwid.length() <= (16) ? hwid : hwid.substring(0, 16) + "...";
               }

               byte[] var10000 = new byte[9];
               var10000[0] = (byte)(29);
               var10000[1] = (byte)(-17);
               var10000[2] = (byte)(114);
               var10000[3] = (byte)(-75);
               var10000[4] = (byte)(-78);
               var10000[5] = (byte)(99);
               var10000[6] = (byte)(-65);
               var10000[7] = (byte)(110);
               var10000[8] = (byte)(116);
               return $$v_s(var10000, 2939674514272014352L);
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "toggle",
            "Toggle a module",
            ".toggle <module>",
            "t"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (args.length < (2)) {
                  byte[] var3 = new byte[23];
                  var3[0] = (byte)(109);
                  var3[1] = (byte)(-23);
                  var3[2] = (byte)(125);
                  var3[3] = (byte)(-71);
                  var3[4] = (byte)(-70);
                  var3[5] = (byte)(54);
                  var3[6] = (byte)(-24);
                  var3[7] = (byte)(46);
                  var3[8] = (byte)(36);
                  var3[9] = (byte)(-51);
                  var3[10] = (byte)(-125);
                  var3[11] = (byte)(-111);
                  var3[12] = (byte)(-21);
                  var3[13] = (byte)(-79);
                  var3[14] = (byte)(-64);
                  var3[15] = (byte)(68);
                  var3[16] = (byte)(5);
                  var3[17] = (byte)(37);
                  var3[18] = (byte)(-88);
                  var3[19] = (byte)(-37);
                  var3[20] = (byte)(3);
                  var3[21] = (byte)(-103);
                  var3[22] = (byte)(-90);
                  this.error($$v_s(var3, 2939674518566981661L));
               } else {
                  Module var2 = VieClientMod.getInstance().getModuleManager().getModuleByName(args[1]);
                  if (var2 == null) {
                     this.error("Module not found: " + args[1]);
                  } else {
                     var2.toggle();
                     String var10001 = var2.getName();
                     String var4;
                     if (var2.isEnabled()) {
                        byte[] var10002 = new byte[5];
                        var10002[0] = (byte)(-6);
                        var10002[1] = (byte)(61);
                        var10002[2] = (byte)(125);
                        var10002[3] = (byte)(-111);
                        var10002[4] = (byte)(-111);
                        var4 = $$v_s(var10002, 2939674518566981661L);
                     } else {
                        byte[] var5 = new byte[6];
                        var5[0] = (byte)(-6);
                        var5[1] = (byte)(61);
                        var5[2] = (byte)(127);
                        var5[3] = (byte)(-111);
                        var5[4] = (byte)(-103);
                        var5[5] = (byte)(74);
                        var4 = $$v_s(var5, 2939674518566981661L);
                     }

                     this.info(var10001 + " -> " + var4);
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "bind",
            "Set module keybind",
            ".bind <module> <key>",
            "b"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (args.length < 3) {
                  this.error(
                     "Usage: .bind <module> <key>"
                  );
               } else {
                  String var2 = args[1].trim();
                  int var3 = KeybindSetting.parseKey(args[2]);
                  if (var3 == Integer.MIN_VALUE) {
                     this.error("Invalid key: " + args[2]);
                  } else if (!var2.equalsIgnoreCase("clickgui")
                     && !var2.equalsIgnoreCase("gui")
                     && !var2.equalsIgnoreCase("menu")
                     && !var2.equalsIgnoreCase("vieclient")) {
                     Module var6 = VieClientMod.getInstance().getModuleManager().getModuleByName(var2);
                     if (var6 == null) {
                        this.error("Module not found: " + var2);
                     } else if (var6 instanceof ClientSettings var5) {
                        var5.setClickGuiKey(var3);
                        this.info("Bound ClickGUI to key " + KeybindSetting.keyCodeToName(var3));
                     } else {
                        var6.getKeybind().set(var3);
                        this.info("Bound " + var6.getName() + " to key " + KeybindSetting.keyCodeToName(var3));
                     }
                  } else {
                     ClientSettings var4 = ClientSettings.get();
                     if (var4 != null) {
                        var4.setClickGuiKey(var3);
                        this.info("Bound ClickGUI to key " + KeybindSetting.keyCodeToName(var3));
                     }
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "set",
            "Change a setting value",
            ".set <module> <setting> <value>",
            "s"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (args.length < (4)) {
                  byte[] var10001 = new byte[38];
                  var10001[0] = (byte)(111);
                  var10001[1] = (byte)(-23);
                  var10001[2] = (byte)(125);
                  var10001[3] = (byte)(-71);
                  var10001[4] = (byte)(-68);
                  var10001[5] = (byte)(54);
                  var10001[6] = (byte)(-24);
                  var10001[7] = (byte)(46);
                  var10001[8] = (byte)(33);
                  var10001[9] = (byte)(-57);
                  var10001[10] = (byte)(-112);
                  var10001[11] = (byte)(-42);
                  var10001[12] = (byte)(-67);
                  var10001[13] = (byte)(-71);
                  var10001[14] = (byte)(-113);
                  var10001[15] = (byte)(28);
                  var10001[16] = (byte)(31);
                  var10001[17] = (byte)(38);
                  var10001[18] = (byte)(-87);
                  var10001[19] = (byte)(-112);
                  var10001[20] = (byte)(73);
                  var10001[21] = (byte)(-64);
                  var10001[22] = (byte)(-21);
                  var10001[23] = (byte)(53);
                  var10001[24] = (byte)(-10);
                  var10001[25] = (byte)(102);
                  var10001[26] = (byte)(-3);
                  var10001[27] = (byte)(40);
                  var10001[28] = (byte)(54);
                  var10001[29] = (byte)(-70);
                  var10001[30] = (byte)(-112);
                  var10001[31] = (byte)(-76);
                  var10001[32] = (byte)(-84);
                  var10001[33] = (byte)(91);
                  var10001[34] = (byte)(16);
                  var10001[35] = (byte)(11);
                  var10001[36] = (byte)(92);
                  var10001[37] = (byte)(-110);
                  this.error($$v_s(var10001, 2939674492797177887L));
               } else {
                  Module var2 = VieClientMod.getInstance().getModuleManager().getModuleByName(args[1]);
                  if (var2 == null) {
                     this.error("Module not found: " + args[1]);
                  } else {
                     Setting var3 = null;

                     for (Setting var5 : var2.getSettings()) {
                        if (var5.getName().equalsIgnoreCase(args[2])) {
                           var3 = var5;
                           break;
                        }
                     }

                     if (var3 == null) {
                        this.error("Setting not found: " + args[2]);
                     } else {
                        CommandManager.this.setSettingValue(var3, args[3]);
                        this.info(var2.getName() + "." + var3.getName() + " = " + var3.get());
                     }
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "modules",
            "List modules",
            ".modules [category]",
            "mods"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (args.length >= (2)) {
                  try {
                     Category var2 = Category.valueOf(args[1].toUpperCase());

                     for (Module var4 : VieClientMod.getInstance().getModuleManager().getModules(var2)) {
                        String var12;
                        if (var4.isEnabled()) {
                           byte[] var10001 = new byte[3];
                           var10001[0] = (byte)(-3);
                           var10001[1] = (byte)(61);
                           var10001[2] = (byte)(125);
                           var12 = $$v_s(var10001, 2939674497092145178L);
                        } else {
                           byte[] var13 = new byte[3];
                           var13[0] = (byte)(-3);
                           var13[1] = (byte)(61);
                           var13[2] = (byte)(43);
                           var12 = $$v_s(var13, 2939674497092145178L);
                        }

                        this.info(var12 + var4.getName());
                     }
                  } catch (IllegalArgumentException var8) {
                     this.error("Unknown category: " + args[1]);
                  }
               } else {
                  for (Category var5 : Category.values()) {
                     this.info("§d--- " + var5.getDisplayName() + " ---");

                     for (Module var7 : VieClientMod.getInstance().getModuleManager().getModules(var5)) {
                        String var15;
                        if (var7.isEnabled()) {
                           byte[] var14 = new byte[3];
                           var14[0] = (byte)(-3);
                           var14[1] = (byte)(61);
                           var14[2] = (byte)(125);
                           var15 = $$v_s(var14, 2939674497092145178L);
                        } else {
                           byte[] var16 = new byte[3];
                           var16[0] = (byte)(-3);
                           var16[1] = (byte)(61);
                           var16[2] = (byte)(43);
                           var15 = $$v_s(var16, 2939674497092145178L);
                        }

                        this.info(var15 + "  " + var7.getName());
                     }
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "friends",
            "Manage friend list",
            ".friends add/remove/list <name>",
            "f"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (args.length < 2) {
                  this.error(
                     "Usage: .friends add/remove/list <name>"
                  );
               } else {
                  CoreEntry.FriendManager var2 = VieClientMod.getInstance().getFriendManager();
                  switch (args[1].toLowerCase()) {
                     case "add":
                        if (args.length < 3) {
                           this.error(
                              "Usage: .friends add <name>"
                           );
                           return;
                        }

                        var2.add(args[2]);
                        this.info("Added friend: " + args[2]);
                        break;
                     case "remove":
                        if (args.length < 3) {
                           this.error(
                              "Usage: .friends remove <name>"
                           );
                           return;
                        }

                        var2.remove(args[2]);
                        this.info("Removed friend: " + args[2]);
                        break;
                     case "list":
                        List var5 = var2.getFriends();
                        if (var5.isEmpty()) {
                           this.info("No friends added.");
                           return;
                        }

                        this.info("Friends: " + String.join(", ", var5));
                        break;
                     default:
                        this.error("Unknown sub-command: " + args[1]);
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "protect",
            "Manage protected name list",
            ".protect add/remove/list/clear <name>",
            "p"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (args.length < 2) {
                  this.info(
                     ".protect add <name>   — hide a name"
                  );
                  this.info(
                     ".protect remove <name> — unhide a name"
                  );
                  this.info(
                     ".protect list          — show all hidden names"
                  );
                  this.info(
                     ".protect clear         — clear all"
                  );
               } else {
                  NameProtect var2 = NameProtect.get();
                  switch (args[1].toLowerCase()) {
                     case "add":
                        if (args.length < 3) {
                           this.error(
                              "Usage: .protect add <name>"
                           );
                           return;
                        }

                        String var15 = args[2].trim();
                        if (var15.isBlank()) {
                           this.error(
                              "Name cannot be blank."
                           );
                           return;
                        }

                        String[] var17 = var2.getProtectedNames();

                        for (String var25 : var17) {
                           if (var25.equalsIgnoreCase(var15)) {
                              this.error("Name already protected: " + var15);
                              return;
                           }
                        }

                        String[] var20 = new String[var17.length + 1];
                        System.arraycopy(var17, 0, var20, 0, var17.length);
                        var20[var17.length] = var15.toLowerCase();
                        var2.setProtectedNames(var20);
                        this.info("Added to protection: " + var15);
                        break;
                     case "remove":
                        if (args.length < 3) {
                           this.error(
                              "Usage: .protect remove <name>"
                           );
                           return;
                        }

                        String var14 = args[2].trim().toLowerCase();
                        String[] var16 = var2.getProtectedNames();
                        String[] var18 = new String[Math.max(0, var16.length - 1)];
                        int var21 = 0;
                        boolean var23 = false;

                        for (String var13 : var16) {
                           if (!var13.equalsIgnoreCase(var14)) {
                              var18[var21++] = var13;
                           } else {
                              var23 = true;
                           }
                        }

                        if (!var23) {
                           this.error("Name not in protection list: " + var14);
                           return;
                        }

                        var2.setProtectedNames(var18);
                        this.info("Removed from protection: " + var14);
                        break;
                     case "list":
                        String[] var5 = var2.getProtectedNames();
                        if (var5.length == 0) {
                           this.info(
                              "No names protected."
                           );
                           return;
                        }

                        this.info(
                           "§d--- Protected Names ---"
                        );

                        for (String var9 : var5) {
                           this.info("§7  " + var9);
                        }

                        this.info("Total: " + var5.length);
                        break;
                     case "clear":
                        var2.setProtectedNames(new String[0]);
                        this.info(
                           "Protection list cleared."
                        );
                        break;
                     default:
                        this.error("Unknown sub-command: " + args[1] + " (add/remove/list/clear)");
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "staff",
            "Manage custom staff list for StaffLists module",
            ".staff add/remove/list/clear <name>",
            "st"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (args.length < 2) {
                  this.info(
                     ".staff add <name>    — Add player to staff list"
                  );
                  this.info(
                     ".staff remove <name> — Remove player from staff list"
                  );
                  this.info(
                     ".staff list          — List custom staff members"
                  );
                  this.info(
                     ".staff clear         — Clear all custom staff"
                  );
               } else {
                  switch (args[1].toLowerCase()) {
                     case "add":
                        if (args.length < 3) {
                           this.error(
                              "Usage: .staff add <name>"
                           );
                           return;
                        }

                        String var6 = args[2].toLowerCase(Locale.ROOT);
                        StaffLists.DYNAMIC_STAFF.add(var6);
                        this.info("Added " + args[2] + " to Staff list.");
                        break;
                     case "remove":
                        if (args.length < 3) {
                           this.error(
                              "Usage: .staff remove <name>"
                           );
                           return;
                        }

                        String var5 = args[2].toLowerCase(Locale.ROOT);
                        StaffLists.DYNAMIC_STAFF.remove(var5);
                        this.info("Removed " + args[2] + " from Staff list.");
                        break;
                     case "list":
                        Set var4 = StaffLists.DYNAMIC_STAFF;
                        if (var4.isEmpty()) {
                           this.info(
                              "No custom staff added. (Default list and auto rank tags still active)"
                           );
                           return;
                        }

                        this.info("Custom Staff: " + String.join(", ", var4));
                        break;
                     case "clear":
                        StaffLists.DYNAMIC_STAFF.clear();
                        this.info(
                           "Cleared all custom staff."
                        );
                        break;
                     default:
                        this.error("Unknown sub-command: " + args[1] + " (add/remove/list/clear)");
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "help",
            "Show help",
            ".help [command]",
            "h"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (args.length >= (2)) {
                  for (Command var3 : CommandManager.this.commands) {
                     if (var3.matches(args[1])) {
                        this.info(var3.getName() + " — " + var3.getDescription());
                        this.info("Syntax: " + var3.getSyntax());
                        return;
                     }
                  }

                  this.error("Unknown command: " + args[1]);
               } else {
                  byte[] var10001 = new byte[30];
                  var10001[0] = (byte)(-8);
                  var10001[1] = (byte)(61);
                  var10001[2] = (byte)(120);
                  var10001[3] = (byte)(-13);
                  var10001[4] = (byte)(48);
                  var10001[5] = (byte)(111);
                  var10001[6] = (byte)(-87);
                  var10001[7] = (byte)(45);
                  var10001[8] = (byte)(59);
                  var10001[9] = (byte)(-57);
                  var10001[10] = (byte)(-60);
                  var10001[11] = (byte)(-75);
                  var10001[12] = (byte)(41);
                  var10001[13] = (byte)(-13);
                  var10001[14] = (byte)(-60);
                  var10001[15] = (byte)(109);
                  var10001[16] = (byte)(30);
                  var10001[17] = (byte)(106);
                  var10001[18] = (byte)(-113);
                  var10001[19] = (byte)(-63);
                  var10001[20] = (byte)(-64);
                  var10001[21] = (byte)(-33);
                  var10001[22] = (byte)(-72);
                  var10001[23] = (byte)(69);
                  var10001[24] = (byte)(-26);
                  var10001[25] = (byte)(97);
                  var10001[26] = (byte)(-76);
                  var10001[27] = (byte)(107);
                  var10001[28] = (byte)(-72);
                  var10001[29] = (byte)(-25);
                  this.info($$v_s(var10001, 6019796081371762719L));

                  for (Command var5 : CommandManager.this.commands) {
                     this.info("§f" + var5.getSyntax() + " §7— " + var5.getDescription());
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "say",
            "Send real chat message",
            ".say <message>"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (args.length < (2)) {
                  byte[] var10001 = new byte[21];
                  var10001[0] = (byte)(111);
                  var10001[1] = (byte)(-23);
                  var10001[2] = (byte)(125);
                  var10001[3] = (byte)(-71);
                  var10001[4] = (byte)(121);
                  var10001[5] = (byte)(120);
                  var10001[6] = (byte)(-87);
                  var10001[7] = (byte)(85);
                  var10001[8] = (byte)(33);
                  var10001[9] = (byte)(-61);
                  var10001[10] = (byte)(-99);
                  var10001[11] = (byte)(-42);
                  var10001[12] = (byte)(120);
                  var10001[13] = (byte)(-9);
                  var10001[14] = (byte)(-60);
                  var10001[15] = (byte)(112);
                  var10001[16] = (byte)(25);
                  var10001[17] = (byte)(43);
                  var10001[18] = (byte)(-85);
                  var10001[19] = (byte)(-53);
                  var10001[20] = (byte)(-110);
                  this.error($$v_s(var10001, 6019796085666730015L));
               } else {
                  String var2 = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
                  if (mc.field_1724 != null) {
                     mc.field_1724.field_3944.method_45729(var2);
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "gui",
            "Open ClickGUI",
            ".gui",
            "clickgui",
            "cgui"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               if (mc != null) {
                  if (mc.field_1724 != null && mc.field_1687 != null) {
                     mc.method_1507(new NewClickGUI());
                     byte[] var2 = new byte[19];
                     var2[0] = (byte)(118);
                     var2[1] = (byte)(-22);
                     var2[2] = (byte)(121);
                     var2[3] = (byte)(-80);
                     var2[4] = (byte)(118);
                     var2[5] = (byte)(44);
                     var2[6] = (byte)(-18);
                     var2[7] = (byte)(91);
                     var2[8] = (byte)(18);
                     var2[9] = (byte)(-50);
                     var2[10] = (byte)(-115);
                     var2[11] = (byte)(-107);
                     var2[12] = (byte)(44);
                     var2[13] = (byte)(-35);
                     var2[14] = (byte)(-12);
                     var2[15] = (byte)(74);
                     var2[16] = (byte)(71);
                     var2[17] = (byte)(100);
                     var2[18] = (byte)(-30);
                     this.info($$v_s(var2, 6019796089961697308L));
                  } else {
                     byte[] var10001 = new byte[19];
                     var10001[0] = (byte)(115);
                     var10001[1] = (byte)(-11);
                     var10001[2] = (byte)(117);
                     var10001[3] = (byte)(-80);
                     var10001[4] = (byte)(63);
                     var10001[5] = (byte)(35);
                     var10001[6] = (byte)(-87);
                     var10001[7] = (byte)(12);
                     var10001[8] = (byte)(62);
                     var10001[9] = (byte)(-48);
                     var10001[10] = (byte)(-120);
                     var10001[11] = (byte)(-110);
                     var10001[12] = (byte)(103);
                     var10001[13] = (byte)(-4);
                     var10001[14] = (byte)(-56);
                     var10001[15] = (byte)(113);
                     var10001[16] = (byte)(26);
                     var10001[17] = (byte)(62);
                     var10001[18] = (byte)(-30);
                     this.error($$v_s(var10001, 6019796089961697308L));
                  }
               }
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "promote",
            "Set fake role (MEDIA)",
            ".promote [media|vip|owner|admin]"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               FakeRoles.setFakeRole(FakeRoles.Role.MEDIA);
               byte[] var10001 = new byte[33];
               var10001[0] = (byte)(-8);
               var10001[1] = (byte)(61);
               var10001[2] = (byte)(125);
               var10001[3] = (byte)(-104);
               var10001[4] = (byte)(127);
               var10001[5] = (byte)(41);
               var10001[6] = (byte)(-20);
               var10001[7] = (byte)(41);
               var10001[8] = (byte)(61);
               var10001[9] = (byte)(-50);
               var10001[10] = (byte)(-127);
               var10001[11] = (byte)(-42);
               var10001[12] = (byte)(53);
               var10001[13] = (byte)(-1);
               var10001[14] = (byte)(-43);
               var10001[15] = (byte)(35);
               var10001[16] = (byte)(30);
               var10001[17] = (byte)(37);
               var10001[18] = (byte)(-20);
               var10001[19] = (byte)(108);
               var10001[20] = (byte)(9);
               var10001[21] = (byte)(-42);
               var10001[22] = (byte)(-126);
               var10001[23] = (byte)(102);
               var10001[24] = (byte)(-57);
               var10001[25] = (byte)(86);
               var10001[26] = (byte)(-35);
               var10001[27] = (byte)(7);
               var10001[28] = (byte)(-53);
               var10001[29] = (byte)(8);
               var10001[30] = (byte)(86);
               var10001[31] = (byte)(-110);
               var10001[32] = (byte)(-5);
               this.info($$v_s(var10001, 6019796094256664607L));
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
      this.register(
         new Command(
            "demote",
            "Remove active fake role",
            ".demote"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               FakeRoles.disableFakeRole();
               byte[] var10001 = new byte[22];
               var10001[0] = (byte)(-8);
               var10001[1] = (byte)(61);
               var10001[2] = (byte)(127);
               var10001[3] = (byte)(-104);
               var10001[4] = (byte)(120);
               var10001[5] = (byte)(41);
               var10001[6] = (byte)(-20);
               var10001[7] = (byte)(91);
               var10001[8] = (byte)(32);
               var10001[9] = (byte)(-51);
               var10001[10] = (byte)(-120);
               var10001[11] = (byte)(-109);
               var10001[12] = (byte)(97);
               var10001[13] = (byte)(-2);
               var10001[14] = (byte)(-56);
               var10001[15] = (byte)(112);
               var10001[16] = (byte)(11);
               var10001[17] = (byte)(40);
               var10001[18] = (byte)(-96);
               var10001[19] = (byte)(-53);
               var10001[20] = (byte)(-51);
               var10001[21] = (byte)(-100);
               this.info($$v_s(var10001, 6019796064191893535L));
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
      );
      this.register(
         new Command(
            "media",
            "View Media status and creators info",
            ".media [player]"
         ) {
            // $VF: synthetic field
            private static final boolean $$vie_p = true;

            @Override
            public void execute(String[] args) {
               this.info(
                  "§d--- Vie Client Media System ---"
               );
               this.info(
                  "FakeRoles: "
                     + (
                        FakeRoles.isActive()
                           ? "§aENABLED"
                           : "§cDISABLED"
                     )
               );
               FakeRoles.Role var2 = FakeRoles.getSelectedRole();
               this.info("Current Role: §f" + (var2 != null ? var2.name() : "None"));
               this.info(
                  "Effective Prefix: §f"
                     + (FakeRoles.isActive() ? FakeRoles.getEffectivePrefix() : "(none)")
               );
               this.info(
                  "Commands: §e.promote [trial|official] §7| §e.demote"
               );
            }

            // $VF: synthetic method
            private static String $$v_s(byte[] var0, long var1) {
               byte[] var3 = new byte[var0.length];

               for (int var4 = 0; var4 < var0.length; var4++) {
                  var3[var4] = (byte)(
                     var0[var4]
                        ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                        ^ (var4 + 1) * (37)
                  );
               }

               return new String(var3, StandardCharsets.UTF_8);
            }
         }
      );
   }

   public boolean dispatch(String message) {
      if (!message.startsWith(".")) {
         return false;
      }

      String var2 = message.substring(".".length()).trim();
      if (var2.isEmpty()) {
         return false;
      }

      byte[] var10001 = new byte[3];
      var10001[0] = (byte)(83);
      var10001[1] = (byte)(-23);
      var10001[2] = (byte)(55);
      String[] var3 = var2.split($$v_s(var10001, -7877538230903254998L));

      for (Command var5 : this.commands) {
         if (var5.matches(var3[0])) {
            var5.execute(var3);
            return true;
         }
      }

      if (mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470("§d[VC]§c Unknown command: " + var3[0] + ". Type .help"), false);
      }

      return true;
   }

   private void register(Command command) {
      this.commands.add(command);
   }

   private void setSettingValue(Setting setting, String value) {
      Object var3 = setting.get();
      if (var3 instanceof Boolean) {
         setting.set(Boolean.parseBoolean(value));
      } else if (var3 instanceof Integer) {
         setting.set(Integer.parseInt(value));
      } else if (var3 instanceof Double) {
         setting.set(Double.parseDouble(value));
      } else if (var3 instanceof Float) {
         setting.set(Float.parseFloat(value));
      } else if (var3 instanceof String) {
         setting.set(value);
      } else if (setting instanceof EnumSetting var4) {
         for (Enum var8 : var4.getValues()) {
            if (var8.name().equalsIgnoreCase(value)) {
               var4.set(var8);
               return;
            }
         }

         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(75);
         var10000[1] = (byte)(-33);
         var10000[2] = (byte)(90);
         var10000[3] = (byte)(-97);
         var10000[4] = (byte)(91);
         var10000[5] = (byte)(-50);
         var10000[6] = (byte)(-6);
         if ($$v_s(var10000, -7877538230903254998L).equalsIgnoreCase(value) && var4.getValues().length > 0) {
            var4.set(var4.getValues()[0]);
         }
      }
   }

   public List getCommands() {
      return this.commands;
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
