package com.zigythebird.playeranimcore.animation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.zigythebird.playeranimcore.enums.AnimationFormat;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ExtraAnimationData(Map<String, Object> data) {
   public static final String NAME_KEY = "name";
   public static final String UUID_KEY = "uuid";
   public static final String FORMAT_KEY = "format";
   public static final String BEGIN_TICK_KEY = "beginTick";
   public static final String END_TICK_KEY = "endTick";
   public static final String EASING_BEFORE_KEY = "easeBeforeKeyframe";
   public static final String APPLY_BEND_TO_OTHER_BONES_KEY = "applyBendToOtherBones";
   public static final String PARTICLE_EFFECTS_KEY = "particleEffects";
   public static final String DISABLE_AXIS_IF_NOT_MODIFIED = "disableAxisIfNotModified";

   public ExtraAnimationData(String key, Object value) {
      this(new HashMap<>(Collections.singletonMap(key, value)));
   }

   public ExtraAnimationData() {
      this(new HashMap<>(1));
   }

   @Nullable
   public String name() {
      Object data = this.data().get("name");
      String name;
      if (data instanceof JsonObject jsonObject) {
         name = jsonObject.get("fallback").getAsString();
      } else {
         name = (String)data;
      }

      return name != null ? name.toLowerCase(Locale.ROOT).replace("\"", "").replace(" ", "_") : null;
   }

   public boolean has(String name) {
      return this.data().containsKey(name);
   }

   @Nullable
   public ByteBuffer getBinary(String name) {
      Object obj = this.getRaw(name);
      if (obj == null) {
         return null;
      }

      if (obj instanceof String str) {
         try {
            this.put(name, obj = Base64.getDecoder().decode(str));
         } catch (IllegalArgumentException e) {
            return null;
         }
      }

      if (obj instanceof byte[] bytes) {
         this.put(name, obj = ByteBuffer.wrap(bytes).asReadOnlyBuffer());
      }

      return obj instanceof ByteBuffer buffer ? buffer.asReadOnlyBuffer() : null;
   }

   public Object getRaw(String name) {
      return this.data().get(name);
   }

   public <T> Optional<T> get(String key) {
      Object obj = this.getRaw(key);
      if (obj == null) {
         return Optional.empty();
      }

      try {
         return Optional.of((T)obj);
      } catch (Throwable var4) {
         return Optional.empty();
      }
   }

   public <T> T getNullable(String key) {
      return this.<T>get(key).orElse(null);
   }

   public List<?> getList(String key) {
      Object obj = this.getRaw(key);

      return switch (obj) {
         case null -> Collections.emptyList();
         case JsonArray json -> json.asList();
         case List<?> list -> list;
         default -> throw new ClassCastException(obj.getClass().getName());
      };
   }

   public void put(String name, Object object) {
      this.data.put(name, object);
   }

   public void fromJson(JsonObject node, boolean root) {
      for (Entry<String, JsonElement> entry : node.entrySet()) {
         String key = entry.getKey();
         if (!root || !"version".equalsIgnoreCase(key) && !"emote".equalsIgnoreCase(key)) {
            this.data().put(key, this.getValue(entry.getValue()));
         }
      }
   }

   public Object getValue(JsonElement element) {
      if (element instanceof JsonPrimitive p) {
         if (p.isBoolean()) {
            return p.getAsBoolean();
         }

         if (p.isString()) {
            return p.getAsString();
         }

         if (p.isNumber()) {
            return p.getAsFloat();
         }
      }

      if (!(element instanceof JsonArray array)) {
         return element.toString();
      } else {
         List<Object> list = new ArrayList<>(array.size());

         for (JsonElement element1 : array) {
            list.add(this.getValue(element1));
         }

         return list;
      }
   }

   public ExtraAnimationData copy() {
      return new ExtraAnimationData(new HashMap<>(this.data()));
   }

   public boolean isDisableAxisIfNotModified() {
      return this.<Boolean>get("disableAxisIfNotModified").orElse(true);
   }

   public boolean isAnimationPlayerAnimatorFormat() {
      return this.get("format").orElse(null) == AnimationFormat.PLAYER_ANIMATOR;
   }

   @NotNull
   @Override
   public String toString() {
      return this.data.toString();
   }
}
