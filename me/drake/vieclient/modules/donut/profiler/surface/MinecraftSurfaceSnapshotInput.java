package me.drake.vieclient.modules.donut.profiler.surface;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import me.drake.vieclient.modules.donut.chunkprobe.ChunkKey;
import me.drake.vieclient.util.EntityCache;
import net.minecraft.class_1297;
import net.minecraft.class_1304;
import net.minecraft.class_1308;
import net.minecraft.class_1309;
import net.minecraft.class_1593;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_7923;
import net.minecraft.class_2902.class_2903;

public final class MinecraftSurfaceSnapshotInput implements SurfaceProfilerSampler.SnapshotInput {
   private final class_310 client;
   private final class_638 world;
   private final long clientTick;
   private final long capturedNanos;
   private final List chunks = new ArrayList();
   private final List entities;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MinecraftSurfaceSnapshotInput(class_310 client) {
      this.client = clientx;
      this.world = clientx.field_1687;
      this.clientTick = clientx.field_1724 == null ? 0L : clientx.field_1724.field_6012;
      this.capturedNanos = System.nanoTime();
      this.entities = EntityCache.getEntities();
      if (this.world != null && clientx.field_1724 != null) {
         int var2 = clientx.field_1724.method_31476().field_9181;
         int var3 = clientx.field_1724.method_31476().field_9180;

         for (int var4 = -2; var4 <= (2); var4++) {
            for (int var5 = -2; var5 <= (2); var5++) {
               class_2818 var6 = this.world.method_2935().method_21730(var2 + var4, var3 + var5);
               if (var6 != null) {
                  double var7 = Math.max(Math.abs(var4), Math.abs(var5)) <= 1 ? 1.0 : 0.0;
                  this.chunks
                     .add(
                        new MinecraftSurfaceSnapshotInput.ChunkView(
                           var6,
                           new SurfaceProfilerSampler.ChunkInput(
                              ChunkKey.pack(var2 + var4, var3 + var5), this.context(var6, 8, 8), 1.0, var7
                           )
                        )
                     );
               }
            }
         }
      }
   }

   @Override
   public long clientTick() {
      return this.clientTick;
   }

   @Override
   public long capturedNanos() {
      return this.capturedNanos;
   }

   @Override
   public int chunkCount() {
      return this.chunks.size();
   }

   @Override
   public SurfaceProfilerSampler.ChunkInput chunk(int index) {
      return ((MinecraftSurfaceSnapshotInput.ChunkView)this.chunks.get(index)).input();
   }

   @Override
   public int surfaceY(SurfaceProfilerSampler.ChunkInput input, int localX, int localZ) {
      MinecraftSurfaceSnapshotInput.ChunkView var4 = this.find(input.chunkKey());
      return var4 == null ? -1 : var4.chunk().method_12005(class_2903.field_13202, localX, localZ) - 1;
   }

   @Override
   public SurfaceProfilerSampler.BlockFeature blockAt(SurfaceProfilerSampler.ChunkInput input, int localX, int y, int localZ) {
      if (y < 0) {
         return null;
      }

      MinecraftSurfaceSnapshotInput.ChunkView var5 = this.find(input.chunkKey());
      if (var5 == null) {
         return null;
      }

      class_2680 var6 = var5.chunk()
         .method_8320(new class_2338(var5.chunk().method_12004().method_8326() + localX, y, var5.chunk().method_12004().method_8328() + localZ));
      String var7 = class_7923.field_41175.method_10221(var6.method_26204()).method_12832();
      String var8 = var6.toString();
      if (isCrop(var7)) {
         boolean var12 = switch (var7) {
            case "wheat", "carrots", "potatoes" -> var8.contains("age=7");
            case "beetroots", "nether_wart" -> var8.contains("age=3");
            case "cocoa" -> var8.contains(
               "age=2"
            );
            default -> false;
         };
         return new SurfaceProfilerSampler.BlockFeature(0, var12);
      } else {
         if (!var7.contains("copper")) {
            return !var7.equals("dirt")
                  && !var7.equals("grass_block")
                  && !var7.equals("moss_block")
                  && !var7.equals("mycelium")
                  && !var7.equals("podzol")
               ? new SurfaceProfilerSampler.BlockFeature(-1, false)
               : new SurfaceProfilerSampler.BlockFeature(2, !var7.equals("dirt"));
         }

         boolean var9 = var7.contains("weathered")
            || var7.contains("oxidized");
         return new SurfaceProfilerSampler.BlockFeature(1, var9);
      }
   }

