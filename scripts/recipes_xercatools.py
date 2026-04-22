from recipe_generator import *

recipes = [
    ShapedRecipe([' l ', 'lel', ' l '], {'id': 'xercatools:flask'}, {'e': {'item': 'minecraft:ender_eye'}, 'l': {'item': 'minecraft:leather'}}, {'item': 'minecraft:ender_eye'}, "", "ender_flask"),
    ShapedRecipe(['f', 'c'], {'id': 'xercatools:ender_bow'}, {'c': {'item': 'minecraft:crossbow'}, 'f': {'item': 'xercatools:flask'}}, {'item': 'xercatools:flask'}, "", "ender_flask"),
    ShapedRecipe(['i', 's'], {'id': 'xercatools:knife'}, {'s': {'item': 'minecraft:stick'}, 'i': {'item': 'minecraft:iron_ingot'}}, {'item': 'minecraft:iron_ingot'}, ""),
    ShapedRecipe(['  s', ' sc', 's i'], {'id': 'xercatools:grab_hook'}, {'s': {'item': 'minecraft:stick'}, 'c': {'item': 'minecraft:chain'}, 'i': {'item': 'minecraft:iron_ingot'}}, {'item': 'minecraft:iron_ingot'}, "", "grab_hook"),

    ShapedRecipe(['tst', ' s ', ' s '], {'id': 'xercatools:stone_warhammer'}, {'s': {'item': 'minecraft:stick'}, 't': {'item': 'minecraft:cobblestone'}}, {'item': 'minecraft:cobblestone'}, "", "warhammer"),
    ShapedRecipe(['tst', ' s ', ' s '], {'id': 'xercatools:iron_warhammer'}, {'s': {'item': 'minecraft:stick'}, 't': {'item': 'minecraft:iron_ingot'}}, {'item': 'minecraft:iron_ingot'}, "", "warhammer"),
    ShapedRecipe(['tst', ' s ', ' s '], {'id': 'xercatools:gold_warhammer'}, {'s': {'item': 'minecraft:stick'}, 't': {'item': 'minecraft:gold_ingot'}}, {'item': 'minecraft:gold_ingot'}, "", "warhammer"),
    ShapedRecipe(['tst', ' s ', ' s '], {'id': 'xercatools:diamond_warhammer'}, {'s': {'item': 'minecraft:stick'}, 't': {'item': 'minecraft:diamond'}}, {'item': 'minecraft:diamond'}, "", "warhammer"),

    ShapedRecipe(['iis', ' s ', 's  '], {'id': 'xercatools:wooden_scythe'}, {'i': {'tag': 'minecraft:planks'}, 's': {'item': 'minecraft:stick'}}, {'tag': 'minecraft:planks'}, "", "scythe"),
    ShapedRecipe(['iis', ' s ', 's  '], {'id': 'xercatools:stone_scythe'}, {'i': {'item': 'minecraft:cobblestone'}, 's': {'item': 'minecraft:stick'}}, {'item': 'minecraft:cobblestone'}, "", "scythe"),
    ShapedRecipe(['iis', ' s ', 's  '], {'id': 'xercatools:iron_scythe'}, {'i': {'item': 'minecraft:iron_ingot'}, 's': {'item': 'minecraft:stick'}}, {'item': 'minecraft:iron_ingot'}, "", "scythe"),
    ShapedRecipe(['iis', ' s ', 's  '], {'id': 'xercatools:golden_scythe'}, {'i': {'item': 'minecraft:gold_ingot'}, 's': {'item': 'minecraft:stick'}}, {'item': 'minecraft:gold_ingot'}, "", "scythe"),
    ShapedRecipe(['iis', ' s ', 's  '], {'id': 'xercatools:diamond_scythe'}, {'i': {'item': 'minecraft:diamond'}, 's': {'item': 'minecraft:stick'}}, {'item': 'minecraft:diamond'}, "", "scythe"),

    SpecialRecipe("xercatools:crafting_special_flask_filling"),
    SpecialRecipe("xercatools:crafting_special_ender_bow_filling"),

    SmithingRecipe("xercatools:diamond_scythe", "minecraft:netherite_ingot", "xercatools:netherite_scythe"),
    SmithingRecipe("xercatools:diamond_warhammer", "minecraft:netherite_ingot", "xercatools:netherite_warhammer"),
]

clean_recipe_jsons("xercatools")
generate_recipe_jsons(recipes, "xercatools")
