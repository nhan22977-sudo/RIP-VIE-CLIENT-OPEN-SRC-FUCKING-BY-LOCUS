package me.drake.vieclient.core.event.events;

import me.drake.vieclient.core.event.CancellableEvent;
import net.minecraft.class_1297;
import net.minecraft.class_1299;

public class EntitySpawnEvent extends CancellableEvent {
   private final class_1297 entity;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public EntitySpawnEvent(class_1297 entity) {
      this.entity = entityx;
   }

   public class_1297 getEntity() {
      return this.entity;
   }

   public class_1299 getEntityType() {
      return this.entity.method_5864();
   }
}
