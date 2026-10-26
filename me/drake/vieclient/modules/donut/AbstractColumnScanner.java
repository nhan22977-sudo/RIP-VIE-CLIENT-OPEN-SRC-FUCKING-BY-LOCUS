package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntPredicate;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.ChunkLoadEvent;
import me.drake.vieclient.core.event.events.GameJoinEvent;
import me.drake.vieclient.core.event.events.RenderEvent;
import me.drake.vieclient.core.event.events.TickEvent;
import me.drake.vieclient.core.i18n.Localization;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ButtonSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.modules.donut.chunkdebug.LightPayload;
import me.drake.vieclient.modules.donut.chunkprobe.RawChunkSectionDecoder;
import me.drake.vieclient.util.RenderUtil;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2540;
import net.minecraft.class_2561;
import net.minecraft.class_2672;
import net.minecraft.class_2676;
import net.minecraft.class_2680;

public abstract class AbstractColumnScanner extends Module {
   protected final ColorSetting color;
   protected final BoolSetting tracers = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Tracers",
         "Draw directional line to flagged columns",
         true
      )
   );
   protected final BoolSetting notify = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Notify",
         "Send chat notification on discovery",
         true
      )
   );
   protected final BoolSetting disconnectOnChunk = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Disconnect on Chunk",
         "Tự động ngắt kết nối khi phát hiện base",
         false
      )
   );
   protected final BoolSetting unflagOutOfRange;
   protected final ButtonSetting clearBtn;
   protected final Map flaggedColumns = new ConcurrentHashMap();
   protected static final RawChunkSectionDecoder CHUNK_DECODER = new RawChunkSectionDecoder();
   protected static final IntPredicate DEEPSLATE_PREDICATE = rawId -> {
      class_2680 var1 = (class_2680)class_2248.field_10651.method_10200(rawId);
      return var1 != null
         && (
            var1.method_27852(class_2246.field_28888)
               || var1.method_27852(class_2246.field_29031)
               || var1.method_27852(class_2246.field_38420)
               || var1.method_27852(class_2246.field_28892)
         );
   };
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   protected AbstractColumnScanner(String name, String description, int defaultColorRgb) {
      super(name, description, Category.BASE_FINDING);
      this.color = (ColorSetting)this.addSetting(
         new ColorSetting(
            "Color",
            "Render color of the beacon column",
            defaultColorRgb
         )
      );
      BoolSetting var4 = new BoolSetting(
         "Unflag Out Of Range",
         "Bỏ đánh dấu khi ra khỏi render distance",
         true
      );
      var4.setVisibleInGui(false);
      this.unflagOutOfRange = (BoolSetting)this.addSetting(var4);
      ButtonSetting var5 = new ButtonSetting(
         "Clear",
         "Clear all discovered columns",
         this::clear
      );
      var5.setVisibleInGui(false);
      this.clearBtn = (ButtonSetting)this.addSetting(var5);
   }

   public void clear() {
      this.flaggedColumns.clear();
      if (mc.field_1724 != null) {
         if (Localization.isVietnamese()) {
            mc.field_1724.method_7353(class_2561.method_43470("§d[" + this.getName() + "] §aĐã xóa toàn bộ các cột đã đánh dấu."), false);
         } else {
            mc.field_1724.method_7353(class_2561.method_43470("§d[" + this.getName() + "] §aCleared all flagged columns."), false);
         }
      }
   }

   @Override
   public String getInfo() {
      return String.valueOf(this.flaggedColumns.size());
   }

   public void flag(int chunkX, int chunkZ, int colX, int colZ, String detailVie, String detailEng) {
      long var7 = colX & 4294967295L | (long)colZ << 32;
      String var9 = Localization.isVietnamese() ? detailVie : detailEng;
      boolean var10 = this.flaggedColumns.putIfAbsent(var7, new AbstractColumnScanner.FlaggedColumn(colX, colZ, chunkX, chunkZ, var9)) == null;
      if (var10) {
         if ((Boolean)this.notify.get() && mc.field_1724 != null) {
            if (Localization.isVietnamese()) {
               mc.field_1724
                  .method_7353(
                     class_2561.method_43470(
                        String.format(
                           "§d[%s] §aPhát hiện mục tiêu tại X: %d, Z: %d (Chunk [%d, %d])",
                           this.getName(),
                           colX,
                           colZ,
                           chunkX,
                           chunkZ
                        )
                     ),
                     false
                  );
            } else {
               mc.field_1724
                  .method_7353(
                     class_2561.method_43470(
                        String.format(
                           "§d[%s] §aTarget detected at X: %d, Z: %d (Chunk [%d, %d])",
                           this.getName(),
                           colX,
                           colZ,
                           chunkX,
                           chunkZ
                        )
                     ),
                     false
                  );
            }
         }

         if ((Boolean)this.disconnectOnChunk.get() && mc.method_1562() != null) {
            this.disconnectOnChunk.set(false);
            if (Localization.isVietnamese()) {
               mc.method_1562()
                  .method_48296()
                  .method_10747(
                     class_2561.method_43470(
                        String.format(
                           "§d[%s] §cTự động ngắt kết nối: Phát hiện mục tiêu tại X: %d, Z: %d (Chunk [%d, %d])",
                           this.getName(),
                           colX,
                           colZ,
                           chunkX,
                           chunkZ
                        )
                     )
                  );
            } else {
               mc.method_1562()
                  .method_48296()
                  .method_10747(
                     class_2561.method_43470(
                        String.format(
                           "§d[%s] §cAuto-disconnect: Target detected at X: %d, Z: %d (Chunk [%d, %d])",
                           this.getName(),
                           colX,
                           colZ,
                           chunkX,
                           chunkZ
                        )
                     )
                  );
            }
         }
      }
   }

   public void flag(int chunkX, int chunkZ, int colX, int colZ, String detail) {
      this.flag(chunkX, chunkZ, colX, colZ, detail, detail);
   }

   public void flagChunk(int chunkX, int chunkZ, String detailVie, String detailEng) {
      this.flag(chunkX, chunkZ, chunkX * (16) + (8), chunkZ * (16) + (8), detailVie, detailEng);
   }

   public void flagChunk(int chunkX, int chunkZ, String detail) {
      this.flagChunk(chunkX, chunkZ, detail, detail);
   }

   @EventHandler
   protected void onGameJoin(GameJoinEvent event) {
      this.flaggedColumns.clear();
   }

   @EventHandler
   protected void onTick(TickEvent event) {
      if (event.getPhase() == TickEvent.Phase.PRE && this.isEnabled() && this.isInGame()) {
         if ((Boolean)this.unflagOutOfRange.get() && !this.flaggedColumns.isEmpty() && mc.field_1724 != null && mc.field_1690 != null) {
            int var2 = mc.field_1724.method_31476().field_9181;
            int var3 = mc.field_1724.method_31476().field_9180;
            int var4 = (Integer)mc.field_1690.method_42503().method_41753();
            this.flaggedColumns.values().removeIf(col -> Math.abs(col.chunkX - var2) > var4 || Math.abs(col.chunkZ - var3) > var4);
         }
      }
   }

   @EventHandler
   protected void onChunkLoad(ChunkLoadEvent event) {
      if (this.isEnabled() && this.isInGame()) {
         if (event.isUnloaded() && (Boolean)this.unflagOutOfRange.get()) {
            int var2 = event.getChunkPos().field_9181;
            int var3 = event.getChunkPos().field_9180;
            this.flaggedColumns.values().removeIf(col -> col.chunkX == var2 && col.chunkZ == var3);
         }
      }
   }

   @EventHandler
   public void onRender(RenderEvent event) {
      if (event.getStage() == RenderEvent.Stage.WORLD_3D && this.isInGame()) {
         if (!this.flaggedColumns.isEmpty()) {
            int var2 = (Integer)this.color.get();
            int var3 = var2;
            int var4 = var2 & (16777215) | 889192448;
            double var5 = -64.0;
            double var7 = 256.0;
            float var9 = event.getTickDelta();
            boolean var10 = (Boolean)this.tracers.get();

            for (AbstractColumnScanner.FlaggedColumn var12 : this.flaggedColumns.values()) {
               double var13 = var12.x;
               double var15 = var12.x + 1.0;
               double var17 = var12.z;
               double var19 = var12.z + 1.0;
               RenderUtil.drawFilledBox(var13, var5, var17, var15, var7, var19, var4);
               if (var10) {
                  RenderUtil.drawTracerToPos(var12.x + 0.5, 64.0, var12.z + 0.5, var3, var9);
               }
            }
         }
      }
   }

   protected static Optional decodeSections(class_2672 packet) {
      if (packet != null && packet.method_38598() != null) {
         class_2540 var1 = packet.method_38598().method_38586();
         if (var1 == null) {
            return Optional.empty();
         }

         byte[] var2 = new byte[var1.readableBytes()];
         var1.method_52952(var1.readerIndex(), var2);
         int var3 = mc.field_1687 != null ? mc.field_1687.method_32891() : -4;
         int var4 = mc.field_1687 != null ? mc.field_1687.method_32890() : 24;
         return CHUNK_DECODER.decode(packet.method_11523(), packet.method_11524(), var3, var4, var2, System.nanoTime(), DEEPSLATE_PREDICATE);
      } else {
         return Optional.empty();
      }
   }

   protected static LightPayload getLightPayload(class_2672 packet) {
      return packet != null && packet.method_38599() != null ? LightPayload.copyOf(packet.method_38599()) : null;
   }

   protected static LightPayload getLightPayload(class_2676 packet) {
      return packet != null && packet.method_38600() != null ? LightPayload.copyOf(packet.method_38600()) : null;
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

   public static final class FlaggedColumn {
      public final int x;
      public final int z;
      public final int chunkX;
      public final int chunkZ;
      public final String detail;
      public final long timestamp;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public FlaggedColumn(int x, int z, int chunkX, int chunkZ, String detail) {
         this.x = xx;
         this.z = zx;
         this.chunkX = chunkXx;
         this.chunkZ = chunkZx;
         this.detail = detailx;
         this.timestamp = System.currentTimeMillis();
      }
   }
}
