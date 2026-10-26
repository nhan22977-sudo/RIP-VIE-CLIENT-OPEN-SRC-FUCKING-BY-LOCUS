package io.github.kosmx.emotes.mc;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.api.events.server.ServerEmoteAPI;
import io.github.kosmx.emotes.mc.services.IPermissionService;
import io.github.kosmx.emotes.server.serializer.UniversalEmoteSerializer;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.class_11890;
import net.minecraft.class_12094;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2186;
import net.minecraft.class_2561;
import net.minecraft.class_7157;
import net.minecraft.class_2170.class_5364;

public final class ServerCommands {
   public static final List<String> PERMISSIONS = List.of(
      "emotes.play.player", "emotes.stop.player", "emotes.stop.forced", "emotes.play.showhidden", "emotes.reload"
   );

   public static <T> void register(CommandDispatcher<T> dispatcher, class_7157 registryAccess, class_5364 environment) {
      register(dispatcher, environment == class_5364.field_25420);
   }

   public static <T> void register(CommandDispatcher<T> dispatcher, boolean isDedicated) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("emotes")
                  .then(
                     class_2170.method_9247("play")
                        .then(
                           ((RequiredArgumentBuilder)class_2170.method_9244("emote", StringArgumentType.string())
                                 .suggests(new EmoteArgumentProvider(ServerCommands::getEmotes))
                                 .executes(context -> {
                                    UUID player = ((class_2168)context.getSource()).method_9207().method_5667();
                                    boolean admin = IPermissionService.INSTANCE
                                       .check((class_2168)context.getSource(), "emotes.stop.forced", class_12094.field_63198);
                                    if (!admin && ServerEmoteAPI.isForcedEmote(player)) {
                                       throw new SimpleCommandExceptionType(class_2561.method_43470("Can't stop forced emote without admin rights")).create();
                                    }

                                    Animation emote = EmoteArgumentProvider.getEmote(getEmotes(context), context, "emote");
                                    ServerEmoteAPI.playEmote(player, emote, false);
                                    return 0;
                                 }))
                              .then(
                                 ((RequiredArgumentBuilder)((RequiredArgumentBuilder)class_2170.method_9244("avatar", class_2186.method_9306())
                                          .requires(IPermissionService.INSTANCE.require("emotes.play.player", class_12094.field_63197)))
                                       .executes(
                                          context -> {
                                             ServerEmoteAPI.playEmote(
                                                getAvatar(context, "avatar").method_5667(),
                                                EmoteArgumentProvider.getEmote(getEmotes(context), context, "emote"),
                                                false
                                             );
                                             return 0;
                                          }
                                       ))
                                    .then(
                                       class_2170.method_9244("forced", BoolArgumentType.bool())
                                          .executes(
                                             context -> {
                                                ServerEmoteAPI.playEmote(
                                                   getAvatar(context, "avatar").method_5667(),
                                                   EmoteArgumentProvider.getEmote(getEmotes(context), context, "emote"),
                                                   BoolArgumentType.getBool(context, "forced")
                                                );
                                                return 0;
                                             }
                                          )
                                    )
                              )
                        )
                  ))
               .then(
                  ((LiteralArgumentBuilder)class_2170.method_9247("stop").executes(context -> {
                        boolean admin = IPermissionService.INSTANCE.check((class_2168)context.getSource(), "emotes.stop.forced", class_12094.field_63198);
                        UUID player = ((class_2168)context.getSource()).method_9207().method_5667();
                        if (!admin && ServerEmoteAPI.isForcedEmote(player)) {
                           throw new SimpleCommandExceptionType(class_2561.method_43470("Can't stop forced emote without admin rights")).create();
                        }

                        ServerEmoteAPI.playEmote(player, null, false);
                        return 0;
                     }))
                     .then(
                        ((RequiredArgumentBuilder)class_2170.method_9244("avatar", class_2186.method_9309())
                              .requires(IPermissionService.INSTANCE.require("emotes.stop.player", class_12094.field_63197)))
                           .executes(context -> {
                              ServerEmoteAPI.playEmote(getAvatar(context, "avatar").method_5667(), null, false);
                              return 0;
                           })
                     )
               ))
            .then(
               ((LiteralArgumentBuilder)class_2170.method_9247("reload")
                     .requires(ctx -> IPermissionService.INSTANCE.check(ctx, "emotes.reload", class_12094.field_63199) && isDedicated))
                  .executes(context -> {
                     UniversalEmoteSerializer.loadEmotes();
                     return 0;
                  })
            )
      );
   }

   private static Map<UUID, Animation> getEmotes(CommandContext<class_2168> context) {
      return IPermissionService.INSTANCE.check((class_2168)context.getSource(), "emotes.play.showhidden", class_12094.field_63197)
         ? UniversalEmoteSerializer.getLoadedEmotes()
         : UniversalEmoteSerializer.SERVER_EMOTES;
   }

   public static class_11890 getAvatar(CommandContext<class_2168> context, String name) throws CommandSyntaxException {
      if (class_2186.method_9313(context, name) instanceof class_11890 avatar) {
         return avatar;
      } else {
         throw new SimpleCommandExceptionType(class_2561.method_43470("The specified entity must be an avatar!")).create();
      }
   }
}
