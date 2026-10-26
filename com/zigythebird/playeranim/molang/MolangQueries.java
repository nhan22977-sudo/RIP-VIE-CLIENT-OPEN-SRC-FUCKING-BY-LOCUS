package com.zigythebird.playeranim.molang;

import com.zigythebird.playeranim.animation.PlayerAnimationController;
import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.molang.MolangLoader;
import com.zigythebird.playeranimcore.molang.QueryBinding;
import java.util.Arrays;
import java.util.Optional;
import net.minecraft.class_11890;
import net.minecraft.class_11903;
import net.minecraft.class_12131;
import net.minecraft.class_12206;
import net.minecraft.class_1268;
import net.minecraft.class_1304;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_2350;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_5354;
import net.minecraft.class_5498;
import net.minecraft.class_6025;
import net.minecraft.class_742;
import net.minecraft.class_9460;
import net.minecraft.class_9817;

public final class MolangQueries {
   public static final String ACTOR_COUNT = "actor_count";
   public static final String BLOCKING = "blocking";
   public static final String BODY_X_ROTATION = "body_x_rotation";
   public static final String BODY_Y_ROTATION = "body_y_rotation";
   public static final String CARDINAL_FACING = "cardinal_facing";
   public static final String CARDINAL_FACING_2D = "cardinal_facing_2d";
   public static final String CARDINAL_PLAYER_FACING = "cardinal_player_facing";
   public static final String DAY = "day";
   public static final String DEATH_TICKS = "death_ticks";
   public static final String DISTANCE_FROM_CAMERA = "distance_from_camera";
   public static final String EQUIPMENT_COUNT = "equipment_count";
   public static final String FRAME_ALPHA = "frame_alpha";
   public static final String GET_ACTOR_INFO_ID = "get_actor_info_id";
   public static final String GROUND_SPEED = "ground_speed";
   public static final String HAS_CAPE = "has_cape";
   public static final String HAS_COLLISION = "has_collision";
   public static final String HAS_GRAVITY = "has_gravity";
   public static final String HAS_HEAD_GEAR = "has_head_gear";
   public static final String HAS_OWNER = "has_owner";
   public static final String HAS_PLAYER_RIDER = "has_player_rider";
   public static final String HAS_RIDER = "has_rider";
   public static final String HEAD_X_ROTATION = "head_x_rotation";
   public static final String HEAD_Y_ROTATION = "head_y_rotation";
   public static final String HEALTH = "health";
   public static final String HURT_TIME = "hurt_time";
   public static final String INVULNERABLE_TICKS = "invulnerable_ticks";
   public static final String IS_ALIVE = "is_alive";
   public static final String IS_ANGRY = "is_angry";
   public static final String IS_BABY = "is_baby";
   public static final String IS_BREATHING = "is_breathing";
   public static final String IS_FIRE_IMMUNE = "is_fire_immune";
   public static final String IS_FIRST_PERSON = "is_first_person";
   public static final String IS_IN_CONTACT_WITH_WATER = "is_in_contact_with_water";
   public static final String IS_IN_LAVA = "is_in_lava";
   public static final String IS_IN_WATER = "is_in_water";
   public static final String IS_IN_WATER_OR_RAIN = "is_in_water_or_rain";
   public static final String IS_INVISIBLE = "is_invisible";
   public static final String IS_LEASHED = "is_leashed";
   public static final String IS_MOVING = "is_moving";
   public static final String IS_ON_FIRE = "is_on_fire";
   public static final String IS_ON_GROUND = "is_on_ground";
   public static final String IS_RIDING = "is_riding";
   public static final String IS_SADDLED = "is_saddled";
   public static final String IS_SILENT = "is_silent";
   public static final String IS_SLEEPING = "is_sleeping";
   public static final String IS_SNEAKING = "is_sneaking";
   public static final String IS_SPRINTING = "is_sprinting";
   public static final String IS_SWIMMING = "is_swimming";
   public static final String IS_USING_ITEM = "is_using_item";
   public static final String IS_WALL_CLIMBING = "is_wall_climbing";
   public static final String LIFE_TIME = "life_time";
   public static final String LIMB_SWING = "limb_swing";
   public static final String LIMB_SWING_AMOUNT = "limb_swing_amount";
   public static final String MAIN_HAND_ITEM_MAX_DURATION = "main_hand_item_max_duration";
   public static final String MAIN_HAND_ITEM_USE_DURATION = "main_hand_item_use_duration";
   public static final String MAX_HEALTH = "max_health";
   public static final String MOON_BRIGHTNESS = "moon_brightness";
   public static final String MOON_PHASE = "moon_phase";
   public static final String MOVEMENT_DIRECTION = "movement_direction";
   public static final String PLAYER_LEVEL = "player_level";
   public static final String RIDER_BODY_X_ROTATION = "rider_body_x_rotation";
   public static final String RIDER_BODY_Y_ROTATION = "rider_body_y_rotation";
   public static final String RIDER_HEAD_X_ROTATION = "rider_head_x_rotation";
   public static final String RIDER_HEAD_Y_ROTATION = "rider_head_y_rotation";
   public static final String SCALE = "scale";
   public static final String SLEEP_ROTATION = "sleep_rotation";
   public static final String TIME_OF_DAY = "time_of_day";
   public static final String TIME_STAMP = "time_stamp";
   public static final String VERTICAL_SPEED = "vertical_speed";
   public static final String YAW_SPEED = "yaw_speed";

