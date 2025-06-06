package com.rassvet_ii.cmmce.mixin;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import com.rassvet_ii.cmmce.BBExpander;
import com.rassvet_ii.cmmce.ICMMCEBoxHolder;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.state.EntityHitbox;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer<T extends Entity, S extends EntityRenderState> {

    @Inject(method = "updateRenderState",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getInstance()Lnet/minecraft/client/MinecraftClient;"))
    public void updateRenderState(
            CallbackInfo ci,
            @Local(argsOnly = true) T entity,
            @Local(argsOnly = true) S state
    ) {
        if (BBExpander.shouldExpand(entity)) {
            @SuppressWarnings("unchecked")
            var renderer = (EntityRenderer<T, S>) (Object) this;
            BBExpander.createEncompassBox(entity, renderer, state)
                    .ifPresent(((ICMMCEBoxHolder) entity)::cmmce$setEncompassBox);
        }
    }

    @Inject(method = "createHitbox",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderer;appendHitboxes(Lnet/minecraft/entity/Entity;Lcom/google/common/collect/ImmutableList$Builder;F)V"))
    private void createHitbox(
            T entity, float tickProgress, boolean green, CallbackInfoReturnable<EntityHitboxAndView> cir,
            @Local ImmutableList.Builder<EntityHitbox> builder
    ) {
        if (BBExpander.shouldExpand(entity)) {
            var box = (((ICMMCEBoxHolder) entity)).cmmce$getEncompassBox();

            builder.add(new EntityHitbox(
                    box.minX, box.minY, box.minZ,
                    box.maxX, box.maxY, box.maxZ,
                    0f, 0f, 0f,
                    0.0f, 1.0f, 1.0f
            ));
        }
    }
}
