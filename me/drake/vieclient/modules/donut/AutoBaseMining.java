package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.List;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ButtonSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.KeybindSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.core.setting.StringSetting;
import me.drake.vieclient.modules.donut.autobase.camera.AutoBaseCamera;
import me.drake.vieclient.modules.donut.autobase.checkpoint.JobBudgetManager;
import me.drake.vieclient.modules.donut.autobase.hud.AutoBaseHudWidget;
import me.drake.vieclient.modules.donut.autobase.navigation.BaritoneNavigationAdapter;
import me.drake.vieclient.modules.donut.autobase.navigation.StandPositionFinder;
import me.drake.vieclient.modules.donut.autobase.navigation.BaritoneNavigationAdapter.NavigationStatus;
import me.drake.vieclient.modules.donut.autobase.planner.MiningPlanner;
import me.drake.vieclient.util.DiscordWebhook;
import me.drake.vieclient.util.InventoryUtil;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_3726;
import net.minecraft.class_3965;
import net.minecraft.class_7923;
import net.minecraft.class_2350.class_2351;
import net.minecraft.class_239.class_240;

public class AutoBaseMining extends Module {
   private final ButtonSetting markCorner1Button = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Mark Corner 1",
         "Mark first region corner at target block",
         this::markCorner1FromCrosshair
      )
   );
   private final KeybindSetting markCorner1Key = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Mark Corner 1 Key",
         "Keybind to mark first region corner at crosshair or feet"
      )
   );
   private final ButtonSetting markCorner2Button = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Mark Corner 2",
         "Mark second region corner at target block",
         this::markCorner2FromCrosshair
      )
   );
   private final KeybindSetting markCorner2Key = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Mark Corner 2 Key",
         "Keybind to mark second region corner at crosshair or feet"
      )
   );
   private final ButtonSetting resetRegionButton = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Reset Region",
         "Clear selected region",
         this::resetRegion
      )
   );
   private final KeybindSetting resetRegionKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Reset Region Key",
         "Keybind to clear selected region"
      )
   );
   private final ButtonSetting startButton = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Start Mining",
         "Start the excavation operation",
         this::startMining
      )
   );
   private final KeybindSetting startKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Start Key",
         "Keybind to start the excavation operation"
      )
   );
   private final ButtonSetting pauseResumeButton = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Pause/Resume",
         "Pause or resume the mining operation",
         this::togglePauseResume
      )
   );
   private final KeybindSetting pauseResumeKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Pause/Resume Key",
         "Keybind to pause or resume the mining operation"
      )
   );
   private final ButtonSetting stopButton = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Stop Mining",
         "Stop mining and release all controls",
         this::stopMining
      )
   );
   private final KeybindSetting stopKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Stop Key",
         "Keybind to stop mining and release all controls"
      )
   );
   private final EnumSetting toolType = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Tool Type",
         "Mining tool mode: Normal Pickaxe (1x1) or Drill (3x1 with TunnelBaseFinder camera logic)",
         AutoBaseMining.ToolType.NORMAL_PICKAXE,
         AutoBaseMining.ToolType.values()
      )
   );
   private final EnumSetting patternSetting = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Pattern",
         "Excavation pattern",
         MiningPlanner.MiningPattern.LAYER_BY_LAYER,
         MiningPlanner.MiningPattern.values()
      )
   );
   private final BoolSetting protectContainers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Protect Containers",
         "Prevent breaking chests, shulker boxes, barrels",
         true
      )
   );
   private final BoolSetting protectSpawners = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Protect Spawners",
         "Prevent breaking monster spawners",
         true
      )
   );
   private final IntSetting durabilityThreshold = (IntSetting)this.addSetting(
      new IntSetting(
         "Durability Threshold %",
         "Preserve tools when durability falls below this percentage",
         10,
         1,
         30
      )
   );
   private final DoubleSetting maxReach = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Reach Distance",
         "Maximum mining reach distance",
         4.2,
         3.0,
         5.0
      )
   );
   private final Setting discordNotifications = this.addSetting(
      new BoolSetting(
         "Discord Notifications",
         "Send webhook notifications for mining events.",
         false
      )
   );
   private final Setting discordWebhookUrl = this.addSetting(
      new StringSetting(
         "Webhook URL",
         "Discord webhook URL (stored in plain text in the config).",
         ""
      )
   );
   private final BoolSetting autoMove = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Move",
         "Automatically walk and navigate to mining targets",
         true
      )
   );
   private final MiningPlanner planner = new MiningPlanner();
   private final BaritoneNavigationAdapter navigator = new BaritoneNavigationAdapter();
   private final AutoBaseCamera camera = new AutoBaseCamera();
   private final JobBudgetManager budgetManager = new JobBudgetManager();
   private final TunnelMotion.BreakWatchdog breakWatchdog = new TunnelMotion.BreakWatchdog();
   private class_2338 corner1 = null;
   private class_2338 corner2 = null;
   private AutoBaseMining.MiningState state = AutoBaseMining.MiningState.IDLE;
   private String blockingReason = "";
   private class_2338 currentTarget = null;
   private class_2338 currentNavTarget = null;
   private int navStuckTicks = 0;
   private class_243 lastNavPos = null;
   private class_243 breakAimPoint = null;
   private class_2338 breakAimTarget = null;
   private class_243 lastMiningFocus = null;
   private int miningFocusGraceFrames = 0;
   private int breakingTicks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoBaseMining() {
      super(
         "AutoBaseMining",
         "Automated base excavation with safety checks and tool preservation",
         Category.DONUT_SMP
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.camera.reset();
      this.breakWatchdog.reset();
      this.breakAimPoint = null;
      this.breakAimTarget = null;
      this.lastMiningFocus = null;
      this.miningFocusGraceFrames = 0;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.stopMining();
   }

   public void markCorner1FromCrosshair() {
      if (mc.field_1765 instanceof class_3965 var1 && var1.method_17783() == class_240.field_1332) {
         this.corner1 = var1.method_17777();
         VieClientMod.LOGGER
            .info(
               "[AutoBaseMining] Corner 1 marked at {}",
               this.corner1
            );
      } else if (mc.field_1724 != null) {
         this.corner1 = class_2338.method_49637(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
         VieClientMod.LOGGER
            .info(
               "[AutoBaseMining] Corner 1 marked at player feet {}",
               this.corner1
            );
      }

      if (mc.field_1724 != null && this.corner1 != null) {
         mc.field_1724.method_7353(class_2561.method_43470("§d[AutoBaseMining]§a Corner 1 marked at " + this.corner1.method_23854()), true);
      }
   }

   public void markCorner2FromCrosshair() {
      if (mc.field_1765 instanceof class_3965 var1 && var1.method_17783() == class_240.field_1332) {
         this.corner2 = var1.method_17777();
         VieClientMod.LOGGER
            .info(
               "[AutoBaseMining] Corner 2 marked at {}",
               this.corner2
            );
      } else if (mc.field_1724 != null) {
         this.corner2 = class_2338.method_49637(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
         VieClientMod.LOGGER
            .info(
               "[AutoBaseMining] Corner 2 marked at player feet {}",
               this.corner2
            );
      }

      if (mc.field_1724 != null && this.corner2 != null) {
         mc.field_1724.method_7353(class_2561.method_43470("§d[AutoBaseMining]§a Corner 2 marked at " + this.corner2.method_23854()), true);
      }
   }

   public void resetRegion() {
      this.corner1 = null;
      this.corner2 = null;
      this.stopMining();
      VieClientMod.LOGGER
         .info(
            "[AutoBaseMining] Region reset"
         );
      if (mc.field_1724 != null) {
         mc.field_1724
            .method_7353(
               class_2561.method_43470(
                  "§d[AutoBaseMining]§c Region reset"
               ),
               true
            );
      }
   }

   public void startMining() {
      if (this.corner1 != null && this.corner2 != null) {
         if (!this.isEnabled()) {
            this.setEnabled(true);
         }

         MiningPlanner.ToolMode var1 = this.toolType.get() == AutoBaseMining.ToolType.DRILL ? MiningPlanner.ToolMode.DRILL_3X1 : MiningPlanner.ToolMode.NORMAL;
         this.planner
            .setRegion(
               this.corner1,
               this.corner2,
               (MiningPlanner.MiningPattern)this.patternSetting.get(),
               var1,
               (Boolean)this.protectContainers.get(),
               (Boolean)this.protectSpawners.get()
            );
         this.state = AutoBaseMining.MiningState.MINING;
         this.blockingReason = "";
         this.breakingTicks = 0;
         this.currentTarget = null;
         this.budgetManager.startJob();
         VieClientMod.LOGGER
            .info(
               "[AutoBaseMining] Started excavation of {} blocks with tool {}",
               this.planner.getTotalCount(),
               this.toolType.get()
            );
         if (mc.field_1724 != null) {
            mc.field_1724
               .method_7353(
                  class_2561.method_43470("§d[AutoBaseMining]§a Started mining (" + this.planner.getTotalCount() + " blocks, " + this.toolType.get() + ")"),
                  true
               );
         }

         this.sendDiscordNotification(
            "Mining Started",
            "Excavating region of " + this.planner.getTotalCount() + " blocks using " + this.toolType.get()
         );
      } else {
         this.blockingReason = "Both corners must be selected first";
         if (mc.field_1724 != null) {
            mc.field_1724
               .method_7353(
                  class_2561.method_43470(
                     "§d[AutoBaseMining]§c Both corners must be selected first!"
                  ),
                  true
               );
         }
      }
   }

   private void sendDiscordNotification(String title, String message) {
      if ((Boolean)this.discordNotifications.get()) {
         String var3 = (String)this.discordWebhookUrl.get();
         if (DiscordWebhook.isValidWebhookUrl(var3)) {
            DiscordWebhook.send((String)this.discordWebhookUrl.get(), null, new DiscordWebhook.Embed(title, message, 65280, null, null));
         }
      }
   }

   public void togglePauseResume() {
      if (this.state == AutoBaseMining.MiningState.PAUSED) {
         this.state = AutoBaseMining.MiningState.MINING;
         this.budgetManager.resumeJob();
         this.blockingReason = "";
         if (mc.field_1724 != null) {
            mc.field_1724
               .method_7353(
                  class_2561.method_43470(
                     "§d[AutoBaseMining]§a Mining resumed"
                  ),
                  true
               );
         }
      } else if (this.state != AutoBaseMining.MiningState.IDLE && this.state != AutoBaseMining.MiningState.DONE) {
         this.state = AutoBaseMining.MiningState.PAUSED;
         this.budgetManager.pauseJob();
         this.releaseMiningControls();
         this.navigator.cancelNavigation();
         if (mc.field_1724 != null) {
            mc.field_1724
               .method_7353(
                  class_2561.method_43470(
                     "§d[AutoBaseMining]§e Mining paused"
                  ),
                  true
               );
         }
      }
   }

   public void stopMining() {
      this.state = AutoBaseMining.MiningState.IDLE;
      this.blockingReason = "";
      this.currentTarget = null;
      this.breakingTicks = 0;
      this.releaseMiningControls();
      this.navigator.cancelNavigation();
      this.budgetManager.stopJob();
      this.updateHudSnapshot();
      if (mc.field_1724 != null) {
         byte[] var10001 = new byte[37];
         var10001[0] = (byte)(-69);
         var10001[1] = (byte)(61);
         var10001[2] = (byte)(120);
         var10001[3] = (byte)(-123);
         var10001[4] = (byte)(-108);
         var10001[5] = (byte)(122);
         var10001[6] = (byte)(5);
         var10001[7] = (byte)(117);
         var10001[8] = (byte)(83);
         var10001[9] = (byte)(-61);
         var10001[10] = (byte)(-105);
         var10001[11] = (byte)(-109);
         var10001[12] = (byte)(-64);
         var10001[13] = (byte)(-66);
         var10001[14] = (byte)(55);
         var10001[15] = (byte)(11);
         var10001[16] = (byte)(71);
         var10001[17] = (byte)(45);
         var10001[18] = (byte)(-111);
         var10001[19] = (byte)(108);
         var10001[20] = (byte)(-62);
         var10001[21] = (byte)(-100);
         var10001[22] = 1;
         var10001[23] = (byte)(7);
         var10001[24] = (byte)(-88);
         var10001[25] = (byte)(124);
         var10001[26] = (byte)(-3);
         var10001[27] = (byte)(40);
         var10001[28] = (byte)(58);
         var10001[29] = (byte)(-89);
         var10001[30] = (byte)(122);
         var10001[31] = (byte)(-26);
         var10001[32] = (byte)(-10);
         var10001[33] = (byte)(74);
         var10001[34] = (byte)(12);
         var10001[35] = (byte)(27);
         var10001[36] = (byte)(81);
         mc.field_1724.method_7353(class_2561.method_43470($$v_s(var10001, 3635198112277188700L)), true);
      }
   }

   private void releaseMovementKeys() {
      if (mc.field_1690 != null) {
         mc.field_1690.field_1894.method_23481(false);
         mc.field_1690.field_1881.method_23481(false);
         mc.field_1690.field_1913.method_23481(false);
         mc.field_1690.field_1849.method_23481(false);
         mc.field_1690.field_1903.method_23481(false);
         mc.field_1690.field_1867.method_23481(false);
      }
   }

   private void releaseMiningControls() {
      if (mc.field_1690 != null) {
         mc.field_1690.field_1886.method_23481(false);
      }

      this.releaseMovementKeys();
      if (mc.field_1761 != null) {
         mc.field_1761.method_2925();
      }

      this.breakAimPoint = null;
      this.breakAimTarget = null;
      this.lastMiningFocus = null;
      this.miningFocusGraceFrames = 0;
      this.currentTarget = null;
      this.currentNavTarget = null;
      this.navStuckTicks = 0;
      this.lastNavPos = null;
      this.breakWatchdog.reset();
      this.camera.reset();
      ActionArbiter var1 = ActionArbiter.get();
      if (var1 != null) {
         var1.releaseAll(ActionArbiter.Owner.AUTO_BASE_MINE);
      }
   }

   @Override
   public void onTick() {
      if (this.isInGame()) {
         this.updateHudSnapshot();
         if (this.state != AutoBaseMining.MiningState.IDLE && this.state != AutoBaseMining.MiningState.DONE && this.state != AutoBaseMining.MiningState.PAUSED) {
            if (mc.field_1755 != null) {
               if (mc.field_1690 != null) {
                  mc.field_1690.field_1886.method_23481(false);
               }

               this.releaseMovementKeys();
            } else {
               String var1 = this.budgetManager.checkLimits();
               if (var1 != null) {
                  this.state = AutoBaseMining.MiningState.PAUSED;
                  this.blockingReason = var1;
                  this.releaseMiningControls();
                  this.navigator.cancelNavigation();
                  this.budgetManager.pauseJob();
               } else if (this.planner.isFinished()) {
                  this.state = AutoBaseMining.MiningState.DONE;
                  this.stopMining();
               } else {
                  ActionArbiter var2 = ActionArbiter.get();
                  if (var2 != null) {
                     boolean var3 = var2.tryAcquireAll(
                        ActionArbiter.Owner.AUTO_BASE_MINE,
                        2,
                        ActionArbiter.Resource.HOTBAR_SLOT,
                        ActionArbiter.Resource.ATTACK_KEY,
                        ActionArbiter.Resource.CAMERA,
                        ActionArbiter.Resource.MOVEMENT
                     );
                     if (!var3) {
                        return;
                     }
                  }

                  class_243 var13 = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
                  if (this.currentTarget == null) {
                     this.currentTarget = this.planner.getNextTarget(var13, (Double)this.maxReach.get(), pos -> {
                        if (mc.field_1687 == null) {
                           byte[] var10000 = new byte[13];
                           var10000[0] = (byte)(20);
                           var10000[1] = (byte)(-13);
                           var10000[2] = (byte)(114);
                           var10000[3] = (byte)(-69);
                           var10000[4] = (byte)(-74);
                           var10000[5] = (byte)(125);
                           var10000[6] = (byte)(16);
                           var10000[7] = (byte)(124);
                           var10000[8] = (byte)(101);
                           var10000[9] = (byte)(-104);
                           var10000[10] = (byte)(-123);
                           var10000[11] = (byte)(-97);
                           var10000[12] = (byte)(-1);
                           return $$v_s(var10000, 3635198112277188700L);
                        } else {
                           class_2680 var1x = mc.field_1687.method_8320(pos);
                           return class_7923.field_41175.method_10221(var1x.method_26204()).toString();
                        }
                     });
                     if (this.currentTarget == null) {
                        this.currentTarget = this.planner.getNextNavTarget(var13, pos -> {
                           if (mc.field_1687 == null) {
                              byte[] var10000 = new byte[13];
                              var10000[0] = (byte)(20);
                              var10000[1] = (byte)(-13);
                              var10000[2] = (byte)(114);
                              var10000[3] = (byte)(-69);
                              var10000[4] = (byte)(-74);
                              var10000[5] = (byte)(125);
                              var10000[6] = (byte)(16);
                              var10000[7] = (byte)(124);
                              var10000[8] = (byte)(101);
                              var10000[9] = (byte)(-104);
                              var10000[10] = (byte)(-123);
                              var10000[11] = (byte)(-97);
                              var10000[12] = (byte)(-1);
                              return $$v_s(var10000, 3635198112277188700L);
                           } else {
                              class_2680 var1x = mc.field_1687.method_8320(pos);
                              return class_7923.field_41175.method_10221(var1x.method_26204()).toString();
                           }
                        });
                     }

                     if (this.currentTarget == null) {
                        if (this.planner.getBlockedCount() > 0) {
                           this.state = AutoBaseMining.MiningState.BLOCKED;
                           this.blockingReason = "All remaining blocks blocked or protected";
                        } else {
                           this.state = AutoBaseMining.MiningState.DONE;
                        }

                        this.releaseMiningControls();
                        return;
                     }

                     this.breakingTicks = 0;
                  }

                  boolean var4 = false;
                  if (mc.field_1687 != null && mc.field_1687.method_8320(this.currentTarget).method_26215()) {
                     var4 = true;
                  }

                  if (var4) {
                     if (this.toolType.get() == AutoBaseMining.ToolType.DRILL) {
                        if (mc.field_1687 != null) {
                           for (class_2338 var6 : this.planner.getDrillFootprint(this.currentTarget)) {
                              if (mc.field_1687.method_8320(var6).method_26215()) {
                                 this.planner.markMined(var6);
                              }
                           }
                        }

                        this.planner.markMined(this.currentTarget);
                     } else {
                        this.planner.markMined(this.currentTarget);
                     }

                     this.budgetManager.recordProgress();
                     this.currentTarget = null;
                     this.breakAimPoint = null;
                     this.breakAimTarget = null;
                     this.lastMiningFocus = null;
                     this.miningFocusGraceFrames = 0;
                     this.breakWatchdog.reset();
                     this.breakingTicks = 0;
                     if (mc.field_1690 != null) {
                        mc.field_1690.field_1886.method_23481(false);
                     }
                  } else {
                     double var5 = var13.method_1028(
                        this.currentTarget.method_10263() + 0.5, this.currentTarget.method_10264() + 0.5, this.currentTarget.method_10260() + 0.5
                     );
                     if (this.state == AutoBaseMining.MiningState.NAVIGATING) {
                        if (BaritoneNavigationAdapter.isBaritoneAvailable()) {
                           if (var5 <= (Double)this.maxReach.get() * (Double)this.maxReach.get()) {
                              this.navigator.cancelNavigation();
                              this.releaseMovementKeys();
                              this.state = AutoBaseMining.MiningState.MINING;
                           }
                        } else {
                           this.tickNativeNavigation(var13);
                           if (this.state == AutoBaseMining.MiningState.NAVIGATING) {
                              return;
                           }
                        }
                     }

                     if (var5 > (Double)this.maxReach.get() * (Double)this.maxReach.get()) {
                        if (mc.field_1690 != null) {
                           mc.field_1690.field_1886.method_23481(false);
                        }

                        this.breakAimPoint = null;
                        List var15 = StandPositionFinder.findCandidateStandPositions(
                           mc.field_1687, mc.field_1724, this.currentTarget, null, (Double)this.maxReach.get()
                        );
                        if (var15.isEmpty()) {
                           this.planner
                              .markBlocked(
                                 this.currentTarget,
                                 "No valid standing position found"
                              );
                           this.currentTarget = null;
                           this.currentNavTarget = null;
                           this.releaseMovementKeys();
                           this.breakWatchdog.reset();
                        } else {
                           class_2338 var16 = (class_2338)var15.get(0);
                           this.currentNavTarget = var16;
                           this.state = AutoBaseMining.MiningState.NAVIGATING;
                           if (BaritoneNavigationAdapter.isBaritoneAvailable()) {
                              NavigationStatus var17 = this.navigator.navigateTo(mc, var16);
                              if (var17 == NavigationStatus.PATHING) {
                                 return;
                              }
                           }

                           this.tickNativeNavigation(var13);
                        }
                     } else {
                        if ((Boolean)this.autoMove.get()) {
                           this.tickMiningMovement(var13);
                        } else {
                           this.releaseMovementKeys();
                        }

                        class_2680 var7 = mc.field_1687.method_8320(this.currentTarget);
                        boolean var8 = this.selectAndPreserveTool(var7);
                        if (!var8) {
                           this.state = AutoBaseMining.MiningState.BLOCKED;
                           this.blockingReason = "Tool durability depleted (< " + this.durabilityThreshold.get() + "%)";
                           this.releaseMiningControls();
                        } else {
                           this.state = AutoBaseMining.MiningState.MINING;
                           class_239 var9 = mc.field_1724.method_5745((Double)this.maxReach.get(), 1.0F, false);
                           boolean var10 = false;
                           if (var9 instanceof class_3965 var11 && var9.method_17783() == class_240.field_1332) {
                              class_2338 var12 = var11.method_17777();
                              if (var12.equals(this.currentTarget)) {
                                 var10 = true;
                                 if (!this.currentTarget.equals(this.breakAimTarget) || this.breakAimPoint == null) {
                                    this.breakAimTarget = this.currentTarget.method_10062();
                                    this.breakAimPoint = var11.method_17784();
                                 }
                              } else if (this.toolType.get() == AutoBaseMining.ToolType.DRILL
                                 && this.planner.getDrillFootprint(this.currentTarget).contains(var12)) {
                                 var10 = true;
                                 if (!this.currentTarget.equals(this.breakAimTarget) || this.breakAimPoint == null) {
                                    this.breakAimTarget = this.currentTarget.method_10062();
                                    this.breakAimPoint = var11.method_17784();
                                 }
                              }
                           }

                           if (var10) {
                              if (mc.field_1690 != null) {
                                 mc.field_1690.field_1886.method_23481(true);
                              }
                           } else if (mc.field_1690 != null) {
                              mc.field_1690.field_1886.method_23481(false);
                           }

                           this.breakingTicks++;
                           if (this.breakWatchdog.tick(this.currentTarget, mc.field_1687, mc.field_1724)) {
                              if (this.budgetManager.registerRetry(this.currentTarget)) {
                                 this.breakWatchdog.reset();
                                 this.breakingTicks = 0;
                              } else {
                                 this.planner
                                    .markBlocked(
                                       this.currentTarget,
                                       "Block break timeout (hardness/anti-cheat)"
                                    );
                                 this.currentTarget = null;
                                 this.breakAimPoint = null;
                                 this.breakAimTarget = null;
                                 this.lastMiningFocus = null;
                                 this.miningFocusGraceFrames = 0;
                                 this.breakWatchdog.reset();
                                 this.breakingTicks = 0;
                                 if (mc.field_1690 != null) {
                                    mc.field_1690.field_1886.method_23481(false);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean selectAndPreserveTool(class_2680 state) {
      if (mc.field_1724 == null) {
         return false;
      }

      int var2 = -1;
      float var3 = 1.0F;
      boolean var4 = this.toolType.get() == AutoBaseMining.ToolType.DRILL;

      for (int var5 = 0; var5 < (9); var5++) {
         class_1799 var6 = mc.field_1724.method_31548().method_5438(var5);
         if (!var6.method_7960()) {
            if (var6.method_7963()) {
               int var7 = var6.method_7936();
               int var8 = var6.method_7919();
               int var9 = var7 - var8;
               double var10 = (double)var9 / var7 * 100.0;
               if (var10 < ((Integer)this.durabilityThreshold.get()).intValue() || var9 <= (10)) {
                  continue;
               }
            }

            float var14 = var6.method_7924(statex);
            String var10000 = var6.method_7964().getString().toLowerCase();
            byte[] var10001 = new byte[5];
            var10001[0] = (byte)(29);
            var10001[1] = (byte)(-24);
            var10001[2] = (byte)(117);
            var10001[3] = (byte)(-78);
            var10001[4] = (byte)(-71);
            boolean var15 = var10000.contains($$v_s(var10001, 3635198112277188700L));
            if (var4 && var15) {
               var14 += 1000.0F;
            }

            if (var14 > var3) {
               var3 = var14;
               var2 = var5;
            }
         }
      }

      if (var2 != (-1)) {
         InventoryUtil.swap(var2);
         return true;
      }

      class_1799 var12 = mc.field_1724.method_6047();
      if (var12.method_7963()) {
         int var13 = var12.method_7936() - var12.method_7919();
         if ((double)var13 / var12.method_7936() * 100.0 < ((Integer)this.durabilityThreshold.get()).intValue()) {
            return false;
         }
      }

      return true;
   }

   public EnumSetting getToolTypeSetting() {
      return this.toolType;
   }

   public AutoBaseMining.ToolType getToolType() {
      return (AutoBaseMining.ToolType)this.toolType.get();
   }

   @Override
   public void onKeyPress(int keyCode) {
      if (this.isInGame()) {
         if (this.markCorner1Key.isBound() && (Integer)this.markCorner1Key.get() == keyCode) {
            this.markCorner1FromCrosshair();
         } else if (this.markCorner2Key.isBound() && (Integer)this.markCorner2Key.get() == keyCode) {
            this.markCorner2FromCrosshair();
         } else if (this.resetRegionKey.isBound() && (Integer)this.resetRegionKey.get() == keyCode) {
            this.resetRegion();
         } else if (this.startKey.isBound() && (Integer)this.startKey.get() == keyCode) {
            this.startMining();
         } else if (this.pauseResumeKey.isBound() && (Integer)this.pauseResumeKey.get() == keyCode) {
            this.togglePauseResume();
         } else if (this.stopKey.isBound() && (Integer)this.stopKey.get() == keyCode) {
            this.stopMining();
         }
      }
   }

   public KeybindSetting getMarkCorner1Key() {
      return this.markCorner1Key;
   }

   public KeybindSetting getMarkCorner2Key() {
      return this.markCorner2Key;
   }

   public KeybindSetting getResetRegionKey() {
      return this.resetRegionKey;
   }

   public KeybindSetting getStartKey() {
      return this.startKey;
   }

   public KeybindSetting getPauseResumeKey() {
      return this.pauseResumeKey;
   }

   public KeybindSetting getStopKey() {
      return this.stopKey;
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (this.isInGame() && event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (this.corner1 != null && this.corner2 != null) {
            int var2 = Math.min(this.corner1.method_10263(), this.corner2.method_10263());
            int var3 = Math.max(this.corner1.method_10263(), this.corner2.method_10263()) + 1;
            int var4 = Math.min(this.corner1.method_10264(), this.corner2.method_10264());
            int var5 = Math.max(this.corner1.method_10264(), this.corner2.method_10264()) + 1;
            int var6 = Math.min(this.corner1.method_10260(), this.corner2.method_10260());
            int var7 = Math.max(this.corner1.method_10260(), this.corner2.method_10260()) + 1;
            class_238 var8 = new class_238(var2, var4, var6, var3, var5, var7);
            RenderUtil.drawBox(var8, -866779307);
            RenderUtil.drawFilledBox(var8, 626392917);
         }

         if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1755 == null) {
            if (this.state == AutoBaseMining.MiningState.MINING && this.currentTarget != null) {
               this.steerMiningCamera(event.getFrameTime());
            } else if (this.state == AutoBaseMining.MiningState.NAVIGATING && this.currentNavTarget != null && !BaritoneNavigationAdapter.isBaritoneAvailable()
               )
             {
               this.steerNavCamera(event.getFrameTime());
            }
         }
      }
   }

   private void steerNavCamera(double frameTime) {
      if (!(frameTime <= 0.0) && mc.field_1724 != null && this.currentNavTarget != null) {
         class_243 var3 = mc.field_1724.method_33571();
         double var4 = this.currentNavTarget.method_10263() + 0.5 - var3.field_1352;
         double var6 = this.currentNavTarget.method_10264() + 0.5 - var3.field_1351;
         double var8 = this.currentNavTarget.method_10260() + 0.5 - var3.field_1350;
         double var10 = Math.hypot(var4, var8);
         float var12 = class_3532.method_15393((float)Math.toDegrees(Math.atan2(var8, var4)) - 90.0F);
         float var13 = class_3532.method_15363((float)(-Math.toDegrees(Math.atan2(var6, var10))), -45.0F, 45.0F);
         float var14 = mc.field_1690 != null ? ((Double)mc.field_1690.method_42495().method_41753()).floatValue() : 0.5F;
         double var15 = 200.0;
         float var17 = mc.field_1724.method_36454();
         float var18 = mc.field_1724.method_36455();
         float var19 = TunnelMotion.stepYaw(var17, var12, frameTime, var15, var14);
         float var20 = TunnelMotion.stepPitch(var18, var13, frameTime, var15, var14);
         mc.field_1724.method_36456(var19);
         mc.field_1724.method_36457(var20);
      }
   }

   private void steerMiningCamera(double frameTime) {
      if (!(frameTime <= 0.0) && mc.field_1724 != null && this.currentTarget != null) {
         class_243 var3 = mc.field_1724.method_33571();
         class_243 var4;
         if (this.currentTarget.equals(this.breakAimTarget) && this.breakAimPoint != null) {
            this.lastMiningFocus = this.breakAimPoint;
            this.miningFocusGraceFrames = 10;
            var4 = this.breakAimPoint;
         } else if (this.miningFocusGraceFrames > 0 && this.lastMiningFocus != null) {
            this.miningFocusGraceFrames--;
            var4 = this.lastMiningFocus;
         } else {
            this.lastMiningFocus = null;
            var4 = class_243.method_24953(this.currentTarget);
         }

         double var5 = var4.field_1352 - var3.field_1352;
         double var7 = var4.field_1351 - var3.field_1351;
         double var9 = var4.field_1350 - var3.field_1350;
         double var11 = Math.hypot(var5, var9);
         float var13;
         float var14;
         if (this.toolType.get() == AutoBaseMining.ToolType.DRILL) {
            class_243 var15 = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
            float var16 = this.planner.getRouteYaw(this.currentTarget, var15);
            float var17 = class_3532.method_15363((float)(-Math.toDegrees(Math.atan2(var7, var11))), -90.0F, 90.0F);
            float var18 = class_3532.method_15393((float)Math.toDegrees(Math.atan2(var9, var5)) - 90.0F);
            float var19 = Math.abs(class_3532.method_15393(var18 - var16)) <= 25.0F ? var16 : var18;
            class_243 var20 = this.currentTarget.equals(this.breakAimTarget) && this.breakAimPoint != null ? this.breakAimPoint : var4;
            TunnelMiningView.Aim var21 = TunnelMiningView.aim(var3, var20, var19, var17);
            var13 = var21.yaw();
            var14 = var21.pitch();
         } else {
            var13 = class_3532.method_15393((float)Math.toDegrees(Math.atan2(var9, var5)) - 90.0F);
            var14 = class_3532.method_15363((float)(-Math.toDegrees(Math.atan2(var7, var11))), -90.0F, 90.0F);
         }

         float var22 = mc.field_1690 != null ? ((Double)mc.field_1690.method_42495().method_41753()).floatValue() : 0.5F;
         double var23 = 180.0;
         float var24 = mc.field_1724.method_36454();
         float var25 = mc.field_1724.method_36455();
         float var26 = TunnelMotion.stepYaw(var24, var13, frameTime, var23, var22);
         float var27 = TunnelMotion.stepPitch(var25, var14, frameTime, var23, var22);
         mc.field_1724.method_36456(var26);
         mc.field_1724.method_36457(var27);
      }
   }

   private void tickNativeNavigation(class_243 pPos) {
      if (this.currentNavTarget != null && mc.field_1724 != null && mc.field_1690 != null) {
         double var2 = this.currentNavTarget.method_10263() + 0.5 - pPos.field_1352;
         double var4 = this.currentNavTarget.method_10260() + 0.5 - pPos.field_1350;
         double var6 = this.currentNavTarget.method_10264() - mc.field_1724.method_23318();
         double var8 = var2 * var2 + var4 * var4;
         if (var8 <= 0.16000000000000003 && Math.abs(var6) <= 0.6) {
            this.releaseMovementKeys();
            this.currentNavTarget = null;
            this.navStuckTicks = 0;
            this.state = AutoBaseMining.MiningState.MINING;
         } else {
            if (this.currentTarget != null) {
               double var10 = pPos.method_1028(
                  this.currentTarget.method_10263() + 0.5, this.currentTarget.method_10264() + 0.5, this.currentTarget.method_10260() + 0.5
               );
               if (var10 <= ((Double)this.maxReach.get() - 0.6) * ((Double)this.maxReach.get() - 0.6)) {
                  this.releaseMovementKeys();
                  this.currentNavTarget = null;
                  this.navStuckTicks = 0;
                  this.state = AutoBaseMining.MiningState.MINING;
                  return;
               }
            }

            if (this.lastNavPos != null && pPos.method_1025(this.lastNavPos) < 0.005) {
               this.navStuckTicks++;
               if (this.navStuckTicks > 30 && mc.field_1724.method_24828()) {
                  mc.field_1690.field_1903.method_23481(true);
               }

               if (this.navStuckTicks > 70) {
                  this.releaseMovementKeys();
                  if (this.currentTarget != null) {
                     this.planner
                        .markBlocked(
                           this.currentTarget,
                           "Navigation path obstructed"
                        );
                     this.currentTarget = null;
                  }

                  this.currentNavTarget = null;
                  this.navStuckTicks = 0;
                  this.state = AutoBaseMining.MiningState.MINING;
                  return;
               }
            } else {
               this.navStuckTicks = 0;
            }

            this.lastNavPos = pPos;
            float var13 = (float)Math.toDegrees(Math.atan2(var4, var2)) - 90.0F;
            float var11 = mc.field_1724.method_36454();
            float var12 = Math.abs(class_3532.method_15393(var13 - var11));
            if (var12 <= 35.0F && this.isSafeToStep(pPos, var11, null)) {
               mc.field_1690.field_1894.method_23481(true);
               mc.field_1690.field_1867.method_23481(TunnelMotion.shouldSprint(mc.field_1724));
               this.checkAutoJump(this.currentNavTarget);
            } else {
               mc.field_1690.field_1894.method_23481(false);
               mc.field_1690.field_1867.method_23481(false);
               if (var12 <= 35.0F) {
                  this.checkAutoJump(this.currentNavTarget);
               } else {
                  mc.field_1690.field_1903.method_23481(false);
               }
            }
         }
      } else {
         this.releaseMovementKeys();
         this.state = AutoBaseMining.MiningState.MINING;
      }
   }

   private void tickMiningMovement(class_243 pPos) {
      if (mc.field_1724 != null && mc.field_1690 != null && this.currentTarget != null) {
         double var2 = pPos.method_1028(
            this.currentTarget.method_10263() + 0.5, this.currentTarget.method_10264() + 0.5, this.currentTarget.method_10260() + 0.5
         );
         if (var2 > 3.61) {
            if (this.isSafeToStep(pPos, mc.field_1724.method_36454(), this.currentTarget)) {
               mc.field_1690.field_1894.method_23481(true);
               mc.field_1690.field_1867.method_23481(false);
               this.checkAutoJump(this.currentTarget);
            } else {
               mc.field_1690.field_1894.method_23481(false);
               mc.field_1690.field_1903.method_23481(false);
            }
         } else {
            mc.field_1690.field_1894.method_23481(false);
            mc.field_1690.field_1903.method_23481(false);
         }

         if (this.toolType.get() == AutoBaseMining.ToolType.DRILL && this.planner.getStripAxis() != null) {
            if (this.planner.getStripAxis() == class_2351.field_11051) {
               double var4 = this.currentTarget.method_10263() + 0.5;
               double var6 = var4 - pPos.field_1352;
               if (Math.abs(var6) > 0.22) {
                  double var8 = Math.toRadians(mc.field_1724.method_36454());
                  double var10 = var6 * Math.cos(var8);
                  if (var10 > 0.15) {
                     mc.field_1690.field_1913.method_23481(true);
                     mc.field_1690.field_1849.method_23481(false);
                  } else if (var10 < -0.15) {
                     mc.field_1690.field_1913.method_23481(false);
                     mc.field_1690.field_1849.method_23481(true);
                  } else {
                     mc.field_1690.field_1913.method_23481(false);
                     mc.field_1690.field_1849.method_23481(false);
                  }
               } else {
                  mc.field_1690.field_1913.method_23481(false);
                  mc.field_1690.field_1849.method_23481(false);
               }
            } else {
               double var12 = this.currentTarget.method_10260() + 0.5;
               double var13 = var12 - pPos.field_1350;
               if (Math.abs(var13) > 0.22) {
                  double var14 = Math.toRadians(mc.field_1724.method_36454());
                  double var15 = var13 * Math.sin(var14);
                  if (var15 > 0.15) {
                     mc.field_1690.field_1913.method_23481(true);
                     mc.field_1690.field_1849.method_23481(false);
                  } else if (var15 < -0.15) {
                     mc.field_1690.field_1913.method_23481(false);
                     mc.field_1690.field_1849.method_23481(true);
                  } else {
                     mc.field_1690.field_1913.method_23481(false);
                     mc.field_1690.field_1849.method_23481(false);
                  }
               } else {
                  mc.field_1690.field_1913.method_23481(false);
                  mc.field_1690.field_1849.method_23481(false);
               }
            }
         } else {
            mc.field_1690.field_1913.method_23481(false);
            mc.field_1690.field_1849.method_23481(false);
         }
      } else {
         this.releaseMovementKeys();
      }
   }

   private boolean isSafeToStep(class_243 currentPos, float yaw, class_2338 avoidBlock) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         double var4 = Math.toRadians(yaw);
         double var6 = -Math.sin(var4) * 0.6;
         double var8 = Math.cos(var4) * 0.6;
         double var10 = currentPos.field_1352 + var6;
         double var12 = currentPos.field_1350 + var8;
         class_2338 var14 = class_2338.method_49637(var10, currentPos.field_1351, var12);
         if (avoidBlock == null || !var14.equals(avoidBlock) && !var14.method_10084().equals(avoidBlock)) {
            class_2338 var15 = var14.method_10084();
            class_2680 var16 = mc.field_1687.method_8320(var15);
            if (!var16.method_26194(mc.field_1687, var15, class_3726.method_16194()).method_1110()) {
               return false;
            }

            class_2680 var17 = mc.field_1687.method_8320(var14);
            if (var17.method_26204() != class_2246.field_10164 && var17.method_26204() != class_2246.field_10036) {
               class_2338 var18 = var14.method_10074();
               class_2338 var19 = var14.method_10087(2);
               class_2680 var20 = mc.field_1687.method_8320(var18);
               class_2680 var21 = mc.field_1687.method_8320(var19);
               boolean var22 = !var20.method_26220(mc.field_1687, var18).method_1110();
               boolean var23 = !var21.method_26220(mc.field_1687, var19).method_1110();
               if (avoidBlock != null && var18.equals(avoidBlock)) {
                  var22 = false;
               }

               boolean var24 = var22 || var23;
               return !var24 ? false : var20.method_26204() != class_2246.field_10164 && var21.method_26204() != class_2246.field_10164;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private void checkAutoJump(class_2338 target) {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         if (!mc.field_1724.method_24828()) {
            mc.field_1690.field_1903.method_23481(false);
         } else {
            if (target != null && target.method_10264() > mc.field_1724.method_31478()) {
               double var2 = mc.field_1724.method_5649(target.method_10263() + 0.5, mc.field_1724.method_23318(), target.method_10260() + 0.5);
               if (var2 < 3.24) {
                  class_2338 var4 = target.method_10074();
                  class_2680 var5 = mc.field_1687.method_8320(var4);
                  if (!var5.method_26220(mc.field_1687, var4).method_1110()) {
                     mc.field_1690.field_1903.method_23481(true);
                     return;
                  }
               }
            }

            if (this.state == AutoBaseMining.MiningState.NAVIGATING && mc.field_1724.field_5976) {
               class_2338 var6 = mc.field_1724.method_24515().method_10086(2);
               class_2680 var3 = mc.field_1687.method_8320(var6);
               if (var3.method_26194(mc.field_1687, var6, class_3726.method_16194()).method_1110()) {
                  mc.field_1690.field_1903.method_23481(true);
                  return;
               }
            }

            mc.field_1690.field_1903.method_23481(false);
         }
      }
   }

   private void updateHudSnapshot() {
      long var1 = this.budgetManager.getElapsedWallTimeMs();
      long var3 = this.budgetManager.getRemainingContinuousWorkMs();
      double var5 = var1 > 5000L ? this.planner.getMinedCount() * 60000.0 / var1 : 0.0;
      byte[] var10000 = new byte[14];
      var10000[0] = (byte)(58);
      var10000[1] = (byte)(-5);
      var10000[2] = (byte)(112);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(-96);
      var10000[5] = (byte)(99);
      var10000[6] = (byte)(16);
      var10000[7] = (byte)(110);
      var10000[8] = (byte)(120);
      var10000[9] = (byte)(-52);
      var10000[10] = (byte)(-125);
      var10000[11] = (byte)(-40);
      var10000[12] = (byte)(-93);
      var10000[13] = (byte)(-7);
      String var7 = $$v_s(var10000, 3635198112277188700L);
      if (var5 > 0.5 && this.planner.getRemainingCount() > 0) {
         long var8 = (long)(this.planner.getRemainingCount() / (var5 / 60.0));
         var10000 = new byte[9];
         var10000[0] = (byte)(92);
         var10000[1] = (byte)(-86);
         var10000[2] = (byte)(46);
         var10000[3] = (byte)(-70);
         var10000[4] = (byte)(-17);
         var10000[5] = (byte)(42);
         var10000[6] = (byte)(65);
         var10000[7] = (byte)(40);
         var10000[8] = (byte)(117);
         String var11 = $$v_s(var10000, 3635198112277188700L);
         Object[] var10001 = new Object[2];
         var10001[0] = var8 / 60L;
         var10001[1] = var8 % 60L;
         var7 = String.format(var11, var10001);
      }

      AutoBaseHudWidget.updateSnapshot(
         new AutoBaseHudWidget.HudSnapshot(
            true,
            "Mining Volume (" + ((AutoBaseMining.ToolType)this.toolType.get()).toString() + ")",
            this.state.name(),
            this.planner.getMinedCount(),
            this.planner.getTotalCount(),
            this.planner.getPendingCount(),
            this.planner.getBlockedCount(),
            var1,
            var3,
            var5,
            var7,
            "",
            this.blockingReason
         )
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

   public enum MiningState {
      IDLE,
      NAVIGATING,
      MINING,
      PAUSED,
      BLOCKED,
      DONE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoBaseMining.MiningState[] $values() {
         AutoBaseMining.MiningState[] var10000 = new AutoBaseMining.MiningState[6];
         var10000[0] = IDLE;
         var10000[1] = NAVIGATING;
         var10000[2] = MINING;
         var10000[3] = PAUSED;
         var10000[4] = BLOCKED;
         var10000[5] = DONE;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-34);
         var10002[2] = (byte)(80);
         var10002[3] = (byte)(-101);
         IDLE = new AutoBaseMining.MiningState();
         var10002 = new byte[10];
         var10002[0] = (byte)(115);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(74);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(47);
         var10002[5] = (byte)(-97);
         var10002[6] = (byte)(82);
         var10002[7] = (byte)(-10);
         var10002[8] = (byte)(27);
         var10002[9] = (byte)(-27);
         NAVIGATING = new AutoBaseMining.MiningState();
         var10002 = new byte[6];
         var10002[0] = (byte)(112);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(38);
         var10002[5] = (byte)(-103);
         MINING = new AutoBaseMining.MiningState();
         var10002 = new byte[6];
         var10002[0] = (byte)(109);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(73);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(45);
         var10002[5] = (byte)(-102);
         PAUSED = new AutoBaseMining.MiningState();
         var10002 = new byte[7];
         var10002[0] = (byte)(127);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(35);
         var10002[5] = (byte)(-101);
         var10002[6] = (byte)(66);
         BLOCKED = new AutoBaseMining.MiningState();
         var10002 = new byte[4];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         DONE = new AutoBaseMining.MiningState();
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

   public enum ToolType {
      NORMAL_PICKAXE,
      DRILL;

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ToolType(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static AutoBaseMining.ToolType[] $values() {
         AutoBaseMining.ToolType[] var10000 = new AutoBaseMining.ToolType[2];
         var10000[0] = NORMAL_PICKAXE;
         var10000[1] = DRILL;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[14];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(34);
         var10002[5] = (byte)(63);
         var10002[6] = (byte)(-13);
         var10002[7] = (byte)(96);
         var10002[8] = (byte)(30);
         var10002[9] = (byte)(-31);
         var10002[10] = (byte)(-81);
         var10002[11] = (byte)(-73);
         var10002[12] = (byte)(99);
         var10002[13] = (byte)(-18);
         String var0 = $$v_s(var10002, 1778831530898214938L);
         byte[] var10004 = new byte[14];
         var10004[0] = (byte)(113);
         var10004[1] = (byte)(-11);
         var10004[2] = (byte)(110);
         var10004[3] = (byte)(-77);
         var10004[4] = (byte)(2);
         var10004[5] = (byte)(31);
         var10004[6] = (byte)(-116);
         var10004[7] = (byte)(96);
         var10004[8] = (byte)(62);
         var10004[9] = (byte)(-63);
         var10004[10] = (byte)(-113);
         var10004[11] = (byte)(-105);
         var10004[12] = (byte)(67);
         var10004[13] = (byte)(-50);
         NORMAL_PICKAXE = new AutoBaseMining.ToolType($$v_s(var10004, 1778831530898214938L));
         var10002 = new byte[5];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-56);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(47);
         String var2 = $$v_s(var10002, 1778831530898214938L);
         var10004 = new byte[5];
         var10004[0] = (byte)(123);
         var10004[1] = (byte)(-24);
         var10004[2] = (byte)(117);
         var10004[3] = (byte)(-78);
         var10004[4] = (byte)(15);
         DRILL = new AutoBaseMining.ToolType($$v_s(var10004, 1778831530898214938L));
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
}
