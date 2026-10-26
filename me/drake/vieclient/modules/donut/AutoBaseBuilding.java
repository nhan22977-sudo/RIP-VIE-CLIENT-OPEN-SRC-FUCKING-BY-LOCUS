package me.drake.vieclient.modules.donut;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.ServerBlockUpdateEvent;
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
import me.drake.vieclient.modules.donut.autobase.economy.AutoBuyController;
import me.drake.vieclient.modules.donut.autobase.executor.PlacementStage;
import me.drake.vieclient.modules.donut.autobase.executor.PlacementTarget;
import me.drake.vieclient.modules.donut.autobase.executor.SneakPlaceExecutor;
import me.drake.vieclient.modules.donut.autobase.hud.AutoBaseHudWidget;
import me.drake.vieclient.modules.donut.autobase.navigation.BaritoneNavigationAdapter;
import me.drake.vieclient.modules.donut.autobase.navigation.StandPositionFinder;
import me.drake.vieclient.modules.donut.autobase.navigation.BaritoneNavigationAdapter.NavigationStatus;
import me.drake.vieclient.modules.donut.autobase.planner.BuildPlanner;
import me.drake.vieclient.modules.donut.autobase.schematic.LitematicaParser;
import me.drake.vieclient.modules.donut.autobase.schematic.SchematicData;
import me.drake.vieclient.modules.donut.autobase.schematic.SchematicTransform;
import me.drake.vieclient.util.DiscordWebhook;
import me.drake.vieclient.util.InventoryUtil;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2415;
import net.minecraft.class_243;
import net.minecraft.class_2470;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_3726;
import net.minecraft.class_7923;

