package com.rassvet_ii.cmmce.platforms.fabric;

import com.rassvet_ii.cmmce.platforms.ModPlatform;
//? if fabric {
import com.rassvet_ii.cmmce.CMMCEInit;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class FabricInit implements ModInitializer, ModPlatform {
	@Override
	public void onInitialize() {
		CMMCEInit.init(this);
	}

	@Override
	public String getModloader() {
		return "Fabric";
	}

	@Override
	public boolean isModLoaded(String modloader) {
		return FabricLoader.getInstance().isModLoaded(modloader);
	}
}
//?} else {
/*class FabricInit extends ModPlatform.Invalid {}
*///?}