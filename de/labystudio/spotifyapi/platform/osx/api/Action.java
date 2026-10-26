package de.labystudio.spotifyapi.platform.osx.api;

public class Action {
   public static final Action GET = new Action("get", "the");
   public static final Action OF = new Action("of");
   private final String action;

   public Action(String... args) {
      this.action = String.join(" ", args);
   }

   public Action(String argument) {
      this.action = argument;
   }

   public Action(Action... actions) {
      this.action = toString(actions);
   }

   @Override
   public String toString() {
      return this.action;
   }

   public static String toString(Action... actions) {
      String[] args = new String[actions.length];

      for (int i = 0; i < actions.length; i++) {
         args[i] = actions[i].toString();
      }

      return String.join(" ", args);
   }
}
