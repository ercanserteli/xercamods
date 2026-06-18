from recipe_generator import *

colors = ["black", "blue", "brown", "cyan", "gray", "green", "light_blue", "light_gray",
          "lime", "magenta", "orange", "pink", "purple", "red", "white", "yellow"]

recipes = [
    ShapedRecipe(['#', 'f', '#'], {'id': f'xercacushion:{c}_cushion'}, {'#': {'item': f'minecraft:{c}_wool'}, 'f': {'item': 'minecraft:feather'}}, {'item': 'minecraft:feather'}, "cushion")
    for c in colors
]

clean_recipe_jsons("xercacushion")
generate_recipe_jsons(recipes, "xercacushion")
