from recipe_generator import *

recipes = [
    ShapedRecipe(['#s#', ' s ', ' s '], {'id': 'xercacourt:gavel'}, {'#': {'tag': 'minecraft:planks'}, 's': {'item': 'minecraft:stick'}}, {'item': 'minecraft:stick'}, ""),
    ShapelessRecipe([{'item': 'minecraft:gold_nugget'}, {'item': 'minecraft:cookie'}], {'id': 'xercacourt:attorney_badge'}, {'item': 'minecraft:gold_nugget'}, ""),
    ShapelessRecipe([{'item': 'minecraft:gold_nugget'}, {'item': 'minecraft:poppy'}], {'id': 'xercacourt:prosecutor_badge'}, {'item': 'minecraft:gold_nugget'}, "prosecutor_badge"),
    ShapelessRecipe([{'item': 'minecraft:gold_nugget'}, {'item': 'minecraft:dandelion'}], {'id': 'xercacourt:prosecutor_badge'}, {'item': 'minecraft:gold_nugget'}, "prosecutor_badge"),
]

clean_recipe_jsons("xercacourt")
generate_recipe_jsons(recipes, "xercacourt")
