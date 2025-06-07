package com.rassvet_ii.cmmce.mixin;

import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayer.MultiPhase.class)
public interface IMultiPhase {
    @Accessor("phases")
    RenderLayer.MultiPhaseParameters getPhases();
}
