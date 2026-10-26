package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import me.drake.vieclient.core.event.events.ReceivePacketEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_1923;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2561;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2622;
import net.minecraft.class_2818;
import net.minecraft.class_2960;
import net.minecraft.class_638;
import net.minecraft.class_7923;

public final class BlockEntitiesDebug extends Module {
   private static final int MIN_Y_EXCLUSIVE = -10;
   private static final int WHITE = -1;
   private final BoolSetting tracers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tracers",
         "Draw tracer lines to discoveries",
         true
      )
   );
   private final IntSetting alpha = (IntSetting)this.addSetting(
      new IntSetting(
         "Alpha",
         "Fill opacity",
         96,
         0,
         255
      )
   );
   private final Set discoveries = new LinkedHashSet();
   private class_638 observedWorld;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BlockEntitiesDebug() {
      super(
         "BlockEntitiesDebug",
         "Finds block entities below Y=-10",
         Category.SMP_UTILITIES
      );
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.clearState();
      this.observedWorld = mc.field_1687;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.clearState();
      this.observedWorld = null;
   }

   @EventHandler
   public void onReceivePacket(ReceivePacketEvent event) {
      if (event.getPacket() instanceof class_2622 var2) {
         mc.execute(() -> {
            if (this.isEnabled() && this.isInGame()) {
               String var2x = var2.method_11290() != null ? var2.method_11290().toString() : "";
               this.recordDiscovery(var2.method_11293(), typeId(var2.method_11291()), var2x);
            }
         });
      }
   }

   @EventHandler
   public void onChunkLoad(ChunkLoadEvent event) {
      if (this.isEnabled() && this.isInGame()) {
         if (event.isUnloaded()) {
            this.removeChunk(event.getChunkPos());
         } else {
            class_2818 var2 = event.getChunk();
            if (var2 != null) {
               for (class_2586 var4 : var2.method_12214().values()) {
                  String var5 = var4.method_38242(mc.field_1687.method_30349()).toString();
                  this.recordDiscovery(var4.method_11016(), typeId(var4.method_11017()), var5);
               }
            }
         }
      }
   }

   @EventHandler
   public void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE && this.isEnabled()) {
         if (mc.field_1687 != this.observedWorld) {
            this.clearState();
            this.observedWorld = mc.field_1687;
         }
      }
   }

   @EventHandler(priority = -100)
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && this.isEnabled() && this.isInGame()) {
         for (class_2338 var3 : this.discoveries) {
            RenderUtil.drawFilledBox(new class_238(var3), whiteBoxColor((Integer)this.alpha.get()));
            if ((Boolean)this.tracers.get()) {
               RenderUtil.drawTracerToPos(var3.method_10263() + 0.5, var3.method_10264() + 0.5, var3.method_10260() + 0.5, tracerColor(), event.getTickDelta());
            }
         }
      }
   }

   private void recordDiscovery(class_2338 position, String typeId, String nbt) {
      if (shouldTrack(position.method_10264(), typeId, true)) {
         class_2338 var4 = position.method_10062();
         boolean var5 = !this.discoveries.add(var4);
         if (shouldNotify(var5, true) && mc.field_1724 != null) {
            String var6 = "§d[VC] §b" + typeId + " §7at §f" + var4.method_10263() + ", " + var4.method_10264() + ", " + var4.method_10260();
            if (!nbt.isEmpty()) {
               var6 = var6 + " §8nbt=§7" + nbt;
            }

            mc.field_1724.method_7353(class_2561.method_43470(var6), false);
         }
      }
   }

   private void removeChunk(class_1923 chunk) {
      this.discoveries.removeIf(position -> isInChunk(position, chunk.field_9181, chunk.field_9180));
   }

   private void clearState() {
      this.discoveries.clear();
   }

   private static String typeId(class_2591 type) {
      class_2960 var1 = class_7923.field_41181.method_10221(type);
      String var2;
      if (var1 == null) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(70);
         var10000[1] = (byte)(-12);
         var10000[2] = (byte)(119);
         var10000[3] = (byte)(-80);
         var10000[4] = (byte)(-81);
         var10000[5] = (byte)(90);
         var10000[6] = (byte)(87);
         var2 = $$v_s(var10000, -2217192164260851690L);
      } else {
         var2 = var1.toString();
      }

      return var2;
   }

   private static int withAlpha(int color, int alpha) {
      return Math.max(0, Math.min(255, alphax)) << (24) | color & (16777215);
   }

   static int whiteBoxColor(int alpha) {
      return withAlpha(-1, alphax);
   }

   static int tracerColor() {
      return -1;
   }

   static boolean shouldTrack(int y, String typeId, boolean detectSculk) {
      if (y < (-10)) {
         if (detectSculk) {
            return true;
         }

         byte[] var10001 = new byte[5];
         var10001[0] = (byte)(64);
         var10001[1] = (byte)(-7);
         var10001[2] = (byte)(105);
         var10001[3] = (byte)(-78);
         var10001[4] = (byte)(-85);
         if (!typeId.contains($$v_s(var10001, -2217192164260851690L))) {
            return true;
         }
      }

      return false;
   }

   static boolean shouldNotify(boolean alreadyKnown, boolean deduplicate) {
      return !alreadyKnown || !deduplicate;
   }

   static boolean isInChunk(class_2338 position, int chunkX, int chunkZ) {
      return position.method_10263() >> (4) == chunkX && position.method_10260() >> (4) == chunkZ;
   }

   @Override
   public String getInfo() {
      return this.discoveries.size() + " found";
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
