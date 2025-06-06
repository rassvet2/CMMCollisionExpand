package com.rassvet_ii.cmmce;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;

public class BBExpander {
    public static boolean shouldExpand(Entity entity) {
//        return EntityType.getId(entity.getType()).equals(Identifier.of("minecraft:sheep"));
        return true;
    }

    public static Box expand(Entity entity, Box box) {
        var encompassBox = entity instanceof ICMMCEBoxHolder holder
                ? holder.cmmce$getEncompassBox()
                : null;
        if (encompassBox == null) return box;

        return box.union(encompassBox.offset(entity.getPos()));
    }
}
