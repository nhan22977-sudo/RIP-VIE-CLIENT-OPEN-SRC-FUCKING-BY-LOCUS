package com.zigythebird.playeranimcore.loading;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.Keyframe;
import com.zigythebird.playeranimcore.animation.keyframe.KeyframeStack;
import com.zigythebird.playeranimcore.animation.keyframe.event.data.ParticleKeyframeData;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.AnimationFormat;
import com.zigythebird.playeranimcore.enums.TransformType;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.util.ParticleEffectUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.jetbrains.annotations.Nullable;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.parser.ast.FloatExpression;
import team.unnamed.mocha.runtime.standard.MochaMath;

public class PlayerAnimatorLoader implements JsonDeserializer<Animation> {
   public static final List<Expression> ZERO = Collections.singletonList(FloatExpression.ZERO);
   public static final List<Expression> ONE = Collections.singletonList(FloatExpression.ONE);
   private static final int modVersion = 3;
   public static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(Animation.class, new PlayerAnimatorLoader()).create();
   private static final Map<String, Vec3f> DEFAULT_VALUES = Map.of(
      "right_arm",
      new Vec3f(-5.0F, 2.0F, 0.0F),
      "left_arm",
      new Vec3f(5.0F, 2.0F, 0.0F),
      "left_leg",
      new Vec3f(1.9F, 12.0F, 0.1F),
      "right_leg",
      new Vec3f(-1.9F, 12.0F, 0.1F)
   );

   protected PlayerAnimatorLoader() {
   }

   public Animation deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject node = json.getAsJsonObject();
      if (!node.has("emote")) {
         throw new JsonParseException("not an emotecraft animation");
      }

      int version = 1;
      if (node.has("version")) {
         version = node.get("version").getAsInt();
      }

