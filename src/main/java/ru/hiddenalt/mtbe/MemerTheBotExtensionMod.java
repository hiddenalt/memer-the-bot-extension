package ru.hiddenalt.mtbe;

import net.fabricmc.api.ModInitializer;
import ru.hiddenalt.mtbe.graphql.GraphqlClient;
import ru.hiddenalt.mtbe.gui.screen.ingame.ActionsScreen;
import ru.hiddenalt.mtbe.settings.SettingsManager;

import java.io.File;

public class MemerTheBotExtensionMod implements ModInitializer {

	static protected ActionsScreen lastOpenedActionsScreen;

	public static ActionsScreen getLastOpenedActionsScreen() {
		return lastOpenedActionsScreen;
	}

	public static void setLastOpenedActionsScreen(ActionsScreen lastOpenedActionsScreen) {
		MemerTheBotExtensionMod.lastOpenedActionsScreen = lastOpenedActionsScreen;
	}

	@Override
	public void onInitialize()  {
		this.setup();
		SettingsManager.reloadSettings();
		GraphqlClient.initialize();
	}

	public void setup() {
		String[] directories = new String[]{
				SettingsManager.getSettingsDir(),
				SettingsManager.getSchematicsDir(),
				SettingsManager.getExportedPNGFolder(),
				SettingsManager.getImportedPNGFolder()
		};

		for(String directory : directories){
			File dir = new File(directory);
			if(!dir.mkdirs() && !dir.exists()) {
				System.out.println("Error: cannot make settings directory: " + directory);
				System.exit(1);
			}
		}
	}

}
