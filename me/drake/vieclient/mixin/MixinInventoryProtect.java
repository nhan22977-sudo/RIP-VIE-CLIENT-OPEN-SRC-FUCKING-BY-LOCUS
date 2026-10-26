package me.drake.vieclient.mixin;

import me.drake.vieclient.modules.client.InventoryProtect;
import me.drake.vieclient.modules.render.ItemReplacement;
import net.minecraft.class_1735;
import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_465;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_465.class)
public abstract class MixinInventoryProtect {
   @Inject(method = "method_2385", at = @At("HEAD"), cancellable = true)
   private void onDrawSlot(class_332 context, class_1735 slot, int mouseX, int mouseY, CallbackInfo ci) {
      if (slot != null && InventoryProtect.isActive() && InventoryProtect.shouldMaskContainers()) {
         class_1799 var6 = slot.method_7677();
         class_1799 var7 = InventoryProtect.get().getMaskedStack(slot, var6);
         if (var7 != var6) {
            context.method_51427(var7, slot.field_7873, slot.field_7872);
            context.method_51431(class_310.method_1551().field_1772, var7, slot.field_7873, slot.field_7872);
            ci.cancel();
         }
      }
   }

   @Inject(method = "method_2380", at = @At("HEAD"), cancellable = true)
   private void onDrawMouseoverTooltip(class_332 context, int x, int y, CallbackInfo ci) {
      if (InventoryProtect.shouldHideTooltips()) {
         class_1735 var5 = ((HandledScreenAccessor)this).getFocusedSlot();
         if (var5 != null) {
            class_1799 var6 = var5.method_7677();
            if (var6 != null && !var6.method_7960()) {
               ci.cancel();
            }
         }
      }
   }

   @Inject(method = "method_71083", at = @At("HEAD"), cancellable = true)
   private void onRenderCursorStack(class_332 context, int x, int y, CallbackInfo ci) {
      if (InventoryProtect.isActive() && InventoryProtect.shouldMaskContainers()) {
         ci.cancel();
      }
   }

   @ModifyVariable(method = "method_2380", at = @At(value = "STORE", ordinal = 0))
   private class_1799 onModifyMouseoverTooltipStack(class_1799 stack) {
      return ItemReplacement.getReplaced(stack);
   }
}
