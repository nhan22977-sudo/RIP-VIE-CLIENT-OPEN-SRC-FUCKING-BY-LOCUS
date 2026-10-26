package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.macro.ChatMacroSystem;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.gui.nvg.ui.EmoteWheel;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.modules.donut.AutoMineBridge;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.movement.FreeLook;
import me.drake.vieclient.modules.render.KeyboardSFX;
import net.minecraft.class_11909;
import net.minecraft.class_11910;
import net.minecraft.class_310;
import net.minecraft.class_312;
import net.minecraft.class_408;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_312.class)
public abstract class MixinMouse {
   private HUD getHUD() {
      VieClientMod var1 = VieClientMod.getInstance();
      if (var1 != null && var1.getModuleManager() != null) {
         for (Module var3 : var1.getModuleManager().getModules()) {
            if (var3 instanceof HUD var4) {
               return var4;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @Inject(method = "method_1601", at = @At("HEAD"), cancellable = true)
   private void onMouseButtonInput(long window, class_11910 mouseInput, int action, CallbackInfo ci) {
      class_310 var6 = class_310.method_1551();
      if (var6 != null) {
         if (action == 1 && (mouseInput.comp_4801() == 0 || mouseInput.comp_4801() == 1)) {
            KeyboardSFX.onMouseClick();
         }

         if (action == 1 && EmoteWheel.isOpen() && EmoteWheel.handleMouseButton(mouseInput.comp_4801())) {
            ci.cancel();
         } else if (AutoMineBridge.isSelecting() && AutoMineBridge.handleMouseButton(mouseInput.comp_4801(), action)) {
            ci.cancel();
         } else {
            HUD var7 = this.getHUD();
            if (var7 != null) {
               boolean var8 = var6.field_1755 instanceof class_408;
               if (var8 && !var7.isEditorMode()) {
                  var7.enterEditorMode();
               }

               if (!var8 && var7.isEditorMode() && !var7.isDragging()) {
                  var7.exitEditorMode();
               }
            }

            VieClientMod var14 = VieClientMod.getInstance();
            if (var14 != null && var14.getModuleManager() != null) {
               Freecam var9 = (Freecam)var14.getModuleManager().getModule(Freecam.class);
               class_11909 var10 = new class_11909(0.0, 0.0, mouseInput);
               if (var9 != null && var9.isEnabled() && var9.handleMouseButtonInput(var10, action)) {
                  ci.cancel();
               } else if (var6.field_1755 == null) {
                  if (action == 1) {
                     int var11 = 1000 + mouseInput.comp_4801();
                     ClientSettings var12 = ClientSettings.get();
                     int var13 = var12 != null ? var12.getClickGuiKey() : 344;
                     if (var13 >= 1000 && var11 == var13) {
                        var14.openClickGuiFromKeyPress(var6);
                        ci.cancel();
                        return;
                     }

                     var14.getModuleManager().onKeyPress(var11);
                     ChatMacroSystem.INSTANCE.onKey(var11);
                  }
               }
            }
         }
      }
   }

   @Inject(method = "method_1601", at = @At("HEAD"), cancellable = true)
   private void onMouseButtonEditor(long window, class_11910 mouseInput, int action, CallbackInfo ci) {
      class_310 var6 = class_310.method_1551();
      if (var6 != null && !var6.field_1729.method_1613()) {
         if (!ci.isCancelled()) {
            if (!AutoMineBridge.isSelecting()) {
               boolean var7 = var6.field_1755 instanceof class_408 || var6.field_1755 == null;
               if (var7) {
                  if (!EmoteWheel.isOpen()) {
                     HUD var8 = this.getHUD();
                     if (var8 != null) {
                        if (!var8.isEditorMode()) {
                           var8.enterEditorMode();
                        }

                        double var9 = var6.method_22683().method_4495();
                        double var11 = var6.field_1729.method_1603();
                        double var13 = var6.field_1729.method_1604();
                        if (action == 1) {
                           if (mouseInput.comp_4801() == 0) {
                              var8.onMousePressed(var11 / var9, var13 / var9);
                           } else if (mouseInput.comp_4801() == 1) {
                              var8.onRightMousePressed(var11 / var9, var13 / var9, System.currentTimeMillis());
                           }
                        }

                        if (action == 0) {
                           var8.onMouseReleased();
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(method = "method_1600", at = @At("HEAD"))
   private void onCursorPos(long window, double x, double y, CallbackInfo ci) {
      class_310 var8 = class_310.method_1551();
      if (var8 != null) {
         if (!var8.field_1729.method_1613()) {
            if (EmoteWheel.isOpen()) {
               EmoteWheel.updateMouse();
            } else {
               boolean var9 = var8.field_1755 instanceof class_408 || var8.field_1755 == null;
               if (var9) {
                  HUD var10 = this.getHUD();
                  if (var10 != null) {
                     if (var10.isEditorMode()) {
                        double var11 = var8.method_22683().method_4495();
                        var10.onMouseDragged(x / var11, y / var11);
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(method = "method_1598", at = @At("HEAD"), cancellable = true)
   private void onMouseScrollInput(long window, double horizontal, double vertical, CallbackInfo ci) {
      if (EmoteWheel.isOpen() && EmoteWheel.handleScroll(vertical)) {
         ci.cancel();
      } else if (AutoMineBridge.isSelecting() && AutoMineBridge.handleMouseScroll(vertical)) {
         ci.cancel();
      } else {
         VieClientMod var8 = VieClientMod.getInstance();
         if (var8 != null && var8.getModuleManager() != null) {
            Freecam var9 = (Freecam)var8.getModuleManager().getModule(Freecam.class);
            if (var9 != null && var9.isEnabled() && var9.handleMouseScroll(vertical)) {
               ci.cancel();
            } else {
               FreeLook var10 = (FreeLook)var8.getModuleManager().getModule(FreeLook.class);
               if (var10 != null && var10.isEnabled() && var10.handleMouseScroll(vertical)) {
                  ci.cancel();
               }
            }
         }
      }
   }
}
