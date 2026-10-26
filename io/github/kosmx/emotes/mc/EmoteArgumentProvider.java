package io.github.kosmx.emotes.mc;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.zigythebird.playeranimcore.animation.Animation;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.class_2168;
import net.minecraft.class_2172;
import net.minecraft.class_2561;
import net.minecraft.class_3544;
import net.minecraft.class_5455;
import net.minecraft.class_7225.class_7874;

public class EmoteArgumentProvider implements SuggestionProvider<class_2168> {
   public final Function<CommandContext<class_2168>, Map<UUID, Animation>> emotes;
   public final class_7874 registries;

   public EmoteArgumentProvider(Function<CommandContext<class_2168>, Map<UUID, Animation>> emotes) {
      this(class_5455.field_40585, emotes);
   }

   public EmoteArgumentProvider(class_7874 registries, Function<CommandContext<class_2168>, Map<UUID, Animation>> emotes) {
      this.emotes = emotes;
      this.registries = registries;
   }

   public CompletableFuture<Suggestions> getSuggestions(CommandContext<class_2168> context, SuggestionsBuilder builder) {
      List<String> suggestions = new LinkedList<>();

      for (Animation emote : this.emotes.apply(context).values()) {
         if (emote.data().has("name")) {
            String name = McUtils.fromJson(emote.data().getRaw("name"), this.registries).getString();
            if (name.contains(" ")) {
               name = "\"" + name + "\"";
            }

            suggestions.add(class_3544.method_57180(name));
         } else {
            suggestions.add(emote.uuid().toString());
         }
      }

      return class_2172.method_9253(suggestions.toArray(String[]::new), builder);
   }

   public static Animation getEmote(Map<UUID, Animation> emotes, CommandContext<class_2168> context, String argumentName) throws CommandSyntaxException {
      String id = StringArgumentType.getString(context, argumentName);

      try {
         UUID emoteID = UUID.fromString(id);
         Animation emote = emotes.get(emoteID);
         if (emote == null) {
            throw new SimpleCommandExceptionType(class_2561.method_43470("No emote with ID: " + emoteID)).create();
         } else {
            return emote;
         }
      } catch (IllegalArgumentException var7) {
         for (Animation emote : emotes.values()) {
            if (emote.data().has("name")) {
               String name = class_3544.method_57180(McUtils.fromJson(emote.data().getRaw("name"), class_5455.field_40585).getString());
               if (name.equals(id)) {
                  return emote;
               }
            }
         }

         throw new SimpleCommandExceptionType(class_2561.method_43470("Not emote with name: " + id)).create();
      }
   }
}
