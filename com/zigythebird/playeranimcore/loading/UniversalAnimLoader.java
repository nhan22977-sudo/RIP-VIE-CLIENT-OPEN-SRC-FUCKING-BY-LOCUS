package com.zigythebird.playeranimcore.loading;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.zigythebird.playeranimcore.PlayerAnimLib;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.CustomInstructionKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.SoundKeyframeData;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.util.JsonUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;

public class UniversalAnimLoader implements JsonDeserializer<Map<String, Animation>> {
   public static final Animation.Keyframes NO_KEYFRAMES = new Animation.Keyframes(
      new SoundKeyframeData[0], new ParticleKeyframeData[0], new CustomInstructionKeyframeData[0]
   );
   private static final Pattern UPPERCASE_PATTERN = Pattern.compile("([A-Z])");
   private static final Pattern UNDERSCORE_PATTERN = Pattern.compile("_(.)");

   public static Map<String, Animation> loadAnimations(InputStream resource) throws IOException {
      try (Reader reader = new InputStreamReader(resource)) {
         return loadAnimations((JsonObject)PlayerAnimLib.GSON.fromJson(reader, JsonObject.class));
      }
   }

   public static Map<@NotNull String, Animation> loadAnimations(JsonObject json) {
      if (!json.has("animations")) {
         Animation animation = (Animation)PlayerAnimatorLoader.GSON.fromJson(json, Animation.class);
         return Collections.singletonMap(animation.getNameOrId(), animation);
      }

      Map<String, Animation> animationMap = (Map<String, Animation>)PlayerAnimLib.GSON.fromJson(json.get("animations"), PlayerAnimLib.ANIMATIONS_MAP_TYPE);
      if (json.has("parents") && json.has("model")) {
         Map<String, String> parents = getParents(JsonUtil.getAsJsonObject(json, "parents", new JsonObject()));
         Map<String, Vec3f> bones = getModel(JsonUtil.getAsJsonObject(json, "model", new JsonObject()));

         for (Animation animation : animationMap.values()) {
            if (animation.bones().isEmpty()) {
               animation.bones().putAll(bones);
            }

            if (animation.parents().isEmpty()) {
               animation.parents().putAll(parents);
            }
         }
      }

      return animationMap;
   }

   public Map<String, Animation> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject obj = json.getAsJsonObject();
      Map<String, Animation> animations = new Object2ObjectOpenHashMap(obj.size());

      for (Entry<String, JsonElement> entry : obj.entrySet()) {
         try {
            Animation animation = (Animation)context.deserialize(entry.getValue().getAsJsonObject(), Animation.class);
            if (!animation.data().has("name")) {
               animation.data().put("name", entry.getKey());
            }

            animations.put(entry.getKey(), animation);
         } catch (Exception ex) {
            PlayerAnimLib.LOGGER.error("Unable to parse animation: {}", entry.getKey(), ex);
         }
      }

      return animations;
   }

   public static Map<String, String> getParents(JsonObject parentsObj) {
      Map<String, String> parents = new HashMap<>(parentsObj.size());

      for (Entry<String, JsonElement> entry : parentsObj.entrySet()) {
         parents.put(getCorrectPlayerBoneName(entry.getKey()), entry.getValue().getAsString());
      }

      return parents;
   }

   public static Map<String, Vec3f> getModel(JsonObject modelObj) {
      Map<String, Vec3f> bones = new HashMap<>(modelObj.size());

      for (Entry<String, JsonElement> entry : modelObj.entrySet()) {
         JsonObject object = entry.getValue().getAsJsonObject();
         JsonArray pivot = object.get("pivot").getAsJsonArray();
         Vec3f bone = new Vec3f(pivot.get(0).getAsFloat(), pivot.get(1).getAsFloat(), pivot.get(2).getAsFloat());
         bones.put(entry.getKey(), bone);
      }

      return bones;
   }

   public static String getCorrectPlayerBoneName(String name) {
      return UPPERCASE_PATTERN.matcher(name).replaceAll("_$1").toLowerCase(Locale.ROOT);
   }

   public static String restorePlayerBoneName(String name) {
      StringBuilder result = new StringBuilder();
      String lowerCase = name.toLowerCase(Locale.ROOT);
      Matcher matcher = UNDERSCORE_PATTERN.matcher(lowerCase);

      int lastEnd;
      for (lastEnd = 0; matcher.find(); lastEnd = matcher.end()) {
         result.append(lowerCase, lastEnd, matcher.start());
         result.append(Character.toUpperCase(matcher.group(1).charAt(0)));
      }

      result.append(lowerCase.substring(lastEnd));
      return result.toString();
   }
}
