package io.github.kosmx.emotes.arch.mixin;

import net.minecraft.class_2535;
import net.minecraft.class_8609;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_8609.class)
public interface ServerCommonPacketListenerAccessor {
   @Accessor("field_45013")
   class_2535 getConnection();
}
