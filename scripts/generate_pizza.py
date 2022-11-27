ingredients = ["chicken", "fish", "meat", "mushroom", "pepperoni"]

item_ids = {
    "chicken": "minecraft:chicken",
    "fish": "minecraft:cod",
    "meat": "xercafood:doner_slice",
    "mushroom": "minecraft:brown_mushroom",
    "pepperoni": "xercafood:item_raw_sausage",
}


def itn(ingredient):
    return "" if ingredient == "empty" else "_" + ingredient


class Pizza:
    def __init__(self, slot1=None, slot2=None, slot3=None):
        self.slot1 = slot1 if slot1 else "empty"
        self.slot2 = slot2 if slot2 else "empty"
        self.slot3 = slot3 if slot3 else "empty"

    def item_model_json(self):
        return """
{
    "parent": "item/generated",
    "textures": {
        "layer0": "xercafood:item/pizza/pizza_generic"
    }
}
"""

    def raw_item_model_json(self):
        return """
{
    "parent": "item/generated",
    "textures": {
        "layer0": "xercafood:item/pizza/pizza_raw"
    }
}
"""

    def block_model_json(self, bites=0):
        if bites == 0:
            return """
{{
    "textures": {{
        "bottom": "xercafood:block/pizza/pizza_base_bottom",
        "top": "xercafood:block/pizza/pizza_base_top",
        "side": "xercafood:block/pizza/pizza_outside_inside",
		"particle": "xercafood:block/pizza/pizza_base_top",
        "slot1": "xercafood:block/pizza/pizza_slot1_{}",
        "slot2": "xercafood:block/pizza/pizza_slot2_{}",
        "slot3": "xercafood:block/pizza/pizza_slot3_{}"
    }},
    "elements": [
        {{
            "from": [1, 0, 1],
            "to": [15, 2, 15],
            "faces": {{
                "north": {{"uv": [1, 9, 15, 11], "texture": "#side"}},
                "east": {{"uv": [1, 9, 15, 11], "texture": "#side"}},
                "south": {{"uv": [1, 9, 15, 11], "texture": "#side"}},
                "west": {{"uv": [1, 9, 15, 11], "texture": "#side"}},
                "up": {{"uv": [1, 1, 15, 15], "texture": "#top"}},
                "down": {{"uv": [1, 1, 15, 15], "texture": "#bottom"}}
            }}
        }},
        {{
            "name": "slot1",
            "from": [2, 2.01, 2],
            "to": [14, 2.01, 14],
            "color": 2,
            "faces": {{
                "up": {{"uv": [2, 2, 14, 14], "texture": "#slot1"}}
            }}
        }},
        {{
            "name": "slot2",
            "from": [2, 2.02, 2],
            "to": [14, 2.02, 14],
            "color": 2,
            "faces": {{
                "up": {{"uv": [2, 2, 14, 14], "texture": "#slot2"}}
            }}
        }},
        {{
            "name": "slot3",
            "from": [2, 2.03, 2],
            "to": [14, 2.03, 14],
            "faces": {{
                "up": {{"uv": [2, 2, 14, 14], "texture": "#slot3"}}
            }}
        }}
    ]
}}
""".format(self.slot1, self.slot2, self.slot3)
        elif bites == 1:
            return """
{{
	"textures": {{
		"bottom": "xercafood:block/pizza/pizza_base_bottom",
		"top": "xercafood:block/pizza/pizza_base_top",
		"side": "xercafood:block/pizza/pizza_outside_inside",
		"particle": "xercafood:block/pizza/pizza_base_top",
		"slot1": "xercafood:block/pizza/pizza_slot1_{}",
		"slot2": "xercafood:block/pizza/pizza_slot2_{}",
		"slot3": "xercafood:block/pizza/pizza_slot3_{}"
	}},
	"elements": [
		{{
			"from": [1, 0, 8],
			"to": [15, 2, 15],
			"faces": {{
				"north": {{"uv": [1, 4, 15, 6], "texture": "#side"}},
				"east": {{"uv": [7, 9, 15, 11], "texture": "#side"}},
				"south": {{"uv": [1, 9, 15, 11], "texture": "#side"}},
				"west": {{"uv": [8, 9, 15, 11], "texture": "#side"}},
				"up": {{"uv": [1, 8, 15, 15], "texture": "#top"}},
				"down": {{"uv": [1, 1, 15, 8], "texture": "#bottom"}}
			}}
		}},
		{{
			"from": [1, 0, 1],
			"to": [8, 2, 8],
			"faces": {{
				"north": {{"uv": [1, 9, 8, 11], "rotation": 180, "texture": "#side"}},
				"east": {{"uv": [8, 4, 15, 6], "texture": "#side"}},
				"west": {{"uv": [1, 9, 8, 11], "texture": "#side"}},
				"up": {{"uv": [1, 1, 8, 8], "texture": "#top"}},
				"down": {{"uv": [1, 8, 8, 15], "texture": "#bottom"}}
			}}
		}},
		{{
			"name": "slot1-1",
			"from": [2, 2.01, 2],
			"to": [8, 2.01, 14],
			"color": 4,
			"faces": {{
				"up": {{"uv": [2, 2, 8, 14], "texture": "#slot1"}}
			}}
		}},
		{{
			"name": "slot1-2",
			"from": [8, 2.01, 8],
			"to": [14, 2.01, 14],
			"color": 4,
			"faces": {{
				"up": {{"uv": [8, 8, 14, 14], "texture": "#slot1"}}
			}}
		}},
		{{
			"name": "slot2-1",
			"from": [2, 2.02, 2],
			"to": [8, 2.02, 14],
			"color": 4,
			"faces": {{
				"up": {{"uv": [2, 2, 8, 14], "texture": "#slot2"}}
			}}
		}},
		{{
			"name": "slot2-2",
			"from": [8, 2.02, 8],
			"to": [14, 2.02, 14],
			"color": 4,
			"faces": {{
				"up": {{"uv": [8, 8, 14, 14], "texture": "#slot2"}}
			}}
		}},
		{{
			"name": "slot3-1",
			"from": [2, 2.03, 2],
			"to": [8, 2.03, 14],
			"faces": {{
				"up": {{"uv": [2, 2, 8, 14], "texture": "#slot3"}}
			}}
		}},
		{{
			"name": "slot3-2",
			"from": [8, 2.03, 8],
			"to": [14, 2.03, 14],
			"faces": {{
				"up": {{"uv": [8, 8, 14, 14], "texture": "#slot3"}}
			}}
		}}
	]
}}
""".format(self.slot1, self.slot2, self.slot3)
        elif bites == 2:
            return """
{{
	"textures": {{
		"bottom": "xercafood:block/pizza/pizza_base_bottom",
		"top": "xercafood:block/pizza/pizza_base_top",
		"side": "xercafood:block/pizza/pizza_outside_inside",
		"particle": "xercafood:block/pizza/pizza_base_top",
		"slot1": "xercafood:block/pizza/pizza_slot1_{}",
		"slot2": "xercafood:block/pizza/pizza_slot2_{}",
		"slot3": "xercafood:block/pizza/pizza_slot3_{}"
	}},
	"elements": [
		{{
			"from": [1, 0, 8],
			"to": [15, 2, 15],
			"faces": {{
				"north": {{"uv": [1, 4, 15, 6], "texture": "#side"}},
				"east": {{"uv": [7, 9, 15, 11], "texture": "#side"}},
				"south": {{"uv": [1, 9, 15, 11], "texture": "#side"}},
				"west": {{"uv": [8, 9, 15, 11], "texture": "#side"}},
				"up": {{"uv": [1, 8, 15, 15], "texture": "#top"}},
				"down": {{"uv": [1, 1, 15, 8], "texture": "#bottom"}}
			}}
		}},
		{{
			"name": "slot1",
			"from": [2, 2.01, 8],
			"to": [14, 2.01, 14],
			"faces": {{
				"up": {{"uv": [2, 8, 14, 14], "texture": "#slot1"}}
			}}
		}},
		{{
			"name": "slot2",
			"from": [2, 2.02, 8],
			"to": [14, 2.02, 14],
			"faces": {{
				"up": {{"uv": [2, 8, 14, 14], "texture": "#slot2"}}
			}}
		}},
		{{
			"name": "slot3",
			"from": [2, 2.03, 8],
			"to": [14, 2.03, 14],
			"faces": {{
				"up": {{"uv": [2, 8, 14, 14], "texture": "#slot3"}}
			}}
		}}
	]
}}
""".format(self.slot1, self.slot2, self.slot3)
        elif bites == 3:
            return """
{{
	"textures": {{
		"bottom": "xercafood:block/pizza/pizza_base_bottom",
		"top": "xercafood:block/pizza/pizza_base_top",
		"side": "xercafood:block/pizza/pizza_outside_inside",
		"particle": "xercafood:block/pizza/pizza_base_top",
		"slot1": "xercafood:block/pizza/pizza_slot1_{}",
		"slot2": "xercafood:block/pizza/pizza_slot2_{}",
		"slot3": "xercafood:block/pizza/pizza_slot3_{}"
	}},
	"elements": [
		{{
			"from": [8, 0, 8],
			"to": [15, 2, 15],
			"faces": {{
				"north": {{"uv": [1, 4, 8, 6], "texture": "#side"}},
				"east": {{"uv": [1, 9, 8, 11], "texture": "#side"}},
				"south": {{"uv": [8, 9, 15, 11], "texture": "#side"}},
				"west": {{"uv": [8, 4, 15, 6], "texture": "#side"}},
				"up": {{"uv": [8, 8, 15, 15], "texture": "#top"}},
				"down": {{"uv": [7, 1, 15, 8], "texture": "#bottom"}}
			}}
		}},
		{{
			"name": "slot1",
			"from": [8, 2.01, 8],
			"to": [14, 2.01, 14],
			"faces": {{
				"up": {{"uv": [8, 8, 14, 14], "texture": "#slot1"}}
			}}
		}},
		{{
			"name": "slot2",
			"from": [8, 2.02, 8],
			"to": [14, 2.02, 14],
			"faces": {{
				"up": {{"uv": [8, 8, 14, 14], "texture": "#slot2"}}
			}}
		}},
		{{
			"name": "slot3",
			"from": [8, 2.03, 8],
			"to": [14, 2.03, 14],
			"faces": {{
				"up": {{"uv": [8, 8, 14, 14], "texture": "#slot3"}}
			}}
		}}
	]
}}
""".format(self.slot1, self.slot2, self.slot3)

    def blockstate_json(self):
        return """
{{
    "variants": {{
        "bites=0": {{ "model": "xercafood:block/pizza/{}" }},
        "bites=1": {{ "model": "xercafood:block/pizza/{}" }},
        "bites=2": {{ "model": "xercafood:block/pizza/{}" }},
        "bites=3": {{ "model": "xercafood:block/pizza/{}" }}
    }}
}}
""".format(self.model_fn(0), self.model_fn(1), self.model_fn(2), self.model_fn(3))

    def blocks_object_holder(self):
        return "    public static final BlockPizza PIZZA{} = null;".format(self.postfix().upper())

    def blocks_register(self):
        return "new BlockPizza(BlockPizza.Ingredient.{}, BlockPizza.Ingredient.{}, BlockPizza.Ingredient.{}),".format(
            self.slot1.upper(), self.slot2.upper(), self.slot3.upper())

    def items_register(self):
        return "new ItemPizza(Blocks.PIZZA{}, BlockPizza.Ingredient.{}, BlockPizza.Ingredient.{}, BlockPizza.Ingredient.{}),".format(
            self.postfix().upper(), self.slot1.upper(), self.slot2.upper(), self.slot3.upper())

    def items_raw_register(self):
        return "new ItemRawPizza(BlockPizza.Ingredient.{}, BlockPizza.Ingredient.{}, BlockPizza.Ingredient.{}, Foods.RAW_PIZZA_{}),".format(
            self.slot1.upper(), self.slot2.upper(), self.slot3.upper(), self.ingredient_count())

    def render_layer(self):
        return "ItemBlockRenderTypes.setRenderLayer(Blocks.PIZZA{}, RenderType.cutoutMipped());".format(self.postfix().upper())

    def recipe_gen(self):
        ingr_format = '{{"item": "{}"}}'
        ingrs = ''
        if self.slot1 != "empty":
            ingrs += ", " + ingr_format.format(item_ids[self.slot1])
        if self.slot2 != "empty":
            ingrs += ", " + ingr_format.format(item_ids[self.slot2])
        if self.slot3 != "empty":
            ingrs += ", " + ingr_format.format(item_ids[self.slot3])

        if not ingrs:
            return ""

        return 'ShapelessRecipe([{{"item": "xercafood:raw_pizza"}}{}], {{"item": "xercafood:raw_{}"}}, {{"item": "xercafood:raw_pizza"}}, "pizza", type="xercafood:crafting_condition_shapeless_food"),'.format(ingrs, self.base_name())

    def recipe_cooking_gen(self):
        return 'CookingRecipe("xercafood:crafting_condition_smelting_food", {{"item": "xercafood:raw_{name}"}}, "xercafood:{name}", 0.2, 300, {{"item": "xercafood:raw_{name}"}}, ""),'.format(name=self.base_name())

    def base_name(self):
        return "pizza{}".format(self.postfix())

    def model_fn(self, bites=0):
        if bites == 0:
            return "{}_uneaten".format(self.base_name())
        else:
            return "{}_slice{}".format(self.base_name(), bites)

    def ingredient_count(self):
        return (0 if self.slot1 == "empty" else 1) + (0 if self.slot2 == "empty" else 1) + (
            0 if self.slot3 == "empty" else 1)

    def postfix(self):
        return itn(self.slot1) + itn(self.slot2) + itn(self.slot3)

    def __str__(self):
        return self.slot1 + " " + self.slot2 + " " + self.slot3


