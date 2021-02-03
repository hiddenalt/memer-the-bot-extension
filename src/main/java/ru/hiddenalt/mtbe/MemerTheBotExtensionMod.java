package ru.hiddenalt.mtbe;

import net.fabricmc.api.ModInitializer;
import ru.hiddenalt.mtbe.graphql.GraphqlClient;
import ru.hiddenalt.mtbe.settings.SettingsManager;

public class MemerTheBotExtensionMod implements ModInitializer {
	@Override
	public void onInitialize() {
		SettingsManager.reloadSettings();
		GraphqlClient.initialize();
	}
}