   public static void setDefaultQueryValues(QueryBinding<AnimationController> binding) {
      MolangLoader.setDoubleQuery(binding, "actor_count", actor -> class_310.method_1551().field_1769.field_61737.field_61735.size());
      MolangLoader.setDoubleQuery(binding, "cardinal_player_facing", actor -> ((PlayerAnimationController)actor).getAvatar().method_5735().ordinal());
      MolangLoader.setDoubleQuery(binding, "day", actor -> ((PlayerAnimationController)actor).getAvatar().method_73183().method_75260() / 24000.0);
      MolangLoader.setDoubleQuery(binding, "frame_alpha", actor -> actor.getAnimationData().getPartialTick());
      MolangLoader.setBoolQuery(binding, "has_cape", actor -> {
         class_11890 avatar = ((PlayerAnimationController)actor).getAvatar();
         if (avatar instanceof class_742 player) {
            return player.method_52814().comp_1627() != null;
         } else {
            return avatar instanceof class_11903 mannequin ? mannequin.method_52814().comp_1627() != null : false;
         }
      });
      MolangLoader.setBoolQuery(
         binding,
         "is_first_person",
         actor -> ((PlayerAnimationController)actor).getAvatar() instanceof class_742 player && player.method_7340()
            ? class_310.method_1551().field_1690.method_31044() == class_5498.field_26664
            : false
      );
      MolangLoader.setDoubleQuery(binding, "life_time", actor -> actor.isActive() ? actor.getAnimationTime() : 0.0);
      MolangLoader.setDoubleQuery(binding, "moon_brightness", actor -> {
         class_11890 avatar = ((PlayerAnimationController)actor).getAvatar();
         return ((Float)avatar.method_73183().method_75728().method_75695(class_12206.field_64344, avatar.method_73189())).floatValue();
      });
      MolangLoader.setDoubleQuery(binding, "moon_phase", actor -> {
         class_11890 avatar = ((PlayerAnimationController)actor).getAvatar();
         return ((class_12131)avatar.method_73183().method_75728().method_75695(class_12206.field_64343, avatar.method_73189())).method_75261();
      });
      MolangLoader.setDoubleQuery(
         binding, "player_level", actor -> ((PlayerAnimationController)actor).getAvatar() instanceof class_742 player ? player.field_7520 : 0.0
      );
      MolangLoader.setDoubleQuery(binding, "time_of_day", actor -> ((PlayerAnimationController)actor).getAvatar().method_73183().method_8532() / 24000.0);
      MolangLoader.setDoubleQuery(binding, "time_stamp", actor -> ((PlayerAnimationController)actor).getAvatar().method_73183().method_75260());
      setDefaultEntityQueryValues(binding);
      setDefaultLivingEntityQueryValues(binding);
   }