   @Override
   public int entityCount() {
      return this.entities.size();
   }

   @Override
   public SurfaceProfilerSampler.EntityInput entity(int index) {
      class_1297 var2 = ((EntityCache.CachedEntity)this.entities.get(index)).entity();
      if (var2 != null && !(var2.method_23318() < 0.0)) {
         int var3 = 0;
         int var4 = 0;
         int var5 = 0;
         if (var2 instanceof class_1309 var6) {
            class_1304[] var10000 = new class_1304[4];
            var10000[0] = class_1304.field_6169;
            var10000[1] = class_1304.field_6174;
            var10000[2] = class_1304.field_6172;
            var10000[3] = class_1304.field_6166;

            for (class_1304 var10 : var10000) {
               class_1799 var11 = var6.method_6118(var10);
               if (!var11.method_7960()) {
                  var3++;
                  if (var11.method_7942()) {
                     var4++;
                  }
               }
            }

            var5 = var6.method_6026().size();
         }

         long var14 = ChunkKey.pack((int)Math.floor(var2.method_23317()) >> (4), (int)Math.floor(var2.method_23321()) >> (4));
         String var15 = this.contextAt(var2.method_24515());
         boolean var16 = var2 instanceof class_1593;
         if (var16 && this.client.field_1724 != null) {
            double var17 = var2.method_23317() - this.client.field_1724.method_23317();
            double var12 = var2.method_23321() - this.client.field_1724.method_23321();
            var16 = var17 * var17 + var12 * var12 >= 4096.0;
         }

         return new SurfaceProfilerSampler.EntityInput(
            var2.method_5628(),
            var14,
            class_7923.field_41177.method_10221(var2.method_5864()).toString(),
            var2.method_5864().method_5891().method_6133(),
            var15,
            (int)Math.floor(var2.method_23318()),
            var3,
            var4,
            var5,
            var2.field_6012,
            var2.method_16914(),
            var2 instanceof class_1308 var18 && var18.method_5947(),
            var16,
            var2.method_23317(),
            var2.method_23321(),
            var2.method_18798().field_1352,
            var2.method_18798().field_1350
         );
      } else {
         return null;
      }
   }

   private MinecraftSurfaceSnapshotInput.ChunkView find(long key) {
      for (MinecraftSurfaceSnapshotInput.ChunkView var4 : this.chunks) {
         if (var4.input().chunkKey() == key) {
            return var4;
         }
      }

      return null;
   }

   private String context(class_2818 chunk, int localX, int localZ) {
      int var4 = Math.max(0, chunk.method_12005(class_2903.field_13202, localX, localZ) - 1);
      return this.contextAt(new class_2338(chunk.method_12004().method_8326() + localX, var4, chunk.method_12004().method_8328() + localZ));
   }