pizzas = []

# No ingredients
pizzas.append(Pizza())

# 1 ingredient
for ing in ingredients:
    pizzas.append(Pizza(ing))

# 2 ingredients
for i, ing1 in enumerate(ingredients):
    for j, ing2 in enumerate(ingredients):
        if j >= i:
            pizzas.append(Pizza(ing1, ing2))

# 3 ingredients
for i, ing1 in enumerate(ingredients):
    for j, ing2 in enumerate(ingredients):
        for k, ing3 in enumerate(ingredients):
            if i <= j <= k:
                pizzas.append(Pizza(ing1, ing2, ing3))

print(len(pizzas), "pizzas")

item_models_dir = "../XercaMod/src/main/resources/assets/xercamod/models/item/"
block_models_dir = "../XercaMod/src/main/resources/assets/xercamod/models/block/pizza/"
blockstates_dir = "../XercaMod/src/main/resources/assets/xercamod/blockstates/"
blocks_java_path = "../XercaMod/src/main/java/xerca/xercamod/common/block/Blocks.java"
items_java_path = "../XercaMod/src/main/java/xerca/xercamod/common/item/Items.java"
client_stuff_java_path = "../XercaMod/src/main/java/xerca/xercamod/client/ClientStuff.java"
recipe_gen_py_path = "recipes_xercamod.py"


