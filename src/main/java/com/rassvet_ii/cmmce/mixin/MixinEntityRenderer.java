package com.rassvet_ii.cmmce.mixin;

import com.rassvet_ii.cmmce.BBExpander;
import com.rassvet_ii.cmmce.config.CMMCEConfig;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
//? if >= 1.21.5 {
import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.HitboxRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//?} else {
/*import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if >= 1.21.3 {
import net.minecraft.client.renderer.ShapeRenderer;
 //?} else {
/^import net.minecraft.client.renderer.LevelRenderer;
^///?}
*///?}

//? if >= 1.21.5 {
@Mixin(EntityRenderer.class)
//?} else {
/*@Mixin(EntityRenderDispatcher.class)
*///?}
public class MixinEntityRenderer {

    //? if >= 1.21.5 {
    @Inject(
            method = "Lnet/minecraft/client/renderer/entity/EntityRenderer;extractHitboxes(Lnet/minecraft/world/entity/Entity;FZ)Lnet/minecraft/client/renderer/entity/state/HitboxesRenderState;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;extractAdditionalHitboxes(Lnet/minecraft/world/entity/Entity;Lcom/google/common/collect/ImmutableList$Builder;F)V"))
    private void extractHitboxes(
            Entity entity, float tickProgress, boolean green,
            CallbackInfoReturnable<HitboxesRenderState> cir,
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
    //?} else {
    /*@Inject(method = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;renderHitbox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/entity/Entity;FFFF)V",
        at = @At(value = "TAIL"))
    private static void renderHitbox(
            PoseStack poseStack, VertexConsumer buffer, Entity entity,
            float red, float green, float blue, float alpha, CallbackInfo ci
    ) {
        if (CMMCEConfig.HANDLER.instance().renderDebugBox() && BBExpander.shouldExpand(entity)) {
            var box = BBExpander.expand(entity, entity.getBoundingBox())
                    .move(-entity.getX(), -entity.getY(), -entity.getZ());
            //? if >= 1.21.3 {
            ShapeRenderer.renderLineBox(poseStack, buffer, box, 0f, 1f, 1f, 1f);
             //?} else {
            /^LevelRenderer.renderLineBox(poseStack, buffer, box, 0f, 1f, 1f, 1f);
            ^///?}
        }
    }
    *///?}
}
