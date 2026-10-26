package me.drake.vieclient.core.control;

import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;

public final class ActionArbiter {
   private static final ActionArbiter INSTANCE = new ActionArbiter();
   private final Map claims = new EnumMap<>(ActionArbiter.Resource.class);
   private int currentTick = 0;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public static ActionArbiter get() {
      return INSTANCE;
   }

   private ActionArbiter() {
   }

   public void tick() {
      this.currentTick++;
      this.claims.entrySet().removeIf(e -> this.currentTick >= ((ActionArbiter.Claim)e.getValue()).expiresAtTick());
   }

   public boolean tryAcquire(ActionArbiter.Resource resource, ActionArbiter.Owner owner, int lockTicks) {
      ActionArbiter.Claim var4 = (ActionArbiter.Claim)this.claims.get(resource);
      if (var4 != null && owner != var4.owner() && !owner.outranks(var4.owner())) {
         return false;
      }

      this.claims.put(resource, new ActionArbiter.Claim(owner, this.currentTick + Math.max(1, lockTicks)));
      return true;
   }

   public boolean tryAcquire(ActionArbiter.Resource resource, ActionArbiter.Owner owner) {
      return this.tryAcquire(resource, owner, 1);
   }

   public void release(ActionArbiter.Resource resource, ActionArbiter.Owner owner) {
      ActionArbiter.Claim var3 = (ActionArbiter.Claim)this.claims.get(resource);
      if (var3 != null && var3.owner() == owner) {
         this.claims.remove(resource);
      }
   }

   public boolean tryAcquireAll(ActionArbiter.Owner owner, int lockTicks, ActionArbiter.Resource... resources) {
      if (resources != null && resources.length != 0) {
         for (ActionArbiter.Resource var7 : resources) {
            ActionArbiter.Claim var8 = (ActionArbiter.Claim)this.claims.get(var7);
            if (var8 != null && var8.owner() != owner && !owner.outranks(var8.owner())) {
               return false;
            }
         }

         int var9 = this.currentTick + Math.max(1, lockTicks);

         for (ActionArbiter.Resource var13 : resources) {
            this.claims.put(var13, new ActionArbiter.Claim(owner, var9));
         }

         return true;
      } else {
         return true;
      }
   }

   public void releaseAll(ActionArbiter.Owner owner, ActionArbiter.Resource... resources) {
      if (resources != null) {
         for (ActionArbiter.Resource var6 : resources) {
            this.release(var6, owner);
         }
      }
   }

   public boolean hasClaim(ActionArbiter.Resource resource, ActionArbiter.Owner owner) {
      return this.ownerOf(resource) == owner;
   }

   public ActionArbiter.Owner ownerOf(ActionArbiter.Resource resource) {
      ActionArbiter.Claim var2 = (ActionArbiter.Claim)this.claims.get(resource);
      return var2 == null ? ActionArbiter.Owner.NONE : var2.owner();
   }

   public boolean isHeldByOther(ActionArbiter.Resource resource, ActionArbiter.Owner owner) {
      ActionArbiter.Owner var3 = this.ownerOf(resource);
      return var3 != ActionArbiter.Owner.NONE && var3 != owner;
   }

   public void resetForTest() {
      this.claims.clear();
      this.currentTick = 0;
   }

   private record Claim(ActionArbiter.Owner owner, int expiresAtTick) {
      // $VF: synthetic field
      private static final boolean $$vie_p = true;
   }

   public enum Owner {
      NONE,
      NOSLOW,
      AUTOSPRINT,
      FREELOOK,
      AIMASSIST,
      CRYSTAL_COMBAT,
      ANCHOR_COMBAT,
      AUTOTOOL,
      AUTOEAT,
      TUNNEL_MINE,
      TUNNEL_REPAIR,
      AUTO_MINE,
      AUTO_BUILD,
      AUTO_BASE_MINE,
      AUTO_BASE_BUILD,
      EMERGENCY;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      public boolean outranks(ActionArbiter.Owner other) {
         return this.ordinal() > other.ordinal();
      }

      // $VF: synthetic method
      private static ActionArbiter.Owner[] $values() {
         ActionArbiter.Owner[] var10000 = new ActionArbiter.Owner[16];
         var10000[0] = NONE;
         var10000[1] = NOSLOW;
         var10000[2] = AUTOSPRINT;
         var10000[3] = FREELOOK;
         var10000[4] = AIMASSIST;
         var10000[5] = CRYSTAL_COMBAT;
         var10000[6] = ANCHOR_COMBAT;
         var10000[7] = AUTOTOOL;
         var10000[8] = AUTOEAT;
         var10000[9] = TUNNEL_MINE;
         var10000[10] = TUNNEL_REPAIR;
         var10000[11] = AUTO_MINE;
         var10000[12] = AUTO_BUILD;
         var10000[13] = AUTO_BASE_MINE;
         var10000[14] = AUTO_BASE_BUILD;
         var10000[15] = EMERGENCY;
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

   public enum Resource {
      HOTBAR_SLOT,
      SPRINT,
      USE_KEY,
      ATTACK_KEY,
      CAMERA,
      MOVEMENT,
      SNEAK;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static ActionArbiter.Resource[] $values() {
         ActionArbiter.Resource[] var10000 = new ActionArbiter.Resource[7];
         var10000[0] = HOTBAR_SLOT;
         var10000[1] = SPRINT;
         var10000[2] = USE_KEY;
         var10000[3] = ATTACK_KEY;
         var10000[4] = CAMERA;
         var10000[5] = MOVEMENT;
         var10000[6] = SNEAK;
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
}
