package me.drake.vieclient.modules.donut.autobase.executor;

import net.minecraft.class_1792;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_2680;

public final class PlacementTarget {
   private final class_2338 targetPos;
   private final class_2680 expectedState;
   private final class_1792 requiredItem;
   private final class_2338 supportPos;
   private final class_2350 placementFace;
   private final class_243 hitVector;
   private int retryCount = 0;
   private String failureReason = "";
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public PlacementTarget(
      class_2338 targetPos, class_2680 expectedState, class_1792 requiredItem, class_2338 supportPos, class_2350 placementFace, class_243 hitVector
   ) {
      this.targetPos = targetPosx.method_10062();
      this.expectedState = expectedStatex;
      this.requiredItem = requiredItemx;
      this.supportPos = supportPosx != null ? supportPosx.method_10062() : null;
      this.placementFace = placementFacex;
      this.hitVector = hitVectorx;
   }

   public class_2338 getTargetPos() {
      return this.targetPos;
   }

   public class_2680 getExpectedState() {
      return this.expectedState;
   }

   public class_1792 getRequiredItem() {
      return this.requiredItem;
   }

   public class_2338 getSupportPos() {
      return this.supportPos;
   }

   public class_2350 getPlacementFace() {
      return this.placementFace;
   }

   public class_243 getHitVector() {
      return this.hitVector;
   }

   public int getRetryCount() {
      return this.retryCount;
   }

   public void incrementRetry() {
      this.retryCount++;
   }

   public String getFailureReason() {
      return this.failureReason;
   }

   public void setFailureReason(String failureReason) {
      this.failureReason = failureReasonx;
   }
}
