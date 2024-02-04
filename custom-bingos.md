# Create Custom Bingos

**This is for 1.19+, for 1.18 and before, see [here](https://github.com/noeppi-noeppi/Bongo/blob/1.18/custom-bingos.md)**

Custom bingos can be added via [DataPacks](https://minecraft.gamepedia.com/Data_Pack). You can add custom game tasks (defines what shows up on the bingo card) and custom game settings (defines whether players should be invulnerable or the starting inventory).

## Settings

Your settings should be defined in a file in `data/<datapack-id>/bingo_settings/<bingo-id>.json`. That JSON file should look something like this:

```json
{
  "game": {
    "win_condition": "bongo.default",
    "invulnerable": true,
    "pvp": false,
    "friendly_fire": false,
    "time": "unlimited",
    "consume_items": false,
    "lock_task_on_death": false,
    "teleports_per_team": 0,
    "leaderboard": false,
    "lockout": false
  },
  "level": {
    "keep": [],
    "teleporter": "bongo.default",
    "teleport_radius": 10000
  },
  "equipment": {
    "inventory": [],
    "backpack": [],
    "emergency": [],
    "offhand": {
      "item": "minecraft:air",
      "count": 0
    },
    "head": {
      "item": "minecraft:air",
      "count": 0
    },
    "chest": {
      "item": "minecraft:air",
      "count": 0
    },
    "legs": {
      "item": "minecraft:air",
      "count": 0
    },
    "feet": {
      "item": "minecraft:air",
      "count": 0
    }
  },
  "server": {
    "prevent_joining_during_game": true
  }
}
```


### Win Condition

`game.win_condition` describes what a team has to achieve to win the game. There are 8 win-conditions you can use:

```
bongo.one                 Only one task
bongo.all                 All 25 tasks
bongo.rows                Only rows
bongo.columns             Only columns
bongo.diagonals           Only diagonals
bongo.rows_and_columns    Only rows and columns
bongo.default             (Default) rows, columns and diagonals
bongo.row_and_column      One row AND one column
```

### Invulnerable

`game.invulnerable` is a boolean value that, when set to true, prevents all damage to players while the game is active except damage dealt by other players. It also refills hunger.

*Default: true*

### PvP

`game.pvp` is a boolean value that, when set to false, prevents all damage to players from other players while the game is active.

*Default: false*

### Friendly Fire

When `game.friendly_fire` is false and pvp is enabled players won't be able to deal damage to their teammates.

*Default: false*

### Lock task on death

Whenever a player dies while bongo is active and `game.lock_task_on_death` is set to true, a random task will get blocked for the team of that player. A blocked task can't be completed.

*Default: false*

### Consume Items

If `game.consume_items` is set to true and someone completes a task, the item (or whatever the task is about) will get consumed. Not all task types can be consumed.

*Default: false*

### Teleports per team

`game.teleports_per_team` specifies the amount of teleports each team has using `/bingo teleport`. A negative number means infinite teleports, 0 disables this feature.

*Default: 0*

### maxTime

`game.time` is the maximum time allowed for this bingo in seconds. `"unlimited"` disables the feature (Default). If after `game.time` seconds no team has won the bingo, the team with the most completed tasks will win the bingo. If there is more than one team with equal amounts of tasks, the team that first reaches `max + 1` tasks will win where max is the maximum amount of completed tasks any team has at the time the countdown runs out.

### lockout

Setting `game.lockout` to true will lock a task for all other teams as soon as one teams completes a task. Default is `false`.

### leaderboard

Setting `game.leaderboard` to true will show a list of teams which team had which amount of tasks completed. Useful for [`winAll`](#win-condition) condition. Default is `false`.

### Equipment

The `equipment` settings control the equipment in the game. `inventory`, `head`, `chest`, `legs`, `feet` and `offhand` control the starting inventory, `backpack` the starting backpack inventory and `emergency` the items for the `/bingo emergency` command that are given to a player in exchange to three locked tasks.

All items are in [recipe format](https://minecraft.fandom.com/wiki/Recipe#JSON_format).

### Keep

The `level.keep` setting is a list of strings that determines, what Bongo should not reset, when thegame starts. Supported keys:

```
game_mode               If set, the game mode of player is not changed to survival.
equipment               If set, armor and inventory are not cleared. Also prevents filling in starting inventory and armor items.
advancements            If set, advancements are not revoked.
experience              If set, experience is not removed.
statistics              If set, statistics are kept. Otherwise they are set to 0.
time                    If set, the level time is not set to 0.
weather                 If set, the weather is not cleared.
wandering_trader_time   If set, the wandering trader spawn delay is not reset.
```

`level.keep` can also be set to the special value `"all"` which causes Bongo to keep everything. The default is the empty list `[]`, which means Bongo will clear everything.

### Teleporter

The `level.teleporter` setting specifies how bongo should teleport players when the game starts. Other mods may register their teleporters as well. Bongo has the following builtin teleporters:

```
bongo.default    (default) Auto detect best teleporter. (skyblock for skyblock worlds and standard for other worlds)
bongo.standard   Teleports the player randomly through the world.
bongo.no_tp      No teleportation.
bongo.skyblock   (Only when SkyBlockBuilder is present) Teleports each team to a separate skyblock Island.
```

### teleportRadius

`level.teleport_radius` is the distance from spawn where the players will be teleported. Please note that this is not supported by all [teleporters](#Teleporter) Default is `10000`.

### preventJoiningDuringGame

if `server.prevent_joining_during_game` is set to true, Bongo won't allow new players to join the server while the game is running. Players who were already part of the current round but left after it started will be ableto rejoin. Also operators can always join the game. This defaults to `true`.


## Tasks

Your settings should be defined in a file in `data/<datapack-id>/bingo_tasks/<bingo-id>.json`. That JSON file should look something like this:

```json
{
  "tasks": [
    {
      "type": "bongo.item",
      "value": {
        "item": "minecraft:acacia_boat",
        "count": 1
      }
    },
    {
      "type": "bongo.item",
      "value": {
        "item": "minecraft:acacia_button",
        "count": 1
      }
    }
  ]
}
```

Each entry in the tasks list describes a task. All tasks share the following properties:

```
type              The type of the task. (Items, Advancements, ...)
weight            (optional) How likely it is that this task gets into the bingo card. Higher
                  values mean more likely. Default is 1
inverted          (optional) If a task is inverted, it counts toward winning tasks by default until
                  a player completes the task causes it to be locked. Players then need to avoid a certain
                  action instead of achieving it. Default is false
custom_texture    (optional) A texture resource location that is used to display the task in the
                  bingo card instead of its default way of display. Example: minecraft:textures/apple.png
```

For a valid game tasks definition there must be at minimum 25 tasks.

## Task Types

### Empty

This task will display nothing, and it can't be completed. Its type is `bongo.empty`. This task is mainly for internal use, but it can be useful if you want to restrict a random row and a random column.

### Group

This task type is a pseudo task type (`bongo.group`) that does not describe a specific task, but an array of tasks. When this task is picked, it'll then choose one of the tasks it contains randomly. The containing tasks may also have weights.

*For groups, `weight` can also be an array of integer values. In that case each value is interpreted as a separate weight. If more than one of those separate weights are chosen during creation of the bingo, there will be more than one task chosen from that group. However, the same task will never be chosen twice from the same group. Make sure that your group has at least as many tasks as weights.*

Example:

```json
{
  "type": "bongo.group",
  "weight": 4,
  "tasks": [
    {
      "type": "bongo.item",
      "weight": 9,
      "value": {
        "item": "minecraft:golden_apple"
      }
    },
    {
      "type": "bongo.item",
      "weight": 1,
      "value": {
        "item": "minecraft:enchanted_golden_apple"
      }
    }
  ]
}
```

This task has a weight of `4`. If the task ist picked by that total weight of 4, It'll then choose either a golden apple (90% chance), or an enchanted golden apple (10% chance). 

### Item

This task has the type `bongo.item`. To complete this task a player must have an item in his inventory. The only property is `value` which contains the item in [recipe format](https://minecraft.fandom.com/wiki/Recipe#JSON_format).

Example:

```json
{
  "type": "bongo.item",
  "value": {
    "item": "minecraft:golden_apple"
  }
}
```

### Advancement

This task has the type `bongo.advancement`. To complete this task a player must gain an advancement. Properties:

```
value    The ResourceLocation of the advancement used (minecraft:nether/return_to_sender)
```

Example:

```json
{
  "type": "bongo.advancement",
  "value": "minecraft:nether/return_to_sender"
}
```

### Entity

This task has the type `bongo.entity`. To complete this task a player must kill an entity. Properties:

```
value    The ResourceLocation of the entity used (minecraft:creeper)
```

Example:

```json
{
  "type": "bongo.entity",
  "value": "minecraft:creeper"
}
```

### Biome

This task has the type `bongo.biome`. To complete this task a player must enter a biome. Properties:

```
value    The ResourceLocation of the biome used (minecraft:plains)
```

Example:

```json
{
  "type": "bongo.biome",
  "value": "minecraft:plains"
}
```

### Effect

This task has the type `bongo.effect`. To complete a player must have a potion effect active. This does not work with instant effects. Properties:

```
value    The ResourceLocation of the effect needed.
```

Example:

```json
{
  "type": "bongo.effect",
  "value": "minecraft:regeneration"
}
```

### Statistic

This task has the type `bongo.stat`. To complete it, a player must reach a value in the [statistics](https://minecraft.gamepedia.com/Statistics). When the game starts, all statistics are set to 0.

Example:

```json
{
  "type": "bongo.stat",
  "value": {
    "category": "minecraft:broken",
    "stat": "minecraft:wooden_pickaxe",
    "value": 10
  }
}
```

This would require a player to use up 10 wooden pickaxes.

Other example:

```json
{
  "type": "bongo.stat",
  "value": {
    "category": "custom",
    "stat": "minecraft:walk_one_cm",
    "value": 100000
  }
}
```

This would require a player to walk 100 blocks.

Valid categories are: `mined`, `crafted`, `used`, `broken`, `picked_up`, `dropped`, `killed`, `killed_by` and `custom`. While the first ones require an item, a block or an entity type respectively, the last category defines some special values. Those can be found [here](https://minecraft.gamepedia.com/Statistics#List_of_custom_statistic_names).

### Tag

This task has the type `bongo.tag`. To complete this task a player must have an item from an item tag in his inventory.

Example:

```json
{
  "type": "bongo.tag",
  "value": {
    "id": "minecraft:leaves",
    "count": 1
  }
}
```

## The Dump Command

Executing `/bingo dump` will create a folder called `bongo-dump` in your `.minecraft` folder or your server's folder. This will contain a file for every task type that contains a game definition with all possible elements of that task type. (Items won't have all possible nbt values though).

Because those lists can get quite big when playing in a large modpack you can also do `/bingo dump false` This will tell bongo to only dump elements that you as a player have in some way. 
  * For items, it dumps every item from your main inventory (with this you can also get NBT-Data out of the items).
  * For biomes, it dumps the biome you're currently in.
  * For entities, it dumps all entities that are within a 10x10x20 box around you,
  * For advancements, it dumps all advancements you currently have.
  * For potions, it dumps all potion effects that are currently active for you.

For statistics the dump command will only work when used as `/bingo dump false`.
