package com.rassvet_ii.cmmce;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;

public class BBExpander {
    public static boolean shouldExpand(Entity entity) {
        if (!EntityPredicates.CAN_HIT.test(entity)) return false;

        var player = MinecraftClient.getInstance().player;
        if (player == null) return false;
        if (player.getPos().squaredDistanceTo(entity.getPos()) > MathHelper.square(player.getEntityInteractionRange() + 1)) return false;

        var containedInFilter = CMMCEConfig.HANDLER.instance().getFilterEntities().contains(entity.getType());
        return CMMCEConfig.HANDLER.instance().getFilterMode().shouldExpand(containedInFilter);
    }

    public static Box expand(Entity entity, Box box) {
        var encompassBox = entity instanceof ICMMCEBoxHolder holder
                ? holder.cmmce$getEncompassBox()
                : null;
        if (encompassBox == null) return box;

        return box.union(encompassBox.offset(entity.getPos()));
    }
}
