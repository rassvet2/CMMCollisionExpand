package com.rassvet_ii.cmmce.mixin;

import com.rassvet_ii.cmmce.ICMMCEBoxHolder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Entity.class)
public class MixinEntity implements ICMMCEBoxHolder {
    @Unique
    private Box cmmce$box;

    @Override
    public void cmmce$setEncompassBox(Box encompassBox) {
        cmmce$box = encompassBox;
    }

    @Override
    public Box cmmce$getEncompassBox() {
        return cmmce$box;
    }
}
