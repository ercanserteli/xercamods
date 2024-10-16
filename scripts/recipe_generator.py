import json
import os
import shutil
from enum import Enum

mod_id = ""
mod_id_to_folder = {
    "xercafood": "XercaFood",
    "xercaconfetti": "XercaConfetti",
    "xercamusic": "XercaMusicMod",
    "xercapaint": "XercaPaintMod"
}

conditions = ["grab_hook", "warhammer", "cushion", "tea", "food", "confetti", "ender_flask", "courtroom", "carved_wood",
              "leather_straw", "bookcase", "coins", "scythe", "rope", "terracotta_tile", "omni_chest"]


class Type(Enum):
    crafting_shaped, crafting_shapeless, smelting, campfire_cooking, blasting, smoking, stone_cutting = range(7)


class Recipe:
    def __init__(self, type, group, discover_item, folder):
        self.type = type
        self.group = group
        if discover_item is not None and "item" in discover_item.keys():
            discover_item = "{'items': ['" + discover_item["item"] + "']}"
        self.discover_item = discover_item
        self.folder = folder

        self.cond = None
        if "condition" in type:
            for c in conditions:
                if c in type:
                    self.cond = c
                    if folder == "":
                        self.folder = self.cond
                    break

    def produce_advancement_json(self):
        if self.folder:
            recipe_path = self.folder + "/" + self.get_name()
        else:
            recipe_path = self.get_name()

        if self.cond is None:
            template = """
{{
  "parent": "{mod_id}:recipes/root",
  "rewards": {{
    "recipes": [
      "{mod_id}:{recipe_path}"
    ]
  }},
  "criteria": {{
    "has_item": {{
      "trigger": "minecraft:inventory_changed",
      "conditions": {{
        "items": [
          {discover_item}
        ]
      }}
    }},
    "has_the_recipe": {{
      "trigger": "minecraft:recipe_unlocked",
      "conditions": {{
        "recipe": "{mod_id}:{recipe_path}"
      }}
    }}
  }},
  "requirements": [
    [
      "has_item",
      "has_the_recipe"
    ]
  ]
}}
        """
            return template.format(mod_id=mod_id, recipe_path=recipe_path, discover_item=self.discover_item).replace("'", '"')
        else:
            template = """
{{
  "parent": "{mod_id}:recipes/root",
  "rewards": {{
    "recipes": [
      "{mod_id}:{recipe_path}"
    ]
  }},
  "criteria": {{
    "has_item": {{
      "trigger": "minecraft:inventory_changed",
      "conditions": {{
        "items": [
          {discover_item}
        ]
      }}
    }},
    "has_the_recipe": {{
      "trigger": "minecraft:recipe_unlocked",
      "conditions": {{
        "recipe": "{mod_id}:{recipe_path}"
      }}
    }},
    "config": {{
      "trigger": "xercafood:config_check",
      "conditions":{{
        "config": "{cond}"
      }}
    }}
  }},
  "requirements": [
    [
      "has_item",
      "has_the_recipe"
    ],
    ["config"]
  ]
}}
            """
            return template.format(mod_id=mod_id, recipe_path=recipe_path, discover_item=self.discover_item, cond=self.cond).replace("'", '"')


class ShapedRecipe(Recipe):
    def __init__(self, pattern, result, key, discover_item=None, group="", folder="", type="minecraft:crafting_shaped"):
        super().__init__(type, group, discover_item, folder)
        self.pattern = pattern
        self.key = key
        self.result = result

    def produce_recipe_json(self):
        group_line = '"group": "{}",'.format(self.group) if self.group else ""
        pattern_line = ",\n        ".join(['"{}"'.format(p) for p in self.pattern])
        template = """
{{
    "type": "{type}",
    {group}
    "pattern": [
        {pattern}
    ],
    "key": {key},
    "result": {result}
}}
        """
        return template.format(type=self.type, group=group_line, pattern=pattern_line, key=self.key, result=self.result).replace("'", '"')

    def get_name(self):
        return self.result["id"].split(":", 1)[1]


class ShapelessRecipe(Recipe):
    def __init__(self, ingredients, result, discover_item=None, group="", folder="", type="minecraft:crafting_shapeless"):
        super().__init__(type, group, discover_item, folder)
        self.ingredients = ingredients
        self.result = result

    def produce_recipe_json(self):
        group_line = '"group": "{}",'.format(self.group) if self.group else ""
        template = """
{{
    "type": "{type}",
    {group}
    "ingredients": {ingredients},
    "result": {result}
}}
        """
        return template.format(type=self.type, group=group_line, ingredients=self.ingredients, result=self.result).replace("'", '"')

    def get_name(self):
        return self.result["id"].split(":", 1)[1]


