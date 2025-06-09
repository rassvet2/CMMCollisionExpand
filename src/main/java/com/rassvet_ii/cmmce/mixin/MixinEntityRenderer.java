package com.rassvet_ii.cmmce.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.rassvet_ii.cmmce.BBExpander;
import com.rassvet_ii.cmmce.config.CMMCEConfig;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(method = "Lnet/minecraft/client/renderer/entity/EntityRenderer;extractHitboxes(Lnet/minecraft/world/entity/Entity;FZ)Lnet/minecraft/client/renderer/entity/state/HitboxesRenderState;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;extractAdditionalHitboxes(Lnet/minecraft/world/entity/Entity;Lcom/google/common/collect/ImmutableList$Builder;F)V"))
    private void extractHitboxes(
            Entity entity, float tickProgress, boolean green, CallbackInfoReturnable<HitboxesRenderState> cir,
            @Local ImmutableList.Builder<HitboxRenderState> builder
    ) {
        if (CMMCEConfig.HANDLER.instance().renderDebugBox() && BBExpander.shouldExpand(entity)) {
            var box = BBExpander.expand(entity, entity.getBoundingBox());

            builder.add(new HitboxRenderState(
                    box.minX, box.minY, box.minZ,
                    box.maxX, box.maxY, box.maxZ,
                    (float) -entity.getX(),
                    (float) -entity.getY(),
                    (float) -entity.getZ(),
                    0.0f, 1.0f, 1.0f
            ));
        }
    }
}
