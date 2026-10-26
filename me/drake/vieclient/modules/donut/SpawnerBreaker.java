package me.drake.vieclient.modules.donut;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.IntSetting;
import net.minecraft.class_1268;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2561;
import org.lwjgl.glfw.GLFW;

public final class SpawnerBreaker extends Module {
   private final IntSetting radius = (IntSetting)this.addSetting(
      new IntSetting(
         "Radius",
         "Scan radius in blocks",
         4,
         1,
         6
      )
   );
   private final BoolSetting notify = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Notify",
         "Send a chat message after a spawner breaks",
         true
      )
   );
   private final SpawnerBreakerTargetTracker targetTracker = new SpawnerBreakerTargetTracker();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public SpawnerBreaker() {
      super(
         "SpawnerBreaker",
         "Mines nearby spawners while left click is held",
         Category.SMP_UTILITIES
      );
   }

   @Override
   public void onDisable() {
      this.targetTracker.clear();
      super.onDisable();
   }

   @Override
   public void onTick() {
      if (this.isInGame() && mc.field_1761 != null && mc.method_22683() != null && mc.field_1755 == null) {
         boolean var1 = GLFW.glfwGetMouseButton(mc.method_22683().method_4490(), 0) == 1;
         class_2338 var2 = mc.field_1724.method_24515();
         class_2338 var3 = this.targetTracker.target();
         boolean var4 = var3 == null || SpawnerBreakerTargetSelector.isWithinRadius(var2, var3, (Integer)this.radius.get());
         boolean var5 = var3 == null || mc.field_1687.method_8320(var3).method_27852(class_2246.field_10260);
         SpawnerBreakerTargetTracker.Result var6 = this.targetTracker.update(var1, var4, var5);
         if (var6 == SpawnerBreakerTargetTracker.Result.COMPLETED) {
            if ((Boolean)this.notify.get()) {
               mc.field_1724
                  .method_7353(
                     class_2561.method_43470(
                        "§a[SpawnerBreaker] §fBroke a spawner."
                     ),
                     false
                  );
            }
         } else if (var1) {
            var3 = this.targetTracker.target();
            if (var3 == null) {
               var3 = SpawnerBreakerTargetSelector.findNearest(
                  var2, (Integer)this.radius.get(), pos -> mc.field_1687.method_8320(pos).method_27852(class_2246.field_10260)
               );
               this.targetTracker.select(var3);
            }

            if (var3 != null) {
               if (mc.field_1690 != null) {
                  mc.field_1690.field_1886.method_23481(false);
               }

               mc.field_1761.method_2902(var3, class_2350.field_11036);
               mc.field_1724.method_6104(class_1268.field_5808);
            }
         }
      } else {
         this.targetTracker.clear();
      }
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
