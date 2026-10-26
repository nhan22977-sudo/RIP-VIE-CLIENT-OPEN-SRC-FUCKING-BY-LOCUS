package com.zigythebird.playeranimcore.animation.layered;

import com.zigythebird.playeranimcore.bones.AdvancedBoneSnapshot;
import com.zigythebird.playeranimcore.bones.PlayerAnimBone;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public record AnimationSnapshot(Map<String, AdvancedBoneSnapshot> snapshots) implements IAnimation {
   @Override
   public boolean isActive() {
      return true;
   }

   @Override
   public PlayerAnimBone get3DTransform(@NotNull PlayerAnimBone bone) {
      return this.snapshots.containsKey(bone.getName()) ? bone.copySnapshotSafe(this.snapshots.get(bone.getName())) : bone;
   }

   @NotNull
   @Override
   public String toString() {
      return "AnimationSnapshot{snapshots=" + this.snapshots + "}";
   }
}
