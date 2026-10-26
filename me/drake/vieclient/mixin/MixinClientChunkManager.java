package me.drake.vieclient.mixin;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import net.minecraft.class_1923;
import net.minecraft.class_2540;
import net.minecraft.class_2818;
import net.minecraft.class_631;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(class_631.class)
public class MixinClientChunkManager {
   @Inject(method = "method_16020", at = @At("RETURN"))
   private void onChunkLoad(int x, int z, class_2540 buf, Map heightmaps, Consumer consumer, CallbackInfoReturnable cir) {
      VieClientMod var7 = VieClientMod.getInstance();
      if (var7 != null && var7.getEventBus() != null) {
         ArrayList var8 = new ArrayList();
         if (cir.getReturnValue() != null) {
            class_2818 var9 = (class_2818)cir.getReturnValue();
            var9.method_12214().forEach((pos, be) -> var8.add(pos.method_10062()));
         }

         var7.getEventBus().post(new ChunkLoadEvent(new class_1923(x, z), true, var8, (class_2818)cir.getReturnValue()));
      }
   }

   @Inject(method = "method_2859", at = @At("HEAD"))
   private void onChunkUnload(class_1923 pos, CallbackInfo ci) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null && var3.getEventBus() != null) {
         var3.getEventBus().post(new ChunkLoadEvent(pos, false));
      }
   }
}
