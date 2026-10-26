package me.drake.vieclient.core.module;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.CoreEntry;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.auth.AuthManager;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.core.setting.ButtonSetting;
import me.drake.vieclient.core.setting.KeybindSetting;
import me.drake.vieclient.core.setting.Setting;
import me.drake.vieclient.util.Sfx;
import me.drake.vieclient.util.render.ToastManager;
import net.minecraft.class_2561;
import net.minecraft.class_310;

public abstract class Module {
   protected static final class_310 mc = class_310.method_1551();
   private static final long AUTH_LOCK_NOTICE_COOLDOWN_MS = 2500L;
   private static long lastAuthLockNoticeAtMs = 0L;
   private final String name;
   private final String description;
   private final Category category;
   private boolean enabled = false;
   private final KeybindSetting keybind;
   private final List settings = new ArrayList();
   private final ButtonSetting resetButton;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   protected Module(String name, String description, Category category, int defaultKey) {
      this.name = namex;
      this.description = descriptionx;
      this.category = categoryx;
      byte[] var10003 = new byte[7];
      var10003[0] = (byte)(120);
      var10003[1] = (byte)(-1);
      var10003[2] = (byte)(101);
      var10003[3] = (byte)(-68);
      var10003[4] = (byte)(70);
      var10003[5] = (byte)(-42);
      var10003[6] = (byte)(-111);
      this.keybind = new KeybindSetting($$v_s(var10003, 357586016102764566L), "Toggle key for " + namex, defaultKey);
      this.keybind.onChanged(v -> this.requestConfigSave());
      var10003 = new byte[5];
      var10003[0] = (byte)(97);
      var10003[1] = (byte)(-1);
      var10003[2] = (byte)(111);
      var10003[3] = (byte)(-69);
      var10003[4] = (byte)(91);
      String var6 = $$v_s(var10003, 357586016102764566L);
      byte[] var10004 = new byte[29];
      var10004[0] = (byte)(97);
      var10004[1] = (byte)(-1);
      var10004[2] = (byte)(111);
      var10004[3] = (byte)(-69);
      var10004[4] = (byte)(91);
      var10004[5] = (byte)(-104);
      var10004[6] = (byte)(-108);
      var10004[7] = (byte)(64);
      var10004[8] = (byte)(55);
      var10004[9] = (byte)(-126);
      var10004[10] = (byte)(-105);
      var10004[11] = (byte)(-109);
      var10004[12] = (byte)(3);
      var10004[13] = (byte)(20);
      var10004[14] = (byte)(-76);
      var10004[15] = (byte)(58);
      var10004[16] = (byte)(4);
      var10004[17] = (byte)(57);
      var10004[18] = (byte)(-20);
      var10004[19] = (byte)(-38);
      var10004[20] = (byte)(-16);
      var10004[21] = (byte)(104);
      var10004[22] = (byte)(-63);
      var10004[23] = (byte)(25);
      var10004[24] = (byte)(-19);
      var10004[25] = (byte)(115);
      var10004[26] = (byte)(-31);
      var10004[27] = (byte)(42);
      var10004[28] = (byte)(-45);
      this.resetButton = new ButtonSetting(var6, $$v_s(var10004, 357586016102764566L), () -> {
         for (Setting var2 : this.settings) {
            var2.reset();
         }
      });
   }

   protected Module(String name, String description, Category category) {
      this(namex, descriptionx, categoryx, -1);
   }

   protected Setting addSetting(Setting setting) {
      setting.onChanged(v -> this.requestConfigSave());
      this.settings.add(setting);
      return setting;
   }

   public void onEnable() {
      VieClientMod var1 = VieClientMod.getInstance();
      if (var1 != null && var1.getEventBus() != null) {
         var1.getEventBus().subscribe(this);
      }
   }

   public void onDisable() {
      VieClientMod var1 = VieClientMod.getInstance();
      if (var1 != null && var1.getEventBus() != null) {
         var1.getEventBus().unsubscribe(this);
      }
   }

   public void onTick() {
   }

   public boolean canTickOutOfGame() {
      return false;
   }

   public void onKeyPress(int keyCode) {
   }

   public void onGameOptionsLoad() {
   }

   public void onRender(float tickDelta) {
   }

   public String getInfo() {
      return null;
   }

   public boolean isVisibleInClickGui() {
      return true;
   }

   public boolean isVisibleInHud() {
      return true;
   }

   public void toggle() {
      this.setEnabled(!this.enabled);
   }

   public boolean isTriggerOnBind() {
      return false;
   }

   public void trigger() {
      this.toggle();
   }

   protected boolean bypassAuthLock() {
      return false;
   }

