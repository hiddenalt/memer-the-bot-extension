package ru.hiddenalt.mtbe;

import net.fabricmc.api.ModInitializer;
import ru.hiddenalt.mtbe.graphql.GraphqlClient;
import ru.hiddenalt.mtbe.gui.screen.ingame.ActionsScreen;
import ru.hiddenalt.mtbe.settings.SettingsManager;

public class MemerTheBotExtensionMod implements ModInitializer {

	static protected ActionsScreen lastOpenedActionsScreen;

	public static ActionsScreen getLastOpenedActionsScreen() {
		return lastOpenedActionsScreen;
	}

	public static void setLastOpenedActionsScreen(ActionsScreen lastOpenedActionsScreen) {
		MemerTheBotExtensionMod.lastOpenedActionsScreen = lastOpenedActionsScreen;
	}

	@Override
	public void onInitialize() {
		SettingsManager.reloadSettings();
		GraphqlClient.initialize();
	}
}
