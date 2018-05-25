package fr.aquillet.kiwi.ui.configuration;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import io.reactivex.Completable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalConfiguration {

	private static final String SYS_PROPERTY_CONFIG_FILE = "CONFIGURATION_FILE";
	private static final String PROD_CONFIG_FILE_PATH = "../etc/kiwi.properties";
	private static final String DEFAULT_CONFIG_FILE_PATH = "kiwi.properties";

	private static final Properties CONFIGURATION = new Properties();

	private GlobalConfiguration() {
		// utility class
	}

	public static Completable init() {
		return Completable.fromAction(() -> {
			File configurationFile = new File(System.getProperty(SYS_PROPERTY_CONFIG_FILE, PROD_CONFIG_FILE_PATH));
			if (!configurationFile.exists()) {
				log.warn("No configuration file found. Using default location. (location: {})",
						DEFAULT_CONFIG_FILE_PATH);
				configurationFile = new File(DEFAULT_CONFIG_FILE_PATH);
			}
			log.info("Loading configuration file {}", configurationFile.getAbsolutePath());
			try (FileInputStream fis = new FileInputStream(configurationFile)) {
				CONFIGURATION.load(fis);
				log.info("Application configuration: ");
				CONFIGURATION.entrySet().forEach(e -> log.info("  {}: {}", e.getKey(), e.getValue()));
			}
		});
	}

	public static File getFileValue(String key) {
		return new File(CONFIGURATION.getProperty(key));
	}

	public static int getIntValue(String key) {
		return Integer.parseInt(CONFIGURATION.getProperty(key));
	}



}
