package de.labystudio.spotifyapi.platform.linux.api;

import de.labystudio.spotifyapi.platform.linux.api.model.InterfaceMember;
import de.labystudio.spotifyapi.platform.linux.api.model.Parameter;
import de.labystudio.spotifyapi.platform.linux.api.model.Variant;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DBusSend {
   private static final Parameter PARAM_PRINT_REPLY = new Parameter("print-reply");
   private static final InterfaceMember INTERFACE_GET = new InterfaceMember("org.freedesktop.DBus.Properties.Get");
   private final Parameter[] parameters;
   private final String objectPath;
   private final Runtime runtime;

   public DBusSend(Parameter[] parameters, String objectPath) {
      this.parameters = parameters;
      this.objectPath = objectPath;
      this.runtime = Runtime.getRuntime();
   }

   public Variant get(String... keys) throws Exception {
      String[] contents = new String[keys.length];

      for (int i = 0; i < keys.length; i++) {
         contents[i] = String.format("string:%s", keys[i]);
      }

      return this.send(INTERFACE_GET, contents);
   }

   public Variant send(InterfaceMember interfaceMember, String... contents) throws Exception {
      String[] arguments = new String[2 + this.parameters.length + 2 + contents.length];
      arguments[0] = "dbus-send";
      arguments[1] = PARAM_PRINT_REPLY.toString();

      for (int i = 0; i < this.parameters.length; i++) {
         arguments[2 + i] = this.parameters[i].toString();
      }

      arguments[2 + this.parameters.length] = this.objectPath;
      arguments[2 + this.parameters.length + 1] = interfaceMember.toString();

      for (int i = 0; i < contents.length; i++) {
         arguments[2 + this.parameters.length + 2 + i] = contents[i];
      }

      Process process = this.runtime.exec(arguments);
      int exitCode = process.waitFor();
      if (exitCode == 0) {
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
         StringBuilder builder = new StringBuilder();

         String response;
         while ((response = reader.readLine()) != null) {
            if (!response.startsWith("method ")) {
               builder.append(response).append("\n");
            }
         }

         return builder.toString().isEmpty() ? new Variant("success", true) : Variant.parse(builder.toString());
      } else {
         BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
         StringBuilder builder = new StringBuilder();

         String line;
         while ((line = reader.readLine()) != null) {
            builder.append(line);
         }

         throw new Exception("dbus-send execution \"" + String.join(" ", arguments) + "\" failed with exit code " + exitCode + ": " + builder);
      }
   }
}
