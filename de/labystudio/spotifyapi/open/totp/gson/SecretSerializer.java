package de.labystudio.spotifyapi.open.totp.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import de.labystudio.spotifyapi.open.totp.model.Secret;
import java.lang.reflect.Type;

public class SecretSerializer implements JsonSerializer<Secret> {
   public JsonElement serialize(Secret secret, Type type, JsonSerializationContext context) {
      JsonObject obj = new JsonObject();
      obj.addProperty("version", secret.getVersion());
      obj.addProperty("secret", secret.getSecretAsString());
      return obj;
   }
}
