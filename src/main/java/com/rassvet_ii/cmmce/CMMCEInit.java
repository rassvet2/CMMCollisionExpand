package com.rassvet_ii.cmmce;

import com.rassvet_ii.cmmce.config.CMMCEConfig;
import com.rassvet_ii.cmmce.platforms.ModPlatform;

public class CMMCEInit {

    public static void init(ModPlatform platform) {

        Constants.LOG.info("Hello to {} from {}", Constants.MOD_ID, platform.getModloader());

        CMMCEConfig.HANDLER.load();
    }
}