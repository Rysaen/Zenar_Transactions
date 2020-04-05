package io.github.rysaen.zenartransactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZenarLogger {
	private static final Logger LOGGER = LoggerFactory.getLogger(PluginInfo.ID);

	public static Logger get() {
		return LOGGER;
	}

	private ZenarLogger() {}
}
