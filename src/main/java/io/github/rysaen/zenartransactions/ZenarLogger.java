package io.github.rysaen.zenartransactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZenarLogger {
	private ZenarLogger() {}
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ZenarPlugin.ID);
	
	public static Logger get() {
		return LOGGER;
	}
}
