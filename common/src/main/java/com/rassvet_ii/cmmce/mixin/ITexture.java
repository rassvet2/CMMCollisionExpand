package com.rassvet_ii.cmmce.mixin;

import net.minecraft.client.render.RenderPhase;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(RenderPhase.Texture.class)
public interface ITexture {
    @Invoker("getId")
    Optional<Identifier> getId0();
}
