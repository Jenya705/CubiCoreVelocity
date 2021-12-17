package com.github.jenya705.cubicore;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Plugin(
        id = "cubicore",
        name = "CubiCoreVelocity",
        version = BuildConstants.VERSION,
        authors = {"Jenya705"}
)
public class CubiCoreVelocity {

    public static final Yaml yaml = new Yaml();

    @Inject
    private Logger logger;

    @Inject
    private CommandManager commandManager;

    @Inject
    private ProxyServer server;

    @Inject
    @DataDirectory
    private Path dataDirectory;

    @Getter
    private CubiCoreConfig config;

    @Getter
    private Map<UUID, String> colors;

    private MinecraftChannelIdentifier channel;

    @Subscribe
    @SneakyThrows
    public void onProxyInitialization(ProxyInitializeEvent event) {
        dataDirectory.toFile().mkdirs();
        loadColors();
        config = new CubiCoreConfig(new File(dataDirectory.toFile(), "config.yml"));
        commandManager.register("color", new ColorCommand(this));
        channel = MinecraftChannelIdentifier.create("cubicore", "color");
        server.getChannelRegistrar().register(channel);
    }

    @Subscribe
    @SneakyThrows
    public void onProxyShutdown(ProxyShutdownEvent event) {
        saveColors();
    }

    @Subscribe
    public void onServerChange(ServerPostConnectEvent event) {
        if (colors.containsKey(event.getPlayer().getUniqueId())) {
            sendColor(event.getPlayer(), colors.get(event.getPlayer().getUniqueId()));
        }
    }

    public void sendColor(Player player, String color) {
        player
                .getCurrentServer()
                .ifPresent(serverConnection ->
                        serverConnection.sendPluginMessage(
                                channel,
                                color.getBytes()
                        )
                );
    }

    private void loadColors() throws IOException {
        File colorsFile = new File(dataDirectory.toFile(), "colors.yml");
        if (!colorsFile.exists()) {
            colorsFile.createNewFile();
            colors = new HashMap<>();
            return;
        }
        @Cleanup Reader reader = new FileReader(colorsFile);
        Map<String, String> nativeColors = yaml.load(reader);
        if (colors == null) colors = new HashMap<>();
        for (Map.Entry<String, String> nativeColor: nativeColors.entrySet()) {
            colors.put(UUID.fromString(nativeColor.getKey()), nativeColor.getValue());
        }
    }

    private void saveColors() throws IOException {
        File colorsFile = new File(dataDirectory.toFile(), "colors.yml");
        if (!colorsFile.exists()) colorsFile.createNewFile();
        Map<String, String> nativeColors = new HashMap<>();
        for (Map.Entry<UUID, String> entry: colors.entrySet()) {
            nativeColors.put(entry.getKey().toString(), entry.getValue());
        }
        @Cleanup Writer writer = new FileWriter(colorsFile);
        yaml.dump(nativeColors, writer);
    }
}

