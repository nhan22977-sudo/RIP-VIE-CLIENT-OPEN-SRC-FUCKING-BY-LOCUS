package me.drake.vieclient.modules.render;

import java.nio.charset.StandardCharsets;
import me.drake.vieclient.core.module.Category;
import me.drake.vieclient.core.module.Module;
import me.drake.vieclient.core.setting.DoubleSetting;
import me.drake.vieclient.core.setting.EnumSetting;
import net.minecraft.class_10055;
import net.minecraft.class_11659;
import net.minecraft.class_12249;
import net.minecraft.class_2960;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import org.joml.Matrix4f;

public class PlayerModels extends Module {
   private final EnumSetting model = (EnumSetting)this.addSetting(
      new EnumSetting(
         "Model",
         "Character model to transform into",
         PlayerModels.CharacterModel.TUNG_TUNG_TUNG_SAHUR
      )
   );
   private final DoubleSetting scale = (DoubleSetting)this.addSetting(
      new DoubleSetting(
         "Scale",
         "Overall model scale multiplier",
         1.0,
         0.2,
         3.0,
         0.05
      )
   );
   private static final class_2960 WHITE_TEX;
   private static final float U = 0.0625F;
   private boolean sitting;
   private boolean flying;
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public PlayerModels() {
      super(
         "PlayerModels",
         "Transform your player avatar into custom 3D character models (Figura inspired)",
         Category.CUSTOMIZE
      );
   }

   public PlayerModels.CharacterModel getSelectedModel() {
      return (PlayerModels.CharacterModel)this.model.get();
   }

   public boolean shouldApplyTo(class_10055 state) {
      return this.isEnabled() && mc.field_1724 != null && state != null
         ? state.field_53528 == mc.field_1724.method_5628() || state.field_53528 == (-6969)
         : false;
   }

