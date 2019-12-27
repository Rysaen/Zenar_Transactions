package io.github.rysaen.zenartransactions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import com.google.inject.Inject;

import io.github.rysaen.zenartransactions.commands.DepositCommand;
import io.github.rysaen.zenartransactions.commands.WithdrawCommand;
import io.github.rysaen.zenartransactions.commands.ZenarCommand;
import io.github.rysaen.zenartransactions.denominations.Denominations;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(
		id = ZenarPlugin.ID, 
		name = ZenarPlugin.NAME, 
		description = ZenarPlugin.DESCRIPTION,
		version = ZenarPlugin.VERSION,
		authors = {"Rysaen"},
		dependencies = @Dependency(
				id = "spongeapi", 
				version = ZenarPlugin.SPONGE_API_VERSION
		)
)
public class ZenarPlugin {
	// Plugin meta-info
	public final static String ID = "zenartransactions";
	public final static String NAME = "Zenar Transactions";
	public final static String DESCRIPTION = "Zenar Transactions Plugin";
	public final static String VERSION = "1.0.0";
	public final static String SPONGE_API_VERSION = "7.1.0";
		
	@Inject PluginContainer plugin;
	@Inject @ConfigDir(sharedRoot = true)
	Path configPath;
	
	@Listener
	public void onPreInit(GamePreInitializationEvent evt) {
		ZenarLogger.get().info("Pre-Initialization phase started.");
		// Loading configurations
		ConfigurationNode root = this.loadConfigurations();
		// Loading denominations
		this.loadDenominations(root);
		ZenarLogger.get().info("Pre-Initialization phase ended.");
	}
	
	@Listener
	public void onInit(GameInitializationEvent evt) {
		ZenarLogger.get().info("Initialization phase started.");
		// Commands Registration
		Sponge.getCommandManager().register(this, DepositCommand.build(), "deposita");
		Sponge.getCommandManager().register(this, WithdrawCommand.build(), "ritira");
		Sponge.getCommandManager().register(this, ZenarCommand.build(), "zenar");
		ZenarLogger.get().info("Initialization phase ended.");
	}
	
	private ConfigurationNode loadConfigurations() {
		ZenarLogger.get().info("Loading configurations ...");
		// Configuration file is created if not present
		try {
			Files.createDirectories(configPath);
			plugin.getAsset("default.conf").get().copyToFile(configPath.resolve("zenar.conf"), false, true);
		} catch(IOException e) {
			ZenarLogger.get().error("Cannot create the configuration file.");
		}
		// Instantiate the configuration loader
		ConfigurationLoader<CommentedConfigurationNode> loader =
				HoconConfigurationLoader.builder().setPath(configPath.resolve("zenar.conf")).build();
		ConfigurationNode root = null;
		try {
			root = loader.load();
			ZenarLogger.get().info("Configurations loaded.");
		} catch(IOException e) {
			ZenarLogger.get().error("Cannot instantiate root configuration node");
		}
		return root;
	}
	
	private void loadDenominations(ConfigurationNode node) {
		ZenarLogger.get().info("Loading denominations ...");
		node.getNode("main", "denominations").getChildrenList().forEach(x -> {
			ZenarLogger.get().debug("Denomination node: {}", x);
			Denominations.supply(x.getNode("name").getString(), x.getNode("value").getInt(), x.getNode("itemid").getString());
		});
		ZenarLogger.get().info("Successfully loaded {} denominations.", Denominations.count());
		return;
	}
}
