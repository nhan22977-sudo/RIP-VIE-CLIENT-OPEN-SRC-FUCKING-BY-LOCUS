package me.drake.vieclient.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import me.drake.vieclient.modules.donut.profiler.network.RawFrameBridge;
import net.minecraft.class_2532;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_2532.class)
public class MixinPacketInflater {
   @Unique
   private int vie$compressedBytes;
   @Unique
   private int vie$outputStart;

   @Inject(method = "decode", at = @At("HEAD"))
   private void vie$beforeInflate(ChannelHandlerContext context, ByteBuf input, List output, CallbackInfo ci) {
      this.vie$compressedBytes = input.readableBytes();
      this.vie$outputStart = output.size();
   }

   @Inject(method = "decode", at = @At("RETURN"))
   private void vie$afterInflate(ChannelHandlerContext context, ByteBuf input, List output, CallbackInfo ci) {
      if (output.size() == this.vie$outputStart + 1 && output.get(this.vie$outputStart) instanceof ByteBuf var5) {
         RawFrameBridge.get().onInflated(this.vie$compressedBytes, var5.readableBytes(), System.nanoTime());
      } else {
         if (output.size() > this.vie$outputStart + 1) {
            RawFrameBridge.get().markAmbiguousBundle();
         }
      }
   }
}
