package me.drake.vieclient.modules.movement;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.List;
import java.util.Locale;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.core.setting.StringSetting;
import me.drake.vieclient.mixin.EntityStatusS2CPacketAccessor;
import me.drake.vieclient.modules.donut.TunnelMotion;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_1268;
import net.minecraft.class_1304;
import net.minecraft.class_1703;
import net.minecraft.class_1713;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_2663;
import net.minecraft.class_2680;
import net.minecraft.class_2848;
import net.minecraft.class_2848.class_2849;

public final class AutoFireworkFlight extends Module {
   private static final int ALTITUDE_BAND = 3;
   private static final int PATH_COLOR = -754990515;
   private static final int PATH_LOW_COLOR = -755017126;
   private static final int IMPACT_COLOR = -520146896;
   private static final double PATH_Y_OFFSET = 0.12;
   private static final int SHOP_FAIL_COOLDOWN_TICKS = 1200;
   private final IntSetting stableY = (IntSetting)this.addSetting(
      new IntSetting(
         "Stable Y",
         "Target flight altitude",
         160,
         -64,
         320
      )
   );
   private final IntSetting fireworkSlot = (IntSetting)this.addSetting(
      new IntSetting(
         "Firework Slot",
         "Hotbar slot for fireworks",
         1,
         1,
         9
      )
   );
   private final IntSetting boostDelay = (IntSetting)this.addSetting(
      new IntSetting(
         "Boost Delay",
         "Ticks between firework boosts",
         20,
         1,
         200
      )
   );
   private final IntSetting pitch = (IntSetting)this.addSetting(
      new IntSetting(
         "Pitch",
         "Neutral flight pitch",
         -7,
         -45,
         20
      )
   );
   private final IntSetting rotateSpeed = (IntSetting)this.addSetting(
      new IntSetting(
         "Rotate Speed",
         "Camera pitch speed (1 = slowest, 50 = fastest)",
         30,
         1,
         50
      )
   );
   private final Setting showTracer = this.addSetting(
      new BoolSetting(
         "Show Tracer",
         "Draw the predicted glide path ahead of the player.",
         true
      )
   );
   private final Setting tracerTicks = this.addSetting(
      new IntSetting(
            "Tracer Ticks",
            "How far ahead to simulate, in ticks.",
            120,
            20,
            400
         )
         .setVisibleInGui(false)
   );
   private final Setting buyFireworks = this.addSetting(
      new BoolSetting(
         "Buy Fireworks",
         "Open the shop and buy rockets when the inventory runs dry.",
         true
      )
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
            "Text of the shop category item to open first.",
            "gear"
         )
         .setVisibleInGui(false)
   );
   private final Setting fireworkName = this.addSetting(
      new StringSetting(
            "Firework Name",
            "Text used to find the firework item.",
            "firework"
         )
         .setVisibleInGui(false)
   );
   private final Setting shopClickDelay = this.addSetting(
      new IntSetting(
            "Shop Click Delay",
            "Ticks to wait between shop clicks.",
            10,
            1,
            60
         )
         .setVisibleInGui(false)
   );
   private final Setting shopTimeout = this.addSetting(
      new IntSetting(
            "Shop Timeout",
            "Seconds before a stuck shop screen is abandoned.",
            10,
            2,
            60
         )
         .setVisibleInGui(false)
   );
   private final Setting shopMaxRetries = this.addSetting(
      new IntSetting(
            "Shop Max Retries",
            "Purchase attempts before giving up.",
            3,
            1,
            10
         )
         .setVisibleInGui(false)
   );
   private final Setting disconnectOnPop = this.addSetting(
      new BoolSetting(
         "Disconnect on Pop",
         "Automatically disconnect when a totem pops during flight for safety.",
         true
      )
   );
   private final Setting switchBack = this.addSetting(
      new BoolSetting(
         "Switch Back",
         "Switch back to the previous slot / totem after firework boost.",
         true
      )
   );
   private int boostCooldown;
   private int launchCooldown;
   private Float targetPitch;
   private int restoreSlot = -1;
   private AutoFireworkFlight.ShopPhase shopPhase = AutoFireworkFlight.ShopPhase.IDLE;
   private int shopTicks;
   private int shopRetries;
   private int shopCooldown;
   private boolean enteredCategory;
   private int emptyWarnCooldown;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public AutoFireworkFlight() {
      super(
         "Auto Firework Flight",
         "Sustains stable Elytra flight with fireworks",
         Category.MOVEMENT
      );
   }

   @Override
   public void onDisable() {
      this.boostCooldown = 0;
      this.launchCooldown = 0;
      this.targetPitch = null;
      this.restoreSlot = -1;
      if (this.shopPhase != AutoFireworkFlight.ShopPhase.IDLE && mc.field_1755 != null) {
         mc.field_1755.method_25419();
      }

      this.resetShop();
      this.shopCooldown = 0;
      this.emptyWarnCooldown = 0;
      super.onDisable();
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE && this.isInGame() && mc.field_1761 != null) {
         if (this.restoreSlot >= 0) {
            if (mc.field_1724 != null && (Boolean)this.switchBack.get() && this.restoreSlot < 9) {
               mc.field_1724.method_31548().method_61496(this.restoreSlot);
            }

            this.restoreSlot = -1;
         }

         if (this.boostCooldown > 0) {
            this.boostCooldown--;
         }

         if (this.launchCooldown > 0) {
            this.launchCooldown--;
         }

         if (this.shopCooldown > 0) {
            this.shopCooldown--;
         }

         if (this.emptyWarnCooldown > 0) {
            this.emptyWarnCooldown--;
         }

         if (this.shopPhase != AutoFireworkFlight.ShopPhase.IDLE) {
            this.targetPitch = null;
            this.tickShop();
         } else {
            boolean var2 = mc.field_1724.method_6118(class_1304.field_6174).method_31574(class_1802.field_8833);
            if (!var2) {
               this.targetPitch = null;
            } else {
               int var3 = (Integer)this.fireworkSlot.get() - 1;
               if (!mc.field_1724.method_31548().method_5438(var3).method_31574(class_1802.field_8639)) {
                  this.refill(var3);
                  if (!mc.field_1724.method_31548().method_5438(var3).method_31574(class_1802.field_8639) && this.beginShoppingIfOutOfStock()) {
                     return;
                  }
               }

               if (!mc.field_1724.method_6128()) {
                  this.targetPitch = null;
                  if (shouldStartGliding(!mc.field_1724.method_24828(), var2, false) && this.launchCooldown == 0 && mc.method_1562() != null) {
                     mc.method_1562().method_52787(new class_2848(mc.field_1724, class_2849.field_12982));
                     this.launchCooldown = 5;
                  }
               } else {
                  AutoFireworkFlight.VerticalIntent var4 = verticalIntent(mc.field_1724.method_23318(), (Integer)this.stableY.get(), 3);

                  this.targetPitch = switch (var4) {
                     case CLIMB -> (float)Math.min((Integer)this.pitch.get(), -15);
                     case NEUTRAL -> (float)((Integer)this.pitch.get()).intValue();
                     case DESCEND -> (float)Math.max((Integer)this.pitch.get(), 12);
                  };
                  if (var4 != AutoFireworkFlight.VerticalIntent.DESCEND && canBoost(this.boostCooldown)) {
                     if (mc.field_1724.method_31548().method_5438(var3).method_31574(class_1802.field_8639)) {
                        int var5 = mc.field_1724.method_31548().method_67532();
                        if (var5 != var3 && (Boolean)this.switchBack.get()) {
                           this.restoreSlot = var5;
                        }

                        mc.field_1724.method_31548().method_61496(var3);
                        mc.field_1761.method_2919(mc.field_1724, class_1268.field_5808);
                        mc.field_1724.method_6104(class_1268.field_5808);
                        this.boostCooldown = (Integer)this.boostDelay.get();
                     }
                  }
               }
            }
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame()) {
         if (!mc.field_1724.method_6128()) {
            this.targetPitch = null;
         } else {
            this.drawFlightTracer();
            if (this.targetPitch != null && mc.field_1755 == null) {
               mc.field_1724
                  .method_36457(
                     smoothPitch(mc.field_1724.method_36455(), this.targetPitch, event.getFrameTime(), ((Integer)this.rotateSpeed.get()).intValue() * 20.0)
                  );
            }
         }
      }
   }

   private void drawFlightTracer() {
      if ((Boolean)this.showTracer.get() && mc.field_1687 != null) {
         class_243 var1 = mc.field_1724.method_18798();
         FireworkFlightPath.State var2 = new FireworkFlightPath.State(
            mc.field_1724.method_23317(), mc.field_1724.method_23318(), mc.field_1724.method_23321(), var1.field_1352, var1.field_1351, var1.field_1350
         );
         float var3 = this.targetPitch != null ? this.targetPitch : mc.field_1724.method_36455();
         List var4 = FireworkFlightPath.simulate(var2, mc.field_1724.method_36454(), var3, 0.08, (Integer)this.tracerTicks.get(), this::isBlocked);
         if (!var4.isEmpty()) {
            double var5 = mc.field_1724.method_23317();
            double var7 = mc.field_1724.method_23318() + 0.12;
            double var9 = mc.field_1724.method_23321();
            int var11 = '﮾' ^ '﮽';
            int var12 = (Integer)this.stableY.get();

            for (FireworkFlightPath.Point var14 : var4) {
               double var15 = var14.y() + 0.12;
               int var17 = FireworkFlightPath.belowBand(var14.y(), var12, var11) ? -755017126 : -754990515;
               RenderUtil.drawLine(var5, var7, var9, var14.x(), var15, var14.z(), var17);
               var5 = var14.x();
               var7 = var15;
               var9 = var14.z();
            }

            FireworkFlightPath.Point var18 = (FireworkFlightPath.Point)var4.get(var4.size() - 1);
            if (this.isBlocked(var18)) {
               class_2338 var19 = class_2338.method_49637(var18.x(), var18.y(), var18.z());
               RenderUtil.drawBox(new class_238(var19), -520146896);
            }
         }
      }
   }

   private boolean isBlocked(FireworkFlightPath.Point point) {
      if (mc.field_1687 == null) {
         return false;
      }

      class_2338 var2 = class_2338.method_49637(point.x(), point.y(), point.z());
      if (mc.field_1687.method_2935().method_12126(var2.method_10263() >> (4), var2.method_10260() >> (4), false) == null) {
         return false;
      }

      class_2680 var3 = mc.field_1687.method_8320(var2);
      return var3.method_26215() ? false : !var3.method_26220(mc.field_1687, var2).method_1110();
   }

   @EventHandler
   public void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof class_2663 var2) {
         if ((Boolean)this.disconnectOnPop.get() && mc.field_1724 != null) {
            if (var2.method_11470() == (35)) {
               int var5 = ((EntityStatusS2CPacketAccessor)var2).getEntityId();
               boolean var4 = var5 == mc.field_1724.method_5628() || mc.field_1687 != null && var2.method_11469(mc.field_1687) == mc.field_1724;
               if (var4) {
                  byte[] var10001 = new byte[12];
                  var10001[0] = (byte)(95);
                  var10001[1] = (byte)(-11);
                  var10001[2] = (byte)(104);
                  var10001[3] = (byte)(-69);
                  var10001[4] = (byte)(-37);
                  var10001[5] = (byte)(12);
                  var10001[6] = (byte)(99);
                  var10001[7] = (byte)(-5);
                  var10001[8] = (byte)(19);
                  var10001[9] = (byte)(-46);
                  var10001[10] = (byte)(-127);
                  var10001[11] = (byte)(-110);
                  this.triggerSafetyDisconnect($$v_s(var10001, -4895146647464194002L));
               }
            }
         }
      }
   }

   public void triggerSafetyDisconnect(String reason) {
      VieClientMod.LOGGER
         .warn(
            "[AutoFireworkFlight] {}! Disconnecting immediately for safety.",
            reason
         );
      if (mc.method_1562() != null && mc.method_1562().method_48296() != null) {
         mc.method_1562().method_48296().method_10747(class_2561.method_43470("§c[Auto Firework Flight] " + reason + " - Disconnected for safety"));
      }

      if (mc.method_18854()) {
         this.setEnabled(false);
      } else {
         mc.execute(() -> this.setEnabled(false));
      }
   }

   private int countFireworks() {
      int var1 = 0;

      for (int var2 = 0; var2 < (36); var2++) {
         class_1799 var3 = mc.field_1724.method_31548().method_5438(var2);
         if (var3.method_31574(class_1802.field_8639)) {
            var1 += var3.method_7947();
         }
      }

      return var1;
   }

   private boolean beginShoppingIfOutOfStock() {
      if (this.countFireworks() > 0) {
         return false;
      }

      if (!(Boolean)this.buyFireworks.get()) {
         if (this.emptyWarnCooldown == 0) {
            this.warning(
               "Out of fireworks and buying is off."
            );
            this.emptyWarnCooldown = 200;
         }

         return false;
      } else if (this.shopCooldown <= 0 && mc.field_1755 == null && mc.field_1724.field_3944 != null) {
         this.info(
            "Out of fireworks; opening the shop."
         );
         this.shopPhase = AutoFireworkFlight.ShopPhase.OPEN;
         this.shopTicks = 0;
         this.shopRetries = 0;
         this.enteredCategory = false;
         return true;
      } else {
         return false;
      }
   }

   private void tickShop() {
      this.shopTicks++;
      switch (this.shopPhase) {
         case OPEN:
            if (this.shopTicks < 6) {
               return;
            }

            if (mc.field_1724.field_3944 == null) {
               this.abortShop("no network handler");
               return;
            }

            mc.field_1724.field_3944.method_45730(this.stripSlash((String)this.shopCommand.get()));
            this.enteredCategory = false;
            this.shopPhase = AutoFireworkFlight.ShopPhase.FIND_ITEM;
            this.shopTicks = 0;
            break;
         case FIND_ITEM:
            this.tickFindFireworkItem();
            break;
         case FIND_QUANTITY:
            this.tickFindFireworkQuantity();
            break;
         case VERIFY:
            this.tickVerifyPurchase();
            break;
         case CLOSE:
            if (this.shopTicks < 6) {
               return;
            }

            if (mc.field_1755 != null) {
               mc.field_1755.method_25419();
            }

            this.info(
               "Bought fireworks; resuming flight."
            );
            this.resetShop();
            break;
         default:
            this.resetShop();
      }
   }

   private void tickFindFireworkItem() {
      if (this.shopScreenReady()) {
         class_1703 var1 = mc.field_1724.field_7512;
         String var2 = this.normalizedName((String)this.fireworkName.get());
         int var3 = Math.max(0, var1.field_7761.size() - (36));

         for (int var4 = 0; var4 < var3; var4++) {
            class_1735 var5 = (class_1735)var1.field_7761.get(var4);
            if (!var5.method_7677().method_7960()
               && (var5.method_7677().method_31574(class_1802.field_8639) || this.normalizedName(var5.method_7677().method_7964().getString()).contains(var2))) {
               mc.field_1761.method_2906(var1.field_7763, var5.field_7874, 0, class_1713.field_7790, mc.field_1724);
               this.shopPhase = AutoFireworkFlight.ShopPhase.FIND_QUANTITY;
               this.shopTicks = 0;
               return;
            }
         }

         if (!this.enteredCategory && this.tryEnterCategory(var1, var3)) {
            this.enteredCategory = true;
            this.shopTicks = 0;
         }
      }
   }

   private void tickFindFireworkQuantity() {
      if (this.shopScreenReady()) {
         class_1703 var1 = mc.field_1724.field_7512;
         int var2 = Math.max(0, var1.field_7761.size() - (36));
         int var3 = -1;
         long var4 = -1L;

         for (int var6 = 0; var6 < var2; var6++) {
            class_1735 var7 = (class_1735)var1.field_7761.get(var6);
            if (!var7.method_7677().method_7960()) {
               long var8 = largestQuantityLabel(var7.method_7677());
               if (var8 > var4) {
                  var4 = var8;
                  var3 = var7.field_7874;
               }
            }
         }

         if (var3 >= 0) {
            mc.field_1761.method_2906(var1.field_7763, var3, 0, class_1713.field_7790, mc.field_1724);
            this.shopPhase = AutoFireworkFlight.ShopPhase.VERIFY;
            this.shopTicks = 0;
         }
      }
   }

   private void tickVerifyPurchase() {
      if (this.shopTicks >= (Integer)this.shopClickDelay.get()) {
         if (this.countFireworks() > 0) {
            this.shopPhase = AutoFireworkFlight.ShopPhase.CLOSE;
            this.shopTicks = 0;
         } else {
            this.shopRetries++;
            if (this.shopRetries > (Integer)this.shopMaxRetries.get()) {
               this.abortShop("no fireworks after " + this.shopMaxRetries.get() + " attempts");
            } else {
               this.shopPhase = AutoFireworkFlight.ShopPhase.OPEN;
               this.shopTicks = 0;
            }
         }
      }
   }

   private boolean shopScreenReady() {
      if (this.shopTicks > (Integer)this.shopTimeout.get() * (20)) {
         byte[] var10001 = new byte[35];
         var10001[0] = (byte)(120);
         var10001[1] = (byte)(-14);
         var10001[2] = (byte)(115);
         var10001[3] = (byte)(-82);
         var10001[4] = (byte)(-106);
         var10001[5] = (byte)(95);
         var10001[6] = (byte)(112);
         var10001[7] = (byte)(-26);
         var10001[8] = (byte)(6);
         var10001[9] = (byte)(-57);
         var10001[10] = (byte)(-118);
         var10001[11] = (byte)(-42);
         var10001[12] = (byte)(-118);
         var10001[13] = (byte)(-99);
         var10001[14] = (byte)(95);
         var10001[15] = (byte)(-52);
         var10001[16] = (byte)(53);
         var10001[17] = (byte)(37);
         var10001[18] = (byte)(-72);
         var10001[19] = (byte)(-114);
         var10001[20] = (byte)(116);
         var10001[21] = (byte)(-71);
         var10001[22] = (byte)(48);
         var10001[23] = (byte)(-76);
         var10001[24] = (byte)(-36);
         var10001[25] = (byte)(124);
         var10001[26] = (byte)(-16);
         var10001[27] = (byte)(102);
         var10001[28] = (byte)(87);
         var10001[29] = (byte)(-54);
         var10001[30] = (byte)(75);
         var10001[31] = (byte)(104);
         var10001[32] = (byte)(-126);
         var10001[33] = (byte)(87);
         var10001[34] = (byte)(25);
         this.abortShop($$v_s(var10001, -4895146647464194002L));
         return false;
      } else {
         return this.shopTicks >= (Integer)this.shopClickDelay.get() && mc.field_1755 != null && mc.field_1724.field_7512 != mc.field_1724.field_7498;
      }
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

   private void abortShop(String reason) {
      byte[] var10001 = new byte[32];
      var10001[0] = (byte)(77);
      var10001[1] = (byte)(-13);
      var10001[2] = (byte)(110);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(-63);
      var10001[5] = (byte)(67);
      var10001[6] = (byte)(97);
      var10001[7] = (byte)(-1);
      var10001[8] = (byte)(67);
      var10001[9] = (byte)(-46);
      var10001[10] = (byte)(-111);
      var10001[11] = (byte)(-124);
      var10001[12] = (byte)(-115);
      var10001[13] = (byte)(-100);
      var10001[14] = (byte)(90);
      var10001[15] = (byte)(-97);
      var10001[16] = (byte)(62);
      var10001[17] = (byte)(106);
      var10001[18] = (byte)(-83);
      var10001[19] = (byte)(-52);
      var10001[20] = (byte)(103);
      var10001[21] = (byte)(-78);
      var10001[22] = (byte)(39);
      var10001[23] = (byte)(-85);
      var10001[24] = (byte)(-35);
      var10001[25] = (byte)(119);
      var10001[26] = (byte)(-16);
      var10001[27] = (byte)(124);
      var10001[28] = (byte)(30);
      var10001[29] = (byte)(-127);
      var10001[30] = (byte)(24);
      var10001[31] = (byte)(50);
      this.warning($$v_s(var10001, -4895146647464194002L), reason);
      if (mc.field_1755 != null) {
         mc.field_1755.method_25419();
      }

      this.resetShop();
      this.shopCooldown = 1200;
   }

   private void resetShop() {
      this.shopPhase = AutoFireworkFlight.ShopPhase.IDLE;
      this.shopTicks = 0;
      this.shopRetries = 0;
      this.enteredCategory = false;
   }

   private void refill(int hotbar) {
      if (mc.field_1755 == null && mc.field_1761 != null) {
         int var2 = -1;

         for (int var3 = 9; var3 < (36); var3++) {
            if (mc.field_1724.method_31548().method_5438(var3).method_31574(class_1802.field_8639)) {
               var2 = var3;
               break;
            }
         }

         if (var2 >= 0) {
            int var4 = mc.field_1724.field_7498.field_7763;
            mc.field_1761.method_2906(var4, var2, 0, class_1713.field_7790, mc.field_1724);
            mc.field_1761.method_2906(var4, (36) + hotbar, 0, class_1713.field_7790, mc.field_1724);
            mc.field_1761.method_2906(var4, var2, 0, class_1713.field_7790, mc.field_1724);
         }
      }
   }

   private String stripSlash(String command) {
      String var3;
      if (command == null) {
         byte[] var10000 = new byte[4];
         var10000[0] = (byte)(120);
         var10000[1] = (byte)(-14);
         var10000[2] = (byte)(115);
         var10000[3] = (byte)(-82);
         var3 = $$v_s(var10000, -4895146647464194002L);
      } else {
         var3 = command.trim();
      }

      String var2 = var3;

      while (var2.startsWith("/")) {
         var2 = var2.substring(1);
      }

      if (var2.isEmpty()) {
         byte[] var4 = new byte[4];
         var4[0] = (byte)(120);
         var4[1] = (byte)(-14);
         var4[2] = (byte)(115);
         var4[3] = (byte)(-82);
         var3 = $$v_s(var4, -4895146647464194002L);
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
      var10001[0] = (byte)(87);
      var10001[1] = (byte)(-22);
      var10001[2] = (byte)(103);
      var10001[3] = (byte)(-109);
      var10001[4] = (byte)(-53);
      var10001[5] = (byte)(7);
      return var10000.replaceAll($$v_s(var10001, -4895146647464194002L), "").toLowerCase(Locale.ROOT).trim();
   }

   private void info(String format, Object... args) {
      VieClientMod.LOGGER.info("[AFF] " + format, args);
   }

   private void warning(String format, Object... args) {
      VieClientMod.LOGGER.warn("[AFF] " + format, args);
   }

   static AutoFireworkFlight.VerticalIntent verticalIntent(double y, int stableY, int band) {
      if (y < stableYx - band) {
         return AutoFireworkFlight.VerticalIntent.CLIMB;
      } else {
         return y > stableYx + band ? AutoFireworkFlight.VerticalIntent.DESCEND : AutoFireworkFlight.VerticalIntent.NEUTRAL;
      }
   }

   static boolean canBoost(int cooldown) {
      return cooldown <= 0;
   }

   static float smoothPitch(float current, float desired, double frameTime, double speed) {
      return TunnelMotion.stepPitch(current, desired, frameTime, speed);
   }

   static boolean shouldStartGliding(boolean airborne, boolean wearingElytra, boolean gliding) {
      return airborne && wearingElytra && !gliding;
   }

   static boolean isLocalTotemPop(byte status, boolean localPlayer) {
      return localPlayer && status == (35);
   }

   static long largestQuantityLabel(class_1799 stack) {
      return Math.max(largestNumberIn(stack.method_7964().getString()), stack.method_7947());
   }

   static long largestNumberIn(String text) {
      if (text == null) {
         return -1L;
      }

      long var1 = -1L;
      long var3 = -1L;
      boolean var5 = false;

      for (int var6 = 0; var6 < text.length(); var6++) {
         char var7 = text.charAt(var6);
         if (Character.isDigit(var7)) {
            var3 = var5 ? var3 * 10L + (var7 - (48)) : var7 - (48);
            var5 = true;
         } else if (var5) {
            var1 = Math.max(var1, var3);
            var5 = false;
            var3 = -1L;
         }
      }

      if (var5) {
         var1 = Math.max(var1, var3);
      }

      return var1;
   }

   public Setting getDisconnectOnPopSetting() {
      return this.disconnectOnPop;
   }

   public Setting getSwitchBackSetting() {
      return this.switchBack;
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

   private enum ShopPhase {
      IDLE,
      OPEN,
      FIND_ITEM,
      FIND_QUANTITY,
      VERIFY,
      CLOSE;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoFireworkFlight.ShopPhase[] $values() {
         AutoFireworkFlight.ShopPhase[] var10000 = new AutoFireworkFlight.ShopPhase[6];
         var10000[0] = IDLE;
         var10000[1] = OPEN;
         var10000[2] = FIND_ITEM;
         var10000[3] = FIND_QUANTITY;
         var10000[4] = VERIFY;
         var10000[5] = CLOSE;
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
         IDLE = new AutoFireworkFlight.ShopPhase();
         var10002 = new byte[4];
         var10002[0] = (byte)(114);
         var10002[1] = (byte)(-54);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-112);
         OPEN = new AutoFireworkFlight.ShopPhase();
         var10002 = new byte[9];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(78);
         var10002[5] = (byte)(-47);
         var10002[6] = (byte)(124);
         var10002[7] = 1;
         var10002[8] = (byte)(24);
         FIND_ITEM = new AutoFireworkFlight.ShopPhase();
         var10002 = new byte[13];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-102);
         var10002[4] = (byte)(78);
         var10002[5] = (byte)(-55);
         var10002[6] = (byte)(125);
         var10002[7] = (byte)(5);
         var10002[8] = (byte)(27);
         var10002[9] = (byte)(-10);
         var10002[10] = (byte)(-83);
         var10002[11] = (byte)(-94);
         var10002[12] = (byte)(16);
         FIND_QUANTITY = new AutoFireworkFlight.ShopPhase();
         var10002 = new byte[6];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(87);
         var10002[5] = (byte)(-63);
         VERIFY = new AutoFireworkFlight.ShopPhase();
         var10002 = new byte[5];
         var10002[0] = (byte)(126);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-115);
         var10002[4] = (byte)(84);
         CLOSE = new AutoFireworkFlight.ShopPhase();
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

   enum VerticalIntent {
      CLIMB,
      NEUTRAL,
      DESCEND;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static AutoFireworkFlight.VerticalIntent[] $values() {
         AutoFireworkFlight.VerticalIntent[] var10000 = new AutoFireworkFlight.VerticalIntent[3];
         var10000[0] = CLIMB;
         var10000[1] = NEUTRAL;
         var10000[2] = DESCEND;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-109);
         var10002[4] = (byte)(100);
         CLIMB = new AutoFireworkFlight.VerticalIntent();
         var10002 = new byte[7];
         var10002[0] = (byte)(118);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(73);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(116);
         var10002[5] = (byte)(35);
         var10002[6] = (byte)(93);
         NEUTRAL = new AutoFireworkFlight.VerticalIntent();
         var10002 = new byte[7];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(99);
         var10002[5] = (byte)(44);
         var10002[6] = (byte)(85);
         DESCEND = new AutoFireworkFlight.VerticalIntent();
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
