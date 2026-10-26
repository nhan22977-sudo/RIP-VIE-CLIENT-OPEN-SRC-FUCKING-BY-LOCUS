package me.drake.vieclient.mixin;

import net.minecraft.class_7172;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(class_7172.class)
public interface MixinSimpleOption {
   @Accessor("field_37868")
   void vieSetRawValue(Object var1);
}
