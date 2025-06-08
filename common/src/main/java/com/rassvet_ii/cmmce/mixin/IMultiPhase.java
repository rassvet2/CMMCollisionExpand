package com.rassvet_ii.cmmce.mixin;

import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderType.CompositeRenderType.class)
public interface IMultiPhase {
    @Accessor("state")
    RenderType.CompositeState getPhases();
}
