package net.craftavia.grounditemcap;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Locale;

/** /grounditemcap [reload] — shows the current count or reloads the settings. */
final class CapCommand implements BasicCommand {

    static final String ADMIN = "grounditemcap.admin";

    private final GroundItemCapPlugin plugin;

    CapCommand(final GroundItemCapPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSourceStack source, final String[] args) {
        final CommandSender sender = source.getSender();
        if (!sender.hasPermission(ADMIN)) {
            Messages.send(sender, "no-permission");
            return;
        }
        if (args.length == 0) {
            Messages.send(sender, "status",
                    Component.text(plugin.countedItems().size()),
                    Component.text(plugin.settings().limit()),
                    Component.text(plugin.removedTotal()));
            return;
        }
        if (args[0].toLowerCase(Locale.ROOT).equals("reload")) {
            plugin.reload(sender);
            return;
        }
        Messages.send(sender, "usage");
    }

    @Override
    public @NotNull Collection<String> suggest(final CommandSourceStack source, final String[] args) {
        if (args.length <= 1 && source.getSender().hasPermission(ADMIN)) {
            final String prefix = args.length == 0 ? "" : args[0].toLowerCase(Locale.ROOT);
            return "reload".startsWith(prefix) ? List.of("reload") : List.of();
        }
        return List.of();
    }

    @Override
    public boolean canUse(final CommandSender sender) {
        return sender.hasPermission(ADMIN);
    }
}
