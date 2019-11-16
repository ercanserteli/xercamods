from recipe_generator import *


recipes = [
    ShapedRecipe(['sss', 'sps', 'sss'], {'item': 'xercapaint:item_canvas'}, {'p': {'item': 'minecraft:paper'}, 's': {'item': 'minecraft:stick'}}, {'item': 'minecraft:paper'}, ""),
    ShapedRecipe(['cc', 'cc'], {'item': 'xercapaint:item_canvas_large'}, {'c': {'item': 'xercapaint:item_canvas'}}, {'item': 'xercapaint:item_canvas'}, ""),

    SpecialRecipe("xercapaint:crafting_special_palette_crafting", None, ""),
    SpecialRecipe("xercapaint:crafting_special_palette_filling", None, ""),
]


# generate_recipe_code_from_files("xercapaint")
generate_recipe_jsons(recipes, "xercapaint")
