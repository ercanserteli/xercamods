from recipe_generator import *


recipes = [
    ShapedRecipe([' s ', 'pip', ' p '], {'item': 'xercamusic:cello'}, {'p': {'item': 'minecraft:dark_oak_planks'}, 's': {'item': 'minecraft:stick'}, 'i': {'item': 'minecraft:string'}}, {'item': 'minecraft:dark_oak_planks'}, ""),
    ShapedRecipe([' g ', 'gig', ' g '], {'item': 'xercamusic:cymbal'}, {'g': {'item': 'minecraft:gold_ingot'}, 'i': {'item': 'minecraft:iron_ingot'}}, {'item': 'minecraft:gold_ingot'}, ""),
    ShapedRecipe([' s ', 'pip', ' p '], {'item': 'xercamusic:banjo'}, {'p': {'item': 'minecraft:acacia_planks'}, 's': {'item': 'minecraft:stick'}, 'i': {'item': 'minecraft:string'}}, {'item': 'minecraft:acacia_planks'}, ""),
    ShapedRecipe([' p ', 'plp', ' p '], {'item': 'xercamusic:drum'}, {'p': {'tag': 'minecraft:planks'}, 'l': {'item': 'minecraft:leather'}}, {'tag': 'minecraft:planks'}, ""),
    ShapedRecipe(['c c', 'ddd'], {'item': 'xercamusic:drum_kit'}, {'c': {'item': 'xercamusic:cymbal'}, 'd': {'item': 'xercamusic:drum'}}, {'item': 'xercamusic:drum'}, ""),
    ShapedRecipe(['sss'], {'item': 'xercamusic:flute'}, {'s': {'item': 'minecraft:stick'}}, {'item': 'minecraft:stick'}, ""),
    ShapedRecipe([' s ', 'pip', ' p '], {'item': 'xercamusic:guitar'}, {'p': {'item': 'minecraft:oak_planks'}, 's': {'item': 'minecraft:stick'}, 'i': {'item': 'minecraft:string'}}, {'item': 'minecraft:oak_planks'}, ""),
    ShapedRecipe(['sis', ' s '], {'item': 'xercamusic:lyre'}, {'s': {'item': 'minecraft:stick'}, 'i': {'item': 'minecraft:string'}}, {'item': 'minecraft:stick'}, ""),
    ShapedRecipe(['www', 'wew', 'wrw'], {'item': 'xercamusic:music_box'}, {'e': {'item': 'minecraft:gold_ingot'}, 'r': {'item': 'minecraft:redstone'}, 'w': {'tag': 'minecraft:planks'}}, {'tag': 'minecraft:planks'}, ""),
    ShapedRecipe(['iii', 'ppp'], {'item': 'xercamusic:sansula'}, {'i': {'item': 'minecraft:iron_nugget'}, 'p': {'tag': 'minecraft:planks'}}, {'item': 'minecraft:iron_nugget'}, ""),
    ShapedRecipe(['  g', 'g g', ' g '], {'item': 'xercamusic:saxophone'}, {'g': {'item': 'minecraft:gold_ingot'}}, {'item': 'minecraft:gold_ingot'}, ""),
    ShapedRecipe(['sss', 'g g'], {'item': 'xercamusic:tubular_bell'}, {'s': {'item': 'minecraft:stick'}, 'g': {'item': 'minecraft:gold_ingot'}}, {'item': 'minecraft:gold_ingot'}, ""),
    ShapedRecipe([' s ', 'pip', ' p '], {'item': 'xercamusic:violin'}, {'p': {'item': 'minecraft:spruce_planks'}, 's': {'item': 'minecraft:stick'}, 'i': {'item': 'minecraft:string'}}, {'item': 'minecraft:spruce_planks'}, ""),
    ShapedRecipe(['sss', 'ppp'], {'item': 'xercamusic:xylophone'}, {'s': {'item': 'minecraft:stick'}, 'p': {'tag': 'minecraft:planks'}}, {'tag': 'minecraft:planks'}, ""),

    ShapelessRecipe([{'item': 'minecraft:note_block'}, {'item': 'minecraft:clock'}], {'item': 'xercamusic:metronome'}, {'item': 'minecraft:note_block'}, ""),
    ShapelessRecipe([{'item': 'minecraft:paper'}, {'item': 'minecraft:ink_sac'}, {'item': 'minecraft:feather'}], {'item': 'xercamusic:music_sheet'}, {'item': 'minecraft:paper'}, ""),

    SpecialRecipe("xercamusic:crafting_special_notecloning", None, ""),
]


# generate_recipe_code_from_files("xercamusic")
generate_recipe_jsons(recipes, "xercamusic")
