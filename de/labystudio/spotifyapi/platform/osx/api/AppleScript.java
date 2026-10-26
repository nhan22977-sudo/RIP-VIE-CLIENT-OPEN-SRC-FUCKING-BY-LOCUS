package de.labystudio.spotifyapi.platform.osx.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AppleScript {
   private static final String GRAMMAR_FORMAT = "tell application \"%s\" to %s";
   private final String[] runtimeParameters = new String[]{"osascript", "-e", null};
   private final String application;
   private final Runtime runtime;

   public AppleScript(String application) {
      this.application = application;
      this.runtime = Runtime.getRuntime();
   }

   public String getOf(Action request, Action of) throws Exception {
      return this.execute(Action.GET, request, Action.OF, of);
   }

   public String get(Action request) throws Exception {
      return this.execute(Action.GET, request);
   }

   public String execute(Action... actions) throws Exception {
      String action = Action.toString(actions);
      this.runtimeParameters[2] = String.format("tell application \"%s\" to %s", this.application, action);
      Process process = this.runtime.exec(this.runtimeParameters);
      int exitCode = process.waitFor();
      if (exitCode == 0) {
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         StringBuilder builder = new StringBuilder();

         String line;
         while ((line = reader.readLine()) != null) {
            builder.append(line);
         }

         return builder.toString();
      } else {
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
         StringBuilder builder = new StringBuilder();

         String line;
         while ((line = reader.readLine()) != null) {
            builder.append(line);
         }

         throw new Exception("AppleScript execution \"" + action + "\" failed with exit code " + exitCode + ": " + builder);
      }
   }
}
