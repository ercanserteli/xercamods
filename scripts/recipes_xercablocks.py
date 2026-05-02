from recipe_generator import *

colors = ["black", "blue", "brown", "cyan", "gray", "green", "light_blue", "light_gray",
          "lime", "magenta", "orange", "pink", "purple", "red", "white", "yellow", ""]

shaped_recipes = [
    ShapedRecipe(['www', '   ', 'www'], {'id': 'xercablocks:bookcase'}, {'w': {'tag': 'minecraft:planks'}}, {'tag': 'minecraft:planks'}, "", "bookcase"),
    ShapedRecipe(['kk', 'pp', 'pp'], {'id': 'xercablocks:carving_station'}, {'p': {'tag': 'minecraft:planks'}, 'k': {'item': 'minecraft:shears'}}, {'item': 'minecraft:shears'}, ""),
    ShapedRecipe(['lll', 'lll', 'lll'], {'id': 'xercablocks:block_leather'}, {'l': {'item': 'minecraft:leather'}}, {'item': 'minecraft:leather'}, "", "leather_straw"),
    ShapedRecipe(['lll', 'lll', 'lll'], {'id': 'xercablocks:block_straw'}, {'l': {'item': 'minecraft:sugar_cane'}}, {'item': 'minecraft:sugar_cane'}, "", "leather_straw"),
]

shapeless_recipes = [
    ShapelessRecipe([{'item': 'xercablocks:block_leather'}], {'id': 'minecraft:leather', 'count': 9}, {'item': 'xercablocks:block_leather'}, "", "leather_straw"),
    ShapelessRecipe([{'item': 'xercablocks:block_straw'}], {'id': 'minecraft:sugar_cane', 'count': 9}, {'item': 'xercablocks:block_straw'}, "", "leather_straw"),
    ShapelessRecipe([{'item': 'minecraft:string'}, {'item': 'minecraft:string'}, {'item': 'minecraft:string'}], {'id': 'xercablocks:rope'}, {'item': 'minecraft:string'}, "", "rope"),
]

for c in colors:
    prefix = f"{c}_" if c else ""
    mc_terracotta = f"minecraft:{c}_terracotta" if c else "minecraft:terracotta"
    shaped_recipes.append(ShapedRecipe(['tt', 'tt'], {'id': f'xercablocks:{prefix}terratile', 'count': 4}, {'t': {'item': mc_terracotta}}, {'item': mc_terracotta}, "terratile", "terracotta_tile"))
    shaped_recipes.append(ShapedRecipe(['t  ', 'tt ', 'ttt'], {'id': f'xercablocks:{prefix}terratile_stairs', 'count': 4}, {'t': {'item': f'xercablocks:{prefix}terratile'}}, {'item': f'xercablocks:{prefix}terratile'}, "", "terracotta_tile"))
    shaped_recipes.append(ShapedRecipe(['ttt'], {'id': f'xercablocks:{prefix}terratile_slab', 'count': 6}, {'t': {'item': f'xercablocks:{prefix}terratile'}}, {'item': f'xercablocks:{prefix}terratile'}, "", "terracotta_tile"))

stonecutting_recipes = []
for c in colors:
    prefix = f"{c}_" if c else ""
    mc_terracotta = f"minecraft:{c}_terracotta" if c else "minecraft:terracotta"
    stonecutting_recipes.append(StonecuttingRecipe(mc_terracotta, f"xercablocks:{prefix}terratile", 1, "", "terracotta_tile"))
    stonecutting_recipes.append(StonecuttingRecipe(f"xercablocks:{prefix}terratile", f"xercablocks:{prefix}terratile_slab", 2, "", "terracotta_tile"))
    stonecutting_recipes.append(StonecuttingRecipe(f"xercablocks:{prefix}terratile", f"xercablocks:{prefix}terratile_stairs", 1, "", "terracotta_tile"))

wood_types = [
    ("oak", "log"),
    ("birch", "log"),
    ("spruce", "log"),
    ("jungle", "log"),
    ("acacia", "log"),
    ("dark_oak", "log"),
    ("crimson", "stem"),
    ("warped", "stem"),
]

carving_recipes = []
for wood, log_type in wood_types:
    log = f"minecraft:{wood}_{log_type}"
    stripped = f"minecraft:stripped_{wood}_{log_type}"
    carving_recipes.append(CarvingRecipe(log, stripped, 1, "", "carving"))
    for i in range(1, 9):
        carving_recipes.append(CarvingRecipe(log, f"xercablocks:carved_{wood}_{i}", 1, "", "carving"))
        carving_recipes.append(CarvingRecipe(stripped, f"xercablocks:carved_{wood}_{i}", 1, "", "carving"))

carving_recipes.append(CarvingRecipe("minecraft:cherry_log", "minecraft:stripped_cherry_log", 1, "", "carving"))

all_recipes = [r for lst in [shaped_recipes, shapeless_recipes, stonecutting_recipes, carving_recipes] for r in lst]

clean_recipe_jsons("xercablocks")
generate_recipe_jsons(all_recipes, "xercablocks")