   public void setEnabled(boolean enabled) {
      if (this.enabled == enabledx) {
         if (enabledx) {
            try {
               this.onEnable();
            } catch (Throwable var8) {
            }
         }
      } else if (enabledx && !this.isVisibleInClickGui()) {
         this.enabled = false;
         this.onDisable();
         this.requestConfigSave();
      } else {
         if (enabledx) {
            VieClientMod var2 = VieClientMod.getInstance();
            AuthManager var3 = var2 == null ? null : var2.getAuthManager();
            if (var3 != null && (var3.getStatus() == AuthManager.Status.OUTDATED_CLIENT || var3.isLockedDown())) {
               this.enabled = false;
               this.onDisable();
               this.requestConfigSave();
               if (mc != null && mc.field_1724 != null) {
                  mc.field_1724
                     .method_7353(
                        class_2561.method_43470(
                           "§d[VC]§c Client version is outdated! Please update from https://vieservices.top."
                        ),
                        false
                     );
               }

               return;
            }
         }

         if (CoreEntry.isAuthEnabled() && enabledx && this.category != null && this.category.isProtected() && !this.bypassAuthLock()) {
            VieClientMod var9 = VieClientMod.getInstance();
            AuthManager var11 = var9 == null ? null : var9.getAuthManager();
            String var4 = this.category.getCapabilityName();
            if (var11 == null || !var11.isCategoryEntitled(var4)) {
               if (var11 != null) {
                  var11.requestImmediateValidation();
               }

               if (var11 == null || !var11.isCategoryEntitled(var4)) {
                  this.enabled = false;
                  this.onDisable();
                  this.requestConfigSave();
                  if (mc != null && mc.field_1724 != null) {
                     long var5 = System.currentTimeMillis();
                     if (var5 - lastAuthLockNoticeAtMs >= 2500L) {
                        lastAuthLockNoticeAtMs = var5;
                        String var7 = var11 == null
                           ? "Authentication subsystem unavailable. Module locked."
                           : var11.getLockMessageForUi();
                        mc.field_1724.method_7353(class_2561.method_43470("§d[VC]§c " + var7), false);
                     }
                  }

                  return;
               }
            }
         }

         this.enabled = enabledx;
         VieClientMod var10 = VieClientMod.getInstance();
         if (var10 != null && var10.getModuleManager() != null) {
            var10.getModuleManager().onModuleStateChanged(this, enabledx);
         }

         if (enabledx) {
            this.onEnable();
         } else {
            this.onDisable();
         }

         if (this.isInGame()) {
            Sfx.play(enabledx ? Sfx.MODULE_ACTIVATE : Sfx.MODULE_DEACTIVATE);
         }

         if (var10 != null && mc != null && mc.field_1724 != null && mc.field_1687 != null) {
            ToastManager.push(this.getDisplayName(), enabledx);
         }

         this.requestConfigSave();
      }
   }

   public String getName() {
      return this.name;
   }

   public String getDisplayName() {
      return splitCamel(this.name);
   }

   public static String splitCamel(String s) {
      if (s != null && !s.isEmpty()) {
         byte[] var10001 = new byte[17];
         var10001[0] = (byte)(27);
         var10001[1] = (byte)(-63);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-13);
         var10001[4] = (byte)(85);
         var10001[5] = (byte)(-120);
         var10001[6] = (byte)(-40);
         var10001[7] = (byte)(21);
         var10001[8] = (byte)(6);
         var10001[9] = (byte)(-117);
         var10001[10] = (byte)(-52);
         var10001[11] = (byte)(-83);
         var10001[12] = (byte)(54);
         var10001[13] = (byte)(77);
         var10001[14] = (byte)(-121);
         var10001[15] = (byte)(9);
         var10001[16] = (byte)(74);
         String var8 = $$v_s(var10001, 357586016102764566L);
         byte[] var10002 = new byte[5];
         var10002[0] = (byte)(23);
         var10002[1] = (byte)(-85);
         var10002[2] = (byte)(60);
         var10002[3] = (byte)(-6);
         var10002[4] = (byte)(29);
         String var1 = s.replaceAll(var8, $$v_s(var10002, 357586016102764566L));
         var10001 = new byte[3];
         var10001[0] = (byte)(111);
         var10001[1] = (byte)(-23);
         var10001[2] = (byte)(55);
         String[] var2 = var1.split($$v_s(var10001, 357586016102764566L));
         StringBuilder var3 = new StringBuilder();

         for (String var7 : var2) {
            if (!var7.isEmpty()) {
               if (var3.length() > 0) {
                  var3.append((char)(32));
               }

               var3.append(Character.toUpperCase(var7.charAt(0))).append(var7.substring(1));
            }
         }

         return var3.toString();
      } else {
         return "";
      }
   }

   public String getDescription() {
      return Localization.getModuleDescription(this.name, this.description);
   }

   public Category getCategory() {
      return this.category;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public KeybindSetting getKeybind() {
      return this.keybind;
   }

   public List getSettings() {
      return this.settings;
   }

   public ButtonSetting getResetButton() {
      return this.resetButton;
   }

   public boolean usesEntityCache() {
      return false;
   }

   protected boolean isInGame() {
      return mc != null && mc.field_1724 != null && mc.field_1687 != null;
   }

   protected void requestConfigSave() {
      VieClientMod var1 = VieClientMod.getInstance();
      if (var1 != null && var1.getConfigManager() != null) {
         var1.getConfigManager().requestSave();
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
