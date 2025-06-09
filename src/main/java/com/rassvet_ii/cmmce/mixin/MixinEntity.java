package com.rassvet_ii.cmmce.mixin;

import com.rassvet_ii.cmmce.ICMMCEBoxHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class MixinEntity implements ICMMCEBoxHolder {
    @Unique
    private AABB cmmce$box;

    @Override
    public void cmmce$setEncompassBox(AABB encompassBox) {
        cmmce$box = encompassBox;
    }

    @Override
    public AABB cmmce$getEncompassBox() {
        return cmmce$box;
    }
}
