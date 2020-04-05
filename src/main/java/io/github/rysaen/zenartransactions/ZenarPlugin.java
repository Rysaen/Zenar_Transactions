package io.github.rysaen.zenartransactions;

import java.util.LinkedList;
import java.util.List;

import io.github.rysaen.zenartransactions.config.ConfigurationService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.inject.Inject;

import io.github.rysaen.zenartransactions.commands.DepositCommand;
import io.github.rysaen.zenartransactions.commands.WithdrawCommand;
import io.github.rysaen.zenartransactions.commands.ZenarCommand;
import io.github.rysaen.zenartransactions.denomination.Denominations;
import io.github.rysaen.zenartransactions.listeners.ZenarEventsHandler;
import io.github.rysaen.zenartransactions.util.ZenarRecipes;
import ninja.leaping.configurate.ConfigurationNode;

@Plugin(
		id = PluginInfo.ID,
		name = PluginInfo.NAME,
		description = PluginInfo.DESCRIPTION,
		version = PluginInfo.VERSION,
		dependencies = @Dependency(id = "spongeapi", version = PluginInfo.SPONGE_API_VERSION)
)
public class ZenarPlugin {

	@Inject PluginContainer pluginContainer;
	private ConfigurationService configService;

	@Listener
	public void onPreInit(GamePreInitializationEvent evt) {
		configService = new ConfigurationService();
		configService.load();
	}

	@Listener
	public void onRegister(GameRegistryEvent.Register<CraftingRecipe> evt) {
		ZenarRecipes.processRecipes(pluginContainer).forEach(evt::register);
	}

	@Listener
	public void onInit(GameInitializationEvent evt) {
		ZenarLogger.get().info("Initialization phase started.");
		// Commands Registration
		Sponge.getCommandManager().register(this, DepositCommand.build(), "deposita");
		Sponge.getCommandManager().register(this, WithdrawCommand.build(), "ritira");
		Sponge.getCommandManager().register(this, ZenarCommand.build(), "zenar");
		// Register listeners
		Sponge.getEventManager().registerListeners(this.pluginContainer, new ZenarEventsHandler());
		ZenarLogger.get().info("Initialization phase ended.");
	}

	@Listener
	public void onReload(GameReloadEvent evt) {
		configService.reload();
	}
}
