package me.drake.vieclient.mixin;

import net.minecraft.class_310;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(class_310.class)
public interface MinecraftClientAccessor {
   @Invoker("method_1583")
   void invokeDoItemUse();

   @Accessor("field_1752")
   void setItemUseCooldown(int var1);

   @Accessor("field_1752")
   int getItemUseCooldown();
}
