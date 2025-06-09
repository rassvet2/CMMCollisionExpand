package com.rassvet_ii.cmmce.platforms.neoforge;

import com.rassvet_ii.cmmce.platforms.ModPlatform;
//? if neoforge {
/*import com.rassvet_ii.cmmce.CMMCEInit;
import com.rassvet_ii.cmmce.config.CMMCEConfigScreen;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
//? if <1.21 {
import net.neoforged.neoforge.client.ConfigScreenHandler;
//?} else {
/^import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
^///?}
@Mod("template")
public class NeoForgeInit implements ModPlatform {
	public NeoForgeInit() {
		CMMCEInit.init(this);
        ModLoadingContext.get().registerExtensionPoint(
                //? if <1.21 {
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        ((client, parent) -> CMMCEConfigScreen.build(parent))
                )
                //?} else {
                /^IConfigScreenFactory.class,
                () -> (client, parent) -> CMMCEConfigScreen.build(parent)
                ^///?}
        );
	}

    @Override
    public String getModloader() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }
}
*///?} else {
@SuppressWarnings("unused")
class NeoForgeInit extends ModPlatform.Invalid {}
//?}