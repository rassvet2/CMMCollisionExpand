package com.rassvet_ii.cmmce.mixin;

import com.google.common.base.Preconditions;
import com.llamalad7.mixinextras.sugar.Local;
import com.rassvet_ii.cmmce.EncompassBoxBuilder;
import com.rassvet_ii.cmmce.ICMMCEBoxHolder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(EntityRenderDispatcher.class)
public class MixinEntityRenderDispatcher {

    @Unique
    @Nullable
    private Entity cmmce$entity = null;

    @ModifyVariable(
            method = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/entity/Entity;DDDFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At("HEAD"),
            argsOnly = true)
    private Entity captureEntity(Entity entity) {
        Preconditions.checkState(cmmce$entity == null, "another working entity");
        cmmce$entity = entity;
        return entity;
    }

    @ModifyVariable(
            method = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At(value = "HEAD"),
            argsOnly = true)
    private VertexConsumerProvider beforeEntityRenderer(VertexConsumerProvider vertexConsumers) {
        return new EncompassBoxBuilder(vertexConsumers);
    }


    @ModifyVariable(
            method = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;render(Lnet/minecraft/client/render/entity/state/EntityRenderState;DDDLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/EntityRenderer;)V",
            at = @At(value = "TAIL"),
            argsOnly = true)
    private VertexConsumerProvider afterEntityRenderer(
            VertexConsumerProvider vertexConsumers,
            @Local(argsOnly = true, ordinal = 0) double x,
            @Local(argsOnly = true, ordinal = 1) double y,
            @Local(argsOnly = true, ordinal = 2) double z
    ) {
        Preconditions.checkState(cmmce$entity != null, "working entity not present");
        Preconditions.checkState(vertexConsumers instanceof EncompassBoxBuilder, "expected EncompassBoxBuilder");
        var builder = (EncompassBoxBuilder) vertexConsumers;

        ((ICMMCEBoxHolder) cmmce$entity).cmmce$setEncompassBox(builder.build().offset(-x, -y, -z));
        cmmce$entity = null;

        return builder.unwrap();
    }
}
