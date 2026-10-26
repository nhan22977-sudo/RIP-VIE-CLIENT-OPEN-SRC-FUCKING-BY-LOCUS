package com.zigythebird.playeranimcore.network;

import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.animation.ExtraAnimationData;
import com.zigythebird.playeranimcore.animation.keyframe.BoneAnimation;
import com.zigythebird.playeranimcore.animation.keyframe.Keyframe;
import com.zigythebird.playeranimcore.easing.EasingType;
import com.zigythebird.playeranimcore.enums.AnimationFormat;
import com.zigythebird.playeranimcore.loading.PlayerAnimatorLoader;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import com.zigythebird.playeranimcore.math.Vec3f;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import team.unnamed.mocha.parser.ast.Expression;
import team.unnamed.mocha.parser.ast.FloatExpression;

public final class LegacyAnimationBinary {
   public static final Predicate<String> BEND_BONE = name -> !name.equals("head") && !name.equals("left_item") && !name.equals("right_item");
   public static final Predicate<String> ITEM_BONE = name -> name.equals("left_item") || name.equals("right_item");

   public static void write(Animation animation, ByteBuf buf, int version) {
      Map<String, BoneAnimation> boneAnimations = bonesToWrite(animation);
      buf.writeInt(animation.data().<Float>get("beginTick").orElse(0.0F).intValue());
      int endTick = animation.data().<Float>get("endTick").orElse(animation.length()).intValue();
      buf.writeInt(endTick);
      buf.writeInt((int)animation.length());
      if (animation.loopType() == Animation.LoopType.HOLD_ON_LAST_FRAME) {
         putBoolean(buf, true);
         buf.writeInt(endTick);
      } else {
         putBoolean(buf, animation.loopType().shouldPlayAgain(null, animation));
         buf.writeInt((int)animation.loopType().restartFromTick(null, animation) + 1);
      }

      boolean easeBefore = animation.data()
         .<Boolean>get("easeBeforeKeyframe")
         .orElse(animation.data().data().getOrDefault("format", AnimationFormat.GECKOLIB) == AnimationFormat.GECKOLIB);
      putBoolean(buf, easeBefore);
      putBoolean(buf, false);
      buf.writeByte(keyframeSize(version));
      if (version >= 2) {
         buf.writeInt(boneAnimations.size());

         for (Entry<String, BoneAnimation> part : boneAnimations.entrySet()) {
            putString(buf, UniversalAnimLoader.restorePlayerBoneName(part.getKey()));
            writePart(buf, part.getKey(), part.getValue(), version, easeBefore);
         }
      } else {
         writePart(buf, "head", boneAnimations.get("head"), version, easeBefore);
         writePart(buf, "body", boneAnimations.get("body"), version, easeBefore);
         writePart(buf, "right_arm", boneAnimations.get("right_arm"), version, easeBefore);
         writePart(buf, "left_arm", boneAnimations.get("left_arm"), version, easeBefore);
         writePart(buf, "right_leg", boneAnimations.get("right_leg"), version, easeBefore);
         writePart(buf, "left_leg", boneAnimations.get("left_leg"), version, easeBefore);
      }

      NetworkUtils.writeUuid(buf, animation.uuid());
   }

   private static Map<String, BoneAnimation> bonesToWrite(Animation animation) {
      BoneAnimation torso = animation.boneAnimations().get("torso");
      if (torso != null && !torso.bendKeyFrames().isEmpty() && animation.data().getNullable("applyBendToOtherBones") == Boolean.TRUE) {
         Map<String, BoneAnimation> bones = new LinkedHashMap<>(animation.boneAnimations());
         BoneAnimation body = bones.getOrDefault("body", new BoneAnimation());
         bones.put("body", new BoneAnimation(body.rotationKeyFrames(), body.positionKeyFrames(), body.scaleKeyFrames(), torso.bendKeyFrames()));
         BoneAnimation bendlessTorso = new BoneAnimation(torso.rotationKeyFrames(), torso.positionKeyFrames(), torso.scaleKeyFrames(), Collections.emptyList());
         if (bendlessTorso.hasKeyframes()) {
            bones.put("torso", bendlessTorso);
         } else {
            bones.remove("torso");
         }

         return bones;
      } else {
         return animation.boneAnimations();
      }
   }

