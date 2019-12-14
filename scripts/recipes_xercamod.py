from copy import deepcopy

from recipe_generator import *

shaped_recipes = [
    ShapedRecipe([' l ', 'lel', ' l '], {'item': 'xercamod:flask'}, {'e': {'item': 'minecraft:ender_eye'}, 'l': {'item': 'minecraft:leather'}}, {'item': 'minecraft:ender_eye'}, "", type="xercamod:crafting_condition_shaped_ender_flask"),
    ShapedRecipe(['sas', 'mem', 'www'], {'item': 'xercamod:item_apple_cupcake', 'count': 6}, {'a': {'item': 'minecraft:apple'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:apple'}, "cupcake", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['ADA', 'BEB', 'CCC'], {'item': 'xercamod:item_apple_pie'}, {'A': {'item': 'minecraft:apple'}, 'B': {'item': 'minecraft:sugar'}, 'C': {'item': 'minecraft:wheat'}, 'D': {'item': 'minecraft:milk_bucket'}, 'E': {'item': 'minecraft:egg'}}, {'item': 'minecraft:apple'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['lll', 'lll', 'lll'], {'item': 'xercamod:item_block_leather'}, {'l': {'item': 'minecraft:leather'}}, {'item': 'minecraft:leather'}, "", type="xercamod:crafting_condition_shaped_leather_straw"),
    ShapedRecipe(['lll', 'lll', 'lll'], {'item': 'xercamod:item_block_straw'}, {'l': {'item': 'minecraft:sugar_cane'}}, {'item': 'minecraft:sugar_cane'}, "", type="xercamod:crafting_condition_shaped_leather_straw"),
    ShapedRecipe(['www', '   ', 'www'], {'item': 'xercamod:item_bookcase'}, {'w': {'tag': 'minecraft:planks'}}, {'tag': 'minecraft:planks'}, "", type="xercamod:crafting_condition_shaped_bookcase"),
    ShapedRecipe(['sas', 'mem', 'www'], {'item': 'xercamod:item_carrot_cupcake', 'count': 6}, {'a': {'item': 'minecraft:carrot'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:carrot'}, "cupcake", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe([' b ', 'dc ', ' b '], {'item': 'xercamod:item_chicken_burger'}, {'b': {'item': 'xercamod:item_bun'}, 'c': {'item': 'xercamod:item_cooked_chicken_patty'}, 'd': {'item': 'xercamod:item_tomato_slices'}}, {'item': 'xercamod:item_bun'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe([' wc', 'wcw', 'cw '], {'item': 'xercamod:item_chicken_wrap', 'count': 4}, {'c': {'item': 'minecraft:cooked_chicken'}, 'w': {'item': 'minecraft:wheat'}}, {'item': 'minecraft:cooked_chicken'}, "chicken_wrap", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['cw ', 'wcw', ' wc'], {'item': 'xercamod:item_chicken_wrap', 'count': 4}, {'c': {'item': 'minecraft:cooked_chicken'}, 'w': {'item': 'minecraft:wheat'}}, {'item': 'minecraft:cooked_chicken'}, "chicken_wrap", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe([' s ', 'cmc', ' s '], {'item': 'xercamod:item_chocolate', 'count': 6}, {'s': {'item': 'minecraft:sugar'}, 'c': {'item': 'minecraft:cocoa_beans'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:cocoa_beans'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['sps', 'mem', 'www'], {'item': 'xercamod:item_cocoa_cupcake', 'count': 6}, {'p': {'item': 'minecraft:cocoa_beans'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:cocoa_beans'}, "cupcake", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe([' c ', 'cgc', ' c '], {'item': 'xercamod:item_confetti_ball', 'count': 2}, {'c': {'item': 'xercamod:item_confetti'}, 'g': {'item': 'minecraft:gunpowder'}}, {'item': 'xercamod:item_confetti'}, "", type="xercamod:crafting_condition_shaped_confetti"),
    ShapedRecipe(['sws', 'www'], {'item': 'xercamod:item_croissant', 'count': 2}, {'s': {'item': 'minecraft:sugar'}, 'w': {'item': 'minecraft:wheat'}}, {'item': 'minecraft:wheat'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe([' b ', 'yyy', ' b '], {'item': 'xercamod:item_daisy_sandwich', 'count': 3}, {'b': {'item': 'minecraft:bread'}, 'y': {'item': 'minecraft:oxeye_daisy'}}, {'item': 'minecraft:oxeye_daisy'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['tst', ' s ', ' s '], {'item': 'xercamod:item_diamond_warhammer'}, {'s': {'item': 'minecraft:stick'}, 't': {'item': 'minecraft:diamond'}}, {'item': 'minecraft:diamond'}, "", type="xercamod:crafting_condition_shaped_warhammer"),
    ShapedRecipe(['sws', 'wxw', 'sws'], {'item': 'xercamod:item_donut', 'count': 4}, {'s': {'item': 'minecraft:sugar'}, 'w': {'item': 'minecraft:wheat'}, 'x': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:wheat'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['s', 'c'], {'item': 'xercamod:item_fancy_apple_cupcake'}, {'s': {'item': 'xercamod:item_sprinkles'}, 'c': {'item': 'xercamod:item_apple_cupcake'}}, {'item': 'xercamod:item_sprinkles'}, "cupcake", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['s', 'd'], {'item': 'xercamod:item_fancy_donut'}, {'s': {'item': 'xercamod:item_sprinkles'}, 'd': {'item': 'xercamod:item_donut'}}, {'item': 'xercamod:item_sprinkles'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['s', 'c'], {'item': 'xercamod:item_fancy_pumpkin_cupcake'}, {'s': {'item': 'xercamod:item_sprinkles'}, 'c': {'item': 'xercamod:item_pumpkin_cupcake'}}, {'item': 'xercamod:item_sprinkles'}, "cupcake", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['b', 'h', 'b'], {'item': 'xercamod:item_fish_bread', 'count': 2}, {'b': {'item': 'minecraft:bread'}, 'h': {'item': 'minecraft:cooked_cod'}}, {'item': 'minecraft:cooked_cod'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['#s#', ' s ', ' s '], {'item': 'xercamod:item_gavel'}, {'#': {'tag': 'minecraft:planks'}, 's': {'item': 'minecraft:stick'}}, {'tag': 'minecraft:planks'}, "", type="xercamod:crafting_condition_shaped_courtroom"),
    ShapedRecipe(['c c', ' c '], {'item': 'xercamod:item_glass', 'count': 3}, {'c': {'item': 'minecraft:glass_pane'}}, {'item': 'minecraft:glass_pane'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['nan', 'mem', 'www'], {'item': 'xercamod:item_golden_cupcake'}, {'a': {'item': 'minecraft:golden_apple', 'data': 0}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}, 'n': {'item': 'minecraft:gold_nugget'}}, None, ""),
    ShapedRecipe(['tst', ' s ', ' s '], {'item': 'xercamod:item_gold_warhammer'}, {'s': {'item': 'minecraft:stick'}, 't': {'item': 'minecraft:gold_ingot'}}, {'item': 'minecraft:gold_ingot'}, "", type="xercamod:crafting_condition_shaped_warhammer"),
    ShapedRecipe([' b ', 'dh ', ' b '], {'item': 'xercamod:item_hamburger'}, {'b': {'item': 'xercamod:item_bun'}, 'd': {'item': 'xercamod:item_tomato_slices'}, 'h': {'item': 'xercamod:item_cooked_patty'}}, {'item': 'xercamod:item_bun'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['b', 'p', 'b'], {'item': 'xercamod:item_hotdog'}, {'p': {'item': 'xercamod:item_cooked_sausage'}, 'b': {'item': 'xercamod:item_bun'}}, {'item': 'xercamod:item_bun'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['tst', ' s ', ' s '], {'item': 'xercamod:item_iron_warhammer'}, {'s': {'item': 'minecraft:stick'}, 't': {'item': 'minecraft:iron_ingot'}}, {'item': 'minecraft:iron_ingot'}, "", type="xercamod:crafting_condition_shaped_warhammer"),
    ShapedRecipe(['i', 's'], {'item': 'xercamod:item_knife'}, {'s': {'item': 'minecraft:stick'}, 'i': {'item': 'minecraft:iron_ingot'}}, {'item': 'minecraft:iron_ingot'}, ""),
    ShapedRecipe(['sps', 'mem', 'www'], {'item': 'xercamod:item_melon_cupcake', 'count': 6}, {'p': {'item': 'minecraft:melon'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:melon'}, "cupcake", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe([' b ', 'dmn', ' b '], {'item': 'xercamod:item_mushroom_burger'}, {'b': {'item': 'xercamod:item_bun'}, 'd': {'item': 'xercamod:item_tomato_slices'}, 'm': {'item': 'minecraft:brown_mushroom'}, 'n': {'item': 'minecraft:red_mushroom'}}, {'item': 'xercamod:item_bun'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['sps', 'mem', 'www'], {'item': 'xercamod:item_pumpkin_cupcake', 'count': 6}, {'p': {'item': 'minecraft:pumpkin'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:pumpkin'}, "cupcake", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe([' w ', 'wcw', ' w '], {'item': 'xercamod:item_raw_schnitzel', 'count': 2}, {'c': {'item': 'minecraft:chicken'}, 'w': {'item': 'minecraft:wheat'}}, {'item': 'minecraft:chicken'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['b', 'r', 'b'], {'item': 'xercamod:item_rotten_burger'}, {'b': {'item': 'xercamod:item_bun'}, 'r': {'item': 'minecraft:rotten_flesh'}}, {'item': 'xercamod:item_bun'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['  s', ' b ', 's  '], {'item': 'xercamod:item_shish_kebab', 'count': 2}, {'b': {'item': 'minecraft:cooked_beef'}, 's': {'item': 'minecraft:stick'}}, {'item': 'minecraft:cooked_beef'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['tst', ' s ', ' s '], {'item': 'xercamod:item_stone_warhammer'}, {'s': {'item': 'minecraft:stick'}, 't': {'item': 'minecraft:cobblestone'}}, {'item': 'minecraft:cobblestone'}, "", type="xercamod:crafting_condition_shaped_warhammer"),
    ShapedRecipe(['c c', ' c '], {'item': 'xercamod:item_teacup', 'count': 2}, {'c': {'item': 'minecraft:clay_ball'}}, {'item': 'minecraft:clay_ball'}, "", type="xercamod:crafting_condition_shaped_tea"),
    ShapedRecipe([' c ', 'c c', 'ccc'], {'item': 'xercamod:item_teapot'}, {'c': {'item': 'minecraft:clay_ball'}}, {'item': 'minecraft:clay_ball'}, "", type="xercamod:crafting_condition_shaped_tea"),
    ShapedRecipe(['mnm', ' p ', ' b '], {'item': 'xercamod:item_ultimate_bottom'}, {'p': {'item': 'xercamod:item_cooked_patty'}, 'b': {'item': 'xercamod:item_bun'}, 'm': {'item': 'minecraft:brown_mushroom'}, 'n': {'item': 'minecraft:red_mushroom'}}, {'item': 'xercamod:item_bun'}, "ultimate_bottom", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe(['nmn', ' p ', ' b '], {'item': 'xercamod:item_ultimate_bottom'}, {'p': {'item': 'xercamod:item_cooked_patty'}, 'b': {'item': 'xercamod:item_bun'}, 'm': {'item': 'minecraft:brown_mushroom'}, 'n': {'item': 'minecraft:red_mushroom'}}, {'item': 'xercamod:item_bun'}, "ultimate_bottom", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe([' t ', ' e ', ' b '], {'item': 'xercamod:item_ultimate_burger'}, {'b': {'item': 'xercamod:item_ultimate_bottom'}, 't': {'item': 'xercamod:item_ultimate_top'}, 'e': {'item': 'xercamod:item_bun'}}, {'item': 'xercamod:item_bun'}, "", type="xercamod:crafting_condition_shaped_food"),
    ShapedRecipe([' b ', 'ttt', ' c '], {'item': 'xercamod:item_ultimate_top'}, {'b': {'item': 'xercamod:item_bun'}, 'c': {'item': 'xercamod:item_cooked_chicken_patty'}, 't': {'item': 'xercamod:item_tomato_slices'}}, {'item': 'xercamod:item_bun'}, "", type="xercamod:crafting_condition_shaped_food"),

    # Cushion
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:black_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:black_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:blue_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:blue_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:brown_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:brown_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:cyan_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:cyan_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:gray_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:gray_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:green_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:green_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:light_blue_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:light_blue_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:light_gray_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:light_gray_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:lime_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:lime_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:magenta_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:magenta_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:orange_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:orange_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:pink_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:pink_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:purple_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:purple_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:red_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:red_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:white_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:white_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
    ShapedRecipe(['#', 'f', '#'], {'item': 'xercamod:yellow_cushion', 'count': 1}, {'f': {'item': 'minecraft:feather'}, '#': {'item': 'minecraft:yellow_wool'}}, {'item': 'minecraft:feather'}, "cushion", "cushion", type="xercamod:crafting_condition_shaped_cushion"),
]

shapeless_recipes = [
    ShapelessRecipe([{'item': 'xercamod:item_glass'}, {'item': 'minecraft:apple'}], {'item': 'xercamod:item_apple_juice'}, {'item': 'xercamod:item_glass'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'minecraft:gold_nugget'}, {'item': 'minecraft:cookie'}], {'item': 'xercamod:item_attorney_badge'}, {'item': 'minecraft:gold_nugget'}, "", type="xercamod:crafting_condition_shapeless_courtroom"),
    ShapelessRecipe([{'item': 'minecraft:bread'}, {'item': 'xercamod:item_knife', 'data': 32767}], {'item': 'xercamod:item_bun', 'count': 2}, {'item': 'minecraft:bread'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'xercamod:item_glass'}, {'item': 'minecraft:carrot'}], {'item': 'xercamod:item_carrot_juice'}, {'item': 'xercamod:item_glass'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'minecraft:paper'}, {'item': 'minecraft:cyan_dye'}, {'item': 'minecraft:magenta_dye'}, {'item': 'minecraft:yellow_dye'}], {'item': 'xercamod:item_confetti', 'count': 12}, {'item': 'minecraft:paper'}, "", type="xercamod:crafting_condition_shapeless_confetti"),
    ShapelessRecipe([{'item': 'xercamod:item_glass'}, {'item': 'minecraft:milk_bucket'}], {'item': 'xercamod:item_glass_of_milk'}, {'item': 'xercamod:item_glass'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'xercamod:item_glass'}, {'item': 'minecraft:water_bucket'}], {'item': 'xercamod:item_glass_of_water'}, {'item': 'xercamod:item_glass'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'xercamod:item_knife'}, {'item': 'minecraft:fishing_rod'}], {'item': 'xercamod:item_grab_hook'}, {'item': 'minecraft:fishing_rod'}, "", type="xercamod:crafting_condition_shapeless_grab_hook"),
    ShapelessRecipe([{'item': 'xercamod:item_tea_dried'}, {'item': 'xercamod:item_glass_of_water'}, {'item': 'minecraft:snowball'}, {'item': 'minecraft:sugar'}], {'item': 'xercamod:item_ice_tea'}, {'item': 'xercamod:item_glass_of_water'}, "", type="xercamod:crafting_condition_shapeless_tea"),
    ShapelessRecipe([{'item': 'xercamod:item_glass'}, {'item': 'minecraft:melon'}], {'item': 'xercamod:item_melon_juice'}, {'item': 'xercamod:item_glass'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'minecraft:potato'}, {'item': 'xercamod:item_knife', 'data': 32767}], {'item': 'xercamod:item_potato_slices', 'count': 2}, {'item': 'minecraft:potato'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'minecraft:gold_nugget'}, {'item': 'minecraft:poppy', 'data': 0}], {'item': 'xercamod:item_prosecutor_badge'}, {'item': 'minecraft:gold_nugget'}, "pro_badge", type="xercamod:crafting_condition_shapeless_courtroom"),
    ShapelessRecipe([{'item': 'minecraft:gold_nugget'}, {'item': 'minecraft:dandelion', 'data': 0}], {'item': 'xercamod:item_prosecutor_badge'}, {'item': 'minecraft:gold_nugget'}, "pro_badge", type="xercamod:crafting_condition_shapeless_courtroom"),
    ShapelessRecipe([{'item': 'xercamod:item_glass'}, {'item': 'minecraft:pumpkin'}], {'item': 'xercamod:item_pumpkin_juice'}, {'item': 'xercamod:item_glass'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'minecraft:chicken'}, {'item': 'xercamod:item_knife', 'data': 32767}], {'item': 'xercamod:item_raw_chicken_patty', 'count': 2}, {'item': 'minecraft:chicken'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'minecraft:beef'}, {'item': 'xercamod:item_knife', 'data': 32767}], {'item': 'xercamod:item_raw_patty', 'count': 2}, {'item': 'minecraft:beef'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'minecraft:porkchop'}, {'item': 'xercamod:item_knife', 'data': 32767}], {'item': 'xercamod:item_raw_sausage', 'count': 2}, {'item': 'minecraft:porkchop'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'minecraft:sugar'}, {'item': 'minecraft:sugar'}, {'item': 'minecraft:sugar'}, {'item': 'minecraft:cyan_dye'}, {'item': 'minecraft:magenta_dye'}, {'item': 'minecraft:yellow_dye'}], {'item': 'xercamod:item_sprinkles', 'count': 12}, {'item': 'minecraft:sugar'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'xercamod:item_glass'}, {'item': 'xercamod:item_tomato'}], {'item': 'xercamod:item_tomato_juice'}, {'item': 'xercamod:item_glass'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'xercamod:item_tomato'}], {'item': 'xercamod:item_tomato_seeds'}, {'item': 'xercamod:item_tomato'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'xercamod:item_tomato'}, {'item': 'xercamod:item_knife', 'data': 32767}], {'item': 'xercamod:item_tomato_slices', 'count': 3}, {'item': 'xercamod:item_tomato'}, "", type="xercamod:crafting_condition_shapeless_food"),
    ShapelessRecipe([{'item': 'xercamod:item_glass'}, {'item': 'minecraft:wheat'}, {'item': 'minecraft:wheat'}], {'item': 'xercamod:item_wheat_juice'}, {'item': 'xercamod:item_glass'}, "", type="xercamod:crafting_condition_shapeless_food"),
]

cooking_recipes = [
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_raw_chicken_patty'}, "xercamod:item_cooked_chicken_patty", 0.15, 200, {'item': 'xercamod:item_raw_chicken_patty'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'minecraft:egg'}, "xercamod:item_fried_egg", 0.2, 200, {'item': 'minecraft:egg'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_raw_patty'}, "xercamod:item_cooked_patty", 0.2, 200, {'item': 'xercamod:item_raw_patty'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_potato_slices'}, "xercamod:item_potato_fries", 0.2, 200, {'item': 'xercamod:item_potato_slices'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_raw_sausage'}, "xercamod:item_cooked_sausage", 0.2, 200, {'item': 'xercamod:item_raw_sausage'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_raw_schnitzel'}, "xercamod:item_cooked_schnitzel", 0.2, 200, {'item': 'xercamod:item_raw_schnitzel'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_tea_leaf'}, "xercamod:item_tea_dried", 0.1, 200, {'item': 'xercamod:item_tea_leaf'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_full_teapot_1'}, "xercamod:item_hot_teapot_1", 0.1, 100, {'item': 'xercamod:item_full_teapot_1'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_full_teapot_2'}, "xercamod:item_hot_teapot_2", 0.15, 150, {'item': 'xercamod:item_full_teapot_2'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_full_teapot_3'}, "xercamod:item_hot_teapot_3", 0.2, 200, {'item': 'xercamod:item_full_teapot_3'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_full_teapot_4'}, "xercamod:item_hot_teapot_4", 0.25, 250, {'item': 'xercamod:item_full_teapot_4'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_full_teapot_5'}, "xercamod:item_hot_teapot_5", 0.3, 300, {'item': 'xercamod:item_full_teapot_5'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_full_teapot_6'}, "xercamod:item_hot_teapot_6", 0.35, 350, {'item': 'xercamod:item_full_teapot_6'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercamod:item_full_teapot_7'}, "xercamod:item_hot_teapot_7", 0.4, 400, {'item': 'xercamod:item_full_teapot_7'}, "hot_teapot"),
]

special_recipes = [
    SpecialRecipe("xercamod:crafting_special_flask_filling", None, ""),
    SpecialRecipe("xercamod:crafting_special_flask_milk_filling", None, ""),
    SpecialRecipe("xercamod:crafting_special_tea_filling", None, ""),
    SpecialRecipe("xercamod:crafting_special_tea_pouring", None, ""),
    SpecialRecipe("xercamod:crafting_special_tea_refilling", None, ""),
    SpecialRecipe("xercamod:crafting_special_tea_sugaring", None, ""),
    SpecialRecipe("xercamod:crafting_special_wood_carving", None, ""),
]

campfire_recipes = []
smoker_recipes = []

for r in cooking_recipes:
    campfire_recipe = deepcopy(r)
    campfire_recipe.type = "minecraft:campfire_cooking"
    campfire_recipe.cooking_time *= 3
    campfire_recipes.append(campfire_recipe)

    smoker_recipe = deepcopy(r)
    smoker_recipe.type = "minecraft:smoking"
    smoker_recipe.cooking_time = int(smoker_recipe.cooking_time*0.5)
    smoker_recipes.append(smoker_recipe)

all_recipes = [r for l in [shaped_recipes, shapeless_recipes, cooking_recipes, special_recipes, campfire_recipes, smoker_recipes] for r in l]


# generate_recipe_code_from_files("xercamod")
generate_recipe_jsons(all_recipes, "xercamod")
