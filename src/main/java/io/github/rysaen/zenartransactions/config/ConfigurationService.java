package io.github.rysaen.zenartransactions.config;

import io.github.rysaen.zenartransactions.PluginInfo;
import io.github.rysaen.zenartransactions.ZenarLogger;
import io.github.rysaen.zenartransactions.denomination.Denomination;
import io.github.rysaen.zenartransactions.denomination.Denominations;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.asset.Asset;
import org.spongepowered.api.config.ConfigRoot;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.util.TypeTokens;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigurationService {

    static {
        final PluginContainer plugin = Sponge.getPluginManager().getPlugin(PluginInfo.ID).get();
        final ConfigRoot configRoot = Sponge.getConfigManager().getSharedConfig(plugin);

        MAIN_CONF = configRoot.getConfigPath();
    }

    public static final Path MAIN_CONF;
    private static final String DEFAULT_CONF_NAME = "default.conf";
    private final ConfigurationLoader<CommentedConfigurationNode> loader;

    public ConfigurationService() {

        try {
            Files.createDirectories(ConfigurationService.MAIN_CONF);
            PluginContainer plugin = Sponge.getPluginManager().getPlugin(PluginInfo.ID).get();
            Asset defaultConf = plugin.getAsset(ConfigurationService.DEFAULT_CONF_NAME).get();
            defaultConf.copyToFile(ConfigurationService.MAIN_CONF, false);
        } catch (IOException e) {
            ZenarLogger.get().error("Failed initialize configurations.");
            throw new RuntimeException(e);
        }

        this.loader = HoconConfigurationLoader.builder().setPath(ConfigurationService.MAIN_CONF).build();
    }

    public void load() {

        final ConfigurationNode root;

        ZenarLogger.get().info("Loading denominations ...");

        try {
            root = loader.load();
        } catch (IOException e) {
            ZenarLogger.get().error("Couldn't configurations file.");
            throw new RuntimeException(e);
        }

        try {
            for (ConfigurationNode n : root.getNode("main", "denominations").getChildrenList()) {
                final Denomination d = new Denomination(
                        n.getNode("name").getString(),
                        n.getNode("value").getInt(),
                        n.getNode("item-id").getString(),
                        n.getNode("display-name").getString(),
                        n.getNode("lore").getList(TypeTokens.STRING_TOKEN)
                );
                Denominations.supply(d);
            }
        } catch (ObjectMappingException e) {
            ZenarLogger.get().error("Failed to map denominations.");
            throw new RuntimeException();
        }

        ZenarLogger.get().info("Successfully loaded {} denominations.", Denominations.count());
        return;
    }

    public void reload() {
        // TODO
    }
}