   public static void write(Animation animation, ByteBuf buf) {
      write(animation, buf, getCurrentVersion());
   }

   private static void writePart(ByteBuf buf, String name, BoneAnimation part, int version, boolean easeBefore) {
      if (part == null) {
         int i = 6;
         if (BEND_BONE.test(name)) {
            i += 2;
         }

         if (version >= 3) {
            i += 3;
         }

         for (; i > 0; i--) {
            if (version >= 2) {
               putBoolean(buf, false);
               buf.writeInt(0);
            } else {
               buf.writeInt(-1);
            }
         }
      } else {
         Vec3f def = PlayerAnimatorLoader.getDefaultValues(name);
         boolean isItem = ITEM_BONE.test(name);
         boolean isBody = name.equals("body");
         boolean isCape = name.equals("cape");
         writeKeyframes(buf, part.positionKeyFrames().xKeyframes(), def.x(), version, easeBefore, isBody, isItem || isCape || isBody);
         writeKeyframes(
            buf, isItem ? part.positionKeyFrames().zKeyframes() : part.positionKeyFrames().yKeyframes(), def.y(), version, easeBefore, isBody, !isBody
         );
         writeKeyframes(
            buf, isItem ? part.positionKeyFrames().yKeyframes() : part.positionKeyFrames().zKeyframes(), def.z(), version, easeBefore, isBody, isCape
         );
         writeKeyframes(buf, part.rotationKeyFrames().xKeyframes(), version, easeBefore, isItem || isCape || isBody);
         writeKeyframes(buf, isItem ? part.rotationKeyFrames().zKeyframes() : part.rotationKeyFrames().yKeyframes(), version, easeBefore, isItem || isBody);
         writeKeyframes(buf, isItem ? part.rotationKeyFrames().yKeyframes() : part.rotationKeyFrames().zKeyframes(), version, easeBefore, isItem || isCape);
         if (BEND_BONE.test(name)) {
            if (version >= 2) {
               putBoolean(buf, false);
               buf.writeInt(0);
            } else {
               buf.writeInt(-1);
            }

            writeKeyframes(buf, part.bendKeyFrames(), version, easeBefore, false);
         }

         if (version >= 3) {
            writeKeyframes(buf, part.scaleKeyFrames().xKeyframes(), version, easeBefore, false);
            writeKeyframes(buf, part.scaleKeyFrames().yKeyframes(), version, easeBefore, false);
            writeKeyframes(buf, part.scaleKeyFrames().zKeyframes(), version, easeBefore, false);
         }
      }
   }

   private static void writeKeyframes(ByteBuf buf, List<Keyframe> part, int version, boolean easeBefore, boolean negate) {
      writeKeyframes(buf, part, 0.0F, version, easeBefore, false, negate);
   }

   private static void writeKeyframes(ByteBuf buf, List<Keyframe> part, float def, int version, boolean easeBefore, boolean div, boolean negate) {
      if (version >= 2) {
         putBoolean(buf, !part.isEmpty());
      }

      int keyframeCount = part.size();
      if (!easeBefore) {
         keyframeCount--;
      }

      buf.writeInt(keyframeCount);
      if (keyframeCount > 0) {
         float tickAccumulator = 0.0F;

         for (int i = 0; i < keyframeCount; i++) {
            Keyframe move = part.get(i);
            tickAccumulator += move.length();
            buf.writeInt((int)Math.floor(tickAccumulator));
            buf.writeFloat((MolangLoader.MOCHA_ENGINE.eval(move.endValue()) * (negate ? -1 : 1) + def) / (div ? 16.0F : 1.0F));
            List<Expression> easingArgsToWrite = Collections.emptyList();
            EasingType easingToWrite;
            if (easeBefore) {
               easingToWrite = move.easingType();
               if (move.easingArgs() != null && !move.easingArgs().isEmpty() && !move.easingArgs().getFirst().isEmpty()) {
                  easingArgsToWrite = move.easingArgs().getFirst();
               }
            } else {
               Keyframe nextMove = part.get(i + 1);
               easingToWrite = nextMove.easingType();
               if (nextMove.easingArgs() != null && !nextMove.easingArgs().isEmpty() && !nextMove.easingArgs().getFirst().isEmpty()) {
                  easingArgsToWrite = nextMove.easingArgs().getFirst();
               }
            }

            buf.writeByte(easingToWrite.id);
            if (version >= 4) {
               if (easingArgsToWrite != null && !easingArgsToWrite.isEmpty()) {
                  buf.writeFloat(MolangLoader.MOCHA_ENGINE.eval(easingArgsToWrite));
               } else {
                  buf.writeFloat(Float.NaN);
               }
            }
         }
      }
   }

