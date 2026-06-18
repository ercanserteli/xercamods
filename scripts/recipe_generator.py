import json
import os
import shutil
from enum import Enum

mod_id = ""
mod_id_to_folder = {
    "xercafood": "XercaFood",
    "xercamusic": "XercaMusicMod",
    "xercapaint": "XercaPaintMod",
    "xercablocks": "XercaBlocks",
    "xercacourt": "XercaCourt",
    "xercacushion": "XercaCushion",
    "xercaomnichest": "XercaOmniChest",
    "xercatools": "XercaTools",
}


def normalize_itemstack(result):
    if isinstance(result, str):
        return {"id": result}

    normalized = dict(result)
    if "item" in normalized:
        normalized["id"] = normalized.pop("item")
    return normalized


def result_id(result):
    normalized = normalize_itemstack(result)
    return normalized["id"]


def normalize_advancement_item_predicate(discover_item):
    if discover_item is None:
        return None

    if isinstance(discover_item, str):
        return {"items": discover_item}

    normalized = dict(discover_item)
    if "item" in normalized:
        return {"items": normalized["item"]}
    if "tag" in normalized:
        tag = normalized["tag"]
        if not tag.startswith("#"):
            tag = "#" + tag
        return {"items": tag}
    return normalized


def normalize_ingredient(ingredient):
    # 1.21.2+ ingredient format: bare item id string, "#tag", or a list of ids
    if ingredient is None:
        return None
    if isinstance(ingredient, str):
        return ingredient
    if isinstance(ingredient, list):
        return [normalize_ingredient(i) for i in ingredient]
    if isinstance(ingredient, dict):
        if "item" in ingredient:
            return ingredient["item"]
        if "tag" in ingredient:
            tag = ingredient["tag"]
            return tag if tag.startswith("#") else "#" + tag
    return ingredient


class Type(Enum):
    crafting_shaped, crafting_shapeless, smelting, campfire_cooking, blasting, smoking, stone_cutting = range(7)


class Recipe:
    def __init__(self, type, group, discover_item, folder, load_conditions=None):
        self.type = type
        self.group = group
        self.discover_item = normalize_advancement_item_predicate(discover_item)
        self.folder = folder
        self.load_conditions = load_conditions

    def produce_advancement_json(self):
        if self.folder:
            recipe_path = self.folder + "/" + self.get_name()
        else:
            recipe_path = self.get_name()

        d = {}
        if self.load_conditions:
            d["fabric:load_conditions"] = self.load_conditions

        d.update({
            "parent": f"{mod_id}:recipes/root",
            "rewards": {"recipes": [f"{mod_id}:{recipe_path}"]},
            "criteria": {
                "has_item": {
                    "trigger": "minecraft:inventory_changed",
                    "conditions": {"items": [self.discover_item]},
                },
                "has_the_recipe": {
                    "trigger": "minecraft:recipe_unlocked",
                    "conditions": {"recipe": f"{mod_id}:{recipe_path}"},
                },
            },
            "requirements": [["has_item", "has_the_recipe"]],
        })
        return json.dumps(d, indent=2)

    def add_common_fields(self, d):
        if self.load_conditions:
            d["fabric:load_conditions"] = self.load_conditions
        d["type"] = self.type
        if self.group:
            d["group"] = self.group


class ShapedRecipe(Recipe):
    def __init__(self, pattern, result, key, discover_item=None, group="", folder="", type="minecraft:crafting_shaped", load_conditions=None):
        super().__init__(type, group, discover_item, folder, load_conditions)
        self.pattern = pattern
        self.key = key
        self.result = result

    def produce_recipe_json(self):
        d = {}
        self.add_common_fields(d)
        d["pattern"] = self.pattern
        d["key"] = {k: normalize_ingredient(v) for k, v in self.key.items()}
        d["result"] = normalize_itemstack(self.result)
        return json.dumps(d, indent=2)

    def get_name(self):
        return result_id(self.result).split(":", 1)[1]


