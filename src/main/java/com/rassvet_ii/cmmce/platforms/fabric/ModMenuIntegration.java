package com.rassvet_ii.cmmce.platforms.fabric;

//? if fabric {
import com.rassvet_ii.cmmce.config.CMMCEConfigScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return CMMCEConfigScreen::build;
    }
}
//?} else {
/*public class ModMenuIntegration {}
*///?}