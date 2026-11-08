package net.craftavia.grounditemcap;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class GroundItemCapPlugin extends JavaPlugin {

    private static final long LOG_INTERVAL_MS = 60_000L;

    private final Messages messages = new Messages(this);
    private Settings settings;
    private ScheduledTask task;
    private long removedTotal;
    private long removedSinceLog;
    private long lastLog;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        messages.saveDefaults();
        if (!load()) {
            getSLF4JLogger().error("Could not read the configuration, using the defaults.");
        }
        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event ->
                event.registrar().register("grounditemcap", "Shows the ground item count",
                        List.of("gic"), new CapCommand(this)));
    }

    @Override
    public void onDisable() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        messages.unregister();
    }

    /** Reads config and messages and restarts the check task. Returns false if something could not be read. */
    boolean load() {
        boolean ok = true;
        try {
            reloadConfig();
            settings = Settings.read(getConfig());
        } catch (final RuntimeException ex) {
            getSLF4JLogger().error("Invalid config.yml", ex);
            if (settings == null) {
                settings = Settings.read(new YamlConfiguration());
            }
            ok = false;
        }
        try {
            messages.apply(messages.read(), settings.defaultLocale());
        } catch (final Exception ex) {
            getSLF4JLogger().error("Could not read the message files", ex);
            ok = false;
        }
        if (task != null) {
            task.cancel();
        }
        task = getServer().getGlobalRegionScheduler()
                .runAtFixedRate(this, t -> check(), settings.intervalTicks(), settings.intervalTicks());
        return ok;
    }

    void reload(final CommandSender sender) {
        if (load()) {
            Messages.send(sender, "reloaded", Component.text(settings.limit()));
        } else {
            Messages.send(sender, "reload-failed");
        }
    }

    /** Every dropped item in the counted worlds. */
    List<Item> countedItems() {
        final List<Item> items = new ArrayList<>();
        for (final World world : getServer().getWorlds()) {
            if (!settings.isIgnored(world.getName(), world.getKey().asString())) {
                items.addAll(world.getEntitiesByClass(Item.class));
            }
        }
        return items;
    }

    private void check() {
        final List<Item> items = countedItems();
        final boolean protect = settings.protectSpecial();
        final List<Item> remove = CapPolicy.select(items, settings.limit(), Item::getTicksLived,
                item -> protect && item.getItemStack().hasItemMeta());
        for (final Item item : remove) {
            item.remove();
        }
        removedTotal += remove.size();
        removedSinceLog += remove.size();

        final long now = System.currentTimeMillis();
        if (removedSinceLog > 0 && now - lastLog >= LOG_INTERVAL_MS) {
            if (settings.logRemovals()) {
                getSLF4JLogger().info("Removed {} dropped items to stay under the limit of {}.",
                        removedSinceLog, settings.limit());
            }
            removedSinceLog = 0;
            lastLog = now;
        }
    }

    Settings settings() {
        return settings;
    }

    long removedTotal() {
        return removedTotal;
    }
}
