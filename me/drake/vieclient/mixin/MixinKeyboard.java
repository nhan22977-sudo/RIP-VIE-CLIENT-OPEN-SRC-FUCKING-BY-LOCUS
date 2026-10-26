package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.macro.ChatMacroSystem;
import me.drake.vieclient.gui.nvg.ui.EmoteWheel;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.modules.client.HUD;
import me.drake.vieclient.modules.donut.AutoMineBridge;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.render.KeyboardSFX;
import net.minecraft.class_11908;
import net.minecraft.class_309;
import net.minecraft.class_310;
import net.minecraft.class_408;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_309.class)
public class MixinKeyboard {
   @Inject(method = "method_1466", at = @At("HEAD"), cancellable = true)
   private void onKeyPress(long window, int action, class_11908 input, CallbackInfo ci) {
      class_310 var6 = class_310.method_1551();
      if (var6 != null) {
         if (action == 1) {
            KeyboardSFX.onKey();
         }

         if (action == 1 && EmoteWheel.isOpen() && EmoteWheel.handleKey(input.comp_4795())) {
            ci.cancel();
         } else if (action == 1 && AutoMineBridge.isSelecting() && AutoMineBridge.handleKey(input.comp_4795())) {
            ci.cancel();
         } else {
            HUD var7 = HUD.instance();
            if (var7 != null && var7.isEditorMode() && action == 1) {
               if (input.comp_4795() == 261) {
                  if (var7.deleteSelectedWidget()) {
                     ci.cancel();
                     return;
                  }
               } else if (input.comp_4795() == 256 && var7.isArmorMenuOpen()) {
                  var7.closeArmorMenu();
                  ci.cancel();
                  return;
               }
            }

            VieClientMod var8 = VieClientMod.getInstance();
            if (var8 != null && var8.getModuleManager() != null) {
               Freecam var9 = (Freecam)var8.getModuleManager().getModule(Freecam.class);
               if (var9 != null && var9.isEnabled() && var9.handleKeyboardInput(input, action)) {
                  ci.cancel();
               } else {
                  if (action == 1) {
                     if (var6.field_1755 == null && var6.field_1690 != null && var6.field_1690.field_1890.method_1417(input)) {
                        var6.method_1507(new class_408("", false));
                        ci.cancel();
                        return;
                     }

                     if (var6.field_1755 == null) {
                        ClientSettings var10 = ClientSettings.get();
                        int var11 = var10 != null ? var10.getClickGuiKey() : 344;
                        if (var11 > 0 && input.comp_4795() == var11 || var11 != 260 && input.comp_4795() == 260) {
                           var8.openClickGuiFromKeyPress(var6);
                           ci.cancel();
                           return;
                        }
                     }
                  }

                  if (var6.field_1755 == null) {
                     if (action == 1) {
                        var8.getModuleManager().onKeyPress(input.comp_4795());
                        ChatMacroSystem.INSTANCE.onKey(input.comp_4795());
                     }
                  }
               }
            }
         }
      }
   }
}
