package btw.community.example;

import api.AddonHandler;
import api.BTWAddon;
import net.minecraft.src.KeyBinding;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.util.Properties;

public class HealthHudAddon extends BTWAddon {
	private static HealthHudAddon instance;

	public static KeyBinding toggleHealthBarKey = new KeyBinding("Toggle Health Bar", Keyboard.KEY_H);
	public static boolean healthBarEnabled = true;

	private static final String CONFIG_FILE = "config/health-hud-ce.properties";

	public HealthHudAddon() {
		super();
		// BTW CE 3.0.0 config system bug workaround: delete .old file before AddonHandler crashes trying to rename it
		File oldFile = new File(CONFIG_FILE + ".old");
		if (oldFile.exists()) {
			oldFile.delete();
		}
	}

	public static HealthHudAddon getInstance() {
		return instance;
	}

	@Override
	public void initialize() {
		instance = this;
		loadConfig();
		AddonHandler.logMessage(this.getName() + " v" + this.getVersionString() + " initialized");
	}

	public static void loadConfig() {
		File configFile = new File(CONFIG_FILE);
		if (!configFile.exists()) {
			System.out.println("[Health HUD CE] Config file not found, using defaults");
			saveConfig();
			return;
		}

		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(configFile)) {
			props.load(fis);

			if (props.containsKey("healthBarEnabled")) {
				healthBarEnabled = Boolean.parseBoolean(props.getProperty("healthBarEnabled"));
			}
			if (props.containsKey("toggleKey")) {
				toggleHealthBarKey.keyCode = Integer.parseInt(props.getProperty("toggleKey"));
			}
			System.out.println("[Health HUD CE] Config loaded");
		} catch (IOException | NumberFormatException e) {
			System.err.println("[Health HUD CE] Failed to load config: " + e.getMessage());
		}
	}

	public static void saveConfig() {
		File configDir = new File("config");
		if (!configDir.exists()) {
			configDir.mkdirs();
		}

		File oldFile = new File(CONFIG_FILE + ".old");
		if (oldFile.exists()) {
			oldFile.delete();
		}

		Properties props = new Properties();
		props.setProperty("healthBarEnabled", String.valueOf(healthBarEnabled));
		props.setProperty("toggleKey", String.valueOf(toggleHealthBarKey.keyCode));

		try (FileOutputStream fos = new FileOutputStream(new File(CONFIG_FILE))) {
			props.store(fos, "Health HUD CE Configuration");
			System.out.println("[Health HUD CE] Config saved");
		} catch (IOException e) {
			System.err.println("[Health HUD CE] Failed to save config: " + e.getMessage());
		}
	}
}
