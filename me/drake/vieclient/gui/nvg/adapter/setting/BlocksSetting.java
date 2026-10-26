package me.drake.vieclient.gui.nvg.adapter.setting;

import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.class_2246;
import net.minecraft.class_2248;

public class BlocksSetting extends Setting {
   private final Set blocks = new LinkedHashSet();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public BlocksSetting(CharSequence name, Set value) {
      super(name);
      if (value != null) {
         this.blocks.addAll(value);
      }
   }

   public BlocksSetting(CharSequence name, class_2248... value) {
      super(name);
      if (value != null) {
         for (class_2248 var6 : value) {
            this.blocks.add(var6);
         }
      }
   }

   public Set getBlocks() {
      return new LinkedHashSet(this.blocks);
   }

   public void setBlocks(Set s) {
      this.blocks.clear();
      if (s != null) {
         this.blocks.addAll(s);
      }
   }

   public void addBlock(class_2248 b) {
      if (b != null && b != class_2246.field_10124) {
         this.blocks.add(b);
      }
   }

   public void removeBlock(class_2248 b) {
      this.blocks.remove(b);
   }

   public boolean contains(class_2248 b) {
      return this.blocks.contains(b);
   }

   public void toggleBlock(class_2248 b) {
      if (this.contains(b)) {
         this.removeBlock(b);
      } else {
         this.addBlock(b);
      }
   }

   public void clearBlocks() {
      this.blocks.clear();
   }

   public int size() {
      return this.blocks.size();
   }

   public Set getDefaultValue() {
      return new LinkedHashSet();
   }

   @Override
   public void resetValue() {
      this.blocks.clear();
   }
}
