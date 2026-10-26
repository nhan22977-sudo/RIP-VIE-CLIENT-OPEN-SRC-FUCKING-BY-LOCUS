package me.drake.vieclient.modules.donut;

import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.render.nvg.NanoVGFrame;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.render.ToastManager;
import net.minecraft.class_1294;
import net.minecraft.class_1657;
import net.minecraft.class_1934;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import net.minecraft.class_640;

public class AntiAdminSpectator extends Module {
   private final IntSetting range = (IntSetting)this.addSetting(
      new IntSetting(
         "Range",
         "Maximum spectator detection distance (blocks)",
         150,
         10,
         150
      )
   );
   private final BoolSetting detectAllSpectators = (BoolSetting)this.addSetting(
      new BoolSetting(
         "All Spectators",
         "Warn for all players in spectator mode, not just staff",
         true
      )
   );
   private final BoolSetting targetingOnly = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Targeting Only",
         "Alert only when spectator is facing you or within 5 blocks",
         false
      )
   );
   private final BoolSetting screenBanner = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Screen Banner",
         "Display NanoVG top-screen alert banner",
         true
      )
   );
   private final BoolSetting chatAlert = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Chat Alert",
         "Print formatted threat warnings to chat",
         true
      )
   );
   private final BoolSetting soundAlert = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Sound Alert",
         "Play alarm sound when threat is detected",
         true
      )
   );
   private final BoolSetting toastAlert = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Toast Alert",
         "Show emergency alert toast notifications",
         true
      )
   );
   private final BoolSetting autoPanic = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Safe Mode",
         "Automatically disable blatant modules to prevent bans",
         true
      )
   );
   private final BoolSetting autoDisconnect = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Disconnect",
         "Disconnect from server when admin gets too close",
         false
      )
   );
   private final IntSetting disconnectRange = (IntSetting)this.addSetting(
      new IntSetting(
         "Disconnect Range",
         "Distance threshold to trigger emergency disconnect",
         15,
         3,
         50
      )
   );
   private final Map activeThreats = new ConcurrentHashMap();
   private final Set alertedServerSpectators = Collections.newSetFromMap(new ConcurrentHashMap());
   private final List onScreenAlerts = new CopyOnWriteArrayList();
   private long lastChatAlertTime = 0L;
   private long lastSoundAlertTime = 0L;
   private long lastToastAlertTime = 0L;
   private long lastTablistScanTime = 0L;
   private boolean panicTriggered = false;
   private boolean hadThreatsPreviously = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AntiAdminSpectator() {
      super(
         "AntiAdminSpectator",
         "Cảnh báo khẩn cấp khi Admin/Staff đang Spectator hoặc theo dõi bạn",
         Category.PROTECT
      );
      this.range.setVisibleInGui(false);
      this.disconnectRange.visibleWhen(this.autoDisconnect::get);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.activeThreats.clear();
      this.alertedServerSpectators.clear();
      this.onScreenAlerts.clear();
      this.lastChatAlertTime = 0L;
      this.lastSoundAlertTime = 0L;
      this.lastToastAlertTime = 0L;
      this.lastTablistScanTime = 0L;
      this.panicTriggered = false;
      this.hadThreatsPreviously = false;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.activeThreats.clear();
      this.alertedServerSpectators.clear();
      this.onScreenAlerts.clear();
      this.panicTriggered = false;
      this.hadThreatsPreviously = false;
   }

   public void pushOnScreenAlert(AntiAdminSpectator.OnScreenAlert alert) {
      if (alert != null) {
         if (alert.id != null) {
            this.onScreenAlerts.removeIf(a -> alert.id.equals(a.id));
         }

         this.onScreenAlerts.add(0, alert);

         while (this.onScreenAlerts.size() > (3)) {
            this.onScreenAlerts.remove(this.onScreenAlerts.size() - 1);
         }
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (this.isEnabled() && this.isInGame() && mc.field_1724 != null && mc.field_1687 != null) {
            long var2 = System.currentTimeMillis();
            double var4 = ((Integer)this.range.get()).intValue();
            HashSet var6 = new HashSet();

            for (class_1657 var8 : mc.field_1687.method_18456()) {
               if (var8 != mc.field_1724 && var8.method_5805()) {
                  double var9 = var8.method_5739(mc.field_1724);
                  if (!(var9 > var4)) {
                     String var11 = var8.method_5477().getString();
                     class_640 var12 = mc.method_1562() != null ? mc.method_1562().method_2871(var8.method_5667()) : null;
                     boolean var13 = var8.method_7325() || var12 != null && var12.method_2958() == class_1934.field_9219;
                     boolean var14 = var8.method_5767() || var8.method_5756(mc.field_1724);
                     boolean var15 = StaffLists.isStaff(var11);
                     StaffLists.Role var16 = StaffLists.getStaffRole(var11);
                     if (var16 == null && var12 != null && var12.method_2971() != null) {
                        var16 = StaffLists.detectRankRole(var12.method_2971().getString());
                        if (var16 != null) {
                           var15 = true;
                        }
                     }

                     if (var16 == null && var8.method_5476() != null) {
                        var16 = StaffLists.detectRankRole(var8.method_5476().getString());
                        if (var16 != null) {
                           var15 = true;
                        }
                     }

                     boolean var17 = false;
                     if (var15) {
                        if (var13 || var14 || var8.method_31549().field_7479 || var9 <= 35.0) {
                           var17 = true;
                        }
                     } else if ((Boolean)this.detectAllSpectators.get()) {
                        if (var13) {
                           var17 = true;
                        } else if (var14 && !var8.method_6059(class_1294.field_5905) && var9 <= 25.0) {
                           var17 = true;
                        }
                     }

                     if (var17) {
                        class_243 var18 = var8.method_33571();
                        class_243 var19 = mc.field_1724.method_33571();
                        class_243 var20 = var19.method_1020(var18).method_1029();
                        class_243 var21 = var8.method_5828(1.0F).method_1029();
                        double var22 = class_3532.method_15350(var21.method_1026(var20), -1.0, 1.0);
                        double var24 = Math.toDegrees(Math.acos(var22));
                        boolean var26 = var24 < 38.0 || var9 < 4.0;
                        if (!(Boolean)this.targetingOnly.get() || var26 || !(var9 >= 5.0)) {
                           var6.add(var8.method_5667());
                           AntiAdminSpectator.SpectatorThreat var27 = (AntiAdminSpectator.SpectatorThreat)this.activeThreats.get(var8.method_5667());
                           if (var27 == null) {
                              var27 = new AntiAdminSpectator.SpectatorThreat(
                                 var8.method_5628(),
                                 var8.method_5667(),
                                 var11,
                                 var16,
                                 var15,
                                 var13,
                                 var14,
                                 var9,
                                 var24,
                                 var26,
                                 new class_243(var8.method_23317(), var8.method_23318(), var8.method_23321())
                              );
                              this.activeThreats.put(var8.method_5667(), var27);
                           } else {
                              var27.distance = var9;
                              var27.angle = var24;
                              var27.isLookingAtYou = var26;
                              var27.pos = new class_243(var8.method_23317(), var8.method_23318(), var8.method_23321());
                              var27.lastSeen = var2;
                           }
                        }
                     }
                  }
               }
            }

            this.activeThreats
               .entrySet()
               .removeIf(e -> !var6.contains(e.getKey()) || var2 - ((AntiAdminSpectator.SpectatorThreat)e.getValue()).lastSeen > 2000L);
            if (var2 - this.lastTablistScanTime > 2000L) {
               this.lastTablistScanTime = var2;
               this.scanTablistForSpectators();
            }

            if (!this.activeThreats.isEmpty()) {
               AntiAdminSpectator.SpectatorThreat var28 = this.getPrimaryThreat();
               if (var28 != null) {
                  if (!this.hadThreatsPreviously) {
                     this.hadThreatsPreviously = true;
                     boolean var30 = Localization.isVietnamese();
                     String var31 = var28.role != null
                        ? var28.role.name().replace('_', ' ')
                        : (
                           var28.isStaff
                              ? "STAFF"
                              : "SPECTATOR"
                        );
                     this.pushOnScreenAlert(
                        new AntiAdminSpectator.OnScreenAlert(
                           var30
                              ? "⚠ CẢNH BÁO: ADMIN SPECTATOR ĐANG Ở GẦN BẠN!"
                              : "⚠ CRITICAL: ADMIN SPECTATOR DETECTED NEARBY! ⚠",
                           var30
                              ? "Phát hiện "
                                 + var28.name
                                 + " ("
                                 + var31
                                 + ") ở cự ly "
                                 + String.format(Locale.ROOT, "%.1fm", var28.distance)
                                 + "!"
                              : "Detected "
                                 + var28.name
                                 + " ("
                                 + var31
                                 + ") at "
                                 + String.format(Locale.ROOT, "%.1fm", var28.distance)
                                 + "!",
                           var28.isLookingAtYou
                              ? (
                                 var30
                                    ? "ĐANG NHÌN THẲNG VÀO BẠN!"
                                    : "LOOKING DIRECTLY AT YOU!"
                              )
                              : (
                                 var30
                                    ? "Đang lượn lờ trong khu vực!"
                                    : "Lurking in area!"
                              ),
                           var30
                              ? "✨ Chúc bạn may mắn để không bị ban! Hãy chơi an toàn! ✨"
                              : "✨ Stay calm and play safely to avoid bans! ✨",
                           -50384,
                           8000L,
                           "threat_detected"
                        )
                     );
                  }

                  this.triggerEmergencyAlarms(var28, var2);
               }
            } else {
               if (this.hadThreatsPreviously) {
                  this.hadThreatsPreviously = false;
                  boolean var29 = Localization.isVietnamese();
                  this.pushOnScreenAlert(
                     new AntiAdminSpectator.OnScreenAlert(
                        var29
                           ? "✅ ADMIN SPECTATOR ĐÃ RỜI ĐI"
                           : "✅ ADMIN SPECTATOR HAS LEFT",
                        var29
                           ? "Khu vực an toàn: Không còn Admin nào trong tầm quét nguy hiểm."
                           : "Safe perimeter: No admin threats in detection zone.",
                        var29
                           ? "Bạn có thể tiếp tục chơi bình thường nhưng hãy luôn đề cao cảnh giác!"
                           : "You can resume playing safely. Maintain caution!",
                        var29
                           ? "✨ Chúc bạn may mắn và chơi game vui vẻ không bị ban! ✨"
                           : "✨ Stay safe and enjoy the game! ✨",
                        -14498466,
                        6500L,
                        "threat_cleared"
                     )
                  );
               }

               this.panicTriggered = false;
            }

            this.onScreenAlerts.removeIf(a -> var2 - a.startTime > a.durationMs);
         } else {
            if (!this.activeThreats.isEmpty()) {
               this.activeThreats.clear();
            }
         }
      }
   }

   private void scanTablistForSpectators() {
      if (mc.method_1562() != null) {
         Collection var1 = mc.method_1562().method_2880();
         if (var1 != null) {
            for (class_640 var3 : var1) {
               if (var3.method_2958() == class_1934.field_9219) {
                  GameProfile var4 = var3.method_2966();
                  String var5 = var4 != null ? var4.name() : null;
                  if (var5 != null && (mc.field_1724 == null || !var5.equalsIgnoreCase(mc.field_1724.method_5477().getString()))) {
                     boolean var6 = StaffLists.isStaff(var5);
                     StaffLists.Role var7 = StaffLists.getStaffRole(var5);
                     if (var7 == null && var3.method_2971() != null) {
                        var7 = StaffLists.detectRankRole(var3.method_2971().getString());
                        if (var7 != null) {
                           var6 = true;
                        }
                     }

                     if ((var6 || (Boolean)this.detectAllSpectators.get()) && this.alertedServerSpectators.add(var5.toLowerCase(Locale.ROOT))) {
                        boolean var8 = Localization.isVietnamese();
                        String var9 = var7 != null
                           ? var7.name().replace('_', ' ')
                           : (
                              var6
                                 ? "STAFF"
                                 : "SPECTATOR"
                           );
                        if ((Boolean)this.chatAlert.get() && mc.field_1724 != null) {
                           mc.field_1724
                              .method_7353(
                                 class_2561.method_43470(
                                    var8
                                       ? "§e§l[AntiAdminSpectator] §6Chú ý: §e" + var5 + " §7(" + var9 + ") §6đang ở chế độ Spectator trên server!"
                                       : "§e§l[AntiAdminSpectator] §6Notice: §e" + var5 + " §7(" + var9 + ") §6is in Spectator mode on server!"
                                 ),
                                 false
                              );
                        }

                        this.pushOnScreenAlert(
                           new AntiAdminSpectator.OnScreenAlert(
                              var8
                                 ? "⚡ PHÁT HIỆN STAFF SPECTATOR TRÊN SERVER"
                                 : "⚡ STAFF SPECTATOR DETECTED ON SERVER",
                              var8
                                 ? "Người chơi: " + var5 + " (" + var9 + ") vừa vào chế độ Spectator!"
                                 : "Player: " + var5 + " (" + var9 + ") entered Spectator mode!",
                              var8
                                 ? "Cảnh báo: Staff có thể đang chuẩn bị dịch chuyển kiểm tra bạn!"
                                 : "Warning: Staff may be preparing to teleport to you!",
                              var8
                                 ? "✨ Chúc bạn may mắn để không bị ban! Hãy chơi cẩn thận! ✨"
                                 : "✨ Play carefully to avoid detection! ✨",
                              -680437,
                              8000L,
                              "tablist_" + var5.toLowerCase(Locale.ROOT)
                           )
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private AntiAdminSpectator.SpectatorThreat getPrimaryThreat() {
      AntiAdminSpectator.SpectatorThreat var1 = null;

      for (AntiAdminSpectator.SpectatorThreat var3 : this.activeThreats.values()) {
         if (var1 == null) {
            var1 = var3;
         } else if (var3.isStaff && !var1.isStaff) {
            var1 = var3;
         } else if (var3.isLookingAtYou && !var1.isLookingAtYou) {
            var1 = var3;
         } else if (var3.distance < var1.distance) {
            var1 = var3;
         }
      }

      return var1;
   }

   private void triggerEmergencyAlarms(AntiAdminSpectator.SpectatorThreat threat, long now) {
      String var4 = threat.role != null
         ? threat.role.name().replace('_', ' ')
         : (
            threat.isStaff
               ? "STAFF"
               : "SPECTATOR"
         );
      if ((Boolean)this.soundAlert.get() && now - this.lastSoundAlertTime > 2400L) {
         this.lastSoundAlertTime = now;
         if (mc.field_1724 != null) {
            mc.field_1724.method_5783((class_3414)class_3417.field_14793.comp_349(), 1.0F, 1.8F);
            mc.field_1724.method_5783((class_3414)class_3417.field_14622.comp_349(), 1.0F, 0.6F);
         }
      }

      if ((Boolean)this.chatAlert.get() && now - this.lastChatAlertTime > 7000L) {
         this.lastChatAlertTime = now;
         if (mc.field_1724 != null) {
            boolean var5 = Localization.isVietnamese();
            String var6 = threat.isLookingAtYou
               ? (
                  var5
                     ? "§c§lCÓ (Đang nhìn thẳng vào bạn!)"
                     : "§c§lYES (Facing you directly!)"
               )
               : (
                  var5
                     ? "§eGần bên (Chưa khóa góc nhìn)"
                     : "§eNearby (Not locked on)"
               );
            mc.field_1724
               .method_7353(
                  class_2561.method_43470(
                     var5
                        ? "§4§l[ANTI-ADMIN] §c§l⚠ CẢNH BÁO KHẨN CẤP: PHÁT HIỆN ADMIN SPECTATOR! ⚠"
                        : "§4§l[ANTI-ADMIN] §c§l⚠ CRITICAL ALERT: ADMIN SPECTATOR DETECTED! ⚠"
                  ),
                  false
               );
            mc.field_1724
               .method_7353(
                  class_2561.method_43470(
                     String.format(
                        Locale.ROOT,
                        var5
                           ? "§c§l[ANTI-ADMIN] §fTên: §e%s §7| §fChức vụ: §b%s §7| §fCự ly: §a%.1fm §7| §fGóc nhìn: %s"
                           : "§c§l[ANTI-ADMIN] §fName: §e%s §7| §fRank: §b%s §7| §fRange: §a%.1fm §7| §fFacing: %s",
                        threat.name,
                        var4,
                        threat.distance,
                        var6
                     )
                  ),
                  false
               );
            mc.field_1724
               .method_7353(
                  class_2561.method_43470(
                     var5
                        ? "§6§l[ANTI-ADMIN] §a✨ Chúc bạn may mắn để không bị ban! Hãy giữ bình tĩnh và chơi an toàn! ✨"
                        : "§6§l[ANTI-ADMIN] §a✨ Stay calm and play safely to avoid bans! ✨"
                  ),
                  false
               );
         }
      }

      if ((Boolean)this.toastAlert.get() && now - this.lastToastAlertTime > 6000L) {
         this.lastToastAlertTime = now;
         boolean var7 = Localization.isVietnamese();
         ToastManager.pushNotification(
            "ADMIN SPECTATOR",
            (
                  var7
                     ? "Phát hiện "
                     : "Detected "
               )
               + threat.name
               + " ("
               + var4
               + ")!",
            String.format(
               Locale.ROOT,
               var7
                  ? "%.1fm • Chúc bạn may mắn không bị ban!"
                  : "%.1fm • Play safely to avoid bans!",
               threat.distance
            ),
            "eye",
            "DANGER",
            false
         );
      }

      if ((Boolean)this.autoPanic.get() && !this.panicTriggered) {
         this.panicTriggered = true;
         this.executePanic();
      }

      if ((Boolean)this.autoDisconnect.get()
         && threat.distance <= ((Integer)this.disconnectRange.get()).intValue()
         && mc.method_1562() != null
         && mc.method_1562().method_48296() != null) {
         boolean var8 = Localization.isVietnamese();
         mc.method_1562()
            .method_48296()
            .method_10747(
               class_2561.method_43470(
                  var8
                     ? "§c§l[AntiAdminSpectator]\n§4⚠ Cảnh báo khẩn cấp: Admin "
                        + threat.name
                        + " ("
                        + var4
                        + ") đang spectator bạn ở cự ly "
                        + String.format(Locale.ROOT, "%.1f", threat.distance)
                        + "m!\n§a✨ Chúc bạn may mắn để không bị ban! ✨"
                     : "§c§l[AntiAdminSpectator]\n§4⚠ Critical Alert: Admin "
                        + threat.name
                        + " ("
                        + var4
                        + ") is spectating you at "
                        + String.format(Locale.ROOT, "%.1f", threat.distance)
                        + "m!\n§a✨ Disconnected for your safety! ✨"
               )
            );
      }
   }

   private void executePanic() {
      String[] var1 = new String[]{
         "AutoBuild",
         "AutoMine",
         "AutoBaseMining",
         "SpawnerBreaker",
         "TriggerBot",
         "AimAssist",
         "AutoCrystal",
         "AutoHitCrystal",
         "CartMacro",
         "AnchorMacro",
         "DoubleAnchor",
         "AutoSwordBotting",
         "Hitbox",
         "StaticHitboxes",
         "AutoJumpReset",
         "AutoCrop",
         "AutoTreeFarmer",
         "BoneDropper",
         "AHSniper",
         "Freecam",
         "SafeAnchor",
         "AutoDoubleHand",
         "AutoTotem",
         "AutoSell"
      };
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getModuleManager() != null) {
         int var3 = 0;

         for (String var7 : var1) {
            Module var8 = var2.getModuleManager().getModuleByName(var7);
            if (var8 != null && var8.isEnabled()) {
               var8.setEnabled(false);
               var3++;
            }
         }

         if (var3 > 0) {
            boolean var9 = Localization.isVietnamese();
            if (mc.field_1724 != null) {
               mc.field_1724
                  .method_7353(
                     class_2561.method_43470(
                        var9
                           ? "§e[AntiAdminSpectator] §aĐã tự động tắt " + var3 + " module lộ liễu để bảo vệ bạn khỏi bị ban!"
                           : "§e[AntiAdminSpectator] §aAutomatically disabled " + var3 + " blatant modules to protect you from bans!"
                     ),
                     false
                  );
            }

            this.pushOnScreenAlert(
               new AntiAdminSpectator.OnScreenAlert(
                  var9
                     ? "🛡️ ĐÃ TỰ ĐỘNG BẬT CHẾ ĐỘ AN TOÀN (SAFE MODE)"
                     : "🛡️ SAFE MODE ENGAGED AUTOMATICALLY",
                  var9 ? "Đã tự động tắt " + var3 + " module lộ liễu để bảo vệ bạn khỏi bị ban!" : "Disabled " + var3 + " blatant modules for your protection!",
                  var9
                     ? "Bao gồm: AutoCrystal, AutoMine, TriggerBot, AimAssist, SpawnerBreaker..."
                     : "Modules: AutoCrystal, AutoMine, TriggerBot, AimAssist...",
                  var9
                     ? "✨ Chúc bạn may mắn để không bị ban! Client đã an toàn tuyệt đối! ✨"
                     : "✨ Client state secured. Safe to continue! ✨",
                  -16718337,
                  9000L,
                  "panic_alert"
               )
            );
         }
      }
   }

   private String calculateDirection(class_243 threatPos) {
      if (mc.field_1724 != null && threatPos != null) {
         double var2 = threatPos.field_1352 - mc.field_1724.method_23317();
         double var4 = threatPos.field_1350 - mc.field_1724.method_23321();
         class_4184 var6 = mc.field_1773 != null ? mc.field_1773.method_19418() : null;
         float var7 = var6 != null ? var6.method_19330() : mc.field_1724.method_36454();
         float var8 = (float)Math.toDegrees(Math.atan2(var4, var2)) - 90.0F;
         float var9 = class_3532.method_15393(var8 - var7);
         boolean var10 = Localization.isVietnamese();
         if (Math.abs(var9) <= 22.5F) {
            return var10
               ? "↑ Phía trước"
               : "↑ Ahead";
         } else if (var9 > 22.5F && var9 <= 67.5F) {
            return var10
               ? "↗ Phía trước bên phải"
               : "↗ Ahead Right";
         } else if (var9 > 67.5F && var9 <= 112.5F) {
            return (
                  var10
                     ? "→ Bên phải ("
                     : "→ Right ("
               )
               + Math.round(Math.abs(var9))
               + "°)";
         } else if (var9 > 112.5F && var9 <= 157.5F) {
            return var10
               ? "↘ Phía sau bên phải"
               : "↘ Behind Right";
         } else if (Math.abs(var9) > 157.5F) {
            return (
                  var10
                     ? "↓ Phía sau lưng ("
                     : "↓ Behind ("
               )
               + Math.round(Math.abs(var9))
               + "°)";
         } else if (var9 < -22.5F && var9 >= -67.5F) {
            return var10
               ? "↖ Phía trước bên trái"
               : "↖ Ahead Left";
         } else if (var9 < -22.5F && var9 >= -112.5F) {
            return (
                  var10
                     ? "← Bên trái ("
                     : "← Left ("
               )
               + Math.round(Math.abs(var9))
               + "°)";
         } else {
            return var10
               ? "↙ Phía sau bên trái"
               : "↙ Behind Left";
         }
      } else {
         return "";
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private static int getRoleBadgeColor(StaffLists.Role role) {
      if (role == null) {
         return -13058568;
      }

      return switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$StaffLists$Role[role.ordinal()]) {
         case 1 -> -54742;
         case 2 -> -50384;
         case 3 -> -5745161;
         case 4 -> -680437;
         case 5 -> -15681151;
         default -> throw new MatchException(null, null);
      };
   }

   private static int lerpColor(int c1, int c2, float t) {
      t = class_3532.method_15363(t, 0.0F, 1.0F);
      int var3 = c1 >> (24) & (255);
      int var4 = c1 >> (16) & (255);
      int var5 = c1 >> (8) & (255);
      int var6 = c1 & (255);
      int var7 = c2 >> (24) & (255);
      int var8 = c2 >> (16) & (255);
      int var9 = c2 >> (8) & (255);
      int var10 = c2 & (255);
      int var11 = (int)(var3 + (var7 - var3) * t);
      int var12 = (int)(var4 + (var8 - var4) * t);
      int var13 = (int)(var5 + (var9 - var5) * t);
      int var14 = (int)(var6 + (var10 - var6) * t);
      return var11 << (24) | var12 << (16) | var13 << (8) | var14;
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (this.isEnabled() && (Boolean)this.screenBanner.get() && mc.field_1724 != null) {
         if (!this.activeThreats.isEmpty() || !this.onScreenAlerts.isEmpty()) {
            long var2 = System.currentTimeMillis();
            int var4 = mc.method_22683().method_4486();
            float var5 = (float)(Math.sin(var2 / 140.0) * 0.5 + 0.5);
            float var6 = (float)(Math.sin(var2 / 200.0) * 0.5 + 0.5);
            int var7 = lerpColor(-11870592, -278748, var6);

            try (NanoVGFrame var8 = NanoVGRenderer.frame()) {
               if (!var8.isActive()) {
                  return;
               }

               float var9 = 16.0F;
               AntiAdminSpectator.SpectatorThreat var10 = this.getPrimaryThreat();
               if (var10 != null) {
                  float var11 = Math.min(540.0F, var4 - 32.0F);
                  float var12 = 88.0F;
                  float var13 = (var4 - var11) / 2.0F;
                  int var14 = (int)(215.0F + 40.0F * var5);
                  int var15 = (int)(25.0F + 30.0F * var5);
                  int var16 = 0xFF000000 | var14 << 16 | var15 << 8 | 30;
                  int var17 = Math.round(45.0F * var5) << 24 | 16726832;
                  NanoVGRenderer.drawRoundedRect(var13 - 3.0F, var9 - 3.0F, var11 + 6.0F, var12 + 6.0F, 12.0F, var17);
                  NanoVGRenderer.drawRoundedRect(var13, var9, var11, var12, 10.0F, -200013297);
                  NanoVGRenderer.drawRoundedRectOutline(var13, var9, var11, var12, 10.0F, 2.0F, var16);
                  NanoVGRenderer.drawRoundedRectVarying(var13, var9, var11, 3.5F, 10.0F, 10.0F, 0.0F, 0.0F, var16);
                  float var18 = var13 + 12.0F;
                  float var19 = var9 + 12.0F;
                  float var20 = 46.0F;
                  int var21 = 889192448 | var14 << 16 | var15 << 8 | 30;
                  int var22 = -1728053248 | var14 << 16 | var15 << 8 | 30;
                  NanoVGRenderer.drawRoundedRect(var18, var19, var20, var20, 8.0F, var21);
                  NanoVGRenderer.drawRoundedRectOutline(var18, var19, var20, var20, 8.0F, 1.2F, var22);
                  NanoVGRenderer.drawCenteredText("⚠", var18 + var20 / 2.0F, var19 + 13.0F, 24.0F, -50384);
                  float var23 = var13 + 68.0F;
                  boolean var24 = Localization.isVietnamese();
                  NanoVGRenderer.drawText(
                     var24
                        ? "⚠ CẢNH BÁO KHẨN CẤP: PHÁT HIỆN ADMIN SPECTATOR! ⚠"
                        : "⚠ CRITICAL ALERT: ADMIN SPECTATOR DETECTED! ⚠",
                     var23,
                     var9 + 10.0F,
                     13.0F,
                     -47814,
                     true
                  );
                  StaffLists.Role var25 = var10.role;
                  String var26 = var25 != null
                     ? var25.name().replace('_', ' ')
                     : (
                        var10.isStaff
                           ? "STAFF"
                           : "SPECTATOR"
                     );
                  int var27 = getRoleBadgeColor(var25);
                  float var28 = NanoVGRenderer.getTextWidth(var26, 9.5F) + 10.0F;
                  NanoVGRenderer.drawRoundedRect(var23, var9 + 28.0F, var28, 14.0F, 4.0F, 855638016 | var27 & 16777215);
                  NanoVGRenderer.drawRoundedRectOutline(var23, var9 + 28.0F, var28, 14.0F, 4.0F, 1.0F, -872415232 | var27 & 16777215);
                  NanoVGRenderer.drawText(var26, var23 + 5.0F, var9 + 29.0F, 9.5F, var27, true);
                  float var29 = var23 + var28 + 7.0F;
                  NanoVGRenderer.drawText(var10.name, var29, var9 + 29.0F, 11.0F, -1, true);
                  float var30 = NanoVGRenderer.getTextWidth(var10.name, 11.0F);
                  float var31 = var29 + var30 + 8.0F;
                  String var32 = String.format(Locale.ROOT, "%.1fm", var10.distance);
                  int var33 = var10.distance <= 15.0 ? -47814 : (var10.distance <= 35.0 ? -680437 : -14498466);
                  NanoVGRenderer.drawText(var32, var31, var9 + 29.0F, 11.0F, var33, true);
                  float var34 = var31 + NanoVGRenderer.getTextWidth(var32, 11.0F) + 8.0F;
                  String var35 = var10.isLookingAtYou
                     ? (
                        var24
                           ? "[🔴 ĐANG NHÌN BẠN!]"
                           : "[🔴 FACING YOU!]"
                     )
                     : (
                        var24
                           ? "[🟡 Gần bên]"
                           : "[🟡 Nearby]"
                     );
                  int var36 = var10.isLookingAtYou ? -50384 : -680437;
                  NanoVGRenderer.drawText(var35, var34, var9 + 29.0F, 11.0F, var36, true);
                  float var37 = var34 + NanoVGRenderer.getTextWidth(var35, 11.0F) + 6.0F;
                  String var38 = this.calculateDirection(var10.pos);
                  if (!var38.isEmpty()) {
                     NanoVGRenderer.drawText("(" + var38 + ")", var37, var9 + 29.0F, 10.5F, -7035976, false);
                  }

                  if (this.panicTriggered) {
                     NanoVGRenderer.drawText(
                        var24
                           ? "🛡️ Chế độ an toàn: Đã tự động tắt các module nguy hiểm để bảo vệ bạn!"
                           : "🛡️ Safe Mode: Critical modules disabled to protect your account!",
                        var23,
                        var9 + 47.0F,
                        10.5F,
                        -13058568,
                        false
                     );
                  } else {
                     NanoVGRenderer.drawText(
                        var24
                           ? "⚡ Hãy ngừng ngay mọi hành vi bất thường và tỏ ra bình thường!"
                           : "⚡ Cease suspicious actions immediately and act natural!",
                        var23,
                        var9 + 47.0F,
                        10.5F,
                        -3418655,
                        false
                     );
                  }

                  NanoVGRenderer.drawText(
                     var24
                        ? "✨ Chúc bạn may mắn để không bị ban! Hãy giữ bình tĩnh và chơi an toàn! ✨"
                        : "✨ Stay calm and play safely to avoid detection! ✨",
                     var23,
                     var9 + 64.0F,
                     11.5F,
                     var7,
                     true
                  );
                  var9 += var12 + 8.0F;
               }

               for (AntiAdminSpectator.OnScreenAlert var42 : this.onScreenAlerts) {
                  float var43 = var42.getAlpha(var2);
                  if (!(var43 <= 0.01F)) {
                     float var44 = Math.min(500.0F, var4 - 32.0F);
                     float var45 = 64.0F;
                     float var46 = (var4 - var44) / 2.0F;
                     int var47 = (int)(240.0F * var43) << 24 | 987932;
                     int var48 = (int)(180.0F * var43) << 24 | var42.accentColor & 16777215;
                     int var49 = (int)(255.0F * var43) << 24 | var42.accentColor & 16777215;
                     NanoVGRenderer.drawRoundedRect(var46, var9, var44, var45, 8.0F, var47);
                     NanoVGRenderer.drawRoundedRectOutline(var46, var9, var44, var45, 8.0F, 1.2F, var48);
                     NanoVGRenderer.drawRoundedRectVarying(var46, var9, 4.0F, var45, 8.0F, 0.0F, 0.0F, 8.0F, var49);
                     float var50 = var46 + 12.0F;
                     float var51 = var9 + (var45 - 34.0F) / 2.0F;
                     int var52 = (int)(50.0F * var43) << 24 | var42.accentColor & 16777215;
                     NanoVGRenderer.drawRoundedRect(var50, var51, 34.0F, 34.0F, 6.0F, var52);
                     NanoVGRenderer.drawRoundedRectOutline(var50, var51, 34.0F, 34.0F, 6.0F, 1.0F, var48);
                     String var53 = !var42.title.contains("SAFE")
                           && !var42.title.contains("AN TOÀN")
                        ? (var42.title.contains("RỜI ĐI") ? "✓" : "⚡")
                        : "🛡";
                     NanoVGRenderer.drawCenteredText(var53, var50 + 17.0F, var51 + 9.0F, 18.0F, var49);
                     float var54 = var46 + 54.0F;
                     int var55 = (int)(255.0F * var43) << 24 | var42.accentColor & 16777215;
                     NanoVGRenderer.drawText(var42.title, var54, var9 + 8.0F, 12.0F, var55, true);
                     int var56 = (int)(235.0F * var43) << 24 | 15527921;
                     NanoVGRenderer.drawText(var42.message, var54, var9 + 24.0F, 10.5F, var56, false);
                     int var57 = (int)(255.0F * var43) << 24 | var7 & 16777215;
                     NanoVGRenderer.drawText(var42.blessing, var54, var9 + 41.0F, 10.8F, var57, true);
                     float var58 = var42.getProgress(var2);
                     float var59 = (var44 - 12.0F) * var58;
                     int var60 = (int)(140.0F * var43) << 24 | var42.accentColor & 16777215;
                     NanoVGRenderer.drawRoundedRect(var46 + 6.0F, var9 + var45 - 2.5F, var59, 2.0F, 1.0F, var60);
                     var9 += var45 + 7.0F;
                  }
               }
            }
         }
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

   public static class OnScreenAlert {
      public final String id;
      public final String title;
      public final String message;
      public final String subMessage;
      public final String blessing;
      public final int accentColor;
      public final long startTime;
      public final long durationMs;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public OnScreenAlert(String title, String message, String subMessage, String blessing, int accentColor, long durationMs, String id) {
         this.id = idx;
         this.title = titlex;
         this.message = messagex;
         this.subMessage = subMessagex;
         this.blessing = blessingx;
         this.accentColor = accentColorx;
         this.startTime = System.currentTimeMillis();
         this.durationMs = durationMsx;
      }

      public float getAlpha(long now) {
         long var3 = now - this.startTime;
         if (var3 > this.durationMs) {
            return 0.0F;
         }

         if (var3 < 250L) {
            return (float)var3 / 250.0F;
         }

         long var5 = this.durationMs - var3;
         return var5 < 500L ? Math.max(0.0F, (float)var5 / 500.0F) : 1.0F;
      }

      public float getProgress(long now) {
         long var3 = now - this.startTime;
         return class_3532.method_15363(1.0F - (float)var3 / (float)this.durationMs, 0.0F, 1.0F);
      }
   }

   public static class SpectatorThreat {
      public final int entityId;
      public final UUID uuid;
      public final String name;
      public final StaffLists.Role role;
      public final boolean isStaff;
      public final boolean isSpectatorMode;
      public final boolean isInvisible;
      public double distance;
      public double angle;
      public boolean isLookingAtYou;
      public class_243 pos;
      public final long firstDetected;
      public long lastSeen;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public SpectatorThreat(
         int entityId,
         UUID uuid,
         String name,
         StaffLists.Role role,
         boolean isStaff,
         boolean isSpectatorMode,
         boolean isInvisible,
         double distance,
         double angle,
         boolean isLookingAtYou,
         class_243 pos
      ) {
         this.entityId = entityIdx;
         this.uuid = uuidx;
         this.name = namex;
         this.role = rolex;
         this.isStaff = isStaffx;
         this.isSpectatorMode = isSpectatorModex;
         this.isInvisible = isInvisiblex;
         this.distance = distancex;
         this.angle = anglex;
         this.isLookingAtYou = isLookingAtYoux;
         this.pos = posx;
         this.firstDetected = System.currentTimeMillis();
         this.lastSeen = this.firstDetected;
      }
   }
}
