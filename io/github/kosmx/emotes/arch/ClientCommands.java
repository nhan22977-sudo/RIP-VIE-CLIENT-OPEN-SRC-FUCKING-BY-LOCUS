package io.github.kosmx.emotes.arch;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.zigythebird.playeranimcore.animation.Animation;
import io.github.kosmx.emotes.main.EmoteHolder;
import io.github.kosmx.emotes.main.network.ClientEmotePlay;
import io.github.kosmx.emotes.mc.EmoteArgumentProvider;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.class_2168;
import net.minecraft.class_2170;
import net.minecraft.class_2561;
import net.minecraft.class_7157;

public class ClientCommands {
   public static final class_2561 FORCED = class_2561.method_43471("emotecraft.cant.override.forced");

   public static <T> void register(CommandDispatcher<T> dispatcher, class_7157 registryAccess) {
      dispatcher.register(
         (LiteralArgumentBuilder)((LiteralArgumentBuilder)class_2170.method_9247("emotes-client")
               .then(
                  class_2170.method_9247("play")
                     .then(
                        class_2170.method_9244("emote", StringArgumentType.string())
                           .suggests(new EmoteArgumentProvider(ClientCommands::getEmotes))
                           .executes(ctx -> {
                              if (!ClientEmotePlay.clientStartLocalEmote(EmoteArgumentProvider.getEmote(getEmotes(ctx), ctx, "emote"))) {
                                 throw new SimpleCommandExceptionType(FORCED).create();
                              } else {
                                 return 0;
                              }
                           })
                     )
               ))
            .then(class_2170.method_9247("stop").executes(ctx -> {
               if (ClientEmotePlay.isForcedEmote()) {
                  throw new SimpleCommandExceptionType(FORCED).create();
               }

               ClientEmotePlay.clientStopLocalEmote();
               return 0;
            }))
      );
   }

   private static Map<UUID, Animation> getEmotes(CommandContext<class_2168> context) {
      return EmoteHolder.list.entrySet().stream().collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().emote));
   }
}
