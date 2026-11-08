package net.craftavia.grounditemcap;

import net.kyori.adventure.translation.Translator;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/** Values read from config.yml. */
public record Settings(int limit, long intervalTicks, boolean protectSpecial, Set<String> ignoredWorlds,
                       boolean logRemovals, Locale defaultLocale) {

    public static Settings read(final FileConfiguration config) {
        final int limit = Math.max(1, config.getInt("limit", 1000));
        final long intervalTicks = Math.max(1, config.getInt("check-interval-seconds", 5)) * 20L;
        final Set<String> ignored = config.getStringList("ignored-worlds").stream()
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        final Locale locale = Translator.parseLocale(config.getString("default-language", "en"));
        return new Settings(limit, intervalTicks, config.getBoolean("protect-special-items", true), ignored,
                config.getBoolean("log-removals", true), locale == null ? Locale.ENGLISH : locale);
    }

    /** World names are compared case-insensitively; the namespaced key (minecraft:overworld) also matches. */
    public boolean isIgnored(final String worldName, final String worldKey) {
        return ignoredWorlds.contains(worldName.toLowerCase(Locale.ROOT))
                || ignoredWorlds.contains(worldKey.toLowerCase(Locale.ROOT));
    }
}
