package me.drake.vieclient.core.module;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.auth.AuthManager;
import me.drake.vieclient.core.control.ActionArbiter;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.modules.client.Emote;
import me.drake.vieclient.modules.client.FakeRoles;
import me.drake.vieclient.modules.client.FakeStats;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.modules.client.InventoryProtect;
import me.drake.vieclient.modules.client.MusicHUD;
import me.drake.vieclient.modules.client.NameProtect;
import me.drake.vieclient.modules.combat.AimAssist;
import me.drake.vieclient.modules.combat.AnchorMacro;
import me.drake.vieclient.modules.combat.AutoCrystal;
import me.drake.vieclient.modules.combat.AutoDoubleHand;
import me.drake.vieclient.modules.combat.AutoHitCrystal;
import me.drake.vieclient.modules.combat.AutoInvTotem;
import me.drake.vieclient.modules.combat.AutoJumpReset;
import me.drake.vieclient.modules.combat.AutoSwordBotting;
import me.drake.vieclient.modules.combat.AutoTotem;
import me.drake.vieclient.modules.combat.CartMacro;
import me.drake.vieclient.modules.combat.DoubleAnchor;
import me.drake.vieclient.modules.combat.ElytraSwap;
import me.drake.vieclient.modules.combat.Hitbox;
import me.drake.vieclient.modules.combat.HoverTotem;
import me.drake.vieclient.modules.combat.MaceSwap;
import me.drake.vieclient.modules.combat.STap;
import me.drake.vieclient.modules.combat.SafeAnchor;
import me.drake.vieclient.modules.combat.ShieldDisabler;
import me.drake.vieclient.modules.combat.SpearSwap;
import me.drake.vieclient.modules.combat.StaticHitboxes;
import me.drake.vieclient.modules.combat.TriggerBot;
import me.drake.vieclient.modules.combat.WTap;
import me.drake.vieclient.modules.donut.AHSniper;
import me.drake.vieclient.modules.donut.AntiAdminSpectator;
import me.drake.vieclient.modules.donut.AutoCrop;
import me.drake.vieclient.modules.donut.AutoPearlMethod;
import me.drake.vieclient.modules.donut.AutoSell;
import me.drake.vieclient.modules.donut.AutoTreeFarmer;
import me.drake.vieclient.modules.donut.BlockEntitiesDebug;
import me.drake.vieclient.modules.donut.BoneDropper;
import me.drake.vieclient.modules.donut.ChunkDebug;
import me.drake.vieclient.modules.donut.EmergencySeller;
import me.drake.vieclient.modules.donut.InventorySell;
import me.drake.vieclient.modules.donut.RtpDebug;
import me.drake.vieclient.modules.donut.SpawnerBreaker;
import me.drake.vieclient.modules.donut.SpawnerTags;
import me.drake.vieclient.modules.donut.SpawnersNotifier;
import me.drake.vieclient.modules.donut.SpawnersProtect;
import me.drake.vieclient.modules.donut.StaffLists;
import me.drake.vieclient.modules.donut.SusChunkFinder;
import me.drake.vieclient.modules.donut.TunnelBaseFinder;
import me.drake.vieclient.modules.donut.VieDebug;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.movement.AutoFireworkFlight;
import me.drake.vieclient.modules.movement.AutoSprint;
import me.drake.vieclient.modules.movement.FreeLook;
import me.drake.vieclient.modules.movement.TridentBoost;
import me.drake.vieclient.modules.player.AutoEat;
import me.drake.vieclient.modules.player.AutoRelog;
import me.drake.vieclient.modules.player.AutoTool;
import me.drake.vieclient.modules.player.FastUse;
import me.drake.vieclient.modules.player.HomeSetter;
import me.drake.vieclient.modules.player.NoSlow;
import me.drake.vieclient.modules.player.SkinProtect;
import me.drake.vieclient.modules.player.SwingSpeed;
import me.drake.vieclient.modules.render.Accessories;
import me.drake.vieclient.modules.render.AnimationFX;
import me.drake.vieclient.modules.render.BedrockHoleESP;
import me.drake.vieclient.modules.render.BlockESP;
import me.drake.vieclient.modules.render.BlockNotifier;
import me.drake.vieclient.modules.render.BlockSelection;
import me.drake.vieclient.modules.render.BreakingProgress;
import me.drake.vieclient.modules.render.ColorCorrection;
import me.drake.vieclient.modules.render.CombatFX;
import me.drake.vieclient.modules.render.CrystalFX;
import me.drake.vieclient.modules.render.CustomCrosshair;
import me.drake.vieclient.modules.render.CustomFOV;
import me.drake.vieclient.modules.render.EdgeGlow;
import me.drake.vieclient.modules.render.FillGlow;
import me.drake.vieclient.modules.render.FullBright;
import me.drake.vieclient.modules.render.HoleESP;
import me.drake.vieclient.modules.render.ItemReplacement;
import me.drake.vieclient.modules.render.JumpCircle;
import me.drake.vieclient.modules.render.KeyboardSFX;
import me.drake.vieclient.modules.render.MobESP;
import me.drake.vieclient.modules.render.MotionBlur;
import me.drake.vieclient.modules.render.NameTags;
import me.drake.vieclient.modules.render.NonLa;
import me.drake.vieclient.modules.render.Optimization;
import me.drake.vieclient.modules.render.PlayerESP;
import me.drake.vieclient.modules.render.PlayerModels;
import me.drake.vieclient.modules.render.SkyCustom;
import me.drake.vieclient.modules.render.StorageESP;
import me.drake.vieclient.modules.render.SuspiciousESP;
import me.drake.vieclient.modules.render.SwingAnimation;
import me.drake.vieclient.modules.render.TargetESP;
import me.drake.vieclient.modules.render.Tracers;
import me.drake.vieclient.modules.render.Trajectories;
import me.drake.vieclient.modules.render.TrueStretched;
import me.drake.vieclient.modules.render.WeatherNotifier;
import me.drake.vieclient.modules.render.WorldParticles;
import me.drake.vieclient.modules.world.AutoPlace;
import net.minecraft.class_310;

