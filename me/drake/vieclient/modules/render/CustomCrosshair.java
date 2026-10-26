package me.drake.vieclient.modules.render;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.event.EventHandler;
import me.drake.vieclient.core.event.events.PostBlitRenderEvent;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.BoolSetting;
import me.drake.vieclient.core.setting.ColorSetting;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import me.drake.vieclient.modules.client.ClientSettings;
import me.drake.vieclient.render.nvg.IconManager;
import me.drake.vieclient.render.nvg.NanoVGRenderer;
import me.drake.vieclient.util.RainbowUtil;
import net.minecraft.class_1309;
import net.minecraft.class_3966;
import net.minecraft.class_408;

public class CustomCrosshair extends Module {
   private static CustomCrosshair INSTANCE;
   private final EnumSetting style = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Style",
         "Display style for custom crosshair",
         CustomCrosshair.Style.VIE_LOGO,
         CustomCrosshair.Style.values()
      )
   );
   private final EnumSetting colorMode = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Color Mode",
         "Color mode for crosshair icon",
         CustomCrosshair.ColorMode.PINK,
         CustomCrosshair.ColorMode.values()
      )
   );
   private final ColorSetting customColor = (ColorSetting)this.addSetting(
      new ColorSetting(
         "Custom Color",
         "Custom color when color mode is set to Custom",
         -33876
      )
   );
   private final DoubleSetting size = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Size",
         "Crosshair logo size in pixels",
         18.0,
         10.0,
         40.0,
         1.0
      )
   );
   private final BoolSetting glowBloom = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Glow Bloom",
         "Radiant bloom glow aura around the crosshair",
         true
      )
   );
   private final BoolSetting targetTint = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Target Red",
         "Flash red when crosshair is hovering over a living target",
         true
      )
   );
   private final BoolSetting pulseOnAttack = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Attack Pulse",
         "Slight pulse and expansion feedback on attack",
         true
      )
   );
   private final BoolSetting cooldownBar = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Cooldown Bar",
         "Display attack cooldown meter arc below crosshair",
         true
      )
   );
   private final BoolSetting spin = (BoolSetting)this.addSetting(
      new BoolSetting(
         "Spin",
         "Continuously rotate the crosshair in a circle",
         false
      )
   );
   private final DoubleSetting spinSpeed = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Spin Speed",
         "Crosshair rotation speed",
         1.0,
         0.1,
         5.0,
         0.1
      )
   );
   private float attackPulseAnim = 0.0F;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public CustomCrosshair() {
      super(
         "CustomCrosshair",
         "Replace vanilla crosshair with custom Vie Client crosshair and cooldown meter",
         Category.RENDER
      );
      INSTANCE = this;
      this.customColor.visibleWhen(() -> this.colorMode.get() == CustomCrosshair.ColorMode.CUSTOM);
      this.spinSpeed.visibleWhen(this.spin::get);
   }

   public static CustomCrosshair get() {
      return INSTANCE;
   }

   public static boolean isActive() {
      return INSTANCE != null && INSTANCE.isEnabled();
   }

   @EventHandler
   public void onPostBlit(PostBlitRenderEvent event) {
      if (this.isInGame() && this.isEnabled()) {
         if (!mc.field_1690.field_1842) {
            if (mc.field_1755 == null || mc.field_1755 instanceof class_408) {
               int var2 = mc.method_22683().method_4486();
               int var3 = mc.method_22683().method_4502();
               float var4 = var2 / 2.0F;
               float var5 = var3 / 2.0F;
               float var6 = mc.field_1724 != null ? mc.field_1724.method_7261(0.0F) : 1.0F;
               if ((Boolean)this.pulseOnAttack.get() && var6 < 0.99F) {
                  this.attackPulseAnim = Math.min(1.0F, this.attackPulseAnim + 0.15F);
               } else {
                  this.attackPulseAnim = Math.max(0.0F, this.attackPulseAnim - 0.08F);
               }

               float var7 = ((Double)this.size.get()).floatValue() * (1.0F + this.attackPulseAnim * 0.2F);
               boolean var8 = (Boolean)this.targetTint.get()
                  && mc.field_1765 instanceof class_3966 var10
                  && var10.method_17782() instanceof class_1309 var9
                  && var9.method_5805();
               Color var19 = this.resolveBaseColor();
               Color var20 = var8 ? new Color(-52412) : var19;
               boolean var22 = (Boolean)this.spin.get();
               if (var22) {
                  float var12 = (float)(System.currentTimeMillis() % 3600000L / 1000.0 * Math.PI * 2.0 * (Double)this.spinSpeed.get() * 0.5);
                  NanoVGRenderer.save();
                  NanoVGRenderer.translate(var4, var5);
                  NanoVGRenderer.rotate(var12);
                  NanoVGRenderer.translate(-var4, -var5);
               }

               if ((Boolean)this.glowBloom.get()) {
                  Color var23 = new Color(var20.getRed(), var20.getGreen(), var20.getBlue(), 75);
                  NanoVGRenderer.drawRadialGlow(var4, var5, var7 * 0.95F, var23);
               }

               if (this.style.get() == CustomCrosshair.Style.VIE_WITH_CROSS) {
                  float var24 = var7 * 0.65F;
                  float var13 = var7 * 0.4F;
                  float var14 = 1.5F;
                  Color var15 = new Color(var20.getRed(), var20.getGreen(), var20.getBlue(), 210);
                  NanoVGRenderer.drawLine(var4 - var24 - var13, var5, var4 - var24, var5, var14, var15);
                  NanoVGRenderer.drawLine(var4 + var24, var5, var4 + var24 + var13, var5, var14, var15);
                  NanoVGRenderer.drawLine(var4, var5 - var24 - var13, var4, var5 - var24, var14, var15);
                  NanoVGRenderer.drawLine(var4, var5 + var24, var4, var5 + var24 + var13, var14, var15);
               } else if (this.style.get() == CustomCrosshair.Style.VIE_DOT) {
                  NanoVGRenderer.drawCircle(var4, var5, 1.5F, var20);
               } else if (this.style.get() == CustomCrosshair.Style.CIRCLE || this.style.get() == CustomCrosshair.Style.CIRCLE_DOT) {
                  float var25 = var7 * 0.52F;
                  Color var27 = new Color(var20.getRed(), var20.getGreen(), var20.getBlue(), 210);
                  NanoVGRenderer.drawCircleOutline(var4, var5, var25, 1.4F, var27);
                  float var30 = var25 + 1.2F;
                  float var33 = var7 * 0.28F;
                  float var16 = 1.4F;
                  NanoVGRenderer.drawLine(var4 - var30 - var33, var5, var4 - var30, var5, var16, var27);
                  NanoVGRenderer.drawLine(var4 + var30, var5, var4 + var30 + var33, var5, var16, var27);
                  NanoVGRenderer.drawLine(var4, var5 - var30 - var33, var4, var5 - var30, var16, var27);
                  NanoVGRenderer.drawLine(var4, var5 + var30, var4, var5 + var30 + var33, var16, var27);
                  if (this.style.get() == CustomCrosshair.Style.CIRCLE_DOT) {
                     NanoVGRenderer.drawCircle(var4, var5, 1.5F, var20);
                  }
               }

               int var26 = this.colorMode.get() == CustomCrosshair.ColorMode.WHITE
                  ? IconManager.idColored("logovie")
                  : IconManager.id("logovie");
               if (var26 != -1) {
                  float var28 = var4 - var7 / 2.0F;
                  float var31 = var5 - var7 / 2.0F;
                  NanoVGRenderer.drawImage(var26, var28, var31, var7, var7, var20);
               }

               if (var22) {
                  NanoVGRenderer.restore();
               }

               if ((Boolean)this.cooldownBar.get() && var6 < 0.99F) {
                  float var29 = var7 * 1.2F;
                  float var32 = 2.5F;
                  float var34 = var4 - var29 / 2.0F;
                  float var35 = var5 + var7 * 0.7F;
                  NanoVGRenderer.drawRoundedRect(var34, var35, var29, var32, 1.2F, new Color(0, 0, 0, 140));
                  float var17 = var29 * Math.min(1.0F, Math.max(0.0F, var6));
                  Color var18 = var6 >= 0.95F ? new Color(-10027128) : var20;
                  NanoVGRenderer.drawRoundedRect(var34, var35, var17, var32, 1.2F, var18);
               }
            }
         }
      }
   }

   private Color resolveBaseColor() {
      return switch ((CustomCrosshair.ColorMode)this.colorMode.get()) {
         case PINK -> new Color(-33876);
         case ACCENT -> {
            ClientSettings var1 = ClientSettings.get();
            yield var1 != null ? new Color(var1.getPrimaryColor()) : new Color(-33876);
         }
         case CUSTOM -> new Color((Integer)this.customColor.get());
         case WHITE -> Color.WHITE;
         case RAINBOW -> new Color(RainbowUtil.rainbow(1.0, 0L), true);
      };
   }

   public EnumSetting getStyleSetting() {
      return this.style;
   }

   public EnumSetting getColorModeSetting() {
      return this.colorMode;
   }

   public ColorSetting getCustomColorSetting() {
      return this.customColor;
   }

   public DoubleSetting getSizeSetting() {
      return this.size;
   }

   public BoolSetting getSpinSetting() {
      return this.spin;
   }

   public DoubleSetting getSpinSpeedSetting() {
      return this.spinSpeed;
   }

   public Color getBaseColor() {
      return this.resolveBaseColor();
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

   public enum ColorMode {
      PINK,
      ACCENT,
      CUSTOM,
      WHITE,
      RAINBOW;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CustomCrosshair.ColorMode[] $values() {
         CustomCrosshair.ColorMode[] var10000 = new CustomCrosshair.ColorMode[5];
         var10000[0] = PINK;
         var10000[1] = ACCENT;
         var10000[2] = CUSTOM;
         var10000[3] = WHITE;
         var10000[4] = RAINBOW;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[4];
         var10002[0] = (byte)(110);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(82);
         var10002[3] = (byte)(-107);
         PINK = new CustomCrosshair.ColorMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(127);
         var10002[1] = (byte)(-39);
         var10002[2] = (byte)(95);
         var10002[3] = (byte)(-101);
         var10002[4] = (byte)(70);
         var10002[5] = (byte)(121);
         ACCENT = new CustomCrosshair.ColorMode();
         var10002 = new byte[6];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-49);
         var10002[2] = (byte)(79);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(71);
         var10002[5] = (byte)(96);
         CUSTOM = new CustomCrosshair.ColorMode();
         var10002 = new byte[5];
         var10002[0] = (byte)(105);
         var10002[1] = (byte)(-46);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-118);
         var10002[4] = (byte)(77);
         WHITE = new CustomCrosshair.ColorMode();
         var10002 = new byte[7];
         var10002[0] = (byte)(108);
         var10002[1] = (byte)(-37);
         var10002[2] = (byte)(85);
         var10002[3] = (byte)(-112);
         var10002[4] = (byte)(74);
         var10002[5] = (byte)(98);
         var10002[6] = (byte)(89);
         RAINBOW = new CustomCrosshair.ColorMode();
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

   public enum Style {
      VIE_LOGO,
      VIE_WITH_CROSS,
      VIE_DOT,
      CIRCLE,
      CIRCLE_DOT;

      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      // $VF: synthetic method
      private static CustomCrosshair.Style[] $values() {
         CustomCrosshair.Style[] var10000 = new CustomCrosshair.Style[5];
         var10000[0] = VIE_LOGO;
         var10000[1] = VIE_WITH_CROSS;
         var10000[2] = VIE_DOT;
         var10000[3] = CIRCLE;
         var10000[4] = CIRCLE_DOT;
         return var10000;
      }

      // $VF: Failed to inline enum fields
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      static {
         byte[] var10002 = new byte[8];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(-47);
         var10002[5] = (byte)(56);
         var10002[6] = (byte)(26);
         var10002[7] = (byte)(28);
         VIE_LOGO = new CustomCrosshair.Style();
         var10002 = new byte[14];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(-54);
         var10002[5] = (byte)(62);
         var10002[6] = (byte)(9);
         var10002[7] = (byte)(27);
         var10002[8] = (byte)(9);
         var10002[9] = (byte)(-31);
         var10002[10] = (byte)(-74);
         var10002[11] = (byte)(-71);
         var10002[12] = (byte)(-106);
         var10002[13] = (byte)(-4);
         VIE_WITH_CROSS = new CustomCrosshair.Style();
         var10002 = new byte[7];
         var10002[0] = (byte)(104);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(89);
         var10002[3] = (byte)(-127);
         var10002[4] = (byte)(-39);
         var10002[5] = (byte)(56);
         var10002[6] = (byte)(9);
         VIE_DOT = new CustomCrosshair.Style();
         var10002 = new byte[6];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(-47);
         var10002[5] = (byte)(50);
         CIRCLE = new CustomCrosshair.Style();
         var10002 = new byte[10];
         var10002[0] = (byte)(125);
         var10002[1] = (byte)(-45);
         var10002[2] = (byte)(78);
         var10002[3] = (byte)(-99);
         var10002[4] = (byte)(-47);
         var10002[5] = (byte)(50);
         var10002[6] = (byte)(2);
         var10002[7] = (byte)(23);
         var10002[8] = (byte)(25);
         var10002[9] = (byte)(-10);
         CIRCLE_DOT = new CustomCrosshair.Style();
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