class ShapelessRecipe(Recipe):
    def __init__(self, ingredients, result, discover_item=None, group="", folder="", type="minecraft:crafting_shapeless", load_conditions=None):
        super().__init__(type, group, discover_item, folder, load_conditions)
        self.ingredients = ingredients
        self.result = result

    def produce_recipe_json(self):
        d = {}
        self.add_common_fields(d)
        d["ingredients"] = [normalize_ingredient(i) for i in self.ingredients]
        d["result"] = normalize_itemstack(self.result)
        return json.dumps(d, indent=2)

    def get_name(self):
        return result_id(self.result).split(":", 1)[1]


class CookingRecipe(Recipe):
    def __init__(self, type, ingredient, result, experience, cooking_time, discover_item=None, group="", folder="", load_conditions=None):
        super().__init__(type, group, discover_item, folder, load_conditions)
        self.ingredient = ingredient
        self.experience = experience
        self.cooking_time = cooking_time
        self.result = result

    def produce_recipe_json(self):
        d = {}
        self.add_common_fields(d)
        d["ingredient"] = normalize_ingredient(self.ingredient)
        d["result"] = normalize_itemstack(self.result)
        d["experience"] = self.experience
        d["cookingtime"] = self.cooking_time
        return json.dumps(d, indent=2)

    def get_name(self):
        item_name = result_id(self.result).split(":", 1)[1]
        if "smelting" in self.type:
            return "smelting_" + item_name
        if "campfire" in self.type:
            return "campfire_cooking_" + item_name
        if "smoking" in self.type:
            return "smoking_" + item_name


class SpecialRecipe(Recipe):
    def __init__(self, type, discover_item=None, group="", folder="", load_conditions=None):
        super().__init__(type, group, discover_item, folder, load_conditions)

    def produce_recipe_json(self):
        d = {}
        self.add_common_fields(d)
        return json.dumps(d, indent=2)

    def get_name(self):
        return self.type.split("crafting_special_", 1)[1]


class StonecuttingRecipe(Recipe):
    def __init__(self, ingredient, result, count, group="", folder="", type="minecraft:stonecutting", load_conditions=None):
        super().__init__(type, group, None, folder, load_conditions)
        self.ingredient = ingredient
        self.count = count
        self.result = result

    def produce_recipe_json(self):
        d = {}
        self.add_common_fields(d)
        d.update({
            "ingredient": normalize_ingredient(self.ingredient),
            "result": normalize_itemstack({"id": self.result, "count": self.count}),
        })
        return json.dumps(d, indent=2)

    def get_name(self):
        return self.result.split(":", 1)[1] + "_from_" + self.ingredient.split(":", 1)[1] + "_stonecutting"


class CarvingRecipe(Recipe):
    def __init__(self, ingredient, result, count, group="", folder="", type="xercablocks:carving", load_conditions=None):
        super().__init__(type, group, None, folder, load_conditions)
        self.ingredient = ingredient
        self.count = count
        self.result = result

    def produce_recipe_json(self):
        d = {}
        self.add_common_fields(d)
        d.update({
            "ingredient": normalize_ingredient(self.ingredient),
            "result": normalize_itemstack({"id": self.result, "count": self.count}),
        })
        return json.dumps(d, indent=2)

    def get_name(self):
        return self.result.split(":", 1)[1] + "_from_" + self.ingredient.split(":", 1)[1] + "_carving"


class SmithingRecipe(Recipe):
    def __init__(self, base, addition, result, template="minecraft:netherite_upgrade_smithing_template", group="", folder="", type="minecraft:smithing_transform", load_conditions=None):
        super().__init__(type, group, None, folder, load_conditions)
        self.base = base
        self.addition = addition
        self.result = result
        self.template = template

    def produce_recipe_json(self):
        d = {}
        self.add_common_fields(d)
        d.update({
            "base": normalize_ingredient(self.base),
            "addition": normalize_ingredient(self.addition),
            "result": {"id": self.result},
            "template": normalize_ingredient(self.template),
        })
        return json.dumps(d, indent=2)

    def get_name(self):
        return self.result.split(":", 1)[1] + "_smithing"


