package com.rassvet_ii.cmmce;

import com.rassvet_ii.cmmce.config.CMMCEConfig;
import com.rassvet_ii.cmmce.platform.Services;

public class CMMCEMod {

    public static void init() {

        if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) {

            Constants.LOG.info("Hello to {}", Constants.MOD_ID);
        }

        CMMCEConfig.HANDLER.load();
    }
}