      ExtraAnimationData extra = new ExtraAnimationData();
      extra.fromJson(node, true);
      extra.put("format", AnimationFormat.PLAYER_ANIMATOR);
      if (3 < version) {
         throw new JsonParseException(extra.name() + " is version " + version + ". Player Animation library can only process version 3.");
      } else {
         return this.emoteDeserializer(extra, node.getAsJsonObject("emote"), version);
      }
   }

   private Animation emoteDeserializer(ExtraAnimationData extra, JsonObject node, int version) throws JsonParseException {
      if (version < 3) {
         extra.put("applyBendToOtherBones", true);
      }

      boolean easeBeforeKeyframe = node.has("easeBeforeKeyframe") && node.get("easeBeforeKeyframe").getAsBoolean();
      extra.put("easeBeforeKeyframe", easeBeforeKeyframe);
      float beginTick = 0.0F;
      if (node.has("beginTick")) {
         beginTick = node.get("beginTick").getAsFloat();
         extra.put("beginTick", beginTick);
      }

      float endTick = beginTick + 1.0F;
      if (node.has("endTick")) {
         endTick = Math.max(node.get("endTick").getAsFloat(), endTick);
         extra.put("endTick", endTick);
      }

      if (endTick <= 0.0F) {
         throw new JsonParseException("endTick must be bigger than 0");
      }

      Animation.LoopType loopType = Animation.LoopType.PLAY_ONCE;
      if (node.has("isLoop") && node.has("returnTick")) {
         boolean isLooped = node.get("isLoop").getAsBoolean();
         int returnTick = Math.max(node.get("returnTick").getAsInt() - 1, 0);
         if (isLooped) {
            if (returnTick > endTick || returnTick < 0) {
               throw new JsonParseException("The returnTick has to be a non-negative value smaller than the endTick value");
            }

            if (returnTick == 0) {
               loopType = Animation.LoopType.LOOP;
            } else {
               loopType = Animation.LoopType.returnToTickLoop(returnTick);
            }
         }
      }

      float stopTick = node.has("stopTick") ? node.get("stopTick").getAsFloat() : 0.0F;
      if (loopType == Animation.LoopType.PLAY_ONCE) {
         endTick = stopTick <= endTick ? endTick + 3.0F : stopTick;
      }

      boolean degrees = !node.has("degrees") || node.get("degrees").getAsBoolean();
      Map<String, BoneAnimation> bones = this.moveDeserializer(node.getAsJsonArray("moves").asList(), degrees, version, endTick);

      for (Entry<String, BoneAnimation> boneAnimation : bones.entrySet()) {
         if (!easeBeforeKeyframe) {
            correctEasings(boneAnimation.getValue().positionKeyFrames());
            correctEasings(boneAnimation.getValue().rotationKeyFrames());
            correctEasings(boneAnimation.getValue().scaleKeyFrames());
            correctEasings(boneAnimation.getValue().bendKeyFrames());
         }

         if (boneAnimation.getKey().equals("right_item") || boneAnimation.getKey().equals("left_item")) {
            swapTheZYAxis(boneAnimation.getValue().positionKeyFrames());
            swapTheZYAxis(boneAnimation.getValue().rotationKeyFrames());
         }
      }

      Animation.Keyframes keyframes = UniversalAnimLoader.NO_KEYFRAMES;
      if (extra.has("particleEffects")) {
         String identifier = ParticleEffectUtils.parseIdentifier((String)extra.getRaw("particleEffects"));
         keyframes = new Animation.Keyframes(
            keyframes.sounds(), new ParticleKeyframeData[]{new ParticleKeyframeData(beginTick, identifier, "body", "")}, keyframes.customInstructions()
         );
      }

      return new Animation(extra, endTick, loopType, bones, keyframes, new HashMap<>(), new HashMap<>());
   }

   public static void swapTheZYAxis(KeyframeStack rotationStack) {
      List<Keyframe> yKeyframes = new ArrayList<>(rotationStack.yKeyframes());
      rotationStack.yKeyframes().clear();
      rotationStack.yKeyframes().addAll(rotationStack.zKeyframes());
      rotationStack.zKeyframes().clear();
      rotationStack.zKeyframes().addAll(yKeyframes);
   }

   public static void correctEasings(KeyframeStack keyframeStack) {
      correctEasings(keyframeStack.xKeyframes());
      correctEasings(keyframeStack.yKeyframes());
      correctEasings(keyframeStack.zKeyframes());
   }

   public static void correctEasings(List<Keyframe> list) {
      EasingType previousEasing = EasingType.EASE_IN_OUT_SINE;
      List<List<Expression>> previousEasingArgs = new ObjectArrayList();
      Keyframe keyframe = null;

      for (int i = 0; i < list.size(); i++) {
         keyframe = list.get(i);
         list.set(i, new Keyframe(keyframe.length(), keyframe.startValue(), keyframe.endValue(), previousEasing, previousEasingArgs));
         previousEasing = keyframe.easingType();
         previousEasingArgs = keyframe.easingArgs();
      }

      if (keyframe != null) {
         list.add(new Keyframe(0.001F, keyframe.endValue(), keyframe.endValue(), keyframe.easingType(), keyframe.easingArgs()));
      }
   }

   private Map<String, BoneAnimation> moveDeserializer(List<JsonElement> node, boolean degrees, int version, float endTick) {
      Map<String, BoneAnimation> bones = new TreeMap<>();
      node.sort((e1, e2) -> {
         int i1 = e1.getAsJsonObject().get("tick").getAsInt();
         int i2 = e2.getAsJsonObject().get("tick").getAsInt();
         return Integer.compare(i1, i2);
      });

      for (JsonElement n : node) {
         JsonObject obj = n.getAsJsonObject();
         float tick = obj.get("tick").getAsFloat();
         if (!(tick > endTick)) {
            EasingType easing = easingTypeFromString(obj.has("easing") ? obj.get("easing").getAsString() : "linear");
            int turn = obj.has("turn") ? obj.get("turn").getAsInt() : 0;

            for (Entry<String, JsonElement> entry : obj.entrySet()) {
               if (!entry.getKey().equals("tick") && !entry.getKey().equals("comment") && !entry.getKey().equals("easing") && !entry.getKey().equals("turn")) {
                  String boneKey = UniversalAnimLoader.getCorrectPlayerBoneName(entry.getKey());
                  if (version < 3 && boneKey.equals("torso")) {
                     boneKey = "body";
                  }

                  BoneAnimation bone = bones.computeIfAbsent(
                     UniversalAnimLoader.getCorrectPlayerBoneName(boneKey),
                     boneName -> new BoneAnimation(new KeyframeStack(), new KeyframeStack(), new KeyframeStack(), new ArrayList<>())
                  );
                  this.addBodyPartIfExists(boneKey, bone, entry.getValue(), degrees, tick, easing, turn);
               }
            }
         }
      }

      BoneAnimation body = bones.get("body");
      if (body != null && !body.bendKeyFrames().isEmpty()) {
         BoneAnimation torso = bones.computeIfAbsent("torso", name -> new BoneAnimation());
         torso.bendKeyFrames().addAll(body.bendKeyFrames());
         body.bendKeyFrames().clear();
         if (!body.hasKeyframes()) {
            bones.remove("body");
         }
      }

      return bones;
   }

   private void addBodyPartIfExists(String boneName, BoneAnimation bone, JsonElement node, boolean degrees, float tick, EasingType easing, int turn) {
      JsonObject partNode = node.getAsJsonObject();
      boolean isItem = boneName.equals("right_item") || boneName.equals("left_item");
      boolean isCape = boneName.equals("cape");
      boolean isBody = boneName.equals("body");
      this.fillKeyframeStack(
         bone.positionKeyFrames(),
         getDefaultValues(boneName),
         isBody ? TransformType.POSITION : null,
         "x",
         "y",
         "z",
         partNode,
         degrees,
         tick,
         easing,
         turn,
         isItem,
         isCape,
         isBody
      );
      this.fillKeyframeStack(
         bone.rotationKeyFrames(), Vec3f.ZERO, TransformType.ROTATION, "pitch", "yaw", "roll", partNode, degrees, tick, easing, turn, isItem, isCape, isBody
      );
      this.fillKeyframeStack(
         bone.scaleKeyFrames(), Vec3f.ZERO, TransformType.SCALE, "scaleX", "scaleY", "scaleZ", partNode, degrees, tick, easing, turn, false, false, false
      );
      this.addPartIfExists(
         Keyframe.getLastKeyframeTime(bone.bendKeyFrames()),
         bone.bendKeyFrames(),
         0.0F,
         TransformType.BEND,
         "bend",
         partNode,
         degrees,
         tick,
         easing,
         turn,
         false
      );
   }

   private void fillKeyframeStack(
      KeyframeStack stack,
      Vec3f def,
      TransformType transformType,
      String x,
      String y,
      @Nullable String z,
      JsonObject node,
      boolean degrees,
      float tick,
      EasingType easing,
      int turn,
      boolean isItem,
      boolean isCape,
      boolean isBody
   ) {
      this.addPartIfExists(
         stack.getLastXAxisKeyframeTime(), stack.xKeyframes(), def.x(), transformType, x, node, degrees, tick, easing, turn, isItem || isCape || isBody
      );
      this.addPartIfExists(
         stack.getLastYAxisKeyframeTime(),
         stack.yKeyframes(),
         def.y(),
         transformType,
         y,
         node,
         degrees,
         tick,
         easing,
         turn,
         isItem || transformType == null || isBody && transformType == TransformType.ROTATION
      );
      this.addPartIfExists(
         stack.getLastZAxisKeyframeTime(),
         stack.zKeyframes(),
         def.z(),
         transformType,
         z,
         node,
         degrees,
         tick,
         easing,
         turn,
         isItem && transformType == TransformType.ROTATION || isCape
      );
   }

   private void addPartIfExists(
      float lastTick,
      List<Keyframe> part,
      float def,
      TransformType transformType,
      String name,
      JsonObject node,
      boolean degrees,
      float tick,
      EasingType easing,
      int rotate,
      boolean shouldNegate
   ) {
      if (node.has(name)) {
         Keyframe lastFrame = part.isEmpty() ? null : part.getLast();
         float prevTime = lastFrame != null ? lastTick : 0.0F;
         float delta = tick - prevTime;
         float value = convertPlayerAnimValue(def, node.get(name).getAsFloat(), transformType, degrees, shouldNegate, rotate);
         List<Expression> expressions = Collections.singletonList(FloatExpression.of(value));
         List<List<Expression>> emptyList = Collections.singletonList(new ObjectArrayList(0));
         part.add(
            new Keyframe(delta, lastFrame == null ? (transformType == TransformType.SCALE ? ONE : ZERO) : lastFrame.endValue(), expressions, easing, emptyList)
         );
      }
   }

   private static float convertPlayerAnimValue(float def, float value, TransformType transformType, boolean degrees, boolean shouldNegate, int rotate) {
      if (transformType == null) {
         value -= def;
      }

      if (shouldNegate) {
         value *= -1.0F;
      }

      if (transformType == TransformType.ROTATION) {
         if (degrees) {
            value = MochaMath.d2r(value);
         }

         value += (float) (Math.PI * 2) * rotate;
      }

      if (transformType == TransformType.POSITION) {
         value *= 16.0F;
      }

      return value;
   }

   public static EasingType easingTypeFromString(String string) {
      EasingType easingType = EasingType.fromString(string);
      return easingType == EasingType.LINEAR ? EasingType.fromString("ease" + string) : easingType;
   }

   public static Vec3f getDefaultValues(String bone) {
      return DEFAULT_VALUES.getOrDefault(bone, Vec3f.ZERO);
   }
}
