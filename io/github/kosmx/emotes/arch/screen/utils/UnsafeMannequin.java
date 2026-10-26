package io.github.kosmx.emotes.arch.screen.utils;

import com.mojang.authlib.GameProfile;
import java.util.Objects;
import net.minecraft.class_11903;
import net.minecraft.class_1657;
import net.minecraft.class_1664;
import net.minecraft.class_2246;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_310;
import net.minecraft.class_3611;
import net.minecraft.class_638;
import net.minecraft.class_6862;
import net.minecraft.class_746;
import net.minecraft.class_9296;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnsafeMannequin extends class_11903 {
   public UnsafeMannequin(@Nullable class_638 clientLevel, GameProfile gameProfile) {
      super(Objects.requireNonNullElse(clientLevel, UnsafeClientLevel.INSTANCE), class_310.method_1551().method_73362());
      this.method_74098(class_9296.method_73307(gameProfile));
      this.method_74877(true);
   }

   public boolean method_5756(class_1657 player) {
      return true;
   }

   public boolean method_74091(class_1664 part) {
      return true;
   }

   public void initEmotePerspective() {
   }

   protected void method_74700() {
      super.method_74700();
      if (this.field_62582 != null) {
         this.field_62582.thenAccept(playerSkin -> playerSkin.ifPresent(this::method_74704));
      }
   }

   public boolean method_33724() {
      return true;
   }

   public boolean method_5692(class_6862<class_3611> fluidTag, double motionScale) {
      return false;
   }

   protected boolean method_5876() {
      return false;
   }

   public void method_5670() {
      this.field_6012++;
   }

   public void method_6007() {
   }

   public boolean method_5807() {
      return false;
   }

   @NotNull
   public class_2680 method_55667() {
      return class_2246.field_10243.method_9564();
   }

   @NotNull
   public class_243 method_73189() {
      class_746 localPlayer = class_310.method_1551().field_1724;
      return localPlayer == null ? super.method_73189() : localPlayer.method_73189();
   }
}
