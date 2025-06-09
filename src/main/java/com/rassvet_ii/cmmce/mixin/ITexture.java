package com.rassvet_ii.cmmce.mixin;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(RenderStateShard.TextureStateShard.class)
public interface ITexture {
    @Invoker("cutoutTexture")
    Optional<ResourceLocation> getId0();
}
