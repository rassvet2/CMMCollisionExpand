package com.rassvet_ii.cmmce.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.rassvet_ii.cmmce.BBExpander;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ProjectileUtil.class)
public class MixinProjectileUtil {

    @ModifyVariable(
            method = "getEntityHitResult*",
            at = @At(value = "STORE"),
            ordinal = 1)
    private static AABB getEntityHitResult(
            AABB box,
            @Local(ordinal = 2) Entity entity
    ) {
        return BBExpander.expand(entity, box);
    }
}
