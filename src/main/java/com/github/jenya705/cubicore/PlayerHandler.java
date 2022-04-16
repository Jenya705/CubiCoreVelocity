package com.github.jenya705.cubicore;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.Objects;

/**
 * @author Jenya705
 */
@RequiredArgsConstructor
public class PlayerHandler {

    private final CubiCoreVelocity plugin;

    @Subscribe
    public void join(ServerConnectedEvent event) {
        event.getPreviousServer()
                .ifPresent(it -> disconnectMessage(it, event.getPlayer()));
        connectMessage(event.getServer(), event.getPlayer());
    }

    @Subscribe
    public void disconnect(DisconnectEvent event) {
        event.getPlayer()
                .getCurrentServer()
                .ifPresent(it -> disconnectMessage(it.getServer(), event.getPlayer()));
    }

    private void connectMessage(RegisteredServer server, Player player) {
        TextColor color = Objects.requireNonNullElse(
                plugin.getColors().get(player.getUniqueId()),
                NamedTextColor.GRAY
        );
        Component message = Component
                .translatable("multiplayer.player.joined")
                .args(Component
                        .empty()
                        .color(color)
                        .append(Component
                                .text("> ")
                                .decorate(TextDecoration.BOLD)
                        )
                        .append(buildPlayerComponent(player))
                );
        server.getPlayersConnected()
                .forEach(it -> it.sendMessage(message));
        player.sendMessage(message);
    }

    private void disconnectMessage(RegisteredServer server, Player player) {
        TextColor color = Objects.requireNonNullElse(
                plugin.getColors().get(player.getUniqueId()),
                NamedTextColor.GRAY
        );
        server.getPlayersConnected()
                .forEach(it -> it
                        .sendMessage(Component
                                .translatable("multiplayer.player.left")
                                .args(Component
                                        .empty()
                                        .color(color)
                                        .append(Component
                                                .text("< ")
                                                .decorate(TextDecoration.BOLD)
                                        )
                                        .append(buildPlayerComponent(player))
                                ))
                );
    }

    private static Component buildPlayerComponent(Player player) {
        return Component.text(player.getUsername())
                .hoverEvent(player)
                .clickEvent(ClickEvent.clickEvent(
                        ClickEvent.Action.SUGGEST_COMMAND, "/tell " + player.getUsername()
                ));
    }

}
