package com.rassvet_ii.cmmce.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.rassvet_ii.cmmce.BBExpander;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(method = "createHitbox",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;appendHitboxes(Lnet/minecraft/entity/Entity;Lcom/google/common/collect/ImmutableList$Builder;F)V"))
    private void createHitbox(
            Entity entity, float tickProgress, boolean green, CallbackInfoReturnable<EntityHitboxAndView> cir,
            @Local ImmutableList.Builder<EntityHitbox> builder
    ) {
        if (BBExpander.shouldExpand(entity)) {
            var box = BBExpander.expand(entity, entity.getBoundingBox());

            builder.add(new EntityHitbox(
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
