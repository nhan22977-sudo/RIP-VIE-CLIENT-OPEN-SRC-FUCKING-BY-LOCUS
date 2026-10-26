package io.github.kosmx.emotes.main.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import io.github.kosmx.emotes.server.config.ConfigSerializer;
import it.unimi.dsi.fastutil.Pair;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.class_3675;
import net.minecraft.class_3675.class_306;

public class ClientConfigSerializer extends ConfigSerializer<ClientConfig> {
   public ClientConfigSerializer() {
      super(ClientConfig::new);
   }

   public ClientConfig deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      ClientConfig config = (ClientConfig)super.deserialize(json, typeOfT, context);
      this.clientDeserialize(json.getAsJsonObject(), config);
      return config;
   }

   public JsonElement serialize(ClientConfig config, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject node = super.serialize(config, typeOfSrc, context).getAsJsonObject();
      this.clientSerialize(config, node);
      return node;
   }

   private void clientDeserialize(JsonObject node, ClientConfig config) {
      if (node.has("fastmenu")) {
         this.fastMenuDeserializer(node.get("fastmenu").getAsJsonObject(), config);
      }

      if (node.has("keys")) {
         this.keyBindsDeserializer(node.get("keys"), config);
      }
   }

   private void fastMenuDeserializer(JsonObject node, ClientConfig config) {
      for (int j = 0; j < config.fastMenuEmotes.length; j++) {
         if (node.has(Integer.toString(j))) {
            JsonElement subNode = node.get(Integer.toString(j));
            if (subNode.isJsonObject()) {
               for (int i = 0; i != 8; i++) {
                  if (node.get(Integer.toString(j)).getAsJsonObject().has(Integer.toString(i))) {
                     config.fastMenuEmotes[j][i] = getEmoteID(node.get(Integer.toString(j)).getAsJsonObject().get(Integer.toString(i)));
                  }
               }
            } else {
               config.fastMenuEmotes[0][j] = getEmoteID(node.get(Integer.toString(j)));
            }
         }
      }
   }

   private void keyBindsDeserializer(JsonElement node, ClientConfig config) {
      if (config.configVersion < 4) {
         this.oldKeyBindsSerializer(node.getAsJsonArray(), config);
      } else {
         for (Entry<String, JsonElement> element : node.getAsJsonObject().entrySet()) {
            String str = element.getValue().getAsString();
            config.emoteKeyMap.put(UUID.fromString(element.getKey()), class_3675.method_15981(str));
         }
      }
   }

   private void oldKeyBindsSerializer(JsonArray node, ClientConfig config) {
      for (JsonElement jsonElement : node) {
         JsonObject n = jsonElement.getAsJsonObject();
         String str = n.get("key").getAsString();
         config.emoteKeyMap.add(Pair.of(getEmoteID(n.get("id")), class_3675.method_15981(str)));
      }
   }

   private void clientSerialize(ClientConfig config, JsonObject node) {
      node.add("fastmenu", this.fastMenuSerializer(config));
      node.add("keys", this.keyBindsSerializer(config));
   }

   private JsonObject fastMenuSerializer(ClientConfig config) {
      JsonObject node = new JsonObject();

      for (int j = 0; j < config.fastMenuEmotes.length; j++) {
         if (config.fastMenuEmotes[j] != null) {
            JsonObject subNode = new JsonObject();

            for (int i = 0; i != 8; i++) {
               if (config.fastMenuEmotes[j][i] != null) {
                  subNode.addProperty(Integer.toString(i), config.fastMenuEmotes[j][i].toString());
                  node.add(Integer.toString(j), subNode);
               }
            }
         }
      }

      return node;
   }

   private JsonObject keyBindsSerializer(ClientConfig config) {
      JsonObject array = new JsonObject();

      for (Pair<UUID, class_306> emote : config.emoteKeyMap) {
         array.addProperty(((UUID)emote.left()).toString(), ((class_306)emote.right()).method_1441());
      }

      return array;
   }

   public static UUID getEmoteID(JsonElement element) {
      try {
         return UUID.fromString(element.getAsString());
      } catch (Exception e) {
         return new UUID(0L, 0L);
      }
   }
}
