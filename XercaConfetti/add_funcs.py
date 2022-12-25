from sys import argv
from utils import *

mod_id = ""


def add_item(mod_id, item_name, eng_name, item_class="Item", tex_folder="", is_food=False):
    if tex_folder != "":
        tex_folder = tex_folder + "/"
    eng_name = spacelize(eng_name)

    add_item_mod_entry(mod_id, item_name, item_class)
    add_item_models_entry(mod_id, item_name, tex_folder)
    add_lang_entry(mod_id, item_name, "en_us", eng_name, "item")

    if is_food:
        add_food_entry(mod_id, item_name)


def add_block(mod_id, block_name, eng_name, block_class="Block", tex_folder=""):
    if tex_folder != "":
        tex_folder = tex_folder + "/"
    eng_name = spacelize(eng_name)
    is_pure = block_class == "Block"

    add_block_mod_entry(mod_id, block_name, block_class, is_pure)
    add_block_models_entry(mod_id, block_name, tex_folder)

    add_lang_entry(mod_id, block_name, "en_us", eng_name, "block")


def add_entity(mod_id, entity_name, eng_name, entity_class=None, is_mob=False):
    eng_name = spacelize(eng_name)
    if not entity_class:
        entity_class = ccamelize(entity_name)

    add_entity_mod_entry(mod_id, entity_name, entity_class, is_mob)
    add_entity_classes(mod_id, entity_class)

    add_lang_entry(mod_id, entity_name, "en_us", eng_name, "entity")


def add_food_entry(mod_id, item_name):
    food_name = item_name.upper()
    if item_name[:5] == "item_":
        food_name = food_name[5:]
        
    line = f'    public static final FoodComponent {food_name}_FOOD = new FoodComponent.Builder().hunger(6).saturationModifier(1.2f).build();\n'
    add_to_java_file("Mod.java", "// Food Definitions", line, mod_id)


def add_lang_entry(mod_id, entry_name, lang_id, trans_name, prefix):
    line = f'  "{prefix}.{mod_id}.{entry_name}": "{trans_name}",\n'
    add_to_asset_file(f"lang/{lang_id}.json", "{", line, mod_id)


def add_item_mod_entry(mod_id, item_name, item_class):
    line = f'    public static final {item_class} {item_name.upper()} = new {item_class}(new Item.Settings());\n'
    add_to_java_file("Mod.java", "// Item Definitions", line, mod_id)
    line = f'        Registry.register(Registry.ITEM, new ResourceLocation(modId, "{item_name}"), {item_name.upper()});\n'
    add_to_java_file("Mod.java", "// Item Registration", line, mod_id)


def add_block_mod_entry(mod_id, block_name, block_class, is_pure):
    line = f'    public static final {block_class} {block_name.upper()} = new {block_class}(AbstractBlock.Settings.of(Material.SOIL).strength(0.5f));\n'
    add_to_java_file("Mod.java", "// Block Definitions", line, mod_id)
    line = f'        Registry.register(Registry.BLOCK, new ResourceLocation(modId, "{block_name}"), {block_name.upper()});\n'
    add_to_java_file("Mod.java", "// Block Registration", line, mod_id)
    
    line = f'    public static final BlockItem {block_name.upper()}_ITEM = new BlockItem({block_name.upper()}, new Item.Settings().group(ItemGroup.DECORATIONS));\n'
    add_to_java_file("Mod.java", "// Item Definitions", line, mod_id)
    line = f'        Registry.register(Registry.ITEM, new ResourceLocation(modId, "{block_name}"), {block_name.upper()}_ITEM);\n'
    add_to_java_file("Mod.java", "// Item Registration", line, mod_id)
   

def add_item_models_entry(mod_id, item_name, tex_folder):
    # Model json
    file_name = f"src\\main\\resources\\assets\\{mod_id}\\models\\item\\{item_name}.json"
    with open(file_name, 'w') as f:
        text = """
{{
    "parent": "item/{}",
    "textures": {{
        "layer0": "{}:item/{}{}"
    }}
}}""".format("generated", mod_id, tex_folder, item_name)
        f.write(text)


def add_block_models_entry(mod_id, block_name, tex_folder):
    # Blockstate json
    file_name = f"src\\main\\resources\\assets\\{mod_id}\\blockstates\\{block_name}.json"
    with open(file_name, 'w') as f:
        text = """
{{
  "variants": {{
    "": {{
      "model": "{}:block/{}"
    }}
  }}
}}""".format(mod_id, block_name)
        f.write(text)
    

    # Model json
    file_name = f"src\\main\\resources\\assets\\{mod_id}\\models\\block\\{block_name}.json"
    with open(file_name, 'w') as f:
        text = """
{{
  "parent": "minecraft:block/{}",
  "textures": {{
    "all": "{}:block/{}{}"
  }}
}}""".format("cube_all", mod_id, tex_folder, block_name)
        f.write(text)
    

    # Item Model json
    file_name = f"src\\main\\resources\\assets\\{mod_id}\\models\\item\\{block_name}.json"
    with open(file_name, 'w') as f:
        text = """
{{
  "parent": "{}:block/{}"
}}""".format(mod_id, block_name)
        f.write(text)
        
        
def add_entity_classes(mod_id, entity_class):
    # Entity class
    file_name = f"src\\main\\java\\xerca\\{mod_id}\\{entity_class}.java"
    with open(file_name, 'w') as f:
        text = f"""
package xerca.{mod_id};

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class {entity_class} extends Entity {{
    public {entity_class}(EntityType<{entity_class}> type, World world) {{
        super(type, world);
    }}
}}
"""
        f.write(text)
        
    # Entity renderer class
    file_name = f"src\\main\\java\\xerca\\{mod_id}\\client\\{entity_class}Renderer.java"
    with open(file_name, 'w') as f:
        text = f"""
package xerca.{mod_id}.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.ResourceLocation;
import xerca.{mod_id}.{entity_class};

public class {entity_class}Renderer extends EntityRenderer<{entity_class}> {{
    public {entity_class}Renderer(EntityRendererFactory.Context context) {{
        super(context);
    }}

    @Override
    public ResourceLocation getTexture({entity_class} entity) {{
        return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
    }}
}}
"""
        f.write(text)


def add_entity_mod_entry(mod_id, entity_name, entity_class, is_mob=False):
    line = f"""
    public static final EntityType<{entity_class}> {entity_name.upper()} = Registry.register(Registry.ENTITY_TYPE, new ResourceLocation(modId, "{entity_name}"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, (EntityType.EntityFactory<{entity_class}>){entity_class}::new)
            .dimensions(new EntityDimensions( 1.0f, 1.0f, true)).build());
    """
    add_to_java_file("Mod.java", "// EntityType Definitions", line, mod_id)
    
    if is_mob:
        line = f'        FabricDefaultAttributeRegistry.register({entity_name.upper()}, {entity_class}.createAttributes());\n'
        add_to_java_file("Mod.java", "// Entity Attribute Registration", line, mod_id)
 
    line = f'        EntityRendererRegistry.register(Mod.{entity_name.upper()}, {entity_class}Renderer::new);\n'
    add_to_java_file("client/ModClient.java", "// Entity Renderer Registration", line, mod_id)
        