   public static Animation read(ByteBuf buf) throws IOException {
      return read(buf, getCurrentVersion());
   }

   public static Animation read(ByteBuf buf, int version) throws IOException {
      ExtraAnimationData data = new ExtraAnimationData();
      int beginTick = buf.readInt();
      data.put("beginTick", (float)beginTick);
      int endTick = Math.max(buf.readInt(), beginTick + 1);
      if (endTick <= 0) {
         throw new IOException("endTick must be bigger than 0");
      }

      data.put("endTick", (float)endTick);
      int stopTick = buf.readInt();
      boolean isLooped = getBoolean(buf);
      int returnTick = Math.max(0, buf.readInt() - 1);
      Animation.LoopType loopType = Animation.LoopType.PLAY_ONCE;
      if (isLooped) {
         if (returnTick > endTick) {
            throw new IOException("The returnTick has to be a non-negative value smaller than the endTick value");
         }

         if (returnTick == 0) {
            loopType = Animation.LoopType.LOOP;
         } else {
            loopType = Animation.LoopType.returnToTickLoop(returnTick);
         }
      }

      if (loopType == Animation.LoopType.PLAY_ONCE) {
         endTick = stopTick <= endTick ? endTick + 3 : stopTick;
      }

      boolean easeBefore = getBoolean(buf);
      data.put("easeBeforeKeyframe", easeBefore);
      getBoolean(buf);
      int keyframeSize = buf.readByte();
      if (keyframeSize <= 0) {
         throw new IOException("keyframe size must be greater than 0, current: " + keyframeSize);
      }

      Map<String, BoneAnimation> boneAnimations = new HashMap<>();
      if (version >= 2) {
         int count = buf.readInt();

         for (int i = 0; i < count; i++) {
            String name = UniversalAnimLoader.getCorrectPlayerBoneName(getString(buf));
            boneAnimations.put(name, readPart(buf, name, new BoneAnimation(), version, keyframeSize, easeBefore));
         }
      } else {
         boneAnimations.put("head", readPart(buf, "head", new BoneAnimation(), version, keyframeSize, easeBefore));
         boneAnimations.put("body", readPart(buf, "body", new BoneAnimation(), version, keyframeSize, easeBefore));
         boneAnimations.put("right_arm", readPart(buf, "right_arm", new BoneAnimation(), version, keyframeSize, easeBefore));
         boneAnimations.put("left_arm", readPart(buf, "left_arm", new BoneAnimation(), version, keyframeSize, easeBefore));
         boneAnimations.put("right_leg", readPart(buf, "right_leg", new BoneAnimation(), version, keyframeSize, easeBefore));
         boneAnimations.put("left_leg", readPart(buf, "left_leg", new BoneAnimation(), version, keyframeSize, easeBefore));
      }

      boneAnimations.values().removeIf(bone -> !bone.hasKeyframes());
      BoneAnimation body = boneAnimations.get("body");
      if (body != null && !body.bendKeyFrames().isEmpty()) {
         BoneAnimation torso = boneAnimations.computeIfAbsent("torso", namex -> new BoneAnimation());
         torso.bendKeyFrames().addAll(body.bendKeyFrames());
         body.bendKeyFrames().clear();
         if (!body.hasKeyframes()) {
            boneAnimations.remove("body");
         }

         data.put("applyBendToOtherBones", true);
      }

      data.put("uuid", NetworkUtils.readUuid(buf));
      data.put("format", AnimationFormat.PLAYER_ANIMATOR);
      return new Animation(data, endTick, loopType, boneAnimations, UniversalAnimLoader.NO_KEYFRAMES, new HashMap<>(), new HashMap<>());
   }

