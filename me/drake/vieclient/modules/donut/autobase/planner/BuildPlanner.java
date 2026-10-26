package me.drake.vieclient.modules.donut.autobase.planner;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import me.drake.vieclient.modules.donut.autobase.schematic.SchematicBlockEntry;
import me.drake.vieclient.modules.donut.autobase.schematic.SchematicData;
import me.drake.vieclient.modules.donut.autobase.schematic.SchematicTransform;
import net.minecraft.class_1792;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;

public class BuildPlanner {
   private final List allTasks = new ArrayList();
   private final Map taskByPos = new HashMap();
   private final Map dependencies = new HashMap();
   private final Set completedPositions = Collections.synchronizedSet(new HashSet());
   private final Set pendingPositions = Collections.synchronizedSet(new HashSet());
   private final Map blockedPositions = new ConcurrentHashMap();
   private boolean planningCompleted = false;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public void loadSchematic(SchematicData data, class_2338 origin, SchematicTransform transform) {
      this.allTasks.clear();
      this.taskByPos.clear();
      this.dependencies.clear();
      this.completedPositions.clear();
      this.pendingPositions.clear();
      this.blockedPositions.clear();
      this.planningCompleted = false;
      if (data != null && data.blocks() != null && !data.blocks().isEmpty()) {
         SchematicData var4 = transform != null && !transform.equals(SchematicTransform.IDENTITY) ? data.transform(transform) : data;

         for (SchematicBlockEntry var6 : var4.blocks().values()) {
            if (!var6.isAir()) {
               class_2338 var7 = var6.pos().method_10081(origin);
               BuildPlanner.PlannedBuildTask var8 = new BuildPlanner.PlannedBuildTask(
                  var7,
                  var6.blockId(),
                  var6.properties(),
                  var6.requiredItemId(),
                  var6.itemQuantity(),
                  var6.isDependentHalf(),
                  var6.state(),
                  var6.requiredItem()
               );
               this.allTasks.add(var8);
               this.taskByPos.put(var7, var8);
            }
         }

         this.allTasks
            .sort(
               Comparator.<BuildPlanner.PlannedBuildTask>comparingInt(t -> t.getTargetPos().method_10264())
                  .thenComparingInt(t -> t.getTargetPos().method_10263())
                  .thenComparingInt(t -> t.getTargetPos().method_10260())
            );

         for (BuildPlanner.PlannedBuildTask var16 : this.allTasks) {
            class_2338 var17 = var16.getTargetPos();
            HashSet var18 = new HashSet();
            String var9 = var16.getExpectedCleanId();
            Map var10 = var16.getExpectedProperties();
            if (this.isGravityBlock(var9)) {
               class_2338 var11 = var17.method_10074();
               if (this.taskByPos.containsKey(var11)) {
                  var18.add(var11);
               }
            }

            if (var9.endsWith("_door")) {
               String var19 = (String)var10.get("half");
               if ("upper".equalsIgnoreCase(var19)) {
                  class_2338 var12 = var17.method_10074();
                  if (this.taskByPos.containsKey(var12)) {
                     var18.add(var12);
                  }
               }
            }

            if (var9.endsWith("_bed")) {
               String var20 = (String)var10.get("part");
               if ("head".equalsIgnoreCase(var20)) {
                  String var22 = var10.getOrDefault(
                     "facing", "north"
                  );
                  class_2350 var13 = parseDirection(var22);
                  if (var13 != null) {
                     class_2338 var14 = var17.method_10093(var13.method_10153());
                     if (this.taskByPos.containsKey(var14)) {
                        var18.add(var14);
                     }
                  }
               }
            }

            if (var9.contains("wall_")
               || var9.endsWith("_ladder")) {
               String var21 = (String)var10.get("facing");
               if (var21 != null) {
                  class_2350 var23 = parseDirection(var21);
                  if (var23 != null) {
                     class_2338 var24 = var17.method_10093(var23.method_10153());
                     if (this.taskByPos.containsKey(var24)) {
                        var18.add(var24);
                     }
                  }
               }
            }

            if (!var18.isEmpty()) {
               this.dependencies.put(var17, var18);
            }
         }

         this.planningCompleted = true;
      } else {
         this.planningCompleted = true;
      }
   }

