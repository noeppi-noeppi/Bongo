# Create Custom Bingos

Custom bingos can be added via [DataPacks](https://minecraft.gamepedia.com/Data_Pack). A custom bingo (also called a game definition) is a json file that goes into `data/<datapack-id>/bingos/<bingo-id>.json`. That JSON file should look like this:

```json
{
  "winCondition": "bongo.default",
  "tasks": [
    {
      "type": "bongo.item",
      "weight": 20,
      "id": "minecraft:golden_apple"
    }
  ]
}
```

`winCondition` describes what a team has to achive to win the game. There are 8 win conditions you can use:

```
bongo.one                 Only one task
bongo.all                 All 25 tasks
bongo.rows                Only rows
bongo.columns             Only colums
bongo.diagonals           Only diagonals
bongo.rows_and_columns    Only rows and columns
bongo.default             (Default) rows, columns and diagonals
bongo.row_and_column      One row AND one column
```

`tasks` is a list of tasks. A task is an object. All tasks share the following properties:

```
type      The type of the task. (Items, Advancements, ...)
weight    (optional) How likely it is that this task gets into the bingo card. Higher
          values mean more likely. Default is 1
```

For a valid game definition there must be at minimum 25 tasks.

## Task Types

### Empty

This task will display nothing and it can not be completed. It's type is `bongo.empty`. This task is mainly for internal use but it can be useful if you want to restrict a random row and a random column.

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