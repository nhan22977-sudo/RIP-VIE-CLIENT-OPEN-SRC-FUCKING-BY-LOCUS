package me.drake.vieclient.mixin;

import java.util.List;
import java.util.Map;
import net.minecraft.class_11683.class_12048;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_12048.class)
public interface ModelCommandsAccessor {
   @Accessor("field_62982")
   Map vie$getOpaqueModelCommands();

   @Accessor("field_62983")
   List vie$getBlendedModelCommands();
}
