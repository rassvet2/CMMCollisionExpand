package com.rassvet_ii.cmmce.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderLayer.MultiPhaseParameters.class)
public interface IMultiPhaseParameters {
    @Accessor("texture")
    RenderPhase.TextureBase getTexture();
}
