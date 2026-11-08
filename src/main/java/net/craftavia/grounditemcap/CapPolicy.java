package net.craftavia.grounditemcap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

/** Decides which items to remove. Kept free of Bukkit types so it can be unit tested. */
public final class CapPolicy {

    private CapPolicy() {
    }

    /**
     * Returns the items to remove so that the total gets back to {@code limit}: oldest first,
     * protected items are skipped (they still count toward the total).
     *
     * @param items     every counted item
     * @param limit     maximum number of items allowed
     * @param age       age of an item in ticks
     * @param protect   true for items that must never be removed
     */
    public static <T> List<T> select(final List<T> items, final int limit, final ToIntFunction<T> age,
                                     final Predicate<T> protect) {
        final int excess = items.size() - limit;
        if (excess <= 0) {
            return List.of();
        }
        final List<T> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparingInt(age).reversed());
        final List<T> result = new ArrayList<>(excess);
        for (final T item : sorted) {
            if (result.size() >= excess) {
                break;
            }
            if (!protect.test(item)) {
                result.add(item);
            }
        }
        return result;
    }
}
