package me.drake.vieclient.core.event.events;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.Event;
import net.minecraft.class_332;

public class RenderEvent extends Event {
   private final RenderEvent.Stage stage;
   private final float tickDelta;
   private final float frameTime;
   private class_332 drawContext;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public RenderEvent(RenderEvent.Stage stage, float tickDelta) {
      this(stagex, tickDeltax, 0.0F);
   }

   public RenderEvent(RenderEvent.Stage stage, float tickDelta, float frameTime) {
      this.stage = stagex;
      this.tickDelta = tickDeltax;
      this.frameTime = frameTimex;
   }

   public RenderEvent.Stage getStage() {
      return this.stage;
   }

   public float getTickDelta() {
      return this.tickDelta;
   }

   public float getFrameTime() {
      return this.frameTime;
   }

   public class_332 getDrawContext() {
      return this.drawContext;
   }

   public void setDrawContext(class_332 ctx) {
      this.drawContext = ctx;
   }

   public enum Stage {
      HUD_2D,
      WORLD_3D;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static RenderEvent.Stage[] $values() {
         RenderEvent.Stage[] var10000 = new RenderEvent.Stage[2];
         var10000[0] = HUD_2D;
         var10000[1] = WORLD_3D;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[6];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(88);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(21);
         var10002[5] = (byte)(99);
         HUD_2D = new RenderEvent.Stage();
         var10002 = new byte[8];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(99);
         var10002[5] = (byte)(120);
         var10002[6] = (byte)(31);
         var10002[7] = (byte)(106);
         WORLD_3D = new RenderEvent.Stage();
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

   public enum Type {
      HUD,
      WORLD;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static RenderEvent.Type[] $values() {
         RenderEvent.Type[] var10000 = new RenderEvent.Type[2];
         var10000[0] = HUD;
         var10000[1] = WORLD;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[3];
         var10002[0] = (byte)(113);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(88);
         HUD = new RenderEvent.Type();
         var10002 = new byte[5];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-43);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-110);
         var10002[4] = (byte)(-41);
         WORLD = new RenderEvent.Type();
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
