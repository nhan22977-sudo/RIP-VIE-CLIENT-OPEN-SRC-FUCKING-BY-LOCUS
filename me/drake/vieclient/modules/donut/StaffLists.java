package me.drake.vieclient.modules.donut;

import com.mojang.authlib.GameProfile;
import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.BackdropPrepareEvent;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.gui.nvg.ui.components.GuiStyle;
import me.drake.vieclient.gui.nvg.ui.components.ThemeManager;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.render.FrostedBackground;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.MaterialFX;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.render.CordNotification;
import me.drake.vieclient.util.render.ToastManager;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_5251;
import net.minecraft.class_640;

public class StaffLists extends Module {
   private static final int MOD_COLOR = -7542374;
   private static final int SR_MOD_COLOR = -13656508;
   private static final int ADMIN_COLOR = -28117;
   private static final int SR_ADMIN_COLOR = -41892;
   private static final int TEAM_COLOR = -5138436;
   private static final Color SAFE_COLOR = new Color(-8586240);
   public final IntSetting scale = (IntSetting)this.addSetting(
      new IntSetting(
         "Scale",
         "HUD scale (1-5)",
         1,
         1,
         5,
         1
      )
   );
   public final BoolSetting notify = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Notify",
         "Notify when staff joins or leaves",
         true
      )
   );
   public final BoolSetting chatNotify = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Chat",
         "Send notifications to chat",
         true
      )
   );
   public final BoolSetting toastNotify = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Toast",
         "Show on-screen toast notifications",
         true
      )
   );
   public final BoolSetting cordNotify = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Cord",
         "Show top compass cord notification",
         true
      )
   );
   public final BoolSetting soundNotify = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Sound",
         "Play alert sound when staff joins/leaves",
         true
      )
   );
   public static final Set DYNAMIC_STAFF = ConcurrentHashMap.newKeySet();
   private final List cachedOnline = new ArrayList();
   private List cachedRoleGroups = List.of();
   private boolean worldJoined = false;
   private boolean wasInWarmup = false;
   private int warmupTicks = 0;
   private static final int WARMUP_TICKS = 80;
   private final Set seenDuringWarmup = new HashSet();
   private final Map missingScanCount = new ConcurrentHashMap();
   private boolean glassReadyThisFrame;
   private boolean backdropQueuedThisFrame;
   private StaffLists.PanelLayout preparedLayout;
   private StaffLists.PanelLayout cachedLayout;
   private int lastStaffVersion = -1;
   private int lastScale = -1;
   private float lastPosX = -1.0F;
   private float lastPosY = -1.0F;
   private int staffVersion = 0;
   private int scanThrottle = 0;
   private boolean boundsCleared = false;
   private HUD cachedHud;
   private static final Pattern COLOR_PATTERN = Pattern.compile(
      "(?i)§[0-9a-fk-or]"
   );
   public static final String STAR_CHARS = "★☆⭐✦✧✪✫✬✭✮✯✰";
   private static final Pattern RANK_OWNER = Pattern.compile(
      "(?i)(?:\\[|\\(|\\b)(?:owner|sr\\.?admin|sradmin)(?:\\]|\\)|\\b|\\s|:|\\|)"
   );
   private static final Pattern RANK_SR_MOD = Pattern.compile(
      "(?i)(?:\\[|\\(|\\b)(?:sr\\.?mod|srmod)(?:\\]|\\)|\\b|\\s|:|\\|)"
   );
   private static final Pattern RANK_ADMIN = Pattern.compile(
      "(?i)(?:\\[|\\(|\\b)(?:admin|administrator)(?:\\]|\\)|\\b|\\s|:|\\|)"
   );
   private static final Pattern RANK_MOD = Pattern.compile(
      "(?i)(?:\\[|\\(|\\b)(?:mod|moderator)(?:\\]|\\)|\\b|\\s|:|\\|)"
   );
   private static final Pattern RANK_TEAM = Pattern.compile(
      "(?i)(?:\\[|\\(|\\b)(?:helper|trial\\.?mod|staff|team)(?:\\]|\\)|\\b|\\s|:|\\|)"
   );
   private static final List STAFF = List.of(
      new StaffLists.StaffMember("Munkerlich", StaffLists.Role.MOD),
      new StaffLists.StaffMember("Chaon", StaffLists.Role.MOD),
      new StaffLists.StaffMember("Showered", StaffLists.Role.SR_MOD),
      new StaffLists.StaffMember("PastaGamer08", StaffLists.Role.SR_MOD),
      new StaffLists.StaffMember("Bautiedgar", StaffLists.Role.SR_MOD),
      new StaffLists.StaffMember("Bloodspulse", StaffLists.Role.SR_MOD),
      new StaffLists.StaffMember("GsMusie", StaffLists.Role.ADMIN),
      new StaffLists.StaffMember("Frwost", StaffLists.Role.ADMIN),
      new StaffLists.StaffMember(
         "FluffyMaster07", StaffLists.Role.ADMIN
      ),
      new StaffLists.StaffMember("W1zox_", StaffLists.Role.SR_ADMIN),
      new StaffLists.StaffMember("ItsZDeath", StaffLists.Role.SR_ADMIN),
      new StaffLists.StaffMember("ArchivePedro", StaffLists.Role.TEAM),
      new StaffLists.StaffMember("Evify", StaffLists.Role.TEAM),
      new StaffLists.StaffMember("Noah", StaffLists.Role.TEAM),
      new StaffLists.StaffMember("Zababi", StaffLists.Role.TEAM),
      new StaffLists.StaffMember("Nathan", StaffLists.Role.TEAM),
      new StaffLists.StaffMember("Owen1212055", StaffLists.Role.TEAM)
   );
   private static final Map STAFF_BY_NAME = new HashMap();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public int getScale() {
      return (Integer)this.scale.get();
   }

   public static boolean isStarChar(char c) {
      byte[] var10000 = new byte[36];
      var10000[0] = (byte)(-30);
      var10000[1] = (byte)(2);
      var10000[2] = (byte)(-103);
      var10000[3] = (byte)(60);
      var10000[4] = (byte)(72);
      var10000[5] = 0;
      var10000[6] = (byte)(94);
      var10000[7] = (byte)(89);
      var10000[8] = (byte)(-8);
      var10000[9] = (byte)(64);
      var10000[10] = (byte)(120);
      var10000[11] = (byte)(80);
      var10000[12] = (byte)(106);
      var10000[13] = (byte)(-62);
      var10000[14] = (byte)(51);
      var10000[15] = (byte)(110);
      var10000[16] = (byte)(-52);
      var10000[17] = (byte)(-32);
      var10000[18] = (byte)(46);
      var10000[19] = (byte)(50);
      var10000[20] = (byte)(-53);
      var10000[21] = (byte)(-108);
      var10000[22] = (byte)(112);
      var10000[23] = (byte)(8);
      var10000[24] = (byte)(90);
      var10000[25] = (byte)(-114);
      var10000[26] = (byte)(57);
      var10000[27] = (byte)(-92);
      var10000[28] = (byte)(-60);
      var10000[29] = (byte)(-96);
      var10000[30] = (byte)(38);
      var10000[31] = (byte)(-32);
      var10000[32] = (byte)(79);
      var10000[33] = (byte)(-40);
      var10000[34] = (byte)(-32);
      var10000[35] = (byte)(-50);
      return $$v_s(var10000, -2540214455569756123L).indexOf(c) >= 0;
   }

   public static boolean hasStarChar(String text) {
      if (text == null) {
         return false;
      }

      for (int var1 = 0; var1 < text.length(); var1++) {
         if (isStarChar(text.charAt(var1))) {
            return true;
         }
      }

      return false;
   }

   public static StaffLists.Role detectStarRole(class_2561 textComponent, String profileName) {
      if (textComponent == null) {
         return null;
      }

      String var2 = textComponent.getString();
      if (!hasStarChar(var2)) {
         return null;
      }

      if (profileName != null && !profileName.isBlank()) {
         String var3 = var2.toLowerCase(Locale.ROOT);
         String var4 = profileName.toLowerCase(Locale.ROOT);
         int var5 = var3.indexOf(var4);
         if (var5 >= 0) {
            int var6 = -1;

            for (int var7 = 0; var7 < var2.length(); var7++) {
               if (isStarChar(var2.charAt(var7))) {
                  var6 = var7;
                  break;
               }
            }

            if (var6 > var5) {
               return null;
            }
         }
      }

      class_5251[] var8 = new class_5251[1];
      textComponent.method_27658((style, part) -> {
         if (hasStarChar(part) && style != null && style.method_10973() != null) {
            var8[0] = style.method_10973();
            return Optional.of(part);
         } else {
            return Optional.empty();
         }
      }, class_2583.field_24360);
      if (var8[0] != null) {
         return roleFromRgb(var8[0].method_27716());
      }

      for (int var9 = 0; var9 < var2.length(); var9++) {
         if (isStarChar(var2.charAt(var9))) {
            for (int var10 = var9 - 1; var10 >= 1; var10--) {
               if (var2.charAt(var10 - 1) == (167)) {
                  char var11 = Character.toLowerCase(var2.charAt(var10));
                  StaffLists.Role var12 = roleFromColorCode(var11);
                  if (var12 != null) {
                     return var12;
                  }
               }
            }
            break;
         }
      }

      return StaffLists.Role.TEAM;
   }

   public static StaffLists.Role roleFromColorCode(char code) {
      return switch (code) {
         case '1', '3', '9', 'b' -> StaffLists.Role.TEAM;
         case '2' -> StaffLists.Role.SR_MOD;
         case '4', 'c' -> StaffLists.Role.SR_ADMIN;
         case '5', 'd' -> StaffLists.Role.TEAM;
         case '6', 'e' -> StaffLists.Role.ADMIN;
         case 'a' -> StaffLists.Role.MOD;
         default -> null;
      };
   }

   public static StaffLists.Role roleFromRgb(int rgb) {
      float var1 = (rgb >> (16) & (255)) / 255.0F;
      float var2 = (rgb >> (8) & (255)) / 255.0F;
      float var3 = (rgb & (255)) / 255.0F;
      float[] var4 = Color.RGBtoHSB(
         rgb >> (16) & (255), rgb >> (8) & (255), rgb & (255), null
      );
      float var5 = var4[0] * 360.0F;
      float var6 = var4[1];
      float var7 = var4[2];
      if (var6 < 0.2F) {
         return StaffLists.Role.TEAM;
      } else if (var5 >= 345.0F || var5 <= 18.0F) {
         return StaffLists.Role.SR_ADMIN;
      } else if (var5 > 18.0F && var5 <= 68.0F) {
         return StaffLists.Role.ADMIN;
      } else if (var5 > 68.0F && var5 <= 165.0F) {
         return !(var7 < 0.7F) && (!(var1 < 0.2F) || !(var2 < 0.8F) || !(var3 < 0.2F)) ? StaffLists.Role.MOD : StaffLists.Role.SR_MOD;
      } else {
         return var5 > 165.0F && var5 <= 260.0F ? StaffLists.Role.TEAM : StaffLists.Role.TEAM;
      }
   }

   public static StaffLists.Role detectRankRole(String text) {
      if (text != null && !text.isBlank()) {
         String var1 = strip(text);
         if (RANK_OWNER.matcher(var1).find()) {
            return StaffLists.Role.SR_ADMIN;
         } else if (RANK_SR_MOD.matcher(var1).find()) {
            return StaffLists.Role.SR_MOD;
         } else if (RANK_ADMIN.matcher(var1).find()) {
            return StaffLists.Role.ADMIN;
         } else if (RANK_MOD.matcher(var1).find()) {
            return StaffLists.Role.MOD;
         } else {
            return RANK_TEAM.matcher(var1).find() ? StaffLists.Role.TEAM : null;
         }
      } else {
         return null;
      }
   }

   public boolean isCustomStaff(String name) {
      if (name != null && !DYNAMIC_STAFF.isEmpty()) {
         for (String var3 : DYNAMIC_STAFF) {
            if (name.equalsIgnoreCase(var3)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public static boolean isStaff(String name) {
      if (name != null && !name.isBlank()) {
         String var1 = name.toLowerCase(Locale.ROOT);
         if (STAFF_BY_NAME.containsKey(var1)) {
            return true;
         }

         for (String var3 : DYNAMIC_STAFF) {
            if (var3 != null && var3.equalsIgnoreCase(var1)) {
               return true;
            }
         }

         return StaffCache.isKnownStaff(var1);
      } else {
         return false;
      }
   }

   public static StaffLists.Role getStaffRole(String name) {
      if (name != null && !name.isBlank()) {
         String var1 = name.toLowerCase(Locale.ROOT);
         StaffLists.StaffMember var2 = (StaffLists.StaffMember)STAFF_BY_NAME.get(var1);
         if (var2 != null) {
            return var2.role;
         }

         for (String var4 : DYNAMIC_STAFF) {
            if (var4 != null && var4.equalsIgnoreCase(var1)) {
               return StaffLists.Role.MOD;
            }
         }

         return StaffCache.getKnownRole(var1);
      } else {
         return null;
      }
   }

   public StaffLists() {
      super(
         "StaffLists",
         "Shows online SMP staff in the HUD",
         Category.SMP_UTILITIES
      );
      this.scale.setVisibleInGui(false);
      this.chatNotify.visibleWhen(this.notify::get);
      this.toastNotify.visibleWhen(this.notify::get);
      this.cordNotify.visibleWhen(this.notify::get);
      this.soundNotify.visibleWhen(this.notify::get);
   }

   @Override
   public void onEnable() {
      HUD var1 = this.hudEditor();
      if (var1 != null) {
         Map var10000 = var1.getWidgetPositions();
         byte[] var10001 = new byte[9];
         var10001[0] = (byte)(115);
         var10001[1] = (byte)(-18);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-72);
         var10001[4] = (byte)(-74);
         var10001[5] = (byte)(-22);
         var10001[6] = (byte)(-43);
         var10001[7] = (byte)(-121);
         var10001[8] = (byte)(28);
         if (var10000.get($$v_s(var10001, -2540214455569756123L)) == null) {
            int var2 = class_310.method_1551().method_22683().method_4486();
            int var3 = class_310.method_1551().method_22683().method_4502();
            float[] var5 = new float[2];
            var5[0] = Math.max(1.0F, var2 - 200.0F);
            var5[1] = Math.max(1.0F, var3 / 2.0F - 100.0F);
            float[] var4 = var5;
            var10000 = var1.getWidgetPositions();
            var10001 = new byte[9];
            var10001[0] = (byte)(115);
            var10001[1] = (byte)(-18);
            var10001[2] = (byte)(125);
            var10001[3] = (byte)(-72);
            var10001[4] = (byte)(-74);
            var10001[5] = (byte)(-22);
            var10001[6] = (byte)(-43);
            var10001[7] = (byte)(-121);
            var10001[8] = (byte)(28);
            var10000.put($$v_s(var10001, -2540214455569756123L), var4);
         }
      }

      this.cachedOnline.clear();
      this.cachedRoleGroups = List.of();
      this.cachedLayout = null;
      this.staffVersion++;
      this.scanThrottle = 0;
      this.worldJoined = false;
      this.warmupTicks = 0;
      this.seenDuringWarmup.clear();
      this.boundsCleared = false;
      StaffCache.ensureLoaded();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.preparedLayout = null;
      this.cachedLayout = null;
      this.backdropQueuedThisFrame = false;
      this.glassReadyThisFrame = false;
      this.cachedOnline.clear();
      this.cachedRoleGroups = List.of();
      this.staffVersion++;
      this.worldJoined = false;
      this.warmupTicks = 0;
      this.seenDuringWarmup.clear();
      this.clearHUDBounds();
      this.boundsCleared = true;
   }

   @EventHandler
   public void onTick(TickEvent e) {
      if (e.getPhase() == TickEvent.Phase.PRE) {
         if (this.isEnabled() && this.isInGame() && mc != null && mc.field_1724 != null && mc.field_1687 != null && mc.method_1562() != null) {
            String var2 = StaffCache.getCurrentServerKey();
            if (!this.worldJoined) {
               this.worldJoined = true;
               this.warmupTicks = 0;
               this.seenDuringWarmup.clear();
               StaffCache.ensureLoaded();
            }

            boolean var3 = this.warmupTicks < 80;
            if (var3) {
               this.warmupTicks++;
            }

            int var4 = var3 ? 5 : 10;
            if (++this.scanThrottle >= var4) {
               this.scanThrottle = 0;
               Collection var5 = mc.method_1562().method_45732();
               if (var5 == null || var5.isEmpty()) {
                  var5 = mc.method_1562().method_2880();
               }

               if (var5 != null && !var5.isEmpty()) {
                  LinkedHashMap var6 = new LinkedHashMap();

                  for (class_640 var8 : var5) {
                     if (var8 != null) {
                        GameProfile var9 = var8.method_2966();
                        String var10 = var9 != null ? var9.name() : null;
                        String var11 = var10 != null ? var10.toLowerCase(Locale.ROOT) : null;
                        if (var11 != null && !var11.isBlank()) {
                           StaffLists.StaffMember var12 = null;
                           var12 = (StaffLists.StaffMember)STAFF_BY_NAME.get(var11);
                           if (var12 == null && !DYNAMIC_STAFF.isEmpty()) {
                              for (String var14 : DYNAMIC_STAFF) {
                                 if (var14 != null && var14.equalsIgnoreCase(var11)) {
                                    var12 = new StaffLists.StaffMember(var10, StaffLists.Role.MOD);
                                    break;
                                 }
                              }
                           }

                           if (var12 == null) {
                              class_2561 var35 = var8.method_2971();
                              if (var35 != null) {
                                 String var37 = var35.getString();
                                 String var15 = strip(var37);
                                 if (!var15.isEmpty()) {
                                    var12 = (StaffLists.StaffMember)STAFF_BY_NAME.get(var15);
                                    if (var12 == null && !DYNAMIC_STAFF.isEmpty()) {
                                       for (String var17 : DYNAMIC_STAFF) {
                                          if (var17 != null && var17.equalsIgnoreCase(var15)) {
                                             var12 = new StaffLists.StaffMember(var10, StaffLists.Role.MOD);
                                             break;
                                          }
                                       }
                                    }

                                    if (var12 == null) {
                                       StaffLists.Role var40 = detectRankRole(var37);
                                       if (var40 != null) {
                                          var12 = new StaffLists.StaffMember(var10, var40);
                                       }
                                    }
                                 }

                                 if (var12 == null) {
                                    StaffLists.Role var41 = detectStarRole(var35, var10);
                                    if (var41 != null) {
                                       var12 = new StaffLists.StaffMember(var10, var41);
                                    }
                                 }
                              }

                              if (var12 == null && var8.method_2955() != null) {
                                 class_2561 var38 = var8.method_2955().method_1144();
                                 if (var38 != null) {
                                    StaffLists.Role var39 = detectStarRole(var38, var10);
                                    if (var39 != null) {
                                       var12 = new StaffLists.StaffMember(var10, var39);
                                    } else {
                                       StaffLists.Role var42 = detectRankRole(var38.getString());
                                       if (var42 != null) {
                                          var12 = new StaffLists.StaffMember(var10, var42);
                                       }
                                    }
                                 }
                              }
                           }

                           if (var12 != null && !var6.containsKey(var11)) {
                              StaffLists.OnlineStaff var36 = new StaffLists.OnlineStaff(var12, var8);
                              var6.put(var11, var36);
                           }
                        }
                     }
                  }

                  boolean var18 = false;
                  if (var6.size() != this.cachedOnline.size()) {
                     var18 = true;
                  } else {
                     for (StaffLists.OnlineStaff var24 : this.cachedOnline) {
                        String var29 = var24.staff().name.toLowerCase(Locale.ROOT);
                        if (!var6.containsKey(var29)) {
                           var18 = true;
                           break;
                        }
                     }
                  }

                  if (var18) {
                     this.cachedOnline.clear();
                     this.cachedOnline.addAll(var6.values());
                     this.cachedRoleGroups = this.groupByRole();
                     this.staffVersion++;
                  }

                  if (this.wasInWarmup && !var3) {
                     for (String var25 : StaffCache.getStaffKeys(var2)) {
                        if (!var6.containsKey(var25)) {
                           StaffCache.remove(var2, var25);
                        }
                     }

                     this.seenDuringWarmup.clear();
                     this.missingScanCount.clear();
                  }

                  this.wasInWarmup = var3;
                  if (var3) {
                     for (Entry var26 : var6.entrySet()) {
                        this.seenDuringWarmup.add((String)var26.getKey());
                        StaffCache.put(var2, displayName((StaffLists.OnlineStaff)var26.getValue()), ((StaffLists.OnlineStaff)var26.getValue()).staff().role);
                     }
                  } else {
                     for (Entry var27 : var6.entrySet()) {
                        String var30 = (String)var27.getKey();
                        this.missingScanCount.remove(var30);
                        if (!StaffCache.contains(var2, var30)) {
                           if ((Boolean)this.notify.get()) {
                              this.onStaffJoin((StaffLists.OnlineStaff)var27.getValue());
                           }

                           StaffCache.put(var2, displayName((StaffLists.OnlineStaff)var27.getValue()), ((StaffLists.OnlineStaff)var27.getValue()).staff().role);
                        } else {
                           StaffCache.updateLastSeen(var2, var30);
                        }
                     }

                     for (String var28 : StaffCache.getStaffKeys(var2)) {
                        if (!var6.containsKey(var28)) {
                           int var31 = this.missingScanCount.getOrDefault(var28, 0) + 1;
                           this.missingScanCount.put(var28, var31);
                           if (var31 >= 2) {
                              StaffCache.StaffCacheEntry var32 = StaffCache.get(var2, var28);
                              if (var32 != null) {
                                 StaffLists.StaffMember var34 = new StaffLists.StaffMember(var32.name, var32.role);
                                 if ((Boolean)this.notify.get()) {
                                    this.onStaffLeave(var34);
                                 }
                              }

                              StaffCache.remove(var2, var28);
                              this.missingScanCount.remove(var28);
                           }
                        }
                     }
                  }
               } else {
                  if (!this.cachedOnline.isEmpty()) {
                     this.cachedOnline.clear();
                     this.cachedRoleGroups = List.of();
                     this.cachedLayout = null;
                     this.staffVersion++;
                  }
               }
            }
         } else {
            if (this.worldJoined || !this.cachedOnline.isEmpty()) {
               this.worldJoined = false;
               this.warmupTicks = 0;
               this.seenDuringWarmup.clear();
               this.cachedOnline.clear();
               this.cachedRoleGroups = List.of();
               this.cachedLayout = null;
               this.staffVersion++;
            }
         }
      }
   }

   private void onStaffJoin(StaffLists.OnlineStaff online) {
      String var2 = displayName(online);
      String var3 = online.staff().role.name().replace((char)(95), (char)(32));
      if ((Boolean)this.chatNotify.get() && mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470("§d[StaffList] §f" + var2 + " §7(" + var3 + ") §ajoined the game!"), false);
      }

      if ((Boolean)this.toastNotify.get()) {
         ToastManager.pushStaff(var2, var3, true);
      } else if ((Boolean)this.cordNotify.get()) {
         CordNotification.push("Staff " + var2 + " joined (" + var3 + ")", 0);
      }

      if ((Boolean)this.soundNotify.get() && mc.field_1724 != null) {
         mc.field_1724.method_5783((class_3414)class_3417.field_14793.comp_349(), 1.0F, 1.0F);
      }
   }

   private void onStaffLeave(StaffLists.StaffMember staff) {
      String var2 = staff.name;
      String var3 = staff.role.name().replace((char)(95), (char)(32));
      if ((Boolean)this.chatNotify.get() && mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470("§d[StaffList] §f" + var2 + " §7(" + var3 + ") §cleft the game!"), false);
      }

      if ((Boolean)this.toastNotify.get()) {
         ToastManager.pushStaff(var2, var3, false);
      } else if ((Boolean)this.cordNotify.get()) {
         CordNotification.push("Staff " + var2 + " left (" + var3 + ")", 0);
      }

      if ((Boolean)this.soundNotify.get() && mc.field_1724 != null) {
         mc.field_1724.method_5783((class_3414)class_3417.field_14622.comp_349(), 1.0F, 0.8F);
      }
   }

   private static String strip(String text) {
      if (text != null && !text.isEmpty()) {
         if (text.indexOf(167) >= 0) {
            text = COLOR_PATTERN.matcher(text).replaceAll("");
         }

         return text.trim().toLowerCase(Locale.ROOT);
      } else {
         return "";
      }
   }

   @EventHandler
   public void onBackdropPrepare(BackdropPrepareEvent event) {
      this.preparedLayout = null;
      this.backdropQueuedThisFrame = false;
      this.glassReadyThisFrame = false;
      if (this.isEnabled()) {
         this.boundsCleared = false;
         if (this.isInGame() && mc.field_1724 != null && mc.field_1687 != null) {
            this.preparedLayout = this.measurePanelLayout();
            if (this.preparedLayout == null) {
               this.clearHUDBounds();
            } else {
               HUD var5 = this.hudEditor();
               float var10000;
               if (var5 == null) {
                  var10000 = 1.0F;
               } else {
                  byte[] var10001 = new byte[9];
                  var10001[0] = (byte)(115);
                  var10001[1] = (byte)(-18);
                  var10001[2] = (byte)(125);
                  var10001[3] = (byte)(-72);
                  var10001[4] = (byte)(-74);
                  var10001[5] = (byte)(-22);
                  var10001[6] = (byte)(-43);
                  var10001[7] = (byte)(-121);
                  var10001[8] = (byte)(28);
                  var10000 = var5.getWidgetScale($$v_s(var10001, -2540214455569756123L));
               }

               float var3 = var10000;
               FrostedBackground.queueLogicalRect(
                  this.preparedLayout.x(), this.preparedLayout.y(), this.preparedLayout.width() * var3, this.preparedLayout.height() * var3, 7.0F * var3, false
               );
               this.backdropQueuedThisFrame = true;
            }
         } else {
            this.clearHUDBounds();
         }
      } else {
         try {
            HUD var2 = this.hudEditor();
            if (var2 != null && var2.isEditorMode() && mc.field_1724 != null && mc.field_1687 != null) {
               this.drawEditorPlaceholder();
               this.boundsCleared = false;
            } else if (!this.boundsCleared) {
               this.clearHUDBounds();
               this.boundsCleared = true;
            }
         } catch (Throwable var4) {
            if (!this.boundsCleared) {
               this.clearHUDBounds();
               this.boundsCleared = true;
            }
         }
      }
   }

   private void drawEditorPlaceholder() {
      HUD var1 = this.hudEditor();
      float var10000;
      if (var1 == null) {
         var10000 = 1.0F;
      } else {
         byte[] var10001 = new byte[9];
         var10001[0] = (byte)(115);
         var10001[1] = (byte)(-18);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-72);
         var10001[4] = (byte)(-74);
         var10001[5] = (byte)(-22);
         var10001[6] = (byte)(-43);
         var10001[7] = (byte)(-121);
         var10001[8] = (byte)(28);
         var10000 = var1.getWidgetScale($$v_s(var10001, -2540214455569756123L));
      }

      float var2 = var10000;
      float var3 = 10.5F * var2;
      float var4 = 6.0F * var2;
      float var5 = Math.max(9.0F * var2, var3 - 1.0F * var2);
      byte[] var11 = new byte[5];
      var11[0] = (byte)(83);
      var11[1] = (byte)(-18);
      var11[2] = (byte)(125);
      var11[3] = (byte)(-72);
      var11[4] = (byte)(-74);
      float var6 = NanoVGRenderer.getTextWidth($$v_s(var11, -2540214455569756123L), 9.0F * var2);
      float var7 = var5 + 4.0F * var2 + var6;
      float var8 = var7 + var4 * 2.0F;
      float var9 = var4 * 2.0F + var3;
      float[] var10 = this.getPositionFromEditor();
      this.cacheHUDBounds(var10[0], var10[1], var8, var9);
      if (var1 != null) {
         byte[] var12 = new byte[9];
         var12[0] = (byte)(115);
         var12[1] = (byte)(-18);
         var12[2] = (byte)(125);
         var12[3] = (byte)(-72);
         var12[4] = (byte)(-74);
         var12[5] = (byte)(-22);
         var12[6] = (byte)(-43);
         var12[7] = (byte)(-121);
         var12[8] = (byte)(28);
         var1.drawExternalEditorOutline($$v_s(var12, -2540214455569756123L), var10[0], var10[1], var8, var9);
      }
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      StaffLists.PanelLayout var2 = this.preparedLayout;
      if (var2 != null) {
         this.glassReadyThisFrame = this.backdropQueuedThisFrame && event.backdropDrawn();
         this.renderPanelLayout(var2);
      }
   }

   private StaffLists.PanelLayout measurePanelLayout() {
      float var1 = ((Integer)this.scale.get()).intValue();
      float var2 = 9.0F * var1;
      float var3 = 10.5F * var1;
      float var4 = 6.0F * var1;
      float var5 = Math.max(8.0F * var1, var3 - 2.0F * var1);
      float var6 = 4.0F * var1;
      float var7 = Math.max(9.0F * var1, var3 - 1.0F * var1);
      float[] var8 = this.getPositionFromEditor();
      float var9 = var8[0];
      float var10 = var8[1];
      int var11 = (Integer)this.scale.get();
      if (this.cachedLayout != null && this.staffVersion == this.lastStaffVersion && var11 == this.lastScale && var9 == this.lastPosX && var10 == this.lastPosY
         )
       {
         return this.cachedLayout;
      }

      float var12 = NanoVGRenderer.getTextWidth("Staff", var2);
      float var13 = var7 + 4.0F * var1 + var12;
      if (this.cachedOnline.isEmpty()) {
         StaffLists.PanelLayout var22 = new StaffLists.PanelLayout(
            var9, var10, var13 + var4 * 2.0F, var4 * 2.0F + var3, var1, var2, var3, var4, var5, var6, List.of()
         );
         this.cachedLayout = var22;
         this.lastStaffVersion = this.staffVersion;
         this.lastScale = var11;
         this.lastPosX = var9;
         this.lastPosY = var10;
         return var22;
      }

      List var14 = this.cachedRoleGroups.isEmpty() ? this.groupByRole() : this.cachedRoleGroups;
      float var15 = 0.0F;
      int var16 = 0;

      for (StaffLists.RoleGroup var18 : var14) {
         for (StaffLists.OnlineStaff var20 : var18.members) {
            float var21 = NanoVGRenderer.getTextWidth(displayName(var20), var2);
            if (var21 > var15) {
               var15 = var21;
            }

            var16++;
         }
      }

      float var23 = var5 + var6 + var15;
      float var24 = Math.max(var13, var23);
      float var25 = var4 * 2.0F + var3 + var16 * var3;
      StaffLists.PanelLayout var26 = new StaffLists.PanelLayout(var9, var10, var24 + var4 * 2.0F, var25, var1, var2, var3, var4, var5, var6, var14);
      this.cachedLayout = var26;
      this.lastStaffVersion = this.staffVersion;
      this.lastScale = var11;
      this.lastPosX = var9;
      this.lastPosY = var10;
      return var26;
   }

   private void renderPanelLayout(StaffLists.PanelLayout layout) {
      try (NanoVGFrame var2 = NanoVGRenderer.frame()) {
         if (!var2.isActive()) {
            return;
         }

         HUD var3 = this.hudEditor();
         float var4 = layout.x();
         float var5 = layout.y();
         float var6 = layout.width();
         float var7 = layout.height();
         this.cacheHUDBounds(var4, var5, var6, var7);

         try (HUD.WidgetRenderScope var8 = var3 == null
               ? null
               : var3.beginWidgetRender("stafflist", var4, var5)) {
            NanoVGRenderer.globalAlpha(1.0F);
            float var9 = layout.scale();
            float var10 = layout.fontSize();
            float var11 = layout.lineHeight();
            float var12 = layout.padding();
            float var13 = layout.headSize();
            float var14 = layout.headGap();
            this.drawPanel(var4, var5, var6, var7);
            float var15 = Math.max(9.0F * var9, var11 - 1.0F * var9);
            float var16 = var5 + var12 + (var11 - var10) / 2.0F;
            float var17 = var4 + var12;
            float var18 = var16 + (var10 - var15) / 2.0F;
            IconManager.draw("stafflist", var17, var18, var15, 255);
            NanoVGRenderer.drawText(
               "Staff", var17 + var15 + 4.0F * var9, var16, var10, ThemeManager.text(255), true
            );
            if (!layout.groups().isEmpty()) {
               float var19 = var5 + var12 + var11;

               for (StaffLists.RoleGroup var21 : layout.groups()) {
                  int var22 = var21.color;

                  for (StaffLists.OnlineStaff var24 : var21.members) {
                     float var25 = var19 + (var11 - var13) / 2.0F;
                     IconManager.draw("staff", var4 + var12, var25, var13, 230);
                     NanoVGRenderer.drawText(displayName(var24), var4 + var12 + var13 + var14, var19 + (var11 - var10) / 2.0F, var10, var22);
                     var19 += var11;
                  }
               }
            }
         }

         this.drawEditorOutline(var4, var5, var6, var7);
      }
   }

   private void drawPanel(float x, float y, float w, float h) {
      float var5 = 7.0F;
      int var6 = (int)(ThemeManager.bodyAlpha(1.0F) * MaterialFX.bodyTintMul());
      int[] var7 = ThemeManager.bodyTop();
      int[] var8 = ThemeManager.bodyBot();
      MaterialFX.glassBody(x, y, w, h, var5, 1.0F);
      int var9 = var6 << (24)
         | (var7[0] & (255)) << (16)
         | (var7[1] & (255)) << (8)
         | var7[2] & (255);
      int var10 = var6 << (24)
         | (var8[0] & (255)) << (16)
         | (var8[1] & (255)) << (8)
         | var8[2] & (255);
      NanoVGRenderer.drawRoundedRectGradient(x, y, w, h, var5, var9, var10);
      if (!ThemeManager.isCleanStyle()) {
         NanoVGRenderer.drawRoundedRectOutline(x, y, w, h, var5, 1.0F, themeBorderArgb());
      }

      GuiStyle.gloss(x, y, w, h, var5, 1.0F);
      MaterialFX.innerTopRim(x, y, w, h, var5, 1.0F, 184549375);
   }

   private static int themeBorderArgb() {
      int[] var0 = ThemeManager.borderRGB();
      return -1107296256
         | (var0[0] & (255)) << (16)
         | (var0[1] & (255)) << (8)
         | var0[2] & (255);
   }

   private List groupByRole() {
      ArrayList var1 = new ArrayList();
      ArrayList var2 = new ArrayList();
      ArrayList var3 = new ArrayList();
      ArrayList var4 = new ArrayList();
      ArrayList var5 = new ArrayList();
      ArrayList var6 = new ArrayList();

      for (StaffLists.OnlineStaff var8 : this.cachedOnline) {
         switch (var8.staff().role) {
            case MOD:
               var2.add(var8);
               break;
            case SR_MOD:
               var3.add(var8);
               break;
            case ADMIN:
               var4.add(var8);
               break;
            case SR_ADMIN:
               var5.add(var8);
               break;
            case TEAM:
               var6.add(var8);
         }
      }

      if (!var2.isEmpty()) {
         var1.add(new StaffLists.RoleGroup("MOD", var2, -7542374));
      }

      if (!var3.isEmpty()) {
         var1.add(new StaffLists.RoleGroup("SR MOD", var3, -13656508));
      }

      if (!var4.isEmpty()) {
         var1.add(new StaffLists.RoleGroup("ADMIN", var4, -28117));
      }

      if (!var5.isEmpty()) {
         var1.add(new StaffLists.RoleGroup("SR ADMIN", var5, -41892));
      }

      if (!var6.isEmpty()) {
         var1.add(new StaffLists.RoleGroup("TEAM", var6, -5138436));
      }

      return var1;
   }

   private static String displayName(StaffLists.OnlineStaff online) {
      String var1 = online.staff().name;
      if (online.entry() != null
         && online.entry().method_2966() != null
         && online.entry().method_2966().name() != null
         && !online.entry().method_2966().name().isBlank()) {
         var1 = online.entry().method_2966().name();
      }

      return var1;
   }

   private float[] getPositionFromEditor() {
      HUD var1 = this.hudEditor();
      if (var1 != null) {
         byte[] var10001 = new byte[9];
         var10001[0] = (byte)(115);
         var10001[1] = (byte)(-18);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-72);
         var10001[4] = (byte)(-74);
         var10001[5] = (byte)(-22);
         var10001[6] = (byte)(-43);
         var10001[7] = (byte)(-121);
         var10001[8] = (byte)(28);
         float var2 = var1.wx($$v_s(var10001, -2540214455569756123L), 10.0F);
         var10001 = new byte[9];
         var10001[0] = (byte)(115);
         var10001[1] = (byte)(-18);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-72);
         var10001[4] = (byte)(-74);
         var10001[5] = (byte)(-22);
         var10001[6] = (byte)(-43);
         var10001[7] = (byte)(-121);
         var10001[8] = (byte)(28);
         float var3 = var1.wy($$v_s(var10001, -2540214455569756123L), 60.0F);
         float[] var4 = new float[2];
         var4[0] = var2;
         var4[1] = var3;
         return var4;
      } else {
         float[] var10000 = new float[2];
         var10000[0] = 10.0F;
         var10000[1] = 60.0F;
         return var10000;
      }
   }

   private void cacheHUDBounds(float x, float y, float w, float h) {
      HUD var5 = this.hudEditor();
      if (var5 != null) {
         byte[] var10001 = new byte[9];
         var10001[0] = (byte)(115);
         var10001[1] = (byte)(-18);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-72);
         var10001[4] = (byte)(-74);
         var10001[5] = (byte)(-22);
         var10001[6] = (byte)(-43);
         var10001[7] = (byte)(-121);
         var10001[8] = (byte)(28);
         var5.cacheExternalBounds($$v_s(var10001, -2540214455569756123L), x, y, w, h);
      }
   }

   private void clearHUDBounds() {
      try {
         HUD var1 = (HUD)VieClientMod.getInstance().getModuleManager().getModule(HUD.class);
         if (var1 != null) {
            byte[] var10001 = new byte[9];
            var10001[0] = (byte)(115);
            var10001[1] = (byte)(-18);
            var10001[2] = (byte)(125);
            var10001[3] = (byte)(-72);
            var10001[4] = (byte)(-74);
            var10001[5] = (byte)(-22);
            var10001[6] = (byte)(-43);
            var10001[7] = (byte)(-121);
            var10001[8] = (byte)(28);
            var1.removeExternalBounds($$v_s(var10001, -2540214455569756123L));
         }
      } catch (Exception var2) {
      }
   }

   private void drawEditorOutline(float x, float y, float w, float h) {
      HUD var5 = this.hudEditor();
      if (var5 != null) {
         byte[] var10001 = new byte[9];
         var10001[0] = (byte)(115);
         var10001[1] = (byte)(-18);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-72);
         var10001[4] = (byte)(-74);
         var10001[5] = (byte)(-22);
         var10001[6] = (byte)(-43);
         var10001[7] = (byte)(-121);
         var10001[8] = (byte)(28);
         var5.drawExternalEditorOutline($$v_s(var10001, -2540214455569756123L), x, y, w, h);
      }
   }

   private HUD hudEditor() {
      if (this.cachedHud != null) {
         return this.cachedHud;
      }

      try {
         this.cachedHud = (HUD)VieClientMod.getInstance().getModuleManager().getModule(HUD.class);
         return this.cachedHud;
      } catch (RuntimeException var2) {
         return null;
      }
   }

   private static Color themeBorder() {
      int[] var0 = ThemeManager.borderRGB();
      return new Color(var0[0], var0[1], var0[2], 190);
   }

   static {
      for (StaffLists.StaffMember var1 : STAFF) {
         STAFF_BY_NAME.put(var1.name.toLowerCase(Locale.ROOT), var1);
      }
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

   private record OnlineStaff(StaffLists.StaffMember staff, class_640 entry) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private record PanelLayout(
      float x,
      float y,
      float width,
      float height,
      float scale,
      float fontSize,
      float lineHeight,
      float padding,
      float headSize,
      float headGap,
      List<StaffLists.RoleGroup> groups
   ) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum Role {
      MOD,
      SR_MOD,
      ADMIN,
      SR_ADMIN,
      TEAM;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static StaffLists.Role[] $values() {
         StaffLists.Role[] var10000 = new StaffLists.Role[5];
         var10000[0] = MOD;
         var10000[1] = SR_MOD;
         var10000[2] = ADMIN;
         var10000[3] = SR_ADMIN;
         var10000[4] = TEAM;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(115);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(88);
         MOD = new StaffLists.Role();
         var10002 = new byte[6];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(67);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(-113);
         var10002[5] = (byte)(-127);
         SR_MOD = new StaffLists.Role();
         var10002 = new byte[5];
         var10002[0] = (byte)(127);
         var10002[1] = (byte)(-34);
         var10002[2] = (byte)(81);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-114);
         ADMIN = new StaffLists.Role();
         var10002 = new byte[8];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(67);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(-124);
         var10002[5] = (byte)(-120);
         var10002[6] = (byte)(-43);
         var10002[7] = (byte)(35);
         SR_ADMIN = new StaffLists.Role();
         var10002 = new byte[4];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(93);
         var10002[3] = (byte)(-109);
         TEAM = new StaffLists.Role();
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

   private static final class RoleGroup {
      final String title;
      final List members;
      final int color;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      RoleGroup(String title, List members, int color) {
         this.title = titlex;
         this.members = membersx;
         this.color = colorx;
      }
   }

   public static final class StaffMember {
      public final String name;
      public final StaffLists.Role role;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public StaffMember(String name, StaffLists.Role role) {
         this.name = namex;
         this.role = rolex;
      }
   }
}