   private static BoneAnimation readPart(ByteBuf buf, String name, BoneAnimation part, int version, int keyframeSize, boolean easeBefore) {
      Vec3f def = PlayerAnimatorLoader.getDefaultValues(name);
      boolean isBody = name.equals("body");
      boolean isItem = ITEM_BONE.test(name);
      boolean isCape = name.equals("cape");
      readKeyframes(
         buf, part.positionKeyFrames().xKeyframes(), def.x(), version, keyframeSize, isBody, isItem || isCape || isBody, easeBefore, PlayerAnimatorLoader.ZERO
      );
      readKeyframes(buf, part.positionKeyFrames().yKeyframes(), def.y(), version, keyframeSize, isBody, !isBody, easeBefore, PlayerAnimatorLoader.ZERO);
      readKeyframes(buf, part.positionKeyFrames().zKeyframes(), def.z(), version, keyframeSize, isBody, isCape, easeBefore, PlayerAnimatorLoader.ZERO);
      readKeyframes(buf, part.rotationKeyFrames().xKeyframes(), version, keyframeSize, isItem || isCape || isBody, easeBefore, PlayerAnimatorLoader.ZERO);
      readKeyframes(buf, part.rotationKeyFrames().yKeyframes(), version, keyframeSize, isItem || isBody, easeBefore, PlayerAnimatorLoader.ZERO);
      readKeyframes(buf, part.rotationKeyFrames().zKeyframes(), version, keyframeSize, isItem || isCape, easeBefore, PlayerAnimatorLoader.ZERO);
      if (BEND_BONE.test(name)) {
         readKeyframes(buf, new ArrayList<>(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ZERO);
         readKeyframes(buf, part.bendKeyFrames(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ZERO);
      }

      if (version >= 3) {
         readKeyframes(buf, part.scaleKeyFrames().xKeyframes(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ONE);
         readKeyframes(buf, part.scaleKeyFrames().yKeyframes(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ONE);
         readKeyframes(buf, part.scaleKeyFrames().zKeyframes(), version, keyframeSize, false, easeBefore, PlayerAnimatorLoader.ONE);
      }

      if (!easeBefore) {
         PlayerAnimatorLoader.correctEasings(part.positionKeyFrames());
         PlayerAnimatorLoader.correctEasings(part.rotationKeyFrames());
         PlayerAnimatorLoader.correctEasings(part.scaleKeyFrames());
         PlayerAnimatorLoader.correctEasings(part.bendKeyFrames());
      }

      if (isItem) {
         PlayerAnimatorLoader.swapTheZYAxis(part.positionKeyFrames());
         PlayerAnimatorLoader.swapTheZYAxis(part.rotationKeyFrames());
      }

      return part;
   }

   private static void readKeyframes(
      ByteBuf buf, List<Keyframe> part, int version, int keyframeSize, boolean negate, boolean easeBefore, List<Expression> fallback
   ) {
      readKeyframes(buf, part, 0.0F, version, keyframeSize, false, negate, easeBefore, fallback);
   }

   private static void readKeyframes(
      ByteBuf buf, List<Keyframe> part, float def, int version, int keyframeSize, boolean mul, boolean negate, boolean easeBefore, List<Expression> fallback
   ) {
      int length;
      boolean enabled;
      if (version >= 2) {
         enabled = getBoolean(buf);
         length = buf.readInt();
      } else {
         length = buf.readInt();
         enabled = length >= 0;
      }

      if (!enabled) {
         if (length > 0) {
            buf.readerIndex(buf.readerIndex() + length * keyframeSize);
         }

         part.clear();
      } else {
         int lastTick = 0;

         for (int i = 0; i < length; i++) {
            Keyframe prevKeyframe = part.isEmpty() ? null : part.getLast();
            int currentPos = buf.readerIndex();
            int tick = buf.readInt();
            float keyframeLength = (float)tick - lastTick;
            lastTick = tick;
            List<Expression> expression = Collections.singletonList(FloatExpression.of((buf.readFloat() - def) * (mul ? 16 : 1) * (negate ? -1 : 1)));
            EasingType easingType = EasingType.fromId(buf.readByte());
            Float easingArg = null;
            if (version >= 4) {
               easingArg = buf.readFloat();
               if (Float.isNaN(easingArg)) {
                  easingArg = null;
               }
            }

            List<Expression> startValue = prevKeyframe != null ? prevKeyframe.endValue() : (easeBefore ? expression : fallback);
            part.add(
               new Keyframe(
                  keyframeLength,
                  startValue,
                  expression,
                  easingType,
                  easingArg == null
                     ? Collections.singletonList(Collections.emptyList())
                     : Collections.singletonList(Collections.singletonList(FloatExpression.of(easingArg)))
               )
            );
            buf.readerIndex(currentPos + keyframeSize);
         }
      }
   }

   public static int getCurrentVersion() {
      return 4;
   }

   public static int calculateSize(Animation animation) {
      return calculateSize(animation, getCurrentVersion());
   }

   public static int calculateSize(Animation animation, int version) {
      int size = 36;
      Map<String, BoneAnimation> boneAnimations = bonesToWrite(animation);
      boolean easeBefore = animation.data()
         .<Boolean>get("easeBeforeKeyframe")
         .orElse(animation.data().data().getOrDefault("format", AnimationFormat.GECKOLIB) == AnimationFormat.GECKOLIB);
      if (version < 2) {
         size += partSize(boneAnimations.get("head"), false, version, easeBefore);
         size += partSize(boneAnimations.get("body"), true, version, easeBefore);
         size += partSize(boneAnimations.get("right_arm"), true, version, easeBefore);
         size += partSize(boneAnimations.get("left_arm"), true, version, easeBefore);
         size += partSize(boneAnimations.get("right_leg"), true, version, easeBefore);
         size += partSize(boneAnimations.get("left_leg"), true, version, easeBefore);
      } else {
         size += 4;

         for (Entry<String, BoneAnimation> entry : boneAnimations.entrySet()) {
            size += stringSize(UniversalAnimLoader.restorePlayerBoneName(entry.getKey()))
               + partSize(entry.getValue(), BEND_BONE.test(entry.getKey()), version, easeBefore);
         }
      }

      return size;
   }

   private static int stringSize(String str) {
      byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
      return bytes.length + 4;
   }

   private static int partSize(BoneAnimation part, boolean bendable, int version, boolean easeBefore) {
      if (part == null) {
         int i = 6;
         if (bendable) {
            i += 2;
         }

         if (version >= 3) {
            i += 3;
         }

         return i * (version >= 2 ? 5 : 4);
      } else {
         int size = 0;
         size += axisSize(part.positionKeyFrames().xKeyframes(), version, easeBefore);
         size += axisSize(part.positionKeyFrames().yKeyframes(), version, easeBefore);
         size += axisSize(part.positionKeyFrames().zKeyframes(), version, easeBefore);
         size += axisSize(part.rotationKeyFrames().xKeyframes(), version, easeBefore);
         size += axisSize(part.rotationKeyFrames().yKeyframes(), version, easeBefore);
         size += axisSize(part.rotationKeyFrames().zKeyframes(), version, easeBefore);
         if (bendable) {
            size += version >= 2 ? 5 : 4;
            size += axisSize(part.bendKeyFrames(), version, easeBefore);
         }

         if (version >= 3) {
            size += axisSize(part.scaleKeyFrames().xKeyframes(), version, easeBefore);
            size += axisSize(part.scaleKeyFrames().yKeyframes(), version, easeBefore);
            size += axisSize(part.scaleKeyFrames().zKeyframes(), version, easeBefore);
         }

         return size;
      }
   }

   private static int axisSize(List<Keyframe> axis, int version, boolean easeBefore) {
      return Math.max(0, axis.size() - (easeBefore ? 0 : 1)) * keyframeSize(version) + (version >= 2 ? 5 : 4);
   }

   private static byte keyframeSize(int version) {
      return (byte)(version < 4 ? 9 : 13);
   }

   public static void putBoolean(ByteBuf byteBuffer, boolean bl) {
      byteBuffer.writeByte((byte)(bl ? 1 : 0));
   }

   public static boolean getBoolean(ByteBuf buf) {
      return buf.readByte() != 0;
   }

   public static void putString(ByteBuf buf, String str) {
      byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
      buf.writeInt(bytes.length);
      buf.writeBytes(bytes);
   }

   public static String getString(ByteBuf buf) {
      int len = buf.readInt();
      byte[] bytes = new byte[len];
      buf.readBytes(bytes);
      return new String(bytes, StandardCharsets.UTF_8);
   }
}
