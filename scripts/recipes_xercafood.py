from copy import deepcopy

from recipe_generator import *

xercatools_not_loaded_condition = [
    {
        "condition": "fabric:not",
        "value": {
            "condition": "fabric:all_mods_loaded",
            "values": [
                "xercatools"
            ]
        }
    }
]

shaped_recipes = [
    ShapedRecipe(['sas', 'mem', 'www'], {'id': 'xercafood:glowberry_cupcake', 'count': 6}, {'a': {'item': 'minecraft:glow_berries'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:glow_berries'}, ""),
    ShapedRecipe(['www', 'mem', 'sas'], {'id': 'xercafood:ender_cupcake', 'count': 6}, {'a': {'item': 'minecraft:chorus_fruit'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:chorus_fruit'}, ""),
    ShapedRecipe(['sas', 'mem', 'www'], {'id': 'xercafood:honey_cupcake', 'count': 6}, {'a': {'item': 'minecraft:honey_bottle'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:honey_bottle'}, "cupcake"),
    ShapedRecipe(['sas', 'mem', 'www'], {'id': 'xercafood:sweet_berry_cupcake', 'count': 6}, {'a': {'item': 'minecraft:sweet_berries'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:sweet_berries'}, "cupcake"),
    ShapedRecipe(['sas', 'mem', 'www'], {'id': 'xercafood:apple_cupcake', 'count': 6}, {'a': {'item': 'minecraft:apple'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:apple'}, "cupcake"),
    ShapedRecipe(['ADA', 'BEB', 'CCC'], {'id': 'xercafood:sweet_berry_pie'}, {'A': {'item': 'minecraft:sweet_berries'}, 'B': {'item': 'minecraft:sugar'}, 'C': {'item': 'minecraft:wheat'}, 'D': {'item': 'minecraft:milk_bucket'}, 'E': {'item': 'minecraft:egg'}}, {'item': 'minecraft:sweet_berries'}, ""),
    ShapedRecipe(['ADA', 'BEB', 'CCC'], {'id': 'xercafood:apple_pie'}, {'A': {'item': 'minecraft:apple'}, 'B': {'item': 'minecraft:sugar'}, 'C': {'item': 'minecraft:wheat'}, 'D': {'item': 'minecraft:milk_bucket'}, 'E': {'item': 'minecraft:egg'}}, {'item': 'minecraft:apple'}, ""),
    ShapedRecipe(['sas', 'mem', 'www'], {'id': 'xercafood:carrot_cupcake', 'count': 6}, {'a': {'item': 'minecraft:carrot'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:carrot'}, "cupcake"),
    ShapedRecipe([' b ', 'dc ', ' b '], {'id': 'xercafood:chicken_burger'}, {'b': {'item': 'xercafood:bun'}, 'c': {'item': 'xercafood:cooked_chicken_patty'}, 'd': {'item': 'xercafood:tomato_slices'}}, {'item': 'xercafood:bun'}, ""),
    ShapedRecipe([' wc', 'wcw', 'cw '], {'id': 'xercafood:chicken_wrap', 'count': 4}, {'c': {'item': 'minecraft:cooked_chicken'}, 'w': {'item': 'minecraft:wheat'}}, {'item': 'minecraft:cooked_chicken'}, "chicken_wrap"),
    ShapedRecipe(['cw ', 'wcw', ' wc'], {'id': 'xercafood:chicken_wrap', 'count': 4}, {'c': {'item': 'minecraft:cooked_chicken'}, 'w': {'item': 'minecraft:wheat'}}, {'item': 'minecraft:cooked_chicken'}, "chicken_wrap"),
    ShapedRecipe([' s ', 'cmc', ' s '], {'id': 'xercafood:chocolate', 'count': 6}, {'s': {'item': 'minecraft:sugar'}, 'c': {'item': 'minecraft:cocoa_beans'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:cocoa_beans'}, ""),
    ShapedRecipe(['sps', 'mem', 'www'], {'id': 'xercafood:cocoa_cupcake', 'count': 6}, {'p': {'item': 'minecraft:cocoa_beans'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:cocoa_beans'}, "cupcake"),
    ShapedRecipe(['sws', 'www'], {'id': 'xercafood:croissant', 'count': 2}, {'s': {'item': 'minecraft:sugar'}, 'w': {'item': 'minecraft:wheat'}}, {'item': 'minecraft:wheat'}, ""),
    ShapedRecipe([' b ', 'yyy', ' b '], {'id': 'xercafood:daisy_sandwich', 'count': 3}, {'b': {'item': 'minecraft:bread'}, 'y': {'item': 'minecraft:oxeye_daisy'}}, {'item': 'minecraft:oxeye_daisy'}, ""),
    ShapedRecipe(['sws', 'wxw', 'sws'], {'id': 'xercafood:donut', 'count': 4}, {'s': {'item': 'minecraft:sugar'}, 'w': {'item': 'minecraft:wheat'}, 'x': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:wheat'}, ""),
    ShapedRecipe(['s', 'c'], {'id': 'xercafood:sweet_berry_cupcake_fancy'}, {'s': {'item': 'xercafood:sprinkles'}, 'c': {'item': 'xercafood:sweet_berry_cupcake'}}, {'item': 'xercafood:sprinkles'}, "fancy_cupcake"),
    ShapedRecipe(['s', 'c'], {'id': 'xercafood:fancy_apple_cupcake'}, {'s': {'item': 'xercafood:sprinkles'}, 'c': {'item': 'xercafood:apple_cupcake'}}, {'item': 'xercafood:sprinkles'}, "fancy_cupcake"),
    ShapedRecipe(['s', 'd'], {'id': 'xercafood:fancy_donut'}, {'s': {'item': 'xercafood:sprinkles'}, 'd': {'item': 'xercafood:donut'}}, {'item': 'xercafood:sprinkles'}, ""),
    ShapedRecipe(['s', 'c'], {'id': 'xercafood:fancy_pumpkin_cupcake'}, {'s': {'item': 'xercafood:sprinkles'}, 'c': {'item': 'xercafood:pumpkin_cupcake'}}, {'item': 'xercafood:sprinkles'}, "fancy_cupcake"),
    ShapedRecipe(['b', 'h', 'b'], {'id': 'xercafood:fish_bread', 'count': 2}, {'b': {'item': 'minecraft:bread'}, 'h': {'item': 'minecraft:cooked_cod'}}, {'item': 'minecraft:cooked_cod'}, ""),
    ShapedRecipe(['c c', ' c '], {'id': 'xercafood:glass', 'count': 3}, {'c': {'item': 'minecraft:glass_pane'}}, {'item': 'minecraft:glass_pane'}, ""),
    ShapedRecipe(['nan', 'mem', 'www'], {'id': 'xercafood:golden_cupcake'}, {'a': {'item': 'minecraft:golden_apple', 'data': 0}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}, 'n': {'item': 'minecraft:gold_nugget'}}, None, ""),
    ShapedRecipe([' b ', 'dh ', ' b '], {'id': 'xercafood:hamburger'}, {'b': {'item': 'xercafood:bun'}, 'd': {'item': 'xercafood:tomato_slices'}, 'h': {'item': 'xercafood:cooked_patty'}}, {'item': 'xercafood:bun'}, ""),
    ShapedRecipe([' b ', 'dhc', ' b '], {'id': 'xercafood:cheeseburger'}, {'b': {'item': 'xercafood:bun'}, 'd': {'item': 'xercafood:tomato_slices'}, 'h': {'item': 'xercafood:cooked_patty'}, 'c': {'item': 'xercafood:cheese_slice'}}, {'item': 'xercafood:bun'}, ""),
    ShapedRecipe(['b', 'p', 'b'], {'id': 'xercafood:hotdog'}, {'p': {'item': 'xercafood:cooked_sausage'}, 'b': {'item': 'xercafood:bun'}}, {'item': 'xercafood:bun'}, ""),
    ShapedRecipe(['i', 's'], {'item': 'xercafood:knife'}, {'s': {'item': 'minecraft:stick'}, 'i': {'item': 'minecraft:iron_ingot'}}, {'item': 'minecraft:iron_ingot'}, "", load_conditions=xercatools_not_loaded_condition),
    ShapedRecipe(['sps', 'mem', 'www'], {'id': 'xercafood:melon_cupcake', 'count': 6}, {'p': {'item': 'minecraft:melon'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:melon'}, "cupcake"),
    ShapedRecipe([' b ', 'dmn', ' b '], {'id': 'xercafood:mushroom_burger'}, {'b': {'item': 'xercafood:bun'}, 'd': {'item': 'xercafood:tomato_slices'}, 'm': {'item': 'minecraft:brown_mushroom'}, 'n': {'item': 'minecraft:red_mushroom'}}, {'item': 'xercafood:bun'}, ""),
    ShapedRecipe(['sps', 'mem', 'www'], {'id': 'xercafood:pumpkin_cupcake', 'count': 6}, {'p': {'item': 'minecraft:pumpkin'}, 's': {'item': 'minecraft:sugar'}, 'e': {'item': 'minecraft:egg'}, 'w': {'item': 'minecraft:wheat'}, 'm': {'item': 'minecraft:milk_bucket'}}, {'item': 'minecraft:pumpkin'}, "cupcake"),
    ShapedRecipe([' w ', 'wcw', ' w '], {'id': 'xercafood:raw_schnitzel', 'count': 2}, {'c': {'item': 'minecraft:chicken'}, 'w': {'item': 'minecraft:wheat'}}, {'item': 'minecraft:chicken'}, ""),
    ShapedRecipe(['b', 'r', 'b'], {'id': 'xercafood:rotten_burger'}, {'b': {'item': 'xercafood:bun'}, 'r': {'item': 'minecraft:rotten_flesh'}}, {'item': 'xercafood:bun'}, ""),
    ShapedRecipe(['c c', ' c '], {'id': 'xercafood:teacup', 'count': 2}, {'c': {'item': 'minecraft:clay_ball'}}, {'item': 'minecraft:clay_ball'}, ""),
    ShapedRecipe([' c ', 'c c', 'ccc'], {'id': 'xercafood:teapot'}, {'c': {'item': 'minecraft:clay_ball'}}, {'item': 'minecraft:clay_ball'}, ""),
    ShapedRecipe(['mnm', ' p ', ' b '], {'id': 'xercafood:ultimate_bottom'}, {'p': {'item': 'xercafood:cooked_patty'}, 'b': {'item': 'xercafood:bun'}, 'm': {'item': 'minecraft:brown_mushroom'}, 'n': {'item': 'minecraft:red_mushroom'}}, {'item': 'xercafood:bun'}, "ultimate_bottom"),
    ShapedRecipe(['nmn', ' p ', ' b '], {'id': 'xercafood:ultimate_bottom'}, {'p': {'item': 'xercafood:cooked_patty'}, 'b': {'item': 'xercafood:bun'}, 'm': {'item': 'minecraft:brown_mushroom'}, 'n': {'item': 'minecraft:red_mushroom'}}, {'item': 'xercafood:bun'}, "ultimate_bottom"),
    ShapedRecipe([' t ', ' e ', ' b '], {'id': 'xercafood:ultimate_burger'}, {'b': {'item': 'xercafood:ultimate_bottom'}, 't': {'item': 'xercafood:ultimate_top'}, 'e': {'item': 'xercafood:bun'}}, {'item': 'xercafood:bun'}, ""),
    ShapedRecipe([' b ', 'tst', ' c '], {'id': 'xercafood:ultimate_top'}, {'b': {'item': 'xercafood:bun'}, 'c': {'item': 'xercafood:cooked_chicken_patty'}, 't': {'item': 'xercafood:tomato_slices'}, 's': {'item': 'xercafood:cheese_slice'}}, {'item': 'xercafood:bun'}, ""),
    ShapedRecipe([' wd', 'wdw', 'dw '], {'id': 'xercafood:doner_wrap', 'count': 4}, {'d': {'item': 'xercafood:doner_slice'}, 'w': {'item': 'minecraft:wheat'}}, {'item': 'xercafood:doner_slice'}, ""),
    ShapedRecipe([' b ', 'tdp', ' b '], {'id': 'xercafood:chubby_doner'}, {'b': {'item': 'xercafood:bun'}, 't': {'item': 'xercafood:tomato_slices'}, 'd': {'item': 'xercafood:doner_slice'}, 'p': {'item': 'xercafood:potato_fries'}}, {'item': 'xercafood:doner_slice'}, ""),

    ShapedRecipe(['c c', 'ccc', 'i i'], {'id': 'xercafood:vat'},  {'c': {'item': 'minecraft:copper_ingot'}, 'i': {'item': 'minecraft:iron_ingot'}}, {'item': 'minecraft:copper_ingot'}, ""),
    ShapedRecipe(['ct ', 'www'], {'id': 'xercafood:raw_pizza'},  {'w': {'item': 'minecraft:wheat'}, 'c': {'item': 'xercafood:cheese_slice'}, 't': {'item': 'xercafood:tomato_slices'}}, {'item': 'xercafood:cheese_slice'}, "pizza_base"),
    ShapedRecipe(['tc ', 'www'], {'id': 'xercafood:raw_pizza'},  {'w': {'item': 'minecraft:wheat'}, 'c': {'item': 'xercafood:cheese_slice'}, 't': {'item': 'xercafood:tomato_slices'}}, {'item': 'xercafood:cheese_slice'}, "pizza_base"),
    ShapedRecipe(['b', 'c', 'b'], {'id': 'xercafood:cheese_toast'},  {'b': {'item': 'xercafood:bun'}, 'c': {'item': 'xercafood:cheese_slice'}}, {'item': 'xercafood:cheese_slice'}, ""),
    ShapedRecipe(['ses', 'www', 'www'], {'id': 'xercafood:cola', 'count': 6},  {'w': {'item': 'xercafood:carbonated_water'}, 'e': {'item': 'xercafood:cola_extract'}, 's': {'item': 'minecraft:snowball'}}, {'item': 'xercafood:carbonated_water'}, ""),
]

shapeless_recipes = [
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'xercafood:yoghurt'}], {'item': 'xercafood:ayran'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'minecraft:sweet_berries'}], {'item': 'xercafood:sweet_berry_juice'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'minecraft:apple'}], {'item': 'xercafood:apple_juice'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'minecraft:bread'}, {'tag': 'c:tools/knife'}], {'item': 'xercafood:bun', 'count': 2}, {'item': 'minecraft:bread'}),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'minecraft:carrot'}], {'item': 'xercafood:carrot_juice'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'minecraft:milk_bucket'}], {'item': 'xercafood:glass_of_milk'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'minecraft:water_bucket'}], {'item': 'xercafood:glass_of_water'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'xercafood:tea_dried'}, {'item': 'xercafood:glass_of_water'}, {'item': 'minecraft:snowball'}, {'item': 'minecraft:sugar'}], {'item': 'xercafood:ice_tea'}, {'item': 'xercafood:glass_of_water'}, ""),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'minecraft:melon'}], {'item': 'xercafood:melon_juice'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'minecraft:potato'}, {'tag': 'c:tools/knife'}], {'item': 'xercafood:potato_slices', 'count': 2}, {'item': 'minecraft:potato'}),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'minecraft:pumpkin'}], {'item': 'xercafood:pumpkin_juice'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'minecraft:chicken'}, {'tag': 'c:tools/knife'}], {'item': 'xercafood:raw_chicken_patty', 'count': 2}, {'item': 'minecraft:chicken'}),
    ShapelessRecipe([{'item': 'minecraft:beef'}, {'tag': 'c:tools/knife'}], {'item': 'xercafood:raw_patty', 'count': 2}, {'item': 'minecraft:beef'}),
    ShapelessRecipe([{'item': 'minecraft:porkchop'}, {'tag': 'c:tools/knife'}], {'item': 'xercafood:raw_sausage', 'count': 2}, {'item': 'minecraft:porkchop'}),
    ShapelessRecipe([{'item': 'minecraft:sugar'}, {'item': 'minecraft:sugar'}, {'item': 'minecraft:sugar'}, {'item': 'minecraft:cyan_dye'}, {'item': 'minecraft:magenta_dye'}, {'item': 'minecraft:yellow_dye'}], {'item': 'xercafood:sprinkles', 'count': 12}, {'item': 'minecraft:sugar'}),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'xercafood:tomato'}], {'item': 'xercafood:tomato_juice'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'xercafood:tomato'}], {'item': 'xercafood:tomato_seeds'}, {'item': 'xercafood:tomato'}),
    ShapelessRecipe([{'item': 'xercafood:tomato'}, {'tag': 'c:tools/knife'}], {'item': 'xercafood:tomato_slices', 'count': 3}, {'item': 'xercafood:tomato'}),
    ShapelessRecipe([{'item': 'xercafood:cheese_wheel'}, {'tag': 'c:tools/knife'}], {'item': 'xercafood:cheese_slice', 'count': 4}, {'item': 'xercafood:cheese_wheel'}),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'minecraft:wheat'}, {'item': 'minecraft:wheat'}], {'item': 'xercafood:wheat_juice'}, {'item': 'xercafood:glass'}),
    ShapelessRecipe([{'item': 'xercafood:carbonated_water'}, {'item': 'minecraft:sugar'}, {'item': 'minecraft:snowball'}], {'item': 'xercafood:soda'}, {'item': 'xercafood:carbonated_water'}),
    ShapelessRecipe([{'item': 'minecraft:warped_roots'}, {'item': 'minecraft:rotten_flesh'}, {'item': 'minecraft:spider_eye'}, {'item': 'minecraft:sugar'}, {'item': 'minecraft:sugar'}, {'item': 'minecraft:sugar'}], {'item': 'xercafood:cola_powder'}, {'item': 'minecraft:spider_eye'}),

    ShapelessRecipe([{'tag': 'c:tools/knife'}, {'item': 'minecraft:salmon'}], {'item': 'xercafood:sashimi', 'count': 2}, {'item': 'xercafood:cooked_rice'}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'minecraft:dried_kelp'}], {'item': 'xercafood:riceball', 'count': 2}, {'item': 'xercafood:cooked_rice'}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'minecraft:dried_kelp'}, {'item': 'xercafood:sashimi'}], {'item': 'xercafood:sushi', 'count': 2}, {'item': 'xercafood:cooked_rice'}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'xercafood:sashimi'}], {'item': 'xercafood:nigiri_sushi'}, {'item': 'xercafood:cooked_rice', 'count': 2}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'xercafood:fried_egg'}], {'item': 'xercafood:omurice'}, {'item': 'xercafood:cooked_rice'}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'xercafood:fried_egg'}, {'item': 'minecraft:dried_kelp'}], {'item': 'xercafood:egg_sushi', 'count': 2}, {'item': 'xercafood:cooked_rice'}),
    ShapelessRecipe([{'item': 'xercafood:glass'}, {'item': 'xercafood:rice_seeds'}, {'item': 'xercafood:rice_seeds'}], {'item': 'xercafood:sake'}, {'item': 'xercafood:rice_seeds'}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'minecraft:cooked_cod'}, {'item': 'minecraft:glow_ink_sac'}, {'item': 'minecraft:kelp'}, {'item': 'minecraft:bowl'}], {'item': 'xercafood:glow_squid_ink_paella'}, {'item': 'xercafood:cooked_rice'}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'minecraft:cooked_cod'}, {'item': 'minecraft:ink_sac'}, {'item': 'minecraft:kelp'}, {'item': 'minecraft:bowl'}], {'item': 'xercafood:squid_ink_paella'}, {'item': 'xercafood:cooked_rice'}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'minecraft:cooked_beef'}, {'item': 'minecraft:bowl'}], {'item': 'xercafood:beef_donburi'}, {'item': 'xercafood:cooked_rice'}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'minecraft:cooked_chicken'}, {'item': 'minecraft:bowl'}, {'item': 'xercafood:fried_egg'}], {'item': 'xercafood:oyakodon'}, {'item': 'xercafood:cooked_rice'}),
    ShapelessRecipe([{'item': 'xercafood:cooked_rice'}, {'item': 'minecraft:sugar'}, {'item': 'minecraft:bowl'}, {'item': 'minecraft:milk_bucket'}], {'item': 'xercafood:rice_pudding'}, {'item': 'xercafood:rice_seeds'}),
    ShapelessRecipe([{'item': 'xercafood:doner_slice'}, {'item': 'xercafood:yoghurt'}, {'item': 'xercafood:tomato_slices'}, {'item': 'minecraft:bread'}, {'item': 'minecraft:bowl'}], {'item': 'xercafood:alexander'}, {'item': 'xercafood:doner_slice'}),
    ShapelessRecipe([{'item': 'minecraft:sweet_berries'}, {'item': 'xercafood:yoghurt'}, {'item': 'minecraft:honey_bottle'}, {'item': 'minecraft:bowl'}], {'item': 'xercafood:honeyberry_yoghurt'}, {'item': 'xercafood:yoghurt'}),
    ShapelessRecipe([{'item': 'minecraft:mutton'}, {'item': 'minecraft:stick'}, {'item': 'xercafood:tomato_slices'}], {'item': 'xercafood:raw_shish_kebab'}, {'item': 'minecraft:mutton'}),

    # PIZZA RECIPES START
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:raw_sausage"}, {"item": "xercafood:raw_sausage"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_pepperoni_pepperoni_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:brown_mushroom"}, {"item": "xercafood:raw_sausage"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_mushroom_pepperoni_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:brown_mushroom"}, {"item": "minecraft:brown_mushroom"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_mushroom_mushroom_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:brown_mushroom"}, {"item": "minecraft:brown_mushroom"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_mushroom_mushroom_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:raw_sausage"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_meat_pepperoni_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}, {"item": "minecraft:brown_mushroom"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_meat_mushroom_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}, {"item": "minecraft:brown_mushroom"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_meat_mushroom_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_meat_meat_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:doner_slice"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_meat_meat_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_meat_meat_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "xercafood:raw_sausage"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_fish_pepperoni_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "minecraft:brown_mushroom"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_fish_mushroom_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "minecraft:brown_mushroom"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_fish_mushroom_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_fish_meat_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "xercafood:doner_slice"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_fish_meat_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_fish_meat_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "minecraft:cod"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_fish_fish_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "minecraft:cod"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_fish_fish_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "minecraft:cod"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_fish_fish_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "minecraft:cod"}, {"item": "minecraft:cod"}], {"item": "xercafood:raw_pizza_fish_fish_fish"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "xercafood:raw_sausage"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_chicken_pepperoni_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:brown_mushroom"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_chicken_mushroom_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:brown_mushroom"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_chicken_mushroom_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_chicken_meat_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "xercafood:doner_slice"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_chicken_meat_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_chicken_meat_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:cod"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_chicken_fish_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:cod"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_chicken_fish_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:cod"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_chicken_fish_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:cod"}, {"item": "minecraft:cod"}], {"item": "xercafood:raw_pizza_chicken_fish_fish"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:chicken"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_chicken_chicken_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:chicken"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_chicken_chicken_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:chicken"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_chicken_chicken_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:chicken"}, {"item": "minecraft:cod"}], {"item": "xercafood:raw_pizza_chicken_chicken_fish"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:chicken"}, {"item": "minecraft:chicken"}], {"item": "xercafood:raw_pizza_chicken_chicken_chicken"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:raw_sausage"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_pepperoni_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:brown_mushroom"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_mushroom_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:brown_mushroom"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_mushroom_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_meat_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_meat_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_meat_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_fish_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_fish_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_fish_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}, {"item": "minecraft:cod"}], {"item": "xercafood:raw_pizza_fish_fish"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_chicken_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_chicken_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_chicken_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:cod"}], {"item": "xercafood:raw_pizza_chicken_fish"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}, {"item": "minecraft:chicken"}], {"item": "xercafood:raw_pizza_chicken_chicken"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:raw_sausage"}], {"item": "xercafood:raw_pizza_pepperoni"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:brown_mushroom"}], {"item": "xercafood:raw_pizza_mushroom"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "xercafood:doner_slice"}], {"item": "xercafood:raw_pizza_meat"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:cod"}], {"item": "xercafood:raw_pizza_fish"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    ShapelessRecipe([{"item": "xercafood:raw_pizza"}, {"item": "minecraft:chicken"}], {"item": "xercafood:raw_pizza_chicken"}, {"item": "xercafood:raw_pizza"}, "pizza"),
    # PIZZA RECIPES END
]