def write_recipe_adv_root_json():
    content = """{
  "criteria": {
    "impossible": {
      "trigger": "minecraft:impossible"
    }
  },
  "requirements": [
    [
      "impossible"
    ]
  ]
}"""
    file_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/advancement/recipes/root.json"
    with open(file_dir, "w") as f:
        f.write(content)


def generate_json(main_dir, json_string, r):
    folder = "/{}".format(r.folder) if r.folder else ""
    folder_dir = "{}{}".format(main_dir, folder)
    if folder:
        os.makedirs(folder_dir, exist_ok=True)

    file_dir = "{}/{}.json".format(folder_dir, r.get_name())

    while os.path.exists(file_dir):
        with open(file_dir, "r") as f:
            if f.read() != json_string:
                file_dir = "{}_alt.json".format(os.path.splitext(file_dir)[0])
            else:
                break

    with open(file_dir, "w", newline='\n') as f:
        f.write(json_string.strip() + "\n")


def clean_recipe_jsons(mod_id_input):
    global mod_id
    mod_id = mod_id_input
    recipe_main_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/recipe"
    adv_main_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/advancement/recipes"

    try:
        shutil.rmtree(recipe_main_dir)
    except FileNotFoundError:
        pass
    os.makedirs(recipe_main_dir)

    try:
        shutil.rmtree(adv_main_dir)
    except FileNotFoundError:
        pass
    os.makedirs(adv_main_dir)


def generate_recipe_jsons(recipes, mod_id_input):
    global mod_id
    mod_id = mod_id_input
    recipe_main_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/recipe"
    adv_main_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/advancement/recipes"

    write_recipe_adv_root_json()
    for r in recipes:
        generate_json(recipe_main_dir, r.produce_recipe_json(), r)
        if r.discover_item:
            generate_json(adv_main_dir, r.produce_advancement_json(), r)


def generate_recipe_code_from_files(mod_id_input):
    global mod_id
    mod_id = mod_id_input
    type_to_class = {
        "minecraft:crafting_shapeless": "ShapelessRecipe",
        "crafting_shapeless": "ShapelessRecipe",
        "minecraft:crafting_shaped": "ShapedRecipe",
        "crafting_shaped": "ShapedRecipe",
        "smelting": "CookingRecipe",
    }
    file_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/recipe"

    recipe_jsons = []
    for entry in os.scandir(file_dir):
        if entry.path.endswith(".json"):
            with open(entry.path, "r") as f:
                j = json.load(f)
                recipe_jsons.append(j)

    shapeds = []
    shapelesses = []
    cookings = []
    specials = []
    for j in recipe_jsons:
        recipe_class = type_to_class[j.get("type")] if j.get("type") in type_to_class else "SpecialRecipe"
        group = j.get("group") if j.get("group") else ""

        if recipe_class == "ShapedRecipe":
            code_template = '{}({}, {}, {}, {}, "{}"),'
            shapeds.append(code_template.format(recipe_class, j.get("pattern"), j.get("result"), j.get("key"), "DISCOVER", group))
        elif recipe_class == "ShapelessRecipe":
            code_template = '{}({}, {}, {}, "{}"),'
            shapelesses.append(code_template.format(recipe_class, j.get("ingredients"), j.get("result"), "DISCOVER", group))
        elif recipe_class == "CookingRecipe":
            code_template = '{}("{}", {}, "{}", {}, {}, {}, "{}"),'
            cookings.append(code_template.format(recipe_class, j.get("type"), j.get("ingredient"), j.get("result", {}).get("id"), j.get("experience"), j.get("cookingtime"), "DISCOVER", group))
        else:
            code_template = '{}("{}", {}, "{}"),'
            specials.append(code_template.format(recipe_class, j.get("type"), "DISCOVER", group))

    for r in shapeds:
        print(r)
    print("")
    for r in shapelesses:
        print(r)
    print("")
    for r in cookings:
        print(r)
    print("")
    for r in specials:
        print(r)