   private static void setDefaultEntityQueryValues(QueryBinding<AnimationController> binding) {
      MolangLoader.setDoubleQuery(
         binding, "body_x_rotation", actor -> ((PlayerAnimationController)actor).getAvatar().method_5695(actor.getAnimationData().getPartialTick())
      );
      MolangLoader.setDoubleQuery(
         binding,
         "body_y_rotation",
         actor -> ((PlayerAnimationController)actor).getAvatar() instanceof class_1309 living
            ? class_3532.method_16439(actor.getAnimationData().getPartialTick(), living.field_6220, living.field_6283)
            : ((PlayerAnimationController)actor).getAvatar().method_5705(actor.getAnimationData().getPartialTick())
      );
      MolangLoader.setDoubleQuery(binding, "cardinal_facing", actor -> ((PlayerAnimationController)actor).getAvatar().method_5735().method_10146());
      MolangLoader.setDoubleQuery(binding, "cardinal_facing_2d", actor -> {
         int directionId = ((PlayerAnimationController)actor).getAvatar().method_5735().method_10146();
         return directionId < 2 ? 6.0 : directionId;
      });
      MolangLoader.setDoubleQuery(
         binding,
         "distance_from_camera",
         actor -> class_310.method_1551().field_1773.method_19418().method_71156().method_1022(((PlayerAnimationController)actor).getAvatar().method_73189())
      );
      MolangLoader.setDoubleQuery(binding, "get_actor_info_id", actor -> ((PlayerAnimationController)actor).getAvatar().method_5628());
      MolangLoader.setDoubleQuery(
         binding,
         "equipment_count",
         actor -> ((PlayerAnimationController)actor).getAvatar() instanceof class_9460 armorable
            ? Arrays.stream(class_1304.values()).filter(class_1304::method_46643).filter(slot -> !armorable.method_6118(slot).method_7960()).count()
            : 0L
      );
      MolangLoader.setBoolQuery(binding, "has_collision", actor -> !((PlayerAnimationController)actor).getAvatar().field_5960);
      MolangLoader.setBoolQuery(binding, "has_gravity", actor -> !((PlayerAnimationController)actor).getAvatar().method_5740());
      MolangLoader.setBoolQuery(
         binding, "has_owner", actor -> ((PlayerAnimationController)actor).getAvatar() instanceof class_6025 ownable && ownable.method_66287() != null
      );
      MolangLoader.setBoolQuery(binding, "has_player_rider", actor -> ((PlayerAnimationController)actor).getAvatar().method_5703(class_1657.class::isInstance));
      MolangLoader.setBoolQuery(binding, "has_rider", actor -> ((PlayerAnimationController)actor).getAvatar().method_5782());
      MolangLoader.setBoolQuery(binding, "is_alive", actor -> ((PlayerAnimationController)actor).getAvatar().method_5805());
      MolangLoader.setBoolQuery(
         binding, "is_angry", actor -> ((PlayerAnimationController)actor).getAvatar() instanceof class_5354 neutralMob && neutralMob.method_29511()
      );
      MolangLoader.setBoolQuery(
         binding,
         "is_breathing",
         actor -> ((PlayerAnimationController)actor).getAvatar().method_5669() >= ((PlayerAnimationController)actor).getAvatar().method_5748()
      );
      MolangLoader.setBoolQuery(binding, "is_fire_immune", actor -> ((PlayerAnimationController)actor).getAvatar().method_5864().method_19946());
      MolangLoader.setBoolQuery(binding, "is_invisible", actor -> ((PlayerAnimationController)actor).getAvatar().method_5767());
      MolangLoader.setBoolQuery(binding, "is_in_contact_with_water", actor -> ((PlayerAnimationController)actor).getAvatar().method_5721());
      MolangLoader.setBoolQuery(binding, "is_in_lava", actor -> ((PlayerAnimationController)actor).getAvatar().method_5771());
      MolangLoader.setBoolQuery(binding, "is_in_water", actor -> ((PlayerAnimationController)actor).getAvatar().method_5799());
      MolangLoader.setBoolQuery(binding, "is_in_water_or_rain", actor -> ((PlayerAnimationController)actor).getAvatar().method_5721());
      MolangLoader.setBoolQuery(
         binding, "is_leashed", actor -> ((PlayerAnimationController)actor).getAvatar() instanceof class_9817 leashable && leashable.method_60953()
      );
      MolangLoader.setBoolQuery(binding, "is_moving", actor -> actor.getAnimationData().isMoving());
      MolangLoader.setBoolQuery(binding, "is_on_fire", actor -> ((PlayerAnimationController)actor).getAvatar().method_5809());
      MolangLoader.setBoolQuery(binding, "is_on_ground", actor -> ((PlayerAnimationController)actor).getAvatar().method_24828());
      MolangLoader.setBoolQuery(binding, "is_riding", actor -> ((PlayerAnimationController)actor).getAvatar().method_5765());
      MolangLoader.setBoolQuery(
         binding,
         "is_saddled",
         actor -> ((PlayerAnimationController)actor).getAvatar() instanceof class_9460 saddleable
            && !saddleable.method_6118(class_1304.field_55946).method_7960()
      );
      MolangLoader.setBoolQuery(binding, "is_silent", actor -> ((PlayerAnimationController)actor).getAvatar().method_5701());
      MolangLoader.setBoolQuery(binding, "is_sneaking", actor -> ((PlayerAnimationController)actor).getAvatar().method_18276());
      MolangLoader.setBoolQuery(binding, "is_sprinting", actor -> ((PlayerAnimationController)actor).getAvatar().method_5624());
      MolangLoader.setBoolQuery(binding, "is_swimming", actor -> ((PlayerAnimationController)actor).getAvatar().method_5681());
      MolangLoader.setDoubleQuery(
         binding,
         "movement_direction",
         actor -> actor.getAnimationData().isMoving()
            ? class_2350.method_58251(((PlayerAnimationController)actor).getAvatar().method_18798()).method_10146()
            : 6.0
      );
      MolangLoader.setDoubleQuery(
         binding,
         "rider_body_x_rotation",
         actor -> ((PlayerAnimationController)actor).getAvatar().method_5782()
            ? (
               ((PlayerAnimationController)actor).getAvatar().method_31483() instanceof class_1309
                  ? 0.0
                  : ((PlayerAnimationController)actor).getAvatar().method_31483().method_5695(actor.getAnimationData().getPartialTick())
            )
            : 0.0
      );
      MolangLoader.setDoubleQuery(
         binding,
         "rider_body_y_rotation",
         actor -> ((PlayerAnimationController)actor).getAvatar().method_5782()
            ? (
               ((PlayerAnimationController)actor).getAvatar().method_31483() instanceof class_1309 living
                  ? class_3532.method_16439(actor.getAnimationData().getPartialTick(), living.field_6220, living.field_6283)
                  : ((PlayerAnimationController)actor).getAvatar().method_31483().method_5705(actor.getAnimationData().getPartialTick())
            )
            : 0.0
      );
      MolangLoader.setDoubleQuery(
         binding,
         "rider_head_x_rotation",
         actor -> ((PlayerAnimationController)actor).getAvatar().method_31483() instanceof class_1309 living
            ? living.method_5695(actor.getAnimationData().getPartialTick())
            : 0.0
      );
      MolangLoader.setDoubleQuery(
         binding,
         "rider_head_y_rotation",
         actor -> ((PlayerAnimationController)actor).getAvatar().method_31483() instanceof class_1309 living
            ? living.method_5705(actor.getAnimationData().getPartialTick())
            : 0.0
      );
      MolangLoader.setDoubleQuery(binding, "vertical_speed", actor -> ((PlayerAnimationController)actor).getAvatar().method_18798().field_1351);
      MolangLoader.setDoubleQuery(
         binding,
         "yaw_speed",
         actor -> ((PlayerAnimationController)actor).getAvatar().method_36454() - ((PlayerAnimationController)actor).getAvatar().field_5982
      );
   }