cooking_recipes = [
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:rice_pudding'}, "xercafood:baked_rice_pudding", 0.2, 250, {'item': 'xercafood:rice_pudding'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:raw_chicken_patty'}, "xercafood:cooked_chicken_patty", 0.2, 200, {'item': 'xercafood:raw_chicken_patty'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'minecraft:egg'}, "xercafood:fried_egg", 0.2, 200, {'item': 'minecraft:egg'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:raw_patty'}, "xercafood:cooked_patty", 0.2, 200, {'item': 'xercafood:raw_patty'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:potato_slices'}, "xercafood:potato_fries", 0.2, 200, {'item': 'xercafood:potato_slices'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:raw_sausage'}, "xercafood:cooked_sausage", 0.2, 200, {'item': 'xercafood:raw_sausage'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:raw_schnitzel'}, "xercafood:cooked_schnitzel", 0.2, 200, {'item': 'xercafood:raw_schnitzel'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:tea_leaf'}, "xercafood:tea_dried", 0.1, 200, {'item': 'xercafood:tea_leaf'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:full_teapot_1'}, "xercafood:hot_teapot_1", 0.1, 100, {'item': 'xercafood:full_teapot_1'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:full_teapot_2'}, "xercafood:hot_teapot_2", 0.15, 150, {'item': 'xercafood:full_teapot_2'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:full_teapot_3'}, "xercafood:hot_teapot_3", 0.2, 200, {'item': 'xercafood:full_teapot_3'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:full_teapot_4'}, "xercafood:hot_teapot_4", 0.25, 250, {'item': 'xercafood:full_teapot_4'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:full_teapot_5'}, "xercafood:hot_teapot_5", 0.3, 300, {'item': 'xercafood:full_teapot_5'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:full_teapot_6'}, "xercafood:hot_teapot_6", 0.35, 350, {'item': 'xercafood:full_teapot_6'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:full_teapot_7'}, "xercafood:hot_teapot_7", 0.4, 400, {'item': 'xercafood:full_teapot_7'}, "hot_teapot"),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:rice_seeds'}, "xercafood:cooked_rice", 0.2, 200, {'item': 'xercafood:rice_seeds'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'minecraft:milk_bucket'}, "xercafood:yoghurt", 0.2, 200, {'item': 'minecraft:milk_bucket'}, ""),
    CookingRecipe("minecraft:smelting", {'item': 'xercafood:raw_shish_kebab'}, "xercafood:shish_kebab", 0.2, 200, {'item': 'xercafood:raw_shish_kebab'}, ""),

    # PIZZA COOKING RECIPES START
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_pepperoni_pepperoni_pepperoni"}, "xercafood:pizza_pepperoni_pepperoni_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_pepperoni_pepperoni_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_mushroom_pepperoni_pepperoni"}, "xercafood:pizza_mushroom_pepperoni_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_mushroom_pepperoni_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_mushroom_mushroom_pepperoni"}, "xercafood:pizza_mushroom_mushroom_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_mushroom_mushroom_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_mushroom_mushroom_mushroom"}, "xercafood:pizza_mushroom_mushroom_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_mushroom_mushroom_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat_pepperoni_pepperoni"}, "xercafood:pizza_meat_pepperoni_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_meat_pepperoni_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat_mushroom_pepperoni"}, "xercafood:pizza_meat_mushroom_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_meat_mushroom_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat_mushroom_mushroom"}, "xercafood:pizza_meat_mushroom_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_meat_mushroom_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat_meat_pepperoni"}, "xercafood:pizza_meat_meat_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_meat_meat_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat_meat_mushroom"}, "xercafood:pizza_meat_meat_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_meat_meat_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat_meat_meat"}, "xercafood:pizza_meat_meat_meat", 0.2, 300, {"item": "xercafood:raw_pizza_meat_meat_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_pepperoni_pepperoni"}, "xercafood:pizza_fish_pepperoni_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_fish_pepperoni_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_mushroom_pepperoni"}, "xercafood:pizza_fish_mushroom_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_fish_mushroom_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_mushroom_mushroom"}, "xercafood:pizza_fish_mushroom_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_fish_mushroom_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_meat_pepperoni"}, "xercafood:pizza_fish_meat_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_fish_meat_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_meat_mushroom"}, "xercafood:pizza_fish_meat_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_fish_meat_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_meat_meat"}, "xercafood:pizza_fish_meat_meat", 0.2, 300, {"item": "xercafood:raw_pizza_fish_meat_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_fish_pepperoni"}, "xercafood:pizza_fish_fish_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_fish_fish_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_fish_mushroom"}, "xercafood:pizza_fish_fish_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_fish_fish_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_fish_meat"}, "xercafood:pizza_fish_fish_meat", 0.2, 300, {"item": "xercafood:raw_pizza_fish_fish_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_fish_fish"}, "xercafood:pizza_fish_fish_fish", 0.2, 300, {"item": "xercafood:raw_pizza_fish_fish_fish"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_pepperoni_pepperoni"}, "xercafood:pizza_chicken_pepperoni_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_pepperoni_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_mushroom_pepperoni"}, "xercafood:pizza_chicken_mushroom_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_mushroom_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_mushroom_mushroom"}, "xercafood:pizza_chicken_mushroom_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_mushroom_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_meat_pepperoni"}, "xercafood:pizza_chicken_meat_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_meat_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_meat_mushroom"}, "xercafood:pizza_chicken_meat_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_meat_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_meat_meat"}, "xercafood:pizza_chicken_meat_meat", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_meat_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_fish_pepperoni"}, "xercafood:pizza_chicken_fish_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_fish_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_fish_mushroom"}, "xercafood:pizza_chicken_fish_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_fish_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_fish_meat"}, "xercafood:pizza_chicken_fish_meat", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_fish_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_fish_fish"}, "xercafood:pizza_chicken_fish_fish", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_fish_fish"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_chicken_pepperoni"}, "xercafood:pizza_chicken_chicken_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_chicken_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_chicken_mushroom"}, "xercafood:pizza_chicken_chicken_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_chicken_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_chicken_meat"}, "xercafood:pizza_chicken_chicken_meat", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_chicken_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_chicken_fish"}, "xercafood:pizza_chicken_chicken_fish", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_chicken_fish"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_chicken_chicken"}, "xercafood:pizza_chicken_chicken_chicken", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_chicken_chicken"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_pepperoni_pepperoni"}, "xercafood:pizza_pepperoni_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_pepperoni_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_mushroom_pepperoni"}, "xercafood:pizza_mushroom_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_mushroom_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_mushroom_mushroom"}, "xercafood:pizza_mushroom_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_mushroom_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat_pepperoni"}, "xercafood:pizza_meat_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_meat_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat_mushroom"}, "xercafood:pizza_meat_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_meat_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat_meat"}, "xercafood:pizza_meat_meat", 0.2, 300, {"item": "xercafood:raw_pizza_meat_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_pepperoni"}, "xercafood:pizza_fish_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_fish_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_mushroom"}, "xercafood:pizza_fish_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_fish_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_meat"}, "xercafood:pizza_fish_meat", 0.2, 300, {"item": "xercafood:raw_pizza_fish_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish_fish"}, "xercafood:pizza_fish_fish", 0.2, 300, {"item": "xercafood:raw_pizza_fish_fish"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_pepperoni"}, "xercafood:pizza_chicken_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_mushroom"}, "xercafood:pizza_chicken_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_meat"}, "xercafood:pizza_chicken_meat", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_fish"}, "xercafood:pizza_chicken_fish", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_fish"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken_chicken"}, "xercafood:pizza_chicken_chicken", 0.2, 300, {"item": "xercafood:raw_pizza_chicken_chicken"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_pepperoni"}, "xercafood:pizza_pepperoni", 0.2, 300, {"item": "xercafood:raw_pizza_pepperoni"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_mushroom"}, "xercafood:pizza_mushroom", 0.2, 300, {"item": "xercafood:raw_pizza_mushroom"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_meat"}, "xercafood:pizza_meat", 0.2, 300, {"item": "xercafood:raw_pizza_meat"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_fish"}, "xercafood:pizza_fish", 0.2, 300, {"item": "xercafood:raw_pizza_fish"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza_chicken"}, "xercafood:pizza_chicken", 0.2, 300, {"item": "xercafood:raw_pizza_chicken"}, ""),
    CookingRecipe("minecraft:smelting", {"item": "xercafood:raw_pizza"}, "xercafood:pizza", 0.2, 300, {"item": "xercafood:raw_pizza"}, ""),
    # PIZZA COOKING RECIPES END
]

special_recipes = [
    SpecialRecipe("xercafood:crafting_special_tea_filling", None, ""),
    SpecialRecipe("xercafood:crafting_special_tea_pouring", None, ""),
    SpecialRecipe("xercafood:crafting_special_tea_refilling", None, ""),
    SpecialRecipe("xercafood:crafting_special_tea_sugaring", None, ""),
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
    smoker_recipe.cooking_time = int(smoker_recipe.cooking_time * 0.5)
    smoker_recipes.append(smoker_recipe)

all_recipes = [r for l in [shaped_recipes, shapeless_recipes, cooking_recipes, special_recipes, campfire_recipes, smoker_recipes] for r in l]


# generate_recipe_code_from_files("xercafood")
clean_recipe_jsons("xercafood")
generate_recipe_jsons(all_recipes, "xercafood")
