package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.IntSetting;
import me.drake.vieclient.core.setting.KeybindSetting;
import me.drake.vieclient.util.KeyUtils;
import net.minecraft.class_3532;

public class CustomFOV extends Module {
   private final IntSetting fov = (IntSetting)this.addSetting(
      new IntSetting(
         "FOV",
         "Target field of view angle in degrees",
         120,
         30,
         170,
         1
      )
   );
   private final BoolSetting staticFov = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Static FOV",
         "Ignore sprinting, potion and speed dynamic FOV changes",
         false
      )
   );
   private final BoolSetting smooth = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Smooth Transition",
         "Smoothly interpolate FOV transitions",
         true
      )
   );
   private final DoubleSetting smoothSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Smooth Speed",
         "Speed of FOV interpolation",
         12.0,
         1.0,
         30.0,
         0.5
      )
   );
   private final BoolSetting zoom = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Zoom",
         "Quick zoom override mode when holding zoom key",
         false
      )
   );
   private final IntSetting zoomFov = (IntSetting)this.addSetting(
      new IntSetting(
         "Zoom FOV",
         "Field of view angle while zooming",
         30,
         10,
         80,
         1
      )
   );
   private final KeybindSetting zoomKey = (KeybindSetting)this.addSetting(
      new KeybindSetting(
         "Zoom Key",
         "Key to hold down for zooming",
         -1
      )
   );
   private float currentFov = 120.0F;
   private float prevFov = 120.0F;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public CustomFOV() {
      super(
         "CustomFOV",
         "Adjust field of view beyond vanilla limits",
         Category.RENDER
      );
      this.smoothSpeed.visibleWhen(this.smooth::get);
      this.zoomFov.visibleWhen(this.zoom::get);
      this.zoomKey.visibleWhen(this.zoom::get);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      float var1 = this.getTargetBaseFov();
      this.currentFov = var1;
      this.prevFov = var1;
   }

   @Override
   public void onTick() {
      this.prevFov = this.currentFov;
      float var1 = this.getTargetBaseFov();
      if ((Boolean)this.smooth.get()) {
         float var2 = (float)((Double)this.smoothSpeed.get() * 0.05);
         this.currentFov = this.currentFov + (var1 - this.currentFov) * class_3532.method_15363(var2, 0.05F, 1.0F);
      } else {
         this.currentFov = var1;
      }
   }

   public boolean isZooming() {
      return this.zoom.get() && this.zoomKey.isBound() ? KeyUtils.isKeyPressed((Integer)this.zoomKey.get()) : false;
   }

   public float getTargetBaseFov() {
      return this.isZooming() ? ((Integer)this.zoomFov.get()).intValue() : ((Integer)this.fov.get()).intValue();
   }

   public float computeFov(float vanillaFov, float tickDelta, boolean changingFov) {
      float var4 = mc != null && mc.field_1690 != null && mc.field_1690.method_41808() != null
         ? ((Integer)mc.field_1690.method_41808().method_41753()).floatValue()
         : 70.0F;
      float var5 = this.smooth.get() ? class_3532.method_16439(tickDelta, this.prevFov, this.currentFov) : this.getTargetBaseFov();
      if ((Boolean)this.staticFov.get()) {
         return var5;
      }

      float var6 = var4 > 0.0F ? vanillaFov / var4 : 1.0F;
      return var5 * var6;
   }

   @Override
   public String getInfo() {
      String var1;
      if (this.isZooming()) {
         byte[] var10000 = new byte[4];
         var10000[0] = (byte)(112);
         var10000[1] = (byte)(-11);
         var10000[2] = (byte)(115);
         var10000[3] = (byte)(-77);
         var1 = $$v_s(var10000, 8019623314990551055L);
      } else {
         var1 = String.valueOf(this.fov.get());
      }

      return var1;
   }

   public IntSetting getFovSetting() {
      return this.fov;
   }

   public BoolSetting getStaticFovSetting() {
      return this.staticFov;
   }

   public BoolSetting getSmoothSetting() {
      return this.smooth;
   }

   public DoubleSetting getSmoothSpeedSetting() {
      return this.smoothSpeed;
   }

   public BoolSetting getZoomSetting() {
      return this.zoom;
   }

   public IntSetting getZoomFovSetting() {
      return this.zoomFov;
   }

   public KeybindSetting getZoomKeySetting() {
      return this.zoomKey;
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
