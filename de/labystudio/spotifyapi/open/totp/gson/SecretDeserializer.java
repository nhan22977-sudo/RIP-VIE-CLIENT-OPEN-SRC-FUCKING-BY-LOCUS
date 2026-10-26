package de.labystudio.spotifyapi.open.totp.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.labystudio.spotifyapi.open.totp.model.Secret;
import java.lang.reflect.Type;

public class SecretDeserializer implements JsonDeserializer<Secret> {
   public Secret deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      int version = obj.get("version").getAsInt();
      JsonElement secret = obj.get("secret");
      if (!secret.isJsonArray()) {
         if (secret.isJsonPrimitive() && secret.getAsJsonPrimitive().isString()) {
            String secretString = secret.getAsString();
            return Secret.fromString(secretString, version);
         } else {
            throw new JsonParseException("Invalid secret format: " + secret);
         }
      } else {
         JsonArray array = secret.getAsJsonArray();
         int[] numbers = new int[array.size()];

         for (int i = 0; i < array.size(); i++) {
            numbers[i] = array.get(i).getAsInt();
         }

         return Secret.fromNumbers(numbers, version);
      }
   }
}
