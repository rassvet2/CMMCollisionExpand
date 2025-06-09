package com.rassvet_ii.cmmce.mixin;

import com.google.common.base.Preconditions;
import com.llamalad7.mixinextras.sugar.Local;
import com.rassvet_ii.cmmce.EncompassBoxBuilder;
import com.rassvet_ii.cmmce.ICMMCEBoxHolder;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
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
            //? if >=1.21.3 {
            method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            //?} else {
            /*method = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            *///?}
            at = @At("HEAD"),
            argsOnly = true)
    private Entity captureEntity(Entity entity) {
        Preconditions.checkState(cmmce$entity == null, "another working entity");
        cmmce$entity = entity;
        return entity;
    }

    @ModifyVariable(
            //? if >=1.21.3 {
            method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            //?} else {
            /*method = "render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            *///?}
            at = @At(value = "HEAD"),
            argsOnly = true)
    private MultiBufferSource beforeEntityRenderer(MultiBufferSource vertexConsumers) {
        return new EncompassBoxBuilder(vertexConsumers);
    }


    @ModifyVariable(
            //? if >=1.21.3 {
            method = "render(Lnet/minecraft/world/entity/Entity;DDDFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;ILnet/minecraft/client/renderer/entity/EntityRenderer;)V",
            //?} else {
            /*method = "render(Lnet/minecraft/world/entity/Entity;DDDFFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            *///?}
            at = @At(value = "TAIL"),
            argsOnly = true)
    private MultiBufferSource afterEntityRenderer(
            MultiBufferSource vertexConsumers,
            @Local(argsOnly = true, ordinal = 0) double x,
            @Local(argsOnly = true, ordinal = 1) double y,
            @Local(argsOnly = true, ordinal = 2) double z
    ) {
        Preconditions.checkState(cmmce$entity != null, "working entity not present");
        Preconditions.checkState(vertexConsumers instanceof EncompassBoxBuilder, "expected EncompassBoxBuilder");
        var builder = (EncompassBoxBuilder) vertexConsumers;

        ((ICMMCEBoxHolder) cmmce$entity).cmmce$setEncompassBox(builder.build().move(-x, -y, -z));
        cmmce$entity = null;

        return builder.unwrap();
    }
}