public class ModuleManager {
   private final List modules = new ArrayList();
   private final Map modulesByClass = new HashMap();
   private final List activeTickingModules = new CopyOnWriteArrayList();
   private int enabledEntityCacheConsumersCount = 0;
   private long lastAuthEnforceMs = 0L;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public ModuleManager() {
      this.registerModules();
      byte[] var10001 = new byte[34];
      var10001[0] = (byte)(114);
      var10001[1] = (byte)(-52);
      var10001[2] = (byte)(117);
      var10001[3] = (byte)(-69);
      var10001[4] = (byte)(37);
      var10001[5] = (byte)(51);
      var10001[6] = (byte)(-75);
      var10001[7] = (byte)(-34);
      var10001[8] = (byte)(47);
      var10001[9] = (byte)(-42);
      var10001[10] = (byte)(-71);
      var10001[11] = (byte)(-42);
      var10001[12] = (byte)(108);
      var10001[13] = (byte)(-30);
      var10001[14] = (byte)(-109);
      var10001[15] = (byte)(-86);
      var10001[16] = (byte)(10);
      var10001[17] = (byte)(62);
      var10001[18] = (byte)(-87);
      var10001[19] = (byte)(-36);
      var10001[20] = (byte)(-77);
      var10001[21] = (byte)(-53);
      var10001[22] = (byte)(-84);
      var10001[23] = (byte)(-112);
      var10001[24] = (byte)(-20);
      var10001[25] = (byte)(50);
      var10001[26] = (byte)(-7);
      var10001[27] = (byte)(41);
      var10001[28] = (byte)(-118);
      var10001[29] = (byte)(-94);
      var10001[30] = (byte)(-56);
      var10001[31] = (byte)(86);
      var10001[32] = (byte)(-70);
      var10001[33] = (byte)(20);
      VieClientMod.LOGGER.info($$v_s(var10001, -7791366034300874740L), this.modules.size());
   }

