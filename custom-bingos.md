# Create Custom Bingos

Custom bingos can be added via [DataPacks](https://minecraft.gamepedia.com/Data_Pack). You can add custom game tasks (defines what shows up on the bingo card) and custom game settings (defines whether players should be invulnerable or the starting inventory).

## Settings

Your settings should be defined in a file in `data/<datapack-id>/bingo_settings/<bingo-id>.json`. That JSON file should look something like this:

```json
{
  "invulnerable": false,
  "winCondition": "bongo.default",
  "friendlyFire": false,
  "lockTaskOnDeath": true,
  "teleportsPerTeam": 1,
  "consumeItems": false,
  "pvp": true,
  "startingInventory": [
    {
      "id": "minecraft:leather_helmet",
      "Slot": "head"
    }
  ],
  "backpackInventory": [
    {
      "id": "minecraft:apple"
    }
  ],
  "emergencyItems": [
    {
      "id": "minecraft:lava_bucket"
    }
  ],
  "teleporter": "bongo.no_tp",
  "teleportRadius": 500,
  "maxTime": -1,
  "lockout": false,
  "leaderboard": false
}
```


### Win Condition

`winCondition` describes what a team has to achieve to win the game. There are 8 win-conditions you can use:

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

`invulnerable` is a boolean value that, when set to true, prevents all damage to players while the game is active except damage dealt by other players. It also refills hunger.

*Default: true*

### PvP

`pvp` is a boolean value that, when set to false, prevents all damage to players from other players while the game is active.

*Default: false*

### Friendly Fire

When `friendlyFire` is false and pvp is enabled players won't be able to deal damage to their teammates.

*Default: false*

### Lock task on death

Whenever a player dies while bongo is active and `lockTaskOnDeath` is set to true, a random task will get blocked for the team of that player. A blocked task can't be completed.

*Default: false*

### Consume Items

If `consumeItems` is set to true and someone completes a task, the item (or whatever the task is about) will get consumed. Not all task types can be consumed.

*Default: false*

### Teleports per team

`teleportsPerTeam` specifies the amount of teleports each team has using `/bingo teleport`. A negative number means infinite teleports, 0 disables this feature.

*Default: 0*

### Staring Inventory

`startingInventory` is a list of items that each player gets when the game starts. Each item can have the following properties:

```
id       The ResourceLocation of the item used (minecraft:golden_apple)
Count    (optional) How many items are required to complete this task
tag      (optional) NBT-Data the item must have to be accepted
Slot     (optional) In which slot the item should be placed.
```

Valid slots are:

```
mainhand   (default) The item is placed in the players main inventory. Can be used at most 36 times.
offhand    The item is placed in the players offhand slot. Can be used only once.
head       The item is placed in the players head slot. Can be used only once.
chest      The item is placed in the players chest slot. Can be used only once.
legs       The item is placed in the players legs slot. Can be used only once.
feet       The item is placed in the players feet slot. Can be used only once.
```

### Backpack Inventory

`backpackInventory` is a list of items that each team gets in the backpack when the game starts. Unlike `startingInventory` here not each player but each team gets the items. It works the same as `startingInventory` with the exception that `Slot` is not available.

### Emergency Items

`emergencyItems` is a list of items that each team can get once per game with `/bingo emergency`. However on redeeming, 3 random tasks will get locked. It works the same as `startingInventory` with the exception that `Slot` is not available. An empty list will disable the feature.

### Teleporter

The teleporter specifies how bongo should teleport players when the game starts. Other mods may register their teleporters as well. Bongo has the following builtin teleporters:

```
bongo.default    (default) Auto detect best teleporter. (skyblock for skyblock worlds and standard for other worlds)
bongo.standard   Teleports the player randomly through the world.
bongo.no_tp      No teleportation.
bongo.skyblock   (Only when SkyBlockBuilder is present) Teleports each team to a separate skyblock Island.
```

### teleportRadius

`teleportRadius` is the distance from spawn where the players will be teleported. Please note that this is not supported by all [teleporters](#Teleporter) Default is `10000`.

### maxTime

`maxTime` is the maximum time allowed for this bingo. `-1` disables the feature (Default). If after `maxTime` seconds no team has won the bingo, the team with the most completed tasks will win the bingo. If there is more than one team with equal amounts of tasks, the team that first reaches `max + 1` tasks will win where max is the maximum amount of completed tasks any team has at the time the countdown runs out.

### lockout

Setting `lockout` to true will lock a task for all other teams as soon as one teams completes a task. Default is `false`.

### leaderboard

Setting `leaderboard` to true will show a list of teams which team had which amount of tasks completed. Useful for [`winAll`](#win-condition) condition. Default is `false`.

## Tasks

Your settings should be defined in a file in `data/<datapack-id>/bingo_tasks/<bingo-id>.json`. That JSON file should look something like this:

```json
{
  "tasks": [
    {
      "type": "bongo.item",
      "weight": 20,
      "id": "minecraft:acacia_boat"
    },
    {
      "type": "bongo.item",
      "weight": 20,
      "id": "minecraft:acacia_button"
    }
  ]
}
```

Each entry in the tasks list describes a task. All tasks share the following properties:

```
type      The type of the task. (Items, Advancements, ...)
weight    (optional) How likely it is that this task gets into the bingo card. Higher
          values mean more likely. Default is 1
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
      "id": "minecraft:golden_apple"
    },
    {
      "type": "bongo.item",
      "weight": 1,
      "id": "minecraft:enchanted_golden_apple"
    }
  ]
}
```

This task has a weight of `4`. If the task ist picked by that total weight of 4, It'll then choose either a golden apple (90% chance), or an enchanted golden apple (10% chance). 

### Item

This task has the type `bongo.item`. To complete this task a player must have an item in his inventory. Properties:

```
id       The ResourceLocation of the item used (minecraft:golden_apple)
Count    (optional) How many items are required to complete this task
tag      (optional) NBT-Data the item must have to be accepted
```
Example:

```json
{
  "type": "bongo.item",
  "id": "minecraft:golden_apple"
}
```

### Advancement

This task has the type `bongo.advancement`. To complete this task a player must gain an advancement. Properties:

```
advancement    The ResourceLocation of the advancement used (minecraft:nether/return_to_sender)
```

Example:

```json
{
  "type": "bongo.advancement",
  "advancement": "minecraft:nether/return_to_sender"
}
```

### Entity

This task has the type `bongo.entity`. To complete this task a player must kill an entity. Properties:

```
entity    The ResourceLocation of the entity used (minecraft:creeper)
```

Example:

```json
{
  "type": "bongo.entity",
  "entity": "minecraft:creeper"
}
```

### Biome

This task has the type `bongo.biome`. To complete this task a player must enter a biome. Properties:

```
biome    The ResourceLocation of the biome used (minecraft:plains)
```

Example:

```json
{
  "type": "bongo.biome",
  "biome": "minecraft:plains"
}
```

### Potion

This task has the type `bongo.potion`. To complete a player must have a potion effect active. Sadly this does not work with instant effects.

Example:

```json
{
  "type": "bongo.potion",
  "potion": "minecraft:regeneration"
}
```

### Statistic

This task has the type `bongo.stat`. To complete it, a player must reach a value in the [statistics](https://minecraft.gamepedia.com/Statistics). When the game starts, all statistics are set to 0.

Example:

```json
{
  "type": "bongo.stat",
  "category": "broken",
  "stat": "minecraft:wooden_pickaxe",
  "value": 10
}
```

This would require a player to use up 10 wooden pickaxes.

Other example:

```json
{
  "type": "bongo.stat",
  "category": "custom",
  "stat": "minecraft:walk_one_cm",
  "value": 100000
}
```

This would require a player to walk 100 blocks.

Valid categories are: `mined`, `crafted`, `used`, `broken`, `picked_up`, `dropped`, `killed`, `killed_by` and `custom`. While the first ones require an item, a block or an entity type respectively, the last category defines some special values. Those can be found [here](https://minecraft.gamepedia.com/Statistics#List_of_custom_statistic_names).

### Tag

This task has the type `bongo.tag`. To complete this task a player must have an item from an item tag in his inventory. Properties:

```
tag      The ResourceLocation of the item tag used (minecraft:leaves)
Count    (optional) How many items are required to complete this task
```
Example:

```json
{
  "type": "bongo.item",
  "tag": "minecraft:leaves"
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