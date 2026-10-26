package me.drake.vieclient.modules.donut.chunkdebug;

import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public record MachineEvidence(MachineEvidence.Cluster strongestCluster) {
   public static final int CELL_SIZE = 8;
   private static final int STRONG_COMPONENTS = 12;
   private static final int STRONG_TYPES = 3;
   private static final int VERY_STRONG_COMPONENTS = 24;
   private static final int VERY_STRONG_TYPES = 4;
   public static final MachineEvidence NONE = new MachineEvidence(MachineEvidence.Cluster.NONE);
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MachineEvidence(MachineEvidence.Cluster strongestCluster) {
      strongestCluster = strongestCluster == null ? MachineEvidence.Cluster.NONE : strongestCluster;
      this.strongestCluster = strongestCluster;
   }

   public static MachineEvidence.Builder builder() {
      return new MachineEvidence.Builder();
   }

   public boolean hasStrongCluster() {
      return this.strongestCluster.isStrong();
   }

   public boolean hasVeryStrongCluster() {
      return this.strongestCluster.isVeryStrong();
   }

   public static final class Builder {
      private final Map cells = new HashMap();
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public void add(int x, int y, int z, MachineEvidence.ComponentType type) {
         if (type != null) {
            MachineEvidence.CellKey var5 = new MachineEvidence.CellKey(
               Math.floorDiv(x, 8), Math.floorDiv(y, 8), Math.floorDiv(z, 8)
            );
            this.cells.computeIfAbsent(var5, ignored -> new MachineEvidence.CellStats()).add(type);
         }
      }

      public MachineEvidence build() {
         MachineEvidence.Cluster var1 = MachineEvidence.Cluster.NONE;

         for (MachineEvidence.CellStats var3 : this.cells.values()) {
            var1 = MachineEvidence.Cluster.strongerOf(var1, var3.toCluster());
         }

         return new MachineEvidence(var1);
      }
   }

   private record CellKey(int x, int y, int z) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   private static final class CellStats {
      private int componentCount;
      private final EnumSet types = EnumSet.noneOf(MachineEvidence.ComponentType.class);
      private boolean hasController;
      private boolean hasActuator;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      void add(MachineEvidence.ComponentType type) {
         this.componentCount++;
         this.types.add(type);
         this.hasController = this.hasController | type.role() == MachineEvidence.Role.CONTROLLER;
         this.hasActuator = this.hasActuator | type.role() == MachineEvidence.Role.ACTUATOR;
      }

      MachineEvidence.Cluster toCluster() {
         return new MachineEvidence.Cluster(this.componentCount, this.types.size(), this.hasController, this.hasActuator);
      }
   }

   public record Cluster(int componentCount, int distinctTypes, boolean hasController, boolean hasActuator) {
      static final MachineEvidence.Cluster NONE = new MachineEvidence.Cluster(0, 0, false, false);
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public boolean isStrong() {
         return this.componentCount >= (12) && this.distinctTypes >= (3) && this.hasController && this.hasActuator;
      }

      public boolean isVeryStrong() {
         return this.componentCount >= (24) && this.distinctTypes >= (4) && this.hasController && this.hasActuator;
      }

      static MachineEvidence.Cluster strongerOf(MachineEvidence.Cluster first, MachineEvidence.Cluster second) {
         int var2 = rank(first);
         int var3 = rank(second);
         if (var3 != var2) {
            return var3 > var2 ? second : first;
         } else if (second.componentCount != first.componentCount) {
            return second.componentCount > first.componentCount ? second : first;
         } else {
            return second.distinctTypes > first.distinctTypes ? second : first;
         }
      }

      private static int rank(MachineEvidence.Cluster cluster) {
         if (cluster.isVeryStrong()) {
            return 2;
         } else {
            return cluster.isStrong() ? 1 : 0;
         }
      }
   }

   public enum ComponentType {
      OBSERVER(MachineEvidence.Role.CONTROLLER),
      REPEATER(MachineEvidence.Role.CONTROLLER),
      COMPARATOR(MachineEvidence.Role.CONTROLLER),
      PISTON(MachineEvidence.Role.ACTUATOR),
      STICKY_PISTON(MachineEvidence.Role.ACTUATOR),
      DISPENSER(MachineEvidence.Role.ACTUATOR),
      DROPPER(MachineEvidence.Role.ACTUATOR),
      REDSTONE_WIRE(MachineEvidence.Role.SUPPORT),
      HOPPER(MachineEvidence.Role.SUPPORT),
      REDSTONE_LAMP(MachineEvidence.Role.SUPPORT),
      NOTE_BLOCK(MachineEvidence.Role.SUPPORT);

      private final MachineEvidence.Role role;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      ComponentType(MachineEvidence.Role role) {
         this.role = rolex;
      }

      public MachineEvidence.Role role() {
         return this.role;
      }

      // $VF: synthetic method
      private static MachineEvidence.ComponentType[] $values() {
         MachineEvidence.ComponentType[] var10000 = new MachineEvidence.ComponentType[11];
         var10000[0] = OBSERVER;
         var10000[1] = REPEATER;
         var10000[2] = COMPARATOR;
         var10000[3] = PISTON;
         var10000[4] = STICKY_PISTON;
         var10000[5] = DISPENSER;
         var10000[6] = DROPPER;
         var10000[7] = REDSTONE_WIRE;
         var10000[8] = HOPPER;
         var10000[9] = REDSTONE_LAMP;
         var10000[10] = NOTE_BLOCK;
         return var10000;
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

   public enum Role {
      CONTROLLER,
      ACTUATOR,
      SUPPORT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static MachineEvidence.Role[] $values() {
         MachineEvidence.Role[] var10000 = new MachineEvidence.Role[3];
         var10000[0] = CONTROLLER;
         var10000[1] = ACTUATOR;
         var10000[2] = SUPPORT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[10];
         var10002[0] = (byte)(123);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(114);
         var10002[5] = (byte)(9);
         var10002[6] = (byte)(119);
         var10002[7] = (byte)(-101);
         var10002[8] = (byte)(21);
         var10002[9] = (byte)(-16);
         CONTROLLER = new MachineEvidence.Role();
         var10002 = new byte[8];
         var10002[0] = (byte)(121);
         var10002[1] = (byte)(-39);
         var10002[2] = (byte)(72);
         var10002[3] = (byte)(-117);
         var10002[4] = (byte)(97);
         var10002[5] = (byte)(18);
         var10002[6] = (byte)(116);
         var10002[7] = (byte)(-123);
         ACTUATOR = new MachineEvidence.Role();
         var10002 = new byte[7];
         var10002[0] = (byte)(107);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(76);
         var10002[3] = (byte)(-114);
         var10002[4] = (byte)(111);
         var10002[5] = (byte)(20);
         var10002[6] = (byte)(111);
         SUPPORT = new MachineEvidence.Role();
      }

      // $VF: synthetic method
      private static String $$v_s(byte[] var0, long var1) {
         byte[] var3 = new byte[var0.length];

         for (int var4 = 0; var4 < var0.length; var4++) {
            var3[var4] = (byte)(
               var0[var4]
                  ^ (int)(var1 >>> ((var4 & (7)) << (3))) & (255)
                  ^ (var4 + 1) * (37)
            );
         }

         return new String(var3, StandardCharsets.UTF_8);
      }
   }
}
