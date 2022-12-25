from copy import deepcopy

from recipe_generator import *

shaped_recipes = [
    ShapedRecipe([' c ', 'cgc', ' c '], {'item': 'xercaconfetti:confetti_ball', 'count': 2}, {'c': {'item': 'xercaconfetti:confetti'}, 'g': {'item': 'minecraft:gunpowder'}}, {'item': 'xercaconfetti:confetti'}, "")
]

shapeless_recipes = [
    ShapelessRecipe([{'item': 'minecraft:paper'}, {'item': 'minecraft:cyan_dye'}, {'item': 'minecraft:magenta_dye'}, {'item': 'minecraft:yellow_dye'}], {'item': 'xercaconfetti:confetti', 'count': 12}, {'item': 'minecraft:paper'}, "")
]

all_recipes = [r for l in [shaped_recipes, shapeless_recipes] for r in l]


clean_recipe_jsons("xercaconfetti")
generate_recipe_jsons(all_recipes, "xercaconfetti")
