package net.craftavia.grounditemcap;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CapPolicyTest {

    record Drop(String name, int age, boolean special) {
    }

    private static List<Drop> select(final List<Drop> drops, final int limit) {
        return CapPolicy.select(drops, limit, Drop::age, Drop::special);
    }

    @Test
    void underLimitRemovesNothing() {
        final List<Drop> drops = List.of(new Drop("a", 10, false), new Drop("b", 20, false));
        assertTrue(select(drops, 2).isEmpty());
        assertTrue(select(drops, 5).isEmpty());
    }

    @Test
    void removesOldestFirst() {
        final List<Drop> drops = List.of(new Drop("new", 5, false), new Drop("old", 500, false),
                new Drop("mid", 50, false));
        assertEquals(List.of("old", "mid"), select(drops, 1).stream().map(Drop::name).toList());
    }

    @Test
    void skipsProtectedItems() {
        final List<Drop> drops = List.of(new Drop("sword", 900, true), new Drop("dirt", 100, false),
                new Drop("stone", 10, false));
        assertEquals(List.of("dirt"), select(drops, 2).stream().map(Drop::name).toList());
    }

    @Test
    void cannotGoBelowProtectedCount() {
        final List<Drop> drops = List.of(new Drop("a", 1, true), new Drop("b", 2, true), new Drop("c", 3, false));
        assertEquals(List.of("c"), select(drops, 0).stream().map(Drop::name).toList());
    }
}
