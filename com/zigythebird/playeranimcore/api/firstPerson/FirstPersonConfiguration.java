package com.zigythebird.playeranimcore.api.firstPerson;

public class FirstPersonConfiguration {
   boolean showRightArm = false;
   boolean showLeftArm = false;
   boolean showRightItem = true;
   boolean showLeftItem = true;
   boolean showArmor = false;

   public FirstPersonConfiguration() {
   }

   public FirstPersonConfiguration(boolean showRightArm, boolean showLeftArm, boolean showRightItem, boolean showLeftItem) {
      this(showRightArm, showLeftArm, showRightItem, showLeftItem, false);
   }

   public FirstPersonConfiguration(boolean showRightArm, boolean showLeftArm, boolean showRightItem, boolean showLeftItem, boolean showArmor) {
      this.showRightArm = showRightArm;
      this.showLeftArm = showLeftArm;
      this.showRightItem = showRightItem;
      this.showLeftItem = showLeftItem;
      this.showArmor = showArmor;
   }

   public boolean isShowArmor() {
      return this.showArmor;
   }

   public boolean isShowLeftItem() {
      return this.showLeftItem;
   }

   public boolean isShowRightItem() {
      return this.showRightItem;
   }

   public boolean isShowLeftArm() {
      return this.showLeftArm;
   }

   public boolean isShowRightArm() {
      return this.showRightArm;
   }

   public FirstPersonConfiguration setShowRightItem(boolean showRightItem) {
      this.showRightItem = showRightItem;
      return this;
   }

   public FirstPersonConfiguration setShowArmor(boolean showArmor) {
      this.showArmor = showArmor;
      return this;
   }

   public FirstPersonConfiguration setShowLeftItem(boolean showLeftItem) {
      this.showLeftItem = showLeftItem;
      return this;
   }

   public FirstPersonConfiguration setShowLeftArm(boolean showLeftArm) {
      this.showLeftArm = showLeftArm;
      return this;
   }

   public FirstPersonConfiguration setShowRightArm(boolean showRightArm) {
      this.showRightArm = showRightArm;
      return this;
   }
}
