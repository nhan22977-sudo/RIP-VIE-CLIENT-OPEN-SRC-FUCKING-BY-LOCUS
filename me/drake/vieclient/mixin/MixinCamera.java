package me.drake.vieclient.mixin;

import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.modules.exploit.Freecam;
import me.drake.vieclient.modules.movement.FreeLook;
import net.minecraft.class_1297;
import net.minecraft.class_1937;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(class_4184.class)
public class MixinCamera {
   @Shadow
   private boolean field_18709;
   @Unique
   private float vie$tickDelta;

   @Inject(method = "method_19321", at = @At("HEAD"))
   private void onUpdateHead(class_1937 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
      this.vie$tickDelta = tickDelta;
      VieClientMod var7 = VieClientMod.getInstance();
      if (var7 != null && var7.getModuleManager() != null) {
         Freecam var8 = (Freecam)var7.getModuleManager().getModule(Freecam.class);
         if (var8 != null && var8.isEnabled()) {
            var8.onCameraFrame();
         }

         FreeLook var9 = (FreeLook)var7.getModuleManager().getModule(FreeLook.class);
         if (var9 != null && var9.isEnabled()) {
            var9.onCameraFrame();
         }
      }
   }

   @Inject(method = "method_19321", at = @At("TAIL"))
   private void onUpdateTail(class_1937 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
      VieClientMod var7 = VieClientMod.getInstance();
      if (var7 != null && var7.getModuleManager() != null) {
         Freecam var8 = (Freecam)var7.getModuleManager().getModule(Freecam.class);
         if (var8 != null && var8.isEnabled()) {
            this.field_18709 = true;
         }
      }
   }

   @ModifyArgs(method = "method_19321", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4184;method_19327(DDD)V"))
   private void onUpdateSetPosArgs(Args args) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getModuleManager() != null) {
         Freecam var3 = (Freecam)var2.getModuleManager().getModule(Freecam.class);
         if (var3 != null && var3.isEnabled()) {
            args.set(0, var3.getInterpolatedX(this.vie$tickDelta));
            args.set(1, var3.getInterpolatedY(this.vie$tickDelta));
            args.set(2, var3.getInterpolatedZ(this.vie$tickDelta));
         }
      }
   }

   @ModifyArgs(method = "method_19321", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4184;method_19325(FF)V"))
   private void onUpdateSetRotationArgs(Args args) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getModuleManager() != null) {
         Freecam var3 = (Freecam)var2.getModuleManager().getModule(Freecam.class);
         FreeLook var4 = (FreeLook)var2.getModuleManager().getModule(FreeLook.class);
         if (var3 != null && var3.isEnabled()) {
            args.set(0, (float)var3.getInterpolatedYaw(this.vie$tickDelta));
            args.set(1, (float)var3.getInterpolatedPitch(this.vie$tickDelta));
         } else if (var4 != null && var4.isEnabled()) {
            args.set(0, var4.getRenderYaw());
            args.set(1, var4.getRenderPitch());
         }
      }
   }

   @ModifyArgs(method = "method_19321", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_4184;method_19318(F)F"))
   private void onUpdateClipToSpaceArgs(Args args) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getModuleManager() != null) {
         FreeLook var3 = (FreeLook)var2.getModuleManager().getModule(FreeLook.class);
         if (var3 != null && var3.isEnabled()) {
            args.set(0, var3.getCameraDistance());
         }
      }
   }

   @Inject(method = "method_19318", at = @At("HEAD"), cancellable = true)
   private void onClipToSpace(float desiredDistance, CallbackInfoReturnable cir) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getModuleManager() != null) {
         FreeLook var4 = (FreeLook)var3.getModuleManager().getModule(FreeLook.class);
         if (var4 != null && var4.isNoClip()) {
            cir.setReturnValue(var4.getCameraDistance());
         }
      }
   }

   @Inject(method = "method_19333", at = @At("HEAD"), cancellable = true)
   private void onIsThirdPerson(CallbackInfoReturnable cir) {
      VieClientMod var2 = VieClientMod.getInstance();
      if (var2 != null && var2.getModuleManager() != null) {
         Freecam var3 = (Freecam)var2.getModuleManager().getModule(Freecam.class);
         if (var3 != null && var3.isEnabled()) {
            cir.setReturnValue(true);
         }
      }
   }
}
