package me.drake.vieclient.mixin;

import io.netty.channel.ChannelHandlerContext;
import me.drake.vieclient.VieClientMod;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.SendPacketEvent;
import net.minecraft.class_2535;
import net.minecraft.class_2596;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_2535.class)
public class MixinClientConnection {
   @Inject(method = "method_10743(Lnet/minecraft/class_2596;)V", at = @At("HEAD"), cancellable = true)
   private void onSend(class_2596 packet, CallbackInfo ci) {
      VieClientMod var3 = VieClientMod.getInstance();
      if (var3 != null) {
         SendPacketEvent var4 = new SendPacketEvent(packet);
         var3.getEventBus().post(var4);
         if (var4.isCancelled()) {
            ci.cancel();
         }
      }
   }

   @Inject(method = "method_10770(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/class_2596;)V", at = @At("HEAD"), cancellable = true)
   private void onChannelRead0(ChannelHandlerContext ctx, class_2596 packet, CallbackInfo ci) {
      try {
         VieClientMod var4 = VieClientMod.getInstance();
         if (var4 == null) {
            return;
         }

         ReceivePacketEvent var5 = new ReceivePacketEvent(packet);
         var4.getEventBus().post(var5);
         if (var5.isCancelled()) {
            ci.cancel();
         }
      } catch (Exception var6) {
      }
   }
}