   private String contextAt(class_2338 pos) {
      String var9;
      if (this.world == null) {
         byte[] var10000 = new byte[7];
         var10000[0] = (byte)(80);
         var10000[1] = (byte)(-12);
         var10000[2] = (byte)(119);
         var10000[3] = (byte)(-80);
         var10000[4] = (byte)(-44);
         var10000[5] = (byte)(39);
         var10000[6] = (byte)(94);
         var9 = $$v_s(var10000, 2320354373516120064L);
      } else {
         var9 = this.world.method_23753(pos).toString();
      }

      String var2 = var9;
      if (this.world != null && this.world.method_8419()) {
         byte[] var12 = new byte[4];
         var12[0] = (byte)(87);
         var12[1] = (byte)(-5);
         var12[2] = (byte)(117);
         var12[3] = (byte)(-80);
         var9 = $$v_s(var12, 2320354373516120064L);
      } else {
         byte[] var10 = new byte[5];
         var10[0] = (byte)(70);
         var10[1] = (byte)(-10);
         var10[2] = (byte)(121);
         var10[3] = (byte)(-65);
         var10[4] = (byte)(-55);
         var9 = $$v_s(var10, 2320354373516120064L);
      }

      String var3 = var9;
      long var4 = this.world == null ? 0L : this.world.method_8532() % 24000L;
      if (var4 >= 13000L && var4 <= 23000L) {
         byte[] var15 = new byte[5];
         var15[0] = (byte)(75);
         var15[1] = (byte)(-13);
         var15[2] = (byte)(123);
         var15[3] = (byte)(-74);
         var15[4] = (byte)(-49);
         var9 = $$v_s(var15, 2320354373516120064L);
      } else {
         byte[] var13 = new byte[3];
         var13[0] = (byte)(65);
         var13[1] = (byte)(-5);
         var13[2] = (byte)(101);
         var9 = $$v_s(var13, 2320354373516120064L);
      }

      String var6 = var9;
      if (this.world == null) {
         byte[] var16 = new byte[7];
         var16[0] = (byte)(80);
         var16[1] = (byte)(-12);
         var16[2] = (byte)(119);
         var16[3] = (byte)(-80);
         var16[4] = (byte)(-44);
         var16[5] = (byte)(39);
         var16[6] = (byte)(94);
         var9 = $$v_s(var16, 2320354373516120064L);
      } else {
         var9 = this.world.method_8407().method_5460();
      }

      String var7 = var9;
      int var8 = this.world == null ? -1 : (int)(this.world.method_8532() / 24000L & 7L);
      return var2 + "|" + var3 + "|" + var6 + "|" + var7 + "|moon-" + var8;
   }

   private static boolean isCrop(String path) {
      byte[] var10001 = new byte[5];
      var10001[0] = (byte)(82);
      var10001[1] = (byte)(-14);
      var10001[2] = (byte)(121);
      var10001[3] = (byte)(-65);
      var10001[4] = (byte)(-49);
      if (!path.equals($$v_s(var10001, 2320354373516120064L))) {
         var10001 = new byte[7];
         var10001[0] = (byte)(70);
         var10001[1] = (byte)(-5);
         var10001[2] = (byte)(110);
         var10001[3] = (byte)(-84);
         var10001[4] = (byte)(-44);
         var10001[5] = (byte)(36);
         var10001[6] = (byte)(67);
         if (!path.equals($$v_s(var10001, 2320354373516120064L))) {
            var10001 = new byte[8];
            var10001[0] = (byte)(85);
            var10001[1] = (byte)(-11);
            var10001[2] = (byte)(104);
            var10001[3] = (byte)(-65);
            var10001[4] = (byte)(-49);
            var10001[5] = (byte)(63);
            var10001[6] = (byte)(85);
            var10001[7] = (byte)(123);
            if (!path.equals($$v_s(var10001, 2320354373516120064L))) {
               var10001 = new byte[9];
               var10001[0] = (byte)(71);
               var10001[1] = (byte)(-1);
               var10001[2] = (byte)(121);
               var10001[3] = (byte)(-86);
               var10001[4] = (byte)(-55);
               var10001[5] = (byte)(63);
               var10001[6] = (byte)(95);
               var10001[7] = (byte)(124);
               var10001[8] = (byte)(62);
               if (!path.equals($$v_s(var10001, 2320354373516120064L))) {
                  var10001 = new byte[11];
                  var10001[0] = (byte)(75);
                  var10001[1] = (byte)(-1);
                  var10001[2] = (byte)(104);
                  var10001[3] = (byte)(-74);
                  var10001[4] = (byte)(-34);
                  var10001[5] = (byte)(34);
                  var10001[6] = (byte)(111);
                  var10001[7] = (byte)(127);
                  var10001[8] = (byte)(44);
                  var10001[9] = (byte)(-48);
                  var10001[10] = (byte)(-112);
                  if (!path.equals($$v_s(var10001, 2320354373516120064L))) {
                     var10001 = new byte[5];
                     var10001[0] = (byte)(70);
                     var10001[1] = (byte)(-11);
                     var10001[2] = (byte)(127);
                     var10001[3] = (byte)(-79);
                     var10001[4] = (byte)(-38);
                     if (!path.equals($$v_s(var10001, 2320354373516120064L))) {
                        return false;
                     }
                  }
               }
            }
         }
      }

      return true;
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

   private record ChunkView(class_2818 chunk, SurfaceProfilerSampler.ChunkInput input) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }
}
