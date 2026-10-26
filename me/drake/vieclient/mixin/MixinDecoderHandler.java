package me.drake.vieclient.mixin;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.List;
import me.drake.vieclient.modules.donut.profiler.DonutProfilerService;
import me.drake.vieclient.modules.donut.profiler.network.RawFrameBridge;
import net.minecraft.class_2543;
import net.minecraft.class_2672;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(class_2543.class)
public class MixinDecoderHandler {
   @Unique
   private int vie$decoderBytes;
   @Unique
   private int vie$outputStart;

   @Inject(method = "decode", at = @At("HEAD"))
   private void vie$beforeDecode(ChannelHandlerContext context, ByteBuf input, List output, CallbackInfo ci) {
      this.vie$decoderBytes = input.readableBytes();
      this.vie$outputStart = output.size();
   }

   @Inject(method = "decode", at = @At("RETURN"))
   private void vie$afterDecode(ChannelHandlerContext context, ByteBuf input, List output, CallbackInfo ci) {
      int var5 = output.size() - this.vie$outputStart;
      if (var5 != 1) {
         if (var5 > 1) {
            RawFrameBridge.get().markAmbiguousBundle();
         }
      } else if (output.get(this.vie$outputStart) instanceof class_2672 var6) {
         int var9 = var6.method_38598().method_38586().readableBytes();
         String var8 = "payload-" + sizeBand(var9);
         RawFrameBridge.get()
            .onDecodedChunk(var6.getClass().getSimpleName(), var6.method_11523(), var6.method_11524(), var8, System.nanoTime(), this.vie$decoderBytes)
            .ifPresent(DonutProfilerService.get()::publish);
      } else {
         RawFrameBridge.get().onDecodedNonChunk();
      }
   }

   @Unique
   private static int sizeBand(int bytes) {
      return bytes <= 0 ? 0 : Integer.highestOneBit(bytes);
   }
}