   private void registerModules() {
      this.register(new FreeLook());
      this.register(new AutoSprint());
      this.register(new AutoFireworkFlight());
      this.register(new TridentBoost());
      this.register(new AutoEat());
      this.register(new AutoTool());
      this.register(new NoSlow());
      this.register(new FastUse());
      this.register(new SwingSpeed());
      this.register(new HomeSetter());
      this.register(new SkinProtect());
      this.register(new StorageESP());
      this.register(new BlockESP());
      this.register(new BlockNotifier());
      this.register(new BedrockHoleESP());
      this.register(new HoleESP());
      this.register(new MobESP());
      this.register(new PlayerESP());
      this.register(new FullBright());
      this.register(new Tracers());
      this.register(new NameTags());
      this.register(new MusicHUD());
      this.register(new SuspiciousESP());
      this.register(new BlockSelection());
      this.register(new BreakingProgress());
      this.register(new JumpCircle());
      this.register(new Trajectories());
      this.register(new WeatherNotifier());
      this.register(new NonLa());
      this.register(new CombatFX());
      this.register(new SkyCustom());
      this.register(new SwingAnimation());
      this.register(new CrystalFX());
      this.register(new WorldParticles());
      this.register(new PlayerModels());
      this.register(new Accessories());
      this.register(new AnimationFX());
      this.register(new CustomCrosshair());
      this.register(new CustomFOV());
      this.register(new ColorCorrection());
      this.register(new MotionBlur());
      this.register(new TargetESP());
      this.register(new EdgeGlow());
      this.register(new FillGlow());
      this.register(new TrueStretched());
      this.register(new Optimization());
      this.register(new KeyboardSFX());
      this.register(new ItemReplacement());
      this.register(new AutoPlace());
      this.register(new Freecam());
      this.register(new AutoRelog());
      this.register(new AHSniper());
      this.register(new AutoSell());
      this.register(new InventorySell());
      this.register(new EmergencySeller());
      this.register(new BoneDropper());
      this.register(new AutoTreeFarmer());
      this.register(new AutoCrop());
      this.register(new BlockEntitiesDebug());
      this.register(new RtpDebug());
      this.register(new AutoPearlMethod());
      this.register(new SpawnerBreaker());
      this.register(new SpawnersNotifier());
      this.register(new SpawnersProtect());
      this.registerDevModule(
         "me.drake.vieclient.modules.donut.AutoBuild"
      );
      this.registerDevModule(
         "me.drake.vieclient.modules.donut.AutoMine"
      );
      StaffLists var1 = new StaffLists();
      this.register(var1);
      var1.setEnabled(true);
      this.register(new AntiAdminSpectator());
      this.register(new ChunkDebug());
      this.register(new SusChunkFinder());
      this.register(new TunnelBaseFinder());
      this.register(new SpawnerTags());
      this.register(new VieDebug());
      HUD var2 = new HUD();
      this.register(var2);
      var2.setEnabled(true);
      ClientSettings var3 = new ClientSettings();
      this.register(var3);
      var3.setEnabled(true);
      this.register(new Emote());
      this.register(new FakeStats());
      this.register(new NameProtect());
      this.register(new InventoryProtect());
      this.register(new FakeRoles());
      this.register(new AutoInvTotem());
      this.register(new AimAssist());
      this.register(new AutoDoubleHand());
      this.register(new AutoCrystal());
      this.register(new AutoHitCrystal());
      this.register(new AutoJumpReset());
      this.register(new AnchorMacro());
      this.register(new CartMacro());
      this.register(new DoubleAnchor());
      this.register(new ElytraSwap());
      this.register(new HoverTotem());
      this.register(new Hitbox());
      this.register(new MaceSwap());
      this.register(new SafeAnchor());
      this.register(new ShieldDisabler());
      this.register(new SpearSwap());
      this.register(new StaticHitboxes());
      this.register(new TriggerBot());
      this.register(new AutoTotem());
      this.register(new WTap());
      this.register(new STap());
      this.register(new AutoSwordBotting());
   }

   private void register(Module module) {
      this.modules.add(module);
      this.modulesByClass.put(module.getClass(), module);
      if (module.isEnabled()) {
         if (!this.activeTickingModules.contains(module)) {
            this.activeTickingModules.add(module);
         }

         if (module.usesEntityCache()) {
            this.enabledEntityCacheConsumersCount++;
         }
      }
   }

   public synchronized void onModuleStateChanged(Module module, boolean enabled) {
      if (enabled) {
         if (!this.activeTickingModules.contains(module)) {
            this.activeTickingModules.add(module);
         }

         if (module.usesEntityCache()) {
            this.enabledEntityCacheConsumersCount++;
         }
      } else {
         this.activeTickingModules.remove(module);
         if (module.usesEntityCache()) {
            this.enabledEntityCacheConsumersCount = Math.max(0, this.enabledEntityCacheConsumersCount - 1);
         }
      }
   }

   private void registerDevModule(String className) {
      if (!Boolean.parseBoolean(
            System.getProperty(
               "vie.customer",
               "true"
            )
         )
         || !className.endsWith("AutoBuild")
            && !className.endsWith("AutoMine")) {
         try {
            Class var2 = Class.forName(className);
            if (Module.class.isAssignableFrom(var2)) {
               Module var3 = (Module)var2.getDeclaredConstructor().newInstance();
               Module var4 = this.getModuleByName(var3.getName());
               if (var4 != null) {
                  this.modules.remove(var4);
                  this.modulesByClass.remove(var4.getClass());
                  this.activeTickingModules.remove(var4);
               }

               this.register(var3);
            }
         } catch (ClassNotFoundException | NoClassDefFoundError var5) {
         } catch (Throwable var6) {
            VieClientMod.LOGGER
               .warn(
                  "[ModuleManager] Failed to register dev module {}",
                  className,
                  var6
               );
         }
      }
   }