   private static void setDefaultLivingEntityQueryValues(QueryBinding<AnimationController> binding) {
      MolangLoader.setBoolQuery(binding, "blocking", actor -> ((PlayerAnimationController)actor).getAvatar().method_6039());
      MolangLoader.setDoubleQuery(
         binding,
         "death_ticks",
         actor -> ((PlayerAnimationController)actor).getAvatar().field_6213 == 0
            ? 0.0
            : ((PlayerAnimationController)actor).getAvatar().field_6213 + actor.getAnimationData().getPartialTick()
      );
      MolangLoader.setDoubleQuery(binding, "ground_speed", actor -> ((PlayerAnimationController)actor).getAvatar().method_18798().method_37267());
      MolangLoader.setBoolQuery(
         binding, "has_head_gear", actor -> !((PlayerAnimationController)actor).getAvatar().method_6118(class_1304.field_6169).method_7960()
      );
      MolangLoader.setDoubleQuery(
         binding, "head_x_rotation", actor -> ((PlayerAnimationController)actor).getAvatar().method_5695(actor.getAnimationData().getPartialTick())
      );
      MolangLoader.setDoubleQuery(
         binding, "head_y_rotation", actor -> ((PlayerAnimationController)actor).getAvatar().method_5705(actor.getAnimationData().getPartialTick())
      );
      MolangLoader.setDoubleQuery(binding, "health", actor -> ((PlayerAnimationController)actor).getAvatar().method_6032());
      MolangLoader.setDoubleQuery(
         binding,
         "hurt_time",
         actor -> ((PlayerAnimationController)actor).getAvatar().field_6235 == 0
            ? 0.0
            : ((PlayerAnimationController)actor).getAvatar().field_6235 - actor.getAnimationData().getPartialTick()
      );
      MolangLoader.setDoubleQuery(
         binding,
         "invulnerable_ticks",
         actor -> ((PlayerAnimationController)actor).getAvatar().field_6008 == 0
            ? 0.0
            : ((PlayerAnimationController)actor).getAvatar().field_6008 - actor.getAnimationData().getPartialTick()
      );
      MolangLoader.setBoolQuery(binding, "is_baby", actor -> ((PlayerAnimationController)actor).getAvatar().method_6109());
      MolangLoader.setBoolQuery(binding, "is_sleeping", actor -> ((PlayerAnimationController)actor).getAvatar().method_6113());
      MolangLoader.setBoolQuery(binding, "is_using_item", actor -> ((PlayerAnimationController)actor).getAvatar().method_6115());
      MolangLoader.setBoolQuery(binding, "is_wall_climbing", actor -> ((PlayerAnimationController)actor).getAvatar().method_6101());
      MolangLoader.setDoubleQuery(binding, "limb_swing", actor -> ((PlayerAnimationController)actor).getAvatar().field_42108.method_48569());
      MolangLoader.setDoubleQuery(
         binding,
         "limb_swing_amount",
         actor -> ((PlayerAnimationController)actor).getAvatar().field_42108.method_48570(actor.getAnimationData().getPartialTick())
      );
      MolangLoader.setDoubleQuery(
         binding,
         "main_hand_item_max_duration",
         actor -> ((PlayerAnimationController)actor).getAvatar().method_6047().method_7935(((PlayerAnimationController)actor).getAvatar())
      );
      MolangLoader.setDoubleQuery(
         binding,
         "main_hand_item_use_duration",
         actor -> ((PlayerAnimationController)actor).getAvatar().method_6058() == class_1268.field_5808
            ? ((PlayerAnimationController)actor).getAvatar().method_6048() / 20.0 + actor.getAnimationData().getPartialTick()
            : 0.0
      );
      MolangLoader.setDoubleQuery(binding, "max_health", actor -> ((PlayerAnimationController)actor).getAvatar().method_6063());
      MolangLoader.setDoubleQuery(binding, "scale", actor -> ((PlayerAnimationController)actor).getAvatar().method_55693());
      MolangLoader.setDoubleQuery(
         binding,
         "sleep_rotation",
         actor -> Optional.ofNullable(((PlayerAnimationController)actor).getAvatar().method_18401())
            .<Float>map(class_2350::method_10144)
            .orElse(0.0F)
            .floatValue()
      );
   }
}
