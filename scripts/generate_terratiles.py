flatten = lambda l: [item for sublist in l for item in sublist]

colors = [
    "black",
    "blue",
    "brown",
    "cyan",
    "gray",
    "green",
    "light_blue",
    "light_gray",
    "lime",
    "magenta",
    "orange",
    "pink",
    "purple",
    "red",
    "white",
    "yellow",
]
color_names_en = {
    "black":      "Black",
    "blue":       "Blue",
    "brown":      "Brown",
    "cyan":       "Cyan",
    "gray":       "Gray",
    "green":      "Green",
    "light_blue": "Light blue",
    "light_gray": "Light gray",
    "lime":       "Lime",
    "magenta":    "Magenta",
    "orange":     "Orange",
    "pink":       "Pink",
    "purple":     "Purple",
    "red":        "Red",
    "white":      "White",
    "yellow":     "Yellow"
}
color_names_tr = {
    "black":      "Siyah",
    "blue":       "Mavi",
    "brown":      "Kahverengi",
    "cyan":       "Turkuaz",
    "gray":       "Gri",
    "green":      "Yeşil",
    "light_blue": "Açık mavi",
    "light_gray": "Açık gri",
    "lime":       "Sarımsı yeşil",
    "magenta":    "Majenta",
    "orange":     "Turuncu",
    "pink":       "Pembe",
    "purple":     "Mor",
    "red":        "Kırmızı",
    "white":      "Beyaz",
    "yellow":     "Sarı"
}

fulls = ["{}_terratile".format(c) for c in colors]
slabs = ["{}_terratile_slab".format(c) for c in colors]
stairs = ["{}_terratile_stairs".format(c) for c in colors]
blocks = fulls + slabs + stairs

# Block Models

file_dir = f"../XercaMod/src/main/resources/assets/xercamod/models/block/terracotta_tiles/"

# texture_bases = [c + "_terra_cotta_tiles" for c in colors]
full_json_paths = [file_dir + block + ".json" for block in fulls]
slab_json_paths = [file_dir + block + ".json" for block in slabs]
slab_top_json_paths = [file_dir + block + "_top.json" for block in slabs]
stairs_json_paths = [file_dir + block + ".json" for block in stairs]
stairs_inner_json_paths = [file_dir + block + "_inner.json" for block in stairs]
stairs_outer_json_paths = [file_dir + block + "_outer.json" for block in stairs]
stairs_inverted_inner_json_paths = [file_dir + block + "_inverted_inner.json" for block in stairs]
stairs_inverted_outer_json_paths = [file_dir + block + "_inverted_outer.json" for block in stairs]
stairs_inverted_json_paths = [file_dir + block + "_inverted.json" for block in stairs]

