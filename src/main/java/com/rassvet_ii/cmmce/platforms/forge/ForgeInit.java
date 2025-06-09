package com.rassvet_ii.cmmce.platforms.forge;

import com.rassvet_ii.cmmce.platforms.ModPlatform;
//? if forge {
/*import com.rassvet_ii.cmmce.CMMCEInit;
import com.rassvet_ii.cmmce.config.CMMCEConfigScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

@Mod("template")
public class ForgeInit implements ModPlatform {
    public ForgeInit() {
        CMMCEInit.init(this);
        MinecraftForge.registerConfigScreen(CMMCEConfigScreen::build);
    }

    @Override
    public String getModloader() {
        return "LexForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
*///?} else {
@SuppressWarnings("unused")
class ForgeInit extends ModPlatform.Invalid {}
//?}