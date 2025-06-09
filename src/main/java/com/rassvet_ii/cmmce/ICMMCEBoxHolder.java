package com.rassvet_ii.cmmce;

import net.minecraft.world.phys.AABB;

public interface ICMMCEBoxHolder {
    void cmmce$setEncompassBox(AABB encompassBox);
    AABB cmmce$getEncompassBox();
}