   private static class_2350 parseDirection(String name) {
      if (name == null) {
         return null;
      }

      try {
         return class_2350.valueOf(name.trim().toUpperCase(Locale.ROOT));
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public BuildPlanner.PlannedBuildTask getNextTask(class_243 playerPos, double maxReach, String heldItemId, BuildPlanner.WorldBlockQuery worldQuery) {
      if (this.planningCompleted && !this.allTasks.isEmpty()) {
         BuildPlanner.PlannedBuildTask var6 = null;
         double var7 = Double.MAX_VALUE;

         for (BuildPlanner.PlannedBuildTask var10 : this.allTasks) {
            class_2338 var11 = var10.getTargetPos();
            if (!this.completedPositions.contains(var11) && !this.pendingPositions.contains(var11) && !this.blockedPositions.containsKey(var11)) {
               if (worldQuery != null && worldQuery.isBlockSatisfied(var11, var10.getExpectedCleanId(), var10.getExpectedProperties())) {
                  this.completedPositions.add(var11);
               } else {
                  Set var12 = (Set)this.dependencies.get(var11);
                  if (var12 != null && !var12.isEmpty()) {
                     boolean var13 = true;

                     for (class_2338 var15 : var12) {
                        if (!this.completedPositions.contains(var15)) {
                           var13 = false;
                           break;
                        }
                     }

                     if (!var13) {
                        continue;
                     }
                  }

                  if (playerPos == null) {
                     return var10;
                  }

                  double var17 = playerPos.method_1028(var11.method_10263() + 0.5, var11.method_10264() + 0.5, var11.method_10260() + 0.5);
                  if (!(var17 > maxReach * maxReach)) {
                     double var18 = var17;
                     if (heldItemId != null && !heldItemId.equalsIgnoreCase(var10.getExpectedCleanId())) {
                        var18 += 15.0;
                     }

                     if (var18 < var7) {
                        var7 = var18;
                        var6 = var10;
                     }
                  }
               }
            }
         }

         return var6;
      } else {
         return null;
      }
   }

   public Map calculateRemainingMaterialDemand() {
      LinkedHashMap var1 = new LinkedHashMap();

      for (BuildPlanner.PlannedBuildTask var3 : this.allTasks) {
         class_2338 var4 = var3.getTargetPos();
         if (!this.completedPositions.contains(var4)) {
            int var5 = var3.getItemQuantity();
            if (var5 > 0) {
               String var6 = var3.getRequiredItemId();
               if (var6 == null || var6.isEmpty()) {
                  var6 = var3.getExpectedCleanId();
               }

               var1.put(var6, var1.getOrDefault(var6, 0) + var5);
            }
         }
      }

      return var1;
   }

   public void markPending(class_2338 pos) {
      this.pendingPositions.add(pos);
   }

   public void unmarkPending(class_2338 pos) {
      this.pendingPositions.remove(pos);
   }

   public void markCompleted(class_2338 pos) {
      this.pendingPositions.remove(pos);
      this.blockedPositions.remove(pos);
      this.completedPositions.add(pos);
      BuildPlanner.PlannedBuildTask var2 = (BuildPlanner.PlannedBuildTask)this.taskByPos.get(pos);
      if (var2 != null) {
         String var3 = var2.getExpectedCleanId();
         Map var4 = var2.getExpectedProperties();
         if ("lower"
               .equalsIgnoreCase((String)var4.get("half"))
            || var3.endsWith("_door")) {
            this.completedPositions.add(pos.method_10084());
         }

         if (var3.endsWith("_bed")
            && "foot"
               .equalsIgnoreCase((String)var4.get("part"))) {
            String var5 = var4.getOrDefault(
               "facing", "north"
            );
            class_2350 var6 = parseDirection(var5);
            if (var6 != null) {
               this.completedPositions.add(pos.method_10093(var6));
            }
         }
      }
   }

   public void markBlocked(class_2338 pos, String reason) {
      this.pendingPositions.remove(pos);
      String var10002;
      if (reason != null) {
         var10002 = reason;
      } else {
         byte[] var3 = new byte[14];
         var3[0] = (byte)(118);
         var3[1] = (byte)(-12);
         var3[2] = (byte)(119);
         var3[3] = (byte)(-80);
         var3[4] = (byte)(122);
         var3[5] = (byte)(-109);
         var3[6] = (byte)(107);
         var3[7] = (byte)(-112);
         var3[8] = (byte)(57);
         var3[9] = (byte)(-57);
         var3[10] = (byte)(-123);
         var3[11] = (byte)(-123);
         var3[12] = (byte)(34);
         var3[13] = (byte)(82);
         var10002 = $$v_s(var3, -7492236418426351610L);
      }

      this.blockedPositions.put(pos, var10002);
   }

   public void resetBlocked(class_2338 pos) {
      this.blockedPositions.remove(pos);
   }

   public void resetAllBlocked() {
      this.blockedPositions.clear();
   }

   public boolean isGravityBlock(String cleanId) {
      if (cleanId == null) {
         return false;
      }

      byte[] var10001 = new byte[4];
      var10001[0] = (byte)(80);
      var10001[1] = (byte)(-5);
      var10001[2] = (byte)(114);
      var10001[3] = (byte)(-70);
      if (!cleanId.endsWith($$v_s(var10001, -7492236418426351610L))) {
         var10001 = new byte[6];
         var10001[0] = (byte)(68);
         var10001[1] = (byte)(-24);
         var10001[2] = (byte)(125);
         var10001[3] = (byte)(-88);
         var10001[4] = (byte)(112);
         var10001[5] = (byte)(-120);
         if (!cleanId.endsWith($$v_s(var10001, -7492236418426351610L))) {
            var10001 = new byte[15];
            var10001[0] = (byte)(64);
            var10001[1] = (byte)(-11);
            var10001[2] = (byte)(114);
            var10001[3] = (byte)(-67);
            var10001[4] = (byte)(103);
            var10001[5] = (byte)(-127);
            var10001[6] = (byte)(113);
            var10001[7] = (byte)(-43);
            var10001[8] = (byte)(20);
            var10001[9] = (byte)(-46);
            var10001[10] = (byte)(-117);
            var10001[11] = (byte)(-127);
            var10001[12] = (byte)(41);
            var10001[13] = (byte)(89);
            var10001[14] = (byte)(95);
            if (!cleanId.contains($$v_s(var10001, -7492236418426351610L))) {
               var10001 = new byte[5];
               var10001[0] = (byte)(66);
               var10001[1] = (byte)(-12);
               var10001[2] = (byte)(106);
               var10001[3] = (byte)(-73);
               var10001[4] = (byte)(121);
               if (!cleanId.endsWith($$v_s(var10001, -7492236418426351610L))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public int getTotalCount() {
      return this.allTasks.size();
   }

   public int getCompletedCount() {
      return this.completedPositions.size();
   }

   public int getPendingCount() {
      return this.pendingPositions.size();
   }

   public int getBlockedCount() {
      return this.blockedPositions.size();
   }

   public int getRemainingCount() {
      return Math.max(0, this.getTotalCount() - this.getCompletedCount());
   }

   public boolean isFinished() {
      return this.planningCompleted && this.getCompletedCount() >= this.getTotalCount();
   }

   public Map getBlockedPositions() {
      return Collections.unmodifiableMap(this.blockedPositions);
   }

   public List getAllTasks() {
      return Collections.unmodifiableList(this.allTasks);
   }

   public BuildPlanner.PlannedBuildTask getTaskAt(class_2338 pos) {
      return (BuildPlanner.PlannedBuildTask)this.taskByPos.get(pos);
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

   public static final class PlannedBuildTask {
      private final class_2338 targetPos;
      private final String expectedCleanId;
      private final Map expectedProperties;
      private final String requiredItemId;
      private final int itemQuantity;
      private final boolean isDependentHalf;
      private final class_2680 expectedState;
      private final class_1792 requiredItem;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public PlannedBuildTask(
         class_2338 targetPos,
         String expectedCleanId,
         Map expectedProperties,
         String requiredItemId,
         int itemQuantity,
         boolean isDependentHalf,
         class_2680 expectedState,
         class_1792 requiredItem
      ) {
         this.targetPos = targetPosx;
         this.expectedCleanId = expectedCleanIdx;
         this.expectedProperties = expectedPropertiesx != null ? expectedPropertiesx : Collections.emptyMap();
         this.requiredItemId = requiredItemIdx;
         this.itemQuantity = itemQuantityx;
         this.isDependentHalf = isDependentHalfx;
         this.expectedState = expectedStatex;
         this.requiredItem = requiredItemx;
      }

      public class_2338 getTargetPos() {
         return this.targetPos;
      }

      public String getExpectedCleanId() {
         return this.expectedCleanId;
      }

      public Map getExpectedProperties() {
         return this.expectedProperties;
      }

      public String getRequiredItemId() {
         return this.requiredItemId;
      }

      public int getItemQuantity() {
         return this.itemQuantity;
      }

      public boolean isDependentHalf() {
         return this.isDependentHalf;
      }

      public class_2680 getExpectedState() {
         return this.expectedState;
      }

      public class_1792 getRequiredItem() {
         return this.requiredItem;
      }
   }

   @FunctionalInterface
   public interface WorldBlockQuery {
      boolean isBlockSatisfied(class_2338 var1, String var2, Map var3);
   }
}
