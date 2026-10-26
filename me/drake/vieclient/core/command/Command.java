package me.drake.vieclient.core.command;

import net.minecraft.class_2561;
import net.minecraft.class_310;

public abstract class Command {
   private final String name;
   private final String description;
   private final String syntax;
   private final String[] aliases;
   protected static final class_310 mc = class_310.method_1551();
   // $VF: synthetic field
   private static final boolean $$vie_p = true;

   protected Command(String name, String description, String syntax, String... aliases) {
      this.name = namex;
      this.description = descriptionx;
      this.syntax = syntaxx;
      this.aliases = aliasesx;
   }

   public abstract void execute(String[] var1);

   protected void info(String message) {
      if (mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470("§d[VC]§r " + message), false);
      }
   }

   protected void error(String message) {
      if (mc.field_1724 != null) {
         mc.field_1724.method_7353(class_2561.method_43470("§d[VC]§c " + message), false);
      }
   }

   public String getName() {
      return this.name;
   }

   public String getDescription() {
      return this.description;
   }

   public String getSyntax() {
      return this.syntax;
   }

   public String[] getAliases() {
      return this.aliases;
   }

   public boolean matches(String input) {
      if (this.name.equalsIgnoreCase(input)) {
         return true;
      }

      for (String var5 : this.aliases) {
         if (var5.equalsIgnoreCase(input)) {
            return true;
         }
      }

      return false;
   }
}
