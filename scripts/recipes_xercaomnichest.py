from recipe_generator import *

recipes = [
    ShapedRecipe(['aea', 'ece', 'aea'], {'id': 'xercaomnichest:omni_chest'}, {'e': {'item': 'minecraft:ender_eye'}, 'c': {'item': 'minecraft:ender_chest'}, 'a': {'item': 'minecraft:amethyst_block'}}, {'item': 'minecraft:ender_chest'}, ""),
]

clean_recipe_jsons("xercaomnichest")
generate_recipe_jsons(recipes, "xercaomnichest")
