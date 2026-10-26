package me.drake.vieclient.gui.nvg.adapter.setting;

import java.util.ArrayList;
import java.util.List;

public class MacroSetting extends Setting {
   private final List commands = new ArrayList();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   public MacroSetting(CharSequence name, List defaultCommands) {
      super(name);
      if (defaultCommands != null) {
         this.commands.addAll(defaultCommands);
      }
   }

   public MacroSetting(CharSequence name) {
      this(name, null);
   }

   public List getCommands() {
      return this.commands;
   }

   public void setCommands(List a) {
      this.commands.clear();
      this.commands.addAll(a);
   }

   public void addCommand(String c) {
      this.commands.add(c);
   }

   public void removeCommand(int index) {
      if (index >= 0 && index < this.commands.size()) {
         this.commands.remove(index);
      }
   }

   public void clearCommands() {
      this.commands.clear();
   }

   public List getDefaultCommands() {
      return new ArrayList();
   }

   @Override
   public void resetValue() {
      this.commands.clear();
   }

   public MacroSetting setDescription(CharSequence s) {
      super.setDescription(s);
      return this;
   }
}
