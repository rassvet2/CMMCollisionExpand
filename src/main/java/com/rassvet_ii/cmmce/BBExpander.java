package com.rassvet_ii.cmmce;

import com.rassvet_ii.cmmce.config.CMMCEConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
//? if >=1.21.5 {
import net.minecraft.world.entity.EntitySelector;
//?}

public class BBExpander {
    public static boolean shouldExpand(Entity entity) {
        //? if >=1.21.5 {
        if (!EntitySelector.CAN_BE_PICKED.test(entity)) return false;
        //?} else {
        /*if (entity.isSpectator() || !entity.isPickable()) return false;
        *///?}

        var player = Minecraft.getInstance().player;
        if (player == null) return false;

        //? if > 1.20.4 {
        double range = player.entityInteractionRange();
        //?} else {
        /*var gameMode = Minecraft.getInstance().gameMode;
        double range = gameMode == null ? 6.0
                : gameMode.hasFarPickRange() ? 6.0
                : gameMode.getPickRange();
        *///?}
        if (player.position().distanceToSqr(entity.position()) > Mth.square(range + 1)) return false;

        var containedInFilter = CMMCEConfig.HANDLER.instance().getFilterEntities().contains(entity.getType());
        return CMMCEConfig.HANDLER.instance().getFilterMode().shouldExpand(containedInFilter);
    }

    public static AABB expand(Entity entity, AABB box) {
        var encompassBox = entity instanceof ICMMCEBoxHolder holder
                ? holder.cmmce$getEncompassBox()
                : null;
        if (encompassBox == null) return box;

        return box.minmax(encompassBox.move(entity.position()));
    }
}
