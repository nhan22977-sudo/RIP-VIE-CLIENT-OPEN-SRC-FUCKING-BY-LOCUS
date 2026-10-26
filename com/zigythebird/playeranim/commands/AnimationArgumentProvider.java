package com.zigythebird.playeranim.commands;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.zigythebird.playeranim.animation.PlayerAnimResources;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.class_2172;
import net.minecraft.class_2960;

public class AnimationArgumentProvider<S> implements SuggestionProvider<S> {
   public CompletableFuture<Suggestions> getSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
      List<String> suggestions = new LinkedList<>();

      for (class_2960 animation : PlayerAnimResources.getAnimations().keySet()) {
         suggestions.add(animation.toString());
      }

      return class_2172.method_9265(suggestions, builder);
   }
}