public class AutoBaseBuilding extends Module {
   private final StringSetting schematicPath = (StringSetting)this.addSetting(
      new StringSetting(
         "Schematic Path",
         "Path to the .litematic file",
         "schematics/base.litematic"
      )
   );
   private final EnumSetting rotationSetting = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Rotation",
         "Blueprint rotation",
         class_2470.field_11467,
         class_2470.values()
      )
   );
   private final EnumSetting mirrorSetting = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Mirror",
         "Blueprint mirror",
         class_2415.field_11302,
         class_2415.values()
      )
   );
   private final DoubleSetting maxReach = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Reach Distance",
         "Maximum placement reach",
         4.2,
         3.0,
         5.0
      )
   );
   private final BoolSetting autoBuyEnabled = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Auto Buy Materials",
         "Automatically buy missing items via /ah",
         true
      )
   );
   private final IntSetting maxOverbuy = (IntSetting)this.addSetting(
      new IntSetting(
         "Max Overbuy",
         "Maximum allowed extra items per AH listing (0 = strict exact)",
         0,
         0,
         64
      )
   );
   private final IntSetting reserveSlots = (IntSetting)this.addSetting(
      new IntSetting(
         "Reserve Slots",
         "Minimum empty inventory slots to preserve",
         2,
         0,
         8
      )
   );
   private final IntSetting maxSpendBudget = (IntSetting)this.addSetting(
      new IntSetting(
         "Max Spend Budget",
         "Maximum in-game currency to spend on AH",
         1000000,
         1000,
         50000000
      )
   );
   private final Setting discordNotifications = this.addSetting(
      new BoolSetting(
         "Discord Notifications",
         "Send Discord webhook when build starts/completes.",
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
   private final ButtonSetting setOriginButton = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Set Origin",
         "Set schematic origin at current player feet",
         this::setOriginAtPlayer
      )
   );
   private final KeybindSetting setOriginKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Set Origin Key",
         "Keybind to set blueprint origin at player feet"
      )
   );
   private final ButtonSetting loadSchematicButton = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Load Schematic",
         "Parse and prepare the blueprint file",
         this::loadSchematicFromFile
      )
   );
   private final ButtonSetting startButton = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Start",
         "Start building process",
         this::startBuilding
      )
   );
   private final KeybindSetting startKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Start Key",
         "Keybind to start the building operation"
      )
   );
   private final ButtonSetting pauseResumeButton = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Pause/Resume",
         "Pause or resume the building operation",
         this::togglePauseResume
      )
   );
   private final KeybindSetting pauseResumeKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Pause/Resume Key",
         "Keybind to pause or resume the building operation"
      )
   );
   private final ButtonSetting stopButton = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Stop",
         "Stop building and release all resources",
         this::stopBuilding
      )
   );
   private final KeybindSetting stopKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Stop Key",
         "Keybind to stop building and release all resources"
      )
   );
   private final BuildPlanner planner = new BuildPlanner();
   private final SneakPlaceExecutor executor = new SneakPlaceExecutor();
   private final BaritoneNavigationAdapter navigator = new BaritoneNavigationAdapter();
   private final AutoBaseCamera camera = new AutoBaseCamera();
   private final AutoBuyController autoBuy = new AutoBuyController();
   private final JobBudgetManager budgetManager = new JobBudgetManager();
   private SchematicData currentSchematic = null;
   private class_2338 originPos = null;
   private AutoBaseBuilding.BuildState state = AutoBaseBuilding.BuildState.IDLE;
   private String blockingReason = "";
   private BuildPlanner.PlannedBuildTask activePlannedTask = null;
   private class_2338 currentNavTarget = null;
   private int navStuckTicks = 0;
   private class_243 lastNavPos = null;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoBaseBuilding() {
      super(
         "AutoBaseBuilding",
         "Automated structural base construction from Litematica schematics",
         Category.DONUT_SMP
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.camera.reset();
      this.autoBuy
         .configure(
            (Boolean)this.autoBuyEnabled.get(),
            (Integer)this.maxOverbuy.get(),
            (Integer)this.reserveSlots.get(),
            50000.0,
            ((Integer)this.maxSpendBudget.get()).intValue()
         );
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.stopBuilding();
   }

   public void setOriginAtPlayer() {
      if (mc.field_1724 != null) {
         this.originPos = class_2338.method_49637(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
         byte[] var10001 = new byte[35];
         var10001[0] = (byte)(34);
         var10001[1] = (byte)(-37);
         var10001[2] = (byte)(105);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(30);
         var10001[5] = (byte)(31);
         var10001[6] = (byte)(-95);
         var10001[7] = (byte)(-126);
         var10001[8] = (byte)(116);
         var10001[9] = (byte)(-32);
         var10001[10] = (byte)(-111);
         var10001[11] = (byte)(-97);
         var10001[12] = (byte)(69);
         var10001[13] = (byte)(-31);
         var10001[14] = (byte)(-127);
         var10001[15] = (byte)(-25);
         var10001[16] = (byte)(78);
         var10001[17] = (byte)(23);
         var10001[18] = (byte)(-20);
         var10001[19] = (byte)(-3);
         var10001[20] = (byte)(-92);
         var10001[21] = (byte)(-39);
         var10001[22] = (byte)(-80);
         var10001[23] = (byte)(-50);
         var10001[24] = (byte)(-77);
         var10001[25] = (byte)(123);
         var10001[26] = (byte)(-13);
         var10001[27] = (byte)(47);
         var10001[28] = (byte)(-105);
         var10001[29] = (byte)(-11);
         var10001[30] = (byte)(-52);
         var10001[31] = (byte)(22);
         var10001[32] = (byte)(-71);
         var10001[33] = (byte)(65);
         var10001[34] = 1;
         VieClientMod.LOGGER.info($$v_s(var10001, -2755213650754809764L), this.originPos);
      }
   }

   public void loadSchematicFromFile() {
      File var1 = new File((String)this.schematicPath.get());
      if (!var1.exists()) {
         var1 = new File("schematics", (String)this.schematicPath.get());
      }

      if (!var1.exists()) {
         VieClientMod.LOGGER
            .error(
               "[AutoBaseBuilding] File not found: {}",
               this.schematicPath.get()
            );
         this.blockingReason = "File not found: " + (String)this.schematicPath.get();
      } else {
         try {
            this.currentSchematic = LitematicaParser.parse(var1);
            SchematicTransform var2 = new SchematicTransform((class_2470)this.rotationSetting.get(), (class_2415)this.mirrorSetting.get());
            class_2338 var3 = this.originPos != null
               ? this.originPos
               : (
                  mc.field_1724 != null
                     ? class_2338.method_49637(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321())
                     : class_2338.field_10980
               );
            this.planner.loadSchematic(this.currentSchematic, var3, var2);
            VieClientMod.LOGGER
               .info(
                  "[AutoBaseBuilding] Loaded schematic '{}' ({} blocks)",
                  this.currentSchematic.name(),
                  this.planner.getTotalCount()
               );
            this.blockingReason = "";
         } catch (Throwable var4) {
            VieClientMod.LOGGER
               .error(
                  "[AutoBaseBuilding] Failed to parse schematic: ",
                  var4
               );
            this.blockingReason = "Parse error: " + var4.getMessage();
         }
      }
   }

   public void startBuilding() {
      if (this.currentSchematic == null || this.planner.getTotalCount() == 0) {
         this.loadSchematicFromFile();
         if (this.currentSchematic == null) {
            return;
         }
      }

      if (this.originPos == null && mc.field_1724 != null) {
         this.setOriginAtPlayer();
      }

      this.state = AutoBaseBuilding.BuildState.PLACING;
      this.blockingReason = "";
      this.budgetManager.startJob();
      VieClientMod.LOGGER
         .info(
            "[AutoBaseBuilding] Building started"
         );
      this.sendDiscordNotification(
         "Building Started",
         "Blueprint: "
            + (this.currentSchematic != null ? this.currentSchematic.name() : "unknown")
      );
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
      if (this.state == AutoBaseBuilding.BuildState.PAUSED) {
         this.state = AutoBaseBuilding.BuildState.PLACING;
         this.budgetManager.resumeJob();
         this.blockingReason = "";
         if (mc.field_1724 != null) {
            mc.field_1724
               .method_7353(
                  class_2561.method_43470(
                     "§d[AutoBaseBuilding]§a Building resumed"
                  ),
                  true
               );
         }
      } else if (this.state != AutoBaseBuilding.BuildState.IDLE && this.state != AutoBaseBuilding.BuildState.DONE) {
         this.state = AutoBaseBuilding.BuildState.PAUSED;
         this.budgetManager.pauseJob();
         this.executor.release(mc, ActionArbiter.get());
         this.navigator.cancelNavigation();
         this.releaseMovementKeys();
         this.currentNavTarget = null;
         this.navStuckTicks = 0;
         this.lastNavPos = null;
         if (mc.field_1724 != null) {
            mc.field_1724
               .method_7353(
                  class_2561.method_43470(
                     "§d[AutoBaseBuilding]§e Building paused"
                  ),
                  true
               );
         }
      }
   }

   public void stopBuilding() {
      this.state = AutoBaseBuilding.BuildState.IDLE;
      this.blockingReason = "";
      this.activePlannedTask = null;
      this.executor.release(mc, ActionArbiter.get());
      this.navigator.cancelNavigation();
      this.releaseMovementKeys();
      this.currentNavTarget = null;
      this.navStuckTicks = 0;
      this.lastNavPos = null;
      this.autoBuy.cancelAndReset(mc);
      this.budgetManager.stopJob();
      this.updateHudSnapshot();
      if (mc.field_1724 != null) {
         byte[] var10001 = new byte[41];
         var10001[0] = (byte)(-69);
         var10001[1] = (byte)(61);
         var10001[2] = (byte)(120);
         var10001[3] = (byte)(-123);
         var10001[4] = (byte)(48);
         var10001[5] = (byte)(40);
         var10001[6] = (byte)(-76);
         var10001[7] = (byte)(-98);
         var10001[8] = (byte)(83);
         var10001[9] = (byte)(-61);
         var10001[10] = (byte)(-105);
         var10001[11] = (byte)(-109);
         var10001[12] = (byte)(107);
         var10001[13] = (byte)(-16);
         var10001[14] = (byte)(-127);
         var10001[15] = (byte)(-27);
         var10001[16] = (byte)(77);
         var10001[17] = (byte)(35);
         var10001[18] = (byte)(-94);
         var10001[19] = (byte)(-55);
         var10001[20] = (byte)(-100);
         var10001[21] = (byte)(111);
         var10001[22] = (byte)(55);
         var10001[23] = (byte)(-62);
         var10001[24] = (byte)(-31);
         var10001[25] = (byte)(80);
         var10001[26] = (byte)(-31);
         var10001[27] = (byte)(47);
         var10001[28] = (byte)(-107);
         var10001[29] = (byte)(-79);
         var10001[30] = (byte)(-47);
         var10001[31] = (byte)(23);
         var10001[32] = (byte)(-2);
         var10001[33] = (byte)(26);
         var10001[34] = (byte)(15);
         var10001[35] = (byte)(10);
         var10001[36] = (byte)(-2);
         var10001[37] = (byte)(-115);
         var10001[38] = (byte)(16);
         var10001[39] = (byte)(116);
         var10001[40] = (byte)(-43);
         mc.field_1724.method_7353(class_2561.method_43470($$v_s(var10001, -2755213650754809764L)), true);
      }
   }

   @EventHandler
   public void onServerBlockUpdate(ServerBlockUpdateEvent event) {
      this.executor.onServerBlockUpdate(event);
   }

   @Override
   public void onTick() {
      if (this.isInGame()) {
         this.updateHudSnapshot();
         if (this.state != AutoBaseBuilding.BuildState.IDLE
            && this.state != AutoBaseBuilding.BuildState.DONE
            && this.state != AutoBaseBuilding.BuildState.PAUSED) {
            if (this.state != AutoBaseBuilding.BuildState.BUYING_AH && mc.field_1755 != null) {
               this.releaseMovementKeys();
            } else {
               String var1 = this.budgetManager.checkLimits();
               if (var1 != null) {
                  this.state = AutoBaseBuilding.BuildState.PAUSED;
                  this.blockingReason = var1;
                  this.executor.release(mc, ActionArbiter.get());
                  this.navigator.cancelNavigation();
                  this.budgetManager.pauseJob();
               } else if (this.state == AutoBaseBuilding.BuildState.BUYING_AH) {
                  this.autoBuy.tick(mc);
                  if (this.autoBuy.getCurrentState() == AutoBuyController.State.BLOCKED) {
                     this.state = AutoBaseBuilding.BuildState.BLOCKED;
                     this.blockingReason = this.autoBuy.getBlockingReason();
                  } else if (this.autoBuy.getCurrentState() == AutoBuyController.State.IDLE) {
                     this.state = AutoBaseBuilding.BuildState.PLACING;
                     this.blockingReason = "";
                     this.budgetManager.recordProgress();
                  }
               } else {
                  if (this.state == AutoBaseBuilding.BuildState.NAVIGATING) {
                     class_243 var2 = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
                     if (this.activePlannedTask != null) {
                        class_2338 var3 = this.activePlannedTask.getTargetPos();
                        double var4 = var2.method_1028(var3.method_10263() + 0.5, var3.method_10264() + 0.5, var3.method_10260() + 0.5);
                        if (var4 <= ((Double)this.maxReach.get() - 0.5) * ((Double)this.maxReach.get() - 0.5)) {
                           this.navigator.cancelNavigation();
                           this.releaseMovementKeys();
                           this.currentNavTarget = null;
                           this.navStuckTicks = 0;
                           this.state = AutoBaseBuilding.BuildState.PLACING;
                        }
                     }

                     if (this.state == AutoBaseBuilding.BuildState.NAVIGATING) {
                        if (BaritoneNavigationAdapter.isBaritoneAvailable()) {
                           if (this.navigator.getStatus() == NavigationStatus.ARRIVED) {
                              this.releaseMovementKeys();
                              this.currentNavTarget = null;
                              this.state = AutoBaseBuilding.BuildState.PLACING;
                           }
                        } else {
                           this.tickNativeNavigation(var2);
                           if (this.state == AutoBaseBuilding.BuildState.NAVIGATING) {
                              return;
                           }
                        }
                     }
                  }

                  if (this.executor.getStage() != PlacementStage.IDLE) {
                     this.executor.tick(mc, ActionArbiter.get(), this.camera, (Double)this.maxReach.get());
                     if (this.executor.getStage() == PlacementStage.COMPLETED) {
                        this.budgetManager.recordProgress();
                        if (this.executor.getCurrentTarget() != null) {
                           this.planner.markCompleted(this.executor.getCurrentTarget().getTargetPos());
                        }

                        this.executor.release(mc, ActionArbiter.get());
                        this.activePlannedTask = null;
                     } else if (this.executor.getStage() == PlacementStage.FAILED_BLOCKED && this.executor.getCurrentTarget() != null) {
                        class_2338 var15 = this.executor.getCurrentTarget().getTargetPos();
                        if (this.budgetManager.registerRetry(var15)) {
                           this.executor.release(mc, ActionArbiter.get());
                        } else {
                           this.planner.markBlocked(var15, this.executor.getCurrentTarget().getFailureReason());
                           this.executor.release(mc, ActionArbiter.get());
                           this.activePlannedTask = null;
                        }
                     }
                  } else if (this.planner.isFinished()) {
                     this.state = AutoBaseBuilding.BuildState.DONE;
                     this.stopBuilding();
                  } else {
                     class_243 var14 = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
                     String var16 = this.getHeldItemCleanId();
                     BuildPlanner.WorldBlockQuery var17 = (pos, id, props) -> {
                        if (mc.field_1687 == null) {
                           return false;
                        }

                        class_2680 var3x = mc.field_1687.method_8320(pos);
                        String var4x = class_7923.field_41175.method_10221(var3x.method_26204()).toString();
                        return var4x.equalsIgnoreCase(id);
                     };
                     if (this.activePlannedTask == null) {
                        this.activePlannedTask = this.planner.getNextTask(var14, (Double)this.maxReach.get(), var16, var17);
                     }

                     if (this.activePlannedTask == null) {
                        for (BuildPlanner.PlannedBuildTask var6 : this.planner.getAllTasks()) {
                           if (!this.planner.getBlockedPositions().containsKey(var6.getTargetPos()) && this.planner.getTaskAt(var6.getTargetPos()) != null) {
                              this.activePlannedTask = var6;
                              break;
                           }
                        }
                     }

                     if (this.activePlannedTask == null) {
                        if (this.planner.getBlockedCount() > 0) {
                           this.state = AutoBaseBuilding.BuildState.BLOCKED;
                           this.blockingReason = "All remaining blocks blocked";
                        } else {
                           this.state = AutoBaseBuilding.BuildState.DONE;
                        }
                     } else {
                        String var18 = this.activePlannedTask.getRequiredItemId();
                        if (var18 != null
                           && !var18.isEmpty()
                           && !var18.equals("minecraft:air")) {
                           int var19 = this.getInventoryItemCount(var18);
                           if (var19 < this.activePlannedTask.getItemQuantity()) {
                              if ((Boolean)this.autoBuyEnabled.get()) {
                                 this.state = AutoBaseBuilding.BuildState.BUYING_AH;
                                 this.autoBuy.requestPurchase(var18, this.activePlannedTask.getItemQuantity());
                                 return;
                              }

                              this.state = AutoBaseBuilding.BuildState.BLOCKED;
                              this.blockingReason = "Missing material: " + var18;
                              return;
                           }
                        }

                        class_2338 var20 = this.activePlannedTask.getTargetPos();
                        double var7 = var14.method_1028(var20.method_10263() + 0.5, var20.method_10264() + 0.5, var20.method_10260() + 0.5);
                        AutoBaseBuilding.ResolvedSupport var9 = findSupportForBlock(mc.field_1687, var20, this.activePlannedTask.getExpectedState());
                        if (var9 == null) {
                           this.planner
                              .markBlocked(
                                 var20,
                                 "No adjacent solid support block in world"
                              );
                           this.activePlannedTask = null;
                        } else if (var7 > (Double)this.maxReach.get() * (Double)this.maxReach.get()) {
                           class_2350 var21 = getRequiredPlayerFacing(this.activePlannedTask.getExpectedState());
                           List var22 = StandPositionFinder.findCandidateStandPositions(
                              mc.field_1687, mc.field_1724, var20, var9.placeFace(), var21, (Double)this.maxReach.get()
                           );
                           if (var22.isEmpty()) {
                              this.planner
                                 .markBlocked(
                                    var20,
                                    "No valid standing spot found"
                                 );
                              this.activePlannedTask = null;
                           } else {
                              class_2338 var12 = (class_2338)var22.get(0);
                              this.currentNavTarget = var12;
                              this.state = AutoBaseBuilding.BuildState.NAVIGATING;
                              if (BaritoneNavigationAdapter.isBaritoneAvailable()) {
                                 NavigationStatus var13 = this.navigator.navigateTo(mc, var12);
                                 if (var13 == NavigationStatus.PATHING) {
                                    return;
                                 }
                              }

                              this.tickNativeNavigation(var14);
                           }
                        } else {
                           class_1792 var10 = (class_1792)class_7923.field_41178
                              .method_63535(class_2960.method_60654(this.activePlannedTask.getRequiredItemId()));
                           PlacementTarget var11 = new PlacementTarget(
                              var20,
                              this.activePlannedTask.getExpectedState() != null
                                 ? this.activePlannedTask.getExpectedState()
                                 : class_2246.field_10340.method_9564(),
                              var10,
                              var9.supportPos(),
                              var9.placeFace(),
                              var9.hitVec()
                           );
                           this.selectItemInHotbar(this.activePlannedTask.getRequiredItemId());
                           this.executor.setTarget(var11);
                           this.state = AutoBaseBuilding.BuildState.PLACING;
                           this.planner.markPending(var20);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private String getHeldItemCleanId() {
      if (mc.field_1724 == null) {
         return null;
      }

      class_1799 var1 = mc.field_1724.method_6047();
      return var1.method_7960() ? null : class_7923.field_41178.method_10221(var1.method_7909()).toString();
   }

   private int getInventoryItemCount(String cleanId) {
      if (mc.field_1724 != null && cleanId != null) {
         int var2 = 0;

         for (int var3 = 0; var3 < (36); var3++) {
            class_1799 var4 = mc.field_1724.method_31548().method_5438(var3);
            if (!var4.method_7960() && class_7923.field_41178.method_10221(var4.method_7909()).toString().equalsIgnoreCase(cleanId)) {
               var2 += var4.method_7947();
            }
         }

         return var2;
      } else {
         return 0;
      }
   }

   private void selectItemInHotbar(String cleanId) {
      if (mc.field_1724 != null && cleanId != null) {
         for (int var2 = 0; var2 < (9); var2++) {
            class_1799 var3 = mc.field_1724.method_31548().method_5438(var2);
            if (!var3.method_7960() && class_7923.field_41178.method_10221(var3.method_7909()).toString().equalsIgnoreCase(cleanId)) {
               InventoryUtil.swap(var2);
               return;
            }
         }
      }
   }

   private void updateHudSnapshot() {
      long var1 = this.budgetManager.getElapsedWallTimeMs();
      long var3 = this.budgetManager.getRemainingContinuousWorkMs();
      double var5 = var1 > 5000L ? this.planner.getCompletedCount() * 60000.0 / var1 : 0.0;
      byte[] var10000 = new byte[14];
      var10000[0] = (byte)(58);
      var10000[1] = (byte)(-5);
      var10000[2] = (byte)(112);
      var10000[3] = (byte)(-67);
      var10000[4] = (byte)(4);
      var10000[5] = (byte)(49);
      var10000[6] = (byte)(-95);
      var10000[7] = (byte)(-123);
      var10000[8] = (byte)(120);
      var10000[9] = (byte)(-52);
      var10000[10] = (byte)(-125);
      var10000[11] = (byte)(-40);
      var10000[12] = (byte)(7);
      var10000[13] = (byte)(-85);
      String var7 = $$v_s(var10000, -2755213650754809764L);
      if (var5 > 0.5 && this.planner.getRemainingCount() > 0) {
         long var8 = (long)(this.planner.getRemainingCount() / (var5 / 60.0));
         var10000 = new byte[9];
         var10000[0] = (byte)(92);
         var10000[1] = (byte)(-86);
         var10000[2] = (byte)(46);
         var10000[3] = (byte)(-70);
         var10000[4] = (byte)(75);
         var10000[5] = (byte)(120);
         var10000[6] = (byte)(-16);
         var10000[7] = (byte)(-61);
         var10000[8] = (byte)(117);
         String var12 = $$v_s(var10000, -2755213650754809764L);
         Object[] var10001 = new Object[2];
         var10001[0] = var8 / 60L;
         var10001[1] = var8 % 60L;
         var7 = String.format(var12, var10001);
      }

      String var10 = "";
      if (this.activePlannedTask != null) {
         var10 = "Target: " + this.activePlannedTask.getExpectedCleanId();
      }

      AutoBaseHudWidget.HudSnapshot var13 = new AutoBaseHudWidget.HudSnapshot;
      String var10003;
      if (this.currentSchematic != null) {
         var10003 = this.currentSchematic.name();
      } else {
         byte[] var14 = new byte[4];
         var14[0] = (byte)(55);
         var14[1] = (byte)(-11);
         var14[2] = (byte)(114);
         var14[3] = (byte)(-69);
         var10003 = $$v_s(var14, -2755213650754809764L);
      }

      var13./* $VF: Unable to resugar constructor */<init>(
         false,
         var10003,
         this.state.name(),
         this.planner.getCompletedCount(),
         this.planner.getTotalCount(),
         this.planner.getPendingCount(),
         this.planner.getBlockedCount(),
         var1,
         var3,
         var5,
         var7,
         var10,
         this.blockingReason
      );
      AutoBaseHudWidget.updateSnapshot(var13);
   }

   public static class_2350 getRequiredPlayerFacing(class_2680 expectedState) {
      if (expectedState == null) {
         return null;
      }

      if (expectedState.method_28498(class_2741.field_12481)) {
         class_2350 var1 = (class_2350)expectedState.method_11654(class_2741.field_12481);
         String var2 = expectedState.method_26204().toString().toLowerCase();
         byte[] var10001 = new byte[6];
         var10001[0] = (byte)(9);
         var10001[1] = (byte)(-13);
         var10001[2] = (byte)(111);
         var10001[3] = (byte)(-86);
         var10001[4] = (byte)(30);
         var10001[5] = (byte)(51);
         if (!var2.contains($$v_s(var10001, -2755213650754809764L))) {
            var10001 = new byte[9];
            var10001[0] = (byte)(29);
            var10001[1] = (byte)(-13);
            var10001[2] = (byte)(111);
            var10001[3] = (byte)(-82);
            var10001[4] = (byte)(20);
            var10001[5] = (byte)(51);
            var10001[6] = (byte)(-77);
            var10001[7] = (byte)(-108);
            var10001[8] = (byte)(99);
            if (!var2.contains($$v_s(var10001, -2755213650754809764L))) {
               var10001 = new byte[7];
               var10001[0] = (byte)(29);
               var10001[1] = (byte)(-24);
               var10001[2] = (byte)(115);
               var10001[3] = (byte)(-82);
               var10001[4] = 1;
               var10001[5] = (byte)(56);
               var10001[6] = (byte)(-78);
               if (!var2.contains($$v_s(var10001, -2755213650754809764L))) {
                  return var1.method_10153();
               }
            }
         }

         return var1;
      } else {
         return null;
      }
   }

   public static AutoBaseBuilding.ResolvedSupport findSupportForBlock(class_1937 world, class_2338 targetPos, class_2680 expectedState) {
      if (world != null && targetPos != null) {
         if (expectedState != null) {
            String var3 = expectedState.method_26204().toString().toLowerCase();
            if (expectedState.method_28498(class_2741.field_12481)
               && (
                  var3.contains("ladder")
                     || var3.contains("wall_torch")
                     || var3.contains("wall_sign")
               )) {
               class_2350 var4 = (class_2350)expectedState.method_11654(class_2741.field_12481);
               class_2338 var5 = targetPos.method_10093(var4.method_10153());
               class_2680 var6 = world.method_8320(var5);
               if (!var6.method_26220(world, var5).method_1110()) {
                  class_2350 var20 = var4;
                  class_243 var21 = class_243.method_24953(var5).method_1019(class_243.method_24954(var20.method_62675()).method_1021(0.48));
                  return new AutoBaseBuilding.ResolvedSupport(var5, var20, var21);
               }
            }

            if (expectedState.method_28498(class_2741.field_16561)) {
               if ((Boolean)expectedState.method_11654(class_2741.field_16561)) {
                  class_2338 var13 = targetPos.method_10084();
                  class_2680 var16 = world.method_8320(var13);
                  if (!var16.method_26220(world, var13).method_1110()) {
                     return new AutoBaseBuilding.ResolvedSupport(var13, class_2350.field_11033, class_243.method_24953(var13).method_1031(0.0, -0.48, 0.0));
                  }
               } else {
                  class_2338 var14 = targetPos.method_10074();
                  class_2680 var17 = world.method_8320(var14);
                  if (!var17.method_26220(world, var14).method_1110()) {
                     return new AutoBaseBuilding.ResolvedSupport(var14, class_2350.field_11036, class_243.method_24953(var14).method_1031(0.0, 0.48, 0.0));
                  }
               }
            }
         }

         class_2350[] var12 = new class_2350[]{
            class_2350.field_11033, class_2350.field_11043, class_2350.field_11035, class_2350.field_11039, class_2350.field_11034, class_2350.field_11036
         };

         for (class_2350 var7 : var12) {
            class_2338 var8 = targetPos.method_10093(var7);
            class_2680 var9 = world.method_8320(var8);
            if (!var9.method_26215() && !var9.method_26220(world, var8).method_1110()) {
               class_2350 var10 = var7.method_10153();
               class_243 var11 = class_243.method_24953(var8).method_1019(class_243.method_24954(var10.method_62675()).method_1021(0.48));
               return new AutoBaseBuilding.ResolvedSupport(var8, var10, var11);
            }
         }

         return null;
      } else {
         return null;
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
            this.state = AutoBaseBuilding.BuildState.PLACING;
         } else {
            if (this.activePlannedTask != null) {
               class_2338 var10 = this.activePlannedTask.getTargetPos();
               double var11 = pPos.method_1028(var10.method_10263() + 0.5, var10.method_10264() + 0.5, var10.method_10260() + 0.5);
               if (var11 <= ((Double)this.maxReach.get() - 0.5) * ((Double)this.maxReach.get() - 0.5)) {
                  this.releaseMovementKeys();
                  this.currentNavTarget = null;
                  this.navStuckTicks = 0;
                  this.state = AutoBaseBuilding.BuildState.PLACING;
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
                  if (this.activePlannedTask != null) {
                     this.planner
                        .markBlocked(
                           this.activePlannedTask.getTargetPos(),
                           "Navigation path obstructed"
                        );
                     this.activePlannedTask = null;
                  }

                  this.currentNavTarget = null;
                  this.navStuckTicks = 0;
                  this.state = AutoBaseBuilding.BuildState.PLACING;
                  return;
               }
            } else {
               this.navStuckTicks = 0;
            }

            this.lastNavPos = pPos;
            float var13 = (float)Math.toDegrees(Math.atan2(var4, var2)) - 90.0F;
            float var14 = mc.field_1724.method_36454();
            float var12 = Math.abs(class_3532.method_15393(var13 - var14));
            if (var12 <= 35.0F && this.isSafeToStep(pPos, var14)) {
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
         this.state = AutoBaseBuilding.BuildState.PLACING;
      }
   }

   private boolean isSafeToStep(class_243 currentPos, float yaw) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         double var3 = Math.toRadians(yaw);
         double var5 = -Math.sin(var3) * 0.6;
         double var7 = Math.cos(var3) * 0.6;
         double var9 = currentPos.field_1352 + var5;
         double var11 = currentPos.field_1350 + var7;
         class_2338 var13 = class_2338.method_49637(var9, currentPos.field_1351, var11);
         class_2338 var14 = var13.method_10084();
         class_2680 var15 = mc.field_1687.method_8320(var14);
         if (!var15.method_26194(mc.field_1687, var14, class_3726.method_16194()).method_1110()) {
            return false;
         } else {
            class_2680 var16 = mc.field_1687.method_8320(var13);
            if (var16.method_26204() != class_2246.field_10164 && var16.method_26204() != class_2246.field_10036) {
               class_2338 var17 = var13.method_10074();
               class_2338 var18 = var13.method_10087(2);
               class_2680 var19 = mc.field_1687.method_8320(var17);
               class_2680 var20 = mc.field_1687.method_8320(var18);
               boolean var21 = !var19.method_26220(mc.field_1687, var17).method_1110() || !var20.method_26220(mc.field_1687, var18).method_1110();
               return !var21 ? false : var19.method_26204() != class_2246.field_10164 && var20.method_26204() != class_2246.field_10164;
            } else {
               return false;
            }
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

            if (this.state == AutoBaseBuilding.BuildState.NAVIGATING && mc.field_1724.field_5976) {
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

   @Override
   public void onKeyPress(int keyCode) {
      if (this.isInGame()) {
         if (this.startKey.isBound() && (Integer)this.startKey.get() == keyCode) {
            this.startBuilding();
         } else if (this.pauseResumeKey.isBound() && (Integer)this.pauseResumeKey.get() == keyCode) {
            this.togglePauseResume();
         } else if (this.stopKey.isBound() && (Integer)this.stopKey.get() == keyCode) {
            this.stopBuilding();
         } else if (this.setOriginKey.isBound() && (Integer)this.setOriginKey.get() == keyCode) {
            this.setOriginAtPlayer();
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (this.isInGame() && event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (mc.field_1724 != null && mc.field_1687 != null && mc.field_1755 == null) {
            if (this.state == AutoBaseBuilding.BuildState.NAVIGATING && this.currentNavTarget != null && !BaritoneNavigationAdapter.isBaritoneAvailable()) {
               this.steerNavCamera(event.getFrameTime());
            } else if (this.state == AutoBaseBuilding.BuildState.PLACING && this.executor.getStage() == PlacementStage.AIM) {
               this.camera.update(mc.field_1724);
            }
         }
      }
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

   public KeybindSetting getSetOriginKey() {
      return this.setOriginKey;
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

   public enum BuildState {
      IDLE,
      NAVIGATING,
      PLACING,
      BUYING_AH,
      PAUSED,
      BLOCKED,
      DONE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoBaseBuilding.BuildState[] $values() {
         AutoBaseBuilding.BuildState[] var10000 = new AutoBaseBuilding.BuildState[7];
         var10000[0] = IDLE;
         var10000[1] = NAVIGATING;
         var10000[2] = PLACING;
         var10000[3] = BUYING_AH;
         var10000[4] = PAUSED;
         var10000[5] = BLOCKED;
         var10000[6] = DONE;
         return var10000;
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

   public record ResolvedSupport(class_2338 supportPos, class_2350 placeFace, class_243 hitVec) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
