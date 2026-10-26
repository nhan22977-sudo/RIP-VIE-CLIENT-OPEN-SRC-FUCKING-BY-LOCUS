package com.zigythebird.playeranim.api;

import com.zigythebird.playeranim.animation.AvatarAnimManager;
import com.zigythebird.playeranimcore.animation.layered.IAnimation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.class_11890;
import net.minecraft.class_2960;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface PlayerAnimationFactory {
   PlayerAnimationFactory.FactoryHolder ANIMATION_DATA_FACTORY = new PlayerAnimationFactory.FactoryHolder();

   @Nullable
   IAnimation invoke(@NotNull class_11890 var1);

   class FactoryHolder {
      private static final List<Function<class_11890, PlayerAnimationFactory.FactoryHolder.DataHolder>> factories = new ArrayList<>();

      private FactoryHolder() {
      }

      public void registerFactory(@Nullable class_2960 id, int priority, @NotNull PlayerAnimationFactory factory) {
         factories.add(
            player -> Optional.ofNullable(factory.invoke(player))
               .map(animation -> new PlayerAnimationFactory.FactoryHolder.DataHolder(id, priority, animation))
               .orElse(null)
         );
      }

      @Internal
      public void prepareAnimations(class_11890 player, AvatarAnimManager playerStack, Map<class_2960, IAnimation> animationMap) {
         for (Function<class_11890, PlayerAnimationFactory.FactoryHolder.DataHolder> factory : factories) {
            PlayerAnimationFactory.FactoryHolder.DataHolder dataHolder = factory.apply(player);
            if (dataHolder != null) {
               playerStack.addAnimLayer(dataHolder.priority(), dataHolder.animation());
               if (dataHolder.id() != null) {
                  animationMap.put(dataHolder.id(), dataHolder.animation());
               }
            }
         }
      }

      @Internal
      private record DataHolder(@Nullable class_2960 id, int priority, @NotNull IAnimation animation) {
      }
   }
}
