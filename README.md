# GroundItemCap

**A hard cap on dropped items for Paper servers: when there are too many items on the ground, the oldest ones go first.**

Farms, broken chests and mob grinders can leave thousands of item entities lying around. Paper can merge
and despawn items, but it has no server-wide limit. GroundItemCap counts the dropped items every few seconds
and, when the total is over your limit, removes the oldest ones until it is back under it. Fresh drops and
items with custom data are left alone.

**Download:** [Hangar](https://hangar.papermc.io/mrpunky/GroundItemCap) · [GitHub Releases](https://github.com/craftavia/GroundItemCap/releases)

## Features

- **One server-wide limit** across all worlds (or all worlds except the ones you ignore).
- **Oldest first** – the items that have been on the ground the longest are removed first, so a player's
  fresh drops are the last to go.
- **Protects special items** – renamed, enchanted, damaged items and filled containers are never removed
  (on by default).
- **Quiet** – no chat broadcasts; one console line per minute at most when items were removed.
- `/grounditemcap` shows the live count, the limit and how many items were removed since startup.
- Per-player language (English and Turkish bundled), live reload, no dependencies, no database,
  no network calls, no metrics.

## Requirements

| | |
|---|---|
| Server | Paper or a fork such as Purpur, **1.21.4 – 1.21.x and 26.x** |
| Java | 21 or newer |

Folia is not supported.

## Installation

1. Download `GroundItemCap-x.y.z.jar` from [Releases](https://github.com/craftavia/GroundItemCap/releases).
2. Put it in your `plugins/` folder and start the server.
3. Adjust `plugins/GroundItemCap/config.yml` and run `/grounditemcap reload`.

## Commands and permissions

| Command | Description | Permission | Default |
|---|---|---|---|
| `/grounditemcap` (`/gic`) | Items on the ground, limit, removed since startup | `grounditemcap.admin` | op |
| `/grounditemcap reload` | Reloads config and messages | `grounditemcap.admin` | op |

## Configuration

```yaml
limit: 1000                  # max dropped items on the ground, all counted worlds together
check-interval-seconds: 5
protect-special-items: true  # never remove items with custom data (they still count)
ignored-worlds: []           # not counted, never removed
log-removals: true
default-language: en
```

Messages are in `messages_en.properties` and `messages_tr.properties` (MiniMessage). Players see the file
matching their client language; add a language by dropping `messages_<code>.properties` in the plugin folder.

### Notes

- Only item entities in **loaded chunks** are counted; items in unloaded chunks are untouched.
- Removed items are gone for good. Pick a limit well above what your farms and players normally leave
  on the ground. If players report missing drops, raise the limit first.
- Protected items count toward the limit but are never removed, so the count can stay above the limit
  if most items on the ground are special.
- Tested on Paper 1.21.4 and 26.2. The plugin it comes from runs on Craftavia's Skyblock server
  (Purpur 26.2).

## Building

```bash
./gradlew build
```

The jar is written to `build/libs/`.

Made by the Craftavia team, used on play.craftavia.net, https://craftavia.net

## Türkçe

**İndir:** [Hangar](https://hangar.papermc.io/mrpunky/GroundItemCap) · [GitHub Releases](https://github.com/craftavia/GroundItemCap/releases)

GroundItemCap, sunucudaki **yerde duran eşya sayısına üst sınır** koyan ücretsiz bir Paper eklentisidir.
Yerdeki toplam eşya limiti geçince en eski eşyalardan başlayarak siler. İsimli, büyülü, hasarlı eşyalara ve
içi dolu kutulara dokunmaz. Oyun dili Türkçe olan oyuncu mesajları Türkçe görür.

Kurulum: jar'ı `plugins/` klasörüne atın, sunucuyu açın, `config.yml` içindeki `limit` değerini ayarlayın ve
`/grounditemcap reload` yazın. `/grounditemcap` (kısaca `/gic`) yerdeki eşya sayısını gösterir.

Craftavia ekibi tarafından geliştirildi, play.craftavia.net sunucusunda kullanılıyor: https://craftavia.net

## License

[MIT](LICENSE) © Craftavia
