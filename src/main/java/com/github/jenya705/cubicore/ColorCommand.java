package com.github.jenya705.cubicore;

import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author Jenya705
 */
@AllArgsConstructor
public class ColorCommand implements SimpleCommand {

    private static final List<String> colors = new ArrayList<>(NamedTextColor.NAMES.keys());
    private static final Pattern pattern = Pattern.compile("#[0-9a-fA-F]{6}");

    private final CubiCoreVelocity plugin;

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (!(invocation.source() instanceof Player)) {
            invocation.source().sendMessage(Component
                    .text("This command only for players")
                    .color(NamedTextColor.RED)
            );
        }
        Player player = (Player) invocation.source();
        if (args.length == 0) {
            player.sendMessage(Component
                    .text(plugin.getConfig().getColorNotChosen())
                    .color(NamedTextColor.RED)
            );
            return;
        }
        String chosenColor = args[0];
        NamedTextColor namedTextColor = NamedTextColor.NAMES.value(chosenColor.toLowerCase(Locale.ROOT));
        if (namedTextColor == null) {
            // not in list. checking for hex color
            if (chosenColor.length() == 7 && pattern.matcher(chosenColor).matches()) {
                TextColor color = TextColor.fromHexString(chosenColor);
                player.sendMessage(Component
                        .text(plugin.getConfig().getColorSuccess())
                        .color(color)
                );
                plugin.getColors().put(player.getUniqueId(), color);
                plugin.sendColor(player, color);
            }
            else {
                player.sendMessage(Component
                        .text(plugin.getConfig().getColorNotValid())
                        .color(NamedTextColor.RED)
                );
            }
        }
        else {
            player.sendMessage(Component
                    .text(plugin.getConfig().getColorSuccess())
                    .color(namedTextColor)
            );
            String hexString = namedTextColor.asHexString().substring(1);
            plugin.getColors().put(player.getUniqueId(), namedTextColor);
            plugin.sendColor(player, namedTextColor);
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("cubicore.color");
    }

    @Override
    public List<String> suggest(Invocation invocation) {
        if (invocation.arguments().length <= 1) {
            return colors;
        }
        return Collections.emptyList();
    }
}