   public void renderCharacter(class_10055 state, class_4587 matrices, class_11659 queue) {
      PlayerModels.CharacterModel var4 = (PlayerModels.CharacterModel)this.model.get();
      int var5 = state.field_61820;
      int var6 = class_4608.field_21444;
      this.sitting = state.field_53413;
      this.flying = state.field_53411 || state.field_53412;
      float var7 = state.field_53450;
      float var8 = this.sitting ? 0.0F : (this.flying ? Math.min(state.field_53451, 0.2F) : Math.min(1.0F, state.field_53451));
      float var9 = state.field_53447;
      float var10 = state.field_53448;
      boolean var11 = state.field_53410;
      float var12 = state.field_53328;
      queue.method_73483(matrices, class_12249.method_75994(WHITE_TEX), (entry, vc) -> {
         class_4587 var12x = new class_4587();
         var12x.method_23760().method_66521(entry);
         var12x.method_22903();
         if (var11) {
            var12x.method_46416(0.0F, 0.2F, 0.0F);
         }

         float var13 = (float)Math.sin(var12 * 0.08) * 0.012F;
         var12x.method_46416(0.0F, var13, 0.0F);
         float var14 = 1.0F;
         float var15 = 1.0F;
         float var16 = 1.0F;
         switch (var4) {
            case TUNG_TUNG_TUNG_SAHUR:
               var14 = 1.0F;
               var15 = 1.0F;
               var16 = 1.0F;
               break;
            case SANTA_CLAUS:
               var14 = 1.15F;
               var15 = 1.08F;
               var16 = 1.15F;
               break;
            case DORAEMON:
               var14 = 0.65F;
               var15 = 0.65F;
               var16 = 0.65F;
               break;
            case MICKEY_MOUSE:
               var14 = 0.75F;
               var15 = 0.75F;
               var16 = 0.75F;
               break;
            case PIKACHU:
               var14 = 0.48F;
               var15 = 0.48F;
               var16 = 0.48F;
               break;
            case MINION:
               var14 = 0.6F;
               var15 = 0.6F;
               var16 = 0.6F;
               break;
            case SIMPSON:
               var14 = 1.02F;
               var15 = 1.0F;
               var16 = 1.02F;
               break;
            case SPONGEBOB:
               var14 = 0.68F;
               var15 = 0.68F;
               var16 = 0.68F;
               break;
            case PATRICK_STAR:
               var14 = 0.72F;
               var15 = 0.72F;
               var16 = 0.72F;
               break;
            case MARIO:
               var14 = 0.8F;
               var15 = 0.78F;
               var16 = 0.8F;
               break;
            case SONIC:
               var14 = 0.85F;
               var15 = 0.88F;
               var16 = 0.85F;
               break;
            case SHREK:
               var14 = 1.35F;
               var15 = 1.25F;
               var16 = 1.35F;
               break;
            case NARUTO:
               var14 = 0.94F;
               var15 = 0.94F;
               var16 = 0.94F;
               break;
            case SKIBIDI_TOILET:
               var14 = 0.96F;
               var15 = 0.94F;
               var16 = 0.96F;
         }

         float var17 = ((Double)this.scale.get()).floatValue();
         float var18 = var14 * var17;
         float var19 = var15 * var17;
         float var20 = var16 * var17;
         var12x.method_46416(0.0F, 1.5F, 0.0F);
         var12x.method_22905(var18, var19, var20);
         var12x.method_46416(0.0F, -1.5F, 0.0F);
         switch (var4) {
            case TUNG_TUNG_TUNG_SAHUR:
               this.renderTungTung(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case SANTA_CLAUS:
               this.renderSanta(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case DORAEMON:
               this.renderDoraemon(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case MICKEY_MOUSE:
               this.renderMickey(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case PIKACHU:
               this.renderPikachu(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case MINION:
               this.renderMinion(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case SIMPSON:
               this.renderSimpson(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case SPONGEBOB:
               this.renderSpongeBob(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case PATRICK_STAR:
               this.renderPatrick(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case MARIO:
               this.renderMario(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case SONIC:
               this.renderSonic(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case SHREK:
               this.renderShrek(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case NARUTO:
               this.renderNaruto(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
               break;
            case SKIBIDI_TOILET:
               this.renderSkibidiToilet(var12x, vc, var9, var10, var7, var8, var12, var5, var6);
         }

         var12x.method_22909();
      });
   }

   private void renderTungTung(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      int var10 = -7514844;
      int var11 = -10142952;
      int var12 = -2645668;
      int var13 = -14544633;
      int var14 = -11259886;
      byte var15 = -1;
      int var16 = -15658735;
      int var17 = -11927552;
      int var18 = -6264778;
      int var19 = -2041141;
      float var20 = (float)Math.sin(age * 0.4) * 0.02F;
      m.method_46416(0.0F, var20, 0.0F);
      m.method_22903();
      m.method_46416(0.0F, 1.03125F, 0.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw * 0.45F));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch * 0.25F));
      m.method_46416(0.0F, -1.03125F, 0.0F);
      box(m, vc, -1.8F, -9.6F, -0.6F, 3.6F, 1.2F, 1.2F, var11, l, o);
      box(m, vc, -1.8F, -8.6F, -0.6F, 0.9F, 1.8F, 1.2F, var11, l, o);
      box(m, vc, 0.9F, -8.6F, -0.6F, 0.9F, 1.8F, 1.2F, var11, l, o);
      float var21 = (float)Math.sin(age * 0.18 + swing * 0.4) * 0.2F;
      box(m, vc, -0.6F + var21, -11.4F, -0.4F, 1.2F, 2.0F, 0.8F, -3692691, l, o);
      box(m, vc, -0.8F + var21 * 1.3F, -13.2F, -0.5F, 1.6F, 2.0F, 1.0F, -5009582, l, o);
      box(m, vc, -3.8F, -7.0F, -3.8F, 7.6F, 0.6F, 7.6F, var11, l, o);
      box(m, vc, -3.2F, -7.05F, -3.2F, 6.4F, 0.65F, 6.4F, var12, l, o);
      box(m, vc, -1.6F, -7.1F, -1.6F, 3.2F, 0.7F, 3.2F, var10, l, o);
      box(m, vc, -3.8F, -6.5F, -3.8F, 7.6F, 23.0F, 7.6F, var10, l, o);
      box(m, vc, -3.9F, -6.5F, -3.4F, 0.25F, 23.0F, 6.8F, var11, l, o);
      box(m, vc, 3.65F, -6.5F, -3.4F, 0.25F, 23.0F, 6.8F, var11, l, o);
      box(m, vc, -3.4F, -6.5F, -3.9F, 6.8F, 23.0F, 0.25F, var11, l, o);
      box(m, vc, -3.4F, -6.5F, 3.65F, 6.8F, 23.0F, 0.25F, var11, l, o);
      box(m, vc, -4.0F, 5.0F, -1.5F, 0.35F, 2.8F, 2.8F, var11, l, o);
      box(m, vc, -4.05F, 5.5F, -1.0F, 0.38F, 1.8F, 1.8F, var13, l, o);
      box(m, vc, 3.7F, 8.0F, 0.2F, 0.35F, 2.4F, 2.4F, var11, l, o);
      box(m, vc, 3.75F, 8.5F, 0.6F, 0.38F, 1.4F, 1.4F, var13, l, o);
      box(m, vc, -0.8F, 2.0F, -4.0F, 1.6F, 13.5F, 1.2F, var13, l, o);
      box(m, vc, -0.5F, 2.5F, -3.9F, 1.0F, 12.5F, 0.6F, -15923709, l, o);
      box(m, vc, -3.8F, 16.0F, -3.8F, 7.6F, 0.6F, 7.6F, var11, l, o);
      box(m, vc, -3.2F, 16.05F, -3.2F, 6.4F, 0.5F, 6.4F, var12, l, o);
      box(m, vc, -3.3F, -5.3F, -4.25F, 2.5F, 2.5F, 0.8F, var15, l, o);
      box(m, vc, -3.4F, -5.7F, -4.3F, 2.7F, 0.6F, 0.9F, var11, l, o);
      box(m, vc, -2.7F, -4.9F, -4.4F, 1.3F, 1.5F, 0.3F, var16, l, o);
      box(m, vc, -2.4F, -5.1F, -4.45F, 0.45F, 0.45F, 0.2F, var15, l, o);
      box(m, vc, 0.8F, -5.3F, -4.25F, 2.5F, 2.5F, 0.8F, var15, l, o);
      box(m, vc, 0.7F, -5.7F, -4.3F, 2.7F, 0.6F, 0.9F, var11, l, o);
      box(m, vc, 1.4F, -4.9F, -4.4F, 1.3F, 1.5F, 0.3F, var16, l, o);
      box(m, vc, 1.7F, -5.1F, -4.45F, 0.45F, 0.45F, 0.2F, var15, l, o);
      box(m, vc, -3.5F, -6.1F, -4.2F, 2.8F, 0.6F, 0.4F, var11, l, o);
      box(m, vc, 0.7F, -6.1F, -4.2F, 2.8F, 0.6F, 0.4F, var11, l, o);
      box(m, vc, -3.5F, -2.4F, -4.1F, 7.0F, 2.6F, 0.5F, var17, l, o);
      box(m, vc, -1.2F, -0.7F, -4.25F, 2.4F, 0.8F, 0.35F, -45747, l, o);
      box(m, vc, -3.2F, -2.35F, -4.2F, 1.3F, 1.0F, 0.25F, var15, l, o);
      box(m, vc, -1.7F, -2.35F, -4.2F, 1.3F, 1.0F, 0.25F, var15, l, o);
      box(m, vc, 0.4F, -2.35F, -4.2F, 1.3F, 1.0F, 0.25F, var15, l, o);
      box(m, vc, 1.9F, -2.35F, -4.2F, 1.3F, 1.0F, 0.25F, var15, l, o);
      box(m, vc, -2.5F, -0.95F, -4.2F, 1.3F, 1.0F, 0.25F, var15, l, o);
      box(m, vc, -1.0F, -0.95F, -4.2F, 1.3F, 1.0F, 0.25F, var15, l, o);
      box(m, vc, 1.2F, -0.95F, -4.2F, 1.3F, 1.0F, 0.25F, var15, l, o);
      m.method_22903();
      m.method_46416(-0.28125F, 0.15625F, 0.0F);
      float var22 = (float)Math.sin(age * 0.3) * 0.2F;
      m.method_22907(class_7833.field_40718.rotationDegrees(16.0F));
      this.applyRightArm(m, swing, amp, -26.0F + var22 * 20.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(-10.0F));
      box(m, vc, -0.6F, 0.0F, -0.6F, 1.2F, 9.5F, 1.2F, var14, l, o);
      box(m, vc, -0.8F, 4.5F, -0.4F, 0.3F, 0.6F, 0.6F, var11, l, o);
      m.method_22903();
      m.method_46416(0.0F, 0.53125F, -0.0625F);
      m.method_22907(class_7833.field_40714.rotationDegrees(45.0F + var22 * 20.0F));
      m.method_22907(class_7833.field_40718.rotationDegrees(-8.0F));
      box(m, vc, -0.55F, -2.5F, -0.55F, 1.1F, 4.0F, 1.1F, var19, l, o);
      box(m, vc, -0.6F, -1.5F, -0.6F, 1.2F, 0.35F, 1.2F, -14606047, l, o);
      box(m, vc, -0.6F, 0.0F, -0.6F, 1.2F, 0.35F, 1.2F, -14606047, l, o);
      box(m, vc, -0.7F, -3.0F, -0.7F, 1.4F, 0.6F, 1.4F, var18, l, o);
      box(m, vc, -0.8F, 1.5F, -0.8F, 1.6F, 5.0F, 1.6F, var18, l, o);
      box(m, vc, -1.1F, 6.5F, -1.1F, 2.2F, 7.5F, 2.2F, var18, l, o);
      box(m, vc, -1.8F, 11.5F, -0.3F, 3.6F, 0.5F, 0.6F, -3155748, l, o);
      box(m, vc, -0.3F, 9.0F, -1.8F, 0.6F, 0.5F, 3.6F, -5194043, l, o);
      box(m, vc, -0.9F, 14.0F, -0.9F, 1.8F, 0.8F, 1.8F, var18, l, o);
      m.method_22909();
      m.method_22909();
      m.method_22903();
      m.method_46416(0.28125F, 0.15625F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees(-16.0F));
      this.applyLeftArm(m, swing, amp, 6.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(8.0F));
      box(m, vc, -0.6F, 0.0F, -0.6F, 1.2F, 10.0F, 1.2F, var14, l, o);
      box(m, vc, -0.7F, 9.8F, -0.7F, 1.4F, 1.4F, 1.4F, var11, l, o);
      m.method_22909();
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.1125F, 1.03125F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -0.7F, 0.0F, -0.7F, 1.4F, 6.8F, 1.4F, var14, l, o);
      box(m, vc, -1.0F, 6.5F, -2.4F, 2.0F, 1.0F, 3.4F, var11, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.1125F, 1.03125F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -0.7F, 0.0F, -0.7F, 1.4F, 6.8F, 1.4F, var14, l, o);
      box(m, vc, -1.0F, 6.5F, -2.4F, 2.0F, 1.0F, 3.4F, var11, l, o);
      m.method_22909();
   }

   private void renderSanta(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      short var10 = -13159;
      int var11 = -4179669;
      int var12 = -5688794;
      byte var13 = -1;
      int var14 = -657931;
      int var15 = -1118482;
      short var16 = -10496;
      short var17 = -3722;
      int var18 = -14935012;
      int var19 = -15261654;
      int var20 = -14777271;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -3.5F, -7.5F, -3.5F, 7.0F, 7.5F, 7.0F, var10, l, o);
      box(m, vc, -4.2F, -8.4F, -4.2F, 8.4F, 1.8F, 8.4F, var13, l, o);
      box(m, vc, -3.8F, -10.6F, -3.8F, 7.6F, 2.4F, 7.6F, var11, l, o);
      box(m, vc, -1.5F, -12.5F, -2.5F, 5.2F, 2.2F, 5.2F, var11, l, o);
      box(m, vc, 2.2F, -11.5F, -0.5F, 2.5F, 3.5F, 2.5F, var12, l, o);
      float var21 = (float)Math.sin(age * 0.12 + swing * 0.5) * 0.8F;
      box(m, vc, 2.5F, -8.2F + var21, 0.0F, 2.4F, 2.4F, 2.4F, var13, l, o);
      box(m, vc, -2.8F, -5.8F, -3.8F, 2.0F, 0.7F, 0.4F, var13, l, o);
      box(m, vc, 0.8F, -5.8F, -3.8F, 2.0F, 0.7F, 0.4F, var13, l, o);
      box(m, vc, -2.5F, -5.0F, -3.6F, 1.4F, 1.0F, 0.15F, -1, l, o);
      box(m, vc, 1.1F, -5.0F, -3.6F, 1.4F, 1.0F, 0.15F, -1, l, o);
      box(m, vc, -2.1F, -4.8F, -3.65F, 0.8F, 0.8F, 0.15F, -14931917, l, o);
      box(m, vc, 1.3F, -4.8F, -3.65F, 0.8F, 0.8F, 0.15F, -14931917, l, o);
      box(m, vc, -1.9F, -5.0F, -3.7F, 0.3F, 0.3F, 0.15F, -1, l, o);
      box(m, vc, 1.5F, -5.0F, -3.7F, 0.3F, 0.3F, 0.15F, -1, l, o);
      box(m, vc, -2.6F, -4.7F, -4.0F, 1.6F, 1.3F, 0.2F, var17, l, o);
      box(m, vc, 1.0F, -4.7F, -4.0F, 1.6F, 1.3F, 0.2F, var17, l, o);
      box(m, vc, -1.0F, -4.4F, -3.95F, 2.0F, 0.3F, 0.15F, var17, l, o);
      box(m, vc, -2.4F, -4.5F, -4.05F, 1.2F, 0.9F, 0.1F, -1998522374, l, o);
      box(m, vc, 1.2F, -4.5F, -4.05F, 1.2F, 0.9F, 0.1F, -1998522374, l, o);
      box(m, vc, -2.1F, -4.4F, -4.1F, 0.35F, 0.35F, 0.08F, var13, l, o);
      box(m, vc, 1.5F, -4.4F, -4.1F, 0.35F, 0.35F, 0.08F, var13, l, o);
      box(m, vc, -0.8F, -4.2F, -4.0F, 1.6F, 1.4F, 1.0F, -1618884, l, o);
      box(m, vc, -3.2F, -4.0F, -3.7F, 1.4F, 1.0F, 0.2F, -33925, l, o);
      box(m, vc, 1.8F, -4.0F, -3.7F, 1.4F, 1.0F, 0.2F, -33925, l, o);
      box(m, vc, -3.0F, -3.2F, -4.2F, 6.0F, 1.4F, 0.8F, var13, l, o);
      box(m, vc, -3.8F, -2.2F, -4.0F, 7.6F, 2.6F, 1.2F, var13, l, o);
      box(m, vc, -3.2F, 0.2F, -3.6F, 6.4F, 2.8F, 1.2F, var14, l, o);
      box(m, vc, -2.0F, 2.8F, -3.2F, 4.0F, 2.0F, 0.9F, var15, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -4.5F, 0.0F, -3.0F, 9.0F, 11.5F, 6.0F, var11, l, o);
      box(m, vc, -1.2F, 0.0F, -3.2F, 2.4F, 11.5F, 0.4F, var13, l, o);
      box(m, vc, -1.5F, 0.0F, -3.25F, 0.3F, 10.4F, 0.3F, var17, l, o);
      box(m, vc, 1.2F, 0.0F, -3.25F, 0.3F, 10.4F, 0.3F, var17, l, o);
      box(m, vc, -4.6F, 10.1F, -3.1F, 9.2F, 0.3F, 6.2F, var17, l, o);
      box(m, vc, -4.7F, 10.4F, -3.2F, 9.4F, 1.6F, 6.4F, var13, l, o);
      box(m, vc, -4.7F, 6.6F, -3.15F, 9.4F, 1.9F, 6.3F, var18, l, o);
      box(m, vc, -1.8F, 6.4F, -3.45F, 3.6F, 2.3F, 0.4F, var16, l, o);
      box(m, vc, -1.0F, 6.8F, -3.5F, 2.0F, 1.5F, 0.5F, var18, l, o);
      box(m, vc, -0.2F, 7.1F, -3.52F, 0.4F, 0.9F, 0.55F, var17, l, o);
      box(m, vc, -3.5F, 1.0F, 2.8F, 7.0F, 9.5F, 4.5F, var20, l, o);
      box(m, vc, -2.0F, -0.4F, 3.2F, 4.0F, 1.8F, 3.5F, var16, l, o);
      box(m, vc, -1.0F, -2.0F, 3.6F, 2.2F, 2.0F, 2.2F, -1618884, l, o);
      box(m, vc, -1.05F, -1.6F, 3.55F, 2.3F, 0.4F, 2.3F, var17, l, o);
      box(m, vc, 1.2F, -3.2F, 3.8F, 0.8F, 3.8F, 0.8F, var13, l, o);
      box(m, vc, 1.15F, -2.6F, 3.75F, 0.9F, 0.6F, 0.9F, var12, l, o);
      box(m, vc, 1.15F, -1.4F, 3.75F, 0.9F, 0.6F, 0.9F, var12, l, o);
      box(m, vc, 1.15F, -0.2F, 3.75F, 0.9F, 0.6F, 0.9F, var12, l, o);
      box(m, vc, 0.4F, -4.0F, 3.8F, 1.6F, 0.8F, 0.8F, var12, l, o);
      box(m, vc, 0.4F, -3.2F, 3.8F, 0.8F, 0.8F, 0.8F, var13, l, o);
      m.method_22909();
      this.renderSantaArms(m, vc, swing, amp, var11, var13, var19, l, o);
      this.renderSantaBoots(m, vc, swing, amp, var11, var19, var13, var16, l, o);
   }

   private void renderDoraemon(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      int var10 = -16736537;
      byte var11 = -1;
      int var12 = -1630941;
      short var13 = -10496;
      int var14 = -6724331;
      int var15 = -14935012;
      int var16 = -4179669;
      int var17 = -33902;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -4.5F, -9.0F, -4.0F, 9.0F, 9.0F, 8.0F, var10, l, o);
      box(m, vc, -3.8F, -9.8F, -3.3F, 7.6F, 1.0F, 6.6F, var10, l, o);
      box(m, vc, -4.9F, -8.3F, -3.5F, 9.8F, 7.6F, 7.0F, var10, l, o);
      float var18 = age * 24.0F;
      box(m, vc, -0.7F, -10.4F, -0.7F, 1.4F, 0.6F, 1.4F, var12, l, o);
      box(m, vc, -0.3F, -13.0F, -0.3F, 0.6F, 2.6F, 0.6F, var13, l, o);
      m.method_22903();
      m.method_46416(0.0F, -0.8125F, 0.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(var18));
      box(m, vc, -0.7F, -0.5F, -0.7F, 1.4F, 0.5F, 1.4F, -932849, l, o);
      box(m, vc, -6.5F, -0.4F, -0.8F, 13.0F, 0.3F, 1.6F, -812014, l, o);
      m.method_22909();
      box(m, vc, -3.8F, -7.5F, -4.2F, 7.6F, 7.0F, 0.4F, var11, l, o);
      box(m, vc, -3.2F, -0.8F, -4.15F, 6.4F, 0.8F, 0.35F, var11, l, o);
      box(m, vc, -2.5F, -7.8F, -4.35F, 2.3F, 3.0F, 0.25F, var11, l, o);
      box(m, vc, 0.2F, -7.8F, -4.35F, 2.3F, 3.0F, 0.25F, var11, l, o);
      box(m, vc, -1.2F, -6.6F, -4.42F, 0.8F, 1.5F, 0.2F, var15, l, o);
      box(m, vc, 0.4F, -6.6F, -4.42F, 0.8F, 1.5F, 0.2F, var15, l, o);
      box(m, vc, -1.0F, -6.8F, -4.45F, 0.35F, 0.45F, 0.2F, var11, l, o);
      box(m, vc, 0.6F, -6.8F, -4.45F, 0.35F, 0.45F, 0.2F, var11, l, o);
      box(m, vc, -0.85F, -5.2F, -4.6F, 1.7F, 1.7F, 1.0F, var12, l, o);
      box(m, vc, -0.35F, -5.0F, -4.68F, 0.45F, 0.45F, 0.15F, var11, l, o);
      box(m, vc, -0.15F, -3.5F, -4.25F, 0.3F, 1.8F, 0.15F, var15, l, o);
      box(m, vc, -2.6F, -2.8F, -4.26F, 5.2F, 2.2F, 0.15F, var16, l, o);
      box(m, vc, -1.5F, -1.6F, -4.28F, 3.0F, 1.0F, 0.15F, var17, l, o);
      box(m, vc, -3.6F, -4.4F, -4.25F, 1.8F, 0.25F, 0.15F, var15, l, o);
      box(m, vc, -3.7F, -3.4F, -4.25F, 1.9F, 0.25F, 0.15F, var15, l, o);
      box(m, vc, -3.6F, -2.4F, -4.25F, 1.8F, 0.25F, 0.15F, var15, l, o);
      box(m, vc, 1.8F, -4.4F, -4.25F, 1.8F, 0.25F, 0.15F, var15, l, o);
      box(m, vc, 1.8F, -3.4F, -4.25F, 1.9F, 0.25F, 0.15F, var15, l, o);
      box(m, vc, 1.8F, -2.4F, -4.25F, 1.8F, 0.25F, 0.15F, var15, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -4.2F, -0.6F, -3.6F, 8.4F, 1.0F, 7.2F, var12, l, o);
      float var19 = (float)Math.sin(age * 0.18 + swing) * 0.4F;
      box(m, vc, -1.1F + var19, 0.2F, -4.2F, 2.2F, 2.2F, 1.8F, var13, l, o);
      box(m, vc, -1.15F + var19, 0.9F, -4.25F, 2.3F, 0.35F, 1.9F, var14, l, o);
      box(m, vc, -0.35F + var19, 1.4F, -4.3F, 0.7F, 0.7F, 0.2F, var15, l, o);
      box(m, vc, -0.3F + var19, 1.6F, -4.25F, 0.6F, 0.6F, 0.3F, -10665929, l, o);
      box(m, vc, -4.8F, 0.0F, -3.4F, 9.6F, 18.0F, 6.8F, var10, l, o);
      box(m, vc, -3.8F, 2.0F, -3.65F, 7.6F, 12.0F, 0.4F, var11, l, o);
      box(m, vc, -3.0F, 6.5F, -3.85F, 6.0F, 5.0F, 0.35F, var11, l, o);
      box(m, vc, -3.1F, 6.3F, -3.9F, 6.2F, 0.35F, 0.25F, -11908534, l, o);
      box(m, vc, 0.4F, 5.0F, -4.1F, 2.6F, 1.8F, 0.7F, -3769036, l, o);
      box(m, vc, 0.6F, 5.4F, -4.15F, 2.2F, 0.6F, 0.75F, -11922155, l, o);
      box(m, vc, -0.3F, 15.0F, 3.4F, 0.6F, 0.6F, 1.0F, -5194043, l, o);
      box(m, vc, -1.0F, 14.3F, 4.2F, 2.0F, 2.0F, 2.0F, var12, l, o);
      m.method_22909();
      this.renderDoraemonArms(m, vc, swing, amp, var10, var11, l, o);
      this.renderDoraemonFeet(m, vc, swing, amp, var10, var11, l, o);
   }

   private void renderMickey(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      int var10 = -15658735;
      int var11 = -16250872;
      short var12 = -11862;
      int var13 = -2054014;
      short var14 = -31339;
      int var15 = -1759967;
      int var16 = -4776932;
      byte var17 = -1;
      short var18 = -13312;
      int var19 = -1664768;
      int var20 = -10679788;
      int var21 = -39548;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -3.5F, -7.5F, -3.5F, 7.0F, 7.5F, 7.0F, var10, l, o);
      box(m, vc, -3.0F, -8.2F, -3.0F, 6.0F, 0.8F, 6.0F, var10, l, o);
      box(m, vc, -3.8F, -7.0F, -3.0F, 7.6F, 6.2F, 6.0F, var10, l, o);
      box(m, vc, -3.0F, -7.0F, 3.2F, 6.0F, 6.2F, 0.6F, var10, l, o);
      m.method_22903();
      m.method_46416(-0.2F, -0.46875F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees(-16.0F));
      m.method_22907(class_7833.field_40716.rotationDegrees(-8.0F));
      box(m, vc, -2.6F, -4.8F, -0.35F, 5.2F, 5.2F, 0.7F, var10, l, o);
      box(m, vc, -3.2F, -4.2F, -0.35F, 6.4F, 4.0F, 0.7F, var10, l, o);
      box(m, vc, -2.0F, -5.4F, -0.35F, 4.0F, 6.4F, 0.7F, var10, l, o);
      box(m, vc, -2.0F, -4.2F, -0.42F, 4.0F, 4.0F, 0.15F, var11, l, o);
      box(m, vc, -1.4F, -4.6F, -0.42F, 2.8F, 4.8F, 0.15F, var11, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.2F, -0.46875F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees(16.0F));
      m.method_22907(class_7833.field_40716.rotationDegrees(8.0F));
      box(m, vc, -2.6F, -4.8F, -0.35F, 5.2F, 5.2F, 0.7F, var10, l, o);
      box(m, vc, -3.2F, -4.2F, -0.35F, 6.4F, 4.0F, 0.7F, var10, l, o);
      box(m, vc, -2.0F, -5.4F, -0.35F, 4.0F, 6.4F, 0.7F, var10, l, o);
      box(m, vc, -2.0F, -4.2F, -0.42F, 4.0F, 4.0F, 0.15F, var11, l, o);
      box(m, vc, -1.4F, -4.6F, -0.42F, 2.8F, 4.8F, 0.15F, var11, l, o);
      m.method_22909();
      box(m, vc, -3.0F, -6.8F, -3.7F, 6.0F, 3.2F, 0.35F, var12, l, o);
      box(m, vc, -3.4F, -5.0F, -3.8F, 6.8F, 3.6F, 0.45F, var12, l, o);
      box(m, vc, -1.4F, -7.3F, -3.65F, 2.8F, 0.8F, 0.3F, var12, l, o);
      box(m, vc, -3.5F, -4.2F, -3.9F, 1.4F, 1.4F, 0.2F, var14, l, o);
      box(m, vc, 2.1F, -4.2F, -3.9F, 1.4F, 1.4F, 0.2F, var14, l, o);
      box(m, vc, -2.4F, -6.6F, -3.9F, 1.8F, 0.4F, 0.15F, var10, l, o);
      box(m, vc, 0.6F, -6.6F, -3.9F, 1.8F, 0.4F, 0.15F, var10, l, o);
      box(m, vc, -2.1F, -6.1F, -3.95F, 1.8F, 2.5F, 0.2F, var17, l, o);
      box(m, vc, 0.3F, -6.1F, -3.95F, 1.8F, 2.5F, 0.2F, var17, l, o);
      box(m, vc, -1.4F, -5.5F, -4.02F, 1.0F, 1.7F, 0.15F, var10, l, o);
      box(m, vc, 0.4F, -5.5F, -4.02F, 1.0F, 1.7F, 0.15F, var10, l, o);
      box(m, vc, -1.2F, -5.8F, -4.08F, 0.4F, 0.5F, 0.12F, var17, l, o);
      box(m, vc, 0.6F, -5.8F, -4.08F, 0.4F, 0.5F, 0.12F, var17, l, o);
      box(m, vc, -2.2F, -3.5F, -5.0F, 4.4F, 2.2F, 1.8F, var12, l, o);
      box(m, vc, -1.5F, -3.8F, -4.8F, 3.0F, 0.6F, 1.4F, var12, l, o);
      box(m, vc, -1.0F, -4.2F, -5.5F, 2.0F, 1.4F, 1.2F, -16119286, l, o);
      box(m, vc, -0.4F, -4.3F, -5.58F, 0.6F, 0.45F, 0.15F, var17, l, o);
      box(m, vc, -0.6F, -3.0F, -5.2F, 1.2F, 0.25F, 0.6F, var13, l, o);
      box(m, vc, -2.4F, -2.3F, -4.4F, 4.8F, 2.0F, 0.7F, var20, l, o);
      box(m, vc, -1.6F, -2.25F, -4.45F, 3.2F, 0.35F, 0.35F, var17, l, o);
      box(m, vc, -1.4F, -1.4F, -4.48F, 2.8F, 0.9F, 0.55F, var21, l, o);
      box(m, vc, -0.2F, -1.45F, -4.52F, 0.4F, 0.9F, 0.25F, -2076560, l, o);
      box(m, vc, -1.6F, -0.3F, -4.0F, 3.2F, 0.8F, 0.8F, var12, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -3.5F, 0.0F, -1.8F, 7.0F, 4.8F, 3.6F, var10, l, o);
      box(m, vc, -2.8F, -0.5F, -1.5F, 5.6F, 0.8F, 3.0F, var10, l, o);
      box(m, vc, -3.9F, 4.6F, -2.3F, 7.8F, 1.0F, 4.6F, var16, l, o);
      box(m, vc, -3.8F, 5.4F, -2.2F, 7.6F, 5.4F, 4.4F, var15, l, o);
      box(m, vc, -3.85F, 5.4F, -0.4F, 7.7F, 5.4F, 0.8F, var16, l, o);
      box(m, vc, -3.7F, 10.4F, -2.1F, 3.5F, 1.2F, 4.2F, var15, l, o);
      box(m, vc, 0.2F, 10.4F, -2.1F, 3.5F, 1.2F, 4.2F, var15, l, o);
      box(m, vc, -2.5F, 6.2F, -2.45F, 1.7F, 2.7F, 0.35F, var17, l, o);
      box(m, vc, -2.3F, 6.4F, -2.48F, 1.3F, 2.3F, 0.15F, -1118482, l, o);
      box(m, vc, -1.9F, 6.8F, -2.52F, 0.35F, 0.45F, 0.15F, -9079435, l, o);
      box(m, vc, -1.9F, 7.7F, -2.52F, 0.35F, 0.45F, 0.15F, -9079435, l, o);
      box(m, vc, 0.8F, 6.2F, -2.45F, 1.7F, 2.7F, 0.35F, var17, l, o);
      box(m, vc, 1.0F, 6.4F, -2.48F, 1.3F, 2.3F, 0.15F, -1118482, l, o);
      box(m, vc, 1.55F, 6.8F, -2.52F, 0.35F, 0.45F, 0.15F, -9079435, l, o);
      box(m, vc, 1.55F, 7.7F, -2.52F, 0.35F, 0.45F, 0.15F, -9079435, l, o);
      box(m, vc, -2.5F, 6.2F, 2.15F, 1.7F, 2.7F, 0.35F, var17, l, o);
      box(m, vc, 0.8F, 6.2F, 2.15F, 1.7F, 2.7F, 0.35F, var17, l, o);
      m.method_22903();
      m.method_46416(0.0F, 0.55F, 0.13125F);

      for (int var22 = 0; var22 < 6; var22++) {
         float var23 = (float)Math.sin(age * 0.22 + swing * 0.6 + var22 * 0.65) * 0.22F;
         float var24 = (float)Math.cos(age * 0.18 + var22 * 0.55) * 0.12F;
         m.method_22907(class_7833.field_40716.rotation(var23));
         m.method_22907(class_7833.field_40714.rotation(var24));
         float var25 = 0.65F - var22 * 0.08F;
         box(m, vc, -var25 * 0.5F, 0.0F, 0.0F, var25, var25, 1.5F, var10, l, o);
         m.method_46416(0.0F, 0.0F, 0.084375F);
      }

      m.method_22909();
      m.method_22909();
      this.renderMickeyArms(m, vc, swing, amp, var10, var17, l, o);
      m.method_22903();
      m.method_46416(-0.28125F, 0.65625F, -0.125F);
      this.applyRightArm(m, swing, amp);
      m.method_22907(class_7833.field_40714.rotationDegrees(35.0F));
      m.method_22907(class_7833.field_40718.rotationDegrees(-15.0F));
      int var27 = -12703965;
      short var28 = -10496;
      short var29 = -3722;
      byte var30 = -1;
      box(m, vc, -0.3F, -5.5F, -0.3F, 0.6F, 8.5F, 0.6F, var27, l, o);
      box(m, vc, -0.4F, -5.7F, -0.4F, 0.8F, 0.5F, 0.8F, var28, l, o);
      box(m, vc, -0.4F, 2.5F, -0.4F, 0.8F, 0.4F, 0.8F, var28, l, o);
      box(m, vc, -0.5F, 2.9F, -0.5F, 1.0F, 0.6F, 1.0F, var28, l, o);
      m.method_22903();
      m.method_46416(0.0F, -0.425F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees((float)Math.sin(age * 0.1) * 8.0F));
      box(m, vc, -1.0F, -1.0F, -0.4F, 2.0F, 2.0F, 0.8F, var29, l, o);
      box(m, vc, -0.5F, -0.5F, -0.45F, 1.0F, 1.0F, 0.9F, var30, l, o);
      box(m, vc, -0.5F, -2.4F, -0.35F, 1.0F, 1.5F, 0.7F, var28, l, o);
      box(m, vc, -2.4F, -0.5F, -0.35F, 1.5F, 1.0F, 0.7F, var28, l, o);
      box(m, vc, 0.9F, -0.5F, -0.35F, 1.5F, 1.0F, 0.7F, var28, l, o);
      box(m, vc, -1.8F, 0.7F, -0.35F, 1.2F, 1.4F, 0.7F, var28, l, o);
      box(m, vc, 0.6F, 0.7F, -0.35F, 1.2F, 1.4F, 0.7F, var28, l, o);
      float var26 = age * 6.0F;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(var26));
      m.method_46416(0.1375F, 0.0F, 0.0F);
      box(m, vc, -0.25F, -0.25F, -0.25F, 0.5F, 0.5F, 0.5F, -8333057, l, o);
      m.method_22909();
      m.method_22903();
      m.method_22907(class_7833.field_40718.rotationDegrees(-var26 * 0.85F));
      m.method_46416(0.0F, 0.125F, 0.0F);
      box(m, vc, -0.25F, -0.25F, -0.25F, 0.5F, 0.5F, 0.5F, -4523, l, o);
      m.method_22909();
      m.method_22903();
      m.method_22907(class_7833.field_40714.rotationDegrees(var26 * 1.2F));
      m.method_46416(0.0F, 0.0F, 0.1125F);
      box(m, vc, -0.2F, -0.2F, -0.2F, 0.4F, 0.4F, 0.4F, var17, l, o);
      m.method_22909();
      m.method_22909();
      m.method_22909();
      this.renderMickeyShoes(m, vc, swing, amp, var10, var18, var19, l, o);
   }

   private void renderPikachu(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      short var10 = -11776;
      int var11 = -1762012;
      int var12 = -15000805;
      int var13 = -9554410;
      byte var14 = -1;
      int var15 = -6743260;
      int var16 = -33909;
      int var17 = -3037184;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -4.5F, -6.5F, -4.0F, 9.0F, 8.5F, 8.0F, var10, l, o);
      box(m, vc, -5.2F, -3.0F, -3.8F, 10.4F, 4.5F, 7.6F, var10, l, o);
      float var18 = (float)Math.sin(age * 0.18) * 4.0F;
      float var19 = (float)Math.cos(age * 0.16) * 3.5F;
      m.method_22903();
      m.method_46416(-0.28125F, -0.40625F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees(-20.0F + var18));
      box(m, vc, -1.0F, -4.5F, -0.6F, 2.0F, 4.5F, 1.4F, var10, l, o);
      box(m, vc, -1.0F, -8.5F, -0.6F, 2.0F, 4.0F, 1.4F, var12, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.28125F, -0.40625F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees(20.0F - var19));
      box(m, vc, -1.0F, -4.5F, -0.6F, 2.0F, 4.5F, 1.4F, var10, l, o);
      box(m, vc, -1.0F, -8.5F, -0.6F, 2.0F, 4.0F, 1.4F, var12, l, o);
      m.method_22909();
      box(m, vc, -5.3F, -2.2F, -3.2F, 0.4F, 2.8F, 2.8F, var11, l, o);
      box(m, vc, -5.0F, -2.2F, -4.15F, 2.0F, 2.8F, 0.25F, var11, l, o);
      box(m, vc, 4.9F, -2.2F, -3.2F, 0.4F, 2.8F, 2.8F, var11, l, o);
      box(m, vc, 3.0F, -2.2F, -4.15F, 2.0F, 2.8F, 0.25F, var11, l, o);
      float var20 = (float)Math.abs(Math.sin(age * 0.45));
      if (var20 > 0.35F) {
         int var21 = -8333057;
         short var22 = -256;
         m.method_22903();
         m.method_46416(-0.3625F, -0.09375F, -0.175F);
         m.method_22907(class_7833.field_40718.rotationDegrees(age * 15.0F));
         box(m, vc, -0.6F, -0.2F, 0.0F, 1.2F, 0.4F, 0.4F, var22, l, o);
         box(m, vc, -0.2F, -0.6F, 0.0F, 0.4F, 1.2F, 0.4F, var21, l, o);
         m.method_22909();
         m.method_22903();
         m.method_46416(0.3625F, -0.09375F, -0.175F);
         m.method_22907(class_7833.field_40718.rotationDegrees(-age * 17.0F));
         box(m, vc, -0.6F, -0.2F, 0.0F, 1.2F, 0.4F, 0.4F, var22, l, o);
         box(m, vc, -0.2F, -0.6F, 0.0F, 0.4F, 1.2F, 0.4F, var21, l, o);
         m.method_22909();
      }

      box(m, vc, -2.8F, -4.8F, -4.15F, 1.8F, 2.0F, 0.25F, var12, l, o);
      box(m, vc, 1.0F, -4.8F, -4.15F, 1.8F, 2.0F, 0.25F, var12, l, o);
      box(m, vc, -2.4F, -4.9F, -4.22F, 0.7F, 0.7F, 0.15F, var14, l, o);
      box(m, vc, 1.4F, -4.9F, -4.22F, 0.7F, 0.7F, 0.15F, var14, l, o);
      box(m, vc, -0.35F, -3.2F, -4.2F, 0.7F, 0.5F, 0.2F, var12, l, o);
      box(m, vc, -1.4F, -1.8F, -4.15F, 2.8F, 1.6F, 0.2F, var15, l, o);
      box(m, vc, -0.9F, -1.0F, -4.2F, 1.8F, 0.7F, 0.2F, var16, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -4.8F, 1.5F, -3.8F, 9.6F, 18.5F, 7.6F, var10, l, o);
      box(m, vc, -4.2F, 4.0F, -4.6F, 8.4F, 14.0F, 1.2F, var10, l, o);
      box(m, vc, -4.2F, 6.0F, 3.65F, 8.4F, 2.0F, 0.4F, var13, l, o);
      box(m, vc, -4.2F, 10.5F, 3.65F, 8.4F, 2.0F, 0.4F, var13, l, o);
      float var23 = (float)Math.sin(age * 0.16 + swing * 0.5) * 0.35F;
      m.method_22903();
      m.method_46416(0.0F, 0.90625F, 0.225F);
      m.method_22907(class_7833.field_40716.rotation(var23));
      box(m, vc, -0.8F, 0.0F, 0.0F, 1.6F, 2.5F, 1.4F, var13, l, o);
      box(m, vc, -0.3F, -2.8F, 0.8F, 3.2F, 2.0F, 0.7F, var13, l, o);
      box(m, vc, -3.2F, -6.0F, 1.2F, 3.6F, 2.4F, 0.7F, var10, l, o);
      box(m, vc, -0.6F, -9.5F, 1.7F, 4.5F, 2.8F, 0.7F, var10, l, o);
      box(m, vc, -1.8F, -14.5F, 2.2F, 7.0F, 4.6F, 0.7F, var10, l, o);
      if (var20 > 0.4F) {
         box(m, vc, -2.6F, -16.0F, 2.0F, 1.0F, 1.0F, 0.6F, -8333057, l, o);
         box(m, vc, 3.8F, -13.5F, 2.0F, 1.0F, 1.0F, 0.6F, -256, l, o);
      }

      m.method_22909();
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.1625F, 0.53125F, -0.2375F);
      this.applyRightArm(m, swing, amp, -17.0F);
      box(m, vc, -1.2F, -1.0F, -3.2F, 2.4F, 2.4F, 4.0F, var10, l, o);
      box(m, vc, -1.1F, 0.4F, -3.6F, 0.6F, 0.8F, 0.6F, var17, l, o);
      box(m, vc, -0.3F, 0.4F, -3.7F, 0.6F, 0.8F, 0.7F, var17, l, o);
      box(m, vc, 0.5F, 0.4F, -3.6F, 0.6F, 0.8F, 0.6F, var17, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.1625F, 0.53125F, -0.2375F);
      this.applyLeftArm(m, swing, amp, -17.0F);
      box(m, vc, -1.2F, -1.0F, -3.2F, 2.4F, 2.4F, 4.0F, var10, l, o);
      box(m, vc, -1.1F, 0.4F, -3.6F, 0.6F, 0.8F, 0.6F, var17, l, o);
      box(m, vc, -0.3F, 0.4F, -3.7F, 0.6F, 0.8F, 0.7F, var17, l, o);
      box(m, vc, 0.5F, 0.4F, -3.6F, 0.6F, 0.8F, 0.6F, var17, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.2F, 1.25F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -1.8F, 0.0F, -3.0F, 3.6F, 4.0F, 5.5F, var10, l, o);
      box(m, vc, -1.5F, 2.8F, -3.6F, 0.8F, 1.2F, 0.8F, var17, l, o);
      box(m, vc, -0.4F, 2.8F, -3.7F, 0.8F, 1.2F, 0.9F, var17, l, o);
      box(m, vc, 0.7F, 2.8F, -3.6F, 0.8F, 1.2F, 0.8F, var17, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.2F, 1.25F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -1.8F, 0.0F, -3.0F, 3.6F, 4.0F, 5.5F, var10, l, o);
      box(m, vc, -1.5F, 2.8F, -3.6F, 0.8F, 1.2F, 0.8F, var17, l, o);
      box(m, vc, -0.4F, 2.8F, -3.7F, 0.8F, 1.2F, 0.9F, var17, l, o);
      box(m, vc, 0.7F, 2.8F, -3.6F, 0.8F, 1.2F, 0.8F, var17, l, o);
      m.method_22909();
   }

   private void renderMinion(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      short var10 = -11776;
      int var11 = -14527068;
      int var12 = -15055742;
      int var13 = -3155748;
      int var14 = -7297874;
      int var15 = -14935012;
      int var16 = -8825528;
      int var17 = -15658735;
      byte var18 = -1;
      int var19 = -9614271;
      int var20 = -12232092;
      m.method_22903();
      m.method_46416(0.0F, 0.3125F, 0.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw * 0.7F));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch * 0.5F));
      m.method_46416(0.0F, -0.3125F, 0.0F);
      box(m, vc, -3.8F, -7.5F, -3.8F, 7.6F, 3.0F, 7.6F, var10, l, o);
      box(m, vc, -3.0F, -8.6F, -3.0F, 6.0F, 1.2F, 6.0F, var10, l, o);
      float var21 = (float)Math.sin(age * 0.15) * 0.3F;
      box(m, vc, -1.2F + var21, -10.4F, -0.3F, 0.4F, 2.0F, 0.4F, var17, l, o);
      box(m, vc, -0.2F - var21 * 0.5F, -10.8F, 0.2F, 0.4F, 2.4F, 0.4F, var17, l, o);
      box(m, vc, 0.8F + var21 * 0.8F, -10.4F, -0.4F, 0.4F, 2.0F, 0.4F, var17, l, o);
      box(m, vc, -4.5F, -4.5F, -4.5F, 9.0F, 9.5F, 9.0F, var10, l, o);
      box(m, vc, -4.7F, -2.8F, -4.7F, 9.4F, 1.8F, 9.4F, var15, l, o);
      box(m, vc, -4.95F, -2.5F, -0.8F, 0.45F, 1.2F, 1.6F, var13, l, o);
      box(m, vc, 4.5F, -2.5F, -0.8F, 0.45F, 1.2F, 1.6F, var13, l, o);
      box(m, vc, -2.8F, -3.6F, -5.3F, 5.6F, 3.6F, 0.8F, var13, l, o);
      box(m, vc, -2.4F, -3.2F, -5.6F, 4.8F, 2.8F, 0.5F, var14, l, o);
      box(m, vc, -2.2F, -3.0F, -5.35F, 4.4F, 2.4F, 0.15F, -1999644452, l, o);
      box(m, vc, -2.6F, -3.4F, -5.7F, 0.5F, 0.5F, 0.2F, -13154481, l, o);
      box(m, vc, 2.1F, -3.4F, -5.7F, 0.5F, 0.5F, 0.2F, -13154481, l, o);
      box(m, vc, -2.0F, -2.8F, -5.2F, 4.0F, 2.0F, 0.2F, var18, l, o);
      box(m, vc, -0.9F, -2.5F, -5.25F, 1.8F, 1.5F, 0.15F, var16, l, o);
      box(m, vc, -0.55F, -2.2F, -5.3F, 1.1F, 1.0F, 0.15F, var17, l, o);
      box(m, vc, -0.3F, -2.3F, -5.35F, 0.4F, 0.4F, 0.15F, var18, l, o);
      box(m, vc, -1.8F, 2.2F, -4.75F, 3.6F, 1.2F, 0.25F, var19, l, o);
      box(m, vc, -1.3F, 2.3F, -4.8F, 2.6F, 0.5F, 0.15F, var18, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -4.5F, 5.0F, -4.5F, 9.0F, 4.0F, 9.0F, var10, l, o);
      box(m, vc, -4.6F, 9.0F, -4.6F, 9.2F, 8.5F, 9.2F, var11, l, o);
      box(m, vc, -3.4F, 5.5F, -4.8F, 6.8F, 4.0F, 0.35F, var11, l, o);
      box(m, vc, -3.4F, 5.5F, 4.45F, 6.8F, 4.0F, 0.35F, var11, l, o);
      short var22 = -10496;
      box(m, vc, -3.45F, 5.3F, -4.85F, 6.9F, 0.25F, 0.15F, var22, l, o);
      box(m, vc, -3.45F, 5.3F, -4.85F, 0.25F, 4.0F, 0.15F, var22, l, o);
      box(m, vc, 3.2F, 5.3F, -4.85F, 0.25F, 4.0F, 0.15F, var22, l, o);
      box(m, vc, -3.8F, 3.5F, -4.85F, 1.2F, 3.0F, 9.7F, var12, l, o);
      box(m, vc, 2.6F, 3.5F, -4.85F, 1.2F, 3.0F, 9.7F, var12, l, o);
      box(m, vc, -3.5F, 5.8F, -4.95F, 0.6F, 0.6F, 0.2F, var17, l, o);
      box(m, vc, 2.9F, 5.8F, -4.95F, 0.6F, 0.6F, 0.2F, var17, l, o);
      box(m, vc, -1.8F, 6.5F, -4.9F, 3.6F, 2.4F, 0.25F, var12, l, o);
      box(m, vc, -1.85F, 6.4F, -4.95F, 3.7F, 0.2F, 0.12F, var22, l, o);
      box(m, vc, -0.9F, 6.9F, -5.0F, 1.8F, 1.6F, 0.15F, var17, l, o);
      box(m, vc, -0.4F, 7.3F, -5.05F, 0.9F, 0.5F, 0.15F, var11, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.3125F, 0.3125F, 0.0F);
      this.applyRightArm(m, swing, amp);
      box(m, vc, -1.2F, -1.0F, -1.2F, 2.4F, 8.5F, 2.4F, var10, l, o);
      box(m, vc, -1.5F, 7.0F, -1.5F, 3.0F, 3.8F, 3.0F, var17, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.3125F, 0.3125F, 0.0F);
      this.applyLeftArm(m, swing, amp);
      box(m, vc, -1.2F, -1.0F, -1.2F, 2.4F, 8.5F, 2.4F, var10, l, o);
      box(m, vc, -1.5F, 7.0F, -1.5F, 3.0F, 3.8F, 3.0F, var17, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.1375F, 1.09375F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -1.6F, 0.0F, -1.6F, 3.2F, 3.5F, 3.2F, var11, l, o);
      box(m, vc, -2.1F, 3.5F, -3.0F, 4.2F, 2.2F, 5.2F, var17, l, o);
      box(m, vc, -2.2F, 5.5F, -3.2F, 4.4F, 1.0F, 5.6F, var20, l, o);
      box(m, vc, -1.8F, 6.2F, -2.6F, 3.6F, 0.35F, 1.2F, -14606047, l, o);
      box(m, vc, -1.8F, 6.2F, -0.6F, 3.6F, 0.35F, 1.2F, -14606047, l, o);
      box(m, vc, -1.8F, 6.2F, 1.2F, 3.6F, 0.35F, 1.2F, -14606047, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.1375F, 1.09375F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -1.6F, 0.0F, -1.6F, 3.2F, 3.5F, 3.2F, var11, l, o);
      box(m, vc, -2.1F, 3.5F, -3.0F, 4.2F, 2.2F, 5.2F, var17, l, o);
      box(m, vc, -2.2F, 5.5F, -3.2F, 4.4F, 1.0F, 5.6F, var20, l, o);
      box(m, vc, -1.8F, 6.2F, -2.6F, 3.6F, 0.35F, 1.2F, -14606047, l, o);
      box(m, vc, -1.8F, 6.2F, -0.6F, 3.6F, 0.35F, 1.2F, -14606047, l, o);
      box(m, vc, -1.8F, 6.2F, 1.2F, 3.6F, 0.35F, 1.2F, -14606047, l, o);
      m.method_22909();
   }

