package com.rassvet_ii.cmmce.mixin;

import net.minecraft.client.renderer.RenderStateShard;
import org.spongepowered.asm.mixin.Mixin;
//? if <1.21.5 {
/*import org.spongepowered.asm.mixin.gen.Accessor;
*///?}

@Mixin(RenderStateShard.class)
public interface IRenderStateShard {
    //? if <1.21.5 {
    /*@Accessor("name")
    String getName();
    *///?}
}