for i, j in enumerate(full_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/terratile",
  "textures": {{
    "top": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top",
    "front": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_front",
    "side": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_side"
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(slab_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/terratile_slab",
  "textures": {{
    "top": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top",
    "front": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_front",
    "side": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_side"
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(slab_top_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/terratile_slab_top",
  "textures": {{
    "top": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top",
    "front": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_front",
    "side": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_side"
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(stairs_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/terratile_stairs",
  "textures": {{
    "bottom": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top",
    "top": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top",
    "side": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_side",
    "front": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_front"
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(stairs_inner_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/terratile_inner_stairs",
  "textures": {{
    "bottom": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top_inner",
    "top": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top_inner",
    "side": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_side",
    "front": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_front"
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(stairs_outer_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/terratile_outer_stairs",
  "textures": {{
    "bottom": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top_outer",
    "top": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top_outer",
    "side": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_side",
    "front": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_front"
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(stairs_inverted_inner_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/terratile_inverted_inner_stairs",
  "textures": {{
    "bottom": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top_inner",
    "top": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top_inner",
    "side": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_side",
    "front": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_front"
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(stairs_inverted_outer_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/terratile_inverted_outer_stairs",
  "textures": {{
    "bottom": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top_outer",
    "top": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top_outer",
    "side": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_side",
    "front": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_front"
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(stairs_inverted_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/terratile_inverted_stairs",
  "textures": {{
    "bottom": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top",
    "top": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_top",
    "side": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_side",
    "front": "xercafood:block/terracotta_tiles/{color}_terra_cotta_tiles_front"
  }}
}}""".format(color=colors[i]))

# Item Models

file_dir = f"../XercaMod/src/main/resources/assets/xercamod/models/item/"

full_json_paths = [file_dir + block + ".json" for block in fulls]
slab_json_paths = [file_dir + block + ".json" for block in slabs]
stairs_json_paths = [file_dir + block + ".json" for block in stairs]

for i, j in enumerate(full_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/{color}_terratile"
}}""".format(color=colors[i]))

for i, j in enumerate(slab_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/{color}_terratile_slab"
}}""".format(color=colors[i]))

for i, j in enumerate(stairs_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "parent": "xercafood:block/terracotta_tiles/{color}_terratile_stairs"
}}""".format(color=colors[i]))

# Lang
#
# for c in colors:
#     add_lang_entry("xercafood", c + "_terratile", "en_us", color_names_en[c] + " Terracotta Tile", prefix="block")
#     add_lang_entry("xercafood", c + "_terratile_slab", "en_us", color_names_en[c] + " Terracotta Tile Slab", prefix="block")
#     add_lang_entry("xercafood", c + "_terratile_stairs", "en_us", color_names_en[c] + " Terracotta Tile Stairs", prefix="block")
#     add_lang_entry("xercafood", c + "_terratile", "tr_tr", color_names_tr[c] + " Terakota Kiremit", prefix="block")
#     add_lang_entry("xercafood", c + "_terratile_slab", "tr_tr", color_names_tr[c] + " Terakota Kiremit Basamak", prefix="block")
#     add_lang_entry("xercafood", c + "_terratile_stairs", "tr_tr", color_names_tr[c] + " Terakota Kiremit Merdiven", prefix="block")

# Blockstates

file_dir = f"../XercaMod/src/main/resources/assets/xercamod/blockstates/"

full_json_paths = [file_dir + block + ".json" for block in fulls]
slab_json_paths = [file_dir + block + ".json" for block in slabs]
stairs_json_paths = [file_dir + block + ".json" for block in stairs]

for i, j in enumerate(full_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "variants": {{
    "facing=north": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile"
    }},
    "facing=south": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile",
      "y": 180
    }},
    "facing=west": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile",
      "y": 270
    }},
    "facing=east": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile",
      "y": 90
    }}
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(slab_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
  "variants": {{
    "type=bottom,facing=north": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile_slab"
    }},
    "type=bottom,facing=south": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile_slab",
      "y": 180
    }},
    "type=bottom,facing=west": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile_slab",
      "y": 270
    }},
    "type=bottom,facing=east": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile_slab",
      "y": 90
    }}
    ,
    "type=top,facing=north": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile_slab_top"
    }},
    "type=top,facing=south": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile_slab_top",
      "y": 180
    }},
    "type=top,facing=west": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile_slab_top",
      "y": 270
    }},
    "type=top,facing=east": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile_slab_top",
      "y": 90
    }}
    ,
    "type=double,facing=north": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile"
    }},
    "type=double,facing=south": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile",
      "y": 180
    }},
    "type=double,facing=west": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile",
      "y": 270
    }},
    "type=double,facing=east": {{
      "model": "xercafood:block/terracotta_tiles/{color}_terratile",
      "y": 90
    }}
  }}
}}""".format(color=colors[i]))

for i, j in enumerate(stairs_json_paths):
    with open(j, "w") as f:
        f.write("""
{{
    "variants": {{
        "facing=east,half=bottom,shape=straight":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs" }},
        "facing=west,half=bottom,shape=straight":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs", "y": 180 }},
        "facing=south,half=bottom,shape=straight": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs", "y": 90 }},
        "facing=north,half=bottom,shape=straight": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs", "y": 270 }},
        "facing=east,half=bottom,shape=outer_right":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_outer" }},
        "facing=west,half=bottom,shape=outer_right":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_outer", "y": 180 }},
        "facing=south,half=bottom,shape=outer_right": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_outer", "y": 90 }},
        "facing=north,half=bottom,shape=outer_right": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_outer", "y": 270 }},
        "facing=east,half=bottom,shape=outer_left":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_outer", "y": 270 }},
        "facing=west,half=bottom,shape=outer_left":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_outer", "y": 90 }},
        "facing=south,half=bottom,shape=outer_left": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_outer" }},
        "facing=north,half=bottom,shape=outer_left": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_outer", "y": 180 }},
        "facing=east,half=bottom,shape=inner_right":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inner" }},
        "facing=west,half=bottom,shape=inner_right":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inner", "y": 180 }},
        "facing=south,half=bottom,shape=inner_right": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inner", "y": 90 }},
        "facing=north,half=bottom,shape=inner_right": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inner", "y": 270 }},
        "facing=east,half=bottom,shape=inner_left":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inner", "y": 270 }},
        "facing=west,half=bottom,shape=inner_left":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inner", "y": 90 }},
        "facing=south,half=bottom,shape=inner_left": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inner" }},
        "facing=north,half=bottom,shape=inner_left": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inner", "y": 180 }},
        "facing=east,half=top,shape=straight":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted"}},
        "facing=west,half=top,shape=straight":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted", "y": 180 }},
        "facing=south,half=top,shape=straight": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted", "y": 90 }},
        "facing=north,half=top,shape=straight": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted", "y": 270 }},
        "facing=east,half=top,shape=outer_right":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_outer", "y": 90 }},
        "facing=west,half=top,shape=outer_right":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_outer", "y": 270 }},
        "facing=south,half=top,shape=outer_right": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_outer", "y": 180 }},
        "facing=north,half=top,shape=outer_right": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_outer" }},
        "facing=east,half=top,shape=outer_left":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_outer" }},
        "facing=west,half=top,shape=outer_left":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_outer",  "y": 180 }},
        "facing=south,half=top,shape=outer_left": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_outer",  "y": 90 }},
        "facing=north,half=top,shape=outer_left": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_outer",  "y": 270 }},
        "facing=east,half=top,shape=inner_right":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_inner", "y": 90 }},
        "facing=west,half=top,shape=inner_right":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_inner", "y": 270 }},
        "facing=south,half=top,shape=inner_right": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_inner", "y": 180 }},
        "facing=north,half=top,shape=inner_right": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_inner" }},
        "facing=east,half=top,shape=inner_left":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_inner" }},
        "facing=west,half=top,shape=inner_left":  {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_inner",  "y": 180 }},
        "facing=south,half=top,shape=inner_left": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_inner",  "y": 90 }},
        "facing=north,half=top,shape=inner_left": {{ "model": "xercafood:block/terracotta_tiles/{color}_terratile_stairs_inverted_inner",  "y": 270 }}
    }}
}}
""".format(color=colors[i]))

# Loot tables

file_dir = f"../XercaMod/src/main/resources/data/xercamod/loot_tables/blocks/"
fs = fulls + stairs
json_paths = [file_dir + block + ".json" for block in fs]

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
          "name": "xercafood:{}"
        }}
      ],
      "conditions": [
        {{
          "condition": "minecraft:survives_explosion"
        }}
      ]
    }}
  ]
}}""".format(fs[i]))

json_paths = [file_dir + block + ".json" for block in slabs]

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
          "functions": [
            {{
              "function": "minecraft:set_count",
              "conditions": [
                {{
                  "condition": "minecraft:block_state_property",
                  "block": "xercafood:{}",
                  "properties": {{
                    "type": "double"
                  }}
                }}
              ],
              "count": 2
            }},
            {{
              "function": "minecraft:explosion_decay"
            }}
          ],
          "name": "xercafood:{}"
        }}
      ]
    }}
  ]
}}""".format(slabs[i], slabs[i]))


"""

black
blue
brown
cyan
gray
green
light_blue
light_gray
lime
magenta
orange
pink
purple
red
white
yellow

BLACK
BLUE
BROWN
CYAN
GRAY
GREEN
LIGHT_BLUE
LIGHT_GRAY
LIME
MAGENTA
ORANGE
PINK
PURPLE
RED
WHITE
YELLOW

"""