def generate_stuff(pizzas):
    for p in pizzas:
        print(p)
        # # Write blockstate json
        # b_fn = p.base_name() + ".json"
        # bj = p.blockstate_json()
        #
        # with open(op.join(blockstates_dir, b_fn), "w") as f:
        #     f.write(bj)
        #
        # # Write block model jsons
        # for i in range(4):
        #     m_fn = p.model_fn(i) + ".json"
        #     mj = p.block_model_json(i)
        #
        #     with open(op.join(block_models_dir, m_fn), "w") as f:
        #         f.write(mj)
        #
        # # Write item model jsons
        # m_fn = p.base_name() + ".json"
        # mj = p.item_model_json()
        #
        # with open(op.join(item_models_dir, m_fn), "w") as f:
        #     f.write(mj)
        # m_fn = "raw_" + p.base_name() + ".json"
        # mj = p.raw_item_model_json()
        #
        # with open(op.join(item_models_dir, m_fn), "w") as f:
        #     f.write(mj)
        #
        # # Insert into Blocks.java
        # with open(blocks_java_path, 'r+') as f:
        #     content = f.read()
        #
        #     # Object holder
        #     line_find = "// PIZZA OBJECT HOLDER BEGIN"
        #     i = content.find(line_find) + len(line_find) + 1
        #     line = p.blocks_object_holder() + "\n"
        #     content = content[:i] + line + content[i:]
        #
        #     # Register
        #     line_find = "// PIZZA REGISTER BEGIN"
        #     i = content.find(line_find) + len(line_find) + 1
        #     line = p.blocks_register() + "\n"
        #     content = content[:i] + line + content[i:]
        #
        #     f.seek(0)
        #     f.write(content)
        #     f.truncate()
        #
        # # Insert into Items.java
        # with open(items_java_path, 'r+') as f:
        #     content = f.read()
        #
        #     # Register
        #     line_find = "// PIZZA REGISTER BEGIN"
        #     i = content.find(line_find) + len(line_find) + 1
        #     line = p.items_register() + "\n"
        #     content = content[:i] + line + content[i:]
        #
        #     # Register raw items
        #     line_find = "// PIZZA RAW REGISTER BEGIN"
        #     i = content.find(line_find) + len(line_find) + 1
        #     line = p.items_raw_register() + "\n"
        #     content = content[:i] + line + content[i:]
        #
        #     f.seek(0)
        #     f.write(content)
        #     f.truncate()
        #
        # # Insert into ClientStuff renderLayers
        # with open(client_stuff_java_path, 'r+') as f:
        #     content = f.read()
        #
        #     # Render layers
        #     line_find = "private static void pizzaRenderLayers(){"
        #     i = content.find(line_find) + len(line_find) + 1
        #     line = p.render_layer() + "\n"
        #     content = content[:i] + line + content[i:]
        #
        #     f.seek(0)
        #     f.write(content)
        #     f.truncate()

        # with open(recipe_gen_py_path, "r+") as f:
        #     content = f.read()
        #
        #     # # Crafting recipes
        #     line_find = "# PIZZA RECIPES START"
        #     i = content.find(line_find) + len(line_find) + 1
        #     line = "    " + p.recipe_gen() + "\n"
        #     content = content[:i] + line + content[i:]
        #
        #     # Cooking recipes
        #     line_find = "# PIZZA COOKING RECIPES START"
        #     i = content.find(line_find) + len(line_find) + 1
        #     line = "    " + p.recipe_cooking_gen() + "\n"
        #     content = content[:i] + line + content[i:]
        #
        #     f.seek(0)
        #     f.write(content)
        #     f.truncate()


generate_stuff(pizzas)