class CookingRecipe(Recipe):
    def __init__(self, type, ingredient, result, experience, cooking_time, discover_item=None, group="", folder=""):
        super().__init__(type, group, discover_item, folder)
        self.ingredient = ingredient
        self.experience = experience
        self.cooking_time = cooking_time
        self.result = result

    def produce_recipe_json(self):
        group_line = '"group": "{}",'.format(self.group) if self.group else ""
        template = """
{{
    "type": "{type}",
    {group}
    "ingredient": [
        {ingredient}
    ],
    "result": "{result}",
    "experience": {experience},
    "cookingtime": {cooking_time}
}}
        """
        return template.format(type=self.type, group=group_line, ingredient=self.ingredient, experience=self.experience, cooking_time=self.cooking_time, result=self.result).replace("'", '"')

    def get_name(self):
        item_name = self.result.split(":", 1)[1]
        if "smelting" in self.type:
            return "smelting_" + item_name
        if "campfire" in self.type:
            return "campfire_cooking_" + item_name
        if "smoking" in self.type:
            return "smoking_" + item_name


class SpecialRecipe(Recipe):
    def __init__(self, type, discover_item=None, group="", folder=""):
        super().__init__(type, group, discover_item, folder)

    def produce_recipe_json(self):
        group_line = '"group": "{}",'.format(self.group) if self.group else ""
        template = """
{{
    {group}
    "type": "{type}"
}}
        """
        return template.format(group=group_line, type=self.type).replace("'", '"')

    def get_name(self):
        return self.type.split("crafting_special_", 1)[1]


class StonecuttingRecipe(Recipe):
    def __init__(self, ingredient, result, count, group="", folder="", type="minecraft:stonecutting"):
        super().__init__(type, group, None, folder)
        self.ingredient = ingredient
        self.count = count
        self.result = result

    def produce_recipe_json(self):
        # group_line = '"group": "{}",'.format(self.group) if self.group else ""
        template = """
{{
    "type": "{type}",
    "ingredient": {{
        "item": "{ingredient}"
    }},
    "result": "{result}",
    "count": {count}
}}
        """
        return template.format(type=self.type, ingredient=self.ingredient, count=self.count, result=self.result).replace("'", '"')

    def get_name(self):
        return self.result.split(":", 1)[1] + "_from_" + self.ingredient.split(":", 1)[1] + "_stonecutting"


class CarvingRecipe(Recipe):
    def __init__(self, ingredient, result, count, group="", folder="", type="xercafood:carving"):
        super().__init__(type, group, None, folder)
        self.ingredient = ingredient
        self.count = count
        self.result = result

    def produce_recipe_json(self):
        # group_line = '"group": "{}",'.format(self.group) if self.group else ""
        template = """
{{
    "type": "{type}",
    "ingredient": {{
        "item": "{ingredient}"
    }},
    "result": "{result}",
    "count": {count}
}}
        """
        return template.format(type=self.type, ingredient=self.ingredient, count=self.count, result=self.result).replace("'", '"')

    def get_name(self):
        return self.result.split(":", 1)[1] + "_from_" + self.ingredient.split(":", 1)[1] + "_carving"


class SmithingRecipe(Recipe):
    def __init__(self, base, addition, result, group="", folder="", type="minecraft:smithing"):
        super().__init__(type, group, None, folder)
        self.base = base
        self.addition = addition
        self.result = result

    def produce_recipe_json(self):
        # group_line = '"group": "{}",'.format(self.group) if self.group else ""
        template = """
{{
    "type": "{type}",
    "base": {{
        "item": "{base}"
    }},
    "addition": {{
        "item": "{addition}"
    }},
    "result": {{
        "item": "{result}"
    }}
}}
        """
        return template.format(type=self.type, base=self.base, addition=self.addition, result=self.result).replace("'", '"')

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
    file_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/advancements/recipes/root.json"
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
    recipe_main_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/recipes"
    adv_main_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/advancements/recipes"

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
    recipe_main_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/recipes"
    adv_main_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/advancements/recipes"

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
    file_dir = f"../{mod_id_to_folder[mod_id]}/src/main/resources/data/{mod_id}/recipes"

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
            cookings.append(code_template.format(recipe_class, j.get("type"), j.get("ingredient"), j.get("result"), j.get("experience"), j.get("cookingtime"), "DISCOVER", group))
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
