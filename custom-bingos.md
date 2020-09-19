# Create Custom Bingos

Custom bingos can be added via [DataPacks](https://minecraft.gamepedia.com/Data_Pack). A custom bingo (also called a game definition) is a json file that goes into `data/<datapack-id>/bingos/<bingo-id>.json`. That JSON file should look like this:

```json
{
  "settings": {
    "winCondition": "bongo.default",
    "invulnerable": true,
    "pvp": false,
    "friendlyFire": false,
    "lockTaskOnDeath": false,
    "consumeItems": false
  },
  "tasks": [
    {
      "type": "bongo.item",
      "weight": 20,
      "id": "minecraft:golden_apple"
    }
  ]
}
```

## Settings

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

## Tasks

`tasks` is a list of tasks. A task is an object. All tasks share the following properties:

```
type      The type of the task. (Items, Advancements, ...)
weight    (optional) How likely it is that this task gets into the bingo card. Higher
          values mean more likely. Default is 1
```

For a valid game definition there must be at minimum 25 tasks.

## Task Types

### Empty

This task will display nothing, and it can't be completed. Its type is `bongo.empty`. This task is mainly for internal use, but it can be useful if you want to restrict a random row and a random column.

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