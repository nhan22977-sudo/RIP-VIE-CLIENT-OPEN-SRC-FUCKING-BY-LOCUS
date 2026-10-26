package me.drake.vieclient.modules.donut;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ButtonSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.core.setting.StringSetting;
import me.drake.vieclient.gui.nvg.ui.components.TunnelFilterScreen;
import me.drake.vieclient.util.DiscordWebhook;
import me.drake.vieclient.util.RenderUtil;
import me.drake.vieclient.utils.player.InvUtils;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1923;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2281;
import net.minecraft.class_2315;
import net.minecraft.class_2325;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2377;
import net.minecraft.class_238;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_2480;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2663;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_304;
import net.minecraft.class_3486;
import net.minecraft.class_3489;
import net.minecraft.class_3532;
import net.minecraft.class_3708;
import net.minecraft.class_3865;
import net.minecraft.class_3965;
import net.minecraft.class_746;
import net.minecraft.class_8886;
import net.minecraft.class_2338.class_2339;
import net.minecraft.class_239.class_240;

public class TunnelBaseFinder extends Module {
   private final ButtonSetting openFilter = (ButtonSetting)this.addSetting(
      new ButtonSetting(
         "Open Filter",
         "Open the storage/spawner block filter",
         this::openFilterScreen
      )
   );
   private final IntSetting rotateSpeed = (IntSetting)this.addSetting(
      new IntSetting(
         "Rotate Speed",
         "Camera turning speed (1 = slowest, 50 = fastest).",
         30,
         1,
         50
      )
   );
   private final Setting humanize = this.addSetting(
      new BoolSetting(
            "Humanize",
            "Natural human acceleration, micro-tremor, and smooth mining camera transitions.",
            true
         )
         .setVisibleInGui(false)
   );
   private final Setting disconnectPlayers = this.addSetting(
      new BoolSetting(
         "Disconnect on Player",
         "Disconnect when another player is nearby.",
         true
      )
   );
   private final Setting playerRange = this.addSetting(
      new DoubleSetting(
            "Player Range",
            "Player detection range.",
            64.0,
            1.0,
            128.0
         )
         .setVisibleInGui(false)
   );
   private final Setting ignoreFriends = this.addSetting(
      new BoolSetting(
            "Ignore Friends",
            "Do not disconnect for friends.",
            false
         )
         .setVisibleInGui(false)
   );
   private final Setting disconnectStorage = this.addSetting(
      new BoolSetting(
         "Disconnect on Storage",
         "Disconnect when the storage threshold is reached.",
         true
      )
   );
   private final Setting storageThreshold = this.addSetting(
      new IntSetting(
            "Storage Threshold",
            "Number of matching storage blocks treated as a base.",
            8,
            1,
            64
         )
         .setVisibleInGui(false)
   );
   private final Setting storageRange = this.addSetting(
      new IntSetting(
            "Storage Range",
            "Horizontal and vertical storage scan radius.",
            64,
            2,
            96
         )
         .setVisibleInGui(false)
   );
   public final BoolSetting disconnectSpawner = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Disconnect on Spawner",
         "Disconnect when a mob spawner is found nearby.",
         true
      )
   );
   private final Setting spawnerRange = this.addSetting(
      new IntSetting(
            "Spawner Range",
            "Horizontal and vertical spawner scan radius.",
            64,
            2,
            96
         )
         .setVisibleInGui(false)
   );
   private final Setting scanDelay = this.addSetting(
      new IntSetting(
            "Storage Scan Delay",
            "Ticks between storage/spawner scans.",
            10,
            1,
            40
         )
         .setVisibleInGui(false)
   );
   public final Setting chests = this.addSetting(
      new BoolSetting(
            "Chests",
            "Count normal and trapped chests.",
            true
         )
         .setVisibleInGui(false)
   );
   public final Setting barrels = this.addSetting(
      new BoolSetting(
            "Barrels",
            "Count barrels.",
            true
         )
         .setVisibleInGui(false)
   );
   public final Setting shulkers = this.addSetting(
      new BoolSetting(
            "Shulker Boxes",
            "Count all shulker box colors.",
            true
         )
         .setVisibleInGui(false)
   );
   public final Setting hoppers = this.addSetting(
      new BoolSetting(
            "Hoppers",
            "Count hoppers.",
            true
         )
         .setVisibleInGui(false)
   );
   public final Setting furnaces = this.addSetting(
      new BoolSetting(
            "Furnaces",
            "Count furnaces, smokers and blast furnaces.",
            false
         )
         .setVisibleInGui(false)
   );
   public final Setting dispensers = this.addSetting(
      new BoolSetting(
            "Dispensers Droppers",
            "Count dispensers and droppers.",
            false
         )
         .setVisibleInGui(false)
   );
   public final Setting crafters = this.addSetting(
      new BoolSetting(
            "Crafters",
            "Count crafter blocks (1.21+).",
            false
         )
         .setVisibleInGui(false)
   );
   private final IntSetting bottleSlot = (IntSetting)this.addSetting(
      new IntSetting(
         "Bottle Hotbar Slot",
         "Hotbar slot containing XP bottles (1-9).",
         9,
         1,
         9
      )
   );
   private final Setting autoRepair = this.addSetting(
      new BoolSetting(
            "Auto Repair",
            "Put a damaged pickaxe in offhand and throw XP bottles.",
            true
         )
         .setVisibleInGui(false)
   );
   private final Setting repairAt = this.addSetting(
      new IntSetting(
            "Repair At Percent",
            "Start repairing at or below this durability.",
            25,
            1,
            99
         )
         .setVisibleInGui(false)
   );
   private final Setting repairTo = this.addSetting(
      new IntSetting(
            "Repair To Percent",
            "Stop repairing at this durability.",
            95,
            2,
            100
         )
         .setVisibleInGui(false)
   );
   private final Setting buyBottles = this.addSetting(
      new BoolSetting(
            "Buy Bottles If Missing",
            "Open /shop and buy XP bottles when the slot is empty.",
            true
         )
         .setVisibleInGui(false)
   );
   private final Setting buyTotem = this.addSetting(
      new BoolSetting(
            "Buy Totem After Repair",
            "Open /shop and buy/equip a totem after repairing.",
            true
         )
         .setVisibleInGui(false)
   );
   private final Setting shopCommand = this.addSetting(
      new StringSetting(
            "Shop Command",
            "Command without the leading slash.",
            "shop"
         )
         .setVisibleInGui(false)
   );
   private final Setting categoryName = this.addSetting(
      new StringSetting(
            "Shop Category Name",
            "Text of the shop category item.",
            "gear"
         )
         .setVisibleInGui(false)
   );
   private final Setting totemName = this.addSetting(
      new StringSetting(
            "Totem Name",
            "Text used to find the totem item.",
            "totem"
         )
         .setVisibleInGui(false)
   );
   private final Setting bottleMenuSlot = this.addSetting(
      new IntSetting(
            "Bottle Menu Slot",
            "Shop slot that opens the bottle screen.",
            14,
            1,
            54
         )
         .setVisibleInGui(false)
   );
   private final Setting bottleQtySlotA = this.addSetting(
      new IntSetting(
            "Bottle Quantity Slot 1",
            "First slot to click in the bottle screen.",
            17,
            1,
            54
         )
         .setVisibleInGui(false)
   );
   private final Setting bottleQtySlotB = this.addSetting(
      new IntSetting(
            "Bottle Quantity Slot 2",
            "Second slot to click in the bottle screen.",
            18,
            1,
            54
         )
         .setVisibleInGui(false)
   );
   private final Setting bottleClickDelay = this.addSetting(
      new IntSetting(
            "Bottle Click Delay",
            "Ticks between bottle-purchase clicks.",
            10,
            2,
            40
         )
         .setVisibleInGui(false)
   );
   private final Setting shopTimeout = this.addSetting(
      new IntSetting(
            "Shop Timeout Seconds",
            "Stop waiting if the shop GUI does not appear.",
            8,
            2,
            30
         )
         .setVisibleInGui(false)
   );
   private final Setting bottleMaxRetries = this.addSetting(
      new IntSetting(
            "Bottle Max Retries",
            "Retries when the wrong item lands in the bottle slot.",
            5,
            1,
            20
         )
         .setVisibleInGui(false)
   );
   private final Setting bottleDelay = this.addSetting(
      new IntSetting(
            "Bottle Delay",
            "Ticks between normal XP bottle uses.",
            3,
            2,
            10
         )
         .setVisibleInGui(false)
   );
   private final Setting debugMining = this.addSetting(
      new BoolSetting(
            "Debug Mining",
            "Log mining state every 5 seconds.",
            false
         )
         .setVisibleInGui(false)
   );
   private final Setting localRecovery = this.addSetting(
      new BoolSetting(
            "Local Recovery",
            "Jump or mine the block that is physically blocking the bot.",
            true
         )
         .setVisibleInGui(false)
   );
   public final Setting chestsThreshold = this.addSetting(
      new IntSetting(
            "Chests Threshold",
            "Chest threshold for base detection.",
            8,
            1,
            64
         )
         .setVisibleInGui(false)
   );
   public final Setting barrelsThreshold = this.addSetting(
      new IntSetting(
            "Barrels Threshold",
            "Barrel threshold for base detection.",
            8,
            1,
            64
         )
         .setVisibleInGui(false)
   );
   public final Setting shulkersThreshold = this.addSetting(
      new IntSetting(
            "Shulker Boxes Threshold",
            "Shulker box threshold for base detection.",
            8,
            1,
            64
         )
         .setVisibleInGui(false)
   );
   public final Setting hoppersThreshold = this.addSetting(
      new IntSetting(
            "Hoppers Threshold",
            "Hopper threshold for base detection.",
            8,
            1,
            64
         )
         .setVisibleInGui(false)
   );
   public final Setting furnacesThreshold = this.addSetting(
      new IntSetting(
            "Furnaces Threshold",
            "Furnace threshold for base detection.",
            8,
            1,
            64
         )
         .setVisibleInGui(false)
   );
   public final Setting dispensersThreshold = this.addSetting(
      new IntSetting(
            "Dispensers Droppers Threshold",
            "Dispenser/dropper threshold for base detection.",
            8,
            1,
            64
         )
         .setVisibleInGui(false)
   );
   public final Setting craftersThreshold = this.addSetting(
      new IntSetting(
            "Crafters Threshold",
            "Crafter threshold for base detection.",
            8,
            1,
            64
         )
         .setVisibleInGui(false)
   );
   private final Setting useSeedPrediction = this.addSetting(
      new BoolSetting(
         "Seed Prediction",
         "Use a known world seed to avoid gravel blobs in unloaded chunks.",
         false
      )
   );
   private final Setting worldSeed = this.addSetting(
      new StringSetting(
         "World Seed",
         "World seed. Blank disables prediction.",
         ""
      )
   );
   private boolean webhookSentThisSession;
   private final Setting discordEnabled = this.addSetting(
      new BoolSetting(
         "Discord Webhook",
         "Send a Discord message when a base, spawner or player is found.",
         false
      )
   );
   private final Setting discordUrl = this.addSetting(
      new StringSetting(
         "Webhook URL",
         "Discord webhook URL (stored in plain text in the config).",
         ""
      )
   );
   private final Setting discordPing = this.addSetting(
      new StringSetting(
         "Webhook Ping",
         "Text put in the message content, e.g. <@123456> or @everyone.",
         ""
      )
   );
   private final Setting discordCoords = this.addSetting(
      new BoolSetting(
         "Webhook Coords",
         "Include exact coordinates in the message.",
         true
      )
   );
   private static final int ROTATE_WAIT = 5;
   private static final int SCAN_INTERVAL = 10;
   private static final int HAZARD_SCAN_CHUNKS_PER_TICK = 4;
   private static final long HAZARD_SCAN_BUDGET_NANOS = 4000000L;
   private static final int HAZARD_VERTICAL_RANGE = 12;
   private static final int TURN_SCAN_DISTANCE = 10;
   private static final int LOCAL_RECOVERY_MIN_STALL_TICKS = 10;
   private static final int LOCAL_RECOVERY_MAX_EPISODE_TICKS = 60;
   private static final int LOCAL_RECOVERY_COOLDOWN_TICKS = 60;
   private static final int LOCAL_RECOVERY_LOCK_TICKS = 15;
   private static final double ASCEND_LATERAL_MOTION_MAX = 0.1;
   private static final double ASCEND_FLAT_DIST_MAX = 1.2;
   private static final double ASCEND_SIDE_DIST_MAX = 0.2;
   private static final int ASCEND_MISS_TICKS = 25;
   private static final double WALK_ONE_BLOCK_COST = 4.63284688441047;
   private static final double JUMP_ONE_BLOCK_COST = 3.17;
   private static final int FAILED_HEADING_TTL = 600;
   private static final int FAILED_HEADING_CLEAN_INTERVAL = 100;
   private static final int ESCAPE_HEADING_PENALTY = 40;
   private static final int TURN_LOOP_WINDOW_TICKS = 200;
   private static final int TURN_LOOP_REFRESH_THRESHOLD = 4;
   private static final int TURN_LOOP_STRAIGHT_LOCK_TICKS = 200;
   private static final int PROGRESS_SAMPLE_INTERVAL = 10;
   private static final int PROGRESS_WINDOW_SAMPLES = 60;
   private static final double MIN_NET_PROGRESS = 16.0;
   private static final int ESCAPE_LOCK_TICKS = 200;
   private static final int OBSTACLE_AVOID_MARGIN = 6;
   private static final int DETOUR_CLEAR_TICKS = 40;
   private static final int DETOUR_MIN_TRAVEL = 16;
   private static final int OVERHEAD_SCAN_HEIGHT = 4;
   private static final int STUCK_MINING_TICKS = 160;
   private static final int STUCK_CHECK_TICKS = 60;
   private static final double STUCK_MOVE_THRESHOLD_SQ = 1.0;
   private static final int STUCK_JUMP_WAIT_TICKS = 20;
   private static final int STUCK_JUMP_COOLDOWN_TICKS = 20;
   private static final int REROUTE_AFTER_JUMP_FAILURES = 8;
   private static final int MINE_AFTER_JUMP_FAILURES = 4;
   private static final int JUMP_ESCAPE_TIER_TICKS = 30;
   private static final int VOID_Y_THRESHOLD = 59;
   private static final int VOID_CHECK_DEPTH = 4;
   private static final int WATER_DROWN_DEPTH = 5;
   private static final float ROTATION_PRECISION = 0.1F;
   private static final float MOVEMENT_ALIGN_TOLERANCE = 22.5F;
   private static final double ROTATION_SPEED_SCALE = 20.0;
   private static final int CAVE_CONFIRM_SCANS = 3;
   private static final int TURN_LOCK_TICKS = 50;
   private static final int FOLLOW_TURN_LOCK_TICKS = 3;
   private static final int RESUME_DELAY_TICKS = 10;
   private static final int DOGLEG_DISTANCE = 5;
   private static final int MAX_DOGLEG_ATTEMPTS = 3;
   private static final int SHORE_SCAN_DEPTH = 32;
   private static final int MAX_SKIRT_OFFSET = 32;
   private static final boolean BREAK_BLOCKS = true;
   private static final boolean SWING_HAND = true;
   private static final boolean SPRINT = true;
   private static final double REACH = 4.5;
   private static final int DIG_BURST_BLOCKS = 100;
   private static final int DIG_REST_TICKS = 30;
   private static final int MIN_WALK_STEPS = 30;
   private static final int SIDE_STEPS = 10;
   private static final boolean PATH_WANDER = true;
   private static final int WANDER_SEGMENT_MIN = 8;
   private static final int WANDER_SEGMENT_MAX = 16;
   private static final int WANDER_CHANCE_PERCENT = 25;
   private static final int HAZARD_MARGIN_RETRY_TICKS = 20;
   private static final int HAZARD_STUCK_ESCALATE_SECONDS = 8;
   private static final int BOTTLE_FAIL_COOLDOWN_TICKS = 1200;
   private static final int MAX_REPAIR_SECONDS = 45;
   private static final boolean PATH_TRACER = true;
   private static final int PREDICTION_DISTANCE = 160;
   private static final int PATH_COLOR = -766455041;
   private static final boolean HAZARD_OUTLINES = true;
   private static final int HAZARD_SCAN_RADIUS = 32;
   private static final int HAZARD_COLOR = -587257816;
   private static final boolean HAZARD_FOLLOW_PLAYER = true;
   private static final int HAZARD_PLATE_Y = -59;
   private TunnelBaseFinder.Phase phase = TunnelBaseFinder.Phase.ALIGNING;
   private TunnelBaseFinder.Heading facingDir;
   private TunnelBaseFinder.Heading targetDir;
   private TunnelBaseFinder.Heading initialDir;
   private float targetYaw;
   private int rotateTimer;
   private int scanTimer;
   private boolean isBreaking;
   private int breakProgress;
   private class_2338 breakTarget;
   private class_2338 breakStartedTarget;
   private class_2338 localRecoveryTarget;
   private class_2338 ascendBreakTarget;
   private int ascendMissTicks;
   private TunnelRecoveryPlanner.Action localRecoveryAction;
   private int localRecoveryTicks;
   private class_2338 lastProgressPos;
   private int noProgressTicks;
   private int localRecoveryEpisodeTicks;
   private int localRecoveryCooldown;
   private class_2350 breakSide = class_2350.field_11036;
   private int sideStrafeCount;
   private class_2338 stuckReferencePos;
   private int stuckCheckTicksLeft;
   private int stuckJumpTicksLeft;
   private int stuckJumpCooldown;
   private int consecutiveIssuedJumpAttempts;
   private TunnelBaseFinder.Heading preEscapeHeading;
   private TunnelBaseFinder.JumpEscapeTier jumpEscapeTier = TunnelBaseFinder.JumpEscapeTier.NONE;
   private int jumpEscapeTicksLeft;
   private int jumpEscapeStartY;
   private int digRestTicksLeft;
   private int blocksMinedSinceRest;
   private int completedBreakCount;
   private int refreshHeadingTurns;
   private TunnelBaseFinder.RepairPhase repairPhase = TunnelBaseFinder.RepairPhase.IDLE;
   private int repairSlot = -1;
   private int previousHotbarSlot = -1;
   private float previousPitch;
   private float targetRepairPitch;
   private int repairTicks;
   private int actionTicks;
   private int bottleWarnCooldown;
   private int bottleFailCooldown;
   private int storageScanTicks;
   private boolean disconnected;
   private class_2350 pendingCaveTurn;
   private int pendingCaveScans;
   private int turnLockTicks;
   private final List predictedPath = new ArrayList();
   private final List predictedRiseAt = new ArrayList();
   private final List farPath = new ArrayList();
   private int ticksSinceReplan;
   private int ticksSinceFarReplan;
   private TunnelBaseFinder.Heading routeMainHeading;
   private TunnelBaseFinder.Heading preDetourMainHeading;
   private int detourClearTicks;
   private class_2338 detourStartPos;
   private int wanderSuppressedTicks;
   private final ArrayDeque progressSamples = new ArrayDeque();
   private int progressSampleTimer;
   private float miningPitch;
   private Float recoveryPitchTarget;
   private class_2338 breakAimTarget;
   private class_243 breakAimPoint;
   private class_243 lastMiningFocus;
   private int miningFocusGraceFrames;
   private boolean pausedByScreen;
   private int resumeDelayTicks;
   private TunnelBaseFinder.Heading dogLegResumeDir;
   private class_2338 dogLegStartPos;
   private int dogLegAttempts;
   private TunnelBaseFinder.Heading skirtSide;
   private int skirtOffset;
   private boolean enteredShopCategory;
   private final Map hazardCellsByChunk = new ConcurrentHashMap();
   private final Map voidCellsByChunk = new ConcurrentHashMap();
   private final ArrayDeque hazardChunkQueue = new ArrayDeque();
   private final Set hazardChunkQueued = ConcurrentHashMap.newKeySet();
   private class_1923 lastHazardCenter;
   private class_2338 lastVisitedPos;
   private final TunnelHazardModel hazards = new TunnelHazardModel();
   private final TunnelRoutePlanner planner = new TunnelRoutePlanner();
   private final TunnelMotion.BreakWatchdog breakWatchdog = new TunnelMotion.BreakWatchdog();
   private final TunnelVisitMap visitMap = new TunnelVisitMap();
   private final TunnelHeadingArbiter headingArbiter = new TunnelHeadingArbiter();
   private final TunnelTurnLoopWatchdog turnLoopWatchdog = new TunnelTurnLoopWatchdog(200, 4);
   private final Long2IntOpenHashMap failedHeadings = new Long2IntOpenHashMap();
   private int failedHeadingClock;
   private int failedHeadingCleanupTimer;
   private TunnelBaseFinder.Heading committedRouteHeading;
   private int routeCommitTicks;
   private int deadEndCooldown;
   private TunnelTerrainOracle oracle;
   private static final int NEAR_STEPS = 24;
   private static final int FAR_STEPS = 160;
   private static final int NEAR_QUERY_BUDGET = 2500;
   private static final int FAR_REPLAN_INTERVAL = 20;
   private static final int MIN_REPLAN_INTERVAL_TICKS = 3;
   private static final int ROUTE_COMMIT_TICKS = 4;
   private static final int DEAD_END_COOLDOWN_TICKS = 60;
   private static final int MIN_USABLE_PATH = 4;
   private static final double SPLICE_MAX_DIST_SQ = 400.0;
   private static final int ROUTE_QUERY_BUDGET = 6000;
   private int noEscapeWarnCooldown;
   private int hazardMarginCooldown;
   private int hazardStuckSeconds;
   private long lastCancelAtMs;
   private int cancelCount;
   private TunnelBaseFinder.RepairPhase afterBottlePhase = TunnelBaseFinder.RepairPhase.PREPARE;
   private int bottleRetries;
   private int jumpKeyReleaseTicks;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   private static Setting hidden(Setting setting) {
      setting.setVisibleInGui(false);
      return setting;
   }

   static TunnelBaseFinder.Heading jumpEscapeHeading(TunnelBaseFinder.Heading original, TunnelBaseFinder.JumpEscapeTier tier) {
      return switch (tier) {
         case NONE -> original;
         case RIGHT -> original.clockwise();
         case LEFT -> original.counterClockwise();
      };
   }

   static TunnelBaseFinder.JumpEscapeTier nextJumpEscapeTier(TunnelBaseFinder.JumpEscapeTier tier) {
      return tier == TunnelBaseFinder.JumpEscapeTier.RIGHT ? TunnelBaseFinder.JumpEscapeTier.LEFT : TunnelBaseFinder.JumpEscapeTier.NONE;
   }

   static boolean hasClearedJumpEscape(int playerY, int startY) {
      return playerY > startY;
   }

   static int recordIssuedJumpAttempt(int issuedAttempts, boolean jumpIssued) {
      return jumpIssued ? issuedAttempts + 1 : issuedAttempts;
   }

   static boolean hasEarnedJumpEscape(int issuedAttempts) {
      return issuedAttempts >= (4);
   }

   static int advanceJumpEscapeTicks(int ticksLeft) {
      return ticksLeft - 1;
   }

   static TunnelBaseFinder.Heading restoreJumpEscapeHeading(TunnelBaseFinder.Heading savedHeading) {
      return savedHeading;
   }

   static boolean isJumpEscapeSafe(boolean climbing, boolean touchingWater, boolean inLava, boolean hasVehicle, boolean onGround) {
      return !climbing && !touchingWater && !inLava && !hasVehicle && onGround;
   }

   public TunnelBaseFinder() {
      super(
         "TunnelBaseFinder",
         "Walks tunnels, disconnects on bases/players, repairs a pickaxe and restores a totem.",
         Category.BASE_FINDING
      );
   }

   @Override
   public void onEnable() {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         super.onEnable();
         this.webhookSentThisSession = false;
         this.disconnected = false;
         this.storageScanTicks = 0;
         this.bottleWarnCooldown = 0;
         this.bottleFailCooldown = 0;
         this.resetRepair();
         this.isBreaking = false;
         this.breakProgress = 0;
         this.sideStrafeCount = 0;
         this.stuckReferencePos = null;
         this.stuckCheckTicksLeft = 60;
         this.stuckJumpTicksLeft = 0;
         this.stuckJumpCooldown = 0;
         this.consecutiveIssuedJumpAttempts = 0;
         this.preEscapeHeading = null;
         this.jumpEscapeTier = TunnelBaseFinder.JumpEscapeTier.NONE;
         this.jumpEscapeTicksLeft = 0;
         this.jumpEscapeStartY = 0;
         this.digRestTicksLeft = 0;
         this.blocksMinedSinceRest = 0;
         this.completedBreakCount = 0;
         this.refreshHeadingTurns = 0;
         this.headingArbiter.reset();
         this.turnLoopWatchdog.reset(null, 0, 0);
         this.breakTarget = null;
         this.breakStartedTarget = null;
         this.rotateTimer = 0;
         this.pendingCaveTurn = null;
         this.pendingCaveScans = 0;
         this.turnLockTicks = 0;
         this.lastProgressPos = null;
         this.noProgressTicks = 0;
         this.localRecoveryEpisodeTicks = 0;
         this.localRecoveryCooldown = 0;
         this.localRecoveryAction = null;
         this.localRecoveryTarget = null;
         this.ascendBreakTarget = null;
         this.ascendMissTicks = 0;
         this.localRecoveryTicks = 0;
         this.predictedPath.clear();
         this.predictedRiseAt.clear();
         this.farPath.clear();
         this.ticksSinceReplan = 3;
         this.ticksSinceFarReplan = 20;
         this.routeMainHeading = null;
         this.preDetourMainHeading = null;
         this.detourStartPos = null;
         this.detourClearTicks = 0;
         this.wanderSuppressedTicks = 0;
         this.progressSamples.clear();
         this.progressSampleTimer = 0;
         this.breakAimTarget = null;
         this.breakAimPoint = null;
         this.lastMiningFocus = null;
         this.miningFocusGraceFrames = 0;
         this.pausedByScreen = false;
         this.resumeDelayTicks = 0;
         this.dogLegResumeDir = null;
         this.dogLegStartPos = null;
         this.dogLegAttempts = 0;
         this.skirtSide = null;
         this.skirtOffset = 0;
         this.enteredShopCategory = false;
         this.hazardCellsByChunk.clear();
         this.voidCellsByChunk.clear();
         this.hazardChunkQueue.clear();
         this.hazardChunkQueued.clear();
         this.lastHazardCenter = null;
         this.lastVisitedPos = null;
         this.lastCancelAtMs = 0L;
         this.cancelCount = 0;
         this.hazardMarginCooldown = 0;
         this.hazardStuckSeconds = 0;
         if (mc.field_1724 != null) {
            this.facingDir = this.yawToHeading(mc.field_1724.method_36454());
            this.initialDir = this.facingDir;
            this.targetDir = this.facingDir;
            this.targetYaw = this.headingToYaw(this.facingDir);
            this.miningPitch = mc.field_1724.method_36455();
            this.phase = TunnelBaseFinder.Phase.ALIGNING;
            this.requestHeading(
               this.facingDir,
               TunnelHeadingArbiter.HeadingOwner.ROUTE,
               0,
               "initial route heading"
            );
            this.refreshHeadingTurns = 0;
         }

         this.failedHeadings.clear();
         this.failedHeadingClock = 0;
         this.failedHeadingCleanupTimer = 0;
         this.committedRouteHeading = null;
         this.routeCommitTicks = 0;
         this.deadEndCooldown = 0;
         this.planner.resetSkirt();
         this.breakWatchdog.reset();
         this.visitMap.reset(mc.field_1724.method_24515());
         this.lastVisitedPos = null;
         this.oracle = this.useSeedPrediction.get() ? TunnelTerrainOracle.from((String)this.worldSeed.get()) : null;
         if (this.oracle != null) {
            this.info(
               "Seed prediction on (seed %d)",
               this.oracle.seed()
            );
         }

         this.info(
            "Starting tunnel walk, facing %s",
            this.initialDir != null ? this.initialDir.toString().toUpperCase(Locale.ROOT) : "?"
         );
      } else {
         this.warning(
            "Cannot start outside a world."
         );
         this.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.releaseAllKeys();
      byte[] var10001 = new byte[12];
      var10001[0] = (byte)(-123);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(120);
      var10001[3] = (byte)(-85);
      var10001[4] = (byte)(118);
      var10001[5] = (byte)(19);
      var10001[6] = (byte)(-47);
      var10001[7] = (byte)(-66);
      var10001[8] = (byte)(-12);
      var10001[9] = (byte)(-61);
      var10001[10] = (byte)(-112);
      var10001[11] = (byte)(-109);
      this.abortLocalRecovery($$v_s(var10001, -1877252675377835827L));
      var10001 = new byte[12];
      var10001[0] = (byte)(-123);
      var10001[1] = (byte)(-11);
      var10001[2] = (byte)(120);
      var10001[3] = (byte)(-85);
      var10001[4] = (byte)(118);
      var10001[5] = (byte)(19);
      var10001[6] = (byte)(-47);
      var10001[7] = (byte)(-66);
      var10001[8] = (byte)(-12);
      var10001[9] = (byte)(-61);
      var10001[10] = (byte)(-112);
      var10001[11] = (byte)(-109);
      this.finishJumpEscape($$v_s(var10001, -1877252675377835827L));
      this.restoreAfterRepair();
      this.cancelCurrentBreakNow();
      this.phase = TunnelBaseFinder.Phase.ALIGNING;
      this.dogLegResumeDir = null;
      this.dogLegStartPos = null;
      if (this.oracle != null) {
         this.oracle.close();
         this.oracle = null;
      }

      this.breakWatchdog.reset();
      this.planner.resetSkirt();
      if (mc.field_1724 != null) {
         this.visitMap.reset(mc.field_1724.method_24515());
      }
   }

   public void openFilterScreen() {
      mc.method_1507(new TunnelFilterScreen(this));
   }

   public boolean isScanning() {
      return this.phase != TunnelBaseFinder.Phase.ALIGNING;
   }

   @EventHandler
   private void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE) {
         if (mc.field_1724 != null && mc.field_1687 != null && !this.disconnected) {
            if (this.bottleFailCooldown > 0) {
               this.bottleFailCooldown--;
            }

            if (this.bottleWarnCooldown > 0) {
               this.bottleWarnCooldown--;
            }

            this.hazards.beginTick(mc.field_1687, this.oracle);
            this.visitMap.tickPrune(mc.field_1724.method_31477(), mc.field_1724.method_31479());
            this.tickFailedHeadings();
            this.scanHazardChunks();
            if (!this.checkPlayers()) {
               if (++this.storageScanTicks >= (Integer)this.scanDelay.get()) {
                  this.storageScanTicks = 0;
                  if (this.scanBaseSignals(true, true)) {
                     return;
                  }
               }

               if (this.repairPhase != TunnelBaseFinder.RepairPhase.IDLE || this.tryStartRepair()) {
                  this.tickRepair();
               } else if (mc.field_1755 != null) {
                  this.pauseForScreen();
               } else {
                  if (this.pausedByScreen) {
                     this.resumeAfterScreen();
                  }

                  if (this.resumeDelayTicks > 0) {
                     this.resumeDelayTicks--;
                     this.releaseMovementKeys();
                     mc.field_1690.field_1886.method_23481(false);
                  } else {
                     if (this.turnLockTicks > 0) {
                        this.turnLockTicks--;
                     }

                     this.headingArbiter.tick();
                     class_2338 var2 = mc.field_1724.method_24515();
                     if (this.lastProgressPos != null && this.lastProgressPos.equals(var2)) {
                        this.noProgressTicks++;
                     } else {
                        this.lastProgressPos = var2.method_10062();
                        this.noProgressTicks = 0;
                     }

                     if (this.localRecoveryCooldown > 0) {
                        this.localRecoveryCooldown--;
                     }

                     if (this.wanderSuppressedTicks > 0) {
                        this.wanderSuppressedTicks--;
                     }

                     this.releaseJumpKeyIfPending();
                     boolean var3 = this.tickJumpEscape();
                     if (!var3) {
                        if (this.tickTurnLoopWatchdog()) {
                           return;
                        }

                        this.tickProgressRecovery();
                        if (this.tickLocalRecovery()) {
                           return;
                        }

                        boolean var4 = TunnelMotion.shouldSprint(mc.field_1724, mc.field_1724.method_5624());
                        if (var4 != mc.field_1724.method_5624()) {
                           mc.field_1724.method_5728(var4);
                        }

                        switch (this.phase) {
                           case ALIGNING:
                              this.tickAligning();
                              break;
                           case ROTATING:
                              this.tickRotating();
                              break;
                           case MINING:
                              this.tickMining();
                        }
                     }

                     if (this.immediateHazardAhead()) {
                        mc.field_1690.field_1894.method_23481(false);
                        mc.field_1724.method_5728(false);
                     }

                     if (this.oracle != null) {
                        this.oracle.flush();
                     }
                  }
               }
            }
         }
      }
   }

   private boolean immediateHazardAhead() {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         TunnelBaseFinder.Heading var1 = this.facingDir != null ? this.facingDir : this.yawToHeading(mc.field_1724.method_36454());
         class_2338 var2 = mc.field_1724.method_24515().method_10069(var1.dx, 0, var1.dz);
         if (!this.isLavaBlock(var2) && !this.isLavaBlock(var2.method_10084())) {
            for (int var3 = 2; var3 <= (4); var3++) {
               if (this.isLavaBlock(var2.method_10086(var3))) {
                  return true;
               }
            }

            return this.isVoidHazard(var2);
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @EventHandler
   private void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D) {
         if (mc.field_1724 != null && mc.field_1687 != null && !this.disconnected) {
            if (mc.field_1755 == null && this.repairPhase == TunnelBaseFinder.RepairPhase.IDLE && this.recoveryPitchTarget != null) {
               this.steerPitchToward(this.recoveryPitchTarget, event.getFrameTime(), this.rotationSpeed());
            } else if (mc.field_1755 == null
               && this.repairPhase == TunnelBaseFinder.RepairPhase.IDLE
               && (this.phase == TunnelBaseFinder.Phase.ALIGNING || this.phase == TunnelBaseFinder.Phase.ROTATING)) {
               this.steerByYaw(event.getFrameTime());
               this.steerPitchToward(this.miningPitch, event.getFrameTime(), this.rotationSpeed());
            }

            if (mc.field_1755 == null
               && this.repairPhase == TunnelBaseFinder.RepairPhase.IDLE
               && this.recoveryPitchTarget == null
               && this.phase == TunnelBaseFinder.Phase.MINING) {
               this.steerMiningView(event.getFrameTime());
            }

            if (this.repairPhase == TunnelBaseFinder.RepairPhase.AIM_DOWN
               || this.repairPhase == TunnelBaseFinder.RepairPhase.MENDING
               || this.repairPhase == TunnelBaseFinder.RepairPhase.AIM_RESTORE) {
               this.steerRepairPitch(event.getFrameTime());
            }

            int var2 = mc.field_1724.method_31478();
            double var3 = 2.0;

            for (Entry var6 : this.hazardCellsByChunk.entrySet()) {
               List var7 = (List)var6.getValue();
               List var8 = this.voidCellsByChunk.getOrDefault(var6.getKey(), List.of());
               int var9 = var7.size() + var8.size();
               if (var9 != 0) {
                  double var10 = 0.0;
                  double var12 = 0.0;

                  for (class_2338 var15 : var7) {
                     var10 += var15.method_10263();
                     var12 += var15.method_10260();
                  }

                  for (class_2338 var31 : var8) {
                     var10 += var31.method_10263();
                     var12 += var31.method_10260();
                  }

                  var10 /= var9;
                  var12 /= var9;
                  RenderUtil.drawBox(new class_238(var10 - var3, var2, var12 - var3, var10 + var3, var2 + 0.06, var12 + var3), -587257816);
               }
            }

            if (!this.predictedPath.isEmpty() || !this.farPath.isEmpty()) {
               double var16 = mc.field_1724.method_23317();
               double var4 = mc.field_1724.method_23318() + 0.12;
               double var17 = mc.field_1724.method_23321();

               for (class_2338 var20 : this.predictedPath) {
                  double var23 = var20.method_10263() + 0.5;
                  double var26 = var20.method_10264() + 0.12;
                  double var29 = var20.method_10260() + 0.5;
                  RenderUtil.drawLine(var16, var4, var17, var23, var26, var29, -766455041);
                  var16 = var23;
                  var4 = var26;
                  var17 = var29;
               }

               for (class_2338 var21 : this.farPath) {
                  double var24 = var21.method_10263() + 0.5;
                  double var27 = var21.method_10264() + 0.12;
                  double var30 = var21.method_10260() + 0.5;
                  RenderUtil.drawLine(var16, var4, var17, var24, var27, var30, -766455041);
                  var16 = var24;
                  var4 = var27;
                  var17 = var30;
               }
            }
         }
      }
   }

   @EventHandler
   private void onReceivePacket(ReceivePacketEvent event) {
      if (this.isEnabled() && mc.field_1687 != null && !this.disconnected) {
         if (event.getPacket() instanceof class_2663 var2) {
            if (var2.method_11470() == (35)) {
               class_1297 var4 = var2.method_11469(mc.field_1687);
               if (var4 != null && var4.equals(mc.field_1724)) {
                  byte[] var10001 = new byte[19];
                  var10001[0] = (byte)(-111);
                  var10001[1] = (byte)(-11);
                  var10001[2] = (byte)(105);
                  var10001[3] = (byte)(-84);
                  var10001[4] = (byte)(58);
                  var10001[5] = (byte)(2);
                  var10001[6] = (byte)(-98);
                  var10001[7] = (byte)(-71);
                  var10001[8] = (byte)(-27);
                  var10001[9] = (byte)(-49);
                  var10001[10] = (byte)(-60);
                  var10001[11] = (byte)(-100);
                  var10001[12] = (byte)(55);
                  var10001[13] = (byte)(-35);
                  var10001[14] = (byte)(-83);
                  var10001[15] = (byte)(-107);
                  var10001[16] = (byte)(-56);
                  var10001[17] = (byte)(37);
                  var10001[18] = (byte)(-68);
                  this.disconnect($$v_s(var10001, -1877252675377835827L));
               }
            }
         }
      }
   }

   private boolean checkPlayers() {
      if (!(Boolean)this.disconnectPlayers.get()) {
         return false;
      }

      double var1 = (Double)this.playerRange.get() * (Double)this.playerRange.get();

      for (class_1657 var4 : mc.field_1687.method_18456()) {
         if (var4 != mc.field_1724
            && !var4.method_31481()
            && !var4.method_7325()
            && (!(Boolean)this.ignoreFriends.get() || !this.isFriend(var4))
            && mc.field_1724.method_5858(var4) <= var1) {
            this.detectAndDisconnect(
               TunnelBaseFinder.DetectionKind.PLAYER,
               var4.method_5477().getString(),
               var4.method_24515(),
               "[TBF] YOU FOUND PLAYER :" + var4.method_5477().getString()
            );
            return true;
         }
      }

      return false;
   }

   private boolean isFriend(class_1657 player) {
      VieClientMod var2 = VieClientMod.getInstance();
      return var2 != null && var2.getFriendManager() != null && var2.getFriendManager().isFriend(player.method_5477().getString());
   }

   private boolean checkStorage() {
      return this.scanBaseSignals(true, false);
   }

   private boolean checkSpawner() {
      return this.scanBaseSignals(false, true);
   }

   private boolean scanBaseSignals(boolean wantStorage, boolean wantSpawner) {
      if (wantStorage && !(Boolean)this.disconnectStorage.get()) {
         wantStorage = false;
      }

      if (wantSpawner && !(Boolean)this.disconnectSpawner.get()) {
         wantSpawner = false;
      }

      if (!wantStorage && !wantSpawner) {
         return false;
      }

      int var3 = 0;
      if (wantStorage) {
         var3 = Math.max(var3, (Integer)this.storageRange.get());
      }

      if (wantSpawner) {
         var3 = Math.max(var3, (Integer)this.spawnerRange.get());
      }

      class_2338 var4 = mc.field_1724.method_24515();
      int var5 = (Integer)this.storageRange.get();
      int var6 = (Integer)this.spawnerRange.get();
      int var7 = 0;
      int var8 = 0;
      int var9 = 0;
      int var10 = 0;
      int var11 = 0;
      int var12 = 0;
      int var13 = 0;
      int var14 = var4.method_10263() - var3 >> 4;
      int var15 = var4.method_10263() + var3 >> 4;
      int var16 = var4.method_10260() - var3 >> 4;
      int var17 = var4.method_10260() + var3 >> 4;

      for (int var18 = var14; var18 <= var15; var18++) {
         for (int var19 = var16; var19 <= var17; var19++) {
            class_2818 var20 = mc.field_1687.method_2935().method_12126(var18, var19, false);
            if (var20 != null) {
               for (Entry var22 : var20.method_12214().entrySet()) {
                  class_2338 var23 = (class_2338)var22.getKey();
                  int var24 = Math.abs(var23.method_10263() - var4.method_10263());
                  int var25 = Math.abs(var23.method_10264() - var4.method_10264());
                  int var26 = Math.abs(var23.method_10260() - var4.method_10260());
                  int var27 = Math.max(var24, Math.max(var25, var26));
                  class_2248 var28 = ((class_2586)var22.getValue()).method_11010().method_26204();
                  if (wantSpawner && var27 <= var6 && var28 == class_2246.field_10260) {
                     this.detectAndDisconnect(
                        TunnelBaseFinder.DetectionKind.SPAWNER,
                        "spawner",
                        var23,
                        "[TBF] YOU FOUND SPAWNER"
                     );
                     return true;
                  }

                  if (wantStorage && var27 <= var5) {
                     if ((Boolean)this.chests.get() && var28 instanceof class_2281) {
                        if (++var7 >= (Integer)this.chestsThreshold.get()) {
                           this.detectAndDisconnect(
                              TunnelBaseFinder.DetectionKind.BASE,
                              "chests",
                              var23,
                              "[TBF] YOU FOUND BASE (chests)"
                           );
                           return true;
                        }
                     }

                     if ((Boolean)this.barrels.get() && var28 instanceof class_3708) {
                        if (++var8 >= (Integer)this.barrelsThreshold.get()) {
                           this.detectAndDisconnect(
                              TunnelBaseFinder.DetectionKind.BASE,
                              "barrels",
                              var23,
                              "[TBF] YOU FOUND BASE (barrels)"
                           );
                           return true;
                        }
                     }

                     if ((Boolean)this.shulkers.get() && var28 instanceof class_2480) {
                        if (++var9 >= (Integer)this.shulkersThreshold.get()) {
                           this.detectAndDisconnect(
                              TunnelBaseFinder.DetectionKind.BASE,
                              "shulkers",
                              var23,
                              "[TBF] YOU FOUND BASE (shulkers)"
                           );
                           return true;
                        }
                     }

                     if ((Boolean)this.hoppers.get() && var28 instanceof class_2377) {
                        if (++var10 >= (Integer)this.hoppersThreshold.get()) {
                           this.detectAndDisconnect(
                              TunnelBaseFinder.DetectionKind.BASE,
                              "hoppers",
                              var23,
                              "[TBF] YOU FOUND BASE (hoppers)"
                           );
                           return true;
                        }
                     }

                     if ((Boolean)this.furnaces.get() && var28 instanceof class_3865) {
                        if (++var11 >= (Integer)this.furnacesThreshold.get()) {
                           this.detectAndDisconnect(
                              TunnelBaseFinder.DetectionKind.BASE,
                              "furnaces",
                              var23,
                              "[TBF] YOU FOUND BASE (furnaces)"
                           );
                           return true;
                        }
                     }

                     if ((Boolean)this.crafters.get() && var28 instanceof class_8886) {
                        if (++var13 >= (Integer)this.craftersThreshold.get()) {
                           this.detectAndDisconnect(
                              TunnelBaseFinder.DetectionKind.BASE,
                              "crafters",
                              var23,
                              "[TBF] YOU FOUND BASE (crafters)"
                           );
                           return true;
                        }
                     }

                     if ((Boolean)this.dispensers.get() && (var28 instanceof class_2315 || var28 instanceof class_2325)) {
                        if (++var12 >= (Integer)this.dispensersThreshold.get()) {
                           this.detectAndDisconnect(
                              TunnelBaseFinder.DetectionKind.BASE,
                              "dispensers",
                              var23,
                              "[TBF] YOU FOUND BASE (dispensers)"
                           );
                           return true;
                        }
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   private boolean isSelectedStorage(class_2248 block) {
      if ((Boolean)this.chests.get() && block instanceof class_2281) {
         return true;
      } else if ((Boolean)this.barrels.get() && block instanceof class_3708) {
         return true;
      } else if ((Boolean)this.shulkers.get() && block instanceof class_2480) {
         return true;
      } else if ((Boolean)this.hoppers.get() && block instanceof class_2377) {
         return true;
      } else if ((Boolean)this.furnaces.get() && block instanceof class_3865) {
         return true;
      } else {
         return this.crafters.get() && block instanceof class_8886
            ? true
            : (Boolean)this.dispensers.get() && (block instanceof class_2315 || block instanceof class_2325);
      }
   }

   private void detectAndDisconnect(TunnelBaseFinder.DetectionKind kind, String detail, class_2338 where, String disconnectReason) {
      if (!this.disconnected) {
         if ((Boolean)this.discordEnabled.get() && !this.webhookSentThisSession) {
            this.webhookSentThisSession = true;
            String var5 = (String)this.discordUrl.get();
            String var6 = (String)this.discordPing.get();
            String var7 = mc.field_1687 == null
               ? "unknown"
               : mc.field_1687.method_27983().method_29177().toString();
            String var8 = mc.method_1558() == null
               ? "singleplayer"
               : mc.method_1558().field_3761;
            String var9 = mc.method_1548().method_1676();
            String var10 = Instant.now().toString();
            ArrayList var11 = new ArrayList();
            var11.add(new DiscordWebhook.Field("Kind", kind.title, true));
            var11.add(
               new DiscordWebhook.Field(
                  "Detail",
                  detail == null ? "unknown" : detail,
                  true
               )
            );
            var11.add(new DiscordWebhook.Field("Dimension", var7, false));
            var11.add(new DiscordWebhook.Field("Server", var8, false));
            var11.add(new DiscordWebhook.Field("Account", var9, false));
            if ((Boolean)this.discordCoords.get() && mc.field_1724 != null) {
               int var12 = mc.field_1724.method_31477();
               int var13 = mc.field_1724.method_31478();
               int var14 = mc.field_1724.method_31479();
               var11.add(
                  new DiscordWebhook.Field(
                     "Your position",
                     var12 + ", " + var13 + ", " + var14,
                     true
                  )
               );
               if (where != null) {
                  int var15 = where.method_10263();
                  int var16 = where.method_10264();
                  int var17 = where.method_10260();
                  double var18 = Math.sqrt(mc.field_1724.method_5649(var15 + 0.5, var16 + 0.5, var17 + 0.5));
                  var11.add(
                     new DiscordWebhook.Field(
                        "Found at", var15 + ", " + var16 + ", " + var17, true
                     )
                  );
                  var11.add(
                     new DiscordWebhook.Field(
                        "Distance",
                        String.format(Locale.ROOT, "%.1f", var18),
                        true
                     )
                  );
               }
            }

            DiscordWebhook.Embed var20 = new DiscordWebhook.Embed(
               kind.title,
               "TunnelBaseFinder detection",
               kind.color,
               var10,
               List.copyOf(var11)
            );
            DiscordWebhook.send(var5, var6, var20);
         }

         this.disconnect(disconnectReason);
      }
   }

   private void disconnect(String reason) {
      this.disconnected = true;
      this.releaseAllKeys();
      this.cancelCurrentBreakNow();
      if (mc.method_1562() != null) {
         mc.method_1562().method_48296().method_10747(class_2561.method_43470(reason));
      }
   }

   private boolean tryStartRepair() {
      if ((Boolean)this.autoRepair.get() && mc.field_1755 == null && this.bottleFailCooldown <= 0) {
         int var1 = this.findDamagedPickaxe();
         if (var1 == -1) {
            return false;
         }

         int var2 = (Integer)this.bottleSlot.get() - 1;
         if (mc.field_1724.method_31548().method_5438(var2).method_31574(class_1802.field_8287)) {
            this.repairSlot = var1;
            this.repairPhase = TunnelBaseFinder.RepairPhase.PREPARE;
            this.actionTicks = 0;
            this.repairTicks = 0;
            return true;
         }

         if (!(Boolean)this.buyBottles.get()) {
            if (this.bottleWarnCooldown == 0) {
               this.warning(
                  "XP bottle hotbar slot %d does not contain experience bottles.",
                  this.bottleSlot.get()
               );
               this.bottleWarnCooldown = 200;
            }

            return false;
         } else {
            this.repairSlot = var1;
            this.afterBottlePhase = TunnelBaseFinder.RepairPhase.PREPARE;
            this.repairPhase = TunnelBaseFinder.RepairPhase.BUY_BOTTLE_OPEN_SHOP;
            this.actionTicks = 0;
            return true;
         }
      } else {
         return false;
      }
   }

   private int findDamagedPickaxe() {
      int var1 = -1;
      int var2 = 101;

      for (int var3 = 0; var3 <= (8); var3++) {
         class_1799 var4 = mc.field_1724.method_31548().method_5438(var3);
         if (var4.method_31573(class_3489.field_42614) && var4.method_7963()) {
            int var5 = this.durabilityPercent(var4);
            if (var5 <= (Integer)this.repairAt.get() && var5 < var2) {
               var1 = var3;
               var2 = var5;
            }
         }
      }

      return var1;
   }

   private void tickRepair() {
      this.releaseMovementKeys();
      mc.field_1690.field_1886.method_23481(false);
      this.actionTicks++;
      switch (this.repairPhase) {
         case IDLE:
         default:
            break;
         case BUY_BOTTLE_OPEN_SHOP:
            if (this.actionTicks < 6) {
               return;
            }

            if (mc.field_1724.field_3944 != null) {
               mc.field_1724.field_3944.method_45730(this.stripSlash((String)this.shopCommand.get()));
            }

            this.bottleRetries = 0;
            this.repairPhase = TunnelBaseFinder.RepairPhase.BUY_BOTTLE_CLICK_MENU;
            this.actionTicks = 0;
            break;
         case BUY_BOTTLE_CLICK_MENU:
            this.tickClickShopSlot((Integer)this.bottleMenuSlot.get(), TunnelBaseFinder.RepairPhase.BUY_BOTTLE_CLICK_QTY_A);
            break;
         case BUY_BOTTLE_CLICK_QTY_A:
            this.tickClickShopSlot((Integer)this.bottleQtySlotA.get(), TunnelBaseFinder.RepairPhase.BUY_BOTTLE_CLICK_QTY_B);
            break;
         case BUY_BOTTLE_CLICK_QTY_B:
            this.tickClickShopSlot((Integer)this.bottleQtySlotB.get(), TunnelBaseFinder.RepairPhase.BUY_BOTTLE_VERIFY);
            break;
         case BUY_BOTTLE_VERIFY:
            this.tickVerifyBottlePurchase();
            break;
         case BUY_BOTTLE_RESTOCK:
            this.tickBuyBottleRestock();
            break;
         case PREPARE:
            this.previousHotbarSlot = mc.field_1724.method_31548().method_67532();
            this.previousPitch = mc.field_1724.method_36455();
            this.miningPitch = this.previousPitch;
            swapMainToOffhand(this.repairSlot);
            mc.field_1724.method_31548().method_61496((Integer)this.bottleSlot.get() - 1);
            this.targetRepairPitch = 90.0F;
            this.repairPhase = TunnelBaseFinder.RepairPhase.AIM_DOWN;
            this.actionTicks = 0;
            break;
         case AIM_DOWN:
            if (!this.isPitchAligned(this.targetRepairPitch)) {
               return;
            }

            mc.field_1724.method_36457(this.targetRepairPitch);
            this.repairPhase = TunnelBaseFinder.RepairPhase.MENDING;
            this.actionTicks = 0;
            break;
         case MENDING:
            this.tickMending();
            break;
         case AIM_RESTORE:
            if (!this.isPitchAligned(this.targetRepairPitch)) {
               return;
            }

            mc.field_1724.method_36457(this.targetRepairPitch);
            this.repairPhase = TunnelBaseFinder.RepairPhase.OPEN_SHOP;
            this.actionTicks = 0;
            break;
         case OPEN_SHOP:
            if (this.actionTicks < 6) {
               return;
            }

            if ((Boolean)this.buyTotem.get() && mc.field_1724.field_3944 != null) {
               mc.field_1724.field_3944.method_45730(this.stripSlash((String)this.shopCommand.get()));
               this.enteredShopCategory = false;
               this.repairPhase = TunnelBaseFinder.RepairPhase.FIND_TOTEM;
            } else {
               this.repairPhase = TunnelBaseFinder.RepairPhase.RESTORE_TOTEM;
            }

            this.actionTicks = 0;
            break;
         case FIND_TOTEM:
            this.tickFindTotem();
            break;
         case CONFIRM_TOTEM:
            this.tickConfirmTotem();
            break;
         case RESTORE_TOTEM:
            switch (restoreTotemStep(mc.field_1755 != null, this.actionTicks)) {
               case CLOSE_AND_DEFER:
                  mc.field_1755.method_25419();
                  this.actionTicks = 0;
                  return;
               case WAIT:
                  return;
               case EQUIP:
               default:
                  this.equipTotem();
                  this.repairPhase = TunnelBaseFinder.RepairPhase.IDLE;
                  this.actionTicks = 0;
                  this.resumeDelayTicks = 10;
                  this.info(
                     "Pickaxe repaired; totem restored."
                  );
            }
      }
   }

   private void tickMending() {
      this.repairTicks++;
      class_1799 var1 = mc.field_1724.method_6079();
      if (!var1.method_31573(class_3489.field_42614)) {
         this.warning(
            "Repair stopped because the pickaxe left the offhand."
         );
         this.abortRepair();
      } else if (this.durabilityPercent(var1) >= (Integer)this.repairTo.get()) {
         this.finishMending();
      } else if (this.repairTicks >= 900) {
         this.warning(
            "Repair timed out; check that the pickaxe has Mending."
         );
         this.abortRepair();
      } else {
         int var2 = (Integer)this.bottleSlot.get() - 1;
         if (!mc.field_1724.method_31548().method_5438(var2).method_31574(class_1802.field_8287)) {
            this.warning(
               "XP bottles ran out before repair completed."
            );
            this.abortRepair();
         } else {
            mc.field_1690.field_1904.method_23481(this.repairTicks % (Integer)this.bottleDelay.get() == 1);
         }
      }
   }

   private void finishMending() {
      mc.field_1690.field_1904.method_23481(false);
      swapOffhandTo(this.repairSlot);
      this.restoreHotbar();
      this.miningPitch = this.previousPitch;
      this.targetRepairPitch = this.miningPitch;
      this.repairPhase = TunnelBaseFinder.RepairPhase.AIM_RESTORE;
      this.actionTicks = 0;
   }

   private void tickFindTotem() {
      if (this.actionTicks > (Integer)this.shopTimeout.get() * 20) {
         this.warning(
            "Totem was not found in the shop before timeout."
         );
         this.repairPhase = TunnelBaseFinder.RepairPhase.RESTORE_TOTEM;
         this.actionTicks = 0;
      } else if (this.actionTicks >= 3 && mc.field_1755 != null && mc.field_1724.field_7512 != mc.field_1724.field_7498) {
         class_1703 var1 = mc.field_1724.field_7512;
         String var2 = this.normalizedName((String)this.totemName.get());
         int var3 = Math.max(0, var1.field_7761.size() - 36);

         for (int var4 = 0; var4 < var3; var4++) {
            class_1735 var5 = (class_1735)var1.field_7761.get(var4);
            if (!var5.method_7677().method_7960()
               && (var5.method_7677().method_31574(class_1802.field_8288) || this.normalizedName(var5.method_7677().method_7964().getString()).contains(var2))) {
               mc.field_1761.method_2906(var1.field_7763, var5.field_7874, 0, class_1713.field_7790, mc.field_1724);
               this.repairPhase = TunnelBaseFinder.RepairPhase.CONFIRM_TOTEM;
               this.actionTicks = 0;
               return;
            }
         }

         if (!this.enteredShopCategory && this.tryEnterCategory(var1, var3)) {
            this.enteredShopCategory = true;
            this.actionTicks = 0;
         }
      }
   }

   private void tickConfirmTotem() {
      if (this.actionTicks > (Integer)this.shopTimeout.get() * (20)) {
         byte[] var10001 = new byte[45];
         var10001[0] = (byte)(-80);
         var10001[1] = (byte)(-37);
         var10001[2] = (byte)(95);
         var10001[3] = (byte)(-2);
         var10001[4] = (byte)(84);
         var10001[5] = (byte)(62);
         var10001[6] = (byte)(-80);
         var10001[7] = (byte)(-125);
         var10001[8] = (byte)(-96);
         var10001[9] = (byte)(-64);
         var10001[10] = (byte)(-111);
         var10001[11] = (byte)(-126);
         var10001[12] = (byte)(54);
         var10001[13] = (byte)(-63);
         var10001[14] = (byte)(-73);
         var10001[15] = (byte)(-107);
         var10001[16] = (byte)(-49);
         var10001[17] = (byte)(43);
         var10001[18] = (byte)(-65);
         var10001[19] = (byte)(-114);
         var10001[20] = (byte)(-60);
         var10001[21] = (byte)(-23);
         var10001[22] = (byte)(-43);
         var10001[23] = (byte)(-67);
         var10001[24] = (byte)(54);
         var10001[25] = (byte)(125);
         var10001[26] = (byte)(-31);
         var10001[27] = (byte)(40);
         var10001[28] = (byte)(-10);
         var10001[29] = (byte)(-34);
         var10001[30] = (byte)(-21);
         var10001[31] = (byte)(32);
         var10001[32] = (byte)(110);
         var10001[33] = (byte)(85);
         var10001[34] = (byte)(14);
         var10001[35] = (byte)(27);
         var10001[36] = (byte)(-38);
         var10001[37] = (byte)(-94);
         var10001[38] = (byte)(56);
         var10001[39] = (byte)(64);
         var10001[40] = (byte)(69);
         var10001[41] = (byte)(-83);
         var10001[42] = (byte)(49);
         var10001[43] = (byte)(98);
         var10001[44] = (byte)(12);
         this.warning($$v_s(var10001, -1877252675377835827L));
         this.proceedAfterTotemPurchase();
      } else if (this.actionTicks >= (3) && mc.field_1755 != null && mc.field_1724.field_7512 != mc.field_1724.field_7498) {
         class_1703 var1 = mc.field_1724.field_7512;
         int var2 = Math.max(0, var1.field_7761.size() - (36));
         if (this.clickConfirmButton(var1, var2)) {
            this.proceedAfterTotemPurchase();
         }
      }
   }

   private void proceedAfterTotemPurchase() {
      this.actionTicks = 0;
      if ((Boolean)this.buyBottles.get()) {
         this.afterBottlePhase = TunnelBaseFinder.RepairPhase.RESTORE_TOTEM;
         this.repairPhase = TunnelBaseFinder.RepairPhase.BUY_BOTTLE_OPEN_SHOP;
      } else {
         this.repairPhase = TunnelBaseFinder.RepairPhase.RESTORE_TOTEM;
      }
   }

   private void tickClickShopSlot(int guiSlotNumber, TunnelBaseFinder.RepairPhase nextPhase) {
      if (this.actionTicks > (Integer)this.shopTimeout.get() * 20) {
         this.warning(
            "Shop screen did not respond in time; repair skipped."
         );
         this.abortBottleSequence();
      } else if (this.actionTicks >= (Integer)this.bottleClickDelay.get() && mc.field_1755 != null && mc.field_1724.field_7512 != mc.field_1724.field_7498) {
         class_1703 var3 = mc.field_1724.field_7512;
         int var4 = Math.max(0, var3.field_7761.size() - 36);
         int var5 = guiSlotNumber - 1;
         if (var5 >= 0 && var5 < var4) {
            class_1735 var6 = (class_1735)var3.field_7761.get(var5);
            mc.field_1761.method_2906(var3.field_7763, var6.field_7874, 0, class_1713.field_7790, mc.field_1724);
            this.repairPhase = nextPhase;
            this.actionTicks = 0;
         } else {
            this.warning(
               "Configured bottle shop slot %d is out of range.",
               guiSlotNumber
            );
            this.abortBottleSequence();
         }
      }
   }

   private void tickVerifyBottlePurchase() {
      if (this.actionTicks >= (Integer)this.bottleClickDelay.get()) {
         int var1 = (Integer)this.bottleSlot.get() - 1;
         class_1799 var2 = mc.field_1724.method_31548().method_5438(var1);
         if (var2.method_31574(class_1802.field_8287)) {
            this.repairPhase = TunnelBaseFinder.RepairPhase.BUY_BOTTLE_RESTOCK;
            this.actionTicks = 0;
         } else {
            this.bottleRetries++;
            if (this.bottleRetries > (Integer)this.bottleMaxRetries.get()) {
               this.warning(
                  "Could not buy experience bottles after %d attempts; repair skipped.",
                  this.bottleMaxRetries.get()
               );
               this.abortBottleSequence();
            } else {
               if (!var2.method_7960()) {
                  class_1703 var3 = mc.field_1724.field_7512;
                  int var4 = hotbarScreenSlotIndex(var3 == mc.field_1724.field_7498, var3.field_7761.size(), var1);
                  if (var4 < 0 || var4 >= var3.field_7761.size()) {
                     this.warning(
                        "Bottle hotbar slot %d is not present in the active screen.",
                        this.bottleSlot.get()
                     );
                     this.abortBottleSequence();
                     return;
                  }

                  int var5 = ((class_1735)var3.field_7761.get(var4)).field_7874;
                  mc.field_1761.method_2906(var3.field_7763, var5, 1, class_1713.field_7795, mc.field_1724);
               }

               this.repairPhase = TunnelBaseFinder.RepairPhase.BUY_BOTTLE_CLICK_MENU;
               this.actionTicks = 0;
            }
         }
      }
   }

   private void tickBuyBottleRestock() {
      if (this.actionTicks >= (6)) {
         closeBeforeDeferredRestore(this::closeCurrentScreen, this::finishBottleRestock);
      }
   }

   private void closeCurrentScreen() {
      if (mc.field_1755 != null) {
         mc.field_1755.method_25419();
      }
   }

   private void finishBottleRestock() {
      InvUtils.FindItemResult var1 = InvUtils.find(class_1802.field_8287);
      if (!var1.found()) {
         this.warning(
            "Bought from shop but no experience bottles were found in the inventory."
         );
         this.abortBottleSequence();
      } else {
         int var2 = (Integer)this.bottleSlot.get() - 1;
         if (var1.slot() != var2) {
            swapMainTo(var1.slot(), var2);
         }

         this.repairPhase = this.afterBottlePhase;
         this.actionTicks = 0;
         this.repairTicks = 0;
      }
   }

   static int hotbarScreenSlotIndex(boolean playerHandler, int handlerSlotCount, int configuredBottleSlot) {
      return playerHandler ? (36) + configuredBottleSlot : handlerSlotCount - (9) + configuredBottleSlot;
   }

   static TunnelBaseFinder.RestoreTotemStep restoreTotemStep(boolean screenOpen, int actionTicks) {
      if (screenOpen) {
         return TunnelBaseFinder.RestoreTotemStep.CLOSE_AND_DEFER;
      } else {
         return actionTicksx < (8) ? TunnelBaseFinder.RestoreTotemStep.WAIT : TunnelBaseFinder.RestoreTotemStep.EQUIP;
      }
   }

   static TunnelBaseFinder.RepairPhase abortBottleContinuation(TunnelBaseFinder.RepairPhase afterBottlePhase) {
      return afterBottlePhasex == TunnelBaseFinder.RepairPhase.RESTORE_TOTEM ? TunnelBaseFinder.RepairPhase.RESTORE_TOTEM : TunnelBaseFinder.RepairPhase.IDLE;
   }

   static void closeBeforeDeferredRestore(Runnable closeScreen, Runnable deferredRestore) {
      closeScreen.run();
      deferredRestore.run();
   }

   private void abortBottleSequence() {
      this.bottleFailCooldown = 1200;
      if (mc.field_1755 != null) {
         mc.field_1755.method_25419();
      }

      this.repairPhase = abortBottleContinuation(this.afterBottlePhase);
      this.actionTicks = 0;
   }

   private boolean tryEnterCategory(class_1703 handler, int containerSlots) {
      String var3 = this.normalizedName((String)this.categoryName.get());
      if (var3.isEmpty()) {
         return false;
      }

      for (int var4 = 0; var4 < containerSlots; var4++) {
         class_1735 var5 = (class_1735)handler.field_7761.get(var4);
         if (!var5.method_7677().method_7960() && this.normalizedName(var5.method_7677().method_7964().getString()).contains(var3)) {
            mc.field_1761.method_2906(handler.field_7763, var5.field_7874, 0, class_1713.field_7790, mc.field_1724);
            return true;
         }
      }

      return false;
   }

   private boolean clickConfirmButton(class_1703 handler, int containerSlots) {
      int var3 = 0;

      class_1735 var4;
      while (true) {
         if (var3 >= containerSlots) {
            return false;
         }

         var4 = (class_1735)handler.field_7761.get(var3);
         String var5 = this.normalizedName(var4.method_7677().method_7964().getString());
         byte[] var10001 = new byte[8];
         var10001[0] = (byte)(-112);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(127);
         var10001[3] = (byte)(-2);
         var10001[4] = (byte)(116);
         var10001[5] = (byte)(30);
         var10001[6] = (byte)(-112);
         var10001[7] = (byte)(-93);
         if (var5.contains($$v_s(var10001, -1877252675377835827L))) {
            break;
         }

         var10001 = new byte[7];
         var10001[0] = (byte)(-117);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(114);
         var10001[3] = (byte)(-72);
         var10001[4] = (byte)(115);
         var10001[5] = (byte)(4);
         var10001[6] = (byte)(-100);
         if (var5.contains($$v_s(var10001, -1877252675377835827L))) {
            break;
         }

         var3++;
      }

      mc.field_1761.method_2906(handler.field_7763, var4.field_7874, 0, class_1713.field_7790, mc.field_1724);
      return true;
   }

   private void equipTotem() {
      if (!mc.field_1724.method_6079().method_31574(class_1802.field_8288)) {
         InvUtils.FindItemResult var1 = InvUtils.find(class_1802.field_8288);
         if (var1.found()) {
            swapMainToOffhand(var1.slot());
         } else {
            byte[] var10001 = new byte[31];
            var10001[0] = (byte)(-90);
            var10001[1] = (byte)(-11);
            var10001[2] = (byte)(60);
            var10001[3] = (byte)(-86);
            var10001[4] = (byte)(117);
            var10001[5] = (byte)(2);
            var10001[6] = (byte)(-108);
            var10001[7] = (byte)(-96);
            var10001[8] = (byte)(-96);
            var10001[9] = (byte)(-53);
            var10001[10] = (byte)(-105);
            var10001[11] = (byte)(-42);
            var10001[12] = (byte)(35);
            var10001[13] = (byte)(-40);
            var10001[14] = (byte)(-72);
            var10001[15] = (byte)(-36);
            var10001[16] = (byte)(-44);
            var10001[17] = (byte)(43);
            var10001[18] = (byte)(-82);
            var10001[19] = (byte)(-62);
            var10001[20] = (byte)(-49);
            var10001[21] = (byte)(-90);
            var10001[22] = (byte)(-43);
            var10001[23] = (byte)(-14);
            var10001[24] = (byte)(112);
            var10001[25] = (byte)(119);
            var10001[26] = (byte)(-27);
            var10001[27] = (byte)(51);
            var10001[28] = (byte)(-5);
            var10001[29] = (byte)(-114);
            var10001[30] = (byte)(-89);
            this.warning($$v_s(var10001, -1877252675377835827L));
         }
      }
   }

   private void abortRepair() {
      mc.field_1690.field_1904.method_23481(false);
      if (this.repairSlot >= 0 && mc.field_1724.method_6079().method_31573(class_3489.field_42614)) {
         swapOffhandTo(this.repairSlot);
      }

      this.restoreViewAndHotbar();
      this.resetRepair();
   }

   private void restoreAfterRepair() {
      if (mc.field_1724 != null) {
         mc.field_1690.field_1904.method_23481(false);
         if (this.repairPhase != TunnelBaseFinder.RepairPhase.IDLE && this.repairSlot >= 0 && mc.field_1724.method_6079().method_31573(class_3489.field_42614)) {
            swapOffhandTo(this.repairSlot);
         }

         this.restoreViewAndHotbar();
         this.resetRepair();
      }
   }

   private void restoreViewAndHotbar() {
      this.restoreHotbar();
      mc.field_1724.method_36457(this.previousPitch);
   }

   private void restoreHotbar() {
      if (this.previousHotbarSlot >= 0) {
         mc.field_1724.method_31548().method_61496(this.previousHotbarSlot);
      }
   }

   private void resetRepair() {
      this.repairPhase = TunnelBaseFinder.RepairPhase.IDLE;
      this.repairSlot = -1;
      this.previousHotbarSlot = -1;
      this.repairTicks = 0;
      this.actionTicks = 0;
      this.bottleRetries = 0;
   }

   private int durabilityPercent(class_1799 stack) {
      return stack.method_7963() && stack.method_7936() > 0
         ? Math.round((stack.method_7936() - stack.method_7919()) * 100.0F / stack.method_7936())
         : 100;
   }

   private String stripSlash(String command) {
      String var3;
      if (command == null) {
         byte[] var10000 = new byte[4];
         var10000[0] = (byte)(-101);
         var10000[1] = (byte)(-14);
         var10000[2] = (byte)(115);
         var10000[3] = (byte)(-82);
         var3 = $$v_s(var10000, -1877252675377835827L);
      } else {
         var3 = command.trim();
      }

      String var2 = var3;

      while (var2.startsWith("/")) {
         var2 = var2.substring(1);
      }

      if (var2.isEmpty()) {
         byte[] var4 = new byte[4];
         var4[0] = (byte)(-101);
         var4[1] = (byte)(-14);
         var4[2] = (byte)(115);
         var4[3] = (byte)(-82);
         var3 = $$v_s(var4, -1877252675377835827L);
      } else {
         var3 = var2;
      }

      return var3;
   }

   private String normalizedName(String value) {
      if (value == null) {
         return "";
      }

      String var10000 = Normalizer.normalize(value, Form.NFD);
      byte[] var10001 = new byte[6];
      var10001[0] = (byte)(-76);
      var10001[1] = (byte)(-22);
      var10001[2] = (byte)(103);
      var10001[3] = (byte)(-109);
      var10001[4] = (byte)(103);
      var10001[5] = (byte)(93);
      return var10000.replaceAll($$v_s(var10001, -1877252675377835827L), "").toLowerCase(Locale.ROOT).trim();
   }

   private void tickAligning() {
      if (this.isAligned()) {
         byte[] var10001 = new byte[29];
         var10001[0] = (byte)(-87);
         var10001[1] = (byte)(-10);
         var10001[2] = (byte)(117);
         var10001[3] = (byte)(-71);
         var10001[4] = (byte)(116);
         var10001[5] = (byte)(19);
         var10001[6] = (byte)(-107);
         var10001[7] = (byte)(-19);
         var10001[8] = (byte)(-26);
         var10001[9] = (byte)(-61);
         var10001[10] = (byte)(-121);
         var10001[11] = (byte)(-97);
         var10001[12] = (byte)(44);
         var10001[13] = (byte)(-55);
         var10001[14] = (byte)(-7);
         var10001[15] = (byte)(-112);
         var10001[16] = (byte)(-53);
         var10001[17] = (byte)(102);
         var10001[18] = (byte)(-20);
         var10001[19] = (byte)(-49);
         var10001[20] = (byte)(-50);
         var10001[21] = (byte)(-16);
         var10001[22] = (byte)(-64);
         var10001[23] = (byte)(-13);
         var10001[24] = (byte)(51);
         var10001[25] = (byte)(123);
         var10001[26] = (byte)(-6);
         var10001[27] = (byte)(33);
         var10001[28] = (byte)(-68);
         this.info($$v_s(var10001, -1877252675377835827L), this.facingDir.toString().toUpperCase(Locale.ROOT));
         this.phase = TunnelBaseFinder.Phase.MINING;
         this.breakTarget = mc.field_1724.method_24515();
      }

      this.releaseMovementKeys();
   }

   private void tickRotating() {
      this.trackVisited();
      this.facingDir = this.yawToHeading(mc.field_1724.method_36454());
      if (!this.refreshRouteAtSafetyMargin()) {
         if (this.scanTimer > 0) {
            this.scanTimer--;
         }

         this.tickBreakingBlocks();
         if (this.rotateTimer > 0) {
            this.rotateTimer--;
            this.releaseStrafeKeys();
            this.releaseMovementKeys();
         } else {
            this.releaseMovementKeys();
            if (this.isAligned()) {
               if (this.isRouteSegmentAligned()) {
                  this.pressRouteForwardOnly();
               }

               this.phase = TunnelBaseFinder.Phase.MINING;
            }
         }
      }
   }

   private boolean isRouteSegmentAligned() {
      if (!this.predictedPath.isEmpty() && mc.field_1724 != null) {
         class_2338 var1 = mc.field_1724.method_24515();
         class_2338 var2 = (class_2338)this.predictedPath.get(0);
         TunnelBaseFinder.Heading var3 = this.headingFromDelta(var2.method_10263() - var1.method_10263(), var2.method_10260() - var1.method_10260());
         if (var3 == null && this.predictedPath.size() > 1) {
            class_2338 var4 = (class_2338)this.predictedPath.get(1);
            var3 = this.headingFromDelta(var4.method_10263() - var1.method_10263(), var4.method_10260() - var1.method_10260());
         }

         return var3 == null || var3 == this.targetDir;
      } else {
         return false;
      }
   }

   private void pressRouteForwardOnly() {
      this.releaseStrafeKeys();
      this.pressForward();
   }

   private void tickMining() {
      this.facingDir = this.yawToHeading(mc.field_1724.method_36454());
      this.trackVisited();
      this.ensurePredictedPath();
      this.restoreMainHeadingAfterDetour();
      if (this.scanTimer > 0) {
         this.scanTimer--;
      }

      if (!this.refreshRouteAtSafetyMargin()) {
         if (this.dogLegResumeDir != null && mc.field_1724.method_24515().method_10262(this.dogLegStartPos) >= 25.0) {
            TunnelBaseFinder.Heading var5 = this.dogLegResumeDir;
            if (this.requestHeading(
               var5,
               TunnelHeadingArbiter.HeadingOwner.DOGLEG,
               50,
               "resuming after dogleg"
            )) {
               this.dogLegResumeDir = null;
               this.dogLegStartPos = null;
               this.dogLegAttempts = 0;
               this.preDetourMainHeading = null;
               this.detourStartPos = null;
               this.detourClearTicks = 0;
               this.predictedPath.clear();
               this.ticksSinceReplan = 3;
               this.rebuildPredictedPath(true);
               this.turnToTracer(
                  "resuming after dogleg",
                  true
               );
            }
         } else {
            if (this.turnLockTicks == 0) {
               if (this.tickPlanOrHazardTurn()) {
                  return;
               }

               if (this.disconnected) {
                  return;
               }
            }

            boolean var1 = this.pressRouteMovement();
            if (!this.tickPhysicalStuckCheck()) {
               if (var1) {
                  if (this.isBreaking) {
                     this.breakProgress++;
                     if (this.breakWatchdog.tick(this.breakTarget, mc.field_1687, mc.field_1724)) {
                        this.breakWatchdog.reset();
                        this.cancelCurrentBreak();
                        TunnelBaseFinder.Heading var6 = this.targetDir != null
                           ? this.targetDir
                           : (this.facingDir != null ? this.facingDir : this.yawToHeading(mc.field_1724.method_36454()));
                        this.markHeadingFailed(mc.field_1724.method_24515(), var6);
                        if (this.replanTracerEscape(
                           mc.field_1724.method_24515(),
                           var6,
                           "stuck mining tracer reroute",
                           TunnelHeadingArbiter.HeadingOwner.ESCAPE
                        )) {
                           this.warning(
                              "Stuck mining the same block for %ds; rerouting on tracer.",
                              8
                           );
                        }

                        return;
                     }

                     byte var2 = 30;
                     byte var3 = 10;
                     if (this.breakProgress >= var2) {
                        int var4 = this.breakProgress - var2;
                        if (var4 >= this.sideStrafeCount * var3) {
                           this.sideStrafeCount++;
                        }
                     }
                  }

                  this.tickBreakingBlocks();
               }
            }
         }
      }
   }

   private void tickBreakingBlocks() {
      this.recordCompletedBreak();
      class_2338 var1 = this.getBlockInFront();
      if (var1 == null) {
         mc.field_1690.field_1886.method_23481(false);
         this.isBreaking = false;
         this.breakProgress = 0;
         this.breakTarget = null;
         this.breakAimTarget = null;
         this.breakAimPoint = null;
         this.breakStartedTarget = null;
         this.sideStrafeCount = 0;
      } else {
         if (this.breakTarget != null && !var1.equals(this.breakTarget)) {
            this.blocksMinedSinceRest++;
            if (this.blocksMinedSinceRest >= (100)) {
               this.blocksMinedSinceRest = 0;
               this.digRestTicksLeft = 30;
            }
         }

         if (this.digRestTicksLeft > 0) {
            this.digRestTicksLeft--;
            this.releaseAttackForNaturalPause();
         } else {
            this.startBreaking(var1);
            if (this.scanTimer == 0) {
               this.scanTimer = 10;
            }
         }
      }
   }

   private void recordCompletedBreak() {
      if (this.isBreaking) {
         this.recordCompletedBreak(this.breakTarget);
      }
   }

   private boolean recordCompletedBreak(class_2338 target) {
      if (mc.field_1687 != null
         && target != null
         && hasCompletedTrackedBreak(this.breakStartedTarget, target, mc.field_1687.method_8320(target).method_26215())) {
         this.completedBreakCount++;
         if (target.equals(this.breakTarget)) {
            this.isBreaking = false;
            this.breakAimTarget = null;
            this.breakAimPoint = null;
         }

         this.breakStartedTarget = null;
         return true;
      } else {
         return false;
      }
   }

   static boolean hasCompletedTrackedBreak(class_2338 startedTarget, class_2338 observedTarget, boolean targetIsAir) {
      return startedTarget != null && startedTarget.equals(observedTarget) && targetIsAir;
   }

   private void markBreakStarted(class_2338 target) {
      if (target != null) {
         this.breakStartedTarget = target.method_10062();
      }
   }

   private void releaseAttackForNaturalPause() {
      mc.field_1690.field_1886.method_23481(false);
      this.isBreaking = false;
      this.breakProgress = 0;
      this.sideStrafeCount = 0;
      this.breakWatchdog.reset();
   }

   private boolean requestHeading(TunnelBaseFinder.Heading heading, TunnelHeadingArbiter.HeadingOwner owner, int lockTicks, String why) {
      if (!this.headingArbiter.request(heading, owner, lockTicks)) {
         if ((Boolean)this.debugMining.get()) {
            this.info(
               "Heading rejected: requester=%s owner=%s lock=%d reason=%s",
               owner,
               this.headingArbiter.owner(),
               this.headingArbiter.remainingLockTicks(),
               why
            );
         }

         return false;
      } else {
         this.routeMainHeading = heading;
         this.refreshHeadingTurns++;
         this.turnTo(heading, why, true);
         return true;
      }
   }

   private boolean requestRouteExecutionHeading(TunnelBaseFinder.Heading heading, String why) {
      if (!this.headingArbiter.request(heading, TunnelHeadingArbiter.HeadingOwner.ROUTE, 0)) {
         if ((Boolean)this.debugMining.get()) {
            this.info(
               "Heading rejected: requester=%s owner=%s lock=%d reason=%s",
               TunnelHeadingArbiter.HeadingOwner.ROUTE,
               this.headingArbiter.owner(),
               this.headingArbiter.remainingLockTicks(),
               why
            );
         }

         return false;
      } else {
         this.turnTo(heading, why, false);
         return true;
      }
   }

   private boolean tickTurnLoopWatchdog() {
      TunnelTurnLoopWatchdog.Outcome var1 = this.turnLoopWatchdog.observe(mc.field_1724.method_24515(), this.completedBreakCount, this.refreshHeadingTurns);
      if (var1 == TunnelTurnLoopWatchdog.Outcome.NONE) {
         return false;
      }

      class_2338 var2 = mc.field_1724.method_24515();
      if (var1 == TunnelTurnLoopWatchdog.Outcome.LOCK_STRAIGHT) {
         TunnelBaseFinder.Heading var3 = this.routeMainHeading != null
            ? this.routeMainHeading
            : (this.targetDir != null ? this.targetDir : this.yawToHeading(mc.field_1724.method_36454()));
         if (this.requestHeading(
            var3,
            TunnelHeadingArbiter.HeadingOwner.RECOVERY,
            200,
            "turn-loop watchdog straight lock"
         )) {
            this.warning(
               "Turn loop stalled for %ds; locking straight before escape.",
               10
            );
            return true;
         } else {
            return false;
         }
      } else {
         this.warning(
            "Turn loop remained stalled after straight lock; escaping."
         );
         this.enterEscapeMode(var2);
         return true;
      }
   }

   private void turnTo(TunnelBaseFinder.Heading dir, String reason, boolean refreshRoute) {
      if (dir != null) {
         if (dir == this.targetDir) {
            if (!this.isAligned() && this.phase == TunnelBaseFinder.Phase.MINING) {
               this.rotateTimer = 0;
               this.phase = TunnelBaseFinder.Phase.ROTATING;
            }
         } else {
            boolean var4 = !refreshRoute && this.isBreaking && this.breakTargetAheadOf(dir);
            if (!var4) {
               this.cancelCurrentBreak();
            }

            if (refreshRoute) {
               this.committedRouteHeading = null;
               this.routeCommitTicks = 0;
            }

            this.releaseStrafeKeys();
            this.targetDir = dir;
            this.targetYaw = this.headingToYaw(this.targetDir);
            this.rotateTimer = 0;
            this.phase = TunnelBaseFinder.Phase.ROTATING;
            this.turnLockTicks = refreshRoute ? 50 : 3;
            this.pendingCaveTurn = null;
            this.pendingCaveScans = 0;
            this.lastMiningFocus = null;
            this.miningFocusGraceFrames = 0;
            if (refreshRoute) {
               this.rebuildPredictedPath();
            }
         }
      }
   }

   private boolean beginJumpEscape(TunnelBaseFinder.Heading original) {
      if (mc.field_1724 != null
         && original != null
         && isJumpEscapeSafe(
            mc.field_1724.method_6101(), mc.field_1724.method_5799(), mc.field_1724.method_5771(), mc.field_1724.method_5765(), mc.field_1724.method_24828()
         )) {
         this.preEscapeHeading = original;
         this.jumpEscapeStartY = mc.field_1724.method_31478();
         this.jumpEscapeTier = TunnelBaseFinder.JumpEscapeTier.RIGHT;
         this.jumpEscapeTicksLeft = 30;
         TunnelBaseFinder.Heading var10001 = jumpEscapeHeading(this.preEscapeHeading, this.jumpEscapeTier);
         int var10003 = 30;
         byte[] var10004 = new byte[17];
         var10004[0] = (byte)(-126);
         var10004[1] = (byte)(-17);
         var10004[2] = (byte)(113);
         var10004[3] = (byte)(-82);
         var10004[4] = (byte)(58);
         var10004[5] = (byte)(19);
         var10004[6] = (byte)(-126);
         var10004[7] = (byte)(-82);
         var10004[8] = (byte)(-31);
         var10004[9] = (byte)(-46);
         var10004[10] = (byte)(-127);
         var10004[11] = (byte)(-42);
         var10004[12] = (byte)(48);
         var10004[13] = (byte)(-57);
         var10004[14] = (byte)(-66);
         var10004[15] = (byte)(-35);
         var10004[16] = (byte)(-52);
         if (!this.requestHeading(var10001, TunnelHeadingArbiter.HeadingOwner.RECOVERY, var10003, $$v_s(var10004, -1877252675377835827L))) {
            this.clearJumpEscape();
            return false;
         } else {
            this.logJumpEscapeTier();
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean tickJumpEscape() {
      if (this.jumpEscapeTier != TunnelBaseFinder.JumpEscapeTier.NONE && mc.field_1724 != null) {
         if (hasClearedJumpEscape(mc.field_1724.method_31478(), this.jumpEscapeStartY)) {
            this.finishJumpEscape(
               "climbed above start"
            );
            this.consecutiveIssuedJumpAttempts = 0;
            return false;
         }

         this.pressForward();
         this.tryAutoClearObstacle();
         this.jumpEscapeTicksLeft = advanceJumpEscapeTicks(this.jumpEscapeTicksLeft);
         if (this.jumpEscapeTicksLeft > 0) {
            return true;
         }

         TunnelBaseFinder.JumpEscapeTier var1 = nextJumpEscapeTier(this.jumpEscapeTier);
         if (var1 != TunnelBaseFinder.JumpEscapeTier.NONE) {
            this.jumpEscapeTier = var1;
            this.jumpEscapeTicksLeft = 30;
            if (this.requestHeading(
               jumpEscapeHeading(this.preEscapeHeading, this.jumpEscapeTier),
               TunnelHeadingArbiter.HeadingOwner.RECOVERY,
               30,
               "jump escape left"
            )) {
               this.logJumpEscapeTier();
               return true;
            }
         }

         TunnelBaseFinder.Heading var2 = this.preEscapeHeading;
         class_2338 var3 = mc.field_1724.method_24515();
         this.finishJumpEscape(
            "both side tiers failed"
         );
         this.consecutiveIssuedJumpAttempts = 0;
         TunnelBaseFinder.Heading var4 = var2 != null ? var2 : (this.targetDir != null ? this.targetDir : this.yawToHeading(mc.field_1724.method_36454()));
         this.markHeadingFailed(var3, var4);
         if (this.replanTracerEscape(
            var3,
            var4,
            "jump escape exhausted",
            TunnelHeadingArbiter.HeadingOwner.RECOVERY
         )) {
            this.warning(
               "Jump escape exhausted; rerouting on tracer."
            );
         }

         return true;
      } else {
         return false;
      }
   }

   private void finishJumpEscape(String reason) {
      TunnelBaseFinder.Heading var2 = restoreJumpEscapeHeading(this.preEscapeHeading);
      this.clearJumpEscape();
      if (var2 != null && mc.field_1724 != null) {
         this.requestHeading(
            var2,
            TunnelHeadingArbiter.HeadingOwner.RECOVERY,
            0,
            "restoring jump escape heading"
         );
      }

      if ((Boolean)this.debugMining.get()) {
         this.info(
            "Jump escape finished (%s)",
            reason
         );
      }
   }

   private void clearJumpEscape() {
      this.preEscapeHeading = null;
      this.jumpEscapeTier = TunnelBaseFinder.JumpEscapeTier.NONE;
      this.jumpEscapeTicksLeft = 0;
      this.jumpEscapeStartY = 0;
   }

   private void logJumpEscapeTier() {
      if ((Boolean)this.debugMining.get()) {
         byte[] var10001 = new byte[44];
         var10001[0] = (byte)(-94);
         var10001[1] = (byte)(-17);
         var10001[2] = (byte)(113);
         var10001[3] = (byte)(-82);
         var10001[4] = (byte)(58);
         var10001[5] = (byte)(19);
         var10001[6] = (byte)(-126);
         var10001[7] = (byte)(-82);
         var10001[8] = (byte)(-31);
         var10001[9] = (byte)(-46);
         var10001[10] = (byte)(-127);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(54);
         var10001[13] = (byte)(-57);
         var10001[14] = (byte)(-68);
         var10001[15] = (byte)(-57);
         var10001[16] = (byte)(-104);
         var10001[17] = (byte)(111);
         var10001[18] = (byte)(-65);
         var10001[19] = (byte)(-114);
         var10001[20] = (byte)(-52);
         var10001[21] = (byte)(-23);
         var10001[22] = (byte)(-45);
         var10001[23] = (byte)(-67);
         var10001[24] = (byte)(117);
         var10001[25] = (byte)(118);
         var10001[26] = (byte)(-76);
         var10001[27] = (byte)(50);
         var10001[28] = (byte)(-5);
         var10001[29] = (byte)(-99);
         var10001[30] = (byte)(-30);
         var10001[31] = (byte)(54);
         var10001[32] = (byte)(40);
         var10001[33] = (byte)(18);
         var10001[34] = (byte)(15);
         var10001[35] = (byte)(10);
         var10001[36] = (byte)(-101);
         var10001[37] = (byte)(-92);
         var10001[38] = (byte)(37);
         var10001[39] = (byte)(116);
         var10001[40] = (byte)(29);
         var10001[41] = (byte)(-25);
         var10001[42] = (byte)(32);
         var10001[43] = (byte)(63);
         String var1 = $$v_s(var10001, -1877252675377835827L);
         Object[] var10002 = new Object[3];
         var10002[0] = this.jumpEscapeTier;
         var10002[1] = 30;
         var10002[2] = this.jumpEscapeStartY;
         this.info(var1, var10002);
      }
   }

   private boolean tickPhysicalStuckCheck() {
      if (this.isBreaking) {
         this.stuckReferencePos = null;
         this.stuckCheckTicksLeft = 60;
         this.stuckJumpTicksLeft = 0;
         return false;
      }

      if (this.stuckJumpTicksLeft > 0) {
         this.stuckJumpTicksLeft--;
         if (this.stuckJumpTicksLeft == 0) {
            this.stuckReferencePos = null;
            this.stuckCheckTicksLeft = 60;
         }

         return false;
      } else {
         if (this.stuckJumpCooldown > 0) {
            this.stuckJumpCooldown--;
         }

         class_2338 var1 = mc.field_1724.method_24515();
         if (this.stuckReferencePos == null) {
            this.stuckReferencePos = var1.method_10062();
            this.stuckCheckTicksLeft = 60;
            return false;
         }

         this.stuckCheckTicksLeft--;
         if (this.stuckCheckTicksLeft > 0) {
            return false;
         }

         if (var1.method_10262(this.stuckReferencePos) < 1.0) {
            this.stuckReferencePos = var1.method_10062();
            this.stuckCheckTicksLeft = 60;
            if (!hasEarnedJumpEscape(this.consecutiveIssuedJumpAttempts)) {
               boolean var2 = this.tryAutoClearObstacle();
               this.consecutiveIssuedJumpAttempts = recordIssuedJumpAttempt(this.consecutiveIssuedJumpAttempts, var2);
               if (var2) {
                  this.stuckJumpTicksLeft = 20;
                  return true;
               }
            }

            class_2338 var4 = mc.field_1724.method_24515();
            TunnelBaseFinder.Heading var3 = this.targetDir != null
               ? this.targetDir
               : (this.facingDir != null ? this.facingDir : this.yawToHeading(mc.field_1724.method_36454()));
            if (hasEarnedJumpEscape(this.consecutiveIssuedJumpAttempts) && this.beginJumpEscape(var3)) {
               return true;
            } else {
               this.markHeadingFailed(var4, var3);
               if (this.replanTracerEscape(
                  var4,
                  var3,
                  "local recovery tracer reroute",
                  TunnelHeadingArbiter.HeadingOwner.RECOVERY
               )) {
                  this.consecutiveIssuedJumpAttempts = 0;
                  this.warning(
                     "No local recovery action; rerouting on tracer."
                  );
                  return true;
               } else {
                  return false;
               }
            }
         } else {
            this.consecutiveIssuedJumpAttempts = 0;
            this.stuckReferencePos = var1.method_10062();
            this.stuckCheckTicksLeft = 60;
            return false;
         }
      }
   }

   private boolean tryAutoClearObstacle() {
      class_746 var1 = mc.field_1724;
      if (var1 == null) {
         return false;
      }

      if (!isJumpEscapeSafe(var1.method_6101(), var1.method_5799(), var1.method_5771(), var1.method_5765(), var1.method_24828())) {
         return false;
      }

      class_2338 var2 = var1.method_24515().method_10086(2);
      TunnelBaseFinder.Heading var3 = this.targetDir != null ? this.targetDir : this.facingDir;
      if (this.isSolidCell(var2) && var3 != null && this.isSolidCell(this.offset(var1.method_24515(), var3, 1))) {
         return false;
      }

      class_304 var4 = mc.field_1690.field_1903;
      var4.method_23481(true);
      this.scheduleJumpKeyRelease();
      return true;
   }

   private void scheduleJumpKeyRelease() {
      this.jumpKeyReleaseTicks = 1;
   }

   private void releaseJumpKeyIfPending() {
      if (this.jumpKeyReleaseTicks > 0) {
         this.jumpKeyReleaseTicks--;
         if (this.jumpKeyReleaseTicks == 0 && mc.field_1690 != null) {
            mc.field_1690.field_1903.method_23481(false);
         }
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private boolean tickLocalRecovery() {
      if (mc.field_1724 != null
         && mc.field_1687 != null
         && this.repairPhase == TunnelBaseFinder.RepairPhase.IDLE
         && (this.phase == TunnelBaseFinder.Phase.MINING || this.phase == TunnelBaseFinder.Phase.ROTATING)) {
         if (!(Boolean)this.localRecovery.get()) {
            return false;
         }

         class_2338 var1 = mc.field_1724.method_24515();
         if (this.phase == TunnelBaseFinder.Phase.MINING && this.tickRise(var1)) {
            return true;
         }

         if (!this.predictedPath.isEmpty()) {
            return false;
         }

         if (TunnelRecoveryPlanner.isForbiddenTravelY(var1.method_10264())) {
            class_2338 var3 = this.findLayerEscapeTarget(var1);
            if (var3 != null) {
               this.moveTowardLocalTarget(var3, false);
               if (mc.field_1724.method_24515().method_10264() != var1.method_10264()) {
                  this.localRecoveryAction = null;
               }

               return true;
            } else {
               this.localRecoveryAction = TunnelRecoveryPlanner.Action.replan();
               return false;
            }
         } else {
            boolean var2 = this.localRecoveryAction != null && this.localRecoveryAction.type() != TunnelRecoveryPlanner.ActionType.REPLAN;
            if (!var2) {
               if (this.localRecoveryCooldown > 0) {
                  return false;
               }

               if (this.noProgressTicks < 10) {
                  return false;
               }

               this.localRecoveryEpisodeTicks = 0;
            }

            if (++this.localRecoveryEpisodeTicks > 60) {
               this.abortLocalRecovery("episode timeout");
               return false;
            }

            if (this.localRecoveryAction == null || this.localRecoveryTicks-- <= 0 || !this.isLocalRecoveryStillValid()) {
               this.localRecoveryAction = this.chooseLocalRecovery(var1);
               this.localRecoveryTicks = 20;
            }

            if (this.localRecoveryAction != null && this.localRecoveryAction.type() != TunnelRecoveryPlanner.ActionType.REPLAN) {
               switch (<unrepresentable>.$SwitchMap$me$drake$vieclient$modules$donut$TunnelRecoveryPlanner$ActionType[this.localRecoveryAction.type().ordinal()]) {
                  case 1:
                     this.aimAtAndMine(this.localRecoveryAction.target());
                     return true;
                  case 2:
                  case 3:
                     if (!this.isSafeLanding(this.localRecoveryAction.target())) {
                        this.localRecoveryAction = TunnelRecoveryPlanner.Action.replan();
                        return false;
                     }

                     this.moveTowardLocalTarget(this.localRecoveryAction.target(), this.localRecoveryAction.type() == TunnelRecoveryPlanner.ActionType.JUMP);
                     return true;
                  default:
                     return false;
               }
            } else {
               this.recoveryPitchTarget = null;
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void abortLocalRecovery(String reason) {
      if (this.localRecoveryAction != null && (Boolean)this.debugMining.get()) {
         byte[] var10001 = new byte[38];
         var10001[0] = (byte)(-92);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(127);
         var10001[3] = (byte)(-65);
         var10001[4] = (byte)(118);
         var10001[5] = (byte)(86);
         var10001[6] = (byte)(-125);
         var10001[7] = (byte)(-88);
         var10001[8] = (byte)(-29);
         var10001[9] = (byte)(-51);
         var10001[10] = (byte)(-110);
         var10001[11] = (byte)(-109);
         var10001[12] = (byte)(48);
         var10001[13] = (byte)(-41);
         var10001[14] = (byte)(-7);
         var10001[15] = (byte)(-44);
         var10001[16] = (byte)(-38);
         var10001[17] = (byte)(37);
         var10001[18] = (byte)(-66);
         var10001[19] = (byte)(-38);
         var10001[20] = (byte)(-49);
         var10001[21] = (byte)(-30);
         var10001[22] = (byte)(-127);
         var10001[23] = (byte)(-75);
         var10001[24] = (byte)(117);
         var10001[25] = (byte)(97);
         var10001[26] = (byte)(-67);
         var10001[27] = (byte)(106);
         var10001[28] = (byte)(-78);
         var10001[29] = (byte)(-118);
         var10001[30] = (byte)(-24);
         var10001[31] = (byte)(55);
         var10001[32] = (byte)(111);
         var10001[33] = (byte)(95);
         var10001[34] = (byte)(8);
         var10001[35] = (byte)(67);
         var10001[36] = (byte)(-33);
         var10001[37] = (byte)(-91);
         String var2 = $$v_s(var10001, -1877252675377835827L);
         Object[] var10002 = new Object[2];
         var10002[0] = reason;
         var10002[1] = this.localRecoveryTarget;
         this.info(var2, var10002);
      }

      this.localRecoveryAction = null;
      this.localRecoveryTarget = null;
      this.ascendBreakTarget = null;
      this.ascendMissTicks = 0;
      this.localRecoveryTicks = 0;
      this.localRecoveryEpisodeTicks = 0;
      this.recoveryPitchTarget = null;
      this.breakStartedTarget = null;
      this.localRecoveryCooldown = 60;
      this.noProgressTicks = 0;
      mc.field_1690.field_1886.method_23481(false);
   }

   private class_2338 findLayerEscapeTarget(class_2338 origin) {
      TunnelBaseFinder.Heading var2 = this.targetDir != null ? this.targetDir : this.yawToHeading(mc.field_1724.method_36454());
      class_2338[] var10000 = new class_2338[5];
      var10000[0] = origin.method_10074();
      var10000[1] = this.offset(origin, var2, 1).method_10074();
      var10000[2] = this.offset(origin, var2.clockwise(), 1).method_10074();
      var10000[3] = this.offset(origin, var2.counterClockwise(), 1).method_10074();
      var10000[4] = this.offset(origin, var2.opposite(), 1).method_10074();
      class_2338[] var3 = var10000;

      for (class_2338 var7 : var3) {
         if (var7.method_10264() == (-59) && this.isSafeLanding(var7)) {
            return var7.method_10062();
         }
      }

      return null;
   }

   private TunnelRecoveryPlanner.Action chooseLocalRecovery(class_2338 origin) {
      TunnelRecoveryPlanner.Snapshot.Builder var2 = TunnelRecoveryPlanner.Snapshot.builder(origin);
      TunnelBaseFinder.Heading var3 = this.targetDir != null ? this.targetDir : this.yawToHeading(mc.field_1724.method_36454());
      class_2338 var4 = this.findMineableCollision(origin, var3);
      if (var4 != null) {
         var2.edgeObstacle(var4, true);
      }

      class_2338 var5 = this.offset(origin, var3, 1);
      if (!this.isSafeLanding(var5)) {
         var2.frontBlocked();
      }

      class_2338 var6 = this.findLandingTarget(origin, var3);
      if (var6 != null) {
         var2.landing(var6);
      }

      return new TunnelRecoveryPlanner().choose(var2.build());
   }

   private class_2338 findMineableCollision(class_2338 origin, TunnelBaseFinder.Heading heading) {
      class_238 var3 = mc.field_1724.method_5829().method_1014(0.08);
      class_2338 var4 = null;
      int var5 = -2147483648;

      for (int var6 = origin.method_10264(); var6 <= origin.method_10264() + 1; var6++) {
         for (int var7 = origin.method_10263() - 1; var7 <= origin.method_10263() + 1; var7++) {
            for (int var8 = origin.method_10260() - 1; var8 <= origin.method_10260() + 1; var8++) {
               class_2338 var9 = new class_2338(var7, var6, var8);
               if (var3.method_994(new class_238(var9))) {
                  class_2680 var10 = mc.field_1687.method_8320(var9);
                  if (!var10.method_26215()
                     && !(var10.method_26214(mc.field_1687, var9) < 0.0F)
                     && mc.field_1687.method_8321(var9) == null
                     && this.hazards.canMine(var9)
                     && !var9.equals(origin)
                     && !var9.equals(origin.method_10074())) {
                     int var11 = (var9.method_10263() - origin.method_10263()) * heading.dx + (var9.method_10260() - origin.method_10260()) * heading.dz;
                     if (var11 > 0 && (var4 == null || var11 > var5 || var11 == var5 && var9.method_10264() < var4.method_10264())) {
                        var4 = var9.method_10062();
                        var5 = var11;
                     }
                  }
               }
            }
         }
      }

      return var4;
   }

   private class_2338 findLandingTarget(class_2338 origin, TunnelBaseFinder.Heading heading) {
      for (int var3 = 1; var3 <= (2); var3++) {
         class_2338 var4 = this.offset(origin, heading, var3);

         for (int var5 = 0; var5 <= 1; var5++) {
            class_2338 var6 = var4.method_10086(var5);
            if (this.isSafeLanding(var6)) {
               return var6;
            }
         }
      }

      return null;
   }

   private boolean canWalkThrough(class_2338 pos) {
      if (mc.field_1687 == null || pos == null) {
         return false;
      }

      if (this.isSolidCell(pos)) {
         return false;
      }

      class_2680 var2 = mc.field_1687.method_8320(pos);
      return var2.method_26227().method_15767(class_3486.field_15518)
         ? false
         : !this.hazards.isFlowingLiquid(pos.method_10263(), pos.method_10264(), pos.method_10260());
   }

   private boolean canWalkOn(class_2338 pos) {
      if (!this.isSolidCell(pos)) {
         return false;
      }

      TunnelHazardModel.Verdict var2 = this.hazards.classify(pos.method_10084());
      return var2 != TunnelHazardModel.Verdict.LETHAL && var2 != TunnelHazardModel.Verdict.FLOWING;
   }

   private boolean isSolidCell(class_2338 pos) {
      if (mc.field_1687 != null && pos != null) {
         class_2680 var2 = mc.field_1687.method_8320(pos);
         return var2.method_26215() ? false : !var2.method_26220(mc.field_1687, pos).method_1110();
      } else {
         return false;
      }
   }

   private List ascendBreakSet(class_2338 origin, TunnelBaseFinder.Heading heading) {
      class_2338 var3 = this.offset(origin, heading, 1);
      class_2338 var4 = var3.method_10084();
      if (!this.canWalkOn(var3)) {
         return null;
      }

      if (this.isFallingBlock(var4.method_10086(2))) {
         return null;
      }

      ArrayList var5 = new ArrayList(3);
      if (this.isSolidCell(origin.method_10086(2))) {
         var5.add(origin.method_10086(2));
      }

      if (this.isSolidCell(var4)) {
         var5.add(var4);
      }

      if (this.isSolidCell(var4.method_10084())) {
         var5.add(var4.method_10084());
      }

      for (class_2338 var7 : var5) {
         if (!this.hazards.canMine(var7)) {
            return null;
         }

         if (mc.field_1687.method_8321(var7) != null) {
            return null;
         }
      }

      return var5;
   }

   private List levelBreakSet(class_2338 origin, TunnelBaseFinder.Heading heading) {
      class_2338 var3 = this.offset(origin, heading, 1);
      if (!this.canWalkOn(var3.method_10074())) {
         return null;
      }

      ArrayList var4 = new ArrayList(2);
      if (this.isSolidCell(var3)) {
         var4.add(var3);
      }

      if (this.isSolidCell(var3.method_10084())) {
         var4.add(var3.method_10084());
      }

      for (class_2338 var6 : var4) {
         if (!this.hazards.canMine(var6)) {
            return null;
         }

         if (mc.field_1687.method_8321(var6) != null) {
            return null;
         }
      }

      return var4;
   }

   private boolean isFallingBlock(class_2338 pos) {
      if (mc.field_1687 == null) {
         return false;
      }

      class_2680 var2 = mc.field_1687.method_8320(pos);
      return var2.method_27852(class_2246.field_10255)
         || var2.method_27852(class_2246.field_10102)
         || var2.method_27852(class_2246.field_10534)
         || var2.method_27852(class_2246.field_42728)
         || var2.method_27852(class_2246.field_43227);
   }

   private double breakTicks(class_2338 pos) {
      if (mc.field_1687 != null && mc.field_1724 != null) {
         class_2680 var2 = mc.field_1687.method_8320(pos);
         float var3 = var2.method_26165(mc.field_1724, mc.field_1687, pos);
         return var3 <= 0.0F ? 1000000.0 : 1.0 / var3 + 2.0;
      } else {
         return 1000000.0;
      }
   }

   private double totalBreakTicks(List cells) {
      double var2 = 0.0;

      for (class_2338 var5 : cells) {
         var2 += this.breakTicks(var5);
      }

      return var2;
   }

   private boolean headBonkClear(class_2338 origin) {
      class_2338 var2 = origin.method_10086(2);
      return this.canWalkThrough(var2.method_10095())
         && this.canWalkThrough(var2.method_10078())
         && this.canWalkThrough(var2.method_10072())
         && this.canWalkThrough(var2.method_10067());
   }

   static TunnelBaseFinder.JumpProjection projectJump(double px, double pz, class_2338 dest, TunnelBaseFinder.Heading heading) {
      double var6 = dest.method_10263() + 0.5 - px;
      double var8 = dest.method_10260() + 0.5 - pz;
      if (heading.isDiagonal()) {
         double var12 = 1.4142135623730951;
         return new TunnelBaseFinder.JumpProjection(
            Math.abs((heading.dx * var6 + heading.dz * var8) / var12), Math.abs((-heading.dz * var6 + heading.dx * var8) / var12)
         );
      } else {
         int var10 = Math.abs(heading.dx);
         int var11 = Math.abs(heading.dz);
         return new TunnelBaseFinder.JumpProjection(var10 * Math.abs(var6) + var11 * Math.abs(var8), var11 * Math.abs(var6) + var10 * Math.abs(var8));
      }
   }

   static boolean isExecutingPlannedRise(boolean firstStepRises, class_2338 origin, class_2338 firstWaypoint, TunnelBaseFinder.Heading executedRoute) {
      return firstStepRises
         && origin != null
         && firstWaypoint != null
         && executedRoute != null
         && firstWaypoint.method_10263() == origin.method_10263() + executedRoute.dx
         && firstWaypoint.method_10260() == origin.method_10260() + executedRoute.dz;
   }

   private boolean shouldJumpNow(class_2338 origin, class_2338 dest, TunnelBaseFinder.Heading heading) {
      if (mc.field_1724 == null) {
         return false;
      } else if (mc.field_1724.method_5799()) {
         return true;
      } else if (!mc.field_1724.method_24828()) {
         return false;
      } else {
         double var4 = mc.field_1724.method_23317();
         double var6 = mc.field_1724.method_23321();
         TunnelBaseFinder.JumpProjection var8 = projectJump(var4, var6, dest, heading);
         class_243 var9 = mc.field_1724.method_18798();
         double var10 = heading.isDiagonal()
            ? (-heading.dz * var9.field_1352 + heading.dx * var9.field_1350) / 1.4142135623730951
            : Math.abs(heading.dx) * var9.field_1350 + Math.abs(heading.dz) * var9.field_1352;
         if (Math.abs(var10) > 0.1) {
            return false;
         } else {
            return this.headBonkClear(origin)
               ? true
               : var8.flatDistance() <= 1.2 * (heading.isDiagonal() ? 1.4142135623730951 : 1.0)
                  && var8.sideDistance() <= 0.2 * (heading.isDiagonal() ? 1.4142135623730951 : 1.0);
         }
      }
   }

   private boolean tickRise(class_2338 origin) {
      if (mc.field_1724 == null || mc.field_1687 == null) {
         return false;
      }

      if (this.localRecoveryCooldown > 0 && this.ascendBreakTarget == null) {
         return false;
      }

      if (this.noProgressTicks < 10 && this.ascendBreakTarget == null) {
         return false;
      }

      TunnelBaseFinder.Heading var2 = this.routeHeadingForRise();
      if (var2 == null) {
         return false;
      }

      class_2338 var3 = this.offset(origin, var2, 1);
      if (this.isSolidCell(var3) && this.canWalkOn(var3)) {
         List var4 = this.ascendBreakSet(origin, var2);
         List var5 = this.levelBreakSet(origin, var2);
         double var6 = var4 == null ? Double.MAX_VALUE : 7.80284688441047 + this.totalBreakTicks(var4);
         double var8 = var5 == null ? Double.MAX_VALUE : 4.63284688441047 + this.totalBreakTicks(var5);
         if (var6 == Double.MAX_VALUE && var8 == Double.MAX_VALUE) {
            this.ascendBreakTarget = null;
            this.ascendMissTicks = 0;
            this.localRecoveryCooldown = 60;
            this.markHeadingFailed(origin, var2);
            this.warning(
               "Rise impassable both ways; rerouting."
            );
            this.replanTracerEscape(
               origin,
               var2,
               "rise impossible",
               TunnelHeadingArbiter.HeadingOwner.RECOVERY
            );
            return true;
         }

         boolean var10 = var8 <= var6;
         List var11 = var10 ? var5 : var4;
         if (!var11.isEmpty()) {
            class_2338 var13 = (class_2338)var11.get(0);
            if ((Boolean)this.debugMining.get() && !var13.equals(this.ascendBreakTarget)) {
               this.info(
                  "Rise: %s (level=%.1f ascend=%.1f), breaking %s",
                  var10
                     ? "level"
                     : "ascend",
                  var8,
                  var6,
                  var13
               );
            }

            this.ascendBreakTarget = var13;
            if (this.breakRiseCell(var13)) {
               return true;
            }

            this.ascendBreakTarget = null;
            return true;
         } else {
            if (var11.isEmpty()) {
               this.breakWatchdog.reset();
               mc.field_1690.field_1886.method_23481(false);
            }

            this.ascendBreakTarget = null;
            this.ascendMissTicks = 0;
            this.recoveryPitchTarget = null;
            if (var10) {
               return false;
            }

            this.pressForward();
            class_2338 var12 = var3.method_10084();
            if (mc.field_1724.method_31478() > origin.method_10264()) {
               return true;
            }

            if (this.shouldJumpNow(origin, var12, var2)) {
               mc.field_1690.field_1903.method_23481(true);
               this.scheduleJumpKeyRelease();
            }

            return true;
         }
      } else {
         this.ascendBreakTarget = null;
         this.ascendMissTicks = 0;
         return false;
      }
   }

   private boolean breakRiseCell(class_2338 target) {
      if (target == null || mc.field_1724 == null || mc.field_1687 == null) {
         return false;
      } else if (mc.field_1687.method_8320(target).method_26215()) {
         this.recordCompletedBreak(target);
         this.breakWatchdog.reset();
         mc.field_1690.field_1886.method_23481(false);
         this.recoveryPitchTarget = null;
         this.ascendMissTicks = 0;
         return false;
      } else {
         this.localRecoveryTarget = target.method_10062();
         if (this.breakWatchdog.tick(target, mc.field_1687, mc.field_1724)) {
            this.abandonRiseTarget(
               target, "rise mining timeout"
            );
            return false;
         } else {
            boolean var2 = target.method_10264() >= mc.field_1724.method_31478() + 2;
            if (!var2) {
               this.recoveryPitchTarget = null;
               this.breakTarget = this.localRecoveryTarget;
               this.pressForward();
               mc.field_1690.field_1886.method_23481(true);
               this.markBreakStarted(target);
               return true;
            } else {
               double var3 = target.method_10264() + 0.5 - mc.field_1724.method_23320();
               double var5 = Math.hypot(target.method_10263() + 0.5 - mc.field_1724.method_23317(), target.method_10260() + 0.5 - mc.field_1724.method_23321());
               this.recoveryPitchTarget = (float)class_3532.method_15350(-Math.toDegrees(Math.atan2(var3, var5)), -90.0, 90.0);
               this.releaseMovementKeys();
               class_239 var7 = mc.field_1724.method_5745(4.5, 1.0F, false);
               boolean var8 = var7 instanceof class_3965 var9 && var7.method_17783() == class_240.field_1332 && var9.method_17777().equals(target);
               if (var8) {
                  this.ascendMissTicks = 0;
                  this.breakTarget = this.localRecoveryTarget;
                  mc.field_1690.field_1886.method_23481(true);
                  this.markBreakStarted(target);
                  return true;
               } else {
                  mc.field_1690.field_1886.method_23481(false);
                  if (++this.ascendMissTicks > 25) {
                     this.abandonRiseTarget(
                        target,
                        "crosshair never on target"
                     );
                     return false;
                  } else {
                     return true;
                  }
               }
            }
         }
      }
   }

   private void abandonRiseTarget(class_2338 target, String reason) {
      if ((Boolean)this.debugMining.get()) {
         byte[] var10001 = new byte[28];
         var10001[0] = (byte)(-70);
         var10001[1] = (byte)(-13);
         var10001[2] = (byte)(111);
         var10001[3] = (byte)(-69);
         var10001[4] = (byte)(58);
         var10001[5] = (byte)(27);
         var10001[6] = (byte)(-104);
         var10001[7] = (byte)(-93);
         var10001[8] = (byte)(-27);
         var10001[9] = (byte)(-126);
         var10001[10] = (byte)(-125);
         var10001[11] = (byte)(-105);
         var10001[12] = (byte)(52);
         var10001[13] = (byte)(-53);
         var10001[14] = (byte)(-7);
         var10001[15] = (byte)(-64);
         var10001[16] = (byte)(-56);
         var10001[17] = (byte)(106);
         var10001[18] = (byte)(-93);
         var10001[19] = (byte)(-64);
         var10001[20] = (byte)(-118);
         var10001[21] = (byte)(-93);
         var10001[22] = (byte)(-46);
         var10001[23] = (byte)(-67);
         var10001[24] = (byte)(120);
         var10001[25] = (byte)(55);
         var10001[26] = (byte)(-25);
         var10001[27] = (byte)(111);
         String var5 = $$v_s(var10001, -1877252675377835827L);
         Object[] var10002 = new Object[2];
         var10002[0] = target;
         var10002[1] = reason;
         this.info(var5, var10002);
      }

      this.breakWatchdog.reset();
      mc.field_1690.field_1886.method_23481(false);
      if (target != null && target.equals(this.breakStartedTarget)) {
         this.breakStartedTarget = null;
      }

      this.recoveryPitchTarget = null;
      this.ascendBreakTarget = null;
      this.ascendMissTicks = 0;
      this.localRecoveryCooldown = 60;
      TunnelBaseFinder.Heading var3 = this.routeHeadingForRise();
      if (var3 != null && mc.field_1724 != null) {
         class_2338 var4 = mc.field_1724.method_24515();
         this.markHeadingFailed(var4, var3);
         this.replanTracerEscape(var4, var3, reason, TunnelHeadingArbiter.HeadingOwner.RECOVERY);
      }
   }

   private TunnelBaseFinder.Heading routeHeadingForRise() {
      if (mc.field_1724 == null) {
         return null;
      } else if (!this.predictedPath.isEmpty()) {
         class_2338 var4 = mc.field_1724.method_24515();
         class_2338 var2 = (class_2338)this.predictedPath.get(0);
         TunnelBaseFinder.Heading var3 = this.headingFromDelta(var2.method_10263() - var4.method_10263(), var2.method_10260() - var4.method_10260());
         return var3 != null && !var3.isDiagonal() ? var3 : null;
      } else {
         TunnelBaseFinder.Heading var1 = this.targetDir != null ? this.targetDir : this.facingDir;
         return var1 != null && !var1.isDiagonal() ? var1 : null;
      }
   }

   private boolean isSafeLanding(class_2338 pos) {
      if (pos != null && pos.method_10264() != (-58)) {
         class_2680 var2 = mc.field_1687.method_8320(pos);
         class_2680 var3 = mc.field_1687.method_8320(pos.method_10084());
         class_2680 var4 = mc.field_1687.method_8320(pos.method_10074());
         return var2.method_26215() && var3.method_26215() && !var4.method_26215() && this.hazards.classify(pos) != TunnelHazardModel.Verdict.LETHAL;
      } else {
         return false;
      }
   }

   private void aimAtAndMine(class_2338 target) {
      if (target != null) {
         this.localRecoveryTarget = target.method_10062();
         this.breakTarget = this.localRecoveryTarget;
         this.recoveryPitchTarget = null;
         this.pressForward();
         mc.field_1690.field_1886.method_23481(true);
         this.markBreakStarted(this.localRecoveryTarget);
      }
   }

   private void moveTowardLocalTarget(class_2338 target, boolean jump) {
      TunnelBaseFinder.Heading var3 = this.headingFromDelta(
         target.method_10263() - mc.field_1724.method_31477(), target.method_10260() - mc.field_1724.method_31479()
      );
      if (var3 != null && this.turnLockTicks == 0 && var3 != this.targetDir) {
         int var10003 = 15;
         byte[] var10004 = new byte[21];
         var10004[0] = (byte)(-124);
         var10004[1] = (byte)(-11);
         var10004[2] = (byte)(127);
         var10004[3] = (byte)(-65);
         var10004[4] = (byte)(118);
         var10004[5] = (byte)(86);
         var10004[6] = (byte)(-125);
         var10004[7] = (byte)(-88);
         var10004[8] = (byte)(-29);
         var10004[9] = (byte)(-51);
         var10004[10] = (byte)(-110);
         var10004[11] = (byte)(-109);
         var10004[12] = (byte)(48);
         var10004[13] = (byte)(-41);
         var10004[14] = (byte)(-7);
         var10004[15] = (byte)(-63);
         var10004[16] = (byte)(-39);
         var10004[17] = (byte)(56);
         var10004[18] = (byte)(-85);
         var10004[19] = (byte)(-53);
         var10004[20] = (byte)(-34);
         this.requestHeading(var3, TunnelHeadingArbiter.HeadingOwner.RECOVERY, var10003, $$v_s(var10004, -1877252675377835827L));
      }

      this.pressForward();
      if (jump && mc.field_1724.method_24828()) {
         mc.field_1690.field_1903.method_23481(true);
         this.scheduleJumpKeyRelease();
      }
   }

   private boolean isLocalRecoveryStillValid() {
      if (this.localRecoveryAction != null && this.localRecoveryAction.target() != null) {
         if (this.localRecoveryAction.type() == TunnelRecoveryPlanner.ActionType.MINE) {
            class_2338 var1 = this.localRecoveryAction.target();
            if (var1 == null || mc.field_1687.method_8320(var1).method_26215()) {
               if (var1 != null) {
                  this.recordCompletedBreak(var1);
               }

               this.localRecoveryAction = null;
               this.recoveryPitchTarget = null;
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private TunnelBaseFinder.Heading headingFromDelta(int dx, int dz) {
      int var3 = Integer.signum(dx);
      int var4 = Integer.signum(dz);

      for (TunnelBaseFinder.Heading var8 : TunnelBaseFinder.Heading.VALUES) {
         if (var8.dx == var3 && var8.dz == var4) {
            return var8;
         }
      }

      return null;
   }

   private boolean immediatePathClear() {
      int var1 = Math.min(2, this.predictedPath.size());
      if (var1 == 0) {
         return false;
      }

      for (int var2 = 0; var2 < var1; var2++) {
         class_2338 var3 = (class_2338)this.predictedPath.get(var2);
         if (this.hazards.classifyWithLateralShell(var3.method_10263(), var3.method_10264(), var3.method_10260()).blocksRoute()) {
            return false;
         }
      }

      return true;
   }

   private TunnelBaseFinder.Heading nextHeadingFromPath() {
      if (this.predictedPath.size() < (2)) {
         return null;
      }

      class_2338 var1 = (class_2338)this.predictedPath.get(0);
      class_2338 var2 = (class_2338)this.predictedPath.get(1);
      int var3 = Integer.signum(var2.method_10263() - var1.method_10263());
      int var4 = Integer.signum(var2.method_10260() - var1.method_10260());

      for (TunnelBaseFinder.Heading var8 : TunnelBaseFinder.Heading.VALUES) {
         if (var8.dx == var3 && var8.dz == var4) {
            return var8;
         }
      }

      return null;
   }

   private TunnelBaseFinder.Heading firstHeadingFromPlayer() {
      if (!this.predictedPath.isEmpty() && mc.field_1724 != null) {
         class_2338 var1 = mc.field_1724.method_24515();
         class_2338 var2 = (class_2338)this.predictedPath.get(0);
         int var3 = Integer.signum(var2.method_10263() - var1.method_10263());
         int var4 = Integer.signum(var2.method_10260() - var1.method_10260());
         if (var3 == 0 && var4 == 0) {
            return this.nextHeadingFromPath();
         }

         for (TunnelBaseFinder.Heading var8 : TunnelBaseFinder.Heading.VALUES) {
            if (var8.dx == var3 && var8.dz == var4) {
               return var8;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private boolean refreshRouteAtSafetyMargin() {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         TunnelBaseFinder.Heading var1 = this.targetDir != null
            ? this.targetDir
            : (this.facingDir != null ? this.facingDir : this.yawToHeading(mc.field_1724.method_36454()));
         class_2338 var2 = mc.field_1724.method_24515();
         boolean var3 = this.isDangerAlong(var2, var1, 6);
         if (!var3) {
            this.hazardMarginCooldown = 0;
            this.hazardStuckSeconds = 0;
            return false;
         }

         if (this.hazardMarginCooldown > 0) {
            this.hazardMarginCooldown--;
            return false;
         }

         this.hazardMarginCooldown = 20;
         this.turnLockTicks = 0;
         this.predictedPath.clear();
         this.rebuildPredictedPath();
         TunnelBaseFinder.Heading var4 = this.firstHeadingFromPlayer();
         if (var4 != null && var4 != var1 && !this.isDangerAlong(var2, var4, 10)) {
            this.dogLegAttempts = 0;
            this.hazardStuckSeconds = 0;
            return this.requestHeading(
               var4,
               TunnelHeadingArbiter.HeadingOwner.HAZARD,
               50,
               "hazard within 6 blocks, refreshed route"
            );
         }

         if (this.replanTracerEscape(
            var2,
            var1,
            "hazard route fallback",
            TunnelHeadingArbiter.HeadingOwner.HAZARD
         )) {
            this.hazardStuckSeconds = 0;
            return true;
         }

         this.hazardStuckSeconds++;
         if (this.hazardStuckSeconds >= 8
            && this.replanTracerEscape(
               var2,
               var1,
               "hazard boxed escalation",
               TunnelHeadingArbiter.HeadingOwner.HAZARD
            )) {
            this.hazardStuckSeconds = 0;
            this.warning(
               "Hazard boxed for %ds; replanned tracer escape.",
               8
            );
            return true;
         }

         if (this.noEscapeWarnCooldown <= 0) {
            this.warning(
               "Hazard within %d blocks and no escape heading; retrying.",
               6
            );
            this.noEscapeWarnCooldown = 40;
         } else {
            this.noEscapeWarnCooldown--;
         }

         return false;
      } else {
         return false;
      }
   }

   private boolean tickPlanOrHazardTurn() {
      TunnelBaseFinder.Heading var1 = this.targetDir != null
         ? this.targetDir
         : (this.facingDir != null ? this.facingDir : this.yawToHeading(mc.field_1724.method_36454()));
      class_2338 var2 = mc.field_1724.method_24515();
      this.pendingCaveTurn = null;
      this.pendingCaveScans = 0;
      if (this.predictedPath.isEmpty() || !this.immediatePathClear()) {
         this.rebuildPredictedPath(false);
      }

      TunnelBaseFinder.Heading var3 = this.firstHeadingFromPlayer();
      if (var3 == null) {
         var3 = this.nextHeadingFromPath();
      }

      if (var3 == null || !this.immediatePathClear()) {
         return this.replanTracerEscape(
            var2,
            var1,
            "boxed-in tracer reroute",
            TunnelHeadingArbiter.HeadingOwner.ESCAPE
         );
      } else if (var3 != var1) {
         this.dogLegAttempts = 0;
         return this.requestRouteExecutionHeading(
            var3, "following route"
         );
      } else {
         return false;
      }
   }

   private TunnelBaseFinder.Heading chooseSafeTurn(class_2338 origin, TunnelBaseFinder.Heading forward, int distance) {
      TunnelRoutePlanner.Dir var4 = this.planner
         .chooseSafeTurn(
            origin.method_10263(),
            origin.method_10264(),
            origin.method_10260(),
            toDir(forward),
            distance,
            this.hazards,
            (x, z, dir) -> this.visitMap.visits(x, z) + this.headingFailurePenalty(x, z, toHeading(dir))
         );
      return var4 == null ? null : toHeading(var4);
   }

   private TunnelBaseFinder.Heading perpendicular(TunnelBaseFinder.Heading mainDir) {
      return mainDir.clockwise().clockwise();
   }

   private int shoreClearance(class_2338 origin, TunnelBaseFinder.Heading mainDir, TunnelBaseFinder.Heading side) {
      for (int var4 = 1; var4 <= (32); var4++) {
         class_2338 var5 = this.offset(origin, side, var4);
         if (this.isDangerColumn(var5)) {
            return 33;
         }

         if (!this.isDangerAlong(var5, mainDir, 6)) {
            return var4;
         }
      }

      return 33;
   }

   private TunnelBaseFinder.Heading chooseSkirtSide(class_2338 origin, TunnelBaseFinder.Heading mainDir) {
      TunnelBaseFinder.Heading var3 = this.perpendicular(mainDir);
      TunnelBaseFinder.Heading var4 = this.perpendicular(mainDir).opposite();
      int var5 = this.shoreClearance(origin, mainDir, var3);
      int var6 = this.shoreClearance(origin, mainDir, var4);
      if (var5 == (33) && var6 == (33)) {
         return null;
      } else {
         return var5 <= var6 ? var3 : var4;
      }
   }

   private TunnelBaseFinder.Heading rejoinStep(TunnelBaseFinder.Heading skirtSide, TunnelBaseFinder.Heading mainDir) {
      int var3 = Integer.signum(mainDir.dx - skirtSidex.dx);
      int var4 = Integer.signum(mainDir.dz - skirtSidex.dz);

      for (TunnelBaseFinder.Heading var8 : TunnelBaseFinder.Heading.VALUES) {
         if (var8.dx == var3 && var8.dz == var4) {
            return var8;
         }
      }

      return mainDir;
   }

   private void tickProgressRecovery() {
      if (this.repairPhase == TunnelBaseFinder.RepairPhase.IDLE && mc.field_1755 == null && !this.isBreaking) {
         if (++this.progressSampleTimer >= (10)) {
            this.progressSampleTimer = 0;
            class_2338 var1 = mc.field_1724.method_24515().method_10062();
            this.progressSamples.addLast(var1);
            if (this.progressSamples.size() > (60)) {
               class_2338 var2 = (class_2338)this.progressSamples.removeFirst();
               double var3 = var1.method_10263() - var2.method_10263();
               double var5 = var1.method_10260() - var2.method_10260();
               if (Math.sqrt(var3 * var3 + var5 * var5) < 16.0) {
                  this.enterEscapeMode(var1);
               }
            }
         }
      }
   }

   private void enterEscapeMode(class_2338 origin) {
      TunnelBaseFinder.Heading var2 = this.routeMainHeading != null ? this.routeMainHeading : this.targetDir;
      if (var2 != null) {
         this.markHeadingFailed(origin, var2);
      }

      TunnelBaseFinder.Heading var3 = null;
      double var4 = Double.NEGATIVE_INFINITY;

      for (TunnelBaseFinder.Heading var9 : TunnelBaseFinder.Heading.VALUES) {
         if (var9 != var2 && !this.isDangerAlong(origin, var9, 6)) {
            double var10 = this.visitMap.noveltyAlong(origin.method_10263(), origin.method_10260(), var9.dx, var9.dz, 24)
               + 8.0 * this.visitMap.outwardBias(origin.method_10263(), origin.method_10260(), var9.dx, var9.dz)
               - this.headingFailurePenalty(origin.method_10263(), origin.method_10260(), var9) * 0.5;
            if (var10 > var4) {
               var4 = var10;
               var3 = var9;
            }
         }
      }

      this.progressSamples.clear();
      if (var3 != null) {
         if (this.requestHeading(
            var3,
            TunnelHeadingArbiter.HeadingOwner.ESCAPE,
            200,
            "loop escape route"
         )) {
            this.preDetourMainHeading = null;
            this.detourStartPos = null;
            this.detourClearTicks = 0;
            this.committedRouteHeading = null;
            this.routeCommitTicks = 0;
            this.planner.resetSkirt();
            this.planner.setWanderSuppressed(true);
            this.wanderSuppressedTicks = 200;
            this.dogLegAttempts = 0;
            this.predictedPath.clear();
            this.ticksSinceReplan = 3;
            this.rebuildPredictedPath(true);
            this.warning(
               "Loop detected; rebuilt tracer toward %s.",
               var3
            );
            this.turnToTracer(
               "loop escape tracer route",
               true
            );
         }
      }
   }

   private void restoreMainHeadingAfterDetour() {
      this.planner.setWanderSuppressed(this.wanderSuppressedTicks > 0);
      if (this.preDetourMainHeading != null && mc.field_1724 != null) {
         class_2338 var1 = mc.field_1724.method_24515();
         boolean var2 = this.planner.skirtSide() == null && !this.isDangerAlong(var1, this.preDetourMainHeading, 10);
         if (var2) {
            this.detourClearTicks++;
         } else {
            this.detourClearTicks = 0;
         }

         if (this.detourClearTicks >= 40) {
            if (this.detourStartPos == null || !(var1.method_10262(this.detourStartPos) < 256.0)) {
               if (this.requestHeading(
                  this.preDetourMainHeading,
                  TunnelHeadingArbiter.HeadingOwner.DETOUR,
                  50,
                  "restoring main heading after detour"
               )) {
                  this.preDetourMainHeading = null;
                  this.detourStartPos = null;
                  this.detourClearTicks = 0;
                  this.predictedPath.clear();
                  this.ticksSinceReplan = 3;
                  this.rebuildPredictedPath(true);
                  this.info(
                     "Restored main tunnel heading after detour."
                  );
               }
            }
         }
      }
   }

   private void commitDetourHeading(TunnelBaseFinder.Heading heading) {
      if (heading != null) {
         TunnelBaseFinder.Heading var2 = this.routeMainHeading;
         int var10003 = 50;
         byte[] var10004 = new byte[25];
         var10004[0] = (byte)(-117);
         var10004[1] = (byte)(-11);
         var10004[2] = (byte)(113);
         var10004[3] = (byte)(-77);
         var10004[4] = (byte)(115);
         var10004[5] = (byte)(2);
         var10004[6] = (byte)(-123);
         var10004[7] = (byte)(-92);
         var10004[8] = (byte)(-18);
         var10004[9] = (byte)(-59);
         var10004[10] = (byte)(-60);
         var10004[11] = (byte)(-110);
         var10004[12] = (byte)(39);
         var10004[13] = (byte)(-38);
         var10004[14] = (byte)(-74);
         var10004[15] = (byte)(-64);
         var10004[16] = (byte)(-54);
         var10004[17] = (byte)(106);
         var10004[18] = (byte)(-92);
         var10004[19] = (byte)(-53);
         var10004[20] = (byte)(-53);
         var10004[21] = (byte)(-30);
         var10004[22] = (byte)(-56);
         var10004[23] = (byte)(-13);
         var10004[24] = (byte)(55);
         if (this.requestHeading(heading, TunnelHeadingArbiter.HeadingOwner.DETOUR, var10003, $$v_s(var10004, -1877252675377835827L))) {
            if (this.preDetourMainHeading == null) {
               this.preDetourMainHeading = var2;
               this.detourStartPos = mc.field_1724 != null ? mc.field_1724.method_24515().method_10062() : null;
            }

            this.detourClearTicks = 0;
         }
      }
   }

   private void trackVisited() {
      if (mc.field_1724 != null) {
         class_2338 var1 = mc.field_1724.method_24515();
         if (!var1.equals(this.lastVisitedPos)) {
            this.lastVisitedPos = var1.method_10062();
            this.visitMap.mark(var1.method_10263(), var1.method_10260());
         }
      }
   }

   private void tickFailedHeadings() {
      this.failedHeadingClock++;
      if (++this.failedHeadingCleanupTimer >= (100)) {
         this.failedHeadingCleanupTimer = 0;
         ObjectIterator var1 = this.failedHeadings.long2IntEntrySet().iterator();

         while (var1.hasNext()) {
            if (((it.unimi.dsi.fastutil.longs.Long2IntMap.Entry)var1.next()).getIntValue() <= this.failedHeadingClock) {
               var1.remove();
            }
         }
      }
   }

   private void markHeadingFailed(class_2338 pos, TunnelBaseFinder.Heading heading) {
      if (pos != null && heading != null) {
         this.failedHeadings.put(failedHeadingKey(pos, heading), this.failedHeadingClock + (600));
      }
   }

   private int headingFailurePenalty(int x, int z, TunnelBaseFinder.Heading heading) {
      return this.failedHeadings.getOrDefault(failedHeadingKey(x, z, heading), 0) > this.failedHeadingClock ? 40 : 0;
   }

   private static long failedHeadingKey(class_2338 pos, TunnelBaseFinder.Heading heading) {
      return failedHeadingKey(pos.method_10263(), pos.method_10260(), heading);
   }

   private static long failedHeadingKey(int x, int z, TunnelBaseFinder.Heading heading) {
      return class_2338.method_10064(x >> (3), heading.ordinal(), z >> (3));
   }

   private boolean turnToTracer(String reason, boolean safetyReroute) {
      TunnelBaseFinder.Heading var3 = this.firstHeadingFromPlayer();
      if (var3 == null) {
         return false;
      }

      TunnelBaseFinder.Heading var4 = this.targetDir != null ? this.targetDir : this.facingDir;
      if (var3 == var4) {
         return false;
      }

      this.turnTo(var3, reason, false);
      if (safetyReroute) {
         this.turnLockTicks = 50;
      }

      return true;
   }

   private boolean replanTracerEscape(class_2338 origin, TunnelBaseFinder.Heading forward, String reason, TunnelHeadingArbiter.HeadingOwner owner) {
      TunnelBaseFinder.Heading var5 = this.findEscapeHeading(origin, forward);
      if (var5 != null && var5 != forward) {
         if (!this.requestHeading(var5, owner, 50, reason)) {
            return false;
         }

         this.predictedPath.clear();
         this.ticksSinceReplan = 3;
         this.rebuildPredictedPath(true);
         return this.turnToTracer(reason, true);
      } else {
         return false;
      }
   }

   private TunnelBaseFinder.Heading findEscapeHeading(class_2338 origin, TunnelBaseFinder.Heading forward) {
      int[] var10000 = new int[3];
      var10000[0] = 10;
      var10000[1] = 6;
      var10000[2] = 4;

      for (int var6 : var10000) {
         TunnelBaseFinder.Heading var7 = this.chooseSafeTurn(origin, forward, var6);
         if (var7 != null) {
            return var7;
         }
      }

      TunnelBaseFinder.Heading var11 = null;
      int var12 = 2147483647;
      TunnelBaseFinder.Heading[] var16 = new TunnelBaseFinder.Heading[2];
      var16[0] = forward.clockwise().clockwise().clockwise();
      var16[1] = forward.counterClockwise().counterClockwise().counterClockwise();
      TunnelBaseFinder.Heading[] var13 = var16;

      for (TunnelBaseFinder.Heading var9 : var13) {
         if (!this.isDangerAlong(origin, var9, 4)) {
            int var10 = this.visitMap.pathVisitCost(origin.method_10263(), origin.method_10260(), var9.dx, var9.dz, 4)
               + this.headingFailurePenalty(origin.method_10263(), origin.method_10260(), var9);
            if (var10 < var12) {
               var12 = var10;
               var11 = var9;
            }
         }
      }

      if (var11 != null) {
         return var11;
      }

      this.markHeadingFailed(origin, forward);
      return this.startDogLeg(forward);
   }

   private TunnelBaseFinder.Heading startDogLeg(TunnelBaseFinder.Heading forward) {
      if (this.dogLegResumeDir == null) {
         this.dogLegAttempts++;
         if (this.dogLegAttempts > (3)) {
            byte[] var10001 = new byte[44];
            var10001[0] = (byte)(-90);
            var10001[1] = (byte)(-11);
            var10001[2] = (byte)(60);
            var10001[3] = (byte)(-83);
            var10001[4] = (byte)(123);
            var10001[5] = (byte)(16);
            var10001[6] = (byte)(-108);
            var10001[7] = (byte)(-19);
            var10001[8] = (byte)(-16);
            var10001[9] = (byte)(-61);
            var10001[10] = (byte)(-112);
            var10001[11] = (byte)(-98);
            var10001[12] = (byte)(98);
            var10001[13] = (byte)(-56);
            var10001[14] = (byte)(-74);
            var10001[15] = (byte)(-57);
            var10001[16] = (byte)(-49);
            var10001[17] = (byte)(43);
            var10001[18] = (byte)(-66);
            var10001[19] = (byte)(-54);
            var10001[20] = (byte)(-118);
            var10001[21] = (byte)(-25);
            var10001[22] = (byte)(-57);
            var10001[23] = (byte)(-23);
            var10001[24] = (byte)(53);
            var10001[25] = (byte)(96);
            var10001[26] = (byte)(-76);
            var10001[27] = (byte)(117);
            var10001[28] = (byte)(-78);
            var10001[29] = (byte)(-102);
            var10001[30] = (byte)(-26);
            var10001[31] = (byte)(34);
            var10001[32] = (byte)(100);
            var10001[33] = (byte)(95);
            var10001[34] = (byte)(27);
            var10001[35] = (byte)(94);
            var10001[36] = (byte)(-101);
            var10001[37] = (byte)(-94);
            var10001[38] = (byte)(37);
            var10001[39] = (byte)(72);
            var10001[40] = (byte)(77);
            var10001[41] = (byte)(-78);
            var10001[42] = (byte)(48);
            var10001[43] = (byte)(101);
            this.disconnect($$v_s(var10001, -1877252675377835827L));
            return null;
         }

         this.dogLegResumeDir = forward.clockwise().clockwise();
         this.dogLegStartPos = mc.field_1724.method_24515().method_10062();
      }

      return forward.opposite();
   }

   private boolean isDangerAlong(class_2338 origin, TunnelBaseFinder.Heading heading, int distance) {
      return this.planner.blockedAhead(origin.method_10263(), origin.method_10264(), origin.method_10260(), toDir(heading), distance, this.hazards, false);
   }

   private boolean isWalkBlocked(class_2338 foot) {
      class_2680 var2 = mc.field_1687.method_8320(foot);
      if (var2.method_26215()) {
         if (!mc.field_1687.method_8320(foot.method_10086(1)).method_26215()) {
            return true;
         }

         if (mc.field_1687.method_8320(foot.method_10087(1)).method_26215()) {
            return true;
         }
      }

      return false;
   }

   private boolean isDangerColumn(class_2338 foot) {
      return this.hazards.classify(foot).blocksRoute();
   }

   private boolean isVoidHazard(class_2338 foot) {
      for (int var2 = 1; var2 <= (4); var2++) {
         class_2680 var3 = mc.field_1687.method_8320(foot.method_10087(var2));
         class_2680 var4 = mc.field_1687.method_8320(foot.method_10087(var2 - 1));
         if (var3.method_27852(class_2246.field_10164) || var3.method_26227().method_15767(class_3486.field_15518)) {
            return true;
         }

         if (var3.method_26227().method_15767(class_3486.field_15517) && var4.method_26215() && var2 >= (5)) {
            return true;
         }

         if (!var3.method_26215() && !var3.method_26227().method_15767(class_3486.field_15517)) {
            return false;
         }
      }

      return foot.method_10264() < (59);
   }

   private class_2338 offset(class_2338 origin, TunnelBaseFinder.Heading heading, int distance) {
      return origin.method_10069(heading.dx * distance, 0, heading.dz * distance);
   }

   private boolean isDangerBlock(class_2338 pos) {
      class_2680 var2 = mc.field_1687.method_8320(pos);
      return var2.method_27852(class_2246.field_10164)
         || var2.method_27852(class_2246.field_10255)
         || var2.method_27852(class_2246.field_10102)
         || var2.method_27852(class_2246.field_10534)
         || var2.method_26227().method_15767(class_3486.field_15518)
         || var2.method_26227().method_15767(class_3486.field_15517);
   }

   private void scanHazardChunks() {
      if (mc.field_1724 != null && mc.field_1687 != null) {
         class_1923 var1 = mc.field_1724.method_31476();
         int var2 = Math.max(1, 3);
         boolean var3 = this.lastHazardCenter == null
            || var1.field_9181 != this.lastHazardCenter.field_9181
            || var1.field_9180 != this.lastHazardCenter.field_9180;
         if (var3) {
            this.lastHazardCenter = var1;
            this.hazardChunkQueue.clear();
            this.hazardChunkQueued.clear();

            for (int var4 = -var2; var4 <= var2; var4++) {
               for (int var5 = -var2; var5 <= var2; var5++) {
                  long var6 = class_1923.method_8331(var1.field_9181 + var4, var1.field_9180 + var5);
                  if (this.hazardChunkQueued.add(var6)) {
                     this.hazardChunkQueue.addLast(var6);
                  }
               }
            }

            pruneHazardCellsOutsideWindow(this.hazardCellsByChunk, this.voidCellsByChunk, this.hazardChunkQueued);
         }

         int var11 = mc.field_1724.method_31478();
         int var12 = 0;
         long var13 = System.nanoTime() + 4000000L;

         while (var12 < (4) && System.nanoTime() < var13) {
            Long var8 = (Long)this.hazardChunkQueue.pollFirst();
            if (var8 == null) {
               break;
            }

            this.hazardChunkQueued.remove(var8);
            var12++;
            class_2818 var9 = mc.field_1687.method_2935().method_12126(class_1923.method_8325(var8), class_1923.method_8332(var8), false);
            if (var9 == null) {
               this.hazardCellsByChunk.remove(var8);
               this.voidCellsByChunk.remove(var8);
            } else {
               TunnelBaseFinder.ChunkHazardScan var10 = this.scanChunkHazards(var9, var11);
               this.hazardCellsByChunk.put(var8, var10.cells());
               this.voidCellsByChunk.put(var8, var10.voids());
            }
         }
      }
   }

   static void pruneHazardCellsOutsideWindow(Map hazardCells, Map voidCells, Set windowKeys) {
      hazardCells.keySet().retainAll(windowKeys);
      voidCells.keySet().retainAll(windowKeys);
   }

   private TunnelBaseFinder.ChunkHazardScan scanChunkHazards(class_2818 chunk, int footY) {
      int var3 = chunk.method_12004().method_8326();
      int var4 = chunk.method_12004().method_8328();
      int var5 = Math.max(chunk.method_31607(), footY - (12));
      int var6 = Math.min(chunk.method_31600(), footY + (12));
      ArrayList var7 = new ArrayList();
      ArrayList var8 = new ArrayList();
      class_2339 var9 = new class_2339();

      for (int var10 = 0; var10 < (16); var10++) {
         for (int var11 = 0; var11 < (16); var11++) {
            int var12 = var3 + var10;
            int var13 = var4 + var11;
            if (this.isVoidHazard(var9.method_10103(var12, footY, var13))) {
               var8.add(var9.method_10062());
            }

            for (int var14 = var5; var14 <= var6; var14++) {
               if (this.isDangerBlock(var9.method_10103(var12, var14, var13))) {
                  var7.add(var9.method_10062());
               }
            }
         }
      }

      return new TunnelBaseFinder.ChunkHazardScan(var7, var8);
   }

   private boolean isLavaBlock(class_2338 pos) {
      class_2680 var2 = mc.field_1687.method_8320(pos);
      return var2.method_27852(class_2246.field_10164) || var2.method_26227().method_15767(class_3486.field_15518);
   }

   private boolean isTwoHighOpening(class_2338 foot) {
      return !this.isDangerBlock(foot) && !this.isDangerBlock(foot.method_10084())
         ? mc.field_1687.method_8320(foot).method_26215() && mc.field_1687.method_8320(foot.method_10084()).method_26215()
         : false;
   }

   private boolean isAligned() {
      float var1 = Math.abs(class_3532.method_15393(mc.field_1724.method_36454() - this.targetYaw));
      return var1 < 1.0F;
   }

   private boolean isMovementAligned() {
      if (mc.field_1724 == null) {
         return false;
      }

      float var1 = Math.abs(class_3532.method_15393(mc.field_1724.method_36454() - this.targetYaw));
      return var1 <= 22.5F;
   }

   private void steerByYaw(double frameTime) {
      this.steerYawToward(this.targetYaw, frameTime, this.rotationSpeed());
   }

   private double rotationSpeed() {
      return ((Integer)this.rotateSpeed.get()).intValue() * 20.0;
   }

   private void steerYawToward(float desiredYaw, double frameTime, double speed) {
      if (!(frameTime <= 0.0) && mc.field_1724 != null) {
         float var6 = mc.field_1724.method_36454();
         float var7 = class_3532.method_15393(desiredYaw - var6);
         if (!(Boolean)this.humanize.get() && Math.abs(var7) < 0.1F) {
            mc.field_1724.method_36456(desiredYaw);
         } else {
            float var8 = ((Double)mc.field_1690.method_42495().method_41753()).floatValue();
            float var9 = desiredYaw;
            if ((Boolean)this.humanize.get()) {
               float var10 = (float)Math.min(1.0, 0.2 + 0.8 * Math.abs(var7));
               long var11 = System.currentTimeMillis();
               float var13 = (float)(Math.sin(var11 * 0.007) * 0.035 * var10 + Math.cos(var11 * 0.004) * 0.02 * (1.0F - Math.min(1.0F, Math.abs(var7))));
               var9 += var13;
            }

            mc.field_1724.method_36456(TunnelMotion.stepYaw(var6, var9, frameTime, speed, var8));
         }
      }
   }

   private void steerMiningView(double frameTime) {
      class_243 var3 = this.isBreaking && this.breakTarget != null && this.breakTarget.equals(this.breakAimTarget) ? this.breakAimPoint : null;
      class_243 var4 = var3;
      if ((Boolean)this.humanize.get()) {
         if (var3 != null) {
            this.lastMiningFocus = var3;
            this.miningFocusGraceFrames = 10;
         } else if (this.miningFocusGraceFrames > 0 && this.lastMiningFocus != null) {
            this.miningFocusGraceFrames--;
            var4 = this.lastMiningFocus;
         } else {
            this.lastMiningFocus = null;
         }
      }

      TunnelMiningView.Aim var5 = TunnelMiningView.aim(mc.field_1724.method_33571(), var4, this.targetYaw, this.miningPitch);
      this.steerYawToward(var5.yaw(), frameTime, this.rotationSpeed());
      this.steerPitchToward(var5.pitch(), frameTime, this.rotationSpeed());
   }

   private void steerRepairPitch(double frameTime) {
      this.steerPitchToward(this.targetRepairPitch, frameTime, this.rotationSpeed());
   }

   private void steerPitchToward(float desiredPitch, double frameTime, double speed) {
      if (!(frameTime <= 0.0) && mc.field_1724 != null) {
         float var6 = mc.field_1724.method_36455();
         float var7 = mc.field_1690 != null ? ((Double)mc.field_1690.method_42495().method_41753()).floatValue() : 0.5F;
         float var8 = desiredPitch;
         if ((Boolean)this.humanize.get()) {
            float var9 = desiredPitch - var6;
            float var10 = (float)Math.min(1.0, 0.2 + 0.8 * Math.abs(var9));
            long var11 = System.currentTimeMillis();
            float var13 = (float)(Math.cos(var11 * 0.007) * 0.025 * var10 + Math.sin(var11 * 0.003) * 0.015 * (1.0F - Math.min(1.0F, Math.abs(var9))));
            var8 = class_3532.method_15363(desiredPitch + var13, -90.0F, 90.0F);
         }

         float var14 = TunnelMotion.stepPitch(var6, var8, frameTime, speed, var7);
         mc.field_1724.method_36457(var14);
      }
   }

   private boolean isPitchAligned(float pitch) {
      return Math.abs(mc.field_1724.method_36455() - pitch) < 0.11F;
   }

   private void ensurePredictedPath() {
      this.ticksSinceReplan++;
      this.ticksSinceFarReplan++;
      if (this.routeCommitTicks > 0) {
         this.routeCommitTicks--;
      }

      if (this.deadEndCooldown > 0) {
         this.deadEndCooldown--;
      }

      class_2338 var1 = mc.field_1724.method_24515();
      this.trimReachedPredictedPath(var1);
      this.trimReachedWaypoints(this.farPath, var1);
      if (this.predictedPath.isEmpty() || distSqXZ((class_2338)this.predictedPath.get(0), var1) > 100.0) {
         this.rebuildPredictedPath(false);
      } else if (this.ticksSinceFarReplan >= (20)) {
         this.rebuildFarPath();
      }
   }

   private void trimReachedWaypoints(List path, class_2338 playerPos) {
      int var3 = 0;

      while (var3 < path.size() && ((class_2338)path.get(var3)).method_10262(playerPos) <= 2.0) {
         var3++;
      }

      if (var3 > 0) {
         path.subList(0, var3).clear();
      }
   }

   private void trimReachedPredictedPath(class_2338 playerPos) {
      int var2 = 0;

      while (var2 < this.predictedPath.size() && ((class_2338)this.predictedPath.get(var2)).method_10262(playerPos) <= 2.0) {
         var2++;
      }

      if (var2 != 0) {
         this.predictedPath.subList(0, var2).clear();

         for (int var3 = this.predictedRiseAt.size() - 1; var3 >= 0; var3--) {
            int var4 = (Integer)this.predictedRiseAt.get(var3) - var2;
            if (var4 < 0) {
               this.predictedRiseAt.remove(var3);
            } else {
               this.predictedRiseAt.set(var3, var4);
            }
         }
      }
   }

   private static double distSqXZ(class_2338 a, class_2338 b) {
      double var2 = a.method_10263() - b.method_10263();
      double var4 = a.method_10260() - b.method_10260();
      return var2 * var2 + var4 * var4;
   }

   private void rebuildPredictedPath() {
      this.rebuildPredictedPath(true);
   }

   private void rebuildPredictedPath(boolean urgent) {
      if (urgent || this.ticksSinceReplan >= 3 || this.predictedPath.isEmpty()) {
         if (mc.field_1724 != null && mc.field_1687 != null) {
            int var2 = this.predictedPath.size();
            TunnelBaseFinder.Heading var3 = this.routeMainHeading != null
               ? this.routeMainHeading
               : (this.targetDir != null ? this.targetDir : this.yawToHeading(mc.field_1724.method_36454()));
            this.planner.setPlayer(mc.field_1724);
            TunnelRoutePlanner.Result var4 = this.planner
               .plan(
                  mc.field_1724.method_24515(),
                  toDir(var3),
                  24,
                  this.hazards,
                  2500,
                  (x, z, dir) -> this.visitMap.visits(x, z) + this.headingFailurePenalty(x, z, toHeading(dir))
               );
            List var5 = var4.path();
            if (var5.size() < 4 && var2 > var5.size()) {
               this.ticksSinceReplan = 0;
               if (this.ticksSinceFarReplan >= 20 || urgent) {
                  this.rebuildFarPath();
               }
            } else {
               this.predictedPath.clear();
               this.predictedRiseAt.clear();
               this.predictedPath.addAll(var5);
               this.predictedRiseAt.addAll(var4.riseAt());
               this.ticksSinceReplan = 0;
               if (this.ticksSinceFarReplan >= 20 || urgent) {
                  this.rebuildFarPath();
               }

               if (this.predictedPath.isEmpty()) {
                  this.spliceFarPathIntoNear();
               }

               if (var4.deadEnd() && this.predictedPath.size() < 4) {
                  if (this.deadEndCooldown <= 0) {
                     this.deadEndCooldown = 60;
                     this.warning(
                        "Route planner: dead end (%d usable waypoints); escaping.",
                        this.predictedPath.size()
                     );
                     this.enterEscapeMode(mc.field_1724.method_24515());
                  } else if (this.predictedPath.isEmpty()) {
                     this.warning(
                        "Route planner: no safe first step from here."
                     );
                  }
               }
            }
         }
      }
   }

   private void spliceFarPathIntoNear() {
      if (!this.farPath.isEmpty() && mc.field_1724 != null) {
         class_2338 var1 = mc.field_1724.method_24515();
         int var2 = -1;
         double var3 = Double.MAX_VALUE;

         for (int var5 = 0; var5 < this.farPath.size(); var5++) {
            double var6 = distSqXZ((class_2338)this.farPath.get(var5), var1);
            if (var6 < var3) {
               var3 = var6;
               var2 = var5;
            }
         }

         if (var2 >= 0 && !(var3 > 400.0)) {
            for (int var8 = var2; var8 < this.farPath.size() && this.predictedPath.size() < 24; var8++) {
               this.predictedPath.add((class_2338)this.farPath.get(var8));
            }

            if (!this.predictedPath.isEmpty()) {
               this.info(
                  "Near plan empty; spliced %d waypoints from the far route.",
                  this.predictedPath.size()
               );
            }
         }
      }
   }

   private void rebuildFarPath() {
      this.farPath.clear();
      if (mc.field_1724 != null && mc.field_1687 != null) {
         class_2338 var1 = this.predictedPath.isEmpty() ? mc.field_1724.method_24515() : (class_2338)this.predictedPath.get(this.predictedPath.size() - 1);
         TunnelBaseFinder.Heading var2 = this.routeMainHeading != null
            ? this.routeMainHeading
            : (this.targetDir != null ? this.targetDir : this.yawToHeading(mc.field_1724.method_36454()));
         this.planner.setPlayer(mc.field_1724);
         TunnelRoutePlanner.Result var3 = this.planner
            .plan(
               var1,
               toDir(var2),
               160,
               this.hazards,
               6000,
               (x, z, dir) -> this.visitMap.visits(x, z) + this.headingFailurePenalty(x, z, toHeading(dir)),
               false
            );
         this.farPath.addAll(var3.path());
         this.ticksSinceFarReplan = 0;
      }
   }

   private TunnelBaseFinder.Heading wanderDirAt(class_2338 pos, TunnelBaseFinder.Heading mainDir) {
      int var3 = Math.max(1, 12);
      long var4 = (long)pos.method_10263() * mainDir.dx + (long)pos.method_10260() * mainDir.dz;
      long var6 = Math.floorDiv(var4, var3);
      if ((var6 & 1L) == 0L) {
         return mainDir;
      } else {
         long var8 = var6 * -7046029254386353131L;
         var8 ^= var8 >>> (29);
         if ((int)Math.floorMod(var8, 100L) >= (25)) {
            return mainDir;
         } else {
            return (var8 >>> (33) & 1L) == 0L ? mainDir.clockwise() : mainDir.counterClockwise();
         }
      }
   }

   private TunnelBaseFinder.Heading yawToHeading(float yaw) {
      yaw = class_3532.method_15393(yaw);
      int var2 = Math.round(yaw / 45.0F);
      var2 = (var2 % (8) + (8)) % (8);
      return TunnelBaseFinder.Heading.VALUES[var2];
   }

   private float headingToYaw(TunnelBaseFinder.Heading heading) {
      return heading != null ? heading.yaw : 0.0F;
   }

   private boolean pressRouteMovement() {
      if (mc.field_1724 != null && !this.predictedPath.isEmpty()) {
         class_2338 var1 = mc.field_1724.method_24515();
         class_2338 var2 = (class_2338)this.predictedPath.get(0);
         TunnelBaseFinder.Heading var3 = this.headingFromDelta(var2.method_10263() - var1.method_10263(), var2.method_10260() - var1.method_10260());
         if (var3 == null && this.predictedPath.size() > 1) {
            class_2338 var4 = (class_2338)this.predictedPath.get(1);
            var3 = this.headingFromDelta(var4.method_10263() - var1.method_10263(), var4.method_10260() - var1.method_10260());
         }

         if (var3 == null) {
            this.releaseMovementKeys();
            return false;
         }

         if (this.routeCommitTicks > 0
            && this.committedRouteHeading != null
            && var3 != this.committedRouteHeading
            && !this.isDangerAlong(var1, this.committedRouteHeading, 2)) {
            var3 = this.committedRouteHeading;
         } else {
            this.committedRouteHeading = var3;
            this.routeCommitTicks = 4;
         }

         class_2338 var5 = this.offset(var1, var3, 1);
         if (isExecutingPlannedRise(this.predictedRiseAt.contains(0), var1, var2, var3) && this.shouldJumpNow(var1, var5.method_10084(), var3)) {
            mc.field_1690.field_1903.method_23481(true);
            this.scheduleJumpKeyRelease();
         }

         if (this.targetDir != var3
            && !this.requestRouteExecutionHeading(
               var3,
               "align to tracer segment"
            )) {
            this.releaseStrafeKeys();
            this.pressForward();
            return this.isMovementAligned();
         } else {
            this.releaseStrafeKeys();
            this.pressForward();
            return this.isMovementAligned();
         }
      } else {
         this.releaseMovementKeys();
         return false;
      }
   }

   private void pressForward() {
      mc.field_1690.field_1894.method_23481(true);
   }

   private void pressStrafe(boolean left) {
      mc.field_1690.field_1913.method_23481(left);
      mc.field_1690.field_1849.method_23481(!left);
   }

   private void releaseStrafeKeys() {
      mc.field_1690.field_1913.method_23481(false);
      mc.field_1690.field_1849.method_23481(false);
   }

   private void releaseMovementKeys() {
      mc.field_1690.field_1894.method_23481(false);
      mc.field_1690.field_1881.method_23481(false);
      this.releaseStrafeKeys();
   }

   private void releaseSneakAndSprintKeys() {
      mc.field_1690.field_1832.method_23481(false);
      mc.field_1690.field_1867.method_23481(false);
   }

   private void releaseAllKeys() {
      this.releaseMovementKeys();
      this.releaseSneakAndSprintKeys();
      mc.field_1690.field_1886.method_23481(false);
      mc.field_1690.field_1904.method_23481(false);
      mc.field_1690.field_1903.method_23481(false);
   }

   private void pauseForScreen() {
      this.releaseAllKeys();
      if (!this.pausedByScreen) {
         byte[] var10001 = new byte[12];
         var10001[0] = (byte)(-123);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(120);
         var10001[3] = (byte)(-85);
         var10001[4] = (byte)(118);
         var10001[5] = (byte)(19);
         var10001[6] = (byte)(-47);
         var10001[7] = (byte)(-66);
         var10001[8] = (byte)(-12);
         var10001[9] = (byte)(-61);
         var10001[10] = (byte)(-112);
         var10001[11] = (byte)(-109);
         this.abortLocalRecovery($$v_s(var10001, -1877252675377835827L));
         var10001 = new byte[12];
         var10001[0] = (byte)(-123);
         var10001[1] = (byte)(-11);
         var10001[2] = (byte)(120);
         var10001[3] = (byte)(-85);
         var10001[4] = (byte)(118);
         var10001[5] = (byte)(19);
         var10001[6] = (byte)(-47);
         var10001[7] = (byte)(-66);
         var10001[8] = (byte)(-12);
         var10001[9] = (byte)(-61);
         var10001[10] = (byte)(-112);
         var10001[11] = (byte)(-109);
         this.finishJumpEscape($$v_s(var10001, -1877252675377835827L));
         this.cancelCurrentBreakNow();
         this.lastMiningFocus = null;
         this.miningFocusGraceFrames = 0;
         this.pausedByScreen = true;
      }
   }

   private void resumeAfterScreen() {
      this.pausedByScreen = false;
      this.cancelCurrentBreak();
      this.releaseStrafeKeys();
      this.scanTimer = 0;
      this.rotateTimer = 0;
      this.sideStrafeCount = 0;
      TunnelBaseFinder.Heading var1 = this.targetDir != null ? this.targetDir : this.yawToHeading(mc.field_1724.method_36454());
      this.targetDir = var1;
      this.targetYaw = this.headingToYaw(var1);
      this.facingDir = this.yawToHeading(mc.field_1724.method_36454());
      this.phase = this.isAligned() ? TunnelBaseFinder.Phase.MINING : TunnelBaseFinder.Phase.ROTATING;
      this.resumeDelayTicks = 10;
   }

   private boolean breakTargetAheadOf(TunnelBaseFinder.Heading dir) {
      if (dir != null && this.breakTarget != null && mc.field_1724 != null) {
         double var2 = this.breakTarget.method_10263() + 0.5 - mc.field_1724.method_23317();
         double var4 = this.breakTarget.method_10260() + 0.5 - mc.field_1724.method_23321();
         return var2 * var2 + var4 * var4 > 20.25 ? false : var2 * dir.dx + var4 * dir.dz > 0.0;
      } else {
         return false;
      }
   }

   private void cancelCurrentBreak() {
      this.breakWatchdog.reset();
      mc.field_1690.field_1886.method_23481(false);
      long var1 = System.currentTimeMillis();
      if (mc.field_1761 != null && var1 - this.lastCancelAtMs >= 150L) {
         this.lastCancelAtMs = var1;
         this.cancelCount++;
         mc.field_1761.method_2925();
      }

      this.isBreaking = false;
      this.breakProgress = 0;
      this.breakTarget = null;
      this.breakAimTarget = null;
      this.breakAimPoint = null;
      this.breakStartedTarget = null;
   }

   private void cancelCurrentBreakNow() {
      this.lastCancelAtMs = 0L;
      this.cancelCurrentBreak();
   }

   private void startBreaking(class_2338 pos) {
      if (pos != null && mc.field_1761 != null) {
         if (!pos.equals(this.breakTarget) || !this.isBreaking) {
            this.breakTarget = pos.method_10062();
            this.breakProgress = 0;
            this.sideStrafeCount = 0;
         }

         this.isBreaking = true;
         mc.field_1690.field_1886.method_23481(true);
         this.markBreakStarted(this.breakTarget);
      }
   }

   private void swingAtBlock(class_2338 pos) {
      if (pos != null) {
         mc.field_1724.method_6104(class_1268.field_5808);
      }
   }

   private class_2338 getBlockInFront() {
      class_239 var1 = mc.field_1724.method_5745(4.5, 1.0F, false);
      if (var1 instanceof class_3965 var2 && var1.method_17783() == class_240.field_1332) {
         class_2338 var3 = var2.method_17777();
         class_2680 var4 = mc.field_1687.method_8320(var3);
         if (!var4.method_26215() && var4.method_26214(mc.field_1687, var3) >= 0.0F) {
            if (!this.hazards.canMine3x3(var3, toDir(this.facingDir != null ? this.facingDir : this.yawToHeading(mc.field_1724.method_36454())))) {
               this.breakTarget = null;
               this.breakAimTarget = null;
               this.breakAimPoint = null;
               this.breakStartedTarget = null;
               return null;
            }

            if (!var3.equals(this.breakAimTarget) || this.breakAimPoint == null) {
               this.breakAimTarget = var3.method_10062();
               this.breakAimPoint = var2.method_17784();
            }

            this.breakSide = var2.method_17780();
            return var3;
         }
      }

      this.breakTarget = null;
      this.breakAimTarget = null;
      this.breakAimPoint = null;
      this.breakStartedTarget = null;
      return null;
   }

   private static int invIndexToScreenId(int index) {
      return index <= (8) ? (36) + index : index;
   }

   private static void clickPlayerSlot(int slotId, int button, class_1713 type) {
      if (mc.field_1724 != null) {
         mc.field_1761.method_2906(mc.field_1724.field_7498.field_7763, slotId, button, type, mc.field_1724);
      }
   }

   private static void swapMainToOffhand(int mainIndex) {
      clickPlayerSlot(invIndexToScreenId(mainIndex), 40, class_1713.field_7791);
   }

   private static void swapOffhandTo(int mainIndex) {
      if (mainIndex >= 0 && mainIndex <= (8)) {
         clickPlayerSlot(45, mainIndex, class_1713.field_7791);
      }
   }

   private static void swapMainTo(int fromIndex, int toIndex) {
      if (toIndex >= 0 && toIndex <= (8)) {
         clickPlayerSlot(invIndexToScreenId(fromIndex), toIndex, class_1713.field_7791);
      }
   }

   private void info(String format, Object... args) {
      VieClientMod.LOGGER.info("[TBF] " + format, args);
   }

   private void warning(String format, Object... args) {
      VieClientMod.LOGGER.warn("[TBF] " + format, args);
   }

   private static TunnelRoutePlanner.Dir toDir(TunnelBaseFinder.Heading h) {
      return TunnelRoutePlanner.Dir.VALUES[h.ordinal()];
   }

   private static TunnelBaseFinder.Heading toHeading(TunnelRoutePlanner.Dir d) {
      return TunnelBaseFinder.Heading.VALUES[d.ordinal()];
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

   private record ChunkHazardScan(List<class_2338> cells, List<class_2338> voids) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private enum DetectionKind {
      PLAYER("Player detected", 16733525),
      SPAWNER("Spawner found", 16755200),
      BASE("Base found", 5635925);

      private final String title;
      private final int color;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      DetectionKind(String title, int color) {
         this.title = titlex;
         this.color = colorx;
      }

      // $VF: synthetic method
      private static TunnelBaseFinder.DetectionKind[] $values() {
         TunnelBaseFinder.DetectionKind[] var10000 = new TunnelBaseFinder.DetectionKind[3];
         var10000[0] = PLAYER;
         var10000[1] = SPAWNER;
         var10000[2] = BASE;
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

   enum Heading {
      SOUTH(0, 1, 0.0F),
      SOUTHWEST(-1, 1, 45.0F),
      WEST(-1, 0, 90.0F),
      NORTHWEST(-1, -1, 135.0F),
      NORTH(0, -1, 180.0F),
      NORTHEAST(1, -1, -135.0F),
      EAST(1, 0, -90.0F),
      SOUTHEAST(1, 1, -45.0F);

      private static final TunnelBaseFinder.Heading[] VALUES = values();
      final int dx;
      final int dz;
      final float yaw;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      Heading(int dx, int dz, float yaw) {
         this.dx = dxx;
         this.dz = dzx;
         this.yaw = yawx;
      }

      boolean isDiagonal() {
         return this.dx != 0 && this.dz != 0;
      }

      TunnelBaseFinder.Heading clockwise() {
         return VALUES[(this.ordinal() + 1) % (8)];
      }

      TunnelBaseFinder.Heading counterClockwise() {
         return VALUES[(this.ordinal() + (7)) % (8)];
      }

      TunnelBaseFinder.Heading opposite() {
         return VALUES[(this.ordinal() + (4)) % (8)];
      }

      // $VF: synthetic method
      private static TunnelBaseFinder.Heading[] $values() {
         TunnelBaseFinder.Heading[] var10000 = new TunnelBaseFinder.Heading[8];
         var10000[0] = SOUTH;
         var10000[1] = SOUTHWEST;
         var10000[2] = WEST;
         var10000[3] = NORTHWEST;
         var10000[4] = NORTH;
         var10000[5] = NORTHEAST;
         var10000[6] = EAST;
         var10000[7] = SOUTHEAST;
         return var10000;
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

   enum JumpEscapeTier {
      NONE,
      RIGHT,
      LEFT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TunnelBaseFinder.JumpEscapeTier[] $values() {
         TunnelBaseFinder.JumpEscapeTier[] var10000 = new TunnelBaseFinder.JumpEscapeTier[3];
         var10000[0] = NONE;
         var10000[1] = RIGHT;
         var10000[2] = LEFT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-101);
         NONE = new TunnelBaseFinder.JumpEscapeTier();
         var10002 = new byte[5];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(91);
         var10002[3] = (byte)(-106);
         var10002[4] = (byte)(-121);
         RIGHT = new TunnelBaseFinder.JumpEscapeTier();
         var10002 = new byte[4];
         var10002[0] = (byte)(116);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(90);
         var10002[3] = (byte)(-118);
         LEFT = new TunnelBaseFinder.JumpEscapeTier();
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

   record JumpProjection(double flatDistance, double sideDistance) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private enum Phase {
      ALIGNING,
      ROTATING,
      MINING;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TunnelBaseFinder.Phase[] $values() {
         TunnelBaseFinder.Phase[] var10000 = new TunnelBaseFinder.Phase[3];
         var10000[0] = ALIGNING;
         var10000[1] = ROTATING;
         var10000[2] = MINING;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[8];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-103);
         var10002[4] = (byte)(-35);
         var10002[5] = (byte)(-6);
         var10002[6] = (byte)(103);
         var10002[7] = (byte)(126);
         ALIGNING = new TunnelBaseFinder.Phase();
         var10002 = new byte[8];
         var10002[0] = (byte)(106);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-97);
         var10002[4] = (byte)(-57);
         var10002[5] = (byte)(-6);
         var10002[6] = (byte)(103);
         var10002[7] = (byte)(126);
         ROTATING = new TunnelBaseFinder.Phase();
         var10002 = new byte[6];
         var10002[0] = (byte)(117);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-35);
         var10002[5] = (byte)(-12);
         MINING = new TunnelBaseFinder.Phase();
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

   enum RepairPhase {
      IDLE,
      BUY_BOTTLE_OPEN_SHOP,
      BUY_BOTTLE_CLICK_MENU,
      BUY_BOTTLE_CLICK_QTY_A,
      BUY_BOTTLE_CLICK_QTY_B,
      BUY_BOTTLE_VERIFY,
      BUY_BOTTLE_RESTOCK,
      PREPARE,
      AIM_DOWN,
      MENDING,
      AIM_RESTORE,
      OPEN_SHOP,
      FIND_TOTEM,
      CONFIRM_TOTEM,
      RESTORE_TOTEM;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TunnelBaseFinder.RepairPhase[] $values() {
         TunnelBaseFinder.RepairPhase[] var10000 = new TunnelBaseFinder.RepairPhase[15];
         var10000[0] = IDLE;
         var10000[1] = BUY_BOTTLE_OPEN_SHOP;
         var10000[2] = BUY_BOTTLE_CLICK_MENU;
         var10000[3] = BUY_BOTTLE_CLICK_QTY_A;
         var10000[4] = BUY_BOTTLE_CLICK_QTY_B;
         var10000[5] = BUY_BOTTLE_VERIFY;
         var10000[6] = BUY_BOTTLE_RESTOCK;
         var10000[7] = PREPARE;
         var10000[8] = AIM_DOWN;
         var10000[9] = MENDING;
         var10000[10] = AIM_RESTORE;
         var10000[11] = OPEN_SHOP;
         var10000[12] = FIND_TOTEM;
         var10000[13] = CONFIRM_TOTEM;
         var10000[14] = RESTORE_TOTEM;
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

   enum RestoreTotemStep {
      CLOSE_AND_DEFER,
      WAIT,
      EQUIP;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static TunnelBaseFinder.RestoreTotemStep[] $values() {
         TunnelBaseFinder.RestoreTotemStep[] var10000 = new TunnelBaseFinder.RestoreTotemStep[3];
         var10000[0] = CLOSE_AND_DEFER;
         var10000[1] = WAIT;
         var10000[2] = EQUIP;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[15];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(68);
         var10002[5] = (byte)(-59);
         var10002[6] = (byte)(-99);
         var10002[7] = (byte)(13);
         var10002[8] = (byte)(20);
         var10002[9] = (byte)(-3);
         var10002[10] = (byte)(-96);
         var10002[11] = (byte)(-77);
         var10002[12] = (byte)(31);
         var10002[13] = (byte)(7);
         var10002[14] = (byte)(-90);
         CLOSE_AND_DEFER = new TunnelBaseFinder.RestoreTotemStep();
         var10002 = new byte[4];
         var10002[0] = (byte)(111);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-118);
         WAIT = new TunnelBaseFinder.RestoreTotemStep();
         var10002 = new byte[5];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-53);
         var10002[2] = (byte)(73);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(81);
         EQUIP = new TunnelBaseFinder.RestoreTotemStep();
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