   private void renderSimpson(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      short var10 = -9969;
      int var11 = -3038893;
      int var12 = -7511512;
      int var13 = -10666210;
      int var14 = -33909;
      int var15 = -131330;
      int var16 = -13736795;
      int var17 = -14397566;
      int var18 = -12434878;
      int var19 = -14935012;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -3.5F, -8.0F, -3.5F, 7.0F, 8.0F, 7.0F, var10, l, o);
      box(m, vc, -3.0F, -8.8F, -3.0F, 6.0F, 0.9F, 6.0F, var10, l, o);
      float var20 = (float)Math.sin(age * 0.14) * 0.25F;
      box(m, vc, -0.8F + var20, -9.4F, -1.0F, 1.6F, 0.7F, 0.3F, var19, l, o);
      box(m, vc, -0.8F - var20, -9.4F, 1.0F, 1.6F, 0.7F, 0.3F, var19, l, o);
      box(m, vc, -3.6F, -4.5F, -0.6F, 0.2F, 1.6F, 1.8F, var19, l, o);
      box(m, vc, 3.4F, -4.5F, -0.6F, 0.2F, 1.6F, 1.8F, var19, l, o);
      box(m, vc, -3.8F, -3.6F, -0.4F, 0.4F, 1.4F, 1.2F, var10, l, o);
      box(m, vc, 3.4F, -3.6F, -0.4F, 0.4F, 1.4F, 1.2F, var10, l, o);
      box(m, vc, -2.5F, -5.8F, -4.2F, 2.4F, 2.6F, 0.9F, var15, l, o);
      box(m, vc, 0.1F, -5.8F, -4.2F, 2.4F, 2.6F, 0.9F, var15, l, o);
      box(m, vc, -1.4F, -4.7F, -4.3F, 0.6F, 0.6F, 0.2F, var19, l, o);
      box(m, vc, 0.8F, -4.7F, -4.3F, 0.6F, 0.6F, 0.2F, var19, l, o);
      box(m, vc, -0.8F, -4.0F, -4.9F, 1.6F, 1.3F, 1.2F, var10, l, o);
      box(m, vc, -2.8F, -3.2F, -4.5F, 5.6F, 3.2F, 1.4F, var11, l, o);
      box(m, vc, -2.4F, -2.6F, -4.55F, 0.4F, 0.4F, 0.1F, var13, l, o);
      box(m, vc, 2.0F, -2.6F, -4.55F, 0.4F, 0.4F, 0.1F, var13, l, o);
      box(m, vc, -1.8F, -0.6F, -4.55F, 0.4F, 0.4F, 0.1F, var13, l, o);
      box(m, vc, 1.4F, -0.6F, -4.55F, 0.4F, 0.4F, 0.1F, var13, l, o);
      box(m, vc, -2.0F, -1.8F, -4.6F, 4.0F, 0.4F, 0.2F, var12, l, o);
      box(m, vc, -1.5F, -1.4F, -4.58F, 3.0F, 1.0F, 0.2F, var13, l, o);
      box(m, vc, -0.8F, -0.9F, -4.62F, 1.6F, 0.5F, 0.2F, var14, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -4.2F, 0.0F, -2.6F, 8.4F, 11.0F, 5.2F, var15, l, o);
      box(m, vc, -2.8F, -0.6F, -2.9F, 2.2F, 1.2F, 0.5F, -1249295, l, o);
      box(m, vc, 0.6F, -0.6F, -2.9F, 2.2F, 1.2F, 0.5F, -1249295, l, o);
      box(m, vc, -0.4F, 0.6F, -2.7F, 0.8F, 2.0F, 0.2F, var10, l, o);
      box(m, vc, -3.8F, 4.0F, -4.2F, 7.6F, 6.8F, 2.0F, var15, l, o);
      box(m, vc, -3.4F, 9.8F, -4.4F, 6.8F, 1.6F, 2.0F, var15, l, o);
      m.method_22909();
      this.renderStandardArms(m, vc, swing, amp, var15, var10, var10, l, o);
      m.method_22903();
      m.method_46416(-0.3125F, 0.6875F, -0.125F);
      this.applyRightArm(m, swing, amp);
      m.method_22907(class_7833.field_40714.rotationDegrees(25.0F));
      int var21 = -2053779;
      int var22 = -38476;
      box(m, vc, -1.6F, -1.6F, -0.8F, 3.2F, 3.2F, 1.6F, var21, l, o);
      box(m, vc, -0.6F, -0.6F, -0.9F, 1.2F, 1.2F, 1.8F, -16777216, l, o);
      box(m, vc, -1.7F, -1.7F, -1.0F, 3.4F, 3.4F, 0.35F, var22, l, o);
      box(m, vc, -1.2F, -1.2F, -1.1F, 0.4F, 0.2F, 0.15F, -16718337, l, o);
      box(m, vc, 0.6F, -1.0F, -1.1F, 0.2F, 0.4F, 0.15F, -256, l, o);
      box(m, vc, -0.8F, 0.8F, -1.1F, 0.4F, 0.2F, 0.15F, -1, l, o);
      box(m, vc, 0.7F, 0.5F, -1.1F, 0.2F, 0.4F, 0.15F, -59580, l, o);
      box(m, vc, -0.2F, -1.4F, -1.1F, 0.3F, 0.2F, 0.15F, -8978685, l, o);
      m.method_22909();
      this.renderSimpsonLegs(m, vc, swing, amp, var16, var17, var18, l, o);
   }

   private void renderSpongeBob(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      int var10 = -534216;
      int var11 = -4023513;
      byte var12 = -1;
      int var13 = -16731905;
      int var14 = -14935012;
      int var15 = -2047445;
      int var16 = -4179669;
      int var17 = -33902;
      int var18 = -1630941;
      int var19 = -8895982;
      int var20 = -14935012;
      int var21 = -1630941;
      int var22 = -14057287;
      int var23 = -15658735;
      m.method_22903();
      m.method_46416(0.0F, 0.53125F, 0.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw * 0.75F));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch * 0.6F));
      m.method_46416(0.0F, -0.53125F, 0.0F);
      box(m, vc, -5.2F, -4.0F, -2.8F, 10.4F, 12.5F, 5.6F, var10, l, o);
      box(m, vc, -4.2F, -3.0F, -2.85F, 1.4F, 1.4F, 0.1F, var11, l, o);
      box(m, vc, 2.8F, -2.5F, -2.85F, 1.2F, 1.2F, 0.1F, var11, l, o);
      box(m, vc, -4.4F, 4.0F, -2.85F, 1.1F, 1.1F, 0.1F, var11, l, o);
      box(m, vc, 3.2F, 4.5F, -2.85F, 1.1F, 1.1F, 0.1F, var11, l, o);
      box(m, vc, -5.25F, 0.0F, -0.5F, 0.1F, 1.6F, 1.6F, var11, l, o);
      box(m, vc, 5.15F, 1.0F, 0.5F, 0.1F, 1.4F, 1.4F, var11, l, o);
      box(m, vc, -2.8F, -2.5F, -3.1F, 2.6F, 3.0F, 0.35F, var12, l, o);
      box(m, vc, 0.2F, -2.5F, -3.1F, 2.6F, 3.0F, 0.35F, var12, l, o);
      box(m, vc, -1.9F, -1.5F, -3.18F, 1.3F, 1.5F, 0.15F, var13, l, o);
      box(m, vc, 0.6F, -1.5F, -3.18F, 1.3F, 1.5F, 0.15F, var13, l, o);
      box(m, vc, -1.6F, -1.2F, -3.22F, 0.7F, 0.9F, 0.15F, var14, l, o);
      box(m, vc, 0.9F, -1.2F, -3.22F, 0.7F, 0.9F, 0.15F, var14, l, o);
      box(m, vc, -2.3F, -3.2F, -3.05F, 0.3F, 0.8F, 0.2F, var14, l, o);
      box(m, vc, -1.5F, -3.4F, -3.05F, 0.3F, 0.9F, 0.2F, var14, l, o);
      box(m, vc, 1.2F, -3.4F, -3.05F, 0.3F, 0.9F, 0.2F, var14, l, o);
      box(m, vc, 2.0F, -3.2F, -3.05F, 0.3F, 0.8F, 0.2F, var14, l, o);
      float var24 = (float)Math.sin(age * 0.22 + swing * 0.5) * 0.12F;
      m.method_22903();
      m.method_46416(0.0F, 0.0F, -0.175F);
      m.method_22907(class_7833.field_40714.rotation(var24));
      box(m, vc, -0.5F, -0.5F, -2.2F, 1.0F, 1.0F, 2.2F, var15, l, o);
      m.method_22909();
      box(m, vc, -3.8F, 0.0F, -2.9F, 1.1F, 0.9F, 0.15F, -24454, l, o);
      box(m, vc, 2.7F, 0.0F, -2.9F, 1.1F, 0.9F, 0.15F, -24454, l, o);
      box(m, vc, -3.0F, 1.5F, -2.95F, 6.0F, 2.0F, 0.2F, var16, l, o);
      box(m, vc, -1.8F, 2.6F, -3.0F, 3.6F, 0.8F, 0.15F, var17, l, o);
      box(m, vc, -1.2F, 1.6F, -3.1F, 1.0F, 1.2F, 0.2F, var12, l, o);
      box(m, vc, 0.2F, 1.6F, -3.1F, 1.0F, 1.2F, 0.2F, var12, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -5.3F, 8.5F, -2.9F, 10.6F, 2.5F, 5.8F, var12, l, o);
      box(m, vc, -2.4F, 8.4F, -3.1F, 1.8F, 1.4F, 0.3F, var12, l, o);
      box(m, vc, 0.6F, 8.4F, -3.1F, 1.8F, 1.4F, 0.3F, var12, l, o);
      float var25 = (float)Math.sin(age * 0.16 + swing * 0.4) * 0.12F;
      m.method_22903();
      m.method_46416(0.0F, 0.5375F, -0.2F);
      m.method_22907(class_7833.field_40718.rotation(var25));
      box(m, vc, -0.7F, -0.4F, 0.05F, 1.4F, 0.8F, 0.35F, -4179669, l, o);
      box(m, vc, -0.6F, 0.0F, 0.0F, 1.2F, 3.2F, 0.3F, var18, l, o);
      box(m, vc, -0.4F, 3.2F, 0.0F, 0.8F, 0.8F, 0.3F, var18, l, o);
      m.method_22909();
      box(m, vc, -5.3F, 11.0F, -2.9F, 10.6F, 3.5F, 5.8F, var19, l, o);
      box(m, vc, -4.5F, 10.8F, -2.95F, 0.5F, 1.2F, 0.15F, var19, l, o);
      box(m, vc, -1.8F, 10.8F, -2.95F, 0.5F, 1.2F, 0.15F, var19, l, o);
      box(m, vc, 1.3F, 10.8F, -2.95F, 0.5F, 1.2F, 0.15F, var19, l, o);
      box(m, vc, 4.0F, 10.8F, -2.95F, 0.5F, 1.2F, 0.15F, var19, l, o);
      box(m, vc, -5.35F, 11.0F, -2.95F, 10.7F, 0.8F, 5.9F, var20, l, o);
      box(m, vc, -3.5F, 11.05F, -3.0F, 1.4F, 0.7F, 0.15F, var12, l, o);
      box(m, vc, 2.1F, 11.05F, -3.0F, 1.4F, 0.7F, 0.15F, var12, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.34375F, 0.5625F, 0.0F);
      this.applyRightArm(m, swing, amp, -20.0F);
      box(m, vc, -1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, var12, l, o);
      box(m, vc, -0.6F, 1.0F, -0.6F, 1.2F, 7.5F, 1.2F, var10, l, o);
      m.method_22903();
      m.method_46416(0.0F, 0.53125F, -0.075F);
      m.method_22907(class_7833.field_40714.rotationDegrees(25.0F));
      int var26 = -2843835;
      int var27 = -10670062;
      int var28 = -12345273;
      short var29 = -10752;
      box(m, vc, -1.6F, 1.0F, -1.6F, 3.2F, 0.7F, 3.2F, var26, l, o);
      box(m, vc, -1.8F, 0.4F, -1.8F, 3.6F, 0.7F, 3.6F, var27, l, o);
      box(m, vc, -1.9F, 0.2F, -1.9F, 3.8F, 0.3F, 3.8F, var29, l, o);
      box(m, vc, -2.0F, -0.3F, -2.0F, 4.0F, 0.5F, 4.0F, var28, l, o);
      box(m, vc, -1.7F, -1.3F, -1.7F, 3.4F, 1.1F, 3.4F, var26, l, o);
      box(m, vc, -1.3F, -1.8F, -1.3F, 2.6F, 0.6F, 2.6F, var26, l, o);
      box(m, vc, -0.6F, -1.9F, -0.4F, 0.3F, 0.15F, 0.2F, var12, l, o);
      box(m, vc, 0.5F, -1.85F, 0.3F, 0.3F, 0.15F, 0.2F, var12, l, o);
      box(m, vc, -0.1F, -1.9F, 0.6F, 0.3F, 0.15F, 0.2F, var12, l, o);
      m.method_22909();
      m.method_22909();
      m.method_22903();
      m.method_46416(0.34375F, 0.5625F, 0.0F);
      this.applyLeftArm(m, swing, amp);
      box(m, vc, -1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, var12, l, o);
      box(m, vc, -0.6F, 1.0F, -0.6F, 1.2F, 8.0F, 1.2F, var10, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.15F, 0.90625F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -1.2F, 0.0F, -1.2F, 2.4F, 1.5F, 2.4F, var19, l, o);
      box(m, vc, -0.6F, 1.5F, -0.6F, 1.2F, 3.2F, 1.2F, var10, l, o);
      box(m, vc, -0.8F, 4.7F, -0.8F, 1.6F, 2.8F, 1.6F, var12, l, o);
      box(m, vc, -0.85F, 5.3F, -0.85F, 1.7F, 0.4F, 1.7F, var22, l, o);
      box(m, vc, -0.85F, 6.0F, -0.85F, 1.7F, 0.4F, 1.7F, var21, l, o);
      box(m, vc, -1.2F, 7.5F, -2.6F, 2.4F, 2.0F, 4.2F, var23, l, o);
      box(m, vc, -1.1F, 9.3F, 0.4F, 2.2F, 0.5F, 1.2F, -13948117, l, o);
      box(m, vc, -0.6F, 7.3F, -1.8F, 1.2F, 0.4F, 0.4F, var12, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.15F, 0.90625F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -1.2F, 0.0F, -1.2F, 2.4F, 1.5F, 2.4F, var19, l, o);
      box(m, vc, -0.6F, 1.5F, -0.6F, 1.2F, 3.2F, 1.2F, var10, l, o);
      box(m, vc, -0.8F, 4.7F, -0.8F, 1.6F, 2.8F, 1.6F, var12, l, o);
      box(m, vc, -0.85F, 5.3F, -0.85F, 1.7F, 0.4F, 1.7F, var22, l, o);
      box(m, vc, -0.85F, 6.0F, -0.85F, 1.7F, 0.4F, 1.7F, var21, l, o);
      box(m, vc, -1.2F, 7.5F, -2.6F, 2.4F, 2.0F, 4.2F, var23, l, o);
      box(m, vc, -1.1F, 9.3F, 0.4F, 2.2F, 0.5F, 1.2F, -13948117, l, o);
      box(m, vc, -0.6F, 7.3F, -1.8F, 1.2F, 0.4F, 0.4F, var12, l, o);
      m.method_22909();
   }

   private void renderPatrick(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      int var10 = -677448;
      int var11 = -2060642;
      int var12 = -4034946;
      int var13 = -8200022;
      int var14 = -10955123;
      int var15 = -5936707;
      int var16 = -8569704;
      short var17 = -10496;
      byte var18 = -1;
      int var19 = -14935012;
      int var20 = -6743260;
      int var21 = -11006956;
      short var22 = -31326;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -3.8F, -6.0F, -3.2F, 7.6F, 7.0F, 6.4F, var10, l, o);
      box(m, vc, -3.4F, -5.5F, -3.5F, 6.8F, 6.0F, 0.4F, var10, l, o);
      float var23 = (float)Math.sin(age * 0.12 + swing * 0.4) * 0.08F;
      m.method_22903();
      m.method_46416(0.0F, -0.375F, 0.0F);
      m.method_22907(class_7833.field_40718.rotation(var23));
      box(m, vc, -2.8F, -4.5F, -2.5F, 5.6F, 4.5F, 5.0F, var10, l, o);
      box(m, vc, -1.6F, -7.5F, -1.6F, 3.2F, 3.0F, 3.2F, var10, l, o);
      box(m, vc, -0.8F, -9.8F, -0.8F, 1.6F, 2.4F, 1.6F, var10, l, o);
      box(m, vc, -0.9F, -8.5F, -1.0F, 0.6F, 0.6F, 0.25F, var11, l, o);
      box(m, vc, 0.4F, -9.2F, -0.9F, 0.5F, 0.5F, 0.25F, var11, l, o);
      box(m, vc, -0.3F, -7.8F, 1.5F, 0.6F, 0.6F, 0.25F, var11, l, o);
      m.method_22909();
      box(m, vc, -2.8F, -5.2F, -3.3F, 0.6F, 0.6F, 0.25F, var11, l, o);
      box(m, vc, 2.2F, -5.2F, -3.3F, 0.6F, 0.6F, 0.25F, var11, l, o);
      box(m, vc, -3.2F, -3.5F, 2.8F, 0.7F, 0.7F, 0.3F, var11, l, o);
      box(m, vc, 2.5F, -2.8F, 2.8F, 0.7F, 0.7F, 0.3F, var11, l, o);
      box(m, vc, -2.6F, -4.8F, -3.5F, 2.0F, 0.6F, 0.25F, var19, l, o);
      box(m, vc, 0.6F, -4.8F, -3.5F, 2.0F, 0.6F, 0.25F, var19, l, o);
      box(m, vc, -0.6F, -4.5F, -3.45F, 1.2F, 0.35F, 0.2F, var12, l, o);
      box(m, vc, -2.3F, -4.0F, -3.55F, 1.9F, 2.3F, 0.35F, var18, l, o);
      box(m, vc, 0.4F, -4.0F, -3.55F, 1.9F, 2.3F, 0.35F, var18, l, o);
      box(m, vc, -1.4F, -3.0F, -3.62F, 0.75F, 0.9F, 0.15F, var19, l, o);
      box(m, vc, 0.65F, -3.0F, -3.62F, 0.75F, 0.9F, 0.15F, var19, l, o);
      box(m, vc, -1.2F, -3.2F, -3.68F, 0.3F, 0.35F, 0.12F, var18, l, o);
      box(m, vc, 0.85F, -3.2F, -3.68F, 0.3F, 0.35F, 0.12F, var18, l, o);
      box(m, vc, -2.6F, -1.0F, -3.5F, 5.2F, 2.2F, 0.35F, var21, l, o);
      box(m, vc, -2.8F, -1.1F, -3.45F, 5.6F, 0.3F, 0.25F, var20, l, o);
      box(m, vc, -1.6F, 0.1F, -3.55F, 3.2F, 1.0F, 0.35F, var22, l, o);
      box(m, vc, -0.2F, 0.1F, -3.58F, 0.4F, 1.0F, 0.15F, var12, l, o);
      box(m, vc, 2.3F, 0.6F, -3.6F, 0.45F, 0.9F, 0.35F, -1434396417, l, o);
      box(m, vc, 2.35F, 1.2F, -3.55F, 0.35F, 0.45F, 0.25F, -2009021185, l, o);
      box(m, vc, -3.2F, 1.0F, -3.6F, 6.4F, 1.0F, 0.7F, var11, l, o);
      box(m, vc, -2.6F, 1.8F, -3.7F, 5.2F, 0.6F, 0.5F, var11, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -5.0F, 1.0F, -3.8F, 10.0F, 11.0F, 7.6F, var10, l, o);
      box(m, vc, -4.2F, 1.5F, 3.6F, 8.4F, 10.0F, 0.6F, var10, l, o);
      box(m, vc, -3.5F, 3.2F, 3.9F, 0.9F, 0.9F, 0.35F, var11, l, o);
      box(m, vc, 2.6F, 4.5F, 3.9F, 0.9F, 0.9F, 0.35F, var11, l, o);
      box(m, vc, -1.0F, 7.8F, 3.9F, 0.8F, 0.8F, 0.35F, var11, l, o);
      box(m, vc, -4.6F, 3.5F, -4.8F, 9.2F, 8.2F, 1.6F, var10, l, o);
      box(m, vc, -4.0F, 8.0F, -5.1F, 8.0F, 3.8F, 0.9F, var10, l, o);
      box(m, vc, -0.8F, 6.2F, -5.15F, 1.6F, 1.6F, 0.35F, var11, l, o);
      box(m, vc, -0.4F, 6.4F, -5.22F, 0.8F, 0.8F, 0.25F, -6735026, l, o);
      box(m, vc, -5.2F, 12.0F, -4.0F, 10.4F, 5.5F, 8.0F, var13, l, o);
      box(m, vc, -5.3F, 12.0F, -4.1F, 10.6F, 1.0F, 8.2F, var14, l, o);
      box(m, vc, -5.35F, 11.8F, -4.15F, 10.7F, 0.8F, 8.3F, var15, l, o);
      box(m, vc, -0.7F, 11.6F, -4.35F, 1.4F, 0.9F, 0.7F, var16, l, o);
      box(m, vc, -0.9F, 12.4F, -4.4F, 0.4F, 2.0F, 0.3F, var18, l, o);
      box(m, vc, -0.95F, 14.2F, -4.42F, 0.5F, 0.5F, 0.35F, var17, l, o);
      box(m, vc, 0.5F, 12.4F, -4.4F, 0.4F, 2.2F, 0.3F, var18, l, o);
      box(m, vc, 0.45F, 14.4F, -4.42F, 0.5F, 0.5F, 0.35F, var17, l, o);
      box(m, vc, -3.2F, 13.0F, -4.2F, 2.4F, 2.4F, 0.25F, var15, l, o);
      box(m, vc, -3.4F, 13.5F, -4.22F, 2.8F, 1.4F, 0.2F, var16, l, o);
      box(m, vc, -2.5F, 12.6F, -4.22F, 1.0F, 3.2F, 0.2F, var16, l, o);
      box(m, vc, -2.2F, 13.9F, -4.26F, 0.6F, 0.6F, 0.2F, var17, l, o);
      box(m, vc, 1.6F, 14.2F, -4.2F, 2.4F, 2.4F, 0.25F, var15, l, o);
      box(m, vc, 1.4F, 14.7F, -4.22F, 2.8F, 1.4F, 0.2F, var16, l, o);
      box(m, vc, 2.3F, 13.8F, -4.22F, 1.0F, 3.2F, 0.2F, var16, l, o);
      box(m, vc, 2.6F, 15.1F, -4.26F, 0.6F, 0.6F, 0.2F, var17, l, o);
      box(m, vc, -1.8F, 13.8F, 4.05F, 2.4F, 2.4F, 0.25F, var15, l, o);
      box(m, vc, -0.8F, 14.7F, 4.12F, 0.6F, 0.6F, 0.2F, var17, l, o);
      box(m, vc, 2.2F, 13.0F, 4.05F, 2.2F, 2.2F, 0.25F, var15, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.3625F, 0.2F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees(22.0F));
      this.applyRightArm(m, swing, amp);
      m.method_22907(class_7833.field_40714.rotationDegrees(22.0F));
      m.method_22907(class_7833.field_40716.rotationDegrees(-12.0F));
      box(m, vc, -1.7F, -1.2F, -1.7F, 3.4F, 8.5F, 3.4F, var10, l, o);
      box(m, vc, -1.3F, 7.0F, -1.3F, 2.6F, 2.0F, 2.6F, var11, l, o);
      box(m, vc, -1.8F, 1.0F, -0.5F, 0.25F, 0.7F, 0.7F, var11, l, o);
      box(m, vc, -1.8F, 4.0F, -0.5F, 0.25F, 0.7F, 0.7F, var11, l, o);
      m.method_22903();
      m.method_46416(0.0F, 0.53125F, -0.1125F);
      m.method_22907(class_7833.field_40714.rotationDegrees(-22.0F));
      m.method_22907(class_7833.field_40716.rotationDegrees(12.0F));
      int var24 = -2844072;
      int var25 = -4227013;
      int var26 = -12345273;
      short var27 = -19712;
      int var28 = -11652050;
      int var29 = -3790808;
      short var30 = -5632;
      box(m, vc, -2.0F, -2.6F, -2.0F, 4.0F, 1.4F, 4.0F, var24, l, o);
      box(m, vc, -1.5F, -3.2F, -1.5F, 3.0F, 0.7F, 3.0F, var25, l, o);
      box(m, vc, -1.0F, -3.25F, -1.0F, 0.35F, 0.15F, 0.25F, var18, l, o);
      box(m, vc, 0.6F, -3.25F, -0.8F, 0.35F, 0.15F, 0.25F, var18, l, o);
      box(m, vc, -0.5F, -3.25F, 0.7F, 0.35F, 0.15F, 0.25F, var18, l, o);
      box(m, vc, 0.4F, -3.25F, 0.5F, 0.35F, 0.15F, 0.25F, var18, l, o);
      box(m, vc, -2.3F, -1.3F, -2.3F, 4.6F, 0.4F, 4.6F, var26, l, o);
      box(m, vc, -2.5F, -1.2F, -1.0F, 0.4F, 0.3F, 2.0F, var26, l, o);
      box(m, vc, 2.1F, -1.2F, -1.2F, 0.4F, 0.3F, 2.2F, var26, l, o);
      box(m, vc, -2.1F, -0.95F, -2.1F, 4.2F, 0.35F, 4.2F, var27, l, o);
      box(m, vc, -2.3F, -0.8F, -2.3F, 0.6F, 0.7F, 0.6F, var27, l, o);
      box(m, vc, 1.7F, -0.8F, -2.3F, 0.6F, 0.7F, 0.6F, var27, l, o);
      box(m, vc, -2.3F, -0.8F, 1.7F, 0.6F, 0.7F, 0.6F, var27, l, o);
      box(m, vc, 1.7F, -0.8F, 1.7F, 0.6F, 0.7F, 0.6F, var27, l, o);
      box(m, vc, -2.1F, -0.6F, -2.1F, 4.2F, 0.8F, 4.2F, var28, l, o);
      box(m, vc, -1.6F, -0.4F, -2.25F, 0.7F, 0.5F, 0.25F, var29, l, o);
      box(m, vc, 0.8F, -0.4F, -2.25F, 0.7F, 0.5F, 0.25F, var30, l, o);
      box(m, vc, -2.0F, 0.2F, -2.0F, 4.0F, 0.8F, 4.0F, var24, l, o);
      m.method_22909();
      m.method_22909();
      m.method_22903();
      m.method_46416(0.3625F, 0.2F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees(-24.0F));
      this.applyLeftArm(m, swing, amp);
      m.method_22907(class_7833.field_40716.rotationDegrees(8.0F));
      box(m, vc, -1.7F, -1.2F, -1.7F, 3.4F, 9.0F, 3.4F, var10, l, o);
      box(m, vc, -1.3F, 7.5F, -1.3F, 2.6F, 1.8F, 2.6F, var11, l, o);
      box(m, vc, 1.55F, 1.5F, -0.5F, 0.25F, 0.7F, 0.7F, var11, l, o);
      box(m, vc, 1.55F, 4.5F, -0.5F, 0.25F, 0.7F, 0.7F, var11, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.15625F, 1.09375F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -2.1F, 0.0F, -2.1F, 4.2F, 3.2F, 4.2F, var13, l, o);
      box(m, vc, -1.9F, 3.2F, -2.5F, 3.8F, 3.5F, 4.6F, var10, l, o);
      box(m, vc, -1.8F, 5.8F, -3.2F, 0.6F, 0.9F, 0.9F, var11, l, o);
      box(m, vc, -1.0F, 5.8F, -3.3F, 0.6F, 0.9F, 1.0F, var11, l, o);
      box(m, vc, -0.2F, 5.8F, -3.3F, 0.6F, 0.9F, 1.0F, var11, l, o);
      box(m, vc, 0.6F, 5.8F, -3.2F, 0.6F, 0.9F, 0.9F, var11, l, o);
      box(m, vc, 1.3F, 5.8F, -3.0F, 0.5F, 0.8F, 0.8F, var11, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.15625F, 1.09375F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -2.1F, 0.0F, -2.1F, 4.2F, 3.2F, 4.2F, var13, l, o);
      box(m, vc, -1.9F, 3.2F, -2.5F, 3.8F, 3.5F, 4.6F, var10, l, o);
      box(m, vc, -1.8F, 5.8F, -3.0F, 0.5F, 0.8F, 0.8F, var11, l, o);
      box(m, vc, -1.1F, 5.8F, -3.2F, 0.6F, 0.9F, 0.9F, var11, l, o);
      box(m, vc, -0.3F, 5.8F, -3.3F, 0.6F, 0.9F, 1.0F, var11, l, o);
      box(m, vc, 0.5F, 5.8F, -3.3F, 0.6F, 0.9F, 1.0F, var11, l, o);
      box(m, vc, 1.3F, 5.8F, -3.2F, 0.6F, 0.9F, 0.9F, var11, l, o);
      m.method_22909();
   }

   private void renderMario(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      short var10 = -11862;
      int var11 = -2054014;
      int var12 = -1759967;
      int var13 = -4776932;
      int var14 = -14987406;
      int var15 = -15383712;
      int var16 = -14125914;
      short var17 = -10496;
      short var18 = -3722;
      int var19 = -12434878;
      short var20 = -5317;
      int var21 = -13885932;
      int var22 = -15068917;
      int var23 = -16611119;
      int var24 = -11652050;
      byte var25 = -1;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -3.5F, -7.5F, -3.5F, 7.0F, 7.5F, 7.0F, var10, l, o);
      box(m, vc, -3.9F, -5.5F, -0.6F, 0.6F, 2.4F, 1.8F, var10, l, o);
      box(m, vc, -3.95F, -5.0F, -0.3F, 0.2F, 1.4F, 1.2F, var11, l, o);
      box(m, vc, 3.3F, -5.5F, -0.6F, 0.6F, 2.4F, 1.8F, var10, l, o);
      box(m, vc, 3.75F, -5.0F, -0.3F, 0.2F, 1.4F, 1.2F, var11, l, o);
      float var26 = (float)Math.sin(age * 0.15 + swing * 0.4) * 0.12F;
      box(m, vc, -4.2F, -9.6F, -4.2F, 8.4F, 3.4F, 8.4F, var12, l, o);
      box(m, vc, -3.6F, -10.6F, -3.6F, 7.2F, 1.2F, 7.2F, var12, l, o);
      box(m, vc, -3.8F, -9.2F, 3.6F, 7.6F, 2.8F, 1.4F, var13, l, o);
      box(m, vc, -4.4F, -7.8F + var26, -5.8F, 8.8F, 1.2F, 2.6F, var12, l, o);
      box(m, vc, -4.2F, -7.0F + var26, -5.6F, 8.4F, 0.3F, 2.2F, var13, l, o);
      box(m, vc, -1.6F, -10.0F, -4.35F, 3.2F, 2.5F, 0.35F, var25, l, o);
      box(m, vc, -1.8F, -9.8F, -4.32F, 3.6F, 2.1F, 0.3F, var25, l, o);
      box(m, vc, -1.1F, -9.6F, -4.45F, 0.45F, 1.7F, 0.2F, var12, l, o);
      box(m, vc, 0.65F, -9.6F, -4.45F, 0.45F, 1.7F, 0.2F, var12, l, o);
      box(m, vc, -0.75F, -9.3F, -4.45F, 0.45F, 0.9F, 0.2F, var12, l, o);
      box(m, vc, 0.3F, -9.3F, -4.45F, 0.45F, 0.9F, 0.2F, var12, l, o);
      box(m, vc, -0.25F, -8.9F, -4.45F, 0.5F, 0.7F, 0.2F, var12, l, o);
      box(m, vc, -3.8F, -6.5F, 1.2F, 7.6F, 3.6F, 2.8F, var21, l, o);
      box(m, vc, -3.8F, -3.4F, 2.2F, 7.6F, 1.4F, 1.8F, var21, l, o);
      box(m, vc, -3.7F, -5.8F, -1.8F, 0.4F, 2.6F, 1.8F, var21, l, o);
      box(m, vc, 3.3F, -5.8F, -1.8F, 0.4F, 2.6F, 1.8F, var21, l, o);
      box(m, vc, -2.2F, -5.8F, -3.65F, 1.7F, 2.0F, 0.25F, var25, l, o);
      box(m, vc, 0.5F, -5.8F, -3.65F, 1.7F, 2.0F, 0.25F, var25, l, o);
      box(m, vc, -1.6F, -5.3F, -3.72F, 1.0F, 1.3F, 0.15F, var23, l, o);
      box(m, vc, 0.6F, -5.3F, -3.72F, 1.0F, 1.3F, 0.15F, var23, l, o);
      box(m, vc, -1.4F, -5.1F, -3.76F, 0.6F, 0.8F, 0.15F, var22, l, o);
      box(m, vc, 0.8F, -5.1F, -3.76F, 0.6F, 0.8F, 0.15F, var22, l, o);
      box(m, vc, -1.3F, -5.3F, -3.82F, 0.35F, 0.4F, 0.12F, var25, l, o);
      box(m, vc, 0.9F, -5.3F, -3.82F, 0.35F, 0.4F, 0.12F, var25, l, o);
      box(m, vc, -1.2F, -4.5F, -5.4F, 2.4F, 1.9F, 2.0F, var10, l, o);
      box(m, vc, -1.6F, -4.1F, -4.9F, 0.6F, 1.1F, 1.1F, var10, l, o);
      box(m, vc, 1.0F, -4.1F, -4.9F, 0.6F, 1.1F, 1.1F, var10, l, o);
      box(m, vc, -3.2F, -3.2F, -5.0F, 6.4F, 1.6F, 1.4F, var21, l, o);
      box(m, vc, -3.8F, -2.6F, -4.8F, 1.4F, 1.4F, 1.0F, var21, l, o);
      box(m, vc, -2.7F, -2.2F, -4.9F, 1.4F, 1.5F, 1.0F, var21, l, o);
      box(m, vc, -4.2F, -3.2F, -4.6F, 0.8F, 1.0F, 0.8F, var21, l, o);
      box(m, vc, 2.4F, -2.6F, -4.8F, 1.4F, 1.4F, 1.0F, var21, l, o);
      box(m, vc, 1.3F, -2.2F, -4.9F, 1.4F, 1.5F, 1.0F, var21, l, o);
      box(m, vc, 3.4F, -3.2F, -4.6F, 0.8F, 1.0F, 0.8F, var21, l, o);
      box(m, vc, -3.0F, -3.3F, -4.85F, 6.0F, 0.4F, 1.1F, var22, l, o);
      box(m, vc, -1.8F, -1.6F, -4.4F, 3.6F, 1.2F, 0.6F, -11927552, l, o);
      box(m, vc, -1.4F, -1.6F, -4.45F, 2.8F, 0.45F, 0.4F, var25, l, o);
      box(m, vc, -1.0F, -0.9F, -4.45F, 2.0F, 0.5F, 0.4F, -39548, l, o);
      box(m, vc, -1.6F, -0.4F, -4.0F, 3.2F, 0.8F, 0.8F, var10, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -4.0F, 0.0F, -2.5F, 8.0F, 6.0F, 5.0F, var12, l, o);
      box(m, vc, -3.2F, -0.6F, -2.0F, 6.4F, 0.8F, 4.0F, var13, l, o);
      box(m, vc, -4.2F, 5.0F, -2.7F, 8.4F, 6.2F, 5.4F, var14, l, o);
      box(m, vc, -3.2F, 1.4F, -2.88F, 6.4F, 4.2F, 0.4F, var14, l, o);
      box(m, vc, -1.8F, 2.4F, -2.96F, 3.6F, 2.4F, 0.25F, var15, l, o);
      box(m, vc, -1.85F, 2.35F, -3.0F, 3.7F, 0.2F, 0.15F, var20, l, o);
      box(m, vc, -0.4F, 2.8F, -3.02F, 0.8F, 1.4F, 0.15F, var20, l, o);
      box(m, vc, -3.6F, 0.0F, -2.92F, 1.4F, 2.2F, 5.85F, var14, l, o);
      box(m, vc, 2.2F, 0.0F, -2.92F, 1.4F, 2.2F, 5.85F, var14, l, o);
      box(m, vc, -3.2F, 2.0F, 2.65F, 6.4F, 3.6F, 0.35F, var14, l, o);
      box(m, vc, -3.7F, 1.2F, 2.75F, 1.6F, 0.8F, 0.25F, -3155748, l, o);
      box(m, vc, 2.1F, 1.2F, 2.75F, 1.6F, 0.8F, 0.25F, -3155748, l, o);
      box(m, vc, -3.3F, 2.0F, -3.1F, 1.1F, 1.1F, 0.35F, var17, l, o);
      box(m, vc, -3.1F, 2.2F, -3.18F, 0.7F, 0.7F, 0.15F, var18, l, o);
      box(m, vc, -2.95F, 2.35F, -3.22F, 0.2F, 0.2F, 0.12F, var19, l, o);
      box(m, vc, -2.65F, 2.35F, -3.22F, 0.2F, 0.2F, 0.12F, var19, l, o);
      box(m, vc, -2.95F, 2.65F, -3.22F, 0.2F, 0.2F, 0.12F, var19, l, o);
      box(m, vc, -2.65F, 2.65F, -3.22F, 0.2F, 0.2F, 0.12F, var19, l, o);
      box(m, vc, 2.2F, 2.0F, -3.1F, 1.1F, 1.1F, 0.35F, var17, l, o);
      box(m, vc, 2.4F, 2.2F, -3.18F, 0.7F, 0.7F, 0.15F, var18, l, o);
      box(m, vc, 2.55F, 2.35F, -3.22F, 0.2F, 0.2F, 0.12F, var19, l, o);
      box(m, vc, 2.85F, 2.35F, -3.22F, 0.2F, 0.2F, 0.12F, var19, l, o);
      box(m, vc, 2.55F, 2.65F, -3.22F, 0.2F, 0.2F, 0.12F, var19, l, o);
      box(m, vc, 2.85F, 2.65F, -3.22F, 0.2F, 0.2F, 0.12F, var19, l, o);
      box(m, vc, -4.3F, 6.2F, -0.3F, 0.25F, 0.6F, 0.6F, var17, l, o);
      box(m, vc, 4.05F, 6.2F, -0.3F, 0.25F, 0.6F, 0.6F, var17, l, o);
      box(m, vc, -3.4F, 6.2F, 2.72F, 2.8F, 3.2F, 0.25F, var15, l, o);
      box(m, vc, -3.1F, 7.8F, 2.76F, 2.2F, 0.2F, 0.15F, var20, l, o);
      box(m, vc, 0.6F, 6.2F, 2.72F, 2.8F, 3.2F, 0.25F, var15, l, o);
      box(m, vc, 0.9F, 7.8F, 2.76F, 2.2F, 0.2F, 0.15F, var20, l, o);
      m.method_22909();
      this.renderStandardArms(m, vc, swing, amp, var12, var12, var25, l, o);
      m.method_22903();
      m.method_46416(-0.34375F, 0.65625F, -0.1375F);
      this.applyRightArm(m, swing, amp);
      m.method_22907(class_7833.field_40714.rotationDegrees(20.0F));
      int var27 = -1759967;
      short var28 = -1817;
      box(m, vc, -1.2F, 0.0F, -1.2F, 2.4F, 2.4F, 2.4F, var28, l, o);
      box(m, vc, -0.8F, 0.4F, -1.35F, 0.45F, 1.2F, 0.25F, var22, l, o);
      box(m, vc, 0.35F, 0.4F, -1.35F, 0.45F, 1.2F, 0.25F, var22, l, o);
      box(m, vc, -0.7F, 0.5F, -1.4F, 0.2F, 0.4F, 0.15F, var25, l, o);
      box(m, vc, 0.45F, 0.5F, -1.4F, 0.2F, 0.4F, 0.15F, var25, l, o);
      box(m, vc, -2.2F, -1.8F, -2.2F, 4.4F, 2.0F, 4.4F, var27, l, o);
      box(m, vc, -1.6F, -2.7F, -1.6F, 3.2F, 1.0F, 3.2F, var27, l, o);
      box(m, vc, -1.0F, -2.0F, -2.35F, 2.0F, 1.6F, 0.25F, var25, l, o);
      box(m, vc, -1.0F, -2.0F, 2.1F, 2.0F, 1.6F, 0.25F, var25, l, o);
      box(m, vc, -2.35F, -2.0F, -1.0F, 0.25F, 1.6F, 2.0F, var25, l, o);
      box(m, vc, 2.1F, -2.0F, -1.0F, 0.25F, 1.6F, 2.0F, var25, l, o);
      box(m, vc, -0.9F, -2.85F, -0.9F, 1.8F, 0.25F, 1.8F, var25, l, o);
      float var29 = (float)Math.sin(age * 0.2) * 0.4F;
      box(m, vc, -1.8F + var29, -3.4F, 0.2F, 0.35F, 0.35F, 0.35F, var18, l, o);
      box(m, vc, 1.6F - var29, -3.1F, -0.4F, 0.3F, 0.3F, 0.3F, var18, l, o);
      m.method_22909();
      this.renderMarioBoots(m, vc, swing, amp, var14, var16, var24, var20, l, o);
   }

   private void renderSonic(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      int var10 = -15052170;
      int var11 = -15648684;
      short var12 = -11862;
      int var13 = -2054014;
      int var14 = -16725933;
      int var15 = -16744909;
      int var16 = -1630941;
      byte var17 = -1;
      short var18 = -10496;
      int var19 = -15658735;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -3.5F, -7.5F, -3.5F, 7.0F, 7.5F, 7.0F, var10, l, o);
      box(m, vc, -3.2F, -8.2F, -3.0F, 6.4F, 0.8F, 6.2F, var10, l, o);
      box(m, vc, -3.2F, -6.8F, -3.9F, 6.4F, 1.2F, 0.6F, var10, l, o);
      float var20 = (float)Math.sin(age * 0.22 + swing * 0.7) * 0.09F;
      m.method_22903();
      m.method_46416(-0.0875F, -0.45F, 0.125F);
      m.method_22907(class_7833.field_40716.rotationDegrees(-15.0F));
      m.method_22907(class_7833.field_40714.rotation(-0.25F + var20));
      box(m, vc, -1.6F, -1.8F, 0.0F, 3.2F, 3.2F, 5.4F, var10, l, o);
      box(m, vc, -1.1F, -1.3F, 4.8F, 2.2F, 2.2F, 2.4F, var10, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.0875F, -0.45F, 0.125F);
      m.method_22907(class_7833.field_40716.rotationDegrees(15.0F));
      m.method_22907(class_7833.field_40714.rotation(-0.25F + var20));
      box(m, vc, -1.6F, -1.8F, 0.0F, 3.2F, 3.2F, 5.4F, var10, l, o);
      box(m, vc, -1.1F, -1.3F, 4.8F, 2.2F, 2.2F, 2.4F, var10, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.075F, -0.2875F, 0.175F);
      m.method_22907(class_7833.field_40716.rotationDegrees(-8.0F));
      m.method_22907(class_7833.field_40714.rotation(var20 * 1.3F));
      box(m, vc, -1.8F, -1.8F, 0.0F, 3.6F, 3.4F, 6.0F, var10, l, o);
      box(m, vc, -1.2F, -1.2F, 5.4F, 2.4F, 2.2F, 2.6F, var10, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.075F, -0.2875F, 0.175F);
      m.method_22907(class_7833.field_40716.rotationDegrees(8.0F));
      m.method_22907(class_7833.field_40714.rotation(var20 * 1.3F));
      box(m, vc, -1.8F, -1.8F, 0.0F, 3.6F, 3.4F, 6.0F, var10, l, o);
      box(m, vc, -1.2F, -1.2F, 5.4F, 2.4F, 2.2F, 2.6F, var10, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.0625F, -0.1125F, 0.15F);
      m.method_22907(class_7833.field_40714.rotation(0.35F + var20 * 0.8F));
      box(m, vc, -1.5F, -1.5F, 0.0F, 3.0F, 2.8F, 4.8F, var10, l, o);
      box(m, vc, -1.0F, -1.0F, 4.4F, 2.0F, 1.8F, 2.0F, var10, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.0625F, -0.1125F, 0.15F);
      m.method_22907(class_7833.field_40714.rotation(0.35F + var20 * 0.8F));
      box(m, vc, -1.5F, -1.5F, 0.0F, 3.0F, 2.8F, 4.8F, var10, l, o);
      box(m, vc, -1.0F, -1.0F, 4.4F, 2.0F, 1.8F, 2.0F, var10, l, o);
      m.method_22909();
      box(m, vc, -4.6F, -3.8F, -2.4F, 1.3F, 2.0F, 2.6F, var10, l, o);
      box(m, vc, 3.3F, -3.8F, -2.4F, 1.3F, 2.0F, 2.6F, var10, l, o);
      box(m, vc, -4.6F, -10.8F, -1.2F, 2.0F, 3.8F, 1.8F, var10, l, o);
      box(m, vc, -4.3F, -10.2F, -1.1F, 1.4F, 2.8F, 0.5F, var12, l, o);
      box(m, vc, -4.1F, -9.4F, -1.0F, 1.0F, 1.6F, 0.25F, var13, l, o);
      box(m, vc, 2.6F, -10.8F, -1.2F, 2.0F, 3.8F, 1.8F, var10, l, o);
      box(m, vc, 2.9F, -10.2F, -1.1F, 1.4F, 2.8F, 0.5F, var12, l, o);
      box(m, vc, 3.1F, -9.4F, -1.0F, 1.0F, 1.6F, 0.25F, var13, l, o);
      box(m, vc, -2.6F, -6.3F, -3.8F, 2.4F, 3.0F, 0.35F, var17, l, o);
      box(m, vc, 0.2F, -6.3F, -3.8F, 2.4F, 3.0F, 0.35F, var17, l, o);
      box(m, vc, -1.8F, -5.4F, -3.9F, 1.3F, 1.8F, 0.18F, var14, l, o);
      box(m, vc, 0.5F, -5.4F, -3.9F, 1.3F, 1.8F, 0.18F, var14, l, o);
      box(m, vc, -1.5F, -5.1F, -3.96F, 0.8F, 1.2F, 0.15F, var19, l, o);
      box(m, vc, 0.7F, -5.1F, -3.96F, 0.8F, 1.2F, 0.15F, var19, l, o);
      box(m, vc, -1.4F, -5.3F, -4.02F, 0.4F, 0.45F, 0.12F, var17, l, o);
      box(m, vc, 0.8F, -5.3F, -4.02F, 0.4F, 0.45F, 0.12F, var17, l, o);
      box(m, vc, -1.1F, -4.6F, -4.02F, 0.25F, 0.25F, 0.12F, var15, l, o);
      box(m, vc, -2.8F, -3.6F, -5.0F, 5.6F, 2.5F, 1.7F, var12, l, o);
      box(m, vc, -0.6F, -4.2F, -5.6F, 1.2F, 1.1F, 0.9F, var19, l, o);
      box(m, vc, -0.2F, -4.3F, -5.66F, 0.4F, 0.35F, 0.15F, var17, l, o);
      box(m, vc, 0.4F, -2.5F, -5.1F, 2.2F, 0.6F, 0.25F, var19, l, o);
      box(m, vc, 1.2F, -2.8F, -5.14F, 0.5F, 0.7F, 0.2F, var17, l, o);
      box(m, vc, 0.6F, -2.4F, -5.12F, 1.2F, 0.35F, 0.15F, -6743260, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -3.5F, 0.0F, -2.0F, 7.0F, 10.5F, 4.0F, var10, l, o);
      box(m, vc, -2.6F, 1.5F, -2.35F, 5.2F, 7.2F, 0.6F, var12, l, o);
      box(m, vc, -1.4F, 1.8F, 1.9F, 2.8F, 2.6F, 2.6F, var10, l, o);
      box(m, vc, -1.2F, 5.2F, 1.9F, 2.4F, 2.4F, 2.4F, var10, l, o);
      float var21 = (float)Math.sin(age * 0.22 + swing * 0.6) * 0.25F;
      m.method_22903();
      m.method_46416(0.0F, 0.55F, 0.1125F);
      m.method_22907(class_7833.field_40716.rotation(var21));
      m.method_22907(class_7833.field_40714.rotationDegrees(25.0F));
      box(m, vc, -0.8F, -0.8F, 0.0F, 1.6F, 1.6F, 2.2F, var10, l, o);
      box(m, vc, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, 1.5F, var10, l, o);
      m.method_22909();
      m.method_22909();
      this.renderStandardArms(m, vc, swing, amp, var10, var12, var17, l, o);
      m.method_22903();
      m.method_46416(0.3875F, 0.46875F, -0.1375F);
      float var22 = (float)Math.sin(age * 0.18) * 0.5F;
      m.method_46416(0.0F, var22 * 0.0625F, 0.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(age * 5.0F));
      short var23 = -5632;
      short var24 = -19712;
      box(m, vc, -1.8F, -1.8F, -0.4F, 3.6F, 0.6F, 0.8F, var23, l, o);
      box(m, vc, -1.8F, 1.2F, -0.4F, 3.6F, 0.6F, 0.8F, var23, l, o);
      box(m, vc, -1.8F, -1.2F, -0.4F, 0.6F, 2.4F, 0.8F, var24, l, o);
      box(m, vc, 1.2F, -1.2F, -0.4F, 0.6F, 2.4F, 0.8F, var24, l, o);
      box(m, vc, -0.4F, -0.4F, -0.2F, 0.8F, 0.8F, 0.4F, -16718218, l, o);
      box(m, vc, -0.2F, -0.2F, -0.25F, 0.4F, 0.4F, 0.5F, var17, l, o);
      float var25 = age * 8.0F;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(var25));
      m.method_46416(0.15F, 0.0F, 0.0F);
      box(m, vc, -0.2F, -0.2F, -0.2F, 0.4F, 0.4F, 0.4F, -8333057, l, o);
      m.method_22909();
      m.method_22903();
      m.method_22907(class_7833.field_40718.rotationDegrees(-var25 * 0.9F));
      m.method_46416(0.0F, 0.1375F, 0.0F);
      box(m, vc, -0.2F, -0.2F, -0.2F, 0.4F, 0.4F, 0.4F, -4523, l, o);
      m.method_22909();
      m.method_22909();
      this.renderSonicSneakers(m, vc, swing, amp, var10, var16, var17, var18, l, o);
   }

   private void renderShrek(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      int var10 = -8545218;
      int var11 = -10652626;
      int var12 = -12693730;
      int var13 = -659720;
      int var14 = -2634552;
      int var15 = -10665929;
      int var16 = -12703965;
      int var17 = -7508381;
      int var18 = -11652050;
      int var19 = -13755372;
      int var20 = -12703965;
      byte var21 = -1;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -5.2F, -9.0F, -4.5F, 10.4F, 9.0F, 9.0F, var10, l, o);
      box(m, vc, -4.6F, -9.8F, -4.0F, 9.2F, 1.0F, 8.0F, var10, l, o);
      box(m, vc, -5.4F, -4.5F, -3.8F, 10.8F, 4.5F, 7.6F, var10, l, o);
      float var22 = (float)Math.sin(age * 0.15) * 0.08F;
      m.method_22903();
      m.method_46416(-0.325F, -0.45F, 0.0F);
      m.method_22907(class_7833.field_40718.rotation(var22));
      m.method_22907(class_7833.field_40716.rotationDegrees(-15.0F));
      box(m, vc, -1.8F, -0.6F, -0.8F, 2.0F, 1.4F, 1.6F, var10, l, o);
      box(m, vc, -3.4F, -1.0F, -1.2F, 1.8F, 2.2F, 2.4F, var10, l, o);
      box(m, vc, -3.6F, -0.7F, -0.9F, 0.35F, 1.6F, 1.8F, var11, l, o);
      box(m, vc, -3.65F, -0.5F, -0.7F, 0.25F, 1.2F, 1.4F, var12, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.325F, -0.45F, 0.0F);
      m.method_22907(class_7833.field_40718.rotation(-var22));
      m.method_22907(class_7833.field_40716.rotationDegrees(15.0F));
      box(m, vc, -0.2F, -0.6F, -0.8F, 2.0F, 1.4F, 1.6F, var10, l, o);
      box(m, vc, 1.6F, -1.0F, -1.2F, 1.8F, 2.2F, 2.4F, var10, l, o);
      box(m, vc, 3.25F, -0.7F, -0.9F, 0.35F, 1.6F, 1.8F, var11, l, o);
      box(m, vc, 3.4F, -0.5F, -0.7F, 0.25F, 1.2F, 1.4F, var12, l, o);
      m.method_22909();
      box(m, vc, -4.5F, -6.8F, -4.9F, 9.0F, 1.4F, 0.7F, var10, l, o);
      box(m, vc, -3.2F, -6.0F, -4.8F, 2.2F, 1.8F, 0.35F, var21, l, o);
      box(m, vc, 1.0F, -6.0F, -4.8F, 2.2F, 1.8F, 0.35F, var21, l, o);
      box(m, vc, -2.4F, -5.6F, -4.9F, 1.2F, 1.2F, 0.18F, -11913947, l, o);
      box(m, vc, 1.2F, -5.6F, -4.9F, 1.2F, 1.2F, 0.18F, -11913947, l, o);
      box(m, vc, -2.1F, -5.4F, -4.95F, 0.6F, 0.8F, 0.15F, -14935012, l, o);
      box(m, vc, 1.5F, -5.4F, -4.95F, 0.6F, 0.8F, 0.15F, -14935012, l, o);
      box(m, vc, -2.0F, -5.5F, -5.0F, 0.3F, 0.3F, 0.12F, var21, l, o);
      box(m, vc, 1.6F, -5.5F, -5.0F, 0.3F, 0.3F, 0.12F, var21, l, o);
      box(m, vc, -1.8F, -4.6F, -6.0F, 3.6F, 2.4F, 1.8F, var10, l, o);
      box(m, vc, -2.3F, -4.2F, -5.6F, 0.7F, 1.2F, 1.2F, var10, l, o);
      box(m, vc, 1.6F, -4.2F, -5.6F, 0.7F, 1.2F, 1.2F, var10, l, o);
      box(m, vc, -1.3F, -3.8F, -6.05F, 0.6F, 0.5F, 0.2F, var11, l, o);
      box(m, vc, 0.7F, -3.8F, -6.05F, 0.6F, 0.5F, 0.2F, var11, l, o);
      box(m, vc, -3.0F, -2.2F, -4.85F, 6.0F, 1.5F, 0.45F, -12703965, l, o);
      box(m, vc, -2.2F, -1.6F, -4.95F, 0.6F, 0.7F, 0.35F, -1596, l, o);
      box(m, vc, 1.6F, -1.6F, -4.95F, 0.6F, 0.7F, 0.35F, -1596, l, o);
      box(m, vc, -1.2F, -1.8F, -4.92F, 2.4F, 0.5F, 0.3F, -1596, l, o);
      box(m, vc, -2.0F, -0.6F, -4.5F, 4.0F, 0.9F, 1.0F, var10, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -5.5F, 0.0F, -4.0F, 11.0F, 12.5F, 8.0F, var13, l, o);
      box(m, vc, -3.8F, -0.8F, -3.0F, 7.6F, 1.0F, 6.0F, var14, l, o);
      box(m, vc, -5.0F, 2.5F, -5.6F, 10.0F, 9.5F, 2.2F, var13, l, o);
      box(m, vc, -5.8F, 0.0F, -4.2F, 3.2F, 9.5F, 8.4F, var15, l, o);
      box(m, vc, 2.6F, 0.0F, -4.2F, 3.2F, 9.5F, 8.4F, var15, l, o);
      box(m, vc, -5.8F, 0.0F, 3.8F, 11.6F, 9.5F, 0.5F, var15, l, o);
      box(m, vc, -2.0F, 1.6F, -5.72F, 0.4F, 0.4F, 0.2F, var16, l, o);
      box(m, vc, 1.6F, 1.6F, -5.72F, 0.4F, 0.4F, 0.2F, var16, l, o);
      box(m, vc, -1.8F, 1.7F, -5.75F, 3.6F, 0.35F, 0.2F, var17, l, o);
      box(m, vc, -1.7F, 4.0F, -5.72F, 0.4F, 0.4F, 0.2F, var16, l, o);
      box(m, vc, 1.3F, 4.0F, -5.72F, 0.4F, 0.4F, 0.2F, var16, l, o);
      box(m, vc, -1.5F, 4.1F, -5.75F, 3.0F, 0.35F, 0.2F, var17, l, o);
      box(m, vc, -1.4F, 6.4F, -5.72F, 0.4F, 0.4F, 0.2F, var16, l, o);
      box(m, vc, 1.0F, 6.4F, -5.72F, 0.4F, 0.4F, 0.2F, var16, l, o);
      box(m, vc, -1.2F, 6.5F, -5.75F, 2.4F, 0.35F, 0.2F, var17, l, o);
      box(m, vc, -5.9F, 9.5F, -4.5F, 11.8F, 1.4F, 8.8F, var20, l, o);
      float var23 = (float)Math.sin(age * 0.14 + swing * 0.4) * 0.18F;
      box(m, vc, -1.5F, 9.2F, -4.9F, 3.0F, 2.0F, 0.8F, var20, l, o);
      box(m, vc, -1.2F + var23, 11.0F, -4.95F, 0.6F, 3.5F, 0.5F, -11652050, l, o);
      box(m, vc, 0.2F - var23, 11.0F, -4.95F, 0.6F, 2.8F, 0.5F, -11652050, l, o);
      box(m, vc, 4.8F, 9.0F, -2.5F, 1.8F, 3.2F, 2.8F, var16, l, o);
      box(m, vc, 4.75F, 8.8F, -2.6F, 1.9F, 1.2F, 3.0F, var15, l, o);
      box(m, vc, 6.2F, 9.6F, -1.3F, 0.4F, 0.5F, 0.5F, -10496, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.425F, 0.125F, 0.0F);
      this.applyRightArm(m, swing, amp);
      m.method_22907(class_7833.field_40714.rotationDegrees(25.0F));
      box(m, vc, -2.4F, -2.0F, -2.4F, 4.8F, 5.5F, 4.8F, var13, l, o);
      box(m, vc, -2.2F, 3.5F, -2.2F, 4.4F, 6.0F, 4.4F, var10, l, o);
      box(m, vc, -2.5F, 8.5F, -2.5F, 5.0F, 3.8F, 5.0F, var10, l, o);
      box(m, vc, -2.1F, 11.8F, -2.6F, 4.2F, 0.4F, 0.3F, -1249295, l, o);
      m.method_22903();
      m.method_46416(0.0F, 0.65625F, -0.15625F);
      m.method_22907(class_7833.field_40714.rotationDegrees(-20.0F));
      short var24 = -1596;
      int var25 = -2634552;
      int var26 = -6190977;
      int var27 = -7508381;
      int var28 = -8978685;
      int var29 = -12345273;
      box(m, vc, -1.8F, -1.8F, -1.8F, 3.6F, 3.6F, 3.6F, var24, l, o);
      box(m, vc, -1.4F, -2.4F, -1.4F, 2.8F, 0.8F, 2.8F, var25, l, o);
      box(m, vc, -1.4F, 1.6F, -1.4F, 2.8F, 0.8F, 2.8F, var25, l, o);
      box(m, vc, -2.0F, -1.4F, -1.2F, 0.35F, 2.8F, 2.4F, var26, l, o);
      box(m, vc, 1.65F, -1.2F, -1.4F, 0.35F, 2.6F, 2.4F, var26, l, o);
      box(m, vc, -1.2F, -1.4F, 1.65F, 2.4F, 2.8F, 0.35F, var26, l, o);
      box(m, vc, -0.8F, 2.3F, -0.8F, 1.6F, 1.2F, 1.6F, var27, l, o);
      box(m, vc, -0.4F, 3.4F, -0.4F, 0.8F, 0.8F, 0.8F, var27, l, o);
      box(m, vc, -0.6F, -4.2F, -0.6F, 1.2F, 2.0F, 1.2F, var29, l, o);
      box(m, vc, -0.4F, -6.0F, -0.4F, 0.8F, 2.0F, 0.8F, var28, l, o);
      box(m, vc, 0.2F, -5.2F, -0.3F, 0.5F, 1.4F, 0.6F, var28, l, o);
      m.method_22909();
      m.method_22909();
      m.method_22903();
      m.method_46416(0.425F, 0.125F, 0.0F);
      this.applyLeftArm(m, swing, amp);
      box(m, vc, -2.4F, -2.0F, -2.4F, 4.8F, 5.5F, 4.8F, var13, l, o);
      box(m, vc, -2.2F, 3.5F, -2.2F, 4.4F, 6.0F, 4.4F, var10, l, o);
      box(m, vc, -2.5F, 8.5F, -2.5F, 5.0F, 3.8F, 5.0F, var10, l, o);
      box(m, vc, -2.1F, 11.8F, -2.6F, 4.2F, 0.4F, 0.3F, -1249295, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(-0.175F, 0.78125F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -2.4F, 0.0F, -2.4F, 4.8F, 7.5F, 4.8F, var18, l, o);
      box(m, vc, -2.6F, 7.5F, -3.2F, 5.2F, 4.0F, 5.8F, var19, l, o);
      box(m, vc, -2.7F, 11.2F, -3.4F, 5.4F, 0.9F, 6.2F, -15004151, l, o);
      box(m, vc, -2.4F, 12.0F, -3.0F, 4.8F, 0.4F, 1.6F, -15923708, l, o);
      box(m, vc, -2.4F, 12.0F, 0.6F, 4.8F, 0.4F, 1.6F, -15923708, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.175F, 0.78125F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -2.4F, 0.0F, -2.4F, 4.8F, 7.5F, 4.8F, var18, l, o);
      box(m, vc, -2.6F, 7.5F, -3.2F, 5.2F, 4.0F, 5.8F, var19, l, o);
      box(m, vc, -2.7F, 11.2F, -3.4F, 5.4F, 0.9F, 6.2F, -15004151, l, o);
      box(m, vc, -2.4F, 12.0F, -3.0F, 4.8F, 0.4F, 1.6F, -15923708, l, o);
      box(m, vc, -2.4F, 12.0F, 0.6F, 4.8F, 0.4F, 1.6F, -15923708, l, o);
      m.method_22909();
   }

   private void renderNaruto(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      int var10 = -668761;
      short var11 = -10496;
      short var12 = -24576;
      int var13 = -37120;
      int var14 = -1683200;
      int var15 = -15918294;
      int var16 = -15064194;
      int var17 = -3155748;
      int var18 = -16611119;
      byte var19 = -1;
      int var20 = -14935012;
      int var21 = -14606047;
      int var22 = -4179669;
      int var23 = -12703965;
      int var24 = -10665929;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch));
      box(m, vc, -3.5F, -7.5F, -3.5F, 7.0F, 7.5F, 7.0F, var10, l, o);
      box(m, vc, -4.0F, -9.5F, -4.0F, 8.0F, 2.5F, 8.0F, var11, l, o);
      box(m, vc, -2.5F, -11.2F, -2.5F, 5.0F, 2.0F, 5.0F, var11, l, o);
      box(m, vc, -1.2F, -12.4F, -1.2F, 2.4F, 1.4F, 2.4F, var11, l, o);
      box(m, vc, -4.2F, -8.0F, 1.2F, 8.4F, 3.8F, 3.2F, var11, l, o);
      box(m, vc, -4.2F, -6.5F, -3.2F, 0.9F, 2.4F, 2.0F, var11, l, o);
      box(m, vc, 3.3F, -6.5F, -3.2F, 0.9F, 2.4F, 2.0F, var11, l, o);
      box(m, vc, -3.8F, -7.2F, -3.8F, 7.6F, 1.8F, 7.6F, var16, l, o);
      box(m, vc, -2.4F, -7.0F, -4.05F, 4.8F, 1.4F, 0.35F, var17, l, o);
      int var25 = -13154481;
      box(m, vc, -2.2F, -6.9F, -4.12F, 0.35F, 0.35F, 0.15F, var25, l, o);
      box(m, vc, 1.85F, -6.9F, -4.12F, 0.35F, 0.35F, 0.15F, var25, l, o);
      box(m, vc, -2.2F, -5.9F, -4.12F, 0.35F, 0.35F, 0.15F, var25, l, o);
      box(m, vc, 1.85F, -5.9F, -4.12F, 0.35F, 0.35F, 0.15F, var25, l, o);
      box(m, vc, -0.15F, -6.9F, -4.12F, 0.3F, 0.3F, 0.15F, var25, l, o);
      box(m, vc, -0.15F, -5.9F, -4.12F, 0.3F, 0.3F, 0.15F, var25, l, o);
      box(m, vc, -0.6F, -6.6F, -4.1F, 1.2F, 0.6F, 0.15F, var20, l, o);
      box(m, vc, -0.8F, -6.8F, 3.8F, 1.6F, 1.2F, 0.6F, var16, l, o);
      float var26 = (float)Math.sin(age * 0.25 + swing * 0.6) * 0.3F;
      float var27 = (float)Math.cos(age * 0.22 + swing * 0.5) * 0.25F;
      m.method_22903();
      m.method_46416(-0.025F, -0.40625F, 0.2625F);
      m.method_22907(class_7833.field_40716.rotation(var26));
      m.method_22907(class_7833.field_40714.rotation(-0.35F + var27 * 0.4F));
      box(m, vc, -0.6F, 0.0F, 0.0F, 1.2F, 6.5F, 0.3F, var16, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.025F, -0.40625F, 0.2625F);
      m.method_22907(class_7833.field_40716.rotation(-var27));
      m.method_22907(class_7833.field_40714.rotation(-0.45F + var26 * 0.4F));
      box(m, vc, -0.6F, 0.0F, 0.0F, 1.2F, 7.0F, 0.3F, var16, l, o);
      m.method_22909();
      box(m, vc, -2.4F, -5.2F, -3.65F, 1.6F, 1.5F, 0.2F, var19, l, o);
      box(m, vc, 0.8F, -5.2F, -3.65F, 1.6F, 1.5F, 0.2F, var19, l, o);
      box(m, vc, -1.8F, -4.8F, -3.72F, 0.8F, 0.9F, 0.15F, var18, l, o);
      box(m, vc, 1.0F, -4.8F, -3.72F, 0.8F, 0.9F, 0.15F, var18, l, o);
      box(m, vc, -3.2F, -4.2F, -3.6F, 1.2F, 0.25F, 0.15F, var20, l, o);
      box(m, vc, -3.3F, -3.6F, -3.6F, 1.3F, 0.25F, 0.15F, var20, l, o);
      box(m, vc, -3.2F, -3.0F, -3.6F, 1.2F, 0.25F, 0.15F, var20, l, o);
      box(m, vc, 2.0F, -4.2F, -3.6F, 1.2F, 0.25F, 0.15F, var20, l, o);
      box(m, vc, 2.0F, -3.6F, -3.6F, 1.3F, 0.25F, 0.15F, var20, l, o);
      box(m, vc, 2.0F, -3.0F, -3.6F, 1.2F, 0.25F, 0.15F, var20, l, o);
      box(m, vc, -1.4F, -2.2F, -3.6F, 2.8F, 0.8F, 0.15F, -7855582, l, o);
      box(m, vc, -1.0F, -2.15F, -3.65F, 2.0F, 0.35F, 0.12F, var19, l, o);
      m.method_22909();
      m.method_22903();
      box(m, vc, -2.8F, -1.2F, -2.6F, 5.6F, 1.6F, 5.2F, var15, l, o);
      box(m, vc, -2.6F, -1.0F, -2.4F, 5.2F, 1.4F, 0.35F, var19, l, o);
      box(m, vc, -4.0F, 0.0F, -2.2F, 8.0F, 11.0F, 4.4F, var13, l, o);
      box(m, vc, -4.1F, 0.0F, -2.3F, 8.2F, 2.2F, 4.6F, var15, l, o);
      box(m, vc, -1.2F, 4.0F, 2.25F, 2.4F, 2.4F, 0.25F, var19, l, o);
      box(m, vc, -0.6F, 4.6F, 2.3F, 1.2F, 1.2F, 0.25F, -1630941, l, o);
      box(m, vc, -3.2F, 0.8F, -2.35F, 1.3F, 8.5F, 4.8F, var24, l, o);
      box(m, vc, -2.8F, 4.2F, -2.42F, 1.6F, 0.8F, 0.25F, var17, l, o);
      box(m, vc, -5.6F, 3.2F, 2.5F, 11.2F, 3.2F, 3.2F, var22, l, o);
      box(m, vc, -6.6F, 3.0F, 2.3F, 1.2F, 3.6F, 3.6F, var23, l, o);
      box(m, vc, 5.4F, 3.0F, 2.3F, 1.2F, 3.6F, 3.6F, var23, l, o);
      box(m, vc, -2.2F, 3.1F, 2.45F, 4.4F, 3.4F, 3.3F, var19, l, o);
      box(m, vc, -1.0F, 3.3F, 5.8F, 2.0F, 3.0F, 0.25F, var20, l, o);
      m.method_22909();
      this.renderStandardArms(m, vc, swing, amp, var13, var13, var10, l, o);
      m.method_22903();
      m.method_46416(-0.34375F, 0.65625F, -0.1125F);
      this.applyRightArm(m, swing, amp);
      m.method_22907(class_7833.field_40714.rotationDegrees(65.0F));
      int var28 = -5194043;
      int var29 = -8875876;
      int var30 = -1249295;
      int var31 = -13154481;
      box(m, vc, -0.7F, 6.2F, -0.2F, 1.4F, 1.4F, 0.4F, var31, l, o);
      box(m, vc, -0.3F, 6.5F, -0.3F, 0.6F, 0.8F, 0.6F, -16777216, l, o);
      box(m, vc, -0.5F, 2.2F, -0.5F, 1.0F, 4.0F, 1.0F, var30, l, o);
      box(m, vc, -0.55F, 3.2F, -0.55F, 1.1F, 0.3F, 1.1F, -13154481, l, o);
      box(m, vc, -0.8F, 1.8F, -0.8F, 1.6F, 0.4F, 1.6F, var29, l, o);
      box(m, vc, -1.2F, -2.5F, -0.3F, 2.4F, 4.3F, 0.6F, var28, l, o);
      box(m, vc, -0.6F, -4.8F, -0.25F, 1.2F, 2.4F, 0.5F, var28, l, o);
      box(m, vc, -0.2F, -5.8F, -0.2F, 0.4F, 1.1F, 0.4F, var29, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.34375F, 0.65625F, -0.1125F);
      this.applyLeftArm(m, swing, amp);
      m.method_22907(class_7833.field_40714.rotationDegrees(40.0F));
      byte var32 = -1;
      int var33 = -16718337;
      int var34 = -8333057;
      int var35 = -16739862;
      m.method_22903();
      m.method_46416(0.0F, 0.15625F, -0.125F);
      box(m, vc, -1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, var32, l, o);
      box(m, vc, -1.4F, -1.4F, -1.4F, 2.8F, 2.8F, 2.8F, var34, l, o);
      box(m, vc, -1.8F, -1.8F, -1.8F, 3.6F, 3.6F, 3.6F, var33, l, o);
      float var36 = age * 18.0F;
      m.method_22903();
      m.method_22907(class_7833.field_40718.rotationDegrees(var36));
      box(m, vc, -2.4F, -0.3F, -0.3F, 4.8F, 0.6F, 0.6F, var33, l, o);
      box(m, vc, -0.3F, -2.4F, -0.3F, 0.6F, 4.8F, 0.6F, var33, l, o);
      m.method_22909();
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(-var36 * 1.3F));
      m.method_22907(class_7833.field_40714.rotationDegrees(45.0F));
      box(m, vc, -2.2F, -0.25F, -0.25F, 4.4F, 0.5F, 0.5F, var32, l, o);
      box(m, vc, -0.25F, -2.2F, -0.25F, 0.5F, 4.4F, 0.5F, var32, l, o);
      m.method_22909();
      m.method_22903();
      m.method_22907(class_7833.field_40714.rotationDegrees(var36 * 1.1F));
      box(m, vc, -0.3F, -0.3F, -2.2F, 0.6F, 0.6F, 4.4F, var35, l, o);
      m.method_22909();
      float var37 = age * 12.0F;
      m.method_22903();
      m.method_22907(class_7833.field_40716.rotationDegrees(var37));
      m.method_46416(0.15625F, 0.0F, 0.0F);
      box(m, vc, -0.2F, -0.2F, -0.2F, 0.4F, 0.4F, 0.4F, var32, l, o);
      m.method_22909();
      m.method_22903();
      m.method_22907(class_7833.field_40718.rotationDegrees(-var37 * 1.2F));
      m.method_46416(0.0F, 0.15625F, 0.0F);
      box(m, vc, -0.2F, -0.2F, -0.2F, 0.4F, 0.4F, 0.4F, var34, l, o);
      m.method_22909();
      m.method_22909();
      m.method_22909();
      this.renderNarutoLegs(m, vc, swing, amp, var13, var19, var21, var10, l, o);
   }

   private void applyRightLeg(class_4587 m, float swing, float amp) {
      if (this.sitting) {
         m.method_22907(class_7833.field_40714.rotationDegrees(-80.0F));
         m.method_22907(class_7833.field_40716.rotationDegrees(18.0F));
         m.method_22907(class_7833.field_40718.rotationDegrees(4.5F));
      } else if (this.flying) {
         float var4 = class_3532.method_15362(swing * 0.4F) * 0.15F;
         m.method_22907(class_7833.field_40714.rotation(0.25F + var4));
      } else {
         float var5 = class_3532.method_15362(swing * 0.6662F) * 1.4F * amp;
         m.method_22907(class_7833.field_40714.rotation(var5));
      }
   }

   private void applyLeftLeg(class_4587 m, float swing, float amp) {
      if (this.sitting) {
         m.method_22907(class_7833.field_40714.rotationDegrees(-80.0F));
         m.method_22907(class_7833.field_40716.rotationDegrees(-18.0F));
         m.method_22907(class_7833.field_40718.rotationDegrees(-4.5F));
      } else if (this.flying) {
         float var4 = class_3532.method_15362(swing * 0.4F + (float) Math.PI) * 0.15F;
         m.method_22907(class_7833.field_40714.rotation(0.25F + var4));
      } else {
         float var5 = class_3532.method_15362(swing * 0.6662F + (float) Math.PI) * 1.4F * amp;
         m.method_22907(class_7833.field_40714.rotation(var5));
      }
   }

   private void applyRightArm(class_4587 m, float swing, float amp) {
      this.applyRightArm(m, swing, amp, 0.0F);
   }

   private void applyRightArm(class_4587 m, float swing, float amp, float baseOffsetDeg) {
      if (this.sitting) {
         m.method_22907(class_7833.field_40714.rotationDegrees(-36.0F + baseOffsetDeg));
      } else if (this.flying) {
         float var5 = class_3532.method_15362(swing * 0.4F + (float) Math.PI) * 0.1F;
         m.method_22907(class_7833.field_40714.rotation(0.2F + var5 + baseOffsetDeg * 0.0174533F));
      } else {
         float var6 = class_3532.method_15362(swing * 0.6662F + (float) Math.PI) * 2.0F * amp * 0.5F;
         m.method_22907(class_7833.field_40714.rotation(var6 + baseOffsetDeg * 0.0174533F));
      }
   }

   private void applyLeftArm(class_4587 m, float swing, float amp) {
      this.applyLeftArm(m, swing, amp, 0.0F);
   }

   private void applyLeftArm(class_4587 m, float swing, float amp, float baseOffsetDeg) {
      if (this.sitting) {
         m.method_22907(class_7833.field_40714.rotationDegrees(-36.0F + baseOffsetDeg));
      } else if (this.flying) {
         float var5 = class_3532.method_15362(swing * 0.4F) * 0.1F;
         m.method_22907(class_7833.field_40714.rotation(0.2F + var5 + baseOffsetDeg * 0.0174533F));
      } else {
         float var6 = class_3532.method_15362(swing * 0.6662F) * 2.0F * amp * 0.5F;
         m.method_22907(class_7833.field_40714.rotation(var6 + baseOffsetDeg * 0.0174533F));
      }
   }

   private void renderStandardArms(class_4587 m, class_4588 vc, float swing, float amp, int sleeveCol, int forearmCol, int handCol, int l, int o) {
      m.method_22903();
      m.method_46416(-0.3125F, 0.125F, 0.0F);
      this.applyRightArm(m, swing, amp);
      box(m, vc, -2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, sleeveCol, l, o);
      box(m, vc, -2.0F, 2.0F, -2.0F, 4.0F, 5.0F, 4.0F, forearmCol, l, o);
      box(m, vc, -2.2F, 7.0F, -2.2F, 4.4F, 3.2F, 4.4F, handCol, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.3125F, 0.125F, 0.0F);
      this.applyLeftArm(m, swing, amp);
      box(m, vc, -2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, sleeveCol, l, o);
      box(m, vc, -2.0F, 2.0F, -2.0F, 4.0F, 5.0F, 4.0F, forearmCol, l, o);
      box(m, vc, -2.2F, 7.0F, -2.2F, 4.4F, 3.2F, 4.4F, handCol, l, o);
      m.method_22909();
   }

   private void renderSantaArms(class_4587 m, class_4588 vc, float swing, float amp, int coatCol, int cuffCol, int mittenCol, int l, int o) {
      m.method_22903();
      m.method_46416(-0.3125F, 0.125F, 0.0F);
      this.applyRightArm(m, swing, amp);
      box(m, vc, -2.2F, -2.0F, -2.2F, 4.4F, 8.0F, 4.4F, coatCol, l, o);
      box(m, vc, -2.5F, 6.0F, -2.5F, 5.0F, 1.8F, 5.0F, cuffCol, l, o);
      box(m, vc, -2.1F, 7.8F, -2.1F, 4.2F, 3.2F, 4.2F, mittenCol, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.3125F, 0.125F, 0.0F);
      this.applyLeftArm(m, swing, amp);
      box(m, vc, -2.2F, -2.0F, -2.2F, 4.4F, 8.0F, 4.4F, coatCol, l, o);
      box(m, vc, -2.5F, 6.0F, -2.5F, 5.0F, 1.8F, 5.0F, cuffCol, l, o);
      box(m, vc, -2.1F, 7.8F, -2.1F, 4.2F, 3.2F, 4.2F, mittenCol, l, o);
      m.method_22909();
   }

   private void renderSantaBoots(class_4587 m, class_4588 vc, float swing, float amp, int pantsCol, int bootCol, int furCol, int buckleCol, int l, int o) {
      m.method_22903();
      m.method_46416(-0.11875F, 0.75F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -2.1F, 0.0F, -2.1F, 4.2F, 7.8F, 4.2F, pantsCol, l, o);
      box(m, vc, -2.4F, 7.8F, -2.4F, 4.8F, 1.5F, 4.8F, furCol, l, o);
      box(m, vc, -2.2F, 9.3F, -2.8F, 4.4F, 2.7F, 5.4F, bootCol, l, o);
      box(m, vc, -2.3F, 9.6F, -1.0F, 0.2F, 1.2F, 1.5F, buckleCol, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.75F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -2.1F, 0.0F, -2.1F, 4.2F, 7.8F, 4.2F, pantsCol, l, o);
      box(m, vc, -2.4F, 7.8F, -2.4F, 4.8F, 1.5F, 4.8F, furCol, l, o);
      box(m, vc, -2.2F, 9.3F, -2.8F, 4.4F, 2.7F, 5.4F, bootCol, l, o);
      box(m, vc, 2.1F, 9.6F, -1.0F, 0.2F, 1.2F, 1.5F, buckleCol, l, o);
      m.method_22909();
   }

   private void renderDoraemonArms(class_4587 m, class_4588 vc, float swing, float amp, int blueCol, int whiteCol, int l, int o) {
      int var9 = -1630941;
      int var10 = -1249295;
      int var11 = -3769036;
      int var12 = -11922155;
      m.method_22903();
      m.method_46416(-0.3125F, 0.125F, 0.0F);
      this.applyRightArm(m, swing, amp);
      box(m, vc, -1.8F, -1.5F, -1.8F, 3.6F, 7.2F, 3.6F, blueCol, l, o);
      box(m, vc, -2.0F, 5.5F, -2.0F, 4.0F, 0.45F, 4.0F, var9, l, o);
      box(m, vc, -2.2F, 5.9F, -2.2F, 4.4F, 4.4F, 4.4F, whiteCol, l, o);
      box(m, vc, -2.5F, 6.4F, -1.8F, 5.0F, 3.4F, 3.6F, whiteCol, l, o);
      box(m, vc, -1.8F, 6.4F, -2.5F, 3.6F, 3.4F, 5.0F, whiteCol, l, o);
      box(m, vc, -0.9F, 7.2F, -2.6F, 1.8F, 1.8F, 0.15F, var10, l, o);
      box(m, vc, -1.6F, 5.6F, -4.4F, 3.2F, 1.1F, 3.2F, var11, l, o);
      box(m, vc, -1.75F, 6.7F, -4.5F, 3.5F, 0.7F, 3.5F, var12, l, o);
      box(m, vc, -1.6F, 7.4F, -4.4F, 3.2F, 1.1F, 3.2F, var11, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.3125F, 0.125F, 0.0F);
      this.applyLeftArm(m, swing, amp);
      box(m, vc, -1.8F, -1.5F, -1.8F, 3.6F, 7.2F, 3.6F, blueCol, l, o);
      box(m, vc, -2.0F, 5.5F, -2.0F, 4.0F, 0.45F, 4.0F, var9, l, o);
      box(m, vc, -2.2F, 5.9F, -2.2F, 4.4F, 4.4F, 4.4F, whiteCol, l, o);
      box(m, vc, -2.5F, 6.4F, -1.8F, 5.0F, 3.4F, 3.6F, whiteCol, l, o);
      box(m, vc, -1.8F, 6.4F, -2.5F, 3.6F, 3.4F, 5.0F, whiteCol, l, o);
      box(m, vc, -0.9F, 7.2F, -2.6F, 1.8F, 1.8F, 0.15F, var10, l, o);
      m.method_22909();
   }

   private void renderDoraemonFeet(class_4587 m, class_4588 vc, float swing, float amp, int blueCol, int whiteCol, int l, int o) {
      int var9 = -3155748;
      m.method_22903();
      m.method_46416(-0.15F, 1.125F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -1.6F, 0.0F, -1.6F, 3.2F, 3.0F, 3.2F, blueCol, l, o);
      box(m, vc, -2.4F, 3.0F, -3.8F, 4.8F, 2.9F, 6.8F, whiteCol, l, o);
      box(m, vc, -2.6F, 3.5F, -3.2F, 5.2F, 2.2F, 5.6F, whiteCol, l, o);
      box(m, vc, -2.0F, 3.4F, -4.3F, 4.0F, 2.3F, 0.8F, whiteCol, l, o);
      box(m, vc, -2.0F, 5.8F, -3.4F, 4.0F, 0.35F, 5.8F, var9, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.15F, 1.125F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -1.6F, 0.0F, -1.6F, 3.2F, 3.0F, 3.2F, blueCol, l, o);
      box(m, vc, -2.4F, 3.0F, -3.8F, 4.8F, 2.9F, 6.8F, whiteCol, l, o);
      box(m, vc, -2.6F, 3.5F, -3.2F, 5.2F, 2.2F, 5.6F, whiteCol, l, o);
      box(m, vc, -2.0F, 3.4F, -4.3F, 4.0F, 2.3F, 0.8F, whiteCol, l, o);
      box(m, vc, -2.0F, 5.8F, -3.4F, 4.0F, 0.35F, 5.8F, var9, l, o);
      m.method_22909();
   }

   private void renderLegsWithShoes(class_4587 m, class_4588 vc, float swing, float amp, int pantsCol, int shoesCol, int l, int o) {
      m.method_22903();
      m.method_46416(-0.11875F, 0.75F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, pantsCol, l, o);
      box(m, vc, -2.1F, 9.0F, -2.6F, 4.2F, 3.0F, 5.0F, shoesCol, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.75F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, pantsCol, l, o);
      box(m, vc, -2.1F, 9.0F, -2.6F, 4.2F, 3.0F, 5.0F, shoesCol, l, o);
      m.method_22909();
   }

   private void renderMickeyArms(class_4587 m, class_4588 vc, float swing, float amp, int armCol, int gloveCol, int l, int o) {
      int var9 = -16119286;
      int var10 = -1249295;
      m.method_22903();
      m.method_46416(-0.28125F, 0.15625F, 0.0F);
      this.applyRightArm(m, swing, amp);
      box(m, vc, -1.0F, -1.2F, -1.0F, 2.0F, 7.8F, 2.0F, armCol, l, o);
      box(m, vc, -2.8F, 5.0F, -2.8F, 5.6F, 1.3F, 5.6F, gloveCol, l, o);
      box(m, vc, -2.6F, 4.4F, -2.6F, 5.2F, 0.7F, 5.2F, var10, l, o);
      box(m, vc, -2.5F, 6.2F, -2.5F, 5.0F, 4.2F, 5.0F, gloveCol, l, o);
      box(m, vc, 1.6F, 7.0F, -1.8F, 1.5F, 2.4F, 1.8F, gloveCol, l, o);
      box(m, vc, 1.8F, 9.2F, -1.5F, 1.2F, 1.2F, 1.4F, gloveCol, l, o);
      box(m, vc, -2.4F, 10.2F, -2.1F, 1.1F, 1.8F, 1.4F, gloveCol, l, o);
      box(m, vc, -1.1F, 10.2F, -2.1F, 1.1F, 2.0F, 1.4F, gloveCol, l, o);
      box(m, vc, 0.2F, 10.2F, -2.1F, 1.1F, 1.7F, 1.4F, gloveCol, l, o);
      box(m, vc, -1.6F, 6.8F, -2.62F, 0.45F, 2.8F, 0.2F, var9, l, o);
      box(m, vc, -0.2F, 6.8F, -2.62F, 0.45F, 2.8F, 0.2F, var9, l, o);
      box(m, vc, 1.2F, 6.8F, -2.62F, 0.45F, 2.8F, 0.2F, var9, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.28125F, 0.15625F, 0.0F);
      this.applyLeftArm(m, swing, amp);
      box(m, vc, -1.0F, -1.2F, -1.0F, 2.0F, 7.8F, 2.0F, armCol, l, o);
      box(m, vc, -2.8F, 5.0F, -2.8F, 5.6F, 1.3F, 5.6F, gloveCol, l, o);
      box(m, vc, -2.6F, 4.4F, -2.6F, 5.2F, 0.7F, 5.2F, var10, l, o);
      box(m, vc, -2.5F, 6.2F, -2.5F, 5.0F, 4.2F, 5.0F, gloveCol, l, o);
      box(m, vc, -3.1F, 7.0F, -1.8F, 1.5F, 2.4F, 1.8F, gloveCol, l, o);
      box(m, vc, -3.0F, 9.2F, -1.5F, 1.2F, 1.2F, 1.4F, gloveCol, l, o);
      box(m, vc, -1.3F, 10.2F, -2.1F, 1.1F, 1.7F, 1.4F, gloveCol, l, o);
      box(m, vc, 0.0F, 10.2F, -2.1F, 1.1F, 2.0F, 1.4F, gloveCol, l, o);
      box(m, vc, 1.3F, 10.2F, -2.1F, 1.1F, 1.8F, 1.4F, gloveCol, l, o);
      box(m, vc, -1.6F, 6.8F, -2.62F, 0.45F, 2.8F, 0.2F, var9, l, o);
      box(m, vc, -0.2F, 6.8F, -2.62F, 0.45F, 2.8F, 0.2F, var9, l, o);
      box(m, vc, 1.2F, 6.8F, -2.62F, 0.45F, 2.8F, 0.2F, var9, l, o);
      m.method_22909();
   }

   private void renderMickeyShoes(class_4587 m, class_4588 vc, float swing, float amp, int pantsCol, int shoeCol, int shoeDarkCol, int l, int o) {
      int var10 = -4753152;
      int var11 = -7710208;
      m.method_22903();
      m.method_46416(-0.11875F, 0.71875F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -0.9F, 0.0F, -0.9F, 1.8F, 8.2F, 1.8F, pantsCol, l, o);
      box(m, vc, -2.8F, 7.8F, -4.6F, 5.6F, 4.0F, 8.4F, shoeCol, l, o);
      box(m, vc, -2.6F, 8.2F, -5.6F, 5.2F, 3.2F, 1.2F, shoeCol, l, o);
      box(m, vc, -2.0F, 8.8F, -6.1F, 4.0F, 2.2F, 0.7F, shoeCol, l, o);
      box(m, vc, -2.4F, 6.8F, -2.8F, 4.8F, 1.4F, 4.0F, -9677, l, o);
      box(m, vc, -2.95F, 11.4F, -5.8F, 5.9F, 0.9F, 9.8F, shoeDarkCol, l, o);
      box(m, vc, -2.7F, 10.8F, -6.1F, 5.4F, 0.8F, 0.5F, shoeDarkCol, l, o);
      box(m, vc, -2.8F, 12.0F, 1.2F, 5.6F, 0.6F, 2.6F, var10, l, o);
      box(m, vc, -2.5F, 12.0F, -5.2F, 5.0F, 0.35F, 2.0F, var11, l, o);
      box(m, vc, -2.5F, 12.0F, -2.6F, 5.0F, 0.35F, 2.2F, var11, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.71875F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -0.9F, 0.0F, -0.9F, 1.8F, 8.2F, 1.8F, pantsCol, l, o);
      box(m, vc, -2.8F, 7.8F, -4.6F, 5.6F, 4.0F, 8.4F, shoeCol, l, o);
      box(m, vc, -2.6F, 8.2F, -5.6F, 5.2F, 3.2F, 1.2F, shoeCol, l, o);
      box(m, vc, -2.0F, 8.8F, -6.1F, 4.0F, 2.2F, 0.7F, shoeCol, l, o);
      box(m, vc, -2.4F, 6.8F, -2.8F, 4.8F, 1.4F, 4.0F, -9677, l, o);
      box(m, vc, -2.95F, 11.4F, -5.8F, 5.9F, 0.9F, 9.8F, shoeDarkCol, l, o);
      box(m, vc, -2.7F, 10.8F, -6.1F, 5.4F, 0.8F, 0.5F, shoeDarkCol, l, o);
      box(m, vc, -2.8F, 12.0F, 1.2F, 5.6F, 0.6F, 2.6F, var10, l, o);
      box(m, vc, -2.5F, 12.0F, -5.2F, 5.0F, 0.35F, 2.0F, var11, l, o);
      box(m, vc, -2.5F, 12.0F, -2.6F, 5.0F, 0.35F, 2.2F, var11, l, o);
      m.method_22909();
   }

   private void renderPikachuArms(class_4587 m, class_4588 vc, float swing, float amp, int yellowCol, int clawCol, int l, int o) {
      m.method_22903();
      m.method_46416(-0.3F, 0.15625F, 0.0F);
      this.applyRightArm(m, swing, amp, -11.5F);
      box(m, vc, -1.8F, -1.0F, -1.8F, 3.6F, 8.0F, 3.6F, yellowCol, l, o);
      box(m, vc, -1.4F, 6.8F, -2.0F, 2.8F, 0.6F, 0.3F, clawCol, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.3F, 0.15625F, 0.0F);
      this.applyLeftArm(m, swing, amp, -11.5F);
      box(m, vc, -1.8F, -1.0F, -1.8F, 3.6F, 8.0F, 3.6F, yellowCol, l, o);
      box(m, vc, -1.4F, 6.8F, -2.0F, 2.8F, 0.6F, 0.3F, clawCol, l, o);
      m.method_22909();
   }

   private void renderMinionArms(class_4587 m, class_4588 vc, float swing, float amp, int yellowCol, int gloveCol, int l, int o) {
      m.method_22903();
      m.method_46416(-0.30625F, 0.125F, 0.0F);
      this.applyRightArm(m, swing, amp);
      box(m, vc, -1.6F, -1.5F, -1.6F, 3.2F, 7.5F, 3.2F, yellowCol, l, o);
      box(m, vc, -2.0F, 6.0F, -2.0F, 4.0F, 4.2F, 4.0F, gloveCol, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.30625F, 0.125F, 0.0F);
      this.applyLeftArm(m, swing, amp);
      box(m, vc, -1.6F, -1.5F, -1.6F, 3.2F, 7.5F, 3.2F, yellowCol, l, o);
      box(m, vc, -2.0F, 6.0F, -2.0F, 4.0F, 4.2F, 4.0F, gloveCol, l, o);
      m.method_22909();
   }

   private void renderMinionBoots(class_4587 m, class_4588 vc, float swing, float amp, int denimCol, int bootCol, int soleCol, int l, int o) {
      m.method_22903();
      m.method_46416(-0.11875F, 0.75F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 8.5F, 4.0F, denimCol, l, o);
      box(m, vc, -2.3F, 8.5F, -2.8F, 4.6F, 2.8F, 5.2F, bootCol, l, o);
      box(m, vc, -2.4F, 11.0F, -3.0F, 4.8F, 1.0F, 5.6F, soleCol, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.75F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 8.5F, 4.0F, denimCol, l, o);
      box(m, vc, -2.3F, 8.5F, -2.8F, 4.6F, 2.8F, 5.2F, bootCol, l, o);
      box(m, vc, -2.4F, 11.0F, -3.0F, 4.8F, 1.0F, 5.6F, soleCol, l, o);
      m.method_22909();
   }

   private void renderSimpsonLegs(class_4587 m, class_4588 vc, float swing, float amp, int jeansCol, int cuffCol, int shoeCol, int l, int o) {
      m.method_22903();
      m.method_46416(-0.11875F, 0.75F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 8.6F, 4.0F, jeansCol, l, o);
      box(m, vc, -2.15F, 8.4F, -2.15F, 4.3F, 0.7F, 4.3F, cuffCol, l, o);
      box(m, vc, -2.2F, 9.1F, -2.8F, 4.4F, 2.9F, 5.2F, shoeCol, l, o);
      box(m, vc, -1.0F, 9.2F, -2.9F, 2.0F, 0.4F, 1.2F, -1, l, o);
      box(m, vc, -1.0F, 9.8F, -2.9F, 2.0F, 0.4F, 1.2F, -1, l, o);
      box(m, vc, -2.25F, 11.5F, -3.0F, 4.5F, 0.6F, 5.5F, -14606047, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.75F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 8.6F, 4.0F, jeansCol, l, o);
      box(m, vc, -2.15F, 8.4F, -2.15F, 4.3F, 0.7F, 4.3F, cuffCol, l, o);
      box(m, vc, -2.2F, 9.1F, -2.8F, 4.4F, 2.9F, 5.2F, shoeCol, l, o);
      box(m, vc, -1.0F, 9.2F, -2.9F, 2.0F, 0.4F, 1.2F, -1, l, o);
      box(m, vc, -1.0F, 9.8F, -2.9F, 2.0F, 0.4F, 1.2F, -1, l, o);
      box(m, vc, -2.25F, 11.5F, -3.0F, 4.5F, 0.6F, 5.5F, -14606047, l, o);
      m.method_22909();
   }

   private void renderSpongeBobLegs(
      class_4587 m, class_4588 vc, float swing, float amp, int pantsCol, int skinCol, int sockCol, int stripeBlue, int stripeRed, int shoeCol, int l, int o
   ) {
      m.method_22903();
      m.method_46416(-0.11875F, 0.75F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -1.8F, 0.0F, -1.8F, 3.6F, 2.2F, 3.6F, pantsCol, l, o);
      box(m, vc, -1.0F, 2.2F, -1.0F, 2.0F, 4.2F, 2.0F, skinCol, l, o);
      box(m, vc, -1.3F, 6.4F, -1.3F, 2.6F, 3.2F, 2.6F, sockCol, l, o);
      box(m, vc, -1.35F, 7.1F, -1.35F, 2.7F, 0.45F, 2.7F, stripeBlue, l, o);
      box(m, vc, -1.35F, 7.8F, -1.35F, 2.7F, 0.45F, 2.7F, stripeRed, l, o);
      box(m, vc, -1.8F, 9.6F, -3.0F, 3.6F, 2.4F, 5.0F, shoeCol, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.75F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -1.8F, 0.0F, -1.8F, 3.6F, 2.2F, 3.6F, pantsCol, l, o);
      box(m, vc, -1.0F, 2.2F, -1.0F, 2.0F, 4.2F, 2.0F, skinCol, l, o);
      box(m, vc, -1.3F, 6.4F, -1.3F, 2.6F, 3.2F, 2.6F, sockCol, l, o);
      box(m, vc, -1.35F, 7.1F, -1.35F, 2.7F, 0.45F, 2.7F, stripeBlue, l, o);
      box(m, vc, -1.35F, 7.8F, -1.35F, 2.7F, 0.45F, 2.7F, stripeRed, l, o);
      box(m, vc, -1.8F, 9.6F, -3.0F, 3.6F, 2.4F, 5.0F, shoeCol, l, o);
      m.method_22909();
   }

   private void renderMarioBoots(class_4587 m, class_4588 vc, float swing, float amp, int pantsCol, int cuffCol, int bootCol, int stitchCol, int l, int o) {
      int var11 = -12703965;
      int var12 = -14935012;
      int var13 = -15658735;
      m.method_22903();
      m.method_46416(-0.11875F, 0.75F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 7.5F, 4.0F, pantsCol, l, o);
      box(m, vc, -2.3F, 7.0F, -2.3F, 4.6F, 1.5F, 4.6F, cuffCol, l, o);
      box(m, vc, -2.35F, 6.9F, -2.35F, 4.7F, 0.2F, 4.7F, stitchCol, l, o);
      box(m, vc, -2.2F, 8.4F, -3.2F, 4.4F, 2.8F, 5.8F, bootCol, l, o);
      box(m, vc, -2.0F, 9.0F, -4.4F, 4.0F, 2.2F, 1.4F, bootCol, l, o);
      box(m, vc, -1.6F, 9.6F, -4.9F, 3.2F, 1.4F, 0.6F, bootCol, l, o);
      box(m, vc, -1.6F, 8.2F, -2.8F, 0.35F, 1.2F, 0.2F, stitchCol, l, o);
      box(m, vc, 1.25F, 8.2F, -2.8F, 0.35F, 1.2F, 0.2F, stitchCol, l, o);
      box(m, vc, -1.2F, 8.6F, -2.85F, 2.4F, 0.3F, 0.15F, var11, l, o);
      box(m, vc, -2.3F, 11.0F, -4.6F, 4.6F, 0.5F, 7.4F, var11, l, o);
      box(m, vc, -2.35F, 11.5F, -4.7F, 4.7F, 0.8F, 7.6F, var12, l, o);
      box(m, vc, -2.2F, 12.2F, 0.6F, 4.4F, 0.6F, 2.4F, var13, l, o);
      box(m, vc, -2.0F, 12.2F, -4.2F, 4.0F, 0.35F, 1.4F, var13, l, o);
      box(m, vc, -2.0F, 12.2F, -2.0F, 4.0F, 0.35F, 1.4F, var13, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.75F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 7.5F, 4.0F, pantsCol, l, o);
      box(m, vc, -2.3F, 7.0F, -2.3F, 4.6F, 1.5F, 4.6F, cuffCol, l, o);
      box(m, vc, -2.35F, 6.9F, -2.35F, 4.7F, 0.2F, 4.7F, stitchCol, l, o);
      box(m, vc, -2.2F, 8.4F, -3.2F, 4.4F, 2.8F, 5.8F, bootCol, l, o);
      box(m, vc, -2.0F, 9.0F, -4.4F, 4.0F, 2.2F, 1.4F, bootCol, l, o);
      box(m, vc, -1.6F, 9.6F, -4.9F, 3.2F, 1.4F, 0.6F, bootCol, l, o);
      box(m, vc, -1.6F, 8.2F, -2.8F, 0.35F, 1.2F, 0.2F, stitchCol, l, o);
      box(m, vc, 1.25F, 8.2F, -2.8F, 0.35F, 1.2F, 0.2F, stitchCol, l, o);
      box(m, vc, -1.2F, 8.6F, -2.85F, 2.4F, 0.3F, 0.15F, var11, l, o);
      box(m, vc, -2.3F, 11.0F, -4.6F, 4.6F, 0.5F, 7.4F, var11, l, o);
      box(m, vc, -2.35F, 11.5F, -4.7F, 4.7F, 0.8F, 7.6F, var12, l, o);
      box(m, vc, -2.2F, 12.2F, 0.6F, 4.4F, 0.6F, 2.4F, var13, l, o);
      box(m, vc, -2.0F, 12.2F, -4.2F, 4.0F, 0.35F, 1.4F, var13, l, o);
      box(m, vc, -2.0F, 12.2F, -2.0F, 4.0F, 0.35F, 1.4F, var13, l, o);
      m.method_22909();
   }

   private void renderSonicSneakers(class_4587 m, class_4588 vc, float swing, float amp, int legCol, int shoeCol, int sockCol, int buckleCol, int l, int o) {
      int var11 = -1249295;
      int var12 = -10395295;
      int var13 = -11913984;
      short var14 = -3722;
      m.method_22903();
      m.method_46416(-0.11875F, 0.75F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -1.3F, 0.0F, -1.3F, 2.6F, 7.2F, 2.6F, legCol, l, o);
      box(m, vc, -1.9F, 6.8F, -1.9F, 3.8F, 2.2F, 3.8F, sockCol, l, o);
      box(m, vc, -2.0F, 7.4F, -2.0F, 4.0F, 0.5F, 4.0F, var11, l, o);
      box(m, vc, -2.3F, 8.8F, -3.6F, 4.6F, 2.8F, 6.4F, shoeCol, l, o);
      box(m, vc, -2.1F, 9.4F, -4.7F, 4.2F, 2.0F, 1.3F, shoeCol, l, o);
      box(m, vc, -1.8F, 10.2F, -5.0F, 3.6F, 1.0F, 0.5F, sockCol, l, o);
      box(m, vc, -2.35F, 9.0F, -1.8F, 4.7F, 1.4F, 2.0F, sockCol, l, o);
      box(m, vc, -2.55F, 8.8F, -1.6F, 0.4F, 1.8F, 1.6F, buckleCol, l, o);
      box(m, vc, -2.6F, 9.1F, -1.3F, 0.45F, 1.2F, 1.0F, var13, l, o);
      box(m, vc, -2.62F, 9.5F, -0.9F, 0.48F, 0.4F, 0.35F, var14, l, o);
      box(m, vc, -2.4F, 11.4F, -4.9F, 4.8F, 0.7F, 7.9F, sockCol, l, o);
      box(m, vc, -2.0F, 11.9F, -4.3F, 4.0F, 0.3F, 6.8F, var12, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.75F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -1.3F, 0.0F, -1.3F, 2.6F, 7.2F, 2.6F, legCol, l, o);
      box(m, vc, -1.9F, 6.8F, -1.9F, 3.8F, 2.2F, 3.8F, sockCol, l, o);
      box(m, vc, -2.0F, 7.4F, -2.0F, 4.0F, 0.5F, 4.0F, var11, l, o);
      box(m, vc, -2.3F, 8.8F, -3.6F, 4.6F, 2.8F, 6.4F, shoeCol, l, o);
      box(m, vc, -2.1F, 9.4F, -4.7F, 4.2F, 2.0F, 1.3F, shoeCol, l, o);
      box(m, vc, -1.8F, 10.2F, -5.0F, 3.6F, 1.0F, 0.5F, sockCol, l, o);
      box(m, vc, -2.35F, 9.0F, -1.8F, 4.7F, 1.4F, 2.0F, sockCol, l, o);
      box(m, vc, 2.15F, 8.8F, -1.6F, 0.4F, 1.8F, 1.6F, buckleCol, l, o);
      box(m, vc, 2.15F, 9.1F, -1.3F, 0.45F, 1.2F, 1.0F, var13, l, o);
      box(m, vc, 2.15F, 9.5F, -0.9F, 0.48F, 0.4F, 0.35F, var14, l, o);
      box(m, vc, -2.4F, 11.4F, -4.9F, 4.8F, 0.7F, 7.9F, sockCol, l, o);
      box(m, vc, -2.0F, 11.9F, -4.3F, 4.0F, 0.3F, 6.8F, var12, l, o);
      m.method_22909();
   }

   private void renderNarutoLegs(class_4587 m, class_4588 vc, float swing, float amp, int pantsCol, int bandageCol, int sandalCol, int skinCol, int l, int o) {
      int var11 = -12703965;
      int var12 = -3155748;
      int var13 = -15461356;
      int var14 = -2643838;
      m.method_22903();
      m.method_46416(-0.11875F, 0.75F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 7.2F, 4.0F, pantsCol, l, o);
      box(m, vc, -2.2F, 6.5F, -2.2F, 4.4F, 1.0F, 4.4F, pantsCol, l, o);
      box(m, vc, -2.15F, 2.8F, -2.15F, 4.3F, 3.4F, 4.3F, bandageCol, l, o);
      box(m, vc, -2.2F, 3.6F, -2.2F, 4.4F, 0.5F, 4.4F, -1249295, l, o);
      box(m, vc, -2.65F, 3.2F, -1.0F, 0.6F, 2.6F, 2.0F, var11, l, o);
      box(m, vc, -2.7F, 3.2F, -1.05F, 0.65F, 1.0F, 2.1F, -11652050, l, o);
      box(m, vc, -2.75F, 4.0F, -0.2F, 0.15F, 0.45F, 0.45F, var12, l, o);
      box(m, vc, -1.9F, 7.2F, -1.9F, 3.8F, 2.4F, 3.8F, bandageCol, l, o);
      box(m, vc, -1.95F, 8.2F, -1.95F, 3.9F, 0.45F, 3.9F, -1249295, l, o);
      box(m, vc, -2.05F, 9.6F, -1.8F, 4.1F, 1.8F, 3.8F, sandalCol, l, o);
      box(m, vc, -1.9F, 10.1F, -2.8F, 3.8F, 1.1F, 1.2F, skinCol, l, o);
      box(m, vc, -0.6F, 10.1F, -2.85F, 0.3F, 1.1F, 0.15F, var14, l, o);
      box(m, vc, 0.4F, 10.1F, -2.85F, 0.3F, 1.1F, 0.15F, var14, l, o);
      box(m, vc, -2.15F, 11.2F, -3.1F, 4.3F, 0.8F, 5.5F, var13, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.75F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 7.2F, 4.0F, pantsCol, l, o);
      box(m, vc, -2.2F, 6.5F, -2.2F, 4.4F, 1.0F, 4.4F, pantsCol, l, o);
      box(m, vc, -1.9F, 7.2F, -1.9F, 3.8F, 2.4F, 3.8F, bandageCol, l, o);
      box(m, vc, -1.95F, 8.2F, -1.95F, 3.9F, 0.45F, 3.9F, -1249295, l, o);
      box(m, vc, -2.05F, 9.6F, -1.8F, 4.1F, 1.8F, 3.8F, sandalCol, l, o);
      box(m, vc, -1.9F, 10.1F, -2.8F, 3.8F, 1.1F, 1.2F, skinCol, l, o);
      box(m, vc, -0.6F, 10.1F, -2.85F, 0.3F, 1.1F, 0.15F, var14, l, o);
      box(m, vc, 0.4F, 10.1F, -2.85F, 0.3F, 1.1F, 0.15F, var14, l, o);
      box(m, vc, -2.15F, 11.2F, -3.1F, 4.3F, 0.8F, 5.5F, var13, l, o);
      m.method_22909();
   }

   private void renderLegs(class_4587 m, class_4588 vc, float swing, float amp, int pantsCol, int shoesCol, int l, int o) {
      m.method_22903();
      m.method_46416(-0.11875F, 0.75F, 0.0F);
      this.applyRightLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, pantsCol, l, o);
      box(m, vc, -2.1F, 9.0F, -2.6F, 4.2F, 3.0F, 5.0F, shoesCol, l, o);
      m.method_22909();
      m.method_22903();
      m.method_46416(0.11875F, 0.75F, 0.0F);
      this.applyLeftLeg(m, swing, amp);
      box(m, vc, -2.0F, 0.0F, -2.0F, 4.0F, 9.0F, 4.0F, pantsCol, l, o);
      box(m, vc, -2.1F, 9.0F, -2.6F, 4.2F, 3.0F, 5.0F, shoesCol, l, o);
      m.method_22909();
   }

   private void renderSkibidiToilet(class_4587 m, class_4588 vc, float hYaw, float hPitch, float swing, float amp, float age, int l, int o) {
      int var10 = -657414;
      int var11 = -3155748;
      int var12 = -7297874;
      int var13 = -2039584;
      int var14 = -9079435;
      short var15 = -19712;
      int var16 = -15906911;
      int var17 = -14043402;
      byte var18 = -1;
      int var19 = -14606047;
      int var20 = -668761;
      int var21 = -2775417;
      int var22 = -14609136;
      int var23 = -11927552;
      int var24 = -44462;
      byte var25 = -1;
      int var26 = -14776091;
      int var27 = -15658735;
      float var28 = (float)Math.abs(Math.sin(swing * 0.6662)) * 1.5F * amp;
      float var29 = (float)Math.sin(swing * 0.6662) * 6.5F * amp;
      float var30 = (float)Math.cos(swing * 0.6662) * 4.0F * amp;
      m.method_22903();
      m.method_46416(0.0F, -var28 * 0.0625F, 0.0F);
      m.method_22907(class_7833.field_40718.rotationDegrees(var29));
      m.method_22907(class_7833.field_40714.rotationDegrees(var30));
      box(m, vc, -4.5F, 22.5F, -3.5F, 9.0F, 1.5F, 8.5F, var10, l, o);
      box(m, vc, -4.8F, 23.2F, -3.8F, 9.6F, 0.8F, 9.1F, var12, l, o);
      box(m, vc, -4.2F, 21.8F, 0.5F, 0.9F, 0.9F, 0.9F, var13, l, o);
      box(m, vc, 3.3F, 21.8F, 0.5F, 0.9F, 0.9F, 0.9F, var13, l, o);
      box(m, vc, -3.2F, 17.5F, -2.5F, 6.4F, 5.0F, 6.5F, var10, l, o);
      box(m, vc, -3.6F, 14.0F, -3.0F, 7.2F, 3.5F, 7.5F, var10, l, o);
      box(m, vc, -3.3F, 15.0F, -2.2F, 0.25F, 6.5F, 5.8F, var11, l, o);
      box(m, vc, 3.05F, 15.0F, -2.2F, 0.25F, 6.5F, 5.8F, var11, l, o);
      box(m, vc, -1.8F, 16.0F, 3.8F, 3.6F, 6.0F, 2.6F, var10, l, o);
      box(m, vc, -1.5F, 14.5F, 4.4F, 3.0F, 2.5F, 2.2F, var11, l, o);
      box(m, vc, -4.2F, 13.0F, 4.0F, 0.6F, 9.5F, 0.6F, var14, l, o);
      box(m, vc, -4.6F, 21.0F, 3.7F, 1.4F, 1.4F, 1.2F, var15, l, o);
      box(m, vc, -5.0F, 21.3F, 3.9F, 0.4F, 0.8F, 0.8F, -2937041, l, o);
      box(m, vc, -5.0F, 10.0F, -5.0F, 10.0F, 4.0F, 10.5F, var10, l, o);
      box(m, vc, -5.8F, 6.5F, -6.2F, 11.6F, 3.5F, 12.0F, var10, l, o);
      box(m, vc, -6.2F, 5.5F, -6.6F, 12.4F, 1.2F, 12.8F, var10, l, o);
      box(m, vc, -4.8F, 5.8F, -5.2F, 9.6F, 1.5F, 9.6F, var12, l, o);
      box(m, vc, -4.2F, 7.3F, -4.6F, 8.4F, 1.5F, 8.4F, var11, l, o);
      float var31 = (float)Math.sin(age * 0.16 + swing * 0.5) * 0.15F;
      box(m, vc, -4.0F, 8.5F + var31, -4.4F, 8.0F, 1.2F, 8.0F, var16, l, o);
      box(m, vc, -2.5F, 8.45F + var31, -3.2F, 3.0F, 0.2F, 2.5F, var17, l, o);
      box(m, vc, 0.8F, 8.45F + var31, -1.0F, 2.0F, 0.2F, 2.0F, var17, l, o);
      box(m, vc, -6.4F, 4.5F, -6.8F, 1.8F, 1.0F, 12.8F, var18, l, o);
      box(m, vc, 4.6F, 4.5F, -6.8F, 1.8F, 1.0F, 12.8F, var18, l, o);
      box(m, vc, -4.6F, 4.5F, -6.8F, 9.2F, 1.0F, 2.0F, var18, l, o);
      box(m, vc, -4.6F, 4.5F, 4.2F, 9.2F, 1.0F, 2.0F, var18, l, o);
      box(m, vc, -3.4F, 4.0F, 5.0F, 1.4F, 1.6F, 1.6F, var13, l, o);
      box(m, vc, 2.0F, 4.0F, 5.0F, 1.4F, 1.6F, 1.6F, var13, l, o);
      m.method_22903();
      m.method_46416(0.0F, 0.28125F, 0.34375F);
      m.method_22907(class_7833.field_40714.rotationDegrees(-12.0F));
      box(m, vc, -5.6F, -14.0F, -0.6F, 11.2F, 14.0F, 1.2F, var18, l, o);
      box(m, vc, -5.0F, -15.2F, -0.6F, 10.0F, 1.4F, 1.2F, var18, l, o);
      box(m, vc, -4.6F, -12.5F, -0.95F, 1.0F, 1.0F, 0.4F, var19, l, o);
      box(m, vc, 3.6F, -12.5F, -0.95F, 1.0F, 1.0F, 0.4F, var19, l, o);
      m.method_22909();
      box(m, vc, -6.8F, -4.0F, 2.6F, 13.6F, 13.5F, 5.4F, var10, l, o);
      box(m, vc, -7.2F, -5.5F, 2.2F, 14.4F, 1.6F, 6.2F, var10, l, o);
      box(m, vc, -6.8F, -5.8F, 2.6F, 13.6F, 0.4F, 5.4F, var12, l, o);
      float var32 = (float)Math.sin(age * 0.28) * 0.1F;
      m.method_22903();
      m.method_46416(-0.425F, -0.15625F, 0.2625F);
      m.method_22907(class_7833.field_40718.rotation(var32));
      box(m, vc, -0.8F, -0.6F, -0.6F, 0.8F, 1.2F, 1.2F, var13, l, o);
      box(m, vc, -3.2F, 0.0F, -0.4F, 2.6F, 0.6F, 0.8F, var13, l, o);
      box(m, vc, -3.6F, -0.2F, -0.5F, 0.6F, 1.0F, 1.0F, var14, l, o);
      m.method_22909();
      box(m, vc, -2.2F, 1.5F, -2.0F, 4.4F, 7.5F, 4.4F, var20, l, o);
      box(m, vc, -1.8F, 2.0F, -2.4F, 1.2F, 6.0F, 0.6F, var21, l, o);
      box(m, vc, 0.6F, 2.0F, -2.4F, 1.2F, 6.0F, 0.6F, var21, l, o);
      box(m, vc, -0.5F, 3.2F, -2.55F, 1.0F, 1.2F, 0.4F, var20, l, o);
      float var33 = (float)Math.sin(age * 0.55) * 16.0F;
      float var34 = (float)Math.sin(age * 0.32) * 22.0F;
      float var35 = (float)Math.cos(age * 0.32) * 8.0F;
      m.method_22903();
      m.method_46416(0.0F, 0.09375F, 0.0F);
      m.method_22907(class_7833.field_40716.rotationDegrees(hYaw * 0.5F + var34));
      m.method_22907(class_7833.field_40714.rotationDegrees(hPitch * 0.4F + var33));
      m.method_22907(class_7833.field_40718.rotationDegrees(var35));
      box(m, vc, -3.6F, -8.5F, -3.6F, 7.2F, 8.5F, 7.2F, var20, l, o);
      box(m, vc, -3.0F, -1.2F, -4.2F, 6.0F, 2.4F, 4.0F, var20, l, o);
      box(m, vc, -4.2F, -5.5F, -0.8F, 0.7F, 2.8F, 1.8F, var20, l, o);
      box(m, vc, -4.3F, -5.0F, -0.4F, 0.2F, 1.8F, 1.0F, var21, l, o);
      box(m, vc, 3.5F, -5.5F, -0.8F, 0.7F, 2.8F, 1.8F, var20, l, o);
      box(m, vc, 4.1F, -5.0F, -0.4F, 0.2F, 1.8F, 1.0F, var21, l, o);
      box(m, vc, -3.8F, -10.4F, -4.0F, 7.6F, 2.4F, 8.2F, var22, l, o);
      box(m, vc, -3.5F, -11.4F, -4.4F, 7.0F, 1.4F, 5.0F, var22, l, o);
      box(m, vc, -3.9F, -8.8F, 1.0F, 7.8F, 6.0F, 3.0F, var22, l, o);
      box(m, vc, -3.9F, -7.5F, -1.8F, 0.4F, 3.2F, 1.6F, var22, l, o);
      box(m, vc, 3.5F, -7.5F, -1.8F, 0.4F, 3.2F, 1.6F, var22, l, o);
      box(m, vc, -3.2F, -6.6F, -4.1F, 2.6F, 0.7F, 0.6F, var22, l, o);
      box(m, vc, 0.6F, -6.6F, -4.1F, 2.6F, 0.7F, 0.6F, var22, l, o);
      box(m, vc, -3.0F, -5.8F, -4.1F, 2.4F, 2.2F, 0.6F, var25, l, o);
      box(m, vc, -2.4F, -5.4F, -4.2F, 1.3F, 1.4F, 0.2F, var26, l, o);
      box(m, vc, -2.1F, -5.1F, -4.25F, 0.7F, 0.8F, 0.15F, var27, l, o);
      box(m, vc, -1.9F, -5.3F, -4.3F, 0.3F, 0.3F, 0.1F, var25, l, o);
      box(m, vc, 0.6F, -5.8F, -4.1F, 2.4F, 2.2F, 0.6F, var25, l, o);
      box(m, vc, 1.1F, -5.4F, -4.2F, 1.3F, 1.4F, 0.2F, var26, l, o);
      box(m, vc, 1.4F, -5.1F, -4.25F, 0.7F, 0.8F, 0.15F, var27, l, o);
      box(m, vc, 1.6F, -5.3F, -4.3F, 0.3F, 0.3F, 0.1F, var25, l, o);
      box(m, vc, -0.6F, -5.2F, -4.8F, 1.2F, 2.4F, 1.2F, var20, l, o);
      box(m, vc, -0.8F, -3.4F, -5.2F, 1.6F, 1.0F, 1.6F, var20, l, o);
      box(m, vc, -0.6F, -2.6F, -4.9F, 0.4F, 0.3F, 0.6F, var21, l, o);
      box(m, vc, 0.2F, -2.6F, -4.9F, 0.4F, 0.3F, 0.6F, var21, l, o);
      box(m, vc, -2.8F, -2.4F, -4.4F, 5.6F, 2.8F, 1.2F, var23, l, o);
      box(m, vc, -2.4F, -2.35F, -4.5F, 4.8F, 0.7F, 0.4F, var25, l, o);
      box(m, vc, -2.2F, -0.45F, -4.5F, 4.4F, 0.6F, 0.4F, var25, l, o);
      float var36 = (float)Math.sin(age * 0.75) * 0.25F;
      box(m, vc, -1.2F, -1.2F + var36, -4.55F, 2.4F, 0.9F, 0.6F, var24, l, o);
      m.method_22909();
      m.method_22909();
   }

   private static void box(class_4587 matrices, class_4588 vc, float x, float y, float z, float w, float h, float d, int argb, int light, int overlay) {
      Matrix4f var11 = matrices.method_23760().method_23761();
      float var12 = x * 0.0625F;
      float var13 = y * 0.0625F;
      float var14 = z * 0.0625F;
      float var15 = (x + w) * 0.0625F;
      float var16 = (y + h) * 0.0625F;
      float var17 = (z + d) * 0.0625F;
      int var18 = argb >> 24 & 0xFF;
      int var19 = argb >> 16 & 0xFF;
      int var20 = argb >> 8 & 0xFF;
      int var21 = argb & 0xFF;
      vertex(vc, var11, var12, var16, var17, var19, var20, var21, var18, 0.0F, 1.0F, 0.0F, light, overlay);
      vertex(vc, var11, var15, var16, var17, var19, var20, var21, var18, 0.0F, 1.0F, 0.0F, light, overlay);
      vertex(vc, var11, var15, var16, var14, var19, var20, var21, var18, 0.0F, 1.0F, 0.0F, light, overlay);
      vertex(vc, var11, var12, var16, var14, var19, var20, var21, var18, 0.0F, 1.0F, 0.0F, light, overlay);
      vertex(vc, var11, var12, var13, var14, var19, var20, var21, var18, 0.0F, -1.0F, 0.0F, light, overlay);
      vertex(vc, var11, var15, var13, var14, var19, var20, var21, var18, 0.0F, -1.0F, 0.0F, light, overlay);
      vertex(vc, var11, var15, var13, var17, var19, var20, var21, var18, 0.0F, -1.0F, 0.0F, light, overlay);
      vertex(vc, var11, var12, var13, var17, var19, var20, var21, var18, 0.0F, -1.0F, 0.0F, light, overlay);
      vertex(vc, var11, var15, var16, var14, var19, var20, var21, var18, 0.0F, 0.0F, -1.0F, light, overlay);
      vertex(vc, var11, var15, var13, var14, var19, var20, var21, var18, 0.0F, 0.0F, -1.0F, light, overlay);
      vertex(vc, var11, var12, var13, var14, var19, var20, var21, var18, 0.0F, 0.0F, -1.0F, light, overlay);
      vertex(vc, var11, var12, var16, var14, var19, var20, var21, var18, 0.0F, 0.0F, -1.0F, light, overlay);
      vertex(vc, var11, var12, var16, var17, var19, var20, var21, var18, 0.0F, 0.0F, 1.0F, light, overlay);
      vertex(vc, var11, var12, var13, var17, var19, var20, var21, var18, 0.0F, 0.0F, 1.0F, light, overlay);
      vertex(vc, var11, var15, var13, var17, var19, var20, var21, var18, 0.0F, 0.0F, 1.0F, light, overlay);
      vertex(vc, var11, var15, var16, var17, var19, var20, var21, var18, 0.0F, 0.0F, 1.0F, light, overlay);
      vertex(vc, var11, var12, var16, var14, var19, var20, var21, var18, -1.0F, 0.0F, 0.0F, light, overlay);
      vertex(vc, var11, var12, var13, var14, var19, var20, var21, var18, -1.0F, 0.0F, 0.0F, light, overlay);
      vertex(vc, var11, var12, var13, var17, var19, var20, var21, var18, -1.0F, 0.0F, 0.0F, light, overlay);
      vertex(vc, var11, var12, var16, var17, var19, var20, var21, var18, -1.0F, 0.0F, 0.0F, light, overlay);
      vertex(vc, var11, var15, var16, var17, var19, var20, var21, var18, 1.0F, 0.0F, 0.0F, light, overlay);
      vertex(vc, var11, var15, var13, var17, var19, var20, var21, var18, 1.0F, 0.0F, 0.0F, light, overlay);
      vertex(vc, var11, var15, var13, var14, var19, var20, var21, var18, 1.0F, 0.0F, 0.0F, light, overlay);
      vertex(vc, var11, var15, var16, var14, var19, var20, var21, var18, 1.0F, 0.0F, 0.0F, light, overlay);
   }

   private static void vertex(
      class_4588 vc, Matrix4f pos, float x, float y, float z, int r, int g, int b, int a, float nx, float ny, float nz, int light, int overlay
   ) {
      vc.method_22918(pos, x, y, z).method_1336(r, g, b, a).method_22913(0.0F, 0.0F).method_22922(overlay).method_60803(light).method_22914(nx, ny, nz);
   }

   static {
      byte[] var10000 = new byte[9];
      var10000[0] = (byte)(94);
      var10000[1] = (byte)(-13);
      var10000[2] = (byte)(114);
      var10000[3] = (byte)(-69);
      var10000[4] = (byte)(-41);
      var10000[5] = (byte)(-23);
      var10000[6] = (byte)(64);
      var10000[7] = (byte)(13);
      var10000[8] = (byte)(47);
      String var0 = $$v_s(var10000, 4837504873135329302L);
      byte[] var10001 = new byte[33];
      var10001[0] = (byte)(71);
      var10001[1] = (byte)(-1);
      var10001[2] = (byte)(100);
      var10001[3] = (byte)(-86);
      var10001[4] = (byte)(-63);
      var10001[5] = (byte)(-23);
      var10001[6] = (byte)(68);
      var10001[7] = (byte)(24);
      var10001[8] = (byte)(116);
      var10001[9] = (byte)(-64);
      var10001[10] = (byte)(-120);
      var10001[11] = (byte)(-103);
      var10001[12] = (byte)(-113);
      var10001[13] = (byte)(40);
      var10001[14] = (byte)(38);
      var10001[15] = (byte)(100);
      var10001[16] = (byte)(11);
      var10001[17] = (byte)(35);
      var10001[18] = (byte)(-72);
      var10001[19] = (byte)(-53);
      var10001[20] = (byte)(91);
      var10001[21] = (byte)(8);
      var10001[22] = (byte)(30);
      var10001[23] = (byte)(85);
      var10001[24] = (byte)(-24);
      var10001[25] = (byte)(96);
      var10001[26] = (byte)(-15);
      var10001[27] = (byte)(50);
      var10001[28] = (byte)(89);
      var10001[29] = (byte)(61);
      var10001[30] = (byte)(41);
      var10001[31] = (byte)(-115);
      var10001[32] = (byte)(-76);
      WHITE_TEX = class_2960.method_60655(var0, $$v_s(var10001, 4837504873135329302L));
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

   public enum CharacterModel {
      TUNG_TUNG_TUNG_SAHUR(
         "Tung Tung Tung Sahur"
      ),
      SANTA_CLAUS("Santa Clause"),
      DORAEMON("Doraemon"),
      MICKEY_MOUSE("Mickey Mouse"),
      PIKACHU("Pikachu"),
      MINION("Minion"),
      SIMPSON("Simpson"),
      SPONGEBOB("SpongeBob"),
      PATRICK_STAR("Patrick Star"),
      MARIO("Super Mario"),
      SONIC("Sonic"),
      SHREK("Shrek"),
      NARUTO("Naruto"),
      SKIBIDI_TOILET("Skibidi Toilet");

      private final String displayName;
      // $VF: synthetic field
      private static final boolean $$vie_p = true;

      CharacterModel(String displayName) {
         this.displayName = displayNamex;
      }

      @Override
      public String toString() {
         return this.displayName;
      }

      // $VF: synthetic method
      private static PlayerModels.CharacterModel[] $values() {
         PlayerModels.CharacterModel[] var10000 = new PlayerModels.CharacterModel[14];
         var10000[0] = TUNG_TUNG_TUNG_SAHUR;
         var10000[1] = SANTA_CLAUS;
         var10000[2] = DORAEMON;
         var10000[3] = MICKEY_MOUSE;
         var10000[4] = PIKACHU;
         var10000[5] = MINION;
         var10000[6] = SIMPSON;
         var10000[7] = SPONGEBOB;
         var10000[8] = PATRICK_STAR;
         var10000[9] = MARIO;
         var10000[10] = SONIC;
         var10000[11] = SHREK;
         var10000[12] = NARUTO;
         var10000[13] = SKIBIDI_TOILET;
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
