package me.drake.vieclient.modules.donut.chunkprobe;

import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public final class ResidualEvidenceIndex {
   private static final int MAX_ENTRIES = 1024;
   private static final ResidualEvidenceIndex INSTANCE = new ResidualEvidenceIndex();
   private final LinkedHashMap sources = new LinkedHashMap(16, 0.75F, true);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static ResidualEvidenceIndex get() {
      return INSTANCE;
   }

   public synchronized void publish(ResidualEvidenceIndex.Source source, long chunkKey, long updatedMs) {
      EnumMap var6 = (EnumMap)this.sources.remove(chunkKey);
      if (var6 == null) {
         var6 = new EnumMap<>(ResidualEvidenceIndex.Source.class);
      }

      var6.put(source, Math.max(0L, updatedMs));
      this.sources.put(chunkKey, var6);
      this.trimToCapacity();
   }

   public synchronized boolean hasEvidence(ResidualEvidenceIndex.Source source, long chunkKey, long nowMs) {
      this.pruneExpired(nowMs);
      EnumMap var6 = (EnumMap)this.sources.get(chunkKey);
      return var6 != null && var6.containsKey(source);
   }

   public synchronized boolean hasOther(ResidualEvidenceIndex.Source source, long chunkKey, long nowMs) {
      this.pruneExpired(nowMs);
      EnumMap var6 = (EnumMap)this.sources.get(chunkKey);
      return var6 != null && var6.keySet().stream().anyMatch(candidate -> candidate != source);
   }

   public synchronized void remove(ResidualEvidenceIndex.Source source, long chunkKey) {
      EnumMap var4 = (EnumMap)this.sources.get(chunkKey);
      if (var4 != null) {
         var4.remove(source);
         if (var4.isEmpty()) {
            this.sources.remove(chunkKey);
         }
      }
   }

   public synchronized void clear(ResidualEvidenceIndex.Source source) {
      this.sources.entrySet().removeIf(entry -> {
         ((EnumMap)entry.getValue()).remove(source);
         return ((EnumMap)entry.getValue()).isEmpty();
      });
   }

   public synchronized void clearAll() {
      this.sources.clear();
   }

   public synchronized void pruneExpired(long nowMs) {
      long var3 = Math.max(0L, nowMs);
      this.sources.entrySet().removeIf(entry -> {
         ((EnumMap)entry.getValue()).entrySet().removeIf(source -> var3 - (Long)source.getValue() > 120000L);
         return ((EnumMap)entry.getValue()).isEmpty();
      });
   }

   private void trimToCapacity() {
      while (this.sources.size() > (1024)) {
         Entry var1 = (Entry)this.sources.entrySet().iterator().next();
         this.sources.remove(var1.getKey());
      }
   }

   public enum Source {
      PERFECT,
      CAVE,
      VERTICAL_RESIDUAL,
      ENTITY_SYNC,
      BLOCK_BREAK;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ResidualEvidenceIndex.Source[] $values() {
         ResidualEvidenceIndex.Source[] var10000 = new ResidualEvidenceIndex.Source[5];
         var10000[0] = PERFECT;
         var10000[1] = CAVE;
         var10000[2] = VERTICAL_RESIDUAL;
         var10000[3] = ENTITY_SYNC;
         var10000[4] = BLOCK_BREAK;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[7];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-104);
         var10002[4] = (byte)(-43);
         var10002[5] = (byte)(48);
         var10002[6] = (byte)(-64);
         PERFECT = new ResidualEvidenceIndex.Source();
         var10002 = new byte[4];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(74);
         var10002[3] = (byte)(-101);
         CAVE = new ResidualEvidenceIndex.Source();
         var10002 = new byte[17];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-33);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(-39);
         var10002[5] = (byte)(48);
         var10002[6] = (byte)(-43);
         var10002[7] = (byte)(-63);
         var10002[8] = (byte)(9);
         var10002[9] = (byte)(-16);
         var10002[10] = (byte)(-95);
         var10002[11] = (byte)(-91);
         var10002[12] = (byte)(-127);
         var10002[13] = (byte)(-17);
         var10002[14] = (byte)(-23);
         var10002[15] = (byte)(-76);
         var10002[16] = (byte)(34);
         VERTICAL_RESIDUAL = new ResidualEvidenceIndex.Source();
         var10002 = new byte[11];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-44);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-105);
         var10002[4] = (byte)(-60);
         var10002[5] = (byte)(42);
         var10002[6] = (byte)(-53);
         var10002[7] = (byte)(-34);
         var10002[8] = (byte)(15);
         var10002[9] = (byte)(-20);
         var10002[10] = (byte)(-89);
         ENTITY_SYNC = new ResidualEvidenceIndex.Source();
         var10002 = new byte[11];
         var10002[0] = (byte)(124);
         var10002[1] = (byte)(-42);
         var10002[2] = (byte)(83);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(-37);
         var10002[5] = (byte)(44);
         var10002[6] = (byte)(-42);
         var10002[7] = (byte)(-33);
         var10002[8] = (byte)(19);
         var10002[9] = (byte)(-29);
         var10002[10] = (byte)(-81);
         BLOCK_BREAK = new ResidualEvidenceIndex.Source();
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
}
