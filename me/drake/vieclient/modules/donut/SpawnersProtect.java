package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.StringSetting;
import me.drake.vieclient.modules.combat.CombatAim;
import me.drake.vieclient.util.BlockUtil;
import me.drake.vieclient.util.DiscordWebhook;
import me.drake.vieclient.util.EnchantmentUtil;
import me.drake.vieclient.util.InventoryUtil;
import me.drake.vieclient.util.RotationUtil;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1893;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_465;
import net.minecraft.class_7923;
import net.minecraft.class_239.class_240;

public class SpawnersProtect extends Module {
   private static final double MAX_REACH_DISTANCE = 4.0;
   private static final double REACH_SQ = 16.0;
   private final IntSetting radius = (IntSetting)this.addSetting(
      new IntSetting(
         $$v_s(new byte[]{80, -5, 120, -73, -76, -47}, 4609008124747304999L),
         $$v_s(
            new byte[]{
               82,
               -1,
               110,
               -73,
               -84,
               -57,
               -127,
               114,
               24,
               -126,
               -112,
               -103,
               -71,
               30,
               -72,
               27,
               55,
               41,
               -72,
               -114,
               5,
               58,
               -41,
               34,
               -37,
               102,
               -25,
               102,
               40,
               68,
               -23,
               -65,
               -111,
               74,
               29,
               9,
               79,
               103,
               39,
               -124,
               -22,
               -22,
               38,
               122,
               -106,
               -71,
               86,
               -68,
               27
            },
            4609008124747304999L
         ),
         16,
         4,
         32
      )
   );
   private final BoolSetting discordEnabled = (BoolSetting)this.addSetting(
      new BoolSetting(
         $$v_s(new byte[]{70, -13, 111, -67, -82, -48, -111, 55, 61, -57, -122, -98, -10, 21, -74}, 4609008124747304999L),
         $$v_s(
            new byte[]{
               81,
               -1,
               114,
               -70,
               -31,
               -42,
               -99,
               101,
               15,
               -61,
               -112,
               -42,
               -8,
               22,
               -72,
               29,
               38,
               57,
               -20,
               -49,
               31,
               54,
               -123,
               42,
               -45,
               97,
               -25,
               47,
               38,
               68,
               -83,
               -19,
               -121,
               74,
               19,
               12,
               85,
               113,
               117,
               -125,
               -91,
               -30,
               0,
               127,
               -118,
               -71,
               82,
               -67,
               86
            },
            4609008124747304999L
         ),
         true
      )
   );
   private final StringSetting webhookUrl = (StringSetting)this.addSetting(
      new StringSetting(
         $$v_s(new byte[]{85, -1, 126, -74, -82, -51, -98, 55, 63, -16, -88}, 4609008124747304999L),
         $$v_s(
            new byte[]{70, -13, 111, -67, -82, -48, -111, 55, 29, -57, -122, -98, -10, 21, -74, 79, 55, 36, -88, -34, 30, 59, -53, 51, -102, 71, -58, 10},
            4609008124747304999L
         ),
         ""
      )
   );
   private final StringSetting webhookPing = (StringSetting)this.addSetting(
      new StringSetting(
         $$v_s(new byte[]{85, -1, 126, -74, -82, -51, -98, 55, 58, -53, -118, -111}, 4609008124747304999L),
         $$v_s(
            new byte[]{
               79,
               -1,
               114,
               -86,
               -88,
               -51,
               -101,
               55,
               26,
               -53,
               -118,
               -111,
               -71,
               82,
               -99,
               10,
               36,
               47,
               -66,
               -41,
               30,
               60,
               -64,
               103,
               -107,
               50,
               -44,
               46,
               44,
               88,
               -24,
               -65,
               -51,
               26,
               14,
               17,
               77,
               103,
               124
            },
            4609008124747304999L
         ),
         $$v_s(new byte[]{66, -1, 106, -69, -77, -37, -102, 121, 15}, 4609008124747304999L)
      )
   );
   private final BoolSetting humanizeAim = (BoolSetting)this.addSetting(
      new BoolSetting(
         $$v_s(new byte[]{74, -17, 113, -65, -81, -53, -113, 114, 74, -29, -115, -101}, 4609008124747304999L),
         $$v_s(
            new byte[]{
               74,
               -17,
               113,
               -65,
               -81,
               -113,
               -103,
               126,
               1,
               -57,
               -60,
               -123,
               -12,
               21,
               -78,
               27,
               58,
               106,
               -81,
               -49,
               28,
               55,
               -41,
               38,
               -102,
               119,
               -11,
               53,
               32,
               68,
               -22,
               -65,
               -107,
               83,
               8,
               22,
               1,
               111,
               58,
               -126,
               -71,
               -89,
               100,
               81,
               -70,
               -98
            },
            4609008124747304999L
         ),
         true
      )
   );
   private final DoubleSetting aimSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         $$v_s(new byte[]{67, -13, 113, -2, -110, -46, -112, 114, 14}, 4609008124747304999L),
         $$v_s(
            new byte[]{
               79,
               -5,
               100,
               -2,
               -91,
               -57,
               -110,
               101,
               15,
               -57,
               -105,
               -42,
               -23,
               31,
               -81,
               79,
               38,
               35,
               -81,
               -59,
               81,
               54,
               -48,
               53,
               -45,
               124,
               -13,
               102,
               42,
               75,
               -32,
               -6,
               -112,
               91,
               92,
               19,
               78,
               116,
               48,
               -102,
               -81,
               -84,
               48
            },
            4609008124747304999L
         ),
         18.0,
         5.0,
         45.0
      )
   );
   private final BoolSetting fastTravel = (BoolSetting)this.addSetting(
      new BoolSetting(
         $$v_s(new byte[]{68, -5, 111, -86, -31, -10, -121, 118, 28, -57, -120}, 4609008124747304999L),
         $$v_s(
            new byte[]{
               81,
               -22,
               110,
               -73,
               -81,
               -42,
               -43,
               96,
               2,
               -57,
               -118,
               -42,
               -19,
               8,
               -68,
               25,
               55,
               38,
               -91,
               -64,
               22,
               114,
               -47,
               40,
               -102,
               125,
               -31,
               50,
               100,
               69,
               -21,
               -78,
               -112,
               95,
               29,
               29,
               73,
               34,
               38,
               -121,
               -85,
               -75,
               42,
               115,
               -117,
               -87,
               29,
               -25,
               12,
               -34,
               65,
               -25,
               -3,
               -110,
               118,
               73,
               127,
               -45,
               -97,
               -58,
               -34,
               -30,
               -120,
               17,
               98,
               51,
               -78,
               -66,
               -13,
               3,
               -37,
               48,
               -49
            },
            4609008124747304999L
         ),
         true
      )
   );
   private final BoolSetting autoDisconnect = (BoolSetting)this.addSetting(
      new BoolSetting(
         $$v_s(new byte[]{67, -17, 104, -79, -31, -26, -100, 100, 9, -51, -118, -104, -4, 25, -87}, 4609008124747304999L),
         $$v_s(
            new byte[]{
               70,
               -13,
               111,
               -67,
               -82,
               -52,
               -101,
               114,
               9,
               -42,
               -60,
               -112,
               -21,
               21,
               -80,
               79,
               33,
               47,
               -66,
               -40,
               20,
               32,
               -123,
               38,
               -36,
               102,
               -15,
               52,
               105,
               89,
               -3,
               -2,
               -107,
               84,
               25,
               12,
               82,
               34,
               52,
               -123,
               -81,
               -30,
               55,
               115,
               -102,
               -81,
               79,
               -86,
               86
            },
            4609008124747304999L
         ),
         true
      )
   );
   private final BoolSetting manualTrigger = (BoolSetting)this.addSetting(
      new BoolSetting(
         $$v_s(new byte[]{79, -5, 114, -85, -96, -50, -43, 67, 24, -53, -125, -111, -4, 8}, 4609008124747304999L),
         $$v_s(
            new byte[]{
               75,
               -12,
               111,
               -86,
               -96,
               -52,
               -127,
               123,
               19,
               -126,
               -112,
               -124,
               -16,
               29,
               -70,
               10,
               32,
               106,
               -65,
               -34,
               16,
               37,
               -53,
               34,
               -56,
               50,
               -28,
               52,
               38,
               94,
               -24,
               -4,
               -106,
               83,
               19,
               16,
               1,
               112,
               58,
               -126,
               -66,
               -85,
               42,
               115,
               -39,
               -83,
               84,
               -69,
               90,
               -123,
               89,
               -70,
               -15,
               -59,
               100,
               78,
               110,
               -37,
               -102,
               -127
            },
            4609008124747304999L
         ),
         false
      )
   );
   private final CombatAim aim = new CombatAim();
   private final Map lastPlayerDistances = new HashMap();
   private final Set unreachableSpawners = new HashSet();
   private final List minedSpawnerPositions = new ArrayList();
   private SpawnersProtect.State state = SpawnersProtect.State.IDLE;
   private String threatPlayerName = null;
   private double threatDistance = 0.0;
   private class_2338 threatPos = null;
   private class_2338 currentMiningTarget = null;
   private class_2338 targetEnderChest = null;
   private boolean hasSilkTouch = false;
   private int minedSpawnersCount = 0;
   private int depositedSpawnersCount = 0;
   private int chestInteractCooldown = 0;
   private int chestOpenWaitTicks = 0;
   private int depositDelayTicks = 0;
   private int miningTicks = 0;
   private class_243 lastMovePos = null;
   private int stuckTicks = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SpawnersProtect() {
      super(
         $$v_s(new byte[]{81, -22, 125, -87, -81, -57, -121, 100, 58, -48, -117, -126, -4, 25, -87}, 4609008124747304999L),
         $$v_s(
            new byte[]{
               82,
               -24,
               115,
               -86,
               -92,
               -63,
               -127,
               100,
               74,
               -47,
               -108,
               -105,
               -18,
               20,
               -72,
               29,
               33,
               106,
               -82,
               -41,
               81,
               63,
               -52,
               41,
               -45,
               124,
               -13,
               102,
               62,
               67,
               -7,
               -9,
               -62,
               73,
               21,
               18,
               74,
               34,
               33,
               -104,
               -65,
               -95,
               44,
               54,
               -104,
               -76,
               89,
               -17,
               86,
               -113,
               92,
               -95,
               -94,
               -37,
               113,
               78,
               116,
               -43,
               -44,
               -110,
               -58,
               -86,
               -120,
               17,
               38,
               63,
               -82,
               -66,
               -30,
               10,
               -48,
               36,
               -34,
               66,
               -53,
               -40,
               121,
               78,
               -11,
               -35,
               -9,
               107,
               -8
            },
            4609008124747304999L
         ),
         Category.SMP_UTILITIES
      );
      this.humanizeAim.setVisibleInGui(false);
      this.fastTravel.setVisibleInGui(false);
      this.autoDisconnect.setVisibleInGui(false);
      this.aimSpeed.visibleWhen(this.humanizeAim::get);
      this.webhookUrl.visibleWhen(this.discordEnabled::get);
      this.webhookPing.visibleWhen(this.discordEnabled::get);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetRoutine();
   }

   @Override
   public void onDisable() {
      this.releaseKeys();
      this.resetRoutine();
      super.onDisable();
   }

   private void resetRoutine() {
      this.state = SpawnersProtect.State.IDLE;
      this.threatPlayerName = null;
      this.threatDistance = 0.0;
      this.threatPos = null;
      this.currentMiningTarget = null;
      this.targetEnderChest = null;
      this.hasSilkTouch = false;
      this.minedSpawnersCount = 0;
      this.depositedSpawnersCount = 0;
      this.chestInteractCooldown = 0;
      this.chestOpenWaitTicks = 0;
      this.depositDelayTicks = 0;
      this.miningTicks = 0;
      this.stuckTicks = 0;
      this.lastMovePos = null;
      this.unreachableSpawners.clear();
      this.minedSpawnerPositions.clear();
      this.lastPlayerDistances.clear();
      this.aim.reset();
   }

   private void releaseKeys() {
      this.stopMovement();
      if (mc.field_1690 != null) {
         mc.field_1690.field_1832.method_23481(false);
         mc.field_1690.field_1886.method_23481(false);
         mc.field_1690.field_1867.method_23481(false);
      }

      if (mc.field_1724 != null) {
         mc.field_1724.method_5660(false);
      }
   }

   private void stopMovement() {
      if (mc.field_1690 != null) {
         mc.field_1690.field_1894.method_23481(false);
         mc.field_1690.field_1881.method_23481(false);
         mc.field_1690.field_1913.method_23481(false);
         mc.field_1690.field_1849.method_23481(false);
         mc.field_1690.field_1903.method_23481(false);
      }
   }

   @Override
   public void onTick() {
      if (!this.isInGame()) {
         if (this.state != SpawnersProtect.State.IDLE) {
            this.releaseKeys();
            this.resetRoutine();
         }
      } else {
         if ((Boolean)this.manualTrigger.get()) {
            this.manualTrigger.set(false);
            if (this.state == SpawnersProtect.State.IDLE) {
               this.threatPlayerName = $$v_s(new byte[]{79, -5, 114, -85, -96, -50, -43, 67, 24, -53, -125, -111, -4, 8}, 4609008124747304999L);
               this.threatDistance = 0.0;
               this.threatPos = mc.field_1724.method_24515();
               this.notifyChat(
                  $$v_s(
                     new byte[]{
                        -64,
                        61,
                        121,
                        -123,
                        -110,
                        -46,
                        -108,
                        96,
                        4,
                        -57,
                        -106,
                        -123,
                        -55,
                        8,
                        -78,
                        27,
                        55,
                        41,
                        -72,
                        -13,
                        81,
                        -112,
                        2,
                        38,
                        -9,
                        115,
                        -6,
                        51,
                        40,
                        70,
                        -83,
                        -21,
                        -112,
                        83,
                        27,
                        25,
                        68,
                        112,
                        117,
                        -106,
                        -87,
                        -74,
                        45,
                        96,
                        -104,
                        -82,
                        88,
                        -85,
                        19,
                        -54,
                        127,
                        -85,
                        -78,
                        -57,
                        119,
                        78,
                        116,
                        -43,
                        -44,
                        -107,
                        -39,
                        -21,
                        -102,
                        17,
                        39,
                        40,
                        -81,
                        -80,
                        -81,
                        76
                     },
                     4609008124747304999L
                  )
               );
               this.transitionTo(SpawnersProtect.State.SELECT_TOOL);
            }
         }

         switch (this.state) {
            case IDLE:
               this.checkThreats();
               break;
            case SELECT_TOOL:
               this.handleSelectTool();
               break;
            case MINING_SPAWNERS:
               this.handleMiningSpawners();
               break;
            case LOCATE_ENDER_CHEST:
               this.handleLocateEnderChest();
               break;
            case AIM_AND_OPEN_CHEST:
               this.handleAimAndOpenChest();
               break;
            case DEPOSITING_ITEMS:
               this.handleDepositingItems();
               break;
            case COMPLETE_AND_DISCONNECT:
               this.handleCompleteAndDisconnect();
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (mc.field_1724 != null && mc.field_1687 != null) {
            class_2338 var2 = null;
            if (this.state == SpawnersProtect.State.MINING_SPAWNERS) {
               var2 = this.currentMiningTarget;
            } else if (this.state == SpawnersProtect.State.AIM_AND_OPEN_CHEST) {
               var2 = this.targetEnderChest;
            }

            if (var2 != null) {
               float[] var3 = RotationUtil.getRotations(var2, mc.field_1724);
               this.stepAim(var3[0], var3[1], event.getFrameTime());
            }
         }
      }
   }

   private void stepAim(float targetYaw, float targetPitch, float frameTime) {
      if (mc.field_1724 != null && mc.field_1690 != null) {
         if (!(Boolean)this.humanizeAim.get()) {
            mc.field_1724.method_36456(targetYaw);
            mc.field_1724.method_36457(targetPitch);
         } else {
            float var4 = ((Double)mc.field_1690.method_42495().method_41753()).floatValue();
            CombatAim.Angles var5 = this.aim
               .step(mc.field_1724.method_36454(), mc.field_1724.method_36455(), targetYaw, targetPitch, (Double)this.aimSpeed.get(), var4, frameTime, true);
            mc.field_1724.method_36456(var5.yaw());
            mc.field_1724.method_36457(var5.pitch());
         }
      }
   }

   private void transitionTo(SpawnersProtect.State newState) {
      this.state = newState;
      this.aim.reset();
      this.stopMovement();
      this.chestInteractCooldown = 0;
      this.chestOpenWaitTicks = 0;
      this.depositDelayTicks = 0;
      this.miningTicks = 0;
      this.stuckTicks = 0;
      this.lastMovePos = null;
   }

   private void checkThreats() {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         int var1 = (Integer)this.radius.get();
         double var2 = (double)var1 * var1;

         for (class_1657 var5 : mc.field_1687.method_18456()) {
            if (var5 != mc.field_1724 && !var5.method_31481() && !var5.method_7325() && var5.method_5805() && !this.isFriend(var5)) {
               double var6 = mc.field_1724.method_5858(var5);
               if (var6 <= var2) {
                  double var8 = Math.sqrt(var6);
                  Double var10 = (Double)this.lastPlayerDistances.get(var5.method_5667());
                  this.lastPlayerDistances.put(var5.method_5667(), var8);
                  this.threatPlayerName = var5.method_5477().getString();
                  this.threatDistance = var8;
                  this.threatPos = var5.method_24515();
                  this.notifyChat(
                     String.format(
                        $$v_s(
                           new byte[]{
                              -64,
                              61,
                              127,
                              -123,
                              -110,
                              -46,
                              -108,
                              96,
                              4,
                              -57,
                              -106,
                              -123,
                              -55,
                              8,
                              -78,
                              27,
                              55,
                              41,
                              -72,
                              -13,
                              81,
                              -112,
                              2,
                              33,
                              -22,
                              126,
                              -11,
                              63,
                              44,
                              88,
                              -83,
                              93,
                              69,
                              95,
                              89,
                              13,
                              1,
                              -64,
                              -14,
                              -111,
                              -85,
                              -78,
                              52,
                              100,
                              -106,
                              -69,
                              94,
                              -89,
                              91,
                              -124,
                              75,
                              -18,
                              -7,
                              112,
                              -94,
                              68,
                              63,
                              -100,
                              -59,
                              -128,
                              -60,
                              72,
                              74,
                              25,
                              107,
                              123,
                              -4,
                              -51,
                              -28,
                              1,
                              -64,
                              37,
                              -61,
                              12,
                              -61,
                              -106,
                              42,
                              74,
                              -4,
                              -40,
                              -4,
                              111,
                              -2,
                              29,
                              31,
                              60,
                              75
                           },
                           4609008124747304999L
                        ),
                        this.threatPlayerName,
                        this.threatDistance
                     )
                  );
                  this.sendThreatAlert(var5, var8);
                  this.transitionTo(SpawnersProtect.State.SELECT_TOOL);
                  return;
               }

               this.lastPlayerDistances.put(var5.method_5667(), Math.sqrt(var6));
            }
         }
      }
   }

   private boolean isFriend(class_1657 player) {
      VieClientMod var2 = VieClientMod.getInstance();
      return var2 != null && var2.getFriendManager() != null && var2.getFriendManager().isFriend(player.method_5477().getString());
   }

   private void handleSelectTool() {
      if (mc.field_1690 != null) {
         mc.field_1690.field_1832.method_23481(true);
      }

      if (mc.field_1724 != null) {
         mc.field_1724.method_5660(true);
      }

      int var1 = this.findSilkTouchPickaxeInHotbar();
      if (var1 != -1) {
         InventoryUtil.swap(var1);
         this.hasSilkTouch = true;
         this.notifyChat("§a[SpawnersProtect] §fSelected Silk Touch pickaxe (Hotbar slot " + (var1 + 1) + ").");
      } else {
         int var2 = this.findSilkTouchPickaxeInMainInv();
         if (var2 != -1) {
            int var3 = mc.field_1724.method_31548().method_67532();
            this.swapMainTo(var2, var3);
            this.hasSilkTouch = true;
            this.notifyChat("§a[SpawnersProtect] §fSwapped Silk Touch pickaxe from inventory to slot " + (var3 + 1) + ".");
         } else {
            this.hasSilkTouch = false;
            this.notifyChat(
               $$v_s(
                  new byte[]{
                     -64,
                     61,
                     42,
                     -123,
                     -110,
                     -46,
                     -108,
                     96,
                     4,
                     -57,
                     -106,
                     -123,
                     -55,
                     8,
                     -78,
                     27,
                     55,
                     41,
                     -72,
                     -13,
                     81,
                     -112,
                     2,
                     36,
                     -19,
                     115,
                     -26,
                     40,
                     32,
                     68,
                     -22,
                     -91,
                     -62,
                     116,
                     19,
                     94,
                     114,
                     107,
                     57,
                     -100,
                     -22,
                     -106,
                     43,
                     99,
                     -102,
                     -78,
                     29,
                     -65,
                     91,
                     -119,
                     71,
                     -81,
                     -87,
                     -41,
                     37,
                     65,
                     117,
                     -57,
                     -102,
                     -126,
                     -120,
                     -86,
                     -96,
                     22,
                     44,
                     51,
                     -78,
                     -7,
                     -95,
                     17,
                     -59,
                     54,
                     -35,
                     12,
                     -63,
                     -60,
                     42,
                     26,
                     -4,
                     -63,
                     -21,
                     125,
                     -19,
                     23,
                     31
                  },
                  4609008124747304999L
               )
            );
         }
      }

      this.transitionTo(SpawnersProtect.State.MINING_SPAWNERS);
   }

   private boolean isSilkTouchPickaxe(class_1799 stack) {
      if (stack != null && !stack.method_7960()) {
         boolean var3;
         label24: {
            if (!stack.method_31573(class_3489.field_42614)) {
               String var10000 = class_7923.field_41178.method_10221(stack.method_7909()).method_12832();
               byte[] var10001 = new byte[8];
               var10001[0] = (byte)(93);
               var10001[1] = (byte)(-22);
               var10001[2] = (byte)(117);
               var10001[3] = (byte)(-67);
               var10001[4] = (byte)(-86);
               var10001[5] = (byte)(-61);
               var10001[6] = (byte)(-115);
               var10001[7] = (byte)(114);
               if (!var10000.endsWith($$v_s(var10001, 4609008124747304999L))) {
                  var3 = false;
                  break label24;
               }
            }

            var3 = true;
         }

         boolean var2 = var3;
         return var2 && EnchantmentUtil.hasEnchantment(stack, class_1893.field_9099);
      } else {
         return false;
      }
   }

   private int findSilkTouchPickaxeInHotbar() {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 0; var1 < (9); var1++) {
         class_1799 var2 = mc.field_1724.method_31548().method_5438(var1);
         if (this.isSilkTouchPickaxe(var2)) {
            return var1;
         }
      }

      return -1;
   }

   private int findSilkTouchPickaxeInMainInv() {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 9; var1 < (36); var1++) {
         class_1799 var2 = mc.field_1724.method_31548().method_5438(var1);
         if (this.isSilkTouchPickaxe(var2)) {
            return var1;
         }
      }

      return -1;
   }

   private void swapMainTo(int fromIndex, int toHotbarIndex) {
      if (mc.field_1724 != null && mc.field_1761 != null) {
         int var3 = fromIndex <= (8) ? (36) + fromIndex : fromIndex;
         mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, var3, toHotbarIndex, class_1713.field_7791, mc.field_1724);
         InventoryUtil.swap(toHotbarIndex);
      }
   }

   private void handleMiningSpawners() {
      if (mc.field_1690 != null) {
         mc.field_1690.field_1832.method_23481(true);
      }

      if (mc.field_1724 != null) {
         mc.field_1724.method_5660(true);
      }

      if (this.currentMiningTarget != null) {
         if (!mc.field_1687.method_8320(this.currentMiningTarget).method_27852(class_2246.field_10260)) {
            this.minedSpawnersCount++;
            this.minedSpawnerPositions.add(this.currentMiningTarget.method_10062());
            this.notifyChat("§a[SpawnersProtect] §fBroke spawner #" + this.minedSpawnersCount + ".");
            if (mc.field_1761 != null) {
               mc.field_1761.method_2925();
            }

            if (mc.field_1690 != null) {
               mc.field_1690.field_1886.method_23481(false);
            }

            this.currentMiningTarget = null;
            this.miningTicks = 0;
            this.stuckTicks = 0;
            this.lastMovePos = null;
            this.aim.reset();
            this.stopMovement();
         } else if (this.miningTicks > 160) {
            this.notifyChat(
               "§6[SpawnersProtect] §cSpawner at " + this.currentMiningTarget.method_23854() + " could not be broken (claim protected / blocked). Skipping."
            );
            this.unreachableSpawners.add(this.currentMiningTarget);
            if (mc.field_1761 != null) {
               mc.field_1761.method_2925();
            }

            if (mc.field_1690 != null) {
               mc.field_1690.field_1886.method_23481(false);
            }

            this.currentMiningTarget = null;
            this.miningTicks = 0;
            this.stuckTicks = 0;
            this.lastMovePos = null;
            this.aim.reset();
            this.stopMovement();
         }
      }

      if (this.currentMiningTarget == null) {
         List var1 = this.findNearbySpawners((Integer)this.radius.get());
         var1.removeIf(this.unreachableSpawners::contains);
         if (var1.isEmpty()) {
            List var13 = this.findNearbyDroppedSpawners(((Integer)this.radius.get()).intValue());
            if (!var13.isEmpty()) {
               class_1542 var14 = (class_1542)var13.get(0);
               class_243 var4 = var14.method_33571();
               float[] var15 = RotationUtil.getRotations(var14);
               float var16 = Math.abs(class_3532.method_15393(var15[0] - mc.field_1724.method_36454()));
               this.navigateTowards(var14.method_24515(), var4, var16);
               return;
            }

            if (mc.field_1690 != null) {
               mc.field_1690.field_1886.method_23481(false);
            }

            this.stopMovement();
            this.notifyChat("§a[SpawnersProtect] §fAll spawners cleared (" + this.minedSpawnersCount + " total). Locating Ender Chest...");
            this.transitionTo(SpawnersProtect.State.LOCATE_ENDER_CHEST);
            return;
         }

         this.currentMiningTarget = (class_2338)var1.get(0);
         this.miningTicks = 0;
         this.stuckTicks = 0;
         this.lastMovePos = null;
      }

      class_243 var12 = class_243.method_24953(this.currentMiningTarget);
      class_243 var2 = mc.field_1724.method_33571();
      double var3 = var2.method_1025(var12);
      boolean var5 = var3 <= 16.0;
      float[] var6 = RotationUtil.getRotations(this.currentMiningTarget, mc.field_1724);
      float var7 = Math.abs(class_3532.method_15393(var6[0] - mc.field_1724.method_36454()));
      float var8 = Math.abs(var6[1] - mc.field_1724.method_36455());
      if (!var5) {
         if (mc.field_1690 != null) {
            mc.field_1690.field_1886.method_23481(false);
         }

         this.navigateTowards(this.currentMiningTarget, var12, var7);
      } else {
         this.stopMovement();
         if (mc.field_1690 != null) {
            mc.field_1690.field_1832.method_23481(true);
         }

         if (mc.field_1724 != null) {
            mc.field_1724.method_5660(true);
         }

         if (!(Boolean)this.humanizeAim.get()) {
            mc.field_1724.method_36456(var6[0]);
            mc.field_1724.method_36457(var6[1]);
         } else {
            this.stepAim(var6[0], var6[1], 0.05F);
         }

         boolean var9 = this.isLookingAt(this.currentMiningTarget);
         boolean var10 = var7 < 10.0F && var8 < 10.0F;
         if ((var9 || var10) && mc.field_1761 != null) {
            class_2350 var11 = this.getTargetSide(this.currentMiningTarget);
            if (!mc.field_1761.method_2923()) {
               mc.field_1761.method_2910(this.currentMiningTarget, var11);
            } else {
               mc.field_1761.method_2902(this.currentMiningTarget, var11);
            }

            mc.field_1724.method_6104(class_1268.field_5808);
            if (mc.field_1690 != null) {
               mc.field_1690.field_1886.method_23481(true);
            }

            this.miningTicks++;
         } else if (mc.field_1690 != null) {
            mc.field_1690.field_1886.method_23481(false);
         }
      }
   }

   private void navigateTowards(class_2338 targetPos, class_243 targetCenter, float yawDiff) {
      if (mc.field_1690 != null && mc.field_1724 != null) {
         double var4 = mc.field_1724.method_33571().method_1025(targetCenter);
         if ((Boolean)this.fastTravel.get() && var4 > 25.0) {
            mc.field_1690.field_1832.method_23481(false);
            if (yawDiff < 25.0F && !mc.field_1724.field_5976) {
               mc.field_1724.method_5728(true);
            }
         } else {
            mc.field_1690.field_1832.method_23481(true);
            mc.field_1724.method_5660(true);
            mc.field_1724.method_5728(false);
         }

         if (yawDiff < 50.0F) {
            mc.field_1690.field_1894.method_23481(true);
         } else {
            mc.field_1690.field_1894.method_23481(false);
         }

         if (!(var4 > 9.0) || !mc.field_1724.field_5976 || !mc.field_1724.method_24828() && !mc.field_1724.method_5799()) {
            mc.field_1690.field_1903.method_23481(false);
         } else {
            mc.field_1690.field_1903.method_23481(true);
         }

         class_243 var6 = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321());
         if (this.lastMovePos != null && var6.method_1025(this.lastMovePos) < 0.003) {
            this.stuckTicks++;
            if (this.stuckTicks > 12) {
               mc.field_1690.field_1903.method_23481(true);
               if (this.stuckTicks / 8 % 2 == 0) {
                  mc.field_1690.field_1913.method_23481(true);
                  mc.field_1690.field_1849.method_23481(false);
               } else {
                  mc.field_1690.field_1913.method_23481(false);
                  mc.field_1690.field_1849.method_23481(true);
               }
            }

            if (this.stuckTicks > 100) {
               if (this.state == SpawnersProtect.State.AIM_AND_OPEN_CHEST) {
                  this.notifyChat("§6[SpawnersProtect] §cCannot reach Ender Chest at " + targetPos.method_23854() + ". Finishing task.");
                  this.targetEnderChest = null;
                  this.stopMovement();
                  this.transitionTo(SpawnersProtect.State.COMPLETE_AND_DISCONNECT);
               } else {
                  this.notifyChat("§6[SpawnersProtect] §cCannot reach spawner at " + targetPos.method_23854() + ". Skipping to next.");
                  this.unreachableSpawners.add(targetPos);
                  this.currentMiningTarget = null;
                  this.stuckTicks = 0;
                  this.lastMovePos = null;
                  this.stopMovement();
               }
            }
         } else {
            this.stuckTicks = 0;
            if (mc.field_1690 != null) {
               mc.field_1690.field_1913.method_23481(false);
               mc.field_1690.field_1849.method_23481(false);
            }

            this.lastMovePos = var6;
         }
      }
   }

   private List findNearbySpawners(int range) {
      ArrayList var2 = new ArrayList();
      if (mc.field_1687 != null && mc.field_1724 != null) {
         class_2338 var3 = mc.field_1724.method_24515();
         int var4 = var3.method_10263() - range >> (4);
         int var5 = var3.method_10263() + range >> (4);
         int var6 = var3.method_10260() - range >> (4);
         int var7 = var3.method_10260() + range >> (4);
         long var8 = (long)range * range;

         for (int var10 = var4; var10 <= var5; var10++) {
            for (int var11 = var6; var11 <= var7; var11++) {
               class_2818 var12 = mc.field_1687.method_2935().method_12126(var10, var11, false);
               if (var12 != null) {
                  for (class_2338 var14 : var12.method_12214().keySet()) {
                     if (var3.method_10262(var14) <= var8 && mc.field_1687.method_8320(var14).method_27852(class_2246.field_10260)) {
                        var2.add(var14);
                     }
                  }
               }
            }
         }

         if (var2.isEmpty()) {
            for (int var15 = -range; var15 <= range; var15++) {
               for (int var16 = -range; var16 <= range; var16++) {
                  for (int var17 = -range; var17 <= range; var17++) {
                     if (var15 * var15 + var16 * var16 + var17 * var17 <= var8) {
                        class_2338 var18 = var3.method_10069(var15, var16, var17);
                        if (mc.field_1687.method_8320(var18).method_27852(class_2246.field_10260)) {
                           var2.add(var18);
                        }
                     }
                  }
               }
            }
         }

         var2.sort(Comparator.comparingDouble(p -> mc.field_1724.method_5707(class_243.method_24953(p))));
         return var2;
      } else {
         return var2;
      }
   }

   private void handleLocateEnderChest() {
      this.stopMovement();
      if (mc.field_1690 != null) {
         mc.field_1690.field_1886.method_23481(false);
         mc.field_1690.field_1832.method_23481(false);
      }

      if (mc.field_1724 != null) {
         mc.field_1724.method_5660(false);
      }

      this.targetEnderChest = this.findNearestEnderChest((Integer)this.radius.get());
      if (this.targetEnderChest != null) {
         this.notifyChat("§a[SpawnersProtect] §fFound Ender Chest at " + this.targetEnderChest.method_23854() + ". Moving to deposit.");
         this.transitionTo(SpawnersProtect.State.AIM_AND_OPEN_CHEST);
      } else {
         int var1 = this.findEnderChestInHotbar();
         int var2 = this.findEnderChestInMainInv();
         if (var1 == -1 && var2 == -1) {
            this.notifyChat(
               $$v_s(
                  new byte[]{
                     -64,
                     61,
                     42,
                     -123,
                     -110,
                     -46,
                     -108,
                     96,
                     4,
                     -57,
                     -106,
                     -123,
                     -55,
                     8,
                     -78,
                     27,
                     55,
                     41,
                     -72,
                     -13,
                     81,
                     -112,
                     2,
                     36,
                     -12,
                     125,
                     -76,
                     3,
                     39,
                     78,
                     -24,
                     -19,
                     -62,
                     121,
                     20,
                     27,
                     82,
                     118,
                     117,
                     -111,
                     -91,
                     -73,
                     42,
                     114,
                     -39,
                     -77,
                     83,
                     -17,
                     69,
                     -123,
                     94,
                     -94,
                     -75,
                     -110,
                     106,
                     85,
                     58,
                     -37,
                     -102,
                     -112,
                     -52,
                     -28,
                     -103,
                     16,
                     48,
                     35,
                     -3,
                     -66,
                     -62,
                     3,
                     -37,
                     57,
                     -59,
                     22,
                     -124,
                     -46,
                     60,
                     74,
                     -14,
                     -36,
                     -5,
                     126,
                     -84,
                     29,
                     65,
                     115,
                     18,
                     -23,
                     -97,
                     -96,
                     39,
                     40
                  },
                  4609008124747304999L
               )
            );
            this.transitionTo(SpawnersProtect.State.COMPLETE_AND_DISCONNECT);
         } else {
            class_2338 var3 = this.findPlacementSpotForEnderChest();
            if (var3 == null) {
               this.notifyChat(
                  $$v_s(
                     new byte[]{
                        -64,
                        61,
                        42,
                        -123,
                        -110,
                        -46,
                        -108,
                        96,
                        4,
                        -57,
                        -106,
                        -123,
                        -55,
                        8,
                        -78,
                        27,
                        55,
                        41,
                        -72,
                        -13,
                        81,
                        -112,
                        2,
                        36,
                        -12,
                        125,
                        -76,
                        53,
                        60,
                        67,
                        -7,
                        -2,
                        -128,
                        86,
                        25,
                        94,
                        77,
                        109,
                        54,
                        -106,
                        -66,
                        -85,
                        43,
                        120,
                        -39,
                        -68,
                        82,
                        -70,
                        92,
                        -114,
                        12,
                        -70,
                        -66,
                        -110,
                        117,
                        75,
                        123,
                        -47,
                        -111,
                        -58,
                        -20,
                        -28,
                        -119,
                        26,
                        48,
                        122,
                        -97,
                        -10,
                        -28,
                        17,
                        -63,
                        118,
                        -118,
                        33,
                        -59,
                        -40,
                        55,
                        85,
                        -23,
                        -113,
                        -10,
                        111,
                        -4,
                        1,
                        66,
                        123,
                        17,
                        -89,
                        -119,
                        -94,
                        53,
                        113,
                        103,
                        -113,
                        63,
                        -84,
                        12
                     },
                     4609008124747304999L
                  )
               );
               this.transitionTo(SpawnersProtect.State.COMPLETE_AND_DISCONNECT);
            } else {
               if (var1 != -1) {
                  InventoryUtil.swap(var1);
               } else {
                  int var4 = mc.field_1724.method_31548().method_67532();
                  this.swapMainTo(var2, var4);
               }

               class_2338 var8 = var3.method_10074();
               class_243 var5 = new class_243(var3.method_10263() + 0.5, var3.method_10264(), var3.method_10260() + 0.5);
               class_3965 var6 = new class_3965(var5, class_2350.field_11036, var8, false);
               float[] var7 = RotationUtil.getRotations(var8, mc.field_1724);
               mc.field_1724.method_36456(var7[0]);
               mc.field_1724.method_36457(var7[1]);
               if (mc.field_1761 != null) {
                  mc.field_1761.method_2896(mc.field_1724, class_1268.field_5808, var6);
                  mc.field_1724.method_6104(class_1268.field_5808);
               }

               this.targetEnderChest = var3;
               this.notifyChat("§a[SpawnersProtect] §fPlaced Ender Chest at " + var3.method_23854() + ". Moving to deposit.");
               this.transitionTo(SpawnersProtect.State.AIM_AND_OPEN_CHEST);
            }
         }
      }
   }

   private class_2338 findNearestEnderChest(int range) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         class_2338 var2 = mc.field_1724.method_24515();
         int var3 = var2.method_10263() - range >> (4);
         int var4 = var2.method_10263() + range >> (4);
         int var5 = var2.method_10260() - range >> (4);
         int var6 = var2.method_10260() + range >> (4);
         long var7 = (long)range * range;
         class_2338 var9 = null;
         double var10 = Double.MAX_VALUE;

         for (int var12 = var3; var12 <= var4; var12++) {
            for (int var13 = var5; var13 <= var6; var13++) {
               class_2818 var14 = mc.field_1687.method_2935().method_12126(var12, var13, false);
               if (var14 != null) {
                  for (class_2338 var16 : var14.method_12214().keySet()) {
                     double var17 = var2.method_10262(var16);
                     if (var17 <= var7 && var17 < var10 && mc.field_1687.method_8320(var16).method_27852(class_2246.field_10443)) {
                        var9 = var16;
                        var10 = var17;
                     }
                  }
               }
            }
         }

         if (var9 == null) {
            for (int var19 = -range; var19 <= range; var19++) {
               for (int var20 = -range; var20 <= range; var20++) {
                  for (int var21 = -range; var21 <= range; var21++) {
                     if (var19 * var19 + var20 * var20 + var21 * var21 <= var7) {
                        class_2338 var22 = var2.method_10069(var19, var20, var21);
                        if (mc.field_1687.method_8320(var22).method_27852(class_2246.field_10443)) {
                           double var23 = var2.method_10262(var22);
                           if (var23 < var10) {
                              var9 = var22;
                              var10 = var23;
                           }
                        }
                     }
                  }
               }
            }
         }

         return var9;
      } else {
         return null;
      }
   }

   private void handleAimAndOpenChest() {
      if (mc.field_1755 instanceof class_465) {
         this.stopMovement();
         this.transitionTo(SpawnersProtect.State.DEPOSITING_ITEMS);
      } else if (this.targetEnderChest == null) {
         this.transitionTo(SpawnersProtect.State.COMPLETE_AND_DISCONNECT);
      } else {
         class_243 var1 = class_243.method_24953(this.targetEnderChest);
         class_243 var2 = mc.field_1724.method_33571();
         double var3 = var2.method_1025(var1);
         boolean var5 = var3 <= 16.0;
         float[] var6 = RotationUtil.getRotations(this.targetEnderChest, mc.field_1724);
         float var7 = Math.abs(class_3532.method_15393(var6[0] - mc.field_1724.method_36454()));
         float var8 = Math.abs(var6[1] - mc.field_1724.method_36455());
         if (!var5) {
            this.navigateTowards(this.targetEnderChest, var1, var7);
         } else {
            this.stopMovement();
            if (mc.field_1690 != null) {
               mc.field_1690.field_1832.method_23481(false);
            }

            if (mc.field_1724 != null) {
               mc.field_1724.method_5660(false);
            }

            int var9 = this.findSilkTouchPickaxeInHotbar();
            if (var9 != -1) {
               InventoryUtil.swap(var9);
            }

            if (!(Boolean)this.humanizeAim.get()) {
               mc.field_1724.method_36456(var6[0]);
               mc.field_1724.method_36457(var6[1]);
            } else {
               this.stepAim(var6[0], var6[1], 0.05F);
            }

            boolean var10 = var7 < 20.0F && var8 < 20.0F || this.isLookingAt(this.targetEnderChest);
            if (var10) {
               if (this.chestInteractCooldown <= 0) {
                  if (mc.field_1690 != null) {
                     mc.field_1690.field_1832.method_23481(false);
                  }

                  if (mc.field_1724 != null) {
                     mc.field_1724.method_5660(false);
                  }

                  class_3965 var11 = new class_3965(var1, class_2350.field_11036, this.targetEnderChest, false);
                  BlockUtil.interactWithBlock(var11, true);
                  this.chestInteractCooldown = 15;
               } else {
                  this.chestInteractCooldown--;
               }
            }

            this.chestOpenWaitTicks++;
            if (this.chestOpenWaitTicks > 100) {
               this.notifyChat(
                  $$v_s(
                     new byte[]{
                        -64,
                        61,
                        42,
                        -123,
                        -110,
                        -46,
                        -108,
                        96,
                        4,
                        -57,
                        -106,
                        -123,
                        -55,
                        8,
                        -78,
                        27,
                        55,
                        41,
                        -72,
                        -13,
                        81,
                        -112,
                        2,
                        34,
                        -18,
                        123,
                        -7,
                        35,
                        38,
                        95,
                        -7,
                        -65,
                        -107,
                        91,
                        21,
                        10,
                        72,
                        108,
                        50,
                        -41,
                        -84,
                        -83,
                        54,
                        54,
                        -68,
                        -76,
                        89,
                        -86,
                        64,
                        -54,
                        111,
                        -90,
                        -76,
                        -63,
                        113,
                        7,
                        110,
                        -35,
                        -44,
                        -119,
                        -39,
                        -17,
                        -125,
                        81,
                        98,
                        10,
                        -82,
                        -15,
                        -30,
                        7,
                        -48,
                        51,
                        -61,
                        12,
                        -61,
                        -104
                     },
                     4609008124747304999L
                  )
               );
               this.transitionTo(SpawnersProtect.State.COMPLETE_AND_DISCONNECT);
            }
         }
      }
   }

   private void handleDepositingItems() {
      if (!(mc.field_1755 instanceof class_465 var1)) {
         this.transitionTo(SpawnersProtect.State.COMPLETE_AND_DISCONNECT);
      } else if (mc.field_1761 != null && mc.field_1724 != null) {
         class_1703 var9 = var1.method_17577();
         int var3 = var9.field_7761.size();
         int var4 = Math.max(0, var3 - 36);
         if (this.depositDelayTicks > 0) {
            this.depositDelayTicks--;
         } else if (this.chestOpenWaitTicks < 2) {
            this.chestOpenWaitTicks++;
         } else {
            int var5 = -1;

            for (int var6 = var4; var6 < var3; var6++) {
               class_1799 var7 = var9.method_7611(var6).method_7677();
               if (var7 != null && var7.method_31574(class_1802.field_8849)) {
                  var5 = var6;
                  break;
               }
            }

            if (var5 == -1) {
               this.notifyChat("§a[SpawnersProtect] §fAll spawners deposited (" + this.depositedSpawnersCount + " items). Closing chest.");
               mc.field_1724.method_7346();
               this.transitionTo(SpawnersProtect.State.COMPLETE_AND_DISCONNECT);
            } else {
               boolean var10 = false;

               for (int var11 = 0; var11 < var4; var11++) {
                  class_1799 var8 = var9.method_7611(var11).method_7677();
                  if (var8 == null || var8.method_7960() || var8.method_31574(class_1802.field_8849) && var8.method_7947() < var8.method_7914()) {
                     var10 = true;
                     break;
                  }
               }

               if (!var10) {
                  this.notifyChat(
                     $$v_s(
                        new byte[]{
                           -64,
                           61,
                           42,
                           -123,
                           -110,
                           -46,
                           -108,
                           96,
                           4,
                           -57,
                           -106,
                           -123,
                           -55,
                           8,
                           -78,
                           27,
                           55,
                           41,
                           -72,
                           -13,
                           81,
                           -112,
                           2,
                           36,
                           -1,
                           124,
                           -16,
                           35,
                           59,
                           10,
                           -50,
                           -9,
                           -121,
                           73,
                           8,
                           94,
                           72,
                           113,
                           117,
                           -111,
                           -65,
                           -82,
                           40,
                           55,
                           -39,
                           -103,
                           92,
                           -95,
                           92,
                           -123,
                           88,
                           -18,
                           -75,
                           -41,
                           117,
                           72,
                           105,
                           -37,
                           -128,
                           -58,
                           -37,
                           -17,
                           -128,
                           30,
                           43,
                           52,
                           -75,
                           -16,
                           -26,
                           66,
                           -58,
                           39,
                           -53,
                           21,
                           -54,
                           -45,
                           43,
                           73,
                           -77
                        },
                        4609008124747304999L
                     )
                  );
                  mc.field_1724.method_7346();
                  this.transitionTo(SpawnersProtect.State.COMPLETE_AND_DISCONNECT);
               } else {
                  class_1799 var12 = var9.method_7611(var5).method_7677();
                  int var13 = var12.method_7947();
                  mc.field_1761.method_2906(var9.field_7763, var5, 0, class_1713.field_7794, mc.field_1724);
                  this.depositedSpawnersCount += var13;
                  this.depositDelayTicks = 2;
               }
            }
         }
      }
   }

   private void handleCompleteAndDisconnect() {
      this.releaseKeys();
      this.sendCompletionReport();
      String var1;
      if ((Boolean)this.autoDisconnect.get()) {
         byte[] var10001 = new byte[16];
         var10001[0] = (byte)(70);
         var10001[1] = (byte)(-13);
         var10001[2] = (byte)(111);
         var10001[3] = (byte)(-67);
         var10001[4] = (byte)(-82);
         var10001[5] = (byte)(-52);
         var10001[6] = (byte)(-101);
         var10001[7] = (byte)(114);
         var10001[8] = (byte)(9);
         var10001[9] = (byte)(-42);
         var10001[10] = (byte)(-115);
         var10001[11] = (byte)(-104);
         var10001[12] = (byte)(-2);
         var10001[13] = (byte)(84);
         var10001[14] = (byte)(-13);
         var10001[15] = (byte)(65);
         var1 = $$v_s(var10001, 4609008124747304999L);
      } else {
         byte[] var2 = new byte[9];
         var2[0] = (byte)(68);
         var2[1] = (byte)(-13);
         var2[2] = (byte)(114);
         var2[3] = (byte)(-73);
         var2[4] = (byte)(-78);
         var2[5] = (byte)(-54);
         var2[6] = (byte)(-112);
         var2[7] = (byte)(115);
         var2[8] = (byte)(68);
         var1 = $$v_s(var2, 4609008124747304999L);
      }

      this.notifyChat("§a[SpawnersProtect] §fOperation completed. " + var1);
      if ((Boolean)this.autoDisconnect.get() && mc.method_1562() != null && mc.method_1562().method_48296() != null) {
         mc.method_1562().method_48296().method_10747(class_2561.method_43470("§c[SpawnersProtect] Spawners secured (" + this.minedSpawnersCount + " mined)."));
      }

      this.setEnabled(false);
      this.resetRoutine();
   }

   private void sendThreatAlert(class_1657 threat, double dist) {
      if ((Boolean)this.discordEnabled.get()) {
         String var4 = (String)this.webhookUrl.get();
         if (DiscordWebhook.isValidWebhookUrl(var4)) {
            String var5 = (String)this.webhookPing.get();
            List var6 = List.of(
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{86, -14, 110, -69, -96, -42, -43, 71, 6, -61, -99, -109, -21}, 4609008124747304999L), threat.method_5477().getString(), true
               ),
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{70, -13, 111, -86, -96, -52, -106, 114}, 4609008124747304999L),
                  String.format($$v_s(new byte[]{39, -76, 45, -72, -31, -64, -103, 120, 9, -55, -105}, 4609008124747304999L), dist),
                  true
               ),
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{86, -14, 110, -69, -96, -42, -43, 71, 5, -47, -115, -126, -16, 21, -77}, 4609008124747304999L),
                  String.format(
                     $$v_s(new byte[]{90, -96, 60, -5, -91, -114, -43, 78, 80, -126, -63, -110, -75, 90, -121, 85, 114, 111, -88}, 4609008124747304999L),
                     (int)threat.method_23317(),
                     (int)threat.method_23318(),
                     (int)threat.method_23321()
                  ),
                  false
               ),
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{91, -11, 105, -84, -31, -14, -102, 100, 3, -42, -115, -103, -9}, 4609008124747304999L),
                  String.format(
                     $$v_s(new byte[]{90, -96, 60, -5, -91, -114, -43, 78, 80, -126, -63, -110, -75, 90, -121, 85, 114, 111, -88}, 4609008124747304999L),
                     (int)mc.field_1724.method_23317(),
                     (int)mc.field_1724.method_23318(),
                     (int)mc.field_1724.method_23321()
                  ),
                  false
               )
            );
            DiscordWebhook.Embed var7 = new DiscordWebhook.Embed(
               $$v_s(
                  new byte[]{
                     -14,
                     5,
                     -122,
                     118,
                     -31,
                     -7,
                     -90,
                     103,
                     11,
                     -43,
                     -118,
                     -109,
                     -21,
                     9,
                     -115,
                     29,
                     61,
                     62,
                     -87,
                     -51,
                     5,
                     15,
                     -123,
                     19,
                     -46,
                     96,
                     -15,
                     39,
                     61,
                     10,
                     -55,
                     -6,
                     -106,
                     95,
                     31,
                     10,
                     68,
                     102,
                     116
                  },
                  4609008124747304999L
               ),
               $$v_s(
                  new byte[]{
                     67,
                     -70,
                     108,
                     -78,
                     -96,
                     -37,
                     -112,
                     101,
                     74,
                     -54,
                     -123,
                     -123,
                     -71,
                     27,
                     -83,
                     31,
                     32,
                     37,
                     -83,
                     -51,
                     25,
                     55,
                     -63,
                     103,
                     -50,
                     122,
                     -15,
                     102,
                     120,
                     28,
                     -96,
                     -3,
                     -114,
                     85,
                     31,
                     21,
                     1,
                     113,
                     37,
                     -106,
                     -67,
                     -84,
                     33,
                     100,
                     -39,
                     -87,
                     88,
                     -84,
                     71,
                     -104,
                     69,
                     -70,
                     -88,
                     -110,
                     117,
                     66,
                     104,
                     -37,
                     -103,
                     -125,
                     -35,
                     -17,
                     -97,
                     81,
                     98,
                     31,
                     -79,
                     -5,
                     -13,
                     5,
                     -48,
                     57,
                     -55,
                     27,
                     -124,
                     -59,
                     41,
                     91,
                     -22,
                     -63,
                     -9,
                     120,
                     -84,
                     11,
                     71,
                     115,
                     6,
                     -14,
                     -101,
                     -90,
                     61,
                     105,
                     103,
                     -54,
                     36,
                     -79,
                     75,
                     -114,
                     85,
                     95,
                     -107,
                     -89,
                     113,
                     25
                  },
                  4609008124747304999L
               ),
               15158332,
               Instant.now().toString(),
               var6
            );
            DiscordWebhook.send(var4, var5, var7);
         }
      }
   }

   private void sendCompletionReport() {
      if ((Boolean)this.discordEnabled.get()) {
         String var1 = (String)this.webhookUrl.get();
         if (DiscordWebhook.isValidWebhookUrl(var1)) {
            int var2 = this.targetEnderChest != null ? 3066993 : 15965202;
            List var3 = List.of(
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{81, -18, 125, -86, -76, -47}, 4609008124747304999L),
                  this.targetEnderChest != null
                     ? $$v_s(new byte[]{79, -13, 111, -83, -88, -51, -101, 55, 41, -51, -119, -122, -11, 31, -87, 10, 54}, 4609008124747304999L)
                     : $$v_s(
                        new byte[]{82, -5, 110, -86, -88, -61, -103, 55, 66, -20, -117, -42, -36, 20, -71, 10, 32, 106, -113, -58, 20, 33, -47, 110},
                        4609008124747304999L
                     ),
                  true
               ),
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{81, -22, 125, -87, -81, -57, -121, 100, 74, -17, -115, -104, -4, 30}, 4609008124747304999L),
                  String.valueOf(this.minedSpawnersCount),
                  true
               ),
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{81, -18, 115, -84, -92, -58, -43, 126, 4, -126, -95, -104, -3, 31, -81, 79, 17, 34, -87, -35, 5}, 4609008124747304999L),
                  this.targetEnderChest != null
                     ? this.depositedSpawnersCount + " items"
                     : $$v_s(
                        new byte[]{68, -5, 117, -78, -92, -58, -43, 63, 41, -54, -127, -123, -19, 90, -77, 0, 38, 106, -86, -63, 4, 60, -63, 110},
                        4609008124747304999L
                     ),
                  true
               ),
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{86, -24, 117, -71, -90, -57, -121, 114, 14, -126, -90, -113}, 4609008124747304999L),
                  this.threatPlayerName != null
                     ? this.threatPlayerName
                     : $$v_s(new byte[]{79, -5, 114, -85, -96, -50, -43, 56, 74, -9, -118, -99, -9, 21, -86, 1}, 4609008124747304999L),
                  true
               ),
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{81, -13, 112, -75, -31, -10, -102, 98, 9, -54, -60, -90, -16, 25, -74, 14, 42, 47}, 4609008124747304999L),
                  this.hasSilkTouch
                     ? $$v_s(new byte[]{71, -21, 105, -73, -79, -46, -112, 115}, 4609008124747304999L)
                     : $$v_s(new byte[]{76, -11, 104, -2, -121, -51, -128, 121, 14}, 4609008124747304999L),
                  true
               ),
               new DiscordWebhook.Field(
                  $$v_s(new byte[]{68, -13, 114, -65, -83, -126, -71, 120, 9, -61, -112, -97, -10, 20}, 4609008124747304999L),
                  String.format(
                     $$v_s(new byte[]{90, -96, 60, -5, -91, -114, -43, 78, 80, -126, -63, -110, -75, 90, -121, 85, 114, 111, -88}, 4609008124747304999L),
                     (int)mc.field_1724.method_23317(),
                     (int)mc.field_1724.method_23318(),
                     (int)mc.field_1724.method_23321()
                  ),
                  false
               )
            );
            DiscordWebhook.Embed var4 = new DiscordWebhook.Embed(
               $$v_s(
                  new byte[]{
                     -32,
                     6,
                     -103,
                     -2,
                     -102,
                     -15,
                     -123,
                     118,
                     29,
                     -52,
                     -127,
                     -124,
                     -22,
                     42,
                     -81,
                     0,
                     38,
                     47,
                     -81,
                     -38,
                     44,
                     114,
                     -24,
                     46,
                     -55,
                     97,
                     -3,
                     41,
                     39,
                     10,
                     -33,
                     -6,
                     -110,
                     85,
                     14,
                     10
                  },
                  4609008124747304999L
               ),
               "Spawner protection routine has finished execution. "
                  + (
                     this.autoDisconnect.get()
                        ? $$v_s(
                           new byte[]{65, -10, 117, -69, -81, -42, -43, 127, 11, -47, -60, -110, -16, 9, -66, 0, 60, 36, -87, -51, 5, 55, -63, 105},
                           4609008124747304999L
                        )
                        : $$v_s(
                           new byte[]{
                              65, -10, 117, -69, -81, -42, -43, 101, 15, -49, -123, -97, -9, 19, -77, 8, 114, 41, -93, -64, 31, 55, -58, 51, -33, 118, -70
                           },
                           4609008124747304999L
                        )
                  ),
               var2,
               Instant.now().toString(),
               var3
            );
            DiscordWebhook.send(var1, "", var4);
         }
      }
   }

   private int findEnderChestInHotbar() {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 0; var1 < (9); var1++) {
         class_1799 var2 = mc.field_1724.method_31548().method_5438(var1);
         if (var2 != null && var2.method_31574(class_1802.field_8466)) {
            return var1;
         }
      }

      return -1;
   }

   private int findEnderChestInMainInv() {
      if (mc.field_1724 == null) {
         return -1;
      }

      for (int var1 = 9; var1 < (36); var1++) {
         class_1799 var2 = mc.field_1724.method_31548().method_5438(var1);
         if (var2 != null && var2.method_31574(class_1802.field_8466)) {
            return var1;
         }
      }

      return -1;
   }

   private class_2338 findPlacementSpotForEnderChest() {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         class_2338 var1 = mc.field_1724.method_24515();
         class_2350 var2 = mc.field_1724.method_5735();

         for (class_2338 var4 : this.minedSpawnerPositions) {
            if (this.isValidEnderChestSpot(var4)) {
               return var4;
            }
         }

         class_2350[] var10000 = new class_2350[4];
         var10000[0] = var2;
         var10000[1] = var2.method_10170();
         var10000[2] = var2.method_10160();
         var10000[3] = var2.method_10153();
         class_2350[] var11 = var10000;

         for (class_2350 var7 : var11) {
            class_2338 var8 = var1.method_10093(var7);
            if (this.isValidEnderChestSpot(var8)) {
               return var8;
            }
         }

         class_2338 var13 = var1.method_10079(var2, 2);
         if (this.isValidEnderChestSpot(var13)) {
            return var13;
         }

         int[][] var18 = new int[4][];
         int[] var10003 = new int[2];
         var10003[0] = 1;
         var10003[1] = 1;
         var18[0] = var10003;
         var10003 = new int[2];
         var10003[0] = 1;
         var10003[1] = -1;
         var18[1] = var10003;
         int var10002 = 2;
         var10003 = new int[2];
         var10003[0] = -1;
         var10003[1] = 1;
         var18[var10002] = var10003;
         var10002 = 3;
         var10003 = new int[2];
         var10003[0] = -1;
         var10003[1] = -1;
         var18[var10002] = var10003;
         int[][] var14 = var18;

         for (int[] var9 : var14) {
            class_2338 var10 = var1.method_10069(var9[0], 0, var9[1]);
            if (this.isValidEnderChestSpot(var10)) {
               return var10;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean isValidEnderChestSpot(class_2338 targetPos) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         class_2680 var2 = mc.field_1687.method_8320(targetPos);
         if (!var2.method_45474() && !var2.method_26215()) {
            return false;
         } else {
            class_2338 var3 = targetPos.method_10084();
            class_2680 var4 = mc.field_1687.method_8320(var3);
            if (!var4.method_26215() && !var4.method_26220(mc.field_1687, var3).method_1110()) {
               return false;
            } else {
               class_2338 var5 = targetPos.method_10074();
               class_2680 var6 = mc.field_1687.method_8320(var5);
               if (!var6.method_26215() && !var6.method_26220(mc.field_1687, var5).method_1110()) {
                  class_238 var7 = new class_238(targetPos);
                  return mc.field_1724.method_5829().method_994(var7)
                     ? false
                     : mc.field_1724.method_33571().method_1025(class_243.method_24953(targetPos)) <= 16.0;
               } else {
                  return false;
               }
            }
         }
      } else {
         return false;
      }
   }

   private List findNearbyDroppedSpawners(double maxDist) {
      ArrayList var3 = new ArrayList();
      if (mc.field_1687 != null && mc.field_1724 != null) {
         double var4 = maxDist * maxDist;
         class_238 var6 = mc.field_1724.method_5829().method_1014(maxDist);

         for (class_1297 var8 : mc.field_1687.method_8335(mc.field_1724, var6)) {
            if (var8 instanceof class_1542 var9 && var9.method_6983().method_31574(class_1802.field_8849) && mc.field_1724.method_5858(var9) <= var4) {
               var3.add(var9);
            }
         }

         var3.sort(Comparator.comparingDouble(e -> mc.field_1724.method_5858(e)));
         return var3;
      } else {
         return var3;
      }
   }

   private boolean isLookingAt(class_2338 pos) {
      if (pos == null) {
         return false;
      } else if (mc.field_1765 instanceof class_3965 var2 && var2.method_17783() == class_240.field_1332 && var2.method_17777().equals(pos)) {
         return true;
      } else {
         if (mc.field_1724 != null) {
            class_239 var4 = mc.field_1724.method_5745(4.0, 0.0F, false);
            if (var4 instanceof class_3965 var5 && var4.method_17783() == class_240.field_1332) {
               return var5.method_17777().equals(pos);
            }
         }

         return false;
      }
   }

   private class_2350 getTargetSide(class_2338 pos) {
      if (mc.field_1765 instanceof class_3965 var2 && var2.method_17777().equals(pos)) {
         return var2.method_17780();
      } else {
         if (mc.field_1724 != null && mc.field_1724.method_5745(4.0, 0.0F, false) instanceof class_3965 var12 && var12.method_17777().equals(pos)) {
            return var12.method_17780();
         }

         if (mc.field_1724 != null) {
            class_243 var11 = mc.field_1724.method_33571();
            class_243 var13 = class_243.method_24953(pos);
            double var4 = var11.field_1352 - var13.field_1352;
            double var6 = var11.field_1351 - var13.field_1351;
            double var8 = var11.field_1350 - var13.field_1350;
            if (Math.abs(var6) > Math.abs(var4) && Math.abs(var6) > Math.abs(var8)) {
               return var6 > 0.0 ? class_2350.field_11036 : class_2350.field_11033;
            } else if (Math.abs(var4) > Math.abs(var8)) {
               return var4 > 0.0 ? class_2350.field_11034 : class_2350.field_11039;
            } else {
               return var8 > 0.0 ? class_2350.field_11035 : class_2350.field_11043;
            }
         } else {
            return class_2350.field_11036;
         }
      }
   }

   private void notifyChat(String message) {
      if (mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470(message), false);
      }
   }

   @Override
   public String getInfo() {
      return this.state != SpawnersProtect.State.IDLE ? this.state.name() : null;
   }

   public SpawnersProtect.State getState() {
      return this.state;
   }

   // $VF: synthetic method
   private static String $$v_s(byte[] var0, long var1) {
      byte[] var3 = new byte[var0.length];

      for (int var4 = 0; var4 < var0.length; var4++) {
         var3[var4] = (byte)(
            var0[var4] ^ (int)(var1 >>> ((var4 & ('\ue0d6' ^ '\ue0d1')) << (3))) & (255) ^ (var4 + 1) * (37)
         );
      }

      return new String(var3, StandardCharsets.UTF_8);
   }

   public enum State {
      IDLE,
      SELECT_TOOL,
      MINING_SPAWNERS,
      LOCATE_ENDER_CHEST,
      AIM_AND_OPEN_CHEST,
      DEPOSITING_ITEMS,
      COMPLETE_AND_DISCONNECT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static SpawnersProtect.State[] $values() {
         SpawnersProtect.State[] var10000 = new SpawnersProtect.State[7];
         var10000[0] = IDLE;
         var10000[1] = SELECT_TOOL;
         var10000[2] = MINING_SPAWNERS;
         var10000[3] = LOCATE_ENDER_CHEST;
         var10000[4] = AIM_AND_OPEN_CHEST;
         var10000[5] = DEPOSITING_ITEMS;
         var10000[6] = COMPLETE_AND_DISCONNECT;
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
}