   public void onTick() {
      ActionArbiter.get().tick();
      long var1 = System.currentTimeMillis();
      if (var1 - this.lastAuthEnforceMs >= 1000L) {
         this.lastAuthEnforceMs = var1;
         VieClientMod var3 = VieClientMod.getInstance();
         AuthManager var4 = var3 != null ? var3.getAuthManager() : null;
         if (var4 == null || !var4.canEnableProtectedModules()) {
            this.disableProtectedModules();
         }
      }

      class_310 var7 = class_310.method_1551();
      boolean var8 = var7 != null && var7.field_1724 != null && var7.field_1687 != null;

      for (Module var6 : this.activeTickingModules) {
         if (var6.isEnabled() && (var8 || var6.getCategory() == Category.PROTECT || var6.getCategory() == Category.CLIENT || var6.canTickOutOfGame())) {
            var6.onTick();
         }
      }
   }

   public void onKeyPress(int keyCode) {
      this.handleBindCode(keyCode);
   }

   public void onMousePress(int mouseButton) {
      this.handleBindCode((1000) + mouseButton);
   }

   private void handleBindCode(int bindCode) {
      this.toggleByBindCode(bindCode);

      for (Module var3 : this.modules) {
         try {
            var3.onKeyPress(bindCode);
         } catch (Throwable var5) {
            byte[] var10001 = new byte[42];
            var10001[0] = (byte)(114);
            var10001[1] = (byte)(-41);
            var10001[2] = (byte)(115);
            var10001[3] = (byte)(-70);
            var10001[4] = (byte)(19);
            var10001[5] = (byte)(51);
            var10001[6] = (byte)(-71);
            var10001[7] = (byte)(-10);
            var10001[8] = (byte)(32);
            var10001[9] = (byte)(-52);
            var10001[10] = (byte)(-123);
            var10001[11] = (byte)(-111);
            var10001[12] = (byte)(91);
            var10001[13] = (byte)(-11);
            var10001[14] = (byte)(-87);
            var10001[15] = (byte)(-29);
            var10001[16] = (byte)(60);
            var10001[17] = (byte)(56);
            var10001[18] = (byte)(-66);
            var10001[19] = (byte)(-63);
            var10001[20] = (byte)(-92);
            var10001[21] = (byte)(-113);
            var10001[22] = (byte)(-27);
            var10001[23] = (byte)(-123);
            var10001[24] = (byte)(-79);
            var10001[25] = (byte)(125);
            var10001[26] = (byte)(-6);
            var10001[27] = (byte)(13);
            var10001[28] = (byte)(-117);
            var10001[29] = (byte)(-82);
            var10001[30] = (byte)(-12);
            var10001[31] = (byte)(65);
            var10001[32] = (byte)(-84);
            var10001[33] = (byte)(73);
            var10001[34] = (byte)(15);
            var10001[35] = (byte)(94);
            var10001[36] = (byte)(-32);
            var10001[37] = (byte)(-112);
            var10001[38] = (byte)(14);
            var10001[39] = (byte)(123);
            var10001[40] = (byte)(-102);
            var10001[41] = (byte)(-65);
            VieClientMod.LOGGER.error($$v_s(var10001, -7791366034300874740L), var3.getName(), var5);
         }
      }
   }

   private void toggleByBindCode(int bindCode) {
      for (Module var3 : this.modules) {
         if (var3.getKeybind().isBound() && (Integer)var3.getKeybind().get() == bindCode) {
            if (var3.isTriggerOnBind()) {
               var3.trigger();
            } else {
               var3.toggle();
            }
         }
      }
   }

   public int disableProtectedModules() {
      int var1 = 0;

      for (Module var3 : this.modules) {
         if (var3.getCategory() != Category.CLIENT && var3.getCategory() != Category.PROTECT && var3.isEnabled()) {
            var3.setEnabled(false);
            var1++;
         }
      }

      return var1;
   }

   public int disableAllModules() {
      int var1 = 0;

      for (Module var3 : this.modules) {
         if (var3.isEnabled()) {
            var3.setEnabled(false);
            var1++;
         }
      }

      return var1;
   }

   public Module getModule(Class clazz) {
      return (Module)this.modulesByClass.get(clazz);
   }

