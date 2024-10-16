from recipe_generator import *


recipes = [
    ShapedRecipe(['sss', 'sps', 'sss'], {'id': 'xercapaint:item_canvas'}, {'p': {'item': 'minecraft:paper'}, 's': {'item': 'minecraft:stick'}}, {'item': 'minecraft:paper'}, ""),
    ShapedRecipe(['cc', 'cc'], {'id': 'xercapaint:item_canvas_large'}, {'c': {'item': 'xercapaint:item_canvas'}}, {'item': 'xercapaint:item_canvas'}, "", type="xercapaint:crafting_tagless_shaped"),
    ShapedRecipe(['cc'], {'id': 'xercapaint:item_canvas_long'}, {'c': {'item': 'xercapaint:item_canvas'}}, {'item': 'xercapaint:item_canvas'}, "", type="xercapaint:crafting_tagless_shaped"),
    ShapedRecipe(['cc'], {'id': 'xercapaint:item_canvas_large'}, {'c': {'item': 'xercapaint:item_canvas_tall'}}, {'item': 'xercapaint:item_canvas_tall'}, "", type="xercapaint:crafting_tagless_shaped"),
    ShapedRecipe(['c', 'c'], {'id': 'xercapaint:item_canvas_tall'}, {'c': {'item': 'xercapaint:item_canvas'}}, {'item': 'xercapaint:item_canvas'}, "", type="xercapaint:crafting_tagless_shaped"),
    ShapedRecipe(['c', 'c'], {'id': 'xercapaint:item_canvas_large'}, {'c': {'item': 'xercapaint:item_canvas_long'}}, {'item': 'xercapaint:item_canvas_long'}, "", type="xercapaint:crafting_tagless_shaped"),
    ShapedRecipe([' s ', ' s ', 's s'], {'id': 'xercapaint:item_easel'}, {'s': {'item': 'minecraft:stick'}}, {'item': 'minecraft:stick'}, ""),

    SpecialRecipe("xercapaint:crafting_special_palette_crafting", None, ""),
    SpecialRecipe("xercapaint:crafting_special_palette_filling", None, ""),
    SpecialRecipe("xercapaint:crafting_special_canvas_cloning", None, ""),
]

clean_recipe_jsons("xercapaint")
# generate_recipe_code_from_files("xercapaint")
generate_recipe_jsons(recipes, "xercapaint")
