from sys import argv


blocks = [
    ("block_leather", "item_block_leather"),
    ("block_straw", "item_block_straw"),
    ("block_bookcase", "item_bookcase"),

    "carved_oak_1",
    "carved_oak_2",
    "carved_oak_3",
    "carved_oak_4",
    "carved_oak_5",
    "carved_oak_6",
    "carved_oak_7",
    "carved_oak_8",
    "carved_birch_1",
    "carved_birch_2",
    "carved_birch_3",
    "carved_birch_4",
    "carved_birch_5",
    "carved_birch_6",
    "carved_birch_7",
    "carved_birch_8",

    "black_cushion",
    "blue_cushion",
    "brown_cushion",
    "cyan_cushion",
    "gray_cushion",
    "green_cushion",
    "light_blue_cushion",
    "light_gray_cushion",
    "lime_cushion",
    "magenta_cushion",
    "orange_cushion",
    "pink_cushion",
    "purple_cushion",
    "red_cushion",
    "white_cushion",
    "yellow_cushion",
]

mod_id = argv[1]
file_dir = f"src/main/resources/data/{mod_id}/loot_tables/blocks/"
json_paths = [file_dir + block[0] + ".json" for block in blocks if isinstance(block, tuple)]
json_paths.extend([file_dir + block + ".json" for block in blocks if isinstance(block, str)])

for i, j in enumerate(json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "type": "minecraft:block",
  "pools": [
    {{
      "rolls": 1,
      "entries": [
        {{
          "type": "minecraft:item",
          "name": "{}:{}"
        }}
      ],
      "conditions": [
        {{
          "condition": "minecraft:survives_explosion"
        }}
      ]
    }}
  ]
}}""".format(mod_id, blocks[i] if isinstance(blocks[i], str) else blocks[i][1]))
