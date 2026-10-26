package com.zigythebird.playeranimcore.event;

import com.zigythebird.playeranimcore.animation.AnimationController;
import com.zigythebird.playeranimcore.molang.QueryBinding;
import team.unnamed.mocha.MochaEngine;

public class MolangEvent {
   public static final Event<MolangEvent.MolangEventInterface> MOLANG_EVENT = new Event<>(listeners -> (controller, engine, queryBinding) -> {
      for (MolangEvent.MolangEventInterface listener : listeners) {
         listener.registerMolangQueries(controller, engine, queryBinding);
      }
   });

   @FunctionalInterface
   public interface MolangEventInterface {
      void registerMolangQueries(AnimationController var1, MochaEngine<AnimationController> var2, QueryBinding<AnimationController> var3);
   }
}