   public Module getModuleByName(String name) {
      if (name == null) {
         return null;
      }

      for (Module var3 : this.modules) {
         if (var3.getName().equalsIgnoreCase(name)) {
            return var3;
         }
      }

      String var5 = name.replace(" ", "").replace("/", "");

      for (Module var4 : this.modules) {
         if (var4.getName().replace(" ", "").replace("/", "").equalsIgnoreCase(var5)) {
            return var4;
         }
      }

      if (var5.equalsIgnoreCase("staffnotifier")
         || var5.equalsIgnoreCase("stafflistnotifier")) {
         Module var7 = this.getModule(StaffLists.class);
         if (var7 != null) {
            return var7;
         }
      }

      if (var5.equalsIgnoreCase("rtp")
         || var5.equalsIgnoreCase("rtpdebug")) {
         Module var8 = this.getModule(RtpDebug.class);
         if (var8 != null) {
            return var8;
         }
      }

      if (var5.equalsIgnoreCase("autopearl")
         || var5.equalsIgnoreCase("pearlmethod")) {
         Module var9 = this.getModule(AutoPearlMethod.class);
         if (var9 != null) {
            return var9;
         }
      }

      if (var5.equalsIgnoreCase("viedebug")
         || var5.equalsIgnoreCase("vie")) {
         Module var10 = this.getModule(VieDebug.class);
         if (var10 != null) {
            return var10;
         }
      }

      return null;
   }

   public List getModules() {
      return Collections.unmodifiableList(this.modules);
   }

   public List getModules(Category category) {
      if (category == Category.SMP_UTILITIES) {
         LinkedHashMap var2 = new LinkedHashMap();
         this.modules.stream().filter(m -> m.getCategory() == Category.SMP_UTILITIES).forEach(m -> var2.putIfAbsent(m.getName().toLowerCase(Locale.ROOT), m));
         return new ArrayList(var2.values());
      } else {
         return this.modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
      }
   }

   public List getEnabled() {
      return this.modules.stream().filter(Module::isEnabled).collect(Collectors.toList());
   }

   public boolean hasEnabledNonClientModules() {
      for (Module var2 : this.modules) {
         if (var2.getCategory() != Category.CLIENT && var2.getCategory() != Category.PROTECT && var2.isEnabled()) {
            return true;
         }
      }

      return false;
   }

   public boolean hasEnabledEntityCacheConsumers() {
      return this.enabledEntityCacheConsumersCount > 0;
   }

   public void onGameOptionsLoad() {
      for (Module var2 : this.modules) {
         if (var2.isEnabled()) {
            try {
               var2.onGameOptionsLoad();
            } catch (Throwable var4) {
               byte[] var10001 = new byte[50];
               var10001[0] = (byte)(114);
               var10001[1] = (byte)(-52);
               var10001[2] = (byte)(117);
               var10001[3] = (byte)(-69);
               var10001[4] = (byte)(37);
               var10001[5] = (byte)(51);
               var10001[6] = (byte)(-75);
               var10001[7] = (byte)(-34);
               var10001[8] = (byte)(47);
               var10001[9] = (byte)(-42);
               var10001[10] = (byte)(-71);
               var10001[11] = (byte)(-42);
               var10001[12] = (byte)(81);
               var10001[13] = (byte)(-23);
               var10001[14] = (byte)(-77);
               var10001[15] = (byte)(-94);
               var10001[16] = (byte)(20);
               var10001[17] = (byte)(47);
               var10001[18] = (byte)(-125);
               var10001[19] = (byte)(-34);
               var10001[20] = (byte)(-94);
               var10001[21] = (byte)(-58);
               var10001[22] = (byte)(-29);
               var10001[23] = (byte)(-123);
               var10001[24] = (byte)(-30);
               var10001[25] = (byte)(94);
               var10001[26] = (byte)(-5);
               var10001[27] = (byte)(39);
               var10001[28] = (byte)(-118);
               var10001[29] = (byte)(-9);
               var10001[30] = (byte)(-62);
               var10001[31] = (byte)(82);
               var10001[32] = (byte)(-96);
               var10001[33] = (byte)(86);
               var10001[34] = (byte)(25);
               var10001[35] = (byte)(26);
               var10001[36] = (byte)(-90);
               var10001[37] = (byte)(-103);
               var10001[38] = (byte)(19);
               var10001[39] = (byte)(41);
               var10001[40] = (byte)(-63);
               var10001[41] = (byte)(-81);
               var10001[42] = (byte)(43);
               var10001[43] = (byte)(114);
               var10001[44] = (byte)(43);
               var10001[45] = (byte)(75);
               var10001[46] = (byte)(113);
               var10001[47] = (byte)(67);
               var10001[48] = (byte)(98);
               var10001[49] = (byte)(-105);
               VieClientMod.LOGGER.warn($$v_s(var10001, -7791366034300874740L), var2.getName(), var4);
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
}
